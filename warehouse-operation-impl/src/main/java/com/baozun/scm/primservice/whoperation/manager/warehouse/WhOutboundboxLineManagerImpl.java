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

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxLineCommand;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxLineDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;

@Service("whOutboundboxLineManager")
public class WhOutboundboxLineManagerImpl extends BaseManagerImpl implements WhOutboundboxLineManager {
    
    public static final Logger log = LoggerFactory.getLogger(WhOutboundboxLineManagerImpl.class);
    
    @Autowired
    private WhOutboundboxLineDao whOutboundboxLineDao;

    @Override
    public void saveOrUpdate(WhOutboundboxLineCommand whOutboundboxLineCommand) {
        WhOutboundboxLine whOutboundboxLine = new WhOutboundboxLine();
        //复制数据        
        BeanUtils.copyProperties(whOutboundboxLineCommand, whOutboundboxLine);
        if(null != whOutboundboxLineCommand.getId() ){
            whOutboundboxLineDao.saveOrUpdate(whOutboundboxLine);
        }else{
            whOutboundboxLineDao.insert(whOutboundboxLine);
        }
    }

    @Override
    public void saveOrUpdate(WhOutboundboxLine whOutboundboxLine) {

        if(null != whOutboundboxLine.getId() ){
            whOutboundboxLineDao.saveOrUpdate(whOutboundboxLine);
        }else{
            whOutboundboxLineDao.insert(whOutboundboxLine);
        }
    }

}
