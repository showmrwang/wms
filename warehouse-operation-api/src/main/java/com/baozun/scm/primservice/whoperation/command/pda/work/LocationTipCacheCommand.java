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
    /** 外部容器id */
    private Long outerContainerId;
    
    /** 内部容器id */
    private Long insideContainerId;
    
    
    private Map<Long,ArrayDeque<Long>> tipOuterInsideContainerIds = new HashMap<Long,ArrayDeque<Long>>();   //内部容器存在托盘
    
    private Map<Long,ArrayDeque<Long>> tipLocInsideContainerIds = new HashMap<Long,ArrayDeque<Long>>();   //内部容器不存在托盘
    /** 提示外容器号队列 */
    private Map<Long,ArrayDeque<Long>>tipLocOuterContainerIds = new HashMap<Long,ArrayDeque<Long>>();   //库位上对应的托盘

    public Long getOuterContainerId() {
        return outerContainerId;
    }

    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }

    public Long getInsideContainerId() {
        return insideContainerId;
    }

    public void setInsideContainerId(Long insideContainerId) {
        this.insideContainerId = insideContainerId;
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
