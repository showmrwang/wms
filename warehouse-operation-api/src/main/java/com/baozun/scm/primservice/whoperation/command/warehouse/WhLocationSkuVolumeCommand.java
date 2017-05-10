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
package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


/**
 * 
 * @author larkark
 *
 */
public class WhLocationSkuVolumeCommand extends BaseModel {


    private static final long serialVersionUID = -8702295660048915924L;
    // columns START
    private Long lsvId;
    /** 序号 */
    private Integer serialNumber;
    /** 库位ID */
    private Long locationId;
    /** 商品ID */
    private Long skuId;
    /** 容量下限 */
    private Integer lowerCapacity;
    /** 容量上限 */
    private Integer upperCapacity;
    /** 库位编码 */
    private String locationCode;
    /** 商品编码 */
    private String skuCode;
    /** 商品条码 */
    private String skuBarCode;
    /** 商品名称 */
    private String skuName;
    /** 在库库存 */
    private Long onHandQty;
    
    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getLowerCapacity() {
        return lowerCapacity;
    }

    public void setLowerCapacity(Integer lowerCapacity) {
        this.lowerCapacity = lowerCapacity;
    }

    public Integer getUpperCapacity() {
        return upperCapacity;
    }

    public void setUpperCapacity(Integer upperCapacity) {
        this.upperCapacity = upperCapacity;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Long getLsvId() {
        return lsvId;
    }

    public void setLsvId(Long lsvId) {
        this.lsvId = lsvId;
    }


    public Long getOnHandQty() {
        return onHandQty;
    }

    public void setOnHandQty(Long onHandQty) {
        this.onHandQty = onHandQty;
    }
}
