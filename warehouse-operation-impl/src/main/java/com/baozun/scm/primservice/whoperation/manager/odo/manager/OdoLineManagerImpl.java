package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoVasCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoVasDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.InventoryStatusDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.system.SysDictionary;
import com.baozun.scm.primservice.whoperation.model.warehouse.InventoryStatus;

@Service("odoLineManager")
@Transactional
public class OdoLineManagerImpl extends BaseManagerImpl implements OdoLineManager {
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private WhOdoVasDao whOdoVasDao;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private InventoryStatusDao inventoryStatusDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdoLine findOdoLineById(Long id, Long ouId) {
        return this.whOdoLineDao.findOdoLineById(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OdoLineCommand> findOdoLineById(List<Long> idList, Long ouId) {
        return this.whOdoLineDao.findOdoLineByIdList(idList, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OdoLineCommand> findOdoLineByIdStr(List<String> idStrList, Long ouId) {
        return this.whOdoLineDao.findOdoLineByIdStrList(idStrList, ouId);
    }

    /**
     * 根据ODOLINEID和OUID查找ODOLINE
     *
     * @author mingwei.xie
     * @param idList
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OdoLineCommand> findOdoLineByOdoId(List<Long> idList, Long ouId){
        return whOdoLineDao.findOdoLineByOdoId(idList, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OdoLineCommand> findOdoLineByOdoIdOrderByPickingSort(List<Long> idList, Long ouId) {
        return this.whOdoLineDao.findOdoLineByOdoIdOrderByPickingSort(idList, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<OdoLineCommand> findOdoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<OdoLineCommand> pages = this.whOdoLineDao.findListByQueryMapWithPageExt(page, sorts, params);
        List<OdoLineCommand> odoLineList = pages.getItems();
        if (odoLineList != null && odoLineList.size() > 0) {
            // 库存状态
            InventoryStatus status = new InventoryStatus();
            status.setLifecycle(1);
            List<InventoryStatus> invStatusList = this.inventoryStatusDao.findListByParam(status);
            Map<Long, String> invStatusMap = new HashMap<Long, String>();
            // 出库单明细状态
            Set<String> dic1 = new HashSet<String>();
            for (InventoryStatus s : invStatusList) {
                invStatusMap.put(s.getId(), s.getName());
            }
            for (OdoLineCommand odo : odoLineList) {
                odo.setInvStatusName(invStatusMap.get(odo.getInvStatus()));
                dic1.add(odo.getOdoLineStatus());
            }
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            map.put(Constants.ODO_LINE_STATUS, new ArrayList<String>(dic1));
            Map<String, SysDictionary> dicMap = this.findSysDictionaryByRedis(map);
            for (OdoLineCommand odoline : odoLineList) {
                SysDictionary sys = dicMap.get(Constants.ODO_LINE_STATUS + "_" + odoline.getOdoLineStatus());
                odoline.setOdoLineStatusName(sys == null ? odoline.getOdoLineStatus() : sys.getDicLabel());
            }
        }
        return pages;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public long findOdoLineListCountByOdoId(Long odoId, Long ouId) {
        return this.whOdoLineDao.findOdoLineListCountByOdoIdOuId(odoId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoLine> findOdoLineListByOdoId(Long odoId, Long ouId) {
        return this.whOdoLineDao.findOdoLineListByOdoIdOuId(odoId, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OdoLineCommand> findOdoLineCommandListByOdoId(Long odoId, Long ouId) {
        return this.whOdoLineDao.findOdoLineCommandListByOdoIdOuId(odoId, ouId);
    }

    @Override
    public void cancelLines(WhOdo odo, List<WhOdoLine> lineList, Long ouId, Long userId, String logId) {
        try {
            if (lineList != null && lineList.size() > 0) {
                for (WhOdoLine line : lineList) {
                    line.setOdoLineStatus(OdoStatus.CANCEL);
                    int updateLineCount = this.whOdoLineDao.saveOrUpdateByVersion(line);
                    if (updateLineCount <= 0) {
                        throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                    }
                }
            }
            odo = this.getSummaryByOdolineList(odo);
            if (OdoStatus.CREATING.equals(odo.getOdoStatus())) {
                odo.setOdoStatus(OdoStatus.NEW);
            }
            int updateOdoCount = this.whOdoDao.saveOrUpdateByVersion(odo);
            if (updateOdoCount <= 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodes.DAO_EXCEPTION);
        }

    }

    private WhOdo getSummaryByOdolineList(WhOdo odo) {
        List<WhOdoLine> lineList = this.whOdoLineDao.findOdoLineListByOdoIdOuId(odo.getId(), odo.getOuId());
        // 出库单统计数目
        double qty = Constants.DEFAULT_DOUBLE;
        int skuNumberOfPackages = Constants.DEFAULT_INTEGER;
        double amt = Constants.DEFAULT_DOUBLE;
        boolean isHazardous = odo.getIncludeHazardousCargo();
        boolean isFragile = odo.getIncludeFragileCargo();
        Set<Long> skuIdSet = new HashSet<Long>();
        boolean isAllowMerge = false;
        if (odo.getIsLocked() == null || odo.getIsLocked() == false) {
            isAllowMerge = true;
        } else {
            List<WhOdoVasCommand> ouVasList = this.whOdoVasDao.findOdoOuVasCommandByOdoIdOdoLineIdType(odo.getId(), null, odo.getOuId());
            if (ouVasList != null && ouVasList.size() > 0) {
                isAllowMerge = false;
            }
        }
        if (lineList != null && lineList.size() > 0) {
            for (WhOdoLine line : lineList) {
                if (OdoStatus.ODOLINE_CANCEL.equals(line.getOdoLineStatus())) {
                    continue;
                }
                skuIdSet.add(line.getSkuId());
                amt += line.getLineAmt();
                qty += line.getQty();

                if (isAllowMerge) {
                    List<WhOdoVasCommand> ouVasList = this.whOdoVasDao.findOdoOuVasCommandByOdoIdOdoLineIdType(odo.getId(), line.getId(), odo.getOuId());
                    if (ouVasList != null && ouVasList.size() > 0) {
                        isAllowMerge = false;
                    }
                }
            }

        }
        odo.setQty(qty);
        odo.setAmt(amt);
        skuNumberOfPackages = skuIdSet.size();
        odo.setSkuNumberOfPackages(skuNumberOfPackages);
        odo.setIncludeFragileCargo(isFragile);
        odo.setIncludeHazardousCargo(isHazardous);

        // 设置允许合并与否
        odo.setIsAllowMerge(false);
        return odo;
    }

    @Override
    @Deprecated
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean updateOdoLineStatus(Long odoLineId, Long ouId, String status) {
        if (null == odoLineId || null == ouId || null == status) {
            throw new BusinessException("没有数据");
        }
        WhOdoLine odoLine = this.whOdoLineDao.findOdoLineById(odoLineId, ouId);
        WhOdoLine whOdoLine = new WhOdoLine();
        BeanUtils.copyProperties(odoLine, whOdoLine);
        whOdoLine.setOdoLineStatus(status);
        int cnt = whOdoLineDao.saveOrUpdateByVersion(whOdoLine);
        if (cnt <= 0) {
            throw new BusinessException("更新出库单明细状态失败");
        }
        return true;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoLine> findOdoLineListByWaveCode(String code, Long ouId) {
        WhOdoLine line = new WhOdoLine();
        line.setWaveCode(code);
        line.setOuId(ouId);
        return this.whOdoLineDao.findListByParamExt(line);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoLine> findOdoLineListByOdoIdStatus(Long odoId, Long ouId, String[] statusList) {
        
        return this.whOdoLineDao.findOdoLineListByOdoIdStatus(odoId, ouId, statusList);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoLine> findOdoLineListByOdoIdAndLinenumList(Long odoId, Long ouId, List<Integer> lineSeq) {
        if (lineSeq == null || lineSeq.size() == 0) {
            return null;
        }
        List<WhOdoLine> lineList = this.whOdoLineDao.findOdoLineListByOdoIdAndLinenumList(odoId, ouId, lineSeq);
        if (lineList == null || lineList.size() != lineSeq.size()) {
            throw new BusinessException(ErrorCodes.PARAMS_ERROR);
        }
        return lineList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteLines(List<WhOdoLine> lineList) {
        for (WhOdoLine line : lineList) {
            this.whOdoLineDao.deleteByIdOuId(line.getId(), line.getOuId());
        }
    }
}
