package com.baozun.scm.primservice.whoperation.command.odo.wave;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class SoftAllocationResponseCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -5079222778481500744L;
    /** 当前软分配是否成功*/
    private boolean isSuccess;
    /** 空列表*/
    private List<Long> emptyQtyList;
    /** 剩余库存*/
    private Long qty;
    /** 当前sku id*/
    private Long skuId;
    /** 商品数量map*/
    private Map<Long, Long> skuInvAvailableQtyMap;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<Long> getEmptyQtyList() {
        return emptyQtyList;
    }

    public void setEmptyQtyList(List<Long> emptyQtyList) {
        this.emptyQtyList = emptyQtyList;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Map<Long, Long> getSkuInvAvailableQtyMap() {
        return skuInvAvailableQtyMap;
    }

    public void setSkuInvAvailableQtyMap(Map<Long, Long> skuInvAvailableQtyMap) {
        this.skuInvAvailableQtyMap = skuInvAvailableQtyMap;
    }

}
