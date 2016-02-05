package com.baozun.scm.primservice.whoperation.manager.poasn.poasnmanager;

import java.util.List;

import lark.common.annotation.MoreDB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baozun.scm.primservice.whoperation.dao.poasn.CheckAsnCodeDao;
import com.baozun.scm.primservice.whoperation.exception.BusinessException;
import com.baozun.scm.primservice.whoperation.exception.ErrorCodes;
import com.baozun.scm.primservice.whoperation.model.poasn.CheckAsnCode;

@Service("asnCheckManager")
@Transactional
public class AsnCheckManagerImpl implements AsnCheckManager {

    @Autowired
    private CheckAsnCodeDao checkAsnCodeDao;

    // @Override
    // @MoreDB("infoSource")
    // public ResponseMsg insertAsnWithCheckWithoutOuId(AsnCheckCommand asnCheckCommand) {
    // CheckAsnCode checkAsnCode = asnCheckCommand.getCheckAsnCode();
    // WhAsn whAsn = asnCheckCommand.getWhAsn();
    // List<WhAsnLine> whAsnLines = asnCheckCommand.getWhAsnLines();
    // ResponseMsg rm = asnCheckCommand.getRm();
    // /* 校验asn单是否在t_wh_asn_check中存在 */
    // List<CheckAsnCode> asn = checkAsnCodeDao.findListByParam(checkAsnCode);
    // /* 不存在则在asn中间表中创建 */
    // if (asn.isEmpty()) {
    // /* 中间表创建asn */
    // Long i = checkAsnCodeDao.insert(checkAsnCode);
    // if (i != 0) {
    // /* asn表创建asn */
    // rm = this.createAsnAndLineToInfo(whAsn, whAsnLines, rm);
    // } else {
    // throw new BusinessException(ErrorCodes.SAVE_CHECK_TABLE_FAILED);
    // }
    //
    // } else {
    // /* 从asn表中根据extCode和store id查找asn单号 */
    // long count = whAsnDao.findAsnByCodeAndStore(checkAsnCode.getAsnExtCode(),
    // checkAsnCode.getStoreId(), null);
    // /* 如果找不到则调用asn manager插入asn表 */
    // if (0 == count) {
    // /* 插入asn表 */
    // rm = this.createAsnAndLineToInfo(whAsn, whAsnLines, rm);
    // } else {
    // /* asn单已经存在 */
    // throw new BusinessException(ErrorCodes.PO_EXIST);
    // }
    // }
    // return rm;
    // }

    @Override
    @MoreDB("infoSource")
    public boolean insertAsnWithCheckAndOuId(CheckAsnCode checkAsnCode) {
        /**
         * true:不存在此asn单号 false:存在此asn
         */
        boolean flag = false;
        /* 查找check表中是否有此asn单信息 */
        List<CheckAsnCode> asn = checkAsnCodeDao.findListByParam(checkAsnCode);
        if (!asn.isEmpty()) {
            /* 存在此asn单号 */
            flag = true;
        } else {
            /* 不存在此asn单号则在check表中插入此asn信息 */
            long i = checkAsnCodeDao.insert(checkAsnCode);
            if (1 == i) {
                /* 插入check表成功 */
                flag = false;
            } else {
                /* 插入check表失败 */
                throw new BusinessException(ErrorCodes.SAVE_CHECK_TABLE_FAILED_ASN);
            }
        }
        return flag;
    }

    // private ResponseMsg createAsnAndLineToInfo(WhAsn asn, List<WhAsnLine> whAsnLines, ResponseMsg
    // rm) {
    // long i = whAsnDao.insert(asn);
    // if (0 == i) {
    // throw new BusinessException(ErrorCodes.SAVE_PO_FAILED);
    // }
    // if (whAsnLines.size() > 0) {
    // // 有line信息保存
    // for (WhAsnLine whAsnLine : whAsnLines) {
    // whAsnLine.setAsnId(asn.getId());
    // whAsnLineDao.insert(whAsnLine);
    // }
    // }
    // rm.setResponseStatus(ResponseMsg.STATUS_SUCCESS);
    // rm.setMsg(asn.getId() + "");
    // return rm;
    // }
}
