package com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy;

import java.util.List;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnproxy.SelectPoAsnManagerProxy;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;

/**
 * 查询PoAsn相关数据
 * 
 * @author bin.hu
 * 
 */
@Service("selectPoAsnManagerProxy")
public class SelectPoAsnManagerProxyImpl implements SelectPoAsnManagerProxy {

    protected static final Logger log = LoggerFactory.getLogger(SelectPoAsnManagerProxy.class);

    @Autowired
    private PoManager poManager;
    @Autowired
    private AsnManager asnManager;
    @Autowired
    private PoLineManager poLineManager;

    /**
     * 
     * 查询po单列表(带分页)
     * 
     * @param page
     * @param sorts
     * @param params
     * @param sourceType
     * @return
     */
    @Override
    public Pagination<WhPoCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType) {
        Pagination<WhPoCommand> whPoCommandList = null;
        if (null == sourceType) {
            sourceType = Constants.SHARD_SOURCE;
        }
        // 判断读取那个库的数据
        if (sourceType == Constants.SHARD_SOURCE) {
            // 拆分库
            whPoCommandList = poManager.findListByQueryMapWithPageExtByShard(page, sorts, params);
        }
        if (sourceType == Constants.INFO_SOURCE) {
            // 公共库
            whPoCommandList = poManager.findListByQueryMapWithPageExtByInfo(page, sorts, params);
        }
        return whPoCommandList;
    }

    /**
     * 通过asnCode查询对应数据 ASN预约时模糊查询对应数据
     */
    @Override
    public List<WhAsnCommand> findWhAsnListByAsnCode(String asnCode, Integer status, Long ouid) {
        return asnManager.findWhAsnListByAsnCode(asnCode, status, ouid);
    }

    /**
     * 通过id+ou_id 查询PO单信息
     */
    @Override
    public WhPo findWhPoById(WhPoCommand whPoCommand) {
        WhPo whpo = null;
        if (null == whPoCommand.getOuId()) {
            // 查询基本库内信息
            whpo = poManager.findWhPoByIdByInfo(whPoCommand);
        } else {
            // 查询拆库内信息
            whpo = poManager.findWhPoByIdByShard(whPoCommand);
        }
        return whpo;
    }

    /**
     * 查询PO单明细行 包括保存和未保存的数据
     */
    @Override
    public Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType) {
        Pagination<WhPoLineCommand> whPoLineCommandList = null;
        if (null == sourceType) {
            sourceType = Constants.SHARD_SOURCE;
        }
        // 判断读取那个库的数据
        if (sourceType == Constants.SHARD_SOURCE) {
            // 拆分库
            whPoLineCommandList = poLineManager.findListByQueryMapWithPageExtByShard(page, sorts, params);
        }
        if (sourceType == Constants.INFO_SOURCE) {
            // 公共库
            whPoLineCommandList = poLineManager.findListByQueryMapWithPageExtByInfo(page, sorts, params);
        }
        return whPoLineCommandList;
    }


}
