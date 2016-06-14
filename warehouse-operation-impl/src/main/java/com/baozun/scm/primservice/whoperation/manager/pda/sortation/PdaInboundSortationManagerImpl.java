package com.baozun.scm.primservice.whoperation.manager.pda.sortation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.pda.sortation.PdaInboundSortationCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

/**
 * PDA入库分拣manager
 * 
 * @author bin.hu
 * 
 */
@Service("pdaInboundSortationManager")
@Transactional
public class PdaInboundSortationManagerImpl extends BaseManagerImpl implements PdaInboundSortationManager {

    protected static final Logger log = LoggerFactory.getLogger(PdaInboundSortationManagerImpl.class);

    @Autowired
    private ContainerDao containerDao;
    @Autowired
    private RuleManager ruleManager;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;

    /**
     * 扫描容器号 验证容器号
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaInboundSortationCommand pdaScanContainer(PdaInboundSortationCommand pdaInboundSortationCommand) {
        log.info(this.getClass().getSimpleName() + ".pdaScanContainer method begin! logid: " + pdaInboundSortationCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[PdaInboundSortationCommand:{}]", pdaInboundSortationCommand.toString());
        }
        // 验证容器号为空
        if (StringUtil.isEmpty(pdaInboundSortationCommand.getContainerCode())) {
            log.warn("pdaScanContainer pdaInboundSortationCommand.getContainerCode() is null logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINERCODE_NULL);
        }
        // 查询对应容器数据
        ContainerCommand container = containerDao.getContainerByCode(pdaInboundSortationCommand.getContainerCode(), pdaInboundSortationCommand.getOuId());
        if (null == container) {
            // 容器信息不存在
            log.warn("pdaScanContainer container is null logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        // 验证容器Lifecycle是否有效
        if (container.getLifecycle().equals(BaseModel.LIFECYCLE_DISABLE)) {
            // 容器Lifecycle无效
            log.warn("pdaScanContainer container lifecycle error =" + container.getLifecycle() + " logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_LIFRCYCLE_ERROR);
        }
        // 如果容器Lifecycle为占用 判断是否状态为待上架/可用状态
        if (container.getLifecycle().equals(ContainerStatus.CONTAINER_LIFECYCLE_OCCUPIED)) {
            if (!container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_CAN_PUTAWAY) && !container.getStatus().equals(ContainerStatus.CONTAINER_STATUS_USABLE)) {
                log.warn("pdaScanContainer container status error =" + container.getStatus() + " logid: " + pdaInboundSortationCommand.getLogId());
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_STATUS_ERROR, new Object[] {container.getStatus()});
            }
        }
        pdaInboundSortationCommand.setContainerId(container.getId());// 保存容器ID
        // // 判断该容器是否有符合的入库分拣规则
        // RuleAfferCommand ruleAffer = new RuleAfferCommand();
        // ruleAffer.setLogId(pdaInboundSortationCommand.getLogId());
        // ruleAffer.setOuid(pdaInboundSortationCommand.getOuId());
        // ruleAffer.setAfferContainerCode(pdaInboundSortationCommand.getContainerCode());
        // ruleAffer.setRuleType(Constants.INBOUND_RULE);// 入库分拣规则
        // ruleAffer.setRuleId(pdaInboundSortationCommand.getRuleId());
        // RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        // if (!export.getUsableness()) {
        // // 如果没有对应规则 提示错误
        // log.warn("pdaScanContainer export.getUsableness() is false logid: " +
        // pdaInboundSortationCommand.getLogId());
        // throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_USABLENESS_FALSE);
        // }
        // pdaInboundSortationCommand.setRuleId(export.getWhInBoundRuleCommand().getId());
        // 验证容器是否存在库存信息
        List<String> containerList = new ArrayList<String>();
        containerList.add(pdaInboundSortationCommand.getContainerCode());
        List<WhSkuInventoryCommand> invList = whSkuInventoryDao.findWhSkuInventoryByContainerCode(pdaInboundSortationCommand.getOuId(), containerList);
        if (invList.size() == 0) {
            // 容器没有对应的库存信息
            log.warn("pdaScanContainer WhSkuInventory is null logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_INV_NULL);
        }
        log.info(this.getClass().getSimpleName() + ".pdaScanContainer method end! logid: " + pdaInboundSortationCommand.getLogId());
        return pdaInboundSortationCommand;
    }

    /**
     * 扫描SKU 验证SKU
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaInboundSortationCommand pdaScanSku(PdaInboundSortationCommand pdaInboundSortationCommand) {
        log.info(this.getClass().getSimpleName() + ".pdaScanSku method begin! logid: " + pdaInboundSortationCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[PdaInboundSortationCommand:{}]", pdaInboundSortationCommand.toString());
        }
        // 验证SKU是否为空
        if (StringUtil.isEmpty(pdaInboundSortationCommand.getSkuBarCode())) {
            log.warn("pdaScanSku pdaInboundSortationCommand.getSkuCode() is null logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SKUCODE_NULL);
        }
        // 验证此SKU是否存在该原始容器号对应的库存内
        List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.findWhSkuInventoryBySkuCodeAndContainerId(pdaInboundSortationCommand.getOuId(), pdaInboundSortationCommand.getSkuBarCode(), pdaInboundSortationCommand.getContainerId());
        if (skuInvList.size() == 0) {
            // 无对应库存记录
            log.warn("pdaScanSku pdaInboundSortationCommand.getSkuCode() in containerCode: " + pdaInboundSortationCommand.getContainerCode() + "inv is null logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SKUINV_NULL);
        }
        Long skuid = skuInvList.get(0).getSkuId();
        // 验证商品是否状态可用
        Sku sku = skuDao.findByIdShared(skuid, pdaInboundSortationCommand.getOuId());
        if (!sku.getLifecycle().equals(BaseModel.LIFECYCLE_NORMAL)) {
            // 商品状态不可用
            log.warn("pdaScanSku Sku lifecycle error =" + sku.getLifecycle() + " logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SKULIFRCYCLE_ERROR);
        }
        if (skuInvList.size() == 1) {
            // 如果只有1条数据 保存库存数据ID
            pdaInboundSortationCommand.setSkuInvId(skuInvList.get(0).getId());
        }
        if (skuInvList.size() > 1) {
            boolean b = true;
            // 如果对应库存记录>1 判断是不同店铺 相同SKU 相同SKU属性的库存
            WhSkuInventoryCommand command = skuInvList.get(0);// 比较对象
            for (WhSkuInventoryCommand inv : skuInvList) {
                if (!command.equals(inv)) {
                    // 如果不相同 直接跳出循环
                    b = false;
                    break;
                }
            }
            // 如果所有数据都是相同的 保存库存数据ID
            if (b) {
                pdaInboundSortationCommand.setSkuInvId(skuInvList.get(0).getId());
            }
        }
        pdaInboundSortationCommand.setSkuId(skuid);
        log.info(this.getClass().getSimpleName() + ".pdaScanSku method end! logid: " + pdaInboundSortationCommand.getLogId());
        return pdaInboundSortationCommand;
    }

    /**
     * 输入SKU 移入数量 验证SKU 移入数量
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaInboundSortationCommand pdaScanSkuQty(PdaInboundSortationCommand pdaInboundSortationCommand) {
        log.info(this.getClass().getSimpleName() + ".pdaScanSkuQty method begin! logid: " + pdaInboundSortationCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[PdaInboundSortationCommand:{}]", pdaInboundSortationCommand.toString());
        }
        // 验证移入数量是否为空
        if (null == pdaInboundSortationCommand.getShiftInQty()) {
            log.warn("pdaScanSkuQty pdaInboundSortationCommand.getShiftInQty() is null logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SHIFTINQTY_NULL);
        }
        // 验证移入数量是否>=0
        if (pdaInboundSortationCommand.getShiftInQty() <= 0.0) {
            log.warn("pdaScanSkuQty pdaInboundSortationCommand.getShiftInQty() <= 0.0 logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SHIFTINQTY_ERROR);
        }
        // 验证移入数量是否>待移出数量
        // 获取当前库存记录
        WhSkuInventory skuInv = whSkuInventoryDao.findWhSkuInventoryById(pdaInboundSortationCommand.getSkuInvId(), pdaInboundSortationCommand.getOuId());
        if (pdaInboundSortationCommand.getShiftInQty() > skuInv.getOnHandQty()) {
            log.warn("pdaScanSkuQty pdaInboundSortationCommand.getShiftInQty() > skuInv.getOnHandQty() logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SHIFTINOUTQTY_ERROR);
        }
        log.info(this.getClass().getSimpleName() + ".pdaScanSkuQty method end! logid: " + pdaInboundSortationCommand.getLogId());
        return pdaInboundSortationCommand;
    }

    /**
     * 验证扫描目标容器 并且保存对应入库分拣信息
     * 
     * @throws Exception
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaInboundSortationCommand pdaScanNewContainer(PdaInboundSortationCommand pdaInboundSortationCommand) throws Exception {
        log.info(this.getClass().getSimpleName() + ".pdaScanNewContainer method begin! logid: " + pdaInboundSortationCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[PdaInboundSortationCommand:{}]", pdaInboundSortationCommand.toString());
        }
        // 验证目标容器号是否为空
        if (StringUtil.isEmpty(pdaInboundSortationCommand.getTargetContainerCode())) {
            log.warn("pdaScanNewContainer pdaInboundSortationCommand.getTargetContainerCode() is null logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINERCODE_NULL);
        }
        if (!StringUtil.isEmpty(pdaInboundSortationCommand.getTargetContainerCodeSelect())) {
            // 验证确认容器号是否=目标容器号
            if (!pdaInboundSortationCommand.getTargetContainerCode().equals(pdaInboundSortationCommand.getTargetContainerCodeSelect())) {
                log.warn("pdaScanNewContainer pdaInboundSortationCommand.getTargetContainerCode().equals(pdaInboundSortationCommand.getTargetContainerCodeSelect()) false logid: " + pdaInboundSortationCommand.getLogId());
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINERCODE_NULL);
            }
        }
        // 查询对应目标容器数据
        ContainerCommand container = containerDao.getContainerByCode(pdaInboundSortationCommand.getTargetContainerCode(), pdaInboundSortationCommand.getOuId());
        if (null == container) {
            // 容器信息不存在
            log.warn("pdaScanNewContainer container is null logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
        }
        // 验证目标容器状态是否可用
        if (!container.getLifecycle().equals(BaseModel.LIFECYCLE_NORMAL)) {
            // 目标容器状态不可用
            log.warn("pdaScanNewContainer container lifecycle error =" + container.getLifecycle() + " logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_LIFRCYCLE_ERROR, new Object[] {container.getLifecycle()});
        }
        // 验证目标容器is_full(是否装满)=true
        if (container.getIsFull()) {
            // 提示此容器已装满
            log.warn("pdaScanNewContainer container container.getIsFull() true logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_ISFULL_ERROR);
        }
        // 保留目标容器ID
        pdaInboundSortationCommand.setNewContainerId(container.getId());
        // 对库存数据进行拆分处理
        pdaScanNewContainerInsertSkuInv(pdaInboundSortationCommand);
        log.info(this.getClass().getSimpleName() + ".pdaScanNewContainer method end! logid: " + pdaInboundSortationCommand.getLogId());
        return pdaInboundSortationCommand;
    }

    /**
     * 入库分拣扫描目标容器号 对库存数据进行拆分处理
     * 
     * @throws Exception
     */
    private PdaInboundSortationCommand pdaScanNewContainerInsertSkuInv(PdaInboundSortationCommand pdaInboundSortation) throws Exception {
        boolean b = false;
        // 获取原始容器号对应库存信息
        WhSkuInventory skuInv = whSkuInventoryDao.findWhSkuInventoryById(pdaInboundSortation.getSkuInvId(), pdaInboundSortation.getOuId());
        // 验证移入数量是否>库存在库库存数量
        if (pdaInboundSortation.getShiftInQty() > skuInv.getOnHandQty()) {
            log.warn("pdaScanNewContainer pdaInboundSortationCommand.getShiftInQty() > skuInv.getOnHandQty() logid: " + pdaInboundSortation.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SHIFTINOUTQTY_ERROR);
        }
        // 新的库存记录
        WhSkuInventory newSkuInv = new WhSkuInventory();
        // 原始库存记录复制给新库存记录
        BeanUtils.copyProperties(skuInv, newSkuInv);
        newSkuInv.setId(null);
        // 外部容器号=原始容器号
        newSkuInv.setOuterContainerId(pdaInboundSortation.getContainerId());
        // 内部容器号=目标容器号
        newSkuInv.setInsideContainerId(pdaInboundSortation.getNewContainerId());
        // 在库库存=移入数量
        newSkuInv.setOnHandQty(pdaInboundSortation.getShiftInQty());
        newSkuInv.setAllocatedQty(0.0);
        newSkuInv.setToBeFilledQty(0.0);
        newSkuInv.setFrozenQty(0.0);
        newSkuInv.setLastModifyTime(new Date());
        String uuid = SkuInventoryUuid.invUuid(newSkuInv);
        newSkuInv.setUuid(uuid);
        // 查询此UUID在库存表是否存在
        WhSkuInventory skuInvUuid = whSkuInventoryDao.findWhSkuInventoryByUuid(pdaInboundSortation.getOuId(), uuid);
        if (null == skuInvUuid) {
            // 如果没有对应数据 插入新的数据
            whSkuInventoryDao.insert(newSkuInv);
            // 插入操作日志
            insertGlobalLog(Constants.GLOBAL_LOG_INSERT, newSkuInv, pdaInboundSortation.getOuId(), pdaInboundSortation.getUserId(), null, null);
        } else {
            // 更新原有库存记录在库库存数量
            b = updateSkuInvOnHandQty(newSkuInv, pdaInboundSortation, uuid);
            if (!b) {
                // 修改原来库存记录失败
                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
            }
        }
        // 判断是否有UUID传入
        if (!StringUtil.isEmpty(pdaInboundSortation.getUuid())) {
            // 如果有UUID 证明有SN/残次信息录入 需要更新对应的UUID
            b = updateSkuInvSnUuid(skuInv, pdaInboundSortation, uuid);
            if (!b) {
                // 修改SN/残次UUID失败后
                throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
            }
        }
        // 修改原始容器号在库库存
        b = updateSkuInvOnHandQtyForOriginal(newSkuInv, pdaInboundSortation);
        if (!b) {
            // 修改原始容器号在库库存失败
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        // 根据sku+内部容器号 查找容器库存数据 location is null 查询多条对应的库存数据
        List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.findWhSkuInventoryBySkuIdAndContainerid(pdaInboundSortation.getOuId(), pdaInboundSortation.getSkuId(), pdaInboundSortation.getContainerId(), null);
        if (skuInvList.size() == 0) {
            // 如果原始容器号库存为0 需要更新原始容器号对应的新容器号 把外部容器号制空 并且如果新容器号is_full(是否装满) = true -->false
            // 新容器号库存 外部容器号制空
            List<WhSkuInventoryCommand> newSkuInvList = whSkuInventoryDao.findWhSkuInventoryBySkuIdAndContainerid(pdaInboundSortation.getOuId(), pdaInboundSortation.getSkuId(), null, pdaInboundSortation.getContainerId());
            for (WhSkuInventoryCommand inv : newSkuInvList) {
                b = updateSkuInvOuterContainerIsNull(inv, pdaInboundSortation);
                if (!b) {
                    // 修改新容器号库存 制空外部容器号失败
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
                // 修改容器号is_full = false
                b = updateSkuInvContainerIsFull(inv.getInsideContainerId(), inv.getOuId(), pdaInboundSortation.getUserId(), pdaInboundSortation.getLogId(), false);
                if (!b) {
                    // 修改容器is_full error
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
            }
            // 此原始容器号已完成全部商品分拣
            pdaInboundSortation.setIsDone(true);
        } else {
            // 如果还有对应库存记录
            // 判断是否点击容器已满
            if (pdaInboundSortation.getIsFull()) {
                // 更新对应目标容器is_full = true
                b = updateSkuInvContainerIsFull(pdaInboundSortation.getNewContainerId(), pdaInboundSortation.getOuId(), pdaInboundSortation.getUserId(), pdaInboundSortation.getLogId(), true);
                if (!b) {
                    // 修改目标容器is_full error
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
            }
        }
        return pdaInboundSortation;
    }

    /***
     * 更新原有库存记录在库库存数量
     * 
     * @param newSkuInv
     * @param pdaInboundSortation
     * @param uuid
     * @return
     * @throws Exception
     */
    private boolean updateSkuInvOnHandQty(WhSkuInventory newSkuInv, PdaInboundSortationCommand pdaInboundSortation, String uuid) throws Exception {
        boolean b = false;
        for (int i = 1; i <= 5; i++) {
            // 每次尝试更新5次 避免并发情况
            // 延迟200毫秒
            Thread.sleep(200);
            // 查询对应相同UUID库存记录
            WhSkuInventory oldSkuInv = whSkuInventoryDao.findWhSkuInventoryByUuid(pdaInboundSortation.getOuId(), uuid);
            // 在库库存数量累加
            oldSkuInv.setOnHandQty(oldSkuInv.getOnHandQty() + newSkuInv.getOnHandQty());
            int count = whSkuInventoryDao.saveOrUpdateByVersion(oldSkuInv);
            if (count == 0) {
                // 修改失败 继续执行
                log.warn("pdaScanNewContainer updateSkuInvOnHandQty error count= " + i + " logid: " + pdaInboundSortation.getLogId());
                continue;
            } else {
                // 修改成功 跳出循环 返回true
                b = true;
                break;
            }
        }
        return b;
    }

    /**
     * 更新原始容器号的在库库存
     * 
     * @param newSkuInv
     * @param pdaInboundSortation
     * @param uuid
     * @return
     * @throws Exception
     */
    private boolean updateSkuInvOnHandQtyForOriginal(WhSkuInventory newSkuInv, PdaInboundSortationCommand pdaInboundSortation) throws Exception {
        boolean b = false;
        int count = 0;
        String dml = "";
        for (int i = 1; i <= 5; i++) {
            // 每次尝试更新5次 避免并发情况
            // 延迟200毫秒
            Thread.sleep(200);
            // 获取原始容器号对应库存信息
            WhSkuInventory skuInv = whSkuInventoryDao.findWhSkuInventoryById(pdaInboundSortation.getSkuInvId(), pdaInboundSortation.getOuId());
            // 减在库库存数量
            Double onHandQty = skuInv.getOnHandQty() - newSkuInv.getOnHandQty();
            if (onHandQty.intValue() < 0) {
                // 验证原始容器库存-此次移入数量<0
                log.warn("pdaScanNewContainer updateSkuInvOnHandQtyForOriginal onHandQty.intValue() < 0 error  logid: " + pdaInboundSortation.getLogId());
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_ONHANDQTY_ERROR, new Object[] {skuInv.getOnHandQty(), newSkuInv.getOnHandQty()});
            }
            skuInv.setOnHandQty(onHandQty);
            // 判断原始容器号在库库存是否=0
            if (onHandQty.intValue() == 0) {
                // 删除原始容器号商品数据
                count = whSkuInventoryDao.deleteWhSkuInventoryById(skuInv.getId(), skuInv.getOuId());
                dml = Constants.GLOBAL_LOG_DELETE;
            } else {
                // 修改原始容器号商品在库库存
                count = whSkuInventoryDao.saveOrUpdateByVersion(skuInv);
                dml = Constants.GLOBAL_LOG_UPDATE;
            }
            if (count == 0) {
                // 修改失败 继续执行
                log.warn("pdaScanNewContainer updateSkuInvOnHandQtyForOriginal error count= " + i + " logid: " + pdaInboundSortation.getLogId());
                continue;
            } else {
                // 插入操作日志
                insertGlobalLog(dml, skuInv, pdaInboundSortation.getOuId(), pdaInboundSortation.getUserId(), null, null);
                b = true;
                break;
            }
        }
        return b;
    }

    /**
     * 更新SN/残次中的UUID
     * 
     * @param pdaInboundSortation
     * @param uuid
     * @return
     * @throws Exception
     */
    private boolean updateSkuInvSnUuid(WhSkuInventory skuInv, PdaInboundSortationCommand pdaInboundSortation, String uuid) throws Exception {
        boolean b = false;
        for (int i = 1; i <= 5; i++) {
            // 每次尝试更新5次 避免并发情况
            // 延迟200毫秒
            Thread.sleep(200);
            // 通过原始库存的UUID+SYS_UUID更新对应的UUID
            int count = whSkuInventorySnDao.updateWhSkuInventorySnUuid(pdaInboundSortation.getOuId(), skuInv.getUuid(), uuid, pdaInboundSortation.getUuid());
            if (count != pdaInboundSortation.getShiftInQty().intValue()) {
                // 判断修改的SN/残次信息是否=本次移入数量
                log.warn("pdaScanNewContainer updateSkuInvSnUuid error count= " + i + " sysUuid: " + pdaInboundSortation.getUuid() + " logid: " + pdaInboundSortation.getLogId());
                continue;
            } else {
                // 修改成功 跳出循环 返回true
                b = true;
                break;
            }
        }
        return b;
    }

    /***
     * 更新目标容器is_full = true/false
     * 
     * @param pdaInboundSortation
     * @return
     * @throws Exception
     */
    private boolean updateSkuInvContainerIsFull(Long containerId, Long ouid, Long userId, String logId, Boolean isFull) throws Exception {
        boolean b = false;
        for (int i = 1; i <= 5; i++) {
            // 每次尝试更新5次 避免并发情况
            // 延迟200毫秒
            Thread.sleep(200);
            // 通过目标容器ID查找容器信息
            Container c = containerDao.findByIdExt(containerId, ouid);
            c.setIsFull(isFull);
            c.setOperatorId(userId);
            int count = containerDao.saveOrUpdateByVersion(c);
            if (count == 0) {
                log.warn("pdaScanNewContainer updateSkuInvContainerIsFull error count= " + i + " containerId: " + containerId + " logid: " + logId);
                continue;
            } else {
                // 修改成功 跳出循环 返回true
                // 插入操作日志
                insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, c, ouid, userId, null, null);
                b = true;
                break;
            }
        }
        return b;
    }

    /***
     * 修改对应库存明细 把outer_container_id 设置为null
     * 
     * @param skuInv
     * @param pdaInboundSortation
     * @return
     * @throws Exception
     */
    private boolean updateSkuInvOuterContainerIsNull(WhSkuInventoryCommand skuInv, PdaInboundSortationCommand pdaInboundSortation) throws Exception {
        boolean b = false;
        for (int i = 1; i <= 5; i++) {
            // 每次尝试更新5次 避免并发情况
            // 延迟200毫秒
            Thread.sleep(200);
            WhSkuInventory inv = whSkuInventoryDao.findWhSkuInventoryById(skuInv.getId(), skuInv.getOuId());
            inv.setOuterContainerId(null);
            int count = whSkuInventoryDao.saveOrUpdateByVersion(inv);
            if (count == 0) {
                log.warn("pdaScanNewContainer updateSkuInvOuterContainerIsNull error count= " + i + " WhSkuInventoryId: " + inv.getId() + " logid: " + pdaInboundSortation.getLogId());
                continue;
            } else {
                // 修改成功 跳出循环 返回true
                // 插入操作日志
                insertGlobalLog(Constants.GLOBAL_LOG_UPDATE, inv, skuInv.getOuId(), pdaInboundSortation.getUserId(), null, null);
                b = true;
                break;
            }
        }
        return b;
    }

    /**
     * 封装多条库存属性 返回页面进行筛选
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaInboundSortationCommand pdaScanSkuInvAttr(PdaInboundSortationCommand pdaInboundSortationCommand) {
        log.info(this.getClass().getSimpleName() + ".pdaScanSkuInvAttr method begin! logid: " + pdaInboundSortationCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[PdaInboundSortationCommand:{}]", pdaInboundSortationCommand.toString());
        }
        // 原产地
        Map<String, String> cooMap = new HashMap<String, String>();
        // 生产日期
        Map<String, String> mfgMap = new HashMap<String, String>();
        // 失效日期
        Map<String, String> expMap = new HashMap<String, String>();
        // 批次号
        Map<String, String> batchNumMap = new HashMap<String, String>();
        // 库存属性1
        Map<String, String> invAttr1Map = new HashMap<String, String>();
        // 库存属性2
        Map<String, String> invAttr2Map = new HashMap<String, String>();
        // 库存属性3
        Map<String, String> invAttr3Map = new HashMap<String, String>();
        // 库存属性4
        Map<String, String> invAttr4Map = new HashMap<String, String>();
        // 库存属性5
        Map<String, String> invAttr5Map = new HashMap<String, String>();
        // 库存类型
        Map<String, String> invTypeMap = new HashMap<String, String>();
        // 库存状态
        Map<Integer, String> invStatusMap = new HashMap<Integer, String>();
        // 根据sku+内部容器号 查找容器库存数据 location is null 查询多条对应的库存数据
        List<WhSkuInventoryCommand> skuInvList = whSkuInventoryDao.findWhSkuInventoryBySkuIdAndContainerid(pdaInboundSortationCommand.getOuId(), pdaInboundSortationCommand.getSkuId(), pdaInboundSortationCommand.getContainerId(), null);
        for (WhSkuInventoryCommand inv : skuInvList) {
            // 封装库存属性数据
            // 判断原产地是否为空
            if (!StringUtil.isEmpty(inv.getCountryOfOrigin())) {
                // 获取原产地MAP中的值
                String coo = cooMap.get(inv.getCountryOfOrigin());
                if (StringUtil.isEmpty(coo)) {
                    // 放入原产地MAP
                    cooMap.put(inv.getCountryOfOrigin(), inv.getCountryOfOrigin());
                }
            }
            // 判断保质期 生产日期
            if (!StringUtil.isEmpty(inv.getMfgDateStr())) {
                String mfg = mfgMap.get(inv.getMfgDateStr());
                if (StringUtil.isEmpty(mfg)) {
                    // 封装生产日期
                    mfgMap.put(inv.getMfgDateStr(), inv.getMfgDateStr());
                }
            }
            // 判断保质期 失效日期
            if (!StringUtil.isEmpty(inv.getExpDateStr())) {
                String exp = expMap.get(inv.getExpDateStr());
                if (StringUtil.isEmpty(exp)) {
                    // 封装生产日期
                    expMap.put(inv.getExpDateStr(), inv.getExpDateStr());
                }
            }
            // 判断批次号
            if (!StringUtil.isEmpty(inv.getBatchNumber())) {
                String batchNum = batchNumMap.get(inv.getBatchNumber());
                if (StringUtil.isEmpty(batchNum)) {
                    // 封装批次号
                    batchNumMap.put(inv.getBatchNumber(), inv.getBatchNumber());
                }
            }
            // 判断库存属性1
            if (!StringUtil.isEmpty(inv.getInvAttr1())) {
                String invAttr1 = invAttr1Map.get(inv.getInvAttr1());
                if (StringUtil.isEmpty(invAttr1)) {
                    // 封装库存属性1
                    invAttr1Map.put(inv.getInvAttr1(), inv.getInvAttr1Str());
                }
            }
            // 判断库存属性2
            if (!StringUtil.isEmpty(inv.getInvAttr2())) {
                String invAttr2 = invAttr2Map.get(inv.getInvAttr2());
                if (StringUtil.isEmpty(invAttr2)) {
                    // 封装库存属性2
                    invAttr2Map.put(inv.getInvAttr2(), inv.getInvAttr2Str());
                }
            }
            // 判断库存属性3
            if (!StringUtil.isEmpty(inv.getInvAttr3())) {
                String invAttr3 = invAttr3Map.get(inv.getInvAttr3());
                if (StringUtil.isEmpty(invAttr3)) {
                    // 封装库存属性3
                    invAttr3Map.put(inv.getInvAttr3(), inv.getInvAttr3Str());
                }
            }
            // 判断库存属性4
            if (!StringUtil.isEmpty(inv.getInvAttr4())) {
                String invAttr4 = invAttr4Map.get(inv.getInvAttr4());
                if (StringUtil.isEmpty(invAttr4)) {
                    // 封装库存属性4
                    invAttr4Map.put(inv.getInvAttr4(), inv.getInvAttr4Str());
                }
            }
            // 判断库存属性5
            if (!StringUtil.isEmpty(inv.getInvAttr5())) {
                String invAttr5 = invAttr5Map.get(inv.getInvAttr5());
                if (StringUtil.isEmpty(invAttr5)) {
                    // 封装库存属性5
                    invAttr5Map.put(inv.getInvAttr5(), inv.getInvAttr5Str());
                }
            }
            // 判断库存类型
            if (!StringUtil.isEmpty(inv.getInvType())) {
                String invType = invTypeMap.get(inv.getInvType());
                if (StringUtil.isEmpty(invType)) {
                    // 封装库存类型
                    invTypeMap.put(inv.getInvType(), inv.getInvTypeName());
                }
            }
            // 判断库存状态
            if (null != inv.getInvStatus()) {
                String invStatus = invStatusMap.get(inv.getInvStatus().intValue());
                if (StringUtil.isEmpty(invStatus)) {
                    // 封装库存状态
                    invStatusMap.put(inv.getInvStatus().intValue(), inv.getInvstatusName());
                }
            }
        }
        pdaInboundSortationCommand.setCooMap(cooMap);
        pdaInboundSortationCommand.setMfgMap(mfgMap);
        pdaInboundSortationCommand.setExpMap(expMap);
        pdaInboundSortationCommand.setInvStatusMap(invStatusMap);
        pdaInboundSortationCommand.setInvTypeMap(invTypeMap);
        pdaInboundSortationCommand.setInvAttr1Map(invAttr1Map);
        pdaInboundSortationCommand.setInvAttr2Map(invAttr2Map);
        pdaInboundSortationCommand.setInvAttr3Map(invAttr3Map);
        pdaInboundSortationCommand.setInvAttr4Map(invAttr4Map);
        pdaInboundSortationCommand.setInvAttr5Map(invAttr5Map);
        log.info(this.getClass().getSimpleName() + ".pdaScanSkuInvAttr method end! logid: " + pdaInboundSortationCommand.getLogId());
        return pdaInboundSortationCommand;
    }

    /**
     * 验证扫描SN/残次条码 并且记录对应数据
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void pdaScanSn(PdaInboundSortationCommand pdaInboundSortationCommand) {
        log.info(this.getClass().getSimpleName() + ".pdaScanSn method begin! logid: " + pdaInboundSortationCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[PdaInboundSortationCommand:{}]", pdaInboundSortationCommand.toString());
        }
        // 查询对应库存信息
        WhSkuInventory skuInv = whSkuInventoryDao.findWhSkuInventoryById(pdaInboundSortationCommand.getSkuInvId(), pdaInboundSortationCommand.getOuId());
        WhSkuInventorySn sn = whSkuInventorySnDao.findWhSkuInventoryByUuidAndSnOrDefectWareBarcode(pdaInboundSortationCommand.getOuId(), skuInv.getUuid(), pdaInboundSortationCommand.getSnCode());
        if (null == sn) {
            // 无此SN/残次信息
            log.warn("pdaScanSn sn is null logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_NULL);
        }
        // 判断此SN/残次条码是否本次扫过
        if (sn.getSysUuid().equals(pdaInboundSortationCommand.getUuid())) {
            log.warn("pdaScanSn sn.getSysUuid() = pdaInboundSortation.getUuid() error logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SN_DOUBLE_ERROR);
        }
        // 修改对应SN/残次信息的SYS_UUID
        sn.setSysUuid(pdaInboundSortationCommand.getUuid());
        int count = whSkuInventorySnDao.update(sn);
        if (count == 0) {
            log.warn("pdaScanSn update sn sys_uuid error logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        log.info(this.getClass().getSimpleName() + ".pdaScanSn method end! logid: " + pdaInboundSortationCommand.getLogId());
    }

    /**
     * 扫描完SN/残次条码 验证相关信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void pdaScanSnDone(PdaInboundSortationCommand pdaInboundSortationCommand) {
        log.info(this.getClass().getSimpleName() + ".pdaScanSnDone method begin! logid: " + pdaInboundSortationCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[PdaInboundSortationCommand:{}]", pdaInboundSortationCommand.toString());
        }
        // 查询对应库存信息
        WhSkuInventory skuInv = whSkuInventoryDao.findWhSkuInventoryById(pdaInboundSortationCommand.getSkuInvId(), pdaInboundSortationCommand.getOuId());
        // 查询对应SN/残次信息 uuid+sysuuid
        List<WhSkuInventorySn> snList = whSkuInventorySnDao.findWhSkuInventorySnByUuidAndSysUuid(pdaInboundSortationCommand.getOuId(), skuInv.getUuid(), pdaInboundSortationCommand.getUuid());
        if (snList.size() > pdaInboundSortationCommand.getShiftInQty() || snList.size() < pdaInboundSortationCommand.getShiftInQty()) {
            // 扫描的SN/残次信息 大于/小于 本次移入数量
            log.warn("pdaScanSn snList.size() <> pdaInboundSortation.getShiftInQty() error logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SNLISTQTY_ERROR, new Object[] {pdaInboundSortationCommand.getShiftInQty(), snList.size()});
        }
        log.info(this.getClass().getSimpleName() + ".pdaScanSnDone method end! logid: " + pdaInboundSortationCommand.getLogId());
    }

    /**
     * 选择完商品库存属性 进行验证
     * 
     * @throws Exception
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaInboundSortationCommand pdaScanSkuAttr(PdaInboundSortationCommand pdaInboundSortationCommand) throws Exception {
        log.info(this.getClass().getSimpleName() + ".pdaScanSkuAttr method begin! logid: " + pdaInboundSortationCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[PdaInboundSortationCommand:{}]", pdaInboundSortationCommand.toString());
        }
        // 封装商品库存属性
        WhSkuInventoryCommand skuInv = new WhSkuInventoryCommand();
        BeanUtils.copyProperties(pdaInboundSortationCommand, skuInv);
        skuInv.setOuId(pdaInboundSortationCommand.getOuId());
        skuInv.setInsideContainerId(pdaInboundSortationCommand.getContainerId());
        // 查找对应库存记录
        WhSkuInventoryCommand inv = whSkuInventoryDao.findWhSkuInventoryBySkuAttr(skuInv);
        if (null == inv) {
            // 对应商品库存属性 库存记录不存在
            log.warn("pdaScanSkuAttr WhSkuInventory attr is null logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_SKUATTRINV_ERROR);
        }
        pdaInboundSortationCommand.setSkuInvId(inv.getId());
        log.info(this.getClass().getSimpleName() + ".pdaScanSkuAttr method begin! end: " + pdaInboundSortationCommand.getLogId());
        return pdaInboundSortationCommand;
    }

    /**
     * 进入扫描目标容器号 获取对应规则 获取对应目标容器信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public PdaInboundSortationCommand scanNewContainerView(PdaInboundSortationCommand pdaInboundSortationCommand) {
        log.info(this.getClass().getSimpleName() + ".scanNewContainerView method begin! logid: " + pdaInboundSortationCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[PdaInboundSortationCommand:{}]", pdaInboundSortationCommand.toString());
        }
        // 通过库存ID信息查找对应规则信息
        RuleAfferCommand ruleAffer = new RuleAfferCommand();
        // 封装数据
        ruleAffer.setLogId(pdaInboundSortationCommand.getLogId());
        ruleAffer.setOuid(pdaInboundSortationCommand.getOuId());
        ruleAffer.setRuleType(Constants.INBOUND_RULE);// 入库分拣规则
        ruleAffer.setRuleId(pdaInboundSortationCommand.getRuleId());
        ruleAffer.setInvId(pdaInboundSortationCommand.getSkuInvId());
        RuleExportCommand export = ruleManager.ruleExport(ruleAffer);
        if (!export.getUsableness()) {
            // 如果没有对应规则 提示错误
            log.warn("pdaScanContainer export.getUsableness() is false logid: " + pdaInboundSortationCommand.getLogId());
            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_USABLENESS_FALSE);
        }
        // 保存对应规则ID
        pdaInboundSortationCommand.setRuleId(export.getWhInBoundRuleCommand().getId());
        log.info(this.getClass().getSimpleName() + ".scanNewContainerView method end! logid: " + pdaInboundSortationCommand.getLogId());
        return pdaInboundSortationCommand;
    }

}
