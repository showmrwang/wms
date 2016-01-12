package com.baozun.scm.primservice.whoperation.manager.createspoasn;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;


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
    public Pagination<WhPoCommand> findListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params) {
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
        if (null == po.getPoDate()) {
            whPo.setPoDate(new Date());
        }
        whPo.setCreateTime(new Date());
        whPo.setCreatedId(po.getUserId());
        whPo.setLastModifyTime(new Date());
        whPo.setModifiedId(po.getUserId());
        whPoDao.saveOrUpdate(whPo);
        if (po.getPoLineList().size() > 0) {
            // 有line信息保存
        }
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
