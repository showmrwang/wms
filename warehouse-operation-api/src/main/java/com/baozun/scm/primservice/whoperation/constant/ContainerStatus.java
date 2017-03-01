package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

/**
 * 容器状态常量
 * 
 * @author bin.hu
 * 
 */
public class ContainerStatus implements Serializable {

    private static final long serialVersionUID = -1099470484756047626L;

    /** lifecycle */
    public static final int CONTAINER_LIFECYCLE_FORBIDDEN = 0; // 禁用 forbidden
    public static final int CONTAINER_LIFECYCLE_USABLE = 1; // 可用 usable
    // public static final int CONTAINER_LIFECYCLE_DELETE = 2; // 删除
    public static final int CONTAINER_LIFECYCLE_OCCUPIED = 3; // 占用 occupied


    /** status */
    public static final int CONTAINER_STATUS_FORBIDDEN = 0; // 禁用 forbidden
    public static final int CONTAINER_STATUS_USABLE = 1; // 可用 usable
    public static final int CONTAINER_STATUS_RCVD = 2; // 收货中
    public static final int CONTAINER_STATUS_CAN_PUTAWAY = 3; // 待上架 putaway
    public static final int CONTAINER_STATUS_PUTAWAY = 5; // 上架中
    public static final int CONTAINER_STATUS_SHEVLED = 6; // 已上架/货位中
    public static final int CONTAINER_STATUS_REC_OUTBOUNDBOX = 7; // 出库箱推荐

}
