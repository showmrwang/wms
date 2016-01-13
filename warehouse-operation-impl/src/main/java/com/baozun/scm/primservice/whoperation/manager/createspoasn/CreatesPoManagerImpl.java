package com.baozun.scm.primservice.whoperation.manager.createspoasn;

import java.util.Date;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;


/**
 * 创建PO单据
 * 
 * @author bin.hu
 * 
 */
@Service("createsPoManager")
@Transactional
public class CreatesPoManagerImpl implements CreatesPoManager {

    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhPoLineDao whPoLineDao;

    /**
     * 查询po单列表(带分页)
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
            whPoCommandList = findListByQueryMapWithPageExtByShard(page, sorts, params);
        }
        if (sourceType == Constants.INFO_SOURCE) {
            // 公共库
            whPoCommandList = findListByQueryMapWithPageExtByInfo(page, sorts, params);
        }
        return whPoCommandList;
    }

    /**
     * 读取公共库PO单数据
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @MoreDB("infoSource")
    public Pagination<WhPoCommand> findListByQueryMapWithPageExtByInfo(Page page, Sort[] sorts, Map<String, Object> params) {
        return whPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }

    /**
     * 读取拆分库PO单数据
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @MoreDB("shardSource")
    public Pagination<WhPoCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return whPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }


    /**
     * 保存po单信息
     * 
     */
    @Override
    public ResponseMsg createPoAndLine(WhPoCommand po, ResponseMsg rm) {
        WhPo whPo = new WhPo();
        BeanUtils.copyProperties(po, whPo);
        // 相关单据号 调用HUB编码生成器获得
        whPo.setExtCode(String.valueOf(System.currentTimeMillis()));
        // 采购时间为空默认为当前时间
        if (null == po.getPoDate()) {
            whPo.setPoDate(new Date());
        }
        whPo.setCreateTime(new Date());
        whPo.setCreatedId(po.getUserId());
        whPo.setLastModifyTime(new Date());
        whPo.setModifiedId(po.getUserId());
        whPoDao.saveOrUpdate(whPo);
        if (null != po.getPoLineList()) {
            // 有line信息保存
            for (int i = 0; i < po.getPoLineList().size(); i++) {
                WhPoLineCommand polineCommand = po.getPoLineList().get(i);
                WhPoLine poline = new WhPoLine();
                BeanUtils.copyProperties(polineCommand, poline);
                poline.setOuId(whPo.getOuId());
                if (null == poline.getLinenum()) {
                    // 行号为空的话默认1开始递增
                    poline.setLinenum(i++);
                }
                poline.setCreateTime(new Date());
                poline.setCreatedId(po.getUserId());
                poline.setLastModifyTime(new Date());
                poline.setModifiedId(po.getUserId());
                whPoLineDao.saveOrUpdate(poline);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(whPo.getId() + "");
        return rm;

    }

    /**
     * 通过OP单ID查询相关信息
     */
    @Override
    public WhPoCommand findWhPoById(Long id, Long ouid) {
        return whPoDao.findWhPoById(id, ouid);
    }
}
