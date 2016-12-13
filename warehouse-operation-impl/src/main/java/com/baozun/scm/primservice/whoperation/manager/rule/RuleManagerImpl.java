package com.baozun.scm.primservice.whoperation.manager.rule;

import java.util.ArrayList;
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

import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.CheckOperationsAreaRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutboundBoxRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.PlatformRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendPlatformCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendRuleConditionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhDistributionPatternRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhInBoundRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventorySnCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.CheckOperationsAreaRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutboundBoxRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.PlatformRecommendRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.RecommendPlatformDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.RecommendRuleConditionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ReplenishmentRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ShelveRecommendRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhDistributionPatternRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhInBoundRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventorySnDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhInBoundRule;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

@Service("ruleManager")
@Transactional
public class RuleManagerImpl extends BaseManagerImpl implements RuleManager {

    public static final Logger log = LoggerFactory.getLogger(RuleManagerImpl.class);

    @Autowired
    private PlatformRecommendRuleDao platformRecommendRuleDao;
    @Autowired
    private RecommendPlatformDao recommendPlatformDao;
    @Autowired
    private WhInBoundRuleDao whInBoundRuleDao;
    @Autowired
    private RecommendRuleConditionDao recommendRuleConditionDao;
    @Autowired
    private ShelveRecommendRuleDao shelveRecommendRuleDao;
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;

    @Autowired
    private WhSkuInventorySnDao whSkuInventorySnDao;

    @Autowired
    private ReplenishmentRuleDao replenishmentRuleDao;
    @Autowired
    private WhDistributionPatternRuleDao whDistributionPatternRuleDao;

    @Autowired
    private OutboundBoxRuleDao outboundBoxRuleDao;

    @Autowired
    private CheckOperationsAreaRuleDao checkOperationsAreaRuleDao;

