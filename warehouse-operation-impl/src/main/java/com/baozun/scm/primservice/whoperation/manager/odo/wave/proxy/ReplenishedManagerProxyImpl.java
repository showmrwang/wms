package com.baozun.scm.primservice.whoperation.manager.odo.wave.proxy;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.rule.RuleAfferCommand;
import com.baozun.scm.primservice.whoperation.command.rule.RuleExportCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentRuleCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.ReplenishmentStrategyCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveDao;
import com.baozun.scm.primservice.whoperation.dao.odo.wave.WhWaveLineDao;
import com.baozun.scm.primservice.whoperation.dao.system.SysDictionaryDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.rule.RuleManager;
import com.baozun.scm.primservice.whoperation.manager.warehouse.ReplenishmentRuleManager;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;

@Service("replenishedManagerProxy")
public class ReplenishedManagerProxyImpl extends BaseManagerImpl implements ReplenishedManagerProxy {
	
	@Autowired
	private WhWaveDao whWaveDao;
	@Autowired
	private WhWaveLineDao whWaveLineDao;
	@Autowired
	private SysDictionaryDao sysDictionaryDao;
	@Autowired
	private RuleManager ruleManager;
	@Autowired
	private CodeManager codeManager;
    @Autowired
    private ReplenishmentRuleManager replenishmentRuleManager;
	
	@Override
	public List<Long> findWaveIdsByWavePhaseCode(String phaseCode, Long ouId) {
		WhWave wave = new WhWave();
		wave.setPhaseCode(phaseCode);
		wave.setOuId(ouId);
		wave.setAllocatePhase(null);
		wave.setIsRunWave(true);
		return whWaveDao.findWaveIdsByParam(wave);
	}

	@Override
	public List<WhWaveLine> findWaveLineByNotEnoughAllocationQty(Long waveId, Long ouId) {
		return whWaveLineDao.findNotEnoughAllocationQty(waveId, ouId);
	}

	@Override
	public List<Long> findOdoContainsSkuId(Long waveId, List<Long> skuIds, Long ouId) {
		return whWaveDao.findOdoContainsSkuId(waveId, skuIds, ouId);
	}

	@Override
	public SysDictionary getGroupbyGroupValueAndDicValue(String groupValue, String dicValue) {
		return sysDictionaryDao.getGroupbyGroupValueAndDicValue(groupValue, dicValue);
	}

	@Override
	public Map<Long, List<ReplenishmentRuleCommand>> getSkuReplenishmentRule(List<Long> skuIds, Long ouId) {
		RuleAfferCommand ruleAffer = new RuleAfferCommand();
		ruleAffer.setRuleType(Constants.RULE_TYPE_REPLENISHMENT_SKU);
		ruleAffer.setOuid(ouId);
		ruleAffer.setReplenishmentRuleSkuIdList(skuIds);
		ruleAffer.setReplenishmentRuleWaveReplenish(Boolean.TRUE);
		RuleExportCommand ruleExport = ruleManager.ruleExport(ruleAffer);
		Map<Long, List<ReplenishmentRuleCommand>> skuMap = ruleExport.getReplenishmentRuleSkuMatchListMap();
		return skuMap;
	}

	@Override
	public String generateBhCode() {
		return codeManager.generateCode(Constants.WMS, Constants.BH_MODEL_URL, null, null, null);
	}

    @Override
    public List<ReplenishmentStrategyCommand> findReplenishmentStrategyListByRuleId(Long id, Long ouId) {
        return this.replenishmentRuleManager.getReplenishmentStrategyCommandByRuleId(id, ouId);
    }

}
