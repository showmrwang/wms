package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;

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
    /** sn列表*/
    private List<WhSkuInventorySn> snList;
    /** 出库功能配置*/
    private WhFunctionOutBound whFunctonOutBound;
    /** 页面提示信息*/
    private String message;


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

    public WhFunctionOutBound getWhFunctonOutBound() {
        return whFunctonOutBound;
    }

    public void setWhFunctonOutBound(WhFunctionOutBound whFunctonOutBound) {
        this.whFunctonOutBound = whFunctonOutBound;
    }

    public List<WhSkuInventorySn> getSnList() {
        return snList;
    }

    public void setSnList(List<WhSkuInventorySn> snList) {
        this.snList = snList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
