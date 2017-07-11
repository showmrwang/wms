package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DistributionMode;
import com.baozun.scm.primservice.whoperation.constant.OdoLineStatus;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoVasManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.WhWaveManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

@Service("waveDistributionModeManagerProxy")
public class WaveDistributionModeManagerProxyImpl extends BaseManagerImpl implements WaveDistributionModeManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(WaveDistributionModeManagerProxy.class);

    @Autowired
    private WarehouseManager warehouseManager;
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
    @Autowired
    private DistributionModeArithmeticManagerProxy distributionModeArithmeticManagerProxy;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private OdoVasManager odoVasManager;

    @Override
    public void setWaveDistributionMode(Long waveId, Warehouse wh, Long userId) {
        String logId = this.getLogId();
        logId += waveId + "$";
        log.info("logId:{},waveId:{},task ->method setWaveDistributionMode start ", logId, waveId);
        Long ouId = wh.getId();
        if (userId == null) {
            userId = 1000001L;
        }
        // 从缓存中获取波次主档
        WhWave wave = this.whWaveManager.getWaveByIdAndOuId(waveId, ouId);
        WhWaveMaster master = null;
        // this.cacheManager.getObject(CacheKeyConstant.CACHE_WAVE_MASTER + wave.getWaveMasterId());
        if (master == null) {

            master = this.whWaveManager.findWaveMasterbyIdOuId(wave.getWaveMasterId(), ouId);
        }
        // #TODO
        if (master == null) {
            log.error("logId:{},task ->method setWaveDistributionMode get waveMaster[WaveId:{},ouId:{}] throw error", logId, waveId, ouId);
            return;
        }
        List<WhOdo> odoList = this.odoManager.findOdoListByWaveCode(wave.getCode(), ouId);


        Set<Long> noModeOdoList = new HashSet<Long>();// 没有配货模式的出库单


        Map<String, Set<Long>> secKillOdoMap = new HashMap<String, Set<Long>>();// 秒杀出库单集合
        Map<String, Set<Long>> suitsOdoMap = new HashMap<String, Set<Long>>();// 套装出库单集合
        Map<String, Set<Long>> twoSuitsOdoMap = new HashMap<String, Set<Long>>();// 主副品出库单集合

        Map<Long, String> diyOdoMap = new HashMap<Long, String>();// 用户自定义配货模式集合

        // 出库单集合
        log.info("logId:{},task ->method setWaveDistributionMode:count odo Map ", logId);
        Map<Long, String> odoIdCounterCodeMap = new HashMap<Long, String>();
        for (WhOdo odo : odoList) {
            log.debug("logId:{},task ->method setWaveDistributionMode: add to odoIdCounterCodeMap[odoId:{},counterCode:{}]", logId, odo.getId(), odo.getCounterCode());
            // @mender yimin.lu 指定库存属性的出库单/有包装要求/有仓库增值服务/管理库存属性 不参与计算默认配货模式 2017/6/29
            boolean flag = this.odoManager.isSuitForDefaultDistributionMode(odo);
            if (flag) {

                odoIdCounterCodeMap.put(odo.getId(), odo.getCounterCode());
            } else {
                noModeOdoList.add(odo.getId());
            }
        }

        Iterator<Entry<Long, String>> it = odoIdCounterCodeMap.entrySet().iterator();



        // 主副品
        Set<String> twoSuitsOdoSet = new HashSet<String>();
        Set<String> twoSuitsSkuSet = new HashSet<String>();

        log.info("logId:{},task ->method setWaveDistributionMode : default distributionMode count", logId);
        Map<String, Integer> twoSuitsCounterMap = new HashMap<String, Integer>();
        while (it.hasNext()) {
            Entry<Long, String> entry = it.next();
            Long unitOdoId = entry.getKey();
            String unitCode = entry.getValue();

            String[] unitCodeArray = unitCode.split("\\|");
            String unitSkuType = unitCodeArray[1];
            String unitSkuQty = unitCodeArray[2];

            // 数量=种类
            // 是：进行配货模式计算
            if (unitSkuType.equals(unitSkuQty)) {
                switch (Integer.valueOf(unitSkuType).intValue()) {
                    case 1:
                        calcSeckill(unitCode, master, unitOdoId, secKillOdoMap, noModeOdoList, logId);
                        break;
                    case 2:
                        calcTwoSuits(unitCode, unitOdoId, twoSuitsOdoSet, twoSuitsSkuSet, twoSuitsCounterMap, master, suitsOdoMap, noModeOdoList, logId);
                        break;
                    default:
                        calcSuits(unitCode, master, unitOdoId, suitsOdoMap, noModeOdoList, logId);
                        break;

                }
            } else {// 否：进入自定义分配模式计算流程
                noModeOdoList.add(unitOdoId);
                log.info("logId:{},method setWaveDistributionMode:odo[id:{}] no default mode ", logId, unitOdoId);
                // odoIdCounterCodeMap.remove(unitOdoId);
                it.remove();
            }


        }
        log.info("logId:{},method setWaveDistributionMode: odoes has classify for default distribution mode ", logId);
        // 主副品的额外计算逻辑
        // 1.将主副品剔除出来
        log.info("logId:{},method setWaveDistributionMode: counter twoSuits begin", logId);
        if (twoSuitsOdoSet.size() > 0) {
            twoSuitsSkuSet = this.sortTwoSuitsByFps(twoSuitsSkuSet, twoSuitsCounterMap);
            twoSuitsOdoMapIterator(twoSuitsOdoSet, twoSuitsSkuSet, master.getTwoSkuSuitOdoQtys(), twoSuitsOdoMap, logId);
        }
        // 2.再次计算套装组合
        log.info("logId:{},method setWaveDistributionMode: counter twoSuits ->add noMode odo to suits", logId);
        if (twoSuitsOdoSet.size() > 0) {
            for (String codeId : twoSuitsOdoSet) {
                String[] codeIdArray = codeId.split("\\|");
                Long unitOdoId = Long.parseLong(codeIdArray[4]);
                String code = codeIdArray[0] + "|" + codeIdArray[1] + "|" + codeIdArray[2] + "|" + codeIdArray[3];
                calcSuits(code, master, unitOdoId, suitsOdoMap, noModeOdoList, null);
            }
        }
        log.info("logId:{},method setWaveDistributionMode: counter secKill", logId);
        // 秒杀的额外计算逻辑：
        Map<Long, String> secKillSet = new HashMap<Long, String>();
        Iterator<Entry<String, Set<Long>>> secKillIt = secKillOdoMap.entrySet().iterator();
        while (secKillIt.hasNext()) {
            Entry<String, Set<Long>> entry = secKillIt.next();
            for (Long seckillOdoId : entry.getValue()) {
                if (entry.getValue().size() >= master.getSeckillOdoQtys()) {
                    log.info("logId:{},method setWaveDistributionMode: secKill[{}] add odo[{}]", logId, entry.getKey(), seckillOdoId);
                    secKillSet.put(seckillOdoId, entry.getKey());
                } else {
                    log.info("logId:{},method setWaveDistributionMode: secKill-> no default mode of odo[{}]", logId, seckillOdoId);
                    noModeOdoList.add(seckillOdoId);
                }
            }
        }

        // 套装额外计算逻辑：
        log.info("logId:{},method setWaveDistributionMode: counter suits", logId);
        Map<Long, String> suitsSet = new HashMap<Long, String>();
        Iterator<Entry<String, Set<Long>>> suitsIt = suitsOdoMap.entrySet().iterator();
        while (suitsIt.hasNext()) {
            Entry<String, Set<Long>> entry = suitsIt.next();
            for (Long suitsOdoId : entry.getValue()) {
                if (entry.getValue().size() >= master.getSuitsOdoQtys()) {
                    log.info("logId:{},method setWaveDistributionMode: suits[{}] add odo[{}]", logId, entry.getKey(), suitsOdoId);
                    suitsSet.put(suitsOdoId, entry.getKey());
                } else {
                    log.info("logId:{},method setWaveDistributionMode: suits-> no default mode of odo[{}]", logId, suitsOdoId);
                    noModeOdoList.add(suitsOdoId);
                }
            }
        }
        // 主副品：
        log.info("logId:{},method setWaveDistributionMode: counter twosuits", logId);
        Map<Long, String> twoSuitsSet = new HashMap<Long, String>();
        Iterator<Entry<String, Set<Long>>> twoSuitsIt = twoSuitsOdoMap.entrySet().iterator();
        while (twoSuitsIt.hasNext()) {
            Entry<String, Set<Long>> entry = twoSuitsIt.next();
            for (Long twoSuitOdoId : entry.getValue()) {
                log.info("logId:{},method setWaveDistributionMode: twosuits[{}] add odo[{}]", logId, entry.getKey(), twoSuitOdoId);
                twoSuitsSet.put(twoSuitOdoId, entry.getKey());
            }
        }
        log.info("logId:{},method setWaveDistributionMode :DIY rules for odoes ", logId);
        /**
         * 至此，所有的出库单已经分组为：秒杀/主副品/套装/未知； 下面的逻辑：将未分配的出库单，分配到用户预定义的出库单配货模式中；如果失败，则加入剔除序列
         */
        if (noModeOdoList.size() > 0) {
            // @mender yimin.lu 2017/6/29 序列号商品不能计算为播种模式
            RuleAfferCommand ruleAffer = new RuleAfferCommand();
            ruleAffer.setOuid(ouId);
            ruleAffer.setWaveId(waveId);
            ruleAffer.setRuleType(Constants.DISTRIBUTION_PATTERN);

            RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
            List<WhDistributionPatternRuleCommand> ruleList = export.getWhDistributionPatternRuleCommandList();// 获取规则的集合
            if (ruleList != null && ruleList.size() > 0) {
                for (int i = 0; i < ruleList.size(); i++) {
                    WhDistributionPatternRuleCommand rule = ruleList.get(i);
                    String ruleCode = rule.getDistributionPatternCode();
                    // List<Long> odoIdList =
                    // this.whWaveManager.findOdoListInWaveWhenDistributionPattern(waveId, ouId,
                    // ruleList.get(i).getRuleSql());// 某条规则对应的出库单集合
                    log.info("logId:{},method setWaveDistributionMode :DIY rule[{}] for odoes ", logId, ruleCode);
                    List<Long> odoIdList = rule.getOdoIdList();
                    if (odoIdList != null && odoIdList.size() > 0) {
                        // @mender yimin.lu 2017/7/10 规则订单数下限
                        if (rule.getOrdersLowerLimit() != null && (rule.getOrdersLowerLimit() > odoIdList.size() || rule.getOrdersLowerLimit() > noModeOdoList.size())) {
                            continue;
                        }
                        odoIdList.retainAll(noModeOdoList);// 取得相同的元素
                        if (odoIdList == null || odoIdList.size() == 0) {
                            continue;
                        }
                        if (rule.getOrdersLowerLimit() != null && rule.getOrdersLowerLimit() > odoIdList.size()) {
                            continue;
                        }
                        List<Long> ruleOdoIdList = new ArrayList<Long>();
                        for (Long ruleOdoId : odoIdList) {
                            // @mender yimin.lu 2017/6/29 出库单是否包含序列号商品
                            if (this.isSn(ruleOdoId, ouId) && Constants.PICKING_MODE_SEED.equals(ruleList.get(i).getPickingMode().toString())) {
                                continue;
                            }
                            ruleOdoIdList.add(ruleOdoId);
                        }
                        if (rule.getOrdersLowerLimit() != null && rule.getOrdersLowerLimit() > ruleOdoIdList.size()) {

                        } else {
                            for (Long ruleOdoId : ruleOdoIdList) {
                                log.info("logId:{},method setWaveDistributionMode :DIY rule[{}] add odo[{}] ", logId, ruleCode, ruleOdoId);
                                diyOdoMap.put(ruleOdoId, ruleCode);
                                noModeOdoList.remove(ruleOdoId);
                            }
                        }
                    }
                    if (noModeOdoList.size() == 0) {
                        break;
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
        for (WhOdo odo : odoList) {
            Long odoId = odo.getId();
            if (secKillSet.containsKey(odoId)) {
                log.info("logId:{},method setWaveDistributionMode :secKill odo[{}]] ", logId, odoId);
                odo.setDistributeMode(DistributionMode.DISTRIBUTION_SECKILL);
                odo.setDistributionCode(secKillSet.get(odoId));
            } else if (twoSuitsSet.containsKey(odoId)) {
                log.info("logId:{},method setWaveDistributionMode :twosuits odo[{}]] ", logId, odoId);
                odo.setDistributeMode(DistributionMode.DISTRIBUTION_TWOSKUSUIT);
                odo.setDistributionCode(twoSuitsSet.get(odoId));
            } else if (suitsSet.containsKey(odoId)) {
                log.info("logId:{},method setWaveDistributionMode :suits odo[{}]] ", logId, odoId);
                odo.setDistributeMode(DistributionMode.DISTRIBUTION_SUITS);
                odo.setDistributionCode(suitsSet.get(odoId));
            } else if (diyOdoMap.containsKey(odoId)) {
                log.info("logId:{},method setWaveDistributionMode :diyRule odo[{}]] ", logId, odoId);
                odo.setDistributeMode(diyOdoMap.get(odoId));
                odo.setDistributionCode(null);
            } else {
                log.info("logId:{},method setWaveDistributionMode :noMode odo[{}]] ", logId, odoId);
                odo.setDistributeMode(null);
                odo.setDistributionCode(null);
                odo.setAssignFailReason(Constants.DISTRIBUTE_MODE_FAIL);
                odo.setWaveCode(null);
                odo.setOdoStatus(OdoStatus.NEW);
                List<WhOdoLine> odolineList = this.odoLineManager.findOdoLineListByOdoId(odoId, ouId);
                for (WhOdoLine line : odolineList) {
                    line.setOdoLineStatus(OdoLineStatus.NEW);
                    line.setWaveCode(null);
                    line.setAssignQty(null);
                }

            }

        }
        // 封装波次头
        // @mender yimin.lu 2017/3/14 剔除波次 接口方法调整
        // packageWave(wave, waveLineMap, master, ouId, offOdoLineList, noModeOdoList.size());
        // 保存
        log.info("logId:{},method setWaveDistributionMode :invoke whWaveManager.matchWaveDisTributionMode ", logId);
        this.whWaveManager.matchWaveDisTributionMode(odoList, wave, ouId, userId, wh, logId);

    }


    // ------------------------------------------------------------------------------------------------------------------------------------------------------------

    private boolean isSn(Long ruleOdoId, Long ouId) {
        boolean isSn = false;
        WhOdo odo = this.odoManager.findOdoByIdOuId(ruleOdoId, ouId);
        String counterCode = odo.getCounterCode();
        if (StringUtils.hasText(counterCode)) {
            String[] unitCodeArray = counterCode.split("\\|");

            String[] unitSkuIdArray = unitCodeArray[3].substring(1, unitCodeArray[3].length() - 1).split("\\$");

            if (unitSkuIdArray != null && unitSkuIdArray.length > 0) {
                for (String unitSkuId : unitSkuIdArray) {
                    SkuRedisCommand s = this.skuRedisManager.findSkuMasterBySkuId(Long.parseLong(unitSkuId), ouId, null);
                    if (s != null) {
                        if (Constants.SERIAL_NUMBER_TYPE_ALL.equals(s.getSkuMgmt().getSerialNumberType())) {
                            isSn = true;
                        } else {
                            isSn = false;
                        }
                    }
                    if (!isSn) {
                        break;
                    }
                }
            }
        }


        return isSn;
    }


    private Set<String> sortTwoSuitsByFps(Set<String> twoSuitsSkuSet, Map<String, Integer> twoSuitsCounterMap) {
        Map<String, Integer> newCounterMap = new HashMap<String, Integer>();
        for (String twoSuitsCode : twoSuitsSkuSet) {
            newCounterMap.put(twoSuitsCode, twoSuitsCounterMap.get(twoSuitsCode));
        }
        Map<String, Integer> sortedMap = this.sortMapByValue(newCounterMap);
        return sortedMap.keySet();
    }



    /**
     * 秒杀分组
     * 
     * @param code
     * @param isCalcSecKill
     * @param odoId
     * @param secKillOdoMap
     * @param noModeOdoList
     * @param logId
     */
    private void calcSeckill(String code, WhWaveMaster master, Long odoId, Map<String, Set<Long>> secKillOdoMap, Set<Long> noModeOdoList, String logId) {
        log.info("logId:{}, method calcSeckill start!params:[odoId:{}]", logId, odoId);
        if (!master.getIsCalcSeckill()) {
            log.info("logId:{}, odo[id:{}] no default mode suits", logId, odoId);
            noModeOdoList.add(odoId);
            return;
        }
        if (secKillOdoMap.containsKey(code)) {
            log.info("logId:{}, odo[id:{}] add to secKill[code:{}] collections", logId, odoId, code);
            secKillOdoMap.get(code).add(odoId);
        } else {
            log.info("logId:{}, odo[id:{}] add to secKill[code:{}] collections", logId, odoId, code);
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
     * @param logId
     */
    private void calcSuits(String code, WhWaveMaster master, Long odoId, Map<String, Set<Long>> suitsOdoMap, Set<Long> noModeOdoList, String logId) {
        log.info("logId:{}, method calcSuits start!params:[odoId:{}]", logId, odoId);
        if (!master.getIsCalcSuits()) {
            log.info("logId:{}, odo[id:{}] no default mode suits", logId, odoId);
            noModeOdoList.add(odoId);
            return;
        }
        String[] codeArray = code.split("\\" + CacheKeyConstant.WAVE_ODO_SPLIT);
        Integer skuType = Integer.parseInt(codeArray[1]);
        if (skuType < 2 || skuType > master.getSuitsMaxSkuCategorys()) {
            noModeOdoList.add(odoId);
            log.info("logId:{}, odo[id:{}] add to calcSuits[code:{}] collections", logId, odoId, code);
            return;
        }
        if (suitsOdoMap.containsKey(code)) {
            log.info("logId:{}, odo[id:{}] add to suits[code:{}] collections", logId, odoId, code);
            suitsOdoMap.get(code).add(odoId);
        } else {
            log.info("logId:{}, odo[id:{}] add to suits[code:{}] collections", logId, odoId, code);
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
     * @param logId
     */
    private void calcTwoSuits(String code, Long odoId, Set<String> twoSuitsOdoSet, Set<String> twoSuitsSkuSet, Map<String, Integer> twoSuitsSkuMap, WhWaveMaster master, Map<String, Set<Long>> suitsOdoMap, Set<Long> noModeOdoList, String logId) {

        if (!master.getIsCalcTwoSkuSuit()) {
            log.info("logId:{},method calcTwoSuits invoke calcSuits start! params:[code:{},odoId:{}]", logId, code, odoId);
            calcSuits(code, master, odoId, suitsOdoMap, noModeOdoList, null);
        } else {
            log.info("logId:{},method calcTwoSuits start! params:[code:{},odoId:{}]", logId, code, odoId);
            String[] unitCodeArray = code.split("\\|");

            String[] unitSkuIdArray = unitCodeArray[3].substring(1, unitCodeArray[3].length() - 1).split("\\$");

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
                if (entry.getValue() >= master.getTwoSkuSuitOdoQtys()) {
                    log.info("logId:{},method calcTwoSuits:odo[id:{}] add to TwoSuits[code:{},count:{}] collections", logId, odoId, entry.getKey(), entry.getValue());
                    twoSuitsSkuSet.add(entry.getKey());
                }
            }
            log.info("logId:{},method calcTwoSuits:odo[id:{}] add to TwoSuits collection", logId, odoId);
            twoSuitsOdoSet.add(code + "|" + odoId);
        }

    }

    private void twoSuitsOdoMapIterator(Set<String> twoSuitsOdoSet, Set<String> twoSuitsSkuSet, Integer twoSkuSuitOdoQtys, Map<String, Set<Long>> twoSuitsOdoMap, String logId) {
        log.info("logId:{},method twoSuitsOdoMapIterator begin", logId);
        if (twoSuitsSkuSet.size() == 0) {
            return;
        }
        String unitSku = twoSuitsSkuSet.iterator().next();
        log.info("logId:{},method twoSuitsOdoMapIterator : twoSuits:unit sku[{}]", logId, unitSku);
        Set<Long> twoSuitsSet = new HashSet<Long>();// 记录此主品下的出库单
        int i = 0;
        for (String codeId : twoSuitsOdoSet) {
            if (codeId.contains("$" + unitSku + "$")) {
                log.info("logId:{},method twoSuitsOdoMapIterator : twoSuits:unit sku[{}] add odo[{}]", logId, unitSku, codeId);
                i++;
                String[] codeIdArray = codeId.split("\\|");
                twoSuitsSet.add(Long.parseLong(codeIdArray[4]));
            }
        }
        if (i >= twoSkuSuitOdoQtys) {
            log.info("logId:{},method twoSuitsOdoMapIterator : twoSuits:unit sku[{}]", logId, unitSku);
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
        twoSuitsOdoMapIterator(twoSuitsOdoSet, twoSuitsSkuSet, twoSkuSuitOdoQtys, twoSuitsOdoMap, logId);
    }


    @Override
    public Map<String, List<Long>> getSecKillOdoList(Long ouId) {
        List<String> keys = this.cacheManager.Keys(CacheKeyConstant.SECKILL_ODO_PREFIX + ouId + "|*");
        Map<String, List<Long>> map = new HashMap<String, List<Long>>();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                String[] keyArray = key.split("%");
                String[] codeOdoIdArray = keyArray[2].split("\\|");
                String code = codeOdoIdArray[0] + "|" + codeOdoIdArray[1] + "|" + codeOdoIdArray[2] + "|" + codeOdoIdArray[3];
                Long odoId = Long.parseLong(codeOdoIdArray[4]);
                if (map.containsKey(code)) {
                    map.get(code).add(odoId);
                } else {
                    List<Long> odoIdList = new ArrayList<Long>();
                    odoIdList.add(odoId);
                    map.put(code, odoIdList);
                }
            }
        }
        return map;
    }


    @Override
    public void initSecKillDistributionMode(String code, Long ouId, List<Long> odoIdList, String distributeMode) {
        try {
            String[] codeArray = code.split("\\|");
            Long skuId = Long.parseLong(codeArray[3].substring(1, codeArray[3].length() - 1));
            Sku sku = this.odoManager.findSkuByIdToShard(skuId, ouId);
            String skuCode = sku.getCode();
            for (Long odoId : odoIdList) {
                WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
                odo.setDistributeMode(distributeMode);
                odo.setDistributionCode(skuCode);
                odo.setIsAllowMerge(false);
                try {
                    this.odoManager.updateByVersion(odo);
                } catch (Exception e) {
                    // 更新失败，则再试一次
                    odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
                    this.odoManager.updateByVersion(odo);
                }
                this.cacheManager.remove(CacheKeyConstant.SECKILL_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
            }
        } catch (Exception e) {

        }
    }


    @Override
    public Map<String, List<Long>> getSuitsOdoList(Long ouId) {
        List<String> keys = this.cacheManager.Keys(CacheKeyConstant.SUITS_ODO_PREFIX + ouId + "|*");
        Map<String, List<Long>> map = new HashMap<String, List<Long>>();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                String[] keyArray = key.split("%");
                String[] codeOdoIdArray = keyArray[2].split("\\|");
                String code = codeOdoIdArray[0] + "|" + codeOdoIdArray[1] + "|" + codeOdoIdArray[2] + "|" + codeOdoIdArray[3];
                Long odoId = Long.parseLong(codeOdoIdArray[4]);
                if (map.containsKey(code)) {
                    map.get(code).add(odoId);
                } else {
                    List<Long> odoIdList = new ArrayList<Long>();
                    odoIdList.add(odoId);
                    map.put(code, odoIdList);
                }
            }
        }
        return map;
    }


    @Override
    public void initSuitsDistributionMode(String code, Long ouId, List<Long> odoIdList, String distributeMode) {
        try {
            String[] codeArray = code.split("\\|");
            String[] skuIdArray = codeArray[3].substring(1, codeArray[3].length() - 1).split("\\$");
            String skuCode = "";
            for (int i = 0; i < skuIdArray.length; i++) {
                Sku sku = this.odoManager.findSkuByIdToShard(Long.parseLong(skuIdArray[i]), ouId);
                if (i < skuIdArray.length - 1) {

                    skuCode += sku.getCode() + ",";
                } else {
                    skuCode += sku.getCode();
                }
            }

            for (Long odoId : odoIdList) {
                WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
                odo.setDistributeMode(distributeMode);
                odo.setDistributionCode(skuCode);
                odo.setIsAllowMerge(false);
                try {
                    this.odoManager.updateByVersion(odo);
                } catch (Exception e) {
                    // 更新失败，则再试一次
                    odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
                    this.odoManager.updateByVersion(odo);
                }
                this.cacheManager.remove(CacheKeyConstant.SUITS_ODO_PREFIX + code + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
            }
        } catch (Exception e) {

        }

    }


    @Override
    public Map<String, List<Long>> getTwoSuitsOdoList(Long ouId) {
        List<String> keys = this.cacheManager.Keys(CacheKeyConstant.TWOSKUSUIT_ODO_PREFIX + ouId + "|*");
        Map<String, List<Long>> map = new HashMap<String, List<Long>>();
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                String[] keyArray = key.split("%");
                String[] codeOdoIdArray = keyArray[2].split("\\|");
                String code = codeOdoIdArray[0] + "|" + codeOdoIdArray[1] + "|" + codeOdoIdArray[2] + "|" + codeOdoIdArray[3];
                Long odoId = Long.parseLong(codeOdoIdArray[4]);
                if (map.containsKey(code)) {
                    map.get(code).add(odoId);
                } else {
                    List<Long> odoIdList = new ArrayList<Long>();
                    odoIdList.add(odoId);
                    map.put(code, odoIdList);
                }
            }
        }
        return map;
    }


    @Override
    public void initTwoSuitsDistributionMode(String code, Long ouId, List<Long> odoIdList, String distributeMode) {
        try {
            String[] codeArray = code.split("\\|");
            Long skuId = Long.parseLong(codeArray[3]);
            Sku sku = this.odoManager.findSkuByIdToShard(skuId, ouId);
            String skuCode = sku.getCode();
            String twoSkuSuitPrefix = codeArray[0] + CacheKeyConstant.WAVE_ODO_SPLIT + codeArray[1] + CacheKeyConstant.WAVE_ODO_SPLIT + codeArray[2];
            for (Long odoId : odoIdList) {
                WhOdo odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
                odo.setDistributeMode(distributeMode);
                odo.setDistributionCode(skuCode);
                odo.setIsAllowMerge(false);
                try {
                    this.odoManager.updateByVersion(odo);
                } catch (Exception e) {
                    // 更新失败，则再试一次
                    odo = this.odoManager.findOdoByIdOuId(odoId, ouId);
                    this.odoManager.updateByVersion(odo);
                }
                this.cacheManager.remove(CacheKeyConstant.TWOSKUSUIT_PREFIX + twoSkuSuitPrefix + CacheKeyConstant.WAVE_ODO_SPLIT + skuId + CacheKeyConstant.WAVE_ODO_SPLIT + odoId);
            }
        } catch (Exception e) {

        }
    }


    @Override
    public List<String> findDistinctCounterCode(Long ouId) {
        log.info(this.getClass().getSimpleName() + ".findDistinctCounterCode params:[ouId:{}]", ouId);
        return this.odoManager.findDistinctCounterCode(ouId);
    }


    @Override
    public List<Long> findOdoByCounterCode(String counterCode, Long ouId) {
        log.info(this.getClass().getSimpleName() + ".findOdoByCounterCode params [counterCode:{},ouId:{}]", counterCode, ouId);
        return this.odoManager.findOdoByCounterCode(counterCode, ouId);
    }

    @Override
    public List<Long> findOdoByCounterCodeToCalcDistributeMode(String counterCode, Long ouId) {
        log.info(this.getClass().getSimpleName() + ".findOdoByCounterCode params [counterCode:{},ouId:{}]", counterCode, ouId);
        return this.odoManager.findOdoByCounterCodeToCalcDistributeMode(counterCode, ouId);
    }


    @Override
    public boolean breakCounter(Long ouId) {
        log.info(this.getClass().getSimpleName() + ".breakCounter params [ouId:{}]", ouId);
        boolean flag = true;
        try {

            flag = this.cacheManager.remonKeys(CacheKeyConstant.OU_ODO_PREFIX + ouId + "*");
            log.info(this.getClass().getSimpleName() + ".breakCounter REMOVE cache OU_ODO returns {}", flag);
            if (!flag) {
                return flag;
            }
            flag = this.cacheManager.remonKeys(CacheKeyConstant.SECKILL_ODO_PREFIX + ouId + "*");
            log.info(this.getClass().getSimpleName() + ".breakCounter REMOVE cache SECKILL_ODO returns {}", flag);
            if (!flag) {
                return flag;
            }
            List<String> seckillKeys = this.cacheManager.Keys(CacheKeyConstant.SECKILL_PREFIX + ouId + "*");
            if (seckillKeys != null && seckillKeys.size() > 0) {
                for (String key : seckillKeys) {
                    String[] keyArray = key.split("%");
                    String code = keyArray[2];
                    long i = this.cacheManager.incr(CacheKeyConstant.SECKILL_PREFIX + code);
                    this.cacheManager.decrBy(CacheKeyConstant.SECKILL_PREFIX + code, (int) i);
                }
            }
            flag = this.cacheManager.remonKeys(CacheKeyConstant.SECKILL_PREFIX + ouId + "*");
            log.info(this.getClass().getSimpleName() + ".breakCounter REMOVE cache SECKILL returns {}", flag);
            if (!flag) {
                return flag;
            }

            flag = this.cacheManager.remonKeys(CacheKeyConstant.TWOSKUSUIT_ODO_PREFIX + ouId + "*");
            log.info(this.getClass().getSimpleName() + ".breakCounter REMOVE cache TWOSKUSUIT_ODO returns {}", flag);
            if (!flag) {
                return flag;
            }

            List<String> twoKeys = this.cacheManager.Keys(CacheKeyConstant.TWOSKUSUIT_PREFIX + ouId + "*");

            if (twoKeys != null && twoKeys.size() > 0) {
                for (String key : twoKeys) {
                    String[] keyArray = key.split("%");
                    String code = keyArray[2];
                    long i = this.cacheManager.incr(CacheKeyConstant.TWOSKUSUIT_PREFIX + code);
                    this.cacheManager.decrBy(CacheKeyConstant.TWOSKUSUIT_PREFIX + code, (int) i);
                }
            }
            flag = this.cacheManager.remonKeys(CacheKeyConstant.TWOSKUSUIT_PREFIX + ouId + "*");
            log.info(this.getClass().getSimpleName() + ".breakCounter REMOVE cache TWOSKUSUIT returns {}", flag);
            if (!flag) {
                return flag;
            }

            flag = this.cacheManager.remonKeys(CacheKeyConstant.SUITS_ODO_PREFIX + ouId + "*");
            log.info(this.getClass().getSimpleName() + ".breakCounter REMOVE cache SUITS_ODO returns {}", flag);
            if (!flag) {
                return flag;
            }

            List<String> suitsKeys = this.cacheManager.Keys(CacheKeyConstant.SUITS_PREFIX + ouId + "*");
            if (suitsKeys != null && suitsKeys.size() > 0) {
                for (String key : suitsKeys) {
                    String[] keyArray = key.split("%");
                    String code = keyArray[2];
                    long i = this.cacheManager.incr(CacheKeyConstant.SUITS_PREFIX + code);
                    this.cacheManager.decrBy(CacheKeyConstant.SUITS_PREFIX + code, (int) i);
                }
            }

            flag = this.cacheManager.remonKeys(CacheKeyConstant.SUITS_PREFIX + ouId + "*");
            log.info(this.getClass().getSimpleName() + ".breakCounter REMOVE cache SUITS returns {}", flag);
            if (!flag) {
                return flag;
            }

            flag = this.cacheManager.remonKeys(CacheKeyConstant.TWOSKUSUIT_DIV_ODO_PREFIX + ouId + "*");
            log.info(this.getClass().getSimpleName() + ".breakCounter REMOVE cache TWOSKUSUIT_DIV_ODO returns {}", flag);
            if (!flag) {
                return flag;
            }

            flag = this.cacheManager.remonKeys(CacheKeyConstant.SUITS_DIV_ODO_PREFIX + ouId + "*");
            log.info(this.getClass().getSimpleName() + ".breakCounter REMOVE cache SUITS_DIV_ODO returns {}", flag);
            if (!flag) {
                return flag;
            }
        } catch (Exception e) {
            log.error(e + "");
            return false;
        }

        return flag;
    }


    @Override
    public void addToWhDistributionModeArithmeticPool(String counterCode, Long odoId) {
        try {

            this.distributionModeArithmeticManagerProxy.addToWhDistributionModeArithmeticPool(counterCode, odoId);
        } catch (Exception e) {
            log.error(e + "");
        }
    }


    @Override
    public void divFromOrderPool(String counterCode, Long odoId) {
        this.distributionModeArithmeticManagerProxy.divFromOrderPool(counterCode, odoId);
    }


    @Override
    public void getCounter(String string, Long skuIdA) {
        System.out.println("sku[" + skuIdA + "]:");
        long a = this.cacheManager.incr(CacheKeyConstant.TWOSKUSUIT_PREFIX + string + "|" + skuIdA);
        this.cacheManager.decr(CacheKeyConstant.TWOSKUSUIT_PREFIX + string + "|" + skuIdA);
        System.out.println("counter:" + (a - 1));

        List<String> keys = this.cacheManager.Keys(CacheKeyConstant.TWOSKUSUIT_ODO_PREFIX + string + "|" + skuIdA + "|*");
        System.out.println("ou_count[" + skuIdA + "]:" + keys.size());
        for (String key : keys) {
            System.out.println("key:" + key);
        }

    }


    @Override
    public void printCache(long l) {
        List<String> key3s = this.cacheManager.Keys(CacheKeyConstant.WMS_CACHE_SYS_DICTIONARY + "*");
        System.out.println("key3s:" + key3s);
        boolean flag = this.cacheManager.remonKeys(CacheKeyConstant.WMS_CACHE_SYS_DICTIONARY + "*");
        System.out.println("key3s:" + flag);
        if (1 == 1) {
            return;
        }
        List<String> key2s = this.cacheManager.Keys(CacheKeyConstant.TWOSKUSUIT_PREFIX + l + "|*");
        System.out.println("counter[ ]:" + key2s.size());
        for (String key : key2s) {
            String[] array = key.split("%");
            System.out.println("key:" + key + ";count:" + this.cacheManager.getValue(CacheKeyConstant.TWOSKUSUIT_PREFIX + array[2]));
        }

        List<String> key1s = this.cacheManager.Keys(CacheKeyConstant.TWOSKUSUIT_ODO_PREFIX + l + "|*");
        System.out.println("ou_count[ ]:" + key1s.size());
        for (String key : key1s) {
            System.out.println("key:" + key);
        }
    }


    public Map<String, Integer> sortMapByValue(Map<String, Integer> oriMap) {
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(oriMap.entrySet());
            Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
                    int value1 = 0, value2 = 0;
                    try {
                        value1 = entry1.getValue().intValue();
                        value2 = entry2.getValue().intValue();
                    } catch (NumberFormatException e) {
                        value1 = 0;
                        value2 = 0;
                    }
                    return value2 - value1;
                }
            });
            Iterator<Map.Entry<String, Integer>> iter = entryList.iterator();
            Map.Entry<String, Integer> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }


}
