package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

public interface PoManager extends BaseManager {

    /**
     * [通用方法]根据ID，OUID查找INFO.WHPO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhPo findWhPoByIdToInfo(Long id, Long ouId);

    /**
     * [通用方法]根据ID,OUID查找SHARD.WHPO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhPo findWhPoByIdToShard(Long id, Long ouId);

    /**
     * [通用方法]根据ID，OUID查找INFO.WHPO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhPoCommand findWhPoCommandByIdToInfo(Long id, Long ouId);

    /**
     * [通用方法]根据ID,OUID查找SHARD.WHPO
     * 
     * @param id
     * @param ouId
     * @return
     */
    WhPoCommand findWhPoCommandByIdToShard(Long id, Long ouId);

    /**
     * [业务方法]PO单一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<WhPoCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params);

    /**
     * [业务方法]模糊查询 TODO
     * 
     * @param poCode
     * @param status
     * @param customerList
     * @param storeList
     * @param ouid
     * @param linenum
     * @return
     */
    List<WhPoCommand> findWhPoListByExtCodeToShard(String poCode, List<Integer> status,List<Long> customerList,List<Long> storeList, Long ouid, Integer linenum);

    /**
     * [业务方法]删除SHARD.WHPO关联操作：删除INFO.WHPO并回滚BIPO
     * 
     * @param po
     * @param userId
     */
    void deletePoAndPoLineToInfo(WhPo po, Long userId);

    /**
     * [业务方法]删除SHARD.WHPO
     * 
     * @param po
     * @param userId
     */
    void deletePoAndPoLineToShard(WhPo po, Long userId);

    /**
     * [通用方法]乐观锁更新INFO.WHPO
     * 
     * @param o
     */
    void saveOrUpdateByVersionToInfo(WhPo o);

    /**
     * [通用方法]乐观锁更新WHPO.WHPO
     * 
     * @param o
     */
    void saveOrUpdateByVersionToShard(WhPo o);

    /**
     * [业务方法]取消SHARD.WHPO关联操作：取消INFO.WHPO并回滚BIPO数量
     * 
     * @param updateInfoPo
     * @param extlineNumList
     * @param userId
     */
    void cancelPoToInfo(WhPo updateInfoPo, Boolean isPoCancel, List<Integer> extlineNumList, Long userId);

    /**
     * [业务方法]取消SHARD.WHPO
     * 
     * @param updateShardPo
     * @param isPoCancel
     * @param extlineNumList
     * @param userId
     */
    void cancelPoToShard(WhPo updateShardPo, Boolean isPoCancel, List<Integer> extlineNumList, Long userId);

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

    /**
     * info库中，extCode,storeId会对应多条Po;
     * 
     * @param poCode
     * @return
     */
    List<WhPo> findWhPoByExtCodeStoreIdToInfo(String extCode, Long storeId);

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
    
    /**
     * [通用方法]根据EXTCODE+STOREID查找WHPO列表
     * 
     * @param extCode @required
     * @param storeId @required
     * @return
     */
    List<WhPoCommand> findPoListByExtCodeStoreId(String extCode, String storeId);

    /**
     * 关闭PO单
     * 
     * @param id
     * @param ouId
     * @param userId
     */
    void closePoToShard(Long id, Long ouId, Long userId);

    void closePoToInfo(Long id, Long ouId, Long userId);

    /**
     * [业务方法]同步数据到集团下
     * 
     * @param infoPo
     * @param operateType
     */
    void snycPoToInfo(WhPo infoPo, String operateType);

}
