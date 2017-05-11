package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuLocationCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.LocationProductVolumeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.LocationSkuVolumeDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationProductVolume;
import com.baozun.scm.primservice.whoperation.model.warehouse.LocationSkuVolume;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

@Service("locationManagerProxy")
@Transactional
public class LocationManagerImpl extends BaseManagerImpl implements LocationManager {
    @Autowired
    private WhLocationDao whLocationDao;
    @Autowired
    private WhSkuLocationDao whSkuLocationDao;
    @Autowired
    private LocationProductVolumeDao locationProductVolumeDao;
    @Autowired
    private WhSkuDao whSkuDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private LocationSkuVolumeDao locationSkuVolumeDao;

    @Autowired
    private WhCheckingDao whCheckingDao;

    @Autowired
    private WhCheckingLineDao whCheckingLineDao;

    // @Autowired
    // private LocationSkuVolumeDao locationSkuVolumeDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public LocationProductVolume getLocationProductVolumeByPcIdAndSize(Long pcId, String sizeType, Long ouId) {
        LocationProductVolume search = new LocationProductVolume();
        search.setOuId(ouId);
        search.setPcId(pcId);
        search.setSize(sizeType);
        search.setUnit(Constants.ALLOCATE_UNIT_PIECE);
        List<LocationProductVolume> result = this.locationProductVolumeDao.findListByParam(search);
        return (result == null || result.size() == 0) ? null : result.get(0);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> sortByIds(Set<Long> ids, Long ouId) {
        return this.whLocationDao.sortByIds(ids, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Location> findLocationWithStaticNoMix(Long ouId) {
        Location location = new Location();
        location.setOuId(ouId);
        location.setIsCheckLocation(false);
        location.setIsStatic(true);
        location.setIsMixStacking(false);

        return this.whLocationDao.findListByParam(location);
    }

    @Override
    public Long getBindedSkuByLocationId(Long locationId, Long ouId) {
        WhSkuLocationCommand command = new WhSkuLocationCommand();
        command.setLifecycle(Constants.LIFECYCLE_START);
        command.setLocationId(locationId);
        command.setOuId(ouId);
        List<WhSkuLocationCommand> list = this.whSkuLocationDao.findSkuLocationToShard(command);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0).getSkuId();
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public SkuRedisCommand findSkuMasterBySkuId(Long skuId, Long ouId, String logId) {
        return whSkuDao.findSkuAllInfoByParamExt(skuId, ouId);
    }

    @Override
    public List<Location> findCheckLocationWithStaticNoMix(Long ouId) {
        Location location = new Location();
        location.setOuId(ouId);
        location.setIsCheckLocation(true);
        location.setIsStatic(true);
        location.setIsMixStacking(false);

        return this.whLocationDao.findListByParam(location);
    }

    @Override
    public Long reduceQty(Long facilityId, Long skuId, String outboundboxCode, Long ouId, Long userId) {
        // List<Long> locationIds = locationSkuVolumeDao.findLocationIdsByfacilityId(facilityId,
        // ouId);
        // 耗材原来库存
        Double oldQty = 0.0;
        // 耗材扣减后库存
        Double newQty = 0.0;
        // 耗材原来库存id
        Long oldInvId = 0L;
        // 新插入耗材库存id
        // Long newInvId = 0L;
        List<LocationSkuVolume> lsvList = this.locationSkuVolumeDao.findBySkuIdAndFacilityId(skuId, facilityId, ouId);
        if (null != lsvList && !lsvList.isEmpty()) {
            for (LocationSkuVolume lsv : lsvList) {
                if (null != lsv) {
                    // 这里是任意取一条库存明细做操作 没有优先级
                    List<WhSkuInventory> invList = whSkuInventoryDao.findSkuInvByLocationIdWithOnHandQty(lsv.getLocationId(), skuId, ouId);
                    if (null == invList || invList.isEmpty()) {
                        // 找不到耗材 返回为空
                        return null;
                    }
                    WhSkuInventory inv = invList.get(0);
                    oldInvId = inv.getId();
                    oldQty = inv.getOnHandQty();
                    inv.setOnHandQty(1.0);
                    inv.setOccupationCode(outboundboxCode);
                    inv.setId(null);
                    try {
                        // 插入被扣减的库存信息
                        this.whSkuInventoryDao.insert(inv);
                        // newInvId = inv.getId();
                        // 更新库存:分两种:1是更新后在库库存量为0,直接删除;2是更新后数量不为0, 更新数据
                        newQty = oldQty - 1;
                        if (0 == newQty) {
                            // 直接删除原来的库位库存信息
                            this.whSkuInventoryDao.deleteWhSkuInventoryById(oldInvId, ouId);
                        } else {
                            inv.setId(oldInvId);
                            inv.setOnHandQty(newQty);
                            this.whSkuInventoryDao.saveOrUpdateByVersion(inv);
                        }
                        return lsv.getLocationId();
                        // 插入log日志表
                        // this.insertSkuInventoryLog(oldInvId, -1.0, oldQty, true, ouId, userId,
                        // InvTransactionType.CHECK);
                        // 删除新插入库存明细
                        // this.whSkuInventoryDao.deleteWhSkuInventoryById(newInvId, ouId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return lsv.getLocationId();
                    }
                }
            }
        }
        return null;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<LocationSkuVolume> findListByfacilityId(Long facilityId, Long ouId) {
        // 根据复核台id查出所对应的库位信息
        // 先找出所有库位id在根据库位id找到对应的库为名 和该库位下商品剩余数
        List<LocationSkuVolume> list = new ArrayList<>();
        try {
            List<Long> locationIds = locationSkuVolumeDao.findLocationIdsByfacilityId(facilityId, ouId);
            Long a = 1L;
            if (null != locationIds && locationIds.size() > 0) {
                for (Long locationId : locationIds) {

                    LocationSkuVolume locationSkuVolume = getLocationSkuVolumeByLocationIdAndOuid(ouId, locationId);
                    list.add(locationSkuVolume);
                    locationSkuVolume.setId(a);
                    a++;
                }
            }
            return list;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.SKU_IS_NULL_BY_ID);
        }
    }

    /**
     * 通过库位id和ouid获取库位库存信息
     * 
     * @param ouId
     * @param locationId
     * @return
     */
    private LocationSkuVolume getLocationSkuVolumeByLocationIdAndOuid(Long ouId, Long locationId) {
        LocationSkuVolume locationSkuVolume = locationSkuVolumeDao.findListBylocationId(locationId, ouId);
        Long OnHandQty = whSkuInventoryDao.findOnHandQtyByLocationId(locationId, ouId);
        if (null == OnHandQty) {
            locationSkuVolume.setOnHandQty(0L);
        } else {
            locationSkuVolume.setOnHandQty(OnHandQty);
        }

        return locationSkuVolume;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Long reduceQtyAndUpdateOutboundbox(WhCheckingCommand command) {
        Long facilityId = command.getCheckingFacilityId();
        Long skuId = command.getConsumableSkuId();
        Long ouId = command.getOuId();
        Long userId = command.getModifiedId();
        String outboundboxCode = command.getOutboundboxCode();
        // 1.扣减耗材
        Long locationId = this.reduceQty(facilityId, skuId, outboundboxCode, ouId, userId);
        // 2.保存出库箱
        updateCheckOutboundBox(command);
        return locationId;
    }

    /**
     * [业务方法] 回写出库箱信息
     * @param whCheckingCommand
     */
    private void updateCheckOutboundBox(WhCheckingCommand whCheckingCommand) {
        WhChecking checking = new WhChecking();
        String outboundboxCode = whCheckingCommand.getOutboundboxCode();
        checking.setOuterContainerId(whCheckingCommand.getOuterContainerId());
        checking.setFacilityId(whCheckingCommand.getFacilityId());
        checking.setOuId(whCheckingCommand.getOuId());
        checking.setContainerLatticeNo(whCheckingCommand.getContainerLatticeNo());
        List<WhChecking> checkingList = whCheckingDao.findListByParam(checking);
        if (null != checkingList && !checkingList.isEmpty()) {
            checking = checkingList.get(0);
            checking.setOutboundboxCode(outboundboxCode);
            whCheckingDao.update(checking);
            WhCheckingLine whCheckingLine = new WhCheckingLine();
            whCheckingLine.setCheckingId(checking.getId());
            whCheckingLine.setOuId(checking.getOuId());
            List<WhCheckingLine> checkingLineList = whCheckingLineDao.findListByParam(whCheckingLine);
            if (null != checkingLineList && !checkingLineList.isEmpty()) {
                for (WhCheckingLine line : checkingLineList) {
                    line.setOutboundboxId(whCheckingCommand.getConsumableSkuId());
                    line.setOutboundboxCode(outboundboxCode);
                    whCheckingLineDao.saveOrUpdateByVersion(line);
                }
            }
        }
    }
}
