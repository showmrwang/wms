package com.baozun.scm.primservice.whoperation.command.poasn;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckPoCode;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public class PoCheckCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1341087110395460861L;

    private CheckPoCode checkPoCode;

    private WhPo whPo;

    private List<WhPoLine> whPoLines;

    private ResponseMsg rm;

    public CheckPoCode getCheckPoCode() {
        return checkPoCode;
    }

    public void setCheckPoCode(CheckPoCode checkPoCode) {
        this.checkPoCode = checkPoCode;
    }

    public WhPo getWhPo() {
        return whPo;
    }

    public void setWhPo(WhPo whPo) {
        this.whPo = whPo;
    }

    public List<WhPoLine> getWhPoLines() {
        return whPoLines;
    }

    public void setWhPoLines(List<WhPoLine> whPoLines) {
        this.whPoLines = whPoLines;
    }

    public ResponseMsg getRm() {
        return rm;
    }

    public void setRm(ResponseMsg rm) {
        this.rm = rm;
    }



}
