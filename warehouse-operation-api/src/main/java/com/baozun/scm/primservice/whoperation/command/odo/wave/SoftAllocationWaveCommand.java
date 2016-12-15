package com.baozun.scm.primservice.whoperation.command.odo.wave;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;

public class SoftAllocationWaveCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -8920773706853136978L;

    private List<WhWaveLine> whWaveLine;

    private WhWave whWave;

    public List<WhWaveLine> getWhWaveLine() {
        return whWaveLine;
    }

    public void setWhWaveLine(List<WhWaveLine> whWaveLine) {
        this.whWaveLine = whWaveLine;
    }

    public WhWave getWhWave() {
        return whWave;
    }

    public void setWhWave(WhWave whWave) {
        this.whWave = whWave;
    }



}
