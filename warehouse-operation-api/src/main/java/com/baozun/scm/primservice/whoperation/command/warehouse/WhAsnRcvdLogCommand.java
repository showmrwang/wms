package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdSnLog;

public class WhAsnRcvdLogCommand extends BaseCommand {


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
    /** 已收数量 */
    private Double qtyRcvd;
    /** 容器编码 */
    private String containerCode;
    /** 容器名称 */
    private String containerName;
    /** 生产日期 */
    private Date mfgDate;
    /** 失效日期 */
    private Date expDate;
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
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人Id */
    private Long operatorId;

    private List<WhAsnRcvdSnLog> whAsnRcvdSnLogList;

    public List<WhAsnRcvdSnLog> getWhAsnRcvdSnLogList() {
        return whAsnRcvdSnLogList;
    }

    public void setWhAsnRcvdSnLogList(List<WhAsnRcvdSnLog> whAsnRcvdSnLogList) {
        this.whAsnRcvdSnLogList = whAsnRcvdSnLogList;
    }

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

    public void setOperatorId(Long value) {
        this.operatorId = value;
    }

    public Long getOperatorId() {
        return this.operatorId;
    }

    public Double getQtyRcvd() {
        return qtyRcvd;
    }

    public void setQtyRcvd(Double qtyRcvd) {
        this.qtyRcvd = qtyRcvd;
    }

}
