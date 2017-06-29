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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.baozun.scm.primservice.whoperation.model.BaseModel;
/**
 * 
 * @author feng.hu
 *
 */
public class WhFunctionContainerMove extends BaseModel {

    private static final long serialVersionUID = 1922898554282499359L;
    
    //columns START
	/** 对应功能ID */
	private Long functionId;
	/** 移动模式  1整箱移动 2部分移动 默认整箱移动 */
	private Integer movePattern;
	/** 是否提示扫描商品(整箱移动模式) */
	private Boolean isScanSku;
	/** 扫描模式  1数量扫描 2逐件扫描 默认数量扫描 */
	private Integer scanPattern;
	/** 是否提示商品库存属性(整箱移动模式) */
	private Boolean isTipInvAttr;
	/** 是否扫描商品库存属性(整箱移动模式) */
	private Boolean isScanInvAttr;
	/** 是否自动打印箱标签 */
	private Boolean isPrintCartonLabel;
	/** 对应组织ID */
	private Long ouId;
	//columns END

	public WhFunctionContainerMove() {}

	public WhFunctionContainerMove(Long id) {
		this.id = id;
	}

    public Long getFunctionId() {
        return functionId;
    }

    public Integer getMovePattern() {
        return movePattern;
    }

    public Boolean getIsScanSku() {
        return isScanSku;
    }

    public Integer getScanPattern() {
        return scanPattern;
    }

    public Boolean getIsTipInvAttr() {
        return isTipInvAttr;
    }

    public Boolean getIsScanInvAttr() {
        return isScanInvAttr;
    }

    public Boolean getIsPrintCartonLabel() {
        return isPrintCartonLabel;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public void setMovePattern(Integer movePattern) {
        this.movePattern = movePattern;
    }

    public void setIsScanSku(Boolean isScanSku) {
        this.isScanSku = isScanSku;
    }

    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }

    public void setIsTipInvAttr(Boolean isTipInvAttr) {
        this.isTipInvAttr = isTipInvAttr;
    }

    public void setIsScanInvAttr(Boolean isScanInvAttr) {
        this.isScanInvAttr = isScanInvAttr;
    }

    public void setIsPrintCartonLabel(Boolean isPrintCartonLabel) {
        this.isPrintCartonLabel = isPrintCartonLabel;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
        .append("Id",getId())       
        .append("FunctionId",getFunctionId())       
        .append("MovePattern",getMovePattern())     
        .append("IsScanSku",getIsScanSku())     
        .append("ScanPattern",getScanPattern())     
        .append("IsTipInvAttr",getIsTipInvAttr())       
        .append("IsScanInvAttr",getIsScanInvAttr())     
        .append("IsPrintCartonLabel",getIsPrintCartonLabel())       
        .append("OuId",getOuId())       
            .toString();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
        .append(getId())
        .append(getFunctionId())
        .append(getMovePattern())
        .append(getIsScanSku())
        .append(getScanPattern())
        .append(getIsTipInvAttr())
        .append(getIsScanInvAttr())
        .append(getIsPrintCartonLabel())
        .append(getOuId())
            .toHashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof WhFunctionContainerMove == false) return false;
        if(this == obj) return true;
        WhFunctionContainerMove other = (WhFunctionContainerMove)obj;
        return new EqualsBuilder()
        .append(getId(),other.getId())

        .append(getFunctionId(),other.getFunctionId())

        .append(getMovePattern(),other.getMovePattern())

        .append(getIsScanSku(),other.getIsScanSku())

        .append(getScanPattern(),other.getScanPattern())

        .append(getIsTipInvAttr(),other.getIsTipInvAttr())

        .append(getIsScanInvAttr(),other.getIsScanInvAttr())

        .append(getIsPrintCartonLabel(),other.getIsPrintCartonLabel())

        .append(getOuId(),other.getOuId())

            .isEquals();
    }

}

