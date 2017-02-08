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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lichuan
 *
 */
public final class TipSkuDetailProvider {

    public static boolean isTipSkuDetail(Map<Long, Set<String>> locSkuAttrIds, String skuId) {
        boolean ret = false;
        if (null != locSkuAttrIds && 1 < locSkuAttrIds.size()) {
            Set<Long> skuLocs = new HashSet<Long>();
            for (Long lId : locSkuAttrIds.keySet()) {
                Set<String> skuAttrIds = locSkuAttrIds.get(lId);
                if (null != skuAttrIds) {
                    for (String saId : skuAttrIds) {
                        if (skuId.equals(saId)) {
                            skuLocs.add(lId);
                            break;
                        }
                    }
                }
            }
            if (1 < skuLocs.size()) {
                ret = true;
            }
        } else {
            ret = false;
        }
        return ret;
    }

    public static boolean isTipSkuInvType(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVTYPE];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuInvType(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVTYPE];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }

    public static boolean isTipSkuInvStatus(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVSTATUS];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuInvStatus(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVSTATUS];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }
    
    public static boolean isTipSkuBatchNumber(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_BATCHNUMBER];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuBatchNumber(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_BATCHNUMBER];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }
    
    public static boolean isTipSkuCountryOfOrigin(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_COUNTRYOFORIGIN];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuCountryOfOrigin(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_COUNTRYOFORIGIN];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }

    public static boolean isTipSkuMfgDate(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_MFGDATE];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuMfgDate(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_MFGDATE];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }

    public static boolean isTipSkuExpDate(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_EXPDATE];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuExpDate(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_EXPDATE];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }

    public static boolean isTipSkuInvAttr1(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVATTR1];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuInvAttr1(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVATTR1];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }

    public static boolean isTipSkuInvAttr2(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVATTR2];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuInvAttr2(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVATTR2];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }

    public static boolean isTipSkuInvAttr3(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVATTR3];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuInvAttr3(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVATTR3];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }

    public static boolean isTipSkuInvAttr4(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVATTR4];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuInvAttr4(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVATTR4];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }

    public static boolean isTipSkuInvAttr5(String skuAttrId) {
        boolean ret = false;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVATTR5];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = false;
        } else {
            ret = true;
        }
        return ret;
    }

    public static String getSkuInvAttr5(String skuAttrId) {
        String ret = null;
        String[] values = skuAttrId.split(SkuCategoryProvider.DV);
        String v = values[SkuCategoryProvider.IDX_INVATTR5];
        if (SkuCategoryProvider.PH.equals(v)) {
            ret = null;
        } else {
            ret = v;
        }
        return ret;
    }

    public static boolean isTipSkuSn(String skuAttrId) {
        boolean ret = false;
        String saId = SkuCategoryProvider.getSkuAttrId(skuAttrId);
        if (skuAttrId.equals(saId)) {
            ret = false;
        } else {
            String[] values = skuAttrId.split(SkuCategoryProvider.DV);
            String v = values[SkuCategoryProvider.IDX_SKUSN];
            if (SkuCategoryProvider.PH.equals(v)) {
                ret = false;
            } else {
                ret = true;
            }
        }
        return ret;
    }

    public static String getSkuSn(String skuAttrId) {
        String ret = null;
        String saId = SkuCategoryProvider.getSkuAttrId(skuAttrId);
        if (skuAttrId.equals(saId)) {
            ret = null;
        } else {
            String[] values = skuAttrId.split(SkuCategoryProvider.DV);
            String v = values[SkuCategoryProvider.IDX_SKUSN];
            if (SkuCategoryProvider.PH.equals(v)) {
                ret = null;
            } else {
                ret = v;
            }
        }
        return ret;
    }

    public static boolean isTipSkuDefect(String skuAttrId) {
        boolean ret = false;
        String saId = SkuCategoryProvider.getSkuAttrId(skuAttrId);
        if (skuAttrId.equals(saId)) {
            ret = false;
        } else {
            String[] values = skuAttrId.split(SkuCategoryProvider.DV);
            String v = values[SkuCategoryProvider.IDX_SKUDEFECT];
            if (SkuCategoryProvider.PH.equals(v)) {
                ret = false;
            } else {
                ret = true;
            }
        }
        return ret;
    }

    public static String getSkuDefect(String skuAttrId) {
        String ret = null;
        String saId = SkuCategoryProvider.getSkuAttrId(skuAttrId);
        if (skuAttrId.equals(saId)) {
            ret = null;
        } else {
            String[] values = skuAttrId.split(SkuCategoryProvider.DV);
            String v = values[SkuCategoryProvider.IDX_SKUDEFECT];
            if (SkuCategoryProvider.PH.equals(v)) {
                ret = null;
            } else {
                ret = v;
            }
        }
        return ret;
    }

}
