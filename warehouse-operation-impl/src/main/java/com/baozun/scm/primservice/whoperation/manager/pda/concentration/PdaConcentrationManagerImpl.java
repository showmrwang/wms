package com.baozun.scm.primservice.whoperation.manager.pda.concentration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.baozun.scm.primservice.whoperation.command.odo.wave.RecFacilityPathCommand;
import com.baozun.scm.primservice.whoperation.command.pda.collection.WorkCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhFacilityRecPathCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationExecLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSeedingCollectionLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhTemporaryStorageLocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CollectionStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.constant.OdoLineStatus;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.OperationStatus;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingCollectionLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFacilityRecPathDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhTemporaryStorageLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy.WaveFacilityManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhWorkManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingCollection;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingCollectionLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhDistributionPatternRule;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFacilityRecPath;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollection;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSeedingCollectionLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhTemporaryStorageLocation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.ParamsUtil;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;

@Transactional
@Service("pdaConcentrationManager")
public class PdaConcentrationManagerImpl extends BaseManagerImpl implements PdaConcentrationManager {

    protected static final Logger log = LoggerFactory.getLogger(PdaConcentrationManagerImpl.class);

    @Autowired
    private WhFacilityRecPathDao whFacilityRecPathDao;

    @Autowired
    private WhSeedingCollectionDao whSeedingCollectionDao;

    @Autowired
    private WhSeedingCollectionLineDao whSeedingCollectionLineDao;
    @Autowired
    private WhCheckingCollectionLineDao whCheckingCollectionLineDao;

    @Autowired
    private WhCheckingCollectionDao whCheckingCollectionDao;
    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;

    @Autowired
    private WhWorkDao whWorkDao;

    @Autowired
    private WhOperationDao whOperationDao;

    @Autowired
    private WhDistributionPatternRuleDao whDistributionPatternRuleDao;

    @Autowired
    private WhOdoDao whOdoDao;

    @Autowired
    private WhTemporaryStorageLocationDao whTemporaryStorageLocationDao;

    @Autowired
    private ContainerDao containerDao;

    @Autowired
    private WhLocationDao whLocationDao;

    @Autowired
    private WaveFacilityManagerProxy waveFacilityManagerProxy;

    @Autowired
    private WhOperationExecLineDao whOperationExecLineDao;

    @Autowired
    private WhWorkManager workManager;

    @Autowired
    private WarehouseManager warehouseManager;

    @Autowired
    private WhOdoLineDao whOdoLineDao;

    @Autowired
    private WhWorkLineDao whWorkLineDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertIntoSeedingCollection(String batch, Long workId, Long ouId) {
        List<WhOperationExecLineCommand> execLineCommandList = this.whOperationExecLineDao.findCommandByWorkId(workId, ouId);
        if (null != execLineCommandList && !execLineCommandList.isEmpty()) {
            for (WhOperationExecLineCommand execLineCommand : execLineCommandList) {
                WhSeedingCollection seedingCollection = new WhSeedingCollection();
                seedingCollection.setBatch(batch);
                seedingCollection.setContainerId(execLineCommand.getUseContainerId());
                seedingCollection.setOuId(ouId);
                seedingCollection.setCollectionStatus(CollectionStatus.NEW);
                seedingCollection.setCreateTime(new Date());
                seedingCollection.setLastModifyTime(new Date());
                this.whSeedingCollectionDao.insert(seedingCollection);
                insertIntoSeedingCollectionLine(seedingCollection);
            }
        }
    }

