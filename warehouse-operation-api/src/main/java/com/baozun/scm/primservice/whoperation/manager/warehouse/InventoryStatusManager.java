package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;

public interface InventoryStatusManager extends BaseManager{
    
    List<InventoryStatus> findInventoryStatusList(InventoryStatus status);
    List<InventoryStatus> findAllInventoryStatus();
}
