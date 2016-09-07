package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.auth.OperationUnitManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.BiPoManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.StoreManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WarehouseManager;
import com.baozun.scm.primservice.whoperation.model.auth.OperationUnit;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;

/**
 * 查询PoAsn相关数据
 * 
 * @author bin.hu
 * 
 */
@Service("selectPoAsnManagerProxy")
public class SelectPoAsnManagerProxyImpl implements SelectPoAsnManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(SelectPoAsnManagerProxy.class);

    @Autowired
    private PoManager poManager;
    @Autowired
    private BiPoManager biPoManager;
    @Autowired
    private AsnManager asnManager;
    @Autowired
    private PoLineManager poLineManager;
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private AsnLineManager asnLineManager;
    @Autowired
    private BiPoLineManager biPoLineManager;
    @Autowired
    private StoreManager storeManager;
    @Autowired
    private OperationUnitManager operationUnitManager;
    @Autowired
    private WarehouseManager warehouseManager;

    /**
     * 
     * 查询po单列表(带分页)
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    @Override
    public Pagination<WhPoCommand> findWhPoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return poManager.findListByQueryMapWithPageExtByShard(page, sorts, params);
    }

    /**
     * 
     * 查询bipo单列表(带分页)
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    @Override
    public Pagination<BiPoCommand> findBiPoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return biPoManager.findListByQueryMapWithPageExtByInfo(page, sorts, params);
    }

    /**
     * 通过asnextCode查询对应数据 ASN预约时模糊查询对应数据
     */
    @Override
    public List<WhAsnCommand> findWhAsnListByAsnExtCode(String asnExtCode, Integer[] statuses, Long ouid) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnListByAsnExtCode method begin!");
        if (null == asnExtCode) {
            throw new BusinessException(ErrorCodes.PARAM_IS_NULL, new Object[] {"asnExtCode"});
        }
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".findWhAsnListByAsnExtCode params:asnExtCode:{},status:{},ouid:{}", asnExtCode, statuses, ouid);
        }
        return asnManager.findWhAsnListByAsnExtCode(asnExtCode, statuses, ouid);
    }

    /**
     * 通过id+ou_id 查询PO单信息
     */
    @Override
    public WhPo findWhPoById(Long id, Long ouId) {
        log.info(this.getClass().getSimpleName() + ".findWhPoById method begin!");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".findWhPoById method params:[id:{},ouId:{}]", id, ouId);
        }
        WhPo whpo = null == ouId ? poManager.findWhPoByIdToInfo(id, ouId) : poManager.findWhPoByIdToShard(id, ouId);
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".findWhPoById method returns {}!", whpo);
        }
        return whpo;
    }

    @Override
    public BiPo findBiPoById(Long id) {
        log.info(this.getClass().getSimpleName() + ".findBiPoById method begin!");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".findWhPoById method params:{}", id);
        }
        BiPo bipo = biPoManager.findBiPoById(id);
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".findBiPoById method returns {}!", bipo);
        }
        return bipo;
    }

    /**
     * 查询PO单明细行 包括保存和未保存的数据
     */
    @Override
    public Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType) {
        log.info(this.getClass().getSimpleName() + ".findPoLineListByQueryMapWithPageExt method begin!");
        Pagination<WhPoLineCommand> whPoLineCommandList = null;
        if (null == sourceType) {
            sourceType = Constants.SHARD_SOURCE;
        }
        // 判断读取那个库的数据
        if (sourceType == Constants.SHARD_SOURCE) {
            // 拆分库
            whPoLineCommandList = poLineManager.findListByQueryMapWithPageExtByShard(page, sorts, params);
        }
        if (sourceType == Constants.INFO_SOURCE) {
            // 公共库
            whPoLineCommandList = poLineManager.findListByQueryMapWithPageExtByInfo(page, sorts, params);
        }
        log.info(this.getClass().getSimpleName() + ".findPoLineListByQueryMapWithPageExt method end!");
        return whPoLineCommandList;
    }

    /**
     * 查询PO单明细行 包括保存和未保存的数据
     */
    @Override
    public Pagination<BiPoLineCommand> findBiPoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return biPoLineManager.findListByQueryMapWithPageExt(page, sorts, params);
    }

    /**
     * 拆分BIPO时查询PO单明细行
     */
    @Override
    public Pagination<BiPoLineCommand> findListByQueryMapWithPageExtForCreateSubPo(Page page, Sort[] sorts, Map<String, Object> params) {
        return biPoLineManager.findListByQueryMapWithPageExtForCreateSubPo(page, sorts, params);
    }

    /**
     * 通过id+ou_id 查询PO单信息
     */
    @Override
    public WhPoLineCommand findWhPoLineCommandById(Long id, Long ouId) {
        log.info(this.getClass().getSimpleName() + ".findWhPoLineById method begin!");
        // 查询拆库内信息
        WhPoLineCommand whpoLine = poLineManager.findPoLineCommandbyIdToShard(id, ouId);
        log.info(this.getClass().getSimpleName() + ".findWhPoLineById method end!");
        return whpoLine;
    }

    /**
     * 查询ASN单列表(带分页)
     */
    @Override
    public Pagination<WhAsnCommand> findWhAsnListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnListByQueryMapWithPageExt method begin!");
        Pagination<WhAsnCommand> whAsnCommandList = null;
        if (null == sourceType) {
            sourceType = Constants.SHARD_SOURCE;
        }
        whAsnCommandList = asnManager.findListByQueryMapWithPageExtByShard(page, sorts, params);
        log.info(this.getClass().getSimpleName() + ".findWhAsnListByQueryMapWithPageExt method end!");
        return whAsnCommandList;
    }

    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息
     */
    @Override
    public List<WhPoCommand> findWhPoListByExtCode(String extCode, List<Integer> statusList, List<Long> customerList, List<Long> storeList, Long ouid, Integer linenum) {
        log.info(this.getClass().getSimpleName() + ".findWhPoListByExtCode method begin!");
        return poManager.findWhPoListByExtCodeToShard(extCode, statusList, customerList, storeList, ouid, linenum);
    }

    @Override
    public WhAsnCommand findWhAsnCommandById(Long id, Long ouId) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnById method begin!");
        return asnManager.findWhAsnCommandByIdToShard(id, ouId);
    }

    /**
     * ASNLINE 列表(带分页)
     */
    @Override
    public Pagination<WhAsnLineCommand> findAsnLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType) {
        log.info(this.getClass().getSimpleName() + ".findAsnLineListByQueryMapWithPageExt method begin!");
        Pagination<WhAsnLineCommand> whAsnLineCommandList = asnLineManager.findListByQueryMapWithPageExtByShard(page, sorts, params);
        return whAsnLineCommandList;
    }

    @Override
    public WhAsnLineCommand findWhAsnLineCommandById(Long id, Long ouId) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnLineById method begin!");
        return this.asnLineManager.findWhAsnLineCommandByIdToShard(id, ouId);
    }

    @Override
    public long getSkuCountInAsnBySkuId(Long asnId, Long ouId, Long skuId) {
        log.info(this.getClass().getSimpleName() + ".getSkuCountInAsnBySkuId method begin!");
        if (log.isDebugEnabled()) {
            log.debug("params: [asnId:{},ouId:{},skuId:{}]", asnId, ouId, skuId);
        }
        if (null == asnId) {
            log.error("SelectPoAsnManagerProxy.getSkuCountInAsnBySkuId,params AsnId is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        if (null == skuId) {
            log.error("SelectPoAsnManagerProxy.getSkuCountInAsnBySkuId,params skuId is null exception");
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }

        if (null == ouId) {
            log.error("SelectPoAsnManagerProxy.getSkuCountInAsnBySkuId,params ouId is null exception");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        WhAsnLine asnline = new WhAsnLine();
        asnline.setAsnId(asnId);
        asnline.setOuId(ouId);
        asnline.setSkuId(skuId);
        List<WhAsnLine> lineList = this.asnLineManager.findListByParamExt(asnline);
        long count = 0;
        for (WhAsnLine line : lineList) {
            count += line.getQtyPlanned();
        }
        log.info(this.getClass().getSimpleName() + ".getSkuCountInAsnBySkuId method END!");
        return count;
    }

    @Override
    public List<WhAsnLine> findWhAsnLineByAsnId(Long asnId, Long ouId) {
        return this.asnLineManager.findWhAsnLineByAsnIdOuIdToShard(asnId, ouId);
    }

    @Override
    public BiPo findBiPoByPoCode(String poCode) {
        return this.biPoManager.findBiPoByPoCode(poCode);
    }

    @Override
    public BiPoCommand findBiPoCommandById(Long id) {
        return this.biPoManager.findBiPoCommandById(id);
    }

    @Override
    public BiPoCommand findBiPoCommandByPoCode(String poCode) {
        return this.biPoManager.findBiPoCommandByPoCode(poCode);
    }

    @Override
    public BiPoLineCommand findBiPoLineCommandById(Long id) {
        return this.biPoLineManager.findBiPoLineCommandById(id);
    }

    @Override
    public BiPoLine findBiPoLineById(Long id) {
        return this.biPoLineManager.findBiPoLineById(id);
    }

    @Override
    public WhPoCommand findWhPoCommandById(Long id, Long ouId) {
        log.info(this.getClass().getSimpleName() + ".findWhPoById method begin!");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".findWhPoById method params:[id:{},ouId:{}]", id, ouId);
        }
        WhPoCommand whpo = null;
        if (null == ouId) {
            // 查询基本库内信息
            whpo = poManager.findWhPoCommandByIdToInfo(id, ouId);
        } else {
            // 查询拆库内信息
            whpo = poManager.findWhPoCommandByIdToShard(id, ouId);
        }
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".findWhPoById method returns {}!", whpo);
        }
        return whpo;
    }

    @Override
    public Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateSubPo(Page page, Sort[] sorts, Map<String, Object> paraMap, Integer infoSource) {
        return this.poLineManager.findPoLineListByQueryMapWithPageExtForCreateSubPoToInfo(page, sorts, paraMap);
    }

    @Override
    public Integer returnReceiptMode(WhAsnCommand whCommand) {
        // 查看店铺上的预收货模式，1为总数，2为总箱数，3为商品总数，优先级高于仓库
        boolean configureStore = false;
        boolean configureWh = false;
        Integer receiptMode = -1;
        if (whCommand != null && whCommand.getStoreId() != null) {
            Store s = storeManager.getStoreById(whCommand.getStoreId());
            if (null == s) {
                log.error("SelectPoAsnManagerProxy.returnReceiptMode,object store is null exception");
                throw new BusinessException(ErrorCodes.OBJECT_IS_NULL);
            }
            receiptMode = s.getGoodsReceiptMode();
            if (null == receiptMode) {
                configureStore = true;
            }
        } else {
            log.error("SelectPoAsnManagerProxy.returnReceiptMode,WhAsn has no Store");
            throw new BusinessException(ErrorCodes.WH_ASN_STORE_EMPTY, new Object[] {whCommand.getAsnExtCode()});
        }
        // 查看仓库上的预收货模式，1为总数，2为总箱数，3为商品总数
        if (configureStore) {
            if (whCommand != null && whCommand.getOuId() != null) {
                OperationUnit ou = operationUnitManager.findOperationUnitById(whCommand.getOuId());
                if (null == ou) {
                    log.error("SelectPoAsnManagerProxy.returnReceiptMode,OperationUnit not find By Id " + whCommand.getOuId());
                    throw new BusinessException(ErrorCodes.OBJECT_IS_NULL);
                }
                Warehouse wh = warehouseManager.findWarehouseByCode(ou.getCode());
                if (null == wh) {
                    log.error("SelectPoAsnManagerProxy.returnReceiptMode,Warehouse not find By Code " + ou.getCode());
                    throw new BusinessException(ErrorCodes.OBJECT_IS_NULL);
                }
                receiptMode = wh.getGoodsReceiptMode();
                if (null == receiptMode) {
                    configureWh = true;
                }
            }
        }

        // 两个都未维护，提示维护相应功能
        if (configureWh) {
            log.error("SelectPoAsnManagerProxy.returnReceiptMode,Store and warehouse not config asnPreReceipt mode");
            throw new BusinessException(ErrorCodes.STORE_WAREHOUSE_IS_CONFIG);
        }
        return receiptMode;
    }

    @Override
    public WhAsn findWhAsnById(Long id, Long ouId) {
        return this.asnManager.findWhAsnByIdToShard(id, ouId);
    }

    @Override
    public void checkWhAsnLineBySkuId(List<WhAsnLine> whAsnLineList, Long skuId, Long ouId, String logId) {
        boolean errorFlag = false;
        if (log.isInfoEnabled()) {
            log.info("checkWhAsnLineBySkuId start, params whAsnLineList is:[{}], ouId is:[{}], logId is:[{}],skuId is :[{}] ", new Object[] {whAsnLineList.toString(), ouId, logId, skuId});
        }
        if (null == skuId) {
            log.error("params skuId is null exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (null == ouId) {
            log.error("params ouId is null exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if(whAsnLineList.size() <=0 ){
            log.error("params whAsnLineList is null exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        for (WhAsnLine whAsnLine : whAsnLineList) {
            if(skuId.equals(whAsnLine.getSkuId())){
                errorFlag = true;
                break;
                //throw new BusinessException(ErrorCodes.SKU_NOT_FOUND_IN_ASN);
            }else{
                continue;
            }
        }
        if(!errorFlag){
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND_IN_ASN);
        }
        if (log.isInfoEnabled()) {
            log.info("checkWhAsnLineBySkuId end, params whAsnLineList is:[{}], ouId is:[{}], logId is:[{}],skuId is :[{}] ", new Object[] {whAsnLineList.toString(), ouId, logId, skuId});
        }
    }

    @Override
    public Pagination<WhAsnLineCommand> findAsnLineListByQueryMapWithPageExtForCreateAsn(Page page, Sort[] sorts, Map<String, Object> paraMap, Integer shardSource) {
        return this.asnLineManager.findAsnLineListByQueryMapWithPageExtForCreateAsn(page, sorts, paraMap);
    }

    @Override
    public Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExtForCreateAsn(Page page, Sort[] sorts, Map<String, Object> paraMap, Integer infoSource) {
        return this.poLineManager.findPoLineListByQueryMapWithPageExtForCreateAsnToShard(page, sorts, paraMap);
    }

    @Override
    public List<WhAsnCommand> findAsnListByStatus(int status, Long ouId,List<Long> customerList,List<Long> storeList) {
        return this.asnManager.findAsnListByStatus(status, ouId,customerList,storeList);
    }

    @Override
    public BiPo findBiPoByExtCodeStoreId(String extCode, Long storeId) {
        return this.biPoManager.findBiPoByExtCodeStoreId(extCode, storeId);
    }
}
