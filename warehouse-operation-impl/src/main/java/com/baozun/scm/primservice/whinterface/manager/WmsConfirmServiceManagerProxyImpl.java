package com.baozun.scm.primservice.whinterface.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whinterface.model.inventory.WmsSkuInventoryFlow;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundStatusConfirm;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.confirm.WhOdoStatusConfirmManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryFlowManager;
import com.baozun.scm.primservice.whoperation.model.confirm.WhOdoStatusConfirm;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryFlow;
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

    /**
     * 同步库存流失 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param whCode not null 仓库编码
     * @return
     */
    @Override
    public List<WmsSkuInventoryFlow> wmsSkuInventoryFlow(Date beginTime, Date endTime, String whCode) {
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
        // 验证仓库是否存在
        Warehouse w = warehouseManager.findWarehouseByCode(whCode);
        if (null == w) {
            throw new BusinessException("warehouse is null error");
        }
        // 格式化时间
        String begin = DateUtil.formatDate(beginTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        String end = DateUtil.formatDate(endTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        if (log.isDebugEnabled()) {
            log.debug("WmsConfirmServiceManagerProxy.wmsSkuInventoryFlow beginTime: " + begin + " endTime: " + end + " whCode: " + whCode);
        }
        List<WmsSkuInventoryFlow> flow = new ArrayList<WmsSkuInventoryFlow>();
        List<WhSkuInventoryFlow> whSkuInventoryFlows = whSkuInventoryFlowManager.findWhSkuInventoryFlowByCreateTime(begin, end, w.getId());
        for (WhSkuInventoryFlow whSkuInventoryFlow : whSkuInventoryFlows) {
            // 有数据生成同步数据
            WmsSkuInventoryFlow f = new WmsSkuInventoryFlow();
            BeanUtils.copyProperties(whSkuInventoryFlow, f);
            f.setWhCode(whCode);
            flow.add(f);
        }
        // 获取对应时间段+仓库的库存流水信息
        log.info("WmsConfirmServiceManagerProxy.wmsSkuInventoryFlow end!");
        return flow;
    }

    /**
     * 同步出库单状态反馈 bin.hu
     * 
     * @param beginTime not null 数据开始时间
     * @param endTime not null 数据结束时间
     * @param whCode not null 仓库编码
     * @param dataSource not null 数据来源 区分上位系统
     * @return
     */
    @Override
    public List<WmsOutBoundStatusConfirm> wmsOutBoundStatusConfirm(Date beginTime, Date endTime, String whCode, String dataSource) {
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
        // 验证仓库是否存在
        Warehouse w = warehouseManager.findWarehouseByCode(whCode);
        if (null == w) {
            throw new BusinessException("warehouse is null error");
        }
        // 格式化时间
        String begin = DateUtil.formatDate(beginTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        String end = DateUtil.formatDate(endTime, DateUtil.DEFAULT_DATE_TIME_FORMAT);
        if (log.isDebugEnabled()) {
            log.debug("WmsConfirmServiceManagerProxy.wmsOutBoundStatusConfirm beginTime: " + begin + " endTime: " + end + " whCode: " + whCode);
        }
        List<WmsOutBoundStatusConfirm> wobsc = new ArrayList<WmsOutBoundStatusConfirm>();
        List<WhOdoStatusConfirm> whOdoStatusConfirms = whOdoStatusConfirmManager.findWhOdoStatusConfirmByCreateTimeAndDataSource(begin, end, w.getId(), dataSource);
        for (WhOdoStatusConfirm whOdoStatusConfirm : whOdoStatusConfirms) {
            WmsOutBoundStatusConfirm o = new WmsOutBoundStatusConfirm();
            // 有数据生成同步数据
            BeanUtils.copyProperties(whOdoStatusConfirm, o);
            o.setWhCode(whCode);
            wobsc.add(o);
        }
        log.info("WmsConfirmServiceManagerProxy.wmsOutBoundStatusConfirm end!");
        return wobsc;
    }
}
