package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OperatioExecLineStatisticsCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** 是否整托整箱 */
    private Boolean isWholeCase;
    /** 所有托盘 */
    private Set<Long> pallets = new HashSet<Long>();
    /** 所有货箱 */
    private Set<Long> insideContainers = new HashSet<Long>();
    /** 所有库位 */
    private List<Long> locationIds = new ArrayList<Long>();
    /** 目标库位对应所有的周转箱 */
    private Map<Long, Set<Long>> locationToTurnoverBoxIds = new HashMap<Long, Set<Long>>();
    /** 周转箱对应的所有sku */
    private Map<Long, Set<Long>> turnoverBoxToSkuIds = new HashMap<Long, Set<Long>>();
    /** 周转箱每个sku总件数 */
    private Map<Long, Map<Long, Long>> turnoverBoxSkuQty = new HashMap<Long, Map<Long, Long>>();
    /** 周转箱每个sku对应的唯一sku及件数 */
    private Map<Long, Map<Long, Map<String, Long>>> turnoverBoxSkuAttrIdsQty = new HashMap<Long, Map<Long, Map<String, Long>>>();
    /** 周转箱每个唯一sku对应的所有sn及残次条码 */
    private Map<Long, Map<String, Set<String>>> turnoverBoxSkuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>();

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

    public Set<Long> getInsideContainers() {
        return insideContainers;
    }

    public void setInsideContainers(Set<Long> insideContainers) {
        this.insideContainers = insideContainers;
    }

    public List<Long> getLocationIds() {
        return locationIds;
    }

    public void setLocationIds(List<Long> locationIds) {
        this.locationIds = locationIds;
    }

    public Map<Long, Set<Long>> getLocationToTurnoverBoxIds() {
        return locationToTurnoverBoxIds;
    }

    public void setLocationToTurnoverBoxIds(Map<Long, Set<Long>> locationToTurnoverBoxIds) {
        this.locationToTurnoverBoxIds = locationToTurnoverBoxIds;
    }

    public Map<Long, Set<Long>> getTurnoverBoxToSkuIds() {
        return turnoverBoxToSkuIds;
    }

    public void setTurnoverBoxToSkuIds(Map<Long, Set<Long>> turnoverBoxToSkuIds) {
        this.turnoverBoxToSkuIds = turnoverBoxToSkuIds;
    }

    public Map<Long, Map<Long, Long>> getTurnoverBoxSkuQty() {
        return turnoverBoxSkuQty;
    }

    public void setTurnoverBoxSkuQty(Map<Long, Map<Long, Long>> turnoverBoxSkuQty) {
        this.turnoverBoxSkuQty = turnoverBoxSkuQty;
    }

    public Map<Long, Map<Long, Map<String, Long>>> getTurnoverBoxSkuAttrIdsQty() {
        return turnoverBoxSkuAttrIdsQty;
    }

    public void setTurnoverBoxSkuAttrIdsQty(Map<Long, Map<Long, Map<String, Long>>> turnoverBoxSkuAttrIdsQty) {
        this.turnoverBoxSkuAttrIdsQty = turnoverBoxSkuAttrIdsQty;
    }

    public Map<Long, Map<String, Set<String>>> getTurnoverBoxSkuAttrIdsSnDefect() {
        return turnoverBoxSkuAttrIdsSnDefect;
    }

    public void setTurnoverBoxSkuAttrIdsSnDefect(Map<Long, Map<String, Set<String>>> turnoverBoxSkuAttrIdsSnDefect) {
        this.turnoverBoxSkuAttrIdsSnDefect = turnoverBoxSkuAttrIdsSnDefect;
    }


}
