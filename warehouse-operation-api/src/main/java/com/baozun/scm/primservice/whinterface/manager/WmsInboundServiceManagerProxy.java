package com.baozun.scm.primservice.whinterface.manager;

import com.baozun.scm.primservice.whinterface.model.inbound.WmsInBoundCancel;
import com.baozun.scm.primservice.whinterface.msg.WmsResponse;

/**
 * WMS入库业务接口
 * 
 * @author Administrator
 *
 */
public interface WmsInboundServiceManagerProxy {
    WmsResponse wmsInBoundCancel(WmsInBoundCancel wmsInBoundCancel);
}
