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
package com.baozun.scm.primservice.whoperation.manager.rule.putaway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.WhLocationRecommendType;

/**
 * @author lichuan
 *
 */
@Service("sysGuidePalletPutawayCondition")
@Transactional
public class SysGuidePalletPutawayCondition extends BasePutawayCondition implements PutawayCondition {
    protected static final Logger log = LoggerFactory.getLogger(SysGuidePalletPutawayCondition.class);

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
                sql.append(" ").append("c.id = inv.outer_container_id");
                sql.append(" ").append("and c.id = ").append(attrParams.getContainerId());
                return sql.toString();
            case WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS:
                if (null != attrParams.getIsMixStacking()) {
                    sql.append(" ").append("loc.is_mix_stacking = ").append((true == attrParams.getIsMixStacking() ? "1" : "0"));
                }
                sql.append(" exists (select 1 from t_wh_sku_inventory inv where inv.location_id = loc.id and inv.ou_id = ").append(attrParams.getOuId());
                invAttrMgmtAspect(attrParams, sql);
                //sql.append(" ").append("group by inv.location_id,inv.ou_id,inv.sku_id having count(inv.sku_id) = 1");
                sql.append(")");
                return sql.toString();
            case WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS:
                sql.append(" ").append("loc.is_mix_stacking = ").append("1");
                sql.append(" ").append("and loc.mix_stacking_number >= ").append(attrParams.getSkuCategory());
                sql.append(" ").append(" and loc.max_chaos_sku >= ").append(attrParams.getSkuAttrCategory());
                sql.append(" and exists (select 1 from t_wh_sku_inventory inv where inv.location_id = loc.id and inv.ou_id = ").append(attrParams.getOuId());
                invAttrMgmtAspect(attrParams, sql);
                //sql.append(" ").append("group by inv.location_id,inv.ou_id,inv.sku_id having count(inv.sku_id) = 1");
                sql.append(")");
                return sql.toString();
            case WhLocationRecommendType.ONE_LOCATION_ONLY:
                return sql.toString();
            default:
                sql = new StringBuilder("");
        }
        return sql.toString();
    }
    

}
