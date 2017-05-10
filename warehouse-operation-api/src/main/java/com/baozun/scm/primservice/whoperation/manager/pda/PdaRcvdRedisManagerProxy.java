package com.baozun.scm.primservice.whoperation.manager.pda;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;

public interface PdaRcvdRedisManagerProxy extends BaseManager {

    /** ASN缓存信息 */
    // ASNLine信息
    void cacheAsnLine(Long occupationId, WhAsnLine asnline);

    Set<String> findAllLineId(Long occupationId);

    WhAsnLine findLine(Long occupationId, String asnlineId);

    // asnLine 超收数量信息
    void cacheAsnLineOverchargeCount(Long occupationId, String asnlineId, int overchargeCount);

    Integer findLineOverchargeCount(Long occupationId, String asnlineId);

    // 明细商品数量
    void cacheLineSkuCount(Long occupationId, Long asnLineId, Long skuId, int count);

    String findLineSkuCount(Long occupationId, Long asnLineId, Long skuId);

    long decrLineSkuCount(Long occupationId, Long lineId, Long skuId, Integer divCount);

    long incrLineSkuCount(Long occupationId, Long lineId, Long skuId, Integer divCount);

    // 明细SN信息
    void cacheAsnLineSn(Long asnLineId, String[] snArray);

    long countLineSn(String lineId);

    boolean eistsLineSn(String lineId, String sn);

    // asn商品数量
    void cacheAsnSkuCount(Long id, Map<Long, Integer> skuMap);

    String findSkuCount(Long occupationId, Long skuId);

    long decrSkuCount(Long occupationId, Long skuId, Integer divCount);

    long incrSkuCount(Long occupationId, Long skuId, Integer divCount);

    // Asn信息
    void cacheAsn(WhAsn cacheAsn);

    void freeAsn(Long occupationId);

    WhAsn findAsn(Long asnId);
    // Po信息
    void cachePo(WhPo po);



    // 收货数据
    List<RcvdCacheCommand> findAllRcvd(Long userId);

    void rcvd(Long userId, RcvdCacheCommand rcvdCommand);

    void freeRcvd(Long userId);

    void cacheRcvd(Long userId, List<RcvdCacheCommand> list);

    // 容器
    void freeContainer(Long insideContainerId);

    void cacheContainer(Long insideContainerId, RcvdContainerCacheCommand cacheContainer);

    RcvdContainerCacheCommand findContainer(Long insideContainerId);

    // 容器-收货商品数据
    void freeContainerSku(Long insideContainerId);

    RcvdContainerCacheCommand findContainerSku(Long insideContainerId, Long skuId);

    void cacheContainerSku(Long insideContainerId, Long skuId, RcvdContainerCacheCommand rcvdContainerCacheCommand);

    // 用户收货的SN数据
    void freeRcvdSn(Long userId);

    List<RcvdSnCacheCommand> findRcvdSn(Long userId);

    void cacheRcvdSn(Long userId, List<RcvdSnCacheCommand> cacheSn);


    // 托盘-容器缓存
    void putPalletContainer(Long outerContainerId, Long insideContainerId);

    void popPalletContainer(Long outside);

    void freeAllPalletContainer(Long outerContainerId);

    List<String> findAllPalletContainer(Long outerContainerId);


    // 用户缓存
    boolean existsUser(String userId);

    void registerUser(String userId);










}
