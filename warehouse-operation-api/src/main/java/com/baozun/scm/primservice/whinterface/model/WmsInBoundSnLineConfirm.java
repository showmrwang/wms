package com.baozun.scm.primservice.whinterface.model;

import java.io.Serializable;

/**
 * 入库反馈SN/残次信息
 * 
 * @author kai.zhu
 * @version 2017年2月20日
 */
public class WmsInBoundSnLineConfirm implements Serializable {
	
	private static final long serialVersionUID = -3596541602546458282L;
	
	/** Sn号 */
	private String sn;
	/** 残次条码 */
	private String defectWareBarcode;
	/** 残次原因来源 STORE店铺 WH仓库 */
	private String defectSource;
	/** 残次原因类型CODE */
	private String defectType;
	/** 残次原因CODE */
	private String defectReasons;
	
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getDefectWareBarcode() {
		return defectWareBarcode;
	}
	public void setDefectWareBarcode(String defectWareBarcode) {
		this.defectWareBarcode = defectWareBarcode;
	}
	public String getDefectSource() {
		return defectSource;
	}
	public void setDefectSource(String defectSource) {
		this.defectSource = defectSource;
	}
	public String getDefectType() {
		return defectType;
	}
	public void setDefectType(String defectType) {
		this.defectType = defectType;
	}
	public String getDefectReasons() {
		return defectReasons;
	}
	public void setDefectReasons(String defectReasons) {
		this.defectReasons = defectReasons;
	}
	
}
