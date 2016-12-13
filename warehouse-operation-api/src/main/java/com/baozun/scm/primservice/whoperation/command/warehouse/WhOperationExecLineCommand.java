package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhOperationExecLineCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -4435843847505213278L;

    /** id*/
    private Long id;
    /** 作业ID */
    private Long operationId;
    /** 工作明细ID */
    private Long workLineId;
    /** 仓库组织ID */
    private Long ouId;
    /** 操作开始时间 */
    private Date startTime;
    /** 操作结束时间 */
    private Date finishTime;
    /** 商品ID */
    private Long skuId;
    /** 执行量/完成量 */
    private Long qty;
    /** 库存状态 */
    private Long invStatus;
    /** 库存类型 */
    private String invType;
    /** 批次号 */
    private String batchNumber;
    /** 生产日期 */
    private Date mfgDate;
    /** 失效日期 */
    private Date expDate;
    /** 最小失效日期 */
    private Date minExpDate;
    /** 最大失效日期 */
    private Date maxExpDate;
    /** 原产地 */
    private String countryOfOrigin;
    /** 库存属性1 */
    private String invAttr1;
    /** 库存属性2 */
    private String invAttr2;
    /** 库存属性3 */
    private String invAttr3;
    /** 库存属性4 */
    private String invAttr4;
    /** 库存属性5 */
    private String invAttr5;
    /** 原始库位 */
    private Long fromLocationId;
    /** 原始库位外部容器 */
    private Long fromOuterContainerId;
    /** 原始库位内部容器 */
    private Long fromInsideContainerId;
    /** 使用出库箱，耗材ID */
    private Long useOutboundboxId;
    /** 使用出库箱编码 */
    private String useOutboundboxCode;
    /** 使用容器 */
    private Long useContainerId;
    /** 使用外部容器，小车 */
    private Long useOuterContainerId;
    /** 使用货格编码数 */
    private Integer useContainerLatticeNo;
    /** 目标库位 */
    private Long toLocationId;
    /** 目标库位外部容器 */
    private Long toOuterContainerId;
    /** 目标库位内部容器 */
    private Long toInsideContainerId;
    /** 是否短拣 */
    private Boolean isShortPicking;
    /** 是否使用新的出库箱/周转箱 */
    private Boolean isUseNew;
    /** 原始使用出库箱，耗材ID */
    private Long oldOutboundboxId;
    /** 原始使用出库箱编码 */
    private String oldOutboundboxCode;
    /** 原始使用容器 */
    private Long oldContainerId;
    /** 原始使用外部容器，小车 */
    private Long oldOuterContainerId;
    /** 原始使用货格编码数 */
    private Integer oldContainerLatticeNo;
    /** 出库单ID */
    private Long odoId;
    /** 出库单明细ID */
    private Long odoLineId;
    /** 创建时间 */
    private Date createTime;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;

    /* ============================自定义============================ */

    /** 作业编码*/
    private String operationCode;
    /** 是否短拣string*/
    private Boolean isShortPickingString;
    /** 是否是否使用新的出库箱/周转箱String*/
    private Boolean isUseNewString;
    /** 原始使用容器编码*/
    private String oldContainerCode;
    /** 原始使用外部容器,小车编码*/
    private String oldOuterContainerCode;
    /** 工作号*/
    private String workCode;
    /** 工作明细号*/
    private String workLineCode;
    /** 商品code*/
    private String skuCode;
    /** 库存状态*/
    private String invStatusString;
    /** 库存类型*/
    private String invTypeString;
    /** 库存属性1*/
    private String invAttr1String;
    /** 库存属性2*/
    private String invAttr2String;
    /** 库存属性3*/
    private String invAttr3String;
    /** 库存属性4*/
    private String invAttr4String;
    /** 库存属性5*/
    private String invAttr5String;
    /** 原始库位 */
    private String fromLocation;
    /** 原始库位外部容器 */
    private String fromOuterContainerCode;
    /** 原始库位内部容器 */
    private String fromInsideContainerCode;
    /** 使用容器 */
    private String useContainerCode;
    /** 使用外部容器，小车 */
    private String useOuterContainerCode;
    /** 目标库位 */
    private String toLocation;
    /** 目标库位外部容器 */
    private String toOuterContainerCode;
    /** 目标库位内部容器 */
    private String toInsideContainerCode;
    /** 出库单code*/
    private String odoCode;
    /** 出库单明细code*/
    private String odoLineCode;
    /** 操作人*/
    private String operator;


    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public Long getWorkLineId() {
        return workLineId;
    }

    public void setWorkLineId(Long workLineId) {
        this.workLineId = workLineId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Long invStatus) {
        this.invStatus = invStatus;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Date getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Date getMinExpDate() {
        return minExpDate;
    }

    public void setMinExpDate(Date minExpDate) {
        this.minExpDate = minExpDate;
    }

    public Date getMaxExpDate() {
        return maxExpDate;
    }

    public void setMaxExpDate(Date maxExpDate) {
        this.maxExpDate = maxExpDate;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getInvAttr1() {
        return invAttr1;
    }

    public void setInvAttr1(String invAttr1) {
        this.invAttr1 = invAttr1;
    }

    public String getInvAttr2() {
        return invAttr2;
    }

    public void setInvAttr2(String invAttr2) {
        this.invAttr2 = invAttr2;
    }

    public String getInvAttr3() {
        return invAttr3;
    }

    public void setInvAttr3(String invAttr3) {
        this.invAttr3 = invAttr3;
    }

    public String getInvAttr4() {
        return invAttr4;
    }

    public void setInvAttr4(String invAttr4) {
        this.invAttr4 = invAttr4;
    }

    public String getInvAttr5() {
        return invAttr5;
    }

    public void setInvAttr5(String invAttr5) {
        this.invAttr5 = invAttr5;
    }

    public Long getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(Long fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public Long getFromOuterContainerId() {
        return fromOuterContainerId;
    }

    public void setFromOuterContainerId(Long fromOuterContainerId) {
        this.fromOuterContainerId = fromOuterContainerId;
    }

    public Long getFromInsideContainerId() {
        return fromInsideContainerId;
    }

    public void setFromInsideContainerId(Long fromInsideContainerId) {
        this.fromInsideContainerId = fromInsideContainerId;
    }

    public Long getUseOutboundboxId() {
        return useOutboundboxId;
    }

    public void setUseOutboundboxId(Long useOutboundboxId) {
        this.useOutboundboxId = useOutboundboxId;
    }

    public String getUseOutboundboxCode() {
        return useOutboundboxCode;
    }

    public void setUseOutboundboxCode(String useOutboundboxCode) {
        this.useOutboundboxCode = useOutboundboxCode;
    }

    public Long getUseContainerId() {
        return useContainerId;
    }

    public void setUseContainerId(Long useContainerId) {
        this.useContainerId = useContainerId;
    }

    public Long getUseOuterContainerId() {
        return useOuterContainerId;
    }

    public void setUseOuterContainerId(Long useOuterContainerId) {
        this.useOuterContainerId = useOuterContainerId;
    }

    public Integer getUseContainerLatticeNo() {
        return useContainerLatticeNo;
    }

    public void setUseContainerLatticeNo(Integer useContainerLatticeNo) {
        this.useContainerLatticeNo = useContainerLatticeNo;
    }

    public Long getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(Long toLocationId) {
        this.toLocationId = toLocationId;
    }

    public Long getToOuterContainerId() {
        return toOuterContainerId;
    }

    public void setToOuterContainerId(Long toOuterContainerId) {
        this.toOuterContainerId = toOuterContainerId;
    }

    public Long getToInsideContainerId() {
        return toInsideContainerId;
    }

    public void setToInsideContainerId(Long toInsideContainerId) {
        this.toInsideContainerId = toInsideContainerId;
    }

    public Boolean getIsShortPicking() {
        return isShortPicking;
    }

    public void setIsShortPicking(Boolean isShortPicking) {
        this.isShortPicking = isShortPicking;
    }

    public Boolean getIsUseNew() {
        return isUseNew;
    }

    public void setIsUseNew(Boolean isUseNew) {
        this.isUseNew = isUseNew;
    }

    public Long getOldOutboundboxId() {
        return oldOutboundboxId;
    }

    public void setOldOutboundboxId(Long oldOutboundboxId) {
        this.oldOutboundboxId = oldOutboundboxId;
    }

    public String getOldOutboundboxCode() {
        return oldOutboundboxCode;
    }

    public void setOldOutboundboxCode(String oldOutboundboxCode) {
        this.oldOutboundboxCode = oldOutboundboxCode;
    }

    public Long getOldContainerId() {
        return oldContainerId;
    }

    public void setOldContainerId(Long oldContainerId) {
        this.oldContainerId = oldContainerId;
    }

    public Long getOldOuterContainerId() {
        return oldOuterContainerId;
    }

    public void setOldOuterContainerId(Long oldOuterContainerId) {
        this.oldOuterContainerId = oldOuterContainerId;
    }

    public Integer getOldContainerLatticeNo() {
        return oldContainerLatticeNo;
    }

    public void setOldContainerLatticeNo(Integer oldContainerLatticeNo) {
        this.oldContainerLatticeNo = oldContainerLatticeNo;
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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Boolean getIsShortPickingString() {
        return isShortPickingString;
    }

    public void setIsShortPickingString(Boolean isShortPickingString) {
        this.isShortPickingString = isShortPickingString;
    }

    public Boolean getIsUseNewString() {
        return isUseNewString;
    }

    public void setIsUseNewString(Boolean isUseNewString) {
        this.isUseNewString = isUseNewString;
    }

    public String getOldOuterContainerCode() {
        return oldOuterContainerCode;
    }

    public void setOldOuterContainerCode(String oldOuterContainerCode) {
        this.oldOuterContainerCode = oldOuterContainerCode;
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getInvStatusString() {
        return invStatusString;
    }

    public void setInvStatusString(String invStatusString) {
        this.invStatusString = invStatusString;
    }

    public String getInvTypeString() {
        return invTypeString;
    }

    public void setInvTypeString(String invTypeString) {
        this.invTypeString = invTypeString;
    }

    public String getInvAttr1String() {
        return invAttr1String;
    }

    public void setInvAttr1String(String invAttr1String) {
        this.invAttr1String = invAttr1String;
    }

    public String getInvAttr2String() {
        return invAttr2String;
    }

    public void setInvAttr2String(String invAttr2String) {
        this.invAttr2String = invAttr2String;
    }

    public String getInvAttr3String() {
        return invAttr3String;
    }

    public void setInvAttr3String(String invAttr3String) {
        this.invAttr3String = invAttr3String;
    }

    public String getInvAttr4String() {
        return invAttr4String;
    }

    public void setInvAttr4String(String invAttr4String) {
        this.invAttr4String = invAttr4String;
    }

    public String getInvAttr5String() {
        return invAttr5String;
    }

    public void setInvAttr5String(String invAttr5String) {
        this.invAttr5String = invAttr5String;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getFromOuterContainerCode() {
        return fromOuterContainerCode;
    }

    public void setFromOuterContainerCode(String fromOuterContainerCode) {
        this.fromOuterContainerCode = fromOuterContainerCode;
    }

    public String getFromInsideContainerCode() {
        return fromInsideContainerCode;
    }

    public void setFromInsideContainerCode(String fromInsideContainerCode) {
        this.fromInsideContainerCode = fromInsideContainerCode;
    }

    public String getUseContainerCode() {
        return useContainerCode;
    }

    public void setUseContainerCode(String useContainerCode) {
        this.useContainerCode = useContainerCode;
    }

    public String getUseOuterContainerCode() {
        return useOuterContainerCode;
    }

    public void setUseOuterContainerCode(String useOuterContainerCode) {
        this.useOuterContainerCode = useOuterContainerCode;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getToOuterContainerCode() {
        return toOuterContainerCode;
    }

    public void setToOuterContainerCode(String toOuterContainerCode) {
        this.toOuterContainerCode = toOuterContainerCode;
    }

    public String getToInsideContainerCode() {
        return toInsideContainerCode;
    }

    public void setToInsideContainerCode(String toInsideContainerCode) {
        this.toInsideContainerCode = toInsideContainerCode;
    }

    public String getOdoCode() {
        return odoCode;
    }

    public void setOdoCode(String odoCode) {
        this.odoCode = odoCode;
    }

    public String getOdoLineCode() {
        return odoLineCode;
    }

    public void setOdoLineCode(String odoLineCode) {
        this.odoLineCode = odoLineCode;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOldContainerCode() {
        return oldContainerCode;
    }

    public void setOldContainerCode(String oldContainerCode) {
        this.oldContainerCode = oldContainerCode;
    }

    public String getWorkLineCode() {
        return workLineCode;
    }

    public void setWorkLineCode(String workLineCode) {
        this.workLineCode = workLineCode;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
