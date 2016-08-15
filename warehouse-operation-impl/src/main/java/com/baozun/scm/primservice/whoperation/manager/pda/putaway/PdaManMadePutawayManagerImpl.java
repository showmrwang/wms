package com.baozun.scm.primservice.whoperation.manager.pda.putaway;

import java.util.ArrayList;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.pda.putaway.PdaManMadePutawayCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionPutAwayDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
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
        // 判断托盘或货箱是否存在多个sku商品

        // 判断托盘/货箱是否存在相同sku，不同库存属性的商品

        // 如果库位已存在库存商品：判断托盘、货箱内的sku商品、库存属性是否和库位上的相同

        // 累加容器，容器内sku商品、库位上已有容器，商品重量， 判断是否<=库位承重 *

        // 判断整托整箱 *

        // 如果整托：判断托盘上的货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU;如果整箱：判断货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU*;


        return null;
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
        System.out.println("comging。。。。");

        // 判断店铺、仓库 是否配置了关键库存属性

        if (true) {
            // 判断库位上已由SKU商品，容器内所有SKU商品对应店铺/仓库配置的关键库存属性参数是否相同
        }

        // 判断库位最大混放SKU种类数

        // 判断库位已有sku种类数+容器内SKU种类数是否<=最大混放SKU种类数 *

        // 判断库位最大混放sku属性数

        // 判断库位已有SKU属性数+容器内SKU属性数是否<=最大混放SKU属性数*

        // 累加容器、容器内SKU商品、库位上已有容器、商品的重量，判断是否<=库位承重*

        // 判断整托、整箱*

        // 如果整托：判断托盘上的货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU;如果整箱：判断货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU*;



        return null;
    }


    
    
  
    
    
    
    
    
    
//**********************************************************************************************************   

    /**
     * 判断库位已有sku种类数+容器内SKU种类数是否<=最大混放SKU种类数*
     * 
     * @author lijun.shen
     * @return
     */
    private boolean accumulationSkuVariety() {
        return false;
    }

    /**
     * 判断库位已有SKU属性数+容器内SKU属性数是否<=最大混放SKU属性数*
     * 
     * @author lijun.shen
     * @return
     */
    private boolean accumulationSkuAtt() {
        return false;
    }


    /**
     * 累加容器，容器内sku商品、库位上已有容器，商品重量， 判断是否<=库位承重 *
     * 
     * @author lijun.shen
     * @return
     */
    private boolean accumulationWeigt() {
        return false;
    }

    /**
     * 如果整托：判断托盘上的货箱类型是否CASELEVEL,然后判断对应功能上的非CASELEVEL是否需要扫描SKU;如果整箱：判断货箱类型是否CASELEVEL,
     * 然后判断对应功能上的非CASELEVEL是否需要扫描SKU*;
     * 
     * @author lijun.shen
     * @return
     */
    private boolean isScanSku() {
        return false;
    }



}
