package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OperationLineCacheCommand extends BaseCommand{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8243021325918156706L;
    /**内部容器短拣作业明细id集合*/
    private Map<Long,Set<Long>> insideShortPikcingOperIds;
    /**内部容器非短拣作业明细id集合*/
    private Map<Long,Set<Long>> insidePickingOperIds;
    /**库位上散装sku对应短拣作业明细id集合*/
    private Map<Long,Set<Long>> locShortPikcingOperIds;
    /**库位上散装sku对应非短拣作业明细id集合*/
    private Map<Long,Set<Long>> locPickingOperIds;
    /**作业明细id对应的拣货数量*/
    private List<Map<Long,Double>> operLineIdToQtyList;
    /**出库箱列表*/
    private Set<String> outBoundxBoxs;
    /** 周转箱列表*/
    private Set<Long> turnoverBoxs;
    /** 缓存库位队列 */
    private ArrayDeque<Long> tipLocationIds = new ArrayDeque<Long>();
    /**有小车有出库箱的情况下:出库箱的队列*/
    private ArrayDeque<String> tipOutBonxBoxIds = new ArrayDeque<String>();

    public Set<String> getOutBoundxBoxs() {
        return outBoundxBoxs;
    }

    public void setOutBoundxBoxs(Set<String> outBoundxBoxs) {
        this.outBoundxBoxs = outBoundxBoxs;
    }

    public Set<Long> getTurnoverBoxs() {
        return turnoverBoxs;
    }

    public void setTurnoverBoxs(Set<Long> turnoverBoxs) {
        this.turnoverBoxs = turnoverBoxs;
    }

    public ArrayDeque<Long> getTipLocationIds() {
        return tipLocationIds;
    }

    public void setTipLocationIds(ArrayDeque<Long> tipLocationIds) {
        this.tipLocationIds = tipLocationIds;
    }

    public ArrayDeque<String> getTipOutBonxBoxIds() {
        return tipOutBonxBoxIds;
    }

    public void setTipOutBonxBoxIds(ArrayDeque<String> tipOutBonxBoxIds) {
        this.tipOutBonxBoxIds = tipOutBonxBoxIds;
    }

    public List<Map<Long, Double>> getOperLineIdToQtyList() {
        return operLineIdToQtyList;
    }

    public void setOperLineIdToQtyList(List<Map<Long, Double>> operLineIdToQtyList) {
        this.operLineIdToQtyList = operLineIdToQtyList;
    }

    public Map<Long, Set<Long>> getInsideShortPikcingOperIds() {
        return insideShortPikcingOperIds;
    }

    public void setInsideShortPikcingOperIds(Map<Long, Set<Long>> insideShortPikcingOperIds) {
        this.insideShortPikcingOperIds = insideShortPikcingOperIds;
    }

    public Map<Long, Set<Long>> getInsidePickingOperIds() {
        return insidePickingOperIds;
    }

    public void setInsidePickingOperIds(Map<Long, Set<Long>> insidePickingOperIds) {
        this.insidePickingOperIds = insidePickingOperIds;
    }

    public Map<Long, Set<Long>> getLocShortPikcingOperIds() {
        return locShortPikcingOperIds;
    }

    public void setLocShortPikcingOperIds(Map<Long, Set<Long>> locShortPikcingOperIds) {
        this.locShortPikcingOperIds = locShortPikcingOperIds;
    }

    public Map<Long, Set<Long>> getLocPickingOperIds() {
        return locPickingOperIds;
    }

    public void setLocPickingOperIds(Map<Long, Set<Long>> locPickingOperIds) {
        this.locPickingOperIds = locPickingOperIds;
    }

    
    

}
