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

import com.baozun.scm.baseservice.sac.manager.CodeManager;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoLineCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.AsnManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoLineManager;
import com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager.PoManager;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsn;
import com.baozun.scm.primservice.whoperation.model.poasn.WhAsnLine;
import com.baozun.scm.primservice.whoperation.util.StringUtil;

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
    @Autowired
    private CodeManager codeManager;
    @Autowired
    private AsnLineManager asnLineManager;

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
    public Pagination<WhPoCommand> findWhPoListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType) {
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
     * 通过asnextCode查询对应数据 ASN预约时模糊查询对应数据
     */
    @Override
    public List<WhAsnCommand> findWhAsnListByAsnExtCode(String asnExtCode, Integer status, Long ouid) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnListByAsnExtCode method begin!");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".findWhAsnListByAsnExtCode params:asnExtCode:{},status:{},ouid:{}", asnExtCode, status, ouid);
        }
        return asnManager.findWhAsnListByAsnExtCode(asnExtCode, status, ouid);
    }

    /**
     * 通过id+ou_id 查询PO单信息
     */
    @Override
    public WhPoCommand findWhPoById(WhPoCommand whPoCommand) {
        log.info(this.getClass().getSimpleName() + ".findWhPoById method begin!");
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".findWhPoById method params:{}", whPoCommand);
        }
        WhPoCommand whpo = null;
        if (null == whPoCommand.getOuId()) {
            // 查询基本库内信息
            whpo = poManager.findWhPoByIdToInfo(whPoCommand);
        } else {
            // 查询拆库内信息
            whpo = poManager.findWhPoByIdToShard(whPoCommand);
        }
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".findWhPoById method returns {}!", whpo);
        }
        return whpo;
    }

    /**
     * 查询PO单明细行 包括保存和未保存的数据
     */
    @Override
    public Pagination<WhPoLineCommand> findPoLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType) {
        log.info(this.getClass().getSimpleName() + ".findPoLineListByQueryMapWithPageExt method begin!");
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
        log.info(this.getClass().getSimpleName() + ".findPoLineListByQueryMapWithPageExt method end!");
        return whPoLineCommandList;
    }

    /**
     * 通过id+ou_id 查询PO单信息
     */
    @Override
    public WhPoLineCommand findWhPoLineById(WhPoLineCommand Command) {
        log.info(this.getClass().getSimpleName() + ".findWhPoLineById method begin!");
        WhPoLineCommand whpoLine = null;
        if (null == Command.getOuId()) {
            // 查询基本库内信息
            whpoLine = poLineManager.findPoLinebyIdToInfo(Command);
        } else {
            // 查询拆库内信息
            whpoLine = poLineManager.findPoLinebyIdToShard(Command);
        }
        log.info(this.getClass().getSimpleName() + ".findWhPoLineById method end!");
        return whpoLine;
    }

    /**
     * 查询ASN单列表(带分页)
     */
    @Override
    public Pagination<WhAsnCommand> findWhAsnListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnListByQueryMapWithPageExt method begin!");
        Pagination<WhAsnCommand> whAsnCommandList = null;
        if (null == sourceType) {
            sourceType = Constants.SHARD_SOURCE;
        }
        // 判断读取那个库的数据
        if (sourceType == Constants.SHARD_SOURCE) {
            // 拆分库
            whAsnCommandList = asnManager.findListByQueryMapWithPageExtByShard(page, sorts, params);
        }
        if (sourceType == Constants.INFO_SOURCE) {
            // 公共库
            whAsnCommandList = asnManager.findListByQueryMapWithPageExtByInfo(page, sorts, params);
        }
        log.info(this.getClass().getSimpleName() + ".findWhAsnListByQueryMapWithPageExt method end!");
        return whAsnCommandList;
    }

    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息
     */
    @Override
    public List<WhPoCommand> findWhPoListByExtCode(WhPoCommand command) {
        log.info(this.getClass().getSimpleName() + ".findWhPoListByExtCode method begin!");
        if (null == command.getOuId()) {
            return poManager.findWhPoListByExtCodeToInfo(command.getExtCode(), command.getStatusList(), command.getOuId(), command.getLinenum());
        } else {
            return poManager.findWhPoListByExtCodeToShard(command.getExtCode(), command.getStatusList(), command.getOuId(), command.getLinenum());
        }
    }

    /**
     * 通过编码生成器接口获取asn相关单据号
     */
    @Override
    public String getAsnExtCode() {
        log.info(this.getClass().getSimpleName() + ".getAsnExtCode method begin!");
        String extCode = codeManager.generateCode(Constants.WMS, Constants.WHASN_MODEL_URL, Constants.WMS_ASN_EXT, null, null);
        if (StringUtil.isEmpty(extCode)) {
            log.warn("getAsnExtCode warn generateCode is null");
            throw new BusinessException(ErrorCodes.GET_GENERATECODE_NULL, new Object[] {"asn"});
        }
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getSimpleName() + ".getAsnExtCode method returns:{}!", extCode);
        }
        log.info(this.getClass().getSimpleName() + ".getAsnExtCode method end!");
        return extCode;
    }

    @Override
    public WhAsnCommand findWhAsnById(WhAsnCommand whAsnCommand) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnById method begin!");
        return asnManager.findWhAsnByIdToShard(whAsnCommand);
    }

    /**
     * ASNLINE 列表(带分页)
     */
    @Override
    public Pagination<WhAsnLineCommand> findAsnLineListByQueryMapWithPageExt(Page page, Sort[] sorts, Map<String, Object> params, Integer sourceType) {
        log.info(this.getClass().getSimpleName() + ".findAsnLineListByQueryMapWithPageExt method begin!");
        Pagination<WhAsnLineCommand> whAsnLineCommandList = asnLineManager.findListByQueryMapWithPageExtByShard(page, sorts, params);
        return whAsnLineCommandList;
    }

    @Override
    public WhAsnLineCommand findWhAsnLineById(WhAsnLineCommand Command) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnLineById method begin!");
        return this.asnLineManager.findWhAsnLineByIdToShard(Command);
    }

    @Override
    public WhAsn getAsnByAsnExtCode(String asnExtCode, Long ouId) {
        return null;
    }

    @Override
    public long getSkuCountInAsnBySkuId(Long asnId, Long ouId, Long skuId) {
        log.info(this.getClass().getSimpleName() + ".getSkuCountInAsnBySkuId method begin!");
        if (log.isDebugEnabled()) {
            log.debug("params: [asnId:{},ouId:{},skuId:{}]", asnId, ouId, skuId);
        }
        WhAsnLine asnline = new WhAsnLine();
        asnline.setAsnId(asnId);
        asnline.setAsnId(ouId);
        asnline.setAsnId(skuId);
        List<WhAsnLine> lineList = this.asnLineManager.findListByShard(asnline);
        long count = 0;
        for (WhAsnLine line : lineList) {
            count += line.getQtyPlanned();
        }
        log.info(this.getClass().getSimpleName() + ".getSkuCountInAsnBySkuId method END!");
        return count;
    }

}
