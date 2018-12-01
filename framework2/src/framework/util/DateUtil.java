/*
 * @(#)DateUtil.java
 */
package framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 날짜관련 유틸리티 클래스이다.
 */
public class DateUtil {

	/**
	 * 날짜여부 체크
	 * @param year : 년
	 * @param month : 월
	 * @param day : 일
	 * @return boolean : 날짜여부
	 */
	public static boolean isDate(int year, int month, int day) {
		return (toDate(year, month, day) != null);
	}

	/**
	 * 날짜여부 체크
	 * @param dateStr : 년월일
	 * @return boolean : 날짜여부
	 */
	public static boolean isDate(String dateStr) {
		return (toDate(dateStr) != null);
	}

	/**
	 * 날짜여부 체크
	 * @param dateStr : 년월일
	 * @param format : 날짜형식(ex : yyyyMMdd, yyyy-MM-dd...)
	 * @return boolean : 날짜여부
	 */
	public static boolean isDate(String dateStr, String format) {
		return (toDate(dateStr, format) != null);
	}

	/**
	 * 원하는 날짜 Date 생성
	 * @param year : 년
	 * @param month : 월
	 * @param day : 일
	 * @return Date : 원하는 날짜의 Date객체
	 */
	public static Date toDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setLenient(false);
		cal.set(year, month - 1, day);
		return cal.getTime();
	}

	/**
	 * 원하는 날짜 Date 생성
	 * @param dateStr : 년월일(yyyy-MM-dd)
	 * @return Date : 원하는 날짜의 Date객체
	 */
	public static Date toDate(String dateStr) {
		return toDate(dateStr.replaceAll("[-|/|.]", ""), "yyyyMMdd");
	}

	/**
	 * 원하는 날짜 Date 생성
	 * @param dateStr : 년월일
	 * @param format : 날짜형식(ex : yyyyMMdd, yyyy-MM-dd...)
	 * @return Date : 원하는 날짜의 Date객체
	 */
	public static Date toDate(String dateStr, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException pe) {
			return null;
		}
	}

	/**
	 * Date 객체를 String으로 변환(yyyy-MM-dd)
	 * @param date : 날짜객체
	 * @return String : 날짜의 String 객체(yyyy-MM-dd)
	 */
	public static String toString(Date date) {
		return toString(date, "yyyy-MM-dd");
	}

	/**
	 * Date 객체를 String으로 변환
	 * @param date : 날짜객체
	 * @param format : 원하는 날짜형식(ex : yyyyMMdd, yyyy-MM-dd...)
	 * @return String : 날짜의 String 객체
	 */
	public static String toString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 두 날짜 사이의 차
	 * @param startday : 시작일
	 * @param endday : 종료일
	 * @return 두 날짜 사이의 차
	 */
	public static long getDateDiff(Date startday, Date endday) {
		long diff = endday.getTime() - startday.getTime();
		return (diff / (1000 * 60 * 60 * 24));
	}
}