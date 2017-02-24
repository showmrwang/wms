package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;

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
    private String oneLevelType;
    /**
     * 二级容器类型
     */
    private Long twoLevelType;

    /** 是否满箱 0:否 1:是 */
    private Boolean isFull;
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
     * 1.可用;2.已删除;0.禁用;3:占用;5:可上架
     */
    private Integer lifecycle;

    private Long ouId;
    /**
     * 状态
     * 
     * @return
     */
    private Integer status;

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

    /**
     * 容器是否装过sku
     */
    private Integer isUsed;

    /** 容器装箱数 */
    private Long qty;

    /** 容器内sku id*/
    private Long skuId;

    //==================出库箱推荐用
    private List<WhOdoOutBoundBoxCommand> odoOutboundBoxCommandList;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

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


    public String getOneLevelType() {
        return oneLevelType;
    }

    public void setOneLevelType(String oneLevelType) {
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

    public Boolean getIsFull() {
        return isFull;
    }

    public void setIsFull(Boolean isFull) {
        this.isFull = isFull;
    }

    public Integer getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Integer isUsed) {
        this.isUsed = isUsed;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public List<WhOdoOutBoundBoxCommand> getOdoOutboundBoxCommandList() {
        return odoOutboundBoxCommandList;
    }

    public void setOdoOutboundBoxCommandList(List<WhOdoOutBoundBoxCommand> odoOutboundBoxCommandList) {
        this.odoOutboundBoxCommandList = odoOutboundBoxCommandList;
    }
}
