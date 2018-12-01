/*
 * @(#)XmlUtil.java
 */
package framework.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import framework.db.ColumnNotFoundException;
import framework.db.RecordSet;

/**
 * XML을 이용하여 개발할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class XmlUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private XmlUtil() {
	}

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet 이용

	/**
	 * RecordSet을 xml 형식으로 출력한다. (xml 헤더포함). XmlUtil.setRecordSet과 동일
	 * <br>
	 * ex) response로 rs를 xml 형식으로 출력하는 경우: XmlUtil.render(response, rs, "utf-8")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs xml 형식으로 변환할 RecordSet 객체
	 * @param encoding 헤더에 포함될 인코딩
	 * 
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int render(HttpServletResponse response, RecordSet rs, String encoding) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs, encoding);
	}

	/**
	 * RecordSet을 xml 형식으로 출력한다. (xml 헤더포함)
	 * <br>
	 * ex) response로 rs를 xml 형식으로 출력하는 경우: XmlUtil.setRecordSet(response, rs, "utf-8")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs xml 형식으로 변환할 RecordSet 객체
	 * @param encoding 헤더에 포함될 인코딩
	 * 
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs, String encoding) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		pw.print(xmlHeaderStr(encoding));
		pw.print("<items>");
		int rowCount = 0;
		while (rs.nextRow()) {
			rowCount++;
			pw.print(xmlItemStr(rs, colNms));
		}
		pw.print("</items>");
		return rowCount;
	}

	/**
	 * RecordSet을 xml 형식으로 변환한다. (xml 헤더 미포함). XmlUtil.format과 동일
	 * <br>
	 * ex) rs를 xml 형식으로 변환하는 경우: String xml = XmlUtil.render(rs)
	 *
	 * @param rs xml 형식으로 변환할 RecordSet 객체
	 *
	 * @return xml 형식으로 변환된 문자열
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static String render(RecordSet rs) throws ColumnNotFoundException {
		return format(rs);
	}

	/**
	 * RecordSet을 xml 형식으로 변환한다. (xml 헤더 미포함)
	 * <br>
	 * ex) rs를 xml 형식으로 변환하는 경우: String xml = XmlUtil.format(rs)
	 *
	 * @param rs xml 형식으로 변환할 RecordSet 객체
	 *
	 * @return xml 형식으로 변환된 문자열
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static String format(RecordSet rs) throws ColumnNotFoundException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		buffer.append("<items>");
		while (rs.nextRow()) {
			buffer.append(xmlItemStr(rs, colNms));
		}
		buffer.append("</items>");
		return buffer.toString();
	}

	/**
	 * RecordSet을 xml 형식으로 변환한다. (xml 헤더포함). XmlUtil.format과 동일
	 * <br>
	 * ex) rs를 xml 형식으로 변환하는 경우: String xml = XmlUtil.render(rs, "utf-8")
	 *
	 * @param rs xml 형식으로 변환할 RecordSet 객체
	 * @param encoding 헤더에 포함될 인코딩
	 *
	 * @return xml 형식으로 변환된 문자열
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static String render(RecordSet rs, String encoding) throws ColumnNotFoundException {
		return format(rs, encoding);
	}

	/**
	 * RecordSet을 xml 형식으로 변환한다. (xml 헤더포함)
	 * <br>
	 * ex) rs를 xml 형식으로 변환하는 경우: String xml = XmlUtil.format(rs, "utf-8")
	 *
	 * @param rs xml 형식으로 변환할 RecordSet 객체
	 * @param encoding 헤더에 포함될 인코딩
	 *
	 * @return xml 형식으로 변환된 문자열
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static String format(RecordSet rs, String encoding) throws ColumnNotFoundException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(xmlHeaderStr(encoding));
		buffer.append(format(rs));
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet 이용

	/**
	 * ResultSet을 xml 형식으로 출력한다 (xml 헤더포함). XmlUtil.setResultSet과 동일
	 * <br>
	 * ex) response로 rs를 xml 형식으로 출력하는 경우: XmlUtil.render(response, rs, "utf-8")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs xml 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param encoding 헤더에 포함될 인코딩
	 * 
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int render(HttpServletResponse response, ResultSet rs, String encoding) throws SQLException, IOException {
		return setResultSet(response, rs, encoding);
	}

	/**
	 * ResultSet을 xml 형식으로 출력한다 (xml 헤더포함).
	 * <br>
	 * ex) response로 rs를 xml 형식으로 출력하는 경우: XmlUtil.setResultSet(response, rs, "utf-8")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs xml 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param encoding 헤더에 포함될 인코딩
	 * 
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs, String encoding) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			for (int i = 1; i <= count; i++) {
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			}
			pw.print(xmlHeaderStr(encoding));
			pw.print("<items>");
			int rowCount = 0;
			while (rs.next()) {
				rowCount++;
				pw.print(xmlItemStr(rs, colNms));
			}
			pw.print("</items>");
			return rowCount;
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * ResultSet을 xml 형식으로 변환한다 (xml 헤더 미포함). XmlUtil.format과 동일
	 * <br>
	 * ex) rs를 xml 형식으로 변환하는 경우: String xml = XmlUtil.render(rs)
	 *
	 * @param rs xml 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @throws SQLException SQLException
	 * @return xml 형식 문자열
	 */
	public static String render(ResultSet rs) throws SQLException {
		return format(rs);
	}

	/**
	 * ResultSet을 xml 형식으로 변환한다 (xml 헤더 미포함).
	 * <br>
	 * ex) rs를 xml 형식으로 변환하는 경우: String xml = XmlUtil.format(rs)
	 *
	 * @param rs xml 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @throws SQLException SQLException
	 * @return xml 형식 문자열
	 */
	public static String format(ResultSet rs) throws SQLException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			for (int i = 1; i <= count; i++) {
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			}
			buffer.append("<items>");
			while (rs.next()) {
				buffer.append(xmlItemStr(rs, colNms));
			}
			buffer.append("</items>");
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return buffer.toString();
	}

	/**
	 * ResultSet을 xml 형식으로 변환한다 (xml 헤더포함). XmlUtil.format과 동일
	 * <br>
	 * ex) rs를 xml 형식으로 변환하는 경우: String xml = XmlUtil.render(rs, "utf-8")
	 *
	 * @param rs xml 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param encoding 헤더에 포함될 인코딩
	 * @return xml 형식 문자열
	 * @throws SQLException SQLException
	 */
	public static String render(ResultSet rs, String encoding) throws SQLException {
		return format(rs, encoding);
	}

	/**
	 * ResultSet을 xml 형식으로 변환한다 (xml 헤더포함).
	 * <br>
	 * ex) rs를 xml 형식으로 변환하는 경우: String xml = XmlUtil.format(rs, "utf-8")
	 *
	 * @param rs xml 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param encoding 헤더에 포함될 인코딩
	 * @return xml 형식 문자열
	 * @throws SQLException SQLException
	 */
	public static String format(ResultSet rs, String encoding) throws SQLException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(xmlHeaderStr(encoding));
		buffer.append(format(rs));
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// 기타 Collection 이용

	/**
	 * Map객체를 xml 형식으로 변환한다 (xml 헤더 미포함). XmlUtil.format과 동일
	 * <br>
	 * ex) map을 xml 형식으로 변환하는 경우: String xml = XmlUtil.render(map)
	 *
	 * @param map 변환할 Map객체
	 *
	 * @return xml 형식으로 변환된 문자열
	 */
	public static String render(Map<String, Object> map) {
		return format(map);
	}

	/**
	 * Map객체를 xml 형식으로 변환한다 (xml 헤더 미포함).
	 * <br>
	 * ex) map을 xml 형식으로 변환하는 경우: String xml = XmlUtil.format(map)
	 *
	 * @param map 변환할 Map객체
	 *
	 * @return xml 형식으로 변환된 문자열
	 */
	public static String format(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("<items>");
		buffer.append(xmlItemStr(map));
		buffer.append("</items>");
		return buffer.toString();
	}

	/**
	 * Map객체를 xml 형식으로 변환한다 (xml 헤더포함). XmlUtil.format과 동일
	 * <br>
	 * ex) map을 xml 형식으로 변환하는 경우 : String xml = XmlUtil.render(map, "utf-8")
	 *
	 * @param map 변환할 Map객체
	 * @param encoding 헤더에 포함될 인코딩
	 *
	 * @return xml 형식으로 변환된 문자열
	 */
	public static String render(Map<String, Object> map, String encoding) {
		return format(map, encoding);
	}

	/**
	 * Map객체를 xml 형식으로 변환한다 (xml 헤더포함).
	 * <br>
	 * ex) map을 xml 형식으로 변환하는 경우 : String xml = XmlUtil.format(map, "utf-8")
	 *
	 * @param map 변환할 Map객체
	 * @param encoding 헤더에 포함될 인코딩
	 *
	 * @return xml 형식으로 변환된 문자열
	 */
	public static String format(Map<String, Object> map, String encoding) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(xmlHeaderStr(encoding));
		buffer.append(format(map));
		return buffer.toString();
	}

	/**
	 * List객체를 xml 형태로 변환한다 (xml 헤더 미포함).  XmlUtil.format과 동일
	 * <br>
	 * ex) mapList를 xml으로 변환하는 경우: String xml = XmlUtil.render(mapList)
	 *
	 * @param mapList 변환할 List객체
	 *
	 * @return xml형식으로 변환된 문자열
	 */
	public static String render(List<Map<String, Object>> mapList) {
		return format(mapList);
	}

	/**
	 * List객체를 xml 형태로 변환한다 (xml 헤더 미포함).
	 * <br>
	 * ex) mapList를 xml으로 변환하는 경우: String xml = XmlUtil.format(mapList)
	 *
	 * @param mapList 변환할 List객체
	 *
	 * @return xml형식으로 변환된 문자열
	 */
	public static String format(List<Map<String, Object>> mapList) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("<items>");
		for (Map<String, Object> map : mapList) {
			buffer.append(xmlItemStr(map));
		}
		buffer.append("</items>");
		return buffer.toString();
	}

	/**
	 * List객체를 xml 형태로 변환한다 (xml 헤더포함). XmlUtil.format과 동일
	 * <br>
	 * ex) mapList를 xml으로 변환하는 경우 : String xml = XmlUtil.render(mapList, "utf-8")
	 *
	 * @param mapList 변환할 List객체
	 * @param encoding 헤더에 포함될 인코딩
	 *
	 * @return xml형식으로 변환된 문자열
	 */
	public static String render(List<Map<String, Object>> mapList, String encoding) {
		return format(mapList, encoding);
	}

	/**
	 * List객체를 xml 형태로 변환한다 (xml 헤더포함).
	 * <br>
	 * ex) mapList를 xml으로 변환하는 경우 : String xml = XmlUtil.format(mapList, "utf-8")
	 *
	 * @param mapList 변환할 List객체
	 * @param encoding 헤더에 포함될 인코딩
	 *
	 * @return xml형식으로 변환된 문자열
	 */
	public static String format(List<Map<String, Object>> mapList, String encoding) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(xmlHeaderStr(encoding));
		buffer.append(format(mapList));
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private 메소드

	/**
	 *  xml 헤더 문자열 생성
	 */
	private static String xmlHeaderStr(String encoding) {
		return "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>";
	}

	/**
	 * xml item 문자열 생성
	 */
	@SuppressWarnings("unchecked")
	private static String xmlItemStr(Map<String, Object> map) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("<item>");
		for (Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value == null) {
				buffer.append("<" + key.toLowerCase() + ">" + "</" + key.toLowerCase() + ">");
			} else {
				if (value instanceof Number) {
					buffer.append("<" + key.toLowerCase() + ">" + value.toString() + "</" + key.toLowerCase() + ">");
				} else if (value instanceof Map) {
					buffer.append("<" + key.toLowerCase() + ">" + format((Map<String, Object>) value) + "</" + key.toLowerCase() + ">");
				} else if (value instanceof List) {
					buffer.append("<" + key.toLowerCase() + ">" + format((List<Map<String, Object>>) value) + "</" + key.toLowerCase() + ">");
				} else {
					buffer.append("<" + key.toLowerCase() + ">" + "<![CDATA[" + value.toString() + "]]>" + "</" + key.toLowerCase() + ">");
				}
			}
		}
		buffer.append("</item>");
		return buffer.toString();
	}

	/**
	 * xml item 문자열 생성
	 * @throws ColumnNotFoundException 
	 */
	private static String xmlItemStr(RecordSet rs, String[] colNms) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		buffer.append("<item>");
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.get(colNms[c]);
			if (value == null) {
				buffer.append("<" + colNms[c].toLowerCase() + ">" + "</" + colNms[c].toLowerCase() + ">");
			} else {
				if (value instanceof Number) {
					buffer.append("<" + colNms[c].toLowerCase() + ">" + value.toString() + "</" + colNms[c].toLowerCase() + ">");
				} else {
					buffer.append("<" + colNms[c].toLowerCase() + ">" + "<![CDATA[" + value.toString() + "]]>" + "</" + colNms[c].toLowerCase() + ">");
				}
			}
		}
		buffer.append("</item>");
		return buffer.toString();
	}

	private static String xmlItemStr(ResultSet rs, String[] colNms) throws SQLException {
		StringBuilder buffer = new StringBuilder();
		buffer.append("<item>");
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.getObject(colNms[c]);
			if (value == null) {
				buffer.append("<" + colNms[c].toLowerCase() + ">" + "</" + colNms[c].toLowerCase() + ">");
			} else {
				if (value instanceof Number) {
					buffer.append("<" + colNms[c].toLowerCase() + ">" + value.toString() + "</" + colNms[c].toLowerCase() + ">");
				} else {
					buffer.append("<" + colNms[c].toLowerCase() + ">" + "<![CDATA[" + value.toString() + "]]>" + "</" + colNms[c].toLowerCase() + ">");
				}
			}
		}
		buffer.append("</item>");
		return buffer.toString();
	}
}