    /***
     * 根据规则传入参数返回对应规则输出参数
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public RuleExportCommand ruleExport(RuleAfferCommand ruleAffer) {
        log.info(this.getClass().getSimpleName() + ".ruleExport method begin! logid: " + ruleAffer.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[RuleAfferCommand:{}]", ruleAffer.toString());
        }
        RuleExportCommand export = null;
        // 判断规则类型
        if (StringUtil.isEmpty(ruleAffer.getRuleType())) {
            log.warn("ruleExport ruleAffer.getRuleType() is null logid: " + ruleAffer.getLogId());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        switch (ruleAffer.getRuleType()) {
            case Constants.PLATFORM_RECOMMEND_RULE:
                // 月台规则
                export = exportPlatformRecommendRule(ruleAffer);
                break;
            case Constants.INBOUND_RULE:
                // 入库分拣
                export = exportInboundRule(ruleAffer);
                break;
            case Constants.SHELVE_RECOMMEND_RULE_ALL:
                // 上架 整托盘 整箱
                export = exportShelveRuleAll(ruleAffer);
                break;
            case Constants.SHELVE_RECOMMEND_RULE:
                // 上架 拆箱
                export = exportShelveRule(ruleAffer);
                break;
            case Constants.ALLOCATE_RULE:
                // 分配规则
                export = allocateRule(ruleAffer);
                break;
            case Constants.RULE_TYPE_REPLENISHMENT_SKU:
                // 补货规则 商品
                export = exportReplenishmentRuleForSku(ruleAffer);
                break;
            case Constants.RULE_TYPE_REPLENISHMENT_LOCATION:
                // 补货规则 库位
                export = exportReplenishmentRuleForLocation(ruleAffer);
                break;
            case Constants.DISTRIBUTION_PATTERN:
                // 配货模式规则
                export = distributionPattern(ruleAffer);
                break;
            case Constants.RULE_TYPE_OUTBOUND_BOX:
                // 出库箱装箱规则
                export = exportOutboundBoxRule(ruleAffer);
                break;
            case Constants.RULE_TYPE_CHECK_OPERATIONS_AREA:
                //复核台推荐规则
                export = exportCheckOperationsAreaRule(ruleAffer);
                break;
            default:
                log.error("ruleExport ruleAffer.getRuleType() is error ruleAffer.getRuleType() = " + ruleAffer.getRuleType() + " logid: " + ruleAffer.getLogId());
                throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        log.info(this.getClass().getSimpleName() + ".ruleExport method end! logid: " + ruleAffer.getLogId());
        return export;
    }

    /**
     * 根据容器ID和库存ID验证该属性的sku是否可以放入容器
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public RuleExportCommand ruleExportContainerCode(RuleAfferCommand ruleAffer) {
        RuleExportCommand export = new RuleExportCommand();
        export.setExportContainerCode(null);
        Long sortingResult = whInBoundRuleDao.executeSortingRuleSql(ruleAffer.getInvId(), ruleAffer.getContainerId(), ruleAffer.getWhInBoundRuleCommand().getSortingSql(), ruleAffer.getOuid());
        if (null == sortingResult) {
            export.setIsSkuMatchContainer(false);
        } else {
            export.setIsSkuMatchContainer(true);
        }
        return export;
    }

    /**
     * 根据出库单ID获取排序后的出库单明细的拆分条件
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public RuleExportCommand ruleExportOutboundBoxSplitRequire(RuleAfferCommand ruleAffer) {
        OutboundBoxRuleCommand outboundBoxRuleCommand = ruleAffer.getOutboundBoxRuleCommand();
        Long odoId = ruleAffer.getOutboundBoxSortOdoId();
        Long ouId = ruleAffer.getOuid();

        List<String> odoLineIdGroupList = outboundBoxRuleDao.executeOutboundBoxTacticsSql(outboundBoxRuleCommand.getSortSql(), odoId, ouId);
        RuleExportCommand exportCommand = new RuleExportCommand();
        exportCommand.setOdoLineIdGroupList(odoLineIdGroupList);
        return exportCommand;
    }

    /**
     * 月台推荐规则
     * 
     * @return
     */
    private RuleExportCommand exportPlatformRecommendRule(RuleAfferCommand ruleAffer) {
        if (null == ruleAffer.getAfferAsnId()) {
            // 判断预约号是否为空
            log.warn("ruleExport ruleAffer.getAfferAsnId() is null logid: " + ruleAffer.getLogId());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        RuleExportCommand export = new RuleExportCommand();
        // 返回MAP值 key: 0:找不到规则 1:找到规则单月台不可用 2:找到对应月台
        Map<Integer, RecommendPlatformCommand> returnMap = new HashMap<Integer, RecommendPlatformCommand>();
        returnMap.put(Constants.NO_MATCHING_RULES, new RecommendPlatformCommand());
        List<PlatformRecommendRuleCommand> prList = platformRecommendRuleDao.findPlatformRecommendRuleByOuId(ruleAffer.getOuid());
        for (PlatformRecommendRuleCommand p : prList) {
            // 查询规则是否符合要求
            Long prr = platformRecommendRuleDao.executeRuleSql(p.getRuleSql(), ruleAffer.getOuid(), ruleAffer.getAfferAsnId());
            if (null != prr) {
                // 如果不为空 则该规则符合要求
                returnMap.clear();// 去除0:找不到规则
                returnMap.put(Constants.NONE_AVAILABLE_PLATFORMS, new RecommendPlatformCommand());// 添加1:找到规则
                RecommendPlatformCommand rpc = recommendPlatformDao.findCommandByRuleIdOrderByPriority(p.getId(), ruleAffer.getOuid());
                if (null != rpc) {
                    // 如果有可用月台 返回2:找到对应月台
                    returnMap.clear();// 去除1:找到规则
                    returnMap.put(Constants.AVAILABLE_PLATFORM, rpc);// 添加2:找到对应月台
                    break;
                }
            }
        }
        export.setExportPlatform(returnMap);
        return export;
    }

    /**
     * 整托盘 整箱上架规则匹配
     * 
     * @param ruleAffer
     * @return
     */
    private RuleExportCommand exportShelveRuleAll(RuleAfferCommand ruleAffer) {
        if (ruleAffer.getAfferInsideContainerIdList().size() == 0) {
            // 判断容器号List是否为空
            log.warn("ruleExport ruleAffer.getAfferInsideContainerIdList().size() == 0 logid: " + ruleAffer.getLogId());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        String insideContainerIdListStr = forMatAfferInsideContainerIdList(ruleAffer.getAfferInsideContainerIdList());
        RuleExportCommand export = new RuleExportCommand();
        // 查询所有对应容器号的库存信息 返回的String是"invId,invSnId"或者"invId,null"
        List<String> originInvSnIdList = whSkuInventorySnDao.findInvSnIdStrByInsideContainerId(ruleAffer.getOuid(), ruleAffer.getAfferInsideContainerIdList());
        // 存在原始库存信息
        if (null != originInvSnIdList && !originInvSnIdList.isEmpty()) {
            // 查询所有可用上架规则 并且排序
            List<ShelveRecommendRuleCommand> sList = shelveRecommendRuleDao.findShelveRecommendRuleByOuid(ruleAffer.getOuid());
            // 存放所有符合条件的规则LIST
            List<ShelveRecommendRuleCommand> returnList = new ArrayList<ShelveRecommendRuleCommand>();
            for (ShelveRecommendRuleCommand s : sList) {
                // 查询上架规则对应库存信息ID LIST
                List<String> list = shelveRecommendRuleDao.executeRuleSql(s.getRuleSql().replace(Constants.SHELVE_RULE_PLACEHOLDER, insideContainerIdListStr), ruleAffer.getOuid());
                // 如果符合上架规则的记录包含所有的原始库存记录 加入list规则对象
                if (null != list && list.containsAll(originInvSnIdList)) {
                    // 把规则加入list
                    returnList.add(s);
                }
            }
            export.setShelveRecommendRuleList(returnList);
        }
        return export;
    }

    /**
     * 拆箱上架规则匹配
     * 
     * @param ruleAffer
     * @return
     */
    private RuleExportCommand exportShelveRule(RuleAfferCommand ruleAffer) {
        if (ruleAffer.getAfferInsideContainerIdList().size() == 0) {
            // 判断容器号List是否为空
            log.warn("ruleExport ruleAffer.getAfferInsideContainerIdList().size() == 0 logid: " + ruleAffer.getLogId());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        String insideContainerIdListStr = forMatAfferInsideContainerIdList(ruleAffer.getAfferInsideContainerIdList());
        RuleExportCommand export = new RuleExportCommand();
        // 查询所有可用上架规则 并且排序
        List<ShelveRecommendRuleCommand> shelveRuleList = shelveRecommendRuleDao.findShelveRecommendRuleByOuid(ruleAffer.getOuid());
        // 存放库存记录对应哪些规则
        Map<String, List<ShelveRecommendRuleCommand>> invSnRuleMap = new HashMap<String, List<ShelveRecommendRuleCommand>>();
        for (ShelveRecommendRuleCommand shelveRule : shelveRuleList) {
            // 查询上架规则对应库存信息ID LIST
            List<String> invIdSnIdStrList = shelveRecommendRuleDao.executeRuleSql(shelveRule.getRuleSql().replace(Constants.SHELVE_RULE_PLACEHOLDER, insideContainerIdListStr), ruleAffer.getOuid());
            if (invIdSnIdStrList.size() > 0) {
                for (String invIdSnIdStr : invIdSnIdStrList) {
                    List<ShelveRecommendRuleCommand> shelveRuList = invSnRuleMap.get(invIdSnIdStr);// 库存记录对应的规则List
                    if (null == shelveRuList) {
                        // 没值new新对象
                        shelveRuList = new ArrayList<ShelveRecommendRuleCommand>();
                    }
                    // 放入list
                    shelveRuList.add(shelveRule);
                    // 放入Map
                    invSnRuleMap.put(invIdSnIdStr, shelveRuList);
                }
            }
        }
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = whSkuInventoryDao.findWhSkuInventoryForRuleByInsideContainerId(ruleAffer.getOuid(), ruleAffer.getAfferInsideContainerIdList());
        // 循环库存记录 判断库存记录是否有匹配规则
        for (WhSkuInventoryCommand inventoryCommand : invList) {
            List<WhSkuInventorySnCommand> inventorySnCommandList = whSkuInventorySnDao.findWhSkuInventoryByUuid(inventoryCommand.getOuId(), inventoryCommand.getUuid());
            if (null == inventorySnCommandList || inventorySnCommandList.isEmpty()) {
                // 查询returnMap是否有对应inv.id值
                List<ShelveRecommendRuleCommand> srr = invSnRuleMap.get(inventoryCommand.getId() + ",null");
                inventoryCommand.setShelveRecommendRuleCommandList(srr);
            } else {
                inventoryCommand.setShelveRecommendRuleCommandList(null);
                for (WhSkuInventorySnCommand inventorySnCommand : inventorySnCommandList) {
                    // 查询returnMap是否有对应inv.id值
                    List<ShelveRecommendRuleCommand> srr = invSnRuleMap.get(inventoryCommand.getId() + "," + inventorySnCommand.getId());
                    inventorySnCommand.setShelveRecommendRuleCommandList(srr);
                }
            }
            inventoryCommand.setWhSkuInventorySnCommandList(inventorySnCommandList);
        }
        export.setShelveSkuInvCommandList(invList);
        return export;
    }

    /***
     * 入库分拣规则匹配
     *
     * @param ruleAffer
     * @return
     */
    private RuleExportCommand exportInboundRule(RuleAfferCommand ruleAffer) {
        if (null == ruleAffer.getInvId()) {
            // 判断原始库存ID是否为空
            log.warn("ruleExport ruleAffer.getInvId() is null logid: " + ruleAffer.getLogId());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        RuleExportCommand export = new RuleExportCommand();
        export.setUsableness(false);
        // TODO 新增实现，验证该库存记录是否已有匹配的规则，可直接推荐已绑定的容器
        // WhContainerAssign whContainerAssign
        // whContainerAssignDao.findWhContainerAssignByInvId(Long invId, Long ouId);
        // if(null != whContainerAssign){
        // export.setWhContainerAssign(whContainerAssign);
        // export.setUsableness(true);
        // return export;
        // }
        if (null == ruleAffer.getRuleId()) {
            // 如果规则ID为空 需要判断所有可用规则是否有符合
            // 查询所有的入库分拣规则
            List<WhInBoundRuleCommand> iList = whInBoundRuleDao.findInboundRuleByOuId(ruleAffer.getOuid());
            for (WhInBoundRuleCommand i : iList) {
                // 验证原始库存记录是否可用使用该规则
                Long inbound = whInBoundRuleDao.executeRuleSql(i.getRuleSql(), ruleAffer.getOuid(), ruleAffer.getInvId());
                if (null != inbound) {
                    // 可拆跳出循环返回
                    export.setUsableness(true);
                    export.setWhInBoundRuleCommand(i);
                    break;
                }
            }
        } else {
            WhInBoundRuleCommand whInBoundRuleCommand = new WhInBoundRuleCommand();
            // 如果规则ID不为空 判断此规则是否可用
            WhInBoundRule inBoundRule = whInBoundRuleDao.findByIdExt(ruleAffer.getRuleId(), ruleAffer.getOuid());
            if (null != inBoundRule) {
                // 判断该规则状态是否可用
                if (inBoundRule.getLifecycle().equals(WhInBoundRule.LIFECYCLE_NORMAL)) {
                    // 验证原始库存记录是否可使用该规则
                    Long inbound = whInBoundRuleDao.executeRuleSql(inBoundRule.getRuleSql(), ruleAffer.getOuid(), ruleAffer.getInvId());
                    if (null != inbound) {
                        BeanUtils.copyProperties(inBoundRule, whInBoundRuleCommand);
                        // 可拆跳出循环返回
                        export.setUsableness(true);
                        export.setWhInBoundRuleCommand(whInBoundRuleCommand);
                    }
                }
            }
        }
        return export;
    }

    /**
     * 分配规则
     * 
     * @param ruleAffer odoLineId 出库单明细ID
     * @return allocateRuleOdoLineId 出库单明细对应分配规则ID
     */
    private RuleExportCommand allocateRule(RuleAfferCommand ruleAffer) {
        RuleExportCommand export = new RuleExportCommand();
        return export;
    }

    /***
     * 补货规则匹配商品
     *
     * @author mingwei.xie
     * @param ruleAffer
     * @return
     */
    private RuleExportCommand exportReplenishmentRuleForSku(RuleAfferCommand ruleAffer) {
        if (null == ruleAffer.getReplenishmentRuleSkuIdList() || ruleAffer.getReplenishmentRuleSkuIdList().isEmpty()
                || (null == ruleAffer.getReplenishmentRuleOrderReplenish() && null == ruleAffer.getReplenishmentRuleRealTimeReplenish() && null == ruleAffer.getReplenishmentRuleWaveReplenish())) {
            log.error("ruleExport exportReplenishmentRuleForSku error, param skuId or replenishment type is null, ruleAffer is:[{}]", ruleAffer);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        // 商品ID
        List<Long> skuIdList = ruleAffer.getReplenishmentRuleSkuIdList();
        // 组织ID
        Long ouId = ruleAffer.getOuid();
        // 查询所有可用的补货规则,按照优先级排序,
        List<ReplenishmentRuleCommand> ruleCommandList =
                replenishmentRuleDao.findRuleByReplenishTypeOuIdOrderByPriorityAsc(ruleAffer.getReplenishmentRuleOrderReplenish(), ruleAffer.getReplenishmentRuleRealTimeReplenish(), ruleAffer.getReplenishmentRuleWaveReplenish(), ruleAffer.getOuid());
        // 存放商品匹配了哪些规则
        Map<Long, List<ReplenishmentRuleCommand>> skuReplenishmentRuleMap = new HashMap<>();
        if (null != ruleCommandList && !ruleCommandList.isEmpty()) {
            for (ReplenishmentRuleCommand ruleCommand : ruleCommandList) {
                // 转换成逗号分隔的字符串
                String skuIdListStr = StringUtil.listToStringWithoutBrackets(skuIdList, ',');
                // 匹配规则的商品
                List<Long> matchSkuIdList = replenishmentRuleDao.executeSkuRuleSql(ruleCommand.getSkuRuleSql().replace(Constants.REOLENISHMENT_RULE_SKUID_LIST_PLACEHOLDER, skuIdListStr), ouId);
                if (null != matchSkuIdList && !matchSkuIdList.isEmpty()) {
                    for (Long skuId : matchSkuIdList) {
                        List<ReplenishmentRuleCommand> skuMatchRuleList = skuReplenishmentRuleMap.get(skuId);
                        if (null == skuMatchRuleList) {
                            skuMatchRuleList = new ArrayList<>();
                        }
                        skuMatchRuleList.add(ruleCommand);
                        // 记录每个商品匹配了哪些规则，且规则按照优先级排序
                        skuReplenishmentRuleMap.put(skuId, skuMatchRuleList);
                    }
                }
            }
        }

        RuleExportCommand ruleExportCommand = new RuleExportCommand();
        ruleExportCommand.setReplenishmentRuleSkuMatchListMap(skuReplenishmentRuleMap);
        return ruleExportCommand;
    }

    /**
     * 补货规则匹配库位
     *
     * @author mingwei.xie
     * @param ruleAffer
     * @return
     */
    private RuleExportCommand exportReplenishmentRuleForLocation(RuleAfferCommand ruleAffer) {
        if (null == ruleAffer.getReplenishmentRuleLocationIdList() || ruleAffer.getReplenishmentRuleLocationIdList().isEmpty() || null == ruleAffer.getReplenishmentRuleCommand()) {
            log.error("ruleExport exportReplenishmentRule error, param skuId or replenishmentRule is null, ruleAffer is:[{}]", ruleAffer);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }

        List<Long> locationIdList = ruleAffer.getReplenishmentRuleLocationIdList();
        // 组织ID
        Long ouId = ruleAffer.getOuid();
        ReplenishmentRuleCommand replenishmentRuleCommand = ruleAffer.getReplenishmentRuleCommand();
        // 转换成逗号分隔的字符串
        String locationIdListStr = StringUtil.listToStringWithoutBrackets(locationIdList, ',');
        // 匹配库位的规则
        List<Long> matchLocationIdList = replenishmentRuleDao.executeLocationRuleSql(replenishmentRuleCommand.getLocationRuleSql().replace(Constants.REOLENISHMENT_RULE_LOCATIONID_LIST_PLACEHOLDER, locationIdListStr), ouId);

        RuleExportCommand ruleExportCommand = new RuleExportCommand();
        ruleExportCommand.setReplenishmentRuleLocationMatchList(matchLocationIdList);
        return ruleExportCommand;
    }

    private RuleExportCommand exportOutboundBoxRule(RuleAfferCommand ruleAffer) {
        if (null == ruleAffer.getOutboundBoxRuleOdoIdList() || ruleAffer.getOutboundBoxRuleOdoIdList().isEmpty()) {
            log.error("ruleExport exportOutboundBoxRule error, param odoIdList is null, outboundBoxRuleOdoIdList is:[{}]", ruleAffer.getOutboundBoxRuleOdoIdList());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        // 出库单ID列表
        List<Long> odoIdList = ruleAffer.getOutboundBoxRuleOdoIdList();
        // 组织ID
        Long ouId = ruleAffer.getOuid();
        // 查询所有可用的出库箱装箱规则,按照优先级排序
        List<OutboundBoxRuleCommand> ruleCommandList = outboundBoxRuleDao.findRuleByOuIdOrderByPriorityAsc(ouId);
        // 存储哪些出库单对应哪个规则
        Map<List<Long>, OutboundBoxRuleCommand> odoListoutboundRuleListMap = new HashMap<>();
        for (OutboundBoxRuleCommand ruleCommand : ruleCommandList) {
            String odoIdListStr = StringUtil.listToStringWithoutBrackets(odoIdList, ',');
            List<Long> matchOdoIdList = outboundBoxRuleDao.executeRuleSql(ruleCommand.getOutboundboxRuleSql().replace(Constants.OUTBOUNDBOX_RULE_PLACEHOLDER, odoIdListStr), ouId);
            if (null != matchOdoIdList && !matchOdoIdList.isEmpty()) {
                odoListoutboundRuleListMap.put(matchOdoIdList, ruleCommand);
                odoIdList.removeAll(matchOdoIdList);
            }
            if (odoIdList.isEmpty()) {
                // 所有出库单都已匹配了规则
                break;
            }
        }
        if (!odoIdList.isEmpty()) {
            // 未匹配上规则的出库单
            odoListoutboundRuleListMap.put(odoIdList, null);
        }

        // 出库单分别对应的规则
        Map<Long, OutboundBoxRuleCommand> odoOutboundBoxRuleMap = new HashMap<>();
        for (List<Long> odoIdListTemp : odoListoutboundRuleListMap.keySet()) {
            OutboundBoxRuleCommand ruleCommand = odoListoutboundRuleListMap.get(odoIdListTemp);
            for (Long odoId : odoIdListTemp) {
                odoOutboundBoxRuleMap.put(odoId, ruleCommand);
            }
        }

        RuleExportCommand ruleExportCommand = new RuleExportCommand();
        ruleExportCommand.setOdoOutboundBoxRuleMap(odoOutboundBoxRuleMap);
        return ruleExportCommand;
    }

    private RuleExportCommand exportCheckOperationsAreaRule(RuleAfferCommand ruleAffer){
        if (null == ruleAffer.getCheckOperationsAreaOdoIdList() || ruleAffer.getCheckOperationsAreaOdoIdList().isEmpty()) {
            log.error("ruleExport exportCheckOperationsAreaRule error, param odoIdList is null, checkOperationsAreaOdoIdList is:[{}]", ruleAffer.getCheckOperationsAreaOdoIdList());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        // 出库单ID列表
        List<Long> odoIdList = ruleAffer.getCheckOperationsAreaOdoIdList();
        // 组织ID
        Long ouId = ruleAffer.getOuid();

        CheckOperationsAreaRuleCommand availableRule = null;
        // 查询所有可用的出库箱装箱规则,按照优先级排序
        List<CheckOperationsAreaRuleCommand> ruleCommandList = checkOperationsAreaRuleDao.findRuleByOuIdOrderByPriorityAsc(ouId);
        for (CheckOperationsAreaRuleCommand ruleCommand : ruleCommandList) {
            String odoIdListStr = StringUtil.listToStringWithoutBrackets(odoIdList, ',');
            List<Long> matchOdoIdList = checkOperationsAreaRuleDao.executeRuleSql(ruleCommand.getRuleSql().replace(Constants.CHECK_OPERATIONS_AREA_ODOID_LIST_PLACEHOLDER, odoIdListStr), ouId);
            if (null != matchOdoIdList && !matchOdoIdList.isEmpty()) {
                //找到一个可用的即可
                availableRule = ruleCommand;
                break;
            }
        }

        RuleExportCommand ruleExportCommand = new RuleExportCommand();
        ruleExportCommand.setCheckOperationsAreaRuleCommand(availableRule);
        return ruleExportCommand;
    }

    /**
     * 配货模式规则
     * 
     * @param ruleAffer odoLineId 出库单明细ID
     * @return allocateRuleOdoLineId 出库单明细对应分配规则ID
     */
    private RuleExportCommand distributionPattern(RuleAfferCommand ruleAffer) {
        // 波次ID
        Long waveId = ruleAffer.getWaveId();
        // 组织ID
        Long ouId = ruleAffer.getOuid();

        // 查询所有可用的配货模式规则,按照优先级排序
        List<WhDistributionPatternRuleCommand> ruleCommandList = whDistributionPatternRuleDao.findRuleByOuIdOrderByPriorityAsc(ruleAffer.getOuid());

        if (null != ruleCommandList && !ruleCommandList.isEmpty()) {
            for (WhDistributionPatternRuleCommand ruleCommand : ruleCommandList) {
                // 匹配配货模式规则的出库单
                if (!StringUtil.isEmpty(ruleCommand.getRuleSql())) {
                    List<Long> odoIdList = whDistributionPatternRuleDao.testRuleSql(ruleCommand.getRuleSql(), ouId, waveId);
                    if (null != odoIdList && !odoIdList.isEmpty()) {
                        ruleCommand.setOdoIdList(odoIdList);
                    }
                }
            }
        }
        RuleExportCommand ruleExportCommand = new RuleExportCommand();
        ruleExportCommand.setWhDistributionPatternRuleCommandList(ruleCommandList);
        return ruleExportCommand;
    }

    /**
     * 通过入库规则ResultConditionIds获取对应字段映射
     *
     * @param ids
     * @return
     */
    protected String selectColumnsByConditionIds(List<Long> ids) {
        List<RecommendRuleConditionCommand> ruleConditionCommandList = recommendRuleConditionDao.getSelectColumnsByConditionIds(ids);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ruleConditionCommandList.size(); i++) {
            stringBuilder.append(ruleConditionCommandList.get(i).getTableCode()).append(".").append(ruleConditionCommandList.get(i).getColumnCode());
            if (i != ruleConditionCommandList.size() - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    /***
     * 封装容器号
     * 
     * @param afferInsideContainerIdList
     * @return
     */
    private String forMatAfferInsideContainerIdList(List<Long> afferInsideContainerIdList) {
        StringBuilder stringBuilder = new StringBuilder();
        // 封装容器号
        for (int i = 0; i < afferInsideContainerIdList.size(); i++) {
            Long insideContainerId = afferInsideContainerIdList.get(i);
            stringBuilder.append(insideContainerId);
            if (i != afferInsideContainerIdList.size() - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}
