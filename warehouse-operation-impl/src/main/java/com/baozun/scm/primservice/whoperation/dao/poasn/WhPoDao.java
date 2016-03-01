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
package com.baozun.scm.primservice.whoperation.dao.poasn;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;



public interface WhPoDao extends BaseDao<WhPo, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<WhPo> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhPoCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhPo o);

    @CommonQuery
    int saveOrUpdateByVersion(WhPo o);

    WhPo findWhPoById(@Param("id") Long id, @Param("ouid") Long ouid);

    WhPoCommand findWhPoCommandById(@Param("id") Long id, @Param("ouid") Long ouid);

    int editPoStatus(@Param("ids") List<Long> ids, @Param("status") Integer status, @Param("userid") Long userid, @Param("ouid") Long ouid, @Param("lastModifyTime") Date lastModifyTime);

    long findPoByCodeAndStore(@Param("extCode") String extCode, @Param("storeId") Long storeId, @Param("ouId") Long ouId);

    List<WhPoCommand> findWhPoListByPoCode(@Param("statusList") List<Integer> statusList, @Param("poCode") String poCode, @Param("ouid") Long ouid);

    int deleteByIdOuId(@Param("id") Long id, @Param("ouid") Long ouid);
}
