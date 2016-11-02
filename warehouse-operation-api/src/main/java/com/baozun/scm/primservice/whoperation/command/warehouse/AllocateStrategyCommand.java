package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class AllocateStrategyCommand extends BaseCommand {
    /**
     * 
     */
    private static final long serialVersionUID = -8951380192569952422L;
    // columns START
    /** 分配规则ID */
    private Long allocateRuleId;
    /** 分配区域ID */
    private Long areaId;
    /** 分配策略id */
    private Long strategyCode;
    /** 分配单位ids */
    private String allocateUnitCodes;
    /** 优先级 */
    private Integer priority;
    /** 仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 创建人ID */
    private Long createdId;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;

    // columns END

    public Long getAllocateRuleId() {
        return allocateRuleId;
    }

    public void setAllocateRuleId(Long allocateRuleId) {
        this.allocateRuleId = allocateRuleId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getStrategyCode() {
        return strategyCode;
    }

    public void setStrategyCode(Long strategyCode) {
        this.strategyCode = strategyCode;
    }

    public String getAllocateUnitCodes() {
        return allocateUnitCodes;
    }

    public void setAllocateUnitCodes(String allocateUnitCodes) {
        this.allocateUnitCodes = allocateUnitCodes;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }


}
