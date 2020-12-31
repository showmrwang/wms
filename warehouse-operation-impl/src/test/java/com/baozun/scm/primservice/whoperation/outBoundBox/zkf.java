package com.baozun.scm.primservice.whoperation.outBoundBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class zkf {

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String l="2016-04-02 11:15:29";
        Date date=formater.parse(l);
        System.out.println(date);
    }

}
