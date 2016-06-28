/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
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
    //占位符
    private static final String PH = "┊";
    
    public static String getSkuCategoryByInv(WhSkuInventoryCommand invCmd) {
        String ret = "";
        Long skuId = invCmd.getSkuId();
        String invType = (null == invCmd.getInvType() ? "" : invCmd.getInvType());
        String invStatus = (null == invCmd.getInvStatus() ? "" : invCmd.getInvStatus() + "");
        String batchNumber = (null == invCmd.getBatchNumber() ? "" : invCmd.getBatchNumber());
        String mfgDate = (null == invCmd.getMfgDate() ? "" : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getMfgDate()));
        String expDate = (null == invCmd.getExpDate() ? "" : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getExpDate()));
        String countryOfOrigin = (null == invCmd.getCountryOfOrigin() ? "" : invCmd.getCountryOfOrigin());
        String invAttr1 = (null == invCmd.getInvAttr1() ? "" : invCmd.getInvAttr1());
        String invAttr2 = (null == invCmd.getInvAttr2() ? "" : invCmd.getInvAttr2());
        String invAttr3 = (null == invCmd.getInvAttr3() ? "" : invCmd.getInvAttr3());
        String invAttr4 = (null == invCmd.getInvAttr4() ? "" : invCmd.getInvAttr4());
        String invAttr5 = (null == invCmd.getInvAttr5() ? "" : invCmd.getInvAttr5());
        ret = skuId + PH + invType + PH  + invStatus + PH  + batchNumber + PH  + mfgDate + PH  + expDate + PH  + countryOfOrigin + PH  + invAttr1 + PH  + invAttr2 + PH  + invAttr3 + PH  + invAttr4 + PH  + invAttr5;
        return ret;
    }

}
