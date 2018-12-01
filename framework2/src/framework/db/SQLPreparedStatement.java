/* 
 * @(#)SQLPreparedStatement.java
 * Prepared Statement 를 이용하기 위한 객체
 */
package framework.db;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SQLPreparedStatement extends DBStatement {
	private String _sql;
	private ConnectionManager _connMgr = null;
	private PreparedStatement _pstmt = null;
	private RecordSet _rs = null;
	private int _upCnt = 0;
	private List<Object> _param = new ArrayList<Object>();
	private Object _caller = null;

	public SQLPreparedStatement(String sql, ConnectionManager connMgr, Object caller) {
		this._sql = sql;
		this._connMgr = connMgr;
		this._caller = caller;
	}

	@Override
	public void close() throws SQLException {
		try {
			if (_pstmt != null) {
				_pstmt.close();
				_pstmt = null;
			}
			clearParam();
		} catch (SQLException e) {
			getLogger().error("close Error!");
			throw e;
		}
	}

	public void clearParam() {
		this._param = new ArrayList<Object>();
	}

	public RecordSet executeQuery() throws SQLException {
		return executeQuery(0, 0);
	}

	public RecordSet executeQuery(int currPage, int pageSize) throws SQLException {
		if (getSQL() == null) {
			getLogger().error("Query is Null");
			return null;
		}
		try {
			PreparedStatement pstmt = getPrepareStatment();
			if (getParamSize() > 0) {
				for (int i = 1; i <= getParamSize(); i++) {
					Object param = getObject(i - 1);
					if (param == null || "".equals(param)) {
						pstmt.setNull(i, java.sql.Types.VARCHAR);
					} else if (param instanceof java.util.Date) {
						java.util.Date d = (java.util.Date) param;
						pstmt.setObject(i, new java.sql.Timestamp(d.getTime()));
					} else {
						pstmt.setObject(i, param);
					}
				}
			}
			if (getLogger().isDebugEnabled()) {
				StringBuilder log = new StringBuilder();
				log.append("@Sql Start (P_STATEMENT) FetchSize : " + pstmt.getFetchSize() + " Caller : " + _caller.getClass().getName() + "\n");
				log.append("@Sql Command: \n" + getQueryString());
				getLogger().debug(log.toString());
			}
			_rs = new RecordSet(pstmt.executeQuery(), currPage, pageSize);
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("@Sql End (P_STATEMENT)");
			}
		} catch (SQLException e) {
			getLogger().error("executeQuery Error!");
			throw new SQLException(e.getMessage() + "\nSQL : " + getQueryString());
		}
		return _rs;
	}

	public RecordSet executeQuery(String sql) throws SQLException {
		setSQL(sql);
		return executeQuery(0, 0);
	}

	public RecordSet executeQuery(String sql, int currPage, int pageSize) throws SQLException {
		setSQL(sql);
		return executeQuery(currPage, pageSize);
	}

	public int executeUpdate() throws SQLException {
		if (getSQL() == null) {
			getLogger().error("Query is Null");
			return 0;
		}
		try {
			PreparedStatement pstmt = getPrepareStatment();
			if (getParamSize() > 0) {
				for (int i = 1; i <= getParamSize(); i++) {
					Object param = getObject(i - 1);
					if (param == null || "".equals(param)) {
						pstmt.setNull(i, java.sql.Types.VARCHAR);
					} else if (param instanceof CharSequence) {
						pstmt.setString(i, param.toString());
					} else if (param instanceof byte[]) {
						int size = ((byte[]) param).length;
						if (size > 0) {
							InputStream is = new ByteArrayInputStream((byte[]) param);
							pstmt.setBinaryStream(i, is, size);
						} else {
							pstmt.setBinaryStream(i, null, 0);
						}
					} else if (param instanceof java.util.Date) {
						java.util.Date d = (java.util.Date) param;
						pstmt.setObject(i, new java.sql.Timestamp(d.getTime()));
					} else {
						pstmt.setObject(i, param);
					}
				}
			}
			if (getLogger().isDebugEnabled()) {
				StringBuilder log = new StringBuilder();
				log.append("@Sql Start (P_STATEMENT) FetchSize : " + pstmt.getFetchSize() + " Caller : " + _caller.getClass().getName() + "\n");
				log.append("@Sql Command: \n" + getQueryString());
				getLogger().debug(log.toString());
			}
			_upCnt = pstmt.executeUpdate();
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("@Sql End (P_STATEMENT)");
			}
		} catch (SQLException e) {
			getLogger().error("executeUpdate Error!");
			throw new SQLException(e.getMessage() + "\nSQL : " + getQueryString());
		}
		return _upCnt;
	}

	public int executeUpdate(String sql) throws Throwable {
		setSQL(sql);
		return executeUpdate();
	}

	public Object getObject(int idx) {
		return this._param.get(idx);
	}

	public Object[] getParams() {
		if (_param == null)
			return null;
		return _param.toArray();
	}

	public int getParamSize() {
		return _param.size();
	}

	protected PreparedStatement getPrepareStatment() throws SQLException {
		if (getSQL() == null) {
			getLogger().error("Query is Null");
			return null;
		}
		try {
			if (_pstmt == null) {
				_pstmt = _connMgr.getConnection().prepareStatement(getSQL(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				_pstmt.setFetchSize(100);
			}
		} catch (SQLException e) {
			getLogger().error("getPrepareStatment Error!");
			throw e;
		}
		return _pstmt;
	}

	public RecordSet getRecordSet() {
		return this._rs;
	}

	public String getSQL() {
		return this._sql;
	}

	public String getString(int idx) {
		return (String) getObject(idx);
	}

	public int getUpdateCount() {
		return this._upCnt;
	}

	public void set(Object[] obj) {
		clearParam();
		for (int i = 0; i < obj.length; i++) {
			set(i, obj[i]);
		}
	}

	public void set(int idx, double value) {
		set(idx, Double.valueOf(value));
	}

	public void set(int idx, int value) {
		set(idx, Integer.valueOf(value));
	}

	public void set(int idx, long value) {
		set(idx, Long.valueOf(value));
	}

	public void set(int idx, Object obj) {
		_param.add(idx, obj);
	}

	public void set(int idx, byte[] value) {
		set(idx, (Object) value);
	}

	public void setSQL(String newSql) throws SQLException {
		close();
		_sql = newSql;
	}

	@Override
	public String toString() {
		return "SQL : " + getSQL();
	}

	public String getQueryString() {
		Object value = null;
		int qMarkCount = 0;
		StringTokenizer token = new StringTokenizer(getSQL(), "?");
		StringBuilder buf = new StringBuilder();
		while (token.hasMoreTokens()) {
			String oneChunk = token.nextToken();
			buf.append(oneChunk);
			try {
				if (_param.size() > qMarkCount) {
					value = _param.get(qMarkCount++);
					if (value == null || "".equals(value)) {
						value = "NULL";
					} else if (value instanceof CharSequence) {
						value = "'" + value + "'";
					} else if (value instanceof java.util.Date) {
						java.util.Date d = (java.util.Date) value;
						value = "'" + new java.sql.Timestamp(d.getTime()) + "'";
					}
				} else {
					if (token.hasMoreTokens()) {
						value = null;
					} else {
						value = "";
					}
				}
				buf.append("" + value);
			} catch (Throwable e) {
				buf.append("ERROR WHEN PRODUCING QUERY STRING FOR LOG." + e.toString());
			}
		}
		return buf.toString().trim();
	}
}