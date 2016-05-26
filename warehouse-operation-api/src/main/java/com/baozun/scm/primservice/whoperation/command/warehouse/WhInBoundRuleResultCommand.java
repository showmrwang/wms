package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhInBoundRuleResultCommand extends BaseCommand {

    private static final long serialVersionUID = -7635200299901826968L;
    /** 商品编码 */
    private String skuCode;
    /** 尺码 */
    private String skuSize;
    /** 款式 */
    private String skuStyle;
    /** 颜色 */
    private String skuColor;
    /** 货品类型 */
    private Long skuTypeOfGoods;
    /** 所属品牌ID */
    private Long skuBrandId;
    /** 是否管理效期 有效期商品 */
    private Boolean skuIsValid;
    /** 是否管理批次号 */
    private Boolean skuIsBatchNo;
    /** 序列号管理类型 */
    private Long skuSerialNumberType;
    /** 是否管理原产地 */
    private Boolean skuIsCountryOfOrigin;
    /** 是否管理库存类型 */
    private Boolean skuIsInvType;
    /** 是否管理SKU属性 */
    private Boolean skuIsSkuAttr;
    /** 是否易碎品 */
    private Boolean skuFragileCargo;
    /** 是否混放 */
    private Boolean skuIsMixAllowed;
    /** 是否危险品 */
    private Boolean skuIsHazardousCargo;
    /** 是否贵重物品 */
    private Boolean skuIsValuables;
    /** 存储条件 */
    private String skuStorageCondition;
    /** 商品类型1 */
    private Long skuType1;
    /** 商品类型2 */
    private Long skuType2;
    /** 商品类型3 */
    private Long skuType3;
    /** 商品类型4 */
    private Long skuType4;
    /** 商品类型5 */
    private Long skuType5;
    /** 商品类型6 */
    private Long skuType6;
    /** 商品类型7 */
    private Long skuType7;
    /** 商品类型8 */
    private Long skuType8;
    /** 商品类型9 */
    private Long skuType9;
    /** 商品类型10 */
    private Long skuType10;
    /** 是否库存属性1 */
    private Boolean skuInvAttr1;
    /** 是否库存属性2 */
    private Boolean skuInvAttr2;
    /** 是否库存属性3 */
    private Boolean skuInvAttr3;
    /** 是否库存属性4 */
    private Boolean skuInvAttr4;
    /** 是否库存属性5 */
    private Boolean skuInvAttr5;
    /** 自定义1 */
    private String skuUserDefined1;
    /** 自定义2 */
    private String skuUserDefined2;
    /** 自定义3 */
    private String skuUserDefined3;
    /** 自定义4 */
    private String skuUserDefined4;
    /** 自定义5 */
    private String skuUserDefined5;
    /** 自定义6 */
    private String skuUserDefined6;
    /** 自定义7 */
    private String skuUserDefined7;
    /** 自定义8 */
    private String skuUserDefined8;
    /** 自定义9 */
    private String skuUserDefined9;
    /** 自定义10 */
    private String skuUserDefined10;
    /** 库存类型 */
    private String invType;
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
    /** 批次号 */
    private String invBatchNumber;
    /** 生产日期 */
    private Date invMfgDate;
    /** 失效日期 */
    private Date invExpDate;
    /** 客户ID */
    private Long invCustomerId;
    /** 店铺ID */
    private Long invStoreId;
    /** 原产地 */
    private String invCountryOfOrigin;
    /** 商品ID */
    private Long invSkuId;
    /** 客户编号 */
    private String customerCode;
    /** 客户类型 */
    private Long customerType;
    /** 结算方式 */
    private Long customerPaymentTerm;
    /** 店铺编码 */
    private String storeCode;
    /** 结算方式 */
    private Long storePaymentTerm;

    //以下属性不再equals比较内
    /** 占用码 */
    private String occupationCode;
    /** 组织ID */
    private Long ouId;
    /** 容器编码 */
    private String containerCode;

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuSize() {
        return skuSize;
    }

    public void setSkuSize(String skuSize) {
        this.skuSize = skuSize;
    }

    public String getSkuStyle() {
        return skuStyle;
    }

    public void setSkuStyle(String skuStyle) {
        this.skuStyle = skuStyle;
    }

    public String getSkuColor() {
        return skuColor;
    }

    public void setSkuColor(String skuColor) {
        this.skuColor = skuColor;
    }

    public Long getSkuTypeOfGoods() {
        return skuTypeOfGoods;
    }

    public void setSkuTypeOfGoods(Long skuTypeOfGoods) {
        this.skuTypeOfGoods = skuTypeOfGoods;
    }

    public Long getSkuBrandId() {
        return skuBrandId;
    }

    public void setSkuBrandId(Long skuBrandId) {
        this.skuBrandId = skuBrandId;
    }

    public Boolean getSkuIsValid() {
        return skuIsValid;
    }

    public void setSkuIsValid(Boolean skuIsValid) {
        this.skuIsValid = skuIsValid;
    }

    public Boolean getSkuIsBatchNo() {
        return skuIsBatchNo;
    }

    public void setSkuIsBatchNo(Boolean skuIsBatchNo) {
        this.skuIsBatchNo = skuIsBatchNo;
    }

    public Long getSkuSerialNumberType() {
        return skuSerialNumberType;
    }

    public void setSkuSerialNumberType(Long skuSerialNumberType) {
        this.skuSerialNumberType = skuSerialNumberType;
    }

    public Boolean getSkuIsCountryOfOrigin() {
        return skuIsCountryOfOrigin;
    }

    public void setSkuIsCountryOfOrigin(Boolean skuIsCountryOfOrigin) {
        this.skuIsCountryOfOrigin = skuIsCountryOfOrigin;
    }

    public Boolean getSkuIsInvType() {
        return skuIsInvType;
    }

    public void setSkuIsInvType(Boolean skuIsInvType) {
        this.skuIsInvType = skuIsInvType;
    }

    public Boolean getSkuIsSkuAttr() {
        return skuIsSkuAttr;
    }

    public void setSkuIsSkuAttr(Boolean skuIsSkuAttr) {
        this.skuIsSkuAttr = skuIsSkuAttr;
    }

    public Boolean getSkuFragileCargo() {
        return skuFragileCargo;
    }

    public void setSkuFragileCargo(Boolean skuFragileCargo) {
        this.skuFragileCargo = skuFragileCargo;
    }

    public Boolean getSkuIsMixAllowed() {
        return skuIsMixAllowed;
    }

    public void setSkuIsMixAllowed(Boolean skuIsMixAllowed) {
        this.skuIsMixAllowed = skuIsMixAllowed;
    }

    public Boolean getSkuIsHazardousCargo() {
        return skuIsHazardousCargo;
    }

    public void setSkuIsHazardousCargo(Boolean skuIsHazardousCargo) {
        this.skuIsHazardousCargo = skuIsHazardousCargo;
    }

    public Boolean getSkuIsValuables() {
        return skuIsValuables;
    }

    public void setSkuIsValuables(Boolean skuIsValuables) {
        this.skuIsValuables = skuIsValuables;
    }

    public String getSkuStorageCondition() {
        return skuStorageCondition;
    }

    public void setSkuStorageCondition(String skuStorageCondition) {
        this.skuStorageCondition = skuStorageCondition;
    }

    public Long getSkuType1() {
        return skuType1;
    }

    public void setSkuType1(Long skuType1) {
        this.skuType1 = skuType1;
    }

    public Long getSkuType2() {
        return skuType2;
    }

    public void setSkuType2(Long skuType2) {
        this.skuType2 = skuType2;
    }

    public Long getSkuType3() {
        return skuType3;
    }

    public void setSkuType3(Long skuType3) {
        this.skuType3 = skuType3;
    }

    public Long getSkuType4() {
        return skuType4;
    }

    public void setSkuType4(Long skuType4) {
        this.skuType4 = skuType4;
    }

    public Long getSkuType5() {
        return skuType5;
    }

    public void setSkuType5(Long skuType5) {
        this.skuType5 = skuType5;
    }

    public Long getSkuType6() {
        return skuType6;
    }

    public void setSkuType6(Long skuType6) {
        this.skuType6 = skuType6;
    }

    public Long getSkuType7() {
        return skuType7;
    }

    public void setSkuType7(Long skuType7) {
        this.skuType7 = skuType7;
    }

    public Long getSkuType8() {
        return skuType8;
    }

    public void setSkuType8(Long skuType8) {
        this.skuType8 = skuType8;
    }

    public Long getSkuType9() {
        return skuType9;
    }

    public void setSkuType9(Long skuType9) {
        this.skuType9 = skuType9;
    }

    public Long getSkuType10() {
        return skuType10;
    }

    public void setSkuType10(Long skuType10) {
        this.skuType10 = skuType10;
    }

    public Boolean getSkuInvAttr1() {
        return skuInvAttr1;
    }

    public void setSkuInvAttr1(Boolean skuInvAttr1) {
        this.skuInvAttr1 = skuInvAttr1;
    }

    public Boolean getSkuInvAttr2() {
        return skuInvAttr2;
    }

    public void setSkuInvAttr2(Boolean skuInvAttr2) {
        this.skuInvAttr2 = skuInvAttr2;
    }

    public Boolean getSkuInvAttr3() {
        return skuInvAttr3;
    }

    public void setSkuInvAttr3(Boolean skuInvAttr3) {
        this.skuInvAttr3 = skuInvAttr3;
    }

    public Boolean getSkuInvAttr4() {
        return skuInvAttr4;
    }

    public void setSkuInvAttr4(Boolean skuInvAttr4) {
        this.skuInvAttr4 = skuInvAttr4;
    }

    public Boolean getSkuInvAttr5() {
        return skuInvAttr5;
    }

    public void setSkuInvAttr5(Boolean skuInvAttr5) {
        this.skuInvAttr5 = skuInvAttr5;
    }

    public String getSkuUserDefined1() {
        return skuUserDefined1;
    }

    public void setSkuUserDefined1(String skuUserDefined1) {
        this.skuUserDefined1 = skuUserDefined1;
    }

    public String getSkuUserDefined2() {
        return skuUserDefined2;
    }

    public void setSkuUserDefined2(String skuUserDefined2) {
        this.skuUserDefined2 = skuUserDefined2;
    }

    public String getSkuUserDefined3() {
        return skuUserDefined3;
    }

    public void setSkuUserDefined3(String skuUserDefined3) {
        this.skuUserDefined3 = skuUserDefined3;
    }

    public String getSkuUserDefined4() {
        return skuUserDefined4;
    }

    public void setSkuUserDefined4(String skuUserDefined4) {
        this.skuUserDefined4 = skuUserDefined4;
    }

    public String getSkuUserDefined5() {
        return skuUserDefined5;
    }

    public void setSkuUserDefined5(String skuUserDefined5) {
        this.skuUserDefined5 = skuUserDefined5;
    }

    public String getSkuUserDefined6() {
        return skuUserDefined6;
    }

    public void setSkuUserDefined6(String skuUserDefined6) {
        this.skuUserDefined6 = skuUserDefined6;
    }

    public String getSkuUserDefined7() {
        return skuUserDefined7;
    }

    public void setSkuUserDefined7(String skuUserDefined7) {
        this.skuUserDefined7 = skuUserDefined7;
    }

    public String getSkuUserDefined8() {
        return skuUserDefined8;
    }

    public void setSkuUserDefined8(String skuUserDefined8) {
        this.skuUserDefined8 = skuUserDefined8;
    }

    public String getSkuUserDefined9() {
        return skuUserDefined9;
    }

    public void setSkuUserDefined9(String skuUserDefined9) {
        this.skuUserDefined9 = skuUserDefined9;
    }

    public String getSkuUserDefined10() {
        return skuUserDefined10;
    }

    public void setSkuUserDefined10(String skuUserDefined10) {
        this.skuUserDefined10 = skuUserDefined10;
    }

    public String getInvType() {
        return invType;
    }

    public void setInvType(String invType) {
        this.invType = invType;
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

    public String getInvBatchNumber() {
        return invBatchNumber;
    }

    public void setInvBatchNumber(String invBatchNumber) {
        this.invBatchNumber = invBatchNumber;
    }

    public Date getInvMfgDate() {
        return invMfgDate;
    }

    public void setInvMfgDate(Date invMfgDate) {
        this.invMfgDate = invMfgDate;
    }

    public Date getInvExpDate() {
        return invExpDate;
    }

    public void setInvExpDate(Date invExpDate) {
        this.invExpDate = invExpDate;
    }

    public Long getInvCustomerId() {
        return invCustomerId;
    }

    public void setInvCustomerId(Long invCustomerId) {
        this.invCustomerId = invCustomerId;
    }

    public Long getInvStoreId() {
        return invStoreId;
    }

    public void setInvStoreId(Long invStoreId) {
        this.invStoreId = invStoreId;
    }

    public String getInvCountryOfOrigin() {
        return invCountryOfOrigin;
    }

    public void setInvCountryOfOrigin(String invCountryOfOrigin) {
        this.invCountryOfOrigin = invCountryOfOrigin;
    }

    public Long getInvSkuId() {
        return invSkuId;
    }

    public void setInvSkuId(Long invSkuId) {
        this.invSkuId = invSkuId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public Long getCustomerType() {
        return customerType;
    }

    public void setCustomerType(Long customerType) {
        this.customerType = customerType;
    }

    public Long getCustomerPaymentTerm() {
        return customerPaymentTerm;
    }

    public void setCustomerPaymentTerm(Long customerPaymentTerm) {
        this.customerPaymentTerm = customerPaymentTerm;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public Long getStorePaymentTerm() {
        return storePaymentTerm;
    }

    public void setStorePaymentTerm(Long storePaymentTerm) {
        this.storePaymentTerm = storePaymentTerm;
    }

    public String getOccupationCode() {
        return occupationCode;
    }

    public void setOccupationCode(String occupationCode) {
        this.occupationCode = occupationCode;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WhInBoundRuleResultCommand)) return false;

        WhInBoundRuleResultCommand other = (WhInBoundRuleResultCommand) obj;

        return new EqualsBuilder()
                .append(getSkuCode(), other.getSkuCode())
                .append(getSkuSize(), other.getSkuSize())
                .append(getSkuStyle(), other.getSkuStyle())
                .append(getSkuColor(), other.getSkuColor())
                .append(getSkuTypeOfGoods(), other.getSkuTypeOfGoods())
                .append(getSkuBrandId(), other.getSkuBrandId())
                .append(getSkuIsValid(), other.getSkuIsValid())
                .append(getSkuIsBatchNo(), other.getSkuIsBatchNo())
                .append(getSkuSerialNumberType(), other.getSkuSerialNumberType())
                .append(getSkuIsCountryOfOrigin(), other.getSkuIsCountryOfOrigin())
                .append(getSkuIsInvType(), other.getSkuIsInvType())
                .append(getSkuIsSkuAttr(), other.getSkuIsSkuAttr())
                .append(getSkuFragileCargo(), other.getSkuFragileCargo())
                .append(getSkuIsMixAllowed(), other.getSkuIsMixAllowed())
                .append(getSkuIsHazardousCargo(), other.getSkuIsHazardousCargo())
                .append(getSkuIsValuables(), other.getSkuIsValuables())
                .append(getSkuStorageCondition(), other.getSkuStorageCondition())
                .append(getSkuType1(), other.getSkuType1())
                .append(getSkuType2(), other.getSkuType2())
                .append(getSkuType3(), other.getSkuType3())
                .append(getSkuType4(), other.getSkuType4())
                .append(getSkuType5(), other.getSkuType5())
                .append(getSkuType6(), other.getSkuType6())
                .append(getSkuType7(), other.getSkuType7())
                .append(getSkuType8(), other.getSkuType8())
                .append(getSkuType9(), other.getSkuType9())
                .append(getSkuType10(), other.getSkuType10())
                .append(getSkuInvAttr1(), other.getSkuInvAttr1())
                .append(getSkuInvAttr2(), other.getSkuInvAttr2())
                .append(getSkuInvAttr3(), other.getSkuInvAttr3())
                .append(getSkuInvAttr4(), other.getSkuInvAttr4())
                .append(getSkuInvAttr5(), other.getSkuInvAttr5())
                .append(getSkuUserDefined1(), other.getSkuUserDefined1())
                .append(getSkuUserDefined2(), other.getSkuUserDefined2())
                .append(getSkuUserDefined3(), other.getSkuUserDefined3())
                .append(getSkuUserDefined4(), other.getSkuUserDefined4())
                .append(getSkuUserDefined5(), other.getSkuUserDefined5())
                .append(getSkuUserDefined6(), other.getSkuUserDefined6())
                .append(getSkuUserDefined7(), other.getSkuUserDefined7())
                .append(getSkuUserDefined8(), other.getSkuUserDefined8())
                .append(getSkuUserDefined9(), other.getSkuUserDefined9())
                .append(getSkuUserDefined10(), other.getSkuUserDefined10())
                .append(getInvType(), other.getInvType())
                .append(getInvStatus(), other.getInvStatus())
                .append(getInvAttr1(), other.getInvAttr1())
                .append(getInvAttr2(), other.getInvAttr2())
                .append(getInvAttr3(), other.getInvAttr3())
                .append(getInvAttr4(), other.getInvAttr4())
                .append(getInvAttr5(), other.getInvAttr5())
                .append(getInvBatchNumber(), other.getInvBatchNumber())
                .append(getInvMfgDate(), other.getInvMfgDate())
                .append(getInvExpDate(), other.getInvExpDate())
                .append(getInvCustomerId(), other.getInvCustomerId())
                .append(getInvStoreId(), other.getInvStoreId())
                .append(getInvCountryOfOrigin(), other.getInvCountryOfOrigin())
                .append(getInvSkuId(), other.getInvSkuId())
                .append(getCustomerCode(), other.getCustomerCode())
                .append(getCustomerType(), other.getCustomerType())
                .append(getCustomerPaymentTerm(), other.getCustomerPaymentTerm())
                .append(getStoreCode(), other.getStoreCode())
                .append(getStorePaymentTerm(), other.getStorePaymentTerm())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getSkuCode())
                .append(getSkuSize())
                .append(getSkuStyle())
                .append(getSkuColor())
                .append(getSkuTypeOfGoods())
                .append(getSkuBrandId())
                .append(getSkuIsValid())
                .append(getSkuIsBatchNo())
                .append(getSkuSerialNumberType())
                .append(getSkuIsCountryOfOrigin())
                .append(getSkuIsInvType())
                .append(getSkuIsSkuAttr())
                .append(getSkuFragileCargo())
                .append(getSkuIsMixAllowed())
                .append(getSkuIsHazardousCargo())
                .append(getSkuIsValuables())
                .append(getSkuStorageCondition())
                .append(getSkuType1())
                .append(getSkuType2())
                .append(getSkuType3())
                .append(getSkuType4())
                .append(getSkuType5())
                .append(getSkuType6())
                .append(getSkuType7())
                .append(getSkuType8())
                .append(getSkuType9())
                .append(getSkuType10())
                .append(getSkuInvAttr1())
                .append(getSkuInvAttr2())
                .append(getSkuInvAttr3())
                .append(getSkuInvAttr4())
                .append(getSkuInvAttr5())
                .append(getSkuUserDefined1())
                .append(getSkuUserDefined2())
                .append(getSkuUserDefined3())
                .append(getSkuUserDefined4())
                .append(getSkuUserDefined5())
                .append(getSkuUserDefined6())
                .append(getSkuUserDefined7())
                .append(getSkuUserDefined8())
                .append(getSkuUserDefined9())
                .append(getSkuUserDefined10())
                .append(getInvType())
                .append(getInvStatus())
                .append(getInvAttr1())
                .append(getInvAttr2())
                .append(getInvAttr3())
                .append(getInvAttr4())
                .append(getInvAttr5())
                .append(getInvBatchNumber())
                .append(getInvMfgDate())
                .append(getInvExpDate())
                .append(getInvCustomerId())
                .append(getInvStoreId())
                .append(getInvCountryOfOrigin())
                .append(getInvSkuId())
                .append(getCustomerCode())
                .append(getCustomerType())
                .append(getCustomerPaymentTerm())
                .append(getStoreCode())
                .append(getStorePaymentTerm())
                .toHashCode();
    }

}
