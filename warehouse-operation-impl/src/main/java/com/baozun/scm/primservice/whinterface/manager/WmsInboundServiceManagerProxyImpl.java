package com.baozun.scm.primservice.whinterface.manager;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whinterface.model.inbound.WmsInBoundCancel;
import com.baozun.scm.primservice.whinterface.msg.WmsErrorCode;
import com.baozun.scm.primservice.whinterface.msg.WmsResponse;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy.EditPoAsnManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy.SelectPoAsnManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;

@Service("wmsInboundServiceManagerProxy")
public class WmsInboundServiceManagerProxyImpl implements WmsInboundServiceManagerProxy {
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private SelectPoAsnManagerProxy selectPoAsnManagerProxy;
    @Autowired
    private EditPoAsnManagerProxy editPoAsnManagerProxy;

    @Override
    public WmsResponse wmsInBoundCancel(WmsInBoundCancel wmsInBoundCancel) {
        try {
            // 校验传入参数
            WmsResponse checkResponse = this.checkParamsForInBoundCancel(wmsInBoundCancel);
            if (WmsResponse.STATUS_ERROR == checkResponse.getStatus()) {
                return checkResponse;
            }
            List<BiPo> bipoList = this.selectPoAsnManagerProxy.findBiPoByExtCode(wmsInBoundCancel.getExtPoCode());
            if (bipoList == null || bipoList.size() == 0) {
                return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.EXTCODE_NO_ERROR, "EXTCODE_NO_ERROR");
            }

            if (bipoList.size() > 1) {
                return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.EXTCODE_NOT_UNIQUE_ERROR, "EXTCODE_NOT_UNIQUE_ERROR");
            }

            BiPo bipo = bipoList.get(0);
            // @mender yimin.lu 2017/6/22
            if (null == bipo.getStatus() || (PoAsnStatus.BIPO_NEW != bipo.getStatus() && PoAsnStatus.BIPO_ALLOT != bipo.getStatus())) {
                return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.STATUS_CANCEL_ERROR, "PO_STATUS_CANCEL_ERROR");
            }
            if (wmsInBoundCancel.getIsPoCancel()) {
                ResponseMsg msg = this.editPoAsnManagerProxy.cancel(bipo.getId(), true, null, null, null);
                if (msg == null) {
                    return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.SYSTEM_EXCEPTION, "SYSTEM_EXCEPTION ");
                }
                if (ResponseMsg.STATUS_SUCCESS == msg.getReasonStatus()) {
                    return new WmsResponse(WmsResponse.STATUS_SUCCESS, null, null);
                } else {
                    return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.UPDATE_DATA_ERROR, msg.getMsg());
                }
            } else {
                List<BiPoLine> lineList = this.selectPoAsnManagerProxy.findBiPoLineByBiPoIdAndLineNums(bipo.getId(), wmsInBoundCancel.getExtLinenum());
                if (lineList == null || lineList.size() != wmsInBoundCancel.getExtLinenum().size()) {
                    return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.SEARCH_ERROR, " find BiPoLine by ExtLinenum error");
                }
                ResponseMsg msg = this.editPoAsnManagerProxy.cancel(bipo.getId(), false, lineList, null, null);
                if (msg == null) {
                    return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.SYSTEM_EXCEPTION, "SYSTEM_EXCEPTION ");
                }
                if (ResponseMsg.STATUS_SUCCESS == msg.getReasonStatus()) {
                    return new WmsResponse(WmsResponse.STATUS_SUCCESS, null, null);
                } else {
                    return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.UPDATE_DATA_ERROR, msg.getMsg());
                }
            }
        } catch (Exception e) {
            return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.SYSTEM_EXCEPTION, "SYSTEM_EXCEPTION ");
        }
    }

    private WmsResponse checkParamsForInBoundCancel(WmsInBoundCancel wmsInBoundCancel) {
        if (StringUtils.isEmpty(wmsInBoundCancel.getExtPoCode())) {
            return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.PARAM_IS_NULL, "ExtPoCode");
        }
        if (StringUtils.isEmpty(wmsInBoundCancel.getDataSource())) {
            return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.PARAM_IS_NULL, "dataSource");
        }
        if (null == wmsInBoundCancel.getIsPoCancel()) {
            return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.PARAM_IS_NULL, "IsPoCancel");
        }
        if (!wmsInBoundCancel.getIsPoCancel()) {
            if (null == wmsInBoundCancel.getExtLinenum() && wmsInBoundCancel.getExtLinenum().size() == 0) {
                return new WmsResponse(WmsResponse.STATUS_ERROR, WmsErrorCode.PARAM_IS_NULL, "lineSeq");
            }
        }
        return new WmsResponse(WmsResponse.STATUS_SUCCESS, null, null);
    }

}
