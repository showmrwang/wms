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

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuLocationCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuLocation;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

public interface WhSkuLocationDao extends BaseDao<WhSkuLocation,Long>{

    /**
     * @deprecated
     * @param page
     * @param sorts
     * @param params
     * @return
     */
	@QueryPage("findListCountByQueryMap")
	Pagination<WhSkuLocation> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhSkuLocationCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

	@QueryPage("queryCount")
	Pagination<WhSkuLocation> query(Page page,Sort[] sorts, QueryCondition cond);
	
	List<WhSkuLocation> query(QueryCondition cond);
	
	Long queryCount(QueryCondition cond);

    /**
     * @deprecated
     * @param o
     * @return
     */
	@CommonQuery
	int saveOrUpdate(WhSkuLocation o);

    List<WhSkuLocationCommand> findSkuLocationToShard(WhSkuLocationCommand skuLocationCommand);

    int updateByVersion(WhSkuLocation o);

    /**
     * 
     * @deprecated
     * @param skuLocation
     * @return
     */
    WhSkuLocationCommand checkForSaveOrUpdate(WhSkuLocation skuLocation);

    WhSkuLocation findByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    int deleteByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);
    
    /**
     * 根据库位查询当前库位上的已有托盘数
     * @author lichuan
     * @param ouId
     * @param locId
     * @return
     */
    int findPalletCountByLocation(@Param("ouId") Long ouId, @Param("locId") Long locId);
    
    /**
     * 检查容器内商品是否全部绑定到静态库位
     * @author lichuan
     * @param ouId
     * @param locId
     * @param containerList
     * @return
     */
    int findContainerSkuCountNotInSkuLocation(@Param("ouId") Long ouId, @Param("locId") Long locId, @Param("containerList") List<String> containerList);
    
    /**
     * 查询商品是否绑定到静态库位
     * @author lichuan
     * @param ouId
     * @param locId
     * @param skuId
     * @return
     */
    int findSkuCountInSkuLocation(@Param("ouId") Long ouId, @Param("locId") Long locId, @Param("skuId") Long skuId);
    
    /**
     * 查询绑定到静态库位的所有商品数
     * @author lichuan
     * @param ouId
     * @param locId
     * @return
     */
    int findAllSkuCountInSkuLocation(@Param("ouId") Long ouId, @Param("locId") Long locId);
}
