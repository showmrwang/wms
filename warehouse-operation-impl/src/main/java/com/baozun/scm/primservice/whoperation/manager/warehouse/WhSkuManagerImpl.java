package com.baozun.scm.primservice.whoperation.manager.warehouse;

import lark.common.annotation.MoreDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.warehouse.WhSkuCommand;
import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.warehouse.WhSkuDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.warehouse.WhSku;

@Service("whSkuManager")
@Transactional
public class WhSkuManagerImpl extends BaseManagerImpl implements WhSkuManager {

    public static final Logger log = LoggerFactory.getLogger(WhSkuManagerImpl.class);

    @Autowired
    private WhSkuDao whSkuDao;

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhSkuCommand getSkuBybarCode(String barcode, Long ouId) {
        WhSkuCommand whSkuCommand = whSkuDao.findWhSkuByBarcodeExt(barcode, ouId);
        return whSkuCommand;
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhSku getskuById(Long skuId, Long ouId) {
        return whSkuDao.findById(skuId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhSku getSkuBySkuCodeOuId(String skuCode, Long ouId) {
        return this.whSkuDao.findSkuBySkuCode(skuCode, ouId);
    }

    @Override
    @MoreDB(DbDataSource.MOREDB_SHARDSOURCE)
    public WhSkuCommand findBySkuIdAndOuId(Long skuId, Long ouId) {
        return this.whSkuDao.findWhSkuByIdExt(skuId, ouId);
    }
}
