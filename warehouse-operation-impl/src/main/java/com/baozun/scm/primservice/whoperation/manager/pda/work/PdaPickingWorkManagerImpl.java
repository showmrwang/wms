package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.CheckScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperationLineCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.work.PickingScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationExecLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhWorkCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoOutBoundBoxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionPickingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationExecLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOperationLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkOperDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryAllocatedDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.pda.concentration.PdaConcentrationManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.TipSkuDetailProvider;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.LocationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationLineManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhOperationManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhWorkManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.outbound.CheckingModeCalcManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPicking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationExecLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperationLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkOper;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryAllocated;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;
import com.baozun.utilities.type.StringUtil;

/**
 * PDA拣货manager
 * 
 * @author qiming.liu
 * 
 */
@Service("pdaPickingWorkManager")
@Transactional
public class PdaPickingWorkManagerImpl extends BaseManagerImpl implements PdaPickingWorkManager {

    protected static final Logger log = LoggerFactory.getLogger(PdaPickingWorkManagerImpl.class);

    @Autowired
    private PdaPickingWorkCacheManager pdaPickingWorkCacheManager;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private WhLocationDao whLocationDao;
    @Autowired
    private WhOperationManager whOperationManager;
    @Autowired
    private WhOperationLineManager whOperationLineManager;
    @Autowired
    private WhWorkOperDao whWorkOperDao;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private OutBoundBoxTypeDao outBoundBoxTypeDao;
    @Autowired
    private WhFunctionPickingDao whFunctionPickingDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private LocationManager locationManager;
    @Autowired
    private WhSkuDao whSkuDao;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private WhOperationExecLineDao whOperationExecLineDao;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhWorkManager workManager;
    @Autowired
    private PdaConcentrationManager pdaConcentrationManager;
    @Autowired
    private WhOperationDao whOperationDao;
    @Autowired
    private WhOperationLineDao whOperationLineDao;
    @Autowired
    private CheckingModeCalcManager checkingModeCalcManager;
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private OdoLineManager odoLineManager;
    @Autowired
    private WhOdoDao odoDao;
    @Autowired
    private WhOdoOutBoundBoxDao whOdoOutBoundBoxDao;
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private WhSkuInventoryAllocatedDao whSkuInventoryAllocatedDao;
    @Autowired
    private WhSkuInventoryLogManager whSkuInventoryLogManager;



