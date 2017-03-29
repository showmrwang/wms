package com.baozun.scm.primservice.whoperation.manager.archiv;

import java.util.Date;
import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.dao.archiv.OdoArchivDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoAddressDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoAttrDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoInvoiceDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoInvoiceLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineAttrDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineSnDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoVasDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhOutboundboxLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivIndex;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAddress;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoAttr;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoInvoice;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoInvoiceLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLineAttr;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLineSn;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoVas;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundbox;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLine;
import com.baozun.scm.primservice.whoperation.util.DateUtil;

/***
 * 出库单归档
 * 
 * @author bin.hu
 *
 */
@Service("odoArchivManager")
@Transactional
public class OdoArchivManagerImpl implements OdoArchivManager {

    protected static final Logger log = LoggerFactory.getLogger(OdoArchivManager.class);

    @Autowired
    private OdoArchivDao odoArchivDao;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private WhOdoLineDao whOdoLineDao;
    @Autowired
    private WhOdoLineSnDao whOdoLineSnDao;
    @Autowired
    private WhOdoAddressDao whOdoAddressDao;
    @Autowired
    private WhOdoAttrDao whOdoAttrDao;
    @Autowired
    private WhOdoInvoiceDao whOdoInvoiceDao;
    @Autowired
    private WhOdoInvoiceLineDao whOdoInvoiceLineDao;
    @Autowired
    private WhOdoLineAttrDao whOdoLineAttrDao;
    @Autowired
    private WhOdoTransportMgmtDao whOdoTransportMgmtDao;
    @Autowired
    private WhOdoVasDao whOdoVasDao;
    @Autowired
    private WhOutboundboxDao whOutboundboxDao;
    @Autowired
    private WhOutboundboxLineDao whOutboundboxLineDao;

    /***
     * 归档仓库Odo信息
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int archivOdo(Long odoid, Long ouid) {
        int count = 0;
        try {
            String sysDate = DateUtil.getSysDate();
            // 查询对应odo信息
            WhOdo whOdo = whOdoDao.findByIdOuId(odoid, ouid);
            if (null == whOdo) {
                log.warn("OdoArchivManagerImpl archivOdo warn whpo is null odoid:" + odoid + " ouid:" + ouid);
                return count;
            }
            // 插入odoArchiv信息
            whOdo.setSysDate(sysDate);
            whOdo.setArchivTime(new Date());
            int odo = odoArchivDao.archivWhOdo(whOdo);
            count += odo;
            // 归档odoLine+odoLineSn+odoLineAttr
            count = archivWhOdoLine(odoid, ouid, sysDate, count);
            // 归档odoAddress
            count = archivWhOdoAddress(odoid, ouid, sysDate, count);
            // 归档odoAttr
            count = archivWhOdoAttr(odoid, ouid, sysDate, count);
            // 归档odoInvoice+odoInvoiceLine
            count = archivWhOdoInvoice(odoid, ouid, sysDate, count);
            // 归档odoTransportMgmt
            count = archivWhOdoTransportMgmt(odoid, ouid, sysDate, count);
            // 归档odoVas by odoid
            count = archivWhOdoVas(odoid, ouid, sysDate, count);
            // 归档outboundbox by odoid
            count = archivWhOdoOutBoundBox(odoid, ouid, sysDate, count);
            // 归档Odo outboundBoxLine+outboundBoxLindSn信息
            count = archivWhOdoOutBoundBoxLine(odoid, ouid, sysDate, count);
            // 保存出库单索引数据(仓库) 只限于出库单状态为完成
            if (whOdo.getOdoStatus().equals(OdoStatus.ODO_OUTSTOCK_FINISH)) {
                WhOdoArchivIndex oai = new WhOdoArchivIndex();
                oai.setEcOrderCode(whOdo.getEcOrderCode());
                oai.setDataSource(whOdo.getDataSource());
                oai.setWmsOdoCode(whOdo.getOdoCode());
                oai.setSysDate(sysDate);
                oai.setOuId(ouid);
                int odoIndex = odoArchivDao.saveOdoArchivIndex(oai);
                if (odoIndex == 0) {
                    log.error("OdoArchivManagerImpl saveOdoArchivIndex error");
                    throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
                }
            }
        } catch (Exception e) {
            log.error("OdoArchivManagerImpl archivOdo error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }

    /**
     * 归档odoLine+odoLineSn+odoLineAttr
     * 
     * @param odoid
     * @param ouid
     * @param sysDate
     * @param count
     * @return
     */
    private int archivWhOdoLine(Long odoid, Long ouid, String sysDate, int count) {
        // 查询对应odoLine信息
        List<WhOdoLine> odoLineList = whOdoLineDao.findOdoLineListByOdoIdOuId(odoid, ouid);
        for (WhOdoLine ol : odoLineList) {
            // 插入odoLineArchiv信息
            ol.setSysDate(sysDate);
            int odoLine = odoArchivDao.archivWhOdoLine(ol);
            count += odoLine;
            // 查询对应odoLineSn信息
            List<WhOdoLineSn> odoLineSnList = whOdoLineSnDao.findWhOdoLineSnByOdoLineId(ol.getId(), ouid);
            for (WhOdoLineSn ols : odoLineSnList) {
                ols.setSysDate(sysDate);
                // 插入odoLineSnArchiv信息
                int odoLineSn = odoArchivDao.archivWhOdoLineSn(ols);
                count += odoLineSn;
            }
            // 查询对应odoLineAttr信息
            WhOdoLineAttr odoLineAttr = whOdoLineAttrDao.findWhOdoLineAttrByOdoLineId(ol.getId(), ouid);
            if (null != odoLineAttr) {
                // 插入WhOdoLineAttrArchiv信息
                odoLineAttr.setSysDate(sysDate);
                int ola = odoArchivDao.archivWhOdoLineAttr(odoLineAttr);
                count += ola;
            }
        }
        return count;
    }

