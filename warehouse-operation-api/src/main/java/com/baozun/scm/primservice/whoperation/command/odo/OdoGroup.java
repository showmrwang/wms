package com.baozun.scm.primservice.whoperation.command.odo;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLineAttrSn;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;

public class OdoGroup extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 7735452194752766065L;
    /***/
    private WhOdo odo;
    /***/
    /** 明细 */
    private List<WhOdoLine> odoLineList;
    /** 运输商 */
    private WhOdoTransportMgmt transportMgmt;
    /** 配送对象 */
    private WhOdoAddress WhOdoAddress;
    /***/
    private List<WhOdoVas> odoVasList;

    List<WhOdoLineAttrSn> lineSnList;
    /***/
    private Long userId;
    /***/
    private Long ouId;
    /***/
    private Long odoId;
    /***/
    private Long odoLineId;

    public List<WhOdoLineAttrSn> getLineSnList() {
        return lineSnList;
    }

    public void setLineSnList(List<WhOdoLineAttrSn> lineSnList) {
        this.lineSnList = lineSnList;
    }

    public WhOdo getOdo() {
        return odo;
    }

    public void setOdo(WhOdo odo) {
        this.odo = odo;
    }

    public List<WhOdoLine> getOdoLineList() {
        return odoLineList;
    }

    public void setOdoLineList(List<WhOdoLine> odoLineList) {
        this.odoLineList = odoLineList;
    }

    public WhOdoTransportMgmt getTransportMgmt() {
        return transportMgmt;
    }

    public void setTransportMgmt(WhOdoTransportMgmt transportMgmt) {
        this.transportMgmt = transportMgmt;
    }

    public WhOdoAddress getWhOdoAddress() {
        return WhOdoAddress;
    }

    public void setWhOdoAddress(WhOdoAddress whOdoAddress) {
        WhOdoAddress = whOdoAddress;
    }

    public List<WhOdoVas> getOdoVasList() {
        return odoVasList;
    }

    public void setOdoVasList(List<WhOdoVas> odoVasList) {
        this.odoVasList = odoVasList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public Long getOdoLineId() {
        return odoLineId;
    }

    public void setOdoLineId(Long odoLineId) {
        this.odoLineId = odoLineId;
    }


}
