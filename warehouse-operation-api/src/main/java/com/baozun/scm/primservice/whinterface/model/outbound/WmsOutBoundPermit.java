package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;

/**
 * 出库单允许出库
 *
 */
public class WmsOutBoundPermit implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5545673705724469513L;

    /** 上位系统出库单号 */
    private String extOdoCode;
    /** 数据来源 区分上位系统 */
    private String dataSource;
    /** 仓库编码 */
    private String whCode;

    public String getExtOdoCode() {
        return extOdoCode;
    }

    public void setExtOdoCode(String extOdoCode) {
        this.extOdoCode = extOdoCode;
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