    /**
     * @author lichuan
     * @param batch
     * @param execLineCommandList
     * @param ouId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertIntoSeedingCollection(String batch, Long workId, List<WhOperationExecLineCommand> execLineCommandList, Long ouId) {
        WhWork work = this.whWorkDao.findWorkById(workId, ouId);
        Set<Long> containerIds = new HashSet<Long>();
        if (null != work) {
            if (null != execLineCommandList && !execLineCommandList.isEmpty()) {
                for (WhOperationExecLineCommand execLineCommand : execLineCommandList) {
                    containerIds.add(execLineCommand.getUseContainerId());
                }
            }
            if (null != containerIds && !containerIds.isEmpty()) {
                for (Long containerId : containerIds) {
                    WhSeedingCollection seedingCollection = new WhSeedingCollection();
                    seedingCollection.setBatch(batch);
                    seedingCollection.setContainerId(containerId);
                    seedingCollection.setOuId(ouId);
                    seedingCollection.setCollectionStatus(CollectionStatus.NEW);
                    seedingCollection.setPickingMode(work.getPickingMode());
                    seedingCollection.setCheckingMode(work.getCheckingMode());
                    seedingCollection.setDistributionMode(work.getDistributionMode());
                    seedingCollection.setCreateTime(new Date());
                    seedingCollection.setLastModifyTime(new Date());
                    this.whSeedingCollectionDao.insert(seedingCollection);
                    insertIntoSeedingCollectionLine(seedingCollection);
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertIntoCheckingCollection(String batch, Long workId, Long ouId, WhWorkCommand work) {
        List<WhOperationExecLineCommand> execLineCommandList = this.whOperationExecLineDao.findCommandByWorkId(workId, ouId);
        if (null != execLineCommandList && !execLineCommandList.isEmpty()) {
            for (WhOperationExecLineCommand execLineCommand : execLineCommandList) {
                WhCheckingCollection whCheckingCollection = new WhCheckingCollection();
                whCheckingCollection.setBatch(batch);
                whCheckingCollection.setContainerId(execLineCommand.getUseContainerId());
                whCheckingCollection.setOuId(ouId);
                whCheckingCollection.setOutboundboxCode(execLineCommand.getUseOutboundboxCode());
                whCheckingCollection.setContainerLatticeNo(execLineCommand.getUseContainerLatticeNo());
                whCheckingCollection.setWaveCode(work.getWaveCode());
                whCheckingCollection.setPickingMode(work.getPickingMode());
                whCheckingCollection.setDistributionMode(work.getDistributionMode());
                whCheckingCollection.setCheckingMode(work.getCheckingMode());
                whCheckingCollection.setOuterContainerId(execLineCommand.getUseOuterContainerId());
                whCheckingCollection.setCollectionStatus(CollectionStatus.FINISH);
                this.whCheckingCollectionDao.insert(whCheckingCollection);
            }
        }
    }

    /**
     * @author lichuan
     * @param batch
     * @param execLineCommandList
     * @param ouId
     * @param work
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertIntoCheckingCollection(String batch, List<WhOperationExecLineCommand> execLineCommandList, Long ouId, WhWorkCommand work, Long useId) {
        // 拣货作业执行明细分组
        List<WhOperationExecLineCommand> operationExecLineGroup = new ArrayList<WhOperationExecLineCommand>();
        if (null != execLineCommandList && !execLineCommandList.isEmpty()) {
            for (WhOperationExecLineCommand cmd : execLineCommandList) {
                String line =
                        ParamsUtil.concatParam((null == cmd.getUseOuterContainerId() ? "" : cmd.getUseOuterContainerId().toString()), (null == cmd.getUseContainerLatticeNo() ? "" : cmd.getUseContainerLatticeNo().toString()),
                                (null == cmd.getUseContainerId() ? "" : cmd.getUseContainerId().toString()), cmd.getUseOutboundboxCode());
                boolean isExists = isExistsOperationExecLineGroup(operationExecLineGroup, line);
                if (false == isExists) {
                    operationExecLineGroup.add(cmd);
                }
            }
        }
        if (null != operationExecLineGroup && !operationExecLineGroup.isEmpty()) {
            for (WhOperationExecLineCommand execLineCommand : operationExecLineGroup) {
                WhCheckingCollection whCheckingCollection = new WhCheckingCollection();
                whCheckingCollection.setBatch(batch);
                whCheckingCollection.setContainerId(execLineCommand.getUseContainerId());
                whCheckingCollection.setOuId(ouId);
                whCheckingCollection.setOutboundboxCode(execLineCommand.getUseOutboundboxCode());
                whCheckingCollection.setContainerLatticeNo(execLineCommand.getUseContainerLatticeNo());
                whCheckingCollection.setWaveCode(work.getWaveCode());
                whCheckingCollection.setPickingMode(work.getPickingMode());
                whCheckingCollection.setDistributionMode(work.getDistributionMode());
                whCheckingCollection.setCheckingMode(work.getCheckingMode());
                whCheckingCollection.setOuterContainerId(execLineCommand.getUseOuterContainerId());
                whCheckingCollection.setCollectionStatus(CollectionStatus.FINISH);
                whCheckingCollection.setCreateId(useId);
                whCheckingCollection.setCreateTime(new Date());
                whCheckingCollection.setLastModifyTime(new Date());
                this.whCheckingCollectionDao.insert(whCheckingCollection);
                insertIntoCheckingCollectionLine(whCheckingCollection);
            }
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WorkCollectionCommand createObject(String batch, Long workId, Long ouId, Boolean isLastContainer, Long scanContainerId) {
        log.info("PdaConcentrationManager.createObject() start...");
        log.info("params: batch:{}, workId:{}, ouId{}, isLastContainer:{}, scanContainerId{};", batch, workId, ouId, isLastContainer, scanContainerId);
        WorkCollectionCommand command = new WorkCollectionCommand();
        // TODO 需要odoIds, 最后一个库位位子, 容器列表
        // 通过工作和组织找到批次下的所有拣货工作使用的容器和出库单列表
        command.setOuId(ouId);
        Set<String> containerSet = new HashSet<String>();
        Set<Long> containerIdSet = new HashSet<Long>();
        Set<Long> odoIdSet = new HashSet<Long>();
        List<WhOperationExecLineCommand> execLineCommandList = new ArrayList<WhOperationExecLineCommand>();
        // TODO 修改是否是最后一个容器
        if (null != isLastContainer) {
            command.setIsLastContainer(isLastContainer); // 是否是最后一个容器
        } else {
            // TODO 根据批次找到工作是否完成
            List<WhOperation> operationList = this.whOperationDao.findByBatchAndContainerId(batch, ouId, scanContainerId);
            if (null == operationList || 1 < operationList.size()) {
                throw new BusinessException(ErrorCodes.CONCENTRATION_NO_INFO_RETURN);
            }
            WhOperation operation = operationList.get(0);
            if (OperationStatus.FINISH != operation.getStatus()) {
                // 如果作业状态是未完成 那么是否是最后一个容器为否
                command.setIsLastContainer(false);
            } else {
                command.setIsLastContainer(true);
            }
            isLastContainer = command.getIsLastContainer();
        }
        if (null != workId) {
            execLineCommandList = this.whOperationExecLineDao.findCommandByWorkId(workId, ouId);
        } else {
            // TODO 根据batch和传入容器查找对应的工作下的所有作业执行明细
            execLineCommandList = this.whOperationExecLineDao.findCommandByBatchAndContainer(batch, scanContainerId, ouId);
        }
        // Long lastPickingLocation;
        if (null != execLineCommandList && !execLineCommandList.isEmpty()) {
            command.setAreaId(execLineCommandList.get(0).getWorkAreaId());
            for (WhOperationExecLineCommand execLineCommand : execLineCommandList) {
                containerSet.add(execLineCommand.getUseContainerCode());
                containerIdSet.add(execLineCommand.getUseContainerId());
                odoIdSet.add(execLineCommand.getOdoId());
                if ((null != isLastContainer) && isLastContainer) {
                    // 计算最后一个容器库位
                    log.info("last location id:" + execLineCommand.getFromLocationId());
                    command.setLastLocationId(execLineCommand.getFromLocationId()); // 最后一个容器库位
                }
            }
        }
        List<String> containerList = new ArrayList<String>(containerSet);
        // List<Long> containerIdList = new ArrayList<Long>(containerIdSet);
        List<Long> odoIdList = new ArrayList<Long>(odoIdSet);
        command.setContainerList(containerList); // 容器列表
        command.setOdoIdList(odoIdList); // 出库单列表
        command.setBatch(batch); // 批次号
        log.info("PdaConcentrationManager.createObject() finish...");
        return command;
    }

    @Override
    public void initCache(Long userId, String batch) {
        List<WhFacilityRecPathCommand> rfpList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId, batch);
        if (null != rfpList) {
            cacheManager.remonKeys(CacheConstants.PDA_CACHE_COLLECTION_REC + userId);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WorkCollectionCommand recommendSeedingWall(WorkCollectionCommand workCollectionCommand) {
        log.info("PdaConcentrationManager.recommendSeedingWall start...");
        // TODO 根据workId查出批次号以及是否是播种模式
        WhWorkCommand work = this.whWorkDao.findWorkByWorkCode(workCollectionCommand.getWorkCode(), workCollectionCommand.getOuId());
        WhOdo odo = new WhOdo();
        odo.setWaveCode(work.getWaveCode());
        odo.setOuId(workCollectionCommand.getOuId());
        List<WhOdo> odoList = whOdoDao.findListByParam(odo);
        if (null == odoList || odoList.isEmpty()) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        WhDistributionPatternRule whDistributionPatternRule = this.whDistributionPatternRuleDao.findByOdoIdAndOuId(odoList.get(0).getId(), workCollectionCommand.getOuId());
        Integer pickingMode = whDistributionPatternRule.getPickingMode(); // 拣货模式
        String batch = work.getBatch();
        workCollectionCommand.setBatch(batch);
        log.info("pickingMode:{}, waveCode:{}, workId:{}, batch:{}", pickingMode, work.getWaveCode(), work.getId(), batch);
        // Long userId = workCollectionCommand.getUserId();
        // Boolean flag = false;
        Boolean hasRec = false;
        // TODO 判断是否是播种模式
        if (Constants.PICKING_MODE_SEED.equals(work.getPickingMode())) {
            hasRec = initRecFacilityPath(workCollectionCommand);
        }
        // TODO flag
        workCollectionCommand.setIsSuccess(hasRec);
        log.info("PdaConcentrationManager.recommendSeedingWall finish...");
        return workCollectionCommand;
    }

    /**
     * [业务方法] 初始化推荐逻辑缓存
     * 
     * @param workCollectionCommand
     * @return [false:容器列表为空]/[true:容器列表不为空]
     */
    private Boolean initRecFacilityPath(WorkCollectionCommand workCollectionCommand) {
        log.info("PdaConcentrationManager.initRecFacilityPath start...");
        Boolean hasSuccess = false;
        Long userId = workCollectionCommand.getUserId();
        Long ouId = workCollectionCommand.getOuId();
        String batch = workCollectionCommand.getBatch();
        List<String> containerList = workCollectionCommand.getContainerList();
        List<String> failContainerList = new ArrayList<String>();
        RecFacilityPathCommand recFacilityPath = new RecFacilityPathCommand();
        for (String containerCode : containerList) {
            log.info("batch:{}, containerCode:{}, areaId:{}, isLastContainer:{}", batch, containerCode, workCollectionCommand.getAreaId(), workCollectionCommand.getIsLastContainer());
            recFacilityPath.setOuId(ouId);
            recFacilityPath.setBatch(batch);
            recFacilityPath.setContainerCode(containerCode);
            recFacilityPath.setOdoIdList(workCollectionCommand.getOdoIdList());
            recFacilityPath.setPickingMode(Constants.PICKING_MODE_SEED);
            recFacilityPath.setAreaId(workCollectionCommand.getAreaId());
            recFacilityPath.setLastContainer(workCollectionCommand.getIsLastContainer());
            // TODO 调用播种墙推荐逻辑
            RecFacilityPathCommand command = new RecFacilityPathCommand();
            try {
                log.info("start recommend facility...");
                command = waveFacilityManagerProxy.matchOutboundFacility(recFacilityPath);
                log.info("finish recommend facility...");
            } catch (Exception e) {
                log.error("", e);
                command.setStatus(0);
            }
            if (1 == command.getStatus()) {
                // TODO 应该是是否推荐成功
                hasSuccess = true;
                WhFacilityRecPathCommand wrpc = new WhFacilityRecPathCommand();
                List<WhFacilityRecPathCommand> whFacilityRecPathCommand = whFacilityRecPathDao.getRecommendResultByContainerCode(containerCode, batch, ouId);
                if (null != whFacilityRecPathCommand && !whFacilityRecPathCommand.isEmpty()) {
                    wrpc = whFacilityRecPathCommand.get(0);
                }
                List<WhFacilityRecPathCommand> rfpList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId, batch);
                if (null == rfpList || rfpList.isEmpty()) {
                    rfpList = new ArrayList<WhFacilityRecPathCommand>();
                    rfpList.add(wrpc);
                } else {
                    rfpList.add(wrpc);
                }
                cacheManager.setMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId, batch, rfpList, CacheConstants.CACHE_ONE_YEAR);
            } else {
                failContainerList.add(containerCode);
                containerList.remove(containerCode);
                if (containerList.isEmpty()) {
                    break;
                }
            }
        }
        // 补偿机制:当有成功获取推荐路径 才会有补偿机制
        if (hasSuccess && workCollectionCommand.getIsCompensation()) {
            List<String> failList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC_FAIL + userId, batch);
            // if (null != failList) {
            // cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC_FAIL + userId,
            // batch);
            // }
            cacheManager.setMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC_FAIL + userId, batch, failContainerList, CacheConstants.CACHE_ONE_YEAR);
        }
        if (containerList.isEmpty()) {
            // 容器列表为空
            log.info("PdaConcentrationManager.initRecFacilityPath finish...");
            return false;
        }
        log.info("PdaConcentrationManager.initRecFacilityPath finish...");
        return true;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String findTargetPos(WorkCollectionCommand command, String cacheKey) {
        Boolean isRecPath = command.getIsRecPath();
        int containerQty = (null == command.getContainerList()) ? 0 : command.getContainerList().size();
        WhFacilityRecPathCommand rfp = this.findRecFacilityPathCommandByIndex(command, cacheKey);
        if (null == rfp) {
            return Constants.FINISH;
        }
        String targetPosUrl = "";
        String targetPos = "";
        String targetPosCheck = "";
        // String containerCode = rfp.getContainerCode();
        // String batch = command.getBatch();
        // Long ouId = command.getOuId();
        // WhFacilityRecPath whFacilityRecPath =
        // whFacilityRecPathDao.findWhFacilityRecPathByBatchAndContainer(batch, containerCode,
        // ouId);
        String transitLocationCode = rfp.getTransitLocationCode(); // 中转库位编码
        String transitLocationCheckCode = rfp.getTransitLocationCheckCode(); // 中转库位校验码
        String temporaryStorageLocationCode = rfp.getTemporaryStorageLocationCode(); // 暂存区域库位编码
        String temporaryStorageLocationCheckCode = rfp.getTemporaryStorageLocationCheckCode(); // 暂存区域库位校验码
        String seedingwallCode = rfp.getSeedingwallCode(); // 播种墙编码
        String seedingwallCheckCode = rfp.getSeedingwallCheckCode(); // 播种墙校验码
        if (StringUtils.hasText(transitLocationCode) && CacheConstants.PDA_CACHE_COLLECTION_REC.equals(cacheKey)) {
            // 有中转库位且是推荐播种墙逻辑
            targetPosUrl = (!command.getIsScanCheckCode()) ? Constants.TARGET_5 : Constants.TARGET_6;
            // targetPos = (!command.getIsScanCheckCode()) ? transitLocationCode :
            // transitLocationCheckCode;
            targetPos = transitLocationCode;
            targetPosCheck = transitLocationCheckCode;
        } else {
            if (!StringUtils.hasText(seedingwallCode)) {
                targetPosUrl = (!command.getIsScanCheckCode()) ? Constants.TARGET_3 : Constants.TARGET_4;
                // targetPos = (!command.getIsScanCheckCode()) ? temporaryStorageLocationCode :
                // temporaryStorageLocationCheckCode;
                targetPos = temporaryStorageLocationCode;
                targetPosCheck = temporaryStorageLocationCheckCode;
            } else {
                // isRecPath: 如果是集货操作, 设为false; 如果是推荐操作, 设为true
                int flag = checkSeedingWallCapacity(seedingwallCode, rfp.getBatch(), rfp.getOuId(), containerQty, isRecPath);
                if (Constants.SEEDING_WALL_SUFFICIENT == flag) {
                    Boolean isMoveAllSuccess = MoveAllContainer(command, cacheKey);
                    if (isMoveAllSuccess) {
                        this.compensationCache(command);
                        return Constants.FINISH;
                    }
                } else if (Constants.SEEDING_WALL_MOVE_ONE == flag) {
                    // 按箱移动到播种墙
                    targetPosUrl = (!command.getIsScanCheckCode()) ? Constants.TARGET_1 : Constants.TARGET_2;
                    // targetPos = (!command.getIsScanCheckCode()) ? seedingwallCode :
                    // seedingwallCheckCode;
                    targetPos = seedingwallCode;
                    targetPosCheck = seedingwallCheckCode;
                } else {
                    // 按箱移动到暂存库位
                    targetPosUrl = (!command.getIsScanCheckCode()) ? Constants.TARGET_3 : Constants.TARGET_4;
                    // targetPos = (!command.getIsScanCheckCode()) ? temporaryStorageLocationCode :
                    // temporaryStorageLocationCheckCode;
                    targetPos = temporaryStorageLocationCode;
                    targetPosCheck = temporaryStorageLocationCheckCode;
                }
            }
        }
        // TODO 提示容器号 而不是编码
        if (CacheConstants.PDA_CACHE_COLLECTION_REC.equals(cacheKey)) {
            return targetPosUrl + "$" + rfp.getContainerCode() + "$" + targetPos + "$" + targetPosCheck;
        } else {
            return targetPosUrl + "$" + rfp.getContainerCode() + "$" + targetPos + "$" + targetPosCheck + "$" + rfp.getBatch();
        }
    }

    /**
     * [通用方法] 通过索引取得播种墙推荐路径
     * 
     * @param workCollectionCommand
     * @return
     */
    private WhFacilityRecPathCommand findRecFacilityPathCommandByIndex(WorkCollectionCommand workCollectionCommand, String cacheKey) {
        Long userId = workCollectionCommand.getUserId();
        String batch = "";
        if (CacheConstants.PDA_CACHE_COLLECTION_REC.equals(cacheKey)) {
            // 推荐路径
            batch = workCollectionCommand.getBatch();
        } else {
            // 集货操作
            // int index = workCollectionCommand.getIndex();
            List<Long> containerList = cacheManager.getMapObject(cacheKey, userId.toString());
            if (null == containerList || containerList.isEmpty()) {
                throw new BusinessException("no container found");
            }
            Long containerId = containerList.get(0);
            WhSeedingCollection sc = whSeedingCollectionDao.findSeedingCollectionByContainerId(containerId, null, workCollectionCommand.getOuId());
            if (null == sc) {
                throw new BusinessException("no seeding collection data found");
            }
            batch = sc.getBatch();
        }
        List<WhFacilityRecPathCommand> rfpList = cacheManager.getMapObject(cacheKey + userId, batch);
        if (null == rfpList || rfpList.isEmpty()) {
            return null;
        } else {
            WhFacilityRecPathCommand command = rfpList.get(0);
            // if (CacheConstants.PDA_CACHE_COLLECTION_REC.equals(cacheKey)) {
            // rfpList = new ArrayList<WhFacilityRecPathCommand>();
            // cacheManager.setMapObject(cacheKey + userId.toString(), batch, rfpList,
            // CacheConstants.CACHE_ONE_DAY);
            // }
            return command;
        }
        // if (null != rfpList && !rfpList.isEmpty()) {
        // if (index > (rfpList.size() - 1)) {
        // return null;
        // }
        // WhFacilityRecPathCommand command = rfpList.get(workCollectionCommand.getIndex());
        // // cacheManager.remonKeys(CacheConstants.PDA_CACHE_COLLECTION_REC + userId);
        // return command;
        // }
        // return null;
    }

    /**
     * [业务方法] 判断推荐的播种墙剩余容量是否满足
     * 
     * @param seedingwallCode
     * @param batch
     * @param ouId
     * @return
     */
    private int checkSeedingWallCapacity(String seedingwallCode, String batch, Long ouId, int containerQty, Boolean isRecPath) {
        WhOutboundFacility whOutboundFacility = this.whOutboundFacilityDao.findByCodeAndOuId(seedingwallCode, ouId);
        Integer facilityUpperLimit = whOutboundFacility.getFacilityUpperLimit(); // 设施容量上限
        if (null == facilityUpperLimit) {
            throw new BusinessException("no upper limit");
        }
        WhSeedingCollectionCommand whSeedingCollectionCommand = new WhSeedingCollectionCommand();
        whSeedingCollectionCommand.setBatch(batch);
        whSeedingCollectionCommand.setOuId(ouId);
        whSeedingCollectionCommand.setFacilityCode(seedingwallCode);
        whSeedingCollectionCommand.setCollectionStatus(CollectionStatus.FINISH);
        whSeedingCollectionCommand.setFacilityId(whOutboundFacility.getId());
        int occupied = this.whSeedingCollectionDao.countCapacityByParamExt(whSeedingCollectionCommand); // 播种墙已占有容量(集货表状态为非完成)
        int capacity = facilityUpperLimit - occupied;
        if (capacity > 0) {
            // 播种墙容量足够
            int rest = capacity - containerQty;
            if (isRecPath && rest >= 0) {
                // 播种墙剩余容量满足一次移动所有容器到播种墙
                return Constants.SEEDING_WALL_SUFFICIENT;
            } else {
                // 每次移动一个容器到播种墙
                return Constants.SEEDING_WALL_MOVE_ONE;
            }
        } else {
            // 播种墙容量不足
            return Constants.SEEDING_WALL_NOT_SUFFICIENT;
        }
    }

    /**
     * [业务方法] 移动全部容器
     * 
     * @param workCollectionCommand
     * @return
     */
    private Boolean MoveAllContainer(WorkCollectionCommand workCollectionCommand, String cacheKey) {
        List<String> containerList = workCollectionCommand.getContainerList();
        if (null != containerList && !containerList.isEmpty()) {
            for (int i = 0; i < containerList.size(); i++) {
                workCollectionCommand.setIndex(i);
                if (null == workCollectionCommand.getTargetType()) {
                    workCollectionCommand.setTargetType(Constants.SEEDING_WALL);
                }
                Boolean isRecordSuccess = this.recordSeedingCollection(workCollectionCommand, cacheKey);
                if (!isRecordSuccess) {
                    throw new BusinessException("记录容器集货信息失败");
                }
                Boolean isMoveSuccess = this.moveContainer(workCollectionCommand, cacheKey);
                if (!isMoveSuccess) {
                    throw new BusinessException("移动容器失败");
                }
                this.clearTemporaryStorageLocation(workCollectionCommand.getBatch(), workCollectionCommand.getOuId());
                // 更改出库单状态
                this.updateOdoStatus(workCollectionCommand.getContainerId(), workCollectionCommand.getOuId());
            }
        }
        return true;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean checkAndMoveContainer(WorkCollectionCommand workCollectionCommand, String cacheKey) {
        if (null == workCollectionCommand) {
            throw new BusinessException("object null");
        }
        Boolean isSuccess = true;
        String inputContainerCode = workCollectionCommand.getInputContainerCode();
        String containerCode = workCollectionCommand.getContainerCode();
        if (!containerCode.equals(inputContainerCode)) {
            isSuccess = false;
            throw new BusinessException("容器不匹配");
        }
        if (null == workCollectionCommand.getContainerId()) {

            ContainerCommand container = containerDao.getContainerByCode(inputContainerCode, workCollectionCommand.getOuId());
            if (null == container) {
                isSuccess = false;
                throw new BusinessException("找不到容器");
            }
            workCollectionCommand.setContainerId(container.getId());
        }
        Boolean isRecordSuccess = this.recordSeedingCollection(workCollectionCommand, cacheKey);
        if (!isRecordSuccess) {
            isSuccess = false;
            throw new BusinessException("记录容器集货信息失败");
        }
        Boolean isMoveSuccess = this.moveContainer(workCollectionCommand, cacheKey);
        if (!isMoveSuccess) {
            isSuccess = false;
            throw new BusinessException("移动容器失败");
        }

        // 5.判断暂存库位,没有就释放
        String batch = workCollectionCommand.getBatch();
        if (workCollectionCommand.getTargetType() == Constants.SEEDING_WALL) {
            Long ouId = workCollectionCommand.getOuId();
            this.clearTemporaryStorageLocation(batch, ouId);
        }
        try {
            updateOdoStatus(workCollectionCommand.getContainerId(), workCollectionCommand.getOuId());
        } catch (Exception e) {}
        if (CacheConstants.PDA_CACHE_COLLECTION_REC.equals(cacheKey)) {
            List<WhFacilityRecPathCommand> rfpList = new ArrayList<WhFacilityRecPathCommand>();
            log.info("release rec cache: batch:[{}]", batch);
            cacheManager.setMapObject(cacheKey + workCollectionCommand.getUserId().toString(), batch, rfpList, CacheConstants.CACHE_ONE_DAY);
        }
        return isSuccess;
    }

    private boolean clearTemporaryStorageLocation(String batch, Long ouId) {
        List<WhWorkCommand> workList = whWorkDao.findWorkByBatch(batch, ouId);
        for (WhWorkCommand work : workList) {
            if (work.getStatus() != WorkStatus.FINISH) {
                return false;
            }
        }
        int count = whSeedingCollectionDao.checkBatchIsAllIntoSeedingWall(batch, ouId);
        if (count == 0) {
            List<WhTemporaryStorageLocation> storageLocationList = whTemporaryStorageLocationDao.findTsLocationByBatch(batch, ouId);
            for (WhTemporaryStorageLocation ts : storageLocationList) {
                ts.setBatch(null);
                ts.setStatus(1);
                whTemporaryStorageLocationDao.saveOrUpdateByVersion(ts);
            }
            return true;
        }
        return false;
    }

    private void updateOdoStatus(Long containerId, Long ouId) {
        log.info("updateOdoStatus start...");
        log.info("param: containerId:[{}], ouId:[{}]", containerId, ouId);
        // 根据容器查找集货信息
        WhSeedingCollection seedingCollection = this.whSeedingCollectionDao.findSeedingCollectionByContainerId(containerId, null, ouId);
        // 集货终点标识, 到播种墙上为完成
        boolean flag = seedingCollection.getFacilityId() == null ? false : true;
        if (null != seedingCollection) {
            Long scId = seedingCollection.getId();
            // 查找集货明细
            List<WhSeedingCollectionLine> seedingCollectionLineList = this.whSeedingCollectionLineDao.findByScIdAndOuId(scId, ouId);
            if (null != seedingCollectionLineList && !seedingCollectionLineList.isEmpty()) {
                for (WhSeedingCollectionLine seedingCollectionLine : seedingCollectionLineList) {
                    // 更新每个集货明细对应的出库单状态为集货完成
                    Long odoId = seedingCollectionLine.getOdoId();
                    Long odoLineId = seedingCollectionLine.getOdoLineId();
                    log.info("odoId:[{}], odoLineId:[{}]", odoId, odoLineId);
                    WhOdoLine whOdoLine = whOdoLineDao.findOdoLineById(odoLineId, ouId);
                    if (null != whOdoLine) {
                        if (OdoLineStatus.PICKING_FINISH.equals(whOdoLine.getOdoLineStatus()) || OdoLineStatus.COLLECTION.equals(whOdoLine.getOdoLineStatus())) {
                            if (flag) {
                                whOdoLine.setOdoLineStatus(OdoLineStatus.COLLECTION_FINISH);
                            } else {
                                whOdoLine.setOdoLineStatus(OdoLineStatus.COLLECTION);
                            }
                            whOdoLineDao.saveOrUpdateByVersion(whOdoLine);
                        }
                    }
                    // 查找出库单下所有出库单状态是否为集货完成
                    Long cnt = whOdoLineDao.findCntByOdoIdAndOuId(odoId, ouId);
                    // 更新出库单状态为集货完成
                    WhOdo whOdo = whOdoDao.findByIdOuId(odoId, ouId);
                    if (null == whOdo) {
                        log.error("odo is null, odoId:" + odoId);
                        throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                    }
                    if (0 == cnt) {
                        // 全部集货完成, 更改出库单头的状态为完成
                        log.info("all odo line finish collection");
                        if (!OdoStatus.COLLECTION_FINISH.equals(whOdo.getOdoStatus()) && !OdoStatus.SEEDING.equals(whOdo.getOdoStatus()) && !OdoStatus.SEEDING_FINISH.equals(whOdo.getOdoStatus()) && !OdoStatus.CHECKING.equals(whOdo.getOdoStatus())
                                && !OdoStatus.CHECKING_FINISH.equals(whOdo.getOdoStatus()) && !OdoStatus.WEIGHING.equals(whOdo.getOdoStatus()) && !OdoStatus.WEIGHING_FINISH.equals(whOdo.getOdoStatus()) && !OdoStatus.HANDOVER.equals(whOdo.getOdoStatus())
                                && !OdoStatus.HANDOVER_FINISH.equals(whOdo.getOdoStatus()) && !OdoStatus.CANCEL.equals(whOdo.getOdoStatus())) {
                            whOdo.setOdoStatus(OdoStatus.COLLECTION_FINISH);
                            whOdo.setLagOdoStatus(OdoStatus.COLLECTION_FINISH);
                            whOdoDao.saveOrUpdateByVersion(whOdo);
                        } else if (!OdoStatus.COLLECTION_FINISH.equals(whOdo.getLagOdoStatus()) && !OdoStatus.CANCEL.equals(whOdo.getLagOdoStatus())) {
                            whOdo.setLagOdoStatus(OdoStatus.COLLECTION_FINISH);
                            whOdoDao.saveOrUpdateByVersion(whOdo);
                        }
                    } else {
                        // 出库单明细还有未集货完成的状态, 更改出库单头的状态为集货中
                        if (!OdoStatus.COLLECTION.equals(whOdo.getOdoStatus()) && !OdoStatus.COLLECTION_FINISH.equals(whOdo.getOdoStatus()) && !OdoStatus.SEEDING.equals(whOdo.getOdoStatus()) && !OdoStatus.SEEDING_FINISH.equals(whOdo.getOdoStatus())
                                && !OdoStatus.CHECKING.equals(whOdo.getOdoStatus()) && !OdoStatus.CHECKING_FINISH.equals(whOdo.getOdoStatus()) && !OdoStatus.WEIGHING.equals(whOdo.getOdoStatus()) && !OdoStatus.WEIGHING_FINISH.equals(whOdo.getOdoStatus())
                                && !OdoStatus.HANDOVER.equals(whOdo.getOdoStatus()) && !OdoStatus.HANDOVER_FINISH.equals(whOdo.getOdoStatus()) && !OdoStatus.CANCEL.equals(whOdo.getOdoStatus())) {
                            whOdo.setOdoStatus(OdoStatus.COLLECTION);
                            whOdoDao.saveOrUpdateByVersion(whOdo);
                        }
                    }
                }
            }
        }
        log.info("updateOdoStatus finish...");
    }

    /**
     * [业务方法] 记录容器集货信息
     * 
     * @param workCollectionCommand
     * @return
     */
    private Boolean recordSeedingCollection(WorkCollectionCommand workCollectionCommand, String cacheKey) {
        WhFacilityRecPathCommand command = this.findRecFacilityPathCommandByIndex(workCollectionCommand, cacheKey);
        if (null != command) {
            // 调用一个方法 kai.zhu
            this.updateContainerToDestination(command, workCollectionCommand.getTargetType(), workCollectionCommand.getOuId());

            /*
             * Long containerId = workCollectionCommand.getContainerId(); Long ouId =
             * workCollectionCommand.getOuId(); WhSeedingCollection whSeedingCollection = new
             * WhSeedingCollection();
             * whSeedingCollection.setBatch(workCollectionCommand.getBatch());
             * whSeedingCollection.setOuId(ouId); //
             * whSeedingCollection.setCollectionStatus(CollectionStatus.SEEDING); //
             * whSeedingCollection.setCollectionStatus(CollectionStatus.NEW.toString());
             * whSeedingCollection.setContainerId(containerId); List<WhSeedingCollection> scList =
             * whSeedingCollectionDao.findListByParam(whSeedingCollection); if (null == scList ||
             * scList.isEmpty()) { throw new BusinessException("no seeding collection"); }
             * whSeedingCollection = scList.get(0); Integer status =
             * whSeedingCollection.getCollectionStatus(); if (CollectionStatus.NEW.equals(status) ||
             * CollectionStatus.TRANSFER.equals(status) ||
             * CollectionStatus.TEMPORARY_STORAGE.equals(status)) { //
             * whSeedingCollection.setId(1L); Integer targetType =
             * workCollectionCommand.getTargetType(); switch (targetType) { case
             * Constants.SEEDING_WALL: whSeedingCollection.setFacilityId(command.getFacilityId());
             * whSeedingCollection.setLocationId(null);
             * whSeedingCollection.setTemporaryLocationId(null);
             * whSeedingCollection.setCollectionStatus(CollectionStatus.TO_SEED); // @Gianni
             * 2017-06-23 取消更新推荐路径状态 // updateRecPath(workCollectionCommand.getBatch(), //
             * workCollectionCommand.getContainerCode(), ouId); break; case
             * Constants.TEMPORARY_STORAGE_LOCATION: whSeedingCollection.setLocationId(null);
             * whSeedingCollection.setTemporaryLocationId(command.getTemporaryStorageLocationId());
             * whSeedingCollection.setCollectionStatus(CollectionStatus.TEMPORARY_STORAGE); break;
             * case Constants.TRANSIT_LOCATION: // TODO 接口需要添加
             * whSeedingCollection.setTemporaryLocationId(null);
             * whSeedingCollection.setLocationId(command.getTransitLocationId());
             * whSeedingCollection.setCollectionStatus(CollectionStatus.TRANSFER); break; default:
             * throw new BusinessException("error"); } int cnt =
             * this.whSeedingCollectionDao.update(whSeedingCollection); if (0 >= cnt) { return
             * false; }
             * 
             * } else { throw new BusinessException("状态不符合"); }
             */
        }
        return true;
    }

    /**
     * [业务方法] 更新推荐路径状态
     * 
     * @param batch
     * @param containerCode
     * @param ouId
     */
    private void updateRecPath(String batch, String containerCode, Long ouId) {
        WhFacilityRecPath path = this.whFacilityRecPathDao.findWhFacilityRecPathByBatchAndContainer(batch, containerCode, ouId);
        if (null != path) {
            path.setStatus(2);
            int cnt = this.whFacilityRecPathDao.update(path);
            if (0 >= cnt) {
                throw new BusinessException("更新推荐路径表状态失败");
            }
        }
    }


    /**
     * [业务方法] 移动容器
     * 
     * @param workCollectionCommand
     * @return
     */
    private Boolean moveContainer(WorkCollectionCommand workCollectionCommand, String cacheKey) {
        WhFacilityRecPathCommand command = this.findRecFacilityPathCommandByIndex(workCollectionCommand, cacheKey);
        // 引用一套方法 kai.zhu
        this.updateContainerSkuInventory(command, workCollectionCommand.getTargetType(), workCollectionCommand.getUserId(), workCollectionCommand.getOuId());

        /*
         * Integer targetType = workCollectionCommand.getTargetType(); WhSkuInventory skuInventory =
         * new WhSkuInventory(); skuInventory.setInsideContainerId(command.getContainerId());
         * skuInventory.setOuId(workCollectionCommand.getOuId()); List<WhSkuInventory> invList =
         * this.whSkuInventoryDao.findListByParam(skuInventory); if (null != command) { if (null !=
         * invList && !invList.isEmpty()) { Boolean isMove = false; for (WhSkuInventory inv :
         * invList) { switch (targetType) { case Constants.SEEDING_WALL: isMove = true;
         * inv.setLocationId(null); inv.setTemporaryLocationId(null); break; case
         * Constants.TEMPORARY_STORAGE_LOCATION: isMove = true; inv.setLocationId(null);
         * inv.setTemporaryLocationId(command.getTemporaryStorageLocationId()); break; case
         * Constants.TRANSIT_LOCATION: isMove = true; // TODO 接口需要添加
         * inv.setTemporaryLocationId(null); inv.setLocationId(command.getTransitLocationId());
         * break; default: break; } if (isMove) { try { inv.setUuid(SkuInventoryUuid.invUuid(inv));
         * } catch (Exception e) { e.printStackTrace(); return false; } int cnt =
         * this.whSkuInventoryDao.saveOrUpdateByVersion(inv); if (0 >= cnt) { return false; } } } }
         * }
         */
        return true;
    }

    @Override
    public void cleanCache(WorkCollectionCommand workCollectionCommand) {
        Long userId = workCollectionCommand.getUserId();
        String batch = workCollectionCommand.getBatch();
        cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC + userId, batch);
    }

    @Override
    public void compensationCache(WorkCollectionCommand workCollectionCommand) {
        workCollectionCommand.setRestart(true);
        Long userId = workCollectionCommand.getUserId();
        String batch = workCollectionCommand.getBatch();
        List<String> containerList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC_FAIL + userId, batch);
        if (null != containerList && !containerList.isEmpty()) {
            workCollectionCommand.setContainerList(containerList);
            workCollectionCommand.setIsCompensation(false);
            initRecFacilityPath(workCollectionCommand);
        }
    }

    /**
     * 出库集货-获取系统推荐暂存库位
     * 
     * @return
     */
    @Override
    public WhTemporaryStorageLocationCommand getRecommendTemporaryStorageLocation(Long ouId) {
        // 找占用或者正在播种且对应暂存库位有容器的播种墙
        List<WhOutboundFacilityCommand> facilityList = whOutboundFacilityDao.getSeedingFacility(ouId);
        if (null == facilityList || facilityList.isEmpty()) {
            return null;
        }
        WhOutboundFacilityCommand facility = null;
        for (WhOutboundFacilityCommand command : facilityList) {
            Long fid = command.getId();
            String batch = command.getBatch();
            // 获取正在播种墙上播种的数量
            Integer count = whSeedingCollectionDao.getSeedingNumFromFacility(fid, batch, ouId);
            // 判断是否有已占用未移动的播种墙
            if (count == 0) {
                facility = command;
                break;
            }
            command.setSeedingCount(count);
        }
        if (null != facility) {
            return getTemporaryStorageLocationBySeedingWall(facility);
        }
        // 循环播种墙计算达到容量下限的播种墙
        for (WhOutboundFacilityCommand command : facilityList) {
            Integer lowerLimit = command.getFacilityLowerLimit(); // 播种工作数量下限
            Integer seedingCount = command.getSeedingCount(); // 正在播种数
            if (seedingCount.intValue() < lowerLimit.intValue()) {
                if (null == facility) {
                    facility = command;
                    continue;
                }
                Integer count = lowerLimit - seedingCount;
                if (count.intValue() > facility.getFacilityLowerLimit().intValue() - facility.getSeedingCount().intValue()) {
                    facility = command;
                }
            }
        }
        if (null != facility) {
            return getTemporaryStorageLocationBySeedingWall(facility);
        }
        // 循环播种墙计算容量没有达到上限的播种墙
        for (WhOutboundFacilityCommand command : facilityList) {
            Integer upperLimit = command.getFacilityUpperLimit(); // 播种工作数量上限
            Integer seedingCount = command.getSeedingCount(); // 正在播种数
            if (seedingCount.intValue() < upperLimit.intValue()) {
                if (null == facility) {
                    facility = command;
                    continue;
                }
                Integer count = upperLimit - seedingCount;
                if (count.intValue() > facility.getFacilityUpperLimit().intValue() - facility.getSeedingCount().intValue()) {
                    facility = command;
                }
            }
        }
        if (null != facility) {
            return getTemporaryStorageLocationBySeedingWall(facility);
        }
        return null;
    }

    private WhTemporaryStorageLocationCommand getTemporaryStorageLocationBySeedingWall(WhOutboundFacilityCommand facility) {
        WhTemporaryStorageLocationCommand command = whTemporaryStorageLocationDao.getTemporaryStorageLocationBySeedingWall(facility);
        return command;
    }

    /**
     * 检查容器号对应的位置是否存在
     */
    @Override
    public WhSeedingCollectionCommand checkContainerInWhere(String containerCode, Integer type, Long ouId) {
        if (StringUtils.isEmpty(type)) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
        if (null == seedingCollection) {
            throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_DATA_NULL_ERROR, new Object[] {containerCode});
        }
        switch (type) {
            case Constants.TEMPORARY_STORAGE_LOCATION:
                if (null == seedingCollection.getTemporaryLocationId()) {
                    throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_NOT_IN_TEMPORARYLOCATION, new Object[] {containerCode});
                }
                break;
            case Constants.TRANSIT_LOCATION:
                if (null == seedingCollection.getLocationId()) {
                    throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_NOT_IN_LOCATION, new Object[] {containerCode});
                }
                break;
            default:
                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return seedingCollection;
    }

    /**
     * 判断当前容器是否有推荐结果
     */
    @Override
    public WhFacilityRecPathCommand checkContainerHaveRecommendResult(String cacheKey, String containerCode, String batch, Long userId, Long ouId) {
        // cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(),
        // batch);
        if (StringUtils.isEmpty(containerCode)) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        WhFacilityRecPathCommand rec = null;
        List<WhFacilityRecPathCommand> recList = whFacilityRecPathDao.getRecommendResultByContainerCode(containerCode, batch, ouId);
        if (null != recList && !recList.isEmpty()) {
            rec = recList.get(0);
        }
        if (null == rec) {
            throw new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_PATH_ERROR);
        }
        List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(cacheKey + userId.toString(), batch);
        if (null == recPathList) {
            recPathList = new ArrayList<WhFacilityRecPathCommand>();
        }
        if (!recPathList.isEmpty()) {
            for (WhFacilityRecPathCommand recPath : recPathList) {
                if (recPath.getContainerCode().equals(containerCode)) {
                    throw new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_RESULT_REPEAT, new Object[] {containerCode});
                }
                if (!recPath.getBatch().equals(batch)) {
                    throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_BATCH_DIFFERENCE, new Object[] {containerCode});
                }
            }
        }
        recPathList.add(0, rec);
        cacheManager.setMapObject(cacheKey + userId.toString(), batch, recPathList, CacheConstants.CACHE_ONE_DAY);
        List<Long> containerList = cacheManager.getMapObject(cacheKey, userId.toString());
        if (null == containerList || containerList.isEmpty()) {
            containerList = new ArrayList<Long>();
        }
        containerList.add(rec.getContainerId());
        cacheManager.setMapObject(cacheKey, userId.toString(), containerList, CacheConstants.CACHE_ONE_DAY);
        return rec;
    }

    /**
     * 判断是否达到可携带容量数量限制且小于播种墙容器上限
     */
    @Override
    public boolean checkContainerQtyLimit(Long facilityId, Integer carryQty, Integer containerQty, Integer upperLimit, String batch, Long ouId) {
        Integer seedingCount = whSeedingCollectionDao.getSeedingNumFromFacility(facilityId, batch, ouId);
        if (carryQty.intValue() == containerQty.intValue() || (carryQty.intValue() + seedingCount.intValue()) == upperLimit.intValue()) {
            return true;
        }
        return false;
    }

    @Override
    public WhFacilityRecPathCommand popRecommendResultListHead(String batch, Long userId) {
        WhFacilityRecPathCommand rec = null;
        List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
        if (null != recPathList && !recPathList.isEmpty()) {
            rec = recPathList.get(0);
        }
        return rec;
    }

    @Override
    public WhFacilityRecPathCommand popManualRecommendResultListHead(Long userId) {
        WhFacilityRecPathCommand rec = null;
        List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
        if (null != recPathList && !recPathList.isEmpty()) {
            rec = recPathList.get(0);
        }
        return rec;
    }

    /**
     * 判断小批次是否全部移动到播种墙
     */
    @Override
    public boolean checkBatchIsAllIntoSeedingWall(String batch, Long userId, Long ouId) {
        return this.clearTemporaryStorageLocation(batch, ouId);
    }

    /**
     * 记录容器到播种墙上集货信息
     */
    @Override
    public void updateContainerToDestination(WhFacilityRecPathCommand rec, Integer destinationType, Long ouId) {
        Long containerId = rec.getContainerId();
        String batch = rec.getBatch();
        String containerCode = rec.getContainerCode();
        if (null == containerId || StringUtils.isEmpty(batch) || StringUtils.isEmpty(containerCode)) {
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        WhSeedingCollection collection = whSeedingCollectionDao.findSeedingCollectionByContainerId(containerId, batch, ouId);
        Integer status = collection.getCollectionStatus();
        if (!CollectionStatus.NEW.equals(status) && !CollectionStatus.TRANSFER.equals(status) && !CollectionStatus.TEMPORARY_STORAGE.equals(status)) {
            throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_STATUS_ERROR);
        }
        // boolean flag = false;
        if (destinationType == Constants.SEEDING_WALL) {
            // 目标移到播种墙
            Long facilityId = rec.getFacilityId();
            if (null == facilityId) {
                throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
            collection.setFacilityId(facilityId);
            collection.setTemporaryLocationId(null);
            collection.setLocationId(null);
            collection.setCollectionStatus(CollectionStatus.TO_SEED);
            // 路径推荐结果状态修改为完成
            whFacilityRecPathDao.updateStatusToFinish(batch, containerCode, ouId);
            // flag = true;
        } else if (destinationType == Constants.TEMPORARY_STORAGE_LOCATION) {
            // 目标移到暂存库位
            Long temporaryStorageLocationId = rec.getTemporaryStorageLocationId();
            if (null == temporaryStorageLocationId) {
                throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
            collection.setFacilityId(null);
            collection.setTemporaryLocationId(temporaryStorageLocationId);
            collection.setLocationId(null);
            collection.setCollectionStatus(CollectionStatus.TEMPORARY_STORAGE);
        } else if (destinationType == Constants.TRANSIT_LOCATION) {
            // 目标移到中转库位
            Long transitLocationId = rec.getTransitLocationId();
            if (null == transitLocationId) {
                throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
            collection.setFacilityId(null);
            collection.setTemporaryLocationId(null);
            collection.setLocationId(transitLocationId);
            collection.setCollectionStatus(CollectionStatus.TRANSFER);
        }
        int updateCount = whSeedingCollectionDao.saveOrUpdate(collection);
        if (updateCount != 1) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        /*
         * if (flag) { // 更新批次下的容器绑定播种墙 int count =
         * whSeedingCollectionDao.countNotHaveFacilityIdByBatch(batch, ouId); if (count > 0) {
         * whSeedingCollectionDao.updateFacilityByBatch(batch, rec.getFacilityId(), ouId); } }
         */
        /*
         * if (destinationType == Constants.SEEDING_WALL) { String seedingWallCode =
         * rec.getSeedingwallCode(); // 移到播种墙时保存redis数据 //
         * SEEDING_(仓库ID)_(播种墙CODE)_(批次号)_(容器CODE)=Map<SkuId_uuid, WhSeedingCollectionLine>
         * addSeedingDataIntoCache(seedingWallCode, containerCode, containerId, batch, ouId); }
         */
    }

    /*
     * private void addSeedingDataIntoCache(String seedingWallCode, String containerCode, Long
     * containerId, String batch, Long ouId) { if (StringUtils.isEmpty(seedingWallCode) ||
     * StringUtils.isEmpty(containerCode)) { throw new
     * BusinessException(ErrorCodes.SYSTEM_EXCEPTION); } StringBuilder sb = new StringBuilder(64);
     * sb.append(CacheConstants.CACHE_SEEDING).append("-"); sb.append(ouId).append("-");
     * sb.append(seedingWallCode).append("-"); sb.append(batch).append("-");
     * sb.append(containerCode); String cacheKey = sb.toString();
     * 
     * // 查找容器库存 List<WhSeedingCollectionLine> dataList =
     * this.whSkuInventoryDao.findSeedingDataByContainerId(containerId, ouId); if (null != dataList
     * && !dataList.isEmpty()) { // 封装集货sku数据 Map<String, WhSeedingCollectionLine> seedingDataMap =
     * new HashMap<String, WhSeedingCollectionLine>(); for (WhSeedingCollectionLine data : dataList)
     * { String mapKey = data.getSkuId() + "_" + data.getUuid(); seedingDataMap.put(mapKey, data); }
     * cacheManager.setObject(cacheKey, seedingDataMap, CacheConstants.CACHE_ONE_WEEK); } }
     */

    @Override
    public void removeRecommendResultListCache(String batch, Long userId) {
        cacheManager.removeMapValue(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
        cacheManager.removeMapValue(CacheConstants.PDA_CACHE_PICKING_COLLECTION_REC + userId.toString(), batch);
        cacheManager.removeMapValue(CacheConstants.PDA_CACHE_PICKING_COLLECTION_REC, userId.toString());
    }

    /**
     * 人为集货扫描容器验证
     */
    @Override
    public void addManualContainerCodeIntoCache(String containerCode, Long userId, Long ouId) {
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 判断是否有播种集货结果
        WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
        if (null == seedingCollection) {
            throw new BusinessException(ErrorCodes.COLLECTION_NOT_HAVE_CONTAINER_INFO, new Object[] {containerCode});
        }
        ArrayDeque<String> containerCodeDeque = cacheManager.getObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId);
        if (null == containerCodeDeque) {
            containerCodeDeque = new ArrayDeque<String>();
        }
        if (containerCodeDeque.contains(containerCode)) {
            throw new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_RESULT_REPEAT, new Object[] {containerCode});
        }
        containerCodeDeque.addFirst(containerCode);
        cacheManager.setObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId, containerCodeDeque, CacheConstants.CACHE_ONE_DAY);
    }

    @Override
    public boolean checkManualContainerCacheNotNull(Boolean isApplyFacility, Long userId) {
        if (null != isApplyFacility && isApplyFacility) {
            List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
            if (null == recPathList) {
                throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_QTY_IS_NULL);
            }
        } else {
            ArrayDeque<String> containerCodeDeque = cacheManager.getObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId);
            if (null == containerCodeDeque || containerCodeDeque.isEmpty()) {
                throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_QTY_IS_NULL);
            }
        }
        return true;
    }

    /**
     * 通过扫描的目的地编码得到集货位置的类型
     */
    @Override
    public int getDestinationTypeByCode(String destinationCode, Long ouId) {
        if (StringUtils.isEmpty(destinationCode)) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        WhOutboundFacility facility = whOutboundFacilityDao.findByCodeAndOuId(destinationCode, ouId);
        if (null != facility) {
            return Constants.SEEDING_WALL;
        }
        WhTemporaryStorageLocation storageLocation = whTemporaryStorageLocationDao.findByCodeAndOuId(destinationCode, ouId);
        if (null != storageLocation) {
            return Constants.TEMPORARY_STORAGE_LOCATION;
        }
        Location location = whLocationDao.findLocationByCode(destinationCode, ouId);
        if (null != location) {
            return Constants.TRANSIT_LOCATION;
        }
        throw new BusinessException(ErrorCodes.COLLECTION_DESTINATION_NOT_RIGHT);
    }

    /**
     * 判断推荐结果表中当前容器对应的小批次是否关联当前目的地
     * @param map 
     */
    @Override
    public Map<String, Object> checkContainerAssociatedWithDestination(String containerCode, String destinationCode, Integer destinationType, Long userId, Long ouId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("flag", true);
        if (StringUtils.isEmpty(containerCode) || StringUtils.isEmpty(destinationCode) || null == destinationType) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        ArrayDeque<String> containerCodeDeque = cacheManager.getObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId);
        if (!containerCodeDeque.contains(containerCode)) {
            throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_IS_NOT_IN_SCAN_LIST, new Object[] {containerCode});
        }
        WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
        if (null == seedingCollection) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        String batch = seedingCollection.getBatch();
        if (destinationType == Constants.SEEDING_WALL) {
            String seedingWallCode = whFacilityRecPathDao.getRecommendSeedingWallCodeByBatch(batch, ouId);
            if (!StringUtils.isEmpty(seedingWallCode)) {
                if (seedingWallCode.equals(destinationCode)) {
                    return map;
                } else {
                    map.put("flag", false);
                    map.put("msg", seedingWallCode);
                    return map;
                }
            }
        } else if (destinationType == Constants.TEMPORARY_STORAGE_LOCATION) {
            String temporaryStorageLocationCode = whFacilityRecPathDao.getRecommendTemporaryStorageLocationCodeByBatch(batch, ouId);
            if (!StringUtils.isEmpty(temporaryStorageLocationCode)) {
                if (temporaryStorageLocationCode.equals(destinationCode)) {
                    return map;
                } else {
                    map.put("flag", false);
                    map.put("msg", temporaryStorageLocationCode);
                    return map;
                }
            }
        } else if (destinationType == Constants.TRANSIT_LOCATION) {
            String transitLocationCode = whFacilityRecPathDao.getRecommendTransitLocationCodeByBatch(batch, ouId);
            if (!StringUtils.isEmpty(transitLocationCode)) {
                if (transitLocationCode.equals(destinationCode)) {
                    return map;
                } else {
                    map.put("flag", false);
                    map.put("msg", transitLocationCode);
                    return map;
                }
            }
        }
        // 判断目的地是否关联其他小批次
        List<String> batchList = whFacilityRecPathDao.getBatchListByDestinationCode(destinationCode, destinationType, ouId);
        if (null == batchList || batchList.isEmpty()) {
            if (destinationType == Constants.SEEDING_WALL) {
                WhOutboundFacility facility = whOutboundFacilityDao.findByCodeAndOuId(destinationCode, ouId);
                if (StringUtils.isEmpty(facility.getBatch())) {
                    facility.setBatch(batch);
                    facility.setStatus(String.valueOf(Constants.WH_FACILITY_STATUS_2));
                    whOutboundFacilityDao.saveOrUpdateByVersion(facility);
                    return map;
                }
            } else if (destinationType == Constants.TEMPORARY_STORAGE_LOCATION) {
                WhTemporaryStorageLocation storageLocation = whTemporaryStorageLocationDao.findByCodeAndOuId(destinationCode, ouId);
                if (StringUtils.isEmpty(storageLocation.getBatch())) {
                    storageLocation.setBatch(batch);
                    storageLocation.setStatus(Constants.WH_FACILITY_STATUS_2);
                    whTemporaryStorageLocationDao.saveOrUpdateByVersion(storageLocation);
                    return map;
                }
            } else if (destinationType == Constants.TRANSIT_LOCATION) {
                return map;
            }
        }
        map.put("flag", false);
        map.put("msg", "null");
        return map;
    }

    @Override
    public Map<String, Object> manualMoveContainerToDestination(String containerCode, String destinationCode, Integer destinationType, Long userId, Long ouId) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 判断推荐结果表中当前容器对应的小批次是否关联当前目的地
        Map<String, Object> checkDestinationMap = this.checkContainerAssociatedWithDestination(containerCode, destinationCode, destinationType, userId, ouId);
        boolean flag = (boolean) checkDestinationMap.get("flag");
        if (!flag) {
            map.put("index", 2);
            map.put("msg", checkDestinationMap.get("msg"));
            return map;
        }
        if (StringUtils.isEmpty(containerCode) || StringUtils.isEmpty(destinationCode) || null == destinationType) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
        ContainerCommand container = containerDao.getContainerByCode(containerCode, ouId);
        if (null == seedingCollection || null == container) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        // 1.将容器与目的地信息插入推荐结果表
        String batch = seedingCollection.getBatch();
        WhOutboundFacility facility = null;
        WhTemporaryStorageLocation storageLocation = null;
        Location location = null;
        WhFacilityRecPathCommand recCommand = null;
        List<WhFacilityRecPathCommand> recCommandList = whFacilityRecPathDao.getRecommendResultByContainerCode(containerCode, batch, ouId);
        if (null != recCommandList && !recCommandList.isEmpty()) {
            recCommand = recCommandList.get(0);
        }
        if (null == recCommand) {
            recCommand = new WhFacilityRecPathCommand();
            if (destinationType == Constants.SEEDING_WALL) {
                facility = whOutboundFacilityDao.findByCodeAndOuId(destinationCode, ouId);
                if (null == facility) {
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                recCommand.setFacilityId(facility.getId());
                recCommand.setSeedingwallCode(destinationCode);
                recCommand.setSeedingwallCheckCode(facility.getCheckCode());
                recCommand.setSeedingwallUpperLimit(facility.getFacilityUpperLimit());
            } else if (destinationType == Constants.TEMPORARY_STORAGE_LOCATION) {
                storageLocation = whTemporaryStorageLocationDao.findByCodeAndOuId(destinationCode, ouId);
                if (null == storageLocation) {
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                recCommand.setTemporaryStorageLocationId(storageLocation.getId());
                recCommand.setTemporaryStorageLocationCheckCode(storageLocation.getCheckCode());
                recCommand.setTemporaryStorageLocationCode(storageLocation.getTemporaryStorageCode());
            } else if (destinationType == Constants.TRANSIT_LOCATION) {
                location = whLocationDao.findLocationByCode(destinationCode, ouId);
                if (null == location) {
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                recCommand.setTransitLocationId(location.getId());
                recCommand.setTransitLocationCheckCode(location.getBarCode());
                recCommand.setTransitLocationCode(location.getCode());
            }
            recCommand.setBatch(batch);
            recCommand.setStatus(1);
            recCommand.setOuId(ouId);
            recCommand.setContainerCode(containerCode);
            recCommand.setContainerId(container.getId());

            WhFacilityRecPath rec = new WhFacilityRecPath();
            BeanUtils.copyProperties(recCommand, rec);
            whFacilityRecPathDao.insert(rec);
        } else {
            if (destinationType == Constants.SEEDING_WALL && StringUtils.isEmpty(recCommand.getSeedingwallCode())) {
                facility = whOutboundFacilityDao.findByCodeAndOuId(destinationCode, ouId);
                if (null == facility) {
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                recCommand.setFacilityId(facility.getId());
                recCommand.setSeedingwallCode(destinationCode);
                recCommand.setSeedingwallCheckCode(facility.getCheckCode());
                recCommand.setSeedingwallUpperLimit(facility.getFacilityUpperLimit());
                whFacilityRecPathDao.updateSeedingwallByBatch(facility.getFacilityCode(), facility.getCheckCode(), facility.getFacilityUpperLimit(), batch, ouId);
            } else if (destinationType == Constants.TEMPORARY_STORAGE_LOCATION && StringUtils.isEmpty(recCommand.getTemporaryStorageLocationCode())) {
                storageLocation = whTemporaryStorageLocationDao.findByCodeAndOuId(destinationCode, ouId);
                if (null == storageLocation) {
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                recCommand.setTemporaryStorageLocationId(storageLocation.getId());
                recCommand.setTemporaryStorageLocationCheckCode(storageLocation.getCheckCode());
                recCommand.setTemporaryStorageLocationCode(storageLocation.getTemporaryStorageCode());
                whFacilityRecPathDao.updateTemporaryStorageLocationByBatch(storageLocation.getTemporaryStorageCode(), storageLocation.getCheckCode(), batch, ouId);
            } else if (destinationType == Constants.TRANSIT_LOCATION && StringUtils.isEmpty(recCommand.getTransitLocationCode())) {
                location = whLocationDao.findLocationByCode(destinationCode, ouId);
                if (null == location) {
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                recCommand.setTransitLocationId(location.getId());
                recCommand.setTransitLocationCheckCode(location.getBarCode());
                recCommand.setTransitLocationCode(location.getCode());
                whFacilityRecPathDao.updateTransitLocationByBatch(location.getCode(), location.getBarCode(), batch, ouId);
            }
        }

        // 2.将容器移动到目的地
        this.updateContainerSkuInventory(recCommand, destinationType, userId, ouId);

        // 3.记录容器集货信息（到目的地）
        this.updateContainerToDestination(recCommand, destinationType, ouId);

        // 4.更新出库单状态
        this.updateOdoStatus(recCommand.getContainerId(), ouId);

        // 5.判断暂存库位,没有就释放
        if (destinationType == Constants.SEEDING_WALL) {
            this.clearTemporaryStorageLocation(batch, ouId);
        }

        ArrayDeque<String> containerCodeDeque = cacheManager.getObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId);
        if (null == containerCodeDeque || containerCodeDeque.isEmpty()) {
            throw new BusinessException(ErrorCodes.COLLECTION_CONTAINER_QTY_IS_NULL);
        }
        containerCodeDeque.remove(containerCode);
        cacheManager.setObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId, containerCodeDeque, CacheConstants.CACHE_ONE_DAY);
        // 判断是否全部完成
        if (containerCodeDeque.isEmpty()) {
            map.put("index", 1);
            return map;
        }
        map.put("index", 0);
        return map;
    }

    @Override
    public void updateContainerSkuInventory(WhFacilityRecPathCommand recCommand, Integer destinationType, Long userId, Long ouId) {
        Warehouse warehouse = warehouseManager.findWarehouseByIdExt(ouId);
        if (null == warehouse) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        List<WhSkuInventory> invList = this.whSkuInventoryDao.findListByInsideContainerId(recCommand.getContainerId(), ouId);
        if (null == invList || invList.isEmpty()) {
            log.error("skuInventory is empty, InsideContainerId:" + recCommand.getContainerId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_INV_NULL);
        }
        for (WhSkuInventory inv : invList) {
            switch (destinationType) {
                case Constants.SEEDING_WALL:
                    inv.setLocationId(null);
                    inv.setTemporaryLocationId(null);
                    break;
                case Constants.TEMPORARY_STORAGE_LOCATION:
                    inv.setLocationId(null);
                    inv.setTemporaryLocationId(recCommand.getTemporaryStorageLocationId());
                    break;
                case Constants.TRANSIT_LOCATION:
                    inv.setLocationId(recCommand.getTransitLocationId());
                    inv.setTemporaryLocationId(null);
                    break;
                default:
                    break;
            }
            try {
                inv.setUuid(SkuInventoryUuid.invUuid(inv));
            } catch (Exception e) {
                log.error("SkuInventoryUuid.invUuid error");
            }
            int cnt = this.whSkuInventoryDao.saveOrUpdateByVersion(inv);
            this.insertSkuInventoryLog(inv.getId(), inv.getOnHandQty(), inv.getOnHandQty(), warehouse.getIsTabbInvTotal(), ouId, userId, InvTransactionType.FACILITY_GOODS_COLLECTION);
            if (0 >= cnt) {
                throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
        }
    }

    @Override
    public void removeManualContainerCodeFromCache(Long userId) {
        try {
            cacheManager.remove(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_CONTAINER + userId);
            cacheManager.removeMapValue(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
        } catch (Exception e) {
            log.error("redis error", e);
        }
    }

    @Override
    public void useSysRecommendResult(String containerCode, Long userId, Long ouId) {
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            // 容器信息不存在
            log.error("container is not exists, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
        }
        // 判断是否有播种推荐结果
        WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
        if (null == seedingCollection) {
            throw new BusinessException(ErrorCodes.COLLECTION_NOT_HAVE_CONTAINER_INFO, new Object[] {containerCode});
        }
        String batch = seedingCollection.getBatch();
        if (StringUtils.isEmpty(batch)) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        WhFacilityRecPathCommand rec = null;
        List<WhFacilityRecPathCommand> recList = whFacilityRecPathDao.getRecommendResultByContainerCode(containerCode, batch, ouId);
        if (null != recList && !recList.isEmpty()) {
            rec = recList.get(0);
        }
        if (null == rec) {
            String recommendSeedingWall = whFacilityRecPathDao.getRecommendSeedingWallCodeByBatch(batch, ouId);
            String recommendTemporaryStorageLocation = whFacilityRecPathDao.getRecommendTemporaryStorageLocationCodeByBatch(batch, ouId);
            String recommendTransitLocation = whFacilityRecPathDao.getRecommendTransitLocationCodeByBatch(batch, ouId);
            if (StringUtils.isEmpty(recommendSeedingWall) && StringUtils.isEmpty(recommendTemporaryStorageLocation) && StringUtils.isEmpty(recommendTransitLocation)) {
                log.error("containerCode:{},ouId:{}", containerCode, ouId);
                throw new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_RESULT_ERROR);
            }
            rec = new WhFacilityRecPathCommand();
            if (!StringUtils.isEmpty(recommendSeedingWall)) {
                WhOutboundFacility facility = whOutboundFacilityDao.findByCodeAndOuId(recommendSeedingWall, ouId);
                if (null == facility) {
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                rec.setSeedingwallCode(recommendSeedingWall);
                rec.setSeedingwallCheckCode(facility.getCheckCode());
                rec.setSeedingwallUpperLimit(facility.getFacilityUpperLimit());
                rec.setFacilityId(facility.getId());
            }
            if (!StringUtils.isEmpty(recommendTemporaryStorageLocation)) {
                WhTemporaryStorageLocation temporaryStorageLocation = whTemporaryStorageLocationDao.findByCodeAndOuId(recommendTemporaryStorageLocation, ouId);
                if (null == temporaryStorageLocation) {
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                rec.setTemporaryStorageLocationCode(recommendTemporaryStorageLocation);
                rec.setTemporaryStorageLocationCheckCode(temporaryStorageLocation.getCheckCode());
                rec.setTemporaryStorageLocationId(temporaryStorageLocation.getId());
            }
            if (!StringUtils.isEmpty(recommendTransitLocation)) {
                Location location = whLocationDao.findLocationByCode(recommendTransitLocation, ouId);
                if (null == location) {
                    throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
                }
                rec.setTransitLocationCode(recommendTransitLocation);
                rec.setTransitLocationCheckCode(location.getBarCode());
                rec.setTransitLocationId(location.getId());
            }
            rec.setBatch(batch);
            rec.setContainerCode(containerCode);
            rec.setContainerId(containerCmd.getId());
            rec.setOuId(ouId);
            rec.setStatus(1);
            /*
             * Long scanContainerId = containerCmd.getId(); WorkCollectionCommand workCommand =
             * this.createObject(batch, null, ouId, null, scanContainerId); // 推荐播种墙逻辑,并判断是否推荐成功
             * RecFacilityPathCommand recFacilityPath = new RecFacilityPathCommand();
             * recFacilityPath.setOuId(ouId); recFacilityPath.setBatch(batch);
             * recFacilityPath.setContainerCode(containerCode);
             * recFacilityPath.setOdoIdList(workCommand.getOdoIdList());
             * recFacilityPath.setLastContainer(workCommand.getIsLastContainer()); //
             * recFacilityPath.setAreaId(workCommand.getLastLocationId());
             * recFacilityPath.setAreaId(workCommand.getAreaId());
             * recFacilityPath.setPickingMode(Constants.PICKING_MODE_SEED); // 调用播种墙推荐逻辑
             * RecFacilityPathCommand command =
             * waveFacilityManagerProxy.matchOutboundFacility(recFacilityPath); if (1 ==
             * command.getStatus()) { // 推荐成功 // rec =
             * whFacilityRecPathDao.getRecommendResultByContainerCode(containerCode, // batch,
             * ouId); List<WhFacilityRecPathCommand> recList2 =
             * whFacilityRecPathDao.getRecommendResultByContainerCode(containerCode, batch, ouId);
             * if (null != recList2 && !recList2.isEmpty()) { rec = recList2.get(0); } else { throw
             * new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_RESULT_ERROR); } } else { throw
             * new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_RESULT_ERROR); }
             */
        }
        // 缓存推荐结果
        List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
        if (null == recPathList) {
            recPathList = new ArrayList<WhFacilityRecPathCommand>();
        }
        if (!recPathList.isEmpty()) {
            for (WhFacilityRecPathCommand recPath : recPathList) {
                if (recPath.getContainerCode().equals(containerCode)) {
                    throw new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_RESULT_REPEAT, new Object[] {containerCode});
                }
            }
        }
        recPathList.add(0, rec);
        cacheManager.setMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString(), recPathList, CacheConstants.CACHE_ONE_DAY);
    }

    @Override
    public void moveContainerToDestination(WhFacilityRecPathCommand recCommand, Integer destinationType, Boolean isManual, Long userId, Long ouId) {
        if (null != isManual && isManual) {
            List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
            if (null != recPathList && !recPathList.isEmpty()) {
                WhFacilityRecPathCommand command = recPathList.get(0);
                if (null == command.getId()) {
                    WhFacilityRecPath rec = new WhFacilityRecPath();
                    BeanUtils.copyProperties(command, rec);
                    whFacilityRecPathDao.insert(rec);
                }
            }
        }

        // 1.将容器移动到目的地
        this.updateContainerSkuInventory(recCommand, destinationType, userId, ouId);

        // 2.记录容器集货信息（到目的地）
        this.updateContainerToDestination(recCommand, destinationType, ouId);

        // 3. 更新出库单状态
        this.updateOdoStatus(recCommand.getContainerId(), ouId);

        // 4.判断暂存库位,没有就释放
        if (destinationType == Constants.SEEDING_WALL) {
            this.clearTemporaryStorageLocation(recCommand.getBatch(), ouId);
        }

        // 5.清理缓存
        List<WhFacilityRecPathCommand> recPathList = null;
        if (null != isManual && isManual) {
            recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
            if (null != recPathList && !recPathList.isEmpty()) {
                recPathList.remove(0);
                cacheManager.setMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString(), recPathList, CacheConstants.CACHE_ONE_DAY);
            }
        } else {
            String batch = recCommand.getBatch();
            recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
            if (null != recPathList && !recPathList.isEmpty()) {
                recPathList.remove(0);
                cacheManager.setMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch, recPathList, CacheConstants.CACHE_ONE_DAY);
            }
        }
    }

    @Override
    public Integer checkDestinationByRecommendResult(WhFacilityRecPathCommand rec, Long userId, Long ouId) {
        if (null == rec) {
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        String containerCode = rec.getContainerCode();
        String batch = rec.getBatch();
        WhSeedingCollectionCommand seedingCollection = whSeedingCollectionDao.getSeedingCollectionByContainerCode(containerCode, ouId);
        Integer position = 4; // 当期容器位置, 4为还在集货区
        if (null != seedingCollection.getLocationId()) {
            position = Constants.TRANSIT_LOCATION;
        } else if (null != seedingCollection.getTemporaryLocationId()) {
            position = Constants.TEMPORARY_STORAGE_LOCATION;
        } else if (null != seedingCollection.getFacilityId()) {
            position = Constants.SEEDING_WALL;
        }

        // 容器在集货区, 推荐结果中有中转库位, 移至中转库位
        if (!StringUtils.isEmpty(rec.getTransitLocationCode()) && position == 4) {
            return Constants.TRANSIT_LOCATION;
        }
        if (!StringUtils.isEmpty(rec.getSeedingwallCode())) {
            Long fid = rec.getFacilityId();
            Integer seedingNum = whSeedingCollectionDao.getSeedingNumFromFacility(fid, batch, ouId);
            if (rec.getSeedingwallUpperLimit().intValue() > seedingNum.intValue()) {
                return Constants.SEEDING_WALL;
            }
        } else {
            // 容器在暂存库位, 推荐结果中没有播种墙信息
            if (position == 2) {
                List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString());
                if (null != recPathList && !recPathList.isEmpty()) {
                    recPathList.remove(0);
                    cacheManager.setMapObject(CacheConstants.PDA_CACHE_MANUAL_COLLECTION_REC, userId.toString(), recPathList, CacheConstants.CACHE_ONE_DAY);
                }
                return null;
            }
        }
        if (StringUtils.isEmpty(rec.getTemporaryStorageLocationCode())) {
            return null;
        }
        return Constants.TEMPORARY_STORAGE_LOCATION;
    }

    // TODO 待校验
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean checkAndRecordInventory(WorkCollectionCommand workCollectionCommand, String cacheKey) {
        String inputContainerCode = workCollectionCommand.getInputContainerCode();
        String containerCode = workCollectionCommand.getContainerCode();
        if (!containerCode.equals(inputContainerCode)) {
            throw new BusinessException("容器不匹配");
        }
        Boolean isRecordSuccess = this.recordSeedingCollection(workCollectionCommand, cacheKey);
        if (!isRecordSuccess) {
            throw new BusinessException("记录容器集货信息失败");
        }
        Boolean isMoveSuccess = this.moveContainer(workCollectionCommand, cacheKey);
        if (!isMoveSuccess) {
            throw new BusinessException("移动容器失败");
        }
        return null;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertIntoSeedingCollectionLine(WhSeedingCollection whSeedingCollection) {
        log.info("pdaConcentrationManager.insertIntoSeedingCollectionLine start...");
        Long containerId = whSeedingCollection.getContainerId();
        if (null != containerId) {
            List<WhSeedingCollectionLineCommand> invList = this.whSkuInventoryDao.findListByContainerIdExt(containerId, whSeedingCollection.getOuId());
            if (null != invList && !invList.isEmpty()) {
                for (WhSeedingCollectionLineCommand inv : invList) {
                    if (null != inv.getStoreId()) {
                        Store store = this.getStoreByRedis(inv.getStoreId());
                        if (null != store) {
                            inv.setStoreCode(store.getStoreCode());
                            inv.setStoreName(store.getStoreName());
                        }
                    }
                    if (null != inv.getCustomerId()) {
                        Customer customer = this.getCustomerByRedis(inv.getCustomerId());
                        if (null != customer) {
                            inv.setCustomerCode(customer.getCustomerCode());
                            inv.setCustomerName(customer.getCustomerName());
                        }
                    }
                    WhSeedingCollectionLine line = new WhSeedingCollectionLine();
                    BeanUtils.copyProperties(inv, line);
                    line.setSeedingCollectionId(whSeedingCollection.getId());
                    line.setSeedingQty(0L);
                    this.whSeedingCollectionLineDao.insert(line);
                }
            }
        }
        log.info("pdaConcentrationManager.insertIntoSeedingCollectionLine start...");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertIntoCheckingCollectionLine(WhCheckingCollection whCheckingCollection) {
        // 按外部容器 内部容器 货格号去库存中找
        Long containerId = whCheckingCollection.getContainerId();
        Long outerContainerId = whCheckingCollection.getOuterContainerId();
        Integer containerLatticeNo = whCheckingCollection.getContainerLatticeNo();
        String outboundboxCode = whCheckingCollection.getOutboundboxCode();
        if (null == containerId && null == outboundboxCode && null == outerContainerId && null == containerLatticeNo) {
            throw new BusinessException("外部容器、内部容器、货格号、出库箱编码为空");
        }

        List<WhCheckingCollectionLine> invList = this.whSkuInventoryDao.findWhCheckingCollectionListByContainerId(containerId, outerContainerId, containerLatticeNo, outboundboxCode, whCheckingCollection.getOuId());
        if (null != invList && !invList.isEmpty()) {
            for (WhCheckingCollectionLine inv : invList) {
                if (null != inv.getStoreId()) {
                    Store store = this.getStoreByRedis(inv.getStoreId());
                    if (null != store) {
                        inv.setStoreCode(store.getStoreCode());
                        inv.setStoreName(store.getStoreName());
                    }
                }
                if (null != inv.getCustomerId()) {
                    Customer customer = this.getCustomerByRedis(inv.getCustomerId());
                    if (null != customer) {
                        inv.setCustomerCode(customer.getCustomerCode());
                        inv.setCustomerName(customer.getCustomerName());
                    }
                }
                WhCheckingCollectionLine line = new WhCheckingCollectionLine();
                BeanUtils.copyProperties(inv, line);
                line.setCheckingCollectionId(whCheckingCollection.getId());
                line.setSeedingQty(0L);
                this.whCheckingCollectionLineDao.insert(line);
            }
        }


    }

    @Override
    public boolean checkBatchInTemporaryStorageLocation(String batch, Long userId, Long ouId) {
        int count = whSeedingCollectionDao.checkCountInDestination(batch, Constants.TEMPORARY_STORAGE_LOCATION, ouId);
        List<WhFacilityRecPathCommand> recPathList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_COLLECTION_REC + userId.toString(), batch);
        if (null == recPathList) {
            throw new BusinessException(ErrorCodes.COLLECTION_RECOMMEND_PATH_ERROR);
        }
        if (recPathList.size() == count) {
            return true;
        }
        return false;
    }

    @Override
    public boolean popCacheByIndex(Integer index, Long userId, String batch, Long ouId) {
        List<Long> containerList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_PICKING_COLLECTION_REC, userId.toString());
        if (null == containerList || containerList.isEmpty()) {
            throw new BusinessException("no container found");
        }
        Long containerId = containerList.get(0);
        WhSeedingCollection sc = whSeedingCollectionDao.findSeedingCollectionByContainerId(containerId, null, ouId);
        if (null == sc) {
            throw new BusinessException("no seeding collection data found");
        }
        List<WhFacilityRecPathCommand> rfpList = cacheManager.getMapObject(CacheConstants.PDA_CACHE_PICKING_COLLECTION_REC + userId, sc.getBatch());
        if (null != rfpList && !rfpList.isEmpty()) {
            // if (index > (rfpList.size() - 1)) {
            // throw new BusinessException("redis error");
            // }
            if (rfpList.remove(rfpList.get(0))) {
                cacheManager.setMapObject(CacheConstants.PDA_CACHE_PICKING_COLLECTION_REC + userId, sc.getBatch(), rfpList, CacheConstants.CACHE_ONE_YEAR);
            }
            if (null == rfpList || rfpList.isEmpty()) {
                containerList.remove(containerList.get(0));
                if (null == containerList || containerList.isEmpty()) {
                    return true;
                } else {
                    cacheManager.setMapObject(CacheConstants.PDA_CACHE_PICKING_COLLECTION_REC, userId.toString(), containerList, CacheConstants.CACHE_ONE_DAY);
                    return false;
                }
            } else {
                return false;
            }
        } else {
            throw new BusinessException("redis error");
        }
        // return true;
    }

    private boolean isExistsOperationExecLineGroup(List<WhOperationExecLineCommand> operationExecLineGroup, String execLine) {
        boolean isExists = false;
        if (null != operationExecLineGroup && !operationExecLineGroup.isEmpty()) {
            for (WhOperationExecLineCommand cmd : operationExecLineGroup) {
                String line =
                        ParamsUtil.concatParam((null == cmd.getUseOuterContainerId() ? "" : cmd.getUseOuterContainerId().toString()), (null == cmd.getUseContainerLatticeNo() ? "" : cmd.getUseContainerLatticeNo().toString()),
                                (null == cmd.getUseContainerId() ? "" : cmd.getUseContainerId().toString()), cmd.getUseOutboundboxCode());
                if (line.equals(execLine)) {
                    isExists = true;
                    break;
                }
            }
        }
        return isExists;
    }

    private void initSeedingCollectionInfo(String batch, Long ouId, Long userId) {
        log.info("pdaConcentrationManager.initSeedingCollectionInfo start...");
        log.info("params: batch:[{}], ouId:[{}]", batch, ouId);
        List<WhSeedingCollection> list = whSeedingCollectionDao.findWhSeedingCollectionByBatchNo(batch, ouId);
        if (null == list || list.isEmpty()) {
            log.info("no seedingcollection info found");
            List<WhWorkCommand> workList = whWorkDao.findWorkByBatchExt(batch, ouId);
            if (null != workList && !workList.isEmpty()) {
                for (WhWorkCommand work : workList) {
                    log.info("work of batch, containerCode:[{}]", work.getContainerCode());
                    // 非当前工作的集货信息从工作明细查询
                    WhSeedingCollection seedingCollection = new WhSeedingCollection();
                    seedingCollection.setBatch(batch);
                    seedingCollection.setContainerId(work.getContainerId());
                    seedingCollection.setOuId(ouId);
                    seedingCollection.setCollectionStatus(CollectionStatus.NEW);
                    seedingCollection.setPickingMode(work.getPickingMode());
                    seedingCollection.setCheckingMode(work.getCheckingMode());
                    seedingCollection.setDistributionMode(work.getDistributionMode());
                    seedingCollection.setCreateTime(new Date());
                    seedingCollection.setLastModifyTime(new Date());
                    seedingCollection.setModifiedId(userId);
                    this.whSeedingCollectionDao.insert(seedingCollection);
                    log.info("work of batch, seedingCollectionId:[{}]", seedingCollection.getId());
                    insertIntoSeedingCollectionLine(seedingCollection);
                }
            }
        }
        log.info("pdaConcentrationManager.initSeedingCollectionInfo finish...");
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertIntoSeedingCollectionNew(String batch, Long workId, List<WhOperationExecLineCommand> execLineCommandList, Long ouId, Long userId) {
        log.info("pdaConcentrationManager.insertIntoSeedingCollectionNew start...");
        log.info("insertIntoSeedingCollectionNew params: batch:[{}], workId:[{}], ouId:[{}]", batch, workId, ouId);
        // 1. 初始化集货表
        initSeedingCollectionInfo(batch, ouId, userId);
        WhWorkCommand workCommand = this.whWorkDao.findWorkByIdExt(workId, ouId);
        Long containerId = workCommand.getContainerId();
        String containerCode = workCommand.getContainerCode();
        log.info("insertIntoSeedingCollectionNew containerId:[{}], containerCode:[{}]", containerId, containerCode);
        // WhSeedingCollection sc =
        // whSeedingCollectionDao.findSeedingCollectionByContainerId(containerId, batch, ouId);
        // List<WhOperationExecLineCommand> execLineCommandList =
        // this.whOperationExecLineDao.findCommandByContainerId(containerId, workId, ouId);
        // 2.根据作业执行明细查找出容器列表
        Set<Long> containerIds = new HashSet<Long>();
        if (null != execLineCommandList && !execLineCommandList.isEmpty()) {
            for (WhOperationExecLineCommand execLineCommand : execLineCommandList) {
                containerIds.add(execLineCommand.getUseContainerId());
            }
        }
        // 3. 根据每个容器查找集货信息 先删除再插入
        if (null != containerIds && !containerIds.isEmpty()) {
            for (Long cid : containerIds) {
                log.info("current containerId:[{}]", cid);
                WhSeedingCollection sc = whSeedingCollectionDao.findSeedingCollectionByContainerId(containerId, batch, ouId);
                if (null != sc) {
                    log.info("seedingCollection info found, scId:[{}], containerId:[{}], batch:[{}], ouId:[{}]", sc.getId(), containerId, batch, ouId);
                    Long scId = sc.getId();
                    // 删除集货明细数据
                    whSeedingCollectionLineDao.deleteByScId(scId, ouId);
                    // 删除集货表数据
                    whSeedingCollectionDao.deleteByIdExt(scId, ouId);
                }
                WhSeedingCollection seedingCollection = new WhSeedingCollection();
                seedingCollection.setBatch(batch);
                seedingCollection.setContainerId(cid);
                seedingCollection.setOuId(ouId);
                seedingCollection.setCollectionStatus(CollectionStatus.NEW);
                seedingCollection.setPickingMode(workCommand.getPickingMode());
                seedingCollection.setCheckingMode(workCommand.getCheckingMode());
                seedingCollection.setDistributionMode(workCommand.getDistributionMode());
                seedingCollection.setCreateTime(new Date());
                seedingCollection.setLastModifyTime(new Date());
                seedingCollection.setModifiedId(userId);
                this.whSeedingCollectionDao.insert(seedingCollection);
                log.info("work of batch, seedingCollectionId:[{}]", seedingCollection.getId());
                insertIntoSeedingCollectionLine(seedingCollection);
            }
        }
        log.info("pdaConcentrationManager.insertIntoSeedingCollectionNew finish...");
    }
}
