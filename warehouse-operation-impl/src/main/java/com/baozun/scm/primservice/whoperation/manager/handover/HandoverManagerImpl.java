package com.baozun.scm.primservice.whoperation.manager.handover;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhHandoverStationCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.HandoverCollectionStatus;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.constant.OdoLineStatus;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.OutboundboxStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.handover.HandoverCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.handover.HandoverDao;
import com.baozun.scm.primservice.whoperation.dao.handover.HandoverLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOdoPackageInfoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.confirm.outbound.WhOutboundConfirmManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhSkuManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.handover.Handover;
import com.baozun.scm.primservice.whoperation.model.handover.HandoverCollection;
import com.baozun.scm.primservice.whoperation.model.handover.HandoverLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOdoPackageInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSku;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

@Service("handoverManager")
@Transactional
public class HandoverManagerImpl extends BaseManagerImpl implements HandoverManager {
    public static final Logger log = LoggerFactory.getLogger(HandoverManagerImpl.class);

    @Autowired
    private HandoverCollectionDao handoverCollectionDao;
    @Autowired
    private HandoverDao handoverDao;
    @Autowired
    private HandoverLineDao handoverLineDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhOutboundboxDao whOutboundboxDao;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private WhOdoPackageInfoDao whOdoPackageInfoDao;
    @Autowired
    private WhOutboundConfirmManager whOutboundConfirmManager;
    @Autowired
    private UomDao uomDao;
    @Autowired
    private WhSkuManager whSkuManager;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void insertHandoverCollection(WhOutboundbox whOutboundbox, WhHandoverStationCommand HandoverStation) {
        // 更改出库箱状态为待交接

        // 插入交接集货表
        HandoverCollection handoverCollection = new HandoverCollection();
        Handover handover = new Handover();
        handover.setHandoverStationId(HandoverStation.getId());
        List<Handover> handoverList = handoverDao.findListByParam(handover);
        handoverCollection.setGroupCondition(handoverList.get(0).getGroupCondition());
        handoverCollection.setHandoverBatch(whOutboundbox.getBatch());
        handoverCollection.setHandoverStationId(HandoverStation.getId());
        handoverCollection.setHandoverStationType(HandoverStation.getType());
        handoverCollection.setHandoverStatus("5");
        handoverCollection.setOuId(HandoverStation.getOuId());
        handoverCollection.setOutboundboxCode(whOutboundbox.getOutboundboxCode());
        handoverCollection.setOutboundboxId(whOutboundbox.getOutboundboxId());
        handoverCollectionDao.insert(handoverCollection);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public String findBatchByHandoverStationIdAndStatus(Long handoverStationId, Integer status, Long ouId) {
        return handoverCollectionDao.findBatchByHandoverStationIdAndStatus(handoverStationId, status, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<HandoverCollection> findByHandoverStation(Long id, Long ouId) {
        return handoverCollectionDao.findByHandoverStation(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdateHandoverCollection(HandoverCollection hc) {
        handoverCollectionDao.saveOrUpdate(hc);
    }

    /**
     * 出库箱出库
     * 
     * @param hcList 要出库的所有出库箱信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> handover(List<HandoverCollection> hcList, Long ouId, Long userId) {
        if (log.isInfoEnabled()) {
            log.info("HandoverManagerImpl.handover start, logId is:[{}]", logId);
        }
        if (null == hcList || 0 == hcList.size()) {
            log.error("hcList is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.HANDOVER_COLLECTION_IS_NULL);
        }
        // 2生成交接信息 交接头
        Handover handover = new Handover();
        HandoverCollection handoverCollectionrecord = hcList.get(0);
        handover.setGroupCondition(handoverCollectionrecord.getGroupCondition());
        handover.setHandoverBatch(handoverCollectionrecord.getHandoverBatch());
        handover.setHandoverStationId(handoverCollectionrecord.getHandoverStationId());
        handover.setHandoverStationType(handoverCollectionrecord.getHandoverStationType());
        handover.setOuId(ouId);
        // 总出库箱数
        Integer totalBox = handoverCollectionDao.findCountByHandoverStationIdAndStatus(hcList.get(0).getHandoverStationId(), Constants.HANDOVER_COLLECTION_TO_HANDOVER, ouId);
        handover.setTotalBox(totalBox);
        // 计重
        Long totalCalcWeight = 0L;
        // 称重
        Long totalActualWeight = 0L;
        for (HandoverCollection handoverCollection : hcList) {
            WhOdoPackageInfo whOdoPackageInfo = whOdoPackageInfoDao.findByOutboundBoxCode(handoverCollection.getOutboundboxCode(), ouId);
            if (null != whOdoPackageInfo) {
                totalCalcWeight += whOdoPackageInfo.getCalcWeight();
                totalActualWeight += whOdoPackageInfo.getActualWeight();
            }
        }
        handover.setTotalActualWeight(totalActualWeight);
        handover.setTotalCalcWeight(totalActualWeight);
        handover.setCreateTime(new Date());
        handover.setCreateId(userId);
        handover.setLastModifyTime(new Date());
        handover.setModifiedId(userId);
        Integer isTheSameCodeAndName = handoverCollectionDao.isTheSameCodeAndName(handoverCollectionrecord.getHandoverStationId(), ouId);
        if (1 == isTheSameCodeAndName) {
            // 店铺运输客户都一样 交接头中保存客户店铺运输
            String outboundboxCode = hcList.get(0).getOutboundboxCode();
            WhOutboundbox whOutboundbox = new WhOutboundbox();
            whOutboundbox.setOutboundboxCode(outboundboxCode);
            List<WhOutboundbox> WhOutboundboxList = whOutboundboxDao.findListByParam(whOutboundbox);
            WhOutboundbox outboundbox = WhOutboundboxList.get(0);
            handover.setCustomerCode(outboundbox.getCustomerCode());
            handover.setCustomerName(outboundbox.getCustomerName());
            handover.setStoreCode(outboundbox.getStoreCode());
            handover.setStoreName(outboundbox.getStoreName());
            handover.setTransportCode(outboundbox.getTransportCode());
            handover.setTransportName(outboundbox.getTransportName());

        }


        Handover handover2 = handoverDao.findByBatch(handoverCollectionrecord.getHandoverBatch());
        if (null != handover2) {
            // 已有该交接头信息
            log.error("handover error handover already exist, handover2 is:[{}]", handover2);
            throw new BusinessException(ErrorCodes.HANDOVER_EXISTS);
        }
        long inserthandover = handoverDao.insert(handover);
        if (0 == inserthandover) {
            // 插入交接表失败
            log.error("handover error handover insert error, inserthandovermumber is:[{}]", inserthandover);
            throw new BusinessException(ErrorCodes.HANDOVER_INSERT_ERRPR);
        }
        Long handoverId = handover.getId();
        // 1执行出库 保存库存日志
        List<Long> handoverIds = new ArrayList<Long>();
        handoverIds.add(handoverId);
        for (HandoverCollection handoverCollection : hcList) {
            String outboundboxCode = handoverCollection.getOutboundboxCode();
            List<WhSkuInventory> skuInventoryList = whSkuInventoryDao.findSkuInvByoutboundboxCode(outboundboxCode, ouId);
            if (null == skuInventoryList || skuInventoryList.size() == 0) {
                // 库存为空
                log.error("handover error skuInventoryList  , skuInventoryList is:[{}]", skuInventoryList);
                throw new BusinessException(ErrorCodes.SKUINVENTORY_IS_NULL);
            }
            for (WhSkuInventory whSkuInventory : skuInventoryList) {
                insertSkuInventoryLog(whSkuInventory.getId(), -whSkuInventory.getOnHandQty(), whSkuInventory.getOnHandQty(), true, ouId, userId, InvTransactionType.HANDOVER_OUTBOUND);
                int deleteWhSkuInventoryById = whSkuInventoryDao.deleteWhSkuInventoryById(whSkuInventory.getId(), ouId);
                if (0 == deleteWhSkuInventoryById) {
                    // 库存删除失败
                    log.error("handover error deleteWhSkuInventoryById  , inserthandovermumber is:[{}]", deleteWhSkuInventoryById);
                    throw new BusinessException(ErrorCodes.SKUINVENTORY_DELETE_ERROR);
                }
            }
            // 3交接明细
            HandoverLine handoverLine = new HandoverLine();
            handoverLine.setHandoverId(handoverId);
            handoverLine.setOuId(handoverCollection.getOuId());
            handoverLine.setOutboundboxCode(outboundboxCode);
            handoverLine.setOutboundboxId(handoverCollection.getOutboundboxId());
            WhOutboundbox whOutboundbox = new WhOutboundbox();
            whOutboundbox.setOutboundboxCode(outboundboxCode);
            List<WhOutboundbox> WhOutboundboxList = whOutboundboxDao.findListByParam(whOutboundbox);
            WhOutboundbox outboundbox = WhOutboundboxList.get(0);
            handoverLine.setWhOutboundboxId(outboundbox.getId() + "");
            handoverLineDao.insert(handoverLine);
            // 4更新集货交接状态为已完成
            handoverCollection.setHandoverStatus(HandoverCollectionStatus.FINISH);
            handoverCollectionDao.saveOrUpdate(handoverCollection);
            // 5出库箱状态改为已交接
            outboundbox.setStatus(OutboundboxStatus.FINISH);
            whOutboundboxDao.saveOrUpdate(outboundbox);
            // 6出库单下所有对应的出库箱是否都已经交接 如果都交接了 更新订单状态 订单明细状态
            Long odoId = outboundbox.getOdoId();
            int a = whOutboundboxDao.findunhandoverBoxByOdoId(odoId, ouId);
            if (0 == a) {


                WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
                odo.setOdoStatus(OdoStatus.FINISH);
                int odoUpdate = whOdoDao.saveOrUpdateByVersion(odo);
                if (0 == odoUpdate) {
                    // 出库单状态更新失败
                    log.error("handover error whOdoDao.saveOrUpdateByVersion  , odoUpdate is:[{}]", odoUpdate);
                    throw new BusinessException(ErrorCodes.ODO_SAVEORUPDATEBYVERSION_ERROR);
                }
                // 调用胡斌方法
                // TODO
                whOutboundConfirmManager.saveWhOutboundConfirm(odo);
                List<WhOdoLine> whOdoLineList = whOdoLineDao.findOdoLineListByOdoIdOuId(odoId, ouId);
                for (WhOdoLine whOdoLine : whOdoLineList) {
                    whOdoLine.setOdoLineStatus(OdoLineStatus.HANDOVER_FINISH);
                    int odoLineUpdate = whOdoLineDao.saveOrUpdate(whOdoLine);
                    if (0 == odoLineUpdate) {
                        // 出库单明细状态更新失败
                        log.error("handover error whOdoLineDao.saveOrUpdateByVersion  , odoUpdate is:[{}]", odoLineUpdate);
                        throw new BusinessException(ErrorCodes.ODOLINE_SAVEORUPDATEBYVERSION_ERROR);
                    }
                }
            }
        }

        if (log.isInfoEnabled()) {
            log.info("HandoverManagerImpl.handover end, logId is:[{}]", logId);
        }
        return handoverIds;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void print(List<HandoverCollection> hcList) {
        // 打印出库单据
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Long check(List<HandoverCollection> hcList, Long ouId) {
        // 判断交接批次下出库箱是否全部交接
        return handoverCollectionDao.findHandoverCollectionByBatchAndStatus(hcList.get(0).getHandoverBatch(), HandoverCollectionStatus.TO_HANDOVER, ouId);

    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public HandoverCollection findHandoverCollectionByOutboundboxCode(String outboundBoxCode, Long ouId) {
        return handoverCollectionDao.findByOutboundboxCode(outboundBoxCode, ouId);
    }


    /**
     * 算计重
     * 
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void packageWeightCalculationByOdo(WhOutboundbox whOutboundbox, Long ouId, Long userId) {
        List<UomCommand> weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL); // 重量度量单位
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
            }
        }
        SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(weightUomConversionRate);
        Double sum = 0.0;
        WhSkuInventory whSkuInventory = new WhSkuInventory();
        whSkuInventory.setOutboundboxCode(whOutboundbox.getOutboundboxCode());
        whSkuInventory.setOuId(ouId);
        List<WhSkuInventory> whSkuInventories = whSkuInventoryDao.findListByParam(whSkuInventory);
        for (WhSkuInventory whSkuInventory2 : whSkuInventories) {
            Double actualWeight = 0.0;
            WhSku whSku = whSkuManager.getskuById(whSkuInventory2.getSkuId(), ouId);
            actualWeight = weightCalculator.calculateStuffWeight(whSku.getWeight()) * whSkuInventory2.getOnHandQty();
            sum += actualWeight;
        }
        WhOdoPackageInfo odoPackageInfo = whOdoPackageInfoDao.findByOutboundBoxCode(whOutboundbox.getOutboundboxCode(), ouId);
        if (null != odoPackageInfo) {
            odoPackageInfo.setCalcWeight(sum.longValue());
            whOdoPackageInfoDao.saveOrUpdateByVersion(odoPackageInfo);
        } else {
            // WhFunctionOutBound whFunctionOutBound =
            // whFunctionOutBoundDao.findByFunctionIdExt(functionId, ouId);
            WhOdoPackageInfo whOdoPackageInfo = new WhOdoPackageInfo();
            whOdoPackageInfo.setOdoId(whOutboundbox.getOdoId());
            whOdoPackageInfo.setOutboundboxId(whOutboundbox.getId());
            whOdoPackageInfo.setOutboundboxCode(whOutboundbox.getOutboundboxCode());
            whOdoPackageInfo.setStatus(Constants.LIFECYCLE_START);
            // whOdoPackageInfo.setFloats(whFunctionOutBound.getWeightFloatPercentage());
            whOdoPackageInfo.setLifecycle(Constants.LIFECYCLE_START);
            whOdoPackageInfo.setCreateId(userId);
            whOdoPackageInfo.setCreateTime(new Date());
            whOdoPackageInfo.setLastModifyTime(new Date());
            whOdoPackageInfo.setModifiedId(userId);
            whOdoPackageInfo.setCalcWeight(sum.longValue());
            whOdoPackageInfo.setOuId(ouId);
            whOdoPackageInfoDao.insert(whOdoPackageInfo);
        }
    }
}
