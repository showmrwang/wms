package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkOperDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkOper;

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
    @Autowired
    private WhOperationManager whOperationManager;
    @Autowired
    private WhOperationLineManager whOperationLineManager;
    @Autowired
    private WhWorkOperDao whWorkOperDao;
    @Autowired
    private WhSkuInventoryDao WhSkuInventoryDao;
    

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
        whWorkOper.setOperatorId(userId);
        
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
        Map<Long, Long> skuQty = new HashMap<Long, Long>();
        // 库位上每个sku对应的唯一sku及件数
        Map<Long, Map<String, Long>> skuAttrIds = new HashMap<Long, Map<String, Long>>();
        // 库位上每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>> skuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();
        // 库位上每个唯一sku对应的货格（is_whole_case=0&&有小车&&库位上sku不在任何容器内）
        Map<String, Set<String>> skuAttrIdsContainerLattice = new HashMap<String, Set<String>>();
        // 外部容器对应所有内部容器 
        Map<Long, Set<Long>> outerToInside = new HashMap<Long, Set<Long>>();
        // 内部容器对应所有sku
        Map<Long, Set<Long>> insideSkuIds = new HashMap<Long, Set<Long>>();
        // 内部容器每个sku总件数
        Map<Long, Long> insideSkuQty = new HashMap<Long, Long>();
        // 内部容器每个sku对应的唯一sku及件数
        Map<Long, Map<String, Long>> insideSkuAttrIds = new HashMap<Long, Map<String, Long>>();
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
                if(skuQty.get(operationLine.getSkuId()) != null){
                   Long qty =  skuQty.get(operationLine.getSkuId()) + operationLine.getQty().longValue();
                   skuQty.put(operationLine.getSkuId(), qty);
                }else{
                    skuQty.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                }
                //获取每个sku对应的唯一sku及件数
                if(null != skuAttrIds.get(operationLine.getSkuId())){
                    Map<String, Long> skuAttrIdsQty = skuAttrIds.get(operationLine.getSkuId());
                    if (null != skuAttrIdsQty.get(onlySku)) {
                        skuAttrIdsQty.put(onlySku, skuAttrIdsQty.get(onlySku) + operationLine.getQty().longValue());
                    } else {
                        skuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                    }
                    skuAttrIds.put(operationLine.getSkuId(), skuAttrIdsQty);
                 }else{
                     Map<String, Long> skuAttrIdsQty = new HashMap<String, Long>();
                     skuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                     skuAttrIds.put(operationLine.getSkuId(), skuAttrIdsQty);
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
                if(insideSkuQty.get(operationLine.getSkuId()) != null){
                    Long insQty =  insideSkuQty.get(operationLine.getSkuId()) + operationLine.getQty().longValue();
                    insideSkuQty.put(operationLine.getSkuId(), insQty);
                 }else{
                     insideSkuQty.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                 }
                //内部容器每个sku对应的唯一sku及件数
                if(null != insideSkuAttrIds.get(operationLine.getSkuId())){
                    Map<String, Long> insideSkuAttrIdsQty = insideSkuAttrIds.get(operationLine.getSkuId());
                    if (null != insideSkuAttrIdsQty.get(onlySku)) {
                        insideSkuAttrIdsQty.put(onlySku, insideSkuAttrIdsQty.get(onlySku) + operationLine.getQty().longValue());
                    } else {
                        insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                    }
                    insideSkuAttrIds.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                 }else{
                     Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                     insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                     insideSkuAttrIds.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
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
        // 所有库位
        statisticsCommand.setLocationIds(locationIds);
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
        pickingScanResultCommand.setOperatorId(whOperationCommand.getId());//是否需要仓库id
        
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
        if(outerContainerIds.size() > 0){
            //仅占用托盘内商品            
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_ONE);
        }else if(outerContainerIds.size() == 0 && insideContainerIds.size() > 0){
            //仅占用货箱内商品
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_TWO);
        }else if(outerContainerIds.size() == 0 && insideContainerIds.size() == 0){
            //仅占用库位上散件商品
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_THREE);
        }else if(pallets.size() >0 || containers.size() > 0 ){//有托盘||有货箱（无外部容器）||散件--散件不知道是啥
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

    @Override
    public PickingScanResultCommand pdaPickingWholeCase(PickingScanResultCommand command) {
        // TODO Auto-generated method stub
        return null;
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
    public void findOperatioLineForGroup(WhWork whWork, Long ouId){
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
        Map<Long, Long> skuQty = new HashMap<Long, Long>();
        // 库位上每个sku对应的唯一sku及件数
        Map<Long, Map<String, Long>> skuAttrIds = new HashMap<Long, Map<String, Long>>();
        // 库位上每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>> skuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();
        // 库位上每个唯一sku对应的货格（is_whole_case=0&&有小车&&库位上sku不在任何容器内）
        Map<String, Set<String>> skuAttrIdsContainerLattice = new HashMap<String, Set<String>>();
        // 外部容器对应所有内部容器 
        Map<Long, Set<Long>> outerToInside = new HashMap<Long, Set<Long>>();
        // 内部容器对应所有sku
        Map<Long, Set<Long>> insideSkuIds = new HashMap<Long, Set<Long>>();
        // 内部容器每个sku总件数
        Map<Long, Long> insideSkuQty = new HashMap<Long, Long>();
        // 内部容器每个sku对应的唯一sku及件数
        Map<Long, Map<String, Long>> insideSkuAttrIds = new HashMap<Long, Map<String, Long>>();
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
                if(skuQty.get(operationLine.getSkuId()) != null){
                   Long qty =  skuQty.get(operationLine.getSkuId()) + operationLine.getQty().longValue();
                   skuQty.put(operationLine.getSkuId(), qty);
                }else{
                    skuQty.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                }
                //获取每个sku对应的唯一sku及件数
                if(null != skuAttrIds.get(operationLine.getSkuId())){
                    Map<String, Long> skuAttrIdsQty = skuAttrIds.get(operationLine.getSkuId());
                    if (null != skuAttrIdsQty.get(onlySku)) {
                        skuAttrIdsQty.put(onlySku, skuAttrIdsQty.get(onlySku) + operationLine.getQty().longValue());
                    } else {
                        skuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                    }
                    skuAttrIds.put(operationLine.getSkuId(), skuAttrIdsQty);
                 }else{
                     Map<String, Long> skuAttrIdsQty = new HashMap<String, Long>();
                     skuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                     skuAttrIds.put(operationLine.getSkuId(), skuAttrIdsQty);
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
                if(insideSkuQty.get(operationLine.getSkuId()) != null){
                    Long insQty =  insideSkuQty.get(operationLine.getSkuId()) + operationLine.getQty().longValue();
                    insideSkuQty.put(operationLine.getSkuId(), insQty);
                 }else{
                     insideSkuQty.put(operationLine.getSkuId(), operationLine.getQty().longValue());
                 }
                //内部容器每个sku对应的唯一sku及件数
                if(null != insideSkuAttrIds.get(operationLine.getSkuId())){
                    Map<String, Long> insideSkuAttrIdsQty = insideSkuAttrIds.get(operationLine.getSkuId());
                    if (null != insideSkuAttrIdsQty.get(onlySku)) {
                        insideSkuAttrIdsQty.put(onlySku, insideSkuAttrIdsQty.get(onlySku) + operationLine.getQty().longValue());
                    } else {
                        insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                    }
                    insideSkuAttrIds.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                 }else{
                     Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                     insideSkuAttrIdsQty.put(onlySku, operationLine.getQty().longValue());
                     insideSkuAttrIds.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
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
        // 所有库位
        statisticsCommand.setLocationIds(locationIds);
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
    }
}
