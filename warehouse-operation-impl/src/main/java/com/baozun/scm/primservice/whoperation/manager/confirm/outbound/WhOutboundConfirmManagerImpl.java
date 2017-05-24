package com.baozun.scm.primservice.whoperation.manager.confirm.outbound;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxLineSnCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.dao.confirm.outbound.WhOutboundAttrConfirmDao;
import com.baozun.scm.primservice.whoperation.dao.confirm.outbound.WhOutboundConfirmDao;
import com.baozun.scm.primservice.whoperation.dao.confirm.outbound.WhOutboundInvoiceConfirmDao;
import com.baozun.scm.primservice.whoperation.dao.confirm.outbound.WhOutboundInvoiceLineConfirmDao;
import com.baozun.scm.primservice.whoperation.dao.confirm.outbound.WhOutboundLineConfirmDao;
import com.baozun.scm.primservice.whoperation.dao.confirm.outbound.WhOutboundSnLineConfirmDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoAttrDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDeliveryInfoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.InventoryStatusDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhInvoiceDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhInvoiceLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxLineSnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundAttrConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundInvoiceConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundInvoiceLineConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundLineConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundSnLineConfirm;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAttr;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhInvoice;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhInvoiceLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSku;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Service("whOutboundConfirmManager")
@Transactional
public class WhOutboundConfirmManagerImpl extends BaseManagerImpl implements WhOutboundConfirmManager {

    public static final Logger log = LoggerFactory.getLogger(WhOutboundConfirmManagerImpl.class);

    @Autowired
    private WhOutboundConfirmDao whOutboundConfirmDao;
    @Autowired
    private WhOutboundLineConfirmDao whOutboundLineConfirmDao;
    @Autowired
    private WhOutboundInvoiceConfirmDao whOutboundInvoiceConfirmDao;
    @Autowired
    private WhOutboundInvoiceLineConfirmDao whOutboundInvoiceLineConfirmDao;
    @Autowired
    private WhOutboundAttrConfirmDao whOutboundAttrConfirmDao;
    @Autowired
    private WhOdoDeliveryInfoDao whOdoDeliveryInfoDao;
    @Autowired
    private WhOdoAttrDao whOdoAttrDao;
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private InventoryStatusDao inventoryStatusDao;
    @Autowired
    private WhSkuDao whSkuDao;
    @Autowired
    private WhOutboundboxDao whOutboundboxDao;
    @Autowired
    private WhOutboundboxLineDao whOutboundboxLineDao;
    @Autowired
    private WhInvoiceDao whInvoiceDao;
    @Autowired
    private WhInvoiceLineDao whInvoiceLineDao;
    @Autowired
    private WhOutboundboxLineSnDao whOutboundboxLineSnDao;
    @Autowired
    private WhOutboundSnLineConfirmDao whOutboundSnLineConfirmDao;


