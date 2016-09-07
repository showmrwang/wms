package com.baozun.scm.primservice.whoperation.command.odo;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLineAttr;

public class OdoLineInfoCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = -1079070669526262840L;

    /** 出库单明细 */
    private WhOdoLine whOdoLine;

    /** 出库单明细属性 */
    private WhOdoLineAttr whOdoLineAttr;

    public WhOdoLine getWhOdoLine() {
        return whOdoLine;
    }

    public void setWhOdoLine(WhOdoLine whOdoLine) {
        this.whOdoLine = whOdoLine;
    }

    public WhOdoLineAttr getWhOdoLineAttr() {
        return whOdoLineAttr;
    }

    public void setWhOdoLineAttr(WhOdoLineAttr whOdoLineAttr) {
        this.whOdoLineAttr = whOdoLineAttr;
    }

}
