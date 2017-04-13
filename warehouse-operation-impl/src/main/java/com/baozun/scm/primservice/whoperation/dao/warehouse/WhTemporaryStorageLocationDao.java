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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhTemporaryStorageLocationCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhTemporaryStorageLocation;

public interface WhTemporaryStorageLocationDao extends BaseDao<WhTemporaryStorageLocation, Long>{

	@QueryPage("findListCountByQueryMapExt")
	Pagination<WhTemporaryStorageLocationCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);
	
	@CommonQuery
	int saveOrUpdate(WhTemporaryStorageLocation o);
	
	@CommonQuery
	int saveOrUpdateByVersion(WhTemporaryStorageLocation o);
	
	WhTemporaryStorageLocation findByIdAndOuId(@Param("id") Long id, @Param("ouId") Long ouId);
	
	WhTemporaryStorageLocationCommand findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

	int checkCodeAndNameUnique(@Param("id") Long id, @Param("code") String code, @Param("name") String name, @Param("ouId") Long ouId);

	int updateCheckOperationsAreaIsNull(@Param("id") Long id, @Param("ouId") Long ouId);

	List<WhTemporaryStorageLocation> findTemporaryStorageUsableList(@Param("type") String type, @Param("ouId") Long ouId);

	List<WhTemporaryStorageLocation> findTemporaryStorageBindList(@Param("type") String type, @Param("id") Long id, @Param("ouId") Long ouId);

	int deleteByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

	int updateWorkingStorageSectionIsNull(@Param("id") Long id, @Param("ouId") Long ouId);

    WhTemporaryStorageLocation getTopFreeStorageLocationByWorkingStorageSectionId(@Param("workingStorageSectionId") Long workingStorageSectionId, @Param("ouId") Long ouId);

	WhTemporaryStorageLocationCommand getTemporaryStorageLocationBySeedingWall(WhOutboundFacilityCommand facility);

	WhTemporaryStorageLocation findByCodeAndOuId(@Param("code") String temporaryStorageLocationCode, @Param("ouId") Long ouId);

    List<WhTemporaryStorageLocation> findTsLocationByBatch(@Param("batch") String batch, @Param("ouId") Long ouId);

}
