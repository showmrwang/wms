package com.baozun.scm.primservice.whoperation.manager.collect;

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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.command.collect.WhOdoArchivLineIndexCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.archiv.OdoArchivDao;
import com.baozun.scm.primservice.whoperation.dao.collect.WhOdoArchivIndexDao;
import com.baozun.scm.primservice.whoperation.dao.collect.WhOdoArchivLineIndexDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.InventoryStatusDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivIndex;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivLineIndex;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;
import com.baozun.scm.primservice.whoperation.util.HashUtil;
import com.baozun.scm.primservice.whoperation.util.StringUtil;
import com.baozun.utilities.DateUtil;

@Service("whOdoArchivIndexManager")
@Transactional
public class WhOdoArchivIndexManagerImpl extends BaseManagerImpl implements WhOdoArchivIndexManager {

    protected static final Logger log = LoggerFactory.getLogger(WhOdoArchivIndexManager.class);

    @Autowired
    private WhOdoArchivIndexDao whOdoArchivIndexDao;
    @Autowired
    private WhOdoArchivLineIndexDao whOdoArchivLineIndexDao;
    @Autowired
    private OdoArchivDao odoArchivDao;
    @Autowired
    private InventoryStatusDao inventoryStatusDao;

    /**
     * 保存仓库出库单归档索引数据
     * 
     * @param whOdoArchivIndex
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public int saveWhOdoArchivIndex(WhOdoArchivIndex whOdoArchivIndex) {
        log.info("WhOdoArchivIndexManagerImpl.saveWhOdoArchivIndex begin!");
        // 通过出库单电商平台订单号
        Long count = 0L;
        // 通过hash算法计算表序列号
        String serialNumber = HashUtil.serialNumberByHashCode(whOdoArchivIndex.getEcOrderCode());
        if (StringUtil.isEmpty(serialNumber)) {
            log.warn("WhOdoArchivIndexManagerImpl.saveWhOdoArchivIndex serialNumber is null EcOrderCode: " + whOdoArchivIndex.getEcOrderCode());
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        whOdoArchivIndex.setNum(serialNumber);
        whOdoArchivIndex.setCreateTime(new Date());
        count = whOdoArchivIndexDao.insert(whOdoArchivIndex);
        log.info("WhOdoArchivIndexManagerImpl.saveWhOdoArchivIndex end!");
        return count.intValue();
    }

    /**
     * 通过电商平台订单号(NOT NULL) or 数据来源(DEFAULT NULL) or 仓库组织ID(DEFAULT NULL) or wms出库单号(DEFAULT NULL)
     * 查询仓库出库单归档索引数据
     * 
     * @param ecOrderCode
     * @param dataSource
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public List<WhOdoArchivIndex> findWhOdoArchivIndexByEcOrderCode(String ecOrderCode, String dataSource, String wmsOdoCode, Long ouid) {
        log.info("WhOdoArchivIndexManagerImpl.findWhOdoArchivIndexByEcOrderCode begin!");
        // 验证电商平台订单号是否为空
        if (StringUtil.isEmpty(ecOrderCode)) {
            log.warn("WhOdoArchivIndexManagerImpl.findWhOdoArchivIndexByEcOrderCode ecOrderCode is null");
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        // 通过hash算法计算表序列号
        String serialNumber = HashUtil.serialNumberByHashCode(ecOrderCode);
        if (StringUtil.isEmpty(serialNumber)) {
            log.warn("WhOdoArchivIndexManagerImpl.findWhOdoArchivIndexByEcOrderCode serialNumber is null EcOrderCode: " + ecOrderCode);
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        List<WhOdoArchivIndex> whOdoArchivIndexList = whOdoArchivIndexDao.findWhOdoArchivIndexByEcOrderCode(ecOrderCode, dataSource, wmsOdoCode, serialNumber, ouid);
        log.info("WhOdoArchivIndexManagerImpl.findWhOdoArchivIndexByEcOrderCode end!");
        return whOdoArchivIndexList;
    }

    /**
     * 保存仓库出库单归档索引数据
     * 
     * @param whOdoArchivIndex
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public void saveWhOdoArchivIndexExt(WhOdoArchivIndex index) {
        // 先判断是否已在表中存在
        List<WhOdoArchivIndex> odoArchivIndexList = this.findWhOdoArchivIndexByEcOrderCode(index.getEcOrderCode(), index.getDataSource(), index.getWmsOdoCode(), index.getOuId());
        // 不存在则保存
        if (null == odoArchivIndexList || odoArchivIndexList.isEmpty()) {
            int insertCount = this.saveWhOdoArchivIndex(index);
            if (insertCount != 1) {
                throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<WhOdoArchivIndex> findWhOdoArchivIndexData(Long ouId) {
        return whOdoArchivIndexDao.findWhOdoArchivIndexData(ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteWhOdoArchivIndex(WhOdoArchivIndex index) {
        int updateCount = whOdoArchivIndexDao.deleteWhOdoArchivIndexById(index.getId(), index.getOuId());
        if (updateCount != 1) {
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public List<WhOdoArchivLineIndex> saveWhOdoLineArchivListIntoCollect(WhOdoArchivIndex odoArchivIndex, List<WhOdoArchivLineIndex> whOdoArchivLineIndexList) {
        if (null == whOdoArchivLineIndexList || whOdoArchivLineIndexList.isEmpty()) {
            return null;
        }
        // 插入t_wh_odo_archiv_line_index_${num}中
        for (WhOdoArchivLineIndex index : whOdoArchivLineIndexList) {
            int count = odoArchivDao.saveOdoArchivLineIndex(index);
            if (count != 1) {
                throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
            }
            index.setCollectOdoArchivLineId(index.getId());
            index.setCollectTableName("t_wh_odo_archiv_line_index_" + index.getNum());
        }
        // 更新t_wh_odo_archiv_index_${num}的isReturnedPurchase为1
        String ecOrderCode = odoArchivIndex.getEcOrderCode();
        String num = HashUtil.serialNumberByHashCode(ecOrderCode);
        int updateCount = whOdoArchivIndexDao.updateReturnedPurchase(odoArchivIndex.getId(), odoArchivIndex.getOuId(), num);
        if (updateCount != 1) {
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
        return whOdoArchivLineIndexList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public boolean checkWhOdoArchivLineIndexExsits(String ecOrderCode, String dataSource, Long ouId) {
        String num = HashUtil.serialNumberByHashCode(ecOrderCode);
        int count = whOdoArchivIndexDao.checkWhOdoArchivLineIndexExsits(ecOrderCode, dataSource, num);
        if (count > 0) {
            return false;
        }
        return true;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoArchivLineIndex> findWhOdoArchivLineIndexListByAsnId(Long asnId, Long ouId) {
        WhOdoArchivLineIndex search = new WhOdoArchivLineIndex();
        search.setAsnId(asnId);
        search.setOuId(ouId);
        return this.whOdoArchivLineIndexDao.findListByParam(search);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoArchivLineIndexCommand> findWhOdoArchivLineIndexCommandListByAsnId(Long asnId, Long ouId) {
        List<WhOdoArchivLineIndex> lineList = this.findWhOdoArchivLineIndexListByAsnId(asnId, ouId);
        if (lineList == null || lineList.size() == 0) {
            return null;
        }
        // 库存状态
        InventoryStatus status = new InventoryStatus();
        // status.setLifecycle(1);
        List<InventoryStatus> invStatusList = this.inventoryStatusDao.findListByParam(status);
        Map<Long, String> invStatusMap = new HashMap<Long, String>();
        for (InventoryStatus s : invStatusList) {
            invStatusMap.put(s.getId(), s.getName());
        }
        Set<String> dic2 = new HashSet<String>();// 库存类型
        Set<String> dic3 = new HashSet<String>();// 库存属性1
        Set<String> dic4 = new HashSet<String>();// 库存属性2
        Set<String> dic5 = new HashSet<String>();// 库存属性3
        Set<String> dic6 = new HashSet<String>();// 库存属性4
        Set<String> dic7 = new HashSet<String>();// 库存属性5

        for (WhOdoArchivLineIndex line : lineList) {
            if (StringUtils.hasText(line.getInvType())) {
                dic2.add(line.getInvType());
            }
            if (StringUtils.hasText(line.getInvAttr1())) {
                dic3.add(line.getInvAttr1());
            }
            if (StringUtils.hasText(line.getInvAttr2())) {
                dic4.add(line.getInvAttr2());
            }
            if (StringUtils.hasText(line.getInvAttr3())) {
                dic5.add(line.getInvAttr3());
            }
            if (StringUtils.hasText(line.getInvAttr4())) {
                dic6.add(line.getInvAttr4());
            }
            if (StringUtils.hasText(line.getInvAttr5())) {
                dic7.add(line.getInvAttr5());
            }
        }
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map.put(Constants.INVENTORY_TYPE, new ArrayList<String>(dic2));
        map.put(Constants.INVENTORY_ATTR_1, new ArrayList<String>(dic3));
        map.put(Constants.INVENTORY_ATTR_2, new ArrayList<String>(dic4));
        map.put(Constants.INVENTORY_ATTR_3, new ArrayList<String>(dic5));
        map.put(Constants.INVENTORY_ATTR_4, new ArrayList<String>(dic6));
        map.put(Constants.INVENTORY_ATTR_5, new ArrayList<String>(dic7));
        Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);

        List<WhOdoArchivLineIndexCommand> commandLineList = new ArrayList<WhOdoArchivLineIndexCommand>();
        for (WhOdoArchivLineIndex line : lineList) {
            WhOdoArchivLineIndexCommand lineCommand = new WhOdoArchivLineIndexCommand();
            BeanUtils.copyProperties(line, lineCommand);

            if (null != line.getInvStatus()) {
                lineCommand.setInvName(invStatusMap.get(line.getInvStatus()));
            }

            if (StringUtils.hasText(line.getInvType())) {
                SysDictionary sys = dicMap.get(Constants.INVENTORY_TYPE + "_" + line.getInvType());
                lineCommand.setInvTypeLabel(sys == null ? line.getInvType() : sys.getDicLabel());
            } else {
                lineCommand.setInvTypeLabel("");
            }
            if (StringUtils.hasText(line.getInvAttr1())) {
                SysDictionary sys = dicMap.get(Constants.INVENTORY_ATTR_1 + "_" + line.getInvAttr1());
                lineCommand.setInv1Str(sys == null ? line.getInvAttr1() : sys.getDicLabel());
            } else {
                lineCommand.setInv1Str("");
            }
            if (StringUtils.hasText(line.getInvAttr2())) {
                SysDictionary sys = dicMap.get(Constants.INVENTORY_ATTR_2 + "_" + line.getInvAttr2());
                lineCommand.setInv2Str(sys == null ? line.getInvAttr2() : sys.getDicLabel());
            } else {
                lineCommand.setInv2Str("");
            }
            if (StringUtils.hasText(line.getInvAttr3())) {
                SysDictionary sys = dicMap.get(Constants.INVENTORY_ATTR_3 + "_" + line.getInvAttr3());
                lineCommand.setInv3Str(sys == null ? line.getInvAttr3() : sys.getDicLabel());
            } else {
                lineCommand.setInv3Str("");
            }
            if (StringUtils.hasText(line.getInvAttr4())) {
                SysDictionary sys = dicMap.get(Constants.INVENTORY_ATTR_4 + "_" + line.getInvAttr4());
                lineCommand.setInv4Str(sys == null ? line.getInvAttr4() : sys.getDicLabel());
            } else {
                lineCommand.setInv4Str("");
            }
            if (StringUtils.hasText(line.getInvAttr5())) {
                SysDictionary sys = dicMap.get(Constants.INVENTORY_ATTR_5 + "_" + line.getInvAttr5());
                lineCommand.setInv5Str(sys == null ? line.getInvAttr5() : sys.getDicLabel());
            } else {
                lineCommand.setInv5Str("");
            }
            if (line.getMfgDate() != null) {
                lineCommand.setMfgDateStr(DateUtil.format(line.getMfgDate(), Constants.DATE_PATTERN_YMD));
            } else {
                lineCommand.setMfgDateStr("");
            }
            if (line.getExpDate() != null) {
                lineCommand.setExpDateStr(DateUtil.format(line.getExpDate(), Constants.DATE_PATTERN_YMD));
            } else {
                lineCommand.setExpDateStr("");
            }
            commandLineList.add(lineCommand);

        }
        return commandLineList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public List<WhOdoArchivLineIndex> findWhOdoArchivIndexLineByWms3(String ecOrderCode, WhOdoArchivIndex odoArchivIndex, String wms3, Long ouId) {
        String num = HashUtil.serialNumberByHashCode(ecOrderCode);
        List<WhOdoArchivLineIndex> indexList = whOdoArchivLineIndexDao.findWhOdoArchivLineIndexByEcOrderCodeAndSource(ecOrderCode, wms3, num);
        for (WhOdoArchivLineIndex line : indexList) {
            line.setCollectOdoArchivLineId(odoArchivIndex.getId());
            line.setCollectTableName("t_wh_odo_archiv_line_index_" + num);
            line.setOuId(ouId);
        }
        return indexList;
    }
}
