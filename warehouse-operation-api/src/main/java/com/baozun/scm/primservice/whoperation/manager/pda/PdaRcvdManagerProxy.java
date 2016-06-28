package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PdaRcvdManagerProxy extends BaseManager {

    /**
     * 通用收货，扫描ASN，初始化缓存
     */
    void initAsnCacheForGeneralReceiving(Long occupationId, Long ouId);

    /**
     * 通用收货：将扫描的商品数据从缓存中推送到数据库中
     * 
     * @param commmand
     */
    void saveScanedSkuWhenGeneralRcvdForPda(List<RcvdCacheCommand> commmandList);

    /**
     * 将扫描的临时数据推送到缓存中
     * 
     * @param rcvdCacheCommand
     */
    void cacheScanedSkuWhenGeneralRcvd(WhSkuInventoryCommand command);

    /**
     * 刷新ASN缓存操作
     * 
     * @param id
     */
    void freshAsnCacheForGeneralReceiving(Long occupationId, Long ouId);

    /**
     * 获取匹配的明细行
     * 
     * @param command
     * @return
     */
    String getMatchLineListStr(WhSkuInventoryCommand command);

    /**
     * 缓存SN号
     * 
     * @param command
     * @param snCount
     */
    void cacheScanedSkuSnWhenGeneralRcvd(WhSkuInventoryCommand command, Integer snCount);

    /**
     * 从库存中初始化缓存；校验容器的可用性
     * 
     * @param command
     */
    void checkContainer(WhSkuInventoryCommand command, Long ouId);

    /**
     * 校验托盘的可用性
     * 
     * @param command
     * @param ouId
     */
    void checkPallet(WhSkuInventoryCommand command, Long ouId);

    /**
     * 初始化扫描的SKU的属性
     * 
     * @param isInvattrAsnPointoutUser
     * @param nextOpt
     * @param command
     */
    WhSkuInventoryCommand initAttrWhenScanningSku(Boolean isInvattrAsnPointoutUser, Integer nextOpt, WhSkuInventoryCommand command);

    /**
     * 获取下一个扫描的ASN属性
     * 
     * @param command
     * @return
     */
    Integer getNextSkuAttrOperatorForScanning(WhSkuInventoryCommand command);

    /**
     * 初始化ASN缓存【业务方法】
     * 
     * @param command
     */
    void initOrFreshCacheForScanningAsn(WhSkuInventoryCommand command);

}
