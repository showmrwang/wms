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

/***
 * 播种功能
 * 
 * @author bin.hu
 *
 */
public class WhFunctionSeedingWall extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = -7885797660360132521L;

    /** 对应功能ID */
    private Long functionId;
    /** 扫描模式 逐件扫描 数量扫描 默认数量扫描 */
    private Integer scanPattern;
    /** 播种模式-单一货格 多货格 */
    private Integer seedingWallPattern;
    /** 是否扫描货格号 */
    private Boolean isScanGoodsLattice;
    /** 显示单号 */
    private String showCode;
    /** 组织仓库ID */
    private Long ouId;

    public Long getFunctionId() {
        return this.functionId;
    }

    public void setFunctionId(Long value) {
        this.functionId = value;
    }

    public Integer getScanPattern() {
        return this.scanPattern;
    }

    public void setScanPattern(Integer value) {
        this.scanPattern = value;
    }

    public Integer getSeedingWallPattern() {
        return this.seedingWallPattern;
    }

    public void setSeedingWallPattern(Integer value) {
        this.seedingWallPattern = value;
    }

    public Boolean getIsScanGoodsLattice() {
        return this.isScanGoodsLattice;
    }

    public void setIsScanGoodsLattice(Boolean value) {
        this.isScanGoodsLattice = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public String getShowCode() {
        return showCode;
    }

    public void setShowCode(String showCode) {
        this.showCode = showCode;
    }

}
