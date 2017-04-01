package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.annotation.MoreDB;

import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.print.exception.BusinessException;
import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.LocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.CreateWorkResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryAllocatedCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OperationStatus;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoOutBoundBoxDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveMasterDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AreaDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentTaskDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WorkTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryAllocatedDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.warehouse.LocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ReplenishmentTaskManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Area;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationProductVolume;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuWhmgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WorkType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;

@Service("createWorkManager")
@Transactional
public class CreateWorkManagerImpl implements CreateWorkManager {

    protected static final Logger log = LoggerFactory.getLogger(CreateWorkManagerImpl.class);

    @Autowired
    private CodeManager codeManager;

    @Autowired
    private WhWaveDao waveDao;

    @Autowired
    private WhOdoOutBoundBoxDao odoOutBoundBoxDao;

    @Autowired
    private WhWorkDao workDao;

    @Autowired
    private WhWorkLineDao workLineDao;

    @Autowired
    private WhOperationDao operationDao;

    @Autowired
    private WhOperationLineDao operationLineDao;

    @Autowired
    private WorkTypeDao workTypeDao;

    @Autowired
    private WhSkuInventoryDao skuInventoryDao;

    @Autowired
    private WhSkuInventoryTobefilledDao whSkuInventoryTobefilledDao;

    @Autowired
    private WhOdoDao odoDao;

    @Autowired
    private ContainerDao containerDao;

    @Autowired
    private WhWaveMasterDao waveMasterDao;

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private WhLocationDao locationDao;

    @Autowired
    private ReplenishmentRuleDao replenishmentRuleDao;

    @Autowired
    private WhSkuInventoryAllocatedDao skuInventoryAllocatedDao;

    @Autowired
    private WhSkuInventoryTobefilledDao skuInventoryTobefilledDao;

    @Autowired
    private ReplenishmentTaskDao replenishmentTaskDao;

    @Autowired
    private LocationManager locationManager;

    @Autowired
    private WhSkuInventoryManager whskuInventoryManager;

    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;

    @Autowired
    private WhDistributionPatternRuleDao whDistributionPatternRuleDao;

    @Autowired
    private ReplenishmentTaskManager replenishmentTaskManager;

    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;


    /**
     * 波次内创建补货工作和作业
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public CreateWorkResultCommand createReplenishmentWorkInWave(WhWave whWave, List<WhSkuInventoryAllocatedCommand> whSkuInventoryAllocatedCommandLst, ReplenishmentRuleCommand replenishmentRuleCommand, Long userId) {
        CreateWorkResultCommand createWorkResultCommand = new CreateWorkResultCommand();
        try {
            // 工作总单数
            Integer execOdoQty = null == whWave.getExecOdoQty() ? 0 : whWave.getExecOdoQty();
            // 工作总行数
            Integer execOdoLineQty = null == whWave.getExecOdoLineQty() ? 0 : whWave.getExecOdoLineQty();
            // 创建拣货工作--创建工作头信息
            WhSkuInventoryAllocatedCommand siaCommand = whSkuInventoryAllocatedCommandLst.get(0);
            String replenishmentWorkCode = this.saveReplenishmentWork(replenishmentRuleCommand.getWaveId(), siaCommand, userId);
            execOdoQty = execOdoQty + 1;
            int rWorkLineTotal = 0;
            Set<String> replenishmentCodes = new HashSet<String>();
            // 循环统计的分组补货信息列表
            for (WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand : whSkuInventoryAllocatedCommandLst) {
                // 判断分配量与待移入量是否相等
                if (!skuInventoryAllocatedCommand.getQty().equals(skuInventoryAllocatedCommand.getToQty())) {
                    log.error("qty != toQty", skuInventoryAllocatedCommand.getQty(), skuInventoryAllocatedCommand.getToQty());
                }
                // 创建补货工作明细
                this.saveReplenishmentWorkLine(replenishmentWorkCode, userId, skuInventoryAllocatedCommand);
                execOdoLineQty = execOdoLineQty + 1;
                // 工作明细数量
                rWorkLineTotal = rWorkLineTotal + 1;
                // 获取明细补货单据号
                replenishmentCodes.add(skuInventoryAllocatedCommand.getReplenishmentCode());
            }
            // 校验工作明细数量是否正确
            if (rWorkLineTotal != whSkuInventoryAllocatedCommandLst.size()) {
                log.error("rWorkLineTotal is error", rWorkLineTotal);
                throw new BusinessException("明细数量错误");
            }
            // 更新补货工作头信息
            this.updateReplenishmentWork(replenishmentRuleCommand.getWaveId(), replenishmentWorkCode, siaCommand);
            // 生成作业头
            String replenishmentOperationCode = this.saveReplenishmentOperation(replenishmentWorkCode, siaCommand);
            // 判断补货工作释放方式是否是按照需求量释放
            if (1 == replenishmentRuleCommand.getReleaseWorkWay()) {
                // 基于工作明细生成作业明细
                int replenishmentOperationLineCount = this.saveReplenishmentOperationLine(replenishmentWorkCode, replenishmentOperationCode, replenishmentRuleCommand.getTaskOuId(), null);
                // 校验作业明细
                if (replenishmentOperationLineCount != rWorkLineTotal) {
                    log.error("replenishmentOperationLineCount is error", rWorkLineTotal);
                }
            } else {
                // 计算目标库位容器
                List<WhSkuInventoryAllocatedCommand> skuInventoryAllocatedCommandLst = locationReplenishmentCalculation(whSkuInventoryAllocatedCommandLst, replenishmentRuleCommand.getTaskOuId());
                if(null == skuInventoryAllocatedCommandLst){
                    // 获取作业头信息
                    WhOperationCommand WhOperationCommand = this.operationDao.findOperationByCode(replenishmentOperationCode, replenishmentRuleCommand.getTaskOuId());
                    operationDao.delete(WhOperationCommand.getId());
                    log.error("skuInventoryAllocatedCommandLst is error", skuInventoryAllocatedCommandLst);    
                }
                if (null != skuInventoryAllocatedCommandLst && 0 < skuInventoryAllocatedCommandLst.size()) {
                    // 基于目标库位容器及工作明细生成作业明细
                    int replenishmentOperationLineCount = this.saveReplenishmentOperationLine(replenishmentWorkCode, replenishmentOperationCode, replenishmentRuleCommand.getTaskOuId(), skuInventoryAllocatedCommandLst);
                    if (replenishmentOperationLineCount != rWorkLineTotal) {
                        log.error("replenishmentOperationLineCount is error", rWorkLineTotal);
                    }
                }
            }
            // 判断补货单号对应库存是否都创完工作
            for (String replenishmentCode : replenishmentCodes) {
                Double totalQtyAllocated = skuInventoryAllocatedDao.getTotalQtyByReplenishmentCode(replenishmentCode, replenishmentRuleCommand.getTaskOuId());
                Double totalQtyWorkLine = workLineDao.getTotalQtyByReplenishmentCode(replenishmentCode, replenishmentRuleCommand.getTaskOuId());
                if (null != totalQtyAllocated && null != totalQtyWorkLine && totalQtyAllocated.equals(totalQtyWorkLine)) {
                    // 将补货任务行标识为已创建工作
                    ReplenishmentTask replenishmentTask = replenishmentTaskDao.findReplenishmentTaskByCode(replenishmentCode, replenishmentRuleCommand.getTaskOuId());
                    replenishmentTask.setIsCreateWork(true);
                    replenishmentTaskDao.saveOrUpdateByVersion(replenishmentTask);
                }
            }
            whWave.setExecOdoQty(execOdoQty);
            whWave.setExecOdoLineQty(execOdoLineQty);
            createWorkResultCommand.setWhWave(whWave);
        } catch (Exception e) {
            log.error("", e);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        return createWorkResultCommand;
    }

    /**
     * 波次内创建拣货工作和作业
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public CreateWorkResultCommand createPickingWorkInWave(WhWave whWave, WhOdoOutBoundBox whOdoOutBoundBoxGroup, WhOdoOutBoundBox whOdoOutBoundBox, CreateWorkResultCommand createWorkResultCommand, Long userId) {
        try {
            Map<Long, List<WhSkuInventory>> odoLineIdAndInventory = new HashMap<Long, List<WhSkuInventory>>();
            Map<Long, List<WhSkuInventoryTobefilled>> odoLineIdAndTobefilled = new HashMap<Long, List<WhSkuInventoryTobefilled>>();
            Map<Long, Double> odoLineIdAndQtyMap = new HashMap<Long, Double>();
            Double odoLineIdAndQty = 0.00;
            odoLineIdAndInventory = createWorkResultCommand.getOdoLineIdAndInventory();
            odoLineIdAndTobefilled = createWorkResultCommand.getOdoLineIdAndTobefilled();
            odoLineIdAndQtyMap = createWorkResultCommand.getOdoLineIdAndQty();
            // 工作总单数
            Integer execOdoQty = null == whWave.getExecOdoQty() ? 0 : whWave.getExecOdoQty();
            // 工作总行数
            Integer execOdoLineQty = null == whWave.getExecOdoLineQty() ? 0 : whWave.getExecOdoLineQty();
            // 2.1.1 根据小批次分组查询出所有出库箱/容器信息
            List<WhOdoOutBoundBoxCommand> whOdoOutBoundBoxCommandList = this.getOdoOutBoundBoxListByGroup(whOdoOutBoundBoxGroup);
            // 2.1.2 创建拣货工作--创建工作头信息
            String workCode = this.savePickingWork(whWave, whOdoOutBoundBoxGroup, userId);
            execOdoQty = execOdoQty + 1;
            // 2.1.3 循环出库箱/容器信息列表创建工作明细
            for (WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand : whOdoOutBoundBoxCommandList) {
                // 2.1.3.1 判断库位占用量是否满足
                List<WhSkuInventory> whSkuInventoryList = new ArrayList<WhSkuInventory>();
                List<WhSkuInventoryTobefilled> whSkuInventoryTobefilledList = new ArrayList<WhSkuInventoryTobefilled>();
                // 根据占用单据号和占用单据明细行ID查询库存列表
                if (null == odoLineIdAndInventory.get(whOdoOutBoundBoxCommand.getOdoLineId())) {
                    whSkuInventoryList = this.getSkuInventory(whOdoOutBoundBoxCommand);
                } else {
                    whSkuInventoryList = odoLineIdAndInventory.get(whOdoOutBoundBoxCommand.getOdoLineId());
                }
                if (null == odoLineIdAndTobefilled.get(whOdoOutBoundBoxCommand.getOdoLineId())) {
                    whSkuInventoryTobefilledList = this.getSkuInventoryTobefilled(whOdoOutBoundBoxCommand);
                } else {
                    whSkuInventoryTobefilledList = odoLineIdAndTobefilled.get(whOdoOutBoundBoxCommand.getOdoLineId());
                }
                if (null == odoLineIdAndQtyMap.get(whOdoOutBoundBoxCommand.getOdoLineId())) {
                    odoLineIdAndQty = this.odoOutBoundBoxDao.findQtyByOdoLineId(whOdoOutBoundBoxCommand.getOdoLineId(), whOdoOutBoundBox.getOuId());
                    odoLineIdAndQtyMap.put(whOdoOutBoundBoxCommand.getOdoLineId(), odoLineIdAndQty);
                } else {
                    odoLineIdAndQty = odoLineIdAndQtyMap.get(whOdoOutBoundBoxCommand.getOdoLineId());
                }
                // 初始化占用库存量
                Double onHandQty = 0.0;
                // 计算占用库存量
                List<WhSkuInventory> inventoryQtyList = this.getSkuInventory(whOdoOutBoundBoxCommand);
                for (WhSkuInventory whSkuInventory : inventoryQtyList) {
                    onHandQty = whSkuInventory.getOnHandQty() + onHandQty;
                }
                List<WhSkuInventoryTobefilled> tobefilledQtyList = this.getSkuInventoryTobefilled(whOdoOutBoundBoxCommand);
                for (WhSkuInventoryTobefilled whSkuInventoryTobefilled : tobefilledQtyList) {
                    onHandQty = whSkuInventoryTobefilled.getQty() + onHandQty;
                }
                // 如果不满足，则抛出异常
                if (0 != onHandQty.compareTo(odoLineIdAndQty)) {
                    log.error("onHandQty != odoLineIdAndQty", onHandQty, odoLineIdAndQty);
                    throw new BusinessException("数量错误");
                }
                // 分配数量
                Double odoOutBoundBoxQty = whOdoOutBoundBoxCommand.getQty();
                List<WhSkuInventory> whSkuInventoryLst = new ArrayList<WhSkuInventory>();
                List<WhSkuInventory> skuInventoryLst = new ArrayList<WhSkuInventory>();
                for (WhSkuInventory whSkuInventory : whSkuInventoryList) {
                    Double skuInventoryQty = whSkuInventory.getOnHandQty();
                    // 出库箱数量和库存数量比较
                    int retval = odoOutBoundBoxQty.compareTo(skuInventoryQty);
                    BigDecimal b1 = new BigDecimal(odoOutBoundBoxQty.toString());
                    BigDecimal b2 = new BigDecimal(skuInventoryQty.toString());
                    if (retval > 0) {
                        odoOutBoundBoxQty = b1.subtract(b2).doubleValue();
                        whSkuInventoryLst.add(whSkuInventory);
                        skuInventoryLst.add(whSkuInventory);
                    } else {
                        skuInventoryQty = b2.subtract(b1).doubleValue();
                        if (0 == skuInventoryQty.compareTo(0.00)) {
                            whSkuInventoryLst.add(whSkuInventory);
                            skuInventoryLst.add(whSkuInventory);
                        } else {
                            whSkuInventory.setOnHandQty(skuInventoryQty);
                            WhSkuInventory newWhSkuInventory = new WhSkuInventory();
                            BeanUtils.copyProperties(whSkuInventory, newWhSkuInventory);
                            newWhSkuInventory.setOnHandQty(odoOutBoundBoxQty);
                            whSkuInventoryLst.add(newWhSkuInventory);
                        }
                        odoOutBoundBoxQty = 0.00;
                    }
                    int retva2 = odoOutBoundBoxQty.compareTo(0.00);
                    if (retva2 == 0) {
                        break;
                    }
                }
                whSkuInventoryList = ListUtils.subtract(whSkuInventoryList, skuInventoryLst);
                odoLineIdAndInventory.put(whOdoOutBoundBoxCommand.getOdoLineId(), whSkuInventoryList);
                List<WhSkuInventoryTobefilled> whSkuInventoryTobefilledLst = new ArrayList<WhSkuInventoryTobefilled>();
                if (0 != odoOutBoundBoxQty.compareTo(0.00)) {
                    List<WhSkuInventoryTobefilled> skuInventoryTobefilledLst = new ArrayList<WhSkuInventoryTobefilled>();
                    for (WhSkuInventoryTobefilled whSkuInventoryTobefilled : whSkuInventoryTobefilledList) {
                        Double tobefilledQty = whSkuInventoryTobefilled.getQty();
                        // 出库箱数量和库存数量比较
                        int retval = odoOutBoundBoxQty.compareTo(tobefilledQty);
                        BigDecimal b1 = new BigDecimal(odoOutBoundBoxQty.toString());
                        BigDecimal b2 = new BigDecimal(tobefilledQty.toString());
                        if (retval > 0) {
                            odoOutBoundBoxQty = b1.subtract(b2).doubleValue();
                            whSkuInventoryTobefilledLst.add(whSkuInventoryTobefilled);
                            skuInventoryTobefilledLst.add(whSkuInventoryTobefilled);
                        } else {
                            tobefilledQty = b2.subtract(b1).doubleValue();
                            if (0 == tobefilledQty.compareTo(0.00)) {
                                whSkuInventoryTobefilledLst.add(whSkuInventoryTobefilled);
                                skuInventoryTobefilledLst.add(whSkuInventoryTobefilled);
                            } else {
                                whSkuInventoryTobefilled.setQty(tobefilledQty);
                                WhSkuInventoryTobefilled newWhSkuInventoryTobefilled = new WhSkuInventoryTobefilled();
                                BeanUtils.copyProperties(whSkuInventoryTobefilled, newWhSkuInventoryTobefilled);
                                newWhSkuInventoryTobefilled.setQty(odoOutBoundBoxQty);
                                whSkuInventoryTobefilledLst.add(newWhSkuInventoryTobefilled);
                            }
                            odoOutBoundBoxQty = 0.00;
                        }
                        int retva2 = odoOutBoundBoxQty.compareTo(0.00);
                        if (retva2 == 0) {
                            break;
                        }
                    }
                    whSkuInventoryTobefilledList = ListUtils.subtract(whSkuInventoryTobefilledList, skuInventoryTobefilledLst);
                }
                odoLineIdAndTobefilled.put(whOdoOutBoundBoxCommand.getOdoLineId(), whSkuInventoryTobefilledList);
                // 2.1.3.2 创建工作明细
                this.savePickingWorkLine(whOdoOutBoundBoxCommand, whSkuInventoryLst, whSkuInventoryTobefilledLst, userId, workCode);
                execOdoLineQty = execOdoLineQty + whSkuInventoryLst.size() + whSkuInventoryTobefilledLst.size();
            }
            // 2.1.4 更新工作头信息
            this.updatePickingWork(whWave, workCode, whOdoOutBoundBoxGroup);
            // 2.1.5 创建作业头
            String operationCode = this.savePickingOperation(workCode, whOdoOutBoundBoxGroup);
            // 2.1.6 创建作业明细
            this.savePickingOperationLine(workCode, operationCode, whOdoOutBoundBox.getOuId());
            for (WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand : whOdoOutBoundBoxCommandList) {
                // 2.1.3.3 设置出库箱行标识
                this.updateWhOdoOutBoundBoxCommand(whOdoOutBoundBoxCommand);
            }
            createWorkResultCommand.setOdoLineIdAndInventory(odoLineIdAndInventory);
            createWorkResultCommand.setOdoLineIdAndTobefilled(odoLineIdAndTobefilled);
            whWave.setExecOdoQty(execOdoQty);
            whWave.setExecOdoLineQty(execOdoLineQty);
            createWorkResultCommand.setWhWave(whWave);
        } catch (Exception e) {
            log.error("", e);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        return createWorkResultCommand;
    }

    /**
     * 波次外创建补货工作和作业
     * 
     * @param ouId
     * @param userId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void createReplenishmentWorkOutWave(List<WhSkuInventoryAllocatedCommand> whSkuInventoryAllocatedCommandLst, ReplenishmentRuleCommand replenishmentRuleCommand, Long userId) {
        try {
            // 创建拣货工作--创建工作头信息
            WhSkuInventoryAllocatedCommand siaCommand = whSkuInventoryAllocatedCommandLst.get(0);
            String replenishmentWorkCode = this.saveOutReplenishmentWork(siaCommand, userId);
            int rWorkLineTotal = 0;
            Set<String> replenishmentCodes = new HashSet<String>();
            // 循环统计的分组补货信息列表
            for (WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand : whSkuInventoryAllocatedCommandLst) {
                // 判断分配量与待移入量是否相等
                if (!skuInventoryAllocatedCommand.getQty().equals(skuInventoryAllocatedCommand.getToQty())) {
                    log.error("qty != toQty, qty:{}, toQty:{}", skuInventoryAllocatedCommand.getQty(), skuInventoryAllocatedCommand.getToQty());
                }
                // 创建补货工作明细
                this.saveReplenishmentWorkLine(replenishmentWorkCode, userId, skuInventoryAllocatedCommand);
                rWorkLineTotal = rWorkLineTotal + 1;
                // 获取明细补货单据号
                replenishmentCodes.add(skuInventoryAllocatedCommand.getReplenishmentCode());
            }
            // 校验工作明细数量是否正确
            if (rWorkLineTotal != whSkuInventoryAllocatedCommandLst.size()) {
                log.error("rWorkLineTotal is error, rWorkLineTotal:{}", rWorkLineTotal);
            }
            // 更新补货工作头信息
            this.updateOutReplenishmentWork(replenishmentWorkCode, siaCommand);
            // 生成作业头
            String replenishmentOperationCode = this.saveReplenishmentOperation(replenishmentWorkCode, siaCommand);
            // 基于工作明细生成作业明细
            int replenishmentOperationLineCount = this.saveReplenishmentOperationLine(replenishmentWorkCode, replenishmentOperationCode, replenishmentRuleCommand.getTaskOuId(), null);
            // 校验作业明细
            if (replenishmentOperationLineCount != rWorkLineTotal) {
                log.error("replenishmentOperationLineCount is error, replenishmentOperationLineCount:{}", replenishmentOperationLineCount);
            }
            // 判断补货单号对应库存是否都创完工作
            for (String replenishmentCode : replenishmentCodes) {
                Double totalQtyAllocated = skuInventoryAllocatedDao.getTotalQtyByReplenishmentCode(replenishmentCode, replenishmentRuleCommand.getTaskOuId());
                Double totalQtyWorkLine = workLineDao.getTotalQtyByReplenishmentCode(replenishmentCode, replenishmentRuleCommand.getTaskOuId());
                if (null != totalQtyAllocated && null != totalQtyWorkLine && totalQtyAllocated.equals(totalQtyWorkLine)) {
                    // 将补货任务行标识为已创建工作
                    ReplenishmentTask replenishmentTask = replenishmentTaskDao.findReplenishmentTaskByCode(replenishmentCode, replenishmentRuleCommand.getTaskOuId());
                    replenishmentTask.setIsCreateWork(true);
                    replenishmentTaskDao.saveOrUpdateByVersion(replenishmentTask);
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
    }

    /********************************************************************保存方法--开始******************************************************************/

