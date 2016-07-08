package com.baozun.scm.primservice.whoperation.command.pda.rcvd;

import java.io.Serializable;

public class RcvdSnCacheCommand implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -9086750914630638929L;

    /** sn号 */
    private String sn;
    /** 残次原因类型ID */
    private Long defectTypeId;
    /** 残次原因ID */
    private Long defectReasonsId;
    /** 残次来源 */
    private String defectSource;
    /** 序列号管理类型 */
    private String serialNumberType;

    public String getDefectSource() {
        return defectSource;
    }

    public void setDefectSource(String defectSource) {
        this.defectSource = defectSource;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Long getDefectTypeId() {
        return defectTypeId;
    }

    public void setDefectTypeId(Long defectTypeId) {
        this.defectTypeId = defectTypeId;
    }

    public Long getDefectReasonsId() {
        return defectReasonsId;
    }

    public void setDefectReasonsId(Long defectReasonsId) {
        this.defectReasonsId = defectReasonsId;
    }

    public String getSerialNumberType() {
        return serialNumberType;
    }

    public void setSerialNumberType(String serialNumberType) {
        this.serialNumberType = serialNumberType;
    }



}
