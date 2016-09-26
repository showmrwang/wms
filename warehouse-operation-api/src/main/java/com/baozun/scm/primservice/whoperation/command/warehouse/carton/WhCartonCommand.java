/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * 
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 * 
 */
package com.baozun.scm.primservice.whoperation.command.warehouse.carton;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;

/**
 * 拆箱记录表
 * 
 * @author larkark
 * 
 */
public class WhCartonCommand extends BaseCommand {

    private static final long serialVersionUID = -3800081543401467516L;
    /** 主键ID */
    private Long id;
    /** ASN_ID */
    private Long asnId;
    /** asn_line_id */
    private Long asnLineId;
    /** 商品ID */
    private Long skuId;
    /** 容器ID */
    private Long containerId;
    /** 装箱数量 */
    private Double quantity;
    /** 已收数量 */
    private Double qtyRcvd;
    /** 生产日期 */
    private Date mfgDate;
    /** 失效日期 */
    private Date expDate;
    /** 批次号 */
    private String batchNo;
    /** 原产地 */
    private String countryOfOrigin;
    /** 库存状态 */
    private Long invStatus;
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
    /** 库存类型 */
    private String invType;
    /** 对应仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最好修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** 外部箱号 */
    private String extContainerCode;
    /** 是否caselevel */
    private Boolean isCaselevel;
    /** 本次拆箱数量 */
    private Double bcdevanningQty;
    /** 容器个数 */
    private Integer binQty;

    /** 商品编码 */
    private String skuCode;
    /** 商品名称 */
    private String skuName;
    /** 库存属性1 lable */
    private String invAttr1Str;
    /** 库存属性2 lable */
    private String invAttr2Str;
    /** 库存属性3 lable */
    private String invAttr3Str;
    /** 库存属性4 lable */
    private String invAttr4Str;
    /** 库存属性5 lable */
    private String invAttr5Str;
    /** 库存类型label */
    private String invTypeLabel;
    /** 生产日期String */
    private String mfgDateStr;
    /** 失效日期String */
    private String expDateStr;
    /** 库存状态名称 */
    private String invName;
    /** 容器编码 */
    private String containerCode;
    /** 容器类型 */
    private String categoryName;
    /** 容器类型ID */
    private String categoryId;
    /** asnline状态 */
    private Integer asnLineStatus;

    // caseLeve收货
    private String asnExtCode;
    // 商品扫描条码
    private String skuBarcode;
    // 商品扫描数量
    private Double skuQty;
    // 残次类型
    private Long defectType;
    // 残次原因
    private Long defectReason;
    // 残次数量
    private Integer defectQty;
    // 残次来源
    private String defectSource;
    // SN号
    private String snCode;
    // 缓存中的UUID标识
    private String uuid;
    // SN/残次信息列表
    private List<WhSkuInventorySn> skuInventorySnList;
    // 已收残次数量
    private Integer defectRcvdQty;
    // 上次收货数
    private Double lastRcvdSkuQty;
    // 更新数据库类型
    private Boolean insert;
    // 调整数量,key是UUID,value是调整值
    private Map<String, Double> alterQtyMap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAsnId() {
        return asnId;
    }

    public void setAsnId(Long asnId) {
        this.asnId = asnId;
    }

    public Long getAsnLineId() {
        return asnLineId;
    }

