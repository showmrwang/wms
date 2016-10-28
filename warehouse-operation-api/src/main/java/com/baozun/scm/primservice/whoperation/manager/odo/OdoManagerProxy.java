package com.baozun.scm.primservice.whoperation.manager.odo;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.odo.OdoAddressCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoGroupCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoLineCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.OdoSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoVasCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupResultCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.OdoWaveGroupSearchCommand;
import com.baozun.scm.primservice.whoperation.command.odo.wave.WaveCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;

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
    ResponseMsg createOdoFromWms(OdoGroupCommand odoGroup);

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
     * [业务方法]删除出库单
     * 
     * @param id
     * @param ouId
     * @param logId
     */
    void deleteOdo(Long id, Long ouId, String logId);

    /**
     * [业务方法]删除出库单;可批量
     * 
     * @param lineCommand
     */
    void deleteLines(OdoLineCommand lineCommand);

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
     * [业务方法]出库单创建波次
     * 
     * @param command
     * @return 返回波次号
     */
    String createOdoWave(OdoWaveGroupSearchCommand command);

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
}
