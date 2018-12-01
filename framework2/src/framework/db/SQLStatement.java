/* 
 * @(#)SQLStatement.java
 * Statement 를 이용하기 위한 객체
 */
package framework.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLStatement extends DBStatement {
	private String _sql;
	private ConnectionManager _connMgr = null;
	private Statement _stmt = null;
	private RecordSet _rs = null;
	private int _upCnt = 0;
	private Object _caller = null;

	public SQLStatement(String sql, ConnectionManager connMgr, Object caller) {
		this._sql = sql;
		this._connMgr = connMgr;
		this._caller = caller;
	}

	protected Statement getStatement() throws SQLException {
		try {
			if (_stmt == null) {
				_stmt = _connMgr.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				_stmt.setFetchSize(100);
			}
		} catch (SQLException e) {
			getLogger().error("getStatement Error!");
			throw e;
		}
		return _stmt;
	}

	@Override
	public void close() throws SQLException {
		if (_stmt != null) {
			try {
				_stmt.close();
			} catch (SQLException e) {
				getLogger().error("close Error!");
				throw e;
			}
		}
	}

	public RecordSet executeQuery(int currPage, int pageSize) throws SQLException {
		if (getSQL() == null) {
			getLogger().error("Query is Null");
			return null;
		}
		try {
			Statement stmt = getStatement();
			if (getLogger().isDebugEnabled()) {
				StringBuilder log = new StringBuilder();
				log.append("@Sql Start (STATEMENT) FetchSize : " + stmt.getFetchSize() + " Caller : " + _caller.getClass().getName() + "\n");
				log.append("@Sql Command: \n" + getSQL());
				getLogger().debug(log.toString());
			}
			_rs = new RecordSet(stmt.executeQuery(getSQL()), currPage, pageSize);
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("@Sql End (STATEMENT)");
			}
		} catch (SQLException e) {
			getLogger().error("executeQuery Error!");
			throw new SQLException(e.getMessage() + "\nSQL : " + getSQL());
		}
		return _rs;
	}

	public RecordSet executeQuery() throws SQLException {
		return executeQuery(0, 0);
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
			Statement stmt = getStatement();
			if (getLogger().isDebugEnabled()) {
				StringBuilder log = new StringBuilder();
				log.append("@Sql Start (STATEMENT) FetchSize : " + stmt.getFetchSize() + " Caller : " + _caller.getClass().getName() + "\n");
				log.append("@Sql Command: \n" + getSQL());
				getLogger().debug(log.toString());
			}
			_upCnt = stmt.executeUpdate(getSQL());
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("@Sql End (STATEMENT)");
			}
		} catch (SQLException e) {
			getLogger().error("executeUpdate Error!");
			throw new SQLException(e.getMessage() + "\nSQL : " + getSQL());
		}
		return _upCnt;
	}

	public int executeUpdate(String sql) throws SQLException {
		setSQL(sql);
		return executeUpdate();
	}

	public RecordSet getRecordSet() {
		return this._rs;
	}

	public String getSQL() {
		return this._sql;
	}

	public int getUpdateCount() {
		return this._upCnt;
	}

	public void setSQL(String newSql) {
		this._sql = newSql;
	}

	public String toString() {
		return "SQL : " + getSQL();
	}
}