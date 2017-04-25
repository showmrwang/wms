package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.RegionCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.RegionDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.BaseModel;
import com.baozun.scm.primservice.whoperation.model.warehouse.Region;


@Service("regionManager")
@Transactional
public class RegionManagerImpl extends BaseManagerImpl implements RegionManager {

    @Autowired
    private RegionDao regionDao;

    /**
     * 通过父栏目ID查找对应国家省市
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<RegionCommand> findRegionByParentId(Long id) {
        return regionDao.findRegionByParentId(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Pagination<RegionCommand> getListByParams(Page page, Sort[] sorts, Map<String, Object> params) {
        return regionDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Region findRegionById(Long id) {
        return regionDao.findById(id);
    }

    /**
     * 修改区域状态
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void updateRgType(Long userId, Long rgId, Integer lifecycle) {
        Region region = regionDao.findById(rgId);
        if (null == region) {
            throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
        }
        region.setLifecycle(lifecycle);
        region.setLastModifyTime(new Date());
        region.setOperatorId(userId);
        regionDao.update(region);
        // 插入系统日志表
        insertGlobalLog(GLOBAL_LOG_UPDATE, region, null, userId, null, null);
        // GlobalLogCommand gl = new GlobalLogCommand();
        // gl.setModifiedId(userId);
        // gl.setObjectType(region.getClass().getSimpleName());
        // gl.setModifiedValues(region);
        // gl.setType(GLOBAL_LOG_UPDATE);
        // globalLogManager.insertGlobalLog(gl);
    }

    /**
     * 批量修改区域状态
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public int updateLifeCycle(List<Long> ids, Integer lifeCycle, Long userid) {
        int result = regionDao.updateLifeCycle(ids, lifeCycle, userid);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != ids.size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {ids.size(), result});
        }
        for (Long id : ids) {
            Region r = regionDao.findById(id);
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_UPDATE, r, null, userid, null, null);
            // GlobalLogCommand gl = new GlobalLogCommand();
            // gl.setModifiedId(userid);
            // gl.setObjectType(r.getClass().getSimpleName());
            // gl.setModifiedValues(r);
            // gl.setType(GLOBAL_LOG_UPDATE);
            // globalLogManager.insertGlobalLog(gl);
        }
        return result;
    }

    /**
     * 验证区域名称/编码是否重复
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public RegionCommand checkNameOrCode(String name, String code, Long parentId) {
        return regionDao.checkNameOrCode(name, code, parentId);
    }

    /**
     * 修改/创建区域信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Region saveOrUpdateRegion(Region region, Long userid) {
        // GlobalLogCommand gl = new GlobalLogCommand();
        if (null != region.getId()) {
            // update
            Region r = regionDao.findById(region.getId());
            region.setCreateTime(r.getCreateTime());
            region.setLastModifyTime(r.getLastModifyTime());
            region.setOperatorId(userid);
            region.setSortNo(r.getSortNo());
            region.setShortName(r.getRegionName());
            int count = regionDao.saveOrUpdateByVersion(region);
            if (count == 0) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            // 插入系统日志表
            insertGlobalLog(GLOBAL_LOG_INSERT, region, null, userid, null, null);
            // gl.setModifiedId(userid);
            // gl.setObjectType(region.getClass().getSimpleName());
            // gl.setModifiedValues(region);
            // gl.setType(GLOBAL_LOG_INSERT);
            // globalLogManager.insertGlobalLog(gl);
            return region;
        } else {
            // add
            // 首先查找对应CODE是否存在删除的区域
            Region r = regionDao.findRegionByCodeLifecycle(region.getRegionCode(), BaseModel.LIFECYCLE_DELETED);
            if (null == r) {
                // 不存在创建新的区域
                int sortNo = 1;// 默认排序号为1
                // 查找该区域下最大的排序号
                Integer sortNoSql = regionDao.getSortNoByParentId(region.getParentId());
                if (null != sortNoSql) {
                    // 如果有最大排序号在上面+1
                    sortNo = sortNoSql + 1;
                }
                region.setCreateTime(new Date());
                region.setLastModifyTime(new Date());
                region.setOperatorId(userid);
                region.setSortNo(sortNo);
                region.setShortName(region.getRegionName());
                regionDao.saveOrUpdate(region);
                insertGlobalLog(GLOBAL_LOG_INSERT, region, null, userid, null, null);
                // gl.setModifiedId(userid);
                // gl.setObjectType(region.getClass().getSimpleName());
                // gl.setModifiedValues(region);
                // gl.setType(GLOBAL_LOG_INSERT);
                // globalLogManager.insertGlobalLog(gl);
                return region;
            } else {
                // 存在 直接修改对应状态和属性
                r.setLevel(region.getLevel());
                r.setOperatorId(userid);
                r.setParentId(region.getParentId());
                r.setRegionName(region.getRegionName());
                r.setShortName(region.getRegionName());
                r.setLastModifyTime(new Date());
                r.setLifecycle(region.getLifecycle());
                int count = regionDao.saveOrUpdateByVersion(r);
                if (count == 0) {
                    throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
                }
                insertGlobalLog(GLOBAL_LOG_UPDATE, r, null, userid, null, null);
                // gl.setModifiedId(userid);
                // gl.setObjectType(r.getClass().getSimpleName());
                // gl.setModifiedValues(r);
                // gl.setType(GLOBAL_LOG_UPDATE);
                // globalLogManager.insertGlobalLog(gl);
                return r;
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<RegionCommand> findRegionByParentIdNotDelete(Long id) {
        return regionDao.findRegionByParentIdNotDelete(id);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Region findRegionByNameAndParentId(String name, Long parentId) {
        return this.regionDao.findRegionByNameAndParentId(name, parentId);
    }
}
