package com.baozun.scm.primservice.whoperation.manager.warehouse.carton;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.util.DateUtil;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Service("whCartonManager")
@Transactional
public class WhCartonManagerImpl extends BaseManagerImpl implements WhCartonManager {

    protected static final Logger log = LoggerFactory.getLogger(WhCartonManager.class);

    @Autowired
    private WhCartonDao whCartonDao;
    @Autowired
    private WhAsnLineDao whAsnLineDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private WhAsnDao whAsnDao;

    /**
     * 通过ASN相关信息查询对用拆箱信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCartonCommand> findWhCartonDevanningList(Long asnid, Long asnlineid, Long skuid, Long ouid) {
        return whCartonDao.findWhCartonDevanningList(asnid, asnlineid, skuid, ouid);
    }

    /**
     * 删除已拆商品明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteCarton(WhCartonCommand whCartonCommand) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnLineCommandEditDevanning method begin! logid: " + whCartonCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[whCartonCommand:{}]", whCartonCommand.toString());
        }
        // 查询对应拆箱信息
        WhCarton c = whCartonDao.findWhCatonById(whCartonCommand.getId(), whCartonCommand.getOuId());
        if (null == c) {
            log.warn("deleteCarton WhCarton is null logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.CARTONNULL_ERROR);
        }
        WhAsnLineCommand whAsnLineCommand = whAsnLineDao.findWhAsnLineCommandByIdOuId(c.getAsnLineId(), c.getOuId());
        if (null == whAsnLineCommand) {
            log.warn("deleteCarton asnLine is null logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.ASNLINE_NULL);
        }
        // 验证对应ASNLINE状态是否是未收货状态
        if (!whAsnLineCommand.getStatus().equals(PoAsnStatus.ASNLINE_NOT_RCVD)) {
            log.warn("deleteCarton asnLine status is " + whAsnLineCommand.getStatus() + " error logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.ASNLINE_STATUS_ERROR);
        }
        // 删除对应拆箱信息
        int count = whCartonDao.deleteCartonById(c.getId(), c.getOuId());
        if (count == 0) {
            // 删除失败
            log.warn("deleteCarton WhCarton count=0 logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.DELETE_ERROR);
        }
        // 插入系统日志表
        insertGlobalLog(GLOBAL_LOG_DELETE, c, c.getOuId(), whCartonCommand.getModifiedId(), whAsnLineCommand.getAsnCode(), null);

        // 修改对应po poline asn asnline计划箱数
        WhAsn asn = whAsnDao.findWhAsnById(c.getAsnId(), c.getOuId());
        asn.setCtnPlanned(asn.getCtnPlanned() - 1);// 计划箱数
        asn.setModifiedId(whCartonCommand.getModifiedId());
        int asnCount = whAsnDao.saveOrUpdateByVersion(asn);
        if (asnCount == 0) {
            log.warn("addDevanningList update Asn CtnPlanned error logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        WhPo whPo = whPoDao.findWhPoById(asn.getPoId(), asn.getOuId());
        whPo.setCtnPlanned(whPo.getCtnPlanned() - 1);// 计划箱数
        whPo.setModifiedId(whCartonCommand.getModifiedId());
        int poCount = whPoDao.saveOrUpdateByVersion(whPo);
        if (poCount == 0) {
            log.warn("addDevanningList update Po CtnPlanned error logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        WhAsnLine whAsnLine = whAsnLineDao.findWhAsnLineById(c.getAsnLineId(), asn.getOuId());
        whAsnLine.setCtnPlanned(whAsnLine.getCtnPlanned() - 1);
        whAsnLine.setModifiedId(whCartonCommand.getModifiedId());
        int whAsnLineCount = whAsnLineDao.saveOrUpdateByVersion(whAsnLine);
        if (whAsnLineCount == 0) {
            log.warn("addDevanningList update WhAsnLine CtnPlanned error logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        WhPoLine whPoLine = whPoLineDao.findWhPoLineById(whAsnLine.getPoLineId(), whCartonCommand.getOuId());
        whPoLine.setCtnPlanned(whPoLine.getCtnPlanned() - 1);
        whPoLine.setModifiedId(whCartonCommand.getModifiedId());
        int whPoLineCount = whPoLineDao.saveOrUpdateByVersion(whPoLine);
        if (whPoLineCount == 0) {
            log.warn("addDevanningList update WhPoLine CtnPlanned error logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }

        // 插入系统日志表
        insertGlobalLog(GLOBAL_LOG_UPDATE, asn, asn.getOuId(), asn.getModifiedId(), whPo.getPoCode(), null);// asn
        insertGlobalLog(GLOBAL_LOG_UPDATE, whAsnLine, whAsnLine.getOuId(), whAsnLine.getModifiedId(), asn.getAsnCode(), null);// asnline
        insertGlobalLog(GLOBAL_LOG_UPDATE, whPo, whPo.getOuId(), whPo.getModifiedId(), null, null);// po
        insertGlobalLog(GLOBAL_LOG_UPDATE, whPoLine, whPoLine.getOuId(), whPoLine.getModifiedId(), whPo.getPoCode(), null);// poline
        log.info(this.getClass().getSimpleName() + ".findWhAsnLineCommandEditDevanning method end! logid: " + whCartonCommand.getLogId());
    }

    /**
     * 根据ID+OUID查询对应拆箱信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhCarton findWhCatonById(Long id, Long ouid) {
        return whCartonDao.findWhCatonById(id, ouid);
    }

    /**
     * 新增ASN拆箱明细信息
     * 
     * @throws Exception
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void addDevanningList(WhCartonCommand whCartonCommand) throws Exception {
        log.info(this.getClass().getSimpleName() + ".addDevanningList method begin! logid: " + whCartonCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[whCartonCommand:{}]", whCartonCommand.toString());
        }
        if (null == whCartonCommand.getCartonList()) {
            // 没有新增拆箱商品明细
            log.warn("addDevanningList CartonList is null logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.ADD_CARTONLIST_NULL_ERROR);
        }
        Sku sku = skuDao.findByIdShared(whCartonCommand.getSkuId(), whCartonCommand.getOuId());
        if (null == sku) {
            // 商品不存在
            log.warn("addDevanningList Sku is null logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.SKU_IS_NULL_BY_ID, new Object[] {whCartonCommand.getSkuId()});
        }
        // 验证SKU是否可用状态
        if (!sku.getLifecycle().equals(Sku.LIFECYCLE_NORMAL)) {
            // 判断商品是否可用
            log.warn("addDevanningList Sku lifecycle not normal error logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.SKU_IS_LIFECYCLE_ERROR, new Object[] {sku.getCode(), sku.getName()});
        }
        List<WhCartonCommand> cartonList = whCartonCommand.getCartonList();
        // 获取这单可拆数量
        WhAsnLineCommand usableDevanningQty = whAsnLineDao.findWhAsnLineCommandEditDevanning(whCartonCommand.getAsnLineId(), whCartonCommand.getAsnId(), whCartonCommand.getOuId(), whCartonCommand.getSkuId());
        // 验证本次拆箱数量是否超过可拆箱数量 每箱数量是否相同等数量信息
        checkAddCartonListQty(cartonList, usableDevanningQty.getUsableDevanningQty(), whCartonCommand.getLogId());

        int binQtySum = 0;// 总箱数
        // 插入拆箱信息
        for (WhCartonCommand cc : cartonList) {
            // 获取2级容器类型
            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(Long.valueOf(cc.getCategoryId()), whCartonCommand.getOuId());
            if (null == c2c) {
                log.warn("addDevanningList Container2ndCategory is null CategoryId() " + cc.getCategoryId() + " error logid: " + whCartonCommand.getLogId());
                throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
            }
            // 计算一共多少箱数
            int binQty = new BigDecimal(cc.getBcdevanningQty()).divide(new BigDecimal(cc.getQuantity())).intValue();
            binQtySum = binQtySum + binQty;// 计算总箱数
            for (int i = 0; i < binQty; i++) {
                // 通过HUB接口获取容器编码
                String code = getContainerCode(c2c, whCartonCommand.getLogId());
                // 先新增一条容器
                Container c = new Container();
                c.setCode(code);
                c.setName(c2c.getCategoryName());
                c.setOneLevelType(c2c.getOneLevelType());// 一级容器
                c.setTwoLevelType(c2c.getId());// 二级容器
                c.setOuId(whCartonCommand.getOuId());
                c.setCreateTime(new Date());
                c.setLastModifyTime(new Date());
                c.setOperatorId(whCartonCommand.getCreatedId());
                long tcount = containerDao.insert(c);
                if (tcount == 0) {
                    log.warn("addDevanningList insert Container error logid: " + whCartonCommand.getLogId());
                    throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
                }
                // 插入系统日志表
                insertGlobalLog(GLOBAL_LOG_INSERT, c, c.getOuId(), c.getOperatorId(), null, null);
                // 插入ASN拆箱表
                WhCarton carton = new WhCarton();
                carton.setAsnId(whCartonCommand.getAsnId());
                carton.setAsnLineId(whCartonCommand.getAsnLineId());
                carton.setSkuId(whCartonCommand.getSkuId());
                carton.setContainerId(c.getId());// 容器
                carton.setOuId(whCartonCommand.getOuId());
                carton.setBatchNo(cc.getBatchNo());// 批次号
                carton.setCountryOfOrigin(cc.getCountryOfOrigin());// 原产地
                carton.setQuantity(cc.getQuantity());// 数量
                if (!StringUtil.isEmpty(cc.getMfgDateStr())) {
                    // 生产日期
                    carton.setMfgDate(DateUtil.getDateFormat(cc.getMfgDateStr(), "yyyy-MM-dd"));
                }
                if (!StringUtil.isEmpty(cc.getExpDateStr())) {
                    // 失效日期
                    carton.setExpDate(DateUtil.getDateFormat(cc.getExpDateStr(), "yyyy-MM-dd"));
                }
                carton.setInvStatus(cc.getInvStatus());// 库存状态
                carton.setInvType(cc.getInvType());// 库存类型
                // 库存属性1-5
                carton.setInvAttr1(cc.getInvAttr1());
                carton.setInvAttr2(cc.getInvAttr2());
                carton.setInvAttr3(cc.getInvAttr3());
                carton.setInvAttr4(cc.getInvAttr4());
                carton.setInvAttr5(cc.getInvAttr5());
                carton.setIsCaselevel(true);// 是否caselevel
                carton.setCreatedId(whCartonCommand.getCreatedId());
                carton.setModifiedId(whCartonCommand.getCreatedId());
                carton.setCreateTime(new Date());
                carton.setLastModifyTime(new Date());
                long ccount = whCartonDao.insert(carton);
                if (ccount == 0) {
                    log.warn("addDevanningList insert Carton error logid: " + whCartonCommand.getLogId());
                    throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
                }
                // 插入系统日志表
                insertGlobalLog(GLOBAL_LOG_INSERT, carton, carton.getOuId(), carton.getCreatedId(), null, null);
            }
            // 修改对应po poline asn asnline计划箱数
            WhAsn asn = whAsnDao.findWhAsnById(whCartonCommand.getAsnId(), whCartonCommand.getOuId());
            asn.setCtnPlanned(asn.getCtnPlanned() + binQtySum);// 计划箱数
            asn.setModifiedId(whCartonCommand.getCreatedId());
            int asnCount = whAsnDao.saveOrUpdateByVersion(asn);
            if (asnCount == 0) {
                log.warn("addDevanningList update Asn CtnPlanned error logid: " + whCartonCommand.getLogId());
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

            WhPo whPo = whPoDao.findWhPoById(asn.getPoId(), asn.getOuId());
            whPo.setCtnPlanned(whPo.getCtnPlanned() + binQtySum);// 计划箱数
            whPo.setModifiedId(whCartonCommand.getCreatedId());
            int poCount = whPoDao.saveOrUpdateByVersion(whPo);
            if (poCount == 0) {
                log.warn("addDevanningList update Po CtnPlanned error logid: " + whCartonCommand.getLogId());
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

            WhAsnLine whAsnLine = whAsnLineDao.findWhAsnLineById(whCartonCommand.getAsnLineId(), asn.getOuId());
            whAsnLine.setCtnPlanned(whAsnLine.getCtnPlanned() + binQtySum);
            whAsnLine.setModifiedId(whCartonCommand.getCreatedId());
            int whAsnLineCount = whAsnLineDao.saveOrUpdateByVersion(whAsnLine);
            if (whAsnLineCount == 0) {
                log.warn("addDevanningList update WhAsnLine CtnPlanned error logid: " + whCartonCommand.getLogId());
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

            WhPoLine whPoLine = whPoLineDao.findWhPoLineById(whAsnLine.getPoLineId(), whCartonCommand.getOuId());
            whPoLine.setCtnPlanned(whPoLine.getCtnPlanned() + binQtySum);
            whPoLine.setModifiedId(whCartonCommand.getCreatedId());
            int whPoLineCount = whPoLineDao.saveOrUpdateByVersion(whPoLine);
            if (whPoLineCount == 0) {
                log.warn("addDevanningList update WhPoLine CtnPlanned error logid: " + whCartonCommand.getLogId());
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }

            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_UPDATE, asn, asn.getOuId(), asn.getModifiedId(), whPo.getPoCode(), null);// asn
            insertGlobalLog(GLOBAL_LOG_UPDATE, whAsnLine, whAsnLine.getOuId(), whAsnLine.getModifiedId(), asn.getAsnCode(), null);// asnline
            insertGlobalLog(GLOBAL_LOG_UPDATE, whPo, whPo.getOuId(), whPo.getModifiedId(), null, null);// po
            insertGlobalLog(GLOBAL_LOG_UPDATE, whPoLine, whPoLine.getOuId(), whPoLine.getModifiedId(), whPo.getPoCode(), null);// poline
        }
        log.info(this.getClass().getSimpleName() + ".addDevanningList method begin! end: " + whCartonCommand.getLogId());
    }

    /***
     * 验证本次拆箱数量是否超过可拆箱数量 每箱数量是否相同等数量信息
     */
    private static void checkAddCartonListQty(List<WhCartonCommand> cartonList, Double usableDevanningQty, String logid) {
        Double qty = 0.0;
        for (WhCartonCommand carton : cartonList) {
            // 验证本次拆箱数量是否>0
            if (carton.getBcdevanningQty() <= 0) {
                log.warn("addDevanningList carton.getBcdevanningQty() <= 0.0 error logid: " + logid);
                throw new BusinessException(ErrorCodes.ADD_CARTONLIST_BCDEVANNINGQTY_ERROR);
            }
            // 验证每箱商品数量是否>0
            if (carton.getQuantity() <= 0) {
                log.warn("addDevanningList carton.getQuantity() <= 0.0 error logid: " + logid);
                throw new BusinessException(ErrorCodes.ADD_CARTONLIST_QUANTITY_ERROR);
            }
            // 验证每箱数量是否相同
            if (carton.getBcdevanningQty() % carton.getQuantity() != 0.0) {
                log.warn("addDevanningList carton.getBcdevanningQty() % carton.getQuantity() != 0.0 error logid: " + logid);
                throw new BusinessException(ErrorCodes.ADD_CARTONLIST_BINQTY_ERROR, new Object[] {carton.getBcdevanningQty(), carton.getQuantity(), carton.getBinQty()});
            }
            qty = qty + carton.getBcdevanningQty();// 累加本次拆箱商品数量
        }
        if (qty.compareTo(usableDevanningQty) > 0) {
            // 本次总拆箱商品数量大于可拆商品数量
            log.warn("addDevanningList qty > usableDevanningQty error logid: " + logid);
            throw new BusinessException(ErrorCodes.ADD_CARTONLIST_QTY_ERROR, new Object[] {qty, usableDevanningQty});
        }
    }

