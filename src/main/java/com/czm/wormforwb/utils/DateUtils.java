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

    private static final SimpleDateFormat strFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

    private static final SimpleDateFormat normalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

    private static final SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd");

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
    public static String getNowDate(){
        Date today = new Date();
        return fileFormat.format(today);
    }
}
