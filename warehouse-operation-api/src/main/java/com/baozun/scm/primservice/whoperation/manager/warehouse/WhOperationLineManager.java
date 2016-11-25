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
package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;


public interface WhOperationLineManager extends BaseManager{
    
    /**
     * [通用方法] 创建作业明细信息
     * 
     * @param WhOperationLineCommand
     * @return
     */
    Boolean saveOrUpdate(WhOperationLineCommand whOperationLineCommand);
    
    /**
     * [通用方法] 根据作业头和ouId查询作业明细信息
     * 
     * @param operationId
     * @param ouId
     * @return
     */
    List<WhOperationLineCommand> findOperationLineByOperationId(Long operationId, Long ouId);

}
