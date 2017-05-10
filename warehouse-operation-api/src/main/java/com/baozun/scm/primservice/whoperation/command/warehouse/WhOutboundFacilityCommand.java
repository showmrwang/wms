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
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 出库设施基础信息
 * 
 * @author larkark
 * 
 */
public class WhOutboundFacilityCommand extends BaseCommand {

    private static final long serialVersionUID = -2583241233463964707L;

    /** 主键ID */
    private Long Id;
    /** 编码 */
    private String facilityCode;
    /** 名称 */
    private String facilityName;
    /** 类型 */
    private String facilityType;
    /** 出库箱类型 */
    private Long outboundboxTypeId;
    /** 作业/容器容量 */
    private Integer capacity;
    /** 顺序优先级 */
    private Integer priority;
    /** 设施容量下限 */
    private Integer facilityLowerLimit;
    /** 设施容量上限 */
    private Integer facilityUpperLimit;
    /** 分组ID */
    private Long facilityGroup;
    /** 效验码 */
    private String checkCode;
    /** 设施状态 */
    private String status;
    /** 占用批次号 */
    private String batch;
    /** 创建时间 */
    private Date createTime;
    /** 修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;
    /** 1.可用;2.已删除;0.禁用 */
    private Integer lifecycle;
    /** 仓库组织ID */
    private Long ouId;
    /** mac地址 */
    private String mac;

    // 自定义
    /** 类型名称 */
    private String facilityTypeName;
    /** 出库箱类型名称 */
    private String outboundboxTypeName;
    /** 出库设施分组名称 */
    private String facilityGroupName;
    /** 类型名称集合 */
    private List<String> facilityTypeList;
    /** 出库箱名称集合 */
    private List<Long> outboundboxTypeList;
    /** 分组集合 */
    private List<Long> facilityGroupList;
    /** 复核暂存库位集合 */
    private List<Long> checkTableIdList;
    /** 正在播种的容器数量 */
    private Integer seedingCount;

    public Long getId() {
        return Id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public Long getOutboundboxTypeId() {
        return outboundboxTypeId;
    }

    public void setOutboundboxTypeId(Long outboundboxTypeId) {
        this.outboundboxTypeId = outboundboxTypeId;
    }

    public Long getFacilityGroup() {
        return facilityGroup;
    }

    public void setFacilityGroup(Long facilityGroup) {
        this.facilityGroup = facilityGroup;
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

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getFacilityTypeName() {
        return facilityTypeName;
    }

    public void setFacilityTypeName(String facilityTypeName) {
        this.facilityTypeName = facilityTypeName;
    }

    public String getOutboundboxTypeName() {
        return outboundboxTypeName;
    }

    public void setOutboundboxTypeName(String outboundboxTypeName) {
        this.outboundboxTypeName = outboundboxTypeName;
    }

    public String getFacilityGroupName() {
        return facilityGroupName;
    }

    public void setFacilityGroupName(String facilityGroupName) {
        this.facilityGroupName = facilityGroupName;
    }

    public List<String> getFacilityTypeList() {
        return facilityTypeList;
    }

    public void setFacilityTypeList(List<String> facilityTypeList) {
        this.facilityTypeList = facilityTypeList;
    }

    public List<Long> getOutboundboxTypeList() {
        return outboundboxTypeList;
    }

    public void setOutboundboxTypeList(List<Long> outboundboxTypeList) {
        this.outboundboxTypeList = outboundboxTypeList;
    }

    public List<Long> getFacilityGroupList() {
        return facilityGroupList;
    }

    public void setFacilityGroupList(List<Long> facilityGroupList) {
        this.facilityGroupList = facilityGroupList;
    }

    public List<Long> getCheckTableIdList() {
        return checkTableIdList;
    }

    public void setCheckTableIdList(List<Long> checkTableIdList) {
        this.checkTableIdList = checkTableIdList;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getFacilityLowerLimit() {
        return facilityLowerLimit;
    }

    public void setFacilityLowerLimit(Integer facilityLowerLimit) {
        this.facilityLowerLimit = facilityLowerLimit;
    }

    public Integer getFacilityUpperLimit() {
        return facilityUpperLimit;
    }

    public void setFacilityUpperLimit(Integer facilityUpperLimit) {
        this.facilityUpperLimit = facilityUpperLimit;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Integer getSeedingCount() {
        return seedingCount;
    }

    public void setSeedingCount(Integer seedingCount) {
        this.seedingCount = seedingCount;
    }
}
