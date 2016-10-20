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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveMasterDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;

@Service("whWaveManager")
@Transactional
public class WhWaveManagerImpl extends BaseManagerImpl implements WhWaveManager {

    @Autowired
    private WhWaveDao whWaveDao;

    @Autowired
    private UomDao uomDao;

    @Autowired
    private OdoManager odoManager;

    @Autowired
    private WhWaveMasterDao whWaveMasterDao;

    @Autowired
    private WhOdoDao whOdoDao;

    @Autowired
    private WhOdoLineDao whOdoLineDao;

    @Autowired
    private WhWaveLineDao whWaveLineDao;

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
        WhWave whWave = this.whWaveDao.calculateQuantity(waveId, ouId);

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
        wave.setTotalVolume(whWave.getTotalVolume());
        wave.setTotalWeight(whWave.getTotalWeight());
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
}
