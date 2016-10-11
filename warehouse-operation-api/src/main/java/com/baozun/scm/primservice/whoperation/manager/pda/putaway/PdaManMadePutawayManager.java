package com.baozun.scm.primservice.whoperation.manager.pda.putaway;

import com.baozun.scm.primservice.whoperation.command.pda.putaway.PdaManMadePutawayCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

public interface PdaManMadePutawayManager extends BaseManager{

   /**
    * 验证容器号
    * 
    * @param pdaManMadePutawayCommand
    * @return
    */
   public PdaManMadePutawayCommand pdaScanContainer(PdaManMadePutawayCommand pdaManMadePutawayCommand);


    /**
     * 验证库位号
     * 
     * @param pdaManMadePutawayCommand
     * @return
     */
   public PdaManMadePutawayCommand pdaScanLocation(PdaManMadePutawayCommand pdaManMadePutawayCommand,String invAttrMgmtHouse,Warehouse warehouse);
   
   
   /***
    * 扫描内部容器
    * @param pdaManMadePutawayCommand
    * @return
    */
   public PdaManMadePutawayCommand manScanInsideContainer(PdaManMadePutawayCommand pdaManMadePutawayCommand,Long ouId,Warehouse warehouse);
   
   
   /***
    * 整托上架:扫描sku
    * @param pdaManMadePutawayCommand
    * @param ouId
    * @return
    */
   public PdaManMadePutawayCommand manMadeScanSku(PdaManMadePutawayCommand pdaManMadePutawayCommand,Long ouId,WhSkuCommand skuCmd,Warehouse wareHouse);
   
   
   /***
    * 整箱上架:扫描sku
    * @param pdaManMadePutawayCommand
    * @param ouId
    * @param skuCmd
    * @param wareHouse
    * @return
    */
   public PdaManMadePutawayCommand containerPutwayScanSku(PdaManMadePutawayCommand pdaManMadePutawayCommand,Long ouId,WhSkuCommand skuCmd,Warehouse wareHouse);


}
