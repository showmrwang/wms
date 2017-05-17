package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.sac.command.JoinPkCommand;
import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WhWaveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.ReplenishmentTaskStatus;
import com.baozun.scm.primservice.whoperation.constant.WavePhase;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.constant.WorkStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveMasterDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AllocateStrategyDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentTaskDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhWorkLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryAllocatedDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy.DistributionModeArithmeticManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMasterPrintCondition;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.ReplenishmentTask;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWork;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhWorkLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryAllocated;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;

@Service("whWaveManager")
@Transactional
public class WhWaveManagerImpl extends BaseManagerImpl implements WhWaveManager {

    private static final Logger log = LoggerFactory.getLogger(WhWaveManagerImpl.class);

    @Autowired
    private WhWaveDao whWaveDao;
    @Autowired
    private WhWaveLineDao whWaveLineDao;
    @Autowired
    private UomDao uomDao;
    @Autowired
    private WhWaveMasterDao whWaveMasterDao;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private AllocateStrategyDao allocateStrategyDao;
    @Autowired
    private WhWaveLineManager whWaveLineManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private WhDistributionPatternRuleDao whDistributionPatternRuleDao;
    @Autowired
    private DistributionModeArithmeticManagerProxy distributionModeArithmeticManagerProxy;
    @Autowired
    private WhWorkDao whWorkDao;
    @Autowired
    private WhWorkLineDao whWorkLineDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhSkuInventoryAllocatedDao whSkuInventoryAllocatedDao;
    @Autowired
    private WhSkuInventoryTobefilledDao whSkuInventoryTobefilledDao;
    @Autowired
    private ReplenishmentTaskDao replenishmentTaskDao;
    @Autowired
    private WhOdoTransportMgmtDao whOdoTransportMgmtDao;
    @Autowired
    private PkManager pkManager;
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private WarehouseManager warehouseManager;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWave getWaveByIdAndOuId(Long waveId, Long ouId) {
        if (null == waveId || null == ouId) {
            throw new BusinessException("软分配 : 没有参数");
        }
        WhWave whWave = new WhWave();
        whWave.setId(waveId);
        whWave.setOuId(ouId);
        whWave.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        whWave.setAllocatePhase(null);
        List<WhWave> whWaveList = this.whWaveDao.findListByParam(whWave);
        if (null == whWaveList || 1 != whWaveList.size()) {
            throw new BusinessException("多个波次");
        }
        return whWaveList.get(0);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<SoftAllocationCommand> getSkuInvTotalQty(Long waveId, Long ouId) {
        if (null == waveId || null == ouId) {
            throw new BusinessException("软分配 : 没有参数");
        }
        List<SoftAllocationCommand> commandList = this.whWaveDao.getSkuInvTotalQty(waveId, ouId);
        return commandList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<SoftAllocationCommand> getSkuInvOccupiedQty(Long waveId, Long ouId) {
        if (null == waveId || null == ouId) {
            throw new BusinessException("软分配 : 没有参数");
        }
        List<SoftAllocationCommand> commandList = this.whWaveDao.getSkuInvOccupiedQty(waveId, ouId);
        return commandList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateWaveAfterSoftAllocate(WhWave wave, Long ouId) {
        // WhWave whWave = this.whWaveDao.calculateQuantity(waveId, ouId);

        List<SoftAllocationCommand> commandList = this.whWaveLineDao.findWaveLineCommandByWaveIdAndStatus(wave.getId(), ouId, BaseModel.LIFECYCLE_NORMAL);

        if (null == commandList || commandList.isEmpty()) {
            // 如果没有找到波次明细,说明明细都已被剔除;关闭波次
            wave.setPhaseCode(null);
            wave.setStatus(WaveStatus.WAVE_CANCEL);
            int num = this.whWaveDao.saveOrUpdateByVersion(wave);
            if (1 != num) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            return;
        }

        Integer totalOdoQty = 0; // 出库单总数
        Integer totalOdoLineQty = 0;// 出库单明细行总数
        Double totalAmount = 0.0;// 总金额
        Double totalVolume = 0.0;// 总体积
        Double totalWeight = 0.0;// 总重量
        Double totalSkuQty = 0.0;// 商品总件数
        Integer skuCategoryQty = 0;// 商品类型总数
        Set<Long> skuCategorySet = new HashSet<Long>();
        Set<Long> odoSet = new HashSet<Long>();
        Set<Long> odoLineSet = new HashSet<Long>();
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> weightUomCmds = this.uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
            }
        }

        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> volumnUomCmds = this.uomDao.findUomByGroupCode(WhUomType.VOLUME_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : volumnUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        for (SoftAllocationCommand command : commandList) {
            Sku sku = command.getSku();
            WhWaveLine whWaveLine = command.getWhWaveLine();
            WhOdoLine whOdoLine = command.getWhOdoLine();
            Long skuId = sku.getId();
            Double planQty = whOdoLine.getPlanQty();
            // skuCategorySet记录商品类型
            skuCategorySet.add(skuId);
            // odoSet记录出库单总数
            odoSet.add(whOdoLine.getOdoId());
            // odoLineSet记录出库单明细行总数
            odoLineSet.add(whOdoLine.getId());
            // 累计计算总金额
            totalAmount += whWaveLine.getQty() * whOdoLine.getLinePrice();
            // 累计计算商品总件数
            totalSkuQty += whWaveLine.getQty();

            totalVolume += sku.getVolume() * planQty * (StringUtils.isEmpty(sku.getVolumeUom()) ? 1 : lenUomConversionRate.get(sku.getVolumeUom()));
            totalWeight += sku.getWeight() * planQty * (StringUtils.isEmpty(sku.getWeightUom()) ? 1 : weightUomConversionRate.get(sku.getWeightUom()));
        }

        totalOdoQty = odoSet.size();
        totalOdoLineQty = odoLineSet.size();
        skuCategoryQty = skuCategorySet.size();


        // 获取下一个波次阶段
        WhWaveMaster whWaveMaster = whWaveMasterDao.findByIdExt(wave.getWaveMasterId(), ouId);
        Long waveTempletId = whWaveMaster.getWaveTemplateId();
        String phaseCode = this.getWavePhaseCode(wave.getPhaseCode(), waveTempletId, ouId);
        wave.setPhaseCode(phaseCode);

        wave.setTotalOdoQty(totalOdoQty);
        wave.setTotalOdoLineQty(totalOdoLineQty);
        wave.setTotalAmount(totalAmount);
        // wave.setTotalVolume(whWave.getTotalVolume());
        // wave.setTotalWeight(whWave.getTotalWeight());
        wave.setTotalVolume(totalVolume);
        wave.setTotalWeight(totalWeight);
        wave.setTotalSkuQty(totalSkuQty);
        wave.setSkuCategoryQty(skuCategoryQty);
        wave.setFinishTime(new Date());
        this.whWaveDao.saveOrUpdateByVersion(wave);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WaveCommand> findWaveListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<WaveCommand> pages = this.whWaveDao.findListByQueryMapWithPageExt(page, sorts, params);
        if (pages != null && pages.getItems() != null && pages.getItems().size() > 0) {

            Set<String> dic1 = new HashSet<String>();// 波次状态
            Set<String> dic2 = new HashSet<String>();// 波次阶段

            List<WaveCommand> list = pages.getItems();
            for (WaveCommand wave : list) {
                if (wave.getStatus() != null) {
                    dic1.add(wave.getStatus() + "");
                }
                if (StringUtils.hasText(wave.getPhaseCode())) {
                    dic2.add(wave.getPhaseCode());
                }
            }
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            if (dic1.size() > 0) {

                map.put(Constants.WAVE_STATUS, new ArrayList<String>(dic1));
            }
            if (dic2.size() > 0) {
                map.put(Constants.WH_WAVE_PHASE, new ArrayList<String>(dic2));
            }

            Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);
            for (WaveCommand wave : list) {
                if (wave.getStatus() != null) {
                    SysDictionary sys = dicMap.get(Constants.WAVE_STATUS + "_" + wave.getStatus());
                    wave.setStatusName(sys == null ? (wave.getStatus() + "") : sys.getDicLabel());
                }
                if (StringUtils.hasText(wave.getPhaseCode())) {
                    SysDictionary sys = dicMap.get(Constants.WH_WAVE_PHASE + "_" + wave.getPhaseCode());
                    wave.setPhaseName(sys == null ? wave.getPhaseCode() : sys.getDicLabel());
                }
            }

        }
        return pages;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteWave(WhWave wave, List<WhWaveLine> waveLineList, List<WhOdo> odoList, List<WhOdoLine> odoLineList, Long userId) {
        Long ouId = wave.getOuId();
        for (WhWaveLine waveLine : waveLineList) {
            this.whWaveLineDao.deleteByIdOuId(waveLine.getId(), ouId);
        }
        this.whWaveDao.deleteByIdOuId(wave.getId(), ouId);
        for (WhOdo odo : odoList) {
            odo.setWaveCode(null);
            // #TODO适用于整单出库逻辑
            odo.setOdoStatus(OdoStatus.NEW);
            this.whOdoDao.saveOrUpdateByVersion(odo);
        }
        for (WhOdoLine line : odoLineList) {
            line.setWaveCode(null);
            line.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
            this.whOdoLineDao.saveOrUpdateByVersion(line);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWave updateWaveForSoftStart(WhWave whWave) {
        whWave.setStatus(WaveStatus.WAVE_EXECUTING);
        whWave.setIsWeakAllocated(true);
        whWave.setStartTime(new Date());
        return whWave;
        // int cnt = this.whWaveDao.saveOrUpdateByVersion(whWave);
        // if (0 >= cnt) {
        // throw new BusinessException("软分配开始阶段-更新波次信息失败");
        // }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhDistributionPatternRuleCommand> findRuleByOuIdOrderByPriorityAsc(Long ouId) {
        return this.whDistributionPatternRuleDao.findRuleByOuIdOrderByPriorityAsc(ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> findOdoListInWaveWhenDistributionPattern(Long waveId, Long ouId, String ruleSql) {
        return this.whDistributionPatternRuleDao.testRuleSql(ruleSql, ouId, waveId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> findWaveByPhase(String phaseCode, Long ouId) {
        WhWave whWave = new WhWave();
        whWave.setOuId(ouId);
        whWave.setPhaseCode(phaseCode);
        whWave.setAllocatePhase(null);
        whWave.setIsRunWave(true);
        whWave.setStatus(WaveStatus.WAVE_EXECUTING);
        List<Long> waveIds = this.whWaveDao.findWaveIdsByParam(whWave);
        return waveIds;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OdoMergeCommand> findWaveMergeOdo(Long waveId, Long ouId) {
        WaveCommand command = this.whWaveDao.findWaveByIdAndOuId(waveId, ouId);
        String waveCode = command.getCode();
        String outboundCartonType = command.getNeedOutboundCartonType();
        String epistaticSystemsOrderType = command.getNeedEpistaticSystemsOrderType();
        String store = command.getNeedStore();
        String deliverGoodsTime = command.getNeedDeliverGoodsTime();
        // 所有可以合并的单子
        List<String> idString = this.whOdoDao.findWaveOdoMergableIds(waveCode, ouId, outboundCartonType, epistaticSystemsOrderType, store, deliverGoodsTime);
        String idStr = "";
        if (null != idString && !idString.isEmpty()) {
            for (String id : idString) {
                idStr += id + ",";
            }
            idStr = idStr.substring(0, idStr.length() - 1);
        }
        String odoIds = "(" + idStr + ")";
        // String odoIds = "(" + this.whOdoDao.findWaveOdoMergableIds(waveCode, ouId,
        // outboundCartonType, epistaticSystemsOrderType, store, deliverGoodsTime) + ")";
        List<OdoMergeCommand> list = new ArrayList<OdoMergeCommand>();
        if (StringUtils.hasText(odoIds) && !"()".equals(odoIds)) {
            list = this.whOdoDao.odoMerge(OdoStatus.WAVE, odoIds, ouId, outboundCartonType, epistaticSystemsOrderType, store, deliverGoodsTime);
        }
        return list;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void changeWavePhaseCode(Long waveId, Long ouId) {
        WhWave wave = new WhWave();
        wave.setId(waveId);
        wave.setOuId(ouId);
        wave.setIsRunWave(true);
        wave.setAllocatePhase(null);
        List<WhWave> list = this.whWaveDao.findListByParam(wave);
        wave = (null == list || list.isEmpty()) ? null : list.get(0);
        if (null != wave) {
            // 查询是否还有波次明细
            WhWaveLine line = new WhWaveLine();
            line.setWaveId(waveId);
            line.setOuId(ouId);
            long lineCount = whWaveLineDao.findListCountByParam(line);
            if (lineCount == 0) {
                wave.setPhaseCode(null);
                wave.setStatus(WaveStatus.WAVE_CANCEL);
                int num = this.whWaveDao.saveOrUpdateByVersion(wave);
                if (1 != num) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                return;
            }
            // 获取下一个波次阶段
            WhWaveMaster whWaveMaster = whWaveMasterDao.findByIdExt(wave.getWaveMasterId(), ouId);
            Long waveTempletId = whWaveMaster.getWaveTemplateId();
            String phase = this.getWavePhaseCode(wave.getPhaseCode(), waveTempletId, ouId);
            if (!StringUtils.isEmpty(phase)) {
                wave.setPhaseCode(phase);
                wave.setStatus(WaveStatus.WAVE_EXECUTING);
                int num = this.whWaveDao.saveOrUpdateByVersion(wave);
                if (1 != num) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> getNeedAllocationRuleWhWave(Integer allocatePhase, Long ouId, String logId) {
        List<Long> datas = whWaveDao.getNeedAllocationRuleWhWave(allocatePhase, ouId);
        return datas;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateWhWaveAllocatePhase(List<Long> waveIdList, Integer allocatePhase, Long ouId) {
        int num = whWaveDao.updateWhWaveAllocatePhase(waveIdList, allocatePhase, ouId);
        return num;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void checkWaveHardAllocateEnough(Long waveId, Warehouse wh) {
        Long ouId = wh.getId();
        WhWaveLine line = new WhWaveLine();
        line.setWaveId(waveId);
        line.setOuId(ouId);
        long lineCount = whWaveLineDao.findListCountByParam(line);
        if (lineCount == 0) {
            WhWave wave = whWaveDao.findWaveExtByIdAndOuId(waveId, ouId);
            wave.setPhaseCode(null);
            wave.setStatus(WaveStatus.WAVE_CANCEL);
            int num = this.whWaveDao.saveOrUpdateByVersion(wave);
            if (1 != num) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            return;
        }

        List<WhWaveLine> lines = whWaveLineDao.findNotEnoughAllocationQty(waveId, ouId);
        // 获取下一个波次阶段编码
        WhWave wave = whWaveDao.findWaveExtByIdAndOuId(waveId, ouId);
        if (null == wave) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        WhWaveMaster whWaveMaster = whWaveMasterDao.findByIdExt(wave.getWaveMasterId(), ouId);
        Long waveTempletId = whWaveMaster.getWaveTemplateId();
        String phaseCode = this.getWavePhaseCode(wave.getPhaseCode(), waveTempletId, ouId);
        if (null == lines || lines.isEmpty()) {
            // 如果是补货阶段,则跳过
            if (WavePhase.REPLENISHED.equals(phaseCode)) {
                phaseCode = this.getWavePhaseCode(phaseCode, waveTempletId, ouId);
            }
            wave.setPhaseCode(phaseCode);
            int num = whWaveDao.saveOrUpdateByVersion(wave);
            if (num != 1) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } else {
            // key:分配规则Id, value:规则下对应的策略
            Map<Long, List<String>> ruleMap = new HashMap<Long, List<String>>();
            // 库存数量没有分配完整的波次明细中包含的所有的出库单Id
            Set<Long> allOdoIds = new HashSet<Long>();
            // 库存数量没有分配完整的波次明细中不包含静态库位超分配和空库位的出库单Id
            Set<Long> odoIds = new HashSet<Long>();
            // 判断分配策略是否包含静态库位可超分配或者空库位
            for (WhWaveLine whWaveLine : lines) {
                Long ruleId = whWaveLine.getAllocateRuleId();
                List<String> strategyCodes = ruleMap.get(ruleId);
                if (null == strategyCodes) {
                    strategyCodes = allocateStrategyDao.findAllocateStrategyCodeByRuleId(ruleId, ouId);
                    ruleMap.put(ruleId, strategyCodes);
                }
                if (!strategyCodes.contains(Constants.ALLOCATE_STRATEGY_STATICLOCATIONCANASSIGNMENT) && !strategyCodes.contains(Constants.ALLOCATE_STRATEGY_EMPTYLOCATION)) {
                    odoIds.add(whWaveLine.getOdoId());
                }
                allOdoIds.add(whWaveLine.getOdoId());
            }
            // 如果是补货阶段,则进入补货阶段
            if (WavePhase.REPLENISHED.equals(phaseCode)) {
                if (odoIds.isEmpty()) {
                    wave.setPhaseCode(phaseCode);
                    int num = whWaveDao.saveOrUpdateByVersion(wave);
                    if (1 != num) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                } else {
                    // 剔除规则中没有静态库位可超分配或空库位的工作单
                    whWaveLineManager.deleteWaveLinesByOdoIdList(new ArrayList<Long>(odoIds), waveId, ouId, Constants.INVENTORY_SHORTAGE);
                    for (Long odoId : odoIds) {
                        // 释放库存
                        whSkuInventoryManager.releaseInventoryByOdoId(odoId, wh);
                    }
                    // 从波次中剔除出库单后更新波次头统计信息
                    this.calculateWaveHeadInfo(waveId, ouId);
                    // 计算是否进入补货
                    this.changeWaveByHardAllocation(waveId, waveTempletId, phaseCode, ouId);
                }
            } else {
                if (!allOdoIds.isEmpty()) {
                    // 剔除库存数量没有分配完全所有工作单
                    whWaveLineManager.deleteWaveLinesByOdoIdList(new ArrayList<Long>(allOdoIds), waveId, ouId, Constants.INVENTORY_SHORTAGE);
                    for (Long odoId : allOdoIds) {
                        // 释放库存
                        whSkuInventoryManager.releaseInventoryByOdoId(odoId, wh);
                    }
                    // 从波次中剔除出库单后更新波次头统计信息
                    this.calculateWaveHeadInfo(waveId, ouId);
                }
                // 波次进入到下个阶段
                this.changeWavePhaseCode(waveId, ouId);
            }
        }
        // 回写odoLine的分配数量
        WhWaveLine waveLine = new WhWaveLine();
        waveLine.setWaveId(waveId);
        waveLine.setOuId(ouId);
        List<WhWaveLine> waveLines = whWaveLineDao.findListByParam(waveLine);
        // 记录波次内现有出库单idList
        for (WhWaveLine wavelines : waveLines) {
            WhOdoLine odoLine = whOdoLineDao.findOdoLineById(wavelines.getOdoLineId(), ouId);
            odoLine.setAssignQty(wavelines.getAllocateQty());
            odoLine.setAssignFailReason(null);
            odoLine.setIsAssignSuccess(true);
            whOdoLineDao.saveOrUpdate(odoLine);
        }
    }

    /**
     * 计算是否进入补货阶段
     */
    private void changeWaveByHardAllocation(Long waveId, Long waveTempletId, String phaseCode, Long ouId) {
        WhWave wave = whWaveDao.findWaveExtByIdAndOuId(waveId, ouId);
        WhWaveLine line = new WhWaveLine();
        line.setWaveId(waveId);
        line.setOuId(ouId);
        long lineCount = whWaveLineDao.findListCountByParam(line);
        if (lineCount == 0) {
            wave.setPhaseCode(null);
            wave.setStatus(WaveStatus.WAVE_CANCEL);
            int num = this.whWaveDao.saveOrUpdateByVersion(wave);
            if (1 != num) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            return;
        }
        lineCount = whWaveLineDao.countNotEnoughAllocationQty(waveId, ouId);
        if (lineCount == 0) {
            phaseCode = this.getWavePhaseCode(phaseCode, waveTempletId, ouId);
            wave.setPhaseCode(phaseCode);
            int num = whWaveDao.saveOrUpdateByVersion(wave);
            if (num != 1) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            return;
        }
        wave.setPhaseCode(phaseCode);
        int num = whWaveDao.saveOrUpdateByVersion(wave);
        if (num != 1) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    /**
     * 从波次中剔除出库单后重新计算波次头统计信息
     */
    public void calculateWaveHeadInfo(Long waveId, Long ouId) {
        List<Long> odoIdList = whWaveLineDao.findOdoIdByWaveId(waveId, ouId);
        WaveCommand waveHeadInfo = odoManager.findWaveSumDatabyOdoIdList(odoIdList, ouId);
        WhWave wave = whWaveDao.findWaveExtByIdAndOuId(waveId, ouId);
        wave.setSkuCategoryQty(waveHeadInfo.getSkuCategoryQty());
        wave.setTotalVolume(waveHeadInfo.getTotalVolume());
        wave.setTotalWeight(waveHeadInfo.getTotalWeight());
        wave.setTotalOdoLineQty(waveHeadInfo.getTotalOdoLineQty());
        wave.setTotalAmount(waveHeadInfo.getTotalAmount());
        wave.setTotalSkuQty(waveHeadInfo.getTotalSkuQty());
        int updateCount = whWaveDao.saveOrUpdateByVersion(wave);
        if (updateCount == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void matchWaveDisTributionMode(List<WhOdo> odoList, WhWave wave, Long ouId, Long userId, Warehouse wh) {
        // #mender yimin.lu 获取需要提出波次的出库单集合
        Long waveId = wave.getId();
        // 更新出库单
        boolean flag = false;
        if (odoList != null && odoList.size() > 0) {
            for (WhOdo odo : odoList) {
                if (StringUtils.hasText(odo.getDistributeMode())) {
                    int updateCount = this.whOdoDao.saveOrUpdateByVersion(odo);
                    if (updateCount <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                } else {
                    this.deleteWaveLinesFromWaveByWavePhase(wave.getId(), odo.getId(), Constants.DISTRIBUTE_MODE_FAIL, wh, WavePhase.DISTRIBUTION_NUM);
                    if (!flag) {

                        flag = true;
                    }
                }
            }
        }
        if (flag) {
            this.calculateWaveHeadInfo(waveId, ouId);
        }
        // 更新波次的下一个阶段
        this.changeWavePhaseCode(waveId, ouId);
        // 波次取消需要取消补货任务
        this.checkReplenishmentTaskForWave(waveId, ouId);
    }

    public void checkReplenishmentTaskForWave(Long waveId, Long ouId) {
        WaveCommand wave = this.whWaveDao.findWaveByIdAndOuId(waveId, ouId);
        if (wave == null) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (WaveStatus.WAVE_CANCEL == wave.getStatus()) {
            ReplenishmentTask task = new ReplenishmentTask();
            task.setOuId(ouId);
            task.setWaveId(waveId);
            task.setStatus(ReplenishmentTaskStatus.REPLENISHMENT_TASK_NEW);
            List<ReplenishmentTask> rtList = this.replenishmentTaskDao.findListByParam(task);
            if (rtList != null && rtList.size() > 0) {
                for (ReplenishmentTask rt : rtList) {
                    WhSkuInventoryTobefilled tbfInv = new WhSkuInventoryTobefilled();
                    tbfInv.setOuId(ouId);
                    tbfInv.setReplenishmentCode(rt.getReplenishmentCode());
                    List<WhSkuInventoryTobefilled> tbfInvList = this.whSkuInventoryTobefilledDao.findskuInventoryTobefilleds(tbfInv);
                    if (tbfInvList == null || tbfInvList.size() == 0) {
                        rt.setStatus(ReplenishmentTaskStatus.REPLENISHMENT_TASK_CANCEL);
                        this.replenishmentTaskDao.saveOrUpdateByVersion(rt);
                    }
                }
            }
        }
    }

    @Override
    public void releaseInventoryByWaveId(Long waveId, Warehouse wh) {
        Long ouId = wh.getId();
        List<Long> odoIds = whWaveLineDao.findOdoIdByWaveId(waveId, ouId);
        for (Long odoId : odoIds) {
            // 释放库存
            whSkuInventoryManager.releaseInventoryByOdoId(odoId, wh);
        }
    }

    @Override
    public List<Long> findWaveIdsByWavePhaseCode(String phaseCode, Long ouId) {
        WhWave wave = new WhWave();
        wave.setPhaseCode(phaseCode);
        wave.setOuId(ouId);
        wave.setAllocatePhase(null);
        return whWaveDao.findWaveIdsByParam(wave);
    }

    @Override
    public void deleteWaveLinesAndReleaseInventoryByOdoIdList(Long waveId, Collection<Long> odoIds, String reason, Warehouse wh) {
        this.deleteWaveLinesAndReleaseInventoryByOdoIdList(waveId, odoIds, reason, wh, WavePhase.ALLOCATED_NUM);
    }

    private void deleteWaveLinesAndReleaseInventoryByOdoIdList(Long waveId, Collection<Long> odoIds, String reason, Warehouse wh, Integer wavePhase) {
        Long ouId = wh.getId();
        for (Long odoId : odoIds) {
            this.deleteWaveLinesFromWaveByWavePhase(waveId, odoId, reason, wh, wavePhase);
        }
        this.calculateWaveHeadInfo(waveId, ouId);
    }

    @Override
    public void deleteWaveLinesAndReleaseInventoryByOdoId(Long waveId, Long odoId, String reason, Warehouse wh) {
        log.info("releaseOdoFromWave, odoId:[{}],waveId:[{}],ouId:[{}]", odoId, waveId, wh.getId());
        this.deleteWaveLinesFromWaveByWavePhase(waveId, odoId, reason, wh, WavePhase.ALLOCATED_NUM);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteWaveLinesFromWaveByWavePhase(Long waveId, Long odoId, String reason, Warehouse wh, Integer wavePhase) {
        if (null == waveId || null == odoId || StringUtils.isEmpty(reason) || null == wh) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        Long ouId = wh.getId();
        // 共用删除逻辑
        // 1.修改出库单明细waveCode为空
        // 2.修改出库单waveCode为空
        // 3.从波次明细中剔除出库单
        // 4.出库单重新计算配货模式
        WhOdo odo = whWaveLineManager.deleteWaveLinesByOdoId(odoId, waveId, ouId, reason);
        if (null == wavePhase) {
            return;
        }
        // @mender yimin.lu 2017/5/9
        // 库存的操作逻辑：
        this.releaseInventory(waveId, odoId, wavePhase, odo.getOdoCode(), wh);
        // 其余额外的操作逻辑
        /** 软分配剔除逻辑 */
        if (wavePhase.intValue() >= WavePhase.WEAK_ALLOCATED_NUM) {}
        /** 合并出库单剔除逻辑 */
        if (wavePhase.intValue() >= WavePhase.MERGE_ODO_NUM) {}
        /** 硬分配剔除逻辑 */
        if (wavePhase.intValue() >= WavePhase.ALLOCATED_NUM) {
        }
        /** 补货剔除逻辑 */
        if (wavePhase.intValue() >= WavePhase.REPLENISHED_NUM) {
        }
        /** 配货模式计算剔除逻辑 */
        if (wavePhase.intValue() >= WavePhase.DISTRIBUTION_NUM) {}
        /** 创建出库箱/容器剔除逻辑 */
        if (wavePhase.intValue() >= WavePhase.CREATE_OUTBOUND_CARTON_NUM) {}
        /** 创建工作剔除逻辑 */
        if (wavePhase.intValue() >= WavePhase.CREATE_WORK_NUM) {}
    }



    private void releaseInventory(Long waveId, Long odoId, Integer wavePhase, String odoCode, Warehouse wh) {
        Long ouId = wh.getId();
        /** 硬分配剔除逻辑 */
        if (wavePhase.intValue() >= WavePhase.ALLOCATED_NUM) {
            // 出库单释放库存
            whSkuInventoryManager.releaseInventoryByOccupyCode(odoCode, wh);
            // 修改出库单明细已分配数量
            whOdoLineDao.updateOdoLineAssignQtyIsZero(odoId, wh.getId());
        }
        /** 补货剔除逻辑 */
        if (wavePhase.intValue() >= WavePhase.REPLENISHED_NUM) {
            WhWave wave = whWaveDao.findWaveExtByIdAndOuId(waveId, ouId);
            // 删除已分配库存
            List<WhSkuInventoryAllocated> skuInvAllocatedList = this.whSkuInventoryAllocatedDao.findbyOccupationCode(odoCode, ouId);
            if (skuInvAllocatedList != null && skuInvAllocatedList.size() > 0) {
                for (WhSkuInventoryAllocated invAllocated : skuInvAllocatedList) {
                    invAllocated.setOccupationCode(null);
                    invAllocated.setOccupationLineId(null);
                    int updateCount = this.whSkuInventoryAllocatedDao.saveOrUpdateByVersion(invAllocated);
                    if (updateCount <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
            // 删除待移入库存
            List<WhSkuInventoryTobefilled> skuInvTobefilledList = this.whSkuInventoryTobefilledDao.findbyOccupationCode(odoCode, ouId);
            if (skuInvTobefilledList != null && skuInvTobefilledList.size() > 0) {
                for (WhSkuInventoryTobefilled invTobefilled : skuInvTobefilledList) {
                    invTobefilled.setOccupationCode(null);
                    invTobefilled.setOccupationLineId(null);
                    int updateCount = this.whSkuInventoryTobefilledDao.saveOrUpdateByVersion(invTobefilled);
                    if (updateCount <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
        }

    }

    @Override
    public List<Long> findOdoContainsSkuId(Long waveId, List<Long> skuIds, Long ouId) {
        return whWaveDao.findOdoContainsSkuId(waveId, skuIds, ouId);
    }

    @Override
    public List<Long> getNeedSoftAllocationWhWave(Long ouId) {
        List<Long> WaveIds = whWaveDao.getWhWaveListByPhaseCode(Constants.WEAK_ALLOCATED, ouId);
        return WaveIds;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> getNeedPickingWorkWhWave(Long ouId) {
        List<Long> WaveIds = whWaveDao.getNeedPickingWorkWhWave(WaveStatus.WAVE_EXECUTING, WavePhase.CREATE_WORK, ouId);
        return WaveIds;
    }

    @Override
    public String getNextParseCode(Long waveId, Long ouId) {
        WhWave wave = new WhWave();
        wave.setId(waveId);
        wave.setOuId(ouId);
        wave.setAllocatePhase(null);
        wave = this.whWaveDao.findListByParam(wave).get(0);
        WhWaveMaster whWaveMaster = whWaveMasterDao.findByIdExt(wave.getWaveMasterId(), ouId);
        Long waveTempletId = whWaveMaster.getWaveTemplateId();
        String phaseCode = this.getWavePhaseCode(wave.getPhaseCode(), waveTempletId, ouId);
        return phaseCode;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWaveMaster findWaveMasterbyIdOuId(Long waveMasterId, Long ouId) {
        return this.whWaveMasterDao.findByIdExt(waveMasterId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWave> findWaveNotRunning(Long ouId) {
        WhWave wave = new WhWave();
        wave.setOuId(ouId);
        wave.setIsRunWave(false);
        wave.setAllocatePhase(null);
        wave.setLifecycle(Constants.LIFECYCLE_START);
        List<WhWave> list = this.whWaveDao.findListByParam(wave);
        return list;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void startWave(WhWave wave) {
        wave.setIsRunWave(true);
        wave.setStatus(WaveStatus.WAVE_EXECUTING);
        wave.setStartTime(new Date());
        int updateCount = this.whWaveDao.saveOrUpdateByVersion(wave);
        if (updateCount <= 0) {
            log.error("update wave[id:{}] error", wave.getId());
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWave findWaveByIdOuId(Long id, Long ouId) {
        return this.whWaveDao.findWaveExtByIdAndOuId(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void divFromWaveByOdo(WhWave wave, Map<Long, List<WhWaveLine>> odoIdWaveLineMap, Long ouId, Long userId, String logId) {
        Iterator<Entry<Long, List<WhWaveLine>>> it = odoIdWaveLineMap.entrySet().iterator();
        Set<Long> odoIdSet = new HashSet<Long>();
        while (it.hasNext()) {
            Entry<Long, List<WhWaveLine>> entry = it.next();
            Long odoId = entry.getKey();
            for (WhWaveLine waveLine : entry.getValue()) {
                WhOdoLine odoLine = this.whOdoLineDao.findOdoLineById(waveLine.getOdoLineId(), ouId);
                odoLine.setWaveCode("");
                odoLine.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
                odoLine.setModifiedId(userId);
                int odoLineUpdateCount = this.whOdoLineDao.saveOrUpdateByVersion(odoLine);
                if (odoLineUpdateCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                this.whWaveLineDao.deleteByIdOuId(waveLine.getId(), ouId);
            }
            odoIdSet.add(odoId);
        }
        Map<Long, String> odoIdCounterCodeMap = new HashMap<Long, String>();
        for (Long odoId : odoIdSet) {
            WhOdo odo = this.whOdoDao.findByIdOuId(odoId, ouId);
            odoIdCounterCodeMap.put(odo.getId(), odo.getCounterCode());
            odo.setWaveCode("");
            odo.setModifiedId(userId);
            odo.setOdoStatus(OdoStatus.NEW);
            int odoupdateCount = this.whOdoDao.saveOrUpdateByVersion(odo);
            if (odoupdateCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }

        wave.setModifiedId(userId);
        int updateWaveCount = this.whWaveDao.saveOrUpdateByVersion(wave);
        if (updateWaveCount <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        this.distributionModeArithmeticManagerProxy.addToPool(odoIdCounterCodeMap);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void releaseWave(WhWave wave, List<WhWork> workList, Long ouId, Long userId) {
        try {

            for (WhWork work : workList) {
                work.setIsLocked(false);
                work.setModifiedId(userId);
                int upcount = this.whWorkDao.saveOrUpdateByVersion(work);
                if (upcount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
            List<WhOdo> odoList = this.whOdoDao.findOdoListByWaveCode(wave.getCode(), ouId);
            if (odoList != null && odoList.size() > 0) {
                for (WhOdo o : odoList) {
                    o.setOdoStatus(OdoStatus.RELEASE_WORK);
                    o.setModifiedId(userId);
                    int updateCount = this.whOdoDao.saveOrUpdateByVersion(o);
                    if (updateCount <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
            int updateCount = this.whWaveDao.saveOrUpdateByVersion(wave);
            if (updateCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void cancelWaveForNew(WhWave wave, List<WhOdo> odoList, Long ouId, Long userId) {
        Map<Long, String> odoIdCounterCodeMap = new HashMap<Long, String>();// 配货模式计算
        for (WhOdo odo : odoList) {
            odoIdCounterCodeMap.put(odo.getId(), odo.getCounterCode());

            List<WhOdoLine> odoLineList = this.whOdoLineDao.findOdoLineListByOdoIdOuId(odo.getId(), ouId);
            for (WhOdoLine odoLine : odoLineList) {
                odoLine.setModifiedId(userId);
                odoLine.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
                odoLine.setWaveCode(null);
                int updateOdoLineCount = this.whOdoLineDao.saveOrUpdateByVersion(odoLine);
                if (updateOdoLineCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }

            }
            odo.setModifiedId(userId);
            odo.setOdoStatus(OdoStatus.NEW);
            odo.setWaveCode(null);
            int updateOdoCount = this.whOdoDao.saveOrUpdateByVersion(odo);
            if (updateOdoCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        }

        int updateWaveCount = this.whWaveDao.saveOrUpdateByVersion(wave);
        if (updateWaveCount <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        // 重新计算配货模式
        this.distributionModeArithmeticManagerProxy.addToPool(odoIdCounterCodeMap);

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void cancelWaveWithWork(WhWave wave, List<WhOdo> odoList, Long ouId, Long userId) {
        Warehouse wh = this.warehouseManager.findWarehouseById(ouId);
        Long waveId = wave.getId();
        // 工作的取消：
        // 1.补货工作：不取消
        // 2.拣货工作：取消
        this.releaseReplenishmentFromWave(waveId, wave.getCode(), ouId, userId);
        this.releasePickingFromWave(wave.getCode(), ouId);

        wave.setStatus(WaveStatus.WAVE_CANCEL);
        wave.setModifiedId(userId);
        int updateWaveCount = this.whWaveDao.saveOrUpdateByVersion(wave);
        if (updateWaveCount <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 遍历所有的的出库单
        // 当出库单为需要释放的时候，则将带移入和库存都回滚，并将和波次的绑定关系释放，置出库单状态为新建；
        // 否则则将出库单延迟释放：@mender yimin.lu 2017/5/9 不存在延迟取消的出库单
        for (WhOdo odo : odoList) {
            this.deleteWaveLinesFromWaveByWavePhase(wave.getId(), odo.getId(), null, wh, WavePhase.EXECUTED);

        }

    }

    private void releasePickingFromWave(String waveCode, Long ouId) {
        WhWork workSearch = new WhWork();
        workSearch.setWaveCode(waveCode);
        workSearch.setWorkCategory(Constants.WORK_CATEGORY_PICKING);
        workSearch.setOuId(ouId);

        workSearch.setLifecycle(Constants.LIFECYCLE_START);
        List<WhWork> workList = this.whWorkDao.findListByParam(workSearch);
        if (workList != null && workList.size() > 0) {
            for (WhWork work : workList) {
                work.setStatus(WorkStatus.CANCEL);
                int updateWorkCount = this.whWorkDao.saveOrUpdateByVersion(work);
                if (updateWorkCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }

    }

    private void releaseReplenishmentFromWave(Long waveId, String waveCode, Long ouId, Long userId) {
        WhWork workSearch = new WhWork();
        workSearch.setWaveCode(waveCode);
        workSearch.setWorkCategory(Constants.WORK_CATEGORY_REPLENISHMENT);
        workSearch.setOuId(ouId);

        workSearch.setLifecycle(Constants.LIFECYCLE_START);
        List<WhWork> workList = this.whWorkDao.findListByParam(workSearch);
        if (workList != null && workList.size() > 0) {
            for (WhWork work : workList) {
                work.setWaveId(null);
                work.setWaveCode(null);
                work.setOrderCode(null);
                // #TODO
                work.setIsLocked(false);
                int updateWorkCount = this.whWorkDao.saveOrUpdateByVersion(work);
                if (updateWorkCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                List<WhWorkLine> lineList = this.whWorkLineDao.findWorkLineByWorkIdOuId(work.getId(), ouId);
                if (lineList != null && lineList.size() > 0) {
                    for (WhWorkLine workLine : lineList) {
                        workLine.setOdoId(null);
                        workLine.setOdoLineId(null);
                        int updateCount = this.whWorkLineDao.saveOrUpdateByVersion(workLine);
                        if (updateCount <= 0) {
                            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                        }
                    }
                }
            }
        }

        ReplenishmentTask taskSearch = new ReplenishmentTask();
        taskSearch.setOuId(ouId);
        taskSearch.setWaveId(waveId);
        List<ReplenishmentTask> result = this.replenishmentTaskDao.findListByParam(taskSearch);
        if (result != null && result.size() > 0) {
            for (ReplenishmentTask task : result) {
                task.setWaveId(null);// 补货工作不绑定波次
                task.setOperatorId(userId);
                int updateTaskCount = this.replenishmentTaskDao.saveOrUpdateByVersion(task);
                if (updateTaskCount <= 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWave> findWaveToBeCreated(Long ouId) {
        WhWave wave = new WhWave();
        wave.setOuId(ouId);
        wave.setStatus(WaveStatus.WAVE_TOBECREATED);
        wave.setAllocatePhase(null);
        wave.setLifecycle(Constants.LIFECYCLE_START);
        List<WhWave> list = this.whWaveDao.findListByParam(wave);
        return list;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void finishCreateWave(WhWave wave) {
        wave.setStatus(WaveStatus.WAVE_NEW);
        int waveCount = this.whWaveDao.saveOrUpdate(wave);
        if (waveCount <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void addOdoLineToWave(List<Long> odoIdList, WhWave wave) {
        Long userId = wave.getCreatedId();
        Long ouId = wave.getOuId();
        for (Long odoId : odoIdList) {
            WhOdo odo = this.whOdoDao.findByIdOuId(odoId, ouId);
            WhOdoTransportMgmt trans = this.whOdoTransportMgmtDao.findTransportMgmtByOdoIdOuId(odoId, ouId);
            List<WhOdoLine> odoLineList = this.whOdoLineDao.findOdoLineListByOdoIdOuId(odoId, ouId);
            if (odoLineList != null && odoLineList.size() > 0) {

                for (WhOdoLine line : odoLineList) {
                    WhWaveLine waveLine = new WhWaveLine();
                    waveLine.setOdoLineId(line.getId());
                    waveLine.setOdoId(line.getOdoId());
                    waveLine.setOdoCode(odo.getOdoCode());
                    waveLine.setOdoPriorityLevel(odo.getPriorityLevel());
                    waveLine.setOdoPlanDeliverGoodsTime(trans.getPlanDeliverGoodsTime());
                    waveLine.setOdoOrderTime(odo.getOrderTime());
                    waveLine.setIsStaticLocationAllocate(false);
                    waveLine.setLinenum(line.getLinenum());
                    waveLine.setStoreId(odo.getStoreId());
                    waveLine.setExtLinenum(line.getExtLinenum());
                    waveLine.setSkuId(line.getSkuId());
                    waveLine.setSkuBarCode(line.getSkuBarCode());
                    waveLine.setSkuName(line.getSkuName());
                    waveLine.setQty(line.getPlanQty());
                    waveLine.setAllocateQty(line.getAssignQty());
                    waveLine.setIsWholeOrderOutbound(line.getFullLineOutbound());
                    waveLine.setFullLineOutbound(line.getFullLineOutbound());
                    waveLine.setMfgDate(line.getMfgDate());
                    waveLine.setExpDate(line.getExpDate());
                    waveLine.setMinExpDate(line.getMinExpDate());
                    waveLine.setMaxExpDate(line.getMaxExpDate());
                    waveLine.setBatchNumber(line.getBatchNumber());
                    waveLine.setCountryOfOrigin(line.getCountryOfOrigin());
                    waveLine.setInvStatus(line.getInvStatus());
                    waveLine.setInvType(line.getInvType());
                    waveLine.setInvAttr1(line.getInvAttr1());
                    waveLine.setInvAttr2(line.getInvAttr2());
                    waveLine.setInvAttr3(line.getInvAttr3());
                    waveLine.setInvAttr4(line.getInvAttr4());
                    waveLine.setInvAttr5(line.getInvAttr5());
                    waveLine.setOutboundCartonType(line.getOutboundCartonType());
                    waveLine.setColor(line.getColor());
                    waveLine.setStyle(line.getStyle());
                    waveLine.setSize(line.getSize());
                    waveLine.setOuId(ouId);
                    waveLine.setCreateTime(new Date());
                    waveLine.setCreatedId(userId);
                    waveLine.setLastModifyTime(new Date());
                    waveLine.setModifiedId(userId);
                    waveLine.setWaveId(wave.getId());
                    this.whWaveLineDao.insert(waveLine);

                    line.setWaveCode(wave.getCode());
                    line.setOdoLineStatus(OdoStatus.ODOLINE_WAVE);
                    int updateCount = this.whOdoLineDao.saveOrUpdateByVersion(line);
                    if (updateCount <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void addOdoLineToWaveNew(List<Long> odoIdList, WhWave wave) {
        Date date1 = new Date();
        log.info("operation:addOdoLineToWaveNew,time start at {},{} odo are ready to begin!", date1, odoIdList.size());
        Long userId = wave.getCreatedId();
        Long ouId = wave.getOuId();

        Date date2 = new Date();
        log.info("operation:addOdoLineToWaveNew,update odo_lines,time start at {}", date2);
        // 更新明细数量
        int updateCount = this.whOdoLineDao.updateOdoLineToWave(odoIdList, OdoStatus.ODOLINE_WAVE, wave.getCode(), ouId, userId);
        Date date3 = new Date();
        log.info("operation:addOdoLineToWaveNew,update odo_lines,time end at {},cost time {},update {} counts", date3, date3.getTime() - date2.getTime(), updateCount);
        // 插入波次明细数量
        JoinPkCommand pkCommand = this.pkManager.generatePkList(Constants.WMS, Constants.WAVE_LINE_URL, updateCount);
        List<Long> idList = pkCommand.toArray();
        LinkedList<Long> idLinkedList = new LinkedList<Long>(idList);

        List<WhWaveLine> waveLineList = new ArrayList<WhWaveLine>();

        List<OdoLineCommand> odoLineList = this.whOdoLineDao.findOdoLineListToWaveByOdoIdListOuId(odoIdList, ouId);
        if (odoLineList != null && odoLineList.size() > 0) {

            for (OdoLineCommand line : odoLineList) {
                WhWaveLine waveLine = new WhWaveLine();
                waveLine.setId(idLinkedList.pop());
                waveLine.setOdoLineId(line.getId());
                waveLine.setOdoId(line.getOdoId());
                waveLine.setOdoCode(line.getOdoCode());
                waveLine.setOdoPriorityLevel(line.getPriorityLevel());
                waveLine.setOdoPlanDeliverGoodsTime(line.getPlanDeliverGoodsTime());
                waveLine.setOdoOrderTime(line.getOrderTime());
                waveLine.setIsStaticLocationAllocate(false);
                waveLine.setLinenum(line.getLinenum());
                waveLine.setStoreId(line.getStoreId());
                waveLine.setExtLinenum(line.getExtLinenum());
                waveLine.setSkuId(line.getSkuId());
                waveLine.setSkuBarCode(line.getSkuBarCode());
                waveLine.setSkuName(line.getSkuName());
                waveLine.setQty(line.getPlanQty());
                waveLine.setAllocateQty(line.getAssignQty());
                waveLine.setIsWholeOrderOutbound(line.getFullLineOutbound());
                waveLine.setFullLineOutbound(line.getFullLineOutbound());
                waveLine.setMfgDate(line.getMfgDate());
                waveLine.setExpDate(line.getExpDate());
                waveLine.setMinExpDate(line.getMinExpDate());
                waveLine.setMaxExpDate(line.getMaxExpDate());
                waveLine.setBatchNumber(line.getBatchNumber());
                waveLine.setCountryOfOrigin(line.getCountryOfOrigin());
                waveLine.setInvStatus(line.getInvStatus());
                waveLine.setInvType(line.getInvType());
                waveLine.setInvAttr1(line.getInvAttr1());
                waveLine.setInvAttr2(line.getInvAttr2());
                waveLine.setInvAttr3(line.getInvAttr3());
                waveLine.setInvAttr4(line.getInvAttr4());
                waveLine.setInvAttr5(line.getInvAttr5());
                waveLine.setOutboundCartonType(line.getOutboundCartonType());
                waveLine.setColor(line.getColor());
                waveLine.setStyle(line.getStyle());
                waveLine.setSize(line.getSize());
                waveLine.setOuId(ouId);
                waveLine.setCreateTime(new Date());
                waveLine.setCreatedId(userId);
                waveLine.setLastModifyTime(new Date());
                waveLine.setModifiedId(userId);
                waveLine.setWaveId(wave.getId());
                waveLine.setAllocateQty(0d);
                waveLineList.add(waveLine);
                // this.whWaveLineDao.insert(waveLine);
            }
        }
        Date date4 = new Date();
        log.info("operation:addOdoLineToWaveNew,insert wave_lines,time start at {}", date4);
        // 批量插入
        int insertCount = this.whWaveLineDao.batchInsert(waveLineList);
        System.out.print("插入条目：" + insertCount);
        Date date5 = new Date();
        log.info("operation:addOdoLineToWaveNew,insert wave_lines,time end at {},costs {},update {} counts", date5, date5.getTime() - date4.getTime(), insertCount);
        Date date6 = new Date();
        log.info("operation:addOdoLineToWaveNew,time end at {},costs {}!", date6, date6.getTime() - date1.getTime());

        if (updateCount == insertCount) {
            WhWave updateWave = this.whWaveDao.findWaveExtByIdAndOuId(wave.getId(), ouId);
            this.finishCreateWave(updateWave);
        }
    }

    /**
     * 根据波次编码获取波次列表
     *
     * @author mingwei.xie
     * @param phaseCode
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWaveCommand> getWhWaveByPhaseCode(String phaseCode, Long ouId) {
        WhWave whWave = new WhWave();
        whWave.setOuId(ouId);
        whWave.setPhaseCode(phaseCode);
        whWave.setAllocatePhase(null);
        whWave.setIsRunWave(true);
        whWave.setStatus(WaveStatus.WAVE_EXECUTING);
        return this.whWaveDao.getWhWaveByParam(whWave);

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWave getWaveByWaveIdAndOuId(Long waveId, Long ouId) {
        if (null == waveId || null == ouId) {
            throw new BusinessException("波次内创工作 : 没有参数");
        }
        WhWave whWave = new WhWave();
        whWave.setId(waveId);
        whWave.setOuId(ouId);
        whWave.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        whWave.setStatus(WaveStatus.WAVE_EXECUTING);
        whWave.setPhaseCode(WavePhase.CREATE_WORK);
        whWave.setAllocatePhase(null);

        List<WhWave> whWaveList = this.whWaveDao.findListByParam(whWave);

        if (null == whWaveList || 1 != whWaveList.size()) {
            throw new BusinessException("多个波次");
        }
        return whWaveList.get(0);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateWaveByWhWave(WhWave whWave) {
        try {
            int updateCount = this.whWaveDao.saveOrUpdateByVersion(whWave);
            if (updateCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> excuteSortSql(String excuteSql, Long ouId) {
        List<Long> odoIdList = new ArrayList<Long>();
        List<Map<String, Object>> list = whWaveMasterDao.excuteSortSql(excuteSql, ouId);
        for (Map<String, Object> map : list) {
            odoIdList.add((Long) map.get("id"));
        }
        return odoIdList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWaveMasterPrintCondition findPrintConditionByWaveId(Long waveId, String printType, Long ouId) {
        WhWave wave = whWaveDao.findWaveExtByIdAndOuId(waveId, ouId);
        if (null == wave) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        WhWaveMasterPrintCondition c = whWaveMasterDao.findPrintConditionByWaveMasterId(wave.getWaveMasterId(), printType, ouId);
        return c;
    }

}
