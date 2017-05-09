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
package com.baozun.scm.primservice.whoperation.model.odo.wave;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhWaveMasterPrintCondition extends BaseModel {
	
	private static final long serialVersionUID = -5706313699140758838L;
	
	//columns START
	/** 波次主档ID */
	private Long waveMasterPrintId;
	/** 打印单据类型 */
	private String printDocType;
	/** 拆分条件ID */
	private Long printSplitConditionId;
	/** 拆分条件SQL */
	private String printSplitConditionSql;
	/** 数据排序规则SQL */
	private String printSortSql;
	/** 仓库组织ID */
	private Long ouId;
	//columns END

	public WhWaveMasterPrintCondition() {}

	public WhWaveMasterPrintCondition(Long id) {
		this.id = id;
	}

	public Long getWaveMasterPrintId() {
		return waveMasterPrintId;
	}

	public void setWaveMasterPrintId(Long waveMasterPrintId) {
		this.waveMasterPrintId = waveMasterPrintId;
	}

	public String getPrintDocType() {
		return printDocType;
	}

	public void setPrintDocType(String printDocType) {
		this.printDocType = printDocType;
	}

	public Long getPrintSplitConditionId() {
		return printSplitConditionId;
	}

	public void setPrintSplitConditionId(Long printSplitConditionId) {
		this.printSplitConditionId = printSplitConditionId;
	}

	public String getPrintSplitConditionSql() {
		return printSplitConditionSql;
	}

	public void setPrintSplitConditionSql(String printSplitConditionSql) {
		this.printSplitConditionSql = printSplitConditionSql;
	}

	public String getPrintSortSql() {
		return printSortSql;
	}

	public void setPrintSortSql(String printSortSql) {
		this.printSortSql = printSortSql;
	}

	public Long getOuId() {
		return ouId;
	}

	public void setOuId(Long ouId) {
		this.ouId = ouId;
	}

}

