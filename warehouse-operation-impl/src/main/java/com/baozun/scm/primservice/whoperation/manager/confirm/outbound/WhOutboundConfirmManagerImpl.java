package com.baozun.scm.primservice.whoperation.manager.confirm.outbound;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.confirm.outbound.WhOutboundConfirmDao;
import com.baozun.scm.primservice.whoperation.dao.confirm.outbound.WhOutboundInvoiceConfirmDao;
import com.baozun.scm.primservice.whoperation.dao.confirm.outbound.WhOutboundInvoiceLineConfirmDao;
import com.baozun.scm.primservice.whoperation.dao.confirm.outbound.WhOutboundLineConfirmDao;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundInvoiceConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundInvoiceLineConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundLineConfirm;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;

@Service("whOutboundConfirmManager")
@Transactional
public class WhOutboundConfirmManagerImpl implements WhOutboundConfirmManager {

    public static final Logger log = LoggerFactory.getLogger(WhOutboundConfirmManagerImpl.class);

    @Autowired
    private WhOutboundConfirmDao whOutboundConfirmDao;
    @Autowired
    private WhOutboundLineConfirmDao whOutboundLineConfirmDao;
    @Autowired
    private WhOutboundInvoiceConfirmDao whOutboundInvoiceConfirmDao;
    @Autowired
    private WhOutboundInvoiceLineConfirmDao whOutboundInvoiceLineConfirmDao;


    /**
     * 生成出库单反馈数据 bin.hu
     * 
     * @param whOdo
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveWhOutboundConfirm(WhOdo whOdo) {
        log.info("WhOutboundConfirmManagerImpl.saveWhOutboundConfirm begin!");
        log.info("WhOutboundConfirmManagerImpl.saveWhOutboundConfirm end!");
    }

    /**
     * 通过创建时间段+仓库ID+数据来源获取对应出库单反馈数据 bin.hu
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOutboundConfirm> findWhOutboundConfirmByCreateTimeAndDataSource(String beginTime, String endTime, Long ouid, String dataSource) {
        // 获取出库单反馈数据
        List<WhOutboundConfirm> whOutboundConfirms = whOutboundConfirmDao.findWhOutboundConfirmByCreateTimeAndDataSource(beginTime, endTime, ouid, dataSource);
        for (WhOutboundConfirm whOutboundConfirm : whOutboundConfirms) {
            // 获取出库单明细数据
            List<WhOutboundLineConfirm> whOutboundLineConfirms = whOutboundLineConfirmDao.findWhOutboundLineConfirmByOutBoundId(whOutboundConfirm.getId(), ouid);
            whOutboundConfirm.setWhOutBoundLineConfirm(whOutboundLineConfirms);
            // 获取出库单发票信息
            List<WhOutboundInvoiceConfirm> whOutboundInvoiceConfirms = whOutboundInvoiceConfirmDao.findWhOutboundInvoiceConfirmByOutBoundId(whOutboundConfirm.getId(), ouid);
            for (WhOutboundInvoiceConfirm inv : whOutboundInvoiceConfirms) {
                // 获取发票明细信息
                List<WhOutboundInvoiceLineConfirm> invLine = whOutboundInvoiceLineConfirmDao.findWhOutboundInvoiceLineConfirmByInvoiceId(inv.getId(), ouid);
                inv.setWhOutBoundConfirmInvoiceLines(invLine);
            }
            whOutboundConfirm.setWhOutBoundInvoiceConfirm(whOutboundInvoiceConfirms);
        }
        return whOutboundConfirms;
    }

}
