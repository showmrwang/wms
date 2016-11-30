package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

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
     * [业务方法]删除波次
     * 
     * @param wave
     * @param waveLineList
     * @param odoList
     * @param odoLineList
     * @param userId
     */
    public void deleteWave(WhWave wave, List<WhWaveLine> waveLineList, List<WhOdo> odoList, List<WhOdoLine> odoLineList, Long userId);

    /**
     * [业务方法] 软分配-软分配开始阶段更新波次状态
     * @param whWave
     */
    public void updateWaveForSoftStart(WhWave whWave);

    /**
     * [通用方法] 根据波次阶段查找波次列表
     * @param phaseCode
     * @param ouId
     */
    List<Long> findWaveByPhase(String phaseCode, Long ouId);

    /**
     * [业务方法] 波次合并出库单-查找可以合并出库单id
     * @param waveId
     * @param ouId
     * @return
     */
    List<OdoMergeCommand> findWaveMergeOdo(Long waveId, Long ouId);

    /**
     * [通用方法] 更新波次阶段
     * @param waveId
     * @param phaseCode
     * @param ouId
     */
    void changeWavePhaseCode(Long waveId, Long ouId);

    /**
     * [业务方法] 硬分配-查找所有需要匹配规则的波次
     */
    public List<Long> getNeedAllocationRuleWhWave(Integer allocatePhase, Long ouId, String logId);

    /**
     * [业务方法] 硬分配-更新波次阶段为硬分配的阶段未硬分配
     */
    int updateWhWaveAllocatePhase(List<Long> waveIdList, Integer allocatePhase, Long ouId);

    /**
     * [业务方法] 硬分配-检查一个波次中分配数量是否足够
     * @param waveId
     * @param wh
     */
    void checkWaveHardAllocateEnough(Long waveId, Warehouse wh);

    /**
     * [业务方法] 硬分配-异常释放这一波次库存
     */
    void releaseInventoryByWaveId(Long waveId, Warehouse wh);

    /**
     * 查询波次可用的配货模式
     * 
     * @param ouId
     * @return
     */
    List<WhDistributionPatternRuleCommand> findRuleByOuIdOrderByPriorityAsc(Long ouId);

    /**
     * 查询波次中适用于某种配货模式的出库单集合
     * 
     * @param waveId
     * @param ouId
     * @param ruleSql
     * @return
     */
    List<Long> findOdoListInWaveWhenDistributionPattern(Long waveId, Long ouId, String ruleSql);

    /**
     * [业务方法]
     * 
     * @param odoList
     * @param offWaveLineList
     * @param offOdoLineList
     * @param wave
     * @param ouId
     * @param userId
     */
    void matchWaveDisTributionMode(List<WhOdo> odoList, List<WhWaveLine> offWaveLineList, List<WhOdoLine> offOdoLineList, WhWave wave, Long ouId, Long userId);

    /**
     * [通用方法] 根据WavePhaseCode波次编码查找波次ID集合
     * @param phaseCode
     * @param ouId
     * @return
     */
    List<Long> findWaveIdsByWavePhaseCode(String phaseCode, Long ouId);
    
	/**
	 * [业务方法] 整出库单剔除并释放库存
	 */
	void deleteWaveLinesAndReleaseInventoryByOdoId(Long waveId, Long odoId, String reason, Warehouse wh);
	
    /**
     * [业务方法] 在一个波次中查找包含skuIds的OdoId
     * @param skuIds
     * @param ouId
     * @return
     */
    List<Long> findOdoContainsSkuId(Long waveId, List<Long> skuIds, Long ouId);

    /**
     * [业务方法] 软分配-查找可以进行软分配波次
     */
    public List<Long> getNeedSoftAllocationWhWave(Long ouId);
    
    /**
     * [业务方法] 创捡货工作和作业-查找所有需要匹配规则的波次
     */
    public List<Long> getNeedPickingWorkWhWave(Long ouId);
    
    /**
     * [通用方法] 获取波次下一阶段的code
     * @param waveId
     * @param ouId
     * @return
     */
    public String getNextParseCode(Long waveId, Long ouId);

    /**
     * 查找波次主档信息
     * 
     * @param waveMasterId
     * @param ouId
     * @return
     */
    public WhWaveMaster findWaveMasterbyIdOuId(Long waveMasterId, Long ouId);
    
}
