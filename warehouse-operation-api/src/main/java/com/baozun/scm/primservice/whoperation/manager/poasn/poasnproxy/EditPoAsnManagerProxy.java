package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.Date;
import java.util.List;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.BiPoLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface EditPoAsnManagerProxy extends BaseManager {

    /**
     * 取消PO单操作
     * 
     * @param whPoCommand
     * @return
     */
    ResponseMsg cancelPo(Long poId, Long ouId, Boolean isPoCancel, List<Integer> extlineNumList, Long userId);

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
    ResponseMsg deletePoAndPoLine(WhPoCommand command);

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

    /**
     * BIPO明细页面 编辑单条明细
     * 
     * @param biPoLine
     * @return
     */
    ResponseMsg editBiPoLine(BiPoLine biPoLine);

    /**
     * 删除BiPO，并关联删除明细； 逻辑：当处于新建状态下，才允许删除
     * 
     * @param poCommand
     * @return
     */
    ResponseMsg deleteBiPo(BiPoCommand poCommand);

    /**
     * 取消BIPO，并置明细状态为取消;逻辑：1.当处于新建状态下时，允许取消；2.当已分配到仓库，并且每个仓库的都是取消的时候，才允许取消
     * 
     * @param command
     * @return
     */
    ResponseMsg cancel(Long poId, Boolean isPoCancel, List<BiPoLine> biPoLineList, Long userId, String logId);


    /**
     * 根据PoId,uuid删除BiPo
     * 
     * @param poId
     * @param uuid
     */
    void deleteBiPoLineByPoIdAndUuid(BiPoLineCommand command);

    /**
     * 编辑BIPO单表头；逻辑：未分配仓库之前可以编辑
     * 
     * @param updatePo
     * @return
     */
    ResponseMsg editBiPo(BiPo updatePo);

    /**
     * 缓存锁使用
     * 
     * @param id
     * @param ouid
     * @param lastModifyTime
     * @return
     */
    int updateByVersionForLock(Long id, Long ouid, Date lastModifyTime);

    /**
     * 释放缓存锁使用
     * 
     * @param id
     * @param ouid
     * @param lastModifyTime
     * @return
     */
    int updateByVersionForUnLock(Long id, Long ouid);

    /**
     * 业务方法： 更新仓库PO单头信息
     * 
     * @param command
     * @return
     */
    ResponseMsg editPoNew(WhPoCommand command);

    /**
     * 业务方法：删除BIPO单明细
     * 
     * @param command
     */
    void deleteBiPoLines(BiPoLineCommand command);


}
