package com.baozun.scm.primservice.whoperation.command.rule;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendPlatformCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhInBoundRuleCommand;

/***
 * 调用规则输出参数结果
 * 
 * @author bin.hu
 * 
 */
public class RuleExportCommand extends BaseCommand {


    private static final long serialVersionUID = -6178748605025450454L;

    /** 是否可用 */
    private Boolean usableness;

    /** 目标容器号 */
    private String exportContainerCode;

    /** 返回月台可用信息 key: 0:找不到规则 1:找到规则单月台不可用 2:找到对应月台 */
    private Map<Integer, RecommendPlatformCommand> exportPlatformCode;

    /** 入库分拣规则对象 */
    private WhInBoundRuleCommand whInBoundRuleCommand;

    /** 上架规则 */
    private ShelveRecommendRuleCommand shelveRecommendRuleCommand;

    /** 上架规则返回 */
    private Map<Long, List<ShelveRecommendRuleCommand>> shelveMap;

    /** 上架规则List */
    private List<ShelveRecommendRuleCommand> shelveRecommendRuleList;

    public Boolean getUsableness() {
        return usableness;
    }

    public void setUsableness(Boolean usableness) {
        this.usableness = usableness;
    }

    public String getExportContainerCode() {
        return exportContainerCode;
    }

    public void setExportContainerCode(String exportContainerCode) {
        this.exportContainerCode = exportContainerCode;
    }

    public Map<Integer, RecommendPlatformCommand> getExportPlatformCode() {
        return exportPlatformCode;
    }

    public void setExportPlatformCode(Map<Integer, RecommendPlatformCommand> exportPlatformCode) {
        this.exportPlatformCode = exportPlatformCode;
    }

    public WhInBoundRuleCommand getWhInBoundRuleCommand() {
        return whInBoundRuleCommand;
    }

    public void setWhInBoundRuleCommand(WhInBoundRuleCommand whInBoundRuleCommand) {
        this.whInBoundRuleCommand = whInBoundRuleCommand;
    }

    public ShelveRecommendRuleCommand getShelveRecommendRuleCommand() {
        return shelveRecommendRuleCommand;
    }

    public void setShelveRecommendRuleCommand(ShelveRecommendRuleCommand shelveRecommendRuleCommand) {
        this.shelveRecommendRuleCommand = shelveRecommendRuleCommand;
    }

    public Map<Long, List<ShelveRecommendRuleCommand>> getShelveMap() {
        return shelveMap;
    }

    public void setShelveMap(Map<Long, List<ShelveRecommendRuleCommand>> shelveMap) {
        this.shelveMap = shelveMap;
    }

    public List<ShelveRecommendRuleCommand> getShelveRecommendRuleList() {
        return shelveRecommendRuleList;
    }

    public void setShelveRecommendRuleList(List<ShelveRecommendRuleCommand> shelveRecommendRuleList) {
        this.shelveRecommendRuleList = shelveRecommendRuleList;
    }



}
