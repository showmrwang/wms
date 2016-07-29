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
package com.baozun.scm.primservice.whoperation.dao.poasn;

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;

public interface BiPoDao extends BaseDao<BiPo,Long>{

    /**
     * [业务方法]BIPO一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
	@QueryPage("findListCountByQueryMap")
    @Deprecated
	Pagination<BiPo> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
    /**
     * QueryCondition查询
     * 
     * @param page
     * @param sorts
     * @param cond
     * @return
     */
	@QueryPage("queryCount")
    @Deprecated
	Pagination<BiPo> query(Page page,Sort[] sorts, QueryCondition cond);
	
    /**
     * QueryCondition查询
     * 
     * @param page
     * @param sorts
     * @param cond
     * @return
     */
    @Deprecated
	List<BiPo> query(QueryCondition cond);
	
    /**
     * 
     * QueryCondition查询
     * 
     * @param page
     * @param sorts
     * @param cond
     * @return
     */
    @Deprecated
	Long queryCount(QueryCondition cond);
	
    /**
     * 非乐观锁更新
     * 
     * @param o
     * @return
     */
	@CommonQuery
    @Deprecated
	int saveOrUpdate(BiPo o);
	
    /**
     * 乐观锁更新数据
     * 
     * @param o
     * @return
     */
    @CommonQuery
    int saveOrUpdateByVersion(BiPo o);

    /**
     * EXTCODE,STOREID的查找唯一的BIPO
     * 
     * @param extCode @required
     * @param storeId @required
     * @return
     */
    BiPo findBiPoByExtCodeStoreId(@Param("extCode") String extCode, @Param("storeId") Long storeId);

    /**
     * [通用方法]根据POCODE查询BIPO
     * 
     * @param code @required
     * @return
     */
    BiPo findbyPoCode(@Param("code") String code);

    /**
     * 根据pocode关联查询BIPO;
     * 关联customer,store,sysdictionary[potype],t_wh_logistics_provider,t_wh_supplier
     * 
     * @param code @required
     * @return
     */
    BiPoCommand findCommandbyPoCode(@Param("code") String code);

    /**
     * 根据id关联查询BIPO; 关联customer,store,sysdictionary[potype],t_wh_logistics_provider,t_wh_supplier
     * 
     * @param code @required
     * @return
     */
    BiPoCommand findCommandbyId(@Param("id") Long id);

    /**
     * [业务方法]BIPO一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<BiPoCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 根据店铺ID和extcode查询
     * 
     * @param storeId @required
     * @param extCode @required
     * @return
     */
    List<BiPo> findListByStoreIdExtCode(@Param("storeId") Long storeId, @Param("extCode") String extCode);

}
