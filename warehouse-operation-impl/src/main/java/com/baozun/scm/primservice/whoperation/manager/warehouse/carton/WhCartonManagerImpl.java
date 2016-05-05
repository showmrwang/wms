package com.baozun.scm.primservice.whoperation.manager.warehouse.carton;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.carton.WhCartonCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
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
            log.warn("deleteCarton asn is null logid: " + whCartonCommand.getLogId());
            throw new BusinessException(ErrorCodes.CARTONNULL_ERROR);
        }
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

}
