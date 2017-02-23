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
public class WhOutboundInvoiceLine extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = -2086733746509974484L;
    
    //columns START
	/** 出库单发票ID */
	private java.lang.Long outboundInvoiceId;
	/** 行号 */
	private java.lang.String linenum;
	/** 数量 */
	private Long qty;
	/** 单价 */
	private Long unitPrice;
	/** 总金额 */
	private Long amt;
	/** 类别 */
	private java.lang.String item;
	//columns END
	
    public java.lang.Long getOutboundInvoiceId() {
        return outboundInvoiceId;
    }
    public void setOutboundInvoiceId(java.lang.Long outboundInvoiceId) {
        this.outboundInvoiceId = outboundInvoiceId;
    }
    public java.lang.String getLinenum() {
        return linenum;
    }
    public void setLinenum(java.lang.String linenum) {
        this.linenum = linenum;
    }
    public Long getQty() {
        return qty;
    }
    public void setQty(Long qty) {
        this.qty = qty;
    }
    public Long getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }
    public Long getAmt() {
        return amt;
    }
    public void setAmt(Long amt) {
        this.amt = amt;
    }
    public java.lang.String getItem() {
        return item;
    }
    public void setItem(java.lang.String item) {
        this.item = item;
    }

}

