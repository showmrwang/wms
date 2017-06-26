package com.baozun.scm.primservice.whinterface.manager;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundCancel;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundLocked;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundPermit;
import com.baozun.scm.primservice.whinterface.msg.WmsErrorCode;
import com.baozun.scm.primservice.whinterface.msg.WmsInterfaceConstant;
import com.baozun.scm.primservice.whinterface.msg.WmsResponse;
import com.baozun.scm.primservice.whoperation.manager.odo.OdoManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

@Service("wmsOutboundServiceManagerProxy")
public class WmsOutboundServiceManagerProxyImpl implements WmsOutboundServiceManagerProxy {

    public static final Logger log = LoggerFactory.getLogger(WmsOutboundServiceManagerProxyImpl.class);

    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private OdoManager odoManager;
    @Autowired
    private OdoLineManager odoLineManager;
    @Autowired
    private OdoManagerProxy odoManagerProxy;

    @Override
    public WmsResponse wmsOutBoundLocked(WmsOutBoundLocked wmsOutBoundLocked) {
        try {

            // 校验传入参数
            WmsResponse checkResponse = this.checkParamsForOutBoundLocked(wmsOutBoundLocked);
            if (WmsResponse.STATUS_ERROR == checkResponse.getStatus()) {
                return checkResponse;
            }
            Warehouse warehouse = this.warehouseManager.findWarehouseByCode(wmsOutBoundLocked.getWhCode());
            if (warehouse == null) {
                return new WmsResponse(0, WmsErrorCode.NOT_HAVE_WAREHOUSE_INFOMATION, "WAREHOUSE_IS_NULL");
            }
            Long ouId = warehouse.getId();
            List<WhOdo> odoList = this.odoManager.findByExtCodeOuIdNotCancel(wmsOutBoundLocked.getExtOdoCode(), wmsOutBoundLocked.getDataSource(), ouId);

            if (odoList == null || odoList.size() == 0) {
                return new WmsResponse(0, WmsErrorCode.EXTCODE_NO_ERROR, "EXTCODE_NO_ERROR");
            }

            if (odoList.size() > 1) {
                return new WmsResponse(0, WmsErrorCode.EXTCODE_NOT_UNIQUE_ERROR, "EXTCODE_NOT_UNIQUE_ERROR");
            }
            WhOdo odo = odoList.get(0);
            switch (wmsOutBoundLocked.getLocked().intValue()) {
                case WmsInterfaceConstant.OUTBOUND_LOCKED:
                    if (odo.getIsLocked()) {
                        return new WmsResponse(0, WmsErrorCode.OUTBOUND_LOCKED_ERROR, "OUT BOUND HAS LOCKED");
                    }
                    odo.setIsLocked(true);
                    break;
                case WmsInterfaceConstant.OUTBOUND_UNLOCKED:
                    if (!odo.getIsLocked()) {
                        return new WmsResponse(0, WmsErrorCode.OUTBOUND_LOCKED_ERROR, "OUT BOUND HAS UNLOCKED");
                    }
                    odo.setIsLocked(false);
                default:
                    return new WmsResponse(0, WmsErrorCode.PARAM_IS_ERROR, "PARAMS[LOCKED] MUST BE 0 OR 1");
            }
            try {

                this.odoManager.updateByVersion(odo);
            } catch (Exception e) {
                return new WmsResponse(0, WmsErrorCode.UPDATE_DATA_ERROR, "UPDATE BY VERSION ERROR");
            }
        } catch (Exception e) {
            return new WmsResponse(0, WmsErrorCode.SYSTEM_EXCEPTION, "SYSTEM_EXCEPTION");
        }

        return new WmsResponse(1, null, null);
    }

    @Override
    public WmsResponse wmsOutBoundCancel(WmsOutBoundCancel wmsOutBoundCancel) {
        log.info("wmsOutBoundCancel params:[{}]", wmsOutBoundCancel);
        try {
            WmsResponse checkResponse = this.checkParamsForOutBoundCancel(wmsOutBoundCancel);
            if (WmsResponse.STATUS_ERROR == checkResponse.getStatus()) {
                return checkResponse;
            }

            Warehouse warehouse = this.warehouseManager.findWarehouseByCode(wmsOutBoundCancel.getWhCode());
            if (warehouse == null) {
                return new WmsResponse(0, WmsErrorCode.NOT_HAVE_WAREHOUSE_INFOMATION, "WAREHOUSE_IS_NULL");
            }
            Long ouId = warehouse.getId();
            List<WhOdo> odoList = this.odoManager.findByExtCodeOuIdNotCancel(wmsOutBoundCancel.getExtOdoCode(), wmsOutBoundCancel.getDataSource(), ouId);

            if (odoList == null || odoList.size() == 0) {
                return new WmsResponse(0, WmsErrorCode.EXTCODE_NO_ERROR, "EXTCODE_NO_ERROR");
            }
            if (odoList.size() > 1) {
                return new WmsResponse(0, WmsErrorCode.EXTCODE_NOT_UNIQUE_ERROR, "EXTCODE_NOT_UNIQUE_ERROR");
            }
            WhOdo odo = odoList.get(0);
            if (wmsOutBoundCancel.getIsOdoCancel().booleanValue()) {
                ResponseMsg msg = this.odoManagerProxy.cancel(odo, ouId, true, null, null, null);
                if (ResponseMsg.STATUS_SUCCESS != msg.getResponseStatus()) {
                    return new WmsResponse(0, msg.getMsg(), "CANCEL ERROR");
                }
            } else {
                List<WhOdoLine> lineList = null;
                try {
                    lineList = this.odoLineManager.findOdoLineListByOdoIdAndLinenumList(odo.getId(), ouId, wmsOutBoundCancel.getLineSeq());
                } catch (Exception ex) {
                    return new WmsResponse(0, WmsErrorCode.SEARCH_ERROR, "SEARCH_ERROR");
                }
                ResponseMsg msg = this.odoManagerProxy.cancel(odo, ouId, true, null, null, null);
                if (ResponseMsg.STATUS_SUCCESS != msg.getResponseStatus()) {
                    return new WmsResponse(0, WmsErrorCode.UPDATE_DATA_ERROR, "CANCEL ERROR");
                }
            }

        } catch (Exception e) {
            return new WmsResponse(0, WmsErrorCode.SYSTEM_EXCEPTION, "SYSTEM_EXCEPTION");
        }
        return new WmsResponse(1, null, null);
    }

