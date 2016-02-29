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
package com.baozun.scm.primservice.whoperation.model.poasn;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

/**
 * 
 * @PO单对应分配ASN单仓库关联表
 * 
 */
public class PoAsnOu extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -4343602338902944646L;

    /** 主键ID */
    private java.lang.Long id;
    /** po单ID */
    private java.lang.Long poId;
    /** 分配ASN中的仓库ID */
    private java.lang.Long ouId;

    public java.lang.Long getId() {
        return id;
    }

    public void setId(java.lang.Long id) {
        this.id = id;
    }

    public java.lang.Long getPoId() {
        return poId;
    }

    public void setPoId(java.lang.Long poId) {
        this.poId = poId;
    }

    public java.lang.Long getOuId() {
        return ouId;
    }

    public void setOuId(java.lang.Long ouId) {
        this.ouId = ouId;
    }



}
