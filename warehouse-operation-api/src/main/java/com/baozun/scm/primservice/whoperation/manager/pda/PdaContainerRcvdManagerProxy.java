package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.List;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerAttrCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuCommand;
import com.baozun.scm.primservice.whoperation.command.sku.skucommand.SkuStandardPackingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;

public interface PdaContainerRcvdManagerProxy extends BaseManager {



    void initAsnForContainerReceiving(WhSkuInventoryCommand command, Long ouId);

    List<SkuStandardPackingCommand> getContainerNumber(String skuBarCode, Double qty, Long ouId);

    SkuCommand checkAsnSku(Long asnId, String occupationCode, String skuCode, Long ouId);

    ContainerCommand checkContainer(WhSkuInventoryCommand command);

    boolean checkFunc(Long funcId, Long ouId);

    void doReceive(WhSkuInventoryCommand command, Long number);

    /**
     * 处理商品属性跳转url
     * @param index 当前索引
     * @return
     */
    String handleUrl(int index);

    WhSkuInventoryCommand returnCommand(WhSkuInventoryCommand command);

    WhSkuInventoryCommand dispatchAttrCheck(RcvdContainerAttrCommand command);

    WhSkuInventoryCommand checkValidDate(WhSkuInventoryCommand command);

    WhSkuInventoryCommand checkBatchNo(WhSkuInventoryCommand command);

    WhSkuInventoryCommand checkCountry(WhSkuInventoryCommand command);

    WhSkuInventoryCommand checkInvType(WhSkuInventoryCommand command);

    WhSkuInventoryCommand checkInvAttr(WhSkuInventoryCommand command);

    WhSkuInventoryCommand checkInvStatus(WhSkuInventoryCommand command);

    WhSkuInventoryCommand checkDefective(WhSkuInventoryCommand command);

    WhSkuInventoryCommand checkSn(WhSkuInventoryCommand command, String sn);

    /**
     * 不允许差异收货
     * @param command
     * @return
     */
    boolean discrepancyNoAllowrcvd(WhSkuInventoryCommand command);

    /**
     * 将当前扫描的属性存入缓存中
     * @param command
     * @return
     */
    boolean cacheRcvd(RcvdContainerAttrCommand command);


    List<SkuStandardPackingCommand> getContainerType(Long skuId, Long ouId);

    SkuStandardPackingCommand getContainerQty(Long skuId, Long ouId, Long containerType);

    /**
     * 点击完成后,将此次的扫描数据写入缓存
     * @param command
     * @return
     */
    void completeScanning(WhSkuInventoryCommand command);

    String getAsnSkuCount(Long occupationId, Long skuId);

    Set<String> getLineSet(Long occupationId);

    WhAsnLine getAsnLine(Long occupationId, String lineId);

    List<RcvdSnCacheCommand> getCacheSn(String userId);

    List<RcvdCacheCommand> getRcvdCacheCommandList(String userId);

    void cancelOperation(WhSkuInventoryCommand command, List<RcvdCacheCommand> list, String userId, Long ouId);

    void setCacheSn(String userId, List<RcvdSnCacheCommand> cacheSn);

    void setCache(String userId, List<RcvdCacheCommand> list);

}
