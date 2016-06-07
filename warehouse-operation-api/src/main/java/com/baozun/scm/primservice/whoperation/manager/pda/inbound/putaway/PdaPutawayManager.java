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

import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

/**
 * @author lichuan
 *
 */
public interface PdaPutawayManager extends BaseManager {
    
    /**
     * 系统指导上架扫托盘号
     * @author lichuan
     * @param containerCode
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    LocationCommand sysGuideScanPallet(String containerCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId);
    
    /**
     * 系统指导执行上架
     * @author lichuan
     * @param containerCode
     * @param locationCode
     * @param funcId
     * @param asnId
     * @param putawayPatternDetailType
     * @param caseMode
     * @param ouId
     * @param userId
     * @param logId
     */
    void sysGuidePutaway(String containerCode, String locationCode, Long funcId, Integer putawayPatternDetailType, Integer caseMode, Long ouId, Long userId, String logId);
    
    /**
     * 根据外部容器查询Caselevel箱数量
     * @author lichuan
     * @param containerode
     * @param ouId
     * @param logId
     * @return
     */
    int findCaselevelCartonNumsByOuterContainerCode(String containerCode, Long ouId, String logId);

}
