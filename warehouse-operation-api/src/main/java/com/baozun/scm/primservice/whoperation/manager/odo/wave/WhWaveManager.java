package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
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
    void changeWavePhaseCode(Long waveId, String phaseCode, Long ouId);

    /**
	 * [业务方法] 硬分配-查找所有需要匹配规则的波次
	 */
	public List<Long> getNeedAllocationRuleWhWave(Integer allocatePhase, Long ouId, String logId);
	
	/**
	 * [业务方法] 硬分配-更新波次阶段为硬分配的阶段未硬分配
	 */
	int updateWhWaveAllocatePhase(List<Long> waveIdList, Integer allocatePhase, Long ouId);

	void checkWaveHardAllocateEnough(Long waveId, Warehouse wh);
	
	/**
	 * [业务方法] 硬分配-异常释放这一波次库存
	 */
	void releaseInventoryByWaveId(Long waveId, Warehouse wh);

}
