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

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;



public interface WhAsnLineDao extends BaseDao<WhAsnLine, Long> {

    /**
     * [业务方法]ASNLINE一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMap")
    @Deprecated
    Pagination<WhAsnLine> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]ASNLINE一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhAsnLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [通用方法]非乐观锁更新数据
     * 
     * @param o
     * @return
     */
    @CommonQuery
    @Deprecated
    int saveOrUpdate(WhAsnLine o);

    /**
     * [通用方法]乐观锁更新数据
     * 
     * @param o
     * @return
     */
    @CommonQuery
    int saveOrUpdateByVersion(WhAsnLine o);

    /**
     * [通用方法]根据ID,OUID删除数据
     * 
     * @param id
     * @param ouid
     * @return
     */
    int deleteByIdOuId(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * [通用方法]根据ID,OUID查找ASNLINE
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhAsnLineCommand findWhAsnLineCommandByIdOuId(@Param("id") Long id, @Param("ouid") Long ouId);

    /**
     * [通用方法]根据ID,OUID查找ASNLINE
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhAsnLine findWhAsnLineById(@Param("id") Long id, @Param("ouid") Long ouId);

    /**
     * [业务方法]通过ASNID+SKUID获取ASN明细可拆商品明细
     * 
     * @param id
     * @param asnid
     * @param ouid
     * @param skuid
     * @return
     */
    List<WhAsnLineCommand> findWhAsnLineCommandDevanningList(@Param("id") Long id, @Param("asnid") Long asnid, @Param("ouid") Long ouid, @Param("skuid") Long skuid);

    /***
     * [业务方法]获取ASN拆箱编辑页面相关数据
     * 
     * @param id
     * @param asnid
     * @param ouid
     * @param skuid
     * @return
     */
    WhAsnLineCommand findWhAsnLineCommandEditDevanning(@Param("id") Long id, @Param("asnid") Long asnid, @Param("ouid") Long ouid, @Param("skuid") Long skuid);

    /**
     * [业务方法]创建ASN单时候ASNLINE明细一览
     * 
     * @param page
     * @param sorts
     * @param paraMap
     * @return
     */
    @QueryPage("findListCountByQueryMapExtForCreateAsn")
    Pagination<WhAsnLineCommand> findListByQueryMapWithPageExtForCreateAsn(Page page, Sort[] sorts, Map<String, Object> paraMap);

    /**
     * [通用方法]根据POLINEID，ouID,uuid查找ASNLINE
     * 
     * @param polineId @required
     * @param uuid @required
     * @param ouId @required
     * @return
     */
    WhAsnLine findWhAsnLineByPoLineIdAndUuidAndOuId(@Param("poLineId") Long polineId, @Param("uuid") String uuid, @Param("ouId") Long ouId);

    /**
     * [业务方法]根据ASNID,OUID查找某一UUID的ASNLINE临时数据
     * 
     * @param asnId @required
     * @param ouId @required
     * @param uuid @required
     * @return
     */
    int checkAsnSku(@Param("occupationCode") String occupationCode, @Param("skuCode") String skuCode, @Param("ouId") Long ouId);
    List<WhAsnLine> findWhAsnLineByAsnIdOuIdUuid(@Param("asnId") Long asnId, @Param("ouId") Long ouId, @Param("uuid") String uuid);

    /**
     * [通用方法]根据ASNID,OUID查找ASNLINE
     * 
     * @param asnId @required
     * @param ouId @required
     * @return
     */
    List<WhAsnLine> findWhAsnLineByAsnIdOuId(@Param("asnId") Long asnId, @Param("ouId") Long ouId);

    /**
     * [业务方法]根据ASNID,OUID查找[非某一UUID]的ASNLINE临时数据
     * 
     * @param asnId @required
     * @param ouId @required
     * @param uuid
     * @return
     */
    List<WhAsnLine> findTempWhAsnLineByAsnIdOuIdNotUuid(@Param("asnId") Long asnId, @Param("ouId") Long ouId, @Param("uuid") String uuid);

    /**
     * [通用方法]根据一些常用的参数查询WhAsnLine
     * 
     * @param line
     * @return
     */
    List<WhAsnLine> findListByParamExt(WhAsnLine line);

    /**
     * [通用方法]根据ASNID,POLINEID,OUID[,UUID]查找对应的明细行
     * 
     * @param asnId @required
     * @param poLineId @required
     * @param ouId @required
     * @param uuid
     * @return
     */
    WhAsnLine findWhAsnLineByAsnIdPolineIdOuIdAndUuid(Long asnId, Long poLineId, Long ouId, String uuid);

}
