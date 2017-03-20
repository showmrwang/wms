package com.baozun.scm.primservice.whoperation.command.warehouse.inventory;

import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class HardAllocationCommand extends BaseCommand {
	
	private static final long serialVersionUID = -9173255848798908227L;
	
	/** 占用数量 */
	private Double occupyQty = 0.0d;
	/** 容器占用数量 */
	private Double containerQty = 0.0d;
	/** 记录静态库位可超分配的库位id */
	private Set<String> staticLocationIds;
	/** 记录整托占用容器ids */
	private Set<String> trayIds;
	/** 记录整箱占用容器ids */
	private Set<String> packingCaseIds;
	
	public Double getOccupyQty() {
		return occupyQty;
	}
	public void setOccupyQty(Double occupyQty) {
		this.occupyQty = occupyQty;
	}
	public Double getContainerQty() {
		return containerQty;
	}
	public void setContainerQty(Double containerQty) {
		this.containerQty = containerQty;
	}
	public Set<String> getStaticLocationIds() {
		return staticLocationIds;
	}
	public void setStaticLocationIds(Set<String> staticLocationIds) {
		this.staticLocationIds = staticLocationIds;
	}
	public Set<String> getTrayIds() {
		return trayIds;
	}
	public void setTrayIds(Set<String> trayIds) {
		this.trayIds = trayIds;
	}
	public Set<String> getPackingCaseIds() {
		return packingCaseIds;
	}
	public void setPackingCaseIds(Set<String> packingCaseIds) {
		this.packingCaseIds = packingCaseIds;
	}
	
}
