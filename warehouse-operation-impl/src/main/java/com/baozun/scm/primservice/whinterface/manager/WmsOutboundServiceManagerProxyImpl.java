package com.baozun.scm.primservice.whinterface.manager;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundCancel;
import com.baozun.scm.primservice.whinterface.model.outbound.WmsOutBoundLocked;
import com.baozun.scm.primservice.whinterface.msg.WmsErrorCode;
import com.baozun.scm.primservice.whinterface.msg.WmsInterfaceConstant;
import com.baozun.scm.primservice.whinterface.msg.WmsResponse;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.manager.odo.OdoManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoLineManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

@Service("wmsOutboundServiceManagerProxy")
public class WmsOutboundServiceManagerProxyImpl implements WmsOutboundServiceManagerProxy {
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
                return new WmsResponse(0, WmsErrorCode.WAREHOUSE_IS_NULL, "WAREHOUSE_IS_NULL");
            }
            Long ouId = warehouse.getId();
            List<WhOdo> odoList = this.odoManager.findByExtCodeOuIdNotCancel(wmsOutBoundLocked.getExtOdoCode(), ouId);

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
            return new WmsResponse(0, WmsErrorCode.UNKNOWN_ERROR, "UNKNOWN_ERROR");
        }

        return new WmsResponse(1, null, null);
    }

    @Override
    public WmsResponse wmsOutBoundCancel(WmsOutBoundCancel wmsOutBoundCancel) {
        try {
            WmsResponse checkResponse=this.checkParamsForOutBoundCancel(wmsOutBoundCancel);
            if (WmsResponse.STATUS_ERROR == checkResponse.getStatus()) {
                return checkResponse;
            }

            Warehouse warehouse = this.warehouseManager.findWarehouseByCode(wmsOutBoundCancel.getWhCode());
            if (warehouse == null) {
                return new WmsResponse(0, WmsErrorCode.WAREHOUSE_IS_NULL, "WAREHOUSE_IS_NULL");
            }
            Long ouId = warehouse.getId();
            List<WhOdo> odoList = this.odoManager.findByExtCodeOuIdNotCancel(wmsOutBoundCancel.getExtOdoCode(), ouId);

            if (odoList == null || odoList.size() == 0) {
                return new WmsResponse(0, WmsErrorCode.EXTCODE_NO_ERROR, "EXTCODE_NO_ERROR");
            }

            if (odoList.size() > 1) {
                return new WmsResponse(0, WmsErrorCode.EXTCODE_NOT_UNIQUE_ERROR, "EXTCODE_NOT_UNIQUE_ERROR");
            }
            WhOdo odo = odoList.get(0);
            if (!OdoStatus.ODO_NEW.equals(odo.getOdoStatus())) {
                return new WmsResponse(0, WmsErrorCode.ODO_STATUS_CANCEL_ERROR, "ODO_STATUS_CANCEL_ERROR");
            }
            if (wmsOutBoundCancel.getIsOdoCancel().booleanValue()) {
                this.odoManagerProxy.cancel(odo, ouId, true, null, null, null);
            } else {
                List<WhOdoLine> lineList = null;
                try {
                    lineList = this.odoLineManager.findOdoLineListByOdoIdAndLinenumList(odo.getId(), ouId, wmsOutBoundCancel.getLineSeq());
                } catch (Exception ex) {
                    return new WmsResponse(0, WmsErrorCode.SEARCH_ERROR, "SEARCH_ERROR");
                }
                try {
                    this.odoManagerProxy.cancel(odo, ouId, false, lineList, null, null);
                } catch (Exception ex) {
                    return new WmsResponse(0, WmsErrorCode.UPDATE_DATA_ERROR, "CANCEL ERROR");
                }
            }

        } catch (Exception e) {
            return new WmsResponse(0, WmsErrorCode.UNKNOWN_ERROR, "UNKNOWN_ERROR");
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



}
