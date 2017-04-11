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
package com.baozun.scm.primservice.whoperation.dao.handover;

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhHandoverStationCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhHandoverStation;



public interface WhHandoverStationDao extends BaseDao<WhHandoverStation, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhHandoverStation> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhHandoverStation o);

    @CommonQuery
    public int saveOrUpdateByVersion(WhHandoverStation o);


    public void deleteByExt(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * 查询的结果根据序号排序
     * 
     * @param id
     * @param ouId
     * @return
     */
    public List<WhHandoverStationCommand> findWhHandoverStationByIdSort(@Param("facilityGroupId") Long facilityGroupId, @Param("ouId") Long ouId);

    /***
     * 查询复核台组id为空的仓库数据
     * 
     * @param ouId
     * @return
     */
    public List<WhHandoverStationCommand> findWhHandoverStationByOuIdSort(Long ouId);

    /***
     * 根据code查询交接库位
     * 
     * @param recommandHandoverStationCode
     * @return
     */
    WhHandoverStationCommand findByCode(@Param("code") String recommandHandoverStationCode);
    
    public List<WhHandoverStationCommand> findOneByFacilityGroupId(@Param("facilityGroupId") Long facilityGroupId, @Param("ouId") Long ouId);
    
    public List<WhHandoverStationCommand> findOneByOuId(@Param("ouId") Long ouId);
}
