package com.shulianxunying.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by SuChang on 2017/3/20 14:09.
 */
public class DateUtils {

    // 获取当前时间所在年的周数
    public static int getWeekOfYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);

        return c.get(Calendar.WEEK_OF_YEAR);
    }

    // 获取当前时间所在年的最大周数
    public static int getMaxWeekNumOfYear(int year) {
        Calendar c = new GregorianCalendar();
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);

        return getWeekOfYear(c.getTime());
    }

    // 获取某年的第几周的开始日期
    public static Date getFirstDayOfWeek(int year, int week) {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);

        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, week * 7);

        return getFirstDayOfWeek(cal.getTime());
    }

    // 获取某年的第几周的结束日期
    public static Date getLastDayOfWeek(int year, int week) {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);

        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, week * 7);

        return getLastDayOfWeek(cal.getTime());
    }


    // 获取当前时间所在周的开始日期
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return c.getTime();
    }

    // 获取当前时间所在周的结束日期
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return c.getTime();
    }

    public static Date getLastDayOfMonth(Date date) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.MONTH, -1);
        return c.getTime();
    }

    /**
     * 最近一个月
     * @param date
     * @return
     */
    public static Date[] getLastMonthFirstDayAndLastDay(Date date) {
        Date[] out = new Date[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        int actualMinimum = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //得到月初
        calendar.set(Calendar.DAY_OF_MONTH, actualMaximum);
        out[1] = calendar.getTime();
        //得到月末
        calendar.set(Calendar.DAY_OF_MONTH, actualMinimum);
        out[0] = calendar.getTime();
        return out;
    }

    /**
     * 最近一个季度
     * @param date
     * @return
     */
    public static Date[] getLastQuarterFirstDayAndLastDay(Date date) {
        Date[] out = new Date[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, actualMaximum);
        out[0] = calendar.getTime();

        calendar.add(Calendar.MONTH, -2);
        int actualMinimum = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, actualMinimum);
        out[1] = calendar.getTime();
        return out;
    }

    public static Date getLastMonthLastWeek(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        System.out.println(sdf.format(calendar.getTime()));
        System.out.println(calendar.get(Calendar.WEEK_OF_YEAR));
//        calendar.add(Calendar.MONTH,-1);
        System.out.println(sdf.format(calendar.getTime()));
        System.out.println(calendar.get(Calendar.WEEK_OF_YEAR));
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH,actualMaximum);
        System.out.println(sdf.format(calendar.getTime()));
        System.out.println(calendar.get(Calendar.WEEK_OF_YEAR));
        System.out.println(sdf.format(getFirstDayOfWeek(2017,calendar.get(Calendar.WEEK_OF_YEAR)-1)));
        System.out.println(sdf.format(getLastDayOfWeek(2017,calendar.get(Calendar.WEEK_OF_YEAR)-1)));
        return date;
    }



    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date[] dates = getLastQuarterFirstDayAndLastDay(new Date());
//        System.out.println(sdf.format(dates[0]));
//        System.out.println(sdf.format(dates[1]));
//        getLastMonthLastWeek(new Date());
        System.out.println(sdf.format(getFirstDayOfWeek(2017,1)));
        System.out.println(sdf.format(getLastDayOfWeek(2017,1)));
    }
}
