package com.baozun.scm.primservice.whinterface.service;

import com.baozun.scm.primservice.whinterface.model.WmsInBound;
import com.baozun.scm.primservice.whinterface.msg.WmsResponse;

/**
 * [对接上位系统]Wms服务接口
 * 
 * @version 2017年2月20日
 */
public interface WmsService {
	
	/**
	 * 上位系统通知宝尊WMS4.0系统入库单据信息
	 * @return
	 */
	WmsResponse wmsInBound(WmsInBound inBound);
	
}
