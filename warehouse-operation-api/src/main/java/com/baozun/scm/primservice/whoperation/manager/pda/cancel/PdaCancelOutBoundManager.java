package com.baozun.scm.primservice.whoperation.manager.pda.cancel;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;

public interface PdaCancelOutBoundManager extends BaseManager {

    int checkContainerCodeInSkuInventory(String containerCode, String latticNo, Long ouId);

    List<Container2ndCategory> getTwoLevelTypeList(Long ouId);

}
