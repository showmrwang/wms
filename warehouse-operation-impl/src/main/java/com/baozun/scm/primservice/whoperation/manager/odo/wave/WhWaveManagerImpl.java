package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.WavePhase;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveMasterDao;
import com.baozun.scm.primservice.whoperation.dao.system.SysDictionaryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.AllocateStrategyDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

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
    private OdoManager odoManager;
    @Autowired
    private WhWaveMasterDao whWaveMasterDao;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private AllocateStrategyDao allocateStrategyDao;
    @Autowired
    private WhWaveLineManager whWaveLineManager;
    @Autowired
    private SysDictionaryDao sysDictionaryDao;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private WhDistributionPatternRuleDao whDistributionPatternRuleDao;

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
    public void updateWaveAfterSoftAllocate(Long waveId, Long ouId) {
        // WhWave whWave = this.whWaveDao.calculateQuantity(waveId, ouId);

        List<SoftAllocationCommand> commandList = this.whWaveLineDao.findWaveLineCommandByWaveIdAndStatus(waveId, ouId, WaveStatus.WAVE_EXECUTING, BaseModel.LIFECYCLE_NORMAL);

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


        WhWave wave = new WhWave();
        wave.setId(waveId);
        wave.setOuId(ouId);
        wave = this.whWaveDao.findListByParam(wave).get(0);

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
                    SysDictionary sys = dicMap.get(Constants.WAVE_STATUS + "_" + wave.getPhaseCode());
                    wave.setStatusName(sys == null ? wave.getPhaseCode() : sys.getDicLabel());
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
            odo.setOdoStatus(OdoStatus.ODO_NEW);
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
    public void updateWaveForSoftStart(WhWave whWave) {
        whWave.setStatus(WaveStatus.WAVE_EXECUTING);
        whWave.setIsWeakAllocated(true);
        whWave.setStartTime(new Date());
        int cnt = this.whWaveDao.saveOrUpdateByVersion(whWave);
        if (0 >= cnt) {
            throw new BusinessException("软分配开始阶段-更新波次信息失败");
        }
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
        String odoIds = this.whOdoDao.findWaveOdoMergableIds(waveCode, ouId, outboundCartonType, epistaticSystemsOrderType, store, deliverGoodsTime);
        List<OdoMergeCommand> list = this.whOdoDao.odoMerge(odoIds, ouId, outboundCartonType, epistaticSystemsOrderType, store, deliverGoodsTime);
        return list;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void changeWavePhaseCode(Long waveId, String phaseCode, Long ouId) {
        WhWave wave = new WhWave();
        wave.setId(waveId);
        wave.setOuId(ouId);
        wave = this.whWaveDao.findListByParam(wave).get(0);

        // 获取下一个波次阶段
        WhWaveMaster whWaveMaster = whWaveMasterDao.findByIdExt(wave.getWaveMasterId(), ouId);
        Long waveTempletId = whWaveMaster.getWaveTemplateId();
        String currPhaseCode = wave.getPhaseCode();
        if (!currPhaseCode.equalsIgnoreCase(phaseCode)) {
            throw new BusinessException("阶段不一致");
        }
        String phase = this.getWavePhaseCode(wave.getPhaseCode(), waveTempletId, ouId);
        wave.setPhaseCode(phase);
        this.whWaveDao.saveOrUpdateByVersion(wave);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> getNeedAllocationRuleWhWave(Integer allocatePhase, Long ouId, String logId) {
        List<Long> datas = whWaveDao.getNeedAllocationRuleWhWave(allocatePhase, ouId);
        if (log.isInfoEnabled()) {
            log.info("getHardAllocateWhWaveList,ouId:{},waveList:{},logId:{}", ouId, StringUtils.collectionToCommaDelimitedString(datas), logId);
        }
        return datas;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int updateWhWaveAllocatePhase(List<Long> waveIdList, Integer allocatePhase, Long ouId) {
        int num = whWaveDao.updateWhWaveAllocatePhase(waveIdList, allocatePhase, ouId);
        if (num < 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        return num;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void checkWaveHardAllocateEnough(Long waveId, Warehouse wh) {
        Long ouId = wh.getId();
        List<WhWaveLine> lines = whWaveLineDao.findNotEnoughAllocationQty(waveId, ouId);
        // 获取下一个波次阶段编码
        WhWave wave = new WhWave();
        wave.setId(waveId);
        wave.setOuId(ouId);
        wave.setAllocatePhase(null);
        wave = this.whWaveDao.findListByParam(wave).get(0);
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
            Map<Long, List<String>> ruleMap = new HashMap<Long, List<String>>();
            Set<Long> allOdoIds = new HashSet<Long>();
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
                    SysDictionary dictionary = sysDictionaryDao.getGroupbyGroupValueAndDicValue(Constants.WAVE_FAIL_REASON, Constants.NOT_STATIC_EMPTY_LOCATION);
                    for (Long odoId : odoIds) {
                        whWaveLineManager.deleteWaveLinesByOdoId(odoId, waveId, ouId, dictionary.getDicLabel());
                        // 释放库存
                        whSkuInventoryManager.releaseInventoryByOdoId(odoId, wh);
                    }
                    wave.setPhaseCode(phaseCode);
                    int num = whWaveDao.saveOrUpdateByVersion(wave);
                    if (1 != num) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            } else {
                // 剔除库存数量没有分配完全所有工作单
                SysDictionary dictionary = sysDictionaryDao.getGroupbyGroupValueAndDicValue(Constants.WAVE_FAIL_REASON, Constants.NOT_STATIC_EMPTY_LOCATION);
                for (Long odoId : allOdoIds) {
                    whWaveLineManager.deleteWaveLinesByOdoId(odoId, waveId, ouId, dictionary.getDicLabel());
                    // 释放库存
                    whSkuInventoryManager.releaseInventoryByOdoId(odoId, wh);
                }
                wave.setPhaseCode(phaseCode);
                int num = whWaveDao.saveOrUpdateByVersion(wave);
                if (1 != num) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
            }
        }
        // 回写odoLine的分配数量
        WhWaveLine waveLine = new WhWaveLine();
        waveLine.setWaveId(waveId);
        waveLine.setOuId(ouId);
        List<WhWaveLine> waveLines = whWaveLineDao.findListByParam(waveLine);
        for (WhWaveLine line : waveLines) {
            WhOdoLine odoLine = whOdoLineDao.findOdoLineById(line.getOdoLineId(), ouId);
            odoLine.setAssignQty(line.getAllocateQty());
            odoLine.setIsAssignSuccess(true);
            whOdoLineDao.saveOrUpdate(odoLine);
        }
    }

    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void matchWaveDisTributionMode(List<WhOdo> odoList, List<WhWaveLine> offWaveLineList, List<WhOdoLine> offOdoLineList, WhWave wave, Long ouId, Long userId) {
        for (WhWaveLine line : offWaveLineList) {
            this.whWaveLineDao.deleteByIdOuId(line.getId(), ouId);
        }
        for (WhOdoLine line : offOdoLineList) {
            if (userId != null) {
                line.setModifiedId(userId);
            }
            this.whOdoLineDao.saveOrUpdateByVersion(line);
        }
        for (WhOdo odo : odoList) {
            if (userId != null) {
                odo.setModifiedId(userId);
            }
            this.whOdoDao.saveOrUpdateByVersion(odo);
        }
        this.whWaveDao.saveOrUpdateByVersion(wave);

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
    public List<Long> findOdoContainsSkuId(Long waveId, List<Long> skuIds, Long ouId) {
        return whWaveDao.findOdoContainsSkuId(waveId, skuIds, ouId);
    }

    @Override
    public List<Long> getNeedSoftAllocationWhWave(Long ouId) {
        List<Long> WaveIds = whWaveDao.getWhWaveListByPhaseCode(Constants.WEAK_ALLOCATED, ouId);
        return WaveIds;
    }

}
