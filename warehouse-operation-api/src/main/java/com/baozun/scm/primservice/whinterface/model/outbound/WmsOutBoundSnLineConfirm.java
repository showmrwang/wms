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
package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;

/**
 * 出库单反馈SN号/残次信息
 * 
 *
 */
public class WmsOutBoundSnLineConfirm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7172823289968437626L;
    /** sn号 */
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
