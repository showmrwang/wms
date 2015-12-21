package com.baozun.scm.primservice.whoperation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 根据维度要求生成批量库位编码 条码/补货条码
 * 
 * @author bin.hu
 * 
 */
public class LocationCodeProcess {

    /**
     * 通过1-8个维度生成对应库位编码
     * 
     * @param dimension 维度/增量 格式:"起始-结束,增量"
     * @param splitMark 分隔符
     * @param number 维度数量
     * @return
     */
    public static List<String> formatAll(List<String> dimension, String splitMark, Integer number) {
        Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
        int count = 1;
        for (String s : dimension) {
            List<String> list = formatSingle(s.split(",")[0], s.split(",")[1]);
            map.put(count, list);
            count++;
        }
        List<String> valueList = returnList(map, splitMark, number);
        return valueList;
    }


    /**
     * 通过维度算出CODE条目数
     * 
     * @param dimension
     * @return
     */
    public static Integer getCodeListSize(List<String> dimension) {
        int count = 0;
        for (String s : dimension) {
            List<String> list = formatSingle(s.split(",")[0], s.split(",")[1]);
            count += count + list.size();
        }
        return count;
    }

    /**
     * 通过库位编码生成库位条码/补货条码
     * 
     * @param code 库位编码
     * @param type 1:条码 2:补货条码
     * @return
     */
    public static String getReplenishmentOrBarcode(String code, Integer type) {
        int count = 11;
        String returnString = "";
        String hashValue = String.valueOf(JSHash(code));// 首先CODE进行1次hash算法
        int hashSize = hashValue.length();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(hashValue);
        for (int i = 0; i < (count - hashSize); i++) {
            // 如果不够11位进行补全
            // 随机位置插入缺失数字0-9
            hashValue = stringBuffer.insert(Integer.parseInt(getLttersOrNumber(false, hashValue.length())), getLttersOrNumber(false, 10)).toString();
        }
        // 拼接完进行36进制处理
        String thirtySix = toThirtySixSystem(hashValue);
        stringBuffer.delete(0, stringBuffer.length());
        stringBuffer.append(thirtySix);
        // 36进制出来,根据类型判定补一位字母/数字条码补全英文 补货条码补全数字
        if (type == 1) {
            // 条码
            returnString = stringBuffer.insert(Integer.parseInt(getLttersOrNumber(false, thirtySix.length())), getLttersOrNumber(true, 10)).toString();
        } else {
            // 补货条码
            returnString = stringBuffer.insert(Integer.parseInt(getLttersOrNumber(false, thirtySix.length())), getLttersOrNumber(false, 10)).toString();
        }
        if (returnString.length() < 9) {
            // 如果不够9位进行补全
            for (int i = 0; i < (9 - returnString.length()); i++) {
                // 随机位置插入缺失字母a-z
                returnString = stringBuffer.insert(Integer.parseInt(getLttersOrNumber(false, returnString.length())), getLttersOrNumber(true, 10)).toString();
            }
        }
        return returnString;
    }

    /**
     * 将10进制的字符串、数字，转成36进制的字符串
     * 
     * @param obj
     * @return
     */
    public static String toThirtySixSystem(Object obj) {
        if (obj instanceof String) {
            Long result = Long.parseLong((String) obj);
            return Long.toString(result, 36);
        } else if (obj instanceof Integer) {
            return Integer.toString((Integer) obj, 36);
        } else if (obj instanceof Long) {
            return Long.toString((Long) obj, 36);
        }
        return null;
    }

    /**
     * hash算法
     * 
     * @param str
     * @return
     */
    public static int JSHash(String str) {
        int hash = 1315423911;
        for (int i = 0; i < str.length(); i++) {
            hash ^= ((hash << 5) + str.charAt(i) + (hash >> 2));
        }
        return (hash & 0x7FFFFFFF);
    }

    /**
     * 随机获得A-Z字母0-9数字
     * 
     * @check true字母 false数字
     * @return
     */
    private static String getLttersOrNumber(boolean check, Integer number) {
        String str = "";
        Random random = new Random();
        if (check) { // 字符串A-Z
            // int choice = random.nextBoolean() ? 65 : 97; 取得65大写字母还是97小写字母
            str = String.valueOf((char) (97 + random.nextInt(26)));// 取得小写字母
        } else { // 数字
            str = String.valueOf(random.nextInt(number));
        }
        return str;
    }


