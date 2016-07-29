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

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;



public interface WhPoLineDao extends BaseDao<WhPoLine, Long> {

    /**
     * [业务方法]明细一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMap")
    @Deprecated
    Pagination<WhPoLine> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]明细一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhPoLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 非乐观锁更新数据
     * 
     * @param o
     * @return
     */
    @CommonQuery
    @Deprecated
    int saveOrUpdate(WhPoLine o);

    /**
     * 乐观锁更新数据
     * 
     * @param o
     * @return
     */
    @CommonQuery
    int saveOrUpdateByVersion(WhPoLine o);

    /**
     * [通用方法]删除UUID为此UUID的数据
     * 
     * @param poid
     * @param ouid
     * @param uuid
     * @return
     */
    int deletePoLineByUuid(@Param("poid") Long poid, @Param("ouid") Long ouid, @Param("uuid") String uuid);

    /**
     * [通用方法]删除UUID不为空并且不等于这个UUID的数据
     * 
     * @param poid @required
     * @param ouid @required
     * @param uuid
     * @return
     */
    int deletePoLineByNotUuid(@Param("poid") Long poid, @Param("ouid") Long ouid, @Param("uuid") String uuid);

    /**
     * [通用方法]根据ID,OUID删除WHPOLINE
     * 
     * @param id @required
     * @param ouid @required
     * @return
     */
    int deletePoLineByIdOuId(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * [通用方法]删除UUID不为空的数据
     * 
     * @param id @required
     * @param ouid @required
     * @return
     */
    int deletePoLineByUuidNotNull(@Param("poid") Long id, @Param("ouid") Long ouid);

    /**
     * [通用方法]根据ID,OUID查找WHPOLINE
     * 
     * @param id @required
     * @param ouid @required
     * @return
     */
    WhPoLineCommand findWhPoLineCommandById(@Param("id") Long id, @Param("ouId") Long ouid);

    /**
     * [业务方法]查找商品属性相同的WHPOLINE
     * 
     * @param status @required
     * @param poid @required
     * @param ouid @required
     * @param skuid @required
     * @param isIqck @required
     * @param mfgDate @required
     * @param expDate @required
     * @param validDate @required
     * @param batchNo @required
     * @param coo @required
     * @param invStatus @required
     * @param uuid
     * @return
     */
    WhPoLine findPoLineByAddPoLineParam(@Param("status") List<Integer> status, @Param("poid") Long poid, @Param("ouid") Long ouid, @Param("skuid") Long skuid, @Param("isIqc") Integer isIqck, @Param("mfgDate") Date mfgDate, @Param("expDate") Date expDate,
            @Param("validDate") Integer validDate, @Param("batchNo") String batchNo, @Param("coo") String coo, @Param("invStatus") Long invStatus, @Param("uuid") String uuid);

    /**
     * [通用方法]根据POID,OUID查找WHPOLINE;UUID为空，查询所有数据；UUID不为空，查询临时数据
     * 
     * @param poid @required
     * @param ouid @required
     * @param uuid
     * @return
     */
    List<WhPoLine> findWhPoLineByPoIdOuIdUuid(@Param("poid") Long poid, @Param("ouid") Long ouid, @Param("uuid") String uuid);

    /**
     * [通用方法]根据ID,OUID查询WHPOLINE
     * 
     * @param id
     * @param ouid
     * @return
     */
    WhPoLine findWhPoLineById(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * [通用方法]根据EXTCODE，STOREID,OUID,POLINEID找到非取消状态下的对应的行
     * 
     * @author YIMIN.LU
     * @param extCode @required
     * @param storeId @required
     * @param ouId @required
     * @param id @required
     * @return
     */
    WhPoLine findByExtCodeStoreIdOuIdPoLineId(@Param("extCode") String extCode, @Param("storeId") Long storeId, @Param("ouId") Long ouId, @Param("poLineId") Long id);

    /**
     * [业务方法]根据POID,OUID,POLINEID查找某个状态组的WHPOLINE
     * 
     * @param poLineId @required
     * @param statusList @required
     * @param poId @required
     * @param ouId @required
     * @param uuid
     * @return
     */
    WhPoLine findPoLineByPolineIdAndStatusListAndPoIdAndOuId(@Param("poLineId") Long poLineId, @Param("statusList") List<Integer> statusList, @Param("poId") Long poId, @Param("ouId") Long ouId, @Param("uuid") String uuid);

    /**
     * [业务方法]根据POID,OUID,UUID删除临时数据WHPOLINE
     * 
     * @param poId
     * @param ouId
     * @param uuid
     * @return
     */
    List<WhPoLine> findPoLineByPoIdOuIdAndUuidNotNullNotEqual(@Param("poId") Long poId, @Param("ouId") Long ouId, @Param("uuid") String uuid);

    /**
     * [业务方法]创建子PO时候INFO.WHPOLINE一览查询
     * 
     * @param page
     * @param sorts
     * @param paraMap
     * @return
     */
    @QueryPage("findListCountByQueryMapExtForCreateSubPoToInfo")
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateSubPoToInfo(Page page, Sort[] sorts, Map<String, Object> paraMap);

    /**
     * [业务方法]创建ASN时SHARD.WHPOLINE一览查询
     * 
     * @param page
     * @param sorts
     * @param paraMap
     * @return
     */
    @QueryPage("findListCountByQueryMapExtForCreateAsnToShard")
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateAsnToShard(Page page, Sort[] sorts, Map<String, Object> paraMap);

    /**
     * [通用方法]根据参数列表查找WHPOLINE
     * 
     * @param command
     * @return
     */
    List<WhPoLine> findListByParamExt(WhPoLineCommand command);

    /**
     * [业务方法]处于非取消，非关闭状态下可用数量大于0的WHPOLINE集合
     * 
     * @param poId
     * @param ouId
     * @return
     */
    List<WhPoLine> findWhPoLineByPoIdOuIdWhereHasAvailableQtyToShard(@Param("poId") Long poId, @Param("ouId") Long ouId);

    /**
     * [通用方法]根据EXTCODE,STOREID,OUID,STATUSLIST查找WHPO
     * 
     * @param extCode @required
     * @param storeId @required
     * @param ouId @required
     * @return
     */
    List<WhPoLine> findPoLineByExtCodeStoreIdOuIdStatus(@Param("extCode") String extCode, @Param("storeId") Long storeId, @Param("ouId") Long ouId, @Param("statusList") List<Integer> statusList);

}
