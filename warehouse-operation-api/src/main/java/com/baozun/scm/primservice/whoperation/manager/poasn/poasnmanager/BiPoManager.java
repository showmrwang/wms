package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import com.baozun.scm.primservice.whoperation.command.poasn.BiPoCommand;
import com.baozun.scm.primservice.whoperation.manager.BaseManager;
import com.baozun.scm.primservice.whoperation.model.poasn.BiPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoTransportMgmt;
import com.baozun.scm.primservice.whoperation.model.warehouse.ma.TransportProvider;

/**
 * @author yimin.lu
 *
 */
public interface BiPoManager extends BaseManager {
    /**
     * [通用逻辑] 根据字段查找集合; 此方法注意BiPo对象可能有默认值，会影响查询结果
     * 
     * @param biPo
     * @return
     */
    @Deprecated
    List<BiPo> findListByParam(BiPo biPo);

    /**
     * [通用方法]根据ID查询BIPO
     * 
     * @param id
     * @return
     */
    BiPo findBiPoById(Long id);

    /**
     * [通用方法]根据POCODE查询BIPO
     * 
     * @param poCode
     * @return
     */
    BiPo findBiPoByPoCode(String poCode);

    /**
     * [通用方法]根据id关联查询BIPO;
     * 关联customer,store,sysdictionary[potype],t_wh_logistics_provider,t_wh_supplier
     * 
     * @param code
     * @return
     */
    BiPoCommand findBiPoCommandById(Long id);

    /**
     * [通用方法]根据pocode关联查询BIPO;
     * 关联customer,store,sysdictionary[potype],t_wh_logistics_provider,t_wh_supplier
     * 
     * @param code
     * @return
     */
    BiPoCommand findBiPoCommandByPoCode(String poCode);

    /**
     * [业务方法]BiPo一览查询
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    Pagination<BiPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params);


    /**
     * [业务方法]创建PO单分支一：创建INFO库PO单;@author yimin.lu
     * 逻辑:①当创建的PO单没有仓库时候，只插入到BIPO中，不插入info库的whpo中；并不需要调用另一个分支
     * 逻辑:②当创建的PO单有仓库的时候，数据会同步到INFO库的whpo和shard库的whpo;此时不可单独使用
     * 
     * @param whPo
     * @param whPoTm 
     * @param whPoLines
     * @param rm
     * @return
     */
    void createPoAndLineToInfo(WhPo whPo, WhPoTransportMgmt whPoTm, List<WhPoLine> whPoLines);

    /**
     * [业务方法]创建PO单分支二：创建shard库PO单；
     * 
     * @param whPo
     * @param whPoTm 
     * @param whPoLines
     * @param rm
     * @return
     */
    void createPoAndLineToShared(WhPo whPo, WhPoTransportMgmt whPoTm, List<WhPoLine> whPoLines);

    /**
     * [业务方法]删除BIPO单操作：删除BIPO及明细
     * 
     * @param id
     * @param userId
     * @return
     */
    void deleteBiPoAndLine(Long id, Long userId);

    /**
     * [业务方法]取消BIPO单操作：将BIPO及其明细置为取消状态
     * 
     * @param id
     * @param userId
     * @return
     */
    void cancelBiPo(Long id, Long userId);

    /**
     * [业务方法]编辑BIPO单表头
     * 
     * @param updatePo
     * @return
     */
    void editBiPo(BiPo updatePo);

    /**
     * [业务方法]创建子PO操作分支一
     * 
     * @param po
     * @param whPoLineList
     */
    void createSubPoToInfo(WhPo po, List<WhPoLine> whPoLineList);

    /**
     * [业务方法]创建子PO操作分支一：将INFO临时数据保存。
     * 
     * @param id @required
     * @param extCode @required
     * @param StoreId @required
     * @param ouId @required
     * @param uuid @required
     * @param userId @required
     */
    void saveSubPoToInfo(Long id, String extCode, Long StoreId, Long ouId, String uuid, Long userId);

    /**
     * [业务方法]创建子PO操作分支一:将临时数据删除
     * 
     * @param poCode
     * @param ouId
     * @param id
     */
    void closeSubPoToInfo(String extCode, Long storeId, Long ouId, Long id);

    /**
     * [通用方法]根据店铺ID和相关单据号查询
     * 
     * @param storeId @required
     * @param extCode @required
     * @return
     */
    List<BiPo> findListByStoreIdExtCode(Long storeId, String extCode);

    /**
     * [通用方法]根据相关单据号查询
     * 
     * @param storeId @required
     * @param extCode @required
     * @return
     */
    List<BiPo> findListByExtCode(String extCode);

    /**
     * [通用方法] 根据EXTCODE,STOREID查找BIPO
     * 
     * @param extCode @required
     * @param storeId @required
     * @return
     */
    BiPo findBiPoByExtCodeStoreId(String extCode, Long storeId);

    /**
     * 根据运输服务商Code查找运输服务商
     * 
     * @param logisticsProviderCode
     * @return
     */
    TransportProvider findTransportProviderByCode(String logisticsProviderCode);

    /**
     * 计算是否自动关单
     * 
     * @param storeId
     * @param ouId
     * @return
     */
    Boolean calIsAutoClose(Long storeId, Long ouId);
    
    /**
     * 退换货创建Po单逻辑
     * @author kai.zhu
     * @version 2017年3月28日
     */
	void createPoByReturnStorage(WhPo whPo, WhPoTransportMgmt whPoTm, List<WhPoLine> whPoLines, Long ouId);

}
