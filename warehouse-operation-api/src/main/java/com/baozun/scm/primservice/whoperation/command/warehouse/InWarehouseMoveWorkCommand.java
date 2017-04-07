package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryTobefilledCommand;
import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class InWarehouseMoveWorkCommand  extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1535971536020737829L;
    
    //columns START
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
    
}
