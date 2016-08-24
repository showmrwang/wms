package com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.command.pda.putaway.PdaManMadePutawayCommand;
import com.baozun.scm.primservice.whoperation.constant.ManMadePutawayCacheConstants;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

import lark.common.annotation.MoreDB;

/**
 * 
 * @author shenlijun
 *
 */
@Service("pdaManmadePutawayCacheManager")
@Transactional
public class PdaManmadePutawayCacheManagerImpl extends BaseManagerImpl implements PdaManmadePutawayCacheManager {
    protected static final Logger log = LoggerFactory.getLogger(PdaManmadePutawayCacheManagerImpl.class);

    @Autowired
    WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private CacheManager cacheManager;


    /**
     * 缓存 容器对应的sku信息
     * 
     * @author lijun.shen
     * @param pdaManMadePutawayCommand
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhSkuInventory> manMadePutawayCacheSku(PdaManMadePutawayCommand pdaManMadePutawayCommand) {
        Long containerId = pdaManMadePutawayCommand.getContainerId();
        Long ouId = pdaManMadePutawayCommand.getOuId();
        WhSkuInventory inventory = new WhSkuInventory();
        inventory.setOuId(ouId);
        if (pdaManMadePutawayCommand.getIsOutContainerInv()) {
            inventory.setOuterContainerId(containerId);
        } else {
            inventory.setInsideContainerId(containerId);
        }

        // 查询出容器对应的sku有哪些
        List<WhSkuInventory> list = whSkuInventoryDao.findWhSkuInventoryByPramas(inventory);

        if (null == list || 0 == list.size()) {
            log.error("manMadePutawayCacheSku  inventory not found error!, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SKU, new Object[] {containerId});
        }
        cacheManager.setMapObject(ManMadePutawayCacheConstants.MANMADE_PUTAWAY_CONTAINER_SKU_CACHE, containerId.toString(), list, ManMadePutawayCacheConstants.CACHE_ONE_DAY);
        List<WhSkuInventory> listcache = cacheManager.getMapObject(ManMadePutawayCacheConstants.MANMADE_PUTAWAY_CONTAINER_SKU_CACHE, containerId.toString());
        System.out.println(listcache.size());
        if (log.isInfoEnabled()) {
            log.info("manMadePutawayCacheSku end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return list;
    }


    @Override
    public List manMadePutawayCacheBinId(Long containerId, Long ouId) {
        WhSkuInventory inventory = new WhSkuInventory();
        inventory.setOuId(ouId);
        inventory.setOuterContainerId(containerId);
        List<WhSkuInventory> list = whSkuInventoryDao.findSkuInventoryByOutContainerId(inventory);
        if (null == list || 0 == list.size()) {
            log.error("manMadePutawayCacheSku  inventory not found error!, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_INSIDE_CONTAINER_ID, new Object[] {containerId});
        }
        cacheManager.setMapObject(ManMadePutawayCacheConstants.MANMADE_PUTAWAY_INSIDE_CONTAINER_ID_CACHE, containerId.toString(), list, ManMadePutawayCacheConstants.CACHE_ONE_DAY);
        List cacheInsideContainerList = cacheManager.getMapObject(ManMadePutawayCacheConstants.MANMADE_PUTAWAY_INSIDE_CONTAINER_ID_CACHE, containerId.toString());
        System.out.println(cacheInsideContainerList.size());
        if (log.isInfoEnabled()) {
            log.info("manMadePutawayCacheBinId end, contianerId is:[{}], ouId is:[{}], logId is:[{}]", containerId, ouId, logId);
        }
        return list;
    }



}
