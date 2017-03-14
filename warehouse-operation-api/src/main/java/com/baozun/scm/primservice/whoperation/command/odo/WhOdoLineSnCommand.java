package com.baozun.scm.primservice.whoperation.command.odo;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhOdoLineSnCommand extends BaseCommand{
    /**
     * 
     */
    private static final long serialVersionUID = 5372918898410860891L;
    /** 出库单明细ID */
    private Long odoLineId;
    /** sn号 */
    private String sn;
    /** 残次品条码 */
    private String defectWareBarcode;
    /** 残次原因类型名称 */
    private String defectType;
    /** 残次原因名称 */
    private String defectReasons;
    /** 对应仓库ID */
    private Long ouId;

    // ================
    /** 出库单明细外接行号 */
    private String extLinenum;


    public String getExtLinenum() {
        return extLinenum;
    }

    public void setExtLinenum(String extLinenum) {
        this.extLinenum = extLinenum;
    }
    public Long getOdoLineId() {
        return odoLineId;
    }
    public void setOdoLineId(Long odoLineId) {
        this.odoLineId = odoLineId;
    }
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
    public Long getOuId() {
        return ouId;
    }
    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    
    
}
