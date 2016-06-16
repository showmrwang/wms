package com.baozun.scm.primservice.whoperation.command.rule;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhInBoundRuleCommand;

/**
 * 调用规则传入参数
 * 
 * @author bin.hu
 * 
 */
public class RuleAfferCommand extends BaseCommand {


    private static final long serialVersionUID = -5060737922693774265L;

    /** 规则类型 */
    private String ruleType;

    /** 仓库组织ID */
    private Long ouid;

    /** 预约号 */
    private String afferReserveCode;

    /** 原始容器号 */
    private String afferContainerCode;

    /** 容器ID */
    private Long containerId;

    /** 入库分拣规则对象 */
    private WhInBoundRuleCommand whInBoundRuleCommand;

    /** 库存ID */
    private Long invId;

    /** 箱号List 运用于上架 */
    private List<String> afferContainerCodeList;

    /** 规则ID */
    private Long ruleId;

    /** 功能ID */
    private Long funcId;


    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public Long getOuid() {
        return ouid;
    }

    public void setOuid(Long ouid) {
        this.ouid = ouid;
    }

    public String getAfferReserveCode() {
        return afferReserveCode;
    }

    public void setAfferReserveCode(String afferReserveCode) {
        this.afferReserveCode = afferReserveCode;
    }

    public String getAfferContainerCode() {
        return afferContainerCode;
    }

    public void setAfferContainerCode(String afferContainerCode) {
        this.afferContainerCode = afferContainerCode;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }

    public WhInBoundRuleCommand getWhInBoundRuleCommand() {
        return whInBoundRuleCommand;
    }

    public void setWhInBoundRuleCommand(WhInBoundRuleCommand whInBoundRuleCommand) {
        this.whInBoundRuleCommand = whInBoundRuleCommand;
    }

    public Long getInvId() {
        return invId;
    }

    public void setInvId(Long invId) {
        this.invId = invId;
    }

    public List<String> getAfferContainerCodeList() {
        return afferContainerCodeList;
    }

    public void setAfferContainerCodeList(List<String> afferContainerCodeList) {
        this.afferContainerCodeList = afferContainerCodeList;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * @return the funcId
     */
    public Long getFuncId() {
        return funcId;
    }

    /**
     * @param funcId the funcId to set
     */
    public void setFuncId(Long funcId) {
        this.funcId = funcId;
    }



}
