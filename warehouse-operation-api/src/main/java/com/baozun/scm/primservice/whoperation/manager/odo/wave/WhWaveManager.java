package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;

public interface WhWaveManager extends BaseManager {

    /**
     * [通用方法] 软分配-获取波次头信息
     * @param waveId
     * @param ouId
     * @return
     */
    public WhWave getWaveByIdAndOuId(Long waveId, Long ouId);

    /**
     * [业务方法] 软分配-获取波次中所有商品库存可用总量
     * @param waveId
     * @param ouId
     * @return
     */
    public List<SoftAllocationCommand> getSkuInvTotalQty(Long waveId, Long ouId);

    /**
     * [业务方法] 软分配-获取波次中所有商品已分配总量
     * @param waveId
     * @param ouId
     * @return
     */
    public List<SoftAllocationCommand> getSkuInvOccupiedQty(Long waveId, Long ouId);

    /**
     * [业务方法] 软分配-更新波次头
     * @param waveId
     * @param ouId
     */
    public void updateWaveAfterSoftAllocate(Long waveId, Long ouId);

    /**
     * [业务方法]波次一览
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    public Pagination<WaveCommand> findWaveListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法] 软分配-软分配开始阶段更新波次状态
     * @param whWave
     */
    public void updateWaveForSoftStart(WhWave whWave);

}
