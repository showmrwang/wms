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
import com.baozun.scm.primservice.whoperation.dao.archiv.PoAsnArchivDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.BiPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnSnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoSnDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoTransportMgmtDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhAsnRcvdLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhAsnRcvdSnLogDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnSn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoSn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhAsnRcvdSnLog;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;
import com.baozun.scm.primservice.whoperation.util.DateUtil;

/**
 * 入库单归档
 * 
 * @author bin.hu
 *
 */
@Service("poAsnArchivManager")
@Transactional
public class PoAsnArchivManagerImpl implements PoAsnArchivManager {

    protected static final Logger log = LoggerFactory.getLogger(PoAsnArchivManager.class);

    @Autowired
    private BiPoDao biPoDao;
    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private PoAsnArchivDao poAsnArchivDao;
    @Autowired
    private WhPoLineDao whPoLineDao;
    @Autowired
    private WhPoSnDao whPoSnDao;
    @Autowired
    private WhAsnDao whAsnDao;
    @Autowired
    private WhAsnLineDao whAsnLineDao;
    @Autowired
    private WhAsnSnDao whAsnSnDao;
    @Autowired
    private WhPoTransportMgmtDao whPoTransportMgmtDao;
    @Autowired
    private WhAsnTransportMgmtDao whAsnTransportMgmtDao;
    @Autowired
    private WhAsnRcvdLogDao whAsnRcvdLogDao;
    @Autowired
    private WhAsnRcvdSnLogDao whAsnRcvdSnLogDao;
    @Autowired
    private WhCartonDao whCartonDao;

    /** 备份集团/仓库whPo */
    private static final String WhPoInsert = "id,po_code,ext_code,ext_po_code,ou_id,customer_id,store_id,supplier_id,from_location,to_location,logistics_provider,po_type,ext_po_type,status,is_iqc,po_date,eta,delivery_time,"
            + "qty_planned,qty_rcvd,ctn_planned,ctn_rcvd,start_time,stop_time,inbound_time,is_wms,is_vmi,data_source,create_time,created_id,last_modify_time,modified_id,original_ext_odo_code,original_ec_order_code";
    /** 备份集团/仓库whPo */
    /** 备份集团/仓库whPoLine */
    private static final String WhPoLineInsert = "id,po_id,ou_id,linenum,ext_line_num,sku_id,qty_planned,overshipped,available_qty,"
            + "ctn_planned,qty_rcvd,ctn_rcvd,status,is_iqc,mfg_date,exp_date,valid_date,carton_no,batch_no,country_of_origin,inv_status,inv_attr1,"
            + "inv_attr2,inv_attr3,inv_attr4,inv_attr5,create_time,created_id,last_modify_time,modified_id,inv_type,valid_date_uom";
    /** 备份集团/仓库whPoLine */
    /** 备份集团/仓库whPoSn */
    private static final String WhPoSnInsert = "id,po_line_id,sku_id,sn,ou_id,create_time,created_id,last_modify_time,modified_id";
    /** 备份集团/仓库whPoSn */

    /** 备份集团BiPo */
    private static final String BiPoInsert = "id,po_code,ext_code,ext_po_code,customer_id,store_id,supplier_id,from_location,to_location,logistics_provider,po_type,ext_po_type,status,is_iqc,po_date,eta,delivery_time,"
            + "qty_planned,qty_rcvd,ctn_planned,ctn_rcvd,start_time,stop_time,inbound_time,is_wms,is_vmi,data_source,create_time,created_id,last_modify_time,modified_id,archiv_time,original_ext_odo_code,original_ec_order_code";
    private static final String BiPoSelect = "id,po_code,ext_code,ext_po_code,customer_id,store_id,supplier_id,from_location,to_location,logistics_provider,po_type,ext_po_type,status,is_iqc,po_date,eta,delivery_time,"
            + "qty_planned,qty_rcvd,ctn_planned,ctn_rcvd,start_time,stop_time,inbound_time,is_wms,is_vmi,data_source,create_time,created_id,last_modify_time,modified_id,now(),original_ext_odo_code,original_ec_order_code";
    /** 备份集团BiPo */
    /** 备份集团BiPoLine */
    private static final String BiPoLineInsert = "id,po_id,linenum,ext_line_num,sku_id,qty_planned,overshipped,available_qty,"
            + "ctn_planned,qty_rcvd,ctn_rcvd,status,is_iqc,mfg_date,exp_date,valid_date,carton_no,batch_no,country_of_origin,inv_status,inv_attr1,"
            + "inv_attr2,inv_attr3,inv_attr4,inv_attr5,create_time,created_id,last_modify_time,modified_id,inv_type,valid_date_uom";
    /** 备份集团BiPoLine */
    /** 备份集团BiPoSn */
    private static final String BiPoSnInsert = "id,po_line_id,sku_id,sn,create_time,created_id,last_modify_time,modified_id";
    /** 备份集团BiPoSn */

