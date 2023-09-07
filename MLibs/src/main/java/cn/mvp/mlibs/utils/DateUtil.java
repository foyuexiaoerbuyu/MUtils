package cn.mvp.mlibs.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import cn.mvp.mlibs.log.Log;
import cn.mvp.mlibs.log.XLogUtil;

/**
 * `kk:mm`和`HH:mm`是两种不同的时间格式表示方式，具体区别如下：
 * 1. `kk:mm`表示的是24小时制的时间，小时范围是1-24。例如，`13:30`表示下午1点30分，`23:45`表示晚上11点45分。
 * 2. `HH:mm`表示的也是24小时制的时间，但小时范围是0-23。例如，`13:30`仍然表示下午1点30分，`23:45`表示晚上11点45分。
 * 注意，这两种格式在大部分情况下是相似的，只是对于小时的表示范围稍有不同。在实际使用中，你可以根据需要选择合适的时间格式。
 */
public class DateUtil {
    private static final String TAG = "DateUtil";
    /** yyyy-MM-dd */
    public static final String REGEX_DATE = "yyyy-MM-dd";

    /** kk:mm */
    public static final String REGEX_TIME = "kk:mm";

    /** yyyy-MM-dd HH:mm:ss */
    public static final String REGEX_DATE_TIME = "yyyy-MM-dd kk:mm:ss";
    public static final String REGEX_DATE_TIME_1 = "yyyy/MM/dd HH:mm:ss";

    /** yyyy-MM-dd HH:mm:ss:SSS */
    public static final String REGEX_DATE_TIME_MILL = "yyyy-MM-dd kk:mm:ss:SSS";
    public static final String REGEX_DATE_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /** yyyy年MM月dd日 */
    public static final String REGEX_DATE_CHINESE = "yyyy年MM月dd日";

    /** yyyy年MM月dd日 kk:mm */
    public static final String REGEX_DATE_TIME_CHINESE = "yyyy年MM月dd日 kk:mm";

    private static Map<String, SimpleDateFormat> formatterMap;

    /** 获取当前日期或时间的字符串 */
    public static String formatDate(String regex) {
        return getFormatter(regex).format(new Date());
    }

