package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.List;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface EditPoAsnManagerProxy extends BaseManager {

    /**
     * 根据ASN的ID,OUID,查找ASN并修改STATUS
     * 
     * @param whAsnCommand
     * @return
     */
    ResponseMsg editAsnStatus(WhAsnCommand whAsnCommand);

    /**
     * 取消PO单操作
     * 
     * @param whPoCommand
     * @return
     */
    ResponseMsg cancelPo(WhPoCommand whPoCommand);

    /**
     * 修改PO单表头信息
     * 
     * @param po
     * @return
     */
    ResponseMsg editPo(WhPo po);

    /**
     * 根据POLINE的POID,OUID,UUID删除PO单明细
     * 
     * @param whPoLine
     */
    void deletePoLineByUuid(WhPoLineCommand whPoLine);

    /**
     * 修改POLINE
     * 
     * @param whPoLine
     * @return
     */
    ResponseMsg editPoLine(WhPoLine whPoLine);

    /**
     * 修改ASNLINE
     * 
     * @param whAsnLineCommand
     * @return
     */
    ResponseMsg editAsnLine(WhAsnLineCommand whAsnLineCommand);

    /**
     * 删除POLINE操作
     * 
     * @param command
     */
    void deletePoLines(WhPoLineCommand command);

    /**
     * 编辑ASN表头信息
     * 
     * @param asn
     * @return
     */
    ResponseMsg editAsn(WhAsn asn);

    /**
     * 删除PO单操作
     * 
     * @param whPoCommand
     * @return
     */
    ResponseMsg deletePoAndPoLine(List<WhPoCommand> whPoCommand);

    /**
     * 审核PO单操作
     * 
     * @param poCommand
     */
    void auditPo(WhPoCommand poCommand);

    /**
     * 审核ASN单操作
     * 
     * @param asnCommand
     * @return
     */
    ResponseMsg auditAsn(WhAsnCommand asnCommand);

    /**
     * 删除ASN操作（不可批量）
     * 
     * @param WhAsnCommand
     * @return
     */
    ResponseMsg deleteAsnAndAsnLine(WhAsnCommand WhAsnCommand);

    /**
     * 删除ASN明细（可批量）
     * 
     * @param command
     * @return
     */
    ResponseMsg deleteAsnLines(WhAsnLineCommand command);
}
