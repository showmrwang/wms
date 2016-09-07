package com.baozun.scm.primservice.whoperation.command.odo;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoGroupCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = 7375456505949641849L;
    /***/
    private OdoCommand odo;
    /***/
    private OdoTransportMgmtCommand transPortMgmt;
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


}
