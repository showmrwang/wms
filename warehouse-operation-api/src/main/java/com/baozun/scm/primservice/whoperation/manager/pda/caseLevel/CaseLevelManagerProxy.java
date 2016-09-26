package com.baozun.scm.primservice.whoperation.manager.pda.caseLevel;

import java.util.List;
import java.util.Map;

import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.Uom;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionRcvd;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;

public interface CaseLevelManagerProxy extends BaseManager {

    /**
     * 根据Id获取asn信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    public WhAsn getWhAsnById(Long asnId, Long ouId, String logId);

    /**
     * 根据容器ID查询容器信息
     *
     * @author mingwei.xie
     * @param containerId
     * @param ouId
     * @return
     */
    public Container getContainerById(Long containerId, Long ouId);

    /**
     * 根据容器号查询容器信息
     *
     * @author mingwei.xie
     * @param containerCode
     * @param ouId
     * @return
     */
    public ContainerCommand getContainerByCode(String containerCode, Long ouId);

    /**
     * 从缓存中获取caseLevel货箱操作人
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @return
     */
    public String getContainerOptUserFromCache(Long asnId, Long containerId);

    /**
     * 占用容器
     * 
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param userId
     * @param ouId
     * @param logId
     * @return
     */
    public void occupiedContainerByOptUser(Long asnId, Long containerId, Long userId, Long ouId, String logId);

    public void clearRcvdCacheForOccupiedContainer(Long asnId, Long containerId, Long userId, Long ouId, String logId);

    /**
     * 取消当前容器扫描，释放容器占用，删除所有相关缓存
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param userId
     * @param ouId
     * @param logId
     */
    public void cancelCurrentContainerRcvd(Long asnId, Long containerId, Long userId, Long ouId, String logId);

    /**
     * 获取caseLevel的装箱信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @return
     */
    public List<WhCartonCommand> getWhCartonListByContainer(Long asnId, Long containerId, Long ouId, String logId);

    /**
     * 获取caseLevel箱中指定商品的信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @return
     */
    public List<WhCartonCommand> getWhCartonListBySku(Long asnId, Long containerId, Long skuId, Long ouId, String logId);

    /**
     * 根据商品条码获取skuId及默认数量
     *
     * @author mingwei.xie
     * @param barCode
     * @param logId
     * @return
     */
    public Map<Long, Integer> getSkuByBarCode(String barCode, String logId);

    /**
     * 根据skuId获取商品信息
     *
     * @author mingwei.xie
     * @param skuId
     * @param ouId
     * @param logId
     * @return
     */
    public SkuRedisCommand getSkuMasterBySkuId(Long skuId, Long ouId, String logId);

    /**
     * 将功能信息作为get请求的参数传往下一个页面，防止功能在收货过程中被修改
     *
     * @author mingwei.xie
     * @param rcvdFun
     * @return
     */
    public String getFunctionInfoStr(WhFunctionRcvd rcvdFun);

    /**
     * 将商品装箱信息作为get请求的参数
     *
     * @author mingwei.xie
     * @param whCartonCommand
     * @return
     */
    public String getWhCartonInfoStr(WhCartonCommand whCartonCommand);

    public void saveScanRcvdSnDefectInfoCache(WhCartonCommand whCartonCommand, List<WhSkuInventorySn> whSkuInventorySnList, Long userId, Long ouId, String logId);

    public List<WhSkuInventorySn> getScanRcvdSnDefectInfoCache(WhCartonCommand whCartonCommand, Long userId, Long ouId, String logId);

    public List<String> getScanRcvdSnCache(WhCartonCommand whCartonCommand, Long userId, Long ouId, String logId);

    public void clearScanRcvdSnDefectInfoCache(WhCartonCommand whCartonCommand, Long userId, Long ouId, String logId);

    /**
     *
     *
     * @author mingwei.xie
     * @return
     */
    public String generateDefectWareBarcode();

    /**
     * 验证在货箱内是否存在指定SN号的sku
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param snCode
     * @param userId
     * @param ouId
     * @param logId
     * @return
     */
    public boolean isCaseLevelSnExist(Long asnId, Long containerId, Long skuId, String snCode, Long userId, Long ouId, String logId);

    /**
     * caseLevel收货中此商品是否已收入该SN
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param snCode
     * @param ouId
     * @param logId
     * @return
     */
    public boolean isRcvdSnCacheExist(Long asnId, Long containerId, Long skuId, String snCode, Long ouId, String logId);

    /**
     * 保存已收商品的缓存
     *
     * @author mingwei.xie
     * @param whCartonCommand
     * @param whFunctionRcvd
     * @param userId
     * @param ouId
     * @param logId
     */
    public void saveRcvdCartonCache(WhCartonCommand whCartonCommand, WhFunctionRcvd whFunctionRcvd, Long userId, Long ouId, String logId);

