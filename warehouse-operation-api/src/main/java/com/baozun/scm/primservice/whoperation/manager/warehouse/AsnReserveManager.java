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

package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.warehouse.AsnReserveCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.AsnReserve;

public interface AsnReserveManager extends BaseManager {
    /**
     * 通过参数查询ASN预约分页列表
     *
     * @param page
     * @param sorts
     * @param param
     * @param logId
     * @return
     */
    Pagination<AsnReserveCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param, String logId);
    
    /**
     * 根据ID查找预约信息
     *
     * @author mingwei.xie
     * @param asnReserveId
     * @param ouId
     * @param logId
     * @return
     */
    AsnReserveCommand getAsnReserveCommandById(Long asnReserveId, Long ouId, String logId);
    
    /**
     * 取消预约，执行物理删除
     *
     * @author mingwei.xie
     * @param asnReserveIdList
     * @param userId
     * @param ouId
     * @param logId
     * @return
     */
    void removeAsnReserve(List<Long> asnReserveIdList, Long userId, Long ouId, String logId);
    
    
    /**
     * 生成Asn预约号
     *
     * @param ouId
     * @param logId
     * @return
     */
    String createAsnReserveCode(Long ouId, String logId);
    
    
    /**
     * 根据日期查询当天的预约信息
     *
     * @author mingwei.xie
     * @param reserveDate
     * @param ouId
     * @param logId
     * @return
     */
    List<AsnReserveCommand> findAsnReserveCommandByEtaDate(Date reserveDate, Long ouId, String logId);
    
    /**
     * 新建/修改预约信息
     *
     * @author mingwei.xie
     * @param asnReserveCommand
     * @param userId
     * @param ouId
     * @param logId
     */
    void saveAsnReserve(AsnReserveCommand asnReserveCommand, Long userId, Long ouId, String logId);

    /**
     * 根据asnId查找预约信息
     *
     * @author mingwei.xie
     * @param id
     * @param ouId
     * @param logId
     * @return
     */
    AsnReserve findAsnReserveByAsnId(Long id, Long ouId, String logId);
}
