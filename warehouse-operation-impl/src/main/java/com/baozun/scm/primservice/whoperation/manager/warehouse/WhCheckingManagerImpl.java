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

import java.util.ArrayList;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhCheckingCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingStatus;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundFacilityDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSeedingCollectionDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhChecking;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhCheckingLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhDistributionPatternRule;

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
    public WhCheckingCommand checkInput(WhCheckingCommand whCheckingCommand) {
        String input = whCheckingCommand.getInput();
        if (!StringUtils.hasLength(input)) {
            throw new BusinessException("输入为空");
        }
        Boolean bingo = false;
        // 判断:播种墙
        bingo = scanSeedingWall(whCheckingCommand);
        if (bingo) {
            return whCheckingCommand;
        }
        // 判断:小车

        bingo = scanOuterContainer(whCheckingCommand);
        if (bingo) {
            return whCheckingCommand;
        }
        // 判断:出库箱
        bingo = scanOutboundBox(whCheckingCommand);
        if (bingo) {
            return whCheckingCommand;
        }
        // 判断:货格
        bingo = scanContainerLatticeNo(whCheckingCommand);
        if (bingo) {
            return whCheckingCommand;
        }
        // 判断:周转箱
        bingo = scanContainerCode(whCheckingCommand);
        if (bingo) {
            return whCheckingCommand;
        }
        // 判断:小批次
        bingo = scanBatch(whCheckingCommand);
        if (bingo) {
            return whCheckingCommand;
        }
        // 判断:出库单号
        bingo = scanOdoCode(whCheckingCommand);
        if (bingo) {
            return whCheckingCommand;
        }
        // 判断:平台订单号
        bingo = scanEcOrderCode(whCheckingCommand);
        if (bingo) {
            return whCheckingCommand;
        }
        // 判断:外部对接编码
        bingo = scanExtCode(whCheckingCommand);
        if (bingo) {
            return whCheckingCommand;
        }

        return null;

    }

    private List<WhCheckingLine> findWhCheckingLineByChecking(WhCheckingCommand checking) {
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
        List<WhCheckingLine> whCheckingLineList = whCheckingLineDao.findListByParam(whCheckingLine);
        return whCheckingLineList;
    }

    /**
     * [业务方法] 按单复核-扫描播种墙编码
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanSeedingWall(WhCheckingCommand whCheckingCommand) {
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
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
            return true;
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描播种墙编码
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanOuterContainer(WhCheckingCommand whCheckingCommand) {
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        // 根据播种墙编码查找复核数据
        List<WhCheckingCommand> whCheckingList = whCheckingDao.findListByOuterContainerCode(input, ouId);
        if (null != whCheckingList && !whCheckingList.isEmpty()) {
            // 扫描播种墙编码
            // set 小车id
            whCheckingCommand.setOuterContainerId(whCheckingList.get(0).getFacilityId());
            // set 小车code
            whCheckingCommand.setOuterContainerCode(input);
            // set 下个页面提示
            whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX_OR_NO);
            // 返回
            return true;
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描出库箱编码
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanOutboundBox(WhCheckingCommand whCheckingCommand) {
        String input = whCheckingCommand.getInput();
        WhChecking checking = new WhChecking();
        List<WhChecking> checkingList = new ArrayList<WhChecking>();
        checking.setOutboundboxCode(input);
        checkingList = whCheckingDao.findListByParam(checking);
        if (null != checkingList && !checkingList.isEmpty()) {
            // 扫描出库箱编码
            checking = checkingList.get(0);
            if (null != checking.getFacilityId() && null == whCheckingCommand.getFacilityId()) {
                // 如果出库箱在播种墙上 提示扫描播种墙
                whCheckingCommand.setTip(Constants.TIP_SEEDING_WALL);
                return true;
            } else if (null != checking.getOuterContainerId() && null == whCheckingCommand.getOuterContainerId()) {
                // 如果出库箱在小车上 提示扫描小车
                whCheckingCommand.setTip(Constants.TIP_OUTER_CONTAINER);
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
                            return true;
                        } else {
                            // 可以执行复核操作
                            whCheckingCommand.setId(ch.getId());
                            List<WhCheckingLine> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                            whCheckingCommand.setCheckingLineList(whCheckingLineList);
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
                            return true;
                        }
                    } else {
                        // 没有找到复核信息, 提示换个出库箱或者货格号扫描
                        whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX_OR_NO);
                        return true;
                    }
                } else {
                    // 查找到的复核信息没有播种墙或者小车
                    if (null != checking.getStatus() && CheckingStatus.FINISH == checking.getStatus()) {
                        // 找到的复核行状态为10,提示已复核完成
                        whCheckingCommand.setTip(Constants.TIP_FINISH);
                        // TODO
                        return true;
                    } else {
                        // 非小车或播种墙的扫描出库箱 直接完成验证 进入复核流程
                        BeanUtils.copyProperties(checking, whCheckingCommand);
                        List<WhCheckingLine> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                        whCheckingCommand.setCheckingLineList(whCheckingLineList);
                        whCheckingCommand.setOutboundboxCode(input);
                        whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                        /** 按单复核方式:出库箱流程*/
                        whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_OUTBOUND_BOX);
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
    private Boolean scanContainerLatticeNo(WhCheckingCommand whCheckingCommand) {
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
                    return true;
                } else if (null != checking.getOuterContainerId() && null == whCheckingCommand.getOuterContainerId()) {
                    // 如果货格在小车上 提示扫描小车
                    whCheckingCommand.setTip(Constants.TIP_OUTER_CONTAINER);
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
                                return true;
                            } else {
                                // 可以执行复核操作
                                whCheckingCommand.setId(ch.getId());
                                List<WhCheckingLine> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                                whCheckingCommand.setCheckingLineList(whCheckingLineList);
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
                                return true;
                            }
                        } else {
                            // 没有找到复核信息, 提示换个出库箱或者货格号扫描
                            whCheckingCommand.setTip(Constants.TIP_OUTBOUND_BOX_OR_NO);
                            return true;
                        }
                    } else {
                        // 查找到的复核信息没有播种墙或者小车, 这种场景不应该发生, 提示扫描编码不存在
                        whCheckingCommand.setTip(Constants.TIP_CODE_ERROR);
                        // return false;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            // 没找到对应编码,提示扫描编码不存在
            whCheckingCommand.setTip(Constants.TIP_CODE_ERROR);
            // return false;
        }
        return false;
    }

    /**
     * [业务方法] 按单复核-扫描周转箱编码
     * @param whCheckingCommand
     * @return
     */
    private Boolean scanContainerCode(WhCheckingCommand whCheckingCommand) {
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        WhChecking checking = new WhChecking();
        WhCheckingCommand checkingCommand = new WhCheckingCommand();
        List<WhCheckingCommand> whCheckingList = whCheckingDao.findListByContainerCode(input, ouId);
        if (null != whCheckingList && !whCheckingList.isEmpty()) {
            checkingCommand = whCheckingList.get(0);
            if (CheckingStatus.FINISH == checkingCommand.getStatus()) {
                // 找到的复核行状态为10,提示已复核完成
                whCheckingCommand.setTip(Constants.TIP_FINISH);
                // TODO
                return true;
            } else {
                // TODO
                // 需要判断是否有绑定出库箱, 意味着是否是主副品或者套装.如果有则把出库箱对应的复核明细也缓存到页面
                // 可以执行复核操作
                BeanUtils.copyProperties(checkingCommand, checking);
                List<WhCheckingLine> whCheckingLineList = findWhCheckingLineByChecking(checkingCommand);
                whCheckingCommand.setCheckingLineList(whCheckingLineList);
                whCheckingCommand.setContainerId(checking.getContainerId());
                /** 按单复核方式:周转箱流程*/
                whCheckingCommand.setoDCheckWay(Constants.CHECKING_BY_ODO_WAY_CONTAINER);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
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
    private Boolean scanBatch(WhCheckingCommand whCheckingCommand) {
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
                return true;
            } else {
                // 可以执行复核操作
                whCheckingCommand.setId(checking.getId());
                List<WhCheckingLine> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                whCheckingCommand.setCheckingLineList(whCheckingLineList);
                whCheckingCommand.setBatch(input);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
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
    private Boolean scanOdoCode(WhCheckingCommand whCheckingCommand) {
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        WhOdo odo = whOdoDao.findOdoByCodeAndOuId(input, ouId);
        if (null != odo) {
            Long odoId = odo.getId();
            WhDistributionPatternRule distributionPatternRule = whDistributionPatternRuleDao.findByOdoIdAndOuId(odoId, ouId);
            if (null != distributionPatternRule && (Constants.PICKING_MODE_PICKING.equals(distributionPatternRule.getPickingMode()) || Constants.PICKING_MODE_SEED.equals(distributionPatternRule.getPickingMode()))) {
                whCheckingCommand.setOdoId(odoId);
                List<WhCheckingLine> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                whCheckingCommand.setCheckingLineList(whCheckingLineList);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
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
    private Boolean scanEcOrderCode(WhCheckingCommand whCheckingCommand) {
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        WhOdo odo = whOdoDao.findOdoByEcOrderCodeAndOuId(input, ouId);
        if (null != odo) {
            Long odoId = odo.getId();
            WhDistributionPatternRule distributionPatternRule = whDistributionPatternRuleDao.findByOdoIdAndOuId(odoId, ouId);
            if (null != distributionPatternRule && (Constants.PICKING_MODE_PICKING.equals(distributionPatternRule.getPickingMode()) || Constants.PICKING_MODE_SEED.equals(distributionPatternRule.getPickingMode()))) {
                whCheckingCommand.setOdoId(odoId);
                List<WhCheckingLine> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                whCheckingCommand.setCheckingLineList(whCheckingLineList);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
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
    private Boolean scanExtCode(WhCheckingCommand whCheckingCommand) {
        String input = whCheckingCommand.getInput();
        Long ouId = whCheckingCommand.getOuId();
        WhOdo odo = whOdoDao.findOdoByExtCodeAndOuId(input, ouId);
        if (null != odo) {
            Long odoId = odo.getId();
            WhDistributionPatternRule distributionPatternRule = whDistributionPatternRuleDao.findByOdoIdAndOuId(odoId, ouId);
            if (null != distributionPatternRule && (Constants.PICKING_MODE_PICKING.equals(distributionPatternRule.getPickingMode()) || Constants.PICKING_MODE_SEED.equals(distributionPatternRule.getPickingMode()))) {
                whCheckingCommand.setOdoId(odoId);
                List<WhCheckingLine> whCheckingLineList = findWhCheckingLineByChecking(whCheckingCommand);
                whCheckingCommand.setCheckingLineList(whCheckingLineList);
                whCheckingCommand.setTip(Constants.TIP_SUCCESS);
                return true;
            }
        }
        return false;
    }

    // ============================= 按单复核 end =============================

}
