package com.baozun.scm.primservice.whoperation.command.warehouse;


import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Platform;

public class RecommendPlatformCommand extends BaseCommand {


    private static final long serialVersionUID = -3758412833147551187L;
    /** 主键ID */
    private Long id;
    /** 月台推荐规则ID */
    private Long platformRecommendRuleId;
    /** 月台ID */
    private Long platformId;
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
    /** 月台 */
    private Platform platform;
    /** 推荐月台所属类型分组的月台列表 */
    private List<Platform> typeGroupPlatformList;
    /** 月台编码 */
    private String platformCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlatformRecommendRuleId() {
        return platformRecommendRuleId;
    }

    public void setPlatformRecommendRuleId(Long platformRecommendRuleId) {
        this.platformRecommendRuleId = platformRecommendRuleId;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
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

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public List<Platform> getTypeGroupPlatformList() {
        return typeGroupPlatformList;
    }

    public void setTypeGroupPlatformList(List<Platform> typeGroupPlatformList) {
        this.typeGroupPlatformList = typeGroupPlatformList;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }


}
