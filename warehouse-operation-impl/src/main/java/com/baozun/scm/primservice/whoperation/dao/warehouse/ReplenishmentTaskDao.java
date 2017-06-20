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

import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;

public interface ReplenishmentTaskDao extends BaseDao<ReplenishmentTask,Long>{

	@QueryPage("findListCountByQueryMap")
	Pagination<ReplenishmentTask> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
	@CommonQuery
	int saveOrUpdate(ReplenishmentTask o);
	
	@CommonQuery
	int saveOrUpdateByVersion(ReplenishmentTask o);
	
	int deleteByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);
	
	/**
     * 根据补货编码查询信息
     * 
     * @param replenishmentCode
     * @param ouId
     * @return
     */
	ReplenishmentTask findReplenishmentTaskByCode(@Param("replenishmentCode") String replenishmentCode, @Param("ouId") Long ouId);
	
	/**
     * 根据波次Id查询信息
     * 
     * @param replenishmentCode
     * @param ouId
     * @return
     */
    ReplenishmentTask findReplenishmentTaskByWaveId(@Param("waveId") Long waveId, @Param("ouId") Long ouId);
	
}
