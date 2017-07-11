package com.baozun.scm.primservice.whoperation.command.pda.work;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class ReplenishmentPutawayCacheCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    /** 已经扫描的库位 */
    private ArrayDeque<Long> tipLocationIds = new ArrayDeque<Long>();
    /** 已经扫描的周转箱 */
    private Map<Long,ArrayDeque<Long>> tipTurnoverBoxIds = new HashMap<Long,ArrayDeque<Long>>();
    /** 已经扫描的托盘 */
    private Map<Long,ArrayDeque<Long>> tipPalletIds = new HashMap<Long,ArrayDeque<Long>>();

    public ArrayDeque<Long> getTipLocationIds() {
        return tipLocationIds;
    }

    public void setTipLocationIds(ArrayDeque<Long> tipLocationIds) {
        this.tipLocationIds = tipLocationIds;
    }

    public Map<Long, ArrayDeque<Long>> getTipTurnoverBoxIds() {
        return tipTurnoverBoxIds;
    }

    public void setTipTurnoverBoxIds(Map<Long, ArrayDeque<Long>> tipTurnoverBoxIds) {
        this.tipTurnoverBoxIds = tipTurnoverBoxIds;
    }

    public Map<Long, ArrayDeque<Long>> getTipPalletIds() {
        return tipPalletIds;
    }

    public void setTipPalletIds(Map<Long, ArrayDeque<Long>> tipPalletIds) {
        this.tipPalletIds = tipPalletIds;
    }


}
