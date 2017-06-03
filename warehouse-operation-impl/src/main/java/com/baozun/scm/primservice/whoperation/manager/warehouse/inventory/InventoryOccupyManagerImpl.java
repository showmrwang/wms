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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.baseservice.sac.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.HardAllocationCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
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
    public Double hardAllocateOccupy(List<WhSkuInventoryCommand> skuInvs, Double qty, String occupyCode, Long odoLineId, String occupySource, Warehouse wh) {
		if (null == skuInvs || skuInvs.isEmpty()) {
			return 0.0;
		}
		Long ouId = wh.getId();
		Double occupyNum = 0.0;	// 实际占用数量
		Double count = qty;		// 传入的计划占用量
		for (WhSkuInventoryCommand inv : skuInvs) {
			// Double oldQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(inv.getUuid(), ouId);
			// 可用数量:在库数量减去已分配的数量
			Double useableQty = inventoryDao.getUseableQtyByUuid(inv.getUuid(), ouId);
			if (-1 != Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
				continue;
			}
			if (-1 == count.compareTo(inv.getOnHandQty())) {
				boolean b = occupyInv(inv.getId(), count, occupyCode, occupySource, odoLineId, wh);
				if(!b){
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
				insertShareInventory(inv, inv.getOnHandQty() - count, logId);
				return qty;
			} else {
				boolean b = occupyInv(inv.getId(), inv.getOnHandQty(), occupyCode, occupySource, odoLineId, wh);
				if(!b){
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
				count = count - inv.getOnHandQty();
				occupyNum = occupyNum + inv.getOnHandQty();
			}
		}
		return occupyNum;
	}
    
    private boolean occupyInv(Long invId, Double qty, String occupyCode, String occupySource, Long odoLineId, Warehouse wh) {
    	boolean flag = false;
		WhSkuInventory skuInv = inventoryDao.findWhSkuInventoryById(invId, wh.getId());
		if (skuInv.getOnHandQty().compareTo(qty) != 0) {
		    // 占用数量跟实际库存数量不一致
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
		skuInv.setOnHandQty(qty);
		skuInv.setOccupationCode(occupyCode);
		skuInv.setOccupationCodeSource(occupySource);
		skuInv.setOccupationLineId(odoLineId);
		int num = inventoryDao.saveOrUpdateByVersion(skuInv);
		if (1 == num) {
			flag = true;
		}
    	return flag;
    }
    
    private boolean subtractInv(Long invId, Double qty, String occupyCode, String occupySource, Warehouse wh) {
    	boolean flag = false;
    	WhSkuInventory skuInv = inventoryDao.findWhSkuInventoryById(invId, wh.getId());
    	Double updateQty = skuInv.getOnHandQty() - qty;
    	if (Constants.DEFAULT_DOUBLE.compareTo(updateQty) == 1) {
            // 更新的库存数量小于0
    	    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
		skuInv.setOnHandQty(updateQty);
		int num = inventoryDao.saveOrUpdateByVersion(skuInv);
		if (1 == num) {
			flag = true;
		}
		return flag;
	}

	@Override
	public Double hardAllocateListOccupy(List<WhSkuInventoryCommand> list, Double qty, String occupyCode, Long odoLineId, String occupySource, Warehouse wh, Boolean isStaticLocation, Set<String> staticLocationIds) {
		if (null == list || list.isEmpty()) {
			return 0.0;
		}
		Long ouId = wh.getId();
		Double occupyNum = 0.0;	// 实际占用数量
		Double count = qty;		// 传入的计划占用量
		for (int i = 0; i < list.size(); i++) {
			WhSkuInventoryCommand inv = list.get(i);
			// Double oldQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(inv.getUuid(), ouId);
			// 可用数量:在库数量减去已分配的数量
			Double useableQty = inventoryDao.getUseableQtyByUuid(inv.getUuid(), ouId);
			if (-1 != Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
				list.remove(i--);
				continue;
			}
			// OnHandQty <= useableQty
			if (1 != inv.getOnHandQty().compareTo(useableQty)) {
				if (-1 == count.compareTo(inv.getOnHandQty())) {
					// 扣减原来的库存的数量
					boolean b = subtractInv(inv.getId(), count, occupyCode, occupySource, wh);
					if(!b){
						throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
					}
					// 插入被占用的库存信息
					insertOccupyInventory(inv, count, occupyCode, odoLineId, occupySource, logId);
					inv.setOnHandQty(inv.getOnHandQty() - count);
					occupyNum = occupyNum + count;
					count = 0.0;
				} else {
					boolean b = occupyInv(inv.getId(), inv.getOnHandQty(), occupyCode, occupySource, odoLineId, wh);
					if(!b){
						throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
					}
					count = count - inv.getOnHandQty();
					occupyNum = occupyNum + inv.getOnHandQty();
					// inv.setOnHandQty(inv.getOnHandQty() - count);
					list.remove(i--);
				}
			} else {
				// count < useableQty
				if (-1 == count.compareTo(useableQty)) {
					// 扣减原来的库存的数量
					boolean b = subtractInv(inv.getId(), count, occupyCode, occupySource, wh);
					if(!b){
						throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
					}
					// 插入被占用的库存信息
					insertOccupyInventory(inv, count, occupyCode, odoLineId, occupySource, logId);
					inv.setOnHandQty(inv.getOnHandQty() - count);
					occupyNum = occupyNum + count;
					count = 0.0;
				} else {
					boolean b = subtractInv(inv.getId(), useableQty, occupyCode, occupySource, wh);
					if(!b){
						throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
					}
					// 插入被占用的库存信息
					insertOccupyInventory(inv, useableQty, occupyCode, odoLineId, occupySource, logId);
					inv.setOnHandQty(inv.getOnHandQty() - useableQty);
					occupyNum = occupyNum + useableQty;
					count = count - useableQty;
				}
			}
			
			if (null != isStaticLocation && isStaticLocation && null != staticLocationIds) {
				staticLocationIds.add(inv.getLocationId().toString());
			}
			if (0 == Constants.DEFAULT_DOUBLE.compareTo(count)) {
				break;
			}
		}
		return occupyNum;
	}
	
	@Override
	public HardAllocationCommand hardAllocateListOccupyNew(List<WhSkuInventoryCommand> list, List<String> units, Double qty, Long skuId, String occupyCode, Long odoLineId, String occupySource, Warehouse wh, Boolean isStaticLocation, String logId) {
		if (null == list || list.isEmpty()) {
			return new HardAllocationCommand();
		}
		Long ouId = wh.getId();
		Double occupyNum = 0.0;	// 实际占用数量
		Double containerNum = 0.0;	// 容器占用数量
		// 记录静态库位可超分配的库位id
		Set<String> staticLocationIds = new HashSet<String>();
		// 记录整托占用容器ids
		Set<String> trayIds = new HashSet<String>();
		// 记录整箱占用容器ids
		Set<String> packingCaseIds = new HashSet<String>();
		for (int i = 0; i < list.size(); i++) {
			WhSkuInventoryCommand inv = list.get(i);
			// 策略中包含托盘
			if (units.contains(Constants.ALLOCATE_UNIT_TP) && null != inv.getOuterContainerId()) {
				List<WhSkuInventoryCommand> skuInvList = inventoryDao.findUseableInventoryByOuterContainerId(inv.getOuterContainerId(), skuId, ouId);
				Double useableQty = this.sumContainerInventoryQuantity(skuInvList, ouId);
				if (qty.compareTo(useableQty) != -1 && Constants.DEFAULT_DOUBLE.compareTo(useableQty) == -1) {
					// 占用数量大于等于容器数量
					Double num = this.occupyInvByPalletContainer(skuInvList, qty, useableQty, skuId, occupyCode, odoLineId, occupySource, wh, logId, isStaticLocation, staticLocationIds, trayIds, null);
					occupyNum += num;
					containerNum += num;
					qty -= num;
					this.deleteSkuInvs(list, skuInvList);
					i--;
					if (Constants.DEFAULT_DOUBLE.compareTo(qty) == 0) {
						break;
					}
					continue;
				}
			}
			// 策略中包含货箱
			if (units.contains(Constants.ALLOCATE_UNIT_HX) && null != inv.getInsideContainerId()) {
				List<WhSkuInventoryCommand> skuInvList = inventoryDao.findUseableInventoryByInsideContainerId(inv.getInsideContainerId(), skuId, ouId);
				Double useableQty = this.sumContainerInventoryQuantity(skuInvList, ouId);
				if (qty.compareTo(useableQty) != -1 && Constants.DEFAULT_DOUBLE.compareTo(useableQty) == -1) {
					// 占用数量大于等于容器数量
					Double num = this.occupyInvByPalletContainer(skuInvList, qty, useableQty, skuId, occupyCode, odoLineId, occupySource, wh, logId, isStaticLocation, staticLocationIds, null, packingCaseIds);
					occupyNum += num;
					containerNum += num;
					qty -= num;
					this.deleteSkuInvs(list, skuInvList);
					i--;
					if (Constants.DEFAULT_DOUBLE.compareTo(qty) == 0) {
						break;
					}
					continue;
				}
			}
			// 策略中包含件
			if (units.contains(Constants.ALLOCATE_UNIT_PIECE)) {
				/*Double oldQty = null;
				if (null != wh.getIsTabbInvTotal() && wh.getIsTabbInvTotal()) {
					oldQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(inv.getUuid(), ouId);
				}*/
				// 可用数量:在库数量减去已分配的数量
				Double useableQty = inventoryDao.getUseableQtyByUuid(inv.getUuid(), ouId);
				if (-1 != Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
					list.remove(i--);
					continue;
				}
				// OnHandQty <= useableQty
				if (1 != inv.getOnHandQty().compareTo(useableQty)) {
					if (-1 == qty.compareTo(inv.getOnHandQty())) {
						// 扣减原来的库存的数量
						boolean b = subtractInv(inv.getId(), qty, occupyCode, occupySource, wh);
						if (!b) {
							throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
						}
						// 插入被占用的库存信息
						insertOccupyInventory(inv, qty, occupyCode, odoLineId, occupySource, logId);
						inv.setOnHandQty(inv.getOnHandQty() - qty);
						occupyNum = occupyNum + qty;
						qty = 0.0;
					} else {
						boolean b = occupyInv(inv.getId(), inv.getOnHandQty(), occupyCode, occupySource, odoLineId, wh);
						if (!b) {
							throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
						}
						qty = qty - inv.getOnHandQty();
						occupyNum = occupyNum + inv.getOnHandQty();
						// inv.setOnHandQty(inv.getOnHandQty() - count);
						list.remove(i--);
					}
				} else {
					// qty < useableQty
					if (-1 == qty.compareTo(useableQty)) {
						// 扣减原来的库存的数量
						boolean b = subtractInv(inv.getId(), qty, occupyCode, occupySource, wh);
						if (!b) {
							throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
						}
						// 插入被占用的库存信息
						insertOccupyInventory(inv, qty, occupyCode, odoLineId, occupySource, logId);
						inv.setOnHandQty(inv.getOnHandQty() - qty);
						occupyNum = occupyNum + qty;
						qty = 0.0;
					} else {
						boolean b = subtractInv(inv.getId(), useableQty, occupyCode, occupySource, wh);
						if (!b) {
							throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
						}
						// 插入被占用的库存信息
						insertOccupyInventory(inv, useableQty, occupyCode, odoLineId, occupySource, logId);
						// inv.setOnHandQty(inv.getOnHandQty() - useableQty);
						list.remove(i--);
						occupyNum = occupyNum + useableQty;
						qty = qty - useableQty;
					}
				}
				if (Constants.DEFAULT_DOUBLE.compareTo(qty) == 0) {
					break;
				}
			}
		}
		HardAllocationCommand command = new HardAllocationCommand();
		command.setOccupyQty(occupyNum);
		command.setContainerQty(containerNum);
		command.setStaticLocationIds(staticLocationIds);
		command.setTrayIds(trayIds);
		command.setPackingCaseIds(packingCaseIds);
		return command;
	}
	
	private Double occupyInvByPalletContainer(List<WhSkuInventoryCommand> skuInvList, Double qty, Double useableQty,
			Long skuId, String occupyCode, Long odoLineId, String occupySource, Warehouse wh, String logId,
			Boolean isStaticLocation, Set<String> staticLocationIds, Set<String> trayIds, Set<String> packingCaseIds) {
		// Long ouId = wh.getId();
		Double occupyNum = Constants.DEFAULT_DOUBLE;
		for (WhSkuInventoryCommand skuInv : skuInvList) {
			/*Double oldQty = null;
			if (null != wh.getIsTabbInvTotal() && wh.getIsTabbInvTotal()) {
				oldQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(skuInv.getUuid(), ouId);
			}*/
			if (1 != skuInv.getOnHandQty().compareTo(useableQty)) {
				boolean b = occupyInv(skuInv.getId(), skuInv.getOnHandQty(), occupyCode, occupySource, odoLineId, wh);
				if (!b) {
					throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
				}
				occupyNum += skuInv.getOnHandQty();
				useableQty -= skuInv.getOnHandQty();
				qty -= skuInv.getOnHandQty();

			} else {
				boolean b = subtractInv(skuInv.getId(), useableQty, occupyCode, occupySource, wh);
				if (!b) {
					throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
				}
				insertOccupyInventory(skuInv, useableQty, occupyCode, odoLineId, occupySource, logId);
				occupyNum += useableQty;
				qty -= useableQty;
				useableQty = 0.0;
			}
			if (null != isStaticLocation && isStaticLocation && null != staticLocationIds) {
				staticLocationIds.add(skuInv.getLocationId().toString());
			}
			if (null != trayIds) {
				trayIds.add(skuInv.getOuterContainerId().toString());
			} else if (null != packingCaseIds) {
				packingCaseIds.add(skuInv.getInsideContainerId().toString());
			}
			if (Constants.DEFAULT_DOUBLE.compareTo(useableQty) == 0) {
				break;
			}
		}
		return occupyNum;
	}
	
	private Double sumContainerInventoryQuantity(List<WhSkuInventoryCommand> skuInvList, Long ouId) {
		Double qty = Constants.DEFAULT_DOUBLE;
		if (null == skuInvList || skuInvList.isEmpty()) {
			return qty;
		}
		Set<String> uuidSet = new HashSet<String>();
		for (WhSkuInventoryCommand skuInv : skuInvList) {
			uuidSet.add(skuInv.getUuid());
		}
		// 根据uuid查出已分配的库存并减去得到可用的库存
		qty = inventoryDao.getUseableQtyByUuidList(new ArrayList<String>(uuidSet), ouId);
		return qty;
	}
	
	private void deleteSkuInvs(List<WhSkuInventoryCommand> list, List<WhSkuInventoryCommand> deleteList) {
		for (WhSkuInventoryCommand deleteInv : deleteList) {
			for (int i = 0; i < list.size(); i++) {
				WhSkuInventoryCommand skuInv = list.get(i);
				if (skuInv.getId().equals(deleteInv.getId())) {
					list.remove(i);
					break;
				}
			}
		}
	}

	@Override
	public Double occupyInvUuidsByPalletContainer(List<WhSkuInventoryCommand> uuids, Double qty, String occupyCode, Long odoLineId, String occupySource, Warehouse wh, String unitCodes, List<WhSkuInventoryCommand> allSkuInvs, Boolean isStaticLocation, Set<String> staticLocationIds, Set<String> trayIds, Set<String> packingCaseIds) {
		if (null == uuids || uuids.isEmpty()) {
			return 0.0;
		}
		Long ouId = wh.getId();
		Double actualQty = 0.0;
		for (WhSkuInventoryCommand invCmd : uuids) {
			// Double onHandQty = invCmd.getSumOnHandQty();
			List<String> uuidList = Arrays.asList(invCmd.getUuid().split(","));
			// 可用数量:在库数量减去已分配的数量
			Double useableQty = inventoryDao.getUseableQtyByUuidList(uuidList, ouId);
			if (-1 != qty.compareTo(useableQty) && -1 == Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
				List<WhSkuInventoryCommand> invs = inventoryDao.findWhSkuInventoryByUuidList(ouId, uuidList);
				
				Double occupyNum = 0.0;
				for (WhSkuInventoryCommand inv : invs) {
					// Double oldQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(inv.getUuid(), ouId);
					// OnHandQty <= useableQty
					if (1 != inv.getOnHandQty().compareTo(useableQty)) {
						boolean b = occupyInv(inv.getId(), inv.getOnHandQty(), occupyCode, occupySource, odoLineId, wh);
						if(!b){
		                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
		                }
						occupyNum += inv.getOnHandQty();
						useableQty -= inv.getOnHandQty();
						qty -= inv.getOnHandQty();
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
					} else {
						boolean b = subtractInv(inv.getId(), useableQty, occupyCode, occupySource, wh);
						if(!b){
		                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
		                }
						insertOccupyInventory(inv, useableQty, occupyCode, odoLineId, occupySource, logId);
						occupyNum += useableQty;
						qty -= useableQty;
						useableQty = 0.0;
						// 这些uuid的可用量没有了,在所有库存sku列表中扣除
						if (null != allSkuInvs) {
							for (int i = 0; i < allSkuInvs.size(); i++) {
								WhSkuInventoryCommand skuInv = allSkuInvs.get(i);
								if (uuidList.contains(skuInv.getUuid())) {
									allSkuInvs.remove(i--);
								}
							}
						}
					}
					
					if (null != isStaticLocation && isStaticLocation && null != staticLocationIds) {
						staticLocationIds.add(inv.getLocationId().toString());
					}
					if (Constants.ALLOCATE_UNIT_TP.equals(unitCodes) && null != trayIds) {
						trayIds.add(inv.getOuterContainerId().toString());
					} else if (Constants.ALLOCATE_UNIT_HX.equals(unitCodes) && null != packingCaseIds) {
						packingCaseIds.add(inv.getInsideContainerId().toString());
					}
					if (0 == Constants.DEFAULT_DOUBLE.compareTo(useableQty)) {
						break;
					}
				}
				actualQty += occupyNum;
				if (0 == Constants.DEFAULT_DOUBLE.compareTo(qty)) {
					break;
				}
			}
		}
		return actualQty;
	}

	@Override
	public Double occupyInvUuids(List<WhSkuInventoryCommand> uuids, Double qty, String occupyCode, Long odoLineId, String occupySource, Warehouse wh, String allocateUnitContainer, List<WhSkuInventoryCommand> allSkuInvs, Boolean isStaticLocation, Set<String> staticLocationIds) {
		if (null == uuids || uuids.isEmpty()) {
			return 0.0;
		}
		Long ouId = wh.getId();
		Double actualQty = 0.0;
		Double count = qty;
		for (WhSkuInventoryCommand invCmd : uuids) {
			Double sumOnHandQty = invCmd.getSumOnHandQty();
			List<String> uuid = Arrays.asList(invCmd.getUuid().split(","));
			if (-1 != Constants.DEFAULT_DOUBLE.compareTo(sumOnHandQty)) {
				if (null != allSkuInvs) {
					for (int i = 0; i < allSkuInvs.size(); i++) {
						WhSkuInventoryCommand skuInv = allSkuInvs.get(i);
						if (uuid.contains(skuInv.getUuid())) {
							allSkuInvs.remove(i--);
						}
					}
				}
				continue;
			}
			List<WhSkuInventoryCommand> invs = inventoryDao.findWhSkuInventoryByUuidList(ouId, uuid);
			for (WhSkuInventoryCommand inv : invs) {
				// Double oldQty = whSkuInventoryLogDao.sumSkuInvOnHandQty(inv.getUuid(), ouId);
				Double useableQty = inventoryDao.getUseableQtyByUuid(inv.getUuid(), ouId);
				// OnHandQty <= useableQty
				if (1 != inv.getOnHandQty().compareTo(useableQty)) {
					if (-1 == count.compareTo(inv.getOnHandQty())) {
						boolean b = subtractInv(inv.getId(), count, occupyCode, occupySource, wh);
						if(!b){
							throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
						}
						insertOccupyInventory(inv, count, occupyCode, odoLineId, occupySource, logId);
						count = 0.0;
						actualQty = qty;
						minusAllSkuInv(allSkuInvs, inv.getId(), count);
					} else {
						boolean b = occupyInv(inv.getId(), inv.getOnHandQty(), occupyCode, occupySource, odoLineId, wh);
						if(!b){
							throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
						}
						count -= inv.getOnHandQty();
						actualQty += inv.getOnHandQty();
						// 在所有库存sku列表中扣除
						minusAllSkuInv(allSkuInvs, inv.getId(), null);
					}
				} else {
					if (-1 == count.compareTo(useableQty)) {
						boolean b = subtractInv(inv.getId(), count, occupyCode, occupySource, wh);
						if(!b){
							throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
						}
						insertOccupyInventory(inv, count, occupyCode, odoLineId, occupySource, logId);
						count = 0.0;
						actualQty = qty;
						// 在所有库存sku列表中扣除
						minusAllSkuInv(allSkuInvs, inv.getId(), count);
					} else {
						boolean b = subtractInv(inv.getId(), useableQty, occupyCode, occupySource, wh);
						if(!b){
							throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
						}
						insertOccupyInventory(inv, useableQty, occupyCode, odoLineId, occupySource, logId);
						count -= useableQty;
						actualQty += useableQty;
						minusAllSkuInv(allSkuInvs, inv.getId(), count);
					}
				}
				if (null != isStaticLocation && isStaticLocation && null != staticLocationIds) {
					staticLocationIds.add(inv.getLocationId().toString());
				}
				if (0 == Constants.DEFAULT_DOUBLE.compareTo(count)) {
					break;
				}
			}
			if (0 == Constants.DEFAULT_DOUBLE.compareTo(count)) {
				break;
			}
		}
		return actualQty;
	}

	private void minusAllSkuInv(List<WhSkuInventoryCommand> allSkuInvs, Long invId, Double count) {
		if (null != allSkuInvs) {
			for (int i = 0; i < allSkuInvs.size(); i++) {
				WhSkuInventoryCommand skuInv = allSkuInvs.get(i);
				if (skuInv.getId().equals(invId)) {
					if (null != count) {
						skuInv.setOnHandQty(skuInv.getOnHandQty() - count);
					} else {
						allSkuInvs.remove(i);
					}
					break;
				}
			}
		}
	}
	
}
