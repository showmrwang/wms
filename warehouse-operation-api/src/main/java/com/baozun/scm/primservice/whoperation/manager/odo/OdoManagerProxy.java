package com.baozun.scm.primservice.whoperation.manager.odo;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.apache.poi.ss.usermodel.Workbook;

import com.baozun.scm.primservice.whoperation.command.odo.OdoAddressCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoGroupCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoVasCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.command.wave.WaveLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;
import com.baozun.scm.primservice.whoperation.model.odo.wave.WhWave;

public interface OdoManagerProxy extends BaseManager {
    /**
     * [业务方法]出库单一览
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<OdoResultCommand> findOdoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法，接口方法]创建出库单
     * 
     * @param odoGroup
     * @return
     */
    ResponseMsg createOdo(OdoGroupCommand odoGroup);

    /**
     * [通用方法]根据ID,OUID查找ODO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdo findOdOById(Long id, Long ouId);

    /**
     * [通用方法]根据ODO_ID,OUID查找ODOTRANSPORTMGMT
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdoTransportMgmt findTransportMgmtByOdoIdOuId(Long odoId, Long ouId);

    /**
     * [通用方法]根据ODOLINEID和OUID查找ODOLINE
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdoLine findOdoLineById(Long id, Long ouId);

    /**
     * [业务方法]保存ODO明细
     * 
     * @param lineCommand
     */
    void saveOdoUnit(OdoLineCommand lineCommand);

    /**
     * [业务方法]出库单明细分页查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<OdoLineCommand> findOdoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [通用方法]根据ODOID和OUID查找配货信息
     * 
     * @param odoId
     * @param ouId
     * @return
     */
    WhOdoAddress findOdoAddressByOdoId(Long odoId, Long ouId);

    /**
     * [业务方法]创建出库单分支:保存配送对象
     * 
     * @param odoAddressCommand
     */
    void saveDistributionUnit(OdoAddressCommand odoAddressCommand);

    /**
     * [通用方法]根据ID查找odoAddress
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhOdoAddress findOdoAddressById(Long id, Long ouId);

    /**
     * [业务方法]创建出库单分支：保存出库对象
     * 
     * @param odoAddressCommand
     */
    void saveConsigneeUnit(OdoAddressCommand odoAddressCommand);

    /**
     * [通用方法]查找出库单增值服务
     * 
     * @param odoId
     * @param object
     * @param odoLineId
     * @param vasType
     * @param ouId
     * @return
     */
    List<WhOdoVas> findOdoVasByOdoIdOdoLineIdType(Long odoId, Long odoLineId, String vasType, Long ouId);

    /**
     * [通用方法]查找出库单仓库增值服务;
     * 
     * @param odoId
     * @param odoLineId
     * @param ouId
     * @return
     */
    List<WhOdoVasCommand> findOdoOuVasCommandByOdoIdOdoLineIdType(Long odoId, Long odoLineId, Long ouId);

    /**
     * 保存出库单仓库增值服务
     * 
     * @param odoId
     * @param odoLineId
     * @param ouId
     * @param odoVasList
     * @param logId
     */
    void saveOdoOuVas(Long odoId, Long odoLineId, Long ouId, List<WhOdoVasCommand> odoVasList, String logId);

    /**
     * [通用方法]查找出库单快递增值服务
     * 
     * @param odoId
     * @param odoLineId
     * @param ouId
     * @return
     */
    List<WhOdoVasCommand> findOdoExpressVasCommandByOdoIdOdoLineId(Long odoId, Long odoLineId, Long ouId);

    /**
     * [业务方法]保存出库单快递增值服务
     * 
     * @param odoId
     * @param odoLineId
     * @param ouId
     * @param odoVasList
     * @param logId
     */
    void saveOdoExpressVas(Long odoId, Long odoLineId, Long ouId, List<WhOdoVasCommand> odoVasList, String logId);

    
    /**
     * [业务方法]出库单操作：整单取消/行取消 @mender yimin.lu 2017/2/24
     * 
     * @param odo
     * @param ouId
     * @param isOdoCancel
     * @param lineList
     * @param userId
     * @param logId
     */
    public void cancel(WhOdo odo, Long ouId, Boolean isOdoCancel, List<WhOdoLine> lineList, Long userId, String logId);

    /**
     * 查询创建波次分组出库单
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<OdoWaveGroupResultCommand> findOdoSummaryListForWaveByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]根据参数查询出库单头信息
     * 
     * @param command
     * @return
     */
    List<OdoResultCommand> findOdoCommandListForWave(OdoSearchCommand command);

    /**
     * [业务方法]创建波次的时候汇总信息
     * 
     * @param command
     * @return
     */
    OdoWaveGroupResultCommand findOdoSummaryForWave(OdoWaveGroupSearchCommand command);

