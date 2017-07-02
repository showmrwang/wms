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
package com.baozun.scm.primservice.whoperation.dao.odo;

import java.util.List;
import java.util.Map;

import lark.common.annotation.CommonQuery;
import lark.common.annotation.QueryPage;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.QueryCondition;
import lark.common.dao.Sort;
import lark.orm.dao.supports.BaseDao;

import org.apache.ibatis.annotations.Param;

import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;

public interface WhOdoOutBoundBoxDao extends BaseDao<WhOdoOutBoundBox, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOdoOutBoundBox> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhOdoOutBoundBox> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhOdoOutBoundBox> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhOdoOutBoundBox o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOdoOutBoundBox o);
    
    /**
     * [业务方法] 波次中创拣货工作-获取波次中的所有小批次
     * @param waveId
     * @param ouId
     * @return
     */
    List<WhOdoOutBoundBox> findPickingWorkWhOdoOutBoundBox(@Param("waveId") Long waveId, @Param("ouId") Long ouId);
    
    /**
     * [业务方法] 波次中创拣货工作-获取波次中的所有小批次
     * @param waveId
     * @param ouId
     * @return
     */
    List<WhOdoOutBoundBox> getOdoOutBoundBoxForGroup(WhOdoOutBoundBox whOdoOutBoundBox);
    
    /**
     * [业务方法] 波次中创拣货工作-根据批次分组查询所有出库箱/容器信息
     * @param waveId
     * @param ouId
     * @return
     */
    List<WhOdoOutBoundBoxCommand> getOdoOutBoundBoxListByGroup(WhOdoOutBoundBox whOdoOutBoundBox);
    
    /**
     * [业务方法] 波次中创拣货工作-查询对应的耗材
     * @param outbounxboxTypeId
     * @param outbounxboxTypeCode
     * @return
     */
    Long findOutboundboxType(@Param("outbounxboxTypeId") Long outbounxboxTypeId, @Param("outbounxboxTypeCode") String outbounxboxTypeCode, @Param("ouId") Long ouId);
    
    /**
     * [业务方法] 波次中创拣货工作-根据id查询数据
     * @param id
     * @param ouId
     * @return
     */
    WhOdoOutBoundBoxCommand findWhOdoOutBoundBoxCommandById(@Param("id") Long id, @Param("ouId") Long ouId);
    
    /**
     * [业务方法] 波次中创拣货工作-根据id查询数据
     * @param id
     * @param ouId
     * @return
     */
    Double findQtyByWaveAndOdoLine(@Param("waveId") Long waveId, @Param("odoLineId") Long odoLineId, @Param("ouId") Long ouId);

    /**
     * 获取波次下的小批次
     * @author kai.zhu
     * @version 2017年5月2日
     */
    List<String> getBatchListByWaveId(@Param("waveId") Long waveId, @Param("ouId") Long ouId);
    
    /**
     * 根据batch获取关联的odoIdList
     */
    List<Long> getOdoIdListByBatch(@Param("batch") String batch, @Param("waveId") Long waveId, @Param("ouId") Long ouId);
    
    /**
     * [业务方法] 根据出库单ID和是否已创建工作查询数据
     * @param odoId
     * @param isCreateWork
     * @param ouId
     * @return
     */
    List<WhOdoOutBoundBoxCommand> gethOdoOutBoundBoxLstByOdo(@Param("odoId") Long odoId, @Param("odoLineId") Long odoLineId, @Param("isCreateWork") Boolean isCreateWork, @Param("ouId") Long ouId);


    public List<Long> getWaveOdoIdList(@Param("waveId") Long waveId, @Param("ouId") Long ouId);
    
    public List<Long> getWaveOdoIdListByOdoStatus(@Param("waveId") Long waveId, @Param("odoStatus") String odoStatus, @Param("ouId") Long ouId);

    /**
     * 
     * @param waveId
     * @param odoId
     * @param odoLineId
     * @param ouId @required
     * @return
     */
    List<WhOdoOutBoundBox> findOutboundboxListByWaveIdAndOdoIdAndOdoLineIdAndOuId(@Param("waveId") Long waveId, @Param("odoId") Long odoId, @Param("odoLineId") Long odoLineId, @Param("ouId") Long ouId);

    long deleteExt(@Param("id") Long id, @Param("ouId") Long ouId);
    
    /**
     * 
     * @param waveIds
     * @return
     */
    int deleteWaveByIds(@Param("list") List<Long> waveIds, @Param("ouId") Long ouId);
    
    /**
     * 
     * @param waveId
     * @return
     */
    List<WhOdoOutBoundBox> findOdoOutBoundByWaveId(@Param("waveId") Long waveId);
    
    /**
     * 获取推荐出库箱类型名称
     * @param outbounxboxTypeCode
     * @param ouId
     * @return
     */
    String getWhOutboundBoxTypeName(@Param("outbounxboxTypeCode") String outbounxboxTypeCode, @Param("ouId") Long ouId);

}
