package com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.pda.putaway.PdaManMadePutawayCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

/**
 * 人工指定上架缓存
 * 
 * @author shenlijun
 *
 */
public interface PdaManmadePutawayCacheManager extends BaseManager {

    /**
     * pda人工指定上架缓存sku信息
     * 
     * @author lijun.shen
     * @return
     */
    List<WhSkuInventory> manMadePutawayCacheSku(PdaManMadePutawayCommand pdaManMadePutawayCommand);

}
