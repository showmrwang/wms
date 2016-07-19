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

import java.util.Date;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 *
 * @author larkark
 *
 */
public class AsnReserveCommand extends BaseCommand {

    private static final long serialVersionUID = -766151944854267065L;
    private Long id;
    /** 预约号 */
    private String code;
    /** asn单ID */
    private Long asnId;
    /** 计划到货时间 */
    private Date eta;
    /** 实际到货时间 */
    private Date deliveryTime;
    /** 预计停靠时长 */
    private Integer estParkingTime;
    /** 优先级 */
    private String level;
    /** 仓库组织ID */
    private Long ouId;
    /** 预约状态:1:已创建 2:已签入 10:收货完成 */
    private Integer status;
    /** 创建时间 */
    private Date createTime;
    /** 创建人 */
    private Long createdId;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** asn外部编码 */
    private String asnExtCode;
    /** 预约优先级 */
    private String asnReserveLevel;
    /** 预约状态 */
    private String asnReserveStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getAsnId() {
        return asnId;
    }

    public void setAsnId(Long asnId) {
        this.asnId = asnId;
    }

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getEstParkingTime() {
        return estParkingTime;
    }

    public void setEstParkingTime(Integer estParkingTime) {
        this.estParkingTime = estParkingTime;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatedId() {
        return createdId;
    }

    public void setCreatedId(Long createdId) {
        this.createdId = createdId;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public String getAsnExtCode() {
        return asnExtCode;
    }

    public void setAsnExtCode(String asnExtCode) {
        this.asnExtCode = asnExtCode;
    }

    public String getAsnReserveLevel() {
        return asnReserveLevel;
    }

    public void setAsnReserveLevel(String asnReserveLevel) {
        this.asnReserveLevel = asnReserveLevel;
    }

    public String getAsnReserveStatus() {
        return asnReserveStatus;
    }

    public void setAsnReserveStatus(String asnReserveStatus) {
        this.asnReserveStatus = asnReserveStatus;
    }
}
