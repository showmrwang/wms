package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;

@Service("odoLineManager")
@Transactional
public class OdoLineManagerImpl extends BaseManagerImpl implements OdoLineManager {
    @Autowired
    private WhOdoLineDao whOdoLineDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhOdoLine findOdoLineById(Long id, Long ouId) {
        return this.whOdoLineDao.findOdoLineById(id, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<OdoLineCommand> findOdoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
        Pagination<OdoLineCommand> pages = this.whOdoLineDao.findListByQueryMapWithPageExt(page, sorts, params);
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
    public void deleteLines(List<WhOdoLine> lineList, Long ouId, Long userId, String logId) {
        // TODO Auto-generated method stub

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
}
