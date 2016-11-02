/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.manager.warehouse.inventory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WarehouseDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryLogDao;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseInventoryManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.Warehouse;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

import lark.common.annotation.MoreDB;

/**
 * @author lichuan
 *
 */
@Transactional
@Service("inventoryOccupyManager")
public class InventoryOccupyManagerImpl extends BaseInventoryManagerImpl implements InventoryOccupyManager {
    protected static final Logger log = LoggerFactory.getLogger(InventoryOccupyManagerImpl.class);
    @Autowired
    private WhSkuInventoryDao inventoryDao;
    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private WhSkuInventoryLogDao whSkuInventoryLogDao;
    /**
     * @author lichuan
     * @param invCmds
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void simpleOccupy(List<WhSkuInventoryCommand> invCmds, String occupyCode, String logId) {
        String occupyKey = null;
        Double eQty = null;
        for(WhSkuInventoryCommand invCmd : invCmds){
            if(null == occupyKey || !occupyKey.equals(invCmd.getOccupyKey())){
                occupyKey = invCmd.getOccupyKey();
                eQty = (null == invCmd.getExpectQty() ? 0.0 : invCmd.getExpectQty());
            }
            if(0 >= eQty.compareTo(new Double("0.0"))){
                continue;
            }
            Long invId = invCmd.getId();
            if(-1 == eQty.compareTo(invCmd.getOnHandQty())){
                int occupied = inventoryDao.occupyInvByCodeAndId(eQty, occupyCode, invId);
                if(-1 == occupied){
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
                insertShareInventory(invCmd, invCmd.getOnHandQty().doubleValue() - eQty.doubleValue(), logId);
                eQty = new Double("0.0");
            }else{
                int occupied = inventoryDao.occupyInvByCodeAndId(eQty, occupyCode, invId);
                if(-1 == occupied){
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
                eQty = eQty.doubleValue() - invCmd.getOnHandQty().doubleValue();
            }
        }
    }
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Double hardAllocateOccupy(List<WhSkuInventoryCommand> skuInvs, Double qty, String occupyCode, Long odoLineId, Warehouse wh) {
		if (null == skuInvs || skuInvs.isEmpty()) {
			return 0.0;
		}
		Double occupyNum = 0.0;	// 实际占用数量
		Double count = qty;		// 传入的计划占用量
		for (WhSkuInventoryCommand inv : skuInvs) {
			Double oldQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(inv.getUuid(), wh.getId());
			if (-1 == count.compareTo(inv.getOnHandQty())) {
				boolean b = occupyInv(count, oldQty, occupyCode, "ODO", odoLineId, inv.getId(), wh);
				if(!b){
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
				insertShareInventory(inv, inv.getOnHandQty() - count, logId);
				return qty;
			} else {
				boolean b = occupyInv(inv.getOnHandQty(), oldQty, occupyCode, "ODO", odoLineId, inv.getId(), wh);
				if(!b){
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
				count = count - inv.getOnHandQty();
				occupyNum = occupyNum + inv.getOnHandQty();
			}
		}
		return occupyNum;
	}
    
    private boolean occupyInv(Double qty, Double oldQty, String occupyCode, String occupySource, Long odoLineId, Long invId, Warehouse wh) {
    	boolean flag = false;
    	for (int i = 0; i < 5; i++) {
    		WhSkuInventory skuInv = inventoryDao.findWhSkuInventoryById(invId, wh.getId());
    		skuInv.setOnHandQty(qty);
    		skuInv.setOccupationCode(occupyCode);
    		skuInv.setOccupationCodeSource(occupySource);
    		skuInv.setOccupationLineId(odoLineId);
    		int num = inventoryDao.saveOrUpdateByVersion(skuInv);
    		if (1 == num) {
    			flag = true;
    			insertSkuInventoryLog(invId, -qty, oldQty, wh.getIsTabbInvTotal(), wh.getId(), 1L);
    			break;
			} else {
				// 修改失败 继续执行
                log.warn("occupyInv error:invId:{},occupyCode:{},qty:{},logId:{}");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
			}
		}
    	return flag;
    }

	@Override
	public Double hardAllocateListOccupy(List<WhSkuInventoryCommand> list, Double qty, String occupyCode, Long odoLineId, Warehouse wh, Boolean isStaticLocation, Set<String> staticLocationIds) {
		if (null == list || list.isEmpty()) {
			return 0.0;
		}
		Double occupyNum = 0.0;	// 实际占用数量
		Double count = qty;		// 传入的计划占用量
		for (int i = 0; i < list.size(); i++) {
			WhSkuInventoryCommand inv = list.get(i);
			Double oldQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(inv.getUuid(), wh.getId());
			if (count.compareTo(inv.getOnHandQty()) == -1) {
				boolean b = occupyInv(qty, oldQty, occupyCode, "ODO", odoLineId, inv.getId(), wh);
				if(!b){
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
				insertShareInventory(inv, inv.getOnHandQty() - count, logId);
				inv.setOnHandQty(inv.getOnHandQty() - count);
				count = 0.0;
			} else {
				boolean b = occupyInv(inv.getOnHandQty(), oldQty, occupyCode, "ODO", odoLineId, inv.getId(), wh);
				if(!b){
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
				count = count - inv.getOnHandQty();
				occupyNum = occupyNum + inv.getOnHandQty();
				// inv.setOnHandQty(inv.getOnHandQty() - count);
				list.remove(i--);
			}
			if (null != isStaticLocation && isStaticLocation) {
				staticLocationIds.add(inv.getLocationId().toString());
			}
			if (0 == new Double(0.0).compareTo(count)) {
				break;
			}
		}
		return occupyNum;
	}

	@Override
	public Double occupyInvUuidsByPalletContainer(List<WhSkuInventoryCommand> uuids, List<WhSkuInventoryCommand> allSkuInvs, Double qty, String occupyCode, Long odoLineId, Warehouse wh, String unitCodes, Boolean isStaticLocation, Set<String> staticLocationIds) {
		if (null == uuids || uuids.isEmpty()) {
			return 0.0;
		}
		Double actualQty = 0.0;
		for (WhSkuInventoryCommand invCmd : uuids) {
			Double onHandQty = invCmd.getSumOnHandQty();
			if (-1 != qty.compareTo(onHandQty)) {
				List<WhSkuInventoryCommand> invs = null;
				if (Constants.ALLOCATE_UNIT_PALLET.equals(unitCodes)) {
					List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
					invs = inventoryDao.findWhSkuInventoryByUuidList(wh.getId(), uuidList);
				} else {
					invCmd.setOuId(wh.getId());
					invs = inventoryDao.findInventoryByUuidAndCondition(invCmd);
				}
				
				Double occupyNum = 0.0;
				for (WhSkuInventoryCommand inv : invs) {
					Double oldQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(inv.getUuid(), wh.getId());
					boolean b = occupyInv(inv.getOnHandQty(), oldQty, occupyCode, "ODO", odoLineId, inv.getId(), wh);
					if(!b){
	                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
	                }
					// 在所有库存sku列表中扣除
					if (null != allSkuInvs) {
						for (int i = 0; i < allSkuInvs.size(); i++) {
							WhSkuInventoryCommand skuInv = allSkuInvs.get(i);
							if (skuInv.getId().equals(inv.getId())) {
								allSkuInvs.remove(i);
								break;
							}
						}
					}
					if (null != isStaticLocation && isStaticLocation) {
						staticLocationIds.add(inv.getLocationId().toString());
					}
					occupyNum += inv.getOnHandQty();
				}
				qty -= occupyNum;
				actualQty += occupyNum;
			}
		}
		return actualQty;
	}

	@Override
	public Double occupyInvUuids(List<WhSkuInventoryCommand> uuids, List<WhSkuInventoryCommand> allSkuInvs, Double qty, String occupyCode, Long odoLineId, Warehouse wh, String allocateUnitContainer, Boolean isStaticLocation, Set<String> staticLocationIds) {
		if (null == uuids || uuids.isEmpty()) {
			return 0.0;
		}
		Double actualQty = 0.0;
		Double count = qty;
		for (WhSkuInventoryCommand invCmd : uuids) {
			invCmd.setOuId(wh.getId());
			List<WhSkuInventoryCommand> invs = inventoryDao.findInventoryByUuidAndCondition(invCmd);
			for (WhSkuInventoryCommand inv : invs) {
				Double oldQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(inv.getUuid(), wh.getId());
				
				if (-1 == count.compareTo(inv.getOnHandQty())) {
					boolean b = occupyInv(qty, oldQty, occupyCode, "ODO", odoLineId, inv.getId(), wh);
					if(!b){
	                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
	                }
					insertShareInventory(inv, inv.getOnHandQty() - count, logId);
					count = 0.0;
					actualQty = qty;
					// 在所有库存sku列表中扣除
					if (null != allSkuInvs) {
						for (int i = 0; i < allSkuInvs.size(); i++) {
							WhSkuInventoryCommand skuInv = allSkuInvs.get(i);
							if (skuInv.getId().equals(inv.getId())) {
								skuInv.setOnHandQty(skuInv.getOnHandQty() - count);
								break;
							}
						}
					}
				} else {
					boolean b = occupyInv(inv.getOnHandQty(), oldQty, occupyCode, "ODO", odoLineId, inv.getId(), wh);
					if(!b){
	                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
	                }
					count -= inv.getOnHandQty();
					actualQty += inv.getOnHandQty();
					// 在所有库存sku列表中扣除
					if (null != allSkuInvs) {
						for (int i = 0; i < allSkuInvs.size(); i++) {
							WhSkuInventoryCommand skuInv = allSkuInvs.get(i);
							if (skuInv.getId().equals(inv.getId())) {
								allSkuInvs.remove(i);
								break;
							}
						}
					}
				}
				if (null != isStaticLocation && isStaticLocation) {
					staticLocationIds.add(inv.getLocationId().toString());
				}
			}
			if (0 == new Double(0.0).compareTo(count)) {
				break;
			}
		}
		return actualQty;
	}
	
}
