package com.baozun.scm.primservice.whoperation.manager.pda.inbound.cache;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.redis.manager.CacheManager;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.CheckScanSkuResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ContainerStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.InventoryStatisticResultCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.ManMadeContainerStatisticCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipContainerCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.inbound.putaway.TipScanSkuCacheCommand;
import com.baozun.scm.primservice.whoperation.command.pda.putaway.PdaManMadePutawayCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CacheConstants;
import com.baozun.scm.primservice.whoperation.constant.CacheKeyConstant;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.WhPutawayPatternDetailType;
import com.baozun.scm.primservice.whoperation.constant.WhScanPatternType;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

/**
 * 
 * @author 
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
    @Autowired
    private WhCartonDao whCartonDao;
    @Autowired
    private UomDao uomDao;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private WhSkuDao whSkuDao;

    
    
    
    /**
     * 外部容器库存:提示货箱容器号
     * 
     * @param containerCmd
     * @param insideContainerIds
     * @param logId
     * @return
     */
    @Override
    public Long outContainerInvPutawayTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, String logId) {
        // TODO Auto-generated method stub
        log.info("pdaManmadePutawayCacheManager outContainerInvPutawayTipContainer is start");
        Long containerId = containerCmd.getId();
        Long tipContainerId = null;
        TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
        ArrayDeque<Long> cacheContainerIds = null;
        if (null != cacheContainerCmd) {
            cacheContainerIds = cacheContainerCmd.getTipInsideContainerIds();
        }
        if (null != cacheContainerIds && !cacheContainerIds.isEmpty()) {
            // 随机取一个容器
            for (Long ic : insideContainerIds) {
                Long icId = ic;
                if (null != icId) {
                    boolean isExists = false;
                    Iterator<Long> iter = cacheContainerIds.iterator();
                    while (iter.hasNext()) {
                        Long value = iter.next();
                        if (null == value) value = -1L;
                        if (0 == value.compareTo(icId)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (false == isExists) {
                        tipContainerId = icId;
                        cacheContainerIds.addFirst(tipContainerId);
                        cacheContainerCmd.setTipInsideContainerIds(cacheContainerIds);
                        cacheManager.setObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), cacheContainerCmd, CacheConstants.CACHE_ONE_DAY);
                        break;
                    }
                }
            }
        } else {
            log.error("tip container is exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        log.info("pdaManmadePutawayCacheManager outContainerInvPutawayTipContainer is end");
        return tipContainerId;
    }
    
    
    /**
     * 缓存容器内部的信息
     * 
     * @param pdaManMadePutawayCommand
     * @return
     */
    @Override
    public ManMadeContainerStatisticCommand manMadePutawayCacheContainer(PdaManMadePutawayCommand manMadePutawayCommand,Long containerId) {
        if (log.isInfoEnabled()) {
            log.info("manMadePutawayCacheContainer param (PdaManMadePutawayCommand  is:[{}]",  manMadePutawayCommand);
        }
        ManMadeContainerStatisticCommand manMadeContainer = cacheManager.getMapObject(CacheKeyConstant.MANMADE_PUTWAY_CACHE_CONTAINER,containerId.toString());
        if(null == manMadeContainer) {
            List<String> containerList = new ArrayList<String>();
            if(manMadePutawayCommand.getIsOuterContainer()) {   //外部容器库存
                containerList.add(manMadePutawayCommand.getContainerCode());   
            }else{
                containerList.add(manMadePutawayCommand.getInsideContainerCode());   //内部容器库存
            }
            List<WhSkuInventoryCommand> list = whSkuInventoryDao.findWhSkuInventoryByOuterContainerCode(manMadePutawayCommand.getOuId(), containerList);
            manMadeContainer = this.containerCacheStatistic(manMadePutawayCommand, list,containerId);
            cacheManager.setMapObject(CacheKeyConstant.MANMADE_PUTWAY_CACHE_CONTAINER, containerId.toString(), manMadeContainer, CacheKeyConstant.CACHE_ONE_DAY);
        }
        return manMadeContainer;
    }


    /**
     * 缓存容器内上架sku信息
     * @param containerId
     * @param ouId
     * @return
     */
    @Override
    public List<WhSkuInventory> manMadePutwayCacheSkuInventory(Long containerId, Long ouId, int putawayPatternDetailType) {
        // TODO Auto-generated method stub
        List<WhSkuInventory> whskuList = cacheManager.getMapObject(CacheKeyConstant.MAN_MADE_PUTWAY_CHCHE_SKU_INVENTORY,containerId.toString());
        int length = whskuList.size();
        if(null == whskuList || length == 0) {
            // 验证是否外部容器
            if (putawayPatternDetailType == WhPutawayPatternDetailType.PALLET_PUTAWAY) {
                whskuList = whSkuInventoryDao.findWhSkuInventoryByCId(ouId, null, containerId);  //整托
            } else{
                whskuList = whSkuInventoryDao.findWhSkuInventoryByCId(ouId, containerId, null);  //整箱
                if(null == whskuList || length == 0) {
                    whskuList = whSkuInventoryDao.findWhSkuInventoryByCId(ouId, null, containerId);  //整箱
                }
            }
            if(null == whskuList || whskuList.size() == 0) {
                throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_SKU_AMOUNT_ERROR);
            }
            cacheManager.setMapObject(CacheKeyConstant.MAN_MADE_PUTWAY_CHCHE_SKU_INVENTORY,containerId.toString(),whskuList,CacheKeyConstant.CACHE_ONE_DAY);
        }
        return whskuList;
    }


    /***
     * 统计容器信息
     * @param manMadePutawayCommand
     */
    private ManMadeContainerStatisticCommand containerCacheStatistic(PdaManMadePutawayCommand manMadePutawayCommand,List<WhSkuInventoryCommand> list,Long containerId) {
      log.info("PdaManMadePutawayManagerImpl containerCacheStatistic is start"); 
      /** 所有内部容器 */
      Set<Long> insideContainerIds = new HashSet<Long>();
      /** 所有caselevel内部容器 */
      Set<Long> caselevelContainerIds = new HashSet<Long>();
      /** 所有非caselevel内部容器 */
      Set<Long> notcaselevelContainerIds = new HashSet<Long>();
      /**内部容器重量(容器+sku的重量)*/
      Map<Long, Double> insideContainersWeight = new HashMap<Long, Double>();
      /** 内部容器对应sku,id集合 */
      Map<Long,Set<Long>> insideContainerIdSkuIds = new HashMap<Long,Set<Long>>();
      Map<String, Double> lenUomConversionRate = new HashMap<String, Double>();   //长度，度量单位转换率
      Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();  //重量，度量单位转换率
      SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(weightUomConversionRate);
      List<UomCommand> lenUomCmds = null;   //长度度量单位
      List<UomCommand> weightUomCmds = null;   //重量度量单位
      Long ouId = manMadePutawayCommand.getOuId();
      ManMadeContainerStatisticCommand manMadeContainer = cacheManager.getMapObject(CacheKeyConstant.MANMADE_PUTWAY_CACHE_CONTAINER,containerId.toString());
      if(null == manMadeContainer) {
          manMadeContainer = new ManMadeContainerStatisticCommand();
          lenUomCmds = uomDao.findUomByGroupCode(WhUomType.LENGTH_UOM, BaseModel.LIFECYCLE_NORMAL);
          for (UomCommand lenUom : lenUomCmds) {
              String uomCode = "";
              Double uomRate = 0.0;
              if (null != lenUom) {
                  uomCode = lenUom.getUomCode();
                  uomRate = lenUom.getConversionRate();
                  lenUomConversionRate.put(uomCode, uomRate);
              }
          }
          weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL);
          for (UomCommand lenUom : weightUomCmds) {
              String uomCode = "";
              Double uomRate = 0.0;
              if (null != lenUom) {
                  uomCode = lenUom.getUomCode();
                  uomRate = lenUom.getConversionRate();
                  weightUomConversionRate.put(uomCode, uomRate);
              }
          }
          manMadeContainer.setLenUomConversionRate(lenUomConversionRate);
          manMadeContainer.setWeightUomConversionRate(weightUomConversionRate);
          if(manMadePutawayCommand.getIsOuterContainer())  {  //外部容器
              if (null == list || 0 == list.size()) {
                  log.error("manMadePutawayCacheSku  inventory not found error!, logId is:[{}]", logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_INSIDE_CONTAINER_ID, new Object[] {containerId});
              }
              manMadeContainer.setOuterContainerCode(manMadePutawayCommand.getContainerCode());  //外部容器号
              manMadeContainer.setOuterContainerId(manMadePutawayCommand.getContainerId());
              //所有内部容器:caselevel容器和非caselevel容器
              for(WhSkuInventoryCommand command:list) {
                  Long iscId = command.getInsideContainerId();
                  WhCarton carton = whCartonDao.findWhCaselevelCartonById(iscId, ouId);
                  if (null != carton) {
                      caselevelContainerIds.add(iscId);  //统计caselevel内部容器信息
                  } else {
                      notcaselevelContainerIds.add(iscId);   //统计非caselevel内部容器信息
                  }
              }
              //计算托盘和货箱重量
              for(WhSkuInventoryCommand command:list) {
                  Long icId = command.getInsideContainerId();
                  Container ic = null;
                  if (null == icId || null == (ic = containerDao.findByIdExt(icId, ouId))) {
                      log.error("sys guide pallet putaway inside container is not found, icId is:[{}], logId is:[{}]", icId, logId);
                      throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
                  } else {
                      insideContainerIds.add(icId);   //统计所有内部容器
                  }
                  // 验证容器状态是否可用
                  if (!BaseModel.LIFECYCLE_NORMAL.equals(ic.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != ic.getLifecycle()) {
                      log.error("sys guide pallet putaway inside container lifecycle is not normal, icId is:[{}], logId is:[{}]", icId, logId);
                      throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                  }
                  // 获取容器状态
                  Integer icStatus = ic.getStatus();
                  if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != icStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != icStatus) {
                      log.error("sys guide pallet putaway inside container status is invalid, icId is:[{}], containerStatus is:[{}], logId is:[{}]", icId, icStatus, logId);
                      throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {ic.getCode()});
                  }
                  Long insideContainerCate = ic.getTwoLevelType();   
                  Container2ndCategory insideContainer2 = container2ndCategoryDao.findByIdExt(insideContainerCate, ouId);
                  if (null == insideContainer2) {
                      log.error("sys guide pallet putaway container2ndCategory is null error, icId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", icId, insideContainerCate, logId);
                      throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
                  }
                  if (1 != insideContainer2.getLifecycle()) {
                      log.error("sys guide pallet putaway container2ndCategory lifecycle is not normal error, icId is:[{}], containerId is:[{}], logId is:[{}]", icId, insideContainer2.getId(), logId);
                      throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
                  }
                  Double icLength = insideContainer2.getLength();
                  Double icWidth = insideContainer2.getWidth();
                  Double icHeight = insideContainer2.getHigh();
                  Double icWeight = insideContainer2.getWeight();
                  if (null == icLength || null == icWidth || null == icHeight) {
                      log.error("sys guide pallet putaway inside container length、width、height is null error, icId is:[{}], logId is:[{}]", icId, logId);
                      throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
                  }
                  if (null == icWeight) {
                      log.error("sys guide pallet putaway inside container weight is null error, icId is:[{}], logId is:[{}]", icId, logId);
                      throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
                  }
                  Double toBefillQty = command.getToBeFilledQty();   //待移入库存 
                  Double onHandQty = command.getOnHandQty();   //在库库存
                  Double curerntSkuQty = 0.0;     //当前sku数量
                  Long locationId = command.getLocationId();
                  if (null != locationId) {
                      if (null != toBefillQty) {
                          curerntSkuQty = toBefillQty;
                      }
                  } else {
                      if (null == onHandQty || 0 <= new Double("0.0").compareTo(onHandQty)) {
                          log.error("rcvd inv onHandQty is less than 0 error, logId is:[{}]", logId);
                          throw new BusinessException(ErrorCodes.RCVD_INV_SKU_QTY_ERROR);
                      }
                      if (null != onHandQty) {
                          curerntSkuQty = onHandQty;
                      }
                  }
                  WhSkuCommand skuCmd =  whSkuDao.findWhSkuByIdExt(command.getSkuId(), ouId);
                  if (null == skuCmd) {
                          log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", command.getId(), logId);
                          throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                      }
                      Double skuLength = skuCmd.getLength();
                      Double skuWidth = skuCmd.getWidth();
                      Double skuHeight = skuCmd.getHeight();
                      Double skuWeight = skuCmd.getWeight();
                      if (null == skuLength || null == skuWidth || null == skuHeight) {
                          log.error("sys guide pallet putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", command.getId(), logId);
                          throw new BusinessException(ErrorCodes.SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
                      }
                      if (null == skuWeight) {
                          log.error("sys guide pallet putaway sku weight is null error, skuId is:[{}], logId is:[{}]", command.getId(), logId);
                          throw new BusinessException(ErrorCodes.SKU_WEIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
                      }
                      if (null != insideContainersWeight.get(icId)) {
                          insideContainersWeight.put(icId, insideContainersWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                      } else {
                          // 先计算容器自重
                          insideContainersWeight.put(icId, weightCalculator.calculateStuffWeight(icWeight));
                          // 再计算当前商品重量
                          insideContainersWeight.put(icId, insideContainersWeight.get(icId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                      }
              }
              //内部容器对应sku,id集合
              for(Long insideCId :insideContainerIds) {
                  Set<Long> skuIds = new HashSet<Long>();
                  for(WhSkuInventoryCommand skuInvCommand:list) {
                      Long skuId = skuInvCommand.getSkuId();
                      if(insideCId == skuInvCommand.getInsideContainerId()) {
                          skuIds.add(skuId);
                      }
                  }
                  insideContainerIdSkuIds.put(insideCId, skuIds);
              }
              manMadeContainer.setCaselevelContainerIds(caselevelContainerIds);
              manMadeContainer.setNotcaselevelContainerIds(notcaselevelContainerIds);
              manMadeContainer.setInsideContainerIds(insideContainerIds);
              manMadeContainer.setInsideContainerIdSkuIds(insideContainerIdSkuIds);
              manMadeContainer.setInsideContainersWeight(insideContainersWeight);
          }
          if(!manMadePutawayCommand.getIsOuterContainer()) {  //内部容器
              Container ic = null;
              if (null == containerId || null == (ic = containerDao.findByIdExt(containerId, ouId))) {
                  log.error("sys guide pallet putaway inside container is not found, icId is:[{}], logId is:[{}]", containerId, logId);
                  throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_IS_NOT_EXISTS);
              } else {
                  insideContainerIds.add(containerId);   //统计所有内部容器
              }
              // 验证容器状态是否可用
              if (!BaseModel.LIFECYCLE_NORMAL.equals(ic.getLifecycle()) && ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED != ic.getLifecycle()) {
                  log.error("sys guide pallet putaway inside container lifecycle is not normal, icId is:[{}], logId is:[{}]", containerId, logId);
                  throw new BusinessException(ErrorCodes.COMMON_INSIDE_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
              }
              // 获取容器状态
              Integer icStatus = ic.getStatus();
              if (ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY != icStatus && ContainerStatus.CONTAINER_STATUS_PUTAWAY != icStatus) {
                  log.error("sys guide pallet putaway inside container status is invalid, icId is:[{}], containerStatus is:[{}], logId is:[{}]", containerId, icStatus, logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_STATUS_ERROR_UNABLE_PUTAWAY, new Object[] {ic.getCode()});
              }
              Long insideContainerCate = ic.getTwoLevelType();   
              Container2ndCategory insideContainer2 = container2ndCategoryDao.findByIdExt(insideContainerCate, ouId);
              if (null == insideContainer2) {
                  log.error("sys guide pallet putaway container2ndCategory is null error, icId is:[{}], 2endCategoryId is:[{}], logId is:[{}]", containerId, insideContainerCate, logId);
                  throw new BusinessException(ErrorCodes.CONTAINER2NDCATEGORY_NULL_ERROR);
              }
              if (1 != insideContainer2.getLifecycle()) {
                  log.error("sys guide pallet putaway container2ndCategory lifecycle is not normal error, icId is:[{}], containerId is:[{}], logId is:[{}]", containerId, insideContainer2.getId(), logId);
                  throw new BusinessException(ErrorCodes.COMMON_CONTAINER_LIFECYCLE_IS_NOT_NORMAL);
              }
              Double icLength = insideContainer2.getLength();
              Double icWidth = insideContainer2.getWidth();
              Double icHeight = insideContainer2.getHigh();
              Double icWeight = insideContainer2.getWeight();
              if (null == icLength || null == icWidth || null == icHeight) {
                  log.error("sys guide pallet putaway inside container length、width、height is null error, icId is:[{}], logId is:[{}]", containerId, logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
              }
              if (null == icWeight) {
                  log.error("sys guide pallet putaway inside container weight is null error, icId is:[{}], logId is:[{}]", containerId, logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_WEIGHT_IS_NULL_ERROR, new Object[] {ic.getCode()});
              }
              if (null == list || 0 == list.size()) {
                  log.error("manMadePutawayCacheSku  inventory not found error!, logId is:[{}]", logId);
                  throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_INSIDE_CONTAINER_ID, new Object[] {containerId});
              }
              //内部容器对应sku,id集合
              Set<Long> skuIds = new HashSet<Long>();
              for(WhSkuInventoryCommand skuInvCommand:list) {
                   Long skuId = skuInvCommand.getSkuId();
                   if(containerId == skuInvCommand.getInsideContainerId()) {
                       skuIds.add(skuId);
                   }else{
                       throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_INSIDE_CONTAINER_ID, new Object[] {containerId}); 
                   }
               }
              insideContainerIdSkuIds.put(containerId , skuIds);
              
            //计算托盘和货箱重量
              for(WhSkuInventoryCommand command:list) {
                  Double toBefillQty = command.getToBeFilledQty();   //待移入库存 
                  Double onHandQty = command.getOnHandQty();   //在库库存
                  Double curerntSkuQty = 0.0;     //当前sku数量
                  Long locationId = command.getLocationId();
                  if (null != locationId) {
                      if (null != toBefillQty) {
                          curerntSkuQty = toBefillQty;
                      }
                  } else {
                      if (null == onHandQty || 0 <= new Double("0.0").compareTo(onHandQty)) {
                          log.error("rcvd inv onHandQty is less than 0 error, logId is:[{}]", logId);
                          throw new BusinessException(ErrorCodes.RCVD_INV_SKU_QTY_ERROR);
                      }
                      if (null != onHandQty) {
                          curerntSkuQty = onHandQty;
                      }
                  }
                  WhSkuCommand skuCmd =  whSkuDao.findWhSkuByIdExt(command.getId(), ouId);
                  if (null == skuCmd) {
                          log.error("sys guide pallet putaway sku is not exists error, skuId is:[{}], logId is:[{}]", command.getId(), logId);
                          throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                      }
                      Double skuLength = skuCmd.getLength();
                      Double skuWidth = skuCmd.getWidth();
                      Double skuHeight = skuCmd.getHeight();
                      Double skuWeight = skuCmd.getWeight();
                      if (null == skuLength || null == skuWidth || null == skuHeight) {
                          log.error("sys guide pallet putaway sku length、width、height is null error, skuId is:[{}], logId is:[{}]", command.getId(), logId);
                          throw new BusinessException(ErrorCodes.SKU_LENGTH_WIDTH_HIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
                      }
                      if (null == skuWeight) {
                          log.error("sys guide pallet putaway sku weight is null error, skuId is:[{}], logId is:[{}]", command.getId(), logId);
                          throw new BusinessException(ErrorCodes.SKU_WEIGHT_IS_NULL_ERROR, new Object[] {skuCmd.getBarCode()});
                      }
                      if (null != insideContainersWeight.get(containerId)) {
                          insideContainersWeight.put(containerId, insideContainersWeight.get(containerId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                      } else {
                          // 先计算容器自重
                          insideContainersWeight.put(containerId, weightCalculator.calculateStuffWeight(icWeight));
                          // 再计算当前商品重量
                          insideContainersWeight.put(containerId, insideContainersWeight.get(containerId) + (weightCalculator.calculateStuffWeight(skuWeight) * curerntSkuQty));
                      }
                 }
                manMadeContainer.setInsideContainerCode(manMadePutawayCommand.getContainerCode());
                manMadeContainer.setInsideContainerId(containerId);
                manMadeContainer.setInsideContainerIdSkuIds(insideContainerIdSkuIds);
                manMadeContainer.setInsideContainersWeight(insideContainersWeight);
            }
      }
      log.info("PdaManMadePutawayManagerImpl containerCacheStatistic is end"); 
      return manMadeContainer;
    }
    
    @Override
    public CheckScanSkuResultCommand manMadePalletPutawayCacheSkuOrTipContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds, Map<Long, Map<Long, Long>> insideContainerSkuIdsQty,
            WhSkuCommand skuCmd, Integer scanPattern, String logId) {
        CheckScanSkuResultCommand cssrCmd = new CheckScanSkuResultCommand();
        Long ocId = ocCmd.getId();
        Long icId = icCmd.getId();
        // 0.先判断当前内部容器是否在缓存中
        boolean icExists = false;
        for (Long iId : insideContainerIds) {
            if (0 == icId.compareTo(iId)) {
                icExists = true;
            }
        }
        if (false == icExists) {
            log.error("tip container is not in cache server error, logId is[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        // 1.当前的内部容器是不是提示容器队列的第一个
        TipContainerCacheCommand tipContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
        if (null == tipContainerCmd) {
            log.error("scan container queue is exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        } else {
            ArrayDeque<Long> icIds = tipContainerCmd.getTipInsideContainerIds();
            if (null == icIds || icIds.isEmpty()) {
                log.error("scan container queue is exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
            } else {
                Long firstId = icIds.peekFirst();
                if (0 != icId.compareTo(firstId)) {
                    log.error("tip container is not queue first element exception, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                }
            }
        }
        // 2.得到当前内部容器的所有商品并复核商品
        Long skuId = skuCmd.getId();
        Double skuQty = skuCmd.getScanSkuQty();
        Set<Long> icSkusIds = insideContainerSkuIds.get(icId);
        ArrayDeque<Long> scanIcIds = tipContainerCmd.getTipInsideContainerIds();// 取到已扫描容器队列
        boolean skuExists = false;
        for (Long sId : icSkusIds) {
            if (0 == skuId.compareTo(sId)) {
                skuExists = true;
                Map<Long, Long> icSkuAndQty = insideContainerSkuIdsQty.get(icId);
                Long icSkuQty = icSkuAndQty.get(skuId);
                if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {
                    TipScanSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                    ArrayDeque<Long> oneByOneScanSkuIds = null;
                    if (null != tipScanSkuCmd) {
                        oneByOneScanSkuIds = tipScanSkuCmd.getOneByOneScanSkuIds();
                    }
                    if (null != oneByOneScanSkuIds && !oneByOneScanSkuIds.isEmpty()) {
                        boolean isExists = false;
                        Iterator<Long> iter = oneByOneScanSkuIds.iterator();
                        while (iter.hasNext()) {
                            Long value = iter.next();
                            if (null == value) value = -1L;
                            if (0 == skuId.compareTo(new Long(value))) {
                                isExists = true;
                                break;
                            }
                        }
                        long value = 0L;
                        if (false == isExists) {
                            oneByOneScanSkuIds.addFirst(skuId);// 先加入逐件扫描的队列
                            tipScanSkuCmd.setOneByOneScanSkuIds(oneByOneScanSkuIds);
                        } else {
                            // 取到扫描的数量
                            String cacheValue = cacheManager.getValue(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString());
                            if (!StringUtils.isEmpty(cacheValue)) {
                                value = new Long(cacheValue).longValue();
                            }
                        }
                        if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                            log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                        }
                        long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString(), skuQty.intValue());
                        if (cacheValue == icSkuQty.longValue()) {
                            ArrayDeque<Long> cacheSkuIds = tipScanSkuCmd.getScanSkuIds();
                            if (null == cacheSkuIds || cacheSkuIds.isEmpty()) {
                                cacheSkuIds = new ArrayDeque<Long>();
                            }
                            cacheSkuIds.addFirst(skuId);
                            tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                            cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                            if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                // 全部商品已复核完毕
                                if (isCacheAllExists(insideContainerIds, scanIcIds)) {
                                    // 全部容器已复核完毕
                                    cssrCmd.setPutaway(true);// 可上架
                                } else {
                                    // 提示下一个容器
                                    Long tipContainerId = sysGuidePalletPutawayCacheTipContainer(ocCmd, insideContainerIds, insideContainerSkuIds, logId);
                                    cssrCmd.setNeedTipContainer(true);
                                    cssrCmd.setTipContainerId(tipContainerId);
                                }
                            } else {
                                // 继续复核
                                cssrCmd.setNeedScanSku(true);
                            }
                            break;
                        } else if (cacheValue < icSkuQty.longValue()) {
                            // 继续复核
                            cssrCmd.setNeedScanSku(true);
                            break;
                        } else {
                            log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                        }
                    } else {
                        // 不考虑功能参数复合过程中改变的情况
                        TipScanSkuCacheCommand cacheSkuCmd = new TipScanSkuCacheCommand();
                        cacheSkuCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                        cacheSkuCmd.setOuterContainerId(ocCmd.getId());
                        cacheSkuCmd.setOuterContainerCode(ocCmd.getCode());
                        cacheSkuCmd.setInsideContainerId(icCmd.getId());
                        cacheSkuCmd.setInsideContainerCode(icCmd.getCode());
                        ArrayDeque<Long> oneByOneCacheSkuIds = new ArrayDeque<Long>();
                        oneByOneCacheSkuIds.addFirst(skuId);
                        cacheSkuCmd.setOneByOneScanSkuIds(oneByOneCacheSkuIds);
                        long value = 0L;
                        if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                            log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                        }
                        long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString(), skuQty.intValue());
                        if (cacheValue == icSkuQty.longValue()) {
                            ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                            cacheSkuIds.addFirst(skuId);
                            cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                            cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                            if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                // 全部商品已复核完毕
                                if (isCacheAllExists(insideContainerIds, scanIcIds)) {
                                    // 全部容器已复核完毕
                                    cssrCmd.setPutaway(true);// 可上架
                                } else {
                                    // 提示下一个容器
                                    Long tipContainerId = sysGuidePalletPutawayCacheTipContainer(ocCmd, insideContainerIds, insideContainerSkuIds, logId);
                                    cssrCmd.setNeedTipContainer(true);
                                    cssrCmd.setTipContainerId(tipContainerId);
                                }
                            } else {
                                // 继续复核
                                cssrCmd.setNeedScanSku(true);
                            }
                            break;
                        } else if (cacheValue < icSkuQty.longValue()) {
                            // 继续复核
                            cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                            cssrCmd.setNeedScanSku(true);
                            break;
                        } else {
                            log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                            throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                        }
                    }
                } else {
                    if (skuQty.longValue() == icSkuQty.longValue()) {
                        TipScanSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                        ArrayDeque<Long> scanSkuIds = null;
                        if (null != tipScanSkuCmd) {
                            scanSkuIds = tipScanSkuCmd.getScanSkuIds();// 取到已扫描商品队列
                        }
                        if (null != scanSkuIds && !scanSkuIds.isEmpty()) {
                            boolean isExists = false;
                            Iterator<Long> iter = scanSkuIds.iterator();
                            while (iter.hasNext()) {
                                Long value = iter.next();
                                if (null == value) value = -1L;
                                if (0 == skuId.compareTo(new Long(value))) {
                                    isExists = true;
                                    break;
                                }
                            }
                            if (false == isExists) {
                                scanSkuIds.addFirst(skuId);// 加入队列
                                tipScanSkuCmd.setScanSkuIds(scanSkuIds);
                                cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                if (isCacheAllExists(icSkusIds, scanSkuIds)) {
                                    // 全部商品已复核完毕
                                    if (isCacheAllExists(insideContainerIds, scanIcIds)) {
                                        // 全部容器已复核完毕
                                        cssrCmd.setPutaway(true);// 可上架
                                    } else {
                                        // 提示下一个容器
                                        Long tipContainerId = sysGuidePalletPutawayCacheTipContainer(ocCmd, insideContainerIds, insideContainerSkuIds, logId);
                                        cssrCmd.setNeedTipContainer(true);
                                        cssrCmd.setTipContainerId(tipContainerId);
                                    }
                                } else {
                                    // 继续复核
                                    cssrCmd.setNeedScanSku(true);
                                }
                                break;
                            } else {
                                log.error("scan sku has already checked, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                                throw new BusinessException(ErrorCodes.CONTAINER_SKU_HAS_ALREADY_SCANNED, new Object[] {icCmd.getCode()});
                            }
                        } else {
                            TipScanSkuCacheCommand cacheSkuCmd = new TipScanSkuCacheCommand();
                            cacheSkuCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                            cacheSkuCmd.setOuterContainerId(ocCmd.getId());
                            cacheSkuCmd.setOuterContainerCode(ocCmd.getCode());
                            cacheSkuCmd.setInsideContainerId(icCmd.getId());
                            cacheSkuCmd.setInsideContainerCode(icCmd.getCode());
                            ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                            cacheSkuIds.addFirst(skuId);
                            cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                            cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                            if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                // 全部商品已复核完毕
                                if (isCacheAllExists(insideContainerIds, scanIcIds)) {
                                    // 全部容器已复核完毕
                                    cssrCmd.setPutaway(true);// 可上架
                                } else {
                                    // 提示下一个容器
                                    Long tipContainerId = sysGuidePalletPutawayCacheTipContainer(ocCmd, insideContainerIds, insideContainerSkuIds, logId);
                                    cssrCmd.setNeedTipContainer(true);
                                    cssrCmd.setTipContainerId(tipContainerId);
                                }
                            } else {
                                // 继续复核
                                cssrCmd.setNeedScanSku(true);
                            }
                            break;
                        }
                    } else {
                        log.error("scan sku qty is not equal with rcvd inv qty, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                        throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_NOT_EQUAL_SCAN_SKU_QTY_ERROR, new Object[] {icCmd.getCode()});
                    }
                }
            }
        }
        if (false == skuExists) {
            log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
            throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
        }
        return cssrCmd;
    }
    
    
    private Long sysGuidePalletPutawayCacheTipContainer(ContainerCommand containerCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds, String logId) {
        Long containerId = containerCmd.getId();
        Long tipContainerId = null;
        TipContainerCacheCommand tipContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString());
        ArrayDeque<Long> cacheContainerIds = null;
        if (null != tipContainerCmd) {
            cacheContainerIds = tipContainerCmd.getTipInsideContainerIds();// 取到已扫描内部容器
        }
        if (null != cacheContainerIds && !cacheContainerIds.isEmpty()) {
            for (Long id : insideContainerIds) {
                if (null != id) {
                    Long icId = id;
                    boolean isExists = false;
                    Iterator<Long> iter = cacheContainerIds.iterator();
                    while (iter.hasNext()) {
                        Long value = iter.next();
                        if (null == value) value = -1L;
                        if (0 == value.compareTo(icId)) {
                            isExists = true;
                            TipScanSkuCacheCommand cacheSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                            ArrayDeque<Long> cacheSkuIds = null;
                            if (null != cacheSkuCmd) {
                                cacheSkuIds = cacheSkuCmd.getScanSkuIds();
                            }
                            if (null != cacheSkuIds && !cacheSkuIds.isEmpty()) {
                                Set<Long> icSkus = insideContainerSkuIds.get(icId);
                                if (isCacheAllExists(icSkus, cacheSkuIds)) {
                                    continue;
                                } else {
                                    tipContainerId = id;
                                    break;
                                }
                            } else {
                                tipContainerId = id;
                                break;
                            }

                        }
                    }
                    if (false == isExists) {
                        tipContainerId = id;
                        cacheContainerIds.addFirst(tipContainerId);
                        tipContainerCmd.setTipInsideContainerIds(cacheContainerIds);
                        cacheManager.setObject(CacheConstants.SCAN_CONTAINER_QUEUE + containerId.toString(), tipContainerCmd, CacheConstants.CACHE_ONE_DAY);
                        break;
                    } else {
                        if (null != tipContainerId) {
                            break;
                        }
                    }

                }
            }
        } else {
            log.error("tip container is exception, logId is:[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        return tipContainerId;
    }
    
    private boolean isCacheAllExists(Set<Long> ids, String cacheKey) {
        boolean allExists = true;
        long len = cacheManager.listLen(cacheKey);
        if (0 < len) {
            for (Long id : ids) {
                Long cId = id;
                boolean isExists = false;
                for (int i = 0; i < new Long(len).intValue(); i++) {
                    String value = cacheManager.findListItem(cacheKey, i);
                    if (null == value) value = "-1";
                    if (0 == new Long(value).compareTo(cId)) {
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

    
    private boolean isCacheAllExists(Set<Long> ids, ArrayDeque<Long> cacheKeys) {
        boolean allExists = true;
        if (null != cacheKeys && !cacheKeys.isEmpty()) {
            for (Long id : ids) {
                Long cId = id;
                boolean isExists = false;
                Iterator<Long> iter = cacheKeys.iterator();
                while (iter.hasNext()) {
                    Long value = iter.next();
                    if (null == value) value = -1L;
                    if (0 == value.compareTo(cId)) {
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
    
    
    /**人工上架:整托上架清楚缓存
     * @param containerCmd
     * @param ouId
     * @param logId
     */
    @Override
    public void manMadePalletPutawayRemoveAllCache(ContainerCommand containerCmd, String logId) {
        Long ocId = containerCmd.getId();
        // 0.先清除所有复核商品队列
        ManMadeContainerStatisticCommand manCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, ocId.toString());
        if (null != manCmd) {
            Set<Long> insideContainerIds = manCmd.getInsideContainerIds();
            Map<Long, Set<Long>> insideContainerSkuIds = manCmd.getInsideContainerIdSkuIds();
            for (Long icId : insideContainerIds) {
                Set<Long> skuIds = insideContainerSkuIds.get(icId);
                for (Long skuId : skuIds) {
                    // 清除逐件扫描的队列
                    cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString());
                }
                cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
            }
        }
        // 1.再清除所有提示容器队列
        cacheManager.remove(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
        // 2.清除所有库存统计信息
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, ocId.toString());
        // 3.清除所有库存缓存信息
        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, ocId.toString());
    }

    /***
     * 整箱上架:人工上架判断容器内部sku是否扫描完毕
     * @param ocCmd
     * @param icCmd
     * @param insideContainerIds
     * @param insideContainerSkuIds
     * @param insideContainerSkuIdsQty
     * @param skuCmd
     * @param scanPattern
     * @param logId
     * @return
     */
    @Override
    public CheckScanSkuResultCommand manMadeContainerPutawayCacheSkuAndCheckContainer(ContainerCommand ocCmd, ContainerCommand icCmd, Set<Long> insideContainerIds, Map<Long, Set<Long>> insideContainerSkuIds,
            Map<Long, Map<Long, Long>> insideContainerSkuIdsQty, WhSkuCommand skuCmd, Integer scanPattern, String logId) {
        CheckScanSkuResultCommand cssrCmd = new CheckScanSkuResultCommand();
        Long ocId = null;
        Long icId = icCmd.getId();
        // 0.先判断当前内部容器是否在缓存中
        boolean icExists = false;
        for (Long iId : insideContainerIds) {
            if (0 == icId.compareTo(iId)) {
                icExists = true;
                break;
            }
        }
        if (false == icExists) {
            log.error("tip container is not in cache server error, logId is[{}]", logId);
            throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
        }
        if (null != ocCmd) {
            ocId = ocCmd.getId();
            // 1.当前的内部容器是不是提示容器队列的第一个
            TipContainerCacheCommand cacheContainerCmd = cacheManager.getObject(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
            ArrayDeque<Long> cacheContainerIds = null;
            if (null != cacheContainerCmd) {
                cacheContainerIds = cacheContainerCmd.getTipInsideContainerIds();
            }
            if (null != cacheContainerIds && !cacheContainerIds.isEmpty()) {
                Long value = cacheContainerIds.peekFirst();// 队列的第一个
                if (null == value) value = -1L;
                if (0 != value.compareTo(icId)) {
                    log.error("tip container is not queue first element exception, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);
                }
            } else {
                log.error("scan container queue is exception, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.COMMON_CACHE_IS_ERROR);

            }
            // 2.得到当前内部容器的所有商品并复核商品
            Long skuId = skuCmd.getId();
            Double skuQty = skuCmd.getScanSkuQty();
            Set<Long> icSkusIds = insideContainerSkuIds.get(icId);
            boolean skuExists = false;
            for (Long sId : icSkusIds) {
                if (0 == skuId.compareTo(sId)) {
                    skuExists = true;
                    Map<Long, Long> icSkuAndQty = insideContainerSkuIdsQty.get(icId);
                    Long icSkuQty = icSkuAndQty.get(skuId);
                    if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {
                        TipScanSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                        ArrayDeque<Long> oneByOneScanSkuIds = null;   //已经扫描的sku队列
                        if (null != tipScanSkuCmd) {
                            oneByOneScanSkuIds = tipScanSkuCmd.getOneByOneScanSkuIds();
                        }
                        if (null != oneByOneScanSkuIds && !oneByOneScanSkuIds.isEmpty()) {
                            boolean isExists = false;
                            Iterator<Long> iter = oneByOneScanSkuIds.iterator();
                            while (iter.hasNext()) {
                                Long value = iter.next();
                                if (null == value) value = -1L;
                                if (0 == skuId.compareTo(new Long(value))) {
                                    isExists = true;  //当前sku已经扫描
                                    break;
                                }
                            }
                            long value = 0L;
                            if (false == isExists) {
                                oneByOneScanSkuIds.addFirst(skuId);// 先加入逐件扫描的队列
                                tipScanSkuCmd.setOneByOneScanSkuIds(oneByOneScanSkuIds);
                            } else {
                                // 取到扫描的数量
                                String cacheValue = cacheManager.getValue(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString());
                                if (!StringUtils.isEmpty(cacheValue)) {
                                    value = new Long(cacheValue).longValue();
                                }
                            }
                            if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                            }
                            long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString(), skuQty.intValue());
                            if (cacheValue == icSkuQty.longValue()) {
                                ArrayDeque<Long> cacheSkuIds = tipScanSkuCmd.getScanSkuIds();
                                if (null == cacheSkuIds || cacheSkuIds.isEmpty()) {
                                    cacheSkuIds = new ArrayDeque<Long>();
                                }
                                cacheSkuIds.addFirst(skuId);
                                tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                                cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                    // 全部商品已复核完毕
                                    if (isCacheAllExists(insideContainerIds, cacheContainerIds)) {
                                        // 全部容器已复核完毕
                                        cssrCmd.setPutaway(true);// 可上架
                                    } else {
                                        cssrCmd.setNeedTipContainer(true);// 上架后需要提示下一个容器
                                    }
                                } else {
                                    // 继续复核
                                    cssrCmd.setNeedScanSku(true);
                                }
                                break;
                            } else if (cacheValue < icSkuQty.longValue()) {
                                // 继续复核
                                cssrCmd.setNeedScanSku(true);
                                break;
                            } else {
                                log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                            }
                        } else {
                            // 不考虑功能参数复合过程中改变的情况
                            TipScanSkuCacheCommand cacheSkuCmd = new TipScanSkuCacheCommand();
                            cacheSkuCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                            cacheSkuCmd.setOuterContainerId(ocCmd.getId());
                            cacheSkuCmd.setOuterContainerCode(ocCmd.getCode());
                            cacheSkuCmd.setInsideContainerId(icCmd.getId());
                            cacheSkuCmd.setInsideContainerCode(icCmd.getCode());
                            ArrayDeque<Long> oneByOneCacheSkuIds = new ArrayDeque<Long>();
                            oneByOneCacheSkuIds.addFirst(skuId);
                            cacheSkuCmd.setOneByOneScanSkuIds(oneByOneCacheSkuIds);
                            long value = 0L;
                            if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                            }
                            long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString(), skuQty.intValue());
                            if (cacheValue == icSkuQty.longValue()) {
                                ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                cacheSkuIds.addFirst(skuId);
                                cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                                cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                    // 全部商品已复核完毕
                                    if (isCacheAllExists(insideContainerIds, cacheContainerIds)) {
                                        // 全部容器已复核完毕
                                        cssrCmd.setPutaway(true);// 可上架
                                    } else {
                                        cssrCmd.setNeedTipContainer(true);// 上架后需要提示下一个容器
                                    }
                                } else {
                                    // 继续复核
                                    cssrCmd.setNeedScanSku(true);
                                }
                                break;
                            } else if (cacheValue < icSkuQty.longValue()) {
                                // 继续复核
                                cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                cssrCmd.setNeedScanSku(true);
                                break;
                            } else {
                                log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                            }
                        }
                    } else {
                        if (skuQty.longValue() == icSkuQty.longValue()) {
                            TipScanSkuCacheCommand cacheSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                            ArrayDeque<Long> cacheSkuIds = null;
                            if (null != cacheSkuCmd) {
                                cacheSkuIds = cacheSkuCmd.getScanSkuIds();
                            }
                            if (null != cacheSkuIds && !cacheSkuIds.isEmpty()) {
                                boolean isExists = false;
                                Iterator<Long> iter = cacheSkuIds.iterator();
                                while (iter.hasNext()) {
                                    Long value = iter.next();
                                    if (null == value) value = -1L;
                                    if (0 == value.compareTo(skuId)) {
                                        isExists = true;
                                        break;
                                    }
                                }
                                if (false == isExists) {
                                    cacheSkuIds.addFirst(skuId);
                                    cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                                    cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                    if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                        // 全部商品已复核完毕
                                        // 判断上架以后是否需要提示下一个容器
                                        if (isCacheAllExists(insideContainerIds, cacheContainerIds)) {
                                            // 全部容器已复核完毕
                                            cssrCmd.setPutaway(true);// 可上架
                                        } else {
                                            cssrCmd.setNeedTipContainer(true);// 上架后需要提示下一个容器
                                        }
                                    } else {
                                        // 继续复核
                                        cssrCmd.setNeedScanSku(true);
                                    }
                                    break;
                                } else {
                                    // 重复扫描如果是最后一件则认为可以上架，否则报错提示
                                    if (isCacheAllExists(icSkusIds, CacheConstants.SCAN_SKU_QUEUE + icId.toString())) {
                                        // 全部商品已复核完毕
                                        // 判断上架以后是否需要提示下一个容器
                                        if (isCacheAllExists(insideContainerIds, cacheContainerIds)) {
                                            // 全部容器已复核完毕
                                            cssrCmd.setPutaway(true);// 可上架
                                        } else {
                                            cssrCmd.setNeedTipContainer(true);// 上架后需要提示下一个容器
                                        }
                                    } else {
                                        log.error("scan sku has already checked, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                                        throw new BusinessException(ErrorCodes.CONTAINER_SKU_HAS_ALREADY_SCANNED, new Object[] {icCmd.getCode()});
                                    }
                                }
                            } else {
                                TipScanSkuCacheCommand tipCmd = new TipScanSkuCacheCommand();
                                tipCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                                tipCmd.setOuterContainerId(ocCmd.getId());
                                tipCmd.setOuterContainerCode(ocCmd.getCode());
                                tipCmd.setInsideContainerId(icCmd.getId());
                                tipCmd.setInsideContainerCode(icCmd.getCode());
                                ArrayDeque<Long> tipSkuIds = new ArrayDeque<Long>();
                                tipSkuIds.addFirst(skuId);
                                tipCmd.setScanSkuIds(tipSkuIds);
                                cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), tipCmd, CacheConstants.CACHE_ONE_DAY);
                                if (isCacheAllExists(icSkusIds, tipSkuIds)) {
                                    // 全部商品已复核完毕
                                    // 判断上架以后是否需要提示下一个容器
                                    if (isCacheAllExists(insideContainerIds, cacheContainerIds)) {
                                        // 全部容器已复核完毕
                                        cssrCmd.setPutaway(true);// 可上架
                                    } else {
                                        cssrCmd.setNeedTipContainer(true);// 上架后需要提示下一个容器
                                    }
                                } else {
                                    // 继续复核
                                    cssrCmd.setNeedScanSku(true);
                                }
                                break;
                            }
                        } else {
                            log.error("scan sku qty is not equal with rcvd inv qty, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                            throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_NOT_EQUAL_SCAN_SKU_QTY_ERROR, new Object[] {icCmd.getCode()});
                        }
                    }
                }
            }
            if (false == skuExists) {
                log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
            }
        } else {
            // 1.得到当前内部容器的所有商品并复核商品
            Long skuId = skuCmd.getId();
            Double skuQty = skuCmd.getScanSkuQty();
            Set<Long> icSkusIds = insideContainerSkuIds.get(icId);
            boolean skuExists = false;
            for (Long sId : icSkusIds) {
                if (0 == skuId.compareTo(sId)) {
                    skuExists = true;
                    Map<Long, Long> icSkuAndQty = insideContainerSkuIdsQty.get(icId);
                    Long icSkuQty = icSkuAndQty.get(skuId);
                    if (WhScanPatternType.ONE_BY_ONE_SCAN == scanPattern) {
                        TipScanSkuCacheCommand tipScanSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                        ArrayDeque<Long> oneByOneScanSkuIds = null;
                        if (null != tipScanSkuCmd) {
                            oneByOneScanSkuIds = tipScanSkuCmd.getOneByOneScanSkuIds();
                        }
                        if (null != oneByOneScanSkuIds && !oneByOneScanSkuIds.isEmpty()) {
                            boolean isExists = false;
                            Iterator<Long> iter = oneByOneScanSkuIds.iterator();
                            while (iter.hasNext()) {
                                Long value = iter.next();
                                if (null == value) value = -1L;
                                if (0 == skuId.compareTo(new Long(value))) {
                                    isExists = true;
                                    break;
                                }
                            }
                            long value = 0L;
                            if (false == isExists) {
                                oneByOneScanSkuIds.addFirst(skuId);// 先加入逐件扫描的队列
                                tipScanSkuCmd.setOneByOneScanSkuIds(oneByOneScanSkuIds);
                            } else {
                                // 取到扫描的数量
                                String cacheValue = cacheManager.getValue(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString());
                                if (!StringUtils.isEmpty(cacheValue)) {
                                    value = new Long(cacheValue).longValue();
                                }
                            }
                            if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                            }
                            long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString(), skuQty.intValue());
                            if (cacheValue == icSkuQty.longValue()) {
                                ArrayDeque<Long> cacheSkuIds = tipScanSkuCmd.getScanSkuIds();
                                if (null == cacheSkuIds || cacheSkuIds.isEmpty()) {
                                    cacheSkuIds = new ArrayDeque<Long>();
                                }
                                cacheSkuIds.addFirst(skuId);
                                tipScanSkuCmd.setScanSkuIds(cacheSkuIds);
                                cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), tipScanSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                    // 全部商品已复核完毕
                                    cssrCmd.setPutaway(true);// 可上架
                                } else {
                                    // 继续复核
                                    cssrCmd.setNeedScanSku(true);
                                }
                                break;
                            } else if (cacheValue < icSkuQty.longValue()) {
                                // 继续复核
                                cssrCmd.setNeedScanSku(true);
                                break;
                            } else {
                                log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                            }
                        } else {
                            // 不考虑功能参数复合过程中改变的情况
                            TipScanSkuCacheCommand cacheSkuCmd = new TipScanSkuCacheCommand();
                            cacheSkuCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.PALLET_PUTAWAY);
                            cacheSkuCmd.setInsideContainerId(icCmd.getId());
                            cacheSkuCmd.setInsideContainerCode(icCmd.getCode());
                            ArrayDeque<Long> oneByOneCacheSkuIds = new ArrayDeque<Long>();
                            oneByOneCacheSkuIds.addFirst(skuId);
                            cacheSkuCmd.setOneByOneScanSkuIds(oneByOneCacheSkuIds);
                            long value = 0L;
                            if ((value + skuQty.longValue()) > icSkuQty.longValue()) {
                                log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, value + skuQty.longValue(), icSkuQty, logId);
                                throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY, new Object[] {value + skuQty.longValue()});
                            }
                            long cacheValue = cacheManager.incrBy(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString(), skuQty.intValue());
                            if (cacheValue == icSkuQty.longValue()) {
                                ArrayDeque<Long> cacheSkuIds = new ArrayDeque<Long>();
                                cacheSkuIds.addFirst(skuId);
                                cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                                cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                    // 全部商品已复核完毕
                                    cssrCmd.setPutaway(true);// 可上架
                                } else {
                                    // 继续复核
                                    cssrCmd.setNeedScanSku(true);
                                }
                                break;
                            } else if (cacheValue < icSkuQty.longValue()) {
                                // 继续复核
                                cssrCmd.setNeedScanSku(true);
                                break;
                            } else {
                                log.error("sku scan qty has already more than rcvd qty, skuId is:[{}], scan qty is:[{}], rcvd qty is:[{}], logId is:[{}]", skuId, cacheValue, icSkuQty, logId);
                                throw new BusinessException(ErrorCodes.SCAN_SKU_QTY_IS_MORE_THAN_RCVD_QTY);
                            }
                        }
                    } else {
                        if (skuQty.longValue() == icSkuQty.longValue()) {
                            TipScanSkuCacheCommand cacheSkuCmd = cacheManager.getObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                            ArrayDeque<Long> cacheSkuIds = null;
                            if (null != cacheSkuCmd) {
                                cacheSkuIds = cacheSkuCmd.getScanSkuIds();
                            }
                            if (null != cacheSkuIds && !cacheSkuIds.isEmpty()) {
                                boolean isExists = false;
                                Iterator<Long> iter = cacheSkuIds.iterator();
                                while (iter.hasNext()) {
                                    Long value = iter.next();
                                    if (null == value) value = -1L;
                                    if (0 == value.compareTo(skuId)) {
                                        isExists = true;
                                        break;
                                    }
                                }
                                if (false == isExists) {
                                    cacheSkuIds.addFirst(skuId);
                                    cacheSkuCmd.setScanSkuIds(cacheSkuIds);
                                    cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), cacheSkuCmd, CacheConstants.CACHE_ONE_DAY);
                                    if (isCacheAllExists(icSkusIds, cacheSkuIds)) {
                                        // 全部商品已复核完毕
                                        cssrCmd.setPutaway(true);// 可上架
                                    } else {
                                        // 继续复核
                                        cssrCmd.setNeedScanSku(true);
                                    }
                                    break;
                                } else {
                                    log.error("scan sku has already checked, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                                    throw new BusinessException(ErrorCodes.CONTAINER_SKU_HAS_ALREADY_SCANNED, new Object[] {icCmd.getCode()});
                                }
                            } else {
                                TipScanSkuCacheCommand tipCmd = new TipScanSkuCacheCommand();
                                tipCmd.setPutawayPatternDetailType(WhPutawayPatternDetailType.CONTAINER_PUTAWAY);
                                tipCmd.setInsideContainerId(icCmd.getId());
                                tipCmd.setInsideContainerCode(icCmd.getCode());
                                ArrayDeque<Long> tipSkuIds = new ArrayDeque<Long>();
                                tipSkuIds.addFirst(skuId);
                                tipCmd.setScanSkuIds(tipSkuIds);
                                cacheManager.setObject(CacheConstants.SCAN_SKU_QUEUE + icId.toString(), tipCmd, CacheConstants.CACHE_ONE_DAY);
                                if (isCacheAllExists(icSkusIds, tipSkuIds)) {
                                    // 全部商品已复核完毕
                                    cssrCmd.setPutaway(true);// 可上架
                                } else {
                                    // 继续复核
                                    cssrCmd.setNeedScanSku(true);
                                }
                                break;
                            }
                        } else {
                            log.error("scan sku qty is not equal with rcvd inv qty, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                            throw new BusinessException(ErrorCodes.CONTAINER_SKU_QTY_NOT_EQUAL_SCAN_SKU_QTY_ERROR, new Object[] {icCmd.getCode()});
                        }
                    }
                }
            }
            if (false == skuExists) {
                log.error("scan sku is not found in current inside contianer error, ocId is:[{}], icId is:[{}], scanSkuId is:[{}], logId is:[{}]", ocId, icId, skuId, logId);
                throw new BusinessException(ErrorCodes.CONTAINER_NOT_FOUND_SCAN_SKU_ERROR, new Object[] {icCmd.getCode()});
            }
        }
        return cssrCmd;
    }
    
    /***
     * 人工上架:整箱上架清除缓存
     * @param containerCmd
     * @param insideContainerCmd
     * @param isAfterPutawayTipContainer
     * @param logId
     */
    public void manMadeContainerPutawayRemoveAllCache(ContainerCommand outerContainerCmd, ContainerCommand insideContainerCmd, Boolean isAfterPutawayTipContainer, String logId){
        if (null != outerContainerCmd) {
            Long ocId = outerContainerCmd.getId();
            Long icId = insideContainerCmd.getId();
            if (false == isAfterPutawayTipContainer) {  //内部容器是否提示完毕
                       ManMadeContainerStatisticCommand isCmd = cacheManager.getMapObject(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                       if (null != isCmd) {
                            Map<Long, Set<Long>> insideContainerSkuIds = isCmd.getInsideContainerIdSkuIds();
                            Set<Long> skuIds = insideContainerSkuIds.get(icId);
                            for (Long skuId : skuIds) {
                                // 清除逐件扫描的队列
                                cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString() + skuId.toString());
                        }
                        cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
                        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                        cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
                }
                // 1.再清除所有提示容器队列
                cacheManager.remove(CacheConstants.SCAN_CONTAINER_QUEUE + ocId.toString());
                // 2.清除所有内部容器统计信息
                cacheManager.removeMapValue(CacheConstants.CONTAINER_STATISTIC, ocId.toString());

            } else {
                // 0.清除所有库存统计信息
                cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
                // 1.清除所有库存缓存信息
                cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
            }
        } else {
            Long icId = insideContainerCmd.getId();
            // 0.清除所有商品队列
            cacheManager.remove(CacheConstants.SCAN_SKU_QUEUE + icId.toString());
            // 1.清除所有库存统计信息
            cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY_STATISTIC, icId.toString());
            // 2.清除所有库存缓存信息
            cacheManager.removeMapValue(CacheConstants.CONTAINER_INVENTORY, icId.toString());
        }
    }

    @SuppressWarnings("unused")
    private boolean isCacheAllExists2(Set<String> ids, String cacheKey) {
        boolean allExists = true;
        long len = cacheManager.listLen(cacheKey);
        if (0 < len) {
            for (String id : ids) {
                String cId = id;
                boolean isExists = false;
                for (int i = 0; i < new Long(len).intValue(); i++) {
                    String value = cacheManager.findListItem(cacheKey, i);
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

}
