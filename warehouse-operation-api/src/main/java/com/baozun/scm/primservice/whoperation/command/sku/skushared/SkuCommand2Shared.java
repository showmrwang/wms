package com.baozun.scm.primservice.whoperation.command.sku.skushared;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.sku.SkuBarcode;
import com.baozun.scm.primservice.whoperation.model.sku.SkuExtattr;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuWhmgmt;


public class SkuCommand2Shared extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 6949533805920551646L;

    /** 商品信息 */
    private Sku sku;
    /** 商品条码 */
    private List<SkuBarcode> skuBarcodeList;
    /** 商品扩展属性 */
    private SkuExtattr skuExtattr;
    /** 商品有效期 */
    private SkuMgmt skuMgmt;

    /** 特定多条码商品条码 */
    private SkuBarcode SkuBarcode;

    private WhSkuWhmgmt whSkuWhMgmt;

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public List<SkuBarcode> getSkuBarcodeList() {
        return skuBarcodeList;
    }

    public void setSkuBarcodeList(List<SkuBarcode> skuBarcodeList) {
        this.skuBarcodeList = skuBarcodeList;
    }

    public SkuExtattr getSkuExtattr() {
        return skuExtattr;
    }

    public void setSkuExtattr(SkuExtattr skuExtattr) {
        this.skuExtattr = skuExtattr;
    }

    public SkuMgmt getSkuMgmt() {
        return skuMgmt;
    }

    public void setSkuMgmt(SkuMgmt skuMgmt) {
        this.skuMgmt = skuMgmt;
    }

    public SkuBarcode getSkuBarcode() {
        return SkuBarcode;
    }

    public void setSkuBarcode(SkuBarcode skuBarcode) {
        SkuBarcode = skuBarcode;
    }

    public WhSkuWhmgmt getWhSkuWhMgmt() {
        return whSkuWhMgmt;
    }

    public void setWhSkuWhMgmt(WhSkuWhmgmt whSkuWhMgmt) {
        this.whSkuWhMgmt = whSkuWhMgmt;
    }

}
