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
package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.model.BaseModel;

public class WhCheckingResultCommand extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 7075809126455313703L;
	
    /** 功能ID */
    private Long functionId;
    /** 对应组织ID */
    private Long ouId;
    /** 复核头集合 */
    private List<WhCheckingCommand> whCheckingCommandLst;
    
    
    public Long getFunctionId() {
        return functionId;
    }
    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }
    public Long getOuId() {
        return ouId;
    }
    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    public List<WhCheckingCommand> getWhCheckingCommandLst() {
        return whCheckingCommandLst;
    }
    public void setWhCheckingCommandLst(List<WhCheckingCommand> whCheckingCommandLst) {
        this.whCheckingCommandLst = whCheckingCommandLst;
    }

}