    /**
     * 保存工作操作员信息
     * 
     * @author qiming.liu
     * @param workId
     * @param ouId
     * @param userId
     * 
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveWorkOper(Long workId, Long ouId, Long userId) {
        // 根据工作Id和ouId获取作业信息
        WhOperationCommand whOperationCommand = whOperationManager.findOperationByWorkId(workId, ouId);

        WhWorkOper whWorkOper = new WhWorkOper();
        // 操作员ID
        whWorkOper.setOperUserId(userId);
        // 工作ID
        whWorkOper.setOuId(ouId);
        // 仓库组织ID
        whWorkOper.setWorkId(workId);
        // 作业ID
        whWorkOper.setOperationId(whOperationCommand.getId());
        // 状态
        whWorkOper.setStatus(WorkStatus.NEW);
        // 是否管理员指派
        whWorkOper.setIsAdminAssign(false);
        // 创建时间
        whWorkOper.setCreateTime(new Date());
        // 最后操作时间
        whWorkOper.setLastModifyTime(new Date());
        // 创建人ID
        whWorkOper.setCreatedId(userId);
        // 修改人ID
        whWorkOper.setModifiedId(userId);
        // 操作人ID
        whWorkOper.setOperationId(userId);

        whWorkOperDao.insert(whWorkOper);

        // 根据作业id获取作业明细信息
        List<WhOperationLineCommand> operationLineList = whOperationLineManager.findOperationLineByOperationId(whOperationCommand.getId(), whOperationCommand.getOuId());
        for (WhOperationLineCommand operationLine : operationLineList) {
            // 根据出库单code获取出库单信息
            WhOdo odo = odoDao.findByIdOuId(operationLine.getOdoId(), operationLine.getOuId());
            if (null != odo && !OdoStatus.PICKING.equals(odo.getOdoStatus())) {
                odo.setOdoStatus(OdoStatus.PICKING);
                odo.setLagOdoStatus(OdoStatus.WAVE_FINISH);
                odoDao.update(odo);
            }
        }
    }

    /**
     * 统计分析工作及明细并缓存
     * 
     * @author qiming.liu
     * @param whWork
     * @param ouId
     * @return
     */
    @Override
    public void getOperatioLineForGroup(WhOperationCommand whOperationCommand) {
        // 所有小车
        Set<Long> outerContainers = new HashSet<Long>();
        // 所有出库箱
        Set<String> outbounxBoxs = new HashSet<String>();
        // 小车货格与出库箱对应关系
        Map<Integer, String> carStockToOutgoingBox = new HashMap<Integer, String>();
        // 所有周转箱
        Set<Long> turnoverBoxs = new HashSet<Long>();
        // 所有托盘
        Set<Long> pallets = new HashSet<Long>();
        // 所有货箱
        Set<Long> containers = new HashSet<Long>();
        // 所有库位
        Set<Long> locationIds = new HashSet<Long>();
        // 库位上所有外部容器
        Map<Long, Set<Long>> outerContainerIds = new HashMap<Long, Set<Long>>();
        // 库位上所有内部容器（无外部容器情况）
        Map<Long, Set<Long>> insideContainerIds = new HashMap<Long, Set<Long>>();
        // 库位上所有sku（sku不在任何容器内）
        Map<Long, Set<Long>> skuIds = new HashMap<Long, Set<Long>>();
        // 库位上每个sku总件数
        Map<Long, Map<Long, Long>> skuQty = new HashMap<Long, Map<Long, Long>>();
        // 库位上每个sku对应的唯一sku及件数
        Map<Long, Map<Long, Map<String, Long>>> skuAttrIds = new HashMap<Long, Map<Long, Map<String, Long>>>();
        // 库位上每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>> skuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();
        // 库位上每个唯一sku对应的货格（is_whole_case=0&&有小车&&库位上sku不在任何容器内）
        Map<Long, Map<String, Set<Integer>>> skuAttrIdsContainerLattice = new HashMap<Long, Map<String, Set<Integer>>>();
        // 外部容器对应所有内部容器
        Map<Long, Set<Long>> outerToInside = new HashMap<Long, Set<Long>>();
        // 内部容器对应所有sku
        Map<Long, Set<Long>> insideSkuIds = new HashMap<Long, Set<Long>>();
        // 内部容器每个sku总件数
        Map<Long, Map<Long, Long>> insideSkuQty = new HashMap<Long, Map<Long, Long>>();
        // 内部容器每个sku对应的唯一sku及件数
        Map<Long, Map<Long, Map<String, Long>>> insideSkuAttrIds = new HashMap<Long, Map<Long, Map<String, Long>>>();
        // 内部容器每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();
        // 内部容器每个唯一sku对应的货格（is_whole_case=0&&有小车）
        Map<Long, Map<String, Set<Integer>>> insideSkuAttrIdsContainerLattice = new HashMap<Long, Map<String, Set<Integer>>>();
        // 工作明细ID和唯一sku对应关系
        Map<String, String> workLineToOnlySku = new HashMap<String, String>();
        // 货格对应的库位号
        Map<Integer, Set<Long>> latticeLoc = new HashMap<Integer, Set<Long>>();
        // 货格+库位对应唯一sku对应的数量(散装的)
        Map<String, Map<String, Long>> latticeSkuAttrIdsQty = new HashMap<String, Map<String, Long>>();
        // 货格+库位对应唯一sku对应的数量(有货箱的)
        Map<String, Map<Long, Map<String, Long>>> latticeInsideSkuAttrIdsQty = new HashMap<String, Map<Long, Map<String, Long>>>();

        // 根据作业id获取作业明细信息
        List<WhOperationLineCommand> operationLineList = whOperationLineManager.findOperationLineByOperationId(whOperationCommand.getId(), whOperationCommand.getOuId());
        for (WhOperationLineCommand operationLine : operationLineList) {
            // 计划量减执行量
            int lineQty = operationLine.getQty().compareTo(operationLine.getCompleteQty());
            // 如果计划量减执行量等于0，跳出循环
            if (0 == lineQty) {
                continue;
            }
            // 流程相关统计信息
            if (whOperationCommand.getIsWholeCase() == false) {
                // 所有小车
                if (null != operationLine.getUseOuterContainerId()) {
                    outerContainers.add(operationLine.getUseOuterContainerId());
                }
                // 所有出库箱
                if (null != operationLine.getUseOutboundboxCode()) {
                    outbounxBoxs.add(operationLine.getUseOutboundboxCode());
                }
                // 小车货格与出库箱对应关系
                if (null != operationLine.getUseContainerLatticeNo()) {
                    carStockToOutgoingBox.put(operationLine.getUseContainerLatticeNo(), operationLine.getUseOutboundboxCode());
                }
                // 所有周转箱
                if (null != operationLine.getUseContainerId()) {
                    turnoverBoxs.add(operationLine.getUseContainerId());
                }
            } else {
                // 所有托盘
                if (null != operationLine.getFromOuterContainerId()) {
                    pallets.add(operationLine.getFromOuterContainerId());
                }
                // 所有货箱
                if (null != operationLine.getFromInsideContainerId()) {
                    containers.add(operationLine.getFromInsideContainerId());
                }
            }
            // 获取内部容器唯一sku
            String onlySku = SkuCategoryProvider.getSkuAttrIdByOperationLine(operationLine);
            String lineToSku = this.getWorkLineToOnlySku(operationLine.getId(), operationLine.getWorkLineId(), operationLine.getUuid());
            workLineToOnlySku.put(lineToSku, onlySku);
            // 根据出库单id和ouId获取出库单信息
            WhOdo whOdo = odoManager.findOdoByIdOuId(operationLine.getOdoId(), whOperationCommand.getOuId());
            List<WhSkuInventorySnCommand> skuInventorySnCommands = new ArrayList<WhSkuInventorySnCommand>();
            if(null != whOdo && null != whOdo.getOdoCode()){
                // 根据库存UUID查找对应SN/残次信息
                skuInventorySnCommands = whSkuInventorySnDao.findInvSnByOccupationCodeAndUuid(whOdo.getOdoCode(), operationLine.getUuid(), whOperationCommand.getOuId());
            }
            // 获取库位ID
            locationIds.add(operationLine.getFromLocationId());
            // 获取外部容器
            if (null != operationLine.getFromLocationId() && null != operationLine.getFromOuterContainerId() && null != outerContainerIds.get(operationLine.getFromLocationId())) {
                outerContainerIds.get(operationLine.getFromLocationId()).add(operationLine.getFromOuterContainerId());
            } else if (null != operationLine.getFromLocationId() && null != operationLine.getFromOuterContainerId() && null == outerContainerIds.get(operationLine.getFromLocationId())) {
                Set<Long> fromOuterContainerIdSet = new HashSet<Long>();
                fromOuterContainerIdSet.add(operationLine.getFromOuterContainerId());
                outerContainerIds.put(operationLine.getFromLocationId(), fromOuterContainerIdSet);
            }
            // 获取内部容器（无外部容器情况）
            if (null != operationLine.getFromLocationId() && null != operationLine.getFromInsideContainerId() && null == operationLine.getFromOuterContainerId()) {
                // 无外部容器情况
                if (insideContainerIds.get(operationLine.getFromLocationId()) != null) {
                    insideContainerIds.get(operationLine.getFromLocationId()).add(operationLine.getFromInsideContainerId());
                } else {
                    Set<Long> fromInsideContainerIdSet = new HashSet<Long>();
                    fromInsideContainerIdSet.add(operationLine.getFromInsideContainerId());
                    insideContainerIds.put(operationLine.getFromLocationId(), fromInsideContainerIdSet);
                }
            }
            // sku不在任何容器内
            if (null == operationLine.getFromOuterContainerId() && null == operationLine.getFromInsideContainerId()) {
                // 获取sku（sku不在任何容器内）
                if (null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null != skuIds.get(operationLine.getFromLocationId())) {
                    skuIds.get(operationLine.getFromLocationId()).add(operationLine.getSkuId());
                } else if (null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null == skuIds.get(operationLine.getFromLocationId())) {
                    Set<Long> skuIdSet = new HashSet<Long>();
                    skuIdSet.add(operationLine.getSkuId());
                    skuIds.put(operationLine.getFromLocationId(), skuIdSet);
                }
                // 获取每个sku总件数
                if (null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != skuQty.get(operationLine.getFromLocationId())) {
                    Map<Long, Long> skuIdQtyMap = new HashMap<Long, Long>();
                    skuIdQtyMap = skuQty.get(operationLine.getFromLocationId());
                    if (null != skuIdQtyMap.get(operationLine.getSkuId())) {
                        Long qty = skuIdQtyMap.get(operationLine.getSkuId()) + (long) (operationLine.getQty() - operationLine.getCompleteQty());
                        skuIdQtyMap.put(operationLine.getSkuId(), qty);
                    } else {
                        skuIdQtyMap.put(operationLine.getSkuId(), (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                    }
                    skuQty.put(operationLine.getFromLocationId(), skuIdQtyMap);
                } else if (null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null == skuQty.get(operationLine.getFromLocationId())) {
                    Map<Long, Long> skuIdQtyMap = new HashMap<Long, Long>();
                    skuIdQtyMap.put(operationLine.getSkuId(), (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                    skuQty.put(operationLine.getFromLocationId(), skuIdQtyMap);
                }
                // 获取每个sku对应的唯一sku及件数
                if (null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != onlySku && null != skuAttrIds.get(operationLine.getFromLocationId())) {
                    Map<Long, Map<String, Long>> skuIdMap = new HashMap<Long, Map<String, Long>>();
                    skuIdMap = skuAttrIds.get(operationLine.getFromLocationId());
                    if (null != skuIdMap.get(operationLine.getSkuId())) {
                        Map<String, Long> skuAttrIdsQty = skuIdMap.get(operationLine.getSkuId());
                        if (null != skuAttrIdsQty.get(onlySku)) {
                            skuAttrIdsQty.put(onlySku, skuAttrIdsQty.get(onlySku) + (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                        } else {
                            skuAttrIdsQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                        }
                        skuIdMap.put(operationLine.getSkuId(), skuAttrIdsQty);
                    } else {
                        Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                        insideSkuAttrIdsQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                        skuIdMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                    }
                    skuAttrIds.put(operationLine.getFromLocationId(), skuIdMap);
                } else if (null != operationLine.getFromLocationId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != onlySku && null == skuAttrIds.get(operationLine.getFromLocationId())) {
                    Map<Long, Map<String, Long>> skuIdMap = new HashMap<Long, Map<String, Long>>();
                    Map<String, Long> skuAttrIdsQty = new HashMap<String, Long>();
                    skuAttrIdsQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                    skuIdMap.put(operationLine.getSkuId(), skuAttrIdsQty);
                    skuAttrIds.put(operationLine.getFromLocationId(), skuIdMap);
                }
                // 库位上每个唯一sku对应的所有sn及残次条码
                if (null != operationLine.getFromLocationId() && null != onlySku && null != skuAttrIdsSnDefect.get(operationLine.getFromLocationId())) {
                    Map<String, Set<String>> onlySkuSnMap = new HashMap<String, Set<String>>();
                    onlySkuSnMap = skuAttrIdsSnDefect.get(operationLine.getFromLocationId());
                    if (null != onlySkuSnMap.get(onlySku)) {
                        Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                        snDefectWareBarcodeSet = onlySkuSnMap.get(onlySku);
                        for (int i = 0; i < operationLine.getQty() - operationLine.getCompleteQty(); i++) {
                            if (null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)) {
                                snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));
                            }
                        }
                        onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    } else {
                        Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                        for (int i = 0; i < operationLine.getQty() - operationLine.getCompleteQty(); i++) {
                            if (null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)) {
                                snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));
                            }
                        }
                        onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    }
                    skuAttrIdsSnDefect.put(operationLine.getFromLocationId(), onlySkuSnMap);
                } else if (null != operationLine.getFromLocationId() && null != onlySku && null == skuAttrIdsSnDefect.get(operationLine.getFromLocationId())) {
                    Map<String, Set<String>> onlySkuSnMap = new HashMap<String, Set<String>>();
                    Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                    for (int i = 0; i < operationLine.getQty() - operationLine.getCompleteQty(); i++) {
                        if (null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)) {
                            snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));
                        }
                    }
                    onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    skuAttrIdsSnDefect.put(operationLine.getFromLocationId(), onlySkuSnMap);
                }
                // 库位上每个唯一sku对应的货格（is_whole_case=0&&有小车&&库位上sku不在任何容器内）
                if (null != operationLine.getFromLocationId() && null != onlySku && null != operationLine.getUseContainerLatticeNo() && whOperationCommand.getIsWholeCase() == false) {
                    if (null != skuAttrIdsContainerLattice.get(operationLine.getFromLocationId())) {
                        Map<String, Set<Integer>> onlySkuUseContainerLatticeNoMap = new HashMap<String, Set<Integer>>();
                        onlySkuUseContainerLatticeNoMap = skuAttrIdsContainerLattice.get(operationLine.getFromLocationId());
                        if (null != onlySkuUseContainerLatticeNoMap.get(onlySku)) {
                            Set<Integer> useContainerLatticeNoSet = new HashSet<Integer>();
                            useContainerLatticeNoSet = onlySkuUseContainerLatticeNoMap.get(onlySku);
                            useContainerLatticeNoSet.add(operationLine.getUseContainerLatticeNo());
                            onlySkuUseContainerLatticeNoMap.put(onlySku, useContainerLatticeNoSet);
                        } else {
                            Set<Integer> useContainerLatticeNoSet = new HashSet<Integer>();
                            useContainerLatticeNoSet.add(operationLine.getUseContainerLatticeNo());
                            onlySkuUseContainerLatticeNoMap.put(onlySku, useContainerLatticeNoSet);
                        }
                        skuAttrIdsContainerLattice.put(operationLine.getFromLocationId(), onlySkuUseContainerLatticeNoMap);
                    } else {
                        Map<String, Set<Integer>> onlySkuUseContainerLatticeNoMap = new HashMap<String, Set<Integer>>();
                        Set<Integer> useContainerLatticeNoSet = new HashSet<Integer>();
                        useContainerLatticeNoSet.add(operationLine.getUseContainerLatticeNo());
                        onlySkuUseContainerLatticeNoMap.put(onlySku, useContainerLatticeNoSet);
                        skuAttrIdsContainerLattice.put(operationLine.getFromLocationId(), onlySkuUseContainerLatticeNoMap);
                    }
                }
            }
            // 存在外部容器并且有对应内部容器
            if (null != operationLine.getFromInsideContainerId()) {
                if (null != operationLine.getFromOuterContainerId()) {
                    // 外部容器对应所有内部容器
                    if (null != operationLine.getFromOuterContainerId() && null != operationLine.getSkuId() && null != outerToInside.get(operationLine.getFromOuterContainerId())) {
                        outerToInside.get(operationLine.getFromOuterContainerId()).add(operationLine.getFromInsideContainerId());
                    } else if (null != operationLine.getFromOuterContainerId() && null != operationLine.getSkuId() && null == outerToInside.get(operationLine.getFromOuterContainerId())) {
                        Set<Long> fromInsideContainerIdSet = new HashSet<Long>();
                        fromInsideContainerIdSet.add(operationLine.getFromInsideContainerId());
                        outerToInside.put(operationLine.getFromOuterContainerId(), fromInsideContainerIdSet);
                    }
                }
                // 内部容器对应所有sku
                if (null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null != insideSkuIds.get(operationLine.getFromInsideContainerId())) {
                    insideSkuIds.get(operationLine.getFromInsideContainerId()).add(operationLine.getSkuId());
                } else if (null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null == insideSkuIds.get(operationLine.getFromInsideContainerId())) {
                    Set<Long> skuIdSet = new HashSet<Long>();
                    skuIdSet.add(operationLine.getSkuId());
                    insideSkuIds.put(operationLine.getFromInsideContainerId(), skuIdSet);
                }
                // 内部容器每个sku总件数
                if (null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != insideSkuQty.get(operationLine.getFromInsideContainerId())) {
                    Map<Long, Long> skuIdQtyMap = new HashMap<Long, Long>();
                    skuIdQtyMap = insideSkuQty.get(operationLine.getFromInsideContainerId());
                    if (null != skuIdQtyMap.get(operationLine.getSkuId())) {
                        Long insQty = skuIdQtyMap.get(operationLine.getSkuId()) + (long) (operationLine.getQty() - operationLine.getCompleteQty());
                        skuIdQtyMap.put(operationLine.getSkuId(), insQty);
                    } else {
                        skuIdQtyMap.put(operationLine.getSkuId(), (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                    }
                    insideSkuQty.put(operationLine.getFromInsideContainerId(), skuIdQtyMap);
                } else if (null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null == insideSkuQty.get(operationLine.getFromInsideContainerId())) {
                    Map<Long, Long> skuIdQtyMap = new HashMap<Long, Long>();
                    skuIdQtyMap.put(operationLine.getSkuId(), (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                    insideSkuQty.put(operationLine.getFromInsideContainerId(), skuIdQtyMap);
                }
                // 内部容器每个sku对应的唯一sku及件数
                if (null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != onlySku && null != insideSkuAttrIds.get(operationLine.getFromInsideContainerId())) {
                    Map<Long, Map<String, Long>> fromInsideContainerIdMap = new HashMap<Long, Map<String, Long>>();
                    fromInsideContainerIdMap = insideSkuAttrIds.get(operationLine.getFromInsideContainerId());
                    if (null != fromInsideContainerIdMap.get(operationLine.getSkuId())) {
                        Map<String, Long> insideSkuAttrIdsQty = fromInsideContainerIdMap.get(operationLine.getSkuId());
                        if (null != insideSkuAttrIdsQty.get(onlySku)) {
                            insideSkuAttrIdsQty.put(onlySku, insideSkuAttrIdsQty.get(onlySku) + (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                        } else {
                            insideSkuAttrIdsQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                        }
                        fromInsideContainerIdMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                    } else {
                        Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                        insideSkuAttrIdsQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                        fromInsideContainerIdMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                    }
                    insideSkuAttrIds.put(operationLine.getFromInsideContainerId(), fromInsideContainerIdMap);
                } else if (null != operationLine.getFromInsideContainerId() && null != operationLine.getSkuId() && null != operationLine.getQty() && null != onlySku && null == insideSkuAttrIds.get(operationLine.getFromInsideContainerId())) {
                    Map<Long, Map<String, Long>> fromInsideContainerIdMap = new HashMap<Long, Map<String, Long>>();
                    Map<String, Long> insideSkuAttrIdsQty = new HashMap<String, Long>();
                    insideSkuAttrIdsQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                    fromInsideContainerIdMap.put(operationLine.getSkuId(), insideSkuAttrIdsQty);
                    insideSkuAttrIds.put(operationLine.getFromInsideContainerId(), fromInsideContainerIdMap);
                }
                // 内部容器每个唯一sku对应的所有sn及残次条码
                if (null != operationLine.getFromInsideContainerId() && null != onlySku && null != insideSkuAttrIdsSnDefect.get(operationLine.getFromInsideContainerId())) {
                    Map<String, Set<String>> onlySkuSnMap = new HashMap<String, Set<String>>();
                    onlySkuSnMap = insideSkuAttrIdsSnDefect.get(operationLine.getFromInsideContainerId());
                    if (null != onlySkuSnMap.get(onlySku)) {
                        Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                        snDefectWareBarcodeSet = onlySkuSnMap.get(onlySku);
                        for (int i = 0; i < operationLine.getQty() - operationLine.getCompleteQty(); i++) {
                            if (null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)) {
                                snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));
                            }
                        }
                        onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    } else {
                        Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                        for (int i = 0; i < operationLine.getQty() - operationLine.getCompleteQty(); i++) {
                            if (null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)) {
                                snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));
                            }
                        }
                        onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    }
                    insideSkuAttrIdsSnDefect.put(operationLine.getFromInsideContainerId(), onlySkuSnMap);
                } else if (null != operationLine.getFromInsideContainerId() && null != onlySku && null == insideSkuAttrIdsSnDefect.get(operationLine.getFromInsideContainerId())) {
                    Map<String, Set<String>> onlySkuSnMap = new HashMap<String, Set<String>>();
                    Set<String> snDefectWareBarcodeSet = new HashSet<String>();
                    for (int i = 0; i < operationLine.getQty() - operationLine.getCompleteQty(); i++) {
                        if (null != skuInventorySnCommands && i < skuInventorySnCommands.size() && null != skuInventorySnCommands.get(i)) {
                            snDefectWareBarcodeSet.add(SkuCategoryProvider.concatSkuAttrId(skuInventorySnCommands.get(i).getSn(), skuInventorySnCommands.get(i).getDefectWareBarcode()));
                        }
                    }
                    onlySkuSnMap.put(onlySku, snDefectWareBarcodeSet);
                    insideSkuAttrIdsSnDefect.put(operationLine.getFromInsideContainerId(), onlySkuSnMap);
                }
                // 内部容器每个唯一sku对应的货格（is_whole_case=0&&有小车）
                if (null != operationLine.getFromInsideContainerId() && null != onlySku && null != operationLine.getUseContainerLatticeNo() && whOperationCommand.getIsWholeCase() == false) {
                    if (null != insideSkuAttrIdsContainerLattice.get(operationLine.getFromInsideContainerId())) {
                        Map<String, Set<Integer>> onlySkuUseContainerLatticeNoMap = new HashMap<String, Set<Integer>>();
                        onlySkuUseContainerLatticeNoMap = insideSkuAttrIdsContainerLattice.get(operationLine.getFromInsideContainerId());
                        if (null != onlySkuUseContainerLatticeNoMap.get(onlySku)) {
                            Set<Integer> useContainerLatticeNoSet = new HashSet<Integer>();
                            useContainerLatticeNoSet = onlySkuUseContainerLatticeNoMap.get(onlySku);
                            useContainerLatticeNoSet.add(operationLine.getUseContainerLatticeNo());
                            onlySkuUseContainerLatticeNoMap.put(onlySku, useContainerLatticeNoSet);
                        } else {
                            Set<Integer> useContainerLatticeNoSet = new HashSet<Integer>();
                            useContainerLatticeNoSet.add(operationLine.getUseContainerLatticeNo());
                            onlySkuUseContainerLatticeNoMap.put(onlySku, useContainerLatticeNoSet);
                        }
                        insideSkuAttrIdsContainerLattice.put(operationLine.getFromInsideContainerId(), onlySkuUseContainerLatticeNoMap);
                    } else {
                        Map<String, Set<Integer>> onlySkuUseContainerLatticeNoMap = new HashMap<String, Set<Integer>>();
                        Set<Integer> useContainerLatticeNoSet = new HashSet<Integer>();
                        useContainerLatticeNoSet.add(operationLine.getUseContainerLatticeNo());
                        onlySkuUseContainerLatticeNoMap.put(onlySku, useContainerLatticeNoSet);
                        insideSkuAttrIdsContainerLattice.put(operationLine.getFromInsideContainerId(), onlySkuUseContainerLatticeNoMap);
                    }
                }
            }
            // 货格统计
            if (null != operationLine.getUseContainerLatticeNo()) {
                if (null != operationLine.getFromLocationId()) {
                    // 货格对应库位
                    if (null != latticeLoc.get(operationLine.getUseContainerLatticeNo())) {
                        Set<Long> fromLocationIds = new HashSet<Long>();
                        fromLocationIds = latticeLoc.get(operationLine.getUseContainerLatticeNo());
                        fromLocationIds.add(operationLine.getFromLocationId());
                        latticeLoc.put(operationLine.getUseContainerLatticeNo(), fromLocationIds);
                    } else {
                        Set<Long> fromLocationIds = new HashSet<Long>();
                        fromLocationIds.add(operationLine.getFromLocationId());
                        latticeLoc.put(operationLine.getUseContainerLatticeNo(), fromLocationIds);
                    }
                    String key = operationLine.getUseContainerLatticeNo().toString() + operationLine.getFromLocationId().toString();
                    if (null != operationLine.getFromInsideContainerId()) {
                        // 货格+库位对应唯一sku对应的数量(有货箱的)
                        if (null != latticeInsideSkuAttrIdsQty.get(key)) {
                            Map<Long, Map<String, Long>> insideContainerMap = new HashMap<Long, Map<String, Long>>();
                            insideContainerMap = latticeInsideSkuAttrIdsQty.get(key);
                            if (null != insideContainerMap.get(operationLine.getFromInsideContainerId())) {
                                Map<String, Long> onlySkuAndQty = new HashMap<String, Long>();
                                onlySkuAndQty = insideContainerMap.get(operationLine.getFromInsideContainerId());
                                if (null != onlySkuAndQty.get(onlySku)) {
                                    onlySkuAndQty.put(onlySku, onlySkuAndQty.get(onlySku) + (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                                } else {
                                    onlySkuAndQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                                }
                                insideContainerMap.put(operationLine.getFromInsideContainerId(), onlySkuAndQty);
                            } else {
                                Map<String, Long> onlySkuAndQty = new HashMap<String, Long>();
                                onlySkuAndQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                                insideContainerMap.put(operationLine.getFromInsideContainerId(), onlySkuAndQty);
                            }
                            latticeInsideSkuAttrIdsQty.put(key, insideContainerMap);
                        } else {
                            Map<Long, Map<String, Long>> insideContainerMap = new HashMap<Long, Map<String, Long>>();
                            Map<String, Long> onlySkuAndQty = new HashMap<String, Long>();
                            onlySkuAndQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                            insideContainerMap.put(operationLine.getFromInsideContainerId(), onlySkuAndQty);
                            latticeInsideSkuAttrIdsQty.put(key, insideContainerMap);
                        }
                    }else{
                        // 货格+库位对应唯一sku对应的数量(散装的)
                        if(null != latticeSkuAttrIdsQty.get(key)){
                            Map<String, Long> onlySkuAndQty = new HashMap<String, Long>();
                            onlySkuAndQty = latticeSkuAttrIdsQty.get(key);
                            if(null != onlySkuAndQty.get(onlySku)){
                                onlySkuAndQty.put(onlySku, onlySkuAndQty.get(onlySku) + (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                            }else{
                                onlySkuAndQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                            }
                            latticeSkuAttrIdsQty.put(key, onlySkuAndQty);
                        }else{
                            Map<String, Long> onlySkuAndQty = new HashMap<String, Long>();
                            onlySkuAndQty.put(onlySku, (long) (operationLine.getQty() - operationLine.getCompleteQty()));
                            latticeSkuAttrIdsQty.put(key, onlySkuAndQty);
                        }
                    }
                }
            }
        }

        // 载入统计分析信息
        OperatioLineStatisticsCommand statisticsCommand = new OperatioLineStatisticsCommand();
        // 是否整托整箱
        statisticsCommand.setIsWholeCase(whOperationCommand.getIsWholeCase());
        // 所有小车
        statisticsCommand.setOuterContainers(outerContainers);
        // 所有出库箱
        statisticsCommand.setOutbounxBoxs(outbounxBoxs);
        // 小车货格与出库箱对应关系
        statisticsCommand.setCarStockToOutgoingBox(carStockToOutgoingBox);
        // 所有周转箱
        statisticsCommand.setTurnoverBoxs(turnoverBoxs);
        // 所有托盘
        statisticsCommand.setPallets(pallets);
        // 所有货箱
        statisticsCommand.setContainers(containers);
        // 库位排序
        List<Long> sortLocationIds = new ArrayList<Long>();
        sortLocationIds = locationManager.sortByIds(locationIds, whOperationCommand.getOuId());
        // 所有库位
        statisticsCommand.setLocationIds(sortLocationIds);
        // 库位上所有外部容器
        statisticsCommand.setOuterContainerIds(outerContainerIds);
        // 库位上所有内部容器
        statisticsCommand.setInsideContainerIds(insideContainerIds);
        // 库位上所有sku
        statisticsCommand.setSkuIds(skuIds);
        // 库位上每个sku总件数
        statisticsCommand.setSkuQty(skuQty);
        // 库位上每个sku对应的唯一sku及件数
        statisticsCommand.setSkuAttrIds(skuAttrIds);
        // 库位上每个唯一sku对应的所有sn及残次条码
        statisticsCommand.setSkuAttrIdsSnDefect(skuAttrIdsSnDefect);
        // 库位上每个唯一sku对应的货格（is_whole_case=0&&有小车&&库位上sku不在任何容器内）
        statisticsCommand.setSkuAttrIdsContainerLattice(skuAttrIdsContainerLattice);
        // 外部容器对应所有内部容器
        statisticsCommand.setOuterToInside(outerToInside);
        // 内部容器对应所有sku
        statisticsCommand.setInsideSkuIds(insideSkuIds);
        // 内部容器每个sku总件数
        statisticsCommand.setInsideSkuQty(insideSkuQty);
        // 内部容器每个sku对应的唯一sku及件数
        statisticsCommand.setInsideSkuAttrIds(insideSkuAttrIds);
        // 内部容器每个唯一sku对应的所有sn及残次条码
        statisticsCommand.setInsideSkuAttrIdsSnDefect(insideSkuAttrIdsSnDefect);
        // 内部容器每个唯一sku对应的货格（is_whole_case=0&&有小车）
        statisticsCommand.setInsideSkuAttrIdsContainerLattice(insideSkuAttrIdsContainerLattice);
        // 工作明细ID和唯一sku对应关系
        statisticsCommand.setWorkLineIdToOnlySku(workLineToOnlySku);
        // 货格对应的库位号
        statisticsCommand.setLatticeLoc(latticeLoc);
        // 货格+库位对应唯一sku对应的数量(散装的)
        statisticsCommand.setLatticeSkuAttrIdsQty(latticeSkuAttrIdsQty);
        // 货格+库位对应唯一sku对应的数量(有货箱的)
        statisticsCommand.setLatticeInsideSkuAttrIdsQty(latticeInsideSkuAttrIdsQty);

        // 缓存统计分析结果
        pdaPickingWorkCacheManager.operatioLineStatisticsRedis(whOperationCommand.getId(), statisticsCommand);
        this.removeOutBoundBox(whOperationCommand.getId());
    }

    /**
     * pda拣货推荐容器
     * 
     * @author tangming
     * @param command
     * @param pickingWay
     * @return
     */
    @Override
    public PickingScanResultCommand pdaPickingRemmendContainer(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl pdaPickingRemmendContainer is start");
        PickingScanResultCommand pSRcmd = new PickingScanResultCommand();
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        Long functionId = command.getFunctionId();
        Integer pickingWay = command.getPickingWay();
        pSRcmd.setOperationId(operationId);
        pSRcmd.setPickingWay(pickingWay);
        // 缓存作业明细
        pdaPickingWorkCacheManager.cacheOperationLine(operationId, ouId);
        if (pickingWay == Constants.PICKING_WAY_ONE) { // 使用外部容器(小车) 无出库箱拣货流程
            String tipOuterContainer = pdaPickingWorkCacheManager.pdaPickingWorkTipOutContainer(operationId, ouId);
            pSRcmd.setTipOuterContainer(tipOuterContainer); // 提示小车
            ContainerCommand container = containerDao.getContainerByCode(tipOuterContainer, ouId);
            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(container.getTwoLevelType(), ouId);
            if (null == c2c) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            pSRcmd.setName(c2c.getCategoryName());
        }
        if (pickingWay == Constants.PICKING_WAY_TWO) { // 使用外部(小车)，有出库箱拣货流程
            String tipOuterContainer = pdaPickingWorkCacheManager.pdaPickingWorkTipOutContainer(operationId, ouId);
            pSRcmd.setTipOuterContainer(tipOuterContainer); // 提示小车
            ContainerCommand container = containerDao.getContainerByCode(tipOuterContainer, ouId);
            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(container.getTwoLevelType(), ouId);
            if (null == c2c) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            pSRcmd.setName(c2c.getCategoryName());
        }
        if (pickingWay == Constants.PICKING_WAY_THREE) { // 使用出库箱拣货流程
            String tipOutBounxBoxCode = pdaPickingWorkCacheManager.pdaPickingWorkTipoutboundBox(operationId, ouId);
            pSRcmd.setOutBounxBoxCode(tipOutBounxBoxCode);
            // OutBoundBoxType outBoundBox = outBoundBoxTypeDao.findByCode(tipOutBounxBoxCode,
            // ouId);
            // if (null == outBoundBox) {
            // throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL);
            // }
            // // 验证容器Lifecycle是否有效
            // if (!outBoundBox.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
            // throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_STATUS_NO);
            // }
            // pSRcmd.setName(outBoundBox.getName());
        }
        if (pickingWay == Constants.PICKING_WAY_FOUR) { // 使用周转箱拣货流程
            String turnoverBox = pdaPickingWorkCacheManager.pdaPickingWorkTipTurnoverBox(operationId, ouId);
            pSRcmd.setTipTurnoverBoxCode(turnoverBox);
            ContainerCommand container = containerDao.getContainerByCode(turnoverBox, ouId);
            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(container.getTwoLevelType(), ouId);
            if (null == c2c) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            pSRcmd.setName(c2c.getCategoryName());
        }
        if (pickingWay == Constants.PICKING_WAY_FIVE || pickingWay == Constants.PICKING_WAY_SIX) {
            WhFunctionPicking picking = whFunctionPickingDao.findByFunctionIdExt(ouId, functionId);
            pSRcmd.setIsScanLocation(picking.getIsScanLocation());// 是否扫描库位
            pSRcmd.setIsScanOuterContainer(picking.getIsScanOuterContainer());// 是否扫描托盘
            pSRcmd.setIsScanInsideContainer(picking.getIsScanInsideContainer());// 是否扫描内部容器
            pSRcmd.setIsScanSku(picking.getIsScanSku());// 是否扫描sku
            pSRcmd.setIsScanInvAttr(picking.getIsScanInvAttr());// 是否扫描sku属性
            pSRcmd.setScanPattern(picking.getScanPattern());// 扫描模式
            pSRcmd.setIsTipInvAttr(picking.getIsTipInvAttr());// 是否提示商品库存属性
            pSRcmd.setPalletPickingMode(picking.getPalletPickingMode());// 整拖拣货模式
            pSRcmd.setContainerPickingMode(picking.getContainerPickingMode()); //整箱拣货模式
            OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
            List<Long> locationIds = operatorLine.getLocationIds();
            CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.tipLocation(operationId, locationIds);
            if (cSRCmd.getIsNeedTipLoc()) { // 提示库位
                Long locationId = cSRCmd.getTipLocationId();
                Location location = whLocationDao.findByIdExt(locationId, ouId);
                if (null == location) {
                    throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
                }
                pSRcmd.setLocationId(locationId);
                pSRcmd.setTipLocationCode(location.getCode());
            }
        }
        log.info("PdaPickingWorkManagerImpl pdaPickingRemmendContainer is end");
        return pSRcmd;
    }


    /***
     * 移除出库箱缓存(小车加出库箱的情况)
     * @param operationId
     */
    private void removeOutBoundBox(Long operationId) {
        OperationLineCacheCommand tipLocationCmd = cacheManager.getObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
        if (null != tipLocationCmd) {
            tipLocationCmd.setTipOutBonxBoxIds(null);
            cacheManager.setObject(CacheConstants.CACHE_OPERATION_LINE + operationId.toString(), tipLocationCmd, CacheConstants.CACHE_ONE_DAY);
        }
    }

    /***
     * pda推荐容器拣货扫描容器
     * @author tangming
     * @param command
     * @return
     */
    public PickingScanResultCommand pdaPickingScanContainer(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl pdaPickingScanContainer is start");
        Integer pickingWay = command.getPickingWay();
        Long ouId = command.getOuId();
        Long functionId = command.getFunctionId();
        Long operationId = command.getOperationId();
        String containerCode = command.getOuterContainer(); // 小车
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if (null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        if (pickingWay == Constants.PICKING_WAY_ONE) {
            // 修改小车状态
            this.updateContainerStauts(containerCode, ouId);
            // 提示货格
        }
        if (pickingWay == Constants.PICKING_WAY_TWO) { // 使用外部(小车)，有出库箱拣货流程
            // 修改小车状态
            this.updateContainerStauts(containerCode, ouId);
            Map<Integer, String> carStockToOutgoingBox = operatorLine.getCarStockToOutgoingBox(); // 出库箱和货格对应关系
            List<WhOperationLineCommand> operatorLineList = whOperationLineManager.findOperationLineByOperationId(operationId, ouId);
            CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.pdaPickingTipOutBounxBoxCode(operatorLineList, operationId, carStockToOutgoingBox);
            if (cSRCmd.getIsNeedScanOutBounxBox()) {
                command.setTipOutBounxBoxCode(cSRCmd.getOutBounxBoxCode()); // 出库箱id
                command.setIsNeedScanLatticeNo(true);
                command.setUseContainerLatticeNo(cSRCmd.getUseContainerLatticeNo());
                command.setOuterContainer(containerCode); // 外部容器号(小车，单个出库箱)
                WhFunctionPicking picking = whFunctionPickingDao.findByFunctionIdExt(ouId, functionId);
                command.setIsScanLatticeNo(picking.getIsScanLatticeNo()); // 是否扫描货格
                command.setIsScanOutBoundBox(picking.getIsScanOutBoundBox()); // 是否扫描出库箱
                return command;
            } else {
                command.setIsNeedScanLatticeNo(false);
            }
        }
        if (pickingWay == Constants.PICKING_WAY_FOUR) { // 周转箱
            String turnoverBoxCode = command.getTurnoverBoxCode();
            this.updateContainerStauts(turnoverBoxCode, ouId);
        }
        command.setOuterContainer(containerCode); // 外部容器号(小车，单个出库箱)
        WhFunctionPicking picking = whFunctionPickingDao.findByFunctionIdExt(ouId, functionId);
        command.setIsScanLocation(picking.getIsScanLocation()); // 是否扫描库位
        command.setIsScanOuterContainer(picking.getIsScanOuterContainer()); // 是否扫描托盘
        command.setIsScanInsideContainer(picking.getIsScanInsideContainer()); // 是否扫描内部容器
        command.setIsScanSku(picking.getIsScanSku()); // 是否扫描sku
        command.setIsScanInvAttr(picking.getIsScanInvAttr()); // 是否扫描sku属性
        command.setScanPattern(picking.getScanPattern()); // 扫描模式
        command.setIsTipInvAttr(picking.getIsTipInvAttr()); // 是否提示商品库存属性
        List<Long> locationIds = operatorLine.getLocationIds();
        CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.tipLocation(operationId, locationIds);
        if (cSRCmd.getIsNeedTipLoc()) { // 提示库位
            Long locationId = cSRCmd.getTipLocationId();
            Location location = whLocationDao.findByIdExt(locationId, ouId);
            if (null == location) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
            }
            command.setTipLocationBarCode(location.getBarCode());
            command.setTipLocationCode(location.getCode());
            command.setLocationId(locationId);
        }
        log.info("PdaPickingWorkManagerImpl pdaPickingScanContainer is end");
        return command;
    }

    private void updateContainerStauts(String containerCode, Long ouId) {
        log.info("PdaPickingWorkManagerImpl updateContainerStauts is start");
        Container container = new Container();
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        BeanUtils.copyProperties(containerCmd, container);
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_PICKING);
        containerDao.saveOrUpdateByVersion(container);
        log.info("PdaPickingWorkManagerImpl updateContainerStauts is end");

    }

    /**
     * pda拣货确认提示外部容器托盘
     * @author tangming
     * @param command
     * @param pickingWay
     * @return
     */
    @Override
    public PickingScanResultCommand tipOuterContainer(PickingScanResultCommand command) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkManagerImpl scanLocation is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        String locationCode = command.getTipLocationCode();
        String locationBarCode = command.getTipLocationBarCode();
        if (!StringUtil.isEmpty(locationBarCode) && StringUtil.isEmpty(locationCode)) {
            locationCode = locationBarCode;
        }
        Location location = whLocationDao.findLocationByCode(locationCode, ouId);
        if (null == location) {
            location = whLocationDao.getLocationByBarcode(locationCode, ouId);
            if (null == location) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
            }
        }
        Long locationId = location.getId();
        command.setLocationId(location.getId());
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if (null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> outerContainerIdsLoc = operatorLine.getOuterContainerIds();
        Set<Long> outerContainerIds = outerContainerIdsLoc.get(locationId);
        if (null != outerContainerIds && outerContainerIds.size() != 0) {
            CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.pdaPickingTipOuterContainer(outerContainerIds, locationId,operationId);
            if (cSRCmd.getIsNeedTipOutContainer()) {// 该库位上的所有外部外部容器都扫描完毕
                Long outerContainerId = cSRCmd.getTipOuterContainerId();
                // 判断外部容器
                Container c = containerDao.findByIdExt(outerContainerId, ouId);
                // 提示外部容器编码
                command.setTipOuterContainerCode(c.getCode());
                command.setIsTipOuterContainer(true);
            } else {
                command.setIsTipOuterContainer(false);
            }

        } else {
            command.setIsTipOuterContainer(false);
        }
        log.info("PdaPickingWorkManagerImpl scanLocation is end");
        return command;
    }

    /***
     * 确认提示托盘
     * 
     * @author tangming
     * @param command
     * @return
     */
    public PickingScanResultCommand tipInsideContainer(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl confirmTipOuterContainer is start");
        Long operationId = command.getOperationId();
        String tipOuterContainerCode = command.getTipOuterContainerCode();
        Long ouId = command.getOuId();
        Long locationId = command.getLocationId();
        ContainerCommand outerContainerCmd = null;
        Long outerId = null;
        if (!StringUtil.isEmpty(tipOuterContainerCode)) {
            outerContainerCmd = containerDao.getContainerByCode(tipOuterContainerCode, ouId);
            if (null == outerContainerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerId = outerContainerCmd.getId();
        }
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if (null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> outerToInside = operatorLine.getOuterToInside(); // 本托盘上所有对应的内部容器
        Map<Long, Set<Long>> locInsideContainerIds = operatorLine.getInsideContainerIds(); // 库位上所有的内部容器
        Set<Long> insideContainerIds = null;
        if (null == outerContainerCmd) {
            insideContainerIds = locInsideContainerIds.get(locationId);
        } else {
            insideContainerIds = outerToInside.get(outerId);
        }
        if (null != insideContainerIds && insideContainerIds.size() != 0) {
            CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.pdaPickingTipInsideContainer(insideContainerIds, locationId, outerId,operationId);
            if (cSRCmd.getIsNeedTipInsideContainer()) { // 托盘上还有货箱没有扫描
                Long tipInsideContainerId = cSRCmd.getTipiInsideContainerId();
                Container ic = containerDao.findByIdExt(tipInsideContainerId, ouId);
                command.setTipInsideContainerCode(ic.getCode());
                command.setOuterContainerCode(tipOuterContainerCode);
                command.setIsTipinsideCotnainer(true);
            } else {
                command.setIsTipinsideCotnainer(false);
            }
        } else {
            command.setIsTipinsideCotnainer(false);
        }

        log.info("PdaPickingWorkManagerImpl confirmTipOuterContainer is end");
        return command;
    }


    /***
     * 提示sku
     * @author tangminmg
     * @param command
     * @return
     */
    public PickingScanResultCommand tipSku(PickingScanResultCommand command, String operationWay) {
        log.info("PdaPickingWorkManagerImpl tipSku is start");
        Long operationId = command.getOperationId();
        Long ouId = command.getOuId();
        Long locationId = command.getLocationId();
        String tipInsideContainerCode = command.getTipInsideContainerCode(); // 内部容器
        String tipOuterContainerCode = command.getTipOuterContainerCode();
        Integer pickingWay = command.getPickingWay();
        ContainerCommand insideContainerCmd = null;
        Long insideContainerId = null;
        if (!StringUtil.isEmpty(tipInsideContainerCode)) {
            insideContainerCmd = containerDao.getContainerByCode(tipInsideContainerCode, ouId);
            if (insideContainerCmd == null) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = insideContainerCmd.getId();
        }
        Long outerContainerId = null;
        ContainerCommand outerContainerCmd = null;
        if (!StringUtils.isEmpty(tipOuterContainerCode)) {
            outerContainerCmd = containerDao.getContainerByCode(tipOuterContainerCode, ouId);
            if (outerContainerCmd == null) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerContainerId = outerContainerCmd.getId();
        }
        // 缓存内部容器
        // if (!StringUtil.isEmpty(tipInsideContainerCode)) {
        // this.cacheInsideContainerCode(locationId, insideContainerId, outerContainerId);
        // }
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if (null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> insideSkuIds = operatorLine.getInsideSkuIds(); // 内部容器对应所有的sku
        Map<Long, Set<Long>> locSkuIds = operatorLine.getSkuIds(); // 库位上散放的sku
        Map<Long, Map<String, Set<String>>> skuAttrIdsSnDefect = operatorLine.getSkuAttrIdsSnDefect(); // 库位上每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect = operatorLine.getInsideSkuAttrIdsSnDefect(); // 内部容器每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<Long, Map<String, Long>>> locSkuAttrIdsQty = operatorLine.getSkuAttrIds(); // 库位上每个sku对应的唯一sku及件数
        Map<Long, Map<Long, Map<String, Long>>> insideSkuAttrIds = operatorLine.getInsideSkuAttrIds();
        Map<Long, Map<String, Set<Integer>>> insideSkuAttrIdsContainerLattice = operatorLine.getInsideSkuAttrIdsContainerLattice();
        Map<Long, Map<String, Set<Integer>>> skuAttrIdsContainerLattice = operatorLine.getSkuAttrIdsContainerLattice();
        Map<String, Map<String, Long>> latticeSkuAttrIdsQty = operatorLine.getLatticeSkuAttrIdsQty(); // 货格+库位对应唯一sku对应的数量(散装的)
        Map<String, Map<Long, Map<String, Long>>> latticeInsideSkuAttrIdsQty = operatorLine.getLatticeInsideSkuAttrIdsQty(); // 货格+库位对应唯一sku对应的数量(有货箱的)
        Map<Long, Map<String, Long>> skuIdSkuAttrIdsQty = null;
        if (null != insideContainerId) {// 有货箱
            skuIdSkuAttrIdsQty = insideSkuAttrIds.get(insideContainerId);
        } else {// 没货箱
            skuIdSkuAttrIdsQty = locSkuAttrIdsQty.get(locationId);
        }
        // 容器内对应的没写
        Set<Long> skuIds = null;
        if (null == insideContainerId) { // sku直接放在库位上
            skuIds = locSkuIds.get(locationId);
        } else {
            skuIds = insideSkuIds.get(insideContainerId);
        }
        CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.pdaPickingTipSku(outerContainerId, operationWay, skuIds, operationId, locationId, ouId, insideContainerId, skuAttrIdsSnDefect, insideSkuAttrIdsSnDefect);
        if (cSRCmd.getIsNeedScanSku()) { // 此货箱的sku，还没有扫描完毕
            String skuAttrId = cSRCmd.getTipSkuAttrId(); // 提示唯一的sku
            Long skuId = SkuCategoryProvider.getSkuId(skuAttrId);
            Map<String, Long> skuAttrIdsQty = null;
            if (pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO) {
//                Map<String, Set<Integer>> skuAttrIdsLattice = null;
                Integer lattice = null;
                // 如果有sn残次信息,去掉sn/残次信息
                String skuAttrIdNoSn = SkuCategoryProvider.getSkuAttrId(skuAttrId);
                if (null != insideContainerId) {// 有货箱
//                    skuAttrIdsLattice = insideSkuAttrIdsContainerLattice.get(insideContainerId);
                    lattice = pdaPickingWorkCacheManager.tipLatticeNo(skuAttrIdNoSn, insideContainerId, operationId,ouId);
                } else {// 没有货箱
//                    skuAttrIdsLattice = skuAttrIdsContainerLattice.get(locationId);
                    lattice = pdaPickingWorkCacheManager.tipLatticeNoLoc(locationId, operationId, skuAttrIdNoSn, ouId);
                }
//                Set<Integer> latticeNos = skuAttrIdsLattice.get(skuAttrIdNoSn);
//                Iterator<Integer> it = latticeNos.iterator();
//                while (it.hasNext()) {
//                    lattice = it.next();
//                    break;
//                }
                // 获得当前货格对应的数量
                String key = lattice.toString() + locationId;
                if (null != insideContainerId) {// 有货箱
                    Map<Long, Map<String, Long>> insideSkuAttrIdsQty = latticeInsideSkuAttrIdsQty.get(key);
                    skuAttrIdsQty = insideSkuAttrIdsQty.get(insideContainerId);
                } else {
                    skuAttrIdsQty = latticeSkuAttrIdsQty.get(key);
                }
                //缓存货格号
//                this.cacheLatticeNo(operationId, lattice);
                command.setUseContainerLatticeNo(lattice);
            } else {
                skuAttrIdsQty = skuIdSkuAttrIdsQty.get(skuId);
            }
            WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
            if (null == skuCmd) {
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            command.setTipSkuBarCode(skuCmd.getBarCode()); // 提示sku
            command.setIsNeedTipSku(true);
            command.setSkuId(skuId);
            this.tipSkuDetailAspect(command, skuAttrId, skuAttrIdsQty, logId);
            command.setIsNeedScanSkuSn(cSRCmd.getIsNeedScanSkuSn());
        } else {
            command.setIsNeedTipSku(false);
        }
        log.info("PdaPickingWorkManagerImpl tipSku is end");

        return command;
    }

//    private void cacheLatticeNo(Long operationId, Integer lattice) {
//        ArrayDeque<Integer> latticeList = cacheManager.getObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString());
//        if (null == latticeList) {
//            latticeList = new ArrayDeque<Integer>();
//        }
//        latticeList.addFirst(lattice);
//        cacheManager.setObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString(), latticeList, CacheConstants.CACHE_ONE_DAY);
//    }

    /***
     * 判断货箱内库存属性是否唯一
     * @param command
     * @return
     */
    public PickingScanResultCommand judgeSkuAttrIdsIsUnique(PickingScanResultCommand command, String operationWay) {
        log.info("PdaPickingWorkManagerImpl judgeSkuAttrIdsIsUnique is start");
        Long operationId = command.getOperationId();
        Long locationId = command.getLocationId();
        Long ouId = command.getOuId();
        Long insideContainerId = null;
        String insideContainerCode = command.getTipInsideContainerCode();
        String outerContainerCode = command.getTipOuterContainerCode();
        String skuBarcode = command.getTipSkuBarCode();
        Long skuId = command.getSkuId();
        if (!StringUtil.isEmpty(insideContainerCode)) {
            ContainerCommand ic = containerDao.getContainerByCode(insideContainerCode, ouId);
            if (null == ic) {
                // 容器信息不存在
                log.error("pdaScanContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = ic.getId();
        }
        Long outerContainerId = null;
        if (!StringUtil.isEmpty(outerContainerCode)) {
            ContainerCommand c = containerDao.getContainerByCode(outerContainerCode, ouId);
            if (null == c) {
                // 容器信息不存在
                log.error("pdaScanContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerContainerId = c.getId();
        }
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if (null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Set<Long>> insideContainerSkuIds = operatorLine.getInsideSkuIds();
        Map<Long, Set<Long>> locSkuIds = operatorLine.getSkuIds();
        Set<Long> icSkuIds = null;
        if (StringUtil.isEmpty(insideContainerCode)) { // 商品直接放在库位上
            icSkuIds = locSkuIds.get(locationId);
        } else { // 放在货箱里
            icSkuIds = insideContainerSkuIds.get(insideContainerId);
        }
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId); // 获取对应的商品数量,key值是sku
                                                                                                 // id
        boolean isSkuExists = false;
        for (Long cacheId : cacheSkuIdsQty.keySet()) {
            if (icSkuIds.contains(cacheId)) {
                isSkuExists = true;
            }
            if (true == isSkuExists) {
                // skuId = cacheId;
                break;
            }
        }
        if (false == isSkuExists) {
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerId, insideContainerId, skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideContainerCode});
        }
        // 该库位要拣货的所有库存记录
        // List<WhSkuInventoryCommand> list =
        // pdaPickingWorkCacheManager.cacheLocationInventory(operationId, locationId, ouId,
        // operationWay);
        List<WhSkuInventoryCommand> list = null;
        if (Constants.PICKING_INVENTORY.equals(operationWay)) { // 拣货
            list = whSkuInventoryDao.getWhSkuInventoryCmdByOccupationLineId(locationId, ouId, operationId, outerContainerId, insideContainerId);
        }
        if (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay)) {// 补货
            list = whSkuInventoryDao.getWhSkuInventoryCommandByOperationId(ouId, operationId, locationId, outerContainerId, insideContainerId);
        }
        if (Constants.INVMOVE_PICKING_INVENTORY.equals(operationWay)) {// 库内移动
            list = whSkuInventoryDao.getWhSkuInventoryCommandByInvMove(ouId, operationId, locationId, outerContainerId, insideContainerId);
        }
        if (null == list || list.size() == 0) {
            throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);
        }
        // 货箱内待拣货sku库位库存库存属性是否唯一
        Set<String> skuAttrIdsSet = new HashSet<String>();
        for (WhSkuInventoryCommand invSkuCmd : list) {
            String pSkuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd);
            skuAttrIdsSet.add(pSkuAttrId);
        }
        if (skuAttrIdsSet.size() > 1) { // 货箱内待拣货sku库存属性不唯一
            command.setIsUniqueSkuAttrInside(false); // 不唯一
        } else {// 货箱内待拣货sku库存属性唯一
            command.setIsUniqueSkuAttrInside(true); // 唯一
        }
        log.info("PdaPickingWorkManagerImpl judgeSkuAttrIdsIsUnique is end");

        return command;

    }

    /***
     * 
     * @param srCmd
     * @param tipSkuAttrId
     * @param locSkuAttrIds
     * @param skuAttrIdsQty
     * @param logId
     */
    private void tipSkuDetailAspect(PickingScanResultCommand srCmd, String tipSkuAttrId, Map<String, Long> skuAttrIdsQty, String logId) {
        String skuAttrId = SkuCategoryProvider.getSkuAttrId(tipSkuAttrId);
        Long qty = skuAttrIdsQty.get(skuAttrId);
        if (null == qty) {
            log.error("sku qty is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        srCmd.setTipSkuQty(qty);
        srCmd.setIsNeedScanSkuInvType(TipSkuDetailProvider.isTipSkuInvType(tipSkuAttrId)); // 是否需要扫描商品类型
        if (true == srCmd.getIsNeedScanSkuInvType()) {
            String skuInvType = TipSkuDetailProvider.getSkuInvType(tipSkuAttrId);
            List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroup(Constants.INVENTORY_TYPE, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : invTypeList) {
                if (sd.getDicValue().equals(skuInvType)) {
                    srCmd.setSkuInvType(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv type is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvType("");
        }
        srCmd.setIsNeedScanSkuInvStatus(TipSkuDetailProvider.isTipSkuInvStatus(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvStatus()) {
            String skuInvStatus = TipSkuDetailProvider.getSkuInvStatus(tipSkuAttrId);
            List<InventoryStatus> invStatusList = inventoryStatusManager.findAllInventoryStatus();
            boolean isExists = false;
            for (InventoryStatus is : invStatusList) {
                if (is.getId().toString().equals(skuInvStatus)) {
                    srCmd.setSkuInvStatusName(is.getName());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv status is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvStatus(null);
        }
        srCmd.setIsNeedScanBatchNumber(TipSkuDetailProvider.isTipSkuBatchNumber(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanBatchNumber()) {
            String skuBatchNumber = TipSkuDetailProvider.getSkuBatchNumber(tipSkuAttrId);
            srCmd.setBatchNumber(skuBatchNumber);
        } else {
            srCmd.setBatchNumber("");
        }
        srCmd.setIsNeedScanOrigin(TipSkuDetailProvider.isTipSkuCountryOfOrigin(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanOrigin()) {
            String skuCountryOfOrigin = TipSkuDetailProvider.getSkuCountryOfOrigin(tipSkuAttrId);
            srCmd.setSkuOrigin(skuCountryOfOrigin);
        } else {
            srCmd.setSkuOrigin("");
        }
        // sku不在任何容器内
        srCmd.setIsNeedScanSkuMfgDate(TipSkuDetailProvider.isTipSkuMfgDate(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuMfgDate()) {
            String skuMfgDate = TipSkuDetailProvider.getSkuMfgDate(tipSkuAttrId);
            srCmd.setSkuMfgDate(skuMfgDate);
        } else {
            srCmd.setSkuMfgDate("");
        }
        srCmd.setIsNeedScanSkuExpDate(TipSkuDetailProvider.isTipSkuExpDate(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuExpDate()) {
            String skuExpDate = TipSkuDetailProvider.getSkuExpDate(tipSkuAttrId);
            srCmd.setSkuExpDate(skuExpDate);
        } else {
            srCmd.setSkuExpDate("");
        }
        srCmd.setIsNeedScanSkuInvAttr1(TipSkuDetailProvider.isTipSkuInvAttr1(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvAttr1()) {
            String skuInvAttr1 = TipSkuDetailProvider.getSkuInvAttr1(tipSkuAttrId);
            List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_1, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : list) {
                if (sd.getDicValue().equals(skuInvAttr1)) {
                    srCmd.setSkuInvAttr1(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv attr4 is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvAttr1("");
        }
        srCmd.setIsNeedScanSkuInvAttr2(TipSkuDetailProvider.isTipSkuInvAttr2(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvAttr2()) {
            String skuInvAttr2 = TipSkuDetailProvider.getSkuInvAttr2(tipSkuAttrId);
            List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_2, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : list) {
                if (sd.getDicValue().equals(skuInvAttr2)) {
                    srCmd.setSkuInvAttr2(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv attr2 is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvAttr2("");
        }
        srCmd.setIsNeedScanSkuInvAttr3(TipSkuDetailProvider.isTipSkuInvAttr3(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvAttr3()) {
            String skuInvAttr3 = TipSkuDetailProvider.getSkuInvAttr3(tipSkuAttrId);
            List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_3, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : list) {
                if (sd.getDicValue().equals(skuInvAttr3)) {
                    srCmd.setSkuInvAttr3(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv attr3 is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvAttr3("");
        }
        srCmd.setIsNeedScanSkuInvAttr4(TipSkuDetailProvider.isTipSkuInvAttr4(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvAttr4()) {
            String skuInvAttr4 = TipSkuDetailProvider.getSkuInvAttr4(tipSkuAttrId);
            List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_4, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : list) {
                if (sd.getDicValue().equals(skuInvAttr4)) {
                    srCmd.setSkuInvAttr4(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv attr4 is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvAttr4("");
        }
        srCmd.setIsNeedScanSkuInvAttr5(TipSkuDetailProvider.isTipSkuInvAttr5(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuInvAttr5()) {
            String skuInvAttr5 = TipSkuDetailProvider.getSkuInvAttr5(tipSkuAttrId);
            List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_5, BaseModel.LIFECYCLE_NORMAL);
            boolean isExists = false;
            for (SysDictionary sd : list) {
                if (sd.getDicValue().equals(skuInvAttr5)) {
                    srCmd.setSkuInvAttr5(sd.getDicLabel());
                    isExists = true;
                    break;
                }
            }
            if (false == isExists) {
                log.error("inv attr5 is not found error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
            }
        } else {
            srCmd.setSkuInvAttr5("");
        }
        srCmd.setIsNeedScanSkuSn(TipSkuDetailProvider.isTipSkuSn(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuSn()) {
            String skuSn = TipSkuDetailProvider.getSkuSn(tipSkuAttrId);
            srCmd.setSkuSn(skuSn);
        } else {
            srCmd.setSkuSn("");
        }
        srCmd.setIsNeedScanSkuDefect(TipSkuDetailProvider.isTipSkuDefect(tipSkuAttrId));
        if (true == srCmd.getIsNeedScanSkuDefect()) {
            String skuDefect = TipSkuDetailProvider.getSkuDefect(tipSkuAttrId);
            srCmd.setSkuDefect(skuDefect);
        } else {
            srCmd.setSkuDefect("");
        }
    }

    /***
     * pda扫描sku
     * 
     * @author tangminmg
     * @param command
     * @return
     */
    public PickingScanResultCommand scanSku(PickingScanResultCommand command, WhSkuCommand skuCmd, Boolean isTabbInvTotal, String operationWay) {
        log.info("PdaPickingWorkManagerImpl scanSku is start");
        Long operationId = command.getOperationId();
        Long locationId = command.getLocationId();
        String outBoundBoxCode = command.getOutBounxBoxCode();
        Long userId = command.getUserId();
        Integer pickingWay = null;
        if (Constants.PICKING_INVENTORY.equals(operationWay)) { // 拣货(库位库存变成容器库存)
            pickingWay = command.getPickingWay();
        }
        if (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay)) {
            pickingWay = command.getReplenishWay();
        }
        if (Constants.INVMOVE_PICKING_INVENTORY.equals(operationWay)) {// 库内移动
            pickingWay = command.getInWarehouseMoveWay();
        }
        Long ouId = command.getOuId();
        Long skuId = command.getSkuId();
        String skuBarCode = command.getSkuBarCode();
        String insideContainerCode = command.getTipInsideContainerCode();
        String outerContainerCode = command.getTipOuterContainerCode();
        String turnoverBoxCode = command.getTurnoverBoxCode(); // 周转箱
        Long outBoundBoxId = command.getOutBoundBoxId(); // 出库箱id
        Boolean isShortPikcing = command.getIsShortPicking();
        Boolean isShortPickingEnd = command.getIsShortPickingEnd(); // 拣货完成
        String outerContainer = command.getOuterContainer();
        Integer latticeNo = command.getUseContainerLatticeNo(); // 当前货格
        Long containerId = null;
        if (!StringUtils.isEmpty(outerContainer)) {
            ContainerCommand cmd = containerDao.getContainerByCode(outerContainer, ouId);
            if (null == cmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            containerId = cmd.getId();
        }
        Long turnoverBoxId = null;
        String workCode = command.getWorkBarCode();
        Integer scanPattern = command.getScanPattern();
        if (!StringUtils.isEmpty(turnoverBoxCode)) {
            ContainerCommand turnoverBoxCmd = containerDao.getContainerByCode(turnoverBoxCode, ouId);
            if (null == turnoverBoxCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            turnoverBoxId = turnoverBoxCmd.getId();
        }
        Long insideContainerId = null;
        ContainerCommand insideContainerCmd = null;
        if (!StringUtil.isEmpty(insideContainerCode)) {
            insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
            if (null == insideContainerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = insideContainerCmd.getId();
        }
        ContainerCommand outerContainerCmd = null;
        Long outerContainerId = null;
        if (!StringUtil.isEmpty(outerContainerCode)) {
            outerContainerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
            if (null == outerContainerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerContainerId = outerContainerCmd.getId();
        }
        Double scanQty = skuCmd.getScanSkuQty(); // 扫描的商品数量
        if (null == scanQty || scanQty.longValue() < 1) {
            log.error("scan sku qty is valid, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
        }
        if (StringUtils.isEmpty(skuBarCode)) {
            log.error("sku is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        WhSkuInventoryCommand invSkuCmd = new WhSkuInventoryCommand();
        invSkuCmd.setSkuId(command.getSkuId());
        // invSkuCmd.setInvType(command.getSkuInvType());
        invSkuCmd.setBatchNumber(command.getBatchNumber());
        try {
            if (!StringUtils.isEmpty(command.getSkuMfgDate())) {
                invSkuCmd.setMfgDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(command.getSkuMfgDate()));
            } else {
                invSkuCmd.setMfgDate(null);
            }
            if (!StringUtils.isEmpty(command.getSkuExpDate())) {
                invSkuCmd.setExpDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(command.getSkuExpDate()));
            } else {
                invSkuCmd.setExpDate(null);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        invSkuCmd.setCountryOfOrigin(command.getSkuOrigin());
        List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroup(Constants.INVENTORY_TYPE, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : invTypeList) {
            if (sd.getDicLabel().equals(command.getSkuInvType())) {
                invSkuCmd.setInvType(sd.getDicValue());
                break;
            }
        }
        List<InventoryStatus> invStatusList = inventoryStatusManager.findAllInventoryStatus();
        for (InventoryStatus is : invStatusList) {
            if (is.getName().toString().equals(command.getSkuInvStatusName())) {
                invSkuCmd.setInvStatus(is.getId());
                break;
            }
        }
        List<SysDictionary> list1 = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_1, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : list1) {
            if (sd.getDicLabel().equals(command.getSkuInvAttr1())) {
                invSkuCmd.setInvAttr1(sd.getDicValue());
                break;
            }
        }
        List<SysDictionary> list2 = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_2, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : list2) {
            if (sd.getDicLabel().equals(command.getSkuInvAttr2())) {
                invSkuCmd.setInvAttr2(sd.getDicValue());
                break;
            }
        }
        List<SysDictionary> list3 = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_3, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : list3) {
            if (sd.getDicLabel().equals(command.getSkuInvAttr3())) {
                invSkuCmd.setInvAttr3(sd.getDicValue());
                break;
            }
        }
        List<SysDictionary> list4 = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_4, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : list4) {
            if (sd.getDicLabel().equals(command.getSkuInvAttr4())) {
                invSkuCmd.setInvAttr4(sd.getDicValue());
                break;
            }
        }
        List<SysDictionary> list5 = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_5, BaseModel.LIFECYCLE_NORMAL);
        for (SysDictionary sd : list5) {
            if (sd.getDicLabel().equals(command.getSkuInvAttr5())) {
                invSkuCmd.setInvAttr5(sd.getDicValue());
                break;
            }
        }
        String skuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd); // 没有sn/残次信息
        pdaPickingWorkCacheManager.cacheSkuAttrIdNoSn(locationId, skuAttrIds, insideContainerId, operationId);
        if (!StringUtil.isEmpty(command.getSkuSn()) || !StringUtils.isEmpty(command.getSkuDefect())) {
            // 缓存sn数据
            // String snDefect =
            // SkuCategoryProvider.concatSkuAttrId(command.getSkuSn(),command.getSkuDefect()); //
            // 拼接sn/残次信息
            // String skuAttrIdsSn = SkuCategoryProvider.concatSkuAttrId(skuAttrIds,
            // command.getSkuSn(), command.getSkuDefect()); // 拼接sn/残次信息
            // pdaPickingWorkCacheManager.cacheSkuAttrId(locationId, skuAttrIdsSn,
            // insideContainerId); // 缓存的必须有sn/残次信息
            this.updateSnDefectOccupation(skuId, skuAttrIds, command.getSkuSn(), command.getSkuDefect(), locationId, ouId, operationId, outerContainerId, insideContainerId, operationWay);
            // this.cacheSkuSn(locationId, insideContainerId, skuId, snDefect);
        }
        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarCode, logId); // 获取对应的商品数量,key值是sku
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if (null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Long, Map<Long, Map<String, Long>>> locSkuAttrIdsQty = operatorLine.getSkuAttrIds(); // 库位上每个sku对应的唯一sku及件数
        Map<Long, Map<Long, Map<String, Long>>> insideSkuAttrIds = operatorLine.getInsideSkuAttrIds(); // 内部容器每个sku对应的唯一sku及件数
        Map<Long, Set<Long>> insideSkuIds = operatorLine.getInsideSkuIds(); // 内部容器内所有的sku
        Map<Long, Set<Long>> operLocSkuIds = operatorLine.getSkuIds(); // 库位上所有sku(sku不在任何容器内)
        Map<Long, Set<Long>> locOuterContainerIds = operatorLine.getOuterContainerIds();
        Set<Long> outerContainerIds = locOuterContainerIds.get(locationId); // 当前库位上所有外部容器集合
        Set<Long> icSkuIds = insideSkuIds.get(insideContainerId);
        List<Long> locationIds = operatorLine.getLocationIds();
        Map<Long, Map<Long, Long>> operLocSkuQty = operatorLine.getSkuQty(); // 库位上每个sku总件数
                                                                             // (不在容器内，散装sku)*/
        Map<Long, Long> locSkuQty = operLocSkuQty.get(locationId);
        Map<Long, Map<Long, Long>> insideSkuQty = operatorLine.getInsideSkuQty(); // 内部容器每个sku总件数
        Map<Long, Long> insideContainerSkuIdsQty = insideSkuQty.get(insideContainerId); // 内部容器sku对应的总件数
        Map<Long, Set<Long>> operLocInsideContainerIds = operatorLine.getInsideContainerIds();// 库位上所有的内部容器(无外部容器情况)
        Set<Long> locInsideContainerIds = operLocInsideContainerIds.get(locationId);
        Map<Long, Set<Long>> insideContainerSkuIds = operatorLine.getInsideSkuIds(); // 库位上内部容器对应的所有sku
        Map<Long, Set<Long>> outerToInsideIds = operatorLine.getOuterToInside(); // (库位上有外部容器的内部容器)
        Map<Long, Map<String, Set<String>>> skuAttrIdsSnDefect = operatorLine.getSkuAttrIdsSnDefect(); // 库位上每个唯一sku对应的所有sn及残次条码
        Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect = operatorLine.getInsideSkuAttrIdsSnDefect(); // 内部容器每个唯一sku对应的所有sn及残次条码
        Map<String, Map<String, Long>> latticeSkuAttrIdsQty = operatorLine.getLatticeSkuAttrIdsQty(); //
        Map<String, Map<Long, Map<String, Long>>> latticeInsideSkuAttrIdsQty = operatorLine.getLatticeInsideSkuAttrIdsQty();//
        Map<Long, Map<String, Set<Integer>>> insideSkuAttrIdsContainerLattice = operatorLine.getInsideSkuAttrIdsContainerLattice();
        Map<Long, Map<String, Set<Integer>>> skuAttrIdsContainerLattice = operatorLine.getSkuAttrIdsContainerLattice();
        Map<String, Set<Integer>> insideSkuAttrIdsLattice = null;
        Map<String, Set<Integer>> skuAttrIdsLattice = null;
        Map<String, Long> latticeSkuQty = null; // 没有货箱
        Map<String, Long> latticeInsideSkuQty = null; // 有货箱
        if (Constants.PICKING_INVENTORY.equals(operationWay)) { // 拣货(库位库存变成容器库存)
            if (pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO) { // 小车+小车出库箱
                String key = latticeNo.toString() + locationId;
                if (null != insideContainerId) {
                    Map<Long, Map<String, Long>> insideSkuAttrIdsQty = latticeInsideSkuAttrIdsQty.get(key);
                    latticeInsideSkuQty = insideSkuAttrIdsQty.get(insideContainerId);
                    insideSkuAttrIdsLattice = insideSkuAttrIdsContainerLattice.get(insideContainerId);
                }
                latticeSkuQty = latticeSkuAttrIdsQty.get(key);
                skuAttrIdsLattice = skuAttrIdsContainerLattice.get(locationId);
            }
        }
        Set<Long> insideContainerIds = null;
        if (null != outerContainerId) {
            insideContainerIds = outerToInsideIds.get(outerContainerId);// 外部容器对应的内内部容器
        } else { // 直接放在库位上的货箱
            insideContainerIds = operLocInsideContainerIds.get(locationId);
        }
        boolean isSkuExists = false;
        Integer cacheSkuQty = 1;
        Integer icSkuQty = 1;
        Set<Long> skuIds = null;
        if (null != insideContainerId) {
            skuIds = insideSkuIds.get(insideContainerId);
        } else {
            skuIds = operLocSkuIds.get(locationId);
        }
        for (Long cacheId : cacheSkuIdsQty.keySet()) {
            if (skuIds.contains(cacheId)) {
                isSkuExists = true;
            }
            if (true == isSkuExists) {
                skuId = cacheId;
                cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                if (null != insideContainerId) {
                    icSkuQty = (null == insideContainerSkuIdsQty.get(cacheId) ? 1 : insideContainerSkuIdsQty.get(cacheId).intValue());
                } else {
                    icSkuQty = (null == locSkuQty.get(cacheId) ? 1 : locSkuQty.get(cacheId).intValue());
                }
                break;
            }
        }
        if (false == isSkuExists) {
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {});
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
                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarCode, logId);
                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
            }
        }
        skuCmd.setId(skuId);
        skuCmd.setScanSkuQty(scanQty * cacheSkuQty);// 可能是多条码
        skuCmd.setIsNeedTipSkuDefect(command.getIsNeedScanSkuDefect());
        skuCmd.setIsNeedTipSkuSn(command.getIsNeedScanSkuSn());
        if (Constants.PICKING_WAY_SIX == pickingWay && true == isShortPikcing) {
            this.cacheContainerShortPickingSkuAttrIds(skuAttrIds, operationId, insideContainerId, skuCmd.getScanSkuQty());
        }
        if (Constants.PICKING_WAY_FIVE == pickingWay && true == isShortPikcing) {
            this.cachePalletShortPickingSkuAttrIds(skuAttrIds, operationId, insideContainerId, outerContainerId, skuCmd.getScanSkuQty());
        }
        CheckScanResultCommand cSRCmd =
                pdaPickingWorkCacheManager.pdaPickingyCacheSkuAndCheckContainer(latticeNo,pickingWay, latticeSkuQty, latticeInsideSkuQty, operationWay, ouId, operLocSkuIds, insideSkuAttrIdsSnDefect,
                        skuAttrIdsSnDefect, insideSkuAttrIds, locSkuAttrIdsQty, skuAttrIds, scanPattern, locationIds, locSkuQty, locationId, icSkuIds, outerContainerIds, outerContainerCmd, operationId, insideContainerSkuIds, insideContainerIds,
                        locInsideContainerIds, insideContainerCmd, skuCmd);
        if (cSRCmd.getIsContinueScanSn()) {
            command.setIsContinueScanSn(true);
            String skuAttrId = cSRCmd.getTipSkuAttrId(); // 提示唯一的sku包含唯一sku
            Map<Long, Map<String, Long>> skuIdSkuAttrIdsQty = null;
            if (cSRCmd.getIsHaveInsideContainer()) { // 当前sku有货箱
                skuIdSkuAttrIdsQty = insideSkuAttrIds.get(insideContainerId);
            } else {
                skuIdSkuAttrIdsQty = locSkuAttrIdsQty.get(locationId);
            }
            Long skuId1 = SkuCategoryProvider.getSkuId(skuAttrId);
            Map<String, Long> skuAttrIdsQty = skuIdSkuAttrIdsQty.get(skuId1);
            WhSkuCommand whSkuCmd = whSkuDao.findWhSkuByIdExt(SkuCategoryProvider.getSkuId(skuAttrId), ouId);
            if (null == whSkuCmd) {
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            command.setTipSkuBarCode(whSkuCmd.getBarCode());
            // command.setIsNeedTipSku(true);
            command.setSkuId(skuId);
            this.tipSkuDetailAspect(command, skuAttrId, skuAttrIdsQty, logId);
            command.setIsNeedScanSkuSn(cSRCmd.getIsNeedScanSkuSn());
        } else if (cSRCmd.getIsNeedScanSku()) {
            if (!cSRCmd.getIsHaveInsideContainer()) { // 判断当前sku有没有货箱
                command.setTipOuterContainerCode(null);
                command.setTipInsideContainerCode(null);
            }
            List<String> snList = null;
            if (null != insideContainerId) {// 有货箱
                snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + insideContainerId.toString() + skuId);
            } else {
                snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + locationId.toString() + skuId);
            }
            if (Constants.PICKING_INVENTORY.equals(operationWay)) { // 拣货(库位库存变成容器库存)
                if (Constants.PICKING_WAY_FIVE != pickingWay && Constants.PICKING_WAY_SIX != pickingWay) {
                    // 添加作业执行明细
                    List<WhOperationExecLine> execLineList =
                            this.addPickingOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, operationWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                    operationId, ouId, skuCmd.getScanSkuQty(), latticeNo);
                    // 添加容器库存
                    whSkuInventoryManager.pickingAddContainerInventory(execLineList, snList, containerId, locationId, skuAttrIds, operationId, ouId, isTabbInvTotal, userId, pickingWay, command.getScanPattern(), skuCmd.getScanSkuQty(), turnoverBoxId,
                            outerContainerId, insideContainerId, isShortPikcing, null);
                }
            }
            if (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay)) { // 补货中的拣货(库位库存变成容器库存)
                if (Constants.REPLENISH_WAY_TWO != pickingWay && Constants.REPLENISH_WAY_TWO != pickingWay) {
                    // 添加作业执行明细
                    List<WhOperationExecLine> execLineList =
                            this.addPickingOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, operationWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                    operationId, ouId, skuCmd.getScanSkuQty(), latticeNo);
                    // 已分配的库位库存转变为容器库存
                    whSkuInventoryManager.replenishmentContainerInventory(execLineList, isShortPikcing, snList, skuAttrIds, locationId, operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId, workCode,
                            skuCmd.getScanSkuQty());
                }
            }
            if (Constants.INVMOVE_PICKING_INVENTORY.equals(operationWay)) { // 库内移动中的拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addInvMoveOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty());
                // 已分配的库位库存转变为容器库存
                whSkuInventoryManager.invMoveContainerInventory(isShortPikcing, snList, skuAttrIds, locationId, operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId, workCode, skuCmd.getScanSkuQty());
            }
            command.setIsContinueScanSn(false);
            String skuAttrId = cSRCmd.getTipSkuAttrId(); // 提示唯一的sku包含唯一sku
            String skuAttrIdNoSn = SkuCategoryProvider.getSkuAttrId(skuAttrId);
            Map<String, Long> skuAttrIdsQty = null;
            if (Constants.PICKING_INVENTORY.equals(operationWay) && (pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO)) {
                Integer lattice = null;
                if (cSRCmd.getIsTipNewLattice()) {// 使用新的货格
                    if (cSRCmd.getIsHaveInsideContainer()) {
                        lattice = pdaPickingWorkCacheManager.tipLatticeNo(skuAttrIdNoSn, insideContainerId, operationId, ouId);
                    } else {
                        lattice = pdaPickingWorkCacheManager.tipLatticeNoLoc(locationId,  operationId, skuAttrIdNoSn, ouId);
                    }
                }
                String key = lattice.toString() + locationId;
                if (cSRCmd.getIsHaveInsideContainer()) {
                    Map<Long, Map<String, Long>> insideSkuAttrIdsQty = latticeInsideSkuAttrIdsQty.get(key);
                    skuAttrIdsQty = insideSkuAttrIdsQty.get(insideContainerId);
                } else {
                    skuAttrIdsQty = latticeSkuAttrIdsQty.get(key);
                }
                command.setUseContainerLatticeNo(lattice);
            } else {
                Map<Long, Map<String, Long>> skuIdSkuAttrIdsQty = null;
                if (cSRCmd.getIsHaveInsideContainer()) { // 当前sku有货箱
                    skuIdSkuAttrIdsQty = insideSkuAttrIds.get(insideContainerId);
                } else {
                    skuIdSkuAttrIdsQty = locSkuAttrIdsQty.get(locationId);
                }
                Long skuId1 = SkuCategoryProvider.getSkuId(skuAttrIdNoSn);
                skuAttrIdsQty = skuIdSkuAttrIdsQty.get(skuId1);
            }
            WhSkuCommand whSkuCmd = whSkuDao.findWhSkuByIdExt(SkuCategoryProvider.getSkuId(skuAttrIdNoSn), ouId);
            if (null == whSkuCmd) {
                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
            }
            command.setTipSkuBarCode(whSkuCmd.getBarCode());
            command.setIsNeedTipSku(true);
            command.setSkuId(whSkuCmd.getId());
            this.tipSkuDetailAspect(command, skuAttrId, skuAttrIdsQty, logId);
            command.setIsNeedScanSkuSn(cSRCmd.getIsNeedScanSkuSn());
        } else if (cSRCmd.getIsNeedTipInsideContainer()) { // 提示下一个货箱
            command.setIsContinueScanSn(false);
            Long tipInsideContainerId = cSRCmd.getTipiInsideContainerId();
            Container ic = containerDao.findByIdExt(tipInsideContainerId, ouId);
            command.setTipInsideContainerCode(ic.getCode());
            command.setIsNeedTipInsideContainer(true);
            if (!cSRCmd.getIsHaveOuterContainer()) {
                command.setTipOuterContainerCode(null);
            }
            List<String> snList = null;
            if (null != insideContainerId) {// 有货箱
                snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + insideContainerId.toString() + skuId);
            } else {
                snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + locationId.toString() + skuId);
            }
            if (Constants.PICKING_INVENTORY.equals(operationWay)) { // 拣货(库位库存变成容器库存)
                if (Constants.PICKING_WAY_FIVE != pickingWay) {
                    // 添加作业执行明细
                    List<WhOperationExecLine> execLineList =
                            this.addPickingOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, operationWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                    operationId, ouId, skuCmd.getScanSkuQty(), latticeNo);
                    // 添加容器库存
                    whSkuInventoryManager.pickingAddContainerInventory(execLineList, snList, containerId, locationId, skuAttrIds, operationId, ouId, isTabbInvTotal, userId, pickingWay, command.getScanPattern(), skuCmd.getScanSkuQty(), turnoverBoxId,
                            outerContainerId, insideContainerId, isShortPikcing, null);
                }
            }
            if (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay)) { // 补货中的拣货(库位库存变成容器库存)
                if (Constants.REPLENISH_WAY_TWO != pickingWay) {
                    // 添加作业执行明细
                    List<WhOperationExecLine> execLineList =
                            this.addPickingOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, operationWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                    operationId, ouId, skuCmd.getScanSkuQty(), latticeNo);
                    // 已分配的库位库存转变为容器库存
                    whSkuInventoryManager.replenishmentContainerInventory(execLineList, isShortPikcing, snList, skuAttrIds, locationId, operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId, workCode,
                            skuCmd.getScanSkuQty());
                }
            }
            if (Constants.INVMOVE_PICKING_INVENTORY.equals(operationWay)) { // 库内移动中的拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addInvMoveOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty());
                // 已分配的库位库存转变为容器库存
                whSkuInventoryManager.invMoveContainerInventory(isShortPikcing, snList, skuAttrIds, locationId, operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId, workCode, skuCmd.getScanSkuQty());
            }
        } else if (cSRCmd.getIsNeedTipOutContainer()) { // 提示下一个外部容器
            command.setIsContinueScanSn(false);
            Long outerId = cSRCmd.getTipOuterContainerId();
            // 判断外部容器
            Container c = containerDao.findByIdExt(outerId, ouId);
            // 提示外部容器编码
            command.setTipOuterContainerCode(c.getCode());
            command.setIsNeedTipOutContainer(true);
            List<String> snList = null;
            if (null != insideContainerId) {// 有货箱
                snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + insideContainerId.toString() + skuId);
            } else {
                snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + locationId.toString() + skuId);
            }
            if (Constants.PICKING_INVENTORY.equals(operationWay)) { // 拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addPickingOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, operationWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty(), latticeNo);
                // 添加容器库存
                whSkuInventoryManager.pickingAddContainerInventory(execLineList, snList, containerId, locationId, skuAttrIds, operationId, ouId, isTabbInvTotal, userId, pickingWay, command.getScanPattern(), skuCmd.getScanSkuQty(), turnoverBoxId,
                        outerContainerId, insideContainerId, isShortPikcing, null);
            }
            if (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay)) { // 补货中的拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addPickingOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, operationWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty(), latticeNo);
                // 已分配的库位库存转变为容器库存
                whSkuInventoryManager.replenishmentContainerInventory(execLineList, isShortPikcing, snList, skuAttrIds, locationId, operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId, workCode,
                        skuCmd.getScanSkuQty());
            }
            if (Constants.INVMOVE_PICKING_INVENTORY.equals(operationWay)) { // 库内移动中的拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addInvMoveOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty());
                // 已分配的库位库存转变为容器库存
                whSkuInventoryManager.invMoveContainerInventory(isShortPikcing, snList, skuAttrIds, locationId, operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId, workCode, skuCmd.getScanSkuQty());
            }
        } else if (cSRCmd.getIsNeedTipLoc()) { // 提示下一个库位
            command.setIsContinueScanSn(false);
            List<String> snList = null;
            if (null != insideContainerId) {// 有货箱
                snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + insideContainerId.toString() + skuId);
            } else {
                snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + locationId.toString() + skuId);
            }
            if (Constants.PICKING_INVENTORY.equals(operationWay)) { // 拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addPickingOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, operationWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty(), latticeNo);
                // 添加容器库存
                whSkuInventoryManager.pickingAddContainerInventory(execLineList, snList, containerId, locationId, skuAttrIds, operationId, ouId, isTabbInvTotal, userId, pickingWay, command.getScanPattern(), skuCmd.getScanSkuQty(), turnoverBoxId,
                        outerContainerId, insideContainerId, isShortPikcing, null);
            }
            if (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay)) { // 补货中的拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addPickingOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, operationWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty(), latticeNo);
                // 已分配的库位库存转变为容器库存
                whSkuInventoryManager.replenishmentContainerInventory(execLineList, isShortPikcing, snList, skuAttrIds, locationId, operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId, workCode,
                        skuCmd.getScanSkuQty());
            }
            if (Constants.INVMOVE_PICKING_INVENTORY.equals(operationWay)) { // 库内移动中的拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addInvMoveOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty());
                // 已分配的库位库存转变为容器库存
                whSkuInventoryManager.invMoveContainerInventory(isShortPikcing, snList, skuAttrIds, locationId, operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId, workCode, skuCmd.getScanSkuQty());
            }
            // 提示下一个库位之前，缓存上一个库存
            this.cacheLocation(operationId, locationId);
            Long locId = cSRCmd.getTipLocationId();
            Location location = whLocationDao.findByIdExt(locId, ouId);
            if (null == location) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
            }
            command.setTipLocationBarCode(location.getBarCode());
            command.setTipLocationCode(location.getCode());
            command.setIsNeedTipLocation(true);
            // 清除缓存
            pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(operationId, false, locationId);
        } else if (cSRCmd.getIsPicking()) {
            // 修改小车状态
            if (Constants.PICKING_INVENTORY.equals(operationWay) && (pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO)) {
                this.updateContainer(outerContainer, ouId);
            } else if (Constants.PICKING_INVENTORY.equals(operationWay) && pickingWay == Constants.PICKING_WAY_FOUR) {
                this.updateContainer(turnoverBoxCode, ouId);
            }
            command.setIsPicking(true);
            Location location = whLocationDao.findByIdExt(locationId, ouId);
            Long workAreaId = location.getWorkAreaId();
            command.setWorkAreaId(workAreaId);
            WhWorkCommand work = workManager.findWorkByWorkCode(command.getWorkBarCode(), ouId);
            if (null == work) {
                throw new BusinessException("no work found");
            }
            command.setBatch(work.getBatch());
            List<String> snList = null;
            if (null != insideContainerId) {// 有货箱
                snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + insideContainerId.toString() + skuId);
            } else {
                snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + locationId.toString() + skuId);
            }
            if (Constants.PICKING_INVENTORY.equals(operationWay)) { // 拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addPickingOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, operationWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty(), latticeNo);
                // 添加容器库存
                whSkuInventoryManager.pickingAddContainerInventory(execLineList, snList, containerId, locationId, skuAttrIds, operationId, ouId, isTabbInvTotal, userId, pickingWay, command.getScanPattern(), skuCmd.getScanSkuQty(), turnoverBoxId,
                        outerContainerId, insideContainerId, isShortPikcing, null);
                //判断当前作业是否拣货完成
                this.judgeOperationIsEnd(operationId,ouId);
                // 判断是拣完在播，是否是最后一箱
                List<WhWorkCommand> list = workManager.findWorkByBatch(work.getBatch(), ouId);
                int count = 0;
                for (WhWorkCommand cmd : list) {
                    if (!(WorkStatus.FINISH.equals(cmd.getStatus()) || WorkStatus.PARTLY_FINISH.equals(cmd.getStatus()))) {
                        count++;
                    }
                }
                if (count == 1) {// 当前工作是最后一个
                    command.setIsLastWork(true); // 当前工作是一个小批次下的最后一个工作
                }
                long startTime = System.currentTimeMillis(); // 获取开始时间
                log.info("collection run start time:" + startTime);
                // 插入集货表
                // TODO
                String pickingMode = this.insertIntoCollection(command, ouId, userId);
                long endTime = System.currentTimeMillis(); // 获取结束时间
                log.info("collection run end time:" + endTime);
                log.info("collection run  time:" + (endTime - startTime));
                command.setPickingMode(pickingMode);

                // 更新工作及作业状态
                pdaPickingWorkCacheManager.pdaPickingUpdateStatus(operationId, workCode, ouId, userId);
                // 清除缓存
                pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(operationId, true, locationId);
                // 更改出库单状态
                List<WhOperationLineCommand> whOperationLineCommandLst = whOperationLineDao.findOperationLineByOperationId(operationId, ouId);
                for (WhOperationLineCommand whOperationLineCommand : whOperationLineCommandLst) {
                    List<WhOdoOutBoundBoxCommand> odoOutBoundBoxByOdo = whOdoOutBoundBoxDao.gethOdoOutBoundBoxLstByOdo(whOperationLineCommand.getOdoId(), null, false, whOperationLineCommand.getOuId());
                    List<WhOperationCommand> operationCommandByOdo = whOperationDao.findOperationCommandByOdo(whOperationLineCommand.getOdoId(), null, 10, whOperationLineCommand.getOuId());
                    List<WhOdoOutBoundBoxCommand> odoOutBoundBoxByOdoLine = whOdoOutBoundBoxDao.gethOdoOutBoundBoxLstByOdo(null, whOperationLineCommand.getOdoLineId(), false, whOperationLineCommand.getOuId());
                    List<WhOperationCommand> operationCommandByOdoLine = whOperationDao.findOperationCommandByOdo(null, whOperationLineCommand.getOdoLineId(), 10, whOperationLineCommand.getOuId());
                    if (0 == odoOutBoundBoxByOdo.size() && 0 == operationCommandByOdo.size()) {
                        // 根据出库单code获取出库单信息
                        WhOdo odo = odoDao.findByIdOuId(whOperationLineCommand.getOdoId(), whOperationLineCommand.getOuId());
                        if (Constants.WH_PICKING_MODE.equals(pickingMode)) {
                            odo.setOdoStatus(OdoStatus.PICKING_FINISH);
                            odo.setLagOdoStatus(OdoStatus.PICKING_FINISH);
                        } else {
                            odo.setOdoStatus(OdoStatus.COLLECTION_FINISH);
                            odo.setLagOdoStatus(OdoStatus.COLLECTION_FINISH);
                        }
                        odoDao.update(odo);
                    }
                    if (0 == odoOutBoundBoxByOdoLine.size() && 0 == operationCommandByOdoLine.size()) {
                        // 根据出库单code获取出库单信息
                        WhOdoLine whOdoLine = odoLineManager.findOdoLineById(whOperationLineCommand.getOdoLineId(), whOperationLineCommand.getOuId());
                        whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_PUTAWAY_FINISH);
                        whOdoLineDao.update(whOdoLine);
                    }
                }
            }
            if (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay)) { // 补货中的拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addPickingOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, operationWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty(), latticeNo);
                // 已分配的库位库存转变为容器库存
                whSkuInventoryManager.replenishmentContainerInventory(execLineList, isShortPikcing, snList, skuAttrIds, locationId, operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId, workCode,
                        skuCmd.getScanSkuQty());
                // 更新工作及作业状态
                pdaPickingWorkCacheManager.pdaReplenishmentUpdateOperation(operationId, ouId, userId);
                // 清除缓存
                pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(operationId, true, locationId);
            }
            if (Constants.INVMOVE_PICKING_INVENTORY.equals(operationWay)) { // 库内移动中的拣货(库位库存变成容器库存)
                // 添加作业执行明细
                List<WhOperationExecLine> execLineList =
                        this.addInvMoveOperationExecLine(isShortPickingEnd, command.getScanPattern(), pickingWay, skuAttrIds, locationId, isShortPikcing, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId,
                                operationId, ouId, skuCmd.getScanSkuQty());
                // 已分配的库位库存转变为容器库存
                whSkuInventoryManager.invMoveContainerInventory(isShortPikcing, snList, skuAttrIds, locationId, operationId, ouId, outerContainerId, insideContainerId, turnoverBoxId, isTabbInvTotal, userId, workCode, skuCmd.getScanSkuQty());
                // 更新工作及作业状态
                pdaPickingWorkCacheManager.pdaReplenishmentUpdateOperation(operationId, ouId, userId);
                // 清除缓存
                pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(operationId, true, locationId);
            }
        }
        log.info("PdaPickingWorkManagerImpl scanSku is end");
        return command;
    }

    /**
     * 判断当前作业是否拣货完成
     * @param latticeNos
     * @param operationId
     */
    private void judgeOperationIsEnd(Long operationId,Long ouId){
        int count = whOperationLineDao.findOperationLineByLattice(ouId,operationId);
        if(count > 0){
            throw new BusinessException(ErrorCodes.PICKING__NO_END);
        }
    }

    private void updateContainer(String containerCode, Long ouId) {
        Container container = new Container();
        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
        if (null == containerCmd) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        BeanUtils.copyProperties(containerCmd, container);
        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
        container.setStatus(ContainerStatus.CONTAINER_STATUS_PICKING_END);
        containerDao.saveOrUpdateByVersion(container);
    }



    /***
     * 整箱缓存短拣的sku
     * 
     * @param skuAttrIds(唯一sku)
     * @param operationId
     */
    private void cacheContainerShortPickingSkuAttrIds(String skuAttrIds, Long operationId, Long insideContainerId, Double scanSkuQty) {
        // 整箱
        Map<String, Double> map = cacheManager.getMapObject(CacheConstants.PDA_PICKING_SHORTPICKING_SKU, operationId.toString() + insideContainerId.toString());
        if (null == map || map.size() == 0) {
            map = new HashMap<String, Double>();
            map.put(skuAttrIds, scanSkuQty);
        } else {
            Double qty = map.get(skuAttrIds);
            if (null == qty) {
                map.put(skuAttrIds, qty);
            } else {
                Double sum = qty + scanSkuQty;
                map.put(skuAttrIds, sum);
            }
        }
        // 整箱
        cacheManager.setMapObject(CacheConstants.PDA_PICKING_SHORTPICKING_SKU, operationId.toString() + insideContainerId.toString(), map, CacheConstants.CACHE_ONE_DAY);
    }

    private void cachePalletShortPickingSkuAttrIds(String skuAttrIds, Long operationId, Long insideContainerId, Long outerContainerId, Double scanSkuQty) {
        Map<Long, Map<String, Double>> map = cacheManager.getMapObject(CacheConstants.PDA_PICKING_SHORTPICKING_SKU, operationId.toString() + outerContainerId.toString());
        if (null == map || map.size() == 0) {
            map = new HashMap<Long, Map<String, Double>>();
            Map<String, Double> skuAttrIdsMap = new HashMap<String, Double>();
            skuAttrIdsMap.put(skuAttrIds, scanSkuQty);
            map.put(insideContainerId, skuAttrIdsMap);
        } else {
            Map<String, Double> skuAttrIdsMap = map.get(insideContainerId);
            if (null == skuAttrIdsMap || skuAttrIdsMap.size() == 0) {
                skuAttrIdsMap = new HashMap<String, Double>();
                skuAttrIdsMap.put(skuAttrIds, scanSkuQty);
            } else {
                Double qty = skuAttrIdsMap.get(skuAttrIds);
                if (null == qty) {
                    skuAttrIdsMap.put(skuAttrIds, qty);
                } else {
                    Double sum = qty + scanSkuQty;
                    skuAttrIdsMap.put(skuAttrIds, sum);
                }
            }
        }

        cacheManager.setMapObject(CacheConstants.PDA_PICKING_SHORTPICKING_SKU, operationId.toString() + outerContainerId.toString(), map, CacheConstants.CACHE_ONE_DAY);
    }

    private void updateSnDefectOccupation(Long skuId, String skuAttrIds, String sn, String defect, Long locationId, Long ouId, Long operationId, Long outerContainerId, Long insideContainerId, String operationWay) {
        if (StringUtils.isEmpty(sn)) {
            sn = defect;
        }
        List<WhSkuInventoryCommand> skuInvList = null;
        if (Constants.PICKING_INVENTORY.equals(operationWay)) { // 拣货
            skuInvList = whSkuInventoryDao.getWhSkuInventoryCmdByOccupationLineId(locationId, ouId, operationId, outerContainerId, insideContainerId);
        }
        if (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay)) {// 补货
            skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOperationId(ouId, operationId, locationId, outerContainerId, insideContainerId);
        }
        if (Constants.INVMOVE_PICKING_INVENTORY.equals(operationWay)) {// 库内移动
            skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByInvMove(ouId, operationId, locationId, outerContainerId, insideContainerId);
        }
        if (null == skuInvList || skuInvList.size() == 0) {
            throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);
        }
        for (WhSkuInventoryCommand skuCmd : skuInvList) {
            String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(skuCmd);
            if (skuAttrIds.equals(skuAttrId)) { //
                String uuid = skuCmd.getUuid();
                WhSkuInventorySn skuInvSn = whSkuInventorySnDao.findWhSkuInventoryByUuidAndSnOrDefectWareBarcode(ouId, uuid, sn);
                if (null == skuInvSn) {
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_NULL);
                }
                String snDefect = SkuCategoryProvider.concatSkuAttrId(skuInvSn.getSn(), skuInvSn.getDefectWareBarcode()); // 拼接sn/残次信息
                // 缓存sn
                this.cacheSkuSn(locationId, insideContainerId, skuId, snDefect, operationId);
                break;
            }
        }
    }

    private void cacheSkuSn(Long locationId, Long insideContainerId, Long skuId, String snDefect, Long operationId) {
        List<String> snList = null;
        if (null != insideContainerId) {// 有货箱
            snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + insideContainerId.toString() + skuId);
        } else {
            snList = cacheManager.getObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + locationId.toString() + skuId);
        }
        if (null == snList) {
            snList = new ArrayList<String>();
            snList.add(snDefect);
        } else {
            if (!snList.contains(snDefect)) {
                snList.add(snDefect);
            } else {
                // 多条吗重复
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_DOUBLE_ERROR);
            }
        }
        if (null != insideContainerId) {// 有货箱
            cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + insideContainerId.toString() + skuId, snList, CacheConstants.CACHE_ONE_DAY);
        } else {
            cacheManager.setObject(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + locationId.toString() + skuId, snList, CacheConstants.CACHE_ONE_DAY);
        }
    }

    /**
     * [业务方法] 插入集货表
     * 
     * @param command
     * @param ouId
     */
    private String insertIntoCollection(PickingScanResultCommand command, Long ouId, Long userId) {
        WhWorkCommand work = workManager.findWorkByWorkCode(command.getWorkBarCode(), ouId);
        if (null == work) {
            throw new BusinessException("no work found");
        }
        command.setBatch(work.getBatch());
        List<WhOperationExecLineCommand> execLineCommandList = this.whOperationExecLineDao.findCommandByWorkId(work.getId(), ouId);
        if (Constants.WH_PICKING_MODE.equals(work.getPickingMode())) {
            // 拣货模式为播种
            this.pdaConcentrationManager.insertIntoSeedingCollection(command.getBatch(), work.getId(), execLineCommandList, ouId);
            // this.pdaConcentrationManager.insertIntoSeedingCollectionLine(command.getBatch(),
            // work.getId(), ouId);
        } else {
            // 拣货模式为非播种
            this.pdaConcentrationManager.insertIntoCheckingCollection(command.getBatch(), execLineCommandList, ouId, work, userId);
            checkingModeCalcManager.generateCheckingDataByCollection(work, execLineCommandList, userId, ouId, logId);
        }
        return work.getPickingMode();
    }

    /***
     * 判断容器状态是否正确
     * 
     * @author tangming
     * @param c
     */
    private void judeContainerStatus(Container c) {
        log.info("PdaPickingWorkManagerImpl judeContainerStatus is start");
        if (null == c) {
            // 容器信息不存在
            log.error("pdaScanContainer container is null logid: " + logId);
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        // 验证容器Lifecycle是否有效
        if (!c.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            // 容器Lifecycle无效
            log.error("pdaScanContainer container lifecycle error =" + c.getLifecycle() + " logid: " + logId);
            throw new BusinessException(ErrorCodes.OUT_BOUNDBOX_IS_NOT_NORMAL);
        }
        // 验证容器状态是否是待上架
        if (!(c.getStatus().equals(ContainerStatus.CONTAINER_STATUS_REC_OUTBOUNDBOX) || c.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PICKING))) {
            log.error("pdaScanContainer container status error =" + c.getStatus() + " logid: " + logId);
            throw new BusinessException(ErrorCodes.OUT_BOUNDBOX_IS_NOT_NORMAL, new Object[] {c.getStatus()});
        }
        log.info("PdaPickingWorkManagerImpl judeContainerStatus is end");
    }

    /****
     * 添加作业执行明细
     * 
     * @author tangming
     * @param outBoundxBoxCode(出库箱)
     * @param turnoverBoxCode(周转箱)
     * @param outerContainerCode(外部容器,托盘)
     * @param insideContainerCode(货箱号)
     */
    private List<WhOperationExecLine> addPickingOperationExecLine(Boolean isShortPickingEnd, Integer scanPattern, Integer pickingWay, String operationWay, String skuAttrId, Long locationId, Boolean isShortPicking, Long userId, Long outBoundBoxId, String outBoundBoxCode,
            Long turnoverBoxId, Long outerContainerId, Long insideContainerId, Long operationId, Long ouId, Double qty, Integer latticeNo) {
        List<WhOperationExecLine> list = new ArrayList<WhOperationExecLine>();
        log.info("PdaPickingWorkManagerImpl addPickingOperationExecLine is start");
        List<WhOperationLineCommand> operLineList = whOperationLineDao.findOperationLineByOperationId(operationId, ouId);
        if ((Constants.PICKING_INVENTORY.equals(operationWay) && Constants.PICKING_WAY_FIVE == pickingWay) || (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay) && Constants.REPLENISH_WAY_TWO  == pickingWay)) {
            list = this.palletSaveExecLine(operationId, insideContainerId, ouId, operLineList, userId, locationId, outerContainerId);
        } else if ((Constants.PICKING_INVENTORY.equals(operationWay) && Constants.PICKING_WAY_SIX == pickingWay) || (Constants.REPLENISHMENT_PICKING_INVENTORY.equals(operationWay) && Constants.REPLENISH_WAY_THREE  == pickingWay)) {
            list = this.containerSaveExecLine(operationId, insideContainerId, ouId, operLineList, userId, locationId);
        } else {
            list =
                    this.splitContainerSaveExecLine(latticeNo, isShortPickingEnd, scanPattern, pickingWay, skuAttrId, locationId, isShortPicking, userId, outBoundBoxId, outBoundBoxCode, turnoverBoxId, outerContainerId, insideContainerId, operationId,
                            ouId, qty, operLineList);
        }
        List<WhOperationExecLine> execlineList = whOperationExecLineDao.checkOperationExecLine(ouId, operationId);
        List<WhOperationExecLine> lineList = whOperationExecLineDao.checkOperationLine(ouId, operationId); // 添加作
        if (execlineList.size() != lineList.size()) {
            throw new BusinessException(ErrorCodes.CHECK_OPERTAION_EXEC_LINE_DIFF);
        }
        Boolean result = false;
        for (WhOperationExecLine execLine : execlineList) {
            String execLineAttr = "┊" + execLine.getFromOuterContainerId() + execLine.getFromInsideContainerId() + execLine.getQty() + execLine.getFromLocationId() + "┊";
            for (WhOperationExecLine line : lineList) {
                String lineAttr = "┊" + line.getFromOuterContainerId() + line.getFromInsideContainerId() + line.getQty() + line.getFromLocationId() + "┊";
                if (execLineAttr.equals(lineAttr)) {
                    result = true;
                    break;
                } else {
                    result = false;
                }
            }
        }

        for (WhOperationExecLine line : lineList) {
            String lineAttr = "┊" + line.getFromOuterContainerId() + line.getFromInsideContainerId() + line.getQty() + line.getFromLocationId() + "┊";
            for (WhOperationExecLine execLine : execlineList) {
                String execLineAttr = "┊" + execLine.getFromOuterContainerId() + execLine.getFromInsideContainerId() + execLine.getQty() + execLine.getFromLocationId() + "┊";
                if (lineAttr.equals(execLineAttr)) {
                    result = true;
                    break;
                } else {
                    result = false;
                }
            }
        }
        if (!result) {
            throw new BusinessException(ErrorCodes.CHECK_OPERTAION_EXEC_LINE_DIFF);
        }
        log.info("PdaPickingWorkManagerImpl addPickingOperationExecLine is end");

        return list;
    }


    private List<WhOperationExecLine> splitContainerSaveExecLine(Integer latticeNo, Boolean isShortPickingEnd, Integer scanPattern, Integer pickingWay, String skuAttrId, Long locationId, Boolean isShortPicking, Long userId, Long outBoundBoxId,
            String outBoundBoxCode, Long turnoverBoxId, Long outerContainerId, Long insideContainerId, Long operationId, Long ouId, Double qty, List<WhOperationLineCommand> operLineList) {
        List<WhOperationExecLine> list = new ArrayList<WhOperationExecLine>();
        String ioIds = (outerContainerId == null ? "┊" : outerContainerId + "┊") + (insideContainerId == null ? "︴" : insideContainerId + "︴");
        Double sum = 0.0;
        for (WhOperationLineCommand oLCmd : operLineList) {
            Long operationLineId = null; // 获取当前作业明细id
            // 非整托整箱
            if (oLCmd.getCompleteQty().doubleValue() == oLCmd.getQty().doubleValue()) {
                continue;
            }
            if (pickingWay == Constants.PICKING_WAY_THREE && (pickingWay == Constants.PICKING_WAY_ONE || pickingWay == Constants.PICKING_WAY_TWO)) {// 出库箱流程
                if (!latticeNo.equals(oLCmd.getUseContainerLatticeNo())) {
                    continue;
                }
            }
            String lineioIds = (oLCmd.getFromOuterContainerId() == null ? "┊" : oLCmd.getFromOuterContainerId() + "┊") + (oLCmd.getFromInsideContainerId() == null ? "︴" : oLCmd.getFromInsideContainerId() + "︴");
            String opLskuAttrId = SkuCategoryProvider.getSkuAttrIdByOperationLine(oLCmd);
            if (skuAttrId.equals(opLskuAttrId) && locationId.longValue() == oLCmd.getFromLocationId().longValue() && ioIds.equals(lineioIds)) {
                operationLineId = oLCmd.getId(); // 获取当前作业明细id
                WhOperationExecLine whOperationExecLine = this.getWhOperationExecLine(userId, outBoundBoxCode, turnoverBoxId, outBoundBoxId, operationId, ouId, operationLineId, outerContainerId, insideContainerId);
                whOperationExecLine.setIsUseNew(false);
                if (isShortPicking) {// 短拣商品
                    whOperationExecLine.setIsShortPicking(true);
                }
                if (pickingWay == Constants.PICKING_WAY_THREE) {// 出库箱流程
                    OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
                    if (null == operatorLine) {
                        throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                    }
                    Set<String> outbounxBoxs = operatorLine.getOutbounxBoxs();
                    if (!outbounxBoxs.contains(outBoundBoxCode)) {
                        whOperationExecLine.setIsUseNew(true);
                        whOperationExecLine.setOldOutboundboxCode(whOperationExecLine.getUseOutboundboxCode());
                        whOperationExecLine.setUseOutboundboxCode(outBoundBoxCode);
                    }

                }
                if (pickingWay == Constants.PICKING_WAY_FOUR) {// 周转箱流程
                    OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
                    if (null == operatorLine) {
                        throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                    }
                    Set<Long> turnoverBoxs = operatorLine.getTurnoverBoxs();
                    if (!turnoverBoxs.contains(turnoverBoxId)) {
                        whOperationExecLine.setIsUseNew(true);
                        whOperationExecLine.setOldContainerId(whOperationExecLine.getOldContainerId());
                        whOperationExecLine.setUseContainerId(turnoverBoxId);
                    }
                }
                // 先修改作业执行明细的执行量
                if (null != operationLineId) {
                    WhOperationLine line = new WhOperationLine();
                    BeanUtils.copyProperties(oLCmd, line);
                    if (qty.doubleValue() > oLCmd.getQty().doubleValue()) {// 扫描的数量大于计划量
                        sum += oLCmd.getQty();
                        Double subtract = qty.doubleValue() - sum;
                        if (subtract.doubleValue() > 0) {
                            // line.setCompleteQty(oLCmd.getQty());
                            whOperationExecLine.setQty(oLCmd.getQty());
                            whOperationExecLineDao.insert(whOperationExecLine);
                            insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                            list.add(whOperationExecLine);
                            // 修改作业明细表
                            line.setCompleteQty(oLCmd.getQty());
                            whOperationLineDao.saveOrUpdateByVersion(line);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, line, ouId, userId, null, null);
                            continue;
                        }
                        if (subtract.doubleValue() == 0) {
                            line.setCompleteQty(oLCmd.getQty());
                            whOperationExecLine.setQty(oLCmd.getQty());
                            whOperationExecLineDao.insert(whOperationExecLine);
                            insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                            list.add(whOperationExecLine);
                        }
                        if (subtract.doubleValue() < 0) {
                            line.setCompleteQty(sum - qty.doubleValue());
                            whOperationExecLine.setQty(qty.doubleValue() - (sum - oLCmd.getQty())); // Bug
                            whOperationExecLineDao.insert(whOperationExecLine);
                            insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                            list.add(whOperationExecLine);
                        }

                    } else if (qty.doubleValue() < oLCmd.getQty().doubleValue()) {
                        whOperationExecLine.setQty(qty);
                        whOperationExecLineDao.insert(whOperationExecLine);
                        insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                        list.add(whOperationExecLine);
                    } else {
                        whOperationExecLine.setQty(qty);
                        whOperationExecLineDao.insert(whOperationExecLine);
                        insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                        list.add(whOperationExecLine);
                    }
                    // 修改作业明细
                    if (qty.doubleValue() < oLCmd.getQty().doubleValue()) {
                        Double sumQty = qty + oLCmd.getCompleteQty();
                        line.setCompleteQty(sumQty);
                    }
                    if (qty.doubleValue() == oLCmd.getQty().doubleValue()) {
                        line.setCompleteQty(qty);
                    }
                    whOperationLineDao.saveOrUpdateByVersion(line);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, line, ouId, userId, null, null);
                }
                break;
            }
            if (isShortPickingEnd) { // 拣货完成
                operationLineId = oLCmd.getId(); // 获取当前作业明细id
                WhOperationExecLine whOperationExecLine = this.getWhOperationExecLine(userId, outBoundBoxCode, turnoverBoxId, outBoundBoxId, operationId, ouId, operationLineId, outerContainerId, insideContainerId);
                whOperationExecLine.setQty(qty);
                whOperationExecLine.setIsShortPicking(true);
                whOperationExecLineDao.insert(whOperationExecLine);
                insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
            }
        }
        return list;
    }

    /***
     * 整托添加作业执行明细
     * 
     * @param operationId
     * @param insideContainerId
     * @param ouId
     * @param operLineList
     * @param userId
     * @param locationId
     * @param outerContainerId
     * @return
     */
    private List<WhOperationExecLine> palletSaveExecLine(Long operationId, Long insideContainerId, Long ouId, List<WhOperationLineCommand> operLineList, Long userId, Long locationId, Long outerContainerId) {
        Double sum = 0.0;
        Boolean isUpdateShort = true;
        List<WhOperationExecLine> list = new ArrayList<WhOperationExecLine>();
        for (WhOperationLineCommand oLCmd : operLineList) {
            Map<Long, Map<String, Double>> map = cacheManager.getMapObject(CacheConstants.PDA_PICKING_SHORTPICKING_SKU, operationId.toString() + outerContainerId.toString());
            // 整托
            if (locationId.longValue() == oLCmd.getFromLocationId().longValue() && outerContainerId.equals(oLCmd.getFromOuterContainerId())) {
                Long operationLineId = oLCmd.getId(); // 获取当前作业明细id
                WhOperationExecLine whOperationExecLine = this.getWhOperationExecLine(userId, null, null, null, operationId, ouId, operationLineId, oLCmd.getFromOuterContainerId(), oLCmd.getFromInsideContainerId());
                whOperationExecLine.setUseOuterContainerId(whOperationExecLine.getFromOuterContainerId());
                whOperationExecLine.setUseContainerId(whOperationExecLine.getFromInsideContainerId());
                // 先更新作业明细行
                WhOperationLine line = new WhOperationLine();
                BeanUtils.copyProperties(oLCmd, line);
                line.setCompleteQty(oLCmd.getQty());
                whOperationLineDao.saveOrUpdateByVersion(line);
                insertGlobalLog(GLOBAL_LOG_UPDATE, line, ouId, userId, null, null);
                if (null == map || map.size() == 0) { // 没有短拣sku
                    whOperationExecLine.setQty(oLCmd.getQty());
                    whOperationExecLineDao.insert(whOperationExecLine);
                    insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                    list.add(whOperationExecLine);
                } else {// 当前货箱存在短拣sku
                    String opLskuAttrId = SkuCategoryProvider.getSkuAttrIdByOperationLine(oLCmd); // 当前作业明细唯一sku
                    Map<String, Double> insideMap = map.get(oLCmd.getFromInsideContainerId());
                    if (null != insideMap) {
                        Set<String> skuAttrIds = insideMap.keySet(); // 当前货箱所有短拣的唯一库存属性
                        if (isUpdateShort) {
                            for (String insideSkuAttrId : skuAttrIds) {
                                if (opLskuAttrId.equals(insideSkuAttrId)) { // 短拣当前sku
                                    // 取出短拣数量
                                    Double shortQty = insideMap.get(insideSkuAttrId); // 短拣数量
                                    if (shortQty.doubleValue() == oLCmd.getQty().doubleValue()) { //
                                        whOperationExecLine.setQty(oLCmd.getQty());
                                        whOperationExecLine.setIsShortPicking(true);
                                        whOperationExecLineDao.insert(whOperationExecLine);
                                        insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                                        list.add(whOperationExecLine);
                                        isUpdateShort = false;
                                    }
                                    if (shortQty.doubleValue() < oLCmd.getQty().doubleValue()) { // 短拣数量小于当前行数量
                                        // 先插入短拣数据
                                        whOperationExecLine.setQty(shortQty);
                                        whOperationExecLine.setIsShortPicking(true);
                                        whOperationExecLineDao.insert(whOperationExecLine);
                                        insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                                        list.add(whOperationExecLine);
                                        // 插入非短拣数据
                                        WhOperationExecLine execLine = new WhOperationExecLine();
                                        BeanUtils.copyProperties(whOperationExecLine, execLine);
                                        execLine.setId(null);
                                        execLine.setQty(oLCmd.getQty() - shortQty);
                                        execLine.setIsShortPicking(false);
                                        whOperationExecLineDao.insert(execLine);
                                        insertGlobalLog(GLOBAL_LOG_INSERT, execLine, ouId, userId, null, null);
                                        list.add(execLine);
                                        isUpdateShort = false;

                                    }
                                    if (shortQty.doubleValue() > oLCmd.getQty().doubleValue()) { // 短拣的是多行数据
                                        sum += oLCmd.getQty();
                                        if (shortQty.doubleValue() == sum.doubleValue()) {
                                            whOperationExecLine.setQty(oLCmd.getQty());
                                            whOperationExecLine.setIsShortPicking(true);
                                            whOperationExecLineDao.insert(whOperationExecLine);
                                            insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                                            list.add(whOperationExecLine);
                                            isUpdateShort = false;
                                        }
                                        if (shortQty.doubleValue() < sum.doubleValue()) {
                                            Double qty1 = shortQty - (sum - oLCmd.getQty());
                                            // 先插入短拣数据
                                            whOperationExecLine.setQty(shortQty);
                                            whOperationExecLine.setIsShortPicking(true);
                                            whOperationExecLineDao.insert(whOperationExecLine);
                                            insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                                            list.add(whOperationExecLine);
                                            // 插入非短拣数据
                                            WhOperationExecLine execLine = new WhOperationExecLine();
                                            BeanUtils.copyProperties(whOperationExecLine, execLine);
                                            execLine.setId(null);
                                            execLine.setQty(qty1);
                                            execLine.setIsShortPicking(false);
                                            whOperationExecLineDao.insert(execLine);
                                            insertGlobalLog(GLOBAL_LOG_INSERT, execLine, ouId, userId, null, null);
                                            list.add(execLine);
                                            isUpdateShort = false;

                                        }
                                        if (shortQty.doubleValue() > sum.doubleValue()) {
                                            whOperationExecLine.setQty(oLCmd.getQty());
                                            whOperationExecLine.setIsShortPicking(true);
                                            whOperationExecLineDao.insert(whOperationExecLine);
                                            insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                                            list.add(whOperationExecLine);
                                            continue;
                                        }
                                    }
                                }
                                if (!isUpdateShort) {
                                    break;
                                }
                            }
                        } else {
                            // 更新非短拣
                            whOperationExecLine.setQty(oLCmd.getQty());
                            whOperationExecLineDao.insert(whOperationExecLine);
                            insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                        }
                    }
                }
            }
        }
        return list;
    }

    /***
     * 整箱保存作业执行明细
     * 
     * @param operationId
     * @param insideContainerId
     * @param ouId
     * @param operLineList
     * @param userId
     * @param locationId
     */
    private List<WhOperationExecLine> containerSaveExecLine(Long operationId, Long insideContainerId, Long ouId, List<WhOperationLineCommand> operLineList, Long userId, Long locationId) {
        Double sum = 0.0;
        Boolean isUpdateShort = true;
        List<WhOperationExecLine> list = new ArrayList<WhOperationExecLine>();
        for (WhOperationLineCommand oLCmd : operLineList) {
            Map<String, Double> map = cacheManager.getMapObject(CacheConstants.PDA_PICKING_SHORTPICKING_SKU, operationId.toString() + insideContainerId.toString());
            // 整箱
            if (locationId.longValue() == oLCmd.getFromLocationId().longValue() && insideContainerId.equals(oLCmd.getFromInsideContainerId())) {
                Long operationLineId = oLCmd.getId(); // 获取当前作业明细id
                WhOperationExecLine whOperationExecLine = this.getWhOperationExecLine(userId, null, null, null, operationId, ouId, operationLineId, null, insideContainerId);
                whOperationExecLine.setUseOuterContainerId(whOperationExecLine.getFromOuterContainerId());
                whOperationExecLine.setUseContainerId(whOperationExecLine.getFromInsideContainerId());
                // 先更新作业明细行
                WhOperationLine line = new WhOperationLine();
                BeanUtils.copyProperties(oLCmd, line);
                line.setCompleteQty(oLCmd.getQty());
                whOperationLineDao.saveOrUpdateByVersion(line);
                insertGlobalLog(GLOBAL_LOG_UPDATE, line, ouId, userId, null, null);
                if (null == map || map.size() == 0) { // 没有短拣sku
                    whOperationExecLine.setQty(oLCmd.getQty());
                    whOperationExecLineDao.insert(whOperationExecLine);
                    insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                    list.add(whOperationExecLine);
                } else {// 当前货箱存在短拣sku
                    String opLskuAttrId = SkuCategoryProvider.getSkuAttrIdByOperationLine(oLCmd); // 当前作业明细唯一sku
                    Set<String> skuAttrIds = map.keySet(); // 当前货箱所有短拣的唯一库存属性
                    if (isUpdateShort) {
                        for (String insideSkuAttrId : skuAttrIds) {
                            if (opLskuAttrId.equals(insideSkuAttrId)) { // 短拣当前sku
                                // 取出短拣数量
                                Double shortQty = map.get(insideSkuAttrId); // 短拣数量
                                if (shortQty.doubleValue() == oLCmd.getQty().doubleValue()) { //
                                    whOperationExecLine.setQty(oLCmd.getQty());
                                    whOperationExecLine.setIsShortPicking(true);
                                    whOperationExecLineDao.insert(whOperationExecLine);
                                    insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                                    list.add(whOperationExecLine);
                                    isUpdateShort = false;
                                }
                                if (shortQty.doubleValue() < oLCmd.getQty().doubleValue()) { // 短拣数量小于当前行数量
                                    // 先插入短拣数据
                                    whOperationExecLine.setQty(shortQty);
                                    whOperationExecLine.setIsShortPicking(true);
                                    whOperationExecLineDao.insert(whOperationExecLine);
                                    insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                                    list.add(whOperationExecLine);
                                    // 插入非短拣数据
                                    WhOperationExecLine execLine = new WhOperationExecLine();
                                    BeanUtils.copyProperties(whOperationExecLine, execLine);
                                    execLine.setId(null);
                                    execLine.setQty(oLCmd.getQty() - shortQty);
                                    execLine.setIsShortPicking(false);
                                    whOperationExecLineDao.insert(execLine);
                                    insertGlobalLog(GLOBAL_LOG_INSERT, execLine, ouId, userId, null, null);
                                    list.add(execLine);
                                    isUpdateShort = false;

                                }
                                if (shortQty.doubleValue() > oLCmd.getQty().doubleValue()) { // 短拣的是多行数据
                                    sum += oLCmd.getQty();
                                    if (shortQty.doubleValue() == sum.doubleValue()) {
                                        whOperationExecLine.setQty(oLCmd.getQty());
                                        whOperationExecLine.setIsShortPicking(true);
                                        whOperationExecLineDao.insert(whOperationExecLine);
                                        insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                                        list.add(whOperationExecLine);
                                        isUpdateShort = false;
                                    }
                                    if (shortQty.doubleValue() < sum.doubleValue()) {
                                        Double qty1 = shortQty - (sum - oLCmd.getQty());
                                        // 先插入短拣数据
                                        whOperationExecLine.setQty(shortQty);
                                        whOperationExecLine.setIsShortPicking(true);
                                        whOperationExecLineDao.insert(whOperationExecLine);
                                        insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                                        list.add(whOperationExecLine);
                                        // 插入非短拣数据
                                        WhOperationExecLine execLine = new WhOperationExecLine();
                                        BeanUtils.copyProperties(whOperationExecLine, execLine);
                                        execLine.setId(null);
                                        execLine.setQty(qty1);
                                        execLine.setIsShortPicking(false);
                                        whOperationExecLineDao.insert(execLine);
                                        insertGlobalLog(GLOBAL_LOG_INSERT, execLine, ouId, userId, null, null);
                                        list.add(execLine);
                                        isUpdateShort = false;

                                    }
                                    if (shortQty.doubleValue() > sum.doubleValue()) {
                                        whOperationExecLine.setQty(oLCmd.getQty());
                                        whOperationExecLine.setIsShortPicking(true);
                                        whOperationExecLineDao.insert(whOperationExecLine);
                                        insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                                        list.add(whOperationExecLine);
                                        continue;
                                    }
                                }
                            }
                            if (!isUpdateShort) {
                                break;
                            }
                        }
                    } else {
                        // 更新非短拣
                        whOperationExecLine.setQty(oLCmd.getQty());
                        whOperationExecLineDao.insert(whOperationExecLine);
                        insertGlobalLog(GLOBAL_LOG_INSERT, whOperationExecLine, ouId, userId, null, null);
                    }

                }
            }
        }
        return list;
    }

    /****
     * 添加作业执行明细
     * 
     * @author tangming
     * @param outBoundxBoxCode(出库箱)
     * @param turnoverBoxCode(周转箱)
     * @param outerContainerCode(外部容器,托盘)
     * @param insideContainerCode(货箱号)
     */
    private List<WhOperationExecLine> addInvMoveOperationExecLine(Boolean isShortPickingEnd, Integer scanPattern, Integer pickingWay, String skuAttrId, Long locationId, Boolean isShortPicking, Long userId, Long outBoundBoxId, String outBoundBoxCode,
            Long turnoverBoxId, Long outerContainerId, Long insideContainerId, Long operationId, Long ouId, Double qty) {
        List<WhOperationExecLine> list = new ArrayList<WhOperationExecLine>();
        log.info("PdaPickingWorkManagerImpl addPickingOperationExecLine is start");
        List<WhOperationLineCommand> operLineList = whOperationLineDao.findOperationLineByOperationId(operationId, ouId);
        String ioIds = (outerContainerId == null ? "┊" : outerContainerId + "┊") + (insideContainerId == null ? "︴" : insideContainerId + "︴");
        Double sum = 0.0;
        for (WhOperationLineCommand oLCmd : operLineList) {
            Long operationLineId = null; // 获取当前作业明细id
            // 整托
            if (pickingWay == Constants.PICKING_WAY_TWO) {
                if (locationId.longValue() == oLCmd.getFromLocationId().longValue() && outerContainerId.equals(oLCmd.getFromOuterContainerId())) {
                    operationLineId = oLCmd.getId(); // 获取当前作业明细id
                    WhOperationExecLine whOperationExecLine = this.getWhOperationExecLine(userId, outBoundBoxCode, turnoverBoxId, outBoundBoxId, operationId, ouId, operationLineId, outerContainerId, insideContainerId);
                    whOperationExecLine.setUseOuterContainerId(whOperationExecLine.getFromOuterContainerId());
                    whOperationExecLine.setUseContainerId(whOperationExecLine.getFromInsideContainerId());
                    whOperationExecLine.setQty(qty);
                    whOperationExecLine.setCompleteQty(oLCmd.getQty());
                    if (isShortPicking) {// 短拣商品
                        whOperationExecLine.setIsShortPicking(true);
                    }
                    whOperationExecLineDao.insert(whOperationExecLine);
                    WhOperationLine line = new WhOperationLine();
                    BeanUtils.copyProperties(oLCmd, line);
                    // 修改作业明细
                    if (qty.doubleValue() < oLCmd.getQty().doubleValue()) {
                        Double sumQty = qty + oLCmd.getCompleteQty();
                        line.setCompleteQty(sumQty);
                    }
                    if (qty.doubleValue() == oLCmd.getQty().doubleValue()) {
                        line.setCompleteQty(qty);
                    }
                    whOperationLineDao.saveOrUpdateByVersion(line);
                }
            } else if (pickingWay == Constants.PICKING_WAY_THREE) {
                // 整箱
                if (locationId.longValue() == oLCmd.getFromLocationId().longValue() && insideContainerId.equals(oLCmd.getFromInsideContainerId())) {
                    operationLineId = oLCmd.getId(); // 获取当前作业明细id
                    WhOperationExecLine whOperationExecLine = this.getWhOperationExecLine(userId, outBoundBoxCode, turnoverBoxId, outBoundBoxId, operationId, ouId, operationLineId, outerContainerId, insideContainerId);
                    whOperationExecLine.setUseOuterContainerId(whOperationExecLine.getFromOuterContainerId());
                    whOperationExecLine.setUseContainerId(whOperationExecLine.getFromInsideContainerId());
                    whOperationExecLine.setQty(qty);
                    whOperationExecLine.setCompleteQty(oLCmd.getQty());
                    if (isShortPicking) {// 短拣商品
                        whOperationExecLine.setIsShortPicking(true);
                    }
                    whOperationExecLineDao.insert(whOperationExecLine);
                    WhOperationLine line = new WhOperationLine();
                    BeanUtils.copyProperties(oLCmd, line);
                    // 修改作业明细
                    if (qty.doubleValue() < oLCmd.getQty().doubleValue()) {
                        Double sumQty = qty + oLCmd.getCompleteQty();
                        line.setCompleteQty(sumQty);
                    }
                    if (qty.doubleValue() == oLCmd.getQty().doubleValue()) {
                        line.setCompleteQty(qty);
                    }
                    whOperationLineDao.saveOrUpdateByVersion(line);
                }
            } else {
                // 非整托整箱
                if (oLCmd.getCompleteQty().doubleValue() == oLCmd.getQty().doubleValue()) {
                    continue;
                }
                String lineioIds = (oLCmd.getFromOuterContainerId() == null ? "┊" : oLCmd.getFromOuterContainerId() + "┊") + (oLCmd.getFromInsideContainerId() == null ? "︴" : oLCmd.getFromInsideContainerId() + "︴");
                String opLskuAttrId = SkuCategoryProvider.getSkuAttrIdByOperationLine(oLCmd);
                if (skuAttrId.equals(opLskuAttrId) && locationId.longValue() == oLCmd.getFromLocationId().longValue() && ioIds.equals(lineioIds)) {
                    operationLineId = oLCmd.getId(); // 获取当前作业明细id
                    WhOperationExecLine whOperationExecLine = this.getWhOperationExecLine(userId, outBoundBoxCode, turnoverBoxId, outBoundBoxId, operationId, ouId, operationLineId, outerContainerId, insideContainerId);
                    whOperationExecLine.setIsUseNew(false);
                    if (isShortPicking) {// 短拣商品
                        whOperationExecLine.setIsShortPicking(true);
                    }
                    if (pickingWay == Constants.PICKING_WAY_THREE) {// 出库箱流程
                        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
                        if (null == operatorLine) {
                            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                        }
                        Set<String> outbounxBoxs = operatorLine.getOutbounxBoxs();
                        if (!outbounxBoxs.contains(outBoundBoxCode)) {
                            whOperationExecLine.setIsUseNew(true);
                            whOperationExecLine.setOldOutboundboxCode(whOperationExecLine.getUseOutboundboxCode());
                            whOperationExecLine.setUseOutboundboxCode(outBoundBoxCode);
                        }

                    }
                    if (pickingWay == Constants.PICKING_WAY_FOUR) {// 周转箱流程
                        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
                        if (null == operatorLine) {
                            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                        }
                        Set<Long> turnoverBoxs = operatorLine.getTurnoverBoxs();
                        if (!turnoverBoxs.contains(turnoverBoxId)) {
                            whOperationExecLine.setIsUseNew(true);
                            whOperationExecLine.setOldContainerId(whOperationExecLine.getOldContainerId());
                            whOperationExecLine.setUseContainerId(turnoverBoxId);
                        }
                    }
                    // 先修改作业执行明细的执行量
                    if (null != operationLineId) {
                        WhOperationLine line = new WhOperationLine();
                        BeanUtils.copyProperties(oLCmd, line);
                        if (qty.doubleValue() > oLCmd.getQty().doubleValue()) {// 扫描的数量大于计划量
                            sum += oLCmd.getQty();
                            Double subtract = qty.doubleValue() - sum;
                            if (subtract.doubleValue() > 0) {
                                line.setCompleteQty(oLCmd.getQty());
                                whOperationExecLine.setQty(oLCmd.getQty());
                                whOperationExecLineDao.insert(whOperationExecLine);
                                list.add(whOperationExecLine);
                                // 修改作业明细表
                                line.setCompleteQty(oLCmd.getQty());
                                whOperationLineDao.saveOrUpdateByVersion(line);
                                continue;
                            }
                            if (subtract.doubleValue() == 0) {
                                line.setCompleteQty(oLCmd.getQty());
                                whOperationExecLine.setQty(oLCmd.getQty());
                                whOperationExecLineDao.insert(whOperationExecLine);
                                list.add(whOperationExecLine);
                            }
                            if (subtract.doubleValue() < 0) {
                                line.setCompleteQty(sum - qty.doubleValue());
                                whOperationExecLine.setQty(oLCmd.getQty());
                                whOperationExecLineDao.insert(whOperationExecLine);
                                list.add(whOperationExecLine);
                            }

                        } else if (qty.doubleValue() < oLCmd.getQty().doubleValue()) {
                            whOperationExecLine.setQty(qty);
                            whOperationExecLineDao.insert(whOperationExecLine);
                            list.add(whOperationExecLine);
                        } else {
                            whOperationExecLine.setQty(qty);
                            whOperationExecLineDao.insert(whOperationExecLine);
                            list.add(whOperationExecLine);
                        }
                        // 修改作业明细
                        if (qty.doubleValue() < oLCmd.getQty().doubleValue()) {
                            Double sumQty = qty + oLCmd.getCompleteQty();
                            line.setCompleteQty(sumQty);
                        }
                        if (qty.doubleValue() == oLCmd.getQty().doubleValue()) {
                            line.setCompleteQty(qty);
                        }
                        whOperationLineDao.saveOrUpdateByVersion(line);
                    }
                    break;
                }
            }
            if (isShortPickingEnd) { // 拣货完成
                operationLineId = oLCmd.getId(); // 获取当前作业明细id
                WhOperationExecLine whOperationExecLine = this.getWhOperationExecLine(userId, outBoundBoxCode, turnoverBoxId, outBoundBoxId, operationId, ouId, operationLineId, outerContainerId, insideContainerId);
                whOperationExecLine.setQty(qty);
                whOperationExecLine.setIsShortPicking(true);
                whOperationExecLineDao.insert(whOperationExecLine);
            }
        }
        List<WhOperationExecLine> execlineList = whOperationExecLineDao.checkOperationExecLine(ouId, operationId);
        List<WhOperationExecLine> lineList = whOperationExecLineDao.checkOperationLine(ouId, operationId); // 添加作
        if (execlineList.size() != lineList.size()) {
            throw new BusinessException(ErrorCodes.CHECK_OPERTAION_EXEC_LINE_DIFF);
        }
        Boolean result = false;
        for (WhOperationExecLine execLine : execlineList) {
            String execLineAttr = "┊" + execLine.getFromOuterContainerId() + execLine.getFromInsideContainerId() + execLine.getQty() + execLine.getFromLocationId() + "┊";
            for (WhOperationExecLine line : lineList) {
                String lineAttr = "┊" + line.getFromOuterContainerId() + line.getFromInsideContainerId() + line.getQty() + line.getFromLocationId() + "┊";
                if (execLineAttr.equals(lineAttr)) {
                    result = true;
                    break;
                } else {
                    result = false;
                }
            }
        }

        for (WhOperationExecLine line : lineList) {
            String lineAttr = "┊" + line.getFromOuterContainerId() + line.getFromInsideContainerId() + line.getQty() + line.getFromLocationId() + "┊";
            for (WhOperationExecLine execLine : execlineList) {
                String execLineAttr = "┊" + execLine.getFromOuterContainerId() + execLine.getFromInsideContainerId() + execLine.getQty() + execLine.getFromLocationId() + "┊";
                if (lineAttr.equals(execLineAttr)) {
                    result = true;
                    break;
                } else {
                    result = false;
                }
            }
        }
        if (!result) {
            throw new BusinessException(ErrorCodes.CHECK_OPERTAION_EXEC_LINE_DIFF);
        }
        log.info("PdaPickingWorkManagerImpl addPickingOperationExecLine is end");

        return list;
    }


    /***
     * 返回作业执行明细
     * 
     * @param operationId
     * @param ouId
     * @param operationLineId
     * @return
     */
    private WhOperationExecLine getWhOperationExecLine(Long userId, String outBoundBoxCode, Long turnoverBoxId, Long outBoundBoxId, Long operationId, Long ouId, Long operationLineId, Long outerContainerId, Long insideContainerId) {
        log.info("PdaPickingWorkManagerImpl scanTrunkfulContainer is start");
        List<WhOperationLineCommand> operLineList = pdaPickingWorkCacheManager.cacheOperationLine(operationId, ouId);
        WhOperationLineCommand operLineCommand = null;
        for (WhOperationLineCommand operLinCmd : operLineList) {
            Long id = operLinCmd.getId();
            if (operationLineId.equals(id)) {
                operLineCommand = operLinCmd;
                break;
            }
        }
        WhOperationExecLine operationExecLine = new WhOperationExecLine();
        BeanUtils.copyProperties(operLineCommand, operationExecLine);
        if (!StringUtils.isEmpty(outBoundBoxCode) && null != outBoundBoxId) { // 判断当前的出库箱和拣货的出库箱是否一致
            if (!outBoundBoxCode.equals(operationExecLine.getUseOutboundboxCode())) { // 不一致的时候
                operationExecLine.setIsUseNew(true);
            } else {// 一致， 没有满箱的情况
                operationExecLine.setIsUseNew(false);
            }
            operationExecLine.setUseOutboundboxCode(outBoundBoxCode);
            operationExecLine.setUseOutboundboxId(outBoundBoxId);
            operationExecLine.setOldOutboundboxId(operationExecLine.getUseOutboundboxId());
            operationExecLine.setOldOutboundboxCode(operationExecLine.getUseOutboundboxCode());
        }
        if (null != turnoverBoxId) { // 判断当前的周转箱和拣货的周转箱是否一致
            if (turnoverBoxId.equals(operationExecLine.getUseContainerId())) {
                operationExecLine.setIsUseNew(false);
            } else {// 有满箱的情况
                operationExecLine.setIsUseNew(true);
            }
            operationExecLine.setOldContainerId(operationExecLine.getUseContainerId());
            operationExecLine.setUseContainerId(turnoverBoxId);
            operationExecLine.setToInsideContainerId(turnoverBoxId);
        }
        operationExecLine.setId(null);
        operationExecLine.setIsShortPicking(false);
        operationExecLine.setLastModifyTime(new Date());
        operationExecLine.setCreateTime(new Date());
        operationExecLine.setOperatorId(userId);
        log.info("PdaPickingWorkManagerImpl scanTrunkfulContainer is start");
        return operationExecLine;
    }

    /***
     * 出库箱或者周转箱/满箱处理
     * 
     * @author tangming
     * @param command
     * @return
     */
    public void scanTrunkfulContainer(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl scanTrunkfulContainer is start");
        Integer pickingWay = command.getPickingWay();
        Long ouId = command.getOuId();
        if (pickingWay == Constants.PICKING_WAY_THREE) {
            String outBounxBoxCode = command.getOutBounxBoxCode();
            ContainerCommand c = containerDao.getContainerByCode(outBounxBoxCode, ouId);
            if (null == c) {
                // 容器信息不存在
                log.error("pdaScanContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            // 验证容器Lifecycle是否有效
            if (!c.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                // 容器Lifecycle无效
                log.error("pdaScanContainer container lifecycle error =" + c.getLifecycle() + " logid: " + logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
            }
            // 验证容器状态是否是待上架
            if (!c.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE)) {
                log.error("pdaScanContainer container status error =" + c.getStatus() + " logid: " + logId);
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY, new Object[] {c.getStatus()});
            }
        }
        if (pickingWay == Constants.PICKING_WAY_FOUR) {
            String turnoverBoxCode = command.getTurnoverBoxCode(); // 周转箱
            OutBoundBoxType outBoundBox = outBoundBoxTypeDao.findByCode(turnoverBoxCode, ouId);
            if (null == outBoundBox) {
                throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL);
            }
            // 验证容器Lifecycle是否有效
            if (!outBoundBox.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_STATUS_NO);
            }
        }

        log.info("PdaPickingWorkManagerImpl scanTrunkfulContainer is end");
    }

    /**
     * 拼写工作明细ID和作业明细ID
     */
    public static String getWorkLineToOnlySku(Long id, Long workLineId, String uuid) {
        String lineToSku = "";
        lineToSku = id + "-" + workLineId + "-" + workLineId;
        return lineToSku;
    }

    /**
     * 拆分工作明细ID和作业明细ID
     */
    public static List<Long> demergeWorkLineToOnlySku(String skuAttrId) {
        List<Long> lineToSku = new ArrayList<Long>();
        String[] values = skuAttrId.split("-");
        for (int i = 0; i < values.length; i++) {
            lineToSku.add(Long.valueOf(values[i]));
        }
        return lineToSku;
    }

    /****
     * 校验作业执行明细
     * 
     * @author tangming
     * @param operationId
     * @param ouId
     * @return
     */
    // private void checkOperationExecLine(Long operationId, Long ouId) {
    // log.info("PdaPickingWorkManagerImpl checkOperationExecLine is start");
    // List<WhOperationExecLine> operationExeclineList =
    // whOperationExecLineDao.checkOperationExecLine(operationId, ouId);
    // if (null != operationExeclineList && operationExeclineList.size() != 0) {
    // throw new BusinessException(ErrorCodes.CHECK_OPERTAION_EXEC_LINE_DIFF);
    // }
    // log.info("PdaPickingWorkManagerImpl checkOperationExecLine is end");
    // }


    /***
     * 查询库存sn残次信息
     * 
     * @param sn
     * @param defectWareBarCode
     * @return
     */
    public PickingScanResultCommand judgeIsOccupationCode(PickingScanResultCommand command) {
        log.info("PdaPickingWorkManagerImpl judgeIsOccupationCode is start");
        Long operationId = command.getOperationId();
        WhSkuInventoryCommand invSkuCmd = new WhSkuInventoryCommand();
        invSkuCmd.setInvStatus(Long.valueOf(command.getSkuInvStatus())); // 库存状态
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        try {
            invSkuCmd.setMfgDate(sdf.parse(command.getSkuMfgDate()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        try {
            invSkuCmd.setExpDate(sdf.parse(command.getSkuExpDate()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        invSkuCmd.setInvType(command.getSkuInvType());
        invSkuCmd.setBatchNumber(command.getBatchNumber());
        invSkuCmd.setCountryOfOrigin(command.getSkuOrigin());
        invSkuCmd.setInvAttr1(command.getSkuInvAttr1());
        invSkuCmd.setInvAttr2(command.getSkuInvAttr2());
        invSkuCmd.setInvAttr3(command.getSkuInvAttr3());
        invSkuCmd.setInvAttr4(command.getSkuInvAttr4());
        invSkuCmd.setInvAttr5(command.getSkuInvAttr5());
        String skuAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(invSkuCmd);
        Long locationId = command.getLocationId();
        String insideContainerCode = command.getInsideContainerCode();
        Long ouId = command.getOuId();
        String outerContinerCode = command.getTipOuterContainerCode();
        Long outerContainerId = null;
        ContainerCommand oc = containerDao.getContainerByCode(outerContinerCode, ouId);
        if (null == oc) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        outerContainerId = oc.getId();
        ContainerCommand c = containerDao.getContainerByCode(insideContainerCode, ouId);
        if (null == c) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        Long insideContainerId = c.getId();
        String skuAttrId = locationId + "┊" + outerContainerId + "︴" + insideContainerId;
        String uuid = "";
        if (!StringUtils.isEmpty(command.getSkuSn()) || !StringUtils.isEmpty(command.getSkuDefect())) { // 是sn残次信息
            // 判断是否占用sn/残次条码
            // 到库存表中查询
            List<WhSkuInventoryCommand> whSkuInvCmdList = whSkuInventoryDao.getWhSkuInventoryCmdByOccupationLineId(locationId, ouId, operationId, outerContainerId, insideContainerId);
            if (null == whSkuInvCmdList || whSkuInvCmdList.size() == 0) {
                throw new BusinessException(ErrorCodes.LOCATION_INVENTORY_IS_NO);
            }
            for (WhSkuInventoryCommand skuInvCmd : whSkuInvCmdList) {
                String skuInvAttrId = skuInvCmd.getLocationId() + "┊" + skuInvCmd.getOuterContainerId() + "︴" + skuInvCmd.getInsideContainerId();
                String skuInvAttrIds = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
                if (skuAttrId.equals(skuInvAttrId) && skuAttrIds.equals(skuInvAttrIds)) {
                    uuid = skuInvCmd.getUuid();
                    break;
                }
            }
            String snCode = command.getSkuSn();
            if (StringUtils.isEmpty(snCode)) {
                snCode = command.getSkuBarCode();
            }
            WhSkuInventorySn skuInvSn = whSkuInventorySnDao.findWhSkuInventoryByUuidAndSnOrDefectWareBarcode(ouId, uuid, snCode);
            if (null == skuInvSn) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_NULL);
            }
            if (!StringUtils.isEmpty(skuInvSn.getOccupationCode())) { // 占用sn或者残次条码
                // 提示sn/或者残次条码
                if (!StringUtils.isEmpty(skuInvSn.getDefectWareBarcode())) {// 提示残次条码
                    command.setIsTipSkuDefect(true);
                    command.setTipSkuDefect(skuInvSn.getDefectWareBarcode());
                } else { // 提示sn
                    command.setIsTipSkuSn(true);
                    command.setTipSkuSn(skuInvSn.getSn());
                }
            } else {
                // 扫描sn/残次条码
                if (!StringUtils.isEmpty(skuInvSn.getDefectWareBarcode())) {// 提示残次条码
                    command.setIsNeedScanSkuDefect(true);
                } else { // 提示sn
                    command.setIsNeedScanSkuSn(true);
                }
            }
        }
        log.info("PdaPickingWorkManagerImpl judgeIsOccupationCode is end");
        return command;
    }

    /****
     * 确定补货方式和占用模型
     * 
     * @author qiming.liu
     * @param whWork
     * @param ouId
     * @return
     */
    @Override
    public PickingScanResultCommand getPickingForGroup(WhWork whWork, Long ouId) {
        // 根据工作id获取作业信息
        WhOperationCommand whOperationCommand = whOperationManager.findOperationByWorkId(whWork.getId(), ouId);
        // 统计分析工作及明细并缓存
        this.getOperatioLineForGroup(whOperationCommand);
        // 获取缓存中的统计分析数据
        OperatioLineStatisticsCommand statisticsCommand = pdaPickingWorkCacheManager.getOperatioLineStatistics(whOperationCommand.getId(), whOperationCommand.getOuId());
        // 返回结果初始化
        PickingScanResultCommand pickingScanResultCommand = new PickingScanResultCommand();
        // 作业id
        pickingScanResultCommand.setOperationId(whOperationCommand.getId());// 是否需要仓库id
        // 捡货方式
        if (whOperationCommand.getIsWholeCase() == false && statisticsCommand.getOuterContainers().size() > 0 && statisticsCommand.getOutbounxBoxs().size() == 0) {
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_ONE);
        } else if (whOperationCommand.getIsWholeCase() == false && statisticsCommand.getOuterContainers().size() > 0 && statisticsCommand.getOutbounxBoxs().size() > 0) {
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_TWO);
        } else if (whOperationCommand.getIsWholeCase() == false && statisticsCommand.getOuterContainers().size() == 0 && statisticsCommand.getOutbounxBoxs().size() > 0) {
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_THREE);
        } else if (whOperationCommand.getIsWholeCase() == false && statisticsCommand.getOuterContainers().size() == 0 && statisticsCommand.getTurnoverBoxs().size() > 0) {
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_FOUR);
        } else if (whOperationCommand.getIsWholeCase() == true && statisticsCommand.getPallets().size() > 0 && statisticsCommand.getContainers().size() > 0) {
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_FIVE);
        } else if (whOperationCommand.getIsWholeCase() == true && statisticsCommand.getPallets().size() == 0 && statisticsCommand.getContainers().size() > 0) {
            pickingScanResultCommand.setPickingWay(Constants.PICKING_WAY_SIX);
        }
        // 库存占用模型
        if (statisticsCommand.getOuterContainerIds().size() > 0 && statisticsCommand.getInsideContainerIds().size() == 0 && statisticsCommand.getInsideSkuIds().size() == 0) {
            // 仅占用托盘内商品
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_ONE);
        } else if (statisticsCommand.getOuterContainerIds().size() == 0 && statisticsCommand.getInsideContainerIds().size() > 0 && statisticsCommand.getInsideSkuIds().size() == 0) {
            // 仅占用货箱内商品
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_TWO);
        } else if (statisticsCommand.getOuterContainerIds().size() == 0 && statisticsCommand.getInsideContainerIds().size() == 0 && statisticsCommand.getInsideSkuIds().size() > 0) {
            // 仅占用库位上散件商品
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_THREE);
        } else {// 有托盘||有货箱（无外部容器）||散件
                // 混合占用
            pickingScanResultCommand.setInvOccupyMode(Constants.INV_OCCUPY_MODE_FOUR);
        }
        return pickingScanResultCommand;
    }

    /****
     * 整拖拣货模式生成作业执行明细
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @param WhSkuCommand
     * @param isTabbInvTotal
     * @return
     */
    @Override
    public PickingScanResultCommand palletPickingOperationExecLine(PickingScanResultCommand command, Boolean isTabbInvTotal) {
        Long operationId = command.getOperationId();
        if (1 == command.getPalletPickingMode() || 2 == command.getPalletPickingMode()) {
            command = this.containerPickingOperationExecLine(command, isTabbInvTotal);
            //修改容器状态        
            Container container = new Container();
            ContainerCommand containerCmd = containerDao.getContainerByCode(command.getTipOuterContainerCode(), command.getOuId());
            if (null == containerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            BeanUtils.copyProperties(containerCmd, container);
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            if(null != command.getPickingWay()){
                container.setStatus(ContainerStatus.CONTAINER_STATUS_PICKING_END);    
            }else{
                container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY); 
            }
            containerDao.saveOrUpdateByVersion(container);
        } else {
            String outerContainerCode = command.getTipOuterContainerCode();
            Long outerContainerId = null;
            ContainerCommand outerContainerCmd = new ContainerCommand();
            if (!StringUtil.isEmpty(outerContainerCode)) {
                outerContainerCmd = containerDao.getContainerByCode(outerContainerCode, command.getOuId());
                if (null == outerContainerCmd) {
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
                outerContainerId = outerContainerCmd.getId();
            }
            String insideContainerCode = command.getTipInsideContainerCode();
            Long insideContainerId = null;
            ContainerCommand insideContainerCmd = null;
            if (!StringUtil.isEmpty(insideContainerCode)) {
                insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, command.getOuId());
                if (null == insideContainerCmd) {
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
                insideContainerId = insideContainerCmd.getId();
            }
            OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + command.getOperationId().toString());
            if (null == operatorLine) {
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            }
            Map<Long, Set<Long>> outerToInsideIds = operatorLine.getOuterToInside(); // (库位上有外部容器的内部容器)
            Set<Long> insideContainerIds = null;
            if (null != outerContainerId) {
                insideContainerIds = outerToInsideIds.get(outerContainerId);// 外部容器对应的内内部容器
            }
            CheckScanResultCommand cSRCmd = pdaPickingWorkCacheManager.palletPickingCacheAndCheck(command.getLocationId(), insideContainerIds, outerContainerId, insideContainerId, operationId);
            if (cSRCmd.getIsNeedTipInsideContainer()) {
                Container ic = containerDao.findByIdExt(cSRCmd.getTipiInsideContainerId(), command.getOuId());
                command.setTipInsideContainerCode(ic.getCode());
                command.setIsNeedTipInsideContainer(true);
            }
            if (cSRCmd.getIsPicking()) {
                command.setIsPicking(true);
                command = this.containerPickingOperationExecLine(command, isTabbInvTotal);
                if (null != command.getPickingWay()) {
                    // 判断是拣完在播，是否是最后一箱
                    WhWorkCommand work = workManager.findWorkByWorkCode(command.getWorkBarCode(), command.getOuId());
                    List<WhWorkCommand> list = workManager.findWorkByBatch(work.getBatch(), command.getOuId());
                    int count = 0;
                    for (WhWorkCommand cmd : list) {
                        if (!(WorkStatus.FINISH.equals(cmd.getStatus()) || WorkStatus.PARTLY_FINISH.equals(cmd.getStatus()))) {
                            count++;
                        }
                    }
                    if (count == 1) {// 当前工作是最后一个
                        command.setIsLastWork(true); // 当前工作是一个小批次下的最后一个工作
                    }
                    long startTime = System.currentTimeMillis(); // 获取开始时间
                    log.info("collection run start time:" + startTime);
                    // 插入集货表
                    String pickingMode = this.insertIntoCollection(command, command.getOuId(), command.getUserId());
                    long endTime = System.currentTimeMillis(); // 获取结束时间
                    log.info("collection run end time:" + endTime);
                    log.info("collection run  time:" + (endTime - startTime));
                    command.setPickingMode(pickingMode);
                    if(2 != command.getTempReplenishWay() && 3 != command.getTempReplenishWay()){
                        // 清除缓存
                        pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(command.getOperationId(), true, command.getLocationId());    
                    }
                    // 更改出库单状态
                    List<WhOperationLineCommand> whOperationLineCommandLst = whOperationLineDao.findOperationLineByOperationId(command.getOperationId(), command.getOuId());
                    for (WhOperationLineCommand whOperationLineCommand : whOperationLineCommandLst) {
                        List<WhOdoOutBoundBoxCommand> odoOutBoundBoxByOdo = whOdoOutBoundBoxDao.gethOdoOutBoundBoxLstByOdo(whOperationLineCommand.getOdoId(), null, false, whOperationLineCommand.getOuId());
                        List<WhOperationCommand> operationCommandByOdo = whOperationDao.findOperationCommandByOdo(whOperationLineCommand.getOdoId(), null, 10, whOperationLineCommand.getOuId());
                        List<WhOdoOutBoundBoxCommand> odoOutBoundBoxByOdoLine = whOdoOutBoundBoxDao.gethOdoOutBoundBoxLstByOdo(null, whOperationLineCommand.getOdoLineId(), false, whOperationLineCommand.getOuId());
                        List<WhOperationCommand> operationCommandByOdoLine = whOperationDao.findOperationCommandByOdo(null, whOperationLineCommand.getOdoLineId(), 10, whOperationLineCommand.getOuId());
                        if (0 == odoOutBoundBoxByOdo.size() && 0 == operationCommandByOdo.size()) {
                            // 根据出库单code获取出库单信息
                            WhOdo odo = odoDao.findByIdOuId(whOperationLineCommand.getOdoId(), whOperationLineCommand.getOuId());
                            if (Constants.WH_PICKING_MODE.equals(pickingMode)) {
                                odo.setOdoStatus(OdoStatus.PICKING_FINISH);
                                odo.setLagOdoStatus(OdoStatus.PICKING_FINISH);
                            } else {
                                odo.setOdoStatus(OdoStatus.COLLECTION_FINISH);
                                odo.setLagOdoStatus(OdoStatus.COLLECTION_FINISH);
                            }
                            odoDao.update(odo);
                        }
                        if (0 == odoOutBoundBoxByOdoLine.size() && 0 == operationCommandByOdoLine.size()) {
                            // 根据出库单code获取出库单信息
                            WhOdoLine whOdoLine = odoLineManager.findOdoLineById(whOperationLineCommand.getOdoLineId(), whOperationLineCommand.getOuId());
                            whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_PUTAWAY_FINISH);
                            whOdoLineDao.update(whOdoLine);
                        }
                    }
                }
                //修改容器状态 
                Container container = new Container();
                ContainerCommand containerCmd = containerDao.getContainerByCode(command.getTipOuterContainerCode(), command.getOuId());
                if (null == containerCmd) {
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
                BeanUtils.copyProperties(containerCmd, container);
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                if(null != command.getPickingWay()){
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_PICKING_END);    
                }else{
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY); 
                }
                containerDao.saveOrUpdateByVersion(container);
            }
        }
        return command;
    }

    /****
     * 整箱拣货模式生成作业执行明细
     * 
     * @author qiming.liu
     * @param PickingScanResultCommand
     * @param WhSkuCommand
     * @param isTabbInvTotal
     * @return
     */
    @Override
    public PickingScanResultCommand containerPickingOperationExecLine(PickingScanResultCommand command, Boolean isTabbInvTotal) {
        ContainerCommand conCmd = new ContainerCommand();
        if(!command.getTipOuterContainerCode().isEmpty()){
            conCmd = containerDao.getContainerByCode(command.getTipOuterContainerCode(), command.getOuId());    
        }else{
            conCmd = containerDao.getContainerByCode(command.getTipInsideContainerCode(), command.getOuId());    
        }
        if (null == conCmd) {
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        // 根据作业id获取作业明细信息
        List<WhOperationLineCommand> operationLineList = whOperationLineManager.findOperationLineByOperationId(command.getOperationId(), command.getOuId());
        for (WhOperationLineCommand operationLineCommand : operationLineList) {
            if((!command.getTipOuterContainerCode().isEmpty() && conCmd.getId().longValue() == operationLineCommand.getFromOuterContainerId().longValue()) || (command.getTipOuterContainerCode().isEmpty() && conCmd.getId().longValue() == operationLineCommand.getFromInsideContainerId().longValue())){
                // 缓存数据 
                if(null != operationLineCommand.getFromOuterContainerId()){
                    pdaPickingWorkCacheManager.cacheOuterContainerCode(command.getLocationId(), operationLineCommand.getFromOuterContainerId(), command.getOperationId());    
                }
                if(2 != command.getReplenishWay() || (2 == command.getReplenishWay() && 2 < command.getPalletPickingMode())){
                    pdaPickingWorkCacheManager.cacheInsideContainerCode(command.getLocationId(), operationLineCommand.getFromInsideContainerId(),operationLineCommand.getFromOuterContainerId(), command.getOperationId());
                }
                operationLineCommand.setCompleteQty(operationLineCommand.getQty());
                whOperationLineManager.saveOrUpdate(operationLineCommand);
                // 生成作业作业明细
                WhOperationExecLine whOperationExecLine = new WhOperationExecLine();
                // 复制数据
                BeanUtils.copyProperties(operationLineCommand, whOperationExecLine);
                whOperationExecLine.setCompleteQty(0.0);
                whOperationExecLine.setIsUseNew(false);
                whOperationExecLine.setId(null);
                whOperationExecLine.setUseContainerId(operationLineCommand.getFromInsideContainerId());
                whOperationExecLine.setUseOuterContainerId(operationLineCommand.getFromOuterContainerId());
                whOperationExecLine.setIsShortPicking(false);
                whOperationExecLine.setLastModifyTime(new Date());
                whOperationExecLine.setCreateTime(new Date());
                whOperationExecLine.setOperatorId(command.getUserId());
                whOperationExecLineDao.insert(whOperationExecLine);
                // 根据作业明细查询库存信息
                Double onHandQty = operationLineCommand.getQty();
                List<WhSkuInventory> whSkuInventoryLst = this.whSkuInventoryDao.findInventorysByUuid(operationLineCommand.getOuId(), operationLineCommand.getUuid());
                for (WhSkuInventory oldSkuInventory : whSkuInventoryLst) {
                    if (oldSkuInventory.getOnHandQty() > onHandQty) {
                        // 生成容器库存
                        WhSkuInventory newSkuInventory = new WhSkuInventory();
                        // 复制数据
                        BeanUtils.copyProperties(oldSkuInventory, newSkuInventory);
                        newSkuInventory.setLocationId(null);
                        if (null != command.getInWarehouseMoveWay()) {
                            newSkuInventory.setOccupationCode(operationLineCommand.getInvMoveCode());
                        }
                        if (null != command.getReplenishWay() || null != command.getPickingWay()) {
                            WhOdo whOdo = odoDao.findByIdOuId(operationLineCommand.getOdoId(), operationLineCommand.getOuId());
                            newSkuInventory.setOccupationCode(whOdo.getOdoCode());
                        }
                        newSkuInventory.setOccupationLineId(operationLineCommand.getOdoLineId());
                        newSkuInventory.setOnHandQty(oldSkuInventory.getOnHandQty() - onHandQty);
                        // 内部对接码
                        try {
                            newSkuInventory.setUuid(SkuInventoryUuid.invUuid(newSkuInventory));
                        } catch (Exception e) {
                            log.error(getLogMsg("whSkuInventoryAllocated uuid error, logId is:[{}]", new Object[] {logId}), e);
                            throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                        }
                        whSkuInventoryDao.insert(newSkuInventory);
                        insertGlobalLog(GLOBAL_LOG_INSERT, newSkuInventory, command.getOuId(), command.getUserId(), null, null);
                        insertSkuInventoryLog(newSkuInventory.getId(), newSkuInventory.getOnHandQty(), 0.00, isTabbInvTotal, command.getOuId(), command.getUserId(), InvTransactionType.PICKING);
                        List<WhSkuInventorySnCommand> whSkuInventorySnCommandLst = new ArrayList<WhSkuInventorySnCommand>();
                        whSkuInventorySnCommandLst = whSkuInventorySnDao.findWhSkuInventoryByUuid(oldSkuInventory.getOuId(), oldSkuInventory.getUuid());
                        double count = 0;
                        for (WhSkuInventorySnCommand whSkuInventorySnCommand : whSkuInventorySnCommandLst) {
                            WhSkuInventorySn whSkuInventorySn = new WhSkuInventorySn();
                            // 复制数据
                            BeanUtils.copyProperties(whSkuInventorySnCommand, whSkuInventorySn);
                            whSkuInventorySn.setUuid(newSkuInventory.getUuid());
                            whSkuInventorySn.setOccupationCode(newSkuInventory.getOccupationCode());
                            whSkuInventorySn.setOccupationLineId(newSkuInventory.getOccupationLineId());
                            whSkuInventorySnDao.update(whSkuInventorySn);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, whSkuInventorySn, command.getOuId(), command.getUserId(), null, null);
                            insertSkuInventorySnLog(whSkuInventorySn.getId(), command.getOuId()); // 记录sn日志
                            // 判断数量
                            BigDecimal data1 = new BigDecimal(count);
                            BigDecimal data2 = new BigDecimal(newSkuInventory.getOnHandQty());
                            if (0 == data1.compareTo(data2)) {
                                continue;
                            }
                            count = count + 1;
                        }
                        // 删除原库存
                        oldSkuInventory.setOnHandQty(oldSkuInventory.getOnHandQty() - onHandQty);
                        whSkuInventoryDao.update(oldSkuInventory);
                    } else {
                        // 生成容器库存
                        WhSkuInventory newSkuInventory = new WhSkuInventory();
                        // 复制数据
                        BeanUtils.copyProperties(oldSkuInventory, newSkuInventory);
                        newSkuInventory.setLocationId(null);
                        if (null != command.getInWarehouseMoveWay()) {
                            newSkuInventory.setOccupationCode(operationLineCommand.getInvMoveCode());
                        }
                        if (null != command.getReplenishWay() || null != command.getPickingWay()) {
                            WhOdo whOdo = odoDao.findByIdOuId(operationLineCommand.getOdoId(), operationLineCommand.getOuId());
                            if (null != whOdo) {
                                newSkuInventory.setOccupationCode(whOdo.getOdoCode());
                            }
                        }
                        newSkuInventory.setOccupationLineId(operationLineCommand.getOdoLineId());
                        // 内部对接码
                        try {
                            newSkuInventory.setUuid(SkuInventoryUuid.invUuid(newSkuInventory));
                        } catch (Exception e) {
                            log.error(getLogMsg("whSkuInventoryAllocated uuid error, logId is:[{}]", new Object[] {logId}), e);
                            throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
                        }
                        whSkuInventoryDao.insert(newSkuInventory);
                        insertGlobalLog(GLOBAL_LOG_INSERT, newSkuInventory, command.getOuId(), command.getUserId(), null, null);
                        insertSkuInventoryLog(newSkuInventory.getId(), newSkuInventory.getOnHandQty(), 0.00, isTabbInvTotal, command.getOuId(), command.getUserId(), InvTransactionType.PICKING);
                        List<WhSkuInventorySnCommand> whSkuInventorySnCommandLst = new ArrayList<WhSkuInventorySnCommand>();
                        whSkuInventorySnCommandLst = whSkuInventorySnDao.findWhSkuInventoryByUuid(oldSkuInventory.getOuId(), oldSkuInventory.getUuid());
                        double count = 0;
                        for (WhSkuInventorySnCommand whSkuInventorySnCommand : whSkuInventorySnCommandLst) {
                            WhSkuInventorySn whSkuInventorySn = new WhSkuInventorySn();
                            // 复制数据
                            BeanUtils.copyProperties(whSkuInventorySnCommand, whSkuInventorySn);
                            whSkuInventorySn.setUuid(newSkuInventory.getUuid());
                            whSkuInventorySn.setOccupationCode(newSkuInventory.getOccupationCode());
                            whSkuInventorySn.setOccupationLineId(newSkuInventory.getOccupationLineId());
                            whSkuInventorySnDao.update(whSkuInventorySn);
                            insertGlobalLog(GLOBAL_LOG_UPDATE, whSkuInventorySn, command.getOuId(), command.getUserId(), null, null);
                            insertSkuInventorySnLog(whSkuInventorySn.getId(), command.getOuId()); // 记录sn日志
                            // 判断数量
                            BigDecimal data1 = new BigDecimal(count);
                            BigDecimal data2 = new BigDecimal(newSkuInventory.getOnHandQty());
                            if (0 == data1.compareTo(data2)) {
                                continue;
                            }
                            count = count + 1;
                        }
                        Double oldQty1 = 0.0;
                        if (true == isTabbInvTotal) { // 在库存日志是否记录交易前后库存总数 0：否 1：是
                            try {
                                oldQty1 = whSkuInventoryLogManager.sumSkuInvOnHandQty(oldSkuInventory.getUuid(), command.getOuId());
                            } catch (Exception e) {
                                log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                            }
                        } else {
                            oldQty1 = 0.0;
                        }
                        insertSkuInventoryLog(oldSkuInventory.getId(), -oldSkuInventory.getOnHandQty(), oldQty1, isTabbInvTotal, command.getOuId(), command.getUserId(), InvTransactionType.PICKING);
                        // 删除原库存
                        whSkuInventoryDao.delete(oldSkuInventory.getId());
                        insertGlobalLog(GLOBAL_LOG_DELETE, oldSkuInventory, command.getOuId(), command.getUserId(), null, null);
                    }
                }
                if (null == command.getPickingWay()) {
                    List<WhSkuInventoryAllocated> whSkuInventoryAllocatedLst = whSkuInventoryAllocatedDao.findSkuInventoryAllocatedByUuid(operationLineCommand.getUuid(), operationLineCommand.getOuId());
                    for (WhSkuInventoryAllocated whSkuInventoryAllocated : whSkuInventoryAllocatedLst) {
                        whSkuInventoryAllocatedDao.delete(whSkuInventoryAllocated.getId());
                    }
                }
            }
        }
        if( null == command.getTempReplenishWay() || 2 != command.getTempReplenishWay() || 3 != command.getTempReplenishWay()){
            WhOperationCommand operationCmd = whOperationManager.findOperationById(command.getOperationId(), command.getOuId());
            operationCmd.setIsPickingFinish(true);
            operationCmd.setModifiedId(command.getUserId());
            whOperationManager.saveOrUpdate(operationCmd);    
        }
        //修改容器状态
        if(!command.getTipOuterContainerCode().isEmpty()){
            ContainerCommand outerContainer = containerDao.getContainerByCode(command.getTipOuterContainerCode(), command.getOuId());
            if (null == outerContainer) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + command.getOperationId().toString());
            Map<Long, Set<Long>> outerToInside = new HashMap<Long, Set<Long>>();
            outerToInside = operatorLine.getOuterToInside();  
            Set<Long> insideContainers = new HashSet<Long>();
            insideContainers = outerToInside.get(outerContainer.getId());
            for(Long insideContainerId : insideContainers){
                Container container = new Container();
                ContainerCommand containerCmd = containerDao.findContainerCommandByIdExt(insideContainerId, command.getOuId());
                if (null == containerCmd) {
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
                BeanUtils.copyProperties(containerCmd, container);
                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
                if(null != command.getPickingWay()){
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_PICKING_END);    
                }else{
                    container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY); 
                }
                containerDao.saveOrUpdateByVersion(container);
                
            }
        }else{
            Container container = new Container();
            ContainerCommand containerCmd = containerDao.getContainerByCode(command.getTipInsideContainerCode(), command.getOuId());
            if (null == containerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            BeanUtils.copyProperties(containerCmd, container);
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            if(null != command.getPickingWay()){
                container.setStatus(ContainerStatus.CONTAINER_STATUS_PICKING_END);    
            }else{
                container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY); 
            }
            containerDao.saveOrUpdateByVersion(container);
        }
        if (null != command.getPickingWay()) {
            // 更新工作及作业状态
            pdaPickingWorkCacheManager.pdaPickingUpdateStatus(command.getOperationId(), command.getWorkBarCode(), command.getOuId(), command.getUserId());
        }
        if (null != command.getInWarehouseMoveWay() && 2 == command.getInWarehouseMoveWay() && (3 == command.getPalletPickingMode() || 4 == command.getPalletPickingMode())) {
            command.setIsPicking(true);
        } else if (null != command.getPickingWay() && 5 == command.getPickingWay() && (3 == command.getPalletPickingMode() || 4 == command.getPalletPickingMode())) {
            command.setIsPicking(true);
        } else if (null != command.getReplenishWay() && 2 == command.getReplenishWay() && (3 == command.getPalletPickingMode() || 4 == command.getPalletPickingMode())) {
            command.setIsPicking(true);
        } else {
            command.setIsPicking(true);
            if (null != command.getPickingWay()) {
                // 判断是拣完在播，是否是最后一箱
                WhWorkCommand work = workManager.findWorkByWorkCode(command.getWorkBarCode(), command.getOuId());
                List<WhWorkCommand> list = workManager.findWorkByBatch(work.getBatch(), command.getOuId());
                int count = 0;
                for (WhWorkCommand cmd : list) {
                    if (!(WorkStatus.FINISH.equals(cmd.getStatus()) || WorkStatus.PARTLY_FINISH.equals(cmd.getStatus()))) {
                        count++;
                    }
                }
                if (count == 1) {// 当前工作是最后一个
                    command.setIsLastWork(true); // 当前工作是一个小批次下的最后一个工作
                }
                long startTime = System.currentTimeMillis(); // 获取开始时间
                log.info("collection run start time:" + startTime);
                // 插入集货表
                String pickingMode = this.insertIntoCollection(command, command.getOuId(), command.getUserId());
                long endTime = System.currentTimeMillis(); // 获取结束时间
                log.info("collection run end time:" + endTime);
                log.info("collection run  time:" + (endTime - startTime));
                command.setPickingMode(pickingMode);
                if(2 != command.getTempReplenishWay() && 3 != command.getTempReplenishWay()){
                    // 清除缓存
                    pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(command.getOperationId(), true, command.getLocationId());    
                }
                // 更改出库单状态
                List<WhOperationLineCommand> whOperationLineCommandLst = whOperationLineDao.findOperationLineByOperationId(command.getOperationId(), command.getOuId());
                for (WhOperationLineCommand whOperationLineCommand : whOperationLineCommandLst) {
                    List<WhOdoOutBoundBoxCommand> odoOutBoundBoxByOdo = whOdoOutBoundBoxDao.gethOdoOutBoundBoxLstByOdo(whOperationLineCommand.getOdoId(), null, false, whOperationLineCommand.getOuId());
                    List<WhOperationCommand> operationCommandByOdo = whOperationDao.findOperationCommandByOdo(whOperationLineCommand.getOdoId(), null, 10, whOperationLineCommand.getOuId());
                    List<WhOdoOutBoundBoxCommand> odoOutBoundBoxByOdoLine = whOdoOutBoundBoxDao.gethOdoOutBoundBoxLstByOdo(null, whOperationLineCommand.getOdoLineId(), false, whOperationLineCommand.getOuId());
                    List<WhOperationCommand> operationCommandByOdoLine = whOperationDao.findOperationCommandByOdo(null, whOperationLineCommand.getOdoLineId(), 10, whOperationLineCommand.getOuId());
                    if (0 == odoOutBoundBoxByOdo.size() && 0 == operationCommandByOdo.size()) {
                        // 根据出库单code获取出库单信息
                        WhOdo odo = odoDao.findByIdOuId(whOperationLineCommand.getOdoId(), whOperationLineCommand.getOuId());
                        if (Constants.WH_PICKING_MODE.equals(pickingMode)) {
                            odo.setOdoStatus(OdoStatus.PICKING_FINISH);
                            odo.setLagOdoStatus(OdoStatus.PICKING_FINISH);
                        } else {
                            odo.setOdoStatus(OdoStatus.COLLECTION_FINISH);
                            odo.setLagOdoStatus(OdoStatus.COLLECTION_FINISH);
                        }
                        odoDao.update(odo);
                    }
                    if (0 == odoOutBoundBoxByOdoLine.size() && 0 == operationCommandByOdoLine.size()) {
                        // 根据出库单code获取出库单信息
                        WhOdoLine whOdoLine = odoLineManager.findOdoLineById(whOperationLineCommand.getOdoLineId(), whOperationLineCommand.getOuId());
                        whOdoLine.setOdoLineStatus(OdoStatus.ODOLINE_PUTAWAY_FINISH);
                        whOdoLineDao.update(whOdoLine);
                    }
                }
            }
        }
        return command;
    }

    /***
     * 缓存库位
     * 
     * @param operationId
     * @param locationCode
     * @param ouId
     */
    public void cacheLocation(Long operationId, Long locationId) {
        pdaPickingWorkCacheManager.cacheLocation(operationId, locationId);
    }

    /***
     * 拣货取消流程
     * 
     * @param outerContainerId
     * @param insideContainerId
     * @param cancelPattern
     * @param pickingType
     * @param locationId
     * @param ouId
     */
    public void cancelPattern(String carCode, String outerContainerCode, String insideContainerCode, int cancelPattern, int pickingWay, Long locationId, Long ouId, Long operationId, Long tipSkuId) {

        Long carId = null;
        if (!StringUtils.isEmpty(carCode)) {
            ContainerCommand cmd = containerDao.getContainerByCode(carCode, ouId);
            carId = cmd.getId();
        }
        Long outerContainerId = null;
        if (!StringUtils.isEmpty(outerContainerCode)) {
            ContainerCommand cmd = containerDao.getContainerByCode(outerContainerCode, ouId);
            if (null == cmd) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerContainerId = cmd.getId();
        }
        Long insideContainerId = null;
        if (!StringUtils.isEmpty(insideContainerCode)) {
            ContainerCommand cmd = containerDao.getContainerByCode(insideContainerCode, ouId);
            if (null == cmd) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = cmd.getId();
        }
        pdaPickingWorkCacheManager.cancelPattern(carId, outerContainerId, insideContainerId, cancelPattern, pickingWay, locationId, ouId, operationId, tipSkuId);
    }

    /***
     * 有小车有出库箱的情况下(获取货格号)
     * 
     * @param operationId
     * @param outBounxBoxCode
     * @return
     */
    public Integer getUseContainerLatticeNo(Long operationId, String outBounxBoxCode) {
        Integer useContainerLatticeNo = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if (null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Map<Integer, String> carStockToOutgoingBox = operatorLine.getCarStockToOutgoingBox();
        Set<Integer> keys = carStockToOutgoingBox.keySet();
        for (Integer key : keys) {
            String value = carStockToOutgoingBox.get(key);
            if (outBounxBoxCode.equals(value)) {
                useContainerLatticeNo = key;
                break;
            }
        }
        return useContainerLatticeNo;
    }

    /***
     * 返回库位
     * 
     * @param locationCode
     * @return
     */
    public Location getLocationByCode(String locationCode, Long ouId) {
        Location location = whLocationDao.findLocationByCode(locationCode, ouId);
        if (null == location) {
            location = whLocationDao.getLocationByBarcode(locationCode, ouId);
            if (null == location) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
            }
        }
        return location;
    }


    /***
     * 补货(拣货)取消流程
     * 
     * @param outerContainerId
     * @param insideContainerId
     * @param cancelPattern
     * @param pickingType
     * @param locationId
     * @param ouId
     */
    public void replenishCancelPattern(String outerContainerCode, String insideContainerCode, int cancelPattern, int replenishWay, Long locationId, Long ouId, Long operationId, Long tipSkuId) {
        Long outerContainerId = null;
        if (!StringUtils.isEmpty(outerContainerCode)) {
            ContainerCommand cmd = containerDao.getContainerByCode(outerContainerCode, ouId);
            if (null == cmd) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            outerContainerId = cmd.getId();
        }
        Long insideContainerId = null;
        if (!StringUtils.isEmpty(insideContainerCode)) {
            ContainerCommand cmd = containerDao.getContainerByCode(insideContainerCode, ouId);
            if (null == cmd) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            insideContainerId = cmd.getId();
        }
        pdaPickingWorkCacheManager.replenishmentCancelPattern(outerContainerId, insideContainerId, cancelPattern, replenishWay, locationId, ouId, operationId, tipSkuId);
    }

    /**
     * 拣货完成
     * 
     * @param operationId
     */
    public void shortPickingEnd(String workCode, Long operationId, Long ouId, Long userId, String outBoundBoxCode, String turnoverBoxCode, Long outBoundBoxId) {
        Long turnoverBoxId = null;
        if (!StringUtils.isEmpty(turnoverBoxCode)) {
            ContainerCommand cmd = containerDao.getContainerByCode(turnoverBoxCode, ouId);
            if (null == cmd) {
                log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            turnoverBoxId = cmd.getId();
        }
        // 获取所有的作业明细
        List<WhOperationLineCommand> lineList = whOperationLineDao.findOperationLineByOperationId(operationId, ouId);
        for (WhOperationLineCommand cmd : lineList) {
            Double completeQty = cmd.getCompleteQty(); // 执行量
            Double qty = cmd.getQty(); // 计划量
            if (qty > completeQty) {
                // 当前作业明细中有sku没有拣货,执行短拣
                WhOperationLine line = new WhOperationLine();
                BeanUtils.copyProperties(cmd, line);
                line.setCompleteQty(qty);
                line.setLastModifyTime(new Date());
                whOperationLineDao.saveOrUpdateByVersion(line);
                Double execLineQty = qty - completeQty;
                Long operationLineId = cmd.getId();
                // 添加作业执行明细
                WhOperationExecLine whOperationExecLine = this.getWhOperationExecLine(userId, outBoundBoxCode, turnoverBoxId, outBoundBoxId, operationId, ouId, operationLineId, null, null);
                whOperationExecLine.setQty(execLineQty);
                whOperationExecLine.setIsShortPicking(true);
                whOperationExecLineDao.insert(whOperationExecLine);
            }
        }
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if (null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        List<Long> locationIds = operatorLine.getLocationIds();
        int count = 0;
        for (Long locationId : locationIds) {
            // 清除缓存
            if (count == locationIds.size()) {
                // 清除缓存
                pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(operationId, true, locationId);
            } else {
                pdaPickingWorkCacheManager.pdaPickingRemoveAllCache(operationId, false, locationId);
            }

            count++;
        }
        // 更新工作及作业状态
        pdaPickingWorkCacheManager.pdaPickingUpdateStatus(operationId, workCode, ouId, userId);
    }

    /***
     * 返回货格号
     * 
     * @param command
     * @param operationId
     */
    public Integer getLatticeNoBySkuAttrIds(PickingScanResultCommand command, Long ouId) {
        Long operationId = command.getOperationId();
        Integer useContainerLatticeNo = command.getUseContainerLatticeNo();
        ArrayDeque<Integer> latticeList = cacheManager.getObject(CacheConstants.CACHE_LATTICE_NO + operationId.toString());
        if (null == latticeList) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Integer lattice = latticeList.getFirst();
        if (!useContainerLatticeNo.equals(lattice)) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        return useContainerLatticeNo;
    }


    /**
     * 是否继续扫描sn
     * 
     * @param insideContainerCode
     * @param skuId
     * @param ouId
     * @return
     */
    public Boolean isContainerScanSn(String insideContainerCode, Long skuId, Long ouId, Long locationId, Double scanSkuQty, Boolean isContinueScanSn, Long operationId) {
        Boolean result = false;
        if (scanSkuQty.equals(Constants.PICKING_NUM)) { // 拣货数量为1
            result = false;
        } else { // 扫描数量不为1时
            if (!StringUtils.isEmpty(insideContainerCode)) {
                Long insideContainerId = null;
                ContainerCommand cmd = containerDao.getContainerByCode(insideContainerCode, ouId);
                if (null == cmd) {
                    log.error("pdaPickingRemmendContainer container is null logid: " + logId);
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
                insideContainerId = cmd.getId();
                String cacheValue = cacheManager.getValue(CacheConstants.SCAN_SKU_QUEUE_SN + operationId.toString() + insideContainerId.toString() + skuId.toString());
                Double value = 0.0;
                if (!StringUtils.isEmpty(cacheValue)) {
                    value = new Double(cacheValue).doubleValue();
                    if (value.doubleValue() < (scanSkuQty - 1)) {
                        result = true;
                    }
                } else { // 第一次扫描
                    if (isContinueScanSn) {
                        throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                    } else {
                        result = true;
                    }
                }
            } else {
                String cacheValue = cacheManager.getValue(CacheConstants.SCAN_SKU_QUEUE_SN + operationId.toString() + locationId.toString() + skuId.toString());
                Double value = 0.0;
                if (!StringUtils.isEmpty(cacheValue)) {
                    value = new Double(cacheValue).doubleValue();
                    if (value.doubleValue() < (scanSkuQty - 1)) {
                        result = true;
                    }
                } else {
                    if (isContinueScanSn) {
                        throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                    } else {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 进入拣货作业时,如果缓存，存在先清楚
     * 
     * @param workId
     */
    public void removeCache(Long workId, Long ouId) {
        // 根据工作Id和ouId获取作业信息
        WhOperationCommand WhOperationCommand = whOperationManager.findOperationByWorkId(workId, ouId);
        if (null == WhOperationCommand) {
            throw new BusinessException(ErrorCodes.OPATION_NO_EXIST);
        }
        Long operationId = WhOperationCommand.getId();
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
        if (null != operatorLine) {
            Map<Long, Set<Long>> operSkuIds = operatorLine.getSkuIds(); // 散装sku
            Map<Long, Set<Long>> locInsideContainerIds = operatorLine.getInsideContainerIds(); // 库位上所有的内部容器
            Map<Long, Set<Long>> insideSkuIds = operatorLine.getInsideSkuIds(); // 内部容器对应所有sku
            Map<Long, Set<Long>> outerInsideId = operatorLine.getOuterToInside();
            Map<Long, Set<Long>> locOuterContainerIds = operatorLine.getOuterContainerIds();
            List<Long> locationIds = operatorLine.getLocationIds();
            for (Long locationId : locationIds) {
                // 先删除托盘上的
                if (null != locOuterContainerIds && locOuterContainerIds.size() != 0) {
                    Set<Long> outerContainerIds = locOuterContainerIds.get(locationId);
                    if (null != outerContainerIds) {
                        for (Long outerId : outerContainerIds) {
                            Set<Long> insideIds = outerInsideId.get(outerId);
                            // 先清楚内部容器的sku
                            for (Long insideId : insideIds) {
                                Set<Long> skuIds = insideSkuIds.get(insideId); // 当前内部容器内sku所有的sku
                                for (Long skuId : skuIds) {
                                    cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + insideId.toString() + skuId);
                                    cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + operationId.toString() + insideId.toString() + skuId.toString());
                                    cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + operationId.toString() + insideId.toString() + skuId.toString());
                                    cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_LATTICE_NO + operationId.toString() + insideId.toString() + skuId.toString());
                                }
                                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + operationId.toString() + insideId.toString());
                            }
                            cacheManager.removeMapValue(CacheConstants.PDA_PICKING_SHORTPICKING_SKU, operationId.toString() + outerId.toString());
                        }
                    }
                }

                // 在删库位上的货箱
                if (null != locInsideContainerIds && locInsideContainerIds.size() != 0) {
                    Set<Long> insideIds = locInsideContainerIds.get(locationId);
                    if (null != insideIds) {
                        // 先清楚内部容器的sku
                        for (Long insideId : insideIds) {
                            Set<Long> skuIds = insideSkuIds.get(insideId); // 当前内部容器内sku所有的sku
                            for (Long skuId : skuIds) {
                                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + insideId.toString() + skuId);
                                cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + operationId.toString() + insideId.toString() + skuId.toString());
                                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + operationId.toString() + insideId.toString() + skuId.toString());
                                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_LATTICE_NO + operationId.toString() + insideId.toString() + skuId.toString());
                            }
                            cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + operationId.toString() + insideId.toString());
                            cacheManager.removeMapValue(CacheConstants.PDA_PICKING_SHORTPICKING_SKU, operationId.toString() + insideId.toString());
                        }
                    }
                }

                // 散装sku
                if (null != operSkuIds && operSkuIds.size() != 0) {
                    Set<Long> locSkuIds = operSkuIds.get(locationId);
                    if (null != locSkuIds) {
                        for (Long skuId : locSkuIds) {
                            cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_SN + operationId.toString() + locationId.toString() + skuId);
                            cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + operationId.toString() + locationId.toString() + skuId.toString());
                            cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + operationId.toString() + locationId.toString() + skuId.toString());
                            cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_LATTICE_NO + operationId.toString() + locationId.toString() + skuId.toString());
                        }
                    }
                }
                cacheManager.remove(CacheConstants.PDA_PICKING_SCAN_SKU_QUEUE + operationId.toString() + locationId.toString());
                cacheManager.remove(CacheConstants.CACHE_LOC_INVENTORY + operationId.toString() + locationId.toString()); // 单个库位的缓存
                cacheManager.remove(CacheConstants.CACHE_LOCATION + operationId.toString() + locationId.toString());


            }
            // 清楚作业明细
            cacheManager.remove(CacheConstants.OPERATION_LINE + operationId.toString());
            cacheManager.remove(CacheConstants.CACHE_OPERATION_LINE + operationId.toString());
            cacheManager.remove(CacheConstants.OPERATIONLINE_STATISTICS + operationId.toString());
            cacheManager.remove(CacheConstants.CACHE_LATTICE_NO + operationId.toString());
        }

    }

    @Override
    public PickingScanResultCommand toWholeCase(PickingScanResultCommand command, Boolean isTabbInvTotal, String operationWay) {
        PickingScanResultCommand resultCmd = new PickingScanResultCommand();
        if(!command.getTipOuterContainerCode().isEmpty()){
            if(2 == command.getTempReplenishWay() && 5 != command.getPalletPickingMode()){
                resultCmd = this.palletPickingOperationExecLine(command, isTabbInvTotal);    
            }else if(3 == command.getTempReplenishWay() && 5 != command.getContainerPickingMode()){
                resultCmd = this.containerPickingOperationExecLine(command, isTabbInvTotal);    
            } else{
                // 提示内部容器
                resultCmd = this.tipInsideContainer(command);
            }
        }
        if(!command.getTipInsideContainerCode().isEmpty()){
            if(2 == command.getTempReplenishWay() && 5 != command.getPalletPickingMode()){
                resultCmd = this.palletPickingOperationExecLine(command, isTabbInvTotal);    
            }else if(3 == command.getTempReplenishWay() && 5 != command.getContainerPickingMode()){
                resultCmd = this.containerPickingOperationExecLine(command, isTabbInvTotal);    
            } else{
                // 提示SKU
                resultCmd = this.tipSku(command,Constants.REPLENISHMENT_PICKING_INVENTORY);    
            }
        }
        return resultCmd;
    }

}
