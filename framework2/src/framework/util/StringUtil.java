/* 
 * @(#)StringUtil.java
 */
package framework.util;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 스트링 처리 라이브러리
 */
public class StringUtil {
	private static final Pattern TAG_PATTERN = Pattern.compile("<[^<]*?>");
	private static final Pattern SCRIPT_TAG_PATTERN = Pattern.compile("<\\s*[s|S][c|C][r|R][i|I][p|P][t|T].*?>.*?<\\s*/\\s*[s|S][c|C][r|R][i|I][p|P][t|T]\\s*>", Pattern.DOTALL);

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private StringUtil() {
	}

	/**
	 * 특정 기호를 기준으로 스트링을 잘라서 배열로 반환하는 함수
	 * <br>
	 * ex) abc||def||efg -> array[0]:"abc", array[1]:"def", array[2]:"efg"
	 * 
	 * @param str 원본 문자열
	 * @param token 토큰 문자열
	 * 
	 * @return 토큰으로 분리된 문자열의 배열
	 */
	public static String[] tokenFn(String str, String token) {
		StringTokenizer st = null;
		String[] toStr = null;
		int tokenCount = 0;
		int index = 0;
		int len = 0;
		try {
			// token이 두개이상 붙어있으면 token과 token 사이에 공백을 넣는다.
			len = str.length();
			for (int i = 0; i < len; i++) {
				if ((index = str.indexOf(token + token)) != -1) {
					str = str.substring(0, index) + token + " " + token + str.substring(index + 2, str.length());
				}
			}
			st = new StringTokenizer(str, token);
			tokenCount = st.countTokens();
			toStr = new String[tokenCount];
			for (int i = 0; i < tokenCount; i++) {
				toStr[i] = st.nextToken();
			}
		} catch (Exception e) {
			toStr = null;
		}
		return toStr;
	}

	/**
	 * 정해진 길이보다 문자열이 크면 문자열을 잘라서 ".."를 추가해 주는 기능.
	 * 게시판 제목 같은 곳에서 사용됨.
	 * 
	 * @param str 원본 문자열
	 * @param len 유효 문자열 길이
	 * 
	 * @return 유효문자열에 "..." 이 연결된 문자열
	 */
	public static String limitString(String str, int len) {
		String rval = "";
		byte[] bstr = null;
		int bcount = 0; // 인자로 넘어온 스트링의 총 바이트 수
		int scount = 0; // 인자로 넘어온 스트링의 총 글자 수
		int bindex = 0; // 제한하려 하는 바이트의 인덱스
		int i = 0;
		try {
			bstr = str.getBytes();
			bcount = bstr.length;
			if (bcount <= len) {
				rval = str;
			} else {
				scount = str.length();
				for (i = 0; i < scount - 1; i++) {
					int btmplen = str.substring(i, i + 1).getBytes().length;
					bindex += btmplen;
					if (bindex + 3 >= len) {
						break;
					}
				}
				rval = new String(bstr, 0, bindex) + "..";
			}
		} catch (Exception e) {
		}
		return rval;
	}

