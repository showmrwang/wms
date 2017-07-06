/**
 * Copyright (c) 2017 Baozun All Rights Reserved.
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
package com.baozun.scm.primservice.whoperation.manager.pda.work;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutboundboxMove;
/**
 * pda ，出库箱的拆分和移动
 * @author zhaozili
 *
 */
public interface PdaOutBoundBoxMoveManager extends BaseManager{

    /**
     * 
     * @param containerCode
     * @param boxMoveFunc
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    public ScanResultCommand scanContainer(String containerCode, Integer movePattern, Long ouId,String logId,Long userId);
    
    /***
     * 扫描源容器货格
     * @param sourceContainerCode
     * @param sourceContainerID
     * @param containerLatticNo
     * @param movePattern
     * @param scanType
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    public ScanResultCommand scanContainerLatticCode(String sourceContainerCode, Long sourceContainerID ,Integer containerLatticNo,Integer movePattern,String scanType, Long ouId,String logId,Long userId);
    
    /**
     * 扫描目标容器
     * @param targetContainerCode
     * @param ouId
     * @param logId
     * @param userId
     * @param targetBoxCode
     * @return
     */
    public ScanResultCommand scanTargetContainer(String sourceContainerCode,Long sourceContainerId, Integer containerLatticNo,String targetContainerCode,String scanType, Long ouId,String logId,Long userId,Warehouse wareCommand,WhFunctionOutboundboxMove boxMoveFunc);
    
    /***
     * 扫描目标容器耗材编号
     * @param consumablesNo
     * @param targetContainerCode
     * @param ouId
     * @param logId
     * @param userId
     * @param containerCode
     * @return
     */
    public ScanResultCommand scanTargetConsumables(String sourceContainerCode,Long sourceContainerId, Integer containerLatticNo,String targetContainerCode,String consumablesNo, String scanType,Long ouId,String logId,Long userId,Warehouse wareCommand,WhFunctionOutboundboxMove boxMoveFunc);
    
    /**
     * 出库箱拆分:扫描sku商品
     * @param isScanSkuSn
     * @param insideContainerCode
     * @param skuCmd
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @param warehouse
     * @return
     */
      public ScanResultCommand boxMoveScanSku(String insideContainerCode,Long sourceContainerId, Integer containerLatticNo,String targetContainerCode,WhSkuCommand skuCmd, 
    		   Long funcId,Warehouse warehouse,WhFunctionOutboundboxMove boxMoveFunc, String scanType, Long ouId, Long userId, String logId);

      /***
       * 取消扫描源容器画面操作
       * @param containerCode
       * @param ouId
       * @param logId
       * @param userId
       * @return
       */
      public void cancelScanContainer(String containerCode,String scanType, Long ouId,String logId,Long userId);
      
      /***
       * 取消扫描货格画面操作
       * @param containerCode
       * @param ouId
       * @param logId
       * @param userId
       * @return
       */
      public void cancelScanContainerLattic(String sourceContainerCode,Integer containerLatticNo, String scanType, Long ouId,String logId,Long userId);
      
      /***
       * 取消扫描目标容器画面操作
       * @param containerCode
       * @param ouId
       * @param logId
       * @param userId
       * @return
       */
      public void cancelScanTargetContainer(String sourceContainerCode,Integer containerLatticNo, String scanType, Long ouId,String logId,Long userId);
      
      /***
       * 取消扫描目标容器耗材画面操作
       * @param sourceContainerCode
       * @param containerLatticNo
       * @param scanType
       * @param targetContainerCode
       * @param consumablesCode
       * @param ouId
       * @param logId
       * @param userId
       * @return
       */
      public void cancelScanConsumables(String sourceContainerCode,Integer containerLatticNo, String scanType,String targetContainerCode, String consumablesCode, Long ouId,String logId,Long userId);
      
      /***
       * 取消扫描源商品画面操作
       * @param containerCode
       * @param ouId
       * @param logId
       * @param userId
       * @return
       */
      public void cancelScanSku(String sourceContainerCode,Integer containerLatticNo, String scanType,Integer movePattern, Long ouId,String logId,Long userId);
      
      /***
       * 取消出库箱移库操作的素有缓存
       * @param containerCode
       * @param targetContainerCode
       * @param boxMove
       * @param ouId
       * @param logId
       * @param userId
       * @return
       */
      public void moveFinishScanContainer(String sourceContainerCode,String targetContainerCode,WhFunctionOutboundboxMove boxMove, Long ouId,String logId,Long userId);
}
