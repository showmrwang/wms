package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.warehouse.InWarehouseMoveWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.CreateInWarehouseMoveWorkManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;

@Service("createInWarehouseMoveWorkManagerProxy")
public class CreateInWarehouseMoveWorkManagerProxyImpl implements CreateInWarehouseMoveWorkManagerProxy {

    public static final Logger log = LoggerFactory.getLogger(CreateInWarehouseMoveWorkManagerProxyImpl.class);
    
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private CreateInWarehouseMoveWorkManager createInWarehouseMoveWorkManager;
    

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
    public Boolean createAndExecuteInWarehouseMoveWork(Long[] ids, String[] uuids, Double[] moveQtys, Long toLocationId, Boolean isExecute, Long ouId, Long userId) {
        Boolean isSuccess = true;
        // 1.系统校验
        Boolean systemCheck = this.systemCheck();
        if(false == systemCheck){
            return false;    
        }
        // 2.将库存行根据原始库位与目标库位进行分组
        InWarehouseMoveWorkCommand inWarehouseMoveWorkCommand = this.getSkuInventoryForGroup(ids, uuids, moveQtys, ouId);
        inWarehouseMoveWorkCommand.setToLocationId(toLocationId);
        Map<String, List<WhSkuInventoryCommand>> skuInventoryMap = inWarehouseMoveWorkCommand.getSkuInventoryMap();
        // 3.循环库存分组信息分别创建工作
        for(String key : skuInventoryMap.keySet()){
            // 4.获取每个分组的所有库存明细数据
            List<WhSkuInventoryCommand> skuInventoryCommandLst = skuInventoryMap.get(key);
            try {
                // 5.库存分配（生成分配库存与待移入库存）
                inWarehouseMoveWorkCommand = createInWarehouseMoveWorkManager.saveAllocatedAndTobefilled(inWarehouseMoveWorkCommand, skuInventoryCommandLst);
                // 6-9.创建库内移动工作               
                createInWarehouseMoveWorkManager.createInWarehouseMoveWork(inWarehouseMoveWorkCommand, ouId, userId);
                // 10.是否直接执行
                if(true == isExecute){
                    // 11.库内移动工作执行
                    createInWarehouseMoveWorkManager.executeInWarehouseMoveWork(inWarehouseMoveWorkCommand, ouId, userId);
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
     * 系统校验
     * 
     * @param 
     * @return
     */
    private Boolean systemCheck() {
        Boolean isSuccess = true;
        try {
            // 1.客户：用户勾选库存库存必须同客户
            // 2.库存数量：用户选择的库存记录行必须是未分配的非冻结且非待移入的库存
            // 3.库位管理属性：库位允许混放，需判断混放sku数与最大混放属性数库位不允许混放，库存属性必须保持一致静态库位，只能移动绑定的sku
            // 4.商品管理属性：不允许混放，只能放到存在当前sku的库位或空库位
            // 5.目标库位体积重量计算
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        return isSuccess;
    }
    
    /**
     * 将库存行根据原始库位与目标库位进行分组
     * 
     * @param ids
     * @param uuids
     * @param ouId
     * @return
     */
    private InWarehouseMoveWorkCommand getSkuInventoryForGroup(Long[] ids, String[] uuids, Double[] moveQtys, Long ouId) {
        InWarehouseMoveWorkCommand inWarehouseMoveWorkCommand = new InWarehouseMoveWorkCommand();
        Map<String, List<WhSkuInventoryCommand>> skuInventoryMap = new HashMap<String, List<WhSkuInventoryCommand>>();
        Map<Long, Double> idAndQtyMap = new HashMap<Long, Double>();
        try {
            for(int i = 0; i < ids.length; i++){
                List<WhSkuInventoryCommand> skuInventoryCommandLst = new ArrayList<WhSkuInventoryCommand>();
                WhSkuInventoryCommand whSkuInventoryCommand = whSkuInventoryManager.findWhSkuInventoryByIdAndUuidAndOuid(ids[i], uuids[i], ouId);
                if(null != skuInventoryMap.get(whSkuInventoryCommand.getLocationCode())){
                    skuInventoryCommandLst = skuInventoryMap.get(whSkuInventoryCommand.getLocationCode());
                }
                skuInventoryCommandLst.add(whSkuInventoryCommand);
                skuInventoryMap.put(whSkuInventoryCommand.getLocationCode(), skuInventoryCommandLst);
                idAndQtyMap.put(ids[i], moveQtys[i]);
            }
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        
        inWarehouseMoveWorkCommand.setSkuInventoryMap(skuInventoryMap);
        inWarehouseMoveWorkCommand.setIdAndQtyMap(idAndQtyMap);
        return inWarehouseMoveWorkCommand;
    }
    
}
