/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 集货功能参数
 * @author larkark
 *
 */
public class WhFunctionCollectionCommand extends BaseCommand {
	
	private static final long serialVersionUID = -5949432957349737036L;
	
	//columns START
	/** id */
	private Long id;
	/** 对应功能ID */
	private Long functionId;
	/** 集货模式 收货集货 出库集货 人为集货 */
	private Integer collectionPattern;
	/** 单次携带容器数量 */
	private Integer containerQty;
	/** 对应组织ID */
	private Long ouId;
	//columns END
	
	/** 容器编号 */
	private String containerCode;
	/** 已携带的容器数量 */
	private Integer carryQty;
	/** 批次号 */
	private String batch;
	/** 设施编号 */
	private String facilityCode;
	
	public WhFunctionCollectionCommand(){}

	public WhFunctionCollectionCommand(Long id){
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFunctionId(Long value) {
		this.functionId = value;
	}
	
	public Long getFunctionId() {
		return this.functionId;
	}
	public void setCollectionPattern(Integer value) {
		this.collectionPattern = value;
	}
	
	public Integer getCollectionPattern() {
		return this.collectionPattern;
	}
	public void setContainerQty(Integer value) {
		this.containerQty = value;
	}
	
	public Integer getContainerQty() {
		return this.containerQty;
	}
	public void setOuId(Long value) {
		this.ouId = value;
	}
	
	public Long getOuId() {
		return this.ouId;
	}

	public String getContainerCode() {
		return containerCode;
	}

	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}

	public Integer getCarryQty() {
		return carryQty;
	}

	public void setCarryQty(Integer carryQty) {
		this.carryQty = carryQty;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getFacilityCode() {
		return facilityCode;
	}

	public void setFacilityCode(String facilityCode) {
		this.facilityCode = facilityCode;
	}
}

