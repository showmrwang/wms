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
package com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway;

import java.util.ArrayDeque;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * @author lichuan
 *
 */
public class TipLocationCacheCommand extends BaseCommand {

    private static final long serialVersionUID = 5109899187570668442L;
    /** 上架类型 */
    private int putawayPatternDetailType;
    /** 外部容器id */
    private Long outerContainerId;
    /** 外部容器号 */
    private String outerContainerCode;
    /** 內部容器id */
    private Long insideContainerId;
    /** 內部容器号 */
    private String insideContainerCode;
    /** 提示库位列表 */
    private ArrayDeque<Long> tipLocationIds = new ArrayDeque<Long>();

    public int getPutawayPatternDetailType() {
        return putawayPatternDetailType;
    }

    public void setPutawayPatternDetailType(int putawayPatternDetailType) {
        this.putawayPatternDetailType = putawayPatternDetailType;
    }

    public Long getOuterContainerId() {
        return outerContainerId;
    }

    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }

    public String getOuterContainerCode() {
        return outerContainerCode;
    }

    public void setOuterContainerCode(String outerContainerCode) {
        this.outerContainerCode = outerContainerCode;
    }

    public Long getInsideContainerId() {
        return insideContainerId;
    }

    public void setInsideContainerId(Long insideContainerId) {
        this.insideContainerId = insideContainerId;
    }

    public String getInsideContainerCode() {
        return insideContainerCode;
    }

    public void setInsideContainerCode(String insideContainerCode) {
        this.insideContainerCode = insideContainerCode;
    }

    public ArrayDeque<Long> getTipLocationIds() {
        return tipLocationIds;
    }

    public void setTipLocationIds(ArrayDeque<Long> tipLocationIds) {
        this.tipLocationIds = tipLocationIds;
    }


}
