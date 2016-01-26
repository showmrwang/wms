package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.dao.poasn.CheckPoCodeDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.model.ResponseMsg;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckPoCode;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPo;
import com.baozun.scm.primservice.whoperation.model.poasn.WhPoLine;

@Service("poCheckManager")
@Transactional
public class PoCheckManagerImpl implements PoCheckManager {

    @Autowired
    private CheckPoCodeDao checkPoCodeDao;

    @Autowired
    private WhPoDao whPoDao;

    @Autowired
    private PoManager poManager;

    @Override
    @MoreDB("infoSource")
    public ResponseMsg insertPoWithCheckWithoutOuId(PoCheckCommand poCheckCommand) {
        CheckPoCode checkPoCode = poCheckCommand.getCheckPoCode();
        WhPo whPo = poCheckCommand.getWhPo();
        List<WhPoLine> whPoLines = poCheckCommand.getWhPoLines();
        ResponseMsg rm = poCheckCommand.getRm();
        /* 校验po单是否在t_wh_po_check中存在 */
        CheckPoCode po = checkPoCodeDao.findPoFromPoCheck(checkPoCode);
        /* 不存在则在po表中创建 */
        if (null == po) {
            /* 创建po */
            int i = checkPoCodeDao.saveOrUpdate(checkPoCode);
            if (i != 0) {
                rm = poManager.createPoAndLineToInfo(whPo, whPoLines, rm);
            }
        } else {
            /* 从po表中根据code和store id查找po单号 */
            WhPo whpo = whPoDao.findPoByCodeAndStore(checkPoCode.getPoCode(), checkPoCode.getStoreId());
            /* 如果找不到则调用po manager插入po表 */
            if (null == whpo) {
                /* 插入po表 */
                rm = poManager.createPoAndLineToInfo(whPo, whPoLines, rm);
            } else {
                throw new BusinessException("已经存在此po单");
            }
        }
        return null;
    }

    @Override
    public ResponseMsg insertPoWithCheckAndOuId(CheckPoCode checkPoCode) {
        CheckPoCode po = checkPoCodeDao.findPoFromPoCheck(checkPoCode);
        ResponseMsg responseMsg = new ResponseMsg();
        if (null != po) {
            /* 存在此po单号 */
            responseMsg.setMsg("yes");
        } else {
            /* 不存在此po单号 */
            int i = checkPoCodeDao.saveOrUpdate(checkPoCode);
            if (0 != i) {
                responseMsg.setMsg("no");
            }
        }
        return responseMsg;
    }
}
