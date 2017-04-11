package com.baozun.scm.primservice.whoperation.manager.confirm;

import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.confirm.WhOdoStatusConfirmDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.confirm.WhOdoStatusConfirm;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;

@Service("whOdoStatusConfirmManager")
@Transactional
public class WhOdoStatusConfirmManagerImpl extends BaseManagerImpl implements WhOdoStatusConfirmManager {

    public static final Logger log = LoggerFactory.getLogger(WhOdoStatusConfirmManagerImpl.class);

    @Autowired
    private WhOdoStatusConfirmDao whOdoStatusConfirmDao;

    /***
     * 生成出库单状态反馈数据
     * 
     * @param whOdo
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveWhOdoStatusConfirm(WhOdo whOdo) {
        log.info("WhOdoStatusConfirmManagerImpl.saveWhOdoStatusConfirm begin!");
        Long count = 0L;
        if (null == whOdo) {
            log.warn("WhOdoStatusConfirmManagerImpl.saveWhOdoStatusConfirm whOdo is null");
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"whOdo"});
        }
        // 获取客户信息
        Customer c = getCustomerByRedis(whOdo.getCustomerId());
        // 获取店铺信息
        Store s = getStoreByRedis(whOdo.getStoreId());
        // 生成出库单状态反馈数据
        WhOdoStatusConfirm odo = new WhOdoStatusConfirm();
        odo.setCustomerCode(c.getCustomerCode());
        odo.setStoreCode(s.getStoreCode());
        odo.setExtOdoCode(whOdo.getExtCode());
        odo.setWmsOdoCode(whOdo.getOdoCode());
        odo.setWmsOdoStatus(Integer.parseInt(whOdo.getOdoStatus()));
        odo.setOuId(whOdo.getOuId());
        odo.setCreateTime(new Date());
        odo.setDataSource(whOdo.getDataSource());
        count = whOdoStatusConfirmDao.insert(odo);
        if (count.intValue() == 0) {
            log.error("WhOdoStatusConfirmManagerImpl saveWhOdoStatusConfirm error");
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        log.info("WhOdoStatusConfirmManagerImpl.saveWhOdoStatusConfirm end!");
    }

    /**
     * 通过创建时间段+仓库ID+数据来源获取对应出库单状态反馈数据
     * 
     * @param beginTime
     * @param endTime
     * @param ouid
     * @param dataSource
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoStatusConfirm> findWhOdoStatusConfirmByCreateTimeAndDataSource(String beginTime, String endTime, Long ouid, String dataSource) {
        return whOdoStatusConfirmDao.findWhOdoStatusConfirmByCreateTimeAndDataSource(beginTime, endTime, ouid, dataSource);
    }
}
