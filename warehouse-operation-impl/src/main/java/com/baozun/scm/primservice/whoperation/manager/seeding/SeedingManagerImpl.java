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

package com.baozun.scm.primservice.whoperation.manager.seeding;

import java.util.ArrayList;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.print.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutInvBoxTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CollectionStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionSeedingWallDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryLogDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.warehouse.outbound.CheckingModeCalcManager;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLattice;
import com.baozun.scm.primservice.whoperation.model.seeding.WhSeedingWallLatticeLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionSeedingWall;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollectionLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

@Service("seedingManager")
@Transactional
public class SeedingManagerImpl extends BaseManagerImpl implements SeedingManager {
    public static final Logger log = LoggerFactory.getLogger(SeedingManagerImpl.class);

    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;

    @Autowired
    private ContainerDao containerDao;

    @Autowired
    private WhOdoDao whOdoDao;

    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;

    @Autowired
    private OutBoundBoxTypeDao outBoundBoxTypeDao;

    @Autowired
    private WhFunctionSeedingWallDao whFunctionSeedingWallDao;

    @Autowired
    private WhSeedingCollectionLineDao seedingCollectionLineDao;

    @Autowired
    private WhSeedingCollectionDao whSeedingCollectionDao;

    @Autowired
    private WhSkuInventoryLogDao whSkuInventoryLogDao;
    @Autowired
    private CheckingModeCalcManager checkingModeCalcManager;

    @Autowired
    private SeedingManagerProxy seedingManagerProxy;

    @Autowired
    private WhSeedingCollectionLineDao whSeedingCollectionLineDao;

    @Autowired
    private WhOutboundboxDao whOutboundboxDao;

    @Autowired
    private WhOutboundboxLineDao whOutboundboxLineDao;

