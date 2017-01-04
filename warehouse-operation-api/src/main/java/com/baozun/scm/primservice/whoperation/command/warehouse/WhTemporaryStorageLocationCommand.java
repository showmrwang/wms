package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WhTemporaryStorageLocationCommand extends BaseCommand {

	private static final long serialVersionUID = -7892358348103069584L;
	
	//columns START
	/** 编码 */
	private String temporaryStorageCode;
	/** 名称 */
	private String temporaryStorageName;
	/** 暂存区库位类型 */
	private String temporaryStorageType;
	/** 校验码 */
	private String checkCode;
	/** 作业/容器容量 */
	private Integer capacity;
	/** 出库暂存库位顺序 */
	private Integer priority;
	/** 复核台ID */
	private Long checkOperationsAreaId;
	/** 暂存区域ID */
	private Long workingStorageSectionId;
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
	//columns END
	
	// 自定义
	/** id */
	private Long id;
	/** 类型 */
	private List<String> temporaryStorageTypeList;
	/** 暂存区域 */
	private List<Long> workingStorageSectionList;
	/** 暂存区域名称 */
	private String workingStorageSection;
	/** 复核台 */
	private List<Long> checkOperationsAreaList;
	/** 复核台名称 */
	private String checkOperationsArea;
	/** 暂存区库位类型名称 */
	private String temporaryStorageTypeName;
	
	public String getTemporaryStorageCode() {
		return temporaryStorageCode;
	}
	public void setTemporaryStorageCode(String temporaryStorageCode) {
		this.temporaryStorageCode = temporaryStorageCode;
	}
	public String getTemporaryStorageName() {
		return temporaryStorageName;
	}
	public void setTemporaryStorageName(String temporaryStorageName) {
		this.temporaryStorageName = temporaryStorageName;
	}
	public String getTemporaryStorageType() {
		return temporaryStorageType;
	}
	public void setTemporaryStorageType(String temporaryStorageType) {
		this.temporaryStorageType = temporaryStorageType;
	}
	public String getCheckCode() {
		return checkCode;
	}
	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	public Long getCheckOperationsAreaId() {
		return checkOperationsAreaId;
	}
	public void setCheckOperationsAreaId(Long checkOperationsAreaId) {
		this.checkOperationsAreaId = checkOperationsAreaId;
	}
	public Long getWorkingStorageSectionId() {
		return workingStorageSectionId;
	}
	public void setWorkingStorageSectionId(Long workingStorageSectionId) {
		this.workingStorageSectionId = workingStorageSectionId;
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
	public List<String> getTemporaryStorageTypeList() {
		return temporaryStorageTypeList;
	}
	public void setTemporaryStorageTypeList(List<String> temporaryStorageTypeList) {
		this.temporaryStorageTypeList = temporaryStorageTypeList;
	}
	public String getCheckOperationsArea() {
		return checkOperationsArea;
	}
	public void setCheckOperationsArea(String checkOperationsArea) {
		this.checkOperationsArea = checkOperationsArea;
	}
	public String getWorkingStorageSection() {
		return workingStorageSection;
	}
	public void setWorkingStorageSection(String workingStorageSection) {
		this.workingStorageSection = workingStorageSection;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<Long> getWorkingStorageSectionList() {
		return workingStorageSectionList;
	}
	public void setWorkingStorageSectionList(List<Long> workingStorageSectionList) {
		this.workingStorageSectionList = workingStorageSectionList;
	}
	public List<Long> getCheckOperationsAreaList() {
		return checkOperationsAreaList;
	}
	public void setCheckOperationsAreaList(List<Long> checkOperationsAreaList) {
		this.checkOperationsAreaList = checkOperationsAreaList;
	}
	public String getTemporaryStorageTypeName() {
		return temporaryStorageTypeName;
	}
	public void setTemporaryStorageTypeName(String temporaryStorageTypeName) {
		this.temporaryStorageTypeName = temporaryStorageTypeName;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}
