package com.baozun.scm.primservice.whoperation.manager.pda.sortation;

import com.baozun.scm.primservice.whoperation.command.pda.sortation.PdaInboundSortationCommand;



public interface PdaInboundSortationManager {

    PdaInboundSortationCommand pdaScanContainer(PdaInboundSortationCommand pdaInboundSortationCommand);

    PdaInboundSortationCommand pdaScanSku(PdaInboundSortationCommand pdaInboundSortationCommand);

    PdaInboundSortationCommand pdaScanSkuQty(PdaInboundSortationCommand pdaInboundSortationCommand);

    PdaInboundSortationCommand pdaScanNewContainer(PdaInboundSortationCommand pdaInboundSortationCommand) throws Exception;

    PdaInboundSortationCommand pdaScanSkuInvAttr(PdaInboundSortationCommand pdaInboundSortationCommand);

    void pdaScanSn(PdaInboundSortationCommand pdaInboundSortationCommand);

    void pdaScanSnDone(PdaInboundSortationCommand pdaInboundSortationCommand);

    PdaInboundSortationCommand pdaScanSkuAttr(PdaInboundSortationCommand pdaInboundSortationCommand) throws Exception;
}
