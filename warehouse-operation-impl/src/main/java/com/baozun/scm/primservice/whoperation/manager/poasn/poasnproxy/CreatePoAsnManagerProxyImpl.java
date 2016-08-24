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
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
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
    private AsnManager asnManager;
    @Autowired
    private AsnLineManager asnLineManager;
    @Autowired
    private BiPoManager biPoManager;
    @Autowired
    private PkManager pkManager;
    @Autowired
    private BiPoLineManager biPoLineManager;

    /**
     * 验证po单数据是否完整
     * 
     * @param po
     * @return
     */
    private static void checkPoParameter(WhPoCommand po) {
        if (null == po) {
            throw new BusinessException(ErrorCodes.PO_IS_NULL);
        }
        if (StringUtil.isEmpty(po.getExtCode())) {
            throw new BusinessException(ErrorCodes.PO_EXTCODE_IS_NULL);
        }
        if (null == po.getPoType()) {
            throw new BusinessException(ErrorCodes.PO_POTYPE_IS_NULL);
        }
        if (null == po.getStatus()) {
            throw new BusinessException(ErrorCodes.PO_STATUS_IS_NULL);
        }
        // 验证是否是WMS内部创建还是上位系统同步的PO单
        if (!po.getIsWms()) {
            // false为 上位系统同步的PO单 需要验证poline的数据
            if (po.getPoLineList().size() == 0) {
                throw new BusinessException(ErrorCodes.PO_POLINELIST_IS_NULL);
            }
        }
    }

    /**
     * 验证asn单信息
     * 
     * @param asn
     * @return
     */
    private static void checkAsnParameter(WhAsnCommand asn) {
        if (null == asn) {
            throw new BusinessException(ErrorCodes.ASN_IS_NULL_ERROR);
        }
        if (StringUtil.isEmpty(asn.getAsnExtCode())) {
            throw new BusinessException(ErrorCodes.ASN_EXTCODE_IS_NULL_ERROR);
        }
        if (null == asn.getCustomerId()) {
            throw new BusinessException(ErrorCodes.ASN_CUSTOMER_IS_NULL_ERROR);
        }
        if (null == asn.getStoreId()) {
            throw new BusinessException(ErrorCodes.ASN_STORE_IS_NULL_ERROR);
        }
        // 创建ASN单 OUID为必须值
        if (null == asn.getOuId()) {
            throw new BusinessException(ErrorCodes.ASN_OUID_IS_NULL_ERROR);
        }
    }

    /**
     * 封装创建PO单数据
     * 
     * @param po
     * @return
     */
    private WhPo copyPropertiesPo(WhPoCommand po) {
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
    private List<WhPoLine> copyPropertiesPoLine(WhPoCommand po) {
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
     * 封装ASN单信息
     * 
     * @param asn
     * @return
     */
    private WhAsn copyPropertiesAsn(WhAsnCommand asn) {
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
        Long ouId = asn.getOuId();
        Long poId = asn.getPoId();
        try {
            //查找PO单
            WhPo whpo = this.poManager.findWhPoByIdToShard(poId, ouId);
            if(null==whpo){
                throw new BusinessException(ErrorCodes.PO_NULL);
            }
            if(PoAsnStatus.PO_CANCELED==whpo.getStatus()||PoAsnStatus.PO_CLOSE==whpo.getStatus()){
                throw new BusinessException(ErrorCodes.PO_CREATEASN_STATUS_ERROR);
            }
            this.deleteTempAsnAndLine(poId, ouId, null);
            // WMS单据号 调用HUB编码生成器获得
            String asnCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_INNER, null, null);
            if (StringUtil.isEmpty(asnCode)) {
                log.warn("CreateAsnBatch warn asnCode generateCode is null");
                throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
            }
            asn.setAsnCode(asnCode);
            // 相关单据号 调用HUB编码生成器获得
            String asnExtCode = this.getAsnExtCode(whpo.getStoreId(), whpo.getOuId());
            asn.setAsnExtCode(asnExtCode);

            // 验证数据完整性
            // checkAsnParameter(asn);
           
            List<WhPoLine> poLineList = this.poLineManager.findWhPoLineByPoIdOuIdWhereHasAvailableQtyToShard(whpo.getId(), ouId);
            if(null==poLineList||poLineList.size()==Constants.DEFAULT_INTEGER){
                throw new BusinessException(ErrorCodes.PO_NO_AVAILABLE_ERROR);
            }

            this.asnManager.createAsnBatch(asn, whpo, poLineList);

        } catch (BusinessException e) {
            rm.setMsg(e.getErrorCode() + "");
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            log.error("CreateAsnBatch error poid: " + asn.getPoId() + " ouid: " + asn.getOuId());
            log.error("" + e);
            return rm;
        } catch (Exception ex) {
            log.error("CreateAsnBatch error poid: " + asn.getPoId() + " ouid: " + asn.getOuId());
            log.error("" + ex);
            rm.setMsg(ErrorCodes.SYSTEM_EXCEPTION + "");
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
        }

        log.info("CreateAsnBatch end =======================");
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg("success");
        return rm;
    }


    /**
     * 生成AsnExtCode
     * 
     * @param ouId
     * @param storeId
     * @return
     */
    private String getAsnExtCode(Long ouId, Long storeId) {
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
                throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
            }

            WhAsnCommand search = new WhAsnCommand();
            search.setAsnExtCode(asnExtCode);
            search.setOuId(ouId);
            search.setStoreId(storeId);
            List<WhAsnCommand> searchList = this.asnManager.findListByParamsExt(search);
            // 如果没有 直接结束
            if (searchList == null || searchList.size() == 0) {
                isSuccess = true;
            }
        }
        if (!isSuccess) {
            // 如果5次获取都失败了 直接返回失败
            log.warn("CreateAsnBatch warn asnExtCode generateCode CheckAsnCode is not null");
            throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
        }
        return asnExtCode;
    }


    @Override
    public void createPoLineSingleNew(BaseCommand whPoLine) {
        log.info("CreatePoLineSingle start =======================");
        try{
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
                biPoLineManager.insert(line);
            } else {
                // 如果数据存在 合并数据
                wpl.setQtyPlanned(wpl.getQtyPlanned() + line.getQtyPlanned());// 计划数量
                wpl.setAvailableQty(wpl.getAvailableQty() + line.getQtyPlanned());// 可用数量=原可用数量+新计划数量
                wpl.setModifiedId(line.getModifiedId());
                biPoLineManager.saveOrUpdateByVersion(wpl);
            }
            log.info("CreatePoLineSingle end =======================");
        }catch(BusinessException e){
            throw e;
        }catch(Exception ex){
            log.error("" + ex);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
    }


    /**
     * 批量保存POLINE信息
     */
    @Override
    public void createPoLineBatchNew(BaseCommand whPoLine) {
        log.info("CreatePoLineBatch start =======================");
        try {
            BiPoLineCommand biPoLineCommand = new BiPoLineCommand();
            BeanUtils.copyProperties(whPoLine, biPoLineCommand);

            biPoLineManager.createPoLineBatchToInfo(biPoLineCommand);

            BiPo bipo = this.biPoManager.findBiPoById(biPoLineCommand.getPoId());

            if (bipo.getOuId() != null) {
                List<WhPoLine> infoPolineList = this.poLineManager.findInfoPoLineByExtCodeStoreIdOuIdStatusToInfo(bipo.getExtCode(), bipo.getStoreId(), bipo.getOuId(), Arrays.asList(new Integer[] {PoAsnStatus.POLINE_NEW}));

                poLineManager.createPoLineBatchToShareNew(bipo.getExtCode(), bipo.getStoreId(), bipo.getOuId(), infoPolineList);
            }
            log.info("CreatePoLineBatch end =======================");
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            log.error(e + "");
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
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
        BiPo biPo = this.biPoManager.findBiPoByPoCode(poCode);
        // 如果本次生成的条码在数据库中有数据的话，则再生成一次。阙值加1
        if (biPo != null) {
            return getUniqueCode(threshold + 1);
        }
        return poCode;
    }



    /**
     * 创建BIPO单据
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
        ResponseMsg rm = new ResponseMsg();
        // 验证数据完整性
        Long ouId=po.getOuId();
        try {
            checkPoParameter(po);
            //校验ExtCode: ext_code与storeId 唯一性
            List<BiPo> checkExtCodeBiPoList = this.biPoManager.findListByStoreIdExtCode(po.getStoreId(), po.getExtCode());
            if (null == checkExtCodeBiPoList || checkExtCodeBiPoList.size() > 0) {
                log.warn("check extcode returns failure when createPo!");
                throw new BusinessException(ErrorCodes.PO_CHECK_EXTCODE_ERROR);
            }

            // 相关单据号 调用HUB编码生成器获得
            poCode = getUniqueCode();
            if (StringUtil.isEmpty(poCode)) {
                log.warn("CreatePo warn poCode generateCode is null");
                throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
            }
            // 创建PO单数据
            WhPo whPo = copyPropertiesPo(po);
            whPo.setPoCode(getUniqueCode());
            List<WhPoLine> whPoLines = null != po.getPoLineList() ? copyPropertiesPoLine(po) : null;
            // 判断OU_ID
            // 查询t_wh_check_pocode
            // 有:查询对应
            /**
             * @mender yimin.lu 2016/4/27 以下逻辑做修正；修改后的逻辑： 1.生成主库备份 2.如果有仓库，则在仓库中再插入一份
             */
            biPoManager.createPoAndLineToInfo(whPo, whPoLines);
            if (ouId != null) {
                whPo.setPoCode(getUniqueCode());
                biPoManager.createPoAndLineToShared(whPo, whPoLines);
            }
        } catch (BusinessException e) {
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            rm.setMsg(e.getErrorCode() + "");
            return rm;
        } catch (Exception ex) {
            log.error("CreatePoAsnManagerProxyImpl.createPoNew error:[exception:{}]", ex);
            rm.setResponseStatus(ResponseMsg.DATA_ERROR);
            rm.setMsg(ErrorCodes.SAVE_PO_FAILED + "");
            return rm;
        }

        log.info("CreateBIPo end =======================");
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg("Success!");
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
        // 3.最后的同步机制：由INFO->shard
        Long ouId = command.getOuId();
        String uuid=command.getUuid();
        Long userId = command.getUserId();
        BiPo bipo = this.biPoManager.findBiPoById(command.getId());
        if (null == bipo) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        if (null != bipo.getOuId() && !bipo.getOuId().equals(ouId)) {
            throw new BusinessException(ErrorCodes.BIPO_CREATESUB_OUID_ERROR);
        }
        // 查找非取消状态下的拆单
        WhPo po = this.poManager.findWhPoByExtCodeStoreIdOuIdToInfo(bipo.getExtCode(), bipo.getStoreId(), ouId);
        // 这边的逻辑：
        // 没有对应的PO的时候，则重新生成PO
        // 如果有对应的PO，那么查找这个PO有没有uuid；如果没有，则赋值uuid；如果有的话，看有没有对应的uuid的明细；如果有对应的uuid的明细，表明此单在一个仓库中同事被操作，则抛错；否则将此uuid赋予PO
        // 防止非正常关闭页面导致UUID不能及时更新
        if (null == po) {
            po = new WhPo();
            BeanUtils.copyProperties(bipo, po);
            po.setPoCode(getUniqueCode());
            po.setId(null);
            po.setOuId(ouId);
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
        if (null == command) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        if (null == command.getPoLineList()) {
            throw new BusinessException(ErrorCodes.PACKAGING_ERROR);
        }
        List<WhPoLine> lineList = new ArrayList<WhPoLine>();
        for (WhPoLineCommand lineCommand : command.getPoLineList()) {
            WhPoLine poLine = this.poLineManager.findWhPoLineByIdOuIdToInfo(lineCommand.getId(), command.getOuId());
            if (null == poLine) {
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            poLine.setModifiedId(command.getUserId());
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
        this.biPoManager.saveSubPoToInfo(command.getId(), command.getExtCode(), command.getStoreId(), command.getOuId(), command.getUuid(), command.getUserId());
        WhPo infoPo = this.poManager.findWhPoByExtCodeStoreIdOuIdToInfo(command.getExtCode(), command.getStoreId(), command.getOuId());
        List<Integer> statusList = Arrays.asList(new Integer[] {PoAsnStatus.POLINE_NEW, PoAsnStatus.POLINE_CREATE_ASN, PoAsnStatus.POLINE_RCVD});
        List<WhPoLine> infoPoLineList = this.poLineManager.findWhPoLineListByPoIdOuIdStatusListToInfo(infoPo.getId(), command.getOuId(), statusList);
        this.poManager.saveSubPoToShard(command.getExtCode(), command.getStoreId(), command.getOuId(), command.getUserId(), this.getUniqueCode(), infoPo, infoPoLineList);

    }

    @Override
    public void closeSubPoToInfo(WhPoCommand command) {
        this.biPoManager.closeSubPoToInfo(command.getExtCode(), command.getStoreId(), command.getOuId(), command.getId());
    }

    @Override
    public WhAsn createAsnWithUuid(WhPoCommand command) {
        // 这边的逻辑：
        // 当在WMS系统页面操作创建ASN的时候，先将数据保存到临时表
        // 限定：同一个仓库下；只允许一个人同时操作一个PO单；
        this.deleteTempAsnAndLine(command.getId(), command.getOuId(), command.getUuid());

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
        this.deleteTempAsnAndLine(asn.getPoId(), command.getOuId(), command.getUuid());
        List<WhAsnLine> lineList = new ArrayList<WhAsnLine>();
        if (null != command.getPoLineList() && command.getPoLineList().size() > 0) {
            for (WhPoLineCommand lineCommand : command.getPoLineList()) {
                WhPoLine poline = this.poLineManager.findWhPoLineByIdOuIdToShard(lineCommand.getId(), command.getOuId());
                if (poline.getAvailableQty() < lineCommand.getQtyPlanned()) {
                    throw new BusinessException(ErrorCodes.ASNLINE_QTYPLANNED_ERROR);
                }
                WhAsnLine line = this.asnLineManager.findWhAsnLineByPoLineIdAndUuidAndOuId(poline.getId(), command.getUuid(), poline.getOuId());
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
                }
                lineList.add(line);
            }
        }
        // asn.setUuid(command.getUuid());
        asn.setModifiedId(command.getUserId());
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
        // 删除其他的临时数据
        this.deleteTempAsnAndLine(asnId, ouId, uuid);
        List<WhAsnLine> asnLineList = this.asnLineManager.findWhAsnLineByAsnIdOuIdUuid(asnId, ouId, uuid);
        List<WhAsnLine> saveAsnLineList = new ArrayList<WhAsnLine>();
        List<WhAsnLine> delAsnLineList = new ArrayList<WhAsnLine>();
        Map<Long, Double> poLineMap = new HashMap<Long, Double>();
        Double lineQty = Constants.DEFAULT_DOUBLE;
        if (asnLineList != null) {
            for (WhAsnLine asnLine : asnLineList) {
                WhAsnLine asnOriginline = this.asnLineManager.findWhAsnLineByAsnIdPolineIdOuIdAndUuid(asnId, asnLine.getPoLineId(), ouId, null);
                if (asnOriginline == null) {
                    asnLine.setUuid(null);
                    asnLine.setModifiedId(userId);
                    saveAsnLineList.add(asnLine);
                } else {
                    asnOriginline.setModifiedId(userId);
                    asnOriginline.setQtyPlanned(asnOriginline.getQtyPlanned() + asnLine.getQtyPlanned());
                    saveAsnLineList.add(asnOriginline);
                    delAsnLineList.add(asnLine);
                }
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
        if (StringUtils.hasText(asn.getUuid())) {
            asn.setUuid(null);
        }

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
        if (PoAsnStatus.PO_NEW == po.getStatus()) {
            po.setStatus(PoAsnStatus.PO_CREATE_ASN);
        }

        this.asnManager.saveTempAsnWithUuidToShard(asn, saveAsnLineList, delAsnLineList, po, savePoLineList);
    }

    private void deleteTempAsnAndLine(Long poId, Long ouId, String uuid) {
        // 删除掉所有的临时ASN明细
        // 如果ASN头信息上有UUID，也删除掉
        WhAsn asn = this.asnManager.findTempAsnByPoIdOuIdAndLineNotUuid(poId, ouId, uuid);
        List<WhAsnLine> lineList = null;
        if (asn != null) {
            lineList = this.asnLineManager.findTempWhAsnLineByAsnIdOuIdNotUuid(asn.getId(), ouId, uuid);
            if (StringUtils.isEmpty(asn.getUuid())) {
                asn = null;
            }
        }
        this.asnManager.deleteAsnAndLine(asn, lineList);
    }

    @Override
    public void finishCreatingAsn(WhPoCommand command) {
        if (null != command.getAsnId()) {
            WhAsn asn = this.asnManager.findTempAsnByPoIdOuIdUuid(command.getId(), command.getOuId(), null);
            List<WhAsnLine> lineList = null;
            if (asn != null) {
                lineList = this.asnLineManager.findTempWhAsnLineByAsnIdOuIdNotUuid(asn.getId(), command.getOuId(), null);
                if (StringUtils.isEmpty(asn.getUuid())) {
                    asn = null;
                }
            }
            this.asnManager.deleteAsnAndLine(asn, lineList);
        }

    }

    @Override
    public ResponseMsg createAsn(WhAsnCommand asn) {
        log.info("CreateAsn start =======================");
        ResponseMsg rm = new ResponseMsg();
        try {
            // 验证数据完整性
            checkAsnParameter(asn);
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
            this.asnManager.createAsn(whAsn, asn.getAsnLineList());
        } catch (BusinessException e) {
            rm.setMsg(e.getErrorCode() + "");
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            log.error("CreateAsn error asnCode: " + asn.getAsnExtCode());
            log.error("" + e);
            return rm;
        } catch (Exception ex) {
            rm.setMsg(ErrorCodes.SYSTEM_EXCEPTION + "");
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            log.error("CreateAsn error asnCode: " + asn.getAsnExtCode());
            log.error("" + ex);
            return rm;
        }
        log.info("CreateAsn end =======================");
        return rm;
    }

    /**
     * 通过编码生成器接口获取asn相关单据号
     */
    @Override
    public String getAsnExtCode() {
        log.info(this.getClass().getSimpleName() + ".getAsnExtCode method begin!");
        String extCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_EXT, null, null);
        if (StringUtil.isEmpty(extCode)) {
            log.warn("getAsnExtCode warn generateCode is null");
            throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL, new Object[] {"asn"});
        }
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".getAsnExtCode method returns:{}!", extCode);
        }
        log.info(this.getClass().getSimpleName() + ".getAsnExtCode method end!");
        return extCode;
    }


}
