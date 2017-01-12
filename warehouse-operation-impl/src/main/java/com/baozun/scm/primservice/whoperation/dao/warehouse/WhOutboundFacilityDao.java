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

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;

public interface WhOutboundFacilityDao extends BaseDao<WhOutboundFacility, Long> {

    @QueryPage("findListCountByQueryMap")
    Pagination<WhOutboundFacility> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhOutboundFacility o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOutboundFacility o);

    /**
     * 根据复核台分组获取复核台集合
     *
     * @author mingwei.xie
     * @param facilityGroup
     * @return
     */
    List<WhOutboundFacilityCommand> findListByOutboundFacilityGroup(@Param("ouId") Long ouId, @Param("facilityGroup") Long facilityGroup);

    @QueryPage("findListCountByQueryMapExt")
    Pagination<WhOutboundFacilityCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    WhOutboundFacility findByIdAndOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    WhOutboundFacilityCommand findByIdExt(@Param("id") Long id, @Param("ouId") Long ouId);

    int checkCodeAndNameUnique(@Param("id") Long id, @Param("code") String code, @Param("name") String name, @Param("ouId") Long ouId);

    int deleteByIdAndOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    int updateFacilityGroupIsNull(@Param("groupId") Long groupId, @Param("ouId") Long ouId);

    List<WhOutboundFacility> findUseableFacilityList(@Param("type") String type, @Param("ouId") Long ouId);

    WhOutboundFacility getTopFreeOutBoundFacilityByFacilityGroupId(@Param("facilityGroupId") Long facilityGroupId, @Param("ouId") Long ouId);
    
    /**
     * 找占用或者正在播种且对应暂存库位有容器的播种墙
     * @param ouId
     * @return
     */
	List<WhOutboundFacilityCommand> getSeedingFacility(@Param("ouId") Long ouId);

	WhOutboundFacility findByCodeAndOuId(@Param("code") String seedingwallCode, @Param("ouId") Long ouId);

    /**
     * [通用方法] 根据设施编码查找设施
     * @param facilityCode
     * @param ouId
     * @return
     */
    WhOutboundFacility findByCodeAndOuId(@Param("facilityCode") String facilityCode, @Param("ouId") Long ouId);

}
