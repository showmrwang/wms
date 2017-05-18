package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.util.Locale;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;


public interface WhSkuInventoryManagerProxy extends BaseManager {

    ResponseMsg initSkuInv(String url, String fileName, Long userImportExcelId, Locale locale, Long ouId, Long userId, String logId);

}
