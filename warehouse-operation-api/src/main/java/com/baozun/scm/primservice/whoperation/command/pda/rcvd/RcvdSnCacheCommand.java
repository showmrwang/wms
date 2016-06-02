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
    /** 当没有SN号时候，会有数量 */
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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



}
