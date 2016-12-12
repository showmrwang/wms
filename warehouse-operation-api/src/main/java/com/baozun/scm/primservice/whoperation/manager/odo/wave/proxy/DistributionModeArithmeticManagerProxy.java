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
     * 是否存在订单池中
     * 
     * @param code
     * @param odoId
     * @return
     */
    public boolean isExistsInOrderPool(String code, Long odoId);

    /**
     * 仓库匹配模式计算;
     * 
     * @param code
     */
    public void divFromOrderPool(String code, Long odoId);
    
    /**
     * 从订单池中移除
     * @param code
     */
    public void removeFromOrderPool(String code, Long odoId);

    /**
     * 从订单池中移除
     * 
     * @param code
     */
    public void changeFromOrderPool(String oldCode, String newCode, Long odoId);


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

    /**
     * 出库单合并
     */
    public void mergeOdo(String newCode, Long odoId, Map<Long, String> mergedOdoMp);

    /**
     * 取消合并出库单
     * 
     * @param mergedCounterCode
     * @param odoId
     * @param reNewOdoMap
     */
    public void CancelFormergeOdo(String mergedCounterCode, Long odoId, Map<Long, String> reNewOdoMap);
}
