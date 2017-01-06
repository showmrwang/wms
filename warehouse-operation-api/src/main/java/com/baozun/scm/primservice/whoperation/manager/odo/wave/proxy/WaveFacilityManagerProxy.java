package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import com.baozun.scm.primservice.whoperation.command.odo.wave.RecFacilityPathCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface WaveFacilityManagerProxy extends BaseManager {
    /**
     * 推荐播种墙
     * 
     * @param recFacilityPath
     */
    RecFacilityPathCommand matchOutboundFacility(RecFacilityPathCommand recFacilityPath);
}
