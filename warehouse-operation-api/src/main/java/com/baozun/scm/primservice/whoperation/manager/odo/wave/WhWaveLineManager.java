package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.List;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;

public interface WhWaveLineManager extends BaseManager {
	
	/**
	 * 得到所有硬阶段需要分配规则或硬分配的波次名次行集合
	 * @return
	 */
	List<WhWaveLine> getHardAllocationWhWaveLine(Integer allocatePhase, Long ouId);
}
