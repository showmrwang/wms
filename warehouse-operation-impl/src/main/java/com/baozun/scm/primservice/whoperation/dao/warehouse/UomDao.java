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

import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Uom;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;


/**
 * 
 * @author yimin.lu 度量单位Dao层
 */
public interface UomDao extends BaseDao<Uom, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<Uom> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<UomCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(Uom o);

    @CommonQuery
    int saveOrUpdateByVersion(Uom o);

    List<UomCommand> findUomByGroupCode(@Param("groupCode") String groupCode,@Param("lifecycle") Integer lifecycle);

    int bacthUpdate(@Param("ids") List<Long> list, @Param("lifecycle") Integer lifecycle, @Param("operatorId") Long operatorId);

    /**
     * 根据参数查询度量单位
     * @param
     * @param groupCode
     * @return
     */
    public Uom findUomByParam(@Param("uomCode") String uomCode,@Param("groupCode") String groupCode);
}
