package com.baozun.scm.primservice.whoperation.util.OutBoundBoxCompute;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baozun.scm.primservice.whoperation.command.wave.WaveLineCommand;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;

public class OutBoundBoxCompute {

    public static final Logger log = LoggerFactory.getLogger(OutBoundBoxCompute.class);

    /** 出库箱体积 */
    private static double tempVolume = 0.00;

    /***
     * 通过波次明细对应商品数量/体积+出库箱体积计算多少商品能放入对应出库箱 放入出库箱的商品从传入的List<WaveLine>波次明细中扣除相应数量 出库单绑定出库箱/容器数据累加
     * 
     * @param waveLineList 对应波次明细信息
     * @param Map<String=出库单ID-出库单明细ID-出库箱类型ID/容器ID, WhOdoOutBoundBox> oobbMap 出库单绑定出库箱/容器信息
     * @param boxTotalVolume 出库箱体积
     * @param outBoundBoxType 出库箱类型ID
     * @param containerId 容器ID
     * @return 累计放入出库箱商品总体积
     */
    public static Double obbCompute(List<WaveLineCommand> waveLineList, Map<String, WhOdoOutBoundBox> oobbMap, Double boxTotalVolume, Long outBoundBoxType, Long containerId) {
        log.info("OutBoundBoxCompute.compute method begin!");
        if (null == waveLineList) {
            log.warn("OutBoundBoxCompute.compute waveLineList is null");
            return null;
        }
        if (null == boxTotalVolume) {
            log.warn("OutBoundBoxCompute.compute boxTotalVolume is null");
            return null;
        }
        if (waveLineList.size() == 0) {
            log.warn("OutBoundBoxCompute.compute aveLineList.size() == 0");
            return null;
        }
        List<OutBoundBox> bList = new ArrayList<OutBoundBox>();
        // 封装计算数据
        for (WaveLineCommand w : waveLineList) {
            for (int i = 0; i < w.getQty(); i++) {
                OutBoundBox obb = new OutBoundBox();
                // 商品体积
                obb.setVolume(w.getSkuVolume());
                obb.setWaveLine(w.getId());
                bList.add(obb);
            }
        }
        // 封装成数组
        OutBoundBox[] bags = bList.toArray(new OutBoundBox[] {});
        // 对商品体积进行排序
        skuVolumeSort(bags, boxTotalVolume);
        // 计算多少商品能放入对应出库箱
        Map<Long, Integer> solveResult = solve(bags, boxTotalVolume);
        // 调整波次明细对应数量
        revisionWaveLineQty(waveLineList, oobbMap, solveResult, outBoundBoxType, containerId);
        log.info("OutBoundBoxCompute.compute method end!");
        return tempVolume;
    }

    /***
     * 调整波次明细对应数量 放入出库箱商品数量扣减 如果波次明细商品已经全部放入出库箱 删除对应波次明细
     * 
     * @param waveLineList
     */
    private static void revisionWaveLineQty(List<WaveLineCommand> waveLineList, Map<String, WhOdoOutBoundBox> oobbMap, Map<Long, Integer> solveResult, Long outBoundBoxType, Long containerId) {
        for (int i = 0; i < waveLineList.size(); i++) {
            // 判断放入出库箱商品是否存在
            if (solveResult.containsKey(waveLineList.get(i).getId())) {
                // 出库单绑定出库箱/容器信息累加
                WhOdoOutBoundBox oobb = new WhOdoOutBoundBox();
                String oobbKey = waveLineList.get(i).getOdoId() + "-" + waveLineList.get(i).getOdoLineId();
                if (null == outBoundBoxType) {
                    // 容器
                    if (oobbMap.containsKey(oobbKey + "-" + containerId)) {
                        // 如果有对应的信息 数量累加
                        oobb = oobbMap.get(oobbKey + "-" + containerId);
                        oobb.setQty(oobb.getQty() + solveResult.get(waveLineList.get(i).getId()));
                        oobbMap.put(oobbKey + "-" + containerId, oobb);
                    } else {
                        // 否则新增信息
                        oobb.setOdoId(waveLineList.get(i).getOdoId());
                        oobb.setOdoLineId(waveLineList.get(i).getOdoLineId());
                        oobb.setOuterContainerId(containerId);
                        oobb.setQty(solveResult.get(waveLineList.get(i).getId()).doubleValue());
                        oobbMap.put(oobbKey + "-" + containerId, oobb);
                    }
                } else {
                    // 出库箱类型
                    if (oobbMap.containsKey(oobbKey + "-" + outBoundBoxType)) {
                        // 如果有对应的信息 数量累加
                        oobb = oobbMap.get(oobbKey + "-" + outBoundBoxType);
                        oobb.setQty(oobb.getQty() + solveResult.get(waveLineList.get(i).getId()));
                        oobbMap.put(oobbKey + "-" + outBoundBoxType, oobb);
                    } else {
                        // 否则新增信息
                        oobb.setOdoId(waveLineList.get(i).getOdoId());
                        oobb.setOdoLineId(waveLineList.get(i).getOdoLineId());
                        oobb.setOutbounxboxTypeId(outBoundBoxType);
                        oobb.setQty(solveResult.get(waveLineList.get(i).getId()).doubleValue());
                        oobbMap.put(oobbKey + "-" + outBoundBoxType, oobb);
                    }
                }
                int qty = waveLineList.get(i).getQty().intValue() - solveResult.get(waveLineList.get(i).getId());
                if (qty == 0) {
                    // 如果全部放入出库箱 删除对应波次明细数据
                    waveLineList.remove(i);
                    i--;
                } else {
                    // 否则扣除对应波次明细数量
                    waveLineList.get(i).setQty(Double.valueOf(qty));
                }
            }
        }
    }

    /***
     * 商品体积排序
     * 
     * @param boxs
     * @param totalVolume
     */
    private static void skuVolumeSort(OutBoundBox[] boxs, double totalVolume) {
        // 对商品体积从大到小排序
        Arrays.sort(boxs, Collections.reverseOrder());
    }

    /**
     * 商品放入出库箱计算
     * 
     * @return
     */
    private static Map<Long, Integer> solve(OutBoundBox[] boxs, Double boxTotalVolume) {
        Map<Long, Integer> returnMap = new HashMap<Long, Integer>();
        DecimalFormat df = new DecimalFormat("######0.00");
        for (int i = 0; i < boxs.length; i++) {
            // 判断当前商品是否可以放入出库箱中，若不能查找下一个商品
            if (boxTotalVolume - boxs[i].getVolume() < 0.00) continue;
            // 出库箱体积-本次放入商品体积
            boxTotalVolume = Double.valueOf(df.format(boxTotalVolume -= boxs[i].getVolume()));
            // 累加放入出库箱商品的体积
            tempVolume = Double.valueOf(df.format(tempVolume += boxs[i].getVolume()));
            Long key = boxs[i].getWaveLineId();
            // 判断是否有对应波次明细商品已放入过此出库箱
            if (returnMap.containsKey(key)) {
                // 有 累加商品数量
                returnMap.put(key, returnMap.get(key) + 1);
            } else {
                // 没有 商品数量为1
                returnMap.put(key, 1);
            }
        }
        return returnMap;
    }

}
