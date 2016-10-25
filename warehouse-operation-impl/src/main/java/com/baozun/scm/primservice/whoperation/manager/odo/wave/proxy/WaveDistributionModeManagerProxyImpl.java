package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DistributionMode;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;

@Service("waveDistributionModeManagerProxy")
public class WaveDistributionModeManagerProxyImpl extends BaseManagerImpl implements WaveDistributionModeManagerProxy {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private RuleManager ruleManager;
    @Autowired
    private WhWaveManager whWaveManager;
    @Autowired
    private WhWaveLineManager whWaveLineManager;
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private OdoLineManager odoLineManager;

    @Override
    public void setWaveDistributionMode(Long waveId, Long waveMasterId, Long ouId,Long userId) {

        // 从缓存中获取波次主档
        WhWaveMaster master = this.cacheManager.getObject(CacheKeyConstant.CACHE_WAVE_MASTER + waveMasterId);
        WhWave wave = this.whWaveManager.getWaveByIdAndOuId(waveId, ouId);

        List<WhOdo> odoList = this.odoManager.findOdoListByWaveCode(wave.getCode(), ouId);

        // 出库单集合
        Map<Long, String> odoIdCounterCodeMap = new HashMap<Long, String>();
        for (WhOdo odo : odoList) {
            odoIdCounterCodeMap.put(odo.getId(), odo.getCounterCode());
        }

        List<WhWaveLine> waveLineList = this.whWaveLineManager.findWaveLineListByWaveId(waveId, ouId);
        Map<Long, WhWaveLine> waveLineMap = new HashMap<Long, WhWaveLine>();
        for (WhWaveLine line : waveLineList) {
            waveLineMap.put(line.getOdoLineId(), line);
        }
        waveLineList = null;
        Iterator<Entry<Long, String>> it = odoIdCounterCodeMap.entrySet().iterator();

        Set<Long> noModeOdoList = new HashSet<Long>();// 没有配货模式的出库单


        Map<String, Set<Long>> secKillOdoMap = new HashMap<String, Set<Long>>();// 秒杀出库单集合
        Map<String, Set<Long>> suitsOdoMap = new HashMap<String, Set<Long>>();// 套装出库单集合
        Map<String, Set<Long>> twoSuitsOdoMap = new HashMap<String, Set<Long>>();// 主副品出库单集合

        Map<Long, String> diyOdoMap = new HashMap<Long, String>();// 用户自定义配货模式集合

        // 主副品
        Set<String> twoSuitsOdoSet = new HashSet<String>();
        Set<String> twoSuitsSkuSet = new HashSet<String>();

        while (it.hasNext()) {
            Entry<Long, String> entry = it.next();
            Long unitOdoId = entry.getKey();
            String unitCode = entry.getValue();

            String[] unitCodeArray = unitCode.split("|");
            String unitSkuType = unitCodeArray[1];
            String unitSkuQty = unitCodeArray[2];

            // 数量=种类
            // 是：进行配货模式计算
            if (unitSkuType.equals(unitSkuQty)) {
                switch (Integer.valueOf(unitSkuType).intValue()) {
                    case 1:
                        calcSeckill(unitCode, master.getIsCalcSeckill(), unitOdoId, secKillOdoMap, noModeOdoList);
                        break;
                    case 2:
                        calcTwoSuits(unitCode, unitOdoId, twoSuitsOdoSet, twoSuitsSkuSet, master.getTwoSkuSuitOdoQtys());
                        break;
                    case 3:
                        calcSuits(unitCode, master.getIsCalcSuits(), unitOdoId, suitsOdoMap, noModeOdoList);
                        break;

                }
            } else {// 否：进入自定义分配模式计算流程
                noModeOdoList.add(unitOdoId);
                odoIdCounterCodeMap.remove(unitOdoId);
            }


        }

        // 主副品的额外计算逻辑
        // 1.将主副品剔除出来
        if (twoSuitsOdoSet.size() > 0) {
            twoSuitsOdoMapIterator(twoSuitsOdoSet, twoSuitsSkuSet, master.getTwoSkuSuitOdoQtys(), twoSuitsOdoMap);
        }
        // 2.再次计算套装组合
        if (twoSuitsOdoSet.size() > 0) {
            for (String codeId : twoSuitsOdoSet) {
                String[] codeIdArray = codeId.split("|");
                Long unitOdoId = Long.parseLong(codeIdArray[4]);
                String code = codeIdArray[0] + "|" + codeIdArray[1] + "|" + codeIdArray[2] + "|" + codeIdArray[3];
                calcSuits(code, master.getIsCalcSuits(), unitOdoId, suitsOdoMap, noModeOdoList);
            }
        }
        // 秒杀的额外计算逻辑：
        Set<Long> secKillSet = new HashSet<Long>();
        Iterator<Entry<String, Set<Long>>> secKillIt = secKillOdoMap.entrySet().iterator();
        while (secKillIt.hasNext()) {
            Entry<String, Set<Long>> entry = secKillIt.next();
            for (Long seckillOdoId : entry.getValue()) {
                if (entry.getValue().size() >= master.getSeckillOdoQtys()) {
                    secKillSet.add(seckillOdoId);
                } else {
                    noModeOdoList.add(seckillOdoId);
                }
            }
        }

        // 套装额外计算逻辑：
        Set<Long> suitsSet = new HashSet<Long>();
        Iterator<Entry<String, Set<Long>>> suitsIt = suitsOdoMap.entrySet().iterator();
        while (suitsIt.hasNext()) {
            Entry<String, Set<Long>> entry = suitsIt.next();
            for (Long suitsOdoId : entry.getValue()) {
                if (entry.getValue().size() >= master.getSuitsOdoQtys()) {
                    suitsSet.add(suitsOdoId);
                } else {
                    noModeOdoList.add(suitsOdoId);
                }
            }
        }
        // 主副品：
        Set<Long> twoSuitsSet = new HashSet<Long>();
        Iterator<Entry<String, Set<Long>>> twoSuitsIt = suitsOdoMap.entrySet().iterator();
        while (twoSuitsIt.hasNext()) {
            Entry<String, Set<Long>> entry = twoSuitsIt.next();
            for (Long twoSuitOdoId : entry.getValue()) {
                twoSuitsSet.add(twoSuitOdoId);
            }
        }
        /**
         * 至此，所有的出库单已经分组为：秒杀/主副品/套装/未知； 下面的逻辑：将未分配的出库单，分配到用户预定义的出库单配货模式中；如果失败，则加入剔除序列
         */
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        ruleAffer.setOuid(ouId);
        ruleAffer.setWaveId(waveId);
        ruleAffer.setRuleType(Constants.DISTRIBUTION_PATTERN);

        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        List<WhDistributionPatternRuleCommand> ruleList = export.getWhDistributionPatternRuleCommandList();// 获取规则的集合
        if (ruleList != null && ruleList.size() > 0) {
            for (int i = 0; i < ruleList.size(); i++) {
                String ruleCode = ruleList.get(i).getDistributionPatternCode();
                List<Long> odoIdList = this.whWaveManager.findOdoListInWaveWhenDistributionPattern(waveId, ouId, ruleList.get(i).getRuleSql());// 某条规则对应的出库单集合
                if (odoIdList != null && odoIdList.size() > 0) {
                    for (Long ruleOdoId : odoIdList) {
                        diyOdoMap.put(ruleOdoId, ruleCode);
                        noModeOdoList.remove(ruleOdoId);
                    }
                }

            }
        }

        // 更新逻辑：
        // 出库单：
        // 1.剔除的出库单回滚到新建状态[#TODO并且回滚相关数据]
        // 2.将对应的配货模式更新到出库单头上去
        // 波次
        // 1.剔除掉被剔除的出库单的相关数据
        // 2.重新统计波次头信息
        // 3.更新出库单波次阶段为下一个阶段
        List<WhOdoLine> offOdoLineList = new ArrayList<WhOdoLine>();
        List<WhWaveLine> offWaveLineList = new ArrayList<WhWaveLine>();
        for (WhOdo odo : odoList) {
            Long odoId = odo.getId();
            if (secKillSet.contains(odoId)) {
                odo.setDistributeMode(DistributionMode.DISTRIBUTION_SECKILL);
            } else if (twoSuitsSet.contains(odoId)) {
                odo.setDistributeMode(DistributionMode.DISTRIBUTION_TWOSKUSUIT);
            } else if (suitsSet.contains(odoId)) {
                odo.setDistributeMode(DistributionMode.DISTRIBUTION_SUITS);
            } else if (diyOdoMap.containsKey(odoId)) {
                odo.setDistributeMode(diyOdoMap.get(odoId));
            } else {
                odo.setWaveCode(null);
                odo.setOdoStatus(OdoStatus.ODO_NEW);
                List<WhOdoLine> odolineList = this.odoLineManager.findOdoLineListByOdoId(odoId, ouId);
                for (WhOdoLine line : odolineList) {
                    line.setOdoLineStatus(OdoStatus.ODOLINE_NEW);
                    line.setWaveCode(null);
                    offOdoLineList.add(line);
                    // 波次明细：剔除
                    offWaveLineList.add(waveLineMap.get(line.getId()));
                    waveLineMap.remove(line.getId());
                }

            }

        }
        // 封装波次头
        packageWave(wave, waveLineMap, ouId, offOdoLineList, noModeOdoList.size());
        // 保存
        // this.whWaveManager.matchWaveDisTributionMode(odoList,offWaveLineList,)

    }
    

