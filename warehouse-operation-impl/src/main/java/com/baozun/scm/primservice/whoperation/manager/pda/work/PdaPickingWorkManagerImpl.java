package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionPickingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkOperDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
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
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPicking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkOper;
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
    private WhSkuInventoryDao WhSkuInventoryDao;
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
    public PickingScanResultCommand getOperatioLineForGroup(WhWork whWork, Long ouId) {
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
        Map<String, Set<String>> skuAttrIdsContainerLattice = new HashMap<String, Set<String>>();
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
        Map<String, Set<String>> insideSkuAttrIdsContainerLattice = new HashMap<String, Set<String>>();
        
        //根据工作id获取作业信息        
        WhOperationCommand whOperationCommand = whOperationManager.findOperationByWorkId(whWork.getId(), ouId);
        //根据作业id获取作业明细信息  
        List<WhOperationLineCommand> operationLineList = whOperationLineManager.findOperationLineByOperationId(whOperationCommand.getId(), ouId);
        for(WhOperationLineCommand operationLine : operationLineList){
            //流程相关统计信息 
            if(whOperationCommand.getIsWholeCase() == false){
                // 所有小车
                outerContainers.add(operationLine.getFromOuterContainerId());
                // 所有出库箱
                outbounxBoxs.add(operationLine.getUseOutboundboxCode());
                // 小车货格与出库箱对应关系
                carStockToOutgoingBox.put(operationLine.getUseContainerLatticeNo(), operationLine.getUseOutboundboxCode());
                // 所有周转箱
                turnoverBoxs.add(operationLine.getFromInsideContainerId());
            }else{
                // 所有托盘
                pallets.add(operationLine.getFromOuterContainerId());
                // 所有货箱
                containers.add(operationLine.getFromInsideContainerId());
            }
            // 临时set 
            Set<Long> temporaryL = new HashSet<Long>();
            Set<String> temporaryS = new HashSet<String>();
            Map<Long, Long> temporaryllMap = new HashMap<Long, Long>();
            Map<String, Set<String>> temporaryssetMap = new HashMap<String, Set<String>>();
            Map<Long, Map<String, Long>> temporarylmMap = new HashMap<Long, Map<String, Long>>();
            //获取内部容器唯一sku
            String onlySku = SkuCategoryProvider.getSkuAttrIdByOperationLine(operationLine);
            //获取库位ID            
            locationIds.add(operationLine.getFromLocationId());
            //获取外部容器 
            if(outerContainerIds.get(operationLine.getFromLocationId() ) != null){
                outerContainerIds.get(operationLine.getFromLocationId()).add(operationLine.getFromOuterContainerId());
            }else{
                temporaryL.add(operationLine.getFromOuterContainerId());
                outerContainerIds.put(operationLine.getFromLocationId(), temporaryL);
                temporaryL.clear();
            }
            //获取内部容器（无外部容器情况）
            if(null == operationLine.getFromOuterContainerId()){
                //无外部容器情况                
                if(insideContainerIds.get(operationLine.getFromLocationId() ) != null){
                    insideContainerIds.get(operationLine.getFromLocationId()).add(operationLine.getFromInsideContainerId());
                }else{
                    temporaryL.add(operationLine.getFromInsideContainerId());
                    insideContainerIds.put(operationLine.getFromInsideContainerId(), temporaryL);
                    temporaryL.clear();
                }
            }
            //sku不在任何容器内            
            if(null == operationLine.getFromOuterContainerId() && null == operationLine.getFromInsideContainerId() && null == operationLine.getUseOutboundboxCode()){
                //获取sku（sku不在任何容器内）
                if(skuIds.get(operationLine.getFromLocationId() ) != null){
                    skuIds.get(operationLine.getFromLocationId()).add(operationLine.getSkuId());
                }else{
                    temporaryL.add(operationLine.getSkuId());
                    skuIds.put(operationLine.getFromInsideContainerId(), temporaryL);
                    temporaryL.clear();
                }
                //获取每个sku总件数
                if(null != skuQty.get(operationLine.getFromLocationId())){
                    temporaryllMap = skuQty.get(operationLine.getFromLocationId());
                    if(null != temporaryllMap.get(operationLine.getSkuId())){
                        Long qty =  temporaryllMap.get(operationLine.getSkuId()) + operationLine.getQty().longValue();
                        temporaryllMap.put(operationLine.getSkuId(), qty);
                    }else{
                        temporaryllMap.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                    }
                    skuQty.put(operationLine.getFromLocationId(), temporaryllMap);
                    temporaryllMap.clear();
                }else{
                    temporaryllMap.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                    skuQty.put(operationLine.getFromLocationId(), temporaryllMap);
                    temporaryllMap.clear();
                }
                //获取每个sku对应的唯一sku及件数
                if(null != skuAttrIds.get(operationLine.getFromLocationId())){
                    temporarylmMap = skuAttrIds.get(operationLine.getFromLocationId());
                    if(null != temporarylmMap.get(operationLine.getSkuId())){
                        Map<String, Long> skuAttrIdsQty = temporarylmMap.get(operationLine.getSkuId());
                        if (null != skuAttrIdsQty.get(onlySku)) {
                            skuAttrIdsQty.put(onlySku, skuAttrIdsQty.get(onlySku) + operationLine.getQty().longValue());
                        } else {
                            skuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                        }
                        temporarylmMap.put(operationLine.getSkuId(), skuAttrIdsQty);
                     }else{
                         Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                         insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                         temporarylmMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                     }
                    skuAttrIds.put(operationLine.getFromLocationId(), temporarylmMap);
                    temporarylmMap.clear();
                }else{
                    Map<String, Long> skuAttrIdsQty = new HashMap<String, Long>();
                    skuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                    temporarylmMap.put(operationLine.getSkuId(), skuAttrIdsQty);
                    skuAttrIds.put(operationLine.getFromLocationId(), temporarylmMap);
                    temporarylmMap.clear();
                }
                // 库位上每个唯一sku对应的所有sn及残次条码---------------------------------------------暂时不做，无法根据作业明细确定t_wh_sku_inventory表信息
                // 库位上每个唯一sku对应的货格（is_whole_case=0&&有小车&&库位上sku不在任何容器内）
                if(whOperationCommand.getIsWholeCase() == false && null != operationLine.getFromOuterContainerId()){
                    if(null != skuAttrIdsContainerLattice.get(onlySku)){
                        skuAttrIdsContainerLattice.get(onlySku).add(operationLine.getUseOutboundboxCode());
                    }else{
                        temporaryS.add(operationLine.getUseOutboundboxCode());
                        skuAttrIdsContainerLattice.put(onlySku, temporaryS);
                        temporaryS.clear();
                    }
                }
            }
            // 存在外部容器并且有对应内部容器
            if(null != operationLine.getFromOuterContainerId() && null != operationLine.getFromInsideContainerId()){
                // 外部容器对应所有内部容器
                if(outerToInside.get(operationLine.getFromOuterContainerId()) != null){
                    outerToInside.get(operationLine.getFromOuterContainerId()).add(operationLine.getFromInsideContainerId());
                }else{
                    temporaryL.add(operationLine.getFromInsideContainerId());
                    outerToInside.put(operationLine.getFromOuterContainerId(), temporaryL);
                    temporaryL.clear();
                }
                //内部容器对应所有sku
                if(insideSkuIds.get(operationLine.getFromInsideContainerId()) != null){
                    insideSkuIds.get(operationLine.getFromInsideContainerId()).add(operationLine.getSkuId());
                }else{
                    temporaryL.add(operationLine.getSkuId());
                    insideSkuIds.put(operationLine.getFromInsideContainerId(), temporaryL);
                    temporaryL.clear();
                }
                //内部容器每个sku总件数
                if(null != insideSkuQty.get(operationLine.getFromInsideContainerId())){
                    temporaryllMap = insideSkuQty.get(operationLine.getFromInsideContainerId());
                    if(null != temporaryllMap.get(operationLine.getSkuId())){
                        Long insQty =  temporaryllMap.get(operationLine.getSkuId()) + operationLine.getQty().longValue();
                        temporaryllMap.put(operationLine.getSkuId(), insQty);
                     }else{
                         temporaryllMap.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                     }
                    insideSkuQty.put(operationLine.getFromInsideContainerId(), temporaryllMap);
                    temporaryllMap.clear();
                }else{
                    temporaryllMap.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                    insideSkuQty.put(operationLine.getFromInsideContainerId(), temporaryllMap);
                    temporaryllMap.clear();
                }
                //内部容器每个sku对应的唯一sku及件数
                if(null != insideSkuAttrIds.get(operationLine.getFromInsideContainerId())){
                    temporarylmMap = insideSkuAttrIds.get(operationLine.getFromInsideContainerId());
                    if(null != temporarylmMap.get(operationLine.getSkuId())){
                        Map<String, Long> insideSkuAttrIdsQty = temporarylmMap.get(operationLine.getSkuId());
                        if (null != insideSkuAttrIdsQty.get(onlySku)) {
                            insideSkuAttrIdsQty.put(onlySku, insideSkuAttrIdsQty.get(onlySku) + operationLine.getQty().longValue());
                        } else {
                            insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                        }
                        temporarylmMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                     }else{
                         Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                         insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                         temporarylmMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                     }
                    insideSkuAttrIds.put(operationLine.getFromInsideContainerId(), temporarylmMap);
                    temporarylmMap.clear();
                }else{
                    Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                    insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                    temporarylmMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                    insideSkuAttrIds.put(operationLine.getFromInsideContainerId(), temporarylmMap);
                    temporarylmMap.clear();
                }
                //内部容器每个唯一sku对应的所有sn及残次条码---------------------------------------------暂时不做，无法根据作业明细确定t_wh_sku_inventory表信息
                //内部容器每个唯一sku对应的货格（is_whole_case=0&&有小车）
                if(whOperationCommand.getIsWholeCase() == false && null != operationLine.getFromOuterContainerId()){
                    if(null != insideSkuAttrIdsContainerLattice.get(onlySku)){
                        insideSkuAttrIdsContainerLattice.get(onlySku).add(operationLine.getUseOutboundboxCode());
                    }else{
                        temporaryS.add(operationLine.getUseOutboundboxCode());
                        insideSkuAttrIdsContainerLattice.put(onlySku, temporaryS);
                        temporaryS.clear();
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
        sortLocationIds = locationManager.sortByIds(locationIds, ouId);
        // 所有库位
        statisticsCommand.setLocationIds(sortLocationIds);
        // 库位上所有外部容器
        statisticsCommand.setOuterContainerIds(outerContainerIds);
        // 库位上所有内部容器 
        statisticsCommand.setInsideContainerIds(insideContainerIds);
        // 库位上所有sku
        statisticsCommand.setSkuIds(insideSkuIds);
        // 库位上每个sku总件数
        statisticsCommand.setSkuQty(skuQty);
        // 库位上每个sku对应的唯一sku及件数
        statisticsCommand.setSkuAttrIds(insideSkuAttrIds);
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
        //缓存统计分析结果        
        pdaPickingWorkCacheManager.operatioLineStatisticsRedis(whOperationCommand.getId(), statisticsCommand);
        
        //返回结果        
        PickingScanResultCommand pickingScanResultCommand = new PickingScanResultCommand();
        //作业id        
        pickingScanResultCommand.setOperationId(whOperationCommand.getId());//是否需要仓库id
        
        //捡货方式        
        if(whOperationCommand.getIsWholeCase() == false && outerContainers.size() > 0 && outbounxBoxs.size() == 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_ONE);
        }else if(whOperationCommand.getIsWholeCase() == false && outerContainers.size() > 0 && outbounxBoxs.size() > 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_TWO);
        }else if(whOperationCommand.getIsWholeCase() == false && outerContainers.size() == 0 && outbounxBoxs.size() == 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_THREE);
        }else if(whOperationCommand.getIsWholeCase() == false && outerContainers.size() == 0 && turnoverBoxs.size() == 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_FOUR);
        }else if(whOperationCommand.getIsWholeCase() == true && pallets.size() > 0 && pallets.size() > 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_FIVE);
        }else if(whOperationCommand.getIsWholeCase() == true && pallets.size() == 0 && pallets.size() > 0){
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_SIX);
        }
       
        //库存占用模型
        if(outerContainerIds.size() > 0 && insideContainerIds.size() == 0 && insideSkuIds.size() == 0){
            //仅占用托盘内商品            
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_ONE);
        }else if(outerContainerIds.size() == 0 && insideContainerIds.size() > 0 && insideSkuIds.size() == 0){
            //仅占用货箱内商品
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_TWO);
        }else if(outerContainerIds.size() == 0 && insideContainerIds.size() == 0 && insideSkuIds.size() > 0){
            //仅占用库位上散件商品
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_THREE);
        }else{//有托盘||有货箱（无外部容器）||散件
            //混合占用
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_FOUR);
        }
        
        return pickingScanResultCommand;
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
        if(pickingWay == Constants.PICKING_WAY_ONE) { //使用外部容器(小车) 无出库箱拣货流程
            String tipOuterContainer = pdaPickingWorkCacheManager.pdaPickingWorkTipOutContainer(operationId,ouId);
            pSRcmd.setTipOuterContainer(tipOuterContainer);  //提示小车
        }
        if(pickingWay == Constants.PICKING_WAY_TWO) { //使用外部(小车)，有出库箱拣货流程
            String tipOuterContainer = pdaPickingWorkCacheManager.pdaPickingWorkTipOutContainer(operationId,ouId);
            pSRcmd.setTipOuterContainer(tipOuterContainer);  //提示小车
        }
        if(pickingWay == Constants.PICKING_WAY_THREE) { //使用出库箱拣货流程
            String tipOutBoundBox = pdaPickingWorkCacheManager.pdaPickingWorkTipoutboundBox(operationId,ouId);
            pSRcmd.setOutBoundCode(tipOutBoundBox);
        }
        if(pickingWay == Constants.PICKING_WAY_FOUR) {  //使用周转箱拣货流程
            String turnoverBox = pdaPickingWorkCacheManager.pdaPickingWorkTipTurnoverBox(operationId,ouId);
            pSRcmd.setTipTurnoverBoxCode(turnoverBox);
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
        String containerCode = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        if(pickingWay == Constants.PICKING_WAY_ONE) { //使用外部容器(小车) 无出库箱拣货流程
           containerCode = command.getOuterContainer();
        }
        if(pickingWay == Constants.PICKING_WAY_TWO) { //使用外部(小车)，有出库箱拣货流程
            Map<Integer, String> carStockToOutgoingBox =  operatorLine.getCarStockToOutgoingBox();   //出库箱和货格对应关系
            String outBounxBoxCode = null;   //当前出库箱数
            containerCode = command.getOuterContainer();
            List<WhOperationLineCommand> operatorLineList =  whOperationLineManager.findOperationLineByOperationId(operationId, ouId);
            CheckScanResultCommand cSRCmd =  pdaPickingWorkCacheManager.pdaPickingTipOutBounxBoxCode(operatorLineList, operationId, carStockToOutgoingBox);
            if(cSRCmd.getIsNeedScanOutBounxBox()) {
                ContainerCommand cotainerCmd = containerDao.getContainerByCode(outBounxBoxCode, ouId);
                if (null == cotainerCmd) {
                    // 容器信息不存在
                    log.error("pdaScanContainer container is null logid: " + logId);
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
                // 验证容器Lifecycle是否有效
                if (!cotainerCmd.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                    // 容器Lifecycle无效
                    log.error("pdaScanContainer container lifecycle error =" + cotainerCmd.getLifecycle() + " logid: " + logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
                }
                // 验证容器状态是否是待上架
                if (!cotainerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE)) {
                    log.error("pdaScanContainer container status error =" +cotainerCmd.getStatus() + " logid: " + logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY, new Object[] {cotainerCmd.getStatus()});
                }
                command.setTipOutBounxBoxCode(outBounxBoxCode);  //出库箱id
                command.setIsNeedScanOutBounxBox(true);
                return command;
            }else{
                command.setIsNeedScanOutBounxBox(false);
            }
            
          
        }
        if(pickingWay == Constants.PICKING_WAY_THREE) { //使用出库箱拣货流程
            containerCode = command.getOutBoundCode();
        }
        ContainerCommand outCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == outCmd) {
            // 容器信息不存在
            log.error("pdaScanContainer container is null logid: " + logId);
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        // 验证容器Lifecycle是否有效
        if (!outCmd.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
            // 容器Lifecycle无效
            log.error("pdaScanContainer container lifecycle error =" + outCmd.getLifecycle() + " logid: " + logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        // 验证容器状态是否是待上架
        if (!outCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE)) {
            log.error("pdaScanContainer container status error =" +outCmd.getStatus() + " logid: " + logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY, new Object[] {outCmd.getStatus()});
        }
        if(pickingWay == Constants.PICKING_WAY_FOUR) {  //使用周转箱拣货流程
            String turnoverBoxCode = command.getTurnoverBoxCode();
            OutBoundBoxType outBoundBox = outBoundBoxTypeDao.findByCode(turnoverBoxCode, ouId);
            if(null == outBoundBox) {
                throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL );
            }
            // 验证容器Lifecycle是否有效
            if (!outBoundBox.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_STATUS_NO);
            }
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
        CheckScanResultCommand cSRCmd =  pdaPickingWorkCacheManager.locationTipcache(operationId, pickingWay, locationIds);
        if(cSRCmd.getIsPicking()) { //拣货完毕
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
        OperatioLineStatisticsCommand operatioLineStatisticsCommand = pdaPickingWorkCacheManager.pdaPickingWorkTipWholeCase(command.getOperationId(),command.getOuId());
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
        Long operationId = command.getLocationId();
        Long ouId = command.getOuId();
        String locationCode = command.getLocationCode();
        String locationBarCode = command.getLocationBarCode();
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
        if(null != outerContainerIdsLoc && outerContainerIdsLoc.size() != 0) {
            Set<Long>  outerContainerIds = outerContainerIdsLoc.get(locationId);
            if(command.getIsScanOuterContainer()) { //扫描外部容器
                CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.pdaPickingTipOuterContainer(outerContainerIds, operationId);
                if(cSRCmd.getIsNeedTipOutContainer()) {//该库位上的所有外部外部容器都扫描完毕
                    Long outerContainerId = cSRCmd.getTipOuterContainerId();
                    //判断外部容器
                    Container c = containerDao.findByIdExt(outerContainerId, ouId);
                    this.judeContainerStatus(c);
                    //提示外部容器编码
                    command.setTipOuterContainerCode(c.getCode());
                }
            }
            
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
        CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.pdaPickingTipInsideContainer(insideContainerIds, operationId);
        if(cSRCmd.getIsNeedTipInsideContainer()) { //托盘上还有货箱没有扫描
            Long tipInsideContainerId = cSRCmd.getTipiInsideContainerId();
            Container ic = containerDao.findByIdExt(tipInsideContainerId, ouId);
            this.judeContainerStatus(ic);
            command.setTipInsideContainerCode(ic.getCode());
            command.setOuterContainerCode(tipOuterContainerCode);
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
        Map<Long, Set<Long>> insideSkuIds = operatorLine.getInsideSkuIds();
        Map<Long, Set<Long>> locSkuIds = operatorLine.getSkuIds();
        Set<Long> skuIds = null;
        if(null == insideContainerId) { //sku直接放在库位上
            skuIds = locSkuIds.get(locationId);
        }else{
            skuIds = insideSkuIds.get(insideContainerId);
        }
        CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.pdaPickingTipSku(skuIds, operationId,locationId,ouId, insideContainerId);
        if(cSRCmd.getIsNeedScanSku()){  //此货箱的sku，还没有扫描完毕
            String skuAttrId = cSRCmd.getTipSkuAttrId();   //提示唯一的sku
            Long skuId = SkuCategoryProvider.getSkuId(skuAttrId);
            WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
            if(null == skuCmd) {
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            command.setSkuBarCode(skuCmd.getBarCode());
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
        String skuBarCode = command.getSkuBarCode();   //商品条码
        WhSkuCommand  skuCmd = whSkuDao.findWhSkuByBarcodeExt(skuBarCode, ouId);
        if(null == skuCmd) {
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        Long skuId = skuCmd.getId();
       //提示sku库存属性
        ArrayDeque<String> skuAttrIds = cacheManager.getObject(CacheConstants.CACHE_LOC_SKU_ATTR + locationId.toString()+skuId.toString());
        if(null == skuAttrIds) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        String tipSkuAttrId = skuAttrIds.peekFirst();   //取当前第一个
        Long sId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
        if(sId.longValue() != skuId.longValue()) {
            throw new BusinessException(ErrorCodes.RCVD_CACHE_ERROR);
        }
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long,Map<Long, Map<String, Long>>> locSkuAttrIdsQty = operatorLine.getSkuAttrIds();    //
        Map<Long, Map<String, Long>>  skuIdSkuAttrIdsQty = locSkuAttrIdsQty.get(locationId);
        Map<String,Long> skuAttrIdsQty = skuIdSkuAttrIdsQty.get(skuId);
        if(!StringUtil.isEmpty(insideContainerCode)) {
            ContainerCommand ic = containerDao.getContainerByCode(insideContainerCode, ouId);
            if (null == ic) {
                // 容器信息不存在
                log.error("pdaScanContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = ic.getId();
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
            this.tipSkuDetailAspect(command, tipSkuAttrId, skuAttrIdsQty, logId);
        }else{//货箱内待拣货sku库存属性唯一
            if(command.getIsScanInvAttr()) {
                this.tipSkuDetailAspect(command, tipSkuAttrId, skuAttrIdsQty, logId);
            }
            command.setIsUniqueSkuAttrInside(true);  //唯一
        }
        log.info("PdaPickingWorkManagerImpl judgeSkuAttrIdsIsUnique is end");
        
        return command;
        
    }
    
    /***
     * 如果商品绑定多个库位，则提示库位
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
        Integer pickingWay = command.getPickingWay();
        Long ouId = command.getOuId();
        Long skuId = command.getSkuId();
        String skuBarCode = command.getSkuBarCode();
        String insideContainerCode = command.getInsideContainerCode();
        String outerContainerCode = command.getOuterContainerCode(); 
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
        Long outerOuterContainerId = null;
        if(!StringUtil.isEmpty(outerContainerCode)) {
            outerContainerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
            if(null == outerContainerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerOuterContainerId = outerContainerCmd.getId();
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
//        String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
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
        CheckScanResultCommand cSRCmd =  pdaPickingWorkCacheManager.pdaPickingyCacheSkuAndCheckContainer(locationIds, locSkuQty, locationId, locSkuIds, outerContainerIds, outerContainerCmd, operationId, insideContainerSkuIdsQty, insideContainerSkuIds, insideContainerIds, locInsideContainerIds, insideContainerCmd, skuCmd);
        if(cSRCmd.getIsNeedScanSku()) {
            command.setIsNeedTipSku(true);
            Set<Long> skuIds = null;
            if(null == insideContainerId) { //sku直接放在库位上
                skuIds = locSkuIds;
            }else{
                skuIds = insideSkuIds.get(insideContainerId);
            }
            CheckScanResultCommand cSRCommand = pdaPickingWorkCacheManager.pdaPickingTipSku(skuIds, operationId,locationId,ouId, insideContainerId);
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
                insideIds = outerToInsideIds.get(outerOuterContainerId);
            }
            CheckScanResultCommand cSRCommand = pdaPickingWorkCacheManager.pdaPickingTipInsideContainer(insideIds, operationId);
            if(cSRCommand.getIsNeedTipInsideContainer()) {
                Long tipInsideContainerId = cSRCmd.getTipiInsideContainerId();
                Container ic = containerDao.findByIdExt(tipInsideContainerId, ouId);
                this.judeContainerStatus(ic);
                command.setTipInsideContainerCode(ic.getCode());
            }else{
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
        }else if(cSRCmd.getIsNeedTipOutContainer()) { // 提示下一个外部容器
            CheckScanResultCommand cSRCommand =  pdaPickingWorkCacheManager.pdaPickingTipOuterContainer(outerContainerIds, operationId);
            if(cSRCommand.getIsNeedTipOutContainer()) {
                Long outerContainerId = cSRCmd.getTipOuterContainerId();
                //判断外部容器
                Container c = containerDao.findByIdExt(outerContainerId, ouId);
                this.judeContainerStatus(c);
                //提示外部容器编码
                command.setTipOuterContainerCode(c.getCode());
            }else{
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR); 
            }
        }else if(cSRCmd.getIsNeedTipLoc()) {  //提示下一个库位
            CheckScanResultCommand cSRCommand = pdaPickingWorkCacheManager.locationTipcache(operationId,pickingWay,locationIds);
            if(cSRCommand.getIsNeedTipLoc()) {
                Long locId = cSRCmd.getTipLocationId();
                Location location = whLocationDao.findByIdExt(locId, ouId);
                if(null == location) {
                    throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
                }
                command.setTipLocationBarCode(location.getBarCode());
                command.setTipLocationCode(location.getCode());
            }
        }else if(cSRCmd.getIsPicking()){
               command.setIsPicking(true);
        }
        log.info("PdaPickingWorkManagerImpl scanSku is end");
        return command;
    }

    /***
     * 判断容器状态是否正确
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
}
