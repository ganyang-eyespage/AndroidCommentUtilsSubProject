/**
 * 创建人：SongZhiyong
 * 创建时间：2013-1-3
 */
package com.eyespage.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间日期解析格式化
 *
 * @author SongZhiyong
 */
public class TimeUtil {

  /** UTC时间样式 */
  public static final String UTC_TIME_PATTERN = "E MMM dd HH:mm:ss ZZZZ yyyy";
  /** 普通时间样式 */
  public static final String NORMAL_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
  public static final String TIME_PATTERN = "HH:mm";

  public static final String ISO_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
  public static final String ISO_TIME_PATTERN_DEPRACATED = "yyyy-MM-dd'T'HH:mm:ss.ssssss";

  public static final List<Long> TIMES =
      Arrays.asList(TimeUnit.DAYS.toMillis(365), TimeUnit.DAYS.toMillis(30),
          TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(1), TimeUnit.MINUTES.toMillis(1),
          TimeUnit.SECONDS.toMillis(1));
  public static final List<String> TIMES_STRING =
      Arrays.asList("year", "month", "day", "hour", "minute", "second");

  /**
   * 根据字符串及样式解析出时间Date
   *
   * @param pattern 样式
   * @param time 时间
   * @return date 解析完成返回的日期
   * @throws java.text.ParseException 解析错误
   */
  public static Date parse(String pattern, String time) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
    try {
      return sdf.parse(time);
    } catch (ParseException e) {
      Date date = null;
      try {
        date = parse(ISO_TIME_PATTERN_DEPRACATED, time);
      } catch (ParseException e1) {
        return new Date();
      }
      return date;
    }
  }

  /**
   * 根据日期和样式格式化时间
   *
   * @param pattern 样式
   * @param date 输入日期
   * @return 格式化之后的时间字符串
   */
  public static String format(String pattern, Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
    return sdf.format(date);
  }

  public static String format(String pattern, long timeMills) {
    return format(pattern, new Date(timeMills));
  }

  public static String formatNormal(long timeMills) {
    return format(NORMAL_TIME_PATTERN, new Date(timeMills));
  }

  public static String formatToUTC(String pattern, Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
    sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
    return sdf.format(date);
  }

  /**
   * @param mss 要转换的毫秒数
   * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
   * @author fy.zhang
   */
  public static String formatDuring(long mss) {
    long hours = mss / (1000 * 60 * 60);
    long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
    return hours + "hrs " + minutes + "mins";
  }

  public static String formatDuringFromSecond(long second) {
    long mins = second / 60;
    long secs = second % 60;
    return mins + ":" + secs;
  }

  public static String toDuration(long duration) {
    StringBuffer res = new StringBuffer();
    for (int i = 0; i < TIMES.size(); i++) {
      Long current = TIMES.get(i);
      long temp = duration / current;
      if (temp > 0) {
        res.append(temp)
            .append(" ")
            .append(TIMES_STRING.get(i))
            .append(temp > 1 ? "s" : "")
            .append(" ago");
        break;
      }
    }
    if ("".equals(res.toString()) || res.toString().contains("second")) {
      return "just now";
    } else if (res.toString().contains("minute")) {
      return "minutes ago";
    } else {
      return res.toString();
    }
  }

  public static String parseEyespageTime(String string) {
    Pattern p = Pattern.compile("\\{eyespage:([^:]+):([^\\}]+)\\}");
    Matcher matcher = p.matcher(string);
    while (matcher.find()) {
      String patterns = matcher.group();
      String symbol = matcher.group(1);
      String pattern = matcher.group(2);
      Date date = getDate(symbol);
      string = string.replace(patterns, format(pattern, date));
    }
    return string;
  }

  private static Date getDate(String param) {
    int delta = 0;
    if ("today".equals(param)) {
      delta = 0;
    } else if ("tomorrow".equals(param)) {
      delta = 1;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, delta);
    return calendar.getTime();
  }
}
