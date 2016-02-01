package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.command.poasn.PoCheckCommand;
import com.baozun.scm.primservice.whoperation.dao.poasn.CheckPoCodeDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoDao;
import com.baozun.scm.primservice.whoperation.dao.poasn.WhPoLineDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
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
    private WhPoLineDao whPoLineDao;

    @Override
    @MoreDB("infoSource")
    public ResponseMsg insertPoWithCheckWithoutOuId(PoCheckCommand poCheckCommand) {
        CheckPoCode checkPoCode = poCheckCommand.getCheckPoCode();
        WhPo whPo = poCheckCommand.getWhPo();
        List<WhPoLine> whPoLines = poCheckCommand.getWhPoLines();
        ResponseMsg rm = poCheckCommand.getRm();
        /* 校验po单是否在t_wh_po_check中存在 */
        List<CheckPoCode> po = checkPoCodeDao.findListByParam(checkPoCode);
        /* 不存在则在po表中创建 */
        if (po.isEmpty()) {
            /* 创建po */
            Long i = checkPoCodeDao.insert(checkPoCode);
            if (i != 0) {
                rm = this.createPoAndLineToInfo(whPo, whPoLines, rm);
            } else {
                throw new BusinessException(ErrorCodes.SAVE_CHECK_TABLE_FAILED);
            }

        } else {
            /* 从po表中根据extCode和store id查找po单号 */
            long count = whPoDao.findPoByCodeAndStore(checkPoCode.getExtCode(), checkPoCode.getStoreId(), null);
            /* 如果找不到则调用po manager插入po表 */
            if (0 == count) {
                /* 插入po表 */
                rm = this.createPoAndLineToInfo(whPo, whPoLines, rm);
            } else {
                /* po单已经存在 */
                throw new BusinessException(ErrorCodes.PO_EXIST);
            }
        }
        return rm;
    }

    @Override
    @MoreDB("infoSource")
    public boolean insertPoWithCheckAndOuId(CheckPoCode checkPoCode) {
        /**
         * true:不存在此po单号 false:存在此po
         */
        boolean flag = false;
        /* 查找check表中是否有此po单信息 */
        List<CheckPoCode> po = checkPoCodeDao.findListByParam(checkPoCode);
        if (!po.isEmpty()) {
            /* 存在此po单号 */
            flag = true;
        } else {
            /* 不存在此po单号则在check表中插入此po信息 */
            long i = checkPoCodeDao.insert(checkPoCode);
            if (0 != i) {
                /* 插入check表成功 */
                flag = false;
            } else {
                /* 插入check表失败 */
                throw new BusinessException(ErrorCodes.SAVE_CHECK_TABLE_FAILED);
            }
        }
        return flag;
    }

    private ResponseMsg createPoAndLineToInfo(WhPo po, List<WhPoLine> whPoLines, ResponseMsg rm) {
        long i = whPoDao.insert(po);
        if (0 == i) {
            throw new BusinessException(ErrorCodes.SAVE_PO_FAILED);
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
}
