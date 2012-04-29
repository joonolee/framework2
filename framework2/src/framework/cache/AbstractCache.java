/** 
 * @(#)AbstractCache.java
 */
package framework.cache;

import java.util.Map;

/**
 * 캐시구현체가 상속받아야 하는 추상클래스
 */
public abstract class AbstractCache {

	/**
	 * 키와 값을 캐시에 추가한다.
	 * 
	 * @param key 키
	 * @param value 값
	 * @param seconds 캐시시간(초단위)
	 */
	public abstract void add(String key, Object value, int seconds);

	/**
	 * 키와 값을 캐시에 설정한다.
	 * 
	 * @param key 키
	 * @param value 값
	 * @param seconds 캐시시간(초단위)
	 */
	public abstract void set(String key, Object value, int seconds);

	/**
	 * 키와 값을 캐시에서 교체한다.
	 * 
	 * @param key 키
	 * @param value 값
	 * @param seconds 캐시시간(초단위)
	 */
	public abstract void replace(String key, Object value, int seconds);

	/**
	 * 캐시에서 키로 값을 얻어온다.
	 * 
	 * @param key 키
	 * @return 값
	 */
	public abstract Object get(String key);

	/**
	 * 캐시에서 키의 배열로 값들을 얻어온다.
	 * 
	 * @param keys 키
	 * @return 값
	 */
	public abstract Map<String, Object> get(String[] keys);

	/**
	 * 키의 값을 by 만큼 증가시킨다.
	 * 
	 * @param key 키
	 * @param by 증가시킬 값
	 * @return 증가된 후 값
	 */
	public abstract long incr(String key, int by);

	/**
	 * 키의 값을 by 만큼 감소시킨다.
	 * 
	 * @param key 키
	 * @param by 감소시킬 값
	 * @return 감소된 후 값
	 */
	public abstract long decr(String key, int by);

	/**
	 * 키와 값을 캐시에서 삭제한다.
	 * 
	 * @param key 키
	 */
	public abstract void delete(String key);

	/**
	 * 캐시를 모두 비운다.
	 */
	public abstract void clear();

	/**
	 * 캐시를 멈춘다.
	 */
	public abstract void stop();
}
