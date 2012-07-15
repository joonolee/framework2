/* 
 * @(#)JsonUtil.java
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

import org.stringtree.json.JSONReader;
import org.stringtree.json.JSONWriter;

import framework.db.ColumnNotFoundException;
import framework.db.RecordSet;

/**
 * JSON(JavaScript Object Notation)를 이용하여 개발할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class JsonUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private JsonUtil() {
	}

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet 이용

	/**
	 * RecordSet을 JSON 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 JSON 형식으로 출력하는 경우 => JsonUtil.setRecordSet(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs JSON 형식으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		pw.print("[");
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print(jsonRowStr(rs, colNms));
		}
		pw.print("]");
		return rowCount;
	}

	/**
	 * RecordSet을 Json 배열 형태로 변환한다.
	 * <br>
	 * ex) rs를 JSON 형식으로 변환하는 경우 => String json = JsonUtil.format(rs)
	 * 
	 * @param rs JSON 형식으로 변환할 RecordSet 객체
	 * 
	 * @return JSON 형식으로 변환된 문자열
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		buffer.append("[");
		int rowCount = 0;
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append(jsonRowStr(rs, colNms));
		}
		buffer.append("]");
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet 이용

	/**
	 * ResultSet을 JSON 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 JSON 형식으로 출력하는 경우 => JsonUtil.setResultSet(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs JSON 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @return 처리건수
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs) throws SQLException, IOException {
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
			pw.print("[");
			int rowCount = 0;
			while (rs.next()) {
				if (rowCount++ > 0) {
					pw.print(",");
				}
				pw.print(jsonRowStr(rs, colNms));
			}
			pw.print("]");
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
	 * ResultSet을 Json 배열 형태로 변환한다.
	 * <br>
	 * ex) rs를 JSON 형식으로 변환하는 경우 => String json = JsonUtil.format(rs)
	 * 
	 * @param rs JSON 형식으로 변환할 ResultSet 객체
	 * 
	 * @return JSON 형식으로 변환된 문자열
	 * @throws SQLException 
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
			buffer.append("[");
			int rowCount = 0;
			while (rs.next()) {
				if (rowCount++ > 0) {
					buffer.append(",");
				}
				buffer.append(jsonRowStr(rs, colNms));
			}
			buffer.append("]");
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// 기타 Collection 이용

	/**
	 * Map객체를 JSON 형식으로 변환한다.
	 * <br>
	 * ex) map을 JSON 형식으로 변환하는 경우 => String json = JsonUtil.format(map)
	 *
	 * @param map 변환할 Map객체
	 *
	 * @return JSON 형식으로 변환된 문자열
	 */
	public static String format(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(jsonRowStr(map));
		return buffer.toString();
	}

	/**
	 * List객체를 JSON 형식으로 변환한다.
	 * <br>
	 * ex1) mapList를 JSON 형식으로 변환하는 경우 => String json = JsonUtil.format(mapList)
	 *
	 * @param mapList 변환할 List객체
	 *
	 * @return JSON 형식으로 변환된 문자열
	 */
	public static String format(List<Map<String, Object>> mapList) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		if (mapList.size() > 0) {
			buffer.append("[");
			for (Map<String, Object> map : mapList) {
				buffer.append(jsonRowStr(map));
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}

	/**
	 * 객체를 JSON 형식으로 변환한다.
	 * <br>
	 * ex1) obj를 JSON 형식으로 변환하는 경우 => String json = JsonUtil.stringify(obj)
	 *
	 * @param obj 변환할 객체
	 *
	 * @return JSON 형식으로 변환된 문자열
	 */
	public static String stringify(Object obj) {
		JSONWriter writer = new JSONWriter();
		return writer.write(obj);
	}

	/**
	 * JSON 문자열을 Object 로 변환한다.
	 * <br>
	 * ex1) json를 Object 형식으로 변환하는 경우 => Object obj = JsonUtil.parse(json)
	 *
	 * @param json 변환할 JSON 문자열
	 *
	 * @return Object 형식으로 변환된 객체
	 */
	public static Object parse(String json) {
		JSONReader reader = new JSONReader();
		return reader.read(json);
	}

	/**
	 * JSON 문자열을 예쁘게 들여쓰기를 적용하여 정렬한다.
	 * 
	 * @param json json 변환할 JSON 문자열
	 * @return Object 형식으로 변환된 객체
	 */
	public static String pretty(String json) {
		return pretty(json, "    ");
	}

	/**
	 * JSON 문자열을 예쁘게 들여쓰기를 적용하여 정렬한다.
	 * 
	 * @param json json json 변환할 JSON 문자열
	 * @param indent 들여쓰기에 사용할 문자열
	 * @return Object 형식으로 변환된 객체
	 */
	public static String pretty(String json, String indent) {
		StringBuilder buffer = new StringBuilder();
		int level = 0;
		String target = null;
		for (int i = 0; i < json.length(); i++) {
			target = json.substring(i, i + 1);
			if (target.equals("{") || target.equals("[")) {
				buffer.append(target).append("\n");
				level++;
				for (int j = 0; j < level; j++) {
					buffer.append(indent);
				}
			} else if (target.equals("}") || target.equals("]")) {
				buffer.append("\n");
				level--;
				for (int j = 0; j < level; j++) {
					buffer.append(indent);
				}
				buffer.append(target);
			} else if (target.equals(",")) {
				buffer.append(target);
				buffer.append("\n");
				for (int j = 0; j < level; j++) {
					buffer.append(indent);
				}
			} else {
				buffer.append(target);
			}

		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// 유틸리티

	/**
	 * 자바스크립트상에 특수하게 인식되는 문자들을 JSON등에 사용하기 위해 변환하여준다.
	 * 
	 * @param str 변환할 문자열
	 */
	public static String escapeJS(String str) {
		if (str == null) {
			return "";
		}
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t");
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private 메소드

	/**
	 * JSON 용 Row 문자열 생성
	 */
	@SuppressWarnings("unchecked")
	private static String jsonRowStr(Map<String, Object> map) {
		StringBuilder buffer = new StringBuilder();
		if (map.entrySet().size() > 0) {
			buffer.append("{");
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = "\"" + escapeJS(entry.getKey().toLowerCase()) + "\"";
				Object value = entry.getValue();
				if (value == null) {
					buffer.append(key + ":" + "\"\"");
				} else {
					if (value instanceof Number) {
						buffer.append(key + ":" + value.toString());
					} else if (value instanceof Map) {
						buffer.append(key + ":" + format((Map<String, Object>) value));
					} else if (value instanceof List) {
						buffer.append(key + ":" + format((List<Map<String, Object>>) value));
					} else {
						buffer.append(key + ":" + "\"" + escapeJS(value.toString()) + "\"");
					}
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("}");
		} else {
			buffer.append("{}");
		}
		return buffer.toString();
	}

	/**
	 * JSON 용 Row 문자열 생성
	 * @throws ColumnNotFoundException 
	 */
	private static String jsonRowStr(RecordSet rs, String[] colNms) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
			buffer.append("{");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.get(colNms[c]);
				String key = "\"" + escapeJS(colNms[c].toLowerCase()) + "\"";

				if (value == null) {
					buffer.append(key + ":" + "\"\"");
				} else {
					if (value instanceof Number) {
						buffer.append(key + ":" + value.toString());
					} else {
						buffer.append(key + ":" + "\"" + escapeJS(value.toString()) + "\"");
					}
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("}");
		} else {
			buffer.append("{}");
		}
		return buffer.toString();
	}

	private static String jsonRowStr(ResultSet rs, String[] colNms) throws SQLException {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
			buffer.append("{");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.getObject(colNms[c]);
				String key = "\"" + escapeJS(colNms[c].toLowerCase()) + "\"";

				if (value == null) {
					buffer.append(key + ":" + "\"\"");
				} else {
					if (value instanceof Number) {
						buffer.append(key + ":" + value.toString());
					} else {
						buffer.append(key + ":" + "\"" + escapeJS(value.toString()) + "\"");
					}
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("}");
		} else {
			buffer.append("{}");
		}
		return buffer.toString();
	}
}