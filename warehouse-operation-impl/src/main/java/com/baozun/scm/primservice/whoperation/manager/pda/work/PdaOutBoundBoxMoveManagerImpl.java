package com.baozun.scm.primservice.whoperation.manager.pda.work;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ContainerStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationInvVolumeWeightCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.LocationRecommendResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ScanSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipLocationCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipScanSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuLocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.BoxInventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.constant.WhContainerType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternType;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerAssistDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryTobefilledDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache.PdaPutawayCacheManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.TipSkuDetailProvider;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.statis.InventoryStatisticManager;
import com.baozun.scm.primservice.whoperation.manager.pda.putaway.PdaManMadePutawayManager;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationInvVolumeWieghtManager;
import com.baozun.scm.primservice.whoperation.manager.rule.WhLocationRecommendManager;
import com.baozun.scm.primservice.whoperation.manager.system.SysDictionaryManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.InventoryStatusManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.WhFunctionPutAwayManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.ContainerAssist;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutboundboxMove;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionPutAway;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventoryTobefilled;
import com.baozun.scm.primservice.whoperation.util.StringUtil;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleCubeCalculator;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;


/**
 * @author zhaozili
 *
 */
@Service("pdaOutBoundBoxMoveManager")
@Transactional
public class PdaOutBoundBoxMoveManagerImpl extends BaseManagerImpl implements PdaOutBoundBoxMoveManager{

    protected static final Logger log = LoggerFactory.getLogger(PdaOutBoundBoxMoveManagerImpl.class);
    

    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
//    @Autowired
//    private WhAsnDao whAsnDao;
    @Autowired
    private CacheManager cacheManager;
    
//    @Autowired
//    private PdaPutawayCacheManager pdaPutawayCacheManager;
//    @Autowired
//    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
//    @Autowired
//    private WhPoDao whPoDao;
//    @Autowired
//    private WhCartonDao whCartonDao;
    @Autowired
    private InventoryStatusManager inventoryStatusManager;
    @Autowired
    private WhSkuDao whSkuDao;
//    @Autowired
//    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private WhLocationDao locationDao;
//    @Autowired
//    private ContainerAssistDao containerAssistDao;
//    @Autowired
//    private RuleManager ruleManager;
//    @Autowired
//    private WhLocationRecommendManager whLocationRecommendManager;
//    @Autowired
//    private UomDao uomDao;
//    @Autowired
//    private WhSkuInventoryLogManager whSkuInventoryLogManager;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
//    @Autowired
//    private WhFunctionPutAwayManager whFunctionPutAwayManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
//    @Autowired
//    private PdaManMadePutawayManager pdaManMadePutawayManager;
//    @Autowired
//    private WhSkuLocationDao whSkuLocationDao;
//    @Autowired
//    private StoreDao storeDao;
//    @Autowired
//    private WhLocationInvVolumeWieghtManager whLocationInvVolumeWieghtManager;
//    @Autowired
//    private WhSkuInventoryTobefilledDao whSkuInventoryTobefilledDao;
//    @Autowired
//    private InventoryStatisticManager inventoryStatisticManager;
    @Autowired
    private WhCheckingDao whCheckingDao;
    
    
    
    
    
    
    /***
     * 扫描源容器
     * @param containerCode
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    @Override
    public ScanResultCommand scanContainer(String containerCode,Integer movePattern, Long ouId,String logId,Long userId) {
        // TODO Auto-generated method stub
        log.info("PdaOutBoundBoxMoveManagerImpl scanContainer is start"); 
        if (log.isInfoEnabled()) {
            log.info("PdaOutBoundBoxMoveManagerImpl scanContainer param , ouId is:[{}],  containerCode is:[{}]", ouId,  containerCode);
        }
        ScanResultCommand srCommand = new ScanResultCommand();
        
        	
//            ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
//            if (null == containerCmd) {
//                // 容器信息不存在
//                log.error("container is not exists, logId is:[{}]", logId);
//                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
//            }
//            // 验证容器状态是否可用
//            if (!containerCmd.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
//                log.error("container lifecycle is not normal, logId is:[{}]", logId);
//                throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
//            }
            //判断出库箱是否满足拆箱条件
        	//1、必须拣货完成或者播种完成的出库箱才能拆分（判断条件已经产生了待复核数据）
        	WhCheckingCommand checkingCommand = whCheckingDao.findWhCheckingByOutboundboxCode(containerCode, ouId);
        	if(null == checkingCommand || !checkingCommand.getStatus().equals(CheckingStatus.NEW)){
        		throw new BusinessException(ErrorCodes.OUT_BOUND_BOX_NOT_MOVE_CONDITION);
        	}
        	//根据出库箱查询对应的库存信息
        	List<WhSkuInventoryCommand> orgBoxList = whSkuInventoryDao.findOutboundboxInventory(containerCode, ouId);
        	//根据出库箱查询不到库存信息
        	if(CollectionUtils.isEmpty(orgBoxList)){
        		throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_INV_ERROR, new Object[] {containerCode});
        	}
        	//cacheContainerInventoryStatistics(orgBoxList, userId, ouId, logId, containerCmd, srCmd, putawayPatternDetailType, outerContainerCode);
        	//统计分析库存数据
        	BoxInventoryStatisticResultCommand isCmd = cacheBoxInventoryStatistics(orgBoxList, userId, ouId, logId,containerCode);
        	//2、如果是出库箱功能功能参数是部分移动,箱内的库存属性必须唯一才能移动
        	if(movePattern.equals(Constants.MOVE_PATTERN_PART) && isCmd.getOutBoundBoxSkuIdSkuAttrIdQtys().get(containerCode).get(containerCode)>1){
        		throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_INV_ERROR, new Object[] {containerCode});
        	}
        	//保存容器库存的统计分析数据
        	cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCode, isCmd, CacheConstants.CACHE_ONE_DAY);
        	//保存容器缓存
        	cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY, containerCode, orgBoxList, CacheConstants.CACHE_ONE_DAY);
//        	//随机获取一个sku提示扫描
//        	String tipSkuAttrId = getTipSkuFromBox(containerCode, isCmd.getOutBoundBoxSkuIdSkuAttrIds(),  logId);
//	        Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
//	        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
//	        if (null == skuCmd) {
//	            log.error("sku is not found error, logId is:[{}]", logId);
//	            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
//	        }
//	        //获取商品属性明细信息
//	        tipSkuDetailAspect(srCommand, tipSkuAttrId,  isCmd.getOutBoundBoxSkuIdSkuAttrIdQtys().get(containerCode),logId); 
//	        srCommand.setNeedTipSku(true);
//	        srCommand.setNeedTipSkuDetail(true);
//	        srCommand.setTipSkuBarcode(skuCmd.getBarCode());//提示sku
//	        srCommand.setSkuId(skuId);
	        log.info("PdaOutBoundBoxMoveManagerImpl scanContainer is  end"); 

            return srCommand;   //下一步扫库位 
        }
    /***
     * 扫描目标容器
     * @param containerCode
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
    @Override
    public ScanResultCommand scanTargetContainer(String targetContainerCode,Long ouId,String logId,Long userId,String sourceBoxCode,Warehouse warehouse) {
    	log.info("PdaOutBoundBoxMoveManagerImpl scanTargetContainer is start");
    	ScanResultCommand srCommand = new ScanResultCommand();
    	List<WhSkuInventoryCommand> targetBoxList = whSkuInventoryDao.findOutboundboxInventory(targetContainerCode, ouId);
    	if(CollectionUtils.isNotEmpty(targetBoxList)){
    		//如果目标出库箱已有库存则出库单号与原始出库箱/货格库存保持一致，且目标出库箱也是拣货或播种完成状态
    		List<WhSkuInventoryCommand> sourceBoxList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, sourceBoxCode);
    		if(CollectionUtils.isEmpty(sourceBoxList)){
        		throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {sourceBoxCode});
        	}
    		if(!sourceBoxList.get(0).getOccupationCode().equalsIgnoreCase(targetBoxList.get(0).getOccupationCode())){
    			throw new BusinessException(ErrorCodes.OUT_BOUND_BOX_ODO_CODE_NOT_MATCH);
    		}
    	}else{
    		//扫描新出库箱时，如果是空的出库箱需要看仓库配置是否管理耗材，如果管理耗材需要扣减耗材库存。空的出库箱默认满足拆分移动条件
    		if(warehouse.getIsMgmtConsumableSku()){
    			//
    		}
    	}
    	//获取源容器中的库存统计信息
    	BoxInventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, sourceBoxCode);
    	if(null == isCmd){
    		throw new BusinessException(ErrorCodes.CONTAINER_INVENTORY_STATISTIC_ERROR,new Object[] {sourceBoxCode});
    	}
    	//随机获取一个sku提示扫描
    	String tipSkuAttrId = getTipSkuFromBox(sourceBoxCode, isCmd.getOutBoundBoxSkuIdSkuAttrIds(),  logId);
        Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
        if (null == skuCmd) {
            log.error("sku is not found error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
        }
        //获取商品属性明细信息
        tipSkuDetailAspect(srCommand, tipSkuAttrId,  isCmd.getOutBoundBoxSkuIdSkuAttrIdQtys().get(sourceBoxCode),logId); 
        srCommand.setNeedTipSku(true);
        srCommand.setNeedTipSkuDetail(true);
        srCommand.setTipSkuBarcode(skuCmd.getBarCode());//提示sku
        srCommand.setSkuId(skuId);
    	log.info("PdaOutBoundBoxMoveManagerImpl scanTargetContainer is end");
    	
    	return srCommand;
    }
    
    /**
     * 出库箱拆分:扫描sku商品
     * @param isScanSkuSn
     * @param insideContainerCode
     * @param skuCmd
     * @param funcId
     * @param ouId
     * @param userId
     * @param logId
     * @param warehouse
     * @return
     */
      public ScanResultCommand boxMoveScanSku(Boolean isScanSkuSn,String insideContainerCode,WhSkuCommand skuCmd, 
    		   Long funcId, Long ouId, Long userId, String logId,Warehouse warehouse,Integer scanPattern){
          log.info("PdaSysSuggestPutwayManagerImpl splitPutwayScanSku is start");
          ScanResultCommand srCmd = new ScanResultCommand();
          //srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
          ContainerCommand ocCmd = null;
          ContainerCommand icCmd = null;
         // ContainerCommand icCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
//          if (null == icCmd) {
//              log.error("sys guide splitContainer putaway check san sku, inside container is null error, logId is:[{}]", logId);
//              throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
//          }
//          Long insideContainerId = icCmd.getId();
//          if(!StringUtils.isEmpty(outerContainerCode)) {
//              ocCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
//              outerContainerId = ocCmd.getId();
//          }
          
          //1、获取容器内库存
          List<WhSkuInventoryCommand> invList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, insideContainerCode);
          if (null == invList || 0 == invList.size()) {
              srCmd.setCacheExists(false);// 缓存信息不存在
//              invList = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventoryAndStatistic(icCmd, ouId, logId);
          }
          BoxInventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerCode);
          if (null == isCmd) {
//              isCmd = pdaPutawayCacheManager.sysSuggestSplitPutawayCacheInventoryStatistic(putawayPatternType,userId, icCmd, ouId, logId, outerContainerCode, putawayPatternDetailType);
          }
          ContainerStatisticResultCommand csrCmd = null;
//          if(null != ocCmd) {
//              csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, ocCmd.getId().toString());
//              if (null == csrCmd) {
////                      csrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInsideContainerStatistic(ocCmd, ouId, logId);
//              }
//          }
//          Long locationId = loc.getId();
          Double scanQty = skuCmd.getScanSkuQty();
          String barCode = skuCmd.getBarCode();
          // 1.获取功能配置
//          WhFunctionPutAway putawyaFunc = new WhFunctionPutAway();//whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);
//          if (null == putawyaFunc) {
//              log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
//              throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
//          }
          //Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
          // 1.判断当前商品是否扫完、是否提示下一个库位、容器或上架
          Map<String, Map<String, Long>> insideContainerSkuAttrIdsQty = isCmd.getOutBoundBoxSkuIdSkuAttrIdQtys();   //内部容器唯一sku总件数
          Map<String, Set<String>> insideContainerSkuAttrIds = isCmd.getOutBoundBoxSkuIdSkuAttrIds();    //内部容器对应唯一sku
          Map<String, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = isCmd.getOutBoundBoxSkuAttrIdsSnDefect();  //内部容器唯一sku对应所有残次条码
          Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(insideContainerCode);
          Map<String, Set<Long>> insideContainerSkuIds = isCmd.getOutBoundBoxSkuIds();
          Map<String, Map<Long, Long>> insideContainerSkuIdsQty = isCmd.getOutBoundBoxSkuIdQtys();
          Long sId = null;
          Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(barCode, logId);
          Set<Long> icSkuIds = insideContainerSkuIds.get(insideContainerCode);
          Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(insideContainerCode);
          boolean isSkuExists = false;
          Integer cacheSkuQty = 1;
          Integer icSkuQty = 1;
          for(Long cacheId : cacheSkuIdsQty.keySet()){
              if(icSkuIds.contains(cacheId)){
                  isSkuExists = true;
              }
              if(true == isSkuExists){
                  sId = cacheId;
                  cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
                  icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
                  break;
              }
          }
//          if(false == isSkuExists){
//              log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocCmd.getId(), icCmd.getId(), sId, logId);
//              throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
//          }
//          if(cacheSkuQty > 1 && cacheSkuQty <= icSkuQty){
//              if(0 != (icSkuQty%cacheSkuQty)){
//                  // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
//                  log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
//                  throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
//              }
//          }
//          skuCmd.setId(sId);
//          skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
//          SkuRedisCommand cacheSkuCmd = skuRedisManager.findSkuMasterBySkuId(sId, ouId, logId);
//          if (null == cacheSkuCmd) {
//              log.error("sku is not found error, logId is:[{}]", logId);
//              throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
//          }
          // 查询库位上已有商品
//          List<WhSkuInventoryCommand> locationSkuList = whSkuInventoryDao.findWhSkuInvCmdByLocation(ouId,locationId);
//          List<WhSkuInventoryCommand> whskuList = new ArrayList<WhSkuInventoryCommand>();
//          List<WhSkuInventoryCommand> list = splitPutwayCacheInventory(icCmd,ouId,logId);
//          for(WhSkuInventoryCommand whSkuInvCmd:list) {
//              String skuInvAttrId = SkuCategoryProvider.getSkuAttrIdByInv(whSkuInvCmd);
//              String skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuCmd.getId(), skuCmd.getInvType(),skuCmd.getInvBatchNumber(),skuCmd.getInvCountryOfOrigin(), skuCmd.getInvStatus(), skuCmd.getInvMfgDate(), skuCmd.getInvExpDate(), skuCmd.getInvAttr1(), skuCmd.getInvAttr2(), skuCmd.getInvAttr3(),
//                  skuCmd.getInvAttr4(), skuCmd.getInvAttr5(), skuCmd.getSkuSn(), skuCmd.getSkuDefect());
//              if(skuInvAttrId.equals(skuAttrId)) {
//                  for(int i=0;i<Integer.valueOf(scanQty.toString());i++) {
//                      list.add(whSkuInvCmd);
//                  }
//                  break;
//              }
//          }
          //如果是多次扫sn，则跳过
//          if(!(null != isScanSkuSn && isScanSkuSn == true)) {
//              if(isRecommendFail) { //推荐库位失败的情况,绑定库位
//                  //缓存容器库存
//                  whSkuInventoryManager.manMadeBinding(outerContainerId, insideContainerId, warehouse, locationId, putawayPatternDetailType, ouId, userId, insideContainerCode, skuCmd.getScanSkuQty());
//              }else{
//                  //手动绑定库位
//                  List<WhSkuInventoryTobefilled> listTobeFilled = whSkuInventoryTobefilledDao.findWhSkuInventoryTobefilled(outerContainerId, insideContainerId, ouId);
//                  for(WhSkuInventoryTobefilled skuInvTobeFilled :listTobeFilled) {
//                      skuInvTobeFilled.setLocationId(locationId);  //中心设置库位
//                      //先删除待入库数据，在添加
//                      Long tobeFilledId = skuInvTobeFilled.getId();
//                      whSkuInventoryTobefilledDao.deleteByExt(tobeFilledId, ouId);
//                      //重新插入
//                      skuInvTobeFilled.setId(null);  //id置空
//                      skuInvTobeFilled.setLastModifyTime(new Date());
//                      whSkuInventoryTobefilledDao.insert(skuInvTobeFilled);
//                  }
//              }
//              // 累加容器，容器内sku商品、库位上已有容器，商品重量， 判断是否<=库位承重 *
////              Boolean result = this.IsLocationBearWeight(insideContainerId, locationSkuList, whskuList,putawayPatternDetailType,locationId, ouId);
////              if(result) {//超重时,更换库位
////                  srCmd = this.reminderLocation(isCmd, srCmd, ouId);
////                  srCmd.setIsNeedScanNewLocation(true);
////                  return srCmd;
////              }else{
////                  srCmd.setIsNeedScanNewLocation(false);
////              }
//          }
          //判断是否要是提示下一个商品/当前商品的SN/残次信息
          CheckScanSkuResultCommand cssrCmd =  this.getNextTipSkuOrSnDefect(scanPattern,ocCmd, icCmd, insideContainerSkuAttrIdsQty, insideContainerSkuAttrIdsSnDefect, 
        		  insideContainerSkuAttrIds, skuCmd, insideContainerCode, logId);
          if (cssrCmd.isNeedTipSkuSn()) {
              // 当前商品还未扫描，继续扫sn残次信息
              String tipSkuAttrId = cssrCmd.getTipSkuAttrId();
              this.cacheScanSku(insideContainerCode, tipSkuAttrId);   //  缓存
              tipSkuDetailAspect(srCmd, tipSkuAttrId, skuAttrIdsQty, logId);
              srCmd.setIsContinueScanSn(true);
          } else if (cssrCmd.isNeedTipSku()) {
              String tipSkuAttrId = cssrCmd.getTipSkuAttrId();
              srCmd.setNeedTipSku(true);// 提示下一个sku
                  //提示下一个商品
//                  whSkuInventoryManager.execPutaway(skuCmd.getScanSkuQty(), warehouse, userId, ocCmd, icCmd, locationCode, putawayPatternDetailType, ouId, skuAttrId);
                  ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerCode +sId.toString());
                  List<String> scanSkuAttrIds = null;
                  if(null != scanSkuCmd) {
                      scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();
                  }
                  //whSkuInventoryManager.execPutaway(ocCmd, icCmd, locationCode, skuCmd, scanSkuAttrIds, scanQty, warehouse, putawayPatternDetailType, ouId, userId, logId);
                  Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
                  WhSkuCommand tipSkuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
                  if (null == tipSkuCmd) {
                      log.error("sku is not found error, logId is:[{}]", logId);
                      throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                  }
                  tipSkuDetailAspect(srCmd, tipSkuAttrId,  skuAttrIdsQty, logId);
                  srCmd.setTipSkuBarcode(tipSkuCmd.getBarCode());
                  srCmd.setSkuId(skuId);
                  this.cacheScanSku(insideContainerCode, tipSkuAttrId);   //  缓存
            }else{
            	//容器内商品全部拆分完成
            	//1.出库箱整箱移动需删除原有出库箱待复核数据生成新出库箱待复核数据
            	
            	//2.出库箱部分移动需生成新出库箱待复核数据并更新原有出库箱待复核数据
            	
            	//3.小车/播种墙货格移动到出库箱将出库箱更新到小车货格待复核数据上
            	 //拆箱清除全部缓存
                this.boxMoveRemoveAllCache(insideContainerCode);
                //拆箱清除全部缓存
            	
            } 
