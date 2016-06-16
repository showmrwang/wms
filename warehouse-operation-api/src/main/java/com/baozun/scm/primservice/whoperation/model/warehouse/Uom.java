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
package com.baozun.scm.primservice.whoperation.model.warehouse;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark 单位信息表：用于度量单位换算
 */
public class Uom extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -5068924473688786113L;

    public static final String LENGTH_UOM = "LENGTH_UOM";// 长度
    public static final String AREA_UOM = "AREA_UOM";// 面积
    public static final String VOLUME_UOM = "VOLUME_UOM";// 体积
    public static final String WEIGHT_UOM = "WEIGHT_UOM";// 重量
    public static final String CURRENCT_UOM = "CURRENCT_UOM";// 货币
    public static final String TIME_UOM = "TIME_UOM";// 年月日
  
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
    /** 更新时间 */
    private Date updateDate;

    /** 是否默认展示单位 1.是;0.否;2.禁用 */
    private Boolean defaultDisplayUomFlag;

    public Boolean getDefaultDisplayUomFlag() {
        return defaultDisplayUomFlag;
    }

    public void setDefaultDisplayUomFlag(Boolean defaultDisplayUomFlag) {
        this.defaultDisplayUomFlag = defaultDisplayUomFlag;
    }
    
    
    

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
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
