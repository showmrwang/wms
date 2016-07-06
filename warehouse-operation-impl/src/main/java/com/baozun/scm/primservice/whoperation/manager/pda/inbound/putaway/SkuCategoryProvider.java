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

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;

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
        String invType = (null == invCmd.getInvType() ? PH : invCmd.getInvType());
        String invStatus = (null == invCmd.getInvStatus() ? PH : invCmd.getInvStatus() + "");
        // String batchNumber = (null == invCmd.getBatchNumber() ? PH : invCmd.getBatchNumber());
        String mfgDate = (null == invCmd.getMfgDate() ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getMfgDate()));
        String expDate = (null == invCmd.getExpDate() ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getExpDate()));
        // String countryOfOrigin = (null == invCmd.getCountryOfOrigin() ? PH :
        // invCmd.getCountryOfOrigin());
        String invAttr1 = (null == invCmd.getInvAttr1() ? PH : invCmd.getInvAttr1());
        String invAttr2 = (null == invCmd.getInvAttr2() ? PH : invCmd.getInvAttr2());
        String invAttr3 = (null == invCmd.getInvAttr3() ? PH : invCmd.getInvAttr3());
        String invAttr4 = (null == invCmd.getInvAttr4() ? PH : invCmd.getInvAttr4());
        String invAttr5 = (null == invCmd.getInvAttr5() ? PH : invCmd.getInvAttr5());
        // ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + mfgDate + DV +
        // expDate + DV + countryOfOrigin + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV +
        // invAttr4 + DV + invAttr5;
        ret = skuId + DV + invType + DV + invStatus + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        return ret;
    }

    public static String concatSkuAttrId(Object... argArray) {
        String ret = "";
        int i = 0;
        for (Object obj : argArray) {
            if (0 == i) {
                if (null == obj) {
                    ret += PH;
                } else {
                    ret += obj.toString();
                }
            } else {
                if (null == obj) {
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
        if (null == values || 0 == values.length) {
            id = null;
        } else {
            id = (null == values[IDX_SKUID] ? null : new Long(values[IDX_SKUID]));
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
            String snDefect = skuAttrIdSnDefect.substring(skuAttrId.length());
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
