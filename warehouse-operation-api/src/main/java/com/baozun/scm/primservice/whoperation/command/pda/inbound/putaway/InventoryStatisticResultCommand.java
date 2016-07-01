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
public class InventoryStatisticResultCommand extends BaseCommand {

    private static final long serialVersionUID = 6499364435787143573L;

    /** 上架模式 */
    private int putawayPatternType;
    /** 上架类型 */
    private int putawayPatternDetailType;
    /** 是否有外部容器 */
    private boolean isHasOuterContainer;
    /** 是否已推荐库位 */
    private boolean isRecommendLocation;
    /** 内部容器id */
    private Long insideContainerId;
    /** 内部容器号 */
    private String insideContainerCode;
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
    /** 所有sku种类 */
    Set<Long> skuIds = new HashSet<Long>();
    /** sku总件数 */
    Long skuQty = 0L;
    /** 所有唯一sku */
    Set<String> skuAttrIds = new HashSet<String>();
    /** 所有店铺 */
    Set<Long> storeIds = new HashSet<Long>();
    /** 所有推荐库位 */
    Set<Long> locationIds = new HashSet<Long>();
    /** 内部容器所有sku种类 */
    Map<Long, Set<Long>> insideContainerSkuIds = new HashMap<Long, Set<Long>>();
    /** 内部容器所有sku总件数 */
    Map<Long, Long> insideContainerSkuQty = new HashMap<Long, Long>();
    /** 内部容器单个sku总件数 */
    Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = new HashMap<Long, Map<Long, Long>>();
    /** 内部容器唯一sku种类 */
    Map<Long, Set<String>> insideContainerSkuAttrIds = new HashMap<Long, Set<String>>();
    /** 内部容器唯一sku总件数 */
    Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = new HashMap<Long, Map<String, Long>>();
    /** 内部容器唯一sku对应所有残次条码 */
    Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsDefect = new HashMap<Long, Map<String, Set<String>>>();
    /** 内部容器所有店铺 */
    Map<Long, Set<Long>> insideContainerStoreIds = new HashMap<Long, Set<Long>>();


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

    public boolean isHasOuterContainer() {
        return isHasOuterContainer;
    }

    public void setHasOuterContainer(boolean isHasOuterContainer) {
        this.isHasOuterContainer = isHasOuterContainer;
    }

    public boolean isRecommendLocation() {
        return isRecommendLocation;
    }

    public void setRecommendLocation(boolean isRecommendLocation) {
        this.isRecommendLocation = isRecommendLocation;
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

    public Set<Long> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(Set<Long> skuIds) {
        this.skuIds = skuIds;
    }

    public Long getSkuQty() {
        return skuQty;
    }

    public void setSkuQty(Long skuQty) {
        this.skuQty = skuQty;
    }

    public Set<String> getSkuAttrIds() {
        return skuAttrIds;
    }

    public void setSkuAttrIds(Set<String> skuAttrIds) {
        this.skuAttrIds = skuAttrIds;
    }

    public Set<Long> getStoreIds() {
        return storeIds;
    }

    public void setStoreIds(Set<Long> storeIds) {
        this.storeIds = storeIds;
    }

    public Set<Long> getLocationIds() {
        return locationIds;
    }

    public void setLocationIds(Set<Long> locationIds) {
        this.locationIds = locationIds;
    }

    public Map<Long, Set<Long>> getInsideContainerSkuIds() {
        return insideContainerSkuIds;
    }

    public void setInsideContainerSkuIds(Map<Long, Set<Long>> insideContainerSkuIds) {
        this.insideContainerSkuIds = insideContainerSkuIds;
    }

    public Map<Long, Long> getInsideContainerSkuQty() {
        return insideContainerSkuQty;
    }

    public void setInsideContainerSkuQty(Map<Long, Long> insideContainerSkuQty) {
        this.insideContainerSkuQty = insideContainerSkuQty;
    }

    public Map<Long, Map<Long, Long>> getInsideContainerSkuIdsQty() {
        return insideContainerSkuIdsQty;
    }

    public void setInsideContainerSkuIdsQty(Map<Long, Map<Long, Long>> insideContainerSkuIdsQty) {
        this.insideContainerSkuIdsQty = insideContainerSkuIdsQty;
    }

    public Map<Long, Set<String>> getInsideContainerSkuAttrIds() {
        return insideContainerSkuAttrIds;
    }

    public void setInsideContainerSkuAttrIds(Map<Long, Set<String>> insideContainerSkuAttrIds) {
        this.insideContainerSkuAttrIds = insideContainerSkuAttrIds;
    }

    public Map<Long, Map<String, Long>> getInsideContainerSkuAttrIdsQty() {
        return insideContainerSkuAttrIdsQty;
    }

    public void setInsideContainerSkuAttrIdsQty(Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty) {
        this.insideContainerSkuAttrIdsQty = insideContainerSkuAttrIdsQty;
    }

    public Map<Long, Map<String, Set<String>>> getInsideContainerSkuAttrIdsDefect() {
        return insideContainerSkuAttrIdsDefect;
    }

    public void setInsideContainerSkuAttrIdsDefect(Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsDefect) {
        this.insideContainerSkuAttrIdsDefect = insideContainerSkuAttrIdsDefect;
    }

    public Map<Long, Set<Long>> getInsideContainerStoreIds() {
        return insideContainerStoreIds;
    }

    public void setInsideContainerStoreIds(Map<Long, Set<Long>> insideContainerStoreIds) {
        this.insideContainerStoreIds = insideContainerStoreIds;
    }



}
