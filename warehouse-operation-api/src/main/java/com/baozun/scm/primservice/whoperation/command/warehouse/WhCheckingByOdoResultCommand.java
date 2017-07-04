package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.ArrayList;
import java.util.List;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class WhCheckingByOdoResultCommand extends BaseModel {



    /**
     * 
     */
    private static final long serialVersionUID = -5186248066969592245L;

    private Long ouId;

    private String contaierCode;

    private Integer containerLatticeNo; // 货格号

    // private String outboundbox; // 出库箱号

    private String seedingWallCode; // 播种墙编码

    private String turnoverBoxCode; // 周转箱

    private String outboundBoxCode; // 出库箱编码

    private Long outboundboxId; // 耗材id
    /** 出库单ID */
    private Long odoId;
    /** 出库单明细ID */
    private Long odoLineId;
    /** 按单复核模式类型*/
    private String checkingPattern;
    /** 复核完成*/
    private Boolean checkFinish;
    private Long functionId;
    /** 运单号*/
    private String waybillCode;

    private Long userId;
    /**复合明细集合*/
    private List<WhCheckingLineCommand> checkingLineList = new ArrayList<WhCheckingLineCommand>();
    /** sn列表*/
    private List<String> sn;

    private Long facilityId;
    /** 库位id*/
    private Long locationId;

    private Boolean isScanWaybillCode;

    /** 页面提示信息*/
    private String message;
    /** 面单号类型*/
    private String waybillType;



    public Integer getContainerLatticeNo() {
        return containerLatticeNo;
    }

    public void setContainerLatticeNo(Integer containerLatticeNo) {
        this.containerLatticeNo = containerLatticeNo;
    }

    public String getSeedingWallCode() {
        return seedingWallCode;
    }

    public void setSeedingWallCode(String seedingWallCode) {
        this.seedingWallCode = seedingWallCode;
    }

    public String getTurnoverBoxCode() {
        return turnoverBoxCode;
    }

    public void setTurnoverBoxCode(String turnoverBoxCode) {
        this.turnoverBoxCode = turnoverBoxCode;
    }

    public List<WhCheckingLineCommand> getCheckingLineList() {
        return checkingLineList;
    }

    public void setCheckingLineList(List<WhCheckingLineCommand> checkingLineList) {
        this.checkingLineList = checkingLineList;
    }

    // public String getOutboundbox() {
    // return outboundbox;
    // }
    //
    // public void setOutboundbox(String outboundbox) {
    // this.outboundbox = outboundbox;
    // }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getOutboundBoxCode() {
        return outboundBoxCode;
    }

    public void setOutboundBoxCode(String outboundBoxCode) {
        this.outboundBoxCode = outboundBoxCode;
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

    public Long getOutboundboxId() {
        return outboundboxId;
    }

    public void setOutboundboxId(Long outboundboxId) {
        this.outboundboxId = outboundboxId;
    }

    public String getCheckingPattern() {
        return checkingPattern;
    }

    public void setCheckingPattern(String checkingPattern) {
        this.checkingPattern = checkingPattern;
    }

    public String getContaierCode() {
        return contaierCode;
    }

    public void setContaierCode(String contaierCode) {
        this.contaierCode = contaierCode;
    }



    public Boolean getCheckFinish() {
        return checkFinish;
    }

    public void setCheckFinish(Boolean checkFinish) {
        this.checkFinish = checkFinish;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public List<String> getSn() {
        return sn;
    }

    public void setSn(List<String> sn) {
        this.sn = sn;
    }


    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Boolean getIsScanWaybillCode() {
        return isScanWaybillCode;
    }

    public void setIsScanWaybillCode(Boolean isScanWaybillCode) {
        this.isScanWaybillCode = isScanWaybillCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWaybillType() {
        return waybillType;
    }

    public void setWaybillType(String waybillType) {
        this.waybillType = waybillType;
    }

}
