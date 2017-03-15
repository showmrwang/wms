package com.baozun.scm.primservice.whoperation.manager.archiv;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.archiv.OdoArchivDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdoLine;
import com.baozun.scm.primservice.whoperation.util.DateUtil;

/***
 * 出库单归档
 * 
 * @author bin.hu
 *
 */
@Service("odoArchivManager")
@Transactional
public class OdoArchivManagerImpl implements OdoArchivManager {

    protected static final Logger log = LoggerFactory.getLogger(OdoArchivManager.class);

    @Autowired
    private OdoArchivDao odoArchivDao;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private WhOdoLineDao whOdoLineDao;

    /***
     * 备份Odo信息
     * 
     * @param odoid
     * @param ouid
     * @return
     */
    @Override
    public int archivOdo(Long odoid, Long ouid) {
        int count = 0;
        try {
            String sysDate = DateUtil.getSysDate();
            // 查询对应odo信息
            WhOdo whOdo = whOdoDao.findByIdOuId(odoid, ouid);
            if (null == whOdo) {
                log.warn("OdoArchivManagerImpl archivOdo warn whpo is null odoid:" + odoid + " ouid:" + ouid);
                return count;
            }
            // 插入odoArchiv信息
            whOdo.setSysDate(sysDate);
            whOdo.setArchivTime(new Date());
            int odo = odoArchivDao.archivWhOdo(whOdo);
            count += odo;
            // 查询对应odoLine信息
            List<WhOdoLine> odoLineList = whOdoLineDao.findOdoLineListByOdoIdOuId(odoid, ouid);
            for (WhOdoLine ol : odoLineList) {
                // 插入odoLineArchiv信息
                ol.setSysDate(sysDate);
                int odoLine = odoArchivDao.archivWhOdoLine(ol);
                count += odoLine;
            }
        } catch (Exception e) {
            log.error("OdoArchivManagerImpl archivOdo error" + e);
            throw new BusinessException(ErrorCodes.SYSTEM_ERROR);
        }
        return count;
    }

}
