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

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @author larkark
 *
 */
public class OutInvBoxTypeVolume extends BaseModel {



    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // columns START
    /** 出库箱类型主键 */
    private Long outId;
    /** 对应组织ID */
    private Long ouId;
    /** 货品主键 */
    private Long prvId;
    /** 容量 */
    private Long volume;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 最终修改时间 */
    private java.util.Date lastModifyTime;
    /** 创建人ID */
    private Long createdId;
    /** 修改人ID */
    private Long modifiedId;

    // columns END

    public OutInvBoxTypeVolume() {}

    public OutInvBoxTypeVolume(Long id) {
        this.id = id;
    }

    public void setOutId(Long value) {
        this.outId = value;
    }

    public Long getOutId() {
        return this.outId;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setPrvId(Long value) {
        this.prvId = value;
    }

    public Long getPrvId() {
        return this.prvId;
    }


    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public void setCreateTime(java.util.Date value) {
        this.createTime = value;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setLastModifyTime(java.util.Date value) {
        this.lastModifyTime = value;
    }

    public java.util.Date getLastModifyTime() {
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



}