//          } else if (cssrCmd.isNeedTipLoc()) {
//              // 当前库位对应的商品已扫描完毕，可上架，并提示下一个库位
//              srCmd.setAfterPutawayTipLoc(true);
//              Long tipLocId = cssrCmd.getTipLocId();
//              Location tipLoc = locationDao.findByIdExt(tipLocId, ouId);
//              if (null == tipLoc) {
//                  log.error("location is null error, locId is:[{}], logId is:[{}]", tipLocId, logId);
//                  throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
//              }
//              srCmd.setTipLocationCode(tipLoc.getCode());
////              whSkuInventoryManager.execPutaway(skuCmd.getScanSkuQty(), warehouse, userId, ocCmd, icCmd, locationCode, putawayPatternDetailType, ouId, skuAttrId);
//              ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipLocationId.toString()+sId.toString());
//              List<String> scanSkuAttrIds = null;
//              if(null != scanSkuCmd) {
//                  scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();
//              }
//              whSkuInventoryManager.execPutaway(ocCmd, icCmd, locationCode, skuCmd, scanSkuAttrIds, scanQty, warehouse, putawayPatternDetailType, ouId, userId, logId);
//            //拆箱清除缓存
//              this.splitContainerPutawayRemoveAllCache(ocCmd, icCmd, locationId, logId,true,sId);
//              //拆箱清除缓存
//          } else if (cssrCmd.isNeedTipContainer()) {
////                  whSkuInventoryManager.execPutaway(skuCmd.getScanSkuQty(), warehouse, userId, ocCmd, icCmd, locationCode, putawayPatternDetailType, ouId, skuAttrId);
//              ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipLocationId.toString()+sId.toString());
//              List<String> scanSkuAttrIds = null;
//              if(null != scanSkuCmd) {
//                  scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();
//              }
//              whSkuInventoryManager.execPutaway(ocCmd, icCmd, locationCode, skuCmd, scanSkuAttrIds, scanQty, warehouse, putawayPatternDetailType, ouId, userId, logId);
//                  //拆箱清除缓存
//                  this.splitContainerPutawayRemoveAllCache(ocCmd, icCmd, locationId, logId,true,sId);
//              // 当前容器已扫描完毕，可上架，并提示下一个容器
//              srCmd.setAfterPutawayTipContianer(true);
//              Long tipContainerId = cssrCmd.getTipContainerId();
//              Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
//              if (null == tipContainer) {
//                  log.error("tip container is null error, containerId is:[{}], logId is:[{}]", tipContainerId, logId);
//                  throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
//              }
//              srCmd.setTipContainerCode(tipContainer.getCode());
//          } else {
//              // 执行上架
//              srCmd.setPutaway(true);
////                  whSkuInventoryManager.execPutaway(skuCmd.getScanSkuQty(), warehouse, userId, ocCmd, icCmd, locationCode, putawayPatternDetailType, ouId, skuAttrId);
//              ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipLocationId.toString()+sId.toString());
//              List<String> scanSkuAttrIds = null;
//              if(null != scanSkuCmd) {
//                  scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();
//              }
//              whSkuInventoryManager.execPutaway(ocCmd, icCmd, locationCode, skuCmd, scanSkuAttrIds, scanQty, warehouse, putawayPatternDetailType, ouId, userId, logId);
//                //拆箱清除缓存
//                  this.splitContainerPutawayRemoveAllCache(ocCmd, icCmd, locationId, logId,false,sId);
//          }
          return srCmd;
      }
      
      
     /**
      * 判断是否要是提示下一个商品/当前商品的SN/残次信息
      * @param scanPattern
      * @param ocCmd
      * @param icCmd
      * @param insideContainerSkuAttrIdsQty
      * @param insideContainerSkuAttrIdsSnDefect
      * @param insideContainerSkuAttrIds
      * @param skuCmd
      * @param insideContainerCode
      * @param logId
      * @return
      */
      public CheckScanSkuResultCommand getNextTipSkuOrSnDefect(Integer scanPattern,ContainerCommand ocCmd, ContainerCommand icCmd, Map<String, Map<String, Long>> insideContainerSkuAttrIdsQty,
              Map<String, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect, Map<String, Set<String>> insideContainerSkuAttrIds,  WhSkuCommand skuCmd,String insideContainerCode, String logId) {
          CheckScanSkuResultCommand cssrCmd = new CheckScanSkuResultCommand();
          Long ocId = null;
          //Long icId = icCmd.getId();
          Long skuId = skuCmd.getId();
          // 0.先判断当前内部容器是否在缓存中
//          boolean icExists = false;
//          for (Long iId : insideContainerIds) {
//              if (0 == icId.compareTo(iId)) {
//                  icExists = true;
//                  break;
//              }
//          }
//          if (false == icExists) {
//              log.error("tip container is not in cache server error, logId is[{}]", logId);
//              throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//          }
          Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(insideContainerCode);// 当前内部容器中所有唯一sku对应的数量
          Map<String, Set<String>> skuAttrIdsSnDefect = insideContainerSkuAttrIdsSnDefect.get(insideContainerCode);// 唯一sku对应的所有sn残次信息
          Set<String> skuAttrIds = null; 
          skuAttrIds = insideContainerSkuAttrIds.get(insideContainerCode);// 内部容器对应的所有唯一sku
          //ocId = ocCmd.getId();
          // 1.当前的内部容器是不是提示容器队列的第一个
//              TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
//              ArrayDeque<Long> cacheContainerIds = null;
//              if (null != cacheContainerCmd) {
//                  cacheContainerIds = cacheContainerCmd.getTipInsideContainerIds();
//              }
//              if (null != cacheContainerIds && !cacheContainerIds.isEmpty()) {
//                  Long value = cacheContainerIds.peekFirst();
//                  if (null == value) value = -1L;
//                  if (0 != value.compareTo(icId)) {
//                      log.error("tip container is not queue first element exception, logId is:[{}]", logId);
//                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                  }
//              } else {
//                  log.error("scan container queue is exception, logId is:[{}]", logId);
//                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//
//              }
          // 2.当前的库位是不是提示库位队列的第一个
//              TipLocationCacheCommand tipLocCmd = cacheManager.getObject(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
//              ArrayDeque<Long> tipLocIds = null;
//              if (null != tipLocCmd) {
//                  tipLocIds = tipLocCmd.getTipLocationIds();
//              }
//              if (null != tipLocIds && !tipLocIds.isEmpty()) {
//                  Long value = tipLocIds.peekFirst(); // 队列的第一个
//                  if (null == value) value = -1L;
//                  if (0 != value.compareTo(tipLocationId)) {
//                      log.error("tip location is not queue first element exception, logId is:[{}]", logId);
//                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//                  }
//              } else {
//                  log.error("scan location queue is exception, logId is:[{}]", logId);
//                  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//              }
          // 3.当前的商品是不是提示商品队列的第一个
          String skuAttrId = "";
          String skuAttrIdNoSn = "";
          Boolean isTipSkuSn = skuCmd.getIsNeedTipSkuSn();
          Boolean isTipSkuDefect = skuCmd.getIsNeedTipSkuDefect();
          boolean isSnLine = false;
          if ((null != isTipSkuSn && true == isTipSkuSn) || (null != isTipSkuDefect && true == isTipSkuDefect)) {
        	  skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuCmd.getId(), skuCmd.getInvType(), skuCmd.getInvStatus(), skuCmd.getInvBatchNumber(), skuCmd.getInvCountryOfOrigin(), skuCmd.getInvMfgDate(), skuCmd.getInvExpDate(), skuCmd.getInvAttr1(),
        			  skuCmd.getInvAttr2(), skuCmd.getInvAttr3(), skuCmd.getInvAttr4(), skuCmd.getInvAttr5(), skuCmd.getSkuSn(), skuCmd.getSkuDefect());
        	  skuAttrIdNoSn = SkuCategoryProvider.concatSkuAttrId(skuCmd.getId(), skuCmd.getInvType(), skuCmd.getInvStatus(), skuCmd.getInvBatchNumber(), skuCmd.getInvCountryOfOrigin(), skuCmd.getInvMfgDate(), skuCmd.getInvExpDate(), skuCmd.getInvAttr1(),
        			  skuCmd.getInvAttr2(), skuCmd.getInvAttr3(), skuCmd.getInvAttr4(), skuCmd.getInvAttr5());
        	  isSnLine = true;
          } else {
        	  skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuCmd.getId(), skuCmd.getInvType(), skuCmd.getInvStatus(), skuCmd.getInvBatchNumber(), skuCmd.getInvCountryOfOrigin(), skuCmd.getInvMfgDate(), skuCmd.getInvExpDate(), skuCmd.getInvAttr1(),
        			  skuCmd.getInvAttr2(), skuCmd.getInvAttr3(), skuCmd.getInvAttr4(), skuCmd.getInvAttr5());
        	  isSnLine = false;
          }
          TipScanSkuCacheCommand tipSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + insideContainerCode);
          ArrayDeque<String> tipSkuAttrIds = null;
          if (null != tipSkuCmd) {
        	  tipSkuAttrIds = tipSkuCmd.getScanSkuAttrIds();
          }
          if (null != tipSkuAttrIds && !tipSkuAttrIds.isEmpty()) {
        	  String value = tipSkuAttrIds.peekFirst();
        	  if (!skuAttrId.equals(value)) {
        		  log.error("tip sku is not queue first element exception, logId is:[{}]", logId);
        		  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        	  }
          } else {
        	  log.error("scan sku queue is exception, logId is:[{}]", logId);
        	  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
          }
          // 4.判断当前商品是否扫描完毕
          Double scanSkuQty  = skuCmd.getScanSkuQty();
          if (null == scanSkuQty) {
        	  log.error("scan sku qty is valid, logId is:[{}]", logId);
        	  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
          }
          long skuQty = 0L;
          if(true == isSnLine) {//存在sn/残次信息
        	  skuQty  =  skuAttrIdsQty.get(skuAttrIdNoSn);
          }else{//不存在sn/残次信息
        	  skuQty  =  skuAttrIdsQty.get(skuAttrId);
          }
          
          //存在SN/残次信息
          if(true== isSnLine) {
        	  ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerCode );
        	  if(null == scanSkuCmd) {
        		  scanSkuCmd = new ScanSkuCacheCommand();
        	  }
        	  List<String> scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();   //临时存储sn/残次信息
        	  if(null == scanSkuAttrIds) {
        		  scanSkuAttrIds = new ArrayList<String>();
        	  }
        	  if(!scanSkuAttrIds.contains(skuAttrId)){
        		  scanSkuAttrIds.add(skuAttrId);
        	  }
        	  scanSkuCmd.setScanSkuAttrIds(scanSkuAttrIds);
        	  cacheManager.setObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerCode + skuId.toString(), scanSkuCmd,CacheConstants.CACHE_ONE_DAY);
        	  long snCount =  cacheManager.incr(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerCode + skuId.toString());
        	  if(snCount < scanSkuQty) {
        		  // 继续复核
        		  String tipSkuAttrId = null;
        		  if (false == isSnLine){ //没有sn/残次
        			  tipSkuAttrId = skuAttrId;
        		  }else{  //存在sn/残次信息
        			  Set<String> snDefects = skuAttrIdsSnDefect.get(skuAttrIdNoSn);   //获取当前唯一sku所有对应的sn/残次信息
        			  for(String snDe:snDefects) {
        				  String tipSkuAttrIdSnDefect = SkuCategoryProvider.concatSkuAttrId(skuAttrIdNoSn,snDe);
        				  if(tipSkuAttrIds.contains(tipSkuAttrIdSnDefect)) {
        					  continue;
        				  }else{
        					  tipSkuAttrId =tipSkuAttrIdSnDefect;
        					  break;
        				  }
        			  }
        		  }
        		  cssrCmd.setNeedTipSkuSn(true);
        		  cssrCmd.setTipSkuAttrId(tipSkuAttrId);
        		  return cssrCmd;
        	  }
          }
          long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_QUEUE + insideContainerCode + skuId.toString(), scanSkuQty.intValue());
          //当前唯一SKU商品全部扫完
          if (cacheValue == skuQty) {
        	  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerCode + skuId.toString());
        	  String tipSkuAttrId = "";
        	  Boolean allIsExists = false;  //为true时，存在同一个货箱(或同一个库位要上架的sku)不同唯一sku的情况
        	  for (String sId : skuAttrIds) {
        		  String tempSkuAttrId = null;
        		  if(isSnLine) { //存在sn
//                                 if(isRecommendFail == false){  //库位推荐成功的skAttrIds是sn/残次信息加唯一sku,推荐不成功的只是唯一sku
//                                     tempSkuAttrId = sId;
//                                     Set<String> tempSkuAttrIds = new HashSet<String>();
//                                     tempSkuAttrIds.add(tempSkuAttrId);
//                                     boolean isExists = isCacheAllExists2(tempSkuAttrIds, tipSkuAttrIds);
//                                     if (true == isExists) {
//                                         continue;
//                                     } else {
//                                         allIsExists = true;
//                                         tipSkuAttrId = sId;
//                                         break;
//                                     }
//                                 }else
        			  {
//                                         Set<String> snDefects = skuAttrIdsSnDefect.get(sId);   //获取当前唯一sku所有对应的sn/残次信息
//                                         tempSkuAttrId = SkuCategoryProvider.concatSkuAttrId(sId, snDefect1);
        				  for(String tipsIdSn:tipSkuAttrIds) {
        					  String tipsId =  SkuCategoryProvider.getSkuAttrId(tipsIdSn);
        					  if(!sId.equals(tipsId)) {
        						  allIsExists = true;
        						  tipSkuAttrId = sId;
        						  break;
        					  }else{
        						  continue;
        					  }
        				  }
        			  }
        		  }else{  //不存在sn
        			  tempSkuAttrId = sId;
        			  Set<String> tempSkuAttrIds = new HashSet<String>();
        			  tempSkuAttrIds.add(tempSkuAttrId);
        			  boolean isExists = isCacheAllExists2(tempSkuAttrIds, tipSkuAttrIds);
        			  if (true == isExists) {
        				  continue;
        			  } else {
        				  allIsExists = true;
        				  tipSkuAttrId = sId;
        				  break;
        			  }
        		  }
        	  }
        	  if(allIsExists) {
        		  if (true == isSnLine){
        			  Set<String> snDefects = skuAttrIdsSnDefect.get(skuAttrIdNoSn);   //获取当前唯一sku所有对应的sn/残次信息
        			  for(String snDe:snDefects) {
        				  String tipSkuAttrIdSnDefect = SkuCategoryProvider.concatSkuAttrId(skuAttrIdNoSn,snDe);
        				  if(tipSkuAttrIds.contains(tipSkuAttrIdSnDefect)) {
        					  continue;
        				  }else{
        					  tipSkuAttrId =tipSkuAttrIdSnDefect;
        				  }
        			  }
        			  cssrCmd.setNeedTipSku(true);
        			  cssrCmd.setTipSkuAttrId(tipSkuAttrId);
        		  }else{
        			  cssrCmd.setNeedTipSku(true);
        			  cssrCmd.setTipSkuAttrId(tipSkuAttrId);
        		  }
//                         }else{
//                             boolean isLocAllCache = true;
//                             // 判断是否需要提示下一个库位
//                             if(isRecommendFail == false) { //推荐库位
//                                 isLocAllCache = isCacheAllExists(locationIds, tipLocIds);
//                             }
//                             //判断哪些对应
//                             if (false == isLocAllCache) {
//                                 // 提示下一个库位
//                                 cssrCmd.setPutaway(true);// 可上架
//                                 Long tipLocId = null;
//                                 for (Long lId : locationIds) {
//                                     if (0 == tipLocationId.compareTo(lId)) {
//                                         continue;
//                                     }
//                                     Set<Long> tempLocIds = new HashSet<Long>();
//                                     tempLocIds.add(lId);
//                                     boolean isExists = isCacheAllExists(tempLocIds, tipLocIds);
//                                     if (true == isExists) {
//                                         continue;
//                                     } else {
//                                         tipLocId = lId;
//                                         break;
//                                     }
//                                 }
//                                 cssrCmd.setNeedTipLoc(true);
//                                 cssrCmd.setTipLocId(tipLocId);
//                                 return cssrCmd;
//                           }else{
//                             // 判断是否需要提示下一个容器
//                             Set<Long> allContainerIds = insideContainerIds;
//                             boolean isAllContainerCache = isCacheAllExists(allContainerIds, cacheContainerIds);
//                             Long tipContainerId = null;
//                             if (false == isAllContainerCache) {
//                                 // 提示下一个容器
//                                 cssrCmd.setPutaway(true);
//                                 for (Long cId : allContainerIds) {
//                                     if (0 == icId.compareTo(cId)) {
//                                         continue;
//                                     }
//                                     Set<Long> tempContainerIds = new HashSet<Long>();
//                                     tempContainerIds.add(cId);
//                                     boolean isExists = isCacheAllExists(tempContainerIds, cacheContainerIds);
//                                     if (true == isExists) {
//                                         continue;
//                                     } else {
//                                         tipContainerId = cId;
//                                         break;
//                                     }
//                                 }
//                                 cssrCmd.setNeedTipContainer(true);
//                                 cssrCmd.setTipContainerId(tipContainerId);
//                             } else {
//                                 cssrCmd.setPutaway(true);// 可上架
//                             }
//                         }
        	  } 
        	  //当前唯一SKU商品全部扫完     
          } else if (cacheValue < skuQty) {
        	  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + insideContainerCode + skuId.toString());
        	  // 继续复核
        	  String tipSkuAttrId = null;
        	  if (false == isSnLine){ //没有sn/残次
        		  tipSkuAttrId = skuAttrId;
        	  }else{  //存在sn/残次信息
        		  Set<String> snDefects = skuAttrIdsSnDefect.get(skuAttrIdNoSn);   //获取当前唯一sku所有对应的sn/残次信息
        		  for(String snDe:snDefects) {
        			  String tipSkuAttrIdSnDefect = SkuCategoryProvider.concatSkuAttrId(skuAttrIdNoSn,snDe);
        			  if(tipSkuAttrIds.contains(tipSkuAttrIdSnDefect)) {
        				  continue;
        			  }else{
        				  tipSkuAttrId =tipSkuAttrIdSnDefect;
        				  break;
        			  }
        		  }
        	  }
        	  cssrCmd.setNeedTipSku(true);
        	  cssrCmd.setTipSkuAttrId(tipSkuAttrId);
          } else {
        	  log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, scanSkuQty, logId);
        	  throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
          }
          return cssrCmd;
}
      
      
      
      
    
    public ScanResultCommand scanSku(WhSkuCommand skuCmd,WhFunctionOutboundboxMove boxMoveFunc,String containerCode){
    	log.info("PdaOutBoundBoxMoveManagerImpl scanSku is start");
    	ScanResultCommand srCommand = new ScanResultCommand();
    	
    	//判断是否部分移动
    	boolean isMovePart = false;
    	if(boxMoveFunc.getMovePattern().equals(Constants.MOVE_PATTERN_PART)){
    		isMovePart = true;
    	}
        //返回商品扫描模式
    	srCommand.setScanPattern(boxMoveFunc.getScanPattern());
    	
    	//获取库存统计分析数据
    	InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCode);
    	
    	
    	if(isMovePart){
    		//部分移动
    		
    	}else{
    		//整箱移动
    		
    	}
    	
    	//判断商品属性是否唯一
    	Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
        Set<Long> skuIds = insideContainerSkuIds.get(containerCode);
        boolean skyAttrOnly = false;
        
        for (Long skuId : skuIds) {
        	if(skuId.equals(skuCmd.getId())){
        		skyAttrOnly = true;//确认扫描的商品属性是唯一的
        		break;
        	}
        }
        if(skyAttrOnly){
        	//判断是否提示商品属性
        	if(boxMoveFunc.getIsTipInvAttr()){
//        		skuCmd
//        		srCommand.
        	}
        }
    	log.info("PdaOutBoundBoxMoveManagerImpl scanSku is end");
    	return srCommand;
    }
    /**
     * 拆箱上架：容器信息流程判断
     * @param containerCmd
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
//    private ScanResultCommand splitContainerPutwayScanContainer(String outerContainerCode,ContainerCommand containerCmd,Long ouId,String logId,Long userId){
//        log.info("PdaOutBoundBoxMoveManagerImpl scanContainerByBox is start"); 
//        ScanResultCommand srCmd = new ScanResultCommand();  //扫描返回结果
//        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_SUGGEST_PUTAWAY);
//        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
//        if (null == containerCmd) {
//            // 容器信息不存在
//            log.error("container is not exists, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
//        }
//        // 验证容器状态是否可用
//        if (!containerCmd.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
//            log.error("container lifecycle is not normal, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
//        }
//        
//        // 验证容器状态是否是待上架
//        if (!(containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
//            log.error("container lifecycle is not normal, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
//        }
//        Long containerId = containerCmd.getId();
//        String containerCode = containerCmd.getCode();
//
//        Long outerContainerId = null;
//        if(!StringUtils.isEmpty(outerContainerCode)){
//            ContainerCommand outerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
//            outerContainerId = outerCmd.getId();
//        }
//        //根据库存盘点是否有外部容器
//        int outerCount1 = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId,containerCmd.getId());  //根据容器id，查询容器是否是外部
//        int insideCount1 = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId,containerCmd.getId()); //根据容器id，查询容器是否是内部
//        int outerCount2 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByOuterContainerId(ouId, containerCmd.getId());
//        int insideCount2 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, containerCmd.getId());
//        if (0 < insideCount1 || 0 < insideCount2) {
//            // 拆箱上架判断是是否有外部容器号
//            int ocCount = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId1(ouId, containerId);
//            int ocCount1 = whSkuInventoryDao.findLocTobefilledInventoryCountsByInsideContainerId1(ouId, containerId);
//            if (0 < ocCount || 0 < ocCount1) {
//                if(StringUtils.isEmpty(outerContainerCode)) {
//                    log.error("sys guide container putaway scan container has outer container, should scan outer container first, containerCode is:[{}], logId is:[{}]", containerCode, logId);
//                    throw new BusinessException(ErrorCodes.CONTAINER_HAS_OUTER_CONTAINER_SCAN_OUTER_FIRST, new Object[] {containerCode});
//                }
//            } 
//           srCmd.setContainerType(WhContainerType.INSIDE_CONTAINER);// 内部容器,无外部容器，无需循环提示容器
//           srCmd.setInsideContainerCode(containerCode);
//           this.removeCachce(containerId, false);
//           if(null != outerContainerId){
//               this.containerPutawayCacheInsideContainer(containerCmd, outerContainerId, logId, outerContainerCode);
//           }
//            
//        }
//        if (0 < outerCount1 || 0 < outerCount2) {
//            this.removeCachce(containerId, true);
//            srCmd.setContainerType(WhContainerType.OUTER_CONTAINER);// 外部容器
//            srCmd.setHasOuterContainer(true);// 有外部容器，需要循环提示容器
//            srCmd.setOuterContainerCode(containerCmd.getCode());    //外部容器号
//            ContainerStatisticResultCommand cacheCsr = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerId.toString());  //containerId,外部容器id
//            ContainerStatisticResultCommand csrCmd  = null;
//            if (null == cacheCsr) {
//                // 缓存所有内部容器统计信息
//                csrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);   //缓存信息外部Id
//            } else {
//                csrCmd = cacheCsr;
//            }
//            // 获取所有内部容器id
//            Set<Long> insideContainerIds = csrCmd.getInsideContainerIds();
//            Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
//            // 提示一个内部容器
//            Long tipContainerId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipContainer0(containerCmd, insideContainerIds, logId);
//            srCmd.setNeedTipContainer(true);
//            String tipContainerCode = null;
//            if (null != insideContainerIdsCode) {
//                tipContainerCode = insideContainerIdsCode.get(tipContainerId);
//                if (StringUtils.isEmpty(tipContainerCode)) {
//                    Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
//                    if (null == tipContainer) {
//                        log.error("container is null error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
//                    }
//                    tipContainerCode = tipContainer.getCode();
//                }
//            }
//            srCmd.setTipContainerCode(tipContainerCode);
//            //修改外部容器状态
//            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
//                Container container = new Container();
//                BeanUtils.copyProperties(containerCmd, container);
//                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
//                container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
//                containerDao.saveOrUpdateByVersion(container);
//                srCmd.setOuterContainerCode(containerCode);// 外部容器号
//                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
//            }
//        }
//        if(0 >= insideCount1 && 0 >= outerCount1 && 0 >= insideCount2 && 0 >= outerCount2){
//            // 无收货库存
//            log.error("sys guide pallet putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
//        }
//        // 修改内部容器状态
//        if(srCmd.getContainerType() != WhContainerType.OUTER_CONTAINER ) {   //当前扫描的容器是内部容器
//            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
//                Container container = new Container();
//                BeanUtils.copyProperties(containerCmd, container);
//                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
//                container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
//                containerDao.saveOrUpdateByVersion(container);
//                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
//            }
//        }
//        log.info("PdaOutBoundBoxMoveManagerImpl scanContainerByBox is end"); 
//        return srCmd;
//    }
    
    private void removeCachce(Long containerId,Boolean isHaveOuterContainer){
        if(isHaveOuterContainer){ //有外部容器
            ContainerStatisticResultCommand csCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerId.toString());
            if (null != csCmd) {
                Set<Long> insideContainerIds = csCmd.getInsideContainerIds();
                for (Long icId : insideContainerIds) {
                    InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                    if (null != isCmd) {
                        Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
                        Set<Long> skuIds = insideContainerSkuIds.get(icId);
                        for (Long skuId : skuIds) {
                            // 清除逐件扫描的队列
                            cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString());
                        }
                    }
                    cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                    cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                    cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
                }
            }
            // 1.再清除所有提示容器队列
            cacheManager.remove(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
            // 2.清除所有内部容器统计信息
            cacheManager.removeMapValue(CacheConstants.CONTAINER_STATISTIC, containerId.toString());
        }else{//没有外部容器
            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
            if (null != isCmd) {
                Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
                Set<Long> skuIds = insideContainerSkuIds.get(containerId);
                for (Long skuId : skuIds) {
                    // 清除逐件扫描的队列
                    cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + containerId.toString() + skuId.toString());
                }
            }
            cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + containerId.toString());
            cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
            cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, containerId.toString());
        }
        
    }
    
    /**
     * 整箱上架容器信息判断流程
     * @param containerCmd
     * @param ouId
     * @param logId
     * @param userId
     * @return
     */
//    private ScanResultCommand containerPutwayScanContainer(String outerContainerCode,ContainerCommand containerCmd,Long ouId,String logId,Long userId){
//        log.info("PdaOutBoundBoxMoveManagerImpl scanContainerByBox is start"); 
//        ScanResultCommand srCmd = new ScanResultCommand();  //扫描返回结果
//        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_SUGGEST_PUTAWAY);
//        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
//        if (null == containerCmd) {
//            // 容器信息不存在
//            log.error("container is not exists, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
//        }
//        // 验证容器状态是否可用
//        if (!containerCmd.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
//            log.error("container lifecycle is not normal, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
//        }
//        
//        // 验证容器状态是否是待上架
//        if (!(containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
//            log.error("container lifecycle is not normal, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
//        }
//        Long containerId = containerCmd.getId();
//        String containerCode = containerCmd.getCode();
//        //根据库是否是外部容器
//        int outerCount1 = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId,containerCmd.getId());  //根据容器id，查询容器是否是外部
//        int insideCount1 = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId,containerCmd.getId()); //根据容器id，查询容器是否是内部
//        int outerCount2 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByOuterContainerId(ouId, containerCmd.getId());
//        int insideCount2 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, containerCmd.getId());
//        if (0 < insideCount1 || 0< insideCount2) {
//         // 整箱上架判断是是否有外部容器号
//            int ocCount = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId1(ouId, containerId);
//            int ocCount1 = whSkuInventoryDao.findLocTobefilledInventoryCountsByInsideContainerId1(ouId, containerId);
//            if (0 < ocCount || 0 < ocCount1) {   //大于0,说明有外部容器
//                //判断外部容器是否已经扫描
//                if(StringUtils.isEmpty(outerContainerCode)){   //外部容器没有扫描
//                    log.error("sys guide container putaway scan container has outer container, should scan outer container first, containerCode is:[{}], logId is:[{}]", containerCode, logId);
//                    throw new BusinessException(ErrorCodes.CONTAINER_HAS_OUTER_CONTAINER_SCAN_OUTER_FIRST, new Object[] {containerCode});
//                }else{
//                    ContainerCommand outerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
//                    Long outerContainerId = outerCmd.getId();
//                    this.containerPutawayCacheInsideContainer(containerCmd, outerContainerId, logId, outerContainerCode);
//                }
//            } else {
//                srCmd.setContainerType(WhContainerType.INSIDE_CONTAINER);// 内部容器,无外部容器，无需循环提示容器
//                srCmd.setInsideContainerCode(containerCode);
//            }
//            this.removeCachce(containerId, false);
//        }
//        if (0 < outerCount1 || 0 < outerCount2) {
//            //外部容器，判断是否已经存在缓存,如果存在先删除
//            this.removeCachce(containerId, true);
//            srCmd.setContainerType(WhContainerType.OUTER_CONTAINER);// 外部容器
//            srCmd.setHasOuterContainer(true);// 有外部容器，需要循环提示容器
//            srCmd.setOuterContainerCode(containerCmd.getCode());    //外部容器号
//            ContainerStatisticResultCommand cacheCsr = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerId.toString());
//            if (null == cacheCsr) {
//                // 缓存所有内部容器统计信息
//                cacheCsr = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);   //缓存信息外部Id
//            } 
//            // 获取所有内部容器id
//            Set<Long> insideContainerIds = cacheCsr.getInsideContainerIds();
//            Map<Long, String> insideContainerIdsCode = cacheCsr.getInsideContainerIdsCode();
//            // 提示一个容器
//            Long tipContainerId = pdaPutawayCacheManager.sysGuideContainerPutawayTipContainer0(containerCmd, insideContainerIds, logId);
//            srCmd.setNeedTipContainer(true);
//            String tipContainerCode = null;
//            if (null != insideContainerIdsCode) { 
//                tipContainerCode = insideContainerIdsCode.get(tipContainerId);
//                if (StringUtils.isEmpty(tipContainerCode)) {
//                    Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
//                    if (null == tipContainer) {
//                        log.error("container is null error, logId is:[{}]", logId);
//                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
//                    }
//                    tipContainerCode = tipContainer.getCode();
//                }
//            }
//            srCmd.setTipContainerCode(tipContainerCode);
//            //修改外部容器状态
//            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
//                Container container = new Container();
//                BeanUtils.copyProperties(containerCmd, container);
//                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
//                container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
//                containerDao.saveOrUpdateByVersion(container);
//                srCmd.setOuterContainerCode(containerCode);// 外部容器号
//                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
//            }
//        } 
//        if(0 >= insideCount1 && 0 >= outerCount1 && 0 >= insideCount2 && 0 >= outerCount2){
//            // 无收货库存
//            log.error("sys guide pallet putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
//        }
//        // 修改内部容器状态
//        if(srCmd.getContainerType() != WhContainerType.OUTER_CONTAINER ) {   //当前扫描的容器是内部容器
//            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
//                Container container = new Container();
//                BeanUtils.copyProperties(containerCmd, container);
//                container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
//                container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
//                containerDao.saveOrUpdateByVersion(container);
//                srCmd.setOuterContainerCode(containerCode);// 外部容器号
//                insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
//            }
//        }
//        log.info("PdaOutBoundBoxMoveManagerImpl scanContainerByBox is end"); 
//        return srCmd;
//    }

