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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * t_wh_invoice_line
 * @author larkark
 *
 */
public class WhInvoiceLine extends BaseModel {

    private static final long serialVersionUID = 1161402855531979536L;
    
    // columns START
    /** 发票头ID */
    private Long whInvoiceId;
    /** 行号 */
    private String linenum;
    /** 数量 */
    private Integer qty;
    /** 单价 */
    private Double unitPrice;
    /** 总金额 */
    private Double amt;
    /** 类别 */
    private String item;
    /** 仓库组织ID */
    private Long ouId;
    // columns END

    public WhInvoiceLine() {}

    public WhInvoiceLine(Long id) {
        this.id = id;
    }

    public void setWhInvoiceId(Long whInvoiceId) {
        this.whInvoiceId = whInvoiceId;
    }

    public Long getWhInvoiceId() {
        return this.whInvoiceId;
    }

    public void setLinenum(String linenum) {
        this.linenum = linenum;
    }

    public String getLinenum() {
        return this.linenum;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getQty() {
        return this.qty;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getUnitPrice() {
        return this.unitPrice;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    public Double getAmt() {
        return this.amt;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return this.item;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOuId() {
        return this.ouId;
    }
}

