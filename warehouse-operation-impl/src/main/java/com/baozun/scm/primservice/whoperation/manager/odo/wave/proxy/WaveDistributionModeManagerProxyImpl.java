package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;

@Service("waveDistributionModeManagerProxy")
public class WaveDistributionModeManagerProxyImpl extends BaseManagerImpl implements WaveDistributionModeManagerProxy {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private RuleManager ruleManager;

    @Override
    public void setWaveDistributionMode(Long waveId, Long waveMasterId, Long ouId) {

        // 从缓存中获取波次主档
        WhWaveMaster master = this.cacheManager.getObject(CacheKeyConstant.CACHE_WAVE_MASTER + waveMasterId);

        Set<Long> noModeOdoList = new HashSet<Long>();// 没有配货模式的出库单

        Set<Long> delOdoList = new HashSet<Long>();// 被剔除的出库单

        Map<Long, String> odoIdCounterCodeMap = new HashMap<Long, String>();

        Iterator<Entry<Long, String>> it = odoIdCounterCodeMap.entrySet().iterator();

        Map<String, Set<Long>> secKillOdoMap = new HashMap<String, Set<Long>>();// 秒杀出库单集合
        Map<String, Set<Long>> suitsOdoMap = new HashMap<String, Set<Long>>();// 套装出库单集合
        Map<String, Set<Long>> twoSuitsOdoMap = new HashMap<String, Set<Long>>();// 主副品出库单集合

        Map<Long, Set<Long>> diyOdoMap = new HashMap<Long, Set<Long>>();// 用户自定义配货模式集合

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
        if(twoSuitsOdoSet.size()>0){
            for(String codeId:twoSuitsOdoSet){
                String[] codeIdArray=codeId.split("|");
                Long unitOdoId=Long.parseLong(codeIdArray[4]);
                String code=codeIdArray[0]+"|"+codeIdArray[1]+"|"+codeIdArray[2]+"|"+codeIdArray[3];
                calcSuits(code, master.getIsCalcSuits(), unitOdoId, suitsOdoMap, noModeOdoList);
            }
        }
        /**
         * 至此，所有的出库单已经分组为：秒杀/主副品/套装/未知； 下面的逻辑：将未分配的出库单，分配到用户预定义的出库单配货模式中；如果失败，则加入剔除序列
         */
        RuleAfferCommand ruleAffer=new RuleAfferCommand();
        ruleAffer.setOuid(ouId);
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);

    }
    



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
        String unitSkuType = unitCodeArray[1];
        String unitSkuQty = unitCodeArray[2];

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

    // ------------------------------------------------------------------------------------------------------------------------------------------------------------


}
