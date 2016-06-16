/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.dao.warehouse;

import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuWhmgmtCommand;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuWhmgmt;

import lark.orm.dao.supports.BaseDao;

public interface WhSkuWhmgmtDao extends BaseDao<WhSkuWhmgmt, Long> {

    /**
     * 查询商品扩展信息
     * 
     * @author shenlijun
     * @param params
     * @return
     */
    WhSkuWhmgmtCommand findWhmgmtByParams(Map<String, Object> params);
    
    /**
     * 更新商品信息
     * 
     * @author shenlijun
     * @param skuWhmgmt
     * @return
     */
    int updateByIdAndOuId(WhSkuWhmgmt skuWhmgmt);

    
    /**
     * 根据skuId和ouId查询是否存在此条信息
     * 
     * @author shenlijun
     * @param
     * @return
     */
    Long queryCountBySkuIdAndOuId(Map<String, Object> params);

}
