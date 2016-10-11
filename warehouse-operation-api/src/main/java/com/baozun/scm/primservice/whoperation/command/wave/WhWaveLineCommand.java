package com.baozun.scm.primservice.whoperation.command.wave;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;

public class WhWaveLineCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -6122809461237056322L;

    /** 波次行明细 */
    private WhWaveLine whWaveLine;

    /** 商品 */
    private Sku sku;

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


}
