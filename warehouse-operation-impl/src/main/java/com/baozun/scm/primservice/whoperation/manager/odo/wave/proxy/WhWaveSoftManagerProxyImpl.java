package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.SoftAllocationResponseCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.WaveStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;

@Service("whWaveSoftManagerProxy")

public class WhWaveSoftManagerProxyImpl implements WhWaveSoftManagerProxy {

    @Autowired
    private WhWaveManager whWaveManager;

    @Autowired
    private WhWaveLineManager whWaveLineManager;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private OdoLineManager odoLineManager;

    @Autowired
    private OdoManager odoManager;

    @Override
    public List<WhWaveLine> getWaveLineForSoft(Long waveId, Long ouId) {
        // 1.获取波次头
        WhWave whWave = this.getWaveHeadByIdAndOuId(waveId, ouId);
        if (null == whWave) {
            throw new BusinessException("没有波次头信息");
        }
        if (BaseModel.LIFECYCLE_NORMAL != whWave.getLifecycle() || WaveStatus.WAVE_NEW != whWave.getStatus()) {
            throw new BusinessException("波次头不可用或者波次状态不为新建");
        }
        // 2.设置波次运行状态信息
        whWaveManager.updateWaveForSoftStart(whWave);
        // this.updateWaveForSoftStart(whWave);

        // 3.查询波次明细及排序
        List<WhWaveLine> whWaveLineList = whWaveLineManager.getSoftAllocationWhWaveLine(waveId, ouId);
        // List<WhWaveLine> whWaveLineList = this.getWaveLineByWaveIdAndOuIdForSoft(waveId, ouId);
        return whWaveLineList;
    }

    @SuppressWarnings("unused")
    private void updateWaveForSoftStart(WhWave whWave) {
        whWaveManager.updateWaveForSoftStart(whWave);
    }

    @Override
    public WhWave getWaveHeadByIdAndOuId(Long waveId, Long ouId) {
        WhWave whWave = whWaveManager.getWaveByIdAndOuId(waveId, ouId);
        return whWave;
    }

    @Override
    public List<WhWaveLine> getWaveLineByWaveIdAndOuId(Long waveId, Long ouId) {
        WhWaveLine whWaveLine = new WhWaveLine();
        whWaveLine.setWaveId(waveId);
        whWaveLine.setOuId(ouId);
        List<WhWaveLine> whWaveLineList = whWaveLineManager.getWaveLineByParam(whWaveLine);
        return whWaveLineList;
    }

    @Override
    public List<WhWaveLine> getWaveLineByWaveIdAndOuIdForSoft(Long waveId, Long ouId) {
        List<WhWaveLine> whWaveLineList = whWaveLineManager.getSoftAllocationWhWaveLine(waveId, ouId);
        return whWaveLineList;
    }

    @Override
    public List<SoftAllocationCommand> getSkuInvTotalQty(Long waveId, Long ouId) {
        List<SoftAllocationCommand> softAllocationCommandList = this.whWaveManager.getSkuInvTotalQty(waveId, ouId);
        return softAllocationCommandList;
    }

    @Override
    public List<SoftAllocationCommand> getSkuInvOccupiedQty(Long waveId, Long ouId) {
        List<SoftAllocationCommand> softAllocationCommandList = this.whWaveManager.getSkuInvOccupiedQty(waveId, ouId);
        return softAllocationCommandList;
    }

    @Override
    public SoftAllocationResponseCommand occupiedOperation(Long waveId, Long skuId, Long qty, Long waveLineId, Long ouId) {
        if (null == waveId || null == skuId || null == qty || null == waveLineId || null == ouId) {
            if (null == waveId || null == ouId) {
                throw new BusinessException("软分配 : 没有参数");
            }
        }
        // 1.判断商品是否在空库存列表中
        boolean res = this.cacheFindEmpty(waveId, skuId);
        SoftAllocationResponseCommand command = new SoftAllocationResponseCommand();
        if (res) {
            // 库存不为0
            // 2.计算每个波次明细行是否可以占用库存
            WhWaveLine whWaveLine = this.whWaveLineManager.getWaveLineByIdAndOuId(waveLineId, ouId);
            Double skuQty = whWaveLine.getQty();
            if (qty > skuQty) {
                // 实际可用数量 > 需求数量:可以占用 执行占用方法
                Long odoLineId = whWaveLine.getOdoLineId();
                Long odoId = whWaveLine.getOdoId();
                // 更新出库单明细和出库单头的状态为波次中
                boolean result = this.updateOdoStatus(odoId, odoLineId, ouId);
                if (result) {
                    // 对实际可用量列表中当前商品的实际量做扣减操作
                    Long quantity = qty - skuQty.longValue();
                    if (0 == quantity) {
                        this.cacheAddEmptySku(waveId, skuId);
                    }
                } else {
                    throw new BusinessException("更新状态失败");
                }
            } else {
                // 实际可用数量 < 需求数量:不可以占用 剔除
                // removeOperation(odoId)

            }
        } else {
            // 库存为0 剔除
        }
        return null;
    }

    /**
     * 查看商品是否有库存
     * @param waveId
     * @param skuId
     * @return
     */
    private Boolean cacheFindEmpty(Long waveId, Long skuId) {
        Set<String> skuIdSet = cacheManager.findSetAll(CacheKeyConstant.CACHE_ALLOCATE_SOFT + waveId);
        if (null == skuIdSet || skuIdSet.isEmpty() || 0 == skuIdSet.size() || !skuIdSet.contains(skuId.toString())) {
            return true;
        }
        return false;
    }

    /**
     * 添加库存为0的商品到缓存
     * @param waveId
     * @param skuId
     */
    private void cacheAddEmptySku(Long waveId, Long skuId) {
        String[] ids = {skuId.toString()};
        cacheManager.addSet(waveId.toString(), ids);
    }

    /**
     * 更新出库单头和明细状态
     * @param odoId
     * @param odoLineId
     * @param ouId
     * @return
     */
    private Boolean updateOdoStatus(Long odoId, Long odoLineId, Long ouId) {
        Boolean res = odoManager.updateOdoStatus(odoId, odoLineId, ouId, OdoStatus.ODOLINE_WAVE);
        if (res) {
            return true;
        }
        return false;
    }

    @Override
    public void removeWaveLine(Long waveId, Long odoId, List<Long> odoLineIds, Long ouId) {
        WhOdo whOdo = odoManager.findOdoByIdOuId(odoId, ouId);
        Boolean isWhole = whOdo.getIsWholeOrderOutbound();
        if (isWhole) {
            // 整单出库 剔除逻辑
            odoManager.removeOdoAndLineWhole(waveId, odoId, odoLineIds, ouId);
        } else {
            // TODO 非整单出库 剔除逻辑
        }

    }

    @Override
    public void updateWave(Long waveId, Long ouId) {
        whWaveManager.updateWaveAfterSoftAllocate(waveId, ouId);
    }

    @Override
    public void cleanUpData(Long waveId, Long ouId) {
        cacheManager.remove(CacheKeyConstant.CACHE_ALLOCATE_SOFT + waveId);
    }
}
