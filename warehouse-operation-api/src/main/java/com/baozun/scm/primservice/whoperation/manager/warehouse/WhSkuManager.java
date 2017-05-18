package com.baozun.scm.primservice.whoperation.manager.warehouse;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSku;

public interface WhSkuManager extends BaseManager {

    /**
     * 根据barcode查询商品信息
     * 
     * @author qiming.liu
     * @param barcode
     * @param ouId
     * @return
     */
    WhSkuCommand getSkuBybarCode(String barcode, Long ouId);

    WhSku getskuById(Long skuId, Long ouId);

    WhSku getSkuBySkuCodeOuId(String skuCode, Long ouId);

}