    /** 备份集团bi/wh_po_transport_mgmt poTransportMgmt */
    private static final String poTransportMgmt =
            "id,po_id,transport_service_provider,tracking_number,sender_target_name,sender_target_mobile_phone,sender_target_telephone,sender_target_country,sender_target_province,sender_target_city,sender_target_district,sender_target_villages_towns,sender_target_address,sender_target_email,sender_target_zip";

    private static final String T_BI_PO = "t_bi_po";
    private static final String T_BI_PO_LINE = "t_bi_po_line";
    private static final String T_BI_PO_SN = "t_bi_po_sn";
    private static final String T_BI_PO_TRANSPORT_MGMT = "t_bi_po_transport_mgmt";

    private static final String T_WH_PO = "t_wh_po";
    private static final String T_WH_PO_LINE = "t_wh_po_line";
    private static final String T_WH_PO_SN = "t_wh_po_sn";
    private static final String T_WH_PO_TRANSPORT_MGMT = "t_wh_po_transport_mgmt";


    /**
     * 备份集团下bipo信息
     * 
     * @param poid
     * @return
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public int archivBiPoByInfo(Long poid) {
        int count = 0;
        try {
            String sysDate = DateUtil.getSysDate();
            int po = poAsnArchivDao.archivBiPo(poid, BiPoInsert, BiPoSelect, sysDate);
            count += po;
            int poLine = poAsnArchivDao.archivBiPoLine(poid, BiPoLineInsert, sysDate);
            count += poLine;
            int poSn = poAsnArchivDao.archivBiPoSn(poid, BiPoSnInsert, sysDate);
            count += poSn;
            int poTm = poAsnArchivDao.archivBiPoTransportMgmt(poid, poTransportMgmt, sysDate);
            count += poTm;
        } catch (Exception e) {
            log.error("PoArchivManagerImpl archivWhPoByInfo error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }

    /***
     * 备份集团下whpo
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public int archivWhPoByInfo(Long poid) {
        int count = 0;
        try {
            String sysDate = DateUtil.getSysDate();
            int po = poAsnArchivDao.archivWhPo(poid, WhPoInsert, sysDate);
            count += po;
            int poLine = poAsnArchivDao.archivWhPoLine(poid, WhPoLineInsert, sysDate);
            count += poLine;
            int poSn = poAsnArchivDao.archivWhPoSn(poid, WhPoSnInsert, sysDate);
            count += poSn;
            int poTm = poAsnArchivDao.archivWhPoTransportMgmt(poid, poTransportMgmt, sysDate);
            count += poTm;
        } catch (Exception e) {
            log.error("PoArchivManagerImpl archivWhPoByInfo error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }

    /***
     * 删除集团下whpo
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public int deleteWhPoByInfo(Long poid) {
        int count = 0;
        try {
            int poSn = poAsnArchivDao.deletePoSn(poid, T_WH_PO_SN, T_WH_PO_LINE, null);
            count += poSn;
            int poLine = poAsnArchivDao.deletePoLine(poid, T_WH_PO_LINE, null);
            count += poLine;
            int poTm = poAsnArchivDao.deletePoTransportMgmt(poid, T_WH_PO_TRANSPORT_MGMT, null);
            count += poTm;
            int po = poAsnArchivDao.deletePo(poid, T_WH_PO, null);
            count += po;
        } catch (Exception e) {
            log.error("PoArchivManagerImpl deleteWhPoByInfo error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }

    /***
     * 删除集团下bipo
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public int deleteBiPoByInfo(Long poid) {
        int count = 0;
        try {
            int poSn = poAsnArchivDao.deletePoSn(poid, T_BI_PO_SN, T_BI_PO_LINE, null);
            count += poSn;
            int poLine = poAsnArchivDao.deletePoLine(poid, T_BI_PO_LINE, null);
            count += poLine;
            int poTm = poAsnArchivDao.deletePoTransportMgmt(poid, T_BI_PO_TRANSPORT_MGMT, null);
            count += poTm;
            int po = poAsnArchivDao.deletePo(poid, T_BI_PO, null);
            count += po;
        } catch (Exception e) {
            log.error("PoArchivManagerImpl deleteBiPoByInfo error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }

    /***
     * 备份仓库下whpo信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int archivWhPoByShard(Long poid, Long ouid) {
        int count = 0;
        try {
            String sysDate = DateUtil.getSysDate();
            WhPo whpo = whPoDao.findWhPoById(poid, ouid);
            if (null == whpo) {
                log.warn("PoArchivManagerImpl archivWhPoByShard warn whpo is null poid:" + poid + " ouid:" + ouid);
                return count;
            }
            whpo.setSysDate(sysDate);
            whpo.setArchivTime(new Date());
            // 插入whPoArchiv
            int po = poAsnArchivDao.archivWhPoByShard(whpo);
            count += po;
            WhPoTransportMgmt ptm = whPoTransportMgmtDao.findWhPoTransportMgmtByPoId(poid, ouid);
            if (null != ptm) {
                // 不为空 插入WhPoTransportMgmtArchiv
                ptm.setSysDate(sysDate);
                int poTm = poAsnArchivDao.archivWhPoTransportMgmtByShard(ptm);
                count += poTm;
            }
            List<WhPoLine> poLineList = whPoLineDao.findWhPoLineByPoIdOuIdUuid(poid, ouid, null);
            for (WhPoLine l : poLineList) {
                l.setSysDate(sysDate);
                // 插入whPoLineArchiv
                int poLine = poAsnArchivDao.archivWhPoLineByShard(l);
                count += poLine;
                List<WhPoSn> poSnList = whPoSnDao.findWhPoSnByPoLineId(l.getId(), ouid);
                for (WhPoSn sn : poSnList) {
                    // 插入whPoSnArchiv
                    sn.setSysDate(sysDate);
                    int poSn = poAsnArchivDao.archivWhPoSnByShard(sn);
                    count += poSn;
                }
            }
        } catch (Exception e) {
            log.error("PoArchivManagerImpl archivWhPoByShard error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }

    /***
     * 删除仓库下whpo
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int deleteBiPoByShard(Long poid, Long ouid) {
        int count = 0;
        try {
            int poSn = poAsnArchivDao.deletePoSn(poid, T_WH_PO_SN, T_WH_PO_LINE, ouid);
            count += poSn;
            int poLine = poAsnArchivDao.deletePoLine(poid, T_WH_PO_LINE, ouid);
            count += poLine;
            int poTm = poAsnArchivDao.deletePoTransportMgmt(poid, T_WH_PO_TRANSPORT_MGMT, ouid);
            count += poTm;
            int po = poAsnArchivDao.deletePo(poid, T_WH_PO, ouid);
            count += po;
        } catch (Exception e) {
            log.error("PoArchivManagerImpl deleteBiPoByShard error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }

    /**
     * 备份仓库下whAsn
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int archivWhAsn(Long asnid, Long ouid) {
        int count = 0;
        try {
            String sysDate = DateUtil.getSysDate();
            WhAsn whAsn = whAsnDao.findWhAsnById(asnid, ouid);
            if (null == whAsn) {
                log.warn("PoArchivManagerImpl archivWhAsn warn WhAsn is null asnid:" + asnid + " ouid:" + ouid);
                return count;
            }
            whAsn.setSysDate(sysDate);
            whAsn.setArchivTime(new Date());
            // 插入whasnArchiv
            int asn = poAsnArchivDao.archivWhAsnByShard(whAsn);
            count += asn;
            WhAsnTransportMgmt atm = whAsnTransportMgmtDao.findWhAsnTransportMgmtByAsnId(asnid, ouid);
            if (null != atm) {
                // 不为空 插入WhAsnTransportMgmtArchiv
                atm.setSysDate(sysDate);
                int asnTm = poAsnArchivDao.archivWhAsnTransportMgmtByShard(atm);
                count += asnTm;
            }
            List<WhAsnLine> asnLineList = whAsnLineDao.findWhAsnLineByAsnIdOuId(asnid, ouid);
            for (WhAsnLine asnLine : asnLineList) {
                asnLine.setSysDate(sysDate);
                // 插入whasnLineArchiv
                int al = poAsnArchivDao.archivWhAsnLineByShard(asnLine);
                count += al;
                List<WhAsnSn> asnSnList = whAsnSnDao.findWhAsnSnByAsnLineId(asnLine.getId(), ouid);
                for (WhAsnSn asnsn : asnSnList) {
                    // 插入whAsnSnArchiv
                    asnsn.setSysDate(sysDate);
                    int sn = poAsnArchivDao.archivWhAsnSnByShard(asnsn);
                    count += sn;
                }
            }
            List<WhAsnRcvdLog> asnRcvdLogs = whAsnRcvdLogDao.findWhAsnRcvdLogByAsnId(asnid, ouid);
            for (WhAsnRcvdLog whAsnRcvdLog : asnRcvdLogs) {
                whAsnRcvdLog.setSysDate(sysDate);
                int rcvdLog = poAsnArchivDao.archivWhAsnRcvdLogByShard(whAsnRcvdLog);
                count += rcvdLog;
                List<WhAsnRcvdSnLog> asnRcvdSnLogs = whAsnRcvdSnLogDao.findWhAsnRcvdSnLogByAsnRcvdId(whAsnRcvdLog.getId(), ouid);
                for (WhAsnRcvdSnLog whAsnRcvdSnLog : asnRcvdSnLogs) {
                    whAsnRcvdSnLog.setSysDate(sysDate);
                    int rcvdSnLog = poAsnArchivDao.archivWhAsnRcvdSnLogByShard(whAsnRcvdSnLog);
                    count += rcvdSnLog;
                }
            }
            List<WhCarton> whCartons = whCartonDao.findWhCartonListByAsn(asnid, ouid);
            for (WhCarton whCarton : whCartons) {
                whCarton.setSysDate(sysDate);
                int c = poAsnArchivDao.archivWhCartonByShard(whCarton);
                count += c;
            }

        } catch (Exception e) {
            log.error("PoArchivManagerImpl archivWhAsn error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }

    /**
     * 删除仓库下whasn信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public int deleteWhAsnByShard(Long asnid, Long ouid) {
        int count = 0;
        try {
            int carton = poAsnArchivDao.deleteWhCarton(asnid, ouid);
            count += carton;
            int asnRvcdSn = poAsnArchivDao.deleteAsnRcvdSnLog(asnid, ouid);
            count += asnRvcdSn;
            int asnRvcd = poAsnArchivDao.deleteAsnRcvdLog(asnid, ouid);
            count += asnRvcd;
            int asnSn = poAsnArchivDao.deleteAsnSn(asnid, ouid);
            count += asnSn;
            int asnLine = poAsnArchivDao.deleteAsnLine(asnid, ouid);
            count += asnLine;
            int asnTm = poAsnArchivDao.deleteAsnTransportMgmt(asnid, ouid);
            count += asnTm;
            int asn = poAsnArchivDao.deleteAsn(asnid, ouid);
            count += asn;
        } catch (Exception e) {
            log.error("PoArchivManagerImpl deleteWhAsnByShard error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public List<Long> findBiPoIdListForArchiv() {
        return poAsnArchivDao.findBiPoIdListForArchiv();
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<Long> findWhPoIdListForArchiv(Long ouId) {
        return poAsnArchivDao.findWhPoIdListForArchiv(ouId);
    }

    /**
     * 归档和删除Info库下BiPo和WhPo逻辑
     * 
     * kai.zhu
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public void archivBiPoById(Long biPoId) {
        BiPo biPo = biPoDao.findById(biPoId);
        List<WhPo> whPolist = this.whPoDao.findWhPoByExtCodeStoreIdToInfo(biPo.getExtCode(), biPo.getStoreId());
        if (null != whPolist) {
            for (WhPo whPo : whPolist) {
                // 归档WhPo
                int archivCount = this.archivWhPoByInfo(whPo.getId());
                // 删除WhPo
                int deleteCount = this.deleteWhPoByInfo(whPo.getId());
                if (archivCount != deleteCount) {
                    log.error("archivCount != archivCount, WhPoId:" + whPo.getId());
                    throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
                }
            }
        }
        // 归档BiPo
        int archivCount = this.archivBiPoByInfo(biPoId);
        // 删除BiPo
        int deleteCount = this.deleteBiPoByInfo(biPoId);
        if (archivCount != deleteCount) {
            log.error("archivCount != archivCount, BiPoId:" + biPoId);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
    }

    /**
     * 归档和删除Shard库下WhPo和WhAsn逻辑
     * 
     * kai.zhu
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void archivWhPoById(Long whPoId, Long ouId) {
        List<Long> whAsnIdList = whAsnDao.findWhAsnIdListByPoIdOuId(whPoId, ouId);
        if (null != whAsnIdList) {
            for (Long whAsnId : whAsnIdList) {
                // 归档WhAsn
                int archivCount = this.archivWhAsn(whAsnId, ouId);
                // 删除WhAsn
                int deleteCount = this.deleteWhAsnByShard(whAsnId, ouId);
                if (archivCount != deleteCount) {
                    log.error("archivCount != archivCount, WhAsnId:" + whAsnId);
                    throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
                }
            }
        }
        // 归档WhPo
        int archivCount = this.archivWhPoByShard(whPoId, ouId);
        // 删除WhPo
        int deleteCount = this.deleteBiPoByShard(whPoId, ouId);
        if (archivCount != deleteCount) {
            log.error("archivCount != archivCount, WhPoId:" + whPoId);
            throw new BusinessException(ErrorCodes.SYSTEM_EXCEPTION);
        }
    }

}
