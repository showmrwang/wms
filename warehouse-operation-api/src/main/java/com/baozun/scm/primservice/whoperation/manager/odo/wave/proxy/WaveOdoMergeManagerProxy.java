package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface WaveOdoMergeManagerProxy extends BaseManager {

    List<Long> findWaveInMergePhase(Long ouId);

    List<OdoMergeCommand> findWaveMergeOdo(Long waveId, Long ouId);

    void changeWavePhaseCode(Long waveId, Long ouId);

    void waveOdoMerge(Long waveId, String odoIds, Long ouId);
}
