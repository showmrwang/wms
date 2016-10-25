package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

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
}
