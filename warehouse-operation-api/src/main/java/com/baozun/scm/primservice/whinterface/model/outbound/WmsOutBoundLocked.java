package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;

public class WmsOutBoundLocked implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 796688584277381777L;

    private String extOdoCode;
    private Integer locked;
    private String dataSource;
    private String whCode;


    public String getWhCode() {
        return whCode;
    }

    public void setWhCode(String whCode) {
        this.whCode = whCode;
    }

    public String getExtOdoCode() {
        return extOdoCode;
    }

    public void setExtOdoCode(String extOdoCode) {
        this.extOdoCode = extOdoCode;
    }

    public Integer getLocked() {
        return locked;
    }

    public void setLocked(Integer locked) {
        this.locked = locked;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

}
