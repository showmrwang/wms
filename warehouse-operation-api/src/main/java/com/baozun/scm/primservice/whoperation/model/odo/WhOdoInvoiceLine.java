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
package com.baozun.scm.primservice.whoperation.model.odo;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhOdoInvoiceLine extends BaseModel {

    private static final long serialVersionUID = -5565968440109885293L;

    /** 发票头ID */
    private Long odoInvoiceId;
    /** 行号 */
    private String linenum;
    /** 数量 */
    private Integer qty;
    /** 单价 */
    private Long unitPrice;
    /** 总金额 */
    private Long amt;
    /** 类别 */
    private String item;
    /** 仓库组织ID */
    private Long ouId;

    /** 当前月份 用于归档 */
    private String sysDate;

    public Long getOdoInvoiceId() {
        return odoInvoiceId;
    }

    public void setOdoInvoiceId(Long odoInvoiceId) {
        this.odoInvoiceId = odoInvoiceId;
    }

    public String getLinenum() {
        return linenum;
    }

    public void setLinenum(String linenum) {
        this.linenum = linenum;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
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

    public String getSysDate() {
        return sysDate;
    }

    public void setSysDate(String sysDate) {
        this.sysDate = sysDate;
    }


}
