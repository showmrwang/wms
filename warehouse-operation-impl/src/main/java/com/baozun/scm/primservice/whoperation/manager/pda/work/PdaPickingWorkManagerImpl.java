package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperationLineCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionPickingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkOperDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.TipSkuDetailProvider;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.LocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPicking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkOper;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.utilities.type.StringUtil;

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
    private CacheManager cacheManager;    
    @Autowired
    private WhLocationDao whLocationDao;
    @Autowired
    private WhOperationManager whOperationManager;
    @Autowired
    private WhOperationLineManager whOperationLineManager;
    @Autowired
    private WhWorkOperDao whWorkOperDao;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private OutBoundBoxTypeDao outBoundBoxTypeDao;
    @Autowired
    private WhFunctionPickingDao whFunctionPickingDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private LocationManager locationManager;
    @Autowired
    private WhSkuDao whSkuDao;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private WhOperationExecLineDao whOperationExecLineDao;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    

    /**
     * 保存工作操作员信息
     * 
     * @author qiming.liu
     * @param workId
     * @param ouId
     * @param userId
     * 
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveWorkOper(Long workId, Long ouId, Long userId) {
        //根据工作Id和ouId获取作业信息        
        WhOperationCommand WhOperationCommand = whOperationManager.findOperationByWorkId(workId, ouId);
        
        WhWorkOper whWorkOper = new WhWorkOper();
        //操作员ID        
        whWorkOper.setOperUserId(userId);
        //工作ID
        whWorkOper.setOuId(ouId);
        //仓库组织ID
        whWorkOper.setWorkId(workId);
        //作业ID
        whWorkOper.setOperationId(WhOperationCommand.getId());
        //状态
        whWorkOper.setStatus(WorkStatus.NEW);
        //是否管理员指派
        whWorkOper.setIsAdminAssign(false);
        //创建时间
        whWorkOper.setCreateTime(new Date());
        //最后操作时间
        whWorkOper.setLastModifyTime(new Date());
        //创建人ID
        whWorkOper.setCreatedId(userId);
        //修改人ID
        whWorkOper.setModifiedId(userId);
        //操作人ID
        whWorkOper.setOperationId(userId);
        
        whWorkOperDao.insert(whWorkOper);
    }

    /**
     * 统计分析工作及明细并缓存
     * 
     * @author qiming.liu
     * @param whWork
     * @param ouId
     * @return
     */
    @Override
    public void getOperatioLineForGroup(WhOperationCommand whOperationCommand) {
        // 所有小车
        Set<Long> outerContainers = new HashSet<Long>();
        // 所有出库箱
        Set<String> outbounxBoxs = new HashSet<String>();
        // 小车货格与出库箱对应关系
        Map<Integer, String> carStockToOutgoingBox = new HashMap<Integer, String>();
        // 所有周转箱
        Set<Long> turnoverBoxs = new HashSet<Long>();
        // 所有托盘
        Set<Long> pallets = new HashSet<Long>();
        // 所有货箱
        Set<Long> containers = new HashSet<Long>();
        // 所有库位
        Set<Long> locationIds = new HashSet<Long>();
        // 库位上所有外部容器
        Map<Long, Set<Long>> outerContainerIds = new HashMap<Long, Set<Long>>();
        // 库位上所有内部容器（无外部容器情况）
        Map<Long, Set<Long>> insideContainerIds = new HashMap<Long, Set<Long>>();
        // 库位上所有sku（sku不在任何容器内）
        Map<Long, Set<Long>> skuIds = new HashMap<Long, Set<Long>>();
        // 库位上每个sku总件数
        Map<Long, Map<Long, Long>> skuQty = new HashMap<Long, Map<Long, Long>>();
        // 库位上每个sku对应的唯一sku及件数
        Map<Long, Map<Long, Map<String, Long>>> skuAttrIds = new HashMap<Long, Map<Long, Map<String, Long>>>();
        // 库位上每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>> skuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();
        // 库位上每个唯一sku对应的货格（is_whole_case=0&&有小车&&库位上sku不在任何容器内）
        Map<Long, Map<String, Set<String>>> skuAttrIdsContainerLattice = new HashMap<Long, Map<String, Set<String>>>();
        // 外部容器对应所有内部容器 
        Map<Long, Set<Long>> outerToInside = new HashMap<Long, Set<Long>>();
        // 内部容器对应所有sku
        Map<Long, Set<Long>> insideSkuIds = new HashMap<Long, Set<Long>>();
        // 内部容器每个sku总件数
        Map<Long, Map<Long, Long>> insideSkuQty = new HashMap<Long, Map<Long, Long>>();
        // 内部容器每个sku对应的唯一sku及件数
        Map<Long, Map<Long, Map<String, Long>>> insideSkuAttrIds = new HashMap<Long, Map<Long, Map<String, Long>>>();
        // 内部容器每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();
        // 内部容器每个唯一sku对应的货格（is_whole_case=0&&有小车）
        Map<Long, Map<String, Set<String>>> insideSkuAttrIdsContainerLattice = new HashMap<Long, Map<String, Set<String>>>();
        // 工作明细ID和唯一sku对应关系       
        Map<String, String> workLineToOnlySku = new HashMap<String, String>();
        
        //根据作业id获取作业明细信息  
        List<WhOperationLineCommand> operationLineList = whOperationLineManager.findOperationLineByOperationId(whOperationCommand.getId(), whOperationCommand.getOuId());
        for(WhOperationLineCommand operationLine : operationLineList){
            //流程相关统计信息 
            if(whOperationCommand.getIsWholeCase() == false){
                // 所有小车
                if(null != operationLine.getUseOuterContainerId()){
                    outerContainers.add(operationLine.getUseOuterContainerId());
                }
                // 所有出库箱
                if(null != operationLine.getUseOutboundboxCode()){
                    outbounxBoxs.add(operationLine.getUseOutboundboxCode());    
                }
                // 小车货格与出库箱对应关系
                if(null != operationLine.getUseContainerLatticeNo() && null != operationLine.getUseOutboundboxCode()){
                    carStockToOutgoingBox.put(operationLine.getUseContainerLatticeNo(), operationLine.getUseOutboundboxCode());
                }
                // 所有周转箱
                if(null != operationLine.getFromInsideContainerId()){
                    turnoverBoxs.add(operationLine.getFromInsideContainerId());    
                }
            }else{
                // 所有托盘
                if(null != operationLine.getFromOuterContainerId()){
                    pallets.add(operationLine.getFromOuterContainerId());    
                }
                // 所有货箱
                if(null != operationLine.getFromInsideContainerId()){
                    containers.add(operationLine.getFromInsideContainerId());
                }
            }
            //获取内部容器唯一sku
            String onlySku = SkuCategoryProvider.getSkuAttrIdByOperationLine(operationLine);
            String lineToSku = this.getWorkLineToOnlySku(operationLine.getId(), operationLine.getWorkLineId(), operationLine.getUuid());
            workLineToOnlySku.put(lineToSku, onlySku);
            //根据库存UUID查找对应SN/残次信息
            List<WhSkuInventorySnCommand> skuInventorySnCommands = whSkuInventorySnDao.findWhSkuInventoryByUuid(whOperationCommand.getOuId(), operationLine.getUuid());
            //获取库位ID
            locationIds.add(operationLine.getFromLocationId());
            //获取外部容器
            if(null != operationLine.getFromLocationId() && null != operationLine.getFromOuterContainerId() && null != outerContainerIds.get(operationLine.getFromLocationId())){
                outerContainerIds.get(operationLine.getFromLocationId()).add(operationLine.getFromOuterContainerId());
            }else if(null != operationLine.getFromLocationId() && null != operationLine.getFromOuterContainerId() && null == outerContainerIds.get(operationLine.getFromLocationId())){
                Set<Long> fromOuterContainerIdSet = new HashSet<Long>();
                fromOuterContainerIdSet.add(operationLine.getFromOuterContainerId());
                outerContainerIds.put(operationLine.getFromLocationId(), fromOuterContainerIdSet);
            }
            //获取内部容器（无外部容器情况）
            if(null != operationLine.getFromLocationId() && null != operationLine.getFromInsideContainerId() && null == operationLine.getFromOuterContainerId()){
                //无外部容器情况
                if(insideContainerIds.get(operationLine.getFromLocationId() ) != null){
                    insideContainerIds.get(operationLine.getFromLocationId()).add(operationLine.getFromInsideContainerId());
                }else{
                    Set<Long> fromInsideContainerIdSet = new HashSet<Long>();
                    fromInsideContainerIdSet.add(operationLine.getFromInsideContainerId());
                    insideContainerIds.put(operationLine.getFromLocationId(), fromInsideContainerIdSet);
                }
            }
            //sku不在任何容器内
            if(null == operationLine.getFromOuterContainerId() && null == operationLine.getFromInsideContainerId()){
                //获取sku（sku不在任何容器内）
                if(null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null != skuIds.get(operationLine.getFromLocationId())){
                    skuIds.get(operationLine.getFromLocationId()).add(operationLine.getSkuId());
                }else if(null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null == skuIds.get(operationLine.getFromLocationId())){
                    Set<Long> skuIdSet = new HashSet<Long>();
                    skuIdSet.add(operationLine.getSkuId());
                    skuIds.put(operationLine.getFromLocationId(), skuIdSet);
                }
                //获取每个sku总件数
                if(null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != skuQty.get(operationLine.getFromLocationId())){
                    Map<Long, Long> skuIdQtyMap = new HashMap<Long, Long>();
                    skuIdQtyMap = skuQty.get(operationLine.getFromLocationId());
                    if(null != skuIdQtyMap.get(operationLine.getSkuId())){
                        Long qty =  skuIdQtyMap.get(operationLine.getSkuId()) + operationLine.getQty().longValue();
                        skuIdQtyMap.put(operationLine.getSkuId(), qty);
                    }else{
                        skuIdQtyMap.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                    }
                    skuQty.put(operationLine.getFromLocationId(), skuIdQtyMap);
                }else if(null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null == skuQty.get(operationLine.getFromLocationId())){
                    Map<Long, Long> skuIdQtyMap = new HashMap<Long, Long>();
                    skuIdQtyMap.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                    skuQty.put(operationLine.getFromLocationId(), skuIdQtyMap);
                }
                //获取每个sku对应的唯一sku及件数
                if(null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != onlySku && null != skuAttrIds.get(operationLine.getFromLocationId())){
                    Map<Long, Map<String, Long>> skuIdMap = new HashMap<Long, Map<String, Long>>();
                    skuIdMap = skuAttrIds.get(operationLine.getFromLocationId());
                    if(null != skuIdMap.get(operationLine.getSkuId())){
                        Map<String, Long> skuAttrIdsQty = skuIdMap.get(operationLine.getSkuId());
                        if (null != skuAttrIdsQty.get(onlySku)) {
                            skuAttrIdsQty.put(onlySku, skuAttrIdsQty.get(onlySku) + operationLine.getQty().longValue());
                        } else {
                            skuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                        }
                        skuIdMap.put(operationLine.getSkuId(), skuAttrIdsQty);
                     }else{
                         Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                         insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                         skuIdMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                     }
                    skuAttrIds.put(operationLine.getFromLocationId(), skuIdMap);
                }else if(null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != onlySku && null == skuAttrIds.get(operationLine.getFromLocationId())){
                    Map<Long, Map<String, Long>> skuIdMap = new HashMap<Long, Map<String, Long>>();
                    Map<String, Long> skuAttrIdsQty = new HashMap<String, Long>();
                    skuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                    skuIdMap.put(operationLine.getSkuId(), skuAttrIdsQty);
                    skuAttrIds.put(operationLine.getFromLocationId(), skuIdMap);
                }
                // 库位上每个唯一sku对应的所有sn及残次条码
                if(null != operationLine.getFromLocationId() && null != onlySku && null != skuAttrIdsSnDefect.get(operationLine.getFromLocationId())){
                    Map<String, Set<String>> onlySkuSnMap = new HashMap<String, Set<String>>();
                    onlySkuSnMap = skuAttrIdsSnDefect.get(operationLine.getFromLocationId());
                    if(null != onlySkuSnMap.get(onlySku)){
                        Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                        snDefectWareBarcodeSet = onlySkuSnMap.get(onlySku);
                        for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                            snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                        }
                        onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    }else{
                        Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                        for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                            snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                        }
                        onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    }
                    skuAttrIdsSnDefect.put(operationLine.getFromLocationId(), onlySkuSnMap);
                }else if(null != operationLine.getFromLocationId() && null != onlySku && null == skuAttrIdsSnDefect.get(operationLine.getFromLocationId())){
                    Map<String, Set<String>> onlySkuSnMap = new HashMap<String, Set<String>>();
                    Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                    for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                        snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                    }
                    onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    skuAttrIdsSnDefect.put(operationLine.getFromLocationId(), onlySkuSnMap);
                }
                // 库位上每个唯一sku对应的货格（is_whole_case=0&&有小车&&库位上sku不在任何容器内）
                if(null != operationLine.getFromLocationId() && null != onlySku && null != operationLine.getUseOutboundboxCode() && whOperationCommand.getIsWholeCase() == false){
                    if(null != skuAttrIdsContainerLattice.get(operationLine.getFromLocationId())){
                        Map<String, Set<String>> onlySkuUseOutboundboxMap = new HashMap<String, Set<String>>();
                        onlySkuUseOutboundboxMap = skuAttrIdsContainerLattice.get(operationLine.getFromLocationId());
                        if(null != onlySkuUseOutboundboxMap.get(onlySku)){
                            Set<String> useOutboundboxCodeSet = new HashSet<String>();
                            useOutboundboxCodeSet = onlySkuUseOutboundboxMap.get(onlySku);
                            useOutboundboxCodeSet.add(operationLine.getUseOutboundboxCode());
                            onlySkuUseOutboundboxMap.put(onlySku, useOutboundboxCodeSet);
                        }else{
                            Set<String> useOutboundboxCodeSet = new HashSet<String>();
                            useOutboundboxCodeSet.add(operationLine.getUseOutboundboxCode());
                            onlySkuUseOutboundboxMap.put(onlySku, useOutboundboxCodeSet);
                        }
                        skuAttrIdsContainerLattice.put(operationLine.getFromLocationId(), onlySkuUseOutboundboxMap);
                    }else{
                        Map<String, Set<String>> onlySkuUseOutboundboxMap = new HashMap<String, Set<String>>();
                        Set<String> useOutboundboxCodeSet = new HashSet<String>();
                        useOutboundboxCodeSet.add(operationLine.getUseOutboundboxCode());
                        onlySkuUseOutboundboxMap.put(onlySku, useOutboundboxCodeSet);
                        skuAttrIdsSnDefect.put(operationLine.getFromLocationId(), onlySkuUseOutboundboxMap);
                    }
                }
            }
            // 存在外部容器并且有对应内部容器
            if(null != operationLine.getFromOuterContainerId() && null != operationLine.getFromInsideContainerId()){
                // 外部容器对应所有内部容器
                if(null != operationLine.getFromOuterContainerId() && null != operationLine.getSkuId() && null != outerToInside.get(operationLine.getFromOuterContainerId())){
                    outerToInside.get(operationLine.getFromOuterContainerId()).add(operationLine.getFromInsideContainerId());
                }else if(null != operationLine.getFromOuterContainerId() && null != operationLine.getSkuId() && null == outerToInside.get(operationLine.getFromOuterContainerId())){
                    Set<Long> fromInsideContainerIdSet = new HashSet<Long>();
                    fromInsideContainerIdSet.add(operationLine.getFromInsideContainerId());
                    outerToInside.put(operationLine.getFromOuterContainerId(), fromInsideContainerIdSet);
                }
                //内部容器对应所有sku
                if(null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null != insideSkuIds.get(operationLine.getFromInsideContainerId())){
                    insideSkuIds.get(operationLine.getFromInsideContainerId()).add(operationLine.getSkuId());
                }else if(null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null == insideSkuIds.get(operationLine.getFromInsideContainerId())){
                    Set<Long> skuIdSet = new HashSet<Long>();
                    skuIdSet.add(operationLine.getSkuId());
                    insideSkuIds.put(operationLine.getFromInsideContainerId(), skuIdSet);
                }
                //内部容器每个sku总件数
                if(null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != insideSkuQty.get(operationLine.getFromInsideContainerId())){
                    Map<Long, Long> skuIdQtyMap = new HashMap<Long, Long>();
                    skuIdQtyMap = insideSkuQty.get(operationLine.getFromInsideContainerId());
                    if(null != skuIdQtyMap.get(operationLine.getSkuId())){
                        Long insQty =  skuIdQtyMap.get(operationLine.getSkuId()) + operationLine.getQty().longValue();
                        skuIdQtyMap.put(operationLine.getSkuId(), insQty);
                     }else{
                         skuIdQtyMap.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                     }
                    insideSkuQty.put(operationLine.getFromInsideContainerId(), skuIdQtyMap);
                }else if(null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null == insideSkuQty.get(operationLine.getFromInsideContainerId())){
                    Map<Long, Long> skuIdQtyMap = new HashMap<Long, Long>();
                    skuIdQtyMap.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                    insideSkuQty.put(operationLine.getFromInsideContainerId(), skuIdQtyMap);
                }
                //内部容器每个sku对应的唯一sku及件数
                if(null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != onlySku && null != insideSkuAttrIds.get(operationLine.getFromInsideContainerId())){
                    Map<Long, Map<String, Long>> fromInsideContainerIdMap = new HashMap<Long, Map<String, Long>>();
                    fromInsideContainerIdMap = insideSkuAttrIds.get(operationLine.getFromInsideContainerId());
                    if(null != fromInsideContainerIdMap.get(operationLine.getSkuId())){
                        Map<String, Long> insideSkuAttrIdsQty = fromInsideContainerIdMap.get(operationLine.getSkuId());
                        if (null != insideSkuAttrIdsQty.get(onlySku)) {
                            insideSkuAttrIdsQty.put(onlySku, insideSkuAttrIdsQty.get(onlySku) + operationLine.getQty().longValue());
                        } else {
                            insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                        }
                        fromInsideContainerIdMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                     }else{
                         Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                         insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                         fromInsideContainerIdMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                     }
                    insideSkuAttrIds.put(operationLine.getFromInsideContainerId(), fromInsideContainerIdMap);
                }else if(null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != onlySku && null == insideSkuAttrIds.get(operationLine.getFromInsideContainerId())){
                    Map<Long, Map<String, Long>> fromInsideContainerIdMap = new HashMap<Long, Map<String, Long>>();
                    Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                    insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                    fromInsideContainerIdMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                    insideSkuAttrIds.put(operationLine.getFromInsideContainerId(), fromInsideContainerIdMap);
                }
                // 内部容器每个唯一sku对应的所有sn及残次条码
                if(null != operationLine.getFromInsideContainerId() && null != onlySku && null != insideSkuAttrIdsSnDefect.get(operationLine.getFromInsideContainerId())){
                    Map<String, Set<String>> onlySkuSnMap = new HashMap<String, Set<String>>();
                    onlySkuSnMap = insideSkuAttrIdsSnDefect.get(operationLine.getFromLocationId());
                    if(null != onlySkuSnMap.get(onlySku)){
                        Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                        snDefectWareBarcodeSet = onlySkuSnMap.get(onlySku);
                        for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                            snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                        }
                        onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    }else{
                        Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                        for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                            snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                        }
                        onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    }
                    insideSkuAttrIdsSnDefect.put(operationLine.getFromInsideContainerId(), onlySkuSnMap);
                }else if(null != operationLine.getFromInsideContainerId() && null != onlySku && null == insideSkuAttrIdsSnDefect.get(operationLine.getFromInsideContainerId())){
                    Map<String, Set<String>> onlySkuSnMap = new HashMap<String, Set<String>>();
                    Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                    for(WhSkuInventorySnCommand skuInventorySnCommand :skuInventorySnCommands){
                        snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommand.getSn(), skuInventorySnCommand.getDefectWareBarcode()));
                    }
                    onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    insideSkuAttrIdsSnDefect.put(operationLine.getFromInsideContainerId(), onlySkuSnMap);
                }
                //内部容器每个唯一sku对应的货格（is_whole_case=0&&有小车）
                if(null != operationLine.getFromInsideContainerId() && null != onlySku && null != operationLine.getUseOutboundboxCode() && whOperationCommand.getIsWholeCase() == false){
                    if(null != skuAttrIdsContainerLattice.get(operationLine.getFromInsideContainerId())){
                        Map<String, Set<String>> onlySkuOutboundboxMap = new HashMap<String, Set<String>>();
                        onlySkuOutboundboxMap = skuAttrIdsContainerLattice.get(operationLine.getFromInsideContainerId());
                        if(null != onlySkuOutboundboxMap.get(onlySku)){
                            Set<String> useOutboundboxCodeSet = new HashSet<String>();
                            useOutboundboxCodeSet = onlySkuOutboundboxMap.get(onlySku);
                            useOutboundboxCodeSet.add(operationLine.getUseOutboundboxCode());
                            onlySkuOutboundboxMap.put(onlySku, useOutboundboxCodeSet);
                        }else{
                            Set<String> useOutboundboxCodeSet = new HashSet<String>();
                            useOutboundboxCodeSet.add(operationLine.getUseOutboundboxCode());
                            onlySkuOutboundboxMap.put(onlySku, useOutboundboxCodeSet);
                        }
                        skuAttrIdsContainerLattice.put(operationLine.getFromInsideContainerId(), onlySkuOutboundboxMap);
                    }else if(null != operationLine.getFromInsideContainerId() && null != onlySku && null != operationLine.getUseOutboundboxCode() && whOperationCommand.getIsWholeCase() == false && null == operationLine.getFromOuterContainerId()){
                        Map<String, Set<String>> onlySkuOutboundboxMap = new HashMap<String, Set<String>>();
                        Set<String> useOutboundboxCodeSet = new HashSet<String>();
                        useOutboundboxCodeSet.add(operationLine.getUseOutboundboxCode());
                        onlySkuOutboundboxMap.put(onlySku, useOutboundboxCodeSet);
                        skuAttrIdsSnDefect.put(operationLine.getFromInsideContainerId(), onlySkuOutboundboxMap);
                    }
                }
            }
        }
        
        //载入统计分析信息        
        OperatioLineStatisticsCommand statisticsCommand = new OperatioLineStatisticsCommand();
        // 是否整托整箱
        statisticsCommand.setIsWholeCase(whOperationCommand.getIsWholeCase());
        // 所有小车
        statisticsCommand.setOuterContainers(outerContainers);
        // 所有出库箱
        statisticsCommand.setOutbounxBoxs(outbounxBoxs);
        // 小车货格与出库箱对应关系
        statisticsCommand.setCarStockToOutgoingBox(carStockToOutgoingBox);
        // 所有周转箱
        statisticsCommand.setTurnoverBoxs(turnoverBoxs);
        // 所有托盘
        statisticsCommand.setPallets(pallets);
        // 所有货箱
        statisticsCommand.setContainers(containers);
        // 库位排序
        List<Long> sortLocationIds = new ArrayList<Long>();
        sortLocationIds = locationManager.sortByIds(locationIds, whOperationCommand.getOuId());
        // 所有库位
        statisticsCommand.setLocationIds(sortLocationIds);
        // 库位上所有外部容器
        statisticsCommand.setOuterContainerIds(outerContainerIds);
        // 库位上所有内部容器 
        statisticsCommand.setInsideContainerIds(insideContainerIds);
        // 库位上所有sku
        statisticsCommand.setSkuIds(skuIds);
        // 库位上每个sku总件数
        statisticsCommand.setSkuQty(skuQty);
        // 库位上每个sku对应的唯一sku及件数
        statisticsCommand.setSkuAttrIds(skuAttrIds);
        // 库位上每个唯一sku对应的所有sn及残次条码
        statisticsCommand.setSkuAttrIdsSnDefect(skuAttrIdsSnDefect);
        // 库位上每个唯一sku对应的货格（is_whole_case=0&&有小车&&库位上sku不在任何容器内）
        statisticsCommand.setSkuAttrIdsContainerLattice(skuAttrIdsContainerLattice);
        // 外部容器对应所有内部容器
        statisticsCommand.setOuterToInside(outerToInside);
        // 内部容器对应所有sku
        statisticsCommand.setInsideSkuIds(insideSkuIds);
        // 内部容器每个sku总件数
        statisticsCommand.setInsideSkuQty(insideSkuQty);
        // 内部容器每个sku对应的唯一sku及件数
        statisticsCommand.setInsideSkuAttrIds(insideSkuAttrIds);
        // 内部容器每个唯一sku对应的所有sn及残次条码
        statisticsCommand.setInsideSkuAttrIdsSnDefect(insideSkuAttrIdsSnDefect);
        // 内部容器每个唯一sku对应的货格（is_whole_case=0&&有小车）
        statisticsCommand.setInsideSkuAttrIdsContainerLattice(insideSkuAttrIdsContainerLattice);
        // 工作明细ID和唯一sku对应关系
        statisticsCommand.setWorkLineIdToOnlySku(workLineToOnlySku);
        
        //缓存统计分析结果        
        pdaPickingWorkCacheManager.operatioLineStatisticsRedis(whOperationCommand.getId(), statisticsCommand);
    }
    
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
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        Integer pickingWay = command.getPickingWay();
        pSRcmd.setOperationId(operationId);
        pSRcmd.setPickingWay(pickingWay);
        //缓存作业明细
        pdaPickingWorkCacheManager.cacheOperationLine(operationId, ouId);
        if(pickingWay == Constants.PICKING_WAY_ONE) { //使用外部容器(小车) 无出库箱拣货流程
            String tipOuterContainer = pdaPickingWorkCacheManager.pdaPickingWorkTipOutContainer(operationId,ouId);
            pSRcmd.setTipOuterContainer(tipOuterContainer);  //提示小车
            ContainerCommand container = containerDao.getContainerByCode(tipOuterContainer, ouId);
            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(container.getTwoLevelType(), ouId);
            if(null == c2c) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            pSRcmd.setName(c2c.getCategoryName());
        }
        if(pickingWay == Constants.PICKING_WAY_TWO) { //使用外部(小车)，有出库箱拣货流程
            String tipOuterContainer = pdaPickingWorkCacheManager.pdaPickingWorkTipOutContainer(operationId,ouId);
            pSRcmd.setTipOuterContainer(tipOuterContainer);  //提示小车
            ContainerCommand container = containerDao.getContainerByCode(tipOuterContainer, ouId);
            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(container.getTwoLevelType(), ouId);
            if(null == c2c) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            pSRcmd.setName(c2c.getCategoryName());
        }
        if(pickingWay == Constants.PICKING_WAY_THREE) { //使用出库箱拣货流程
            String tipOutBounxBoxCode = pdaPickingWorkCacheManager.pdaPickingWorkTipoutboundBox(operationId,ouId);
            pSRcmd.setOutBounxBoxCode(tipOutBounxBoxCode);
            OutBoundBoxType outBoundBox = outBoundBoxTypeDao.findByCode(tipOutBounxBoxCode, ouId);
            if(null == outBoundBox) {
                throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL );
            }
            // 验证容器Lifecycle是否有效
            if (!outBoundBox.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_STATUS_NO);
            }
            pSRcmd.setName(outBoundBox.getName());
        }
        if(pickingWay == Constants.PICKING_WAY_FOUR) {  //使用周转箱拣货流程
            String turnoverBox = pdaPickingWorkCacheManager.pdaPickingWorkTipTurnoverBox(operationId,ouId);
            pSRcmd.setTipTurnoverBoxCode(turnoverBox);
            ContainerCommand container = containerDao.getContainerByCode(turnoverBox, ouId);
            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(container.getTwoLevelType(), ouId);
            if(null == c2c) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            pSRcmd.setName(c2c.getCategoryName());
        }
        log.info("PdaPickingWorkManagerImpl pdaPickingRemmendContainer is end");
        return pSRcmd;
    }
    

    /***
     * pda推荐容器拣货扫描容器
     * @author tangming
     * @param command
     * @return
     */
    public PickingScanResultCommand pdaPickingScanContainer(PickingScanResultCommand  command){
        log.info("PdaPickingWorkManagerImpl pdaPickingScanContainer is start");
        Integer pickingWay = command.getPickingWay();
        Long ouId = command.getOuId();
        Long functionId = command.getFunctionId();
        Long operationId = command.getOperationId();
        String containerCode = command.getOuterContainer();  //小车
        Boolean isScanOutBoundBox = command.getIsScanOutBoundBox();
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        if(pickingWay == Constants.PICKING_WAY_ONE) {
            //修改小车状态
            this.updateContainerStauts(containerCode, ouId);
        }
        if(pickingWay == Constants.PICKING_WAY_TWO && isScanOutBoundBox == true) { //使用外部(小车)，有出库箱拣货流程
            //修改小车状态
            this.updateContainerStauts(containerCode, ouId);
            Map<Integer, String> carStockToOutgoingBox =  operatorLine.getCarStockToOutgoingBox();   //出库箱和货格对应关系
            List<WhOperationLineCommand> operatorLineList =  whOperationLineManager.findOperationLineByOperationId(operationId, ouId);
            CheckScanResultCommand cSRCmd =  pdaPickingWorkCacheManager.pdaPickingTipOutBounxBoxCode(operatorLineList, operationId, carStockToOutgoingBox);
            if(cSRCmd.getIsNeedScanOutBounxBox()) {
                command.setTipOutBounxBoxCode(cSRCmd.getOutBounxBoxCode());  //出库箱id
                command.setIsNeedScanOutBounxBox(true);
                command.setUseContainerLatticeNo(cSRCmd.getUseContainerLatticeNo());
                command.setOuterContainer(containerCode);  //外部容器号(小车，单个出库箱)
            }else{
                command.setIsNeedScanOutBounxBox(false);
            }
            return command;
        }
        if(pickingWay == Constants.PICKING_WAY_FOUR){  //周转箱
            String turnoverBoxCode = command.getTurnoverBoxCode();
            this.updateContainerStauts(turnoverBoxCode, ouId);
        }
        command.setOuterContainer(containerCode);  //外部容器号(小车，单个出库箱)
        WhFunctionPicking picking = whFunctionPickingDao.findByFunctionIdExt(ouId, functionId);
        command.setIsScanLocation(picking.getIsScanLocation());  //是否扫描库位
        command.setIsScanOuterContainer(picking.getIsScanOuterContainer());   //是否扫描托盘
        command.setIsScanInsideContainer(picking.getIsScanInsideContainer());    //是否扫描内部容器
        command.setIsScanSku(picking.getIsScanSku());                 //是否扫描sku
        command.setIsScanInvAttr(picking.getIsScanInvAttr());           //是否扫描sku属性
        command.setScanPattern(picking.getScanPattern());  //扫描模式 
        List<Long> locationIds = operatorLine.getLocationIds();
        CheckScanResultCommand cSRCmd =  pdaPickingWorkCacheManager.tipLocation(operationId, locationIds);
        if(cSRCmd.getIsNeedTipLoc()) { //提示库位
            Long locationId = cSRCmd.getTipLocationId();
            Location location = whLocationDao.findByIdExt(locationId, ouId);
            if(null == location) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
            }
            command.setTipLocationBarCode(location.getBarCode());
            command.setTipLocationCode(location.getCode());
        }
        log.info("PdaPickingWorkManagerImpl pdaPickingScanContainer is end");
        return command;
    }
    
    private void updateContainerStauts(String containerCode,Long ouId){
        log.info("PdaPickingWorkManagerImpl updateContainerStauts is start");
        Container container = new Container();
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        BeanUtils.copyProperties(containerCmd, container);
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_PICKING);
        containerDao.saveOrUpdateByVersion(container);
        log.info("PdaPickingWorkManagerImpl updateContainerStauts is end");
        
    }
    
    /**
     * pda拣货整托整箱--获取缓存中的统计信息
     * @author qiming.liu
     * @param command
     * @return
     */
    @Override
    public PickingScanResultCommand pdaPickingWholeCase(PickingScanResultCommand command) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkManagerImpl pdaPickingWholeCase is start");
        PickingScanResultCommand pickingScanResultCommand = new PickingScanResultCommand();
        OperatioLineStatisticsCommand operatioLineStatisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(command.getOperationId(),command.getOuId());
        if(null != operatioLineStatisticsCommand.getLocationIds()){
            pickingScanResultCommand.setLocationId(operatioLineStatisticsCommand.getLocationIds().get(0));
        }
        log.info("PdaPickingWorkManagerImpl pdaPickingWholeCase is end");
        return pickingScanResultCommand;
    }
    
    /**
     * pda拣货确认提示外部容器托盘
     * @author tangming
     * @param command
     * @param pickingWay
     * @return
     */
    @Override
    public PickingScanResultCommand tipOuterContainer(PickingScanResultCommand command) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkManagerImpl scanLocation is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        String locationCode = command.getTipLocationCode();
        String locationBarCode = command.getTipLocationBarCode();
        if(!StringUtil.isEmpty(locationBarCode) && StringUtil.isEmpty(locationCode)) {
            locationCode = locationBarCode;
        }
        Location location = whLocationDao.findLocationByCode(locationCode, ouId);
        if(null == location) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
        }
        Long locationId = location.getId();
        command.setLocationId(location.getId());
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>>  outerContainerIdsLoc = operatorLine.getOuterContainerIds();
        Set<Long>  outerContainerIds = outerContainerIdsLoc.get(locationId);
        if(null != outerContainerIds  && outerContainerIds.size() != 0) {
            CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.pdaPickingTipOuterContainer(outerContainerIds, locationId);
            if(cSRCmd.getIsNeedTipOutContainer()) {//该库位上的所有外部外部容器都扫描完毕
                    Long outerContainerId = cSRCmd.getTipOuterContainerId();
                    //判断外部容器
                    Container c = containerDao.findByIdExt(outerContainerId, ouId);
                    this.judeContainerStatus(c);
                    //提示外部容器编码
                    command.setTipOuterContainerCode(c.getCode());
                    command.setIsTipOuterContainer(true);
             }else{
                    command.setIsTipOuterContainer(false);
             }
            
        }else{
            command.setIsTipOuterContainer(false); 
        }
        log.info("PdaPickingWorkManagerImpl scanLocation is end");
        return command;
    }

    /***确认提示托盘
     * @author tangming
     * @param command
     * @return
     */
    public PickingScanResultCommand  tipInsideContainer(PickingScanResultCommand  command){
        log.info("PdaPickingWorkManagerImpl confirmTipOuterContainer is start");
        Long operationId = command.getOperationId();
        String tipOuterContainerCode = command.getTipOuterContainerCode();
        Long ouId = command.getOuId();
        Long locationId = command.getLocationId();
        ContainerCommand  outerContainerCmd = null;
        Long outerId = null;
        if(!StringUtil.isEmpty(tipOuterContainerCode)) {
            outerContainerCmd = containerDao.getContainerByCode(tipOuterContainerCode, ouId);
            if(null == outerContainerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerId = outerContainerCmd.getId();
        }
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> outerToInside = operatorLine.getOuterToInside();  //本托盘上所有对应的内部容器
        Map<Long, Set<Long>> locInsideContainerIds = operatorLine.getInsideContainerIds();   //库位上所有的内部容器
        Set<Long> insideContainerIds = null;
        if(null == outerContainerCmd) {
                insideContainerIds = locInsideContainerIds.get(locationId);
        }else{
                insideContainerIds = outerToInside.get(outerId);
        }
        if(null != insideContainerIds && insideContainerIds.size() != 0) {
            CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.pdaPickingTipInsideContainer(insideContainerIds, locationId);
            if(cSRCmd.getIsNeedTipInsideContainer()) { //托盘上还有货箱没有扫描
                Long tipInsideContainerId = cSRCmd.getTipiInsideContainerId();
                Container ic = containerDao.findByIdExt(tipInsideContainerId, ouId);
                this.judeContainerStatus(ic);
                command.setTipInsideContainerCode(ic.getCode());
                command.setOuterContainerCode(tipOuterContainerCode);
                command.setIsTipinsideCotnainer(true);
           }else{
                command.setIsTipinsideCotnainer(false);
           }
        }else{
            command.setIsTipinsideCotnainer(false);
        }
       
        log.info("PdaPickingWorkManagerImpl confirmTipOuterContainer is end");
        return command;
    }
    
    /***
     * 提示sku
     * @author tangminmg
     * @param command
     * @return
     */
    public PickingScanResultCommand tipSku(PickingScanResultCommand  command){
        log.info("PdaPickingWorkManagerImpl tipSku is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        Long locationId = command.getLocationId();
        String tipInsideContainerCode = command.getTipInsideContainerCode();   //内部容器
        ContainerCommand  insideContainerCmd = null;
        Long insideContainerId = null;
        if(!StringUtil.isEmpty(tipInsideContainerCode)) {
            insideContainerCmd = containerDao.getContainerByCode(tipInsideContainerCode, ouId);
            if(insideContainerCmd == null) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = insideContainerCmd.getId();
        }
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> insideSkuIds = operatorLine.getInsideSkuIds();  //内部容器对应所有的sku
        Map<Long, Set<Long>> locSkuIds = operatorLine.getSkuIds();            //库位上散放的sku
        Map<Long, Map<String, Set<String>>>    skuAttrIdsSnDefect =  operatorLine.getSkuAttrIdsSnDefect();   //库位上每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>>     insideSkuAttrIdsSnDefect = operatorLine.getInsideSkuAttrIdsSnDefect();  //内部容器每个唯一sku对应的所有sn及残次条码
        Map<Long,Map<Long, Map<String, Long>>> locSkuAttrIdsQty = operatorLine.getSkuAttrIds();    //库位上每个sku对应的唯一sku及件数
        Map<Long, Map<String, Long>>  skuIdSkuAttrIdsQty = locSkuAttrIdsQty.get(locationId);
        Set<Long> skuIds = null;
        if(null == insideContainerId) { //sku直接放在库位上
            skuIds = locSkuIds.get(locationId);
        }else{
            skuIds = insideSkuIds.get(insideContainerId);
        }
        CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.pdaPickingTipSku(skuIds, operationId,locationId,ouId, insideContainerId,skuAttrIdsSnDefect,insideSkuAttrIdsSnDefect);
        if(cSRCmd.getIsNeedTipSku()){  //此货箱的sku，还没有扫描完毕
            String skuAttrId = cSRCmd.getTipSkuAttrId();   //提示唯一的sku
            Long skuId = SkuCategoryProvider.getSkuId(skuAttrId);
            Map<String,Long> skuAttrIdsQty = skuIdSkuAttrIdsQty.get(skuId);
            WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
            if(null == skuCmd) {
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            command.setTipSkuBarCode(skuCmd.getBarCode());    //提示sku
            command.setIsNeedTipSku(true);
            command.setSkuId(skuId);
            this.tipSkuDetailAspect(command, skuAttrId, skuAttrIdsQty, logId);
        }else{
            command.setIsNeedTipSku(false);
        }
        log.info("PdaPickingWorkManagerImpl tipSku is end");
        
        return command;
    }
    
    
    /***
     * 判断货箱内库存属性是否唯一
     * @param command
     * @return
     */
    public PickingScanResultCommand judgeSkuAttrIdsIsUnique(PickingScanResultCommand  command){
        log.info("PdaPickingWorkManagerImpl judgeSkuAttrIdsIsUnique is start");
        Long operationId = command.getOperationId();
        Long locationId = command.getLocationId();
        Long ouId = command.getOuId();
        Long insideContainerId  = null;
        String insideContainerCode = command.getInsideContainerCode();
        String skuBarcode = command.getSkuBarCode();   //商品条码
        Long skuId = null;
        if(!StringUtil.isEmpty(insideContainerCode)) {
            ContainerCommand ic = containerDao.getContainerByCode(insideContainerCode, ouId);
            if (null == ic) {
                // 容器信息不存在
                log.error("pdaScanContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = ic.getId();
        }
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>>   insideContainerSkuIds = operatorLine.getInsideSkuIds();
        Map<Long, Set<Long>>  locSkuIds = operatorLine.getSkuIds();
        Set<Long> icSkuIds = null;   
        if(StringUtil.isEmpty(insideContainerCode)) {  //商品直接放在库位上
            icSkuIds = locSkuIds.get(locationId);
        }else{   //放在货箱里
            icSkuIds = insideContainerSkuIds.get(insideContainerId);
        }
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId);   //获取对应的商品数量,key值是sku id
        boolean isSkuExists = false;
        for(Long cacheId : cacheSkuIdsQty.keySet()){
            if(icSkuIds.contains(cacheId)){
                isSkuExists = true;
            }
            if(true == isSkuExists){
                skuId = cacheId;
                break;
            }
        }
        if(false == isSkuExists){
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerId,insideContainerId, skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideContainerCode});
        }
        //该库位要拣货的所有库存记录
        List<WhSkuInventoryCommand> list = pdaPickingWorkCacheManager.cacheLocationInventory(operationId, locationId, ouId);
        List<WhSkuInventoryCommand> icList = new ArrayList<WhSkuInventoryCommand>();  //当前扫描的内部容器的库位库存
        for(WhSkuInventoryCommand skuInvCmd:list) {
            if(null != insideContainerId) {  //库位上有货箱
                if(insideContainerId.equals(skuInvCmd.getInsideContainerId())) {
                    icList.add(skuInvCmd);  
                }
            }else{   //库位上没有货箱
                if(skuId.longValue() == skuInvCmd.getSkuId().longValue()) {
                    icList.add(skuInvCmd);   
                }
            }
        }
        //货箱内待拣货sku库位库存库存属性是否唯一
        Set<String> skuAttrIdsSet = new HashSet<String>();
        for(WhSkuInventoryCommand invSkuCmd:icList) {
            String pSkuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd);
            skuAttrIdsSet.add(pSkuAttrId);
        }
        if(skuAttrIdsSet.size() > 0) {   //货箱内待拣货sku库存属性不唯一
            command.setIsUniqueSkuAttrInside(false);  // 不唯一
        }else{//货箱内待拣货sku库存属性唯一
            command.setIsUniqueSkuAttrInside(true);  //唯一
        }
        log.info("PdaPickingWorkManagerImpl judgeSkuAttrIdsIsUnique is end");
        
        return command;
        
    }
    
    /***
     * 
     * @param srCmd
     * @param tipSkuAttrId
     * @param locSkuAttrIds
     * @param skuAttrIdsQty
     * @param logId
     */
    private void tipSkuDetailAspect(PickingScanResultCommand srCmd, String tipSkuAttrId,  Map<String, Long> skuAttrIdsQty, String logId) {
        String skuAttrId = SkuCategoryProvider.getSkuAttrId(tipSkuAttrId);
        Long qty = skuAttrIdsQty.get(skuAttrId);
        if (null == qty) {
            log.error("sku qty is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        srCmd.setTipSkuQty(qty);
        srCmd.setIsNeedScanSkuInvType(TipSkuDetailProvider.isTipSkuInvType(tipSkuAttrId));  //是否需要扫描商品类型
        if (true == srCmd.getIsNeedScanSkuInvType()) {
                String skuInvType = TipSkuDetailProvider.getSkuInvType(tipSkuAttrId);
                List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroup(Constants.INVENTORY_TYPE, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : invTypeList) {
                    if (sd.getDicValue().equals(skuInvType)) {
                        srCmd.setSkuInvType(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv type is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setSkuInvType("");
            }
            srCmd.setIsNeedScanSkuInvStatus(TipSkuDetailProvider.isTipSkuInvStatus(tipSkuAttrId));
            if (true == srCmd.getIsNeedScanSkuInvStatus()) {
                String skuInvStatus = TipSkuDetailProvider.getSkuInvStatus(tipSkuAttrId);
                List<InventoryStatus> invStatusList = inventoryStatusManager.findAllInventoryStatus();
                boolean isExists = false;
                for (InventoryStatus is : invStatusList) {
                    if (is.getId().toString().equals(skuInvStatus)) {
                        srCmd.setSkuInvStatus(is.getName());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv status is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setSkuInvStatus("");
            }
            //sku不在任何容器内            
            srCmd.setIsNeedScanSkuMfgDate(TipSkuDetailProvider.isTipSkuMfgDate(tipSkuAttrId));
            if (true == srCmd.getIsNeedScanSkuMfgDate()) {
                String skuMfgDate = TipSkuDetailProvider.getSkuMfgDate(tipSkuAttrId);
                srCmd.setSkuMfgDate(skuMfgDate);
            } else {
                srCmd.setSkuMfgDate("");
            }
            srCmd.setIsNeedScanSkuExpDate(TipSkuDetailProvider.isTipSkuExpDate(tipSkuAttrId));
            if (true == srCmd.getIsNeedScanSkuExpDate()) {
                String skuExpDate = TipSkuDetailProvider.getSkuExpDate(tipSkuAttrId);
                srCmd.setSkuExpDate(skuExpDate);
            } else {
                srCmd.setSkuExpDate("");
            }
            srCmd.setIsNeedScanSkuInvAttr1(TipSkuDetailProvider.isTipSkuInvAttr1(tipSkuAttrId));
            if (true == srCmd.getIsNeedScanSkuInvAttr1()) {
                String skuInvAttr1 = TipSkuDetailProvider.getSkuInvAttr1(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_1, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr1)) {
                        srCmd.setSkuInvAttr1(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
            } else {
                srCmd.setSkuInvAttr1("");
            }
            srCmd.setIsNeedScanSkuInvAttr2(TipSkuDetailProvider.isTipSkuInvAttr2(tipSkuAttrId));
            if (true == srCmd.getIsNeedScanSkuInvAttr2()) {
                String skuInvAttr2 = TipSkuDetailProvider.getSkuInvAttr2(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_2, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr2)) {
                        srCmd.setSkuInvAttr2(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr2 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setSkuInvAttr2("");
            }
            srCmd.setIsNeedScanSkuInvAttr3(TipSkuDetailProvider.isTipSkuInvAttr3(tipSkuAttrId));
            if (true == srCmd.getIsNeedScanSkuInvAttr3()) {
                String skuInvAttr3 = TipSkuDetailProvider.getSkuInvAttr3(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_3, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr3)) {
                        srCmd.setSkuInvAttr3(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr3 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setSkuInvAttr3("");
            }
            srCmd.setIsNeedScanSkuInvAttr4(TipSkuDetailProvider.isTipSkuInvAttr4(tipSkuAttrId));
            if (true == srCmd.getIsNeedScanSkuInvAttr4()) {
                String skuInvAttr4 = TipSkuDetailProvider.getSkuInvAttr4(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_4, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr4)) {
                        srCmd.setSkuInvAttr4(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr4 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setSkuInvAttr4("");
            }
            srCmd.setIsNeedScanSkuInvAttr5(TipSkuDetailProvider.isTipSkuInvAttr5(tipSkuAttrId));
            if (true == srCmd.getIsNeedScanSkuInvAttr5()) {
                String skuInvAttr5 = TipSkuDetailProvider.getSkuInvAttr5(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_5, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr5)) {
                        srCmd.setSkuInvAttr5(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr5 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setSkuInvAttr5("");
            }
            srCmd.setIsNeedScanSkuSn(TipSkuDetailProvider.isTipSkuSn(tipSkuAttrId));
            if (true == srCmd.getIsNeedScanSkuSn()) {
                String skuSn = TipSkuDetailProvider.getSkuSn(tipSkuAttrId);
                srCmd.setSkuSn(skuSn);
            } else {
                srCmd.setSkuSn("");
            }
            srCmd.setIsNeedScanSkuDefect(TipSkuDetailProvider.isTipSkuDefect(tipSkuAttrId));
            if (true == srCmd.getIsNeedScanSkuDefect()) {
                String skuDefect = TipSkuDetailProvider.getSkuDefect(tipSkuAttrId);
                srCmd.setSkuDefect(skuDefect);
            } else {
                srCmd.setSkuDefect("");
            }
    }
    /***pda扫描sku
     * @author tangminmg
     * @param command
     * @return
     */
    public PickingScanResultCommand scanSku(PickingScanResultCommand  command,WhSkuCommand skuCmd){
        log.info("PdaPickingWorkManagerImpl scanSku is start");
        Long operationId = command.getOperationId();
        Long functionId = command.getFunctionId();
        Long locationId = command.getLocationId();   
        String outBoundBoxCode = command.getOutBounxBoxCode();
        Long userId = command.getUserId();
        Integer pickingWay = command.getPickingWay();
        Long ouId = command.getOuId();
        Long skuId = command.getSkuId();
        String skuBarCode = command.getSkuBarCode();
        String insideContainerCode = command.getInsideContainerCode();
        String outerContainerCode = command.getOuterContainerCode(); 
        String turnoverBoxCode = command.getTurnoverBoxCode();   //周转箱
        Long outBoundBoxId = command.getOutBoundBoxId();   //出库箱id
        Boolean isTrunkful = command.getIsTrunkful();  //是否满箱
        ContainerCommand turnoverBoxCmd = containerDao.getContainerByCode(turnoverBoxCode, ouId);
        if(null == turnoverBoxCmd) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        Long turnoverBoxId = turnoverBoxCmd.getId();
        Long insideContainerId = null;
        ContainerCommand insideContainerCmd = null;
        if(!StringUtil.isEmpty(insideContainerCode)) {
            insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
            if(null == insideContainerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = insideContainerCmd.getId();
        }
        ContainerCommand outerContainerCmd = null;
        Long outerContainerId = null;
        if(!StringUtil.isEmpty(outerContainerCode)) {
            outerContainerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
            if(null == outerContainerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerContainerId = outerContainerCmd.getId();
        }
        Double scanQty = skuCmd.getScanSkuQty(); // 扫描的商品数量
        if (null == scanQty || scanQty.longValue() < 1) {
            log.error("scan sku qty is valid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
        }
        if (StringUtils.isEmpty(skuBarCode)) {
            log.error("sku is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        WhSkuInventoryCommand invSkuCmd = new WhSkuInventoryCommand();
        invSkuCmd.setSkuId(command.getSkuId()); 
        invSkuCmd.setInvType(command.getSkuInvType());
        invSkuCmd.setBatchNumber(command.getBatchNumber());
        try {
            invSkuCmd.setMfgDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(command.getSkuMfgDate()));
            invSkuCmd.setExpDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(command.getSkuExpDate()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        invSkuCmd.setCountryOfOrigin(command.getSkuOrigin());
        invSkuCmd.setInvAttr1(command.getSkuInvAttr1());
        invSkuCmd.setInvAttr2(command.getSkuInvAttr2());
        invSkuCmd.setInvAttr3(command.getSkuInvAttr3());
        invSkuCmd.setInvAttr4(command.getSkuInvAttr4());
        invSkuCmd.setInvAttr5(command.getSkuInvAttr5());
        List<InventoryStatus> listInventoryStatus = inventoryStatusManager.findAllInventoryStatus();
        String statusValue = command.getSkuInvStatus();
        // 库存状态
        if (!StringUtils.isEmpty(statusValue)) {
            for (InventoryStatus inventoryStatus : listInventoryStatus) {
                if (statusValue.equals(inventoryStatus.getName())) invSkuCmd.setInvStatus(inventoryStatus.getId()); // 库存状态
                break;
            }
        }
        String skuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd);
        pdaPickingWorkCacheManager.cacheSkuAttrId(locationId,skuId,skuAttrIds);
        WhFunctionPicking picking = whFunctionPickingDao.findByFunctionIdExt(ouId, functionId);
        if (null == picking) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == picking.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarCode, logId); // 获取对应的商品数量,key值是sku
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> insideSkuIds  = operatorLine.getInsideSkuIds();  //内部容器内所有的sku
        Map<Long, Set<Long>> operLocSkuIds = operatorLine.getSkuIds();  //库位上所有sku(sku不在任何容器内)
        Set<Long> locSkuIds = operLocSkuIds.get(locationId);
        Map<Long, Set<Long>> locOuterContainerIds = operatorLine.getOuterContainerIds();
        Set<Long> outerContainerIds = locOuterContainerIds.get(locationId);  //当前库位上所有外部容器集合
        Set<Long> icSkuIds = insideSkuIds.get(insideContainerId);
        List<Long> locationIds = operatorLine.getLocationIds();
        Map<Long, Map<Long, Long>> operLocSkuQty = operatorLine.getSkuQty();
        Map<Long,Long> locSkuQty = operLocSkuQty.get(operationId);
        Map<Long, Map<Long, Long>> insideSkuQty = operatorLine.getInsideSkuQty(); //内部容器每个sku总件数
        Map<Long,Long> insideContainerSkuIdsQty = insideSkuQty.get(insideContainerId);
        Map<Long, Set<Long>> operLocInsideContainerIds = operatorLine.getInsideContainerIds();//库位上所有的内部容器(无外部容器情况)
        Set<Long> locInsideContainerIds = operLocInsideContainerIds.get(locationId);
        Map<Long, Set<Long>>  insideContainerSkuIds = operatorLine.getInsideSkuIds();   //库位上内部容器对应的所有sku
        Map<Long, Set<Long>> outerToInsideIds = operatorLine.getOuterToInside(); //(库位上有外部容器的内部容器)
        Map<Long, Map<String, Set<String>>>    skuAttrIdsSnDefect =  operatorLine.getSkuAttrIdsSnDefect();   //库位上每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>>     insideSkuAttrIdsSnDefect = operatorLine.getInsideSkuAttrIdsSnDefect();  //内部容器每个唯一sku对应的所有sn及残次条码
        Set<Long> insideContainerIds = outerToInsideIds.get(locationId);
        boolean isSkuExists = false;
        Integer cacheSkuQty = 1;
        Integer icSkuQty = 1;
        for (Long cacheId : cacheSkuIdsQty.keySet()) {
            if (icSkuIds.contains(cacheId)) {
                isSkuExists = true;
            }
            if (true == isSkuExists) {
                skuId = cacheId;
                cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                icSkuQty = (null == insideContainerSkuIdsQty.get(cacheId) ? 1 : insideContainerSkuIdsQty.get(cacheId).intValue());
                break;
            }
        }
        if (false == isSkuExists) {
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]",  skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {});
        }
        if (cacheSkuQty > 1 && cacheSkuQty <= icSkuQty) {
            if (0 != (icSkuQty % cacheSkuQty)) {
                // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
                log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
                throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
            }
        }
        if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {
            if (0 != new Double("1").compareTo(scanQty)) {
                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarCode, logId);
                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
            }
        }
        skuCmd.setId(skuId);
        skuCmd.setScanSkuQty(scanQty * cacheSkuQty);// 可能是多条码
        CheckScanResultCommand cSRCmd =  pdaPickingWorkCacheManager.pdaPickingyCacheSkuAndCheckContainer(scanPattern ,locationIds, locSkuQty, locationId, locSkuIds, outerContainerIds, outerContainerCmd, operationId, insideContainerSkuIdsQty, insideContainerSkuIds, insideContainerIds, locInsideContainerIds, insideContainerCmd, skuCmd);
        if(cSRCmd.getIsNeedScanSku()) {
            if(pickingWay == Constants.PICKING_WAY_THREE && isTrunkful) { //是否出库箱满箱
                //跳转到扫描出库箱页面
                command.setIsUserNewContainer(true);
                return command;
            }
            if(pickingWay == Constants.PICKING_WAY_FOUR && isTrunkful) {  //周转箱是否满箱
                //跳转到扫描周转箱页面
                command.setIsUserNewContainer(true);
                return command;
            }
            command.setIsNeedTipSku(true);
            Set<Long> skuIds = null;
            if(null == insideContainerId) { //sku直接放在库位上
                skuIds = locSkuIds;
            }else{
                skuIds = insideSkuIds.get(insideContainerId);
            }
            CheckScanResultCommand cSRCommand = pdaPickingWorkCacheManager.pdaPickingTipSku(skuIds, operationId,locationId,ouId, insideContainerId,skuAttrIdsSnDefect,insideSkuAttrIdsSnDefect);
            if(cSRCommand.getIsNeedScanSku()){  //此货箱的sku，还没有扫描完毕
                String skuAttrId = cSRCmd.getTipSkuAttrId();   //提示唯一的sku
                WhSkuCommand whSkuCmd = whSkuDao.findWhSkuByIdExt(SkuCategoryProvider.getSkuId(skuAttrId), ouId);
                if(null == whSkuCmd) {
                    throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                }
                command.setSkuBarCode(whSkuCmd.getBarCode());   // 继续提示sku条码
            }else{
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
        }else if(cSRCmd.getIsNeedTipInsideContainer()){  //提示下一个货箱
            Set<Long> insideIds = null;
            if(null == outerContainerCmd) {
                insideIds = locInsideContainerIds;
            }else{
                insideIds = outerToInsideIds.get(outerContainerId);
            }
            CheckScanResultCommand cSRCommand = pdaPickingWorkCacheManager.pdaPickingTipInsideContainer(insideIds, locationId);
            if(cSRCommand.getIsNeedTipInsideContainer()) {
                Long tipInsideContainerId = cSRCmd.getTipiInsideContainerId();
                Container ic = containerDao.findByIdExt(tipInsideContainerId, ouId);
                this.judeContainerStatus(ic);
                command.setTipInsideContainerCode(ic.getCode());
                command.setIsNeedTipInsideContainer(true);
            }else{
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
        }else if(cSRCmd.getIsNeedTipOutContainer()) { // 提示下一个外部容器
            CheckScanResultCommand cSRCommand =  pdaPickingWorkCacheManager.pdaPickingTipOuterContainer(outerContainerIds, locationId);
            if(cSRCommand.getIsNeedTipOutContainer()) {
                Long outerId = cSRCmd.getTipOuterContainerId();
                //判断外部容器
                Container c = containerDao.findByIdExt(outerId, ouId);
                this.judeContainerStatus(c);
                //提示外部容器编码
                command.setTipOuterContainerCode(c.getCode());
            }else{
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR); 
            }
        }else if(cSRCmd.getIsNeedTipLoc()) {  //提示下一个库位
            CheckScanResultCommand cSRCommand = pdaPickingWorkCacheManager.tipLocation(operationId, locationIds);
            if(cSRCommand.getIsNeedTipLoc()) {
                Long locId = cSRCmd.getTipLocationId();
                Location location = whLocationDao.findByIdExt(locId, ouId);
                if(null == location) {
                    throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
                }
                command.setTipLocationBarCode(location.getBarCode());
                command.setTipLocationCode(location.getCode());
            }
            // 清除缓存
            pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(operationId, true, locationId);
        }else if(cSRCmd.getIsPicking()){
               command.setIsPicking(true);
               Location location = whLocationDao.findByIdExt(locationId, ouId);
               Long workAreaId = location.getWorkAreaId();
               command.setWorkAreaId(workAreaId);
               command.setBatch(operatorLine.getBatch());
               //添加作业执行明细
               this.addPickingOperationExecLine(userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId, operationId, ouId); 
               //校验作业执行明细
               this.checkOperationExecLine(operationId, ouId);
        }
        log.info("PdaPickingWorkManagerImpl scanSku is end");
        return command;
    }
    
    /***
     * 判断容器状态是否正确
     * @author tangming
     * @param c
     */
    private void judeContainerStatus(Container c){
        log.info("PdaPickingWorkManagerImpl judeContainerStatus is start");
        if (null == c) {
            // 容器信息不存在
            log.error("pdaScanContainer container is null logid: " + logId);
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        // 验证容器Lifecycle是否有效
        if (!c.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            // 容器Lifecycle无效
            log.error("pdaScanContainer container lifecycle error =" + c.getLifecycle() + " logid: " + logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        // 验证容器状态是否是待上架
        if (!(c.getStatus().equals(ContainerStatus.CONTAINER_STATUS_SHEVLED))) {
            log.error("pdaScanContainer container status error =" + c.getStatus() + " logid: " + logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY, new Object[] {c.getStatus()});
        }
        log.info("PdaPickingWorkManagerImpl judeContainerStatus is end");
    }

    /****
     * @author tangming
     * @param outBoundxBoxCode(出库箱)
     * @param turnoverBoxCode(周转箱)
     * @param outerContainerCode(外部容器,托盘)
     * @param insideContainerCode(货箱号)
     */
    private  void addPickingOperationExecLine(Long userId,Long outBoundBoxId,String outBoundBoxCode,Long turnoverBoxId,Long outerContainerId,Long insideContainerId,Long operationId,Long ouId){
        log.info("PdaPickingWorkManagerImpl addPickingOperationExecLine is start");
        OperationLineCacheCommand operLineCacheCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
        if(null == operLineCacheCmd) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Set<Long> shortPikcingOperIds = operLineCacheCmd.getShortPikcingOperIds();  //短拣的作业明细id集合
        Set<Long> pickingOperIds = operLineCacheCmd.getPickingOperIds();   //非短拣的作业明细id集合
        Map<Long,Double> operLineIdToQty =  operLineCacheCmd.getOperLineIdToQty();  //作业明细id对应的拣货sku数量
        for(Long shortOperId:shortPikcingOperIds) {
            WhOperationExecLine whOperationExecLine =  this.getWhOperationExecLine(userId, outBoundBoxCode, turnoverBoxId, outBoundBoxId, operationId, ouId,shortOperId, outerContainerId, insideContainerId);
            Double qty = operLineIdToQty.get(shortOperId);
            whOperationExecLine.setQty(Long.valueOf(qty.toString()));
            whOperationExecLine.setIsShortPicking(true);
            whOperationExecLineDao.insert(whOperationExecLine);
        }
        for(Long operId:pickingOperIds){
            WhOperationExecLine whOperationExecLine =  this.getWhOperationExecLine(userId, outBoundBoxCode, turnoverBoxId, outBoundBoxId, operationId, ouId,operId, outerContainerId, insideContainerId);
            Double qty = operLineIdToQty.get(operId);
            whOperationExecLine.setQty(Long.valueOf(qty.toString()));
            whOperationExecLine.setIsShortPicking(false);
            whOperationExecLineDao.insert(whOperationExecLine);
        }
        log.info("PdaPickingWorkManagerImpl addPickingOperationExecLine is end");
    }
    
    
    /***
     * 返回作业执行明细
     * @param operationId
     * @param ouId
     * @param operationLineId
     * @return
     */
    private WhOperationExecLine getWhOperationExecLine(Long userId,String outBoundBoxCode ,Long turnoverBoxId,Long outBoundBoxId,Long operationId,Long ouId,Long operationLineId,Long outerContainerId,Long insideContainerId) {
        log.info("PdaPickingWorkManagerImpl scanTrunkfulContainer is start");
        List<WhOperationLineCommand> operLineList = pdaPickingWorkCacheManager.cacheOperationLine(operationId, ouId);
        WhOperationLineCommand operLineCommand = null;
        for(WhOperationLineCommand operLinCmd:operLineList){
            Long id = operLinCmd.getId();
            if(operationLineId.equals(id)) {
                operLineCommand = operLinCmd;
                break;
            }
        }
        WhOperationExecLine operationExecLine = new WhOperationExecLine();
        BeanUtils.copyProperties(operLineCommand, operationExecLine);
        if(!StringUtils.isEmpty(outBoundBoxCode) && null != outBoundBoxId) { //判断当前的出库箱和拣货的出库箱是否一致
            if(!outBoundBoxCode.equals(operationExecLine.getUseOutboundboxCode())) {  //不一致的时候
                operationExecLine.setIsUseNew(true);
                operationExecLine.setOldOutboundboxId(operationExecLine.getUseOutboundboxId());
                operationExecLine.setOldOutboundboxCode(operationExecLine.getUseOutboundboxCode());
                operationExecLine.setUseOutboundboxCode(outBoundBoxCode);
                operationExecLine.setUseOutboundboxId(outBoundBoxId);
            }
        }
        if(null != turnoverBoxId) {  //判断当前的周转箱和拣货的周转箱是否一致
            if(turnoverBoxId.equals(operationExecLine.getUseContainerId())) {
                operationExecLine.setIsUseNew(true);
                operationExecLine.setOldContainerId(operationExecLine.getUseContainerId());
                operationExecLine.setUseContainerId(turnoverBoxId);
                operationExecLine.setToInsideContainerId(turnoverBoxId);
            }
        }
        if(null != outerContainerId && null != insideContainerId) {// 整托 ,使用新的托盘
            if(outerContainerId.equals(operationExecLine.getUseOuterContainerId())) {
                operationExecLine.setIsUseNew(true);
                operationExecLine.setOldContainerId(operationExecLine.getUseContainerId());
                operationExecLine.setUseContainerId(insideContainerId);
                operationExecLine.setOldOuterContainerId(operationExecLine.getUseOuterContainerId());
                operationExecLine.setUseOuterContainerId(outerContainerId);
                operationExecLine.setToInsideContainerId(insideContainerId);
                operationExecLine.setToOuterContainerId(outerContainerId);
            }
        } 
        if(null == outerContainerId && null != insideContainerId) {  //整箱
            if(insideContainerId.equals(operationExecLine.getUseContainerId())){
                operationExecLine.setIsUseNew(true);
                operationExecLine.setOldContainerId(operationExecLine.getUseContainerId());
                operationExecLine.setUseContainerId(insideContainerId);
                operationExecLine.setToInsideContainerId(insideContainerId);
            }
        }
        operationExecLine.setId(null);
        operationExecLine.setIsShortPicking(false);
        operationExecLine.setLastModifyTime(new Date());
        operationExecLine.setCreateTime(new Date());
        operationExecLine.setOperatorId(userId);
        log.info("PdaPickingWorkManagerImpl scanTrunkfulContainer is start");
        return operationExecLine;
    }
    
    /***
     * 出库箱或者周转箱/满箱处理
     * @author tangming
     * @param command
     * @return
     */
    public void scanTrunkfulContainer(PickingScanResultCommand  command){
        log.info("PdaPickingWorkManagerImpl scanTrunkfulContainer is start");
        Integer pickingWay = command.getPickingWay();
        Long ouId = command.getOuId();
        if(pickingWay == Constants.PICKING_WAY_THREE) {
            String outBounxBoxCode = command.getOutBounxBoxCode();  
            ContainerCommand c = containerDao.getContainerByCode(outBounxBoxCode, ouId);
            if (null == c) {
                // 容器信息不存在
                log.error("pdaScanContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            // 验证容器Lifecycle是否有效
            if (!c.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                // 容器Lifecycle无效
                log.error("pdaScanContainer container lifecycle error =" + c.getLifecycle() + " logid: " + logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
            }
            // 验证容器状态是否是待上架
            if (!c.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE)) {
                log.error("pdaScanContainer container status error =" +c.getStatus() + " logid: " + logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY, new Object[] {c.getStatus()});
            }
        }
        if(pickingWay == Constants.PICKING_WAY_FOUR) {
            String turnoverBoxCode = command.getTurnoverBoxCode(); // 周转箱
            OutBoundBoxType outBoundBox = outBoundBoxTypeDao.findByCode(turnoverBoxCode, ouId);
            if(null == outBoundBox) {
                throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL );
            }
            // 验证容器Lifecycle是否有效
            if (!outBoundBox.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_STATUS_NO);
            }
        }
        
        log.info("PdaPickingWorkManagerImpl scanTrunkfulContainer is end");
    }
    
    /**
     * 生成作业执行明细--整托整箱 
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @return
     */
    @Override
    public void operatorExecutionLines(PickingScanResultCommand command) {
        if( 1 == command.getPalletPickingMode() ||  2 == command.getPalletPickingMode()){
            List<WhOperationLineCommand> operationLineCommands = whOperationLineManager.findOperationLineByOperationId(command.getOperationId(), command.getOuId());
            for(WhOperationLineCommand operationLineCommand : operationLineCommands){
                WhOperationExecLine operationExecLine = new WhOperationExecLine();
                //将operationLineCommand基本信息复制到operationExecLine中
                BeanUtils.copyProperties(operationLineCommand, operationExecLine);
                //是否短拣
                operationExecLine.setIsShortPicking(false);
                //是否使用新的出库箱/周转箱
                operationExecLine.setIsUseNew(false);
                whOperationExecLineDao.insert(operationExecLine);
            }
        }else{
            // 根据作业ID获取统计信息
            OperatioLineStatisticsCommand statisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(command.getOperationId(),command.getOuId());
            Map<Long, Map<String, Long>> insideMap = statisticsCommand.getInsideSkuAttrIds().get(command.getTipInsideContainerCode());
            for (Map<String, Long> SkuAttr : insideMap.values()) {
                for (String onlySku : SkuAttr.keySet()) {
                    List<Long> demergeLst= this.demergeWorkLineToOnlySku(statisticsCommand.getWorkLineIdToOnlySku().get(onlySku));
                    WhOperationLineCommand operationLineCommand = whOperationLineManager.findLineByWorkLineIdAndId(demergeLst.get(0), demergeLst.get(1));
                    WhOperationExecLine operationExecLine = new WhOperationExecLine();
                    //将operationLineCommand基本信息复制到operationExecLine中
                    BeanUtils.copyProperties(operationLineCommand, operationExecLine);
                    //是否短拣
                    operationExecLine.setIsShortPicking(false);
                    //是否使用新的出库箱/周转箱
                    operationExecLine.setIsUseNew(false);
                    whOperationExecLineDao.insert(operationExecLine);
                }
            }
        }
    }
    
    /**
     * 循环提示内部容器--整箱整托拣货
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @return PickingScanResultCommand
     */
    @Override
    public PickingScanResultCommand wholeCaseForTipInsideContainer(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl wholeCaseForTipInsideContainer is start");
        // 提示内部容器列表
        List<String> tipContainerLst = new ArrayList<String>();
        // 根据作业ID获取统计信息
        OperatioLineStatisticsCommand statisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(command.getOperationId(),command.getOuId());
        // 提示内部容器
        if(null == command.getTipContainer() || 0 == command.getTipContainer().size()){
            // 库位上所有外部容器 
            Set<Long> outerContainerIds = statisticsCommand.getOuterContainerIds().get(command.getLocationId());
            if(null != outerContainerIds && 0 != outerContainerIds.size()){
                //整托                
                for(Long outerContainerId : outerContainerIds){
                    Set<Long> insideContainerIds = statisticsCommand.getOuterToInside().get(outerContainerId);
                    for(Long insideContainerId : insideContainerIds){
                        // 根据容器ID获取容器CODE                
                        Container Container = containerDao.findById(insideContainerId);
                        //推荐托盘列表                
                        tipContainerLst.add(Container.getCode());
                    }
                }
                command.setTipContainer(tipContainerLst);
                command.setTipInsideContainerCode(command.getTipContainer().get(0));
                command.getTipContainer().remove(0);
            }else{
                //整箱                
                Set<Long> insideContainerIds = statisticsCommand.getInsideContainerIds().get(command.getLocationId());
                for(Long insideContainerId : insideContainerIds){
                    // 根据容器ID获取容器CODE                
                    Container Container = containerDao.findById(insideContainerId);
                    //推荐托盘列表                
                    tipContainerLst.add(Container.getCode());
                }
                command.setTipContainer(tipContainerLst);
                command.setTipInsideContainerCode(command.getTipContainer().get(0));
                command.getTipContainer().remove(0);
            }
        }else{
            command.setTipInsideContainerCode(command.getTipContainer().get(0));
            command.getTipContainer().remove(0);
        }
        log.info("PdaPickingWorkManagerImpl wholeCaseForTipInsideContainer is end");
        return command;
    }
       
    /**
     * 循环提示扫描商品--整箱整托拣货
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @return PickingScanResultCommand
     */
    @Override
    public PickingScanResultCommand wholeCaseForTipSku(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl wholeCaseForTipSku is start");
        // 提示商品列表
        List<Long> tipSkuLst = new ArrayList<Long>();
        // 根据作业ID获取统计信息
        OperatioLineStatisticsCommand statisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(command.getOperationId(),command.getOuId());
        // 提示扫描商品
        if(null == command.getTipSkuLst() || 0 == command.getTipSkuLst().size()){
            Set<Long> tipSkus = statisticsCommand.getInsideSkuIds().get(command.getTipInsideContainerCode());
            for(Long tipSku : tipSkus){
                // 提示扫描商品列表                
                tipSkuLst.add(tipSku);
            }
            if(1 == tipSkuLst.size() && 1 == statisticsCommand.getInsideSkuAttrIds().get(command.getTipInsideContainerCode()).get(tipSkuLst.get(0)).size()){
                command.setIsUniqueSkuAttrInside(true);
            }else{
                command.setIsUniqueSkuAttrInside(false);
            }
            command.setTipSkuLst(tipSkuLst);
            command.setSkuId(command.getTipSkuLst().get(0));
            command.setTipSkuQty(statisticsCommand.getInsideSkuQty().get(command.getLocationId()).get(command.getSkuId()));
            /** 内部容器每个sku对应的唯一sku及件数 */
            Map<String, Long> onlySkuAndQty= statisticsCommand.getInsideSkuAttrIds().get(command.getTipInsideContainerId()).get(command.getTipSkuLst().get(0));
            List<String> onlySkuLst = new ArrayList<String>();
            for (String s : onlySkuAndQty.keySet()) {
                onlySkuLst.add(s);
            }
            command.setOnlySkuLst(onlySkuLst);
            WhSkuCommand whSkuCommand = whSkuDao.findWhSkuByIdExt(command.getSkuId(), command.getOuId());
            command.setSkuBarCode(whSkuCommand.getBarCode());
            command.getTipSkuLst().remove(0);
        }else{
            command.setSkuId(command.getTipSkuLst().get(0));
            command.setTipSkuQty(statisticsCommand.getInsideSkuQty().get(command.getLocationId()).get(command.getSkuId()));
            /** 内部容器每个sku对应的唯一sku及件数 */
            Map<String, Long> onlySkuAndQty= statisticsCommand.getInsideSkuAttrIds().get(command.getTipInsideContainerCode()).get(command.getTipSkuLst().get(0));
            List<String> onlySkuLst = new ArrayList<String>();
            for (String s : onlySkuAndQty.keySet()) {
                onlySkuLst.add(s);
            }
            command.setOnlySkuLst(onlySkuLst);
            WhSkuCommand whSkuCommand = whSkuDao.findWhSkuByIdExt(command.getSkuId(), command.getOuId());
            command.setSkuBarCode(whSkuCommand.getBarCode());
            command.getTipSkuLst().remove(0);
        }
        log.info("PdaPickingWorkManagerImpl wholeCaseForTipSku is end");
        return command;
    }
    
    /**
     * 循环扫描当前商品--整箱整托拣货
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand 
     * @return PickingScanResultCommand
     */
    @Override
    public PickingScanResultCommand wholeCaseForTipCurrentSku(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl wholeCaseForTipCurrentSku is start");
        if(null != command.getOnlySkuLst() && 0 < command.getOnlySkuLst().size()){
            // 根据作业ID获取统计信息
            OperatioLineStatisticsCommand statisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(command.getOperationId(),command.getOuId());
            command.setOnlySku(command.getOnlySkuLst().get(0));
            command.setOnlySkuQty(statisticsCommand.getInsideSkuAttrIds().get(command.getTipInsideContainerCode()).get(command.getTipSkuLst().get(0)).get(command.getOnlySku()));
        }
        log.info("PdaPickingWorkManagerImpl wholeCaseForTipCurrentSku is end");
        return command;
    }

    /**
     * 提示托盘--整箱整托拣货 
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @return
     */
    @Override
    public PickingScanResultCommand wholeCaseTipTray(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl wholeCaseTipTray is start");
        // 根据作业ID获取统计信息
        OperatioLineStatisticsCommand statisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(command.getOperationId(),command.getOuId());
        // 库位上所有外部容器 
        Set<Long> outerContainerIds = statisticsCommand.getOuterContainerIds().get(command.getLocationId());
        //提示托盘
        for(Long outerContainerId : outerContainerIds){
            Container c = containerDao.findByIdExt(outerContainerId, command.getOuId());
            //提示外部容器编码
            command.setTipOuterContainerCode(c.getCode());
        }
        log.info("PdaPickingWorkManagerImpl wholeCaseTipTray is end");
        return command;
    }

    /**
     * 提示内部容器--整箱整托拣货 
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @return
     */
    @Override
    public PickingScanResultCommand wholeCaseTipInsideContainer(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl wholeCaseTipInsideContainer is start");
        OperatioLineStatisticsCommand statisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(command.getOperationId(),command.getOuId());
        PickingScanResultCommand resultCommand = new PickingScanResultCommand();
        //根据库位ID提示内部容器列表        
        if(null == command.getTipContainer() && null != statisticsCommand.getOuterToInside().get(command.getTipOuterContainerCode())){
            //提示托盘
            Set<Long> outerContainerIds = statisticsCommand.getOuterContainerIds().get(command.getLocationId());
            List<String> trayLst = new ArrayList<String>(); 
            for(Long outerContainerId : outerContainerIds){
                //外部容器                
                Container Container = containerDao.findById(outerContainerId);
                trayLst.add(Container.getCode());
            }
        }
        if(null != statisticsCommand.getOuterContainerIds()){
            // TODO 有外部容器的情况，整托
        }else{
            // 没有外部容器的情况，整箱
            if(null != statisticsCommand.getInsideContainerIds().get(command.getLocationId())){
                Set<Long> tipInsideContainers = statisticsCommand.getInsideContainerIds().get(command.getLocationId());
                for(Long tipInsideContainer : tipInsideContainers){
                    resultCommand.setTipInsideContainerCode(tipInsideContainer.toString());    
                }
            }
        }
        log.info("PdaPickingWorkManagerImpl wholeCaseTipInsideContainer is end");
        return resultCommand;
    }

    /**
     * 判断是否是SN/残次商品--整箱整托拣货
     * 
     * @author qiming.liu
     * @param 
     * @return
     */
    @Override
    public PickingScanResultCommand wholeCaseIsSn(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl wholeCaseIsSn is start");
        OperatioLineStatisticsCommand statisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(command.getOperationId(),command.getOuId());
        List<Long> demergeLst= this.demergeWorkLineToOnlySku(statisticsCommand.getWorkLineIdToOnlySku().get(command.getOnlySku()));
        
        List<WhSkuInventorySnCommand> whSkuInventorySnCommands = whSkuInventorySnDao.findWhSkuInventoryByUuid(command.getOuId(), demergeLst.get(3).toString());
        
        // 判断是否是SN/残次商品        
        if(0 == whSkuInventorySnCommands.size()){
            command.setIsSkuSn(true);
            List<WhSkuInventoryCommand> whSkuInventoryCommands= whSkuInventoryDao.findInventorysByUuid(command.getOuId(), demergeLst.get(3).toString());
            Boolean isSkuSnOccupation = false;
            for(WhSkuInventorySnCommand whSkuInventorySnCommand : whSkuInventorySnCommands){
                // 判断是否占用SN/残次条码 
                if(whSkuInventoryCommands.get(0).getOccupationCode().equals(whSkuInventorySnCommand.getOccupationCode())){
                    isSkuSnOccupation = true;
                }
            }
            command.setIsSkuSnOccupation(isSkuSnOccupation);
        }else{
            command.setIsSkuSn(false);      
        }
        log.info("PdaPickingWorkManagerImpl wholeCaseIsSn is end");
        return null;
    }
    
    /**
     * 根据库存UUID查找对应SN/残次信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventorySnCommand> findWhSkuInventoryByUuid(Long ouid, String uuid) {
        return whSkuInventorySnDao.findWhSkuInventoryByUuid(ouid, uuid); 
    }
    
    /**
     * 拼写工作明细ID和作业明细ID
     */
    public static String getWorkLineToOnlySku(Long id, Long workLineId, String uuid) {
        String lineToSku = "";
        lineToSku = id + "-" + workLineId + "-" + workLineId;
        return lineToSku;
    }
    
    /**
     * 拆分工作明细ID和作业明细ID
     */
    public static List<Long> demergeWorkLineToOnlySku(String skuAttrId) {
        List<Long> lineToSku = new ArrayList<Long>();
        String[] values = skuAttrId.split("-");
        for(int i=0; i<values.length; i++){
            lineToSku.add(Long.valueOf(values[i]));    
        }
        return lineToSku;
    }
    
    /****
     * 校验作业执行明细
     * @author tangming
     * @param operationId
     * @param ouId
     * @return
     */
    private void checkOperationExecLine(Long operationId,Long ouId){
        log.info("PdaPickingWorkManagerImpl checkOperationExecLine is start");
        List<WhOperationExecLine> operationExeclineList = whOperationExecLineDao.checkOperationExecLine(operationId, ouId);
        for(WhOperationExecLine operationExecline:operationExeclineList) {
            Long workLineId = operationExecline.getWorkLineId();
            Long skuId = operationExecline.getSkuId();
            Long qty = operationExecline.getQty();
            Long fromLocationId = operationExecline.getFromLocationId();
            Long fromOuterContainerId = operationExecline.getFromOuterContainerId();
            Long fromInsideContainerId = operationExecline.getFromInsideContainerId();
            if(!(workLineId == null && skuId == 0 && qty == 0 && fromLocationId == null && fromOuterContainerId == null && fromInsideContainerId == null)) {
                throw new BusinessException(ErrorCodes.CHECK_OPERTAION_EXEC_LINE_DIFF);
            }
        }
        log.info("PdaPickingWorkManagerImpl checkOperationExecLine is end");
    }
    
    
    /***
     * 查询库存sn残次信息
     * @param sn
     * @param defectWareBarCode
     * @return
     */
    public PickingScanResultCommand judgeIsOccupationCode(PickingScanResultCommand command){
        log.info("PdaPickingWorkManagerImpl judgeIsOccupationCode is start");
        WhSkuInventoryCommand invSkuCmd = new WhSkuInventoryCommand();
        invSkuCmd.setInvStatus(Long.valueOf(command.getSkuInvStatus())); // 库存状态
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            invSkuCmd.setMfgDate(sdf.parse(command.getSkuMfgDate()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR); 
        }
        try {
            invSkuCmd.setExpDate(sdf.parse(command.getSkuExpDate()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR); 
        }
        invSkuCmd.setInvType(command.getSkuInvType());
        invSkuCmd.setBatchNumber(command.getBatchNumber());
        invSkuCmd.setCountryOfOrigin(command.getSkuOrigin());
        invSkuCmd.setInvAttr1(command.getSkuInvAttr1());
        invSkuCmd.setInvAttr2(command.getSkuInvAttr2());
        invSkuCmd.setInvAttr3(command.getSkuInvAttr3());
        invSkuCmd.setInvAttr4(command.getSkuInvAttr4());
        invSkuCmd.setInvAttr5(command.getSkuInvAttr5());
        String skuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd);
        Long locationId = command.getLocationId();
        String insideContainerCode = command.getInsideContainerCode();
        Long ouId = command.getOuId();
        ContainerCommand c = containerDao.getContainerByCode(insideContainerCode, ouId);
        if(null == c) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); 
        }
        Long insideContainerId = c.getId();
        String uuid = "";
        if(!StringUtils.isEmpty(command.getSkuSn()) || !StringUtils.isEmpty(command.getSkuDefect())) {  //是sn残次信息
            //判断是否占用sn/残次条码
            List<WhSkuInventoryCommand> whSkuInvCmdList = whSkuInventoryDao.findWhSkuInvCmdByLocation(ouId,locationId);
            for(WhSkuInventoryCommand skuInvCmd:whSkuInvCmdList) {
                String skuInvAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
                if(insideContainerId.equals(skuInvCmd.getInsideContainerId()) && skuAttrIds.equals(skuInvAttrIds)){
                     uuid = skuInvCmd.getUuid();
                     break;
                }
            }
            String snCode = command.getSkuSn();
            if(StringUtils.isEmpty(snCode)) {
                snCode = command.getSkuBarCode();
            }
            WhSkuInventorySn skuInvSn = whSkuInventorySnDao.findWhSkuInventoryByUuidAndSnOrDefectWareBarcode(ouId, uuid, snCode);
            if(null == skuInvSn) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_NULL); 
            }
            if(!StringUtils.isEmpty(skuInvSn.getOccupationCode())) {  //占用sn或者残次条码
                //提示sn/或者残次条码
                if(!StringUtils.isEmpty(skuInvSn.getDefectWareBarcode())) {//提示残次条码
                    command.setIsTipSkuDefect(true);
                    command.setTipSkuDefect(skuInvSn.getDefectWareBarcode());
                }else{ //提示sn
                    command.setIsTipSkuSn(true); 
                    command.setTipSkuSn(skuInvSn.getSn()); 
                }
            }else{
                //扫描sn/残次条码
                if(!StringUtils.isEmpty(skuInvSn.getDefectWareBarcode())) {//提示残次条码
                   command.setIsNeedScanDefect(true);
                }else{ //提示sn
                    command.setIsNeedScanSkuSn(true); 
                }
            }
        }
        log.info("PdaPickingWorkManagerImpl judgeIsOccupationCode is end");
        return command;
    }

    /****
     * 确定补货方式和占用模型
     * 
     * @author qiming.liu
     * @param  whWork
     * @param  ouId
     * @return
     */
    @Override
    public PickingScanResultCommand getPickingForGroup(WhWork whWork, Long ouId) {
        // 根据工作id获取作业信息        
        WhOperationCommand whOperationCommand = whOperationManager.findOperationByWorkId(whWork.getId(), ouId);
        // 统计分析工作及明细并缓存
        this.getOperatioLineForGroup(whOperationCommand);
        // 获取缓存中的统计分析数据        
        OperatioLineStatisticsCommand statisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(whOperationCommand.getId(), whOperationCommand.getOuId());
        //返回结果初始化        
        PickingScanResultCommand pickingScanResultCommand = new PickingScanResultCommand();
        //作业id        
        pickingScanResultCommand.setOperationId(whOperationCommand.getId());//是否需要仓库id
        //捡货方式        
        if(whOperationCommand.getIsWholeCase() == false && statisticsCommand.getOuterContainers().size() > 0 && statisticsCommand.getOutbounxBoxs().size() == 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_ONE);
        }else if(whOperationCommand.getIsWholeCase() == false && statisticsCommand.getOuterContainers().size() > 0 && statisticsCommand.getOutbounxBoxs().size() > 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_TWO);
        }else if(whOperationCommand.getIsWholeCase() == false && statisticsCommand.getOuterContainers().size() == 0 && statisticsCommand.getOutbounxBoxs().size() > 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_THREE);
        }else if(whOperationCommand.getIsWholeCase() == false && statisticsCommand.getOuterContainers().size() == 0 && statisticsCommand.getTurnoverBoxs().size() > 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_FOUR);
        }else if(whOperationCommand.getIsWholeCase() == true && statisticsCommand.getPallets().size() > 0 && statisticsCommand.getPallets().size() > 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_FIVE);
        }else if(whOperationCommand.getIsWholeCase() == true && statisticsCommand.getPallets().size() == 0 && statisticsCommand.getPallets().size() > 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_SIX);
        }
        //库存占用模型
        if(statisticsCommand.getOuterContainerIds().size() > 0 && statisticsCommand.getInsideContainerIds().size() == 0 && statisticsCommand.getInsideSkuIds().size() == 0){
            //仅占用托盘内商品            
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_ONE);
        }else if(statisticsCommand.getOuterContainerIds().size() == 0 && statisticsCommand.getInsideContainerIds().size() > 0 && statisticsCommand.getInsideSkuIds().size() == 0){
            //仅占用货箱内商品
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_TWO);
        }else if(statisticsCommand.getOuterContainerIds().size() == 0 && statisticsCommand.getInsideContainerIds().size() == 0 && statisticsCommand.getInsideSkuIds().size() > 0){
            //仅占用库位上散件商品
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_THREE);
        }else{//有托盘||有货箱（无外部容器）||散件
            //混合占用
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_FOUR);
        }
        return pickingScanResultCommand;
    }
    
    /***
     * pda拣货完成
     * @param operationId
     * @param workCode
     * @param ouId
     * @param userId
     * @param locationId
     * @return
     */
    public void pdaPickingFinish(PickingScanResultCommand command,Boolean isTabbInvTotal){
        log.info("PdaPickingWorkManagerImpl pdaPickingFinish is start");
        Long operationId = command.getOperationId();
        String workCode = command.getWorkBarCode();
        Long ouId = command.getOuId();
        Long userId = command.getUserId();
        Long locationId = command.getLocationId();
        //生成容器/出库箱库存               
        whSkuInventoryManager.pickingAddContainerInventory(operationId, ouId, isTabbInvTotal, userId);
       //更新工作及作业状态
        pdaPickingWorkCacheManager.pdaPickingUpdateStatus(operationId, workCode, ouId, userId);
        //清除缓存
        pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(operationId, false, locationId);
        log.info("PdaPickingWorkManagerImpl pdaPickingFinish is end");
    }
    
    /****
     * 生成作业执行明细并跳转页面
     * 
     * @author qiming.liu
     * @param  PickingScanResultCommand
     * @param  WhSkuCommand
     * @param  isTabbInvTotal
     * @return
     */
    public PickingScanResultCommand wholeCaseOperationExecLine (PickingScanResultCommand  command, WhSkuCommand skuCmd, Boolean isTabbInvTotal){
        log.info("PdaPickingWorkManagerImpl scanSku is start");
        Long operationId = command.getOperationId();
        Long functionId = command.getFunctionId();
        Long locationId = command.getLocationId();   
        String outBoundBoxCode = command.getOutBounxBoxCode();
        Long userId = command.getUserId();
        Integer pickingWay = command.getPickingWay();
        Long ouId = command.getOuId();
        Long skuId = command.getSkuId();
        String skuBarCode = command.getSkuBarCode();
        String insideContainerCode = command.getInsideContainerCode();
        String outerContainerCode = command.getOuterContainerCode(); 
        String turnoverBoxCode = command.getTurnoverBoxCode();   //周转箱
        Long outBoundBoxId = command.getOutBoundBoxId();   //出库箱id
        Boolean isTrunkful = command.getIsTrunkful();  //是否满箱
        ContainerCommand turnoverBoxCmd = containerDao.getContainerByCode(turnoverBoxCode, ouId);
        if(null == turnoverBoxCmd) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        Long turnoverBoxId = turnoverBoxCmd.getId();
        Long insideContainerId = null;
        ContainerCommand insideContainerCmd = null;
        if(!StringUtil.isEmpty(insideContainerCode)) {
            insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
            if(null == insideContainerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = insideContainerCmd.getId();
        }
        ContainerCommand outerContainerCmd = null;
        Long outerContainerId = null;
        if(!StringUtil.isEmpty(outerContainerCode)) {
            outerContainerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
            if(null == outerContainerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerContainerId = outerContainerCmd.getId();
        }
        Double scanQty = skuCmd.getScanSkuQty(); // 扫描的商品数量
        if (null == scanQty || scanQty.longValue() < 1) {
            log.error("scan sku qty is valid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
        }
        if (StringUtils.isEmpty(skuBarCode)) {
            log.error("sku is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        WhSkuInventoryCommand invSkuCmd = new WhSkuInventoryCommand();
        invSkuCmd.setSkuId(command.getSkuId()); 
        invSkuCmd.setInvType(command.getSkuInvType());
        invSkuCmd.setBatchNumber(command.getBatchNumber());
        try {
            invSkuCmd.setMfgDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(command.getSkuMfgDate()));
            invSkuCmd.setExpDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(command.getSkuExpDate()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        invSkuCmd.setCountryOfOrigin(command.getSkuOrigin());
        invSkuCmd.setInvAttr1(command.getSkuInvAttr1());
        invSkuCmd.setInvAttr2(command.getSkuInvAttr2());
        invSkuCmd.setInvAttr3(command.getSkuInvAttr3());
        invSkuCmd.setInvAttr4(command.getSkuInvAttr4());
        invSkuCmd.setInvAttr5(command.getSkuInvAttr5());
        List<InventoryStatus> listInventoryStatus = inventoryStatusManager.findAllInventoryStatus();
        String statusValue = command.getSkuInvStatus();
        // 库存状态
        if (!StringUtils.isEmpty(statusValue)) {
            for (InventoryStatus inventoryStatus : listInventoryStatus) {
                if (statusValue.equals(inventoryStatus.getName())) invSkuCmd.setInvStatus(inventoryStatus.getId()); // 库存状态
                break;
            }
        }
//        String skuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd);
//        Long operationLineId = pdaPickingWorkCacheManager.cachePickingOperLineId(operationId, skuAttrIds, outerContainerId, insideContainerId, locationId, ouId,isShortPicking,scanQty);
        WhFunctionPicking picking = whFunctionPickingDao.findByFunctionIdExt(ouId, functionId);
        if (null == picking) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == picking.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarCode, logId); // 获取对应的商品数量,key值是sku
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> insideSkuIds  = operatorLine.getInsideSkuIds();  //内部容器内所有的sku
        Map<Long, Set<Long>> operLocSkuIds = operatorLine.getSkuIds();  //库位上所有sku(sku不在任何容器内)
        Set<Long> locSkuIds = operLocSkuIds.get(locationId);
        Map<Long, Set<Long>> locOuterContainerIds = operatorLine.getOuterContainerIds();
        Set<Long> outerContainerIds = locOuterContainerIds.get(locationId);  //当前库位上所有外部容器集合
        Set<Long> icSkuIds = insideSkuIds.get(insideContainerId);
        List<Long> locationIds = operatorLine.getLocationIds();
        Map<Long, Map<Long, Long>> operLocSkuQty = operatorLine.getSkuQty();
        Map<Long,Long> locSkuQty = operLocSkuQty.get(operationId);
        Map<Long, Map<Long, Long>> insideSkuQty = operatorLine.getInsideSkuQty(); //内部容器每个sku总件数
        Map<Long,Long> insideContainerSkuIdsQty = insideSkuQty.get(insideContainerId);
        Map<Long, Set<Long>> operLocInsideContainerIds = operatorLine.getInsideContainerIds();//库位上所有的内部容器(无外部容器情况)
        Set<Long> locInsideContainerIds = operLocInsideContainerIds.get(locationId);
        Map<Long, Set<Long>>  insideContainerSkuIds = operatorLine.getInsideSkuIds();   //库位上内部容器对应的所有sku
        Map<Long, Set<Long>> outerToInsideIds = operatorLine.getOuterToInside(); //(库位上有外部容器的内部容器)
        Map<Long, Map<String, Set<String>>>    skuAttrIdsSnDefect =  operatorLine.getSkuAttrIdsSnDefect();   //库位上每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>>     insideSkuAttrIdsSnDefect = operatorLine.getInsideSkuAttrIdsSnDefect();  //内部容器每个唯一sku对应的所有sn及残次条码
        Set<Long> insideContainerIds = outerToInsideIds.get(locationId);
        boolean isSkuExists = false;
        Integer cacheSkuQty = 1;
        Integer icSkuQty = 1;
        for (Long cacheId : cacheSkuIdsQty.keySet()) {
            if (icSkuIds.contains(cacheId)) {
                isSkuExists = true;
            }
            if (true == isSkuExists) {
                skuId = cacheId;
                cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                icSkuQty = (null == insideContainerSkuIdsQty.get(cacheId) ? 1 : insideContainerSkuIdsQty.get(cacheId).intValue());
                break;
            }
        }
        if (false == isSkuExists) {
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]",  skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {});
        }
        if (cacheSkuQty > 1 && cacheSkuQty <= icSkuQty) {
            if (0 != (icSkuQty % cacheSkuQty)) {
                // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
                log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
                throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
            }
        }
        if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {
            if (0 != new Double("1").compareTo(scanQty)) {
                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarCode, logId);
                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
            }
        }
        skuCmd.setId(skuId);
        skuCmd.setScanSkuQty(scanQty * cacheSkuQty);// 可能是多条码
        CheckScanResultCommand cSRCmd =  pdaPickingWorkCacheManager.pdaPickingyCacheSkuAndCheckContainer(scanPattern ,locationIds, locSkuQty, locationId, locSkuIds, outerContainerIds, outerContainerCmd, operationId, insideContainerSkuIdsQty, insideContainerSkuIds, insideContainerIds, locInsideContainerIds, insideContainerCmd, skuCmd);
        if(cSRCmd.getIsNeedScanSku()) {
            if(pickingWay == Constants.PICKING_WAY_THREE && isTrunkful) { //是否出库箱满箱
                //跳转到扫描出库箱页面
                command.setIsUserNewContainer(true);
                return command;
            }
            if(pickingWay == Constants.PICKING_WAY_FOUR && isTrunkful) {  //周转箱是否满箱
                //跳转到扫描周转箱页面
                command.setIsUserNewContainer(true);
                return command;
            }
            command.setIsNeedTipSku(true);
            Set<Long> skuIds = null;
            if(null == insideContainerId) { //sku直接放在库位上
                skuIds = locSkuIds;
            }else{
                skuIds = insideSkuIds.get(insideContainerId);
            }
            CheckScanResultCommand cSRCommand = pdaPickingWorkCacheManager.pdaPickingTipSku(skuIds, operationId,locationId,ouId, insideContainerId,skuAttrIdsSnDefect,insideSkuAttrIdsSnDefect);
            if(cSRCommand.getIsNeedScanSku()){  //此货箱的sku，还没有扫描完毕
                String skuAttrId = cSRCmd.getTipSkuAttrId();   //提示唯一的sku
                WhSkuCommand whSkuCmd = whSkuDao.findWhSkuByIdExt(SkuCategoryProvider.getSkuId(skuAttrId), ouId);
                if(null == whSkuCmd) {
                    throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                }
                command.setSkuBarCode(whSkuCmd.getBarCode());   // 继续提示sku条码
            }else{
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
        }else if(cSRCmd.getIsNeedTipInsideContainer()){  //提示下一个货箱
            Set<Long> insideIds = null;
            if(null == outerContainerCmd) {
                insideIds = locInsideContainerIds;
            }else{
                insideIds = outerToInsideIds.get(outerContainerId);
            }
            CheckScanResultCommand cSRCommand = pdaPickingWorkCacheManager.pdaPickingTipInsideContainer(insideIds, locationId);
            if(cSRCommand.getIsNeedTipInsideContainer()) {
                Long tipInsideContainerId = cSRCmd.getTipiInsideContainerId();
                Container ic = containerDao.findByIdExt(tipInsideContainerId, ouId);
                this.judeContainerStatus(ic);
                command.setTipInsideContainerCode(ic.getCode());
                command.setIsNeedTipInsideContainer(true);
            }else{
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
        }else if(cSRCmd.getIsNeedTipOutContainer()) { // 提示下一个外部容器
            CheckScanResultCommand cSRCommand =  pdaPickingWorkCacheManager.pdaPickingTipOuterContainer(outerContainerIds, locationId);
            if(cSRCommand.getIsNeedTipOutContainer()) {
                Long outerId = cSRCmd.getTipOuterContainerId();
                //判断外部容器
                Container c = containerDao.findByIdExt(outerId, ouId);
                this.judeContainerStatus(c);
                //提示外部容器编码
                command.setTipOuterContainerCode(c.getCode());
            }else{
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR); 
            }
        }else if(cSRCmd.getIsNeedTipLoc()) {  //提示下一个库位
            CheckScanResultCommand cSRCommand = pdaPickingWorkCacheManager.tipLocation(operationId, locationIds);
            if(cSRCommand.getIsNeedTipLoc()) {
                Long locId = cSRCmd.getTipLocationId();
                Location location = whLocationDao.findByIdExt(locId, ouId);
                if(null == location) {
                    throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
                }
                command.setTipLocationBarCode(location.getBarCode());
                command.setTipLocationCode(location.getCode());
            }
            // 清除缓存
            pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(operationId, true, locationId);
        }else if(cSRCmd.getIsPicking()){
               command.setIsPicking(true);
               //添加作业执行明细
               this.addPickingOperationExecLine(userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId, operationId, ouId); 
               //校验作业执行明细
               this.checkOperationExecLine(operationId, ouId);
               //生成容器/出库箱库存               
               whSkuInventoryManager.pickingAddContainerInventory(operationId, ouId, isTabbInvTotal, userId);
        }
        log.info("PdaPickingWorkManagerImpl scanSku is end");
        return command;
    }
    
    
    /***
     * 缓存库位
     * @param operationId
     * @param locationCode
     * @param ouId
     */
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

    /***
     * 拣货取消流程
     * @param outerContainerId
     * @param insideContainerId
     * @param cancelPattern
     * @param pickingType
     * @param locationId
     * @param ouId
     */
    public void cancelPattern(String carCode,String outerContainerCode,String insideContainerCode, int cancelPattern,int pickingWay,Long locationId,Long ouId,Long operationId,Long tipSkuId){
        
        Long carId = null;
        if(!StringUtils.isEmpty(carCode)) {
            ContainerCommand cmd =  containerDao.getContainerByCode(carCode, ouId);
            carId = cmd.getId();
        }
        Long outerContainerId = null;
        if(!StringUtils.isEmpty(outerContainerCode)){
            ContainerCommand cmd =  containerDao.getContainerByCode(outerContainerCode, ouId);
            if(null == cmd) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerContainerId = cmd.getId();
        }
        Long insideContainerId = null;
        if(!StringUtils.isEmpty(insideContainerCode)){
            ContainerCommand cmd = containerDao.getContainerByCode(insideContainerCode, ouId);
            if(null == cmd) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = cmd.getId();
        }
        pdaPickingWorkCacheManager.cancelPattern(carId, outerContainerId, insideContainerId, cancelPattern, pickingWay, locationId, ouId,operationId,tipSkuId);
    }
    
    /***
     * 有小车有出库箱的情况下(获取货格号)
     * @param operationId
     * @param outBounxBoxCode
     * @return
     */
    public Integer getUseContainerLatticeNo(Long operationId,String outBounxBoxCode){
        Integer useContainerLatticeNo = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Integer, String> carStockToOutgoingBox =  operatorLine.getCarStockToOutgoingBox();
        Set<Integer> keys = carStockToOutgoingBox.keySet();
        for(Integer key:keys){
            String value = carStockToOutgoingBox.get(key);
            if(outBounxBoxCode.equals(value)){
                useContainerLatticeNo = key;
                break;
            }
        }
        return useContainerLatticeNo;
    }
}
