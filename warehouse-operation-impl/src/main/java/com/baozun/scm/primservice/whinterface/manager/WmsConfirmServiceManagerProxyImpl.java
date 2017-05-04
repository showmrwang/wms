package com.baozun.scm.primservice.whinterface.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whinterface.model.inbound.WmsInBoundConfirm;
import com.baozun.scm.primservice.whinterface.model.inbound.WmsInBoundInvLineConfirm;
import com.baozun.scm.primservice.whinterface.model.inbound.WmsInBoundLineConfirm;
import com.baozun.scm.primservice.whinterface.model.inbound.WmsInBoundSnLineConfirm;
import com.baozun.scm.primservice.whinterface.model.inventory.WmsInvoiceConfirm;
import com.baozun.scm.primservice.whinterface.model.inventory.WmsSkuInventoryFlow;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundAttrConfirm;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundConfirm;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundInvoiceConfirm;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundInvoiceLineConfirm;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundLineConfirm;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundSnLineConfirm;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundStatusConfirm;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.confirm.WhInvoiceConfirmManager;
import com.baozun.scm.primservice.whoperation.manager.confirm.WhOdoStatusConfirmManager;
import com.baozun.scm.primservice.whoperation.manager.confirm.inbound.WhInboundConfirmManager;
import com.baozun.scm.primservice.whoperation.manager.confirm.outbound.WhOutboundConfirmManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryFlowManager;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundAttrConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundInvoiceConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundInvoiceLineConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundLineConfirm;
import com.baozun.scm.primservice.whoperation.model.confirm.outbound.WhOutboundSnLineConfirm;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundConfirm;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundInvLineConfirm;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundLineConfirm;
import com.baozun.scm.primservice.whoperation.model.whinterface.inbound.WhInboundSnLineConfirm;
import com.baozun.scm.primservice.whoperation.util.DateUtil;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Service("wmsConfirmServiceManagerProxy")
public class WmsConfirmServiceManagerProxyImpl implements WmsConfirmServiceManagerProxy {

    public static final Logger log = LoggerFactory.getLogger(WmsConfirmServiceManagerProxyImpl.class);

    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private WhSkuInventoryFlowManager whSkuInventoryFlowManager;
    @Autowired
    private WhOdoStatusConfirmManager whOdoStatusConfirmManager;
    @Autowired
    private WhOutboundConfirmManager whOutboundConfirmManager;
    @Autowired
    private WhInvoiceConfirmManager whInvoiceConfirmManager;
    @Autowired
    private WhInboundConfirmManager whInboundConfirmManager;

