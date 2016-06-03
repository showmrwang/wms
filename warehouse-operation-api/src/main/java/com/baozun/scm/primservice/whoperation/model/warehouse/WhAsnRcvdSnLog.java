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
 * 
 * @author larkark
 *
 */
public class WhAsnRcvdSnLog extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 6166504771376549077L;

    /** 对应ASN_RCVD_ID */
    private Long asnRcvdId;
    /** sn号 */
    private String sn;
    /** 残次品条码 */
    private String defectWareBarcode;
    /** 残次原因类型 */
    private String defectType;
    /** 残次原因 */
    private String defectReasons;
    /** 对应组织ID */
    private Long ouId;
  
    
    private String skuName;
    private String skuCode;
    private String containerCode;
    

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public WhAsnRcvdSnLog() {}

    public WhAsnRcvdSnLog(Long id) {
        this.id = id;
    }

    public void setAsnRcvdId(Long value) {
        this.asnRcvdId = value;
    }

    public Long getAsnRcvdId() {
        return this.asnRcvdId;
    }

    public void setSn(String value) {
        this.sn = value;
    }

    public String getSn() {
        return this.sn;
    }

    public void setDefectWareBarcode(String value) {
        this.defectWareBarcode = value;
    }

    public String getDefectWareBarcode() {
        return this.defectWareBarcode;
    }

    public void setDefectType(String value) {
        this.defectType = value;
    }

    public String getDefectType() {
        return this.defectType;
    }

    public void setDefectReasons(String value) {
        this.defectReasons = value;
    }

    public String getDefectReasons() {
        return this.defectReasons;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

}

