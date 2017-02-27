package com.baozun.scm.primservice.whoperation.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
	
	/**
	 * 把集合分割成指定size大小一组
	 * @param list
	 * @param size
	 * @return
	 */
	public static <T> List<List<T>> getSubList(List<T> list, int size) {
		List<List<T>> listArray = new ArrayList<List<T>>();
		int listSize = list.size();
        int pageSize = size;
        if (listSize <= pageSize) {
        	listArray.add(list);
        	return listArray;
		}
        int page = (listSize + (pageSize - 1)) / pageSize;
        for (int i = 0; i < page; i++) {
            List<T> subList = new ArrayList<T>();
            for (int j = 0; j < listSize; j++) {
                int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize;
                if (pageIndex == (i + 1)) {
                    subList.add(list.get(j));
                }
                if ((j + 1) == ((j + 1) * pageSize)) {
                    break;
                }
            }
            listArray.add(subList);
        }
		return listArray;
	}
	
}
