/**
 * Copyright (c) 2017 Baozun All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * 
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 * 
 */
package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.print.command.PrintDataCommand;
import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipScanSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.BoxInventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WhContainerMoveType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaContainerMoveCacheManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.TipSkuDetailProvider;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ContainerManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionContainerMove;

/**
 * @author feng.hu
 *
 */
@Service("pdaContainerMoveManager")
@Transactional
public class PdaContainerMoveManagerImpl extends BaseManagerImpl implements PdaContainerMoveManager {
protected static final Logger log = LoggerFactory.getLogger(PdaContainerMoveManagerImpl.class);
    

    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private CacheManager cacheManager;
    
    @Autowired
    private PdaContainerMoveCacheManager pdaContainerMoveCacheManager;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private WhSkuDao whSkuDao;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private WhCheckingDao whCheckingDao;
    @Autowired
    private PrintObjectManagerProxy printObjectManagerProxy;
    @Autowired
    private ContainerManager containerManager;
    
    /**
     * 扫描源周转箱
     * @param containerCode
     * @param containerId
     * @param boxMoveFunc
     * @param scanType
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    @Override
    public ScanResultCommand scanContainer(String containerCode, Long containerId, Integer movePattern, String scanType, Long ouId,String logId,Long userId) {
        // TODO Auto-generated method stub
        log.info("PdaContainerMoveManagerImpl scanContainer is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaContainerMoveManagerImpl scanContainer param , ouId is:[{}],  containerCode is:[{}]", ouId,  containerCode);
        }
        ScanResultCommand srCommand = new ScanResultCommand();
        if (WhContainerMoveType.OUT_CONTAINER.equals(scanType)) {
            //判断出库箱是否满足拆箱条件
            //1、必须拣货完成或者播种完成的出库箱才能拆分（判断条件已经产生了待复核数据）
            WhCheckingCommand checkingCommand = whCheckingDao.findWhCheckingByContainerId(containerId,CheckingStatus.NEW, ouId);
            if(null == checkingCommand || !checkingCommand.getStatus().equals(CheckingStatus.NEW)){
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_MOVE_CONDITION);
            }
        }
        //根据出库箱查询对应的库存信息
        List<WhSkuInventoryCommand> containerInvList = whSkuInventoryDao.findSkuInventoryAndSnInfo(null, null, null, null,containerId, ouId);
        //根据周转箱查询不到库存信息
        if(CollectionUtils.isEmpty(containerInvList)){
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_INV, new Object[] {containerCode});
        }
        //统计分析库存数据
        BoxInventoryStatisticResultCommand isCmd = cacheContainerInventoryStatistics(containerInvList, userId, ouId, logId,containerCode);
        //2、如果是出库箱功能功能参数是部分移动,箱内的库存属性必须唯一才能移动
        if(movePattern.equals(Constants.MOVE_PATTERN_PART) && isCmd.getOutBoundBoxSkuIdSkuAttrIdQtys().get(containerCode).size() > 1){
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_INV, new Object[] {containerCode});
        }
        //保存容器库存的统计分析数据
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCode, isCmd, CacheConstants.CACHE_ONE_DAY);
        //保存容器缓存
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerCode, containerInvList, CacheConstants.CACHE_ONE_DAY);
        
        log.info("PdaContainerMoveManagerImpl scanContainer is  end"); 

        return srCommand;   //下一步扫库位 
    }
    
    /***
     * 扫描源容器货格
     * @param sourceContainerCode
     * @param sourceContainerID
     * @param containerLatticNo
     * @param movePattern
     * @param scanType
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    @Override
    public ScanResultCommand scanContainerLatticCode(String sourceContainerCode, Long sourceContainerId,Integer containerLatticNo,Integer movePattern,String scanType, Long ouId,String logId,Long userId) {
        // TODO Auto-generated method stub
        log.info("PdaContainerMoveManagerImpl scanContainerLatticCode is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaContainerMoveManagerImpl scanContainerLatticCode param , ouId is:[{}],  containerCode is:[{}]", ouId,  sourceContainerCode);
        }
        
        ScanResultCommand srCommand = new ScanResultCommand();
        //判断出库箱是否满足拆箱条件,是播种库存还是小车库存
        WhCheckingCommand checkingCondition = new WhCheckingCommand();
        checkingCondition.setContainerLatticeNo(containerLatticNo);
        checkingCondition.setOuId(ouId);
        checkingCondition.setStatus(CheckingStatus.NEW);
        if (WhContainerMoveType.OUT_FACILITY.equals(scanType)) {
            checkingCondition.setFacilityId(sourceContainerId);
        } else if (WhContainerMoveType.OUT_SMAIL_CAR.equals(scanType)) {
            checkingCondition.setOuterContainerId(sourceContainerId);
        } else {
            log.info("PdaContainerMoveManagerImpl scanContainerLatticCode is end, scanType is error.");
            throw new BusinessException(ErrorCodes.CONTAINER_MOVE_TYPE_ERROR);
        }
        /** 获取待复核数据 */
        List<WhCheckingCommand> checkingList = whCheckingDao.findListByParamExt(checkingCondition);
        if (null == checkingList || checkingList.size() == 0) {
            throw new BusinessException(ErrorCodes.CONTAINER_CHECKING_DATE_NOT_EXIST, new Object[] {sourceContainerCode});
        }
        /** 判断是否符合拆分条件 */
        //判断是否有出库箱信息
        boolean chkContainer = true;
        //判断数据状态是否有不等于1的数据
        boolean chkingStatus = false;
        for (WhCheckingCommand checkCmd : checkingList) {
            if (null == checkCmd) {
                continue;
            }
            if (StringUtils.isEmpty(checkCmd.getOutboundboxCode()) && null == checkCmd.getContainerId()) {
                chkContainer = false;
                break;
            }
            if (null == checkCmd.getStatus() || !checkCmd.getStatus().equals(CheckingStatus.NEW)) {
                chkingStatus = true;
                break;
            }
        }
        if (chkContainer) {
            throw new BusinessException(ErrorCodes.CHECKING_DATA_EXIST_CONTAINER, new Object[] {sourceContainerCode});
        }
        if (chkingStatus) {
            throw new BusinessException(ErrorCodes.CHECKING_DATA_NOT_MOVE_CONDITION, new Object[] {sourceContainerCode});
        }
        
