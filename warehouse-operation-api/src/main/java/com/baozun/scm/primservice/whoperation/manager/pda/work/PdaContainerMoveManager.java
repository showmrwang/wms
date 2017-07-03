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

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionContainerMove;

/**
 * pda ，周转箱的拆分和移动
 * @author feng.hu
 *
 */
public interface PdaContainerMoveManager extends BaseManager {
    /**
     * 
     * @param containerCode
     * @param containerId
     * @param boxMoveFunc
     * @param scanType
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    public ScanResultCommand scanContainer(String containerCode, Long containerId, Integer movePattern,String scanType, Long ouId,String logId,Long userId);
    
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
     * @param scanType
     * @param ouId
     * @param logId
     * @param userId
     * @param targetBoxCode
     * @return
     */
    public ScanResultCommand scanTargetContainer(String sourceContainerCode,Long sourceContainerId,Integer containerLatticeNo,String targetContainerCode,Long targetContainerId,String scanType,Long ouId,String logId,Long userId,String targetBoxCode, Integer containerStatus, Integer movePattern,Boolean isPrintCartonLabel, Warehouse warehouse);
    
    /**
     * 周转箱拆分:扫描sku商品
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
      public ScanResultCommand containerMoveScanSku(String insideContainerCode,Long sourceContainerId, Integer containerLatticNo,String targetContainerCode,Long targetContainerId,WhSkuCommand skuCmd, 
               Long funcId,Warehouse warehouse,WhFunctionContainerMove containerMoveFunc, String scanType, Long ouId, Long userId, String logId);

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
       * 取消扫描源商品画面操作
       * @param containerCode
       * @param ouId
       * @param logId
       * @param userId
       * @return
       */
      public void cancelScanSku(String sourceContainerCode,Integer containerLatticNo, String scanType, Long ouId,String logId,Long userId);
      
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
      public void moveFinishScanContainer(String sourceContainerCode,String targetContainerCode,WhFunctionContainerMove containerMove,String scanType, Long ouId,String logId,Long userId);
      
}
