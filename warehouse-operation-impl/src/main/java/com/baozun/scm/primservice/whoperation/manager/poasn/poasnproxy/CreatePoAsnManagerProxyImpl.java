package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.rcvd.RcvdSnCacheCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhAsnRcvdLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.constant.PoAsnType;
import com.baozun.scm.primservice.whoperation.excel.ExcelContext;
import com.baozun.scm.primservice.whoperation.excel.ExcelImport;
import com.baozun.scm.primservice.whoperation.excel.exception.ExcelException;
import com.baozun.scm.primservice.whoperation.excel.exception.RootExcelException;
import com.baozun.scm.primservice.whoperation.excel.result.ExcelImportResult;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.archiv.OdoArchivManager;
import com.baozun.scm.primservice.whoperation.manager.bi.OutPutStreamToServersManager;
import com.baozun.scm.primservice.whoperation.manager.collect.WhOdoArchivIndexManager;
import com.baozun.scm.primservice.whoperation.manager.collect.WhOdoArchivLineIndexManager;
import com.baozun.scm.primservice.whoperation.manager.odo.manager.OdoManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.rcvd.GeneralRcvdManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ContainerManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.CustomerManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.SupplierManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.carton.WhCartonManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.bi.ImportExcel;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivIndex;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivLineIndex;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.Customer;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.StoreDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.Supplier;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdSnLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectReasons;
import com.baozun.scm.primservice.whoperation.model.warehouse.conf.basis.WarehouseDefectType;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;
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
    private BiPoLineManager biPoLineManager;
    @Autowired
    private CustomerManager customerManager;
    @Autowired
    private StoreManager storeManager;
    @Autowired
    private OdoArchivManager odoArchivManager;
    @Autowired
    private GeneralRcvdManager generalRcvdManager;
    @Autowired
    private WarehouseManager warehouseManager;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private WhCartonManager whCartonManager;
    @Autowired
    private SupplierManager supplierManager;
    @Autowired
    private OutPutStreamToServersManager outPutStreamToServersManager;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private WhOdoArchivIndexManager whOdoArchivIndexManager;
    @Autowired
    private WhOdoArchivLineIndexManager whOdoArchivLineIndexManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private ContainerManager containerManager;
    @Autowired
    private OdoManager odoManager;

    /**
     * 验证po单数据是否完整
     * 
     * @param po
     * @return
     */
    private static void checkPoParameter(BiPoCommand po) {
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
        //

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
    private WhPo copyPropertiesPo(BiPoCommand po) {
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
        whPo.setDataSource(Constants.WMS);
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
    private List<WhPoLine> copyPropertiesPoLine(BiPoCommand po) {
        log.info(this.getClass().getSimpleName() + ".copyPropertiesPoLine method begin!");
        List<WhPoLine> whPoLine = new ArrayList<WhPoLine>();
        if (null != po.getPoLineList()) {
            // 有line信息保存
            log.debug("CopyPropertiesPoLine po.getPoLineList().size(): " + po.getPoLineList().size());
            for (int i = 0; i < po.getPoLineList().size(); i++) {
                BiPoLineCommand polineCommand = po.getPoLineList().get(i);
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
    public ResponseMsg createAsnBatch(WhAsnCommand asn, Boolean isCreateAsn) {
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
            try {

                this.deleteTempAsnAndLine(poId, ouId, null);
            } catch (Exception ex) {
                log.error("", ex);
                throw new BusinessException(ErrorCodes.ASN_CREATE_SYNC_ERROR);
            }

            // 验证数据完整性
            // checkAsnParameter(asn);
            List<WhPoLine> poLineList = null;
            if (isCreateAsn) {
                poLineList = this.poLineManager.findWhPoLineByPoIdOuIdWhereHasAvailableQtyToShard(whpo.getId(), ouId);
                if (null == poLineList || poLineList.size() == Constants.DEFAULT_INTEGER) {
                    throw new BusinessException(ErrorCodes.PO_NO_AVAILABLE_ERROR);
                }
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
    @Override
    public String getUniqueCode() {
        return this.getUniqueCode(null, Constants.WHPO_MODEL_URL, null, null, null);
    }

    private String getUniqueCode(Integer threshold, String model, String group, String start, String end) {
        // 阙值 3次 如果同一个编码三次生成的条码在库位所在的仓库都有重复，那么则回滚
        threshold = threshold == null ? 0 : threshold;
        if (threshold >= 3) return null;
        String poCode = codeManager.generateCode(Constants.WMS, model, group, start, end);
        BiPo biPo = this.biPoManager.findBiPoByPoCode(poCode);
        // 如果本次生成的条码在数据库中有数据的话，则再生成一次。阙值加1
        if (biPo != null) {
            return getUniqueCode(threshold + 1, model, group, start, end);
        }
        return poCode;
    }

    private String getUniqueCode(String model, String group, String start, String end) {
        return this.getUniqueCode(null, model, group, start, end);
    }

    /**
     * 创建BIPO单据
     */
    @Override
    public ResponseMsg createPoNew(BiPoCommand po) {
        log.info("CreatePo start =======================");
        // @author yimin.lu
        // ①校验数据完整性
        // ②校验ext_code+storeId的唯一性
        // ③生成po_code[唯一性；三次生成都不唯一，抛出异常]
        // ④数据插入到数据库中
        // 4-1:当数据没有仓库的时候，则只需插入到INFO的BIPO中
        // 4-2：当数据有仓库的时候，需要将数据插入到INFO的BIPO中，并同步到INFO的WHPO中来

        // @mender yimin.lu 2017/3/23 设置是否自动关单isAutoClose： 【true】
        // 1.指定店铺和仓库：两者判断
        // 2.指定店铺：在拆分到仓库时回写


        String poCode = null;
        ResponseMsg rm = new ResponseMsg();
        // 验证数据完整性
        Long ouId=po.getOuId();
        try {
            checkPoParameter(po);
            if (StringUtils.isEmpty(po.getDataSource())) {
                po.setDataSource(Constants.WMS4);
            }
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
            whPo.setPoCode(poCode);
            List<WhPoLine> whPoLines = null != po.getPoLineList() ? copyPropertiesPoLine(po) : null;
            // 判断OU_ID
            // 查询t_wh_check_pocode
            // 有:查询对应
            /**
             * @mender yimin.lu 2016/4/27 以下逻辑做修正；修改后的逻辑： 1.生成主库备份 2.如果有仓库，则在仓库中再插入一份
             */
            this.createPoDefault(whPo, whPoLines, ouId);

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


    private void createPoDefault(WhPo whPo, List<WhPoLine> whPoLines, Long ouId) {
        this.createPoDefault(whPo, null, whPoLines, ouId);
    }
    
    private void createPoDefault(WhPo whPo, WhPoTransportMgmt whPoTm, List<WhPoLine> whPoLines, Long ouId) {
        createPoDefault(whPo, whPoTm, whPoLines, null, ouId);
    }
    
    private void createPoDefault(WhPo whPo, WhPoTransportMgmt whPoTm, List<WhPoLine> whPoLines, List<WhOdoArchivLineIndex> indexList, Long ouId) {
        Boolean isAutoClose = this.biPoManager.calIsAutoClose(whPo.getStoreId(), ouId);
        whPo.setIsAutoClose(isAutoClose);
        biPoManager.createPoAndLineToInfo(whPo, whPoTm, whPoLines);
        if (ouId != null) {
            // @mender yimin.lu 2017/5/11 info->shard poCode 一致
            biPoManager.createPoAndLineToShared(whPo, whPoTm, whPoLines, indexList);
        }
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
        this.poManager.saveSubPoToShard(command.getExtCode(), command.getStoreId(), command.getOuId(), command.getUserId(), infoPo.getPoCode(), infoPo, infoPoLineList);

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
                // @mender yimin.lu 2017/5/4 失效日期修正
                line.setExpDate(poline.getExpDate());
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
        asn.setLogisticsProvider(po.getLogisticsProvider());
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

    @Override
    public ResponseMsg importBiPo(String url, String fileName, Long userImportExcelId, Locale locale, Long ouId, Long userId, String logId) {
        File importExcelFile = new File(url, fileName);
        if (!importExcelFile.exists()) {
            throw new BusinessException(ErrorCodes.IMPORT_ERROR_FILE_NOT_EXISTS);
        }

        try {
            // 创建excel上下文实例,它的构成需要配置文件的路径
            ExcelContext context = new ExcelContext("excel-config.xml");


            List<BiPoCommand> poCommandList = new ArrayList<BiPoCommand>();
            List<BiPoLineCommand> poLineList = new ArrayList<BiPoLineCommand>();

            // 入库单头信息
            ExcelImportResult biPoExcelImportResult = this.readSheetFromExcel(context, importExcelFile, locale, Constants.IMPORT_BIPO_EXCEL_CONFIG_ID, Constants.IMPORT_BIPO_TITLE_INDEX);
            if (ExcelImportResult.READ_STATUS_SUCCESS == biPoExcelImportResult.getReadstatus()) {
                // 验证商品信息完整及是否存在重复商品
                this.validateBiPo(biPoExcelImportResult, poCommandList, locale, userId);
            }
            // 如果头信息有严重的问题，则不进行明细的校验，直接推出错误
            if (poCommandList.size() == 0) {
                Workbook workbook = biPoExcelImportResult.getWorkbook();
                ExcelImport.exportImportErroeMsg(workbook, biPoExcelImportResult.getRootExcelException());

                ImportExcel importExcel = new ImportExcel();
                importExcel.setImportType("BI_PO");
                importExcel.setWorkbook(workbook);
                importExcel.setUserImportExcelId(userImportExcelId);
                importExcel.setUserId(userId);
                importExcel.setOuId(ouId);

                // 调用异常输出接口，生成错误信息
                String errorFileName = outPutStreamToServersManager.uploadImportFileErrorToInfo(importExcel);

                ResponseMsg msg = new ResponseMsg();
                msg.setResponseStatus(ResponseMsg.STATUS_ERROR);
                msg.setMsg(errorFileName);
                return msg;
            }
            BiPoCommand poCommand = poCommandList.get(0);
            // 入库单明细信息
            ExcelImportResult biPoLinesExcelImportResult = this.readSheetFromExcel(context, importExcelFile, locale, Constants.IMPORT_BIPOLINE_EXCEL_CONFIG_ID, Constants.IMPORT_BIPOLINE_TITLE_INDEX);
            if (ExcelImportResult.READ_STATUS_SUCCESS == biPoLinesExcelImportResult.getReadstatus()) {
                // 验证商品信息完整及是否存在重复商品
                this.validateBiPoLines(biPoLinesExcelImportResult, poLineList, locale, poCommand, userId, logId);
            }
            if (ExcelImportResult.READ_STATUS_FAILED == biPoExcelImportResult.getReadstatus() || ExcelImportResult.READ_STATUS_FAILED == biPoLinesExcelImportResult.getReadstatus()) {
                Workbook workbook = biPoExcelImportResult.getWorkbook();
                ExcelImport.exportImportErroeMsg(workbook, biPoExcelImportResult.getRootExcelException());
                ExcelImport.exportImportErroeMsg(workbook, biPoLinesExcelImportResult.getRootExcelException());

                ImportExcel importExcel = new ImportExcel();
                importExcel.setImportType("BI_PO");
                importExcel.setWorkbook(workbook);
                importExcel.setUserImportExcelId(userImportExcelId);
                importExcel.setUserId(userId);
                importExcel.setOuId(ouId);

                // 调用异常输出接口，生成错误信息
                String errorFileName = outPutStreamToServersManager.uploadImportFileErrorToInfo(importExcel);

                ResponseMsg msg = new ResponseMsg();
                msg.setResponseStatus(ResponseMsg.STATUS_ERROR);
                msg.setMsg(errorFileName);
                return msg;
            }
            // 如果校验成功，则进行导入
            poCommand.setPoLineList(poLineList);

            return this.createBiPoFromExcel(poCommand, userId, logId);


        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCodes.IMPORT_ERROR);
        }


    }

    private void validateBiPoLines(ExcelImportResult excelImportResult, List<BiPoLineCommand> poLineList, Locale locale, BiPoCommand excelPo, Long userId, String logId) {
        List<BiPoLineCommand> lineCommandList = excelImportResult.getListBean();
        RootExcelException rootExcelException = new RootExcelException("", excelImportResult.getSheetName(), excelImportResult.getTitleSize());

        Map<Long, String> invMap = this.getInvStatusMap();
        for (int index = 0; index < lineCommandList.size(); index++) {
            int rowNum = index + Constants.IMPORT_BIPOLINE_TITLE_INDEX + 1;
            BiPoLineCommand lineCommand = lineCommandList.get(index);


            Sku sku = this.biPoLineManager.findSkuByBarCode(lineCommand.getSkuBarCode(), excelPo.getCustomerId(), logId);
            if (sku == null) {
                rootExcelException.getExcelExceptions().add(new ExcelException("条码找不到对应的商品", null, rowNum, null));
            } else {
                lineCommand.setSkuId(sku.getId());
            }
            if (lineCommand.getInvStatus() == null) {
                rootExcelException.getExcelExceptions().add(new ExcelException("库存状态不能为空", null, rowNum, null));
            } else {
                if (!invMap.containsKey(lineCommand.getInvStatus())) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("库存状态编码错误", null, rowNum, null));
                }
            }
            if (StringUtils.hasText(lineCommand.getInvType())) {
                SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_TYPE, lineCommand.getInvType());
                if (dic == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("库存类型编码错误", null, rowNum, null));
                }
            }
            if (StringUtils.hasText(lineCommand.getInvAttr1())) {
                SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_1, lineCommand.getInvAttr1());
                if (dic == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("库存属性1编码错误", null, rowNum, null));
                }
            }
            if (StringUtils.hasText(lineCommand.getInvAttr2())) {
                SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_2, lineCommand.getInvAttr2());
                if (dic == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("库存属性2编码错误", null, rowNum, null));
                }
            }
            if (StringUtils.hasText(lineCommand.getInvAttr3())) {
                SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_3, lineCommand.getInvAttr3());
                if (dic == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("库存属性3编码错误", null, rowNum, null));
                }
            }
            if (StringUtils.hasText(lineCommand.getInvAttr4())) {
                SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_4, lineCommand.getInvAttr4());
                if (dic == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("库存属性4编码错误", null, rowNum, null));
                }
            }
            if (StringUtils.hasText(lineCommand.getInvAttr5())) {
                SysDictionary dic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.INVENTORY_ATTR_5, lineCommand.getInvAttr5());
                if (dic == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("库存属性5编码错误", null, rowNum, null));
                }
            }
            poLineList.add(lineCommand);

        }
        if (rootExcelException.isException()) {
            excelImportResult.setRootExcelException(rootExcelException);
            excelImportResult.setReadstatus(ExcelImportResult.READ_STATUS_FAILED);
        }
        
    }

    private void validateBiPo(ExcelImportResult excelImportResult, List<BiPoCommand> poCommandList, Locale locale, Long userId) {
        List<BiPoCommand> readBiPoList = excelImportResult.getListBean();
        RootExcelException rootExcelException = new RootExcelException("", excelImportResult.getSheetName(), excelImportResult.getTitleSize());
        int rowNum = Constants.IMPORT_BIPO_TITLE_INDEX + 1;


        if (readBiPoList.size() > 1) {
            rootExcelException.getExcelExceptions().add(new ExcelException("只能按单导入", null, rowNum, null));
        } else {
            BiPoCommand excelPo = readBiPoList.get(0);
            // 校验逻辑
            if (StringUtils.isEmpty(excelPo.getExtCode())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("入库单外接编码不能为空", null, rowNum, null));
            }
            if (StringUtils.isEmpty(excelPo.getCustomerCode())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("入库单客户编码不能为空", null, rowNum, null));
            }
            Customer customer = this.customerManager.findCustomerbyCode(excelPo.getCustomerCode());
            if (customer == null) {
                rootExcelException.getExcelExceptions().add(new ExcelException("客户编码有误，找不到对应的客户信息", null, rowNum, null));
            } else {
                Long customerId = customer.getId();
                excelPo.setCustomerId(customerId);
                if (StringUtils.isEmpty(excelPo.getStoreCode())) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("店铺编码不能为空", null, rowNum, null));
                } else {
                    Store store = this.storeManager.findStoreByCode(excelPo.getStoreCode());
                    if (store == null) {
                        rootExcelException.getExcelExceptions().add(new ExcelException("店铺编码找不到对应的店铺信息", null, rowNum, null));
                    } else {
                        if (!Constants.LIFECYCLE_START.equals(store.getLifecycle())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("店铺无效", null, rowNum, null));
                        }
                        if (!customer.getId().equals(store.getCustomerId())) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("客户-店铺不对应", null, rowNum, null));
                        }
                        Long storeId = store.getId();
                        excelPo.setStoreId(storeId);
                        Boolean customerStoreUserFlag = this.storeManager.checkCustomerStoreUser(customerId, storeId, userId);
                        if (!customerStoreUserFlag) {
                            rootExcelException.getExcelExceptions().add(new ExcelException("用户不具有此客户-店铺权限", null, rowNum, null));
                        }
                        // 校验ExtCode: ext_code与storeId 唯一性
                        if (StringUtils.hasText(excelPo.getExtCode())) {
                            List<BiPo> checkExtCodeBiPoList = this.biPoManager.findListByStoreIdExtCode(storeId, excelPo.getExtCode());
                            if (null == checkExtCodeBiPoList || checkExtCodeBiPoList.size() > 0) {
                                log.warn("check extcode returns failure when createPo!");
                                rootExcelException.getExcelExceptions().add(new ExcelException("PO单校验相关单据号失败，同一个店铺下有相同的相关单据号", null, rowNum, null));
                            }
                        }
                    }
                }
            }

            if (StringUtils.isEmpty(excelPo.getPoType())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("入库单类型不能为空", null, rowNum, null));
            } else {
                SysDictionary poTypeDic = this.sysDictionaryManager.getGroupbyGroupValueAndDicValue(Constants.PO_TYPE, excelPo.getPoType() + "");
                if (poTypeDic == null) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("系统参数入库单类型不正确", null, rowNum, null));
                }
            }
            if (StringUtils.isEmpty(excelPo.getSupplierCode())) {
                rootExcelException.getExcelExceptions().add(new ExcelException("供应商不能为空", null, rowNum, null));
            } else {
                Supplier search = new Supplier();
                search.setCode(excelPo.getSupplierCode());
                search.setLifecycle(Constants.LIFECYCLE_START);
                List<Supplier> s = this.supplierManager.findListByParam(search);
                if (s == null || s.size() == 0) {
                    rootExcelException.getExcelExceptions().add(new ExcelException("供应商编码找不到对应的可用的供应商", null, rowNum, null));
                } else {

                    excelPo.setSupplierId(s.get(0).getId());
                }
            }
            poCommandList.add(excelPo);

        }

        if (rootExcelException.isException()) {
            excelImportResult.setRootExcelException(rootExcelException);
            excelImportResult.setReadstatus(ExcelImportResult.READ_STATUS_FAILED);
        }

    }

    private ExcelImportResult readSheetFromExcel(ExcelContext context, File importExcelFile, Locale locale, String sheetName, int index) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(importExcelFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new BusinessException("文件读取异常");
        }
        return context.readExcel(sheetName, index, inputStream, locale);
    }



    /**
     * EXCEL导入PO
     * 
     * @param excelPo
     * @param poLineCommandListResult
     * @param result
     * @return
     */
    private ResponseMsg createBiPoFromExcel(BiPoCommand excelPo, Long userId, String logId) {
        Long ouId = excelPo.getOuId();
        Long customerId = excelPo.getCustomerId();
        Long storeId = excelPo.getStoreId();
        ResponseMsg rm = new ResponseMsg();
        try {

            List<BiPoLineCommand> biPoLineCommandList = excelPo.getPoLineList();
            List<WhPoLine> lineList = new ArrayList<WhPoLine>();
            Double qtyPlanned = Constants.DEFAULT_DOUBLE;
            if (biPoLineCommandList != null && biPoLineCommandList.size() > 0) {
                for (BiPoLineCommand lineCommand : biPoLineCommandList) {
                    WhPoLine line = new WhPoLine();
                    line.setSkuId(lineCommand.getSkuId());
                    line.setQtyPlanned(lineCommand.getQtyPlanned());
                    line.setAvailableQty(line.getQtyPlanned());
                    qtyPlanned += line.getQtyPlanned();
                    line.setStatus(PoAsnStatus.POLINE_NEW);
                    line.setIsIqc(lineCommand.getIsIqc());
                    line.setMfgDate(lineCommand.getMfgDate());
                    line.setExpDate(lineCommand.getExpDate());
                    line.setValidDate(lineCommand.getValidDate());
                    line.setBatchNo(lineCommand.getBatchNo());
                    line.setCountryOfOrigin(lineCommand.getCountryOfOrigin());
                    line.setInvStatus(lineCommand.getInvStatus());
                    line.setInvAttr1(lineCommand.getInvAttr1());
                    line.setInvAttr2(lineCommand.getInvAttr2());
                    line.setInvAttr3(lineCommand.getInvAttr3());
                    line.setInvAttr4(lineCommand.getInvAttr4());
                    line.setInvAttr5(lineCommand.getInvAttr5());
                    line.setInvType(lineCommand.getInvType());
                    line.setSkuId(lineCommand.getSkuId());
                    line.setCreatedId(userId);
                    line.setCreateTime(new Date());
                    line.setModifiedId(userId);
                    line.setLastModifyTime(new Date());
                    lineList.add(line);
                }
            }

            WhPo po = new WhPo();
            // 相关单据号 调用HUB编码生成器获得
            String poCode = getUniqueCode();
            if (StringUtil.isEmpty(poCode)) {
                log.warn("CreatePo warn poCode generateCode is null");
                throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
            }

            po.setPoCode(poCode);
            po.setExtCode(excelPo.getExtCode());
            po.setOuId(ouId);
            po.setSupplierId(excelPo.getSupplierId());
            po.setPoType(excelPo.getPoType());
            po.setStatus(PoAsnStatus.PO_NEW);
            po.setIsIqc(excelPo.getIsIqc());
            po.setPoDate(excelPo.getPoDate());
            po.setStoreId(storeId);
            po.setCustomerId(customerId);
            po.setEta(excelPo.getEta());

            Integer ctnPlanned = Constants.DEFAULT_INTEGER;
            po.setQtyPlanned(qtyPlanned);
            po.setCtnPlanned(ctnPlanned);
            po.setIsWms(excelPo.getIsWms());
            po.setIsVmi(excelPo.getIsVmi());
            po.setCreateTime(new Date());
            po.setCreatedId(userId);
            po.setLastModifyTime(new Date());
            po.setModifiedId(userId);
            po.setLogisticsProvider(excelPo.getLogisticsProvider());
            this.createPoDefault(po, lineList, ouId);

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
    
    
    /**
     * 创建上位系统传入的Po
     */
	@Override
	public ResponseMsg createPoByExt(WhPo whPo, WhPoTransportMgmt whPoTm, List<WhPoLine> whPoLines, Long ouId) {
	    ResponseMsg msg = new ResponseMsg();
	    msg.setResponseStatus(1);
		// 退换货逻辑
		if (whPo.getPoType() == 2) {
            Store store = this.storeManager.findStoreById(whPo.getStoreId());
            if (null == store) {
                msg.setMsg("store:{" + whPo.getStoreId() + "} not find");
                msg.setResponseStatus(0);
                return msg;
            }
			// 退货入关联销售出
			String ecOrderCode = whPo.getOriginalEcOrderCode();
			String dataSource = whPo.getDataSource();
			if (null != store.getIsReturnedPurchaseOriginalInvAttr() && store.getIsReturnedPurchaseOriginalInvAttr()) {
			    msg = this.createOdoArchivLineIndex(whPo, whPoTm, whPoLines, ecOrderCode, dataSource, ouId, msg);
			} else {
				// 退货入不关联销售出
			    // 查询归档(collect)数据表中的数据
			    boolean flag = whOdoArchivIndexManager.checkWhOdoArchivLineIndexExsits(ecOrderCode, dataSource, ouId);
			    if (!flag) {
			        // 代表原来关联过原始单据, 则此订单依旧关联
			        // 创建到(collect)数据表并生成PoAsn
			        msg = this.createOdoArchivLineIndex(whPo, whPoTm, whPoLines, ecOrderCode, dataSource, ouId, msg);
                } else {
                    // 复用同一套创建Po的逻辑
                    this.createPoDefault(whPo, whPoTm, whPoLines, ouId);
                }
			}
		} else {
			// 复用同一套创建Po的逻辑
			this.createPoDefault(whPo, whPoTm, whPoLines, ouId);
		}
		return msg;
	}

    private ResponseMsg createOdoArchivLineIndex(WhPo whPo, WhPoTransportMgmt whPoTm, List<WhPoLine> whPoLines, String ecOrderCode, String dataSource, Long ouId, ResponseMsg msg) {
        // 查询归档(collect)数据表中的数据
        List<WhOdoArchivIndex> odoArchivIndexList = whOdoArchivIndexManager.findWhOdoArchivIndexByEcOrderCode(ecOrderCode, null, null, ouId);
        if (null == odoArchivIndexList || odoArchivIndexList.isEmpty()) {
            msg.setMsg("odoCollectEmpty,ecOrderCode:{" + ecOrderCode + "}");
            msg.setResponseStatus(0);
            return msg;
        }
        // 要退换货的skuId
        Set<Long> skuIdList = new HashSet<Long>();
        for (WhPoLine poLine : whPoLines) {
            skuIdList.add(poLine.getSkuId());
        }
        List<WhOdoArchivLineIndex> whOdoArchivLineIndexList = null;
        for (WhOdoArchivIndex odoArchivIndex : odoArchivIndexList) {
            if (Constants.WMS3.equals(odoArchivIndex.getDataSource())) {
                // wms3迁移数据直接查找LineIndex记录
                break;
            } else if (null != odoArchivIndex.getIsReturnedPurchase() && !odoArchivIndex.getIsReturnedPurchase()) {
                // 退货入标记为0时,同步出库单明细到collect
                String odoCode = odoArchivIndex.getWmsOdoCode();
                String sysDate = odoArchivIndex.getSysDate();
                // 查找
                List<WhOdoArchivLineIndex> lineIndexList = odoArchivManager.findWhOdoLineArchivByOdoCode(odoCode, ouId, sysDate, ecOrderCode, odoArchivIndex.getDataSource());
                // 保存
                lineIndexList = whOdoArchivIndexManager.saveWhOdoLineArchivListIntoCollect(odoArchivIndex, lineIndexList);
            }
        }
        // 查询collect库中订单下的这些商品明细
        whOdoArchivLineIndexList = whOdoArchivIndexManager.findReturnSkuLine(ecOrderCode, skuIdList, ouId);
        if (null == whOdoArchivLineIndexList || whOdoArchivLineIndexList.isEmpty()) {
            msg.setMsg("not have sku to return,ecOrderCode:" + ecOrderCode);
            msg.setResponseStatus(0);
            return msg;
        }
        // 创建Po的逻辑
        this.createPoDefault(whPo, whPoTm, whPoLines, whOdoArchivLineIndexList, ouId);
        return msg;
    }

    @Override
    public ResponseMsg constructReturnsSkuInventory(List<RcvdCacheCommand> commandList, Long ouId, Long userId, String logId, Boolean isReturns) {
        if (commandList == null || commandList.size() == 0) {
            throw new BusinessException(ErrorCodes.RCVD_CONTAINER_FINISH_ERROR);
        }
        Long asnId = commandList.get(0).getOccupationId();// ASN头ID
        // 获取ASN
        WhAsn asn = this.asnManager.findWhAsnByIdToShard(asnId, ouId);
        if (null == asn) {
            throw new BusinessException(ErrorCodes.OCCUPATION_RCVD_GET_ERROR);
        }
        String occupationCode = asn.getAsnCode();// 占用单据号
        // 更新Po数据集合
        Long poId = asn.getPoId();
        WhPo po = this.poManager.findWhPoByIdToShard(poId, ouId);
        if (null == po) {
            throw new BusinessException(ErrorCodes.PO_RCVD_GET_ERROR);
        }
        Store store = this.getReturnedStore(asn.getStoreId(), isReturns);
        if (store == null) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        Long storeId = store.getId();
        Long customerId=asn.getCustomerId();

        Warehouse wh = this.warehouseManager.findWarehouseById(ouId);

        List<WhAsnLine> saveAsnLineList = new ArrayList<WhAsnLine>();

        List<WhPoLine> savePoLineList = new ArrayList<WhPoLine>();

        // @mender yimin.lu 多容器收货
        Map<Long, String> insideContainerMap = new HashMap<Long, String>();


        // 将数据按照明细ID筛选，统计数目，放到MAP集合中
        Map<Long, Double> lineMap = new HashMap<Long, Double>();

        Map<String, RcvdCacheCommand> uuidRcvdMap = new HashMap<String, RcvdCacheCommand>();

        // 字典表集合
        Set<String> dic1 = new HashSet<String>();// dic1
        Set<String> dic2 = new HashSet<String>();// dic2
        Set<String> dic3 = new HashSet<String>();// dic3
        Set<String> dic4 = new HashSet<String>();// dic4
        Set<String> dic5 = new HashSet<String>();// dic5
        Set<String> dic6 = new HashSet<String>();// dic6
        // @mender yimin
        // 1.保存库存
        // 2.筛选ASN明细数据集合
        for (RcvdCacheCommand cacheInv : commandList) {
            Long lineId = cacheInv.getLineId();
            Long insideContainerId = cacheInv.getInsideContainerId();

            this.packageContainerMap(insideContainerMap, cacheInv);// 封装容器集合

            this.packageLineMap(lineMap, cacheInv);// 封装行集合

            String uuid = this.generateUuid(cacheInv, customerId, storeId);// 生成uuid
            
            // @mender yimin.lu 2017/4/19 key值记录更多的信息【容器ID】
            String asnRcvdLogMaoKey = lineId + "$" + uuid + "$" + insideContainerId;
            if (!uuidRcvdMap.containsKey(asnRcvdLogMaoKey)) {
                uuidRcvdMap.put(asnRcvdLogMaoKey, cacheInv);
            } else {
                // @mender yimin.lu
                RcvdCacheCommand rcvdMapCache = uuidRcvdMap.get(asnRcvdLogMaoKey);

                rcvdMapCache.setSkuBatchCount(rcvdMapCache.getSkuBatchCount() + cacheInv.getSkuBatchCount());

                if (cacheInv.getSnList() != null && cacheInv.getSnList().size() > 0) {
                    if (rcvdMapCache.getSnList() == null) {
                        rcvdMapCache.setSnList(cacheInv.getSnList());
                    } else {
                        rcvdMapCache.getSnList().addAll(cacheInv.getSnList());
                    }
                }
                uuidRcvdMap.put(asnRcvdLogMaoKey, rcvdMapCache);
            }


            // 字典表
            dic1.add(cacheInv.getInvAttr1());
            dic2.add(cacheInv.getInvAttr2());
            dic3.add(cacheInv.getInvAttr3());
            dic4.add(cacheInv.getInvAttr4());
            dic5.add(cacheInv.getInvAttr5());
            dic6.add(cacheInv.getInvType());


        }
        // 库存状态字典表
        Map<Long, String> invStatusMap = this.getInvStatusMap();
        Map<String, List<String>> sysDictionaryList = new HashMap<String, List<String>>();// 字典表集合
        sysDictionaryList.put(Constants.INVENTORY_ATTR_1, new ArrayList<String>(dic1));
        sysDictionaryList.put(Constants.INVENTORY_ATTR_2, new ArrayList<String>(dic2));
        sysDictionaryList.put(Constants.INVENTORY_ATTR_3, new ArrayList<String>(dic3));
        sysDictionaryList.put(Constants.INVENTORY_ATTR_4, new ArrayList<String>(dic4));
        sysDictionaryList.put(Constants.INVENTORY_ATTR_5, new ArrayList<String>(dic5));
        sysDictionaryList.put(Constants.INVENTORY_TYPE, new ArrayList<String>(dic6));
        Map<String, SysDictionary> dicMap = this.generalRcvdManager.findSysDictionaryByRedisExt(sysDictionaryList);

        // @mender yimin.lu
        List<WhSkuInventory> saveInvList = new ArrayList<WhSkuInventory>();// 库存表
        List<WhCarton> saveWhCartonList = new ArrayList<WhCarton>();// 装箱信息表
        List<WhAsnRcvdLogCommand> saveInvLogList = new ArrayList<WhAsnRcvdLogCommand>();// 收货日志表
        List<WhSkuInventorySnCommand> saveSnList = new ArrayList<WhSkuInventorySnCommand>();// SN列表
        Map<Long, String> deTypeMap = new HashMap<Long, String>();// 残次类型
        Map<Long, String> deReasonMap = new HashMap<Long, String>();// 残次原因
        Iterator<Entry<String, RcvdCacheCommand>> uuidRcvdIt = uuidRcvdMap.entrySet().iterator();
        while (uuidRcvdIt.hasNext()) {
            Entry<String, RcvdCacheCommand> uuidRcvdEntry = uuidRcvdIt.next();

            String asnRcvdLogMaoKey = uuidRcvdEntry.getKey();
            String[] keyArray = asnRcvdLogMaoKey.split("\\$");
            String uuid = keyArray[1];

            RcvdCacheCommand cacheInv = uuidRcvdEntry.getValue();
            List<RcvdSnCacheCommand> cacheInvSnList = cacheInv.getSnList();
            Double qtyRcvd = cacheInv.getSkuBatchCount().doubleValue();// 收货数量
            Long lineId = cacheInv.getLineId();// 明细Id
            Long insideContainerId = cacheInv.getInsideContainerId();

            // @mender yimin.lu 2017/5/4 兼容退换货收货
            cacheInv.setOccupationCode(occupationCode);
            this.packageSkuInv(saveInvList, cacheInv, uuid, qtyRcvd, customerId, storeId);// 封装库存信息

            this.packageCarton(saveWhCartonList, cacheInv, qtyRcvd, insideContainerId, asnId, lineId, ouId, userId);// 装箱信息

            this.packageSn(saveSnList, cacheInv, cacheInvSnList, deTypeMap, deReasonMap, ouId, uuid, occupationCode);// sn信息

            this.packageRcvdLog(saveInvLogList, cacheInv, qtyRcvd, cacheInvSnList, deTypeMap, deReasonMap, invStatusMap, dicMap, ouId, uuid, occupationCode, userId);// 收货日志

        }

        // 更新容器
        List<Container> containerList = new ArrayList<Container>();
        Iterator<Entry<Long, String>> containerIt = insideContainerMap.entrySet().iterator();
        while (containerIt.hasNext()) {
            Entry<Long, String> entry = containerIt.next();
            Container container = this.generalRcvdManager.findContainerByIdToShard(entry.getKey(), ouId);
            if (null == container) {
                throw new BusinessException(ErrorCodes.CONTAINER_RCVD_GET_ERROR);
            }
            container.setStatus(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY);
            // @mender yimin.lu 2017/5/4 容器状态
            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
            container.setOperatorId(userId);
            containerList.add(container);
        }

        // 更新ASN明细
        Iterator<Entry<Long, Double>> it = lineMap.entrySet().iterator();
        Double asnCount = Constants.DEFAULT_DOUBLE;
        Map<Long, Double> polineMap = new HashMap<Long, Double>();
        Map<Long, Integer> polineCtnMap = new HashMap<Long, Integer>();
        while (it.hasNext()) {
            Entry<Long, Double> entry = it.next();
            WhAsnLine asnLine = this.asnLineManager.findWhAsnLineByIdToShard(entry.getKey(), ouId);
            if (null == asnLine) {
                throw new BusinessException(ErrorCodes.PARAM_IS_NULL);
            }
            asnLine.setQtyRcvd(asnLine.getQtyRcvd() + entry.getValue());
            asnLine.setModifiedId(userId);
            if (asnLine.getQtyRcvd() >= asnLine.getQtyPlanned()) {
                asnLine.setStatus(PoAsnStatus.ASNLINE_RCVD_FINISH);
            } else {
                asnLine.setStatus(PoAsnStatus.ASNLINE_RCVD);
            }
            // @mender yimin.lu 2017/4/21 更新实际收货的箱数
            Integer asnLineCtnQty = this.getCtnQty(asnId, asnLine.getId(), ouId, containerList);
            asnLine.setCtnRcvd((asnLine.getCtnRcvd() == null ? 0 : asnLine.getCtnRcvd()) + asnLineCtnQty);
            saveAsnLineList.add(asnLine);
            if (polineMap.containsKey(asnLine.getPoLineId())) {
                polineMap.put(asnLine.getPoLineId(), lineMap.get(asnLine.getId()) + entry.getValue());
            } else {
                polineMap.put(asnLine.getPoLineId(), entry.getValue());
            }

            // 箱数统计 @mender yimin.lu 2017/4/21 可能会有误差
            if (polineCtnMap.containsKey(asnLine.getPoLineId())) {
                polineCtnMap.put(asnLine.getPoLineId(), polineCtnMap.get(asnLine.getId()) + asnLineCtnQty);
            } else {
                polineCtnMap.put(asnLine.getPoLineId(), asnLineCtnQty);
            }
            asnCount += entry.getValue();
        }
        // 1.更新ASN明细
        // 2.筛选PO明细数据集合
        asn.setQtyRcvd(asn.getQtyRcvd() + asnCount);
        // asn更新实际箱数 @mender yimin.lu 2017/4/21
        // @mender yimin.lu 通用收货更新实际箱数
        Integer ctnRcvd = this.getCtnQty(asnId, null, ouId, containerList);
        asn.setCtnRcvd((asn.getCtnRcvd() == null ? 0 : asn.getCtnRcvd()) + ctnRcvd);
        // mender yimin.lu 设置完成与关闭节点，在内层方法体封装
        /*
         * if (asn.getQtyRcvd() >= asn.getQtyPlanned()) {
         * asn.setStatus(PoAsnStatus.ASN_RCVD_FINISH); } else { asn.setStatus(PoAsnStatus.ASN_RCVD);
         * } asn.setStopTime(new Date());
         */
        asn.setModifiedId(userId);
        if (asn.getDeliveryTime() == null) {
            asn.setDeliveryTime(new Date());
        }
        if (asn.getStartTime() == null) {
            asn.setStartTime(new Date());
        }
        Iterator<Entry<Long, Double>> poIt = polineMap.entrySet().iterator();
        // 更新PO明细数据集合
        while (poIt.hasNext()) {
            Entry<Long, Double> entry = poIt.next();
            WhPoLine poline = this.poLineManager.findWhPoLineByIdOuIdToShard(entry.getKey(), ouId);
            poline.setQtyRcvd(poline.getQtyRcvd() + entry.getValue());
            if (poline.getQtyRcvd() >= poline.getQtyPlanned()) {
                poline.setStatus(PoAsnStatus.POLINE_RCVD_FINISH);
            } else {
                poline.setStatus(PoAsnStatus.POLINE_RCVD);
            }
            // 更新实际箱数
            poline.setQtyRcvd((poline.getQtyRcvd() == null ? 0 : poline.getQtyRcvd()) + polineCtnMap.get(entry.getKey()));
            poline.setModifiedId(userId);
            savePoLineList.add(poline);
            if (null == poId) {
                poId = poline.getPoId();
            }
        }
        po.setModifiedId(userId);
        po.setQtyRcvd(po.getQtyRcvd() + asnCount);
        // po更新实际箱数 @mender yimin.lu 2017/4/21 TODO 现在箱信息是到ASN维度，如果两个ASN用同一个容器收货，统计数据会有误
        po.setCtnRcvd((po.getCtnRcvd() == null ? 0 : po.getCtnRcvd()) + ctnRcvd);
        if (null == po.getDeliveryTime()) {
            po.setDeliveryTime(new Date());
        }
        if (null == po.getStartTime()) {
            po.setStartTime(new Date());
        }
        // po.setStopTime(new Date());
        
        // 保存到拆库
        try {
            this.generalRcvdManager.saveScanedSkuWhenGeneralRcvdForPda(saveSnList, saveInvList, saveInvLogList, saveAsnLineList, asn, savePoLineList, po, containerList, saveWhCartonList, wh);
        } catch (BusinessException ex) {
            ResponseMsg msg = new ResponseMsg();
            msg.setMsg(ex.getErrorCode() + "");
            msg.setResponseStatus(ResponseMsg.STATUS_ERROR);
            return msg;
        }
        // 关联原单数据
        if(isReturns!=null&&isReturns){
            Store s = this.storeManager.findStoreById(asn.getStoreId());
            if (s.getIsReturnedPurchaseOriginalInvAttr() != null && s.getIsReturnedPurchaseOriginalInvAttr()) {
                try {
                    Map<Long, Double> archivMap = new HashMap<Long, Double>();
                    for (RcvdCacheCommand cacheInv : commandList) {
                        Long archivLineId = cacheInv.getArchivlineId();
                        if (archivMap.containsKey(archivLineId)) {
                            archivMap.put(archivLineId, archivMap.get(archivLineId) + cacheInv.getSkuBatchCount());
                        } else {
                            archivMap.put(archivLineId, (double) cacheInv.getSkuBatchCount());
                        }

                    }
                    Iterator<Entry<Long, Double>> archivLineIt = archivMap.entrySet().iterator();
                    List<WhOdoArchivLineIndex> list = new ArrayList<WhOdoArchivLineIndex>();
                    while (archivLineIt.hasNext()) {
                        Entry<Long, Double> entry = archivLineIt.next();
                        Long archivLineId = entry.getKey();
                        Double count = entry.getValue();


                        WhOdoArchivLineIndex archivLine = this.whOdoArchivLineIndexManager.findByIdToShard(archivLineId, ouId);
                        if (archivLine == null) {
                            throw new BusinessException(ErrorCodes.FEEDBACK_ORIGINAL_ERROR);
                        }
                        archivLine.setReturnedPurchaseQty(count);

                        list.add(archivLine);
                    }
                    this.whOdoArchivLineIndexManager.executeReturns(list);
                } catch (Exception ex) {
                    ResponseMsg msg = new ResponseMsg();
                    msg.setMsg(ErrorCodes.FEEDBACK_RCVD_ERROR + "");
                    msg.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
                    return msg;
                }
            }
        }
        // 反馈数据
        try {
            WhPo shardPo = this.poManager.findWhPoByIdToShard(po.getId(), ouId);
            // @mender yimin.lu 2017/3/7 自动关单逻辑：仓库下PO单关闭要同步到集团下
            // @mender yimin.lu 同步接口调整
            if (PoAsnStatus.PO_CLOSE == shardPo.getStatus()) {
                this.poManager.snycPoToInfo("CLOSE", shardPo, false, savePoLineList);
            } else if (PoAsnStatus.PO_RCVD == shardPo.getStatus()) {
                this.poManager.snycPoToInfo("RCVD", shardPo, false, savePoLineList);
            } else if (PoAsnStatus.PO_RCVD_FINISH == shardPo.getStatus()) {
                this.poManager.snycPoToInfo("RCVD_FINISH", shardPo, false, savePoLineList);
            }
        } catch (Exception ex) {
            ResponseMsg msg = new ResponseMsg();
            msg.setMsg(ErrorCodes.FEEDBACK_RCVD_ERROR + "");
            msg.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
            return msg;
        }
        ResponseMsg msg = new ResponseMsg();
        msg.setMsg("SUCCESS");
        msg.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        return msg;
    }


    private void packageSn(List<WhSkuInventorySnCommand> saveSnList, RcvdCacheCommand cacheInv, List<RcvdSnCacheCommand> rcvdCacheSnList, Map<Long, String> deTypeMap, Map<Long, String> deReasonMap, Long ouId, String uuid, String occupationCode) {
        if (null != rcvdCacheSnList) {
            if (rcvdCacheSnList != null && rcvdCacheSnList.size() > 0) {
                // @mender yimin.lu 2016/10/31 一件商品对应一条SN收货记录
                // @mender yimin.lu 2016/10/28 序列号商品 则会有多条数据；残次品非序列号商品只有一条数据
                for (int i = 0; i < rcvdCacheSnList.size(); i++) {
                    RcvdSnCacheCommand rcvdSn = rcvdCacheSnList.get(i);

                    if (rcvdSn.getDefectTypeId() != null) {

                        // #取得残次类型残次原因的名称。
                        this.packageDefectMap(deTypeMap, deReasonMap, rcvdSn, ouId);
                        WhSkuInventorySnCommand skuInvSn = new WhSkuInventorySnCommand();
                        if (Constants.SERIAL_NUMBER_TYPE_ALL.equals(rcvdSn.getSerialNumberType())) {
                            skuInvSn.setSn(rcvdSn.getSn());
                            skuInvSn.setSerialNumberType(rcvdSn.getSerialNumberType());
                        }
                        skuInvSn.setDefectTypeId(rcvdSn.getDefectTypeId());
                        skuInvSn.setDefectReasonsId(rcvdSn.getDefectReasonsId());
                        skuInvSn.setOccupationCode(occupationCode);
                        skuInvSn.setStatus(Constants.INVENTORY_SN_STATUS_ONHAND);
                        skuInvSn.setDefectWareBarcode(rcvdSn.getDefectWareBarCode());
                        skuInvSn.setOuId(ouId);
                        skuInvSn.setUuid(uuid);
                        skuInvSn.setDefectReasonsName(deReasonMap.get(rcvdSn.getDefectReasonsId()));
                        skuInvSn.setDefectTypeName(deTypeMap.get(rcvdSn.getDefectTypeId()));
                        skuInvSn.setDefectSource(rcvdSn.getDefectSource());
                        saveSnList.add(skuInvSn);
                    } else {
                        if (Constants.SERIAL_NUMBER_TYPE_ALL.equals(rcvdSn.getSerialNumberType())) {
                            WhSkuInventorySnCommand skuInvSn = new WhSkuInventorySnCommand();
                            skuInvSn.setSn(rcvdSn.getSn());
                            skuInvSn.setSerialNumberType(rcvdSn.getSerialNumberType());
                            skuInvSn.setOccupationCode(occupationCode);
                            skuInvSn.setStatus(Constants.INVENTORY_SN_STATUS_ONHAND);
                            skuInvSn.setOuId(ouId);
                            skuInvSn.setUuid(uuid);
                            saveSnList.add(skuInvSn);
                        }
                    }

                }
            }


        }

    }

    private void packageRcvdLog(List<WhAsnRcvdLogCommand> saveInvLogList, RcvdCacheCommand cacheInv, Double qtyRcvd, List<RcvdSnCacheCommand> rcvdCacheSnList, Map<Long, String> deTypeMap, Map<Long, String> deReasonMap, Map<Long, String> invStatusMap,
            Map<String, SysDictionary> dicMap, Long ouId, String uuid,
 String occupationCode, Long userId) {
        List<WhAsnRcvdSnLog> saveSnLogList = null;
        if (null != rcvdCacheSnList) {
            if (rcvdCacheSnList != null && rcvdCacheSnList.size() > 0) {
                saveSnLogList = new ArrayList<WhAsnRcvdSnLog>();
                // @mender yimin.lu 2016/10/31 一件商品对应一条SN收货记录
                // @mender yimin.lu 2016/10/28 序列号商品 则会有多条数据；残次品非序列号商品只有一条数据
                for (int i = 0; i < rcvdCacheSnList.size(); i++) {
                    RcvdSnCacheCommand rcvdSn = rcvdCacheSnList.get(i);

                    if (rcvdSn.getDefectTypeId() != null) {

                        // 插入日志表
                        WhAsnRcvdSnLog whAsnRcvdSnLog = new WhAsnRcvdSnLog();
                        whAsnRcvdSnLog.setSn(rcvdSn.getSn());
                        whAsnRcvdSnLog.setDefectWareBarcode(rcvdSn.getDefectWareBarCode());
                        whAsnRcvdSnLog.setOuId(ouId);
                        whAsnRcvdSnLog.setDefectType(deTypeMap.get(rcvdSn.getDefectTypeId()));
                        whAsnRcvdSnLog.setDefectReasons(deReasonMap.get(rcvdSn.getDefectReasonsId()));

                        saveSnLogList.add(whAsnRcvdSnLog);
                    } else {
                        // 插入收货记录表
                        WhAsnRcvdSnLog whAsnRcvdSnLog = new WhAsnRcvdSnLog();
                        whAsnRcvdSnLog.setSn(rcvdSn.getSn());
                        whAsnRcvdSnLog.setOuId(ouId);
                        saveSnLogList.add(whAsnRcvdSnLog);
                    }

                }
            }


        }
        WhAsnRcvdLogCommand asnRcvdLog = new WhAsnRcvdLogCommand();
        asnRcvdLog.setAsnId(cacheInv.getOccupationId());
        asnRcvdLog.setAsnLineId(cacheInv.getLineId());
        asnRcvdLog.setAsnCode(cacheInv.getOccupationCode());
        Sku sku = this.generalRcvdManager.findSkuByIdToShard(cacheInv.getSkuId(), ouId);
        asnRcvdLog.setSkuCode(sku.getCode());
        asnRcvdLog.setSkuName(sku.getName());
        asnRcvdLog.setQuantity(cacheInv.getSkuBatchCount().longValue());
        // @mender yimin.lu 实际收货数量
        asnRcvdLog.setQtyRcvd(cacheInv.getSkuBatchCount().doubleValue());
        Container container = this.generalRcvdManager.findContainerByIdToShard(cacheInv.getInsideContainerId(), ouId);
        asnRcvdLog.setContainerCode(container.getCode());
        asnRcvdLog.setContainerName(container.getName());
        asnRcvdLog.setMfgDate(cacheInv.getMfgDate());
        asnRcvdLog.setExpDate(cacheInv.getExpDate());
        asnRcvdLog.setBatchNo(cacheInv.getBatchNumber());
        asnRcvdLog.setCountryOfOrigin(cacheInv.getCountryOfOrigin());
        if (cacheInv.getInvStatus() != null) {
            asnRcvdLog.setInvStatus(invStatusMap.get(cacheInv.getInvStatus()));
        }
        if (StringUtils.hasText(cacheInv.getInvType())) {
            SysDictionary dic = dicMap.get(Constants.INVENTORY_TYPE + "_" + cacheInv.getInvType());
            asnRcvdLog.setInvType(dic == null ? cacheInv.getInvType() : dic.getDicLabel());
        }
        if (StringUtils.hasText(cacheInv.getInvAttr1())) {
            SysDictionary dic = dicMap.get(Constants.INVENTORY_ATTR_1 + "_" + cacheInv.getInvAttr1());
            asnRcvdLog.setInvAttr1(dic == null ? cacheInv.getInvAttr1() : dic.getDicLabel());
        }
        if (StringUtils.hasText(cacheInv.getInvAttr2())) {
            SysDictionary dic = dicMap.get(Constants.INVENTORY_ATTR_2 + "_" + cacheInv.getInvAttr2());
            asnRcvdLog.setInvAttr2(dic == null ? cacheInv.getInvAttr1() : dic.getDicLabel());
        }
        if (StringUtils.hasText(cacheInv.getInvAttr3())) {
            SysDictionary dic = dicMap.get(Constants.INVENTORY_ATTR_3 + "_" + cacheInv.getInvAttr3());
            asnRcvdLog.setInvAttr3(dic == null ? cacheInv.getInvAttr3() : dic.getDicLabel());
        }
        if (StringUtils.hasText(cacheInv.getInvAttr4())) {
            SysDictionary dic = dicMap.get(Constants.INVENTORY_ATTR_4 + "_" + cacheInv.getInvAttr4());
            asnRcvdLog.setInvAttr4(dic == null ? cacheInv.getInvAttr4() : dic.getDicLabel());
        }
        if (StringUtils.hasText(cacheInv.getInvAttr1())) {
            SysDictionary dic = dicMap.get(Constants.INVENTORY_ATTR_5 + "_" + cacheInv.getInvAttr5());
            asnRcvdLog.setInvAttr5(dic == null ? cacheInv.getInvAttr5() : dic.getDicLabel());
        }
        asnRcvdLog.setOuId(ouId);
        asnRcvdLog.setCreateTime(new Date());
        asnRcvdLog.setLastModifyTime(new Date());
        asnRcvdLog.setOperatorId(userId);
        if (saveSnLogList != null && saveSnLogList.size() > Constants.DEFAULT_INTEGER) {
            asnRcvdLog.setWhAsnRcvdSnLogList(saveSnLogList);
        }
        saveInvLogList.add(asnRcvdLog);
    }

    private void packageCarton(List<WhCarton> saveWhCartonList, RcvdCacheCommand cacheInv, Double qtyRcvd, Long insideContainerId, Long asnId, Long lineId, Long ouId, Long userId) {
        WhCarton whCarton = new WhCarton();
        whCarton.setAsnId(asnId);
        whCarton.setAsnLineId(lineId);
        whCarton.setSkuId(cacheInv.getSkuId());
        whCarton.setContainerId(insideContainerId);
        whCarton.setExtContainerCode(cacheInv.getInsideContainerCode());
        whCarton.setQuantity(qtyRcvd);
        whCarton.setQtyRcvd(qtyRcvd);
        whCarton.setMfgDate(cacheInv.getMfgDate());
        whCarton.setExpDate(cacheInv.getExpDate());
        whCarton.setBatchNo(cacheInv.getBatchNumber());
        whCarton.setCountryOfOrigin(cacheInv.getCountryOfOrigin());
        whCarton.setInvStatus(cacheInv.getInvStatus());
        whCarton.setInvAttr1(cacheInv.getInvAttr1());
        whCarton.setInvAttr2(cacheInv.getInvAttr2());
        whCarton.setInvAttr3(cacheInv.getInvAttr3());
        whCarton.setInvAttr4(cacheInv.getInvAttr4());
        whCarton.setInvAttr5(cacheInv.getInvAttr5());
        whCarton.setInvType(cacheInv.getInvType());
        whCarton.setOuId(ouId);
        whCarton.setIsCaselevel(false);
        whCarton.setCreateTime(new Date());
        whCarton.setCreatedId(userId);
        whCarton.setLastModifyTime(new Date());
        whCarton.setModifiedId(userId);
        saveWhCartonList.add(whCarton);
    }

    private void packageSkuInv(List<WhSkuInventory> saveInvList, RcvdCacheCommand cacheInv, String uuid, Double qtyRcvd, Long customerId, Long storeId) {
        WhSkuInventory skuInv = new WhSkuInventory();
        BeanUtils.copyProperties(cacheInv, skuInv);
        skuInv.setCustomerId(customerId);
        skuInv.setStoreId(storeId);
        skuInv.setOuId(cacheInv.getOuId());
        skuInv.setUuid(uuid);
        skuInv.setAllocatedQty(Constants.DEFAULT_DOUBLE);
        skuInv.setToBeFilledQty(Constants.DEFAULT_DOUBLE);
        skuInv.setFrozenQty(Constants.DEFAULT_DOUBLE);
        skuInv.setOnHandQty(qtyRcvd);

        saveInvList.add(skuInv);

    }




    private void packageDefectMap(Map<Long, String> deTypeMap, Map<Long, String> deReasonMap, RcvdSnCacheCommand rcvdSn, Long ouId) {

        if (!deTypeMap.containsKey(rcvdSn.getDefectTypeId())) {

            if (Constants.SKU_SN_DEFECT_SOURCE_STORE.equals(rcvdSn.getDefectSource())) {
                StoreDefectType storeDefectType = this.generalRcvdManager.findStoreDefectTypeByIdToGlobal(rcvdSn.getDefectTypeId());
                if (storeDefectType != null) {
                    deTypeMap.put(storeDefectType.getId(), storeDefectType.getName());
                } else {
                    deTypeMap.put(rcvdSn.getDefectTypeId(), rcvdSn.getDefectTypeId() + "");
                }

            } else if (Constants.SKU_SN_DEFECT_SOURCE_WH.equals(rcvdSn.getDefectSource())) {
                WarehouseDefectType warehouseDefectType = this.generalRcvdManager.findWarehouseDefectTypeByIdToShard(rcvdSn.getDefectTypeId(), ouId);
                if (warehouseDefectType != null) {
                    deReasonMap.put(warehouseDefectType.getId(), warehouseDefectType.getName());
                } else {
                    deTypeMap.put(rcvdSn.getDefectTypeId(), rcvdSn.getDefectTypeId() + "");
                }
            }
        }

        if (!deReasonMap.containsKey(rcvdSn.getDefectReasonsId())) {
            if (Constants.SKU_SN_DEFECT_SOURCE_STORE.equals(rcvdSn.getDefectSource())) {
                StoreDefectReasons storeDefectReasons = this.generalRcvdManager.findStoreDefectReasonsByIdToGlobal(rcvdSn.getDefectReasonsId());
                if (storeDefectReasons != null) {
                    deReasonMap.put(rcvdSn.getDefectReasonsId(), storeDefectReasons.getName());
                } else {
                    deReasonMap.put(rcvdSn.getDefectReasonsId(), rcvdSn.getDefectReasonsId() + "");
                }

            } else if (Constants.SKU_SN_DEFECT_SOURCE_WH.equals(rcvdSn.getDefectSource())) {
                WarehouseDefectReasons warehouseDefectReasons = this.generalRcvdManager.findWarehouseDefectReasonsByIdToShard(rcvdSn.getDefectReasonsId(), ouId);
                if (warehouseDefectReasons != null) {
                    deReasonMap.put(rcvdSn.getDefectReasonsId(), warehouseDefectReasons.getName());
                } else {
                    deReasonMap.put(rcvdSn.getDefectReasonsId(), rcvdSn.getDefectReasonsId() + "");
                }
            }
        }



    }


    private void packageLineMap(Map<Long, Double> lineMap, RcvdCacheCommand cacheInv) {
        Long lineId = cacheInv.getLineId();
        if (lineMap.containsKey(lineId)) {
            lineMap.put(cacheInv.getLineId(), lineMap.get(lineId) + cacheInv.getSkuBatchCount());
        } else {
            lineMap.put(cacheInv.getLineId(), cacheInv.getSkuBatchCount().doubleValue());
        }

    }

    private void packageContainerMap(Map<Long, String> insideContainerMap, RcvdCacheCommand cacheInv) {
        if (!insideContainerMap.containsKey(cacheInv.getInsideContainerId())) {
            insideContainerMap.put(cacheInv.getInsideContainerId(), cacheInv.getInsideContainerCode());
        }

    }

    private String generateUuid(RcvdCacheCommand cacheInv, Long customerId, Long storeId) {
        WhSkuInventory skuInv = new WhSkuInventory();
        BeanUtils.copyProperties(cacheInv, skuInv);
        skuInv.setCustomerId(customerId);
        skuInv.setStoreId(storeId);
        skuInv.setOuId(cacheInv.getOuId());
        return this.generateUuid(skuInv);
    }

    private String generateUuid(WhSkuInventory skuInv) {
        // 测试用
        // skuInv.setId((long) Math.random() * 1000000);
        try {
            return SkuInventoryUuid.invUuid(skuInv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<Long, String> getInvStatusMap() {
        List<InventoryStatus> invStatusList = this.inventoryStatusManager.findAllInventoryStatus();
        Map<Long, String> invStatusMap = new HashMap<Long, String>();
        if (invStatusList != null && invStatusList.size() > 0) {
            for (InventoryStatus invStatus : invStatusList) {
                invStatusMap.put(invStatus.getId(), invStatus.getName());
            }
        }
        return invStatusMap;
    }

    private Store getReturnedStore(Long storeId, Boolean isReturns) {
        Store store = this.storeManager.findStoreById(storeId);
        if (store == null) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (isReturns != null && isReturns) {
            if (store.getIsReturnedPurchaseOriginalInvAttr() != null && store.getIsReturnedPurchaseOriginalInvAttr()) {

                if (Constants.STORE_RETURNEDPURCHASESTORE_INBOUND.equals(store.getReturnedPurchaseStore())) {
                    return store;
                } else {
                    // #TODO
                    Store returnedStore = this.storeManager.findStoreByCode(store.getReturnedPurchaseStore());
                    if (returnedStore == null) {
                        throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                    }
                    return returnedStore;
                }
            }
        }
        return store;

    }

    private Integer getCtnQty(Long asnId, Long asnLineId, Long ouId, List<Container> containerList) {
        Integer ctnRcvd = 0;
        // 查询 #TODO 优化
        for (Container c : containerList) {
            WhCartonCommand cartonCommand = new WhCartonCommand();
            cartonCommand.setAsnId(asnId);
            cartonCommand.setContainerId(c.getId());
            cartonCommand.setAsnLineId(asnLineId);
            cartonCommand.setOuId(ouId);
            List<WhCartonCommand> cartonCheckList = this.whCartonManager.findWhCartonByParamExt(cartonCommand);
            if (cartonCheckList == null || cartonCheckList.size() == 0) {
                ctnRcvd++;
            }

        }
        return ctnRcvd;
    }

    @Override
    public String generateExtCode() {
        return this.getUniqueCode(null, Constants.BIPO_MODEL_URL, Constants.WMS_PO_EXT, null, null);
    }

    @Override
    public void createReceivingFinishPoAsn(String packingContainer, Long twoLevelType, String containerCode, String latticNo, Integer target, Long userId, Long ouId) {
        ContainerCommand container = null;
        if (target == 4) {
            // 扫描的出库箱,需要转换为周转箱
            container = containerManager.useOutBoundBoxToContainer(containerCode, twoLevelType, userId, ouId);
        } else {
            container = containerManager.checkContainerStatus(packingContainer, target, ouId);
        }
        // 查找库存
        List<WhSkuInventory> list = whSkuInventoryManager.findContainerSkuInventory(containerCode, latticNo, target, ouId);
        Map<String, List<WhPoLine>> poLineMap = new HashMap<String, List<WhPoLine>>();
        Map<String, Double> qtyMap = new HashMap<String, Double>();
        for (WhSkuInventory inv : list) {
            String odoCode = inv.getOccupationCode();
            if (StringUtils.isEmpty(odoCode)) {
                throw new BusinessException(ErrorCodes.ODO_NOT_FIND);
            }
            List<WhPoLine> poLine = poLineMap.get(odoCode);
            if (null == poLine) {
                poLine = new ArrayList<WhPoLine>();
            }
            // 生成poLine
            WhPoLine line = this.getPoLine(inv, poLine.size() + 1, userId, ouId);
            poLine.add(line);
            poLineMap.put(odoCode, poLine);
            // 记录数量
            Double skuQty = qtyMap.get(odoCode);
            if (null == skuQty) {
                skuQty = Constants.DEFAULT_DOUBLE;
            }
            skuQty += inv.getOnHandQty();
            qtyMap.put(odoCode, skuQty);
        }
        if (target == 1 || target == 3 || target == 4) {
            if (poLineMap.size() != 1) {
                // 含有不同的出库单
                throw new BusinessException(ErrorCodes.SKU_INV_HAVE_MORE_ODO);
            }
        }
        for (Entry<String, List<WhPoLine>> entry : poLineMap.entrySet()) {
            String odoCode = entry.getKey();
            WhOdo odo = odoManager.findByOdoCodeAndOuId(odoCode, ouId);
            if (null == odo) {
                log.error("odo not exists, odoCode:{}", odoCode);
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            // 生成WhPo
            Double skuQty = qtyMap.get(odoCode);
            List<WhPoLine> poLine = poLineMap.get(odoCode);
            WhPo po = this.getWhPo(odo, skuQty, userId, ouId);
            poManager.createReceivingFinishPoAsnToShard(list, po, poLine, container, odoCode, ouId);
        }
    }

    private WhPo getWhPo(WhOdo odo, Double skuQty, Long userId, Long ouId) {
        WhPo po = new WhPo();
        String poCode = this.getUniqueCode();
        String poExtCode = this.generateExtCode();
        if (StringUtil.isEmpty(poCode) || StringUtil.isEmpty(poExtCode)) {
            log.warn("CreatePo warn poCode or poExtCode generateCode is null");
            throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL);
        }
        po.setPoCode(poCode);
        po.setExtCode(poExtCode);
        po.setOuId(ouId);
        po.setCustomerId(odo.getCustomerId());
        po.setStoreId(odo.getStoreId());
        po.setPoType(PoAsnType.POTYPE_1);
        po.setStatus(PoAsnStatus.PO_CLOSE);
        po.setIsIqc(Boolean.FALSE);
        po.setPoDate(new Date());
        po.setEta(new Date());
        po.setDeliveryTime(new Date());
        po.setQtyPlanned(skuQty);
        po.setQtyRcvd(skuQty);
        po.setCtnPlanned(Constants.DEFAULT_INTEGER);
        po.setCtnRcvd(Constants.DEFAULT_INTEGER);
        po.setStartTime(new Date());
        po.setStopTime(new Date());
        po.setIsWms(Boolean.TRUE);
        po.setCreateTime(new Date());
        po.setIsAutoClose(Boolean.TRUE);
        po.setDataSource(Constants.DATA_SOURCE_WMS_ERROR);
        po.setCreatedId(userId);
        po.setLastModifyTime(new Date());
        po.setModifiedId(userId);
        return po;
    }

    private WhPoLine getPoLine(WhSkuInventory inv, int lineNum, Long userId, Long ouId) {
        WhPoLine line = new WhPoLine();
        line.setOuId(ouId);
        line.setLinenum(lineNum);
        line.setSkuId(inv.getSkuId());
        line.setQtyPlanned(inv.getOnHandQty());
        line.setAvailableQty(Constants.DEFAULT_DOUBLE);
        line.setQtyRcvd(inv.getOnHandQty());
        line.setStatus(PoAsnStatus.POLINE_RCVD_FINISH);
        line.setIsIqc(Boolean.FALSE);
        line.setMfgDate(inv.getMfgDate());
        line.setExpDate(inv.getExpDate());
        line.setBatchNo(inv.getBatchNumber());
        line.setCountryOfOrigin(inv.getCountryOfOrigin());
        line.setInvStatus(inv.getInvStatus());
        line.setInvAttr1(inv.getInvAttr1());
        line.setInvAttr2(inv.getInvAttr2());
        line.setInvAttr3(inv.getInvAttr3());
        line.setInvAttr4(inv.getInvAttr4());
        line.setInvAttr5(inv.getInvAttr5());
        line.setInvType(inv.getInvType());
        line.setCreateTime(new Date());
        line.setCreatedId(userId);
        line.setLastModifyTime(new Date());
        line.setModifiedId(userId);
        return line;
    }

}