    /**
     * [业务方法]出库单创建波次 @author yimin.lu 创建完整的波次数据
     * 
     * @param command
     * @return 返回波次号
     */
    String createOdoWave(OdoGroupSearchCommand command);

    /**
     * [业务方法]出库单创建波次【新】@author yimin.lu 创建波次头数据，并将出库单打标，稍后由定时任务创建波次明细
     * @mender yimin.lu 2017/4/19 调整接口，统一创波次流程【自定义，波次主档，定时】，不再负责封装出库单集合
     * @return
     */
    String createOdoWaveNew(Long waveMasterId, Long ouId, Long userId, List<Long> odoIdList, String logId);

    /**
     * [业务方法]订单池一览，自定义创建波次的出库单数据集合
     * 
     * @param command
     * @return
     */
    List<Long> findOdoIdListForWaveByCustom(OdoGroupSearchCommand command);

    /**
     * [业务方法]波次一览
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<WaveCommand> findWaveListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 删除波次
     * 
     * @param waveCommand
     */
    void deleteWave(WaveCommand waveCommand);

    /**
     * 创建出库单完成
     * 
     * @param odoCommand
     */
    void finishCreateOdo(OdoCommand odoCommand);

    /**
     * 出库单导出
     * 
     * @param odoSearchCommand
     * @return
     */
    List<String> findExportExeclList(OdoSearchCommand odoSearchCommand);

    /**
     * 查找波次
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhWave getWaveByIdAndOuId(Long id, Long ouId);

    /**
     * 出库单波次列表
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<WaveLineCommand> findWaveLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * 从波次中剔除出库单
     * 
     * @param waveLineCommand
     */
    void divFromWaveByOdo(WaveLineCommand waveLineCommand);

    /**
     * 释放波次
     * 
     * @param waveCommand
     */
    void releaseWave(WaveCommand waveCommand);

    /**
     * 【业务方法】
     * 
     * @param waveCommand
     */
    void cancelWave(WaveCommand waveCommand);

    /**
     * [业务方法]运行波次
     * 
     * @param waveCommand
     */
    void runWave(WaveCommand waveCommand);

    /**
     * 导入
     * 
     * @param url
     * @param errorUrl
     * @param fileName
     * @param locale
     * @param userId
     * @param ouId
     */
    Workbook importWhOdo(String url, String errorUrl, String fileName, Locale locale, Long ouId, Long userId);

    /**
     * 查找延迟创建的波次
     * 
     * @param ouId
     * @return
     */
    List<WhWave> findWaveToBeCreated(Long ouId);

    /**
     * [业务方法]查找需要加入波次的出库单明细
     * 
     * @param waveCode
     * @param ouId
     * @return
     */
    List<WhOdoLine> findOdoLineToBeAddedToWave(String waveCode, Long ouId);

    /**
     * [业务方法]查找需要加入波次的出库单
     * 
     * @param waveCode
     * @param ouId
     * @return
     */
    List<Long> findOdoToBeAddedToWave(String code, Long ouId);

    /**
     * 【定时任务】将出库单明细添加到波次中
     * 
     * @param odoIdList
     * @param wave
     */
    void addOdoLineToWave(List<Long> odoIdList, WhWave wave);

    /**
     * 【定时任务】创建完成波次
     * 
     * @param wave
     */
    void finishCreateWave(WhWave wave);

    WhOdo findByExtCodeStoreIdOuId(String extOdoCode, Long storeId, Long ouId);
    
    /**
     * 根据出库单Id获取物流相关信息(增值服务,物流商推荐,获取运单号)
     * @author kai.zhu
     * @version 2017年4月24日
     */
    void getLogisticsInfoByOdoId(Long odoId, String logId, Long ouId);

    /**
     * 从一批出库单集合中筛选出可以创建波次的出库单
     * 
     * @param odoIdOriginalList
     * @param ouId
     * @return
     */
    List<Long> findNewOdoIdList(List<Long> odoIdOriginalList, Long ouId);

    /**
     * 从一批出库单集合中，筛选出 发票公司对应的店铺集合
     * 
     * @param odoIdList
     * @param ouId
     * @return
     */
    Map<String, List<Long>> getStoreIdMapByOdoIdListGroupByInvoice(List<Long> odoIdList, Long ouId);

    /**
     * 从一批出库单集合中根据店铺集合筛选出相应的出库单
     * 
     * @param odoIdList
     * @param value
     * @param ouId
     * @return
     */
    List<Long> findOdoIdListByStoreIdListAndOriginalIdList(List<Long> odoIdList, List<Long> storeIdList, Long ouId);


}
