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
package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * @author
 *
 */
public class LocationTipCacheCommand extends BaseCommand {

    private static final long serialVersionUID = 5109899187570668442L;

    /** 拣货类型 */
    private int pickingType;
    /** 外部容器id */
    private Long outerContainerId;
    /** 外部容器号 */
    private String outerContainerCode;
    /** 内部容器id */
    private Long insideContainerId;
    /** 内部容器号 */
    private String insideContainerCode;
    
    private Map<Long,ArrayDeque<Long>> tipOuterInsideContainerIds = new HashMap<Long,ArrayDeque<Long>>();   //内部容器存在托盘
    
    private Map<Long,ArrayDeque<Long>> tipLocInsideContainerIds = new HashMap<Long,ArrayDeque<Long>>();   //内部容器不存在托盘
    /** 提示 内容器号队列 */
//    private ArrayDeque<Long> tipInsideContainerIds = new ArrayDeque<Long>();
    /** 提示外容器号队列 */
    private Map<Long,ArrayDeque<Long>>tipLocOuterContainerIds = new HashMap<Long,ArrayDeque<Long>>();   //库位上对应的托盘
    public int getPickingType() {
        return pickingType;
    }

    public void setPickingType(int pickingType) {
        this.pickingType = pickingType;
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

    public Map<Long, ArrayDeque<Long>> getTipOuterInsideContainerIds() {
        return tipOuterInsideContainerIds;
    }

    public void setTipOuterInsideContainerIds(Map<Long, ArrayDeque<Long>> tipOuterInsideContainerIds) {
        this.tipOuterInsideContainerIds = tipOuterInsideContainerIds;
    }

    public Map<Long, ArrayDeque<Long>> getTipLocInsideContainerIds() {
        return tipLocInsideContainerIds;
    }

    public void setTipLocInsideContainerIds(Map<Long, ArrayDeque<Long>> tipLocInsideContainerIds) {
        this.tipLocInsideContainerIds = tipLocInsideContainerIds;
    }

    public Map<Long, ArrayDeque<Long>> getTipLocOuterContainerIds() {
        return tipLocOuterContainerIds;
    }

    public void setTipLocOuterContainerIds(Map<Long, ArrayDeque<Long>> tipLocOuterContainerIds) {
        this.tipLocOuterContainerIds = tipLocOuterContainerIds;
    }
    
    
}
