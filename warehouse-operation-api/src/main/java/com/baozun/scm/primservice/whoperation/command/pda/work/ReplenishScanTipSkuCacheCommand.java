package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayDeque;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class ReplenishScanTipSkuCacheCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 409863130393331262L;


    private ArrayDeque<Long> scanSkuIds = new ArrayDeque<Long>();
    /** 已复合唯一商品列表(不包含sn)*/
    private ArrayDeque<String> scanSkuAttrIds = new ArrayDeque<String>();
    /** 已复合唯一商品列表(包含sn,残次信息) */
    private ArrayDeque<String> scanSkuAttrIdSn = new ArrayDeque<String>();

    public ArrayDeque<Long> getScanSkuIds() {
        return scanSkuIds;
    }

    public void setScanSkuIds(ArrayDeque<Long> scanSkuIds) {
        this.scanSkuIds = scanSkuIds;
    }

    public ArrayDeque<String> getScanSkuAttrIds() {
        return scanSkuAttrIds;
    }

    public void setScanSkuAttrIds(ArrayDeque<String> scanSkuAttrIds) {
        this.scanSkuAttrIds = scanSkuAttrIds;
    }

    public ArrayDeque<String> getScanSkuAttrIdSn() {
        return scanSkuAttrIdSn;
    }

    public void setScanSkuAttrIdSn(ArrayDeque<String> scanSkuAttrIdSn) {
        this.scanSkuAttrIdSn = scanSkuAttrIdSn;
    }



}
