package com.baozun.scm.primservice.whoperation.util;

import java.security.NoSuchAlgorithmException;

import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

/**
 * 通过库存对象生成UUID
 * 
 * @author bin.hu
 * 
 */
public class SkuInventoryUuid {


    public static String invUuid(WhSkuInventory inv) throws NoSuchAlgorithmException {
        String uuid = null;
        if (null == inv) {
            return uuid;
        }
        // 拼接库存对应字段值
        String forMatString =
                inv.getSkuId() + inv.getLocationId() + inv.getOuterContainerId() + inv.getInsideContainerId() + inv.getCustomerId() + inv.getStoreId() + inv.getInvStatus() + inv.getInvType() + inv.getBatchNumber() + inv.getMfgDate() + inv.getExpDate()
                        + inv.getCountryOfOrigin() + inv.getInvAttr1() + inv.getInvAttr2() + inv.getInvAttr3() + inv.getInvAttr4() + inv.getInvAttr5();
        uuid = Md5Util.getMd5(forMatString);
        return uuid;
    }
}
