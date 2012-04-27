/* 
 * @(#)ColumnNotFoundException.java
 * 데이타베이스에스 해당되는 컬럼이 없을때 발생시키는 예외
 */
package framework.db;

public class ColumnNotFoundException extends Exception {
	private static final long serialVersionUID = 8048251274975376569L;

	public ColumnNotFoundException() {
		super();
	}

	public ColumnNotFoundException(String s) {
		super(s);
	}
}