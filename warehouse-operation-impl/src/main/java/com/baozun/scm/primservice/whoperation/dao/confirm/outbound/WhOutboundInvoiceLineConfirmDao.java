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
package com.baozun.scm.primservice.whoperation.dao.confirm.outbound;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundInvoiceLineConfirm;

public interface WhOutboundInvoiceLineConfirmDao extends BaseDao<WhOutboundInvoiceLineConfirm, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOutboundInvoiceLineConfirm> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhOutboundInvoiceLineConfirm> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhOutboundInvoiceLineConfirm> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhOutboundInvoiceLineConfirm o);

    /**
     * 通过出库单发票ID获取对应数据
     * 
     * @param id
     * @param ouid
     * @return
     */
    List<WhOutboundInvoiceLineConfirm> findWhOutboundInvoiceLineConfirmByInvoiceId(@Param("id") Long id, @Param("ouid") Long ouid);

}
