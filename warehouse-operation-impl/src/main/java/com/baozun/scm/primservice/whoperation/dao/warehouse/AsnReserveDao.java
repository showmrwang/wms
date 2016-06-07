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

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.AsnReserveCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.AsnReserve;


public interface AsnReserveDao extends BaseDao<AsnReserve, Long> {

    /**
     * 通过参数查询ASN预约分页列表
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<AsnReserveCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);


    @CommonQuery
    int saveOrUpdate(AsnReserve o);

    @CommonQuery
    int saveOrUpdateByVersion(AsnReserve o);

    List<AsnReserveCommand> findListByQueryMapWithExt(@Param("eta") Date eta, @Param("groupName") String groupName, @Param("lifecycle") int lifecycle, @Param("ouId") Long ouId);

    /**
     * 批量编辑Asn预约
     * 
     * @param ids
     * @param status
     * @param userid
     * @return
     */
    int updateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status, @Param("userid") Long userid, @Param("ouId") Long ouId);

    int updateAsnReserveSort(@Param("asnReserveList") List<AsnReserve> asnReserveList, @Param("userid") Long userid, @Param("ouId") Long ouId);
    
    /**
     * 查询AsnReserve单个对象
     * @return
     */
    AsnReserve findAsnReserveByStatusExt(@Param("asnCode") String asnCode, @Param("status") Integer status, @Param("ouId") Long ouId);
    
    /**
     * 通过id进行查询单个对象
     * 
     * @param id
     * @return
     */
    AsnReserve findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);
    
    /**
     * 删除Asn预约
     * @param id
     * @param ouId
     * @return
     */
    int deleteByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 根据asnId查找预约信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @return
     */
    AsnReserve findAsnReserveByAsnId(@Param("asnId") Long asnId, @Param("ouId") Long ouId);


}
