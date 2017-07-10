package com.baozun.scm.primservice.whoperation.manager.handover;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.handover.HandoverCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhHandoverStationCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.handover.HandoverCollection;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;

public interface HandoverManager extends BaseManager {
    /**
     * 出库箱放入交接库中
     * 
     * @param whOutboundbox 出库箱
     * @param findhandoverStationByCode 交接工位编码
     */

    void insertHandoverCollection(WhOutboundbox whOutboundbox, WhHandoverStationCommand findhandoverStationByCode);

    /**
     * 查询批次号
     * 
     * @param handoverStationId 交接工位id
     * @param status 状态
     */
    String findBatchByHandoverStationIdAndStatus(Long handoverStationId, Integer status, Long ouId);

    /**
     * 查询批次号
     * 
     * @param handoverStationId 交接工位id
     * @param status 状态
     */
    List<HandoverCollection> findByHandoverStation(Long id, Long ouId);

    /**
     * 保存或更新集货交接
     * 
     * @param HandoverCollection 集货交接
     */
    void saveOrUpdateHandoverCollection(HandoverCollection hc);

    /**
     * 交接出库
     * 
     * @param hcList 该库位下的所有出库箱信息
     * @param ouId
     * @param userId
     * @param ouCode
     */
    List<Long> handover(List<HandoverCollection> hcList, Long ouId, Long userId, String ouCode);

    public String findOuCodeByOuId(Long ouId);

    /**
     * 查看是否都交接完成
     * 
     * @param hcList 该库位下的所有出库箱信息
     * @param ouId
     * @return
     */
    Long check(List<HandoverCollection> hcList, Long ouId);

    /**
     * 根据出库箱编码找集货信息
     * 
     * @param outboundBoxCode
     * @param ouId
     * @return
     */
    HandoverCollection findHandoverCollectionByOutboundboxCode(String outboundBoxCode, Long ouId);


    void packageWeightCalculationByOdo(WhOutboundbox whOutboundbox, Long ouId, Long userId);

    /**
     * 是否电子面单
     * 
     */
    Boolean isOl(Long id, Long ouId);

    String findStoreCodeByStoreId(Long storeId);

    void deleteByOuId(Long id, Long ouId);

    int cancelBoxToHandoverstation(String outboundBoxCode, Long ouId);

    List<HandoverCollection> findhandoverCollectionByHandoverStationId(Long id, Long ouId);

    List<Long> findAlluser(Long ouId);

    Pagination<HandoverCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params);

    String findHandoverBatchByOutboundboxCode(String outboundboxCode, Long ouId);

    HandoverCommand getBatchById(Long id, Long ouId);

    List<HandoverCollection> findhandoverCollectionByHandoverBatch(String handoverBatch, Long ouId);

    /**
     * 打印销售清单
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    // void printSalesList(List<Long> facilityIdsList, Long userId, Long ouId);
    //
}
