package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioExecLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperationExecStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentScanResultComamnd;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.warehouse.LocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;
/***
 * 补货中的上架
 * @author Administrator
 *
 */
@Service("pdaInWarehouseMovePutawayManager")
@Transactional
public class PdaInWarehouseMovePutawayManagerImpl extends BaseManagerImpl implements PdaInWarehouseMovePutawayManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaInWarehouseMovePutawayManagerImpl.class);
    @Autowired
    private PdaReplenishmentPutawayCacheManager pdaReplenishmentPutawayCacheManager;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private WhLocationDao whLocationDao;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhWorkDao whWorkDao;
    @Autowired
    private WhOperationDao  whOperationDao;
    @Autowired
    private WhOperationExecLineDao whOperationExecLineDao;
    @Autowired
    private PdaPickingWorkCacheManager pdaPickingWorkCacheManager;
    @Autowired
    private LocationManager locationManager;
    @Autowired
    private WhOperationManager whOperationManager;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private WhWorkLineDao whWorkLineDao;
    
    
    @Override
    public ReplenishmentPutawayCommand putawayTipLocation(ReplenishmentPutawayCommand command) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl putawayTipLocation is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        OperatioExecLineStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_EXEC_LINE + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        List<Long> locationIds = opExecLineCmd.getLocationIds();
        ReplenishmentScanResultComamnd  sRCmd = pdaReplenishmentPutawayCacheManager.tipLocation(locationIds, operationId);
        Long tipLocationId = sRCmd.getLocationId();
        Location location = whLocationDao.findByIdExt(tipLocationId, ouId);
        if(null == location) {
            throw new BusinessException(ErrorCodes.TIP_LOCATION_FAIL);
        }
        command.setTipLcoationBarCode(location.getBarCode());
        command.setTipLocationCode(location.getCode());
        command.setLocationId(location.getId()); 
        command.setIsNeedScanLocation(true);
        log.info("PdaReplenishmentPutawayManagerImpl putawayTipLocation is end");
        return command;
    }

    @Override
    public ReplenishmentPutawayCommand putawayScanLocation(ReplenishmentPutawayCommand command) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanLocation is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        Long locationId = command.getLocationId();
        OperatioExecLineStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_EXEC_LINE + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> locToTurnoverBoxIds = opExecLineCmd.getLocationToTurnoverBoxIds();
        Set<Long> turnoverBoxIds = locToTurnoverBoxIds.get(locationId);
        ReplenishmentScanResultComamnd  sRCmd = pdaReplenishmentPutawayCacheManager.tipTurnoverBox(turnoverBoxIds, operationId);
        Long turnoverBoxId = sRCmd.getTurnoverBoxId();
        String containerCode = this.judeContainer(turnoverBoxId, ouId);
        command.setTipTurnoverBoxCode(containerCode);
        command.setIsNeedScanTurnoverBox(true);
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanLocation is end");
        return command;
    }

    private String judeContainer(Long turnoverBoxId,Long ouId){
        log.info("PdaReplenishmentPutawayManagerImpl judeContainer is start");
        Container container = containerDao.findByIdExt(turnoverBoxId, ouId);
        if(null == container) {
            throw new BusinessException(ErrorCodes.TIP_CONTAINER_FAIL);
        }
        // 验证容器状态是否可用
        if (!container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        // 验证容器状态是否是待上架
        if (!(container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        log.info("PdaReplenishmentPutawayManagerImpl judeContainer is end");
        return container.getCode();
    }

    
    @Override
    public ReplenishmentPutawayCommand putawayScanTurnoverBox(ReplenishmentPutawayCommand command,Boolean isTabbInvTotal) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanTurnoverBox is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        Long locationId = command.getLocationId();
        Long userId = command.getUserId();
        String workCode = command.getWorkBarCode();
        OperatioExecLineStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_EXEC_LINE + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> locToTurnoverBoxIds = opExecLineCmd.getLocationToTurnoverBoxIds();
        Set<Long> turnoverBoxIds = locToTurnoverBoxIds.get(locationId);
        List<Long> locationIds = opExecLineCmd.getLocationIds();
        ReplenishmentScanResultComamnd  sRCmd = pdaReplenishmentPutawayCacheManager.tipTurnoverBox(turnoverBoxIds, operationId);
        if(sRCmd.getIsNeedScanTurnoverBox()) {  //当前库位对应的周转箱扫描完毕
            Long turnoverBoxId = sRCmd.getTurnoverBoxId();
            String containerCode = this.judeContainer(turnoverBoxId, ouId);
            command.setTipTurnoverBoxCode(containerCode);
            command.setIsNeedScanTurnoverBox(true);
        }else{//继续扫描下一个库位
            ReplenishmentScanResultComamnd  rishSRCmd =  pdaReplenishmentPutawayCacheManager.tipLocation(locationIds, operationId);
            if(rishSRCmd.getIsNeedScanLocation()) { //还有库位没有扫描，继续扫描库位
                Long locId = rishSRCmd.getLocationId();
                Location location = whLocationDao.findByIdExt(locId, ouId);
                if(null == location) {
                    throw new BusinessException(ErrorCodes.TIP_LOCATION_FAIL);
                }
                command.setTipLcoationBarCode(location.getBarCode());
                command.setTipLocationCode(location.getCode());
                command.setLocationId(location.getId()); 
                command.setIsNeedScanLocation(true);
            }else{ //库位已经扫描完毕
                command.setIsScanFinsh(true);
                whSkuInventoryManager.replenishmentPutaway(operationId, ouId, isTabbInvTotal, userId, workCode);
                //更新工作及作业状态
                this.updateStatus(operationId, workCode, ouId, userId);
                //清除所有缓存
                pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayRemoveAllCache(operationId);
            }
        }
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanTurnoverBox is end");
        return command;
    }

    @Override
    public void updateStatus(Long operationId, String workCode,Long ouId,Long userId) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl updateStatus is start");
        WhOperation whOperation = whOperationDao.findOperationByIdExt(operationId,ouId);
        if(null == whOperation) {
            log.error("whOperation id is not normal, operationId is:[{}]", operationId);
            throw new BusinessException(ErrorCodes.OPATION_NO_EXIST);
        }
        whOperation.setStatus(WorkStatus.FINISH);
        whOperation.setModifiedId(userId);
        whOperationDao.saveOrUpdateByVersion(whOperation);
        insertGlobalLog(GLOBAL_LOG_UPDATE, whOperation, ouId, userId, null, null);
        WhWorkCommand workCmd = whWorkDao.findWorkByWorkCode(workCode, ouId);
        if(null == workCmd) {
            log.error("whOperation id is not normal, operationId is:[{}]", operationId);
            throw new BusinessException(ErrorCodes.WORK_NO_EXIST);
        }
        WhWork work = new WhWork();
        BeanUtils.copyProperties(workCmd, work);
        work.setStatus(WorkStatus.FINISH);
        whWorkDao.saveOrUpdateByVersion(work);
        insertGlobalLog(GLOBAL_LOG_UPDATE, work, ouId, userId, null, null);
        log.info("PdaReplenishmentPutawayManagerImpl updateStatus is end");
        
    }
    
    /**
     * 统计分析作业执行明细
     * 
     * @author qiming.liu
     * @param ReplenishmentPutawayCommand
     * @return
     */
    @Override
    public OperationExecStatisticsCommand getOperationExecForGroup(ReplenishmentPutawayCommand replenishmentPutawayCommand) {
        
        // 所有托盘
        Set<Long> pallets = new HashSet<Long>();
        // 所有货箱
        Set<Long> containers = new HashSet<Long>();
        
        // 所有目标库位
        Set<Long> locationIds = new HashSet<Long>();
        // 目标库位对应的所有周转箱        
        Map<Long, Set<Long>> turnoverBoxIds = new HashMap<Long, Set<Long>>();
        // 周转箱对应的所有sku 
        Map<Long, Set<Long>> skuIds = new HashMap<Long, Set<Long>>();
        // 周转箱每个sku总件数
        Map<Long, Map<Long, Long>> skuQty = new HashMap<Long, Map<Long, Long>>();
        // 周转箱每个sku对应的唯一sku及件数
        Map<Long, Map<Long, Map<String, Long>>> skuAttrIds = new HashMap<Long, Map<Long, Map<String, Long>>>();
        // 周转箱每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>> skuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();
        // 目标库位对应的所有外部容器（整托整箱）
        Map<Long, Set<Long>> outerContainerIds = new HashMap<Long, Set<Long>>();
        // 外部容器对应所有内部容器（整托整箱）
        Map<Long, Set<Long>> outerToInside = new HashMap<Long, Set<Long>>();
        // 内部容器对应所有sku（整托整箱）
        Map<Long, Set<Long>> insideSkuIds = new HashMap<Long, Set<Long>>();
        // 内部容器每个sku总件数（整托整箱）
        Map<Long, Map<Long, Long>> insideSkuQty = new HashMap<Long, Map<Long, Long>>();
        // 内部容器每个sku对应的唯一sku及件数（整托整箱）
        Map<Long, Map<Long, Map<String, Long>>> insideSkuAttrIds = new HashMap<Long, Map<Long, Map<String, Long>>>();
        // 内部容器每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();
        
        // 根据作业id获取作业信息        
        WhOperationCommand whOperationCommand = whOperationManager.findOperationById(replenishmentPutawayCommand.getOperationId(), replenishmentPutawayCommand.getOuId());
        //根据作业id获取作业明细信息  
        List<WhOperationExecLine> operationExecLineList = whOperationExecLineDao.getOperationExecLine(replenishmentPutawayCommand.getOperationId(), replenishmentPutawayCommand.getOuId());
        
        for(WhOperationExecLine operationExecLine : operationExecLineList){
            // 临时set 
            Set<Long> temporaryL = new HashSet<Long>();
            Set<String> temporaryS = new HashSet<String>();
            Map<Long, Long> temporaryllMap = new HashMap<Long, Long>();
            Map<String, Set<String>> temporaryssetMap = new HashMap<String, Set<String>>();
            Map<Long, Map<String, Long>> temporarylmMap = new HashMap<Long, Map<String, Long>>();
            //获取内部容器唯一sku
            String onlySku = SkuCategoryProvider.getSkuAttrIdByOperationExecLine(operationExecLine);
            //根据工作明细id获取工作明细数据            
            WhWorkLine whWorkLine = whWorkLineDao.findById(operationExecLine.getWorkLineId());
            //根据库存UUID查找对应SN/残次信息
            List<WhSkuInventorySnCommand> skuInventorySnCommands = whSkuInventorySnDao.findWhSkuInventoryByUuid(whOperationCommand.getOuId(), whWorkLine.getUuid());
            //获取库位ID 
            locationIds.add(operationExecLine.getToLocationId());
            if(whOperationCommand.getIsWholeCase() == false){
                // 获取目标库位对应的所有周转箱
                if(null != turnoverBoxIds.get(operationExecLine.getToLocationId())){
                    turnoverBoxIds.get(operationExecLine.getToLocationId()).add(operationExecLine.getUseContainerId());
                }else{
                    temporaryL.add(operationExecLine.getUseContainerId());
                    turnoverBoxIds.put(operationExecLine.getToLocationId(), temporaryL);
                    temporaryL.clear();
                }
                // 获取周转箱对应的所有sku
                if(null != skuIds.get(operationExecLine.getUseContainerId())){
                    skuIds.get(operationExecLine.getUseContainerId()).add(operationExecLine.getSkuId());
                }else{
                    temporaryL.add(operationExecLine.getSkuId());
                    skuIds.put(operationExecLine.getUseContainerId(), temporaryL);
                    temporaryL.clear();
                }
                // 获取周转箱每个sku总件数
                if(null != skuQty.get(operationExecLine.getUseContainerId())){
                    temporaryllMap = skuQty.get(operationExecLine.getUseContainerId());
                    if(null != temporaryllMap.get(operationExecLine.getSkuId())){
                        Long qty =  temporaryllMap.get(operationExecLine.getSkuId()) + operationExecLine.getQty().longValue();
                        temporaryllMap.put(operationExecLine.getSkuId(), qty);
                    }else{
                        temporaryllMap.put(operationExecLine.getSkuId(), operationExecLine.getQty().longValue());
                    }
                    skuQty.put(operationExecLine.getUseContainerId(), temporaryllMap);
                    temporaryllMap.clear();
                }else{
                    temporaryllMap.put(operationExecLine.getSkuId(), operationExecLine.getQty().longValue());
                    skuQty.put(operationExecLine.getUseContainerId(), temporaryllMap);
                    temporaryllMap.clear();
                }
                // 周转箱每个sku对应的唯一sku及件数
                if(null != skuAttrIds.get(operationExecLine.getUseContainerId())){
                    temporarylmMap = skuAttrIds.get(operationExecLine.getUseContainerId());
                    if(null != temporarylmMap.get(operationExecLine.getSkuId())){
                        Map<String, Long> skuAttrIdsQty = temporarylmMap.get(operationExecLine.getSkuId());
                        if (null != skuAttrIdsQty.get(onlySku)) {
                            skuAttrIdsQty.put(onlySku, skuAttrIdsQty.get(onlySku) + operationExecLine.getQty().longValue());
                        } else {
                            skuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                        }
                        temporarylmMap.put(operationExecLine.getSkuId(), skuAttrIdsQty);
                     }else{
                         Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                         insideSkuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                         temporarylmMap.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                     }
                    skuAttrIds.put(operationExecLine.getUseContainerId(), temporarylmMap);
                    temporarylmMap.clear();
                }else{
                    Map<String, Long> skuAttrIdsQty = new HashMap<String, Long>();
                    skuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                    temporarylmMap.put(operationExecLine.getSkuId(), skuAttrIdsQty);
                    skuAttrIds.put(operationExecLine.getUseContainerId(), temporarylmMap);
                    temporarylmMap.clear();
                }
                // 周转箱每个唯一sku对应的所有sn及残次条码
                if(null != skuAttrIdsSnDefect.get(operationExecLine.getUseContainerId())){
                    temporaryssetMap = skuAttrIdsSnDefect.get(operationExecLine.getUseContainerId());
                    if(null != temporaryssetMap.get(onlySku)){
                        temporaryS = temporaryssetMap.get(onlySku);
                        for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                            temporaryS.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                        }
                        temporaryssetMap.put(onlySku, temporaryS);
                        temporaryS.clear();
                    }else{
                        for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                            temporaryS.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                        }
                        temporaryssetMap.put(onlySku, temporaryS);
                        temporaryS.clear();
                    }
                    skuAttrIdsSnDefect.put(operationExecLine.getUseContainerId(), temporaryssetMap);
                    temporaryssetMap.clear();
                }else{
                    for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                        temporaryS.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                    }
                    temporaryssetMap.put(onlySku, temporaryS);
                    temporaryS.clear();
                    skuAttrIdsSnDefect.put(operationExecLine.getUseContainerId(), temporaryssetMap);
                    temporaryssetMap.clear();
                }
            }else{
                // 所有托盘
                pallets.add(operationExecLine.getUseOuterContainerId());
                // 所有货箱
                containers.add(operationExecLine.getUseContainerId());
                // 目标库位对应的所有外部容器（整托整箱）
                if(null != outerContainerIds.get(operationExecLine.getToLocationId())){
                    outerContainerIds.get(operationExecLine.getToLocationId()).add(operationExecLine.getUseOuterContainerId());
                }else{
                    temporaryL.add(operationExecLine.getUseOuterContainerId());
                    outerContainerIds.put(operationExecLine.getToLocationId(), temporaryL);
                    temporaryL.clear();
                }
                // 外部容器对应所有内部容器（整托整箱）
                if(null != outerToInside.get(operationExecLine.getUseOuterContainerId())){
                    outerToInside.get(operationExecLine.getUseOuterContainerId()).add(operationExecLine.getUseContainerId());
                }else{
                    temporaryL.add(operationExecLine.getUseContainerId());
                    outerToInside.put(operationExecLine.getUseOuterContainerId(), temporaryL);
                    temporaryL.clear();
                }
                // 内部容器对应所有sku（整托整箱）
                if(null != insideSkuIds.get(operationExecLine.getUseContainerId())){
                    insideSkuIds.get(operationExecLine.getUseContainerId()).add(operationExecLine.getSkuId());
                }else{
                    temporaryL.add(operationExecLine.getSkuId());
                    insideSkuIds.put(operationExecLine.getUseContainerId(), temporaryL);
                    temporaryL.clear();
                }
                // 内部容器每个sku总件数（整托整箱）
                if(null != insideSkuQty.get(operationExecLine.getUseContainerId())){
                    temporaryllMap = insideSkuQty.get(operationExecLine.getUseContainerId());
                    if(null != temporaryllMap.get(operationExecLine.getSkuId())){
                        Long insQty =  temporaryllMap.get(operationExecLine.getSkuId()) + operationExecLine.getQty().longValue();
                        temporaryllMap.put(operationExecLine.getSkuId(), insQty);
                     }else{
                         temporaryllMap.put(operationExecLine.getSkuId(), operationExecLine.getQty().longValue());
                     }
                    insideSkuQty.put(operationExecLine.getUseContainerId(), temporaryllMap);
                    temporaryllMap.clear();
                }else{
                    temporaryllMap.put(operationExecLine.getSkuId(), operationExecLine.getQty().longValue());
                    insideSkuQty.put(operationExecLine.getUseContainerId(), temporaryllMap);
                    temporaryllMap.clear();
                }
                // 内部容器每个sku对应的唯一sku及件数（整托整箱）
                if(null != insideSkuAttrIds.get(operationExecLine.getUseContainerId())){
                    temporarylmMap = insideSkuAttrIds.get(operationExecLine.getUseContainerId());
                    if(null != temporarylmMap.get(operationExecLine.getSkuId())){
                        Map<String, Long> insideSkuAttrIdsQty = temporarylmMap.get(operationExecLine.getSkuId());
                        if (null != insideSkuAttrIdsQty.get(onlySku)) {
                            insideSkuAttrIdsQty.put(onlySku, insideSkuAttrIdsQty.get(onlySku) + operationExecLine.getQty().longValue());
                        } else {
                            insideSkuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                        }
                        temporarylmMap.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                     }else{
                         Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                         insideSkuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                         temporarylmMap.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                     }
                    insideSkuAttrIds.put(operationExecLine.getUseContainerId(), temporarylmMap);
                    temporarylmMap.clear();
                }else{
                    Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                    insideSkuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                    temporarylmMap.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                    insideSkuAttrIds.put(operationExecLine.getUseContainerId(), temporarylmMap);
                    temporarylmMap.clear();
                }
                // 内部容器每个唯一sku对应的所有sn及残次条码
                if(null != insideSkuAttrIdsSnDefect.get(operationExecLine.getUseContainerId())){
                    temporaryssetMap = insideSkuAttrIdsSnDefect.get(operationExecLine.getUseContainerId());
                    if(null != temporaryssetMap.get(onlySku)){
                        temporaryS = temporaryssetMap.get(onlySku);
                        for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                            temporaryS.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                        }
                        temporaryssetMap.put(onlySku, temporaryS);
                        temporaryS.clear();
                    }else{
                        for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                            temporaryS.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                        }
                        temporaryssetMap.put(onlySku, temporaryS);
                        temporaryS.clear();
                    }
                    insideSkuAttrIdsSnDefect.put(operationExecLine.getUseContainerId(), temporaryssetMap);
                    temporaryssetMap.clear();
                }else{
                    for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                        temporaryS.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                    }
                    temporaryssetMap.put(onlySku, temporaryS);
                    temporaryS.clear();
                    insideSkuAttrIdsSnDefect.put(operationExecLine.getUseContainerId(), temporaryssetMap);
                    temporaryssetMap.clear();
                }
            }
        }
        
        //载入统计分析信息        
        OperationExecStatisticsCommand statisticsCommand = new OperationExecStatisticsCommand();
        // 是否整托整箱
        statisticsCommand.setIsWholeCase(whOperationCommand.getIsWholeCase());
        // 所有托盘
        statisticsCommand.setPallets(pallets);
        // 所有货箱
        statisticsCommand.setContainers(containers);
        // 库位排序
        List<Long> sortLocationIds = new ArrayList<Long>();
        sortLocationIds = locationManager.sortByIds(locationIds, whOperationCommand.getOuId());
        // 所有目标库位
        statisticsCommand.setLocationIds(sortLocationIds);
        // 目标库位对应的所有周转箱
        statisticsCommand.setTurnoverBoxIds(turnoverBoxIds);
        // 周转箱对应的所有sku
        statisticsCommand.setSkuIds(insideSkuIds);
        // 周转箱每个sku总件数
        statisticsCommand.setSkuQty(skuQty);
        // 周转箱每个sku对应的唯一sku及件数
        statisticsCommand.setSkuAttrIds(insideSkuAttrIds);
        // 周转箱每个唯一sku对应的所有sn及残次条码
        statisticsCommand.setSkuAttrIdsSnDefect(skuAttrIdsSnDefect);
        // 目标库位对应的所有外部容器（整托整箱）
        statisticsCommand.setOuterContainerIds(outerContainerIds);
        // 外部容器对应所有内部容器（整托整箱）
        statisticsCommand.setOuterToInside(outerToInside);
        // 内部容器对应所有sku（整托整箱）
        statisticsCommand.setInsideSkuIds(insideSkuIds);
        // 内部容器每个sku总件数（整托整箱）
        statisticsCommand.setInsideSkuQty(insideSkuQty);
        // 内部容器每个sku对应的唯一sku及件数（整托整箱）
        statisticsCommand.setInsideSkuAttrIds(insideSkuAttrIds);
        // 内部容器每个唯一sku对应的所有sn及残次条码
        statisticsCommand.setInsideSkuAttrIdsSnDefect(insideSkuAttrIdsSnDefect);
        //缓存统计分析结果        
        pdaPickingWorkCacheManager.operationExecStatisticsRedis(whOperationCommand.getId(), statisticsCommand);
        
        return statisticsCommand;
    }
    
//    /**
//     * 校验库位库存
//     * 
//     * @author qiming.liu
//     * @param ReplenishmentPutawayCommand
//     * @return
//     */
//    public void checkStock() {
//        whSkuInventoryManager.replenishmentPutaway(operationId, ouId, isTabbInvTotal, userId, workCode);
//        //更新工作及作业状态
//        this.updateStatus(operationId, workCode, ouId, userId);
//        //清除所有缓存
//        pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayRemoveAllCache(operationId);
//    }
}
