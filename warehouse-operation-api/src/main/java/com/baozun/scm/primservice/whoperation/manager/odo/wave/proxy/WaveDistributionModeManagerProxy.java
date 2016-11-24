package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface WaveDistributionModeManagerProxy extends BaseManager {

    /**
     * 波次中设置配货模式
     * 
     * @param waveId
     * @param waveMasterId
     * @param ouId
     * @param userId
     */
    public void setWaveDistributionMode(Long waveId, Long waveMasterId, Long ouId, Long userId);

    /**
     * 获取秒杀出库单集合
     * 
     * @param ouId
     * @return
     */
    public Map<String, List<Long>> getSecKillOdoList(Long ouId);

    /**
     * 仓库配置秒杀配货模式
     * 
     * @param code
     * @param ouId
     * @param odoIdList
     * @param distributeMode
     */
    public void initSecKillDistributionMode(String code, Long ouId, List<Long> odoIdList, String distributeMode);

    /**
     * 
     * @param ouId
     * @return
     */
    public Map<String, List<Long>> getSuitsOdoList(Long ouId);

    /**
     * 仓库配置
     * 
     * @param code
     * @param ouId
     * @param odoIdList
     * @param distributeMode
     */
    public void initSuitsDistributionMode(String code, Long ouId, List<Long> odoIdList, String distributeMode);

    public Map<String, List<Long>> getTwoSuitsOdoList(Long ouId);

    public void initTwoSuitsDistributionMode(String code, Long ouId, List<Long> odoIdList, String distributeMode);
}