    private WmsResponse checkParamsForOutBoundCancel(WmsOutBoundCancel wmsOutBoundCancel) {
        if (StringUtils.isEmpty(wmsOutBoundCancel.getDataSource())) {
            return new WmsResponse(0, WmsErrorCode.PARAM_IS_NULL, "dataSource");
        }
        if (StringUtils.isEmpty(wmsOutBoundCancel.getExtOdoCode())) {
            return new WmsResponse(0, WmsErrorCode.PARAM_IS_NULL, "ExtOdoCode");
        }
        if (StringUtils.isEmpty(wmsOutBoundCancel.getWhCode())) {
            return new WmsResponse(0, WmsErrorCode.PARAM_IS_NULL, "WhCode");
        }
        if (null == wmsOutBoundCancel.getIsOdoCancel()) {
            return new WmsResponse(0, WmsErrorCode.PARAM_IS_NULL, "IsOdoCancel");
        }
        if (!wmsOutBoundCancel.getIsOdoCancel()) {
            if (null == wmsOutBoundCancel.getLineSeq() && wmsOutBoundCancel.getLineSeq().size() == 0) {
                return new WmsResponse(0, WmsErrorCode.PARAM_IS_NULL, "lineSeq");
            }
        }
        return new WmsResponse(1, null, null);
    }



    private WmsResponse checkParamsForOutBoundLocked(WmsOutBoundLocked wmsOutBoundLocked) {
        if (StringUtils.isEmpty(wmsOutBoundLocked.getExtOdoCode())) {
            return new WmsResponse(0, WmsErrorCode.PARAM_IS_NULL, "ExtOdoCode");
        }
        if (StringUtils.isEmpty(wmsOutBoundLocked.getWhCode())) {
            return new WmsResponse(0, WmsErrorCode.PARAM_IS_NULL, "WhCode");
        }
        if (null == wmsOutBoundLocked.getLocked()) {
            return new WmsResponse(0, WmsErrorCode.PARAM_IS_NULL, "outBoundLocked");
        }
        if (StringUtils.isEmpty(wmsOutBoundLocked.getDataSource())) {
            return new WmsResponse(0, WmsErrorCode.PARAM_IS_NULL, "dataSource");
        }
        return new WmsResponse(1, null, null);
    }

    /**
     * 出库单允许出库接口 bin.hu
     * 
     * @param wmsOutBoundPermit
     * @return
     */
    @Override
    public WmsResponse wmsOutBoundPermit(WmsOutBoundPermit wmsOutBoundPermit) {
        log.info("WmsOutboundServiceManagerProxyImpl.wmsOutBoundPermit begin!");
        try {
            // 验证数据
            WmsResponse checkResponse = checkParamsForOutBoundPermit(wmsOutBoundPermit);
            if (WmsResponse.STATUS_ERROR == checkResponse.getStatus()) {
                return checkResponse;
            }
            // 验证仓库是否存在
            Warehouse warehouse = warehouseManager.findWarehouseByCode(wmsOutBoundPermit.getWhCode());
            if (warehouse == null) {
                return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.NOT_HAVE_WAREHOUSE_INFOMATION, "WAREHOUSE_IS_NULL");
            }
            // 获取对应出库单数据 状态不能为取消
            List<WhOdo> odoList = odoManager.findByExtCodeOuIdNotCancel(wmsOutBoundPermit.getExtOdoCode(), wmsOutBoundPermit.getDataSource(), warehouse.getId());
            if (null == odoList || odoList.size() == 0) {
                return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.EXTCODE_NO_ERROR, "EXTCODE_NO_ERROR");
            }
            try {
                // 修改对应数据
                odoManager.wmsOutBoundPermit(odoList);
            } catch (Exception e) {
                return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.UPDATE_DATA_ERROR, "UPDATE_DATA_ERROR");
            }
        } catch (Exception e) {
            return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.SYSTEM_EXCEPTION, "SYSTEM_EXCEPTION");
        }
        log.info("WmsOutboundServiceManagerProxyImpl.wmsOutBoundPermit end!");
        return new WmsResponse(WmsResponse.STATUS_SUCCESS, null, null);
    }

    private WmsResponse checkParamsForOutBoundPermit(WmsOutBoundPermit wmsOutBoundPermit) {
        if (StringUtils.isEmpty(wmsOutBoundPermit.getExtOdoCode())) {
            return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.PARAM_IS_NULL, "ExtOdoCode");
        }
        if (StringUtils.isEmpty(wmsOutBoundPermit.getWhCode())) {
            return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.PARAM_IS_NULL, "WhCode");
        }
        if (StringUtils.isEmpty(wmsOutBoundPermit.getDataSource())) {
            return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.PARAM_IS_NULL, "dataSource");
        }
        return new WmsResponse(WmsResponse.STATUS_SUCCESS, null, null);
    }


}
