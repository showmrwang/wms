package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.warehouse.WeightingCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionOutBoundDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOdoPackageInfoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOdoPackageInfo;

@Service("weightingManager")
@Transactional
public class WeightingManagerImpl extends BaseManagerImpl implements WeightingManager {

    @Autowired
    private WhOutboundboxDao whOutboundboxDao;

    @Autowired
    private WhOdoDao whOdoDao;

    @Autowired
    private WhFunctionOutBoundDao whFunctionOutBoundDao;

    @Autowired
    private WhOdoPackageInfoDao whOdoPackageInfoDao;

    @Autowired
    private PrintObjectManagerProxy printObjectManagerProxy;

    @Autowired
    private WhOdoLineDao whOdoLineDao;

    @Autowired
    private WhCheckingDao whCheckingDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WeightingCommand inputResponse(WeightingCommand command) {
        // Boolean flag = this.checkParam(command);
        if (true) {
            command = findInputResponse(command);
        }
        return command;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public Boolean checkParam(WeightingCommand command) {
        Long ouId = command.getOuId();
        String outboundBoxCode = "";
        String waybillCode = "";
        Integer res = 0;
        if (StringUtils.hasLength(command.getOutboundBoxCode())) {
            outboundBoxCode = command.getOutboundBoxCode();
        } else if (StringUtils.hasLength(command.getWaybillCode())) {
            waybillCode = command.getWaybillCode();
        } else {
            throw new BusinessException("no data enter");
        }
        res = checkOutboundBoxStatus(outboundBoxCode, waybillCode, ouId);
        if (res < 0) {
            throw new BusinessException("outbound box status fail");
        }
        res = checkOdoStatus(outboundBoxCode, waybillCode, ouId);
        if (res < 0) {
            throw new BusinessException("odo status fail");
        }
        return true;
    }

    private WeightingCommand findInputResponse(WeightingCommand command) {
        if (StringUtils.hasLength(command.getWaybillCode())) {
            // 通过运单号查找待称重信息
            command = whCheckingDao.findByWaybillCode(command.getWaybillCode(), command.getOuId());
        } else {
            // 通过出库箱号查找带称重信息
            command = whCheckingDao.findByOutboundBoxCode(command.getOutboundBoxCode(), command.getOuId());
        }
        return command;
    }

    private Integer checkOutboundBoxStatus(String outboundBoxCode, String waybillCode, Long ouId) {
        String status = this.whOutboundboxDao.checkOutboundBoxStatus(outboundBoxCode, waybillCode, ouId);
        if (null == status || !"10".equals(status)) {
            return -1;
        }
        return 1;
    }

    private Integer checkOdoStatus(String outboundBoxCode, String waybillCode, Long ouId) {
        String status = this.whOdoDao.checkOdoStatus(outboundBoxCode, waybillCode, ouId);
        if (null == status || !"10".equals(status)) {
            return -1;
        }
        return 1;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void weighting(WeightingCommand command) {
        Long funcId = command.getFuncId();
        Long ouId = command.getOuId();
        Long odoId = command.getOdoId();
        // Long outboundBoxId = command.getOutboundBoxId();
        String outboundBoxCode = command.getOutboundBoxCode();
        // 1.判断是否校验称重和计量
        WhFunctionOutBound outbound = this.whFunctionOutBoundDao.findByFunctionIdExt(funcId, ouId);
        if (null == outbound) {
            throw new BusinessException("no function");
        }
        WhOdoPackageInfo packageInfo = whOdoPackageInfoDao.findByOdoIdAndOutboundBoxCode(odoId, outboundBoxCode, ouId);
        // 2.判断称重集中是否满足浮动百分比
        if (outbound.getIsValidateWeight()) {
            Long actualWeight = command.getActualWeight();
            Long calcWeight = packageInfo.getCalcWeight();
            Integer floats = packageInfo.getFloats();
            Long difference = Math.abs(actualWeight - calcWeight) / calcWeight * 100;
            if (difference > floats) {
                throw new BusinessException("excceed");
            }
        }
        // 3.保存包裹实际重量
        packageInfo.setActualWeight(command.getActualWeight());
        int cnt = whOdoPackageInfoDao.saveOrUpdateByVersion(packageInfo);
        if (0 > cnt) {
            throw new BusinessException("save fail");
        }
        // 4.打印单据
        // printObjectManagerProxy.printCommonInterface(data, printDocType, userId, ouId);
        // 5.更新出库单状态
        WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
        WhOdoLine line = new WhOdoLine();
        line.setOdoId(odoId);
        line.setOuId(ouId);
        List<WhOdoLine> odoLineList = whOdoLineDao.findListByParamExt(line);
        if (null != odoLineList && !odoLineList.isEmpty()) {
            for (WhOdoLine odoLine : odoLineList) {
                odoLine.setOdoLineStatus(OdoStatus.ODOLINE_WEIGHT_FINISH);
                whOdoLineDao.saveOrUpdateByVersion(odoLine);
            }
        }
        odo.setOdoStatus(OdoStatus.WEIGHING_FINISH);
        whOdoDao.saveOrUpdateByVersion(odo);
    }


}
