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
     * @return
     */
    Pagination<AsnReserveCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> param);

    /**
     * 根据Id获取店铺
     * 
     * @param id
     * @return
     */
    AsnReserve getAsnReserveById(Long id);

    /**
     * 新增或者保存店铺信息
     * 
     * @return
     */
    AsnReserve saveOrUpdate(AsnReserve asnReserve);

    /**
     * 预约状况查询
     * 
     * @param eta
     * @param groupName
     * @param lifecycle
     * @param ouId
     * @return
     */
    List<AsnReserveCommand> findListByQueryMapWithExt(Date eta, String groupName, int lifecycle, Long ouId);


    /**
     * 预约状况取消
     * 
     * @param ids
     * @param status
     * @param userid
     * @return
     */
    int updateLifeCycle(List<Long> ids, Integer status, Long userid, Long ouId);


    int findListByQueryMapWithExt(AsnReserve asnReserve, Long ouId);
    
    int deleteAsnReserveById(Long id, Long ouId);
    
    /**
     * 生成Asn预约号
     * @return
     */
    String createAsnReserveCode();
    
    /**
     * 校验Asn号是否已经被取消预约，如用户又要新建预约，提示用户该Asn号不可再新建预约，修改原Asn预约状态，即可重新预约
     * @return
     */
    Boolean checkAsnCodeIsReserve(String asnCode, Integer status, Long ouId);
    
}
