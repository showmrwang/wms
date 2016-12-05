package com.baozun.scm.primservice.whoperation.manager.odo.merge;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;

public interface OdoMergeManager extends BaseManager {
    /**
     * [业务方法] 合并订单-分页
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<OdoResultCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法] 合并订单-合并出库订单
     * @param odoIds
     * @param options
     * @param ouId
     * @param userId
     * @return
     */
    Map<String, String> odoMerge(List<String> odoIds, List<String> options, Long ouId, Long userId);

    /**
     * [业务方法] 合并订单-返回合并成功列表
     * @param successIds
     * @param ouId
     * @return
     */
    List<OdoCommand> findOdoList(String ids, Long ouId, String odoStatus);

    /**
     * [业务方法] 合并订单-传入可合并子订单列表->合并成多个订单 返回map<K, V>, Key是success或fail, Value是出库单ids
     * 
     * 1,新建新合并订单主档
     * 2,更新原订单状态
     * 3,新建新合并订单属性表以及配送对象表和运输商管理表
     * 4,新建新合并订单明细行以及属性对象
     * 5,更新原订单明细行以及属性
     * 6,插入新建合并订单明细行及属性
     *
     * @param odoMergeCommandList
     * @param ouId
     * @param userId
     * @return
     */
    Map<String, String> startOdoMerge(List<OdoMergeCommand> odoMergeCommandList, Long ouId, Long userId);

    /**
     * [通用方法] 合并订单-传入可合并子订单->合并成一个订单
     * @param waveId
     * @param odoMergeCommand 对象由whOdoDao.odoMerge查出
     * @param ouId
     * @param userId
     * @return 新生成的订单
     */
    WhOdo generalOdoMerge(String odoIdString, Long ouId, Long userId);

    /**
     * [业务方法] 波次合并出库单-合并出库单
     * @param odoIds
     * @param ouId
     */
    void waveOdoMerge(WhWave wave, String odoIds, Long ouId, Long userId);

    /**
     * [业务方法] 合并出库单-取消合并
     * @param odoId
     * @param ouId
     * @param userId
     */
    void odoMergeCancel(Long odoId, Long ouId, Long userId);

}
