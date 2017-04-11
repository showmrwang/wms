package com.baozun.scm.primservice.whoperation.manager.confirm.outbound;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;

@Service("whOutboundConfirmManager")
@Transactional
public class WhOutboundConfirmManagerImpl implements WhOutboundConfirmManager {

    public static final Logger log = LoggerFactory.getLogger(WhOutboundConfirmManagerImpl.class);

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

}
