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
public class WhOutboundSnLine extends BaseModel {
	
	/**
     * 
     */
    private static final long serialVersionUID = 2382943045849501499L;
    
    //columns START
	/** 出库单明细ID */
	private java.lang.Long outboundLineId;
	/** sn号 */
	private java.lang.String sn;
	/** 残次条码 */
	private java.lang.String defectWareBarcode;
	/** 残次原因来源 STORE店铺 WH仓库 */
	private java.lang.String defectSource;
	/** 残次原因类型CODE */
	private java.lang.String defectType;
	/** 残次原因CODE */
	private java.lang.String defectReasons;
	//columns END
	
    public java.lang.Long getOutboundLineId() {
        return outboundLineId;
    }
    public void setOutboundLineId(java.lang.Long outboundLineId) {
        this.outboundLineId = outboundLineId;
    }
    public java.lang.String getSn() {
        return sn;
    }
    public void setSn(java.lang.String sn) {
        this.sn = sn;
    }
    public java.lang.String getDefectWareBarcode() {
        return defectWareBarcode;
    }
    public void setDefectWareBarcode(java.lang.String defectWareBarcode) {
        this.defectWareBarcode = defectWareBarcode;
    }
    public java.lang.String getDefectSource() {
        return defectSource;
    }
    public void setDefectSource(java.lang.String defectSource) {
        this.defectSource = defectSource;
    }
    public java.lang.String getDefectType() {
        return defectType;
    }
    public void setDefectType(java.lang.String defectType) {
        this.defectType = defectType;
    }
    public java.lang.String getDefectReasons() {
        return defectReasons;
    }
    public void setDefectReasons(java.lang.String defectReasons) {
        this.defectReasons = defectReasons;
    }

}

