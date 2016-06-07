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
import com.baozun.scm.primservice.whoperation.constant.Constants;

public class CheckInQueueCommand extends BaseCommand implements Comparable<CheckInQueueCommand> {

    private static final long serialVersionUID = -4252120373203012535L;

    /** 主键ID */
    private Long id;
    /** 预约ID */
    private Long reserveId;
    /** 仓库组织ID */
    private Long ouId;
    /** 序号 */
    private Integer sequence;
    /** 创建时间 */
    private Date createTime;
    /** 创建人 */
    private Long createdId;
    /** 最后修改时间 */
    private Date lastModifyTime;
    /** 操作人ID */
    private Long modifiedId;
    /** 计划到货时间 */
    private Date eta;
    /** 实际到货时间 */
    private Date deliveryTime;
    /** 优先级 */
    private Long level;

    public Long getReserveId() {
        return reserveId;
    }

    public void setReserveId(Long reserveId) {
        this.reserveId = reserveId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
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

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(CheckInQueueCommand toCompareCommand) {
        if (this.getLevel().equals(toCompareCommand.getLevel())) {
            if((this.getDeliveryTime().compareTo(this.getEta()) < 1) && (toCompareCommand.getDeliveryTime().compareTo(toCompareCommand.getEta()) < 1)){
                //都不迟到，比较预约时间
                if(this.getEta().compareTo(toCompareCommand.getEta()) == 0){
                    return this.getDeliveryTime().compareTo(toCompareCommand.getDeliveryTime());
                }else {
                    return this.getEta().compareTo(toCompareCommand.getEta());
                }
            }else if((this.getDeliveryTime().compareTo(this.getEta()) > 0) && (toCompareCommand.getDeliveryTime().compareTo(toCompareCommand.getEta()) > 0)){
                //都迟到，比较实际到货时间
                return this.getDeliveryTime().compareTo(toCompareCommand.getDeliveryTime());
            }else {
                if(this.getDeliveryTime().compareTo(this.getEta()) > 0){
                    return 1;
                }else {
                    return -1;
                }
            }
        } else {
            if (Constants.ASN_RESERVE_URGENT.equals(this.getLevel())) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
