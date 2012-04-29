package framework.cache;

import java.util.Map;

public abstract class AbstractCache {

	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public abstract void add(String key, Object value, int seconds);

	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public abstract void set(String key, Object value, int seconds);

	/**
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public abstract void replace(String key, Object value, int seconds);

	/**
	 * 
	 * @param key
	 * @return
	 */
	public abstract Object get(String key);

	/**
	 * 
	 * @param keys
	 * @return
	 */
	public abstract Map<String, Object> get(String[] keys);

	/**
	 * 
	 * @param key
	 * @param by
	 * @return
	 */
	public abstract long incr(String key, int by);

	/**
	 * 
	 * @param key
	 * @param by
	 * @return
	 */
	public abstract long decr(String key, int by);

	/**
	 * 
	 * @param key
	 */
	public abstract void delete(String key);

	/**
	 * 
	 */
	public abstract void clear();

	/**
	 * 
	 */
	public abstract void stop();
}