        //根据源容器信息查询对应的库存信息
        List<WhSkuInventoryCommand> containerInvList = null;
        if (WhContainerMoveType.OUT_FACILITY.equals(scanType)) {
            //分拨墙库存信息查询条件
            containerInvList = whSkuInventoryDao.findSkuInventoryAndSnInfo(null,sourceContainerCode,null,containerLatticNo,null,ouId);
        } else if (WhContainerMoveType.OUT_SMAIL_CAR.equals(scanType)) {
            //小车库存信息查询条件
            containerInvList = whSkuInventoryDao.findSkuInventoryAndSnInfo(null,null,sourceContainerId,containerLatticNo,null,ouId);
        } 
        //根据出库箱查询不到库存信息
        if(CollectionUtils.isEmpty(containerInvList)){
            throw new BusinessException(ErrorCodes.FACILITY_NOT_FOUND_INVENTORY_ERROR, new Object[] {sourceContainerCode});
        }
        //统计分析库存数据
        BoxInventoryStatisticResultCommand isCmd = cacheContainerInventoryStatistics(containerInvList, userId, ouId, logId,sourceContainerCode);
        //2、如果是出库箱功能功能参数是部分移动,箱内的库存属性必须唯一才能移动
        if(movePattern.equals(Constants.MOVE_PATTERN_PART) && isCmd.getOutBoundBoxSkuIdSkuAttrIdQtys().get(sourceContainerCode).size() > 1){
            throw new BusinessException(ErrorCodes.CONTAINER_SKU_INV_NOT_UNIQUE, new Object[] {sourceContainerCode});
        }
        //保存容器库存的统计分析数据
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, sourceContainerCode, isCmd, CacheConstants.CACHE_ONE_DAY);
        //保存容器缓存
        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, sourceContainerCode, containerInvList, CacheConstants.CACHE_ONE_DAY);
        
        log.info("PdaContainerMoveManagerImpl scanContainerLatticCode is  end"); 

        return srCommand;   //下一步扫目标出库箱 
    }
    
    
    /***
     * 扫描目标容器
     * @param targetContainerCode
     * @param ouId
     * @param logId
     * @param userId
     * @param sourceBoxCode
     * @param warehouse
     * @return
     */
    @Override
    public ScanResultCommand scanTargetContainer(String targetContainerCode,Long targetContainerId, String scanType,Long ouId,String logId,Long userId,String sourceBoxCode,Integer containerStatus) {
        log.info("PdaContainerMoveManagerImpl scanTargetContainer is start");
        ScanResultCommand srCommand = new ScanResultCommand();
        if (ContainerStatus.CONTAINER_STATUS_USABLE != containerStatus && !WhContainerMoveType.IN_CONTAINER.equals(scanType)) {
            //根据目标周转箱查询对应的库存信息
            List<WhSkuInventoryCommand> containerInvList = whSkuInventoryDao.findSkuInventoryAndSnInfo(null, null, null, null,targetContainerId, ouId);
            if(CollectionUtils.isNotEmpty(containerInvList)){
                //通过缓存获取原出库箱库存信息
                List<WhSkuInventoryCommand> sourceBoxList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, sourceBoxCode);
                if(CollectionUtils.isEmpty(sourceBoxList)){
                    throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {sourceBoxCode});
                }
                //如果目标出库箱已有库存则出库单号与原始出库箱/货格库存保持一致，
                if(!sourceBoxList.get(0).getOccupationCode().equalsIgnoreCase(containerInvList.get(0).getOccupationCode())){
                    throw new BusinessException(ErrorCodes.CONTAINER_ODO_CODE_NOT_MATCH);
                }
                //且目标出库箱也是拣货或播种完成状态
                WhCheckingCommand checkingCommand = whCheckingDao.findWhCheckingByContainerId(targetContainerId,CheckingStatus.NEW, ouId);
                if(null == checkingCommand || !checkingCommand.getStatus().equals(CheckingStatus.NEW)){
                    throw new BusinessException(ErrorCodes.CONTAINER_NOT_MOVE_CONDITION);
                }
            }
        }
        //获取源容器中的库存统计信息
        BoxInventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, sourceBoxCode);
        if(null == isCmd){
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_INVENTORY,new Object[] {sourceBoxCode});
        }
        //随机获取一个sku提示扫描
        String tipSkuAttrId = getTipSkuFromBox(sourceBoxCode, isCmd.getOutBoundBoxSkuIdSkuAttrIds(), isCmd.getOutBoundBoxSkuAttrIdsSnDefect(), logId);
        Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
        if (null == skuCmd) {
            log.error("sku is not found error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        //获取商品属性明细信息
        tipSkuDetailAspect(srCommand, tipSkuAttrId,  isCmd.getOutBoundBoxSkuIdSkuAttrIdQtys().get(sourceBoxCode),logId); 
        srCommand.setNeedTipSku(true);
        srCommand.setNeedTipSkuDetail(true);
        srCommand.setTipSkuBarcode(skuCmd.getBarCode());//提示sku
        srCommand.setSkuId(skuId);
        log.info("PdaContainerMoveManagerImpl scanTargetContainer is end");
        
        return srCommand;
    }
    
    /**
     * 出库箱拆分:扫描sku商品
     * @param isScanSkuSn
     * @param insideContainerCode
     * @param skuCmd
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @param warehouse
     * @param scanPattern
     * @param movePattern
     * @return
     */
      public ScanResultCommand containerMoveScanSku(String sourceContainerCode,Long sourceContainerId, Integer containerLatticNo,String targetContainerCode,Long targetContainerId,WhSkuCommand skuCmd, 
               Long funcId,Warehouse warehouse,WhFunctionContainerMove containerMoveFunc, String scanType, Long ouId, Long userId, String logId){
          log.info("PdaSysSuggestPutwayManagerImpl containerMoveScanSku is start");
          ScanResultCommand srCmd = new ScanResultCommand();
          
          // 得到当前扫描的商品信息        
          String barCode = skuCmd.getBarCode();
          Double scanQty = skuCmd.getScanSkuQty();
          if (null == scanQty || scanQty.longValue() < 1) {
              log.error("PdaContainerMoveManagerImpl containerMoveScanSku is end,scan sku qty is valid, logId is:[{}]", logId);
              throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
          }
          if (StringUtils.isEmpty(barCode)) {
              log.error("PdaContainerMoveManagerImpl containerMoveScanSku is end,sku is null error, logId is:[{}]", logId);
              throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
          }
          srCmd = splitContainerMoveCheckScanSkuConfirm(sourceContainerCode, sourceContainerId,containerLatticNo,targetContainerCode,targetContainerId,containerMoveFunc,skuCmd,warehouse,scanType, funcId, ouId, userId, logId);
          log.error("PdaContainerMoveManagerImpl containerMoveScanSku is end, logId is:[{}]", logId);
          return srCmd;
      }
    /**
     * 出库箱库存信息分析统计流程
     * @param invList
     * @param ouId
     * @param logId
     * @param containerCmd
     * @param srCmd
     * @param putWay
     * @return
     */
    private BoxInventoryStatisticResultCommand cacheContainerInventoryStatistics(List<WhSkuInventoryCommand> invList,Long userId,Long ouId,String logId,String outboundBoxCode){
        // 3.库存信息统计
        BoxInventoryStatisticResultCommand isrCmd = new BoxInventoryStatisticResultCommand();
        
        isrCmd = new BoxInventoryStatisticResultCommand();
        /** 出库箱所有sku种类 */
        Map<String, Set<Long>> outBoundBoxSkuIds = new HashMap<String, Set<Long>>();
        /** 出库箱所有sku总件数 */
        Map<String, Long> outBoundBoxSkuQty = new HashMap<String, Long>();
        /** 出库箱单个sku总件数 */
        Map<String, Map<Long, Long>> outBoundBoxSkuIdQtys = new HashMap<String, Map<Long, Long>>();
        /** 出库箱唯一sku种类 */
        Map<String, Set<String>> outBoundBoxSkuIdSkuAttrIds = new HashMap<String, Set<String>>();
        /** 出库箱唯一sku总件数 */
        Map<String, Map<String, Long>> outBoundBoxSkuIdSkuAttrIdQtys = new HashMap<String, Map<String, Long>>();
        /** 出库箱唯一sku对应所有残次条码 */
        Map<String, Map<String, Set<String>>> outBoundBoxSkuAttrIdsSnDefect = new HashMap<String, Map<String, Set<String>>>();
        /** 出库箱唯一sku种类包含残次条码 */
        Map<String, Set<String>> outBoundBoxSkuIdSkuAttrIdsSnDefect = new HashMap<String, Set<String>>();
        
        Set<Long> skuIds = new HashSet<Long>();// 容器中所有sku种类
        Map<Long, Long> skuIdsQty = new HashMap<Long, Long>();// 容器每个sku总件数
        Set<String> skuAttrIds = new HashSet<String>();//  容器对应唯一sku
        Map<String, Long> skuAttrIdsQty = new HashMap<String,Long>();// 容器对应唯一sku总件数
        Set<String> skuAttrIdsSnDefect = new HashSet<String>();//  容器对应唯一sku包含sn信息
        Long skuQty = 0L;       
        Map<String, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = new HashMap<String, Map<String, Set<String>>>(); //容器内唯一sku对应所有sn及残次条码
        for (WhSkuInventoryCommand invCmd : invList) {
            //获取SKU唯一属性
            String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
            Long skuId = invCmd.getSkuId();
            Double curerntSkuQty = 0.0;     //当前sku数量
            curerntSkuQty  = invCmd.getOnHandQty();   //sku在库库存
            skuQty +=  invCmd.getOnHandQty().longValue();
            if (null != skuId) {
                skuIds.add(skuId);    //所有sku种类数
                WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
                if (null == skuCmd) {
                    outboundBoxCodeRemoveInventory(outboundBoxCode, ouId, logId);
                    log.error("outbound box move SKU  is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                    throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                }
            }
            //加载所有唯一的sku(包含库存属性)
            skuAttrIds.add(skuAttrId);
            //容器内每个SKU的总件数统计
            if (null != skuIdsQty.get(skuId)) {
                skuIdsQty.put(skuId, skuIdsQty.get(skuId) + curerntSkuQty.longValue());
            } else {
                skuIdsQty.put(skuId, curerntSkuQty.longValue());
            }
            //统计唯一SKU总件数
            if (null != skuAttrIdsQty.get(skuAttrId)) {
                skuAttrIdsQty.put(skuAttrId, skuAttrIdsQty.get(skuAttrId) + curerntSkuQty.longValue());
            } else {
                skuAttrIdsQty.put(skuAttrId, curerntSkuQty.longValue());
            }            
            //容器内唯一sku对应所有sn及残次条码                
            List<WhSkuInventorySnCommand> snCmdList = invCmd.getWhSkuInventorySnCommandList();//获取库存的残次信息
            Set<String> snDefects = null;
            if (null != snCmdList && 0 < snCmdList.size()) {
                snDefects = new HashSet<String>();
                for (WhSkuInventorySnCommand snCmd : snCmdList) {
                    if (null != snCmd) {
                        String defectBar = snCmd.getDefectWareBarcode();
                        String sn = snCmd.getSn();
                        /**把sn信息拼到商品唯一属性后面*/
                        String skuAttrIdsSn = SkuCategoryProvider.concatSkuAttrId(skuAttrId, sn, defectBar);
                        snDefects.add(skuAttrIdsSn);
                        skuAttrIdsSnDefect.add(skuAttrIdsSn);
                        if (null != snDefects) {
                            if (null != insideContainerSkuAttrIdsSnDefect.get(outboundBoxCode)) {
                                Map<String, Set<String>> skuAttrIdsDefect = insideContainerSkuAttrIdsSnDefect.get(outboundBoxCode);
                                if (null != skuAttrIdsDefect.get(skuAttrId)) {
                                    Set<String> defects = skuAttrIdsDefect.get(skuAttrId);
                                    defects.addAll(snDefects);
                                    skuAttrIdsDefect.put(skuAttrId, defects);
                                    insideContainerSkuAttrIdsSnDefect.put(outboundBoxCode, skuAttrIdsDefect);
                                } else {
                                    skuAttrIdsDefect.put(skuAttrId, snDefects);
                                    insideContainerSkuAttrIdsSnDefect.put(outboundBoxCode, skuAttrIdsDefect);
                                }
                            } else {
                                Map<String, Set<String>> skuAttrIdsDefect = new HashMap<String, Set<String>>();
                                skuAttrIdsDefect.put(skuAttrId, snDefects);
                                insideContainerSkuAttrIdsSnDefect.put(outboundBoxCode, skuAttrIdsDefect);
                            }
                        }                             
                    }
                }
            } else {
                skuAttrIdsSnDefect.add(skuAttrId);
            }
        }
        outBoundBoxSkuIds.put(outboundBoxCode, skuIds);//出库箱所有sku种类
        outBoundBoxSkuIdQtys.put(outboundBoxCode, skuIdsQty);//出库箱中每个sku的数量
        outBoundBoxSkuIdSkuAttrIds.put(outboundBoxCode, skuAttrIds);//出库箱中唯一SKU
        outBoundBoxSkuIdSkuAttrIdQtys.put(outboundBoxCode, skuAttrIdsQty);//出库箱中唯一sku总件数
        outBoundBoxSkuAttrIdsSnDefect=insideContainerSkuAttrIdsSnDefect;//容器内唯一sku对应所有sn及残次条码
        outBoundBoxSkuQty.put(outboundBoxCode, skuQty);//出库箱总SKU数量
        outBoundBoxSkuIdSkuAttrIdsSnDefect.put(outboundBoxCode, skuAttrIdsSnDefect);
        isrCmd.setOutBoundBoxCode(outboundBoxCode);
        isrCmd.setOutBoundBoxSkuQty(outBoundBoxSkuQty);
        isrCmd.setOutBoundBoxSkuIds(outBoundBoxSkuIds);
        isrCmd.setOutBoundBoxSkuIdQtys(outBoundBoxSkuIdQtys);
        isrCmd.setOutBoundBoxSkuIdSkuAttrIds(outBoundBoxSkuIdSkuAttrIds);
        isrCmd.setOutBoundBoxSkuIdSkuAttrIdQtys(outBoundBoxSkuIdSkuAttrIdQtys);
        isrCmd.setOutBoundBoxSkuAttrIdsSnDefect(outBoundBoxSkuAttrIdsSnDefect);
        isrCmd.setOutBoundBoxSkuIdSkuAttrIdsSnDefect(outBoundBoxSkuIdSkuAttrIdsSnDefect);
        return isrCmd;
    } 
    
   /**
    * 随机获取一个SKU进行提示扫描
    * @param outboundBoxCode
    * @param locationId
    * @param locSkuAttrIds
    * @param logId
    * @return
    */
    private String getTipSkuFromBox(String outboundBoxCode,  Map<String, Set<String>> locSkuAttrIds,Map<String, Map<String, Set<String>>> skuAttrIdsSnDefect, String logId) {
        String tipSku = "";
        TipScanSkuCacheCommand cacheSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + outboundBoxCode);
        ArrayDeque<String> cacheSkuAttrIds = null;
        if (null != cacheSkuCmd) { 
            cacheSkuAttrIds = cacheSkuCmd.getScanSkuAttrIds();
        }
        if (null != cacheSkuAttrIds && !cacheSkuAttrIds.isEmpty()) {
            String value = cacheSkuAttrIds.getFirst();
            tipSku = value;
        } else {
            //获取skuAttrId信息
            Set<String> skuAttrIds = locSkuAttrIds.get(outboundBoxCode);
            //获取SN信息
            Map<String, Set<String>> skuAttrIdsSnDef = skuAttrIdsSnDefect.get(outboundBoxCode);
            // 随机提示一个
            for (String sId : skuAttrIds) {
                if (!StringUtils.isEmpty(sId)) {
                    if(null != skuAttrIdsSnDef && skuAttrIdsSnDef.size() > 0) {
                        Set<String> skuAttrIdsSnSet = skuAttrIdsSnDef.get(sId);
                        if (null != skuAttrIdsSnSet && skuAttrIdsSnSet.size() > 0) {
                            for (String snDef : skuAttrIdsSnSet) {
                                if (!StringUtils.isEmpty(snDef)) {
                                    //包含SN信息的key
                                    tipSku = snDef;
                                    break;
                                }
                            }
                        }
                    }
                    if (StringUtils.isEmpty(tipSku)) {
                        //不包含SN信息的key
                        tipSku = sId;
                    }
                    TipScanSkuCacheCommand tipCmd = new TipScanSkuCacheCommand();
                    tipCmd.setInsideContainerCode(outboundBoxCode);
                    ArrayDeque<String> tipSkuAttrIds = new ArrayDeque<String>();
                    tipSkuAttrIds.addFirst(tipSku);
                    tipCmd.setScanSkuAttrIds(tipSkuAttrIds);
                    //缓存随机获取一个SKU的信息
                    cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + outboundBoxCode, tipCmd, CacheConstants.CACHE_ONE_DAY);
                    break;
                }
            }
        }
        return tipSku;
    }
    
    /**
     * 新增下一个SKU进行提示扫描
     * @param outboundBoxCode
     * @param locationId
     * @param locSkuAttrIds
     * @param logId
     * @return
     */
     private String setTipSkuFromBox(String outboundBoxCode,  Map<String, Set<String>> allSkuAttrIds, String skuAttrId, String logId) {
         String tipSkuAttrId = skuAttrId;
         // 0.先判断提示的sku是否存在当前绑定的库位上
         Set<String> skuAttrIds = allSkuAttrIds.get(outboundBoxCode);
         boolean isExists = false;
         for (String saId : skuAttrIds) {
             if (skuAttrId.equals(saId)) {
                 isExists = true;
                 break;
             }
         }
         if (false == isExists) {
             log.error("tip skuAttrId is not binding loc error, locId is:[{}], tipSkuAttrId is:[{}], logId is:[{}]", outboundBoxCode, skuAttrId, logId);
             throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
         }
         TipScanSkuCacheCommand tipSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + outboundBoxCode);
         ArrayDeque<String> tipSkuAttrIds = null;
         if (null != tipSkuCmd) {
             tipSkuAttrIds = tipSkuCmd.getScanSkuAttrIds();
         }
         if (null != tipSkuAttrIds && !tipSkuAttrIds.isEmpty()) {
             tipSkuAttrIds.addFirst(tipSkuAttrId);
             tipSkuCmd.setScanSkuAttrIds(tipSkuAttrIds);
             cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + outboundBoxCode, tipSkuCmd, CacheConstants.CACHE_ONE_DAY);
         } else {
             TipScanSkuCacheCommand tipCmd = new TipScanSkuCacheCommand();
             tipCmd.setInsideContainerCode(outboundBoxCode);             
             ArrayDeque<String> tipSkuIds = new ArrayDeque<String>();
             tipSkuIds.addFirst(tipSkuAttrId);
             tipCmd.setScanSkuAttrIds(tipSkuIds);
             cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + outboundBoxCode, tipCmd, CacheConstants.CACHE_ONE_DAY);
         }
         return tipSkuAttrId;
     }
    
    /**
     * 商品明细数据赋值
     * @param srCmd
     * @param tipSkuAttrId
     * @param skuAttrIdsQty
     * @param logId
     */
    private void tipSkuDetailAspect(ScanResultCommand srCmd, String tipSkuAttrId, Map<String, Long> skuAttrIdsQty, String logId) {
        boolean isTipSkuDetail = true;
        srCmd.setNeedTipSkuDetail(isTipSkuDetail);
        String skuAttrId = SkuCategoryProvider.getSkuAttrId(tipSkuAttrId);
        Long qty = skuAttrIdsQty.get(skuAttrId);
        if (null == qty) {
            log.error("sku qty is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        srCmd.setTipSkuQty(qty.intValue());
        if (true == isTipSkuDetail) {
            srCmd.setNeedTipSkuInvType(TipSkuDetailProvider.isTipSkuInvType(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvType()) {
                String skuInvType = TipSkuDetailProvider.getSkuInvType(tipSkuAttrId);
                List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroup(Constants.INVENTORY_TYPE, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : invTypeList) {
                    if (sd.getDicValue().equals(skuInvType)) {
                        srCmd.setTipSkuInvType(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv type is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvType("");
            }
            srCmd.setNeedTipSkuInvStatus(TipSkuDetailProvider.isTipSkuInvStatus(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvStatus()) {
                String skuInvStatus = TipSkuDetailProvider.getSkuInvStatus(tipSkuAttrId);
                List<InventoryStatus> invStatusList = inventoryStatusManager.findAllInventoryStatus();
                boolean isExists = false;
                for (InventoryStatus is : invStatusList) {
                    if (is.getId().toString().equals(skuInvStatus)) {
                        srCmd.setTipSkuInvStatus(is.getName());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv status is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvStatus("");
            }
            srCmd.setNeedTipSkuBatchNumber(TipSkuDetailProvider.isTipSkuBatchNumber(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuBatchNumber()) {
                String skuBatchNumber = TipSkuDetailProvider.getSkuBatchNumber(tipSkuAttrId);
                srCmd.setTipSkuBatchNumber(skuBatchNumber);
            } else {
                srCmd.setTipSkuBatchNumber("");
            }
            srCmd.setNeedTipSkuCountryOfOrigin(TipSkuDetailProvider.isTipSkuCountryOfOrigin(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuCountryOfOrigin()) {
                String skuCountryOfOrigin = TipSkuDetailProvider.getSkuCountryOfOrigin(tipSkuAttrId);
                srCmd.setTipSkuCountryOfOrigin(skuCountryOfOrigin);
            } else {
                srCmd.setTipSkuCountryOfOrigin("");
            }
            srCmd.setNeedTipSkuMfgDate(TipSkuDetailProvider.isTipSkuMfgDate(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuMfgDate()) {
                String skuMfgDate = TipSkuDetailProvider.getSkuMfgDate(tipSkuAttrId);
                srCmd.setTipSkuMfgDate(skuMfgDate);
            } else {
                srCmd.setTipSkuMfgDate("");
            }
            srCmd.setNeedTipSkuExpDate(TipSkuDetailProvider.isTipSkuExpDate(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuExpDate()) {
                String skuExpDate = TipSkuDetailProvider.getSkuExpDate(tipSkuAttrId);
                srCmd.setTipSkuExpDate(skuExpDate);
            } else {
                srCmd.setTipSkuExpDate("");
            }
            srCmd.setNeedTipSkuInvAttr1(TipSkuDetailProvider.isTipSkuInvAttr1(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr1()) {
                String skuInvAttr1 = TipSkuDetailProvider.getSkuInvAttr1(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_1, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr1)) {
                        srCmd.setTipSkuInvAttr1(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr1 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr1("");
            }
            srCmd.setNeedTipSkuInvAttr2(TipSkuDetailProvider.isTipSkuInvAttr2(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr2()) {
                String skuInvAttr2 = TipSkuDetailProvider.getSkuInvAttr2(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_2, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr2)) {
                        srCmd.setTipSkuInvAttr2(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr2 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr2("");
            }
            srCmd.setNeedTipSkuInvAttr3(TipSkuDetailProvider.isTipSkuInvAttr3(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr3()) {
                String skuInvAttr3 = TipSkuDetailProvider.getSkuInvAttr3(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_3, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr3)) {
                        srCmd.setTipSkuInvAttr3(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr3 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr3("");
            }
            srCmd.setNeedTipSkuInvAttr4(TipSkuDetailProvider.isTipSkuInvAttr4(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr4()) {
                String skuInvAttr4 = TipSkuDetailProvider.getSkuInvAttr4(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_4, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr4)) {
                        srCmd.setTipSkuInvAttr4(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr4 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr4("");
            }
            srCmd.setNeedTipSkuInvAttr5(TipSkuDetailProvider.isTipSkuInvAttr5(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr5()) {
                String skuInvAttr5 = TipSkuDetailProvider.getSkuInvAttr5(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_5, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr5)) {
                        srCmd.setTipSkuInvAttr5(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr5 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr5("");
            }
            //获取sn信息
            srCmd.setNeedTipSkuSn(TipSkuDetailProvider.isTipSkuSn(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuSn()) {
                String skuSn = TipSkuDetailProvider.getSkuSn(tipSkuAttrId);
                srCmd.setTipSkuSn(skuSn);
            } else {
                srCmd.setTipSkuSn("");
            }
            srCmd.setNeedTipSkuDefect(TipSkuDetailProvider.isTipSkuDefect(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuDefect()) {
                String skuDefect = TipSkuDetailProvider.getSkuDefect(tipSkuAttrId);
                srCmd.setTipSkuDefect(skuDefect);
            } else {
                srCmd.setTipSkuDefect("");
            }
        }
    }

    /**
     * @author 
     * @param containerCmd
     * @param ouId
     * @param logId
     */
    public void outboundBoxCodeRemoveInventory(String  outboundboxCode, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("outboundBox remove inventory start, outboundboxCode is:[{}], ouId is:[{}], logId is:[{}]", outboundboxCode, ouId, logId);
        }
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, outboundboxCode);
        if (log.isInfoEnabled()) {
            log.info("outboundBox remove inventory end, outboundboxCode is:[{}], ouId is:[{}], logId is:[{}]", outboundboxCode, ouId, logId);
        }
    }
    
    /***
     * 取消扫描源容器画面操作
     * @param containerCode
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    @Override
    public void cancelScanContainer(String containerCode,String scanType, Long ouId,String logId,Long userId) {
        log.info("PdaContainerMoveManagerImpl cancelScanContainer is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaContainerMoveManagerImpl cancelScanContainer param , ouId is:[{}],  containerCode is:[{}]", ouId,  containerCode);
        }
        //清除redis缓存
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCode);
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, containerCode);
        
        log.info("PdaContainerMoveManagerImpl cancelScanContainer is  end"); 
    }
    
    /***
     * 取消扫描货格画面操作
     * @param containerCode
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    @Override
    public void cancelScanContainerLattic(String sourceContainerCode,Integer containerLatticNo, String scanType, Long ouId,String logId,Long userId) {
        log.info("PdaContainerMoveManagerImpl cancelScanContainerLattic is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaContainerMoveManagerImpl cancelScanContainerLattic param , ouId is:[{}],  containerCode is:[{}]", ouId,  sourceContainerCode);
        }
        //清除redis缓存
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, sourceContainerCode);
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, sourceContainerCode);        
        log.info("PdaContainerMoveManagerImpl cancelScanContainerLattic is  end"); 
    }
    
    /***
     * 取消扫描目标容器画面操作
     * @param containerCode
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    @Override
    public void cancelScanTargetContainer(String sourceContainerCode,Integer containerLatticNo, String scanType, Long ouId,String logId,Long userId) {
        log.info("PdaContainerMoveManagerImpl cancelScanTargetContainer is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaContainerMoveManagerImpl cancelScanTargetContainer param , ouId is:[{}],  containerCode is:[{}]", ouId,  sourceContainerCode);
        }
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, sourceContainerCode);
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, sourceContainerCode);
        cacheManager.remonKeys(CacheConstants.SCAN_SKU_QUEUE + sourceContainerCode + "*");
        cacheManager.remonKeys(CacheConstants.SCAN_SKU_SN_QUEUE + sourceContainerCode + "*");
        cacheManager.remonKeys(CacheConstants.SCAN_SKU_SN_COUNT + sourceContainerCode + "*");
        log.info("PdaContainerMoveManagerImpl cancelScanTargetContainer is  end"); 
    }
    
    /***
     * 取消扫描源商品画面操作
     * @param containerCode
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    @Override
    public void cancelScanSku(String sourceContainerCode,Integer containerLatticNo, String scanType, Long ouId,String logId,Long userId) {
        log.info("PdaContainerMoveManagerImpl cancelScanSku is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaContainerMoveManagerImpl cancelScanSku param , ouId is:[{}],  containerCode is:[{}]", ouId,  sourceContainerCode);
        }
        //清除redis缓存        
        cacheManager.remonKeys(CacheConstants.SCAN_SKU_QUEUE + sourceContainerCode + "*");
        cacheManager.remonKeys(CacheConstants.SCAN_SKU_SN_QUEUE + sourceContainerCode + "*");
        cacheManager.remonKeys(CacheConstants.SCAN_SKU_SN_COUNT + sourceContainerCode + "*");
        cacheManager.remove(CacheConstants.SCAN_SKU_SN_FOR_CONTAINER + sourceContainerCode);
        cacheManager.remove(CacheConstants.SCAN_SKU_QTY_FOR_CONTAINER + sourceContainerCode);
        cacheManager.remove(CacheConstants.SCAN_SKU_QTY_COUNT + sourceContainerCode);
        
        log.info("PdaContainerMoveManagerImpl cancelScanSku is  end"); 
    }
    
    /**
     * 部分拆分移动核对扫商品
     * 
     * @author feng.hu
     * @param ocCmd
     * @param icCmd
     * @param insideContainer
     * @param locationCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
    private ScanResultCommand splitContainerMoveCheckScanSkuConfirm(String sourceContainerCode, Long sourceContainerId, Integer containerLatticNo,String targetContainerCode,Long targetContainerId, WhFunctionContainerMove containerMoveFunc, WhSkuCommand skuCmd, Warehouse warehouse,String scanType,Long funcId, Long ouId, Long userId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("splitContainerMoveCheckScanSkuConfirm start, sourceContainerCode is:[{}], sourceContainerId is:[{}], containerLatticNo is:[{}], targetContainerCode is:[{}],scanPattern is:[{}], barCode is:[{}],funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]",
                    new Object[] {sourceContainerCode, (null != sourceContainerId ? sourceContainerId : 0),(null != containerLatticNo ? containerLatticNo : 0), targetContainerCode, (null != containerMoveFunc ? containerMoveFunc.getScanPattern() : ""), (null != skuCmd ? skuCmd.getBarCode() : ""), funcId, ouId, userId, logId});
        }
        ScanResultCommand srCmd = new ScanResultCommand();
        //通过缓存获取源容器内库存
        List<WhSkuInventoryCommand> sourceBoxList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, sourceContainerCode);
        if(CollectionUtils.isEmpty(sourceBoxList)){
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {sourceContainerCode});
        }
        //获取源容器中的库存统计信息
        BoxInventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, sourceContainerCode);
        if(null == isCmd){
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_INVENTORY,new Object[] {sourceContainerCode});
        }
        
        Map<String, Map<String, Long>> insideContainerSkuAttrIdsQty = isCmd.getOutBoundBoxSkuIdSkuAttrIdQtys();   //源容器唯一sku总件数
        Map<String, Set<String>> insideContainerSkuAttrIds = isCmd.getOutBoundBoxSkuIdSkuAttrIds();    //源容器对应唯一sku
        Map<String, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = isCmd.getOutBoundBoxSkuAttrIdsSnDefect();  //源容器唯一sku对应所有残次条码
        Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(sourceContainerCode);//源容器中唯一SKU对应的商品件数
        Map<String, Set<Long>> insideContainerSkuIds = isCmd.getOutBoundBoxSkuIds();//出库箱所有sku种类
        Map<String, Map<Long, Long>> insideContainerSkuIdsQty = isCmd.getOutBoundBoxSkuIdQtys();//出库箱中每个sku的数量
        Map<String, Set<String>> insideContainerAllSkuAttrIdsSn = isCmd.getOutBoundBoxSkuIdSkuAttrIdsSnDefect();    //源容器对应唯一sku包含sn信息
        
        // 1.获取功能配置
        if (null == containerMoveFunc) {
            log.error("WhFunctionContainerMove is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }        
        Integer scanPattern = (WhContainerMoveType.ONE_BY_ONE_SCAN == containerMoveFunc.getScanPattern()) ? WhContainerMoveType.ONE_BY_ONE_SCAN : WhContainerMoveType.NUMBER_ONLY_SCAN;
        Integer movePattern = (WhContainerMoveType.FULL_BOX_MOVE == containerMoveFunc.getMovePattern()) ? WhContainerMoveType.FULL_BOX_MOVE : WhContainerMoveType.PART_BOX_MOVE;
        // 商品校验
        Long sId = null;
        String skuBarcode = skuCmd.getBarCode();
        Double scanQty = skuCmd.getScanSkuQty();
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId);
        Set<Long> sourceSkuIds = insideContainerSkuIds.get(sourceContainerCode);
        Map<Long, Long> sourceSkuIdsQty = insideContainerSkuIdsQty.get(sourceContainerCode);
        boolean isSkuExists = false;
        Integer cacheSkuQty = 1;
        Integer sourceSkuQty = 1;
        Integer locSkuAttrQty = 1;
        for(Long cacheId : cacheSkuIdsQty.keySet()){
            if(sourceSkuIds.contains(cacheId)){
                isSkuExists = true;
            }
            if(true == isSkuExists){
                sId = cacheId;
                cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                sourceSkuQty = (null == sourceSkuIdsQty.get(cacheId) ? 1 : sourceSkuIdsQty.get(cacheId).intValue());
                break;
            }
        }
        if(false == isSkuExists){
            log.error("scan sku is not found in current inside contianer error, containerCode is:[{}], scanSkuId is:[{}], logId is:[{}]", sourceContainerCode, sId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU, new Object[] {sourceContainerCode});
        }
        skuCmd.setId(sId);
        skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
        SkuRedisCommand cacheSkuCmd = skuRedisManager.findSkuMasterBySkuId(sId, ouId, logId);
        if (null == cacheSkuCmd) {
            log.error("sku is not found error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        Sku s = cacheSkuCmd.getSku();
        WhSkuCommand sku = new WhSkuCommand();
        BeanUtils.copyProperties(s, sku);
        sku.setId(skuCmd.getId());
        sku.setScanSkuQty(skuCmd.getScanSkuQty());
        sku.setIsNeedTipSkuDetail(null == skuCmd.getIsNeedTipSkuDetail() ? false : skuCmd.getIsNeedTipSkuDetail());
        sku.setIsNeedTipSkuSn(null == skuCmd.getIsNeedTipSkuSn() ? false : skuCmd.getIsNeedTipSkuSn());
        sku.setIsNeedTipSkuDefect(null == skuCmd.getIsNeedTipSkuDefect() ? false : skuCmd.getIsNeedTipSkuDefect());
        sku.setInvType(StringUtils.isEmpty(skuCmd.getInvType()) ? "" : skuCmd.getInvType());
        sku.setInvStatus(StringUtils.isEmpty(skuCmd.getInvStatus()) ? "" : skuCmd.getInvStatus());
        sku.setInvBatchNumber(StringUtils.isEmpty(skuCmd.getInvBatchNumber()) ? "" : skuCmd.getInvBatchNumber());
        sku.setInvCountryOfOrigin(StringUtils.isEmpty(skuCmd.getInvCountryOfOrigin()) ? "" : skuCmd.getInvCountryOfOrigin());
        sku.setInvMfgDate(StringUtils.isEmpty(skuCmd.getInvMfgDate()) ? "" : skuCmd.getInvMfgDate());
        sku.setInvExpDate(StringUtils.isEmpty(skuCmd.getInvExpDate()) ? "" : skuCmd.getInvExpDate());
        sku.setInvAttr1(StringUtils.isEmpty(skuCmd.getInvAttr1()) ? "" : skuCmd.getInvAttr1());
        sku.setInvAttr2(StringUtils.isEmpty(skuCmd.getInvAttr2()) ? "" : skuCmd.getInvAttr2());
        sku.setInvAttr3(StringUtils.isEmpty(skuCmd.getInvAttr3()) ? "" : skuCmd.getInvAttr3());
        sku.setInvAttr4(StringUtils.isEmpty(skuCmd.getInvAttr4()) ? "" : skuCmd.getInvAttr4());
        sku.setInvAttr5(StringUtils.isEmpty(skuCmd.getInvAttr5()) ? "" : skuCmd.getInvAttr5());
        sku.setSkuSn(StringUtils.isEmpty(skuCmd.getSkuSn()) ? "" : skuCmd.getSkuSn());
        sku.setSkuDefect(StringUtils.isEmpty(skuCmd.getSkuDefect()) ? "" : skuCmd.getSkuDefect());
        
        String skuAttrId = SkuCategoryProvider.getSkuAttrIdBySkuCmd(sku);
        locSkuAttrQty = (null == skuAttrIdsQty.get(skuAttrId) ? 1 : skuAttrIdsQty.get(skuAttrId).intValue());
        if(cacheSkuQty > 1 && cacheSkuQty <= locSkuAttrQty){
            if(0 != (locSkuAttrQty%cacheSkuQty)){
                // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
                log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, sourceSkuQty, logId);
                throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
            }
        }
        
        CheckScanSkuResultCommand cssrCmd = pdaContainerMoveCacheManager.sysContainerSplitMoveCacheSkuAndCheck(sourceContainerCode, insideContainerSkuAttrIds, skuAttrIdsQty, insideContainerSkuAttrIdsSnDefect, skuCmd, logId);
        if (cssrCmd.isNeedTipSkuSn()) {
//            // 执行部分上架
//            if(cssrCmd.isPartlyPutaway()){
//                sysGuideBoxContainerMoveInv(sourceContainerCode, sourceContainerId, containerLatticNo,targetContainerCode,scanType, warehouse, skuCmd, false,movePattern, ouId, userId, logId);
//            }
            // 当前商品还未扫描，继续扫sn残次信息
            srCmd.setNeedScanSkuSn(true);// 继续扫sn
            srCmd.setScanPattern(scanPattern);
            srCmd.setScanSkuQty(cssrCmd.getScanSkuQty());            
            srCmd.setIsContinueScanSn(true);
            String tipSkuAttrId = cssrCmd.getTipSkuAttrId();
            Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
            SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
            if (null == cacheSku) {
                log.error("sku is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            Sku tipSku = cacheSku.getSku();
            tipSkuDetailAspect(srCmd, tipSkuAttrId, skuAttrIdsQty, logId);
            srCmd.setTipSkuBarcode(tipSku.getBarCode());
            if (false == cssrCmd.isTipSameSkuAttrId()) {
                setTipSkuFromBox(sourceContainerCode,insideContainerAllSkuAttrIdsSn, tipSkuAttrId, logId);
            }
            if (log.isInfoEnabled()) {
                log.info("splitContainerMoveCheckScanSkuConfirm putawayTipSkuSn, sourceContainerCode is:[{}], sourceContainerId is:[{}], containerLatticNo is:[{}], targetContainerCode is:[{}],scanPattern is:[{}], barCode is:[{}],funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]",
                    new Object[] {sourceContainerCode, (null != sourceContainerId ? sourceContainerId : 0),(null != containerLatticNo ? containerLatticNo : 0), targetContainerCode, (null != containerMoveFunc ? containerMoveFunc.getScanPattern() : ""), (null != skuCmd ? skuCmd.getBarCode() : ""), funcId, ouId, userId, logId});
            }
        } else if (cssrCmd.isNeedTipSku()) {
            // 执行部分移库
            if(cssrCmd.isPartlyPutaway()){
                sysGuideBoxContainerMoveInv(sourceContainerCode, sourceContainerId, containerLatticNo,targetContainerCode,targetContainerId,scanType, warehouse, skuCmd, false,movePattern, ouId, userId, logId);
            }
            // 当前商品复核完毕，提示下一个商品
            srCmd.setNeedTipSku(true);// 提示下一个sku
            srCmd.setScanPattern(scanPattern);
            srCmd.setScanSkuQty(cssrCmd.getScanSkuQty());
            String tipSkuAttrId = cssrCmd.getTipSkuAttrId();
            Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
            SkuRedisCommand cacheSku = skuRedisManager.findSkuMasterBySkuId(skuId, ouId, logId);
            if (null == cacheSku) {
                log.error("sku is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            Sku tipSku = cacheSku.getSku();
            tipSkuDetailAspect(srCmd, tipSkuAttrId, skuAttrIdsQty, logId);
            srCmd.setTipSkuBarcode(tipSku.getBarCode());
            if (false == cssrCmd.isTipSameSkuAttrId()) {
                setTipSkuFromBox(sourceContainerCode,insideContainerAllSkuAttrIdsSn, tipSkuAttrId, logId);
            } else {
                //相同的产品扫描完成后画面提示需要减去上次以扫描过的数量
                int oldQty = srCmd.getTipSkuQty();
                int tmpScanQty = skuCmd.getScanSkuQty() == null ? 0 : skuCmd.getScanSkuQty().intValue();
                srCmd.setTipSkuQty(oldQty-tmpScanQty);
            }
            if (log.isInfoEnabled()) {
                log.info("splitContainerMoveCheckScanSkuConfirm isNeedTipSku, sourceContainerCode is:[{}], sourceContainerId is:[{}], containerLatticNo is:[{}], targetContainerCode is:[{}],scanPattern is:[{}], barCode is:[{}],funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]",
                    new Object[] {sourceContainerCode, (null != sourceContainerId ? sourceContainerId : 0),(null != containerLatticNo ? containerLatticNo : 0), targetContainerCode, (null != containerMoveFunc ? containerMoveFunc.getScanPattern() : ""), (null != skuCmd ? skuCmd.getBarCode() : ""), funcId, ouId, userId, logId});
            }
        } else {
            // 执行移库
            srCmd.setPutaway(true);
            sysGuideBoxContainerMoveInv(sourceContainerCode, sourceContainerId, containerLatticNo,targetContainerCode,targetContainerId,scanType, warehouse, skuCmd, true,movePattern, ouId, userId, logId);
            if (log.isInfoEnabled()) {
                log.info("splitContainerMoveCheckScanSkuConfirm putaway, sourceContainerCode is:[{}], sourceContainerId is:[{}], containerLatticNo is:[{}], targetContainerCode is:[{}],scanPattern is:[{}], barCode is:[{}],funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]",
                    new Object[] {sourceContainerCode, (null != sourceContainerId ? sourceContainerId : 0),(null != containerLatticNo ? containerLatticNo : 0), targetContainerCode, (null != containerMoveFunc ? containerMoveFunc.getScanPattern() : ""), (null != skuCmd ? skuCmd.getBarCode() : ""), funcId, ouId, userId, logId});
            }
            if (WhContainerMoveType.IN_CONTAINER.equals(scanType) || WhContainerMoveType.OUT_CONTAINER.equals(scanType)) {
                /**判断源周转箱是否没有库存了，如果没有库存需要释放周转箱*/
                List<WhSkuInventoryCommand> containerInvList = whSkuInventoryDao.findSkuInventoryAndSnInfo(null, null, null, null,sourceContainerId, ouId);
                if (null == containerInvList || containerInvList.size() == 0) {
                    // 释放移动完商品的周转箱
                    Container container = containerManager.getContainerById(sourceContainerId, ouId);
                    container.setOperatorId(userId);
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                    container.setIsFull(false);
                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                    containerManager.saveOrUpdateByVersion(container);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
            }
            //通过配置查看是否需要打印箱标签
            if (null != containerMoveFunc.getIsPrintCartonLabel() && containerMoveFunc.getIsPrintCartonLabel()) {
                PrintDataCommand printDataCommand = new PrintDataCommand();
                printDataCommand.setOutBoundBoxCode(targetContainerCode);
                printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_1, userId, ouId);
            }
            cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, sourceContainerCode);
            cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, sourceContainerCode);
            cacheManager.remonKeys(CacheConstants.SCAN_SKU_QUEUE + sourceContainerCode + "*");
            cacheManager.remonKeys(CacheConstants.SCAN_SKU_SN_QUEUE + sourceContainerCode + "*");
            cacheManager.remonKeys(CacheConstants.SCAN_SKU_SN_COUNT + sourceContainerCode + "*");
        }
        if (log.isInfoEnabled()) {
            log.info("splitContainerMoveCheckScanSkuConfirm end, sourceContainerCode is:[{}], sourceContainerId is:[{}], containerLatticNo is:[{}], targetContainerCode is:[{}],scanPattern is:[{}], barCode is:[{}],funcId is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]",
                    new Object[] {sourceContainerCode, (null != sourceContainerId ? sourceContainerId : 0),(null != containerLatticNo ? containerLatticNo : 0), targetContainerCode, (null != containerMoveFunc ? containerMoveFunc.getScanPattern() : ""), (null != skuCmd ? skuCmd.getBarCode() : ""), funcId, ouId, userId, logId});
        }
        return srCmd;
    }
    
    public void sysGuideBoxContainerMoveInv(String sourceContainerCode, Long sourceContainerId, Integer containerLatticNo,String targetContainerCode,Long targetContainerId, String scanType, Warehouse warehouse, WhSkuCommand skuCmd, boolean isSavePatten,Integer movePatten, Long ouId, Long userId, String logId) {
        String saId = SkuCategoryProvider.getSkuAttrIdBySkuCmd(skuCmd);
        //当前商品扫描数量
        Double scanSkuQty = skuCmd.getScanSkuQty();
        ScanSkuCacheCommand tipScanSkuSnCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_SN_QUEUE + sourceContainerCode + saId);
        if (null == tipScanSkuSnCmd) {
            log.error("cache is error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        //同一个商品属性包含的所有移动的sn数据
        Map<String, List<String>> scanSkuAttrIdsSn = new HashMap<String, List<String>>();
        List<String> skuAttrIds = tipScanSkuSnCmd.getScanSkuAttrIds();
        scanSkuAttrIdsSn.put(saId, skuAttrIds);
        //同一个商品属性包含的所有移动的商品数据
        Map<String, Double> scanSkuAttrIdsQty = new HashMap<String, Double>();
        scanSkuAttrIdsQty.put(saId, scanSkuQty);
        
        //部分移动模式
        if (WhContainerMoveType.PART_BOX_MOVE == movePatten) {
            whSkuInventoryManager.execuContainerMoveInventory(sourceContainerCode, sourceContainerId, containerLatticNo, targetContainerCode, targetContainerId, scanType, scanSkuAttrIdsSn, warehouse,scanSkuAttrIdsQty, ouId, userId, logId);
            //部分移动时,整箱移动完成,清楚缓存信息
            if (isSavePatten) {
                cacheManager.remove(CacheConstants.SCAN_SKU_QTY_COUNT + sourceContainerCode);
            } else {
              //部分移动时,增加缓存信息
              cacheManager.incrBy(CacheConstants.SCAN_SKU_QTY_COUNT + sourceContainerCode, scanSkuQty.intValue());
            }
        } else {
            //整箱移动模式,先把扫描的信息存入缓存,再判断是否需要保存
            Map<String, List<String>> redisScanSkuAttridsSn = cacheManager.getObject(CacheConstants.SCAN_SKU_SN_FOR_CONTAINER + sourceContainerCode);
            Map<String, Double> redisScanSkuAttridsQty = cacheManager.getObject(CacheConstants.SCAN_SKU_QTY_FOR_CONTAINER + sourceContainerCode);
            
            if (null == redisScanSkuAttridsSn || redisScanSkuAttridsSn.size() == 0) {
                cacheManager.setObject(CacheConstants.SCAN_SKU_SN_FOR_CONTAINER + sourceContainerCode, scanSkuAttrIdsSn, CacheConstants.CACHE_ONE_DAY);
                cacheManager.setObject(CacheConstants.SCAN_SKU_QTY_FOR_CONTAINER + sourceContainerCode, scanSkuAttrIdsQty, CacheConstants.CACHE_ONE_DAY);
            } else {
                if (redisScanSkuAttridsSn.containsKey(saId)) {
                    List<String> skuAttridSnList = redisScanSkuAttridsSn.get(saId);
                    skuAttridSnList.addAll(skuAttrIds);
                    redisScanSkuAttridsSn.put(saId, skuAttridSnList);
                    Double scanSkuAllQty = redisScanSkuAttridsQty.get(saId);
                    scanSkuAllQty = scanSkuAllQty + scanSkuQty;
                    redisScanSkuAttridsQty.put(saId, scanSkuAllQty);
                } else {
                    redisScanSkuAttridsSn.put(saId, skuAttrIds);
                    redisScanSkuAttridsQty.put(saId, scanSkuQty);
                }
                cacheManager.setObject(CacheConstants.SCAN_SKU_SN_FOR_CONTAINER + sourceContainerCode, redisScanSkuAttridsSn, CacheConstants.CACHE_ONE_DAY);
                cacheManager.setObject(CacheConstants.SCAN_SKU_QTY_FOR_CONTAINER + sourceContainerCode, redisScanSkuAttridsQty, CacheConstants.CACHE_ONE_DAY);
            }
            
            if (isSavePatten) {
                Map<String, List<String>> finalScanSkuAttridsSn = cacheManager.getObject(CacheConstants.SCAN_SKU_SN_FOR_CONTAINER + sourceContainerCode);
                Map<String, Double> finalScanSkuAttridsQty = cacheManager.getObject(CacheConstants.SCAN_SKU_QTY_FOR_CONTAINER + sourceContainerCode);
                whSkuInventoryManager.execuContainerMoveInventory(sourceContainerCode, sourceContainerId, containerLatticNo, targetContainerCode,targetContainerId,scanType, finalScanSkuAttridsSn, warehouse,finalScanSkuAttridsQty, ouId, userId, logId);
                cacheManager.remove(CacheConstants.SCAN_SKU_SN_FOR_CONTAINER + sourceContainerCode);
                cacheManager.remove(CacheConstants.SCAN_SKU_QTY_FOR_CONTAINER + sourceContainerCode);
            }
        }
        // 清除计数器
        cacheManager.remove(CacheConstants.SCAN_SKU_SN_QUEUE + sourceContainerCode + saId);
        cacheManager.remove(CacheConstants.SCAN_SKU_SN_COUNT + sourceContainerCode + saId);
    }
    
    /***
     * 取消出库箱移库操作的所有缓存
       * @param containerCode
       * @param targetContainerCode
       * @param boxMove
       * @param ouId
       * @param logId
       * @param userId
     * @return
     */
    @Override
    public void moveFinishScanContainer(String sourceContainerCode,String targetContainerCode,WhFunctionContainerMove containerMove,String scanType,Long ouId,String logId,Long userId) {
        if (log.isInfoEnabled()) {
            log.info("quitScanContainer start, sourceContainerCode is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]",
                    new Object[] {sourceContainerCode, ouId, userId, logId});
        }
        if (WhContainerMoveType.PART_BOX_MOVE == containerMove.getMovePattern() && !WhContainerMoveType.IN_CONTAINER.equals(scanType)) {
            //判断部分移动是否移动过商品
            long scanAllQty = cacheManager.incrBy(CacheConstants.SCAN_SKU_QTY_COUNT + sourceContainerCode, 0);
            if (scanAllQty > 0) {
                //通过配置查看是否需要打印箱标签
                if (containerMove.getIsPrintCartonLabel()) {
                    PrintDataCommand printDataCommand = new PrintDataCommand();
                    printDataCommand.setOutBoundBoxCode(targetContainerCode);
                    printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_1, userId, ouId);
                }
            }
        }
        //清除当前操作的所有redis缓存
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, sourceContainerCode);
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, sourceContainerCode);
        cacheManager.remonKeys(CacheConstants.SCAN_SKU_QUEUE + sourceContainerCode + "*");
        cacheManager.remonKeys(CacheConstants.SCAN_SKU_SN_QUEUE + sourceContainerCode + "*");
        cacheManager.remonKeys(CacheConstants.SCAN_SKU_SN_COUNT + sourceContainerCode + "*");
        cacheManager.remove(CacheConstants.SCAN_SKU_SN_FOR_CONTAINER + sourceContainerCode);
        cacheManager.remove(CacheConstants.SCAN_SKU_QTY_FOR_CONTAINER + sourceContainerCode);
        cacheManager.remove(CacheConstants.SCAN_SKU_QTY_COUNT + sourceContainerCode);
        
        if (log.isInfoEnabled()) {
            log.info("quitScanContainer end, sourceContainerCode is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]",
                    new Object[] {sourceContainerCode, ouId, userId, logId});
        }
    }

}
