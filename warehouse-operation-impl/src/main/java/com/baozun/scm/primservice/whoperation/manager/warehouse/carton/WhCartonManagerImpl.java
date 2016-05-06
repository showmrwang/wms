package com.baozun.scm.primservice.whoperation.manager.warehouse.carton;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.WhAsnLineCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.constant.PoAsnStatus;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhAsnLineDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.carton.WhCartonDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.carton.WhCarton;

@Service("whCartonManager")
@Transactional
public class WhCartonManagerImpl extends BaseManagerImpl implements WhCartonManager {

    protected static final Logger log = LoggerFactory.getLogger(WhCartonManager.class);

    @Autowired
    private WhCartonDao whCartonDao;
    @Autowired
    private WhAsnLineDao whAsnLineDao;

    /**
     * 通过ASN相关信息查询对用拆箱信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public List<WhCartonCommand> findWhCartonDevanningList(Long asnid, Long asnlineid, Long skuid, Long ouid) {
        return whCartonDao.findWhCartonDevanningList(asnid, asnlineid, skuid, ouid);
    }

    /**
     * 删除已拆商品明细
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void deleteCarton(WhCartonCommand whCartonCommand) {
        log.info(this.getClass().getSimpleName() + ".findWhAsnLineCommandEditDevanning method begin! logid: " + whCartonCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[whCartonCommand:{}]", whCartonCommand.toString());
        }
        // 查询对应拆箱信息
        WhCarton c = whCartonDao.findWhCatonById(whCartonCommand.getId(), whCartonCommand.getOuId());
        if (null == c) {
            log.warn("deleteCarton WhCarton is null logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.CARTONNULL_ERROR);
        }
        WhAsnLineCommand whAsnLineCommand = whAsnLineDao.findWhAsnLineById(c.getAsnLineId(), c.getOuId());
        if (null == whAsnLineCommand) {
            log.warn("deleteCarton asnLine is null logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.ASNLINE_NULL);
        }
        // 验证对应ASNLINE状态是否是未收货状态
        if (!whAsnLineCommand.getStatus().equals(PoAsnStatus.ASNLINE_NOT_RCVD)) {
            log.warn("deleteCarton asnLine status is " + whAsnLineCommand.getStatus() + " error logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.ASNLINE_STATUS_ERROR);
        }
        // 删除对应拆箱信息
        int count = whCartonDao.deleteCartonById(c.getId(), c.getOuId());
        if (count == 0) {
            // 删除失败
            log.warn("deleteCarton WhCarton count=0 logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.DELETE_ERROR);
        }
        // 插入系统日志表
        insertGlobalLog(GLOBAL_LOG_DELETE, c, c.getOuId(), whCartonCommand.getModifiedId(), whAsnLineCommand.getAsnCode(), null);
        log.info(this.getClass().getSimpleName() + ".findWhAsnLineCommandEditDevanning method end! logid: " + whCartonCommand.getLogId());
    }

    /**
     * 根据ID+OUID查询对应拆箱信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhCarton findWhCatonById(Long id, Long ouid) {
        return whCartonDao.findWhCatonById(id, ouid);
    }

    /**
     * 新增ASN拆箱明细信息
     */
    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public void addDevanningList(WhCartonCommand whCartonCommand) {
        log.info(this.getClass().getSimpleName() + ".addDevanningList method begin! logid: " + whCartonCommand.getLogId());
        if (log.isDebugEnabled()) {
            log.debug("params:[whCartonCommand:{}]", whCartonCommand.toString());
        }
        if (null == whCartonCommand.getCartonList()) {
            // 没有新增拆箱商品明细
            log.warn("addDevanningList CartonList() is null logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.ADD_CARTONLIST_NULL_ERROR);
        }
        List<WhCartonCommand> cartonList = whCartonCommand.getCartonList();
        // 获取这单可拆数量
        WhAsnLineCommand usableDevanningQty = whAsnLineDao.findWhAsnLineCommandEditDevanning(whCartonCommand.getAsnLineId(), whCartonCommand.getAsnId(), whCartonCommand.getOuId(), whCartonCommand.getSkuId());
        checkAddCartonListQty(cartonList, usableDevanningQty.getUsableDevanningQty(), whCartonCommand.getLogId());// 验证本次拆箱数量是否超过可拆箱数量
        log.info(this.getClass().getSimpleName() + ".addDevanningList method begin! end: " + whCartonCommand.getLogId());
    }

    /***
     * 验证本次拆箱数量是否超过可拆箱数量 每箱数量是否相同等数量信息
     */
    private static void checkAddCartonListQty(List<WhCartonCommand> cartonList, Double usableDevanningQty, String logid) {
        Double qty = 0.0;
        for (WhCartonCommand carton : cartonList) {
            qty = qty + carton.getBcdevanningQty();// 累加本次拆箱商品数量
        }
        if (qty.compareTo(usableDevanningQty) > 0) {
            // 本次总拆箱商品数量大于可拆商品数量
            log.warn("addDevanningList qty > usableDevanningQty error logid: " + logid);
            throw new BusinessException(ErrorCodes.ADD_CARTONLIST_QTY_ERROR, new Object[] {qty, usableDevanningQty});
        }
    }

}
