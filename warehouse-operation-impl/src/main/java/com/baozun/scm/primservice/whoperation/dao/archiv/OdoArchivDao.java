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

import lark.orm.dao.supports.BaseDao;

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

}
