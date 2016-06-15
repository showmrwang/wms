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


    @QueryPage("findListCountByQueryMap")
    Pagination<WhPoLine> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhPoLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhPoLine o);

    @CommonQuery
    int saveOrUpdateByVersion(WhPoLine o);

    int deletePoLineByUuid(@Param("poid") Long poid, @Param("ouid") Long ouid, @Param("uuid") String uuid);

    /**
     * 删除不是此次UUID的数据
     * 
     * @param poid
     * @param ouid
     * @param uuid
     * @return
     */
    int deletePoLineByNotUuid(@Param("poid") Long poid, @Param("ouid") Long ouid, @Param("uuid") String uuid);

    int deletePoLineByIdOuId(@Param("id") Long id, @Param("ouid") Long ouid);

    /**
     * 删除UUID不为空的数据
     * 
     * @param id
     * @param ouid
     * @return
     */
    int deletePoLineByUuidNotNull(@Param("poid") Long id, @Param("ouid") Long ouid);

    WhPoLineCommand findWhPoLineById(@Param("id") Long id, @Param("ouId") Long ouid);

    int editPoLineStatus(@Param("ids") List<Long> ids, @Param("status") Integer status, @Param("userid") Long userid, @Param("ouid") Long ouid, @Param("lastModifyTime") Date lastModifyTime);

    WhPoLine findPoLineByAddPoLineParam(@Param("status") List<Integer> status, @Param("poid") Long poid, @Param("ouid") Long ouid, @Param("skuid") Long skuid, @Param("isIqc") Integer isIqck, @Param("mfgDate") Date mfgDate, @Param("expDate") Date expDate,
            @Param("validDate") Integer validDate, @Param("batchNo") String batchNo, @Param("coo") String coo, @Param("invStatus") Long invStatus, @Param("uuid") String uuid);

    List<WhPoLine> findWhPoLineByPoIdOuId(@Param("poid") Long poid, @Param("ouid") Long ouid, @Param("uuid") String uuid);

    WhPoLine findWhPoLineByIdWhPoLine(@Param("id") Long id, @Param("ouid") Long ouid);

    int deletePoLineByPoId(@Param("id") Long id);

    int deleteByPoIdOuId(@Param("poid") Long id, @Param("ouid") Long ouid);

    /**
     * @author YIMIN.LU 根据POCODE，OUID,POLINEID找到非取消状态下的对应的行
     * @param poCode
     * @param ouId
     * @param id
     * @return
     */
    WhPoLine findByPoCodeAndOuIdAndPoLineId(@Param("poCode") String poCode, @Param("ouId") Long ouId, @Param("poLineId") Long id);

    List<WhPoLine> findInfoPoLineByPoCodeOuId(@Param("poCode") String poCode, @Param("ouId") Long ouId);

    WhPoLine findPoLineByPolineIdAndStatusListAndPoIdAndOuId(@Param("poLineId") Long poLineId, @Param("statusList") List<Integer> statusList, @Param("poId") Long poId, @Param("ouId") Long ouId, @Param("uuid") String uuid);

    List<WhPoLine> findPoLineByPoIdOuIdAndUuidNotNullNotEqual(@Param("poId") Long poId, @Param("ouId") Long ouId, @Param("uuid") String uuid);

    @QueryPage("findListCountByQueryMapExtForCreateSubPoToInfo")
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateSubPoToInfo(Page page, Sort[] sorts, Map<String, Object> paraMap);

    @QueryPage("findListCountByQueryMapExtForCreateAsnToShard")
    Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateAsnToShard(Page page, Sort[] sorts, Map<String, Object> paraMap);

    List<WhPoLine> findListByParamExt(WhPoLineCommand command);

}
