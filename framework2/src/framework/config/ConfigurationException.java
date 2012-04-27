/**
 * @(#)ConfigurationException.java
 */
package framework.config;

/** 
 * 설정값을 읽어올때 사용하는 예외클래스
 */
public class ConfigurationException extends Exception {
	private static final long serialVersionUID = 7481013867482044197L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String s) {
		super(s);
	}
}