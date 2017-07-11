package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.wave.WaveLineCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;

public interface WhWaveLineManager extends BaseManager {

    /**
     * [业务方法] 得到所有硬阶段需要分配规则或硬分配的波次名次行集合
     * @return
     */
    List<WhWaveLine> getHardAllocationWhWaveLine(Integer allocatePhase, Long ouId);

    /**
     * [通用方法] 获取波次明细列表
     * @param whWaveLine
     * @return
     */
    List<WhWaveLine> getWaveLineByParam(WhWaveLine whWaveLine);

    /**
     * [业务方法] 软分配-获取波次明细列表
     * @param waveId
     * @param ouId
     * @return
     */
    List<WhWaveLine> getSoftAllocationWhWaveLine(Long waveId, Long ouId);

    /**
     * [通用方法] 获取波次明细
     * @param waveLineId
     * @param ouId
     * @return
     */
    WhWaveLine getWaveLineByIdAndOuId(Long waveLineId, Long ouId);
    
    /**
	 * [业务方法] 硬分配-添加规则Id到一批波次明细行
	 */
	void modifyRuleIntoWhWaveLine(List<Long> whWaveLine, Long ruleId, Long ouId);
	
	/**
	 * [业务方法] 硬分配-根据波次ID获得所有出库单明细ID
	 */
	List<Long> getOdoLinesByWaveIdList(List<Long> waveIdList, Long ouId);
	
	/**
	 * [业务方法] 硬分配-在一个波次明细中剔除整单并记录原因
	 */
    WhOdo deleteWaveLinesByOdoId(Long odoId, Long waveId, Long ouId, String reason, String logId);
	
	/**
	 * [业务方法] 硬分配-在一个波次明细中剔除整单并记录原因
	 */
	void deleteWaveLinesByOdoIdList(List<Long> odoId, Long waveId, Long ouId, String reason);
	
	/**
	 * [业务方法] 硬分配-根据波次ID获得所有出库单明细ID
	 */
	List<WhWaveLine> getWhWaveLinesByWaveIdList(List<Long> waveIdList, Long ouId);
	
	/**
	 * [业务方法] 硬分配-分配库存成功后回填分配数量和分配容器数量
	 * @param line
	 * @param containerQty
	 * @param ouId 
	 * @param staticLocationIds 
	 * @param isStaticLocation 
	 * @param packingCaseIds 
	 * @param trayIds 
	 * @param logId 
	 */
	void updateWaveLineByAllocateQty(Long id, Double allocateQty, Double containerQty, Boolean isStaticLocation, Set<String> staticLocationIds, Set<String> trayIds, Set<String> packingCaseIds, Long areaId, Long ouId, String logId);

	Map<Long, Map<Long, Map<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>>>> getNeedInventoryMap(Long waveId, Long ouId);

    /**
     * [通用方法]根据波次查询波次明细
     * 
     * @param waveId
     * @param ouId
     * @return
     */
    List<WhWaveLine> findWaveLineListByWaveId(Long waveId, Long ouId);

    /**
     * [通用方法]根据波次查询波次明细
     *
     * @author mingwei.xie
     * @param waveId
     * @param ouId
     * @return
     */
    List<WhWaveLineCommand> findWaveLineCommandListByWaveId(Long waveId, Long ouId);
    
    /**
     * [业务方法] 补货-查询波次内需要补货的波次明细集合
     * @param waveId
     * @param ouId
     * @return
     */
	List<WhWaveLine> findWaveLineByNotEnoughAllocationQty(Long waveId, Long ouId);

    Pagination<WaveLineCommand> findWaveLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 根据波次Id获取出库单Id
     *
     * @param waveId
     * @param ouId
     * @return
     */
    List<Long> getOdoIdListByWaveId(Long waveId, Long ouId);

    /**
     * 根据波次Id获取出库单Id
     *
     * @param waveIdList
     * @param ouId
     * @return
     */
    List<Long> getOdoIdListByWaveIdList(List<Long> waveIdList, Long ouId);
    
    /**
     * 波次中提出取消的单据
     * @author kai.zhu
     * @version 2017年6月27日
     */
    boolean deleteWaveLinesForCancelOdoByWaveId(Long waveId, Long ouId);
}
