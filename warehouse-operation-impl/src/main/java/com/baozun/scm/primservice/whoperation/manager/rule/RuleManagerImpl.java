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
import com.baozun.scm.primservice.whoperation.command.warehouse.PlatformRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendPlatformCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.RecommendRuleConditionCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ShelveRecommendRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhInBoundRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.PlatformRecommendRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.RecommendPlatformDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.RecommendRuleConditionDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ShelveRecommendRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhInBoundRuleDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
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
        // 判断规则TYPE是否符合要求
        if (!ruleAffer.getRuleType().equals(Constants.PLATFORM_RECOMMEND_RULE) && !ruleAffer.getRuleType().equals(Constants.INBOUND_RULE) && !ruleAffer.getRuleType().equals(Constants.SHELVE_RECOMMEND_RULE)
                && !ruleAffer.getRuleType().equals(Constants.SHELVE_RECOMMEND_RULE_ALL)) {
            log.warn("ruleExport ruleAffer.getRuleType() is error ruleAffer.getRuleType() = " + ruleAffer.getRuleType() + " logid: " + ruleAffer.getLogId());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        // 月台规则
        if (ruleAffer.getRuleType().equals(Constants.PLATFORM_RECOMMEND_RULE)) {
            export = exportPlatformRecommendRule(ruleAffer);
        }
        // 入库分拣
        if (ruleAffer.getRuleType().equals(Constants.INBOUND_RULE)) {
            export = exportInboundRule(ruleAffer);
        }
        // 上架 整托盘 整箱
        if (ruleAffer.getRuleType().equals(Constants.SHELVE_RECOMMEND_RULE_ALL)) {
            export = exportShelveRuleAll(ruleAffer);
        }
        // 上架 拆箱
        if (ruleAffer.getRuleType().equals(Constants.SHELVE_RECOMMEND_RULE)) {
            export = exportShelveRule(ruleAffer);
        }
        log.info(this.getClass().getSimpleName() + ".ruleExport method end! logid: " + ruleAffer.getLogId());
        return export;
    }

    /**
     * 月台推荐规则
     * 
     * @return
     */
    private RuleExportCommand exportPlatformRecommendRule(RuleAfferCommand ruleAffer) {
        if (StringUtil.isEmpty(ruleAffer.getAfferReserveCode())) {
            // 判断预约号是否为空
            log.warn("ruleExport ruleAffer.getAfferReserveCode() is null logid: " + ruleAffer.getLogId());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        RuleExportCommand export = new RuleExportCommand();
        // 返回MAP值 key: 0:找不到规则 1:找到规则单月台不可用 2:找到对应月台
        Map<Integer, RecommendPlatformCommand> returnMap = new HashMap<Integer, RecommendPlatformCommand>();
        returnMap.put(Constants.NO_MATCHING_RULES, new RecommendPlatformCommand());
        List<PlatformRecommendRuleCommand> prList = platformRecommendRuleDao.findPlatformRecommendRuleByOuId(ruleAffer.getOuid());
        for (PlatformRecommendRuleCommand p : prList) {
            // 查询规则是否符合要求
            String prr = platformRecommendRuleDao.executeRuleSql(p.getRuleSql(), ruleAffer.getOuid(), ruleAffer.getAfferReserveCode());
            if (!StringUtil.isEmpty(prr)) {
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
        export.setExportPlatformCode(returnMap);
        return export;
    }

    /**
     * 整托盘 整箱上架规则匹配
     * 
     * @param ruleAffer
     * @return
     */
    private RuleExportCommand exportShelveRuleAll(RuleAfferCommand ruleAffer) {
        if (ruleAffer.getAfferContainerCodeList().size() == 0) {
            // 判断容器号List是否为空
            log.warn("ruleExport ruleAffer.getAfferContainerCodeList().size() == 0 logid: " + ruleAffer.getLogId());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        String containerCodeListStr = forMatAfferContainerCodeList(ruleAffer.getAfferContainerCodeList());
        RuleExportCommand export = new RuleExportCommand();
        // 查询所有可用上架规则 并且排序
        List<ShelveRecommendRuleCommand> sList = shelveRecommendRuleDao.findShelveRecommendRuleByOuid(ruleAffer.getOuid());
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = whSkuInventoryDao.findWhSkuInventoryByContainerCode(ruleAffer.getOuid(), ruleAffer.getAfferContainerCodeList());
        if (invList.size() == 0) {
            return export;
        }
        // 所有符合条件的规则LIST
        List<ShelveRecommendRuleCommand> returnList = new ArrayList<ShelveRecommendRuleCommand>();
        for (ShelveRecommendRuleCommand s : sList) {
            // 查询上架规则对应库存信息ID LIST
            List<Long> list = shelveRecommendRuleDao.executeRuleSql(s.getRuleSql().replace("containerCodeListStr", containerCodeListStr), ruleAffer.getOuid());
            // 如果库存信息数量=上架规则对应库存信息ID LIST 加入list规则对象
            if (invList.size() == list.size()) {
                // 把规则加入list
                returnList.add(s);
            }
        }
        export.setShelveRecommendRuleList(returnList);
        return export;
    }

    /**
     * 拆箱上架规则匹配
     * 
     * @param ruleAffer
     * @return
     */
    private RuleExportCommand exportShelveRule(RuleAfferCommand ruleAffer) {
        if (ruleAffer.getAfferContainerCodeList().size() == 0) {
            // 判断容器号List是否为空
            log.warn("ruleExport ruleAffer.getAfferContainerCodeList().size() == 0 logid: " + ruleAffer.getLogId());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        String containerCodeListStr = forMatAfferContainerCodeList(ruleAffer.getAfferContainerCodeList());
        RuleExportCommand export = new RuleExportCommand();
        // 查询所有可用上架规则 并且排序
        List<ShelveRecommendRuleCommand> sList = shelveRecommendRuleDao.findShelveRecommendRuleByOuid(ruleAffer.getOuid());
        // 查询所有对应容器号的库存信息
        List<WhSkuInventoryCommand> invList = whSkuInventoryDao.findWhSkuInventoryByContainerCode(ruleAffer.getOuid(), ruleAffer.getAfferContainerCodeList());
        Map<ShelveRecommendRuleCommand, List<Long>> map = new HashMap<ShelveRecommendRuleCommand, List<Long>>();
        Map<Long, List<ShelveRecommendRuleCommand>> returnMap = new HashMap<Long, List<ShelveRecommendRuleCommand>>();
        for (ShelveRecommendRuleCommand s : sList) {
            // 查询上架规则对应库存信息ID LIST
            List<Long> list = shelveRecommendRuleDao.executeRuleSql(s.getRuleSql().replace("containerCodeListStr", containerCodeListStr), ruleAffer.getOuid());
            if (list.size() > 0) {
                map.put(s, list);
            }
        }
        // 把map Map<ShelveRecommendRuleCommand, List<Long>>封装成map Map<Long,
        // List<ShelveRecommendRuleCommand>>
        for (ShelveRecommendRuleCommand m : map.keySet()) {
            List<Long> maoLong = map.get(m);// 规则对应库存记录LIST
            for (Long l : maoLong) {
                List<ShelveRecommendRuleCommand> longList = returnMap.get(l);// 查询returnMap
                if (null == longList) {
                    // 没值new新对象
                    longList = new ArrayList<ShelveRecommendRuleCommand>();
                }
                // 放入list
                longList.add(m);
                // 放入Map
                returnMap.put(l, longList);
            }
        }
        // 循环库存记录 判断库存记录是否有匹配规则
        for (WhSkuInventoryCommand inv : invList) {
            // 查询returnMap是否有对应inv.id值
            List<ShelveRecommendRuleCommand> srr = returnMap.get(inv.getId());
            if (null == srr) {
                // 没有 put新的数据value = null
                returnMap.put(inv.getId(), null);
            }
        }
        export.setShelveMap(returnMap);
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
     * 通过入库规则ResultConditionIds获取对应字段映射
     *
     * @param ids
     * @return
     */
    protected String selectColumnsByConditionIds(List<Long> ids) {
        List<RecommendRuleConditionCommand> ruleConditionCommandList = recommendRuleConditionDao.getSelectColumnsByConditionIds(ids);
        if (log.isDebugEnabled()) {
            log.debug("RecommendRuleConditionManagerImpl getSelectColumnsByConditionIds, loop ruleConditionCommandList, param [ruleConditionCommandList:{}, ids:{}]", ruleConditionCommandList, ids);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ruleConditionCommandList.size(); i++) {
            stringBuilder.append(ruleConditionCommandList.get(i).getTableCode()).append(".").append(ruleConditionCommandList.get(i).getColumnCode());
            if (i != ruleConditionCommandList.size() - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
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

    /***
     * 封装容器号
     * 
     * @param afferContainerCodeList
     * @return
     */
    private String forMatAfferContainerCodeList(List<String> afferContainerCodeList) {
        StringBuilder stringBuilder = new StringBuilder();
        // 封装容器号
        for (int i = 0; i < afferContainerCodeList.size(); i++) {
            String containerCode = afferContainerCodeList.get(i);
            stringBuilder.append("'");
            stringBuilder.append(containerCode);
            stringBuilder.append("'");
            if (i != afferContainerCodeList.size() - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}
