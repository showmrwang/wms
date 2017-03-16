package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OperationLineCacheCommand extends BaseCommand{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8243021325918156706L;
    /**短拣作业明细id集合*/
    private Set<Long> shortPikcingOperIds;
    /**非短拣作业明细id集合*/
    private Set<Long> pickingOperIds;
    /**作业明细id对应的拣货数量*/
    private Map<Long,Double> operLineIdToQty;
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

    public Set<Long> getShortPikcingOperIds() {
        return shortPikcingOperIds;
    }

    public void setShortPikcingOperIds(Set<Long> shortPikcingOperIds) {
        this.shortPikcingOperIds = shortPikcingOperIds;
    }

    public Set<Long> getPickingOperIds() {
        return pickingOperIds;
    }

    public void setPickingOperIds(Set<Long> pickingOperIds) {
        this.pickingOperIds = pickingOperIds;
    }

    public Map<Long, Double> getOperLineIdToQty() {
        return operLineIdToQty;
    }

    public void setOperLineIdToQty(Map<Long, Double> operLineIdToQty) {
        this.operLineIdToQty = operLineIdToQty;
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

    
    

}
