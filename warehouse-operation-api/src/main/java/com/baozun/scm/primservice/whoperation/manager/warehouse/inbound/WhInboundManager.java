package com.baozun.scm.primservice.whoperation.manager.warehouse.inbound;

import com.baozun.scm.primservice.whoperation.command.whinterface.inbound.WhInboundConfirmCommand;

public interface WhInboundManager {

	void insertWhInboundData(WhInboundConfirmCommand inboundConfirmCommand);

}
