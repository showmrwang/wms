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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;



public interface WhOperationLineDao extends BaseDao<WhOperationLine,Long>{


	@QueryPage("findListCountByQueryMap")
	Pagination<WhOperationLine> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
	@CommonQuery
	int saveOrUpdate(WhOperationLine o);
	
	@CommonQuery
	int saveOrUpdateByVersion(WhOperationLine o);
	
	/**
     * 根据作业头Id和ouId获取作业明细信息
     * 
     * @author qiming.liu
     * @param operationId
     * @param ouId
     * @return
     */
	List<WhOperationLineCommand> findOperationLineByOperationId(@Param("operationId") Long operationId, @Param("ouId") Long ouId);

    /**
     * 根据作业头Id和ouId,locationId获取作业明细信息
     * 
     * @author tangming
     * @param operationId
     * @param ouId
     * @return
     */
    List<WhOperationLineCommand> findOperationLineByLocationId(@Param("ouId") Long ouId,@Param("locationId") Long locationId);
    
    /**
     * 根据作业头Id和ouId获取作业明细信息
     * 
     * @author qiming.liu
     * @param operationId
     * @param ouId
     * @return
     */
    List<WhOperationLineCommand> findOperationLineByOdoId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);
    
    /***
     * 查询当前货箱内sku的数量
     * @param ouId
     * @param insideContainerId
     * @return
     */
    public int findInventoryCountsByInsideContainerId(@Param("ouId") Long ouId, @Param("fromInsideContainerId") Long fromInsideContainerId);
    
    
    
    /***
     * 查询当前托盘内sku的数量
     * @param ouId
     * @param outerContainerId
     * @return
     */
    public int findInventoryCountsByOuterContainerId(@Param("ouId") Long ouId, @Param("fromOuterContainerId") Long fromOuterContainerId,@Param("operationId") Long operationId);
    
    
    public void deleteByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);
    
	
}
