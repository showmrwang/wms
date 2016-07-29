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

    /**
     * WHPO一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMap")
    @Deprecated
    Pagination<WhPo> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * WHPO一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhPoCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 非乐观锁更新数据
     * 
     * @param o
     * @return
     */
    @CommonQuery
    @Deprecated
    int saveOrUpdate(WhPo o);

    /**
     * 乐观锁更新数据
     * 
     * @param o
     * @return
     */
    @CommonQuery
    int saveOrUpdateByVersion(WhPo o);

    /**
     * [通用方法]根据ID和OUID查找WHPO
     * 
     * @param id
     * @param ouid
     * @return
     */
    WhPo findWhPoById(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * [通用方法]根据ID和OUID查找WHPO
     * 
     * @param id
     * @param ouid
     * @return
     */
    WhPoCommand findWhPoCommandById(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * [业务方法]模糊查询数据；TODO
     * 
     * @param statusList
     * @param extCode
     * @param customerList
     * @param storeList
     * @param ouid
     * @param linenum
     * @return
     */
    List<WhPoCommand> findWhPoListByExtCode(@Param("statusList") List<Integer> statusList, @Param("extCode") String extCode,@Param("customerList") List<Long> customerList,@Param("storeList") List<Long> storeList,@Param("ouid") Long ouid, @Param("linenum") Integer linenum);

    /**
     * [通用方法]删除PO
     * 
     * @param id
     * @param ouid
     * @return
     */
    int deleteByIdOuId(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * [通用方法]查看非取消状态中的PO单
     * 
     * @param poCode
     * @param ouId
     * @return
     */
    WhPo findWhPoByExtCodeStoreIdOuId(@Param("extCode") String poCode, @Param("storeId") Long storeId, @Param("ouId") Long ouId);

    /**
     * [INFO]根据extCode,ouId查找INFO库所有PO
     * 
     * @param poCode
     * @return
     */
    List<WhPo> findWhPoByExtCodeStoreIdToInfo(@Param("extCode") String extCode, @Param("storeId") Long storeId);

    /**
     * [业务方法]Po单是否收货完成
     * 
     * @param id
     * @param ouId
     * @return
     */
    boolean checkIsRcvdFinished(@Param("poId") Long id, @Param("ouId") Long ouId);

}
