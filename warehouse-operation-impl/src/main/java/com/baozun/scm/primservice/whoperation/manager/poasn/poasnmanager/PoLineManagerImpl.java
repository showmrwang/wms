package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.ArrayList;
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

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;


/**
 * 创建PO单据
 * 
 * @author bin.hu
 * 
 */
@Service("poLineManager")
@Transactional
public class PoLineManagerImpl implements PoLineManager {

    @Autowired
    private WhPoLineDao whPoLineDao;


    /**
     * 插入poline数据进基本库
     */
    @Override
    @MoreDB("infoSource")
    public void createPoLineSingleToInfo(WhPoLine whPoLine) {
        whPoLineDao.insert(whPoLine);
    }

    /**
     * 插入poline数据进拆库
     */
    @Override
    @MoreDB("shardSource")
    public void createPoLineSingleToShare(WhPoLine whPoLine) {
        whPoLineDao.insert(whPoLine);
    }

    @Override
    public Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.whPoLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    public Pagination<WhPoLineCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.whPoLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    @MoreDB("infoSource")
    public void deletePoLineByUuidToInfo(WhPoLineCommand WhPoLine) {
        whPoLineDao.deletePoLineByUuid(WhPoLine.getPoId(), WhPoLine.getOuId(), WhPoLine.getUuid());
    }

    @Override
    @MoreDB("shardSource")
    public void deletePoLineByUuidToShare(WhPoLineCommand WhPoLine) {
        whPoLineDao.deletePoLineByUuid(WhPoLine.getPoId(), WhPoLine.getOuId(), WhPoLine.getUuid());
    }

    @Override
    @MoreDB("infoSource")
    public void editPoLineToInfo(WhPoLine whPoLine) {
        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB("shardSource")
    public void editPoLineToShare(WhPoLine whPoLine) {
        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB("infoSource")
    public WhPoLineCommand findPoLinebyIdToInfo(WhPoLineCommand command) {
        return this.whPoLineDao.findWhPoLineById(command.getId(), command.getOuId());
    }

    @Override
    @MoreDB("shardSource")
    public WhPoLineCommand findPoLinebyIdToShard(WhPoLineCommand command) {
        return this.whPoLineDao.findWhPoLineById(command.getId(), command.getOuId());
    }

    @Override
    @MoreDB("infoSource")
    public int editPoLineStatusToInfo(WhPoLineCommand command) {
        int result = whPoLineDao.editPoLineStatus(command.getIds(), command.getStatus(), command.getModifiedId(), command.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != command.getIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {command.getIds().size(), result});
        }
        return result;
    }

    @Override
    @MoreDB("shardSource")
    public int editPoLineStatusToShard(WhPoLineCommand command) {
        int result = whPoLineDao.editPoLineStatus(command.getIds(), command.getStatus(), command.getModifiedId(), command.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != command.getIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {command.getIds().size(), result});
        }
        return result;
    }

    /**
     * 通过创建POLINE信息查找是否该PO单下有对应明细信息(基础库)
     */
    @Override
    @MoreDB("infoSource")
    public WhPoLine findPoLineByAddPoLineParamToInfo(WhPoLine line, Boolean type) {
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(PoAsnStatus.POLINE_NEW);
        statusList.add(PoAsnStatus.POLINE_CREATE_ASN);
        statusList.add(PoAsnStatus.POLINE_RCVD);
        if (type) {
            // 查询POLINE单正式数据
            line.setUuid(null);
        }
        return whPoLineDao.findPoLineByAddPoLineParam(statusList, line.getPoId(), null, line.getSkuId(), line.getIsIqc() == true ? 1 : 0, line.getMfgDate(), line.getExpDate(), line.getValidDate(), line.getBatchNo(), line.getCountryOfOrigin(),
                line.getInvStatus(), line.getUuid());
    }

    /**
     * 通过创建POLINE信息查找是否该PO单下有对应明细信息(拆库)
     */
    @Override
    @MoreDB("shardSource")
    public WhPoLine findPoLineByAddPoLineParamToShare(WhPoLine line, Boolean type) {
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(PoAsnStatus.POLINE_NEW);
        statusList.add(PoAsnStatus.POLINE_CREATE_ASN);
        statusList.add(PoAsnStatus.POLINE_RCVD);
        if (type) {
            // 查询POLINE单正式数据
            line.setUuid(null);
        }
        return whPoLineDao.findPoLineByAddPoLineParam(statusList, line.getPoId(), null, line.getSkuId(), line.getIsIqc() == true ? 1 : 0, line.getMfgDate(), line.getExpDate(), line.getValidDate(), line.getBatchNo(), line.getCountryOfOrigin(),
                line.getInvStatus(), line.getUuid());
    }

    /**
     * 修改POLINE明细
     */
    @Override
    @MoreDB("infoSource")
    public void updatePoLineSingleToInfo(WhPoLine whPoLine) {
        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    /**
     * 修改POLINE明细
     */
    @Override
    @MoreDB("shardSource")
    public void updatePoLineSingleToShare(WhPoLine whPoLine) {
        int result = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

    }
}