//   /***
//    * 整托容器信息判断流程
//    * @param containerCmd
//    * @param ouId
//    * @param logId
//    * @param userId
//    * @return
//    */
//    private ScanResultCommand palletPutwayScanContainer(ContainerCommand containerCmd,Long ouId,String logId,Long userId) {
//        // TODO Auto-generated method stub
//        log.info("PdaOutBoundBoxMoveManagerImpl scanContainerBypallet is start"); 
//        if (log.isInfoEnabled()) {
//            log.info("PdaOutBoundBoxMoveManagerImpl scanContainerBypallet param , ouId is:[{}],  containerCmd is:[{}]", ouId,  containerCmd);
//        }
//        ScanResultCommand srCmd = new ScanResultCommand();  //扫描返回结果
//        srCmd.setPutawayPatternType(WhPutawayPatternType.SYS_SUGGEST_PUTAWAY);
//        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
//        if (null == containerCmd) {
//            // 容器信息不存在
//            log.error("container is not exists, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
//        }
//        String containerCode = containerCmd.getCode();
//        // 验证容器状态是否可用
//        if (!containerCmd.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
//            log.error("container lifecycle is not normal, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
//        }
//        
//        // 验证容器状态是否是待上架
//        if (!(containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
//            log.error("container lifecycle is not normal, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
//        }
//        //根据库存盘点是否有外部容器
//        int outerCount1 = whSkuInventoryDao.findRcvdInventoryCountsByOuterContainerId(ouId,containerCmd.getId());  //根据容器id，查询容器是否是外部
//        int insideCount1 = whSkuInventoryDao.findRcvdInventoryCountsByInsideContainerId(ouId,containerCmd.getId()); //根据容器id，查询容器是否是内部
//        int outerCount2 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByOuterContainerId(ouId, containerCmd.getId());
//        int insideCount2 = whSkuInventoryDao.findLocToBeFilledInventoryCountsByInsideContainerId(ouId, containerCmd.getId());
//        if (0 < insideCount1 || 0 < insideCount2) {
//            // 整托上架只能扫外部容器号
//            log.error("sys Recommend putaway scan container is insideContainer error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_IS_INSIDE_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
//        }
//        if (0 < outerCount1 || 0 < outerCount2) {
//            //外部容器判断是否有缓存,如果有缓存，先删除缓存
//            pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(containerCmd, logId);
//            srCmd.setContainerType(WhContainerType.OUTER_CONTAINER);// 外部容器
//            srCmd.setHasOuterContainer(true);// 有外部容器
//            srCmd.setOuterContainerCode(containerCmd.getCode());    //外部容器号
//            Long outContainerId = containerCmd.getId();
//            ContainerStatisticResultCommand cacheCsr = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, outContainerId.toString());
//            if (null == cacheCsr) {
//                // 缓存所有内部容器统计信息
//                cacheCsr = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
//                cacheManager.setMapObject(CacheConstants.CONTAINER_STATISTIC, outContainerId.toString(), cacheCsr, CacheConstants.CACHE_ONE_DAY);
//            } 
//        } else {
//            // 无收货库存
//            log.error("sys guide pallet putaway scan container not found rcvdInvs error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_RCVD_INV_ERROR, new Object[] {containerCode});
//        }
//        // 1.先修改外部容器状态为：上架中，且占用中,另外所有的内部容器状态可以在库存信息统计完成以后再修改
//        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY == containerCmd.getStatus()) {
//            Container container = new Container();
//            BeanUtils.copyProperties(containerCmd, container);
//            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);
//            container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);
//            containerDao.saveOrUpdateByVersion(container);
//            srCmd.setOuterContainerCode(containerCode);// 外部容器号
//            insertGlobalLog(GLOBAL_LOG_UPDATE, container, ouId, userId, null, null);
//        }
//        
//        log.info("PdaOutBoundBoxMoveManagerImpl scanContainerBypallet is end"); 
//        return srCmd;
//    }
//    


    

    /**
     * 出库箱库存信息分析统计流程
     * @param invList
     * @param ouId
     * @param logId
     * @param containerCmd
     * @param srCmd
     * @param putWay
     * @return
     */
    private BoxInventoryStatisticResultCommand cacheBoxInventoryStatistics(List<WhSkuInventoryCommand> invList,Long userId,Long ouId,String logId,String outboundBoxCode){
    	// 3.库存信息统计
    	BoxInventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, outboundBoxCode);
        if(null == isrCmd){
        	isrCmd = new BoxInventoryStatisticResultCommand();
            /** 出库箱所有sku种类 */
            Map<String, Set<Long>> outBoundBoxSkuIds = new HashMap<String, Set<Long>>();
            /** 出库箱所有sku总件数 */
            Map<String, Long> outBoundBoxSkuQty = new HashMap<String, Long>();
            /** 出库箱单个sku总件数 */
            Map<String, Map<Long, Long>> outBoundBoxSkuIdQtys = new HashMap<String, Map<Long, Long>>();
            /** 出库箱唯一sku种类 */
            Map<String, Set<String>> outBoundBoxSkuIdSkuAttrIds = new HashMap<String, Set<String>>();
            /** 出库箱唯一sku总件数 */
            Map<String, Map<String, Long>> outBoundBoxSkuIdSkuAttrIdQtys = new HashMap<String, Map<String, Long>>();
            /** 出库箱唯一sku对应所有残次条码 */
            Map<String, Map<String, Set<String>>> outBoundBoxSkuAttrIdsSnDefect = new HashMap<String, Map<String, Set<String>>>();
        	
        	Set<Long> skuIds = new HashSet<Long>();// 容器中所有sku种类
        	Map<Long, Long> skuIdsQty = new HashMap<Long, Long>();// 容器每个sku总件数
        	Set<String> skuAttrIds = new HashSet<String>();//  容器对应唯一sku
        	Map<String, Long> skuAttrIdsQty = new HashMap<String,Long>();// 容器对应唯一sku总件数
        	Long skuQty = 0L;		
        	Map<String, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = new HashMap<String, Map<String, Set<String>>>(); //容器内唯一sku对应所有sn及残次条码
        	for (WhSkuInventoryCommand invCmd : invList) {
        		//获取SKU唯一属性
        		String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
                Long skuId = invCmd.getSkuId();
                Double curerntSkuQty = 0.0;     //当前sku数量
                curerntSkuQty  = invCmd.getOnHandQty();   //sku在库库存
                skuQty +=  invCmd.getOnHandQty().longValue();
                if (null != skuId) {
                    skuIds.add(skuId);    //所有sku种类数
                    WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
                    if (null == skuCmd) {
                    	outboundBoxCodeRemoveInventory(outboundBoxCode, ouId, logId);
                        log.error("outbound box move SKU  is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
                        throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                    }
                    
                }
                //  
                //加载所有唯一的sku(包含库存属性)
                skuAttrIds.add(skuAttrId);
                //容器内每个SKU的总件数统计
                if (null != skuIdsQty.get(skuId)) {
                    skuIdsQty.put(skuId, skuIdsQty.get(skuId) + curerntSkuQty.longValue());
                } else {
                    skuIdsQty.put(skuId, curerntSkuQty.longValue());
                }
                //统计唯一SKU总件数
                if (null != skuAttrIdsQty.get(skuAttrId)) {
                    skuAttrIdsQty.put(skuAttrId, skuAttrIdsQty.get(skuAttrId) + curerntSkuQty.longValue());
                } else {
                    skuAttrIdsQty.put(skuAttrId, curerntSkuQty.longValue());
                }
                //拆箱上架
                
                //容器内唯一sku对应所有sn及残次条码
                
                  List<WhSkuInventorySnCommand> snCmdList = invCmd.getWhSkuInventorySnCommandList();//获取库存的残次信息
                  Set<String> snDefects = null;
                  if (null != snCmdList && 0 < snCmdList.size()) {
                      snDefects = new HashSet<String>();
                      for (WhSkuInventorySnCommand snCmd : snCmdList) {
                          if (null != snCmd) {
                              String defectBar = snCmd.getDefectWareBarcode();
                              String sn = snCmd.getSn();
                              snDefects.add(SkuCategoryProvider.concatSkuAttrId(sn, defectBar));
                              if (null != snDefects) {
                                  if (null != insideContainerSkuAttrIdsSnDefect.get(outboundBoxCode)) {
                                      Map<String, Set<String>> skuAttrIdsDefect = insideContainerSkuAttrIdsSnDefect.get(outboundBoxCode);
                                      if (null != skuAttrIdsDefect.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd))) {
                                          Set<String> defects = skuAttrIdsDefect.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
                                          defects.addAll(snDefects);
                                          skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), defects);
                                          insideContainerSkuAttrIdsSnDefect.put(outboundBoxCode, skuAttrIdsDefect);
                                      } else {
                                          skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snDefects);
                                          insideContainerSkuAttrIdsSnDefect.put(outboundBoxCode, skuAttrIdsDefect);
                                      }
                                  } else {
                                      Map<String, Set<String>> skuAttrIdsDefect = new HashMap<String, Set<String>>();
                                      skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snDefects);
                                      insideContainerSkuAttrIdsSnDefect.put(outboundBoxCode, skuAttrIdsDefect);
                                  }
                              }
                             
                          }
                      }
                  }
        	}
        	outBoundBoxSkuIds.put(outboundBoxCode, skuIds);//出库箱所有sku种类
        	outBoundBoxSkuIdQtys.put(outboundBoxCode, skuIdsQty);//出库箱中每个sku的数量
        	outBoundBoxSkuIdSkuAttrIds.put(outboundBoxCode, skuAttrIds);//出库箱中唯一SKU
        	outBoundBoxSkuIdSkuAttrIdQtys.put(outboundBoxCode, skuAttrIdsQty);//出库箱中唯一sku总件数
        	outBoundBoxSkuAttrIdsSnDefect=insideContainerSkuAttrIdsSnDefect;//容器内唯一sku对应所有sn及残次条码
        	outBoundBoxSkuQty.put(outboundBoxCode, skuQty);//出库箱总SKU数量
            isrCmd.setOutBoundBoxCode(outboundBoxCode);
        	isrCmd.setOutBoundBoxSkuQty(outBoundBoxSkuQty);
        	isrCmd.setOutBoundBoxSkuIds(outBoundBoxSkuIds);
        	isrCmd.setOutBoundBoxSkuIdQtys(outBoundBoxSkuIdQtys);
        	isrCmd.setOutBoundBoxSkuIdSkuAttrIds(outBoundBoxSkuIdSkuAttrIds);
        	isrCmd.setOutBoundBoxSkuIdSkuAttrIdQtys(outBoundBoxSkuIdSkuAttrIdQtys);
        	isrCmd.setOutBoundBoxSkuAttrIdsSnDefect(outBoundBoxSkuAttrIdsSnDefect);
        }
        return isrCmd;
    } 
    
   /**
    * 随机获取一个SKU进行提示扫描
    * @param outboundBoxCode
    * @param locationId
    * @param locSkuAttrIds
    * @param logId
    * @return
    */
    private String getTipSkuFromBox(String outboundBoxCode,  Map<String, Set<String>> locSkuAttrIds, String logId) {
        String tipSku = "";
        Set<String> skuAttrIds = locSkuAttrIds.get(outboundBoxCode);
        TipScanSkuCacheCommand cacheSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + outboundBoxCode);
        ArrayDeque<String> cacheSkuAttrIds = null;
        if (null != cacheSkuCmd) { 
            cacheSkuAttrIds = cacheSkuCmd.getScanSkuAttrIds();
        }
        if (null != cacheSkuAttrIds && !cacheSkuAttrIds.isEmpty()) {
            String value = cacheSkuAttrIds.getFirst();
            tipSku = value;
        } else {
            // 随机提示一个
            for (String sId : skuAttrIds) {
                if (!StringUtils.isEmpty(sId)) {
                    tipSku = sId;
                    TipScanSkuCacheCommand tipCmd = new TipScanSkuCacheCommand();
//                    tipCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
                    tipCmd.setInsideContainerCode(outboundBoxCode);
                    ArrayDeque<String> tipSkuAttrIds = new ArrayDeque<String>();
                    tipSkuAttrIds.addFirst(tipSku);
                    tipCmd.setScanSkuAttrIds(tipSkuAttrIds);
                    //缓存随机获取一个SKU的信息
                    cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + outboundBoxCode, tipCmd, CacheConstants.CACHE_ONE_DAY);
                    break;
                }
            }
        }
        return tipSku;
    }
    