    /**
     * 根据容器编码查找容器
     *
     * @param code
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ContainerCommand getContainerByCode(String code, Long ouId) {
        return containerDao.getContainerByCode(code, ouId);
    }

    /**
     * 获取播种周转箱的数据
     *
     * @param containerId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSeedingCollectionLine> findSeedingDataByContainerId(Long containerId, Long ouId) {
        return whSkuInventoryDao.findSeedingDataByContainerId(containerId, ouId);
    }

    /**
     * 获取播种中出库单明细信息
     *
     * @param odoId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSeedingWallLatticeLine> getSeedingOdoLineInfo(Long odoId, Long ouId) {
        return whOdoDao.getSeedingOdoLineInfo(odoId, ouId);
    }

    /**
     * 获取出库设施
     *
     * @param facilityCode
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOutboundFacilityCommand getOutboundFacilityByFacilityCode(String facilityCode, Long ouId) {
        return whOutboundFacilityDao.getOutboundFacilityByCode(facilityCode, null, ouId);
    }

    /**
     * 获取出库设施
     *
     * @param facilityCheckCode
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOutboundFacilityCommand getOutboundFacilityByFacilityCheckCode(String facilityCheckCode, Long ouId) {
        return whOutboundFacilityDao.getOutboundFacilityByCode(null, facilityCheckCode, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOutboundFacilityCommand getOutboundFacilityById(Long facilityId, Long ouId) {
        WhOutboundFacilityCommand facilityCommand = whOutboundFacilityDao.findByIdExt(facilityId, ouId);
        // TODO 测试 设置播种墙出库箱类型
        // facilityCommand.setOutboundboxTypeId(23100001L);
        return facilityCommand;
    }

    /**
     * 根据id 和ouId 获取出库箱类型
     *
     * @param id
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public OutInvBoxTypeCommand findOutInventoryBoxType(Long id, Long ouId) {
        return outBoundBoxTypeDao.findOutInventoryBoxType(id, ouId);
    }

    /**
     * 查询播种墙功能配置
     *
     * @param id
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionSeedingWall findFunctionById(Long id, Long ouId) {

        WhFunctionSeedingWall function = whFunctionSeedingWallDao.findByFunctionIdExt(ouId, id);

        return function;
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhSeedingCollectionLine findSeedingCollectionLineById(Long seedingCollectionLineId, Long ouId) {
        return seedingCollectionLineDao.findByIdExt(seedingCollectionLineId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSeedingCollectionLineCommand> getSeedingCollectionLineByCollection(Long seedingCollectionId, Long ouId) {
        return seedingCollectionLineDao.getSeedingCollectionLineByCollection(seedingCollectionId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateSeedingCollectionLineByVersion(WhSeedingCollectionLine seedingCollectionLine) {
        return seedingCollectionLineDao.saveOrUpdateByVersion(seedingCollectionLine);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void batchFinishedSeedingWithException(WhOutboundFacilityCommand facilityCommand, List<WhSeedingCollectionCommand> seedingCollectionList, List<WhSeedingCollectionLineCommand> facilitySeedingCollectionLineList, Long logId) {


        WhOutboundFacility outboundFacility = new WhOutboundFacility();
        BeanUtils.copyProperties(facilityCommand, outboundFacility);
        outboundFacility.setStatus(String.valueOf(Constants.WH_FACILITY_STATUS_5));
        // TODO 测试 不更新播种墙状态
        whOutboundFacilityDao.saveOrUpdateByVersion(outboundFacility);


        for (WhSeedingCollectionCommand orgSeedingCollection : seedingCollectionList) {
            if (!CollectionStatus.FINISH.equals(orgSeedingCollection.getCollectionStatus())) {
                // 非完成状态的周转箱状态改为异常
                WhSeedingCollection seedingCollection = new WhSeedingCollection();
                BeanUtils.copyProperties(orgSeedingCollection, seedingCollection);
                seedingCollection.setCollectionStatus(CollectionStatus.ERROR);
                whSeedingCollectionDao.saveOrUpdateByVersion(seedingCollection);
            }
        }
        // 更新明细的播种数量
        for (WhSeedingCollectionLineCommand orgLine : facilitySeedingCollectionLineList) {
            WhSeedingCollectionLine seedingCollectionLine = new WhSeedingCollectionLine();
            BeanUtils.copyProperties(orgLine, seedingCollectionLine);
            // TODO 测试 不更新播种数量
            whSeedingCollectionLineDao.saveOrUpdate(seedingCollectionLine);
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void finishedSeedingByOutboundBox(Long facilityId, String batchNo, List<WhSeedingCollectionLineCommand> boxSeedingLineList, List<WhSkuInventory> odoOrgSkuInvList, List<WhSkuInventory> odoSeedingSkuInventoryList, Boolean isTabbInvTotal,
            Long userId, Long ouId, String logId) {
        for (WhSkuInventory odoOrgSkuInv : odoOrgSkuInvList) {
            Double originOnHandQty = 0.0;
            if (isTabbInvTotal) {
                originOnHandQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(odoOrgSkuInv.getUuid(), odoOrgSkuInv.getOuId());
            }
            WhSkuInventory orgSkuInv = whSkuInventoryDao.findWhSkuInventoryById(odoOrgSkuInv.getId(), ouId);

            if (0 == odoOrgSkuInv.getOnHandQty()) {
                this.insertSkuInventoryLog(odoOrgSkuInv.getId(), -orgSkuInv.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId, InvTransactionType.SEEDING);

                int deleteCount = whSkuInventoryDao.deleteWhSkuInventoryById(odoOrgSkuInv.getId(), ouId);
                if (0 == deleteCount) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_DELETE, odoOrgSkuInv, ouId, userId, null, null);
            } else {
                int updateCount = whSkuInventoryDao.saveOrUpdateByVersion(odoOrgSkuInv);
                if (1 != updateCount) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                this.insertGlobalLog(GLOBAL_LOG_UPDATE, odoOrgSkuInv, ouId, userId, null, null);
                // 从仓库判断是否需要记录库存数量变化
                this.insertSkuInventoryLog(odoOrgSkuInv.getId(), odoOrgSkuInv.getOnHandQty() - orgSkuInv.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId, InvTransactionType.SEEDING);

            }
        }

        this.saveWhSkuInventoryToDB(odoSeedingSkuInventoryList, isTabbInvTotal, userId, ouId, logId);
        // 生成复核数据并重计算复核模式
        checkingModeCalcManager.generateCheckingDataBySeeding(facilityId, batchNo, boxSeedingLineList, userId, ouId, logId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void finishedSeedingByOdo(Long facilityId, String batchNo, List<WhSeedingCollectionLineCommand> odoSeedingLineList, WhSeedingWallLattice seedingWallLattice, List<WhSkuInventory> odoSeedingSkuInventoryList, Boolean isTabbInvTotal, Long userId,
            Long ouId, String logId) {
        List<WhSkuInventoryCommand> skuInventoryList = whSkuInventoryDao.findListByOccupationCode(seedingWallLattice.getOdoCode(), ouId);
        for (WhSkuInventoryCommand skuInventoryCommand : skuInventoryList) {
            WhSkuInventory whSkuInventory = new WhSkuInventory();
            BeanUtils.copyProperties(skuInventoryCommand, whSkuInventory);

            Double originOnHandQty = 0.0;
            if (isTabbInvTotal) {
                originOnHandQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(whSkuInventory.getUuid(), whSkuInventory.getOuId());
            }
            this.insertSkuInventoryLog(whSkuInventory.getId(), -whSkuInventory.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId, InvTransactionType.SEEDING);

            int deleteCount = whSkuInventoryDao.deleteWhSkuInventoryById(whSkuInventory.getId(), whSkuInventory.getOuId());
            if (0 == deleteCount) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            this.insertGlobalLog(GLOBAL_LOG_DELETE, whSkuInventory, ouId, userId, null, null);
        }
        // whSkuInventoryDao.deleteByOccupationCode(seedingWallLattice.getOdoCode(), ouId);
        this.saveWhSkuInventoryToDB(odoSeedingSkuInventoryList, isTabbInvTotal, userId, ouId, logId);
        // 生成复核数据并重计算复核模式
        checkingModeCalcManager.generateCheckingDataBySeeding(facilityId, batchNo, odoSeedingLineList, userId, ouId, logId);
    }



    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventory> findSeedingOdoSkuInvByOdoLineIdUuid(Long odoLineId, Long ouId, String uuid) {
        return whSkuInventoryDao.findOdoSkuInvByOdoLineIdUuid(odoLineId, ouId, uuid);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createOutboundBox(WhOutboundbox whOutboundbox, List<WhOutboundboxLine> whOutboundboxLineList) {
        whOutboundboxDao.insert(whOutboundbox);

        for (WhOutboundboxLine whOutboundboxLine : whOutboundboxLineList) {
            whOutboundboxLine.setWhOutboundboxId(whOutboundbox.getId());

            whOutboundboxLineDao.insert(whOutboundboxLine);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateOutboundFacility(WhOutboundFacilityCommand facilityCommand, String logId) {
        WhOutboundFacility whOutboundFacility = new WhOutboundFacility();
        BeanUtils.copyProperties(facilityCommand, whOutboundFacility);
        int count = whOutboundFacilityDao.saveOrUpdateByVersion(whOutboundFacility);
        if (count != 1) {
            log.error("SeedingManagerImpl update outboundFacility error, facility is:[{}], logId is:[{}]", whOutboundFacility, logId);
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        return count;
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
     * 保存库存记录到数据库
     *
     * @author mingwei.xie
     * @param toSaveSkuInventoryList
     * @param isTabbInvTotal
     * @param userId
     * @param ouId
     * @param logId
     */
    private void saveWhSkuInventoryToDB(List<WhSkuInventory> toSaveSkuInventoryList, Boolean isTabbInvTotal, Long userId, Long ouId, String logId) {
        // 创建 库存信息 WhSkuInventory
        for (WhSkuInventory whSkuInventory : toSaveSkuInventoryList) {

            Double originOnHandQty = 0.0;
            if (isTabbInvTotal) {
                originOnHandQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(whSkuInventory.getUuid(), whSkuInventory.getOuId());
            }
            // 从仓库判断是否需要记录库存数量变化
            whSkuInventoryDao.insert(whSkuInventory);
            log.warn("SeedingManagerImpl.saveWhSkuInventoryToDB save whSkuInventory to share DB, whSkuInventory is:[{}], logId is:[{}]", whSkuInventory, logId);
            this.insertGlobalLog(GLOBAL_LOG_INSERT, whSkuInventory, ouId, userId, null, null);
            this.insertSkuInventoryLog(whSkuInventory.getId(), whSkuInventory.getOnHandQty(), originOnHandQty, isTabbInvTotal, ouId, userId, InvTransactionType.SEEDING);
        }

    }

