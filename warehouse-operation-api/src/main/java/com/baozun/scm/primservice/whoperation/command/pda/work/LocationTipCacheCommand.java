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
    /** 提示 内容器号队列 */
    private ArrayDeque<Long> tipInsideContainerIds = new ArrayDeque<Long>();
    /** 提示外容器号队列 */
    private ArrayDeque<Long> tipOuterContainerIds = new ArrayDeque<Long>();
    /** 直接放在库位上的sku队列 */
    private ArrayDeque<Long> tipLocSkuIds = new ArrayDeque<Long>();
    /** 缓存库位队列 */
    private ArrayDeque<Long> tipLocationIds = new ArrayDeque<Long>();
    /**有小车有出库箱的情况下:出库箱的队列*/
    private ArrayDeque<String> tipOutBonxBoxIds = new ArrayDeque<String>();
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

    public ArrayDeque<Long> getTipInsideContainerIds() {
        return tipInsideContainerIds;
    }

    public void setTipInsideContainerIds(ArrayDeque<Long> tipInsideContainerIds) {
        this.tipInsideContainerIds = tipInsideContainerIds;
    }

    public ArrayDeque<Long> getTipOuterContainerIds() {
        return tipOuterContainerIds;
    }

    public void setTipOuterContainerIds(ArrayDeque<Long> tipOuterContainerIds) {
        this.tipOuterContainerIds = tipOuterContainerIds;
    }

    public ArrayDeque<Long> getTipLocSkuIds() {
        return tipLocSkuIds;
    }

    public void setTipLocSkuIds(ArrayDeque<Long> tipLocSkuIds) {
        this.tipLocSkuIds = tipLocSkuIds;
    }

    public ArrayDeque<Long> getTipLocationIds() {
        return tipLocationIds;
    }

    public void setTipLocationIds(ArrayDeque<Long> tipLocationIds) {
        this.tipLocationIds = tipLocationIds;
    }

    public ArrayDeque<String> getTipOutBonxBoxIds() {
        return tipOutBonxBoxIds;
    }

    public void setTipOutBonxBoxIds(ArrayDeque<String> tipOutBonxBoxIds) {
        this.tipOutBonxBoxIds = tipOutBonxBoxIds;
    }

   



}