    /**
     * 归档odoAddress
     * 
     * @param odoid
     * @param ouid
     * @param sysDate
     * @param count
     * @return
     */
    private int archivWhOdoAddress(Long odoid, Long ouid, String sysDate, int count) {
        // 查询odoAddress信息
        WhOdoAddress odoAddress = whOdoAddressDao.findOdoAddressByOdoId(odoid, ouid);
        if (null != odoAddress) {
            // 有信息插入odoAddressArchiv
            odoAddress.setSysDate(sysDate);
            int oa = odoArchivDao.archivWhOdoAddress(odoAddress);
            count += oa;
        }
        return count;
    }

    /**
     * 归档odoAttr
     * 
     * @param odoid
     * @param ouid
     * @param sysDate
     * @param count
     * @return
     */
    private int archivWhOdoAttr(Long odoid, Long ouid, String sysDate, int count) {
        // 查询odoAttr信息
        WhOdoAttr odoAttr = whOdoAttrDao.findWhOdoAttrByOdoId(odoid, ouid);
        if (null != odoAttr) {
            // 有数据插入odoAttrArchiv
            odoAttr.setSysDate(sysDate);
            int oa = odoArchivDao.archivWhOdoAttr(odoAttr);
            count += oa;
        }
        return count;
    }

    /***
     * 归档odoInvoice+odoInvoiceLine
     * 
     * @param odoid
     * @param ouid
     * @param sysDate
     * @param count
     * @return
     */
    private int archivWhOdoInvoice(Long odoid, Long ouid, String sysDate, int count) {
        // 查询WhOdoInvoice信息
        List<WhOdoInvoice> odoInvoiceList = whOdoInvoiceDao.findWhOdoInvoiceByOdoId(odoid, ouid);
        for (WhOdoInvoice whOdoInvoice : odoInvoiceList) {
            // 有数据插入odoInvoiceArchiv
            whOdoInvoice.setSysDate(sysDate);
            int oi = odoArchivDao.archivWhOdoInvoice(whOdoInvoice);
            count += oi;
            // 查找WhOdoInvoiceLine信息
            List<WhOdoInvoiceLine> odoInvoiceLineList = whOdoInvoiceLineDao.findWhOdoInvoiceLinesByOdoInvoiceId(whOdoInvoice.getId(), ouid);
            for (WhOdoInvoiceLine whOdoInvoiceLine : odoInvoiceLineList) {
                // 有数据插入odoInvoiceLineArchiv
                whOdoInvoiceLine.setSysDate(sysDate);
                int iol = odoArchivDao.archivWhOdoInvoiceLine(whOdoInvoiceLine);
                count += iol;
            }
        }
        return count;
    }

