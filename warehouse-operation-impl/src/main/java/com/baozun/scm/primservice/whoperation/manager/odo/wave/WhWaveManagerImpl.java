package com.baozun.scm.primservice.whoperation.manager.odo.wave;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.odo.OdoMergeCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveMasterDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;

@Service("whWaveManager")
@Transactional
public class WhWaveManagerImpl extends BaseManagerImpl implements WhWaveManager {

    @Autowired
    private WhWaveDao whWaveDao;

    @Autowired
    private WhWaveLineDao whWaveLineDao;

    @Autowired
    private OdoManager odoManager;

    @Autowired
    private WhWaveMasterDao whWaveMasterDao;

    @Autowired
    private WhOdoDao whOdoDao;

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
        List<UomCommand> weightUomCmds = this.odoManager.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
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
        List<UomCommand> volumnUomCmds = this.odoManager.findUomByGroupCode(WhUomType.VOLUME_UOM, BaseModel.LIFECYCLE_NORMAL);
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
    public Pagination<WaveCommand> findWaveListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.whWaveDao.findListByQueryMapWithPageExt(page, sorts, params);
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


}
