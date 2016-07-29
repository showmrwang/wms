package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
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

    void editPoToInfo(WhPo whPo);

    void editPoToShard(WhPo whPo);

    List<WhPoCommand> findWhPoListByExtCodeToInfo(String poCode, List<Integer> status, List<Long> customerList,List<Long> storeList,Long ouid, Integer linenum);

    List<WhPoCommand> findWhPoListByExtCodeToShard(String poCode, List<Integer> status,List<Long> customerList,List<Long> storeList, Long ouid, Integer linenum);

    void deletePoAndPoLineToInfo(WhPo po, Long userId);

    void deletePoAndPoLineToShard(WhPo po, Long userId);

    ResponseMsg updatePoStatusByAsn(WhAsn asn, List<WhAsnLineCommand> asnLineList, WhPo whPo, Map<Long, WhPoLine> poLineMap, ResponseMsg rm);

    ResponseMsg updatePoStatusByAsnBatch(WhAsnCommand asn, WhPo whPo, List<WhPoLine> whPoLines, ResponseMsg rm);

    void saveOrUpdateByVersionToInfo(WhPo o);

    void saveOrUpdateByVersionToShard(WhPo o);

    void cancelPoToInfo(WhPo updateInfoPo, Long userId);

    void cancelPoToShard(WhPo updateShardPo, Long userId);

    void editPoAdnPoLineWhenDeleteAsnToInfo(WhPo whpo, List<WhPoLine> polineList);

    /**
     * 逻辑：extCode+storeId+ouId能唯一确定一条PO；
     * 
     * @param extCode @required
     * @param storeId @required
     * @param ouId @required
     * @return
     */
    WhPo findWhPoByExtCodeStoreIdOuIdToShard(String extCode, Long storeId, Long ouId);

    /**
     * 逻辑：extCode+storeId+ouId能唯一确定一条PO；
     * 
     * @param extCode @required
     * @param storeId @required
     * @param ouId @required
     * @return
     */
    WhPo findWhPoByExtCodeStoreIdOuIdToInfo(String extCode, Long storeId, Long ouId);

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
     * 将创建子PO的临时数据推送到仓库 @mender yimin.lu 2016/7/27
     * 
     * @param extCode
     * @param storeId
     * @param ouId
     * @param userId
     * @param poCode
     * @param infoPo
     * @param infoPoLineList
     */
    void saveSubPoToShard(String extCode, Long storeId, Long ouId, Long userId, String poCode, WhPo infoPo, List<WhPoLine> infoPoLineList);

}
