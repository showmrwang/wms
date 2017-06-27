package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.ArrayList;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.odo.WhOdoOutBoundBoxCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoOutBoundBoxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.ContainerDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoOutBoundBox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOperation;

@Service("odoOutBoundBoxMapper")
@Transactional
public class OdoOutBoundBoxMapperImpl extends BaseManagerImpl implements OdoOutBoundBoxMapper {
    protected static final Logger log = LoggerFactory.getLogger(OdoManager.class);
    
    @Autowired
    private WhOdoOutBoundBoxDao whOdoOutBoundBoxDao;
    
    @Autowired
    private ContainerDao containerDao;
    
    /**
     * [业务方法] 波次中创拣货工作-获取波次中的所有小批次
     * @param waveId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoOutBoundBox> getPickingWorkWhOdoOutBoundBox(Long waveId, Long ouId) {
        List<WhOdoOutBoundBox> whOdoOutBoundBoxList = this.whOdoOutBoundBoxDao.findPickingWorkWhOdoOutBoundBox(waveId, ouId);
        return whOdoOutBoundBoxList;
    }
    
    /**
     * [业务方法] 波次中创拣货工作-根据批次查询小批次分组数据
     * @param WhOdoOutBoundBox
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoOutBoundBox> getOdoOutBoundBoxForGroup(WhOdoOutBoundBox whOdoOutBoundBox) {
        List<WhOdoOutBoundBox> whOdoOutBoundBoxList = this.whOdoOutBoundBoxDao.getOdoOutBoundBoxForGroup(whOdoOutBoundBox);
        return whOdoOutBoundBoxList;
    }
    
    /**
     * [业务方法] 波次中创拣货工作-根据批次分组查询所有出库箱/容器信息
     * @param WhOdoOutBoundBox
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoOutBoundBoxCommand> getOdoOutBoundBoxListByGroup(WhOdoOutBoundBox whOdoOutBoundBox) {
        List<WhOdoOutBoundBoxCommand> whOdoOutBoundBoxCommandList = this.whOdoOutBoundBoxDao.getOdoOutBoundBoxListByGroup(whOdoOutBoundBox);
        return whOdoOutBoundBoxCommandList;
    }

    /**
     * [业务方法] 波次中创拣货工作-查询对应的耗材
     * @param outbounxboxTypeId
     * @param outbounxboxTypeCode
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Long findOutboundboxType(Long outbounxboxTypeId, String outbounxboxTypeCode, Long ouId) {
        Long skuId = this.whOdoOutBoundBoxDao.findOutboundboxType(outbounxboxTypeId, outbounxboxTypeCode, ouId);
        return skuId;
    }

    @Override
    public WhOdoOutBoundBoxCommand findWhOdoOutBoundBoxCommandById(Long id, Long ouId) {
        WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand = this.whOdoOutBoundBoxDao.findWhOdoOutBoundBoxCommandById(id, ouId);
        return whOdoOutBoundBoxCommand;
    }

    @Override
    public Boolean saveOrUpdate(WhOdoOutBoundBoxCommand whOdoOutBoundBoxCommand) {
        WhOdoOutBoundBox whOdoOutBoundBox = new WhOdoOutBoundBox();
        //复制数据        
        BeanUtils.copyProperties(whOdoOutBoundBoxCommand, whOdoOutBoundBox);
        if(null != whOdoOutBoundBoxCommand.getId() ){
            whOdoOutBoundBoxDao.saveOrUpdateByVersion(whOdoOutBoundBox);
        }else{
            whOdoOutBoundBoxDao.insert(whOdoOutBoundBox);
        }
        return null;
    }

    /**
     * 查询波次中的所有小批次 -- 捡货工作
     * 
     * @param waveId
     * @param ouId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoOutBoundBox> getBoxBatchsForPicking(Long waveId, Long ouId) {
     // 查询波次中的所有小批次
        List<WhOdoOutBoundBox> whOdoOutBoundBoxList = whOdoOutBoundBoxDao.findPickingWorkWhOdoOutBoundBox(waveId, ouId);
        return whOdoOutBoundBoxList;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> getWaveOdoIdList(Long waveId, Long ouId){
        List<Long> waveOdoIdList = whOdoOutBoundBoxDao.getWaveOdoIdList(waveId, ouId);
        return waveOdoIdList;
    }

    /**
     * 查询波次下已经创建周转箱或推荐小车的记录
     * 
     * @param waveId
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhOdoOutBoundBox> findOdoOutBoundByWaveId(Long waveId) {
        return whOdoOutBoundBoxDao.findOdoOutBoundByWaveId(waveId);
    }

    /**
     * 重置波次推荐的周转箱或小车
     * @param boxList
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void ResetWaveBox(List<WhOdoOutBoundBox> boxList) {
        List<Long> containerId=new ArrayList<Long>();
        List<Long> outerContainerId=new ArrayList<Long>();
        List<Long> odoOutBoundBoxId=new ArrayList<Long>();
        for(WhOdoOutBoundBox box:boxList){
            //周转箱id
            if(box.getContainerId()!=null && !"".equals(box.getContainerId())){
                containerId.add(box.getContainerId());
            }
            //小车id
            if(box.getOuterContainerId()!=null && !"".equals(box.getOuterContainerId())){
                outerContainerId.add(box.getOuterContainerId());
            }
            odoOutBoundBoxId.add(box.getId());
        }
        //删除波次的推荐记录
        whOdoOutBoundBoxDao.deleteWaveByIds(odoOutBoundBoxId);
        //删除容器列表的周转箱
        containerDao.deleteBoxTypeByIds(containerId);
        //释放小车
        containerDao.updateCarTypeByIds(outerContainerId);
    }
}
