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

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author 出库单明细对应出库箱/容器表
 *
 */
public class WhOdoOutBoundBox extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -8239815726779979217L;

    //columns START
    
    /** 波次ID */
    private Long waveId;
    /** 出库单ID */
    private Long odoId;
    /** 出库单明细ID */
    private Long odoLineId;
    /** 出库箱类型ID */
    private Long outbounxboxTypeId;
    /** 出库箱编码 */
    private String outbounxboxTypeCode;
    /** 容器ID */
    private Long containerId;
    /** 外部容器ID 小车此类容器 */
    private Long outerContainerId;
    /** 货格编码数 */
    private Integer containerLatticeNo;
    /** 数量 */
    private Double qty;
    /** 批次 */
    private String boxBatch;
    /** 整托整箱：1整托 2整箱 */
    private Integer wholeCase;
    /** 仓库组织ID */
    private Long ouId;
    /** 是否已创建工作 */
    private Boolean isCreateWork;
    /** 创建时间 */
    private Date createTime;
    /** 修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;
    
    //columns END
    
    public Long getWaveId() {
        return waveId;
    }
    public void setWaveId(Long waveId) {
        this.waveId = waveId;
    }
    public Long getOdoId() {
        return odoId;
    }
    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }
    public Long getOdoLineId() {
        return odoLineId;
    }
    public void setOdoLineId(Long odoLineId) {
        this.odoLineId = odoLineId;
    }
    public Long getOutbounxboxTypeId() {
        return outbounxboxTypeId;
    }
    public void setOutbounxboxTypeId(Long outbounxboxTypeId) {
        this.outbounxboxTypeId = outbounxboxTypeId;
    }
    public String getOutbounxboxTypeCode() {
        return outbounxboxTypeCode;
    }
    public void setOutbounxboxTypeCode(String outbounxboxTypeCode) {
        this.outbounxboxTypeCode = outbounxboxTypeCode;
    }
    public Long getContainerId() {
        return containerId;
    }
    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }
    public Long getOuterContainerId() {
        return outerContainerId;
    }
    public void setOuterContainerId(Long outerContainerId) {
        this.outerContainerId = outerContainerId;
    }

    public Integer getContainerLatticeNo() {
        return containerLatticeNo;
    }

    public void setContainerLatticeNo(Integer containerLatticeNo) {
        this.containerLatticeNo = containerLatticeNo;
    }

    public Double getQty() {
        return qty;
    }
    public void setQty(Double qty) {
        this.qty = qty;
    }
    public String getBoxBatch() {
        return boxBatch;
    }
    public void setBoxBatch(String boxBatch) {
        this.boxBatch = boxBatch;
    }
    public Integer getWholeCase() {
        return wholeCase;
    }
    public void setWholeCase(Integer wholeCase) {
        this.wholeCase = wholeCase;
    }
    public Long getOuId() {
        return ouId;
    }
    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    public Boolean getIsCreateWork() {
        return isCreateWork;
    }
    public void setIsCreateWork(Boolean isCreateWork) {
        this.isCreateWork = isCreateWork;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getLastModifyTime() {
        return lastModifyTime;
    }
    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
    public Long getOperatorId() {
        return operatorId;
    }
    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
    
}
