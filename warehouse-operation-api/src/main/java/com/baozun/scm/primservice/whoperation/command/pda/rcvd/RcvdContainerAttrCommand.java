package com.baozun.scm.primservice.whoperation.command.pda.rcvd;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;

public class RcvdContainerAttrCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 2000096178986502226L;

    /** 内部容器ID 托盘 货箱 */
    private Long insideContainerId;
    /** 原产地 */
    private String countryOfOrigin;
    /** 批次号 */
    private String batchNumber;
    /** 库存类型 */
    private String invType;
    /** 库存状态 */
    private String invStatus;
    /** 生产日期 */
    private String mfgDate;
    /** 失效日期 */
    private String expDate;
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
    /** SKUID */
    private Long SkuId;
    /** 用户ID */
    private Long userId;
    /** 商品url */
    private String url;
    /** 当前url标记 */
    private Integer currUrl;
    /** 下一个url */
    private String nextUrl;
    /** 组织ID */
    private Long ouId;
    /** 是否允许差异收货 */
    private Boolean isInvattrDiscrepancyAllowrcvd;
    /** asn ID */
    private Long occupationId;
    /** asn code */
    private String occupationCode;
    /** 通用收货功能匹配明细行 */
    private String lineIdListString;
    /** 残次数量 */
    private Integer snCount;
    /** 残次累计数量 */
    private Integer snAddUpCount;
    /** 是否在扫描sn号页面 */
    private String isSnScan;
    /** 剩余扫描数 */
    private Integer qtyRest;
    /** sn */
    private String sn;
    /** 功能id */
    private Long functionId;
    /** 当前扫描容器序号 */
    private String curOrder;
    /** 容器类型 */
    private Long containerType;
    /** 扫描的起始页 */
    private Integer firstPage;
    /** 容器计划收货数 */
    private Integer containerPlan;
    /** 此次批量收货数 */
    private Integer skuBatchCount;
    /** 通用收货所选择的功能 */
    private WhFunctionRcvd rcvd;
    /** 功能url */
    private String functionUrl;
    /** 生产日期 String */
    private String mfgDateStr;
    /** 失效日期 String */
    private String expDateStr;
    /** 序列号管理类型 */
    private String serialNumberType;

    private String isBatchScan;
    /** 是否提示用户 */
    private String isInvattrAsnPointoutUser;
    /** 序列号 */
    private String serialNumber;
    /** 扫描模式 */
    private Integer scanPattern;
    /** 效期 */
    private Integer validDate;


    public Long getInsideContainerId() {
        return insideContainerId;
    }

    public void setInsideContainerId(Long insideContainerId) {
        this.insideContainerId = insideContainerId;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
    }

    public String getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(String invStatus) {
        this.invStatus = invStatus;
    }

    public String getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(String mfgDate) {
        this.mfgDate = mfgDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
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

    public Long getSkuId() {
        return SkuId;
    }

    public void setSkuId(Long skuId) {
        SkuId = skuId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    public Integer getCurrUrl() {
        return currUrl;
    }

    public void setCurrUrl(Integer currUrl) {
        this.currUrl = currUrl;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Boolean getIsInvattrDiscrepancyAllowrcvd() {
        return isInvattrDiscrepancyAllowrcvd;
    }

    public void setIsInvattrDiscrepancyAllowrcvd(Boolean isInvattrDiscrepancyAllowrcvd) {
        this.isInvattrDiscrepancyAllowrcvd = isInvattrDiscrepancyAllowrcvd;
    }

    public Long getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(Long occupationId) {
        this.occupationId = occupationId;
    }

    public String getLineIdListString() {
        return lineIdListString;
    }

    public void setLineIdListString(String lineIdListString) {
        this.lineIdListString = lineIdListString;
    }

    public Integer getSnCount() {
        return snCount;
    }

    public void setSnCount(Integer snCount) {
        this.snCount = snCount;
    }

    public Integer getSnAddUpCount() {
        return snAddUpCount;
    }

    public void setSnAddUpCount(Integer snAddUpCount) {
        this.snAddUpCount = snAddUpCount;
    }

    public String getIsSnScan() {
        return isSnScan;
    }

    public void setIsSnScan(String isSnScan) {
        this.isSnScan = isSnScan;
    }

    public Integer getQtyRest() {
        return qtyRest;
    }

    public void setQtyRest(Integer qtyRest) {
        this.qtyRest = qtyRest;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public String getCurOrder() {
        return curOrder;
    }

    public void setCurOrder(String curOrder) {
        this.curOrder = curOrder;
    }

    public Long getContainerType() {
        return containerType;
    }

    public void setContainerType(Long containerType) {
        this.containerType = containerType;
    }

    public String getOccupationCode() {
        return occupationCode;
    }

    public void setOccupationCode(String occupationCode) {
        this.occupationCode = occupationCode;
    }

    public Integer getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(Integer firstPage) {
        this.firstPage = firstPage;
    }

    public Integer getContainerPlan() {
        return containerPlan;
    }

    public void setContainerPlan(Integer containerPlan) {
        this.containerPlan = containerPlan;
    }

    public Integer getSkuBatchCount() {
        return skuBatchCount;
    }

    public void setSkuBatchCount(Integer skuBatchCount) {
        this.skuBatchCount = skuBatchCount;
    }

    public WhFunctionRcvd getRcvd() {
        return rcvd;
    }

    public void setRcvd(WhFunctionRcvd rcvd) {
        this.rcvd = rcvd;
    }

    public String getFunctionUrl() {
        return functionUrl;
    }

    public void setFunctionUrl(String functionUrl) {
        this.functionUrl = functionUrl;
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

    public String getSerialNumberType() {
        return serialNumberType;
    }

    public void setSerialNumberType(String serialNumberType) {
        this.serialNumberType = serialNumberType;
    }

    public String getIsBatchScan() {
        return isBatchScan;
    }

    public void setIsBatchScan(String isBatchScan) {
        this.isBatchScan = isBatchScan;
    }

    public String getIsInvattrAsnPointoutUser() {
        return isInvattrAsnPointoutUser;
    }

    public void setIsInvattrAsnPointoutUser(String isInvattrAsnPointoutUser) {
        this.isInvattrAsnPointoutUser = isInvattrAsnPointoutUser;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getScanPattern() {
        return scanPattern;
    }

    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }

    public Integer getValidDate() {
        return validDate;
    }

    public void setValidDate(Integer validDate) {
        this.validDate = validDate;
    }

}
