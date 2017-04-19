/**
 * Copyright (c) 2013 Baozun All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Baozun. You shall not disclose
 * such Confidential Information and shall use it only in accordance with the terms of the license
 * agreement you entered into with Baozun.
 *
 * BAOZUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BAOZUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 *
 */
package com.baozun.scm.primservice.whoperation.manager.pda.inbound.putaway;

import java.util.Comparator;

import org.springframework.util.StringUtils;

import com.baozun.scm.primservice.whoperation.model.warehouse.Location;

/**
 * @author lichuan
 *
 */
public class LocationShelfSorter implements Comparator<Location> {
    /**
     * 上架顺序排序
     * 
     * @author lichuan
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(Location o1, Location o2) {
        Location loc1 = o1;
        Location loc2 = o2;
        if (StringUtils.isEmpty(loc1.getShelfSort())) {
            loc1.setShelfSort(loc1.getCode());
        }
        if (StringUtils.isEmpty(loc2.getShelfSort())) {
            loc2.setShelfSort(loc2.getCode());
        }
        return loc1.getShelfSort().compareTo(loc2.getShelfSort());
    }
}
