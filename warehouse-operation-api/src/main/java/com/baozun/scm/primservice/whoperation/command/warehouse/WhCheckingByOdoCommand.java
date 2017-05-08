package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

public class WhCheckingByOdoCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 4535516885290827781L;

    /** 复合头信息*/
    private WhCheckingCommand checkingCommand;
    /** 复核明细信息*/
    private List<WhCheckingLineCommand> checkingLineCommandList;
    /** 物理仓配置*/
    private Warehouse warehouse;
    /** 复核页面数据显示*/
    private CheckingDisplayCommand checkingDisplayCommand;


    public WhCheckingCommand getCheckingCommand() {
        return checkingCommand;
    }

    public void setCheckingCommand(WhCheckingCommand checkingCommand) {
        this.checkingCommand = checkingCommand;
    }

    public List<WhCheckingLineCommand> getCheckingLineCommandList() {
        return checkingLineCommandList;
    }

    public void setCheckingLineCommandList(List<WhCheckingLineCommand> checkingLineCommandList) {
        this.checkingLineCommandList = checkingLineCommandList;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public CheckingDisplayCommand getCheckingDisplayCommand() {
        return checkingDisplayCommand;
    }

    public void setCheckingDisplayCommand(CheckingDisplayCommand checkingDisplayCommand) {
        this.checkingDisplayCommand = checkingDisplayCommand;
    }

}
