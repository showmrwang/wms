package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayDeque;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OperationLineCacheCommand extends BaseCommand{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8243021325918156706L;
    /** 周转箱列表*/
    private Set<Long> turnoverBoxs;
    /** 缓存库位队列 */
    private ArrayDeque<Long> tipLocationIds = new ArrayDeque<Long>();
    /**有小车有出库箱的情况下:出库箱的队列*/
    private ArrayDeque<String> tipOutBonxBoxIds = new ArrayDeque<String>();


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
    

}
