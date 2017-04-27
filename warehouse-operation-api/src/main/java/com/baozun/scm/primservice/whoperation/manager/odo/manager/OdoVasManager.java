package com.baozun.scm.primservice.whoperation.manager.odo.manager;

import java.util.List;

import com.baozun.scm.primservice.logistics.model.VasTransResult.VasLine;
import com.baozun.scm.primservice.whoperation.command.odo.WhOdoVasCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;

public interface OdoVasManager extends BaseManager {

    /**
     * [通用方法]查找出库单增值服务
     * 
     * @param odoId
     * @param odoLineId
     * @param vasType
     * @param ouId @required
     * @return
     */
    List<WhOdoVas> findOdoVasByOdoIdOdoLineIdType(Long odoId, Long odoLineId, String vasType, Long ouId);

    /**
     * [通用方法]查找出库单仓库增值服务
     * 
     * @param odoId
     * @param odoLineId
     * @param ouId @required
     * @return
     */
    List<WhOdoVasCommand> findOdoOuVasCommandByOdoIdOdoLineIdType(Long odoId, Long odoLineId, Long ouId);

    /**
     * [业务方法]
     * 
     * @param insertVasList
     * @param updateVasList
     * @param delVasList
     */
    void saveOdoOuVas(List<WhOdoVas> insertVasList, List<WhOdoVas> updateVasList, List<WhOdoVas> delVasList);

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
     * 保存物流服务推荐的物流增值服务
     * @author kai.zhu
     * @version 2017年4月26日
     * @param odoVasLineList 
     */
    void insertVasList(Long odoId, List<VasLine> vasList, List<WhOdoVas> odoVasLineList, Long ouId);

}
