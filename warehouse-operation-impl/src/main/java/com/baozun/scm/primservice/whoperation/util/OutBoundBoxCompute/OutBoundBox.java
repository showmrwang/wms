package com.baozun.scm.primservice.whoperation.util.OutBoundBoxCompute;

public class OutBoundBox implements Comparable<OutBoundBox> {

    /** 物品体积 */
    private double volume;

    /** 波次明细ID */
    private Long waveLineId;

    public OutBoundBox(double volume, Long waveLineId) {
        this.volume = volume;
        this.waveLineId = waveLineId;
    }

    public OutBoundBox() {}

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public Long getWaveLineId() {
        return waveLineId;
    }

    public void setWaveLine(Long waveLineId) {
        this.waveLineId = waveLineId;
    }

    @Override
    public int compareTo(OutBoundBox snapsack) {
        double value = snapsack.volume;
        if (volume > value) return 1;
        if (volume < value) return -1;
        return 0;
    }
}
