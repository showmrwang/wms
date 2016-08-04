package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;


/**
 * 创建PO单据
 * 
 * @author bin.hu
 * 
 */
@Service("poManager")
@Transactional
public class PoManagerImpl extends BaseManagerImpl implements PoManager {
    protected static final Logger log = LoggerFactory.getLogger(PoManager.class);
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private GlobalLogManager globalLogManager;
    @Autowired
    private BiPoDao biPoDao;
    @Autowired
    private BiPoLineDao biPoLineDao;


    /**
     * 读取拆分库PO单数据
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhPoCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return whPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    /**
     * 通过OP单ID查询相关信息 基础表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPoCommand findWhPoCommandByIdToInfo(Long id, Long ouId) {
        WhPoCommand whPoCommand = whPoDao.findWhPoCommandById(id, ouId);
        return whPoCommand;
    }

    /**
     * 通过OP单ID查询相关信息 拆库表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPoCommand findWhPoCommandByIdToShard(Long id, Long ouId) {
        WhPoCommand whPoCommand = whPoDao.findWhPoCommandById(id, ouId);
        return whPoCommand;
    }

    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息 拆库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhPoCommand> findWhPoListByExtCodeToShard(String poCode, List<Integer> status,List<Long> customerList,List<Long> storeList, Long ouid, Integer linenum) {
        return whPoDao.findWhPoListByExtCode(status, poCode,customerList, storeList,ouid, linenum);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deletePoAndPoLineToInfo(WhPo po, Long userId) {
        try{
            // 删除POLINE明细信息
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuIdUuid(po.getId(), po.getOuId(), null);
            if (lineList != null && lineList.size() > 0) {
                for (WhPoLine line : lineList) {
                    BiPoLine biLine = this.biPoLineDao.findById(line.getPoLineId());
                    Double qty = biLine.getAvailableQty() + line.getAvailableQty();
                    if (qty.equals(biLine.getQtyPlanned())) {
                        biLine.setStatus(PoAsnStatus.BIPOLINE_NEW);
                    }
                    biLine.setAvailableQty(qty);
                    biLine.setModifiedId(userId);
                    int updateBiLineCount = this.biPoLineDao.saveOrUpdateByVersion(biLine);
                    if (updateBiLineCount <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                    this.whPoLineDao.deletePoLineByIdOuId(line.getId(), line.getOuId());
                }
            }
            // 删除PO表头信息
            BiPo biPo = this.biPoDao.findBiPoByExtCodeStoreId(po.getExtCode(), po.getStoreId());
            whPoDao.deleteByIdOuId(po.getId(), po.getOuId());
            List<WhPo> whPoList = this.whPoDao.findWhPoByExtCodeStoreIdToInfo(biPo.getExtCode(), biPo.getStoreId());
            if (whPoList == null || whPoList.size() == 0) {
                biPo.setStatus(PoAsnStatus.BIPO_NEW);
            }
            biPo.setModifiedId(userId);
            int updateBiCount = this.biPoDao.saveOrUpdateByVersion(biPo);
            if (updateBiCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deletePoAndPoLineToShard(WhPo po, Long userId) {
        try{
            // 删除POLINE明细信息
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuIdUuid(po.getId(), po.getOuId(), null);
            if(lineList!=null&&lineList.size()>0){
                for(WhPoLine line:lineList){
                    int deleteCount = this.whPoLineDao.deletePoLineByIdOuId(line.getId(), line.getOuId());
                    if (deleteCount <= 0) {
                        throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                    }
                }
            }
            // 删除PO表头信息
            whPoDao.deleteByIdOuId(po.getId(), po.getOuId());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void saveOrUpdateByVersionToInfo(WhPo o) {
        try {
            int count = this.whPoDao.saveOrUpdateByVersion(o);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入操作日志
            this.insertGlobalLog(o.getModifiedId(), new Date(), o.getClass().getSimpleName(), o, Constants.GLOBAL_LOG_UPDATE, o.getOuId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdateByVersionToShard(WhPo o) {
        try {
            int count = this.whPoDao.saveOrUpdateByVersion(o);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入操作日志
            this.insertGlobalLog(o.getModifiedId(), new Date(), o.getClass().getSimpleName(), o, Constants.GLOBAL_LOG_UPDATE, o.getOuId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    /**
     * INFO库取消PO单;取消PO单的同时需要取消所有的明细；PO单状态为新建的时候可以取消
     * 
     * @author yimin.lu
     * @mender yimin.lu 2106/7/27 PO单只允许单条取消
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void cancelPoToInfo(WhPo whPo, Long userId) {
        Long ouId = whPo.getOuId();
        log.info(this.getClass().getSimpleName() + ".cancelPoToInfo method begin");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".cancelPoToInfo params:{}", whPo);
        }
        if (PoAsnStatus.PO_NEW == whPo.getStatus()) {
            // 将对应的PO单更新状态为取消
            whPo.setStatus(PoAsnStatus.PO_CANCELED);
            int poCount = this.whPoDao.saveOrUpdateByVersion(whPo);
            if (poCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入操作日志
            this.insertGlobalLog(whPo.getModifiedId(), new Date(), whPo.getClass().getSimpleName(), whPo, Constants.GLOBAL_LOG_UPDATE, ouId);
            // 循环取消对应的Po单明细
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuIdUuid(whPo.getId(), ouId, null);
            for (WhPoLine line : lineList) {
                line.setStatus(PoAsnStatus.POLINE_CANCELED);
                line.setModifiedId(userId);
                int lineCount = this.whPoLineDao.saveOrUpdateByVersion(line);
                if (lineCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                // 插入操作日志
                this.insertGlobalLog(userId, new Date(), line.getClass().getSimpleName(), line, Constants.GLOBAL_LOG_UPDATE, ouId);
                // BIPO
                // @mender yimin.lu 2016/7/27
                BiPoLine biLine = this.biPoLineDao.findById(line.getPoLineId());
                double availableQty = biLine.getAvailableQty();
                biLine.setAvailableQty(availableQty + line.getQtyPlanned());
                biLine.setModifiedId(userId);
                int biLineCount = this.biPoLineDao.saveOrUpdateByVersion(biLine);
                if (biLineCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        } else {
            throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR, new Object[] {whPo.getPoCode()});
        }
    }

    /**
     * INFO库取消PO单;取消PO单的同时需要取消所有的明细
     * 
     * @author yimin.lu
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void cancelPoToShard(WhPo whPo, Long userId) {
        Long ouId = whPo.getOuId();
        log.info(this.getClass().getSimpleName() + ".cancelPoToShard method begin");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".cancelPoToShard params:{}", whPo);
        }
        if (PoAsnStatus.PO_NEW == whPo.getStatus()) {
            // 将对应的PO单更新状态为取消
            whPo.setStatus(PoAsnStatus.PO_CANCELED);
            whPo.setModifiedId(userId);
            int poCount = this.whPoDao.saveOrUpdateByVersion(whPo);
            if (poCount == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入操作日志
            this.insertGlobalLog(userId, new Date(), whPo.getClass().getSimpleName(), whPo, Constants.GLOBAL_LOG_UPDATE, ouId);
            // 循环取消对应的Po单明细
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuIdUuid(whPo.getId(), ouId, null);
            for (WhPoLine line : lineList) {
                line.setStatus(PoAsnStatus.POLINE_CANCELED);
                line.setModifiedId(userId);
                int lineCount = this.whPoLineDao.saveOrUpdateByVersion(line);
                if (lineCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                // 插入操作日志
                this.insertGlobalLog(userId, new Date(), line.getClass().getSimpleName(), line, Constants.GLOBAL_LOG_UPDATE, ouId);
            }
        } else {
            throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR);
        }
    }

    /**
     * 用于插入日志操作
     * 
     * @param userId
     * @param modifyTime
     * @param objectType
     * @param modifiedValues
     * @param type
     * @param ouId
     */
    private void insertGlobalLog(Long userId, Date modifyTime, String objectType, Object modifiedValues, String type, Long ouId) {
        log.info(this.getClass().getSimpleName() + ".insertGlobalLog method begin!");
        try {
            GlobalLogCommand gl = new GlobalLogCommand();
            gl.setModifiedId(userId);
            gl.setModifyTime(modifyTime);
            gl.setObjectType(objectType);
            gl.setModifiedValues(modifiedValues);
            gl.setType(type);
            gl.setOuId(ouId);
            globalLogManager.insertGlobalLog(gl);
        } catch (Exception e) {
            log.error(" insert into global log error:{}!", e);
            throw new BusinessException(ErrorCodes.INSERT_LOG_ERROR);
        }

        log.info(this.getClass().getSimpleName() + ".insertGlobalLog method end!");
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPo findWhPoByExtCodeStoreIdOuIdToShard(String extCode, Long storeId, Long ouId) {
        return this.whPoDao.findWhPoByExtCodeStoreIdOuId(extCode, storeId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPo findWhPoByExtCodeStoreIdOuIdToInfo(String extCode, Long storeId, Long ouId) {
        return this.whPoDao.findWhPoByExtCodeStoreIdOuId(extCode, storeId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPo> findWhPoByExtCodeStoreIdToInfo(String extCode, Long storeId) {
        return this.whPoDao.findWhPoByExtCodeStoreIdToInfo(extCode, storeId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPo findWhPoByIdToInfo(Long id, Long ouId) {
        return this.whPoDao.findWhPoById(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPo findWhPoByIdToShard(Long id, Long ouId) {
        return this.whPoDao.findWhPoById(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createSubPoWithLineToInfo(WhPo po, List<WhPoLine> whPoLineList) {
        if (null == po) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        if (null == po.getId()) {
            this.whPoDao.insert(po);
        } else {
            int count = this.whPoDao.saveOrUpdateByVersion(po);
            if (count <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        for (WhPoLine line : whPoLineList) {
            line.setPoId(po.getId());
            if (null == line.getId()) {
                this.whPoLineDao.insert(line);
            } else {
                int count = this.whPoLineDao.saveOrUpdateByVersion(line);
                if (count <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void revokeSubPoToInfo(List<WhPoLine> lineList) {
        log.info("begin!");
        try {

            if (null == lineList || lineList.size() == 0) {
                throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
            }
            for (WhPoLine line : lineList) {
                this.whPoLineDao.delete(line.getId());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ex) {
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
        log.info("end!");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveSubPoToShard(String extCode, Long storeId, Long ouId, Long userId, String poCode, WhPo infoPo, List<WhPoLine> infoPoLineList) {
        WhPo shardPo = this.whPoDao.findWhPoByExtCodeStoreIdOuId(extCode, storeId, ouId);
        if (null == shardPo) {
            shardPo = new WhPo();
            BeanUtils.copyProperties(infoPo, shardPo);
            shardPo.setPoCode(poCode);
            shardPo.setModifiedId(userId);
            shardPo.setCreatedId(userId);
            shardPo.setCreateTime(new Date());
            shardPo.setLastModifyTime(new Date());
            shardPo.setId(null);
            this.whPoDao.insert(shardPo);
        } else {
            shardPo.setModifiedId(userId);
            shardPo.setQtyPlanned(infoPo.getQtyPlanned());
            this.saveOrUpdateByVersionToInfo(shardPo);
        }
        List<Integer> statusList = Arrays.asList(new Integer[] {PoAsnStatus.POLINE_NEW, PoAsnStatus.POLINE_CREATE_ASN, PoAsnStatus.POLINE_RCVD});
        for (WhPoLine infoLine : infoPoLineList) {
            WhPoLine shardLine = this.whPoLineDao.findPoLineByPolineIdAndStatusListAndPoIdAndOuId(infoLine.getPoLineId(), statusList, shardPo.getId(), ouId, null);
            if (null == shardLine) {
                shardLine = new WhPoLine();
                BeanUtils.copyProperties(infoLine, shardLine);
                shardLine.setId(null);
                shardLine.setCreatedId(userId);
                shardLine.setCreateTime(new Date());
                shardLine.setModifiedId(userId);
                shardLine.setLastModifyTime(new Date());
                shardLine.setPoId(shardPo.getId());
                this.whPoLineDao.insert(shardLine);
            } else {
                shardLine.setAvailableQty(shardLine.getAvailableQty() + infoLine.getQtyPlanned() - shardLine.getQtyPlanned());
                shardLine.setQtyPlanned(infoLine.getQtyPlanned());
                shardLine.setModifiedId(userId);
                this.whPoLineDao.saveOrUpdateByVersion(shardLine);
            }
        }
    }

}
