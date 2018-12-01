/**
 * @(#)Cache.java
 */
package framework.cache;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Cache {

	/**
	 * 로거객체 정의
	 */
	private static Log _logger = LogFactory.getLog(framework.cache.Cache.class);

	/**
	 * 캐시구현체
	 */
	public static AbstractCache cache = null;

	/**
	 * 캐시구현체 이름
	 */
	public static String cacheName = null;

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
	public synchronized static void init() {
		if (cache == null) {
			try {
				cache = Memcached.getInstance();
				cacheName = "Memcached";
			} catch (Exception e) {
				try {
					cache = Redis.getInstance();
					cacheName = "Redis";
				} catch (Exception e2) {
					cache = EhCache.getInstance();
					cacheName = "EhCache";
				}
			}
			_getLogger().info(String.format("[ %s ] init : 초기화 성공", cacheName));
		}
	}

	/**
	 * 키와 값을 캐시에 설정한다.
	 * @param key 키
	 * @param value 값
	 */
	public static void set(String key, Object value) {
		_isSerializable(value);
		cache.set(key, value, DEFAULT_DURATION);
		_getLogger().debug(String.format("[ %s ] set : { key=%s, value=%s, seconds=%d }", cacheName, key, value, DEFAULT_DURATION));
	}

	/**
	 * 키와 값을 캐시에 설정한다.
	 * @param key 키
	 * @param value 값
	 * @param seconds 캐시시간(초단위)
	 */
	public static void set(String key, Object value, int seconds) {
		_isSerializable(value);
		cache.set(key, value, seconds);
		_getLogger().debug(String.format("[ %s ] set : { key=%s, value=%s, seconds=%d }", cacheName, key, value, seconds));
	}

	/**
	 * 키의 값을 1만큼 증가시킨다.
	 * @param key 키
	 * @return 증가된 후 값
	 */
	public static long incr(String key) {
		long result = cache.incr(key, 1);
		_getLogger().debug(String.format("[ %s ] incr : { key=%s, by=%d }", cacheName, key, 1));
		return result;
	}

	/**
	 * 키의 값을 by 만큼 증가시킨다.
	 * @param key 키
	 * @param by 증가시킬 값
	 * @return 증가된 후 값
	 */
	public static long incr(String key, int by) {
		long result = cache.incr(key, by);
		_getLogger().debug(String.format("[ %s ] incr : { key=%s, by=%d }", cacheName, key, by));
		return result;
	}

	/**
	 * 키의 값을 1만큼 감소시킨다.
	 * @param key 키
	 * @return 감소된 후 값
	 */
	public static long decr(String key) {
		long result = cache.decr(key, 1);
		_getLogger().debug(String.format("[ %s ] decr : { key=%s, by=%d }", cacheName, key, 1));
		return result;
	}

	/**키의 값을 by 만큼 감소시킨다.
	 * @param key 키
	 * @param by 감소시킬 값
	 * @return 감소된 후 값
	 */
	public static long decr(String key, int by) {
		long result = cache.decr(key, by);
		_getLogger().debug(String.format("[ %s ] decr : { key=%s, by=%d }", cacheName, key, by));
		return result;
	}

	/**
	 * 캐시에서 키로 값을 얻어온다.
	 * @param key 키
	 * @return 값
	 */
	public static Object get(String key) {
		Object value = cache.get(key);
		_getLogger().debug(String.format("[ %s ] get : { key=%s, value=%s }", cacheName, key, value));
		return value;
	}

	/**
	 * 캐시에서 키의 배열로 값들을 얻어온다.
	 * @param keys 키
	 * @return 값
	 */
	public static Map<String, Object> get(String... keys) {
		Map<String, Object> valueMap = cache.get(keys);
		_getLogger().debug(String.format("[ %s ] get : { key=%s, value=%s }", cacheName, Arrays.asList(keys), valueMap));
		return valueMap;
	}

	/**
	 * 키와 값을 캐시에서 삭제한다.
	 * @param key 키
	 */
	public static void delete(String key) {
		cache.delete(key);
		_getLogger().debug(String.format("[ %s ] delete : { key=%s }", cacheName, key));
	}

	/**
	 * 캐시를 모두 비운다.
	 */
	public static void clear() {
		cache.clear();
		_getLogger().debug(String.format("[ %s ] clear : 캐시 클리어 성공", cacheName));
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드

	/**
	 * 직렬화 가능 객체인지 판별한다.
	 */
	private static void _isSerializable(Object value) {
		if (value != null && !(value instanceof Serializable)) {
			throw new RuntimeException(new NotSerializableException(value.getClass().getName()));
		}
	}

	private static Log _getLogger() {
		return Cache._logger;
	}
}
