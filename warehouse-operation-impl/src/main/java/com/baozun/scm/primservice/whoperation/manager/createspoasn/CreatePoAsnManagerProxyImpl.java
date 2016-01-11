package com.baozun.scm.primservice.whoperation.manager.createspoasn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Service("createPoAsnManagerProxy")
public class CreatePoAsnManagerProxyImpl implements CreatePoAsnManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(CreatePoAsnManagerProxy.class);

    /**
     * 创建PO单据
     */
    @Override
    public ResponseMsg CreatePo(WhPoCommand po) {
        log.info("CreatePo start =======================");
        // 验证数据完整性
        ResponseMsg rm = checkPoData(po);
        if (rm.getResponseStatus() != ResponseMsg.STATUS_SUCCESS) {
            log.warn("CreatePo warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        
        log.info("CreatePo end =======================");
        return null;
    }

    /**
     * 创建ASN单据
     */
    @Override
    public ResponseMsg CreateAsn(WhAsnCommand asn) {
        return null;
    }

    /**
     * 验证po单数据是否完整
     * 
     * @param po
     * @return
     */
    private static ResponseMsg checkPoData(WhPoCommand po) {
        ResponseMsg response = new ResponseMsg();
        response.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        if (null == po) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("po is null");
            return response;
        }
        if (StringUtil.isEmpty(po.getPoCode())) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("PoCode is null");
            return response;
        }
        if (null == po.getPoType()) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("PoType is null");
            return response;
        }
        if (null == po.getStatus()) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("Status is null");
            return response;
        }
        // 验证是否是WMS内部创建还是上位系统同步的PO单
        if (!po.getIsWms()) {
            // false为 上位系统同步的PO单 需要验证poline的数据
            if (po.getPoLineList().size() == 0) {
                response.setResponseStatus(ResponseMsg.DATA_ERROR);
                response.setMsg("PoLineList is null");
                return response;
            }
        }
        return response;
    }


}
