package com.baozun.scm.primservice.whoperation.command.poasn;

import java.io.Serializable;
import java.util.List;

import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckAsnCode;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;

public class AsnCheckCommand implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4027666733776469974L;

    private CheckAsnCode checkAsnCode;

    private WhAsn whAsn;

    private List<WhAsnLine> whAsnLines;

    private ResponseMsg rm;

    public CheckAsnCode getCheckAsnCode() {
        return checkAsnCode;
    }

    public void setCheckAsnCode(CheckAsnCode checkAsnCode) {
        this.checkAsnCode = checkAsnCode;
    }

    public WhAsn getWhAsn() {
        return whAsn;
    }

    public void setWhAsn(WhAsn whAsn) {
        this.whAsn = whAsn;
    }

    public List<WhAsnLine> getWhAsnLines() {
        return whAsnLines;
    }

    public void setWhAsnLines(List<WhAsnLine> whAsnLines) {
        this.whAsnLines = whAsnLines;
    }

    public ResponseMsg getRm() {
        return rm;
    }

    public void setRm(ResponseMsg rm) {
        this.rm = rm;
    }

}
