package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryFlowDao;

@Transactional
@Service("whSkuInventoryFlowManager")
public class WhSkuInventoryFlowManagerImpl implements WhSkuInventoryFlowManager {

    @Autowired
    private WhSkuInventoryFlowDao whSkuInventoryFlowDao;
    
}
