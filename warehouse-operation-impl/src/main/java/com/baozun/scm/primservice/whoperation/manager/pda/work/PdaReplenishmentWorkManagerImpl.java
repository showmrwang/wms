package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionReplenishmentDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionReplenishment;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;

@Service("pdaReplenishmentWorkManager")
@Transactional
public class PdaReplenishmentWorkManagerImpl extends BaseManagerImpl implements PdaReplenishmentWorkManager {


    protected static final Logger log = LoggerFactory.getLogger(PdaReplenishmentWorkManagerImpl.class);
    
    @Autowired
    private PdaPickingWorkCacheManager pdaPickingWorkCacheManager;
    @Autowired
    private CacheManager cacheManager; 
    @Autowired
    private WhLocationDao whLocationDao;
    @Autowired
    private WhFunctionReplenishmentDao whFunctionReplenishmentDao;
    @Autowired
    private PdaPickingWorkManager pdaPickingWorkManager;
    @Autowired
    private WhOperationManager whOperationManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private ContainerDao containerDao;
    
    /****
     * 确定补货方式和占用模型
     * 
     * @author qiming.liu
     * @param  whWork
     * @param  ouId
     * @return
     */
    @Override
    public PickingScanResultCommand getReplenishmentForGroup(WhWork whWork, Long ouId) {
        // 根据工作id获取作业信息        
        WhOperationCommand whOperationCommand = whOperationManager.findOperationByWorkId(whWork.getId(), ouId);
        // 统计分析工作及明细并缓存
        pdaPickingWorkManager.getOperatioLineForGroup(whOperationCommand);
        // 获取缓存中的统计分析数据        
        OperatioLineStatisticsCommand statisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(whOperationCommand.getId(), whOperationCommand.getOuId());
        // 返回结果初始化        
        PickingScanResultCommand psRCmd = new PickingScanResultCommand();
        // 作业id        
        psRCmd.setOperationId(whOperationCommand.getId());
        // 捡货方式        
        if(whOperationCommand.getIsWholeCase() == false && statisticsCommand.getOuterContainers().size() > 0 && statisticsCommand.getOutbounxBoxs().size() == 0){
            psRCmd.setReplenishWay(Constants.REPLENISH_WAY_ONE);
        }else if(whOperationCommand.getIsWholeCase() == true && statisticsCommand.getPallets().size() > 0 && statisticsCommand.getPallets().size() > 0){
            psRCmd.setReplenishWay(Constants.REPLENISH_WAY_TWO);
        }else if(whOperationCommand.getIsWholeCase() == true && statisticsCommand.getPallets().size() == 0 && statisticsCommand.getPallets().size() > 0){
            psRCmd.setReplenishWay(Constants.REPLENISH_WAY_THREE);
        }
        // 库存占用模型
        if(statisticsCommand.getOuterContainerIds().size() > 0 && statisticsCommand.getInsideContainerIds().size() == 0 && statisticsCommand.getInsideSkuIds().size() == 0){
            // 仅占用托盘内商品            
            psRCmd.setInvOccupyMode(Constants.INV_OCCUPY_MODE_ONE);
        }else if(statisticsCommand.getOuterContainerIds().size() == 0 && statisticsCommand.getInsideContainerIds().size() > 0 && statisticsCommand.getInsideSkuIds().size() == 0){
            // 仅占用货箱内商品
            psRCmd.setInvOccupyMode(Constants.INV_OCCUPY_MODE_TWO);
        }else if(statisticsCommand.getOuterContainerIds().size() == 0 && statisticsCommand.getInsideContainerIds().size() == 0 && statisticsCommand.getInsideSkuIds().size() > 0){
            // 仅占用库位上散件商品
            psRCmd.setInvOccupyMode(Constants.INV_OCCUPY_MODE_THREE);
        }else{
            //混合占用
            psRCmd.setInvOccupyMode(Constants.INV_OCCUPY_MODE_FOUR);
        }
        return psRCmd;
    }

