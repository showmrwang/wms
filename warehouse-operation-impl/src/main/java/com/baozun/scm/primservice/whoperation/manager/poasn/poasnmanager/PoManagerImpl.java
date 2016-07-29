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

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
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
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
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
     * 读取公共库PO单数据
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<WhPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        return whPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

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
     * 保存po单信息
     * 
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg createPoAndLineToShare(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        po.setId(null);
        long i = whPoDao.insert(po);
        if (0 == i) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.SAVE_PO_FAILED + "");// 保存至po单信息失败
            // throw new BusinessException(ErrorCodes.SAVE_PO_FAILED);
            return rm;
        }
        if (whPoLines != null && whPoLines.size() > 0) {
            // 有line信息保存
            for (WhPoLine whPoLine : whPoLines) {
                whPoLine.setId(null);
                whPoLine.setPoId(po.getId());
                whPoLineDao.insert(whPoLine);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        return rm;

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public ResponseMsg createPoAndLineToInfo(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        try {
            BiPo biPo = new BiPo();
            BeanUtils.copyProperties(po, biPo);
            // biPo.setId(po.getBiPoId());
            biPoDao.insert(biPo);
            this.insertGlobalLog(GLOBAL_LOG_INSERT, biPo, po.getOuId(), po.getCreatedId(), null, DbDataSource.MOREDB_INFOSOURCE);
            if (po.getOuId() != null) {

                whPoDao.insert(po);
            }
            // 有line信息保存
            if (whPoLines != null && whPoLines.size() > 0) {
                for (WhPoLine whPoLine : whPoLines) {
                    whPoLine.setPoId(po.getId());
                    BiPoLine biPoLine = new BiPoLine();
                    BeanUtils.copyProperties(whPoLine, biPoLine);
                    biPoLineDao.insert(biPoLine);
                    // whPoLineDao.insert(whPoLine);
                    this.insertGlobalLog(GLOBAL_LOG_INSERT, biPoLine, po.getOuId(), po.getCreatedId(), po.getPoCode(), DbDataSource.MOREDB_INFOSOURCE);
                }
            }


        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(ErrorCodes.SAVE_PO_FAILED);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(po.getId() + "");
        return rm;
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
     * 更新PO单信息 基础表
     * 
     * @param whPo
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void editPoToInfo(WhPo whPo) {
        int count = 0;
        count = whPoDao.saveOrUpdateByVersion(whPo);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    /**
     * 更新PO单信息 拆库表
     * 
     * @param whPo
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void editPoToShard(WhPo whPo) {
        int count = 0;
        count = whPoDao.saveOrUpdateByVersion(whPo);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }


    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息 公共库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPoCommand> findWhPoListByExtCodeToInfo(String poCode, List<Integer> status, List<Long> customerList,List<Long> storeList,Long ouid, Integer linenum) {
        return whPoDao.findWhPoListByExtCode(status, poCode,customerList, storeList, ouid, linenum);
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
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuId(po.getId(), po.getOuId(), null);
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
            List<WhPo> whPoList = this.whPoDao.findWhPoByPoCodeToInfo(po.getPoCode());
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
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deletePoAndPoLineToShard(WhPo po, Long userId) {
        try{
            // 删除POLINE明细信息
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuId(po.getId(), po.getOuId(), null);
            if(lineList!=null&&lineList.size()>0){
                for(WhPoLine line:lineList){
                    this.whPoLineDao.deletePoLineByIdOuId(line.getId(), line.getOuId());
                }
            }
            // 删除PO表头信息
            whPoDao.deleteByIdOuId(po.getId(), po.getOuId());
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    /**
     * 通过asn数据修改对应po单状态 只针对没有ouid的po单
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public ResponseMsg updatePoStatusByAsn(WhAsn asn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm) {
        // 修改表头状态
        if (whPo.getStatus() == PoAsnStatus.PO_NEW) {
            // 如果是新建状态 改状态为已创建ASN
            whPo.setStatus(PoAsnStatus.PO_CREATE_ASN);
            whPo.setModifiedId(asn.getModifiedId());
            int result = whPoDao.saveOrUpdateByVersion(whPo);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        // 修改明细状态及可用数量
        for (WhAsnLineCommand asnline : asnLineList) {
            WhPoLine whPoLine = poLineMap.get(asnline.getPoLineId());
            // 修改poline的可用数量
            whPoLine.setAvailableQty(whPoLine.getAvailableQty() - asnline.getQtyPlanned());
            whPoLine.setModifiedId(asn.getModifiedId());
            if (whPoLine.getStatus() == PoAsnStatus.POLINE_NEW) {
                // 如果明细状态为新建的话 改成已创建ASN状态
                whPoLine.setStatus(PoAsnStatus.POLINE_CREATE_ASN);
            }
            int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(asn.getId() + "");
        return rm;
    }

    /**
     * 通过asn数据修改对应po单状态 只针对没有ouid的po单 一键创建ASN
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public ResponseMsg updatePoStatusByAsnBatch(WhAsnCommand asn, WhPo whpo, List<WhPoLine> whPoLines, ResponseMsg rm) {
        // 修改表头状态
        if (whpo.getStatus() == PoAsnStatus.PO_NEW) {
            // 如果是新建状态 改状态为已创建ASN
            whpo.setStatus(PoAsnStatus.PO_CREATE_ASN);
            whpo.setModifiedId(asn.getModifiedId());
            int result = whPoDao.saveOrUpdateByVersion(whpo);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        // 修改明细状态和可用数量
        for (WhPoLine pl : whPoLines) {
            // 修改poline的可用数量
            pl.setAvailableQty(0.0);// 一键创建asnline poline的可用数量0
            pl.setModifiedId(asn.getModifiedId());
            if (pl.getStatus() == PoAsnStatus.POLINE_NEW) {
                // 如果明细状态为新建的话 改成已创建ASN状态
                pl.setStatus(PoAsnStatus.POLINE_CREATE_ASN);
            }
            int result = whPoLineDao.saveOrUpdateByVersion(pl);
            if (result <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(asn.getId() + "");
        return rm;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void saveOrUpdateByVersionToInfo(WhPo o) {
        int count = this.whPoDao.saveOrUpdateByVersion(o);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 插入操作日志
        this.insertGlobalLog(o.getModifiedId(), new Date(), o.getClass().getSimpleName(), o, Constants.GLOBAL_LOG_UPDATE, o.getOuId());
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdateByVersionToShard(WhPo o) {
        int count = this.whPoDao.saveOrUpdateByVersion(o);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 插入操作日志
        this.insertGlobalLog(o.getModifiedId(), new Date(), o.getClass().getSimpleName(), o, Constants.GLOBAL_LOG_UPDATE, o.getOuId());
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
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuId(whPo.getId(), ouId, null);
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
            List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuId(whPo.getId(), ouId, null);
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
     * @author yimin.lu 删除ASN单的时候，对应的PO单在INFO库的情况下，更新PO单和对应的PO单明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void editPoAdnPoLineWhenDeleteAsnToInfo(WhPo whpo, List<WhPoLine> polineList) {
        log.info(this.getClass().getSimpleName() + ".editPoAdnPoLineWhenDeleteAsnToInfo method begin!");
        try {
            // 保存PO
            int poUpdateCount = this.whPoDao.saveOrUpdateByVersion(whpo);
            if (poUpdateCount <= 0) {
                log.warn("edit linked po error!");
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入操作日志
            this.insertGlobalLog(whpo.getModifiedId(), new Date(), whpo.getClass().getSimpleName(), whpo, Constants.GLOBAL_LOG_UPDATE, whpo.getOuId());
            for (WhPoLine poLine : polineList) {
                int lineCount = this.whPoLineDao.saveOrUpdateByVersion(poLine);
                if (lineCount <= 0) {
                    log.warn("edit linked poline error!");
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                // 插入操作日志
                this.insertGlobalLog(poLine.getModifiedId(), new Date(), poLine.getClass().getSimpleName(), poLine, Constants.GLOBAL_LOG_UPDATE, poLine.getOuId());
            }
        } catch (Exception e) {
            log.error(" update info.po and info.poline when deleting asn throws error:{}!", e);
            if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
        log.info(this.getClass().getSimpleName() + ".editPoAdnPoLineWhenDeleteAsnToInfo method end!");
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
    public void createWhPoToInfo(WhPo shardpo, List<WhPoLine> whpolineList) {
        // 集团下BIPO创建子PO单
        // 1.如果po单状态为新建的话，置为已分配到仓库状态；其余状态不变】

        // 更改BIPO状态
        BiPo bipo = this.biPoDao.findbyPoCode(shardpo.getPoCode());
        if (PoAsnStatus.BIPO_NEW == bipo.getStatus()) {
            bipo.setStatus(PoAsnStatus.BIPO_ALLOT);
            int bipoCount = this.biPoDao.saveOrUpdateByVersion(bipo);
            if (bipoCount < 1) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }

        // IF逻辑：如果仓库没有分配过
        if (null == shardpo.getId()) {
            this.whPoDao.insert(shardpo);
            for (WhPoLine line : whpolineList) {
                // 更改BIPO明细
                BiPoLine bipoline = this.biPoLineDao.findById(line.getPoLineId());
                if (bipoline.getAvailableQty() < line.getQtyPlanned()) {
                    throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
                }
                bipoline.setAvailableQty(bipoline.getAvailableQty() - line.getQtyPlanned());
                if (PoAsnStatus.BIPOLINE_NEW == bipoline.getStatus()) {
                    bipoline.setStatus(PoAsnStatus.BIPO_ALLOT);
                }
                bipoline.setModifiedId(shardpo.getModifiedId());
                this.biPoLineDao.saveOrUpdateByVersion(bipoline);

                // 更改INFO.WHPO明细
                line.setPoId(shardpo.getId());
                this.whPoLineDao.insert(line);
            }
            // ELSE:此仓库已经分配过PO
        } else {
            int pocount = this.whPoDao.saveOrUpdateByVersion(shardpo);
            if (pocount < 1) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            for (WhPoLine line : whpolineList) {

                BiPoLine bipoline = this.biPoLineDao.findById(line.getPoLineId());
                bipoline.setAvailableQty(bipoline.getAvailableQty() - line.getQtyPlanned());
                bipoline.setModifiedId(shardpo.getModifiedId());
                if (PoAsnStatus.BIPOLINE_NEW == bipoline.getStatus()) {
                    bipoline.setStatus(PoAsnStatus.BIPO_ALLOT);
                }
                this.biPoLineDao.saveOrUpdateByVersion(bipoline);

                if (null == line.getId()) {
                    line.setPoId(shardpo.getId());
                    this.whPoLineDao.insert(line);
                } else {
                    int linecount = this.whPoLineDao.saveOrUpdateByVersion(line);
                    if (linecount < 1) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createWhPoToShard(WhPo shardpo, List<WhPoLine> whpolineList) {
        if (null == shardpo.getId()) {
            this.whPoDao.insert(shardpo);
            for (WhPoLine line : whpolineList) {
                line.setPoId(shardpo.getId());
                this.whPoLineDao.insert(line);
            }
        } else {
            int pocount = this.whPoDao.saveOrUpdateByVersion(shardpo);
            if (pocount < 1) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            for (WhPoLine line : whpolineList) {
                if (null == line.getId()) {
                    line.setPoId(shardpo.getId());
                    this.whPoLineDao.insert(line);
                } else {
                    int linecount = this.whPoLineDao.saveOrUpdateByVersion(line);
                    if (linecount < 1) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPo> findWhPoByPoCodeToInfo(String poCode) {
        return this.whPoDao.findWhPoByPoCodeToInfo(poCode);
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
        if (null == lineList || lineList.size() == 0) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        for (WhPoLine line : lineList) {
            if (line.getQtyPlanned() <= Constants.DEFAULT_DOUBLE) {
                this.whPoLineDao.delete(line.getId());
            }
            int count = this.whPoLineDao.saveOrUpdateByVersion(line);
            if (count < 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }
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
