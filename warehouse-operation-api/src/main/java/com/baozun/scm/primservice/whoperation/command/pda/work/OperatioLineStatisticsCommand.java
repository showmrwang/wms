package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOperationLineCommand;
/**
 * 作业明细统计
 * 
 * @author qiming.liu
 *
 */
public class OperatioLineStatisticsCommand extends BaseCommand{

    /**
     * 
     */
    private static final long serialVersionUID = -9131853146971418614L;

    //columns START
    
    private Long pickingWay; 
    
    //流程相关统计信息 
    /** 是否整托整箱 */
    private List<WhOperationLineCommand> wholeCaseList;
    /** 所有小车 */
    private List<WhOperationLineCommand> outerContainerList;
    /** 所有出库箱 */
    private List<WhOperationLineCommand> outbounxBoxList;
    /** 小车货格与出库箱对应关系 */
    private List<WhOperationLineCommand> childList;
    /** 所有周转箱 */
    private List<WhOperationLineCommand> turnoverBoxList;
    /** 所有托盘 */
    private List<WhOperationLineCommand> palletList;
    /** 所有货箱 */
    private List<WhOperationLineCommand> containerList;
    
    // 库位商品统计信息 
    /** 所有库位 */
    private Set<Long> locationIdList = new HashSet<Long>();
    /** 库位上所有外部容器 */
    private Map<Long, Set<Long>> outerContainerIds = new HashMap<Long, Set<Long>>();
    /** 库位上所有内部容器 */
    private Map<Long, Set<Long>> insideContainerIds = new HashMap<Long, Set<Long>>();
    /** 库位上所有sku */
    private Map<Long, Set<Long>> skuIds = new HashMap<Long, Set<Long>>();
    /** 库位上每个sku总件数 */
    private Map<Long, Map<Long, Set<Long>>> skuQty = new HashMap<Long, Map<Long, Set<Long>>>();;
    /** 库位上每个sku对应的唯一sku及件数 */
    private Map<Long, Set<Long>> skuAttrIds = new HashMap<Long, Set<Long>>();
    /** 库位上每个唯一sku对应的所有sn及残次条码 */
    private Map<Long, Map<Long, Set<Long>>> skuAttrIdsSnDefect = new HashMap<Long, Map<Long, Set<Long>>>();
    /** 库位上每个唯一sku对应的货格（is_whole_case=0&&有小车&&库位上sku不在任何容器内） */
    private Map<Long, Map<Long, Set<Long>>> skuAttrIdsContainerLattice = new HashMap<Long, Map<Long, Set<Long>>>();
    /** 外部容器对应所有内部容器 */
    private Map<Long, Map<Long, Set<Long>>> outerToInside;
    /** 内部容器对应所有sku*/
    private Map<Long, Map<Long, Map<Long, Set<Long>>>> insideSkuIds;
    /** 内部容器每个sku总件数 */
    private Map<Long, Map<Long, Map<Long, Set<Long>>>> insideSkuQty;
    /** 内部容器每个sku对应的唯一sku及件数 */
    private Map<Long, Map<Long, Map<Long, Set<Long>>>> insideSkuAttrIds;
    /** 内部容器每个唯一sku对应的所有sn及残次条码 */
    private Map<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>> insideSkuAttrIdsSnDefect;
    /** 内部容器每个唯一sku对应的货格（is_whole_case=0&&有小车） */
    private Map<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>> insideSkuAttrIdsContainerLattice;
    
    //columns END
    
