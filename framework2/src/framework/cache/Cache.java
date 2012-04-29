package framework.cache;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Map;

public class Cache {

	/**
	 * 
	 */
	public static AbstractCache cache;

	/**
	 * 
	 */
	private final static int DEFAULT_DURATION = 60 * 60 * 24 * 30;

	/**
	 * 
	 */
	private Cache() {
	}

	/**
	 * 
	 */
	public static void init() {
		try {
			cache = Memcached.getInstance();
		} catch (Exception e) {
			cache = EhCache.getInstance();
		}
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void add(String key, Object value) {
		isSerializable(value);
		cache.add(key, value, DEFAULT_DURATION);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public static void add(String key, Object value, int seconds) {
		isSerializable(value);
		cache.add(key, value, seconds);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void set(String key, Object value) {
		isSerializable(value);
		cache.set(key, value, DEFAULT_DURATION);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public static void set(String key, Object value, int seconds) {
		isSerializable(value);
		cache.set(key, value, seconds);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public static void replace(String key, Object value) {
		isSerializable(value);
		cache.replace(key, value, DEFAULT_DURATION);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public static void replace(String key, Object value, int seconds) {
		isSerializable(value);
		cache.replace(key, value, seconds);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static long incr(String key) {
		return cache.incr(key, 1);
	}

	/**
	 * 
	 * @param key
	 * @param by
	 * @return
	 */
	public static long incr(String key, int by) {
		return cache.incr(key, by);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static long decr(String key) {
		return cache.decr(key, 1);
	}

	/**
	 * 
	 * @param key
	 * @param by
	 * @return
	 */
	public static long decr(String key, int by) {
		return cache.decr(key, by);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		return cache.get(key);
	}

	/**
	 * 
	 * @param keys
	 * @return
	 */
	public static Map<String, Object> get(String... keys) {
		return cache.get(keys);
	}

	/**
	 * 
	 * @param key
	 */
	public static void delete(String key) {
		cache.delete(key);
	}

	/**
	 * 
	 */
	public static void clear() {
		cache.clear();
	}

	/**
	 * 
	 */
	public static void stop() {
		cache.stop();
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private ¸Þ¼Òµå

	/**
	 * 
	 * @param value
	 */
	private static void isSerializable(Object value) {
		if (value != null && !(value instanceof Serializable)) {
			throw new CacheException(new NotSerializableException(value.getClass().getName()));
		}
	}
}
