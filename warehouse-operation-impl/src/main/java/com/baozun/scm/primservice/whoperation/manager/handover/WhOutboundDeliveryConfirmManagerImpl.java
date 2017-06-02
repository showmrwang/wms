package com.baozun.scm.primservice.whoperation.manager.handover;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.logistics.manager.OrderConfirmContentManager;
import com.baozun.scm.primservice.logistics.model.OrderConfirmContent;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OutboundDeliveryConfirmStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.handover.WhOutboundDeliveryConfirmDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.handover.WhOutboundDeliveryConfirm;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

@Service("whOutboundDeliveryConfirmManager")
@Transactional
public class WhOutboundDeliveryConfirmManagerImpl extends BaseManagerImpl implements WhOutboundDeliveryConfirmManager {
    private static final Logger log = LoggerFactory.getLogger(WhOutboundDeliveryConfirmManagerImpl.class);
    @Autowired
    private WhOutboundDeliveryConfirmDao whOutboundDeliveryConfirmDao;
    @Autowired
    private OrderConfirmContentManager orderConfirmContentManager;
    @Autowired
    private UomDao uomDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOutboundDeliveryConfirm> findAll() {
        return whOutboundDeliveryConfirmDao.findListByParam(null);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdate(WhOutboundDeliveryConfirm whOutboundDeliveryConfirm) {
        whOutboundDeliveryConfirmDao.saveOrUpdate(whOutboundDeliveryConfirm);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOutboundDeliveryConfirm> selectNewOutboundDeliveryConfirm() {
        return whOutboundDeliveryConfirmDao.selectNewOutboundDeliveryConfirm();
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void OutboundDeliveryConfirm(WhOutboundDeliveryConfirm whOutboundDeliveryConfirm) {
        try {
            OrderConfirmContent paramOrderConfirmContent = new OrderConfirmContent();
            paramOrderConfirmContent.setOrdercode(whOutboundDeliveryConfirm.getOdoCode());
            paramOrderConfirmContent.setTradeid(whOutboundDeliveryConfirm.getEcOrderCode());
            paramOrderConfirmContent.setOrdersource(whOutboundDeliveryConfirm.getOrderSource());
            paramOrderConfirmContent.setExttransorderid(whOutboundDeliveryConfirm.getExtTransOrderId());
            paramOrderConfirmContent.setWhcode(whOutboundDeliveryConfirm.getOuCode());
            paramOrderConfirmContent.setOwnercode(whOutboundDeliveryConfirm.getStoreCode());
            paramOrderConfirmContent.setLpcode(StringUtils.upperCase(whOutboundDeliveryConfirm.getLogisticsCode()));
            paramOrderConfirmContent.setTrackingno(whOutboundDeliveryConfirm.getWaybillCode());
            // paramOrderConfirmContent.setChildtracknolis();


            Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
            List<UomCommand> weightUomCmds;
            weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
            for (UomCommand lenUom : weightUomCmds) {
                String uomCode = "";
                Double uomRate = 0.0;
                if (null != lenUom) {
                    uomCode = lenUom.getUomCode();
                    uomRate = lenUom.getConversionRate();
                    weightUomConversionRate.put(uomCode, uomRate);
                }
            }
            Double weight = null;
            Double Kg = weightUomConversionRate.get("Kg");
            if (null != Kg) {
                SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(Double.valueOf(whOutboundDeliveryConfirm.getWeight()), "Kg", weightUomConversionRate);
                weight = weightCalculator.getCurrentStuffWeight();
            }
            Double KG = weightUomConversionRate.get("KG");
            if (null != KG) {
                SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(Double.valueOf(whOutboundDeliveryConfirm.getWeight()), "KG", weightUomConversionRate);
                weight = weightCalculator.getCurrentStuffWeight();
            }
            Double kg = weightUomConversionRate.get("kg");
            if (null != kg) {
                SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(Double.valueOf(whOutboundDeliveryConfirm.getWeight()), "kg", weightUomConversionRate);
                weight = weightCalculator.getCurrentStuffWeight();
            }

            paramOrderConfirmContent.setWeight(BigDecimal.valueOf(weight));

            if (null != whOutboundDeliveryConfirm.getHigh()) {
                paramOrderConfirmContent.setHeight(BigDecimal.valueOf(whOutboundDeliveryConfirm.getHigh()));
                paramOrderConfirmContent.setWidth(BigDecimal.valueOf(whOutboundDeliveryConfirm.getWidth()));
                paramOrderConfirmContent.setLeng(BigDecimal.valueOf(whOutboundDeliveryConfirm.getLength()));
            }
            if (null != whOutboundDeliveryConfirm.getConsigneeTargetTelephone()) {
                paramOrderConfirmContent.setReceiverinfo(Long.parseLong(whOutboundDeliveryConfirm.getConsigneeTargetTelephone()));
            }
            paramOrderConfirmContent.setType(whOutboundDeliveryConfirm.getType());
            paramOrderConfirmContent.setScsource(Constants.WMS4);
            paramOrderConfirmContent.setCreatetime(new Date());
            paramOrderConfirmContent.setCaseNumber(whOutboundDeliveryConfirm.getOutboundboxCode());
            orderConfirmContentManager.mialOrderComfirm(paramOrderConfirmContent, Constants.WMS4);
            whOutboundDeliveryConfirm.setStatus(OutboundDeliveryConfirmStatus.FINISH);
            whOutboundDeliveryConfirmDao.saveOrUpdate(whOutboundDeliveryConfirm);
        } catch (NumberFormatException e) {
            log.error("whOutboundDeliveryConfirmManager.OutboundDeliveryConfirm error");
            whOutboundDeliveryConfirmDao.saveOrUpdate(whOutboundDeliveryConfirm);
        }



    }
}
