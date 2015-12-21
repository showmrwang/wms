package com.baozun.scm.primservice.whoperation.constant;

public final class AuthConstants {

    private AuthConstants() {}
    
    /* ----------------------------- 组织类型  ----------------------------------------------- */
    /** 集团类型 */
    public static final String OUTYPE_ROOT = "Root";

    /** DC（物流中心类型） */
    public static final String OUTYPE_OPERATION_CENTER = "OperationCenter";

    /** 逻辑仓库类型 */
    public static final String OUTYPE_WAREHOUSE = "Warehouse";
    
    /* ----------------------------- 权限类型  ----------------------------------------------- */
    /** 1.数据权限 */
    public static final Integer PRIVILEGE_TYPE_DATA = 1;
    /** 数据权限分组名 */
    public static final String DIC_PRIVILEGE_DATA = "PRIVILEGE_DATA";
    
    /** 2.功能权限 */
    public static final Integer PRIVILEGE_TYPE_OP = 2;
    /** 功能权限分组名 */
    public static final String DIC_PRIVILEGE_OP = "PRIVILEGE_OP";

    /* ----------------------------- 基础权限类型  ----------------------------------------------- */
    /** 查看 */
    public static final String P_FUNCTION_TYPE_VIEW = "view";
    /** 新增 */
    public static final String P_FUNCTION_TYPE_SAVE = "add";
    /** 修改 */
    public static final String P_FUNCTION_TYPE_UPDATE = "update";
    /** 删除 */
    public static final String P_FUNCTION_TYPE_DELETE = "remove";
    /** 执行 */
    public static final String P_FUNCTION_TYPE_OPERATE = "operate";
    /** 配置 */
    public static final String P_FUNCTION_TYPE_CONFIG = "config";
}
