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
package com.baozun.scm.primservice.whoperation.dao.warehouse;

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;



public interface WhOutboundboxDao extends BaseDao<WhOutboundbox, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOutboundbox> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhOutboundbox o);

    /**
     * 通过odoid查询对应数据
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    List<WhOutboundbox> findWhOutboundboxByOdoId(@Param("odoid") Long odoid, @Param("ouid") Long ouid);


    /**
     * [业务方法] 称重-通过出库箱编码或者运单号编码查找
     * @param outboundBoxCode
     * @param waybillCode
     * @param ouId
     * @return
     */
    String checkOutboundBoxStatus(@Param("outboundBoxCode") String outboundBoxCode, @Param("waybillCode") String waybillCode, @Param("ouId") Long ouId);

}
