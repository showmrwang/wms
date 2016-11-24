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
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaManmadePutawayCacheManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;
import com.baozun.utilities.type.StringUtil;

@Service("pdaPickingWorkCacheManager")
@Transactional
public class PdaPickingWorkCacheManagerImpl extends BaseManagerImpl implements PdaPickingWorkCacheManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaManmadePutawayCacheManagerImpl.class);
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private OutBoundBoxTypeDao outBoundBoxTypeDao;
    
    /***
     * 提示小车
     * @param operatorId
     * @return
     */
    @Override
    public String pdaPickingWorkTipOutContainer(Long operatorId,Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is start");
        String tipOuterContainer = null;
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
                Container container = containerDao.findByIdExt(id, ouId);
                if(null == container) {
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
                // 验证容器Lifecycle是否有效
                if (!container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                    continue;
                }
                // 验证容器状态是否是待上架
                if (!(container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE))) {
                    continue;
                }
                tipOuterContainer = container.getCode();
                break;
            }
        }
        if(StringUtil.isEmpty(tipOuterContainer)) {
            throw new BusinessException(ErrorCodes.OUT_CONTAINER_IS_NOT_NORMAL);
        }
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is end");
        return tipOuterContainer;
    }

    /***
     * 提示出库箱
     * @param operatorId
     * @return
     */
    @Override
    public String pdaPickingWorkTipoutboundBox(Long operatorId,Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutContainer is start");
        String outbounxBox = null;
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
                Container container = containerDao.findByIdExt(id, ouId);
                if(null == container) {
                    throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                }
                // 验证容器Lifecycle是否有效
                if (!container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                    continue;
                }
                // 验证容器状态是否是待上架
                if (!(container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE))) {
                    continue;
                }
                outbounxBox = container.getCode();
                break;
            }
        }
        if(StringUtil.isEmpty(outbounxBox)) {
            throw new BusinessException(ErrorCodes. OUT_BOUNDBOX_IS_NOT_NORMAL);
        }
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipoutbounxBox is end");
        return outbounxBox;
    }

    /***
     * 提示周转箱
     * @param operatorId
     * @return
     */
    @Override
    public String pdaPickingWorkTipTurnoverBox(Long operatorId,Long ouId) {
        // TODO Auto-generated method stub
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutBound is start");
        String turnoverBox = null;
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
                OutBoundBoxType o = outBoundBoxTypeDao.findByIdExt(id, ouId);
                if(null == o) {
                    throw new BusinessException(ErrorCodes.OUT_BOUNX_BOX_IS_NO_NULL );
                }
                // 验证容器Lifecycle是否有效
                if (!o.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_USABLE)) {
                    continue;
                }
                turnoverBox = o.getCode();
                break;
            }
        }
        log.info("PdaPickingWorkCacheManagerImpl pdaPickingWorkTipOutBound is end");
        return turnoverBox;
    }

    
}
