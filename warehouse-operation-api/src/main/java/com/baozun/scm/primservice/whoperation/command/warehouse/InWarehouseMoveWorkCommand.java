package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryTobefilledCommand;
import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class InWarehouseMoveWorkCommand  extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1535971536020737829L;
    
    //columns START
    
    /** 库位ID 库位号 */
    private Long toLocationId;
    /** 分组信息 */
    private Map<Long, List<WhSkuInventoryCommand>> skuInventoryMap;
    /** 移动数量 */
    private Map<String, Double> idAndQtyMap;
    /** 分配库存集合 */
    private List<WhSkuInventoryAllocatedCommand> whSkuInventoryAllocatedCommandLst;
    /** 待移入库存集合 */
    private List<WhSkuInventoryTobefilledCommand> WhSkuInventoryTobefilledCommandLst;
    
    //columns END
    
    public List<WhSkuInventoryAllocatedCommand> getWhSkuInventoryAllocatedCommandLst() {
        return whSkuInventoryAllocatedCommandLst;
    }
    public void setWhSkuInventoryAllocatedCommandLst(List<WhSkuInventoryAllocatedCommand> whSkuInventoryAllocatedCommandLst) {
        this.whSkuInventoryAllocatedCommandLst = whSkuInventoryAllocatedCommandLst;
    }
    public List<WhSkuInventoryTobefilledCommand> getWhSkuInventoryTobefilledCommandLst() {
        return WhSkuInventoryTobefilledCommandLst;
    }
    public void setWhSkuInventoryTobefilledCommandLst(List<WhSkuInventoryTobefilledCommand> whSkuInventoryTobefilledCommandLst) {
        WhSkuInventoryTobefilledCommandLst = whSkuInventoryTobefilledCommandLst;
    }
    public Map<Long, List<WhSkuInventoryCommand>> getSkuInventoryMap() {
        return skuInventoryMap;
    }
    public void setSkuInventoryMap(Map<Long, List<WhSkuInventoryCommand>> skuInventoryMap) {
        this.skuInventoryMap = skuInventoryMap;
    }
    public Map<String, Double> getIdAndQtyMap() {
        return idAndQtyMap;
    }
    public void setIdAndQtyMap(Map<String, Double> idAndQtyMap) {
        this.idAndQtyMap = idAndQtyMap;
    }
    public Long getToLocationId() {
        return toLocationId;
    }
    public void setToLocationId(Long toLocationId) {
        this.toLocationId = toLocationId;
    }
    
}
