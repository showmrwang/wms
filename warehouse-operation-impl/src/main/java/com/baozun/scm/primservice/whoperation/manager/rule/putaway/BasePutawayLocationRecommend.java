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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.InvAttrMgmtType;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
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
    
    public void invAttrMgmtAspect(AttrParams attrParams, WhSkuInventoryCommand invCmd) {
        String invAttrMgmt = attrParams.getInvAttrMgmt();
        attrParams.setSkuId(invCmd.getSkuId());
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

}
