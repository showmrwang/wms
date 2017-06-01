/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.checking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.print.command.PrintDataCommand;
import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingPrint;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDeliveryInfoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOdoPackageInfoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundConsumableDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhPrintInfoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionOutBoundManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSkuManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOdoPackageInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundConsumable;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhPrintInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;

@Service("checkingManager")
@Transactional
public class CheckingManagerImpl extends BaseManagerImpl implements CheckingManager {

    public static final Logger log = LoggerFactory.getLogger(CheckingManagerImpl.class);

    @Autowired
    private PrintObjectManagerProxy printObjectManagerProxy;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhSkuManager whSkuManager;
    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;
    @Autowired
    private WhCheckingDao whCheckingDao;
    @Autowired
    private WhSkuInventoryDao skuInventoryDao;
    @Autowired
    private WhCheckingCollectionDao checkingCollectionDao;
    @Autowired
    private WhCheckingLineDao whCheckingLineDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhOutboundboxDao whOutboundboxDao;
    @Autowired
    private WhOutboundboxLineDao whOutboundboxLineDao;
    @Autowired
    private WhSkuInventoryLogDao whSkuInventoryLogDao;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private WhOdoPackageInfoDao whOdoPackageInfoDao;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private WhOutboundConsumableDao whOutboundConsumableDao;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private WhFunctionOutBoundManager whFunctionOutBoundManager;
    @Autowired
    private WhPrintInfoDao whPrintInfoDao;
    @Autowired
    private WhOdoDeliveryInfoDao whOdoDeliveryInfoDao;


