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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;

/**
 * @author lichuan
 *
 */
@Service("putawayLocationRecommendFactory")
@Transactional
public class PutawayLocationRecommendFactoryImpl implements PutawayLocationRecommendFactory {
    protected static final Logger log = LoggerFactory.getLogger(PutawayLocationRecommendFactoryImpl.class);

    // 整托上架推荐库位
    @Resource(name = "palletPutawayLocationRecommend")
    private PutawayLocationRecommend palletPutawayLocationRecommend;
    // 整箱上架推荐库位
    @Resource(name = "containerPutawayLocationRecommend")
    private PutawayLocationRecommend containerPutawayLocationRecommend;
    // 拆箱上架推荐库位
    @Resource(name = "splitContainerPutawayLocationRecommend")
    private PutawayLocationRecommend splitContainerPutawayLocationRecommend;

    /**
     * @author lichuan
     * @param ppdt
     * @param logId
     * @return
     */
    @Override
    public PutawayLocationRecommend getPutawayLocationRecommend(int ppdt, String logId) {
        switch (ppdt) {
            case WhPutawayPatternDetailType.PALLET_PUTAWAY:
                return palletPutawayLocationRecommend;
            case WhPutawayPatternDetailType.CONTAINER_PUTAWAY:
                return containerPutawayLocationRecommend;
            case WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY:
                return splitContainerPutawayLocationRecommend;
            default:
                log.error("putawayLocationRecommendFactory.getPutawayLocationRecommend throw exception, putawayPatternDetailType is error, ppdt is:[{}], logId is:[{}]", new Object[] {ppdt, logId});
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
    }

}
