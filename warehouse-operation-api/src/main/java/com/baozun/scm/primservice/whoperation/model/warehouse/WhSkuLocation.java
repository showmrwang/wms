/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
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
public class WhSkuLocation extends BaseModel {
	
	
    /**
     * 
     */
    private static final long serialVersionUID = -4242642624001487793L;
    // columns START
	/** 商品ID */
    private Long skuId;
	/** 库位ID */
    private Long locationId;
	/** 是否有效 */
    private Integer lifecycle;
	/** 修改者ID */
    private Long modifiedId;
	/** lastModifyTime */
    private Date lastModifyTime;
    /** ouId */
    private Long ouId;
	//columns END

    public void setSkuId(Long value) {
		this.skuId = value;
	}
	
    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getSkuId() {
		return this.skuId;
	}

    public void setLocationId(Long value) {
		this.locationId = value;
	}
	
    public Long getLocationId() {
		return this.locationId;
	}

    public void setLifecycle(Integer value) {
		this.lifecycle = value;
	}
	
    public Integer getLifecycle() {
		return this.lifecycle;
	}

    public void setModifiedId(Long value) {
		this.modifiedId = value;
	}
	
    public Long getModifiedId() {
		return this.modifiedId;
	}
	
    public void setLastModifyTime(Date value) {
		this.lastModifyTime = value;
	}
	
    public Date getLastModifyTime() {
		return this.lastModifyTime;
	}
}

