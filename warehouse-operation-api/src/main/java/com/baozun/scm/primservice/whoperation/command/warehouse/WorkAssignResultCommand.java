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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;


/**
 * 
 * @author larkark
 *
 */
public class WorkAssignResultCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 8997687879440341761L;
    /* 成功或者失败 */
    private Boolean isSuccess;
    /* 成功列表 */
    private Set<Long> successIds;
    /* 带移入库存失败 */
    private Map<Long, List<Long>> toBeFilledFailIds;
    /* 商品库存失败 */
    private Map<Long, List<Long>> invFailIds;

    public Set<Long> getSuccessIds() {
        return successIds;
    }

    public void setSuccessIds(Set<Long> successIds) {
        this.successIds = successIds;
    }

    public Map<Long, List<Long>> getToBeFilledFailIds() {
        return toBeFilledFailIds;
    }

    public void setToBeFilledFailIds(Map<Long, List<Long>> toBeFilledFailIds) {
        this.toBeFilledFailIds = toBeFilledFailIds;
    }

    public Map<Long, List<Long>> getInvFailIds() {
        return invFailIds;
    }

    public void setInvFailIds(Map<Long, List<Long>> invFailIds) {
        this.invFailIds = invFailIds;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

}
