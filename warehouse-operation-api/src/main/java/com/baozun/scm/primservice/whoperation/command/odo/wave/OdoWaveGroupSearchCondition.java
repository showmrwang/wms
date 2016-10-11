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
