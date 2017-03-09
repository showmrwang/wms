package com.baozun.scm.primservice.whoperation.manager.archiv;

import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface PoAsnArchivManager extends BaseManager {

    /**
     * 备份集团下bipo
     * 
     * @param poid
     * @return
     */
    int archivBiPoByInfo(Long poid);

    /**
     * 备份集团下whpo信息
     * 
     * @param poid
     * @return
     */
    int archivWhPoByInfo(Long poid);

    /***
     * 删除集团下whpo
     * 
     * @param poid
     * @return
     */
    int deleteWhPoByInfo(Long poid);

    /***
     * 删除集团下bipo
     * 
     * @param poid
     * @return
     */
    int deleteBiPoByInfo(Long poid);

    /**
     * 备份仓库下whpo
     * 
     * @param poid
     * @param userid
     * @return
     */
    int archivWhPoByShard(Long poid, Long ouid);

    /***
     * 删除仓库下whpo
     * 
     * @param poid
     * @param ouid
     * @return
     */
    int deleteBiPoByShard(Long poid, Long ouid);


    /**
     * 备份仓库下whasn
     * 
     * @param asnid
     * @param ouid
     * @return
     */
    int archivWhAsn(Long asnid, Long ouid);

    /***
     * 删除仓库下whasn
     * 
     * @param asnid
     * @param ouid
     * @return
     */
    int deleteWhAsnByShard(Long asnid, Long ouid);
}
