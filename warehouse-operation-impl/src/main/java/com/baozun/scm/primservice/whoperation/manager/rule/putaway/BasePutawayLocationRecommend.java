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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.InvAttrMgmtType;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;

/**
 * @author lichuan
 *
 */
public abstract class BasePutawayLocationRecommend extends BaseManagerImpl implements PutawayLocationRecommend {

    /**
     * @author lichuan
     * @param ruleAffer
     * @param ruleList
     * @param caMap
     * @param invList
     * @param uomMap
     * @param logId
     * @return
     */
    @Override
    public List<LocationRecommendResultCommand> recommendLocation(RuleAfferCommand ruleAffer, RuleExportCommand export, Map<Long, ContainerAssist> caMap, List<WhSkuInventoryCommand> invList, Map<String, Map<String, Double>> uomMap,
            String logId) {
        return null;
    }
    
    protected int invSkuCountAspect(List<Long> skuIds) {
        int counts = 0;
        if (null == skuIds || 0 == skuIds.size()) {
            return counts;
        }
        Set<Long> allSkus = new HashSet<Long>();
        for (Long skuId : skuIds) {
            if (null != skuId) {
                allSkus.add(skuId);
            }
        }
        counts = allSkus.size();
        return counts;
    }
    
    protected int invAttrCountAspect(List<WhSkuInventoryCommand> invList) {
        int counts = 0;
        if (null == invList || 0 == invList.size()) {
            return counts;
        }
        Set<String> allSkuAttrs = new HashSet<String>();
        for (WhSkuInventoryCommand inv : invList) {
            WhSkuInventoryCommand iv = inv;
            if (null != iv) {
                allSkuAttrs.add(SkuCategoryProvider.getSkuAttrIdByInv(iv));
            }
        }
        counts = allSkuAttrs.size();
        return counts;
    }
    
    protected void invAttrMgmtAspect(AttrParams attrParams, WhSkuInventoryCommand invCmd) {
        String invAttrMgmt = attrParams.getInvAttrMgmt();
        if (null == attrParams.getIsMixStacking() || false == attrParams.getIsMixStacking()) {
            attrParams.setSkuId(invCmd.getSkuId());
        }
        if (!StringUtils.isEmpty(invAttrMgmt)) {
            String[] invAttrs = invAttrMgmt.split(",");
            if (null != invAttrs && 0 < invAttrs.length) {
                for (String attr : invAttrs) {
                    switch (attr) {
                        case InvAttrMgmtType.INV_TYPE:
                            attrParams.setInvType(invCmd.getInvType());
                            break;
                        case InvAttrMgmtType.INV_STATUS:
                            attrParams.setInvStatus(invCmd.getInvStatus());
                            break;
                        case InvAttrMgmtType.BATCH_NUMBER:
                            attrParams.setBatchNumber(invCmd.getBatchNumber());
                            break;
                        case InvAttrMgmtType.MFG_DATE:
                            attrParams.setMfgDate(invCmd.getMfgDate());
                            break;
                        case InvAttrMgmtType.EXP_DATE:
                            attrParams.setExpDate(invCmd.getExpDate());
                            break;
                        case InvAttrMgmtType.COUNTRY_OF_ORIGIN:
                            attrParams.setCountryOfOrigin(invCmd.getCountryOfOrigin());
                            break;
                        case InvAttrMgmtType.INV_ATTR1:
                            attrParams.setInvAttr1(invCmd.getInvAttr1());
                            break;
                        case InvAttrMgmtType.INV_ATTR2:
                            attrParams.setInvAttr2(invCmd.getInvAttr2());
                            break;
                        case InvAttrMgmtType.INV_ATTR3:
                            attrParams.setInvAttr3(invCmd.getInvAttr3());
                            break;
                        case InvAttrMgmtType.INV_ATTR4:
                            attrParams.setInvAttr4(invCmd.getInvAttr4());
                            break;
                        case InvAttrMgmtType.INV_ATTR5:
                            attrParams.setInvAttr5(invCmd.getInvAttr5());
                            break;
                    }
                }
            }
        }
    }
    
    protected void invAttrMgmtAspect(AttrParams attrParams, StringBuilder sql) {
        String invAttrMgmt = attrParams.getInvAttrMgmt();
        if (null != attrParams.getSkuId()) {
            sql.append(" ").append("and inv.sku_id = ").append(attrParams.getSkuId().toString());
        }
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
