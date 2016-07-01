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
    private static final String DV = "┊";

    public static String getSkuCategoryByInv(WhSkuInventoryCommand invCmd) {
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

}
