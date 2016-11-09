package com.baozun.scm.primservice.whoperation.manager.pda.putaway;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationInvVolumeWeightCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ManMadeContainerStatisticCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.putaway.PdaManMadePutawayCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuLocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuExtattrDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionPutAwayDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaManmadePutawayCacheManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationInvVolumeWieghtManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionPutAwayManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.sku.SkuExtattr;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPutAway;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.util.StringUtil;



/**
 * PDA人为指定上架manager
 * 
 * @author 
 * 
 */

@Service("pdaManMadePutawayManager")
@Transactional
public class PdaManMadePutawayManagerImpl extends BaseManagerImpl implements PdaManMadePutawayManager {

    protected static final Logger log = LoggerFactory.getLogger(PdaManMadePutawayManagerImpl.class);

    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhFunctionPutAwayDao whFunctionPutAwayDao;
    @Autowired
    private WhLocationDao whLocationDao;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private PdaManmadePutawayCacheManager pdaManmadePutawayCacheManager;
    @Autowired
    private WhCartonDao whCartonDao;
    @Autowired
    private WhSkuDao whSkuDao;
    @Autowired
    private UomDao uomDao;
    @Autowired
    private WhLocationInvVolumeWieghtManager whLocationInvVolumeWieghtManager;
    @Autowired(required = false)
    private WhFunctionManager whSkuLocationManager;
    @Autowired
    private WhFunctionPutAwayManager whFunctionPutAwayManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhSkuLocationDao whSkuLocationDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private SkuExtattrDao skuExtattrDao;
    @Autowired
    private SkuMgmtDao skuMgmtDao;
    @Autowired
    private WhSkuInventoryTobefilledDao whSkuInventoryTobefilledDao;
    @Autowired
    private WhSkuInventoryLogManager whSkuInventoryLogManager;



    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaManMadePutawayCommand pdaScanContainer(PdaManMadePutawayCommand pdaManMadePutawayCommand) {
        log.info("PdaManMadePutawayManagerImpl pdaScanContainer is start");
        Long functionId = pdaManMadePutawayCommand.getFunctionId();
        Long ouId = pdaManMadePutawayCommand.getOuId();
        Long userId = pdaManMadePutawayCommand.getUserId();
        Long containerId = null;
        // 验证容器号为空
        if (StringUtil.isEmpty(pdaManMadePutawayCommand.getContainerCode())) {
            log.error("pdaScanContainer pdaInboundSortationCommand.getContainerCode() is null logid: " + logId);
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        ContainerCommand container = containerDao.getContainerByCode(pdaManMadePutawayCommand.getContainerCode(), ouId);
        Boolean isScanOuterContainer = pdaManMadePutawayCommand.getIsScanOuterContainer() == null ? false: pdaManMadePutawayCommand.getIsScanOuterContainer();
        if (isScanOuterContainer) {   //已经扫外部容器
            // 扫描容器
            this.manMandeScanContainer(ouId, container, logId,userId);
            containerId = container.getId();
            pdaManMadePutawayCommand.setInsideContainerCode(pdaManMadePutawayCommand.getContainerCode());  //当前扫的是内部容器
            // 若为内部容器库存，则进入内部容器的相应判断
            WhFunctionPutAway whFunctionPutAway = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId);
            pdaManMadePutawayCommand.setScanPattern(whFunctionPutAway.getScanPattern());  //扫描方式
            //缓存扫描的货箱
            String outerContainerCode = pdaManMadePutawayCommand.getOuterContainerCode();
            ContainerCommand outCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
            Long outerId = outCmd.getId();
            pdaManmadePutawayCacheManager.containerPutawayCacheInsideContainer(container, outerId , logId, outerContainerCode);
            if (whFunctionPutAway.getIsEntireBinPutaway()) { // 整箱上架
                pdaManMadePutawayCommand.setIsTipInsideContainer(false);  //设置不提示货箱号
                pdaManMadePutawayCommand.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                // 判断内部容器是否存在多个sku商品
                List<WhSkuInventory> list =  whSkuInventoryDao.findContainerInventoryCountsByInsideContainerId(containerId,ouId);
                Boolean result = this.isSkuRepat(list);
                if (!result) {
                    return pdaManMadePutawayCommand;
                }
                if (result) {
                    // 如果存在多个sku，则判断所有商品是否允许混放
                    Boolean isMix = this.getIsMix(list, ouId);
                    if (!isMix) {
                        throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_NOTALLOWED_MIX);
                    }
                }
            } else { // 拆箱上架
                //进入扫sku页面
                pdaManMadePutawayCommand.setIsNeedScanSku(true);
            }
            return pdaManMadePutawayCommand;
        } else {
            // 扫描容器
            this.manMandeScanContainer(ouId, container, logId,userId);
            PdaManMadePutawayCommand manMandeCommand = this.judgeInventory(pdaManMadePutawayCommand, container);
            containerId = container.getId();
            if (manMandeCommand.getIsOuterContainer()) {
                // 若为外部容器库存
                WhFunctionPutAway whFunctionPutAway = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId);
                pdaManMadePutawayCommand.setScanPattern(whFunctionPutAway.getScanPattern());  //扫描方式
                if (!whFunctionPutAway.getIsEntireTrayPutaway()) { // 不是整托上架,提示内部容器号,扫内部容器
                    manMandeCommand.setOuterContainerCode(container.getCode());   //由外部托盘,不是整托上架时，设置外部容器号
                    pdaManMadePutawayCommand.setIsScanOuterContainer(true);   //已经扫外部容器,整箱上架时
                    return manMandeCommand;
                } else {// 整托上架
                    manMandeCommand.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                    //整托上架先判断有没有缓存，如果有缓存，先删除
                    ManMadeContainerStatisticCommand manCmd = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC, container.getId().toString());
                    if(null != manCmd) {
                        pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(container, logId);
                    }
                    // 判断托盘是否存在多个sku商品
                    List<WhSkuInventory> list = whSkuInventoryDao.findContainerInventoryCountsByOuterContainerId(containerId,ouId);
                    //整托上架修改托盘上的内部容器状态为占用中
                    for(WhSkuInventory skuInv:list){
                        Long insideId = skuInv.getInsideContainerId();
                        Container insideContainer = containerDao.findByIdExt(insideId, ouId);
                        if(null == insideContainer) {
                            // 容器信息不存在
                            log.error("pdaScanContainer container is null logid: " + logId);
                            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                        }
                        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == insideContainer.getStatus()) {
                            Container c = new Container();
                            BeanUtils.copyProperties(insideContainer, c);
                            c.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                            c.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
                            containerDao.saveOrUpdateByVersion(c);
                            insertGlobalLog(GLOBAL_LOG_UPDATE,c, ouId, pdaManMadePutawayCommand.getUserId(), null,null);
                        }
                    }
                    
