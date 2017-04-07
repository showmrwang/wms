package com.baozun.scm.primservice.whoperation.model.handover;

import com.baozun.scm.primservice.whoperation.model.BaseModel;


public class Handover extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 2734306414183992422L;

    /** 交接工位ID */
    private Long handoverStationId;
    /** 交接工位类型：仓库交接工位，复核台组交接工位 */
    private String handoverStationType;
    /** 交接批次 */
    private String handoverBatch;
    /** 交接分组条件 */
    private String groupCondition;
    /** 对应组织ID */
    private Long ouId;

    public Long getHandoverStationId() {
        return handoverStationId;
    }

    public void setHandoverStationId(Long handoverStationId) {
        this.handoverStationId = handoverStationId;
    }

    public String getHandoverStationType() {
        return handoverStationType;
    }

    public void setHandoverStationType(String handoverStationType) {
        this.handoverStationType = handoverStationType;
    }

    public String getHandoverBatch() {
        return handoverBatch;
    }

    public void setHandoverBatch(String handoverBatch) {
        this.handoverBatch = handoverBatch;
    }

    public String getGroupCondition() {
        return groupCondition;
    }

    public void setGroupCondition(String groupCondition) {
        this.groupCondition = groupCondition;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
}
