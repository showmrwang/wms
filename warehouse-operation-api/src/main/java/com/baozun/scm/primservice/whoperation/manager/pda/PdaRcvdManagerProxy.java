package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;

public interface PdaRcvdManagerProxy extends BaseManager {

    /**
     * 通用收货，扫描ASN，初始化缓存
     */
    void initAsnCacheForGeneralReceiving(Long occupationId, Long ouId);

    /**
     * 通用收货：将扫描的商品数据从缓存中推送到数据库中
     * 
     * @param ouId
     * @param logId
     * 
     * @param commmand
     */
    ResponseMsg saveScanedSkuWhenGeneralRcvdForPda(Long userId, Long ouId, String logId);

    /**
     * 将扫描的临时数据推送到缓存中
     * 
     * @param rcvdCacheCommand
     */
    void cacheScanedSkuWhenGeneralRcvd(WhSkuInventoryCommand command);

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
     * @param isCacheSkuSn
     */
    void cacheScanedSkuSnWhenGeneralRcvd(WhSkuInventoryCommand command, Integer snCount, boolean isCacheSkuSn);

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
     * 初始化ASN缓存【业务方法】
     * 
     * @param command
     */
    void initOrFreshCacheForScanningAsn(WhSkuInventoryCommand command);

    /**
     * 将收货的托盘或者货箱释放
     * 
     * @param insideContainerId
     * @param ouId
     * @param id
     */
    void revokeContainer(Long insideContainerId, Long ouId, Long userId);

    /**
     * 托盘收货完成逻辑
     */
    void rcvdPallet(Long outerContainerId, Long insideContainerId, Long ouId, Long userId);

    /**
     * ASN收货完成逻辑
     * 
     * @param occupationId
     * @param ouId
     * @param id
     */
    void rcvdAsn(Long occupationId, Long ouId, Long id);

    /**
     * 查询功能菜单
     * 
     * @param id
     * @param ouid
     * @return
     */
    WhFunctionRcvd findwFunctionRcvdByFunctionId(Long id, Long ouid);

    /**
     * 扫描SKU时初始化
     * 
     * @param command
     * @return
     */
    WhSkuInventoryCommand initSkuWhenScanning(WhSkuInventoryCommand command);

    /**
     * 查看装箱信息表
     * 
     * @param cartonCommand
     * @return
     */
    List<WhCartonCommand> findWhCartonByParamExt(WhCartonCommand cartonCommand);

    String initMatchedLineIdStr(WhSkuInventoryCommand command);

    /**
     * 获取缓存Asn对象
     */
    WhAsn getCacheAsnByOccupationId(String occupationId);

    /**
     * 缓存收货操作用户
     */
    void cacheOperUserWhenRcvd(String userId);

    /**
     * 当没有扫描商品的时候，取消货箱缓存
     * 
     * @param inside
     * @param skuId
     * @param ouId
     * @param userId
     * @param logId
     */
    void removeInsideContainerCacheWhenScanSkuNoRcvd(Long inside, Long skuId, Long ouId, Long userId, String logId);

    /**
     * 已扫描商品的时候，取消货箱缓存
     * 
     * @param inside
     * @param outside
     * @param ouId
     * @param userId
     * @param logId
     */
    void removeInsideContainerCacheWhenScanSkuHasRcvd(Long inside, Long outside, Long ouId, Long userId, String logId);

    /**
     * 测试专用
     * 
     * @param cacheKey
     * @return
     */
    String getCacheKeyPrefixWhenRcvd(String cacheKey);

    /**
     * 非SN的残次品
     * 
     * @param command
     */
    void cacheScanedDefeatSkuNoSnWhenGeneralRcvd(WhSkuInventoryCommand command);


}
