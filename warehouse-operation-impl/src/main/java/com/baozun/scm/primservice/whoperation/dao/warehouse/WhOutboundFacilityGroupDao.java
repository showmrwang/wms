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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityGroupCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacilityGroup;

public interface WhOutboundFacilityGroupDao extends BaseDao<WhOutboundFacilityGroup, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<WhOutboundFacilityGroup> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhOutboundFacilityGroup o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOutboundFacilityGroup o);

    /**
     * 查询可用的复核台分组
     *
     * @author mingwei.xie
     * @param type 
     * @param ouId
     * @return
     */
    List<WhOutboundFacilityGroupCommand> getOutboundFacilityGroupCommandList(@Param("type") String type, @Param("ouId") Long ouId);
    
    @QueryPage("findListCountByQueryMapExt")
	Pagination<WhOutboundFacilityGroupCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);
    
    WhOutboundFacilityGroup findByIdAndOuId(@Param("id") Long id, @Param("ouId") Long ouId);
    
    WhOutboundFacilityGroupCommand findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);
    
    int deleteByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

	int checkCodeAndNameUnique(@Param("id") Long id, @Param("code") String code, @Param("name") String name, @Param("sectionId") Long sectionId, @Param("ouId") Long ouId);
	
	/**
	 * 根据路径区域类型找出下面关联的编码和名称
	 * @param typeValue
	 * @param ouId
	 * @return
	 */
	List<Map<String, Object>> findAreaDataByPathType(@Param("typeValue") String typeValue, @Param("ouId") Long ouId);
}
