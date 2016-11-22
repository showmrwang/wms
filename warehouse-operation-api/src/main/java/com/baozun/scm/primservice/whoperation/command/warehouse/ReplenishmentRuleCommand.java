/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class ReplenishmentRuleCommand extends BaseCommand {
    private static final long serialVersionUID = 6781716986795103192L;

    /** 主键ID */
    private Long id;
    /** 补货规则名称 */
    private String replenishmentRuleName;
    /** 补货规则编码 */
    private String replenishmentRuleCode;
    /** 目标库位条件 */
    private String locationRule;
    /** 目标库位SQL */
    private String locationRuleSql;
    /** 货品条件 */
    private String skuRule;
    /** 货品条件SQL */
    private String skuRuleSql;
    /** 订单需求补货 */
    private Boolean orderReplenish;
    /** 货品库位容量补货 */
    private Boolean realTimeReplenish;
    /** 波次补货 */
    private Boolean waveReplenish;
    /** 仓库组织ID */
    private Long ouId;
    /** 优先级 */
    private Integer priority;
    /** 描述 */
    private String description;
    /** 创建时间 */
    private Date createTime;
    /** 创建人 */
    private Long createdId;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 最后操作人ID */
    private Long modifiedId;
    /** 是否启用 1:启用 0:停用 */
    private Integer lifecycle;
    /** 规则补货策略 */
    private List<ReplenishmentStrategyCommand> replenishmentStrategyCommandList;
    /** 匹配规则的商品ID */
    private List<Long> skuIdList;
    /** 匹配规则的库位ID */
    private List<Long> locationIdList;
    /** 规则测试结果 */
    private Boolean testResult;
    
    // 自定义
    /** 匹配目标库位 */
    private Long locationId;
    
    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReplenishmentRuleName() {
        return replenishmentRuleName;
    }

    public void setReplenishmentRuleName(String replenishmentRuleName) {
        this.replenishmentRuleName = replenishmentRuleName;
    }

    public String getReplenishmentRuleCode() {
        return replenishmentRuleCode;
    }

    public void setReplenishmentRuleCode(String replenishmentRuleCode) {
        this.replenishmentRuleCode = replenishmentRuleCode;
    }

    public String getLocationRule() {
        return locationRule;
    }

    public void setLocationRule(String locationRule) {
        this.locationRule = locationRule;
    }

    public String getLocationRuleSql() {
        return locationRuleSql;
    }

    public void setLocationRuleSql(String locationRuleSql) {
        this.locationRuleSql = locationRuleSql;
    }

    public String getSkuRule() {
        return skuRule;
    }

    public void setSkuRule(String skuRule) {
        this.skuRule = skuRule;
    }

    public String getSkuRuleSql() {
        return skuRuleSql;
    }

    public void setSkuRuleSql(String skuRuleSql) {
        this.skuRuleSql = skuRuleSql;
    }

    public Boolean getOrderReplenish() {
        return orderReplenish;
    }

    public void setOrderReplenish(Boolean orderReplenish) {
        this.orderReplenish = orderReplenish;
    }

    public Boolean getRealTimeReplenish() {
        return realTimeReplenish;
    }

    public void setRealTimeReplenish(Boolean realTimeReplenish) {
        this.realTimeReplenish = realTimeReplenish;
    }

    public Boolean getWaveReplenish() {
        return waveReplenish;
    }

    public void setWaveReplenish(Boolean waveReplenish) {
        this.waveReplenish = waveReplenish;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<ReplenishmentStrategyCommand> getReplenishmentStrategyCommandList() {
        return replenishmentStrategyCommandList;
    }

    public void setReplenishmentStrategyCommandList(List<ReplenishmentStrategyCommand> replenishmentStrategyCommandList) {
        this.replenishmentStrategyCommandList = replenishmentStrategyCommandList;
    }

    public List<Long> getSkuIdList() {
        return skuIdList;
    }

    public void setSkuIdList(List<Long> skuIdList) {
        this.skuIdList = skuIdList;
    }

    public List<Long> getLocationIdList() {
        return locationIdList;
    }

    public void setLocationIdList(List<Long> locationIdList) {
        this.locationIdList = locationIdList;
    }

    public Boolean getTestResult() {
        return testResult;
    }

    public void setTestResult(Boolean testResult) {
        this.testResult = testResult;
    }

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
}
