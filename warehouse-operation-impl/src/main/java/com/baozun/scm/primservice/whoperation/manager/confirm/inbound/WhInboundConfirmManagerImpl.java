package com.baozun.scm.primservice.whoperation.manager.confirm.inbound;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whinterface.dao.inbound.WhInboundConfirmDao;
import com.baozun.scm.primservice.whinterface.dao.inbound.WhInboundInvLineConfirmDao;
import com.baozun.scm.primservice.whinterface.dao.inbound.WhInboundLineConfirmDao;
import com.baozun.scm.primservice.whinterface.dao.inbound.WhInboundSnLineConfirmDao;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundConfirm;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundInvLineConfirm;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundLineConfirm;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundSnLineConfirm;

@Service("whInboundConfirmManager")
@Transactional
public class WhInboundConfirmManagerImpl implements WhInboundConfirmManager {

    public static final Logger log = LoggerFactory.getLogger(WhInboundConfirmManagerImpl.class);

    @Autowired
    private WhInboundConfirmDao whInboundConfirmDao;
    @Autowired
    private WhInboundLineConfirmDao whInboundLineConfirmDao;
    @Autowired
    private WhInboundInvLineConfirmDao whInboundInvLineConfirmDao;
    @Autowired
    private WhInboundSnLineConfirmDao whInboundSnLineConfirmDao;

    /**
     * 通过创建时间段+数据来源获取对应入库单反馈数据 bin.hu
     * 
     * @param beginTime
     * @param endTime
     * @param dataSource
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<WhInboundConfirm> findWhInboundConfirmByCreateTimeAndDataSource(String beginTime, String endTime, String dataSource) {
        // 获取入库单反馈数据
        List<WhInboundConfirm> whInboundConfirms = whInboundConfirmDao.findWhInboundConfirmByCreateTimeAndDataSource(beginTime, endTime, dataSource);
        for (WhInboundConfirm whInboundConfirm : whInboundConfirms) {
            // 获取入库单反馈明细数据
            List<WhInboundLineConfirm> whInboundLineConfirms = whInboundLineConfirmDao.findWhInboundLineConfirmByInboundId(whInboundConfirm.getId());
            for (WhInboundLineConfirm iLine : whInboundLineConfirms) {
                // 获取入库单反馈明细库存明细数据
                List<WhInboundInvLineConfirm> invLineConfirms = whInboundInvLineConfirmDao.findWhInboundInvLineConfirmByInboundLineId(iLine.getId());
                for (WhInboundInvLineConfirm invLine : invLineConfirms) {
                    // 获取入库单反馈明细SN/残次明细数据
                    List<WhInboundSnLineConfirm> snLine = whInboundSnLineConfirmDao.findWhInboundSnLineConfirmByInvLineId(invLine.getId());
                    invLine.setWhInboundSnLineConfirms(snLine);
                }
                iLine.setWhInboundInvLineConfirms(invLineConfirms);
            }
            whInboundConfirm.setWhInboundLineConfirms(whInboundLineConfirms);
        }
        return whInboundConfirms;
    }

}
