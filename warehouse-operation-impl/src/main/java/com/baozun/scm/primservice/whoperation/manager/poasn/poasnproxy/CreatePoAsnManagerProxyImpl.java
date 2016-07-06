package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.baseservice.sac.manager.PkManager;
import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.AsnCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnCheckManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoAsnOuManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckAsnCode;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckPoCode;
import com.baozun.scm.primservice.whoperation.model.poasn.PoAsnOu;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
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
public class CreatePoAsnManagerProxyImpl extends BaseManagerImpl implements CreatePoAsnManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(CreatePoAsnManagerProxy.class);


    @Autowired
    private PoManager poManager;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private PoLineManager poLineManager;
    @Autowired
    private AsnCheckManager asnCheckManager;
    @Autowired
    private AsnManager asnManager;
    @Autowired
    private AsnLineManager asnLineManager;
    @Autowired
    private BiPoManager biPoManager;
    @Autowired
    private PkManager pkManager;
    @Autowired
    private BiPoLineManager biPoLineManager;
    @Autowired
    private PoAsnOuManager poAsnOuManager;

    /**
     * 封装创建PO单数据
     * 
     * @param po
     * @return
     */
    public WhPo copyPropertiesPo(WhPoCommand po) {
        log.info(this.getClass().getSimpleName() + ".copyPropertiesPo method begin!");
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
        if (null == whPo.getQtyPlanned()) {
            whPo.setQtyPlanned(Constants.DEFAULT_DOUBLE);
        }
        if (null == whPo.getQtyRcvd()) {
            whPo.setQtyRcvd(Constants.DEFAULT_DOUBLE);
        }
        if (null == whPo.getCtnPlanned()) {
            whPo.setCtnPlanned(Constants.DEFAULT_INTEGER);
        }
        if (null == whPo.getCtnRcvd()) {
            whPo.setCtnRcvd(Constants.DEFAULT_INTEGER);
        }
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".copyPropertiesPo method returns:{}", whPo);
        }
        return whPo;
    }

    /**
     * 封装创建POLINE数据
     * 
     * @param po
     * @return
     */
    public List<WhPoLine> copyPropertiesPoLine(WhPoCommand po) {
        log.info(this.getClass().getSimpleName() + ".copyPropertiesPoLine method begin!");
        List<WhPoLine> whPoLine = new ArrayList<WhPoLine>();
        if (null != po.getPoLineList()) {
            // 有line信息保存
            log.debug("CopyPropertiesPoLine po.getPoLineList().size(): " + po.getPoLineList().size());
            for (int i = 0; i < po.getPoLineList().size(); i++) {
                WhPoLineCommand polineCommand = po.getPoLineList().get(i);
                WhPoLine poline = new WhPoLine();
                BeanUtils.copyProperties(polineCommand, poline);
                poline.setOuId(po.getOuId());
                poline.setCreateTime(new Date());
                poline.setCreatedId(po.getUserId());
                poline.setLastModifyTime(new Date());
                poline.setModifiedId(po.getUserId());
                if (null == poline.getQtyPlanned()) {
                    poline.setQtyPlanned(Constants.DEFAULT_DOUBLE);
                }
                if (null == poline.getAvailableQty()) {
                    poline.setAvailableQty(Constants.DEFAULT_DOUBLE);
                }
                if (null == poline.getCtnPlanned()) {
                    poline.setCtnPlanned(Constants.DEFAULT_INTEGER);
                }
                if (null == poline.getCtnRcvd()) {
                    poline.setCtnRcvd(Constants.DEFAULT_INTEGER);
                }
                if (null == poline.getQtyRcvd()) {
                    poline.setQtyRcvd(Constants.DEFAULT_DOUBLE);
                }
                whPoLine.add(poline);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".copyPropertiesPoLine method returns:{}", whPoLine);
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
            rm = insertAsnWithCheck(whAsn, asn.getAsnLineList(), rm);
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
        log.info(this.getClass().getSimpleName() + ".copyPropertiesAsn method begin!");
        WhAsn whAsn = new WhAsn();
        BeanUtils.copyProperties(asn, whAsn);
        // 采购时间为空默认为当前时间
        if (null == asn.getPoDate()) {
            whAsn.setPoDate(new Date());
        }
        whAsn.setCreatedId(asn.getUserId());
        whAsn.setModifiedId(asn.getUserId());
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".copyPropertiesAsn method returns:{}", whAsn);
        }
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
            // 先期校验：polineList没有可用数量的时候，不能保存
            boolean flag = false;
            for (WhPoLine line : poLineList) {
                if (line.getAvailableQty() > 0) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                throw new BusinessException(ErrorCodes.CHECK_DATA_ERROR);
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
     * @deprecated 检验是否可以插入t_wh_po表
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
        /**
         * @mender yimin.lu 2016/4/27 以下逻辑做修正；修改后的逻辑： 1.生成主库备份 2.如果有仓库，则在仓库中再插入一份
         */
        // rm = this.biPoManager.createPoAndLineToInfo(poCheckCommand);
        if (ResponseMsg.STATUS_SUCCESS == rm.getResponseStatus()) {
            if (ouId != null) {
                rm = poManager.createPoAndLineToShare(whPo, whPoLines, rm);
            }
        }

        // if (null == ouId) {
        // /* po单不带ouId */
        // /* 查找并插入po数据 */
        // rm = poCheckManager.insertPoWithCheckWithoutOuId(poCheckCommand);
        // } else {
        // /* po单带ouId */
        // /* 查找check表中是否有数据 */
        // boolean flag = poCheckManager.insertPoWithCheckAndOuId(checkPoCode);
        // if (!flag) {
        // /* 在check表中不存在po单 */
        // rm = poManager.createPoAndLineToShare(whPo, whPoLines, rm);
        // } else {
        // /* 在check表中存在此po单,则去po表中查找是否有这单. 如果有就抛出异常,没有就插入 */
        // rm = poManager.insertPoWithOuId(poCheckCommand);
        // /* 如果抛出异常,此处会有补偿机制 */
        // }
        // }
        log.info("InsertPoWithCheck end =======================");
        return rm;
    }


    @Override
    public ResponseMsg createPoLineSingleNew(BaseCommand whPoLine) {
        log.info("CreatePoLineSingle start =======================");
        ResponseMsg rm = new ResponseMsg();
        BiPoLine line = new BiPoLine();
        BeanUtils.copyProperties(whPoLine, line);
        BiPoLine wpl = biPoLineManager.findPoLineByAddPoLineParam(line, false);
        // 通过传入的值预先查找该PO单下是否存在对应UUID的商品数据
        if (log.isDebugEnabled()) {
            log.debug("CreatePoLineSingle findPoLineByAddPoLineParam poid: " + line.getPoId() + " uuid: " + line.getUuid());
        }
        if (null == wpl) {
            // 根据POLINE信息查询POLINE正式数据
            wpl = biPoLineManager.findPoLineByAddPoLineParam(line, true);
            if (null != wpl) {
                // 存在正式数据 需要在新数据的polineid赋值 等保存poline信息后合并信息用
                line.setPoLineId(wpl.getId());
            }
            // 如果不存在插入一条数据
            line.setStatus(PoAsnStatus.POLINE_NEW);
            line.setCreateTime(new Date());
            line.setLastModifyTime(new Date());
            if (null == line.getQtyPlanned()) {
                line.setQtyPlanned(Constants.DEFAULT_DOUBLE);
            }
            if (null == line.getAvailableQty()) {
                line.setAvailableQty(Constants.DEFAULT_DOUBLE);
            }
            if (null == line.getCtnPlanned()) {
                line.setCtnPlanned(Constants.DEFAULT_INTEGER);
            }
            if (null == line.getCtnRcvd()) {
                line.setCtnRcvd(Constants.DEFAULT_INTEGER);
            }
            if (null == line.getQtyRcvd()) {
                line.setQtyRcvd(Constants.DEFAULT_DOUBLE);
            }
            try {
                biPoLineManager.createPoLineSingle(line);
            } catch (Exception e) {
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                log.error("CreatePoLineSingle Error PoId: " + line.getPoId());
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
                biPoLineManager.updatePoLineSingle(wpl);
            } catch (Exception e) {
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                log.error("CreatePoLineSingle Error PoId: " + line.getPoId());
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
    public ResponseMsg createPoLineBatchNew(BaseCommand whPoLine) {
        log.info("CreatePoLineBatch start =======================");
        ResponseMsg rm = new ResponseMsg();
        try {
            BiPoLineCommand biPoLineCommand = new BiPoLineCommand();
            BeanUtils.copyProperties(whPoLine, biPoLineCommand);
            biPoLineManager.createPoLineBatchToInfo(biPoLineCommand);
            if (biPoLineCommand.getOuId() != null) {
                BiPo bipo = this.biPoManager.findBiPoById(biPoLineCommand.getPoId());
                biPoLineCommand.setPoCode(bipo.getPoCode());
                List<WhPoLine> infoPolineList = this.poLineManager.findInfoPoLineByPoCodeOuId(bipo.getPoCode(), biPoLineCommand.getOuId());
                poLineManager.createPoLineBatchToShareNew(biPoLineCommand, infoPolineList);
                // 没有ou_id更新基础表数据
                // poLineManager.createPoLineBatchToShare(whPolineCommand );
            }
        } catch (Exception e) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            // log.error("CreatePoLineBatch Error PoId: " + biPolineCommand.getPoId());
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

    /**
     * 获取到唯一的库位条码或补货条码 逻辑：一个仓库的库位条码和补货条码是唯一的
     * 
     * @author yimin.lu 2015/11/15
     * @param code
     * @param type
     * @param ouId
     * @param threshold
     * @return
     */
    private String getUniqueCode() {
        return this.getUniqueCode(null);
    }
    private String getUniqueCode(Integer threshold) {
        // 阙值 3次 如果同一个编码三次生成的条码在库位所在的仓库都有重复，那么则回滚
        threshold = threshold == null ? 0 : threshold;
        if (threshold >= 3) return null;
        String poCode = codeManager.generateCode(Constants.WMS, Constants.WHPO_MODEL_URL, null, null, null);
        BiPo biPo = new BiPo();
        biPo.setPoCode(poCode);
        List<BiPo> list = this.biPoManager.findListByParam(biPo);
        // 如果本次生成的条码在数据库中有数据的话，则再生成一次。阙值加1
        if (list != null && list.size() > 0) {
            return getUniqueCode(threshold + 1);
        }
        return poCode;
    }



    /**
     * 创建PO单据
     */
    @Override
    public ResponseMsg createPoNew(WhPoCommand po) {
        log.info("CreatePo start =======================");
        // @author yimin.lu
        // ①校验数据完整性
        // ②校验ext_code+storeId的唯一性
        // ③生成po_code[唯一性；三次生成都不唯一，抛出异常]
        // ④数据插入到数据库中
        // 4-1:当数据没有仓库的时候，则只需插入到INFO的BIPO中
        // 4-2：当数据有仓库的时候，需要将数据插入到INFO的BIPO中，并同步到INFO的WHPO中来
        String poCode = null;
        // 验证数据完整性
        ResponseMsg rm = checkPoParameter(po);
        if (rm.getResponseStatus() != ResponseMsg.STATUS_SUCCESS) {
            log.warn("CreatePo warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
            return rm;
        }
        Long ouId=po.getOuId();
        try {
            //校验ExtCode: ext_code与storeId 唯一性
            BiPo checkExtCodeBiPo = new BiPo();
            checkExtCodeBiPo.setStoreId(po.getStoreId());
            checkExtCodeBiPo.setExtCode(po.getExtCode());
            List<BiPo> checkExtCodeBiPoList = this.biPoManager.findListByParam(checkExtCodeBiPo);
            if (null == checkExtCodeBiPoList || checkExtCodeBiPoList.size() > 0) {
                log.warn("check extcode returns failure when createPo!");
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                rm.setMsg(" check extcode returns failure when createPo!");
                return rm;
            }

            // 相关单据号 调用HUB编码生成器获得
            poCode = getUniqueCode();
            if (StringUtil.isEmpty(poCode)) {
                log.warn("CreatePo warn poCode generateCode is null");
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                rm.setMsg(ErrorCodes.GET_GENERATECODE_NULL + "");
                log.warn("CreatePo warn ResponseStatus: " + rm.getResponseStatus() + " msg: " + rm.getMsg());
                return rm;
            }
            // 创建PO单数据
            WhPo whPo = copyPropertiesPo(po);
            whPo.setPoCode(poCode);
            List<WhPoLine> whPoLines = null != po.getPoLineList() ? copyPropertiesPoLine(po) : null;
            // 判断OU_ID
            // 查询t_wh_check_pocode
            // 有:查询对应
            /**
             * @mender yimin.lu 2016/4/27 以下逻辑做修正；修改后的逻辑： 1.生成主库备份 2.如果有仓库，则在仓库中再插入一份
             */
            biPoManager.createPoAndLineToInfo(whPo, whPoLines);
            if (ouId != null) {
                biPoManager.createPoAndLineToShared(whPo, whPoLines);
            }
        } catch (Exception e) {
            log.error("CreatePoAsnManagerProxyImpl.createPoNew error:[exception:{}]", e);
            if (e instanceof BusinessException) {
                rm.setMsg(((BusinessException) e).getErrorCode() + "");
            } else {
                rm.setMsg(ErrorCodes.SAVE_PO_FAILED + "");
            }
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            log.info("BiPoManager.createPoAndLineToInfo method end!");
            return rm;
        }
        log.info("CreatePo end =======================");
        rm.setMsg(poCode);
        return rm;
    }


    @Override
    public void createSubPoToInfo(BiPoCommand command) {
        //这边的逻辑如下：
        // 先删除对应仓库中有uuid且uuid不同的数据
        //1.如果拆分的仓库已有此po单的话
        // 1.1 PO单头信息不改 。
        //1.2.1 已有对应的明细，则记录明细行的polineId-》polineId和uuid
        //1.2.2 没有对应的明细，则生成新的明细行或者合并已有的明细行【uuid】
        //2.如果没有此PO单的话
        //2.1 生成新的PO单头信息
        //2.2.1 生成新的PO单明细
        // 3.最后的同步机制：由INFO->shard#TODO
        Long ouId = command.getOuId();
        String uuid=command.getUuid();
        Long userId = command.getUserId();
        BiPo bipo = this.biPoManager.findBiPoById(command.getId());
        if (null == bipo) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        // 查找非取消状态下的拆单
        WhPo po = this.poManager.findWhPoByPoCodeOuIdToInfo(bipo.getPoCode(), ouId);
        // 这边的逻辑：
        // 没有对应的PO的时候，则重新生成PO
        // 如果有对应的PO，那么查找这个PO有没有uuid；如果没有，则赋值uuid；如果有的话，看有没有对应的uuid的明细；如果有对应的uuid的明细，表明此单在一个仓库中同事被操作，则抛错；否则将此uuid赋予PO
        // 防止非正常关闭页面导致UUID不能及时更新
        if (null == po) {
            po = new WhPo();
            BeanUtils.copyProperties(bipo, po);
            po.setId(null);
            po.setOuId(command.getOuId());
            po.setUuid(uuid);
            po.setCreatedId(userId);
            po.setModifiedId(userId);
            po.setCreateTime(new Date());
            po.setLastModifyTime(new Date());
            po.setQtyPlanned(Constants.DEFAULT_DOUBLE);
            po.setQtyRcvd(Constants.DEFAULT_DOUBLE);
            po.setCtnPlanned(Constants.DEFAULT_INTEGER);
            po.setCtnRcvd(Constants.DEFAULT_INTEGER);
            po.setStatus(PoAsnStatus.POLINE_NEW);
        } else {
            if (StringUtils.hasText(po.getUuid()) && !uuid.equals(po.getUuid())) {
                this.poLineManager.deletePoLineByPoIdOuIdAndUuidNotNullNotEqual(po.getId(), po.getOuId(), uuid);
            }
            po.setUuid(uuid);
            po.setModifiedId(userId);
        }

        if (null == command.getPoLineList()) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        List<WhPoLine> whPoLineList = new ArrayList<WhPoLine>();
        for (BiPoLineCommand bipolineCommand : command.getPoLineList()) {
            if(null==bipolineCommand.getQtyPlanned()){
                throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
            }
            BiPoLine line = this.biPoLineManager.findBiPoLineById(bipolineCommand.getId());
            if(null==line){
                throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
            }
            if (line.getAvailableQty() < bipolineCommand.getQtyPlanned()) {
                throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
            }
            WhPoLine whpoline = this.poLineManager.findPoLineByPolineIdAndStatusListAndPoIdAndOuIdToInfo(line.getId(), Arrays.asList(new Integer[] {PoAsnStatus.POLINE_NEW, PoAsnStatus.POLINE_CREATE_ASN, PoAsnStatus.POLINE_RCVD}), po.getId(), ouId,uuid,true);
            if (null == whpoline) {
                whpoline = new WhPoLine();
                BeanUtils.copyProperties(line, whpoline);
                whpoline.setId(null);
                whpoline.setCreatedId(userId);
                whpoline.setOuId(ouId);
                whpoline.setCreateTime(new Date());
                whpoline.setModifiedId(userId);
                whpoline.setLastModifyTime(new Date());
                whpoline.setQtyPlanned(bipolineCommand.getQtyPlanned());
                whpoline.setAvailableQty(bipolineCommand.getQtyPlanned());
                whpoline.setQtyRcvd(Constants.DEFAULT_DOUBLE);
                whpoline.setCtnPlanned(Constants.DEFAULT_INTEGER);
                whpoline.setCtnRcvd(Constants.DEFAULT_INTEGER);
                whpoline.setPoLineId(line.getId());
                whpoline.setUuid(uuid);
                whpoline.setStatus(PoAsnStatus.POLINE_NEW);
            } else {
                whpoline.setQtyPlanned(whpoline.getQtyPlanned() + bipolineCommand.getQtyPlanned());
                whpoline.setAvailableQty(whpoline.getAvailableQty() + bipolineCommand.getQtyPlanned());
                whpoline.setModifiedId(userId);
            }
            whPoLineList.add(whpoline);
        }
        this.poManager.createSubPoWithLineToInfo(po, whPoLineList);
    }


    @Override
    public void revokeSubPoToInfo(WhPoCommand command) {
        // 逻辑：
        // 根据pocode,uuid查找对应的明细行；
        // 修改明细行的qtyPlanned,
        if (null == command) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        if (null == command.getPoLineList()) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        List<WhPoLine> lineList = new ArrayList<WhPoLine>();
        for (WhPoLineCommand lineCommand : command.getPoLineList()) {
            if (lineCommand.getQtyPlanned() < 0) {
                throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
            }
            WhPoLineCommand searchCommand = new WhPoLineCommand();
            searchCommand.setId(lineCommand.getId());
            searchCommand.setOuId(command.getOuId());
            WhPoLineCommand line = this.poLineManager.findPoLinebyIdToInfo(searchCommand);
            if (null == line) {
                throw new BusinessException(ErrorCodes.DATA_EXPRIE_ERROR);
            }
            WhPoLine poLine = new WhPoLine();
            BeanUtils.copyProperties(line, poLine);
            poLine.setModifiedId(command.getUserId());
            poLine.setQtyPlanned(lineCommand.getQtyPlanned());
            poLine.setAvailableQty(lineCommand.getQtyPlanned());

            lineList.add(poLine);
        }
        this.poManager.revokeSubPoToInfo(lineList);
    }


    @Override
    public void createSubPoToShard(WhPoCommand command) {
        // 逻辑：
        // 1.INFO.WHPO/WHPOLINE
        // 1.1删除可用数量为0的临时数据
        // 1.2合并明细；当已有明细为新建，已创建ASN，收货中，合并明细行
        // 1.2修改表头中数量
        // 2.INFO.BIPO/BIPOLINE
        // 2.1修改BIPOLINE对应明细行的数量
        // 3.SHARD.WHPO/WHPOLINE
        // 3.1新增或修改po单表头的数量
        // 3.2新增或修改明细
        // this.poManager.findWhPoByPoCodeOuIdUuid(Command.getPoCode,command.get)
        this.biPoManager.saveSubPoToInfo(command.getId(), command.getPoCode(), command.getOuId(), command.getUuid(), command.getUserId());
        WhPo infoPo = this.poManager.findWhPoByPoCodeOuIdToInfo(command.getPoCode(), command.getOuId());
        List<Integer> statusList = Arrays.asList(new Integer[] {PoAsnStatus.POLINE_NEW, PoAsnStatus.POLINE_CREATE_ASN, PoAsnStatus.POLINE_RCVD});
        List<WhPoLine> infoPoLineList = this.poLineManager.findWhPoLineListByPoIdOuIdStatusListToInfo(infoPo.getId(), command.getOuId(), statusList);
        this.poManager.saveSubPoToShard(command.getPoCode(), command.getOuId(), command.getUserId(), infoPo, infoPoLineList);

    }

    @Override
    public void closeSubPoToInfo(WhPoCommand command) {
        this.biPoManager.closeSubPoToInfo(command.getPoCode(), command.getOuId(), command.getId());
    }

    @Override
    public WhAsn createAsnWithUuid(WhPoCommand command) {
        // 这边的逻辑：
        // 当在WMS系统页面操作创建ASN的时候，先将数据保存到临时表
        // 限定：同一个仓库下；只允许一个人同时操作一个PO单；
        WhPo po = this.poManager.findWhPoByIdToShard(command.getId(), command.getOuId());
        if (null == po) {
            throw new BusinessException(ErrorCodes.PO_NULL);
        }
        // 1.校验ASNEXTCODE的唯一性：店铺的唯一性
        WhAsnCommand checkAsn = new WhAsnCommand();
        checkAsn.setAsnExtCode(command.getAsnExtCode());
        checkAsn.setStoreId(po.getStoreId());
        checkAsn.setOuId(command.getOuId());
        long checkAsnCount = this.asnManager.findListCountByParamsExt(checkAsn);
        if (checkAsnCount > Constants.DEFAULT_INTEGER) {
            throw new BusinessException(ErrorCodes.ASN_EXTCODE_EXISTS);
        }
        // 2.创建ASN以及明细
        List<WhAsnLine> lineList = new ArrayList<WhAsnLine>();
        if (null != command.getPoLineList() && command.getPoLineList().size() > 0) {
            for (WhPoLineCommand lineCommand : command.getPoLineList()) {
                WhPoLine poline = this.poLineManager.findWhPoLineByIdOuIdToShard(lineCommand.getId(), command.getOuId());
                if (poline.getAvailableQty() < lineCommand.getQtyPlanned()) {
                    throw new BusinessException(ErrorCodes.ASNLINE_QTYPLANNED_ERROR);
                }
                WhAsnLine line = new WhAsnLine();
                line.setOuId(command.getOuId());
                line.setPoLineId(poline.getId());
                line.setPoLinenum(poline.getLinenum());
                line.setSkuId(poline.getSkuId());
                line.setQtyPlanned(lineCommand.getQtyPlanned());
                line.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                line.setIsIqc(poline.getIsIqc());
                line.setMfgDate(poline.getMfgDate());
                line.setExpDate(poline.getMfgDate());
                line.setValidDate(poline.getValidDate());
                line.setBatchNo(poline.getBatchNo());
                line.setCountryOfOrigin(poline.getCountryOfOrigin());
                line.setInvStatus(poline.getInvStatus());
                line.setInvAttr1(poline.getInvAttr1());
                line.setInvAttr2(poline.getInvAttr2());
                line.setInvAttr3(poline.getInvAttr3());
                line.setInvAttr4(poline.getInvAttr4());
                line.setInvAttr5(poline.getInvAttr5());
                line.setCreatedId(command.getUserId());
                line.setCreateTime(new Date());
                line.setLastModifyTime(new Date());
                line.setModifiedId(command.getUserId());
                line.setInvType(poline.getInvType());
                line.setValidDateUom(poline.getValidDateUom());
                line.setUuid(command.getUuid());
                line.setIsIqc(poline.getIsIqc());
                line.setSkuId(poline.getSkuId());
                lineList.add(line);
            }
        }
        WhAsn asn = new WhAsn();
        // WMS单据号 调用HUB编码生成器获得
        String asnCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_INNER, null, null);
        asn.setAsnCode(asnCode);
        asn.setAsnExtCode(command.getAsnExtCode());
        asn.setPoId(po.getId());
        asn.setOuId(command.getOuId());
        asn.setExtCode(po.getExtCode());
        asn.setCustomerId(po.getCustomerId());
        asn.setStoreId(po.getStoreId());
        asn.setPoDate(new Date());
        asn.setEta(po.getEta());
        asn.setDeliveryTime(po.getDeliveryTime());
        asn.setSupplierId(po.getSupplierId());
        asn.setLogisticsProviderId(po.getLogisticsProviderId());
        asn.setAsnType(po.getPoType());
        asn.setStatus(PoAsnStatus.ASN_NEW);
        asn.setCreatedId(command.getUserId());
        asn.setCreateTime(new Date());
        asn.setLastModifyTime(new Date());
        asn.setModifiedId(command.getUserId());
        asn.setUuid(command.getUuid());
        asn.setIsIqc(po.getIsIqc());
        this.asnManager.createAsnAndLineWithUuidToShard(asn, lineList);
        return asn;
    }

    @Override
    public void revokeAsnWithUuid(WhAsnCommand command) {
        this.asnManager.revokeAsnWithUuidToShard(command);
    }

    @Override
    public WhAsn updateAsnWithUuid(WhPoCommand command) {
        WhAsn asn = this.asnManager.findWhAsnByIdToShard(command.getAsnId(), command.getOuId());
        if (null == asn) {
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }
        List<WhAsnLine> lineList = new ArrayList<WhAsnLine>();
        if (null != command.getPoLineList() && command.getPoLineList().size() > 0) {
            for (WhPoLineCommand lineCommand : command.getPoLineList()) {
                WhPoLine poline = this.poLineManager.findWhPoLineByIdOuIdToShard(lineCommand.getId(), command.getOuId());
                if (poline.getAvailableQty() < lineCommand.getQtyPlanned()) {
                    throw new BusinessException(ErrorCodes.ASNLINE_QTYPLANNED_ERROR);
                }
                WhAsnLine line = this.asnLineManager.findWhAsnLineByPoLineIdAndUuidAndOuId(poline.getId(), poline.getUuid(), poline.getOuId());
                if (line == null) {
                    line = new WhAsnLine();
                    line.setOuId(command.getOuId());
                    line.setPoLineId(poline.getId());
                    line.setPoLinenum(poline.getLinenum());
                    line.setSkuId(poline.getSkuId());
                    line.setQtyPlanned(lineCommand.getQtyPlanned());
                    line.setStatus(PoAsnStatus.ASNLINE_NOT_RCVD);
                    line.setIsIqc(poline.getIsIqc());
                    line.setMfgDate(poline.getMfgDate());
                    line.setExpDate(poline.getMfgDate());
                    line.setValidDate(poline.getValidDate());
                    line.setBatchNo(poline.getBatchNo());
                    line.setCountryOfOrigin(poline.getCountryOfOrigin());
                    line.setInvStatus(poline.getInvStatus());
                    line.setInvAttr1(poline.getInvAttr1());
                    line.setInvAttr2(poline.getInvAttr2());
                    line.setInvAttr3(poline.getInvAttr3());
                    line.setInvAttr4(poline.getInvAttr4());
                    line.setInvAttr5(poline.getInvAttr5());
                    line.setCreatedId(command.getUserId());
                    line.setCreateTime(new Date());
                    line.setLastModifyTime(new Date());
                    line.setModifiedId(command.getUserId());
                    line.setInvType(poline.getInvType());
                    line.setValidDateUom(poline.getValidDateUom());
                    line.setUuid(command.getUuid());
                    line.setIsIqc(poline.getIsIqc());
                    line.setSkuId(poline.getSkuId());
                } else {
                    line.setQtyPlanned(line.getQtyPlanned() + lineCommand.getQtyPlanned());
                    line.setModifiedId(command.getUserId());
                    line.setLastModifyTime(new Date());
                }
                lineList.add(line);
            }
        }
        asn.setUuid(command.getUuid());
        asn.setModifiedId(command.getUserId());
        asn.setLastModifyTime(new Date());
        this.asnManager.createAsnAndLineWithUuidToShard(asn, lineList);
        return asn;
    }

    @Override
    public void saveTempAsnWithUuid(WhPoCommand command) {
        Long asnId = command.getAsnId();
        Long ouId = command.getOuId();
        String uuid=command.getUuid();
        Long userId=command.getUserId();
        WhAsn asn = this.asnManager.findWhAsnByIdToShard(asnId, ouId);
        if (null == asn) {
            throw new BusinessException(ErrorCodes.ASN_NULL);
        }
        List<WhAsnLine> asnLineList = this.asnLineManager.findWhAsnLineByAsnIdOuIdUuid(asnId, ouId, uuid);
        List<WhAsnLine> saveAsnLineList = new ArrayList<WhAsnLine>();
        Map<Long, Double> poLineMap = new HashMap<Long, Double>();
        Double lineQty = Constants.DEFAULT_DOUBLE;
        if (asnLineList != null) {
            for (WhAsnLine asnLine : asnLineList) {
                asnLine.setUuid(null);
                asnLine.setModifiedId(userId);
                saveAsnLineList.add(asnLine);
                // asn计划数量统计
                lineQty += asnLine.getQtyPlanned();
                // po明细
                if (poLineMap.containsKey(asnLine.getPoLineId())) {
                    poLineMap.put(asnLine.getPoLineId(), poLineMap.get(asnLine.getPoLineId()) + asnLine.getQtyPlanned());
                } else {
                    poLineMap.put(asnLine.getPoLineId(), asnLine.getQtyPlanned());
                }
            }
        }
        // asn数据封装
        asn.setQtyPlanned(asn.getQtyPlanned() + lineQty);
        asn.setModifiedId(userId);

        // po单明细
        Iterator<Entry<Long, Double>> it = poLineMap.entrySet().iterator();
        List<WhPoLine> savePoLineList = new ArrayList<WhPoLine>();
        while (it.hasNext()) {
            Entry<Long, Double> entry = it.next();
            WhPoLine poLine = this.poLineManager.findWhPoLineByIdOuIdToShard(entry.getKey(), ouId);
            if (null == poLine) {
                throw new BusinessException(ErrorCodes.PO_NULL);
            }
            poLine.setModifiedId(userId);
            poLine.setAvailableQty(poLine.getAvailableQty() - entry.getValue());
            savePoLineList.add(poLine);

        }

        // po单
        WhPo po = this.poManager.findWhPoByIdToShard(asn.getPoId(), ouId);

        this.asnManager.saveTempAsnWithUuidToShard(asn, saveAsnLineList, po, savePoLineList);
    }
}