    // ------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 秒杀分组
     * 
     * @param code
     * @param isCalcSecKill
     * @param odoId
     * @param secKillOdoMap
     * @param noModeOdoList
     */
    private void calcSeckill(String code, Boolean isCalcSecKill, Long odoId, Map<String, Set<Long>> secKillOdoMap, Set<Long> noModeOdoList) {
        if (!isCalcSecKill) {
            noModeOdoList.add(odoId);
            return;
        }
        if (secKillOdoMap.containsKey(code)) {
            secKillOdoMap.get(code).add(odoId);
        } else {
            Set<Long> odoSet = new HashSet<Long>();
            odoSet.add(odoId);
            secKillOdoMap.put(code, odoSet);
        }

    }

    /**
     * 套装分组
     * 
     * @param code
     * @param isCalcSuits
     * @param odoId
     * @param suitsOdoMap
     * @param noModeOdoList
     */
    private void calcSuits(String code, Boolean isCalcSuits, Long odoId, Map<String, Set<Long>> suitsOdoMap, Set<Long> noModeOdoList) {
        if (!isCalcSuits) {
            noModeOdoList.add(odoId);
            return;
        }
        if (suitsOdoMap.containsKey(code)) {
            suitsOdoMap.get(code).add(odoId);
        } else {
            Set<Long> odoSet = new HashSet<Long>();
            odoSet.add(odoId);
            suitsOdoMap.put(code, odoSet);
        }

    }

