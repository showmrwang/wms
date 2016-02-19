package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lark.common.annotation.MoreDB;
import lark.common.dao.Page;
import lark.common.dao.Pagination;
import lark.common.dao.Sort;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.command.poasn.WhPoCommand;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;


/**
 * 创建PO单据
 * 
 * @author bin.hu
 * 
 */
@Service("poManager")
@Transactional
public class PoManagerImpl implements PoManager {

    @Autowired
    private WhPoDao whPoDao;
    @Autowired
    private WhPoLineDao whPoLineDao;


    /**
     * 读取公共库PO单数据
     * 
     * @param page
     * @param sorts
     * @param params
     * @return
     */
    @Override
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
    @Override
    @MoreDB("shardSource")
    public Pagination<WhPoCommand> findListByQueryMapWithPageExtByShard(Page page, Sort[] sorts, Map<String, Object> params) {
        return whPoDao.findListByQueryMapWithPageExt(page, sorts, params);
    }


    /**
     * 保存po单信息
     * 
     */
    @Override
    @MoreDB("shardSource")
    public ResponseMsg createPoAndLineToShare(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        long i = whPoDao.insert(po);
        if (0 == i) {
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.SAVE_PO_FAILED + "");// 保存至po单信息失败
            // throw new BusinessException(ErrorCodes.SAVE_PO_FAILED);
            return rm;
        }
        if (whPoLines.size() > 0) {
            // 有line信息保存
            for (WhPoLine whPoLine : whPoLines) {
                whPoLine.setPoId(po.getId());
                whPoLineDao.insert(whPoLine);
            }
        }
        rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
        rm.setMsg(po.getId() + "");
        return rm;

    }

    /**
     * 修改公共库PO单状态
     */
    @Override
    @MoreDB("infoSource")
    public int editPoStatusToInfo(WhPoCommand whPo) {
        int result = whPoDao.editPoStatus(whPo.getPoIds(), whPo.getStatus(), whPo.getModifiedId(), whPo.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != whPo.getPoIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {whPo.getPoIds().size(), result});
        }
        return result;
    }

    /**
     * 修改拆库PO单状态
     */
    @Override
    @MoreDB("shardSource")
    public int editPoStatusToShard(WhPoCommand whPo) {
        int result = whPoDao.editPoStatus(whPo.getPoIds(), whPo.getStatus(), whPo.getModifiedId(), whPo.getOuId(), new Date());
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
        if (result != whPo.getPoIds().size()) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_QUANTITYERROR, new Object[] {whPo.getPoIds().size(), result});
        }
        return result;
    }

    /**
     * 通过OP单ID查询相关信息 基础表
     */
    @Override
    @MoreDB("infoSource")
    public WhPoCommand findWhPoByIdToInfo(WhPoCommand whPo) {
        WhPoCommand whPoCommand = new WhPoCommand();
        WhPo po = whPoDao.findWhPoById(whPo.getId(), whPo.getOuId());
        BeanUtils.copyProperties(po, whPoCommand);
        return whPoCommand;
    }

    /**
     * 通过OP单ID查询相关信息 拆库表
     */
    @Override
    @MoreDB("shardSource")
    public WhPoCommand findWhPoByIdToShard(WhPoCommand whPo) {
        WhPoCommand whPoCommand = new WhPoCommand();
        WhPo po = whPoDao.findWhPoById(whPo.getId(), whPo.getOuId());
        BeanUtils.copyProperties(po, whPoCommand);
        return whPoCommand;
    }

    /**
     * 更新PO单信息 基础表
     * 
     * @param whPo
     */
    @Override
    @MoreDB("infoSource")
    public void editPoToInfo(WhPo whPo) {
        int count = 0;
        count = whPoDao.saveOrUpdateByVersion(whPo);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    /**
     * 更新PO单信息 拆库表
     * 
     * @param whPo
     */
    @Override
    @MoreDB("shardSource")
    public void editPoToShard(WhPo whPo) {
        int count = 0;
        count = whPoDao.saveOrUpdateByVersion(whPo);
        if (count == 0) {
            throw new BusinessException(ErrorCodes.UPDATE_DATA_ERROR);
        }
    }

    @Override
    @MoreDB("shardSource")
    public ResponseMsg insertPoWithOuId(PoCheckCommand poCheckCommand) {
        WhPo whPo = poCheckCommand.getWhPo();
        List<WhPoLine> whPoLines = poCheckCommand.getWhPoLines();
        ResponseMsg rm = poCheckCommand.getRm();
        String extCode = whPo.getExtCode();
        Long storeId = whPo.getStoreId();
        Long ouId = whPo.getOuId();
        /* 查找在性对应的拆库表中是否有此po单信息 */
        long count = whPoDao.findPoByCodeAndStore(extCode, storeId, ouId);
        /* 没有此po单信息 */
        if (0 == count) {
            long i = whPoDao.insert(whPo);
            if (0 == i) {
                rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
                rm.setMsg(ErrorCodes.SAVE_CHECK_TABLE_FAILED + "");// 保存至po_check信息失败
                return rm;
            }
            // whPoDao.saveOrUpdate(whpo);
            if (whPoLines.size() > 0) {
                // 有line信息保存
                for (WhPoLine whPoLine : whPoLines) {
                    whPoLine.setPoId(whPo.getId());
                    whPoLineDao.insert(whPoLine);
                }
            }
            rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
            rm.setMsg(whPo.getId() + "");
        } else {
            /* 存在此po单信息 */
            rm.setResponseStatus(ResponseMsg.STATUS_ERROR);
            rm.setMsg(ErrorCodes.PO_EXIST + "");
            return rm;
        }
        return rm;

    }

    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息 公共库
     */
    @Override
    @MoreDB("infoSource")
    public List<WhPoCommand> findWhPoListByPoCodeToInfo(String poCode, List<Integer> status, Long ouid) {
        return whPoDao.findWhPoListByPoCode(status, poCode, ouid);
    }

    /**
     * 通过po单code 状态 ouid 模糊查询对应po单信息 拆库
     */
    @Override
    @MoreDB("shardSource")
    public List<WhPoCommand> findWhPoListByPoCodeToShard(String poCode, List<Integer> status, Long ouid) {
        return whPoDao.findWhPoListByPoCode(status, poCode, ouid);
    }

    @Override
    @MoreDB("infoSource")
    public WhPo findWhAsnByIdToInfo(Long id, Long ouid) {
        return whPoDao.findWhPoById(id, ouid);
    }

    @Override
    @MoreDB("shardSource")
    public WhPo findWhAsnByIdToShard(Long id, Long ouid) {
        return whPoDao.findWhPoById(id, ouid);
    }

    @Override
    @MoreDB("infoSource")
    public void deletePoAndPoLineToInfo(List<WhPoCommand> whPoCommand) {
        for (WhPoCommand po : whPoCommand) {
            // 删除PO表头信息
            whPoDao.delete(po.getId());
            // 删除POLINE明细信息
            whPoLineDao.deletePoLineByPoId(po.getId());
        }
    }

    @Override
    @MoreDB("shardSource")
    public void deletePoAndPoLineToShard(List<WhPoCommand> whPoCommand) {
        for (WhPoCommand po : whPoCommand) {
            // 删除PO表头信息
            whPoDao.delete(po.getId());
            // 删除POLINE明细信息
            whPoLineDao.deletePoLineByPoId(po.getId());
        }
    }
}
