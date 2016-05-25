package com.baozun.scm.primservice.whoperation.manager.pda.rcvd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.warehouse.inventory.WhSkuInventoryDao;
import com.baozun.scm.primservice.whoperation.manager.BaseManagerImpl;
import com.baozun.scm.primservice.whoperation.manager.pda.Rcvd.GeneralRcvdManager;
import com.baozun.scm.primservice.whoperation.model.warehouse.inventory.WhSkuInventory;

@Service("generalRcvdManager")
@Transactional
public class GeneralRcvdManagerImpl extends BaseManagerImpl implements GeneralRcvdManager {
    @Autowired
    private WhSkuInventoryDao whSkuInventoryDao;

    @Override
    public void saveScanedSkuWhenGeneralRcvdForPda(WhSkuInventory commmand) {
        // TODO Auto-generated method stub

    }


}
