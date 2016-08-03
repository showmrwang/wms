package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;

public interface CreatePoAsnManagerProxy extends BaseManager {

    /**
     * 创建BIPO单 业务方法；【业务方法；接口方法】
     * 
     * @param command
     * @return
     */
    ResponseMsg createPoNew(WhPoCommand command);
    
    /**
     * 创建POLINE明细【 业务方法】
     * 
     * @param biPoLine
     * @return
     */
    void createPoLineSingleNew(BaseCommand poLine);

    /**
     * 保存PO单明细 【业务方法】
     * 
     * @param command
     * @return
     */
    void createPoLineBatchNew(BaseCommand command);

    /**
     * 创建子PO流程：将数据写入INFO——WHPO中，并用UUID标识为同一批次的临时数据 【业务方法】
     * 
     * @param command
     * @return
     */
    void createSubPoToInfo(BiPoCommand command);

    /**
     * 创建子PO流程：撤销已写入INFO——WHPO中选定数据，并用UUID标识为同一批次的临时数据【业务方法】
     * 
     * @param command
     * @return
     */
    void revokeSubPoToInfo(WhPoCommand command);

    /**
     * 创建子PO流程：将数据写入仓库。并修改INFO>WHPO/WHPOLINE数据【业务方法】
     * 
     * @param command
     * @return
     */
    void createSubPoToShard(WhPoCommand command);

    /**
     * 一键创建ASN；添加ASN明细【接口方法；业务方法】
     * 
     * @param asn
     * @return
     */
    ResponseMsg createAsnBatch(WhAsnCommand asn);

    /**
     * 创建子po流程：清除掉所有临时数据【业务方法】
     * 
     * @param command
     */
    void closeSubPoToInfo(WhPoCommand command);

    /**
     * 创建ASN分支一：创建带uuid的asn【业务方法】
     * 
     * @param asn
     * @return
     */
    WhAsn createAsnWithUuid(WhPoCommand command);

    /**
     * 创建ASN分支一：撤销带uuid的asn【业务方法】
     * 
     * @param asn
     * @return
     */
    void revokeAsnWithUuid(WhAsnCommand command);

    /**
     * 创建ASN分支一：更新带UUID的ASN【业务方法】
     * 
     * @param command
     * @return
     */
    WhAsn updateAsnWithUuid(WhPoCommand command);

    /**
     * 创建ASN分支一：保存带UUID的asn数据【业务方法】
     * 
     * @param command
     * @return
     */
    void saveTempAsnWithUuid(WhPoCommand command);

    /**
     * 
     * @param command
     */
    void finishCreatingAsn(WhPoCommand command);

    /**
     * 创建ASN
     * 
     * @param command
     * @return
     */
    ResponseMsg createAsn(WhAsnCommand command);

    /**
     * [业务方法]生成ASN的EXTCODE
     * 
     * @return
     */
    String getAsnExtCode();

}