    /**
     * 主副品分组
     * 
     * @param code
     * @param twoSuitsOdoMap
     * @param noModeOdoList
     * @param twoSuitsOdoSet
     */
    private void calcTwoSuits(String code, Long odoId, Set<String> twoSuitsOdoSet, Set<String> twoSuitsSkuSet, Integer twoSkuSuitOdoQtys) {
        Map<String, Integer> twoSuitsSkuMap = new HashMap<String, Integer>();
        String[] unitCodeArray = code.split("|");

        String[] unitSkuIdArray = unitCodeArray[3].substring(1, unitCodeArray[3].length() - 1).split("$");

        if (twoSuitsSkuMap.containsKey(unitSkuIdArray[0])) {
            twoSuitsSkuMap.put(unitSkuIdArray[0], twoSuitsSkuMap.get(unitSkuIdArray[0]) + 1);
        } else {
            twoSuitsSkuMap.put(unitSkuIdArray[0], 1);
        }
        if (twoSuitsSkuMap.containsKey(unitSkuIdArray[1])) {
            twoSuitsSkuMap.put(unitSkuIdArray[1], twoSuitsSkuMap.get(unitSkuIdArray[1]) + 1);
        } else {
            twoSuitsSkuMap.put(unitSkuIdArray[1], 1);
        }

        Iterator<Entry<String, Integer>> it = twoSuitsSkuMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();
            if (entry.getValue() >= twoSkuSuitOdoQtys) {
                twoSuitsSkuSet.add(entry.getKey());
            }
        }

