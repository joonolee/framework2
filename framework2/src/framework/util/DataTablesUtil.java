/*
 * @(#)DataTablesUtil.java
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
 * DataTables 를 이용하여 개발할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class DataTablesUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private DataTablesUtil() {
	}

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet 이용

	/**
	 * RecordSet을 DataTables 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 DataTables 형식으로 출력하는 경우 => DataTablesUtil.setRecordSet(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs DataTables 형식으로 변환할 RecordSet 객체
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
		pw.print("{");
		int rowCount = 0;
		pw.print("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print(dataTablesRowStr(rs, colNms));
		}
		pw.print("]");
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet을 DataTables 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 DataTables 형식으로 출력하는 경우 => DataTablesUtil.setRecordSet(response, rs, new String[] { "col1", "col2" })
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs DataTables 형식으로 변환할 RecordSet 객체
	 * @param colNames 컬럼이름 배열
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs, String[] colNames) throws ColumnNotFoundException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		rs.moveRow(0);
		pw.print("{");
		int rowCount = 0;
		pw.print("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				pw.print(",");
			}
			pw.print(dataTablesRowStr(rs, colNames));
		}
		pw.print("]");
		pw.print("}");
		return rowCount;
	}

	/**
	 * RecordSet을 DataTables 형식으로 변환한다.
	 * <br>
	 * ex) rs를 DataTables 형식으로 변환하는 경우 => String json = DataTablesUtil.format(rs)
	 * 
	 * @param rs DataTables 형식으로 변환할 RecordSet 객체
	 * @return DataTables 형식으로 변환된 문자열
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		String[] colNms = rs.getColumns();
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append(dataTablesRowStr(rs, colNms));
		}
		buffer.append("]");
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * RecordSet을 DataTables 형식으로 변환한다.
	 * <br>
	 * ex) rs를 DataTables 형식으로 변환하는 경우 => String json = DataTablesUtil.format(rs, new String[] { "col1", "col2" })
	 * 
	 * @param rs DataTables 형식으로 변환할 RecordSet 객체
	 * @param colNames 컬럼이름 배열
	 * @return DataTables 형식으로 변환된 문자열
	 * @throws ColumnNotFoundException 
	 */
	public static String format(RecordSet rs, String[] colNames) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (rs == null) {
			return null;
		}
		rs.moveRow(0);
		buffer.append("{");
		int rowCount = 0;
		buffer.append("\"aaData\":[");
		while (rs.nextRow()) {
			if (rowCount++ > 0) {
				buffer.append(",");
			}
			buffer.append(dataTablesRowStr(rs, colNames));
		}
		buffer.append("]");
		buffer.append("}");
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet 이용

	/**
	 * ResultSet을 DataTables 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 DataTables 형식으로 출력하는 경우 => DataTablesUtil.setResultSet(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs DataTables 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
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
			pw.print("{");
			int rowCount = 0;
			pw.print("\"aaData\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					pw.print(",");
				}
				pw.print(dataTablesRowStr(rs, colNms));
			}
			pw.print("]");
			pw.print("}");
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
	 * ResultSet을 DataTables 형식으로 출력한다.
	 * <br>
	 * ex) response로 rs를 DataTables 형식으로 출력하는 경우 => DataTablesUtil.setResultSet(response, rs, new String[] { "col1", "col2" })
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs DataTables 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param colNames 컬럼이름 배열
	 * @return 처리건수
	 * @throws SQLException 
	 * @throws IOException
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs, String[] colNames) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		try {
			pw.print("{");
			int rowCount = 0;
			pw.print("\"aaData\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					pw.print(",");
				}
				pw.print(dataTablesRowStr(rs, colNames));
			}
			pw.print("]");
			pw.print("}");
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
	 * ResultSet을 DataTables 형식으로 변환한다.
	 * <br>
	 * ex) rs를 DataTables 형식으로 변환하는 경우 => String json = DataTablesUtil.format(rs)
	 * 
	 * @param rs DataTables 형식으로 변환할 ResultSet 객체
	 * @return DataTables 형식으로 변환된 문자열
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
			int rowCount = 0;
			buffer.append("{");
			buffer.append("\"aaData\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					buffer.append(",");
				}
				buffer.append(dataTablesRowStr(rs, colNms));
			}
			buffer.append("]");
			buffer.append("}");
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
	 * ResultSet을 DataTables 형식으로 변환한다.
	 * <br>
	 * ex) rs를 DataTables 형식으로 변환하는 경우 => String json = DataTablesUtil.format(rs, new String[] { "col1", "col2" })
	 * 
	 * @param rs DataTables 형식으로 변환할 ResultSet 객체
	 * @param colNames 컬럼이름 배열
	 * @return DataTables 형식으로 변환된 문자열
	 * @throws SQLException 
	 */
	public static String format(ResultSet rs, String[] colNames) throws SQLException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			int rowCount = 0;
			buffer.append("{");
			buffer.append("\"aaData\":[");
			while (rs.next()) {
				if (rowCount++ > 0) {
					buffer.append(",");
				}
				buffer.append(dataTablesRowStr(rs, colNames));
			}
			buffer.append("]");
			buffer.append("}");
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
	 * List객체를 DataTables 형식으로 변환한다.
	 * <br>
	 * ex1) mapList를 DataTables 형식으로 변환하는 경우 => String json = DataTablesUtil.format(mapList)
	 *
	 * @param mapList 변환할 List객체
	 * @return DataTables 형식으로 변환된 문자열
	 */
	public static String format(List<Map<String, Object>> mapList) {
		if (mapList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("{");
		buffer.append("\"aaData\":");
		if (mapList.size() > 0) {
			buffer.append("[");
			for (Map<String, Object> map : mapList) {
				buffer.append(dataTablesRowStr(map));
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		buffer.append("}");
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
		return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\r\n", "\\\\n").replaceAll("\n", "\\\\n");
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private 메소드

	/**
	 * DataTables 용 Row 문자열 생성
	 */
	private static String dataTablesRowStr(Map<String, Object> map) {
		StringBuilder buffer = new StringBuilder();
		if (map.entrySet().size() > 0) {
			buffer.append("[");
			for (Entry<String, Object> entry : map.entrySet()) {
				Object value = entry.getValue();
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
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
	 * DataTables 용 Row 문자열 생성
	 * @throws ColumnNotFoundException 
	 */
	private static String dataTablesRowStr(RecordSet rs, String[] colNms) throws ColumnNotFoundException {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
			buffer.append("[");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.get(colNms[c].toUpperCase());
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}

	private static String dataTablesRowStr(ResultSet rs, String[] colNms) throws SQLException {
		StringBuilder buffer = new StringBuilder();
		if (colNms.length > 0) {
			buffer.append("[");
			for (int c = 0; c < colNms.length; c++) {
				Object value = rs.getObject(colNms[c].toUpperCase());
				if (value == null) {
					buffer.append("\"\"");
				} else {
					buffer.append("\"" + escapeJS(value.toString()) + "\"");
				}
				buffer.append(",");
			}
			buffer.delete(buffer.length() - 1, buffer.length());
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		return buffer.toString();
	}
}
