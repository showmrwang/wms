package com.baozun.scm.primservice.whoperation.command.odo.wave;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoWaveGroupSearchCondition extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 637649388535131851L;

    private Long customerId;
    private Long storeId;
    private String odoStatus;
    /** 出库单类型 */
    private String odoType;
    /** 配货模式 */
    private String distributeMode;
    /** 运输服务商 */
    private String transportServiceProvider;
    /** 上位单据类型 */
    private String epostaticSystemsOrderType;


    private Boolean isDistributeMode;

    private Boolean isEpistaticSystemsOrderType;

    public String getOdoType() {
        return odoType;
    }

    public void setOdoType(String odoType) {
        this.odoType = odoType;
    }

    public String getDistributeMode() {
        return distributeMode;
    }

    public void setDistributeMode(String distributeMode) {
        this.distributeMode = distributeMode;
    }

    public String getTransportServiceProvider() {
        return transportServiceProvider;
    }

    public void setTransportServiceProvider(String transportServiceProvider) {
        this.transportServiceProvider = transportServiceProvider;
    }

    public String getEpostaticSystemsOrderType() {
        return epostaticSystemsOrderType;
    }

    public void setEpostaticSystemsOrderType(String epostaticSystemsOrderType) {
        this.epostaticSystemsOrderType = epostaticSystemsOrderType;
    }

    public Boolean getIsDistributeMode() {
        return isDistributeMode;
    }

    public void setIsDistributeMode(Boolean isDistributeMode) {
        this.isDistributeMode = isDistributeMode;
    }

    public Boolean getIsEpistaticSystemsOrderType() {
        return isEpistaticSystemsOrderType;
    }

    public void setIsEpistaticSystemsOrderType(Boolean isEpistaticSystemsOrderType) {
        this.isEpistaticSystemsOrderType = isEpistaticSystemsOrderType;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getOdoStatus() {
        return odoStatus;
    }

    public void setOdoStatus(String odoStatus) {
        this.odoStatus = odoStatus;
    }


}