    /**
     * 一键释放播种墙
     * 
     * @throws Exception
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String liberateSeedingWall(String containerCode, Long ouId, Long userId, Long facilityId) throws Exception {
        log.info("SeedingManagerImpl.liberateSeedingWall begin!");
        String msg = "success";
        WhOutboundFacility f = whOutboundFacilityDao.findByIdAndOuId(facilityId, ouId);
        // 验证播种墙是否存在库存
        List<WhSkuInventory> skuInv = whSkuInventoryDao.findSkuInventoryBySeedingWallCode(f.getFacilityCode(), null, ouId);
        if (null != skuInv && skuInv.size() > 0) {
            return "seedingWallInvError";
        }
        // 查询对应容器数据
        ContainerCommand container = containerDao.getContainerByCode(containerCode, ouId);
        if (null == container) {
            // 容器信息不存在
            return "containerNullError";
        }
        // 验证容器Lifecycle是否有效
        if (container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_FORBIDDEN)) {
            // 容器Lifecycle无效
            return "containerLifecycleError";
        }
        // 如果容器Lifecycle为占用 判断是否状态为可用状态
        if (!container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE)) {
            return "containerStatusError";
        }
        // 获取播种墙对应批次周装箱库存
        List<WhSeedingCollection> w = whSeedingCollectionDao.findWhSeedingCollectionByBatchNo(f.getBatch(), ouId);
        if (null != w && w.size() > 0) {
            List<Long> iIds = new ArrayList<Long>();
            for (WhSeedingCollection whSeedingCollection : w) {
                iIds.add(whSeedingCollection.getContainerId());
            }
            List<WhSkuInventory> invList = whSkuInventoryDao.findWhSkuInventoryByInsideContainerIds(ouId, iIds);
            for (WhSkuInventory whSkuInventory : invList) {
                // 修改对应库存进入新的周装箱内
                whSkuInventory.setInsideContainerId(container.getId());
                String uuid = SkuInventoryUuid.invUuid(whSkuInventory);
                whSkuInventory.setUuid(uuid);
                whSkuInventoryDao.saveOrUpdateByVersion(whSkuInventory);
            }
            // 释放播种墙
            f.setBatch(null);
            f.setStatus(String.valueOf(Constants.WH_FACILITY_STATUS_1));
            f.setOperatorId(userId);
            whOutboundFacilityDao.saveOrUpdateByVersion(f);
            insertGlobalLog(GLOBAL_LOG_UPDATE, f, ouId, userId, null, null);
            // 释放原始周装箱状态
            for (Long l : iIds) {
                Container c = containerDao.findByIdExt(l, ouId);
                c.setStatus(ContainerStatus.CONTAINER_STATUS_USABLE);
                c.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
                c.setOperatorId(userId);
                containerDao.saveOrUpdateByVersion(c);
                insertGlobalLog(GLOBAL_LOG_UPDATE, c, ouId, userId, null, null);
            }
            // 占用新周装箱状态
            Container co = containerDao.findByIdExt(container.getId(), ouId);
            co.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            co.setStatus(ContainerStatus.CONTAINER_STATUS_PICKING);
            co.setOperatorId(userId);
            containerDao.saveOrUpdateByVersion(co);
            insertGlobalLog(GLOBAL_LOG_UPDATE, co, ouId, userId, null, null);
        }
        log.info("SeedingManagerImpl.liberateSeedingWall end!");
        return msg;
    }
}