    /**
     *  创建补货工作头信息
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String saveReplenishmentWork(Long waveId, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand, Long userId) {
        // 查询波次头信息
        WhWave whWave = new WhWave();
        whWave = this.waveDao.findWaveExtByIdAndOuIdAndLifecycle(waveId, skuInventoryAllocatedCommand.getOuId(), BaseModel.LIFECYCLE_NORMAL);
        // 查询波次主档信息
        WhWaveMaster whWaveMaster = waveMasterDao.findByIdExt(whWave.getWaveMasterId(), whWave.getOuId());
        // 获取工作类型
        WorkType workType = this.workTypeDao.findWorkTypeByworkCategory("REPLENISHMENT", skuInventoryAllocatedCommand.getOuId());
        // 根据容器ID获取容器CODE
        Container outerContainer = new Container();
        Container insideContainer = new Container();
        if (skuInventoryAllocatedCommand.getOuterContainerId() != null) {
            outerContainer = containerDao.findByIdExt(skuInventoryAllocatedCommand.getOuterContainerId(), skuInventoryAllocatedCommand.getOuId());
        }
        if (skuInventoryAllocatedCommand.getInsideContainerId() != null) {
            insideContainer = containerDao.findByIdExt(skuInventoryAllocatedCommand.getInsideContainerId(), skuInventoryAllocatedCommand.getOuId());
        }
        // 调编码生成器工作头实体标识
        String workCode = codeManager.generateCode(Constants.WMS, Constants.WHWORK_MODEL_URL, "", "WORK", null);
        // 封装数据
        WhWorkCommand whWorkCommand = new WhWorkCommand();
        // 工作号
        whWorkCommand.setCode(workCode);
        // 工作状态，系统常量
        whWorkCommand.setStatus(WorkStatus.NEW);
        // 仓库组织ID
        whWorkCommand.setOuId(skuInventoryAllocatedCommand.getOuId());
        // 工作类型编码
        whWorkCommand.setWorkType(null == workType ? null : workType.getCode());
        // 工作类别编码
        whWorkCommand.setWorkCategory("REPLENISHMENT");
        // 是否锁定 默认值：1
        whWorkCommand.setIsLocked(true);
        // 是否已迁出
        whWorkCommand.setIsAssignOut(false);
        // 是否短拣--执行时判断
        whWorkCommand.setIsShortPicking(null);
        // 是否波次内补货
        whWorkCommand.setIsWaveReplenish(true);
        // 是否拣货库存待移入
        whWorkCommand.setIsPickingTobefilled(null);
        // 是否多次作业--需计算后再更新
        whWorkCommand.setIsMultiOperation(null);
        // 当前工作明细设计到的所有库区编码信息列表--更新时获取数据
        whWorkCommand.setWorkArea(null);
        // 工作优先级
        whWorkCommand.setWorkPriority(null != whWaveMaster.getReplenishmentWorkPriority() ? whWaveMaster.getReplenishmentWorkPriority() : workType.getPriority());
        // 小批次
        whWorkCommand.setBatch(null);
        // 签出批次
        whWorkCommand.setAssignOutBatch(null);
        // 操作开始时间
        whWorkCommand.setStartTime(new Date());
        // 操作结束时间
        whWorkCommand.setFinishTime(new Date());
        // 波次ID
        whWorkCommand.setWaveId(waveId);
        // 波次号
        whWorkCommand.setWaveCode(whWave.getCode());
        // 订单号--更新时获取数据
        whWorkCommand.setOrderCode(null);
        // 库位--更新时获取数据
        whWorkCommand.setLocationCode(null);
        // 托盘--更新时获取数据
        whWorkCommand.setOuterContainerCode(null == outerContainer ? null : outerContainer.getCode());
        // 容器--更新时获取数据
        whWorkCommand.setContainerCode(null == insideContainer ? null : insideContainer.getCode());
        // 创建时间
        whWorkCommand.setCreateTime(new Date());
        // 最后操作时间
        whWorkCommand.setLastModifyTime(new Date());
        // 创建人ID
        whWorkCommand.setCreatedId(userId);
        // 修改人ID
        whWorkCommand.setModifiedId(userId);
        // 操作人ID
        whWorkCommand.setOperatorId(userId);
        // 是否启用 1:启用 0:停用
        whWorkCommand.setLifecycle(0);

        WhWork whWork = new WhWork();
        // 复制数据
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if (null != whWorkCommand.getId()) {
            workDao.saveOrUpdateByVersion(whWork);
        } else {
            workDao.insert(whWork);
        }

        return workCode;
    }

    /**
     *  创建补货工作明细
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveReplenishmentWorkLine(String replenishmentWorkCode, Long userId, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand) {

        // 判断是否整托整箱
        Boolean isWholeCase = false;

        if (null != skuInventoryAllocatedCommand.getInsideContainerId()) {
            WhSkuInventory skuInventory = new WhSkuInventory();
            WhSkuInventoryAllocatedCommand allocatedCommand = new WhSkuInventoryAllocatedCommand();
            WhSkuInventoryAllocatedCommand totalCommand = new WhSkuInventoryAllocatedCommand();
            WhSkuInventoryTobefilled skuInventoryTobefilled = new WhSkuInventoryTobefilled();
            Double onHandQty = 0.00;
            Double frozenQty = 0.00;
            skuInventory.setInsideContainerId(skuInventoryAllocatedCommand.getInsideContainerId());
            allocatedCommand.setInsideContainerId(skuInventoryAllocatedCommand.getInsideContainerId());
            totalCommand.setInsideContainerId(skuInventoryAllocatedCommand.getInsideContainerId());
            skuInventoryTobefilled.setInsideContainerId(skuInventoryAllocatedCommand.getInsideContainerId());
            skuInventoryTobefilled.setLocationId(skuInventoryAllocatedCommand.getLocationId());
            allocatedCommand.setReplenishmentCode(skuInventoryAllocatedCommand.getReplenishmentCode());
            allocatedCommand.setReplenishmentRuleId(skuInventoryAllocatedCommand.getReplenishmentRuleId());
            Double allocatedQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(allocatedCommand);
            Double totalQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(totalCommand);
            Double toBeFilledQty = skuInventoryTobefilledDao.skuInventoryTobefilledQty(skuInventoryTobefilled);
            List<WhSkuInventory> skuInventoryList = skuInventoryDao.getSkuInvListGroupUuid(skuInventory);
            if (null != skuInventoryList && 0 != skuInventoryList.size()) {
                for (WhSkuInventory whSkuInventory : skuInventoryList) {
                    onHandQty = onHandQty + whSkuInventory.getOnHandQty();
                    frozenQty = frozenQty + whSkuInventory.getFrozenQty();
                }
            }
            double zero = 0.0;
            int resultFrozen = frozenQty.compareTo(zero);
            int resultTo = 0;
            if (null != toBeFilledQty) {
                resultTo = toBeFilledQty.compareTo(zero);
            }
            if (totalQty.equals(allocatedQty) && onHandQty.equals(allocatedQty) && 0 == resultFrozen && 0 == resultTo) {
                isWholeCase = true;
            }
        }

        if (null != skuInventoryAllocatedCommand.getOuterContainerId()) {
            WhSkuInventory skuInventory = new WhSkuInventory();
            WhSkuInventoryAllocatedCommand allocatedCommand = new WhSkuInventoryAllocatedCommand();
            WhSkuInventoryAllocatedCommand totalCommand = new WhSkuInventoryAllocatedCommand();
            WhSkuInventoryTobefilled skuInventoryTobefilled = new WhSkuInventoryTobefilled();
            Double onHandQty = 0.00;
            Double frozenQty = 0.00;
            skuInventory.setOuterContainerId(skuInventoryAllocatedCommand.getOuterContainerId());
            allocatedCommand.setOuterContainerId(skuInventoryAllocatedCommand.getOuterContainerId());
            totalCommand.setOuterContainerId(skuInventoryAllocatedCommand.getOuterContainerId());
            skuInventoryTobefilled.setOuterContainerId(skuInventoryAllocatedCommand.getOuterContainerId());
            skuInventoryTobefilled.setLocationId(skuInventoryAllocatedCommand.getLocationId());
            allocatedCommand.setReplenishmentCode(skuInventoryAllocatedCommand.getReplenishmentCode());
            allocatedCommand.setReplenishmentRuleId(skuInventoryAllocatedCommand.getReplenishmentRuleId());
            Double allocatedQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(allocatedCommand);
            Double totalQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(totalCommand);
            Double toBeFilledQty = skuInventoryTobefilledDao.skuInventoryTobefilledQty(skuInventoryTobefilled);
            List<WhSkuInventory> skuInventoryList = skuInventoryDao.getSkuInvListGroupUuid(skuInventory);
            if (null != skuInventoryList && 0 != skuInventoryList.size()) {
                for (WhSkuInventory whSkuInventory : skuInventoryList) {
                    onHandQty = onHandQty + whSkuInventory.getOnHandQty();
                    frozenQty = frozenQty + whSkuInventory.getFrozenQty();
                }
            }
            double zero = 0.0;
            int resultFrozen = frozenQty.compareTo(zero);
            int resultTo = 0;
            if (null != toBeFilledQty) {
                resultTo = toBeFilledQty.compareTo(zero);
            }
            if (totalQty.equals(allocatedQty) && onHandQty.equals(allocatedQty) && 0 == resultFrozen && 0 == resultTo) {
                isWholeCase = true;
            }
        }

        // 根据出库单code获取出库单信息
        WhOdo odo = odoDao.findOdoByCodeAndOuId(skuInventoryAllocatedCommand.getOccupationCode(), skuInventoryAllocatedCommand.getOuId());

        // 获取工作头信息
        WhWorkCommand replenishmentWorkCommand = this.workDao.findWorkByWorkCode(replenishmentWorkCode, skuInventoryAllocatedCommand.getOuId());
        // 判断判断当前分组库存是否整托盘整箱分配

        // 调编码生成器工作明细实体标识
        String replenishmentWorkLineCode = codeManager.generateCode(Constants.WMS, Constants.WHWORKLINE_MODEL_URL, "", "WORKLINE", null);
        // 补货工作明细
        WhWorkLineCommand whWorkLineCommand = new WhWorkLineCommand();
        // 工作明细号
        whWorkLineCommand.setLineCode(replenishmentWorkLineCode);
        // 工作ID
        whWorkLineCommand.setWorkId(replenishmentWorkCommand.getId());
        // 仓库组织ID
        whWorkLineCommand.setOuId(replenishmentWorkCommand.getOuId());
        // 操作开始时间
        whWorkLineCommand.setStartTime(null);
        // 操作结束时间
        whWorkLineCommand.setFinishTime(null);
        // 商品ID
        whWorkLineCommand.setSkuId(skuInventoryAllocatedCommand.getSkuId());
        // 计划量
        whWorkLineCommand.setQty(skuInventoryAllocatedCommand.getQty());
        // 执行量/完成量
        whWorkLineCommand.setCompleteQty(null);
        // 取消量
        whWorkLineCommand.setCancelQty(null);
        // 库存状态
        whWorkLineCommand.setInvStatus(skuInventoryAllocatedCommand.getInvStatus());
        // 库存类型
        whWorkLineCommand.setInvType(skuInventoryAllocatedCommand.getInvType());
        // 批次号
        whWorkLineCommand.setBatchNumber(skuInventoryAllocatedCommand.getBatchNumber());
        // 生产日期
        whWorkLineCommand.setMfgDate(skuInventoryAllocatedCommand.getMfgDate());
        // 失效日期
        whWorkLineCommand.setExpDate(skuInventoryAllocatedCommand.getExpDate());
        // 最小失效日期
        whWorkLineCommand.setMinExpDate(null);
        // 最大失效日期
        whWorkLineCommand.setMaxExpDate(null);
        // 原产地
        whWorkLineCommand.setCountryOfOrigin(skuInventoryAllocatedCommand.getCountryOfOrigin());
        // 库存属性1
        whWorkLineCommand.setInvAttr1(skuInventoryAllocatedCommand.getInvAttr1());
        // 库存属性2
        whWorkLineCommand.setInvAttr2(skuInventoryAllocatedCommand.getInvAttr2());
        // 库存属性3
        whWorkLineCommand.setInvAttr3(skuInventoryAllocatedCommand.getInvAttr3());
        // 库存属性4
        whWorkLineCommand.setInvAttr4(skuInventoryAllocatedCommand.getInvAttr4());
        // 库存属性5
        whWorkLineCommand.setInvAttr5(skuInventoryAllocatedCommand.getInvAttr5());
        // 内部对接码
        whWorkLineCommand.setUuid(skuInventoryAllocatedCommand.getUuid());
        // 原始库位
        whWorkLineCommand.setFromLocationId(skuInventoryAllocatedCommand.getLocationId());
        // 原始库位外部容器
        whWorkLineCommand.setFromOuterContainerId(skuInventoryAllocatedCommand.getOuterContainerId());
        // 原始库位内部容器
        whWorkLineCommand.setFromInsideContainerId(skuInventoryAllocatedCommand.getInsideContainerId());
        // 使用出库箱，耗材ID
        whWorkLineCommand.setUseOutboundboxId(null);
        // 使用出库箱编码
        whWorkLineCommand.setUseOutboundboxCode(null);
        // 使用容器
        whWorkLineCommand.setUseContainerId(null);
        // 使用外部容器，小车
        whWorkLineCommand.setUseOuterContainerId(null);
        // 使用货格编码数
        whWorkLineCommand.setUseContainerLatticeNo(null);
        // 目标库位 --捡货模式没有
        whWorkLineCommand.setToLocationId(skuInventoryAllocatedCommand.getToLocationId());
        // 目标库位外部容器 --捡货模式没有
        whWorkLineCommand.setToOuterContainerId(null);
        // 目标库位内部容器 --捡货模式没有
        whWorkLineCommand.setToInsideContainerId(null);
        // 是否整托整箱
        whWorkLineCommand.setIsWholeCase(isWholeCase);
        // 出库单ID
        whWorkLineCommand.setOdoId(odo == null ? null : odo.getId());
        // 出库单明细ID
        whWorkLineCommand.setOdoLineId(skuInventoryAllocatedCommand.getOccupationLineId());
        // 补货单据号
        whWorkLineCommand.setReplenishmentCode(skuInventoryAllocatedCommand.getReplenishmentCode());
        // 创建时间
        whWorkLineCommand.setCreateTime(new Date());
        // 最后操作时间
        whWorkLineCommand.setLastModifyTime(new Date());
        // 操作人ID
        whWorkLineCommand.setOperatorId(userId);

        WhWorkLine whWorkLine = new WhWorkLine();
        // 复制数据
        BeanUtils.copyProperties(whWorkLineCommand, whWorkLine);
        if (null != whWorkLineCommand.getId()) {
            workLineDao.saveOrUpdateByVersion(whWorkLine);
        } else {
            workLineDao.insert(whWorkLine);
        }
    }

    /**
     *  更新补货工作头信息
     * 
     * @param waveId
     * @param replenishmentWorkCode
     * @param skuInventoryAllocatedCommand
     * @return
     */

    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateReplenishmentWork(Long waveId, String replenishmentWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand) {
        // 获取工作头信息
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(replenishmentWorkCode, skuInventoryAllocatedCommand.getOuId());
        // 获取工作明细信息列表
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), skuInventoryAllocatedCommand.getOuId());

        // 查询波次头信息
        if (null == waveId || null == skuInventoryAllocatedCommand.getOuId()) {
            log.error("waveId:{}, ouId:{}", waveId, skuInventoryAllocatedCommand.getOuId());
        }
        WhWave whWave = new WhWave();
        try {
            whWave = this.waveDao.findWaveExtByIdAndOuIdAndLifecycle(waveId, skuInventoryAllocatedCommand.getOuId(), BaseModel.LIFECYCLE_NORMAL);
        } catch (Exception e) {
            log.error("findWaveExtByIdAndOuIdAndLifecycle is error, ouId:{}, waveId:{}, lifecycle:{}", waveId, skuInventoryAllocatedCommand.getOuId(), BaseModel.LIFECYCLE_NORMAL);
            log.error("", e);
        }
        // 查询波次主档信息
        WhWaveMaster whWaveMaster = waveMasterDao.findByIdExt(whWave.getWaveMasterId(), whWave.getOuId());
        // 获取工作类型
        WorkType workType = this.workTypeDao.findWorkTypeByworkCategory("REPLENISHMENT", skuInventoryAllocatedCommand.getOuId());
        String workArea = "";
        int count = 0;
        Boolean isFromLocationId = true;
        Boolean isFromOuterContainerId = true;
        Boolean isFromInsideContainerId = true;
        Boolean isOdoId = true;

        if (null != whWorkLineCommandList && whWorkLineCommandList.size() > 0) {
            for (WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList) {
                if (count != 0) {
                    // 获取上一次循环的实体类
                    WhWorkLineCommand whWorkLineCommandBefor = whWorkLineCommandList.get(count - 1);

                    if (0 == whWorkLineCommandBefor.getFromLocationId().compareTo(whWorkLineCommand.getFromLocationId())) {
                        isFromLocationId = false;
                    }
                    if (0 == whWorkLineCommandBefor.getFromOuterContainerId().compareTo(whWorkLineCommand.getFromOuterContainerId())) {
                        isFromOuterContainerId = false;
                    }
                    if (0 == whWorkLineCommandBefor.getFromInsideContainerId().compareTo(whWorkLineCommand.getFromInsideContainerId())) {
                        isFromInsideContainerId = false;
                    }
                    if (0 == whWorkLineCommandBefor.getOdoId().compareTo(whWorkLineCommand.getOdoId())) {
                        isOdoId = false;
                    }
                }

                LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommand.getFromLocationId(), whWorkLineCommand.getOuId());
                if (null != locationCommand) {
                    Area area = areaDao.findByIdExt(locationCommand.getWorkAreaId(), locationCommand.getOuId());
                    if (workArea == "") {
                        workArea = area.getAreaCode();
                    } else {
                        workArea = workArea + "," + area.getAreaCode();
                    }
                }
                // 索引自增
                count++;
            }
        } else {
            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommandList.get(0).getFromLocationId(), whWorkLineCommandList.get(0).getOuId());
            if (null != locationCommand) {
                Area area = areaDao.findByIdExt(locationCommand.getWorkAreaId(), locationCommand.getOuId());
                if (workArea == "") {
                    workArea = area.getAreaCode();
                } else {
                    workArea = workArea + "," + area.getAreaCode();
                }
            }
        }
        // 判断工作明细是否只有唯一库位
        if (isFromLocationId == true) {
            // 获取库位表数据
            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommandList.get(0).getFromLocationId(), whWorkLineCommandList.get(0).getOuId());
            if (null != locationCommand) {
                // 设置库位
                whWorkCommand.setLocationCode(locationCommand.getCode());
            }
        }
        // 判断工作明细是否只有唯一外部容器
        if (isFromOuterContainerId == true) {
            // 根据容器ID获取容器CODE
            Container containerOut = new Container();
            containerOut = containerDao.findByIdExt(whWorkLineCommandList.get(0).getFromOuterContainerId(), skuInventoryAllocatedCommand.getOuId());
            if (null != containerOut) {
                // 设置外部容器
                whWorkCommand.setOuterContainerCode(containerOut.getCode());
            }
        }
        // 判断据工作明细是否只有唯一内部容器
        if (isFromInsideContainerId == true) {
            // 根据容器ID获取容器CODE
            Container containerIn = new Container();
            containerIn = containerDao.findByIdExt(whWorkLineCommandList.get(0).getFromInsideContainerId(), skuInventoryAllocatedCommand.getOuId());
            if (null != containerIn) {
                // 设置内部容器
                whWorkCommand.setContainerCode(containerIn.getCode());
            }
        }
        // 判断据工作明细是否只有唯一出库单
        if (isOdoId == true) {
            WhOdo whOdo = this.odoDao.findByIdOuId(whWorkLineCommandList.get(0).getOdoId(), skuInventoryAllocatedCommand.getOuId());
            if (null != whOdo) {
                // 设置订单号
                whWorkCommand.setOrderCode(whOdo.getEcOrderCode());
            }
        }

        // 当前工作明细设计到的所有库区编码信息列表
        whWorkCommand.setWorkArea(workArea);
        // 是否锁定 默认值：1
        whWorkCommand.setIsLocked(whWaveMaster.getIsAutoReleaseWork());
        // 工作优先级
        whWorkCommand.setWorkPriority(null != whWaveMaster.getReplenishmentWorkPriority() ? whWaveMaster.getReplenishmentWorkPriority() : workType.getPriority());

        WhWork whWork = new WhWork();
        // 复制数据
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if (null != whWorkCommand.getId()) {
            workDao.saveOrUpdateByVersion(whWork);
        } else {
            workDao.insert(whWork);
        }
    }

    /**
     *  创建补货作业信息
     * 
     * @param replenishmentWorkCode
     * @param skuInventoryAllocatedCommand
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String saveReplenishmentOperation(String replenishmentWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand) {

        Boolean isWholeCase = true;
        // 获取工作头信息
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(replenishmentWorkCode, skuInventoryAllocatedCommand.getOuId());
        // 获取工作明细信息列表
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), skuInventoryAllocatedCommand.getOuId());
        // 判断是否整托整箱
        for (WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList) {
            if (false == whWorkLineCommand.getIsWholeCase()) {
                isWholeCase = false;
            }
        }
        // 调编码生成器工作明细实体标识
        String operationCode = codeManager.generateCode(Constants.WMS, Constants.WHOPERATION_MODEL_URL, "", "OPERATION", null);

        WhOperationCommand WhOperationCommand = new WhOperationCommand();
        // 作业号
        WhOperationCommand.setCode(operationCode);
        // 状态
        WhOperationCommand.setStatus(OperationStatus.NEW);
        // 工作ID
        WhOperationCommand.setWorkId(whWorkCommand.getId());
        // 仓库组织ID
        WhOperationCommand.setOuId(whWorkCommand.getOuId());
        // 工作类型编码
        WhOperationCommand.setWorkType(whWorkCommand.getWorkType());
        // 工作类别编码
        WhOperationCommand.setWorkCategory(whWorkCommand.getWorkCategory());
        // 优先级
        WhOperationCommand.setWorkPriority(whWorkCommand.getWorkPriority());
        // 小批次
        WhOperationCommand.setBatch(whWorkCommand.getBatch());
        // 操作开始时间
        WhOperationCommand.setStartTime(whWorkCommand.getStartTime());
        // 操作结束时间
        WhOperationCommand.setFinishTime(whWorkCommand.getFinishTime());
        // 波次ID
        WhOperationCommand.setWaveId(whWorkCommand.getWaveId());
        // 波次号
        WhOperationCommand.setWaveCode(whWorkCommand.getWaveCode());
        // 订单号
        WhOperationCommand.setOrderCode(whWorkCommand.getOrderCode());
        // 库位
        WhOperationCommand.setLocationCode(whWorkCommand.getLocationCode());
        // 托盘
        WhOperationCommand.setOuterContainerCode(whWorkCommand.getOuterContainerCode());
        // 容器
        WhOperationCommand.setContainerCode(whWorkCommand.getContainerCode());
        // 是否整托整箱
        WhOperationCommand.setIsWholeCase(isWholeCase);
        // 是否短拣
        WhOperationCommand.setIsShortPicking(whWorkCommand.getIsShortPicking());
        // 是否拣货完成
        WhOperationCommand.setIsPickingFinish(false);
        // 是否波次内补货
        WhOperationCommand.setIsWaveReplenish(whWorkCommand.getIsWaveReplenish());
        // 是否拣货库存待移入
        WhOperationCommand.setIsPickingTobefilled(whWorkCommand.getIsPickingTobefilled());
        // 创建时间
        WhOperationCommand.setCreateTime(whWorkCommand.getCreateTime());
        // 最后操作时间
        WhOperationCommand.setLastModifyTime(whWorkCommand.getLastModifyTime());
        // 创建人ID
        WhOperationCommand.setCreatedId(whWorkCommand.getCreatedId());
        // 修改人ID
        WhOperationCommand.setModifiedId(whWorkCommand.getModifiedId());
        // 操作人ID
        WhOperationCommand.setOperatorId(whWorkCommand.getOperatorId());
        // 是否启用 1:启用 0:停用
        WhOperationCommand.setLifecycle(whWorkCommand.getLifecycle());

        WhOperation whOperation = new WhOperation();
        // 复制数据
        BeanUtils.copyProperties(WhOperationCommand, whOperation);
        if (null != WhOperationCommand.getId()) {
            operationDao.saveOrUpdateByVersion(whOperation);
        } else {
            operationDao.insert(whOperation);
        }
        return operationCode;
    }

    /**
     * 创建补货作业明细
     * 
     * @param replenishmentWorkCode
     * @param replenishmentOperationCode
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int saveReplenishmentOperationLine(String replenishmentWorkCode, String replenishmentOperationCode, Long ouId, List<WhSkuInventoryAllocatedCommand> skuInventoryAllocatedCommandLst) {
        // 获取工作头信息
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(replenishmentWorkCode, ouId);
        // 获取工作明细信息列表
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), ouId);
        // 获取作业头信息
        WhOperationCommand WhOperationCommand = this.operationDao.findOperationByCode(replenishmentOperationCode, ouId);
        Map<String, Double> allocatedMap = new HashMap<String, Double>();
        if(null != skuInventoryAllocatedCommandLst){
            for(WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand : skuInventoryAllocatedCommandLst){
                allocatedMap.put(skuInventoryAllocatedCommand.getUuid(), skuInventoryAllocatedCommand.getQty());
            }
        }
        int count = 0;
        for (WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList) {
            if(null != skuInventoryAllocatedCommandLst && null == allocatedMap.get(whWorkLineCommand.getUuid())){
                continue;
            }
            WhOperationLineCommand WhOperationLineCommand = new WhOperationLineCommand();
            // 作业ID
            WhOperationLineCommand.setOperationId(WhOperationCommand.getId());
            // 工作明细ID
            WhOperationLineCommand.setWorkLineId(whWorkLineCommand.getId());
            // 仓库组织ID
            WhOperationLineCommand.setOuId(whWorkLineCommand.getOuId());
            // 操作开始时间
            WhOperationLineCommand.setStartTime(whWorkLineCommand.getStartTime());
            // 操作结束时间
            WhOperationLineCommand.setFinishTime(whWorkLineCommand.getFinishTime());
            // 商品ID
            WhOperationLineCommand.setSkuId(whWorkLineCommand.getSkuId());
            // 计划量
            if (null == skuInventoryAllocatedCommandLst) {
                WhOperationLineCommand.setQty(whWorkLineCommand.getQty());
            } else {
                WhOperationLineCommand.setQty(allocatedMap.get(whWorkLineCommand.getUuid()));
            }
            // 库存状态
            WhOperationLineCommand.setInvStatus(whWorkLineCommand.getInvStatus());
            // 库存类型
            WhOperationLineCommand.setInvType(whWorkLineCommand.getInvType());
            // 批次号
            WhOperationLineCommand.setBatchNumber(whWorkLineCommand.getBatchNumber());
            // 生产日期
            WhOperationLineCommand.setMfgDate(whWorkLineCommand.getMfgDate());
            // 失效日期
            WhOperationLineCommand.setExpDate(whWorkLineCommand.getExpDate());
            // 最小失效日期
            WhOperationLineCommand.setMinExpDate(whWorkLineCommand.getMinExpDate());
            // 最大失效日期
            WhOperationLineCommand.setMaxExpDate(whWorkLineCommand.getMaxExpDate());
            // 原产地
            WhOperationLineCommand.setCountryOfOrigin(whWorkLineCommand.getCountryOfOrigin());
            // 库存属性1
            WhOperationLineCommand.setInvAttr1(whWorkLineCommand.getInvAttr1());
            // 库存属性2
            WhOperationLineCommand.setInvAttr2(whWorkLineCommand.getInvAttr2());
            // 库存属性3
            WhOperationLineCommand.setInvAttr3(whWorkLineCommand.getInvAttr3());
            // 库存属性4
            WhOperationLineCommand.setInvAttr4(whWorkLineCommand.getInvAttr4());
            // 库存属性5
            WhOperationLineCommand.setInvAttr5(whWorkLineCommand.getInvAttr5());
            // 内部对接码
            WhOperationLineCommand.setUuid(whWorkLineCommand.getUuid());
            // 原始库位
            WhOperationLineCommand.setFromLocationId(whWorkLineCommand.getFromLocationId());
            // 原始库位外部容器
            WhOperationLineCommand.setFromOuterContainerId(whWorkLineCommand.getFromOuterContainerId());
            // 原始库位内部容器
            WhOperationLineCommand.setFromInsideContainerId(whWorkLineCommand.getFromInsideContainerId());
            // 使用出库箱，耗材ID
            WhOperationLineCommand.setUseOutboundboxId(whWorkLineCommand.getUseOutboundboxId());
            // 使用出库箱编码
            WhOperationLineCommand.setUseOutboundboxCode(whWorkLineCommand.getUseOutboundboxCode());
            // 使用容器
            WhOperationLineCommand.setUseContainerId(whWorkLineCommand.getUseContainerId());
            // 使用外部容器，小车
            WhOperationLineCommand.setUseOuterContainerId(whWorkLineCommand.getUseOuterContainerId());
            // 使用货格编码数
            WhOperationLineCommand.setUseContainerLatticeNo(whWorkLineCommand.getUseContainerLatticeNo());
            // 目标库位
            WhOperationLineCommand.setToLocationId(whWorkLineCommand.getToLocationId());
            // 目标库位外部容器
            WhOperationLineCommand.setToOuterContainerId(whWorkLineCommand.getToOuterContainerId());
            // 目标库位内部容器
            WhOperationLineCommand.setToInsideContainerId(whWorkLineCommand.getToInsideContainerId());
            // 出库单ID
            WhOperationLineCommand.setOdoId(whWorkLineCommand.getOdoId());
            // 出库单明细ID
            WhOperationLineCommand.setOdoLineId(whWorkLineCommand.getOdoLineId());
            // 补货单据号
            WhOperationLineCommand.setReplenishmentCode(whWorkLineCommand.getReplenishmentCode());
            // 创建时间
            WhOperationLineCommand.setCreateTime(new Date());
            // 最后操作时间
            WhOperationLineCommand.setLastModifyTime(whWorkLineCommand.getLastModifyTime());
            // 操作人ID
            WhOperationLineCommand.setOperatorId(whWorkLineCommand.getOperatorId());

            WhOperationLine whOperationLine = new WhOperationLine();
            // 复制数据
            BeanUtils.copyProperties(WhOperationLineCommand, whOperationLine);
            if (null != WhOperationLineCommand.getId()) {
                operationLineDao.saveOrUpdateByVersion(whOperationLine);
            } else {
                operationLineDao.insert(whOperationLine);
            }
            count = count + 1;
        }
        return count;
    }

    /**
     *  创建工作头信息
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String savePickingWork(WhWave whWave, WhOdoOutBoundBox whOdoOutBoundBox, Long userId) {
        // 查询波次主档信息
        WhWaveMaster whWaveMaster = waveMasterDao.findByIdExt(whWave.getWaveMasterId(), whWave.getOuId());
        // 判断是否有补货工作
        List<ReplenishmentTask> replenishmentTaskLst = replenishmentTaskManager.findTaskByWave(whWave.getId(), whWave.getOuId());
        // 获取工作类型
        WorkType workType = this.workTypeDao.findWorkTypeByworkCategory("PICKING", whOdoOutBoundBox.getOuId());
        // 根据容器ID获取容器CODE
        Container container = new Container();
        if (whOdoOutBoundBox.getOuterContainerId() != null) {
            container = containerDao.findByIdExt(whOdoOutBoundBox.getOuterContainerId(), whOdoOutBoundBox.getOuId());
        } else {
            container = containerDao.findByIdExt(whOdoOutBoundBox.getContainerId(), whOdoOutBoundBox.getOuId());
        }
        // 调编码生成器工作头实体标识
        String workCode = codeManager.generateCode(Constants.WMS, Constants.WHWORK_MODEL_URL, "", "WORK", null);

        // 所有值为null的字段，将会在更新工作头信息时获取，如果到时候还没有值，那就是被骗了
        WhWorkCommand whWorkCommand = new WhWorkCommand();
        // 工作号
        whWorkCommand.setCode(workCode);
        // 工作状态，系统常量
        whWorkCommand.setStatus(WorkStatus.NEW);
        // 仓库组织ID
        whWorkCommand.setOuId(whOdoOutBoundBox.getOuId());
        // 工作类型编码
        whWorkCommand.setWorkType(null == workType ? null : workType.getCode());
        // 工作类别编码
        whWorkCommand.setWorkCategory("PICKING");
        // 是否锁定 默认值：1
        whWorkCommand.setIsLocked(true);
        // 是否已迁出
        whWorkCommand.setIsAssignOut(false);
        // 当前工作明细设计到的所有库区编码信息列表--更新时获取数据
        whWorkCommand.setWorkArea(null);
        // 工作优先级
        if (null != replenishmentTaskLst && 0 < replenishmentTaskLst.size()) {
            whWorkCommand.setWorkPriority(null != whWaveMaster.getPickingExtPriority() ? whWaveMaster.getPickingExtPriority() : workType.getPriority());
        } else {
            whWorkCommand.setWorkPriority(null != whWaveMaster.getPickingWorkPriority() ? whWaveMaster.getPickingWorkPriority() : workType.getPriority());
        }
        // 小批次
        whWorkCommand.setBatch(whOdoOutBoundBox.getBoxBatch());
        // 操作开始时间
        whWorkCommand.setStartTime(new Date());
        // 操作结束时间
        whWorkCommand.setFinishTime(new Date());
        // 波次ID
        whWorkCommand.setWaveId(whOdoOutBoundBox.getWaveId());
        // 波次号
        whWorkCommand.setWaveCode(whWave.getCode());
        // 订单号--更新时获取数据
        whWorkCommand.setOrderCode(null);
        // 库位--更新时获取数据
        whWorkCommand.setLocationCode(null);
        // 托盘
        if (whOdoOutBoundBox.getOuterContainerId() != null) {
            whWorkCommand.setOuterContainerCode(null == container ? null : container.getCode());
        }
        // 容器
        if (whOdoOutBoundBox.getOuterContainerId() == null) {
            whWorkCommand.setContainerCode(null == container ? null : container.getCode());
        }
        // 创建时间
        whWorkCommand.setCreateTime(new Date());
        // 最后操作时间
        whWorkCommand.setLastModifyTime(new Date());
        // 创建人ID
        whWorkCommand.setCreatedId(userId);
        // 修改人ID
        whWorkCommand.setModifiedId(userId);
        // 操作人ID
        whWorkCommand.setOperatorId(userId);
        // 是否启用 1:启用 0:停用
        whWorkCommand.setLifecycle(0);

        WhWork whWork = new WhWork();
        // 复制数据
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if (null != whWorkCommand.getId()) {
            workDao.saveOrUpdateByVersion(whWork);
        } else {
            workDao.insert(whWork);
        }
        return workCode;
    }

    /**
     *  创建工作明细信息
     * 
     * @param whOdoOutBoundBox
     * @param whSkuInventoryList
     * @param userId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void savePickingWorkLine(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand, List<WhSkuInventory> whSkuInventoryList, List<WhSkuInventoryTobefilled> whSkuInventoryTobefilledList, Long userId, String workCode) {
        // 获取工作头信息
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(workCode, whOdoOutBoundBoxCommand.getOuId());
        // 查询对应的耗材
        Long skuId = odoOutBoundBoxDao.findOutboundboxType(whOdoOutBoundBoxCommand.getOutbounxboxTypeId(), whOdoOutBoundBoxCommand.getOutbounxboxTypeCode(), whOdoOutBoundBoxCommand.getOuId());
        if (whSkuInventoryList == null) {
            log.error("whSkuInventoryList is null");
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        for (WhSkuInventory whSkuInventory : whSkuInventoryList) {

            // 调编码生成器工作明细实体标识
            String workLineCode = codeManager.generateCode(Constants.WMS, Constants.WHWORKLINE_MODEL_URL, "", "WORKLINE", null);

            WhWorkLineCommand whWorkLineCommand = new WhWorkLineCommand();
            // 工作明细号
            whWorkLineCommand.setLineCode(workLineCode);
            // 工作ID
            whWorkLineCommand.setWorkId(whWorkCommand.getId());
            // 仓库组织ID
            whWorkLineCommand.setOuId(whOdoOutBoundBoxCommand.getOuId());
            // 操作开始时间
            whWorkLineCommand.setStartTime(null);
            // 操作结束时间
            whWorkLineCommand.setFinishTime(null);
            // 商品ID
            whWorkLineCommand.setSkuId(whSkuInventory.getSkuId());
            // 计划量
            if (whSkuInventoryList.size() == 1 && (null == whSkuInventoryTobefilledList || 0 == whSkuInventoryTobefilledList.size())) {
                whWorkLineCommand.setQty(whOdoOutBoundBoxCommand.getQty());
            } else {
                whWorkLineCommand.setQty(whSkuInventory.getOnHandQty());
            }
            // 执行量/完成量
            whWorkLineCommand.setCompleteQty(null);
            // 取消量
            whWorkLineCommand.setCancelQty(null);
            // 库存状态
            whWorkLineCommand.setInvStatus(whSkuInventory.getInvStatus());
            // 库存类型
            whWorkLineCommand.setInvType(whSkuInventory.getInvType());
            // 批次号
            whWorkLineCommand.setBatchNumber(whSkuInventory.getBatchNumber());
            // 生产日期
            whWorkLineCommand.setMfgDate(whSkuInventory.getMfgDate());
            // 失效日期
            whWorkLineCommand.setExpDate(whSkuInventory.getExpDate());
            // 最小失效日期
            whWorkLineCommand.setMinExpDate(null);
            // 最大失效日期
            whWorkLineCommand.setMaxExpDate(null);
            // 原产地
            whWorkLineCommand.setCountryOfOrigin(whSkuInventory.getCountryOfOrigin());
            // 库存属性1
            whWorkLineCommand.setInvAttr1(whSkuInventory.getInvAttr1());
            // 库存属性2
            whWorkLineCommand.setInvAttr2(whSkuInventory.getInvAttr2());
            // 库存属性3
            whWorkLineCommand.setInvAttr3(whSkuInventory.getInvAttr3());
            // 库存属性4
            whWorkLineCommand.setInvAttr4(whSkuInventory.getInvAttr4());
            // 库存属性5
            whWorkLineCommand.setInvAttr5(whSkuInventory.getInvAttr5());
            // 内部对接码
            whWorkLineCommand.setUuid(whSkuInventory.getUuid());
            // 原始库位
            whWorkLineCommand.setFromLocationId(whSkuInventory.getLocationId());
            // 原始库位外部容器
            whWorkLineCommand.setFromOuterContainerId(whSkuInventory.getOuterContainerId());
            // 原始库位内部容器
            whWorkLineCommand.setFromInsideContainerId(whSkuInventory.getInsideContainerId());
            // 使用出库箱，耗材ID
            whWorkLineCommand.setUseOutboundboxId(skuId);
            // 使用出库箱编码
            whWorkLineCommand.setUseOutboundboxCode(whOdoOutBoundBoxCommand.getOutbounxboxTypeCode());
            // 使用容器
            whWorkLineCommand.setUseContainerId(whOdoOutBoundBoxCommand.getContainerId());
            // 使用外部容器，小车
            whWorkLineCommand.setUseOuterContainerId(whOdoOutBoundBoxCommand.getOuterContainerId());
            // 使用货格编码数
            whWorkLineCommand.setUseContainerLatticeNo(whOdoOutBoundBoxCommand.getContainerLatticeNo());
            // 目标库位 --捡货模式没有
            whWorkLineCommand.setToLocationId(null);
            // 目标库位外部容器 --捡货模式没有
            whWorkLineCommand.setToOuterContainerId(null);
            // 目标库位内部容器 --捡货模式没有
            whWorkLineCommand.setToInsideContainerId(null);
            if (null != whOdoOutBoundBoxCommand.getWholeCase()) {
                // 是否整托整箱
                whWorkLineCommand.setIsWholeCase(true);
            } else {
                // 是否整托整箱
                whWorkLineCommand.setIsWholeCase(false);
            }
            // 出库单ID
            whWorkLineCommand.setOdoId(whOdoOutBoundBoxCommand.getOdoId());
            // 出库单明细ID
            whWorkLineCommand.setOdoLineId(whOdoOutBoundBoxCommand.getOdoLineId());
            // 补货单据号
            whWorkLineCommand.setReplenishmentCode(null);
            // 创建时间
            whWorkLineCommand.setCreateTime(new Date());
            // 最后操作时间
            whWorkLineCommand.setLastModifyTime(new Date());
            // 操作人ID
            whWorkLineCommand.setOperatorId(userId);

            WhWorkLine whWorkLine = new WhWorkLine();
            // 复制数据
            BeanUtils.copyProperties(whWorkLineCommand, whWorkLine);
            if (null != whWorkLineCommand.getId()) {
                workLineDao.saveOrUpdateByVersion(whWorkLine);
            } else {
                workLineDao.insert(whWorkLine);
            }
        }
        if (null != whSkuInventoryTobefilledList) {
            if (0 != whSkuInventoryTobefilledList.size()) {
                WhWork whWork = new WhWork();
                // 复制数据
                BeanUtils.copyProperties(whWorkCommand, whWork);
                whWork.setIsPickingTobefilled(true);
                workDao.saveOrUpdateByVersion(whWork);
            }
            // 生成待移入工作明细
            for (WhSkuInventoryTobefilled whSkuInventoryTobefilled : whSkuInventoryTobefilledList) {

                // 调编码生成器工作明细实体标识
                String workLineCode = codeManager.generateCode(Constants.WMS, Constants.WHWORKLINE_MODEL_URL, "", "WORKLINE", null);

                WhWorkLineCommand whWorkLineCommand = new WhWorkLineCommand();
                // 工作明细号
                whWorkLineCommand.setLineCode(workLineCode);
                // 工作ID
                whWorkLineCommand.setWorkId(whWorkCommand.getId());
                // 仓库组织ID
                whWorkLineCommand.setOuId(whOdoOutBoundBoxCommand.getOuId());
                // 操作开始时间
                whWorkLineCommand.setStartTime(null);
                // 操作结束时间
                whWorkLineCommand.setFinishTime(null);
                // 商品ID
                whWorkLineCommand.setSkuId(whSkuInventoryTobefilled.getSkuId());
                // 计划量
                if (null == whSkuInventoryList && whSkuInventoryTobefilledList.size() == 1) {
                    whWorkLineCommand.setQty(whOdoOutBoundBoxCommand.getQty());
                } else {
                    whWorkLineCommand.setQty(whSkuInventoryTobefilled.getQty());
                }
                // 执行量/完成量
                whWorkLineCommand.setCompleteQty(null);
                // 取消量
                whWorkLineCommand.setCancelQty(null);
                // 库存状态
                whWorkLineCommand.setInvStatus(whSkuInventoryTobefilled.getInvStatus());
                // 库存类型
                whWorkLineCommand.setInvType(whSkuInventoryTobefilled.getInvType());
                // 批次号
                whWorkLineCommand.setBatchNumber(whSkuInventoryTobefilled.getBatchNumber());
                // 生产日期
                whWorkLineCommand.setMfgDate(whSkuInventoryTobefilled.getMfgDate());
                // 失效日期
                whWorkLineCommand.setExpDate(whSkuInventoryTobefilled.getExpDate());
                // 最小失效日期
                whWorkLineCommand.setMinExpDate(null);
                // 最大失效日期
                whWorkLineCommand.setMaxExpDate(null);
                // 原产地
                whWorkLineCommand.setCountryOfOrigin(whSkuInventoryTobefilled.getCountryOfOrigin());
                // 库存属性1
                whWorkLineCommand.setInvAttr1(whSkuInventoryTobefilled.getInvAttr1());
                // 库存属性2
                whWorkLineCommand.setInvAttr2(whSkuInventoryTobefilled.getInvAttr2());
                // 库存属性3
                whWorkLineCommand.setInvAttr3(whSkuInventoryTobefilled.getInvAttr3());
                // 库存属性4
                whWorkLineCommand.setInvAttr4(whSkuInventoryTobefilled.getInvAttr4());
                // 库存属性5
                whWorkLineCommand.setInvAttr5(whSkuInventoryTobefilled.getInvAttr5());
                // 内部对接码
                whWorkLineCommand.setUuid(whSkuInventoryTobefilled.getUuid());
                // 原始库位
                whWorkLineCommand.setFromLocationId(whSkuInventoryTobefilled.getLocationId());
                // 原始库位外部容器
                whWorkLineCommand.setFromOuterContainerId(whSkuInventoryTobefilled.getOuterContainerId());
                // 原始库位内部容器
                whWorkLineCommand.setFromInsideContainerId(whSkuInventoryTobefilled.getInsideContainerId());
                // 使用出库箱，耗材ID
                whWorkLineCommand.setUseOutboundboxId(skuId);
                // 使用出库箱编码
                whWorkLineCommand.setUseOutboundboxCode(whOdoOutBoundBoxCommand.getOutbounxboxTypeCode());
                // 使用容器
                whWorkLineCommand.setUseContainerId(whOdoOutBoundBoxCommand.getContainerId());
                // 使用外部容器，小车
                whWorkLineCommand.setUseOuterContainerId(whOdoOutBoundBoxCommand.getOuterContainerId());
                // 使用货格编码数
                whWorkLineCommand.setUseContainerLatticeNo(whOdoOutBoundBoxCommand.getContainerLatticeNo());
                // 目标库位 --捡货模式没有
                whWorkLineCommand.setToLocationId(whSkuInventoryTobefilled.getLocationId());
                // 目标库位外部容器 --捡货模式没有
                whWorkLineCommand.setToOuterContainerId(null);
                // 目标库位内部容器 --捡货模式没有
                whWorkLineCommand.setToInsideContainerId(null);
                if (null != whOdoOutBoundBoxCommand.getWholeCase()) {
                    // 是否整托整箱
                    whWorkLineCommand.setIsWholeCase(true);
                } else {
                    // 是否整托整箱
                    whWorkLineCommand.setIsWholeCase(false);
                }
                // 出库单ID
                whWorkLineCommand.setOdoId(whOdoOutBoundBoxCommand.getOdoId());
                // 出库单明细ID
                whWorkLineCommand.setOdoLineId(whOdoOutBoundBoxCommand.getOdoLineId());
                // 补货单据号
                whWorkLineCommand.setReplenishmentCode(whSkuInventoryTobefilled.getReplenishmentCode());
                // 创建时间
                whWorkLineCommand.setCreateTime(new Date());
                // 最后操作时间
                whWorkLineCommand.setLastModifyTime(new Date());
                // 操作人ID
                whWorkLineCommand.setOperatorId(userId);

                WhWorkLine whWorkLine = new WhWorkLine();
                // 复制数据
                BeanUtils.copyProperties(whWorkLineCommand, whWorkLine);
                if (null != whWorkLineCommand.getId()) {
                    workLineDao.saveOrUpdateByVersion(whWorkLine);
                } else {
                    workLineDao.insert(whWorkLine);
                }
            }
        }
    }

    /**
     * 更新工作头信息
     * @param WhOdoOutBoundBox
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updatePickingWork(WhWave whWave, String workCode, WhOdoOutBoundBox odoOutBoundBox) {
        // 获取工作头信息
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(workCode, odoOutBoundBox.getOuId());
        // 获取工作明细信息列表
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), odoOutBoundBox.getOuId());
        // 查询波次主档信息
        WhWaveMaster whWaveMaster = waveMasterDao.findByIdExt(whWave.getWaveMasterId(), whWave.getOuId());
        // 获取工作类型
        WorkType workType = this.workTypeDao.findWorkTypeByworkCategory("PICKING", whWave.getOuId());
        // 判断是否有补货工作
        List<ReplenishmentTask> replenishmentTaskLst = replenishmentTaskManager.findTaskByWave(whWave.getId(), whWave.getOuId());
        String workArea = "";
        int count = 0;
        Boolean isFromLocationId = true;
        Boolean isFromOuterContainerId = true;
        Boolean isFromInsideContainerId = true;
        Boolean isOdoId = true;

        for (WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList) {
            if (count != 0) {
                // 获取上一次循环的实体类
                WhWorkLineCommand whWorkLineCommandBefor = whWorkLineCommandList.get(count - 1);

                if (null != whWorkLineCommandBefor.getFromLocationId() && null != whWorkLineCommand.getFromLocationId() && whWorkLineCommandBefor.getFromLocationId().equals(whWorkLineCommand.getFromLocationId())) {
                    isFromLocationId = false;
                }
                if (null != whWorkLineCommandBefor.getFromOuterContainerId() && null != whWorkLineCommand.getFromOuterContainerId() && whWorkLineCommandBefor.getFromOuterContainerId().equals(whWorkLineCommand.getFromOuterContainerId())) {
                    isFromOuterContainerId = false;
                }
                if (null != whWorkLineCommandBefor.getFromInsideContainerId() && null != whWorkLineCommand.getFromInsideContainerId() && whWorkLineCommandBefor.getFromInsideContainerId().equals(whWorkLineCommand.getFromInsideContainerId())) {
                    isFromInsideContainerId = false;
                }
                if (null != whWorkLineCommandBefor.getOdoId() && null != whWorkLineCommand.getOdoId() && whWorkLineCommandBefor.getOdoId().equals(whWorkLineCommand.getOdoId())) {
                    isOdoId = false;
                }
            }

            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommand.getFromLocationId(), whWorkLineCommand.getOuId());
            if (null != locationCommand) {
                Area area = areaDao.findByIdExt(locationCommand.getWorkAreaId(), locationCommand.getOuId());
                if (workArea == "") {
                    workArea = area.getAreaCode();
                } else {
                    workArea = workArea + "," + area.getAreaCode();
                }
            }
            // 索引自增
            count++;
        }
        // 配置模式
        if (null != whWorkLineCommandList && 0 < whWorkLineCommandList.size()) {
            WhOdo odo = odoDao.findByIdOuId(whWorkLineCommandList.get(0).getOdoId(), whWorkLineCommandList.get(0).getOuId());
            WhDistributionPatternRuleCommand whDistributionPatternRuleCommand = whDistributionPatternRuleDao.findRuleByCode(odo.getDistributeMode(), odo.getOuId());
            whWorkCommand.setDistributionMode(odo.getDistributeMode());
            if (null == whDistributionPatternRuleCommand) {
                log.error("whDistributionPatternRuleCommand = null", whDistributionPatternRuleCommand);
                throw new BusinessException("配货模式不存在");
            }
            whWorkCommand.setPickingMode(whDistributionPatternRuleCommand.getPickingMode().toString());
            whWorkCommand.setCheckingMode(whDistributionPatternRuleCommand.getCheckingMode());
        }
        // 判断工作明细是否只有唯一库位
        if (isFromLocationId == true) {
            // 获取库位表数据
            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommandList.get(0).getFromLocationId(), whWorkLineCommandList.get(0).getOuId());
            if (null != locationCommand) {
                // 设置库位
                whWorkCommand.setLocationCode(locationCommand.getCode());
            }
        }
        // 判断工作明细是否只有唯一外部容器
        if (isFromOuterContainerId == true) {
            // 根据容器ID获取容器CODE
            Container containerOut = new Container();
            containerOut = containerDao.findByIdExt(whWorkLineCommandList.get(0).getFromOuterContainerId(), odoOutBoundBox.getOuId());
            if (null != containerOut) {
                // 设置外部容器
                whWorkCommand.setOuterContainerCode(containerOut.getCode());
            }
        }
        // 判断据工作明细是否只有唯一内部容器
        if (isFromInsideContainerId == true) {
            // 根据容器ID获取容器CODE
            Container containerIn = new Container();
            containerIn = containerDao.findByIdExt(whWorkLineCommandList.get(0).getFromInsideContainerId(), odoOutBoundBox.getOuId());
            if (null != containerIn) {
                // 设置内部容器
                whWorkCommand.setContainerCode(containerIn.getCode());
            }
        }
        // 判断据工作明细是否只有唯一出库单
        if (isOdoId == true) {
            WhOdo whOdo = this.odoDao.findByIdOuId(whWorkLineCommandList.get(0).getOdoId(), odoOutBoundBox.getOuId());
            if (null != whOdo) {
                // 设置订单号
                whWorkCommand.setOrderCode(whOdo.getEcOrderCode());
            }
        }

        // 当前工作明细设计到的所有库区编码信息列表
        whWorkCommand.setWorkArea(workArea);
        // 是否锁定 默认值：1
        whWorkCommand.setIsLocked(whWaveMaster.getIsAutoReleaseWork());
        // 工作优先级
        if (null != replenishmentTaskLst && 0 < replenishmentTaskLst.size()) {
            whWorkCommand.setWorkPriority(null != whWaveMaster.getPickingExtPriority() ? whWaveMaster.getPickingExtPriority() : workType.getPriority());
        } else {
            whWorkCommand.setWorkPriority(null != whWaveMaster.getPickingWorkPriority() ? whWaveMaster.getPickingWorkPriority() : workType.getPriority());
        }
        WhWork whWork = new WhWork();
        // 复制数据
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if (null != whWorkCommand.getId()) {
            workDao.saveOrUpdateByVersion(whWork);
        } else {
            workDao.insert(whWork);
        }
    }

    /**
     * 创建作业头
     * @param workCode
     * @param WhOdoOutBoundBox
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String savePickingOperation(String workCode, WhOdoOutBoundBox whOdoOutBoundBox) {
        // 获取工作头信息
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(workCode, whOdoOutBoundBox.getOuId());
        // 调编码生成器工作明细实体标识
        String operationCode = codeManager.generateCode(Constants.WMS, Constants.WHOPERATION_MODEL_URL, "", "OPERATION", null);

        WhOperationCommand WhOperationCommand = new WhOperationCommand();
        // 作业号
        WhOperationCommand.setCode(operationCode);
        // 状态
        WhOperationCommand.setStatus(OperationStatus.NEW);
        // 工作ID
        WhOperationCommand.setWorkId(whWorkCommand.getId());
        // 仓库组织ID
        WhOperationCommand.setOuId(whWorkCommand.getOuId());
        // 工作类型编码
        WhOperationCommand.setWorkType(whWorkCommand.getWorkType());
        // 工作类别编码
        WhOperationCommand.setWorkCategory(whWorkCommand.getWorkCategory());
        // 优先级
        WhOperationCommand.setWorkPriority(whWorkCommand.getWorkPriority());
        // 小批次
        WhOperationCommand.setBatch(whWorkCommand.getBatch());
        // 操作开始时间
        WhOperationCommand.setStartTime(whWorkCommand.getStartTime());
        // 操作结束时间
        WhOperationCommand.setFinishTime(whWorkCommand.getFinishTime());
        // 波次ID
        WhOperationCommand.setWaveId(whWorkCommand.getWaveId());
        // 波次号
        WhOperationCommand.setWaveCode(whWorkCommand.getWaveCode());
        // 订单号
        WhOperationCommand.setOrderCode(whWorkCommand.getOrderCode());
        // 库位
        WhOperationCommand.setLocationCode(whWorkCommand.getLocationCode());
        // 托盘
        WhOperationCommand.setOuterContainerCode(whWorkCommand.getOuterContainerCode());
        // 容器
        WhOperationCommand.setContainerCode(whWorkCommand.getContainerCode());
        // 是否整托整箱
        if (null != whOdoOutBoundBox.getWholeCase()) {
            // 是否整托整箱
            WhOperationCommand.setIsWholeCase(true);
        } else {
            // 是否整托整箱
            WhOperationCommand.setIsWholeCase(false);
        }
        // 是否短拣
        WhOperationCommand.setIsShortPicking(whWorkCommand.getIsShortPicking());
        // 是否拣货完成
        WhOperationCommand.setIsPickingFinish(false);
        // 是否波次内补货
        WhOperationCommand.setIsWaveReplenish(whWorkCommand.getIsWaveReplenish());
        // 是否拣货库存待移入
        WhOperationCommand.setIsPickingTobefilled(whWorkCommand.getIsPickingTobefilled());
        // 创建时间
        WhOperationCommand.setCreateTime(whWorkCommand.getCreateTime());
        // 最后操作时间
        WhOperationCommand.setLastModifyTime(whWorkCommand.getLastModifyTime());
        // 创建人ID
        WhOperationCommand.setCreatedId(whWorkCommand.getCreatedId());
        // 修改人ID
        WhOperationCommand.setModifiedId(whWorkCommand.getModifiedId());
        // 操作人ID
        WhOperationCommand.setOperatorId(whWorkCommand.getOperatorId());
        // 是否启用 1:启用 0:停用
        WhOperationCommand.setLifecycle(whWorkCommand.getLifecycle());

        WhOperation whOperation = new WhOperation();
        // 复制数据
        BeanUtils.copyProperties(WhOperationCommand, whOperation);
        if (null != WhOperationCommand.getId()) {
            operationDao.saveOrUpdateByVersion(whOperation);
        } else {
            operationDao.insert(whOperation);
        }

        return operationCode;
    }

    /**
     * 创建作业明细
     * @param List<WhOdoOutBoundBox>
     * @return
     */

    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void savePickingOperationLine(String workCode, String operationCode, Long ouId) {
        // 获取工作头信息
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(workCode, ouId);
        // 获取工作明细信息列表
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), ouId);
        // 获取作业头信息
        WhOperationCommand WhOperationCommand = this.operationDao.findOperationByCode(operationCode, ouId);

        for (WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList) {
            WhOperationLineCommand WhOperationLineCommand = new WhOperationLineCommand();
            // 作业ID
            WhOperationLineCommand.setOperationId(WhOperationCommand.getId());
            // 工作明细ID
            WhOperationLineCommand.setWorkLineId(whWorkLineCommand.getId());
            // 仓库组织ID
            WhOperationLineCommand.setOuId(whWorkLineCommand.getOuId());
            // 操作开始时间
            WhOperationLineCommand.setStartTime(whWorkLineCommand.getStartTime());
            // 操作结束时间
            WhOperationLineCommand.setFinishTime(whWorkLineCommand.getFinishTime());
            // 商品ID
            WhOperationLineCommand.setSkuId(whWorkLineCommand.getSkuId());
            // 计划量
            WhOperationLineCommand.setQty(whWorkLineCommand.getQty());
            // 库存状态
            WhOperationLineCommand.setInvStatus(whWorkLineCommand.getInvStatus());
            // 库存类型
            WhOperationLineCommand.setInvType(whWorkLineCommand.getInvType());
            // 批次号
            WhOperationLineCommand.setBatchNumber(whWorkLineCommand.getBatchNumber());
            // 生产日期
            WhOperationLineCommand.setMfgDate(whWorkLineCommand.getMfgDate());
            // 失效日期
            WhOperationLineCommand.setExpDate(whWorkLineCommand.getExpDate());
            // 最小失效日期
            WhOperationLineCommand.setMinExpDate(whWorkLineCommand.getMinExpDate());
            // 最大失效日期
            WhOperationLineCommand.setMaxExpDate(whWorkLineCommand.getMaxExpDate());
            // 原产地
            WhOperationLineCommand.setCountryOfOrigin(whWorkLineCommand.getCountryOfOrigin());
            // 库存属性1
            WhOperationLineCommand.setInvAttr1(whWorkLineCommand.getInvAttr1());
            // 库存属性2
            WhOperationLineCommand.setInvAttr2(whWorkLineCommand.getInvAttr2());
            // 库存属性3
            WhOperationLineCommand.setInvAttr3(whWorkLineCommand.getInvAttr3());
            // 库存属性4
            WhOperationLineCommand.setInvAttr4(whWorkLineCommand.getInvAttr4());
            // 库存属性5
            WhOperationLineCommand.setInvAttr5(whWorkLineCommand.getInvAttr5());
            // 内部对接码
            WhOperationLineCommand.setUuid(whWorkLineCommand.getUuid());
            // 原始库位
            WhOperationLineCommand.setFromLocationId(whWorkLineCommand.getFromLocationId());
            // 原始库位外部容器
            WhOperationLineCommand.setFromOuterContainerId(whWorkLineCommand.getFromOuterContainerId());
            // 原始库位内部容器
            WhOperationLineCommand.setFromInsideContainerId(whWorkLineCommand.getFromInsideContainerId());
            // 使用出库箱，耗材ID
            WhOperationLineCommand.setUseOutboundboxId(whWorkLineCommand.getUseOutboundboxId());
            // 使用出库箱编码
            WhOperationLineCommand.setUseOutboundboxCode(whWorkLineCommand.getUseOutboundboxCode());
            // 使用容器
            WhOperationLineCommand.setUseContainerId(whWorkLineCommand.getUseContainerId());
            // 使用外部容器，小车
            WhOperationLineCommand.setUseOuterContainerId(whWorkLineCommand.getUseOuterContainerId());
            // 使用货格编码数
            WhOperationLineCommand.setUseContainerLatticeNo(whWorkLineCommand.getUseContainerLatticeNo());
            // 目标库位
            WhOperationLineCommand.setToLocationId(whWorkLineCommand.getToLocationId());
            // 目标库位外部容器
            WhOperationLineCommand.setToOuterContainerId(whWorkLineCommand.getToOuterContainerId());
            // 目标库位内部容器
            WhOperationLineCommand.setToInsideContainerId(whWorkLineCommand.getToInsideContainerId());
            // 出库单ID
            WhOperationLineCommand.setOdoId(whWorkLineCommand.getOdoId());
            // 出库单明细ID
            WhOperationLineCommand.setOdoLineId(whWorkLineCommand.getOdoLineId());
            // 创建时间
            WhOperationLineCommand.setCreateTime(new Date());
            // 最后操作时间
            WhOperationLineCommand.setLastModifyTime(whWorkLineCommand.getLastModifyTime());
            // 操作人ID
            WhOperationLineCommand.setOperatorId(whWorkLineCommand.getOperatorId());

            WhOperationLine whOperationLine = new WhOperationLine();
            // 复制数据
            BeanUtils.copyProperties(WhOperationLineCommand, whOperationLine);
            if (null != WhOperationLineCommand.getId()) {
                operationLineDao.saveOrUpdateByVersion(whOperationLine);
            } else {
                operationLineDao.insert(whOperationLine);
            }
        }
    }

    /**
     *  创建补货工作头信息
     * 
     * @param whOdoOutBoundBox
     * @param userId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String saveOutReplenishmentWork(WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand, Long userId) {
        // 获取工作类型
        WorkType workType = this.workTypeDao.findWorkTypeByworkCategory("REPLENISHMENT", skuInventoryAllocatedCommand.getOuId());
        // 根据容器ID获取容器CODE
        Container outerContainer = new Container();
        Container insideContainer = new Container();
        if (skuInventoryAllocatedCommand.getOuterContainerId() != null) {
            outerContainer = containerDao.findByIdExt(skuInventoryAllocatedCommand.getOuterContainerId(), skuInventoryAllocatedCommand.getOuId());
        }
        if (skuInventoryAllocatedCommand.getInsideContainerId() != null) {
            insideContainer = containerDao.findByIdExt(skuInventoryAllocatedCommand.getInsideContainerId(), skuInventoryAllocatedCommand.getOuId());
        }
        // 调编码生成器工作头实体标识
        String workCode = codeManager.generateCode(Constants.WMS, Constants.WHWORK_MODEL_URL, "", "WORK", null);
        // 所有值为null的字段，将会在更新工作头信息时获取，如果到时候还没有值，那就是被骗了
        WhWorkCommand whWorkCommand = new WhWorkCommand();
        // 工作号
        whWorkCommand.setCode(workCode);
        // 工作状态，系统常量
        whWorkCommand.setStatus(WorkStatus.NEW);
        // 仓库组织ID
        whWorkCommand.setOuId(skuInventoryAllocatedCommand.getOuId());
        // 工作类型编码
        whWorkCommand.setWorkType(workType.getCode());
        // 工作类别编码
        whWorkCommand.setWorkCategory("REPLENISHMENT");
        // 是否锁定 默认值：1
        whWorkCommand.setIsLocked(true);
        // 是否已迁出
        whWorkCommand.setIsAssignOut(false);
        // 是否短拣--执行时判断
        whWorkCommand.setIsShortPicking(null);
        // 是否波次内补货
        whWorkCommand.setIsWaveReplenish(false);
        // 是否拣货库存待移入
        whWorkCommand.setIsPickingTobefilled(null);
        // 是否多次作业--需计算后再更新
        whWorkCommand.setIsMultiOperation(null);
        // 当前工作明细设计到的所有库区编码信息列表--更新时获取数据
        whWorkCommand.setWorkArea(null);
        // 工作优先级
        whWorkCommand.setWorkPriority(workType.getPriority());
        // 小批次
        whWorkCommand.setBatch(null);
        // 签出批次
        whWorkCommand.setAssignOutBatch(null);
        // 操作开始时间
        whWorkCommand.setStartTime(new Date());
        // 操作结束时间
        whWorkCommand.setFinishTime(new Date());
        // 波次ID
        whWorkCommand.setWaveId(null);
        // 波次号
        whWorkCommand.setWaveCode(null);
        // 订单号--更新时获取数据
        whWorkCommand.setOrderCode(null);
        // 库位--更新时获取数据
        whWorkCommand.setLocationCode(null);
        // 托盘--更新时获取数据
        whWorkCommand.setOuterContainerCode(null == outerContainer ? null : outerContainer.getCode());
        // 容器--更新时获取数据
        whWorkCommand.setContainerCode(null == insideContainer ? null : insideContainer.getCode());
        // 创建时间
        whWorkCommand.setCreateTime(new Date());
        // 最后操作时间
        whWorkCommand.setLastModifyTime(new Date());
        // 创建人ID
        whWorkCommand.setCreatedId(userId);
        // 修改人ID
        whWorkCommand.setModifiedId(userId);
        // 操作人ID
        whWorkCommand.setOperatorId(userId);
        // 是否启用 1:启用 0:停用
        whWorkCommand.setLifecycle(0);
        WhWork whWork = new WhWork();
        // 复制数据
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if (null != whWorkCommand.getId()) {
            workDao.saveOrUpdateByVersion(whWork);
        } else {
            workDao.insert(whWork);
        }
        return workCode;
    }

    /**
     *  更新补货工作头信息
     * 
     * @param waveId
     * @param replenishmentWorkCode
     * @param skuInventoryAllocatedCommand
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateOutReplenishmentWork(String replenishmentWorkCode, WhSkuInventoryAllocatedCommand skuInventoryAllocatedCommand) {
        // 获取工作头信息
        WhWorkCommand whWorkCommand = this.workDao.findWorkByWorkCode(replenishmentWorkCode, skuInventoryAllocatedCommand.getOuId());
        // 获取工作明细信息列表
        List<WhWorkLineCommand> whWorkLineCommandList = this.workLineDao.findWorkLineByWorkId(whWorkCommand.getId(), skuInventoryAllocatedCommand.getOuId());
        String workArea = "";
        int count = 0;
        Boolean isFromLocationId = true;
        Boolean isFromOuterContainerId = true;
        Boolean isFromInsideContainerId = true;
        Boolean isOdoId = true;

        for (WhWorkLineCommand whWorkLineCommand : whWorkLineCommandList) {
            if (count != 0) {
                // 获取上一次循环的实体类
                WhWorkLineCommand whWorkLineCommandBefor = whWorkLineCommandList.get(count - 1);

                if (null != whWorkLineCommandBefor.getFromLocationId() && null != whWorkLineCommand.getFromLocationId() && whWorkLineCommandBefor.getFromLocationId().equals(whWorkLineCommand.getFromLocationId())) {
                    isFromLocationId = false;
                }
                if (null != whWorkLineCommandBefor.getFromOuterContainerId() && null != whWorkLineCommand.getFromOuterContainerId() && whWorkLineCommandBefor.getFromOuterContainerId().equals(whWorkLineCommand.getFromOuterContainerId())) {
                    isFromOuterContainerId = false;
                }
                if (null != whWorkLineCommandBefor.getFromInsideContainerId() && null != whWorkLineCommand.getFromInsideContainerId() && whWorkLineCommandBefor.getFromInsideContainerId().equals(whWorkLineCommand.getFromInsideContainerId())) {
                    isFromInsideContainerId = false;
                }
                if (null != whWorkLineCommandBefor.getOdoId() && null != whWorkLineCommand.getOdoId() && whWorkLineCommandBefor.getOdoId().equals(whWorkLineCommand.getOdoId())) {
                    isOdoId = false;
                }
            }

            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommand.getFromLocationId(), whWorkLineCommand.getOuId());
            if (null != locationCommand) {
                Area area = areaDao.findByIdExt(locationCommand.getWorkAreaId(), locationCommand.getOuId());
                if (workArea == "") {
                    workArea = area.getAreaCode();
                } else {
                    workArea = workArea + "," + area.getAreaCode();
                }
            }
            // 索引自增
            count++;
        }
        // 判断工作明细是否只有唯一库位
        if (isFromLocationId == true) {
            // 获取库位表数据
            LocationCommand locationCommand = locationDao.findLocationCommandByParam(whWorkLineCommandList.get(0).getFromLocationId(), whWorkLineCommandList.get(0).getOuId());
            if (null != locationCommand) {
                // 设置库位
                whWorkCommand.setLocationCode(locationCommand.getCode());
            }
        }
        // 判断工作明细是否只有唯一外部容器
        if (isFromOuterContainerId == true) {
            // 根据容器ID获取容器CODE
            Container containerOut = new Container();
            containerOut = containerDao.findByIdExt(whWorkLineCommandList.get(0).getFromOuterContainerId(), skuInventoryAllocatedCommand.getOuId());
            if (null != containerOut) {
                // 设置外部容器
                whWorkCommand.setOuterContainerCode(containerOut.getCode());
            }
        }
        // 判断据工作明细是否只有唯一内部容器
        if (isFromInsideContainerId == true) {
            // 根据容器ID获取容器CODE
            Container containerIn = new Container();
            containerIn = containerDao.findByIdExt(whWorkLineCommandList.get(0).getFromInsideContainerId(), skuInventoryAllocatedCommand.getOuId());
            if (null != containerIn) {
                // 设置内部容器
                whWorkCommand.setContainerCode(containerIn.getCode());
            }
        }
        // 判断据工作明细是否只有唯一出库单
        if (isOdoId == true) {
            WhOdo whOdo = this.odoDao.findByIdOuId(whWorkLineCommandList.get(0).getOdoId(), skuInventoryAllocatedCommand.getOuId());
            if (null != whOdo) {
                // 设置订单号
                whWorkCommand.setOrderCode(whOdo.getEcOrderCode());
            }
        }

        // 当前工作明细设计到的所有库区编码信息列表
        whWorkCommand.setWorkArea(workArea);
        WhWork whWork = new WhWork();
        // 复制数据
        BeanUtils.copyProperties(whWorkCommand, whWork);
        if (null != whWorkCommand.getId()) {
            workDao.saveOrUpdateByVersion(whWork);
        } else {
            workDao.insert(whWork);
        }
    }

    /********************************************************************其他方法--开始******************************************************************/

    /**
     * 根据补货工作拆分条件统计分析补货数据
     * @param ReplenishmentRuleCommand
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Map<String, List<WhSkuInventoryAllocatedCommand>> getSkuInventoryAllocatedCommandForGroup(ReplenishmentRuleCommand replenishmentRuleCommand) {
        // 初始化分组MAP
        Map<String, List<WhSkuInventoryAllocatedCommand>> rMap = new HashMap<String, List<WhSkuInventoryAllocatedCommand>>();
        // 根据补货工作释放及拆分条件获取所有补货数据
        List<WhSkuInventoryAllocatedCommand> skuInventoryAllocatedCommandLst = new ArrayList<WhSkuInventoryAllocatedCommand>();
        if (null != replenishmentRuleCommand.getWaveId()) {
            skuInventoryAllocatedCommandLst = getInAllReplenishmentLst(replenishmentRuleCommand);
        } else {
            skuInventoryAllocatedCommandLst = getOutAllReplenishmentLst(replenishmentRuleCommand);
        }
        // 根据补货工作拆分条件统计分析补货数据
        for (WhSkuInventoryAllocatedCommand whSkuInventoryAllocatedCommand : skuInventoryAllocatedCommandLst) {
            // 判断是否整托整箱
            Boolean inWholeCase = false;
            Boolean outWholeCase = false;

            if (null != whSkuInventoryAllocatedCommand.getInsideContainerId()) {
                WhSkuInventory skuInventory = new WhSkuInventory();
                WhSkuInventoryAllocatedCommand allocatedCommand = new WhSkuInventoryAllocatedCommand();
                WhSkuInventoryAllocatedCommand totalCommand = new WhSkuInventoryAllocatedCommand();
                WhSkuInventoryTobefilled skuInventoryTobefilled = new WhSkuInventoryTobefilled();
                Double onHandQty = 0.00;
                Double frozenQty = 0.00;
                skuInventory.setInsideContainerId(whSkuInventoryAllocatedCommand.getInsideContainerId());
                allocatedCommand.setInsideContainerId(whSkuInventoryAllocatedCommand.getInsideContainerId());
                totalCommand.setInsideContainerId(whSkuInventoryAllocatedCommand.getInsideContainerId());
                skuInventoryTobefilled.setInsideContainerId(whSkuInventoryAllocatedCommand.getInsideContainerId());
                skuInventoryTobefilled.setLocationId(whSkuInventoryAllocatedCommand.getLocationId());
                allocatedCommand.setReplenishmentCode(whSkuInventoryAllocatedCommand.getReplenishmentCode());
                allocatedCommand.setReplenishmentRuleId(whSkuInventoryAllocatedCommand.getReplenishmentRuleId());
                Double allocatedQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(allocatedCommand);
                Double totalQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(totalCommand);
                Double toBeFilledQty = skuInventoryTobefilledDao.skuInventoryTobefilledQty(skuInventoryTobefilled);
                List<WhSkuInventory> skuInventoryList = skuInventoryDao.getSkuInvListGroupUuid(skuInventory);
                if (null != skuInventoryList && 0 != skuInventoryList.size()) {
                    for (WhSkuInventory whSkuInventory : skuInventoryList) {
                        onHandQty = onHandQty + whSkuInventory.getOnHandQty();
                        frozenQty = frozenQty + whSkuInventory.getFrozenQty();
                    }
                }
                double zero = 0.0;
                int resultFrozen = frozenQty.compareTo(zero);
                int resultTo = 0;
                if (null != toBeFilledQty) {
                    resultTo = toBeFilledQty.compareTo(zero);
                }

                if (totalQty.equals(allocatedQty) && onHandQty.equals(allocatedQty) && 0 == resultFrozen && 0 == resultTo) {
                    inWholeCase = true;
                }
            }

            if (null != whSkuInventoryAllocatedCommand.getOuterContainerId()) {
                WhSkuInventory skuInventory = new WhSkuInventory();
                WhSkuInventoryAllocatedCommand allocatedCommand = new WhSkuInventoryAllocatedCommand();
                WhSkuInventoryAllocatedCommand totalCommand = new WhSkuInventoryAllocatedCommand();
                WhSkuInventoryTobefilled skuInventoryTobefilled = new WhSkuInventoryTobefilled();
                Double onHandQty = 0.00;
                Double frozenQty = 0.00;
                skuInventory.setOuterContainerId(whSkuInventoryAllocatedCommand.getOuterContainerId());
                allocatedCommand.setOuterContainerId(whSkuInventoryAllocatedCommand.getOuterContainerId());
                totalCommand.setOuterContainerId(whSkuInventoryAllocatedCommand.getOuterContainerId());
                skuInventoryTobefilled.setOuterContainerId(whSkuInventoryAllocatedCommand.getOuterContainerId());
                skuInventoryTobefilled.setLocationId(whSkuInventoryAllocatedCommand.getLocationId());
                allocatedCommand.setReplenishmentCode(whSkuInventoryAllocatedCommand.getReplenishmentCode());
                allocatedCommand.setReplenishmentRuleId(whSkuInventoryAllocatedCommand.getReplenishmentRuleId());
                Double allocatedQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(allocatedCommand);
                Double totalQty = skuInventoryAllocatedDao.skuInventoryAllocatedQty(totalCommand);
                Double toBeFilledQty = skuInventoryTobefilledDao.skuInventoryTobefilledQty(skuInventoryTobefilled);
                List<WhSkuInventory> skuInventoryList = skuInventoryDao.getSkuInvListGroupUuid(skuInventory);

                if (null != skuInventoryList && 0 != skuInventoryList.size()) {
                    for (WhSkuInventory whSkuInventory : skuInventoryList) {
                        onHandQty = onHandQty + whSkuInventory.getOnHandQty();
                        frozenQty = frozenQty + whSkuInventory.getFrozenQty();
                    }
                }
                double zero = 0.0;
                int resultFrozen = frozenQty.compareTo(zero);
                int resultTo = 0;
                if (null != toBeFilledQty) {
                    resultTo = toBeFilledQty.compareTo(zero);
                }
                if (totalQty.equals(allocatedQty) && onHandQty.equals(allocatedQty) && 0 == resultFrozen && 0 == resultTo) {
                    outWholeCase = true;
                }
            }

            // 初始化WhSkuInventoryAllocatedCommand列表
            List<WhSkuInventoryAllocatedCommand> rList = new ArrayList<WhSkuInventoryAllocatedCommand>();
            if (false != replenishmentRuleCommand.getIsFromInsideContainerSplitWork() && false != replenishmentRuleCommand.getIsToLocationSplitWork()) {
                // 分组标示 -- 配置为原始库位货箱与目标库位
                if (true == inWholeCase) {
                    String str = "fromInsideContainerToLocation" + "-" + whSkuInventoryAllocatedCommand.getInsideContainerId() + "-" + whSkuInventoryAllocatedCommand.getToLocationId();
                    if (null != rMap && null != rMap.get(str)) {
                        rList = rMap.get(str);
                    }
                    rList.add(whSkuInventoryAllocatedCommand);
                    rMap.put(str, rList);
                } else {
                    String str = "fromLocationToLocation" + "-" + whSkuInventoryAllocatedCommand.getLocationId() + "-" + whSkuInventoryAllocatedCommand.getToLocationId();
                    if (null != rMap && null != rMap.get(str)) {
                        rList = rMap.get(str);
                    }
                    rList.add(whSkuInventoryAllocatedCommand);
                    rMap.put(str, rList);
                }
            } else if (false != replenishmentRuleCommand.getIsFromInsideContainerSplitWork() && false == replenishmentRuleCommand.getIsToLocationSplitWork()) {
                // 分组标示 -- 配置为原始库位货箱
                if (true == inWholeCase) {
                    String str = "fromInsideContainer" + "-" + whSkuInventoryAllocatedCommand.getInsideContainerId();
                    if (null != rMap && null != rMap.get(str)) {
                        rList = rMap.get(str);
                    }
                    rList.add(whSkuInventoryAllocatedCommand);
                    rMap.put(str, rList);
                } else {
                    String str = "fromLocation" + "-" + whSkuInventoryAllocatedCommand.getLocationId();
                    if (null != rMap && null != rMap.get(str)) {
                        rList = rMap.get(str);
                    }
                    rList.add(whSkuInventoryAllocatedCommand);
                    rMap.put(str, rList);
                }
            } else if (false != replenishmentRuleCommand.getIsFromOuterContainerSplitWork() && false != replenishmentRuleCommand.getIsToLocationSplitWork()) {
                // 分组标示 -- 配置为原始库位托盘与目标库位
                if (true == outWholeCase) {
                    String str = "fromOuterContainerToLocation" + "-" + whSkuInventoryAllocatedCommand.getOuterContainerId() + "-" + whSkuInventoryAllocatedCommand.getToLocationId();
                    if (null != rMap && null != rMap.get(str)) {
                        rList = rMap.get(str);
                    }
                    rList.add(whSkuInventoryAllocatedCommand);
                    rMap.put(str, rList);
                } else {
                    String str = "fromLocationToLocation" + "-" + whSkuInventoryAllocatedCommand.getLocationId() + "-" + whSkuInventoryAllocatedCommand.getToLocationId();
                    if (null != rMap && null != rMap.get(str)) {
                        rList = rMap.get(str);
                    }
                    rList.add(whSkuInventoryAllocatedCommand);
                    rMap.put(str, rList);
                }
            } else if (false != replenishmentRuleCommand.getIsFromOuterContainerSplitWork() && false == replenishmentRuleCommand.getIsToLocationSplitWork()) {
                // 分组标示 -- 配置为原始库位托盘
                if (true == outWholeCase) {
                    String str = "fromOuterContainer" + "-" + whSkuInventoryAllocatedCommand.getOuterContainerId();
                    if (null != rMap && null != rMap.get(str)) {
                        rList = rMap.get(str);
                    }
                    rList.add(whSkuInventoryAllocatedCommand);
                    rMap.put(str, rList);
                } else {
                    String str = "fromLocation" + "-" + whSkuInventoryAllocatedCommand.getLocationId();
                    if (null != rMap && null != rMap.get(str)) {
                        rList = rMap.get(str);
                    }
                    rList.add(whSkuInventoryAllocatedCommand);
                    rMap.put(str, rList);
                }
            } else if (false != replenishmentRuleCommand.getIsFromLocationSplitWork() && false != replenishmentRuleCommand.getIsToLocationSplitWork()) {
                // 分组标示 -- 配置为原始库位与目标库位
                String str = "fromLocationToLocation" + "-" + whSkuInventoryAllocatedCommand.getLocationId() + "-" + whSkuInventoryAllocatedCommand.getToLocationId();
                if (null != rMap && null != rMap.get(str)) {
                    rList = rMap.get(str);
                }
                rList.add(whSkuInventoryAllocatedCommand);
                rMap.put(str, rList);
            } else {
                // 分组标示 -- 配置为原始库位
                String str = "fromLocation" + "-" + whSkuInventoryAllocatedCommand.getLocationId();
                if (null != rMap && null != rMap.get(str)) {
                    rList = rMap.get(str);
                }
                rList.add(whSkuInventoryAllocatedCommand);
                rMap.put(str, rList);
            }
        }
        return rMap;
    }

    /**
     * 根据补货工作释放及拆分条件获取所有补货数据
     * 
     * @param WhSkuInventoryAllocatedCommand
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventoryAllocatedCommand> getInAllReplenishmentLst(ReplenishmentRuleCommand replenishmentRuleCommand) {
        // 根据补货工作释放及拆分条件获取所有补货数据
        List<WhSkuInventoryAllocatedCommand> skuInventoryAllocatedCommandLst = this.skuInventoryAllocatedDao.getInAllReplenishmentLst(replenishmentRuleCommand);
        return skuInventoryAllocatedCommandLst;
    }

    /**
     * 根据补货工作释放及拆分条件获取所有补货数据
     * 
     * @param WhSkuInventoryAllocatedCommand
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventoryAllocatedCommand> getOutAllReplenishmentLst(ReplenishmentRuleCommand replenishmentRuleCommand) {
        // 根据补货工作释放及拆分条件获取所有补货数据
        List<WhSkuInventoryAllocatedCommand> skuInventoryAllocatedCommandLst = this.skuInventoryAllocatedDao.getOutAllReplenishmentLst(replenishmentRuleCommand);
        return skuInventoryAllocatedCommandLst;
    }

    /**
     * 根据小批次分组查询出所有出库箱/容器信息
     * 
     * @param whOdoOutBoundBox
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoOutBoundBoxCommand> getOdoOutBoundBoxListByGroup(WhOdoOutBoundBox whOdoOutBoundBox) {
        List<WhOdoOutBoundBoxCommand> whOdoOutBoundBoxCommandList = this.odoOutBoundBoxDao.getOdoOutBoundBoxListByGroup(whOdoOutBoundBox);
        return whOdoOutBoundBoxCommandList;
    }

    /**
     * 查询库存信息
     * 
     * @param whOdoOutBoundBoxCommand
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventory> getSkuInventory(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand) {
        WhOdo whOdo = this.odoDao.findByIdOuId(whOdoOutBoundBoxCommand.getOdoId(), whOdoOutBoundBoxCommand.getOuId());
        WhSkuInventory whSkuInventory = new WhSkuInventory();
        whSkuInventory.setOccupationCode(whOdo.getOdoCode());
        whSkuInventory.setOccupationLineId(whOdoOutBoundBoxCommand.getOdoLineId());
        whSkuInventory.setIsLocked(null);
        List<WhSkuInventory> SkuInventoryList = this.skuInventoryDao.getSkuInvListGroupUuid(whSkuInventory);
        return SkuInventoryList;
    }

    /**
     * 查询待移入库存信息
     * 
     * @param whOdoOutBoundBoxCommand
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventoryTobefilled> getSkuInventoryTobefilled(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand) {
        WhOdo whOdo = this.odoDao.findByIdOuId(whOdoOutBoundBoxCommand.getOdoId(), whOdoOutBoundBoxCommand.getOuId());
        WhSkuInventoryTobefilled whSkuInventoryTobefilled = new WhSkuInventoryTobefilled();
        whSkuInventoryTobefilled.setOccupationCode(whOdo.getOdoCode());
        whSkuInventoryTobefilled.setOccupationLineId(whOdoOutBoundBoxCommand.getOdoLineId());
        List<WhSkuInventoryTobefilled> SkuInventoryTobefilledList = this.skuInventoryTobefilledDao.getSkuItedListGroupUuid(whSkuInventoryTobefilled);
        return SkuInventoryTobefilledList;
    }

    /**
     * 设置出库箱行标识
     * @param whOdoOutBoundBoxCommand
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateWhOdoOutBoundBoxCommand(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand) {
        WhOdoOutBoundBoxCommand odoOutBoundBoxCommand = this.odoOutBoundBoxDao.findWhOdoOutBoundBoxCommandById(whOdoOutBoundBoxCommand.getId(), whOdoOutBoundBoxCommand.getOuId());
        odoOutBoundBoxCommand.setIsCreateWork(true);
        WhOdoOutBoundBox whOdoOutBoundBox = new WhOdoOutBoundBox();
        // 复制数据
        BeanUtils.copyProperties(odoOutBoundBoxCommand, whOdoOutBoundBox);
        if (null != odoOutBoundBoxCommand.getId()) {
            odoOutBoundBoxDao.saveOrUpdateByVersion(whOdoOutBoundBox);
        } else {
            odoOutBoundBoxDao.insert(whOdoOutBoundBox);
        }
    }

    /**
     *  计算库位容量
     * 
     * @param replenishmentWorkCode
     * @param ouId
     * @return
     */
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventoryAllocatedCommand> locationReplenishmentCalculation(List<WhSkuInventoryAllocatedCommand> whSkuInventoryAllocatedCommandLst, Long ouId) {
        String logId = "";
        Long locationId = whSkuInventoryAllocatedCommandLst.get(0).getToLocationId();
        LocationCommand location = locationDao.findLocationCommandByParam(locationId, ouId);
        Long maxVolume = Constants.DEFAULT_LONG;
        Long minVolume = Constants.DEFAULT_LONG;
        // 上限
        Integer upBound = location.getUpBound();
        Integer downBound = location.getDownBound();
        if (upBound == null || downBound == null) {
            return null;
        }
        // 所有托盘
        Set<Long> pallets = new HashSet<Long>();
        // 所有货箱
        Set<Long> containers = new HashSet<Long>();
        // 库位上所有sku（sku不在任何容器内）
        Map<Long, Double> skuIds = new HashMap<Long, Double>();
        // 计算库位可用体积
        List<WhSkuInventoryCommand> skuInvList = this.whSkuInventoryDao.findWhSkuInvCmdByLocation(ouId, locationId);
        for (WhSkuInventoryCommand whSkuInventoryCommand : skuInvList) {
            if (null != skuIds.get(whSkuInventoryCommand.getSkuId())) {
                Double onHandQty = skuIds.get(whSkuInventoryCommand.getSkuId());
                BigDecimal oldQty = new BigDecimal(onHandQty.toString());
                BigDecimal newQty = new BigDecimal(whSkuInventoryCommand.getQty().toString());
                Double newOnHandQty = oldQty.add(newQty).doubleValue();
                skuIds.put(whSkuInventoryCommand.getSkuId(), newOnHandQty);
            } else {
                skuIds.put(whSkuInventoryCommand.getSkuId(), whSkuInventoryCommand.getQty());
            }
        }
        List<WhSkuInventoryTobefilled> toBeFilledList = this.whSkuInventoryTobefilledDao.findLocWhSkuInventoryTobefilled(locationId, ouId);
        for (WhSkuInventoryTobefilled whSkuInventoryTobefilled : toBeFilledList) {
            if (null != skuIds.get(whSkuInventoryTobefilled.getSkuId())) {
                Double onHandQty = skuIds.get(whSkuInventoryTobefilled.getSkuId());
                BigDecimal oldQty = new BigDecimal(onHandQty.toString());
                BigDecimal newQty = new BigDecimal(whSkuInventoryTobefilled.getQty().toString());
                Double newOnHandQty = oldQty.add(newQty).doubleValue();
                skuIds.put(whSkuInventoryTobefilled.getSkuId(), newOnHandQty);
            } else {
                skuIds.put(whSkuInventoryTobefilled.getSkuId(), whSkuInventoryTobefilled.getQty());
            }
        }
        for (WhSkuInventoryAllocatedCommand whSkuInventoryAllocatedCommand : whSkuInventoryAllocatedCommandLst) {
            if (null != skuIds.get(whSkuInventoryAllocatedCommand.getSkuId())) {
                Double onHandQty = skuIds.get(whSkuInventoryAllocatedCommand.getSkuId());
                BigDecimal oldQty = new BigDecimal(onHandQty.toString());
                BigDecimal newQty = new BigDecimal(whSkuInventoryAllocatedCommand.getQty().toString());
                Double newOnHandQty = oldQty.subtract(newQty).doubleValue();
                skuIds.put(whSkuInventoryAllocatedCommand.getSkuId(), newOnHandQty);
            }
        }
        
        // 已经被占用的体积       
        Double occupyVolume = 0.0;
        // 计算sku体积
        for (Long key : skuIds.keySet()) {
            SkuRedisCommand skuRedis = this.locationManager.findSkuMasterBySkuId(key, ouId, logId);
            Sku sku = skuRedis.getSku();
            occupyVolume = occupyVolume + sku.getVolume() * skuIds.get(key);
        }
        maxVolume = (long) (location.getVolume() * upBound / 100);
        minVolume = (long) Math.ceil(new Double(location.getVolume() * downBound / 100));
        if (occupyVolume >= maxVolume) {
            log.error("occupyVolume >= maxVolume", occupyVolume, maxVolume);
            return null;
        }
        // 剩余体积        
        Long surplusVolume = (long) Math.floor(maxVolume - occupyVolume);
        
        List<WhSkuInventoryAllocatedCommand> skuInventoryAllocatedCommandLst = new ArrayList<WhSkuInventoryAllocatedCommand>();
        for (WhSkuInventoryAllocatedCommand whSkuInventoryAllocatedCommand : whSkuInventoryAllocatedCommandLst) {
            // 根据商品id和组织id获取商品所有相关属性
            SkuRedisCommand skuRedis = this.locationManager.findSkuMasterBySkuId(whSkuInventoryAllocatedCommand.getSkuId(), ouId, logId);
            Sku sku = skuRedis.getSku();
            // 可以放入sku数量      
            Long replenishmentQty = (long) Math.floor(surplusVolume / sku.getVolume());
            Long locationQty = 0L;
            if (StringUtils.hasText(location.getSizeType())) {
                // 仓库商品管理
                WhSkuWhmgmt skuWhmgmt = skuRedis.getWhSkuWhMgmt();
                if (skuWhmgmt != null) {
                    // 货品类型
                    if (skuWhmgmt.getTypeOfGoods() != null) {
                        LocationProductVolume locationProductVolume = this.locationManager.getLocationProductVolumeByPcIdAndSize(skuWhmgmt.getTypeOfGoods(), location.getSizeType(), ouId);
                        if (locationProductVolume != null) {
                            locationQty = locationProductVolume.getVolume();
                            // 上下限数量
                            Long maxQty = locationQty * upBound / 100;
                            Long minQty = (long) Math.ceil(new Double(locationQty * downBound / 100));
                            // 库位库存量=库位在库库存+库位待移入库存
                            double invQty = this.whskuInventoryManager.findInventoryByLocation(locationId, ouId);
                            if (invQty >= minQty) {
                                return null;
                            }
                            replenishmentQty = (long) Math.floor(maxQty - invQty);
                        }
                    }
                }
            }
            // 计算多余sku数量
            Long cQty = (long) Math.floor(whSkuInventoryAllocatedCommand.getQty() - replenishmentQty);
            Long rQty = 0L;
            if(0 < cQty){
                whSkuInventoryAllocatedCommand.setQty((double)replenishmentQty);
                skuInventoryAllocatedCommandLst.add(whSkuInventoryAllocatedCommand);
                break;
            }else{
                skuInventoryAllocatedCommandLst.add(whSkuInventoryAllocatedCommand);
            }
        }
        
        
        
        return skuInventoryAllocatedCommandLst;
    }

}
