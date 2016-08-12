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
package com.baozun.scm.primservice.whoperation.model.odo;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class WhOdoVas extends BaseModel {

    private static final long serialVersionUID = -1680303487084170272L;

    /** 出库单ID */
    private Long odoId;
    /** 出库单明细ID */
    private Long odoLineId;
    /** 增值服务类型 快递/仓库 */
    private String vasType;
    /** 仓库增值服务类型 */
    private Long whVasType;
    /** 快递增值服务ID */
    private Long expressVasType;
    /** 打印模板 */
    private String printTemplet;
    /** 赠品编码/礼品包装 */
    private String skuBarCode;
    /** 内容/备注 */
    private String content;
    /** 箱号 */
    private String cartonNo;
    /** 数量 赠送数量 */
    private String qty;
    /** 金额 */
    private String amt;
    /** 支付方式 */
    private String modeOfPayment;
    /** 增值服务属性1 */
    private String vasAttr1;
    /** 增值服务属性2 */
    private String vasAttr2;
    /** 增值服务属性3 */
    private String vasAttr3;
    /** 增值服务属性4 */
    private String vasAttr4;
    /** 增值服务属性5 */
    private String vasAttr5;
    /** 仓库组织ID */
    private Long ouId;

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

    public String getVasType() {
        return vasType;
    }

    public void setVasType(String vasType) {
        this.vasType = vasType;
    }

    public Long getWhVasType() {
        return whVasType;
    }

    public void setWhVasType(Long whVasType) {
        this.whVasType = whVasType;
    }

    public Long getExpressVasType() {
        return expressVasType;
    }

    public void setExpressVasType(Long expressVasType) {
        this.expressVasType = expressVasType;
    }

    public String getPrintTemplet() {
        return printTemplet;
    }

    public void setPrintTemplet(String printTemplet) {
        this.printTemplet = printTemplet;
    }

    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCartonNo() {
        return cartonNo;
    }

    public void setCartonNo(String cartonNo) {
        this.cartonNo = cartonNo;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public String getVasAttr1() {
        return vasAttr1;
    }

    public void setVasAttr1(String vasAttr1) {
        this.vasAttr1 = vasAttr1;
    }

    public String getVasAttr2() {
        return vasAttr2;
    }

    public void setVasAttr2(String vasAttr2) {
        this.vasAttr2 = vasAttr2;
    }

    public String getVasAttr3() {
        return vasAttr3;
    }

    public void setVasAttr3(String vasAttr3) {
        this.vasAttr3 = vasAttr3;
    }

    public String getVasAttr4() {
        return vasAttr4;
    }

    public void setVasAttr4(String vasAttr4) {
        this.vasAttr4 = vasAttr4;
    }

    public String getVasAttr5() {
        return vasAttr5;
    }

    public void setVasAttr5(String vasAttr5) {
        this.vasAttr5 = vasAttr5;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
}