    /**
     * 同步库存流水 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param start not null 开始记录数
     * @param pageSize not null 每次多少条
     * @param whCode not null 仓库编码
     * @param customerCode not null 客户编码
     * @return
     */
    @Override
    public List<WmsSkuInventoryFlow> wmsSkuInventoryFlow(Date beginTime, Date endTime, Integer start, Integer pageSize, String whCode, String customerCode) {
        log.info("WmsConfirmServiceManagerProxy.wmsSkuInventoryFlow begin!");
        // 判断传入值是否为空
        if (null == beginTime) {
            throw new BusinessException("beginTime is null error");
        }
        if (null == endTime) {
            throw new BusinessException("endTime is null error");
        }
        if (StringUtil.isEmpty(whCode)) {
            throw new BusinessException("whCode is null error");
        }
        if (StringUtil.isEmpty(customerCode)) {
            throw new BusinessException("customerCode is null error");
        }
        if (null == start) {
            throw new BusinessException("start is null error");
        }
        if (null == pageSize) {
            throw new BusinessException("pageSize is null error");
        }
        // 验证仓库是否存在
        Warehouse w = warehouseManager.findWarehouseByCode(whCode);
        if (null == w) {
            throw new BusinessException("warehouse is null error");
        }
        // 格式化时间
        String begin = DateUtil.formatDate(beginTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        String end = DateUtil.formatDate(endTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        if (log.isDebugEnabled()) {
            log.debug("WmsConfirmServiceManagerProxy.wmsSkuInventoryFlow beginTime: " + begin + " endTime: " + end + " start: " + start + "pageSize: " + pageSize + " whCode: " + whCode + " customerCode: " + customerCode);
        }
        // 获取对应时间段+仓库的库存流水信息
        List<WmsSkuInventoryFlow> wmsSkuInventoryFlows = whSkuInventoryFlowManager.findWmsSkuInventoryFlowByCreateTime(begin, end, start, pageSize, w.getId());
        for (WmsSkuInventoryFlow wmsSkuInventoryFlow : wmsSkuInventoryFlows) {
            wmsSkuInventoryFlow.setWhCode(whCode);
        }
        log.info("WmsConfirmServiceManagerProxy.wmsSkuInventoryFlow end!");
        return wmsSkuInventoryFlows;
    }

    /**
     * 同步出库单状态反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param start not null 开始记录数
     * @param pageSize not null 每次多少条
     * @param whCode not null 仓库编码
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    @Override
    public List<WmsOutBoundStatusConfirm> wmsOutBoundStatusConfirm(Date beginTime, Date endTime, Integer start, Integer pageSize, String whCode, String dataSource) {
        log.info("WmsConfirmServiceManagerProxy.wmsOutBoundStatusConfirm begin!");
        // 判断传入值是否为空
        if (null == beginTime) {
            throw new BusinessException("beginTime is null error");
        }
        if (null == endTime) {
            throw new BusinessException("endTime is null error");
        }
        if (StringUtil.isEmpty(whCode)) {
            throw new BusinessException("whCode is null error");
        }
        if (StringUtil.isEmpty(dataSource)) {
            throw new BusinessException("dataSource is null error");
        }
        if (null == start) {
            throw new BusinessException("start is null error");
        }
        if (null == pageSize) {
            throw new BusinessException("pageSize is null error");
        }
        // 验证仓库是否存在
        Warehouse w = warehouseManager.findWarehouseByCode(whCode);
        if (null == w) {
            throw new BusinessException("warehouse is null error");
        }
        // 格式化时间
        String begin = DateUtil.formatDate(beginTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        String end = DateUtil.formatDate(endTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        if (log.isDebugEnabled()) {
            log.debug("WmsConfirmServiceManagerProxy.wmsOutBoundStatusConfirm beginTime: " + begin + " endTime: " + end + " start: " + start + "pageSize: " + pageSize + " whCode: " + whCode + " dataSource: " + dataSource);
        }
        List<WmsOutBoundStatusConfirm> whOutboundStatusConfirms = whOdoStatusConfirmManager.findWmsOutBoundStatusConfirmByCreateTimeAndDataSource(begin, end, start, pageSize, w.getId(), dataSource);
        for (WmsOutBoundStatusConfirm wmsOutBoundStatusConfirm : whOutboundStatusConfirms) {
            // 有数据生成同步数据
            wmsOutBoundStatusConfirm.setWhCode(whCode);
        }
        log.info("WmsConfirmServiceManagerProxy.wmsOutBoundStatusConfirm end!");
        return whOutboundStatusConfirms;
    }

    /**
     * 同步出库单反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param start not null 开始记录数
     * @param pageSize not null 每次多少条
     * @param whCode not null 仓库编码
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    @Override
    public List<WmsOutBoundConfirm> wmsOutBoundConfirm(Date beginTime, Date endTime, Integer start, Integer pageSize, String whCode, String dataSource) {
        log.info("WmsConfirmServiceManagerProxy.wmsOutBoundConfirm begin!");
        // 判断传入值是否为空
        if (null == beginTime) {
            throw new BusinessException("beginTime is null error");
        }
        if (null == endTime) {
            throw new BusinessException("endTime is null error");
        }
        if (StringUtil.isEmpty(whCode)) {
            throw new BusinessException("whCode is null error");
        }
        if (StringUtil.isEmpty(dataSource)) {
            throw new BusinessException("dataSource is null error");
        }
        if (null == start) {
            throw new BusinessException("start is null error");
        }
        if (null == pageSize) {
            throw new BusinessException("pageSize is null error");
        }
        // 验证仓库是否存在
        Warehouse w = warehouseManager.findWarehouseByCode(whCode);
        if (null == w) {
            throw new BusinessException("warehouse is null error");
        }
        // 格式化时间
        String begin = DateUtil.formatDate(beginTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        String end = DateUtil.formatDate(endTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        if (log.isDebugEnabled()) {
            log.debug("WmsConfirmServiceManagerProxy.wmsOutBoundConfirm beginTime: " + begin + " endTime: " + end + " start: " + start + "pageSize: " + pageSize + " whCode: " + whCode + " dataSource: " + dataSource);
        }
        List<WmsOutBoundConfirm> wobc = new ArrayList<WmsOutBoundConfirm>();
        // 获取出库单反馈数据
        List<WhOutboundConfirm> whOutboundConfirms = whOutboundConfirmManager.findWhOutboundConfirmByCreateTimeAndDataSource(begin, end, start, pageSize, w.getId(), dataSource);
        for (WhOutboundConfirm whOutboundConfirm : whOutboundConfirms) {
            WmsOutBoundConfirm o = new WmsOutBoundConfirm();
            // 有数据生成同步数据
            BeanUtils.copyProperties(whOutboundConfirm, o);
            o.setWhCode(whCode);
            if (whOutboundConfirm.getWmsOdoStatus() != 1) {
                o.setOutboundTime(whOutboundConfirm.getCreateTime());
            }
            // 运输服务商 快递单号数据封装
            List<String> tspList = new ArrayList<String>();
            if (!StringUtil.isEmpty(whOutboundConfirm.getTransportServiceProvider())) {
                // 不为空需要处理
                String[] tsp = whOutboundConfirm.getTransportServiceProvider().split(",");
                for (int i = 0; i < tsp.length; i++) {
                    tspList.add(tsp[i]);
                }
                o.setTransportServiceProviders(tspList);
            }
            // 出库单附加信息
            WmsOutBoundAttrConfirm attr = null;
            WhOutboundAttrConfirm attrConfirm = whOutboundConfirm.getWhOutboundAttrConfirm();
            if (null != attrConfirm) {
                attr = new WmsOutBoundAttrConfirm();
                BeanUtils.copyProperties(attrConfirm, attr);
            }
            o.setWmsOutBoundAttrConfirm(attr);
            List<WmsOutBoundLineConfirm> lineConfirms = new ArrayList<WmsOutBoundLineConfirm>();
            // 出库单明细信息
            List<WhOutboundLineConfirm> whOutboundLineConfirms = whOutboundConfirm.getWhOutBoundLineConfirm();
            for (WhOutboundLineConfirm line : whOutboundLineConfirms) {
                WmsOutBoundLineConfirm wmsOutboundLineConfirm = new WmsOutBoundLineConfirm();
                BeanUtils.copyProperties(line, wmsOutboundLineConfirm);
                lineConfirms.add(wmsOutboundLineConfirm);
                List<WmsOutBoundSnLineConfirm> snLineConfirms = new ArrayList<WmsOutBoundSnLineConfirm>();
                // 出库单明细SN/残次信息
                List<WhOutboundSnLineConfirm> snLine = line.getSnLineConfirms();
                for (WhOutboundSnLineConfirm sn : snLine) {
                    WmsOutBoundSnLineConfirm s = new WmsOutBoundSnLineConfirm();
                    BeanUtils.copyProperties(sn, s);
                    snLineConfirms.add(s);
                }
                wmsOutboundLineConfirm.setWmsOutBoundSnLineConfirm(snLineConfirms);
            }
            o.setWmsOutBoundLineConfirm(lineConfirms);
            List<WmsOutBoundInvoiceConfirm> invoiceConfirms = new ArrayList<WmsOutBoundInvoiceConfirm>();
            // 出库单发票信息
            List<WhOutboundInvoiceConfirm> invoice = whOutboundConfirm.getWhOutBoundInvoiceConfirm();
            for (WhOutboundInvoiceConfirm inv : invoice) {
                WmsOutBoundInvoiceConfirm i = new WmsOutBoundInvoiceConfirm();
                BeanUtils.copyProperties(inv, i);
                List<WmsOutBoundInvoiceLineConfirm> invoiceLine = new ArrayList<WmsOutBoundInvoiceLineConfirm>();
                // 出库单发票明细信息
                List<WhOutboundInvoiceLineConfirm> invoiceLineConfirms = inv.getWhOutBoundConfirmInvoiceLines();
                for (WhOutboundInvoiceLineConfirm invLine : invoiceLineConfirms) {
                    WmsOutBoundInvoiceLineConfirm line = new WmsOutBoundInvoiceLineConfirm();
                    BeanUtils.copyProperties(invLine, line);
                    invoiceLine.add(line);
                }
                invoiceConfirms.add(i);
            }
            o.setWmsOutBoundInvoiceConfirm(invoiceConfirms);
            wobc.add(o);
        }
        log.info("WmsConfirmServiceManagerProxy.wmsOutBoundConfirm begin!");
        return wobc;
    }

    /**
     * 发票信息反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param start not null 开始记录数
     * @param pageSize not null 每次多少条
     * @param whCode not null 仓库编码
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    @Override
    public List<WmsInvoiceConfirm> wmsInvoiceConfirm(Date beginTime, Date endTime, Integer start, Integer pageSize, String whCode, String dataSource) {
        log.info("WmsConfirmServiceManagerProxy.wmsInvoiceConfirm begin!");
        // 判断传入值是否为空
        if (null == beginTime) {
            throw new BusinessException("beginTime is null error");
        }
        if (null == endTime) {
            throw new BusinessException("endTime is null error");
        }
        if (StringUtil.isEmpty(whCode)) {
            throw new BusinessException("whCode is null error");
        }
        if (StringUtil.isEmpty(dataSource)) {
            throw new BusinessException("dataSource is null error");
        }
        if (null == start) {
            throw new BusinessException("start is null error");
        }
        if (null == pageSize) {
            throw new BusinessException("pageSize is null error");
        }
        // 验证仓库是否存在
        Warehouse w = warehouseManager.findWarehouseByCode(whCode);
        if (null == w) {
            throw new BusinessException("warehouse is null error");
        }
        // 格式化时间
        String begin = DateUtil.formatDate(beginTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        String end = DateUtil.formatDate(endTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        if (log.isDebugEnabled()) {
            log.debug("WmsConfirmServiceManagerProxy.wmsOutBoundStatusConfirm beginTime: " + begin + " endTime: " + end + " start: " + start + "pageSize: " + pageSize + " whCode: " + whCode + " dataSource: " + dataSource);
        }
        List<WmsInvoiceConfirm> invoiceConfirmList = whInvoiceConfirmManager.findWmsInvoiceConfirmByCreateTimeAndDataSource(begin, end, start, pageSize, w.getId(), dataSource);
        for (WmsInvoiceConfirm wmsInvoiceConfirm : invoiceConfirmList) {
            // 有数据生成同步数据
            wmsInvoiceConfirm.setWhCode(whCode);
        }
        log.info("WmsConfirmServiceManagerProxy.wmsInvoiceConfirm end!");
        return invoiceConfirmList;
    }

    /**
     * 入库单信息反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param start not null 开始记录数
     * @param pageSize not null 每次多少条
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    @Override
    public List<WmsInBoundConfirm> wmsInBoundConfirm(Date beginTime, Date endTime, Integer start, Integer pageSize, String dataSource) {
        log.info("WmsConfirmServiceManagerProxy.wmsInBoundConfirm begin!");
        // 判断传入值是否为空
        if (null == beginTime) {
            throw new BusinessException("beginTime is null error");
        }
        if (null == endTime) {
            throw new BusinessException("endTime is null error");
        }
        if (StringUtil.isEmpty(dataSource)) {
            throw new BusinessException("dataSource is null error");
        }
        if (null == start) {
            throw new BusinessException("start is null error");
        }
        if (null == pageSize) {
            throw new BusinessException("pageSize is null error");
        }
        // 格式化时间
        String begin = DateUtil.formatDate(beginTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        String end = DateUtil.formatDate(endTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        if (log.isDebugEnabled()) {
            log.debug("WmsConfirmServiceManagerProxy.wmsInBoundConfirm beginTime: " + begin + " endTime: " + end + " start: " + start + "pageSize: " + pageSize + " dataSource: " + dataSource);
        }
        List<WmsInBoundConfirm> wmsInboundConfirms = new ArrayList<WmsInBoundConfirm>();
        // 获取入库单反馈相关数据
        List<WhInboundConfirm> whInboundConfirms = whInboundConfirmManager.findWhInboundConfirmByCreateTimeAndDataSource(begin, end, start, pageSize, dataSource);
        for (WhInboundConfirm whInboundConfirm : whInboundConfirms) {
            WmsInBoundConfirm inbound = new WmsInBoundConfirm();
            BeanUtils.copyProperties(whInboundConfirm, inbound);
            inbound.setDeliveryTimeStr(DateUtil.formatDate(whInboundConfirm.getDeliveryTime(), DateUtil.DEFAULT_DATE_TIME_FORMAT_NEW));
            // 获取入库单反馈明细数据
            List<WmsInBoundLineConfirm> wmsInboundLineConfirms = new ArrayList<WmsInBoundLineConfirm>();
            List<WhInboundLineConfirm> whInboundLineConfirms = whInboundConfirm.getWhInboundLineConfirms();
            for (WhInboundLineConfirm inboundLine : whInboundLineConfirms) {
                WmsInBoundLineConfirm line = new WmsInBoundLineConfirm();
                BeanUtils.copyProperties(inboundLine, line);
                // 获取入库单库存反馈明细数据
                List<WmsInBoundInvLineConfirm> inboundInvLine = new ArrayList<WmsInBoundInvLineConfirm>();
                List<WhInboundInvLineConfirm> whInboundInvLineConfirms = inboundLine.getWhInboundInvLineConfirms();
                for (WhInboundInvLineConfirm invLine : whInboundInvLineConfirms) {
                    WmsInBoundInvLineConfirm inv = new WmsInBoundInvLineConfirm();
                    BeanUtils.copyProperties(inboundLine, inv);
                    // 获取入库单反馈明细SN/残次信息
                    List<WmsInBoundSnLineConfirm> wmsSnLineConfirms = new ArrayList<WmsInBoundSnLineConfirm>();
                    List<WhInboundSnLineConfirm> snLineConfirms = invLine.getWhInboundSnLineConfirms();
                    for (WhInboundSnLineConfirm whInboundSnLineConfirm : snLineConfirms) {
                        WmsInBoundSnLineConfirm sn = new WmsInBoundSnLineConfirm();
                        BeanUtils.copyProperties(whInboundSnLineConfirm, sn);
                        wmsSnLineConfirms.add(sn);
                    }
                    inboundInvLine.add(inv);
                }
                wmsInboundLineConfirms.add(line);
            }
            wmsInboundConfirms.add(inbound);
        }
        log.info("WmsConfirmServiceManagerProxy.wmsInBoundConfirm end!");
        return null;
    }
}
