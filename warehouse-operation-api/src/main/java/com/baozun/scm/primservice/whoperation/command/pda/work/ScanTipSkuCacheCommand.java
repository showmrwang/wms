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
public class ScanTipSkuCacheCommand extends BaseCommand {

    private static final long serialVersionUID = -3767908553974150072L;
    /** 上架类型 */
    private int putawayWay;
    /** 外部容器id */
    private Long outerContainerId;
    /** 外部容器号 */
    private String outerContainerCode;
    /** 內部容器id */
    private Long insideContainerId;
    /** 內部容器号 */
    private String insideContainerCode;
    /** 已复合商品队列 */
    private ArrayDeque<Long> scanSkuIds = new ArrayDeque<Long>();
    /** 已复合唯一商品列表 */
    private ArrayDeque<String> scanSkuAttrIds = new ArrayDeque<String>();
    /** 逐件扫描商品列表 */
    private ArrayDeque<Long> oneByOneScanSkuIds = new ArrayDeque<Long>();

    public int getPutawayWay() {
        return putawayWay;
    }

    public void setPutawayWay(int putawayWay) {
        this.putawayWay = putawayWay;
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

    public ArrayDeque<Long> getScanSkuIds() {
        return scanSkuIds;
    }

    public void setScanSkuIds(ArrayDeque<Long> scanSkuIds) {
        this.scanSkuIds = scanSkuIds;
    }

    public ArrayDeque<String> getScanSkuAttrIds() {
        return scanSkuAttrIds;
    }

    public void setScanSkuAttrIds(ArrayDeque<String> scanSkuAttrIds) {
        this.scanSkuAttrIds = scanSkuAttrIds;
    }

    public ArrayDeque<Long> getOneByOneScanSkuIds() {
        return oneByOneScanSkuIds;
    }

    public void setOneByOneScanSkuIds(ArrayDeque<Long> oneByOneScanSkuIds) {
        this.oneByOneScanSkuIds = oneByOneScanSkuIds;
    }


}
