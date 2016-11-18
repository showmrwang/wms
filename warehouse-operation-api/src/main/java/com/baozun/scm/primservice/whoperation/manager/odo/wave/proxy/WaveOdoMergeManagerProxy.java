package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface WaveOdoMergeManagerProxy extends BaseManager {

    /**
     * [业务方法] 波次中合并出库单-查找可以合并的波次列表
     * @param ouId
     * @return
     */
    List<Long> findWaveInMergePhase(Long ouId);

    /**
     * [业务方法] 波次中合并出库单-查找波次中可以合并的出库单
     * @param waveId
     * @param ouId
     * @return
     */
    List<OdoMergeCommand> findWaveMergeOdo(Long waveId, Long ouId);

    /**
     * [业务方法] 波次中合并出库单-修改波次阶段
     * @param waveId
     * @param ouId
     */
    void changeWavePhaseCode(Long waveId, Long ouId);

    /**
     * [业务方法] 波次中合并出库单-合并出库单
     * @param waveId
     * @param odoIds
     * @param ouId
     */
    void waveOdoMerge(Long waveId, String odoIds, Long ouId);
}
