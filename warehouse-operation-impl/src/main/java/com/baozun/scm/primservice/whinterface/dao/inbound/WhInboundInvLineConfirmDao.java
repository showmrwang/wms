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
package com.baozun.scm.primservice.whinterface.dao.inbound;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundInvLineConfirm;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface WhInboundInvLineConfirmDao extends BaseDao<WhInboundInvLineConfirm, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<WhInboundInvLineConfirm> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhInboundInvLineConfirm o);

    /**
     * 通过入库单反馈明细ID获取对应数据
     * 
     * @param id
     * @return
     */
    List<WhInboundInvLineConfirm> findWhInboundInvLineConfirmByInboundLineId(Long id);

}
