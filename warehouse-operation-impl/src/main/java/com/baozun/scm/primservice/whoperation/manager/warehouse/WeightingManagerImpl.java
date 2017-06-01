package com.baozun.scm.primservice.whoperation.manager.warehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.baseservice.print.command.PrintDataCommand;
import com.baozun.scm.baseservice.print.constant.Constants;
import com.baozun.scm.baseservice.print.manager.printObject.PrintObjectManagerProxy;
import com.baozun.scm.primservice.whoperation.command.warehouse.WeightingCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.WhOutboundboxCommand;
import com.baozun.scm.primservice.whoperation.constant.CheckingPrint;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDeliveryInfoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhCheckingDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhFunctionOutBoundDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOdoPackageInfoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhPrintInfoDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.checking.CheckingManager;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdodeliveryInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhFunctionOutBound;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOdoPackageInfo;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhPrintInfo;

@Service("weightingManager")
@Transactional
public class WeightingManagerImpl extends BaseManagerImpl implements WeightingManager {

    public static final Logger log = LoggerFactory.getLogger(WeightingManager.class);
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

    @Autowired
    private WhPrintInfoDao whPrintInfoDao;

    @Autowired
    private CheckingManager checkingManager;

    @Autowired
    private WhOdoDeliveryInfoDao whOdoDeliveryInfoDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WeightingCommand inputResponse(WeightingCommand command) {
        // Boolean flag = this.checkParam(command);
        String outboundBoxCode = command.getOutboundBoxCode();
        String waybillCode = command.getWaybillCode();
        if (true) {
            command = findInputResponse(command);
            command.setOutboundBoxCode(outboundBoxCode);
            command.setWaybillCode(waybillCode);
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
        WeightingCommand weightingCommand = new WeightingCommand();
        if (StringUtils.hasLength(command.getWaybillCode())) {
            // 通过运单号查找待称重信息
            weightingCommand = whCheckingDao.findByWaybillCode(command.getWaybillCode(), command.getOuId());
        } else {
            // 通过出库箱号查找带称重信息
            // command = whCheckingDao.findByOutboundBoxCode(command.getOutboundBoxCode(),
            // command.getOuId());
            String outboundBoxCode = command.getOutboundBoxCode();
            weightingCommand = whCheckingDao.findByOutboundBoxCodeForChecking(outboundBoxCode, command.getOuId());
            if (null == weightingCommand) {
                weightingCommand = whCheckingDao.findByOutboundBoxCodeForChecking1(outboundBoxCode, command.getOuId());
            }
            weightingCommand.setOutboundBoxCode(outboundBoxCode);
        }
        return weightingCommand;
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
    public void weighting(WeightingCommand command, Long userId) {
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

        WhOdodeliveryInfo whOdodeliveryInfo = new WhOdodeliveryInfo();
        whOdodeliveryInfo.setOutboundboxCode(outboundBoxCode);
        whOdodeliveryInfo.setOuId(ouId);
        List<WhOdodeliveryInfo> whOdodeliveryInfoList = whOdoDeliveryInfoDao.findListByParam(whOdodeliveryInfo);
        whOdodeliveryInfo = whOdodeliveryInfoList.get(0);

        // 2.判断称重集中是否满足浮动百分比
        if (outbound.getIsValidateWeight()) {
            Long actualWeight = command.getActualWeight();
            Long calcWeight = packageInfo.getCalcWeight();
            Integer floats = packageInfo.getFloats();
            Double difference = (double) Math.abs(actualWeight - calcWeight);
            Double calcDifference = (double) (calcWeight * floats / 100);
            if (difference > calcDifference) {
                throw new BusinessException("excceed");
            }
        }
        // 3.保存包裹实际重量
        packageInfo.setActualWeight(command.getActualWeight());
        packageInfo.setModifiedId(userId);
        int cnt = whOdoPackageInfoDao.saveOrUpdateByVersion(packageInfo);
        if (0 > cnt) {
            throw new BusinessException("save fail");
        }
        // 4.打印单据
        // printObjectManagerProxy.printCommonInterface(data, printDocType, userId, ouId);
        this.print2(outbound.getWeighingPrint(), whOdodeliveryInfo.getWaybillCode(), outboundBoxCode, packageInfo.getId(), userId, ouId);
        // 5.更新出库单状态
        WhOdo odo = whOdoDao.findByIdOuId(odoId, ouId);
        WhOdoLine line = new WhOdoLine();
        line.setOdoId(odoId);
        line.setOuId(ouId);
        List<WhOdoLine> odoLineList = whOdoLineDao.findListByParamExt(line);
        if (null != odoLineList && !odoLineList.isEmpty()) {
            for (WhOdoLine odoLine : odoLineList) {
                odoLine.setOdoLineStatus(OdoStatus.ODOLINE_WEIGHT_FINISH);
                odoLine.setModifiedId(userId);
                whOdoLineDao.saveOrUpdateByVersion(odoLine);
            }
        }
        odo.setOdoStatus(OdoStatus.WEIGHING_FINISH);
        odo.setModifiedId(userId);
        whOdoDao.saveOrUpdateByVersion(odo);
        // 6.更新出库箱状态
        WhOutboundboxCommand whOutboundboxCommand = whOutboundboxDao.findByOutboundBoxCode(outboundBoxCode, ouId);
        WhOutboundbox whOutboundbox = new WhOutboundbox();
        BeanUtils.copyProperties(whOutboundboxCommand, whOutboundbox);
        whOutboundbox.setStatus("9");
        whOutboundboxDao.update(whOutboundbox);
    }

    /**
     * [业务方法] 称重打印
     * @param weightingPrint
     * @param packageInfoId
     */
    private void print(String weightingPrint, String waybillCode, String outboundBoxCode, Long packageInfoId, Long userId, Long ouId) {
        if (weightingPrint.indexOf("3") >= 0) {
            // 面单打印
            PrintDataCommand data = new PrintDataCommand();
            List<Long> idsList = Arrays.asList(packageInfoId);
            data.setIdList(idsList);
            printObjectManagerProxy.printCommonInterface(data, Constants.PRINT_ORDER_TYPE_1, userId, ouId);
        }
        if (weightingPrint.indexOf("4") >= 0) {
            // 箱标签打印
            PrintDataCommand data = new PrintDataCommand();
            List<Long> idsList = Arrays.asList(packageInfoId);
            data.setIdList(idsList);
            printObjectManagerProxy.printCommonInterface(data, Constants.PRINT_ORDER_TYPE_15, userId, ouId);
        }
    }

    /**
     * [业务方法] 称重打印2
     * @param weightingPrint
     * @param packageInfoId
     */
    private void print2(String weightingPrint, String waybillCode, String outboundBoxCode, Long packageInfoId, Long userId, Long ouId) {
        if (!StringUtils.isEmpty(weightingPrint)) {
            String[] weightingPrintArray = weightingPrint.split(",");
            for (int i = 0; i < weightingPrintArray.length; i++) {
                List<Long> idsList = new ArrayList<Long>();
                List<WhPrintInfo> whPrintInfoLst = whPrintInfoDao.findByOutboundboxCodeAndPrintType(outboundBoxCode, weightingPrintArray[i], ouId);
                if (null == whPrintInfoLst || 0 == whPrintInfoLst.size()) {
                    idsList.add(packageInfoId);
                    WhPrintInfo whPrintInfo = new WhPrintInfo();
                    whPrintInfo.setOutboundboxCode(outboundBoxCode);
                    whPrintInfo.setOuId(ouId);
                    whPrintInfo.setPrintType(weightingPrintArray[i]);
                    whPrintInfo.setPrintCount(1);// 打印次数
                    whPrintInfoDao.insert(whPrintInfo);
                    try {
                        if (CheckingPrint.SINGLE_PLANE.equals(weightingPrintArray[i])) {
                            // 面单
                            checkingManager.printSinglePlane(outboundBoxCode, waybillCode, userId, ouId, null);
                        }
                        if (CheckingPrint.BOX_LABEL.equals(weightingPrintArray[i])) {
                            // 箱标签
                            checkingManager.printBoxLabel(outboundBoxCode, userId, ouId, null);
                        }
                    } catch (Exception e) {
                        log.error("WhCheckingManagerImpl printDefect is execption" + e);
                    }
                } else {
                    Integer printCount = whPrintInfoLst.get(0).getPrintCount();
                    if (printCount < 1) {
                        WhPrintInfo printfo = whPrintInfoLst.get(0);
                        printfo.setPrintCount(1);
                        whPrintInfoDao.saveOrUpdate(printfo);
                        try {
                            if (CheckingPrint.SINGLE_PLANE.equals(weightingPrintArray[i])) {
                                // 面单
                                checkingManager.printSinglePlane(outboundBoxCode, waybillCode, userId, ouId, null);
                            }
                            if (CheckingPrint.BOX_LABEL.equals(weightingPrintArray[i])) {
                                // 箱标签
                                checkingManager.printBoxLabel(outboundBoxCode, userId, ouId, null);
                            }
                        } catch (Exception e) {
                            log.error("WhCheckingManagerImpl printDefect is execption" + e);
                        }
                    }
                }
            }
        }
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WeightingCommand inputResponseForChecking(WeightingCommand command) {
        WeightingCommand weightingCommand = new WeightingCommand();
        String outboundBoxCode = command.getOutboundBoxCode();
        weightingCommand = whCheckingDao.findByOutboundBoxCodeForChecking(outboundBoxCode, command.getOuId());
        if (null == weightingCommand) {
            weightingCommand = whCheckingDao.findByOutboundBoxCodeForChecking(outboundBoxCode, command.getOuId());
        }
        weightingCommand.setOutboundBoxCode(outboundBoxCode);
        return weightingCommand;
    }
}
