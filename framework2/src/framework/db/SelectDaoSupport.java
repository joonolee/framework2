/**
 * @(#)SelectDaoSupport.java
 */
package framework.db;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SELECT 하는 DAO를 작성할때 상속받는 부모 클래스이다.
 */
public class SelectDaoSupport {
	private static Log _logger = LogFactory.getLog(framework.db.SelectDaoSupport.class);
	private ConnectionManager _connMgr = null;

	public ConnectionManager getConnectionManager() {
		return _connMgr;
	}

	public SelectDaoSupport(ConnectionManager connMgr) {
		this._connMgr = connMgr;
	}

	public RecordSet select(String query) throws SQLException {
		return select(query, null, 0, 0);
	}

	public RecordSet select(String query, Object[] where) throws SQLException {
		return select(query, where, 0, 0);
	}

	public RecordSet select(String query, int currPage, int pageSize) throws SQLException {
		return select(query, null, currPage, pageSize);
	}

	public RecordSet select(String query, Object[] where, int currPage, int pageSize) throws SQLException {
		if (where == null) {
			return statmentSelect(query, currPage, pageSize);
		} else {
			return prepardSelect(query, where, currPage, pageSize);
		}
	}

	protected Log getLogger() {
		return SelectDaoSupport._logger;
	}

	private RecordSet prepardSelect(String query, Object[] where, int currPage, int pageSize) throws SQLException {
		SQLPreparedStatement pstmt = _connMgr.createPrepareStatement(query);
		pstmt.set(where);
		RecordSet rs = pstmt.executeQuery(currPage, pageSize);
		pstmt.close();
		return rs;
	}

	private RecordSet statmentSelect(String query, int currPage, int pageSize) throws SQLException {
		SQLStatement stmt = _connMgr.createStatement(query);
		RecordSet rs = stmt.executeQuery(currPage, pageSize);
		stmt.close();
		return rs;
	}
}