    /***
     * 归档OdoTransportMgmt
     * 
     * @param odoid
     * @param ouid
     * @param sysDate
     * @param count
     * @return
     */
    private int archivWhOdoTransportMgmt(Long odoid, Long ouid, String sysDate, int count) {
        // 查询whOdoTransportMgmt数据
        WhOdoTransportMgmt whOdoTransportMgmt = whOdoTransportMgmtDao.findTransportMgmtByOdoIdOuId(odoid, ouid);
        if (null != whOdoTransportMgmt) {
            // 有数据插入odoTransportMgmtArchiv信息
            whOdoTransportMgmt.setSysDate(sysDate);
            int otm = odoArchivDao.archivWhOdoTransportMgmt(whOdoTransportMgmt);
            count += otm;
        }
        return count;
    }

    /**
     * 归档OdoVas by odoid
     * 
     * @param odoid
     * @param ouid
     * @param sysDate
     * @param count
     * @return
     */
    private int archivWhOdoVas(Long odoid, Long ouid, String sysDate, int count) {
        // 查询odoVas数据by OdoLine
        List<WhOdoVas> odoVasList = whOdoVasDao.findOdoVasByOdoIdOdoLineIdType(odoid, null, null, ouid);
        for (WhOdoVas whOdoVas : odoVasList) {
            // 有数据插入WhOdoVasArchiv
            whOdoVas.setSysDate(sysDate);
            int ov = odoArchivDao.archivWhOdoVas(whOdoVas);
            count += ov;
        }
        return count;
    }

    /***
     * 归档Odo outBoundBox信息
     * 
     * @param odoid
     * @param ouid
     * @param sysDate
     * @param count
     * @return
     */
    private int archivWhOdoOutBoundBox(Long odoid, Long ouid, String sysDate, int count) {
        // 查询outBoundBox数据by odoid
        List<WhOutboundbox> whOutboundboxs = whOutboundboxDao.findWhOutboundboxByOdoId(odoid, ouid);
        for (WhOutboundbox whOutboundbox : whOutboundboxs) {
            // 有数据插入WhOutboundboxArchiv
            whOutboundbox.setSysDate(sysDate);
            int obb = odoArchivDao.archivWhOutboundbox(whOutboundbox);
            count += obb;
        }
        return count;
    }

    /***
     * 归档Odo outboundBoxLine+outboundBoxLindSn信息
     * 
     * @param odoid
     * @param ouid
     * @param sysDate
     * @param count
     * @return
     */
    private int archivWhOdoOutBoundBoxLine(Long odoid, Long ouid, String sysDate, int count) {
        // 查询outBoundboxLine数据
        List<WhOutboundboxLine> whOutboundboxLines = whOutboundboxLineDao.findWhOutboundboxLineByOdoId(odoid, ouid);
        for (WhOutboundboxLine whOutboundboxLine : whOutboundboxLines) {
            // 有数据插入WhOutboundboxLineArchiv
            whOutboundboxLine.setSysDate(sysDate);
            int obbl = odoArchivDao.archivWhOutboundboxLine(whOutboundboxLine);
            count += obbl;
            // 查询outboundboxLineSn数据
        }
        return count;
    }

    /**
     * 删除Odo信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int deleteOdo(Long odoid, Long ouid) {
        int count = 0;
        try {
            int odoVas = odoArchivDao.deleteOdoVas(odoid, ouid);
            count += odoVas;
            int odoTm = odoArchivDao.deleteOdoTransportMgmt(odoid, ouid);
            count += odoTm;
            int odoLineSn = odoArchivDao.deleteOdoLineSn(odoid, ouid);
            count += odoLineSn;
            int odoLineAttr = odoArchivDao.deleteOdoLineAttr(odoid, ouid);
            count += odoLineAttr;
            int odoInvoiceLine = odoArchivDao.deleteOdoInvoiceLine(odoid, ouid);
            count += odoInvoiceLine;
            int odoInvoice = odoArchivDao.deleteOdoInvoice(odoid, ouid);
            count += odoInvoice;
            int odoAttr = odoArchivDao.deleteOdoAttr(odoid, ouid);
            count += odoAttr;
            int obb = odoArchivDao.deleteOdoOutBoundBox(odoid, ouid);
            count += obb;
            int obbl = odoArchivDao.deleteOdoOutBoundBoxLine(odoid, ouid);
            count += obbl;
            int odoLine = odoArchivDao.deleteOdoLine(odoid, ouid);
            count += odoLine;
            int odoAddress = odoArchivDao.deleteOdoAddress(odoid, ouid);
            count += odoAddress;
            int odo = odoArchivDao.deleteOdo(odoid, ouid);
            count += odo;
        } catch (Exception e) {
            log.error("OdoArchivManagerImpl deleteOdo error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }
}
