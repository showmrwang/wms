package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author shenlijun
 *
 */
public class WhSkuWhmgmtCommand extends BaseCommand {

    /** SKU_ID */
    private java.lang.Long skuId;
    /** 占零拣货位数量 */
    private java.lang.Integer occupancyZeroPickingQty;
    /** 占箱拣货位数量 */
    private java.lang.Integer occupancyBoxPickingQty;
    /** 拣货率  用于人员绩效管理 */
    private Long pickingRate;
    /** 打包率 用于人员绩效管理 */
    private Long packingRate;
    /** 特殊处理率 */
    private Long abnormalRate;
    /** 二级容器类型 */
    private java.lang.Long twoLevelType;
    /** 出库箱类型 */
    private java.lang.Long outboundCtnType;
    /** 上架规则 */
    private java.lang.Long shelfRule;
    /** 分配规则 */
    private java.lang.Long allocateRule;
    /** 码盘标准 */
    private java.lang.String codeWheelNorm;
    /** 最后盘点时间 */
    private java.util.Date lastCheckTime;
    /** 对应组织ID */
    private java.lang.Long ouId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最后修改时间 */
    private java.util.Date lastModifyTime;
    /** 操作人ID */
    private java.lang.Long operatorId;
    /** 货品类型 */
    private java.lang.Long typeOfGoods;
    
  
    
    

    public java.lang.Long getTypeOfGoods() {
        return typeOfGoods;
    }
    public void setTypeOfGoods(java.lang.Long typeOfGoods) {
        this.typeOfGoods = typeOfGoods;
    }
    public java.lang.Long getSkuId() {
        return skuId;
    }
    public void setSkuId(java.lang.Long skuId) {
        this.skuId = skuId;
    }
    public java.lang.Integer getOccupancyZeroPickingQty() {
        return occupancyZeroPickingQty;
    }
    public void setOccupancyZeroPickingQty(java.lang.Integer occupancyZeroPickingQty) {
        this.occupancyZeroPickingQty = occupancyZeroPickingQty;
    }
    public java.lang.Integer getOccupancyBoxPickingQty() {
        return occupancyBoxPickingQty;
    }
    public void setOccupancyBoxPickingQty(java.lang.Integer occupancyBoxPickingQty) {
        this.occupancyBoxPickingQty = occupancyBoxPickingQty;
    }
    public Long getPickingRate() {
        return pickingRate;
    }
    public void setPickingRate(Long pickingRate) {
        this.pickingRate = pickingRate;
    }
    public Long getPackingRate() {
        return packingRate;
    }
    public void setPackingRate(Long packingRate) {
        this.packingRate = packingRate;
    }
    public Long getAbnormalRate() {
        return abnormalRate;
    }
    public void setAbnormalRate(Long abnormalRate) {
        this.abnormalRate = abnormalRate;
    }
    public java.lang.Long getTwoLevelType() {
        return twoLevelType;
    }
    public void setTwoLevelType(java.lang.Long twoLevelType) {
        this.twoLevelType = twoLevelType;
    }
    public java.lang.Long getOutboundCtnType() {
        return outboundCtnType;
    }
    public void setOutboundCtnType(java.lang.Long outboundCtnType) {
        this.outboundCtnType = outboundCtnType;
    }
    public java.lang.Long getShelfRule() {
        return shelfRule;
    }
    public void setShelfRule(java.lang.Long shelfRule) {
        this.shelfRule = shelfRule;
    }
    public java.lang.Long getAllocateRule() {
        return allocateRule;
    }
    public void setAllocateRule(java.lang.Long allocateRule) {
        this.allocateRule = allocateRule;
    }
    public java.lang.String getCodeWheelNorm() {
        return codeWheelNorm;
    }
    public void setCodeWheelNorm(java.lang.String codeWheelNorm) {
        this.codeWheelNorm = codeWheelNorm;
    }
    public java.util.Date getLastCheckTime() {
        return lastCheckTime;
    }
    public void setLastCheckTime(java.util.Date lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }
    public java.lang.Long getOuId() {
        return ouId;
    }
    public void setOuId(java.lang.Long ouId) {
        this.ouId = ouId;
    }
    public java.util.Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }
    public java.util.Date getLastModifyTime() {
        return lastModifyTime;
    }
    public void setLastModifyTime(java.util.Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
    public java.lang.Long getOperatorId() {
        return operatorId;
    }
    public void setOperatorId(java.lang.Long operatorId) {
        this.operatorId = operatorId;
    }
    
    
    
}
