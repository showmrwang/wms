/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhOutboundConsumableCommand extends BaseCommand {

    private static final long serialVersionUID = 6419735547575417939L;

    /** 主键ID */
    private Long id;
    /** 小批次 */
    private String batch;
    /** 波次号 */
    private String waveCode;
    /** 客户CODE */
    private String customerCode;
    /** 客户名称 */
    private String customerName;
    /** 店铺CODE */
    private String storeCode;
    /** 店铺名称 */
    private String storeName;
    /** 出库单ID */
    private Long odoId;
    /** 出库单号 */
    private String odoCode;
    /** 物流商编码 */
    private String transportCode;
    /** 运单号 */
    private String waybillCode;
    /** 复核台ID */
    private Long facilityId;
    /** 复核台编码 */
    private String facilityCode;
    /** 复核台库位ID */
    private Long locationId;
    /** 复核台库位编码 */
    private String locationCode;
    /** 库区ID */
    private Long areaId;
    /** 库区编码 */
    private String areaCode;
    /** 数量 */
    private Double qty;
    /** 耗材领用单号 */
    private String consumOrder;
    /** 耗材领用/使用人账号 */
    private String consumAccount;
    /** 耗材领用/使用人姓名 */
    private String consumPerson;
    /** 对应组织ID */
    private Long ouId;
    /** 耗材ID */
    private Long outboundboxId;
    /** 出库箱编码 */
    private String outboundboxCode;
    /** 耗材商品编码 */
    private String skuCode;
    /** 耗材商品条码 */
    private String skuBarcode;
    /** 耗材商品名称 */
    private String skuName;
    /** 长 */
    private Double skuLength;
    /** 宽 */
    private Double skuWidth;
    /** 高 */
    private Double skuHeight;
    /** 体积 */
    private Double skuVolume;
    /** 重量 */
    private Double skuWeight;
    /** 创建人ID */
    private Long createId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最后修改时间 */
    private java.util.Date lastModifyTime;
    /** 修改人ID */
    private Long modifiedId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
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

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public String getOdoCode() {
        return odoCode;
    }

    public void setOdoCode(String odoCode) {
        this.odoCode = odoCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
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

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getConsumOrder() {
        return consumOrder;
    }

    public void setConsumOrder(String consumOrder) {
        this.consumOrder = consumOrder;
    }

    public String getConsumAccount() {
        return consumAccount;
    }

    public void setConsumAccount(String consumAccount) {
        this.consumAccount = consumAccount;
    }

    public String getConsumPerson() {
        return consumPerson;
    }

    public void setConsumPerson(String consumPerson) {
        this.consumPerson = consumPerson;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
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

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuBarcode() {
        return skuBarcode;
    }

    public void setSkuBarcode(String skuBarcode) {
        this.skuBarcode = skuBarcode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Double getSkuLength() {
        return skuLength;
    }

    public void setSkuLength(Double skuLength) {
        this.skuLength = skuLength;
    }

    public Double getSkuWidth() {
        return skuWidth;
    }

    public void setSkuWidth(Double skuWidth) {
        this.skuWidth = skuWidth;
    }

    public Double getSkuHeight() {
        return skuHeight;
    }

    public void setSkuHeight(Double skuHeight) {
        this.skuHeight = skuHeight;
    }

    public Double getSkuVolume() {
        return skuVolume;
    }

    public void setSkuVolume(Double skuVolume) {
        this.skuVolume = skuVolume;
    }

    public Double getSkuWeight() {
        return skuWeight;
    }

    public void setSkuWeight(Double skuWeight) {
        this.skuWeight = skuWeight;
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
}
