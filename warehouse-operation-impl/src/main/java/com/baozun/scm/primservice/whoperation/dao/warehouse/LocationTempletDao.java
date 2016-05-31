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

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.LocationTempletCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationTemplet;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;



public interface LocationTempletDao extends BaseDao<LocationTemplet, Long> {


    @QueryPage("findListCountByQueryMapExt")
    Pagination<LocationTempletCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(LocationTemplet o);

    @CommonQuery
    int saveOrUpdateByVersion(LocationTemplet o);

    List<LocationTemplet> findWrongListbyCode(@Param("list") List<String> list);

    int updateLifeCycle(@Param("ids") List<Long> ids, @Param("lifecycle") Integer lifecycle, @Param("userid") Long userid, @Param("ouId") Long ouId);

    LocationTemplet findLocationTempletByCodeAndOuId(@Param("templetCode") String templetCode,@Param("ouId") Long ouId);

    /**
     * 单个删除
     * 
     * @param id
     * @return
     */
    int deleteExt(@Param("id") Long id,@Param("ouId") Long ouId);

    /**
     * 通过多个id删除
     * 
     * @param ids
     * @return
     */
    int deleteByIdsExt(@Param("ids") List<Long> ids,@Param("ouId") Long ouId);

    /**
     * 通过id进行查询单个对象
     * 
     * @param id
     * @return
     */
    LocationTemplet findByIdExt(@Param("id") Long id,@Param("ouId") Long ouId);

    /**
     * 更新
     * @param obj
     * @return
     */
    int updateExt(LocationTemplet obj);

    /**
     * 通过库位模版名称和编号检验库位模版是否存在
     * @param locationTemplet
     * @return
     */
    long uniqueCodeOrName(LocationTemplet locationTemplet);
}
