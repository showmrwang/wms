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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy.CreatePoAsnManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy.EditPoAsnManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.warehouse.CustomerManager;


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


    @Test
    public void testExecute() {
        try {
            WhPoCommand po = new WhPoCommand();
            // po.setId(22L);
            po.setStoreId(22L);
            po.setPoCode("tes22t");
            po.setPoType(1);
            po.setStatus(11);
            po.setIsWms(true);
            po.setOuId(12L);
            // createsAsnManager.findWhAsnListByAsnCode("aa", null);
            // WhPoCommand po = new WhPoCommand();
            // createPoAsnManagerProxy.CreatePo(po);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
