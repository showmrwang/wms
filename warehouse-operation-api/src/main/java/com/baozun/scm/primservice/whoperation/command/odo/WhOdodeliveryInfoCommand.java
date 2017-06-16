package com.baozun.scm.primservice.whoperation.command.odo;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhOdodeliveryInfoCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -5367784359837908314L;
    private Long id;
    /** 出库单ID */
    private Long odoId;
    /** 运输服务商编码 */
    private String transportCode;
    /** 运输服务商名称 */
    private String transportName;
    /** 运单号 */
    private String waybillCode;
    /** 出库箱ID */
    private Long outboundboxId;
    /** 出库箱号 */
    private String outboundboxCode;
    /** 产品类型编码 */
    private String serviceCode;
    /** 产品类型名称 */
    private String serviceName;
    /** 快递增值服务名称 */
    private String transportvasName;
    /** 快递增值服务编码 */
    private String transportvasCode;
    /** 快递时效类型名称 */
    private String ttName;
    /** 快递时效类型编码 */
    private String ttCode;
    /** 仓库增值服务名称 */
    private String vasName;
    /** 仓库增值服务编码 */
    private String vasCode;
    /** 创建人ID */
    private Long createId;
    /** 创建时间 */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransportvasName() {
        return transportvasName;
    }

    public void setTransportvasName(String transportvasName) {
        this.transportvasName = transportvasName;
    }

    public String getTransportvasCode() {
        return transportvasCode;
    }

    public void setTransportvasCode(String transportvasCode) {
        this.transportvasCode = transportvasCode;
    }

    public String getVasName() {
        return vasName;
    }

    public void setVasName(String vasName) {
        this.vasName = vasName;
    }

    public String getVasCode() {
        return vasCode;
    }

    public void setVasCode(String vasCode) {
        this.vasCode = vasCode;
    }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
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

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


    public String getTtName() {
        return ttName;
    }

    public void setTtName(String ttName) {
        this.ttName = ttName;
    }

    public String getTtCode() {
        return ttCode;
    }

    public void setTtCode(String ttCode) {
        this.ttCode = ttCode;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
