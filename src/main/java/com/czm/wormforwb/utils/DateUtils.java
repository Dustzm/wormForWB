package com.czm.wormforwb.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 * @author Slience
 * @date 2022/3/10 15:46
 **/
@Slf4j
public class DateUtils {

    private static final SimpleDateFormat strFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private static final SimpleDateFormat normalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

    private static final SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd");

    private static final SimpleDateFormat logDBFormat = new SimpleDateFormat("yyyyMM");

    /**
     * 中国标准时间转换为常用时间格式
     * @author Slience
     * @date 2022/3/10 15:46
     **/
    public static String convertNormalDateToPattern(String normalDate) {
        try {
            Date date = normalFormat.parse(normalDate);
            return strFormat.format(date);
        } catch (ParseException e) {
            log.error("时间转换抛出异常：" + e.getMessage() + "\n" + e.getStackTrace());
            return null;
        }
    }

    /**
     * 获取当前日期作为日志文件夹名
     * @return String 当前日期
     **/
    public static String getNowDateForFile(){
        Date today = new Date();
        return fileFormat.format(today);
    }

    public static String getYesterDayForFile(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,-1);
        return fileFormat.format(calendar.getTime());
    }

    /**
     * 获取当前月份作为日志表分表名
     * @return String 当前日期
     **/
    public static String getNowMonthForLogDB(){
        return logDBFormat.format(new Date());
    }

    /**
     * 获取次月日期作为日志分表名
     * @return String 次月日期
     **/
    public static String getNextMonthForLogDB(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH,1);
        return logDBFormat.format(calendar.getTime());
    }

    /**
     * 获取当月第一天
     **/
    public static String getFirstDayThisMonth(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        return fileFormat.format(c.getTime());
    }

    /**
     * 获取上个月时间
     **/
    public static String getLastMonthForDB(){
        Calendar cale = Calendar.getInstance();
        cale.set(Calendar.DAY_OF_MONTH,0);//设置为1号,当前日期既为本月第一天
        return logDBFormat.format(cale.getTime());
    }

    public static String getNowDate(){
        Calendar cale = Calendar.getInstance();
        cale.setTime(new Date());//设置为1号,当前日期既为本月第一天
        return fileFormat.format(cale.getTime());
    }

}
