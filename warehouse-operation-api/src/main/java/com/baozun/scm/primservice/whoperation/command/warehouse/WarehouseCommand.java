package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


public class WarehouseCommand extends BaseCommand {

    private static final long serialVersionUID = 8399897248153430529L;

    private Long id;
    /** 仓库编码 */
    private String code;
    /** 仓库名称 */
    private String name;
    /** 运营模式(待定) */
    private Integer opMode;
    /** 管理模式(待定) */
    private Integer manageMode;
    /** 联系人 */
    private String pic;
    /** 联系人电话 */
    private String picContact;
    /** 仓库电话 */
    private String phone;
    /** 仓库传真 */
    private String fax;
    /** 其他联系人和方法1 */
    private String otherContact1;
    /** 其他联系人和方法2 */
    private String otherContact2;
    /** 其他联系人和方法3 */
    private String otherContact3;
    /** 国家ID */
    private Long countryId;
    /** 省ID */
    private Long provinceId;
    /** 市ID */
    private Long cityId;
    /** 详细地址 */
    private String address;
    /** 邮政编码 */
    private String zipCode;
    /** 仓库面积(m²) */
    private Double size;
    /** 面积尺寸单位 */
    private String sizeUom;
    /** 租金单价 */
    private Double rentPrice;
    /** 货币单位 */
    private String rentPriceUom;
    /** 仓库描述 */
    private String description;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人id */
    private Long operatorId;
    /** 1.可用;2.已删除;0.禁用 */
    private Integer lifecycle;
    /** 是否强制预约 0:否 1:是 */
    private Boolean isMandatorilyReserved;
    /** 是否PO超收 0:否 1:是 */
    private Boolean isPoOvercharge;
    /** PO超收比例 */
    private Integer poOverchargeProportion;
    /** 是否ASN超收 0:否 1:是 */
    private Boolean isAsnOvercharge;
    /** asn超收比例 */
    private Integer asnOverchargeProportion;
    /** 是否自动审核PO */
    private Boolean isPoAutoVerify;
    /** 是否自动审核ASN */
    private Boolean isAsnAutoVerify;
    /** 预收货模式 1-总数 2-总箱数 3-商品数量 */
    private Integer goodsReceiptMode;
    /** 收货是否自动打印箱标签 */
    private Boolean isAutoPrintBintag;
    /** 收货是否自动生成箱号 */
    private Boolean isAutoGenerationCn;
    /** 是否允许越库 */
    private Boolean isAllowBlocked;
    /** 容器内sku是否需要扫描 */
    private Boolean isSkuNeedsScan;
    /** 库存属性管理 */
    private String invAttrMgmt;
    /** 是否自动打印收货差异清单 */
    private Boolean isAutoPrintDiff;
    /** 入库是否提示质检 */
    private Boolean isHintQualityTesting;
    /** 是否自动打印预收货交接清单 */
    private Boolean isAutoPrintGoodsReceipt;
    /** 是否自动打印月台标签 */
    private Boolean isAutoPrintPlatformtag;
    /** SKU严重混放数量 */
    private Integer skuMixNumber;

    private Long parentOuId;

    private String ouName;

    private Long ouId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOpMode() {
        return opMode;
    }

    public void setOpMode(Integer opMode) {
        this.opMode = opMode;
    }

    public Integer getManageMode() {
        return manageMode;
    }

