/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxLineSnCommand;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxLineSnDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLineSn;

@Service("whOutboundboxLineSnManager")
public class WhOutboundboxLineSnManagerImpl extends BaseManagerImpl implements WhOutboundboxLineSnManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhOutboundboxLineSnManagerImpl.class);
    
    @Autowired
    private WhOutboundboxLineSnDao whOutboundboxLineSnDao;

    @Override
    public void saveOrUpdate(WhOutboundboxLineSnCommand whOutboundboxLineSnCommand) {
        WhOutboundboxLineSn whOutboundboxLineSn = new WhOutboundboxLineSn();
        //复制数据        
        BeanUtils.copyProperties(whOutboundboxLineSnCommand, whOutboundboxLineSn);
        if(null != whOutboundboxLineSnCommand.getId() ){
            whOutboundboxLineSnDao.saveOrUpdate(whOutboundboxLineSn);
        }else{
            whOutboundboxLineSnDao.insert(whOutboundboxLineSn);
        }
    }

}