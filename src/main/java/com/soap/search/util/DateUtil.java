package com.soap.search.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 得到当前得时间字符串
     * @param format
     * @return
     */
    public static String getCurrentDateStr(String format){
        return getDateStr(new Date(),format);
    }

    /**
     * 得到时间字符串
     * @param date
     * @param format
     * @return
     */
    public static String getDateStr(Date date,String format){
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 格式化日期
     * @param date
     * @param format
     * @return
     */
    public static String getDateStr(long date,String format){
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