    @Override
    public void printPackingList(List<Long> facilityIdsList, Long userId, Long ouId) {
        // 打印装箱清单
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_16, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printSalesList(List<Long> facilityIdsList, Long userId, Long ouId) {
        // 打印销售清单
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setOdoId(facilityIdsList.get(0));
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_13, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printSinglePlane(String outBoundBoxCode, String waybillCode, Long userId, Long ouId, Long odoId) {
        // 打印面单
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setCode(waybillCode);
            printDataCommand.setOutBoundBoxCode(outBoundBoxCode);
            printDataCommand.setOdoId(odoId);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_15, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBoxLabel(String outBoundBoxCode, Long userId, Long ouId, Long odoId) {
        // 打印箱标签
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setCode(outBoundBoxCode);
            printDataCommand.setOdoId(odoId);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_1, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printInvoiceReview(List<Long> facilityIdsList, Long userId, Long ouId) {
        // 打印发票（复核）
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_14, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCheckingCommand> findCheckingBySourceCode(String checkingSourceCode, Long ouId) {
        List<WhCheckingCommand> checkingList = whCheckingDao.findCheckingBySourceCode(checkingSourceCode, ouId);

        return checkingList;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCheckingCommand> findCheckingByTrolley(String trolleyCode, Long ouId) {
        List<WhCheckingCommand> checkingList = whCheckingDao.findCheckingByTrolley(trolleyCode, ouId);

        return checkingList;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCheckingCommand> findCheckingByOdo(Long odoId, Long ouId) {
        List<WhCheckingCommand> checkingList = whCheckingDao.findCheckingByOdo(odoId, ouId);

        return checkingList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCheckingCommand> findCheckingBySeedingFacility(String facilityCode, Long ouId) {
        List<WhCheckingCommand> checkingList = whCheckingDao.findCheckingBySeedingFacility(facilityCode, ouId);

        return checkingList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhCheckingCommand findCheckingByOutboundBox(String outboundBoxCode, Long ouId) {
        WhCheckingCommand whCheckingCommand = whCheckingDao.findWhCheckingByOutboundboxCode(outboundBoxCode, ouId);

        return whCheckingCommand;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhCheckingCommand findCheckingByTurnoverBox(String turnoverBoxCode, Long ouId) {
        WhCheckingCommand whCheckingCommand = whCheckingDao.findCheckingByTurnoverBox(turnoverBoxCode, ouId);

        return whCheckingCommand;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCheckingCommand> findCheckingByBoxCode(String checkingSourceCode, String checkingBoxCode, Long ouId) {
        List<WhCheckingCommand> checkingList = whCheckingDao.findCheckingByBoxCode(checkingSourceCode, checkingBoxCode, ouId);

        return checkingList;
    }

    /**
     * 根据条件查找复核头
     *
     * @author mingwei.xie
     * @param checkingCommand
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhCheckingCommand findCheckingByParam(WhCheckingCommand checkingCommand) {
        return whCheckingDao.findCheckingByParam(checkingCommand);
    }

    /**
     * 根据条件查找复核头
     *
     * @author mingwei.xie
     * @param checkingId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhCheckingCommand findCheckingById(Long checkingId, Long ouId) {
        WhCheckingCommand checkingCommand = new WhCheckingCommand();
        checkingCommand.setId(checkingId);
        checkingCommand.setOuId(ouId);
        WhCheckingCommand returnChecking = whCheckingDao.findCheckingByParam(checkingCommand);
        return returnChecking;
    }

    /**
     * 查找批次下所有的复核箱信息
     *
     * @author mingwei.xie
     * @param batchNo
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCheckingCommand> findCheckingByBatch(String batchNo, Long ouId) {
        return whCheckingDao.findCheckingByBatch(batchNo, ouId);
    }

    /**
     * 统计批次下待复核总单数
     *
     * @param batchNo
     * @param ouId
     * @return
     */

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int getCheckingOdoQtyByBatch(String batchNo, Long ouId) {
        return whCheckingDao.getCheckingOdoQtyByBatch(batchNo, ouId);
    }



    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOutboundFacilityCommand findOutboundFacilityById(Long id, Long ouId) {
        WhOutboundFacilityCommand facilityCommand = whOutboundFacilityDao.findByIdExt(id, ouId);

        return facilityCommand;
    }



    /**
     * 复核 占用耗材库存
     *
     * @param skuInventoryCommand
     * @param outboundBoxCode
     * @param ouId
     * @param logId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void occupationConsumableSkuInventory(WhSkuInventoryCommand skuInventoryCommand, String outboundBoxCode, Long ouId, String logId) {
        WhSkuInventory orgSkuInv = new WhSkuInventory();
        BeanUtils.copyProperties(skuInventoryCommand, orgSkuInv);
        orgSkuInv.setOnHandQty(orgSkuInv.getOnHandQty() - 1);
        // 更新原始库存数-1
        int updateCount = skuInventoryDao.saveOrUpdateByVersion(orgSkuInv);
        if (updateCount != 1) {
            throw new BusinessException(ErrorCodes.CHECKING_OCCUPATION_CONSUMABLE_ERROR);
        }

        WhSkuInventory occupationSkuInv = new WhSkuInventory();
        BeanUtils.copyProperties(skuInventoryCommand, occupationSkuInv);
        occupationSkuInv.setId(null);
        occupationSkuInv.setOnHandQty(1d);
        occupationSkuInv.setOccupationCodeSource(Constants.SKU_INVENTORY_OCCUPATION_SOURCE_CHECKING_CONSUMABLE);
        occupationSkuInv.setOccupationCode(outboundBoxCode);
        occupationSkuInv.setOccupationLineId(skuInventoryCommand.getId());

        skuInventoryDao.insert(occupationSkuInv);

    }

    /**
     * 复核 释放耗材库存
     *
     * @param outboundbox
     * @param ouId
     * @param logId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void releaseConsumableSkuInventory(WhOutboundboxCommand outboundbox, Long ouId, String logId) {
        if (null != outboundbox) {
            if (null == outboundbox.getOutboundboxCode() || null == outboundbox.getConsumableSkuId() || null == outboundbox.getConsumableLocationCode()) {
                throw new BusinessException(ErrorCodes.CHECKING_RELEASE_CONSUMABLE_PARAM_ERROR);
            }
            WhSkuInventoryCommand boxOccuInv =
                    skuInventoryDao.findCheckingConsumableOccSkuInv(outboundbox.getOutboundboxCode(), outboundbox.getConsumableSkuId(), outboundbox.getConsumableLocationCode(), Constants.SKU_INVENTORY_OCCUPATION_SOURCE_CHECKING_CONSUMABLE, ouId);
            if (null == boxOccuInv) {
                throw new BusinessException(ErrorCodes.CHECKING_OCCUPATION_CONSUMABLE_SKUINV_NULL_ERROR);
            }
            // 删除耗材占用的库存
            int deleteCount = skuInventoryDao.deleteWhSkuInventoryById(boxOccuInv.getId(), ouId);
            if (1 != deleteCount) {
                throw new BusinessException(ErrorCodes.CHECKING_DELETE_OCCUPATION_CONSUMABLE_SKUINV_ERROR);
            }

            // 还原耗材占用的原始库存
            WhSkuInventory orgBoxOccuInv = skuInventoryDao.findWhSkuInventoryById(boxOccuInv.getOccupationLineId(), ouId);
            if (null == orgBoxOccuInv) {
                throw new BusinessException(ErrorCodes.CHECKING_ORG_OCCUPATION_CONSUMABLE_SKUINV_NULL_ERROR);
            }
            orgBoxOccuInv.setOnHandQty(orgBoxOccuInv.getOnHandQty() + 1);
            int updateCount = skuInventoryDao.saveOrUpdateByVersion(orgBoxOccuInv);
            if (updateCount != 1) {
                throw new BusinessException(ErrorCodes.CHECKING_RESTORE_ORG_OCCUPATION_CONSUMABLE_SKUINV_ERROR);
            }
        }
    }

    /**
     * 查询批次下的所有复核集货
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCheckingCollectionCommand> findCheckingCollectionByBatch(String batchNo, Long ouId) {
        return checkingCollectionDao.findCheckingCollectionByBatch(batchNo, ouId);
    }

    /**
     * 查询批次下复核集货小车的集货数据
     *
     * @param batchNo
     * @param containerCode
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCheckingCollectionCommand> findCheckingCollectionByBatchTrolley(String batchNo, String containerCode, Long ouId) {
        return checkingCollectionDao.findCheckingCollectionByBatchTrolley(batchNo, containerCode, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventory> findCheckingOdoSkuInvByOdoLineIdUuid(Long odoLineId, Long ouId, String uuid) {
        return whSkuInventoryDao.findOdoSkuInvByOdoLineIdUuid(odoLineId, ouId, uuid);
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventorySnCommand> findCheckingSkuInvSnByCheckingId(Long checkingId, Long ouId) {
        return whSkuInventorySnDao.findCheckingSkuInvSnByCheckingId(checkingId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean isOutboundBoxAlreadyUsed(String outboundBoxCode, Long ouId, String logId) {
        String existOutboundboxCode = whSkuInventoryDao.getExistOutboundBoxCode(outboundBoxCode, ouId);
        Boolean boxAlreadyUsed = false;
        if (null != existOutboundboxCode) {
            boxAlreadyUsed = true;
        }
        return boxAlreadyUsed;
    }

    /**
     * 复核 释放耗材库存
     *
     * @param outboundbox
     * @param ouId
     * @param logId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhSkuInventoryCommand getConsumableSkuInventory(WhOutboundboxCommand outboundbox, Long ouId, String logId) {
        if (null == outboundbox || null == outboundbox.getOutboundboxCode() || null == outboundbox.getConsumableSkuId() || null == outboundbox.getConsumableLocationCode()) {
            throw new BusinessException(ErrorCodes.CHECKING_RELEASE_CONSUMABLE_PARAM_ERROR);
        }
        WhSkuInventoryCommand boxOccuInv =
                skuInventoryDao.findCheckingConsumableOccSkuInv(outboundbox.getOutboundboxCode(), outboundbox.getConsumableSkuId(), outboundbox.getConsumableLocationCode(), Constants.SKU_INVENTORY_OCCUPATION_SOURCE_CHECKING_CONSUMABLE, ouId);
        return boxOccuInv;
    }

    /**
     *
     * @param whOutboundFacility
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int releaseSeedingFacility(WhOutboundFacility whOutboundFacility) {
        int updateCount = whOutboundFacilityDao.saveOrUpdateByVersion(whOutboundFacility);
        return updateCount;
    }

    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void finishedChecking(WhCheckingResultCommand checkingResultCommand, Boolean isTabbInvTotal, Long userId, Long ouId, String logId) {

        // 更新复核头信息
        WhCheckingCommand orgCheckingCommand = checkingResultCommand.getOrgCheckingCommand();
        // 更新复核明细信息
        Set<WhCheckingLineCommand> toUpdateCheckingLineSet = checkingResultCommand.getToUpdateCheckingLineSet();
        // 更新原复核箱库存
        Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet = checkingResultCommand.getToUpdateOdoOrgSkuInvSet();
        // 创建出库箱信息
        WhOutboundbox whOutboundbox = checkingResultCommand.getWhOutboundbox();
        // 创建出库箱装箱明细信息
        List<WhOutboundboxLine> outboundboxLineList = checkingResultCommand.getOutboundboxLineList();
        // 创建出库箱库存
        List<WhSkuInventory> outboundboxSkuInvList = checkingResultCommand.getOutboundboxSkuInvList();
        // 创建包裹计重信息
        WhOdoPackageInfoCommand odoPackageInfoCommand = checkingResultCommand.getOdoPackageInfoCommand();
        // 创建耗材信息
        WhOutboundConsumable whOutboundConsumable = checkingResultCommand.getWhOutboundConsumable();
        // 待删除的耗材库存
        WhSkuInventoryCommand consumableSkuInv = checkingResultCommand.getConsumableSkuInv();
        // 更新出库单
        WhOdo whOdo = checkingResultCommand.getWhOdo();
        // 更新已复核的SN/残次信息
        List<WhSkuInventorySnCommand> checkedSnInvList = checkingResultCommand.getCheckedSnInvList();
        // 待释放容器 小车/周转箱
        Container container = checkingResultCommand.getContainer();
        // 待释放的播种墙
        WhOutboundFacility seedingFacility = checkingResultCommand.getSeedingFacility();
        // 出库单交接运单信息
        WhOdodeliveryInfo ododeliveryInfo = checkingResultCommand.getWhOdodeliveryInfo();


        // 更新复核头状态
        this.updateCheckingInfoToDB(orgCheckingCommand);
        // 更新明细复核数量
        this.updateCheckingBoxCheckingLineToDB(toUpdateCheckingLineSet);
        // 更新复核箱库存
        this.updateCheckingBoxSkuInvToDB(toUpdateOdoOrgSkuInvSet, isTabbInvTotal, userId, ouId);
        // 创建出库箱信息、出库箱明细信息
        this.createOutboundBoxInfo(whOutboundbox, outboundboxLineList, userId, ouId, logId);
        // 创建出库箱库存信息 WhSkuInventory
        this.createOutboundBoxSkuInv(outboundboxSkuInvList, isTabbInvTotal, userId, ouId);
        // 创建包裹计重
        this.saveOdoPackageInfoToDB(odoPackageInfoCommand);
        // 创建耗材信息、删除耗材库存
        if (null != whOutboundConsumable) {
            this.saveConsumableInfoToDB(whOutboundbox, whOutboundConsumable, consumableSkuInv, isTabbInvTotal, ouId, userId);
        }

        if (null != whOdo) {
            // 更新出库单
            this.updateOdo(whOdo, userId, ouId);
        }

        if (null != checkedSnInvList && !checkedSnInvList.isEmpty()) {
            // 更新SN/残次信息的UUID
            this.saveCheckedSnSkuInvToDB(checkedSnInvList, userId, ouId, logId);
        }

        if (null != container) {
            // 更新释放容器
            this.updateContainer(container, userId, ouId);
        }

        if (null != seedingFacility) {
            // 更新释放播种墙
            this.updateSeedingFacility(seedingFacility, userId, ouId);
        }

        // 更新出库单交接运单信息
        this.saveOrUpdateOdoDeliveryInfo(whOutboundbox, ododeliveryInfo, userId, ouId);

        // 保存打印信息
        this.printDefect(checkingResultCommand);
    }

    private void saveOrUpdateOdoDeliveryInfo(WhOutboundbox whOutboundbox, WhOdodeliveryInfo ododeliveryInfo, Long userId, Long ouId) {
        ododeliveryInfo.setOutboundboxId(whOutboundbox.getId());
        if (null == ododeliveryInfo.getId()) {
            whOdoDeliveryInfoDao.insert(ododeliveryInfo);
            this.insertGlobalLog(GLOBAL_LOG_INSERT, ododeliveryInfo, ouId, userId, null, null);
        } else {
            whOdoDeliveryInfoDao.saveOrUpdateByVersion(ododeliveryInfo);
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, ododeliveryInfo, ouId, userId, null, null);
        }
    }

    private void updateSeedingFacility(WhOutboundFacility seedingFacility, Long userId, Long ouId) {
        int updateCount = whOutboundFacilityDao.saveOrUpdateByVersion(seedingFacility);
        if (1 != updateCount) {
            throw new BusinessException(ErrorCodes.CHECKING_RELEASE_SEEDING_FACILITY_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_UPDATE, seedingFacility, ouId, userId, null, null);
    }

    private void updateContainer(Container container, Long userId, Long ouId) {
        int updateCount = containerDao.saveOrUpdateByVersion(container);
        if (1 != updateCount) {
            throw new BusinessException(ErrorCodes.CHECKING_RELEASE_CONTAINER_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
    }

    private void updateOdo(WhOdo whOdo, Long userId, Long ouId) {
        int updateCount = whOdoDao.saveOrUpdateByVersion(whOdo);
        if (1 != updateCount) {
            throw new BusinessException(ErrorCodes.CHECKING_ODO_UPDATE_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_UPDATE, whOdo, ouId, userId, null, null);
    }

    private void saveConsumableInfoToDB(WhOutboundbox whOutboundbox, WhOutboundConsumable whOutboundConsumable, WhSkuInventoryCommand consumableSkuInv, Boolean isTabbInvTotal, Long ouId, Long userId) {
        whOutboundConsumable.setOutboundboxId(whOutboundbox.getId());
        whOutboundConsumableDao.insert(whOutboundConsumable);
        this.insertGlobalLog(GLOBAL_LOG_INSERT, whOutboundConsumable, whOutboundConsumable.getOuId(), userId, null, null);

        Double originOnHandQty = 0.0;
        if (isTabbInvTotal) {
            originOnHandQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(consumableSkuInv.getUuid(), ouId);
        }
        this.insertSkuInventoryLog(consumableSkuInv.getId(), consumableSkuInv.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId, InvTransactionType.CHECK);

        WhSkuInventory consumableOrgSkuInv = new WhSkuInventory();
        BeanUtils.copyProperties(consumableSkuInv, consumableOrgSkuInv);
        int deleteCount = skuInventoryDao.deleteWhSkuInventoryById(consumableSkuInv.getId(), ouId);
        if (1 != deleteCount) {
            throw new BusinessException(ErrorCodes.CHECKING_CONSUMABLE_SKUINV_DELETE_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_DELETE, consumableOrgSkuInv, ouId, userId, null, null);
    }

    private void updateCheckingInfoToDB(WhCheckingCommand orgCheckingCommand) {
        WhChecking whChecking = new WhChecking();
        BeanUtils.copyProperties(orgCheckingCommand, whChecking);
        int updateCount = whCheckingDao.saveOrUpdateByVersion(whChecking);
        if (1 != updateCount) {
            throw new BusinessException(ErrorCodes.CHECKING_UPDATE_CHECKING_ERROR);
        }
        this.insertGlobalLog(GLOBAL_LOG_UPDATE, whChecking, whChecking.getOuId(), whChecking.getModifiedId(), null, null);
    }

    private void updateCheckingBoxCheckingLineToDB(Set<WhCheckingLineCommand> toUpdateCheckingLineSet) {
        for (WhCheckingLineCommand whCheckingLineCommand : toUpdateCheckingLineSet) {
            WhCheckingLine whCheckingLine = new WhCheckingLine();
            BeanUtils.copyProperties(whCheckingLineCommand, whCheckingLine);
            int updateCount = whCheckingLineDao.saveOrUpdateByVersion(whCheckingLine);
            if (1 != updateCount) {
                throw new BusinessException(ErrorCodes.CHECKING_UPDATE_CHECKING_LINE_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_UPDATE, whCheckingLine, whCheckingLine.getOuId(), whCheckingLine.getModifiedId(), null, null);
        }
    }

    private void createOutboundBoxInfo(WhOutboundbox whOutboundbox, List<WhOutboundboxLine> outboundboxLineList, Long userId, Long ouId, String logId) {
        whOutboundboxDao.insert(whOutboundbox);

        for (WhOutboundboxLine whOutboundboxLine : outboundboxLineList) {
            whOutboundboxLine.setWhOutboundboxId(whOutboundbox.getId());
            whOutboundboxLineDao.insert(whOutboundboxLine);
            this.insertGlobalLog(GLOBAL_LOG_INSERT, whOutboundboxLine, whOutboundboxLine.getOuId(), userId, null, null);
        }
    }

    private void updateCheckingBoxSkuInvToDB(Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet, Boolean isTabbInvTotal, Long userId, Long ouId) {
        // 更新复核箱库存
        for (WhSkuInventory odoOrgSkuInv : toUpdateOdoOrgSkuInvSet) {
            Double originOnHandQty = 0.0;
            if (isTabbInvTotal) {
                originOnHandQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(odoOrgSkuInv.getUuid(), odoOrgSkuInv.getOuId());
            }

            WhSkuInventory orgSkuInv = whSkuInventoryDao.findWhSkuInventoryById(odoOrgSkuInv.getId(), ouId);
            if (0 == odoOrgSkuInv.getOnHandQty()) {

                this.insertSkuInventoryLog(odoOrgSkuInv.getId(), -orgSkuInv.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId, InvTransactionType.CHECK);

                int deleteCount = whSkuInventoryDao.deleteWhSkuInventoryById(odoOrgSkuInv.getId(), odoOrgSkuInv.getOuId());
                if (0 == deleteCount) {
                    throw new BusinessException(ErrorCodes.CHECKING_CHECKING_SKUINV_DELETE_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_DELETE, odoOrgSkuInv, ouId, userId, null, null);

            } else {
                int updateCount = whSkuInventoryDao.saveOrUpdateByVersion(odoOrgSkuInv);
                if (1 != updateCount) {
                    throw new BusinessException(ErrorCodes.CHECKING_CHECKING_SKUINV_UPDATE_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_UPDATE, odoOrgSkuInv, ouId, userId, null, null);

                // 从仓库判断是否需要记录库存数量变化
                this.insertSkuInventoryLog(odoOrgSkuInv.getId(), odoOrgSkuInv.getOnHandQty() - orgSkuInv.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId, InvTransactionType.CHECK);

            }
        }
    }

    private void createOutboundBoxSkuInv(List<WhSkuInventory> outboundboxSkuInvList, Boolean isTabbInvTotal, Long userId, Long ouId) {
        for (WhSkuInventory whSkuInventory : outboundboxSkuInvList) {
            Double originOnHandQty = 0.0;
            if (isTabbInvTotal) {
                originOnHandQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(whSkuInventory.getUuid(), whSkuInventory.getOuId());
            }
            // 从仓库判断是否需要记录库存数量变化
            whSkuInventoryDao.insert(whSkuInventory);
            // log.warn("SeedingManagerImpl.saveWhSkuInventoryToDB save whSkuInventory to share DB, whSkuInventory is:[{}], logId is:[{}]",
            // whSkuInventory, logId);
            this.insertGlobalLog(GLOBAL_LOG_INSERT, whSkuInventory, ouId, userId, null, null);
            this.insertSkuInventoryLog(whSkuInventory.getId(), whSkuInventory.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId, InvTransactionType.CHECK);
        }
    }

    private void saveCheckedSnSkuInvToDB(List<WhSkuInventorySnCommand> checkedSnInvList, Long userId, Long ouId, String logId) {
        Set<String> uuidSet = new HashSet<>();
        for (WhSkuInventorySnCommand whSkuInventorySnCommand : checkedSnInvList) {
            if (uuidSet.contains(whSkuInventorySnCommand.getOldUuid())) {
                continue;
            }
            uuidSet.add(whSkuInventorySnCommand.getOldUuid());
            insertSkuInventorySnLog(whSkuInventorySnCommand.getOldUuid(), ouId);
        }
        for (WhSkuInventorySnCommand whSkuInventorySnCommand : checkedSnInvList) {

            WhSkuInventorySn whSkuInventorySn = new WhSkuInventorySn();
            BeanUtils.copyProperties(whSkuInventorySnCommand, whSkuInventorySn);
            int updateCount = whSkuInventorySnDao.saveOrUpdateByVersion(whSkuInventorySn);
            if (1 != updateCount) {
                throw new BusinessException(ErrorCodes.CHECKING_CHECKING_SN_UPDATE_ERROR);
            }

            this.insertGlobalLog(GLOBAL_LOG_UPDATE, whSkuInventorySn, whSkuInventorySn.getOuId(), userId, null, null);
        }
    }

    private void saveOdoPackageInfoToDB(WhOdoPackageInfoCommand odoPackageInfoCommand) {
        WhOdoPackageInfo whOdoPackageInfo = new WhOdoPackageInfo();
        // 复制数据
        BeanUtils.copyProperties(odoPackageInfoCommand, whOdoPackageInfo);
        whOdoPackageInfoDao.insert(whOdoPackageInfo);

        this.insertGlobalLog(GLOBAL_LOG_INSERT, whOdoPackageInfo, whOdoPackageInfo.getOuId(), whOdoPackageInfo.getCreateId(), null, null);

    }

    /**
     * 根据复核打印配置打印单据
     * 
     * @author qiming.liu
     * @param whCheckingResultCommand
     */
    @Override
    public void printDefect(WhCheckingResultCommand whCheckingResultCommand) {
        Long ouId = whCheckingResultCommand.getOuId();
        // 查询功能是否配置复核打印单据配置
        WhFunctionOutBound whFunctionOutBound = whFunctionOutBoundManager.findByFunctionIdExt(whCheckingResultCommand.getFunctionId(), ouId);
        String checkingPrint = whFunctionOutBound.getCheckingPrint();
        String outboundBoxCode = whCheckingResultCommand.getWhOutboundbox().getOutboundboxCode();
        if (null != checkingPrint && !"".equals(checkingPrint)) {
            String[] checkingPrintArray = checkingPrint.split(",");
            for (int i = 0; i < checkingPrintArray.length; i++) {
                List<Long> idsList = new ArrayList<Long>();
                WhCheckingCommand whCheckingCommand = whCheckingResultCommand.getOrgCheckingCommand();
                List<WhPrintInfo> whPrintInfoLst = whPrintInfoDao.findByOutboundboxCodeAndPrintType(whCheckingCommand.getOutboundboxCode(), checkingPrintArray[i], ouId);
                if (null == whPrintInfoLst || 0 == whPrintInfoLst.size()) {
                    WhPrintInfo whPrintInfo = new WhPrintInfo();
                    whPrintInfo.setFacilityId(whCheckingCommand.getFacilityId());
                    if (null != whCheckingCommand.getContainerId()) {
                        whPrintInfo.setContainerId(whCheckingCommand.getContainerId());
                        Container container = containerDao.findByIdExt(whCheckingCommand.getContainerId(), whCheckingCommand.getOuId());
                        whPrintInfo.setContainerCode(container.getCode());
                    }
                    whPrintInfo.setBatch(whCheckingCommand.getBatch());
                    whPrintInfo.setWaveCode(whCheckingCommand.getWaveCode());
                    whPrintInfo.setOuId(whCheckingCommand.getOuId());
                    if (null != whCheckingCommand.getOuterContainerId()) {
                        whPrintInfo.setOuterContainerId(whCheckingCommand.getOuterContainerId());
                        Container outerContainer = containerDao.findByIdExt(whCheckingCommand.getOuterContainerId(), whCheckingCommand.getOuId());
                        whPrintInfo.setOuterContainerCode(outerContainer.getCode());
                    }
                    whPrintInfo.setContainerLatticeNo(whCheckingCommand.getContainerLatticeNo());
                    WhOutboundbox whOutboundbox = whCheckingResultCommand.getWhOutboundbox();
                    if (null == whOutboundbox) {
                        throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                    }
                    whPrintInfo.setOutboundboxId(whOutboundbox.getOutboundboxId());
                    whPrintInfo.setOutboundboxCode(whOutboundbox.getOutboundboxCode());
                    whPrintInfo.setPrintType(checkingPrintArray[i]);
                    whPrintInfo.setPrintCount(1);
                    whPrintInfoDao.insert(whPrintInfo);
                }
                try {
                    if (CheckingPrint.PACKING_LIST.equals(checkingPrintArray[i])) {
                        // 装箱清单
                        this.printPackingList(idsList, whCheckingResultCommand.getUserId(), ouId);
                    }
                    if (CheckingPrint.SALES_LIST.equals(checkingPrintArray[i])) {
                        idsList.add(whCheckingResultCommand.getWhOdo().getId());
                        // 销售清单
                        this.printSalesList(idsList, whCheckingResultCommand.getUserId(), ouId);
                    }
                    if (CheckingPrint.SINGLE_PLANE.equals(checkingPrintArray[i])) {
                        // 面单
                        this.printSinglePlane(outboundBoxCode, whCheckingResultCommand.getWhOdodeliveryInfo().getWaybillCode(), whCheckingResultCommand.getUserId(), ouId, whCheckingResultCommand.getWhOdo().getId());
                    }
                    if (CheckingPrint.BOX_LABEL.equals(checkingPrintArray[i])) {
                        // 箱标签
                        this.printBoxLabel(outboundBoxCode, whCheckingResultCommand.getUserId(), ouId, whCheckingResultCommand.getWhOdo().getId());
                    }
                } catch (Exception e) {
                    log.error(e + "");
                }
            }
        }
    }


}
