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
package com.baozun.scm.primservice.whoperation.dao.collect;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivLineIndex;

public interface WhOdoArchivLineIndexDao extends BaseDao<WhOdoArchivLineIndex, Long>{

	@QueryPage("findListCountByQueryMap")
	Pagination<WhOdoArchivLineIndex> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);
	
	@CommonQuery
	int saveOrUpdate(WhOdoArchivLineIndex o);
	
	List<WhOdoArchivLineIndex> findWhOdoArchivLineIndexByEcOrderCodeAndSource(@Param("ecOrderCode") String ecOrderCode, @Param("wmsSource") String wmsSource, @Param("num") String num);

    List<WhOdoArchivLineIndex> findWhOdoArchivLineIndexByEcOrderCodeAndSkuIdList(@Param("ecOrderCode") String ecOrderCode, @Param("skuIdList") Set<Long> skuIdList, @Param("num") String num);

    WhOdoArchivLineIndex findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    int execute(@Param("tableName") String sql, @Param("id") Long id, @Param("qty") Double qty);
	
}
