package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;

public interface WhOutboundboxManager extends BaseManager {

    /**
     * 保存更新出库箱头信息
     * 
     * @param WhOutboundboxCommand
     */
    void saveOrUpdate(WhOutboundboxCommand whOutboundboxCommand);

    List<WhOutboundbox> getwhOutboundboxByCode(String outboundBoxCode);

    WhOutboundboxCommand getwhOutboundboxCommandByCode(String outboundBoxCode, Long ouId);


    public WhOutboundboxCommand findByOutboundBoxCode(String outboundBoxCode, Long ouId);

    /**
     * [通用方法] 通过主键服务生成出库箱编码
     * @return
     */
    public String generateCode();

}
