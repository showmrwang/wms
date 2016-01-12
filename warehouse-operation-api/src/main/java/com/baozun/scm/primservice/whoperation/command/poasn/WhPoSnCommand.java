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
package com.baozun.scm.primservice.whoperation.command.poasn;

import java.io.Serializable;
import java.util.Date;


public class WhPoSnCommand implements Serializable {


    /**
     * 
     */
    private static final long serialVersionUID = 548363626676486245L;

    /** 主键ID */
    private Long id;
    /** poLineId */
    private Long poLineId;
    /** 商品ID */
    private Long skuId;
    /** sn号 */
    private String sn;
    /** 对应组织ID */
    private Long ouId;
    /** createTime */
    private Date createTime;
    /** createdId */
    private Long createdId;
    /** 修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;

    public Long getId() {
        return this.id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Long getPoLineId() {
        return this.poLineId;
    }

    public void setPoLineId(Long value) {
        this.poLineId = value;
    }

    public Long getSkuId() {
        return this.skuId;
    }

    public void setSkuId(Long value) {
        this.skuId = value;
    }

    public String getSn() {
        return this.sn;
    }

    public void setSn(String value) {
        this.sn = value;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date value) {
        this.createTime = value;
    }

    public Long getCreatedId() {
        return this.createdId;
    }

    public void setCreatedId(Long value) {
        this.createdId = value;
    }

    public Date getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(Date value) {
        this.lastModifyTime = value;
    }

    public Long getModifiedId() {
        return this.modifiedId;
    }

    public void setModifiedId(Long value) {
        this.modifiedId = value;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

}
