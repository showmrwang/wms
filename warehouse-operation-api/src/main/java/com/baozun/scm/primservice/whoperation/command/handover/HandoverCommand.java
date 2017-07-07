package com.baozun.scm.primservice.whoperation.command.handover;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


public class HandoverCommand extends BaseModel {

    /**
     * 
     */

    private static final long serialVersionUID = -2944916408396402201L;
    // columns START
    /** 交接工位ID */
    private Long handoverStationId;
    /** 交接工位编码 */
    private String handoverStationCode;
    /** 交接工位类型：仓库交接工位，复核台组交接工位 */
    private String handoverStationType;
    /** 交接批次 */
    private String handoverBatch;
    /** 交接分组条件 */
    private String groupCondition;
    /** 总出库箱数 */
    private Integer totalBox;
    /** 客户CODE */
    private String customerCode;
    /** 客户名称 */
    private String customerName;
    /** 店铺CODE */
    private String storeCode;
    /** 店铺名称 */
    private String storeName;
    /** 运输服务商CODE */
    private String transportCode;
    /** 运输服务商名称 */
    private String transportName;
    /** 总计重 */
    private Double totalCalcWeight;
    /** 总称重 */
    private Double totalActualWeight;
    /** 对应组织ID */
    private Long ouId;
    /** 创建人ID */
    private Long createId;
    /** 创建人 */
    private String userName;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 修改人ID */
    private Long modifiedId;
    /** 出库箱号 */
    private String outboundboxCode;

    // columns END

    public Long getHandoverStationId() {
        return handoverStationId;
    }

    public String getOutboundboxCode() {
        return outboundboxCode;
    }

    public void setOutboundboxCode(String outboundboxCode) {
        this.outboundboxCode = outboundboxCode;
    }

    public String getHandoverStationCode() {
        return handoverStationCode;
    }

    public void setHandoverStationCode(String handoverStationCode) {
        this.handoverStationCode = handoverStationCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getTotalBox() {
        return totalBox;
    }

    public void setTotalBox(Integer totalBox) {
        this.totalBox = totalBox;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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

    public Double getTotalCalcWeight() {
        return totalCalcWeight;
    }

    public void setTotalCalcWeight(Double totalCalcWeight) {
        this.totalCalcWeight = totalCalcWeight;
    }

    public Double getTotalActualWeight() {
        return totalActualWeight;
    }

    public void setTotalActualWeight(Double totalActualWeight) {
        this.totalActualWeight = totalActualWeight;
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

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public void setHandoverStationType(String handoverStationType) {
        this.handoverStationType = handoverStationType;
    }

    public String getHandoverStationType() {
        return handoverStationType;
    }

    public void setHandoverStationId(Long handoverStationId) {
        this.handoverStationId = handoverStationId;
    }


    public String getHandoverBatch() {
        return handoverBatch;
    }

    public void setHandoverBatch(String handoverBatch) {
        this.handoverBatch = handoverBatch;
    }

    public String getGroupCondition() {
        return groupCondition;
    }

    public void setGroupCondition(String groupCondition) {
        this.groupCondition = groupCondition;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
}
