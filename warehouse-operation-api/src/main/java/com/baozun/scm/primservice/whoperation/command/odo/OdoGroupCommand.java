package com.baozun.scm.primservice.whoperation.command.odo;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoInvoice;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoInvoiceLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLineAttrSn;

public class OdoGroupCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = 7375456505949641849L;
    /***/
    private OdoCommand odo;
    /** 运输商 */
    private OdoTransportMgmtCommand transPortMgmt;
    /** 明细 */
    private List<OdoLineCommand> odoLineList;
    /** 配送对象 */
    private WhOdoAddress WhOdoAddress;
    /***/
    private List<WhOdoVasCommand> odoVasList;
    /***/
    private Long userId;
    /***/
    private Long ouId;
    /***/
    private Long odoId;
    /***/
    private Long odoLineId;

    private List<WhOdoLineAttrSn> lineSnList;

    private WhOdoInvoice odoInvoice;

    private List<WhOdoInvoiceLine> odoInvoiceLineList;

    private Boolean isWms;

    public Boolean getIsWms() {
        return isWms;
    }

    public void setIsWms(Boolean isWms) {
        this.isWms = isWms;
    }

    public WhOdoInvoice getOdoInvoice() {
        return odoInvoice;
    }

    public void setOdoInvoice(WhOdoInvoice odoInvoice) {
        this.odoInvoice = odoInvoice;
    }

    public List<WhOdoInvoiceLine> getOdoInvoiceLineList() {
        return odoInvoiceLineList;
    }

    public void setOdoInvoiceLineList(List<WhOdoInvoiceLine> odoInvoiceLineList) {
        this.odoInvoiceLineList = odoInvoiceLineList;
    }

    public List<WhOdoLineAttrSn> getLineSnList() {
        return lineSnList;
    }

    public void setLineSnList(List<WhOdoLineAttrSn> lineSnList) {
        this.lineSnList = lineSnList;
    }

    public List<OdoLineCommand> getOdoLineList() {
        return odoLineList;
    }

    public void setOdoLineList(List<OdoLineCommand> odoLineList) {
        this.odoLineList = odoLineList;
    }

    public Long getOdoLineId() {
        return odoLineId;
    }

    public void setOdoLineId(Long odoLineId) {
        this.odoLineId = odoLineId;
    }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public OdoCommand getOdo() {
        return odo;
    }

    public void setOdo(OdoCommand odo) {
        this.odo = odo;
    }

    public OdoTransportMgmtCommand getTransPortMgmt() {
        return transPortMgmt;
    }

    public void setTransPortMgmt(OdoTransportMgmtCommand transPortMgmt) {
        this.transPortMgmt = transPortMgmt;
    }

    public List<WhOdoVasCommand> getOdoVasList() {
        return odoVasList;
    }

    public void setOdoVasList(List<WhOdoVasCommand> odoVasList) {
        this.odoVasList = odoVasList;
    }

    public WhOdoAddress getWhOdoAddress() {
        return WhOdoAddress;
    }

    public void setWhOdoAddress(WhOdoAddress whOdoAddress) {
        WhOdoAddress = whOdoAddress;
    }


}
