package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
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
}
