package com.baozun.scm.primservice.whoperation.manager.odo.wave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.command.wave.WaveLineCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WhWaveLineCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy.DistributionModeArithmeticManagerProxy;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;

@Service("whWaveLineManager")
@Transactional
public class WhWaveLineManagerImpl extends BaseManagerImpl implements WhWaveLineManager {

    public static final Logger log = LoggerFactory.getLogger(WhWaveLineManagerImpl.class);

    @Autowired
    private WhWaveLineDao whWaveLineDao;
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private DistributionModeArithmeticManagerProxy distributionModeArithmeticManagerProxy;

    /**
     * 得到所有硬阶段的波次名次行集合
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWaveLine> getHardAllocationWhWaveLine(Integer allocatePhase, Long ouId) {
        List<WhWaveLine> datas = whWaveLineDao.getNeedAllocationRuleWhWaveLine(allocatePhase, ouId);
        return datas;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWaveLine> getWaveLineByParam(WhWaveLine whWaveLine) {
        List<WhWaveLine> whWaveLineList = whWaveLineDao.findListByParam(whWaveLine);
        return whWaveLineList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWaveLine> getSoftAllocationWhWaveLine(Long waveId, Long ouId) {
        List<WhWaveLine> whWaveLineList = this.whWaveLineDao.findSoftAllocationWhWaveLine(waveId, ouId, BaseModel.LIFECYCLE_NORMAL);
        return whWaveLineList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhWaveLine getWaveLineByIdAndOuId(Long waveLineId, Long ouId) {
        WhWaveLine whWaveLine = new WhWaveLine();
        whWaveLine.setId(waveLineId);
        whWaveLine.setOuId(ouId);
        List<WhWaveLine> whWaveLineList = this.whWaveLineDao.findListByParam(whWaveLine);
        if (null == whWaveLineList || 1 != whWaveLineList.size()) {
            throw new BusinessException("多个波次明细");
        }
        return whWaveLineList.get(0);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void modifyRuleIntoWhWaveLine(List<Long> whWaveLine, Long ruleId, Long ouId) {
        if (null == whWaveLine || whWaveLine.isEmpty()) {
            if (log.isErrorEnabled()) {
                log.error("modifyRuleIntoWhWaveLine error:whWaveLine is empty");
            }
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        if (null == ouId) {
            if (log.isErrorEnabled()) {
                log.error("modifyRuleIntoWhWaveLine error:ouId is empty");
            }
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        int num = whWaveLineDao.modifyRuleIntoWhWaveLine(whWaveLine, ruleId, ouId);
        if (num == -1) {
            if (log.isErrorEnabled()) {
                log.error("modifyRuleIntoWhWaveLine error:update_data_error");
            }
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> getOdoLinesByWaveIdList(List<Long> waveIdList, Long ouId) {
        List<Long> datas = whWaveLineDao.getOdoLinesByWaveIdList(waveIdList, ouId);
        return datas;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWaveLine> getWhWaveLinesByWaveIdList(List<Long> waveIdList, Long ouId) {
        List<WhWaveLine> datas = whWaveLineDao.getWhWaveLinesByWaveIdList(waveIdList, ouId);
        return datas;
    }

    @Override
    public List<WhWaveLineCommand> findWaveLineCommandListByWaveId(Long waveId, Long ouId) {
        return whWaveLineDao.getWhWaveLineCommandByWaveId(waveId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteWaveLinesByOdoId(Long odoId, Long waveId, Long ouId, String reason) {
        // 1.修改出库单明细waveCode为空
        int num = whOdoLineDao.updateOdoLineByAllocateFail(odoId, reason, ouId);
        if (0 == num) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 2.修改出库单waveCode为空
        num = whOdoDao.updateOdoByAllocateFail(odoId, reason, ouId);
        if (0 == num) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        // 3.从波次明细中剔除出库单
        num = whWaveLineDao.removeWaveLineWhole(waveId, odoId, ouId);
        // 4.提出的出库单加入配货模式
        WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
        Map<Long, String> odoIdCounterCodeMap = new HashMap<Long, String>();
        odoIdCounterCodeMap.put(odoId, odo.getCounterCode());
        distributionModeArithmeticManagerProxy.addToPool(odoIdCounterCodeMap);
        /*WhWaveLine line = new WhWaveLine();
        line.setWaveId(waveId);
        line.setOuId(ouId);
        long lineCount = whWaveLineDao.findListCountByParam(line);
        if (lineCount == 0) {
            whWaveDao.deleteByIdOuId(waveId, ouId);
            log.info("waveId:{} not have lines,delete", waveId);
        }*/
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateWaveLineByAllocateQty(Long id, Double allocateQty, Double containerQty, Boolean isStaticLocation, Set<String> staticLocationIds, Set<String> trayIds, Set<String> packingCaseIds, Long areaId, Long ouId, String logId) {
        WhWaveLine whWaveLine = whWaveLineDao.findWhWaveLineById(id, ouId);
        Double oldAllocateQty = (null == whWaveLine.getAllocateQty() ? 0.0 : whWaveLine.getAllocateQty());
        Double newAllocateQty = allocateQty + oldAllocateQty;
        whWaveLine.setAllocateQty(newAllocateQty);
        if (-1 == Constants.DEFAULT_DOUBLE.compareTo(containerQty)) {
            whWaveLine.setIsPalletContainer(Boolean.TRUE);
            Double oldPalletContainerQty = (null == whWaveLine.getPalletContainerQty() ? 0.0 : whWaveLine.getPalletContainerQty());
            whWaveLine.setPalletContainerQty(containerQty + oldPalletContainerQty);
        }
        if (null != isStaticLocation && isStaticLocation) {
            whWaveLine.setIsStaticLocationAllocate(isStaticLocation);
            if (!staticLocationIds.isEmpty()) {
				String staticLocationIdsStr = whWaveLine.getStaticLocationIds();
				staticLocationIdsStr = StringUtils.isEmpty(staticLocationIdsStr)
						? StringUtils.collectionToCommaDelimitedString(staticLocationIds)
						: staticLocationIdsStr + "," + StringUtils.collectionToCommaDelimitedString(staticLocationIds);
				whWaveLine.setStaticLocationIds(staticLocationIdsStr);
            }
        }
        if (!trayIds.isEmpty()) {
        	String trayIdsStr = whWaveLine.getTrayIds();
        	trayIdsStr = StringUtils.isEmpty(trayIdsStr) 
        			? StringUtils.collectionToCommaDelimitedString(trayIds)
        			: trayIdsStr + "," + StringUtils.collectionToCommaDelimitedString(trayIds);
            whWaveLine.setTrayIds(trayIdsStr);
        }
        if (!packingCaseIds.isEmpty()) {
        	String packingCaseIdsStr = whWaveLine.getPackingCaseIds();
        	packingCaseIdsStr = StringUtils.isEmpty(packingCaseIdsStr) 
        			? StringUtils.collectionToCommaDelimitedString(packingCaseIds)
                	: packingCaseIdsStr + "," + StringUtils.collectionToCommaDelimitedString(packingCaseIds);
            whWaveLine.setPackingCaseIds(packingCaseIdsStr);
        }
        if (null != areaId) {
            whWaveLine.setAreaId(areaId);
        }
        whWaveLineDao.saveOrUpdateByVersion(whWaveLine);
        log.info("update whwaveLineId:[{}], allocateQty:[{}], logId:[{}]", id, newAllocateQty, logId);
    }
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Map<Long, Map<Long, Map<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>>>> getNeedInventoryMap(List<Long> waveIdList, Long ouId) {
        List<WhWaveLine> whWaveLines = whWaveLineDao.getWhWaveLinesByWaveIdList(waveIdList, ouId);
        if (null != whWaveLines && !whWaveLines.isEmpty()) {
            Map<Long, Map<Long, Map<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>>>> map = new HashMap<Long, Map<Long, Map<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>>>>();
            for (WhWaveLine line : whWaveLines) {
                Long skuId = line.getSkuId();
                Long storeId = line.getStoreId();
                Long invStatus = line.getInvStatus();
                Long ruleId = line.getAllocateRuleId();
                if (map.containsKey(skuId)) {
                    Map<Long, Map<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>>> storeMap = map.get(skuId);
                    if (storeMap.containsKey(storeId)) {
                        Map<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>> invStatusMap = storeMap.get(storeId);
                        if (invStatusMap.containsKey(invStatus)) {
                            Map<Long, Map<Boolean, List<WhWaveLine>>> ruleMap = invStatusMap.get(invStatus);
                            if (ruleMap.containsKey(ruleId)) {
                                Map<Boolean, List<WhWaveLine>> waveLineMap = ruleMap.get(ruleId);
                                // 把波次明细按是否有库存属性分类放入Map
                                addInfoToWaveLineMap(waveLineMap, line);
                            } else {
                                Map<Boolean, List<WhWaveLine>> waveLineMap = new HashMap<Boolean, List<WhWaveLine>>();
                                // 把波次明细按是否有库存属性分类放入Map
                                addInfoToWaveLineMap(waveLineMap, line);
                                ruleMap.put(ruleId, waveLineMap);
                            }
                        } else {
                            Map<Long, Map<Boolean, List<WhWaveLine>>> ruleMap = new HashMap<Long, Map<Boolean, List<WhWaveLine>>>();
                            Map<Boolean, List<WhWaveLine>> waveLineMap = new HashMap<Boolean, List<WhWaveLine>>();
                            // 把波次明细按是否有库存属性分类放入Map
                            addInfoToWaveLineMap(waveLineMap, line);
                            ruleMap.put(ruleId, waveLineMap);
                            invStatusMap.put(invStatus, ruleMap);
                        }
                    } else {
                        Map<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>> invStatusMap = new HashMap<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>>();
                        Map<Long, Map<Boolean, List<WhWaveLine>>> ruleMap = new HashMap<Long, Map<Boolean, List<WhWaveLine>>>();
                        Map<Boolean, List<WhWaveLine>> waveLineMap = new HashMap<Boolean, List<WhWaveLine>>();
                        // 把波次明细按是否有库存属性分类放入Map
                        addInfoToWaveLineMap(waveLineMap, line);
                        ruleMap.put(ruleId, waveLineMap);
                        invStatusMap.put(invStatus, ruleMap);
                        storeMap.put(storeId, invStatusMap);
                    }
                } else {
                    Map<Long, Map<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>>> storeMap = new HashMap<Long, Map<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>>>();
                    Map<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>> invStatusMap = new HashMap<Long, Map<Long, Map<Boolean, List<WhWaveLine>>>>();
                    Map<Long, Map<Boolean, List<WhWaveLine>>> ruleMap = new HashMap<Long, Map<Boolean, List<WhWaveLine>>>();
                    Map<Boolean, List<WhWaveLine>> waveLineMap = new HashMap<Boolean, List<WhWaveLine>>();
                    // 把波次明细按是否有库存属性分类放入Map
                    addInfoToWaveLineMap(waveLineMap, line);
                    ruleMap.put(ruleId, waveLineMap);
                    invStatusMap.put(invStatus, ruleMap);
                    storeMap.put(storeId, invStatusMap);
                    map.put(skuId, storeMap);
                }
            }
            return map;
        }
        return null;
    }

