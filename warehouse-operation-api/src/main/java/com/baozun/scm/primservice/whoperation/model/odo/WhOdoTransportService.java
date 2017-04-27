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
 * 用作记录根据出库单调用物流服务是否成功或异常信息
 * @author larkark
 *
 */
public class WhOdoTransportService extends BaseModel {

    private static final long serialVersionUID = -3318209483873099006L;
    
    // columns START
    /** 出库单ID */
    private Long odoId;
    /** 是否推荐快递增值服务成功 */
    private Boolean isVasSuccess;
    /** 快递增值服务推荐失败编码 */
    private String vasErrorCode;
    /** 是否推荐物流商成功 */
    private Boolean isTspSuccess;
    /** 推荐物流商失败编码 */
    private String tspErrorCode;
    /** 是否获取运单号成功 */
    private Boolean isWaybillCodeSuccess;
    /** 获取运单号失败编码 */
    private String waybillCodeErrorCode;
    /** 仓库组织ID */
    private Long ouId;
    // columns END

    public WhOdoTransportService() {}

    public WhOdoTransportService(Long id) {
        this.id = id;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public Long getOdoId() {
        return this.odoId;
    }

    public void setIsVasSuccess(Boolean isVasSuccess) {
        this.isVasSuccess = isVasSuccess;
    }

    public Boolean getIsVasSuccess() {
        return this.isVasSuccess;
    }

    public void setVasErrorCode(String vasErrorCode) {
        this.vasErrorCode = vasErrorCode;
    }

    public String getVasErrorCode() {
        return this.vasErrorCode;
    }

    public void setIsTspSuccess(Boolean isTspSuccess) {
        this.isTspSuccess = isTspSuccess;
    }

    public Boolean getIsTspSuccess() {
        return this.isTspSuccess;
    }

    public void setTspErrorCode(String tspErrorCode) {
        this.tspErrorCode = tspErrorCode;
    }

    public String getTspErrorCode() {
        return this.tspErrorCode;
    }

    public void setIsWaybillCodeSuccess(Boolean isWaybillCodeSuccess) {
        this.isWaybillCodeSuccess = isWaybillCodeSuccess;
    }

    public Boolean getIsWaybillCodeSuccess() {
        return this.isWaybillCodeSuccess;
    }

    public void setWaybillCodeErrorCode(String waybillCodeErrorCode) {
        this.waybillCodeErrorCode = waybillCodeErrorCode;
    }

    public String getWaybillCodeErrorCode() {
        return this.waybillCodeErrorCode;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOuId() {
        return this.ouId;
    }
}

