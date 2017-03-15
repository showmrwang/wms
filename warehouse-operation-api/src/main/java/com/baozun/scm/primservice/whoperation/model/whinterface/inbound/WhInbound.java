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
package com.baozun.scm.primservice.whoperation.model.whinterface.inbound;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhInbound extends BaseModel {

	private static final long serialVersionUID = -1816380527911072373L;
	
	// columns START
	/** 数据唯一标识 */
	private String uuid;
	/** 入库单据编码 */
	private String extPoCode;
	/** 上位系统原始单号 */
	private String extCode;
	/** 客户编码 */
	private String customerCode;
	/** 店铺CODE */
	private String storeCode;
	/** 来源地 */
	private String fromLocation;
	/** 目的地 */
	private String toLocation;
	/** 计划到货时间格式：年（4位）月（2位）日（2位）时（2位）分（2位）秒（2位） */
	private Date eta;
	/** 仓库编码 */
	private String whCode;
	/** 单据类型 */
	private String poType;
	/** 上位系统单据类型 */
	private String extPoType;
	/** 是否质检 默认否 */
	private Boolean isIqc;
	/** 计划数量 */
	private Double qtyPlanned;
	/** 计划箱数 */
	private Integer ctnPlanned;
	/** 扩展字段信息 */
	private String extMemo;
	/** 数据来源 区分上位系统 */
	private String dataSource;
	/** 状态 */
	private Integer status;
	/** 失败次数 */
	private Integer errorCount;
	/** 创建时间 */
	private Date createTime;
	/** 最后修改时间 */
	private Date lastModifyTime;
	// columns END

	public WhInbound() {
	}

	public WhInbound(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getExtPoCode() {
		return extPoCode;
	}

	public void setExtPoCode(String extPoCode) {
		this.extPoCode = extPoCode;
	}

	public String getExtCode() {
		return extCode;
	}

	public void setExtCode(String extCode) {
		this.extCode = extCode;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}

	public Date getEta() {
		return eta;
	}

	public void setEta(Date eta) {
		this.eta = eta;
	}

	public String getWhCode() {
		return whCode;
	}

	public void setWhCode(String whCode) {
		this.whCode = whCode;
	}

	public String getPoType() {
		return poType;
	}

	public void setPoType(String poType) {
		this.poType = poType;
	}

	public String getExtPoType() {
		return extPoType;
	}

	public void setExtPoType(String extPoType) {
		this.extPoType = extPoType;
	}

	public Boolean getIsIqc() {
		return isIqc;
	}

	public void setIsIqc(Boolean isIqc) {
		this.isIqc = isIqc;
	}

	public Double getQtyPlanned() {
		return qtyPlanned;
	}

	public void setQtyPlanned(Double qtyPlanned) {
		this.qtyPlanned = qtyPlanned;
	}

	public Integer getCtnPlanned() {
		return ctnPlanned;
	}

	public void setCtnPlanned(Integer ctnPlanned) {
		this.ctnPlanned = ctnPlanned;
	}

	public String getExtMemo() {
		return extMemo;
	}

	public void setExtMemo(String extMemo) {
		this.extMemo = extMemo;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}

}
