package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;
import java.util.List;

public class WmsOutBoundCancel implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4378214920791522377L;
    private String extOdoCode;
    private Boolean isOdoCancel;
    private List<Integer> lineSeq;
    private String dataSource;
    private String whCode;

    public String getExtOdoCode() {
        return extOdoCode;
    }

    public void setExtOdoCode(String extOdoCode) {
        this.extOdoCode = extOdoCode;
    }

    public Boolean getIsOdoCancel() {
        return isOdoCancel;
    }

    public void setIsOdoCancel(Boolean isOdoCancel) {
        this.isOdoCancel = isOdoCancel;
    }

    public List<Integer> getLineSeq() {
        return lineSeq;
    }

    public void setLineSeq(List<Integer> lineSeq) {
        this.lineSeq = lineSeq;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }



}
