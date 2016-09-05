package com.baozun.scm.primservice.whoperation.command.odo;

import com.baozun.scm.primservice.whoperation.command.BaseCommand;

public class OdoMergeCommand extends BaseCommand {

    /**
     * 
     */
    private static final long serialVersionUID = -6642649044530173292L;

    /** 原始出库单id */
    private String odoId;

    /** 组织id */
    private Long ouId;

    /** 可合并订单数量 */
    private Long count;

    /** 合并后总计划数量 */
    private Double quantity;

    /** 合并后总金额 */
    private Double sum;

    /** 原始出库单编码 */
    private String originalOdoCode;


    public String getOdoId() {
        return odoId;
    }

    public void setOdoId(String odoId) {
        this.odoId = odoId;
    }

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public String getOriginalOdoCode() {
        return originalOdoCode;
    }

    public void setOriginalOdoCode(String originalOdoCode) {
        this.originalOdoCode = originalOdoCode;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
