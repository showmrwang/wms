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
package com.baozun.scm.primservice.whoperation.putaway;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleStandardCubeCalculator;

/**
 * @author lichuan
 *
 */
@ContextConfiguration(locations = {"classpath*:mybatis-config.xml", "classpath*:spring.xml"})
@ActiveProfiles("dev")
public class PdaPutawayManagerTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private PkManager pkManager;
    
    @Test
    public void getPK(){
       System.out.println("start");
       //com.baozun.scm.primservice.whinfo.model.warehouse.Container
       //14100031 14100032
       
       //com.baozun.scm.primservice.whoperation.model.poasn.WhAsn
       //16100018
       //com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine
       //13100030
       ////com.baozun.scm.primservice.whinfo.model.warehouse.inventory.WhSkuInventory

       
       
       Long id = pkManager.generatePk(Constants.WMS, "com.baozun.scm.primservice.whinfo.model.warehouse.inventory.WhSkuInventory");
       System.out.println(id);
    }
    
    public static void main(String[] args) {
        SimpleStandardCubeCalculator calc = new SimpleStandardCubeCalculator(1.0, 0.8, 0.6, "km", 0.8);
        calc.setCoordinate(SimpleStandardCubeCalculator.COORDS_Z);
        calc.initStuffCube(1.0, 0.48, 0.8, "km");
        System.out.println(calc.calculateAvailable());
    }
}
