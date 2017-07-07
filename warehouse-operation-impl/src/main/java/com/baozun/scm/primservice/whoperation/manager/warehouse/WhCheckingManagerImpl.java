/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * <p/>
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * <p/>
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.logistics.wms4.manager.MaTransportManager;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.CheckingDisplayCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ContainerCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhLocationSkuVolumeCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundFacilityCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingPrint;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.InvTransactionType;
import com.baozun.scm.primservice.whoperation.constant.OdoLineStatus;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.OutboundboxStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDeliveryInfoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportServiceDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionOutBoundDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhLocationDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOdoPackageInfoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundConsumableDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhPrintInfoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.checking.CheckingManager;
import com.baozun.scm.primservice.whoperation.manager.odo.OdoManagerProxy;
import com.baozun.scm.primservice.whoperation.manager.odo.WhOdoDeliveryInfoManager;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.redis.SkuRedisManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryLogManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportService;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container;
import com.baozun.scm.primservice.whoperation.model.warehouse.Location;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhDistributionPatternRule;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOdoPackageInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundConsumable;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundFacility;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhPrintInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventorySn;
import com.baozun.scm.primservice.whoperation.util.SkuInventoryUuid;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

@Service("whCheckingManager")
@Transactional
public class WhCheckingManagerImpl extends BaseManagerImpl implements WhCheckingManager {

    public static final Logger log = LoggerFactory.getLogger(WhCheckingManagerImpl.class);

    @Autowired
    private WhCheckingDao whCheckingDao;

    @Autowired
    private WhSeedingCollectionDao whSeedingCollectionDao;

    @Autowired
    private WhOutboundFacilityDao whOutboundFacilityDao;

    @Autowired
    private ContainerDao containerDao;

    @Autowired
    private WhOutboundboxDao whOutboundboxDao;

    @Autowired
    private WhCheckingLineDao whCheckingLineDao;

    @Autowired
    private WhOdoDao whOdoDao;

    @Autowired
    private WhDistributionPatternRuleDao whDistributionPatternRuleDao;
    @Autowired
    private WhSkuInventoryManager whSkuInventoryManager;
    @Autowired
    private WhOutboundboxLineDao whOutboundboxLineDao;
    @Autowired
    private WhCheckingLineManager whCheckingLineManager;
    @Autowired
    private UomDao uomDao;
    @Autowired
    private WhSkuManager whSkuManager;
    @Autowired
    private WhOdoPackageInfoDao whOdoPackageInfoDao;
    @Autowired
    private WhFunctionOutBoundDao whFunctionOutBoundDao;
    @Autowired
    private WhPrintInfoDao whPrintInfoDao;
    @Autowired
    private CheckingManager checkingManager;
    @Autowired
    private WhSkuDao skuDao;
    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;
    @Autowired
    private WhOdoDeliveryInfoDao whOdoDeliveryInfoDao;
    @Autowired
    private WhOdoTransportMgmtDao whOdoTransportMgmtDao;
    @Autowired
    private MaTransportManager maTransportManager;
    // 分隔符
    public static final String DV = "┊";
    // 占位符
    public static final String PH = "︴";
    @Autowired
    private WhOdoTransportServiceDao whOdoTransportServiceDao;
    @Autowired
    private OdoManagerProxy odoManagerProxy;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhSkuInventoryLogManager whSkuInventoryLogManager;
    @Autowired
    private WhOutboundConsumableDao whOutboundConsumableDao;
    @Autowired
    private WhLocationSkuVolumeManager whLocationSkuVolumeManager;
    @Autowired
    private SkuRedisManager skuRedisManager;
    @Autowired
    private WhLocationDao locationDao;
    @Autowired
    private WhOdoDeliveryInfoManager whOdoDeliveryInfoManager;

    @Autowired
    private WhOdoLineDao whOdoLineDao;


    @Override
    public void saveOrUpdate(WhCheckingCommand whCheckingCommand) {
        WhChecking whChecking = new WhChecking();
        // 复制数据
        BeanUtils.copyProperties(whCheckingCommand, whChecking);
        if (null != whCheckingCommand.getId()) {
            whCheckingDao.saveOrUpdate(whChecking);
        } else {
            whCheckingDao.insert(whChecking);
        }
    }

    // ============================= 按单复核 start =============================
    /**
     * [业务方法] 按单复核-校验扫描编码
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhCheckingByOdoCommand checkInput(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        String input = whCheckingCommand.getInput();
        if (!StringUtils.hasLength(input)) {
            command.setMessage("请输入一个编码开始复核");
        }
        Boolean bingo = false;
        // 判断:播种墙
        bingo = scanSeedingWall(command);
        if (bingo) {
            return command;
        }
        // 判断:小车

        bingo = scanOuterContainer(command);
        if (bingo) {
            return command;
        }
        // 判断:出库箱
        bingo = scanOutboundBox(command);
        if (bingo) {
            return command;
        }
        // 判断:货格
        bingo = scanContainerLatticeNo(command);
        if (bingo) {
            return command;
        }
        // 判断:周转箱
        bingo = scanContainerCode(command);
        if (bingo) {
            return command;
        }
        // 判断:小批次
        bingo = scanBatch(command);
        if (bingo) {
            return command;
        }
        // 判断:出库单号
        bingo = scanOdoCode(command);
        if (bingo) {
            return command;
        }
        // 判断:平台订单号
        // bingo = scanEcOrderCode(command);
        // if (bingo) {
        // return command;
        // }
        // // 判断:外部对接编码
        // bingo = scanExtCode(command);
        // if (bingo) {
        // return command;
        // }

        return null;

    }

    /**
     * [业务方法] 查找出库单对应的面单号或者是面单类型
     * @param checking
     * @return
     */
    private String findWaybillInfo(Long odoId, Long ouId) {
        String waybillType = "";
        WhOdoTransportService odoTransportService = whOdoTransportServiceDao.findByOdoIdAndOuId(odoId, ouId);
        if (null != odoTransportService && odoTransportService.getIsVasSuccess() && odoTransportService.getIsTspSuccess() && odoTransportService.getIsWaybillCodeSuccess()) {
            if (odoTransportService.getIsOl()) {
                waybillType = Constants.ELECTRONIC_WAY_BILL; // 2为电子面单
            } else {
                waybillType = Constants.PAPER_WAY_BILL; // 1为纸质面单
            }
        } else {
            WhOdodeliveryInfo info = odoManagerProxy.getLogisticsInfoByOdoId(odoId, "gianni_test", ouId);
            if (null != info) {
                log.info("mail no =======>" + info.getId());
                if (null != info.getWaybillCode()) {
                    waybillType = Constants.ELECTRONIC_WAY_BILL;
                } else {
                    waybillType = Constants.PAPER_WAY_BILL;
                }
            } else {
                log.info("retrieve mail no =======> failure");
                throw new BusinessException("获取运单号失败");
            }
        }
        return waybillType;
    }

    private WhCheckingByOdoCommand findWhCheckingLineByChecking(WhCheckingByOdoCommand whCheckingByOdoCommand) {
        WhCheckingCommand checking = whCheckingByOdoCommand.getCheckingCommand();
        WhCheckingLine whCheckingLine = new WhCheckingLine();
        List<WhSkuInventorySn> snList = new ArrayList<WhSkuInventorySn>();
        if (null == checking.getId() && null == checking.getOdoId()) {
            // 如果复核id或者出库单id为空,则不应该继续查找复核明细
            // throw new BusinessException("不能查找复核明细");
            whCheckingByOdoCommand.setMessage("不能查找复核明细");
        }
        if (null != checking.getId()) {
            // 有复核id
            whCheckingLine.setCheckingId(checking.getId());
        }
        if (null != checking.getOuId()) {
            whCheckingLine.setOuId(checking.getOuId());
        }
        if (null != checking.getOdoId()) {
            // 有出库单id
            whCheckingLine.setOdoId(checking.getOdoId());
        }
        // 查找复核明细数据
        List<WhCheckingLineCommand> whCheckingLineList = whCheckingLineDao.findListByParamExt(whCheckingLine);

        if (null != whCheckingLineList && !whCheckingLineList.isEmpty()) {
            // 释放耗材
            String occupationCodeSource = whCheckingLineList.get(0).getOdoCode();
            Long sourceOuId = whCheckingLineList.get(0).getOuId();
            this.whSkuInventoryDao.releaseInventoryByOdo(occupationCodeSource, sourceOuId);
            whCheckingLineList = setDicLabel(whCheckingLineList);
            for (WhCheckingLineCommand command : whCheckingLineList) {
                command.setSkuCode(command.getSkuBarCode());
                String skuAttr = SkuCategoryProvider.getSkuAttrIdByCheck(command);
                String[] skuAttrArray = skuAttr.split(DV);
                String attrIndex = "";
                for (String attr : skuAttrArray) {
                    attrIndex = attrIndex + (PH.equals(attr) ? "0" : "1");
                }
                command.setAttrIndex(attrIndex);
                command.setSkuAttr(skuAttr);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                if (null != command.getMfgDate()) {
                    command.setMfgDateStr(format.format(command.getMfgDate()));
                }
                if (null != command.getExpDate()) {
                    command.setExpDateStr(format.format(command.getExpDate()));
                }
                String odoCode = command.getOdoCode();
                String uuid = command.getUuid();
                Long ouId = command.getOuId();
                Long odoLineId = command.getOdoLineId();
                WhSkuInventorySn sn = new WhSkuInventorySn();
                sn.setOccupationCode(odoCode);
                sn.setOuId(ouId);
                sn.setOccupationLineId(odoLineId);
                sn.setStatus(1);
                sn.setUuid(uuid);
                List<WhSkuInventorySn> list = whSkuInventorySnDao.findListByParam(sn);
                if (null != list && !list.isEmpty()) {
                    snList.addAll(list);
                    command.setIsSn(true);
                }
            }
        }
        whCheckingByOdoCommand.setCheckingLineCommandList(whCheckingLineList);
        whCheckingByOdoCommand.setSnList(snList);
        String pickingMode = checking.getPickingMode();
        if ("1".equals(pickingMode) || "2".equals(pickingMode)) {
            checking.setOdoId(whCheckingLineList.get(0).getOdoId());
            String waybillType = "";
            try {
                waybillType = findWaybillInfo(checking.getOdoId(), checking.getOuId());
                checking.setWaybillType(waybillType);
            } catch (Exception e) {
                whCheckingByOdoCommand.setMessage("获取运单号失败, 请重新扫描开始复核");
            }
        }
        whCheckingByOdoCommand.setCheckingCommand(checking);
        return whCheckingByOdoCommand;
    }

