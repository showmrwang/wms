package com.baozun.scm.primservice.whoperation.command.pda.rcvd;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;
import com.baozun.scm.primservice.whoperation.command.sku.SkuRedisCommand;
import com.baozun.scm.primservice.whoperation.command.warehouse.inventory.WhSkuInventoryCommand;
import com.baozun.scm.primservice.whoperation.constant.Constants;
import com.baozun.scm.primservice.whoperation.model.sku.SkuExtattr;
import com.baozun.scm.primservice.whoperation.model.sku.SkuMgmt;

public class RcvdWorkFlow extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 7510242835768371764L;


    // enum:通用收货商品属性扫描序列
    public static final int GENERAL_RECEIVING_START = -1;// 开始
    public static final int GENERAL_RECEIVING_END = 99;// 结束
    public static final int GENERAL_RECEIVING_ISVALID = 0;// 0：是否管理效期
    public static final int GENERAL_RECEIVING_ISBATCHNO = 1; // 1:是否管理批次号
    public static final int GENERAL_RECEIVING_ISCOUNTRYOFORIGIN = 2;// 2:是否管理原产地
    public static final int GENERAL_RECEIVING_ISINVTYPE = 3; // 3:是否管理库存类型
    public static final int GENERAL_RECEIVING_INVATTR1 = 4; // 4:是否管理库存属性1
    public static final int GENERAL_RECEIVING_INVATTR2 = 5; // 5:是否管理库存属性2
    public static final int GENERAL_RECEIVING_INVATTR3 = 6;// 6:是否管理库存属性3
    public static final int GENERAL_RECEIVING_INVATTR4 = 7; // 7:是否管理库存属性4
    public static final int GENERAL_RECEIVING_INVATTR5 = 8;// 8:是否管理库存属性5
    public static final int GENERAL_RECEIVING_ISINVSTATUS = 9; // 9:是否管理库存状态
    public static final int GENERAL_RECEIVING_ISDEFEAT = 10;// 10:残次品类型及残次原因
    public static final int GENERAL_RECEIVING_ISSERIALNUMBER = 11;// 11:是否管理序列号



    /**
     * 生成扫描商品的指针列表
     * 
     * @param sku
     * @return
     */
    public static String getOptMapStr(SkuRedisCommand sku) {
        // 指针列表：
        // 0：是否管理效期
        // 1:是否管理批次号
        // 2:是否管理原产地
        // 3:是否管理库存类型
        // 4:是否管理库存属性1
        // 5:是否管理库存属性2
        // 6:是否管理库存属性3
        // 7:是否管理库存属性4
        // 8:是否管理库存属性5
        // 9:是否管理库存状态
        // 10:残次品类型及残次原因
        // 11:是否管理序列号
        char[] list = new char[12];
        SkuMgmt mgt = sku.getSkuMgmt();
        list[RcvdWorkFlow.GENERAL_RECEIVING_ISINVSTATUS] = '1';
        list[RcvdWorkFlow.GENERAL_RECEIVING_ISDEFEAT] = '1';
        if (mgt != null) {
            list[RcvdWorkFlow.GENERAL_RECEIVING_ISVALID] = null != mgt.getIsValid() && mgt.getIsValid() ? '1' : '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_ISBATCHNO] = null != mgt.getIsBatchNo() && mgt.getIsBatchNo() ? '1' : '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_ISCOUNTRYOFORIGIN] = null != mgt.getIsCountryOfOrigin() && mgt.getIsCountryOfOrigin() ? '1' : '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_ISINVTYPE] = null != mgt.getIsInvType() && mgt.getIsInvType() ? '1' : '0';
            if (null == mgt.getSerialNumberType() || Constants.SERIAL_NUMBER_TYPE_OUT.equals(mgt.getSerialNumberType()) || Constants.SERIAL_NUMBER_TYPE_ALL_NOT.equals(mgt.getSerialNumberType())) {
                list[RcvdWorkFlow.GENERAL_RECEIVING_ISSERIALNUMBER] = '0';
            } else {
                list[RcvdWorkFlow.GENERAL_RECEIVING_ISSERIALNUMBER] = '1';

            }

        } else {
            list[RcvdWorkFlow.GENERAL_RECEIVING_ISVALID] = '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_ISBATCHNO] = '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_ISCOUNTRYOFORIGIN] = '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_ISINVTYPE] = '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_ISSERIALNUMBER] = '0';
        }
        SkuExtattr attr = sku.getSkuExtattr();
        if (attr != null) {
            if (null == attr.getInvAttr1()) {
                list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR1] = '0';
            } else {
                list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR1] = attr.getInvAttr1() ? '1' : '0';
            }
            if (null == attr.getInvAttr2()) {
                list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR2] = '0';
            } else {
                list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR2] = attr.getInvAttr2() ? '1' : '0';
            }
            if (null == attr.getInvAttr3()) {
                list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR3] = '0';
            } else {
                list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR3] = attr.getInvAttr3() ? '1' : '0';
            }
            if (null == attr.getInvAttr4()) {
                list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR4] = '0';
            } else {
                list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR4] = attr.getInvAttr4() ? '1' : '0';
            }
            if (null == attr.getInvAttr5()) {
                list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR5] = '0';
            } else {
                list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR5] = attr.getInvAttr5() ? '1' : '0';
            }
        } else {
            list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR1] = '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR2] = '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR3] = '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR4] = '0';
            list[RcvdWorkFlow.GENERAL_RECEIVING_INVATTR5] = '0';
        }
        return String.copyValueOf(list);
    }



    public static Integer getNextSkuAttrOperatorForScanning(WhSkuInventoryCommand command) {
        char[] optCharArray = command.getSkuUrl().toCharArray();
        Integer nextOpt = RcvdWorkFlow.getNextOperator(command.getSkuUrlOperator(), optCharArray);
        if (RcvdWorkFlow.GENERAL_RECEIVING_ISINVSTATUS == command.getSkuUrlOperator() && !(Constants.INVENTORY_STATUS_DEFEATSALE == command.getInvStatus() || Constants.INVENTORY_STATUS_DEFEATNOTSALE == command.getInvStatus())) {
            nextOpt = RcvdWorkFlow.getNextOperator(nextOpt, optCharArray);
        }
        if (RcvdWorkFlow.GENERAL_RECEIVING_END == nextOpt) {
            nextOpt = command.getSkuUrlOperator();
        }
        return nextOpt;
    }

    /**
     * 获取下一个
     * 
     * @param operator
     * @param optMapList
     * @return
     */
    private static Integer getNextOperator(Integer operator, char[] optMapList) {
        operator = null == operator ? RcvdWorkFlow.GENERAL_RECEIVING_START : operator;
        int nextOpt = operatorCursor(operator);
        if (nextOpt == RcvdWorkFlow.GENERAL_RECEIVING_START || RcvdWorkFlow.GENERAL_RECEIVING_END == nextOpt || '1' == optMapList[nextOpt]) {
            return nextOpt;
        }
        return getNextOperator(nextOpt, optMapList);
    }

    private static int operatorCursor(int operator) {
        int nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_START;
        switch (operator) {
            case RcvdWorkFlow.GENERAL_RECEIVING_START:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_ISVALID;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISVALID:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_ISBATCHNO;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISBATCHNO:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_ISCOUNTRYOFORIGIN;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISCOUNTRYOFORIGIN:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_ISINVTYPE;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISINVTYPE:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_INVATTR1;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR1:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_INVATTR2;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR2:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_INVATTR3;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR3:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_INVATTR4;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR4:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_INVATTR5;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_INVATTR5:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_ISINVSTATUS;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISINVSTATUS:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_ISDEFEAT;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISDEFEAT:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_ISSERIALNUMBER;
                break;
            case RcvdWorkFlow.GENERAL_RECEIVING_ISSERIALNUMBER:
                nextCursor = RcvdWorkFlow.GENERAL_RECEIVING_END;
                break;
        }
        return nextCursor;
    }

}
