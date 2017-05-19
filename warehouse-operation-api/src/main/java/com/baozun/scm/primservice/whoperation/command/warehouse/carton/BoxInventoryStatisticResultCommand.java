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
package com.baozun.scm.primservice.whoperation.command.warehouse.carton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;

/**
 * @author zhaozili
 *
 */
public class BoxInventoryStatisticResultCommand extends BaseCommand {

	private static final long serialVersionUID = 2493540688266101306L;
	
    /** 出库箱id */
    private Long outBoundBoxId;
    /** 出库箱号 */
    private String outBoundBoxCode;
    /** 所有sku种类 */
    Set<Long> skuIds = new HashSet<Long>();
    /** sku总件数 */
    Long skuQty = 0L;
    /** 所有唯一sku */
    Set<String> skuAttrIds = new HashSet<String>();
    /** 出库箱所有sku种类 */
    Map<String, Set<Long>> outBoundBoxSkuIds = new HashMap<String, Set<Long>>();
    /** 出库箱所有sku总件数 */
    Map<String, Long> outBoundBoxSkuQty = new HashMap<String, Long>();
    /** 出库箱单个sku总件数 */
    Map<String, Map<Long, Long>> outBoundBoxSkuIdQtys = new HashMap<String, Map<Long, Long>>();
    /** 出库箱唯一sku种类 */
    Map<String, Set<String>> outBoundBoxSkuIdSkuAttrIds = new HashMap<String, Set<String>>();
    /** 出库箱唯一sku总件数 */
    Map<String, Map<String, Long>> outBoundBoxSkuIdSkuAttrIdQtys = new HashMap<String, Map<String, Long>>();
    /** 出库箱唯一sku对应所有残次条码 */
    Map<String, Map<String, Set<String>>> outBoundBoxSkuAttrIdsSnDefect = new HashMap<String, Map<String, Set<String>>>();
	public Long getOutBoundBoxId() {
		return outBoundBoxId;
	}
	public void setOutBoundBoxId(Long outBoundBoxId) {
		this.outBoundBoxId = outBoundBoxId;
	}
	public String getOutBoundBoxCode() {
		return outBoundBoxCode;
	}
	public void setOutBoundBoxCode(String outBoundBoxCode) {
		this.outBoundBoxCode = outBoundBoxCode;
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
	public Map<String, Set<Long>> getOutBoundBoxSkuIds() {
		return outBoundBoxSkuIds;
	}
	public void setOutBoundBoxSkuIds(Map<String, Set<Long>> outBoundBoxSkuIds) {
		this.outBoundBoxSkuIds = outBoundBoxSkuIds;
	}
	public Map<String, Long> getOutBoundBoxSkuQty() {
		return outBoundBoxSkuQty;
	}
	public void setOutBoundBoxSkuQty(Map<String, Long> outBoundBoxSkuQty) {
		this.outBoundBoxSkuQty = outBoundBoxSkuQty;
	}
	public Map<String, Map<Long, Long>> getOutBoundBoxSkuIdQtys() {
		return outBoundBoxSkuIdQtys;
	}
	public void setOutBoundBoxSkuIdQtys(
			Map<String, Map<Long, Long>> outBoundBoxSkuIdQtys) {
		this.outBoundBoxSkuIdQtys = outBoundBoxSkuIdQtys;
	}
	public Map<String, Set<String>> getOutBoundBoxSkuIdSkuAttrIds() {
		return outBoundBoxSkuIdSkuAttrIds;
	}
	public void setOutBoundBoxSkuIdSkuAttrIds(
			Map<String, Set<String>> outBoundBoxSkuIdSkuAttrIds) {
		this.outBoundBoxSkuIdSkuAttrIds = outBoundBoxSkuIdSkuAttrIds;
	}
	public Map<String, Map<String, Long>> getOutBoundBoxSkuIdSkuAttrIdQtys() {
		return outBoundBoxSkuIdSkuAttrIdQtys;
	}
	public void setOutBoundBoxSkuIdSkuAttrIdQtys(
			Map<String, Map<String, Long>> outBoundBoxSkuIdSkuAttrIdQtys) {
		this.outBoundBoxSkuIdSkuAttrIdQtys = outBoundBoxSkuIdSkuAttrIdQtys;
	}
	public Map<String, Map<String, Set<String>>> getOutBoundBoxSkuAttrIdsSnDefect() {
		return outBoundBoxSkuAttrIdsSnDefect;
	}
	public void setOutBoundBoxSkuAttrIdsSnDefect(
			Map<String, Map<String, Set<String>>> outBoundBoxSkuAttrIdsSnDefect) {
		this.outBoundBoxSkuAttrIdsSnDefect = outBoundBoxSkuAttrIdsSnDefect;
	}

    


}
