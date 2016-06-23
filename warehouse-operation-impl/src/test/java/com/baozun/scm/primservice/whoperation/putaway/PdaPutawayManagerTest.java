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

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
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
    
    @Autowired
    private CacheManager cacheManagr;
    
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

       //com.baozun.scm.primservice.whinfo.model.warehouse.WhSkuLocation
       
       Long id = pkManager.generatePk(Constants.WMS, "com.baozun.scm.primservice.whinfo.model.warehouse.WhSkuLocation");
       System.out.println(id);
    }
    
    @Test
    public void cacheTest(){
        WhSkuInventoryCommand inv = new WhSkuInventoryCommand();
        inv.setId(100L);
        inv.setOccupationCode("asn01");
        WhSkuInventoryCommand invs = cacheManagr.getMapObject(CacheConstants.INVENTORY, inv.getOccupationCode());
        if(null == invs){
            System.out.println("null");
            cacheManagr.setMapObject(CacheConstants.INVENTORY, inv.getOccupationCode(), inv, 30*24*60*60);
        }else{
            System.out.println("inventory has cached！");
        }
        
        invs = cacheManagr.getMapObject(CacheConstants.INVENTORY, "asn01");
        if(null != invs)
        System.out.println(invs.getOccupationCode());
        cacheManagr.removeMapValue(CacheConstants.INVENTORY, "asn01");
        
        if(null == cacheManagr.getMapValue(CacheConstants.INVENTORY, "asn01")){
            System.out.println("inventory is remove!");
        }
        
        long i = cacheManagr.incr(CacheConstants.INVENTORY + "01");
        System.out.println(i);
        long j = cacheManagr.incrBy(CacheConstants.INVENTORY + "01", 5);
        System.out.println(j);
        long k = cacheManagr.decrBy(CacheConstants.INVENTORY + "01", 5);
        System.out.println(k);
        long l = cacheManagr.remove(CacheConstants.INVENTORY + "01");
        System.out.println(l);
        Object o = cacheManagr.getObject(CacheConstants.INVENTORY + "01");
        if(null == o){
            System.out.println("key remove success！");
        }
        
        
        try {
            cacheManagr.setMapObject(CacheConstants.INVENTORY, inv.getOccupationCode(), inv, 30*24*60*60);
            if(null != cacheManagr.getMapObject(CacheConstants.INVENTORY, "asn01")){
            inv.setId(200L);
            cacheManagr.setMapObject(CacheConstants.INVENTORY, inv.getOccupationCode(), inv, 30*24*60*60);
            }
            throw new RuntimeException("r e");
        } catch (Exception e) {
            WhSkuInventoryCommand eInv = cacheManagr.getMapObject(CacheConstants.INVENTORY, "asn01");
            System.out.println(eInv.getId());
            if(null != eInv){
                inv.setId(200L);
                cacheManagr.setMapObject(CacheConstants.INVENTORY, inv.getOccupationCode(), inv, 30*24*60*60);
            }
            cacheManagr.removeMapValue(CacheConstants.INVENTORY, "asn01");
            
            if(null == cacheManagr.getMapValue(CacheConstants.INVENTORY, "asn01")){
                System.out.println("inventory is remove!");
            }
            throw e;
        }
        
    }
    
    public static void main(String[] args) {
        SimpleStandardCubeCalculator calc = new SimpleStandardCubeCalculator(1.0, 0.8, 0.6, "km", 0.8);
        calc.setCoordinate(SimpleStandardCubeCalculator.COORDS_Z);
        calc.initStuffCube(1.0, 0.48, 0.8, "km");
        System.out.println(calc.calculateAvailable());
    }
}
