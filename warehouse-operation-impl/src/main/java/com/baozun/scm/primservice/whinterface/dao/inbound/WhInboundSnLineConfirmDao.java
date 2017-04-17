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

import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundSnLineConfirm;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface WhInboundSnLineConfirmDao extends BaseDao<WhInboundSnLineConfirm, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<WhInboundSnLineConfirm> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhInboundSnLineConfirm o);

    /***
     * 通过入库单反馈库存明细ID查询对应数据
     * 
     * @param id
     * @return
     */
    List<WhInboundSnLineConfirm> findWhInboundSnLineConfirmByInvLineId(Long id);

}