    /**
     * 获取当前日期或时间毫秒数
     *
     * @param regex "yyyy-MM-dd HH:mm:ss"
     */
    public static long getTimeByRegex(String date, String regex) {
        try {
            return getFormatter(regex).parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** 获取指定日期或时间的字符串 */
    public static String formatDate(String regex, Date date) {
        return getFormatter(regex).format(date);
    }

    /** 获取指定日期或时间的字符串 */
    public static String formatDate(String regex, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return getFormatter(regex).format(calendar.getTime());
    }

    /** 获取指定日期或时间的字符串 */
    public static String formatDate(String tagRegex, String srcRegex, String dateStr) {
        try {
            Date date = getFormatter(srcRegex).parse(dateStr);
            return getFormatter(tagRegex).format(date);
        } catch (ParseException e) {
            Log.printExceptionInfo(e);
        }
        return "";
    }

    /** 获取指定日期或时间的Calendar对象 */
    public static Calendar getCalendar(String regex, String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(getFormatter(regex).parse(date));
        } catch (ParseException e) {
            Log.printExceptionInfo(e);
        }
        return calendar;
    }

    public static String formatTime(long time) {
        time = time / 1000;

        //如果时间小于60秒，则返回秒数
        if (time / 60 == 0) {
            return time + "秒";
        }

        //如果时间小于1小时，则返回分钟数
        if (time / (60 * 60) == 0) {
            return (time / 60) + "分" + (time % 60) + "秒";
        }

        //如果时间小于1天，则返回小时数
        if (time / (60 * 60 * 24) == 0) {
            long hour = time / (60 * 60);
            time = time % (60 * 60);
            return hour + "小时" + (time / 60) + "分" + (time % 60) + "秒";
        }

        long day = time / (60 * 60 * 24);
        time = time % (60 * 60 * 24);

        long hour = time / (60 * 60);
        time = time % (60 * 60);

        return day + "天" + hour + "小时" + (time / 60) + "分";
    }

    public static String formatSendDate(String regex, String date) {
        //首先获取到传入的日期和当前日期
        Calendar calendar = getCalendar(regex, date);
        Calendar current = Calendar.getInstance();

        //首先判断是否为当天或者3天以内的邮件
        int currentDay = current.get(Calendar.DAY_OF_YEAR);
        int calendarDay = calendar.get(Calendar.DAY_OF_YEAR);
        //如果是当天的邮件，则返回15:30这样格式的日期
        if (currentDay == calendarDay) {
            return "今天 " + getFormatter(REGEX_TIME).format(calendar.getTime());
        }

        //如果是昨天的邮件，则返回昨天 15:30这样格式的日期
        if (currentDay - calendarDay == 1) {
            return "昨天 " + getFormatter(REGEX_TIME).format(calendar.getTime());
        }

        //如果是前天的邮件，则返回前天 15:30这样格式的日期
        if (currentDay - calendarDay == 2) {
            return "前天 " + getFormatter(REGEX_TIME).format(calendar.getTime());
        }

        //然后判断是否为同一年
        int currentYear = current.get(Calendar.YEAR);
        int calendarYear = calendar.get(Calendar.YEAR);
        //如果不是同一年，则返回年-月-日 时:分:秒这样格式的日期
        if (currentYear != calendarYear) {
            return getFormatter("yyyy-MM-dd kk:mm").format(calendar.getTime());
        }

        //否则返回1月1日 15:02这样格式的日期
        return getFormatter("MM月dd日 kk:mm").format(calendar.getTime());
    }

    private static SimpleDateFormat getFormatter(String regex) {
        if (formatterMap == null) {
            formatterMap = new HashMap<>();
        }

        SimpleDateFormat formatter = formatterMap.get(regex);
        if (formatter == null) {
            formatter = new SimpleDateFormat(regex, Locale.getDefault());
            formatterMap.put(regex, formatter);
        }

        return formatter;
    }

    public static String getHourStr(String timeStr) {
        String[] times = timeStr.split(":");

        int hours = Integer.parseInt(times[0]) * 3600;
        int minutes = Integer.parseInt(times[1]) * 60;
        int seconds = Integer.parseInt(times[2]);

        double time = (hours + minutes + seconds) / 3600.0;

        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(time);
    }

    public static String getWeekStr(int year, int month, int day) {
        String[] arr = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return arr[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public static String formatDate(String regex, long date) {
        return getFormatter(regex).format(new Date(date));
    }

    /** 格式化当前日期 */
    public static String formatCurrentDate(String regex) {
        return formatDate(regex, System.currentTimeMillis());
    }

    /**
     * @param regex    格式
     * @param endStr   结束时间 2021年01月04日
     * @param beginStr 开始时间 2021年01月01日
     * @return 返回两时间的时间间隔（以天计算） 3 两个日期相同,相差天数为0,开始日期早于结束日期为正值,反之为负值
     */
    public static long getDateDiffByDay(String regex, String endStr, String beginStr) {
        long day = 1;
        try {
            SimpleDateFormat format = getFormatter(regex);
            Date begin = format.parse(beginStr);
            Date end = format.parse(endStr);
            return (end.getTime() - begin.getTime()) / (1000 * 3600 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return day;
    }


    /**
     * 转换时间格式为毫秒值
     *
     * @param regex       格式:"yyyy-MM-dd'T'HH:mm:ss.SSS"
     * @param dateTimeStr 日期字符串:"2023-04-27T10:31:22.000+08:00"
     * @return 转换后的毫秒值
     */
    public static long convertToTimestamp(String regex, String dateTimeStr) {
//        String dateTimeStr = "2023-04-25T10:30:00.000+08:00";

        SimpleDateFormat sdf = new SimpleDateFormat(regex, Locale.getDefault());
        try {
// Extract timezone offset
            String timeZoneString = dateTimeStr.substring(dateTimeStr.length() - 6);
            TimeZone timeZone = TimeZone.getTimeZone("GMT" + timeZoneString);
            sdf.setTimeZone(timeZone);

// Remove timezone offset from input string
            dateTimeStr = dateTimeStr.substring(0, dateTimeStr.length() - 6);

            Date date = sdf.parse(dateTimeStr);
            if (date != null) {
                long timestamp = date.getTime();
                XLogUtil.i(TAG, "解析日期时间戳" + timestamp);
                return timestamp;
            }
            XLogUtil.i(TAG, "解析日期错误");
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        try {
//            Date date = sdf.parse(dateTimeStr);
//            return date.getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return 0;
    }

    /**
     * @param dateTimeString 源字符串
     * @param inputFormat    源字符串格式
     * @param outputFormat   目标字符串格式
     * @return 转换格式后的时间字符串
     */
    public static String convertDateTimeFormat(String dateTimeString, String inputFormat, String outputFormat) {
        if (dateTimeString == null) return null;
        SimpleDateFormat inputFormatter = getFormatter(inputFormat);
        SimpleDateFormat outputFormatter = getFormatter(outputFormat);

        try {
            return outputFormatter.format(inputFormatter.parse(dateTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTimeString; // 解析失败
        }
    }


    /**
     * 获取指定日期之前n天的日期
     *
     * @param date 指定的日期
     * @param n    天数
     * @return 指定日期之前n天的日期
     */
    public static Date getDateBefore(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - n);
        return calendar.getTime();
    }

    /**
     * 获取指定日期之后n天的日期
     *
     * @param date 指定的日期
     * @param n    天数
     * @return 指定日期之后n天的日期
     */
    public static Date getDateAfter(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + n);
        return calendar.getTime();
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 两个日期之间的天数差
     */
    public static int getDaysDifference(Date date1, Date date2) {
        long diff = Math.abs(date1.getTime() - date2.getTime());
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    /**
     * 将毫秒数转化为时分秒
     *
     * @param ms 毫秒数
     * @return 转化后的时分秒
     */
    public static String ms2HMS(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
        //如果你想要包括毫秒，只需在返回字符串中添加 strMilliSecond。
        return strHour + ":" + strMinute + ":" + strSecond;
    }

    public static String currentDate() {
        return formatCurrentDate(DateUtil.REGEX_DATE_TIME_MILL);
    }
}
