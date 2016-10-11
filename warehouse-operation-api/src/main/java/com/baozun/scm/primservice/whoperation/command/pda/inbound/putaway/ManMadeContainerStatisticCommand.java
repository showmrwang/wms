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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**tangming
 * @author 
 *
 */
public class ManMadeContainerStatisticCommand extends BaseCommand {

    private static final long serialVersionUID = -9120270662713405423L;

    /** 上架类型 */
    private int putawayPatternDetailType;
    /** 外部容器id */
    private Long outerContainerId;
    /** 外部容器号 */
    private String outerContainerCode;
    /**内部容器id*/
    private Long insideContainerId;
    /**内部容器号*/
    private String insideContainerCode;
    /** 容器类型 1：外部容器 2：内部容器 */
    private int containerType;
    /***整托上架:外部容器重量*/
    private double containerWeight;   
    /***整箱上架货箱重量(包括sku)*/
    private double insideContainerWeight;
    /** 所有内部容器 */
    Set<Long> insideContainerIds = new HashSet<Long>();
    /** 所有caselevel内部容器 */
    Set<Long> caselevelContainerIds = new HashSet<Long>();
    /** 所有非caselevel内部容器 */
    Set<Long> notcaselevelContainerIds = new HashSet<Long>();
    /** 所有内部容器及容器号 */
    Map<Long, String> insideContainerIdsCode = new HashMap<Long, String>();
    /** 内部容器对应sku,id集合 */
    Map<Long,Set<Long>> insideContainerIdSkuIds = new HashMap<Long,Set<Long>>();
    /**内部容器重量(容器+sku的重量)*/
    Map<Long, Double> insideContainersWeight = new HashMap<Long, Double>();
    /**内部容器体积*/
    Map<Long, Double> insideContainerVolume = new HashMap<Long, Double>();
    
    Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();   //长度，度量单位转换率
    Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();  //重量，度量单位转换率
    /** 内部容器单个sku总件数 */
    Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = new HashMap<Long, Map<Long, Long>>();


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

    public String getInsideContainerCode() {
        return insideContainerCode;
    }

    public void setInsideContainerCode(String insideContainerCode) {
        this.insideContainerCode = insideContainerCode;
    }

    public Long getInsideContainerId() {
        return insideContainerId;
    }

    public void setInsideContainerId(Long insideContainerId) {
        this.insideContainerId = insideContainerId;
    }

    public Map<Long, Double> getInsideContainerVolume() {
        return insideContainerVolume;
    }

    public void setInsideContainerVolume(Map<Long, Double> insideContainerVolume) {
        this.insideContainerVolume = insideContainerVolume;
    }

    public double getContainerWeight() {
        return containerWeight;
    }

    public void setContainerWeight(double containerWeight) {
        this.containerWeight = containerWeight;
    }

    public double getInsideContainerWeight() {
        return insideContainerWeight;
    }

    public void setInsideContainerWeight(double insideContainerWeight) {
        this.insideContainerWeight = insideContainerWeight;
    }

    public Map<Long, Double> getInsideContainersWeight() {
        return insideContainersWeight;
    }

    public void setInsideContainersWeight(Map<Long, Double> insideContainersWeight) {
        this.insideContainersWeight = insideContainersWeight;
    }

    public Map<String, Double> getLenUomConversionRate() {
        return lenUomConversionRate;
    }

    public void setLenUomConversionRate(Map<String, Double> lenUomConversionRate) {
        this.lenUomConversionRate = lenUomConversionRate;
    }

    public Map<String, Double> getWeightUomConversionRate() {
        return weightUomConversionRate;
    }

    public void setWeightUomConversionRate(Map<String, Double> weightUomConversionRate) {
        this.weightUomConversionRate = weightUomConversionRate;
    }

    public Map<Long, Map<Long, Long>> getInsideContainerSkuIdsQty() {
        return insideContainerSkuIdsQty;
    }

    public void setInsideContainerSkuIdsQty(Map<Long, Map<Long, Long>> insideContainerSkuIdsQty) {
        this.insideContainerSkuIdsQty = insideContainerSkuIdsQty;
    }

    public Map<Long, Set<Long>> getInsideContainerIdSkuIds() {
        return insideContainerIdSkuIds;
    }

    public void setInsideContainerIdSkuIds(Map<Long, Set<Long>> insideContainerIdSkuIds) {
        this.insideContainerIdSkuIds = insideContainerIdSkuIds;
    }

    
    
    

}
