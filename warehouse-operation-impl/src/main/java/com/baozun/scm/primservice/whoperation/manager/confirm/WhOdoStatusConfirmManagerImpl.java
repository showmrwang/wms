package com.baozun.scm.primservice.whoperation.manager.confirm;

import java.util.Date;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.confirm.WhOdoStatusConfirmDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.confirm.WhOdoStatusConfirm;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

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
    public int saveWhOdoStatusConfirm(WhOdo whOdo) {
        Long count = 0L;
        log.info("WhOdoStatusConfirmManagerImpl.saveWhOdoStatusConfirm begin!");
        if (null == whOdo) {
            log.warn("WhOdoStatusConfirmManagerImpl.saveWhOdoStatusConfirm whOdo is null");
            return count.intValue();
        }
        // 获取客户信息
        Customer c = getCustomerByRedis(whOdo.getCustomerId());
        // 获取店铺信息
        Store s = getStoreByRedis(whOdo.getStoreId());
        // 获取仓库信息
        Warehouse w = getWhToRedis(whOdo.getOuId());
        // 生成出库单状态反馈数据
        WhOdoStatusConfirm odo = new WhOdoStatusConfirm();
        odo.setCustomerCode(c.getCustomerCode());
        odo.setStoreCode(s.getStoreCode());
        odo.setExtOdoCode(whOdo.getExtCode());
        odo.setWmsOdoCode(whOdo.getOdoCode());
        odo.setWmsOdoStatus(Integer.parseInt(whOdo.getOdoStatus()));
        odo.setWhCode(w.getCode());
        odo.setOuId(whOdo.getOuId());
        odo.setCreateTime(new Date());
        count = whOdoStatusConfirmDao.insert(odo);
        log.info("WhOdoStatusConfirmManagerImpl.saveWhOdoStatusConfirm end!");
        return count.intValue();
    }
}
