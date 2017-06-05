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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryAllocated;
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
    public static int AN = 12;
    public static int IDX_SKUID = 0;
    public static int IDX_INVTYPE = 1;
    public static int IDX_INVSTATUS = 2;
    public static int IDX_BATCHNUMBER = 3;
    public static int IDX_COUNTRYOFORIGIN = 4;
    public static int IDX_MFGDATE = 5;
    public static int IDX_EXPDATE = 6;
    public static int IDX_INVATTR1 = 7;
    public static int IDX_INVATTR2 = 8;
    public static int IDX_INVATTR3 = 9;
    public static int IDX_INVATTR4 = 10;
    public static int IDX_INVATTR5 = 11;
    public static int IDX_SKUSN = 12;
    public static int IDX_SKUDEFECT = 13;

    public static String getSkuAttrIdByInv(WhSkuInventoryCommand invCmd) {
        String ret = "";
        Long skuId = invCmd.getSkuId();
        String invType = (StringUtils.isEmpty(invCmd.getInvType()) ? PH : invCmd.getInvType());
        String invStatus = (StringUtils.isEmpty(invCmd.getInvStatus()) ? PH : invCmd.getInvStatus() + "");
        String batchNumber = (StringUtils.isEmpty(invCmd.getBatchNumber()) ? PH : invCmd.getBatchNumber());
        String countryOfOrigin = (StringUtils.isEmpty(invCmd.getCountryOfOrigin()) ? PH : invCmd.getCountryOfOrigin());
        String mfgDate = (StringUtils.isEmpty(invCmd.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getMfgDate()));
        String expDate = (StringUtils.isEmpty(invCmd.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getExpDate()));
        String invAttr1 = (StringUtils.isEmpty(invCmd.getInvAttr1()) ? PH : invCmd.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(invCmd.getInvAttr2()) ? PH : invCmd.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(invCmd.getInvAttr3()) ? PH : invCmd.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(invCmd.getInvAttr4()) ? PH : invCmd.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(invCmd.getInvAttr5()) ? PH : invCmd.getInvAttr5());
        // ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + mfgDate + DV +
        // expDate + DV + countryOfOrigin + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV +
        // invAttr4 + DV + invAttr5;
        ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + countryOfOrigin + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        return ret;
    }

    public static String getSkuAttrIdByOperationLine(WhOperationLineCommand opeCmd) {
        String ret = "";
        Long skuId = opeCmd.getSkuId();
        String invType = (StringUtils.isEmpty(opeCmd.getInvType()) ? PH : opeCmd.getInvType());
        String invStatus = (StringUtils.isEmpty(opeCmd.getInvStatus()) ? PH : opeCmd.getInvStatus() + "");
        String batchNumber = (StringUtils.isEmpty(opeCmd.getBatchNumber()) ? PH : opeCmd.getBatchNumber());
        String countryOfOrigin = (StringUtils.isEmpty(opeCmd.getCountryOfOrigin()) ? PH : opeCmd.getCountryOfOrigin());
        String mfgDate = (StringUtils.isEmpty(opeCmd.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(opeCmd.getMfgDate()));
        String expDate = (StringUtils.isEmpty(opeCmd.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(opeCmd.getExpDate()));
        String invAttr1 = (StringUtils.isEmpty(opeCmd.getInvAttr1()) ? PH : opeCmd.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(opeCmd.getInvAttr2()) ? PH : opeCmd.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(opeCmd.getInvAttr3()) ? PH : opeCmd.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(opeCmd.getInvAttr4()) ? PH : opeCmd.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(opeCmd.getInvAttr5()) ? PH : opeCmd.getInvAttr5());
        ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + countryOfOrigin + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        return ret;
    }

    public static String getSkuAttrIdByOperationExecLine(WhOperationExecLine opeCmd) {
        String ret = "";
        Long skuId = opeCmd.getSkuId();
        String invType = (StringUtils.isEmpty(opeCmd.getInvType()) ? PH : opeCmd.getInvType());
        String invStatus = (StringUtils.isEmpty(opeCmd.getInvStatus()) ? PH : opeCmd.getInvStatus() + "");
        String batchNumber = (StringUtils.isEmpty(opeCmd.getBatchNumber()) ? PH : opeCmd.getBatchNumber());
        String countryOfOrigin = (StringUtils.isEmpty(opeCmd.getCountryOfOrigin()) ? PH : opeCmd.getCountryOfOrigin());
        String mfgDate = (StringUtils.isEmpty(opeCmd.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(opeCmd.getMfgDate()));
        String expDate = (StringUtils.isEmpty(opeCmd.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(opeCmd.getExpDate()));
        String invAttr1 = (StringUtils.isEmpty(opeCmd.getInvAttr1()) ? PH : opeCmd.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(opeCmd.getInvAttr2()) ? PH : opeCmd.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(opeCmd.getInvAttr3()) ? PH : opeCmd.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(opeCmd.getInvAttr4()) ? PH : opeCmd.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(opeCmd.getInvAttr5()) ? PH : opeCmd.getInvAttr5());
        ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + countryOfOrigin + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        return ret;
    }

    public static String getSkuAttrIdByWhSkuInvTobefilled(WhSkuInventoryTobefilled skuTobefilled) {
        String ret = "";
        Long skuId = skuTobefilled.getSkuId();
        String invType = (StringUtils.isEmpty(skuTobefilled.getInvType()) ? PH : skuTobefilled.getInvType());
        String invStatus = (StringUtils.isEmpty(skuTobefilled.getInvStatus()) ? PH : skuTobefilled.getInvStatus() + "");
        String batchNumber = (StringUtils.isEmpty(skuTobefilled.getBatchNumber()) ? PH : skuTobefilled.getBatchNumber());
        String countryOfOrigin = (StringUtils.isEmpty(skuTobefilled.getCountryOfOrigin()) ? PH : skuTobefilled.getCountryOfOrigin());
        String mfgDate = (StringUtils.isEmpty(skuTobefilled.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(skuTobefilled.getMfgDate()));
        String expDate = (StringUtils.isEmpty(skuTobefilled.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(skuTobefilled.getExpDate()));
        String invAttr1 = (StringUtils.isEmpty(skuTobefilled.getInvAttr1()) ? PH : skuTobefilled.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(skuTobefilled.getInvAttr2()) ? PH : skuTobefilled.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(skuTobefilled.getInvAttr3()) ? PH : skuTobefilled.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(skuTobefilled.getInvAttr4()) ? PH : skuTobefilled.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(skuTobefilled.getInvAttr5()) ? PH : skuTobefilled.getInvAttr5());
        ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + countryOfOrigin + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        // ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + mfgDate + DV +
        // expDate + DV + countryOfOrigin + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV +
        // invAttr4 + DV + invAttr5;
        return ret;
    }


    public static String getSkuAttrIdByWhWorkLineCommand(WhWorkLineCommand whWorkLineCmd) {
        String ret = "";
        Long skuId = whWorkLineCmd.getSkuId();
        // String skuCode = whWorkLineCmd.getSkuCode();
        String invType = (StringUtils.isEmpty(whWorkLineCmd.getInvType()) ? PH : whWorkLineCmd.getInvType());
        String invStatus = (StringUtils.isEmpty(whWorkLineCmd.getInvStatus()) ? PH : whWorkLineCmd.getInvStatus() + "");
        String batchNumber = (StringUtils.isEmpty(whWorkLineCmd.getBatchNumber()) ? PH : whWorkLineCmd.getBatchNumber());
        String countryOfOrigin = (StringUtils.isEmpty(whWorkLineCmd.getCountryOfOrigin()) ? PH : whWorkLineCmd.getCountryOfOrigin());
        String mfgDate = (StringUtils.isEmpty(whWorkLineCmd.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(whWorkLineCmd.getMfgDate()));
        String expDate = (StringUtils.isEmpty(whWorkLineCmd.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(whWorkLineCmd.getExpDate()));
        String invAttr1 = (StringUtils.isEmpty(whWorkLineCmd.getInvAttr1()) ? PH : whWorkLineCmd.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(whWorkLineCmd.getInvAttr2()) ? PH : whWorkLineCmd.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(whWorkLineCmd.getInvAttr3()) ? PH : whWorkLineCmd.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(whWorkLineCmd.getInvAttr4()) ? PH : whWorkLineCmd.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(whWorkLineCmd.getInvAttr5()) ? PH : whWorkLineCmd.getInvAttr5());
        ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + countryOfOrigin + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        // ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + mfgDate + DV +
        // expDate + DV + countryOfOrigin + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV +
        // invAttr4 + DV + invAttr5;
        return ret;
    }


    public static String getSkuAttrIdByInv(WhSkuInventoryAllocatedCommand invCmd) {
        String ret = "";
        Long skuId = invCmd.getSkuId();
        String invType = (StringUtils.isEmpty(invCmd.getInvType()) ? PH : invCmd.getInvType());
        String invStatus = (StringUtils.isEmpty(invCmd.getInvStatus()) ? PH : invCmd.getInvStatus() + "");
        String batchNumber = (StringUtils.isEmpty(invCmd.getBatchNumber()) ? PH : invCmd.getBatchNumber());
        String countryOfOrigin = (StringUtils.isEmpty(invCmd.getCountryOfOrigin()) ? PH : invCmd.getCountryOfOrigin());
        String mfgDate = (StringUtils.isEmpty(invCmd.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getMfgDate()));
        String expDate = (StringUtils.isEmpty(invCmd.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getExpDate()));
        String invAttr1 = (StringUtils.isEmpty(invCmd.getInvAttr1()) ? PH : invCmd.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(invCmd.getInvAttr2()) ? PH : invCmd.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(invCmd.getInvAttr3()) ? PH : invCmd.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(invCmd.getInvAttr4()) ? PH : invCmd.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(invCmd.getInvAttr5()) ? PH : invCmd.getInvAttr5());
        // ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + mfgDate + DV +
        // expDate + DV + countryOfOrigin + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV +
        // invAttr4 + DV + invAttr5;
        ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + countryOfOrigin + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        return ret;
    }

    public static String getSkuAttrIdByInv(WhSkuInventoryAllocated invCmd) {
        String ret = "";
        Long skuId = invCmd.getSkuId();
        String invType = (StringUtils.isEmpty(invCmd.getInvType()) ? PH : invCmd.getInvType());
        String invStatus = (StringUtils.isEmpty(invCmd.getInvStatus()) ? PH : invCmd.getInvStatus() + "");
        String batchNumber = (StringUtils.isEmpty(invCmd.getBatchNumber()) ? PH : invCmd.getBatchNumber());
        String countryOfOrigin = (StringUtils.isEmpty(invCmd.getCountryOfOrigin()) ? PH : invCmd.getCountryOfOrigin());
        String mfgDate = (StringUtils.isEmpty(invCmd.getMfgDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getMfgDate()));
        String expDate = (StringUtils.isEmpty(invCmd.getExpDate()) ? PH : new SimpleDateFormat("yyyyMMddHHmmss").format(invCmd.getExpDate()));
        String invAttr1 = (StringUtils.isEmpty(invCmd.getInvAttr1()) ? PH : invCmd.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(invCmd.getInvAttr2()) ? PH : invCmd.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(invCmd.getInvAttr3()) ? PH : invCmd.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(invCmd.getInvAttr4()) ? PH : invCmd.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(invCmd.getInvAttr5()) ? PH : invCmd.getInvAttr5());
        // ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + mfgDate + DV +
        // expDate + DV + countryOfOrigin + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV +
        // invAttr4 + DV + invAttr5;
        ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + countryOfOrigin + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        return ret;
    }

    public static String getSkuAttrIdBySkuCmd(WhSkuCommand skuCmd) {
        String ret = "";
        Long skuId = skuCmd.getId();
        String invType = (StringUtils.isEmpty(skuCmd.getInvType()) ? PH : skuCmd.getInvType());
        String invStatus = (StringUtils.isEmpty(skuCmd.getInvStatus()) ? PH : skuCmd.getInvStatus() + "");
        String batchNumber = (StringUtils.isEmpty(skuCmd.getInvBatchNumber()) ? PH : skuCmd.getInvBatchNumber());
        String countryOfOrigin = (StringUtils.isEmpty(skuCmd.getInvCountryOfOrigin()) ? PH : skuCmd.getInvCountryOfOrigin());
        String mfgDate = (StringUtils.isEmpty(skuCmd.getInvMfgDate()) ? PH : skuCmd.getInvMfgDate());
        String expDate = (StringUtils.isEmpty(skuCmd.getInvExpDate()) ? PH : skuCmd.getInvExpDate());
        String invAttr1 = (StringUtils.isEmpty(skuCmd.getInvAttr1()) ? PH : skuCmd.getInvAttr1());
        String invAttr2 = (StringUtils.isEmpty(skuCmd.getInvAttr2()) ? PH : skuCmd.getInvAttr2());
        String invAttr3 = (StringUtils.isEmpty(skuCmd.getInvAttr3()) ? PH : skuCmd.getInvAttr3());
        String invAttr4 = (StringUtils.isEmpty(skuCmd.getInvAttr4()) ? PH : skuCmd.getInvAttr4());
        String invAttr5 = (StringUtils.isEmpty(skuCmd.getInvAttr5()) ? PH : skuCmd.getInvAttr5());
        ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + countryOfOrigin + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
        return ret;
    }

    public static String getSkuAttrIdByCheck(WhCheckingLineCommand whCheckingLineCommand) {
        String ret = "";
        // Long skuId = whCheckingLineCommand.getSkuId();
        String skuCode = whCheckingLineCommand.getSkuCode();
        String invType = (StringUtils.isEmpty(whCheckingLineCommand.getInvTypeStr()) ? PH : whCheckingLineCommand.getInvTypeStr());
        String invStatus = (StringUtils.isEmpty(whCheckingLineCommand.getInvStatusStr()) ? PH : whCheckingLineCommand.getInvStatusStr() + "");
        String batchNumber = (StringUtils.isEmpty(whCheckingLineCommand.getBatchNumber()) ? PH : whCheckingLineCommand.getBatchNumber());
        String countryOfOrigin = (StringUtils.isEmpty(whCheckingLineCommand.getCountryOfOrigin()) ? PH : whCheckingLineCommand.getCountryOfOrigin());
        String mfgDate = (StringUtils.isEmpty(whCheckingLineCommand.getMfgDate()) ? PH : new SimpleDateFormat("yyyy-MM-dd").format(whCheckingLineCommand.getMfgDate()));
        String expDate = (StringUtils.isEmpty(whCheckingLineCommand.getExpDate()) ? PH : new SimpleDateFormat("yyyy-MM-dd").format(whCheckingLineCommand.getExpDate()));
        String invAttr1 = (StringUtils.isEmpty(whCheckingLineCommand.getInvAttr1Str()) ? PH : whCheckingLineCommand.getInvAttr1Str());
        String invAttr2 = (StringUtils.isEmpty(whCheckingLineCommand.getInvAttr2Str()) ? PH : whCheckingLineCommand.getInvAttr2Str());
        String invAttr3 = (StringUtils.isEmpty(whCheckingLineCommand.getInvAttr3Str()) ? PH : whCheckingLineCommand.getInvAttr3Str());
        String invAttr4 = (StringUtils.isEmpty(whCheckingLineCommand.getInvAttr4Str()) ? PH : whCheckingLineCommand.getInvAttr4Str());
        String invAttr5 = (StringUtils.isEmpty(whCheckingLineCommand.getInvAttr5Str()) ? PH : whCheckingLineCommand.getInvAttr5Str());
        // ret = skuId + DV + invType + DV + invStatus + DV + batchNumber + DV + mfgDate + DV +
        // expDate + DV + countryOfOrigin + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV +
        // invAttr4 + DV + invAttr5;
        ret = skuCode + DV + invType + DV + invStatus + DV + batchNumber + DV + countryOfOrigin + DV + mfgDate + DV + expDate + DV + invAttr1 + DV + invAttr2 + DV + invAttr3 + DV + invAttr4 + DV + invAttr5;
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
