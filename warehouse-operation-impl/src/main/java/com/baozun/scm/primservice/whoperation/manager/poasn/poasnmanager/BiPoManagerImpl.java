package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

@Service("BiPoManager")
@Transactional
public class BiPoManagerImpl extends BaseManagerImpl implements BiPoManager {
    @Autowired
    private BiPoDao biPoDao;
    @Autowired
    private BiPoLineDao biPoLineDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhPoLineDao whPoLineDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<BiPo> findListByParam(BiPo biPo) {
        return this.biPoDao.findListByParam(biPo);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPo findBiPoById(Long id) {
        return this.biPoDao.findById(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPo findBiPoByPoCode(String poCode) {
        return this.biPoDao.findbyPoCode(poCode);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPoCommand findBiPoCommandById(Long id) {
        return this.biPoDao.findCommandbyId(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPoCommand findBiPoCommandByPoCode(String poCode) {
        return this.biPoDao.findCommandbyPoCode(poCode);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<BiPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.biPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public ResponseMsg createPoAndLineToInfo(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        // create-po分支：
        // 逻辑:
        // 1.没有指定仓库，则只需要插入BIPO
        // 2.指定仓库,则需要插入BIPO，并将数据同步到INFO的WHPO中；
        // 3.指定了仓库，状态为已分配仓库状态。而仓库为新建状态
        try {
            BiPo biPo = new BiPo();// biPo为插入到BIPO的实体对象;po为插入到INFO.WHPO的对象
            BeanUtils.copyProperties(po, biPo);
            biPo.setStatus(null == po.getOuId() ? PoAsnStatus.BIPO_NEW : PoAsnStatus.BIPO_ALLOT);
            // 插入BIPO
            biPoDao.insert(biPo);
            this.insertGlobalLog(GLOBAL_LOG_INSERT, biPo, po.getOuId(), po.getCreatedId(), null, DbDataSource.MOREDB_INFOSOURCE);
            if (po.getOuId() != null) {
                // 插入WHPO
                po.setStatus(PoAsnStatus.PO_NEW);
                whPoDao.insert(po);
                this.insertGlobalLog(GLOBAL_LOG_INSERT, po, po.getOuId(), po.getCreatedId(), null, DbDataSource.MOREDB_INFOSOURCE);
            }
            // 有line信息保存
            if (whPoLines != null && whPoLines.size() > 0) {
                for (WhPoLine whPoLine : whPoLines) {
                    // 插入BIPOLINE
                    whPoLine.setPoId(biPo.getId());
                    BiPoLine biPoLine = new BiPoLine();
                    BeanUtils.copyProperties(whPoLine, biPoLine);
                    if (po.getOuId() != null) {
                        biPoLine.setAvailableQty(0d);
                        biPoLine.setStatus(PoAsnStatus.BIPOLINE_ALLOT);
                    } else {
                        biPoLine.setAvailableQty(biPoLine.getQtyPlanned());
                        biPoLine.setStatus(PoAsnStatus.BIPOLINE_NEW);
                    }
                    biPoLineDao.insert(biPoLine);
                    this.insertGlobalLog(GLOBAL_LOG_INSERT, biPoLine, po.getOuId(), po.getCreatedId(), po.getPoCode(), DbDataSource.MOREDB_INFOSOURCE);
                    if (po.getOuId() != null) {
                        // 插入WHPOLINE
                        whPoLine.setId(null);
                        whPoLine.setPoId(po.getId());
                        whPoLine.setPoLineId(biPoLine.getId());
                        whPoLine.setStatus(PoAsnStatus.POLINE_NEW);
                        whPoLine.setAvailableQty(whPoLine.getQtyPlanned());
                        whPoLineDao.insert(whPoLine);
                        this.insertGlobalLog(GLOBAL_LOG_INSERT, whPoLine, po.getOuId(), po.getCreatedId(), po.getPoCode(), DbDataSource.MOREDB_INFOSOURCE);

                    }
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
        return rm;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg createPoAndLineToShared(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        try{
            // 插入SHARD.WHPO
            po.setId(null);
            po.setStatus(PoAsnStatus.PO_NEW);
            long i = whPoDao.insert(po);
            if (i < 0) {
                throw new BusinessException(ErrorCodes.SAVE_PO_FAILED);
            }
            this.insertGlobalLog(GLOBAL_LOG_INSERT, po, po.getOuId(), po.getModifiedId(), null, DbDataSource.MOREDB_SHARDSOURCE);
            // 插入SHARD.WHPOLINE
            if (whPoLines != null && whPoLines.size() > 0) {
                // 有line信息保存
                for (WhPoLine whPoLine : whPoLines) {
                    whPoLine.setId(null);
                    whPoLine.setPoId(po.getId());
                    whPoLine.setStatus(PoAsnStatus.POLINE_NEW);
                    whPoLine.setAvailableQty(whPoLine.getQtyPlanned());
                    long il = whPoLineDao.insert(whPoLine);
                    if (il < 0) {
                        throw new BusinessException(ErrorCodes.SAVE_PO_FAILED);
                    }
                    this.insertGlobalLog(GLOBAL_LOG_INSERT, whPoLine, whPoLine.getOuId(), whPoLine.getModifiedId(), null, DbDataSource.MOREDB_SHARDSOURCE);
                }
            }
        } catch (Exception e) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.SAVE_PO_FAILED + "");// 保存至po单信息失败
            return rm;
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        return rm;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public ResponseMsg deleteBiPoAndLine(Long id, Long userId) {
        ResponseMsg rm=new ResponseMsg();
        try{
            List<BiPoLine> lines = this.biPoLineDao.findBiPoLineByPoIdAndUuid(id, null);
            if (lines != null) {
                for (BiPoLine line : lines) {
                    int dpl=this.biPoLineDao.delete(line.getId());
                    if(dpl<0){
                        throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                    }
                    this.insertGlobalLog(GLOBAL_LOG_DELETE, line, null, userId, line.getPoId() + "", DbDataSource.MOREDB_INFOSOURCE);
                }
            }
            BiPo bipo = this.biPoDao.findById(id);
            int dp = this.biPoDao.delete(id);
            if (dp < 0) {
                throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_DELETE, bipo, null, userId, null, DbDataSource.MOREDB_INFOSOURCE);
        } catch (Exception e) {
            rm.setMsg("delete bipo error");
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            return rm;
        }
        rm.setMsg("success");
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        return rm;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public ResponseMsg cancelBiPo(Long id, Long userId) {
        ResponseMsg rm = new ResponseMsg();
        try {
            List<BiPoLine> lines = this.biPoLineDao.findBiPoLineByPoIdAndUuid(id, null);
            if (lines != null) {
                for (BiPoLine line : lines) {
                    line.setStatus(PoAsnStatus.BIPOLINE_CANCELED);
                    line.setModifiedId(userId);
                    int dpl = this.biPoLineDao.saveOrUpdateByVersion(line);
                    if (dpl < 0) {
                        throw new BusinessException(ErrorCodes.CANCEL_PO_ERROR);
                    }
                    this.insertGlobalLog(GLOBAL_LOG_UPDATE, line, null, userId, line.getPoId() + "", DbDataSource.MOREDB_INFOSOURCE);
                }
            }
            BiPo bipo = this.biPoDao.findById(id);
            if (null == bipo) {
                throw new BusinessException(ErrorCodes.CANCEL_PO_ERROR);
            }
            bipo.setStatus(PoAsnStatus.BIPO_CANCELED);
            bipo.setModifiedId(userId);
            int dp = this.biPoDao.saveOrUpdateByVersion(bipo);
            if (dp < 0) {
                throw new BusinessException(ErrorCodes.CANCEL_PO_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, bipo, null, userId, null, DbDataSource.MOREDB_INFOSOURCE);
        } catch (Exception e) {
            rm.setMsg("cancel bipo error");
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            return rm;
        }
        rm.setMsg("success");
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        return rm;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public ResponseMsg editBiPo(BiPo updatePo) {
        ResponseMsg rm=new ResponseMsg();
        try{
            int updatecount=this.biPoDao.saveOrUpdateByVersion(updatePo);
            if(updatecount<0){
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR); 
            }
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, updatePo, null, updatePo.getModifiedId(), null, DbDataSource.MOREDB_INFOSOURCE);
        }catch(Exception e){
            if(e instanceof BusinessException){
                rm.setResponseStatus(ResponseMsg.DATA_ERROR);
                rm.setMsg(((BusinessException)e).getErrorCode()+"");
                return rm;
            }
        }
        rm.setMsg("success");
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        return rm;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createSubPoToInfo(WhPo po, List<WhPoLine> whPoLineList) {
        if (null == po.getId()) {
            this.whPoDao.insert(po);
        } else {
            this.whPoDao.saveOrUpdateByVersion(po);
        }
        for (WhPoLine line : whPoLineList) {
            if (null == line.getId()) {
                line.setPoId(po.getId());
                this.whPoLineDao.insert(line);
            } else {
                this.whPoLineDao.saveOrUpdateByVersion(line);
            }
        }

    }

}
