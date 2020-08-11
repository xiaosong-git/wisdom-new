package com.xdream.wisdom.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.alibaba.druid.util.StringUtils;

/**
 * 时间的处理
 * @Author Linyb
 * @Date 2016/12/14.
 */
public class DateUtil {

    /**
     * 把指定的时间转化成默认格式的时间字符串
     *
     * @Author Linyb
     * @Date 2016/12/14 10:26
     */
    public static String dateFormatDefaul(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static Integer getAgeByBirthday(String date) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date mydate = null;
        try {
            mydate = myFormatter.parse(date);
        } catch (ParseException e) {
        }
        Calendar cal = Calendar.getInstance();
        if (cal.before(mydate)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(mydate);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }
        return age;
    }

    public static Date changeToDate(String date) {
        String d = date.substring(0, date.length() - 2);
        String str = date.substring(date.length() - 3, date.length()).trim();
        Date mydate = null;
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            mydate = myFormatter.parse(d);
            if ("pm".equals(str)) {
                Long mytime = mydate.getTime();
                mytime += 12 * 60 * 60 * 1000;
                mydate.setTime(mytime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mydate;
    }
    public static int minutesBetween(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date d1 = sdf.parse(startDate);
            Date d2 = sdf.parse(endDate);
            return (int) (d2.getTime() - d1.getTime()) / 1000 / 60;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /***
     * 
     * @return HH:mm:ss
     */
    public static String getCurTime(){
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }
/***
 * 获取 yyyy-MM-dd
 * @return
 */
    public static String getCurDate() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }
    public static String getCurrentDateTime(String type){
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat(type);
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }
/***
 * 获取 yyyy-MM-dd HH:mm
 * @return
 */
    public static String getSystemTime() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }
    /**
     * yyyy-MM-dd HH:mm:SS
     * @return
     */
    public static String getSystemDateTime() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }
    public static String getSystemDateTime2() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:SSS");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }
    /**
     *获取 yyyyMMdd  时间
     * @return
     */
    public static String getSystemTimeEight() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }
    /**
     *获取 yyyyMMddHH  时间
     * @return
     */
    public static String getSystemTimeTen() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHH");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }
    /**
     *获取 yyyyMMddHHmm  时间
     * @return
     */
    public static String getSystemTimeTwelve() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }
    /**
     *获取yyyyMMddHHmmss  时间
     * @return
     */
    public static String getSystemTimeFourteen() {
        String returnStr = null;
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        returnStr = f.format(date);
        return returnStr;
    }
    /**
     * 获取当月第一天
     *
     * @return
     */
    public static String getCurMonthFirstDay() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String first = f.format(c.getTime());
        return first;
    }

    /**
     * 获取当月最后一天
     *
     * @return
     */
    public static String getCurMonthLastDay() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = f.format(ca.getTime());
        return last;
    }

    /**
     * 获取银行的账单日、还款日（例如输入20,10 → 2017-05-20, 2017-06-10）
     *
     * @param bday
     * @param pday
     * @return
     */
    public static String[] changeToBillDateAndRepayDate(Integer bday, Integer pday) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String bill_date = "";
        String repay_date = "";
        String[] result = new String[2];
        Calendar cnow = Calendar.getInstance();
        int now = cnow.get(Calendar.DAY_OF_MONTH);//当前日期
        if (bday < pday) {// 同月
            Calendar tem = Calendar.getInstance();
            tem.set(Calendar.DAY_OF_MONTH, bday);
            bill_date = sdf.format(tem.getTime());
            tem.set(Calendar.DAY_OF_MONTH, pday);
            repay_date = sdf.format(tem.getTime());
            result[0] = bill_date;
            result[1] = repay_date;
        } else {// 不同月
            Calendar cbdate = Calendar.getInstance();
            cbdate.setTime(cnow.getTime());
            Calendar cpdate = Calendar.getInstance();
            cpdate.setTime(cnow.getTime());
            if (now < pday) {// 上个月
                cbdate.set(Calendar.DAY_OF_MONTH, bday);
                cbdate.add(Calendar.MONTH, -1);// 加一个月
                cpdate.set(Calendar.DAY_OF_MONTH, pday);
            } else {
                cbdate.set(Calendar.DAY_OF_MONTH, bday);
                cpdate.set(Calendar.DAY_OF_MONTH, pday);
                cpdate.add(Calendar.MONTH, 1);// 加一个月
            }
//            if (cnow.after(cbdate) && cnow.before(cpdate)) {
                bill_date = sdf.format(cbdate.getTime());
                repay_date = sdf.format(cpdate.getTime());
                result[0] = bill_date;
                result[1] = repay_date;
//            } else {
//                return null;
//            }
        }
        return result;
    }

    //计算两个时间之间间隔的天数
    public static int daysBetween(String startdate, String enddate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        long time1 = 0;
        long time2 = 0;

        try {
            cal.setTime(sdf.parse(startdate));
            time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(enddate));
            time2 = cal.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 作用：将时间加上一定的分钟数
     * @param startTime
     * @param minutes
     * @return
     */
    public static String addMinute(String startTime,long minutes){
        String result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if(!StringUtils.isEmpty(startTime)){
                Date date = sdf.parse(startTime);
                Date resultDate = new Date((date.getTime()+minutes*60*1000));
                result = sdf.format(resultDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }
    /**
     * 十天前的时间
     * @return
     */
    public static String getTenDaysAgo(){
    	Calendar calendar1 = Calendar.getInstance();
    	   calendar1.add(Calendar.DATE, -10);
    	  
    	  SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    	
    	  String ten_days_ago = sdf1.format(calendar1.getTime());
    	 
    	  return ten_days_ago;
    }
    

    public static void main(String[] args) {
//        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
//        //获取上月第一天
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
//        c.add(Calendar.MONTH, -1);// 减一个月
//        String first = f.format(c.getTime());
//        //获取上月最后一天
//        Calendar ca = Calendar.getInstance();
//        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
//        ca.add(Calendar.MONTH, -1);// 减一个月
//        String last = f.format(ca.getTime());
//        System.out.println(first+"--"+last);
    	  String c=	getSystemDateTime2();
    String a=	getSystemDateTime();
    String b=	getSystemDateTime2();
    
    System.out.println(c);
    System.out.println(a);
    System.out.println(b);
 
    }
}
