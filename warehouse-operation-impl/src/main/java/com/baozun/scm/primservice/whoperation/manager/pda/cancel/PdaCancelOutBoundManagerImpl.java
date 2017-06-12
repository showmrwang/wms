package com.baozun.scm.primservice.whoperation.manager.pda.cancel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.constant.ContainerStatus;
import com.baozun.scm.primservice.whoperation.constant.OdoStatus;
import com.baozun.scm.primservice.whoperation.dao.odo.WhOdoDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.Container2ndCategoryDao;
import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.model.odo.WhOdo;
import com.baozun.scm.primservice.whoperation.model.warehouse.Container2ndCategory;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

@Transactional
@Service("pdaCancelOutBoundManager")
public class PdaCancelOutBoundManagerImpl extends BaseManagerImpl implements PdaCancelOutBoundManager {
    
    protected static final Logger log = LoggerFactory.getLogger(PdaCancelOutBoundManagerImpl.class);
    
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;
    @Autowired
    private WhOdoDao whOdoDao;
    @Autowired
    private Container2ndCategoryDao container2ndCategoryDao;
    
    @Override
    public int checkContainerCodeInSkuInventory(String containerCode, String latticNo, Long ouId) {
        
        List<WhSkuInventory> list = whSkuInventoryDao.findSkuInventoryByOutSideContainerCode(containerCode, latticNo, ouId);
        if (null != list && !list.isEmpty()) {
            if (!StringUtils.isEmpty(latticNo)) {
                this.checkOdoIsCancelStatus(list, ouId);
            }
            return 1;
        } 
        list = whSkuInventoryDao.findSkuInventoryByInSideContainerCode(containerCode, ouId);
        if (null != list && !list.isEmpty()) {
            this.checkOdoIsCancelStatus(list, ouId);
            return 2;
        } 
        list = whSkuInventoryDao.findSkuInventoryBySeedingWallCode(containerCode, latticNo, ouId);
        if (null != list && !list.isEmpty()) {
            if (!StringUtils.isEmpty(latticNo)) {
                this.checkOdoIsCancelStatus(list, ouId);
            }
            return 3;
        } 
        list = whSkuInventoryDao.findSkuInventoryByOutBoundBoxCode(containerCode, ouId);
        if (null != list && !list.isEmpty()) {
            this.checkOdoIsCancelStatus(list, ouId);
            return 4;
        } 
        throw new BusinessException(ErrorCodes.NOT_FIND_SKU_INV_BY_CONTAINERCODE);
    }

    private void checkOdoIsCancelStatus(List<WhSkuInventory> list, Long ouId) {
        Set<String> odoCodeSet = new HashSet<String>();
        for (WhSkuInventory inv : list) {
            String odoCode = inv.getOccupationCode();
            if (StringUtils.isEmpty(odoCode)) {
                throw new BusinessException(ErrorCodes.DATA_BIND_EXCEPTION);
            }
            odoCodeSet.add(odoCode);
        }
        for (String odoCode : odoCodeSet) {
            WhOdo odo = whOdoDao.findOdoByCodeAndOuId(odoCode, ouId);
            if (null == odo) {
                throw new BusinessException(ErrorCodes.ODO_NOT_FIND);
            }
            if (!OdoStatus.CANCEL.equals(odo.getOdoStatus())) {
                throw new BusinessException(ErrorCodes.ODO_NOT_CANCEL_STATUS);
            }
        }
    }

    @Override
    public List<Container2ndCategory> getTwoLevelTypeList(Long ouId) {
        Container2ndCategory c2 = new Container2ndCategory();
        c2.setOneLevelType(Constants.CONTAINER_TYPE_BOX);
        c2.setLifecycle(ContainerStatus.CONTAINER_LIFECYCLE_USABLE);
        c2.setOuId(ouId);
        List<Container2ndCategory> findListByParam = container2ndCategoryDao.findListByParam(c2);
        return findListByParam;
    }
    
}
