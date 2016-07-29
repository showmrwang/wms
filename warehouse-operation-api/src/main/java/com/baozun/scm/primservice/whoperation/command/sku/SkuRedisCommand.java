package com.baozun.scm.primservice.whoperation.command.sku;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.sku.SkuExtattr;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuWhmgmt;

public class SkuRedisCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = -5567810545231886927L;

    /** 商品信息 */
    private Sku sku;
    /** 商品扩展属性 */
    private SkuExtattr skuExtattr;
    /** 商品有效期 */
    private SkuMgmt skuMgmt;
    /** 仓库商品管理 */
    private WhSkuWhmgmt whSkuWhMgmt;

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
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

    public WhSkuWhmgmt getWhSkuWhMgmt() {
        return whSkuWhMgmt;
    }

    public void setWhSkuWhMgmt(WhSkuWhmgmt whSkuWhMgmt) {
        this.whSkuWhMgmt = whSkuWhMgmt;
    }


}
