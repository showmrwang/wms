package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
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
public class PoManagerImpl implements PoManager {

    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhPoLineDao whPoLineDao;


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
        long i = whPoDao.insert(po);
        if (0 == i) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.SAVE_PO_FAILED + "");// 保存至po单信息失败
            // throw new BusinessException(ErrorCodes.SAVE_PO_FAILED);
            return rm;
        }
        if (whPoLines.size() > 0) {
            // 有line信息保存
            for (WhPoLine whPoLine : whPoLines) {
                whPoLine.setPoId(po.getId());
                whPoLineDao.insert(whPoLine);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(po.getId() + "");
        return rm;

    }

    /**
     * 修改公共库PO单状态
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public int editPoStatusToInfo(WhPoCommand whPo) {
        int result = whPoDao.editPoStatus(whPo.getPoIds(), whPo.getStatus(), whPo.getModifiedId(), whPo.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != whPo.getPoIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {whPo.getPoIds().size(), result});
        }
        return result;
    }

    /**
     * 修改拆库PO单状态
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int editPoStatusToShard(WhPoCommand whPo) {
        int result = whPoDao.editPoStatus(whPo.getPoIds(), whPo.getStatus(), whPo.getModifiedId(), whPo.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != whPo.getPoIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {whPo.getPoIds().size(), result});
        }
        return result;
    }

    /**
     * 通过OP单ID查询相关信息 基础表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPoCommand findWhPoByIdToInfo(WhPoCommand whPo) {
        WhPoCommand whPoCommand = whPoDao.findWhPoCommandById(whPo.getId(), whPo.getOuId());
        return whPoCommand;
    }

    /**
     * 通过OP单ID查询相关信息 拆库表
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPoCommand findWhPoByIdToShard(WhPoCommand whPo) {
        WhPoCommand whPoCommand = whPoDao.findWhPoCommandById(whPo.getId(), whPo.getOuId());
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

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg insertPoWithOuId(PoCheckCommand poCheckCommand) {
        WhPo whPo = poCheckCommand.getWhPo();
        List<WhPoLine> whPoLines = poCheckCommand.getWhPoLines();
        ResponseMsg rm = poCheckCommand.getRm();
        String extCode = whPo.getExtCode();
        Long storeId = whPo.getStoreId();
        Long ouId = whPo.getOuId();
        /* 查找在性对应的拆库表中是否有此po单信息 */
        long count = whPoDao.findPoByCodeAndStore(extCode, storeId, ouId);
        /* 没有此po单信息 */
        if (0 == count) {
            long i = whPoDao.insert(whPo);
            if (0 == i) {
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                rm.setMsg(ErrorCodes.SAVE_CHECK_TABLE_FAILED + "");// 保存至po_check信息失败
                return rm;
            }
            // whPoDao.saveOrUpdate(whpo);
            if (whPoLines.size() > 0) {
                // 有line信息保存
                for (WhPoLine whPoLine : whPoLines) {
                    whPoLine.setPoId(whPo.getId());
                    whPoLineDao.insert(whPoLine);
                }
            }
            rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
            rm.setMsg(whPo.getId() + "");
        } else {
            /* 存在此po单信息 */
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.PO_EXIST + "");
            return rm;
        }
        return rm;

    }

    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息 公共库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhPoCommand> findWhPoListByPoCodeToInfo(String poCode, List<Integer> status, Long ouid) {
        return whPoDao.findWhPoListByPoCode(status, poCode, ouid);
    }

    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息 拆库
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhPoCommand> findWhPoListByPoCodeToShard(String poCode, List<Integer> status, Long ouid) {
        return whPoDao.findWhPoListByPoCode(status, poCode, ouid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public WhPo findWhAsnByIdToInfo(Long id, Long ouid) {
        return whPoDao.findWhPoById(id, ouid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhPo findWhAsnByIdToShard(Long id, Long ouid) {
        return whPoDao.findWhPoById(id, ouid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void deletePoAndPoLineToInfo(List<WhPoCommand> whPoCommand) {
        for (WhPoCommand po : whPoCommand) {
            // 删除PO表头信息
            whPoDao.delete(po.getId());
            // 删除POLINE明细信息
            whPoLineDao.deletePoLineByPoId(po.getId());
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deletePoAndPoLineToShard(List<WhPoCommand> whPoCommand) {
        for (WhPoCommand po : whPoCommand) {
            // 删除PO表头信息
            whPoDao.delete(po.getId());
            // 删除POLINE明细信息
            whPoLineDao.deletePoLineByPoId(po.getId());
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
            pl.setAvailableQty(0);// 一键创建asnline poline的可用数量0
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
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdateByVersionToShard(WhPo o) {
        int count = this.whPoDao.saveOrUpdateByVersion(o);
        if (count <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void cancelPoToInfo(List<WhPo> poList) {
        for (WhPo whPo : poList) {
            if (PoAsnStatus.PO_NEW == whPo.getStatus()) {
                whPo.setStatus(PoAsnStatus.PO_CANCELED);
                int poCount = this.whPoDao.saveOrUpdateByVersion(whPo);
                if (poCount == 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuId(whPo.getId(), whPo.getOuId(), null);
                for (WhPoLine line : lineList) {
                    line.setStatus(PoAsnStatus.POLINE_CANCELED);
                    line.setModifiedId(whPo.getModifiedId());
                    int lineCount = this.whPoLineDao.saveOrUpdateByVersion(line);
                    if (lineCount == 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            } else {
                throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR, new Object[] {whPo.getPoCode()});
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void cancelPoToShard(List<WhPo> poList) {
        for (WhPo whPo : poList) {
            if (PoAsnStatus.PO_NEW == whPo.getStatus()) {
                whPo.setStatus(PoAsnStatus.PO_CANCELED);
                int poCount = this.whPoDao.saveOrUpdateByVersion(whPo);
                if (poCount == 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                List<WhPoLine> lineList = this.whPoLineDao.findWhPoLineByPoIdOuId(whPo.getId(), whPo.getOuId(), null);
                for (WhPoLine line : lineList) {
                    line.setStatus(PoAsnStatus.POLINE_CANCELED);
                    line.setModifiedId(whPo.getModifiedId());
                    int lineCount = this.whPoLineDao.saveOrUpdateByVersion(line);
                    if (lineCount == 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            } else {
                throw new BusinessException(ErrorCodes.PO_DELETE_STATUS_ERROR, new Object[] {whPo.getPoCode()});
            }
        }
    }
}
