package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhAsnRcvdLogCommand extends BaseCommand{


    private static final long serialVersionUID = 4407198980293854936L;
    
    /** asnid */
    private Long asnId;
    /** asnlineid */
    private Long asnLineId;
    /** asn编码 */
    private String asnCode;
    /** 商品编码 */
    private String skuCode;
    /** 商品名称 */
    private String skuName;
    /** 数量 */
    private Long quantity;
    /** 容器编码 */
    private String containerCode;
    /** 容器名称 */
    private String containerName;
    /** 生产日期 */
    private String mfgDate;
    /** 失效日期 */
    private String expDate;
    /** 批次号 */
    private String batchNo;
    /** 原产地 */
    private String countryOfOrigin;
    /** 库存状态 */
    private String invStatus;
    /** 库存类型 */
    private String invType;
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
    /** 对应组织ID */
    private Long ouId;
    /** 创建时间 */
    private String createTime;
    /** 最后修改时间 */
    private String lastModifyTime;
    /** 操作人Id */
    private Long operatorId;


    public void setAsnId(Long value) {
        this.asnId = value;
    }

    public Long getAsnId() {
        return this.asnId;
    }

    public void setAsnLineId(Long value) {
        this.asnLineId = value;
    }

    public Long getAsnLineId() {
        return this.asnLineId;
    }

    public void setAsnCode(String value) {
        this.asnCode = value;
    }

    public String getAsnCode() {
        return this.asnCode;
    }

    public void setSkuCode(String value) {
        this.skuCode = value;
    }

    public String getSkuCode() {
        return this.skuCode;
    }

    public void setSkuName(String value) {
        this.skuName = value;
    }

    public String getSkuName() {
        return this.skuName;
    }

    public void setQuantity(Long value) {
        this.quantity = value;
    }

    public Long getQuantity() {
        return this.quantity;
    }

    public void setContainerCode(String value) {
        this.containerCode = value;
    }

    public String getContainerCode() {
        return this.containerCode;
    }

    public void setContainerName(String value) {
        this.containerName = value;
    }

    public String getContainerName() {
        return this.containerName;
    }


    public String setMfgDate(String str) {
        return this.mfgDate = str;
    }

    public String getMfgDate() {
        return this.mfgDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public String setExpDate(String expDate) {
        return this.expDate = expDate;
    }

    public void setBatchNo(String value) {
        this.batchNo = value;
    }

    public String getBatchNo() {
        return this.batchNo;
    }

    public void setCountryOfOrigin(String value) {
        this.countryOfOrigin = value;
    }

    public String getCountryOfOrigin() {
        return this.countryOfOrigin;
    }

    public void setInvStatus(String value) {
        this.invStatus = value;
    }

    public String getInvStatus() {
        return this.invStatus;
    }

    public void setInvType(String value) {
        this.invType = value;
    }

    public String getInvType() {
        return this.invType;
    }

    public void setInvAttr1(String value) {
        this.invAttr1 = value;
    }

    public String getInvAttr1() {
        return this.invAttr1;
    }

    public void setInvAttr2(String value) {
        this.invAttr2 = value;
    }

    public String getInvAttr2() {
        return this.invAttr2;
    }

    public void setInvAttr3(String value) {
        this.invAttr3 = value;
    }

    public String getInvAttr3() {
        return this.invAttr3;
    }

    public void setInvAttr4(String value) {
        this.invAttr4 = value;
    }

    public String getInvAttr4() {
        return this.invAttr4;
    }

    public void setInvAttr5(String value) {
        this.invAttr5 = value;
    }

    public String getInvAttr5() {
        return this.invAttr5;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Long getOuId() {
        return this.ouId;
    }


    public void setCreateTime(String value) {
        this.createTime = value;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setLastModifyTime(String value) {
        this.lastModifyTime = value;
    }

    public String getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setOperatorId(Long value) {
        this.operatorId = value;
    }

    public Long getOperatorId() {
        return this.operatorId;
    }

}
