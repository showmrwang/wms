package com.baozun.scm.primservice.whoperation.manager.warehouse;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionOutBoundDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;

/**
 * 功能查询和维护:出库
 * @author Administrator
 *
 */
@Service("whFunctionOutBoundManager")
@Transactional
public class WhFunctionOutBoundManagerImpl extends BaseManagerImpl implements WhFunctionOutBoundManager {


    @Autowired
    private WhFunctionOutBoundDao whFunctionOutBoundDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhFunctionOutBound findByFunctionIdExt(Long functionId, Long ouId) {
        WhFunctionOutBound whFunctionOutBound = whFunctionOutBoundDao.findByFunctionIdExt(functionId, ouId);
        /*
        {
            //TODO 测试 设置功能配置
            if(null == whFunctionOutBound){
                whFunctionOutBound = new WhFunctionOutBound();
            }
            whFunctionOutBound.setFunctionId(111L);
            whFunctionOutBound.setOuId(ouId);
            //功能模块多选：复核、称重、交接
            whFunctionOutBound.setFunctionModule("handover,weighting");
            //多选：首单复核、副品复核、按单复核、按箱复核（复核）
            whFunctionOutBound.setCheckingMode("CHECK_BY_CONTAINER,CHECK_FIRST_ODO");
            //扫描模式 逐件扫描 数量扫描
            whFunctionOutBound.setScanPattern(2);
            //是否提示商品库存属性
            whFunctionOutBound.setIsTipInvAttr(false);
            //是否扫描商品库存属性
            whFunctionOutBound.setIsScanInvAttr(false);
            //是否允许引进出库箱号
            whFunctionOutBound.setIsCheckingToOutboundbox(false);
            //是否自动生成出库箱号
            whFunctionOutBound.setIsAutoGenerateOutboundbox(true);
            //是否扫描出库箱号
            whFunctionOutBound.setIsScanOutboundbox(true);
            //扫描商品是否拍照
            whFunctionOutBound.setIsScanSkuPhotograph(true);
            //扫描拍照属性
            whFunctionOutBound.setScanInvAttrPhotograph(null);
        }
        */
        return whFunctionOutBound;
    }

}
