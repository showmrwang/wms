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

    private Long containerId; // 小车id

    private Integer containerLatticeNo; // 货格号

    private String outboundbox; // 出库箱号

    private String seedingWallCode; // 播种墙编码

    private String turnoverBoxCode; // 周转箱

    private String outboundBoxCode; // 出库箱编码
    
    private Long outboundboxId;
    /** 出库单ID */
    private Long odoId;
    /** 出库单明细ID */
    private Long odoLineId;

    /**复合明细集合*/
    private List<WhCheckingLineCommand> checkingLineList = new ArrayList<WhCheckingLineCommand>();

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

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

    public String getOutboundbox() {
        return outboundbox;
    }

    public void setOutboundbox(String outboundbox) {
        this.outboundbox = outboundbox;
    }

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
    
    
}
