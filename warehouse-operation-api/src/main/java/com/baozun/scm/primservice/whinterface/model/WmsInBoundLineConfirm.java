package com.baozun.scm.primservice.whinterface.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 对接上位系统的wms入库反馈明细实体
 * 
 * @author kai.zhu
 * @version 2017年2月20日
 */
public class WmsInBoundLineConfirm implements Serializable {
	
	private static final long serialVersionUID = -2833209196781153934L;
	
	/** 商品唯一编码 */
	private String upc;
	/** 款式 */
	private String style;
	/** 颜色 */
	private String color;
	/** 尺码 */
	private String size;
	/** 计划数量 */
	private Double qty;
	/** 实际数量 */
	private Double qtyRcvd;
	/** 箱号 */
	private String cartonNo;
	/** 行唯一标识 */
	private String lineSeq;
	/** 库存状态 */
	private String invStatus;
	/** 库存类型 */
	private String invType;
	/** 批次号 */
	private String batchNumber;
	/** 生产日期 */
	private Date mfgDate;
	/** 失效日期 */
	private Date expDate;
	/** 原产地 */
	private String countryOfOrigin;
	/** 库存属性1 */
	private String invAttr1;
	/** 库存属性2 */
	private String invAttr2;
	/** 库存属性3 */
	private String invAttr3;
	/** 库存属性4 */
	private String invAttr4;
	/** 库存属性5 */
	private String invAttr5;
	/** 扩展字段信息 */
	private String extMemo;
	/** 是否质检 */
	private Boolean isIqc;
	/** 收货SN反馈信息 */
	private List<WmsInBoundSnLineConfirm> wmsInBoundSnConfirmLines;
	
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public Double getQty() {
		return qty;
	}
	public void setQty(Double qty) {
		this.qty = qty;
	}
	public Double getQtyRcvd() {
		return qtyRcvd;
	}
	public void setQtyRcvd(Double qtyRcvd) {
		this.qtyRcvd = qtyRcvd;
	}
	public String getCartonNo() {
		return cartonNo;
	}
	public void setCartonNo(String cartonNo) {
		this.cartonNo = cartonNo;
	}
	public String getLineSeq() {
		return lineSeq;
	}
	public void setLineSeq(String lineSeq) {
		this.lineSeq = lineSeq;
	}
	public String getInvStatus() {
		return invStatus;
	}
	public void setInvStatus(String invStatus) {
		this.invStatus = invStatus;
	}
	public String getInvType() {
		return invType;
	}
	public void setInvType(String invType) {
		this.invType = invType;
	}
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public Date getMfgDate() {
		return mfgDate;
	}
	public void setMfgDate(Date mfgDate) {
		this.mfgDate = mfgDate;
	}
	public Date getExpDate() {
		return expDate;
	}
	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}
	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}
	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}
	public String getInvAttr1() {
		return invAttr1;
	}
	public void setInvAttr1(String invAttr1) {
		this.invAttr1 = invAttr1;
	}
	public String getInvAttr2() {
		return invAttr2;
	}
	public void setInvAttr2(String invAttr2) {
		this.invAttr2 = invAttr2;
	}
	public String getInvAttr3() {
		return invAttr3;
	}
	public void setInvAttr3(String invAttr3) {
		this.invAttr3 = invAttr3;
	}
	public String getInvAttr4() {
		return invAttr4;
	}
	public void setInvAttr4(String invAttr4) {
		this.invAttr4 = invAttr4;
	}
	public String getInvAttr5() {
		return invAttr5;
	}
	public void setInvAttr5(String invAttr5) {
		this.invAttr5 = invAttr5;
	}
	public String getExtMemo() {
		return extMemo;
	}
	public void setExtMemo(String extMemo) {
		this.extMemo = extMemo;
	}
	public Boolean getIsIqc() {
		return isIqc;
	}
	public void setIsIqc(Boolean isIqc) {
		this.isIqc = isIqc;
	}
	public List<WmsInBoundSnLineConfirm> getWmsInBoundSnConfirmLines() {
		return wmsInBoundSnConfirmLines;
	}
	public void setWmsInBoundSnConfirmLines(List<WmsInBoundSnLineConfirm> wmsInBoundSnConfirmLines) {
		this.wmsInBoundSnConfirmLines = wmsInBoundSnConfirmLines;
	}
}
