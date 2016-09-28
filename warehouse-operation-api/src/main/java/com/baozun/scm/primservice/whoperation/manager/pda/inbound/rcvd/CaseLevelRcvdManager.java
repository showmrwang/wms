package com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.StoreDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhAsnRcvdLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectReasonsCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.conf.basis.WarehouseDefectTypeCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnSn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.Uom;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;

public interface CaseLevelRcvdManager extends BaseManager {

    public List<WarehouseDefectReasonsCommand> findWarehouseDefectReasonsListByDefectTypeId(Long typeId, Long ouId);

    /**
     * 通过OUID查询对应仓库可用残次类型
     *
     * @return
     */
    public List<WarehouseDefectTypeCommand> findWarehouseDefectTypeListByOuId(Long ouId, Integer lifecycle);

    /**
     * 查询店铺残次原因列表
     *
     * @param storeDefectTypeIdList
     * @return
     */
    public List<StoreDefectReasonsCommand> findStoreDefectReasonsListByDefectTypeIds(List<Long> storeDefectTypeIdList);

    /**
     *
     * 根据店铺ID查询对应残次类型
     *
     * @return
     */
    public List<StoreDefectTypeCommand> findStoreDefectTypeListByStoreId(Long storeId);

    /**
     * 店铺的残次类型
     */
    public StoreDefectType findStoreDefectTypeById(Long id);

    /**
     * 店铺的残次原因
     */
    public StoreDefectReasons findStoreDefectReasonsById(Long id);

    /**
     * 仓库的残次类型
     */
    public WarehouseDefectType findWarehouseDefectTypeById(Long id, Long ouId);

    /**
     * 仓库的残次原因
     */
    public WarehouseDefectReasons findWarehouseDefectReasonsById(Long id, Long ouId);

    /**
     * 根据ID查询库存状态信息
     *
     * @author mingwei.xie
     * @param id
     * @return
     */
    public InventoryStatus getInventoryStatusById(Long id);

    public List<InventoryStatus> findAllInventoryStatus();

    /**
     * 根据uomCode查找数据。逻辑：uomCode具有唯一性
     *
     * @param uomCode
     * @return
     */
    public Uom getUomByCode(String uomCode, String groupCode);

    /**
     * 查询asn对应的caseLevel的货箱信息
     *
     * @author mingwei.xie
     * @param asnId
     * @param containerId
     * @param ouId
     * @return
     */
    public List<WhCartonCommand> getCaseLevelWhCartonListByContainer(Long asnId, Long containerId, Long ouId);

    /**
     * 获取asnLine中指定商品的SN号
     *
     * @author mingwei.xie
     * @param asnLineId
     * @param ouId
     * @return
     */
    public List<WhAsnSn> findSkuSnByAsnLine(Long asnLineId, Long ouId);

    /**
     * 根据Id获取容器
     *
     * @param id
     * @return
     */
    public Container getContainerById(Long id, Long ouId);

    /**
     * 根据容器编码查找容器
     */
    public ContainerCommand getContainerByCode(String code, Long ouId);

    /**
     * updateByVersion
     *
     * @param container
     * @return
     */
    public int saveOrUpdateByVersion(Container container);

    /**
     * 根据Id获取容器
     *
     * @param id
     * @return
     */
    public Container2ndCategory getContainer2ndCategoryById(Long id, Long ouId);

    public Warehouse getWarehouseById(Long ouId);

    /**
     * 判断ASN的caseLevel收货是否完成
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @return
     */
    public boolean isAsnCaseLevelNeedToRcvd(Long asnId, Long ouId);

    public void caseLevelReceivingCompleted(List<WhCartonCommand> rcvdCartonList, List<WhSkuInventory> toSaveSkuInventoryList, List<WhSkuInventorySn> toSaveSkuInventorySnList, List<WhAsnRcvdLogCommand> toSaveWhAsnRcvdLogCommandList,
            List<WhAsnLine> toUpdateAsnLineList, WhAsn toUpdateWhAsn, List<WhPoLine> toUpdatePoLineList, List<WhPo> toUpdateWhPoList, Container toUpdateContainer, ContainerAssist toSaveContainerAssist, Boolean isTabbInvTotal, Long userId, Long ouId,
            String logId);

    /**
     * 校验ASN是否收货完成
     *
     * @author mingwei.xie
     * @param asnId
     * @param ouId
     * @param logId
     * @return
     */
    public Boolean checkIsAsnRcvdFinished(Long asnId, Long ouId, String logId);
}