                    Boolean result = this.isSkuRepat(list);
                    if (!result) {
                        return manMandeCommand;
                    }
                    if (result) {
                        // 如果存在多个sku，则判断所有商品是否允许混放
                        Boolean isMix = this.getIsMix(list, ouId);
                        if (!isMix) {
                            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_NOTALLOWED_MIX);
                        }
                    }
                }
            } else {
                // 若为内部容器库存，则进入内部容器的相应判断
                WhFunctionPutAway whFunctionPutAway = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId);
                pdaManMadePutawayCommand.setScanPattern(whFunctionPutAway.getScanPattern());  //扫描方式
                if (whFunctionPutAway.getIsEntireBinPutaway()) { // 整箱上架
                    manMandeCommand.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                    // 判断托盘是否存在多个sku商品
                    String outerContainerCode = null;
                    List<WhSkuInventory> list = whSkuInventoryDao.findContainerInventoryCountsByInsideContainerId(containerId,ouId);
                    Long cId = list.get(0).getOuterContainerId();
                    if(null != cId) {  //整箱上架有外部容器
                        Container outerContainer = containerDao.findByIdExt(cId, ouId);
                        if (null == outerContainer) {
                                // 容器信息不存在
                                log.error("pdaScanContainer container is null logid: " + logId);
                                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                        }
                       outerContainerCode = outerContainer.getCode();
                       pdaManmadePutawayCacheManager.containerPutawayCacheInsideContainer(container, cId, logId, outerContainerCode);
                    }
                    manMandeCommand.setOuterContainerCode(outerContainerCode);
                    Boolean result = this.isSkuRepat(list);
                    if (!result) {
                        return manMandeCommand;
                    }
                    if (result) {
                        // 如果存在多个sku，则判断所有商品是否允许混放
                        Boolean isMix = this.getIsMix(list, ouId);
                        if (!isMix) {
                            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_NOTALLOWED_MIX);
                        }
                    }
                } else { // 拆箱上架
                     //进入扫sku页面
                    manMandeCommand.setIsNeedScanSku(true);
                }
            }
            // 缓存容器内要上架的sku信息
            pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, manMandeCommand.getIsOuterContainer());
            //缓存扫描的货箱
            log.info("PdaManMadePutawayManagerImpl pdaScanContainer is end");

            return manMandeCommand;
        }

    }
    
    /****
     * 判断托盘/货箱是否存在多个sku商品
     * @param list
     * @return
     */
    private Boolean isSkuRepat(List<WhSkuInventory> list) {
        log.info("PdaManMadePutawayManagerImpl isSkuRepat is start");
        Boolean result = false;  //默认不重复
        if(list.size() > 0) {
            WhSkuInventory  temp = list.get(0);
            for(int i=1;i<list.size();i++) {
                WhSkuInventory skuInv = list.get(i);
                if(!temp.getSkuId().equals(skuInv.getSkuId())) {
                    result = true;
                    break;
                }
            }
        }
        log.info("PdaManMadePutawayManagerImpl isSkuRepat is end");
        return result;
    }

    /**
     * 判断所有容器内商品是否允许混放
     * 
     * @author
     * @param list
     * @return
     */
    private Boolean getIsMix(List<WhSkuInventory> list, Long ouId) {
        Boolean result = true;  //默认不允许混放
        //去除重复的skuId
        List<Long>  tempList = new ArrayList<Long>(); 
        WhSkuInventory  temp = list.get(0);
        for(int i=1;i<list.size();i++) {
            WhSkuInventory skuInv = list.get(i);
            if(!temp.getSkuId().equals(skuInv.getSkuId())) {
                if(!tempList.contains(temp.getSkuId())){  
                    tempList.add(temp.getSkuId());  
                }
                temp = skuInv;
            }
        }
        for (int i = 0; i < list.size(); i++) {
            Long skuId = list.get(i).getSkuId();
            SkuMgmt skuMgmt = skuMgmtDao.findSkuMgmtBySkuIdShared(skuId, ouId);
            if (!skuMgmt.getIsMixAllowed()) {
                result = false;   //  容器内有不允许混放的商品
                break;
            }
        }
        return result;
    }

    /**
     * 扫描容器
     * 
     * @param pdaManMadePutawayCommand
     * @return
     */
    private void manMandeScanContainer(Long ouId, ContainerCommand container, String logId,Long userId) {
        log.info("PdaManMadePutawayManagerImpl manMandeScanContainer is start");
        if (null == container) {
            // 容器信息不存在
            log.error("pdaScanContainer container is null logid: " + logId);
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        // 验证容器Lifecycle是否有效
        if (!container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            // 容器Lifecycle无效
            log.error("pdaScanContainer container lifecycle error =" + container.getLifecycle() + " logid: " + logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        // 验证容器状态是否是待上架
        if (!(container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
                log.error("pdaScanContainer container status error =" + container.getStatus() + " logid: " + logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY, new Object[] {container.getStatus()});
        }
        log.info("PdaManMadePutawayManagerImpl manMandeScanContainer is end");
    }

    private PdaManMadePutawayCommand judgeInventory(PdaManMadePutawayCommand pdaManMadePutawayCommand, ContainerCommand container) {
        Long ouId = pdaManMadePutawayCommand.getOuId();
        Long containerId = container.getId();
        String containerCode = container.getCode();
        Integer putawayPatternDetailType = pdaManMadePutawayCommand.getPutawayPatternDetailType();
        // 判断容器库存是外部容器库存还是内部容器库存
        int outerContainerCount = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId,containerId);
        int insideContainerCount = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId,containerId);
        if (0 < insideContainerCount) {
            // 内部容器库存
            pdaManMadePutawayCommand.setIsOuterContainer(false);
            pdaManMadePutawayCommand.setInsideContainerCode(containerCode);
            String outerContainerCode = null;
            List<WhSkuInventory> whSkuInvList = whSkuInventoryDao.findContainerInventoryCountsByInsideContainerId(containerId,ouId);
            Long cId = whSkuInvList.get(0).getOuterContainerId();
            if(null != cId) {  //整箱上架没有外部容器
                Container outerContainer = containerDao.findByIdExt(cId, ouId);
                if (null == outerContainer) {
                        // 容器信息不存在
                        log.error("pdaScanContainer container is null logid: " + logId);
                        throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
               outerContainerCode = outerContainer.getCode();
               pdaManmadePutawayCacheManager.containerPutawayCacheInsideContainer(container, cId, logId, outerContainerCode);
            }
           
        }
        if (0 < outerContainerCount) {  //外部容器库存
                 // 外部容器库存
                pdaManMadePutawayCommand.setIsOuterContainer(true);
                pdaManMadePutawayCommand.setInsideContainerCode(null);
                pdaManMadePutawayCommand.setOuterContainerCode(containerCode);  //外部容器号
                // 若不是整托上架，跳转到扫描货箱容器页面，页面提示扫描货箱容器号
                if(WhPutawayPatternDetailType.PALLET_PUTAWAY != putawayPatternDetailType) {
                    ManMadeContainerStatisticCommand command = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(pdaManMadePutawayCommand, containerId);
                    Set<Long> insideContainerIds = command.getInsideContainerIds();
                    Long tipContainerId = pdaManmadePutawayCacheManager.containerPutawayTipContainer(container, insideContainerIds, logId);
                    Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
                    if (null == tipContainer) {
                        log.error("container is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                    }
                    String tipContainerCode = tipContainer.getCode();
                    pdaManMadePutawayCommand.setTipContainerCode(tipContainerCode); // 提示货箱号
                    pdaManMadePutawayCommand.setIsTipInsideContainer(true);
               }else{
                   pdaManMadePutawayCommand.setIsTipInsideContainer(false);   //不需要提示货箱号
               }
        }
        if(0 >= insideContainerCount && 0 >= outerContainerCount){
            // 无收货库存
            log.error("sys guide pallet putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
        }
        // 1.先修改外部容器状态为：上架中，且占用中,另外所有的内部容器状态可以在库存信息统计完成以后再修改
        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == container.getStatus()) {
            Container c = new Container();
            BeanUtils.copyProperties(container, c);
            c.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            c.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
            containerDao.saveOrUpdateByVersion(c);
            insertGlobalLog(GLOBAL_LOG_UPDATE,c, ouId, pdaManMadePutawayCommand.getUserId(), null,null);
        }
        // 统计容器信息
        pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(pdaManMadePutawayCommand, containerId);
        return pdaManMadePutawayCommand;
    }



    /**
     * 整托/整箱上架:扫库位
     * 
     * @param pdaManMadePutawayCommand
     * @return
     */

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaManMadePutawayCommand pdaScanLocation(PdaManMadePutawayCommand pdaManMadePutawayCommand, String invAttrMgmtHouse, Warehouse warehouse) {
        // 验证库位号是否为空
        Long ouId = pdaManMadePutawayCommand.getOuId();
        String barCode = pdaManMadePutawayCommand.getBarCode();
        String locationCode = pdaManMadePutawayCommand.getLocationCode();
        Long functionId = pdaManMadePutawayCommand.getFunctionId();
        int putawayPatternDetailType = pdaManMadePutawayCommand.getPutawayPatternDetailType();
        String outerContainerCode = pdaManMadePutawayCommand.getOuterContainerCode();
        String insideContainerCode = pdaManMadePutawayCommand.getInsideContainerCode();
        ContainerCommand insideCommand = null;
        if(!StringUtil.isEmpty(insideContainerCode)) {
            insideCommand = containerDao.getContainerByCode(insideContainerCode, ouId); // 根据内部容器编码查询内部容器
            if (null == insideCommand) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 内容器不存在
            }
        }
        
        ContainerCommand outCommand = null;
        Long containerId = null;
        if(!StringUtil.isEmpty(outerContainerCode)) {
            outCommand = containerDao.getContainerByCode(outerContainerCode, ouId); // 根据外部容器编码查询外部容器
            if(putawayPatternDetailType != 1 && outCommand == null) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 外容器不存在
            }
            containerId = outCommand.getId();
        }else{
            containerId = insideCommand.getId();
        }
        
        // 查询对应库位信息
        Location location = null;
        if(pdaManMadePutawayCommand.getIsInboundLocationBarcode()) {
            location = whLocationDao.getLocationByBarcode(barCode, ouId);
        }else{
            location = whLocationDao.findLocationByCode(locationCode, ouId);
        }
        // 验证库位是否存在
        if (null == location) {
            log.error("pdaScanLocation location is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
        }
        // 验证库位状态是否可用:lifecycle
        if (location.getLifecycle() != 1) {
            log.error("pdaScanLocation lifecycle is error logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_LIFECYCLE_ERROR);
        }
        pdaManMadePutawayCommand.setLocationId(location.getId());
        boolean mixStacking = location.getIsMixStacking(); // 库位是否混放
        Long locationId = location.getId();
        // 验证库位是否静态库位
        if (location.getIsStatic()) {
            // 判断库位是否绑定了容器内所有的SKU商品
            WhSkuLocationCommand whSkuLocComand = new WhSkuLocationCommand();
            whSkuLocComand.setOuId(pdaManMadePutawayCommand.getOuId());
            whSkuLocComand.setLocationId(location.getId());
            List<WhSkuLocationCommand> listCommand = whSkuLocationDao.findSkuLocationToShard(whSkuLocComand);
            // 从缓存中获取要上架的sku商品信息
            List<WhSkuInventory> whskuList = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, pdaManMadePutawayCommand.getIsOuterContainer());
            // 验证库位是否绑定了容器内的所有商品
            boolean result = true;
            for (WhSkuInventory skuInventory : whskuList) {
                Long skuId = skuInventory.getSkuId();
                for (WhSkuLocationCommand skuLoc : listCommand) {
                    Long sId = skuLoc.getSkuId();
                    if (skuId.equals(sId)) {
                        result = false;
                        break;
                    }
                }
            }
            if (result) { // 静态库位没有绑定所有的sku商品
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_STATICLOCATION_NOTINCLUDEALLSKU);
            } else {
                // 判断库位是否允许混放
                if (mixStacking) {
                    // 允许混放
                    this.pdaLocationIsMix(pdaManMadePutawayCommand, invAttrMgmtHouse,containerId);
                } else {
                    // 不允许混放
                    this.pdaLocationNotMix(pdaManMadePutawayCommand,containerId);
                }
            }
        } else { // 不是静态库位
            if (mixStacking) { // 判断是否允许混放
                // 允许混放
                this.pdaLocationIsMix(pdaManMadePutawayCommand, invAttrMgmtHouse,containerId);
            } else {
                // 不允许混放
                this.pdaLocationNotMix(pdaManMadePutawayCommand,containerId);
            }
        }
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayDao.findWhFunctionPutAwayByFunctionId(functionId, ouId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        if (false == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            // 整托上架
            if( WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
                pdaManMadePutawayCommand.setPutway(true); // 上架
                // 上架,清楚缓存
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand, insideCommand, locationId, functionId, warehouse, putawayPatternDetailType, ouId,pdaManMadePutawayCommand.getUserId(), logId);
                // 清楚缓存
                pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
            }
            //整箱上架
            if( WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                ManMadeContainerStatisticCommand madecsCmd = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC, containerId.toString());
                if (null == madecsCmd) {
                    madecsCmd = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(pdaManMadePutawayCommand, containerId);
                }
                Set<Long> insideContainerIds = madecsCmd.getInsideContainerIds();   //所有容器id集合
                CheckScanSkuResultCommand cssrCmd = pdaManmadePutawayCacheManager.manMadeContainerCacheContainer(outCommand, insideCommand, insideContainerIds, logId);
                if(cssrCmd.isNeedTipContainer() == true) { //还有内部容器没有扫描的
                    pdaManMadePutawayCommand.setIsAfterPutawayContainer(true);
                    // 执行上架方法
                    whSkuInventoryManager.manMadePutaway(null,null,null,outCommand, insideCommand, locationId, functionId, warehouse, putawayPatternDetailType, ouId,pdaManMadePutawayCommand.getUserId(), logId);
                    // 清楚缓存
                    if(pdaManMadePutawayCommand.getIsOuterContainer()) {
                        pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand, insideCommand, true, insideContainerCode);
                    }else{
                        pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand, insideCommand, false, insideContainerCode);
                    }
                }else{ //已经扫描完毕
                    pdaManMadePutawayCommand.setPutway(true); // 上架
                    pdaManMadePutawayCommand.setIsAfterPutawayContainer(true);
                    // 执行上架方法
                    whSkuInventoryManager.manMadePutaway(null,null,null,outCommand, insideCommand, locationId, functionId, warehouse, putawayPatternDetailType, ouId,pdaManMadePutawayCommand.getUserId(), logId);
                    // 清楚缓存
                    if(pdaManMadePutawayCommand.getIsOuterContainer()) {
                        pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand, insideCommand, true, insideContainerCode);
                    }else{
                        pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand, insideCommand, false, insideContainerCode);
                    }
                }
              
            }
           
        }
        return pdaManMadePutawayCommand;
    }



    /**
     * 库位不允许混放逻辑
     * 
     * @param command
     * @return
     */
    private void pdaLocationNotMix(PdaManMadePutawayCommand command,Long containerId) {
        Long ouId = command.getOuId();
        // 判断托盘或货箱是否存在多个sku商品
        List<WhSkuInventory> whskuList = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, command.getIsOuterContainer());
        boolean hasMulitSku = this.isNoContainerMultiSku(whskuList);
        if (!hasMulitSku) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_NOT_MULTISKU);
        }
        // 查询库位上已有商品
        WhSkuInventory inventory = new WhSkuInventory();
        inventory.setOuId(command.getOuId());
        inventory.setLocationId(command.getLocationId());
        List<WhSkuInventory> LocationSkuList = whSkuInventoryDao.findWhSkuInventoryByPramas(inventory);
        // 判断托盘/货箱是否存在相同sku，不同库存属性的商品
        boolean diffAttibutes = this.isNoSkuDiffAtt(whskuList);
        if (!diffAttibutes) {
            throw new BusinessException(ErrorCodes.PDA_NOT_ALLOW_DIFFERENT_INVENTORY_ATTRIBUTES);
        }
        // 如果库位已存在库存商品，判断 ：托盘、货箱内的sku商品、库存属性是否和库位上的相同
        boolean IsAttConsistent = this.IsAttConsistent(LocationSkuList, whskuList);
        if (!IsAttConsistent) {
            throw new BusinessException(ErrorCodes.PDA_CONTAINER_SKUATT_NOTSAME_LOCATION_SKUATT);
        }
        // 累加容器，容器内sku商品、库位上已有容器，商品重量， 判断是否<=库位承重 *
        this.IsLocationBearWeight(containerId, LocationSkuList, whskuList, command);
    }


    /**
     * 库位允许混放逻辑
     * 
     * @param command
     * @return
     */
    private void pdaLocationIsMix(PdaManMadePutawayCommand command, String invAttrMgmtHouse,Long containerId) {
        Long ouId = command.getOuId();
        Long locationId = command.getLocationId();
        // 从缓存中取出容器内的商品
        List<WhSkuInventory> whskuList = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, command.getIsOuterContainer());
        // 查询库位上已有商品
        WhSkuInventory inventory = new WhSkuInventory();
        inventory.setOuId(ouId);
        inventory.setLocationId(command.getLocationId());
        List<WhSkuInventory> locationSkuList = whSkuInventoryDao.findWhSkuInventoryByPramas(inventory);
        // 判断库位上已有的sku商品,容器内所有sku商品对应店铺/仓库配置的关键库存属性参数是否相同,如果不相同,则不能混放
        this.verifyStoreOrWarehouse(ouId, command.getPutawayPatternDetailType(), invAttrMgmtHouse,whskuList, locationSkuList);
        //库位已经上架sku商品属性数
        Long locChaosSku =  this.skuAttrCount(locationSkuList);
        //容器内sku商品属性数
        Long skuChaosSku = this.skuAttrCount(whskuList);
        // 查询库位最大混放SKU种类数
        Location location = whLocationDao.findByIdExt(locationId, ouId);
        if(null == location) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
        }
        Integer mixStackingNumber = location.getMixStackingNumber(); // 库位上最大混放种类数
        if(null == mixStackingNumber) {
            mixStackingNumber = 0;
        }
        Long maxChaosSku = location.getMaxChaosSku(); // 库位最大混放SKU属性数
        if(null == maxChaosSku){
            maxChaosSku = 0L;
        }
        // 获取整托或者整箱的sku种类数
        Integer skuMixStacking = 0;
        Long skuId = whskuList.get(0).getSkuId();
        for (int i = 1; i < whskuList.size(); i++) {
            Long tempId = whskuList.get(i).getSkuId();
            if (skuId != tempId) {
                skuMixStacking = skuMixStacking + 1;
            }
        }
        // 获取库位上已上架的sku种类数
        Integer locMixStacking = 0;
        if (null != locationSkuList) {
            Long skuLocId = locationSkuList.get(0).getSkuId();
            for (int i = 1; i < locationSkuList.size(); i++) {
                Long tempId = locationSkuList.get(i).getSkuId();
                if (skuLocId != tempId) {
                    locMixStacking = locMixStacking + 1;
                }
            }
        }
        // 判断库位已有sku种类数+容器内SKU种类数是否<=最大混放SKU种类数 *
        if (mixStackingNumber < (skuMixStacking + locMixStacking)) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_VARIETY_OVER_MAX);
        }
        // 验证库位最大混放sku属性数,判断库位已有SKU属性数+容器内SKU属性数是否<=最大混放SKU属性数*
        if (maxChaosSku < (locChaosSku + skuChaosSku)) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_ATT_OVER_MAX);
        }
        // 累加容器、容器内SKU商品、库位上已有容器、商品的重量，判断是否<=库位承重*
        this.IsLocationBearWeight(containerId, locationSkuList, whskuList, command);
    }



    /**
     * 判断托盘或货箱是否存在多个sku商品
     * 
     * @param mapObjectList
     * @return
     */
    private boolean isNoContainerMultiSku(List<WhSkuInventory> whSkuInventoryList) {
        log.info("PdaManMadePutwayManagerImpl isNoContainerMultiSku is start");
        Boolean result = true; // 默认托盘或者货箱不存在多个sku
        Long tempCount = whSkuInventoryList.get(0).getSkuId();
        for (int i = 1; i < whSkuInventoryList.size(); i++) {
            WhSkuInventory skuInventory = whSkuInventoryList.get(i);
            Long skuId = skuInventory.getSkuId();
            if (skuId.equals(tempCount)) {
                tempCount = skuId;
            } else {
                result = false;
                break;
            }
        }
        log.info("PdaManMadePutwayManagerImpl isNoContainerMultiSku start");
        return result;
    }

    /**
     * 判断托盘/货箱是否存在相同sku，不同库存属性的商品
     * 
     * @author
     * @param mapObjectList
     * @return
     */
    private boolean isNoSkuDiffAtt(List<WhSkuInventory> whSkuInventoryList) {
        log.info("PdaManMadePutwayManagerImpl isNoSkuDiffAtt start");
        boolean result = true; // 默认没有不同的库存属性商品
        WhSkuInventory skuInventory = whSkuInventoryList.get(0);
        for (int i = 1; i < whSkuInventoryList.size(); i++) {
            WhSkuInventory skuInv = whSkuInventoryList.get(i);
            if (null != skuInventory.getInvStatus() && !(skuInventory.getInvStatus().equals(skuInv.getInvStatus()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (null != skuInventory.getInvType() && !(skuInventory.getInvType().equals(skuInv.getInvType()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (null != skuInventory.getBatchNumber() && !(skuInventory.getBatchNumber().equals(skuInv.getBatchNumber()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (null != skuInventory.getMfgDate() && !(skuInventory.getMfgDate().equals(skuInv.getMfgDate()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (null != skuInventory.getExpDate() && !(skuInventory.getExpDate().equals(skuInv.getExpDate()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (null != skuInventory.getCountryOfOrigin() && !(skuInventory.getCountryOfOrigin().equals(skuInv.getCountryOfOrigin()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (null != skuInventory.getInvAttr1() && !(skuInventory.getInvAttr1().equals(skuInv.getInvAttr1()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (null != skuInventory.getInvAttr2() && !(skuInventory.getInvAttr2().equals(skuInv.getInvAttr2()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (null != skuInventory.getInvAttr3() && !(skuInventory.getInvAttr3().equals(skuInv.getInvAttr3()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (null != skuInventory.getInvAttr4() && !(skuInventory.getInvAttr4().equals(skuInv.getInvAttr4()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (null != skuInventory.getInvAttr5() && !(skuInventory.getInvAttr5().equals(skuInv.getInvAttr5()))) {
                result = false;
                skuInventory = skuInv;
                break;
            }
        }
        log.info("PdaManMadePutwayManagerImpl isNoSkuDiffAtt end");
        return result;
    }

    /**
     * 如果库位已存在库存商品，判断 ：托盘、货箱内的sku商品、库存属性是否和库位上的相同
     * 
     * @author
     * @param locationSkuList
     * @param mapObjectList
     * @return
     */
    private boolean IsAttConsistent(List<WhSkuInventory> locationSkuList, List<WhSkuInventory> skuInventoryList) {
        log.info("PdaManMadePutwayManagerImpl IsAttConsistent start");
        boolean result = true; // 托盘或者货箱内的sku库存属性和库位上的相同
        for (WhSkuInventory skuInv : locationSkuList) {
            for (WhSkuInventory inventory : skuInventoryList) {
                if (null != skuInv.getInvStatus() && !(skuInv.getInvStatus().equals(inventory.getInvStatus()))) {
                    result = false;
                    break;
                }
                if (null != skuInv.getInvType() && !(skuInv.getInvType().equals(inventory.getInvType()))) {
                    result = false;
                    break;
                }
                if (null != skuInv.getBatchNumber() && !(skuInv.getBatchNumber().equals(inventory.getBatchNumber()))) {
                    result = false;
                    break;
                }
                if (null != skuInv.getMfgDate() && !(skuInv.getMfgDate().equals(inventory.getMfgDate()))) {
                    result = false;
                    break;
                }
                if (null != skuInv.getExpDate() && !(skuInv.getExpDate().equals(inventory.getExpDate()))) {
                    result = false;
                    break;
                }
                if (null != skuInv.getCountryOfOrigin() && !(skuInv.getCountryOfOrigin().equals(inventory.getCountryOfOrigin()))) {
                    result = false;
                    break;
                }
                if (null != skuInv.getInvAttr1() && !(skuInv.getInvAttr1().equals(inventory.getInvAttr1()))) {
                    result = false;
                    break;
                }
                if (null != skuInv.getInvAttr2() && !(skuInv.getInvAttr2().equals(inventory.getInvAttr2()))) {
                    result = false;
                    break;
                }
                if (null != skuInv.getInvAttr3() && !(skuInv.getInvAttr3().equals(inventory.getInvAttr3()))) {
                    result = false;
                    break;
                }
                if (null != skuInv.getInvAttr4() && !(skuInv.getInvAttr4().equals(inventory.getInvAttr4()))) {
                    result = false;
                    break;
                }
                if (null != skuInv.getInvAttr5() && !(skuInv.getInvAttr5().equals(inventory.getInvAttr5()))) {
                    result = false;
                    break;
                }
            }
        }
        log.info("PdaManMadePutwayManagerImpl IsAttConsistent end");
        return result;
    }


    /**
     * 累加容器，容器内sku商品、库位上已有容器，商品重量， 判断是否<=库位承重 *
     * 
     * @author
     * @param containerId
     * @param locationSkuList
     * @param mapObjectList
     * @return
     */
    private void IsLocationBearWeight(Long containerId, List<WhSkuInventory> locationSkuList, List<WhSkuInventory> whSkuInventoryList, PdaManMadePutawayCommand command) {
        log.info("PdaManMadePutwayManagerImpl IsLocationBearWeight start");
        Long locationId = command.getLocationId();
//        String insideContainerCode = command.getInsideContainerCode();
        Long ouId = command.getOuId();
//        ContainerCommand insideCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
//        Long insideContainerId = insideCmd.getId();  //整箱上架时，扫描的内部容器id
        int putawayPatternDetailType = command.getPutawayPatternDetailType();
        ManMadeContainerStatisticCommand madeContainer = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(command,containerId);
        if (null == madeContainer) {
            throw new BusinessException(ErrorCodes.CASELEVEL_GET_CACHE_ERROR);
        }
        // 查询库位
        Location location = whLocationDao.findByIdExt(locationId, ouId);
        if(null == location) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
        }
        Long locId = location.getId();
        Double locWeight = location.getWeight(); // 库位总承重
        // 当前库位已经存在的重量
        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
        uomMap.put(WhUomType.LENGTH_UOM, madeContainer.getLenUomConversionRate());
        uomMap.put(WhUomType.WEIGHT_UOM, madeContainer.getWeightUomConversionRate());
        LocationInvVolumeWeightCommand livw = whLocationInvVolumeWieghtManager.calculateLocationInvVolumeAndWeight(locId, ouId, uomMap, logId);
        Double livwWeight = livw.getWeight();// 库位上已有货物总重量
        // 整托上架
        if (WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
            Double putwayWeight = madeContainer.getContainerWeight();
            Set<Long> insideContainerIds = madeContainer.getInsideContainerIds();
            Map<Long, Double> insideContainersWeight = madeContainer.getInsideContainersWeight();
            // 计算要上架商品的重量
            for (Long cId : insideContainerIds) {
                putwayWeight = putwayWeight + insideContainersWeight.get(cId);
            }
            // 库位已有商品重量加要上架货物的重量
            Double sum = livwWeight + putwayWeight;
            if (locWeight < sum) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_UNBEAR_WEIGHT); // 容器总重量已经超过库位的承重,请更换库位进行上架
            }
        }
        // 整箱上架
        if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
            String insideContainerCode = command.getInsideContainerCode();
            ContainerCommand insideCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
            Long insideContainerId = insideCmd.getId();  //整箱上架时，扫描的内部容器id
            Double putwayWeight = madeContainer.getInsideContainerWeight();
            Map<Long, Double> insideContainersWeight = madeContainer.getInsideContainersWeight();
            // 计算要上架商品的重量
            putwayWeight = putwayWeight + insideContainersWeight.get(insideContainerId);
            // 库位已有商品重量加要上架货物的重量
            Double sum = livwWeight + putwayWeight;
            if (locWeight < sum) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_UNBEAR_WEIGHT); // 容器总重量已经超过库位的承重,请更换库位进行上架
            }
        }
        // 拆箱上架
        if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
            Double putwayWeight = 0.0;
            for(WhSkuInventory skuInv:whSkuInventoryList) {
                Long skuId = skuInv.getSkuId();
                WhSkuCommand whSkuCommand = whSkuDao.findWhSkuByIdExt(skuId, ouId);
                if(null == whSkuCommand) {
                    throw new BusinessException(ErrorCodes.SKU_NOT_FOUND); // 商品不存在
                }
                putwayWeight = putwayWeight+ whSkuCommand.getWeight();
            }
            // 库位已有商品重量加要上架货物的重量
            Double sum = livwWeight + putwayWeight;
            if (locWeight < sum) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_UNBEAR_WEIGHT); // 容器总重量已经超过库位的承重,请更换库位进行上架
            }
        }
        log.info("PdaManMadePutwayManagerImpl IsLocationBearWeight end");
    }



    /**
     * 查询仓库/店铺的关键属性
     * 
     * @param containerId
     * @param ouId
     * @param putawayPatternDetailType
     * @param invAttrMgmtHouse 仓库库存属性
     * @return
     */
    private void verifyStoreOrWarehouse(Long ouId, int putawayPatternDetailType, String invAttrMgmtHouse, List<WhSkuInventory> whskuList, List<WhSkuInventory> locationSkuList) {
        log.info("PdaManMadePutwayManagerImpl verifyStoreOrWarehouse start");
        // 获取店铺id
        Long storeId = whskuList.get(0).getStoreId();
        Store store = storeDao.findById(storeId);
        // 如果店铺配置了 就依据店铺的来，店铺优先级大于仓库
        if (null == store) {
            throw new BusinessException(ErrorCodes.COMMON_STORE_NOT_FOUND_ERROR);
        }
        String invAttrMgmt = store.getInvAttrMgmt(); // 店铺的库存属性
        if (null == invAttrMgmt) {
            if (null != invAttrMgmtHouse) {
                String[] warehouseAttr = invAttrMgmtHouse.split(","); // 仓库库存属性
                this.attMgmt(warehouseAttr, whskuList, locationSkuList);
            }
        } else {
            String[] storeAttr = invAttrMgmt.split(","); // 拆分店铺的库存属性
            this.attMgmt(storeAttr, whskuList, locationSkuList);
        }
        log.info("PdaManMadePutwayManagerImpl verifyStoreOrWarehouse end");
    }

    
    private void attMgmt(String[] invAttrMgmt,List<WhSkuInventory> whskuList, List<WhSkuInventory> locationSkuList){
        Long result = 0L;
        for (String attr : invAttrMgmt) {
            if (Constants.INV_ATTR_TYPE.equals(attr)) { // 库存类型
                result = result + this.isAttMgmtSame(locationSkuList, whskuList);
            }
            if (Constants.INV_ATTR_STATUS.equals(attr)) { // 库存状态
                result = result + this.isAttMgmtSame(locationSkuList, whskuList);
            }
            if (Constants.INV_ATTR_BATCH.equals(attr)) { // 库存类型
                result = result + this.isAttMgmtSame(locationSkuList, whskuList);
            }
            if (Constants.INV_ATTR_MFG_DATE.equals(attr)) { // 生产日期
                result = result + this.isAttMgmtSame(locationSkuList, whskuList);
            }
            if (Constants.INV_ATTR_EXP_DATE.equals(attr)) { // 失效日期
                result = result + this.isAttMgmtSame(locationSkuList, whskuList);
            }
            if (Constants.INV_ATTR_ORIGIN.equals(attr)) { // 原产地
                result = result + this.isAttMgmtSame(locationSkuList, whskuList);
            }
            if (Constants.INV_ATTR1.equals(attr)) { // 库存属性1
                result = result + this.isAttMgmtSame(locationSkuList, whskuList);
            }
            if (Constants.INV_ATTR2.equals(attr)) { // 库存属性2
                result = result + this.isAttMgmtSame(locationSkuList, whskuList);
            }
            if (Constants.INV_ATTR3.equals(attr)) { //库存属性3
                result =  result +this.isAttMgmtSame(locationSkuList, whskuList);
            }
            if (Constants.INV_ATTR4.equals(attr)) { // 库存属性4
                result = result + this.isAttMgmtSame(locationSkuList, whskuList);
            }
            if (Constants.INV_ATTR5.equals(attr)) { // 库存属性5
                result = result + this.isAttMgmtSame(locationSkuList, whskuList);
            }
        }
        if(0 != result) {
            //库位,容器内sku商品关键库存属性参数不相同,不能整托/整箱/拆箱上架
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_ATTR_MGMT_NOT_EQUAL);
        }
    }
    
    
    /**
     * 判断库位上已有SKU商品，容器内所有SKU商品，对应店铺/仓库配置的关键库存属性参数是否相同
     * 
     * @param isStoreAtt
     * @param locationSkuList
     * @param mapObjectList
     * @return
     */
    private Long isAttMgmtSame(List<WhSkuInventory> locationSkuList, List<WhSkuInventory> whskuList) {
        log.info("PdaManMadePutwayManagerImpl isAttMgmtSame start");
        Long count = 0L;  //容器内sku和库位上的sku库存属性不同的数量
        for(WhSkuInventory cSkuInv:whskuList) {
            for(WhSkuInventory locSkuInv:locationSkuList) {
                if(cSkuInv.getSkuId() == locSkuInv.getSkuId()) {
                   //库存类型不同
                    Long invType = this.compaterTo(cSkuInv.getInvType(), locSkuInv.getInvType());
                    if(invType != 0) {
                        count = count+ invType;
                    }
                    Long invStatus = this.compaterTo(cSkuInv.getInvStatus(), locSkuInv.getInvStatus());
                    if(invStatus != 0) {
                        count = count+ invStatus;
                    }
                    Long batchNumber = this.compaterTo(cSkuInv.getBatchNumber(), locSkuInv.getBatchNumber());
                    if(batchNumber != 0) {
                        count = count+ batchNumber;
                    }
                    Long mfgDate = this.compaterTo(cSkuInv.getMfgDate(), locSkuInv.getMfgDate());
                    if(mfgDate != 0) {
                        count = count+ mfgDate;
                    }
                    Long expDate = this.compaterTo(cSkuInv.getExpDate(), locSkuInv.getExpDate());
                    if(expDate != 0) {
                        count = count+ expDate;
                    }
                    Long countryOfOrigin = this.compaterTo(cSkuInv.getCountryOfOrigin(), locSkuInv.getCountryOfOrigin());
                    if(countryOfOrigin != 0) {
                        count = count+ countryOfOrigin;
                    }
                    Long invAttr1 = this.compaterTo(cSkuInv.getInvAttr1(), locSkuInv.getInvAttr1());
                    if(invAttr1 != 0) {
                        count = count+ invAttr1;
                    }
                    Long invAttr2 = this.compaterTo(cSkuInv.getInvAttr2(), locSkuInv.getInvAttr2());
                    if(invAttr2 != 0) {
                        count = count+ invAttr2;
                    }
                    Long invAttr3 = this.compaterTo(cSkuInv.getInvAttr3(), locSkuInv.getInvAttr3());
                    if(invAttr3 != 0) {
                        count = count+ invAttr3;
                    }
                    Long invAttr4 = this.compaterTo(cSkuInv.getInvAttr4(), locSkuInv.getInvAttr4());
                    if(invAttr4 != 0) {
                        count = count+ invAttr4;
                    }
                    Long invAttr5 = this.compaterTo(cSkuInv.getInvAttr5(), locSkuInv.getInvAttr5());
                    if(invAttr5 != 0) {
                        count = count+ invAttr5;
                    }
                }
                
            }
        }
        log.info("PdaManMadePutwayManagerImpl isAttMgmtSame end");
        return count;
    }
    
    /***
     * 比较字符串是否相同
     * @param cSkuInv
     * @param locSkuInv
     * @return
     */
    private Long compaterTo(Object cSkuInvAttr,Object locSkuInvAttr){
        Long count = 0L;   //店铺,库存配置关键库存属性时,要上架的sku商品和库位上已经上架的sku商品关键库存属性不同的数量
       if(!StringUtils.isEmpty(cSkuInvAttr)) {   //为空的时候返回true
            if(!cSkuInvAttr.equals(locSkuInvAttr)) {
                count++;
            }
        }else{
            if(!StringUtils.isEmpty(locSkuInvAttr)) {
               count++; 
            }
        }
        return count;
    }

    
    /***
     * 整托上架:扫描内部容器
     * 
     * @param pdaManMadePutawayCommand
     * @return
     */
    @Override
    public PdaManMadePutawayCommand manScanInsideContainer(PdaManMadePutawayCommand pdaManMadePutawayCommand, Long ouId,Warehouse warehouse) {
        // TODO Auto-generated method stub
        log.info("PdaManMadePutwayManagerImpl manScanInsideContainer start");
        String insideContainerCode = pdaManMadePutawayCommand.getInsideContainerCode();
        String outerContainerCode = pdaManMadePutawayCommand.getOuterContainerCode();
        Long functionId = pdaManMadePutawayCommand.getFunctionId();
        Long locationId = pdaManMadePutawayCommand.getLocationId();
        // 查询对应容器数据
        ContainerCommand outCommand = containerDao.getContainerByCode(outerContainerCode, ouId);   //内部容器
        if (null == outCommand) {
            // 容器信息不存在
            log.error("pdaScanContainer container is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        // 验证容器Lifecycle是否有效
        if (!outCommand.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            // 容器Lifecycle无效
            log.error("pdaScanContainer container lifecycle error =" + outCommand.getLifecycle() + " logid: " + logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        // 验证容器状态是否是待上架
        if (!(outCommand.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || outCommand.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
                log.error("pdaScanContainer container status error =" + outCommand.getStatus() + " logid: " + logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY, new Object[] {outCommand.getStatus()});
        }
        Long outerContainerId = outCommand.getId();
        
        ContainerCommand insideContainer = containerDao.getContainerByCode(insideContainerCode, ouId);   //内部容器
        if (null == insideContainer) {
            // 容器信息不存在
            log.error("pdaScanContainer container is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        // 验证容器Lifecycle是否有效
        if (!insideContainer.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            // 容器Lifecycle无效
            log.error("pdaScanContainer container lifecycle error =" + insideContainer.getLifecycle() + " logid: " + logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
        }
        // 验证容器状态是否是待上架
        if (!(insideContainer.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || insideContainer.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
                log.error("pdaScanContainer container status error =" + insideContainer.getStatus() + " logid: " + logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY, new Object[] {insideContainer.getStatus()});
        }
        
        Long insideContainerId = insideContainer.getId(); // 内部容器ID
        // 判断扫描的内部容器是否是托盘上的内部容器
        ManMadeContainerStatisticCommand madeContainer = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC, outerContainerId.toString());
        if (null == madeContainer) {
            throw new BusinessException(ErrorCodes.CASELEVEL_GET_CACHE_ERROR);
        }
        Set<Long> insideContainerIds = madeContainer.getInsideContainerIds(); // 托盘上所有内部容器id集合
        /** 所有caselevel内部容器 */
        Set<Long> caselevelContainerIds = madeContainer.getCaselevelContainerIds();
        /** 所有非caselevel内部容器 */
        Set<Long> notcaselevelContainerIds = madeContainer.getNotcaselevelContainerIds();
        if (!insideContainerIds.contains(insideContainerId)) { // 扫描的内部容器不在托盘内
            throw new BusinessException(ErrorCodes.INSIDECONTAINER_NOT_EXISTS_OUTCONTAINER);
        }
        // 缓存扫描的内部容器
        pdaManmadePutawayCacheManager.containerPutawayCacheInsideContainer(insideContainer, outerContainerId, insideContainerCode, outerContainerCode);
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku()); // caselevel
                                                                                                                                  // 是否扫sku,true是扫sku,false是不扫sku
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku()); // 非caselevel
                                                                                                                                           // 是否扫sku
        // 判断内部容器是否是caselevel容器
        WhCarton carton = whCartonDao.findWhCaselevelCartonByContainerId(insideContainerId, ouId);
        if (null != carton) { // caselevel容器
            if (!caselevelContainerIds.contains(insideContainerId)) { // 扫描的内部容器不在托盘内
                throw new BusinessException(ErrorCodes.INSIDECONTAINER_NOT_EXISTS_OUTCONTAINER);
            }
            if (isCaselevelScanSku) { // 需要扫sku
                pdaManMadePutawayCommand.setIsNeedScanSku(true);
                return pdaManMadePutawayCommand;
            } else { // 不需要扫sku,直接上架
                pdaManMadePutawayCommand.setPutway(true);
             // 上架,清楚缓存
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand, insideContainer, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, pdaManMadePutawayCommand.getUserId(), logId);
                // 清楚缓存
                pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
            }
        } else { // 非caselevel容器
            if (!notcaselevelContainerIds.contains(insideContainerId)) { // 扫描的内部容器不在托盘内
                throw new BusinessException(ErrorCodes.INSIDECONTAINER_NOT_EXISTS_OUTCONTAINER);
            }
            if (isNotcaselevelScanSku) { // 需要扫描sku
                pdaManMadePutawayCommand.setIsNeedScanSku(true);
                return pdaManMadePutawayCommand;
            } else {// 不需要扫描sku,直接上架
                pdaManMadePutawayCommand.setPutway(true);
             // 上架,清楚缓存
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand, insideContainer, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, pdaManMadePutawayCommand.getUserId(), logId);
                // 清楚缓存
                pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
            }
        }

        log.info("PdaManMadePutwayManagerImpl manScanInsideContainer end");
        return pdaManMadePutawayCommand;
    }

    /***
     * 整托上架:扫描sku
     * 
     * @param pdaManMadePutawayCommand
     * @param ouId
     * @return
     */
    @Override
    public PdaManMadePutawayCommand manMadeScanSku(PdaManMadePutawayCommand pdaManMadePutawayCommand, Long ouId, WhSkuCommand skuCmd, Warehouse warehouse) {
        // TODO Auto-generated method stub
        log.info("PdaManMadePutwayManagerImpl manMadeScanSku start");
        PdaManMadePutawayCommand resultCommand = new PdaManMadePutawayCommand();
        BeanUtils.copyProperties(pdaManMadePutawayCommand, resultCommand);
        String insideContainerCode = pdaManMadePutawayCommand.getInsideContainerCode();
        String outerContainerCode = pdaManMadePutawayCommand.getOuterContainerCode();
        Long functionId = pdaManMadePutawayCommand.getFunctionId();
        Long locationId = pdaManMadePutawayCommand.getLocationId();// 库位条码
        Long userId = pdaManMadePutawayCommand.getUserId();
        ContainerCommand insideCommand = containerDao.getContainerByCode(insideContainerCode, ouId); // 根据内部容器编码查询内部容器
        if (null == insideCommand) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 外部容器不存在
        }
        ContainerCommand outCommand = containerDao.getContainerByCode(outerContainerCode, ouId); // 根据外部容器编码查询外部容器
        if (null == outCommand) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 外部容器不存在
        }
        Long outerContianerId = outCommand.getId(); // 外不容器id
        String barCode = skuCmd.getBarCode(); // 商品条码
        Double scanQty = skuCmd.getScanSkuQty(); // 扫描的商品数量
        if (null == scanQty || scanQty.longValue() < 1) {
            log.error("scan sku qty is valid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
        }
        if (StringUtils.isEmpty(barCode)) {
            log.error("sku is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
//        WhSkuCommand whSkuCommand = whSkuDao.findWhSkuByBarcodeExt(barCode, ouId); // 根据商品条码，查询商品信息
//        if (null == whSkuCommand) {
//            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND); // 查看扫描的商品在商品表中是否存在
//        } else {
//            BeanUtils.copyProperties(whSkuCommand, skuCmd);
//            whSkuCommand.setScanSkuQty(scanQty);
//        }
//        if (Constants.LIFECYCLE_START != whSkuCommand.getLifecycle()) {
//            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SKULIFRCYCLE_ERROR); // 查看扫描的商品在商品表中是否存在
//        }
        ManMadeContainerStatisticCommand mcsCmd = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(pdaManMadePutawayCommand, outerContianerId);
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Long skuId = null;
        // 2.复核扫描的商品并判断是否切换内部容器
        Set<Long> insideContainerIds = mcsCmd.getInsideContainerIds(); // 所有内部容器id集合
        Set<Long> caselevelContainerIds = mcsCmd.getCaselevelContainerIds(); // 所有caselevel容器集合
        Set<Long> notcaselevelContainerIds = mcsCmd.getNotcaselevelContainerIds(); // 所有非caselevel容器集合
        Map<Long, Set<Long>> insideContainerIdSkuIds = mcsCmd.getInsideContainerIdSkuIds(); // 所有内部容器对应的sku商品
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = mcsCmd.getInsideContainerSkuIdsQty(); // 内部容器单个sku总件数
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
        resultCommand.setScanPattern(scanPattern); // 整托上架扫描方式
        // 商品校验
        String skuBarcode = skuCmd.getBarCode();
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId); // 获取对应的商品数量,key值是sku
                                                                                                 // id
        Set<Long> icSkuIds = insideContainerIdSkuIds.get(insideCommand.getId()); // 当前容器内所有sku id集合
        Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(insideCommand.getId());
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
                icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
                break;
            }
        }
        if (false == isSkuExists) {
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideCommand.getId(), insideCommand.getId(), skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideCommand.getCode()});
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
                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarcode, logId);
                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
            }
        }
        skuCmd.setId(skuId);
        skuCmd.setScanSkuQty(scanQty * cacheSkuQty);// 可能是多条码
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 全部货箱扫描
            CheckScanSkuResultCommand cssrCmd = pdaManmadePutawayCacheManager.manMadePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, insideContainerIds, insideContainerIdSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
            if (cssrCmd.isNeedScanSku()) {
                resultCommand.setIsNeedScanSku(true); // 商品没有扫描完毕
                resultCommand.setTipContainerCode(insideContainerCode);
            } else if (cssrCmd.isNeedTipContainer()) {
                resultCommand.setIsNeedScanContainer(true); // 内不容器没有扫描好
                Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
                if (null == tipContainer) {
                    log.error("container is null error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                }
            } else {
                resultCommand.setPutway(true);
                // 上架,清楚缓存
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand, insideCommand, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, userId, skuBarcode);
                // 清楚缓存
                pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
            }
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            // 只扫caselevel货箱
            if (null == caselevelContainerIds || 0 == caselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                resultCommand.setPutway(true);
                // 上架,清楚缓存
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand, insideCommand, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, userId, skuBarcode);
                // 清楚缓存
                pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
            } else {
                CheckScanSkuResultCommand cssrCmd =
                        pdaManmadePutawayCacheManager.manMadePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, insideContainerIds, insideContainerIdSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
                if (cssrCmd.isNeedScanSku()) {
                    resultCommand.setIsNeedScanSku(true); // 商品没有扫描完毕
                    resultCommand.setTipContainerCode(insideContainerCode);
                } else if (cssrCmd.isNeedTipContainer()) {
                    resultCommand.setIsNeedScanContainer(true); // 内不容器没有扫描好
                    Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
                    if (null == tipContainer) {
                        log.error("container is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                    }
                } else {
                    resultCommand.setPutway(true);
                    // 上架,清楚缓存
                    whSkuInventoryManager.manMadePutaway(null,null,null,outCommand, insideCommand, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, userId, skuBarcode);
                    // 清楚缓存
                    pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
                }
            }

        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 只扫非caselvel货箱
            if (null == notcaselevelContainerIds || 0 == notcaselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                resultCommand.setPutway(true);
                // 上架,清楚缓存
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand, insideCommand, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, userId, skuBarcode);
                // 清楚缓存
                pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
            } else {
                CheckScanSkuResultCommand cssrCmd =
                        pdaManmadePutawayCacheManager.manMadePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, insideContainerIds, insideContainerIdSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
                if (cssrCmd.isNeedScanSku()) {
                    resultCommand.setIsNeedScanSku(true); // 商品没有扫描完毕
                } else if (cssrCmd.isNeedTipContainer()) {
                    resultCommand.setIsNeedScanContainer(true); // 内不容器没有扫描好
                } else {
                    resultCommand.setPutway(true);
                    // 上架,清楚缓存
                    whSkuInventoryManager.manMadePutaway(null,null,null,outCommand, insideCommand, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, userId, skuBarcode);
                    // 清楚缓存
                    pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
                }
            }
        } else {
            log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_IS_CHANGE_ERROR);
        }
        log.info("PdaManMadePutwayManagerImpl manMadeScanSku end");
        return resultCommand;
    }
    
    /***
     * 整箱上架:扫描sku
     * @param pdaManMadePutawayCommand
     * @param ouId
     * @param skuCmd
     * @param wareHouse
     * @return
     */ 
    public PdaManMadePutawayCommand containerPutwayScanSku(PdaManMadePutawayCommand pdaManMadePutawayCommand,Long ouId,WhSkuCommand skuCmd,Warehouse warehouse){
        log.info("PdaManMadePutwayManagerImpl containerPutwayScanSku start");
        String insideContainerCode = pdaManMadePutawayCommand.getInsideContainerCode();
        String outerContainerCode = pdaManMadePutawayCommand.getOuterContainerCode();
        Long functionId = pdaManMadePutawayCommand.getFunctionId();
        int putawayPatternDetailType = pdaManMadePutawayCommand.getPutawayPatternDetailType();
        Long locationId = pdaManMadePutawayCommand.getLocationId();    //库位条码
        Long userId = pdaManMadePutawayCommand.getUserId();
        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
        if(null == insideContainerCmd) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //容器不存在
        }
        //获取内部容器id
        Long insideContainerId = insideContainerCmd.getId();
        Long containerId = null;
        ContainerCommand outCommand = null;
        if(!StringUtils.isEmpty(outerContainerCode)) {
            outCommand = containerDao.getContainerByCode(outerContainerCode, ouId);  //根据容器编码查询外部容器
            containerId = outCommand.getId();
        }else{
            containerId = insideContainerId;
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
//        WhSkuCommand whSkuCommand =  whSkuDao.findWhSkuByBarcodeExt(barCode, ouId);
//        if(null == whSkuCommand){
//            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);    //查看扫描的商品在商品表中是否存在
//        }
        ManMadeContainerStatisticCommand madecsCmd = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC, containerId.toString());
        if (null == madecsCmd) {
            madecsCmd = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(pdaManMadePutawayCommand, containerId);
        }
        Set<Long> insideContainerIds = madecsCmd.getInsideContainerIds();   //所有容器id集合
        Set<Long> caselevelContainerIds = madecsCmd.getCaselevelContainerIds();
        Set<Long> noCaselevelContainerIds = madecsCmd.getNotcaselevelContainerIds();
        Map<Long, Set<Long>> insideContainerSkuIds = madecsCmd.getInsideContainerIdSkuIds();
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = madecsCmd.getInsideContainerSkuIdsQty();
        // 1.获取功能配置
        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
        if (null == putawyaFunc) {
            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
        }
        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
        // 商品校验
        Long skuId = null;
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(barCode, logId);   //获取对应的商品数量,key值是sku id
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
                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", barCode, logId);
                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
            }
        }
        skuCmd.setId(skuId);
        skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());   //true时，要扫sku，false不用扫sku
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 是caselevel货箱扫描商品
            CheckScanSkuResultCommand cssrCmd = pdaManmadePutawayCacheManager.manMadeContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern,logId);
            if (cssrCmd.isNeedScanSku()) {
                pdaManMadePutawayCommand.setIsNeedScanSku(true);
                pdaManMadePutawayCommand.setInsideContainerCode(insideContainerCode);
            }else if (cssrCmd.isNeedTipContainer()) { //一个容器扫描完,整箱上架，提示下一个容器
                pdaManMadePutawayCommand.setIsAfterPutawayContainer(true);
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId,  logId);
                //清除缓存
                pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd, false, logId);
            }else {
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                //清除缓存
                pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd, true, logId);
            }
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,true);
            if(count < 1) {  //此货箱不是caselevel货箱,直接上架
                //判断托盘上还有没有没上架的货箱
                whSkuInventoryManager.manMadePutaway(null,null,null,null,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                if(pdaManMadePutawayCommand.getIsOuterContainer()) {  //外部库存,整箱上架
                    //清除缓存
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,false, logId);
                }else{  //内部容器库存,整箱上架
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,true, logId);
                }
            }
            // 是caselevel货箱扫描商品
            CheckScanSkuResultCommand cssrCmd = pdaManmadePutawayCacheManager.manMadeContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, caselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty,skuCmd, scanPattern,logId);
            if (cssrCmd.isNeedScanSku()) {
                pdaManMadePutawayCommand.setIsNeedScanSku(true);// 直接扫描商品
                pdaManMadePutawayCommand.setInsideContainerCode(insideContainerCode);
            } else if (cssrCmd.isNeedTipContainer()) { //一个容器扫描完,整箱上架，提示下一个容器
                pdaManMadePutawayCommand.setIsAfterPutawayContainer(true);
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId,logId);
                //清除缓存
                pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,false, logId);
            } else {
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId,logId);
                if(pdaManMadePutawayCommand.getIsOuterContainer()) {  //外部库存,整箱上架
                    //清除缓存
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,false, logId);
                }else{  //内部容器库存,整箱上架
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,true, logId);
                }
                
            }
        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,false);
            if(count < 1) {  //此货箱不是caselevel货箱,直接上架
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand,insideContainerCmd,locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                if(pdaManMadePutawayCommand.getIsOuterContainer()) {  //外部库存,整箱上架
                    //清除缓存
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,false, logId);
                }else{  //内部容器库存,整箱上架
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,true, logId);
                }
            }
            // 是caselevel货箱扫描商品
            CheckScanSkuResultCommand cssrCmd = pdaManmadePutawayCacheManager.manMadeContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, noCaselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty,skuCmd, scanPattern,logId);
            if (cssrCmd.isNeedScanSku()) {
                pdaManMadePutawayCommand.setIsNeedScanSku(true);// 直接扫描商品
                pdaManMadePutawayCommand.setInsideContainerCode(insideContainerCode);
            } else if (cssrCmd.isNeedTipContainer()) { //一个容器扫描完,整箱上架，提示下一个容器
                pdaManMadePutawayCommand.setIsAfterPutawayContainer(true);
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                if(pdaManMadePutawayCommand.getIsOuterContainer()) {  //外部库存,整箱上架
                    //清除缓存
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,false, logId);
                }else{  //内部容器库存,整箱上架
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,true, logId);
                }
            } else {
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                if(pdaManMadePutawayCommand.getIsOuterContainer()) {  //外部库存,整箱上架
                    //清除缓存
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,false, logId);
                }else{  //内部容器库存,整箱上架
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,true, logId);
                }
                
            }
        }else if (false == isCaselevelScanSku && false == isNotcaselevelScanSku){
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(null,null,null,outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                if(pdaManMadePutawayCommand.getIsOuterContainer()) {  //外部库存,整箱上架
                    //清除缓存
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,false, logId);
                }else{  //内部容器库存,整箱上架
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,true, logId);
                }
        } else {
            log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_IS_CHANGE_ERROR);
        }
        
        log.info("PdaManMadePutwayManagerImpl containerPutwayScanSku end");
        return pdaManMadePutawayCommand;
    }

    /***
     * 拆箱上架:扫描sku
     * @param pdaManMadePutawayCommand
     * @param ouId
     * @param skuCmd
     * @param wareHouse
     * @return
     */
    @Override
    public PdaManMadePutawayCommand spiltContainerPutwayScanSku(PdaManMadePutawayCommand pdaManMadePutawayCommand, Long ouId, WhSkuCommand skuCmd) {
        // TODO Auto-generated method stub
        log.info("PdaManMadePutwayManagerImpl spiltContainerPutwayScanSku start");
        String outerContainerCode = pdaManMadePutawayCommand.getOuterContainerCode();
        String insideContainerCode = pdaManMadePutawayCommand.getInsideContainerCode();
        Integer scanPattern = pdaManMadePutawayCommand.getScanPattern();
        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
        if(null == insideContainerCmd) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //容器不存在
        }
        //获取内部容器id
        Long insideContainerId = insideContainerCmd.getId();
        Long containerId = null;
        if(!StringUtils.isEmpty(outerContainerCode)) {
            ContainerCommand outContainer = containerDao.getContainerByCode(outerContainerCode, ouId);
            if(null == outContainer) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //容器不存在
            }
            containerId = outContainer.getId();
        }else{
            containerId = insideContainerId;
        }
       
        String skuBarCode = skuCmd.getBarCode();
        Double scanQty = skuCmd.getScanSkuQty();
        if (null == scanQty || scanQty.longValue() < 1) {
            log.error("scan sku qty is valid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
        }
        if (StringUtils.isEmpty(skuBarCode)) {
            log.error("sku is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
//        WhSkuCommand whSkuCommand =  whSkuDao.findWhSkuByBarcodeExt(skuBarCode, ouId);
//        if(null == whSkuCommand){
//            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);    //查看扫描的商品在商品表中是否存在
//        }
//        //判断sku商品的状态是否可用
//        if(Constants.LIFECYCLE_START != whSkuCommand.getLifecycle()) {
//            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SKULIFRCYCLE_ERROR);    //查看扫描的商品在商品表中是否存在
//        }
        ManMadeContainerStatisticCommand madecsCmd = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC, containerId.toString());
        if (null == madecsCmd) {
            madecsCmd = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(pdaManMadePutawayCommand, containerId);
        }
        Map<Long, Set<Long>> insideContainerSkuIds = madecsCmd.getInsideContainerIdSkuIds();
        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = madecsCmd.getInsideContainerSkuIdsQty();
        // 商品校验
        Long skuId = null;
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarCode, logId);   //获取对应的商品数量,key值是sku id
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
        skuCmd.setId(skuId);
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
                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarCode, logId);
                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
            }
        }
        pdaManMadePutawayCommand.setSkuBarCode(skuBarCode);
        pdaManMadePutawayCommand.setScanSkuQty(scanQty); 
        //判断上架sku是否存在不同的库存属性
        List<WhSkuInventory> whskuList = new ArrayList<WhSkuInventory>();
        // 从缓存中取出容器内的商品(取出的商品全部是库存属性不同的商品)
        List<WhSkuInventory> list = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, pdaManMadePutawayCommand.getIsOuterContainer());
        for(WhSkuInventory skuInv:list){
            if(skuInv.getSkuId().equals(skuCmd.getId())) {
                    whskuList.add(skuInv);
            }
        }
        Boolean isSkuDiffAtt = this.isNoSkuDiffAtt(whskuList);
        if(!isSkuDiffAtt) { //相同的sku,存在不同的库存属性,进入扫sku库存属性页面
            pdaManMadePutawayCommand.setNeedSkuDetail(true);
            this.scanSkuDetailAspect(pdaManMadePutawayCommand, skuId, ouId); 
        }else{//相同的sku,不存在不同的库存属性,进入扫库位页面
            pdaManMadePutawayCommand.setNeedSkuDetail(false);
        }
        pdaManMadePutawayCommand.setSkuId(skuId);
        log.info("PdaManMadePutwayManagerImpl spiltContainerPutwayScanSku end");
        return pdaManMadePutawayCommand;
    }
    
    /***
     * 判断sku需要扫描哪些库存属性
     * @param cmd
     * @param skuId
     * @param ouId
     */
    private void scanSkuDetailAspect(PdaManMadePutawayCommand cmd,Long skuId,Long ouId){
        log.info("PdaManMadePutwayManagerImpl tipSkuDetailAspect start");
        cmd.setNeedScanSkuInvStatus(true);  //是否管理库存状态(必填)
        SkuExtattr skuExtattr = skuExtattrDao.findSkuExtattrBySkuIdShared(skuId, ouId);
        if(null == skuExtattr) {
            log.error("PdaManMadePutwayManagerImpl tipSkuDetailAspect param skuId is null logid: " + skuId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        if(true == skuExtattr.getInvAttr1()) {
            cmd.setNeedScanSkuInvAttr1(true);
        }
        if(true == skuExtattr.getInvAttr2()){
            cmd.setNeedScanSkuInvAttr2(true);
        }
        if(true == skuExtattr.getInvAttr3()) {
            cmd.setNeedScanSkuInvAttr3(true);
        }
        if(true == skuExtattr.getInvAttr4()){
            cmd.setNeedScanSkuInvAttr4(true);
        }
        if(true == skuExtattr.getInvAttr5()) {
            cmd.setNeedScanSkuInvAttr5(true);
        }
        SkuMgmt skuMgmt = skuMgmtDao.findSkuMgmtBySkuIdShared(skuId, ouId);
        if(null == skuMgmt) {
            log.error("PdaManMadePutwayManagerImpl tipSkuDetailAspect param skuId is null logid: " + skuId);
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
        }
        if(true == skuMgmt.getIsInvType()) {
            cmd.setNeedScanSkuInvType(true);
        }
        if(true == skuMgmt.getIsBatchNo()) {
            cmd.setNeedScanBatchNumber(true);
        }
        if(true == skuMgmt.getIsCountryOfOrigin()) {
            cmd.setNeedScanOrigin(true);
        }
        if(true == skuMgmt.getIsValid()) {
           cmd.setNeedScanSkuExpDate(true);
           cmd.setNeedScanSkuMfgDate(true);
        }
        log.info("PdaManMadePutwayManagerImpl tipSkuDetailAspect end");
    }
    
    /***
     * 拆箱上架扫描库位
     * @param pdaManMadePutawayCommand
     * @param invAttrMgmtHouse
     * @param warehouse
     * @return
     */
   public PdaManMadePutawayCommand splitPdaScanLocation(PdaManMadePutawayCommand pdaManMadePutawayCommand,String invAttrMgmtHouse,Warehouse warehouse){
       // 验证库位号是否为空
       Long ouId = pdaManMadePutawayCommand.getOuId();
       String barCode = pdaManMadePutawayCommand.getBarCode();  //库位条码
       String skuBarCode = pdaManMadePutawayCommand.getSkuBarCode();   //sku商品条码
       String locationCode = pdaManMadePutawayCommand.getLocationCode();
       String outerContainerCode = pdaManMadePutawayCommand.getOuterContainerCode();
       String insideContainerCode = pdaManMadePutawayCommand.getInsideContainerCode();
       Double scanQty = pdaManMadePutawayCommand.getScanSkuQty();
       Integer scanPattern= pdaManMadePutawayCommand.getScanPattern();
       ContainerCommand insideCommand = containerDao.getContainerByCode(insideContainerCode, ouId);
       if(null == insideCommand) {
           throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //容器不存在
       }
       //获取内部容器id
       Long insideContainerId = insideCommand.getId();
       Long containerId = null;
       ContainerCommand outCommand = null;
       if(!StringUtils.isEmpty(outerContainerCode)) {
           outCommand = containerDao.getContainerByCode(outerContainerCode, ouId);
           if(null == outCommand) {
               throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //容器不存在
           }
           containerId = outCommand.getId();
       }else{
           containerId = insideContainerId;
       }
       // 查询对应库位信息
       Location location = null;
       if(pdaManMadePutawayCommand.getIsInboundLocationBarcode()) {
           location = whLocationDao.getLocationByBarcode(barCode, ouId);
       }else{
           location = whLocationDao.findLocationByCode(locationCode, ouId);
       }
       // 验证库位是否存在
       if (null == location) {
           log.error("pdaScanLocation location is null logid: " + pdaManMadePutawayCommand.getLogId());
           throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
       }
       // 验证库位状态是否可用:lifecycle
       if (location.getLifecycle() != 1) {
           log.error("pdaScanLocation lifecycle is error logid: " + pdaManMadePutawayCommand.getLogId());
           throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_LIFECYCLE_ERROR);
       }
       //是否跟踪容器号
       if(location.getIsTrackVessel() && !pdaManMadePutawayCommand.getIsTrackVessel()) {  //跟踪容器号
           //在库位号输入框下面提示扫描容器号
           pdaManMadePutawayCommand.setIsTrackVessel(true); 
           if(warehouse.getIsInboundLocationBarcode()) {
               pdaManMadePutawayCommand.setLocationCode(location.getBarCode());  //
           }else{
               pdaManMadePutawayCommand.setLocationCode(location.getCode());
           }
           return pdaManMadePutawayCommand;
       }
       Long locationId = location.getId();
       if(pdaManMadePutawayCommand.getIsScanTrackContainer()) {   //如果操作员，扫描跟踪容器号，则处理跟踪容器号的流程,否则直接跳过
           String trackContainerCode = pdaManMadePutawayCommand.getTrackContainerCode();   //跟踪容器号
           //判断跟踪容器号是否存在
           ContainerCommand trackContainer = containerDao.getContainerByCode(trackContainerCode, ouId);
           if(null == trackContainer) {
               log.error("pdaScanLocation trackContainerCode is null logid: " + pdaManMadePutawayCommand.getTipContainerCode());
               throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
           }
           Long trackContainerId = trackContainer.getId();
           //判断扫描的跟踪容器号是否在对应的库位上
           Boolean verfiy = true; // 默认跟踪容器不在库位上
           List<WhSkuInventory> whSkuInvList = whSkuInventoryDao.findWhSkuInventoryByLocationId(ouId, locationId);
           for(WhSkuInventory whskuInv:whSkuInvList) {
               if(trackContainerId == whskuInv.getInsideContainerId()) {
                   verfiy = false;
                   break;
               }
               if(trackContainerId == whskuInv.getOuterContainerId()) {
                   verfiy = false;
                   break;
               }
           }
           if(verfiy) {
               log.error("pdaScanLocation trackContainerCode is error logid: " + pdaManMadePutawayCommand.getTipContainerCode());
               throw new BusinessException(ErrorCodes.TRACK_CONTAINER_NO_LOCATION);
           }
       }
       if (null == scanQty || scanQty.longValue() < 1) {
           log.error("scan sku qty is valid, logId is:[{}]", logId);
           throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
       }
       if (StringUtils.isEmpty(skuBarCode)) {
           log.error("sku is null error, logId is:[{}]", logId);
           throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
       }
//       WhSkuCommand skuCmd =  whSkuDao.findWhSkuByBarcodeExt(skuBarCode, ouId);
//       if(null == skuCmd){
//           throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);    //查看扫描的商品在商品表中是否存在
//       }
       //判断sku商品的状态是否可用
//       if(Constants.LIFECYCLE_START != skuCmd.getLifecycle()) {
//           throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SKULIFRCYCLE_ERROR);    //查看扫描的商品在商品表中是否存在
//       }
       ManMadeContainerStatisticCommand madecsCmd = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC, containerId.toString());
       if (null == madecsCmd) {
           madecsCmd = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(pdaManMadePutawayCommand, containerId);
       }
       Map<Long, Set<Long>> insideContainerSkuIds = madecsCmd.getInsideContainerIdSkuIds();
       Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = madecsCmd.getInsideContainerSkuIdsQty();
       // 商品校验
       Long skuId = null;
       Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarCode, logId);   //获取对应的商品数量,key值是sku id
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
               log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarCode, logId);
               throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
           }
       }
       WhSkuCommand skuCmd = new  WhSkuCommand();
       skuCmd.setId(skuId);
       skuCmd.setBarCode(skuBarCode);
       skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
       pdaManMadePutawayCommand.setLocationId(location.getId());
       boolean mixStacking = location.getIsMixStacking(); // 库位是否混放
       WhSkuInventoryCommand invSkuCmd = new WhSkuInventoryCommand();
       invSkuCmd.setSkuId(skuId);
       List<InventoryStatus> listInventoryStatus = inventoryStatusManager.findAllInventoryStatus();
         String statusValue = pdaManMadePutawayCommand.getSkuInvStatus();
         //库存状态
         if(!StringUtils.isEmpty(statusValue)) {
             for(InventoryStatus inventoryStatus:listInventoryStatus) {
                     if(statusValue.equals(inventoryStatus.getName()))
                         invSkuCmd.setInvStatus(inventoryStatus.getId());     //库存状态
                          break;
             }
         }
       invSkuCmd.setInvType(pdaManMadePutawayCommand.getSkuInvType());
       invSkuCmd.setBatchNumber(pdaManMadePutawayCommand.getBatchNumber());
       invSkuCmd.setMfgDate(pdaManMadePutawayCommand.getSkuMfgDate());
       invSkuCmd.setExpDate(pdaManMadePutawayCommand.getSkuExpDate());
       invSkuCmd.setCountryOfOrigin(pdaManMadePutawayCommand.getSkuOrigin());
       invSkuCmd.setInvAttr1(pdaManMadePutawayCommand.getSkuInvAttr1());
       invSkuCmd.setInvAttr2(pdaManMadePutawayCommand.getSkuInvAttr2());
       invSkuCmd.setInvAttr3(pdaManMadePutawayCommand.getSkuInvAttr3());
       invSkuCmd.setInvAttr4(pdaManMadePutawayCommand.getSkuInvAttr4());
       invSkuCmd.setInvAttr5(pdaManMadePutawayCommand.getSkuInvAttr5());
       // 验证库位是否静态库位
       if (location.getIsStatic()) {
           // 判断库位是否绑定了容器内所有的SKU商品
           WhSkuLocationCommand whSkuLocComand = new WhSkuLocationCommand();
           whSkuLocComand.setOuId(pdaManMadePutawayCommand.getOuId());
           whSkuLocComand.setLocationId(location.getId());
           List<WhSkuLocationCommand> listCommand = whSkuLocationDao.findSkuLocationToShard(whSkuLocComand);
           // 验证库位是否绑定了对应的商品
           boolean result = true;  //默认绑定扫描的sku商品
           for (WhSkuLocationCommand skuLoc : listCommand) {
                   Long sId = skuLoc.getSkuId();
                   if (skuId == sId) {
                       result = false;
                       break;
                   }
           }
           if (result) { // 库位为静态库位，没有绑定对应的sku商品，不能上架
               throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_STATICLOCATION_NOT_SKU);
           } else {
               // 判断库位是否允许混放
               if (mixStacking) {
                   // 允许混放
                   this.splitPdaLocationIsMix(skuCmd.getScanSkuQty(),invSkuCmd,containerId,pdaManMadePutawayCommand, invAttrMgmtHouse,warehouse,skuCmd,insideCommand,outCommand,madecsCmd);
               } else {
                   // 不允许混放
                   this.splitPdaLocationNotMix(skuCmd.getScanSkuQty(),invSkuCmd,containerId,warehouse,insideCommand,outCommand,pdaManMadePutawayCommand,skuCmd,madecsCmd);
               }
           }
       } else { // 不是静态库位
           if (mixStacking) { // 判断是否允许混放
               // 允许混放
               this.splitPdaLocationIsMix(skuCmd.getScanSkuQty(),invSkuCmd,containerId,pdaManMadePutawayCommand, invAttrMgmtHouse,warehouse,skuCmd,insideCommand,outCommand,madecsCmd);
           } else {
               // 不允许混放
               this.splitPdaLocationNotMix(skuCmd.getScanSkuQty(),invSkuCmd,containerId,warehouse,insideCommand,outCommand,pdaManMadePutawayCommand,skuCmd,madecsCmd);
           }
       }
       return pdaManMadePutawayCommand;
   }
   
   /**
    * 拆箱上架:库位不允许混放逻辑
    * 
    * @param command
    * @return
    */
   private void splitPdaLocationNotMix(Double scanSkuQty, WhSkuInventoryCommand invSkuCmd,Long contianerId,Warehouse warehouse,ContainerCommand insideCommand,ContainerCommand outerCommand,PdaManMadePutawayCommand command,WhSkuCommand skuCmd,ManMadeContainerStatisticCommand madecsCmd) {
       Long ouId = command.getOuId();
       Long skuId = skuCmd.getId();
       Long locationId = command.getLocationId();
       Long functionId = command.getFunctionId();
       Integer scanPattern = command.getScanPattern();
       Map<Long, Set<Long>> insideContainerSkuIds = madecsCmd.getInsideContainerIdSkuIds();
       Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = madecsCmd.getInsideContainerSkuIdsQty();
       Set<Long> insideContainerIds = madecsCmd.getInsideContainerIds();
       // 查询库位上已有商品
       WhSkuInventory inventory = new WhSkuInventory();
       inventory.setOuId(command.getOuId());
       inventory.setLocationId(command.getLocationId());
       List<WhSkuInventory> locSkuList = whSkuInventoryDao.findWhSkuInventoryByPramas(inventory);
       //判断库位上是否已存在sku商品
       boolean isExistSku = false;  //默认库位上没有要上架的sku商品
       for(WhSkuInventory skuInv:locSkuList) {
           if(skuId == skuInv.getSkuId()) {
               isExistSku = true;   //当前库位上存在要上架的sku商品
           }
       }
       List<WhSkuInventory> whskuList =  new ArrayList<WhSkuInventory>();
       if(isExistSku) {
        // 如果库位已存在库存商品，判断 ：托盘、货箱内的sku商品、库存属性是否和库位上的相同
           boolean IsAttConsistent = this.IsAttConsistent(locSkuList, whskuList);
           if (!IsAttConsistent) {
               throw new BusinessException(ErrorCodes.PDA_LOC_NO_MAX_SKUATT_NO_SAME);
           }
       }
       // 累加容器，容器内sku商品、库位上已有容器，商品重量， 判断是否<=库位承重 *
       this.IsLocationBearWeight(contianerId, locSkuList, whskuList, command);
       // 直接上架
      CheckScanSkuResultCommand csRcmd = pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayTipSkuOrContainer(outerCommand, insideCommand, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
      if(csRcmd.isNeedScanSku()) {  //容器内还有商品没有扫描完毕继续扫描
               command.setIsAfterPutawaySku(true);
               //直接上架
               whSkuInventoryManager.manMadePutaway(command.isNeedSkuDetail(),scanSkuQty,invSkuCmd,outerCommand, insideCommand, locationId, functionId, warehouse, command.getPutawayPatternDetailType(), ouId,command.getUserId(), logId);
               //清楚缓存
               pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayRemoveAllCache(outerCommand, insideCommand,false,false,logId,skuCmd.getId());
      }
      if(csRcmd.isNeedTipContainer()){  
               command.setIsNeedScanContainer(true);
               //直接上架
               whSkuInventoryManager.manMadePutaway(command.isNeedSkuDetail(),scanSkuQty,invSkuCmd,outerCommand, insideCommand, locationId, functionId, warehouse, command.getPutawayPatternDetailType(), ouId,command.getUserId(), logId);
               //清楚缓存
               pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayRemoveAllCache(outerCommand, insideCommand,true,false,logId,skuCmd.getId());
      }
      if(csRcmd.isPutaway()) {
               command.setPutway(true);
               //直接上架
               whSkuInventoryManager.manMadePutaway(command.isNeedSkuDetail(),scanSkuQty,invSkuCmd,outerCommand, insideCommand, locationId, functionId, warehouse, command.getPutawayPatternDetailType(), ouId,command.getUserId(), logId);
               //清楚缓存
               pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayRemoveAllCache(outerCommand, insideCommand,false,true,logId,skuCmd.getId());
       }
   }
   
   /**
    * 拆箱上架：库位允许混放
    * 
    * @param command
    * @return
    */
   private void splitPdaLocationIsMix(Double scanSkuQty, WhSkuInventoryCommand invSkuCmd,Long containerId,PdaManMadePutawayCommand command, String invAttrMgmtHouse,Warehouse warehouse,WhSkuCommand skuCmd,ContainerCommand insideCommand,ContainerCommand outerCommand,ManMadeContainerStatisticCommand madecsCmd) {
       Long ouId = command.getOuId();
       Long locationId = command.getLocationId();
       Integer scanPattern = command.getScanPattern();  //扫描方式
       Long functionId = command.getFunctionId();
       Map<Long, Set<Long>> insideContainerSkuIds = madecsCmd.getInsideContainerIdSkuIds();
       Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = madecsCmd.getInsideContainerSkuIdsQty();
       Set<Long> insideContainerIds = madecsCmd.getInsideContainerIds();
      
       
       List<WhSkuInventory> whskuList = new ArrayList<WhSkuInventory>();
       // 从缓存中取出容器内的商品(取出的商品全部是库存属性不同的商品)
       List<WhSkuInventory> list = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, command.getIsOuterContainer());
       for(WhSkuInventory skuInv:list){
           if(skuInv.getSkuId().equals(skuCmd.getId()) ) {
                   whskuList.add(skuInv);
           }
       }
       // 查询库位上已有商品
       WhSkuInventory inventory = new WhSkuInventory();
       inventory.setOuId(ouId);
       inventory.setLocationId(command.getLocationId());
       List<WhSkuInventory> locationSkuList = whSkuInventoryDao.findWhSkuInventoryByPramas(inventory);
       // 整箱,整托上架,容器内的sku和库位上的sku库存属性不同的个数
       this.verifyStoreOrWarehouse(ouId, command.getPutawayPatternDetailType(), invAttrMgmtHouse,whskuList, locationSkuList);
       //库位上已经的sku混放属性数
       Long locChaosSku = this.skuAttrCount(locationSkuList);
       Long chaosSku = 0L;//上架sku属性数
       Integer skuMixStacking = 1;   //拆箱上架sku种类数
       //上架sku属性数
       Long skuAttrCount = this.skuAttrCount(whskuList);
       if(WhScanPatternType.ONE_BY_ONE_SCAN  == scanPattern) {  //逐件扫描
           chaosSku = skuAttrCount;
       }
       if(WhScanPatternType.NUMBER_ONLY_SCAN  == scanPattern) {  //数量扫描
           chaosSku = skuAttrCount*Long.valueOf(scanSkuQty.toString());
       }
       // 查询库位
       Location location = whLocationDao.findByIdExt(locationId, ouId);
       if(null == location) {
           throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
       }
       Integer mixStackingNumber = location.getMixStackingNumber(); // 库位上最大混放种类数
       if(null == mixStackingNumber) {
           mixStackingNumber = 0;
       }
       Long maxChaosSku = location.getMaxChaosSku(); // 库位最大混放SKU属性数
       // 获取库位上已上架的sku种类数
       Integer locMixStacking = 0;
       if (null != locationSkuList && locationSkuList.size() != 0) {
           Long skuLocId = locationSkuList.get(0).getSkuId();
           for (int i = 1; i < locationSkuList.size(); i++) {
               Long tempId = locationSkuList.get(i).getSkuId();
               if (skuLocId != tempId) {
                   locMixStacking = locMixStacking + 1;
               }
           }
       }
       // 判断库位已有sku种类数+容器内SKU种类数是否<=最大混放SKU种类数 *
       if (mixStackingNumber < (skuMixStacking + locMixStacking)) {
           throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_VARIETY_OVER_MAX);
       }
       // 验证库位最大混放sku属性数,判断库位已有SKU属性数+容器内SKU属性数是否<=最大混放SKU属性数*
       if (maxChaosSku < (locChaosSku + chaosSku)) {
           throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_ATT_OVER_MAX);
       }
       // 累加容器、容器内SKU商品、库位上已有容器、商品的重量，判断是否<=库位承重*
       this.IsLocationBearWeight(containerId, locationSkuList, whskuList, command);
       String uuid = whskuList.get(0).getUuid();
       List<WhSkuInventorySnCommand> whskuInvSnList = whSkuInventorySnDao.findWhSkuInventoryByUuid(ouId,uuid);
       if(null == whskuInvSnList || whskuInvSnList.size() == 0) {  //上架的sku商品不存在sn/残次信息,直接上架
           CheckScanSkuResultCommand csRcmd = pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayTipSkuOrContainer(outerCommand, insideCommand, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
           if(csRcmd.isNeedScanSku()) {  //容器内还有商品没有扫描完毕继续扫描
               command.setIsAfterPutawaySku(true);
               whSkuInventoryManager.manMadePutaway(command.isNeedSkuDetail(),scanSkuQty,invSkuCmd,outerCommand, insideCommand, locationId, functionId, warehouse, command.getPutawayPatternDetailType(), ouId,command.getUserId(), logId);
               //清楚缓存
               pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayRemoveAllCache(outerCommand, insideCommand,false,false,logId,skuCmd.getId());
           }
           if(csRcmd.isNeedTipContainer()){  //
               command.setIsNeedScanContainer(true);
               whSkuInventoryManager.manMadePutaway(command.isNeedSkuDetail(),scanSkuQty,invSkuCmd,outerCommand, insideCommand, locationId, functionId, warehouse, command.getPutawayPatternDetailType(), ouId,command.getUserId(), logId);
               //清楚缓存
               pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayRemoveAllCache(outerCommand, insideCommand,true,false,logId,skuCmd.getId());
           }
           if(csRcmd.isPutaway()) {
               command.setPutway(true);
               //直接上架
               whSkuInventoryManager.manMadePutaway(command.isNeedSkuDetail(),scanSkuQty,invSkuCmd,outerCommand, insideCommand, locationId, functionId, warehouse, command.getPutawayPatternDetailType(), ouId,command.getUserId(), logId);
               //清楚缓存
               pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayRemoveAllCache(outerCommand, insideCommand,false,true,logId,skuCmd.getId());
           }
       }else{
           command.setScanSkuSnDefect(true);  //需要扫描商品的sn/残次信息
           //随机提示一个sku //判断是否要扫sn/残次信息
           WhSkuInventorySnCommand snCmd = whskuInvSnList.get(0);
           if(!StringUtils.isEmpty(snCmd.getSn())) {
               command.setNeedScanSkuSn(true);  //需要扫描sn
           }else{
               command.setNeedScanSkuSn(false);   
           }
           if(!StringUtils.isEmpty(snCmd.getDefectWareBarcode())) {
               command.setNeedScanSkuDefect(true);   //需要扫描残次信息
           }else{
               command.setNeedScanSkuDefect(false);
           }
            
       }
   }

   
   /***
    * 统计sku属性数
    * @param locationSkuList
    * @return
    */
   private Long skuAttrCount(List<WhSkuInventory> locationSkuList) {
       Long attrCount = 0L; //库存属性数
       for(WhSkuInventory skuInv:locationSkuList) {
           if(null != skuInv.getInvStatus()) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getInvType())) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getInvType())) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getBatchNumber())) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getMfgDate())) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getExpDate())) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getCountryOfOrigin())) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getInvAttr1())) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getInvAttr2())) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getInvAttr3())) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getInvAttr4())) {
               attrCount++;
           }
           if(!StringUtils.isEmpty(skuInv.getInvAttr5())) {
               attrCount++;
           }
           
       }
       return attrCount;
   }
   
   
   /***
    * 拆箱上架扫描SN/残次信息
    * @param pdaManMadePutawayCommand
    * @return
    */
   public PdaManMadePutawayCommand splitPdanScanSkuSn(PdaManMadePutawayCommand mPaCmd,Warehouse warehouse){
       Long ouId = mPaCmd.getOuId();
       String skuBarCode = mPaCmd.getSkuBarCode();   //sku商品条码
       String outerContainerCode = mPaCmd.getOuterContainerCode();
       String insideContainerCode = mPaCmd.getInsideContainerCode();
       Double scanQty = mPaCmd.getScanSkuQty();
       Integer scanPattern= mPaCmd.getScanPattern();
       Long containerId = null;
       Long locationId = mPaCmd.getLocationId();
       Long functionId = mPaCmd.getFunctionId();
       ContainerCommand insideCommand = null;
       if(!StringUtil.isEmpty(insideContainerCode)) {
           insideCommand = containerDao.getContainerByCode(insideContainerCode, ouId); // 根据内部容器编码查询内部容器
           if (null == insideCommand) {
               throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 内部容器不存在
           }
       }
       Long insideContainerId = insideCommand.getId();  //内部容器id
       ContainerCommand outCommand = null;
       if(!StringUtil.isEmpty(outerContainerCode)) {
           outCommand = containerDao.getContainerByCode(outerContainerCode, ouId); // 根据外部容器编码查询外部容器
           if (null == outCommand) {
               throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 内部容器不存在
           }
           containerId = outCommand.getId();
       }else{
           containerId = insideContainerId;
       }
       if (null == scanQty || scanQty.longValue() < 1) {
           log.error("scan sku qty is valid, logId is:[{}]", logId);
           throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
       }
       if (StringUtils.isEmpty(skuBarCode)) {
           log.error("sku is null error, logId is:[{}]", logId);
           throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
       }
//       WhSkuCommand skuCmd =  whSkuDao.findWhSkuByBarcodeExt(skuBarCode, ouId);
//       if(null == skuCmd){
//           throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);    //查看扫描的商品在商品表中是否存在
//       }
//       //判断sku商品的状态是否可用
//       if(Constants.LIFECYCLE_START != skuCmd.getLifecycle()) {
//           throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SKULIFRCYCLE_ERROR);    //查看扫描的商品在商品表中是否存在
//       }
       ManMadeContainerStatisticCommand madecsCmd = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC, containerId.toString());
       if (null == madecsCmd) {
           madecsCmd = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(mPaCmd, containerId);
       }
       Map<Long, Set<Long>> insideContainerSkuIds = madecsCmd.getInsideContainerIdSkuIds();
       Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = madecsCmd.getInsideContainerSkuIdsQty();
       Set<Long> insideContainerIds = madecsCmd.getInsideContainerIds();
       // 商品校验
       Long skuId = null;
       Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarCode, logId);   //获取对应的商品数量,key值是sku id
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
               log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarCode, logId);
               throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
           }
       }
       WhSkuCommand skuCmd = new WhSkuCommand();
       skuCmd.setId(skuId);
       skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
       List<WhSkuInventory> whskuInvList = new ArrayList<WhSkuInventory>();
       // 从缓存中取出容器内的商品
       List<WhSkuInventory> list = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId,mPaCmd.getIsOuterContainer());
       for(WhSkuInventory skuInv:list){
           if(skuInv.getSkuId().longValue() == skuCmd.getId().longValue() && ouId.longValue() == skuInv.getOuId().longValue()) {
               whskuInvList.add(skuInv);
           }
       }
       WhSkuInventory whskuInv = null;
       Boolean flag = false; 
       if(mPaCmd.isNeedSkuDetail()) {//相同su存在不同的库存属性
           for(WhSkuInventory whSkuInventory:whskuInvList) {
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanBatchNumber(),mPaCmd.getBatchNumber(), whSkuInventory.getBatchNumber());
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanOrigin(), mPaCmd.getSkuOrigin(), whSkuInventory.getCountryOfOrigin());
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanSkuInvType(), mPaCmd.getSkuInvType(), whSkuInventory.getInvType());
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanSkuInvStatus(),mPaCmd.getSkuInvStatus(), whSkuInventory.getInvStatus());
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanSkuMfgDate(), mPaCmd.getSkuMfgDate(), whSkuInventory.getMfgDate());
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanSkuExpDate(), mPaCmd.getSkuExpDate(), whSkuInventory.getExpDate());
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanSkuInvAttr1(), mPaCmd.getSkuInvAttr1(), whSkuInventory.getInvAttr1());
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanSkuInvAttr2(), mPaCmd.getSkuInvAttr2(), whSkuInventory.getInvAttr2());
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanSkuInvAttr3(), mPaCmd.getSkuInvAttr3(), whSkuInventory.getInvAttr3());
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanSkuInvAttr4(), mPaCmd.getSkuInvAttr4(), whSkuInventory.getInvAttr4());
               flag = this.compaterToInvAttr(mPaCmd.isNeedScanSkuInvAttr5(), mPaCmd.getSkuInvAttr5(), whSkuInventory.getInvAttr5());
               if(flag) {
                   whskuInv = whSkuInventory;
                   break;
               }
           }
       }else{//相同sku不存在不同库存属性数据
           whskuInv = whskuInvList.get(0);   //相同sku，不存在不同库存属性
       }
       WhSkuInventoryCommand invSkuCmd = new WhSkuInventoryCommand();
       invSkuCmd.setSkuId(skuId);
       List<InventoryStatus> listInventoryStatus = inventoryStatusManager.findAllInventoryStatus();
       String statusValue = mPaCmd.getSkuInvStatus();
       //库存状态
       if(!StringUtils.isEmpty(statusValue)) {
           for(InventoryStatus inventoryStatus:listInventoryStatus) {
                   if(statusValue.equals(inventoryStatus.getName()))
                       invSkuCmd.setInvStatus(inventoryStatus.getId());     //库存状态
                        break;
           }
       }
       invSkuCmd.setInvType(mPaCmd.getSkuInvType());
       invSkuCmd.setBatchNumber(mPaCmd.getBatchNumber());
       invSkuCmd.setMfgDate(mPaCmd.getSkuMfgDate());
       invSkuCmd.setExpDate(mPaCmd.getSkuExpDate());
       invSkuCmd.setCountryOfOrigin(mPaCmd.getSkuOrigin());
       invSkuCmd.setInvAttr1(mPaCmd.getSkuInvAttr1());
       invSkuCmd.setInvAttr2(mPaCmd.getSkuInvAttr2());
       invSkuCmd.setInvAttr3(mPaCmd.getSkuInvAttr3());
       invSkuCmd.setInvAttr4(mPaCmd.getSkuInvAttr4());
       invSkuCmd.setInvAttr5(mPaCmd.getSkuInvAttr5());
       if(WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {  //逐渐扫描
           //判断sn/残次信息是否存在,是否已经被扫描
           this.isScanSkuSnDefect(scanPattern, ouId, insideContainerId, skuId, mPaCmd.isNeedScanSkuDefect(), mPaCmd.isNeedScanSkuSn(), mPaCmd.getSkuSnCode(), whskuInv.getUuid(),mPaCmd.getSkuDefectCode());
       }
       if(WhScanPatternType.NUMBER_ONLY_SCAN == scanPattern) { //批量扫描
           //判断sn/残次信息是否存在,是否已经被扫描
           this.isScanSkuSnDefect(scanPattern, ouId, insideContainerId, skuId, mPaCmd.isNeedScanSkuDefect(), mPaCmd.isNeedScanSkuSn(), mPaCmd.getSkuSnCode(), whskuInv.getUuid(),mPaCmd.getSkuDefectCode());
           long cacheValue = cacheManager.incrBy(CacheConstants.PDA_MAN_MANDE_SCAN_SKU_QUEUE + insideContainerId.toString() + skuId.toString(), Constants.SN_DEFECR_COUNT);
           if(cacheValue < scanQty.longValue()) {  //还有sn/残次信息需要扫描
               mPaCmd.setScanSkuSnDefect(true);  //需要扫描商品的sku/残次信息
           }
           if(cacheValue > scanQty.longValue()) {
               throw new BusinessException(ErrorCodes.SCAN_SKU_SN_QTY_ERROR, new Object[] {cacheValue + scanQty.longValue()});
           }
           
       }
       //执行上架
       CheckScanSkuResultCommand csRcmd = pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayTipSkuOrContainer(outCommand, insideCommand, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
       if(csRcmd.isNeedScanSku()) {  //容器内还有商品没有扫描完毕继续扫描
           
           mPaCmd.setIsAfterPutawaySku(true);
           //直接上架
           whSkuInventoryManager.manMadePutaway(mPaCmd.isNeedSkuDetail(),skuCmd.getScanSkuQty(),invSkuCmd,outCommand, insideCommand, locationId, functionId, warehouse, mPaCmd.getPutawayPatternDetailType(), ouId,mPaCmd.getUserId(), logId);
           //清楚缓存
           pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayRemoveAllCache(outCommand, insideCommand,false,false,logId,skuId);
       }
       if(csRcmd.isNeedTipContainer()){  //
           mPaCmd.setIsNeedScanContainer(true);
           //直接上架
           whSkuInventoryManager.manMadePutaway(mPaCmd.isNeedSkuDetail(),skuCmd.getScanSkuQty(),invSkuCmd,outCommand, insideCommand, locationId, functionId, warehouse, mPaCmd.getPutawayPatternDetailType(), ouId,mPaCmd.getUserId(), logId);
           //清楚缓存
           pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayRemoveAllCache(outCommand, insideCommand,true,false,logId,skuId);
       }
       if(csRcmd.isPutaway()) {  //所有的已经扫描完毕
           mPaCmd.setPutway(true);
           //直接上架
           whSkuInventoryManager.manMadePutaway(mPaCmd.isNeedSkuDetail(),skuCmd.getScanSkuQty(),invSkuCmd, outCommand, insideCommand, locationId, functionId, warehouse, mPaCmd.getPutawayPatternDetailType(), ouId,mPaCmd.getUserId(), logId);
           //清楚缓存
           pdaManmadePutawayCacheManager.manMadeSplitContainerPutawayRemoveAllCache(outCommand, insideCommand,false,true,logId,skuId);
       }
       return mPaCmd;
   }

   /***
    * 判断sn/残次信息是否存在
    * @param scanPattern
    * @param ouId
    * @param insideContainerId
    * @param skuId
    * @param isNeedSkuDefect
    * @param isNeedScanSkuSn
    * @param skuSnCode
    * @param uuid
    * @param skuDefect
    */
   private void isScanSkuSnDefect(Integer scanPattern,Long ouId,Long insideContainerId,Long skuId,boolean isNeedSkuDefect,boolean isNeedScanSkuSn,String skuSnCode,String uuid,String skuDefect){
           //扫sn
           if(isNeedScanSkuSn) {
               WhSkuInventorySn whSkuInvSn =  null;
               whSkuInvSn = whSkuInventorySnDao.findWhSkuInventoryByUuidAndSnOrDefectWareBarcode(ouId, uuid, skuSnCode);
               if(null == whSkuInvSn) {
                   throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_NULL);
               }
               WhSkuInventorySn whSkuSn = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_SCAN_SKU_SN,insideContainerId.toString()+skuId.toString());
               // 判断此SN/残次条码是否本次扫过
               if (!StringUtil.isEmpty(whSkuInvSn.getUuid())) {
                     if (whSkuInvSn.getUuid().equals(whSkuSn.getUuid())) {
                        throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_DOUBLE_ERROR);
                     }
               }
               //缓存sn
               cacheManager.setMapObject(CacheConstants.PDA_MAN_MANDE_SCAN_SKU_SN,insideContainerId.toString()+skuId.toString(), whSkuSn, CacheConstants.CACHE_ONE_DAY);
           }
          //扫残次信息
          WhSkuInventorySn whSkuInvDefect =  null;
          if(isNeedSkuDefect) {
              whSkuInvDefect = whSkuInventorySnDao.findWhSkuInventoryByUuidAndSnOrDefectWareBarcode(ouId, uuid, skuDefect);
              if(null == whSkuInvDefect) {
                  throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_NULL);
              }
              WhSkuInventorySn whSkuDefect = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_SCAN_SKU_DEFECT,insideContainerId.toString()+skuId.toString());
              // 判断此SN/残次条码是否本次扫过
              if (!StringUtil.isEmpty(whSkuInvDefect.getUuid())) {
                  if (whSkuInvDefect.getUuid().equals(whSkuDefect.getUuid())) {
                     throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_DOUBLE_ERROR);
                  }
               } 
              //缓存残次信息
              cacheManager.setMapObject(CacheConstants.PDA_MAN_MANDE_SCAN_SKU_DEFECT,insideContainerId.toString()+skuId.toString(), whSkuDefect, CacheConstants.CACHE_ONE_DAY);
          }
   }
   

   /***
    * 拆箱上架:校验sku库存属性
    * @param pdaManMadePutawayCommand
    */
   public PdaManMadePutawayCommand verifySkuInventoryAttr(PdaManMadePutawayCommand command) {
       log.info("PdaManMadePutawayManagerImpl verifySkuInventoryAttr is start");
       if(null == command) {
           throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
       }
       Boolean sign = true;  //默认扫描的sku属性和要上架的sku属性相同
       String outerContainerCode = command.getOuterContainerCode();
       String insideContainerCode = command.getInsideContainerCode();
       Long skuId = command.getSkuId();
       Long ouId = command.getOuId();
       ContainerCommand insideCommand = null;
       Long containerId = null;
       if(!StringUtil.isEmpty(insideContainerCode)) {
           insideCommand = containerDao.getContainerByCode(insideContainerCode, ouId); // 根据内部容器编码查询内部容器
           if (null == insideCommand) {
               throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 内部容器不存在
           }
       }
       Long insideContainerId = insideCommand.getId();  //内部容器id
       ContainerCommand outCommand = null;
       if(!StringUtil.isEmpty(outerContainerCode)) {
           outCommand = containerDao.getContainerByCode(outerContainerCode, ouId); // 根据外部容器编码查询外部容器
           if (null == outCommand) {
               throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 内部容器不存在
           }
           containerId = outCommand.getId();
       }else{
           containerId = insideContainerId;
       }
//       String skuBarCode = command.getSkuBarCode();
//       WhSkuCommand skuCmd = whSkuDao.findWhSkuByBarcodeExt(skuBarCode, ouId); // 根据商品条码，查询商品信息
//       if (null == skuCmd) {
//           throw new BusinessException(ErrorCodes.SKU_NOT_FOUND); // 查看扫描的商品在商品表中是否存在
//       }
       List<WhSkuInventory> whskuList = new ArrayList<WhSkuInventory> ();
       // 从缓存中取出容器内的商品(取出的商品全部是库存属性不同的商品)
       List<WhSkuInventory> list = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, command.getIsOuterContainer());
       for(WhSkuInventory skuInv:list){
           if(skuInv.getSkuId() == skuId) {
                   whskuList.add(skuInv);
           }
       }
       for(WhSkuInventory whSku:whskuList) {
           sign = this.compaterToInvAttr(command.isNeedScanBatchNumber(),command.getBatchNumber(), whSku.getBatchNumber());
           sign = this.compaterToInvAttr(command.isNeedScanOrigin(), command.getSkuOrigin(), whSku.getCountryOfOrigin());
           sign = this.compaterToInvAttr(command.isNeedScanSkuInvType(), command.getSkuInvType(), whSku.getInvType());
           sign = this.compaterToInvAttr(command.isNeedScanSkuInvStatus(),command.getSkuInvStatus(), whSku.getInvStatus());
           sign = this.compaterToInvAttr(command.isNeedScanSkuMfgDate(), command.getSkuMfgDate(), whSku.getMfgDate());
           sign = this.compaterToInvAttr(command.isNeedScanSkuExpDate(), command.getSkuExpDate(), whSku.getExpDate());
           sign = this.compaterToInvAttr(command.isNeedScanSkuInvAttr1(), command.getSkuInvAttr1(), whSku.getInvAttr1());
           sign = this.compaterToInvAttr(command.isNeedScanSkuInvAttr2(), command.getSkuInvAttr2(), whSku.getInvAttr2());
           sign = this.compaterToInvAttr(command.isNeedScanSkuInvAttr3(), command.getSkuInvAttr3(), whSku.getInvAttr3());
           sign = this.compaterToInvAttr(command.isNeedScanSkuInvAttr4(), command.getSkuInvAttr4(), whSku.getInvAttr4());
           sign = this.compaterToInvAttr(command.isNeedScanSkuInvAttr5(), command.getSkuInvAttr5(), whSku.getInvAttr5());
           if(sign) {
               break;
           }
       }
       if(!sign) {
           throw new BusinessException(ErrorCodes.NO_FOUND_SKU_SAME_INV_ATTR);
       }
      
       log.info("PdaManMadePutawayManagerImpl verifySkuInventoryAttr is end");
       return command;
   }
   
   /***
    * 比较两个对象是否相同
    * @param isNeedScanInvAttr
    * @param scanInvAttr
    * @param skuInvAttr
    * @return
    */
   private Boolean compaterToInvAttr(Boolean isNeedScanInvAttr,Object scanInvAttr,Object skuInvAttr){
       Boolean result = true;   //默认相同
       if(isNeedScanInvAttr){  //需要扫描库存属性5
           if(null == scanInvAttr) {
               throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
           }
           if(!scanInvAttr.equals(skuInvAttr)) {  //不相同
               result = false;
           }else{
               result = true;   //相同
           }
       }
       
       return result;
   }
   /**
    * 计算商品多条码
    * @param skuBarCode
    * @param skuQty
    * @return
    */
   public Double  manMadeCalculateBarCode(String skuBarCode,Double skuQty,Long ouId,PdaManMadePutawayCommand manMadePutawayCommand){
       log.info("PdaManMadePutawayManagerImpl manMadeCalculateBarCode is start");
       Double result = null;
       String outerContainerCode = manMadePutawayCommand.getOuterContainerCode();
       String insideContainerCode = manMadePutawayCommand.getInsideContainerCode();
       ContainerCommand insideCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
       Long containerId = null;
       if(!StringUtils.isEmpty(outerContainerCode)) {
           ContainerCommand outerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
           containerId = outerCmd.getId();
       }else{
           containerId = insideCmd.getId();
       }
        ManMadeContainerStatisticCommand madecsCmd = cacheManager.getMapObject(CacheConstants.PDA_MAN_MANDE_CONTAINER_INVENTORY_STATISTIC, containerId.toString());
        if (null == madecsCmd) {
            madecsCmd = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(manMadePutawayCommand, containerId);
        }
        Map<Long, Set<Long>> insideContainerSkuIds = madecsCmd.getInsideContainerIdSkuIds();
        // 商品校验
        Long skuId = null;
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarCode, logId);   //获取对应的商品数量,key值是sku id
        Set<Long> icSkuIds = insideContainerSkuIds.get(insideCmd.getId());   //当前容器内所有sku id集合
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
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideCmd.getId(), insideCmd.getId(), skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideCmd.getCode()});
        }
        Integer skuBarQty = cacheSkuIdsQty.get(skuId);
        result = skuQty*Double.valueOf(skuBarQty.toString());
       log.info("PdaManMadePutawayManagerImpl manMadeCalculateBarCode is end");
       return result;
   }
   
}
