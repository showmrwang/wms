package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class UomCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -9210183829381701499L;

    private Long id;
    /** 单位编码 */
    private String uomCode;
    /** 单位名称 */
    private String uomName;
    /** 换算率 */
    private Double conversionRate;
    /** 所属单位类型编码 */
    private String groupCode;
    /** 排序号 */
    private Integer sortNo;
    /** 创建时间 */
    private Date createTime;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;
    /** 1.可用;2.已删除;0.禁用 */
    private Integer lifecycle;


    /** 是否默认展示单位 1.是;0.否;2.禁用 */
    private Boolean defaultDisplayUomFlag;

    public Boolean getDefaultDisplayUomFlag() {
        return defaultDisplayUomFlag;
    }

    public void setDefaultDisplayUomFlag(Boolean defaultDisplayUomFlag) {
        this.defaultDisplayUomFlag = defaultDisplayUomFlag;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUomCode() {
        return uomCode;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

    public Double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
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


}