    /**
     * 生成出库单反馈数据 bin.hu
     * 
     * @param whOdo
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveWhOutboundConfirm(WhOdo whOdo) {
        log.info("WhOutboundConfirmManagerImpl.saveWhOutboundConfirm begin!");
        if (null == whOdo) {
            log.warn("WhOutboundConfirmManagerImpl.saveWhOutboundConfirm whOdo is null");
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"whOdo"});
        }
        if (!StringUtil.isEmpty(whOdo.getDataSource())) {
            if (OdoStatus.FINISH.equals(whOdo.getOdoStatus()) || OdoStatus.NEW.equals(whOdo.getOdoStatus())) {
                // 只有单据状态为新建或者是出库完成才需要生成对应数据
                Long ouid = whOdo.getOuId();
                // 运输服务商-快递单号
                String transportServiceProvider = null;
                Map<String, String> tspMap = new HashMap<String, String>();
                // 如果是出库完成状态 需要封装运输服务商-快递单号信息
                if (whOdo.getOdoStatus().equals(OdoStatus.FINISH)) {
                    String tsp = "";
                    List<WhOdodeliveryInfo> whOdodeliveryInfos = whOdoDeliveryInfoDao.findWhOdodeliveryInfoByOdoId(whOdo.getId(), ouid);
                    for (WhOdodeliveryInfo whOdodeliveryInfo : whOdodeliveryInfos) {
                        // 封装对应数据格式Map<出库箱号,运单号>
                        tspMap.put(whOdodeliveryInfo.getOutboundboxCode(), whOdodeliveryInfo.getWaybillCode());
                        tsp += whOdodeliveryInfo.getTransportCode() + "-" + whOdodeliveryInfo.getWaybillCode() + ",";
                    }
                    transportServiceProvider = tsp.substring(0, tsp.length() - 1);
                }
                // 封装库存状态Map
                Map<Long, String> invMap = new HashMap<Long, String>();
                // 查询全部的库存状态
                List<InventoryStatus> inventoryStatus = inventoryStatusDao.findInventoryStatus();
                for (InventoryStatus inv : inventoryStatus) {
                    invMap.put(inv.getId(), inv.getName());
                }
                // 封装出库单反馈头信息
                WhOutboundConfirm ob = new WhOutboundConfirm();
                ob.setExtOdoCode(whOdo.getExtCode());
                ob.setWmsOdoCode(whOdo.getOdoCode());
                ob.setExtOdoType(whOdo.getExtOdoType());
                ob.setTransportServiceProvider(transportServiceProvider);
                ob.setWmsOdoStatus(Integer.parseInt(whOdo.getOdoStatus()));
                ob.setCustomerCode(getCustomerByRedis(whOdo.getCustomerId()).getCustomerCode());
                ob.setStoreCode(getStoreByRedis(whOdo.getStoreId()).getStoreCode());
                ob.setOuId(ouid);
                ob.setEcOrderCode(whOdo.getEcOrderCode());
                ob.setDataSource(whOdo.getDataSource());
                // TODO 后续增加是否整单出库完成逻辑
                ob.setCreateTime(new Date());
                Long obcount = whOutboundConfirmDao.insert(ob);
                if (obcount.intValue() == 0) {
                    log.error("WhOutboundConfirmManagerImpl.saveWhOutboundConfirm error");
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
                Long obid = ob.getId();
                // 封装出库单附加信息
                saveWhOutboundAttrConfirm(whOdo.getId(), ouid, obid);
                // 封装出库单明细信息
                saveWhOutboundLineConfirm(whOdo, ouid, obid, tspMap, invMap);
                // 封装出库单发票信息
                saveWhOutboundInvoiceConfirm(whOdo.getOdoCode(), ouid, obid);
            }
        }
        log.info("WhOutboundConfirmManagerImpl.saveWhOutboundConfirm end!");
    }

    /**
     * 封装出库单附加信息
     * 
     * @param whOdo
     * @param ob
     */
    private void saveWhOutboundAttrConfirm(Long odoid, Long ouid, Long outboundid) {
        WhOdoAttr attr = whOdoAttrDao.findWhOdoAttrByOdoId(odoid, ouid);
        if (null != attr) {
            // 有对应数据进行封装
            WhOutboundAttrConfirm attrConfirm = new WhOutboundAttrConfirm();
            BeanUtils.copyProperties(attr, attrConfirm);
            attrConfirm.setOutboundId(outboundid);
            Long attrCount = whOutboundAttrConfirmDao.insert(attrConfirm);
            if (attrCount.intValue() == 0) {
                log.error("WhOutboundConfirmManagerImpl.saveWhOutboundAttrConfirm error");
                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
            }
        }
    }

