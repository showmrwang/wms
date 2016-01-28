package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoCheckManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
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
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private PoLineManager poLineManager;

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
             * if(ou_id == null){ if(存在){ 查询对应基础库中PO单po_code+store_id是否存在 存在ERROR 提示PO_CODE已经存在
             * 不存在直接插入PO单 }else{ 插入t_wh_check_pocode表 } } if(ou_id !=null) 拆数据源操作
             * 先查询t_wh_check_pocode 存在 查询对应基础库中PO单po_code+store_id是否存在 存在ERROR 提示PO_CODE已经存在
             * 不存在的话直接插入PO单 2个事务
             */
            // 查询t_wh_check_pocode
            // 有:查询对应
            rm = this.insertPoWithCheck(whPo, whPoLines, rm);
            // rm = poManager.createPoAndLine(whPo, whPoLines, rm);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
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
        String extCode = codeManager.generateCode(Constants.WMS, Constants.MODEL_URL, null, null, null);
        if (StringUtil.isEmpty(extCode)) {
            log.warn("CreatePo warn extCode generateCode is null");
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        whPo.setExtCode(extCode);
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
            log.debug("CopyPropertiesPoLine po.getPoLineList().size(): " + po.getPoLineList().size());
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
        log.info("InsertPoWithCheck start =======================");
        /**
         * 流程:
         * 1.封装poCheckCommand对象,包含了WhPo,List<WhPoLine>,ResponseMsg,CheckPoCode
         * 2.没有传入ouId,查找中间表t_wh_check_pocode是否有此PO单,在同一事务中执行以下两步:
         * function==>poCheckManager.insertPoWithCheckWithoutOuId();
         * i)  如果有则去基础信息表查找此PO单。有PO则抛出异常,没有PO则添加一条数据.
         * ii) 如果没有则在t_wh_check_pocode添加一条数据,并在PO表中添加一条数据.
         * 3.有传入ouId,查找中间表t_wh_check_pocode是否有此PO单,在两个事务中分别执行以下两步:
         * function==>poManager.createPoAndLineToShare();
         * i)  如果有则去对应的拆库表查找此PO单。有PO则抛出异常,没有PO则添加一条数据.
         * function==>poManager.insertPoWithOuId();
         * ii) 如果没有则在t_wh_check_pocode添加一条数据,并在PO表中添加一条数据.
         */
        CheckPoCode checkPoCode = new CheckPoCode();
        if (!StringUtil.isEmpty(whPo.getPoCode())) {
            checkPoCode.setPoCode(whPo.getPoCode());
        }
        checkPoCode.setOuId(whPo.getOuId());
        checkPoCode.setStoreId(whPo.getStoreId());
        Long ouId = whPo.getOuId();

        /* 封装poCheckCommand对象 */
        PoCheckCommand poCheckCommand = new PoCheckCommand();
        poCheckCommand.setRm(rm);
        poCheckCommand.setWhPo(whPo);
        poCheckCommand.setWhPoLines(whPoLines);
        poCheckCommand.setCheckPoCode(checkPoCode);
        if (null == ouId) {
            /* po单不带ouId */
            /* 查找并插入po数据 */
            rm = poCheckManager.insertPoWithCheckWithoutOuId(poCheckCommand);
        } else {
            /* po单带ouId */
            /* 查找check表中是否有数据 */
            boolean flag = poCheckManager.insertPoWithCheckAndOuId(checkPoCode);
            if (!flag) {
                /* 在check表中不存在po单 */
                rm = poManager.createPoAndLineToShare(whPo, whPoLines, rm);
            } else {
                /* 在check表中存在此po单,则去po表中查找是否有这单. 如果有就抛出异常,没有就插入 */
                rm = poManager.insertPoWithOuId(poCheckCommand);
                // TODO
                /* 如果抛出异常,此处会有补偿机制 */
            }
        }
        log.info("InsertPoWithCheck end =======================");
        return rm;
    }

    /**
     * 创建POLine明细信息 业务缓存在表数据
     */
    @Override
    public ResponseMsg createPoLineSingle(WhPoLineCommand whPoLine) {
        log.info("CreatePoLineSingle start =======================");
        ResponseMsg rm = new ResponseMsg();
        WhPoLine line = new WhPoLine();
        BeanUtils.copyProperties(whPoLine, line);
        // 如果行号为空给个行号
        if (null == line.getLinenum()) {
            line.setLinenum(1);
        }
        line.setStatus(PoAsnStatus.POLINE_NOT_RCVD);
        line.setCreateTime(new Date());
        line.setLastModifyTime(new Date());
        try {
            if (null == line.getOuId()) {
                // 没有ou_id插入基础表数据
                poLineManager.createPoLineSingleToInfo(line);
            } else {
                // 有ou_id插入拆库表数据
                poLineManager.createPoLineSingleToShare(line);
            }
        } catch (Exception e) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            log.error("CreatePoLineSingle Error PoId: " + whPoLine.getPoId());
            log.error(e + "");
            return rm;
        }
        log.info("CreatePoLineSingle end =======================");
        return rm;
    }


}
