package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.poasn.AsnCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnCheckManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoAsnOuManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoCheckManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckAsnCode;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckPoCode;
import com.baozun.scm.primservice.whoperation.model.poasn.PoAsnOu;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
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
    @Autowired
    private AsnCheckManager asnCheckManager;
    @Autowired
    private AsnManager asnManager;
    @Autowired
    private PoAsnOuManager poAsnOuManager;

    /**
     * 创建PO单据
     */
    @Override
    public ResponseMsg createPo(WhPoCommand po) {
        log.info("CreatePo start =======================");
        // 验证数据完整性
        ResponseMsg rm = checkPoParameter(po);
        if (rm.getResponseStatus() != ResponseMsg.STATUS_SUCCESS) {
            log.warn("CreatePo warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        try {
            // 创建PO单数据
            WhPo whPo = copyPropertiesPo(po);
            // 相关单据号 调用HUB编码生成器获得
            String poCode = codeManager.generateCode(Constants.WMS, Constants.WHPO_MODEL_URL, null, null, null);
            if (StringUtil.isEmpty(poCode)) {
                log.warn("CreatePo warn poCode generateCode is null");
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                rm.setMsg(ErrorCodes.GET_GENERATECODE_NULL + "");
                log.warn("CreatePo warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
                return rm;
            }
            whPo.setPoCode(poCode);
            List<WhPoLine> whPoLines = copyPropertiesPoLine(po);
            // 判断OU_ID
            // 查询t_wh_check_pocode
            // 有:查询对应
            rm = this.insertPoWithCheck(whPo, whPoLines, rm);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            log.error("CreatePo error poCode: " + po.getExtCode());
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
                // if (null == poline.getLinenum()) {
                // // 行号为空的话默认1开始递增
                // poline.setLinenum(i++);
                // }
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
        log.info("CreateAsn start =======================");
        // 验证数据完整性
        ResponseMsg rm = checkAsnParameter(asn);
        if (rm.getResponseStatus() != ResponseMsg.STATUS_SUCCESS) {
            log.warn("CreateAsn warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        try {
            // 创建ASN单数据
            WhAsn whAsn = copyPropertiesAsn(asn);
            // WMS单据号 调用HUB编码生成器获得
            String asnCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_INNER, null, null);
            if (StringUtil.isEmpty(asnCode)) {
                log.warn("CreateAsn warn asnCode generateCode is null");
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                rm.setMsg(ErrorCodes.GET_GENERATECODE_NULL + "");
                log.warn("CreateAsn warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
                return rm;
            }
            whAsn.setAsnCode(asnCode);
            rm = this.insertAsnWithCheck(whAsn, asn.getAsnLineList(), rm);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            log.error("CreateAsn error asnCode: " + asn.getAsnExtCode());
            log.error("" + e);
            return rm;
        }
        log.info("CreateAsn end =======================");
        return rm;
    }

    /**
     * 封装ASN单信息
     * 
     * @param asn
     * @return
     */
    public WhAsn copyPropertiesAsn(WhAsnCommand asn) {
        WhAsn whAsn = new WhAsn();
        BeanUtils.copyProperties(asn, whAsn);
        // 采购时间为空默认为当前时间
        if (null == asn.getPoDate()) {
            whAsn.setPoDate(new Date());
        }
        whAsn.setCreatedId(asn.getUserId());
        whAsn.setModifiedId(asn.getUserId());
        return whAsn;
    }

    /**
     * 批量创建ASN&ASNLINE数据 一键批量创建
     */
    @Override
    public ResponseMsg createAsnBatch(WhAsnCommand asn) {
        log.info("CreateAsnBatch start =======================");
        ResponseMsg rm = new ResponseMsg();
        CheckAsnCode checkAsnCode = new CheckAsnCode();
        try {
            // WMS单据号 调用HUB编码生成器获得
            String asnCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_INNER, null, null);
            if (StringUtil.isEmpty(asnCode)) {
                log.warn("CreateAsnBatch warn asnCode generateCode is null");
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                rm.setMsg(ErrorCodes.GET_GENERATECODE_NULL + "");
                log.warn("CreateAsnBatch warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
                return rm;
            }
            asn.setAsnCode(asnCode);
            // 相关单据号 调用HUB编码生成器获得
            String asnExtCode = null;
            boolean isSuccess = false;
            // 验证asnextcode是否存在 最多调用接口5次
            for (int i = 0; i <= 5; i++) {
                if (true == isSuccess) {
                    break;
                }
                asnExtCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_EXT, null, null);
                if (StringUtil.isEmpty(asnExtCode)) {
                    log.warn("CreateAsnBatch warn asnExtCode generateCode is null");
                    rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                    rm.setMsg(ErrorCodes.GET_GENERATECODE_NULL + "");
                    log.warn("CreateAsnBatch warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
                    return rm;
                }
                asn.setAsnExtCode(asnExtCode);
                checkAsnCode.setAsnExtCode(asnExtCode);
                checkAsnCode.setOuId(asn.getOuId());
                checkAsnCode.setStoreId(asn.getStoreId());
                List<CheckAsnCode> checkAsnCodeList = asnCheckManager.findCheckAsnCodeListByParam(checkAsnCode);
                // 如果没有 直接结束
                if (checkAsnCodeList.size() == 0) {
                    isSuccess = true;
                }
            }
            if (!isSuccess) {
                // 如果5次获取都失败了 直接返回失败
                log.warn("CreateAsnBatch warn asnExtCode generateCode CheckAsnCode is not null");
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                rm.setMsg(ErrorCodes.GET_GENERATECODE_NULL + "");
                log.warn("CreateAsnBatch warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
                return rm;
            }
            rm = checkAsnParameter(asn);
            // 验证数据完整性
            if (rm.getResponseStatus() != ResponseMsg.STATUS_SUCCESS) {
                log.warn("CreatePo warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
                return rm;
            }
            // 插入checkAsnCode表
            checkAsnCode.setAsnCode(asnCode);
            checkAsnCode.setAsnExtCode(asn.getAsnExtCode());
            checkAsnCode.setOuId(asn.getOuId());
            checkAsnCode.setStoreId(asn.getStoreId());
            asnCheckManager.insertAsnWithCheckAndOuId(checkAsnCode);
            /**
             * 查询对应po_ou_id对应的po&poline数据
             */
            WhPo whPo = null;
            List<WhPoLine> poLineList = new ArrayList<WhPoLine>();
            PoAsnOu poAsnOu = new PoAsnOu();// 中间表数据
            poAsnOu.setOuId(asn.getOuId());
            poAsnOu.setPoId(asn.getPoId());
            poAsnOuManager.insertPoAsnOu(poAsnOu);
            if (null == asn.getPoOuId()) {
                // 如果对应的po_ou_id为空去基础库查询
                whPo = poManager.findWhAsnByIdToInfo(asn.getPoId(), asn.getPoOuId());
                // 删除UUID不为空的数据
                poLineManager.deletePoLineByUuidNotNullToInfo(asn.getPoId(), asn.getPoOuId());
                poLineList = poLineManager.findWhPoLineListByPoIdToInfo(asn.getPoId(), asn.getPoOuId());
            } else {
                // 如果对应的po_ou_id不为空 去对应库查询
                whPo = poManager.findWhAsnByIdToShard(asn.getPoId(), asn.getPoOuId());
                // 删除UUID不为空的数据
                poLineManager.deletePoLineByUuidNotNullToShard(asn.getPoId(), asn.getPoOuId());
                poLineList = poLineManager.findWhPoLineListByPoIdToShard(asn.getPoId(), asn.getPoOuId());
            }
            // 创建ASN&ASNLINE信息
            rm = asnManager.createAsnBatch(asn, whPo, poLineList, rm);
            if (null == asn.getPoOuId()) {
                // 如果对应po单没有指定仓库 修改PO&POLINE状态及可用数量
                rm = poManager.updatePoStatusByAsnBatch(asn, whPo, poLineList, rm);
            }
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            log.error("CreateAsnBatch error poid: " + asn.getPoId() + " ouid: " + asn.getOuId());
            log.error("" + e);
            return rm;
        }
        log.info("CreateAsnBatch end =======================");
        return rm;
    }

    /**
     * 验证po单数据是否完整
     * 
     * @param po
     * @return
     */
    private static ResponseMsg checkPoParameter(WhPoCommand po) {
        ResponseMsg response = new ResponseMsg();
        response.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        if (null == po) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("po is null");
            return response;
        }
        if (StringUtil.isEmpty(po.getExtCode())) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("extCode is null");
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
     * 验证asn单信息
     * 
     * @param asn
     * @return
     */
    private static ResponseMsg checkAsnParameter(WhAsnCommand asn) {
        ResponseMsg response = new ResponseMsg();
        response.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        if (null == asn) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("asn is null");
            return response;
        }
        if (StringUtil.isEmpty(asn.getAsnExtCode())) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("asnExtCode is null");
            return response;
        }
        if (null == asn.getCustomerId()) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("CustomerId is null");
            return response;
        }
        if (null == asn.getStoreId()) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("StoreId is null");
            return response;
        }
        // 创建ASN单 OUID为必须值
        if (null == asn.getOuId()) {
            response.setResponseStatus(ResponseMsg.DATA_ERROR);
            response.setMsg("OuId is null");
            return response;
        }
        return response;
    }

    /**
     * 检验是否可以插入t_wh_po表
     */
    private ResponseMsg insertPoWithCheck(WhPo whPo, List<WhPoLine> whPoLines, ResponseMsg rm) {
        log.info("InsertPoWithCheck start =======================");
        /**
         * 流程: 1.封装poCheckCommand对象,包含了WhPo,List<WhPoLine>,ResponseMsg,CheckPoCode
         * 2.没有传入ouId,查找中间表t_wh_check_pocode是否有此PO单,在同一事务中执行以下两步:
         * function==>poCheckManager.insertPoWithCheckWithoutOuId(); i)
         * 如果有则去基础信息表查找此PO单。有PO则抛出异常,没有PO则添加一条数据. ii) 如果没有则在t_wh_check_pocode添加一条数据,并在PO表中添加一条数据.
         * 3.有传入ouId,查找中间表t_wh_check_pocode是否有此PO单,在两个事务中分别执行以下两步:
         * function==>poManager.createPoAndLineToShare(); i) 如果有则去对应的拆库表查找此PO单。有PO则抛出异常,没有PO则添加一条数据.
         * function==>poManager.insertPoWithOuId(); ii) 如果没有则在t_wh_check_pocode添加一条数据,并在PO表中添加一条数据.
         */
        CheckPoCode checkPoCode = new CheckPoCode();
        // poCode为编码服务器生成 extCode为外围服务器传入或WMS创建PO单时填写
        checkPoCode.setExtCode(whPo.getExtCode());
        checkPoCode.setPoCode(whPo.getPoCode());
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
        WhPoLine wpl = null;
        // 通过传入的值预先查找该PO单下是否存在对应UUID的商品数据
        log.debug("CreatePoLineSingle findPoLineByAddPoLineParam poid: " + line.getPoId() + " ou_id: " + line.getOuId() + " uuid: " + line.getUuid());
        if (null == line.getOuId()) {
            // ouid is null to info mycat 需要带uuid
            wpl = poLineManager.findPoLineByAddPoLineParamToInfo(line, false);
        } else {
            // ouid is not null to share mycat 需要带uuid
            wpl = poLineManager.findPoLineByAddPoLineParamToShare(line, false);
        }
        if (null == wpl) {
            // 根据POLINE信息查询POLINE正式数据
            if (null == line.getOuId()) {
                // ouid is null to info mycat 不需要带uuid
                wpl = poLineManager.findPoLineByAddPoLineParamToInfo(line, true);
            } else {
                // ouid is not null to share mycat 不需要带uuid
                wpl = poLineManager.findPoLineByAddPoLineParamToShare(line, true);
            }
            if (null != wpl) {
                // 存在正式数据 需要在新数据的polineid赋值 等保存poline信息后合并信息用
                line.setPoLineId(wpl.getId());
            }
            // 如果不存在插入一条数据
            line.setStatus(PoAsnStatus.POLINE_NEW);
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
        } else {
            // 如果数据存在 合并数据
            wpl.setQtyPlanned(wpl.getQtyPlanned() + line.getQtyPlanned());// 计划数量
            wpl.setAvailableQty(wpl.getAvailableQty() + line.getQtyPlanned());// 可用数量=原可用数量+新计划数量
            // wpl.setLastModifyTime(new Date());
            wpl.setModifiedId(line.getModifiedId());
            try {
                if (null == line.getOuId()) {
                    // 没有ou_id更新基础表数据
                    poLineManager.updatePoLineSingleToInfo(wpl);
                } else {
                    // 有ou_id更新拆库表数据
                    poLineManager.updatePoLineSingleToShare(wpl);
                }
            } catch (Exception e) {
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                log.error("CreatePoLineSingle Error PoId: " + whPoLine.getPoId());
                log.error(e + "");
                return rm;
            }
        }
        log.info("CreatePoLineSingle end =======================");
        return rm;
    }


    /**
     * 批量保存POLINE信息
     */
    @Override
    public ResponseMsg createPoLineBatch(WhPoLineCommand whPoLine) {
        log.info("CreatePoLineBatch start =======================");
        ResponseMsg rm = new ResponseMsg();
        try {
            if (null == whPoLine.getOuId()) {
                // 没有ou_id更新基础表数据
                poLineManager.createPoLineBatchToInfo(whPoLine);
            } else {
                // 有ou_id更新拆库表数据
                poLineManager.createPoLineBatchToShare(whPoLine);
            }
        } catch (Exception e) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            log.error("CreatePoLineBatch Error PoId: " + whPoLine.getPoId());
            log.error(e + "");
            return rm;
        }
        log.info("CreatePoLineBatch end =======================");
        return rm;
    }

    /**
     * 检验是否可以插入t_wh_asn表
     */
    private ResponseMsg insertAsnWithCheck(WhAsn whAsn, List<WhAsnLineCommand> asnLineList, ResponseMsg rm) {
        log.info("InsertAsnWithCheck start =======================");
        /**
         * 流程: 1.封装asnCheckCommand对象,包含了WhAsn,List<WhAsnLine>,ResponseMsg,CheckAsnCode
         * 2.没有传入ouId,查找中间表t_wh_check_asncode是否有此ASN单,在同一事务中执行以下两步:
         * function==>asnCheckManager.insertAsnWithCheckWithoutOuId(); i)
         * 如果有则去基础信息表查找此ASN单。有ASN则抛出异常,没有ASN则添加一条数据. ii)
         * 如果没有则在t_wh_check_asncode添加一条数据,并在ASN表中添加一条数据.
         * 3.有传入ouId,查找中间表t_wh_check_asncode是否有此ASN单,在两个事务中分别执行以下两步:
         * function==>asnManager.createAsnAndLineToShare(); i)
         * 如果有则去对应的拆库表查找此ASN单。有ASN则抛出异常,没有ASN则添加一条数据. function==>asnManager.insertAsnWithOuId(); ii)
         * 如果没有则在t_wh_check_asncode添加一条数据,并在ASN表中添加一条数据.
         */
        CheckAsnCode checkAsnCode = new CheckAsnCode();
        // asnCode为编码服务器生成 extCode为外围服务器传入或WMS创建ASN单时填写
        checkAsnCode.setAsnExtCode(whAsn.getAsnExtCode());
        checkAsnCode.setAsnCode(whAsn.getAsnCode());
        checkAsnCode.setOuId(whAsn.getOuId());
        checkAsnCode.setStoreId(whAsn.getStoreId());
        // Long ouId = whAsn.getOuId();

        /* 封装asnCheckCommand对象 */
        AsnCheckCommand asnCheckCommand = new AsnCheckCommand();
        asnCheckCommand.setRm(rm);
        asnCheckCommand.setWhAsn(whAsn);
        asnCheckCommand.setCheckAsnCode(checkAsnCode);
        // if (null == ouId) {
        // /* asn单不带ouId */
        // /* 查找并插入asn数据 */
            // rm = asnCheckManager.insertAsnWithCheckWithoutOuId(asnCheckCommand);
        // } else {
        /* asn单带ouId */
        /* 查找check表中是否有数据 */
        boolean flag = asnCheckManager.insertAsnWithCheckAndOuId(checkAsnCode);
        WhPo whPo = null;
        Map<Long, WhPoLine> poLineMap = new HashMap<Long, WhPoLine>();
        List<WhPoLine> poLineList = new ArrayList<WhPoLine>();
        // 封装数据
        PoAsnOu poAsnOu = new PoAsnOu();// 中间表数据
        poAsnOu.setOuId(whAsn.getOuId());
        poAsnOu.setPoId(whAsn.getPoId());
        poAsnOuManager.insertPoAsnOu(poAsnOu);
        if (null == whAsn.getPoOuId()) {
            // 如果对应的po_ou_id为空去基础库查询
            whPo = poManager.findWhAsnByIdToInfo(whAsn.getPoId(), whAsn.getPoOuId());
            poLineList = poLineManager.findWhPoLineListByPoIdToInfo(whAsn.getPoId(), whAsn.getPoOuId());
        } else {
            // 如果对应的po_ou_id不为空 去对应库查询
            whPo = poManager.findWhAsnByIdToShard(whAsn.getPoId(), whAsn.getPoOuId());
            poLineList = poLineManager.findWhPoLineListByPoIdToShard(whAsn.getPoId(), whAsn.getPoOuId());
        }
        for (WhPoLine whPoLine : poLineList) {
            // 查询到的lineList放入map等到后续处理
            poLineMap.put(whPoLine.getId(), whPoLine);
        }
        if (!flag) {
            /* 在check表中不存在asn单 */
            rm = asnManager.createAsnAndLineToShare(whAsn, asnLineList, whPo, poLineMap, rm);
        } else {
            /* 在check表中存在此asn单,则去asn表中查找是否有这单. 如果有就抛出异常,没有就插入 */
            rm = asnManager.insertAsnWithOuId(whAsn, asnLineList, whPo, poLineMap, rm);
            /* 如果抛出异常,此处会有补偿机制 */
        }
        if (null == whAsn.getPoOuId()) {
            // 如果对应po单没有指定仓库 修改PO&POLINE状态及可用数量
            rm = poManager.updatePoStatusByAsn(whAsn, asnLineList, whPo, poLineMap, rm);
        }
        // }
        log.info("InsertAsnWithCheck end =======================");
        return rm;
    }

}
