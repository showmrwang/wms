package com.baozun.scm.primservice.whoperation.manager.pda.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingParamCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;

/**
 * PDA拣货manager
 * 
 * @author qiming.liu
 * 
 */
@Service("pdaPickingWorkManager")
@Transactional
public class PdaPickingWorkManagerImpl extends BaseManagerImpl implements PdaPickingWorkManager {

    protected static final Logger log = LoggerFactory.getLogger(PdaPickingWorkManagerImpl.class);

    @Override
    public PickingParamCommand pdaPickingRemmendContainer(PickingParamCommand command, Integer pickingWay) {
        log.info("PdaPickingWorkManagerImpl pdaPickingRemmendContainer is start");
        if(pickingWay == Constants.PICKING_WAY_ONE) { //使用外部容器(小车) 无出库箱拣货流程
            
        }
        if(pickingWay == Constants.PICKING_WAY_TWO) { //使用外部(小车)，有出库箱拣货流程
            
        }
        if(pickingWay == Constants.PICKING_WAY_THREE) { //使用出库箱拣货流程
            
        }
        if(pickingWay == Constants.PICKING_WAY_FOUR) {  //使用周转箱拣货流程
            
        }
        log.info("PdaPickingWorkManagerImpl pdaPickingRemmendContainer is end");
        return null;
    }

    
}
