package com.baozun.scm.primservice.whoperation.manager.pda.sortation;

import com.baozun.scm.primservice.whoperation.command.pda.sortation.PdaInboundSortationCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;



public interface PdaInboundSortationManager extends BaseManager {

    PdaInboundSortationCommand pdaScanContainer(PdaInboundSortationCommand pdaInboundSortationCommand);

    PdaInboundSortationCommand pdaScanSku(PdaInboundSortationCommand pdaInboundSortationCommand);

    PdaInboundSortationCommand pdaScanSkuQty(PdaInboundSortationCommand pdaInboundSortationCommand);

    PdaInboundSortationCommand pdaScanNewContainer(PdaInboundSortationCommand pdaInboundSortationCommand) throws Exception;

    PdaInboundSortationCommand pdaScanSkuInvAttr(PdaInboundSortationCommand pdaInboundSortationCommand);

    void pdaScanSn(PdaInboundSortationCommand pdaInboundSortationCommand);

    void pdaScanSnDone(PdaInboundSortationCommand pdaInboundSortationCommand);

    void pdaContainerFull(PdaInboundSortationCommand pdaInboundSortationCommand);

    PdaInboundSortationCommand pdaScanSkuAttr(PdaInboundSortationCommand pdaInboundSortationCommand) throws Exception;

    PdaInboundSortationCommand scanNewContainerView(PdaInboundSortationCommand pdaInboundSortationCommand) throws Exception;
}
