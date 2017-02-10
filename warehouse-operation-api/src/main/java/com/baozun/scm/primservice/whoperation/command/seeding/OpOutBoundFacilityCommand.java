package com.baozun.scm.primservice.whoperation.command.seeding;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


public class OpOutBoundFacilityCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** sku条码 */
    private String skuBarCode;
    /** 商品编码 */
    private String skuCode;
    /** 商品外部编码 */
    private String skuExtCode;
    /** sku总数量 */
    private Long sumQty;
    /**(周转箱)已播种数量 */
    private Long facilityQty;
    /**(货格出库箱)已播种数量*/
    private Long latticeQty;

    private String facilityCode;
    
    private String checkCode;
    
    private Long ouId;
    
    private String turnoverBoxCode;   //周转箱
    
    private String outboundBoxCode;   //
    
    private Integer latticeNo;
    
    
    private Integer turnoverBoxStatus;    //周转箱状态
    
    public String getSkuBarCode() {
        return skuBarCode;
    }

    public void setSkuBarCode(String skuBarCode) {
        this.skuBarCode = skuBarCode;
    }

    public Long getSumQty() {
        return sumQty;
    }

    public void setSumQty(Long sumQty) {
        this.sumQty = sumQty;
    }

    public Long getFacilityQty() {
        return facilityQty;
    }

    public void setFacilityQty(Long facilityQty) {
        this.facilityQty = facilityQty;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuExtCode() {
        return skuExtCode;
    }

    public void setSkuExtCode(String skuExtCode) {
        this.skuExtCode = skuExtCode;
    }

    public Long getLatticeQty() {
        return latticeQty;
    }

    public void setLatticeQty(Long latticeQty) {
        this.latticeQty = latticeQty;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getTurnoverBoxCode() {
        return turnoverBoxCode;
    }

    public void setTurnoverBoxCode(String turnoverBoxCode) {
        this.turnoverBoxCode = turnoverBoxCode;
    }

    public String getOutboundBoxCode() {
        return outboundBoxCode;
    }

    public void setOutboundBoxCode(String outboundBoxCode) {
        this.outboundBoxCode = outboundBoxCode;
    }

    public Integer getLatticeNo() {
        return latticeNo;
    }

    public void setLatticeNo(Integer latticeNo) {
        this.latticeNo = latticeNo;
    }

    public Integer getTurnoverBoxStatus() {
        return turnoverBoxStatus;
    }

    public void setTurnoverBoxStatus(Integer turnoverBoxStatus) {
        this.turnoverBoxStatus = turnoverBoxStatus;
    }
    
    
}
