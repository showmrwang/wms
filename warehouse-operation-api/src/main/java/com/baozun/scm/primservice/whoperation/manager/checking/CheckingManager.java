package com.baozun.scm.primservice.whoperation.manager.checking;


import com.baozun.scm.primservice.whoperation.manager.BaseManager;

public interface CheckingManager extends BaseManager {
    
    /**
     * 打印装箱清单
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    void printPackingList(Long userId, Long ouId);
    
    /**
     * 打印销售清单
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    void printSalesList(Long userId, Long ouId);
    
    /**
     * 打印面单
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    void printSinglePlane(Long userId, Long ouId);
    
    /**
     * 打印箱标签
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    void printBoxLabel(Long userId, Long ouId);
    
    /**
     * 打印发票（复核）
     * 
     * @param userId 用户Id
     * @param ouId 仓库Id
     */
    void printInvoiceReview(Long userId, Long ouId);
    
}
