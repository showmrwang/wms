package com.baozun.scm.primservice.whoperation.command.warehouse;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * @author shenlijun
 */
public class WhAsnRcvdSnLogCommand extends BaseCommand {


    private static final long serialVersionUID = -7009193562980838416L;

    private String skuName;
    private String skuCode;
    private String containerCode;

    /** 对应ASN_RCVD_ID */
    private Long asnRcvdId;
    /** sn号 */
    private String sn;
    /** 残次品条码 */
    private String defectWareBarcode;
    /** 残次原因类型 */
    private String defectType;
    /** 残次原因 */
    private String defectReasons;
    /** 对应组织ID */
    private Long ouId;


    public void setAsnRcvdId(Long value) {
        this.asnRcvdId = value;
    }

    public Long getAsnRcvdId() {
        return this.asnRcvdId;
    }

    public void setSn(String value) {
        this.sn = value;
    }

    public String getSn() {
        return this.sn;
    }

    public void setDefectWareBarcode(String value) {
        this.defectWareBarcode = value;
    }

    public String getDefectWareBarcode() {
        return this.defectWareBarcode;
    }

    public void setDefectType(String value) {
        this.defectType = value;
    }

    public String getDefectType() {
        return this.defectType;
    }

    public void setDefectReasons(String value) {
        this.defectReasons = value;
    }

    public String getDefectReasons() {
        return this.defectReasons;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

}