    // 封装字典表数据
    private List<WhCheckingLineCommand> setDicLabel(List<WhCheckingLineCommand> list) {
        // 库存类型
        Set<String> dic1 = new HashSet<String>();
        // 库存属性1
        Set<String> dic2 = new HashSet<String>();
        // 库存属性2
        Set<String> dic3 = new HashSet<String>();
        // 库存属性3
        Set<String> dic4 = new HashSet<String>();
        // 库存属性4
        Set<String> dic5 = new HashSet<String>();
        // 库存属性5
        Set<String> dic6 = new HashSet<String>();
        if (list != null && list.size() > 0) {
            for (WhCheckingLineCommand command : list) {
                if (StringUtils.hasText(command.getInvType())) {
                    dic1.add(command.getInvType());
                }
                if (StringUtils.hasText(command.getInvAttr1())) {
                    dic2.add(command.getInvAttr1());
                }
                if (StringUtils.hasText(command.getInvAttr2())) {
                    dic3.add(command.getInvAttr2());
                }
                if (StringUtils.hasText(command.getInvAttr3())) {

                    dic4.add(command.getInvAttr3());
                }
                if (StringUtils.hasText(command.getInvAttr4())) {

                    dic5.add(command.getInvAttr4());
                }
                if (StringUtils.hasText(command.getInvAttr5())) {

                    dic6.add(command.getInvAttr5());
                }
            }
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            map.put(Constants.INVENTORY_TYPE, new ArrayList<String>(dic1));
            map.put(Constants.INVENTORY_ATTR_1, new ArrayList<String>(dic2));
            map.put(Constants.INVENTORY_ATTR_2, new ArrayList<String>(dic3));
            map.put(Constants.INVENTORY_ATTR_3, new ArrayList<String>(dic4));
            map.put(Constants.INVENTORY_ATTR_4, new ArrayList<String>(dic5));
            map.put(Constants.INVENTORY_ATTR_5, new ArrayList<String>(dic6));

            Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);
            for (WhCheckingLineCommand command : list) {
                if (StringUtils.hasText(command.getInvType())) {
                    SysDictionary sys = dicMap.get(Constants.INVENTORY_TYPE + "_" + command.getInvType());
                    command.setInvTypeStr(sys == null ? command.getInvType() : sys.getDicLabel());
                }
                if (StringUtils.hasText(command.getInvAttr1())) {
                    SysDictionary sys = dicMap.get(Constants.INVENTORY_ATTR_1 + "_" + command.getInvAttr1());
                    command.setInvAttr1Str(sys == null ? command.getInvAttr1() : sys.getDicLabel());
                }
                if (StringUtils.hasText(command.getInvAttr2())) {
                    SysDictionary sys = dicMap.get(Constants.INVENTORY_ATTR_2 + "_" + command.getInvAttr2());
                    command.setInvAttr2Str(sys == null ? command.getInvAttr2() : sys.getDicLabel());
                }
                if (StringUtils.hasText(command.getInvAttr3())) {
                    SysDictionary sys = dicMap.get(Constants.INVENTORY_ATTR_3 + "_" + command.getInvAttr3());
                    command.setInvAttr3Str(sys == null ? command.getInvAttr3() : sys.getDicLabel());
                }
                if (StringUtils.hasText(command.getInvAttr4())) {
                    SysDictionary sys = dicMap.get(Constants.INVENTORY_ATTR_4 + "_" + command.getInvAttr4());
                    command.setInvAttr4Str(sys == null ? command.getInvAttr4() : sys.getDicLabel());
                }
                if (StringUtils.hasText(command.getInvAttr5())) {
                    SysDictionary sys = dicMap.get(Constants.INVENTORY_ATTR_5 + "_" + command.getInvAttr5());
                    command.setInvAttr5Str(sys == null ? command.getInvAttr5() : sys.getDicLabel());
                }
            }
        }
        return list;
    }

    /**
     * [业务方法] 按单复核-扫描播种墙编码
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanSeedingWall(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        if (null != whCheckingCommand.getFacilityId() || null != whCheckingCommand.getOuterContainerId()) {
            // 已有播种墙或者小车
            return false;
        }
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        // WhCheckingCommand c = new WhCheckingCommand();
        // BeanUtils.copyProperties(whCheckingCommand, c);
        // 根据播种墙编码查找复核数据
        List<WhCheckingCommand> whCheckingList = whCheckingDao.findListByFacilityCode(input, ouId);
        if (null != whCheckingList && !whCheckingList.isEmpty()) {
            WhCheckingCommand checkingCommand = whCheckingList.get(0);
            // 扫描播种墙编码
            // set 播种墙id
            whCheckingCommand.setFacilityId(whCheckingList.get(0).getFacilityId());
            // set 播种墙code
            whCheckingCommand.setSeedingWallCode(input);
            if (StringUtils.hasLength(checkingCommand.getOutboundboxCode())) {
                whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX);
            } else {
                whCheckingCommand.setTip(Constants.TIP_CONTAINER_LATTICE_NO);
            }
            // set 下个页面提示
            // whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX_OR_NO);
            // 返回
            command.setCheckingCommand(whCheckingCommand);
            return true;
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描小车编码
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanOuterContainer(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        if (null != whCheckingCommand.getFacilityId() || null != whCheckingCommand.getOuterContainerId()) {
            // 已有播种墙或者小车
            return false;
        }

        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        // 根据播种墙编码查找复核数据
        List<WhCheckingCommand> whCheckingList = whCheckingDao.findListByOuterContainerCode(input, ouId);
        if (null != whCheckingList && !whCheckingList.isEmpty()) {
            WhCheckingCommand checkingCommand = whCheckingList.get(0);
            // 扫描播种墙编码
            // set 小车id
            whCheckingCommand.setOuterContainerId(whCheckingList.get(0).getOuterContainerId());
            // set 小车code
            whCheckingCommand.setOuterContainerCode(input);
            if (StringUtils.hasLength(checkingCommand.getOutboundboxCode())) {
                whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX);
            } else {
                whCheckingCommand.setTip(Constants.TIP_CONTAINER_LATTICE_NO);
            }
            // set 下个页面提示
            // whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX_OR_NO);
            // 返回
            command.setCheckingCommand(whCheckingCommand);
            return true;
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描出库箱编码
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanOutboundBox(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        WhCheckingCommand checking = new WhCheckingCommand();
        List<WhCheckingCommand> checkingList = new ArrayList<WhCheckingCommand>();
        String input = whCheckingCommand.getInput();
        checking.setOutboundboxCode(input);
        checking.setOuId(whCheckingCommand.getOuId());
        checkingList = whCheckingDao.findListByParamExt(checking);
        if (null != checkingList && !checkingList.isEmpty()) {
            // 扫描出库箱编码
            checking = checkingList.get(0); // 数据最全
            if (null != whCheckingCommand.getFacilityId() || null != whCheckingCommand.getOuterContainerId()) {
                // 已经有小车id或者播种墙id
                WhChecking ch = new WhChecking();
                ch.setFacilityId(whCheckingCommand.getFacilityId());
                ch.setOuterContainerId(whCheckingCommand.getOuterContainerId());
                ch.setOutboundboxCode(input);
                List<WhChecking> chList = this.whCheckingDao.findListByParamWithNoFinish(ch);
                if (null != chList && !chList.isEmpty()) {
                    // 根据已有条件找到复核信息
                    ch = chList.get(0); // 最全
                    // 可以执行复核操作
                    BeanUtils.copyProperties(checking, whCheckingCommand);
                    if (null != whCheckingCommand.getFacilityId()) {
                        /** 按单复核方式:播种墙出库箱流程*/
                        whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_SEEDING_WALL_OUTBOUND_BOX);
                    }
                    if (null != whCheckingCommand.getOuterContainerId()) {
                        /** 按单复核方式:小车出库箱流程*/
                        whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTER_CONTAINER_OUTBOUND_BOX);
                    }
                    whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                    command.setCheckingCommand(whCheckingCommand);
                    command = findWhCheckingLineByChecking(command);
                    return true;
                } else {
                    // 没有找到复核信息, 提示换个出库箱或者货格号扫描
                    whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX_OR_NO);
                    command.setCheckingCommand(whCheckingCommand);
                    return false;
                }
            } else {
                // 查找到的复核信息没有播种墙或者小车
                // 非小车或播种墙的扫描出库箱 直接完成验证 进入复核流程
                BeanUtils.copyProperties(checking, whCheckingCommand);
                command.setCheckingCommand(whCheckingCommand);
                whCheckingCommand.setOutboundboxCode(input);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                /** 按单复核方式:出库箱流程*/
                whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTBOUND_BOX);
                command.setCheckingCommand(whCheckingCommand);
                command = findWhCheckingLineByChecking(command);
                return true;
            }
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描货格号
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanContainerLatticeNo(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        String input = whCheckingCommand.getInput();
        WhChecking checking = new WhChecking();
        List<WhChecking> checkingList = new ArrayList<WhChecking>();
        try {
            Integer containerLatticeNo = Integer.valueOf(input);
            checking.setContainerLatticeNo(containerLatticeNo);
            checking.setOuterContainerId(whCheckingCommand.getOuterContainerId());
            checking.setFacilityId(whCheckingCommand.getFacilityId());
            checkingList = whCheckingDao.findListByParamWithNoFinish(checking);
            if (null != checkingList && !checkingList.isEmpty()) {
                checking = checkingList.get(0);
                if (null != checking.getFacilityId() && null == whCheckingCommand.getFacilityId()) {
                    // 如果货格在播种墙上 提示扫描播种墙
                    whCheckingCommand.setTip(Constants.TIP_SEEDING_WALL);
                    command.setCheckingCommand(whCheckingCommand);
                    return true;
                } else if (null != checking.getOuterContainerId() && null == whCheckingCommand.getOuterContainerId()) {
                    // 如果货格在小车上 提示扫描小车
                    whCheckingCommand.setTip(Constants.TIP_OUTER_CONTAINER);
                    command.setCheckingCommand(whCheckingCommand);
                    return true;
                } else {
                    if (null != whCheckingCommand.getFacilityId() || null != whCheckingCommand.getOuterContainerId()) {
                        // 已经有小车id或者播种墙id
                        WhChecking ch = new WhChecking();
                        ch.setFacilityId(whCheckingCommand.getFacilityId());
                        ch.setOuterContainerId(whCheckingCommand.getOuterContainerId());
                        ch.setContainerLatticeNo(containerLatticeNo);
                        List<WhChecking> chList = this.whCheckingDao.findListByParamWithNoFinish(ch);
                        if (null != chList && !chList.isEmpty()) {
                            // 根据已有条件找到复核信息
                            ch = chList.get(0);
                            // 可以执行复核操作
                            whCheckingCommand.setId(ch.getId());
                            whCheckingCommand.setContainerLatticeNo(containerLatticeNo);
                            if (null != whCheckingCommand.getFacilityId()) {
                                /** 按单复核方式:播种墙货格流程*/
                                whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_SEEDING_WALL_LATTICE_NO);
                            }
                            if (null != whCheckingCommand.getOuterContainerId()) {
                                /** 按单复核方式:小车货格流程*/
                                whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTER_CONTAINER_LATTICE_NO);
                            }
                            whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                            command.setCheckingCommand(whCheckingCommand);
                            command = findWhCheckingLineByChecking(command);
                            return true;
                        } else {
                            // 没有找到复核信息, 提示换个出库箱或者货格号扫描
                            whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX_OR_NO);
                            command.setCheckingCommand(whCheckingCommand);
                            return true;
                        }
                    } else {
                        // 查找到的复核信息没有播种墙或者小车, 这种场景不应该发生, 提示扫描编码不存在
                        whCheckingCommand.setTip(Constants.TIP_CODE_ERROR);
                        command.setCheckingCommand(whCheckingCommand);
                        // return false;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            // 没找到对应编码,提示扫描编码不存在
            whCheckingCommand.setTip(Constants.TIP_CODE_ERROR);
            command.setCheckingCommand(whCheckingCommand);
            // return false;
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描周转箱编码
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanContainerCode(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        WhCheckingCommand checkingCommand = new WhCheckingCommand();
        List<WhCheckingCommand> whCheckingList = whCheckingDao.findListByContainerCode(input, ouId);
        if (null != whCheckingList && !whCheckingList.isEmpty()) {
            checkingCommand = whCheckingList.get(0);
            // TODO
            // 需要判断是否有绑定出库箱, 意味着是否是主副品或者套装.如果有则把出库箱对应的复核明细也缓存到页面
            if (Constants.PICKING_MODE_BATCH_GROUP.equals(checkingCommand.getPickingMode()) || Constants.PICKING_MODE_BATCH_MAIN.equals(checkingCommand.getPickingMode())) {
                // whCheckingCommand.setTip(Constants.TIP_BATCH);
                BeanUtils.copyProperties(checkingCommand, whCheckingCommand);
                whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_CONTAINER);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                whCheckingCommand.setIsComplex(true);
                command.setCheckingCommand(whCheckingCommand);
                return true;
            }
            // 可以执行复核操作
            BeanUtils.copyProperties(checkingCommand, whCheckingCommand);
            /** 按单复核方式:周转箱流程*/
            whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_CONTAINER);
            whCheckingCommand.setTip(Constants.TIP_SUCCESS);
            command.setCheckingCommand(whCheckingCommand);
            command = findWhCheckingLineByChecking(command);
            return true;
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描批次号
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanBatch(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        WhChecking checking = new WhChecking();
        checking.setBatch(input);
        checking.setOuId(ouId);
        List<WhChecking> whCheckingList = whCheckingDao.findListByParamWithNoFinish(checking);
        if (null != whCheckingList && !whCheckingList.isEmpty()) {
            checking = whCheckingList.get(0);
            if (null != checking.getContainerLatticeNo()) {
                // 货格
                whCheckingCommand.setBatch(input);
                whCheckingCommand.setTip(Constants.TIP_CONTAINER_OR_FACILITY);
                return true;
            } else if (null != checking.getOutboundboxCode()) {
                // 出库箱
                int size = whCheckingList.size();
                if (1 == size) {
                    // 只有一个出库箱
                    whCheckingCommand.setTip(Constants.TIP_BATCH_UNIQUE_OUTBOUND_BOX);
                    whCheckingCommand.setOutboundboxCode(checking.getOutboundboxCode());
                    whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTBOUND_BOX);
                } else {
                    // 多个出库箱
                    whCheckingCommand.setTip(Constants.TIP_BATCH_MULTIPLE_OUTBOUND_BOX);
                    whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTBOUND_BOX);
                }
            }
            if (Constants.PICKING_MODE_BATCH_GROUP.equals(checking.getPickingMode()) || Constants.PICKING_MODE_BATCH_SECKILL.equals(checking.getPickingMode()) || Constants.PICKING_MODE_BATCH_MAIN.equals(checking.getPickingMode())) {
                // TODO 查找对应的主副品或者套装或者秒杀

            }
            // 可以执行复核操作
            // List<WhCheckingLineCommand> whCheckingLineList =
            // findWhCheckingLineByChecking(whCheckingCommand);
            // command.setCheckingLineCommandList(whCheckingLineList);
            whCheckingCommand.setBatch(input);
            command.setCheckingCommand(whCheckingCommand);
            return true;
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描出库单
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanOdoCode(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        WhOdo odo = whOdoDao.findOdoByCodeAndOuId(input, ouId);
        if (null != odo) {
            Long odoId = odo.getId();
            WhChecking checking = new WhChecking();
            // checkingLine.setOdoId(odoId);
            // checkingLine.setOuId(ouId);
            List<WhChecking> whCheckingList = whCheckingLineDao.findListByParamWithNoFinish(odoId, ouId);
            if (null != whCheckingList && !whCheckingList.isEmpty()) {
                checking = whCheckingList.get(0);
                if (null != checking.getContainerLatticeNo()) {
                    // 货格
                    int size = whCheckingList.size();
                    if (1 == size) {
                        // 只有一个货格
                        if (null != checking.getFacilityId()) {
                            // 播种墙+货格
                            Long facilityId = checking.getFacilityId();
                            WhOutboundFacility seedingwall = whOutboundFacilityDao.findByIdAndOuId(facilityId, ouId);
                            whCheckingCommand.setSeedingWallCode(seedingwall.getFacilityCode());
                            whCheckingCommand.setFacilityId(facilityId);
                            whCheckingCommand.setContainerLatticeNo(checking.getContainerLatticeNo());
                            whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_SEEDING_WALL_LATTICE_NO);
                            whCheckingCommand.setTip(Constants.TIP_CONTAINER_OR_FACILITY_UNIQUE);
                            whCheckingCommand.setPickingMode(checking.getPickingMode());
                            whCheckingCommand.setId(checking.getId());
                        } else {
                            // 小车+货格
                            Long outerContainerId = checking.getOuterContainerId();
                            Container outerContainer = containerDao.findByIdExt(outerContainerId, ouId);
                            whCheckingCommand.setOuterContainerCode(outerContainer.getCode());
                            whCheckingCommand.setOuterContainerId(checking.getOuterContainerId());
                            whCheckingCommand.setContainerLatticeNo(checking.getContainerLatticeNo());
                            whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTER_CONTAINER_LATTICE_NO);
                            whCheckingCommand.setTip(Constants.TIP_CONTAINER_OR_FACILITY_UNIQUE);
                            whCheckingCommand.setPickingMode(checking.getPickingMode());
                            whCheckingCommand.setId(checking.getId());
                        }
                    } else {
                        // 多个货格
                        if (null != checking.getFacilityId()) {
                            // 播种墙加货格
                            whCheckingCommand.setTip(Constants.ORDER_IN_MULTI_FACILITY);
                        } else {
                            // 小车加货格
                            Long outerContainerId = checking.getOuterContainerId();
                            Container outerContainer = containerDao.findByIdExt(outerContainerId, ouId);
                            whCheckingCommand.setOuterContainerId(outerContainerId);
                            whCheckingCommand.setOuterContainerCode(outerContainer.getCode());
                            whCheckingCommand.setPickingMode(checking.getPickingMode());
                            whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTER_CONTAINER_LATTICE_NO);
                            whCheckingCommand.setTip(Constants.TIP_CONTAINER_OR_FACILITY);
                        }
                    }
                } else if (null != checking.getOutboundboxCode()) {
                    // 出库箱
                    int size = whCheckingList.size();
                    if (1 == size) {
                        // 只有一个出库箱
                        whCheckingCommand.setTip(Constants.TIP_BATCH_UNIQUE_OUTBOUND_BOX);
                        whCheckingCommand.setOutboundboxCode(checking.getOutboundboxCode());
                        whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTBOUND_BOX);
                    } else {
                        // 多个出库箱
                        whCheckingCommand.setTip(Constants.TIP_BATCH_MULTIPLE_OUTBOUND_BOX);
                        whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTBOUND_BOX);
                    }
                }
                if (null != checking.getContainerId()) {
                    int size = whCheckingList.size();
                    // 周转箱
                    if (Constants.PICKING_MODE_BATCH_SINGLE.equals(checking.getPickingMode()) || Constants.PICKING_MODE_BATCH_GROUP.equals(checking.getPickingMode()) || Constants.PICKING_MODE_BATCH_SECKILL.equals(checking.getPickingMode())
                            || Constants.PICKING_MODE_BATCH_MAIN.equals(checking.getPickingMode())) {
                        // TODO 查找对应的主副品或者套装或者秒杀
                        // whCheckingCommand.setTip(Constants.TIP_GENERAL_CHECKING);
                        whCheckingCommand.setContainerId(checking.getContainerId());
                        Container container = containerDao.findByIdExt(checking.getContainerId(), ouId);
                        whCheckingCommand.setContainerCode(container.getCode());
                        whCheckingCommand.setPickingMode(checking.getPickingMode());
                        whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_CONTAINER);
                    } else {
                        if (size > 1) {
                            // 一个出库单占用多个周转箱, 需要提示扫描周转箱
                            whCheckingCommand.setTip(Constants.TIP_BATCH_MULTIPLE_CONTAINER);
                        } else {
                            // 一个出库单只有一个周转箱
                            // 普通摘果
                            whCheckingCommand.setTip(Constants.TIP_GENERAL_CHECKING);
                            whCheckingCommand.setContainerId(checking.getContainerId());
                            Container container = containerDao.findByIdExt(checking.getContainerId(), ouId);
                            whCheckingCommand.setContainerCode(container.getCode());
                            whCheckingCommand.setPickingMode(checking.getPickingMode());
                            whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_CONTAINER);
                        }
                    }
                }
                // 可以执行复核操作
                // List<WhCheckingLineCommand> whCheckingLineList =
                // findWhCheckingLineByChecking(whCheckingCommand);
                // command.setCheckingLineCommandList(whCheckingLineList);
                whCheckingCommand.setBatch(checking.getBatch());
                command.setCheckingCommand(whCheckingCommand);
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描平台订单号
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanEcOrderCode(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        WhOdo odo = whOdoDao.findOdoByEcOrderCodeAndOuId(input, ouId);
        if (null != odo) {
            Long odoId = odo.getId();
            WhDistributionPatternRule distributionPatternRule = whDistributionPatternRuleDao.findByOdoIdAndOuId(odoId, ouId);
            if (null != distributionPatternRule && (Constants.PICKING_MODE_PICKING.equals(distributionPatternRule.getPickingMode()) || Constants.PICKING_MODE_SEED.equals(distributionPatternRule.getPickingMode()))) {
                whCheckingCommand.setOdoId(odoId);
                // List<WhCheckingLineCommand> whCheckingLineList =
                // findWhCheckingLineByChecking(whCheckingCommand);
                // command.setCheckingLineCommandList(whCheckingLineList);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                command.setCheckingCommand(whCheckingCommand);
                command = findWhCheckingLineByChecking(command);
                return true;
            }
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描批次号
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanExtCode(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        WhOdo odo = whOdoDao.findOdoByExtCodeAndOuId(input, ouId);
        if (null != odo) {
            Long odoId = odo.getId();
            WhDistributionPatternRule distributionPatternRule = whDistributionPatternRuleDao.findByOdoIdAndOuId(odoId, ouId);
            if (null != distributionPatternRule && (Constants.PICKING_MODE_PICKING.equals(distributionPatternRule.getPickingMode()) || Constants.PICKING_MODE_SEED.equals(distributionPatternRule.getPickingMode()))) {
                whCheckingCommand.setOdoId(odoId);
                // List<WhCheckingLineCommand> whCheckingLineList =
                // findWhCheckingLineByChecking(whCheckingCommand);
                // command.setCheckingLineCommandList(whCheckingLineList);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                command.setCheckingCommand(whCheckingCommand);
                command = findWhCheckingLineByChecking(command);
                return true;
            }
        }
        return false;
    }

    // ============================= 按单复核 end =============================

    /**
     * 
     * @param checkingId
     * @param ouId
     * @return
     */
    public WhCheckingCommand findWhChecking(Long checkingId, Long ouId) {

        return whCheckingDao.findWhCheckingCommandByIdExt(checkingId, ouId);
    }


    /**
     * tangming
     * 按单复合
     * @param checkingLineList
     */
    public WhCheckingByOdoResultCommand checkingByOdo(WhCheckingByOdoResultCommand cmd, Boolean isTabbInvTotal, Long userId, Long ouId, Long functionId) {
        log.info("whcheckingManagerImpl checkingByOdo is start");
        List<WhCheckingLineCommand> checkingLineList = cmd.getCheckingLineList();
        List<String> snList = cmd.getSn();
        if (null != snList && !snList.isEmpty()) {
            // 如果待复核商品是sn商品 需要通过扫描的sn号重新匹配复核明细行
            checkingLineList = matchCheckLine(checkingLineList, snList, ouId);
        }
        String outboundbox = cmd.getOutboundBoxCode();
        Long outboundboxId = cmd.getOutboundboxId();
        Long facilityId = cmd.getFacilityId();
        Long locationId = cmd.getLocationId();
        // 更新复合明细表
        Long checkingId = this.updateCheckingByOdo(checkingLineList, ouId, outboundboxId, outboundbox, userId, cmd.getCheckingPattern());
        cmd.setOuId(ouId);
        // 生成出库箱库存(sn有问题)
        this.addOutBoundInventory(cmd, isTabbInvTotal, userId);
        // 获取出库单id
        WhCheckingLineCommand lineCmd = checkingLineList.get(0);
        Long odoId = lineCmd.getOdoId();
        // 出库箱头信息（t_wh_outboundbox）和出库箱明细
        this.addOutboundbox(checkingId, ouId, odoId, outboundbox, lineCmd, outboundboxId, userId);  //不用改
        // 算包裹计重????
        this.packageWeightCalculationByOdo(checkingLineList, functionId, ouId, odoId, outboundboxId, userId, outboundbox);
        // this.odoDeliveryInfoUpdate(cmd.getWaybillCode(), outboundbox, odoId, ouId,
        // outboundboxId);
        // 扣减耗材库存
        List<WhSkuInventory> skuInvList = whSkuInventoryDao.findbyOccupationCode(outboundbox, ouId);
        if (null == skuInvList || skuInvList.size() > 1) {
            throw new BusinessException(ErrorCodes.SUPPLIES__IS_EEROR);
        }
        for (WhSkuInventory skuInv : skuInvList) {
            Double oldQty = 0.0;
            if (true == isTabbInvTotal) {
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(skuInv.getUuid(), ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            this.insertSkuInventoryLog(skuInv.getId(), -skuInv.getOnHandQty(), oldQty, true, ouId, userId, InvTransactionType.CHECK);
            whSkuInventoryDao.deleteWhSkuInventoryById(skuInv.getId(), ouId);
            insertGlobalLog(GLOBAL_LOG_DELETE, skuInv, ouId, userId, null, null);
            if (null != outboundboxId) {
                // 出库单信息
                WhOdo whOdo = odoManagerProxy.findOdOById(odoId, ouId);
                // 复核台信息
                WhOutboundFacilityCommand facilityCommand = checkingManager.findOutboundFacilityById(facilityId, ouId);
                WhCheckingCommand checkingCmd = whCheckingDao.findWhCheckingCommandByIdExt(checkingId, ouId);
                if (null == checkingCmd) {
                    throw new BusinessException(ErrorCodes.PARAMS_ERROR);
                }
                Location location = locationDao.findByIdExt(locationId, ouId);
                if (null == location) {
                    throw new BusinessException(ErrorCodes.PDA_MAN_MADE_PUTAWAY_LOCATION_NULL);
                }
                String locationCode = location.getCode();
                // 记录耗材信息
                WhOutboundConsumable consumable = this.createOutboundConsumable(facilityCommand, outboundbox, checkingCmd, locationCode, outboundboxId, whOdo, userId, ouId, logId);
                whOutboundConsumableDao.insert(consumable);
                insertGlobalLog(GLOBAL_LOG_INSERT, skuInv, ouId, userId, null, null);
            }
        }
        Boolean result = whCheckingLineManager.judeIsLastBox(ouId, odoId);
        List<Long> odoLineIds = new ArrayList<Long>(); // 待复核出库单明细id列表
        for (WhCheckingLineCommand checkingLine : checkingLineList) {
            odoLineIds.add(checkingLine.getOdoLineId());
        }
        if (result) {
            // 更新出库单状态
            this.updateOdoStatusByOdo(odoId, ouId, userId, cmd.getContaierCode(), cmd.getTurnoverBoxCode(), cmd.getSeedingWallCode(), odoLineIds);
        } else {
            // 修改出库单状态为复核中状态。
            this.updateOdoStatus(odoId, ouId, userId, odoLineIds, OdoStatus.CHECKING);
            // WhOdo whOdo = whOdoDao.findByIdOuId(odoId, ouId);
            // whOdo.setOdoStatus(OdoStatus.CHECKING);
            // whOdoDao.saveOrUpdateByVersion(whOdo);
            // insertGlobalLog(GLOBAL_LOG_UPDATE, whOdo, ouId, userId, null, null);
            //释放容器状态
            this.releaseContainer(ouId, userId,cmd.getContaierCode(), cmd.getTurnoverBoxCode(), cmd.getSeedingWallCode());
        }
        // List<WeightingCommand> commandList =
        // whCheckingDao.findByOutboundBoxCodeAndCheckingId(checkingId, outboundbox, outboundboxId,
        // ouId);
        // if (null != commandList && !commandList.isEmpty()) {
        // return commandList.get(0);
        // } else {
        // return null;
        // }
        WhCheckingByOdoResultCommand waybillCommand = bindkWaybillCode(functionId, ouId, odoId, outboundbox, outboundboxId, false);
        log.info("whcheckingManagerImpl checkingByOdo is end");
        return waybillCommand;
    }
    
    
    private void releaseContainer(Long ouId, Long userId, String outerContainerCode, String turnoverBoxCode, String seedingWallCode){
        if (!StringUtils.isEmpty(outerContainerCode)) {
            // 修改小车
            ContainerCommand outerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
            if (null == outerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            Long outerContainerId = outerCmd.getId();
            int count = whSkuInventoryDao.countWhSkuInventoryCommandByOdo(ouId, outerContainerId, null, null);
            log.info("inventory:outerContainerId occupation count:[{}]", count);
            if (count == 0) {
                log.info("release outerContainerCode");
                Container c = new Container();
                BeanUtils.copyProperties(outerCmd, c);
                c.setStatus(Constants.LIFECYCLE_START);
                c.setLifecycle(Constants.LIFECYCLE_START);
                containerDao.saveOrUpdateByVersion(c);
                insertGlobalLog(GLOBAL_LOG_INSERT, c, ouId, userId, null, null);
            }
        }
        // for (WhCheckingLineCommand checkingLine : checkingLineList) {
        // String turnoverBoxCode = checkingLine.getContainerCode();
        if (!StringUtils.isEmpty(turnoverBoxCode)) {
            // 周转箱状态
            ContainerCommand turnCmd = containerDao.getContainerByCode(turnoverBoxCode, ouId);
            if (null == turnCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            Long insideContainerId = turnCmd.getId();
            int count = whSkuInventoryDao.countWhSkuInventoryCommandByOdo(ouId, null, insideContainerId, null);
            log.info("inventory:container occupation count:[{}]", count);
            if (count == 0) {
                log.info("release container");
                Container turn = new Container();
                BeanUtils.copyProperties(turnCmd, turn);
                turn.setStatus(Constants.LIFECYCLE_START);
                turn.setLifecycle(Constants.LIFECYCLE_START);
                containerDao.saveOrUpdateByVersion(turn);
            }
        }
        // }
        if (!StringUtils.isEmpty(seedingWallCode)) {
            // 修改播种墙状态
            int count = whSkuInventoryDao.countWhSkuInventoryCommandByOdo(ouId, null, null, seedingWallCode);
            log.info("inventory:seeding wall occupation count:[{}]", count);
            if (count == 0) {
                // 没有播种墙库存信息 查看此播种墙是否还有待播种数据
                int occupationCnt = whSeedingCollectionDao.countOccupationByFacilityCode(seedingWallCode, ouId);
                log.info("seeding collection:seeding wall occupation count:[{}]", occupationCnt);
                if (0 == occupationCnt) {
                    log.info("release seedingwall");
                    // 没有待播种数据 释放播种墙
                    WhOutboundFacility facility = whOutboundFacilityDao.findByCodeAndOuId(seedingWallCode, ouId);
                    if (null == facility) {
                        throw new BusinessException(ErrorCodes.SEEDING_SEEDING_FACILITY_NULL_ERROR);
                    }
                    facility.setStatus(String.valueOf(Constants.WH_FACILITY_STATUS_1));
                    facility.setBatch(null);
                    whOutboundFacilityDao.saveOrUpdateByVersion(facility);
                }
            }
        }
    }

    /**
     * [业务方法] 重新匹配复核明细
     * @param checkingLineList
     * @param ouId
     * @return
     */
    private List<WhCheckingLineCommand> matchCheckLine(List<WhCheckingLineCommand> checkingLineList, List<String> snList, Long ouId) {

        return checkingLineList;
    }

    private WhOutboundConsumable createOutboundConsumable(WhOutboundFacilityCommand facilityCommand, String outboundBoxCode, WhCheckingCommand orgChecking, String locationCode, Long outboundBoxId, WhOdo whOdo, Long userId, Long ouId, String logId) {

        WhOutboundConsumable whOutboundConsumable = new WhOutboundConsumable();

        WhLocationSkuVolumeCommand locationSkuVolume = whLocationSkuVolumeManager.findFacilityLocSkuVolumeByLocSku(facilityCommand.getId(), locationCode, outboundBoxId, ouId);
        if (null == locationSkuVolume) {
            throw new BusinessException(ErrorCodes.CHECKING_CONSUMABLE_SKUINVLOC_ERROR);
        }

        // 累计包裹重量，计算包裹计重
        SkuRedisCommand skuRedis = skuRedisManager.findSkuMasterBySkuId(outboundBoxId, ouId, logId);
        Sku sku = skuRedis.getSku();

        whOutboundConsumable.setBatch(orgChecking.getBatch());
        whOutboundConsumable.setWaveCode(orgChecking.getWaveCode());
        whOutboundConsumable.setCustomerCode(orgChecking.getCustomerCode());
        whOutboundConsumable.setCustomerName(orgChecking.getCustomerName());
        whOutboundConsumable.setStoreCode(orgChecking.getStoreCode());
        whOutboundConsumable.setStoreName(orgChecking.getStoreName());
        whOutboundConsumable.setOdoId(whOdo.getId());
        whOutboundConsumable.setOdoCode(whOdo.getOdoCode());
        // TODO 不知道设置
        whOutboundConsumable.setTransportCode("");
        whOutboundConsumable.setWaybillCode("");
        whOutboundConsumable.setFacilityId(facilityCommand.getId());
        whOutboundConsumable.setFacilityCode(facilityCommand.getFacilityCode());
        whOutboundConsumable.setLocationId(locationSkuVolume.getLocationId());
        whOutboundConsumable.setLocationCode(locationSkuVolume.getLocationCode());
        whOutboundConsumable.setAreaId(locationSkuVolume.getWorkAreaId());
        whOutboundConsumable.setAreaCode(locationSkuVolume.getWorkAreaCode());
        whOutboundConsumable.setQty(1d);
        whOutboundConsumable.setOuId(ouId);
        whOutboundConsumable.setOutboundboxCode(outboundBoxCode);
        whOutboundConsumable.setSkuCode(sku.getCode());
        whOutboundConsumable.setSkuBarcode(sku.getBarCode());
        whOutboundConsumable.setSkuName(sku.getName());
        whOutboundConsumable.setSkuLength(sku.getLength());
        whOutboundConsumable.setSkuWidth(sku.getWidth());
        whOutboundConsumable.setSkuHeight(sku.getHeight());
        whOutboundConsumable.setSkuVolume(sku.getVolume());
        whOutboundConsumable.setSkuWeight(sku.getWeight());
        whOutboundConsumable.setCreateId(userId);
        whOutboundConsumable.setCreateTime(new Date());
        whOutboundConsumable.setModifiedId(userId);
        whOutboundConsumable.setLastModifyTime(new Date());

        return whOutboundConsumable;

    }

    private void odoDeliveryInfoUpdate(String waybillCode, String outboundbox, Long odoId, Long ouId, Long outboundboxId) {
        List<WhOdodeliveryInfo> list = whOdoDeliveryInfoDao.getWhOdodeliveryInfoByOdoId(odoId, ouId);
        if (null == list || list.size() == 0) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        WhOdodeliveryInfo whOdodeliveryInfo = list.get(0);
        whOdodeliveryInfo.setWaybillCode(waybillCode);
        whOdodeliveryInfo.setOutboundboxCode(outboundbox);
        whOdodeliveryInfo.setOutboundboxId(outboundboxId);
        whOdoDeliveryInfoDao.saveOrUpdateByVersion(whOdodeliveryInfo);
    }


    /**
     * tangming
     * 按单复合更新复合表
     * @param checkingLineList
     */
    private Long updateCheckingByOdo(List<WhCheckingLineCommand> checkingLineList, Long ouId, Long outboundboxId, String outboundbox, Long userId, String checkingPattern) {
        if (StringUtils.isEmpty(checkingPattern) || StringUtils.isEmpty(outboundbox)) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        Long checkingId = null;
        checkingId = checkingLineList.get(0).getCheckingId(); // 复合头id
        // 查询当前复合头对应的要复合的数量,复合明细中出库箱悟为空
        if (Constants.WAY_2.equals(checkingPattern) || Constants.WAY_4.equals(checkingPattern) || Constants.WAY_5.equals(checkingPattern)) {
            for (WhCheckingLineCommand cmd : checkingLineList) {
                Long id = cmd.getId(); // 复合明细id
                Long checkingQty = cmd.getCheckingQty(); // 复合明细数量
                WhCheckingLineCommand lineCmd = whCheckingLineDao.findCheckingLineById(id, ouId);
                if (lineCmd.getQty().longValue() > cmd.getCheckingQty().longValue()) {
                    WhCheckingLine line = new WhCheckingLine();
                    BeanUtils.copyProperties(lineCmd, line);
                    line.setId(null);
                    line.setQty(checkingQty);
                    line.setCheckingQty(checkingQty);
                    line.setOutboundboxId(outboundboxId);
                    line.setOutboundboxCode(outboundbox);
                    line.setCreateId(userId);
                    line.setCreateTime(new Date());
                    line.setLastModifyTime(new Date());
                    whCheckingLineDao.insert(line);
                    insertGlobalLog(GLOBAL_LOG_INSERT, line, ouId, userId, null, null);
                    // 插入出库箱没有用记录
                    WhCheckingLine insertLine = new WhCheckingLine();
                    BeanUtils.copyProperties(lineCmd, insertLine);
                    insertLine.setQty(lineCmd.getQty() - checkingQty);
                    // insertLine.setCheckingQty(0L);
                    insertLine.setOutboundboxId(null);
                    insertLine.setOutboundboxCode(null);
                    whCheckingLineDao.saveOrUpdateByVersion(insertLine);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, line, ouId, userId, null, null);
                }
                if (lineCmd.getQty().longValue() < cmd.getCheckingQty().longValue()) {
                    throw new BusinessException(ErrorCodes.CHECKING_NUM_IS_EEROR);
                }
                if (lineCmd.getQty().longValue() == cmd.getCheckingQty().longValue()) {

                    WhCheckingLine line = new WhCheckingLine();
                    BeanUtils.copyProperties(lineCmd, line);
                    line.setCheckingQty(checkingQty);
                    line.setOutboundboxId(outboundboxId);
                    line.setOutboundboxCode(outboundbox);
                    whCheckingLineDao.saveOrUpdateByVersion(line);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, line, ouId, userId, null, null);

                }
            }
            // 更新复合头状态
            WhCheckingCommand checkingCmd = whCheckingDao.findWhCheckingCommandByIdExt(checkingId, ouId);
            if (null == checkingCmd) {
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            Double count = whCheckingLineDao.countCheckingLine(checkingId, ouId);
            if (count.doubleValue() == 0.0) {
                WhChecking checking = new WhChecking();
                BeanUtils.copyProperties(checkingCmd, checking);
                checking.setStatus(CheckingStatus.FINISH);
                whCheckingDao.saveOrUpdateByVersion(checking);
                insertGlobalLog(GLOBAL_LOG_UPDATE, checking, ouId, userId, null, null);
            } else {
                WhChecking checking = new WhChecking();
                BeanUtils.copyProperties(checkingCmd, checking);
                checking.setStatus(CheckingStatus.PART_FINISH);
                whCheckingDao.saveOrUpdateByVersion(checking);
                insertGlobalLog(GLOBAL_LOG_UPDATE, checking, ouId, userId, null, null);
            }
        } else {
            for (WhCheckingLineCommand cmd : checkingLineList) {
                Long id = cmd.getId(); // 复合明细id
                Long checkingQty = cmd.getCheckingQty(); // 复合明细数量
                WhCheckingLineCommand lineCmd = whCheckingLineDao.findCheckingLineById(id, ouId);
                WhCheckingLine line = new WhCheckingLine();
                BeanUtils.copyProperties(lineCmd, line);
                line.setCheckingQty(checkingQty);
                line.setOutboundboxId(outboundboxId);
                line.setOutboundboxCode(outboundbox);
                whCheckingLineDao.saveOrUpdateByVersion(line);
                insertGlobalLog(GLOBAL_LOG_UPDATE, line, ouId, userId, null, null);
            }
            // 更新复合头状态
            WhCheckingCommand checkingCmd = whCheckingDao.findWhCheckingCommandByIdExt(checkingId, ouId);
            if (null == checkingCmd) {
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            WhChecking checking = new WhChecking();
            BeanUtils.copyProperties(checkingCmd, checking);
            checking.setStatus(CheckingStatus.FINISH);
            whCheckingDao.saveOrUpdateByVersion(checking);
            insertGlobalLog(GLOBAL_LOG_UPDATE, checking, ouId, userId, null, null);
        }
        return checkingId;
    }



    /**
     * 出库箱头信息
     * 
     * @param whCheckingResultCommand
     */
    private void addOutboundbox(Long checkingId, Long ouId, Long odoId, String outboundbox, WhCheckingLineCommand lineCmd, Long outboundboxId, Long userId) {


        /*
         * Thread thread = new Thread();
         * 
         * for (int i = 0; i < 100; i++) { Long pk = pkManager.generatePk("wms",
         * "com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox");
         * System.out.println(pk); try { thread.sleep(1000); } catch (InterruptedException e) { //
         * TODO Auto-generated catch block e.printStackTrace(); } }
         */
        WhCheckingCommand checkingCmd = whCheckingDao.findWhCheckingCommandByIdExt(checkingId, ouId);
        if (null == checkingCmd) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        WhOutboundboxCommand outboundboxCmd = whOutboundboxDao.getwhOutboundboxCommandByCode(outboundbox, ouId);
        if (null != outboundboxCmd) {
            WhOutboundbox whOutboundbox = new WhOutboundbox();
            BeanUtils.copyProperties(outboundboxCmd, whOutboundbox);
            whOutboundbox.setStatus(OutboundboxStatus.CHECKING);
            whOutboundbox.setDistributionMode(checkingCmd.getDistributionMode());
            whOutboundbox.setPickingMode(checkingCmd.getPickingMode());
            whOutboundbox.setCheckingMode(checkingCmd.getCheckingMode());
            whOutboundbox.setOutboundboxId(outboundboxId);
            whOutboundboxDao.saveOrUpdate(whOutboundbox);
            insertGlobalLog(GLOBAL_LOG_UPDATE, whOutboundbox, ouId, userId, null, null);
        } else {
            WhOutboundbox whOutboundbox = new WhOutboundbox();
            BeanUtils.copyProperties(checkingCmd, whOutboundbox);
            whOutboundbox.setOutboundboxCode(outboundbox);
            whOutboundbox.setId(null);
            whOutboundbox.setOuId(ouId);
            whOutboundbox.setOdoId(odoId);
            whOutboundbox.setStatus(OutboundboxStatus.CHECKING);
            whOutboundbox.setOutboundboxId(outboundboxId);
            whOutboundboxDao.insert(whOutboundbox);
            insertGlobalLog(GLOBAL_LOG_INSERT, whOutboundbox, ouId, userId, null, null);
            // 生成出库想明细信息
            List<WhSkuInventoryCommand> listSkuInvCmd = whSkuInventoryManager.findOutboundboxInventory(outboundbox, ouId);
            if (null != listSkuInvCmd && listSkuInvCmd.size() == 0) {
                throw new BusinessException(ErrorCodes.CONTAINER_INVENTORY_NO_EXIST);
            }
            // 添加出库箱明细
            for (WhSkuInventoryCommand skuInvCmd : listSkuInvCmd) {
                Long skuId = skuInvCmd.getSkuId();
                // 获取sku信息
                WhSkuCommand skuCmd = skuDao.findWhSkuByIdExt(skuId, ouId);
                if (null == skuCmd) {
                    throw new BusinessException(ErrorCodes.SKU_NOT_FOUND);
                }
                WhOutboundboxLine outboundboxLine = new WhOutboundboxLine();
                outboundboxLine.setWhOutboundboxId(whOutboundbox.getId());
                outboundboxLine.setSkuCode(skuCmd.getCode());
                outboundboxLine.setSkuExtCode(skuCmd.getExtCode());
                outboundboxLine.setSkuBarCode(skuCmd.getBarCode());
                outboundboxLine.setSkuName(skuCmd.getName());
                outboundboxLine.setQty(skuInvCmd.getOnHandQty());
                outboundboxLine.setCustomerCode(lineCmd.getCustomerCode());
                outboundboxLine.setCustomerName(lineCmd.getCustomerName());
                outboundboxLine.setStoreCode(lineCmd.getStoreCode());
                outboundboxLine.setStoreName(lineCmd.getStoreName());
                outboundboxLine.setInvStatus(skuInvCmd.getInvStatus().toString());
                outboundboxLine.setInvType(skuInvCmd.getInvType());
                outboundboxLine.setBatchNumber(skuInvCmd.getBatchNumber());
                outboundboxLine.setMfgDate(skuInvCmd.getMfgDate());
                outboundboxLine.setExpDate(skuInvCmd.getExpDate());
                outboundboxLine.setCountryOfOrigin(skuInvCmd.getCountryOfOrigin());
                outboundboxLine.setInvAttr1(skuInvCmd.getInvAttr1());
                outboundboxLine.setInvAttr2(skuInvCmd.getInvAttr2());
                outboundboxLine.setInvAttr3(skuInvCmd.getInvAttr3());
                outboundboxLine.setInvAttr4(skuInvCmd.getInvAttr4());
                outboundboxLine.setInvAttr5(skuInvCmd.getInvAttr5());
                outboundboxLine.setUuid(skuInvCmd.getUuid());
                outboundboxLine.setOuId(skuInvCmd.getOuId());
                outboundboxLine.setOdoId(lineCmd.getOdoId());
                outboundboxLine.setOdoLineId(lineCmd.getOdoLineId());
                outboundboxLine.setSysDate(String.valueOf(new Date()));
                whOutboundboxLineDao.insert(outboundboxLine);
                insertGlobalLog(GLOBAL_LOG_INSERT, outboundboxLine, ouId, userId, null, null);
            }
        }
    }


    /**
     * 算包裹计重(按单复合)
     * 
     * @param whCheckingResultCommand
     */
    private void packageWeightCalculationByOdo(List<WhCheckingLineCommand> checkingLineList, Long functionId, Long ouId, Long odoId, Long outboundboxId, Long userId, String outboundboxCode) {
        List<UomCommand> weightUomCmds = uomDao.findUomByGroupCode(WhUomType.WEIGHT_UOM, BaseModel.LIFECYCLE_NORMAL); // 重量度量单位
        Map<String, Double> weightUomConversionRate = new HashMap<String, Double>();
        for (UomCommand lenUom : weightUomCmds) {
            String uomCode = "";
            Double uomRate = 0.0;
            if (null != lenUom) {
                uomCode = lenUom.getUomCode();
                uomRate = lenUom.getConversionRate();
                weightUomConversionRate.put(uomCode, uomRate);
                log.info("whcheckingManagerImpl sku uomCode:" + uomCode);
                log.info("whcheckingManagerImpl sku uomRate:" + uomRate);
            }
        }
        SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(weightUomConversionRate);
        log.info("whcheckingManagerImpl checkingLineList:" + checkingLineList.size());
        Double sum = 0.0;
        for (WhCheckingLineCommand whCheckingLineCommand : checkingLineList) {
            Double actualWeight = 0.0;
            WhSkuCommand whSkuCommand = whSkuManager.getSkuBybarCode(whCheckingLineCommand.getSkuBarCode(), whCheckingLineCommand.getCustomerCode(), ouId);
            log.info("whcheckingManagerImpl sku weight:" + whSkuCommand.getWeight());
            log.info("whcheckingManagerImpl sku qty:" + whCheckingLineCommand.getCheckingQty());
            actualWeight = weightCalculator.calculateStuffWeight(whSkuCommand.getWeight()) * whCheckingLineCommand.getCheckingQty();
            log.info("whcheckingManagerImpl actualWeight:" + actualWeight);
            sum += actualWeight;

        }
        // @Gianni 计重包括耗材重量
        if (null != outboundboxId) {
            WhSkuCommand consumableSku = whSkuManager.findBySkuIdAndOuId(outboundboxId, ouId);
            if (null != consumableSku) {
                log.info("whcheckingManagerImpl consumableSku:" + consumableSku.getWeight());
                sum += consumableSku.getWeight();
            }
        }
        log.info("whcheckingManagerImpl sum:" + sum);
        WhOdoPackageInfo odoPackageInfo = whOdoPackageInfoDao.findByOutboundBoxCode(outboundboxCode, ouId);
        if (null != odoPackageInfo) {
            odoPackageInfo.setCalcWeight(sum);
            whOdoPackageInfoDao.saveOrUpdateByVersion(odoPackageInfo);
            insertGlobalLog(GLOBAL_LOG_UPDATE, odoPackageInfo, ouId, userId, null, null);
        } else {
            WhFunctionOutBound whFunctionOutBound = whFunctionOutBoundDao.findByFunctionIdExt(functionId, ouId);
            WhOdoPackageInfo whOdoPackageInfo = new WhOdoPackageInfo();
            whOdoPackageInfo.setOdoId(odoId);
            // whOdoPackageInfo.setOutboundboxId(outboundboxId);
            whOdoPackageInfo.setOutboundboxCode(outboundboxCode);
            whOdoPackageInfo.setStatus(Constants.LIFECYCLE_START);
            whOdoPackageInfo.setFloats(whFunctionOutBound.getWeightFloatPercentage());
            whOdoPackageInfo.setLifecycle(Constants.LIFECYCLE_START);
            whOdoPackageInfo.setCreateId(userId);
            whOdoPackageInfo.setCreateTime(new Date());
            whOdoPackageInfo.setLastModifyTime(new Date());
            whOdoPackageInfo.setModifiedId(userId);
            whOdoPackageInfo.setCalcWeight(sum);
            whOdoPackageInfo.setOuId(ouId);
            whOdoPackageInfoDao.insert(whOdoPackageInfo);
            insertGlobalLog(GLOBAL_LOG_INSERT, whOdoPackageInfo, ouId, userId, null, null);
        }
    }

    /**
     * [业务方法] 更新出库单与出库单明细状态
     * @param odoId
     * @param ouId
     * @param userId
     */
    private void updateOdoStatus(Long odoId, Long ouId, Long userId, List<Long> odoLineIds, String status) {
        log.info("updateOdoStatus start...");
        log.info("updateOdoStatus: odoId:[{}], ouId:[{}], userId:[{}], odoLineIds:[{}], status:[{}]", odoId, ouId, userId, odoLineIds, status);
        WhOdo whOdo = whOdoDao.findByIdOuId(odoId, ouId);
        // 修改出库单状态为复核完成状态。
        if (!OdoStatus.CANCEL.equals(whOdo.getOdoStatus())) {
            log.info("odo status is not cancel");
            // 订单状态不为取消 才执行更新出库单状态
            whOdo.setOdoStatus(status);
            whOdo.setLagOdoStatus(status);
            whOdo.setModifiedId(userId);
            whOdoDao.saveOrUpdateByVersion(whOdo);
            insertGlobalLog(GLOBAL_LOG_UPDATE, whOdo, ouId, userId, null, null);
            log.info("odo status:[{}]", status);
            if (OdoStatus.CHECKING_FINISH.equals(status)) {
                // 更新出库单明细状态 该出库单已经复核完成
                for (Long odoLineId : odoLineIds) {
                    WhOdoLine whOdoLine = whOdoLineDao.findOdoLineById(odoLineId, ouId);
                    whOdoLine.setModifiedId(userId);
                    whOdoLine.setOdoLineStatus(OdoLineStatus.CHECKING_FINISH);
                    whOdoLineDao.saveOrUpdateByVersion(whOdoLine);
                    insertGlobalLog(GLOBAL_LOG_UPDATE, whOdoLine, ouId, userId, null, null);
                }
            } else {
                // 部分更新出库单明细状态 该出库单只是部分复核完成
                for (Long odoLineId : odoLineIds) {
                    WhOdoLine whOdoLine = whOdoLineDao.findOdoLineById(odoLineId, ouId);
                    Long checkingQty = this.whCheckingLineDao.findCheckingQty(odoLineId, ouId);
                    if (0L == checkingQty) {
                        log.info("checking qty == 0, odo line status: check finish");
                        // 出库单明细行待复核数量为0, 则更新出库单明细行状态为已复核
                        whOdoLine.setModifiedId(userId);
                        whOdoLine.setOdoLineStatus(OdoLineStatus.CHECKING_FINISH);
                        whOdoLineDao.saveOrUpdateByVersion(whOdoLine);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, whOdoLine, ouId, userId, null, null);
                    } else {
                        log.info("checking qty != 0, odo line status: checking");
                        // 出库单明细行待复核数量不为0, 则更新出库单明细行状态为复核中
                        whOdoLine.setModifiedId(userId);
                        whOdoLine.setOdoLineStatus(OdoLineStatus.CHECKING);
                        whOdoLineDao.saveOrUpdateByVersion(whOdoLine);
                        insertGlobalLog(GLOBAL_LOG_UPDATE, whOdoLine, ouId, userId, null, null);
                    }
                }
            }
        }
        //释放容器状态
        log.info("updateOdoStatus end...");
    }

    /**
     * 更新出库单状态(按单复合只更新一个出库单)
     * 
     * 
     * @author
     * @param whCheckingResultCommand
     */
    private void updateOdoStatusByOdo(Long odoId, Long ouId, Long userId, String outerContainerCode, String turnoverBoxCode, String seedingWallCode, List<Long> odoLineIds) {
        log.info("checkingManager.updateOdoStatusByOdo start...");
        log.info("params: odoId:[{}], ouId:[{}], userId:[{}], outerContainerCode:[{}], turnoverBoxCode:[{}], seedingWallCode:[{}]", odoId, ouId, userId, outerContainerCode, turnoverBoxCode, seedingWallCode);
        // 修改出库单状态为复核完成状态。
        updateOdoStatus(odoId, ouId, userId, odoLineIds, OdoStatus.CHECKING_FINISH);

        if (!StringUtils.isEmpty(outerContainerCode)) {
            // 修改小车
            ContainerCommand outerCmd = containerDao.getContainerByCode(outerContainerCode, ouId);
            if (null == outerCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            Long outerContainerId = outerCmd.getId();
            int count = whSkuInventoryDao.countWhSkuInventoryCommandByOdo(ouId, outerContainerId, null, null);
            log.info("inventory:outerContainerId occupation count:[{}]", count);
            if (count == 0) {
                log.info("release outerContainerCode");
                Container c = new Container();
                BeanUtils.copyProperties(outerCmd, c);
                c.setStatus(Constants.LIFECYCLE_START);
                c.setLifecycle(Constants.LIFECYCLE_START);
                containerDao.saveOrUpdateByVersion(c);
                insertGlobalLog(GLOBAL_LOG_INSERT, c, ouId, userId, null, null);
            }
        }
        // for (WhCheckingLineCommand checkingLine : checkingLineList) {
        // String turnoverBoxCode = checkingLine.getContainerCode();
        if (!StringUtils.isEmpty(turnoverBoxCode)) {
            // 周转箱状态
            ContainerCommand turnCmd = containerDao.getContainerByCode(turnoverBoxCode, ouId);
            if (null == turnCmd) {
                throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
            }
            Long insideContainerId = turnCmd.getId();
            int count = whSkuInventoryDao.countWhSkuInventoryCommandByOdo(ouId, null, insideContainerId, null);
            log.info("inventory:container occupation count:[{}]", count);
            if (count == 0) {
                log.info("release container");
                Container turn = new Container();
                BeanUtils.copyProperties(turnCmd, turn);
                turn.setStatus(Constants.LIFECYCLE_START);
                turn.setLifecycle(Constants.LIFECYCLE_START);
                containerDao.saveOrUpdateByVersion(turn);
            }
        }
        // }
        if (!StringUtils.isEmpty(seedingWallCode)) {
            // 修改播种墙状态
            int count = whSkuInventoryDao.countWhSkuInventoryCommandByOdo(ouId, null, null, seedingWallCode);
            log.info("inventory:seeding wall occupation count:[{}]", count);
            if (count == 0) {
                // 没有播种墙库存信息 查看此播种墙是否还有待播种数据
                int occupationCnt = whSeedingCollectionDao.countOccupationByFacilityCode(seedingWallCode, ouId);
                log.info("seeding collection:seeding wall occupation count:[{}]", occupationCnt);
                if (0 == occupationCnt) {
                    log.info("release seedingwall");
                    // 没有待播种数据 释放播种墙
                    WhOutboundFacility facility = whOutboundFacilityDao.findByCodeAndOuId(seedingWallCode, ouId);
                    if (null == facility) {
                        throw new BusinessException(ErrorCodes.SEEDING_SEEDING_FACILITY_NULL_ERROR);
                    }
                    facility.setStatus(String.valueOf(Constants.WH_FACILITY_STATUS_1));
                    facility.setBatch(null);
                    whOutboundFacilityDao.saveOrUpdateByVersion(facility);
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhCheckingByOdoCommand retrieveCheckData(WhCheckingCommand whCheckingCommand) {
        WhCheckingByOdoCommand whCheckingByOdoCommand = new WhCheckingByOdoCommand();
        // 复核头信息
        List<WhCheckingCommand> checkingList = whCheckingDao.findListByParamExt(whCheckingCommand);
        if (null != checkingList && !checkingList.isEmpty()) {
            whCheckingCommand = checkingList.get(0);
            whCheckingByOdoCommand.setCheckingCommand(whCheckingCommand);
            // 获取复合明细
            whCheckingByOdoCommand = findWhCheckingLineByChecking(whCheckingByOdoCommand);
            // 获取页面显示
            whCheckingByOdoCommand = this.findCheckingInfo(whCheckingByOdoCommand);
            return whCheckingByOdoCommand;
        } else {
            throw new BusinessException("check is null");
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateCheckOutboundBox(WhCheckingCommand whCheckingCommand) {
        WhChecking checking = new WhChecking();
        Long userId = whCheckingCommand.getModifiedId();
        String outboundboxCode = whCheckingCommand.getOutboundboxCode();
        checking.setOuterContainerId(whCheckingCommand.getOuterContainerId());
        checking.setFacilityId(whCheckingCommand.getFacilityId());
        checking.setOuId(whCheckingCommand.getOuId());
        checking.setContainerLatticeNo(whCheckingCommand.getContainerLatticeNo());
        List<WhChecking> checkingList = whCheckingDao.findListByParamWithNoFinish(checking);
        if (null != checkingList && !checkingList.isEmpty()) {
            // 更新出库箱信息到复核头;
            checking = checkingList.get(0);
            checking.setOutboundboxCode(outboundboxCode);
            checking.setModifiedId(userId);
            whCheckingDao.update(checking);
            WhCheckingLine whCheckingLine = new WhCheckingLine();
            whCheckingLine.setCheckingId(checking.getId());
            whCheckingLine.setOuId(checking.getOuId());
            List<WhCheckingLine> checkingLineList = whCheckingLineDao.findListByParam(whCheckingLine);
            if (null != checkingLineList && !checkingLineList.isEmpty()) {
                // 更新出库箱信息到复核明细
                for (WhCheckingLine line : checkingLineList) {
                    line.setOutboundboxCode(outboundboxCode);
                    line.setModifiedId(userId);
                    whCheckingLineDao.saveOrUpdateByVersion(line);
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhCheckingByOdoCommand findCheckingInfo(WhCheckingByOdoCommand whCheckingByOdoCommand) {
        WhCheckingCommand whCheckingCommand = whCheckingByOdoCommand.getCheckingCommand();
        CheckingDisplayCommand checkingDisplayCommand = new CheckingDisplayCommand();
        String odoCode = whCheckingByOdoCommand.getCheckingLineCommandList().get(0).getOdoCode();
        WhOdo odo = this.whOdoDao.findOdoByCodeAndOuId(odoCode, whCheckingCommand.getOuId());
        Long ouId = whCheckingCommand.getOuId();
        if (null != odo) {
            // 出库单不为空(秒杀订单等模式在未匹配到商品前不显示出库单信息)
            checkingDisplayCommand.setOdoCode(odoCode);
            checkingDisplayCommand.setExtCode(odo.getExtCode());
        }
        if (null != whCheckingCommand.getOutboundboxCode() && null != whCheckingCommand.getBatch()) {
            // 有出库箱信息
            Long batchBoxCnt = whCheckingDao.findBatchBoxCntByParam(whCheckingCommand.getBatch(), ouId);
            checkingDisplayCommand.setOutboundboxCount(batchBoxCnt);
            Long batchBoxCntCheck = whCheckingDao.findBatchBoxCntCheckByParam(whCheckingCommand.getBatch(), ouId);
            checkingDisplayCommand.setToBeCheckedOutboundboxCount(batchBoxCntCheck);
            Long boxSkuCnt = whCheckingDao.findBoxSkuCntByBoxAndOuId(whCheckingCommand.getOutboundboxCode(), ouId);
            checkingDisplayCommand.setBoxSkuCnt(boxSkuCnt);
        } else if (null != whCheckingCommand.getContainerLatticeNo() && null != whCheckingCommand.getBatch()) {
            // 有货格
            Long latticeSkuCnt = whCheckingDao.findCheckingIdAndOuId(whCheckingCommand.getId(), ouId);
            checkingDisplayCommand.setLatticeSkuCnt(latticeSkuCnt);
        }

        else if (null != whCheckingCommand.getContainerId() && ("1".equals(whCheckingCommand.getPickingMode()) || "2".equals(whCheckingCommand.getPickingMode()))) {
            // 周转箱复核且是普通摘果 计算容器中商品数量与订单待复核商品数量 只要有一个数量为0就自动复核
            Long containerId = whCheckingCommand.getContainerId();
            Long containerSkuCnt = whCheckingDao.findContainerSkuCntByContainerAndOuId(containerId, ouId);
            checkingDisplayCommand.setContainerSkuCnt(containerSkuCnt);
        } else {
            // 无出库箱信息
            checkingDisplayCommand.setOutboundboxCount(0L);
            checkingDisplayCommand.setToBeCheckedOutboundboxCount(0L);
        }
        Long batchOdoCnt = whCheckingDao.findBatchOdoCntByParam(whCheckingCommand.getBatch(), ouId);
        checkingDisplayCommand.setOdoCount(batchOdoCnt);
        Long batchOdoCntCheck = whCheckingDao.findBatchOdoCntCheckByParam(whCheckingCommand.getBatch(), ouId);
        checkingDisplayCommand.setToBeCheckedOdoCount(batchOdoCntCheck);
        Long skuCnt = whCheckingDao.findOdoSkuCntByParam(odoCode, ouId);
        checkingDisplayCommand.setSkuCnt(skuCnt);
        checkingDisplayCommand.setWaveCode(whCheckingCommand.getWaveCode());
        checkingDisplayCommand.setTransportName(whCheckingCommand.getTransportName());
        checkingDisplayCommand.setProductName(whCheckingCommand.getProductName());
        checkingDisplayCommand.setTimeEffectName(whCheckingCommand.getTimeEffectName());
        checkingDisplayCommand.setCustomerName(whCheckingCommand.getCustomerName());
        checkingDisplayCommand.setStoreName(whCheckingCommand.getStoreName());
        checkingDisplayCommand.setBatch(whCheckingCommand.getBatch());
        checkingDisplayCommand.setCheckingMode(whCheckingCommand.getCheckingMode());
        whCheckingByOdoCommand.setCheckingDisplayCommand(checkingDisplayCommand);
        return whCheckingByOdoCommand;
    }


    /**
     * 根据复核打印配置打印单据
     * 
     * @param whCheckingResultCommand
     */
    @Override
    public Boolean printDefect(WhCheckingByOdoResultCommand cmd) {
        log.info("whcheckingManagerImpl printDefect is start");
        Boolean isSuccess = true;
        String checkingPattern = cmd.getCheckingPattern(); // 按单复合模式
        Long ouId = cmd.getOuId();
        Long functionId = cmd.getFunctionId();
        Long userId = cmd.getUserId();
        List<WhCheckingLineCommand> checkingLineList = cmd.getCheckingLineList();
        String outboundBoxCode = cmd.getOutboundBoxCode();
        Long outboundboxId = cmd.getOutboundboxId();
        String waybillCode = cmd.getWaybillCode();
        Long checkingId = checkingLineList.get(0).getCheckingId();
        Long odoId = checkingLineList.get(0).getOdoId();
        // 更新复合头状态
        WhCheckingCommand checkingCmd = whCheckingDao.findWhCheckingCommandByIdExt(checkingId, ouId);
        if (null == checkingCmd) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        // 查询功能是否配置复核打印单据配置
        WhFunctionOutBound whFunctionOutBound = whFunctionOutBoundDao.findByFunctionIdExt(functionId, ouId);
        if (null == whFunctionOutBound) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        String checkingPrint = whFunctionOutBound.getCheckingPrint();
        if (!StringUtils.isEmpty(checkingPrint)) {
            String[] checkingPrintArray = checkingPrint.split(",");
            for (int i = 0; i < checkingPrintArray.length; i++) {
                List<Long> idsList = new ArrayList<Long>();
                Boolean isInsert  = false;
                List<WhPrintInfo> whPrintInfoLst = whPrintInfoDao.findByOutboundboxCodeAndPrintType(outboundBoxCode, checkingPrintArray[i], ouId);
                if (null == whPrintInfoLst || 0 == whPrintInfoLst.size()) {
                    log.info("whprintInfo insert is start");
                    WhPrintInfo whPrintInfo = new WhPrintInfo();
                    // 小车加出库箱
                    if (Constants.WAY_1.equals(checkingPattern)) {
                        whPrintInfo.setOuterContainerId(checkingCmd.getOuterContainerId());
                        Container outerContainer = containerDao.findByIdExt(checkingCmd.getOuterContainerId(), ouId);// 小车
                        if (null == outerContainer) {
                            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                        }
                        whPrintInfo.setOuterContainerCode(outerContainer.getCode());
                    }
                    // 小车加货格
                    if (Constants.WAY_2.equals(checkingPattern)) {
                        whPrintInfo.setOuterContainerId(checkingCmd.getOuterContainerId());
                        Container outerContainer = containerDao.findByIdExt(checkingCmd.getOuterContainerId(), ouId);// 小车
                        if (null == outerContainer) {
                            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                        }
                        whPrintInfo.setOuterContainerCode(outerContainer.getCode());
                        whPrintInfo.setContainerLatticeNo(checkingCmd.getContainerLatticeNo());
                    }
                    // 周转箱
                    if (Constants.WAY_5.equals(checkingPattern)) {
                        whPrintInfo.setContainerId(checkingCmd.getContainerId()); // 周转箱
                        Container container = containerDao.findByIdExt(checkingCmd.getContainerId(), checkingCmd.getOuId());
                        if (null == container) {
                            throw new BusinessException(ErrorCodes.PDA_INBOUND_SORTATION_CONTAINER_NULL);
                        }
                        whPrintInfo.setContainerCode(container.getCode());
                    }
                    // //播种墙出库箱
                    if (Constants.WAY_3.equals(checkingPattern)) {
                        whPrintInfo.setFacilityId(checkingCmd.getFacilityId());
                    }
                    // 播种墙加货格
                    if (Constants.WAY_4.equals(checkingPattern)) {
                        whPrintInfo.setFacilityId(checkingCmd.getFacilityId());
                        whPrintInfo.setContainerLatticeNo(checkingCmd.getContainerLatticeNo());
                    }
                    try{
                        if (CheckingPrint.PACKING_LIST.equals(checkingPrintArray[i])) {
                            isInsert  = true;
                            // 装箱清单
                            checkingManager.printPackingList(idsList, userId, ouId);
                        }  
                        if (CheckingPrint.SALES_LIST.equals(checkingPrintArray[i])) {
                            List<WhPrintInfo> printInfoLst =  whPrintInfoDao.getPrintInfoByOdoId(checkingLineList.get(0).getOdoId(), ouId);
                            if(null == printInfoLst || 0 == printInfoLst.size()) {
                                idsList.add(checkingLineList.get(0).getOdoId());
                                isInsert  = true;
                                // 销售清单
                                checkingManager.printSalesList(idsList, userId, ouId);
                            }else{
                                if(printInfoLst.size() ==1 && printInfoLst.get(0).getPrintCount() == 0){
                                    idsList.add(checkingLineList.get(0).getOdoId());
                                    isInsert  = true;
                                    // 销售清单
                                    checkingManager.printSalesList(idsList, userId, ouId);
                                }
                            }
                        }
                        if (CheckingPrint.SINGLE_PLANE.equals(checkingPrintArray[i])) {
                            isInsert  = true;
                            // 面单
                            log.info("waybill print: outboundBoxCode:[{}], waybillCode:[{}], userId:[{}], odoId:[{}]", outboundBoxCode, waybillCode, userId, checkingLineList.get(0).getOdoId());
                            checkingManager.printSinglePlane(outboundBoxCode, waybillCode, userId, ouId, checkingLineList.get(0).getOdoId());
                        }
                        if (CheckingPrint.BOX_LABEL.equals(checkingPrintArray[i])) {
                            isInsert  = true;
                            // 箱标签
                            checkingManager.printBoxLabel(outboundBoxCode, userId, ouId, checkingLineList.get(0).getOdoId());
                        }
                    }catch(Exception e){
                        log.error("print is error",e);
                        isInsert = false;
                    }
                    if(isInsert){
                        whPrintInfo.setBatch(checkingLineList.get(0).getBatchNumber());
                        whPrintInfo.setWaveCode(checkingCmd.getWaveCode());
                        whPrintInfo.setOuId(ouId);
                        whPrintInfo.setOutboundboxId(outboundboxId);
                        whPrintInfo.setOutboundboxCode(outboundBoxCode);
                        whPrintInfo.setPrintType(checkingPrintArray[i]);
                        whPrintInfo.setPrintCount(1);// 打印次数
                        whPrintInfo.setLastModifyTime(new Date());
                        whPrintInfo.setCreateTime(new Date());
                        whPrintInfo.setCreateId(userId);
                        whPrintInfo.setOdoId(odoId);
                        whPrintInfo.setWaybillCode(waybillCode);
                        whPrintInfoDao.insert(whPrintInfo);
                    }
                } else {
                    log.info("whprintInfo update is start");
                    Integer printCount = whPrintInfoLst.get(0).getPrintCount();
                    if (printCount == 0) {
                        try{
                            if (CheckingPrint.PACKING_LIST.equals(checkingPrintArray[i])) {
                                // 装箱清单
                                checkingManager.printPackingList(idsList, userId, ouId);
                                isInsert  = true;
                            }
                            if (CheckingPrint.SALES_LIST.equals(checkingPrintArray[i])) {
                                List<WhPrintInfo> printInfoLst =  whPrintInfoDao.getPrintInfoByOdoId(checkingLineList.get(0).getOdoId(), ouId);
                                if(null == printInfoLst || 0 == printInfoLst.size()) {
                                    idsList.add(checkingLineList.get(0).getOdoId());
                                    isInsert  = true;
                                    // 销售清单
                                    checkingManager.printSalesList(idsList, userId, ouId);
                                }else{
                                    if(printInfoLst.size() ==1 && printInfoLst.get(0).getPrintCount() == 0){
                                        idsList.add(checkingLineList.get(0).getOdoId());
                                        isInsert  = true;
                                        // 销售清单
                                        checkingManager.printSalesList(idsList, userId, ouId);
                                    }
                                }
                            }
                            if (CheckingPrint.SINGLE_PLANE.equals(checkingPrintArray[i])) {
                                isInsert  = true;
                                // 面单
                                checkingManager.printSinglePlane(outboundBoxCode, waybillCode, userId, ouId, checkingLineList.get(0).getOdoId());
                            }
                            if (CheckingPrint.BOX_LABEL.equals(checkingPrintArray[i])) {
                                isInsert  = true;
                                // 箱标签
                                checkingManager.printBoxLabel(outboundBoxCode, userId, ouId, checkingLineList.get(0).getOdoId());
                            }
                        }catch(Exception e) {
                            log.error("print is error",e);
                            isInsert = false;
                        }
                        if(isInsert){
                            WhPrintInfo printfo = whPrintInfoLst.get(0);
                            printfo.setPrintCount(printfo.getPrintCount() + 1);
                            printfo.setPrintType(checkingPrintArray[i]);
                            printfo.setModifiedId(userId);
                            printfo.setOutboundboxId(outboundboxId);
                            printfo.setOutboundboxCode(outboundBoxCode);
                            printfo.setOdoId(odoId);
                            printfo.setBatch(checkingLineList.get(0).getBatchNumber());
                            printfo.setWaveCode(checkingCmd.getWaveCode());
                            printfo.setWaybillCode(waybillCode);
                            whPrintInfoDao.saveOrUpdateByVersion(printfo);
                        }
                    }
                }
            }
        }
        log.info("whcheckingManagerImpl printDefect is end");
        return isSuccess;
    }

    /***
     * 绑定运单号
     * @param command
     * @return
     */
    public WhCheckingByOdoResultCommand bindkWaybillCode(Long functionId, Long ouId, Long odoId, String outboundboxCode, Long consumableSkuId, Boolean binding) {
        log.info("whcheckingManagerImpl bindkWaybillCode is start");
        WhCheckingByOdoResultCommand command = new WhCheckingByOdoResultCommand();
        String waybillType = "";
        try {
            waybillType = findWaybillInfo(odoId, ouId);
        } catch (Exception e) {
            command.setMessage(Constants.FIND_WAYBILL_CODE_ERROR);
            return command;
        }
        if (!StringUtils.hasLength(waybillType)) {
            command.setMessage(Constants.FIND_WAYBILL_CODE_ERROR);
            return command;
        }
        command.setWaybillType(waybillType);
        if (Constants.ELECTRONIC_WAY_BILL.equals(waybillType)) {
            // 电子面单
            // command.setMessage("请输入一个电子面单");
            WhOdodeliveryInfo info = null;
            List<WhOdodeliveryInfo> infoList = new ArrayList<WhOdodeliveryInfo>();
            if (binding) {
                WhOdodeliveryInfo whOdodeliveryInfo = new WhOdodeliveryInfo();
                whOdodeliveryInfo.setOdoId(odoId);
                whOdodeliveryInfo.setOuId(ouId);
                whOdodeliveryInfo.setOutboundboxCode(outboundboxCode);
                infoList = whOdoDeliveryInfoManager.findByParams(whOdodeliveryInfo);
            } else {
                infoList = whOdoDeliveryInfoManager.findByOdoIdWithoutOutboundbox(odoId, ouId);
            }
            if (null == infoList || infoList.isEmpty()) {
                info = odoManagerProxy.getLogisticsInfoByOdoId(odoId, logId, ouId);
            } else {
                info = infoList.get(0);
            }
            if (info == null) {
                log.info("==================waybill code is null==================");
                command.setMessage(Constants.FIND_WAYBILL_CODE_ERROR);
                return command;
            }
            WhOdodeliveryInfo deliveryInfo = whOdoDeliveryInfoDao.findByIdExt(info.getId(), ouId);
            deliveryInfo.setOutboundboxCode(outboundboxCode);
            if (null != consumableSkuId) {
                deliveryInfo.setOutboundboxId(consumableSkuId);
            }
            log.info("===================start whOdoDeliveryInfoManager.saveOrUpdate===================");
            this.whOdoDeliveryInfoManager.saveOrUpdate(deliveryInfo);
            log.info("===================end whOdoDeliveryInfoManager.saveOrUpdate===================");
            // 判断是否扫描运单号
            WhFunctionOutBound whFunctionOutBound = whFunctionOutBoundDao.findByFunctionIdExt(functionId, ouId);
            if (null == whFunctionOutBound) {
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
            }
            command.setIsScanWaybillCode(whFunctionOutBound.getIsScanWayBill());
            command.setWaybillCode(deliveryInfo.getWaybillCode());
            log.info("whcheckingManagerImpl bindkWaybillCode is end");
        } else {
            // 纸质面单
            // command.setMessage("请输入一个纸质面单");
        }
        return command;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCheckingByOdoCommand> retrieveCheckDataGeneral(WhCheckingCommand command) {
        List<WhCheckingByOdoCommand> generallist = new ArrayList<WhCheckingByOdoCommand>();
        Long ouId = command.getOuId();
        String batch = command.getBatch();
        // 复核头信息
        List<WhCheckingCommand> checkingList = whCheckingDao.findByBatchAndOuId(batch, ouId);
        // List<WhCheckingCommand> checkingList = whCheckingDao.findListByParamExt(command);
        if (null != checkingList && !checkingList.isEmpty()) {
            for (WhCheckingCommand checkingcommand : checkingList) {
                WhCheckingByOdoCommand whCheckingByOdoCommand = new WhCheckingByOdoCommand();
                whCheckingByOdoCommand.setCheckingCommand(checkingcommand);
                whCheckingByOdoCommand = findWhCheckingLineByChecking(whCheckingByOdoCommand);
                whCheckingByOdoCommand = this.findCheckingInfo(whCheckingByOdoCommand);
                generallist.add(whCheckingByOdoCommand);
            }
            // whCheckingByOdoCommand.setCheckingCommand(whCheckingCommand);
            // 获取复合明细
            // 获取页面显示
            // return whCheckingByOdoCommand;
            return generallist;
        } else {
            throw new BusinessException("check is null");
        }
    }

    /**
     * 生成出库箱库存(按单复合,删除原来的库存生成新的出库箱库存)
     */
    private void addOutBoundInventory(WhCheckingByOdoResultCommand cmd, Boolean isTabbInvTotal, Long userId) {
        String checkingPattern = cmd.getCheckingPattern(); //
        Long ouId = cmd.getOuId();
        Integer containerLatticeNo = cmd.getContainerLatticeNo(); // 货格号
        String outboundboxCode = cmd.getOutboundBoxCode();
        String seedingWallCode = cmd.getSeedingWallCode(); // 播种墙编码
        List<String> cacehSnList = cmd.getSn();
        String turnoverBoxCode = cmd.getTurnoverBoxCode();
        Long turnoverBoxId = null;
        if (Constants.WAY_5.equals(checkingPattern)) {
            ContainerCommand c = containerDao.getContainerByCode(turnoverBoxCode, ouId);
            if (null == c) {
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
            }
            turnoverBoxId = c.getId();
        }
        String containerCode = cmd.getContaierCode();

        Long containerId = null;
        if (Constants.WAY_2.equals(checkingPattern) || Constants.WAY_1.equals(checkingPattern)) {
            ContainerCommand container = containerDao.getContainerByCode(containerCode, ouId);
            if (null == container) {
                throw new BusinessException(ErrorCodes.COMMON_CONTAINER_CODE_IS_NULL_ERROR);
            }
            containerId = container.getId();
        }
        /** 复合明细集合 */
        List<WhCheckingLineCommand> checkingLineList = cmd.getCheckingLineList();
        for (WhCheckingLineCommand checkingLine : checkingLineList) {
            Long odoLineId = checkingLine.getOdoLineId();
            Long odoId = checkingLine.getOdoId();
            List<WhSkuInventoryCommand> skuInvSnList = null;
            // 小车货格
            if (Constants.WAY_2.equals(checkingPattern)) {
                log.info("outercontainer+lattice branch: odoLineId:[{}], odoId:[{}], ouId:[{}], containerId:[{}], containerLatticeNo:[{}]", odoLineId, odoId, ouId, containerId, containerLatticeNo);
                skuInvSnList = whSkuInventoryDao.getWhSkuInventorySnCommandByOdo(odoLineId, odoId, ouId, containerId, containerLatticeNo, null, null, null);
            }
            // 小车出库箱
            if (Constants.WAY_1.equals(checkingPattern)) {
                log.info("outercontiner+outboundbox branch: odoLineId:[{}], odoId:[{}], ouId:[{}], containerId:[{}], outboundboxCode:[{}]", odoLineId, odoId, ouId, containerId, outboundboxCode);
                skuInvSnList = whSkuInventoryDao.getWhSkuInventorySnCommandByOdo(odoLineId, odoId, ouId, containerId, null, outboundboxCode, null, null);
            }
            // 播种墙货格
            if (Constants.WAY_4.equals(checkingPattern)) {
                log.info("seedingwall+lattice branch: odoLineId:[{}], odoId:[{}], ouId:[{}], containerLatticeNo:[{}], seedingWallCode:[{}]", odoLineId, odoId, ouId, containerLatticeNo, seedingWallCode);
                skuInvSnList = whSkuInventoryDao.getWhSkuInventorySnCommandByOdo(odoLineId, odoId, ouId, null, containerLatticeNo, null, null, seedingWallCode);
            }
            // 播种墙出库箱
            if (Constants.WAY_3.equals(checkingPattern)) {
                log.info("seedingwall+outboundbox branch: odoLineId:[{}], odoId:[{}], ouId:[{}], outboundboxCode:[{}], seedingWallCode:[{}]", odoLineId, odoId, ouId, outboundboxCode, seedingWallCode);
                skuInvSnList = whSkuInventoryDao.getWhSkuInventorySnCommandByOdo(odoLineId, odoId, ouId, null, null, outboundboxCode, null, seedingWallCode);
            }
            // 周转箱
            if (Constants.WAY_5.equals(checkingPattern)) {
                log.info("container branch: odoLineId:[{}], odoId:[{}], ouId:[{}], turnoverBoxId:[{}]", odoLineId, odoId, ouId, turnoverBoxId);
                skuInvSnList = whSkuInventoryDao.getWhSkuInventorySnCommandByOdo(odoLineId, odoId, ouId, null, null, null, turnoverBoxId, null);
            }
            // 只有出库箱
            if (Constants.WAY_6.equals(checkingPattern)) {
                log.info("outboundbox branch: odoLineId:[{}], odoId:[{}], ouId:[{}], outboundboxCode:[{}]", odoLineId, odoId, ouId, outboundboxCode);
                skuInvSnList = whSkuInventoryDao.getWhSkuInventorySnCommandByOdo(odoLineId, odoId, ouId, null, null, outboundboxCode, null, null);
            }
            if (null != skuInvSnList && skuInvSnList.size() == 0) {
                log.info("null != skuInvSnList && skuInvSnList.size() == 0");
                throw new BusinessException(ErrorCodes.CONTAINER_INVENTORY_NO_EXIST);
            }
            Boolean isContainue = false;
            for (WhSkuInventoryCommand invSnCmd : skuInvSnList) {
                if (invSnCmd.getUuid().equals(checkingLine.getUuid())) {
                    List<WhSkuInventoryCommand> skuInvList = null;
                    // 小车货格
                    if (Constants.WAY_2.equals(checkingPattern)) {
                        log.info("outercontainer+lattice branch: odoLineId:[{}], odoId:[{}], ouId:[{}], containerId:[{}], containerLatticeNo:[{}], uuid:[{}]", odoLineId, odoId, ouId, containerId, containerLatticeNo, checkingLine.getUuid());
                        skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId, odoId, ouId, containerId, containerLatticeNo, null, null, null, checkingLine.getUuid());
                    }
                    // 小车出库箱
                    if (Constants.WAY_1.equals(checkingPattern)) {
                        log.info("outercontiner+outboundbox branch: odoLineId:[{}], odoId:[{}], ouId:[{}], containerId:[{}], outboundboxCode:[{}], uuid:[{}]", odoLineId, odoId, ouId, containerId, containerLatticeNo, checkingLine.getUuid());
                        skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId, odoId, ouId, containerId, null, outboundboxCode, null, null, checkingLine.getUuid());
                    }
                    // 播种墙货格
                    if (Constants.WAY_4.equals(checkingPattern)) {
                        log.info("seedingwall+lattice branch: odoLineId:[{}], odoId:[{}], ouId:[{}], containerLatticeNo:[{}], seedingWallCode:[{}], uuid:[{}]", odoLineId, odoId, ouId, containerLatticeNo, seedingWallCode, checkingLine.getUuid());
                        skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId, odoId, ouId, null, containerLatticeNo, null, null, seedingWallCode, checkingLine.getUuid());
                    }
                    // 播种墙出库箱
                    if (Constants.WAY_3.equals(checkingPattern)) {
                        log.info("seedingwall+outboundbox branch: odoLineId:[{}], odoId:[{}], ouId:[{}], outboundboxCode:[{}], seedingWallCode:[{}], uuid:[{}]", odoLineId, odoId, ouId, outboundboxCode, seedingWallCode, checkingLine.getUuid());
                        skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId, odoId, ouId, null, null, outboundboxCode, null, seedingWallCode, checkingLine.getUuid());
                    }
                    // 周转箱
                    if (Constants.WAY_5.equals(checkingPattern)) {
                        log.info("container branch: odoLineId:[{}], odoId:[{}], ouId:[{}], turnoverBoxId:[{}], uuid:[{}]", odoLineId, odoId, ouId, turnoverBoxId, outboundboxCode);
                        skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId, odoId, ouId, null, null, null, turnoverBoxId, null, checkingLine.getUuid());
                    }
                    // 只有出库箱
                    if (Constants.WAY_6.equals(checkingPattern)) {
                        log.info("outboundbox branch: odoLineId:[{}], odoId:[{}], ouId:[{}], outboundboxCode:[{}], uuid:[{}]", odoLineId, odoId, ouId, outboundboxCode, checkingLine.getUuid());
                        skuInvList = whSkuInventoryDao.getWhSkuInventoryCommandByOdo(odoLineId, odoId, ouId, null, null, outboundboxCode, null, null, checkingLine.getUuid());
                    }
                    Double sum = 0.0;
                    if (null != skuInvList && skuInvList.size() == 0) {
                        throw new BusinessException(ErrorCodes.CONTAINER_INVENTORY_NO_EXIST);
                    }
                    for (WhSkuInventoryCommand invCmd : skuInvList) {// 一单多箱的情况库存记录大于复合明细记录,
                        sum += invCmd.getOnHandQty();
                        if (sum.doubleValue() == Double.valueOf(checkingLine.getCheckingQty()).doubleValue()) {
                            this.addOutBoundBoxInventory(cacehSnList, invCmd, invSnCmd, invCmd.getOnHandQty(), outboundboxCode, isTabbInvTotal, ouId, userId);
                            // 删除容器库存
                            this.deleteContainerInventory(invCmd, isTabbInvTotal, ouId, userId);
                            isContainue = true;
                            break;
                        }
                        if (sum.doubleValue() < Double.valueOf(checkingLine.getCheckingQty()).doubleValue()) {
                            this.addOutBoundBoxInventory(cacehSnList, invCmd, invSnCmd, invCmd.getOnHandQty(), outboundboxCode, isTabbInvTotal, ouId, userId);
                            // 删除容器库存
                            this.deleteContainerInventory(invCmd, isTabbInvTotal, ouId, userId);
                            continue;
                        }
                        if (sum.doubleValue() > Double.valueOf(checkingLine.getCheckingQty()).doubleValue()) {
                            Double qty = Double.valueOf(checkingLine.getCheckingQty()) - (sum - invCmd.getOnHandQty()); // 要生成出库箱库存的sku数量
                            this.addOutBoundBoxInventory(cacehSnList, invCmd, invSnCmd, qty, outboundboxCode, isTabbInvTotal, ouId, userId);
                            // 修改容器库存
                            this.updateContainerInventory(invCmd.getOnHandQty() - qty, invCmd, ouId, userId, isTabbInvTotal, qty);
                            isContainue = true;
                            break;
                        }
                    }
                    if (isContainue) {
                        break;
                    }
                }
            }

        }
        // 校验出库箱库存
        List<WhSkuInventoryCommand> listSkuInvCmd = whSkuInventoryDao.findOutboundboxInventory(outboundboxCode, ouId);
        if (null != listSkuInvCmd && listSkuInvCmd.size() == 0) {
            throw new BusinessException(ErrorCodes.CONTAINER_INVENTORY_NO_EXIST);
        }
    }

    private void addOutBoundBoxInventory(List<String> cacehSnList, WhSkuInventoryCommand invCmd, WhSkuInventoryCommand invSnCmd, Double qty, String outboundboxCode, Boolean isTabbInvTotal, Long ouId, Long userId) {
        List<WhSkuInventorySnCommand> snList = invSnCmd.getWhSkuInventorySnCommandList();
        String uuid = invCmd.getUuid();
        if (null == snList || 0 == snList.size()) {// 没有sn
            String odoUuid = null;
            WhSkuInventory skuInv = new WhSkuInventory();
            BeanUtils.copyProperties(invCmd, skuInv);
            skuInv.setId(null);
            skuInv.setLocationId(null);
            skuInv.setOuterContainerId(null);
            skuInv.setInsideContainerId(null);
            skuInv.setContainerLatticeNo(null);
            skuInv.setSeedingWallCode(null);
            skuInv.setOutboundboxCode(outboundboxCode); // 出库箱编码
            try {
                odoUuid = SkuInventoryUuid.invUuid(skuInv);
                skuInv.setUuid(uuid);// UUID
            } catch (Exception e) {
                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            Double oldQty = 0.0;
            if (true == isTabbInvTotal) {
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            skuInv.setUuid(odoUuid);
            skuInv.setOnHandQty(qty);
            skuInv.setFrozenQty(0.0);
            skuInv.setLastModifyTime(new Date());
            whSkuInventoryDao.insert(skuInv);
            insertGlobalLog(GLOBAL_LOG_INSERT, skuInv, ouId, userId, null, null);
            // 记录入库库存日志(这个实现的有问题)
            insertSkuInventoryLog(skuInv.getId(), skuInv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId, InvTransactionType.CHECK);
        } else {// 有sn
            String odoUuid = null;
            WhSkuInventory skuInv = new WhSkuInventory();
            BeanUtils.copyProperties(invCmd, skuInv);
            skuInv.setId(null);
            skuInv.setLocationId(null);
            skuInv.setOuterContainerId(null);
            skuInv.setInsideContainerId(null);
            skuInv.setContainerLatticeNo(null);
            skuInv.setSeedingWallCode(null);
            skuInv.setOutboundboxCode(outboundboxCode); // 出库箱编码
            try {
                odoUuid = SkuInventoryUuid.invUuid(skuInv);
                skuInv.setUuid(uuid);// UUID
            } catch (Exception e) {
                log.error(getLogMsg("inv uuid error, logId is:[{}]", new Object[] {logId}), e);
                throw new BusinessException(ErrorCodes.COMMON_INV_PROCESS_UUID_ERROR);
            }
            Double oldQty = 0.0;
            if (true == isTabbInvTotal) {
                try {
                    oldQty = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid, ouId);
                } catch (Exception e) {
                    log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                    throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
                }
            } else {
                oldQty = 0.0;
            }
            skuInv.setUuid(odoUuid);
            skuInv.setOnHandQty(qty);
            skuInv.setFrozenQty(0.0);
            skuInv.setLastModifyTime(new Date());
            whSkuInventoryDao.insert(skuInv);
            insertGlobalLog(GLOBAL_LOG_INSERT, skuInv, ouId, userId, null, null);
            // 记录入库库存日志(这个实现的有问题)
            insertSkuInventoryLog(skuInv.getId(), skuInv.getOnHandQty(), oldQty, isTabbInvTotal, ouId, userId, InvTransactionType.CHECK);
            // String uuid1 = invCmd.getUuid();
            // Double oldQty1 = 0.0;
            // if (true == isTabbInvTotal) {
            // try {
            // oldQty1 = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid1, ouId);
            // } catch (Exception e) {
            // log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
            // throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
            // }
            // } else {
            // oldQty1 = 0.0;
            // }
            // insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty1,
            // isTabbInvTotal, ouId, userId, InvTransactionType.CHECK);
            // // 删除原来的库存
            // whSkuInventoryDao.deleteWhSkuInventoryById(invCmd.getId(), ouId);
            // 操作sn/残次信息
            int count = 0;
            for (WhSkuInventorySnCommand cSnCmd : snList) {
                for (String sn : cacehSnList) {
                    if (sn.equals(cSnCmd.getSn()) || sn.equals(cSnCmd.getDefectWareBarcode())) {
                        WhSkuInventorySn skuSn = new WhSkuInventorySn();
                        BeanUtils.copyProperties(cSnCmd, skuSn);
                        skuSn.setUuid(odoUuid);
                        whSkuInventorySnDao.saveOrUpdate(skuSn); // 更新sn
                        insertGlobalLog(GLOBAL_LOG_UPDATE, skuSn, ouId, userId, null, null);
                        insertSkuInventorySnLog(skuSn.getId(), ouId); // 记录sn日志
                        count++;
                    }
                }
                if (count == cacehSnList.size()) {
                    break;
                }
            }
        }

    }

    private void deleteContainerInventory(WhSkuInventoryCommand invCmd, Boolean isTabbInvTotal, Long ouId, Long userId) {
        String uuid1 = invCmd.getUuid();
        Double oldQty1 = 0.0;
        if (true == isTabbInvTotal) {
            try {
                oldQty1 = whSkuInventoryLogManager.sumSkuInvOnHandQty(uuid1, ouId);
            } catch (Exception e) {
                log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
            }
        } else {
            oldQty1 = 0.0;
        }
        insertSkuInventoryLog(invCmd.getId(), -invCmd.getOnHandQty(), oldQty1, isTabbInvTotal, ouId, userId, InvTransactionType.CHECK);
        // 删除原来的容器库存
        WhSkuInventory skuInv = new WhSkuInventory();
        BeanUtils.copyProperties(invCmd, skuInv);
        whSkuInventoryDao.deleteWhSkuInventoryById(skuInv.getId(), ouId);
        insertGlobalLog(GLOBAL_LOG_DELETE, skuInv, ouId, userId, null, null);
    }

    public void updateContainerInventory(Double qty, WhSkuInventoryCommand invCmd, Long ouId, Long userId, Boolean isTabbInvTotal, Double oldQty) {
        WhSkuInventory skuInv = new WhSkuInventory();
        BeanUtils.copyProperties(invCmd, skuInv);
        skuInv.setOnHandQty(qty);
        Double oldQty1 = 0.0;
        if (true == isTabbInvTotal) {
            try {
                oldQty1 = whSkuInventoryLogManager.sumSkuInvOnHandQty(skuInv.getUuid(), ouId);
            } catch (Exception e) {
                log.error("sum sku inv onHand qty error, logId is:[{}]", logId);
                throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
            }
        } else {
            oldQty1 = 0.0;
        }
        whSkuInventoryDao.saveOrUpdateByVersion(skuInv);
        insertGlobalLog(GLOBAL_LOG_UPDATE, skuInv, ouId, userId, null, null);
        insertSkuInventoryLog(invCmd.getId(), -oldQty, oldQty1, isTabbInvTotal, ouId, userId, InvTransactionType.CHECK);
    }
}
