package com.baozun.scm.primservice.whoperation.command.warehouse.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;

public class CreatePickingWorkResultCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -3678225414701753154L;
    
    //columns START
    /** 在库库存 */
    private Map<Long, List<WhSkuInventory>> odoLineIdAndInventory = new HashMap<Long, List<WhSkuInventory>>();
    /** 待移入库存 */
    private Map<Long, List<WhSkuInventoryTobefilled>> odoLineIdAndTobefilled = new HashMap<Long, List<WhSkuInventoryTobefilled>>();
    /** 出库单明细和数量 */
    private Map<Long, Double> odoLineIdAndQty = new HashMap<Long, Double>();
    //columns END
    
    public Map<Long, List<WhSkuInventory>> getOdoLineIdAndInventory() {
        return odoLineIdAndInventory;
    }
    public void setOdoLineIdAndInventory(Map<Long, List<WhSkuInventory>> odoLineIdAndInventory) {
        this.odoLineIdAndInventory = odoLineIdAndInventory;
    }
    public Map<Long, List<WhSkuInventoryTobefilled>> getOdoLineIdAndTobefilled() {
        return odoLineIdAndTobefilled;
    }
    public void setOdoLineIdAndTobefilled(Map<Long, List<WhSkuInventoryTobefilled>> odoLineIdAndTobefilled) {
        this.odoLineIdAndTobefilled = odoLineIdAndTobefilled;
    }
    public Map<Long, Double> getOdoLineIdAndQty() {
        return odoLineIdAndQty;
    }
    public void setOdoLineIdAndQty(Map<Long, Double> odoLineIdAndQty) {
        this.odoLineIdAndQty = odoLineIdAndQty;
    }
    
}
