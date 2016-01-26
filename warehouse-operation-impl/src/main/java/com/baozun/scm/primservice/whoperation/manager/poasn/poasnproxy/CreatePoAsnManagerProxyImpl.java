package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoCheckManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckPoCode;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

/**
 * 创建PoAsn单
 * 
 * @author bin.hu
 * 
 */
@Service("createPoAsnManagerProxy")
public class CreatePoAsnManagerProxyImpl implements CreatePoAsnManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(CreatePoAsnManagerProxy.class);

    @Autowired
    private PoManager poManager;

    @Autowired
    private PoCheckManager poCheckManager;

    /**
     * 创建PO单据
     */
    @Override
    public ResponseMsg createPo(WhPoCommand po) {
        log.info("CreatePo start =======================");
        // 验证数据完整性
        ResponseMsg rm = checkPoData(po);
        if (rm.getResponseStatus() != ResponseMsg.STATUS_SUCCESS) {
            log.warn("CreatePo warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        try {
            // 创建PO单数据
            WhPo whPo = copyPropertiesPo(po);
            List<WhPoLine> whPoLines = copyPropertiesPoLine(po);
            // 判断OU_ID
            /**
             * if(ou_id == null){
             * if(存在){
             * 查询对应基础库中PO单po_code+store_id是否存在
             * 存在ERROR 提示PO_CODE已经存在
             * 不存在直接插入PO单
             * }else{
             * 插入t_wh_check_pocode表
             * }
             * }
             * if(ou_id !=null)
             * 拆数据源操作
             * 先查询t_wh_check_pocode
             * 存在
             * 查询对应基础库中PO单po_code+store_id是否存在
             * 存在ERROR 提示PO_CODE已经存在
             * 不存在的话直接插入PO单
             * 2个事务 
             */
            // 查询t_wh_check_pocode
            // 有:查询对应
            // rm = this.insertPoWithCheck(whPo, whPoLines, rm);
            rm = poManager.createPoAndLine(whPo, whPoLines, rm);
        } catch (Exception e) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            log.error("printService error poCode: " + po.getPoCode());
            log.error("" + e);
            return rm;
        }
        log.info("CreatePo end =======================");
        return rm;
    }


    /**
     * 封装创建PO单数据
     * 
     * @param po
     * @return
     */
    public WhPo copyPropertiesPo(WhPoCommand po) {
        WhPo whPo = new WhPo();
        BeanUtils.copyProperties(po, whPo);
        // 相关单据号 调用HUB编码生成器获得
        whPo.setExtCode(String.valueOf(System.currentTimeMillis()));
        // 采购时间为空默认为当前时间
        if (null == po.getPoDate()) {
            whPo.setPoDate(new Date());
        }
        whPo.setCreateTime(new Date());
        whPo.setCreatedId(po.getUserId());
        whPo.setLastModifyTime(new Date());
        whPo.setModifiedId(po.getUserId());
        return whPo;
    }

    /**
     * 封装创建POLINE数据
     * 
     * @param po
     * @return
     */
    public List<WhPoLine> copyPropertiesPoLine(WhPoCommand po) {
        List<WhPoLine> whPoLine = new ArrayList<WhPoLine>();
        if (null != po.getPoLineList()) {
            // 有line信息保存
            log.debug("po.getPoLineList().size(): " + po.getPoLineList().size());
            for (int i = 0; i < po.getPoLineList().size(); i++) {
                WhPoLineCommand polineCommand = po.getPoLineList().get(i);
                WhPoLine poline = new WhPoLine();
                BeanUtils.copyProperties(polineCommand, poline);
                poline.setOuId(po.getOuId());
                if (null == poline.getLinenum()) {
                    // 行号为空的话默认1开始递增
                    poline.setLinenum(i++);
                }
                poline.setCreateTime(new Date());
                poline.setCreatedId(po.getUserId());
                poline.setLastModifyTime(new Date());
                poline.setModifiedId(po.getUserId());
                whPoLine.add(poline);
            }
        }
        return whPoLine;
    }

    /**
     * 创建ASN单据
     */
    @Override
    public ResponseMsg createAsn(WhAsnCommand asn) {
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

    /**
     * 检验是否可以插入t_wh_po表
     */
    private ResponseMsg insertPoWithCheck(WhPo whPo, List<WhPoLine> whPoLines, ResponseMsg rm) {
        CheckPoCode checkPoCode = new CheckPoCode();
        if (!StringUtil.isEmpty(whPo.getPoCode())) {
            checkPoCode.setPoCode(whPo.getPoCode());
        }
        checkPoCode.setOuId(whPo.getOuId());
        checkPoCode.setStoreId(whPo.getStoreId());
        Long ouId = whPo.getOuId();
        PoCheckCommand poCheckCommand = new PoCheckCommand();
        poCheckCommand.setRm(rm);
        poCheckCommand.setWhPo(whPo);
        poCheckCommand.setWhPoLines(whPoLines);
        poCheckCommand.setCheckPoCode(checkPoCode);


        if (null == ouId) {
            poCheckManager.insertPoWithCheckWithoutOuId(poCheckCommand);
        } else {
            ResponseMsg responseMsg = poCheckManager.insertPoWithCheckAndOuId(checkPoCode);
            if ("no".equals(responseMsg.getMsg())) {
                /* 在check表中不存在po单 */
                poManager.createPoAndLineToShare(whPo, whPoLines, rm);
            } else {
                // poManager.insertPoWithCheckAndOuId(whPo.getPoCode(), whPo.getStoreId(),
                // whPo.getOuId());

            }
        }
        return null;
    }


}
