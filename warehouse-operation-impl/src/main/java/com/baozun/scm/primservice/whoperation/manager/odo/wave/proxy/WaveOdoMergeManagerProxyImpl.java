package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.constant.WavePhase;
import com.baozun.scm.primservice.whoperation.manager.odo.merge.OdoMergeManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;

public class WaveOdoMergeManagerProxyImpl implements WaveOdoMergeManagerProxy {

    @Autowired
    private WhWaveManager whWaveManager;

    @Autowired
    private OdoMergeManager odoMergeManager;

    @Override
    public List<Long> findWaveInMergePhase(Long ouId) {
        List<Long> waveIds = whWaveManager.findWaveByPhase(WavePhase.MERGE_ODO, ouId);
        return waveIds;
    }

    @Override
    public List<OdoMergeCommand> findWaveMergeOdo(Long waveId, Long ouId) {
        List<OdoMergeCommand> odoIds = whWaveManager.findWaveMergeOdo(waveId, ouId);
        return odoIds;
    }

    @Override
    public void changeWavePhaseCode(Long waveId, Long ouId) {
        whWaveManager.changeWavePhaseCode(waveId, WavePhase.MERGE_ODO, ouId);
    }

    @Override
    public void waveOdoMerge(Long waveId, String odoIds, Long ouId) {

        WhWave wave = whWaveManager.getWaveByIdAndOuId(waveId, ouId);
        odoMergeManager.waveOdoMerge(wave, odoIds, ouId, null);

    }
}
