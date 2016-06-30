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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * @author lichuan
 *
 */
public class ContainerStatisticResultCommand extends BaseCommand {

    private static final long serialVersionUID = -9120270662713405423L;

    /** 上架模式 */
    private int putawayPatternType;
    /** 上架类型 */
    private int putawayPatternDetailType;
    /** 外部容器id */
    private Long outerContainerId;
    /** 外部容器号 */
    private String outerContainerCode;
    /** 容器类型 1：外部容器 2：内部容器 */
    private int containerType;
    /** 所有内部容器 */
    Set<Long> insideContainerIds = new HashSet<Long>();
    /** 所有caselevel内部容器 */
    Set<Long> caselevelContainerIds = new HashSet<Long>();
    /** 所有非caselevel内部容器 */
    Set<Long> notcaselevelContainerIds = new HashSet<Long>();
    /** 所有内部容器及容器号 */
    Map<Long, String> insideContainerIdsCode = new HashMap<Long, String>();

    public int getPutawayPatternType() {
        return putawayPatternType;
    }

    public void setPutawayPatternType(int putawayPatternType) {
        this.putawayPatternType = putawayPatternType;
    }

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

    public int getContainerType() {
        return containerType;
    }

    public void setContainerType(int containerType) {
        this.containerType = containerType;
    }

    public Set<Long> getInsideContainerIds() {
        return insideContainerIds;
    }

    public void setInsideContainerIds(Set<Long> insideContainerIds) {
        this.insideContainerIds = insideContainerIds;
    }

    public Set<Long> getCaselevelContainerIds() {
        return caselevelContainerIds;
    }

    public void setCaselevelContainerIds(Set<Long> caselevelContainerIds) {
        this.caselevelContainerIds = caselevelContainerIds;
    }

    public Set<Long> getNotcaselevelContainerIds() {
        return notcaselevelContainerIds;
    }

    public void setNotcaselevelContainerIds(Set<Long> notcaselevelContainerIds) {
        this.notcaselevelContainerIds = notcaselevelContainerIds;
    }

    public Map<Long, String> getInsideContainerIdsCode() {
        return insideContainerIdsCode;
    }

    public void setInsideContainerIdsCode(Map<Long, String> insideContainerIdsCode) {
        this.insideContainerIdsCode = insideContainerIdsCode;
    }


}
