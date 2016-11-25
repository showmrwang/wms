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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;


public interface WhOperationManager extends BaseManager{
    
    /**
     * [通用方法] 创建作业头信息
     * @param WhOperationCommand
     * @return
     */
    Boolean saveOrUpdate(WhOperationCommand whOperationCommand);
    
    /**
     * [通用方法] 根据作业号查询作业头信息
     * @param operationCode
     * @param ouId
     * @return
     */
    WhOperationCommand findOperationByCode(String operationCode, Long ouId);
    
    /**
     * [通用方法] 根据workId获取作业信息
     * @param workId
     * @param ouId
     * @return
     */
    WhOperationCommand findOperationByWorkId(Long workId , Long ouId);

}
