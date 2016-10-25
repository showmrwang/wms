package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

public class WaveStatus implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2846580922409488154L;

    public static final int WAVE_NEW = 1;// 新建；已创建
    public static final int WAVE_EXECUTING = 5;// 运行中
    public static final int WAVE_EXECUTED = 10;// 运行完成
    public static final int WAVE_RELEASE = 15;// 释放
    public static final int WAVE_CANCEL = 17;// 取消
    public static final int WAVE_CLOSE = 20;// 关闭
}
