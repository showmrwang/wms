package com.baozun.scm.primservice.whoperation.command.pda.collection;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class WorkCollectionCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -1578747439528886063L;

    /** ouId*/
    private Long ouId;
    /** 批次号*/
    private String batch;

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }



}
