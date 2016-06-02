package com.baozun.scm.primservice.whoperation.command.system;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class SysDictionaryCommand extends BaseCommand {

    private static final long serialVersionUID = -1355064581191490140L;
    private Long id;
    /* 显示名称 */
    private String dicLabel;
    /* 数据值 */
    private String dicValue;
    /* 描述 */
    private String description;
    /* 生命周期 */
    private Integer lifecycle;
    /* 分组编码 */
    private String groupValue;
    /* 分组名称 */
    private String groupName;
    /* lang */
    private String lang;
    /* 功能(高于分组，将不同的功能进行区分，可以为null) */
    private String functionName;
    /* 排序号 */
    private Integer orderNum;
    /* 是否能编辑 1:可编辑 0:不可编辑 */
    private Integer isEdit;
    /* 创建时间 */
    private Date createTime;
    /* 最后修改时间 */
    private Date lastModifyTime;
    /* 操作人ID */
    private Long operatorId;
    /* 对应GROUP_ID */
    private Long groupId;
    /* 更新时间 */
    private Date updateDate;

    private String groupCode;
    private String gName;
    private Integer isSys;
    private List<Long> ids;

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Integer getIsSys() {
        return isSys;
    }

    public void setIsSys(Integer isSys) {
        this.isSys = isSys;
    }

    public String getDicLabel() {
        return dicLabel;
    }

    public void setDicLabel(String dicLabel) {
        this.dicLabel = dicLabel;
    }

    public String getDicValue() {
        return dicValue;
    }

    public void setDicValue(String dicValue) {
        this.dicValue = dicValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getGroupValue() {
        return groupValue;
    }

    public void setGroupValue(String groupValue) {
        this.groupValue = groupValue;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(Integer isEdit) {
        this.isEdit = isEdit;
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
