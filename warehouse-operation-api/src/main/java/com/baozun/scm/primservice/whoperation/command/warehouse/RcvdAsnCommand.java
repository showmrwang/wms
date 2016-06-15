package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class RcvdAsnCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = 7312796028467654177L;

    private String asnId;
    private String asnLineId;
    private String qtyPlanned;
    private String qtyRcvd;
    private String qtyOverCharge;
    private String qtyToRcvd;

    public String getQtyRcvd() {
        return qtyRcvd;
    }

    public void setQtyRcvd(String qtyRcvd) {
        this.qtyRcvd = qtyRcvd;
    }

    public String getQtyToRcvd() {
        return qtyToRcvd;
    }

    public void setQtyToRcvd(String qtyToRcvd) {
        this.qtyToRcvd = qtyToRcvd;
    }

    public String getAsnId() {
        return asnId;
    }

    public void setAsnId(String asnId) {
        this.asnId = asnId;
    }

    public String getAsnLineId() {
        return asnLineId;
    }

    public void setAsnLineId(String asnLineId) {
        this.asnLineId = asnLineId;
    }

    public String getQtyPlanned() {
        return qtyPlanned;
    }

    public void setQtyPlanned(String qtyPlanned) {
        this.qtyPlanned = qtyPlanned;
    }

    public String getQtyOverCharge() {
        return qtyOverCharge;
    }

    public void setQtyOverCharge(String qtyOverCharge) {
        this.qtyOverCharge = qtyOverCharge;
    }

}
