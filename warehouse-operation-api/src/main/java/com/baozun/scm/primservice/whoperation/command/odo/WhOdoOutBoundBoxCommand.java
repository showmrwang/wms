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
package com.baozun.scm.primservice.whoperation.command.odo;

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author 出库单明细对应出库箱/容器表
 *
 */
public class WhOdoOutBoundBoxCommand extends BaseCommand {


    /**
     * 
     */
    private static final long serialVersionUID = 554959901328869211L;
    
    /** 主键ID */
    private Long id;
    /** 出库单ID */
    private Long odoId;
    /** 出库单明细ID */
    private Long odoLineId;
    /** 出库箱类型ID */
    private Long outbounxboxTypeId;
    /** 出库箱编码 */
    private String outbounxboxTypeCode;
    /** 容器ID */
    private Long containerId;
    /** 外部容器ID 小车此类容器 */
    private Long outerContainerId;
    /** 数量 */
    private Double qty;
    /** 仓库组织ID */
    private Long ouId;
    /** 创建时间 */
    private Date createTime;
    /** 修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long operatorId;

    public Long getId() {
        return this.id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Long getOdoId() {
        return this.odoId;
    }

    public void setOdoId(Long value) {
        this.odoId = value;
    }

    public Long getOdoLineId() {
        return this.odoLineId;
    }

    public void setOdoLineId(Long value) {
        this.odoLineId = value;
    }

    public Long getOutbounxboxTypeId() {
        return this.outbounxboxTypeId;
    }

    public void setOutbounxboxTypeId(Long value) {
        this.outbounxboxTypeId = value;
    }

    public String getOutbounxboxTypeCode() {
        return this.outbounxboxTypeCode;
    }

    public void setOutbounxboxTypeCode(String value) {
        this.outbounxboxTypeCode = value;
    }

    public Long getContainerId() {
        return this.containerId;
    }

    public void setContainerId(Long value) {
        this.containerId = value;
    }

    public Long getOuterContainerId() {
        return this.outerContainerId;
    }

    public void setOuterContainerId(Long value) {
        this.outerContainerId = value;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Long getOuId() {
        return this.ouId;
    }

    public void setOuId(Long value) {
        this.ouId = value;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date value) {
        this.createTime = value;
    }

    public Date getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(Date value) {
        this.lastModifyTime = value;
    }

    public Long getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(Long value) {
        this.operatorId = value;
    }

}
