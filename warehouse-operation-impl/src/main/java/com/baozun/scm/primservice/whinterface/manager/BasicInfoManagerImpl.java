package com.baozun.scm.primservice.whinterface.manager;

import java.util.HashMap;
import java.util.Map;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whinfo.constant.DbDataSource;
import com.baozun.scm.primservice.whinfo.dao.warehouse.CustomerDao;
import com.baozun.scm.primservice.whinfo.dao.warehouse.StoreDao;
import com.baozun.scm.primservice.whinterface.manager.basicinfo.BasicInfoManager;
import com.baozun.scm.primservice.whinterface.model.basicinfo.WmsCustomer;
import com.baozun.scm.primservice.whinterface.model.basicinfo.WmsStore;
import com.baozun.scm.primservice.whinterface.msg.WmsResponse;

@Service("BasicInfoManager")
@Transactional
public class BasicInfoManagerImpl implements BasicInfoManager {
    public static final Logger log = LoggerFactory.getLogger(BasicInfoManager.class);
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private StoreDao storeDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Map<WmsResponse, WmsCustomer> wmsCustomer(String code) {
        log.info("BasicInfoManager.wmsCustomer begin");
        if (log.isDebugEnabled()) {
            log.debug("Param code is {}", code);
        }
        Map<WmsResponse, WmsCustomer> map = new HashMap<WmsResponse, WmsCustomer>();
        WmsResponse wmsResponse = new WmsResponse();
        if (null == code || code.length() == 0) {
            // 传递的编码为空 直接返回
            wmsResponse.setStatus(WmsResponse.STATUS_ERROR);
            wmsResponse.setMsg("输入编码不能为空");
            map.put(wmsResponse, null);
            return map;
        }
        WmsCustomer wmsCustomer = customerDao.findbyCode(code);
        if (null == wmsCustomer) {
            // 查询不到对应客户
            wmsResponse.setStatus(WmsResponse.STATUS_ERROR);
            wmsResponse.setMsg("无该编码对应客户信息");
            map.put(wmsResponse, null);
            return map;
        }
        wmsResponse.setStatus(WmsResponse.STATUS_SUCCESS);
        wmsResponse.setMsg("查询成功");
        map.put(wmsResponse, wmsCustomer);
        log.info("BasicInfoManager.wmsCustomer end");
        return map;
    }

    @Override
    public Map<WmsResponse, WmsStore> wmsStore(String code) {
        log.info("BasicInfoManager.wmsStore begin");
        if (log.isDebugEnabled()) {
            log.debug("Param code is {}", code);
        }
        Map<WmsResponse, WmsStore> map = new HashMap<WmsResponse, WmsStore>();
        WmsResponse wmsResponse = new WmsResponse();
        if (null == code || code.length() == 0) {
            // 传递的编码为空 直接返回
            wmsResponse.setStatus(WmsResponse.STATUS_ERROR);
            wmsResponse.setMsg("输入编码不能为空");
            map.put(wmsResponse, null);
            return map;
        }
        WmsStore wmsStore = storeDao.findByCode(code);
        if (null == wmsStore) {
            // 查询不到对应客户
            wmsResponse.setStatus(WmsResponse.STATUS_ERROR);
            wmsResponse.setMsg("无该编码对应客户信息");
            map.put(wmsResponse, null);
            return map;
        }
        wmsResponse.setStatus(WmsResponse.STATUS_SUCCESS);
        wmsResponse.setMsg("查询成功");
        map.put(wmsResponse, wmsStore);
        log.info("BasicInfoManager.wmsStore end");
        return map;
    }
}
