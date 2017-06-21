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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkLineCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;



public interface WhWorkLineDao extends BaseDao<WhWorkLine,Long>{


	@QueryPage("findListCountByQueryMap")
	Pagination<WhWorkLine> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
	@CommonQuery
	int saveOrUpdate(WhWorkLine o);
	
	@CommonQuery
	int saveOrUpdateByVersion(WhWorkLine o);
	
	int deleteByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);
	
	List<WhWorkLineCommand> findWorkLineByWorkId(@Param("workId") Long workId, @Param("ouId") Long ouId);
	
	public List<WhWorkLineCommand> findWorkLineByLocationId(@Param("locationId") Long locationId,@Param("ouId") Long ouId,@Param("replenishmentCode") String replenishmentCode);
	
	/**
     * 根据补货单据号工作明细计划量之和
     *
     * @author qiming.liu
     * @param replenishmentCode
     * @param ouId
     * @return
     */
    Double getTotalQtyByReplenishmentCode(@Param("replenishmentCode") String replenishmentCode, @Param("ouId") Long ouId, @Param("workCategory") String workCategory);

    List<WhWorkLine> findWorkLineByWorkIdOuId(@Param("workId") Long id, @Param("ouId") Long ouId);
    
    /**
     * 获取需要补充的工作明细
     *
     * @author qiming.liu
     * @param toLocationId
     * @param ouId
     * @return
     */
    List<WhWorkLineCommand> findWorkLineByToLocationId(@Param("toLocationId") Long toLocationId, @Param("ouId") Long ouId);
    
   /**
    * 获取当前工作明细中药拣货的数量
    * @param id
    * @param ouId
    * @return
    */
    public int findWorkLineCount(@Param("workId") Long id, @Param("ouId") Long ouId);
	
}
