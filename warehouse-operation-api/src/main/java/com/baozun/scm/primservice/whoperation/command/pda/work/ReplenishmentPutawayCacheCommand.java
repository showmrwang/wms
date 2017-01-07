package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayDeque;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class ReplenishmentPutawayCacheCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    /** 已经扫描的库位 */
    private ArrayDeque<Long> tipLocationIds = new ArrayDeque<Long>();
    /** 已经扫描的周转箱 */
    private ArrayDeque<Long> tipTurnoverBoxIds = new ArrayDeque<Long>();

    public ArrayDeque<Long> getTipLocationIds() {
        return tipLocationIds;
    }

    public void setTipLocationIds(ArrayDeque<Long> tipLocationIds) {
        this.tipLocationIds = tipLocationIds;
    }

    public ArrayDeque<Long> getTipTurnoverBoxIds() {
        return tipTurnoverBoxIds;
    }

    public void setTipTurnoverBoxIds(ArrayDeque<Long> tipTurnoverBoxIds) {
        this.tipTurnoverBoxIds = tipTurnoverBoxIds;
    }



}
