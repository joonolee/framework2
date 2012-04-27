/* 
 * @(#)SuperDaoSupport.java
 * 테이블을 CRUD 하는 DAO를 작성할때 상속받는 클래스
 */
package framework.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractDao {
	private static Log _logger = LogFactory.getLog(framework.db.AbstractDao.class);
	private ConnectionManager _connMgr = null;

	public AbstractDao(ConnectionManager connMgr) {
		super();
		this._connMgr = connMgr;
	}

	protected ConnectionManager getConnectionManager() {
		return _connMgr;
	}

	protected RecordSet executeQuery(String query) throws Exception {
		return executeQuery(query, null);
	}

	protected RecordSet executeQuery(String query, Object[] where) throws Exception {
		RecordSet rs = null;
		try {
			if (getConnectionManager() == null) {
				getLogger().error("executeQuery : Can't open DB Connection!");
				return null;
			}
			SQLPreparedStatement pstmt = getConnectionManager().createPrepareStatement(query);
			if (where != null) {
				pstmt.set(where);
			}
			rs = pstmt.executeQuery();
			pstmt.close();
		} catch (Exception e) {
			getLogger().error("executeQuery Error!");
			throw e;
		}
		return rs;
	}

	protected int execute(String query, Object[] values) throws Exception {
		int result = 0;
		try {
			if (getConnectionManager() == null) {
				getLogger().error("executeQuery : Can't open DB Connection!");
				return 0;
			}
			SQLPreparedStatement pstmt = getConnectionManager().createPrepareStatement(query);
			pstmt.set(values);
			result = pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			getLogger().error("execute Error!");
			throw e;
		}
		return result;
	}

	public int[] save(ValueObjectArray voArray) throws Exception {
		int result[] = null;
		try {
			if (getConnectionManager() == null) {
				getLogger().error("executeQuery : Can't open DB Connection!");
				return null;
			}
			if (voArray.size() == 0) {
				return new int[] { 0 };
			}
			result = new int[voArray.size()];
			int cnt = 0;
			cnt += executeArray(voArray, ValueObjectArray.INSERT, result, cnt);
			cnt += executeArray(voArray, ValueObjectArray.UPDATE, result, cnt);
			cnt += executeArray(voArray, ValueObjectArray.DELETE, result, cnt);
			cnt += executeArray(voArray, ValueObjectArray.UPDATE_ONLY, result, cnt);
			cnt += executeArray(voArray, ValueObjectArray.USER_DELETE, result, cnt);
			cnt += executeArray(voArray, ValueObjectArray.USER_UPDATE, result, cnt);
		} catch (Exception e) {
			getLogger().error("save Error!");
			throw e;
		}
		return result;
	}

	private int executeArray(ValueObjectArray vo, String type, int[] result, int cnt) throws Exception {
		ValueObject[] values = null;
		try {
			values = vo.get(type);
			if (values == null || values.length < 1)
				return 0;
			SQLPreparedStatement pstmt = getConnectionManager().createPrepareStatement(getSaveSql(type, vo.getUserKeys(), vo.getUserFields()));
			for (int i = 0; i < values.length; i++) {
				pstmt.set(getSaveValue(values[i], type, vo.getUserKeys(), vo.getUserFields()));
				result[cnt++] = pstmt.executeUpdate();
			}
			pstmt.close();
		} catch (Exception e) {
			getLogger().error("executeArray Error!");
			throw e;
		}
		return values.length;
	}

	private String getSaveSql(String type, String[] keys, String[] fields) {
		if (type.equals(ValueObjectArray.INSERT))
			return getInsertSql();
		else if (type.equals(ValueObjectArray.UPDATE))
			return getUpdateSql();
		else if (type.equals(ValueObjectArray.DELETE))
			return getDeleteSql();
		else if (type.equals(ValueObjectArray.UPDATE_ONLY))
			return getUpdateOnlySql(fields);
		else if (type.equals(ValueObjectArray.USER_UPDATE))
			return getUserUpdateOnlySql(fields, keys);
		else if (type.equals(ValueObjectArray.USER_DELETE))
			return getUserDeleteSql(keys);
		return null;
	}

	private Object[] getSaveValue(ValueObject vo, String type, String[] keys, String[] fields) {
		if (type.equals(ValueObjectArray.INSERT))
			return vo.getInsertValue();
		else if (type.equals(ValueObjectArray.UPDATE))
			return vo.getUpdateValue();
		else if (type.equals(ValueObjectArray.DELETE))
			return vo.getPrimaryKeysValue();
		else if (type.equals(ValueObjectArray.UPDATE_ONLY))
			return vo.getUpdateOnlyValue(fields);
		else if (type.equals(ValueObjectArray.USER_UPDATE))
			return vo.getUserUpdateOnlyValue(fields, keys);
		else if (type.equals(ValueObjectArray.USER_DELETE))
			return vo.getUserDeleteValue(keys);
		return null;
	}

	public int insert(ValueObject vo) throws Exception {
		return execute(getInsertSql(), vo.getInsertValue());
	}

	public int update(ValueObject vo) throws Exception {
		return execute(getUpdateSql(), vo.getUpdateValue());
	}

	public int updateOnlyFields(ValueObject vo, String[] updateFieldName) throws Exception {
		return execute(getUpdateOnlySql(updateFieldName), vo.getUpdateOnlyValue(updateFieldName));
	}

	public int userUpdate(ValueObject vo, String[] fields, String[] keyNames) throws Exception {
		return execute(getUserUpdateOnlySql(fields, keyNames), vo.getUserUpdateOnlyValue(fields, keyNames));
	}

	public int delete(ValueObject vo) throws Exception {
		return execute(getDeleteSql(), vo.getPrimaryKeysValue());
	}

	public int userDelete(ValueObject vo, String[] keyNames) throws Exception {
		return execute(getUserDeleteSql(keyNames), vo.getUserDeleteValue(keyNames));
	}

	protected Log getLogger() {
		return AbstractDao._logger;
	}

	public abstract String getInsertSql();

	public abstract String getUpdateSql();

	public abstract String getUpdateOnlySql(String[] updateFieldNames);

	public abstract String getUserUpdateOnlySql(String[] updateFieldNames, String[] updateKeyNames);

	public abstract String getDeleteSql();

	public abstract String getUserDeleteSql(String[] deleteKeyNames);

	public abstract RecordSet select(ValueObject vo) throws Exception;
}