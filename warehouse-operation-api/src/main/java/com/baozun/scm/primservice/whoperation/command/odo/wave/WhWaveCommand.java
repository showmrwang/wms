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
package com.baozun.scm.primservice.whoperation.command.odo.wave;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;

public class WhWaveCommand extends BaseCommand {

    private static final long serialVersionUID = 5705617664033075065L;

    /** id */
    private Long id;
    /** 波次主档编码 */
    private String code;
    /** 波次状态，系统常量 */
    private String status;
    /** 仓库组织ID */
    private Long ouId;
    /** 波次主档ID */
    private Long waveMasterId;
    /** 波次阶段，系统常量 */
    private String phaseCode;
    /** 硬分配阶段0：分配规则1：硬分配 */
    private Integer allocatePhase;
    /** 开始运行时间 */
    private java.util.Date startTime;
    /** 结束运行时间 */
    private java.util.Date finishTime;
    /** 出库单总单数 */
    private Integer totalOdoQty;
    /** 出库单明细总行数 */
    private Integer totalOdoLineQty;
    /** 总金额 */
    private Long totalAmount;
    /** 总体积 */
    private Long totalVolume;
    /** 总重量 */
    private Long totalWeight;
    /** 商品总件数 */
    private Integer totalSkuQty;
    /** 商品种类数 */
    private Integer skuCategoryQty;
    /** 工作总单数 */
    private Integer execOdoQty;
    /** 工作总行数 */
    private Integer execOdoLineQty;
    /** 出库箱总数 */
    private Integer outboundCartonQty;
    /** 创建时间 */
    private Date createTime;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 操作人ID */
    private Long modifiedId;
    /** 是否启用 1:启用 0:停用 */
    private Integer lifecycle;

    // 自定义字段
    private List<WhWaveLineCommand> waveLineCommandList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getWaveMasterId() {
        return waveMasterId;
    }

    public void setWaveMasterId(Long waveMasterId) {
        this.waveMasterId = waveMasterId;
    }

    public String getPhaseCode() {
        return phaseCode;
    }

    public void setPhaseCode(String phaseCode) {
        this.phaseCode = phaseCode;
    }

    public Integer getAllocatePhase() {
        return allocatePhase;
    }

    public void setAllocatePhase(Integer allocatePhase) {
        this.allocatePhase = allocatePhase;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getTotalOdoQty() {
        return totalOdoQty;
    }

    public void setTotalOdoQty(Integer totalOdoQty) {
        this.totalOdoQty = totalOdoQty;
    }

    public Integer getTotalOdoLineQty() {
        return totalOdoLineQty;
    }

    public void setTotalOdoLineQty(Integer totalOdoLineQty) {
        this.totalOdoLineQty = totalOdoLineQty;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Long totalVolume) {
        this.totalVolume = totalVolume;
    }

    public Long getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Long totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Integer getTotalSkuQty() {
        return totalSkuQty;
    }

    public void setTotalSkuQty(Integer totalSkuQty) {
        this.totalSkuQty = totalSkuQty;
    }

    public Integer getSkuCategoryQty() {
        return skuCategoryQty;
    }

    public void setSkuCategoryQty(Integer skuCategoryQty) {
        this.skuCategoryQty = skuCategoryQty;
    }

    public Integer getExecOdoQty() {
        return execOdoQty;
    }

    public void setExecOdoQty(Integer execOdoQty) {
        this.execOdoQty = execOdoQty;
    }

    public Integer getExecOdoLineQty() {
        return execOdoLineQty;
    }

    public void setExecOdoLineQty(Integer execOdoLineQty) {
        this.execOdoLineQty = execOdoLineQty;
    }

    public Integer getOutboundCartonQty() {
        return outboundCartonQty;
    }

    public void setOutboundCartonQty(Integer outboundCartonQty) {
        this.outboundCartonQty = outboundCartonQty;
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

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public List<WhWaveLineCommand> getWaveLineCommandList() {
        return waveLineCommandList;
    }

    public void setWaveLineCommandList(List<WhWaveLineCommand> waveLineCommandList) {
        this.waveLineCommandList = waveLineCommandList;
    }
}
