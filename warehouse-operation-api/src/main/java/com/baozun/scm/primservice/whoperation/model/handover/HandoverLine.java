package com.baozun.scm.primservice.whoperation.model.handover;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class HandoverLine extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 7120313327707091458L;
    // columns START
    /** 交接头ID */
    private Long handoverId;
    /** 耗材ID */
    private Long outboundboxId;
    /** 出库箱编码 */
    private String outboundboxCode;
    /** 出库箱信息ID */
    private String whOutboundboxId;
    /** 对应组织ID */
    private Long ouId;

    // columns END
    public Long getHandoverId() {
        return handoverId;
    }

    public void setHandoverId(Long handoverId) {
        this.handoverId = handoverId;
    }

    public Long getOutboundboxId() {
        return outboundboxId;
    }

    public void setOutboundboxId(Long outboundboxId) {
        this.outboundboxId = outboundboxId;
    }

    public String getOutboundboxCode() {
        return outboundboxCode;
    }

    public void setOutboundboxCode(String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }

    public String getWhOutboundboxId() {
        return whOutboundboxId;
    }

    public void setWhOutboundboxId(String whOutboundboxId) {
        this.whOutboundboxId = whOutboundboxId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
}
