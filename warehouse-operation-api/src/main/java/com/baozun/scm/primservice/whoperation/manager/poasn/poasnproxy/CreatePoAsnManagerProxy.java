package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;

public interface CreatePoAsnManagerProxy extends BaseManager {

    /**
     * 创建PO单
     * 
     * @deprecated
     * @param po
     * @return
     */
    ResponseMsg createPo(WhPoCommand po);

    /**
     * 创建PO单
     * 
     * @param command
     * @return
     */
    ResponseMsg createPoNew(WhPoCommand command);
    /**
     * 创建ASN
     * 
     * @param asn
     * @return
     */
    ResponseMsg createAsn(WhAsnCommand asn);

    /**
     * 创建POLINE 这个方法因为逻辑更改已不再使用
     * @deprecated
     * @param whPoLine
     * @return
     */
    ResponseMsg createPoLineSingle(WhPoLineCommand whPoLine);

    /**
     * 创建POLINE明细
     * 
     * @param biPoLine
     * @return
     */
    ResponseMsg createPoLineSingleNew(BaseCommand poLine);

    /**
     * 将POLINE明细批量保存
     * 
     * @param whPoLine
     * @return
     */
    ResponseMsg createPoLineBatch(WhPoLineCommand whPoLine);

    /**
     * 一键创建ASN；添加ASN明细
     * 
     * @param asn
     * @return
     */
    ResponseMsg createAsnBatch(WhAsnCommand asn);

    ResponseMsg createPoLineBatchNew(BaseCommand command);

    /**
     * 创建子Po
     * 
     * @param command
     * @return
     */
    ResponseMsg createSubPo(BiPoCommand command);

}
