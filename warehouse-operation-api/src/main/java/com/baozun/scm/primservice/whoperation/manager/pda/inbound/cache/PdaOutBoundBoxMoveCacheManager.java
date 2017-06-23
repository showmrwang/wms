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
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache;

import java.util.Map;
import java.util.Set;

import com.baozun.scm.baseservice.task.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;

/**
 * @author feng.hu
 *
 */
public interface PdaOutBoundBoxMoveCacheManager extends BaseManager {
    
    /**
     * pda出库箱整箱移动复核sku并缓存及提示后续操作判断
     * @author feng.hu
     * @param sourceContainerCode
     * @param insideContainerSkuIds
     * @param insideContainerSkuIdsQty
     * @param skuCmd
     * @param scanPattern
     * @param logId
     * @return
     */
    public CheckScanSkuResultCommand sysOutBoundboxContainerFullMoveCacheSkuAndCheck(String sourceContainerCode, Map<String, Set<Long>> insideContainerSkuIds,
                                  Map<String, Map<Long, Long>> insideContainerSkuIdsQty, WhSkuCommand skuCmd, Integer scanPattern, String logId);
    
    
    /**
     * pda出库箱整箱移动复核sku并缓存及提示后续操作判断
     * @author feng.hu
     * @param sourceContainerCode
     * @param insideContainerSkuIds
     * @param insideContainerSkuIdsQty
     * @param skuCmd
     * @param scanPattern
     * @param logId
     * @return
     */
    public CheckScanSkuResultCommand sysOutBoundboxContainerSplitMoveCacheSkuAndCheck(String sourceContainerCode, Map<String, Set<String>> insideContainerSkuAttrIds,
                               Map<String, Long> skuAttrIdsQty,Map<String, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect, WhSkuCommand skuCmd, Integer movePattern, String logId);

}
