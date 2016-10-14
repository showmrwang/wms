package com.baozun.scm.primservice.whoperation.command.odo.wave;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WaveCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = -8139029469287478053L;
    // columns START
    /** 波次主档编码 */
    private String code;
    /** 波次状态，系统常量 */
    private Integer status;
    /** 仓库组织ID */
    private Long ouId;
    /** 波次主档ID */
    private Long waveMasterId;
    /** 波次阶段，系统配置参数 */
    private String phaseCode;
    /** isWeakAllocated */
    private Boolean isWeakAllocated;
    /** 硬分配阶段0：分配规则1：硬分配 默认为0 */
    private Integer allocatePhase;
    /** 开始运行时间 */
    private Date startTime;
    /** 结束运行时间 */
    private Date finishTime;
    /** 出库单总单数 */
    private Integer totalOdoQty;
    /** 出库单明细总行数 */
    private Integer totalOdoLineQty;
    /** 总金额 */
    private Double totalAmount;
    /** 总体积 */
    private Double totalVolume;
    /** 总重量 */
    private Double totalWeight;
    /** 商品总件数 */
    private Double totalSkuQty;
    /** 商品种类数 */
    private Integer skuCategoryQty;
    /** 工作总单数 */
    private Integer execOdoQty;
    /** 工作总行数 */
    private Integer execOdoLineQty;
    /** 出库箱总数 */
    private Integer outboundCartonQty;
    /** 是否系统异常0：否1：是 */
    private Boolean isError;
    /** 异常次数 */
    private Integer errorCount;
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
    /** 是否运行波次 0：否 1：是 */
    private Boolean isRunWave;

    // --------------------波次一览查询条件----------------------------
    private String searchWaveCode;
    private String searchWaveMasterName;
    private String searchWaveMaseterCode;
    private List<String> searchWaveStatus;
    private List<String> searchWavePhase;
    private String searchCreateName;
    private String searchCreateTimeStart;
    private String searchCreateTimeEnd;

    // -----------------波次查询结果------------------
    private String createName;
    private String finishTimeStr;
    private String startTimeStr;
    private String statusName;


    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getFinishTimeStr() {
        return finishTimeStr;
    }

    public void setFinishTimeStr(String finishTimeStr) {
        this.finishTimeStr = finishTimeStr;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
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
    public Boolean getIsWeakAllocated() {
        return isWeakAllocated;
    }
    public void setIsWeakAllocated(Boolean isWeakAllocated) {
        this.isWeakAllocated = isWeakAllocated;
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
    public Double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public Double getTotalVolume() {
        return totalVolume;
    }
    public void setTotalVolume(Double totalVolume) {
        this.totalVolume = totalVolume;
    }
    public Double getTotalWeight() {
        return totalWeight;
    }
    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }
    public Double getTotalSkuQty() {
        return totalSkuQty;
    }
    public void setTotalSkuQty(Double totalSkuQty) {
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
    public Boolean getIsError() {
        return isError;
    }
    public void setIsError(Boolean isError) {
        this.isError = isError;
    }
    public Integer getErrorCount() {
        return errorCount;
    }
    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
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
    public Boolean getIsRunWave() {
        return isRunWave;
    }
    public void setIsRunWave(Boolean isRunWave) {
        this.isRunWave = isRunWave;
    }

    public String getSearchWaveCode() {
        return searchWaveCode;
    }

    public void setSearchWaveCode(String searchWaveCode) {
        this.searchWaveCode = searchWaveCode;
    }

    public String getSearchWaveMasterName() {
        return searchWaveMasterName;
    }

    public void setSearchWaveMasterName(String searchWaveMasterName) {
        this.searchWaveMasterName = searchWaveMasterName;
    }

    public String getSearchWaveMaseterCode() {
        return searchWaveMaseterCode;
    }

    public void setSearchWaveMaseterCode(String searchWaveMaseterCode) {
        this.searchWaveMaseterCode = searchWaveMaseterCode;
    }

    public List<String> getSearchWaveStatus() {
        return searchWaveStatus;
    }

    public void setSearchWaveStatus(List<String> searchWaveStatus) {
        this.searchWaveStatus = searchWaveStatus;
    }

    public List<String> getSearchWavePhase() {
        return searchWavePhase;
    }

    public void setSearchWavePhase(List<String> searchWavePhase) {
        this.searchWavePhase = searchWavePhase;
    }

    public String getSearchCreateName() {
        return searchCreateName;
    }

    public void setSearchCreateName(String searchCreateName) {
        this.searchCreateName = searchCreateName;
    }

    public String getSearchCreateTimeStart() {
        return searchCreateTimeStart;
    }

    public void setSearchCreateTimeStart(String searchCreateTimeStart) {
        this.searchCreateTimeStart = searchCreateTimeStart;
    }

    public String getSearchCreateTimeEnd() {
        return searchCreateTimeEnd;
    }

    public void setSearchCreateTimeEnd(String searchCreateTimeEnd) {
        this.searchCreateTimeEnd = searchCreateTimeEnd;
    }
    
}
