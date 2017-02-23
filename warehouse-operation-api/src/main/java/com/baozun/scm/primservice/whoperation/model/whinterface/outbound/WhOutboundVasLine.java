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
package com.baozun.scm.primservice.whoperation.model.whinterface.outbound;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhOutboundVasLine extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = -4769789371317428263L;
    
	//columns START
	/** 出库单明细ID */
	private java.lang.Long outboundLineId;
	/** 增值服务类型 快递/仓库 */
	private java.lang.String vasType;
	/** 仓库增值服务类型 */
	private java.lang.Long whVasType;
	/** 快递增值服务ID */
	private java.lang.Long expressVasType;
	/** 打印模板 */
	private java.lang.String printTemplet;
	/** 赠品编码/礼品包装 */
	private java.lang.String skuBarCode;
	/** 内容/备注 */
	private java.lang.String content;
	/** 箱号 */
	private java.lang.String cartonNo;
	/** 数量 赠送数量 */
	private java.lang.String qty;
	/** 金额 */
	private java.lang.String amt;
	//columns END
	
    public java.lang.Long getOutboundLineId() {
        return outboundLineId;
    }
    public void setOutboundLineId(java.lang.Long outboundLineId) {
        this.outboundLineId = outboundLineId;
    }
    public java.lang.String getVasType() {
        return vasType;
    }
    public void setVasType(java.lang.String vasType) {
        this.vasType = vasType;
    }
    public java.lang.Long getWhVasType() {
        return whVasType;
    }
    public void setWhVasType(java.lang.Long whVasType) {
        this.whVasType = whVasType;
    }
    public java.lang.Long getExpressVasType() {
        return expressVasType;
    }
    public void setExpressVasType(java.lang.Long expressVasType) {
        this.expressVasType = expressVasType;
    }
    public java.lang.String getPrintTemplet() {
        return printTemplet;
    }
    public void setPrintTemplet(java.lang.String printTemplet) {
        this.printTemplet = printTemplet;
    }
    public java.lang.String getSkuBarCode() {
        return skuBarCode;
    }
    public void setSkuBarCode(java.lang.String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }
    public java.lang.String getContent() {
        return content;
    }
    public void setContent(java.lang.String content) {
        this.content = content;
    }
    public java.lang.String getCartonNo() {
        return cartonNo;
    }
    public void setCartonNo(java.lang.String cartonNo) {
        this.cartonNo = cartonNo;
    }
    public java.lang.String getQty() {
        return qty;
    }
    public void setQty(java.lang.String qty) {
        this.qty = qty;
    }
    public java.lang.String getAmt() {
        return amt;
    }
    public void setAmt(java.lang.String amt) {
        this.amt = amt;
    }

}

