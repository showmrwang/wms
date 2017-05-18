package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuLocationCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuLocationDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSkuLocation;


@Service("whSkuLocationManager")
@Transactional
public class WhSkuLocationManagerImpl implements WhSkuLocationManager {
    @Autowired
    private WhSkuLocationDao whSkuLocationDao;
    @Autowired
    private GlobalLogManager globalLogManager;
    @Autowired
    private WhLocationDao locationDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private  SkuMgmtDao skuMgmtDao;

    public static final Logger log = LoggerFactory.getLogger(WhSkuLocationManager.class);

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WhSkuLocationCommand> findWhSkuLocationListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> paraMap) {
        return this.whSkuLocationDao.findListByQueryMapWithPageExt(page, sorts, paraMap);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuLocationCommand> findWhSkuLocationCommandListByParamToShard(WhSkuLocationCommand skuLocationCommand) {
        return this.whSkuLocationDao.findSkuLocationToShard(skuLocationCommand);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg insert(WhSkuLocation skuLocation) {
        log.info(this.getClass().getSimpleName() + ".insert method begin!");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".insert method params:{}", skuLocation);
        }
        try {
            // 校验数据
            checkForSaveOrUpdate(skuLocation);
            //插入商品库位关系表
            long i=this.whSkuLocationDao.insert(skuLocation);
            if (i == 0) {
                throw new BusinessException(ErrorCodes.INSERT_DATA_ERROR);
            }
            // 插入日志
            this.insertGlobalLog(skuLocation.getModifiedId(), new Date(), skuLocation.getClass().getSimpleName(), skuLocation.toString(), Constants.GLOBAL_LOG_INSERT, skuLocation.getOuId());
        }catch(Exception e){
            log.error(this.getClass().getSimpleName() + ".insert method error:{}", e);
            if (e instanceof BusinessException) {
                return getResponseMsg(((BusinessException) e).getErrorCode() + "", ResponseMsg.DATA_ERROR, null);
            } else {
                return getResponseMsg("insert Error,please try again!", ResponseMsg.STATUS_ERROR, null);
            }
        }
        return getResponseMsg("insert success!", ResponseMsg.STATUS_SUCCESS, null);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg update(WhSkuLocation skuLocation) {
        log.info(this.getClass().getSimpleName() + ".update method begin!");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".update method params:{}", skuLocation);
        }
        try {
            // 校验数据
            checkForSaveOrUpdate(skuLocation);
            int i = this.whSkuLocationDao.updateByVersion(skuLocation);
            if (i == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入日志
            this.insertGlobalLog(skuLocation.getModifiedId(), new Date(), skuLocation.getClass().getSimpleName(), skuLocation.toString(), Constants.GLOBAL_LOG_UPDATE, skuLocation.getOuId());
        } catch (Exception e) {
            log.error(this.getClass().getSimpleName() + ".update method error:{}", e);
            if (e instanceof BusinessException) {
                return getResponseMsg(((BusinessException) e).getErrorCode() + "", ResponseMsg.DATA_ERROR, null);
            } else {
                return getResponseMsg("update Error,please try again!", ResponseMsg.STATUS_ERROR, null);
            }
        }

        return getResponseMsg("update success!", ResponseMsg.STATUS_SUCCESS, null);
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public ResponseMsg deleteByBatch(WhSkuLocationCommand command) {
        log.info(this.getClass().getSimpleName() + ".deleteByBatch method begin!");
        if (log.isDebugEnabled()) {
            log.debug("method deleteByBatch method params:{}", command);
        }
        for (Long id : command.getIdList()) {
            try {
                WhSkuLocation skuLocation = this.whSkuLocationDao.findByIdOuId(id, command.getOuId());
                if (null == skuLocation) {
                    throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                }
                int i = this.whSkuLocationDao.deleteByIdOuId(id, command.getOuId());
                if (i == 0) {
                    throw new BusinessException(ErrorCodes.DELETE_DATA_ERROR);
                }
                // 插入日志
                this.insertGlobalLog(skuLocation.getModifiedId(), new Date(), skuLocation.getClass().getSimpleName(), skuLocation.toString(), Constants.GLOBAL_LOG_DELETE, skuLocation.getOuId());
            } catch (Exception e) {
                log.error(this.getClass().getSimpleName() + ".deleteByBatch method error:{}", e);
                if (e instanceof BusinessException) {
                    return getResponseMsg(((BusinessException) e).getErrorCode() + "", ResponseMsg.DATA_ERROR, null);
                } else {
                    return getResponseMsg("delete Error,please try again!", ResponseMsg.STATUS_ERROR, null);
                }
            }

        }
        return getResponseMsg("delete success!", ResponseMsg.STATUS_SUCCESS, null);
    }

    /**
     * 用于插入日志操作
     * 
     * @param userId
     * @param modifyTime
     * @param objectType
     * @param modifiedValues
     * @param type
     * @param ouId
     */
    private void insertGlobalLog(Long userId, Date modifyTime, String objectType, String modifiedValues, String type, Long ouId) {
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setModifiedId(userId);
        gl.setModifyTime(modifyTime);
        gl.setObjectType(objectType);
        gl.setModifiedValues(modifiedValues);
        gl.setType(type);
        gl.setOuId(ouId);
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".insertGlobalLog method params:{}", gl);
        }
        globalLogManager.insertGlobalLog(gl);

    }

    /**
     * 返回值设定
     * 
     * @param message
     * @param responseStatus
     * @param reasonStatus
     * @return
     */
    private ResponseMsg getResponseMsg(String message, Integer responseStatus, Integer reasonStatus) {
        ResponseMsg rm = new ResponseMsg();
        rm.setMsg(message);
        if (null != reasonStatus) {
            rm.setReasonStatus(reasonStatus);
        }
        if (null != responseStatus) {
            rm.setResponseStatus(responseStatus);
        }
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".getResponseMsg method returns:{}", rm);
        }
        return rm;
    }

    /**
     * 保存或编辑时校验数据
     * 
     * @param skuLocation
     */
    private void checkForSaveOrUpdate(WhSkuLocation skuLocation) {
        if(null == skuLocation.getLocationId() || null == skuLocation.getOuId()) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        //静态库位，如果绑定的一个商品且是不允许混放的商品，则该静态库位不允许再绑定其他商品
        List<WhSkuLocationCommand> list = whSkuLocationDao.findByOuIdLocationId(skuLocation.getLocationId(), skuLocation.getOuId());  //查询
        if(list.size() >0 && 1 == list.size()) {
            for(WhSkuLocationCommand skulo:list) {
                if(skulo.getLocationIsStatic()) {  //如果是静态库位
                    SkuMgmt skuMgmt = skuMgmtDao.findSkuMgmtBySkuIdShared(skulo.getSkuId(), skulo.getOuId());  //查询已经绑定的一个商品
                    if(!skuMgmt.getIsMixAllowed()) {  //如果不允许混放，则不能继续混放商品
                        throw new BusinessException(ErrorCodes.lOCATION_NO_MIX);
                    }
                    //如果静态库位绑定的商品是多个，或者第一个是允许混放的商品,则可以继续绑定混放属性一致，允许混放的商品
                    //查询要绑定商品的额外属性
                    SkuMgmt skut = skuMgmtDao.findSkuMgmtBySkuIdShared(skuLocation.getSkuId(), skuLocation.getOuId());
                    if(!skut.getIsMixAllowed()) {
                        //要绑定的商品是不允许混放的，不能绑定
                        throw new BusinessException(ErrorCodes.SKU_NO_MIX);
                    }
                    //查询该静态库位下已经绑定,允许混放商品的额外属性
                    for(WhSkuLocationCommand whSku:list) {
                         SkuMgmt mgmt = skuMgmtDao.findSkuMgmtBySkuIdShared(whSku.getSkuId(), whSku.getOuId());
                         if(!mgmt.getMixAttr().equals(skut.getMixAttr())) {
                                throw new BusinessException(ErrorCodes.lOCATION_NO_MIX_ATTR); //静态库位绑定的所有允许混放的商品混放属性必须一致
                         }
                    }
                }
            }
         }
        
        // 查询库位
        Location location = this.locationDao.findByIdExt(skuLocation.getLocationId(), skuLocation.getOuId());
        if (null == location) {
            log.error(this.getClass().getSimpleName() + ".update method error:{}", new BusinessException(ErrorCodes.CHECK_DATA_ERROR));
            throw new BusinessException(ErrorCodes.LOCATION_EXPIRY_ERROR);
        }
        // 校验数据是否过期
        if (BaseModel.LIFECYCLE_NORMAL != location.getLifecycle()) {
            log.error(this.getClass().getSimpleName() + ".update method error:{}", new BusinessException(ErrorCodes.LOCATION_EXPIRY_ERROR));
            throw new BusinessException(ErrorCodes.LOCATION_EXPIRY_ERROR);
        }
        // 获得库位的最大混放数量
        int maxMixCount = location.getIsMixStacking() ? location.getMixStackingNumber() : 1;
        // 查询商品
        Sku sku = this.skuDao.findByIdShared(skuLocation.getSkuId(), skuLocation.getOuId());
        // 校验商品是否过期
        if (null == sku) {
            log.error(this.getClass().getSimpleName() + ".update method error:{}", new BusinessException(ErrorCodes.CHECK_DATA_ERROR));
            throw new BusinessException(ErrorCodes.SKU_EXPIRY_ERROR);
        }
        if (BaseModel.LIFECYCLE_NORMAL != sku.getLifecycle()) {
            log.error(this.getClass().getSimpleName() + ".update method error:{}", new BusinessException(ErrorCodes.SKU_EXPIRY_ERROR));
            throw new BusinessException(ErrorCodes.SKU_EXPIRY_ERROR);
        }
        
        // 查询商品-库位关系表
        WhSkuLocation search = new WhSkuLocation();
        search.setOuId(skuLocation.getOuId());
        search.setLocationId(skuLocation.getLocationId());
        // 库位已经混放的商品数
        long mixcount = this.whSkuLocationDao.findListCountByParam(search);
        // 校验库位混放的数量不能大于库位设置的混放数量
        if (mixcount > maxMixCount) {
            log.error(this.getClass().getSimpleName() + ".update method error:{}", new BusinessException(ErrorCodes.LOCATION_MIXCOUNT_ERROR));
            throw new BusinessException(ErrorCodes.LOCATION_MIXCOUNT_ERROR);
        }
        search.setSkuId(skuLocation.getSkuId());
        if (null == skuLocation.getId()) {
            // 校验数据的唯一性
            long count = this.whSkuLocationDao.findListCountByParam(search);
            if (count > 0) {
                log.error(this.getClass().getSimpleName() + ".update method error:{}", new BusinessException(ErrorCodes.DATA_DUPLICATION_ERROR));
                throw new BusinessException(ErrorCodes.DATA_DUPLICATION_ERROR);
            }
        } else {
            search.setId(skuLocation.getId());
            // 是否修改过库位编码
            long count = this.whSkuLocationDao.findListCountByParam(search);
            // 修改过库位编码时需要校验数据的唯一性
            if (count == 0) {
                search.setId(null);
                long dupCount = this.whSkuLocationDao.findListCountByParam(search);
                if (dupCount > 0) {
                    log.error(this.getClass().getSimpleName() + ".update method error:{}", new BusinessException(ErrorCodes.DATA_DUPLICATION_ERROR));
                    throw new BusinessException(ErrorCodes.DATA_DUPLICATION_ERROR);
                }
            }
        }
    }


    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhSkuLocation findByIdOuId(Long id, Long ouId) {
        return this.whSkuLocationDao.findByIdOuId(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuLocation> findByLocationIdOuId(Long locatinId, Long ouId) {
        WhSkuLocation search = new WhSkuLocation();
        search.setOuId(ouId);
        search.setLocationId(locatinId);
        search.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
        return this.whSkuLocationDao.findListByParam(search);
    }

//    /**
//     * @author lijun.shen
//     */
//    @Override
//    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
//    public List<WhSkuLocation> getBindingSku(WhSkuLocation whSkuLocation) {
//        List<WhSkuLocation>  list = whSkuLocationDao.findBindingSku(whSkuLocation);
//        return list;
//    }
}
