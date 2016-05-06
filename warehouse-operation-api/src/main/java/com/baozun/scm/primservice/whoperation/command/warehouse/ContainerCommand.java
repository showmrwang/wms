package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class ContainerCommand extends BaseCommand {


    private static final long serialVersionUID = 4203510602909046411L;

    private Long id;
    /**
     * 容器名称
     */
    private String name;
    /**
     * 容器编码
     */
    private String code;
    /**
     * 一级容器类型
     */
    private Long oneLevelType;
    /**
     * 二级容器类型
     */
    private Long twoLevelType;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后修改时间
     */
    private Date lastModifyTime;
    /**
     * 操作人Id
     */
    private Long operatorId;
    /**
     * 1.可用;2.已删除;0.禁用
     */
    private Integer lifecycle;

    private Long ouId;


    /**
     * 容器一级类型
     */
    private String oneLevelTypeName;
    private String oneLevelTypeValue;

    /**
     * 容器二级类型
     */
    private String twoLevelTypeName;
    private String twoLevelTypeValue;


    private Integer oneLevelTypeLifecycle;

    private Integer twoLevelTypeLifecycle;



    public String getOneLevelTypeValue() {
        return oneLevelTypeValue;
    }

    public void setOneLevelTypeValue(String oneLevelTypeValue) {
        this.oneLevelTypeValue = oneLevelTypeValue;
    }

    public String getTwoLevelTypeValue() {
        return twoLevelTypeValue;
    }

    public void setTwoLevelTypeValue(String twoLevelTypeValue) {
        this.twoLevelTypeValue = twoLevelTypeValue;
    }

    public Integer getOneLevelTypeLifecycle() {
        return oneLevelTypeLifecycle;
    }

    public void setOneLevelTypeLifecycle(Integer oneLevelTypeLifecycle) {
        this.oneLevelTypeLifecycle = oneLevelTypeLifecycle;
    }

    public Integer getTwoLevelTypeLifecycle() {
        return twoLevelTypeLifecycle;
    }

    public void setTwoLevelTypeLifecycle(Integer twoLevelTypeLifecycle) {
        this.twoLevelTypeLifecycle = twoLevelTypeLifecycle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getOneLevelType() {
        return oneLevelType;
    }

    public void setOneLevelType(Long oneLevelType) {
        this.oneLevelType = oneLevelType;
    }

    public Long getTwoLevelType() {
        return twoLevelType;
    }

    public void setTwoLevelType(Long twoLevelType) {
        this.twoLevelType = twoLevelType;
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

    public String getOneLevelTypeName() {
        return oneLevelTypeName;
    }

    public void setOneLevelTypeName(String oneLevelTypeName) {
        this.oneLevelTypeName = oneLevelTypeName;
    }

    public String getTwoLevelTypeName() {
        return twoLevelTypeName;
    }

    public void setTwoLevelTypeName(String twoLevelTypeName) {
        this.twoLevelTypeName = twoLevelTypeName;
    }

}
