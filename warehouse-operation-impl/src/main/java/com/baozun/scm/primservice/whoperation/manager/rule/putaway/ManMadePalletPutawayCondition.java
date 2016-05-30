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
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

/**
 * @author lichuan
 *
 */
@Service("manMadePalletPutawayCondition")
@Transactional
public class ManMadePalletPutawayCondition extends BaseManagerImpl implements PutawayCondition {
    protected static final Logger log = LoggerFactory.getLogger(ManMadePalletPutawayCondition.class);

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
            case WhLocationRecommendType.STATIC_LOCATION:
            case WhLocationRecommendType.MERGE_LOCATION_SAME_INV_ATTRS:
            case WhLocationRecommendType.MERGE_LOCATION_DIFF_INV_ATTRS:
            case WhLocationRecommendType.ONE_LOCATION_ONLY:
            default:
                sql = new StringBuilder("");
        }
        return sql.toString();
    }

}
