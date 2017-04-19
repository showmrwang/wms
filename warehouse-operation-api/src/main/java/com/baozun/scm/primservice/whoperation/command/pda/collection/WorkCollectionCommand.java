package com.baozun.scm.primservice.whoperation.command.pda.collection;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.RecFacilityPathCommand;

public class WorkCollectionCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -1578747439528886063L;

    /** ouId*/
    private Long ouId;
    /** 批次号*/
    private String batch;
    /** 容器名称*/
    private String containerName;
    /** 容器编码*/
    private String containerCode;
    /** 输入容器编码*/
    private String inputContainerCode;
    /** 容器id*/
    private Long containerId;
    @Deprecated
    /**  播种墙推荐逻辑*/
    private List<RecFacilityPathCommand> rfp;
    /** 成功/失败*/
    private Boolean isSuccess;
    /** 输入目标位置:播种墙, 出库暂存库位, 中转库位*/
    private String inputTargetPos;
    /** 目标位置名称*/
    private String targetPosName;
    /** 目标位置编码*/
    private String targetPosCode;
    /** 是否扫描校验码*/
    private Boolean isScanCheckCode;
    /** 推荐播种墙序号:默认为0*/
    private Integer index = 0;
    /** 目标位置类型: 1.播种墙; 2.暂存库位; 3.中转库位*/
    private Integer targetType;
    @Deprecated
    /** 设施id*/
    private Long facilityId;
    /** 操作员id*/
    private Long userId;
    /** 功能id*/
    private Long funcId;
    /** 是否是最后一箱*/
    private Boolean isLastContainer;
    /** 拣货容器列表*/
    private List<String> containerList;
    /** 拣货容器id列表*/
    private List<Long> containerIdList;
    /** 出库单列表*/
    private List<Long> odoIdList;
    /** 工作编码*/
    private String workCode;
    /** 是否执行补偿机制*/
    private Boolean isCompensation = true;
    /** 是否是推荐播种墙逻辑[推荐播种墙和拣货集货用一套逻辑]*/
    private Boolean isRecPath = true;
    /** 是否重新执行推荐逻辑[补偿机制]*/
    private Boolean restart = false;
    /** 携带数量*/
    private Integer carryQty = 0;
    /** 单次最多携带容器数量*/
    private Integer containerQty;
    /** 播种墙容量上限*/
    private Integer upperLimit;
    /** 工作id*/
    private Long workId;
    /** 最后一个容器库位*/
    private Long lastLocationId;
    /** 区域id*/
    private Long areaId;

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public List<RecFacilityPathCommand> getRfp() {
        return rfp;
    }

    public void setRfp(List<RecFacilityPathCommand> rfp) {
        this.rfp = rfp;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public Boolean getIsScanCheckCode() {
        return isScanCheckCode;
    }

    public void setIsScanCheckCode(Boolean isScanCheckCode) {
        this.isScanCheckCode = isScanCheckCode;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getTargetPosName() {
        return targetPosName;
    }

    public void setTargetPosName(String targetPosName) {
        this.targetPosName = targetPosName;
    }

    public String getTargetPosCode() {
        return targetPosCode;
    }

    public void setTargetPosCode(String targetPosCode) {
        this.targetPosCode = targetPosCode;
    }

    public String getInputContainerCode() {
        return inputContainerCode;
    }

    public void setInputContainerCode(String inputContainerCode) {
        this.inputContainerCode = inputContainerCode;
    }

    public String getInputTargetPos() {
        return inputTargetPos;
    }

    public void setInputTargetPos(String inputTargetPos) {
        this.inputTargetPos = inputTargetPos;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFuncId() {
        return funcId;
    }

    public void setFuncId(Long funcId) {
        this.funcId = funcId;
    }

    public Boolean getIsLastContainer() {
        return isLastContainer;
    }

    public void setIsLastContainer(Boolean isLastContainer) {
        this.isLastContainer = isLastContainer;
    }

    public List<String> getContainerList() {
        return containerList;
    }

    public void setContainerList(List<String> containerList) {
        this.containerList = containerList;
    }

    public List<Long> getOdoIdList() {
        return odoIdList;
    }

    public void setOdoIdList(List<Long> odoIdList) {
        this.odoIdList = odoIdList;
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public Boolean getIsCompensation() {
        return isCompensation;
    }

    public void setIsCompensation(Boolean isCompensation) {
        this.isCompensation = isCompensation;
    }

    public Boolean getIsRecPath() {
        return isRecPath;
    }

    public void setIsRecPath(Boolean isRecPath) {
        this.isRecPath = isRecPath;
    }

    public Boolean getRestart() {
        return restart;
    }

    public void setRestart(Boolean restart) {
        this.restart = restart;
    }

    public List<Long> getContainerIdList() {
        return containerIdList;
    }

    public void setContainerIdList(List<Long> containerIdList) {
        this.containerIdList = containerIdList;
    }

    public Integer getCarryQty() {
        return carryQty;
    }

    public void setCarryQty(Integer carryQty) {
        this.carryQty = carryQty;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Integer getContainerQty() {
        return containerQty;
    }

    public void setContainerQty(Integer containerQty) {
        this.containerQty = containerQty;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getLastLocationId() {
        return lastLocationId;
    }

    public void setLastLocationId(Long lastLocationId) {
        this.lastLocationId = lastLocationId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

}
