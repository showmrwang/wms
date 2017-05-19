/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.command.warehouse.checking;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class CheckingProcessCommand extends BaseCommand {

    private static final long serialVersionUID = -3729645446942776273L;

    /** 功能ID */
    private Long functionId;
    /** 操作台ID */
    private Long facilityId;
    /** 复核容器编码 小车/播种墙编码、周转箱、出库箱 */
    private String checkingSourceCode;
    /** 复核箱编码 小车/播种墙货格或者出库箱、周转箱、出库箱 */
    private String checkingBoxCode;
    /** 复核数据源类型 复核集货、播种 */
    private String checkingSourceType;
    /** 正在复核的复核头ID */
    private Long checkingId;
    /** 复核流程分支 6个分支 */
    private String checkingType;


    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getCheckingSourceCode() {
        return checkingSourceCode;
    }

    public void setCheckingSourceCode(String checkingSourceCode) {
        this.checkingSourceCode = checkingSourceCode;
    }

    public String getCheckingBoxCode() {
        return checkingBoxCode;
    }

    public void setCheckingBoxCode(String checkingBoxCode) {
        this.checkingBoxCode = checkingBoxCode;
    }

    public String getCheckingSourceType() {
        return checkingSourceType;
    }

    public void setCheckingSourceType(String checkingSourceType) {
        this.checkingSourceType = checkingSourceType;
    }

    public Long getCheckingId() {
        return checkingId;
    }

    public void setCheckingId(Long checkingId) {
        this.checkingId = checkingId;
    }

    public String getCheckingType() {
        return checkingType;
    }

    public void setCheckingType(String checkingType) {
        this.checkingType = checkingType;
    }
}
