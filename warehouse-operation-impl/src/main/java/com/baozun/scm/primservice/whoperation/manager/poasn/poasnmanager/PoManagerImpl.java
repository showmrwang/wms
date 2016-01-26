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

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
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
    @MoreDB("infoSource")
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
    @MoreDB("shardSource")
    public Pagination<WhPoCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return whPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }


    @Override
    public ResponseMsg createPoAndLine(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        whPoDao.saveOrUpdate(po);
        if (whPoLines.size() > 0) {
            // 有line信息保存
            for (WhPoLine whPoLine : whPoLines) {
                whPoLine.setPoId(po.getId());
                whPoLineDao.saveOrUpdate(whPoLine);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(po.getId() + "");
        return rm;

    }

    /**
     * 保存po单信息
     * 
     */
    @Override
    @MoreDB("infoSource")
    public ResponseMsg createPoAndLineToInfo(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        whPoDao.saveOrUpdate(po);
        if (whPoLines.size() > 0) {
            // 有line信息保存
            for (WhPoLine whPoLine : whPoLines) {
                whPoLine.setPoId(po.getId());
                whPoLineDao.saveOrUpdate(whPoLine);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(po.getId() + "");
        return rm;

    }

    /**
     * 保存po单信息
     * 
     */
    @Override
    @MoreDB("shardSource")
    public ResponseMsg createPoAndLineToShare(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        whPoDao.saveOrUpdate(po);
        if (whPoLines.size() > 0) {
            // 有line信息保存
            for (WhPoLine whPoLine : whPoLines) {
                whPoLine.setPoId(po.getId());
                whPoLineDao.saveOrUpdate(whPoLine);
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
    @MoreDB("infoSource")
    public int editPoStatusByInfo(WhPoCommand whPo) {
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
    @MoreDB("shardSource")
    public int editPoStatusByShard(WhPoCommand whPo) {
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
    @MoreDB("infoSource")
    public WhPo findWhPoByIdByInfo(WhPoCommand whPo) {
        return whPoDao.findWhPoById(whPo.getId(), whPo.getOuId());
    }

    /**
     * 通过OP单ID查询相关信息 拆库表
     */
    @Override
    @MoreDB("shardSource")
    public WhPo findWhPoByIdByShard(WhPoCommand whPo) {
        return whPoDao.findWhPoById(whPo.getId(), whPo.getOuId());
    }

    /**
     * 更新PO单信息 基础表
     * 
     * @param whPo
     */
    @Override
    @MoreDB("infoSource")
    public void editPoByInfo(WhPo whPo) {
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
    @MoreDB("shardSource")
    public void editPoByShard(WhPo whPo) {
        int count = 0;
        count = whPoDao.saveOrUpdateByVersion(whPo);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    public ResponseMsg insertPoByPoAndStore(String poCode, Long storeId) {
        whPoDao.findPoByCodeAndStore(poCode, storeId);
        return null;
    }

    @Override
    public ResponseMsg insertPoByPoAndStore(String poCode, Long storeId, Long ouId) {
        // whPoDao.findPoByCodeAndStore(poCode, storeId, ouId);
        /* 插入操作 */
        return null;
    }

    @Override
    @MoreDB("infoSource")
    public void createPoLineSingleToInfo(WhPoLine whPoLine) {
        whPoLineDao.insert(whPoLine);
    }

    @Override
    @MoreDB("shardSource")
    public void createPoLineSingleToShare(WhPoLine whPoLine) {
        whPoLineDao.insert(whPoLine);
    }
}
