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
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternType;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;

/**
 * @author lichuan
 *
 */
@Service("putawayConditionFactory")
@Transactional
public class PutawayConditionFactoryImpl implements PutawayConditionFactory {
    protected static final Logger log = LoggerFactory.getLogger(PutawayConditionFactoryImpl.class);
    // 系统指导上架整托上架
    @Resource(name = "sysGuidePalletPutawayCondition")
    private PutawayCondition sysGuidePalletPutawayCondition;
    // 系统指导上架货箱上架
    @Resource(name = "sysGuideContainerPutawayCondition")
    private PutawayCondition sysGuideContainerPutawayCondition;
    // 系统指导上架拆托、箱上架
    @Resource(name = "sysGuideSplitContainerPutawayCondition")
    private PutawayCondition sysGuideSplitContainerPutawayCondition;
    // 系统建议上架整托上架
    @Resource(name = "sysSuggetPalletPutawayCondition")
    private PutawayCondition sysSuggetPalletPutawayCondition;
    // 人为指定上架整托上架
    @Resource(name = "manMadePalletPutawayCondition")
    private PutawayCondition manMadePalletPutawayCondition;

    /**
     * @author lichuan
     * @param ppt
     * @param ppdt
     * @param lrt
     * @return
     */
    @Override
    public PutawayCondition getPutawayCondition(int ppt, int ppdt, String logId) {
        if (WhPutawayPatternType.SYS_GUIDE_PUTAWAY == ppt) {
            switch (ppdt) {
                case WhPutawayPatternDetailType.PALLET_PUTAWAY:
                    return sysGuidePalletPutawayCondition;
                case WhPutawayPatternDetailType.CONTAINER_PUTAWAY:
                    return sysGuideContainerPutawayCondition;
                case WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY:
                    return sysGuideSplitContainerPutawayCondition;
                default:
                    log.error("putawayConditionFactory.getPutawayCondition throw exception, putawayPatternDetailType is error, ppt is:[{}], ppdt is:[{}], logId is:[{}]", new Object[] {ppt, ppdt, logId});
                    throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
        } else if (WhPutawayPatternType.SYS_SUGGEST_PUTAWAY == ppt) {
            switch (ppdt) {
                case WhPutawayPatternDetailType.PALLET_PUTAWAY:
                    return sysSuggetPalletPutawayCondition;
                case WhPutawayPatternDetailType.CONTAINER_PUTAWAY:

                case WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY:

                default:
                    log.error("putawayConditionFactory.getPutawayCondition throw exception, putawayPatternDetailType is error, ppt is:[{}], ppdt is:[{}], logId is:[{}]", new Object[] {ppt, ppdt, logId});
                    throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }

        } else if (WhPutawayPatternType.MAN_MADE_PUTAWAY == ppt) {
            switch (ppdt) {
                case WhPutawayPatternDetailType.PALLET_PUTAWAY:
                    return manMadePalletPutawayCondition;
                case WhPutawayPatternDetailType.CONTAINER_PUTAWAY:

                case WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY:

                default:
                    log.error("putawayConditionFactory.getPutawayCondition throw exception, putawayPatternDetailType is error, ppt is:[{}], ppdt is:[{}], logId is:[{}]", new Object[] {ppt, ppdt, logId});
                    throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
        } else {

        }
        return null;
    }

}
