package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.system.GlobalLogCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.OutInvBoxTypeCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.OutBoundBoxTypeDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.system.GlobalLogManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.OutBoundBoxType;

@Service("outBoundBoxTypeManager")
@Transactional
public class OutBoundBoxTypeManagerImpl implements OutBoundBoxTypeManager{

    public static final Logger log = LoggerFactory
            .getLogger(OutBoundBoxTypeManagerImpl.class);
    @Autowired
    private OutBoundBoxTypeDao outBoundBoxTypeDao;
    
    @Autowired
    private GlobalLogManager globalLogManager;
    
    
    /**
     * 查询出库箱类型
     * @param page
     * @param sorts
     * @param param
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Pagination<OutInvBoxTypeCommand> findListByQueryMapWithPage(Page page, Sort[] sorts, Map<String, Object> param) {
        // TODO Auto-generated method stub
        log.info("OutInventoryBoxTypeManagerImpl findListByQueryMapWithPage  is start");
        if (log.isDebugEnabled()) {
            log.debug("Param page is {}", page+"  Param sorts is {}",Sort.toSortStr(sorts)+" Param param is {}",param);
        }
        log.info("Param page is {}", page+"  Param sorts is {}",Sort.toSortStr(sorts)+" Param param is {}",param);
        Pagination<OutInvBoxTypeCommand> paginaction =null;
        try{
            paginaction= outBoundBoxTypeDao.findListByQueryMapWithPageExt(page, sorts, param);
        }catch(Exception e) {
            log.error("OutInventoryBoxTypeManagerImpl findListByQueryMapWithPage is error",e);
        }
       
        log.info("OutInventoryBoxTypeManagerImpl findListByQueryMapWithPage  is end");
        return paginaction;
    }
    
    /**
     * 根据id 和ouId 获取出库箱类型
     * @param id
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public OutInvBoxTypeCommand findOutInventoryBoxType(Long id, Long ouId) {
        // TODO Auto-generated method stub
        log.info("OutInventoryBoxTypeManagerImpl findOutInventoryBoxType  is start");
        if(log.isDebugEnabled()) {
            log.debug("Param id is {}", id+"  Param ouId is {}",ouId);
        }
        OutInvBoxTypeCommand command =  outBoundBoxTypeDao.findOutInventoryBoxType(id, ouId);
        log.info("OutInventoryBoxTypeManagerImpl findOutInventoryBoxType  is end");
        return command;
    }

    /**
     * 保存或更新出库箱类型
     * @param out
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void saveOrUpdate(OutInvBoxTypeCommand command,Long userId) {
        // TODO Auto-generated method stub
        log.info("OutInventoryBoxTypeManagerImpl saveOrUpdateBoxType is start"); 
        if(log.isDebugEnabled()) {
            log.debug("OutInventoryBoxTypeManagerImpl saveOrUpdateBoxType is param {out is []}",command); 
        }
        //一个商品只能绑定一个出库箱
        OutInvBoxTypeCommand uniqueCommand = new OutInvBoxTypeCommand();
        uniqueCommand.setSkuId(command.getSkuId());
        uniqueCommand.setId(command.getId());
        uniqueCommand.setOuId(command.getOuId());
        Long count = outBoundBoxTypeDao.checkUnique(uniqueCommand);
        if(0 != count) {
            throw new BusinessException(ErrorCodes.EXIST_SKU_BOUND_BOX);
        }
        OutBoundBoxType addOut = new OutBoundBoxType();
        //将command基本信息复制到outType中
        BeanUtils.copyProperties(command, addOut);
        if(null == addOut.getId()) {
            addOut.setCreateTime(new Date());
            addOut.setCreatedId(userId);
            outBoundBoxTypeDao.insert(addOut);
            this.insertGlobalLog(addOut, userId, Constants.GLOBAL_LOG_INSERT);
        }else{
            OutBoundBoxType type = outBoundBoxTypeDao.findByIdExt(command.getId(), command.getOuId());
            addOut.setModifiedId(userId);
            addOut.setLastModifyTime(new Date());
            addOut.setCreateTime(type.getCreateTime());
            addOut.setCreatedId(type.getCreatedId());
            outBoundBoxTypeDao.saveOrUpdateByVersion(addOut);
            this.insertGlobalLog(addOut, userId, Constants.GLOBAL_LOG_UPDATE); 
        }
        log.info("OutInventoryBoxTypeManagerImpl saveOrUpdateBoxType is end"); 
    }

    /**
     * 批量停用/启用
     * @param idList
     * @param lifeCycle
     * @param userId
     * @param ouId
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void updateLifeCycle(List<Long> idList, Integer lifeCycle, Long userId, Long ouId) {
        // TODO Auto-generated method stub
        log.info("OutInventoryBoxTypeManagerImpl updateLifeCycle is start"); 
        if(log.isDebugEnabled()) {
            log.debug("OutInventoryBoxTypeManagerImpl updateLifeCycle is param {idList is []}",idList); 
        }
        for(Long id:idList) {
            OutBoundBoxType out = outBoundBoxTypeDao.findByIdExt(id, ouId);
            if(null == out) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            out.setLifecycle(lifeCycle); 
            int count = outBoundBoxTypeDao.saveOrUpdateByVersion(out);
            if(1!=count) {
                throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
            }
            insertGlobalLog(out,userId,Constants.GLOBAL_LOG_UPDATE) ;
        }
        log.info("OutInventoryBoxTypeManagerImpl updateLifeCycle is end"); 
        
    }
    
    
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean checkUnique(OutInvBoxTypeCommand o) {
        // TODO Auto-generated method stub
        log.info("OutInventoryBoxTypeManagerImpl findByParam is start"); 
        if(log.isDebugEnabled()) {
            log.debug("OutInventoryBoxTypeManagerImpl updateLifeCycle is param {o is []}",o); 
        }
        Boolean result = false;  //默认所有的编码或者名称都不相同
        Long count =  outBoundBoxTypeDao.checkUnique(o);
         if(0 != count) {   //数据库存在编码或者名称相同的出库箱类型记录
              result = true;
         }
        log.info("OutInventoryBoxTypeManagerImpl findByParam is end"); 
        return result;
    }
    
    /***
     * 写日志
     * @param checkRule
     * @param userId
     */
    private void insertGlobalLog(OutBoundBoxType outType,Long userId,String type) {
        
        GlobalLogCommand gl = new GlobalLogCommand();
        gl.setModifiedId(userId);
        gl.setObjectType(outType.getClass().getSimpleName());
        gl.setModifiedValues(outType);
        gl.setOuId(outType.getOuId());
        gl.setType(type);
        if (log.isDebugEnabled()) {
            log.debug("OutInventoryBoxTypeManagerImpl updateLifeCycle, save globalLog to sharedDB, param [globalLogCommand:{}]", gl);
        }
        globalLogManager.insertGlobalLog(gl);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<OutBoundBoxType> findListByParamExt(OutBoundBoxType o) {
        return this.outBoundBoxTypeDao.findListByParam(o);
    }
    

}
