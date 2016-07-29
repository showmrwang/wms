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

import java.util.Date;
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

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;

public interface BiPoLineDao extends BaseDao<BiPoLine,Long>{

    /**
     * [业务方法]一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
	@QueryPage("findListCountByQueryMap")
    @Deprecated
	Pagination<BiPoLine> findListByQueryMapWithPage(Page page,Sort[] sorts,Map<String, Object> params);
	
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
	Pagination<BiPoLine> query(Page page,Sort[] sorts, QueryCondition cond);
	
    /**
     * QueryCondition查询
     * 
     * @param cond
     * @return
     */
    @Deprecated
	List<BiPoLine> query(QueryCondition cond);
	
    /**
     * QueryCondition查询
     * 
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
	int saveOrUpdate(BiPoLine o);

    /**
     * [业务方法]添加明细的时候，查询数据库中是否有相同商品属性的明细
     * 
     * @param status
     * @param poid
     * @param ouid
     * @param skuid
     * @param isIqck
     * @param mfgDate
     * @param expDate
     * @param validDate
     * @param batchNo
     * @param coo
     * @param invStatus
     * @param uuid
     * @return
     */
    BiPoLine findPoLineByAddPoLineParam(@Param("status") List<Integer> status, @Param("poid") Long poid, @Param("ouid") Long ouid, @Param("skuid") Long skuid, @Param("isIqc") Integer isIqck, @Param("mfgDate") Date mfgDate, @Param("expDate") Date expDate,
            @Param("validDate") Integer validDate, @Param("batchNo") String batchNo, @Param("coo") String coo, @Param("invStatus") Long invStatus, @Param("uuid") String uuid);

    /**
     * [通用方法]乐观锁更新数据
     * 
     * @param wpl
     * @return
     */
    @CommonQuery
    int saveOrUpdateByVersion(BiPoLine wpl);

    /**
     * [业务方法]BIPOLINE一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<BiPoLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]创建子PO的时候一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExtForCreateSubPo")
    Pagination<BiPoLineCommand> findListByQueryMapWithPageExtForCreateSubPo(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]根据POID,UUID删除某个PO单下所有的临时数据
     * 
     * @param poId @required
     * @param uuid @required
     * @return
     */
    long deleteBiPoLineByPoIdAndNotUuid(@Param("poId") Long poId, @Param("uuid") String uuid);

    /**
     * [通用方法]根据POId [,uuid]查找BIPOLine ;可替换findBiPoLineByPoId
     * 
     * @param poId @required
     * @param uuid
     * @return
     */
    List<BiPoLine> findBiPoLineByPoIdAndUuid(@Param("poId") Long poId, @Param("uuid") String uuid);


    /**
     * 根据id关联查询
     * 关联customer,store,sysdictionary[potype],t_wh_logistics_provider,t_wh_supplier,sku,sku_mgmt
     * ,sku_extattr
     * 
     * @param code
     * @return
     */
    BiPoLineCommand findCommandbyId(@Param("id") Long id);
	
}
