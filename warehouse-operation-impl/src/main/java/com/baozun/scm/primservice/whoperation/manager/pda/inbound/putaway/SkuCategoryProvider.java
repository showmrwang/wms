/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway;

import java.text.SimpleDateFormat;

import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;

/**
 * @author lichuan
 *
 */
public final class SkuCategoryProvider {
    // 占位符
    public static final String PH = "︴";
    // 分隔符
    public static final String DV = "┊";
    // 属性数
    public static int AN = 10;
    public static int IDX_SKUID = 0;
    public static int IDX_INVTYPE = 1;
    public static int IDX_INVSTATUS = 2;
    public static int IDX_MFGDATE = 3;
    public static int IDX_EXPDATE = 4;
    public static int IDX_INVATTR1 = 5;
    public static int IDX_INVATTR2 = 6;
    public static int IDX_INVATTR3 = 7;
    public static int IDX_INVATTR4 = 8;
    public static int IDX_INVATTR5 = 9;
    public static int IDX_SKUSN = 10;
    public static int IDX_SKUDEFECT = 11;

    public static String getSkuAttrIdByInv(WhSkuInventoryCommand invCmd) {
        String ret = "";
        Long skuId = invCmd.getSkuId();
        String invType = (StringUtils.isEmpty(invCmd.getInvType()) ? PH : invCmd.getInvType());
        String invStatus = (StringUtils.isEmpty(invCmd.getInvStatus()) ? PH : invCmd.getInvStatus() + "");
//         String batchNumber = (StringUtils.isEmpty(invCmd.getBatchNumber()) ? PH : invCmd.getBatchNumber());
        String mfgDate = (StringUtils.isEmpty(invCmd.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getMfgDate()));
        String expDate = (StringUtils.isEmpty(invCmd.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getExpDate()));
//         String countryOfOrigin = (StringUtils.isEmpty(invCmd.getCountryOfOrigin()) ? PH :
//         invCmd.getCountryOfOrigin());
        String invAttr1 = (StringUtils.isEmpty(invCmd.getInvAttr1()) ? PH : invCmd.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(invCmd.getInvAttr2()) ? PH : invCmd.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(invCmd.getInvAttr3()) ? PH : invCmd.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(invCmd.getInvAttr4()) ? PH : invCmd.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(invCmd.getInvAttr5()) ? PH : invCmd.getInvAttr5());
//         ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + mfgDate + DV +
//         expDate + DV + countryOfOrigin + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV +
//         invAttr4 + DV + invAttr5;
        ret = skuId + DV + invType + DV + invStatus + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        return ret;
    }
    
