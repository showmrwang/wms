package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
/**
 * 作业明细统计
 * 
 * @author qiming.liu
 *
 */
public class OperationExecStatisticsCommand extends BaseCommand{

    /**
     * 
     */
    private static final long serialVersionUID = -9131853146971418614L;

    //columns START
    
    //流程相关统计信息 
    /** 是否整托整箱 */
    private Boolean isWholeCase;
    /** 所有托盘 */
    private Set<Long> pallets = new HashSet<Long>();
    /** 所有货箱 */
    private Set<Long> containers= new HashSet<Long>();
    
    // 库位商品统计信息 
    /** 所有目标库位 */
    private List<Long> locationIds = new ArrayList<Long>();
    /** 目标库位对应的所有周转箱 */
    private Map<Long, Set<Long>> turnoverBoxIds = new HashMap<Long, Set<Long>>();
    /** 周转箱对应的所有sku */
    private Map<Long, Set<Long>> skuIds = new HashMap<Long, Set<Long>>();
    /** 周转箱每个sku总件数 */
    private Map<Long, Map<Long, Long>> skuQty = new HashMap<Long, Map<Long, Long>>();
    /** 周转箱每个sku对应的唯一sku及件数 */
    private Map<Long, Map<Long, Map<String, Long>>> skuAttrIds = new HashMap<Long, Map<Long, Map<String, Long>>>();
    /** 周转箱每个唯一sku对应的所有sn及残次条码 */
    private Map<Long, Map<String, Set<String>>> skuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();
    /** 目标库位对应的所有外部容器（整托整箱） */
    private Map<Long, Set<Long>> outerContainerIds = new HashMap<Long, Set<Long>>();
    /** 外部容器对应所有内部容器（整托整箱） */
    private Map<Long, Set<Long>> outerToInside = new HashMap<Long, Set<Long>>();
    /** 内部容器对应所有sku（整托整箱）*/
    private Map<Long, Set<Long>> insideSkuIds = new HashMap<Long, Set<Long>>();
    /** 内部容器每个sku总件数（整托整箱） */
    private Map<Long, Map<Long, Long>> insideSkuQty = new HashMap<Long, Map<Long, Long>>();
    /** 内部容器每个sku对应的唯一sku及件数（整托整箱） */
    private Map<Long, Map<Long, Map<String, Long>>> insideSkuAttrIds = new HashMap<Long, Map<Long, Map<String, Long>>>();
    /** 内部容器每个唯一sku对应的所有sn及残次条码 */
    private Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();
    
    //columns END
    
    public Boolean getIsWholeCase() {
        return isWholeCase;
    }
    public void setIsWholeCase(Boolean isWholeCase) {
        this.isWholeCase = isWholeCase;
    }
    public Set<Long> getPallets() {
        return pallets;
    }
    public void setPallets(Set<Long> pallets) {
        this.pallets = pallets;
    }
    public Set<Long> getContainers() {
        return containers;
    }
    public void setContainers(Set<Long> containers) {
        this.containers = containers;
    }
    public List<Long> getLocationIds() {
        return locationIds;
    }
    public void setLocationIds(List<Long> locationIds) {
        this.locationIds = locationIds;
    }
    public Map<Long, Set<Long>> getTurnoverBoxIds() {
        return turnoverBoxIds;
    }
    public void setTurnoverBoxIds(Map<Long, Set<Long>> turnoverBoxIds) {
        this.turnoverBoxIds = turnoverBoxIds;
    }
    public Map<Long, Set<Long>> getSkuIds() {
        return skuIds;
    }
    public void setSkuIds(Map<Long, Set<Long>> skuIds) {
        this.skuIds = skuIds;
    }
    public Map<Long, Map<Long, Long>> getSkuQty() {
        return skuQty;
    }
    public void setSkuQty(Map<Long, Map<Long, Long>> skuQty) {
        this.skuQty = skuQty;
    }
    public Map<Long, Map<Long, Map<String, Long>>> getSkuAttrIds() {
        return skuAttrIds;
    }
    public void setSkuAttrIds(Map<Long, Map<Long, Map<String, Long>>> skuAttrIds) {
        this.skuAttrIds = skuAttrIds;
    }
    public Map<Long, Map<String, Set<String>>> getSkuAttrIdsSnDefect() {
        return skuAttrIdsSnDefect;
    }
    public void setSkuAttrIdsSnDefect(Map<Long, Map<String, Set<String>>> skuAttrIdsSnDefect) {
        this.skuAttrIdsSnDefect = skuAttrIdsSnDefect;
    }
    public Map<Long, Set<Long>> getOuterContainerIds() {
        return outerContainerIds;
    }
    public void setOuterContainerIds(Map<Long, Set<Long>> outerContainerIds) {
        this.outerContainerIds = outerContainerIds;
    }
    public Map<Long, Set<Long>> getOuterToInside() {
        return outerToInside;
    }
    public void setOuterToInside(Map<Long, Set<Long>> outerToInside) {
        this.outerToInside = outerToInside;
    }
    public Map<Long, Set<Long>> getInsideSkuIds() {
        return insideSkuIds;
    }
    public void setInsideSkuIds(Map<Long, Set<Long>> insideSkuIds) {
        this.insideSkuIds = insideSkuIds;
    }
    public Map<Long, Map<Long, Long>> getInsideSkuQty() {
        return insideSkuQty;
    }
    public void setInsideSkuQty(Map<Long, Map<Long, Long>> insideSkuQty) {
        this.insideSkuQty = insideSkuQty;
    }
    public Map<Long, Map<Long, Map<String, Long>>> getInsideSkuAttrIds() {
        return insideSkuAttrIds;
    }
    public void setInsideSkuAttrIds(Map<Long, Map<Long, Map<String, Long>>> insideSkuAttrIds) {
        this.insideSkuAttrIds = insideSkuAttrIds;
    }
    public Map<Long, Map<String, Set<String>>> getInsideSkuAttrIdsSnDefect() {
        return insideSkuAttrIdsSnDefect;
    }
    public void setInsideSkuAttrIdsSnDefect(Map<Long, Map<String, Set<String>>> insideSkuAttrIdsSnDefect) {
        this.insideSkuAttrIdsSnDefect = insideSkuAttrIdsSnDefect;
    }
}
