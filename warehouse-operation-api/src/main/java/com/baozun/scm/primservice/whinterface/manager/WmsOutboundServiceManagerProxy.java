package com.baozun.scm.primservice.whinterface.manager;

import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundCancel;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundLocked;
import com.baozun.scm.primservice.whinterface.msg.WmsResponse;

/**
 * WMS出库业务接口
 * 
 * @author yimin.lu
 *
 */
public interface WmsOutboundServiceManagerProxy {

    /**
     * 出库单锁定
     * 
     * @param wmsOutBoundLocked
     * @return
     */
    WmsResponse wmsOutBoundLocked(WmsOutBoundLocked wmsOutBoundLocked);

    /**
     * 出库单整单/行 取消接口
     * 
     * @param wmsOutBoundCancel
     * @return
     */
    WmsResponse wmsOutBoundCancel(WmsOutBoundCancel wmsOutBoundCancel);
}
