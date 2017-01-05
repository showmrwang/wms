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
 * @author larkark
 *
 */
public class WhFacilityQueue extends BaseModel {

    private static final long serialVersionUID = -3246785576212192702L;

    /** 批次号 */
    private String batch;
    /** 暂存区库位ID */
    private Long temporaryStorageLocationId;
    /** 创建时间 用于优先推荐设施 */
    private Date createTime;
    /** 是否开始推荐 0:否 1:是 */
    private Boolean isRec;
    /** 仓库组织ID */
    private Long ouId;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Long getTemporaryStorageLocationId() {
        return temporaryStorageLocationId;
    }

    public void setTemporaryStorageLocationId(Long temporaryStorageLocationId) {
        this.temporaryStorageLocationId = temporaryStorageLocationId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getIsRec() {
        return isRec;
    }

    public void setIsRec(Boolean isRec) {
        this.isRec = isRec;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
}
