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
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingByOdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.constant.WhUomType;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.UomDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionOutBoundDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOdoPackageInfoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway.SkuCategoryProvider;
import com.baozun.scm.primservice.whoperation.manager.warehouse.inventory.WhSkuInventoryManager;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhDistributionPatternRule;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOdoPackageInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.util.formula.SimpleWeightCalculator;

@Service("whCheckingManager")
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
            throw new BusinessException("输入为空");
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
        bingo = scanEcOrderCode(command);
        if (bingo) {
            return command;
        }
        // 判断:外部对接编码
        bingo = scanExtCode(command);
        if (bingo) {
            return command;
        }

        return null;

    }

    private List<WhCheckingLineCommand> findWhCheckingLineByChecking(WhCheckingCommand checking) {
        WhCheckingLine whCheckingLine = new WhCheckingLine();
        if (null == checking.getId() && null == checking.getOdoId()) {
            // 如果复核id或者出库单id为空,则不应该继续查找复核明细
            throw new BusinessException("不能查找复核明细");
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
            whCheckingLineList = setDicLabel(whCheckingLineList);
            for (WhCheckingLineCommand command : whCheckingLineList) {
                String skuAttr = SkuCategoryProvider.getSkuAttrIdByCheck(command);
                command.setSkuAttr(skuAttr);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                if (null != command.getMfgDate()) {
                    command.setMfgDateStr(format.format(command.getMfgDate()));
                }
                if (null != command.getExpDate()) {
                    command.setExpDateStr(format.format(command.getExpDate()));
                }
                // if(!StringUtils.hasLength(command.getInvType())){
                // command.setInvTypeStr("");
                // }
            }
        }
        return whCheckingLineList;
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
            // 扫描播种墙编码
            // set 播种墙id
            whCheckingCommand.setFacilityId(whCheckingList.get(0).getFacilityId());
            // set 播种墙code
            whCheckingCommand.setSeedingWallCode(input);
            // set 下个页面提示
            whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX_OR_NO);
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
            // 扫描播种墙编码
            // set 小车id
            whCheckingCommand.setOuterContainerId(whCheckingList.get(0).getOuterContainerId());
            // set 小车code
            whCheckingCommand.setOuterContainerCode(input);
            // set 下个页面提示
            whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX_OR_NO);
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
        String input = whCheckingCommand.getInput();
        WhCheckingCommand checking = new WhCheckingCommand();
        List<WhCheckingCommand> checkingList = new ArrayList<WhCheckingCommand>();
        checking.setOutboundboxCode(input);
        checking.setOuId(whCheckingCommand.getOuId());
        checkingList = whCheckingDao.findListByParamExt(checking);
        if (null != checkingList && !checkingList.isEmpty()) {
            // 扫描出库箱编码
            checking = checkingList.get(0);
            if (null != checking.getFacilityId() && null == whCheckingCommand.getFacilityId()) {
                // 如果出库箱在播种墙上 提示扫描播种墙
                whCheckingCommand.setTip(Constants.TIP_SEEDING_WALL);
                command.setCheckingCommand(whCheckingCommand);
                return true;
            } else if (null != checking.getOuterContainerId() && null == whCheckingCommand.getOuterContainerId()) {
                // 如果出库箱在小车上 提示扫描小车
                whCheckingCommand.setTip(Constants.TIP_OUTER_CONTAINER);
                command.setCheckingCommand(whCheckingCommand);
                return true;
            } else {
                if (null != whCheckingCommand.getFacilityId() || null != whCheckingCommand.getOuterContainerId()) {
                    // 已经有小车id或者播种墙id
                    WhChecking ch = new WhChecking();
                    ch.setFacilityId(whCheckingCommand.getFacilityId());
                    ch.setOuterContainerId(whCheckingCommand.getOuterContainerId());
                    ch.setOutboundboxCode(input);
                    List<WhChecking> chList = this.whCheckingDao.findListByParam(ch);
                    if (null != chList && !chList.isEmpty()) {
                        // 根据已有条件找到复核信息
                        ch = chList.get(0);
                        if (CheckingStatus.FINISH == ch.getStatus()) {
                            // 找到的复核行状态为10,提示已复核完成
                            whCheckingCommand.setTip(Constants.TIP_FINISH);
                            // TODO
                            command.setCheckingCommand(whCheckingCommand);
                            return true;
                        } else {
                            // 可以执行复核操作
                            whCheckingCommand.setId(ch.getId());
                            List<WhCheckingLineCommand> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                            command.setCheckingLineCommandList(whCheckingLineList);
                            whCheckingCommand.setOutboundboxCode(ch.getOutboundboxCode());
                            whCheckingCommand.setOutboundboxId(ch.getOutboundboxId());
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
                            return true;
                        }
                    } else {
                        // 没有找到复核信息, 提示换个出库箱或者货格号扫描
                        whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX_OR_NO);
                        command.setCheckingCommand(whCheckingCommand);
                        return true;
                    }
                } else {
                    // 查找到的复核信息没有播种墙或者小车
                    if (null != checking.getStatus() && CheckingStatus.FINISH == checking.getStatus()) {
                        // 找到的复核行状态为10,提示已复核完成
                        whCheckingCommand.setTip(Constants.TIP_FINISH);
                        // TODO
                        command.setCheckingCommand(whCheckingCommand);
                        return true;
                    } else {
                        // 非小车或播种墙的扫描出库箱 直接完成验证 进入复核流程
                        BeanUtils.copyProperties(checking, whCheckingCommand);
                        List<WhCheckingLineCommand> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                        command.setCheckingLineCommandList(whCheckingLineList);
                        whCheckingCommand.setOutboundboxCode(input);
                        whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                        /** 按单复核方式:出库箱流程*/
                        whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTBOUND_BOX);
                        command.setCheckingCommand(whCheckingCommand);
                        return true;
                    }
                }
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
            checkingList = whCheckingDao.findListByParam(checking);
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
                        List<WhChecking> chList = this.whCheckingDao.findListByParam(ch);
                        if (null != chList && !chList.isEmpty()) {
                            // 根据已有条件找到复核信息
                            ch = chList.get(0);
                            if (CheckingStatus.FINISH == ch.getStatus()) {
                                // 找到的复核行状态为10,提示已复核完成
                                whCheckingCommand.setTip(Constants.TIP_FINISH);
                                // TODO
                                command.setCheckingCommand(whCheckingCommand);
                                return true;
                            } else {
                                // 可以执行复核操作
                                whCheckingCommand.setId(ch.getId());
                                List<WhCheckingLineCommand> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                                command.setCheckingLineCommandList(whCheckingLineList);
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
                                return true;
                            }
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
            if (CheckingStatus.FINISH == checkingCommand.getStatus()) {
                // 找到的复核行状态为10,提示已复核完成
                whCheckingCommand.setTip(Constants.TIP_FINISH);
                // TODO
                command.setCheckingCommand(whCheckingCommand);
                return true;
            } else {
                // TODO
                // 需要判断是否有绑定出库箱, 意味着是否是主副品或者套装.如果有则把出库箱对应的复核明细也缓存到页面
                if (Constants.PICKING_MODE_BATCH_GROUP.equals(checkingCommand.getPickingMode()) || Constants.PICKING_MODE_BATCH_SECKILL.equals(checkingCommand.getPickingMode()) || Constants.PICKING_MODE_BATCH_MAIN.equals(checkingCommand.getPickingMode())) {
                    whCheckingCommand.setTip(Constants.TIP_BATCH);
                    command.setCheckingCommand(whCheckingCommand);
                    return true;
                }
                // 可以执行复核操作
                BeanUtils.copyProperties(checkingCommand, whCheckingCommand);
                List<WhCheckingLineCommand> whCheckingLineList = findWhCheckingLineByChecking(checkingCommand);
                command.setCheckingLineCommandList(whCheckingLineList);
                /** 按单复核方式:周转箱流程*/
                whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_CONTAINER);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                command.setCheckingCommand(whCheckingCommand);
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
    private Boolean scanBatch(WhCheckingByOdoCommand command) {
        WhCheckingCommand whCheckingCommand = command.getCheckingCommand();
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        WhChecking checking = new WhChecking();
        checking.setBatch(input);
        checking.setOuId(ouId);
        List<WhChecking> whCheckingList = whCheckingDao.findListByParam(checking);
        if (null != whCheckingList && !whCheckingList.isEmpty()) {
            checking = whCheckingList.get(0);
            if (CheckingStatus.FINISH == checking.getStatus()) {
                // 找到的复核行状态为10,提示已复核完成
                whCheckingCommand.setTip(Constants.TIP_FINISH);
                // TODO
                command.setCheckingCommand(whCheckingCommand);
                return true;
            } else {
                if (Constants.PICKING_MODE_BATCH_GROUP.equals(checking.getPickingMode()) || Constants.PICKING_MODE_BATCH_SECKILL.equals(checking.getPickingMode()) || Constants.PICKING_MODE_BATCH_MAIN.equals(checking.getPickingMode())) {
                    // TODO 查找对应的主副品或者套装或者秒杀

                }
                // 可以执行复核操作
                whCheckingCommand.setId(checking.getId());
                List<WhCheckingLineCommand> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                command.setCheckingLineCommandList(whCheckingLineList);
                whCheckingCommand.setBatch(input);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                command.setCheckingCommand(whCheckingCommand);
                return true;
            }
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
            WhDistributionPatternRule distributionPatternRule = whDistributionPatternRuleDao.findByOdoIdAndOuId(odoId, ouId);
            if (null != distributionPatternRule && (Constants.PICKING_MODE_PICKING.equals(distributionPatternRule.getPickingMode()) || Constants.PICKING_MODE_SEED.equals(distributionPatternRule.getPickingMode()))) {
                whCheckingCommand.setOdoId(odoId);
                List<WhCheckingLineCommand> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                command.setCheckingLineCommandList(whCheckingLineList);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                command.setCheckingCommand(whCheckingCommand);
                return true;
            }
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
                List<WhCheckingLineCommand> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                command.setCheckingLineCommandList(whCheckingLineList);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                command.setCheckingCommand(whCheckingCommand);
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
                List<WhCheckingLineCommand> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                command.setCheckingLineCommandList(whCheckingLineList);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                command.setCheckingCommand(whCheckingCommand);
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

        return whCheckingDao.findWhCheckingByIdExt(checkingId, ouId);
    }


    /**
     * tangming
     * 按单复合
     * @param checkingLineList
     */
    public void checkingByOdo(WhCheckingByOdoResultCommand cmd, Boolean isTabbInvTotal, Long userId, Long ouId, Long functionId) {
        List<WhCheckingLineCommand> checkingLineList = cmd.getCheckingLineList();
        Long outboundboxId = cmd.getOutboundboxId();
        String outboundbox = cmd.getOutboundBoxCode();
        // 更新复合明细表
        Long checkingId = this.updateCheckingByOdo(checkingLineList, ouId);
        cmd.setOuId(ouId);
        // 生成出库箱库存(sn有问题)
        whSkuInventoryManager.addOutBoundInventory(cmd, isTabbInvTotal, userId);
        // 获取出库单id
        WhCheckingLineCommand lineCmd = checkingLineList.get(0);
        Long odoId = lineCmd.getOdoId();
        // 出库箱头信息（t_wh_outboundbox）和出库箱明细
        this.addOutboundbox(checkingId, ouId, odoId, outboundbox, lineCmd);
        // 算包裹计重????
        this.packageWeightCalculationByOdo(checkingLineList, functionId, ouId, odoId, outboundboxId, userId, outboundbox);
        Boolean result = whCheckingLineManager.judeIsLastBox(ouId, odoId);
        if (result) {
            // 更新出库单状态
            this.updateOdoStatusByOdo(odoId, ouId);
        }
    }


    /**
     * tangming
     * 按单复合更新复合表
     * @param checkingLineList
     */
    private Long updateCheckingByOdo(List<WhCheckingLineCommand> checkingLineList, Long ouId) {
        for (WhCheckingLineCommand cmd : checkingLineList) {
            Long id = cmd.getId(); // 复合明细id
            Long checkingQty = cmd.getCheckingQty(); // 复合明细数量
            WhCheckingLineCommand lineCmd = whCheckingLineDao.findCheckingLineById(id, ouId);
            WhCheckingLine line = new WhCheckingLine();
            BeanUtils.copyProperties(lineCmd, line);
            line.setCheckingQty(checkingQty);
            whCheckingLineDao.saveOrUpdateByVersion(line);
        }
        Long checkingId = null;
        for (WhCheckingLineCommand lineCmd : checkingLineList) {
            checkingId = lineCmd.getCheckingId(); // 复合头id
            break;
        }
        // 更新复合头状态
        WhCheckingCommand checkingCmd = whCheckingDao.findWhCheckingByIdExt(checkingId, ouId);
        if (null == checkingCmd) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        WhChecking checking = new WhChecking();
        BeanUtils.copyProperties(checkingCmd, checking);
        checking.setStatus(CheckingStatus.FINISH);
        whCheckingDao.saveOrUpdate(checking);

        return checkingId;
    }



    /**
     * 出库箱头信息
     * 
     * @param whCheckingResultCommand
     */
    private void addOutboundbox(Long checkingId, Long ouId, Long odoId, String outboundbox, WhCheckingLineCommand lineCmd) {
        WhCheckingCommand checkingCmd = whCheckingDao.findWhCheckingByIdExt(checkingId, ouId);
        if (null == checkingCmd) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        WhOutboundbox whOutboundbox = new WhOutboundbox();
        BeanUtils.copyProperties(checkingCmd, whOutboundbox);
        whOutboundbox.setOuId(ouId);
        whOutboundbox.setOdoId(odoId);
        whOutboundboxDao.insert(whOutboundbox);
        // 生成出库想明细信息
        List<WhSkuInventoryCommand> listSkuInvCmd = whSkuInventoryManager.findOutboundboxInventory(outboundbox, ouId);
        // 添加出库箱明细
        for (WhSkuInventoryCommand skuInvCmd : listSkuInvCmd) {
            WhOutboundboxLine outboundboxLine = new WhOutboundboxLine();
            outboundboxLine.setWhOutboundboxId(whOutboundbox.getId());
            outboundboxLine.setSkuCode(lineCmd.getSkuCode());
            outboundboxLine.setSkuExtCode(lineCmd.getSkuExtCode());
            outboundboxLine.setSkuBarCode(lineCmd.getSkuBarCode());
            outboundboxLine.setSkuName(lineCmd.getSkuName());
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
            }
        }
        SimpleWeightCalculator weightCalculator = new SimpleWeightCalculator(weightUomConversionRate);
        Double sum = 0.0;
        for (WhCheckingLineCommand whCheckingLineCommand : checkingLineList) {
            Double actualWeight = 0.0;
            WhSkuCommand whSkuCommand = whSkuManager.getSkuBybarCode(whCheckingLineCommand.getSkuBarCode(), ouId);
            actualWeight = whSkuCommand.getWeight() * whCheckingLineCommand.getCheckingQty();
            sum += actualWeight;

        }
        WhOdoPackageInfo odoPackageInfo = whOdoPackageInfoDao.findByOutboundBoxCode(outboundboxCode, ouId);
        if (null != odoPackageInfo) {
            odoPackageInfo.setCalcWeight(sum.longValue());
            whOdoPackageInfoDao.saveOrUpdateByVersion(odoPackageInfo);
        } else {
            WhFunctionOutBound whFunctionOutBound = whFunctionOutBoundDao.findByFunctionIdExt(functionId, ouId);
            WhOdoPackageInfo whOdoPackageInfo = new WhOdoPackageInfo();
            whOdoPackageInfo.setOdoId(odoId);
            whOdoPackageInfo.setOutboundboxId(outboundboxId);
            whOdoPackageInfo.setOutboundboxCode(outboundboxCode);
            whOdoPackageInfo.setStatus(Constants.LIFECYCLE_START);
            whOdoPackageInfo.setFloats(1);
            whOdoPackageInfo.setLifecycle(Constants.LIFECYCLE_START);
            whOdoPackageInfo.setCreateId(userId);
            whOdoPackageInfo.setCreateTime(new Date());
            whOdoPackageInfo.setLastModifyTime(new Date());
            whOdoPackageInfo.setModifiedId(userId);
            whOdoPackageInfo.setCalcWeight(sum.longValue());
            whOdoPackageInfo.setOuId(ouId);
            whOdoPackageInfoDao.insert(whOdoPackageInfo);
        }
    }

    /**
     * 更新出库单状态(按单复合只更新一个出库单)
     * 
     * 
     * @author
     * @param whCheckingResultCommand
     */
    private void updateOdoStatusByOdo(Long odoId, Long ouId) {
        // 修改出库单状态为复核完成状态。
        WhOdo whOdo = whOdoDao.findByIdOuId(odoId, ouId);
        // 修改出库单状态为复核完成状态。
        whOdo.setHeadStartOdoStatus(OdoStatus.SEEDING);
        whOdoDao.saveOrUpdateByVersion(whOdo);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhCheckingByOdoCommand retrieveCheckData(WhCheckingCommand whCheckingCommand) {
        WhCheckingByOdoCommand whCheckingByOdoCommand = new WhCheckingByOdoCommand();
        List<WhCheckingCommand> checkingList = whCheckingDao.findListByParamExt(whCheckingCommand);
        if (null != checkingList && !checkingList.isEmpty()) {
            whCheckingCommand = checkingList.get(0);
            List<WhCheckingLineCommand> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
            whCheckingByOdoCommand.setCheckingCommand(whCheckingCommand);
            whCheckingByOdoCommand.setCheckingLineCommandList(whCheckingLineList);
            return whCheckingByOdoCommand;
        } else {
            throw new BusinessException("check is null");
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateCheckOutboundBox(WhCheckingCommand whCheckingCommand) {
        WhChecking checking = new WhChecking();
        checking.setId(whCheckingCommand.getId());
        checking.setOuId(whCheckingCommand.getOuId());
        List<WhChecking> checkingList = whCheckingDao.findListByParam(checking);
        if (null != checkingList && !checkingList.isEmpty()) {
            checking = checkingList.get(0);
            checking.setOutboundboxCode(whCheckingCommand.getOutboundboxCode());
            whCheckingDao.update(checking);
        }
    }
}
