/**
 * @(#)Configuration.java
 * 설정파일에서 값을 읽어오는 클래스
 */
package framework.config;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 설정파일(config.properties)에서 값을 읽어오는 클래스이다.
 * 싱글턴 패턴으로 설정파일에 접근하는 객체의 인스턴스가 오직 한개만 생성이 된다.
 */
public class Configuration {
	private static Configuration _uniqueInstance = new Configuration();
	private static final String _baseName = "config";
	private ResourceBundle _bundle = null;

	private Configuration() {
		try {
			_bundle = ResourceBundle.getBundle(_baseName);
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 객체의 인스턴스를 리턴해준다.
	 *
	 * @return Configuration 객체의 인스턴스
	 */
	public static Configuration getInstance() {
		return _uniqueInstance;
	}

	/**
	 * 키(key)문자열과 매핑되어 있는 String 리턴한다.
	 *
	 * @param key 값을 찾기 위한 키 문자열
	 *
	 * @return key에 매핑되어 있는 String 객체
	 */
	public String get(String key) {
		return getString(key);
	}

	/**
	 * 키(key)문자열과 매핑되어 있는 boolean형 변수를 리턴한다.
	 *
	 * @param key 값을 찾기 위한 키 문자열
	 *
	 * @return key에 매핑되어 있는 boolean형 변수
	 */
	public boolean getBoolean(String key) throws IllegalArgumentException {
		boolean value = false;
		try {
			value = (Boolean.valueOf(_bundle.getString(key).trim())).booleanValue();
		} catch (Exception e) {
			throw new IllegalArgumentException("Illegal Boolean Key : " + key);
		}
		return value;
	}

	/**
	 * 키(key)문자열과 매핑되어 있는 int형 변수를 리턴한다.
	 *
	 * @param key 값을 찾기 위한 키 문자열
	 *
	 * @return key에 매핑되어 있는 int형 변수
	 */
	public int getInt(String key) throws IllegalArgumentException {
		int value = -1;
		try {
			value = Integer.parseInt(_bundle.getString(key).trim());
		} catch (Exception e) {
			throw new IllegalArgumentException("Illegal Integer Key : " + key);
		}
		return value;
	}

	/**
	 * 키(key)문자열과 매핑되어 있는 String 리턴한다.
	 *
	 * @param key 값을 찾기 위한 키 문자열
	 *
	 * @return key에 매핑되어 있는 String 객체
	 */
	public String getString(String key) throws IllegalArgumentException {
		String value = null;
		try {
			value = _bundle.getString(key).trim();
		} catch (Exception e) {
			throw new IllegalArgumentException("Illegal String Key : " + key);
		}
		return value;
	}

	/**
	 * 키(key)가 포함되어있는지 여부를 리턴한다.
	 *
	 * @param key 값을 찾기 위한 키 문자열
	 *
	 * @return key의 포함여부
	 */
	public boolean containsKey(String key) {
		return _bundle.containsKey(key);
	}
}