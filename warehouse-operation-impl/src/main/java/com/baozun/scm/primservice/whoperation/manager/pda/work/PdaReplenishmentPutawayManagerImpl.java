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
import com.baozun.scm.primservice.whoperation.command.pda.work.OperationExecStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentPutawayCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.ReplenishmentScanResultComamnd;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CancelPattern;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionReplenishmentDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
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
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionReplenishment;
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
@Service("pdaReplenishmentPutawayManager")
@Transactional
public class PdaReplenishmentPutawayManagerImpl extends BaseManagerImpl implements PdaReplenishmentPutawayManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaReplenishmentPutawayManagerImpl.class);
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
    @Autowired
    private WhOperationLineDao whOperationLineDao;
    @Autowired
    private WhFunctionReplenishmentDao whFunctionReplenishmentDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    
    
    @Override
    public ReplenishmentPutawayCommand putawayTipLocation(ReplenishmentPutawayCommand command) {
        // TODO Auto-generated method stub
        log.info("PdaReplenishmentPutawayManagerImpl putawayTipLocation is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        List<Long> locationIds = opExecLineCmd.getLocationIds();
        Long tipLocationId = null;
        for(Long locationId:locationIds) {
            if(null != locationId) {
                tipLocationId = locationId;
            }
        }
        Location location = whLocationDao.findByIdExt(tipLocationId, ouId);
        if(null == location) {
            throw new BusinessException(ErrorCodes.TIP_LOCATION_FAIL);
        }
        command.setTipLocationBarCode(location.getBarCode());
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
        OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> locToTurnoverBoxIds = opExecLineCmd.getTurnoverBoxIds();
        Set<Long> turnoverBoxIds = locToTurnoverBoxIds.get(locationId);
        if(null == turnoverBoxIds || turnoverBoxIds.size() == 0){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
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
        Long functionId = command.getFunctionId();
        Long userId = command.getUserId();
        String workCode = command.getWorkBarCode();
        OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> locToTurnoverBoxIds = opExecLineCmd.getTurnoverBoxIds();   //所有目标库位对应的周黄钻想
        Set<Long> turnoverBoxIds = locToTurnoverBoxIds.get(locationId);
        String turnoverBoxCode = command.getTurnoverBoxCode();
        ContainerCommand cmd = containerDao.getContainerByCode(turnoverBoxCode, ouId);
        if(null == cmd) {
            throw new BusinessException(ErrorCodes.TIP_CONTAINER_FAIL);
        }
        Long turnoverBoxId = cmd.getId();
        //缓存上一个周转箱
        pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayCacheTurnoverBox(operationId, turnoverBoxId);
        ReplenishmentScanResultComamnd  sRCmd = pdaReplenishmentPutawayCacheManager.tipTurnoverBox(turnoverBoxIds, operationId);
        if(sRCmd.getIsNeedScanTurnoverBox()) {  //当前库位对应的周转箱扫描完毕
            Long tipTurnoverBoxId = sRCmd.getTurnoverBoxId();
            String containerCode = this.judeContainer(tipTurnoverBoxId, ouId);
            command.setTipTurnoverBoxCode(containerCode);
            command.setIsNeedScanTurnoverBox(true);
            //当前周转箱上架
            whSkuInventoryManager.replenishmentPutaway(locationId,operationId, ouId, isTabbInvTotal, userId, workCode,turnoverBoxId);
        }else{//继续扫描下一个库位
             command.setIsScanFinsh(true);
             whSkuInventoryManager.replenishmentPutaway(locationId,operationId, ouId, isTabbInvTotal, userId, workCode,turnoverBoxId);
             //判断当前补货库位有没有拣货工作
             //更新工作及作业状态
             this.updateStatus(operationId, workCode, ouId, userId);
             //清除所有缓存
             pdaReplenishmentPutawayCacheManager.pdaReplenishPutwayRemoveAllCache(operationId);
        }
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanTurnoverBox is end");
        return command;
    }

    
    private void judeLocationIsPicking(Long turnoverBoxId,Long locationId,Long ouId,Long userId){
        //判断目标库位上是否有拣货工作
            //更新到工作明细
            List<WhWorkLineCommand> workLineList = whWorkLineDao.findWorkLineByLocationId(locationId, ouId);
            for(WhWorkLineCommand workLineCmd:workLineList) {
                     Long odoLineId = workLineCmd.getOdoLineId();
                     Long odoId = workLineCmd.getOdoId();
                     String workSkuAttrId = SkuCategoryProvider.getSkuAttrIdByWhWorkLineCommand(workLineCmd);
                     List<WhSkuInventoryCommand> skuInvCmdList = whSkuInventoryDao.findReplenishmentBylocationId(turnoverBoxId,ouId, locationId, odoLineId, odoId);
                     for(WhSkuInventoryCommand invCmd:skuInvCmdList) {
                            Long outerId = invCmd.getOuterContainerId();
                            Long insideId = invCmd.getInsideContainerId();
                            String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
                            if(workSkuAttrId.equals(skuAttrId)) {
                                  if(null != outerId && null != insideId) {
                                      WhWorkLine workLine = new WhWorkLine();
                                      BeanUtils.copyProperties(workLineCmd, workLine);
                                      workLine.setFromInsideContainerId(insideId);
                                      workLine.setFromOuterContainerId(outerId);
                                      whWorkLineDao.saveOrUpdateByVersion(workLine);
                                      insertGlobalLog(GLOBAL_LOG_UPDATE, workLine, ouId, userId, null, null);
                                  }
                                  if(null == outerId && null != insideId){
                                      WhWorkLine workLine = new WhWorkLine();
                                      BeanUtils.copyProperties(workLineCmd, workLine);
                                      workLine.setFromInsideContainerId(insideId);
                                      whWorkLineDao.saveOrUpdateByVersion(workLine);
                                      insertGlobalLog(GLOBAL_LOG_UPDATE, workLine, ouId, userId, null, null);
                                  }
                            }
                     }
            }
            //更新到作业明细
            List<WhOperationLineCommand> operLineCmdList = whOperationLineDao.findOperationLineByLocationId(ouId, locationId);
            if(null != operLineCmdList && operLineCmdList.size() != 0) {
                //库位上有拣货工作
                for(WhOperationLineCommand operLineCmd:operLineCmdList){
                    Long odoLineId = operLineCmd.getOdoLineId();
                    Long odoId = operLineCmd.getOdoId();
                    String workSkuAttrId = SkuCategoryProvider.getSkuAttrIdByOperationLine(operLineCmd);
                    List<WhSkuInventoryCommand> skuInvCmdList = whSkuInventoryDao.findReplenishmentBylocationId(turnoverBoxId,ouId, locationId, odoLineId, odoId);
                    for(WhSkuInventoryCommand invCmd:skuInvCmdList) {
                           Long outerId = invCmd.getOuterContainerId();
                           Long insideId = invCmd.getInsideContainerId();
                           String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
                           if(workSkuAttrId.equals(skuAttrId)) {
                                 if(null != outerId && null != insideId) {
                                     WhOperationLine opLine = new WhOperationLine();
                                     BeanUtils.copyProperties(operLineCmd, opLine);
                                     opLine.setFromOuterContainerId(outerId);
                                     opLine.setFromInsideContainerId(insideId);
                                     whOperationLineDao.saveOrUpdateByVersion(opLine);
                                     insertGlobalLog(GLOBAL_LOG_UPDATE, opLine, ouId, userId, null, null);
                                 }
                                 if(null == outerId && null != insideId){
                                     WhOperationLine opLine = new WhOperationLine();
                                     BeanUtils.copyProperties(operLineCmd, opLine);
                                     opLine.setFromInsideContainerId(insideId);
                                     whOperationLineDao.saveOrUpdateByVersion(opLine);
                                     insertGlobalLog(GLOBAL_LOG_UPDATE, opLine, ouId, userId, null, null);
                                 }
                           }
                    }
                }
            }
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
        Map<String, Set<Long>> skuIds = new HashMap<String, Set<Long>>();
        // 周转箱每个sku总件数
        Map<String, Map<Long, Long>> skuQty = new HashMap<String, Map<Long, Long>>();
        // 周转箱每个sku对应的唯一sku及件数
        Map<String, Map<Long, Map<String, Long>>> skuAttrIds = new HashMap<String, Map<Long, Map<String, Long>>>();
        // 周转箱每个唯一sku对应的所有sn及残次条码
        Map<String, Map<String, Set<String>>> skuAttrIdsSnDefect = new HashMap<String, Map<String, Set<String>>>();
        // 目标库位对应的所有外部容器（整托整箱）
        Map<Long, Set<Long>> outerContainerIds = new HashMap<Long, Set<Long>>();
        // 外部容器对应所有内部容器（整托整箱）
        Map<Long, Set<Long>> outerToInside = new HashMap<Long, Set<Long>>();
        // 内部容器对应所有sku（整托整箱）
        Map<String, Set<Long>> insideSkuIds = new HashMap<String, Set<Long>>();
        // 内部容器每个sku总件数（整托整箱）
        Map<String, Map<Long, Long>> insideSkuQty = new HashMap<String, Map<Long, Long>>();
        // 内部容器每个sku对应的唯一sku及件数（整托整箱）
        Map<String, Map<Long, Map<String, Long>>> insideSkuAttrIds = new HashMap<String, Map<Long, Map<String, Long>>>();
        // 内部容器每个唯一sku对应的所有sn及残次条码
        Map<String, Map<String, Set<String>>> insideSkuAttrIdsSnDefect = new HashMap<String, Map<String, Set<String>>>();
        
        // 根据作业id获取作业信息        
        WhOperationCommand whOperationCommand = whOperationManager.findOperationById(replenishmentPutawayCommand.getOperationId(), replenishmentPutawayCommand.getOuId());
        //根据作业id获取作业明细信息  
        List<WhOperationExecLine> operationExecLineList = whOperationExecLineDao.getOperationExecLineLst(replenishmentPutawayCommand.getOperationId(), replenishmentPutawayCommand.getOuId(), false);
        for(WhOperationExecLine operationExecLine : operationExecLineList){
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
                    Set<Long> useContainerIdSet = new HashSet<Long>();
                    useContainerIdSet.add(operationExecLine.getUseContainerId());
                    turnoverBoxIds.put(operationExecLine.getToLocationId(), useContainerIdSet);
                }
                String toLocationAndUseContainer = operationExecLine.getToLocationId().toString() + operationExecLine.getUseContainerId().toString();
                // 获取周转箱对应的所有sku
                if(null != skuIds.get(operationExecLine.getUseContainerId())){
                    skuIds.get(operationExecLine.getUseContainerId()).add(operationExecLine.getSkuId());
                }else{
                    Set<Long> skuIdSet = new HashSet<Long>();
                    skuIdSet.add(operationExecLine.getSkuId());
                    skuIds.put(toLocationAndUseContainer, skuIdSet);
                }
                // 获取周转箱每个sku总件数
                if(null != skuQty.get(operationExecLine.getUseContainerId())){
                    Map<Long, Long> useContainerIdAndSkuQtyMap = new HashMap<Long, Long>();
                    useContainerIdAndSkuQtyMap = skuQty.get(operationExecLine.getUseContainerId());
                    if(null != useContainerIdAndSkuQtyMap.get(operationExecLine.getSkuId())){
                        Long qty =  useContainerIdAndSkuQtyMap.get(operationExecLine.getSkuId()) + operationExecLine.getQty().longValue();
                        useContainerIdAndSkuQtyMap.put(operationExecLine.getSkuId(), qty);
                    }else{
                        useContainerIdAndSkuQtyMap.put(operationExecLine.getSkuId(), operationExecLine.getQty().longValue());
                    }
                    skuQty.put(toLocationAndUseContainer, useContainerIdAndSkuQtyMap);
                }else{
                    Map<Long, Long> useContainerIdAndSkuQtyMap = new HashMap<Long, Long>();
                    useContainerIdAndSkuQtyMap.put(operationExecLine.getSkuId(), operationExecLine.getQty().longValue());
                    skuQty.put(toLocationAndUseContainer, useContainerIdAndSkuQtyMap);
                }
                // 周转箱每个sku对应的唯一sku及件数
                if(null != skuAttrIds.get(operationExecLine.getUseContainerId())){
                    Map<Long, Map<String, Long>> useContainerIdAndOnlySku  = new HashMap<Long, Map<String, Long>>();
                    useContainerIdAndOnlySku = skuAttrIds.get(operationExecLine.getUseContainerId());
                    if(null != useContainerIdAndOnlySku.get(operationExecLine.getSkuId())){
                        Map<String, Long> skuAttrIdsQty = useContainerIdAndOnlySku.get(operationExecLine.getSkuId());
                        if (null != skuAttrIdsQty.get(onlySku)) {
                            skuAttrIdsQty.put(onlySku, skuAttrIdsQty.get(onlySku) + operationExecLine.getQty().longValue());
                        } else {
                            skuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                        }
                        useContainerIdAndOnlySku.put(operationExecLine.getSkuId(), skuAttrIdsQty);
                     }else{
                         Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                         insideSkuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                         useContainerIdAndOnlySku.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                     }
                    skuAttrIds.put(toLocationAndUseContainer, useContainerIdAndOnlySku);
                }else{
                    Map<Long, Map<String, Long>> useContainerIdAndOnlySku  = new HashMap<Long, Map<String, Long>>();
                    Map<String, Long> skuAttrIdsQty = new HashMap<String, Long>();
                    skuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                    useContainerIdAndOnlySku.put(operationExecLine.getSkuId(), skuAttrIdsQty);
                    skuAttrIds.put(toLocationAndUseContainer, useContainerIdAndOnlySku);
                }
                // 周转箱每个唯一sku对应的所有sn及残次条码
                if(null != skuAttrIdsSnDefect.get(operationExecLine.getUseContainerId())){
                    Map<String, Set<String>> onlySkuAndSn = new HashMap<String, Set<String>>();
                    Set<String> snSet = new HashSet<String>();
                    onlySkuAndSn = skuAttrIdsSnDefect.get(operationExecLine.getUseContainerId());
                    if(null != onlySkuAndSn.get(onlySku)){
                        snSet = onlySkuAndSn.get(onlySku);
                        for (int i = 0; i < operationExecLine.getQty(); i++) {
                            if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                                snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                            }
                        }
                        onlySkuAndSn.put(onlySku, snSet);
                    }else{
                        for (int i = 0; i < operationExecLine.getQty(); i++) {
                            if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                                snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                            }
                        }
                        onlySkuAndSn.put(onlySku, snSet);
                    }
                    skuAttrIdsSnDefect.put(toLocationAndUseContainer, onlySkuAndSn);
                }else{
                    Map<String, Set<String>> onlySkuAndSn = new HashMap<String, Set<String>>();
                    Set<String> snSet = new HashSet<String>();
                    for (int i = 0; i < operationExecLine.getQty(); i++) {
                        if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                            snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                        }
                    }
                    onlySkuAndSn.put(onlySku, snSet);
                    skuAttrIdsSnDefect.put(toLocationAndUseContainer, onlySkuAndSn);
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
                    Set<Long> useOuterContainerIdSet = new HashSet<Long>();
                    useOuterContainerIdSet.add(operationExecLine.getUseOuterContainerId());
                    outerContainerIds.put(operationExecLine.getToLocationId(), useOuterContainerIdSet);
                }
                // 外部容器对应所有内部容器（整托整箱）
                if(null != outerToInside.get(operationExecLine.getUseOuterContainerId())){
                    outerToInside.get(operationExecLine.getUseOuterContainerId()).add(operationExecLine.getUseContainerId());
                }else{
                    Set<Long> useContainerIdSet = new HashSet<Long>();
                    useContainerIdSet.add(operationExecLine.getUseContainerId());
                    outerToInside.put(operationExecLine.getUseOuterContainerId(), useContainerIdSet);
                }
                String toLocationAndUseContainer = operationExecLine.getToLocationId().toString() + operationExecLine.getUseContainerId().toString();
                // 内部容器对应所有sku（整托整箱）
                if(null != insideSkuIds.get(operationExecLine.getUseContainerId())){
                    insideSkuIds.get(operationExecLine.getUseContainerId()).add(operationExecLine.getSkuId());
                }else{
                    Set<Long> skuIdSet = new HashSet<Long>();
                    skuIdSet.add(operationExecLine.getSkuId());
                    insideSkuIds.put(toLocationAndUseContainer, skuIdSet);
                }
                // 内部容器每个sku总件数（整托整箱）
                if(null != insideSkuQty.get(operationExecLine.getUseContainerId())){
                    Map<Long, Long> skuIdAndQtyMap = new HashMap<Long, Long>();
                    skuIdAndQtyMap = insideSkuQty.get(operationExecLine.getUseContainerId());
                    if(null != skuIdAndQtyMap.get(operationExecLine.getSkuId())){
                        Long insQty =  skuIdAndQtyMap.get(operationExecLine.getSkuId()) + operationExecLine.getQty().longValue();
                        skuIdAndQtyMap.put(operationExecLine.getSkuId(), insQty);
                     }else{
                         skuIdAndQtyMap.put(operationExecLine.getSkuId(), operationExecLine.getQty().longValue());
                     }
                    insideSkuQty.put(toLocationAndUseContainer, skuIdAndQtyMap);
                }else{
                    Map<Long, Long> skuIdAndQtyMap = new HashMap<Long, Long>();
                    skuIdAndQtyMap.put(operationExecLine.getSkuId(), operationExecLine.getQty().longValue());
                    insideSkuQty.put(toLocationAndUseContainer, skuIdAndQtyMap);
                }
                // 内部容器每个sku对应的唯一sku及件数（整托整箱）
                if(null != insideSkuAttrIds.get(operationExecLine.getUseContainerId())){
                    Map<Long, Map<String, Long>> skuIdAndOnlySku = new HashMap<Long, Map<String, Long>>();
                    skuIdAndOnlySku = insideSkuAttrIds.get(operationExecLine.getUseContainerId());
                    if(null != skuIdAndOnlySku.get(operationExecLine.getSkuId())){
                        Map<String, Long> insideSkuAttrIdsQty = skuIdAndOnlySku.get(operationExecLine.getSkuId());
                        if (null != insideSkuAttrIdsQty.get(onlySku)) {
                            insideSkuAttrIdsQty.put(onlySku, insideSkuAttrIdsQty.get(onlySku) + operationExecLine.getQty().longValue());
                        } else {
                            insideSkuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                        }
                        skuIdAndOnlySku.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                     }else{
                         Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                         insideSkuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                         skuIdAndOnlySku.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                     }
                    insideSkuAttrIds.put(toLocationAndUseContainer, skuIdAndOnlySku);
                }else{
                    Map<Long, Map<String, Long>> skuIdAndOnlySku = new HashMap<Long, Map<String, Long>>();
                    Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                    insideSkuAttrIdsQty.put(onlySku, operationExecLine.getQty().longValue());
                    skuIdAndOnlySku.put(operationExecLine.getSkuId(), insideSkuAttrIdsQty);
                    insideSkuAttrIds.put(toLocationAndUseContainer, skuIdAndOnlySku);
                }
                // 内部容器每个唯一sku对应的所有sn及残次条码
                if(null != insideSkuAttrIdsSnDefect.get(operationExecLine.getUseContainerId())){
                    Map<String, Set<String>> onlySkuAndSnMap = new HashMap<String, Set<String>>();
                    onlySkuAndSnMap = insideSkuAttrIdsSnDefect.get(operationExecLine.getUseContainerId());
                    if(null != onlySkuAndSnMap.get(onlySku)){
                        Set<String> snSet = new HashSet<String>();
                        snSet = onlySkuAndSnMap.get(onlySku);
                        for (int i = 0; i < operationExecLine.getQty(); i++) {
                            if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                                snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                            }
                        }
                        onlySkuAndSnMap.put(onlySku, snSet);
                    }else{
                        Set<String> snSet = new HashSet<String>();
                        for (int i = 0; i < operationExecLine.getQty(); i++) {
                            if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                                snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                            }
                        }
                        onlySkuAndSnMap.put(onlySku, snSet);
                    }
                    insideSkuAttrIdsSnDefect.put(toLocationAndUseContainer, onlySkuAndSnMap);
                }else{
                    Map<String, Set<String>> onlySkuAndSnMap = new HashMap<String, Set<String>>();
                    Set<String> snSet = new HashSet<String>();
                    for (int i = 0; i < operationExecLine.getQty(); i++) {
                        if(null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)){
                            snSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));    
                        }
                    }
                    onlySkuAndSnMap.put(onlySku, snSet);
                    insideSkuAttrIdsSnDefect.put(toLocationAndUseContainer, onlySkuAndSnMap);
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
        statisticsCommand.setSkuIds(skuIds);
        // 周转箱每个sku总件数
        statisticsCommand.setSkuQty(skuQty);
        // 周转箱每个sku对应的唯一sku及件数
        statisticsCommand.setSkuAttrIds(skuAttrIds);
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
    
    /**
     * 获取补货功能参数
     * @param ouId
     * @param functionId
     * @return
     */
    public WhFunctionReplenishment findWhFunctionReplenishmentByfunctionId(Long ouId,Long functionId){
        WhFunctionReplenishment replenish = whFunctionReplenishmentDao.findByFunctionIdExt(ouId, functionId);
        if(null == replenish) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        return replenish;
    }
    
    /***
     * 补货上架取消
     * @param operationId
     */
    public void cancelPattern(Long operationId,Integer cancelPattern){
        if(CancelPattern.PICKING_SCAN_LOC_CANCEL == cancelPattern){
            cacheManager.remove(CacheConstants.CACHE_PUTAWAY_LOCATION+operationId.toString());
        }else if(CancelPattern.PICKING_TIP_CAR_CANCEL == cancelPattern){
            cacheManager.remove(CacheConstants.OPERATIONEXEC_STATISTICS+operationId.toString());
        }
    }
}
