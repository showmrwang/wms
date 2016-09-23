package com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
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
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ContainerStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.constant.WhContainerType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternType;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerAssistDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaPutawayCacheManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationRecommendManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionPutAwayManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPutAway;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleCubeCalculator;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;


/**
 * @author tangming
 *
 */
@Service("pdaSysSuggestPutwayManager")
@Transactional
public class PdaSysSuggestPutwayManagerImpl extends BaseManagerImpl implements PdaSysSuggestPutwayManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaSysSuggestPutwayManagerImpl.class);
    

    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhAsnDao whAsnDao;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    PdaPutawayCacheManager pdaPutawayCacheManager;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhCartonDao whCartonDao;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private WhSkuDao whSkuDao;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private WhLocationDao locationDao;
    @Autowired
    private ContainerAssistDao containerAssistDao;
    @Autowired
    private RuleManager ruleManager;
    @Autowired
    private WhLocationRecommendManager whLocationRecommendManager;
    @Autowired
    private UomDao uomDao;
    @Autowired
    private WhSkuInventoryLogManager whSkuInventoryLogManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhFunctionPutAwayManager whFunctionPutAwayManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
    
    /***
     * 整托上架
     * @param containerCode
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    @Override
    public ScanResultCommand sysSuggestScanContainer(String containerCode,Long ouId,String logId,Long userId,Integer putawayPatternDetailType,Long funcId,String outerContainerCode,Warehouse warehouse) {
        // TODO Auto-generated method stub
        log.info("PdaSysSuggestPutwayManagerImpl containerInfoJudge is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaSysSuggestPutwayManagerImpl containerInfoJudge param , ouId is:[{}],  containerCode is:[{}]", ouId,  containerCode);
        }
        if(putawayPatternDetailType == WhPutawayPatternDetailType.PALLET_PUTAWAY) {  //整托上架
            //根据容器号,查询容器信息
            ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
            ScanResultCommand  sanResult =  this.palletPutwayScanContainer(containerCmd, ouId, logId, userId);  //整托容器信息判断流程
            //缓存容器库存
            List<WhSkuInventoryCommand>  invList =  this.palletPutwayCacheInventory(containerCmd, ouId, logId);
            //容器库存统计缓存
            InventoryStatisticResultCommand isrCmd = this.cacheContainerInventoryStatistics(invList,userId, ouId, logId, containerCmd, sanResult, putawayPatternDetailType,outerContainerCode);
            ScanResultCommand srCommand = new ScanResultCommand();
            // 4.判断是否已推荐库位
            if (null != isrCmd) {
                Set<Long> locationIds = isrCmd.getLocationIds();
                if (null == locationIds || 0 == locationIds.size()) {
                    //走没有推荐库位分支
                    srCommand = this.palletPutwayNoLocation(isrCmd, ouId, userId, sanResult, funcId, containerCmd, invList,warehouse,putawayPatternDetailType);
                }else{
                    //已经推荐库位
                    pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCmd, ouId, logId);
                    srCommand = this.reminderLocation(isrCmd, sanResult, ouId);
                }
            }else{  
                pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryAndStatistic(containerCmd, ouId, logId);
                log.error("sys guide pallet putaway cache is error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            return srCommand;
        }else if(putawayPatternDetailType == WhPutawayPatternDetailType.CONTAINER_PUTAWAY) {   //整箱上架
            //根据容器号,查询容器信息
            ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
            ScanResultCommand  scanResult =  this.containerPutwayScanContainer(outerContainerCode,containerCmd, ouId, logId, userId);  //整托容器信息判断流程
            if(scanResult.isHasOuterContainer()) {   //扫描的是外部容器
                return scanResult;   //返回扫描容器页面,提示容器号
            }
            //如果扫描的不是外部容器,缓存内部容器库存
            List<WhSkuInventoryCommand>  invList =  this.containerPutwayCacheInventory(containerCmd, ouId, logId);
            //容器库存统计缓存
            InventoryStatisticResultCommand isrCmd = this.cacheContainerInventoryStatistics(invList,userId, ouId, logId, containerCmd, scanResult, putawayPatternDetailType,outerContainerCode);
            ScanResultCommand srCommand = new ScanResultCommand();
            // 4.判断是否已推荐库位
            if (null != isrCmd) {
                Set<Long> locationIds = isrCmd.getLocationIds();
                if (null == locationIds || 0 == locationIds.size()) {
                    //走没有推荐库位分支
                    srCommand = this.containerPutWayNoLocation(isrCmd, ouId, userId, scanResult, funcId, containerCmd, invList,warehouse, putawayPatternDetailType);
                }else{
                    //已经推荐库位
                    pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryStatistic(containerCmd, isrCmd, ouId, logId);
                    srCommand = this.reminderLocation(isrCmd, scanResult, ouId);
                }
            }else{  
                //整箱上架缓存库存统计信息
                pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryStatistic(containerCmd, ouId, logId);
                log.error("sys guide pallet putaway cache is error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            return srCommand;   //下一步扫库位
        }else {   //拆箱上架
            ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
            ScanResultCommand  scanResult = this.splitContainerPutwayScanContainer(outerContainerCode,containerCmd, ouId, logId, userId);
            if(scanResult.isHasOuterContainer()) {   //扫描的是外部容器
                return scanResult;   //返回扫描容器页面,提示一个内容器号
            }
            //如果扫描的不是外部容器,缓存内部容器库存
            List<WhSkuInventoryCommand>  invList =  this.splitPutwayCacheInventory(containerCmd, ouId, logId);
            //容器库存统计缓存
            InventoryStatisticResultCommand isrCmd = this.cacheContainerInventoryStatistics(invList,userId, ouId, logId, containerCmd, scanResult, putawayPatternDetailType,outerContainerCode);
            ScanResultCommand srCommand = new ScanResultCommand();
            // 4.判断是否已推荐库位
            if (null != isrCmd) {
                Set<Long> locationIds = isrCmd.getLocationIds();
                if (null == locationIds || 0 == locationIds.size()) {
                    //走没有推荐库位分支
                    srCommand = this.splitPutWayNoLocation(isrCmd, ouId, userId, scanResult, funcId, containerCmd, invList,putawayPatternDetailType,warehouse);
                }else{
                    //已经推荐库位
                    pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventoryStatistic(containerCmd, isrCmd, ouId, logId);
                    srCommand = this.reminderLocation(isrCmd, scanResult, ouId);
                }
            }else{  
                //拆箱上架缓存库存统计信息
                pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventoryStatistic(containerCmd, ouId, logId);
                log.error("sys guide pallet putaway cache is error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            return srCommand;   //下一步扫库位 
        }
    }
    
    /**
     * 拆箱上架：容器信息流程判断
     * @param containerCmd
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    private ScanResultCommand splitContainerPutwayScanContainer(String outerContainerCode,ContainerCommand containerCmd,Long ouId,String logId,Long userId){
        log.info("PdaSysSuggestPutwayManagerImpl scanContainerByBox is start"); 
        ScanResultCommand srCmd = new ScanResultCommand();  //扫描返回结果
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_SUGGEST_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (!containerCmd.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        
        // 验证容器状态是否是待上架
        if (!(containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        Long containerId = containerCmd.getId();
        String containerCode = containerCmd.getCode();
        //根据库存盘点是否有外部容器
        int outerCount = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId,containerId);  //根据容器id，查询容器是否是外部
        int insideCount = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId,containerId); //根据容器id，查询容器是否是内部
        if (0 < insideCount) {
            // 拆箱上架判断是是否有外部容器号
            int ocCount = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId1(ouId, containerId);
            if (0 < ocCount) {
                if(StringUtils.isEmpty(outerContainerCode)) {
                    log.error("sys guide container putaway scan container has outer container, should scan outer container first, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_HAS_OUTER_CONTAINER_SCAN_OUTER_FIRST, new Object[] {containerCode});
                }
            } else {
                srCmd.setContainerType(WhContainerType.INSIDE_CONTAINER);// 内部容器,无外部容器，无需循环提示容器
                srCmd.setInsideContainerCode(containerCode);
                //缓存扫描的内部容器
                TipContainerCacheCommand tipCmd = new TipContainerCacheCommand();
                tipCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                tipCmd.setOuterContainerId(containerId);
                tipCmd.setOuterContainerCode(containerCmd.getCode());
                ArrayDeque<Long> icIds = new ArrayDeque<Long>();
                icIds.addFirst(containerId);
                tipCmd.setTipInsideContainerIds(icIds);
                cacheManager.setObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), tipCmd, CacheConstants.CACHE_ONE_DAY);
            }
        }
        if (0 < outerCount) {
            srCmd.setContainerType(WhContainerType.OUTER_CONTAINER);// 外部容器
            srCmd.setHasOuterContainer(true);// 有外部容器，需要循环提示容器
            srCmd.setOuterContainerCode(containerCmd.getCode());    //外部容器号
            ContainerStatisticResultCommand cacheCsr = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerId.toString());  //containerId,外部容器id
            ContainerStatisticResultCommand csrCmd  = null;
            if (null == cacheCsr) {
                // 缓存所有内部容器统计信息
                csrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);   //缓存信息外部Id
            } else {
                csrCmd = cacheCsr;
            }
            // 获取所有内部容器id
            Set<Long> insideContainerIds = csrCmd.getInsideContainerIds();
            Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
            // 提示一个内部容器
            Long tipContainerId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipContainer0(containerCmd, insideContainerIds, logId);
            srCmd.setNeedTipContainer(true);
            String tipContainerCode = null;
            if (null != insideContainerIdsCode) {
                tipContainerCode = insideContainerIdsCode.get(tipContainerId);
                if (StringUtils.isEmpty(tipContainerCode)) {
                    Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
                    if (null == tipContainer) {
                        log.error("container is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                    }
                    tipContainerCode = tipContainer.getCode();
                }
            }
            srCmd.setTipContainerCode(tipContainerCode);
            //修改外部容器状态
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
                Container container = new Container();
                BeanUtils.copyProperties(containerCmd, container);
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
                containerDao.saveOrUpdateByVersion(container);
                srCmd.setOuterContainerCode(containerCode);// 外部容器号
                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
            }
        }
        if(0 >= insideCount && 0 >= outerCount){
            // 无收货库存
            log.error("sys guide pallet putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        // 修改内部容器状态
        if(srCmd.getContainerType() != WhContainerType.OUTER_CONTAINER ) {   //当前扫描的容器是内部容器
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
                Container container = new Container();
                BeanUtils.copyProperties(containerCmd, container);
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
                containerDao.saveOrUpdateByVersion(container);
                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
            }
        }
        log.info("PdaSysSuggestPutwayManagerImpl scanContainerByBox is end"); 
        return srCmd;
    }
    
    
    /**
     * 整箱上架容器信息判断流程
     * @param containerCmd
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    private ScanResultCommand containerPutwayScanContainer(String outerContainerCode,ContainerCommand containerCmd,Long ouId,String logId,Long userId){
        log.info("PdaSysSuggestPutwayManagerImpl scanContainerByBox is start"); 
        ScanResultCommand srCmd = new ScanResultCommand();  //扫描返回结果
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_SUGGEST_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 验证容器状态是否可用
        if (!containerCmd.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        
        // 验证容器状态是否是待上架
        if (!(containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        Long containerId = containerCmd.getId();
        String containerCode = containerCmd.getCode();
        //根据库存盘点是否有外部容器
        int outerCount = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId,containerId);  //根据容器id，查询容器是否是外部
        int insideCount = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId,containerId); //根据容器id，查询容器是否是内部
        if (0 < insideCount) {
         // 整箱上架判断是是否有外部容器号
            int ocCount = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId1(ouId, containerId);
            if (0 < ocCount) {
                //判断外部容器是否已经扫描
                if(StringUtils.isEmpty(outerContainerCode)){   //外部容器没有扫描
                    log.error("sys guide container putaway scan container has outer container, should scan outer container first, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                    throw new BusinessException(ErrorCodes.CONTAINER_HAS_OUTER_CONTAINER_SCAN_OUTER_FIRST, new Object[] {containerCode});
                }
            } else {
                srCmd.setContainerType(WhContainerType.INSIDE_CONTAINER);// 内部容器,无外部容器，无需循环提示容器
                srCmd.setInsideContainerCode(containerCode);
                //缓存扫描的内部容器
                TipContainerCacheCommand tipCmd = new TipContainerCacheCommand();
                tipCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                tipCmd.setOuterContainerId(containerId);
                tipCmd.setOuterContainerCode(containerCmd.getCode());
                ArrayDeque<Long> icIds = new ArrayDeque<Long>();
                icIds.addFirst(containerId);
                tipCmd.setTipInsideContainerIds(icIds);
                cacheManager.setObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), tipCmd, CacheConstants.CACHE_ONE_DAY);
            }
        }
        if (0 < outerCount) {
            srCmd.setContainerType(WhContainerType.OUTER_CONTAINER);// 外部容器
            srCmd.setHasOuterContainer(true);// 有外部容器，需要循环提示容器
            srCmd.setOuterContainerCode(containerCmd.getCode());    //外部容器号
            ContainerStatisticResultCommand cacheCsr = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerId.toString());
            ContainerStatisticResultCommand csrCmd  = null;
            if (null == cacheCsr) {
                // 缓存所有内部容器统计信息
                csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);   //缓存信息外部Id
                cacheManager.setMapObject(CacheConstants.CONTAINER_STATISTIC, containerId.toString(),cacheCsr, CacheConstants.CACHE_ONE_DAY);
            } else {
                csrCmd = cacheCsr;
            }
            // 获取所有内部容器id
            Set<Long> insideContainerIds = csrCmd.getInsideContainerIds();
            Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
            // 提示一个容器
            Long tipContainerId = pdaPutawayCacheManager.sysGuideContainerPutawayTipContainer0(containerCmd, insideContainerIds, logId);
            srCmd.setNeedTipContainer(true);
            String tipContainerCode = null;
            if (null != insideContainerIdsCode) {
                tipContainerCode = insideContainerIdsCode.get(tipContainerId);
                if (StringUtils.isEmpty(tipContainerCode)) {
                    Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
                    if (null == tipContainer) {
                        log.error("container is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                    }
                    tipContainerCode = tipContainer.getCode();
                }
            }
            srCmd.setTipContainerCode(tipContainerCode);
            //修改外部容器状态
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
                Container container = new Container();
                BeanUtils.copyProperties(containerCmd, container);
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
                containerDao.saveOrUpdateByVersion(container);
                srCmd.setOuterContainerCode(containerCode);// 外部容器号
                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
            }
        } 
        if(0 >= insideCount && 0 >= outerCount){
            // 无收货库存
            log.error("sys guide pallet putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        // 修改内部容器状态
        if(srCmd.getContainerType() != WhContainerType.OUTER_CONTAINER ) {   //当前扫描的容器是内部容器
            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
                Container container = new Container();
                BeanUtils.copyProperties(containerCmd, container);
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
                containerDao.saveOrUpdateByVersion(container);
                srCmd.setOuterContainerCode(containerCode);// 外部容器号
                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
            }
        }
        log.info("PdaSysSuggestPutwayManagerImpl scanContainerByBox is end"); 
        return srCmd;
    }

   /***
    * 整托容器信息判断流程
    * @param containerCmd
    * @param ouId
    * @param logId
    * @param userId
    * @return
    */
    private ScanResultCommand palletPutwayScanContainer(ContainerCommand containerCmd,Long ouId,String logId,Long userId) {
        // TODO Auto-generated method stub
        log.info("PdaSysSuggestPutwayManagerImpl scanContainerBypallet is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaSysSuggestPutwayManagerImpl scanContainerBypallet param , ouId is:[{}],  containerCmd is:[{}]", ouId,  containerCmd);
        }
        ScanResultCommand srCmd = new ScanResultCommand();  //扫描返回结果
        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_SUGGEST_PUTAWAY);
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        String containerCode = containerCmd.getCode();
        // 验证容器状态是否可用
        if (!containerCmd.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        
        // 验证容器状态是否是待上架
        if (!(containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
            log.error("container lifecycle is not normal, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        //根据库存盘点是否有外部容器
        int outerCount = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId,containerCmd.getId());  //根据容器id，查询容器是否是外部
        int insideCount = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId,containerCmd.getId()); //根据容器id，查询容器是否是内部
        if (0 < insideCount) {
            // 整托上架只能扫外部容器号
            log.error("sys Recommend putaway scan container is insideContainer error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_IS_INSIDE_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        if (0 < outerCount) {
            srCmd.setContainerType(WhContainerType.OUTER_CONTAINER);// 外部容器
            srCmd.setHasOuterContainer(true);// 有外部容器
            srCmd.setOuterContainerCode(containerCmd.getCode());    //外部容器号
            Long outContainerId = containerCmd.getId();
            ContainerStatisticResultCommand cacheCsr = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, outContainerId.toString());
            if (null == cacheCsr) {
                // 缓存所有内部容器统计信息
                cacheCsr = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                cacheManager.setMapObject(CacheConstants.CONTAINER_STATISTIC, outContainerId.toString(), cacheCsr, CacheConstants.CACHE_ONE_DAY);
            } 
        } else {
            // 无收货库存
            log.error("sys guide pallet putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        // 1.先修改外部容器状态为：上架中，且占用中,另外所有的内部容器状态可以在库存信息统计完成以后再修改
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
            Container container = new Container();
            BeanUtils.copyProperties(containerCmd, container);
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
            containerDao.saveOrUpdateByVersion(container);
            srCmd.setOuterContainerCode(containerCode);// 外部容器号
            insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
        }
        
        log.info("PdaSysSuggestPutwayManagerImpl scanContainerBypallet is end"); 
        return srCmd;
    }
    
    /**
     * 整托上架:缓存容器库存
     * @param containerCmd(外部容器)
     * @param ouId
     * @param logId
     * @return
     */
    private List<WhSkuInventoryCommand>  palletPutwayCacheInventory(ContainerCommand containerCmd,Long ouId,String logId) {
        log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventory is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventory param  containerCmd is:[{}]",  containerCmd);
        }
        Long containerId = containerCmd.getId();
        // 2.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> cacheInvs = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString());
        List<WhSkuInventoryCommand> invList = null;
        if (null == cacheInvs || 0 == cacheInvs.size()) {
            //缓存容器库存信息
            invList = pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventory(containerCmd, ouId, logId);
            cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString(), invList, CacheConstants.CACHE_ONE_DAY);   
        } else {
            invList = cacheInvs;
        }
        log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventory is end"); 
        return invList;
    } 
    
    /**
     * 整箱上架:缓存内部容器库存
     * @param containerCmd
     * @param ouId
     * @param logId
     * @return
     */
    private List<WhSkuInventoryCommand>  containerPutwayCacheInventory(ContainerCommand containerCmd,Long ouId,String logId) {
        log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventory is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventory param  containerCmd is:[{}]",  containerCmd);
        }
        Long containerId = containerCmd.getId();
        // 2.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> cacheInvs = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString());
        List<WhSkuInventoryCommand> invList = null;
        if (null == cacheInvs || 0 == cacheInvs.size()) {
            //缓存容器库存信息
            invList = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventory(containerCmd, ouId, logId);
            cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString(), invList, CacheConstants.CACHE_ONE_DAY);   
        } else {
            invList = cacheInvs;
        }
        log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventory is end"); 
        return invList;
    } 
    
    /**
     * 拆箱上架:缓存容器库存
     * @param containerCmd(外部容器)
     * @param ouId
     * @param logId
     * @return
     */
    private List<WhSkuInventoryCommand>  splitPutwayCacheInventory(ContainerCommand containerCmd,Long ouId,String logId) {
        log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventory is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventory param  containerCmd is:[{}]",  containerCmd);
        }
        Long containerId = containerCmd.getId();
        // 2.判断是否已经缓存所有库存信息
        List<WhSkuInventoryCommand> cacheInvs = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString());
        List<WhSkuInventoryCommand> invList = null;
        if (null == cacheInvs || 0 == cacheInvs.size()) {
            //缓存容器库存信息
            invList = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventory(containerCmd, ouId, logId);
            cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerId.toString(), invList, CacheConstants.CACHE_ONE_DAY);   
        } else {
            invList = cacheInvs;
        }
        log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventory is end"); 
        return invList;
    } 
    
    /**
     * 库存信息分析统计流程
     * @param invList
     * @param ouId
     * @param logId
     * @param containerCmd
     * @param srCmd
     * @param putWay
     * @return
     */
    private InventoryStatisticResultCommand cacheContainerInventoryStatistics(List<WhSkuInventoryCommand> invList,Long userId,Long ouId,String logId,ContainerCommand containerCmd,ScanResultCommand srCmd,Integer putawayPatternDetailType,String outerContainerCode) {
           log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventoryStatistics is start"); 
            Long containerId = containerCmd.getId(); 
            String containerCode = containerCmd.getCode(); 
            // 3.库存信息统计
            InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
            Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器
            Set<Long> caselevelContainerIds = new HashSet<Long>();// 所有caselevel内部容器
            Set<Long> notcaselevelContainerIds = new HashSet<Long>();// 所有非caselevel内部容器
            Set<Long> skuIds = new HashSet<Long>();// 所有sku种类
            Long skuQty = 0L;// sku总件数
            Set<String> skuAttrIds = new HashSet<String>();// 所有唯一sku(包含库存属性)
            Set<Long> storeIds = new HashSet<Long>();// 所有店铺
            Set<Long> locationIds = new HashSet<Long>();// 所有推荐库位
            Map<Long, Set<Long>> insideContainerSkuIds = new HashMap<Long, Set<Long>>();// 内部容器对应的所有sku种类
            Map<Long, Long> insideContainerSkuQty = new HashMap<Long, Long>();// 内部容器所有sku总件数
            Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = new HashMap<Long, Map<Long, Long>>();// 内部容器单个sku总件数
            Map<Long, Set<String>> insideContainerSkuAttrIds = new HashMap<Long, Set<String>>();// 内部容器唯一sku(skuId|库存装填|库存类型|生产日期|失效日期|库存属性1|库存属性2|库存属性3|库存属性4|库存属性51|)
            Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = new HashMap<Long, Map<String, Long>>();// 内部容器唯一sku总件数
            /** 内部容器唯一sku对应所有残次条码 和sn*/
            Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>(); //内部容器内唯一sku对应所有sn及残次条码
            Double outerContainerWeight = 0.0;
            Double outerContainerVolume = 0.0;
            Map<Long, Double> insideContainerWeight = new HashMap<Long, Double>();// 内部容器重量
            Map<Long, Double> insideContainerVolume = new HashMap<Long, Double>();// 内部容器体积
            Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();   //长度，度量单位转换率
            Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();  //重量，度量单位转换率
            List<UomCommand> lenUomCmds = null;   //长度度量单位
            List<UomCommand> weightUomCmds = null;   //重量度量单位
            Map<Long, ContainerAssist> insideContainerAsists = new HashMap<Long, ContainerAssist>();
            SimpleCubeCalculator cubeCalculator = new SimpleCubeCalculator(lenUomConversionRate);
            SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(weightUomConversionRate);
            if(null == isrCmd) {
                lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
                for (UomCommand lenUom : lenUomCmds) {
                    String uomCode = "";
                    Double uomRate = 0.0;
                    if (null != lenUom) {
                        uomCode = lenUom.getUomCode();
                        uomRate = lenUom.getConversionRate();
                        lenUomConversionRate.put(uomCode, uomRate);
                    }
                }
                weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
                for (UomCommand lenUom : weightUomCmds) {
                    String uomCode = "";
                    Double uomRate = 0.0;
                    if (null != lenUom) {
                        uomCode = lenUom.getUomCode();
                        uomRate = lenUom.getConversionRate();
                        weightUomConversionRate.put(uomCode, uomRate);
                    }
                }
                isrCmd = new InventoryStatisticResultCommand();
                for (WhSkuInventoryCommand invCmd : invList) { 
                    String asnCode = invCmd.getOccupationCode();
                    if (StringUtils.isEmpty(asnCode)) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("rcvd inv info error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.RCVD_INV_INFO_NOT_OCCUPY_ERROR);
                    }
                    WhAsn asn = whAsnDao.findAsnByCodeAndOuId(asnCode, ouId);
                    if (null == asn) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("asn is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_ASN_IS_NULL_ERROR, new Object[] {asnCode});
                    }
                    if (PoAsnStatus.ASN_RCVD_FINISH != asn.getStatus() && PoAsnStatus.ASN_RCVD != asn.getStatus()) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("asn status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_ASN_STATUS_ERROR, new Object[] {asnCode});
                    }
                    Long poId = asn.getPoId();
                    WhPo po = whPoDao.findWhPoById(poId, ouId);
                    if (null == po) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("po is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.PO_NULL);
                    }
                    String poCode = po.getPoCode();
                    if (PoAsnStatus.PO_RCVD != po.getStatus() && PoAsnStatus.PO_RCVD_FINISH != po.getStatus()) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("po status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
                        throw new BusinessException(ErrorCodes.COMMON_PO_STATUS_ERROR, new Object[] {poCode});
                    }
                    Long icId = invCmd.getInsideContainerId();
                    Container ic = null;
                    if (null == icId || null == (ic = containerDao.findByIdExt(icId, ouId))) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("sys guide pallet putaway inside container is not found, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                    } else {
                        insideContainerIds.add(icId);   //统计所有内部容器
                        srCmd.setHasInsideContainer(true);
                    }
                    // 验证容器状态是否可用
                    if (!BaseModel.LIFECYCLE_NORMAL.equals(ic.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != ic.getLifecycle()) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("sys guide pallet putaway inside container lifecycle is not normal, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                    }
                    // 获取容器状态
                    Integer icStatus = ic.getStatus();
                    if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != icStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != icStatus) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("sys guide pallet putaway inside container status is invalid, icId is:[{}], containerStatus is:[{}], logId is:[{}]", icId, icStatus, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {ic.getCode()});
                    }
                    Long insideContainerCate = ic.getTwoLevelType();   
                    Container2ndCategory insideContainer2 = container2ndCategoryDao.findByIdExt(insideContainerCate, ouId);
                    if (null == insideContainer2) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("sys guide pallet putaway container2ndCategory is null error, icId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", icId, insideContainerCate, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
                    }
                    if (1 != insideContainer2.getLifecycle()) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("sys guide pallet putaway container2ndCategory lifecycle is not normal error, icId is:[{}], containerId is:[{}], logId is:[{}]", icId, insideContainer2.getId(), logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                    }
                    Double icLength = insideContainer2.getLength();
                    Double icWidth = insideContainer2.getWidth();
                    Double icHeight = insideContainer2.getHigh();
                    Double icWeight = insideContainer2.getWeight();
                    if (null == icLength || null == icWidth || null == icHeight) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("sys guide pallet putaway inside container length、width、height is null error, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
                    }
                    if (null == icWeight) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("sys guide pallet putaway inside container weight is null error, icId is:[{}], logId is:[{}]", icId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
                    }
                    Double icVolume = cubeCalculator.calculateStuffVolume(icLength, icWidth, icHeight);  //根据长宽高，返回容器体积
                    insideContainerVolume.put(icId, icVolume);
                    WhCarton carton = whCartonDao.findWhCaselevelCartonById(icId, ouId);
                    if (null != carton) {
                        caselevelContainerIds.add(icId);  //统计caselevel内部容器信息
                    } else {
                        notcaselevelContainerIds.add(icId);   //统计非caselevel内部容器信息
                    }
                    String invType = invCmd.getInvType();
                    if (StringUtils.isEmpty(invType)) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("inv type is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                    }
                    List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_TYPE, invType, BaseModel.LIFECYCLE_NORMAL);  //根据字段组编码及参数值查询字典信息
                    if (null == invTypeList || 0 == invTypeList.size()) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("inv type is not defined error, invType is:[{}], logId is:[{}]", invType, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                    }
                    Long invStatus = invCmd.getInvStatus();
                    if (null == invStatus) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("inv status is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                    }
                    InventoryStatus status = new InventoryStatus();
                    status.setId(invStatus);
                    List<InventoryStatus> invStatusList = inventoryStatusManager.findInventoryStatusList(status);
                    if (null == invStatusList || 0 == invStatusList.size()) {
                        pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                        log.error("inv status is not defined error, invStatusId is:[{}], logId is:[{}]", invStatus, logId);
                        throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                    }
                    String invAttr1 = invCmd.getInvAttr1();
                    if (!StringUtils.isEmpty(invAttr1)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_1, invAttr1, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                            log.error("inv attr1 is not defined error, invAttr1 is:[{}], logId is:[{}]", invAttr1, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR1_NOT_FOUND_ERROR, new Object[] {invAttr1});
                        }
                    }
                    String invAttr2 = invCmd.getInvAttr2();
                    if (!StringUtils.isEmpty(invAttr2)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_2, invAttr2, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                            log.error("inv attr2 is not defined error, invAttr2 is:[{}], logId is:[{}]", invAttr2, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR1_NOT_FOUND_ERROR, new Object[] {invAttr2});
                        }
                    }
                    String invAttr3 = invCmd.getInvAttr3();
                    if (!StringUtils.isEmpty(invAttr3)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_3, invAttr3, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                            log.error("inv attr3 is not defined error, invAttr3 is:[{}], logId is:[{}]", invAttr3, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR3_NOT_FOUND_ERROR, new Object[] {invAttr3});
                        }
                    }
                    String invAttr4 = invCmd.getInvAttr4();
                    if (!StringUtils.isEmpty(invAttr4)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_4, invAttr4, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                            log.error("inv attr4 is not defined error, invAttr4 is:[{}], logId is:[{}]", invAttr4, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR4_NOT_FOUND_ERROR, new Object[] {invAttr4});
                        }
                    }
                    String invAttr5 = invCmd.getInvAttr5();
                    if (!StringUtils.isEmpty(invAttr5)) {
                        List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_5, invAttr5, BaseModel.LIFECYCLE_NORMAL);
                        if (null == list || 0 == list.size()) {
                            pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                            log.error("inv attr5 is not defined error, invAttr5 is:[{}], logId is:[{}]", invAttr5, logId);
                            throw new BusinessException(ErrorCodes.COMMON_INV_ATTR5_NOT_FOUND_ERROR, new Object[] {invAttr5});
                        }
                    }
                    Long skuId = invCmd.getSkuId();
                    Double toBefillQty = invCmd.getToBeFilledQty();   //待移入库存 
                    Double onHandQty = invCmd.getOnHandQty();   //在库库存
                    Double curerntSkuQty = 0.0;     //当前sku数量
                    Long locationId = invCmd.getLocationId();
                    if (null != locationId) {
                        locationIds.add(locationId);    //所有推荐库位
                        if (null != toBefillQty) {
                            curerntSkuQty = toBefillQty;
                            skuQty += toBefillQty.longValue();
                        }
                    } else {
                        if (null == onHandQty || 0 <= new Double("0.0").compareTo(onHandQty)) {
                            pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                            log.error("rcvd inv onHandQty is less than 0 error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.RCVD_INV_SKU_QTY_ERROR);
                        }
                        if (null != onHandQty) {
                            curerntSkuQty = onHandQty;
                            skuQty += onHandQty.longValue();
                        }
                    }
                    if (null != skuId) {
                        skuIds.add(skuId);    //所有sku种类数
                        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
                        if (null == skuCmd) {
                            pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                            log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                        }
                        Double skuLength = skuCmd.getLength();
                        Double skuWidth = skuCmd.getWidth();
                        Double skuHeight = skuCmd.getHeight();
                        Double skuWeight = skuCmd.getWeight();
                        if (null == skuLength || null == skuWidth || null == skuHeight) {
                            pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                            log.error("sys guide pallet putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
                        }
                        if (null == skuWeight) {
                            pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
                            log.error("sys guide pallet putaway sku weight is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
                            throw new BusinessException(ErrorCodes.SKU_WEIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
                        }
                        if (null != insideContainerWeight.get(icId)) {
                            insideContainerWeight.put(icId, insideContainerWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                        } else {
                            // 先计算容器自重
                            insideContainerWeight.put(icId, weightCalculator.calculateStuffWeight(icWeight));
                            // 再计算当前商品重量
                            insideContainerWeight.put(icId, insideContainerWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                        }
                    }
                    skuAttrIds.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));    //所有唯一的sku(包含库存属性)
                    Long stroeId = invCmd.getStoreId();
                    if (null != stroeId) {
                        storeIds.add(stroeId);  //统计所有店铺
                    }
                    if (null != insideContainerSkuIds.get(icId)) {
                        Set<Long> icSkus = insideContainerSkuIds.get(icId);
                        icSkus.add(skuId);
                        insideContainerSkuIds.put(icId, icSkus);   //统计内部容器对应所有的sku
                    } else {
                        Set<Long> icSkus = new HashSet<Long>();
                        icSkus.add(skuId);
                        insideContainerSkuIds.put(icId, icSkus);
                    }
                    if (null != insideContainerSkuQty.get(icId)) {
                        insideContainerSkuQty.put(icId, insideContainerSkuQty.get(icId) + curerntSkuQty.longValue());  //统计内部容器对应所有sku的总件数
                    } else {
                        insideContainerSkuQty.put(icId, curerntSkuQty.longValue());  
                    }
                    if (null != insideContainerSkuIdsQty.get(icId)) {
                        Map<Long, Long> skuIdsQty = insideContainerSkuIdsQty.get(icId);
                        if (null != skuIdsQty.get(skuId)) {
                            skuIdsQty.put(skuId, skuIdsQty.get(skuId) + curerntSkuQty.longValue());
                        } else {
                            skuIdsQty.put(skuId, curerntSkuQty.longValue());
                        }
                    } else {
                        Map<Long, Long> sq = new HashMap<Long, Long>();
                        sq.put(skuId, curerntSkuQty.longValue());
                        insideContainerSkuIdsQty.put(icId, sq);                     //统计内部容器对应某个sku的总件数
                    }
                    if (null != insideContainerSkuAttrIds.get(icId)) {
                        Set<String> icSkus = insideContainerSkuAttrIds.get(icId);
                        icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                        insideContainerSkuAttrIds.put(icId, icSkus);                        
                    } else {
                        Set<String> icSkus = new HashSet<String>();
                        icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                        insideContainerSkuAttrIds.put(icId, icSkus);  //统计内部容器所有的唯一sku
                    }
                    if (null != insideContainerSkuAttrIdsQty.get(icId)) {
                        Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(icId);
                        if (null != skuAttrIdsQty.get(skuId)) {
                            skuAttrIdsQty.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), skuAttrIdsQty.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd)) + curerntSkuQty.longValue());
                        } else {
                            skuAttrIdsQty.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
                        }
                    } else {
                        Map<String, Long> saq = new HashMap<String, Long>();
                        saq.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
                        insideContainerSkuAttrIdsQty.put(icId, saq);
                    }
                    if (null == insideContainerAsists.get(icId)) {
                        ContainerAssist containerAssist = new ContainerAssist();
                        containerAssist.setContainerId(icId);
                        containerAssist.setSysLength(icLength);
                        containerAssist.setSysWidth(icWidth);
                        containerAssist.setSysHeight(icHeight);
                        containerAssist.setSysVolume(icVolume);
                        containerAssist.setCartonQty(1L);
                        containerAssist.setCreateTime(new Date());
                        containerAssist.setLastModifyTime(new Date());
                        containerAssist.setOperatorId(userId);
                        containerAssist.setOuId(ouId);
                        containerAssist.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
                        insideContainerAsists.put(icId, containerAssist);
                    }
                    if(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {  //拆箱上架
                      //内部容器内唯一sku对应所有sn及残次条码
                        List<WhSkuInventorySnCommand> snCmdList = invCmd.getWhSkuInventorySnCommandList();
                        Set<String> snDefects = null;
                        if (null != snCmdList && 0 < snCmdList.size()) {
                            snDefects = new HashSet<String>();
                            for (WhSkuInventorySnCommand snCmd : snCmdList) {
                                if (null != snCmd) {
                                    String defectBar = snCmd.getDefectWareBarcode();
                                    String sn = snCmd.getSn();
                                    snDefects.add(SkuCategoryProvider.concatSkuAttrId(sn, defectBar));
                                }
                            }
                        }
                        if (null != snDefects) {
                            if (null != insideContainerSkuAttrIdsSnDefect.get(icId)) {
                                Map<String, Set<String>> skuAttrIdsDefect = insideContainerSkuAttrIdsSnDefect.get(icId);
                                if (null != skuAttrIdsDefect.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd))) {
                                    Set<String> defects = skuAttrIdsDefect.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                                    defects.addAll(snDefects);
                                    skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), defects);
                                    insideContainerSkuAttrIdsSnDefect.put(icId, skuAttrIdsDefect);
                                } else {
                                    skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snDefects);
                                    insideContainerSkuAttrIdsSnDefect.put(icId, skuAttrIdsDefect);
                                }
                            } else {
                                Map<String, Set<String>> skuAttrIdsDefect = new HashMap<String, Set<String>>();
                                skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snDefects);
                                insideContainerSkuAttrIdsSnDefect.put(icId, skuAttrIdsDefect);
                            }
                        }
                    }
                    
                }
                
                isrCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                isrCmd.setHasOuterContainer(true);
                isrCmd.setInsideContainerIds(insideContainerIds);  //所有内部容器
                isrCmd.setCaselevelContainerIds(notcaselevelContainerIds);  //所有caselevel内部容器
                isrCmd.setNotcaselevelContainerIds(notcaselevelContainerIds);  //所有非caselevel内部容器
                isrCmd.setSkuIds(skuIds);   // 所有sku种类
                isrCmd.setSkuQty(skuQty);// sku总件数
                isrCmd.setSkuAttrIds(skuAttrIds);   // 所有唯一sku(包含库存属性)
                isrCmd.setStoreIds(storeIds); // 所有店铺
                isrCmd.setLocationIds(locationIds);// 所有推荐库位
                isrCmd.setInsideContainerSkuIdsQty(insideContainerSkuIdsQty);    // 内部容器对应的所有sku总件数
                isrCmd.setInsideContainerSkuIds(insideContainerSkuIds);  // 内部容器对应的所有sku种类
                isrCmd.setInsideContainerSkuQty(insideContainerSkuQty);
                isrCmd.setInsideContainerSkuAttrIdsQty(insideContainerSkuAttrIdsQty);  // 内部容器单个sku总件数
                isrCmd.setInsideContainerSkuAttrIds(insideContainerSkuAttrIds);// 内部容器唯一sku(skuId|库存装填|库存类型|生产日期|失效日期|库存属性1|库存属性2|库存属性3|库存属性4|库存属性51|)
                isrCmd.setInsideContainerSkuAttrIdsSnDefect(insideContainerSkuAttrIdsSnDefect); //内部容器内唯一sku对应所有sn及残次条码
                if(WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType){   //整托上架
                    isrCmd.setOuterContainerCode(containerCmd.getCode());  //外部容器号
                }else{//整箱上架,拆箱上架
                    isrCmd.setOuterContainerCode(outerContainerCode);  //外部容器号
                    isrCmd.setInsideContainerCode(containerCmd.getCode());   // 当前扫描的内部容器
                }
               
                isrCmd.setInsideContainerAsists(insideContainerAsists);
                isrCmd.setOuterContainerVolume(outerContainerVolume);  //外部容器体积
                isrCmd.setOuterContainerWeight(outerContainerWeight);  //外部容器自重
                if(WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
                    isrCmd.setOuterContainerId(containerId);   //外部容器id(整托的时候是外部id)
                }
                if(WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
                    isrCmd.setInsideContainerId(containerId);   //整箱上架时,是内部id
                }
                
            }
            //修改所有的内部容器状态,待上架，改为上架中
            for(Long insideContainerId:insideContainerIds) {
                Container container = containerDao.findByIdExt(insideContainerId, ouId);
                if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
                    container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
                    containerDao.saveOrUpdateByVersion(container);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
                }
            }
            // 计算外部容器体积重量
            Long outerContainerCate = containerCmd.getTwoLevelType();
            Container2ndCategory outerContainer2 = container2ndCategoryDao.findByIdExt(outerContainerCate, ouId);
            if (null == outerContainer2) {
                log.error("container2ndCategory is null error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", containerId, outerContainerCate, logId);
                throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
            }
            if (1 != outerContainer2.getLifecycle()) {
                log.error("container2ndCategory lifecycle is not normal error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", containerId, outerContainer2.getId(), logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
            }
            Double ocLength = outerContainer2.getLength();
            Double ocWidth = outerContainer2.getWidth();
            Double ocHeight = outerContainer2.getHigh();
            Double ocWeight = outerContainer2.getWeight();
            if (null == ocLength || null == ocWidth || null == ocHeight) {
                log.error("sys guide pallet putaway inside container length、width、height is null error, ocId is:[{}], logId is:[{}]", containerId, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {containerCode});
            }
            if (null == ocWeight) {
                log.error("sys guide pallet putaway inside container weight is null error, ocId is:[{}], logId is:[{}]", containerId, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {containerCode});
            }
            outerContainerWeight = weightCalculator.calculateStuffWeight(ocWeight);
            outerContainerVolume = cubeCalculator.calculateStuffVolume(ocLength, ocWidth, ocHeight);
            isrCmd.setInsideContainerVolume(insideContainerVolume);
            isrCmd.setInsideContainerWeight(insideContainerWeight);
            log.info("PdaSysSuggestPutwayManagerImpl cacheContainerInventoryStatistics is end"); 
            return isrCmd;
    }
    
    /**
     * 提示库位编码
     * @param isrCmd
     * @param srCmd
     * @param ouId
     * @return
     */
    private ScanResultCommand reminderLocation(InventoryStatisticResultCommand isrCmd,ScanResultCommand srCmd,Long ouId) {
        log.info("PdaSysSuggestPutwayManagerImpl isSuggestLocation is start"); 
        Set<Long> locationIds = isrCmd.getLocationIds(); 
        if (0 < locationIds.size()) {
            srCmd.setRecommendLocation(true);// 已推荐库位
            if (1 < locationIds.size()) {   //整托上架已经推荐库位而且绑架多个库位，认定异常
                log.error("sys guide pallet putaway location is more than one error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
            }
            Long locId = null;
            for (Long locationId : locationIds) {
                if (null == locId) {
                    locId = locationId;
                    if (null != locId) break;
                }
            }
            Location loc = locationDao.findByIdExt(locId, ouId);
            if (null == loc) {
                log.error("location is null error, locId is:[{}], logId is:[{}]", locId, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            srCmd.setTipLocationCode(loc.getCode());// 提示库位编码
            srCmd.setNeedTipLocation(true);// 提示库位
            srCmd.setRecommendFail(true);   //已经存在库位
            srCmd.setContainerType(WhContainerType.INSIDE_CONTAINER);   //扫描的是内部容器
        }
        log.info("PdaSysSuggestPutwayManagerImpl isSuggestLocation is end"); 
        return srCmd;
    }
    
    
    /**
     * 拆箱上架：没有推荐库位分支
     * @param isrCommand
     * @param ouId
     * @param userId
     */
    private ScanResultCommand splitPutWayNoLocation(InventoryStatisticResultCommand  isrCommand,Long ouId,Long userId,ScanResultCommand srCmd,Long funcId,ContainerCommand containerCmd,List<WhSkuInventoryCommand>  invList,Integer putawayPatternDetailType,Warehouse warehouse) {
        log.info("PdaSysSuggestPutwayManagerImpl updateContainer is start"); 
        Long containerId = containerCmd.getId();  //货箱id
        String containerCode = containerCmd.getCode();  //货箱号
        // 内部容器辅助表信息
        Map<Long, ContainerAssist> caMap = new HashMap<Long, ContainerAssist>();
        List<Long> containerids = new ArrayList<Long> ();
        containerids.add(containerId);
        //修改当前内部容器状态
        Container container = containerDao.findByIdExt(containerId , ouId);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);   //上架中
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);  //占用中
        containerDao.saveOrUpdateByVersion(container);
        //更新容器辅助表:先根据扫描的容器Id删除容器辅助表中已经存在的记录行,再将新的统计信息更新到容器辅助表
        containerAssistDao.deleteByContainerIds(ouId, containerids);
        Map<Long,Double> insideContainerWeight = isrCommand.getInsideContainerWeight();
        Map<Long,Double> insideContainerVolume = isrCommand.getInsideContainerVolume();
        //增加新的容器辅助表信息,只更新内部容器
        Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(containerCmd.getTwoLevelType(), ouId);
        ContainerAssist cAssist = new ContainerAssist();
        cAssist.setContainerId(containerId);
        cAssist.setSysLength(c2c.getLength());
        cAssist.setSysWidth(c2c.getWidth());
        cAssist.setSysHeight(c2c.getHigh());
        cAssist.setSysVolume(insideContainerVolume.get(containerId));
        cAssist.setSysWeight(insideContainerWeight.get(containerId));
        cAssist.setCartonQty(Long.valueOf(isrCommand.getInsideContainerIds().size()));   //托盘内部所有容器数
        cAssist.setSkuAttrCategory(Long.valueOf(isrCommand.getSkuAttrIds().size()));   //sku种类数
        cAssist.setSkuQty(isrCommand.getSkuQty());   //sku总件数
        cAssist.setStoreQty(Long.valueOf(isrCommand.getStoreIds().size()));
        cAssist.setSkuCategory(Long.valueOf(isrCommand.getSkuIds().size()));
        cAssist.setCreateTime(new Date());
        cAssist.setOperatorId(userId);
        cAssist.setOuId(ouId);
        cAssist.setLastModifyTime(new Date());
        containerAssistDao.insert(cAssist);
        insertGlobalLog(GLOBAL_LOG_INSERT, cAssist, ouId, userId, null, null);
        //添加内部容器辅助信息
        caMap.put(containerId, cAssist);
        // 6.匹配上架规则
        List<String> icCodeList = new ArrayList<String>();
        icCodeList.add(isrCommand.getInsideContainerCode());
        List<Long> storeList = new ArrayList<Long>();
        CollectionUtils.addAll(storeList, isrCommand.getStoreIds().iterator());
        List<Long> icIdList = new ArrayList<Long>();
        CollectionUtils.addAll(icIdList, isrCommand.getInsideContainerIds().iterator());
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setLogId(logId);
        ruleAffer.setOuid(ouId);
        ruleAffer.setAfferContainerCode(containerCode);    //内部容器号
        ruleAffer.setAfferInsideContainerIdList(icIdList);   //内部容器id
        ruleAffer.setContainerId(containerId);
        ruleAffer.setFuncId(funcId);
        ruleAffer.setAfferContainerCodeList(icCodeList);
        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE);// 拆箱上架
        ruleAffer.setStoreIdList(storeList);
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        // 判断该容器是否有符合的上架规则,有则走库位推荐排队系统,没有抛出异常
        List<WhSkuInventoryCommand> ruleList = export.getShelveSkuInvCommandList();  //拆箱上架规则返回
        if (null == ruleList || 0 == ruleList.size()) {
            log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
        }
        // 7.库位推荐排队系统     推荐库位
        if (null == caMap || 0 == caMap.size()) {
            log.error("container assist info generate error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
        }
        // 判断排队队列是否已经排队
        boolean isRecommend = pdaPutawayCacheManager.sysGuidePutawayLocRecommendQueue(containerCmd.getId(), logId);
        if (false == isRecommend) {  //需要等待排队,否则得到库位推荐执行权
            srCmd.setNeedQueueUp(true);   //需要排队
            return srCmd;  //跳转到扫描容器页面
        }
        //推荐库位流程
        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
        uomMap.put(WhUomType.LENGTH_UOM, isrCommand.getLenUomConversionRate());
        uomMap.put(WhUomType.WEIGHT_UOM, isrCommand.getWeightUomConversionRate());
        List<LocationRecommendResultCommand> lrrList = null;
        try {
            lrrList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, caMap, invList, uomMap, logId);
        } catch (Exception e1) {
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
            throw e1;
        }
        if (null == lrrList || 0 == lrrList.size() || StringUtils.isEmpty(lrrList.get(0).getLocationCode())) {
            srCmd.setRecommendFail(false);   //推荐库位失败
            return srCmd;
        }
        Set<Long> suggestLocationIds = new HashSet<Long>();
        Map<String, Long> invRecommendLocId = new HashMap<String, Long>();
        Map<String, String> invRecommendLocCode = new HashMap<String, String>();
        Map<Long, Set<String>> locSkuAttrIds = new HashMap<Long, Set<String>>();
        Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = new HashMap<Long, Map<Long, Set<String>>>();
        for (LocationRecommendResultCommand lrrCmd : lrrList) {
            Long locationId = lrrCmd.getLocationId();
            if (null != locationId) {
                suggestLocationIds.add(locationId);
                if (null != locSkuAttrIds.get(locationId)) {
                    Set<String> allSkuAttrIds = locSkuAttrIds.get(locationId);
                    allSkuAttrIds.add(lrrCmd.getSkuAttrId());
                    locSkuAttrIds.put(locationId, allSkuAttrIds);
                } else {
                    Set<String> allSkuAttrIds = new HashSet<String>();
                    allSkuAttrIds.add(lrrCmd.getSkuAttrId());
                    locSkuAttrIds.put(locationId, allSkuAttrIds);
                }
            }
            String locationCode = lrrCmd.getLocationCode();
            invRecommendLocId.put(lrrCmd.getSkuAttrId(), locationId);
            invRecommendLocCode.put(lrrCmd.getSkuAttrId(), locationCode);
        }
        if (0 == locSkuAttrIds.size()) {
            log.error("location recommend failure! containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_RECOMMEND_ERROR);
        } else {
            insideContainerLocSkuAttrIds.put(containerId, locSkuAttrIds);
        }
        isrCommand.setLocationIds(suggestLocationIds);
        isrCommand.setInsideContainerLocSkuAttrIds(insideContainerLocSkuAttrIds);
        //缓存容器库存统计信息
        pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
        //库位绑定
        //先待移入库位库存，将on_hand_qty(在库库存)值设置到to_be_filled_qty(待移入库存)、将on_hand_qty设置为0.0、为每行库存记录增加库位插入一条新的库存记录（注意更新uuid），即插入待移入库位库存。如果有SN/残次信息则同样插入SN/残次信息记录（一待入）
        whSkuInventoryManager.binding(invList, warehouse, lrrList, putawayPatternDetailType, ouId, userId, containerCode);
        // 弹出排队队列
        pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
        // 10.提示库位
        srCmd.setRecommendLocation(true);// 已推荐库位
        LocationRecommendResultCommand lrr = lrrList.get(0);
        Long locationId = lrr.getLocationId();  //推荐库位id
        Long lrrLocId =  pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipLocation(containerCmd,suggestLocationIds,locationId,logId);
        Location loc = locationDao.findByIdExt(lrrLocId, ouId);
        if (null == loc) {
            log.error("location is null error, locId is:[{}], logId is:[{}]", lrrLocId, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
        }
        srCmd.setRecommendFail(true);   //推荐库位成功
        srCmd.setTipLocationCode(loc.getCode());// 提示库位编码
        srCmd.setContainerType(WhContainerType.INSIDE_CONTAINER);   //扫描的是内部容器
        srCmd.setNeedTipLocation(true);// 提示库位
        log.info("PdaSysSuggestPutwayManagerImpl updateContainer is end"); 
        
        return srCmd;
    }
    
    /**
     * 整箱上架：没有推荐库位分支
     * @param isrCommand
     * @param ouId
     * @param userId
     */
    private ScanResultCommand containerPutWayNoLocation(InventoryStatisticResultCommand  isrCommand,Long ouId,Long userId,ScanResultCommand srCmd,Long funcId,ContainerCommand containerCmd,List<WhSkuInventoryCommand>  invList,Warehouse warehouse,Integer  putawayPatternDetailType) {
        log.info("PdaSysSuggestPutwayManagerImpl updateContainer is start"); 
        Long containerId = containerCmd.getId();  //货箱id
        Long c2cId = containerCmd.getTwoLevelType();   
        // 内部容器辅助表信息
        Map<Long, ContainerAssist> caMap = new HashMap<Long, ContainerAssist>();
        Set<Long> insideContainerIds = isrCommand.getInsideContainerIds();  //获得所有内部容器id(整箱上架，就一个内部容器id)
        //修改当前内部容器状态
        for(Long insideContainerId:insideContainerIds) {
            Container container = containerDao.findByIdExt(insideContainerId, ouId);
            container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);   //上架中
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);  //占用中
            containerDao.saveOrUpdateByVersion(container);
        }
        //更新容器辅助表:先根据扫描的容器Id删除容器辅助表中已经存在的记录行,再将新的统计信息更新到容器辅助表
        List<Long> containerids = new ArrayList<Long> (insideContainerIds);
        containerAssistDao.deleteByContainerIds(ouId, containerids);
        Map<Long,Double> insideContainerWeight = isrCommand.getInsideContainerWeight();
        Map<Long,Double> insideContainerVolume = isrCommand.getInsideContainerVolume();
        //增加新的容器辅助表信息,只更新内部容器
        for(Long insideContainerId:insideContainerIds) {
            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(c2cId, ouId);
            ContainerAssist cAssist = new ContainerAssist();
            cAssist.setContainerId(insideContainerId);
            cAssist.setSysLength(c2c.getLength());
            cAssist.setSysWidth(c2c.getWidth());
            cAssist.setSysHeight(c2c.getHigh());
            cAssist.setSysVolume(insideContainerVolume.get(insideContainerId));
            cAssist.setSysWeight(insideContainerWeight.get(insideContainerId));
            cAssist.setCartonQty(Long.valueOf(isrCommand.getInsideContainerIds().size()));   //托盘内部所有容器数
            cAssist.setSkuAttrCategory(Long.valueOf(isrCommand.getSkuAttrIds().size()));   //sku种类数
            cAssist.setSkuQty(isrCommand.getSkuQty());   //sku总件数
            cAssist.setStoreQty(Long.valueOf(isrCommand.getStoreIds().size()));
            cAssist.setSkuCategory(Long.valueOf(isrCommand.getSkuIds().size()));
            cAssist.setOuId(ouId);
            cAssist.setOperatorId(userId);
            cAssist.setCreateTime(new Date());
            cAssist.setLastModifyTime(new Date());
            containerAssistDao.insert(cAssist);
            caMap.put(insideContainerId,cAssist);
            insertGlobalLog(GLOBAL_LOG_INSERT, cAssist, ouId, userId, null, null);
        }
        // 6.匹配上架规则
        List<String> icCodeList = new ArrayList<String>();
        icCodeList.add(isrCommand.getInsideContainerCode());
        List<Long> storeList = new ArrayList<Long>();
        CollectionUtils.addAll(storeList, isrCommand.getStoreIds().iterator());
        List<Long> icIdList = new ArrayList<Long>();
        CollectionUtils.addAll(icIdList, isrCommand.getInsideContainerIds().iterator());
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setLogId(logId);
        ruleAffer.setOuid(ouId);
        ruleAffer.setAfferContainerCode(srCmd.getOuterContainerCode());    //原始容器号
        ruleAffer.setAfferInsideContainerIdList(icIdList);   //内部容器id
        ruleAffer.setContainerId(containerCmd.getId());
        ruleAffer.setFuncId(funcId);
        ruleAffer.setAfferContainerCodeList(icCodeList);
        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE_ALL);// 整托 、货箱上架规则
        ruleAffer.setStoreIdList(storeList);
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        // 判断该容器是否有符合的上架规则,有则走库位推荐排队系统,没有抛出异常
        List<ShelveRecommendRuleCommand> ruleList = export.getShelveRecommendRuleList();
        if (null == ruleList || 0 == ruleList.size()) {
            log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
        }
        // 7.库位推荐排队系统     推荐库位
        if (null == caMap || 0 == caMap.size()) {
            log.error("container assist info generate error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
        }
        // 判断排队队列是否已经排队
        boolean isRecommend = pdaPutawayCacheManager.sysGuidePutawayLocRecommendQueue(containerCmd.getId(), logId);
        if (false == isRecommend) {  //需要等待排队,否则得到库位推荐执行权
            srCmd.setNeedQueueUp(true);   //需要排队
            return srCmd;  //跳转到扫描容器页面
        }
        //推荐库位流程
        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
        uomMap.put(WhUomType.LENGTH_UOM, isrCommand.getLenUomConversionRate());
        uomMap.put(WhUomType.WEIGHT_UOM, isrCommand.getWeightUomConversionRate());
        List<LocationRecommendResultCommand> lrrList = null;
        try {
            lrrList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, caMap, invList, uomMap, logId);
        } catch (Exception e1) {
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
            throw e1;
        }
        if (null == lrrList || 0 == lrrList.size() || StringUtils.isEmpty(lrrList.get(0).getLocationCode())) {
            srCmd.setRecommendFail(false);   //推荐库位失败
            return srCmd;
        }
        LocationRecommendResultCommand lrr = lrrList.get(0);
        Long lrrLocId = lrr.getLocationId();  //推荐库位id
        String lrrLocCode = lrr.getLocationCode();  //推荐库位号
        Set<Long> suggestLocationIds = new HashSet<Long>();
        suggestLocationIds.add(lrrLocId);
        isrCommand.setLocationIds(suggestLocationIds);
        // 10.缓存容器库存统计信息
        pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
        //库位绑定
        //先待移入库位库存，将on_hand_qty(在库库存)值设置到to_be_filled_qty(待移入库存)、将on_hand_qty设置为0.0、为每行库存记录增加库位插入一条新的库存记录（注意更新uuid），即插入待移入库位库存。如果有SN/残次信息则同样插入SN/残次信息记录（一待入）
        whSkuInventoryManager.binding(invList, warehouse, lrrList, putawayPatternDetailType, ouId, userId, lrrLocCode);
        // 弹出排队队列
        pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
        // 10.提示库位
        srCmd.setRecommendFail(true);   //推荐库位成功
        srCmd.setRecommendLocation(true);// 已推荐库位
        srCmd.setTipLocationCode(lrrLocCode);// 提示库位编码
        srCmd.setNeedTipLocation(true);// 提示库位
        log.info("PdaSysSuggestPutwayManagerImpl updateContainer is end"); 
        return srCmd;
    }
    
    /**
     * 整托上架：没有推荐库位分支
     * @param isrCommand
     * @param ouId
     * @param userId
     */
    private ScanResultCommand palletPutwayNoLocation(InventoryStatisticResultCommand  isrCommand,Long ouId,Long userId,ScanResultCommand srCmd,Long funcId,ContainerCommand containerCmd,List<WhSkuInventoryCommand>  invList,Warehouse warehouse,Integer putawayPatternDetailType) {
        log.info("PdaSysSuggestPutwayManagerImpl updateContainer is start"); 
        Long containerId = containerCmd.getId();  //外部容器id
        Long outerContainerCate = containerCmd.getTwoLevelType();
        String outContainerCode = containerCmd.getCode();  //外部容器号
        // 内部容器辅助表信息
        Map<Long, ContainerAssist> caMap = new HashMap<Long, ContainerAssist>();
        Long outerContainerId = isrCommand.getOuterContainerId();   //获取外部容器id
        Set<Long> insideContainerIds = isrCommand.getInsideContainerIds();  //获得所有内部容器id
        List<Long> containerids = new ArrayList<Long> ();
        CollectionUtils.addAll(containerids, insideContainerIds.iterator());
        //修改所有内部容器状态
        for(Long insideContainerId:containerids) {
            Container container = containerDao.findByIdExt(insideContainerId, ouId);
            container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);   //上架中
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);  //占用中
            containerDao.saveOrUpdateByVersion(container);
        }
        containerids.add(outerContainerId);     //添加外部容器id
        //删除该外部容器及内部容器对应的辅助表信息
        containerAssistDao.deleteByContainerIds(ouId, containerids);
        Map<Long,Double> insideContainerWeight = isrCommand.getInsideContainerWeight();
        Map<Long, ContainerAssist> insideContainerAsists = isrCommand.getInsideContainerAsists(); //内部容器辅助表统计信息
        Double icTotalWeight = 0.0;
      //如果没有外部容器只更新内部容器对应的辅助表信息
        for(Long insideContaineId:insideContainerIds) {
            icTotalWeight = icTotalWeight+insideContainerWeight.get(insideContaineId);   //计算所有内部容器的重量
            ContainerAssist cAssist = insideContainerAsists.get(insideContaineId);
            cAssist.setSysWeight(insideContainerWeight.get(insideContaineId));
            cAssist.setCartonQty(isrCommand.getInsideContainerIds().size()+ 0L);   //托盘内部所有容器数
            cAssist.setSkuAttrCategory(isrCommand.getSkuAttrIds().size()+ 0L);   //sku种类数
            cAssist.setSkuQty(isrCommand.getSkuQty());   //sku总件数
            cAssist.setStoreQty(isrCommand.getStoreIds().size()+ 0L);
            cAssist.setSkuCategory(isrCommand.getSkuIds().size()+0L);
            containerAssistDao.insert(cAssist);
            insertGlobalLog(GLOBAL_LOG_INSERT, cAssist, ouId, userId, null, null);
            caMap.put(insideContaineId, cAssist);// 所有内部容器辅助信息
        }
        //增加新的容器辅助表信息
        if(null != outerContainerId) {   //外部容器
            //更新外部容器对应的辅助表信息
            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(outerContainerCate, ouId);
            Double ocLength = c2c.getLength();
            Double ocWidth = c2c.getWidth();
            Double ocHeight = c2c.getHigh();
            Double ocWeight = c2c.getWeight();
            if (null == ocLength || null == ocWidth || null == ocHeight) {
                log.error("sys guide pallet putaway inside container length、width、height is null error, ocId is:[{}], logId is:[{}]", containerId, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {outContainerCode });
            }
            if (null == ocWeight) {
                log.error("sys guide pallet putaway inside container weight is null error, ocId is:[{}], logId is:[{}]", containerId, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {outContainerCode });
            }
            Double outerContainerWeight = isrCommand.getOuterContainerWeight();
            Double outerContainerVolume = isrCommand.getOuterContainerVolume();
            ContainerAssist cAssist = new ContainerAssist();
            cAssist.setContainerId(outerContainerId);
            cAssist.setSysLength(ocLength);
            cAssist.setSysWidth(ocWidth);
            cAssist.setSysHeight(ocHeight);
            cAssist.setSysVolume(outerContainerVolume);
            cAssist.setSysWeight(outerContainerWeight+icTotalWeight);
            cAssist.setCartonQty(Long.valueOf(isrCommand.getInsideContainerIds().size()));   //托盘内部所有容器数
            cAssist.setSkuAttrCategory(Long.valueOf(isrCommand.getSkuAttrIds().size()));   //sku种类数
            cAssist.setSkuQty(isrCommand.getSkuQty());   //sku总件数
            cAssist.setStoreQty(Long.valueOf(isrCommand.getStoreIds().size()));
            cAssist.setSkuCategory(Long.valueOf(isrCommand.getSkuIds().size()));
            cAssist.setOuId(ouId);
            cAssist.setCreateTime(new Date());
            cAssist.setLifecycle(Constants.LIFECYCLE_START);
            cAssist.setLastModifyTime(new Date()); 
            containerAssistDao.insert(cAssist);
            caMap.put(outerContainerId, cAssist);// 外部容器辅助信息
            insertGlobalLog(GLOBAL_LOG_INSERT, cAssist, ouId, userId, null, null);
        }
        // 6.匹配上架规则
        List<String> icCodeList = new ArrayList<String>();
        icCodeList.add(isrCommand.getInsideContainerCode());
        List<Long> storeList = new ArrayList<Long>();
        CollectionUtils.addAll(storeList, isrCommand.getStoreIds().iterator());
        List<Long> icIdList = new ArrayList<Long>();
        CollectionUtils.addAll(icIdList, isrCommand.getInsideContainerIds().iterator());
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setLogId(logId);
        ruleAffer.setOuid(ouId);
        ruleAffer.setAfferContainerCode(srCmd.getOuterContainerCode());    //原始容器号
        ruleAffer.setAfferInsideContainerIdList(icIdList);   //内部容器id
        ruleAffer.setContainerId(containerCmd.getId());
        ruleAffer.setFuncId(funcId);
        ruleAffer.setAfferContainerCodeList(icCodeList);
        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE_ALL);// 整托 、货箱上架规则
        ruleAffer.setStoreIdList(storeList);
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        // 判断该容器是否有符合的上架规则,有则走库位推荐排队系统,没有抛出异常
        List<ShelveRecommendRuleCommand> ruleList = export.getShelveRecommendRuleList();
        if (null == ruleList || 0 == ruleList.size()) {
            log.error("no available shelveRecommendRule, recommend location fail! logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.RECOMMEND_LOCATION_NO_RULE_ERROR);
        }
        //将lrrList存入缓存
        List<LocationRecommendResultCommand> list = cacheManager.getMapObject(CacheConstants.LOCATION_RECOMMEND,containerId.toString());  //整托上架，外部容器id
        if(null == list) {
            cacheManager.setMapObject(CacheConstants.LOCATION_RECOMMEND, containerId.toString(), ruleList, CacheConstants.CACHE_ONE_DAY);
        }
        // 7.库位推荐排队系统     推荐库位
        if (null == caMap || 0 == caMap.size()) {
            log.error("container assist info generate error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
        }
        // 判断排队队列是否已经排队
        boolean isRecommend = pdaPutawayCacheManager.sysGuidePutawayLocRecommendQueue(containerCmd.getId(), logId);
        if (false == isRecommend) {  //需要等待排队,否则得到库位推荐执行权
            srCmd.setNeedQueueUp(true);   
            return srCmd;  //跳转到扫描容器页面
        }
        //推荐库位流程
        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
        uomMap.put(WhUomType.LENGTH_UOM, null);
        uomMap.put(WhUomType.WEIGHT_UOM, null);
        List<LocationRecommendResultCommand> lrrList = null;
        try {
            lrrList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export, WhPutawayPatternDetailType.PALLET_PUTAWAY, caMap, invList, uomMap, logId);
        } catch (Exception e1) {
            // 弹出排队队列
            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
            throw e1;
        }
        if (null == lrrList || 0 == lrrList.size() || StringUtils.isEmpty(lrrList.get(0).getLocationCode())) {
              srCmd.setRecommendFail(false);   //推荐库位失败
              return srCmd;
        }
        LocationRecommendResultCommand lrr = lrrList.get(0);
        Long lrrLocId = lrr.getLocationId();  //推荐库位id
        String lrrLocCode = lrr.getLocationCode();  //推荐库位号
        Set<Long> suggestLocationIds = new HashSet<Long>();
        suggestLocationIds.add(lrrLocId);
        isrCommand.setLocationIds(suggestLocationIds);
        // 10.缓存容器库存统计信息
        pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
        //库位绑定
        //先待移入库位库存，将on_hand_qty(在库库存)值设置到to_be_filled_qty(待移入库存)、将on_hand_qty设置为0.0、为每行库存记录增加库位插入一条新的库存记录（注意更新uuid），即插入待移入库位库存。如果有SN/残次信息则同样插入SN/残次信息记录（一待入）
        whSkuInventoryManager.binding(invList, warehouse, lrrList, putawayPatternDetailType, ouId, userId, lrrLocCode);
        // 弹出排队队列
        pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
        // 10.提示库位
        srCmd.setRecommendFail(true);   //推荐库位成功
        srCmd.setRecommendLocation(true);// 已推荐库位
        srCmd.setTipLocationCode(lrrLocCode);// 提示库位编码
        srCmd.setNeedTipLocation(true);// 提示库位
        log.info("PdaSysSuggestPutwayManagerImpl updateContainer is end"); 
        
        return srCmd;
    }
    
    /***
     * 拆箱上架使用推荐库位上架
     * @param locationCode
     * @param containerCode
     * @param userId
     * @param ouId
     * @param srCmd
     * @return
     */
    @Override
    public ScanResultCommand splitUserSuggestLocation(String outContainerCode,String locationCode, String insideContainerCode, Long userId, Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaSysSuggestPutwayManagerImpl splitUserSuggestLocation is start"); 
        ScanResultCommand srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
        if (null == insideContainerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        Long containerId = insideContainerCmd.getId();   //内部容器id
        // 0.判断是否已经缓存所有库存信息,获取库存统计信息及当前功能参数scan_pattern         CacheConstants.CONTAINER_INVENTORY
        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
        if (null == isCmd) {
            isCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventoryStatistic(insideContainerCmd, ouId, logId);
        }
        // 1.提示商品并判断是否需要扫描属性
        Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = isCmd.getInsideContainerSkuAttrIdsQty();   //内部容器唯一sku总件数
        Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = isCmd.getInsideContainerLocSkuAttrIds();  //内部容器推荐库位对应唯一sku及残次条码
        Location loc = locationDao.findLocationByCode(locationCode, ouId);
        if (null == loc) {
            log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
        }
        Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(containerId);  //库位属性
        Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(containerId);   //内部容器唯一sku总件数
        String tipSkuAttrId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipSku0(insideContainerCmd, loc.getId(), locSkuAttrIds, logId);
        Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
        if (null == skuCmd) {
            log.error("sku is not found error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        tipSkuDetailAspect(srCmd, tipSkuAttrId, locSkuAttrIds, skuAttrIdsQty, logId);
        srCmd.setNeedTipSku(true);
        srCmd.setTipSkuBarcode(skuCmd.getBarCode());   //提示sku
        log.info("PdaSysSuggestPutwayManagerImpl splitUserSuggestLocation is end"); 
        return srCmd;
    }
    
    /***
     * 如果商品绑定多个库位，则提示库位
     * @param srCmd
     * @param tipSkuAttrId
     * @param locSkuAttrIds
     * @param skuAttrIdsQty
     * @param logId
     */
    private void tipSkuDetailAspect(ScanResultCommand srCmd, String tipSkuAttrId, Map<Long, Set<String>> locSkuAttrIds, Map<String, Long> skuAttrIdsQty, String logId) {
        boolean isTipSkuDetail = TipSkuDetailProvider.isTipSkuDetail(locSkuAttrIds, tipSkuAttrId);  //判断商品是否绑定多个库位
        isTipSkuDetail = true;
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
    
    /***
     * 整箱上架使用推荐库位上架
     * @param locationCode
     * @param isCaselevelScanSku
     * @param isNotcaselevelScanSku
     * @param containerCode
     * @param userId
     * @param ouId
     * @param srCmd
     * @return
     */
    @Override
    public ScanResultCommand contianerUserSuggestLocation(String locationCode, Long functionId, String outerContainerCode, String insideContainerCode,Long userId, Long ouId,Integer putawayPatternDetailType,Warehouse warehouse) {
        // TODO Auto-generated method stub
        log.info("PdaSysSuggestPutwayManagerImpl contianerUserSuggestLocation is end");
        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
        if (null == insideContainerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }

        Long insideContainerId = insideContainerCmd.getId();
        ContainerCommand outContainerCmd  = null;
        if(StringUtils.isEmpty(outerContainerCode)) {
            outContainerCmd = insideContainerCmd;
        }else{
            outContainerCmd  = containerDao.getContainerByCode(outerContainerCode, ouId);
        }
        if (null == outContainerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 获取容器状态
        Integer containerStatus = insideContainerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {outerContainerCode});
        }
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        //获取库存缓存信息
        ScanResultCommand srCmd = new ScanResultCommand();  //默认不需要扫描容器号
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
                srCmd.setNeedScanSku(true);// 直接扫描商品
                // 提示下一个容器
        }else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) { //caselevel
               //判断该货箱是否是caselevel货箱
               int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,true);
               if(count < 1) {  //此货箱不是caselevel货箱,直接上架
                   srCmd.setPutaway(true);
                   whSkuInventoryManager.putaway(null,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                   srCmd.setNeedScanSku(false);// 不扫描商品，直接上架
                   this.putawayRemoveAllCache(outContainerCmd, insideContainerCmd, ouId, userId, srCmd);   //更新容器状态
               }else{
                   // 是caselevel货箱扫描商品
                   srCmd.setNeedScanSku(true);// 直接扫描商品
               }
        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {  //判断扫描的货箱是否是非caselevel货箱
               //判断该货箱是否是caselevel货箱
               int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,false);
               if(count < 1) {  //此货箱不是caselevel货箱,直接上架
                   srCmd.setPutaway(true);
                   whSkuInventoryManager.putaway(null,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                   srCmd.setNeedScanSku(false);// 不扫描商品，直接上架
                   this.putawayRemoveAllCache(outContainerCmd, insideContainerCmd, ouId, userId, srCmd);   //更新容器状态
               }else{
                   srCmd.setNeedScanSku(true);// 直接扫描商品
               }
         }else if(false == isCaselevelScanSku && false == isNotcaselevelScanSku) {  //整箱上架,直接执行上架流程
               srCmd.setPutaway(true);
               whSkuInventoryManager.putaway(outContainerCmd,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
               srCmd.setNeedScanSku(false);// 不扫描商品，直接上架
               this.putawayRemoveAllCache(outContainerCmd, insideContainerCmd, ouId, userId, srCmd);   //更新容器状态
        }
        log.info("PdaSysSuggestPutwayManagerImpl contianerUserSuggestLocation is end");
        return srCmd;
    }
    
    

    /**提示下一个容器号
     * @param containerCode
     * @param funcId
     * @param putawayPatternDetailType
     * @param ouId
     * @param userId
     * @param logId
     * @return
     */
   private String sysSuggestTipContainer(String containerCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId)  {
        String tipContainerCode = "";
        if (log.isInfoEnabled()) {
            log.info("sys guide putaway tip container start, containerCode is:[{}], putawayPatternDetailType is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", containerCode, putawayPatternDetailType, ouId, userId, logId);
        }
        // 0.判断容器状态
        if (StringUtils.isEmpty(containerCode)) {
            log.error("containerCode is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
        }
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 获取容器状态
        Integer containerStatus = containerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
        }
        // 1.获取容器统计信息
        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
        if (null != containerCmd) {
            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerCmd.getId().toString());
            if (null == csrCmd) {
                if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                    csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
                    csrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
                }
            }
        }
        Set<Long> insideContainerIds = csrCmd.getInsideContainerIds();
        Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
        Long tipContainerId = null;
        if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
            tipContainerId = pdaPutawayCacheManager.sysGuideContainerPutawayTipContainer(containerCmd, insideContainerIds, logId);
        } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            tipContainerId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipContainer(containerCmd, insideContainerIds, logId);
        }
        tipContainerCode = insideContainerIdsCode.get(tipContainerId);
        if (StringUtils.isEmpty(tipContainerCode)) {
            log.error("sys guide putaway tip container is error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.TIP_NEXT_CONTAINER_IS_ERROR, new Object[] {containerCode});
        }
        if (log.isInfoEnabled()) {
            log.info("sys guide putaway tip container end, containerCode is:[{}], putawayPatternDetailType is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], tipContainer is:[{}]", containerCode, putawayPatternDetailType, ouId, userId, logId,
                    tipContainerCode);
        }
        return tipContainerCode;
    }
    
    /***
     * 整托上架使用推荐库位上架(缓存内部容器)
     * @param locationCode
     * @param isCaselevelScanSku
     * @param isNotcaselevelScanSku
     * @param containerCode
     * @param userId
     * @param ouId
     * @param srCmd
     * @return
     */
    @Override
    public ScanResultCommand  palletIsUserSuggestLocation(String locationCode,String outerContainerCode,Long userId,Long ouId,Integer putawayPatternDetailType,Long functionId,Warehouse warehouse) {
        // TODO Auto-generated method stub
        log.info("PdaSysSuggestPutwayManagerImpl wholeIsUserSuggestLocation is start"); 
        ScanResultCommand srCmd = new ScanResultCommand();  //默认不需要扫描容器号
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
        if (StringUtils.isEmpty(outerContainerCode)) {
            log.error("containerCode is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
        }
        ContainerCommand containerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        Integer containerStatus = containerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCmd.getCode()});
        }
        Long outContainerId = containerCmd.getId();
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        //获取库存缓存信息
        InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, outContainerId.toString());
        ContainerStatisticResultCommand csrCmd =  cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, outContainerId.toString());
        Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
        Set<Long>  insideContainerIds = isrCmd.getInsideContainerIds();  //所有内部容器id集合
        Set<Long> caselevelContainerIds = isrCmd.getCaselevelContainerIds(); //caselevel货箱id集合
        Set<Long> noCaselevelContainerIds = isrCmd.getNotcaselevelContainerIds();  //非caselevel货箱Id集合
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
                 // 提示一个内部容器id
                Long tipInsideContainerId =  pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(containerCmd, insideContainerIds,logId);
                srCmd.setNeedTipContainer(true);  //需要提示容器
                // 提示下一个容器
                srCmd.setTipContainerCode(insideContainerIdsCode.get(tipInsideContainerId));
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
                
                if(caselevelContainerIds.size() > 0) {
                    // 只扫caselevel货箱中的商品
                    Long tipInsideContainerId =  pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(containerCmd, caselevelContainerIds,logId);
                    srCmd.setNeedTipContainer(true);  //需要提示容器
                    // 提示下一个容器
                    srCmd.setTipContainerCode(insideContainerIdsCode.get(tipInsideContainerId));
                }else{
                    whSkuInventoryManager.putaway(containerCmd, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                    srCmd.setPutaway(true);  //上架成功，返回到首页
                    this.putawayRemoveAllCache(containerCmd, null, ouId, userId, srCmd);
                }
                
        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
              if(noCaselevelContainerIds.size() > 0) {
               // 只扫非caselvel货箱
                  Long tipInsideContainerId =  pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(containerCmd, noCaselevelContainerIds,logId);
                  srCmd.setNeedTipContainer(true);  //需要提示容器
                  // 提示下一个容器
                  srCmd.setTipContainerCode(insideContainerIdsCode.get(tipInsideContainerId));
                  srCmd.setNotCaselevelScanContainer(true);
              }else{
                  whSkuInventoryManager.putaway(containerCmd, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                  srCmd.setPutaway(true);  //上架成功，返回到首页
                  this.putawayRemoveAllCache(containerCmd, null, ouId, userId, srCmd);
              }
            } else if(false == isCaselevelScanSku && false == isNotcaselevelScanSku) {
                    whSkuInventoryManager.putaway(containerCmd, null,locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId,logId);
                    srCmd.setPutaway(true);  //上架成功，返回到首页
                    this.putawayRemoveAllCache(containerCmd, null, ouId, userId, srCmd);
            }else{
                log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
            }
        return srCmd;
        
    }
    

    /**
     * 整托上架:扫描sku商品
     * @param barCode
     * @param insideContainerCode
     * @param sRCommand
     * @return
     */
    public ScanResultCommand palletPutwayScanSku(WhSkuCommand skuCmd,String containerCode,Long ouId,Long userId,String locationCode,String insideContainerCode,Long functionId,Warehouse warehouse,Integer putawayPatternDetailType) {
            log.info("PdaSysSuggestPutwayManagerImpl sysSuggestScanSku is start"); 
            ScanResultCommand srCmd = new ScanResultCommand();
            srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);  //整托上架
            ContainerCommand insideCommand = containerDao.getContainerByCode(insideContainerCode, ouId);  //根据外部容器编码查询外部容器
            if(null == insideCommand) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //外部容器不存在
            }
            ContainerCommand outCommand = containerDao.getContainerByCode(containerCode, ouId);  //根据外部容器编码查询外部容器
            if(null == outCommand) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //外部容器不存在
            }
            String barCode = skuCmd.getBarCode();
            Double scanQty = skuCmd.getScanSkuQty();
            if (null == scanQty || scanQty.longValue() < 1) {
                log.error("scan sku qty is valid, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
            }
            if (StringUtils.isEmpty(barCode)) {
                log.error("sku is null error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            WhSkuCommand whSkuCommand =  whSkuDao.findWhSkuByBarcodeExt(barCode, ouId);    //根据商品条码，查询商品信息
            if(null == whSkuCommand){
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);    //查看扫描的商品在商品表中是否存在
            }else {
                BeanUtils.copyProperties(whSkuCommand, skuCmd);
                whSkuCommand.setScanSkuQty(scanQty);
            }
            // 0.判断是否已经缓存所有库存信息
            List<WhSkuInventoryCommand> invList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, insideCommand.getId().toString());
            if (null == invList || 0 == invList.size()) {
                srCmd.setCacheExists(false);// 缓存信息不存在
                invList = pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryAndStatistic(outCommand, ouId, logId);
            }
            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideCommand.getId().toString());
            if (null == isCmd) {
                isCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(outCommand, ouId, logId);
            }
            // 1.获取功能配置
            WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
            if (null == putawyaFunc) {
                log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
            }
            Long skuId = null;
            // 2.复核扫描的商品并判断是否切换内部容器
            Set<Long> insideContainerIds = isCmd.getInsideContainerIds();   //所有内部容器id集合
            Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();    //所有内部容器对应的sku种类
            Set<Long> caselevelContainerIds = isCmd.getCaselevelContainerIds();    //所有caselevel容器集合
            Set<Long> notcaselevelContainerIds = isCmd.getNotcaselevelContainerIds();   //所有非caselevel容器集合
            Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = isCmd.getInsideContainerSkuIdsQty();   //内部容器单个sku总件数
            Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
            // 商品校验
            String skuBarcode = skuCmd.getBarCode();
            Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId);   //获取对应的商品数量,key值是sku id
            Set<Long> icSkuIds = insideContainerSkuIds.get(insideCommand.getId());   //当前容器内所有sku id集合
            Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(insideCommand.getId());
            boolean isSkuExists = false;
            Integer cacheSkuQty = 1;
            Integer icSkuQty = 1;
            for(Long cacheId : cacheSkuIdsQty.keySet()){
                if(icSkuIds.contains(cacheId)){
                    isSkuExists = true;
                }
                if(true == isSkuExists){
                    skuId = cacheId;
                    cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                    icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
                    break;
                }
            }
            if(false == isSkuExists){
                log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideCommand.getId(), insideCommand.getId(), skuId, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideCommand.getCode()});
            }
            if(cacheSkuQty > 1 && cacheSkuQty <= icSkuQty){
                if(0 != (icSkuQty%cacheSkuQty)){
                    // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
                    log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
                    throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
                }
            }
            if(WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern){
                if(0 != new Double("1").compareTo(scanQty)){
                    log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarcode, logId);
                    throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
                }
            }
            skuCmd.setId(skuId);
            skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
            Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
            Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
            if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
                // 全部货箱扫描                                                                       
                CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
                if (cssrCmd.isNeedScanSku()) {
                    srCmd.setNeedScanSku(true);// 直接复核商品
                } else if (cssrCmd.isNeedTipContainer()) {
                    srCmd.setNeedTipContainer(true);
                    Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
                    if (null == tipContainer) {
                        log.error("container is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                    }
                    srCmd.setTipContainerCode(tipContainer.getCode());
                } else {
                    srCmd.setPutaway(true);
                    whSkuInventoryManager.putaway(outCommand,null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                    this.putawayRemoveAllCache(outCommand, null, ouId, userId, srCmd);
                }
            } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
                // 只扫caselevel货箱
                if (null == caselevelContainerIds || 0 == caselevelContainerIds.size()) {
                    // 无caselevel货箱，直接上架
                    srCmd.setPutaway(true);
                    whSkuInventoryManager.putaway(outCommand,null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                    this.putawayRemoveAllCache(outCommand, null, ouId, userId, srCmd);
                } else {
                    CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, caselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
                    if (cssrCmd.isNeedScanSku()) {
                        srCmd.setNeedScanSku(true);// 直接复核商品
                    } else if (cssrCmd.isNeedTipContainer()) {
                        srCmd.setNeedTipContainer(true);
                        Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
                        if (null == tipContainer) {
                            log.error("container is null error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                        }
                        srCmd.setTipContainerCode(tipContainer.getCode());
                    } else {
                        srCmd.setPutaway(true);
                        whSkuInventoryManager.putaway(outCommand,null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                        this.putawayRemoveAllCache(outCommand, null, ouId, userId, srCmd);
                    }
                }

            } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
                // 只扫非caselvel货箱
                if (null == notcaselevelContainerIds || 0 == notcaselevelContainerIds.size()) {
                    // 无caselevel货箱，直接上架
                    srCmd.setPutaway(true);
                    whSkuInventoryManager.putaway(outCommand, null,locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                    this.putawayRemoveAllCache( outCommand, null, ouId, userId, srCmd);
                } else {
                    CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, notcaselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
                    if (cssrCmd.isNeedScanSku()) {
                        srCmd.setNeedScanSku(true);// 直接复核商品
                    } else if (cssrCmd.isNeedTipContainer()) {
                        srCmd.setNeedTipContainer(true);
                        Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
                        if (null == tipContainer) {
                            log.error("container is null error, logId is:[{}]", logId);
                            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                        }
                        srCmd.setTipContainerCode(tipContainer.getCode());
                    } else {
                        srCmd.setPutaway(true);
                        whSkuInventoryManager.putaway(outCommand,null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                        this.putawayRemoveAllCache(outCommand, null, ouId, userId, srCmd);
                    }
                }
            } else {
                log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_FUNCTION_IS_CHANGE_ERROR);
            }
            log.info("PdaSysSuggestPutwayManagerImpl sysSuggestScanSku is end"); 
            return srCmd;
    }

    /**
     * 整箱上架:扫描sku商品
     * @param barCode
     * @param insideContainerCode
     * @param sRCommand
     * @return
     */
    public ScanResultCommand containerPutwayScanSku(WhSkuCommand skuCmd,String containerCode,Long ouId,Long userId,String locationCode,String insideContainerCode,Long functionId,Warehouse warehouse,Integer putawayPatternDetailType){
        log.info("PdaSysSuggestPutwayManagerImpl containerPutwayScanSku is start"); 
        ScanResultCommand  srCmd = new ScanResultCommand();
        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY); //整箱上架
        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
        if(null == insideContainerCmd) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //容器不存在
        }
        //获取内部容器id
        Long insideContainerId = insideContainerCmd.getId();
        ContainerCommand outCommand = null;
        if(!StringUtils.isEmpty(containerCode)) {
            outCommand = containerDao.getContainerByCode(containerCode, ouId);  //根据容器编码查询外部容器
        }
        //整箱上架：判断内部容器是否可以上架
        Integer containerStatus = insideContainerCmd.getStatus();
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {insideContainerCode});
        }
        String barCode = skuCmd.getBarCode();
        Double scanQty = skuCmd.getScanSkuQty();
        if (null == scanQty || scanQty.longValue() < 1) {
            log.error("scan sku qty is valid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
        }
        if (StringUtils.isEmpty(barCode)) {
            log.error("sku is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        WhSkuCommand whSkuCommand =  whSkuDao.findWhSkuByBarcodeExt(barCode, ouId);
        if(null == whSkuCommand){
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);    //查看扫描的商品在商品表中是否存在
        }
        InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerCmd.getId().toString());
        if (null == isrCmd) {
            isrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryStatistic(insideContainerCmd, ouId, logId);
        }
        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
        if (null != outCommand) {
            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, outCommand.getId().toString());
            if (null == csrCmd) {
                csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(outCommand, ouId, logId);
            }
        }
        Set<Long> insideContainerIds = isrCmd.getInsideContainerIds();   //所有容器id集合
        Set<Long> caselevelContainerIds = isrCmd.getCaselevelContainerIds();
        Set<Long> noCaselevelContainerIds = isrCmd.getNotcaselevelContainerIds();
        Map<Long, Set<Long>> insideContainerSkuIds = isrCmd.getInsideContainerSkuIds();
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = isrCmd.getInsideContainerSkuIdsQty();
        if (null != outCommand) {
            insideContainerIds = csrCmd.getInsideContainerIds();
            caselevelContainerIds = csrCmd.getCaselevelContainerIds();
            noCaselevelContainerIds = csrCmd.getNotcaselevelContainerIds();
        }
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
        // 商品校验
        Long skuId = null;
        String skuBarcode = skuCmd.getBarCode();
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId);   //获取对应的商品数量,key值是sku id
        Set<Long> icSkuIds = insideContainerSkuIds.get(insideContainerCmd.getId());   //当前容器内所有sku id集合
        Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(insideContainerCmd.getId());
        boolean isSkuExists = false;
        Integer cacheSkuQty = 1;
        Integer icSkuQty = 1;
        for(Long cacheId : cacheSkuIdsQty.keySet()){
            if(icSkuIds.contains(cacheId)){
                isSkuExists = true;
            }
            if(true == isSkuExists){
                skuId = cacheId;
                cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
                break;
            }
        }
        if(false == isSkuExists){
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerCmd.getId(), insideContainerCmd.getId(), skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideContainerCmd.getCode()});
        }
        if(cacheSkuQty > 1 && cacheSkuQty <= icSkuQty){
            if(0 != (icSkuQty%cacheSkuQty)){
                // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
                log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
                throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
            }
        }
        if(WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern){
            if(0 != new Double("1").compareTo(scanQty)){
                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarcode, logId);
                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
            }
        }
        skuCmd.setId(skuId);
        skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 是caselevel货箱扫描商品
            CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern,logId);
            if (cssrCmd.isNeedScanSku()) {
                srCmd.setNeedScanSku(true);// 直接扫描商品
            }else if (cssrCmd.isNeedTipContainer()) { //一个容器扫描完,整箱上架，提示下一个容器
                srCmd.setPutaway(true);
                whSkuInventoryManager.putaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId,  logId);
                srCmd.setAfterPutawayTipContianer(true);
                this.putawayRemoveAllCache( outCommand, insideContainerCmd, ouId, userId, srCmd);
                // 提示下一个容器
                String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                srCmd.setTipContainerCode(tipContainerCode);
            }else {
                srCmd.setPutaway(true);
                whSkuInventoryManager.putaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                this.putawayRemoveAllCache(outCommand, insideContainerCmd, ouId, userId, srCmd);
            }
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,true);
            if(count < 1) {  //此货箱不是caselevel货箱,直接上架
                whSkuInventoryManager.putaway(null,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                this.putawayRemoveAllCache(outCommand, insideContainerCmd, ouId, userId, srCmd);
            }
            // 是caselevel货箱扫描商品
            CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, caselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty,skuCmd, scanPattern,logId);
            if (cssrCmd.isNeedScanSku()) {
                srCmd.setNeedScanSku(true);// 直接扫描商品
            } else if (cssrCmd.isNeedTipContainer()) { //一个容器扫描完,整箱上架，提示下一个容器
                srCmd.setPutaway(true);
                whSkuInventoryManager.putaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId,logId);
                srCmd.setAfterPutawayTipContianer(true);
                this.putawayRemoveAllCache( outCommand, insideContainerCmd, ouId, userId, srCmd);
                // 提示下一个容器
                String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                srCmd.setTipContainerCode(tipContainerCode);
            } else {
                srCmd.setPutaway(true);
                whSkuInventoryManager.putaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId,logId);
                this.putawayRemoveAllCache( outCommand, insideContainerCmd, ouId, userId, srCmd);
            }
        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,false);
            if(count < 1) {  //此货箱不是caselevel货箱,直接上架
                whSkuInventoryManager.putaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                this.putawayRemoveAllCache( outCommand, insideContainerCmd, ouId, userId, srCmd);
            }
            // 是caselevel货箱扫描商品
            CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, noCaselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty,skuCmd, scanPattern,logId);
            if (cssrCmd.isNeedScanSku()) {
                srCmd.setNeedScanSku(true);// 直接扫描商品
            } else if (cssrCmd.isNeedTipContainer()) { //一个容器扫描完,整箱上架，提示下一个容器
                srCmd.setPutaway(true);
                whSkuInventoryManager.putaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                srCmd.setAfterPutawayTipContianer(true);
                this.putawayRemoveAllCache( outCommand, insideContainerCmd, ouId, userId, srCmd);
                // 提示下一个容器
                String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
                srCmd.setTipContainerCode(tipContainerCode);
            } else {
                srCmd.setPutaway(true);
                whSkuInventoryManager.putaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                this.putawayRemoveAllCache( outCommand, insideContainerCmd, ouId, userId, srCmd);
            }
        }else if (false == isCaselevelScanSku && false == isNotcaselevelScanSku){
                srCmd.setPutaway(true);
                whSkuInventoryManager.putaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                this.putawayRemoveAllCache( outCommand, insideContainerCmd, ouId, userId, srCmd);
        } else {
            log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_IS_CHANGE_ERROR);
        }
        log.info("PdaSysSuggestPutwayManagerImpl containerPutwayScanSku is end"); 
        return srCmd;
    }
    
    /**
     * 上架完毕：整箱或整托清楚缓存
     * @param outCmd
     * @param insideCmd
     * @param ouId
     * @param userId
     * @param sCommand
     */
    private void putawayRemoveAllCache(ContainerCommand outCmd,ContainerCommand insideCmd,Long ouId,Long userId,ScanResultCommand sCommand) {
        log.info("PdaSysSuggestPutwayManagerImpl updateContainerStatus is start"); 
        Long outId = null;
        if(null != outCmd) {
            outId = outCmd.getId();   //外部容器id
        }
        Integer putawayPatternDetailType = sCommand.getPutawayPatternDetailType();
        if(WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {  //整箱上架
            Long insideId = insideCmd.getId();   //内部容器id
            if(null == outId && null != insideId) {  //整箱上架，只有容器，没有托盘
                    //整箱上架清除缓存
                    pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(null, insideCmd,true,logId);
            }else{//整箱上架需要将当前内部容器的状态更新为status=1 && lifecyle=1（状态可用&&生命周期也为可用），如果有外部容器且所有内部容器均已切换扫描完毕则需要将外部容器的状态也更新为status=1 && lifecyle=1
                if(sCommand.isAfterPutawayTipContianer()) {   //托盘内还有内有上架的货箱,只更新货箱转态,不更新托盘状态
                    pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCmd, insideCmd,false,logId);
                }else{  //托盘中的货箱全部上架，更新托盘的状态
                    //整箱上架清除缓存
                    pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCmd, insideCmd,true,logId);
                }
            }
        }
        if(WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType)  { //整托上架
            //整托上架清除缓存
            pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(outCmd, logId);
        }
        log.info("PdaSysSuggestPutwayManagerImpl updateContainerStatus is end");
    }
    
    /***
     * 拆箱箱上架:扫描sku商品
     * @param barCode
     * @param containerCode
     * @param ouId
     * @param insideContainerId
     * @param userId
     * @param locationCode
     * @param scanPattern
     * @param countSku
     * @param skuQuantity
     * @param finish
     * @return
     */
      public ScanResultCommand splitPutwayScanSku(String outerContainerCode,String insideContainerCode,WhSkuCommand skuCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId,Integer putawayPatternDetailType,Warehouse warehouse){
          log.info("PdaSysSuggestPutwayManagerImpl splitPutwayScanSku is start");
          ScanResultCommand srCmd = new ScanResultCommand();
          srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
          ContainerCommand ocCmd = null;
          ContainerCommand icCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
          if (null == icCmd) {
              log.error("sys guide splitContainer putaway check san sku, inside container is null error, logId is:[{}]", logId);
              throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
          }
          if(!StringUtils.isEmpty(outerContainerCode)) {
              ocCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
          }
          // 0.判断是否已经缓存所有库存信息
          List<WhSkuInventoryCommand> invList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, icCmd.getId().toString());
          if (null == invList || 0 == invList.size()) {
              srCmd.setCacheExists(false);// 缓存信息不存在
              invList = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventoryAndStatistic(icCmd, ouId, logId);
          }
          InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icCmd.getId().toString());
          if (null == isCmd) {
              isCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventoryStatistic(icCmd, ouId, logId);
          }
          ContainerStatisticResultCommand csrCmd = null;
          if(null != ocCmd) {
              csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, ocCmd.getId().toString());
              if (null == csrCmd) {
                      csrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInsideContainerStatistic(ocCmd, ouId, logId);
              }
          }
          // 1.判断当前商品是否扫完、是否提示下一个库位、容器或上架
          Set<Long> insideContainerIds = isCmd.getInsideContainerIds();
          Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = isCmd.getInsideContainerSkuAttrIdsQty();   //内部容器唯一sku总件数
          Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = isCmd.getInsideContainerSkuAttrIdsSnDefect();  //内部容器唯一sku对应所有残次条码
          Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = isCmd.getInsideContainerLocSkuAttrIds();  // 内部容器推荐库位对应唯一sku及残次条码
          Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(icCmd.getId());   //获取当前内部容器度一应sku和残次条码
          Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(icCmd.getId());
          Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
          Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = isCmd.getInsideContainerSkuIdsQty();
          Location loc = locationDao.findLocationByCode(locationCode, ouId);
          if (null == loc) {
              log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
              throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
          }
          Long locationId = loc.getId();
          String barCode = skuCmd.getBarCode();
          WhSkuCommand sku = whSkuDao.findWhSkuByBarcodeExt(barCode, ouId);
          if (null == sku) {
              log.error("sku is not found error, logId is:[{}]", logId);
              throw new BusinessException(ErrorCodes.LOCATION_SKU_IS_NOT_EXISTS);
          }
          Long sId = null;
          Double scanQty = skuCmd.getScanSkuQty();
          Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(barCode, logId);
          Set<Long> icSkuIds = insideContainerSkuIds.get(icCmd.getId());
          Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(icCmd.getId());
          boolean isSkuExists = false;
          Integer cacheSkuQty = 1;
          Integer icSkuQty = 1;
          for(Long cacheId : cacheSkuIdsQty.keySet()){
              if(icSkuIds.contains(cacheId)){
                  isSkuExists = true;
              }
              if(true == isSkuExists){
                  sId = cacheId;
                  cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                  icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
                  break;
              }
          }
          if(false == isSkuExists){
              log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocCmd.getId(), icCmd.getId(), sId, logId);
              throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
          }
          if(cacheSkuQty > 1 && cacheSkuQty <= icSkuQty){
              if(0 != (icSkuQty%cacheSkuQty)){
                  // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
                  log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
                  throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
              }
          }
          skuCmd.setId(sId);
          skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
          SkuRedisCommand cacheSkuCmd = skuRedisManager.findSkuMasterBySkuId(sId, ouId, logId);
          if (null == cacheSkuCmd) {
              log.error("sku is not found error, logId is:[{}]", logId);
              throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
          }
          sku.setScanSkuQty(skuCmd.getScanSkuQty());
          sku.setIsNeedTipSkuDetail(null == skuCmd.getIsNeedTipSkuDetail() ? false : skuCmd.getIsNeedTipSkuDetail());
          sku.setIsNeedTipSkuSn(null == skuCmd.getIsNeedTipSkuSn() ? false : skuCmd.getIsNeedTipSkuSn());
          sku.setIsNeedTipSkuDefect(null == skuCmd.getIsNeedTipSkuDefect() ? false : skuCmd.getIsNeedTipSkuDefect());
          sku.setInvType(StringUtils.isEmpty(skuCmd.getInvType()) ? "" : skuCmd.getInvType());
          sku.setInvStatus(StringUtils.isEmpty(skuCmd.getInvStatus()) ? "" : skuCmd.getInvStatus());
          sku.setInvMfgDate(StringUtils.isEmpty(skuCmd.getInvMfgDate()) ? "" : skuCmd.getInvMfgDate());
          sku.setInvExpDate(StringUtils.isEmpty(skuCmd.getInvExpDate()) ? "" : skuCmd.getInvExpDate());
          sku.setInvAttr1(StringUtils.isEmpty(skuCmd.getInvAttr1()) ? "" : skuCmd.getInvAttr1());
          sku.setInvAttr2(StringUtils.isEmpty(skuCmd.getInvAttr2()) ? "" : skuCmd.getInvAttr2());
          sku.setInvAttr3(StringUtils.isEmpty(skuCmd.getInvAttr3()) ? "" : skuCmd.getInvAttr3());
          sku.setInvAttr4(StringUtils.isEmpty(skuCmd.getInvAttr4()) ? "" : skuCmd.getInvAttr4());
          sku.setInvAttr5(StringUtils.isEmpty(skuCmd.getInvAttr5()) ? "" : skuCmd.getInvAttr5());
          sku.setSkuSn(StringUtils.isEmpty(skuCmd.getSkuSn()) ? "" : skuCmd.getSkuSn());
          sku.setSkuDefect(StringUtils.isEmpty(skuCmd.getSkuDefect()) ? "" : skuCmd.getSkuDefect());
          CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipSkuOrLocOrContainer(ocCmd, icCmd, insideContainerIds, insideContainerSkuAttrIdsQty, insideContainerSkuAttrIdsSnDefect, insideContainerLocSkuAttrIds, loc.getId(), sku, logId);
          if (cssrCmd.isNeedTipSkuSn()) {
              // 当前商品还未扫描，继续扫sn残次信息
              srCmd.setNeedScanSkuSn(true);// 继续扫sn
              srCmd.setNeedTipSkuDefect(true);   //sku残次信息
              String tipSkuAttrId = cssrCmd.getTipSkuAttrId();
              tipSkuDetailAspect(srCmd, tipSkuAttrId, locSkuAttrIds, skuAttrIdsQty, logId);
          } else if (cssrCmd.isNeedTipSku()) {
              //提示下一个商品
              srCmd.setNeedTipSku(true);// 提示下一个sku
              String tipSkuAttrId = cssrCmd.getTipSkuAttrId();
              Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
              WhSkuCommand tipSkuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
              if (null == tipSkuCmd) {
                  log.error("sku is not found error, logId is:[{}]", logId);
                  throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
              }
              tipSkuDetailAspect(srCmd, tipSkuAttrId, locSkuAttrIds, skuAttrIdsQty, logId);
              srCmd.setTipSkuBarcode(skuCmd.getBarCode());
          } else if (cssrCmd.isNeedTipLoc()) {
              // 当前库位对应的商品已扫描完毕，可上架，并提示下一个库位
              srCmd.setPutaway(true);
              srCmd.setAfterPutawayTipLoc(true);
              Long tipLocId = cssrCmd.getTipLocId();
              Location tipLoc = locationDao.findByIdExt(tipLocId, ouId);
              if (null == tipLoc) {
                  log.error("location is null error, locId is:[{}], logId is:[{}]", tipLocId, logId);
                  throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
              }
              srCmd.setTipLocationCode(tipLoc.getCode());
          } else if (cssrCmd.isNeedTipContainer()) {
              // 当前容器已扫描完毕，可上架，并提示下一个容器
              srCmd.setPutaway(true);
              srCmd.setAfterPutawayTipContianer(true);
              Long tipContainerId = cssrCmd.getTipContainerId();
              Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
              if (null == tipContainer) {
                  log.error("tip container is null error, containerId is:[{}], logId is:[{}]", tipContainerId, logId);
                  throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
              }
              srCmd.setTipContainerCode(tipContainer.getCode());
          } else {
              // 执行上架
              srCmd.setPutaway(true);
              whSkuInventoryManager.putaway(ocCmd,icCmd, locationCode, funcId, warehouse, putawayPatternDetailType, ouId, userId, logId);
              //拆箱清楚缓存
              pdaPutawayCacheManager.sysGuideSplitContainerPutawayRemoveAllCache(ocCmd, icCmd, false, true, locationId, logId);
          }
          return srCmd;
      }


}
