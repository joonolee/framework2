/** 
 * @(#)Cache.java
 */
package framework.cache;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Map;

public class Cache {

	/**
	 * 캐시구현체
	 */
	public static AbstractCache cache;

	/**
	 * 기본 캐시 시간 (30일)
	 */
	private final static int DEFAULT_DURATION = 60 * 60 * 24 * 30;

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private Cache() {
	}

	/**
	 * 캐시 초기화, 설정파일을 읽어 캐시 구현체를 셋팅한다.
	 */
	public static void init() {
		try {
			cache = Memcached.getInstance();
		} catch (Exception e) {
			cache = EhCache.getInstance();
		}
	}

	/**
	 * 키와 값을 캐시에 추가한다.
	 * 
	 * @param key 키
	 * @param value 값
	 */
	public static void add(String key, Object value) {
		isSerializable(value);
		cache.add(key, value, DEFAULT_DURATION);
	}

	/**
	 * 키와 값을 캐시에 추가한다.
	 * 
	 * @param key 키
	 * @param value 값
	 * @param seconds 캐시시간(초단위)
	 */
	public static void add(String key, Object value, int seconds) {
		isSerializable(value);
		cache.add(key, value, seconds);
	}

	/**
	 * 키와 값을 캐시에 설정한다.
	 * 
	 * @param key 키
	 * @param value 값
	 */
	public static void set(String key, Object value) {
		isSerializable(value);
		cache.set(key, value, DEFAULT_DURATION);
	}

	/**
	 * 키와 값을 캐시에 설정한다.
	 * 
	 * @param key 키
	 * @param value 값
	 * @param seconds 캐시시간(초단위)
	 */
	public static void set(String key, Object value, int seconds) {
		isSerializable(value);
		cache.set(key, value, seconds);
	}

	/**
	 * 키와 값을 캐시에서 교체한다.
	 * 
	 * @param key 키
	 * @param value 값
	 */
	public static void replace(String key, Object value) {
		isSerializable(value);
		cache.replace(key, value, DEFAULT_DURATION);
	}

	/**
	 * 키와 값을 캐시에서 교체한다.
	 * 
	 * @param key 키
	 * @param value 값
	 * @param seconds 캐시시간(초단위)
	 */
	public static void replace(String key, Object value, int seconds) {
		isSerializable(value);
		cache.replace(key, value, seconds);
	}

	/**
	 * 키의 값을 1만큼 증가시킨다.
	 * 
	 * @param key 키
	 * @return 증가된 후 값
	 */
	public static long incr(String key) {
		return cache.incr(key, 1);
	}

	/**
	 * 키의 값을 by 만큼 증가시킨다.
	 * 
	 * @param key 키
	 * @param by 증가시킬 값
	 * @return 증가된 후 값
	 */
	public static long incr(String key, int by) {
		return cache.incr(key, by);
	}

	/**
	 * 키의 값을 1만큼 감소시킨다.
	 * 
	 * @param key 키
	 * @return 감소된 후 값
	 */
	public static long decr(String key) {
		return cache.decr(key, 1);
	}

	/**키의 값을 by 만큼 감소시킨다.
	 * 
	 * @param key 키
	 * @param by 감소시킬 값
	 * @return 감소된 후 값
	 */
	public static long decr(String key, int by) {
		return cache.decr(key, by);
	}

	/**
	 * 캐시에서 키로 값을 얻어온다.
	 * 
	 * @param key 키
	 * @return 값
	 */
	public static Object get(String key) {
		return cache.get(key);
	}

	/**
	 * 캐시에서 키의 배열로 값들을 얻어온다.
	 * 
	 * @param keys 키
	 * @return 값
	 */
	public static Map<String, Object> get(String... keys) {
		return cache.get(keys);
	}

	/**
	 * 키와 값을 캐시에서 삭제한다.
	 * 
	 * @param key 키
	 */
	public static void delete(String key) {
		cache.delete(key);
	}

	/**
	 * 캐시를 모두 비운다.
	 */
	public static void clear() {
		cache.clear();
	}

	/**
	 * 캐시를 멈춘다.
	 */
	public static void stop() {
		cache.stop();
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드

	/**
	 * 직렬화 가능 객체인지 판별한다.
	 * @param value
	 */
	private static void isSerializable(Object value) {
		if (value != null && !(value instanceof Serializable)) {
			throw new CacheException(new NotSerializableException(value.getClass().getName()));
		}
	}
}
