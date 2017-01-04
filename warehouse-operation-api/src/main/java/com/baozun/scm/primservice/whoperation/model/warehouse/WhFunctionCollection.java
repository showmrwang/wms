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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 集货功能参数
 * @author larkark
 *
 */
public class WhFunctionCollection extends BaseModel {
	
	private static final long serialVersionUID = -199360846507115950L;
	
	//columns START
	/** 对应功能ID */
	private Long functionId;
	/** 集货模式 收货集货 出库集货 人为集货 */
	private Integer collectionPattern;
	/** 单次携带容器数量 */
	private Integer containerQty;
	/** 对应组织ID */
	private Long ouId;
	//columns END

	public WhFunctionCollection(){}

	public WhFunctionCollection(Long id){
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
}

