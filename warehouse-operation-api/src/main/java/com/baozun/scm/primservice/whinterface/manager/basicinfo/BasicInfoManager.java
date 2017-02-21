package com.baozun.scm.primservice.whinterface.manager.basicinfo;

import java.util.Map;

import com.baozun.scm.primservice.whinterface.model.basicinfo.WmsCustomer;
import com.baozun.scm.primservice.whinterface.model.basicinfo.WmsStore;
import com.baozun.scm.primservice.whinterface.msg.WmsResponse;

public interface BasicInfoManager {
    /*
     * 根据code查询店铺
     */
    Map<WmsResponse, WmsCustomer> wmsCustomer(String code);


    /*
     * 根据code查询客户
     */
    Map<WmsResponse, WmsStore> wmsStore(String code);

}
