package com.baozun.scm.primservice.whinterface.model.inbound;

import java.io.Serializable;
import java.util.List;

public class WmsInBoundCancel implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7615395438559444391L;

    private String extPoCode;
    private String isPoCancel;
    private List<Integer> extLinenum;
    private String dataSource;

    public String getExtPoCode() {
        return extPoCode;
    }

    public void setExtPoCode(String extPoCode) {
        this.extPoCode = extPoCode;
    }

    public String getIsPoCancel() {
        return isPoCancel;
    }

    public void setIsPoCancel(String isPoCancel) {
        this.isPoCancel = isPoCancel;
    }

    public List<Integer> getExtLinenum() {
        return extLinenum;
    }

    public void setExtLinenum(List<Integer> extLinenum) {
        this.extLinenum = extLinenum;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

}
