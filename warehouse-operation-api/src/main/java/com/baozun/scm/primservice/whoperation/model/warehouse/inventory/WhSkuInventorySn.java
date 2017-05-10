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
package com.baozun.scm.primservice.whoperation.model.warehouse.inventory;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 库存明细表
 * 
 * @author larkark
 * 
 */
public class WhSkuInventorySn extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 7442837653527549234L;

    /** 库存ID */
    private Long invId;
    /** 商品ID */
    private Long skuId;
    /** sn号 */
    private String sn;
    /** 占用单据号 */
    private String occupationCode;
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
    /**补货编码*/
    private String replenishmentCode;
    
    private Long occupationLineId;

    public Long getInvId() {
        return invId;
    }

    public void setInvId(Long invId) {
        this.invId = invId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getOccupationCode() {
        return occupationCode;
    }

    public void setOccupationCode(String occupationCode) {
        this.occupationCode = occupationCode;
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

    public Long getDefectTypeId() {
        return defectTypeId;
    }

    public void setDefectTypeId(Long defectTypeId) {
        this.defectTypeId = defectTypeId;
    }

    public Long getDefectReasonsId() {
        return defectReasonsId;
    }

    public void setDefectReasonsId(Long defectReasonsId) {
        this.defectReasonsId = defectReasonsId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInvAttr() {
        return invAttr;
    }

    public void setInvAttr(String invAttr) {
        this.invAttr = invAttr;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getSysUuid() {
        return sysUuid;
    }

    public void setSysUuid(String sysUuid) {
        this.sysUuid = sysUuid;
    }

    public String getReplenishmentCode() {
        return replenishmentCode;
    }

    public void setReplenishmentCode(String replenishmentCode) {
        this.replenishmentCode = replenishmentCode;
    }

    public Long getOccupationLineId() {
        return occupationLineId;
    }

    public void setOccupationLineId(Long occupationLineId) {
        this.occupationLineId = occupationLineId;
    }

    

}
