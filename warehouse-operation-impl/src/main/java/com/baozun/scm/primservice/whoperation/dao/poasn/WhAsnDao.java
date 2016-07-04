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

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;



public interface WhAsnDao extends BaseDao<WhAsn, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhAsn> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhAsnCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    @Deprecated
    int saveOrUpdate(WhAsn o);

    @CommonQuery
    int saveOrUpdateByVersion(WhAsn o);

    List<WhAsnCommand> findWhAsnListByAsnExtCode(@Param("asnExtCode") String asnExtCode, @Param("statusList") Integer[] status, @Param("ouid") Long ouid);

    int editAsnStatus(@Param("ids") List<Long> ids, @Param("status") Integer status, @Param("userid") Long userid, @Param("ouid") Long ouid, @Param("lastModifyTime") Date lastModifyTime);

    long findAsnByCodeAndStore(@Param("asnExtCode") String asnExtCode, @Param("storeId") Long storeId, @Param("ouId") Long ouId);

    WhAsnCommand findWhAsnByIdCommand(@Param("id") Long id, @Param("ouid") Long ouid);
    
    WhAsn findWhAsnById(@Param("id") Long id, @Param("ouid") Long ouid);

    int deleteByIdOuId(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * 缓存锁使用
     * 
     * @param id
     * @param ouid
     * @param lastModifyTime
     * @return
     */
    int updateByVersionForLock(@Param("id") Long id, @Param("ouid") Long ouid, @Param("lastModifyTime") Date lastModifyTime);

    /**
     * 释放缓存锁使用
     * 
     * @param id
     * @param ouid
     * @param lastModifyTime
     * @return
     */
    int updateByVersionForUnLock(@Param("id") Long id, @Param("ouid") Long ouid);
    
    /**
     * 根据Asn编码查询Asn信息
     * @author lichuan
     * @param asnCode
     * @param ouId
     * @return
     */
    WhAsn findAsnByCodeAndOuId(@Param("asnCode") String asnCode, @Param("ouId") Long ouId);

    List<WhAsnCommand> findListByParamExt(WhAsnCommand asnCommand);

    long findListCountByParamExt(WhAsnCommand asnCommand);

    /**
     * 根据客户id集合，店铺id集合查询asn信息
     * @param customerList
     * @param storeList
     * @return
     */
    public List<Long> getWhAsnCommandByCustomerId(@Param("customerList") List<Long> customerList,@Param("storeList") List<Long> storeList);

}
