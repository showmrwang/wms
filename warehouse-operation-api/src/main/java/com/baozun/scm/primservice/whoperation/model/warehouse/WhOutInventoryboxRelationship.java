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
public class WhOutInventoryboxRelationship extends BaseModel {


    /**
     * 
     */
    private static final long serialVersionUID = -5408513524707940033L;


    /** Id */
    private Long id;
    /** outInventoryBoxId */
    private Long outInventoryBoxId;
    /** type */
    private String type;
    /** relationId */
    private Long relationId;
    /** ouId */
    private Long ouId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WhOutInventoryboxRelationship() {}

    public WhOutInventoryboxRelationship(Long id) {
        this.id = id;
    }

    public void setOutInventoryBoxId(Long value) {
        this.outInventoryBoxId = value;
    }

    public Long getOutInventoryBoxId() {
        return this.outInventoryBoxId;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getType() {
        return this.type;
    }

    public void setRelationId(Long value) {
        this.relationId = value;
    }

    public Long getRelationId() {
        return this.relationId;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Long getOuId() {
        return this.ouId;
    }
}
