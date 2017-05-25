package com.baozun.scm.primservice.whoperation.manager.init;

import java.util.Date;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.constant.DbDataSource;
import com.baozun.scm.primservice.whoperation.dao.archiv.OdoArchivDao;
import com.baozun.scm.primservice.whoperation.dao.sku.SkuDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.StoreDao;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivIndex;
import com.baozun.scm.primservice.whoperation.model.collect.WhOdoArchivLineIndex;
import com.baozun.scm.primservice.whoperation.model.sku.Sku;
import com.baozun.scm.primservice.whoperation.model.warehouse.Store;
import com.baozun.scm.primservice.whoperation.util.HashUtil;
import com.baozun.scm.primservice.whoperation.util.Md5Util;

@Transactional
@Service("initManager")
public class InitManagerImpl implements InitManager {

    @Autowired
    private StoreDao storeDao;
    @Autowired
    private SkuDao skuDao;
    @Autowired
    private OdoArchivDao odoArchivDao;

    /**
     * 初始化出库单历史收集数据 bin.hu
     * 
     * @param data
     * @param isExist
     */
    @Override
    public void initOdoCollect(String data, Boolean isExist, String odoCode) {
        String[] d = data.split(",");
        String serialNumber = HashUtil.serialNumberByHashCode(d[0].trim());
        Store store = findStore(d[1].trim().toString());
        Sku sku = findSku(d[2].trim().toString(), store.getCustomerId());
        if (!isExist) {
            // 新建odoArchiv+odoArchivLine
            WhOdoArchivIndex whOdoArchivIndex = new WhOdoArchivIndex();
            whOdoArchivIndex.setEcOrderCode(d[0].trim().toString());
            whOdoArchivIndex.setWmsOdoCode(odoCode);
            whOdoArchivIndex.setDataSource("WMS3");
            whOdoArchivIndex.setIsReturnedPurchase(false);
            whOdoArchivIndex.setNum(serialNumber);
            whOdoArchivIndex.setOuId(26L);
            whOdoArchivIndex.setSysDate("201705");
            whOdoArchivIndex.setCreateTime(new Date());
            saveOdoArchivIndex(whOdoArchivIndex);
        }
        // 增加odoArchivLine
        WhOdoArchivLineIndex w = new WhOdoArchivLineIndex();
        w.setEcOrderCode(d[0].trim().toString());
        w.setSkuId(sku.getId());
        w.setStoreId(store.getId());
        w.setReturnedPurchaseQty(Double.valueOf(d[5].trim().toString()));
        w.setDataSource("WMS3");
        w.setInvStatus(3L);
        w.setColor(sku.getColor());
        w.setStyle(sku.getStyle());
        w.setSize(sku.getSize());
        w.setNum(serialNumber);
        if (!StringUtils.isEmpty(d[4])) {
            w.setSn(d[4].trim().toString());
        }
        try {
            String skuUuid = Md5Util.getMd5(w.getEcOrderCode() + w.getSkuId() + w.getStoreId());
            w.setUuid(skuUuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveOdoArchivLineIndex(w);
    }

    @Transactional
    @MoreDB(DbDataSource.MOREDB_GLOBALSOURCE)
    public Store findStore(String code) {
        Store s = storeDao.findByCode(code);
        return s;
    }

    @Transactional
    @MoreDB(DbDataSource.MOREDB_INFOSOURCE)
    public Sku findSku(String upc, Long customerId) {
        Sku sku = skuDao.findWhSkuByExtCodeAndCustomerId(upc, customerId);
        return sku;
    }

    @Transactional
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public void saveOdoArchivIndex(WhOdoArchivIndex whOdoArchivIndex) {
        odoArchivDao.saveOdoArchivIndex(whOdoArchivIndex);
    }

    @Transactional
    @MoreDB(DbDataSource.MOREDB_COLLECTSOURCE)
    public void saveOdoArchivLineIndex(WhOdoArchivLineIndex WhOdoArchivLineIndex) {
        odoArchivDao.saveOdoArchivLineIndex(WhOdoArchivLineIndex);
    }
}
