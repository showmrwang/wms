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
    /** 容器编码*/
    private String containerCode;
    /** 容器id*/
    private Long containerId;
    /**  播种墙推荐逻辑*/
    private List<RecFacilityPathCommand> rfp;
    /** 成功/失败*/
    private Boolean isSuccess;
    /** 目标位置:播种墙, 出库暂存库位, 中转库位*/
    private String targetPos;
    /** 是否扫描校验码*/
    private Boolean isScanCheckCode;

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

    public String getTargetPos() {
        return targetPos;
    }

    public void setTargetPos(String targetPos) {
        this.targetPos = targetPos;
    }

    public Boolean getIsScanCheckCode() {
        return isScanCheckCode;
    }

    public void setIsScanCheckCode(Boolean isScanCheckCode) {
        this.isScanCheckCode = isScanCheckCode;
    }

}