    private void addInfoToWaveLineMap(Map<Boolean, List<WhWaveLine>> waveLineMap, WhWaveLine line) {
        if (StringUtils.hasText(line.getInvAttr1()) || StringUtils.hasText(line.getInvAttr2()) || StringUtils.hasText(line.getInvAttr3()) || StringUtils.hasText(line.getInvAttr4()) || StringUtils.hasText(line.getInvAttr5())
                || StringUtils.hasText(line.getInvType()) || StringUtils.hasText(line.getBatchNumber()) || StringUtils.hasText(line.getCountryOfOrigin()) || null != line.getMfgDate() || null != line.getExpDate() || null != line.getMaxExpDate()
                || null != line.getMinExpDate()) {
            addInfoToWaveLineList(waveLineMap, line, Boolean.TRUE);
        } else {
            addInfoToWaveLineList(waveLineMap, line, Boolean.FALSE);
        }
    }

    private void addInfoToWaveLineList(Map<Boolean, List<WhWaveLine>> waveLineMap, WhWaveLine line, Boolean flag) {
        List<WhWaveLine> lines = waveLineMap.get(flag);
        if (null != lines && !lines.isEmpty()) {
            lines.add(line);
        } else {
            lines = new ArrayList<WhWaveLine>();
            lines.add(line);
            waveLineMap.put(flag, lines);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhWaveLine> findWaveLineListByWaveId(Long waveId, Long ouId) {
        WhWaveLine whWaveLine = new WhWaveLine();
        whWaveLine.setWaveId(waveId);
        whWaveLine.setOuId(ouId);
        List<WhWaveLine> whWaveLineList = this.whWaveLineDao.findListByParam(whWaveLine);
        return whWaveLineList;
    }

    @Override
    public List<WhWaveLine> findWaveLineByNotEnoughAllocationQty(Long waveId, Long ouId) {
        return whWaveLineDao.findNotEnoughAllocationQty(waveId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<WaveLineCommand> findWaveLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        return this.whWaveLineDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    /**
     * 根据波次Id获取出库单Id
     *
     * @param waveId
     * @param ouId
     * @return
     */
    public List<Long> getOdoIdListByWaveId(Long waveId, Long ouId){
        return whWaveLineDao.getOdoIdListByWaveId(waveId, ouId);
    }

    /**
     * 根据波次Id获取出库单Id
     *
     * @param waveIdList
     * @param ouId
     * @return
     */
    @Override
    public List<Long> getOdoIdListByWaveIdList(List<Long> waveIdList, Long ouId) {
        return whWaveLineDao.getOdoIdListByWaveIdList(waveIdList, ouId);
    }
}
