package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.ArrayList;
import java.util.Date;
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
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionInventoryMoveDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.warehouse.LocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;


/***
 * 补货中的上架
 * @author Administrator
 *
 */
@Service("pdaInWarehouseMovePutawayManager")
@Transactional
public class PdaInWarehouseMovePutawayManagerImpl extends BaseManagerImpl implements PdaInWarehouseMovePutawayManager{

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
    private WhFunctionInventoryMoveDao whFunctionInventoryMoveDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    WhSkuInventoryTobefilledDao whSkuInventoryTobefilledDao;
    @Autowired
    private WhSkuInventoryLogManager whSkuInventoryLogManager;
    
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
            //根据库存UUID查找对应SN/残次信息
            List<WhSkuInventorySnCommand> skuInventorySnCommands = whSkuInventorySnDao.findWhSkuInventoryByUuid(whOperationCommand.getOuId(), operationExecLine.getUuid());
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
    
    @Override
    public ReplenishmentPutawayCommand putawayTipLocation(ReplenishmentPutawayCommand command) {
        log.info("PdaInWarehouseMovePutawayManagerImpl putawayTipLocation is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        OperationExecStatisticsCommand opExecLineCmd = cacheManager.getObject(CacheConstants.OPERATIONEXEC_STATISTICS + operationId.toString());
        if(null == opExecLineCmd){
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        List<Long> locationIds = opExecLineCmd.getLocationIds();
        if(null == locationIds){
            throw new BusinessException(ErrorCodes.TIP_LOCATION_FAIL);    
        }
        Long tipLocationId = locationIds.get(0);
        Location location = whLocationDao.findByIdExt(tipLocationId, ouId);
        if(null == location) {
            throw new BusinessException(ErrorCodes.TIP_LOCATION_FAIL);
        }
        command.setTipLocationBarCode(location.getBarCode());
        command.setTipLocationCode(location.getCode());
        command.setLocationId(location.getId());
        //TODO 判断是否需要扫描库位        
        command.setIsNeedScanLocation(true);
        log.info("PdaInWarehouseMovePutawayManagerImpl putawayTipLocation is end");
        return command;
    }

    @Override
    public ReplenishmentPutawayCommand putawayScanLocation(ReplenishmentPutawayCommand command,Boolean isTabbInvTotal) {
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
        command.setIsScanFinsh(true);
        Set<Long> insideContainerIds = new HashSet<Long>();
        Set<Long> outerContainerIds = new HashSet<Long>();
        if(false == opExecLineCmd.getIsWholeCase()){
            insideContainerIds = opExecLineCmd.getTurnoverBoxIds().get(locationId);
        }else{
            if(null != opExecLineCmd.getPallets()){
                outerContainerIds = opExecLineCmd.getPallets();    
            }else{
                insideContainerIds = opExecLineCmd.getContainers();    
            }
        }
        if(0 != outerContainerIds.size()){
            for(Long outerContainerId : outerContainerIds){
                this.replenishmentPutaway(locationId,operationId, ouId, isTabbInvTotal, userId, workCode, outerContainerId, null);    
            }
        }else{
            for(Long insideContainerId : insideContainerIds){
                this.replenishmentPutaway(locationId,operationId, ouId, isTabbInvTotal, userId, workCode, null, insideContainerId);    
            }    
        }
        //更新工作及作业状态
        this.updateStatus(operationId, workCode, ouId, userId);
        cacheManager.remove(CacheConstants.OPERATIONEXEC_STATISTICS+operationId.toString());
        log.info("PdaReplenishmentPutawayManagerImpl putawayScanTurnoverBox is end");
        return command;
    }
    
    public void replenishmentPutaway(Long locationId, Long operationId, Long ouId, Boolean isTabbInvTotal, Long userId, String workCode, Long outerContainerId, Long insideContainerId) {
        List<WhSkuInventoryCommand> invList = new ArrayList<WhSkuInventoryCommand>();  
        if(null != outerContainerId){
            invList = whSkuInventoryDao.getWhSkuInventoryCommandByOuterContainerId(ouId, outerContainerId);    
        }else{
            invList = whSkuInventoryDao.getWhSkuInventoryCommandByWave(ouId, insideContainerId);
        }
        
        if (null == invList || 0 == invList.size()) {
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {});
        }
        boolean isTV = true;// 是否跟踪容器
        boolean isBM = true;// 是否批次管理
        boolean isVM = true;// 是否管理效期
        Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器id
        Location loc = whLocationDao.findByIdExt(locationId, ouId);
        if (null == loc) {
               log.error("location is null error, id is:[{}], logId is:[{}]", locationId, logId);
               throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
       }
       isTV = (null == loc.getIsTrackVessel() ? false : loc.getIsTrackVessel());
       isBM = (null == loc.getIsBatchMgt() ? false : loc.getIsBatchMgt());
       isVM = (null == loc.getIsValidMgt() ? false : loc.getIsValidMgt());
       // 2.执行上架(一入一出)
       for (WhSkuInventoryCommand invCmd : invList) {
                List<WhSkuInventorySnCommand> snList = invCmd.getWhSkuInventorySnCommandList();
                String uuid = "";
                if (null == snList || 0 == snList.size()) {
                     WhSkuInventory inv = new WhSkuInventory();
                     BeanUtils.copyProperties(invCmd, inv);
                     inv.setId(null);
                     inv.setOnHandQty(invCmd.getOnHandQty());// 在库库存
                     if (false == isTV) {  //是否跟踪容器号
                          inv.setOuterContainerId(null);
                          inv.setInsideContainerId(null);
                     }else{
                         if(null != outerContainerId){
                             inv.setOuterContainerId(outerContainerId);
                             inv.setInsideContainerId(invCmd.getInsideContainerId());
                         }else{
                             inv.setInsideContainerId(insideContainerId);    
                         }
                     } 
                     if (false == isBM) {
                           inv.setBatchNumber(null);
                     }
                     if (false == isVM) {
                           inv.setMfgDate(null);
                           inv.setExpDate(null);
                     }
                     try {
                         uuid = SkuInventoryUuid.invUuid(inv);
                         inv.setUuid(uuid);// UUID
                     } catch (Exception e) {
                         log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                         throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                     }
                     Double oldQty = 0.0;
                     if (true == isTabbInvTotal) {
                         try {
                                oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                         } catch (Exception e) {
                                log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                         }
                     } else {
                         oldQty = 0.0;
                     }
                     inv.setInboundTime(new Date());
                     inv.setLastModifyTime(new Date());
                     inv.setFrozenQty(0.0);
                     whSkuInventoryDao.insert(inv);
                     insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                     // 记录入库库存日志(这个实现的有问题)
                     insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.REPLENISHMENT);
                     whSkuInventoryTobefilledDao.deleteByExt(invCmd.getId(), ouId);  //删除当前待移入库存
                 } else {
                     WhSkuInventory inv = new WhSkuInventory();
                     BeanUtils.copyProperties(invCmd, inv);
                     inv.setId(null);
                     inv.setOnHandQty(invCmd.getOnHandQty());// 在库库存
                     inv.setFrozenQty(0.0);
                     if (false == isTV) {
                         inv.setOuterContainerId(null);
                         inv.setInsideContainerId(null);
                     }else{
                         if(null != outerContainerId){
                             inv.setOuterContainerId(outerContainerId);
                             inv.setInsideContainerId(invCmd.getInsideContainerId());
                         }else{
                             inv.setInsideContainerId(insideContainerId);    
                         }
                     } 
                     if (false == isBM) {
                           inv.setBatchNumber(null);
                     }
                     if (false == isVM) {
                           inv.setMfgDate(null);
                           inv.setExpDate(null);
                     }
                     Long icId = invCmd.getInsideContainerId();
                     if (null != icId) {
                           insideContainerIds.add(icId);
                     }
                     try {
                           uuid = SkuInventoryUuid.invUuid(inv);
                           inv.setUuid(uuid);// UUID
                     } catch (Exception e) {
                           log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                           throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                     }
                     Double oldQty = 0.0;
                     if (true == isTabbInvTotal) {
                           try {
                                   oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                           } catch (Exception e) {
                                   log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                   throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                           }
                     } else {
                               oldQty = 0.0;
                     }
                     inv.setInboundTime(new Date());
                     inv.setLastModifyTime(new Date());
                     inv.setFrozenQty(0.0);
                     whSkuInventoryDao.insert(inv);
                     insertGlobalLog(GLOBAL_LOG_INSERT, inv, ouId, userId, null, null);
                     // 记录入库库存日志(这个实现的有问题)
                     insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.REPLENISHMENT);
                     // 插入sn
                     for (WhSkuInventorySnCommand snCmd : snList) {
                               WhSkuInventorySn sn = new WhSkuInventorySn();
                               BeanUtils.copyProperties(snCmd, sn);
                               sn.setId(null);
                               sn.setUuid(inv.getUuid());
                               whSkuInventorySnDao.insert(sn);
                               insertGlobalLog(GLOBAL_LOG_INSERT, sn, ouId, userId, null, null);
                      }
                      // 记录SN日志(这个实现的有问题)
                       insertSkuInventorySnLog(inv.getUuid(), ouId);
                     }
                     whSkuInventoryTobefilledDao.deleteByExt(invCmd.getId(), ouId);  //删除当前待移入库存
                     if(isTV) {
                         //如果库位跟踪容器号,修改容器状态
                         if(null != outerContainerId) {  //修改托盘
                            Container container =  containerDao.findByIdExt(invCmd.getInsideContainerId(), ouId);
                            if(null == container) {
                                throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_EXISTS );
                            }
                            container.setLifecycle(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                            container.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                            containerDao.saveOrUpdateByVersion(container);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                         }
                     } 
           }
           if(isTV) {
               //如果库位跟踪容器号,修改容器状态
               if(null == outerContainerId && null != insideContainerId) {  //修改内部容器
                      Container container =  containerDao.findByIdExt(insideContainerId, ouId);
                      if(null == container) {
                               throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_EXISTS );
                      }
                      container.setLifecycle(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                      container.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                      containerDao.saveOrUpdateByVersion(container);
                      insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
               }else if(null != outerContainerId) {  //修改外部容器
                   Container container =  containerDao.findByIdExt(outerContainerId, ouId);
                   if(null == container) {
                       throw new BusinessException(ErrorCodes.COMMON_OUTER_CONTAINER_IS_NOT_EXISTS );
                   }
                   container.setLifecycle(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                   container.setStatus(ContainerStatus.CONTAINER_STATUS_SHEVLED);
                   containerDao.saveOrUpdateByVersion(container);
                   insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
           }
           //删除库位库存表中的容器库存
           //1.根据周转箱id,查询容器库存记录
           List<WhSkuInventoryCommand> skuInvCmdList = new ArrayList<WhSkuInventoryCommand>();  
           if(null != outerContainerId){
               skuInvCmdList = whSkuInventoryDao.findContainerOnHandInventoryByOuterContainerId(ouId, outerContainerId);
           }else{
               skuInvCmdList = whSkuInventoryDao.findContainerOnHandInventoryByInsideContainerId(ouId, insideContainerId);
           }
           //循环删除容器库存记录
           for (WhSkuInventoryCommand invCmd : skuInvCmdList) {
               String uuid = invCmd.getUuid();
               Double oldQty = 0.0;
               if (true == isTabbInvTotal) {
                     try {
                             oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                     } catch (Exception e) {
                             log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                             throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                     }
               } else {
                         oldQty = 0.0;
               }
               insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId,InvTransactionType.REPLENISHMENT);
               whSkuInventoryDao.deleteWhSkuInventoryById(invCmd.getId(), ouId);
           }
    }
    
    public void updateStatus(Long operationId, String workCode,Long ouId,Long userId) {
        log.info("PdaInWarehouseMovePutawayManagerImpl updateStatus is start");
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
        log.info("PdaInWarehouseMovePutawayManagerImpl updateStatus is end");
        
    }
    
}
