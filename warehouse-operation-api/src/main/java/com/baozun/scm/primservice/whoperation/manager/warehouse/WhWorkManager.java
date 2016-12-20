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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;


public interface WhWorkManager extends BaseManager{
    
    /**
     * [通用方法] 创建工作头信息
     * @param whWork
     * @return
     */
    Boolean saveOrUpdate(WhWorkCommand whWorkCommand);
    
    /**
     * [通用方法] 根据code和ouId查找工作头信息
     * @param code
     * @param ouId
     * @return
     */
    WhWorkCommand findWorkByWorkCode(String code, Long ouId);
    
    /**
     * [通用方法] 根据id和ouId查找工作头信息
     * @param id
     * @param ouId
     * @return
     */
    WhWork findWorkByWorkId(Long id);

    /**
     * [业务方法]波次释放锁定的工作
     * 
     * @param code
     * @param ouId
     * @param isLock
     * @return
     */
    List<WhWork> findWorkByWaveWithLock(String code, Long ouId);

    /**
     * [业务方法]寻找波次下的工作
     * 
     * @param code
     * @param ouId
     * @param isLock
     * @return
     */
    List<WhWork> findWorkByWave(String code, Long ouId);

}
