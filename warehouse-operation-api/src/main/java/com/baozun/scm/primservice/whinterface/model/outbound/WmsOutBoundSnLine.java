package com.baozun.scm.primservice.whinterface.model.outbound;

import java.io.Serializable;

public class WmsOutBoundSnLine implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6116051737328975177L;
    private String  sn  ;// Sn号
    private String  defectWareBarcode   ;// 残次条码
    private String  defectSource    ;// 残次原因来源 STORE店铺 WH仓库
    private String  defectType  ;// 残次原因类型CODE
    private String  defectReasons   ;// 残次原因CODE
    public String getSn() {
        return sn;
    }
    public void setSn(String sn) {
        this.sn = sn;
    }
    public String getDefectWareBarcode() {
        return defectWareBarcode;
    }
    public void setDefectWareBarcode(String defectWareBarcode) {
        this.defectWareBarcode = defectWareBarcode;
    }
    public String getDefectSource() {
        return defectSource;
    }
    public void setDefectSource(String defectSource) {
        this.defectSource = defectSource;
    }
    public String getDefectType() {
        return defectType;
    }
    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }
    public String getDefectReasons() {
        return defectReasons;
    }
    public void setDefectReasons(String defectReasons) {
        this.defectReasons = defectReasons;
    }

}
