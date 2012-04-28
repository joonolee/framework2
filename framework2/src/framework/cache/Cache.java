package framework.cache;

import java.util.Map;

public class Cache {

	public static AbstractCache cache;

	private final static int DEFAULT_DURATION = 60 * 60 * 24 * 30;

	private Cache() {
	}

	public static void init() {

	}

	public static void add(String key, Object value) {
		cache.add(key, value, DEFAULT_DURATION);
	}

	public static void add(String key, Object value, int seconds) {
		cache.add(key, value, seconds);
	}

	public static void set(String key, Object value) {
		cache.set(key, value, DEFAULT_DURATION);
	}

	public static void set(String key, Object value, int seconds) {
		cache.set(key, value, seconds);
	}

	public static void replace(String key, Object value) {
		cache.replace(key, value, DEFAULT_DURATION);
	}

	public static void replace(String key, Object value, int seconds) {
		cache.replace(key, value, seconds);
	}

	public static Object get(String key) {
		return cache.get(key);
	}

	public static Map<String, Object> get(String... keys) {
		return cache.get(keys);
	}

	public static void delete(String key) {
		cache.delete(key);
	}

	public static void clear() {
		cache.clear();
	}

	public static void stop() {
		cache.stop();
	}
}
