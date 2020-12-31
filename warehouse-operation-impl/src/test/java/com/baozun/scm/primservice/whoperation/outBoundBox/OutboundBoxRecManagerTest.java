/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.outBoundBox;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoOutBoundBoxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoOutBoundBoxMapper;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OutboundBoxRecManagerProxy;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleStandardCubeCalculator;

/**
 * @author lichuan
 *
 */
@ContextConfiguration(locations = {"classpath*:mybatis-config.xml", "classpath*:spring.xml"})
@ActiveProfiles("dev")
public class OutboundBoxRecManagerTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private OutboundBoxRecManagerProxy outBoundBoxRecManagerProxy;
    
    @Autowired
    private OdoOutBoundBoxMapper odoOutBoundBoxMapper;
    
    @Autowired
    private OdoLineManager odoLineManager;
    
    @Autowired
    private WhOdoOutBoundBoxDao whOdoOutBoundBoxDao;
    
    @Autowired
    private ContainerDao containerDao;
    
    @Test
    public void getPK(){
       System.out.println("start");
       List<Long> l=new ArrayList<Long>();
       l.add(28153088L);
       List<OdoLineCommand> list=odoLineManager.findOdoLineByOdoId(l, 146L);
       for(OdoLineCommand o:list){
           System.out.println(o.getId());
       }
       System.out.println("end");
    }
}
