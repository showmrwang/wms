package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;
import java.util.Locale;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;



public interface CreateInWarehouseMoveWorkManagerProxy extends BaseManager {
    
    /**
     * [业务方法] 创建并执行库内移动工作
     * @param ids
     * @param uuids
     * @param toLocation
     * @return
     */
    Boolean createAndExecuteInWarehouseMoveWork( String[] occupationCodes, Long[] occupationLineIds, String[] uuids, Double[] moveQtys, Long toLocation, Boolean isExecute, Long ouId, Long userId, String snKey);
    
    /**
     * [业务方法] 导入sn和残次条码
     * @param url
     * @param fileName
     * @param userImportExcelId
     * @param locale
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    String batchImport(String url, String fileName, Long userImportExcelId, Locale locale,Long ouId, Long userId, String logId);
    
    /**
     * [业务方法] 创库内移动工作校验
     * @param ids
     * @param uuids
     * @param toLocationId
     * @param moveQtys
     * @param ouId
     * @return
     */
    Integer systemCheck(String[] occupationCodes, Long[] occupationLineIds, String[] uuids, Long toLocationId, Double[] moveQtys, Long ouId);
    
}
