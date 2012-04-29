/** 
 * @(#)EhCache.java
 */
package framework.cache;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 기본 캐시 구현체 (http://ehcache.org/)
 */
public class EhCache extends AbstractCache {

	/**
	 * 싱글톤 객체
	 */
	private static EhCache _uniqueInstance;
	
	/**
	 * 캐시 매니저
	 */
	private CacheManager _cacheManager;
	
	/**
	 * 캐시 오브젝트
	 */
	private net.sf.ehcache.Cache _cache;

	/**
	 * 기본 캐시 이름
	 */
	private static final String _CACHE_NAME = "framework2";

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private EhCache() {
		this._cacheManager = CacheManager.create();
		this._cacheManager.addCache(_CACHE_NAME);
		this._cache = _cacheManager.getCache(_CACHE_NAME);
	}

	/** 
	 * 객체의 인스턴스를 리턴해준다.
	 * 
	 * @return EhCache 객체의 인스턴스
	 */
	public static EhCache getInstance() {
		if (_uniqueInstance == null) {
			_uniqueInstance = new EhCache();
		}
		return _uniqueInstance;
	}

	@Override
	public void add(String key, Object value, int seconds) {
		if (_cache.get(key) != null) {
			return;
		}
		Element element = new Element(key, value);
		element.setTimeToLive(seconds);
		_cache.put(element);
	}

	@Override
	public void set(String key, Object value, int seconds) {
		Element element = new Element(key, value);
		element.setTimeToLive(seconds);
		_cache.put(element);
	}

	@Override
	public void replace(String key, Object value, int seconds) {
		if (_cache.get(key) == null) {
			return;
		}
		Element element = new Element(key, value);
		element.setTimeToLive(seconds);
		_cache.put(element);
	}

	@Override
	public Object get(String key) {
		Element element = _cache.get(key);
		return (element == null) ? null : element.getValue();
	}

	@Override
	public Map<String, Object> get(String[] keys) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for (String key : keys) {
			resultMap.put(key, get(key));
		}
		return resultMap;
	}

	@Override
	public synchronized long incr(String key, int by) {
		Element element = _cache.get(key);
		if (element == null) {
			return -1;
		}
		long newValue = ((Number) element.getValue()).longValue() + by;
		Element newElement = new Element(key, newValue);
		newElement.setTimeToLive(element.getTimeToLive());
		_cache.put(newElement);
		return newValue;
	}

	@Override
	public synchronized long decr(String key, int by) {
		Element element = _cache.get(key);
		if (element == null) {
			return -1;
		}
		long newValue = ((Number) element.getValue()).longValue() - by;
		Element newElement = new Element(key, newValue);
		newElement.setTimeToLive(element.getTimeToLive());
		_cache.put(newElement);
		return newValue;
	}

	@Override
	public void delete(String key) {
		_cache.remove(key);
	}

	@Override
	public void clear() {
		_cache.removeAll();
	}

	@Override
	public void stop() {
		_cacheManager.shutdown();
	}
}
