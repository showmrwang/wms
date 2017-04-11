package com.baozun.scm.primservice.whoperation.util;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryAllocated;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;

/**
 * 通过库存对象生成UUID
 * 
 * @author bin.hu
 * 
 */
public class SkuInventoryUuid {


    public static String invUuid(WhSkuInventory inv) throws Exception {
        String uuid = null;
        if (null == inv) {
            return uuid;
        }
        // 拼接库存对应字段值
        String forMatString =
                inv.getSkuId().toString() + "" + (inv.getLocationId() == null ? "" : inv.getLocationId().toString()) + "" + (inv.getOuterContainerId() == null ? "" : inv.getOuterContainerId().toString()) + ""
                        + (inv.getInsideContainerId() == null ? "" : inv.getInsideContainerId().toString()) + inv.getCustomerId().toString() + inv.getStoreId().toString() + inv.getInvStatus().toString() + ""
                        + (inv.getInvType() == null ? "" : inv.getInvType().trim().toString()) + "" + (inv.getBatchNumber() == null ? "" : inv.getBatchNumber().trim().toString()) + ""
                        + (inv.getMfgDate() == null ? "" : new SimpleDateFormat("yyyyMMddHHmmss").format(inv.getMfgDate())) + "" + (inv.getExpDate() == null ? "" : new SimpleDateFormat("yyyyMMddHHmmss").format(inv.getExpDate())) + ""
                        + (inv.getCountryOfOrigin() == null ? "" : inv.getCountryOfOrigin().trim().toString()) + "" + (inv.getInvAttr1() == null ? "" : inv.getInvAttr1().trim().toString()) + ""
                        + (inv.getInvAttr2() == null ? "" : inv.getInvAttr2().trim().toString()) + "" + (inv.getInvAttr3() == null ? "" : inv.getInvAttr3().trim().toString()) + "" + (inv.getInvAttr4() == null ? "" : inv.getInvAttr4().trim().toString())
                        + "" + (inv.getInvAttr5() == null ? "" : inv.getInvAttr5().trim().toString()) + "" + (inv.getOutboundboxCode() == null ? "" : inv.getOutboundboxCode()) + ""
                        + (inv.getTemporaryLocationId() == null ? "" : inv.getTemporaryLocationId()) + (inv.getSeedingWallCode() == null ? "" : inv.getSeedingWallCode()) + (inv.getContainerLatticeNo() == null ? "" : inv.getContainerLatticeNo());
        uuid = Md5Util.getMd5(forMatString);
        return uuid;
    }

    /**
     * 
     * @author lichuan
     * @param inv
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String invUuid(WhSkuInventoryTobefilled inv) throws Exception {
        String uuid = null;
        if (null == inv) {
            return uuid;
        }
        // 拼接库存对应字段值
        String forMatString =
                inv.getSkuId().toString() + "" + (inv.getLocationId() == null ? "" : inv.getLocationId().toString()) + "" + (inv.getOuterContainerId() == null ? "" : inv.getOuterContainerId().toString()) + ""
                        + (inv.getInsideContainerId() == null ? "" : inv.getInsideContainerId().toString()) + inv.getCustomerId().toString() + inv.getStoreId().toString() + inv.getInvStatus().toString() + ""
                        + (inv.getInvType() == null ? "" : inv.getInvType().trim().toString()) + "" + (inv.getBatchNumber() == null ? "" : inv.getBatchNumber().trim().toString()) + ""
                        + (inv.getMfgDate() == null ? "" : new SimpleDateFormat("yyyyMMddHHmmss").format(inv.getMfgDate())) + "" + (inv.getExpDate() == null ? "" : new SimpleDateFormat("yyyyMMddHHmmss").format(inv.getExpDate())) + ""
                        + (inv.getCountryOfOrigin() == null ? "" : inv.getCountryOfOrigin().trim().toString()) + "" + (inv.getInvAttr1() == null ? "" : inv.getInvAttr1().trim().toString()) + ""
                        + (inv.getInvAttr2() == null ? "" : inv.getInvAttr2().trim().toString()) + "" + (inv.getInvAttr3() == null ? "" : inv.getInvAttr3().trim().toString()) + "" + (inv.getInvAttr4() == null ? "" : inv.getInvAttr4().trim().toString())
                        + "" + (inv.getInvAttr5() == null ? "" : inv.getInvAttr5().trim().toString());
        uuid = Md5Util.getMd5(forMatString);
        return uuid;
    }

    /**
     * 
     * @author lichuan
     * @param inv
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String invUuid(WhSkuInventoryAllocated inv) throws Exception {
        String uuid = null;
        if (null == inv) {
            return uuid;
        }
        // 拼接库存对应字段值
        String forMatString =
                inv.getSkuId().toString() + "" + (inv.getLocationId() == null ? "" : inv.getLocationId().toString()) + "" + (inv.getOuterContainerId() == null ? "" : inv.getOuterContainerId().toString()) + ""
                        + (inv.getInsideContainerId() == null ? "" : inv.getInsideContainerId().toString()) + inv.getCustomerId().toString() + inv.getStoreId().toString() + inv.getInvStatus().toString() + ""
                        + (inv.getInvType() == null ? "" : inv.getInvType().trim().toString()) + "" + (inv.getBatchNumber() == null ? "" : inv.getBatchNumber().trim().toString()) + ""
                        + (inv.getMfgDate() == null ? "" : new SimpleDateFormat("yyyyMMddHHmmss").format(inv.getMfgDate())) + "" + (inv.getExpDate() == null ? "" : new SimpleDateFormat("yyyyMMddHHmmss").format(inv.getExpDate())) + ""
                        + (inv.getCountryOfOrigin() == null ? "" : inv.getCountryOfOrigin().trim().toString()) + "" + (inv.getInvAttr1() == null ? "" : inv.getInvAttr1().trim().toString()) + ""
                        + (inv.getInvAttr2() == null ? "" : inv.getInvAttr2().trim().toString()) + "" + (inv.getInvAttr3() == null ? "" : inv.getInvAttr3().trim().toString()) + "" + (inv.getInvAttr4() == null ? "" : inv.getInvAttr4().trim().toString())
                        + "" + (inv.getInvAttr5() == null ? "" : inv.getInvAttr5().trim().toString());
        uuid = Md5Util.getMd5(forMatString);
        return uuid;
    }
}