//    /**
//     * 库存信息分析统计流程
//     * @param invList
//     * @param ouId
//     * @param logId
//     * @param containerCmd
//     * @param srCmd
//     * @param putWay
//     * @return
//     */
//    private InventoryStatisticResultCommand cacheContainerInventoryStatistics(List<WhSkuInventoryCommand> invList,Long userId,Long ouId,String logId,String outboundBoxCode) {
//           log.info("PdaOutBoundBoxMoveManagerImpl cacheContainerInventoryStatistics is start"); 
//            Long containerId = containerCmd.getId(); 
//            String containerCode = containerCmd.getCode(); 
//            // 3.库存信息统计
//            InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
//            if(null == isrCmd) {
//                    Set<Long> insideContainerIds = new HashSet<Long>();// 所有内部容器
//                    Set<Long> caselevelContainerIds = new HashSet<Long>();// 所有caselevel内部容器
//                    Set<Long> notcaselevelContainerIds = new HashSet<Long>();// 所有非caselevel内部容器
//                    Set<Long> skuIds = new HashSet<Long>();// 所有sku种类
//                    Long skuQty = 0L;// sku总件数
//                    Set<String> skuAttrIds = new HashSet<String>();// 所有唯一sku(包含库存属性)
//                    Set<Long> storeIds = new HashSet<Long>();// 所有店铺
//                    Set<Long> locationIds = new HashSet<Long>();// 所有推荐库位
//                    Map<Long, Set<Long>> insideContainerSkuIds = new HashMap<Long, Set<Long>>();// 内部容器对应的所有sku种类
//                    Map<Long, Long> insideContainerSkuQty = new HashMap<Long, Long>();// 内部容器所有sku总件数
//                    Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = new HashMap<Long, Map<Long, Long>>();// 内部容器单个sku总件数
//                    Map<Long, Set<String>> insideContainerSkuAttrIds = new HashMap<Long, Set<String>>();// 内部容器唯一sku(skuId|库存装填|库存类型|生产日期|失效日期|库存属性1|库存属性2|库存属性3|库存属性4|库存属性51|)
//                    Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = new HashMap<Long, Map<String, Long>>();// 内部容器唯一sku总件数
//                    Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = new HashMap<Long, Map<Long, Set<String>>>();   /** 内部容器推荐库位对应唯一sku及残次条码 */
//                    Map<Long, Set<String>> locSkuAttrIds = new HashMap<Long, Set<String>>();
//                    /** 内部容器唯一sku对应所有残次条码 和sn*/
//                    Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = new HashMap<Long, Map<String, Set<String>>>(); //内部容器内唯一sku对应所有sn及残次条码
//                    Double outerContainerWeight = 0.0;
//                    Double outerContainerVolume = 0.0;
//                    Map<Long, Double> insideContainerWeight = new HashMap<Long, Double>();// 内部容器重量
//                    Map<Long, Double> insideContainerVolume = new HashMap<Long, Double>();// 内部容器体积
//                    Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();   //长度，度量单位转换率
//                    Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();  //重量，度量单位转换率
//                    List<UomCommand> lenUomCmds = null;   //长度度量单位
//                    List<UomCommand> weightUomCmds = null;   //重量度量单位
//                    Map<Long, ContainerAssist> insideContainerAsists = new HashMap<Long, ContainerAssist>();
//                    SimpleCubeCalculator cubeCalculator = new SimpleCubeCalculator(lenUomConversionRate);
//                    SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(weightUomConversionRate);
//                        lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
//                        for (UomCommand lenUom : lenUomCmds) {
//                            String uomCode = "";
//                            Double uomRate = 0.0;
//                            if (null != lenUom) {
//                                uomCode = lenUom.getUomCode();
//                                uomRate = lenUom.getConversionRate();
//                                lenUomConversionRate.put(uomCode, uomRate);
//                            }
//                        }
//                        weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
//                        for (UomCommand lenUom : weightUomCmds) {
//                            String uomCode = "";
//                            Double uomRate = 0.0;
//                            if (null != lenUom) {
//                                uomCode = lenUom.getUomCode();
//                                uomRate = lenUom.getConversionRate();
//                                weightUomConversionRate.put(uomCode, uomRate);
//                            }
//                        }
//                        isrCmd = new InventoryStatisticResultCommand();
//                        for (WhSkuInventoryCommand invCmd : invList) { 
//                            String skuAttrId = SkuCategoryProvider.getSkuAttrIdByInv(invCmd);
//                            String asnCode = invCmd.getOccupationCode();
////                            if (StringUtils.isEmpty(asnCode)) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("rcvd inv info error, containerCode is:[{}], logId is:[{}]", containerCode, logId);
////                                throw new BusinessException(ErrorCodes.RCVD_INV_INFO_NOT_OCCUPY_ERROR);
////                            }
////                            WhAsn asn = whAsnDao.findAsnByCodeAndOuId(asnCode, ouId);
////                            if (null == asn) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("asn is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
////                                throw new BusinessException(ErrorCodes.COMMON_ASN_IS_NULL_ERROR, new Object[] {asnCode});
////                            }
////                            if (PoAsnStatus.ASN_RCVD_FINISH != asn.getStatus() && PoAsnStatus.ASN_RCVD != asn.getStatus()) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("asn status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
////                                throw new BusinessException(ErrorCodes.COMMON_ASN_STATUS_ERROR, new Object[] {asnCode});
////                            }
////                            Long poId = asn.getPoId();
////                            WhPo po = whPoDao.findWhPoById(poId, ouId);
////                            if (null == po) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("po is null error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
////                                throw new BusinessException(ErrorCodes.PO_NULL);
////                            }
////                            String poCode = po.getPoCode();
////                            if (PoAsnStatus.PO_RCVD != po.getStatus() && PoAsnStatus.PO_RCVD_FINISH != po.getStatus()) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("po status error! containerCode is:[{}], logId is:[{}]", containerCode, logId);
////                                throw new BusinessException(ErrorCodes.COMMON_PO_STATUS_ERROR, new Object[] {poCode});
////                            }
////                            Long icId = invCmd.getInsideContainerId();
////                            Container ic = null;
////                            if (null == icId || null == (ic = containerDao.findByIdExt(icId, ouId))) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("sys guide pallet putaway inside container is not found, icId is:[{}], logId is:[{}]", icId, logId);
////                                throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
////                            } else {
////                                insideContainerIds.add(icId);   //统计所有内部容器
////                                srCmd.setHasInsideContainer(true);
////                            }
////                            // 验证容器状态是否可用
////                            if (!BaseModel.LIFECYCLE_NORMAL.equals(ic.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != ic.getLifecycle()) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("sys guide pallet putaway inside container lifecycle is not normal, icId is:[{}], logId is:[{}]", icId, logId);
////                                throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
////                            }
////                            // 获取容器状态
////                            Integer icStatus = ic.getStatus();
////                            if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != icStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != icStatus) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("sys guide pallet putaway inside container status is invalid, icId is:[{}], containerStatus is:[{}], logId is:[{}]", icId, icStatus, logId);
////                                throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {ic.getCode()});
////                            }
////                            Long insideContainerCate = ic.getTwoLevelType();   
////                            Container2ndCategory insideContainer2 = container2ndCategoryDao.findByIdExt(insideContainerCate, ouId);
////                            if (null == insideContainer2) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("sys guide pallet putaway container2ndCategory is null error, icId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", icId, insideContainerCate, logId);
////                                throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
////                            }
////                            if (1 != insideContainer2.getLifecycle()) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("sys guide pallet putaway container2ndCategory lifecycle is not normal error, icId is:[{}], containerId is:[{}], logId is:[{}]", icId, insideContainer2.getId(), logId);
////                                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
////                            }
//                            Double icLength = insideContainer2.getLength();
//                            Double icWidth = insideContainer2.getWidth();
//                            Double icHeight = insideContainer2.getHigh();
//                            Double icWeight = insideContainer2.getWeight();
//                            if (null == icLength || null == icWidth || null == icHeight) {
//                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                log.error("sys guide pallet putaway inside container length、width、height is null error, icId is:[{}], logId is:[{}]", icId, logId);
//                                throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
//                            }
//                            if (null == icWeight) {
//                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                log.error("sys guide pallet putaway inside container weight is null error, icId is:[{}], logId is:[{}]", icId, logId);
//                                throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
//                            }
//                            Double icVolume = cubeCalculator.calculateStuffVolume(icLength, icWidth, icHeight);  //根据长宽高，返回容器体积
//                            insideContainerVolume.put(icId, icVolume);
//                            WhCarton carton = whCartonDao.findWhCaselevelCartonByContainerId(icId,ouId);
//                            if (null != carton) {
//                                caselevelContainerIds.add(icId);  //统计caselevel内部容器信息
//                            } else {
//                                notcaselevelContainerIds.add(icId);   //统计非caselevel内部容器信息
//                            }
//                            String invType = invCmd.getInvType();
////                            if (StringUtils.isEmpty(invType)) {
////                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
////                                log.error("inv type is null error, logId is:[{}]", logId);
////                                throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
////                            }
//                            List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_TYPE, invType, BaseModel.LIFECYCLE_NORMAL);  //根据字段组编码及参数值查询字典信息
//                            if (null == invTypeList || 0 == invTypeList.size()) {
//                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                log.error("inv type is not defined error, invType is:[{}], logId is:[{}]", invType, logId);
//                                throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
//                            }
//                            Long invStatus = invCmd.getInvStatus();
//                            if (null == invStatus) {
//                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                log.error("inv status is null error, logId is:[{}]", logId);
//                                throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
//                            }
//                            InventoryStatus status = new InventoryStatus();
//                            status.setId(invStatus);
//                            List<InventoryStatus> invStatusList = inventoryStatusManager.findInventoryStatusList(status);
//                            if (null == invStatusList || 0 == invStatusList.size()) {
//                                pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                log.error("inv status is not defined error, invStatusId is:[{}], logId is:[{}]", invStatus, logId);
//                                throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
//                            }
//                            String invAttr1 = invCmd.getInvAttr1();
//                            if (!StringUtils.isEmpty(invAttr1)) {
//                                List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_1, invAttr1, BaseModel.LIFECYCLE_NORMAL);
//                                if (null == list || 0 == list.size()) {
//                                    pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                    log.error("inv attr1 is not defined error, invAttr1 is:[{}], logId is:[{}]", invAttr1, logId);
//                                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR1_NOT_FOUND_ERROR, new Object[] {invAttr1});
//                                }
//                            }
//                            String invAttr2 = invCmd.getInvAttr2();
//                            if (!StringUtils.isEmpty(invAttr2)) {
//                                List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_2, invAttr2, BaseModel.LIFECYCLE_NORMAL);
//                                if (null == list || 0 == list.size()) {
//                                    pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                    log.error("inv attr2 is not defined error, invAttr2 is:[{}], logId is:[{}]", invAttr2, logId);
//                                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR1_NOT_FOUND_ERROR, new Object[] {invAttr2});
//                                }
//                            }
//                            String invAttr3 = invCmd.getInvAttr3();
//                            if (!StringUtils.isEmpty(invAttr3)) {
//                                List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_3, invAttr3, BaseModel.LIFECYCLE_NORMAL);
//                                if (null == list || 0 == list.size()) {
//                                    pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                    log.error("inv attr3 is not defined error, invAttr3 is:[{}], logId is:[{}]", invAttr3, logId);
//                                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR3_NOT_FOUND_ERROR, new Object[] {invAttr3});
//                                }
//                            }
//                            String invAttr4 = invCmd.getInvAttr4();
//                            if (!StringUtils.isEmpty(invAttr4)) {
//                                List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_4, invAttr4, BaseModel.LIFECYCLE_NORMAL);
//                                if (null == list || 0 == list.size()) {
//                                    pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                    log.error("inv attr4 is not defined error, invAttr4 is:[{}], logId is:[{}]", invAttr4, logId);
//                                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR4_NOT_FOUND_ERROR, new Object[] {invAttr4});
//                                }
//                            }
//                            String invAttr5 = invCmd.getInvAttr5();
//                            if (!StringUtils.isEmpty(invAttr5)) {
//                                List<SysDictionary> list = sysDictionaryManager.getListByGroupAndDicValue(Constants.INVENTORY_ATTR_5, invAttr5, BaseModel.LIFECYCLE_NORMAL);
//                                if (null == list || 0 == list.size()) {
//                                    pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                    log.error("inv attr5 is not defined error, invAttr5 is:[{}], logId is:[{}]", invAttr5, logId);
//                                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR5_NOT_FOUND_ERROR, new Object[] {invAttr5});
//                                }
//                            }
//                            Long skuId = invCmd.getSkuId();
//                            Double toBefillQty = invCmd.getToBeFilledQty();   //待移入库存 
//                            Double onHandQty = invCmd.getOnHandQty();   //在库库存
//                            Double curerntSkuQty = 0.0;     //当前sku数量
//                            Long locationId = invCmd.getLocationId();
//                            if (null != locationId) {
//                                locationIds.add(locationId);    //所有推荐库位
//                                if (null != toBefillQty) {
//                                    curerntSkuQty = toBefillQty;
//                                    skuQty += toBefillQty.longValue();
//                                }
//                            } else {
//                                if (null == onHandQty || 0 <= new Double("0.0").compareTo(onHandQty)) {
//                                    pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                    log.error("rcvd inv onHandQty is less than 0 error, logId is:[{}]", logId);
//                                    throw new BusinessException(ErrorCodes.RCVD_INV_SKU_QTY_ERROR);
//                                }
//                                if (null != onHandQty) {
//                                    curerntSkuQty = onHandQty;
//                                    skuQty += onHandQty.longValue();
//                                }
//                            }
//                            if (null != skuId) {
//                                skuIds.add(skuId);    //所有sku种类数
//                                WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
//                                if (null == skuCmd) {
//                                    pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                    log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", skuId, logId);
//                                    throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
//                                }
//                                Double skuLength = skuCmd.getLength();
//                                Double skuWidth = skuCmd.getWidth();
//                                Double skuHeight = skuCmd.getHeight();
//                                Double skuWeight = skuCmd.getWeight();
//                                if (null == skuLength || null == skuWidth || null == skuHeight) {
//                                    pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                    log.error("sys guide pallet putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
//                                    throw new BusinessException(ErrorCodes.SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
//                                }
//                                if (null == skuWeight) {
//                                    pdaPutawayCacheManager.sysGuidePutawayRemoveInventory(containerCmd, ouId, logId);
//                                    log.error("sys guide pallet putaway sku weight is null error, skuId is:[{}], logId is:[{}]", skuId, logId);
//                                    throw new BusinessException(ErrorCodes.SKU_WEIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
//                                }
//                                if (null != insideContainerWeight.get(icId)) {
//                                    insideContainerWeight.put(icId, insideContainerWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
//                                } else {
//                                    // 先计算容器自重
//                                    insideContainerWeight.put(icId, weightCalculator.calculateStuffWeight(icWeight));
//                                    // 再计算当前商品重量
//                                    insideContainerWeight.put(icId, insideContainerWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
//                                }
//                            }
//                            skuAttrIds.add(skuAttrId);    //所有唯一的sku(包含库存属性)
//                            if(null != locationId) {
//                                locSkuAttrIds.put(locationId, skuAttrIds);
//                            }
//                            insideContainerLocSkuAttrIds.put(icId, locSkuAttrIds);
//                            Long stroeId = invCmd.getStoreId();
//                            if (null != stroeId) {
//                                storeIds.add(stroeId);  //统计所有店铺
//                            }
//                            if (null != insideContainerSkuIds.get(icId)) {
//                                Set<Long> icSkus = insideContainerSkuIds.get(icId);
//                                icSkus.add(skuId);
//                                insideContainerSkuIds.put(icId, icSkus);   //统计内部容器对应所有的sku
//                            } else {
//                                Set<Long> icSkus = new HashSet<Long>();
//                                icSkus.add(skuId);
//                                insideContainerSkuIds.put(icId, icSkus);
//                            }
//                            if (null != insideContainerSkuQty.get(icId)) {
//                                insideContainerSkuQty.put(icId, insideContainerSkuQty.get(icId) + curerntSkuQty.longValue());  //统计内部容器对应所有sku的总件数
//                            } else {
//                                insideContainerSkuQty.put(icId, curerntSkuQty.longValue());  
//                            }
//                            if (null != insideContainerSkuIdsQty.get(icId)) {
//                                Map<Long, Long> skuIdsQty = insideContainerSkuIdsQty.get(icId);
//                                if (null != skuIdsQty.get(skuId)) {
//                                    skuIdsQty.put(skuId, skuIdsQty.get(skuId) + curerntSkuQty.longValue());
//                                } else {
//                                    skuIdsQty.put(skuId, curerntSkuQty.longValue());
//                                }
//                            } else {
//                                Map<Long, Long> sq = new HashMap<Long, Long>();
//                                sq.put(skuId, curerntSkuQty.longValue());
//                                insideContainerSkuIdsQty.put(icId, sq);                     //统计内部容器对应某个sku的总件数
//                            }
//                            if (null != insideContainerSkuAttrIds.get(icId)) {
//                                Set<String> icSkus = insideContainerSkuAttrIds.get(icId);
//                                icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
//                                insideContainerSkuAttrIds.put(icId, icSkus);                        
//                            } else {
//                                Set<String> icSkus = new HashSet<String>();
//                                icSkus.add(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
//                                insideContainerSkuAttrIds.put(icId, icSkus);  //统计内部容器所有的唯一sku
//                            }
//                            if (null != insideContainerSkuAttrIdsQty.get(icId)) {
//                                Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(icId);
//                                if (null != skuAttrIdsQty.get(skuId)) {
//                                    skuAttrIdsQty.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), skuAttrIdsQty.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd)) + curerntSkuQty.longValue());
//                                } else {
//                                    skuAttrIdsQty.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
//                                }
//                            } else {
//                                Map<String, Long> saq = new HashMap<String, Long>();
//                                saq.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), curerntSkuQty.longValue());
//                                insideContainerSkuAttrIdsQty.put(icId, saq);
//                            }
//                            if (null == insideContainerAsists.get(icId)) {
//                                ContainerAssist containerAssist = new ContainerAssist();
//                                containerAssist.setContainerId(icId);
//                                containerAssist.setSysLength(icLength);
//                                containerAssist.setSysWidth(icWidth);
//                                containerAssist.setSysHeight(icHeight);
//                                containerAssist.setSysVolume(icVolume);
//                                containerAssist.setCartonQty(1L);
//                                containerAssist.setCreateTime(new Date());
//                                containerAssist.setLastModifyTime(new Date());
//                                containerAssist.setOperatorId(userId);
//                                containerAssist.setOuId(ouId);
//                                containerAssist.setLifecycle(BaseModel.LIFECYCLE_NORMAL);
//                                insideContainerAsists.put(icId, containerAssist);
//                            }
//                            if(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {  //拆箱上架
//                              //内部容器内唯一sku对应所有sn及残次条码
//                                List<WhSkuInventorySnCommand> snCmdList = invCmd.getWhSkuInventorySnCommandList();
//                                Set<String> snDefects = null;
//                                if (null != snCmdList && 0 < snCmdList.size()) {
//                                    snDefects = new HashSet<String>();
//                                    for (WhSkuInventorySnCommand snCmd : snCmdList) {
//                                        if (null != snCmd) {
//                                            String defectBar = snCmd.getDefectWareBarcode();
//                                            String sn = snCmd.getSn();
//                                            snDefects.add(SkuCategoryProvider.concatSkuAttrId(sn, defectBar));
//                                        }
//                                    }
//                                }
//                                if (null != snDefects) {
//                                    if (null != insideContainerSkuAttrIdsSnDefect.get(icId)) {
//                                        Map<String, Set<String>> skuAttrIdsDefect = insideContainerSkuAttrIdsSnDefect.get(icId);
//                                        if (null != skuAttrIdsDefect.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd))) {
//                                            Set<String> defects = skuAttrIdsDefect.get(SkuCategoryProvider.getSkuAttrIdByInv(invCmd));
//                                            defects.addAll(snDefects);
//                                            skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), defects);
//                                            insideContainerSkuAttrIdsSnDefect.put(icId, skuAttrIdsDefect);
//                                        } else {
//                                            skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snDefects);
//                                            insideContainerSkuAttrIdsSnDefect.put(icId, skuAttrIdsDefect);
//                                        }
//                                    } else {
//                                        Map<String, Set<String>> skuAttrIdsDefect = new HashMap<String, Set<String>>();
//                                        skuAttrIdsDefect.put(SkuCategoryProvider.getSkuAttrIdByInv(invCmd), snDefects);
//                                        insideContainerSkuAttrIdsSnDefect.put(icId, skuAttrIdsDefect);
//                                    }
//                                }
//                            }
//                            
//                        }
//                        
//                        isrCmd.setPutawayPatternDetailType(putawayPatternDetailType);
//                        isrCmd.setHasOuterContainer(true);
//                        isrCmd.setInsideContainerIds(insideContainerIds);  //所有内部容器
//                        isrCmd.setCaselevelContainerIds(notcaselevelContainerIds);  //所有caselevel内部容器
//                        isrCmd.setNotcaselevelContainerIds(notcaselevelContainerIds);  //所有非caselevel内部容器
//                        isrCmd.setSkuIds(skuIds);   // 所有sku种类
//                        isrCmd.setSkuQty(skuQty);// sku总件数
//                        isrCmd.setSkuAttrIds(skuAttrIds);   // 所有唯一sku(包含库存属性)
//                        isrCmd.setStoreIds(storeIds); // 所有店铺
//                        isrCmd.setLocationIds(locationIds);// 所有推荐库位
//                        isrCmd.setInsideContainerSkuIdsQty(insideContainerSkuIdsQty);    // 内部容器对应的所有sku总件数
//                        isrCmd.setInsideContainerSkuIds(insideContainerSkuIds);  // 内部容器对应的所有sku种类
//                        isrCmd.setInsideContainerSkuQty(insideContainerSkuQty);
//                        isrCmd.setInsideContainerSkuAttrIdsQty(insideContainerSkuAttrIdsQty);  // 内部容器单个sku总件数
//                        isrCmd.setInsideContainerSkuAttrIds(insideContainerSkuAttrIds);// 内部容器唯一sku(skuId|库存装填|库存类型|生产日期|失效日期|库存属性1|库存属性2|库存属性3|库存属性4|库存属性51|)
//                        isrCmd.setInsideContainerSkuAttrIdsSnDefect(insideContainerSkuAttrIdsSnDefect); //内部容器内唯一sku对应所有sn及残次条码
//                        isrCmd.setInsideContainerLocSkuAttrIds(insideContainerLocSkuAttrIds);
//                        if(WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType){   //整托上架
//                            isrCmd.setOuterContainerCode(containerCmd.getCode());  //外部容器号
//                        }else{//整箱上架,拆箱上架
//                            isrCmd.setOuterContainerCode(outerContainerCode);  //外部容器号
//                            isrCmd.setInsideContainerCode(containerCmd.getCode());   // 当前扫描的内部容器
//                        }
//                       
//                        isrCmd.setInsideContainerAsists(insideContainerAsists);
//                        isrCmd.setOuterContainerVolume(outerContainerVolume);  //外部容器体积
//                        isrCmd.setOuterContainerWeight(outerContainerWeight);  //外部容器自重
//                        if(WhPutawayPatternDetailType.PALLET_PUTAWAY == putawayPatternDetailType) {
//                            isrCmd.setOuterContainerId(containerId);   //外部容器id(整托的时候是外部id)
//                        }
//                        if(WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
//                            isrCmd.setInsideContainerId(containerId);   //整箱上架时,是内部id
//                        }
//                    // 计算外部容器体积重量
//                    Long outerContainerCate = containerCmd.getTwoLevelType();
//                    Container2ndCategory outerContainer2 = container2ndCategoryDao.findByIdExt(outerContainerCate, ouId);
//                    if (null == outerContainer2) {
//                        log.error("container2ndCategory is null error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", containerId, outerContainerCate, logId);
//                        throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
//                    }
//                    if (1 != outerContainer2.getLifecycle()) {
//                        log.error("container2ndCategory lifecycle is not normal error, cId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", containerId, outerContainer2.getId(), logId);
//                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
//                    }
//                    Double ocLength = outerContainer2.getLength();
//                    Double ocWidth = outerContainer2.getWidth();
//                    Double ocHeight = outerContainer2.getHigh();
//                    Double ocWeight = outerContainer2.getWeight();
//                    if (null == ocLength || null == ocWidth || null == ocHeight) {
//                        log.error("sys guide pallet putaway inside container length、width、height is null error, ocId is:[{}], logId is:[{}]", containerId, logId);
//                        throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {containerCode});
//                    }
//                    if (null == ocWeight) {
//                        log.error("sys guide pallet putaway inside container weight is null error, ocId is:[{}], logId is:[{}]", containerId, logId);
//                        throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {containerCode});
//                    }
//                    outerContainerWeight = weightCalculator.calculateStuffWeight(ocWeight);
//                    outerContainerVolume = cubeCalculator.calculateStuffVolume(ocLength, ocWidth, ocHeight);
//                    isrCmd.setInsideContainerVolume(insideContainerVolume);
//                    isrCmd.setInsideContainerWeight(insideContainerWeight);
//            }
//            log.info("PdaOutBoundBoxMoveManagerImpl cacheContainerInventoryStatistics is end"); 
//            return isrCmd;
//    }
    
    /**
     * 提示库位编码
     * @param isrCmd
     * @param srCmd
     * @param ouId
     * @return
     */
    private ScanResultCommand reminderLocation(InventoryStatisticResultCommand isrCmd,ScanResultCommand srCmd,Long ouId) {
        log.info("PdaOutBoundBoxMoveManagerImpl isSuggestLocation is start"); 
        Set<Long> locationIds = isrCmd.getLocationIds(); 
        if (0 < locationIds.size()) {
            srCmd.setRecommendLocation(true);// 已推荐库位
            if (1 < locationIds.size()) {   //上架已经推荐库位而且绑架多个库位，认定异常
                log.error("sys guide pallet putaway location is more than one error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
            }
            Long locId = null;
            for (Long locationId : locationIds) {
                if (null == locId) {
                    locId = locationId;
                    if (null != locId) break;
                }
            }
            Location loc = locationDao.findByIdExt(locId, ouId);
            if (null == loc) {
                log.error("location is null error, locId is:[{}], logId is:[{}]", locId, logId);
                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
            }
            srCmd.setTipLocationCode(loc.getCode());// 提示库位编码
            srCmd.setNeedTipLocation(true);// 提示库位
            srCmd.setRecommendFail(false);   //已经存在库位
            srCmd.setContainerType(WhContainerType.INSIDE_CONTAINER);   //扫描的是内部容器
            srCmd.setTipLocBarCode(loc.getBarCode()); 
        }
        log.info("PdaOutBoundBoxMoveManagerImpl isSuggestLocation is end"); 
        return srCmd;
    }
    
    
//    /**
//     * 拆箱上架：没有推荐库位分支
//     * @param isrCommand
//     * @param ouId
//     * @param userId
//     */
//    private ScanResultCommand splitPutWayNoLocation(InventoryStatisticResultCommand  isrCommand,Long ouId,Long userId,ScanResultCommand srCmd,Long funcId,ContainerCommand containerCmd,List<WhSkuInventoryCommand>  invList,Integer putawayPatternDetailType,Warehouse warehouse) {
//        log.info("PdaOutBoundBoxMoveManagerImpl updateContainer is start"); 
//        Long containerId = containerCmd.getId();  //货箱id
//        String containerCode = containerCmd.getCode();  //货箱号
//        // 内部容器辅助表信息
//        Map<Long, ContainerAssist> caMap = new HashMap<Long, ContainerAssist>();
//        List<Long> containerids = new ArrayList<Long> ();
//        containerids.add(containerId);
//        //修改当前内部容器状态
//        Container container = containerDao.findByIdExt(containerId , ouId);
//        container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);   //上架中
//        container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);  //占用中
//        containerDao.saveOrUpdateByVersion(container);
//        //更新容器辅助表:先根据扫描的容器Id删除容器辅助表中已经存在的记录行,再将新的统计信息更新到容器辅助表
//        containerAssistDao.deleteByContainerIds(ouId, containerids);
//        Map<Long,Double> insideContainerWeight = isrCommand.getInsideContainerWeight();
//        Map<Long,Double> insideContainerVolume = isrCommand.getInsideContainerVolume();
//        //增加新的容器辅助表信息,只更新内部容器
//        Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(containerCmd.getTwoLevelType(), ouId);
//        ContainerAssist cAssist = new ContainerAssist();
//        cAssist.setContainerId(containerId);
//        cAssist.setSysLength(c2c.getLength());
//        cAssist.setSysWidth(c2c.getWidth());
//        cAssist.setSysHeight(c2c.getHigh());
//        cAssist.setSysVolume(insideContainerVolume.get(containerId));
//        cAssist.setSysWeight(insideContainerWeight.get(containerId));
//        cAssist.setCartonQty(Long.valueOf(isrCommand.getInsideContainerIds().size()));   //托盘内部所有容器数
//        cAssist.setSkuAttrCategory(Long.valueOf(isrCommand.getSkuAttrIds().size()));   //sku种类数
//        cAssist.setSkuQty(isrCommand.getSkuQty());   //sku总件数
//        cAssist.setStoreQty(Long.valueOf(isrCommand.getStoreIds().size()));
//        cAssist.setSkuCategory(Long.valueOf(isrCommand.getSkuIds().size()));
//        cAssist.setCreateTime(new Date());
//        cAssist.setOperatorId(userId);
//        cAssist.setOuId(ouId);
//        cAssist.setLastModifyTime(new Date());
//        containerAssistDao.insert(cAssist);
//        insertGlobalLog(GLOBAL_LOG_INSERT, cAssist, ouId, userId, null, null);
//        //添加内部容器辅助信息
//        caMap.put(containerId, cAssist);
//        // 6.匹配上架规则
//        List<String> icCodeList = new ArrayList<String>();
//        icCodeList.add(isrCommand.getInsideContainerCode());
//        List<Long> storeList = new ArrayList<Long>();
//        CollectionUtils.addAll(storeList, isrCommand.getStoreIds().iterator());
//        List<Long> icIdList = new ArrayList<Long>();
//        CollectionUtils.addAll(icIdList, isrCommand.getInsideContainerIds().iterator());
//        RuleAfferCommand ruleAffer = new RuleAfferCommand();
//        ruleAffer.setLogId(logId);
//        ruleAffer.setOuid(ouId);
//        ruleAffer.setAfferContainerCode(containerCode);    //内部容器号
//        ruleAffer.setAfferInsideContainerIdList(icIdList);   //内部容器id
//        ruleAffer.setContainerId(containerId);
//        ruleAffer.setFuncId(funcId);
//        ruleAffer.setAfferContainerCodeList(icCodeList);
//        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE);// 拆箱上架
//        ruleAffer.setStoreIdList(storeList);
//        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
//        // 判断该容器是否有符合的上架规则,有则走库位推荐排队系统,没有抛出异常
//        List<WhSkuInventoryCommand> ruleList = export.getShelveSkuInvCommandList();  //拆箱上架规则返回
//        if (null == ruleList || 0 == ruleList.size()) {
//            srCmd.setRecommendFail(true);   //推荐库位失败
//            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCmd.getId().toString());
//            if(null == isCmd) {
//                //缓存容器库存统计信息
//                pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//            }
//            return srCmd;
//        }
//        // 7.库位推荐排队系统     推荐库位
//        if (null == caMap || 0 == caMap.size()) {
//            log.error("container assist info generate error, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
//        }
//        // 判断排队队列是否已经排队
//        boolean isRecommend = pdaPutawayCacheManager.sysGuidePutawayLocRecommendQueue(containerCmd.getId(), logId);
//        if (false == isRecommend) {  //需要等待排队,否则得到库位推荐执行权
//            srCmd.setNeedQueueUp(true);   //需要排队
//            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCmd.getId().toString());
//            if(null == isCmd) {
//                //缓存容器库存统计信息
//                pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//            }
//            return srCmd;  //跳转到扫描容器页面
//        }else{
//            srCmd.setNeedQueueUp(false);   //需要排队
//        }
//        //推荐库位流程
//        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
//        uomMap.put(WhUomType.LENGTH_UOM, isrCommand.getLenUomConversionRate());
//        uomMap.put(WhUomType.WEIGHT_UOM, isrCommand.getWeightUomConversionRate());
//        List<LocationRecommendResultCommand> lrrList = null;
//        boolean isNoLocRecommend = true;
//        try {
//            lrrList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY, caMap, invList, uomMap, logId);
//            if (null != lrrList) {
//                for (LocationRecommendResultCommand lrrCmd : lrrList) {
//                    if (!StringUtils.isEmpty(lrrCmd.getLocationCode())) {
//                        isNoLocRecommend = false;
//                        break;
//                    }
//                }
//            }
//        } catch (Exception e1) {
//            // 弹出排队队列
//            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
//            throw e1;
//        }
//        
//        if (null == lrrList || 0 == lrrList.size() || isNoLocRecommend) {
//            srCmd.setRecommendFail(true);   //推荐库位失败
//            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCmd.getId().toString());
//            if(null == isCmd) {
//              //缓存容器库存统计信息
//                pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//            }
//            return srCmd;
//        }
//        Set<Long> suggestLocationIds = new HashSet<Long>();
//        Map<String, Long> invRecommendLocId = new HashMap<String, Long>();
//        Map<String, String> invRecommendLocCode = new HashMap<String, String>();
//        Map<Long, Set<String>> locSkuAttrIds = new HashMap<Long, Set<String>>();
//        Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = new HashMap<Long, Map<Long, Set<String>>>();
//        /** 内部容器推荐库位对应唯一sku总件数 */
//        Map<Long, Map<Long, Map<String, Long>>> insideContainerLocSkuAttrIdsQty = new HashMap<Long, Map<Long, Map<String, Long>>>();
//        Map<Long, Map<String, Long>> locSkuAttrIdsQty = new HashMap<Long, Map<String, Long>>();
//        Map<String, Long> skuAttrIdsQty = new HashMap<String, Long>();
//        for (LocationRecommendResultCommand lrrCmd : lrrList) {
//            String skuAttrIds = SkuCategoryProvider.getSkuAttrId(lrrCmd.getSkuAttrId());
//            Long locationId = lrrCmd.getLocationId();
//            if (null != locationId) {
//                suggestLocationIds.add(locationId);
//                if (null != locSkuAttrIds.get(locationId)) {
//                    Set<String> allSkuAttrIds = locSkuAttrIds.get(locationId);
//                    if(null == allSkuAttrIds){
//                        allSkuAttrIds = new HashSet<String>();
//                    }
//                    allSkuAttrIds.add(lrrCmd.getSkuAttrId());
//                    locSkuAttrIds.put(locationId, allSkuAttrIds);
//                    //统计唯一sku,对应的数量
//                    skuAttrIdsQty = locSkuAttrIdsQty.get(locationId);
//                    Long qty = skuAttrIdsQty.get(skuAttrIds);
//                    if(null != qty){
//                        qty += lrrCmd.getQty().longValue();
//                    }else{
//                        qty = lrrCmd.getQty().longValue();
//                    }
//                    skuAttrIdsQty.put(skuAttrIds, qty);
//                } else {
//                    Set<String> allSkuAttrIds = new HashSet<String>();
//                    allSkuAttrIds.add(lrrCmd.getSkuAttrId());
//                    locSkuAttrIds.put(locationId, allSkuAttrIds);
//                    skuAttrIdsQty.put(skuAttrIds, lrrCmd.getQty().longValue());
//                }
//            }
//            locSkuAttrIdsQty.put(locationId, skuAttrIdsQty);
//            String locationCode = lrrCmd.getLocationCode();
//            invRecommendLocId.put(lrrCmd.getSkuAttrId(), locationId);
//            invRecommendLocCode.put(lrrCmd.getSkuAttrId(), locationCode);
//        }
//        if (0 == locSkuAttrIds.size()) {
//            log.error("location recommend failure! containerCode is:[{}], logId is:[{}]", containerCode, logId);
//            throw new BusinessException(ErrorCodes.COMMON_LOCATION_RECOMMEND_ERROR);
//        } else {
//            insideContainerLocSkuAttrIds.put(containerId, locSkuAttrIds);
//            insideContainerLocSkuAttrIdsQty.put(containerId, locSkuAttrIdsQty);
//        }
//        isrCommand.setInsideContainerLocSkuAttrIdsQty(insideContainerLocSkuAttrIdsQty);
//        isrCommand.setLocationIds(suggestLocationIds);
//        isrCommand.setInsideContainerLocSkuAttrIds(insideContainerLocSkuAttrIds);
//        Map<Long, List<Long>> insideContainerLocSort = new HashMap<Long, List<Long>>();// 内部容器所有排序后库位
//        List<Location> sortLocs = new ArrayList<Location>();
//        List<Long> sortLocationIds = new ArrayList<Long>();
//        Map<Long, Set<String>> allLocSkuAttrs = insideContainerLocSkuAttrIds.get(containerId);
//        if (null != allLocSkuAttrs && !allLocSkuAttrs.isEmpty()) {
//            for (Long lId : allLocSkuAttrs.keySet()) {
//                Location loc = locationDao.findByIdExt(lId, ouId);
//                if (null == loc) {
//                    log.error("location is null error, locId is:[{}], logId is:[{}]", lId, logId);
//                    throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
//                }
//                sortLocs.add(loc);
//            }
////            Collections.sort(sortLocs, new LocationShelfSorter());
////            for (Location sortLoc : sortLocs) {
////                sortLocationIds.add(sortLoc.getId());
////            }
//            insideContainerLocSort.put(containerId, sortLocationIds);
//        }
//        isrCommand.setInsideContainerLocSort(insideContainerLocSort);
//        LocationRecommendResultCommand lrr = lrrList.get(0);
//        Long locationId = lrr.getLocationId();  //推荐库位id
//        Location loc = locationDao.findByIdExt(locationId, ouId);
//        if (null == loc) {
//            log.error("location is null error, locId is:[{}], logId is:[{}]", locationId, logId);
//            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
//        }
//        //库位绑定
//        //先待移入库位库存，将on_hand_qty(在库库存)值设置到to_be_filled_qty(待移入库存)、将on_hand_qty设置为0.0、为每行库存记录增加库位插入一条新的库存记录（注意更新uuid），即插入待移入库位库存。如果有SN/残次信息则同样插入SN/残次信息记录（一待入）
//        whSkuInventoryManager.execBinding(invList, warehouse, lrrList, putawayPatternDetailType, ouId, userId, loc.getCode());
//        //缓存容器库存统计信息
//        pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//        // 弹出排队队列
//        pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
//        // 10.提示库位
//        srCmd.setRecommendLocation(true);// 已推荐库位
//        srCmd.setRecommendFail(false);   //推荐库位成功
//        srCmd.setTipLocationCode(loc.getCode());// 提示库位编码
//        srCmd.setContainerType(WhContainerType.INSIDE_CONTAINER);   //扫描的是内部容器
//        srCmd.setNeedTipLocation(true);// 提示库位
//        srCmd.setTipLocBarCode(loc.getBarCode()); 
//        log.info("PdaOutBoundBoxMoveManagerImpl updateContainer is end"); 
//        
//        return srCmd;
//    }
//    
//    /**
//     * 整箱上架：没有推荐库位分支
//     * @param isrCommand
//     * @param ouId
//     * @param userId
//     */
//    private ScanResultCommand containerPutWayNoLocation(InventoryStatisticResultCommand  isrCommand,Long ouId,Long userId,ScanResultCommand srCmd,Long funcId,ContainerCommand containerCmd,List<WhSkuInventoryCommand>  invList,Warehouse warehouse,Integer  putawayPatternDetailType) {
//        log.info("PdaOutBoundBoxMoveManagerImpl updateContainer is start"); 
//        Long containerId = containerCmd.getId();  //货箱id
//        Long c2cId = containerCmd.getTwoLevelType();   
//        // 内部容器辅助表信息
//        Map<Long, ContainerAssist> caMap = new HashMap<Long, ContainerAssist>();
//        Set<Long> insideContainerIds = isrCommand.getInsideContainerIds();  //获得所有内部容器id(整箱上架，就一个内部容器id)
//        //修改当前内部容器状态
//        for(Long insideContainerId:insideContainerIds) {
//            Container container = containerDao.findByIdExt(insideContainerId, ouId);
//            container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);   //上架中
//            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);  //占用中
//            containerDao.saveOrUpdateByVersion(container);
//        }
//        //更新容器辅助表:先根据扫描的容器Id删除容器辅助表中已经存在的记录行,再将新的统计信息更新到容器辅助表
//        List<Long> containerids = new ArrayList<Long> (insideContainerIds);
//        containerAssistDao.deleteByContainerIds(ouId, containerids);
//        Map<Long,Double> insideContainerWeight = isrCommand.getInsideContainerWeight();
//        Map<Long,Double> insideContainerVolume = isrCommand.getInsideContainerVolume();
//        //增加新的容器辅助表信息,只更新内部容器
//        for(Long insideContainerId:insideContainerIds) {
//            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(c2cId, ouId);
//            ContainerAssist cAssist = new ContainerAssist();
//            cAssist.setContainerId(insideContainerId);
//            cAssist.setSysLength(c2c.getLength());
//            cAssist.setSysWidth(c2c.getWidth());
//            cAssist.setSysHeight(c2c.getHigh());
//            cAssist.setSysVolume(insideContainerVolume.get(insideContainerId));
//            cAssist.setSysWeight(insideContainerWeight.get(insideContainerId));
//            cAssist.setCartonQty(Long.valueOf(isrCommand.getInsideContainerIds().size()));   //托盘内部所有容器数
//            cAssist.setSkuAttrCategory(Long.valueOf(isrCommand.getSkuAttrIds().size()));   //sku种类数
//            cAssist.setSkuQty(isrCommand.getSkuQty());   //sku总件数
//            cAssist.setStoreQty(Long.valueOf(isrCommand.getStoreIds().size()));
//            cAssist.setSkuCategory(Long.valueOf(isrCommand.getSkuIds().size()));
//            cAssist.setOuId(ouId);
//            cAssist.setOperatorId(userId);
//            cAssist.setCreateTime(new Date());
//            cAssist.setLastModifyTime(new Date());
//            containerAssistDao.insert(cAssist);
//            caMap.put(insideContainerId,cAssist);
//            insertGlobalLog(GLOBAL_LOG_INSERT, cAssist, ouId, userId, null, null);
//        }
//        // 6.匹配上架规则
//        List<String> icCodeList = new ArrayList<String>();
//        icCodeList.add(isrCommand.getInsideContainerCode());
//        List<Long> storeList = new ArrayList<Long>();
//        CollectionUtils.addAll(storeList, isrCommand.getStoreIds().iterator());
//        List<Long> icIdList = new ArrayList<Long>();
//        CollectionUtils.addAll(icIdList, isrCommand.getInsideContainerIds().iterator());
//        RuleAfferCommand ruleAffer = new RuleAfferCommand();
//        ruleAffer.setLogId(logId);
//        ruleAffer.setOuid(ouId);
//        ruleAffer.setAfferContainerCode(srCmd.getOuterContainerCode());    //原始容器号
//        ruleAffer.setAfferInsideContainerIdList(icIdList);   //内部容器id
//        ruleAffer.setContainerId(containerCmd.getId());
//        ruleAffer.setFuncId(funcId);
//        ruleAffer.setAfferContainerCodeList(icCodeList);
//        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE_ALL);// 整托 、货箱上架规则
//        ruleAffer.setStoreIdList(storeList);
//        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
//        // 判断该容器是否有符合的上架规则,有则走库位推荐排队系统,没有抛出异常
//        List<ShelveRecommendRuleCommand> ruleList = export.getShelveRecommendRuleList();
//        if (null == ruleList || 0 == ruleList.size()) {
//            srCmd.setRecommendFail(true);   //推荐库位失败
//            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCmd.getId().toString());
//            if(null == isCmd) {
//              //缓存容器库存统计信息
//                pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//            }
//            return srCmd;
//        }
//        // 7.库位推荐排队系统     推荐库位
//        if (null == caMap || 0 == caMap.size()) {
//            log.error("container assist info generate error, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
//        }
//        // 判断排队队列是否已经排队
//        boolean isRecommend = pdaPutawayCacheManager.sysGuidePutawayLocRecommendQueue(containerCmd.getId(), logId);
//        if (false == isRecommend) {  //需要等待排队,否则得到库位推荐执行权
//            srCmd.setNeedQueueUp(true);   //需要排队
//            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCmd.getId().toString());
//            if(null == isCmd) {
//              //缓存容器库存统计信息
//                pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//            }
//            return srCmd;  //跳转到扫描容器页面
//        }else{
//            srCmd.setNeedQueueUp(false);   //需要排队
//        }
//        //推荐库位流程
//        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
//        uomMap.put(WhUomType.LENGTH_UOM, isrCommand.getLenUomConversionRate());
//        uomMap.put(WhUomType.WEIGHT_UOM, isrCommand.getWeightUomConversionRate());
//        List<LocationRecommendResultCommand> lrrList = null;
//        try {
//            lrrList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, caMap, invList, uomMap, logId);
//        } catch (Exception e1) {
//            // 弹出排队队列
//            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
//            throw e1;
//        }
//        if (null == lrrList || 0 == lrrList.size() || StringUtils.isEmpty(lrrList.get(0).getLocationCode())) {
//            srCmd.setRecommendFail(true);   //推荐库位失败
//            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCmd.getId().toString());
//            if(null == isCmd) {
//              //缓存容器库存统计信息
//                pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//            }
//            return srCmd;
//        }
//        LocationRecommendResultCommand lrr = lrrList.get(0);
//        Long lrrLocId = lrr.getLocationId();  //推荐库位id
//        String lrrLocCode = lrr.getLocationCode();  //推荐库位号
//        //根据推荐库位号查询库位条码
//        Location loc = locationDao.findLocationByCode(lrrLocCode, ouId);
//        if (null == loc) {
//            log.error("location is null error, locationCode is:[{}], logId is:[{}]", lrrLocCode, logId);
//            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
//        }
//        Set<Long> suggestLocationIds = new HashSet<Long>();
//        suggestLocationIds.add(lrrLocId);
//        isrCommand.setLocationIds(suggestLocationIds);
//        //库位绑定
//        //先待移入库位库存，将on_hand_qty(在库库存)值设置到to_be_filled_qty(待移入库存)、将on_hand_qty设置为0.0、为每行库存记录增加库位插入一条新的库存记录（注意更新uuid），即插入待移入库位库存。如果有SN/残次信息则同样插入SN/残次信息记录（一待入）
//        whSkuInventoryManager.execBinding(invList, warehouse, lrrList, putawayPatternDetailType, ouId, userId, lrrLocCode);
//        // 10.缓存容器库存统计信息
//        pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//        // 弹出排队队列
//        pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
//        // 10.提示库位
//        srCmd.setRecommendFail(false);   //推荐库位成功
//        srCmd.setRecommendLocation(true);// 已推荐库位
//        srCmd.setTipLocationCode(lrrLocCode);// 提示库位编码
//        srCmd.setNeedTipLocation(true);// 提示库位
//        srCmd.setTipLocBarCode(loc.getBarCode()); 
//        log.info("PdaOutBoundBoxMoveManagerImpl updateContainer is end"); 
//        return srCmd;
//    }
//    
//    /**
//     * 整托上架：没有推荐库位分支
//     * @param isrCommand
//     * @param ouId
//     * @param userId
//     */
//    private ScanResultCommand palletPutwayNoLocation(InventoryStatisticResultCommand  isrCommand,Long ouId,Long userId,ScanResultCommand srCmd,Long funcId,ContainerCommand containerCmd,List<WhSkuInventoryCommand>  invList,Warehouse warehouse,Integer putawayPatternDetailType) {
//        log.info("PdaOutBoundBoxMoveManagerImpl updateContainer is start"); 
//        Long containerId = containerCmd.getId();  //外部容器id
//        Long outerContainerCate = containerCmd.getTwoLevelType();
//        String outContainerCode = containerCmd.getCode();  //外部容器号
//        // 内部容器辅助表信息
//        Map<Long, ContainerAssist> caMap = new HashMap<Long, ContainerAssist>();
//        Long outerContainerId = isrCommand.getOuterContainerId();   //获取外部容器id
//        Set<Long> insideContainerIds = isrCommand.getInsideContainerIds();  //获得所有内部容器id
//        List<Long> containerids = new ArrayList<Long> ();
//        CollectionUtils.addAll(containerids, insideContainerIds.iterator());
//        //修改所有内部容器状态
//        List<String> icCodeList = new ArrayList<String>();
//        for(Long insideContainerId:containerids) {
//            Container container = containerDao.findByIdExt(insideContainerId, ouId);
//            icCodeList.add(container.getCode());
//            container.setStatus(ContainerStatus.CONTAINER_STATUS_PUTAWAY);   //上架中
//            container.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED);  //占用中
//            containerDao.saveOrUpdateByVersion(container);
//        }
//        containerids.add(outerContainerId);     //添加外部容器id
//        //删除该外部容器及内部容器对应的辅助表信息
//        containerAssistDao.deleteByContainerIds(ouId, containerids);
//        Map<Long,Double> insideContainerWeight = isrCommand.getInsideContainerWeight();
//        Map<Long, ContainerAssist> insideContainerAsists = isrCommand.getInsideContainerAsists(); //内部容器辅助表统计信息
//        Double icTotalWeight = 0.0;
//        //如果没有外部容器只更新内部容器对应的辅助表信息
//        for(Long insideContaineId:insideContainerIds) {
//            icTotalWeight = icTotalWeight+insideContainerWeight.get(insideContaineId);   //计算所有内部容器的重量
//            ContainerAssist cAssist = insideContainerAsists.get(insideContaineId);
//            cAssist.setSysWeight(insideContainerWeight.get(insideContaineId));
//            cAssist.setCartonQty(isrCommand.getInsideContainerIds().size()+ 0L);   //托盘内部所有容器数
//            cAssist.setSkuAttrCategory(isrCommand.getSkuAttrIds().size()+ 0L);   //sku种类数
//            cAssist.setSkuQty(isrCommand.getSkuQty());   //sku总件数
//            cAssist.setStoreQty(isrCommand.getStoreIds().size()+ 0L);
//            cAssist.setSkuCategory(isrCommand.getSkuIds().size()+0L);
//            containerAssistDao.insert(cAssist);
//            insertGlobalLog(GLOBAL_LOG_INSERT, cAssist, ouId, userId, null, null);
//            caMap.put(insideContaineId, cAssist);// 所有内部容器辅助信息
//        }
//        //增加新的容器辅助表信息
//        if(null != outerContainerId) {   //外部容器
//            //更新外部容器对应的辅助表信息
//            Container2ndCategory c2c = container2ndCategoryDao.findByIdExt(outerContainerCate, ouId);
//            Double ocLength = c2c.getLength();
//            Double ocWidth = c2c.getWidth();
//            Double ocHeight = c2c.getHigh();
//            Double ocWeight = c2c.getWeight();
//            if (null == ocLength || null == ocWidth || null == ocHeight) {
//                log.error("sys guide pallet putaway inside container length、width、height is null error, ocId is:[{}], logId is:[{}]", containerId, logId);
//                throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {outContainerCode });
//            }
//            if (null == ocWeight) {
//                log.error("sys guide pallet putaway inside container weight is null error, ocId is:[{}], logId is:[{}]", containerId, logId);
//                throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {outContainerCode });
//            }
//            Double outerContainerWeight = isrCommand.getOuterContainerWeight();
//            Double outerContainerVolume = isrCommand.getOuterContainerVolume();
//            ContainerAssist cAssist = new ContainerAssist();
//            cAssist.setContainerId(outerContainerId);
//            cAssist.setSysLength(ocLength);
//            cAssist.setSysWidth(ocWidth);
//            cAssist.setSysHeight(ocHeight);
//            cAssist.setSysVolume(outerContainerVolume);
//            cAssist.setSysWeight(outerContainerWeight+icTotalWeight);
//            cAssist.setCartonQty(Long.valueOf(isrCommand.getInsideContainerIds().size()));   //托盘内部所有容器数
//            cAssist.setSkuAttrCategory(Long.valueOf(isrCommand.getSkuAttrIds().size()));   //sku种类数
//            cAssist.setSkuQty(isrCommand.getSkuQty());   //sku总件数
//            cAssist.setStoreQty(Long.valueOf(isrCommand.getStoreIds().size()));
//            cAssist.setSkuCategory(Long.valueOf(isrCommand.getSkuIds().size()));
//            cAssist.setOuId(ouId);
//            cAssist.setCreateTime(new Date());
//            cAssist.setLifecycle(Constants.LIFECYCLE_START);
//            cAssist.setLastModifyTime(new Date()); 
//            containerAssistDao.insert(cAssist);
//            caMap.put(outerContainerId, cAssist);// 外部容器辅助信息
//            insertGlobalLog(GLOBAL_LOG_INSERT, cAssist, ouId, userId, null, null);
//        }
//        // 6.匹配上架规则
//        List<Long> storeList = new ArrayList<Long>();
//        CollectionUtils.addAll(storeList, isrCommand.getStoreIds().iterator());
//        List<Long> icIdList = new ArrayList<Long>();
//        CollectionUtils.addAll(icIdList, isrCommand.getInsideContainerIds().iterator());
//        RuleAfferCommand ruleAffer = new RuleAfferCommand();
//        ruleAffer.setLogId(logId);
//        ruleAffer.setOuid(ouId);
//        ruleAffer.setAfferContainerCode(srCmd.getOuterContainerCode());    //原始容器号
//        ruleAffer.setAfferInsideContainerIdList(icIdList);   //内部容器id
//        ruleAffer.setContainerId(containerCmd.getId());
//        ruleAffer.setFuncId(funcId);
//        ruleAffer.setAfferContainerCodeList(icCodeList);
//        ruleAffer.setRuleType(Constants.SHELVE_RECOMMEND_RULE_ALL);// 整托 、货箱上架规则
//        ruleAffer.setStoreIdList(storeList);
//        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
//        // 判断该容器是否有符合的上架规则,有则走库位推荐排队系统,没有抛出异常
//        List<ShelveRecommendRuleCommand> ruleList = export.getShelveRecommendRuleList();
//        if (null == ruleList || 0 == ruleList.size()) {
//            srCmd.setRecommendFail(true);   //推荐库位失败
//            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCmd.getId().toString());
//            if(null == isCmd) {
//              //缓存容器库存统计信息
//                pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//            }
//            return srCmd;
//        }
//        //将lrrList存入缓存
//        List<LocationRecommendResultCommand> list = cacheManager.getMapObject(CacheConstants.LOCATION_RECOMMEND,containerId.toString());  //整托上架，外部容器id
//        if(null == list) {
//            cacheManager.setMapObject(CacheConstants.LOCATION_RECOMMEND, containerId.toString(), ruleList, CacheConstants.CACHE_ONE_DAY);
//        }
//        // 7.库位推荐排队系统     推荐库位
//        if (null == caMap || 0 == caMap.size()) {
//            log.error("container assist info generate error, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_ASSIST_INFO_GENERATE_ERROR);
//        }
//        // 判断排队队列是否已经排队
//        boolean isRecommend = pdaPutawayCacheManager.sysGuidePutawayLocRecommendQueue(containerCmd.getId(), logId);
//        if (false == isRecommend) {  //需要等待排队,否则得到库位推荐执行权
//            srCmd.setNeedQueueUp(true);   
//            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCmd.getId().toString());
//            if(null == isCmd) {
//                pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//            }
//            return srCmd;  //跳转到扫描容器页面
//        }else{
//            srCmd.setNeedQueueUp(false);
//        }
//        //推荐库位流程
//        Map<String, Map<String, Double>> uomMap = new HashMap<String, Map<String, Double>>();
////        uomMap.put(WhUomType.LENGTH_UOM, null);
////        uomMap.put(WhUomType.WEIGHT_UOM, null);
//        uomMap.put(WhUomType.LENGTH_UOM, isrCommand.getLenUomConversionRate());
//        uomMap.put(WhUomType.WEIGHT_UOM, isrCommand.getWeightUomConversionRate());
//        List<LocationRecommendResultCommand> lrrList = null;
//        try {
//            lrrList = whLocationRecommendManager.recommendLocationByShevleRule(ruleAffer, export, WhPutawayPatternDetailType.PALLET_PUTAWAY, caMap, invList, uomMap, logId);
//        } catch (Exception e1) {
//            // 弹出排队队列
//            pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
//            throw e1;
//        }
//        if (null == lrrList || 0 == lrrList.size() || StringUtils.isEmpty(lrrList.get(0).getLocationCode())) {
//              srCmd.setRecommendFail(true);   //推荐库位失败
//              InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCmd.getId().toString());
//              if(null == isCmd) {
//                //缓存容器库存统计信息
//                  pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//              }
//              return srCmd;
//        }
//        LocationRecommendResultCommand lrr = lrrList.get(0);
//        Long lrrLocId = lrr.getLocationId();  //推荐库位id
//        String lrrLocCode = lrr.getLocationCode();  //推荐库位号
//        //根据推荐库位号查询库位条码
//        Location loc = locationDao.findLocationByCode(lrrLocCode, ouId);
//        if (null == loc) {
//            log.error("location is null error, locationCode is:[{}], logId is:[{}]", lrrLocCode, logId);
//            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
//        }
//        Set<Long> suggestLocationIds = new HashSet<Long>();
//        suggestLocationIds.add(lrrLocId);
//        isrCommand.setLocationIds(suggestLocationIds);
//        //库位绑定
//        //先待移入库位库存，将on_hand_qty(在库库存)值设置到to_be_filled_qty(待移入库存)、将on_hand_qty设置为0.0、为每行库存记录增加库位插入一条新的库存记录（注意更新uuid），即插入待移入库位库存。如果有SN/残次信息则同样插入SN/残次信息记录（一待入）
//        whSkuInventoryManager.execBinding(invList, warehouse, lrrList, putawayPatternDetailType, ouId, userId, lrrLocCode);
//     // 10.缓存容器库存统计信息
//        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCmd.getId().toString());
//        if(null == isCmd) {
//            //缓存容器库存统计信息
//            pdaPutawayCacheManager.sysGuidePalletPutawayCacheInventoryStatistic(containerCmd, isrCommand, ouId, logId);
//        }
//        // 弹出排队队列
//        pdaPutawayCacheManager.sysGuidePutawayLocRecommendPopQueue(containerId, logId);
//        // 10.提示库位
//        srCmd.setRecommendFail(false);   //推荐库位成功
//        srCmd.setRecommendLocation(true);// 已推荐库位
//        srCmd.setTipLocationCode(lrrLocCode);// 提示库位编码
//        srCmd.setNeedTipLocation(true);// 提示库位
//        srCmd.setTipLocBarCode(loc.getBarCode());  //提示库位
//        log.info("PdaOutBoundBoxMoveManagerImpl updateContainer is end"); 
//        
//        return srCmd;
//    }
//    
//    /***
//     * 拆箱上架使用推荐库位上架
//     * @param locationCode
//     * @param containerCode
//     * @param userId
//     * @param ouId
//     * @param srCmd
//     * @return
//     */
////    @Override
////    public ScanResultCommand splitUserSuggestLocation(int putawayPatternType,String outContainerCode,String locationCode, String insideContainerCode, Long userId, Long ouId,String locBarCode) {
////        // TODO Auto-generated method stub
////        log.info("PdaOutBoundBoxMoveManagerImpl splitUserSuggestLocation is start"); 
////        ScanResultCommand srCmd = new ScanResultCommand();
////        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
////        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
////        if (null == insideContainerCmd) {
////            // 容器信息不存在
////            log.error("container is not exists, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
////        }
////        Long containerId = insideContainerCmd.getId();   //内部容器id
////        // 0.判断是否已经缓存所有库存信息,获取库存统计信息及当前功能参数scan_pattern         CacheConstants.CONTAINER_INVENTORY
////        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerId.toString());
////        if (null == isCmd) {
////            isCmd = pdaPutawayCacheManager.sysSuggestSplitPutawayCacheInventoryStatistic(putawayPatternType,userId, insideContainerCmd, ouId, logId, outContainerCode, WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
////        }
////        // 1.提示商品并判断是否需要扫描属性
////        Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = isCmd.getInsideContainerSkuAttrIdsQty();   //内部容器唯一sku总件数
////        Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = isCmd.getInsideContainerLocSkuAttrIds();  //内部容器推荐库位对应唯一sku及残次条码
////        Location loc = null;
////        if(StringUtils.isEmpty(locationCode) && !StringUtils.isEmpty(locBarCode)) {  //库位号为空,库位条码不为空
////            loc = locationDao.getLocationByBarcode(locBarCode, ouId);
////            if (null == loc) {
////                log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////            }
////            locationCode = loc.getBarCode();
////        }
////        srCmd.setLocationCode(locationCode);
////        if(!StringUtils.isEmpty(locationCode) && StringUtils.isEmpty(locBarCode)){
////            loc = locationDao.findLocationByCode(locationCode, ouId);
////        }
////        if (null == loc) {
////            log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////            throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////        }
////        Long locationId = loc.getId();
////        Set<Long> locationIds = isCmd.getLocationIds();
////        pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipLocation(insideContainerCmd,locationIds,locationId,logId);
////        Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(containerId);  //库位属性
////        Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(containerId);   //内部容器唯一sku总件数
////        String tipSkuAttrId = this.sysSuggestSplitContainerPutawayTipSku(insideContainerCmd, loc.getId(), locSkuAttrIds, logId);
////        Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
////        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
////        if (null == skuCmd) {
////            log.error("sku is not found error, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
////        }
////        srCmd.setNeedTipSku(true);
////        srCmd.setTipSkuBarcode(skuCmd.getBarCode());   //提示sku
////        log.info("PdaOutBoundBoxMoveManagerImpl splitUserSuggestLocation is end"); 
////        return srCmd;
////    }
////    
//    
//    /**
//     * @param insideContainerCmd
//     * @param locationId
//     * @param locSkuAttrIds
//     * @param logId
//     * @return
//     */
////    private String sysSuggestSplitContainerPutawayTipSku(ContainerCommand insideContainerCmd, Long locationId, Map<Long, Set<String>> locSkuAttrIds, String logId) {
////        String tipSku = "";
////        Long icId = insideContainerCmd.getId();
////        Set<String> skuAttrIds = locSkuAttrIds.get(locationId);
////        TipScanSkuCacheCommand cacheSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString());
////        ArrayDeque<String> cacheSkuAttrIds = null;
////        if (null != cacheSkuCmd) { 
////            cacheSkuAttrIds = cacheSkuCmd.getScanSkuAttrIds();
////        }
////        if (null != cacheSkuAttrIds && !cacheSkuAttrIds.isEmpty()) {
////            String value = cacheSkuAttrIds.getFirst();
////            tipSku = value;
////        } else {
////            // 随机提示一个
////            for (String sId : skuAttrIds) {
////                if (!StringUtils.isEmpty(sId)) {
////                    tipSku = sId;
////                    TipScanSkuCacheCommand tipCmd = new TipScanSkuCacheCommand();
////                    tipCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
////                    tipCmd.setInsideContainerId(insideContainerCmd.getId());
////                    tipCmd.setInsideContainerCode(insideContainerCmd.getCode());
////                    tipCmd.setLocationId(locationId);
////                    ArrayDeque<String> tipSkuAttrIds = new ArrayDeque<String>();
////                    tipSkuAttrIds.addFirst(tipSku);
////                    tipCmd.setScanSkuAttrIds(tipSkuAttrIds);
////                    cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + locationId.toString(), tipCmd, CacheConstants.CACHE_ONE_DAY);
////                    break;
////                }
////            }
////        }
////        return tipSku;
////    }
    /**
     * 商品明细数据赋值
     * @param srCmd
     * @param tipSkuAttrId
     * @param skuAttrIdsQty
     * @param logId
     */
    private void tipSkuDetailAspect(ScanResultCommand srCmd, String tipSkuAttrId, Map<String, Long> skuAttrIdsQty, String logId) {
        boolean isTipSkuDetail = true;
        srCmd.setNeedTipSkuDetail(isTipSkuDetail);
        String skuAttrId = SkuCategoryProvider.getSkuAttrId(tipSkuAttrId);
        Long qty = skuAttrIdsQty.get(skuAttrId);
        if (null == qty) {
            log.error("sku qty is null error, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        srCmd.setTipSkuQty(qty.intValue());
        if (true == isTipSkuDetail) {
            srCmd.setNeedTipSkuInvType(TipSkuDetailProvider.isTipSkuInvType(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvType()) {
                String skuInvType = TipSkuDetailProvider.getSkuInvType(tipSkuAttrId);
                List<SysDictionary> invTypeList = sysDictionaryManager.getListByGroup(Constants.INVENTORY_TYPE, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : invTypeList) {
                    if (sd.getDicValue().equals(skuInvType)) {
                        srCmd.setTipSkuInvType(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv type is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_TYPE_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvType("");
            }
            srCmd.setNeedTipSkuInvStatus(TipSkuDetailProvider.isTipSkuInvStatus(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvStatus()) {
                String skuInvStatus = TipSkuDetailProvider.getSkuInvStatus(tipSkuAttrId);
                List<InventoryStatus> invStatusList = inventoryStatusManager.findAllInventoryStatus();
                boolean isExists = false;
                for (InventoryStatus is : invStatusList) {
                    if (is.getId().toString().equals(skuInvStatus)) {
                        srCmd.setTipSkuInvStatus(is.getName());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv status is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_STATUS_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvStatus("");
            }
            srCmd.setNeedTipSkuBatchNumber(TipSkuDetailProvider.isTipSkuBatchNumber(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuBatchNumber()) {
                String skuBatchNumber = TipSkuDetailProvider.getSkuBatchNumber(tipSkuAttrId);
                srCmd.setTipSkuBatchNumber(skuBatchNumber);
            } else {
                srCmd.setTipSkuBatchNumber("");
            }
            srCmd.setNeedTipSkuCountryOfOrigin(TipSkuDetailProvider.isTipSkuCountryOfOrigin(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuCountryOfOrigin()) {
                String skuCountryOfOrigin = TipSkuDetailProvider.getSkuCountryOfOrigin(tipSkuAttrId);
                srCmd.setTipSkuCountryOfOrigin(skuCountryOfOrigin);
            } else {
                srCmd.setTipSkuCountryOfOrigin("");
            }
            srCmd.setNeedTipSkuMfgDate(TipSkuDetailProvider.isTipSkuMfgDate(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuMfgDate()) {
                String skuMfgDate = TipSkuDetailProvider.getSkuMfgDate(tipSkuAttrId);
                srCmd.setTipSkuMfgDate(skuMfgDate);
            } else {
                srCmd.setTipSkuMfgDate("");
            }
            srCmd.setNeedTipSkuExpDate(TipSkuDetailProvider.isTipSkuExpDate(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuExpDate()) {
                String skuExpDate = TipSkuDetailProvider.getSkuExpDate(tipSkuAttrId);
                srCmd.setTipSkuExpDate(skuExpDate);
            } else {
                srCmd.setTipSkuExpDate("");
            }
            srCmd.setNeedTipSkuInvAttr1(TipSkuDetailProvider.isTipSkuInvAttr1(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr1()) {
                String skuInvAttr1 = TipSkuDetailProvider.getSkuInvAttr1(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_1, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr1)) {
                        srCmd.setTipSkuInvAttr1(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr1 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr1("");
            }
            srCmd.setNeedTipSkuInvAttr2(TipSkuDetailProvider.isTipSkuInvAttr2(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr2()) {
                String skuInvAttr2 = TipSkuDetailProvider.getSkuInvAttr2(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_2, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr2)) {
                        srCmd.setTipSkuInvAttr2(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr2 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr2("");
            }
            srCmd.setNeedTipSkuInvAttr3(TipSkuDetailProvider.isTipSkuInvAttr3(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr3()) {
                String skuInvAttr3 = TipSkuDetailProvider.getSkuInvAttr3(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_3, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr3)) {
                        srCmd.setTipSkuInvAttr3(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr3 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr3("");
            }
            srCmd.setNeedTipSkuInvAttr4(TipSkuDetailProvider.isTipSkuInvAttr4(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr4()) {
                String skuInvAttr4 = TipSkuDetailProvider.getSkuInvAttr4(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_4, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr4)) {
                        srCmd.setTipSkuInvAttr4(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr4 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr4("");
            }
            srCmd.setNeedTipSkuInvAttr5(TipSkuDetailProvider.isTipSkuInvAttr5(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuInvAttr5()) {
                String skuInvAttr5 = TipSkuDetailProvider.getSkuInvAttr5(tipSkuAttrId);
                List<SysDictionary> list = sysDictionaryManager.getListByGroup(Constants.INVENTORY_ATTR_5, BaseModel.LIFECYCLE_NORMAL);
                boolean isExists = false;
                for (SysDictionary sd : list) {
                    if (sd.getDicValue().equals(skuInvAttr5)) {
                        srCmd.setTipSkuInvAttr5(sd.getDicLabel());
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    log.error("inv attr5 is not found error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_INV_ATTR_NOT_FOUND_ERROR);
                }
            } else {
                srCmd.setTipSkuInvAttr5("");
            }
            srCmd.setNeedTipSkuSn(TipSkuDetailProvider.isTipSkuSn(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuSn()) {
                String skuSn = TipSkuDetailProvider.getSkuSn(tipSkuAttrId);
                srCmd.setTipSkuSn(skuSn);
            } else {
                srCmd.setTipSkuSn("");
            }
            srCmd.setNeedTipSkuDefect(TipSkuDetailProvider.isTipSkuDefect(tipSkuAttrId));
            if (true == srCmd.isNeedTipSkuDefect()) {
                String skuDefect = TipSkuDetailProvider.getSkuDefect(tipSkuAttrId);
                srCmd.setTipSkuDefect(skuDefect);
            } else {
                srCmd.setTipSkuDefect("");
            }
        }
    }
//    
//    /***
//     * 整箱上架使用推荐库位上架
//     * @param locationCode
//     * @param isCaselevelScanSku
//     * @param isNotcaselevelScanSku
//     * @param containerCode
//     * @param userId
//     * @param ouId
//     * @param srCmd
//     * @return
//     */
////    @Override
////    public ScanResultCommand contianerUserSuggestLocation(int putawayPatternType,String locationCode, Long functionId, String outerContainerCode, String insideContainerCode,Long userId, Long ouId,Integer putawayPatternDetailType,Warehouse warehouse,String locBarCode) {
////        // TODO Auto-generated method stub
////        log.info("PdaOutBoundBoxMoveManagerImpl contianerUserSuggestLocation is end");
////        ScanResultCommand srCmd = new ScanResultCommand();  //默认不需要扫描容器号
////        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
////        if (null == insideContainerCmd) {
////            // 容器信息不存在
////            log.error("container is not exists, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
////        }
////
////        Long insideContainerId = insideContainerCmd.getId();
////        ContainerCommand outContainerCmd  = null;
////        if(!StringUtils.isEmpty(outerContainerCode)) {
////            outContainerCmd  = containerDao.getContainerByCode(outerContainerCode, ouId);
////            if (null == outContainerCmd) {
////                // 容器信息不存在
////                log.error("container is not exists, logId is:[{}]", logId);
////                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
////            }
////        }
////        // 获取容器状态
////        Integer containerStatus = insideContainerCmd.getStatus();
////        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
////            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
////            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {outerContainerCode});
////        }
////        Location loc = null;
////        if(StringUtils.isEmpty(locationCode) && !StringUtils.isEmpty(locBarCode)) {  //库位号为空,库位条码不为空
////            loc = locationDao.getLocationByBarcode(locBarCode, ouId);
////            if (null == loc) {
////                log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////            }
////            locationCode = loc.getBarCode();
////        }
////        srCmd.setLocationCode(locationCode);
////        // 1.获取功能配置
////        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
////        if (null == putawyaFunc) {
////            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
////        }
////        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
////        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
////        InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerCmd.getId().toString());
////        if (null == isrCmd) {
////            isrCmd = pdaPutawayCacheManager.sysSuggestContainerPutawayCacheInventoryStatistic(putawayPatternType,userId, insideContainerCmd, ouId, logId, outerContainerCode, putawayPatternDetailType);
////        }
////        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
////        if (null != outContainerCmd) {
////            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, outContainerCmd.getId().toString());
////            if (null == csrCmd) {
////                csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(outContainerCmd, ouId, logId);
////            }
////        }
////        Set<Long> insideContainerIds = isrCmd.getInsideContainerIds();   //所有容器id集合
////        if (null != outContainerCmd) {
////            insideContainerIds = csrCmd.getInsideContainerIds();
////        }
////        //获取库存缓存信息
////        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
////        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
////                srCmd.setNeedScanSku(true);// 直接扫描商品
////                // 提示下一个容器
////        }else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) { //caselevel
////               //判断该货箱是否是caselevel货箱
////               int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,true);
////               if(count < 1) {  //此货箱不是caselevel货箱,直接上架
////                   srCmd.setNeedScanSku(false);// 不扫描商品，直接上架
////                   whSkuInventoryManager.execPutaway(outContainerCmd, insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                   CheckScanSkuResultCommand  cSRCmd =  this.sysSuggestCacheContainer(outContainerCmd, insideContainerCmd, insideContainerIds, logId);
////                   if(cSRCmd.isNeedTipContainer()){ //还有内部容器没有扫描完毕
////                       srCmd.setNeedTipContainer(true);  //跳转到扫描容器页面
////                       // 提示下一个容器
////                       String tipContainerCode = sysSuggestTipContainer((null == outContainerCmd ? null : outContainerCmd.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                       srCmd.setTipContainerCode(tipContainerCode);
////                   }else{
////                       srCmd.setPutaway(true);
////                       pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outContainerCmd, insideContainerCmd,false,logId);    //已经扫描完毕或者上架容器只有货箱没有托盘
////                   }
////               }else{
////                   // 是caselevel货箱扫描商品
////                   srCmd.setNeedScanSku(true);// 直接扫描商品
////               }
////        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {  //判断扫描的货箱是否是非caselevel货箱
////               //判断该货箱是否是caselevel货箱
////               int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,false);
////               if(count < 1) {  //此货箱不是caselevel货箱,直接上架
////                   CheckScanSkuResultCommand  cSRCmd =  this.sysSuggestCacheContainer(outContainerCmd, insideContainerCmd, insideContainerIds, logId);
////                   if(cSRCmd.isNeedTipContainer()){ //还有内部容器没有扫描完毕
////                       srCmd.setNeedTipContainer(true);  //跳转到扫描容器页面
////                       whSkuInventoryManager.execPutaway(outContainerCmd, insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                       // 提示下一个容器
////                       String tipContainerCode = sysSuggestTipContainer((null == outContainerCmd ? null : outContainerCmd.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                       srCmd.setTipContainerCode(tipContainerCode);
////                   }else{
////                       srCmd.setPutaway(true);
////                       whSkuInventoryManager.execPutaway(outContainerCmd, insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                       pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outContainerCmd, insideContainerCmd,false,logId);    //已经扫描完毕或者上架容器只有货箱没有托盘
////                   }
////               }else{
////                   srCmd.setNeedScanSku(true);// 直接扫描商品
////               }
////         }else if(false == isCaselevelScanSku && false == isNotcaselevelScanSku) {  //整箱上架,直接执行上架流程
////             CheckScanSkuResultCommand  cSRCmd =  this.sysSuggestCacheContainer(outContainerCmd, insideContainerCmd, insideContainerIds, logId);
////             if(cSRCmd.isNeedTipContainer()){ //还有内部容器没有扫描完毕
////                 srCmd.setNeedTipContainer(true);  //跳转到扫描容器页面
////                 whSkuInventoryManager.execPutaway(outContainerCmd, insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                 // 提示下一个容器
////                 String tipContainerCode = sysSuggestTipContainer((null == outContainerCmd ? null : outContainerCmd.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                 srCmd.setTipContainerCode(tipContainerCode);
////             }else{
////                 srCmd.setPutaway(true);
////                 whSkuInventoryManager.execPutaway(outContainerCmd, insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                 pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outContainerCmd, insideContainerCmd,false,logId);    //已经扫描完毕或者上架容器只有货箱没有托盘
////             }
////        }
////        log.info("PdaOutBoundBoxMoveManagerImpl contianerUserSuggestLocation is end");
////        return srCmd;
////    }
////    
////    private CheckScanSkuResultCommand  sysSuggestCacheContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds,String logId){
////        log.info("PdaOutBoundBoxMoveManagerImpl sysSuggestCacheContainer is start");
////        CheckScanSkuResultCommand csRCmd = new CheckScanSkuResultCommand();
////        if(null == ocCmd){
////            csRCmd.setNeedTipContainer(false);
////            return csRCmd;
////        }
////        Long ocId = ocCmd.getId();
////        TipContainerCacheCommand tipContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
////        if (null == tipContainerCmd) {
////            log.error("scan container queue is exception, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
////        }
////        ArrayDeque<Long> scanIcIds = tipContainerCmd.getTipInsideContainerIds();// 取到已扫描容器队列
////        if(this.isCacheAllExists(insideContainerIds, scanIcIds)){
////            csRCmd.setPutaway(true);  //全部扫描完毕
////        }else{
////            csRCmd.setNeedTipContainer(true);  //还有内部容器需要扫描
////            //提示下一个容器
////        }
////        log.info("PdaOutBoundBoxMoveManagerImpl sysSuggestCacheContainer is end");
////        return csRCmd;
////    }
////    
////
////    private boolean isCacheAllExists(Set<Long> ids, ArrayDeque<Long> cacheKeys) {
////        boolean allExists = true;
////        if (null != cacheKeys && !cacheKeys.isEmpty()) {
////            for (Long id : ids) {
////                Long cId = id;
////                boolean isExists = false;
////                Iterator<Long> iter = cacheKeys.iterator();
////                while (iter.hasNext()) {
////                    Long value = iter.next();
////                    if (null == value) value = -1L;
////                    if (0 == value.compareTo(cId)) {
////                        isExists = true;
////                        break;
////                    }
////                }
////                if (false == isExists) {
////                    allExists = false;
////                }
////            }
////        } else {
////            allExists = false;
////        }
////        return allExists;
////    }
//    
//    /**提示下一个容器号
//     * @param containerCode
//     * @param funcId
//     * @param putawayPatternDetailType
//     * @param ouId
//     * @param userId
//     * @param logId
//     * @return
//     */
//   private String sysSuggestTipContainer(String containerCode, Long funcId, Integer putawayPatternDetailType, Long ouId, Long userId, String logId)  {
//        String tipContainerCode = "";
//        if (log.isInfoEnabled()) {
//            log.info("sys guide putaway tip container start, containerCode is:[{}], putawayPatternDetailType is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}]", containerCode, putawayPatternDetailType, ouId, userId, logId);
//        }
//        // 0.判断容器状态
//        if (StringUtils.isEmpty(containerCode)) {
//            log.error("containerCode is null error, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
//        }
//        ContainerCommand containerCmd = containerDao.getContainerByCode(containerCode, ouId);
//        if (null == containerCmd) {
//            // 容器信息不存在
//            log.error("container is not exists, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
//        }
//        // 获取容器状态
//        Integer containerStatus = containerCmd.getStatus();
//        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
//            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
//            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCode});
//        }
//        // 1.获取容器统计信息
//        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
//        if (null != containerCmd) {
//            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, containerCmd.getId().toString());
//            if (null == csrCmd) {
//                if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
//                    csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
//                } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
//                    csrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInsideContainerStatistic(containerCmd, ouId, logId);
//                }
//            }
//        }
//        if (null == csrCmd) {
//            log.error("container statistic cache is error, logId is:[{}]", containerStatus, logId);
//            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
//        }
//        Set<Long> insideContainerIds = csrCmd.getInsideContainerIds();
//        Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
//        Long tipContainerId = null;
//        if (WhPutawayPatternDetailType.CONTAINER_PUTAWAY == putawayPatternDetailType) {
//            tipContainerId = pdaPutawayCacheManager.sysGuideContainerPutawayTipContainer(containerCmd, insideContainerIds, logId);
//        } else if (WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY == putawayPatternDetailType) {
//            tipContainerId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipContainer(containerCmd, insideContainerIds, logId);
//        }
//        tipContainerCode = insideContainerIdsCode.get(tipContainerId);
//        if (StringUtils.isEmpty(tipContainerCode)) {
//            log.error("sys guide putaway tip container is error, logId is:[{}]", logId);
//            throw new BusinessException(ErrorCodes.TIP_NEXT_CONTAINER_IS_ERROR, new Object[] {containerCode});
//        }
//        if (log.isInfoEnabled()) {
//            log.info("sys guide putaway tip container end, containerCode is:[{}], putawayPatternDetailType is:[{}], ouId is:[{}], userId is:[{}], logId is:[{}], tipContainer is:[{}]", containerCode, putawayPatternDetailType, ouId, userId, logId,
//                    tipContainerCode);
//        }
//        return tipContainerCode;
//    }
//    
//    /***
//     * 整托上架使用推荐库位上架(缓存内部容器)
//     * @param locationCode
//     * @param isCaselevelScanSku
//     * @param isNotcaselevelScanSku
//     * @param containerCode
//     * @param userId
//     * @param ouId
//     * @param srCmd
//     * @return
//     */
////    @Override
////    public ScanResultCommand  palletIsUserSuggestLocation(String locationCode,String outerContainerCode,Long userId,Long ouId,Integer putawayPatternDetailType,Long functionId,Warehouse warehouse,String locBarCode) {
////        // TODO Auto-generated method stub
////        log.info("PdaOutBoundBoxMoveManagerImpl wholeIsUserSuggestLocation is start"); 
////        ScanResultCommand srCmd = new ScanResultCommand();  //默认不需要扫描容器号
////        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
////        if (StringUtils.isEmpty(outerContainerCode)) {
////            log.error("containerCode is null error, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
////        }
////        ContainerCommand containerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
////        if (null == containerCmd) {
////            // 容器信息不存在
////            log.error("container is not exists, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
////        }
////        Integer containerStatus = containerCmd.getStatus();
////        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
////            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
////            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {containerCmd.getCode()});
////        }
////        Long outContainerId = containerCmd.getId();
////        Location loc = null;
////        if(StringUtils.isEmpty(locationCode) && !StringUtils.isEmpty(locBarCode)) {  //库位号为空,库位条码不为空
////            loc = locationDao.getLocationByBarcode(locBarCode, ouId);
////            if (null == loc) {
////                log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////            }
////            locationCode = loc.getBarCode();
////        }
////        srCmd.setLocationCode(locationCode);
////        // 1.获取功能配置
////        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
////        if (null == putawyaFunc) {
////            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
////        }
////        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
////        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
////        //获取库存缓存信息
////        InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, outContainerId.toString());
////        ContainerStatisticResultCommand csrCmd =  cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, outContainerId.toString());
////        Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
////        Set<Long>  insideContainerIds = isrCmd.getInsideContainerIds();  //所有内部容器id集合
////        Set<Long> caselevelContainerIds = isrCmd.getCaselevelContainerIds(); //caselevel货箱id集合
////        Set<Long> noCaselevelContainerIds = isrCmd.getNotcaselevelContainerIds();  //非caselevel货箱Id集合
////        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
////                 // 提示一个内部容器id
////                Long tipInsideContainerId =  pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(containerCmd, insideContainerIds,logId);
////                srCmd.setNeedTipContainer(true);  //需要提示容器
////                // 提示下一个容器
////                srCmd.setTipContainerCode(insideContainerIdsCode.get(tipInsideContainerId));
////        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
////                
////                if(caselevelContainerIds.size() > 0) {
////                    // 只扫caselevel货箱中的商品
////                    Long tipInsideContainerId =  pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(containerCmd, caselevelContainerIds,logId);
////                    srCmd.setNeedTipContainer(true);  //需要提示容器
////                    // 提示下一个容器
////                    srCmd.setTipContainerCode(insideContainerIdsCode.get(tipInsideContainerId));
////                }else{
////                    whSkuInventoryManager.execPutaway(containerCmd, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    srCmd.setPutaway(true);  //上架成功，返回到首页
////                    pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(containerCmd, logId);
////                }
////                
////        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
////              if(noCaselevelContainerIds.size() > 0) {
////               // 只扫非caselvel货箱
////                  Long tipInsideContainerId =  pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(containerCmd, noCaselevelContainerIds,logId);
////                  srCmd.setNeedTipContainer(true);  //需要提示容器
////                  // 提示下一个容器
////                  srCmd.setTipContainerCode(insideContainerIdsCode.get(tipInsideContainerId));
////                  srCmd.setNotCaselevelScanContainer(true);
////              }else{
////                  whSkuInventoryManager.execPutaway(containerCmd, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                  srCmd.setPutaway(true);  //上架成功，返回到首页
////                  pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(containerCmd, logId);
////              }
////            } else if(false == isCaselevelScanSku && false == isNotcaselevelScanSku) {
////                    whSkuInventoryManager.execPutaway(containerCmd, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    srCmd.setPutaway(true);  //上架成功，返回到首页
////                    pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(containerCmd, logId);
////            }else{
////                log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
////                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
////            }
////        return srCmd;
////        
////    }
////    
////
////    /**
////     * 整托上架:扫描sku商品
////     * @param barCode
////     * @param insideContainerCode
////     * @param sRCommand
////     * @return
////     */
////    public ScanResultCommand palletPutwayScanSku(int putawayPatternType,WhSkuCommand skuCmd,String containerCode,Long ouId,Long userId,String locationCode,String insideContainerCode,Long functionId,Warehouse warehouse,Integer putawayPatternDetailType) {
////            log.info("PdaOutBoundBoxMoveManagerImpl sysSuggestScanSku is start"); 
////            ScanResultCommand srCmd = new ScanResultCommand();
////            srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);  //整托上架
////            ContainerCommand insideCommand = containerDao.getContainerByCode(insideContainerCode, ouId);  //根据外部容器编码查询外部容器
////            if(null == insideCommand) {
////                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //外部容器不存在
////            }
////            ContainerCommand outCommand = containerDao.getContainerByCode(containerCode, ouId);  //根据外部容器编码查询外部容器
////            if(null == outCommand) {
////                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //外部容器不存在
////            }
////            String barCode = skuCmd.getBarCode();
////            Double scanQty = skuCmd.getScanSkuQty();
////            if (null == scanQty || scanQty.longValue() < 1) {
////                log.error("scan sku qty is valid, logId is:[{}]", logId);
////                throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
////            }
////            if (StringUtils.isEmpty(barCode)) {
////                log.error("sku is null error, logId is:[{}]", logId);
////                throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
////            }
////            InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, outCommand.getId().toString());
////            if (null == isCmd) {
////                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
////            }
////            // 1.获取功能配置
////            WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
////            if (null == putawyaFunc) {
////                log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
////                throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
////            }
////            Long skuId = null;
////            // 2.复核扫描的商品并判断是否切换内部容器
////            Set<Long> insideContainerIds = isCmd.getInsideContainerIds();   //所有内部容器id集合
////            Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();    //所有内部容器对应的sku种类
////            Set<Long> caselevelContainerIds = isCmd.getCaselevelContainerIds();    //所有caselevel容器集合
////            Set<Long> notcaselevelContainerIds = isCmd.getNotcaselevelContainerIds();   //所有非caselevel容器集合
////            Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = isCmd.getInsideContainerSkuIdsQty();   //内部容器单个sku总件数
////            Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
////            // 商品校验
////            String skuBarcode = skuCmd.getBarCode();
////            Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId);   //获取对应的商品数量,key值是sku id
////            Set<Long> icSkuIds = insideContainerSkuIds.get(insideCommand.getId());   //当前容器内所有sku id集合
////            Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(insideCommand.getId());
////            boolean isSkuExists = false;
////            Integer cacheSkuQty = 1;
////            Integer icSkuQty = 1;
////            for(Long cacheId : cacheSkuIdsQty.keySet()){
////                if(icSkuIds.contains(cacheId)){
////                    isSkuExists = true;
////                }
////                if(true == isSkuExists){
////                    skuId = cacheId;
////                    cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
////                    icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
////                    break;
////                }
////            }
////            if(false == isSkuExists){
////                log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideCommand.getId(), insideCommand.getId(), skuId, logId);
////                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideCommand.getCode()});
////            }
////            if(cacheSkuQty > 1 && cacheSkuQty <= icSkuQty){
////                if(0 != (icSkuQty%cacheSkuQty)){
////                    // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
////                    log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
////                    throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
////                }
////            }
////            if(WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern){
////                if(0 != new Double("1").compareTo(scanQty)){
////                    log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarcode, logId);
////                    throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
////                }
////            }
////            skuCmd.setId(skuId);
////            skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
////            Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
////            Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
////            if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
////                // 全部货箱扫描                                                                       
////                CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
////                if (cssrCmd.isNeedScanSku()) {
////                    srCmd.setNeedScanSku(true);// 直接复核商品
////                } else if (cssrCmd.isNeedTipContainer()) {
////                    srCmd.setNeedTipContainer(true);
////                    Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
////                    if (null == tipContainer) {
////                        log.error("container is null error, logId is:[{}]", logId);
////                        throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
////                    }
////                    srCmd.setTipContainerCode(tipContainer.getCode());
////                } else {
////                    srCmd.setPutaway(true);
////                    whSkuInventoryManager.execPutaway(outCommand, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(outCommand, logId);
////                }
////            } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
////                // 只扫caselevel货箱
////                if (null == caselevelContainerIds || 0 == caselevelContainerIds.size()) {
////                    // 无caselevel货箱，直接上架
////                    srCmd.setPutaway(true);
////                    whSkuInventoryManager.execPutaway(outCommand, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(outCommand, logId);
////                } else {
////                    CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, caselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
////                    if (cssrCmd.isNeedScanSku()) {
////                        srCmd.setNeedScanSku(true);// 直接复核商品
////                    } else if (cssrCmd.isNeedTipContainer()) {
////                        srCmd.setNeedTipContainer(true);
////                        Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
////                        if (null == tipContainer) {
////                            log.error("container is null error, logId is:[{}]", logId);
////                            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
////                        }
////                        srCmd.setTipContainerCode(tipContainer.getCode());
////                    } else {
////                        srCmd.setPutaway(true);
////                        whSkuInventoryManager.execPutaway(outCommand, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                        pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(outCommand, logId);
////                    }
////                }
////
////            } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
////                // 只扫非caselvel货箱
////                if (null == notcaselevelContainerIds || 0 == notcaselevelContainerIds.size()) {
////                    // 无caselevel货箱，直接上架
////                    srCmd.setPutaway(true);
////                    whSkuInventoryManager.execPutaway(outCommand, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(outCommand, logId);
////                } else {
////                    CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuidePalletPutawayCacheSkuOrTipContainer(outCommand, insideCommand, notcaselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern, logId);
////                    if (cssrCmd.isNeedScanSku()) {
////                        srCmd.setNeedScanSku(true);// 直接复核商品
////                    } else if (cssrCmd.isNeedTipContainer()) {
////                        srCmd.setNeedTipContainer(true);
////                        Container tipContainer = containerDao.findByIdExt(cssrCmd.getTipContainerId(), ouId);
////                        if (null == tipContainer) {
////                            log.error("container is null error, logId is:[{}]", logId);
////                            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
////                        }
////                        srCmd.setTipContainerCode(tipContainer.getCode());
////                    } else {
////                        srCmd.setPutaway(true);
////                        whSkuInventoryManager.execPutaway(outCommand, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                        pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(outCommand, logId);
////                    }
////                }
////            } else {
////                log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
////                throw new BusinessException(ErrorCodes.COMMON_FUNCTION_IS_CHANGE_ERROR);
////            }
////            log.info("PdaOutBoundBoxMoveManagerImpl sysSuggestScanSku is end"); 
////            return srCmd;
////    }
////
////    /**
////     * 整箱上架:扫描sku商品
////     * @param barCode
////     * @param insideContainerCode
////     * @param sRCommand
////     * @return
////     */
////    public ScanResultCommand containerPutwayScanSku(int putawayPatternType,Boolean isNotUser,Boolean isRecommendFail,WhSkuCommand skuCmd,String containerCode,Long ouId,Long userId,String locationCode,String insideContainerCode,Long functionId,Warehouse warehouse,Integer putawayPatternDetailType){
////        log.info("PdaOutBoundBoxMoveManagerImpl containerPutwayScanSku is start"); 
////        ScanResultCommand  srCmd = new ScanResultCommand();
////        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY); //整箱上架
////        ContainerCommand insideContainerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
////        if(null == insideContainerCmd) {
////            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);    //容器不存在
////        }
////        //获取内部容器id
////        Long insideContainerId = insideContainerCmd.getId();
////        ContainerCommand outCommand = null;
////        if(!StringUtils.isEmpty(containerCode)) {
////            outCommand = containerDao.getContainerByCode(containerCode, ouId);  //根据容器编码查询外部容器
////        }
////        //整箱上架：判断内部容器是否可以上架
////        Integer containerStatus = insideContainerCmd.getStatus();
////        if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != containerStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != containerStatus) {
////            log.error("container status is invalid, containerStatus is:[{}], logId is:[{}]", containerStatus, logId);
////            throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {insideContainerCode});
////        }
////        String barCode = skuCmd.getBarCode();
////        Double scanQty = skuCmd.getScanSkuQty();
////        if (null == scanQty || scanQty.longValue() < 1) {
////            log.error("scan sku qty is valid, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_VALID);
////        }
////        if (StringUtils.isEmpty(barCode)) {
////            log.error("sku is null error, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
////        }
////        InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerCmd.getId().toString());
////        if (null == isrCmd) {
////            isrCmd = pdaPutawayCacheManager.sysSuggestContainerPutawayCacheInventoryStatistic(putawayPatternType,userId, insideContainerCmd, ouId, logId,containerCode, putawayPatternDetailType);
////        }
////        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
////        if (null != outCommand) {
////            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, outCommand.getId().toString());
////            if (null == csrCmd) {
////                csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(outCommand, ouId, logId);
////            }
////        }
////        Set<Long> insideContainerIds = isrCmd.getInsideContainerIds();   //所有容器id集合
////        Set<Long> caselevelContainerIds = isrCmd.getCaselevelContainerIds();
////        Set<Long> noCaselevelContainerIds = isrCmd.getNotcaselevelContainerIds();
////        Map<Long, Set<Long>> insideContainerSkuIds = isrCmd.getInsideContainerSkuIds();
////        Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = isrCmd.getInsideContainerSkuIdsQty();
////        if (null != outCommand) {
////            insideContainerIds = csrCmd.getInsideContainerIds();
////            caselevelContainerIds = csrCmd.getCaselevelContainerIds();
////            noCaselevelContainerIds = csrCmd.getNotcaselevelContainerIds();
////        }
////        // 1.获取功能配置
////        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
////        if (null == putawyaFunc) {
////            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
////        }
////        Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
////        // 商品校验
////        Long skuId = null;
////        String skuBarcode = skuCmd.getBarCode();
////        Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(skuBarcode, logId);   //获取对应的商品数量,key值是sku id
////        Set<Long> icSkuIds = insideContainerSkuIds.get(insideContainerCmd.getId());   //当前容器内所有sku id集合
////        Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(insideContainerCmd.getId());
////        boolean isSkuExists = false;
////        Integer cacheSkuQty = 1;
////        Integer icSkuQty = 1;
////        for(Long cacheId : cacheSkuIdsQty.keySet()){
////            if(icSkuIds.contains(cacheId)){
////                isSkuExists = true;
////            }
////            if(true == isSkuExists){
////                skuId = cacheId;
////                cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
////                icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
////                break;
////            }
////        }
////        if(false == isSkuExists){
////            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", insideContainerCmd.getId(), insideContainerCmd.getId(), skuId, logId);
////            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {insideContainerCmd.getCode()});
////        }
////        if(cacheSkuQty > 1 && cacheSkuQty <= icSkuQty){
////            if(0 != (icSkuQty%cacheSkuQty)){
////                // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
////                log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
////                throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
////            }
////        }
////        if(WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern){
////            if(0 != new Double("1").compareTo(scanQty)){
////                log.error("one by one scan qty is not equals 1 error, skuBarcode is:[{}], logId is:[{}]", skuBarcode, logId);
////                throw new BusinessException(ErrorCodes.COMMON_ONE_BY_ONE_SCAN_QTY_ERROR);
////            }
////        }
////        skuCmd.setId(skuId);
////        skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
////        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
////        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
////        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
////            CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty, skuCmd, scanPattern,logId);
////            if (cssrCmd.isNeedScanSku()) {
////                srCmd.setNeedScanSku(true);// 直接扫描商品
////            }else if (cssrCmd.isNeedTipContainer()) { //一个容器扫描完,整箱上架，提示下一个容器
////                whSkuInventoryManager.execPutaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                srCmd.setAfterPutawayTipContianer(true);  //提示下一个容器
////                // 提示下一个容器
////                String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                srCmd.setTipContainerCode(tipContainerCode);
////            }else {
////                srCmd.setPutaway(true);
////                srCmd.setAfterPutawayTipContianer(false);   //上架完毕
////                whSkuInventoryManager.execPutaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCommand, insideContainerCmd,false, logId);
////            }
////        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
////            int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,true);
////            if(count < 1) {  //此货箱不是caselevel货箱,直接上架
////                //判断托盘上还有没有没上架的货箱
////                CheckScanSkuResultCommand checkCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, noCaselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty,skuCmd, scanPattern,logId);
////                if(checkCmd.isNeedTipContainer()){  //还有货箱没有上架
////                    srCmd.setAfterPutawayTipContianer(true);
////                    whSkuInventoryManager.execPutaway(null,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    // 提示下一个容器
////                    String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                    srCmd.setTipContainerCode(tipContainerCode);
////                }else{
////                    srCmd.setAfterPutawayTipContianer(false);
////                    whSkuInventoryManager.execPutaway(null,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCommand, insideContainerCmd,false, logId);  //所有货箱上架完毕
////                }
////            }else{
////                // 是caselevel货箱扫描商品
////                CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, caselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty,skuCmd, scanPattern,logId);
////                if (cssrCmd.isNeedScanSku()) {
////                    srCmd.setNeedScanSku(true);// 直接扫描商品
////                } else if (cssrCmd.isNeedTipContainer()) { //一个容器扫描完,整箱上架，提示下一个容器
////                    whSkuInventoryManager.execPutaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    srCmd.setAfterPutawayTipContianer(true);
////                    // 提示下一个容器
////                    String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                    srCmd.setTipContainerCode(tipContainerCode);
////                } else {
////                    srCmd.setPutaway(true);
////                    whSkuInventoryManager.execPutaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    srCmd.setAfterPutawayTipContianer(false);
////                    pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCommand, insideContainerCmd,false, logId);
////                }
////            }
////        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
////            int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,false);
////            if(count < 1) {  //此货箱不是caselevel货箱,直接上架
////                CheckScanSkuResultCommand checkCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, noCaselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty,skuCmd, scanPattern,logId);
////                if(checkCmd.isNeedTipContainer()){
////                    srCmd.setAfterPutawayTipContianer(true);
////                    whSkuInventoryManager.execPutaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    // 提示下一个容器
////                    String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                    srCmd.setTipContainerCode(tipContainerCode);
////                }else{
////                    srCmd.setAfterPutawayTipContianer(false);
////                    whSkuInventoryManager.execPutaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCommand, insideContainerCmd,false, logId);
////                }
////            }else{
////                // 是caselevel货箱扫描商品
////                CheckScanSkuResultCommand cssrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, caselevelContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty,skuCmd, scanPattern,logId);
////                if (cssrCmd.isNeedScanSku()) {
////                    srCmd.setNeedScanSku(true);// 直接扫描商品
////                } else if (cssrCmd.isNeedTipContainer()) { //一个容器扫描完,整箱上架，提示下一个容器
////                    whSkuInventoryManager.execPutaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    srCmd.setAfterPutawayTipContianer(true);
////                    // 提示下一个容器
////                    String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                    srCmd.setTipContainerCode(tipContainerCode);
////                } else {
////                    srCmd.setPutaway(true);
////                    srCmd.setAfterPutawayTipContianer(false);
////                    whSkuInventoryManager.execPutaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCommand, insideContainerCmd,false, logId);
////                }
////            }
////           
////        }else if (false == isCaselevelScanSku && false == isNotcaselevelScanSku){
////                CheckScanSkuResultCommand checkCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheSkuAndCheckContainer(outCommand, insideContainerCmd, insideContainerIds, insideContainerSkuIds, insideContainerSkuIdsQty,skuCmd, scanPattern,logId);
////                if(checkCmd.isNeedTipContainer()){
////                    srCmd.setAfterPutawayTipContianer(true);
////                    whSkuInventoryManager.execPutaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    // 提示下一个容器
////                    String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                    srCmd.setTipContainerCode(tipContainerCode);
////                }else{
////                    srCmd.setAfterPutawayTipContianer(false);
////                    srCmd.setPutaway(true);
////                    whSkuInventoryManager.execPutaway(outCommand,insideContainerCmd, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCommand, insideContainerCmd,false, logId);
////                }
////        } else {
////            log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_IS_CHANGE_ERROR);
////        }
////        log.info("PdaOutBoundBoxMoveManagerImpl containerPutwayScanSku is end"); 
////        return srCmd;
////    }
////    
////    
////    /**
////     * 
////     * @param insideContainerId
////     * @param locationId
////     * @param skuAttrIds
////     */
////    private void splitCacheScanSku(Long insideContainerId,Long locationId,String skuAttrIds) {
////        log.info("PdaOutBoundBoxMoveManagerImpl splitCacheScanSku is start"); 
////        TipScanSkuCacheCommand cacheSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + insideContainerId.toString() + locationId.toString());
////        if(null == cacheSkuCmd) {
////            cacheSkuCmd  = new TipScanSkuCacheCommand();
////            ArrayDeque<String> scanSkuAttrIds = new ArrayDeque<String>();
////            scanSkuAttrIds.add(skuAttrIds);
////            cacheSkuCmd.setScanSkuAttrIds(scanSkuAttrIds);
////        }else{
////            ArrayDeque<String> scanSkuAttrIds = cacheSkuCmd.getScanSkuAttrIds();
////            if(null == scanSkuAttrIds){
////                scanSkuAttrIds = new ArrayDeque<String>();
////            }
////            if(!scanSkuAttrIds.contains(skuAttrIds)) {
////                scanSkuAttrIds.addFirst(skuAttrIds);
////            }
////            cacheSkuCmd.setScanSkuAttrIds(scanSkuAttrIds);
////        }
////        
////        cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + insideContainerId.toString() + locationId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
////        log.info("PdaOutBoundBoxMoveManagerImpl splitCacheScanSku is end"); 
////    }
//    /***
//     * 拆箱箱上架:扫描sku商品
//     * @param barCode
//     * @param containerCode
//     * @param ouId
//     * @param insideContainerId
//     * @param userId
//     * @param locationCode
//     * @param scanPattern
//     * @param countSku
//     * @param skuQuantity
//     * @param finish
//     * @return
//     */
////      public ScanResultCommand splitPutwayScanSku(String tipLocationCode,Boolean isCancel,int putawayPatternType,Boolean isNotUser,Boolean isScanSkuSn,Boolean isRecommendFail,String outerContainerCode,String insideContainerCode,WhSkuCommand skuCmd, String locationCode, Long funcId, Long ouId, Long userId, String logId,Integer putawayPatternDetailType,Warehouse warehouse){
////          log.info("PdaOutBoundBoxMoveManagerImpl splitPutwayScanSku is start");
////          ScanResultCommand srCmd = new ScanResultCommand();
////          srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.SPLIT_CONTAINER_PUTAWAY);
////         // 0.判断是否已经缓存所有库存信息reminderLocation
////          ContainerCommand ocCmd = null;
////          ContainerCommand icCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
////          if (null == icCmd) {
////              log.error("sys guide splitContainer putaway check san sku, inside container is null error, logId is:[{}]", logId);
////              throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
////          }
////          Long insideContainerId = icCmd.getId();
////          Long outerContainerId = null;
////          if(!StringUtils.isEmpty(outerContainerCode)) {
////              ocCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
////              outerContainerId = ocCmd.getId();
////          }
////          Location loc = locationDao.findLocationByCode(locationCode, ouId);
////          if (null == loc) {
////              loc = locationDao.getLocationByBarcode(locationCode, ouId);
////              if(null == loc) {
////                  log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////                  throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////              }
////          }
////          Location tiploc = locationDao.findLocationByCode(tipLocationCode, ouId);
////          if (null == tiploc) {
////              tiploc = locationDao.getLocationByBarcode(tipLocationCode, ouId);
////              if(null == tiploc) {
////                  log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////                  throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////              }
////          }
////          Long tipLocationId = tiploc.getId();
////          List<WhSkuInventoryCommand> invList = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY, icCmd.getId().toString());
////          if (null == invList || 0 == invList.size()) {
////              srCmd.setCacheExists(false);// 缓存信息不存在
////              invList = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInventoryAndStatistic(icCmd, ouId, logId);
////          }
////          InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icCmd.getId().toString());
////          if (null == isCmd) {
////              isCmd = pdaPutawayCacheManager.sysSuggestSplitPutawayCacheInventoryStatistic(putawayPatternType,userId, icCmd, ouId, logId, outerContainerCode, putawayPatternDetailType);
////          }
////          ContainerStatisticResultCommand csrCmd = null;
////          if(null != ocCmd) {
////              csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, ocCmd.getId().toString());
////              if (null == csrCmd) {
////                      csrCmd = pdaPutawayCacheManager.sysGuideSplitContainerPutawayCacheInsideContainerStatistic(ocCmd, ouId, logId);
////              }
////          }
////          Long locationId = loc.getId();
////          Double scanQty = skuCmd.getScanSkuQty();
////          String barCode = skuCmd.getBarCode();
////          // 1.获取功能配置
////          WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(funcId, ouId, logId);
////          if (null == putawyaFunc) {
////              log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
////              throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
////          }
////          Integer scanPattern = (WhScanPatternType.ONE_BY_ONE_SCAN == putawyaFunc.getScanPattern()) ? WhScanPatternType.ONE_BY_ONE_SCAN : WhScanPatternType.NUMBER_ONLY_SCAN;
////          // 1.判断当前商品是否扫完、是否提示下一个库位、容器或上架
////          Set<Long> insideContainerIds = isCmd.getInsideContainerIds();
////          Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = isCmd.getInsideContainerSkuAttrIdsQty();   //内部容器唯一sku总件数
////          Map<Long, Map<String, Set<String>>> insideContainerSkuAttrIdsSnDefect = isCmd.getInsideContainerSkuAttrIdsSnDefect();  //内部容器唯一sku对应所有残次条码
////          Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = isCmd.getInsideContainerLocSkuAttrIds();  // 内部容器推荐库位对应唯一sku及残次条码
////          Map<Long, Map<Long, Map<String, Long>>> insideContainerLocSkuAttrIdsQty = isCmd.getInsideContainerLocSkuAttrIdsQty();
////          Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(icCmd.getId());   //获取当前内部容器度一应sku和残次条码
////          Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(icCmd.getId());
////          Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
////          Map<Long, Map<Long, Long>> insideContainerSkuIdsQty = isCmd.getInsideContainerSkuIdsQty();
////          Map<Long,Set<String>> insideContainerSkuAttrIds = isCmd.getInsideContainerSkuAttrIds();    //内部容器对应唯一sku
////          Map<Long, List<Long>> insideContainerLocSort = isCmd.getInsideContainerLocSort();
////          List<Long> locationIds = insideContainerLocSort.get(insideContainerId);
////          Map<String,Long> lskuAttrIdsQty = new HashMap<String,Long>();
////          if(!isRecommendFail) {  //推荐成功或者推荐成功走人为分支
////              Map<Long, Map<String, Long>> locSkuAttrIdsQty = insideContainerLocSkuAttrIdsQty.get(insideContainerId); // 内部容器推荐库位对应唯一sku总件数
////              lskuAttrIdsQty = locSkuAttrIdsQty.get(tipLocationId);
////          }
////          if (null != ocCmd) {
////              insideContainerIds = csrCmd.getInsideContainerIds();
////          }
////          Long sId = null;
////          Map<Long, Integer> cacheSkuIdsQty = skuRedisManager.findSkuByBarCode(barCode, logId);
////          Set<Long> icSkuIds = insideContainerSkuIds.get(icCmd.getId());
////          Map<Long, Long> icSkuIdsQty = insideContainerSkuIdsQty.get(icCmd.getId());
////          boolean isSkuExists = false;
////          Integer cacheSkuQty = 1;
////          Integer icSkuQty = 1;
////          for(Long cacheId : cacheSkuIdsQty.keySet()){
////              if(icSkuIds.contains(cacheId)){
////                  isSkuExists = true;
////              }
////              if(true == isSkuExists){
////                  sId = cacheId;
////                  cacheSkuQty = (null == cacheSkuIdsQty.get(cacheId) ? 1 : cacheSkuIdsQty.get(cacheId));
////                  icSkuQty = (null == icSkuIdsQty.get(cacheId) ? 1 : icSkuIdsQty.get(cacheId).intValue());
////                  break;
////              }
////          }
////          if(false == isSkuExists){
////              log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocCmd.getId(), icCmd.getId(), sId, logId);
////              throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
////          }
////          if(cacheSkuQty > 1 && cacheSkuQty <= icSkuQty){
////              if(0 != (icSkuQty%cacheSkuQty)){
////                  // 取模运算不为零，表示多条码配置的数量无法完成此箱中该商品的数量复合
////                  log.error("scan sku may be multi barcode sku, cacheSkuQty is:[{}], icSkuQty is:[{}], logId is:[{}]", cacheSkuQty, icSkuQty, logId);
////                  throw new BusinessException(ErrorCodes.COMMON_MULTI_BARCODE_QTY_NOT_SUITABLE);
////              }
////          }
////          skuCmd.setId(sId);
////          skuCmd.setScanSkuQty(scanQty*cacheSkuQty);//可能是多条码
////          SkuRedisCommand cacheSkuCmd = skuRedisManager.findSkuMasterBySkuId(sId, ouId, logId);
////          if (null == cacheSkuCmd) {
////              log.error("sku is not found error, logId is:[{}]", logId);
////              throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
////          }
////          // 查询库位上已有商品
////          List<WhSkuInventoryCommand> locationSkuList = whSkuInventoryDao.findWhSkuInvCmdByLocation(ouId,locationId);
////          List<WhSkuInventoryCommand> whskuList = new ArrayList<WhSkuInventoryCommand>();
////          List<WhSkuInventoryCommand> list = splitPutwayCacheInventory(icCmd,ouId,logId);
////          for(WhSkuInventoryCommand whSkuInvCmd:list) {
////              String skuInvAttrId = SkuCategoryProvider.getSkuAttrIdByInv(whSkuInvCmd);
////              String skuAttrId = SkuCategoryProvider.concatSkuAttrId(skuCmd.getId(), skuCmd.getInvType(),skuCmd.getInvBatchNumber(),skuCmd.getInvCountryOfOrigin(), skuCmd.getInvStatus(), skuCmd.getInvMfgDate(), skuCmd.getInvExpDate(), skuCmd.getInvAttr1(), skuCmd.getInvAttr2(), skuCmd.getInvAttr3(),
////                  skuCmd.getInvAttr4(), skuCmd.getInvAttr5(), skuCmd.getSkuSn(), skuCmd.getSkuDefect());
////              if(skuInvAttrId.equals(skuAttrId)) {
////                  for(int i=0;i<Integer.valueOf(scanQty.toString());i++) {
////                      list.add(whSkuInvCmd);
////                  }
////                  break;
////              }
////          }
////          //如果是多次扫sn，则跳过
////          if(!(null != isScanSkuSn && isScanSkuSn == true)) {
////              if(isRecommendFail) { //推荐库位失败的情况,绑定库位
////                  //缓存容器库存
////                  whSkuInventoryManager.manMadeBinding(outerContainerId, insideContainerId, warehouse, locationId, putawayPatternDetailType, ouId, userId, insideContainerCode, skuCmd.getScanSkuQty());
////              }else{
////                  //手动绑定库位
////                  List<WhSkuInventoryTobefilled> listTobeFilled = whSkuInventoryTobefilledDao.findWhSkuInventoryTobefilled(outerContainerId, insideContainerId, ouId);
////                  for(WhSkuInventoryTobefilled skuInvTobeFilled :listTobeFilled) {
////                      skuInvTobeFilled.setLocationId(locationId);  //中心设置库位
////                      //先删除待入库数据，在添加
////                      Long tobeFilledId = skuInvTobeFilled.getId();
////                      whSkuInventoryTobefilledDao.deleteByExt(tobeFilledId, ouId);
////                      //重新插入
////                      skuInvTobeFilled.setId(null);  //id置空
////                      skuInvTobeFilled.setLastModifyTime(new Date());
////                      whSkuInventoryTobefilledDao.insert(skuInvTobeFilled);
////                  }
////              }
////              // 累加容器，容器内sku商品、库位上已有容器，商品重量， 判断是否<=库位承重 *
////              Boolean result = this.IsLocationBearWeight(insideContainerId, locationSkuList, whskuList,putawayPatternDetailType,locationId, ouId);
////              if(result) {//超重时,更换库位
////                  srCmd = this.reminderLocation(isCmd, srCmd, ouId);
////                  srCmd.setIsNeedScanNewLocation(true);
////                  return srCmd;
////              }else{
////                  srCmd.setIsNeedScanNewLocation(false);
////              }
////          }
////          CheckScanSkuResultCommand cssrCmd =  pdaPutawayCacheManager.sysSuggestSplitContainerPutawayTipSkuOrContainer(lskuAttrIdsQty,locationIds,tipLocationId,isCancel,isRecommendFail,locSkuAttrIds,scanPattern,ocCmd, icCmd, insideContainerIds, insideContainerSkuAttrIdsQty, insideContainerSkuAttrIdsSnDefect, insideContainerSkuAttrIds, skuCmd, logId);
////          if (cssrCmd.isNeedTipSkuSn()) {
////              // 当前商品还未扫描，继续扫sn残次信息
////              String tipSkuAttrId = cssrCmd.getTipSkuAttrId();
////              this.splitCacheScanSku(insideContainerId, tipLocationId, tipSkuAttrId);   //  缓存
////              srCmd.setIsContinueScanSn(true);
////          } else if (cssrCmd.isNeedTipSku()) {
////              String tipSkuAttrId = cssrCmd.getTipSkuAttrId();
////              srCmd.setNeedTipSku(true);// 提示下一个sku
////              if(isRecommendFail) {  //人工流程,扫一个上一个
////                  ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipLocationId.toString()+sId.toString());
////                  List<String> scanSkuAttrIds = null;
////                  if(null != scanSkuCmd) {
////                      scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();
////                  }
////                  whSkuInventoryManager.execPutaway(ocCmd, icCmd, locationCode, skuCmd, scanSkuAttrIds, scanQty, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                  //拆箱清除缓存
////                  this.splitContainerPutawayRemoveAllCache(ocCmd, icCmd, locationId, logId,true,sId);
////                  srCmd.setRecommendFail(true);  //推荐失败
////              }else if(isNotUser){
//////                  whSkuInventoryManager.execPutaway(skuCmd.getScanSkuQty(), warehouse, userId, ocCmd, icCmd, locationCode, putawayPatternDetailType, ouId, skuAttrId);
////                  ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipLocationId.toString()+sId.toString());
////                  List<String> scanSkuAttrIds = null;
////                  if(null != scanSkuCmd) {
////                      scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();
////                  }
////                  whSkuInventoryManager.execPutaway(ocCmd, icCmd, locationCode, skuCmd, scanSkuAttrIds, scanQty, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                //拆箱清除缓存
////                  this.splitContainerPutawayRemoveAllCache(ocCmd, icCmd, locationId, logId,true,sId);
////                  //拆箱清除缓存
////                  Long locId = null;
////                  for(Long id:locationIds) {
////                            locId = id;
////                            break;
////                  }
////                  Location location = locationDao.findByIdExt(locId, ouId);
////                  if(null == location) {
////                      throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
////                  }
////                  srCmd.setTipLocationCode(location.getCode()); 
////                  srCmd.setTipLocBarCode(location.getBarCode());
////                  this.splitCacheScanSku(insideContainerId, tipLocationId, tipSkuAttrId);   //  缓存
////              }else{
////                //提示下一个商品
//////                  whSkuInventoryManager.execPutaway(skuCmd.getScanSkuQty(), warehouse, userId, ocCmd, icCmd, locationCode, putawayPatternDetailType, ouId, skuAttrId);
////                  ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipLocationId.toString()+sId.toString());
////                  List<String> scanSkuAttrIds = null;
////                  if(null != scanSkuCmd) {
////                      scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();
////                  }
////                  whSkuInventoryManager.execPutaway(ocCmd, icCmd, locationCode, skuCmd, scanSkuAttrIds, scanQty, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                //拆箱清除缓存
////                  this.splitContainerPutawayRemoveAllCache(ocCmd, icCmd, locationId, logId,true,sId);
////                  //拆箱清除缓存
////                  Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
////                  WhSkuCommand tipSkuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
////                  if (null == tipSkuCmd) {
////                      log.error("sku is not found error, logId is:[{}]", logId);
////                      throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
////                  }
////                  //tipSkuDetailAspect(isRecommendFail,srCmd, tipSkuAttrId, locSkuAttrIds, skuAttrIdsQty, logId);
////                  srCmd.setTipSkuBarcode(skuCmd.getBarCode());
////                  this.splitCacheScanSku(insideContainerId, tipLocationId, tipSkuAttrId);   //  缓存
////              }
////             
////          } else if (cssrCmd.isNeedTipLoc()) {
////              // 当前库位对应的商品已扫描完毕，可上架，并提示下一个库位
////              srCmd.setAfterPutawayTipLoc(true);
////              Long tipLocId = cssrCmd.getTipLocId();
////              Location tipLoc = locationDao.findByIdExt(tipLocId, ouId);
////              if (null == tipLoc) {
////                  log.error("location is null error, locId is:[{}], logId is:[{}]", tipLocId, logId);
////                  throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////              }
////              srCmd.setTipLocationCode(tipLoc.getCode());
//////              whSkuInventoryManager.execPutaway(skuCmd.getScanSkuQty(), warehouse, userId, ocCmd, icCmd, locationCode, putawayPatternDetailType, ouId, skuAttrId);
////              ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipLocationId.toString()+sId.toString());
////              List<String> scanSkuAttrIds = null;
////              if(null != scanSkuCmd) {
////                  scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();
////              }
////              whSkuInventoryManager.execPutaway(ocCmd, icCmd, locationCode, skuCmd, scanSkuAttrIds, scanQty, warehouse, putawayPatternDetailType, ouId, userId, logId);
////            //拆箱清除缓存
////              this.splitContainerPutawayRemoveAllCache(ocCmd, icCmd, locationId, logId,true,sId);
////              //拆箱清除缓存
////          } else if (cssrCmd.isNeedTipContainer()) {
//////                  whSkuInventoryManager.execPutaway(skuCmd.getScanSkuQty(), warehouse, userId, ocCmd, icCmd, locationCode, putawayPatternDetailType, ouId, skuAttrId);
////              ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipLocationId.toString()+sId.toString());
////              List<String> scanSkuAttrIds = null;
////              if(null != scanSkuCmd) {
////                  scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();
////              }
////              whSkuInventoryManager.execPutaway(ocCmd, icCmd, locationCode, skuCmd, scanSkuAttrIds, scanQty, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                  //拆箱清除缓存
////                  this.splitContainerPutawayRemoveAllCache(ocCmd, icCmd, locationId, logId,true,sId);
////              // 当前容器已扫描完毕，可上架，并提示下一个容器
////              srCmd.setAfterPutawayTipContianer(true);
////              Long tipContainerId = cssrCmd.getTipContainerId();
////              Container tipContainer = containerDao.findByIdExt(tipContainerId, ouId);
////              if (null == tipContainer) {
////                  log.error("tip container is null error, containerId is:[{}], logId is:[{}]", tipContainerId, logId);
////                  throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
////              }
////              srCmd.setTipContainerCode(tipContainer.getCode());
////          } else {
////              // 执行上架
////              srCmd.setPutaway(true);
//////                  whSkuInventoryManager.execPutaway(skuCmd.getScanSkuQty(), warehouse, userId, ocCmd, icCmd, locationCode, putawayPatternDetailType, ouId, skuAttrId);
////              ScanSkuCacheCommand scanSkuCmd = cacheManager.getObject(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + insideContainerId.toString() + tipLocationId.toString()+sId.toString());
////              List<String> scanSkuAttrIds = null;
////              if(null != scanSkuCmd) {
////                  scanSkuAttrIds = scanSkuCmd.getScanSkuAttrIds();
////              }
////              whSkuInventoryManager.execPutaway(ocCmd, icCmd, locationCode, skuCmd, scanSkuAttrIds, scanQty, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                //拆箱清除缓存
////                  this.splitContainerPutawayRemoveAllCache(ocCmd, icCmd, locationId, logId,false,sId);
////          }
////          return srCmd;
////      }
////
////      
////      private void splitContainerPutawayRemoveAllCache(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Long locationId, String logId,Boolean isPutawayEnd,Long sId){
////          Long icId = insideContainerCmd.getId();
////            if(isPutawayEnd == true) {  //人工上架,整箱或整托还没结束
////                cacheManager.remove(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + icId.toString() + locationId.toString()+sId.toString());
////                cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + icId.toString() + sId.toString());
////            }else{
////                if(null != containerCmd) {
////                    Long ocId = containerCmd.getId();
////                    // 0.先清除所有复核商品队列及库位队列及内部库存及统计信息
////                    InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
////                    if (null == isCmd) {
////                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR); 
////                    }
////                    Map<Long,Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
////                    Set<Long> skuIds = insideContainerSkuIds.get(icId);
////                       for(Long skuId:skuIds){
////                           cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString()+skuId.toString());
////                     }
////                     if(null != sId){
////                         cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + icId.toString() + sId.toString());
////                         cacheManager.remove(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + icId.toString() + locationId.toString()+sId.toString());
////                     }
////                     cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString()+locationId.toString());
////                     cacheManager.remove(CacheConstants.SCAN_CONTAINER_QUEUE+ocId.toString());
////                     cacheManager.removeMapValue(CacheConstants.CONTAINER_STATISTIC , ocId.toString());
////                     cacheManager.remove(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
////                }else{
////                    InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
////                    if (null == isCmd) {
////                      throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR); 
////                    }
////                    Map<Long,Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
////                    Set<Long> skuIds = insideContainerSkuIds.get(icId);
////                       for(Long skuId:skuIds){
////                           cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString()+skuId.toString());
////                     }
////                     cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString()+locationId.toString());
////                     cacheManager.remove(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString()+locationId.toString());
////                     cacheManager.remove(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
////                }
////                 // 1.清除所有库存统计信息
////                 cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
////                 // 2.清除所有库存缓存信息
////                 cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
////            }
////               
////      }
//      
//      /***
//       * 整托:人工上架分支
//       * @param outerContainerCode
//       * @param insideContainerCode
//       * @param funcId
//       * @param ouId
//       * @param userId
//       * @param putawayPatternDetailType
//       * @return
//       */
////    @Override
////    public ScanResultCommand pallentManPutwayFlow(int putawayPatternType,Boolean isRecommendFail,String invAttrMgmtHouse,String locationCode,String locBarCode,String outerContainerCode, String insideContainerCode, Long functionId, Long ouId, Long userId, Integer putawayPatternDetailType,Warehouse warehouse) {
////        // TODO Auto-generated method stub
////        log.info("PdaOutBoundBoxMoveManagerImpl pallentManPutwayFlow is start");
////        ScanResultCommand sRcmd = new ScanResultCommand();
////        sRcmd.setPutawayPatternDetailType(putawayPatternDetailType);
////        ContainerCommand outCommand = null;
////        outCommand = containerDao.getContainerByCode(outerContainerCode, ouId); // 根据外部容器编码查询外部容器
////        if (null == outCommand) {
////                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 外部容器不存在
////        }
////        Long outerContainerId = outCommand.getId();
////        Location location = null;
////        if(StringUtils.isEmpty(locationCode) && !StringUtils.isEmpty(locBarCode)) {  //库位号为空,库位条码不为空
////            location = locationDao.getLocationByBarcode(locBarCode, ouId);
////            if (null == location) {
////                log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////            }
////            locationCode = location.getBarCode();
////        }else{
////            location =  locationDao.findLocationByCode(locationCode, ouId);
////        }
////        // 验证库位是否存在
////        if (null == location) {
////            log.error("pdaScanLocation location is null logid: " + logId);
////            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
////        }
////        // 验证库位状态是否可用:lifecycle
////        if (location.getLifecycle() != 1) {
////            log.error("pdaScanLocation lifecycle is error logid: " + logId);
////            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_LIFECYCLE_ERROR);
////        }
////        sRcmd.setLocationCode(locationCode);
////        boolean mixStacking = location.getIsMixStacking(); // 库位是否混放
////        Long locationId = location.getId();
////        // 从缓存中获取要上架的sku商品信息
////        List<WhSkuInventoryCommand>  whskuList =  this.palletPutwayCacheInventory(outCommand,ouId,logId);
////        // 验证库位是否静态库位
////        if (location.getIsStatic()) {
////            // 判断库位是否绑定了容器内所有的SKU商品
////            WhSkuLocationCommand whSkuLocComand = new WhSkuLocationCommand();
////            whSkuLocComand.setOuId(ouId);
////            whSkuLocComand.setLocationId(location.getId());
////            List<WhSkuLocationCommand> listCommand = whSkuLocationDao.findSkuLocationToShard(whSkuLocComand);
////            // 验证库位是否绑定了容器内的所有商品
////            boolean result = true;
////            for (WhSkuInventoryCommand skuInventory : whskuList) {
////                Long skuId = skuInventory.getSkuId();
////                for (WhSkuLocationCommand skuLoc : listCommand) {
////                    Long sId = skuLoc.getSkuId();
////                    if (skuId.equals(sId)) {
////                        result = false;
////                        break;
////                    }
////                }
////            }
////            if (result) { // 静态库位没有绑定所有的sku商品
////                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_STATICLOCATION_NOTINCLUDEALLSKU);
////            } else {
////                // 判断库位是否允许混放
////                if (mixStacking) {
////                    // 允许混放
////                    this.pdaLocationIsMix(whskuList,ouId,locationId,invAttrMgmtHouse,putawayPatternDetailType,outCommand);
////                } else {
////                    // 不允许混放
////                    this.pdaLocationNotMix(whskuList,outCommand,ouId,locationId,putawayPatternDetailType);
////                }
////            }
////        } else { // 不是静态库位
////            if (mixStacking) { // 判断是否允许混放
////                // 允许混放
////                this.pdaLocationIsMix(whskuList,ouId,locationId,invAttrMgmtHouse,putawayPatternDetailType,outCommand);
////            } else {
////                // 不允许混放
////                this.pdaLocationNotMix(whskuList,outCommand,ouId,locationId,putawayPatternDetailType);
////            }
////        }
////        if(isRecommendFail) { //推荐库位失败的情况,绑定库位
////            //缓存容器库存
////            whSkuInventoryManager.manMadeBinding(outerContainerId, null, warehouse, locationId, putawayPatternDetailType, ouId, userId, insideContainerCode, null);
////        }else{
////            //手动绑定库位
////            List<WhSkuInventoryTobefilled> listTobeFilled = whSkuInventoryTobefilledDao.findWhSkuInventoryTobefilled(outerContainerId, null, ouId);
////            for(WhSkuInventoryTobefilled skuInvTobeFilled :listTobeFilled) {
////                skuInvTobeFilled.setLocationId(locationId);  //中心设置库位
////                //先删除待入库数据，在添加
////                Long tobeFilledId = skuInvTobeFilled.getId();
////                whSkuInventoryTobefilledDao.deleteByExt(tobeFilledId, ouId);
////                //重新插入
////                skuInvTobeFilled.setId(null);  //id置空
////                skuInvTobeFilled.setLastModifyTime(new Date());
////                whSkuInventoryTobefilledDao.insert(skuInvTobeFilled);
////            }
////        }
////        // 2.清除所有库存统计信息
//////        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, outerContainerId.toString());
////        InventoryStatisticResultCommand iSRCmd = inventoryStatisticManager.cacheContainerInventoryStatistics(putawayPatternType,whskuList,userId, ouId, logId, outCommand,  putawayPatternDetailType,outerContainerCode);
////        //设置库位信息
////        Set<Long> locationIds = iSRCmd.getLocationIds();
//////        Set<Long> locationIds = new HashSet<Long>();
////        if(locationIds == null  || locationIds.size() == 0) {
////           locationIds = new HashSet<Long>();
////          //添加库位
////           locationIds.add(locationId);
////           iSRCmd.setLocationIds(locationIds);
////        }
////        //缓存库存统计信息
////        pdaPutawayCacheManager.sysSuggestPalletPutawayCacheInventoryStatistic(putawayPatternType,userId, outCommand, ouId,logId, outCommand.getCode(), putawayPatternDetailType);
////        // 1.获取功能配置
////        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
////        if (null == putawyaFunc) {
////            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
////        }
////        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
////        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
////        //获取库存缓存信息
////        InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, outerContainerId.toString());
////        ContainerStatisticResultCommand csrCmd =  cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, outerContainerId.toString());
////        Map<Long, String> insideContainerIdsCode = csrCmd.getInsideContainerIdsCode();
////        Set<Long>  insideContainerIds = isrCmd.getInsideContainerIds();  //所有内部容器id集合
////        Set<Long> caselevelContainerIds = isrCmd.getCaselevelContainerIds(); //caselevel货箱id集合
////        Set<Long> noCaselevelContainerIds = isrCmd.getNotcaselevelContainerIds();  //非caselevel货箱Id集合
////        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
////                 // 提示一个内部容器id
////                Long tipInsideContainerId =  pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(outCommand, insideContainerIds,logId);
////                sRcmd.setNeedTipContainer(true);  //需要提示容器
////                // 提示下一个容器
////                sRcmd.setTipContainerCode(insideContainerIdsCode.get(tipInsideContainerId));
////        } else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) {
////                
////                if(caselevelContainerIds.size() > 0) {
////                    // 只扫caselevel货箱中的商品
////                    Long tipInsideContainerId =  pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(outCommand, caselevelContainerIds,logId);
////                    sRcmd.setNeedTipContainer(true);  //需要提示容器
////                    // 提示下一个容器
////                    sRcmd.setTipContainerCode(insideContainerIdsCode.get(tipInsideContainerId));
////                }else{
////                    whSkuInventoryManager.execPutaway(outCommand, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    sRcmd.setPutaway(true);  //上架成功，返回到首页
////                    pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(outCommand, logId);
////                }
////                
////        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {
////              if(noCaselevelContainerIds.size() > 0) {
////               // 只扫非caselvel货箱
////                  Long tipInsideContainerId =  pdaPutawayCacheManager.sysGuidePalletPutawayCacheTipContainer0(outCommand, noCaselevelContainerIds,logId);
////                  sRcmd.setNeedTipContainer(true);  //需要提示容器
////                  // 提示下一个容器
////                  sRcmd.setTipContainerCode(insideContainerIdsCode.get(tipInsideContainerId));
////                  sRcmd.setNotCaselevelScanContainer(true);
////              }else{
////                  whSkuInventoryManager.execPutaway(outCommand, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                  sRcmd.setPutaway(true);  //上架成功，返回到首页
////                  pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(outCommand, logId);
////              }
////            } else if(false == isCaselevelScanSku && false == isNotcaselevelScanSku) {
////                    whSkuInventoryManager.execPutaway(outCommand, null, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                    sRcmd.setPutaway(true);  //上架成功，返回到首页
////                    pdaPutawayCacheManager.sysGuidePalletPutawayRemoveAllCache(outCommand, logId);
////            }else{
////                log.error("function conf is error, should check scan sku detail, logId is:[{}]", logId);
////                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
////            }
////        log.info("PdaOutBoundBoxMoveManagerImpl pallentManPutwayFlow is end");
////        return sRcmd;
////    }
//    
//    
//    
//    /***
//     * 整箱:人工上架分支
//     * @param outerContainerCode
//     * @param insideContainerCode
//     * @param funcId
//     * @param ouId
//     * @param userId
//     * @param putawayPatternDetailType
//     * @return
//     */
////    public ScanResultCommand containerManPutwayFlow(int putawayPatternType,Boolean isRecommendFail,String invAttrMgmtHouse,String locationCode,String locBarCode,String outerContainerCode,String insideContainerCode,Long functionId,Long ouId,Long userId,Integer putawayPatternDetailType,Warehouse warehouse){
////        ScanResultCommand srCmd = new ScanResultCommand();
////        srCmd.setPutawayPatternDetailType(putawayPatternDetailType);
////        ContainerCommand insideCommand = null;
////        if(!StringUtil.isEmpty(insideContainerCode)) {
////            insideCommand = containerDao.getContainerByCode(insideContainerCode, ouId); // 根据内部容器编码查询内部容器
////        }
////        if (null == insideCommand) {
////            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 外部容器不存在
////        }
////        Long insideContainerId = insideCommand.getId();
////        Long outerContainerId = null;
////        ContainerCommand outCommand = null;
////        if(!StringUtil.isEmpty(outerContainerCode)) {
////            outCommand = containerDao.getContainerByCode(outerContainerCode, ouId); // 根据外部容器编码查询外部容器
////            outerContainerId = outCommand.getId();
////        }
////        Location location = null;
////        if(StringUtils.isEmpty(locationCode) && !StringUtils.isEmpty(locBarCode)) {  //库位号为空,库位条码不为空
////            location = locationDao.getLocationByBarcode(locBarCode, ouId);
////            if (null == location) {
////                log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////            }
////            locationCode = location.getBarCode();  //如果库位编码为空,条码不为空，则条码赋值给编码
////        }else{
////            location =  locationDao.findLocationByCode(locationCode, ouId);
////        }
////        // 验证库位是否存在
////        if (null == location) {
////            log.error("pdaScanLocation location is null logid: " + logId);
////            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
////        }
////        srCmd.setLocationCode(locationCode);
////        // 验证库位状态是否可用:lifecycle
////        if (location.getLifecycle() != 1) {
////            log.error("pdaScanLocation lifecycle is error logid: " + logId);
////            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_LIFECYCLE_ERROR);
////        }
////        boolean mixStacking = location.getIsMixStacking(); // 库位是否混放
////        Long locationId = location.getId();
////        // 从缓存中获取要上架的sku商品信息
////        List<WhSkuInventoryCommand>  whskuList =  this.containerPutwayCacheInventory(insideCommand,ouId,logId);
////        // 验证库位是否静态库位
////        if (location.getIsStatic()) {
////            // 判断库位是否绑定了容器内所有的SKU商品
////            WhSkuLocationCommand whSkuLocComand = new WhSkuLocationCommand();
////            whSkuLocComand.setOuId(ouId);
////            whSkuLocComand.setLocationId(location.getId());
////            List<WhSkuLocationCommand> listCommand = whSkuLocationDao.findSkuLocationToShard(whSkuLocComand);
////            // 验证库位是否绑定了容器内的所有商品
////            boolean result = true;
////            for (WhSkuInventoryCommand skuInventory : whskuList) {
////                Long skuId = skuInventory.getSkuId();
////                for (WhSkuLocationCommand skuLoc : listCommand) {
////                    Long sId = skuLoc.getSkuId();
////                    if (skuId.equals(sId)) {
////                        result = false;
////                        break;
////                    }
////                }
////            }
////            if (result) { // 静态库位没有绑定所有的sku商品
////                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_STATICLOCATION_NOTINCLUDEALLSKU);
////            } else {
////                // 判断库位是否允许混放
////                if (mixStacking) {
////                    // 允许混放
////                    this.pdaLocationIsMix(whskuList,ouId,locationId,invAttrMgmtHouse,putawayPatternDetailType,insideCommand);
////                } else {
////                    // 不允许混放
////                    this.pdaLocationNotMix(whskuList,insideCommand,ouId,locationId,putawayPatternDetailType);
////                }
////            }
////        } else { // 不是静态库位
////            if (mixStacking) { // 判断是否允许混放
////                // 允许混放
////                this.pdaLocationIsMix(whskuList,ouId,locationId,invAttrMgmtHouse,putawayPatternDetailType,insideCommand);
////            } else {
////                // 不允许混放
////                this.pdaLocationNotMix(whskuList,insideCommand,ouId,locationId,putawayPatternDetailType);
////            }
////        }
////        if(isRecommendFail) { //推荐库位失败的情况,绑定库位
////            whSkuInventoryManager.manMadeBinding(outerContainerId, insideContainerId, warehouse, locationId, putawayPatternDetailType, ouId, userId, insideContainerCode, null);
////        }else{
////            //手动绑定库位
////            List<WhSkuInventoryTobefilled> listTobeFilled = whSkuInventoryTobefilledDao.findWhSkuInventoryTobefilled(null, insideContainerId, ouId);
////            for(WhSkuInventoryTobefilled skuInvTobeFilled :listTobeFilled) {
////                skuInvTobeFilled.setLocationId(locationId);  //中心设置库位
////                //先删除待入库数据，在添加
////                Long tobeFilledId = skuInvTobeFilled.getId();
////                whSkuInventoryTobefilledDao.deleteByExt(tobeFilledId, ouId);
////                //重新插入
////                skuInvTobeFilled.setId(null);  //id置空
////                skuInvTobeFilled.setLastModifyTime(new Date());
////                whSkuInventoryTobefilledDao.insert(skuInvTobeFilled);
////            }
////        }
////        // 2.清除所有库存统计信息
//////        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideCommand.toString());
////        InventoryStatisticResultCommand iSRCmd = inventoryStatisticManager.cacheContainerInventoryStatistics(putawayPatternType,whskuList,userId, ouId, logId, insideCommand, putawayPatternDetailType,outerContainerCode);
////        //设置库位信息
////        Set<Long> locationIds = iSRCmd.getLocationIds();
//////        Set<Long> locationIds = new HashSet<Long>();
////        if(locationIds == null  || locationIds.size() == 0) {
////           locationIds = new HashSet<Long>();
////          //添加库位
////           locationIds.add(locationId);
////           iSRCmd.setLocationIds(locationIds);
////        }
////        //缓存库存统计信息
////        pdaPutawayCacheManager.sysGuideContainerPutawayCacheInventoryStatistic(insideCommand, iSRCmd, ouId, logId);
////        // 1.获取功能配置
////        WhFunctionPutAway putawyaFunc = whFunctionPutAwayManager.findWhFunctionPutAwayByFunctionId(functionId, ouId, logId);
////        if (null == putawyaFunc) {
////            log.error("whFunctionPutaway is null error, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_FUNCTION_CONF_IS_NULL_ERROR);
////        }
////        Boolean isCaselevelScanSku = (null == putawyaFunc.getIsCaselevelScanSku() ? false : putawyaFunc.getIsCaselevelScanSku());
////        Boolean isNotcaselevelScanSku = (null == putawyaFunc.getIsNotcaselevelScanSku() ? false : putawyaFunc.getIsNotcaselevelScanSku());
////        //获取库存缓存信息
////        srCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
////        InventoryStatisticResultCommand isrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerId.toString());
////        if (null == isrCmd) {
////            isrCmd = pdaPutawayCacheManager.sysSuggestContainerPutawayCacheInventoryStatistic(putawayPatternType,userId, insideCommand, ouId, insideContainerCode, outerContainerCode, putawayPatternDetailType);
////        }
////        ContainerStatisticResultCommand csrCmd = new ContainerStatisticResultCommand();
////        if (null != outCommand) {
////            csrCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_STATISTIC, outCommand.getId().toString());
////            if (null == csrCmd) {
////                csrCmd = pdaPutawayCacheManager.sysGuideContainerPutawayCacheInsideContainerStatistic(outCommand, ouId, logId);
////            }
////        }
////        Set<Long> insideContainerIds = isrCmd.getInsideContainerIds();   //所有容器id集合
////        if(null != outCommand){
////             insideContainerIds = csrCmd.getInsideContainerIds();
////        }
////        if (true == isCaselevelScanSku && true == isNotcaselevelScanSku) {
////                srCmd.setNeedScanSku(true);// 直接扫描商品
////        }else if (true == isCaselevelScanSku && false == isNotcaselevelScanSku) { //caselevel
////            //判断该货箱是否是caselevel货箱
////            int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,true);
////            if(count < 1) {  //此货箱不是caselevel货箱,直接上架
////                srCmd.setNeedScanSku(false);// 不扫描商品，直接上架
////                whSkuInventoryManager.execPutaway(outCommand, insideCommand, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                CheckScanSkuResultCommand  cSRCmd =  this.sysSuggestCacheContainer(outCommand,insideCommand, insideContainerIds, logId);
////                if(cSRCmd.isNeedTipContainer()){ //还有内部容器没有扫描完毕
////                    srCmd.setNeedTipContainer(true);  //跳转到扫描容器页面
////                    // 提示下一个容器
////                    String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                    srCmd.setTipContainerCode(tipContainerCode);
////                }else{
////                    srCmd.setPutaway(true);
////                    pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCommand, insideCommand,false,logId);    //已经扫描完毕或者上架容器只有货箱没有托盘
////                }
////            }else{
////                // 是caselevel货箱扫描商品
////                srCmd.setNeedScanSku(true);// 直接扫描商品
////            }
////        } else if (false == isCaselevelScanSku && true == isNotcaselevelScanSku) {  //判断扫描的货箱是否是非caselevel货箱
////               //判断该货箱是否是caselevel货箱
////               int count = whCartonDao.findWhCartonCountByContainerId(ouId,insideContainerId,false);
////               if(count < 1) {  //此货箱不是caselevel货箱,直接上架
////                   CheckScanSkuResultCommand  cSRCmd =  this.sysSuggestCacheContainer(outCommand, insideCommand, insideContainerIds, logId);
////                   if(cSRCmd.isNeedTipContainer()){ //还有内部容器没有扫描完毕
////                       srCmd.setNeedTipContainer(true);  //跳转到扫描容器页面
////                       whSkuInventoryManager.execPutaway(outCommand, insideCommand, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                       // 提示下一个容器
////                       String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                       srCmd.setTipContainerCode(tipContainerCode);
////                   }else{
////                       srCmd.setPutaway(true);
////                       whSkuInventoryManager.execPutaway(outCommand, insideCommand, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                       pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCommand, insideCommand,false,logId);    //已经扫描完毕或者上架容器只有货箱没有托盘
////                   }
////               }else{
////                   srCmd.setNeedScanSku(true);// 直接扫描商品
////               }
////         }else if(false == isCaselevelScanSku && false == isNotcaselevelScanSku) {  //整箱上架,直接执行上架流程
////             CheckScanSkuResultCommand  cSRCmd =  this.sysSuggestCacheContainer(outCommand, insideCommand, insideContainerIds, logId);
////             if(cSRCmd.isNeedTipContainer()){ //还有内部容器没有扫描完毕
////                 srCmd.setNeedTipContainer(true);  //跳转到扫描容器页面
////                 whSkuInventoryManager.execPutaway(outCommand, insideCommand, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                 // 提示下一个容器
////                 String tipContainerCode = sysSuggestTipContainer((null == outCommand ? null : outCommand.getCode()), functionId, WhPutawayPatternDetailType.CONTAINER_PUTAWAY, ouId, userId, logId);
////                 srCmd.setTipContainerCode(tipContainerCode);
////             }else{
////                 srCmd.setPutaway(true);
////                 whSkuInventoryManager.execPutaway(outCommand, insideCommand, locationCode, functionId, warehouse, putawayPatternDetailType, ouId, userId, logId);
////                 pdaPutawayCacheManager.sysGuideContainerPutawayRemoveAllCache(outCommand, insideCommand,false,logId);    //已经扫描完毕或者上架容器只有货箱没有托盘
////             }
////        }
////        return srCmd;
////    }
////    
//    
//    /***
//     * 拆箱:人工上架分支
//     * @param outerContainerCode
//     * @param insideContainerCode
//     * @param funcId
//     * @param ouId
//     * @param userId
//     * @param putawayPatternDetailType
//     * @return
//     */
////    public ScanResultCommand splitContaienrManPutwayFlow(String tipLocationCode,int putawayPatternType,Boolean isNotUser,Boolean isRecommendFail,String invAttrMgmtHouse,String locationCode,String locBarCode,String outerContainerCode,String insideContainerCode,Long funcId,Long ouId,Long userId,Integer putawayPatternDetailType,Warehouse warehouse){
////        ScanResultCommand sRcmd = new ScanResultCommand();
////        sRcmd.setPutawayPatternDetailType(putawayPatternDetailType);
////        ContainerCommand insideCommand = null;
////        if(!StringUtil.isEmpty(insideContainerCode)) {
////            insideCommand = containerDao.getContainerByCode(insideContainerCode, ouId); // 根据内部容器编码查询内部容器
////        }
////        if (null == insideCommand) {
////            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL); // 外部容器不存在
////        }
////        Long insideContainerId = insideCommand.getId();
////        Location tipLoc = locationDao.getLocationByBarcode(tipLocationCode, ouId);
////        if (null == tipLoc) {
////            tipLoc = locationDao.findLocationByCode(tipLocationCode, ouId);
////            if (null == tipLoc) {
////                log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////            }
////        }
////        Long tipLocationId = tipLoc.getId(); 
////        Location location = null;
////        if(StringUtils.isEmpty(locationCode) && !StringUtils.isEmpty(locBarCode)) {  //库位号为空,库位条码不为空
////            location = locationDao.getLocationByBarcode(locBarCode, ouId);
////            if (null == location) {
////                log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////            }
////            locationCode = location.getBarCode();
////        }else{
////            location =  locationDao.findLocationByCode(locationCode, ouId);
////        }
////        // 验证库位是否存在
////        if (null == location) {
////            log.error("pdaScanLocation location is null logid: " + logId);
////            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
////        }
////        // 验证库位状态是否可用:lifecycle
////        if (location.getLifecycle() != 1) {
////            log.error("pdaScanLocation lifecycle is error logid: " + logId);
////            throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_LIFECYCLE_ERROR);
////        }
////        sRcmd.setLocationCode(locationCode);
////        boolean mixStacking = location.getIsMixStacking(); // 库位是否混放
////        Long locationId = location.getId();
////        // 从缓存中获取要上架的sku商品信息
////        List<WhSkuInventoryCommand>  whskuList =  this.splitPutwayCacheInventory(insideCommand,ouId,logId);
////        // 验证库位是否静态库位
////        if (location.getIsStatic()) {
////            // 判断库位是否绑定了容器内所有的SKU商品
////            WhSkuLocationCommand whSkuLocComand = new WhSkuLocationCommand();
////            whSkuLocComand.setOuId(ouId);
////            whSkuLocComand.setLocationId(location.getId());
////            List<WhSkuLocationCommand> listCommand = whSkuLocationDao.findSkuLocationToShard(whSkuLocComand);
////            // 验证库位是否绑定了容器内的所有商品
////            boolean result = true;
////            for (WhSkuInventoryCommand skuInventory : whskuList) {
////                Long skuId = skuInventory.getSkuId();
////                for (WhSkuLocationCommand skuLoc : listCommand) {
////                    Long sId = skuLoc.getSkuId();
////                    if (skuId.equals(sId)) {
////                        result = false;
////                        break;
////                    }
////                }
////            }
////            if (result) { // 静态库位没有绑定所有的sku商品
////                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_STATICLOCATION_NOTINCLUDEALLSKU);
////            } else {
////                // 判断库位是否允许混放
////                if (mixStacking) {
////                    // 允许混放
////                    this.pdaLocationIsMix(whskuList,ouId,locationId,invAttrMgmtHouse,putawayPatternDetailType,insideCommand);
////                } else {
////                    // 不允许混放
////                    this.pdaLocationNotMix(whskuList,insideCommand,ouId,locationId,putawayPatternDetailType);
////                }
////            }
////        } else { // 不是静态库位
////            if (mixStacking) { // 判断是否允许混放
////                // 允许混放
////                this.pdaLocationIsMix(whskuList,ouId,locationId,invAttrMgmtHouse,putawayPatternDetailType,insideCommand);
////            } else {
////                // 不允许混放
////                this.pdaLocationNotMix(whskuList,insideCommand,ouId,locationId,putawayPatternDetailType);
////            }
////        }
////        //缓存库存信息
////        List<WhSkuInventoryCommand>  invList =  this.splitPutwayCacheInventory(insideCommand, ouId, logId);
////        // 2.清除所有库存统计信息
//////        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerId.toString());
////        InventoryStatisticResultCommand iSRCmd = inventoryStatisticManager.cacheContainerInventoryStatistics(putawayPatternType,invList,userId, ouId, logId, insideCommand,  putawayPatternDetailType,outerContainerCode);
////        //设置库位信息
////        Set<Long> locationIds = iSRCmd.getLocationIds();
////        if(locationIds == null  || locationIds.size() == 0) {
////           locationIds = new HashSet<Long>();
////          //添加库位
////           locationIds.add(locationId);
////           iSRCmd.setLocationIds(locationIds);
////        }
////        //缓存库存统计信息
////        cacheManager.setMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerId.toString(), iSRCmd, CacheConstants.CACHE_ONE_DAY);
////        // 0.判断是否已经缓存所有库存信息,获取库存统计信息及当前功能参数scan_pattern         CacheConstants.CONTAINER_INVENTORY
////        InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, insideContainerId.toString());
////        if (null == isCmd) {
////            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
////        }
////        // 1.提示商品并判断是否需要扫描属性
////        Map<Long, Map<String, Long>> insideContainerSkuAttrIdsQty = isCmd.getInsideContainerSkuAttrIdsQty();   //内部容器唯一sku总件数
////        Map<Long, Map<Long, Set<String>>> insideContainerLocSkuAttrIds = isCmd.getInsideContainerLocSkuAttrIds();  //内部容器推荐库位对应唯一sku及残次条码
////        Map<Long, Set<String>> insideContainerSkuAttrIds = isCmd.getInsideContainerSkuAttrIds();  // /** 内部容器唯一sku种类 */
////        Map<Long, Set<String>> locSkuAttrIds = insideContainerLocSkuAttrIds.get(insideContainerId);  //库位属性
////        Map<String, Long> skuAttrIdsQty = insideContainerSkuAttrIdsQty.get(insideContainerId);   //内部容器唯一sku总件数'
////        isCmd.getInsideContainerSkuAndSkuAttrIds();
////        String tipSkuAttrId = "";
//////        if(isRecommendFail==true || isNotUser == true) {  //推荐库位失败,或者不使用推荐库位(isRecommendFail=false，时推荐库位成功，isRecommendFail=true时，推荐库位失败,isNotUser=true，不使用推荐库位，isNotUser=false时，使用推荐库位)
////            tipSkuAttrId =  this.sysSuggestSplitContainerPutawayTipSku(invList,insideContainerId, tipLocationId, insideContainerSkuAttrIds, insideContainerCode);
//////            
//////        }else{
//////            tipSkuAttrId = pdaPutawayCacheManager.sysGuideSplitContainerPutawayTipSku0(insideCommand, locationId, locSkuAttrIds, logId);
//////        }
////        Long skuId = SkuCategoryProvider.getSkuId(tipSkuAttrId);
////        WhSkuCommand skuCmd = whSkuDao.findWhSkuByIdExt(skuId, ouId);
////        if (null == skuCmd) {
////            log.error("sku is not found error, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
////        }
////        //tipSkuDetailAspect(isRecommendFail,sRcmd,tipSkuAttrId, locSkuAttrIds, skuAttrIdsQty, logId);
////        sRcmd.setNeedTipSku(true);
////        sRcmd.setTipSkuBarcode(skuCmd.getBarCode());   //提示sku
////        //人工分支缓存扫描的库位
////        this.cacheManTipLocation(insideContainerId, tipLocationId, logId, insideContainerCode);
////        return sRcmd;
////    }
//


//    
//    /***
//     * 比较字符串是否相同
//     * 
//     * @param cSkuInv
//     * @param locSkuInv
//     * @return
//     */
////    private Boolean compaterTo(Object cSkuInvAttr, Object locSkuInvAttr) {
////        Boolean result = false;  // 
////        if (!StringUtils.isEmpty(cSkuInvAttr)) { // 为空的时候返回true
////            if (!cSkuInvAttr.equals(locSkuInvAttr)) {  //要上架的sku商品和库位上已经上架的sku商品关键库存属性不同
////                result = true;
////            }
////        } else {
////            if (!StringUtils.isEmpty(locSkuInvAttr)) {
////                result = true;
////            }
////        }
////        return result;
////    }
//    
//    /***
//     * 统计sku属性数
//     * 
//     * @param locationSkuList
//     * @return
//     */
////    private Long skuAttrCount(List<WhSkuInventoryCommand> locationSkuList) {
////        Integer attrCount = 0; // 库存属性数
////        Set<String> skuAttrsIds = new HashSet<String>();
////        int sizeCount = locationSkuList.size();
////        for(int i=0;i<sizeCount;i++){
////            WhSkuInventoryCommand skuInvCmd = locationSkuList.get(i);
////            String  skuAttrsId = SkuCategoryProvider.getSkuAttrIdByInv(skuInvCmd);
////            skuAttrsIds.add(skuAttrsId);
////        }
////        attrCount = skuAttrsIds.size();
////        return Long.valueOf(attrCount);
////    }
//    
//
//    
//    /***
//     * 整托上架:扫描内部容器
//     * @param insideContainerCode
//     */
////    public void sysSuggestScanInsideContainer(String insideContainerCode,Long ouId){
////        ContainerCommand containerCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
////        if (null == containerCmd) {
////            // 容器信息不存在
////            log.error("container is not exists, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_CONTAINER_IS_NOT_EXISTS);
////        }
////        // 验证容器状态是否可用
////        if (!containerCmd.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
////            log.error("container lifecycle is not normal, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
////        }
////        
////        // 验证容器状态是否是待上架
////        if (!(containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) || containerCmd.getStatus().equals(ContainerStatus.CONTAINER_STATUS_PUTAWAY))) {
////            log.error("container lifecycle is not normal, logId is:[{}]", logId);
////            throw new BusinessException(ErrorCodes.COMMON_CONTAINER__NOT_PUTWAY);
////        }
////    }
////    
//
//
//    
//
//    
//
//    
//
//    
//    /***
//     * 点上架完成时，清楚缓存
//     * @param outerContainerCode
//     * @param insideContainerCode
//     * @param locationCode
//     * @param ouId
//     */
////    public void putawayEndRemoveCache(String outerContainerCode, String insideContainerCode, String locationCode,Long ouId){
////        ContainerCommand outerCmd = null;
////        if(!StringUtils.isEmpty(outerContainerCode)) {
////            outerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
////            if(null == outerCmd){
////                throw new BusinessException(ErrorCodes.PARAMS_ERROR); 
////            }
////        }
////        ContainerCommand insideCmd = null;
////        if(!StringUtils.isEmpty(insideContainerCode)){
////            insideCmd = containerDao.getContainerByCode(insideContainerCode, ouId);
////            if(null == insideCmd){
////                throw new BusinessException(ErrorCodes.PARAMS_ERROR); 
////            }
////        }
////        Location loc = locationDao.findLocationByCode(locationCode, ouId);
////        if (null == loc) {
////            loc = locationDao.getLocationByBarcode(locationCode, ouId);
////            if(null == loc) {
////                log.error("location is null error, locCode is:[{}], logId is:[{}]", locationCode, logId);
////                throw new BusinessException(ErrorCodes.COMMON_LOCATION_IS_NOT_EXISTS);
////            }
////        }
////        Long locationId = loc.getId();
////        this.splitContainerPutawayRemoveAllCache(outerCmd, insideCmd, locationId, locationCode, false, null);
////    }
//    
    /**
     * @author 
     * @param containerCmd
     * @param ouId
     * @param logId
     */
    public void outboundBoxCodeRemoveInventory(String  outboundboxCode, Long ouId, String logId) {
        if (log.isInfoEnabled()) {
            log.info("outboundBox remove inventory start, outboundboxCode is:[{}], ouId is:[{}], logId is:[{}]", outboundboxCode, ouId, logId);
        }
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, outboundboxCode);
        if (log.isInfoEnabled()) {
            log.info("outboundBox remove inventory end, outboundboxCode is:[{}], ouId is:[{}], logId is:[{}]", outboundboxCode, ouId, logId);
        }
    }
    
    private boolean isCacheAllExists2(Set<String> ids, ArrayDeque<String> cacheKeys) {
        boolean allExists = true;
        if (null != cacheKeys && !cacheKeys.isEmpty()) {
            for (String id : ids) {
                String cId = id;
                boolean isExists = false;
                Iterator<String> iter = cacheKeys.iterator();
                while (iter.hasNext()) {
                    String value = iter.next();
                    if (null == value) value = "-1";
                    if (value.equals(cId)) {
                        isExists = true;
                        break;
                    }
                }
                if (false == isExists) {
                    allExists = false;
                }
            }
        } else {
            allExists = false;
        }
        return allExists;
    }
    
    /**
     * 缓存提示的SKU
     * @param insideContainerId
     * @param locationId
     * @param skuAttrIds
     */
    private void cacheScanSku(String insideContainerCode,String skuAttrIds) {
        log.info("PdaSysSuggestPutwayManagerImpl cacheScanSku is start"); 
        TipScanSkuCacheCommand cacheSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + insideContainerCode);
        if(null == cacheSkuCmd) {
            cacheSkuCmd  = new TipScanSkuCacheCommand();
            ArrayDeque<String> scanSkuAttrIds = new ArrayDeque<String>();
            scanSkuAttrIds.add(skuAttrIds);
            cacheSkuCmd.setScanSkuAttrIds(scanSkuAttrIds);
        }else{
            ArrayDeque<String> scanSkuAttrIds = cacheSkuCmd.getScanSkuAttrIds();
            if(null == scanSkuAttrIds){
                scanSkuAttrIds = new ArrayDeque<String>();
            }
            if(!scanSkuAttrIds.contains(skuAttrIds)) {
                scanSkuAttrIds.addFirst(skuAttrIds);
            }
            cacheSkuCmd.setScanSkuAttrIds(scanSkuAttrIds);
        }
        
        cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + insideContainerCode, cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
        log.info("PdaSysSuggestPutwayManagerImpl cacheScanSku is end"); 
    }
    
    private void splitContainerPutawayRemoveAllCache(ContainerCommand containerCmd, ContainerCommand insideContainerCmd, Long locationId, String logId,Boolean isPutawayEnd,Long sId){
        Long icId = insideContainerCmd.getId();
          if(isPutawayEnd == true) {  //人工上架,整箱或整托还没结束
              cacheManager.remove(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + icId.toString() + locationId.toString()+sId.toString());
              cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + icId.toString() + sId.toString());
          }else{
              if(null != containerCmd) {
                  Long ocId = containerCmd.getId();
                  // 0.先清除所有复核商品队列及库位队列及内部库存及统计信息
                  InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                  if (null == isCmd) {
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR); 
                  }
                  Map<Long,Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
                  Set<Long> skuIds = insideContainerSkuIds.get(icId);
                     for(Long skuId:skuIds){
                         cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString()+skuId.toString());
                   }
                   if(null != sId){
                       cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE_SN + icId.toString() + sId.toString());
                       cacheManager.remove(CacheConstants.SUGGEST_SCAN_SKU_QUEUE_SN + icId.toString() + locationId.toString()+sId.toString());
                   }
                   cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString()+locationId.toString());
                   cacheManager.remove(CacheConstants.SCAN_CONTAINER_QUEUE+ocId.toString());
                   cacheManager.removeMapValue(CacheConstants.CONTAINER_STATISTIC , ocId.toString());
                   cacheManager.remove(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
              }else{
                  InventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                  if (null == isCmd) {
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR); 
                  }
                  Map<Long,Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerSkuIds();
                  Set<Long> skuIds = insideContainerSkuIds.get(icId);
                     for(Long skuId:skuIds){
                         cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString()+skuId.toString());
                   }
                   cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString()+locationId.toString());
                   cacheManager.remove(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString()+locationId.toString());
                   cacheManager.remove(CacheConstants.SCAN_LOCATION_QUEUE + icId.toString());
              }
               // 1.清除所有库存统计信息
               cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
               // 2.清除所有库存缓存信息
               cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
          }
             
    }
    private void boxMoveRemoveAllCache(String containerCode){
    	  // 0.先清除所有商品队列统计信息
    	BoxInventoryStatisticResultCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCode);
    	  if (null == isCmd) {
    		  throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR); 
    	  }
    	  Map<String,Set<Long>> insideContainerSkuIds = isCmd.getOutBoundBoxSkuIds();
    	  Set<Long> skuIds = insideContainerSkuIds.get(containerCode);
    	  for(Long skuId:skuIds){
    		  cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + containerCode+skuId.toString());
    	  }
    	  
          // 1.清除所有库存统计信息
          cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, containerCode);
          // 2.清除所有库存缓存信息
          cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, containerCode);
    }
             
}