    /***
     * 提示拣货库位
     * @author tangming
     * @param operationId
     * @return
     */
    public PickingScanResultCommand replenishmentTipLocation(Long functionId,Long operationId,Long ouId){
           PickingScanResultCommand psRCmd = new PickingScanResultCommand();
           OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
           if(null == operatorLine) {
               throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
           }
           List<Long> locationIds = operatorLine.getLocationIds();   //所有排序后的拣货库位
           CheckScanResultCommand cSRCmd =  pdaPickingWorkCacheManager.tipLocation(operationId, locationIds);
           if(cSRCmd.getIsPicking()) {
               throw new BusinessException(ErrorCodes.REPLE_WORK_ISEND);
           }else{
               psRCmd.setIsPicking(false);
               Long locationId = cSRCmd.getTipLocationId();   //提示库位id
               Location location = whLocationDao.findByIdExt(locationId, ouId);
               if(null == location){
                   throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
               }
               psRCmd.setTipLocationBarCode(location.getBarCode());
               psRCmd.setTipLocationCode(location.getCode());
           }
           //查询补货功能模板参数
           WhFunctionReplenishment resplenishment = whFunctionReplenishmentDao.findByFunctionIdExt(ouId, functionId);
           if(null == resplenishment) {
               throw new BusinessException(ErrorCodes.PARAMS_ERROR);
           }
           psRCmd.setIsScanLocation(resplenishment.getIsScanLocation());  //是否扫描库位
           psRCmd.setIsScanOuterContainer(resplenishment.getIsScanOuterContainer());   //是否扫描托盘
           psRCmd.setIsScanInsideContainer(resplenishment.getIsScanInsideContainer());    //是否扫描内部容器
           psRCmd.setIsScanSku(resplenishment.getIsScanSku());                 //是否扫描sku
           psRCmd.setIsScanInvAttr(resplenishment.getIsScanInvAttr());           //是否扫描sku属性
           psRCmd.setIsTipInvAttr(resplenishment.getIsTipInvAttr());  //是否提示sku库存属性
           psRCmd.setScanPattern(resplenishment.getScanPattern());  //扫描模式 
           return psRCmd;
    }

    /**
     * 校验库位
     * @param locationCode
     * @param locationBarCode
     * @param ouId
     * @return
     */
    @Override
    public Long verificationLocation(String locationCode, String locationBarCode, Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkController verificationLocation is start");
        Long locationId = null;
        if(!StringUtils.isEmpty(locationCode)) {
            Location location =  whLocationDao.findLocationByCode(locationCode, ouId);
            if(null == location) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL );
            }
            locationId = location.getId();
        }
        if(!StringUtils.isEmpty(locationBarCode)) {
            Location location =  whLocationDao.getLocationByBarcode(locationBarCode, ouId);
            if(null == location) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL );
            }
            locationId = location.getId();
        }
        log.info("PdaPickingWorkController verificationLocation is end");
        return locationId;
    }
    
    /***
     * 拣货完成
     * @param command
     */
    public void pdaPickingFinish(PickingScanResultCommand  command,Boolean isTabbInvTotal){
        Long operationId = command.getOperationId();
        String workCode = command.getWorkBarCode();
        Long ouId = command.getOuId();
        Long userId = command.getUserId();
        Long locationId = command.getLocationId();
        String outerContainerCode = command.getOuterContainerCode();
        String turnoverBoxCode = command.getTurnoverBoxCode();
        String insideContainerCode = command.getInsideContainerCode();
        Long outerContainerId = null;
        if(StringUtils.isEmpty(outerContainerCode)) {
            ContainerCommand c = containerDao.getContainerByCode(outerContainerCode, ouId);
            if(null == c) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerContainerId = c.getId();
        }
        Long insideContainerId = null;
        if(StringUtils.isEmpty(insideContainerCode)) {
            ContainerCommand c = containerDao.getContainerByCode(insideContainerCode, ouId);
            if(null == c) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = c.getId();
        }
        
        Long turnoverBoxId = null;
        if(StringUtils.isEmpty(turnoverBoxCode)) {
            ContainerCommand c = containerDao.getContainerByCode(turnoverBoxCode, ouId);
            if(null == c) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            turnoverBoxId = c.getId();
        }
        //已分配的库位库存转变为容器库存
        whSkuInventoryManager.replenishmentContainerInventory(operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId,workCode);
       //更新工作及作业状态
        pdaPickingWorkCacheManager.pdaReplenishmentUpdateOperation(operationId, ouId,userId);
        //清除缓存
        pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(operationId, false, locationId);
    }
    
    public void cacheLocation(Long operationId,String locationCode,Long ouId){
        
        Location location = whLocationDao.findLocationByCode(locationCode, ouId);
        if(null == location) {
            location =  whLocationDao.getLocationByBarcode(locationCode, ouId);
            if(null == location) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL); 
            }
        }
        Long locationId = location.getId();
        pdaPickingWorkCacheManager.cacheLocation(operationId, locationId);
    }
}
