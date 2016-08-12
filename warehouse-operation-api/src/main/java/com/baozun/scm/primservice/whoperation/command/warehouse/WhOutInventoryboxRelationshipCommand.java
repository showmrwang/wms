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
package com.baozun.scm.primservice.whoperation.command.warehouse;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author larkark
 *
 */
public class WhOutInventoryboxRelationshipCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** 关系id */
    private Long id;
    /** 出库箱id */
    private Long outInventoryBoxId;
    /** 类型 */
    private String type;
    /** 关系id */
    private Long relationId;
    /** 组织id */
    private Long ouId;


    /** 类型名称 */
    private String typeName;
    /** 店铺或客户名称 */
    private String relationName;
    /** 店铺或客户编码 */
    private String relationCode;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationCode() {
        return relationCode;
    }

    public void setRelationCode(String relationCode) {
        this.relationCode = relationCode;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
