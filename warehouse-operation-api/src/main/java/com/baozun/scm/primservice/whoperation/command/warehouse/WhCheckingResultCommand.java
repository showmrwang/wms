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
package com.baozun.scm.primservice.whoperation.command.warehouse;

import java.util.List;
import java.util.Set;

import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundConsumable;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

public class WhCheckingResultCommand extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 7075809126455313703L;
    
    //columns START
    /** 功能ID */
    private Long functionId;
    /** 对应组织ID */
    private Long ouId;
    /** 用户Id */
    private Long userId;
    //columns END

    /**==============================复核保存数据=============================*/
    /** 更新复核头信息 */
    WhCheckingCommand orgCheckingCommand;
    /** 创建出库箱信息 */
    WhOutboundbox whOutboundbox;
    /** 创建出库箱装箱明细信息 */
    List<WhOutboundboxLine> outboundboxLineList;
    /** 更新复核明细信息 */
    Set<WhCheckingLineCommand> toUpdateCheckingLineSet;
    /** 创建出库箱库存 */
    List<WhSkuInventory> outboundboxSkuInvList;
    /** 更新原复核箱库存 */
    Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet;
    /** 创建包裹计重信息 */
    WhOdoPackageInfoCommand odoPackageInfoCommand;
    /** 耗材信息 */
    WhOutboundConsumable whOutboundConsumable;
    /** 耗材库存 */
    WhSkuInventoryCommand consumableSkuInv;
    /** 出库单 */
    WhOdo whOdo;
    /** 待释放的容器 小车/周转箱 */
    Container container;
    /** 待释放播种墙 */
    WhOutboundFacility seedingFacility;
    /** 出库单交接运单信息 */
    WhOdodeliveryInfo whOdodeliveryInfo;
    /** 更新已复核的SN/残次信息 */
    List<WhSkuInventorySnCommand> checkedSnInvList;



    
    public Long getFunctionId() {
        return functionId;
    }
    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }
    public Long getOuId() {
        return ouId;
    }
    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public WhCheckingCommand getOrgCheckingCommand() {
        return orgCheckingCommand;
    }

    public void setOrgCheckingCommand(WhCheckingCommand orgCheckingCommand) {
        this.orgCheckingCommand = orgCheckingCommand;
    }

    public WhOutboundbox getWhOutboundbox() {
        return whOutboundbox;
    }

    public void setWhOutboundbox(WhOutboundbox whOutboundbox) {
        this.whOutboundbox = whOutboundbox;
    }

    public Set<WhCheckingLineCommand> getToUpdateCheckingLineSet() {
        return toUpdateCheckingLineSet;
    }

    public void setToUpdateCheckingLineSet(Set<WhCheckingLineCommand> toUpdateCheckingLineSet) {
        this.toUpdateCheckingLineSet = toUpdateCheckingLineSet;
    }

    public List<WhOutboundboxLine> getOutboundboxLineList() {
        return outboundboxLineList;
    }

    public void setOutboundboxLineList(List<WhOutboundboxLine> outboundboxLineList) {
        this.outboundboxLineList = outboundboxLineList;
    }

    public List<WhSkuInventory> getOutboundboxSkuInvList() {
        return outboundboxSkuInvList;
    }

    public void setOutboundboxSkuInvList(List<WhSkuInventory> outboundboxSkuInvList) {
        this.outboundboxSkuInvList = outboundboxSkuInvList;
    }

    public Set<WhSkuInventory> getToUpdateOdoOrgSkuInvSet() {
        return toUpdateOdoOrgSkuInvSet;
    }

    public void setToUpdateOdoOrgSkuInvSet(Set<WhSkuInventory> toUpdateOdoOrgSkuInvSet) {
        this.toUpdateOdoOrgSkuInvSet = toUpdateOdoOrgSkuInvSet;
    }

    public WhSkuInventoryCommand getConsumableSkuInv() {
        return consumableSkuInv;
    }

    public void setConsumableSkuInv(WhSkuInventoryCommand consumableSkuInv) {
        this.consumableSkuInv = consumableSkuInv;
    }

    public WhOutboundConsumable getWhOutboundConsumable() {
        return whOutboundConsumable;
    }

    public void setWhOutboundConsumable(WhOutboundConsumable whOutboundConsumable) {
        this.whOutboundConsumable = whOutboundConsumable;
    }

    public WhOdoPackageInfoCommand getOdoPackageInfoCommand() {
        return odoPackageInfoCommand;
    }

    public void setOdoPackageInfoCommand(WhOdoPackageInfoCommand odoPackageInfoCommand) {
        this.odoPackageInfoCommand = odoPackageInfoCommand;
    }

    public WhOdo getWhOdo() {
        return whOdo;
    }

    public void setWhOdo(WhOdo whOdo) {
        this.whOdo = whOdo;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public WhOutboundFacility getSeedingFacility() {
        return seedingFacility;
    }

    public void setSeedingFacility(WhOutboundFacility seedingFacility) {
        this.seedingFacility = seedingFacility;
    }

    public WhOdodeliveryInfo getWhOdodeliveryInfo() {
        return whOdodeliveryInfo;
    }

    public void setWhOdodeliveryInfo(WhOdodeliveryInfo whOdodeliveryInfo) {
        this.whOdodeliveryInfo = whOdodeliveryInfo;
    }

    public List<WhSkuInventorySnCommand> getCheckedSnInvList() {
        return checkedSnInvList;
    }

    public void setCheckedSnInvList(List<WhSkuInventorySnCommand> checkedSnInvList) {
        this.checkedSnInvList = checkedSnInvList;
    }
}

