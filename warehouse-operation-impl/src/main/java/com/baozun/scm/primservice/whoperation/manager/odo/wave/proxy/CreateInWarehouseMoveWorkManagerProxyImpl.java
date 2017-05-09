package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.warehouse.InWarehouseMoveWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.excel.ExcelContext;
import com.baozun.scm.primservice.whoperation.excel.result.ExcelImportResult;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.CreateInWarehouseMoveWorkManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.pda.work.PdaPickingWorkManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.LocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;

@Service("createInWarehouseMoveWorkManagerProxy")
public class CreateInWarehouseMoveWorkManagerProxyImpl implements CreateInWarehouseMoveWorkManagerProxy {

    public static final Logger log = LoggerFactory.getLogger(CreateInWarehouseMoveWorkManagerProxyImpl.class);

    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private CreateInWarehouseMoveWorkManager createInWarehouseMoveWorkManager;
    @Autowired
    private PdaPickingWorkManager pdaPickingWorkManager;
    @Autowired
    private WhLocationDao whLocationDao;
    @Autowired
    private LocationManager locationManager;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhSkuInventoryTobefilledDao whSkuInventoryTobefilledDao;
    @Autowired
    private WhSkuLocationDao whSkuLocationDao;



    /**
     * 创建并执行库内移动工作
     * 
     * @param ids
     * @param uuids
     * @param toLocation
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean createAndExecuteInWarehouseMoveWork( String[] occupationCodes, Long[] occupationLineIds, String[] uuids, Double[] moveQtys, Long toLocation, Boolean isExecute, Long ouId, Long userId, String snKey) {
        Boolean isSuccess = false;
        List<WhSkuInventorySn> skuInventorySnLst = new ArrayList<WhSkuInventorySn>();
        skuInventorySnLst = createInWarehouseMoveWorkManager.getSnStatistics(snKey);
        // 2.将库存行根据原始库位与目标库位进行分组
        InWarehouseMoveWorkCommand inWarehouseMoveWorkCommand = this.getSkuInventoryForGroup(occupationCodes, occupationLineIds, uuids, moveQtys, ouId);
        inWarehouseMoveWorkCommand.setToLocationId(toLocation);
        Map<Long, List<WhSkuInventoryCommand>> skuInventoryMap = inWarehouseMoveWorkCommand.getSkuInventoryMap();
        // 3.循环库存分组信息分别创建工作
        for (Long key : skuInventoryMap.keySet()) {
            // 4.获取每个分组的所有库存明细数据
            List<WhSkuInventoryCommand> skuInventoryCommandLst = skuInventoryMap.get(key);
            try {
                // 5.库存分配（生成分配库存与待移入库存）
                inWarehouseMoveWorkCommand = createInWarehouseMoveWorkManager.saveAllocatedAndTobefilled(inWarehouseMoveWorkCommand, skuInventoryCommandLst);
                // 6-9.创建库内移动工作
                String inWarehouseMoveWorkCode = createInWarehouseMoveWorkManager.createInWarehouseMoveWork(inWarehouseMoveWorkCommand, ouId, userId);
                // 10.是否直接执行
                if (true == isExecute) {
                    // 11.库内移动工作执行
                    isSuccess = createInWarehouseMoveWorkManager.executeInWarehouseMoveWork(inWarehouseMoveWorkCode, ouId, userId, skuInventorySnLst);
                }
            } catch (Exception e) {
                log.error(e + "");
                isSuccess = false;
                continue;
            }
        }
        // 12.所有统计分组是否都已创建工作
        return isSuccess;
    }

    /**
     * 创库内移动工作校验
     * 
     * @param 
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Integer systemCheck(String[] occupationCodes, Long[] occupationLineIds, String[] uuids, Long toLocationId, Double[] moveQtys, Long ouId) {
        // sku种类和数量
        Set<Long> customerIds = new HashSet<Long>();
        // sku种类和数量
        Set<Long> skus = new HashSet<Long>();
        // sku属性和数量
        Set<String> onlySkus = new HashSet<String>();
        // 获取目标库位信息
        Location location = whLocationDao.findByIdExt(toLocationId, ouId);
        // 获取目标库位库存信息
        List<WhSkuInventoryCommand> toSkuInventoryCommandLst = whSkuInventoryDao.findWhSkuInvCmdByLocation(ouId, toLocationId);
        if (null != toSkuInventoryCommandLst) {
            for (WhSkuInventoryCommand toSkuInventoryCommand : toSkuInventoryCommandLst) {
                String onlySku = SkuCategoryProvider.getSkuAttrIdByInv(toSkuInventoryCommand);
                skus.add(toSkuInventoryCommand.getSkuId());
                onlySkus.add(onlySku);
                customerIds.add(toSkuInventoryCommand.getCustomerId());
            }
        }
        // 获取目标库位待移入库存信息
        List<WhSkuInventoryTobefilled> toSkuInventoryTobefilledLst = whSkuInventoryTobefilledDao.findLocWhSkuInventoryTobefilled(toLocationId, ouId);
        if (null != toSkuInventoryCommandLst) {
            for (WhSkuInventoryTobefilled toSkuInventoryTobefilled : toSkuInventoryTobefilledLst) {
                String onlySku = SkuCategoryProvider.getSkuAttrIdByWhSkuInvTobefilled(toSkuInventoryTobefilled);
                skus.add(toSkuInventoryTobefilled.getSkuId());
                onlySkus.add(onlySku);
                customerIds.add(toSkuInventoryTobefilled.getCustomerId());
            }
        }
        // 统计库内移动信息
        List<WhSkuInventoryCommand> skuInventoryCommandLst = new ArrayList<WhSkuInventoryCommand>();
        for (int i = 0; i < uuids.length; i++) {
            WhSkuInventoryCommand whSkuInventoryCommand = new WhSkuInventoryCommand();
            String occupationCode = "";
            Long occupationLineId = 0L;
            if(0 < occupationCodes.length){
                occupationCode = occupationCodes[i];    
            }else{
                occupationCode = null;
            }
            if(0 < occupationLineIds.length){
                occupationLineId = occupationLineIds[i];    
            }else{
                occupationLineId = null;
            }
            whSkuInventoryCommand = whSkuInventoryManager.findInvLstByOccupationCode(occupationCode, occupationLineId, uuids[i], ouId); 
            String onlySku = SkuCategoryProvider.getSkuAttrIdByInv(whSkuInventoryCommand);
            skus.add(whSkuInventoryCommand.getSkuId());
            onlySkus.add(onlySku);
            skuInventoryCommandLst.add(whSkuInventoryCommand);
            customerIds.add(whSkuInventoryCommand.getCustomerId());
        }
        // 客户唯一校验
        if(null != customerIds && 1 < customerIds.size()){
            return ErrorCodes.IN_WAREHOUSE_MOVE_CUSTOMER_ERROR;
        }
        // 库位管理属性校验
        if (true == location.getIsMixStacking()) {
            if (location.getMixStackingNumber() < skus.size()) {
                return ErrorCodes.IN_WAREHOUSE_MOVE_MIX_STACKING_NUMBER_ERROR;
            }
            if(location.getMaxChaosSku() < onlySkus.size()){
                return ErrorCodes.IN_WAREHOUSE_MOVE_MAX_CHAOS_SKU_ERROR;
            }
        } else {
            if (1 != skus.size() || 1 != onlySkus.size()) {
                return ErrorCodes.IN_WAREHOUSE_MOVE_MIX_STACKING_ERROR;
            }
        }
        // 静态库位是否绑定商品
        if (true == location.getIsStatic()) {
            for (Long skuId : skus) {
                int isSuccess = whSkuLocationDao.findSkuCountInSkuLocation(ouId, toLocationId, skuId);
                if (0 == isSuccess) {
                    return ErrorCodes.IN_WAREHOUSE_MOVE_ISSTATIC_ERROR;
                }
            }
        }
        return null;
    }

    /**
     * 将库存行根据原始库位与目标库位进行分组
     * 
     * @param ids
     * @param uuids
     * @param ouId
     * @return
     */
    private InWarehouseMoveWorkCommand getSkuInventoryForGroup(String[] occupationCodes, Long[] occupationLineIds, String[] uuids, Double[] moveQtys, Long ouId) {
        InWarehouseMoveWorkCommand inWarehouseMoveWorkCommand = new InWarehouseMoveWorkCommand();
        Map<Long, List<WhSkuInventoryCommand>> skuInventoryMap = new HashMap<Long, List<WhSkuInventoryCommand>>();
        Map<String, Double> idAndQtyMap = new HashMap<String, Double>();
        try {
            for (int i = 0; i < uuids.length; i++) {
                List<WhSkuInventoryCommand> skuInventoryCommandLst = new ArrayList<WhSkuInventoryCommand>();
                WhSkuInventoryCommand skuInventoryCommand = new WhSkuInventoryCommand();
                skuInventoryCommand.setUuid(uuids[i]);
                if(0 < occupationCodes.length){
                    skuInventoryCommand.setOccupationCode(occupationCodes[i]);
                }
                if(0 < occupationLineIds.length){
                    skuInventoryCommand.setOccupationLineId(occupationLineIds[i]);
                }
                skuInventoryCommand.setOuId(ouId);
                List<WhSkuInventoryCommand> whSkuInventoryCommandLst = whSkuInventoryManager.findInvComLstByInWarehouseMove(skuInventoryCommand);
                for(WhSkuInventoryCommand whSkuInventoryCommand : whSkuInventoryCommandLst){
                    if (null != skuInventoryMap.get(whSkuInventoryCommand.getLocationCode())) {
                        skuInventoryCommandLst = skuInventoryMap.get(whSkuInventoryCommand.getLocationCode());
                    }
                    skuInventoryCommandLst.add(whSkuInventoryCommand);
                    skuInventoryMap.put(whSkuInventoryCommand.getLocationId(), skuInventoryCommandLst);    
                }
                if(0 < occupationCodes.length){
                    if(0 < occupationLineIds.length){
                        idAndQtyMap.put(occupationCodes[i] + "-" + occupationLineIds[i] + "-" + uuids[i], moveQtys[i]);
                    }else{
                        idAndQtyMap.put(occupationCodes[i] + "-" + "-" + uuids[i], moveQtys[i]);
                    }
                }else{
                    if(0 < occupationLineIds.length){
                        idAndQtyMap.put("-" + occupationLineIds[i] + "-" + uuids[i], moveQtys[i]);
                    }else{
                        idAndQtyMap.put("-" + "-" + uuids[i], moveQtys[i]);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        inWarehouseMoveWorkCommand.setSkuInventoryMap(skuInventoryMap);
        inWarehouseMoveWorkCommand.setIdAndQtyMap(idAndQtyMap);
        return inWarehouseMoveWorkCommand;
    }

    /**
     * 批量导入Sn和残次条码Execl
     * 
     * @param url
     * @param fileName
     * @param userImportExcelId
     * @param locale
     * @param ouId
     * @param userId
     * @param logId 
     * @return
     */
    @Override
    public String batchImport(String url, String fileName, Long userImportExcelId, Locale locale, Long ouId, Long userId, String logId) {
        File importExcelFile = new File(url, fileName);
        if (!importExcelFile.exists()) {
            throw new BusinessException("文件不存在");
        }
        List<WhSkuInventorySn> skuInventorySnsLst = new ArrayList<WhSkuInventorySn>();
        String key = "";
        try {
            // 创建excel上下文实例,它的构成需要配置文件的路径
            ExcelContext context = new ExcelContext("excel-config.xml");
            // Sn和残次条码
            ExcelImportResult result = this.readSkuFromExcel(context, importExcelFile, locale);
            skuInventorySnsLst = result.getListBean();
            key = createInWarehouseMoveWorkManager.snStatisticsRedis(skuInventorySnsLst);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    private ExcelImportResult readSkuFromExcel(ExcelContext context, File importExcelFile, Locale locale) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(importExcelFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new BusinessException("文件读取异常");
        }
        return context.readExcel("whSkuInventorySn", 0, inputStream, locale);
    }

    @Override
    public List<WhSkuInventorySn> operationSnList(String key) {
        List<WhSkuInventorySn> skuInventorySnLst =  createInWarehouseMoveWorkManager.getSnStatistics(key);
        createInWarehouseMoveWorkManager.delSnStatistics(key);
        return skuInventorySnLst;
    }

}
