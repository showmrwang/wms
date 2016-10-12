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
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
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
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaManmadePutawayCacheManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.TipSkuDetailProvider;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationInvVolumeWieghtManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionPutAwayManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPutAway;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
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
    private SkuMgmtDao skuMgmtDao;
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
        Boolean isTipContainerCode = pdaManMadePutawayCommand.getIsTipContainerCode() == null ? false: pdaManMadePutawayCommand.getIsTipContainerCode();
        if (isTipContainerCode) {
            // 扫描容器
            this.manMandeScanContainer(ouId, container, logId,userId);
            containerId = container.getId();
            pdaManMadePutawayCommand.setInsideContainerCode(pdaManMadePutawayCommand.getContainerCode());  //当前扫的是内部容器
            // 若为内部容器库存，则进入内部容器的相应判断
            WhFunctionPutAway whFunctionPutAway = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId);
            pdaManMadePutawayCommand.setScanPattern(whFunctionPutAway.getScanPattern());  //扫描方式
            if (whFunctionPutAway.getIsEntireBinPutaway()) { // 整箱上架
                pdaManMadePutawayCommand.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                // 判断托盘是否存在多个sku商品
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
            log.info("PdaManMadePutawayManagerImpl pdaScanContainer is end");
            return pdaManMadePutawayCommand;
        } else {
            // 扫描容器
            this.manMandeScanContainer(ouId, container, logId,userId);
            PdaManMadePutawayCommand manMandeCommand = this.judgeInventory(pdaManMadePutawayCommand, container);
            containerId = manMandeCommand.getContainerId();
            if (manMandeCommand.getIsOuterContainer()) {
                // 若为外部容器库存
                WhFunctionPutAway whFunctionPutAway = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId);
                pdaManMadePutawayCommand.setScanPattern(whFunctionPutAway.getScanPattern());  //扫描方式
                if (!whFunctionPutAway.getIsEntireTrayPutaway()) { // 不是整托上架,提示内部容器号,扫内部容器
                    pdaManMadePutawayCommand.setIsTipContainerCode(true);
                    return manMandeCommand;
                } else {// 整托上架
                    manMandeCommand.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                    // 判断托盘是否存在多个sku商品
                    List<WhSkuInventory> list = whSkuInventoryDao.findContainerInventoryCountsByOuterContainerId(containerId,ouId);
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
                    List<WhSkuInventory> list = whSkuInventoryDao.findContainerInventoryCountsByInsideContainerId(containerId,ouId);
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
            pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, manMandeCommand.getPutawayPatternDetailType());
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
                if(temp.getSkuId() != skuInv.getSkuId()) {
                    result = true;
                    break;
                }
            }
        }
        log.info("PdaManMadePutawayManagerImpl isSkuRepat is end");
        return result;
    }

    /**
     * 判断所有商品是否允许混放
     * 
     * @author
     * @param list
     * @return
     */
    private Boolean getIsMix(List<WhSkuInventory> list, Long ouId) {
        int mixCount = 0;
        for (int i = 0; i < list.size(); i++) {
            Long skuId = list.get(i).getSkuId();
            SkuMgmt skuMgmt = skuMgmtDao.findByIdShared(skuId, ouId);
            if (!skuMgmt.getIsMixAllowed()) {
                mixCount++;
            }
        }
        if (mixCount > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 扫描容器
     * 
     * @param pdaManMadePutawayCommand
     * @return
     */
    private void manMandeScanContainer(Long ouId, ContainerCommand container, String logId,Long userId) {
        log.info("PdaManMadePutawayManagerImpl containerCacheStatistic is start");
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
        String containerCode = container.getCode();
        // 验证容器是否存在库存记录
        List<String> containerList = new ArrayList<String>();
        containerList.add(containerCode);
        List<WhSkuInventoryCommand> invList = whSkuInventoryDao.findWhSkuInventoryByContainerCode(ouId, containerList);
        if (invList.size() == 0) {
            // 容器没有对应的库存信息
            log.error("pdaScanContainer WhSkuInventory is null logid: " + logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR);
        }
        log.info("PdaManMadePutawayManagerImpl containerCacheStatistic is end");
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
        }
        if (0 < outerContainerCount) {
            pdaManMadePutawayCommand.setOuterContainerCode(containerCode);  //当前扫的是外部容器
            List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.getSkuInvListByOutContainerID(ouId,containerId);
            WhSkuInventoryCommand skuInvCmd = skuInvList.get(0);
            if(null == skuInvCmd.getInsideContainerId()) { //内部容器库存
                // 内部容器库存
                pdaManMadePutawayCommand.setIsOuterContainer(false);
                pdaManMadePutawayCommand.setInsideContainerCode(containerCode);
            }else{
                 // 外部容器库存
                pdaManMadePutawayCommand.setIsOuterContainer(true);
                pdaManMadePutawayCommand.setInsideContainerCode(null);
                // 若不是整托上架，跳转到扫描货箱容器页面，页面提示扫描货箱容器号
                if(WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                    ManMadeContainerStatisticCommand command = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(pdaManMadePutawayCommand, containerId);
                    Set<Long> insideContainerIds = command.getInsideContainerIds();
                    Long tipContainerId = pdaManmadePutawayCacheManager.outContainerInvPutawayTipContainer(container, insideContainerIds, logId);
                    Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
                    if (null == tipContainer) {
                        log.error("container is null error, logId is:[{}]", logId);
                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
                    }
                    String tipContainerCode = tipContainer.getCode();
                    pdaManMadePutawayCommand.setTipContainerCode(tipContainerCode); // 提示货箱号
                    pdaManMadePutawayCommand.setIsTipContainerCode(true);
               }
                pdaManMadePutawayCommand.setIsTipContainerCode(false);   //不需要提示货箱号
            }
        }else {
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
        pdaManMadePutawayCommand.setContainerId(containerId);  //外部库存时,为托盘id,内部库存时，为货箱id
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
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 外部容器不存在
            }
        }
        
        ContainerCommand outCommand = null;
        if(!StringUtil.isEmpty(outerContainerCode)) {
            outCommand = containerDao.getContainerByCode(outerContainerCode, ouId); // 根据外部容器编码查询外部容器
            if (null == outCommand) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 外部容器不存在
            }
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
            List<WhSkuInventory> whskuList = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(pdaManMadePutawayCommand.getContainerId(), ouId, pdaManMadePutawayCommand.getPutawayPatternDetailType());
            // 验证库位是否绑定了容器内的所有商品
            boolean result = true;
            for (WhSkuInventory skuInventory : whskuList) {
                Long skuId = skuInventory.getSkuId();
                for (WhSkuLocationCommand skuLoc : listCommand) {
                    Long sId = skuLoc.getSkuId();
                    if (skuId == sId) {
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
                    this.pdaLocationIsMix(pdaManMadePutawayCommand, invAttrMgmtHouse);
                } else {
                    // 不允许混放
                    this.pdaLocationNotMix(pdaManMadePutawayCommand);
                }
            }
        } else { // 不是静态库位
            if (mixStacking) { // 判断是否允许混放
                // 允许混放
                this.pdaLocationIsMix(pdaManMadePutawayCommand, invAttrMgmtHouse);
            } else {
                // 不允许混放
                this.pdaLocationNotMix(pdaManMadePutawayCommand);
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
                whSkuInventoryManager.manMadePutaway(outCommand, insideCommand, locationId, functionId, warehouse, putawayPatternDetailType, ouId,pdaManMadePutawayCommand.getUserId(), logId);
                // 清楚缓存
                pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
            }
            //整箱上架
            if( WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
                pdaManMadePutawayCommand.setPutway(true); // 上架
                // 执行上架方法
                whSkuInventoryManager.manMadePutaway(outCommand, insideCommand, locationId, functionId, warehouse, putawayPatternDetailType, ouId,pdaManMadePutawayCommand.getUserId(), logId);
                // 清楚缓存
                if(pdaManMadePutawayCommand.getIsOuterContainer()) {
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand, insideCommand, true, insideContainerCode);
                }else{
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand, insideCommand, false, insideContainerCode);
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
    private void pdaLocationNotMix(PdaManMadePutawayCommand command) {
        Long containerId = command.getContainerId();
        Long ouId = command.getOuId();
        // 判断托盘或货箱是否存在多个sku商品
        List<WhSkuInventory> whskuList = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, command.getPutawayPatternDetailType());
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
        this.IsLocationBearWeight(command.getContainerId(), LocationSkuList, whskuList, command.getPutawayPatternDetailType(), command.getLocationId(), ouId);
    }


    /**
     * 库位允许混放逻辑
     * 
     * @param command
     * @return
     */
    private void pdaLocationIsMix(PdaManMadePutawayCommand command, String invAttrMgmtHouse) {
        Long containerId = command.getContainerId();
        Long ouId = command.getOuId();
        Long locationId = command.getLocationId();
        // 从缓存中取出容器内的商品
        List<WhSkuInventory> whskuList = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, command.getPutawayPatternDetailType());
        // 查询库位上已有商品
        WhSkuInventory inventory = new WhSkuInventory();
        inventory.setOuId(ouId);
        inventory.setLocationId(command.getLocationId());
        List<WhSkuInventory> locationSkuList = whSkuInventoryDao.findWhSkuInventoryByPramas(inventory);
        // 整箱,整托上架,容器内的sku和库位上的sku库存属性不同的个数
        Long chaosSku = this.verifyStoreOrWarehouse(containerId, ouId, command.getPutawayPatternDetailType(), invAttrMgmtHouse,whskuList, locationSkuList);
        //库位上已经的sku混放属性数
        Long locChaosSku = this.isAttMgmtSame(locationSkuList, locationSkuList);
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
        if (maxChaosSku < (locChaosSku + chaosSku)) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_ATT_OVER_MAX);
        }
        // 累加容器、容器内SKU商品、库位上已有容器、商品的重量，判断是否<=库位承重*
        this.IsLocationBearWeight(containerId, locationSkuList, whskuList, command.getPutawayPatternDetailType(),locationId, ouId);
    }



    /**
     * 判断托盘或货箱是否存在多个sku商品
     * 
     * @author lijun.shen
     * @param mapObjectList
     * @return
     */
    private boolean isNoContainerMultiSku(List<WhSkuInventory> whSkuInventoryList) {
        log.info("PdaManMadePutwayManagerImpl isNoContainerMultiSku is start");
        Boolean result = false; // 默认托盘或者货箱存在多个sku
        Long tempCount = whSkuInventoryList.get(0).getId();
        for (int i = 1; i < whSkuInventoryList.size(); i++) {
            WhSkuInventory skuInventory = whSkuInventoryList.get(i);
            Long skuId = skuInventory.getId();
            if (skuId == tempCount) {
                tempCount = skuId;
            } else {
                result = true;
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
            if (!skuInventory.getInvStatus().equals(skuInv.getInvStatus())) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (!skuInventory.getInvType().equals(skuInv.getInvType())) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (!skuInventory.getBatchNumber().equals(skuInv.getBatchNumber())) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (!skuInventory.getMfgDate().equals(skuInv.getMfgDate())) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (!skuInventory.getExpDate().equals(skuInv.getExpDate())) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (!skuInventory.getCountryOfOrigin().equals(skuInv.getCountryOfOrigin())) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (!skuInventory.getInvAttr1().equals(skuInv.getInvAttr1())) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (!skuInventory.getInvAttr2().equals(skuInv.getInvAttr2())) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (!skuInventory.getInvAttr3().equals(skuInv.getInvAttr3())) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (!skuInventory.getInvAttr4().equals(skuInv.getInvAttr4())) {
                result = false;
                skuInventory = skuInv;
                break;
            }
            if (!skuInventory.getInvAttr5().equals(skuInv.getInvAttr5())) {
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
                if (!skuInv.getInvStatus().equals(inventory.getInvStatus())) {
                    result = false;
                    break;
                }
                if (!skuInv.getInvType().equals(inventory.getInvType())) {
                    result = false;
                    break;
                }
                if (!skuInv.getBatchNumber().equals(inventory.getBatchNumber())) {
                    result = false;
                    break;
                }
                if (!skuInv.getMfgDate().equals(inventory.getMfgDate())) {
                    result = false;
                    break;
                }
                if (!skuInv.getExpDate().equals(inventory.getExpDate())) {
                    result = false;
                    break;
                }
                if (!skuInv.getCountryOfOrigin().equals(inventory.getCountryOfOrigin())) {
                    result = false;
                    break;
                }
                if (!skuInv.getInvAttr1().equals(inventory.getInvAttr1())) {
                    result = false;
                    break;
                }
                if (!skuInv.getInvAttr2().equals(inventory.getInvAttr2())) {
                    result = false;
                    break;
                }
                if (!skuInv.getInvAttr3().equals(inventory.getInvAttr3())) {
                    result = false;
                    break;
                }
                if (!skuInv.getInvAttr4().equals(inventory.getInvAttr4())) {
                    result = false;
                    break;
                }
                if (!skuInv.getInvAttr5().equals(inventory.getInvAttr5())) {
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
    private void IsLocationBearWeight(Long containerId, List<WhSkuInventory> locationSkuList, List<WhSkuInventory> whSkuInventoryList, int putawayPatternDetailType, Long locationId, Long ouId) {
        log.info("PdaManMadePutwayManagerImpl IsLocationBearWeight start");
        ManMadeContainerStatisticCommand madeContainer = cacheManager.getMapObject(CacheKeyConstant.MANMADE_PUTWAY_CACHE_CONTAINER, containerId.toString());
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
            Double putwayWeight = madeContainer.getInsideContainerWeight();
            Long insideContainerId = madeContainer.getInsideContainerId();
            Map<Long, Double> insideContainersWeight = madeContainer.getInsideContainersWeight();
            // 计算要上架商品的重量
            putwayWeight = putwayWeight + insideContainersWeight.get(insideContainerId);
            // 库位已有商品重量加要上架货物的重量
            Double sum = livwWeight + putwayWeight;
            if (locWeight > sum) {
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
    private Long verifyStoreOrWarehouse(Long containerId, Long ouId, int putawayPatternDetailType, String invAttrMgmtHouse, List<WhSkuInventory> whskuList, List<WhSkuInventory> locationSkuList) {
        log.info("PdaManMadePutwayManagerImpl verifyStoreOrWarehouse start");
        Long result = 0L;  
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
                result = this.attMgmt(warehouseAttr, whskuList, locationSkuList);
            }
        } else {
            String[] storeAttr = invAttrMgmt.split(","); // 拆分店铺的库存属性
            result = this.attMgmt(storeAttr, whskuList, locationSkuList);
        }
        log.info("PdaManMadePutwayManagerImpl verifyStoreOrWarehouse end");
        return result;
    }

    
    private Long attMgmt(String[] invAttrMgmt,List<WhSkuInventory> whskuList, List<WhSkuInventory> locationSkuList){
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
        return result;
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
                    Long invType = this.compatorTo(cSkuInv.getInvType(), locSkuInv.getInvType());
                    if(invType != 0) {
                        count = count+ invType;
                    }
                    Long invStatus = this.compatorTo(cSkuInv.getInvStatus(), locSkuInv.getInvStatus());
                    if(invStatus != 0) {
                        count = count+ invStatus;
                    }
                    Long batchNumber = this.compatorTo(cSkuInv.getBatchNumber(), locSkuInv.getBatchNumber());
                    if(batchNumber != 0) {
                        count = count+ batchNumber;
                    }
                    Long mfgDate = this.compatorTo(cSkuInv.getMfgDate(), locSkuInv.getMfgDate());
                    if(mfgDate != 0) {
                        count = count+ mfgDate;
                    }
                    Long expDate = this.compatorTo(cSkuInv.getExpDate(), locSkuInv.getExpDate());
                    if(expDate != 0) {
                        count = count+ expDate;
                    }
                    Long countryOfOrigin = this.compatorTo(cSkuInv.getCountryOfOrigin(), locSkuInv.getCountryOfOrigin());
                    if(countryOfOrigin != 0) {
                        count = count+ countryOfOrigin;
                    }
                    Long invAttr1 = this.compatorTo(cSkuInv.getInvAttr1(), locSkuInv.getInvAttr1());
                    if(invAttr1 != 0) {
                        count = count+ invAttr1;
                    }
                    Long invAttr2 = this.compatorTo(cSkuInv.getInvAttr2(), locSkuInv.getInvAttr2());
                    if(invAttr2 != 0) {
                        count = count+ invAttr2;
                    }
                    Long invAttr3 = this.compatorTo(cSkuInv.getInvAttr3(), locSkuInv.getInvAttr3());
                    if(invAttr3 != 0) {
                        count = count+ invAttr3;
                    }
                    Long invAttr4 = this.compatorTo(cSkuInv.getInvAttr4(), locSkuInv.getInvAttr4());
                    if(invAttr4 != 0) {
                        count = count+ invAttr4;
                    }
                    Long invAttr5 = this.compatorTo(cSkuInv.getInvAttr5(), locSkuInv.getInvAttr5());
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
    private Long compatorTo(Object cSkuInvAttr,Object locSkuInvAttr){
        Long count = 0L;
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
        Long outerContainerId = pdaManMadePutawayCommand.getContainerId(); // 外部容器id
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
        ManMadeContainerStatisticCommand madeContainer = cacheManager.getMapObject(CacheKeyConstant.MANMADE_PUTWAY_CACHE_CONTAINER, outerContainerId.toString());
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
        TipContainerCacheCommand tipCmd = new TipContainerCacheCommand();
        tipCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);  //整托上架
        tipCmd.setOuterContainerId(outerContainerId);
        tipCmd.setOuterContainerCode(pdaManMadePutawayCommand.getOuterContainerCode());
        ArrayDeque<Long> icIds = new ArrayDeque<Long>();
        icIds.addFirst(insideContainerId);
        tipCmd.setTipInsideContainerIds(icIds);
        cacheManager.setObject(CacheConstants.SCAN_CONTAINER_QUEUE + outerContainerId.toString(), tipCmd, CacheConstants.CACHE_ONE_DAY);
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
                whSkuInventoryManager.manMadePutaway(outCommand, insideContainer, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, pdaManMadePutawayCommand.getUserId(), logId);
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
        WhSkuCommand whSkuCommand = whSkuDao.findWhSkuByBarcodeExt(barCode, ouId); // 根据商品条码，查询商品信息
        if (null == whSkuCommand) {
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND); // 查看扫描的商品在商品表中是否存在
        } else {
            BeanUtils.copyProperties(whSkuCommand, skuCmd);
            whSkuCommand.setScanSkuQty(scanQty);
        }
        if (Constants.LIFECYCLE_START != whSkuCommand.getLifecycle()) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SKULIFRCYCLE_ERROR); // 查看扫描的商品在商品表中是否存在
        }
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
        whSkuCommand.setId(skuId);
        whSkuCommand.setScanSkuQty(scanQty * cacheSkuQty);// 可能是多条码
        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
            // 全部货箱扫描
            CheckScanSkuResultCommand cssrCmd = pdaManmadePutawayCacheManager.manMadePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, insideContainerIds, insideContainerIdSkuIds, insideContainerSkuIdsQty, whSkuCommand, scanPattern, logId);
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
                whSkuInventoryManager.manMadePutaway(outCommand, insideCommand, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, userId, skuBarcode);
                // 清楚缓存
                pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
            }
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            // 只扫caselevel货箱
            if (null == caselevelContainerIds || 0 == caselevelContainerIds.size()) {
                // 无caselevel货箱，直接上架
                resultCommand.setPutway(true);
                // 上架,清楚缓存
                whSkuInventoryManager.manMadePutaway(outCommand, insideCommand, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, userId, skuBarcode);
                // 清楚缓存
                pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
            } else {
                CheckScanSkuResultCommand cssrCmd =
                        pdaManmadePutawayCacheManager.manMadePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, insideContainerIds, insideContainerIdSkuIds, insideContainerSkuIdsQty, whSkuCommand, scanPattern, logId);
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
                    whSkuInventoryManager.manMadePutaway(outCommand, insideCommand, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, userId, skuBarcode);
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
                whSkuInventoryManager.manMadePutaway(outCommand, insideCommand, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, userId, skuBarcode);
                // 清楚缓存
                pdaManmadePutawayCacheManager.manMadePalletPutawayRemoveAllCache(outCommand, logId);
            } else {
                CheckScanSkuResultCommand cssrCmd =
                        pdaManmadePutawayCacheManager.manMadePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, insideContainerIds, insideContainerIdSkuIds, insideContainerSkuIdsQty, whSkuCommand, scanPattern, logId);
                if (cssrCmd.isNeedScanSku()) {
                    resultCommand.setIsNeedScanSku(true); // 商品没有扫描完毕
                } else if (cssrCmd.isNeedTipContainer()) {
                    resultCommand.setIsNeedScanContainer(true); // 内不容器没有扫描好
                } else {
                    resultCommand.setPutway(true);
                    // 上架,清楚缓存
                    whSkuInventoryManager.manMadePutaway(outCommand, insideCommand, locationId, functionId, warehouse, pdaManMadePutawayCommand.getPutawayPatternDetailType(), ouId, userId, skuBarcode);
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
        ContainerCommand outCommand = null;
        if(!StringUtils.isEmpty(outerContainerCode)) {
            outCommand = containerDao.getContainerByCode(outerContainerCode, ouId);  //根据容器编码查询外部容器
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
        ManMadeContainerStatisticCommand madecsCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerCmd.getId().toString());
        if (null == madecsCmd) {
            madecsCmd = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(pdaManMadePutawayCommand, insideContainerId);
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
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId,  logId);
                //清除缓存
                pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd, false, logId);
            }else {
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                //清除缓存
                pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd, true, logId);
            }
        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
            int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,true);
            if(count < 1) {  //此货箱不是caselevel货箱,直接上架
                //判断托盘上还有没有没上架的货箱
                whSkuInventoryManager.manMadePutaway(null,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
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
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId,logId);
                pdaManMadePutawayCommand.setIsAfterPutawayTipContianer(true);
                //清除缓存
                pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,false, logId);
            } else {
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId,logId);
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
                whSkuInventoryManager.manMadePutaway(outCommand,insideContainerCmd,locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
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
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                if(pdaManMadePutawayCommand.getIsOuterContainer()) {  //外部库存,整箱上架
                    //清除缓存
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,false, logId);
                }else{  //内部容器库存,整箱上架
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,true, logId);
                }
            } else {
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
                if(pdaManMadePutawayCommand.getIsOuterContainer()) {  //外部库存,整箱上架
                    //清除缓存
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,false, logId);
                }else{  //内部容器库存,整箱上架
                    pdaManmadePutawayCacheManager.manMadeContainerPutawayRemoveAllCache(outCommand,insideContainerCmd,true, logId);
                }
                
            }
        }else if (false == isCaselevelScanSku && false == isNotcaselevelScanSku){
                pdaManMadePutawayCommand.setPutway(true);
                whSkuInventoryManager.manMadePutaway(outCommand,insideContainerCmd, locationId, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
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
        Long  functionId = pdaManMadePutawayCommand.getFunctionId();
        String insideContainerCode = pdaManMadePutawayCommand.getInsideContainerCode();
        Long containerId = pdaManMadePutawayCommand.getContainerId();
        Integer putawayPatternDetailType = pdaManMadePutawayCommand.getPutawayPatternDetailType();
        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
        if(null == insideContainerCmd) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //容器不存在
        }
        //获取内部容器id
        Long insideContainerId = insideContainerCmd.getId();
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
        //判断sku商品的状态是否可用
        if(Constants.LIFECYCLE_START != whSkuCommand.getLifecycle()) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SKULIFRCYCLE_ERROR);    //查看扫描的商品在商品表中是否存在
        }
        ManMadeContainerStatisticCommand madecsCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerCmd.getId().toString());
        if (null == madecsCmd) {
            madecsCmd = pdaManmadePutawayCacheManager.manMadePutawayCacheContainer(pdaManMadePutawayCommand, insideContainerId);
        }
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
        //判断相同的sku是否存在不同的库存属性
        List<WhSkuInventory> whSkuInventoryList = pdaManmadePutawayCacheManager.manMadePutwayCacheSkuInventory(containerId, ouId, putawayPatternDetailType);
        Boolean isSkuDiffAtt = this.isNoSkuDiffAtt(whSkuInventoryList);
        if(!isSkuDiffAtt) { //相同的sku,存在不同的库存属性,进入扫sku库存属性页面
            pdaManMadePutawayCommand.setNeedTipSkuDetail(true);
        }else{//相同的sku,不存在不同的库存属性,进入扫库位页面
            pdaManMadePutawayCommand.setNeedTipSkuDetail(false);
        }
        pdaManMadePutawayCommand.setBarCode(barCode);
        log.info("PdaManMadePutwayManagerImpl spiltContainerPutwayScanSku end");
        return pdaManMadePutawayCommand;
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
               throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 外部容器不存在
           }
       }
       
       ContainerCommand outCommand = null;
       if(!StringUtil.isEmpty(outerContainerCode)) {
           outCommand = containerDao.getContainerByCode(outerContainerCode, ouId); // 根据外部容器编码查询外部容器
           if (null == outCommand) {
               throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 外部容器不存在
           }
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
           //扫描的sku商品
           WhSkuCommand whSkuCommand =  whSkuDao.findWhSkuByBarcodeExt(barCode, ouId);
           if(null == whSkuCommand){
               throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);    //查看扫描的商品在商品表中是否存在
           }
           // 验证库位是否绑定了对应的商品
           boolean result = true;  //默认绑定扫描的sku商品
           Long skuId = whSkuCommand.getId();
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
                   this.pdaLocationIsMix(pdaManMadePutawayCommand, invAttrMgmtHouse);
               } else {
                   // 不允许混放
                   this.pdaLocationNotMix(pdaManMadePutawayCommand);
               }
           }
       } else { // 不是静态库位
           if (mixStacking) { // 判断是否允许混放
               // 允许混放
               this.pdaLocationIsMix(pdaManMadePutawayCommand, invAttrMgmtHouse);
           } else {
               // 不允许混放
               this.pdaLocationNotMix(pdaManMadePutawayCommand);
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
          
       }
       return pdaManMadePutawayCommand;
   }

    /***
     * 
     * @param srCmd
     * @param tipSkuAttrId
     * @param locSkuAttrIds
     * @param skuAttrIdsQty
     * @param logId
     */
    private void tipSkuDetailAspect(PdaManMadePutawayCommand srCmd, String tipSkuAttrId, Map<Long, Set<String>> locSkuAttrIds, Map<String, Long> skuAttrIdsQty, String logId) {
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

}
