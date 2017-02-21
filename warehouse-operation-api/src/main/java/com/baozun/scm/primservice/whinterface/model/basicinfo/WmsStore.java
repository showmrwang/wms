package com.baozun.scm.primservice.whinterface.model.basicinfo;

import java.io.Serializable;

public class WmsStore implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1997167382949638643L;


    /*
     * 店铺编码
     */
    private String storeCode;
    /*
     * 店铺名称
     */
    private String storeName;

    @Override
    public String toString() {
        return "WmsStore [storeCode=" + storeCode + ", storeName=" + storeName + ", description=" + description + ", pic=" + pic + ", picContact=" + picContact + ", invoiceType=" + invoiceType + ", paymentTerm=" + paymentTerm + ", isMandatorilyReserved="
                + isMandatorilyReserved + ", isPoOvercharge=" + isPoOvercharge + ", poOverchargeProportion=" + poOverchargeProportion + ", isAsnOvercharge=" + isAsnOvercharge + ", asnOverchargeProportion=" + asnOverchargeProportion + ", isPoAutoVerify="
                + isPoAutoVerify + ", isAsnAutoVerify=" + isAsnAutoVerify + ", goodsReceiptMode=" + goodsReceiptMode + ", isAutoPrintBintag=" + isAutoPrintBintag + ", isAutoGenerationCn=" + isAutoGenerationCn + ", isAllowBlocked=" + isAllowBlocked
                + ", invAttrMgmt=" + invAttrMgmt + ", isAllowCollectDiff=" + isAllowCollectDiff + ", isAutoPrintDiff=" + isAutoPrintDiff + ", isHintQualityTesting=" + isHintQualityTesting + ", isAutoPrintGoodsReceipt=" + isAutoPrintGoodsReceipt
                + ", customerName=" + customerName + ", customerCode=" + customerCode + ", picMobileTelephone=" + picMobileTelephone + ", countryName=" + countryName + ", countryCode=" + countryCode + ", provinceName=" + provinceName + ", provinceCode="
                + provinceCode + ", cityName=" + cityName + ", cityCode=" + cityCode + ", address=" + address + ", zipCode=" + zipCode + ", email=" + email + ", villagesTownsName=" + villagesTownsName + ", villagesTownsCode=" + villagesTownsCode
                + ", districtName=" + districtName + ", districtCode=" + districtCode + "]";
    }

    /*
     * 描述
     */
    private String description;
    /*
     * 联系人
     */
    private String pic;
    /*
     * 联系人电话
     */
    private String picContact;
    /*
     * 发票类型
     */
    private String invoiceType;
    /*
     * 结算方式
     */
    private String paymentTerm;

    /*
     * 是否强制预约 0:否 1:是
     */
    private Integer isMandatorilyReserved;

    /*
     * 是否PO超收 0:否 1:是
     */
    private Integer isPoOvercharge;

    /*
     * PO超收比例
     */
    private Integer poOverchargeProportion;
    /*
     * 是否ASN超收 0:否 1:是
     */
    private Integer isAsnOvercharge;

    /*
     * asn超收比例
     */
    private Integer asnOverchargeProportion;

    /*
     * 是否自动审核PO
     */
    private Integer isPoAutoVerify;
    /*
     * 是否自动审核ASN
     */
    private Integer isAsnAutoVerify;
    /*
     * 预收货模式 1-总数 2-总箱数 3-商品数量
     */
    private Integer goodsReceiptMode;
    /*
     * 收货是否自动打印箱标签
     */
    private Integer isAutoPrintBintag;
    /*
     * 收货是否自动生成箱号
     */
    private Integer isAutoGenerationCn;
    /*
     * 是否允许越库
     */
    private Integer isAllowBlocked;
    /*
     * 库存属性管理
     */
    private String invAttrMgmt;
    /*
     * 是否允许收货差异
     */
    private Integer isAllowCollectDiff;
    /*
     * 是否自动打印收货差异清单
     */
    private Integer isAutoPrintDiff;
    /*
     * 入库是否提示质检
     */
    private Integer isHintQualityTesting;
    /*
     * 是否自动打印预收货交接清单
     */
    private Integer isAutoPrintGoodsReceipt;

    /*
     * 客户
     */
    private String customerName;
    private String customerCode;

    /*
     * 联系手机
     */
    private String picMobileTelephone;
    /*
     * 国家
     */
    private String countryName;
    private String countryCode;
    /*
     * 省
     */
    private String provinceName;
    private String provinceCode;
    /*
     * 市
     */
    private String cityName;
    private String cityCode;
    /*
     * 详细地址
     */
    private String address;
    /*
     * 邮政编码
     */
    private String zipCode;
    /*
     * 邮箱
     */
    private String email;
    /*
     * 乡镇/街道
     */
    private String villagesTownsName;
    private String villagesTownsCode;
    /*
     * 区
     */
    private String districtName;
    private String districtCode;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public Integer getIsMandatorilyReserved() {
        return isMandatorilyReserved;
    }

    public void setIsMandatorilyReserved(Integer isMandatorilyReserved) {
        this.isMandatorilyReserved = isMandatorilyReserved;
    }

    public Integer getIsPoOvercharge() {
        return isPoOvercharge;
    }

    public void setIsPoOvercharge(Integer isPoOvercharge) {
        this.isPoOvercharge = isPoOvercharge;
    }

    public Integer getPoOverchargeProportion() {
        return poOverchargeProportion;
    }

    public void setPoOverchargeProportion(Integer poOverchargeProportion) {
        this.poOverchargeProportion = poOverchargeProportion;
    }

    public Integer getIsAsnOvercharge() {
        return isAsnOvercharge;
    }

    public void setIsAsnOvercharge(Integer isAsnOvercharge) {
        this.isAsnOvercharge = isAsnOvercharge;
    }

    public Integer getAsnOverchargeProportion() {
        return asnOverchargeProportion;
    }

    public void setAsnOverchargeProportion(Integer asnOverchargeProportion) {
        this.asnOverchargeProportion = asnOverchargeProportion;
    }

    public Integer getIsPoAutoVerify() {
        return isPoAutoVerify;
    }

    public void setIsPoAutoVerify(Integer isPoAutoVerify) {
        this.isPoAutoVerify = isPoAutoVerify;
    }

    public Integer getIsAsnAutoVerify() {
        return isAsnAutoVerify;
    }

    public void setIsAsnAutoVerify(Integer isAsnAutoVerify) {
        this.isAsnAutoVerify = isAsnAutoVerify;
    }

    public Integer getGoodsReceiptMode() {
        return goodsReceiptMode;
    }

    public void setGoodsReceiptMode(Integer goodsReceiptMode) {
        this.goodsReceiptMode = goodsReceiptMode;
    }

    public Integer getIsAutoPrintBintag() {
        return isAutoPrintBintag;
    }

    public void setIsAutoPrintBintag(Integer isAutoPrintBintag) {
        this.isAutoPrintBintag = isAutoPrintBintag;
    }

    public Integer getIsAutoGenerationCn() {
        return isAutoGenerationCn;
    }

    public void setIsAutoGenerationCn(Integer isAutoGenerationCn) {
        this.isAutoGenerationCn = isAutoGenerationCn;
    }

    public Integer getIsAllowBlocked() {
        return isAllowBlocked;
    }

    public void setIsAllowBlocked(Integer isAllowBlocked) {
        this.isAllowBlocked = isAllowBlocked;
    }

    public String getInvAttrMgmt() {
        return invAttrMgmt;
    }

    public void setInvAttrMgmt(String invAttrMgmt) {
        this.invAttrMgmt = invAttrMgmt;
    }

    public Integer getIsAllowCollectDiff() {
        return isAllowCollectDiff;
    }

    public void setIsAllowCollectDiff(Integer isAllowCollectDiff) {
        this.isAllowCollectDiff = isAllowCollectDiff;
    }

    public Integer getIsAutoPrintDiff() {
        return isAutoPrintDiff;
    }

    public void setIsAutoPrintDiff(Integer isAutoPrintDiff) {
        this.isAutoPrintDiff = isAutoPrintDiff;
    }

    public Integer getIsHintQualityTesting() {
        return isHintQualityTesting;
    }

    public void setIsHintQualityTesting(Integer isHintQualityTesting) {
        this.isHintQualityTesting = isHintQualityTesting;
    }

    public Integer getIsAutoPrintGoodsReceipt() {
        return isAutoPrintGoodsReceipt;
    }

    public void setIsAutoPrintGoodsReceipt(Integer isAutoPrintGoodsReceipt) {
        this.isAutoPrintGoodsReceipt = isAutoPrintGoodsReceipt;
    }


    public String getPicMobileTelephone() {
        return picMobileTelephone;
    }

    public void setPicMobileTelephone(String picMobileTelephone) {
        this.picMobileTelephone = picMobileTelephone;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVillagesTownsName() {
        return villagesTownsName;
    }

    public void setVillagesTownsName(String villagesTownsName) {
        this.villagesTownsName = villagesTownsName;
    }

    public String getVillagesTownsCode() {
        return villagesTownsCode;
    }

    public void setVillagesTownsCode(String villagesTownsCode) {
        this.villagesTownsCode = villagesTownsCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

}
