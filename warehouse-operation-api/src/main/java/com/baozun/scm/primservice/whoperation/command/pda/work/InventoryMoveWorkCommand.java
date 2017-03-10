package com.baozun.scm.primservice.whoperation.command.pda.work;

import lark.common.dao.Page;
import lark.common.dao.Pagination;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;

public class InventoryMoveWorkCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -4174816928851334322L;

    /** 功能id*/
    private Long funcId;
    /** 组织id*/
    private Long ouId;
    /** 用户id*/
    private Long userId;
    /** 系统自动推送工作:1 是, 2 不是*/
    private Boolean isAutoObtainWork;
    /** 推送条数上限*/
    private Integer maxObtainWorkQty;
    /** 获取工作方法*/
    private String obtainWorkWay;
    /** 工作列表*/
    private Pagination<WhWorkCommand> workList;
    /** 页码*/
    private Page pageInfo;
    /** 获取工作方式:扫描工作号*/
    private Boolean scanWorkCode;
    /** 获取工作方式:扫描库位号*/
    private Boolean scanLocCode;
    /** 获取工作方式:扫描容器号*/
    private Boolean scanContainerCode;
    /** 获取工作方式:扫描小批次号*/
    private Boolean scanOutBound;
    /** 获取工作方式:扫描波次号*/
    private Boolean scanWaveCode;
    /** 获取工作方式:扫描出库箱号*/
    private Boolean scanOutBoundBox;
    /** 工作号*/
    private String workCode;
    /** 库位号*/
    private String locCode;
    /** 容器号*/
    private String containerCode;
    /** 小批次号*/
    private String outBound;
    /** 波次号*/
    private String waveCode;
    /** 出库箱号*/
    private String outBoundBox;
    /** 是否是第二次进入*/
    private Boolean isSecond;

    public Long getFuncId() {
        return funcId;
    }

    public void setFuncId(Long funcId) {
        this.funcId = funcId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getIsAutoObtainWork() {
        return isAutoObtainWork;
    }

    public void setIsAutoObtainWork(Boolean isAutoObtainWork) {
        this.isAutoObtainWork = isAutoObtainWork;
    }

    public Integer getMaxObtainWorkQty() {
        return maxObtainWorkQty;
    }

    public void setMaxObtainWorkQty(Integer maxObtainWorkQty) {
        this.maxObtainWorkQty = maxObtainWorkQty;
    }

    public String getObtainWorkWay() {
        return obtainWorkWay;
    }

    public void setObtainWorkWay(String obtainWorkWay) {
        this.obtainWorkWay = obtainWorkWay;
    }

    public Pagination<WhWorkCommand> getWorkList() {
        return workList;
    }

    public void setWorkList(Pagination<WhWorkCommand> workList) {
        this.workList = workList;
    }

    public Page getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Page pageInfo) {
        this.pageInfo = pageInfo;
    }

    public Boolean getScanWorkCode() {
        return scanWorkCode;
    }

    public void setScanWorkCode(Boolean scanWorkCode) {
        this.scanWorkCode = scanWorkCode;
    }

    public Boolean getScanLocCode() {
        return scanLocCode;
    }

    public void setScanLocCode(Boolean scanLocCode) {
        this.scanLocCode = scanLocCode;
    }

    public Boolean getScanContainerCode() {
        return scanContainerCode;
    }

    public void setScanContainerCode(Boolean scanContainerCode) {
        this.scanContainerCode = scanContainerCode;
    }

    public Boolean getScanOutBound() {
        return scanOutBound;
    }

    public void setScanOutBound(Boolean scanOutBound) {
        this.scanOutBound = scanOutBound;
    }

    public Boolean getScanWaveCode() {
        return scanWaveCode;
    }

    public void setScanWaveCode(Boolean scanWaveCode) {
        this.scanWaveCode = scanWaveCode;
    }

    public Boolean getScanOutBoundBox() {
        return scanOutBoundBox;
    }

    public void setScanOutBoundBox(Boolean scanOutBoundBox) {
        this.scanOutBoundBox = scanOutBoundBox;
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getLocCode() {
        return locCode;
    }

    public void setLocCode(String locCode) {
        this.locCode = locCode;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getOutBound() {
        return outBound;
    }

    public void setOutBound(String outBound) {
        this.outBound = outBound;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public String getOutBoundBox() {
        return outBoundBox;
    }

    public void setOutBoundBox(String outBoundBox) {
        this.outBoundBox = outBoundBox;
    }

    public Boolean getIsSecond() {
        return isSecond;
    }

    public void setIsSecond(Boolean isSecond) {
        this.isSecond = isSecond;
    }

}
