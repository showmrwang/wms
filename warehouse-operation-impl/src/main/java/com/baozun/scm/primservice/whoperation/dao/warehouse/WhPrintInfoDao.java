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

import com.baozun.scm.primservice.whoperation.model.warehouse.WhPrintInfo;



public interface WhPrintInfoDao extends BaseDao<WhPrintInfo, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhPrintInfo> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @CommonQuery
    int saveOrUpdate(WhPrintInfo o);

    @CommonQuery
    int saveOrUpdateByVersion(WhPrintInfo o);

    /**
     * 打印信息表
     * 
     * @param outboundboxCode
     * @param checkingPrint
     * @return
     */
    List<WhPrintInfo> findByOutboundboxCodeAndPrintType(@Param("outboundboxCode") String outboundboxCode, @Param("checkingPrint") String checkingPrint, @Param("ouId") Long ouId);

    /**
     * 根据箱号找打印信息
     * 
     * @param outboundboxCode
     */
    List<WhPrintInfo> findFromcheckingCollectionByOutboundboxCode(@Param("outboundboxCode") String outboundboxCode, @Param("ouId") Long ouId);

    /**
     * [通用方法] 查找打印次数
     * @param whPrintInfo
     * @return
     */
    Long findListCountByParamExt(WhPrintInfo whPrintInfo);
    
    /**
     * 根据出库单号查询打印信息
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    List<WhPrintInfo> findPrintInfoByOdoId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

}