	/**
	 * 스트링 타입의 날짜 데이타를 정해진 포맷으로 변환하는 함수
	 * 
	 * <br>
	 * ex1) StringUtil.nalDesign("20080101090000", 1) => "2008-01-01"
	 * <br>
	 * ex2) StringUtil.nalDesign("20080101090000", 2) => "08-01-01 09:00"
	 * <br>
	 * ex3) StringUtil.nalDesign("20080101090000", 3) => "09:00"
	 * <br>
	 * ex4) StringUtil.nalDesign("20080101090000", 4) => "01-01"
	 * <br>
	 * ex5) StringUtil.nalDesign("20080101090000", 5) => "08-01-01"
	 * <br>
	 * ex6) StringUtil.nalDesign("20080101090000", 6) => "01-01 09:00"
	 * <br>
	 * ex7) StringUtil.nalDesign("20080101090000", 7) => "2008년 01월 01일"
	 * 
	 * @param str 원본 문자열
	 * @param option 날짜 옵션
	 * 
	 * @return 포맷된 날짜 문자열
	 */
	public static String nalDesign(String str, int option) {
		String returnValue = "";
		if (str != null && str.length() > 7) {
			if (option == 1)
				returnValue = str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8);
			else if (option == 2) // 12자리 이상의 날짜를 인자로 받아서 년 월 일 사이에 "/" 시 분 사이에 ":"를 끼워 넣는다.
				returnValue = str.substring(2, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8) + " " + str.substring(8, 10) + ":" + str.substring(10, 12);
			else if (option == 3) // 12자리 이상의 날짜를 인자로 받아서 시 분 사이에 ":"를 끼워 넣는다.(시,분 만 리턴한다.)
				returnValue = str.substring(8, 10) + ":" + str.substring(10, 12);
			else if (option == 4)
				returnValue = str.substring(4, 6) + "-" + str.substring(6, 8);
			else if (option == 5)
				returnValue = str.substring(2, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8);
			else if (option == 6)
				returnValue = str.substring(4, 6) + "-" + str.substring(6, 8) + " " + str.substring(8, 10) + ":" + str.substring(10, 12);
			else if (option == 7) // 8자리 날짜를 인자로 받아서 2006년 03월 28일 형식으로 만든다.
				returnValue = str.substring(0, 4) + "년 " + str.substring(4, 6) + "월 " + str.substring(6, 8) + "일";
			else
				returnValue = "";
		} else {
			returnValue = "-";
		}
		return returnValue;
	}

	/**
	 * 스트링의 특정 부분을 다른 기호로 변환하는 함수
	 * 
	 * @param src 원본 문자열
	 * @param oldstr 찾을 문자열
	 * @param newstr 바꿀 문자열
	 * 
	 * @return 찾을 문자열이 바꿀 문자열로 변환된 문자열
	 */
	public static String replaceStr(String src, String oldstr, String newstr) {
		if (src == null)
			return null;
		StringBuilder dest = new StringBuilder();
		int len = oldstr.length();
		int srclen = src.length();
		int pos = 0;
		int oldpos = 0;
		while ((pos = src.indexOf(oldstr, oldpos)) >= 0) {
			dest.append(src.substring(oldpos, pos));
			dest.append(newstr);
			oldpos = pos + len;
		}
		if (oldpos < srclen)
			dest.append(src.substring(oldpos, srclen));
		return dest.toString();
	}

	/**
	 * 스트링 타입의 바이트 단위를 사람이 읽기 좋은 형태로 변환(KByte, MByte, GByte)
	 * 
	 * @param stringbyte 스트링으로 표기된 바이트 문자열
	 * 
	 * @return 사람이 읽기 좋은 형태의 문자열 
	 */
	public static String byteToHumanReadable(String stringbyte) {
		double d = 0.0;
		String ret = "";
		try {
			if (stringbyte == null || stringbyte.equals("")) {
				ret = "0 Bytes";
				return ret;
			}
			double dbyte = Double.parseDouble(stringbyte);
			java.text.MessageFormat mf = new java.text.MessageFormat("{0,number,####.#}");
			if (dbyte == 0.0) {
				ret = "0 Bytes";
			} else if (dbyte >= 1024.0 && dbyte < 1048576.0) {
				d = dbyte / 1024.0;
				Object[] objs = { Double.valueOf(d) };
				ret = mf.format(objs);
				ret += " KB";
			} else if (dbyte >= 1048576.0 && dbyte < 1073741824.0) {
				d = dbyte / 1048576.0;
				Object[] objs = { Double.valueOf(d) };
				ret = mf.format(objs);
				ret += " MB";
			} else if (dbyte >= 1073741824.0) {
				d = dbyte / 1073741824.0;
				Object[] objs = { Double.valueOf(d) };
				ret = mf.format(objs);
				ret += " GB";
			} else {
				Object[] objs = { Double.valueOf(dbyte) };
				ret = mf.format(objs);
				ret += " Bytes";
			}
			return (ret);
		} catch (Exception e) {
			return "0 Bytes";
		}
	}

	/**
	 * long 타입의 바이트 단위를 사람이 읽기 좋은 형태로 변환(KByte, MByte, GByte)
	 * 
	 * @param longbyte long타입으로 표기된 바이트 값
	 * 
	 * @return 사람이 읽기 좋은 형태의 문자열
	 */
	public static String byteToHumanReadable(long longbyte) {
		Long L_byte = Long.valueOf(longbyte);
		double d = 0.0;
		String ret = "";
		if (L_byte.toString() == null || L_byte.toString().equals("")) {
			ret = "0 Bytes";
			return ret;
		}
		double dbyte = Double.parseDouble(L_byte.toString());
		java.text.MessageFormat mf = new java.text.MessageFormat("{0,number,####.#}");
		if (dbyte == 0.0) {
			ret = "0 Bytes";
		} else if (dbyte >= 1024.0 && dbyte < 1048576.0) {
			d = dbyte / 1024.0;
			Object[] objs = { Double.valueOf(d) };
			ret = mf.format(objs);
			ret += " KB";
		} else if (dbyte >= 1048576.0 && dbyte < 1073741824.0) {
			d = dbyte / 1048576.0;
			Object[] objs = { Double.valueOf(d) };
			ret = mf.format(objs);
			ret += " MB";
		} else if (dbyte >= 1073741824.0) {
			d = dbyte / 1073741824.0;
			Object[] objs = { Double.valueOf(d) };
			ret = mf.format(objs);
			ret += " GB";
		} else {
			Object[] objs = { Double.valueOf(dbyte) };
			ret = mf.format(objs);
			ret += " Bytes";
		}
		return (ret);
	}

	/**
	 * 인자에 해당하는 스트링의 charter-set을 한글로 변환하는 함수
	 * 
	 * @param str 원본 문자열
	 * 
	 * @return 한글(EUC-KR)로 변환된 문자열
	 * 
	 * @exception java.io.UnsupportedEncodingException
	 */
	public static String convertKorean(String str) throws java.io.UnsupportedEncodingException {
		return new String(str.getBytes("iso-8859-1"), "EUC-KR");
	}

	/**
	 * 인자에 해당하는 스트링의 charter-set을 utf-8로 변환하는 함수
	 * 
	 * @param str 원본 문자열
	 * 
	 * @return 유니코드(UTF-8)로 변환된 문자열
	 * 
	 * @exception java.io.UnsupportedEncodingException
	 */
	public static String convertUTF8(String str) throws java.io.UnsupportedEncodingException {
		return new String(str.getBytes("iso-8859-1"), "utf-8");
	}

	/**
	 * int 타입의 숫자를 숫자형태(세자리마다 ,로 구분)로 변환하는 함수
	 * 
	 * @param num 원본 int형 숫자
	 * 
	 * @return 세자리마다 콤마(,)로 구분된 문자열
	 */
	public static String numberFormat(int num) {
		return numberFormat(Integer.toString(num));
	}

	/**
	 * long 타입의 숫자를 숫자형태(세자리마다 ,로 구분)로 변환하는 함수
	 * 
	 * @param num 원본 long형 숫자
	 * 
	 * @return 세자리마다 콤마(,)로 구분된 문자열
	 */
	public static String numberFormat(long num) {
		return numberFormat(Long.toString(num));
	}

	/**
	 * 스트링 타입의 숫자를 숫자형태(세자리마다 ,로 구분)로 변환하는 함수
	 * 
	 * @param str 원본 문자열
	 * 
	 * @return 세자리마다 콤마(,)로 구분된 문자열
	 */
	public static String numberFormat(String str) {
		try {
			return java.text.NumberFormat.getInstance().format(Integer.parseInt(str));
		} catch (Exception e) {
			return "0";
		}
	}

	/**
	 * 인자에 해당하는 스트링이 null이면 스트링 타입의 null("")로 변환하는 함수
	 * 
	 * @param str 원본 문자열
	 * 
	 * @return 널(null)값을 빈문자("") 로 변환한 문자열
	 */
	public static String nullToBlankString(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	/**
	 * 첫번째 인자에 해당하는 스트링이 null이면 두번째 인자의 값을 반환하는 함수
	 * 
	 * @param str1 원본 문자열
	 * @param str2 스트링이 null 이면 리턴할 문자열
	 * 
	 * @return 널(null)값을 두번째 인자의 값 문자열
	 */
	public static String null2Str(String str1, String str2) {
		if (str1 == null) {
			return str2;
		}
		return str1;
	}

	/**
	 * 오늘 날짜를 인자에 해당하는 형태로 가져오는 함수
	 * 
	 * @param option 1 은 "2000-11-12", 2 는 "2000", 3 은 "11", 4 는 "12", 5 는 "20001112", 6 은 시, 7 은 분, 8 은 초, 9 는 요일별 정수전환, 10은 오늘이 몇번째 주인지
	 * 
	 * @return 오늘날짜를 포맷한 문자열
	 */
	public static String makeToday(int option) {
		Calendar calToday00;
		calToday00 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
		String dayVal = calToday00.get(Calendar.DATE) + "";
		String monthVal = Integer.toString(calToday00.get(Calendar.MONTH) + 1);
		int ampm = calToday00.get(Calendar.AM_PM);
		if (Integer.parseInt(dayVal) < 10)
			dayVal = "0" + dayVal;
		if (Integer.parseInt(monthVal) < 10)
			monthVal = "0" + monthVal;
		String dateVal = "";
		// ===================================================
		//	1 은 "2000-11-12"
		//	2 는 "2000"
		//	3 은 "11"
		//	4 는 "12"
		//	5 는 "20001112"
		//	6 은 시
		//	7 은 분
		//	8 은 초
		//	9 는 요일별 정수전환
		//	10은 오늘이 몇번째 주인지
		// ===================================================
		switch (option) {
		case 1:
			dateVal = Integer.toString(calToday00.get(Calendar.YEAR)) + "-" + monthVal + "-" + dayVal;
			break;
		case 2:
			dateVal = Integer.toString(calToday00.get(Calendar.YEAR));
			break;
		case 3:
			dateVal = monthVal;
			break;
		case 4:
			dateVal = dayVal;
			break;
		case 5:
			dateVal = Integer.toString(calToday00.get(Calendar.YEAR)) + monthVal + dayVal;
			break;
		case 6:
			dateVal = Integer.toString(calToday00.get(Calendar.HOUR) + ampm * 12);
			break;
		case 7:
			dateVal = Integer.toString(calToday00.get(Calendar.MINUTE));
			break;
		case 8:
			dateVal = Integer.toString(calToday00.get(Calendar.SECOND));
			break;
		case 9:
			dateVal = Integer.toString(calToday00.get(Calendar.DAY_OF_WEEK));
			break;
		case 10:
			dateVal = Integer.toString(calToday00.get(Calendar.WEEK_OF_MONTH));
			break;
		}
		return dateVal;
	}

	/**
	 * 인자에 해당하는 날짜로부터 몇 일 이동한 날짜를 가져오는 함수
	 * 
	 * @param curDate 기준 날짜
	 * @param option 1은 day 만큼 이후의 날짜, 2는 day 만큼 이전의 날짜
	 * @param day 이후, 이전으로 계산할 일자(일 단위)
	 * 
	 * @return 변환된 문자열
	 */
	public static String moveDate(String curDate, int option, int day) {
		String destDate = "";
		int curYear;
		int curMonth;
		int curDay;
		Calendar cal;
		curYear = Integer.parseInt(curDate.substring(0, 4));
		curMonth = Integer.parseInt(curDate.substring(4, 6));
		curDay = Integer.parseInt(curDate.substring(6, 8));
		cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
		if (option == 1)
			cal.set(curYear, curMonth - 1, curDay + day); // day 만큼 이후의 날짜.
		else
			cal.set(curYear, curMonth - 1, curDay - day); // day 만큼 이전의 날짜.
		curYear = cal.get(Calendar.YEAR);
		curMonth = cal.get(Calendar.MONTH) + 1;
		curDay = cal.get(Calendar.DATE);
		destDate = Integer.toString(curYear);
		if (curMonth < 10)
			destDate += "0" + Integer.toString(curMonth);
		else
			destDate += Integer.toString(curMonth);
		if (curDay < 10)
			destDate += "0" + Integer.toString(curDay);
		else
			destDate += Integer.toString(curDay);
		return destDate;
	}

	/**
	 * 인자에 해당하는 날짜와 현재 날짜의 간격이 interval에 포함되면 true, 포함되지 않으면 false를 반환하는 함수
	 * interval 의 기본값은 1일로 설정된다.
	 * @param regday 등록 날짜 문자열
	 * 
	 * @return interval 보다 작으면 true, 같거나 크면 false
	 */
	public static boolean isNew(String regday) {
		int default_interval = 1;
		return isNew(regday, default_interval);
	}

	/**
	 * 인자에 해당하는 날짜와 현재 날짜의 간격이 interval에 포함되면 true, 포함되지 않으면 false를 반환하는 함수
	 * 
	 * @param regday 등록 날짜 문자열
	 * @param interval 비교할 시간 간격(일 단위)
	 * 
	 * @return interval 보다 작으면 true, 같거나 크면 false 
	 */
	public static boolean isNew(String regday, int interval) {
		Calendar today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
		Calendar regCal = Calendar.getInstance();
		Date current;
		Date regdate;
		int diffDay;
		boolean isnew;
		try {
			int regYear = Integer.parseInt(regday.substring(0, 4));
			int regMonth = Integer.parseInt(regday.substring(4, 6)) - 1;
			int regDay = Integer.parseInt(regday.substring(6, 8));
			int regHour = Integer.parseInt(regday.substring(8, 10));
			int regMinute = Integer.parseInt(regday.substring(10, 12));
			int regSecond = Integer.parseInt(regday.substring(12, 14));
			regCal.set(regYear, regMonth, regDay, regHour, regMinute, regSecond);
			current = today.getTime();
			regdate = regCal.getTime();
			diffDay = Math.abs((int) ((current.getTime() - regdate.getTime()) / 1000.0 / 60.0 / 60.0 / 24.0));
			isnew = (diffDay < interval) ? true : false;
		} catch (Exception e) {
			isnew = false;
		}
		return isnew;
	}

	/**
	 * 문자열이 빈문자열(null 또는 "")인지 검사하여 빈문자열이면 true, 아니면 false를 반환하는 함수
	 * @param str 체크할 문자열
	 * @return 빈문자열(null 또는 "")이면 true, 아니면 false
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 문자열이 빈문자열(null 또는 "")이 아닌지 검사하여 빈문자열이면 false, 아니면 true를 반환하는 함수
	 * @param str 체크할 문자열
	 * @return 빈문자열(null 또는 "")이면 false, 아니면 true
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 인자에 포함된 모든 태크를 제거하는 함수
	 * @param src 원본문자열
	 * @return 태그가 제거된 문자열
	 */
	public static String stripTag(String src) {
		if (src == null) {
			return "";
		}
		String noTag = src;
		while (TAG_PATTERN.matcher(noTag).find()) {
			noTag = TAG_PATTERN.matcher(noTag).replaceAll("");
		}
		return noTag;
	}

	/**
	 * 인자에 포함된 스크립트 태크를 제거하는 함수
	 * @param src 원본문자열
	 * @return 스크립트 태그가 제거된 문자열
	 */
	public static String stripScriptTag(String src) {
		if (src == null) {
			return "";
		}
		String noScriptTag = src;
		while (SCRIPT_TAG_PATTERN.matcher(noScriptTag).find()) {
			noScriptTag = SCRIPT_TAG_PATTERN.matcher(noScriptTag).replaceAll("");
		}
		return noScriptTag;
	}

	/**
	 * html 특수문자를 일치하는 문자 엔티티로 변환하는 함수 
	 * 
	 * @param src 원본문자열
	 * @return html 특수문자가 escape 된 문자열
	 */
	public static String escapeHtmlSpecialChars(String src) {
		if (src == null) {
			return null;
		}
		StringBuilder result = new StringBuilder(src.length());
		for (int i = 0; i < src.length(); i++) {
			switch (src.charAt(i)) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '&':
				result.append("&amp;");
				break;
			default:
				result.append(src.charAt(i));
				break;
			}
		}
		return result.toString();
	}
}