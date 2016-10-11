package com.baozun.scm.primservice.whoperation.command.odo.wave;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class SoftAllocationResponseCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -5079222778481500744L;

    private boolean isSuccess;

    private List<Long> emptyQtyList;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<Long> getEmptyQtyList() {
        return emptyQtyList;
    }

    public void setEmptyQtyList(List<Long> emptyQtyList) {
        this.emptyQtyList = emptyQtyList;
    }


}
