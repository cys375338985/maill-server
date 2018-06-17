package com.mmail.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by cys on 2018/5/25.
 */
public class DateTimeUtil {
    public static final  String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static Date strToDate(String dateTimeStr, String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dataTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dataTime.toDate();
    }
    public static String dateTostr(Date date, String formatStr){
        if(date== null){
            return StringUtils.EMPTY;
        }

        DateTime dataTime = new DateTime(date);
        try{
            dataTime.toString(formatStr);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        return dataTime.toString(formatStr);
    }
    public static Date strToDate(String dateTimeStr){
        return strToDate(dateTimeStr,STANDARD_FORMAT);
    }
    public static String dateTostr(Date date){
        return dateTostr(date,STANDARD_FORMAT);
    }
}
