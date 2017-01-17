package com.baozun.scm.primservice.whoperation.command.odo.wave;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class RecFacilityPathCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -1667784942949707497L;

    /** 批次号 */
    private String batch;
    /** 容器编码 */
    private String containerCode;
    /** 出库单ID List */
    private List<Long> odoIdList;
    /** 区域ID 不为空 默认为路径起点 */
    private Long areaId;
    /** 设备ID 不为空+设备类型不为空 默认为路径起点 */
    private Long facilityId;
    /** 设备类型 根据不用业务设置 此功能为空 */
    private String facilityType;
    /** 拣货模式 根据不同业务设置 此功能为【播种】Constants.WH_SEEDING_WALL */
    private String pickingMode;
    /** 仓库组织ID */
    private Long ouId;
    /** 是否最后一箱 true:false */
    private boolean isLastContainer;
    /** 状态 0：失败 1：成功 */
    private Integer status;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public List<Long> getOdoIdList() {
        return odoIdList;
    }

    public void setOdoIdList(List<Long> odoIdList) {
        this.odoIdList = odoIdList;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getPickingMode() {
        return pickingMode;
    }

    public void setPickingMode(String pickingMode) {
        this.pickingMode = pickingMode;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public boolean isLastContainer() {
        return isLastContainer;
    }

    public void setLastContainer(boolean isLastContainer) {
        this.isLastContainer = isLastContainer;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
