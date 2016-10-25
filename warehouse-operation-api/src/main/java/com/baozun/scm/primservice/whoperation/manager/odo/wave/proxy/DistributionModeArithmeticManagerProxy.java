package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;

/**
 * 配货模式计算类
 * @author Administrator
 *
 */
public interface DistributionModeArithmeticManagerProxy extends BaseManager {
    /**
     * 
     * @param ouId
     * @param skuNumberOfPackages
     * @param qty
     * @param skuIdSet
     * @return
     */
    public String getCounterCodeForOdo(Long ouId, Integer skuNumberOfPackages, Double qty, Set<Long> skuIdSet);

    /**
     * 仓库匹配模式计算：从匹配模式计数池中添加
     * 
     * @param code
     * @param odoId
     */
    public void addToWhDistributionModeArithmeticPool(String code, Long odoId);

    /**
     * 仓库匹配模式计算：计数器减；不移除出库单
     * 
     * @param code
     */
    public void DivFromWhDistributionModeArithmeticPool(String code, Long odoId);


    /**
     * 仓库陪陪模式计算：仓库匹配模式计算
     */
    public void DistributionModeArithmetic(String code, Long odoId);



    /**
     * 出库单添加到波次中
     * 
     * @param codeOdoMap
     */
    public void AddToWave(Map<Long, String> codeOdoMap);

    /**
     * 出库单添加到波次中
     * 
     * @param codeOdoMap
     */
    public void AddToWave(String code, Long odoId);

}
