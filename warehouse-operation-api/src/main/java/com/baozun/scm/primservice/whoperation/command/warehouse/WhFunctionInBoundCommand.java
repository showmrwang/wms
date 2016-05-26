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

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

/**
 * 
 * @author larkark
 * 
 */
public class WhFunctionInBoundCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -8922271533758232928L;

    /** 主键ID */
    private Long id;
    /** 对应功能ID */
    private Long functionId;
    /** 入库分拣规则ID */
    private Long inboundRuleId;
    /** 扫描模式 逐件扫描 数量扫描 默认数量扫描 */
    private Integer scanPattern;
    /** 是否需要扫描目标容器号 */
    private Boolean isScanContainer;
    /** 对应组织ID */
    private Long ouId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getInboundRuleId() {
        return inboundRuleId;
    }

    public void setInboundRuleId(Long inboundRuleId) {
        this.inboundRuleId = inboundRuleId;
    }

    public Integer getScanPattern() {
        return scanPattern;
    }

    public void setScanPattern(Integer scanPattern) {
        this.scanPattern = scanPattern;
    }

    public Boolean getIsScanContainer() {
        return isScanContainer;
    }

    public void setIsScanContainer(Boolean isScanContainer) {
        this.isScanContainer = isScanContainer;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

}
