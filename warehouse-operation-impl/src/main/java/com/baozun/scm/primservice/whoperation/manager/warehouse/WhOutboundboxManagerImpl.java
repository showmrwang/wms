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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;

@Service("whOutboundboxManager")
public class WhOutboundboxManagerImpl extends BaseManagerImpl implements WhOutboundboxManager {

    public static final Logger log = LoggerFactory.getLogger(WhOutboundboxManagerImpl.class);

    @Autowired
    private WhOutboundboxDao whOutboundboxDao;

    @Autowired
    private CodeManager codeManager;

    @Override
    public void saveOrUpdate(WhOutboundboxCommand whOutboundboxCommand) {
        WhOutboundbox whOutboundbox = new WhOutboundbox();
        // 复制数据
        BeanUtils.copyProperties(whOutboundboxCommand, whOutboundbox);
        if (null != whOutboundboxCommand.getId()) {
            whOutboundboxDao.saveOrUpdate(whOutboundbox);
        } else {
            whOutboundboxDao.insert(whOutboundbox);
        }
    }

    @Override
    public List<WhOutboundbox> getwhOutboundboxByCode(String outboundBoxCode) {
        WhOutboundbox whOutboundbox = new WhOutboundbox();
        whOutboundbox.setOutboundboxCode(outboundBoxCode);
        return whOutboundboxDao.findListByParam(whOutboundbox);
    }

    @Override
    public WhOutboundboxCommand getwhOutboundboxCommandByCode(String outboundBoxCode, Long ouId) {
        return whOutboundboxDao.getwhOutboundboxCommandByCode(outboundBoxCode, ouId);
    }


    @Override
    public WhOutboundboxCommand findByOutboundBoxCode(String outboundBoxCode, Long ouId) {
        return whOutboundboxDao.findByOutboundBoxCode(outboundBoxCode, ouId);
    }

    @Override
    public String generateCode() {
        String outboundBoxCode = this.codeManager.generateCode(Constants.WMS, Constants.OUTBOUNDBOX_CODE, null, null, null);
        return outboundBoxCode;
    }
}
