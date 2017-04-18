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
package com.baozun.scm.primservice.whoperation.model.confirm.outbound;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 出库单发票明细信息
 *
 */
public class WhOutboundInvoiceLineConfirm extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = 2227720735261597853L;

    /** 出库单发票反馈ID */
    private Long outboundInvoiceConfirmId;
    /** 行号 */
    private String linenum;
    /** 数量 */
    private Double qty;
    /** 单价 */
    private Double unitPrice;
    /** 总金额 */
    private Double amt;
    /** 类别 */
    private String item;
    /** 仓库组织ID */
    private Long ouId;

    public Long getOutboundInvoiceConfirmId() {
        return outboundInvoiceConfirmId;
    }

    public void setOutboundInvoiceConfirmId(Long outboundInvoiceConfirmId) {
        this.outboundInvoiceConfirmId = outboundInvoiceConfirmId;
    }

    public String getLinenum() {
        return linenum;
    }

    public void setLinenum(String linenum) {
        this.linenum = linenum;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getAmt() {
        return amt;
    }

    public void setAmt(Double amt) {
        this.amt = amt;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }



}
