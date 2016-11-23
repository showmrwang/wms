package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.work.OperatioLineStatisticsCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaManmadePutawayCacheManagerImpl;

@Service("pdaPickingWorkCacheManager")
@Transactional
public class PdaPickingWorkCacheManagerImpl extends BaseManagerImpl implements PdaPickingWorkCacheManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaManmadePutawayCacheManagerImpl.class);
    @Autowired
    private CacheManager cacheManager;
    
    /***
     * 提示小车
     * @param operatorId
     * @return
     */
    @Override
    public Long pdaPickingWorkTipOutContainer(Long operatorId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is start");
        Long tipOuterContainerId = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operatorId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Set<Long> outerContainerIds = operatorLine.getOuterContainers();   //所有小车ids
        if(outerContainerIds.size() == 0) {
            throw new BusinessException(ErrorCodes.OUT_CONTAINER_IS_NO_NULL);   //推荐小车不能为空
        }
        for(Long id:outerContainerIds) {
            if(null != id) {
                tipOuterContainerId = id;
                break;
            }
        }
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is end");
        return tipOuterContainerId;
    }

    /***
     * 提示出库箱
     * @param operatorId
     * @return
     */
    @Override
    public Long pdaPickingWorkTipoutbounxBox(Long operatorId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is start");
        Long tipOutbounxBoxId = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operatorId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Set<Long> outbounxBoxIds = operatorLine.getOutbounxBoxs();
        if(outbounxBoxIds.size() == 0) {
            throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL);   //推荐出库箱不能为空
        }
        for(Long id:outbounxBoxIds) {
            if(null != id) {
                tipOutbounxBoxId = id;
                break;
            }
        }
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipoutbounxBox is end");
        return tipOutbounxBoxId;
    }

    /***
     * 提示周转箱
     * @param operatorId
     * @return
     */
    @Override
    public Long pdaPickingWorkTipOutBound(Long operatorId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutBound is start");
        Long turnoverBoxId = null;
        OperatioLineStatisticsCommand operatorLine = cacheManager.getObject(CacheConstants.OPERATIONLINE_STATISTICS + operatorId.toString());
        if(null == operatorLine) {
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        Set<Long> turnoverBoxIds = operatorLine.getTurnoverBoxs();
        if(turnoverBoxIds.size() == 0) {
            throw new BusinessException(ErrorCodes.TURNOVER_BOX_IS_NO_NULL);   //推荐周转箱不能为空
        }
        for(Long id:turnoverBoxIds) {
            if(null != id) {
                turnoverBoxId = id;
                break;
            }
        }
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutBound is end");
        return turnoverBoxId;
    }

    
}
