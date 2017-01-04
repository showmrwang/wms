package com.baozun.scm.primservice.whoperation.constant;

import java.io.Serializable;

/**
 * 集货模式
 * @author zhukai
 *
 */
public class WhConcentrationPatternType implements Serializable {

	private static final long serialVersionUID = -198444002125449854L;
	
	/**
     * 集货模式
     */
    public static final int PICKING_CONCENTRATION = 1; 	// 拣货集货
    public static final int OUTBOUND_CONCENTRATION= 2;	// 出库集货
    public static final int MANUAL_CONCENTRATION = 3;	// 人为集货
}
