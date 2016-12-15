package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OperationLineCacheCommand extends BaseCommand{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
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
    
//    /**出库箱拣货的作业明细*/
//    private Map<String,Set<Long>> outBoundxBoxOpLineIdMap;
//    
//    /**周转箱拣货的作业明细*/
//    private Map<Long,Set<Long>> turnoverBoxsOpLineIdMap;
    
    
//    /**出库箱拣货的作业执行明细*/
//    private Map<String,Set<WhOperationExecLine>> outBoundxBoxOpLineExecIdMap;
//    
//    /**周转箱拣货的作业执行明细*/
//    private Map<Long,Set<WhOperationExecLine>> turnoverBoxsOpLineExecIdMap;
    
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

//    public Map<String, Set<Long>> getOutBoundxBoxOpLineIdMap() {
//        return outBoundxBoxOpLineIdMap;
//    }
//
//    public void setOutBoundxBoxOpLineIdMap(Map<String, Set<Long>> outBoundxBoxOpLineIdMap) {
//        this.outBoundxBoxOpLineIdMap = outBoundxBoxOpLineIdMap;
//    }
//
//    public Map<Long, Set<Long>> getTurnoverBoxsOpLineIdMap() {
//        return turnoverBoxsOpLineIdMap;
//    }
//
//    public void setTurnoverBoxsOpLineIdMap(Map<Long, Set<Long>> turnoverBoxsOpLineIdMap) {
//        this.turnoverBoxsOpLineIdMap = turnoverBoxsOpLineIdMap;
//    }

//    public Map<String, Set<WhOperationExecLine>> getOutBoundxBoxOpLineExecIdMap() {
//        return outBoundxBoxOpLineExecIdMap;
//    }
//
//    public void setOutBoundxBoxOpLineExecIdMap(Map<String, Set<WhOperationExecLine>> outBoundxBoxOpLineExecIdMap) {
//        this.outBoundxBoxOpLineExecIdMap = outBoundxBoxOpLineExecIdMap;
//    }
//
//    public Map<Long, Set<WhOperationExecLine>> getTurnoverBoxsOpLineExecIdMap() {
//        return turnoverBoxsOpLineExecIdMap;
//    }
//
//    public void setTurnoverBoxsOpLineExecIdMap(Map<Long, Set<WhOperationExecLine>> turnoverBoxsOpLineExecIdMap) {
//        this.turnoverBoxsOpLineExecIdMap = turnoverBoxsOpLineExecIdMap;
//    }

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
