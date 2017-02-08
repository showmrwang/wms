package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoGroup;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.UomCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLineAttrSn;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveLine;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWaveMaster;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;

public interface OdoManager extends BaseManager {
    Pagination<OdoResultCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]创建出库单TODO
     * 
     * @param odo
     * @param odoLineList
     * @param transportMgmt
     * @param userId
     * @param ouId
     * @param lineSnList
     * @param odoVasList
     * @param odoAddress
     */
    void createOdo(WhOdo odo, List<WhOdoLine> odoLineList, WhOdoTransportMgmt transportMgmt, WhOdoAddress odoAddress, List<WhOdoVas> odoVasList, List<WhOdoLineAttrSn> lineSnList, Long ouId, Long userId);

    /**
     * [通用方法]根据ID,OUID查找ODO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdo findOdoByIdOuId(Long id, Long ouId);

    /**
     * [通用方法]根据ID,OUID查找ODO
     * 
     * @param id
     * @param ouId
     * @return
     */
    OdoCommand findOdoCommandByIdOuId(Long id, Long ouId);

    /**
     * [通用商品]查找出库单是否包含某种商品
     * 
     * @param odoId
     * @param skuId
     * @param ouId
     * @return
     */
    int existsSkuInOdo(Long odoId, Long skuId, Long ouId);

    /**
     * [业务方法]创建明细
     * 
     * @param line
     * @param insertVasList
     */
    void saveUnit(WhOdoLine line, List<WhOdoVas> insertVasList);

    /**
     * [业务方法]配置地址信息
     * 
     * @param odoAddress
     * @param transportMgmt
     * @param odo
     */
    void saveAddressUnit(WhOdoAddress odoAddress, WhOdo odo);

    /**
     * [业务方法]删除Odo
     * 
     * @param id
     * @param ouId
     * @param logId
     */
    void deleteOdo(Long id, Long ouId, String logId);

    /**
     * [业务方法]查询移除某些明细后，出库单的商品种类数；参数可选
     * 
     * @param idList
     * @param ouId
     * @return
     */
    Integer getSkuNumberAwayFormSomeLines(List<Long> idList, Long ouId);

    /**
     * [业务方法]出库单分组列表
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<OdoWaveGroupResultCommand> findOdoListForWaveByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]出库单分组列表
     * 
     * @author yimin.lu 查询优化设置
     * @param command
     * @return
     */
    List<OdoResultCommand> findOdoCommandListForWave(OdoSearchCommand command);

    /**
     * [业务方法]出库单分组列表
     * 
     * @param command
     * @return
     */
    List<Long> findOdoIdListForWave(OdoSearchCommand command);


    OdoWaveGroupResultCommand findOdoSummaryForWave(OdoWaveGroupSearchCommand command);

    /**
     * [业务方法]出库单分组列表
     * 
     * @param search
     * @return
     */
    List<WhOdo> findOdoListForWave(OdoSearchCommand search);

    /**
     * [通用方法]根据组别查找单位
     * 
     * @param groupCode
     * @param lifecycle
     * @return
     */
    List<UomCommand> findUomByGroupCode(String groupCode, Integer lifecycle);

    /**
     * 查找商品信息
     * 
     * @param skuId
     * @param ouId
     * @return
     */
    Sku findSkuByIdToShard(Long skuId, Long ouId);

    /**
     * [通用方法]查找波次主档
     * 
     * @param waveMasterId
     * @param ouId
     * @return
     */
    WhWaveMaster findWaveMasterByIdouId(Long waveMasterId, Long ouId);

    /**
     * [通用方法]
     * 
     * @param wave
     * @param WaveTemplateId
     * @param waveLineList
     * @param odoMap
     * @param odolineList
     * @param userId
     * @param logId
     * @return
     */
    void createOdoWave(WhWave wave, Long waveTemplateId, List<WhWaveLine> waveLineList, Map<Long, WhOdo> odoMap, List<WhOdoLine> odolineList, Long userId, String logId);

    /**
     * [通用方法] 修改出库单头和出库单明细状态
     * @param odoId
     * @param odoLineId
     * @param ouId
     * @param status
     * @return
     */
    Boolean updateOdoStatus(Long odoId, Long odoLineId, Long ouId, String status);

    /**
     * [业务方法] 软分配-剔除出库单头和明细逻辑
     * @param waveId
     * @param odoId
     * @param odoLineIds
     * @param ouId
     */
    void removeOdoAndLineWhole(Long waveId, Long odoId, List<Long> odoLineIds, Long ouId);
    
    /**
	 * [业务方法] 硬分配-根据提供波次ID查找当中有波次明细未分配规则的出库单ID
	 */
	List<OdoCommand> getNoRuleOdoIdList(List<Long> waveIdList, Long id);

    /**
     * [通用方法]根据waveCode,ouId查找所有的出库单
     * 
     * @param code
     * @param ouId
     * @return
     */
    List<WhOdo> findOdoListByWaveCode(String code, Long ouId);

    void finishCreateOdo(WhOdo odo, List<WhOdoLine> lineList);

    int updateByVersion(WhOdo odo);

    /**
     * 出库单导出
     * 
     * @param odoSearchCommand
     * @return
     */
    List<String> findExportExeclList(OdoSearchCommand odoSearchCommand);

    List<String> findDistinctCounterCode(Long ouId);

    List<Long> findOdoByCounterCode(String counterCode, Long ouId);

    void createOdo(List<OdoGroup> groupList, Long ouId, Long userId);

    /**
     * 【业务方法】统计批量出库单数据的总金额，总件数，商品种类数，体积数目
     * 
     * @param odoIdList
     * @return
     */
    WaveCommand findWaveSumDatabyOdoIdList(List<Long> odoIdList, Long ouId);

    /**
     * 【业务方法】延迟创建波次
     * 
     * @param wave
     * @param waveTemplateId
     * @param odoIdList
     */
    void createOdoWaveNew(WhWave wave, Long waveTemplateId, List<Long> odoIdList);

    /**
     * 【业务方法】查找延迟加入波次的出库单
     * 
     * @param waveCode
     * @param ouId
     * @return
     */
    List<Long> findOdoToBeAddedToWave(String waveCode, Long ouId);

}
