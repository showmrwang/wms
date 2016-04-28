package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;

@Service("BiPoLineManager")
@Transactional
public class BiPoLineManagerImpl extends BaseManagerImpl implements BiPoLineManager {

    @Autowired
    private BiPoLineDao biPoLineDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public BiPoLine findPoLineByAddPoLineParam(BiPoLine line, boolean type) {
        List<Integer> statusList = new ArrayList<Integer>();
        statusList.add(PoAsnStatus.POLINE_NEW);
        statusList.add(PoAsnStatus.POLINE_CREATE_ASN);
        statusList.add(PoAsnStatus.POLINE_RCVD);
        String uuid = line.getUuid();
        if (type) {
            // 查询POLINE单正式数据
            uuid = null;
        }
        return biPoLineDao.findPoLineByAddPoLineParam(statusList, line.getPoId(), null, line.getSkuId(), line.getIsIqc() == true ? 1 : 0, line.getMfgDate(), line.getExpDate(), line.getValidDate(), line.getBatchNo(), line.getCountryOfOrigin(),
                line.getInvStatus(), uuid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void createPoLineSingle(BiPoLine line) {
        biPoLineDao.insert(line);

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void updatePoLineSingle(BiPoLine wpl) {
        int result = biPoLineDao.saveOrUpdateByVersion(wpl);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<BiPoLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.biPoLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }


}
