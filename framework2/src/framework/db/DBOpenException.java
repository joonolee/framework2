/* 
 * @(#)DBOpenException.java
 * 데이타베이스를 접속할 수 없을 때 발생시키는 예외
 */
package framework.db;

public class DBOpenException extends RuntimeException {
	private static final long serialVersionUID = -6920519198514818194L;

	public DBOpenException() {
		super();
	}

	public DBOpenException(String s) {
		super(s);
	}
}