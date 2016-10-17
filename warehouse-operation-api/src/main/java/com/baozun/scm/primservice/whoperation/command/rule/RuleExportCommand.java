package com.baozun.scm.primservice.whoperation.command.rule;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendPlatformCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhInBoundRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;

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
    private Map<Integer, RecommendPlatformCommand> exportPlatform;

    /** 入库分拣规则对象 */
    private WhInBoundRuleCommand whInBoundRuleCommand;

    /** 上架规则 */
    private ShelveRecommendRuleCommand shelveRecommendRuleCommand;

    /** 拆箱上架规则返回 */
    private List<WhSkuInventoryCommand> shelveSkuInvCommandList;

    /** 上架规则List */
    private List<ShelveRecommendRuleCommand> shelveRecommendRuleList;

    /** 商品是否匹配容器 */
    private Boolean isSkuMatchContainer;

    /** 出库单明细对应分配规则 */
    private Map<List<Long>, Long> allocateRuleOdoLineId;

    /** 补货规则 */
    private List<ReplenishmentRuleCommand> replenishmentRuleCommandList;

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

    public Map<Integer, RecommendPlatformCommand> getExportPlatform() {
        return exportPlatform;
    }

    public void setExportPlatform(Map<Integer, RecommendPlatformCommand> exportPlatform) {
        this.exportPlatform = exportPlatform;
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

    public List<WhSkuInventoryCommand> getShelveSkuInvCommandList() {
        return shelveSkuInvCommandList;
    }

    public void setShelveSkuInvCommandList(List<WhSkuInventoryCommand> shelveSkuInvCommandList) {
        this.shelveSkuInvCommandList = shelveSkuInvCommandList;
    }

    public List<ShelveRecommendRuleCommand> getShelveRecommendRuleList() {
        return shelveRecommendRuleList;
    }

    public void setShelveRecommendRuleList(List<ShelveRecommendRuleCommand> shelveRecommendRuleList) {
        this.shelveRecommendRuleList = shelveRecommendRuleList;
    }

    public Boolean getIsSkuMatchContainer() {
        return isSkuMatchContainer;
    }

    public void setIsSkuMatchContainer(Boolean isSkuMatchContainer) {
        this.isSkuMatchContainer = isSkuMatchContainer;
    }

    public Map<List<Long>, Long> getAllocateRuleOdoLineId() {
        return allocateRuleOdoLineId;
    }

    public void setAllocateRuleOdoLineId(Map<List<Long>, Long> allocateRuleOdoLineId) {
        this.allocateRuleOdoLineId = allocateRuleOdoLineId;
    }

    public List<ReplenishmentRuleCommand> getReplenishmentRuleCommandList() {
        return replenishmentRuleCommandList;
    }

    public void setReplenishmentRuleCommandList(List<ReplenishmentRuleCommand> replenishmentRuleCommandList) {
        this.replenishmentRuleCommandList = replenishmentRuleCommandList;
    }
}
