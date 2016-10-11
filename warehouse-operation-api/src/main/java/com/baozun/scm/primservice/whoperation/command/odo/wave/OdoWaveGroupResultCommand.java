package com.baozun.scm.primservice.whoperation.command.odo.wave;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoWaveGroupResultCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 2904637294225979289L;

    /** 组别名称 */
    private String groupName;
    /** 客户 */
    private Long customerId;
    private String customerName;
    /** 店铺 */
    private Long storeId;
    private String storeName;
    /** 出库单状态 */
    private String odoStatus;
    private String odoStatusName;
    /** 订单数 */
    private Long odoCount;
    /** 明细行总数数 */
    private Long lineCount;
    /** 总件数 */
    private Long qty;
    /** 商品种类数 */
    private Long skuTypeCount;
    /** 店铺数量 */
    private Long storeCount;
    /** 出库单类型数 */
    private Long odoTypeCount;

    public Long getLineCount() {
        return lineCount;
    }

    public void setLineCount(Long lineCount) {
        this.lineCount = lineCount;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getSkuTypeCount() {
        return skuTypeCount;
    }

    public void setSkuTypeCount(Long skuTypeCount) {
        this.skuTypeCount = skuTypeCount;
    }

    public Long getStoreCount() {
        return storeCount;
    }

    public void setStoreCount(Long storeCount) {
        this.storeCount = storeCount;
    }

    public Long getOdoTypeCount() {
        return odoTypeCount;
    }

    public void setOdoTypeCount(Long odoTypeCount) {
        this.odoTypeCount = odoTypeCount;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getOdoStatus() {
        return odoStatus;
    }

    public void setOdoStatus(String odoStatus) {
        this.odoStatus = odoStatus;
    }

    public Long getOdoCount() {
        return odoCount;
    }

    public void setOdoCount(Long odoCount) {
        this.odoCount = odoCount;
    }


    public String getOdoStatusName() {
        return odoStatusName;
    }

    public void setOdoStatusName(String odoStatusName) {
        this.odoStatusName = odoStatusName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }



}
