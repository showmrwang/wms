package com.baozun.scm.primservice.whoperation.manager.pda.putaway;

import java.util.ArrayList;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.putaway.PdaManMadePutawayCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionPutAwayDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaManmadePutawayCacheManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPutAway;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.StringUtil;



/**
 * PDA人为指定上架manager
 * 
 * @author shenlijun
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



    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaManMadePutawayCommand pdaScanContainer(PdaManMadePutawayCommand pdaManMadePutawayCommand) {

        // 验证容器号为空
        if (StringUtil.isEmpty(pdaManMadePutawayCommand.getContainerCode())) {
            log.warn("pdaScanContainer pdaInboundSortationCommand.getContainerCode() is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_CONTAINERCODE_NULL);
        }


        // 查询对应容器数据
        ContainerCommand container = containerDao.getContainerByCode(pdaManMadePutawayCommand.getContainerCode(), pdaManMadePutawayCommand.getOuId());
        if (null == container) {
            // 容器信息不存在
            log.warn("pdaScanContainer container is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_CONTAINER_NULL);
        }

        // 验证容器Lifecycle是否有效
        if (container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_FORBIDDEN)) {
            // 容器Lifecycle无效
            log.warn("pdaScanContainer container lifecycle error =" + container.getLifecycle() + " logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_LIFRCYCLE_ERROR);
        }


        // 如果容器Lifecycle为占用 判断容器状态是否为待上架/可用状态
        if (container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            if (!container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) && !container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE)) {
                log.warn("pdaScanContainer container status error =" + container.getStatus() + " logid: " + pdaManMadePutawayCommand.getLogId());
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_STATUS_ERROR, new Object[] {container.getStatus()});
            }
        }
        pdaManMadePutawayCommand.setContainerId(container.getId());// 保存容器ID


        // 验证容器是否存在库存记录
        List<String> containerList = new ArrayList<String>();
        containerList.add(pdaManMadePutawayCommand.getContainerCode());
        List<WhSkuInventoryCommand> invList = whSkuInventoryDao.findWhSkuInventoryByContainerCode(pdaManMadePutawayCommand.getOuId(), containerList);
        if (invList.size() == 0) {
            // 容器没有对应的库存信息
            log.warn("pdaScanContainer WhSkuInventory is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_CONTAINER_INV_NULL);
        }

        // 判断容器库存是外部容器库存还是内部容器库存
        int outerContainerCount = whSkuInventoryDao.findContainerInventoryCountsByOuterContainerId(pdaManMadePutawayCommand);
        int insideContainerCount = whSkuInventoryDao.findContainerInventoryCountsByInsideContainerId(pdaManMadePutawayCommand);
        if (0 < outerContainerCount) {
            // 外部容器库存
            pdaManMadePutawayCommand.setIsOutContainerInv(true);
        }
        if (0 < insideContainerCount) {
            // 内部容器库存
            pdaManMadePutawayCommand.setIsOutContainerInv(false);
        }

        // 将容器编码信息放入缓存
        cacheManager.setValue(CacheKeyConstant.CACHE_MMP_CONTAINER_ID_PREFIX + pdaManMadePutawayCommand.getContainerCode(), pdaManMadePutawayCommand.getUserId().toString(), CacheKeyConstant.CACHE_ONE_DAY);
        // 将sku信息放入缓存
        pdaManmadePutawayCacheManager.manMadePutawayCacheSku(pdaManMadePutawayCommand);

        return pdaManMadePutawayCommand;
    }


    /**
     * 验证货箱容器号
     * 
     * @author lijun.shen
     * @param pdaManMadePutawayCommand
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaManMadePutawayCommand pdaScanBinContainer(PdaManMadePutawayCommand pdaManMadePutawayCommand) {


        // 验证容器号是否为空
        if (StringUtil.isEmpty(pdaManMadePutawayCommand.getContainerCode())) {
            log.warn("pdaScanContainer pdaInboundSortationCommand.getContainerCode() is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_CONTAINERCODE_NULL);
        }


        // 查询对应容器数据
        ContainerCommand container = containerDao.getContainerByCode(pdaManMadePutawayCommand.getContainerCode(), pdaManMadePutawayCommand.getOuId());
        if (null == container) {
            // 容器信息不存在
            log.warn("pdaScanContainer container is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_CONTAINER_NULL);
        }

        // 验证容器Lifecycle是否有效
        if (container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_FORBIDDEN)) {
            // 容器Lifecycle无效
            log.warn("pdaScanContainer container lifecycle error =" + container.getLifecycle() + " logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_LIFRCYCLE_ERROR);
        }


        // 如果容器Lifecycle为占用 判断容器状态是否为待上架/可用状态
        if (container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            if (!container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) && !container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE)) {
                log.warn("pdaScanContainer container status error =" + container.getStatus() + " logid: " + pdaManMadePutawayCommand.getLogId());
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_STATUS_ERROR, new Object[] {container.getStatus()});
            }
        }
        pdaManMadePutawayCommand.setContainerId(container.getId());// 保存容器ID

        // 验证容器是否存在库存记录
        List<String> containerList = new ArrayList<String>();
        containerList.add(pdaManMadePutawayCommand.getContainerCode());
        List<WhSkuInventoryCommand> invList = whSkuInventoryDao.findWhSkuInventoryByContainerCode(pdaManMadePutawayCommand.getOuId(), containerList);
        if (invList.size() == 0) {
            // 容器没有对应的库存信息
            log.warn("pdaScanContainer WhSkuInventory is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_CONTAINER_INV_NULL);
        }
        return pdaManMadePutawayCommand;
    }


    /**
     * 验证库位号
     * 
     * @author lijun.shen
     * @param pdaManMadePutawayCommand
     * @return
     */

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaManMadePutawayCommand pdaScanLocation(PdaManMadePutawayCommand pdaManMadePutawayCommand) {

        // 验证库位号是否为空
        if (StringUtil.isEmpty(pdaManMadePutawayCommand.getBarCode())) {
            log.warn("pdaScanLocation pdaManMadePutawayCommand.getBarCode() is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_BARCODE_NULL);
        }
        // 查询对应库位信息
        Location location = whLocationDao.getLocationByBarcode(pdaManMadePutawayCommand.getBarCode());

        // 验证库位是否存在
        if (null == location) {
            log.warn("pdaScanLocation location is null logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
        }

        // 验证库位状态是否可用:lifecycle
        if (location.getLifecycle() != 1) {
            log.warn("pdaScanLocation lifecycle is error logid: " + pdaManMadePutawayCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_LIFECYCLE_ERROR);
        }
        // 验证库位是否静态库位
        if (location.getIsStatic()) {
            pdaManMadePutawayCommand.setIsStatic(true);
        }

        pdaManMadePutawayCommand.setLocationId(location.getId());
        return pdaManMadePutawayCommand;
    }



    /**
     * 库位不允许混放逻辑
     * 
     * @author lijun.shen
     * @param command
     * @return
     */

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaManMadePutawayCommand pdaLocationNotMix(PdaManMadePutawayCommand command) {
        System.out.println("coming....");


        // 判断托盘或货箱是否存在多个sku商品
        List<WhSkuInventory> mapObjectList = cacheManager.getMapObject(CacheKeyConstant.CACHE_MMP_CONTAINER_ID_PREFIX + command.getContainerId(), command.getUserId().toString());
        boolean hasMulitSku = this.haveMultiSku(mapObjectList);
        if (!hasMulitSku) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_NOT_MULTISKU);
        }

        // 查询库位上已有商品
        WhSkuInventory inventory = new WhSkuInventory();
        inventory.setOuId(command.getOuId());
        inventory.setLocationId(command.getLocationId());
        List<WhSkuInventory> LocationSkuList = whSkuInventoryDao.findWhSkuInventoryByPramas(inventory);

        // 判断托盘/货箱是否存在相同sku，不同库存属性的商品
        boolean haveDifferentAttibutes = this.haveDiffAtt(mapObjectList);
        if (!haveDifferentAttibutes) {
            throw new BusinessException(ErrorCodes.PDA_NOT_ALLOW_DIFFERENT_INVENTORY_ATTRIBUTES);
        }

        // 如果库位已存在库存商品，判断 ：托盘、货箱内的sku商品、库存属性是否和库位上的相同
        boolean IsAttConsistent = this.IsAttConsistent(LocationSkuList, mapObjectList);
        if (!IsAttConsistent) {
            throw new BusinessException(ErrorCodes.PDA_CONTAINER_SKUATT_NOTSAME_LOCATION_SKUATT);
        }

        // 累加容器，容器内sku商品、库位上已有容器，商品重量， 判断是否<=库位承重 *
        boolean isLocationBearWeight = this.IsLocationBearWeight(command.getContainerId(), LocationSkuList, mapObjectList);
        if (!isLocationBearWeight) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_UNBEAR_WEIGHT);
        }

        // 判断整托整箱
        WhFunctionPutAway whFunctionPutAway = whFunctionPutAwayDao.findListByParam(command.getFunctionId(), command.getOuId());
        if (whFunctionPutAway.getIsEntireTrayPutaway()) {
            // 如果整托：判断托盘上的货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU;如果整箱：判断货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU*;
        }

        if (whFunctionPutAway.getIsEntireBinPutaway()) {
            // 如果整箱：判断货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU和CASELEVEL是否需要扫描SKU
        }


        return command;
    }


    /**
     * 库位允许混放逻辑
     * 
     * @author lijun.shen
     * @param command
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaManMadePutawayCommand pdaLocationIsMix(PdaManMadePutawayCommand command) {
        System.out.println("comging....");

        // 从缓存中取出容器内的商品
        List<WhSkuInventory> mapObjectList = cacheManager.getMapObject(CacheKeyConstant.CACHE_MMP_CONTAINER_ID_PREFIX + command.getContainerId(), command.getUserId().toString());
        // 查询库位上已有商品
        WhSkuInventory inventory = new WhSkuInventory();
        inventory.setOuId(command.getOuId());
        inventory.setLocationId(command.getLocationId());
        List<WhSkuInventory> LocationSkuList = whSkuInventoryDao.findWhSkuInventoryByPramas(inventory);


        // 查询店铺、仓库 配置关键库存属性
        String isStoreAtt = this.VerifyStoreOrWarehouse(command.getContainerId());
        if (isStoreAtt != null) {
            // 判断库位上已有SKU商品，容器内所有SKU商品，对应店铺/仓库配置的关键库存属性参数是否相同
            boolean isAttMgmt = this.IsAttMgmtSame(isStoreAtt, LocationSkuList, mapObjectList);
            if (!isAttMgmt) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_ATTR_MGMT_NOT_EQUAL);
            }
        }

        // 查询库位最大混放SKU种类数
        Location location = new Location();
        location.setId(command.getLocationId());
        location.setOuId(command.getOuId());
        location = whLocationDao.findLocationByParam(location);

        // 判断库位已有sku种类数+容器内SKU种类数是否<=最大混放SKU种类数 *
        boolean skuVariety = this.accumulationSkuVariety(location.getMixStackingNumber(), mapObjectList, LocationSkuList);
        if (!skuVariety) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_VARIETY_OVER_MAX);
        }

        // 验证库位最大混放sku属性数,判断库位已有SKU属性数+容器内SKU属性数是否<=最大混放SKU属性数*
        boolean skuAtt = this.accumulationSkuAtt(location.getMaxChaosSku(), mapObjectList, LocationSkuList);
        if (!skuAtt) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_ATT_OVER_MAX);
        }

        // 累加容器、容器内SKU商品、库位上已有容器、商品的重量，判断是否<=库位承重*
        boolean isLocationBearWeight = this.IsLocationBearWeight(command.getContainerId(), LocationSkuList, mapObjectList);
        if (!isLocationBearWeight) {
            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_UNBEAR_WEIGHT);
        }

        // 判断整托、整箱*---先查询是整托还是整箱
        WhFunctionPutAway whFunctionPutAway = whFunctionPutAwayDao.findListByParam(command.getFunctionId(), command.getOuId());

        if (whFunctionPutAway.getIsEntireTrayPutaway()) {
            command.setIsEntireTrayPutaway(whFunctionPutAway.getIsEntireTrayPutaway());
            // 如果整托：判断托盘上的货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU;如果整箱：判断货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU*;
            boolean isNeedScanSku = this.IsEntireTrayNeedScanSku(whFunctionPutAway, command.getContainerId());
        }
        if (whFunctionPutAway.getIsEntireBinPutaway()) {
            command.setIsEntireBinPutaway(whFunctionPutAway.getIsEntireBinPutaway());
            // 如果整箱：判断货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU和CASELEVEL是否需要扫描SKU
            boolean isBinNeedScanSku = this.IsEntireBinNeedScanSku(whFunctionPutAway, command.getContainerId());
        }

        return command;
    }



    // **********************************************************************************************************************
    /**
     * 判断托盘或货箱是否存在多个sku商品
     * 
     * @author lijun.shen
     * @param mapObjectList
     * @return
     */
    private boolean haveMultiSku(List<WhSkuInventory> mapObjectList) {
        return false;
    }

    /**
     * 判断托盘/货箱是否存在相同sku，不同库存属性的商品
     * 
     * @author lijun.shen
     * @param mapObjectList
     * @return
     */
    private boolean haveDiffAtt(List<WhSkuInventory> mapObjectList) {
        List<WhSkuInventory> list = new ArrayList<>();
        for (WhSkuInventory whSkuInventory : mapObjectList) {
            if (list.size() == 0) {
                list.add(whSkuInventory);
            } else {
                if (list.contains(whSkuInventory)) {
                    list.add(whSkuInventory);
                }
            }
        }
        if (mapObjectList.size() == list.size()) {
            return true;
        }
        return false;
    }

    /**
     * 如果库位已存在库存商品，判断 ：托盘、货箱内的sku商品、库存属性是否和库位上的相同
     * 
     * @author lijun.shen
     * @param locationSkuList
     * @param mapObjectList
     * @return
     */
    private boolean IsAttConsistent(List<WhSkuInventory> locationSkuList, List<WhSkuInventory> mapObjectList) {
        return locationSkuList.containsAll(mapObjectList);
    }


    /**
     * 累加容器，容器内sku商品、库位上已有容器，商品重量， 判断是否<=库位承重 *
     * 
     * @author lijun.shen
     * @param containerId
     * @param locationSkuList
     * @param mapObjectList
     * @return
     */
    private boolean IsLocationBearWeight(Long containerId, List<WhSkuInventory> locationSkuList, List<WhSkuInventory> mapObjectList) {
        // 根据containerId 查询出容器重量


        // 库位上重量： 根据locationSkuList，查询出skuid

        // 根据skuid查询商品重量

        // 容器和容器内sku重量：根据mapObjectList，查询出skuid

        // 根据skuid查询商品重量

        return false;
    }



    /**
     * 查询仓库/店铺的关键属性
     * 
     * @author lijun.shen
     * @param containerId
     * @return
     */
    private String VerifyStoreOrWarehouse(Long containerId) {
        // 根据容器ID查询库存表(t_wh_sku_inventory)，获取占用码信息

        // 根据占用码查询ASN表(t_wh_asn 占用码就是asncode),获得店铺和仓库ID

        // 用店铺和仓库id分别取查询店铺表和仓库表，验是否有关键属性的配置

        // 如果店铺配置了 就依据店铺的来，店铺优先级大于仓库

        return null;
    }

    /**
     * 判断库位上已有SKU商品，容器内所有SKU商品，对应店铺/仓库配置的关键库存属性参数是否相同
     * 
     * @author lijun.shen
     * @param isStoreAtt
     * @param locationSkuList
     * @param mapObjectList
     * @return
     */
    private boolean IsAttMgmtSame(String isStoreAtt, List<WhSkuInventory> locationSkuList, List<WhSkuInventory> mapObjectList) {
        return false;
    }

    /**
     * 判断库位已有sku种类数+容器内SKU种类数是否<=最大混放SKU种类数 *
     * 
     * @author lijun.shen
     * @param mixStackingNumber
     * @param mapObjectList
     * @param locationSkuList
     * @return
     */
    private boolean accumulationSkuVariety(Integer mixStackingNumber, List<WhSkuInventory> mapObjectList, List<WhSkuInventory> locationSkuList) {
        return false;
    }


    /**
     * 判断库位已有SKU属性数+容器内SKU属性数是否<=最大混放SKU属性数*
     * 
     * @author lijun.shen
     * @param locationSkuList
     * @param mapObjectList
     * @param MaxChaosSku
     * @return
     */
    private boolean accumulationSkuAtt(Long MaxChaosSku, List<WhSkuInventory> mapObjectList, List<WhSkuInventory> locationSkuList) {
        return false;
    }


    /**
     * 如果整托：判断托盘上的货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU;如果整箱：判断货箱类型是否CASELEVEL,
     * 然后判断对应功能上的非CASELEVEL是否需要扫描SKU*;
     * 
     * @author lijun.shen
     * @param whFunctionPutAway
     * @param containerId
     * @return
     */
    private boolean IsEntireTrayNeedScanSku(WhFunctionPutAway whFunctionPutAway, Long containerId) {
        // 首先查出托盘上的货箱的id是多少,或有多个，循环每一个，只要有一个是需要扫描sku的则需要进入到扫描sku那支线
        WhSkuInventory whSkuInventory = new WhSkuInventory();
        whSkuInventory.setOuterContainerId(containerId);
        whSkuInventory.setOuId(whFunctionPutAway.getOuId());
        List<WhSkuInventory> skuInventoryList = whSkuInventoryDao.findSkuInventoryByOutContainerId(whSkuInventory);

        if (skuInventoryList.size() == 1){
            
        }


            // 查询货箱是否caselevel

            // 在whFunctionPutAway对象中找到对应的 是否需要扫sku的值

            return false;
    }

    /**
     * 如果整箱：判断货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU和CASELEVEL是否需要扫描SKU
     * 
     * @author lijun.shen
     * @param whFunctionPutAway
     * @param containerId
     * @return
     */
    private boolean IsEntireBinNeedScanSku(WhFunctionPutAway whFunctionPutAway, Long containerId) {
        // 查询货箱是否caselevel

        // 在whFunctionPutAway对象中找到对应的 是否需要扫sku的值
        return false;
    }

}
