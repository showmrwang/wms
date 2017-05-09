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

import java.util.List;

import org.apache.ibatis.annotations.Param;

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

public interface PoAsnArchivDao extends BaseDao<WhPo, Long> {

    /***
     * 备份集团BiPo信息
     * 
     * @param poid
     * @param sysDate
     * @return
     */
    int archivBiPo(@Param("poid") Long poid, @Param("biPoInsert") String biPoInsert, @Param("biPoSelect") String biPoSelect, @Param("sysDate") String sysDate);

    /***
     * 备份集团BiPoLine信息
     * 
     * @param poid
     * @param sysDate
     * @return
     */
    int archivBiPoLine(@Param("poid") Long poid, @Param("biPoLineInsert") String biPoLineInsert, @Param("sysDate") String sysDate);

    /***
     * 备份集团BiPoSn信息
     * 
     * @param poid
     * @param sysDate
     * @return
     */
    int archivBiPoSn(@Param("poid") Long poid, @Param("biPoSnInsert") String biPoSnInsert, @Param("sysDate") String sysDate);

    /**
     * 备份集团BiPoTransportMgmt信息
     */
    int archivBiPoTransportMgmt(@Param("poid") Long poid, @Param("poTransportMgmt") String poTransportMgmt, @Param("sysDate") String sysDate);

    /***
     * 备份集团whPo信息
     * 
     * @param poid
     * @param sysDate
     * @return
     */
    int archivWhPo(@Param("poid") Long poid, @Param("whPoInsert") String whPoInsert, @Param("sysDate") String sysDate);

    /***
     * 备份集团whPoLine信息
     * 
     * @param poid
     * @param sysDate
     * @return
     */
    int archivWhPoLine(@Param("poid") Long poid, @Param("whPoLineInsert") String whPoLineInsert, @Param("sysDate") String sysDate);

    /***
     * 备份集团whPoSn信息
     * 
     * @param poid
     * @param sysDate
     * @return
     */
    int archivWhPoSn(@Param("poid") Long poid, @Param("whPoSnInsert") String whPoSnInsert, @Param("sysDate") String sysDate);

    /**
     * 备份集团WhPoTransportMgmt信息
     */
    int archivWhPoTransportMgmt(@Param("poid") Long poid, @Param("poTransportMgmt") String poTransportMgmt, @Param("sysDate") String sysDate);

    /**
     * 删除集团/仓库bi/whpo信息
     * 
     * @param poid
     * @return
     */
    int deletePo(@Param("poid") Long poid, @Param("tableName") String tableName, @Param("ouid") Long ouid);

    /**
     * 删除集团/仓库bi/whpoLine信息
     * 
     * @param poid
     * @return
     */
    int deletePoLine(@Param("poid") Long poid, @Param("tableName") String tableName, @Param("ouid") Long ouid);

    /**
     * 删除集团/仓库bi/whpoSn信息
     * 
     * @param poid
     * @return
     */
    int deletePoSn(@Param("poid") Long poid, @Param("tableName") String tableName, @Param("lineTableName") String lineTableName, @Param("ouid") Long ouid);

    /***
     * 删除集团/仓库bi/whPotransportMgmt信息
     * 
     * @param poid
     * @param tableName
     * @param ouid
     * @return
     */
    int deletePoTransportMgmt(@Param("poid") Long poid, @Param("tableName") String tableName, @Param("ouid") Long ouid);

    /***
     * 备份仓库whPo
     * 
     * @param whPo
     * @return
     */
    int archivWhPoByShard(WhPo whPo);

    /***
     * 备份仓库whPoLine
     * 
     * @param whPoLine
     * @return
     */
    int archivWhPoLineByShard(WhPoLine whPoLine);

    /**
     * 备份仓库whPoSn
     * 
     * @param whPoSn
     * @return
     */
    int archivWhPoSnByShard(WhPoSn whPoSn);

    /**
     * 备份仓库下WhPoTransportMgmt
     * 
     * @param whPoTransportMgmt
     * @return
     */
    int archivWhPoTransportMgmtByShard(WhPoTransportMgmt whPoTransportMgmt);


    /***
     * 备份仓库whAsn
     * 
     * @param whAsn
     * @return
     */
    int archivWhAsnByShard(WhAsn whAsn);

    /***
     * 备份仓库whAsnLine
     * 
     * @param whAsnLine
     * @return
     */
    int archivWhAsnLineByShard(WhAsnLine whAsnLine);

    /**
     * 备份仓库whAsnSn
     * 
     * @param whAsnSn
     * @return
     */
    int archivWhAsnSnByShard(WhAsnSn whAsnSn);

    /**
     * 备份仓库下WhAsnTransportMgmt
     * 
     * @param whAsnTransportMgmt
     * @return
     */
    int archivWhAsnTransportMgmtByShard(WhAsnTransportMgmt whAsnTransportMgmt);

    /**
     * 备份仓库下WhAsnRcvdLog
     * 
     * @param WhAsnRcvdLog
     * @return
     */
    int archivWhAsnRcvdLogByShard(WhAsnRcvdLog whAsnRcvdLog);

    /**
     * 备份仓库下WhAsnRcvdSnLog
     * 
     * @param WhAsnRcvdSnLog
     * @return
     */
    int archivWhAsnRcvdSnLogByShard(WhAsnRcvdSnLog whAsnRcvdSnLog);

    /**
     * 删除仓库whasn信息
     * 
     * @param asnid
     * @return
     */
    int deleteAsn(@Param("asnid") Long asnid, @Param("ouid") Long ouid);

    /**
     * 删除仓库whpoLine信息
     * 
     * @param asnid
     * @return
     */
    int deleteAsnLine(@Param("asnid") Long asnid, @Param("ouid") Long ouid);

    /**
     * 删除仓库whpoSn信息
     * 
     * @param asnid
     * @return
     */
    int deleteAsnSn(@Param("asnid") Long asnid, @Param("ouid") Long ouid);

    /**
     * 删除仓库whAsnTransportMgmt
     * 
     * @param asnid
     * @param ouid
     * @return
     */
    int deleteAsnTransportMgmt(@Param("asnid") Long asnid, @Param("ouid") Long ouid);

    /**
     * 删除仓库whAsnRcvdLog
     * 
     * @param asnid
     * @param ouid
     * @return
     */
    int deleteAsnRcvdLog(@Param("asnid") Long asnid, @Param("ouid") Long ouid);
    
    /**
     * 删除仓库whAsnRcvdSnLog
     * 
     * @param asnid
     * @param ouid
     * @return
     */
    int deleteAsnRcvdSnLog(@Param("asnid") Long asnid, @Param("ouid") Long ouid);
    
    /**
     * 查找已完成, 一个月之前的BiPo单据
     * @author kai.zhu
     * @version 2017年5月8日
     */
    List<Long> findBiPoIdListForArchiv();
    
    /**
     * 查找已完成, 一个月之前的WhPo单据
     * @author kai.zhu
     * @version 2017年5月8日
     */
    List<Long> findWhPoIdListForArchiv(@Param("ouId") Long ouId);

}