    public void setManageMode(Integer manageMode) {
        this.manageMode = manageMode;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPicContact() {
        return picContact;
    }

    public void setPicContact(String picContact) {
        this.picContact = picContact;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getOtherContact1() {
        return otherContact1;
    }

    public void setOtherContact1(String otherContact1) {
        this.otherContact1 = otherContact1;
    }

    public String getOtherContact2() {
        return otherContact2;
    }

    public void setOtherContact2(String otherContact2) {
        this.otherContact2 = otherContact2;
    }

    public String getOtherContact3() {
        return otherContact3;
    }

    public void setOtherContact3(String otherContact3) {
        this.otherContact3 = otherContact3;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getSizeUom() {
        return sizeUom;
    }

    public void setSizeUom(String sizeUom) {
        this.sizeUom = sizeUom;
    }

    public String getRentPriceUom() {
        return rentPriceUom;
    }

    public void setRentPriceUom(String rentPriceUom) {
        this.rentPriceUom = rentPriceUom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Long getParentOuId() {
        return parentOuId;
    }

    public void setParentOuId(Long parentOuId) {
        this.parentOuId = parentOuId;
    }

    public String getOuName() {
        return ouName;
    }

    public void setOuName(String ouName) {
        this.ouName = ouName;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(Double rentPrice) {
        this.rentPrice = rentPrice;
    }

    public Boolean getIsMandatorilyReserved() {
        return isMandatorilyReserved;
    }

    public void setIsMandatorilyReserved(Boolean isMandatorilyReserved) {
        this.isMandatorilyReserved = isMandatorilyReserved;
    }

    public Boolean getIsPoOvercharge() {
        return isPoOvercharge;
    }

    public void setIsPoOvercharge(Boolean isPoOvercharge) {
        this.isPoOvercharge = isPoOvercharge;
    }

    public Integer getPoOverchargeProportion() {
        return poOverchargeProportion;
    }

    public void setPoOverchargeProportion(Integer poOverchargeProportion) {
        this.poOverchargeProportion = poOverchargeProportion;
    }

    public Boolean getIsAsnOvercharge() {
        return isAsnOvercharge;
    }

    public void setIsAsnOvercharge(Boolean isAsnOvercharge) {
        this.isAsnOvercharge = isAsnOvercharge;
    }

    public Integer getAsnOverchargeProportion() {
        return asnOverchargeProportion;
    }

    public void setAsnOverchargeProportion(Integer asnOverchargeProportion) {
        this.asnOverchargeProportion = asnOverchargeProportion;
    }

    public Boolean getIsPoAutoVerify() {
        return isPoAutoVerify;
    }

    public void setIsPoAutoVerify(Boolean isPoAutoVerify) {
        this.isPoAutoVerify = isPoAutoVerify;
    }

    public Boolean getIsAsnAutoVerify() {
        return isAsnAutoVerify;
    }

    public void setIsAsnAutoVerify(Boolean isAsnAutoVerify) {
        this.isAsnAutoVerify = isAsnAutoVerify;
    }

    public Integer getGoodsReceiptMode() {
        return goodsReceiptMode;
    }

    public void setGoodsReceiptMode(Integer goodsReceiptMode) {
        this.goodsReceiptMode = goodsReceiptMode;
    }

    public Boolean getIsAutoPrintBintag() {
        return isAutoPrintBintag;
    }

    public void setIsAutoPrintBintag(Boolean isAutoPrintBintag) {
        this.isAutoPrintBintag = isAutoPrintBintag;
    }

    public Boolean getIsAutoGenerationCn() {
        return isAutoGenerationCn;
    }

    public void setIsAutoGenerationCn(Boolean isAutoGenerationCn) {
        this.isAutoGenerationCn = isAutoGenerationCn;
    }

    public Boolean getIsAllowBlocked() {
        return isAllowBlocked;
    }

    public void setIsAllowBlocked(Boolean isAllowBlocked) {
        this.isAllowBlocked = isAllowBlocked;
    }

    public Boolean getIsSkuNeedsScan() {
        return isSkuNeedsScan;
    }

    public void setIsSkuNeedsScan(Boolean isSkuNeedsScan) {
        this.isSkuNeedsScan = isSkuNeedsScan;
    }

    public String getInvAttrMgmt() {
        return invAttrMgmt;
    }

    public void setInvAttrMgmt(String invAttrMgmt) {
        this.invAttrMgmt = invAttrMgmt;
    }

    public Boolean getIsAutoPrintDiff() {
        return isAutoPrintDiff;
    }

    public void setIsAutoPrintDiff(Boolean isAutoPrintDiff) {
        this.isAutoPrintDiff = isAutoPrintDiff;
    }

    public Boolean getIsHintQualityTesting() {
        return isHintQualityTesting;
    }

    public void setIsHintQualityTesting(Boolean isHintQualityTesting) {
        this.isHintQualityTesting = isHintQualityTesting;
    }

    public Boolean getIsAutoPrintGoodsReceipt() {
        return isAutoPrintGoodsReceipt;
    }

    public void setIsAutoPrintGoodsReceipt(Boolean isAutoPrintGoodsReceipt) {
        this.isAutoPrintGoodsReceipt = isAutoPrintGoodsReceipt;
    }

    public Boolean getIsAutoPrintPlatformtag() {
        return isAutoPrintPlatformtag;
    }

    public void setIsAutoPrintPlatformtag(Boolean isAutoPrintPlatformtag) {
        this.isAutoPrintPlatformtag = isAutoPrintPlatformtag;
    }

    public Integer getSkuMixNumber() {
        return skuMixNumber;
    }

    public void setSkuMixNumber(Integer skuMixNumber) {
        this.skuMixNumber = skuMixNumber;
    }



}
