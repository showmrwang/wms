package com.baozun.scm.primservice.whoperation.command.odo;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAttr;

public class OdoInfoCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = 3260651040078119729L;

    /** 出库单主档 */
    private WhOdo whOdo;

    /** 出库单主档属性 */
    private WhOdoAttr whOdoAttr;

    /** 出库单明细 */
    private List<OdoLineInfoCommand> whOdoLineInfoCommandList;

    /** 出库单配送对象 */
    private WhOdoAddress whOdoAddress;

    public WhOdo getWhOdo() {
        return whOdo;
    }

    public void setWhOdo(WhOdo whOdo) {
        this.whOdo = whOdo;
    }

    public WhOdoAttr getWhOdoAttr() {
        return whOdoAttr;
    }

    public void setWhOdoAttr(WhOdoAttr whOdoAttr) {
        this.whOdoAttr = whOdoAttr;
    }

    public List<OdoLineInfoCommand> getWhOdoLineInfoCommandList() {
        return whOdoLineInfoCommandList;
    }

    public void setWhOdoLineInfoCommandList(List<OdoLineInfoCommand> whOdoLineInfoCommandList) {
        this.whOdoLineInfoCommandList = whOdoLineInfoCommandList;
    }

    public WhOdoAddress getWhOdoAddress() {
        return whOdoAddress;
    }

    public void setWhOdoAddress(WhOdoAddress whOdoAddress) {
        this.whOdoAddress = whOdoAddress;
    }
}
