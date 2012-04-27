/* 
 * @(#)ValueObject.java
 * 테이블의 값을 담는 VO를 작성할때 상속받는 클래스
 */
package framework.db;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ValueObject {
	private static Log _logger = LogFactory.getLog(framework.db.ValueObject.class);

	protected Log getLogger() {
		return ValueObject._logger;
	}

	public abstract Object[] getFieldsValue();

	public abstract String[] getFieldsName();

	public abstract String[] getPrimaryKeysName();

	public abstract Object[] getPrimaryKeysValue();

	public abstract void setByName(String key, Object value);

	public abstract Object getByName(String key);

	public abstract Object[] getUpdateValue();

	public abstract Object[] getInsertValue();

	public abstract Object[] getUpdateOnlyValue(String[] fields);

	public abstract Object[] getUserUpdateOnlyValue(String[] updateFieldNames, String[] updateKeyNames);

	public abstract Object[] getUserDeleteValue(String[] keyNames);

	public abstract Map<String, String> getTypeMap();

	public abstract Map<String, Integer> getColumnMap();
}