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


    /**
     * [业务方法]asn一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMap")
    @Deprecated
    Pagination<WhAsn> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]asn一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhAsnCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [通用方法]非乐观锁更新数据
     */
    @CommonQuery
    @Deprecated
    int saveOrUpdate(WhAsn o);

    /**
     * [通用方法]乐观锁更新数据
     * 
     * @param o
     * @return
     */
    @CommonQuery
    int saveOrUpdateByVersion(WhAsn o);

    /**
     * [通用方法]根据ASNEXTCODE和OUID以及状态查询ASN列表
     * 
     * @param asnExtCode
     * @param status
     * @param ouid
     * @return
     */
    List<WhAsnCommand> findWhAsnListByAsnExtCode(@Param("asnExtCode") String asnExtCode, @Param("statusList") Integer[] status, @Param("ouid") Long ouid);

    /**
     * TODO
     * 
     * @param asnExtCode
     * @param storeId
     * @param ouId
     * @return
     */
    long findAsnByCodeAndStore(@Param("asnExtCode") String asnExtCode, @Param("storeId") Long storeId, @Param("ouId") Long ouId);

    /**
     * [通用方法]根据ID,OUID查找ASN
     * 
     * @param id
     * @param ouid
     * @return
     */
    WhAsnCommand findWhAsnCommandById(@Param("id") Long id, @Param("ouid") Long ouid);
    
    /**
     * [通用方法]根据ID,OUID查找ASN
     * 
     * @param id
     * @param ouid
     * @return
     */
    WhAsn findWhAsnById(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * [通用方法]根据ID,OUID删除WHASN
     * 
     * @param id
     * @param ouid
     * @return
     */
    int deleteByIdOuId(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * [业务方法]缓存锁使用
     * 
     * @param id
     * @param ouid
     * @param lastModifyTime
     * @return
     */
    int updateByVersionForLock(@Param("id") Long id, @Param("ouid") Long ouid, @Param("lastModifyTime") Date lastModifyTime);

    /**
     * [业务方法]释放缓存锁使用
     * 
     * @param id
     * @param ouid
     * @param lastModifyTime
     * @return
     */
    int updateByVersionForUnLock(@Param("id") Long id, @Param("ouid") Long ouid);
    
    /**
     * [通用方法]根据Asn编码查询Asn信息
     * 
     * @author lichuan
     * @param asnCode
     * @param ouId
     * @return
     */
    WhAsn findAsnByCodeAndOuId(@Param("asnCode") String asnCode, @Param("ouId") Long ouId);

    /**
     * [通用方法]
     * 
     * @param asnCommand
     * @return
     */
    List<WhAsnCommand> findListByParamExt(WhAsnCommand asnCommand);

    /**
     * [通用方法]
     * 
     * @param asnCommand
     * @return
     */
    long findListCountByParamExt(WhAsnCommand asnCommand);

    /**
     * [业务方法]根据客户id集合，店铺id集合查询asnID
     * 
     * @param customerList
     * @param storeList
     * @return
     */
    public List<Long> getWhAsnCommandByCustomerId(@Param("customerList") List<Long> customerList,@Param("storeList") List<Long> storeList);

    /**
     * [业务方法]根据POID,OUID查找 {存在明细行UUID不为空【且不等于此UUID】}的ASN
     * 
     * @param poId @required
     * @param ouId @required
     * @param uuid
     * @return
     */
    WhAsn findTempAsnByPoIdOuIdAndLineNotUuid(@Param("poId") Long poId, @Param("ouId") Long ouId, @Param("uuid") String uuid);

    /**
     * [业务方法]根据POID,OUID查找UUID不为空【UUID等于此UUID的】ASN
     * 
     * @param poId @required
     * @param ouId @required
     * @param uuid
     * @return
     */
    WhAsn findTempAsnByPoIdOuIdUuid(@Param("poId") Long poId, @Param("ouId") Long ouId, @Param("uuid") String uuid);

    /**
     * [业务方法]根据指定ASN列表中处于某状态的ASN列表
     *
     * @author mingwei.xie
     * @param status
     * @param ouId
     * @return
     */
    List<WhAsnCommand> findAsnListByStatus(@Param("status") int status, @Param("ouId") Long ouId,@Param("customerList") List<Long> customerList,@Param("storeList") List<Long> storeList);

    /**
     * [业务方法]校验asn是否收货完成
     * 
     * @param id
     * @param ouId
     * @return
     */
    boolean checkIsRcvdFinished(@Param("asnId") Long id, @Param("ouId") Long ouId);

    /**
     * [通用方法]根据POID，OUID查询WHASN
     * 
     * @param poId
     * @param ouId
     * @return
     */
    List<WhAsn> findWhAsnByPoIdOuId(@Param("poId") Long poId, @Param("ouId") Long ouId);

    /**
     * 查询退换货数据
     * 
     * @param command
     * @return
     */
    List<WhAsnCommand> findReturns(WhAsnCommand command);

}
