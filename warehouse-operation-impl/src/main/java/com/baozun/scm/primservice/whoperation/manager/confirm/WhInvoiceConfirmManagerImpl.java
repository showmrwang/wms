package com.baozun.scm.primservice.whoperation.manager.confirm;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.confirm.WhInvoiceConfirmDao;
import com.baozun.scm.primservice.whoperation.model.confirm.WhInvoiceConfirm;

@Service("whInvoiceConfirmManager")
@Transactional
public class WhInvoiceConfirmManagerImpl implements WhInvoiceConfirmManager {

    public static final Logger log = LoggerFactory.getLogger(WhInvoiceConfirmManagerImpl.class);

    @Autowired
    private WhInvoiceConfirmDao whInvoiceConfirmDao;

    /**
     * 通过创建时间段+仓库ID+数据来源获取对应发票反馈数据
     * 
     * @param beginTime
     * @param endTime
     * @param ouid
     * @param dataSource
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhInvoiceConfirm> findWhInvoiceConfirmByCreateTimeAndDataSource(String beginTime, String endTime, Long ouid, String dataSource) {
        return whInvoiceConfirmDao.findWhInvoiceConfirmByCreateTimeAndDataSource(beginTime, endTime, ouid, dataSource);
    }

}