    public List<WhOperationLineCommand> getWholeCaseList() {
        return wholeCaseList;
    }
    public void setWholeCaseList(List<WhOperationLineCommand> wholeCaseList) {
        this.wholeCaseList = wholeCaseList;
    }
    public List<WhOperationLineCommand> getOuterContainerList() {
        return outerContainerList;
    }
    public void setOuterContainerList(List<WhOperationLineCommand> outerContainerList) {
        this.outerContainerList = outerContainerList;
    }
    public List<WhOperationLineCommand> getOutbounxBoxList() {
        return outbounxBoxList;
    }
    public void setOutbounxBoxList(List<WhOperationLineCommand> outbounxBoxList) {
        this.outbounxBoxList = outbounxBoxList;
    }
    public List<WhOperationLineCommand> getChildList() {
        return childList;
    }
    public void setChildList(List<WhOperationLineCommand> childList) {
        this.childList = childList;
    }
    public List<WhOperationLineCommand> getTurnoverBoxList() {
        return turnoverBoxList;
    }
    public void setTurnoverBoxList(List<WhOperationLineCommand> turnoverBoxList) {
        this.turnoverBoxList = turnoverBoxList;
    }
    public List<WhOperationLineCommand> getPalletList() {
        return palletList;
    }
    public void setPalletList(List<WhOperationLineCommand> palletList) {
        this.palletList = palletList;
    }
    public List<WhOperationLineCommand> getContainerList() {
        return containerList;
    }
    public void setContainerList(List<WhOperationLineCommand> containerList) {
        this.containerList = containerList;
    }
    public Set<Long> getLocationIdList() {
        return locationIdList;
    }
    public void setLocationIdList(Set<Long> locationIdList) {
        this.locationIdList = locationIdList;
    }
    public Map<Long, Set<Long>> getOuterContainerIds() {
        return outerContainerIds;
    }
    public void setOuterContainerIds(Map<Long, Set<Long>> outerContainerIds) {
        this.outerContainerIds = outerContainerIds;
    }
    public Map<Long, Set<Long>> getInsideContainerIds() {
        return insideContainerIds;
    }
    public void setInsideContainerIds(Map<Long, Set<Long>> insideContainerIds) {
        this.insideContainerIds = insideContainerIds;
    }
    public Map<Long, Set<Long>> getSkuIds() {
        return skuIds;
    }
    public void setSkuIds(Map<Long, Set<Long>> skuIds) {
        this.skuIds = skuIds;
    }
    public Map<Long, Map<Long, Set<Long>>> getSkuQty() {
        return skuQty;
    }
    public void setSkuQty(Map<Long, Map<Long, Set<Long>>> skuQty) {
        this.skuQty = skuQty;
    }
    public Map<Long, Set<Long>> getSkuAttrIds() {
        return skuAttrIds;
    }
    public void setSkuAttrIds(Map<Long, Set<Long>> skuAttrIds) {
        this.skuAttrIds = skuAttrIds;
    }
    public Map<Long, Map<Long, Set<Long>>> getSkuAttrIdsSnDefect() {
        return skuAttrIdsSnDefect;
    }
    public void setSkuAttrIdsSnDefect(Map<Long, Map<Long, Set<Long>>> skuAttrIdsSnDefect) {
        this.skuAttrIdsSnDefect = skuAttrIdsSnDefect;
    }
    public Map<Long, Map<Long, Set<Long>>> getSkuAttrIdsContainerLattice() {
        return skuAttrIdsContainerLattice;
    }
    public void setSkuAttrIdsContainerLattice(Map<Long, Map<Long, Set<Long>>> skuAttrIdsContainerLattice) {
        this.skuAttrIdsContainerLattice = skuAttrIdsContainerLattice;
    }
    public Map<Long, Map<Long, Set<Long>>> getOuterToInside() {
        return outerToInside;
    }
    public void setOuterToInside(Map<Long, Map<Long, Set<Long>>> outerToInside) {
        this.outerToInside = outerToInside;
    }
    public Map<Long, Map<Long, Map<Long, Set<Long>>>> getInsideSkuIds() {
        return insideSkuIds;
    }
    public void setInsideSkuIds(Map<Long, Map<Long, Map<Long, Set<Long>>>> insideSkuIds) {
        this.insideSkuIds = insideSkuIds;
    }
    public Map<Long, Map<Long, Map<Long, Set<Long>>>> getInsideSkuQty() {
        return insideSkuQty;
    }
    public void setInsideSkuQty(Map<Long, Map<Long, Map<Long, Set<Long>>>> insideSkuQty) {
        this.insideSkuQty = insideSkuQty;
    }
    public Map<Long, Map<Long, Map<Long, Set<Long>>>> getInsideSkuAttrIds() {
        return insideSkuAttrIds;
    }
    public void setInsideSkuAttrIds(Map<Long, Map<Long, Map<Long, Set<Long>>>> insideSkuAttrIds) {
        this.insideSkuAttrIds = insideSkuAttrIds;
    }
    public Map<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>> getInsideSkuAttrIdsSnDefect() {
        return insideSkuAttrIdsSnDefect;
    }
    public void setInsideSkuAttrIdsSnDefect(Map<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>> insideSkuAttrIdsSnDefect) {
        this.insideSkuAttrIdsSnDefect = insideSkuAttrIdsSnDefect;
    }
    public Map<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>> getInsideSkuAttrIdsContainerLattice() {
        return insideSkuAttrIdsContainerLattice;
    }
    public void setInsideSkuAttrIdsContainerLattice(Map<Long, Map<Long, Map<Long, Map<Long, Set<Long>>>>> insideSkuAttrIdsContainerLattice) {
        this.insideSkuAttrIdsContainerLattice = insideSkuAttrIdsContainerLattice;
    }
    public Long getPickingWay() {
        return pickingWay;
    }
    public void setPickingWay(Long pickingWay) {
        this.pickingWay = pickingWay;
    }
    
}
