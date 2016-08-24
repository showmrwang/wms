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
     * pda人工指定上架缓存容器号对应的sku信息
     * 
     * @author lijun.shen
     * @return
     */
    List<WhSkuInventory> manMadePutawayCacheSku(PdaManMadePutawayCommand pdaManMadePutawayCommand);

    

    /**
     * 将外部容器里的内部容器的货箱号存入缓存
     * 
     * @author lijun.shen
     * @param pdaManMadePutawayCommand
     * @return
     */
    List manMadePutawayCacheBinId(Long containerId, Long ouId);


  



}
