/**
 * @(#)Box.java
 */
package framework.action;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import framework.util.StringUtil;

/** 
 * 요청객체, 쿠키객체의 값을 담는 해시테이블 객체이다.
 * 요청객체의 파라미터를 추상화 하여 Box 를 생성해 놓고 파라미터이름을 키로 해당 값을 원하는 데이타 타입으로 반환받는다.
 */
public class Box extends HashMap<String, String[]> {
	private static final long serialVersionUID = 7143941735208780214L;
	private String _name = null;

	/***
	 * Box 생성자
	 * @param name Box 객체의 이름
	 */
	public Box(String name) {
		super();
		this._name = name;
	}

	/** 
	 * 요청객체의 파라미터 이름과 값을 저장한 해시테이블을 생성한다.
	 * <br>
	 * ex) request Box 객체를 얻는 경우 => Box box = Box.getBox(request)
	 * 
	 * @param request HTTP 클라이언트 요청객체
	 * 
	 * @return 요청Box 객체
	 */
	public static Box getBox(HttpServletRequest request) {
		Box box = new Box("requestbox");
		for (Object obj : request.getParameterMap().keySet()) {
			String key = (String) obj;
			box.put(key, request.getParameterValues(key));
		}
		return box;
	}

	/** 
	 * 요청객체의 쿠키 이름과 값을 저장한 해시테이블을 생성한다.
	 * <br>
	 * ex) cookie Box 객체를 얻는 경우 => Box box = Box.getBoxFromCookie(request)
	 * 
	 * @param request HTTP 클라이언트 요청객체
	 * 
	 * @return 쿠키Box 객체
	 */
	public static Box getBoxFromCookie(HttpServletRequest request) {
		Box cookiebox = new Box("cookiebox");
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return cookiebox;
		}
		for (Cookie cookie : cookies) {
			cookiebox.put(cookie.getName(), new String[] { cookie.getValue() == null ? "" : cookie.getValue() });
		}
		return cookiebox;
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 값(value)문자열을 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 값
	 */
	public String get(String key) {
		return get(key, "");
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 값(value)문자열을 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public String get(String key, String defaultValue) {
		String[] value = super.get(key);
		if (value == null || value.length == 0) {
			return defaultValue;
		}
		return value[0];
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 문자열 배열을 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 값
	 */
	public String[] getArray(String key) {
		return getArray(key, new String[] {});
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 문자열 배열을 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public String[] getArray(String key, String[] defaultValue) {
		String[] value = super.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Boolean 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 값
	 */
	public Boolean getBoolean(String key) {
		return getBoolean(key, Boolean.FALSE);
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Boolean 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public Boolean getBoolean(String key, Boolean defaultValue) {
		String value = getString(key).trim();
		if (value.isEmpty()) {
			return defaultValue;
		}
		return Boolean.valueOf(value);
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Double 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 값
	 */
	public Double getDouble(String key) {
		return getDouble(key, Double.valueOf(0));
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Double 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public Double getDouble(String key, Double defaultValue) {
		try {
			String value = getString(key).trim().replaceAll(",", "");
			if (value.isEmpty()) {
				return defaultValue;
			}
			return Double.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 BigDecimal 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 값
	 */
	public BigDecimal getBigDecimal(String key) {
		return getBigDecimal(key, BigDecimal.ZERO);
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 BigDecimal 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		try {
			String value = getString(key).trim().replaceAll(",", "");
			if (value.isEmpty()) {
				return defaultValue;
			}
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Float 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 값
	 */
	public Float getFloat(String key) {
		return getFloat(key, Float.valueOf(0));
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Float 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public Float getFloat(String key, Float defaultValue) {
		try {
			String value = getString(key).trim().replaceAll(",", "");
			if (value.isEmpty()) {
				return defaultValue;
			}
			return Float.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Integer 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 값
	 */
	public Integer getInteger(String key) {
		return getInteger(key, Integer.valueOf(0));
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Integer 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public Integer getInteger(String key, Integer defaultValue) {
		try {
			String value = getString(key).trim().replaceAll(",", "");
			if (value.isEmpty()) {
				return defaultValue;
			}
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Long 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 값
	 */
	public Long getLong(String key) {
		return getLong(key, Long.valueOf(0));
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Long 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public Long getLong(String key, Long defaultValue) {
		try {
			String value = getString(key).trim().replaceAll(",", "");
			if (value.isEmpty()) {
				return defaultValue;
			}
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 String 객체를 리턴한다.
	 * 크로스사이트 스크립팅 공격 방지를 위해 &lt;, &gt; 치환을 수행한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 값
	 */
	public String getString(String key) {
		return getString(key, "");
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 String 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public String getString(String key, String defaultValue) {
		String value = get(key);
		if (value.isEmpty()) {
			return defaultValue;
		}
		return StringUtil.escapeHtmlSpecialChars(value);
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 String 객체를 변환없이 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @return key에 매핑되어 있는 값
	 */
	public String getRawString(String key) {
		return getRawString(key, "");
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 String 객체를 변환없이 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public String getRawString(String key, String defaultValue) {
		String value = get(key);
		if (value.isEmpty()) {
			return defaultValue;
		}
		return value;
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Date 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열(기본형식: yyyy-MM-dd)
	 * @return key에 매핑되어 있는 값
	 */
	public Date getDate(String key) {
		return getDate(key, (Date) null);
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Date 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열(기본형식: yyyy-MM-dd)
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public Date getDate(String key, Date defaultValue) {
		String value = getString(key).trim().replaceAll("[^\\d]", "");
		if (value.isEmpty()) {
			return defaultValue;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setLenient(false);
		try {
			return sdf.parse(value);
		} catch (ParseException e) {
			return defaultValue;
		}
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Date 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param format 날짜 포맷(예, yyyy-MM-dd HH:mm:ss)
	 * @return key에 매핑되어 있는 값
	 */
	public Date getDate(String key, String format) {
		return getDate(key, format, (Date) null);
	}

	/** 
	 * 키(key)문자열과 매핑되어 있는 Date 객체를 리턴한다.
	 * @param key 값을 찾기 위한 키 문자열
	 * @param format 날짜 포맷(예, yyyy-MM-dd HH:mm:ss)
	 * @param defaultValue 값이 없을 때 리턴할 기본 값
	 * @return key에 매핑되어 있는 값 또는 기본 값
	 */
	public Date getDate(String key, String format, Date defaultValue) {
		String value = getString(key).trim();
		if (value.isEmpty()) {
			return defaultValue;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
		try {
			return sdf.parse(value);
		} catch (ParseException e) {
			return defaultValue;
		}
	}

	/**
	 * 키(key)에 매핑되는 스트링을 셋팅한다.
	 * 
	 * @param key 값을 찾기 위한 키 문자열
	 * @param value 키에 매핑되는 문자열
	 * @return 원래 key에 매핑되어 있는 스트링 배열
	 */
	public String[] put(String key, String value) {
		return put(key, new String[] { value });
	}

	/** 
	 * Box 객체가 가지고 있는 값들을 화면 출력을 위해 문자열로 변환한다.
	 * 
	 * @return 화면에 출력하기 위해 변환된 문자열
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("{ ");
		long currentRow = 0;
		for (String key : this.keySet()) {
			String value = null;
			Object o = this.get(key);
			if (o == null) {
				value = "";
			} else {
				if (o.getClass().isArray()) {
					int length = Array.getLength(o);
					if (length == 0) {
						value = "";
					} else if (length == 1) {
						Object item = Array.get(o, 0);
						if (item == null) {
							value = "";
						} else {
							value = item.toString();
						}
					} else {
						StringBuilder valueBuf = new StringBuilder();
						valueBuf.append("[");
						for (int j = 0; j < length; j++) {
							Object item = Array.get(o, j);
							if (item != null) {
								valueBuf.append(item.toString());
							}
							if (j < length - 1) {
								valueBuf.append(",");
							}
						}
						valueBuf.append("]");
						value = valueBuf.toString();
					}
				} else {
					value = o.toString();
				}
			}
			if (currentRow++ > 0) {
				buf.append(", ");
			}
			buf.append(key + "=" + value);
		}
		buf.append(" }");
		return "Box[" + _name + "]=" + buf.toString();
	}

	/** 
	 * Box 객체가 가지고 있는 값들을 쿼리 스트링으로 변환한다.
	 * 
	 * @return 쿼리 스트링으로 변환된 문자열
	 */
	public String toQueryString() {
		StringBuilder buf = new StringBuilder();
		long currentRow = 0;
		for (String key : this.keySet()) {
			Object o = this.get(key);
			if (currentRow++ > 0) {
				buf.append("&");
			}
			if (o == null) {
				buf.append(key + "=" + "");
			} else {
				if (o.getClass().isArray()) {
					StringBuilder valueBuf = new StringBuilder();
					for (int j = 0, length = Array.getLength(o); j < length; j++) {
						Object item = Array.get(o, j);
						if (item != null) {
							valueBuf.append(key + "=" + item.toString());
						}
						if (j < length - 1) {
							valueBuf.append("&");
						}
					}
					buf.append(valueBuf.toString());
				} else {
					buf.append(key + "=" + o.toString());
				}
			}
		}
		return buf.toString();
	}

	/** 
	 * Box 객체가 가지고 있는 값들을 Xml로 변환한다.
	 * 
	 * @return Xml로 변환된 문자열
	 */
	public String toXml() {
		StringBuilder buf = new StringBuilder();
		buf.append("<items>");
		buf.append("<item>");
		for (String key : this.keySet()) {
			Object o = this.get(key);
			if (o == null || "".equals(o)) {
				buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
			} else {
				if (o.getClass().isArray()) {
					int length = Array.getLength(o);
					if (length == 0) {
						buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
					} else if (length == 1) {
						Object item = Array.get(o, 0);
						if (item == null || "".equals(item)) {
							buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
						} else {
							buf.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + item.toString() + "]]>" + "</" + key.toLowerCase() + ">");
						}
					} else {
						for (int j = 0; j < length; j++) {
							Object item = Array.get(o, j);
							if (item == null || "".equals(item)) {
								buf.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
							} else {
								buf.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + item.toString() + "]]>" + "</" + key.toLowerCase() + ">");
							}
						}
					}
				} else {
					buf.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + o.toString() + "]]>" + "</" + key.toLowerCase() + ">");
				}
			}
		}
		buf.append("</item>");
		buf.append("</items>");
		return buf.toString();
	}

	/** 
	 * Box 객체가 가지고 있는 값들을 Json 표기법으로 변환한다.
	 * 
	 * @return Json 표기법으로 변환된 문자열
	 */
	public String toJson() {
		StringBuilder buf = new StringBuilder();
		buf.append("{ ");
		long currentRow = 0;
		for (String key : this.keySet()) {
			String value = null;
			Object o = this.get(key);
			if (o == null) {
				value = "\"\"";
			} else {
				if (o.getClass().isArray()) {
					int length = Array.getLength(o);
					if (length == 0) {
						value = "\"\"";
					} else if (length == 1) {
						Object item = Array.get(o, 0);
						if (item == null) {
							value = "\"\"";
						} else {
							value = "\"" + escapeJS(item.toString()) + "\"";
						}
					} else {
						StringBuilder valueBuf = new StringBuilder();
						valueBuf.append("[");
						for (int j = 0; j < length; j++) {
							Object item = Array.get(o, j);
							if (item != null) {
								valueBuf.append("\"" + escapeJS(item.toString()) + "\"");
							}
							if (j < length - 1) {
								valueBuf.append(",");
							}
						}
						valueBuf.append("]");
						value = valueBuf.toString();
					}
				} else {
					value = "\"" + escapeJS(o.toString()) + "\"";
				}
			}
			if (currentRow++ > 0) {
				buf.append(", ");
			}
			buf.append("\"" + escapeJS(key) + "\"" + ":" + value);
		}
		buf.append(" }");
		return buf.toString();
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드
	/**
	 * 자바스크립트상에 특수하게 인식되는 문자들을 JSON등에 사용하기 위해 변환하여준다.
	 * 
	 * @param str 변환할 문자열
	 */
	private String escapeJS(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}
}