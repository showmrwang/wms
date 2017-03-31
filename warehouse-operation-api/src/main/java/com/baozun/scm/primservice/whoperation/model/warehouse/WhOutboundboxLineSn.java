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
public class WhOutboundboxLineSn extends BaseModel {

    private static final long serialVersionUID = 1214558575182808179L;

    // columns START
    /** 出库箱明细行ID */
    private Long whOutboundboxLineId;
    /** sn号 */
    private String sn;
    /** 占用单据号 */
    private String occupationCode;
    /** 补货单号 */
    private String replenishmentCode;
    /** 残次品条码 */
    private String defectWareBarcode;
    /** 残次类型来源 STORE店铺 WH仓库 */
    private String defectSource;
    /** 残次原因类型ID */
    private Long defectTypeId;
    /** 残次原因ID */
    private Long defectReasonsId;
    /** 状态 1:在库2:已分配3:冻结 */
    private Integer status;
    /** 库存对应属性拼接 开发内部使用 */
    private String invAttr;
    /** 内部对接码 */
    private String uuid;
    /** 对应仓库ID */
    private Long ouId;
    /** 系统uuid 用于逻辑处理 */
    private String sysUuid;
    // columns END

    public WhOutboundboxLineSn() {}

    public WhOutboundboxLineSn(Long id) {
        this.id = id;
    }

    public void setWhOutboundboxLineId(Long whOutboundboxLineId) {
        this.whOutboundboxLineId = whOutboundboxLineId;
    }

    public Long getWhOutboundboxLineId() {
        return this.whOutboundboxLineId;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSn() {
        return this.sn;
    }

    public void setOccupationCode(String occupationCode) {
        this.occupationCode = occupationCode;
    }

    public String getOccupationCode() {
        return this.occupationCode;
    }

    public void setReplenishmentCode(String replenishmentCode) {
        this.replenishmentCode = replenishmentCode;
    }

    public String getReplenishmentCode() {
        return this.replenishmentCode;
    }

    public void setDefectWareBarcode(String defectWareBarcode) {
        this.defectWareBarcode = defectWareBarcode;
    }

    public String getDefectWareBarcode() {
        return this.defectWareBarcode;
    }

    public void setDefectSource(String defectSource) {
        this.defectSource = defectSource;
    }

    public String getDefectSource() {
        return this.defectSource;
    }

    public void setDefectTypeId(Long defectTypeId) {
        this.defectTypeId = defectTypeId;
    }

    public Long getDefectTypeId() {
        return this.defectTypeId;
    }

    public void setDefectReasonsId(Long defectReasonsId) {
        this.defectReasonsId = defectReasonsId;
    }

    public Long getDefectReasonsId() {
        return this.defectReasonsId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setInvAttr(String invAttr) {
        this.invAttr = invAttr;
    }

    public String getInvAttr() {
        return this.invAttr;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setSysUuid(String sysUuid) {
        this.sysUuid = sysUuid;
    }

    public String getSysUuid() {
        return sysUuid;
    }

}

