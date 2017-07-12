package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationResponseCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationWaveCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;


/**
 * @author gianni.zhang
 *
 * 2016年9月23日 上午11:01:20
 */
public interface WhWaveSoftManagerProxy extends BaseManager {

    /**
     * [业务方法] 软分配-返回波次明细给上层服务
     * @param waveId
     * @param ouId
     * @return
     */
    public SoftAllocationWaveCommand getWaveLineForSoft(Long waveId, Long ouId);

    /**
     * [通用方法] 获取波次头信息
     * @param waveId
     * @param ouId
     */
    public WhWave getWaveHeadByIdAndOuId(Long waveId, Long ouId);

    /**
     * [通用方法] 获取波次明细列表
     * @param waveId
     * @param ouId
     * @return
     */
    public List<WhWaveLine> getWaveLineByWaveIdAndOuId(Long waveId, Long ouId);

    /**
     * [业务方法] 软分配-获取波次明细列表并排序
     * @param waveId
     * @param ouId
     * @return
     */
    public List<WhWaveLine> getWaveLineByWaveIdAndOuIdForSoft(Long waveId, Long ouId);

    /**
     * [业务方法] 软分配-获取商品库存可用量总和
     * @param waveId
     * @param ouId
     * @return
     */
    public List<SoftAllocationCommand> getSkuInvTotalQty(Long waveId, Long ouId);

    /**
     * [业务方法] 软分配-获取商品已分配量总和
     * @param waveId
     * @param ouId
     * @return
     */
    public List<SoftAllocationCommand> getSkuInvOccupiedQty(Long waveId, Long ouId);

    /**
     * [业务方法] 软分配-占用逻辑
     * @param waveId
     * @param skuId
     * @param waveLineId
     * @param ouId
     * @param skuInvAvailableQtyMap
     * @return
     */
    public SoftAllocationResponseCommand occupiedOperation(Long waveId, Long skuId, Long waveLineId, Long ouId, Map<Long, Long> skuInvAvailableQtyMap);

    /**
     * [业务方法] 软分配-整单剔除逻辑
     * @param waveId
     * @param odoId
     * @param odoLineIds
     * @param ouId
     */
    public void removeWaveLine(Long waveId, Long odoId, List<Long> odoLineIds, Long ouId);

    /**
     * [业务方法] 通用剔除出库单方法
     * 
     * @param waveId
     * @param odoId
     * @param odoLineIds
     * @param ouId
     * @param logId
     */
    public void removeWaveLineGeneral(Long waveId, Long odoId, Long ouId, String logId);

    /**
    * [业务方法] 软分配-更新波次头
    * @param whWave
    * @param ouId
    */
    public void updateWave(WhWave whWave, Long ouId);

    /**
     * [业务方法] 软分配-清除数据
     * @param waveId
     * @param ouId
     */
    public void cleanUpData(Long waveId, Long ouId);
}
