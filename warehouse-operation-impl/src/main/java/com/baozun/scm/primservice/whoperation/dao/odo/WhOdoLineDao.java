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

import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;

public interface WhOdoLineDao extends BaseDao<WhOdoLine, Long> {


    @QueryPage("findListCountByQueryMap")
    Pagination<WhOdoLine> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> params);

    @QueryPage("queryCount")
    Pagination<WhOdoLine> query(Page page, Sort[] sorts, QueryCondition cond);

    List<WhOdoLine> query(QueryCondition cond);

    Long queryCount(QueryCondition cond);

    @CommonQuery
    int saveOrUpdate(WhOdoLine o);

    @CommonQuery
    int saveOrUpdateByVersion(WhOdoLine o);

    /**
     * [通用方法]根据ODOLINEID,OUID查找ODOLINE
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdoLine findOdoLineById(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [业务方法]出库单明细分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @QueryPage("findListCountByQueryMapExt")
    Pagination<OdoLineCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [通用方法]根据ODOID和OUID查找明细数量
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    long findOdoLineListCountByOdoIdOuId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * [通用方法]根据ODOID和OUID查找明细
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    List<WhOdoLine> findOdoLineListByOdoIdOuId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * [通用方法]根据ODOID和OUID查找明细
     *
     * @param odoId
     * @param ouId
     * @return
     */
    List<OdoLineCommand> findOdoLineCommandListByOdoIdOuId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * 获取出库单明细列表
     * @param o
     * @return
     */
    List<OdoLineCommand> findObject(WhOdoLine o);

    int deleteByIdOuId(@Param("id") Long id, @Param("ouId") Long ouId);

    /**
     * [通用方法]查找出库单明细
     * 
     * @param line
     * @return
     */
    List<WhOdoLine> findListByParamExt(WhOdoLine line);

    /**
     * [业务方法] 合并订单-通过合并后出库单code和原始出库单明细行 行号查找原始出库单明细行信息
     */
    WhOdoLine findByOdoCodeAndLineNum(@Param("lineNum") Integer lineNum, @Param("originalOdoCode") String originalOdoCode, @Param("ouId") Long ouId);

    /**
     * [业务方法] 修改出库单明细waveCode为空
     */
    int updateOdoLineByAllocateFail(@Param("odoId") Long odoId, @Param("reason") String reason, @Param("ouId") Long ouId);
    
    /**
     * 更新订单明细状态
     * @param modified
     * @param status
     * @param odoId
     * @return
     */
    int updateOdoLineTypeByOdoId(WhOdoLine o);

    /**
     * [业务方法] 修改出库单明细waveCode为空
     */
    int updateOdoLineByAllocateFailAndOdoIdList(@Param("odoIdList") List<Long> odoIdList, @Param("reason") String reason, @Param("ouId") Long ouId);

    /**
     * [通用方法]根据ODOID查找某种状态的出库单
     * 
     * @param odoId
     * @param ouId
     * @param statusList
     * @return
     */
    List<WhOdoLine> findOdoLineListByOdoIdStatus(@Param("odoId") Long odoId, @Param("ouId") Long ouId, @Param("statusList") String[] statusList);

    /**
     * [业务方法] 软分配-查询不为新建状态的出库单明细
     * @param line
     * @return
     */
    Long findListCountNotNew(WhOdoLine line);

    /**
     * 根据OdoId把明细的分配数量变成0
     * @param odoId
     * @param ouId
     * @return
     */
    int updateOdoLineAssignQtyIsZero(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * 【业务方法】根据出库单ID集合查找出库单明细
     * 
     * @param odoIdList
     * @param ouId
     * @return
     */
    List<WhWaveLine> findByOdoIdList(@Param("odoIdList") List<Long> odoIdList, @Param("ouId") Long ouId);

    /**
     * 【业务方法】将出库单明细添加到波次中
     * 
     * @param odoIdList
     * @param status
     * @param code
     * @param ouId
     * @param userId
     * @return
     */
    int updateOdoLineToWave(@Param("odoIdList") List<Long> odoIdList, @Param("status") String status, @Param("waveCode") String code, @Param("ouId") Long ouId, @Param("userId") Long userId);

    List<OdoLineCommand> findOdoLineListToWaveByOdoIdListOuId(@Param("odoIdList") List<Long> odoIdList, @Param("ouId") Long ouId);

    /**
     * 根据ODOLINEID和OUID查找ODOLINE
     *
     * @author mingwei.xie
     * @param idList
     * @param ouId
     * @return
     */
    List<OdoLineCommand> findOdoLineByIdList(@Param("idList") List<Long> idList, @Param("ouId") Long ouId);

    /**
     * 根据ODOLINEID和OUID查找ODOLINE
     *
     * @author mingwei.xie
     * @param idStrList
     * @param ouId
     * @return
     */
    List<OdoLineCommand> findOdoLineByIdStrList(@Param("idStrList") List<String> idStrList, @Param("ouId") Long ouId);

    /**
     * 根据ODOLINEID和OUID查找ODOLINE
     *
     * @author mingwei.xie
     * @param idList
     * @param ouId
     * @return
     */
    List<OdoLineCommand> findOdoLineByOdoId(@Param("idList") List<Long> idList, @Param("ouId") Long ouId);

    /**
     * 根据ODOLINEID和OUID查找ODOLINE
     *
     * @author mingwei.xie
     * @param idList
     * @param ouId
     * @return
     */
    List<OdoLineCommand> findOdoLineByOdoIdOrderByPickingSort(@Param("idList") List<Long> idList, @Param("ouId") Long ouId);

    List<WhOdoLine> findOdoLineListByOdoIdAndLinenumList(@Param("odoId") Long odoId, @Param("ouId") Long ouId, @Param("extLinenumList") List<Integer> lineSeq);

    /**
     * 查找出库单中的商品id集合
     */
    List<Long> findSkuIdListByOdoIdOuId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    /**
     * [业务方法] 查找出库单所有明细状态
     * @param odoId
     * @param ouId
     * @return
     */
    Long findCntByOdoIdAndOuId(@Param("odoId") Long odoId, @Param("ouId") Long ouId);

    Long countNotSuitDistribeModeLines(@Param("odoId") Long odoId, @Param("ouId") Long ouId);
}
