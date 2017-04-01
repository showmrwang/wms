/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 * 
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 * 
 */
package com.baozun.scm.primservice.whoperation.dao.archiv;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import lark.orm.dao.supports.BaseDao;

import com.baozun.scm.primservice.whoperation.command.collect.WhOdoArchivLineIndexCommand;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivIndex;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivLineIndex;
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
import com.baozun.scm.primservice.whoperation.model.warehouse.WhOutboundboxLineSn;



public interface OdoArchivDao extends BaseDao<WhOdo, Long> {

    /**
     * 归档odo
     * 
     * @param whOdo
     * @return
     */
    int archivWhOdo(WhOdo whOdo);

    /**
     * 归档odoLine
     * 
     * @param whOdoLine
     * @return
     */
    int archivWhOdoLine(WhOdoLine whOdoLine);

    /***
     * 归档odoLineSn
     * 
     * @param whOdoLineSn
     * @return
     */
    int archivWhOdoLineSn(WhOdoLineSn whOdoLineSn);

    /***
     * 归档odoAddress
     * 
     * @param whOdoAddress
     * @return
     */
    int archivWhOdoAddress(WhOdoAddress whOdoAddress);

    /**
     * 归档odoAttr
     * 
     * @param whOdoAttr
     * @return
     */
    int archivWhOdoAttr(WhOdoAttr whOdoAttr);

    /***
     * 归档whOdoInvoice
     * 
     * @param whOdoInvoice
     * @return
     */
    int archivWhOdoInvoice(WhOdoInvoice whOdoInvoice);

    /**
     * 归档whOdoInvoiceLine
     * 
     * @param whOdoInvoiceLine
     * @return
     */
    int archivWhOdoInvoiceLine(WhOdoInvoiceLine whOdoInvoiceLine);

    /***
     * 归档whOdoLineAttr
     * 
     * @param whOdoLineAttr
     * @return
     */
    int archivWhOdoLineAttr(WhOdoLineAttr whOdoLineAttr);

    /**
     * 归档whOdoTransportMgmt
     * 
     * @param whOdoTransportMgmt
     * @return
     */
    int archivWhOdoTransportMgmt(WhOdoTransportMgmt whOdoTransportMgmt);

    /**
     * 归档whOdoVas
     * 
     * @param whOdoVas
     * @return
     */
    int archivWhOdoVas(WhOdoVas whOdoVas);

    /***
     * 归档WhOutboundbox
     * 
     * @param WhOutboundbox
     * @return
     */
    int archivWhOutboundbox(WhOutboundbox whOutboundbox);

    /***
     * 归档whOutboundboxLine
     * 
     * @param whOutboundboxLine
     * @return
     */
    int archivWhOutboundboxLine(WhOutboundboxLine whOutboundboxLine);

    /***
     * 归档whOutboundboxLineSn
     * 
     * @param whOutboundboxLineSn
     * @return
     */
    int archivWhOutboundboxLineSn(WhOutboundboxLineSn whOutboundboxLineSn);

    /**
     * 删除WhOdo
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdo(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /**
     * 删除WhOdoLine
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoLine(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /**
     * 删除WhOdoLineSn
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoLineSn(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /***
     * 删除WhOdoAttr
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoAttr(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /***
     * 删除WhOdoLineAttr
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoLineAttr(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /**
     * 删除WhOdoInvoice
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoInvoice(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /**
     * 删除WhOdoInvoiceLine
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoInvoiceLine(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /**
     * 删除WhOdoAddress
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoAddress(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /**
     * 删除WhOdoVas
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoVas(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /**
     * 删除WhOdoTransportMgmt
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoTransportMgmt(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /**
     * 删除WhOutBoundBox
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoOutBoundBox(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /***
     * 删除WhOutBoundBoxLine
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoOutBoundBoxLine(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /**
     * 删除WhOutBoundBoxLineSn
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    int deleteOdoOutBoundBoxLineSn(@Param("odoid") Long odoid, @Param("ouid") Long ouid);

    /**
     * 保存出库单索引数据(仓库)
     * 
     * @param whOdoArchivIndex
     * @return
     */
    int saveOdoArchivIndex(WhOdoArchivIndex whOdoArchivIndex);

    /**
     * 保存出库单明细索引数据(仓库)
     * 
     * @return
     */
    int saveOdoArchivLineIndex(WhOdoArchivLineIndex whOdoArchivLineIndex);

    /**
     * 查询归档的出库单明细数据
     * 
     * @author kai.zhu
     * @version 2017年3月30日
     * @return
     */
    List<WhOdoArchivLineIndexCommand> findWhOutboundboxLineArchivByOdoId(@Param("odoId") Long odoId, @Param("ouId") Long ouId, @Param("sysDate") String sysDate);

    /**
     * 查找出库箱明细Sn信息
     * 
     * @author kai.zhu
     * @version 2017年3月31日
     */
    List<WhOutboundboxLineSn> findWhOutboundboxSnLineArchivByOutBoundLineId(@Param("boxLineId") Long boxLineId, @Param("ouId") Long ouId, @Param("sysDate") String sysDate);

}
