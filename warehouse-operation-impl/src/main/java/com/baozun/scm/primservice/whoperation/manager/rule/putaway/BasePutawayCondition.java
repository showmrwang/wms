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
package com.baozun.scm.primservice.whoperation.manager.rule.putaway;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import com.baozun.scm.primservice.whoperation.constant.InvAttrMgmtType;
import com.baozun.scm.primservice.whoperation.constant.WhLocationRecommendType;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

/**
 * @author lichuan
 *
 */
public abstract class BasePutawayCondition extends BaseManagerImpl implements PutawayCondition {

    /**
     * @author lichuan
     * @param attrParams
     * @return
     */
    @Override
    public String getCondition(AttrParams attrParams) {
        StringBuilder sql = new StringBuilder("");
        if (null == attrParams) return sql.toString();
        String lrt = attrParams.getLrt();
        switch (lrt) {
            case WhLocationRecommendType.EMPTY_LOCATION:
                sql.append(" ").append(" loc.mix_stacking_number >= ").append(attrParams.getSkuCategory());
                sql.append(" ").append(" and loc.max_chaos_sku >= ").append(attrParams.getSkuAttrCategory());
                return sql.toString();
            case WhLocationRecommendType.STATIC_LOCATION:
                return sql.toString();
            case WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS:
                if (null != attrParams.getIsMixStacking()) {
                    sql.append(" ").append("loc.is_mix_stacking = ").append((true == attrParams.getIsMixStacking() ? "1" : "0"));
                }
                sql.append(" exists (select 1 from t_wh_sku_inventory inv where inv.location_id = loc.id and inv.ou_id = ").append(attrParams.getOuId());
                invAttrMgmtAspect(attrParams, sql);
                sql.append(" ").append("group by inv.location_id,inv.ou_id,inv.sku_id having(inv.sku_id) = 1");
                sql.append(")");
                return sql.toString();
            case WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS:
                /*if (null != attrParams.getIsMixStacking()) {
                    sql.append(" ").append("loc.is_mix_stacking = ").append((true == attrParams.getIsMixStacking() ? "1" : "0"));
                }*/
                sql.append(" exists (select 1 from t_wh_sku_inventory inv where inv.location_id = loc.id and inv.ou_id = ").append(attrParams.getOuId());
                sql.append(" ").append("group by inv.location_id,inv.ou_id,inv.sku_id having(inv.sku_id) = 1");
                sql.append(")");
                return sql.toString();
            case WhLocationRecommendType.ONE_LOCATION_ONLY:
                return sql.toString();
            default:
                sql = new StringBuilder("");
        }
        return sql.toString();
    }
    
    protected void invAttrMgmtAspect(AttrParams attrParams, StringBuilder sql) {
        String invAttrMgmt = attrParams.getInvAttrMgmt();
        sql.append(" ").append("and inv.sku_id = ").append(attrParams.getSkuId().toString());
        if (!StringUtils.isEmpty(invAttrMgmt)) {
            String[] invAttrs = invAttrMgmt.split(",");
            if (null != invAttrs && 0 < invAttrs.length) {
                for (String attr : invAttrs) {
                    switch (attr) {
                        case InvAttrMgmtType.INV_TYPE:
                            if (StringUtils.isEmpty(attrParams.getInvType())) {
                                sql.append(" ").append(" and (inv.inv_type is null or inv.inv_type = '' )");
                            } else {
                                sql.append(" ").append(" and inv.inv_type = ").append("'").append(attrParams.getInvType()).append("'");
                            }
                            break;
                        case InvAttrMgmtType.INV_STATUS:
                            if (null == attrParams.getInvStatus()) {
                                sql.append(" ").append(" and (inv.inv_status is null or inv.inv_status = '') ");
                            } else {
                                sql.append(" ").append(" and inv.inv_status = ").append(attrParams.getInvStatus());
                            }
                            break;
                        case InvAttrMgmtType.BATCH_NUMBER:
                            if (StringUtils.isEmpty(attrParams.getBatchNumber())) {
                                sql.append(" ").append(" and (inv.batch_number is null or inv.batch_number = '')");
                            } else {
                                sql.append(" ").append(" and inv.batch_number = ").append("'").append(attrParams.getBatchNumber()).append("'");
                            }
                            break;
                        case InvAttrMgmtType.MFG_DATE:
                            if (null == attrParams.getMfgDate()) {
                                sql.append(" ").append(" and (inv.mfg_date is null or inv.mfg_date = '')");
                            } else {
                                sql.append(" ").append(" and inv.mfg_date = ").append("'").append(null == attrParams.getMfgDate() ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(attrParams.getMfgDate())).append("'");
                            }
                            break;
                        case InvAttrMgmtType.EXP_DATE:
                            if (null == attrParams.getExpDate()) {
                                sql.append(" ").append(" and (inv.exp_date is null or inv.exp_date = '')");
                            } else {
                                sql.append(" ").append(" and inv.exp_date = ").append("'").append(null == attrParams.getExpDate() ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(attrParams.getExpDate())).append("'");
                            }
                            break;
                        case InvAttrMgmtType.COUNTRY_OF_ORIGIN:
                            if (StringUtils.isEmpty(attrParams.getCountryOfOrigin())) {
                                sql.append(" ").append(" and (inv.country_of_origin is null or inv.country_of_origin = '')");
                            } else {
                                sql.append(" ").append(" and inv.country_of_origin = ").append("'").append(attrParams.getCountryOfOrigin()).append("'");
                            }
                            break;
                        case InvAttrMgmtType.INV_ATTR1:
                            if (StringUtils.isEmpty(attrParams.getInvAttr1())) {
                                sql.append(" ").append(" and (inv.inv_attr1 is null or inv.inv_attr1 = '')");
                            } else {
                                sql.append(" ").append(" and inv.inv_attr1 = ").append("'").append(attrParams.getInvAttr1()).append("'");
                            }
                            break;
                        case InvAttrMgmtType.INV_ATTR2:
                            if (StringUtils.isEmpty(attrParams.getInvAttr1())) {
                                sql.append(" ").append(" and (inv.inv_attr2 is null or inv.inv_attr2 = '')");
                            } else {
                                sql.append(" ").append(" and inv.inv_attr2 = ").append("'").append(attrParams.getInvAttr2()).append("'");
                            }
                            break;
                        case InvAttrMgmtType.INV_ATTR3:
                            if (StringUtils.isEmpty(attrParams.getInvAttr1())) {
                                sql.append(" ").append(" and (inv.inv_attr3 is null or inv.inv_attr3 = '')");
                            } else {
                                sql.append(" ").append(" and inv.inv_attr3 = ").append("'").append(attrParams.getInvAttr3()).append("'");
                            }
                            break;
                        case InvAttrMgmtType.INV_ATTR4:
                            if (StringUtils.isEmpty(attrParams.getInvAttr1())) {
                                sql.append(" ").append(" and (inv.inv_attr4 is null or inv.inv_attr4 = '')");
                            } else {
                                sql.append(" ").append(" and inv.inv_attr4 = ").append("'").append(attrParams.getInvAttr4()).append("'");
                            }
                            break;
                        case InvAttrMgmtType.INV_ATTR5:
                            if (StringUtils.isEmpty(attrParams.getInvAttr1())) {
                                sql.append(" ").append(" and (inv.inv_attr5 is null or inv.inv_attr5 = '')");
                            } else {
                                sql.append(" ").append(" and inv.inv_attr5 = ").append("'").append(attrParams.getInvAttr5()).append("'");
                            }
                            break;
                    }
                }
            }
        }
    }

}
