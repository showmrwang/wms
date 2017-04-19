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

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * t_wh_invoice
 * 
 * @author larkark
 *
 */
public class WhInvoice extends BaseModel {

    private static final long serialVersionUID = 1867408384084341026L;

    /** WMS发票流水号 */
    private String code;
    /** 出库单编码 */
    private String odoCode;
    /** 导出序列 */
    private String index;
    /** 店铺编码 */
    private String storeCode;
    /** 上位系统发票流水号 */
    private String invoiceCode;
    /** 发票日期 */
    private String invoiceDate;
    /** 发票号 */
    private String invoiceNo;
    /** 付款单位（发票抬头） */
    private String payer;
    /** 商品 */
    private String item;
    /** 数量 */
    private Integer qty;
    /** 单价 */
    private Double unitPrice;
    /** 总金额 */
    private Double amt;
    /** 发票备注 */
    private String memo;
    /** 收款人 */
    private String payee;
    /** 开票人 */
    private String drawer;
    /** 导出次数 */
    private Integer exportCount;
    /** 导出批次 */
    private String exportBatch;
    /** 公司 */
    private String company;
    /** 是否货票分离 0:否 1:是 */
    private Boolean isFreightInvoiceSunder;
    /** 数据来源 区分上位系统 */
    private String dataSource;
    /** 仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 操作时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;
    /** 是否导出 */
    private Boolean isExport;
    /** 最后导出时间 */
    private Date lastExportTime;


    public void setOdoCode(String odoCode) {
        this.odoCode = odoCode;
    }

    public String getOdoCode() {
        return this.odoCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreCode() {
        return this.storeCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public String getInvoiceCode() {
        return this.invoiceCode;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceDate() {
        return this.invoiceDate;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceNo() {
        return this.invoiceNo;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayer() {
        return this.payer;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return this.item;
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

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getPayee() {
        return this.payee;
    }

    public void setDrawer(String drawer) {
        this.drawer = drawer;
    }

    public String getDrawer() {
        return this.drawer;
    }

    public void setExportCount(Integer exportCount) {
        this.exportCount = exportCount;
    }

    public Integer getExportCount() {
        return this.exportCount;
    }

    public void setExportBatch(String exportBatch) {
        this.exportBatch = exportBatch;
    }

    public String getExportBatch() {
        return this.exportBatch;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return this.company;
    }

    public void setIsFreightInvoiceSunder(Boolean isFreightInvoiceSunder) {
        this.isFreightInvoiceSunder = isFreightInvoiceSunder;
    }

    public Boolean getIsFreightInvoiceSunder() {
        return this.isFreightInvoiceSunder;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Date getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getOperatorId() {
        return this.operatorId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Boolean getIsExport() {
        return isExport;
    }

    public void setIsExport(Boolean isExport) {
        this.isExport = isExport;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public Date getLastExportTime() {
        return lastExportTime;
    }

    public void setLastExportTime(Date lastExportTime) {
        this.lastExportTime = lastExportTime;
    }
}
