package com.sdbnet.hywy.enterprise.utils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 日期操作工具类，主要实现了日期的常用操作。
 * <p>
 * 在工具类中经常使用到工具类的格式化描述，这个主要是一个日期的操作类，所以日志格式主要使用 SimpleDateFormat的定义格式.
 * <p>
 * 格式的意义如下： 日期和时间模式 <br>
 * 日期和时间格式由日期和时间模式字符串指定。在日期和时间模式字符串中，未加引号的字母 'A' 到 'Z' 和 'a' 到 'z'
 * 被解释为模式字母，用来表示日期或时间字符串元素。文本可以使用单引号 (') 引起来，以免进行解释。"''"
 * 表示单引号。所有其他字符均不解释；只是在格式化时将它们简单复制到输出字符串，或者在分析时与输入字符串进行匹配。
 * <p>
 * 定义了以下模式字母（所有其他字符 'A' 到 'Z' 和 'a' 到 'z' 都被保留）： <br>
 * <table>
 * <tr>
 * <td>字母</td>
 * <td>日期或时间元素</td>
 * <td>表示</td>
 * <td>示例</td>
 * <td>
 * </tr>
 * <tr>
 * <td>G</td>
 * <td>Era</td>
 * <td>标志符</td>
 * <td>Text</td>
 * <td>AD</td>
 * <td>
 * </tr>
 * <tr>
 * <td>y</td>
 * <td>年</td>
 * <td>Year</td>
 * <td>1996;</td>
 * <td>96</td>
 * <td>
 * </tr>
 * <tr>
 * <td>M</td>
 * <td>年中的月份</td>
 * <td>Month</td>
 * <td>July;</td>
 * <td>Jul;</td>
 * <td>07
 * </tr>
 * <tr>
 * <td>w</td>
 * <td>年中的周数</td>
 * <td>Number</td>
 * <td>27</td>
 * <td>
 * </tr>
 * <tr>
 * <td>W</td>
 * <td>月份中的周数</td>
 * <td>Number</td>
 * <td>2</td>
 * <td>
 * </tr>
 * <tr>
 * <td>D</td>
 * <td>年中的天数</td>
 * <td>Number</td>
 * <td>189</td>
 * <td>
 * </tr>
 * <tr>
 * <td>d</td>
 * <td>月份中的天数</td>
 * <td>Number</td>
 * <td>10</td>
 * <td>
 * </tr>
 * <tr>
 * <td>F</td>
 * <td>月份中的星期</td>
 * <td>Number</td>
 * <td>2</td>
 * <td>
 * </tr>
 * <tr>
 * <td>E</td>
 * <td>星期中的天数</td>
 * <td>Text</td>
 * <td>Tuesday;</td>
 * <td>Tue
 * </tr>
 * <tr>
 * <td>a</td>
 * <td>Am/pm</td>
 * <td>标记</td>
 * <td>Text</td>
 * <td>PM</td>
 * <td>
 * </tr>
 * <tr>
 * <td>H</td>
 * <td>一天中的小时数（0-23）</td>
 * <td>Number</td>
 * <td>0
 * </tr>
 * <tr>
 * <td>k</td>
 * <td>一天中的小时数（1-24）</td>
 * <td>Number</td>
 * <td>24</td>
 * <td>
 * </tr>
 * <tr>
 * <td>K</td>
 * <td>am/pm</td>
 * <td>中的小时数（0-11）</td>
 * <td>Number</td>
 * <td>0</td>
 * <td>
 * </tr>
 * <tr>
 * <td>h</td>
 * <td>am/pm</td>
 * <td>中的小时数（1-12）</td>
 * <td>Number</td>
 * <td>12</td>
 * <td>
 * </tr>
 * <tr>
 * <td>m</td>
 * <td>小时中的分钟数</td>
 * <td>Number</td>
 * <td>30</td>
 * <td>
 * </tr>
 * <tr>
 * <td>s</td>
 * <td>分钟中的秒数</td>
 * <td>Number</td>
 * <td>55</td>
 * <td>
 * </tr>
 * <tr>
 * <td>S</td>
 * <td>毫秒数</td>
 * <td>Number</td>
 * <td>978</td>
 * <td>
 * </tr>
 * <tr>
 * <td>z</td>
 * <td>时区</td>
 * <td>General</td>
 * <td>time</td>
 * <td>zone</td>
 * <td>Pacific</td>
 * <td>Standard</td>
 * <td>Time;</td>
 * <td>PST;</td>
 * <td>GMT-08:00
 * </tr>
 * <tr>
 * <td>Z</td>
 * <td>时区</td>
 * <td>RFC</td>
 * <td>822</td>
 * <td>time</td>
 * <td>zone</td>
 * <td>-0800</td>
 * <td>
 * </tr>
 * </table>
 * 
 * 模式字母通常是重复的，其数量确定其精确表示：
 * 
 */
public final class DateUtil implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3098985139095632110L;

	private DateUtil() {
	}

	/**
	 * 格式化日期显示格式yyyy-MM-dd
	 * 
	 * @param sdate
	 *            原始日期格式
	 * @return yyyy-MM-dd格式化后的日期显示
	 */
	public static String dateFormat(String sdate) {
		return dateFormat(sdate, "yyyy-MM-dd");
	}

	/**
	 * 格式化日期显示格式
	 * 
	 * @param sdate
	 *            原始日期格式
	 * @param format
	 *            格式化后日期格式
	 * @return 格式化后的日期显示
	 */
	public static String dateFormat(String sdate, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		java.sql.Date date = java.sql.Date.valueOf(sdate);
		String dateString = formatter.format(date);

		return dateString;
	}

	/**
	 * 求两个日期相差天数
	 * 
	 * @param sd
	 *            起始日期，格式yyyy-MM-dd
	 * @param ed
	 *            终止日期，格式yyyy-MM-dd
	 * @return 两个日期相差天数
	 */
	public static long getIntervalDays(String sd, String ed) {
		return ((java.sql.Date.valueOf(ed)).getTime() - (java.sql.Date
				.valueOf(sd)).getTime()) / (3600 * 24 * 1000);
	}

	/**
	 * 起始年月yyyy-MM与终止月yyyy-MM之间跨度的月数
	 * 
	 * @return int
	 */
	public static int getInterval(String beginMonth, String endMonth) {
		int intBeginYear = Integer.parseInt(beginMonth.substring(0, 4));
		int intBeginMonth = Integer.parseInt(beginMonth.substring(beginMonth
				.indexOf("-") + 1));
		int intEndYear = Integer.parseInt(endMonth.substring(0, 4));
		int intEndMonth = Integer.parseInt(endMonth.substring(endMonth
				.indexOf("-") + 1));

		return ((intEndYear - intBeginYear) * 12)
				+ (intEndMonth - intBeginMonth) + 1;
	}

	/**
	 * 取得当前日期的年份，以yyyy格式返回.
	 * 
	 * @return 当年 yyyy
	 */
	public static String getCurrentYear() {
		return getFormatCurrentTime("yyyy");
	}

	/**
	 * 自动返回上一年。例如当前年份是2007年，那么就自动返回2006
	 * 
	 * @return 返回结果的格式为 yyyy
	 */
	public static String getBeforeYear() {
		String currentYear = getFormatCurrentTime("yyyy");
		int beforeYear = Integer.parseInt(currentYear) - 1;
		return "" + beforeYear;
	}

	/**
	 * 取得当前日期的月份，以MM格式返回.
	 * 
	 * @return 当前月份 MM
	 */
	public static String getCurrentMonth() {
		return getFormatCurrentTime("MM");
	}

	/**
	 * 取得当前日期的天数，以格式"dd"返回.
	 * 
	 * @return 当前月中的某天dd
	 */
	public static String getCurrentDay() {
		return getFormatCurrentTime("dd");
	}

	/**
	 * 返回当前时间字符串。
	 * <p>
	 * 格式：yyyy-MM-dd
	 * 
	 * @return String 指定格式的日期字符串.
	 */
	public static String getCurrentDate() {
		return getFormatDateTime(new Date(), "yyyy-MM-dd");
	}

	/**
	 * 返回给定时间字符串。
	 * <p>
	 * 格式：yyyy-MM-dd
	 * 
	 * @param date
	 *            日期
	 * @return String 指定格式的日期字符串.
	 */
	public static String getFormatDate(Date date) {
		return getFormatDateTime(date, "yyyy-MM-dd");
	}

	/**
	 * 根据制定的格式，返回日期字符串。例如2007-05-05
	 * 
	 * @param format
	 *            "yyyy-MM-dd" 或者 "yyyy/MM/dd"
	 * @return 指定格式的日期字符串。
	 */
	public static String getFormatDate(String format) {
		return getFormatDateTime(new Date(), format);
	}

	/**
	 * 返回当前时间字符串。
	 * <p>
	 * 格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return String 指定格式的日期字符串.
	 */
	public static String getCurrentTime() {
		return getFormatDateTime(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 返回给定时间字符串。
	 * <p>
	 * 格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 *            日期
	 * @return String 指定格式的日期字符串.
	 */
	public static String getFormatTime(Date date) {
		return getFormatDateTime(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 根据给定的格式，返回时间字符串。
	 * <p>
	 * 格式参照类描绘中说明.
	 * 
	 * @param format
	 *            日期格式字符串
	 * @return String 指定格式的日期字符串.
	 */
	public static String getFormatCurrentTime(String format) {
		return getFormatDateTime(new Date(), format);
	}

	/**
	 * 根据给定的格式与时间(Date类型的)，返回时间字符串<br>
	 * 
	 * @param date
	 *            指定的日期
	 * @param format
	 *            日期格式字符串
	 * @return String 指定格式的日期字符串.
	 */
	public static String getFormatDateTime(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 取得指定年月日的日期对象.
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月注意是从1到12
	 * @param day
	 *            日
	 * @return 一个java.util.Date()类型的对象
	 */
	public static Date getDateObj(int year, int month, int day) {
		Calendar c = new GregorianCalendar();
		c.set(year, month - 1, day);
		return c.getTime();
	}

	/**
	 * 取得指定分隔符分割的年月日的日期对象.
	 * 
	 * @param args
	 *            格式为"yyyy-MM-dd"
	 * @param split
	 *            时间格式的间隔符，例如“-”，“/”
	 * @return 一个java.util.Date()类型的对象
	 */
	public static Date getDateObj(String args, String split) {
		String[] temp = args.split(split);
		int year = new Integer(temp[0]).intValue();
		int month = new Integer(temp[1]).intValue();
		int day = new Integer(temp[2]).intValue();
		return getDateObj(year, month, day);
	}

	/**
	 * 取得给定字符串描述的日期对象，描述模式采用pattern指定的格式.
	 * 
	 * @param dateStr
	 *            日期描述
	 * @param pattern
	 *            日期模式
	 * @return 给定字符串描述的日期对象。
	 */
	public static Date getDateFromString(String dateStr, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date resDate = null;
		try {
			resDate = sdf.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resDate;
	}

	/**
	 * 取得当前Date对象.
	 * 
	 * @return Date 当前Date对象.
	 */
	public static Date getDateObj() {
		Calendar c = new GregorianCalendar();
		return c.getTime();
	}

	/**
	 * 
	 * @return 当前月份有多少天；
	 */
	public static int getDaysOfCurMonth() {
		int curyear = new Integer(getCurrentYear()).intValue(); // 当前年份
		int curMonth = new Integer(getCurrentMonth()).intValue();// 当前月份
		int mArray[] = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30,
				31 };
		// 判断闰年的情况 ，2月份有29天；
		if ((curyear % 400 == 0)
				|| ((curyear % 100 != 0) && (curyear % 4 == 0))) {
			mArray[1] = 29;
		}
		return mArray[curMonth - 1];
		// 如果要返回下个月的天数，注意处理月份12的情况，防止数组越界；
		// 如果要返回上个月的天数，注意处理月份1的情况，防止数组越界；
	}

	/**
	 * 根据指定的年月 返回指定月份（yyyy-MM）有多少天。
	 * 
	 * @param time
	 *            yyyy-MM
	 * @return 天数，指定月份的天数。
	 */
	public static int getDaysOfCurMonth(final String time) {
		String[] timeArray = time.split("-");
		int curyear = new Integer(timeArray[0]).intValue(); // 当前年份
		int curMonth = new Integer(timeArray[1]).intValue();// 当前月份
		int mArray[] = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30,
				31 };
		// 判断闰年的情况 ，2月份有29天；
		if ((curyear % 400 == 0)
				|| ((curyear % 100 != 0) && (curyear % 4 == 0))) {
			mArray[1] = 29;
		}
		if (curMonth == 12) {
			return mArray[0];
		}
		return mArray[curMonth - 1];
		// 如果要返回下个月的天数，注意处理月份12的情况，防止数组越界；
		// 如果要返回上个月的天数，注意处理月份1的情况，防止数组越界；
	}

	/**
	 * 返回指定为年度为year月度为month的月份内，第weekOfMonth个星期的第dayOfWeek天。<br>
	 * 00 00 00 01 02 03 04 <br>
	 * 05 06 07 08 09 10 11<br>
	 * 12 13 14 15 16 17 18<br>
	 * 19 20 21 22 23 24 25<br>
	 * 26 27 28 29 30 31 <br>
	 * 2006年的第一个周的1到7天为：05 06 07 01 02 03 04 <br>
	 * 2006年的第二个周的1到7天为：12 13 14 08 09 10 11 <br>
	 * 2006年的第三个周的1到7天为：19 20 21 15 16 17 18 <br>
	 * 2006年的第四个周的1到7天为：26 27 28 22 23 24 25 <br>
	 * 2006年的第五个周的1到7天为：02 03 04 29 30 31 01 。本月没有就自动转到下个月了。
	 * 
	 * @param year
	 *            形式为yyyy <br>
	 * @param month
	 *            形式为MM,参数值在[1-12]。<br>
	 * @param weekOfMonth
	 *            在[1-6],因为一个月最多有6个周。<br>
	 * @param dayOfWeek
	 *            数字在1到7之间，包括1和7。1表示星期天，7表示星期六<br>
	 *            -6为星期日-1为星期五，0为星期六 <br>
	 * @return <type>int</type>
	 */
	public static int getDayofWeekInMonth(String year, String month,
			String weekOfMonth, String dayOfWeek) {
		Calendar cal = new GregorianCalendar();
		// 在具有默认语言环境的默认时区内使用当前时间构造一个默认的 GregorianCalendar。
		int y = new Integer(year).intValue();
		int m = new Integer(month).intValue();
		cal.clear();// 不保留以前的设置
		cal.set(y, m - 1, 1);// 将日期设置为本月的第一天。
		cal.set(Calendar.DAY_OF_WEEK_IN_MONTH,
				new Integer(weekOfMonth).intValue());
		cal.set(Calendar.DAY_OF_WEEK, new Integer(dayOfWeek).intValue());
		// System.out.print(cal.get(Calendar.MONTH)+" ");
		// System.out.print("当"+cal.get(Calendar.WEEK_OF_MONTH)+"\t");
		// WEEK_OF_MONTH表示当天在本月的第几个周。不管1号是星期几，都表示在本月的第一个周
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 根据指定的年月日小时分秒，返回一个java.Util.Date对象。
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月 0-11
	 * @param date
	 *            日
	 * @param hourOfDay
	 *            小时 0-23
	 * @param minute
	 *            分 0-59
	 * @param second
	 *            秒 0-59
	 * @return 一个Date对象。
	 */
	public static Date getDate(int year, int month, int date, int hourOfDay,
			int minute, int second) {
		Calendar cal = new GregorianCalendar();
		cal.set(year, month, date, hourOfDay, minute, second);
		return cal.getTime();
	}

	/**
	 * 根据指定的年、月、日返回当前是星期几。1表示星期天、2表示星期一、7表示星期六。
	 * 
	 * @param year
	 * @param month
	 *            month是从1开始的12结束
	 * @param day
	 * @return 返回一个代表当期日期是星期几的数字。1表示星期天、2表示星期一、7表示星期六。
	 */
	public static int getDayOfWeek(String year, String month, String day) {
		Calendar cal = new GregorianCalendar(new Integer(year).intValue(),
				new Integer(month).intValue() - 1, new Integer(day).intValue());
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 根据指定的年、月、日返回当前是星期几。1表示星期天、2表示星期一、7表示星期六。
	 * 
	 * @param date
	 *            "yyyy/MM/dd",或者"yyyy-MM-dd"
	 * @return 返回一个代表当期日期是星期几的数字。1表示星期天、2表示星期一、7表示星期六。
	 */
	public static int getDayOfWeek(String date) {
		String[] temp = null;
		if (date.indexOf("/") > 0) {
			temp = date.split("/");
		}
		if (date.indexOf("-") > 0) {
			temp = date.split("-");
		}
		return getDayOfWeek(temp[0], temp[1], temp[2]);
	}

	/**
	 * 返回当前日期是星期几。例如：星期日、星期一、星期六等等。
	 * 
	 * @param date
	 *            格式为 yyyy/MM/dd 或者 yyyy-MM-dd
	 * @return 返回当前日期是星期几
	 */
	public static String getChinaDayOfWeek(String date) {
		String[] weeks = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四",
				"星期五", "星期六" };
		int week = getDayOfWeek(date);
		return weeks[week - 1];
	}

	/**
	 * 根据指定的年、月、日返回当前是星期几。1表示星期天、2表示星期一、7表示星期六。
	 * 
	 * @param date
	 * 
	 * @return 返回一个代表当期日期是星期几的数字。1表示星期天、2表示星期一、7表示星期六。
	 */
	public static int getDayOfWeek(Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 返回制定日期所在的周是一年中的第几个周。<br>
	 * created by wangmj at 20060324.<br>
	 * 
	 * @param year
	 * @param month
	 *            范围1-12<br>
	 * @param day
	 * @return int
	 */
	public static int getWeekOfYear(String year, String month, String day) {
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(new Integer(year).intValue(),
				new Integer(month).intValue() - 1, new Integer(day).intValue());
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * 取得给定日期加上一定天数后的日期对象.
	 * 
	 * @param date
	 *            给定的日期对象
	 * @param amount
	 *            需要添加的天数，如果是向前的天数，使用负数就可以.
	 * @return Date 加上一定天数以后的Date对象.
	 */
	public static Date getDateAdd(Date date, int amount) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, amount);
		return cal.getTime();
	}

	/**
	 * 取得给定日期加上一定天数后的日期对象.
	 * 
	 * @param date
	 *            给定的日期对象
	 * @param amount
	 *            需要添加的天数，如果是向前的天数，使用负数就可以.
	 * @param format
	 *            输出格式.
	 * @return Date 加上一定天数以后的Date对象.
	 */
	public static String getFormatDateAdd(Date date, int amount, String format) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, amount);
		return getFormatDateTime(cal.getTime(), format);
	}

	/**
	 * 获得当前日期固定间隔天数的日期，如前60天dateAdd(-60)
	 * 
	 * @param amount
	 *            距今天的间隔日期长度，向前为负，向后为正
	 * @param format
	 *            输出日期的格式.
	 * @return java.lang.String 按照格式输出的间隔的日期字符串.
	 */
	public static String getFormatCurrentAdd(int amount, String format) {

		Date d = getDateAdd(new Date(), amount);

		return getFormatDateTime(d, format);
	}

	/**
	 * 取得给定格式的昨天的日期输出
	 * 
	 * @param format
	 *            日期输出的格式
	 * @return String 给定格式的日期字符串.
	 */
	public static String getFormatYestoday(String format) {
		return getFormatCurrentAdd(-1, format);
	}

	/**
	 * 返回指定日期的前一天。<br>
	 * 
	 * @param sourceDate
	 * @param format
	 *            yyyy MM dd hh mm ss
	 * @return 返回日期字符串，形式和formcat一致。
	 */
	public static String getYestoday(String sourceDate, String format) {
		return getFormatDateAdd(getDateFromString(sourceDate, format), -1,
				format);
	}

	/**
	 * 返回明天的日期，<br>
	 * 
	 * @param format
	 * @return 返回日期字符串，形式和formcat一致。
	 */
	public static String getFormatTomorrow(String format) {
		return getFormatCurrentAdd(1, format);
	}

	/**
	 * 返回指定日期的后一天。<br>
	 * 
	 * @param sourceDate
	 * @param format
	 * @return 返回日期字符串，形式和formcat一致。
	 */
	public static String getFormatDateTommorrow(String sourceDate, String format) {
		return getFormatDateAdd(getDateFromString(sourceDate, format), 1,
				format);
	}

	/**
	 * 根据主机的默认 TimeZone，来获得指定形式的时间字符串。
	 * 
	 * @param dateFormat
	 * @return 返回日期字符串，形式和formcat一致。
	 */
	public static String getCurrentDateString(String dateFormat) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setTimeZone(TimeZone.getDefault());

		return sdf.format(cal.getTime());
	}

	/**
	 * @deprecated 不鼓励使用。 返回当前时间串 格式:yyMMddhhmmss,在上传附件时使用
	 * 
	 * @return String
	 */
	@Deprecated
	public static String getCurDate() {
		GregorianCalendar gcDate = new GregorianCalendar();
		int year = gcDate.get(Calendar.YEAR);
		int month = gcDate.get(Calendar.MONTH) + 1;
		int day = gcDate.get(Calendar.DAY_OF_MONTH);
		int hour = gcDate.get(Calendar.HOUR_OF_DAY);
		int minute = gcDate.get(Calendar.MINUTE);
		int sen = gcDate.get(Calendar.SECOND);
		String y;
		String m;
		String d;
		String h;
		String n;
		String s;
		y = new Integer(year).toString();

		if (month < 10) {
			m = "0" + new Integer(month).toString();
		} else {
			m = new Integer(month).toString();
		}

		if (day < 10) {
			d = "0" + new Integer(day).toString();
		} else {
			d = new Integer(day).toString();
		}

		if (hour < 10) {
			h = "0" + new Integer(hour).toString();
		} else {
			h = new Integer(hour).toString();
		}

		if (minute < 10) {
			n = "0" + new Integer(minute).toString();
		} else {
			n = new Integer(minute).toString();
		}

		if (sen < 10) {
			s = "0" + new Integer(sen).toString();
		} else {
			s = new Integer(sen).toString();
		}

		return "" + y + m + d + h + n + s;
	}

	/**
	 * 根据给定的格式，返回时间字符串。 和getFormatDate(String format)相似。
	 * 
	 * @param format
	 *            yyyy MM dd hh mm ss
	 * @return 返回一个时间字符串
	 */
	public static String getCurTimeByFormat(String format) {
		Date newdate = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(newdate);
	}

	/**
	 * 获取两个时间串时间的差值，单位为秒
	 * 
	 * @param startTime
	 *            开始时间 yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            结束时间 yyyy-MM-dd HH:mm:ss
	 * @return 两个时间的差值(秒)
	 */
	public static long getDiff(String startTime, String endTime) {
		long diff = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date startDate = ft.parse(startTime);
			Date endDate = ft.parse(endTime);
			diff = startDate.getTime() - endDate.getTime();
			diff = diff / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return diff;
	}

	/**
	 * 获取小时/分钟/秒
	 * 
	 * @param second
	 *            秒
	 * @return 包含小时、分钟、秒的时间字符串，例如3小时23分钟13秒。
	 */
	public static String getHour(long second) {
		long hour = second / 60 / 60;
		long minute = (second - hour * 60 * 60) / 60;
		long sec = (second - hour * 60 * 60) - minute * 60;

		return hour + "小时" + minute + "分钟" + sec + "秒";

	}

	/**
	 * 返回指定时间字符串。
	 * <p>
	 * 格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return String 指定格式的日期字符串.
	 */
	public static String getDateTime(long microsecond) {
		return getFormatDateTime(new Date(microsecond), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 返回当前时间加实数小时后的日期时间。
	 * <p>
	 * 格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return Float 加几实数小时.
	 */
	public static String getDateByAddFltHour(float flt) {
		int addMinute = (int) (flt * 60);
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, addMinute);
		return getFormatDateTime(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 返回指定时间加指定小时数后的日期时间。
	 * <p>
	 * 格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return 时间.
	 */
	public static String getDateByAddHour(String datetime, int minute) {
		String returnTime = null;
		Calendar cal = new GregorianCalendar();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = ft.parse(datetime);
			cal.setTime(date);
			cal.add(Calendar.MINUTE, minute);
			returnTime = getFormatDateTime(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnTime;

	}
	
	/**
	 * 返回指定时间加指定分钟后的日期时间。
	 * <p>
	 * 格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return 时间.
	 */
	public static String getDateByAddMinute(String datetime, int minute) {
		String returnTime = null;
		Calendar cal = new GregorianCalendar();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = ft.parse(datetime);
			cal.setTime(date);
			cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + minute);
			returnTime = getFormatDateTime(cal.getTime(), "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnTime;
		
	}

	/**
	 * 获取两个时间串时间的差值，单位为小时
	 * 
	 * @param startTime
	 *            开始时间 yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            结束时间 yyyy-MM-dd HH:mm:ss
	 * @return 两个时间的差值(秒)
	 */
	public static int getDiffHour(String startTime, String endTime) {
		long diff = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date startDate = ft.parse(startTime);
			Date endDate = ft.parse(endTime);
			diff = startDate.getTime() - endDate.getTime();
			diff = diff / (1000 * 60 * 60);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Long(diff).intValue();
	}

	/**
	 * 返回年份的下拉框。
	 * 
	 * @param selectName
	 *            下拉框名称
	 * @param value
	 *            当前下拉框的值
	 * @param startYear
	 *            开始年份
	 * @param endYear
	 *            结束年份
	 * @return 年份下拉框的html
	 */
	public static String getYearSelect(String selectName, String value,
			int startYear, int endYear) {
		int start = startYear;
		int end = endYear;
		if (startYear > endYear) {
			start = endYear;
			end = startYear;
		}
		StringBuffer sb = new StringBuffer("");
		sb.append("<select name=\"" + selectName + "\">");
		for (int i = start; i <= end; i++) {
			if (!value.trim().equals("") && i == Integer.parseInt(value)) {
				sb.append("<option value=\"" + i + "\" selected>" + i
						+ "</option>");
			} else {
				sb.append("<option value=\"" + i + "\">" + i + "</option>");
			}
		}
		sb.append("</select>");
		return sb.toString();
	}

	/**
	 * 返回年份的下拉框。
	 * 
	 * @param selectName
	 *            下拉框名称
	 * @param value
	 *            当前下拉框的值
	 * @param startYear
	 *            开始年份
	 * @param endYear
	 *            结束年份
	 *            例如开始年份为2001结束年份为2005那么下拉框就有五个值。（2001、2002、2003、2004、2005）。
	 * @return 返回年份的下拉框的html。
	 */
	public static String getYearSelect(String selectName, String value,
			int startYear, int endYear, boolean hasBlank) {
		int start = startYear;
		int end = endYear;
		if (startYear > endYear) {
			start = endYear;
			end = startYear;
		}
		StringBuffer sb = new StringBuffer("");
		sb.append("<select name=\"" + selectName + "\">");
		if (hasBlank) {
			sb.append("<option value=\"\"></option>");
		}
		for (int i = start; i <= end; i++) {
			if (!value.trim().equals("") && i == Integer.parseInt(value)) {
				sb.append("<option value=\"" + i + "\" selected>" + i
						+ "</option>");
			} else {
				sb.append("<option value=\"" + i + "\">" + i + "</option>");
			}
		}
		sb.append("</select>");
		return sb.toString();
	}

	/**
	 * 返回年份的下拉框。
	 * 
	 * @param selectName
	 *            下拉框名称
	 * @param value
	 *            当前下拉框的值
	 * @param startYear
	 *            开始年份
	 * @param endYear
	 *            结束年份
	 * @param js
	 *            这里的js为js字符串。例如 " onchange=\"changeYear()\" "
	 *            ,这样任何js的方法就可以在jsp页面中编写，方便引入。
	 * @return 返回年份的下拉框。
	 */
	public static String getYearSelect(String selectName, String value,
			int startYear, int endYear, boolean hasBlank, String js) {
		int start = startYear;
		int end = endYear;
		if (startYear > endYear) {
			start = endYear;
			end = startYear;
		}
		StringBuffer sb = new StringBuffer("");

		sb.append("<select name=\"" + selectName + "\" " + js + ">");
		if (hasBlank) {
			sb.append("<option value=\"\"></option>");
		}
		for (int i = start; i <= end; i++) {
			if (!value.trim().equals("") && i == Integer.parseInt(value)) {
				sb.append("<option value=\"" + i + "\" selected>" + i
						+ "</option>");
			} else {
				sb.append("<option value=\"" + i + "\">" + i + "</option>");
			}
		}
		sb.append("</select>");
		return sb.toString();
	}

	/**
	 * 返回年份的下拉框。
	 * 
	 * @param selectName
	 *            下拉框名称
	 * @param value
	 *            当前下拉框的值
	 * @param startYear
	 *            开始年份
	 * @param endYear
	 *            结束年份
	 * @param js
	 *            这里的js为js字符串。例如 " onchange=\"changeYear()\" "
	 *            ,这样任何js的方法就可以在jsp页面中编写，方便引入。
	 * @return 返回年份的下拉框。
	 */
	public static String getYearSelect(String selectName, String value,
			int startYear, int endYear, String js) {
		int start = startYear;
		int end = endYear;
		if (startYear > endYear) {
			start = endYear;
			end = startYear;
		}
		StringBuffer sb = new StringBuffer("");
		sb.append("<select name=\"" + selectName + "\" " + js + ">");
		for (int i = start; i <= end; i++) {
			if (!value.trim().equals("") && i == Integer.parseInt(value)) {
				sb.append("<option value=\"" + i + "\" selected>" + i
						+ "</option>");
			} else {
				sb.append("<option value=\"" + i + "\">" + i + "</option>");
			}
		}
		sb.append("</select>");
		return sb.toString();
	}

	/**
	 * 获取月份的下拉框
	 * 
	 * @param selectName
	 * @param value
	 * @param hasBlank
	 * @return 返回月份的下拉框。
	 */
	public static String getMonthSelect(String selectName, String value,
			boolean hasBlank) {
		StringBuffer sb = new StringBuffer("");
		sb.append("<select name=\"" + selectName + "\">");
		if (hasBlank) {
			sb.append("<option value=\"\"></option>");
		}
		for (int i = 1; i <= 12; i++) {
			if (!value.trim().equals("") && i == Integer.parseInt(value)) {
				sb.append("<option value=\"" + i + "\" selected>" + i
						+ "</option>");
			} else {
				sb.append("<option value=\"" + i + "\">" + i + "</option>");
			}
		}
		sb.append("</select>");
		return sb.toString();
	}

	/**
	 * 获取月份的下拉框
	 * 
	 * @param selectName
	 * @param value
	 * @param hasBlank
	 * @param js
	 * @return 返回月份的下拉框。
	 */
	public static String getMonthSelect(String selectName, String value,
			boolean hasBlank, String js) {
		StringBuffer sb = new StringBuffer("");
		sb.append("<select name=\"" + selectName + "\" " + js + ">");
		if (hasBlank) {
			sb.append("<option value=\"\"></option>");
		}
		for (int i = 1; i <= 12; i++) {
			if (!value.trim().equals("") && i == Integer.parseInt(value)) {
				sb.append("<option value=\"" + i + "\" selected>" + i
						+ "</option>");
			} else {
				sb.append("<option value=\"" + i + "\">" + i + "</option>");
			}
		}
		sb.append("</select>");
		return sb.toString();
	}

	/**
	 * 获取天的下拉框，默认的为1-31。 注意：此方法不能够和月份下拉框进行联动。
	 * 
	 * @param selectName
	 * @param value
	 * @param hasBlank
	 * @return 获得天的下拉框
	 */
	public static String getDaySelect(String selectName, String value,
			boolean hasBlank) {
		StringBuffer sb = new StringBuffer("");
		sb.append("<select name=\"" + selectName + "\">");
		if (hasBlank) {
			sb.append("<option value=\"\"></option>");
		}
		for (int i = 1; i <= 31; i++) {
			if (!value.trim().equals("") && i == Integer.parseInt(value)) {
				sb.append("<option value=\"" + i + "\" selected>" + i
						+ "</option>");
			} else {
				sb.append("<option value=\"" + i + "\">" + i + "</option>");
			}
		}
		sb.append("</select>");
		return sb.toString();
	}

	/**
	 * 获取天的下拉框，默认的为1-31
	 * 
	 * @param selectName
	 * @param value
	 * @param hasBlank
	 * @param js
	 * @return 获取天的下拉框
	 */
	public static String getDaySelect(String selectName, String value,
			boolean hasBlank, String js) {
		StringBuffer sb = new StringBuffer("");
		sb.append("<select name=\"" + selectName + "\" " + js + ">");
		if (hasBlank) {
			sb.append("<option value=\"\"></option>");
		}
		for (int i = 1; i <= 31; i++) {
			if (!value.trim().equals("") && i == Integer.parseInt(value)) {
				sb.append("<option value=\"" + i + "\" selected>" + i
						+ "</option>");
			} else {
				sb.append("<option value=\"" + i + "\">" + i + "</option>");
			}
		}
		sb.append("</select>");
		return sb.toString();
	}

	/**
	 * 计算两天之间有多少个周末（这个周末，指星期六和星期天，一个周末返回结果为2，两个为4，以此类推。） （此方法目前用于统计司机用车记录。）
	 * 
	 * @param startDate
	 *            开始日期 ，格式"yyyy/MM/dd"
	 * @param endDate
	 *            结束日期 ，格式"yyyy/MM/dd"
	 * @return int
	 */
	public static int countWeekend(String startDate, String endDate) {
		int result = 0;
		Date sdate = null;
		Date edate = null;
		sdate = getDateObj(startDate, "/"); // 开始日期
		edate = getDateObj(endDate, "/");// 结束日期
		// 首先计算出都有那些日期，然后找出星期六星期天的日期
		int sumDays = Math.abs(getDiffDays(startDate, endDate));
		int dayOfWeek = 0;
		for (int i = 0; i <= sumDays; i++) {
			dayOfWeek = getDayOfWeek(getDateAdd(sdate, i)); // 计算每过一天的日期
			if (dayOfWeek == 1 || dayOfWeek == 7) { // 1 星期天 7星期六
				result++;
			}
		}
		return result;
	}

	/**
	 * 返回两个日期之间相差多少天。
	 * 
	 * @param startDate
	 *            格式"yyyy/MM/dd"
	 * @param endDate
	 *            格式"yyyy/MM/dd"
	 * @return 整数。
	 */
	public static int getDiffDays(String startDate, String endDate) {
		long diff = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			Date sDate = ft.parse(startDate + " 00:00:00");
			Date eDate = ft.parse(endDate + " 00:00:00");
			diff = eDate.getTime() - sDate.getTime();
			diff = diff / 86400000;// 1000*60*60*24;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) diff;

	}

	/**
	 * 返回两个日期之间的详细日期数组(包括开始日期和结束日期)。 例如：2007/07/01 到2007/07/03 ,那么返回数组
	 * {"2007/07/01","2007/07/02","2007/07/03"}
	 * 
	 * @param startDate
	 *            格式"yyyy/MM/dd"
	 * @param endDate
	 *            格式"yyyy/MM/dd"
	 * @return 返回一个字符串数组对象
	 */
	public static String[] getArrayDiffDays(String startDate, String endDate) {
		int LEN = 0; // 用来计算两天之间总共有多少天
		// 如果结束日期和开始日期相同
		if (startDate.equals(endDate)) {
			return new String[] { startDate };
		}
		Date sdate = null;
		sdate = getDateObj(startDate, "/"); // 开始日期
		LEN = getDiffDays(startDate, endDate);
		String[] dateResult = new String[LEN + 1];
		dateResult[0] = startDate;
		for (int i = 1; i < LEN + 1; i++) {
			dateResult[i] = getFormatDateTime(getDateAdd(sdate, i),
					"yyyy/MM/dd");
		}

		return dateResult;
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////
	/**
	 * 一个日期早于一个日期区间
	 */
	public static final int BEFORE_START_DATE = -2;

	/**
	 * 一个日期等于一个日期区间的开始日期
	 */
	public static final int EQUAL_START_DATE = -1;

	/**
	 * 一个日期在一个日期区间之内
	 */
	public static final int BETWEEN_TOW_DATE = 0;

	/**
	 * 一个日期等于一个日期区间的结束日期
	 */
	public static final int EQUAL_END_DATE = 1;

	/**
	 * 一个日期晚于一个日期区间
	 */
	public static final int AFTER_END_DATE = 2;

	/**
	 * 三个日期相等
	 */
	public static final int TREE_DATE_EQUAL = 3;

	/**
	 * 普通日期格式
	 */
	public static final String NORMAL_DATE_PATTERN = "yyyy-MM-dd";

	public static final String ZHCN_DATE_PATTERN = "yyyy年MM月dd日";

	public static final String ZHCN_DATE_TIME_PATTERN = "yyyy年MM月dd日 HH:mm:ss";

	/** 值班日期(白班)每天的起始时间点,或(晚班)的结束时间点 */
	public static String dutyStartData = "9:00:00";

	/** 值班日期(白班)每天的结束时间点，或(晚班)的起始时间点 */
	public static String dutyEndData = "18:30:00";

	/**
	 * 
	 * @Description:判断<firstDate>时间点是否在<secondDate>时间点之前 如果此 firstDate
	 *                                                   的时间在参数<secondDate
	 *                                                   >表示的时间之前，则返回小于 0 的值
	 * @param firstDate
	 * @param secondDate
	 * @return
	 * @ReturnType boolean
	 * @author:
	 * @Created 2012 2012-9-20上午08:40:33
	 */
	public static boolean isBefore(Date firstDate, Date secondDate) {

		return compare(firstDate, secondDate) < 0 ? true : false;
	}

	/**
	 * 
	 * @Description:判断<firstDate>时间点是否在<secondDate>时间点之后 如果此 firstDate
	 *                                                   的时间在参数<secondDate
	 *                                                   >表示的时间之后，则返回大于 0 的值
	 * @param firstDate
	 * @param secondDate
	 * @ReturnType boolean
	 * @author:
	 * @Created 2012 2012-9-20上午08:38:48
	 */
	public static boolean isAfter(Date firstDate, Date secondDate) {

		return compare(firstDate, secondDate) > 0 ? true : false;
	}

	/**
	 * 
	 * @Description:比较两个时间点是否相等
	 * @param firstDate
	 * @param secondDate
	 * @ReturnType boolean
	 * @author:
	 * @Created 2012 2012-9-20上午08:37:30
	 */
	public static boolean isEqual(Date firstDate, Date secondDate) {

		return compare(firstDate, secondDate) == 0 ? true : false;
	}

	/**
	 * 
	 * @Description:比较两个时间点 如果secondDate表示的时间等于此 firstDate 表示的时间，则返回 0 值； 如果此
	 *                      firstDate 的时间在参数<secondDate>表示的时间之前，则返回小于 0 的值； 如果此
	 *                      firstDate 的时间在参数<secondDate>表示的时间之后，则返回大于 0 的值
	 * @param firstDate
	 * @param secondDate
	 * @ReturnType int
	 * @author:
	 * @Created 2012 2012-9-20上午08:34:33
	 */
	public static int compare(Date firstDate, Date secondDate) {

		Calendar firstCalendar = null;
		/** 使用给定的 Date 设置此 Calendar 的时间。 **/
		if (firstDate != null) {
			firstCalendar = Calendar.getInstance();
			firstCalendar.setTime(firstDate);
		}

		Calendar secondCalendar = null;
		/** 使用给定的 Date 设置此 Calendar 的时间。 **/
		if (firstDate != null) {
			secondCalendar = Calendar.getInstance();
			secondCalendar.setTime(secondDate);
		}

		try {
			/**
			 * 比较两个 Calendar 对象表示的时间值（从历元至现在的毫秒偏移量）。 如果参数表示的时间等于此 Calendar
			 * 表示的时间，则返回 0 值； 如果此 Calendar 的时间在参数表示的时间之前，则返回小于 0 的值； 如果此
			 * Calendar 的时间在参数表示的时间之后，则返回大于 0 的值
			 * **/
			return firstCalendar.compareTo(secondCalendar);
		} catch (NullPointerException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 
	 * @Description:
	 * @param startDate
	 * @param endDate
	 * @param inDate
	 * @return
	 * @ReturnType int
	 * @author:
	 * @Created 2012 2012-9-20上午08:42:06
	 */
	public static int betweenTowDate(Date startDate, Date endDate, Date inDate) {

		/**
		 * 判断<endDate>时间点是否在<startDate>时间点之前
		 * 如果为真表示<endDate>时间点在<startDate>时间点之前则抛出异常 即结束时间在开始时间之前是不正常的
		 */
		if (isBefore(endDate, startDate)) {
			throw new IllegalArgumentException(
					"endDate can not before startDate!");
		}

		/**
		 * 比较两个时间点<inDate>和<startDate> 如果inDate表示的时间等于此 startDate 表示的时间，则返回 0 值；
		 * 如果此 inDate 的时间在参数<startDate>表示的时间之前，则返回小于 0 的值； 如果此 inDate
		 * 的时间在参数<startDate>表示的时间之后，则返回大于 0 的值
		 */
		int sflag = compare(inDate, startDate);

		/**
		 * 比较两个时间点<inDate>和<endDate> 如果inDate表示的时间等于此 endDate 表示的时间，则返回 0 值； 如果此
		 * inDate 的时间在参数<endDate>表示的时间之前，则返回小于 0 的值； 如果此 inDate
		 * 的时间在参数<endDate>表示的时间之后，则返回大于 0 的值
		 */
		int eflag = compare(inDate, endDate);

		int flag = 0;

		/** 如果此 inDate 的时间在参数<startDate>表示的时间之前，则返回小于 0 的值 **/
		if (sflag < 0) {
			/** 一个日期早于一个日期区间 **/
			flag = DateUtil.BEFORE_START_DATE;
		} else if (sflag == 0) {
			/** 如果inDate表示的时间等于此 startDate 表示的时间，则返回 0 值； **/
			/** 如果inDate表示的时间等于此 endDate 表示的时间，则返回 0 值； **/
			if (eflag == 0) {
				/** 三个日期相等 **/
				flag = DateUtil.TREE_DATE_EQUAL;
			} else {
				/** 一个日期等于一个日期区间的开始日期 **/
				flag = DateUtil.EQUAL_START_DATE;
			}
		} else if (sflag > 0 && eflag < 0) {
			/**
			 * 满足-inDate 的时间在参数<startDate>表示的时间之后，和 inDate
			 * 的时间在参数<endDate>表示的时间之前
			 **/
			/** 一个日期在一个日期区间之内 **/
			flag = DateUtil.BETWEEN_TOW_DATE;
		} else if (eflag == 0) {
			/** 如果inDate表示的时间等于此 endDate 表示的时间 **/
			/** 一个日期等于一个日期区间的结束日期 **/
			flag = DateUtil.EQUAL_END_DATE;
		} else if (eflag > 0) {
			/** 满足inDate 的时间在参数<endDate>表示的时间之后 **/
			/** 一个日期晚于一个日期区间 **/
			flag = DateUtil.AFTER_END_DATE;
		}
		return flag;
	}

	/**
	 * 
	 * @Description:分别判断当前日期是与一个日期区间的六种情况比较 （1） 一个日期早于一个日期区间 （2）三个日期相等
	 *                                      （3）一个日期等于一个日期区间的开始日期
	 *                                      （4）一个日期在一个日期区间之内
	 *                                      （5）一个日期等于一个日期区间的结束日期 （6）一个日期晚于一个日期区间
	 * @param startDate
	 * @param endDate
	 * @return
	 * @ReturnType int
	 * @author:
	 * @Created 2012 2012-9-20上午09:00:31
	 */
	public static int betweenTowDate(Date startDate, Date endDate) {
		return betweenTowDate(startDate, endDate, new Date());
	}

	/**
	 * 将Date型转换为String型
	 * 
	 * @param dtDate
	 *            Date 要转换的时间
	 * @param strPattern
	 *            String 转换的格式
	 * @return String 转换后的时间 说明：格式可以为 yyyy-MM-dd HH:mm:ss等，可任意组合，如 yyyy年MM月dd日
	 *         yyyy代表年 MM代表月 dd代表日 HH hh kk代表小时 mm代表分钟 ss代表秒钟 format
	 */
	public static String dateToString(Date dtDate, String strPattern) {
		SimpleDateFormat lsdfDate = new SimpleDateFormat(strPattern);
		return lsdfDate.format(dtDate);
	}

	/**
	 * 
	 * @Description:将符合相应格式的字符串转化为日期 <格式自定义>
	 * @param dateStr
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @ReturnType Date 日期字串为空或者不符合日期格式时返回null
	 * @author:
	 * @Created 2012 2012-9-20上午09:06:00
	 */
	public static Date getDate(String dateStr, String pattern) {
		return getDate(dateStr, pattern, null);
	}

	/**
	 * 将符合相应格式的字符串转化为日期 格式自定义
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @param defaultDate
	 *            默认日期
	 * @return 日期字串为空或者不符合日期格式时返回defaultDate
	 */
	public static Date getDate(String dateStr, String pattern, Date defaultDate) {

		if (dateStr != null && pattern != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			try {
				return sdf.parse(dateStr);
			} catch (ParseException e) {

			}
		}
		return defaultDate;
	}

	/**
	 * 
	 * @Description:将某种日期转换成指定格式的日期数据 <获取相应格式的Date>
	 * @param date
	 *            需要格式的日期参数
	 * @param pattern
	 *            日期格式
	 * @ReturnType Date
	 * @author:
	 * @Created 2012 2012-9-20上午09:08:24
	 */
	public static Date getFormatDate(Date date, String pattern) {

		if (date == null) {
			throw new IllegalArgumentException("date can not be null!");
		}

		if (pattern == null) {
			pattern = NORMAL_DATE_PATTERN;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		String dateStr = sdf.format(date);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {

		}
		return date;
	}

	/**
	 * 
	 * @Description:将符合相应格式的字符串转化为日期 格式 yyyy-MM-dd
	 * @param dateStr
	 * @return 日期字串为空或者不符合日期格式时返回null
	 * @ReturnType Date
	 * @author:
	 * @Created 2012 2012-9-20上午09:04:28
	 */
	public static Date getDate(String dateStr) {
		return getDate(dateStr, DateUtil.NORMAL_DATE_PATTERN);
	}

	/**
	 * 
	 * @Description:将当前日期转换成字符串 格式 yyyy-MM-dd
	 * @ReturnType String
	 * @author:
	 * @Created 2012 2012-9-19下午05:54:42
	 */
	public static String datetoStr() {
		SimpleDateFormat sdf = new SimpleDateFormat(NORMAL_DATE_PATTERN);
		String curDate = sdf.format(new Date());
		return curDate;
	}

	/**
	 * 
	 * @Description: 获取当天日期,日期格式为YYYYMMDD
	 * @ReturnType String
	 * @author:
	 * @Created 2012 2012-9-20上午09:17:19
	 */
	public static String getIntraday() {
		Calendar date = Calendar.getInstance();// 获得当前日期
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH) + 1;
		int day = date.get(Calendar.DAY_OF_MONTH);
		String updateFileDate = new String(new Integer(year).toString()
				+ new Integer(month).toString() + new Integer(day).toString());
		return updateFileDate;
	}

	/**
	 * 
	 * @Description:获取当天日期，日期格式为YYYY-MM-DD HH:mm:ss
	 * @return
	 * @ReturnType Date
	 * @author:
	 * @Created 2012 2012-9-20上午09:58:36
	 */
	public static Date getCurrentlyDate() {
		Date currentDate = new Date();
		return getFormatDate(currentDate, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 
	 * @Description: 获取时间的小时数（24小时制）
	 * @param date
	 * @return
	 * @ReturnType int
	 * @author:
	 * @Created 2012 2012-9-25下午08:33:44
	 */
	public static int getTime24Hour() {

		Calendar calender = Calendar.getInstance();
		calender.setTime(getCurrentlyDate());
		return calender.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 
	 * @Description:获取前一日
	 * @param date
	 * @return
	 * @ReturnType Date
	 * @author:
	 * @Created 2012 2012-9-25下午08:32:00
	 */
	public static Date getBeforeDate(Date date) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.add(Calendar.DATE, -1);
		return calender.getTime();
	}

	/**
	 * 
	 * @Description:取得当前日期的下一日
	 * @ReturnType String
	 * @author:
	 * @Created 2012 2012-9-20上午09:12:06
	 */
	public static String getTomorrowDate() {
		Date myDate = new Date();
		Calendar calender = Calendar.getInstance();
		calender.setTime(myDate);
		calender.add(Calendar.DATE, 1);
		return dateToString(calender.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 
	 * @Description:取当前日期后的第二日
	 * @return
	 * @ReturnType String
	 * @author:
	 * @Created 2012 2012-9-20上午10:37:47
	 */
	public static String getNextTomorrowDate() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Date myDate = new Date();

		long myTime = (myDate.getTime() / 1000) - 60 * 60 * 24 * 365;

		myDate.setTime(myTime * 1000);

		String mDate = formatter.format(myDate);

		myTime = (myDate.getTime() / 1000) + 60 * 60 * 24;

		myDate.setTime(myTime * 1000);

		mDate = formatter.format(myDate);

		return mDate;
	}

	/**
	 * 
	 * @Description:获取当前星期在当前月份中的第几个星期
	 * @param date
	 * @return
	 * @ReturnType int
	 * @author:
	 * @Created 2012 2012-10-29上午11:45:13
	 */
	public static int getTimeWeekOfMonth(Date date) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		return calender.get(Calendar.WEEK_OF_MONTH);
	}

	/**
	 * 
	 * 功能描述：获取时间在当前星期的第几天
	 * 
	 * @author <p>
	 *         创建日期 ：2012 2012-10-29上午11:45:52
	 *         </p>
	 * 
	 * @param date
	 * @return 返回星期数,其中: 1表示星期一, ...7表示星期天
	 * 
	 *         <p>
	 *         修改历史 ：(修改人，修改时间，修改原因/内容)
	 *         </p>
	 */
	public static int getTimeDateOfWeek(Date date) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		int week = calender.get(Calendar.DAY_OF_WEEK) - 1;
		if (week == 0)
			week = 7;
		return week;
	}

	/**
	 * 
	 * 功能描述：获取时间在当前星期的第几天
	 * 
	 * @author <p>
	 *         创建日期 ：2012 2012-10-29上午11:45:52
	 *         </p>
	 * 
	 * @param date
	 * @return 返回星期数,其中: 7表示星期六..1表示星期天
	 * 
	 *         <p>
	 *         修改历史 ：(修改人，修改时间，修改原因/内容)
	 *         </p>
	 */
	public static int getQuartzTimeDateOfWeek(Date date) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		int week = calender.get(Calendar.DAY_OF_WEEK);
		return week;
	}

	/**
	 * 
	 * @Description:某个时间与当前时间进行求差
	 * @param date
	 * @return
	 * @ReturnType long
	 * @author:
	 * @Created 2012 2012-12-12下午01:00:53
	 */
	public static long getAppointTimeDifference(Date startDate, Date endDate) {
		long l = endDate.getTime() - startDate.getTime();
		long day = l / (24 * 60 * 60 * 1000);
		return day;
	}

	/**
	 * 
	 * @Description:某个时间与当前时间进行求差
	 * @param date
	 * @return
	 * @ReturnType long
	 * @author:
	 * @Created 2012 2012-12-12下午01:00:53
	 */
	public static long getTimeDifference(Date date) {
		/** 获取当前系统时间 **/
		java.util.Date currentlyDate = DateUtil.getCurrentlyDate();

		long l = date.getTime() - currentlyDate.getTime();

		long day = l / (24 * 60 * 60 * 1000);

		long hour = (l / (60 * 60 * 1000) - day * 24);

		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);

		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

		System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
		return day;
	}

	public static void main(String[] args) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date now;
		try {
			now = df.parse("2012-12-10 13:31:34");

			java.util.Date date = DateUtil.getCurrentlyDate();

			long l = now.getTime() - date.getTime();

			long day = l / (24 * 60 * 60 * 1000);

			long hour = (l / (60 * 60 * 1000) - day * 24);

			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);

			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

			System.out.println("" + day + "天" + hour + "小时" + min + "分" + s
					+ "秒");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Description:获取时间的分数
	 * @param date
	 * @ReturnType int
	 * @author:
	 * @Created 2012 2012-10-29上午11:45:13
	 */
	public static int getTimeMinute(Date date) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		return calender.get(Calendar.MINUTE);
	}

	/**
	 * 给出时间分格式转换时间单位
	 * 
	 * @param time
	 *            String ; 12:20:30
	 * @return String ;12h20m30s
	 */
	public static String getTimeUnit(String time) {
		time = time.replaceFirst(":", "h");
		time = time.replaceFirst(":", "m") + "s";
		return time;
	}

	/**
	 * 根据当前日期及增加天数得到相应日期
	 * 
	 * @param s
	 * @param n
	 * @return
	 * @throws Exception
	 */
	public static String addDay(String s, int n) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cd = Calendar.getInstance();
		try {
			cd.setTime(sdf.parse(s));
			cd.add(Calendar.DATE, n);
		} catch (Exception e) {
		}
		return sdf.format(cd.getTime());
	}

	public static boolean isValidDate(String str) {
		boolean convertSuccess = true;
		// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
			// 设置lenient为false.
			// 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			// e.printStackTrace();
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			convertSuccess = false;
		}
		return convertSuccess;
	}
}