    public static String getSkuAttrIdByOperationLine(WhOperationLineCommand opeCmd) {
        String ret = "";
        Long skuId = opeCmd.getSkuId();
        String invType = (StringUtils.isEmpty(opeCmd.getInvType()) ? PH : opeCmd.getInvType());
        String invStatus = (StringUtils.isEmpty(opeCmd.getInvStatus()) ? PH : opeCmd.getInvStatus() + "");
        String mfgDate = (StringUtils.isEmpty(opeCmd.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(opeCmd.getMfgDate()));
        String expDate = (StringUtils.isEmpty(opeCmd.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(opeCmd.getExpDate()));
        String invAttr1 = (StringUtils.isEmpty(opeCmd.getInvAttr1()) ? PH : opeCmd.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(opeCmd.getInvAttr2()) ? PH : opeCmd.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(opeCmd.getInvAttr3()) ? PH : opeCmd.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(opeCmd.getInvAttr4()) ? PH : opeCmd.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(opeCmd.getInvAttr5()) ? PH : opeCmd.getInvAttr5());
        ret = skuId + DV + invType + DV + invStatus + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        return ret;
    }
    
    public static String getSkuAttrIdByOperationExecLine(WhOperationExecLine opeCmd) {
        String ret = "";
        Long skuId = opeCmd.getSkuId();
        String invType = (StringUtils.isEmpty(opeCmd.getInvType()) ? PH : opeCmd.getInvType());
        String invStatus = (StringUtils.isEmpty(opeCmd.getInvStatus()) ? PH : opeCmd.getInvStatus() + "");
        String mfgDate = (StringUtils.isEmpty(opeCmd.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(opeCmd.getMfgDate()));
        String expDate = (StringUtils.isEmpty(opeCmd.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(opeCmd.getExpDate()));
        String invAttr1 = (StringUtils.isEmpty(opeCmd.getInvAttr1()) ? PH : opeCmd.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(opeCmd.getInvAttr2()) ? PH : opeCmd.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(opeCmd.getInvAttr3()) ? PH : opeCmd.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(opeCmd.getInvAttr4()) ? PH : opeCmd.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(opeCmd.getInvAttr5()) ? PH : opeCmd.getInvAttr5());
        ret = skuId + DV + invType + DV + invStatus + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        return ret;
    }

    public static String getSkuAttrIdByWhSkuInvTobefilled(WhSkuInventoryTobefilled skuTobefilled) {
        String ret = "";
        Long skuId = skuTobefilled.getSkuId();
        String invType = (StringUtils.isEmpty(skuTobefilled.getInvType()) ? PH : skuTobefilled.getInvType());
        String invStatus = (StringUtils.isEmpty(skuTobefilled.getInvStatus()) ? PH : skuTobefilled.getInvStatus() + "");
//        String batchNumber = (StringUtils.isEmpty(skuTobefilled.getBatchNumber()) ? PH : skuTobefilled.getBatchNumber());
        String mfgDate = (StringUtils.isEmpty(skuTobefilled.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(skuTobefilled.getMfgDate()));
        String expDate = (StringUtils.isEmpty(skuTobefilled.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(skuTobefilled.getExpDate()));
//        String countryOfOrigin = (StringUtils.isEmpty(skuTobefilled.getCountryOfOrigin()) ? PH :
//            skuTobefilled.getCountryOfOrigin());
        String invAttr1 = (StringUtils.isEmpty(skuTobefilled.getInvAttr1()) ? PH : skuTobefilled.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(skuTobefilled.getInvAttr2()) ? PH : skuTobefilled.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(skuTobefilled.getInvAttr3()) ? PH : skuTobefilled.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(skuTobefilled.getInvAttr4()) ? PH : skuTobefilled.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(skuTobefilled.getInvAttr5()) ? PH : skuTobefilled.getInvAttr5());
        ret = skuId + DV + invType + DV + invStatus + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
//        ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + mfgDate + DV +
//                expDate + DV + countryOfOrigin + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV +
//                invAttr4 + DV + invAttr5;
        return ret;
    }
    
    public static String concatSkuAttrId(Object... argArray) {
        String ret = "";
        int i = 0;
        for (Object obj : argArray) {
            if (0 == i) {
                if (StringUtils.isEmpty(obj)) {
                    ret += PH;
                } else {
                    ret += obj.toString();
                }
            } else {
                if (StringUtils.isEmpty(obj)) {
                    ret = ret + DV + PH;
                } else {
                    ret = ret + DV + obj.toString();
                }
            }
            i++;
        }
        return ret;
    }

    public static Long getSkuId(String skuAttrId) {
        Long id = null;
        String[] values = skuAttrId.split(DV);
        if (StringUtils.isEmpty(values) || 0 == values.length) {
            id = null;
        } else {
            id = (StringUtils.isEmpty(values[IDX_SKUID]) ? null : new Long(values[IDX_SKUID]));
        }
        return id;
    }

    public static String getSkuAttrId(String skuAttrIdSnDefect) {
        String ret = "";
        String[] values = skuAttrIdSnDefect.split(DV);
        if (AN < values.length) {
            Object[] params = new Object[AN];
            for (int i = 0; i < AN; i++) {
                params[i] = values[i];
            }
            ret = SkuCategoryProvider.concatSkuAttrId(params);
        } else {
            ret = skuAttrIdSnDefect;
        }
        return ret;
    }

    public static String getSnDefect(String skuAttrIdSnDefect) {
        String ret = null;
        String[] values = skuAttrIdSnDefect.split(DV);
        if (AN < values.length) {
            String skuAttrId = getSkuAttrId(skuAttrIdSnDefect);
            String snDefect = skuAttrIdSnDefect.substring(skuAttrId.length() + 1);
            ret = snDefect;
        }
        return ret;
    }

    public static void main(String[] args) {
        System.out.println(SkuCategoryProvider.concatSkuAttrId(1, 2));
        String skuAttrIdSnDefect = SkuCategoryProvider.concatSkuAttrId(1, 2, 3, 4);
        String[] values = skuAttrIdSnDefect.split(DV);
        String skuAttrId = "";
        if (values.length > 2) {
            skuAttrId = SkuCategoryProvider.concatSkuAttrId(values[0], values[1]);
            System.out.println("1");
        } else {
            skuAttrId = skuAttrIdSnDefect;
            System.out.println("2");
        }
        System.out.println(skuAttrId);
    }

}
