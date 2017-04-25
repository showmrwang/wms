/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * 
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 * 
 */
package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author larkark 国家省市表
 */
public class RegionCommand extends BaseCommand {


    /**
     * 
     */
    private static final long serialVersionUID = 238453913994000331L;
    /** id */
    private Long id;
    /** 区域名称 */
    private String regionName;
    /** 区域编码 */
    private String regionCode;
    /** 区域名称(英文) */
    private String regionNameEn;
    /** 父栏目ID */
    private Long parentId;
    /** 区域简称 */
    private String shortName;
    /** 排序号 */
    private Integer sortNo;
    /** 级别1:国家2:省3:市4:区 */
    private Integer level;
    /** 1.可用;2.已删除;0.禁用 */
    private Integer lifecycle;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人id */
    private Long operatorId;
    /** 父节点名称 */
    private String parenName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRegionName(String value) {
        this.regionName = value;
    }

    public String getRegionName() {
        return this.regionName;
    }

    public void setRegionNameEn(String value) {
        this.regionNameEn = value;
    }

    public String getRegionNameEn() {
        return this.regionNameEn;
    }

    public void setParentId(Long value) {
        this.parentId = value;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setShortName(String value) {
        this.shortName = value;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setSortNo(Integer value) {
        this.sortNo = value;
    }

    public Integer getSortNo() {
        return this.sortNo;
    }

    public void setLifecycle(Integer value) {
        this.lifecycle = value;
    }

    public Integer getLifecycle() {
        return this.lifecycle;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getParenName() {
        return parenName;
    }

    public void setParenName(String parenName) {
        this.parenName = parenName;
    }

}
