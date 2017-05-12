package com.baozun.scm.primservice.whoperation;

/**
 * Copyright (c) 2010 Jumbomart All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Jumbomart. You shall not
 * disclose such Confidential Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Jumbo.
 * 
 * JUMBOMART MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. JUMBOMART SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 * 
 */
/**
 * Copyright (c) 2010 Jumbomart All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Jumbomart. You shall not
 * disclose such Confidential Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Jumbo.
 * 
 * JUMBOMART MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. JUMBOMART SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 * 
 */



import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import sun.net.www.content.audio.wav;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.manager.confirm.outbound.WhOutboundConfirmManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy.CreatePoAsnManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy.EditPoAsnManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.warehouse.CustomerManager;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundLineConfirm;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;


@ContextConfiguration(locations = {"classpath*:spring.xml"})
public class TaskTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private CreatePoAsnManagerProxy createPoAsnManagerProxy;

    @Autowired
    private AsnManager createsAsnManager;

    @Autowired
    private PoManager createsPoManager;

    @Autowired
    private EditPoAsnManagerProxy editPoAsnManagerProxy;
    @Autowired
    private CustomerManager customerManager;
    
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private WhOutboundConfirmManager whOutboundConfirmManager;


    @Test
    public void testExecute() {
        try {
            WhOdo whOdo = whOdoDao.findByIdOuId(12100120L, 119L);
            whOutboundConfirmManager.saveWhOutboundConfirm(whOdo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
