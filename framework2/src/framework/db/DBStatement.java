/** 
 * @(#)DBStatement.java
 */
package framework.db;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * 모든 SQL 문장을 처리하는 클래스가 상속받아야 할 추상클래스이다.
 */
public abstract class DBStatement {
	private static Log _logger = LogFactory.getLog(framework.db.DBStatement.class);

	/** 
	 * Statement의 close 를 구현하기 위한 추상 메소드
	 */
	public abstract void close() throws SQLException;

	/** 
	 * DBStatement 로거객체를 리턴한다.
	 * 모든 로그는 해당 로거를 이용해서 출력하여야 한다.
	 * <br>
	 * ex1) 에러 정보를 출력할 경우 => getLogger().error("...에러메시지내용")
	 * <br>
	 * ex2) 디버그 정보를 출력할 경우 => getLogger().debug("...디버그메시지내용")
	 *
	 * @return DBStatement의 로거객체
	 */
	protected Log getLogger() {
		return DBStatement._logger;
	}
}