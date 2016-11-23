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
    
    private ContainerDao containerDao;
    @Autowired
    private OutBoundBoxTypeDao outBoundBoxTypeDao;
    
    
    @Override
    public PickingScanResultCommand  pdaPickingRemmendContainer(PickingScanResultCommand  command) {
        log.info("PdaPickingWorkManagerImpl pdaPickingRemmendContainer is start");
        PickingScanResultCommand pSRcmd = new PickingScanResultCommand();
        Long operatorId = command.getOperatorId();
        Long ouId = command.getOuId();
        Integer pickingWay = command.getPickingWay();
        pSRcmd.setOperatorId(operatorId);
        if(pickingWay == Constants.PICKING_WAY_ONE) { //使用外部容器(小车) 无出库箱拣货流程
            Long tipOuterContainerId = pdaPickingWorkCacheManager.pdaPickingWorkTipOutContainer(operatorId);
            Container c = containerDao.findByIdExt(tipOuterContainerId, ouId);
            if(null == c) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            pSRcmd.setTipOuterContainerCode(c.getCode());  //提示小车
        }
        if(pickingWay == Constants.PICKING_WAY_TWO) { //使用外部(小车)，有出库箱拣货流程
            Long tipOuterContainerId = pdaPickingWorkCacheManager.pdaPickingWorkTipOutContainer(operatorId);
            Container c = containerDao.findByIdExt(tipOuterContainerId, ouId);
            if(null == c) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            pSRcmd.setTipOuterContainerCode(c.getCode());  //提示小车
        }
        if(pickingWay == Constants.PICKING_WAY_THREE) { //使用出库箱拣货流程
            Long tipOuterContainerId = pdaPickingWorkCacheManager.pdaPickingWorkTipoutbounxBox(operatorId);
            Container c = containerDao.findByIdExt(tipOuterContainerId, ouId);
            if(null == c) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            pSRcmd.setTipOuterContainerCode(c.getCode());  //提示出库箱
        }
        if(pickingWay == Constants.PICKING_WAY_FOUR) {  //使用周转箱拣货流程
            Long tipOutBoundId = pdaPickingWorkCacheManager.pdaPickingWorkTipOutBound(operatorId);
            OutBoundBoxType o = outBoundBoxTypeDao.findByIdExt(tipOutBoundId, ouId);
            if(null == o) {
                throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL );
            }
            pSRcmd.setTipOutBoundCode(o.getCode());  //提示周转箱
        }
        log.info("PdaPickingWorkManagerImpl pdaPickingRemmendContainer is end");
        return pSRcmd;
    }

    
}