    public void setAsnLineId(Long asnLineId) {
        this.asnLineId = asnLineId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
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

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public Long getInvStatus() {
        return invStatus;
    }

    public void setInvStatus(Long invStatus) {
        this.invStatus = invStatus;
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

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
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

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getInvAttr1Str() {
        return invAttr1Str;
    }

    public void setInvAttr1Str(String invAttr1Str) {
        this.invAttr1Str = invAttr1Str;
    }

    public String getInvAttr2Str() {
        return invAttr2Str;
    }

    public void setInvAttr2Str(String invAttr2Str) {
        this.invAttr2Str = invAttr2Str;
    }

    public String getInvAttr3Str() {
        return invAttr3Str;
    }

    public void setInvAttr3Str(String invAttr3Str) {
        this.invAttr3Str = invAttr3Str;
    }

    public String getInvAttr4Str() {
        return invAttr4Str;
    }

    public void setInvAttr4Str(String invAttr4Str) {
        this.invAttr4Str = invAttr4Str;
    }

    public String getInvAttr5Str() {
        return invAttr5Str;
    }

    public void setInvAttr5Str(String invAttr5Str) {
        this.invAttr5Str = invAttr5Str;
    }

    public String getInvTypeLabel() {
        return invTypeLabel;
    }

    public void setInvTypeLabel(String invTypeLabel) {
        this.invTypeLabel = invTypeLabel;
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

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    private List<WhCartonCommand> cartonList;


    public List<WhCartonCommand> getCartonList() {
        return cartonList;
    }

    public void setCartonList(List<WhCartonCommand> cartonList) {
        this.cartonList = cartonList;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getAsnLineStatus() {
        return asnLineStatus;
    }

    public void setAsnLineStatus(Integer asnLineStatus) {
        this.asnLineStatus = asnLineStatus;
    }

    public String getExtContainerCode() {
        return extContainerCode;
    }

    public void setExtContainerCode(String extContainerCode) {
        this.extContainerCode = extContainerCode;
    }

    public Boolean getIsCaselevel() {
        return isCaselevel;
    }

    public void setIsCaselevel(Boolean isCaselevel) {
        this.isCaselevel = isCaselevel;
    }

    public Double getBcdevanningQty() {
        return bcdevanningQty;
    }

    public void setBcdevanningQty(Double bcdevanningQty) {
        this.bcdevanningQty = bcdevanningQty;
    }

    public Integer getBinQty() {
        return binQty;
    }

    public void setBinQty(Integer binQty) {
        this.binQty = binQty;
    }

    public Double getQtyRcvd() {
        return qtyRcvd;
    }

    public void setQtyRcvd(Double qtyRcvd) {
        this.qtyRcvd = qtyRcvd;
    }


    public String getAsnExtCode() {
        return asnExtCode;
    }

    public void setAsnExtCode(String asnExtCode) {
        this.asnExtCode = asnExtCode;
    }

    public String getSkuBarcode() {
        return skuBarcode;
    }

    public void setSkuBarcode(String skuBarcode) {
        this.skuBarcode = skuBarcode;
    }

    public Double getSkuQty() {
        return skuQty;
    }

    public void setSkuQty(Double skuQty) {
        this.skuQty = skuQty;
    }

    public Long getDefectType() {
        return defectType;
    }

    public void setDefectType(Long defectType) {
        this.defectType = defectType;
    }

    public Long getDefectReason() {
        return defectReason;
    }

    public void setDefectReason(Long defectReason) {
        this.defectReason = defectReason;
    }

    public Integer getDefectQty() {
        return defectQty;
    }

    public void setDefectQty(Integer defectQty) {
        this.defectQty = defectQty;
    }

    public String getDefectSource() {
        return defectSource;
    }

    public void setDefectSource(String defectSource) {
        this.defectSource = defectSource;
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

    public List<WhSkuInventorySn> getSkuInventorySnList() {
        return skuInventorySnList;
    }

    public void setSkuInventorySnList(List<WhSkuInventorySn> skuInventorySnList) {
        this.skuInventorySnList = skuInventorySnList;
    }

    public Integer getDefectRcvdQty() {
        return defectRcvdQty;
    }

    public void setDefectRcvdQty(Integer defectRcvdQty) {
        this.defectRcvdQty = defectRcvdQty;
    }

    public Double getLastRcvdSkuQty() {
        return lastRcvdSkuQty;
    }

    public void setLastRcvdSkuQty(Double lastRcvdSkuQty) {
        this.lastRcvdSkuQty = lastRcvdSkuQty;
    }

    public Boolean getInsert() {
        return insert;
    }

    public void setInsert(Boolean insert) {
        this.insert = insert;
    }

    public Map<String, Double> getAlterQtyMap() {
        return alterQtyMap;
    }

    public void setAlterQtyMap(Map<String, Double> alterQtyMap) {
        this.alterQtyMap = alterQtyMap;
    }
}
