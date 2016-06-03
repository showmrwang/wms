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
package com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 仓库残次类型
 * 
 * @author larkark
 * 
 */
public class WarehouseDefectType extends BaseModel {


    private static final long serialVersionUID = -1909889129301642049L;

    // columns START
    /** 残次类型名称 */
    private String name;
    /** 残次类型编码 */
    private String code;
    /** 描述 */
    private String description;
    /** 仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 最后操作时间 */
    private Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 操作人ID */
    private Long modifiedId;
    /** 是否启用 1:启用 0:停用 */
    private Integer lifecycle;


    public void setName(String value) {
        this.name = value;
    }

    public String getName() {
        return this.name;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getCode() {
        return this.code;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setCreateTime(Date value) {
        this.createTime = value;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setLastModifyTime(Date value) {
        this.lastModifyTime = value;
    }

    public Date getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setCreatedId(Long value) {
        this.createdId = value;
    }

    public Long getCreatedId() {
        return this.createdId;
    }

    public void setModifiedId(Long value) {
        this.modifiedId = value;
    }

    public Long getModifiedId() {
        return this.modifiedId;
    }

    public void setLifecycle(Integer value) {
        this.lifecycle = value;
    }

    public Integer getLifecycle() {
        return this.lifecycle;
    }

}
