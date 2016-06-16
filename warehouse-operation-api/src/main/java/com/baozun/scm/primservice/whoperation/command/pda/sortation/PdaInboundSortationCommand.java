package com.baozun.scm.primservice.whoperation.command.pda.sortation;

import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * PDA入库分拣 传参Command
 * 
 * @author bin.hu
 * 
 */
public class PdaInboundSortationCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 8299948815637617499L;

    /** 仓库组织ID */
    private Long ouId;

    /** 操作人ID */
    private Long userId;

    /** 容器号 */
    private String containerCode;

    /** 目标容器号 */
    private String targetContainerCode;

    /** 容器ID */
    private Long containerId;

    /** 目标容器ID */
    private Long newContainerId;

    /** 规则ID */
    private Long ruleId;

    /** 功能ID */
    private Long inboundId;

    /** 商品SKU */
    private String skuBarCode;

    /** 商品ID */
    private Long skuId;

    /** 库存ID */
    private Long skuInvId;

    /** 移入商品数量 */
    private Double shiftInQty;

    /** 移出商品数量 */
    private Double shiftOutQty;

    /** 系统提示目标容器号 */
    private String targetContainerCodeSelect;

    /** sn号 */
    private String snCode;

    /** uuid */
    private String uuid;

    /** 是否装满 */
    private Boolean isFull;

    /** 此原始容器号所有完成所有商品分拣 */
    private Boolean isSortationDone = false;

    /** 生产日期 String */
    private String mfgDateStr;

    /** 失效日期 String */
    private String expDateStr;

    /** ===============库存属性Map=================== */
    // 原产地
    private Map<String, String> cooMap;
    // 生产日期
    private Map<String, String> mfgMap;
    // 失效日期
    private Map<String, String> expMap;
    // 批次号
    private Map<String, String> batchNumMap;
    // 库存属性1
    private Map<String, String> invAttr1Map;
    // 库存属性2
    private Map<String, String> invAttr2Map;
    // 库存属性3
    private Map<String, String> invAttr3Map;
    // 库存属性4
    private Map<String, String> invAttr4Map;
    // 库存属性5
    private Map<String, String> invAttr5Map;
    // 库存类型
    private Map<String, String> invTypeMap;
    // 库存状态
    private Map<Integer, String> invStatusMap;
    /** ===============库存属性Map=================== */

    // 原产地
    private String countryOfOrigin;
    // 生产日期
    private String mfg;
    // 失效日期
    private String exp;
    // 批次号
    private String batchNumber;
    // 库存属性1
    private String invAttr1;
    // 库存属性2
    private String invAttr2;
    // 库存属性3
    private String invAttr3;
    // 库存属性4
    private String invAttr4;
    // 库存属性5
    private String invAttr5;
    // 库存类型
    private String invType;
    // 库存状态
    private Long invStatus;

    /** 用户分拣是否共享目标容器 */
    private Boolean isSortationContainerAssign;

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getSkuInvId() {
        return skuInvId;
    }

    public void setSkuInvId(Long skuInvId) {
        this.skuInvId = skuInvId;
    }

    public Long getInboundId() {
        return inboundId;
    }

    public void setInboundId(Long inboundId) {
        this.inboundId = inboundId;
    }

    public Double getShiftInQty() {
        return shiftInQty;
    }

    public void setShiftInQty(Double shiftInQty) {
        this.shiftInQty = shiftInQty;
    }

    public Double getShiftOutQty() {
        return shiftOutQty;
    }

    public void setShiftOutQty(Double shiftOutQty) {
        this.shiftOutQty = shiftOutQty;
    }

    public String getTargetContainerCode() {
        return targetContainerCode;
    }

    public void setTargetContainerCode(String targetContainerCode) {
        this.targetContainerCode = targetContainerCode;
    }

    public String getTargetContainerCodeSelect() {
        return targetContainerCodeSelect;
    }

    public void setTargetContainerCodeSelect(String targetContainerCodeSelect) {
        this.targetContainerCodeSelect = targetContainerCodeSelect;
    }

    public Long getNewContainerId() {
        return newContainerId;
    }

    public void setNewContainerId(Long newContainerId) {
        this.newContainerId = newContainerId;
    }

    public Map<String, String> getCooMap() {
        return cooMap;
    }

    public void setCooMap(Map<String, String> cooMap) {
        this.cooMap = cooMap;
    }

    public Map<String, String> getMfgMap() {
        return mfgMap;
    }

    public void setMfgMap(Map<String, String> mfgMap) {
        this.mfgMap = mfgMap;
    }

    public Map<String, String> getExpMap() {
        return expMap;
    }

    public void setExpMap(Map<String, String> expMap) {
        this.expMap = expMap;
    }

    public Map<String, String> getBatchNumMap() {
        return batchNumMap;
    }

    public void setBatchNumMap(Map<String, String> batchNumMap) {
        this.batchNumMap = batchNumMap;
    }

    public Map<String, String> getInvAttr1Map() {
        return invAttr1Map;
    }

    public void setInvAttr1Map(Map<String, String> invAttr1Map) {
        this.invAttr1Map = invAttr1Map;
    }

    public Map<String, String> getInvAttr2Map() {
        return invAttr2Map;
    }

    public void setInvAttr2Map(Map<String, String> invAttr2Map) {
        this.invAttr2Map = invAttr2Map;
    }

    public Map<String, String> getInvAttr3Map() {
        return invAttr3Map;
    }

    public void setInvAttr3Map(Map<String, String> invAttr3Map) {
        this.invAttr3Map = invAttr3Map;
    }

    public Map<String, String> getInvAttr4Map() {
        return invAttr4Map;
    }

    public void setInvAttr4Map(Map<String, String> invAttr4Map) {
        this.invAttr4Map = invAttr4Map;
    }

    public Map<String, String> getInvAttr5Map() {
        return invAttr5Map;
    }

    public void setInvAttr5Map(Map<String, String> invAttr5Map) {
        this.invAttr5Map = invAttr5Map;
    }

    public Map<String, String> getInvTypeMap() {
        return invTypeMap;
    }

    public void setInvTypeMap(Map<String, String> invTypeMap) {
        this.invTypeMap = invTypeMap;
    }

    public Map<Integer, String> getInvStatusMap() {
        return invStatusMap;
    }

    public void setInvStatusMap(Map<Integer, String> invStatusMap) {
        this.invStatusMap = invStatusMap;
    }

    public String getSnCode() {
        return snCode;
    }

    public void setSnCode(String snCode) {
        this.snCode = snCode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getIsFull() {
        return isFull;
    }

    public void setIsFull(Boolean isFull) {
        this.isFull = isFull;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getMfg() {
        return mfg;
    }

    public void setMfg(String mfg) {
        this.mfg = mfg;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
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

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public Long getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Long invStatus) {
        this.invStatus = invStatus;
    }

    public String getMfgDateStr() {
        return mfgDateStr;
    }

    public void setMfgDateStr(String mfgDateStr) {
        this.mfgDateStr = mfgDateStr;
    }

    public String getExpDateStr() {
        return expDateStr;
    }

    public void setExpDateStr(String expDateStr) {
        this.expDateStr = expDateStr;
    }

    public Boolean getIsSortationDone() {
        return isSortationDone;
    }

    public void setIsSortationDone(Boolean isSortationDone) {
        this.isSortationDone = isSortationDone;
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

    public Boolean getIsSortationContainerAssign() {
        return isSortationContainerAssign;
    }

    public void setIsSortationContainerAssign(Boolean isSortationContainerAssign) {
        this.isSortationContainerAssign = isSortationContainerAssign;
    }

}
