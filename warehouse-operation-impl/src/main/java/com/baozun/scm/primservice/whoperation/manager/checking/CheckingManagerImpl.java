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

import java.util.Date;
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

import com.baozun.scm.baseservice.print.command.PrintDataCommand;
import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOdoPackageInfoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOdoPackageInfoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSkuManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOdoPackageInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

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
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_13, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printSinglePlane(List<Long> facilityIdsList, Long userId, Long ouId) {
        // 打印面单
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setIdList(facilityIdsList);
            printObjectManagerProxy.printCommonInterface(printDataCommand, Constants.PRINT_ORDER_TYPE_15, userId, ouId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBoxLabel(List<Long> facilityIdsList, Long userId, Long ouId) {
        // 打印箱标签
        try {
            PrintDataCommand printDataCommand = new PrintDataCommand();
            printDataCommand.setIdList(facilityIdsList);
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
    public int getCheckingOdoQtyByBatch(String batchNo, Long ouId) {
        return whCheckingDao.getCheckingOdoQtyByBatch(batchNo, ouId);
    }


    /**
     * 生成出库箱库存
     *
     */
    @Override
    public String createOutboundboxInventory(WhCheckingCommand checkingCommand, List<WhSkuInventory> whSkuInventoryLst) {
        if (null == whSkuInventoryLst) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        WhSkuInventory whSkuInventory = whSkuInventoryLst.get(0);
        WhSkuInventoryCommand whSkuInventoryCommand = new WhSkuInventoryCommand();
        /** 商品ID */
        whSkuInventoryCommand.setSkuId(whSkuInventory.getSkuId());
        /** 库位ID 库位号 */
        whSkuInventoryCommand.setLocationId(whSkuInventory.getLocationId());
        /** 暂存库位ID */
        whSkuInventoryCommand.setTemporaryLocationId(whSkuInventory.getTemporaryLocationId());
        /** 外部容器ID 托盘 货箱 */
        whSkuInventoryCommand.setOuterContainerId(null);
        /** 内部容器ID 托盘 货箱 */
        whSkuInventoryCommand.setInsideContainerId(null);
        /** 客户ID */
        whSkuInventoryCommand.setCustomerId(whSkuInventory.getCustomerId());
        /** 店铺ID */
        whSkuInventoryCommand.setStoreId(whSkuInventory.getStoreId());
        /** 占用单据号 */
        whSkuInventoryCommand.setOccupationCode(whSkuInventory.getOccupationCode());
        /** 占用单据明细行ID */
        whSkuInventoryCommand.setOnHandQty(null);
        /** 播种墙编码 */
        whSkuInventoryCommand.setSeedingWallCode(null);
        /** 货格号 */
        whSkuInventoryCommand.setContainerLatticeNo(null);
        /** 出库箱号 */
        whSkuInventoryCommand.setOutboundboxCode(checkingCommand.getOutboundboxCode());
        /** 在库可用库存 */
        whSkuInventoryCommand.setOnHandQty(whSkuInventory.getOnHandQty());
        /** 已分配库存 */
        whSkuInventoryCommand.setAllocatedQty(null);
        /** 待移入库存 */
        whSkuInventoryCommand.setToBeFilledQty(null);
        /** 冻结库存 */
        whSkuInventoryCommand.setFrozenQty(whSkuInventory.getFrozenQty());
        /** 库存状态 */
        whSkuInventoryCommand.setInvStatus(whSkuInventory.getInvStatus());
        /** 库存类型 */
        whSkuInventoryCommand.setInvType(whSkuInventory.getInvType());
        /** 批次号 */
        whSkuInventoryCommand.setBatchNumber(whSkuInventory.getBatchNumber());
        /** 生产日期 */
        whSkuInventoryCommand.setMfgDate(whSkuInventory.getMfgDate());
        /** 失效日期 */
        whSkuInventoryCommand.setExpDate(whSkuInventory.getExpDate());
        /** 原产地 */
        whSkuInventoryCommand.setCountryOfOrigin(whSkuInventory.getCountryOfOrigin());
        /** 库存属性1 */
        whSkuInventoryCommand.setInvAttr1(whSkuInventory.getInvAttr1());
        /** 库存属性2 */
        whSkuInventoryCommand.setInvAttr2(whSkuInventory.getInvAttr2());
        /** 库存属性3 */
        whSkuInventoryCommand.setInvAttr3(whSkuInventory.getInvAttr3());
        /** 库存属性4 */
        whSkuInventoryCommand.setInvAttr4(whSkuInventory.getInvAttr4());
        /** 库存属性5 */
        whSkuInventoryCommand.setInvAttr5(whSkuInventory.getInvAttr5());
        /** 内部对接码 */
        try {
            WhSkuInventory inv = new WhSkuInventory();
            // 复制数据
            BeanUtils.copyProperties(whSkuInventoryCommand, inv);
            whSkuInventoryCommand.setUuid(SkuInventoryUuid.invUuid(inv));
        } catch (Exception e) {
            log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
            throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
        }
        /** 是否可用 */
        whSkuInventoryCommand.setIsLocked(whSkuInventory.getIsLocked());
        /** 对应仓库ID */
        whSkuInventoryCommand.setOuId(whSkuInventory.getOuId());
        /** 占用单据号来源 */
        whSkuInventoryCommand.setOccupationCodeSource(whSkuInventory.getOccupationCodeSource());
        /** 入库时间 */
        whSkuInventoryCommand.setInboundTime(whSkuInventory.getInboundTime());
        /** 最后操作时间 */
        whSkuInventoryCommand.setLastModifyTime(new Date());
        whSkuInventoryManager.saveOrUpdate(whSkuInventoryCommand);
        return whSkuInventoryCommand.getUuid();
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOutboundFacilityCommand findOutboundFacilityById(Long id, Long ouId) {
        WhOutboundFacilityCommand facilityCommand = whOutboundFacilityDao.findByIdExt(id, ouId);

        return facilityCommand;
    }

    /**
     * 根据绑定的MAC地址查询复核台
     *
     * @param macAddr
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOutboundFacilityCommand findOutboundFacilityByMacAddr(String ipAddr, String macAddr, Long ouId) {
        return whOutboundFacilityDao.findOutboundFacilityByMacAddr(ipAddr, macAddr, ouId);
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
     * @param outboundboxList
     * @param ouId
     * @param logId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void releaseConsumableSkuInventory(List<WhOutboundboxCommand> outboundboxList, Long ouId, String logId) {
        if (null != outboundboxList) {
            for (WhOutboundboxCommand outboundBox : outboundboxList) {
                if (null == outboundBox.getOutboundboxCode() || null == outboundBox.getConsumableSkuId() || null == outboundBox.getConsumableLocationCode()) {
                    throw new BusinessException(ErrorCodes.CHECKING_RELEASE_CONSUMABLE_PARAM_ERROR);
                }
                WhSkuInventoryCommand boxOccuInv =
                        skuInventoryDao.findCheckingConsumableOccSkuInv(outboundBox.getOutboundboxCode(), outboundBox.getConsumableSkuId(), outboundBox.getConsumableLocationCode(), Constants.SKU_INVENTORY_OCCUPATION_SOURCE_CHECKING_CONSUMABLE, ouId);
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
    }

    /**
     * 查询批次下的所有复核集货
     *
     * @param batchNo
     * @param ouId
     * @return
     */
    @Override
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
    public List<WhCheckingCollectionCommand> findCheckingCollectionByBatchTrolley(String batchNo, String containerCode, Long ouId) {
        return checkingCollectionDao.findCheckingCollectionByBatchTrolley(batchNo, containerCode, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventory> findCheckingOdoSkuInvByOdoLineIdUuid(Long odoLineId, Long ouId, String uuid) {
        return whSkuInventoryDao.findOdoSkuInvByOdoLineIdUuid(odoLineId, ouId, uuid);
    }

    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void finishedChecking(WhCheckingCommand orgChecking, Set<WhCheckingLineCommand> toUpdateCheckingLineSet, List<WhOutboundbox> whOutboundboxList, Map<WhOutboundbox, List<WhOutboundboxLine>> outboundboxLineListMap,
            Map<WhOutboundbox, List<WhSkuInventory>> outboundboxSkuInvListMap, Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet, List<WhOdoPackageInfoCommand> odoPackageInfoList, List<WhSkuInventorySnCommand> checkedSnInvList, String checkingType,
            Boolean isTabbInvTotal, Long userId, Long ouId, String logId) {

        // 更新复核头状态
        WhChecking whChecking = new WhChecking();
        BeanUtils.copyProperties(orgChecking, whChecking);
        whCheckingDao.saveOrUpdateByVersion(whChecking);

        // 更新明细复核数量
        for (WhCheckingLineCommand whCheckingLineCommand : toUpdateCheckingLineSet) {
            WhCheckingLine whCheckingLine = new WhCheckingLine();
            BeanUtils.copyProperties(whCheckingLineCommand, whCheckingLine);
            whCheckingLineDao.saveOrUpdateByVersion(whCheckingLine);
        }

        // 创建出库箱信息和明细信息
        if (Constants.OUTBOUND_BOX_CHECKING_TYPE_TROLLEY_GRID.equals(checkingType) || Constants.OUTBOUND_BOX_CHECKING_TYPE_SEEDING_GRID.equals(checkingType) || Constants.OUTBOUND_BOX_CHECKING_TYPE_TURNOVER_BOX.equals(checkingType)) {
            // TODO 创建出库箱和明细数据
            for (WhOutboundbox whOutboundbox : whOutboundboxList) {

                whOutboundboxDao.insert(whOutboundbox);

                List<WhOutboundboxLine> outboundboxLineList = outboundboxLineListMap.get(whOutboundbox);
                for (WhOutboundboxLine whOutboundboxLine : outboundboxLineList) {
                    whOutboundboxLine.setWhOutboundboxId(whOutboundbox.getId());
                    whOutboundboxLineDao.insert(whOutboundboxLine);
                }

            }
        }

        // 更新复核箱库存
        for (WhSkuInventory odoOrgSkuInv : toUpdateOdoOrgSkuInvSet) {
            if (0 == odoOrgSkuInv.getOnHandQty()) {
                whSkuInventoryDao.deleteWhSkuInventoryById(odoOrgSkuInv.getId(), odoOrgSkuInv.getOuId());
            } else {
                whSkuInventoryDao.saveOrUpdateByVersion(odoOrgSkuInv);
                Double originOnHandQty = 0.0;
                if (isTabbInvTotal) {
                    originOnHandQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(odoOrgSkuInv.getUuid(), odoOrgSkuInv.getOuId());
                }
                // 从仓库判断是否需要记录库存数量变化
                this.insertSkuInventoryLog(odoOrgSkuInv.getId(), odoOrgSkuInv.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId, InvTransactionType.SEEDING);

            }
        }

        // 创建出库箱库存
        for (WhOutboundbox whOutboundbox : whOutboundboxList) {
            List<WhSkuInventory> outboundBoxSkuInvList = outboundboxSkuInvListMap.get(whOutboundbox);

            // 创建 库存信息 WhSkuInventory
            for (WhSkuInventory whSkuInventory : outboundBoxSkuInvList) {
                Double originOnHandQty = 0.0;
                if (isTabbInvTotal) {
                    originOnHandQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(whSkuInventory.getUuid(), whSkuInventory.getOuId());
                }
                // 从仓库判断是否需要记录库存数量变化
                whSkuInventoryDao.insert(whSkuInventory);
                // log.warn("SeedingManagerImpl.saveWhSkuInventoryToDB save whSkuInventory to share DB, whSkuInventory is:[{}], logId is:[{}]",
                // whSkuInventory, logId);
                this.insertGlobalLog(GLOBAL_LOG_INSERT, whSkuInventory, ouId, userId, null, null);
                this.insertSkuInventoryLog(whSkuInventory.getId(), whSkuInventory.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId, InvTransactionType.SEEDING);
            }
        }


        if (!checkedSnInvList.isEmpty()) {
            for (WhSkuInventorySnCommand whSkuInventorySnCommand : checkedSnInvList) {

                WhSkuInventorySn whSkuInventorySn = new WhSkuInventorySn();
                BeanUtils.copyProperties(whSkuInventorySnCommand, whSkuInventorySn);
                whSkuInventorySnDao.saveOrUpdateByVersion(whSkuInventorySn);
            }
        }

        for (WhOdoPackageInfoCommand whOdoPackageInfoCommand : odoPackageInfoList) {
            WhOdoPackageInfo whOdoPackageInfo = new WhOdoPackageInfo();
            // 复制数据
            BeanUtils.copyProperties(whOdoPackageInfoCommand, whOdoPackageInfo);
            whOdoPackageInfoDao.insert(whOdoPackageInfo);
        }


        throw new BusinessException("test error");
    }


}
