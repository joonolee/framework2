import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class DaogenForMSSQL {
	private static final String _filePath = "xml";
	private static String _jdbcDriver = "";
	private static String _jdbcUrl = "";
	private static String _jdbcUid = "";
	private static String _jdbcPwd = "";

	// 테이블 목록
	private static List<String> _tbList = Arrays.asList();

	public static void main(String[] args) throws Exception {
		// db properties setting
		ResourceBundle bundle = ResourceBundle.getBundle("db");
		_jdbcDriver = bundle.getString("jdbc.driver").trim();
		_jdbcUrl = bundle.getString("jdbc.url").trim();
		_jdbcUid = bundle.getString("jdbc.uid").trim();
		_jdbcPwd = bundle.getString("jdbc.pwd").trim();
		// table list
		if (_tbList.size() == 0) {
			_tbList = Arrays.asList(args);
		}
		Connection conn = null;
		Statement stmt = null;
		Statement stmt2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			System.out.println("MS-SQL JDBC Driver Loading.....");
			DriverManager.registerDriver((Driver) Class.forName(_jdbcDriver).newInstance());
			conn = DriverManager.getConnection(_jdbcUrl, _jdbcUid, _jdbcPwd);
			System.out.println("MS-SQL JDBC Driver Loading Complete\n");
			stmt = conn.createStatement();
			stmt2 = conn.createStatement();
			if (_tbList != null && _tbList.size() > 0) {
				for (String tableName : _tbList) {
					rs = stmt.executeQuery("SELECT TOP 1 * FROM " + tableName);
					ResultSetMetaData meta = rs.getMetaData();
					System.out.println(tableName);
					write(meta, tableName, conn);
					rs.close();
				}
			} else {
				String TABLE = null;
				rs2 = stmt2.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES ");

				while (rs2.next()) {
					TABLE = rs2.getString(1);
					rs = stmt.executeQuery("SELECT TOP 1 * FROM " + TABLE);
					ResultSetMetaData meta = rs.getMetaData();
					System.out.println(TABLE);
					write(meta, TABLE, conn);
					rs = null;
				}
			}
			stmt.close();
			conn.close();
		} catch (Throwable e) {
			e.printStackTrace();
			if (rs != null)
				rs.close();
			if (rs2 != null)
				rs2.close();
			if (stmt != null)
				stmt.close();
			if (stmt2 != null)
				stmt2.close();
			if (conn != null)
				conn.close();
		}
	}

	private static void write(ResultSetMetaData meta, String name, Connection conn2) throws Throwable {
		ResultSet rs3 = null;
		Statement stmt3 = null;
		boolean pkProcess = false;
		List<String> primaryKeyList = new LinkedList<String>();
		try {
			stmt3 = conn2.createStatement();
			StringBuffer strPK = new StringBuffer();
			strPK.append("SELECT COL.COLUMN_NAME  ");
			strPK.append("FROM SYSOBJECTS CONS ");
			strPK.append("	INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE COL ON CONS.NAME = COL.CONSTRAINT_NAME ");
			strPK.append("WHERE CONS.XTYPE = 'PK' ");
			strPK.append("	AND COL.TABLE_NAME = '" + name.trim() + "' ");
			strPK.append("ORDER BY COL.ORDINAL_POSITION ");
			rs3 = stmt3.executeQuery(strPK.toString());
			pkProcess = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (pkProcess) {
			while (rs3.next()) {
				primaryKeyList.add(rs3.getString("COLUMN_NAME"));
			}
		}
		File dir = new File(_filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(_filePath, name + ".xml");
		if (file.exists()) {
			file.delete();
		}
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
		bw.write("<table name=\"" + name + "\"  schema=\"" + _jdbcUid + "\" class=\"" + name + "\">\n");
		bw.write("<description></description>\n");
		bw.write("<columns>\n");
		for (int c = 1; c <= meta.getColumnCount(); c++) {
			StringBuffer str = new StringBuffer();
			str.append("<column name=\"");
			str.append(meta.getColumnName(c));
			str.append("\" type=\"");
			str.append(getJavaType(meta.getColumnType(c), meta.getPrecision(c), meta.getScale(c)));
			str.append("\" dbType=\"");
			str.append(getDBType(meta.getColumnType(c), meta.getPrecision(c), meta.getScale(c)));
			str.append("\" desc=\"\" notnull=\"");
			str.append((meta.isNullable(c) == 0 ? "true" : "false") + "\"");
			if (meta.getColumnName(c).equals("ENTERID") || meta.getColumnName(c).equals("ENTERNAME") || meta.getColumnName(c).equals("ENTERPGM")) {
				str.append(" update=\"none\"");
			}
			// 입력일, 수정일에 대한 별도 처리
			if (meta.getColumnName(c).equals("ENTERDATE")) {
				str.append(" insert=\"GETDATE()\" update=\"none\"");
			}
			if (meta.getColumnName(c).equals("UPDATEDATE")) {
				str.append(" insert=\"none\" update=\"GETDATE()\"");
			}
			if (primaryKeyList.contains(meta.getColumnName(c))) {
				str.append(" primarykey=\"true\"");
			}
			str.append(" />\n");
			bw.write(str.toString());
		}
		bw.write("</columns>\n");
		bw.write("</table>");
		bw.close();
		fw.close();
		System.out.println("info : " + _filePath + "/" + name + ".xml create end ");
		String cmd = "";
		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			cmd = "run.bat " + name;
		} else {
			cmd = "run.sh " + name;
		}
		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
	}

	private static String getJavaType(int type, int len, int s) {
		switch (type) {
		case Types.INTEGER:
		case Types.SMALLINT:
			return "Integer";
		case Types.DECIMAL:
		case Types.NUMERIC:
			if (s == 0 && len < 8) {
				return "Integer";
			} else if (s == 0) {
				return "Long";
			} else {
				return "BigDecimal";
			}
		case Types.DATE:
			return "String";
		default:
			return "String";
		}
	}

	private static String getDBType(int type, int len, int s) {
		switch (type) {
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.DECIMAL:
		case Types.NUMERIC:
			return "number(" + len + (s == 0 ? ")" : "." + s + ")");
		case Types.VARCHAR:
			return "varchar(" + len + ")";
		case Types.NVARCHAR:
			return "nvarchar(" + len + ")";
		case Types.CHAR:
			return "char(" + len + ")";
		case Types.NCHAR:
			return "nchar(" + len + ")";
		case Types.DATE:
			return "date";
		default:
			return type + "";
		}
	}
}