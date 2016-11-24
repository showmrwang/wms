package com.baozun.scm.primservice.whoperation.manager.pda.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;

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

    @Autowired
    private PdaPickingWorkCacheManager  pdaPickingWorkCacheManager;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private OutBoundBoxTypeDao outBoundBoxTypeDao;
    
    /**
     * pda拣货推荐容器
     * @author tangming
     * @param command
     * @param pickingWay
     * @return
     */
    @Override
    public PickingScanResultCommand  pdaPickingRemmendContainer(PickingScanResultCommand  command) {
        log.info("PdaPickingWorkManagerImpl pdaPickingRemmendContainer is start");
        PickingScanResultCommand pSRcmd = new PickingScanResultCommand();
        Long operatorId = command.getOperatorId();
        Long ouId = command.getOuId();
        Integer pickingWay = command.getPickingWay();
        pSRcmd.setOperatorId(operatorId);
        if(pickingWay == Constants.PICKING_WAY_ONE) { //使用外部容器(小车) 无出库箱拣货流程
            String tipOuterContainer = pdaPickingWorkCacheManager.pdaPickingWorkTipOutContainer(operatorId,ouId);
            pSRcmd.setTipOuterContainer(tipOuterContainer);  //提示小车
        }
        if(pickingWay == Constants.PICKING_WAY_TWO) { //使用外部(小车)，有出库箱拣货流程
            String tipOuterContainer = pdaPickingWorkCacheManager.pdaPickingWorkTipOutContainer(operatorId,ouId);
            pSRcmd.setTipOuterContainer(tipOuterContainer);  //提示小车
        }
        if(pickingWay == Constants.PICKING_WAY_THREE) { //使用出库箱拣货流程
            String tipOutBoundBox = pdaPickingWorkCacheManager.pdaPickingWorkTipoutboundBox(operatorId,ouId);
            pSRcmd.setOutBoundCode(tipOutBoundBox);
        }
        if(pickingWay == Constants.PICKING_WAY_FOUR) {  //使用周转箱拣货流程
            String turnoverBox = pdaPickingWorkCacheManager.pdaPickingWorkTipTurnoverBox(operatorId,ouId);
            pSRcmd.setTipTurnoverBoxCode(turnoverBox);
        }
        log.info("PdaPickingWorkManagerImpl pdaPickingRemmendContainer is end");
        return pSRcmd;
    }


    /***
     * 循环扫描排序后的库位
     * @author tangming
     * @param command
     * @return
     */
    @Override
    public PickingScanResultCommand loopScanLocation(PickingScanResultCommand command) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkManagerImpl loopScanLocation is start");
        log.info("PdaPickingWorkManagerImpl loopScanLocation is start");
        return null;
    }

    
}