    /**
     * 封装出库单明细信息
     * 
     * @param whOdo
     * @param ouid
     * @param outboundid
     */
    private void saveWhOutboundLineConfirm(WhOdo whOdo, Long ouid, Long outboundid, Map<String, String> tspMap, Map<Long, String> invMap) {
        if (whOdo.getOdoStatus().equals(OdoStatus.NEW)) {
            // 新建状态 出库单反馈明细直接用odoLine数据
            List<WhOdoLine> whOdoLines = whOdoLineDao.findOdoLineListByOdoIdOuId(whOdo.getId(), ouid);
            for (WhOdoLine whOdoLine : whOdoLines) {
                WhOutboundLineConfirm line = new WhOutboundLineConfirm();
                BeanUtils.copyProperties(whOdoLine, line);
                line.setOutbouncConfirmId(outboundid);
                line.setWmsOdoCode(whOdo.getOdoCode());
                line.setExtLineNum(whOdoLine.getExtLinenum());
                line.setQty(whOdoLine.getPlanQty());
                line.setActualQty(whOdoLine.getActualQty());
                line.setInvStatus(invMap.get(whOdoLine.getInvStatus()));
                line.setStoreCode(getStoreByRedis(whOdoLine.getStoreId()).getStoreCode());
                WhSku sku = whSkuDao.findWhSkuById(whOdoLine.getSkuId(), ouid);
                line.setUpc(sku.getExtCode());
                line.setColor(sku.getColor());
                line.setStyle(sku.getStyle());
                line.setSize(sku.getSize());
                Long lineCount = whOutboundLineConfirmDao.insert(line);
                if (lineCount.intValue() == 0) {
                    log.error("WhOutboundConfirmManagerImpl.saveWhOutboundLineConfirm error");
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            }
        }
        if (whOdo.getOdoStatus().equals(OdoStatus.FINISH)) {
            // 出库完成状态 出库单反馈明细需要查询出库箱等数据表
            // 获取出库箱装箱信息
            List<WhOutboundbox> outboundboxs = whOutboundboxDao.findWhOutboundboxByOdoId(whOdo.getId(), ouid);
            for (WhOutboundbox whOutboundbox : outboundboxs) {
                // 获取出库箱装箱明细信息
                List<WhOutboundboxLine> outboundboxLines = whOutboundboxLineDao.findWhOutboundboxLineByBoxId(whOutboundbox.getId(), ouid);
                for (WhOutboundboxLine boxLine : outboundboxLines) {
                    WhOutboundLineConfirm line = new WhOutboundLineConfirm();
                    BeanUtils.copyProperties(boxLine, line);
                    line.setOutbouncConfirmId(outboundid);
                    line.setWmsOdoCode(whOdo.getOdoCode());
                    line.setOutboundBoxCode(whOutboundbox.getOutboundboxCode());
                    String trackNumber = tspMap.get(whOutboundbox.getOutboundboxCode());
                    line.setInvStatus(invMap.get(Long.parseLong(boxLine.getInvStatus())));
                    if (!StringUtil.isEmpty(trackNumber)) {
                        line.setTrackingNumber(trackNumber);
                    }
                    // 获取出库单明细信息
                    WhOdoLine odoLine = whOdoLineDao.findOdoLineById(boxLine.getOdoLineId(), ouid);
                    line.setQty(odoLine.getPlanQty());
                    line.setActualQty(boxLine.getQty());
                    line.setExtLineNum(odoLine.getExtLinenum());
                    WhSku sku = whSkuDao.findWhSkuById(odoLine.getSkuId(), ouid);
                    line.setUpc(sku.getExtCode());
                    line.setColor(sku.getColor());
                    line.setStyle(sku.getStyle());
                    line.setSize(sku.getSize());
                    Long lineCount = whOutboundLineConfirmDao.insert(line);
                    if (lineCount.intValue() == 0) {
                        log.error("WhOutboundConfirmManagerImpl.saveWhOutboundLineConfirm error");
                        throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                    }
                    // 获取SN/残次信息
                    List<WhOutboundboxLineSnCommand> snList = whOutboundboxLineSnDao.findWhOutboundboxLineSnCommandByOutBoundBoxLineId(boxLine.getId(), ouid);
                    for (WhOutboundboxLineSnCommand lineSnCommand : snList) {
                        // 有数据保存SN/残次反馈信息
                        WhOutboundSnLineConfirm sn = new WhOutboundSnLineConfirm();
                        BeanUtils.copyProperties(lineSnCommand, sn);
                        sn.setOutboundLineConfirmId(line.getId());
                        if (Constants.SKU_SN_DEFECT_SOURCE_WH.equals(sn.getDefectSource())) {
                            // 仓库残次原因/类型
                            sn.setDefectType(lineSnCommand.getWhDefectTypeCode());
                            sn.setDefectReasons(lineSnCommand.getWhDefectReasonsCode());
                        } else {
                            // 店铺残次原因/类型
                            sn.setDefectType(lineSnCommand.getStoreDefectTypeCode());
                            sn.setDefectReasons(lineSnCommand.getStoreDefectReasonsCode());
                        }
                        sn.setOuId(ouid);
                        Long snLineCount = whOutboundSnLineConfirmDao.insert(sn);
                        if (snLineCount.intValue() == 0) {
                            log.error("WhOutboundConfirmManagerImpl.saveWhOutboundLineConfirm error");
                            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                        }
                    }
                }
            }
        }
    }

    /**
     * 封装出库单发票信息
     * 
     * @param odoid
     * @param ouid
     * @param outboundid
     */
    private void saveWhOutboundInvoiceConfirm(String odoCode, Long ouid, Long outboundid) {
        // 获取出库单发票信息
        WhInvoice whInvoice = whInvoiceDao.findWhInvoiceByOdoId(odoCode, ouid);
        if (null != whInvoice) {
            WhOutboundInvoiceConfirm w = new WhOutboundInvoiceConfirm();
            BeanUtils.copyProperties(whInvoice, w);
            w.setOutboundConfirmId(outboundid);
            Long invCount = whOutboundInvoiceConfirmDao.insert(w);
            if (invCount.intValue() == 0) {
                log.error("WhOutboundConfirmManagerImpl.saveWhOutboundInvoiceConfirm error");
                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
            }
            // 获取出库单发票明细信息
            List<WhInvoiceLine> whInvoiceLines = whInvoiceLineDao.findWhInvoiceLineByInvoiceId(whInvoice.getId(), ouid);
            for (WhInvoiceLine whInvoiceLine : whInvoiceLines) {
                WhOutboundInvoiceLineConfirm line = new WhOutboundInvoiceLineConfirm();
                BeanUtils.copyProperties(whInvoiceLine, line);
                line.setOutboundInvoiceConfirmId(w.getId());
                Long lineCount = whOutboundInvoiceLineConfirmDao.insert(line);
                if (lineCount.intValue() == 0) {
                    log.error("WhOutboundConfirmManagerImpl.saveWhOutboundInvoiceConfirm error");
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            }
        }
    }

    /**
     * 通过创建时间段+仓库ID+数据来源获取对应出库单反馈数据 bin.hu
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOutboundConfirm> findWhOutboundConfirmByCreateTimeAndDataSource(String beginTime, String endTime, Integer start, Integer pageSize, Long ouid, String dataSource) {
        // 获取出库单反馈数据
        List<WhOutboundConfirm> whOutboundConfirms = whOutboundConfirmDao.findWhOutboundConfirmByCreateTimeAndDataSource(beginTime, endTime, start, pageSize, ouid, dataSource);
        for (WhOutboundConfirm whOutboundConfirm : whOutboundConfirms) {
            // 获取出库单附加属性数据
            WhOutboundAttrConfirm whOutboundAttrConfirm = whOutboundAttrConfirmDao.findWhOutboundAttrConfirmByOutBoundId(whOutboundConfirm.getId(), ouid);
            whOutboundConfirm.setWhOutboundAttrConfirm(whOutboundAttrConfirm);
            // 获取出库单明细数据
            List<WhOutboundLineConfirm> whOutboundLineConfirms = whOutboundLineConfirmDao.findWhOutboundLineConfirmByOutBoundId(whOutboundConfirm.getId(), ouid);
            for (WhOutboundLineConfirm whOutboundLineConfirm : whOutboundLineConfirms) {
                List<WhOutboundSnLineConfirm> snLineConfirms = whOutboundSnLineConfirmDao.findWhOutboundSnLineConfirmByOutBoundLineId(whOutboundLineConfirm.getId(), ouid);
                whOutboundLineConfirm.setSnLineConfirms(snLineConfirms);
            }
            whOutboundConfirm.setWhOutBoundLineConfirm(whOutboundLineConfirms);
            // 获取出库单发票信息
            List<WhOutboundInvoiceConfirm> whOutboundInvoiceConfirms = whOutboundInvoiceConfirmDao.findWhOutboundInvoiceConfirmByOutBoundId(whOutboundConfirm.getId(), ouid);
            for (WhOutboundInvoiceConfirm inv : whOutboundInvoiceConfirms) {
                // 获取发票明细信息
                List<WhOutboundInvoiceLineConfirm> invLine = whOutboundInvoiceLineConfirmDao.findWhOutboundInvoiceLineConfirmByInvoiceId(inv.getId(), ouid);
                inv.setWhOutBoundConfirmInvoiceLines(invLine);
            }
            whOutboundConfirm.setWhOutBoundInvoiceConfirm(whOutboundInvoiceConfirms);
        }
        return whOutboundConfirms;
    }

}
