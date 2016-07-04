package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckPoCode;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface PoManager extends BaseManager {

    ResponseMsg createPoAndLineToInfo(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm);

    ResponseMsg createPoAndLineToShare(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm);

    WhPo findWhPoByIdToInfo(Long id, Long ouId);

    WhPo findWhPoByIdToShard(Long id, Long ouId);

    WhPoCommand findWhPoCommandByIdToInfo(Long id, Long ouId);

    WhPoCommand findWhPoCommandByIdToShard(Long id, Long ouId);

    Pagination<WhPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);

    Pagination<WhPoCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    int editPoStatusToInfo(WhPoCommand whPo);

    int editPoStatusToShard(WhPoCommand whPo);

    void editPoToInfo(WhPo whPo);

    void editPoToShard(WhPo whPo);

    ResponseMsg insertPoWithOuId(PoCheckCommand poCheckCommand);

    List<WhPoCommand> findWhPoListByExtCodeToInfo(String poCode, List<Integer> status, List<Long> customerList,List<Long> storeList,Long ouid, Integer linenum);

    List<WhPoCommand> findWhPoListByExtCodeToShard(String poCode, List<Integer> status,List<Long> customerList,List<Long> storeList, Long ouid, Integer linenum);

    WhPo findWhAsnByIdToInfo(Long id, Long ouid);

    WhPo findWhAsnByIdToShard(Long id, Long ouid);

    void deletePoAndPoLineToInfo(List<WhPoCommand> whPoCommand);

    void deletePoAndPoLineToShard(List<WhPoCommand> whPoCommand);

    ResponseMsg updatePoStatusByAsn(WhAsn asn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm);

    ResponseMsg updatePoStatusByAsnBatch(WhAsnCommand asn, WhPo whPo, List<WhPoLine> whPoLines, ResponseMsg rm);

    void deleteCheckPoCodeToInfo(List<CheckPoCode> poCodeList, Long userId);

    void saveOrUpdateByVersionToInfo(WhPo o);

    void saveOrUpdateByVersionToShard(WhPo o);

    void cancelPoToInfo(List<WhPo> poList);

    void cancelPoToShard(List<WhPo> poList);

    void editPoAdnPoLineWhenDeleteAsnToInfo(WhPo whpo, List<WhPoLine> polineList);

    /**
     * 逻辑：poCode+ouId能唯一确定一条PO；poCode,ouId不能为空
     * 
     * @param poCode
     * @param ouId
     * @return
     */
    WhPo findWhPoByPoCodeOuIdToShard(String poCode, Long ouId);

    /**
     * 逻辑：poCode+ouId能唯一确定一条非取消状态的PO；poCode,ouId不能为空
     * 
     * @param poCode
     * @param ouId
     * @return
     */
    WhPo findWhPoByPoCodeOuIdToInfo(String poCode, Long ouId);

    void createWhPoToInfo(WhPo shardpo, List<WhPoLine> whpolineList);

    void createWhPoToShard(WhPo shardpo, List<WhPoLine> whpolineList);

    /**
     * info库中，poCode会对应多条Po;
     * 
     * @param poCode
     * @return
     */
    List<WhPo> findWhPoByPoCodeToInfo(String poCode);

    /**
     * 插入拆分的明细到INFO.WHPO/WHPOLINE
     * 
     * @param po
     * @param whPoLineList
     */
    void createSubPoWithLineToInfo(WhPo po, List<WhPoLine> whPoLineList);

    /**
     * 撤销拆分的明细数量
     * 
     * @param lineList
     */
    void revokeSubPoToInfo(List<WhPoLine> lineList);

    /**
     * 将创建子PO的临时数据推送到仓库
     * 
     * @param poCode
     * @param ouId
     * @param userId
     * @param infoPoLineList
     * @param infoPo
     */
    void saveSubPoToShard(String poCode, Long ouId, Long userId, WhPo infoPo, List<WhPoLine> infoPoLineList);

}