    /**
     * 根据维度起始/增量规则返回维度批量编码
     * 
     * @param from 开始结束字母/数字
     * @param increment 对应增量
     * @return
     */
    private static List<String> formatSingle(String fromValue, String increment) {
        /**
         * 暂时只提供1个维度4个字符
         */
        String from = fromValue.split("-")[0];
        String to = fromValue.split("-")[1];
        int size = from.length();
        String f = from.substring(0, 1);// 起始
        String t = to.substring(0, 1);// 结束
        String inc = increment.substring(0, 1);
        List<String> valueList = new ArrayList<String>();
        List<String> returnList = formatValue(f, t, Integer.parseInt(inc));
        for (String first : returnList) {
            if (size >= 2) {
                // 如果起始长度大于2的话继续嵌套
                f = from.substring(1, 2);// 起始
                t = to.substring(1, 2);// 结束
                inc = increment.substring(1, 2);
                List<String> returnList1 = formatValue(f, t, Integer.parseInt(inc));
                for (String second : returnList1) {
                    if (size >= 3) {
                        // 如果起始长度大于3的话继续嵌套
                        f = from.substring(2, 3);// 起始
                        t = to.substring(2, 3);// 结束
                        inc = increment.substring(2, 3);
                        List<String> returnList2 = formatValue(f, t, Integer.parseInt(inc));
                        for (String third : returnList2) {
                            if (size >= 4) {
                                // 如果起始长度==4的话继续嵌套
                                f = from.substring(3, size);// 起始
                                t = to.substring(3, size);// 结束
                                inc = increment.substring(3, size);
                                List<String> returnList3 = formatValue(f, t, Integer.parseInt(inc));
                                for (String fourth : returnList3) {
                                    valueList.add(first + second + third + fourth);
                                }
                            }
                            if (size == 3) {
                                valueList.add(first + second + third);
                            }
                        }
                    }
                    if (size == 2) {
                        valueList.add(first + second);
                    }
                }
            }
            if (size == 1) {
                valueList.add(first);
            }
        }
        return valueList;
    }

    /**
     * 通过开始/结束/增量得到对应数据
     * 
     * @param from
     * @param to
     * @param increment
     * @return
     */
    private static List<String> formatValue(String from, String to, int increment) {
        char s = from.charAt(0);// 起始
        char e = to.charAt(0);// 结束
        int ascii = (int) s;// 转换成ascii
        List<String> returnList = new ArrayList<String>();
        if (ascii >= 65 && ascii <= 90) {
            // A-Z
            int inc = 0;
            // 字母最多循环26次
            for (int i = 1; i <= 26; i++) {
                char value;
                int a = (int) s;// 开始字母ascii
                value = (char) (a + inc);
                inc = inc + increment;// 每次累加增量
                // 如果当前字母大于结束字母结束循环
                if ((int) value > (int) e) {
                    break;
                }
                returnList.add(String.valueOf(value));
            }
        } else {
            // 0-9
            int inc = 0;
            int f = Integer.parseInt(from);// 开始数字
            int t = Integer.parseInt(to);// 结束数字
            for (int i = 1; i <= 9; i++) {
                int value = f + inc;
                inc = inc + increment;// 每次累加增量
                // 如果数字大于结束数字结束循环
                if (value > t) {
                    break;
                }
                returnList.add(String.valueOf(value));
            }
        }
        return returnList;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    private static List<String> returnList(Map<Integer, List<String>> map, String splitMark, Integer number) {
        List<String> valueList = new ArrayList<String>();
        /**
         * 暂时只有8个维度,嵌套循环拼接库位
         */
        List<String> returnList = map.get(1);
        for (String first : returnList) {
            if (number >= 2) {
                List<String> returnList2 = map.get(2);
                for (String second : returnList2) {
                    if (number >= 3) {
                        List<String> returnList3 = map.get(3);
                        for (String third : returnList3) {
                            if (number >= 4) {
                                List<String> returnList4 = map.get(4);
                                for (String fourth : returnList4) {
                                    if (number >= 5) {
                                        List<String> returnList5 = map.get(5);
                                        for (String fifth : returnList5) {
                                            if (number >= 6) {
                                                List<String> returnList6 = map.get(6);
                                                for (String sixth : returnList6) {
                                                    if (number >= 7) {
                                                        List<String> returnList7 = map.get(7);
                                                        for (String seventh : returnList7) {
                                                            if (number >= 8) {
                                                                List<String> returnList8 = map.get(8);
                                                                for (String eighth : returnList8) {
                                                                    valueList.add(first + splitMark + second + splitMark + third + splitMark + fourth + splitMark + fifth + splitMark + sixth + splitMark + seventh + splitMark + eighth);
                                                                }
                                                            }
                                                            if (number == 7) {
                                                                valueList.add(first + splitMark + second + splitMark + third + splitMark + fourth + splitMark + fifth + splitMark + sixth + splitMark + seventh);
                                                            }
                                                            if (number == 7) {
                                                                valueList.add(first + splitMark + second + splitMark + third + splitMark + fourth + splitMark + fifth + splitMark + sixth + splitMark + seventh);
                                                            }
                                                        }
                                                    }
                                                    if (number == 6) {
                                                        valueList.add(first + splitMark + second + splitMark + third + splitMark + fourth + splitMark + fifth + splitMark + sixth);
                                                    }
                                                }
                                            }
                                            if (number == 5) {
                                                valueList.add(first + splitMark + second + splitMark + third + splitMark + fourth + splitMark + fifth);
                                            }
                                        }
                                    }
                                    if (number == 4) {
                                        valueList.add(first + splitMark + second + splitMark + third + splitMark + fourth);
                                    }
                                }
                            }
                            if (number == 3) {
                                valueList.add(first + splitMark + second + splitMark + third);
                            }
                        }
                    }
                    if (number == 2) {
                        valueList.add(first + splitMark + second);
                    }
                }
            }
            if (number == 1) {
                valueList.add(first);
            }
        }
        return valueList;
    }

}
