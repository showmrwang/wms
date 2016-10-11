package com.baozun.scm.primservice.whoperation.command.odo.wave;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;

public class SoftAllocationCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -364209048786170897L;

    /** 商品id */
    private Long skuId;
    /** 组织id */
    private Long ouId;
    /** 商品数量 */
    private Long qtys;
    /** 商品总数量 */
    private Long sQtys;
    /** 波次行明细 */
    private WhWaveLine whWaveLine;
    /** 商品 */
    private Sku sku;
    /** 出库单明细行*/
    private WhOdoLine whOdoLine;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getQtys() {
        return qtys;
    }

    public void setQtys(Long qtys) {
        this.qtys = qtys;
    }

    public Long getsQtys() {
        return sQtys;
    }

    public void setsQtys(Long sQtys) {
        this.sQtys = sQtys;
    }

    public WhWaveLine getWhWaveLine() {
        return whWaveLine;
    }

    public void setWhWaveLine(WhWaveLine whWaveLine) {
        this.whWaveLine = whWaveLine;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public WhOdoLine getWhOdoLine() {
        return whOdoLine;
    }

    public void setWhOdoLine(WhOdoLine whOdoLine) {
        this.whOdoLine = whOdoLine;
    }

}