    /**
     * 根据sku获取已缓存的收货记录
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param logId
     * @return
     */
    public List<WhCartonCommand> getRcvdCartonBySkuFromCache(Long asnId, Long containerId, Long skuId, String logId);

    /**
     * 获取商品已收数量
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param userId
     * @param ouId
     * @param logId
     * @return
     */
    public Double getRcvdSkuQtyFromCache(Long asnId, Long containerId, Long skuId, Long userId, Long ouId, String logId);

    /**
     * 获取本次收货的商品数量
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param logId
     * @return
     */
    public Map<Long, Double> getCurrentRcvdSkuQtyMap(Long asnId, Long containerId, String logId);

    /**
     * 根据UUID更新收货数量
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param uuid
     * @param alterQty
     * @param userId
     * @param ouId
     * @param logId
     */
    public void updateRcvdCartonQtyByUUID(Long asnId, Long containerId, Long skuId, String uuid, Double alterQty, Long userId, Long ouId, String logId);

    /**
     * 重新收货，缓存当前收货数，清除缓存数据
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param logId
     */
    public void reRcvd(Long asnId, Long containerId, String logId);

    /**
     * 获取上次收货的商品数量
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param logId
     * @return
     */
    public Map<Long, Double> getLastRcvdSkuQty(Long asnId, Long containerId, String logId);

    /**
     * 获取指定sku的上次收货数
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param skuId
     * @param logId
     * @return
     */
    public Double getLastRcvdSkuQtyBySkuId(Long asnId, Long containerId, Long skuId, String logId);

    /**
     * 根据店铺ID获取店铺信息
     *
     * @author mingwei.xie
     * @param storeId
     * @param logId
     * @return
     */
    public Store getStoreById(Long storeId, String logId);

    /**
     * 获取系统参数
     *
     * @author mingwei.xie
     * @param groupValue
     * @param lifecycle
     * @return
     */
    public List<SysDictionary> getSysDictionaryByGroupValue(String groupValue, Integer lifecycle);

    /**
     * 根据残次类型查询仓库的残次原因
     *
     * @author mingwei.xie
     * @param typeId
     * @param ouId
     * @return
     */
    public List<WarehouseDefectReasonsCommand> getWarehouseDefectReasonsListByDefectTypeId(Long typeId, Long ouId);

    /**
     * 查询仓库配置的残次类型
     *
     * @author mingwei.xie
     * @param ouId
     * @param lifecycle
     * @return
     */
    public List<WarehouseDefectTypeCommand> getWarehouseDefectTypeByOuId(Long ouId, Integer lifecycle);

    /**
     * 查询店铺残次原因列表
     *
     * @param storeDefectTypeIdList
     * @return
     */
    public List<StoreDefectReasonsCommand> getStoreDefectReasonsByDefectTypeIds(List<Long> storeDefectTypeIdList);

    /**
     *
     * 根据店铺ID查询对应残次类型
     *
     * @return
     */
    public List<StoreDefectTypeCommand> getStoreDefectTypeListByStoreId(Long storeId);

    /**
     * 店铺的残次类型
     */
    public StoreDefectType getStoreDefectTypeById(Long id);

    /**
     * 店铺的残次原因
     */
    public StoreDefectReasons getStoreDefectReasonsById(Long id);

    /**
     * 仓库的残次类型
     */
    public WarehouseDefectType getWarehouseDefectTypeById(Long id, Long ouId);

    /**
     * 仓库的残次原因
     */
    public WarehouseDefectReasons getWarehouseDefectReasonsById(Long id, Long ouId);

    /**
     * 根据ID查询库存状态信息
     *
     * @author mingwei.xie
     * @param id
     * @return
     */
    public InventoryStatus getInventoryStatusById(Long id);

    public List<InventoryStatus> getAllInventoryStatus();

    /**
     * 根据uomCode查找数据。逻辑：uomCode具有唯一性
     *
     * @param uomCode
     * @return
     */
    public Uom getUomByCode(String uomCode, String groupCode);

    /**
     * caseLevel箱收货完成
     *
     * @author mingwei.xie
     * @param whFunctionRcvd
     * @param whCartonCommand
     * @param userId
     * @param ouId
     * @param logId
     */
    public void caseLevelReceivingCompleted(WhFunctionRcvd whFunctionRcvd, WhCartonCommand whCartonCommand, Long userId, Long ouId, String logId);

    /**
     * 判断ASN的caseLevel收货是否完成
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @return
     */
    public boolean isAsnCaseLevelNeedToRcvd(Long asnId, Long ouId);

    /**
     * caseLevel箱收货完成
     *
     * @author mingwei.xie
     * @param whFunctionRcvd
     * @param whCartonCommand
     * @param userId
     * @param ouId
     * @param logId
     */
    public void caseLevelReceivingCompleted(WhFunctionRcvd whFunctionRcvd, WhCartonCommand whCartonCommand, List<WhCartonCommand> rcvdCartonList, Long userId, Long ouId, String logId);

}
