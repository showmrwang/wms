package com.baozun.scm.primservice.whoperation.command.warehouse;


import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class AreaCommand extends BaseCommand{

    /**
     * 
     */
    private static final long serialVersionUID = -191043999806350496L;

    /**
     * 区域id
     */
    private Long areaId;
    /** 
     * 仓库id
     */
    private Long ouId;
    /** 
     * 仓库名称 
     */
    private String ouName;
    /**
     * 区域编码
     */
    private String areaCode;
    /**
     * 区域名称
     */
    private String areaName;
    /**
     * 区域类型
     */
    private String areaType;
    /**
     * 生命周期
     */
    private Integer lifecycle;


    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getOuName() {
        return ouName;
    }

    public void setOuName(String ouName) {
        this.ouName = ouName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

}