        twoSuitsOdoSet.add(code + "|" + odoId);
        
    }

    private void twoSuitsOdoMapIterator(Set<String> twoSuitsOdoSet, Set<String> twoSuitsSkuSet, Integer twoSkuSuitOdoQtys, Map<String, Set<Long>> twoSuitsOdoMap) {
        if (twoSuitsSkuSet.size() == 0) {
            return;
        }
        String unitSku = twoSuitsSkuSet.iterator().next();
        Set<Long> twoSuitsSet = new HashSet<Long>();// 记录此主品下的出库单
        int i = 0;
        for (String codeId : twoSuitsOdoSet) {
            if (codeId.contains("$" + unitSku + "$")) {
                i++;
                String[] codeIdArray = codeId.split("|");
                twoSuitsSet.add(Long.parseLong(codeIdArray[4]));
            }
        }
        if (i >= twoSkuSuitOdoQtys) {
            twoSuitsOdoMap.put(unitSku, twoSuitsSet);
            Iterator<String> it = twoSuitsOdoSet.iterator();
            while (it.hasNext()) {
                String codeId = it.next();
                if (codeId.contains("$" + unitSku + "$")) {
                    it.remove();
                }

            }
        }
        twoSuitsSkuSet.remove(unitSku);
        twoSuitsOdoMapIterator(twoSuitsSkuSet, twoSuitsSkuSet, twoSkuSuitOdoQtys, twoSuitsOdoMap);
    }


    private void packageWave(WhWave wave, Map<Long, WhWaveLine> waveLineMap, Long ouId, List<WhOdoLine> offOdoLineList, int divOdoSize) {
        int odoCount = wave.getTotalOdoQty() - divOdoSize;// 波次出库单总单数
        int odolineCount = waveLineMap.size();// 波次明细数


        double totalSkuQty = wave.getTotalSkuQty();// 商品总件数
        Iterator<Entry<Long, WhWaveLine>> waveLineIt = waveLineMap.entrySet().iterator();
        Set<Long> skuSet = new HashSet<Long>();
        while (waveLineIt.hasNext()) {
            Entry<Long, WhWaveLine> entry = waveLineIt.next();
            WhWaveLine line = entry.getValue();
            skuSet.add(line.getSkuId());
        }
        // 金额减除
        double totalAmt = wave.getTotalAmount();
        Map<Long, Double> skuMap = new HashMap<Long, Double>();// 商品总件数
        for (WhOdoLine odoLine : offOdoLineList) {
            totalAmt -= odoLine.getPlanQty() * odoLine.getLinePrice();
            totalSkuQty -= odoLine.getQty();
            if (skuMap.containsKey(odoLine.getSkuId())) {
                skuMap.put(odoLine.getSkuId(), skuMap.get(odoLine.getSkuId()) + odoLine.getPlanQty());
            } else {
                skuMap.put(odoLine.getSkuId(), odoLine.getPlanQty());
            }
        }
        // 商品种类数
        int skuCategoryQty = skuSet.size();
        // 总体积
        double totalVolume = wave.getTotalVolume();
        // 总重量
        double totalWeight = wave.getTotalWeight();

        // 体积单位转换率
        Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();
        List<UomCommand> lenUomCmds = this.odoManager.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
        for (UomCommand lenUom : lenUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                lenUomConversionRate.put(uomCode, uomRate);
            }
        }
        // 重量单位转换率
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

        Iterator<Entry<Long, Double>> skuIt = skuMap.entrySet().iterator();
        while (skuIt.hasNext()) {
            Entry<Long, Double> entry = skuIt.next();
            Sku sku = this.odoManager.findSkuByIdToShard(entry.getKey(), ouId);
            if (sku != null) {
                totalVolume -= sku.getVolume() * entry.getValue() * (StringUtils.isEmpty(sku.getVolumeUom()) ? 1 : lenUomConversionRate.get(sku.getVolumeUom()));
                totalWeight -= sku.getWeight() * entry.getValue() * (StringUtils.isEmpty(sku.getWeightUom()) ? 1 : weightUomConversionRate.get(sku.getWeightUom()));
            }
        }


        /**
         * 创建波次头
         */
        // a 生成波次编码，校验唯一性；补偿措施
        // #TODO 校验波次号
        wave.setPhaseCode("CREATE_OUTBOUND_CARTON");
        wave.setTotalOdoQty(odoCount);
        wave.setTotalOdoLineQty(odolineCount);
        wave.setTotalAmount(totalAmt);
        wave.setTotalVolume(totalVolume);
        wave.setTotalWeight(totalWeight);
        wave.setTotalSkuQty(totalSkuQty);
        wave.setSkuCategoryQty(skuCategoryQty);
    }


}
