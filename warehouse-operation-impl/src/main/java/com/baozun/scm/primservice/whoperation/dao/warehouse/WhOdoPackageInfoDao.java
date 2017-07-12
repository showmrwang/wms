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

import com.baozun.scm.primservice.whoperation.model.warehouse.WhOdoPackageInfo;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;



public interface WhOdoPackageInfoDao extends BaseDao<WhOdoPackageInfo, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOdoPackageInfo> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhOdoPackageInfo o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOdoPackageInfo o);

    /**
     * [通用方法] 通过出库单id,出库箱id, 组织id查找出库单打包信息
     * 
     * @param odoId
     * @param outboundBoxId
     * @param ouId
     * @return
     */
    WhOdoPackageInfo findByOdoIdAndOutboundBoxCode(@Param("odoId") Long odoId, @Param("outboundBoxCode") String outboundBoxCode, @Param("ouId") Long ouId);

    /**
     * [通用方法] 通过出出库箱编码, 组织id查找出库单打包信息
     * 
     * @param outboundBoxCode
     * @param ouId
     * @return
     */
    WhOdoPackageInfo findByOutboundBoxCode(@Param("outboundboxCode") String outboundboxCode, @Param("ouId") Long ouId);
    
    /**
     * 通过出库单ID查找对应数据
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    List<WhOdoPackageInfo> findByOdoIdAndOuId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

}