    /***
     * 通过HUB接口获取容器编码
     * 
     * @param c2c
     * @return
     */
    private String getContainerCode(Container2ndCategory c2c, String logid) {
        log.info("Interface codeManager generateCode is start,Param 1: {},Param: 2 {}, Param 3: {}, Param 4: {}, Param 5: {}  start logid: " + logid, Constants.WMS, Constants.CONTAINER_MODEL_URL, null, c2c.getPrefix(), c2c.getSuffix());
        boolean b = true;
        String code = null;
        try {
            for (int i = 1; i <= 5; i++) {
                // 每次获取5次 5次都失败直接抛错
                code = codeManager.generateCode(Constants.WMS, Constants.CONTAINER_MODEL_URL, c2c.getCodeGenerator(), c2c.getPrefix(), c2c.getSuffix());
                if (null == code) {
                    // 为空记录失败次数
                    log.warn("addDevanningList Interface codeManager generateCode code is null count: " + i + " logid: " + logid);
                    continue;
                } else {
                    return code;
                }
            }
            if (b) {
                // 5次获取失败
                // 编码生成器接口异常
                log.warn("addDevanningList Interface codeManager generateCode code is null count: 5 logid: " + logid);
                throw new BusinessException(ErrorCodes.CODE_INTERFACE_REEOR);
            }
        } catch (Exception e) {
            // 编码生成器接口异常
            e.printStackTrace();
            log.warn("addDevanningList Interface codeManager generateCode error logid: " + logid);
            throw new BusinessException(ErrorCodes.CODE_INTERFACE_REEOR);
        }
        log.info("Interface codeManager generateCode is start,Param 1: {},Param: 2 {}, Param 3: {}, Param 4: {}, Param 5: {}  end logid: " + logid, Constants.WMS, Constants.CONTAINER_MODEL_URL, null, c2c.getPrefix(), c2c.getSuffix());
        return code;
    }

    @Override
    public List<WhCartonCommand> findWhCartonByParamExt(WhCartonCommand whCartonCommand) {
        return this.whCartonDao.findWhCartonByParamExt(whCartonCommand);
    }

}
