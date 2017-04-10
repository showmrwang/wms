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
package com.baozun.scm.primservice.whinterface.model.outbound;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 出库单状态反馈
 * 
 *
 */
public class WmsOutBoundStatusConfirm extends BaseModel {



    /**
     * 
     */
    private static final long serialVersionUID = 8335217566158955109L;
    /** 上位系统出库单号 */
    private String extOdoCode;
    /** wms出库单号 */
    private String wmsOdoCode;
    /** wms出库单状态 */
    private Integer wmsOdoStatus;
    /** 客户编码 */
    private String customerCode;
    /** 店铺编码 */
    private String storeCode;
    /** 组织仓库Code */
    private String whCode;
    /** 创建时间 */
    private Date createTime;
    /** 数据来源 区分上位系统 */
    private String dataSource;

    public String getExtOdoCode() {
        return extOdoCode;
    }

    public void setExtOdoCode(String extOdoCode) {
        this.extOdoCode = extOdoCode;
    }

    public String getWmsOdoCode() {
        return wmsOdoCode;
    }

    public void setWmsOdoCode(String wmsOdoCode) {
        this.wmsOdoCode = wmsOdoCode;
    }

    public Integer getWmsOdoStatus() {
        return wmsOdoStatus;
    }

    public void setWmsOdoStatus(Integer wmsOdoStatus) {
        this.wmsOdoStatus = wmsOdoStatus;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }


}
