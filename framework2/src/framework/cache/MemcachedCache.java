package framework.cache;

import java.util.Map;

public class MemcachedCache extends AbstractCache {

	@Override
	public void add(String key, Object value, int seconds) {

	}

	@Override
	public void set(String key, Object value, int seconds) {

	}

	@Override
	public void replace(String key, Object value, int seconds) {

	}

	@Override
	public Object get(String key) {

		return null;
	}

	@Override
	public Map<String, Object> get(String[] keys) {

		return null;
	}

	@Override
	public long incr(String key, int by) {
		return 0;
	}

	@Override
	public long decr(String key, int by) {
		return 0;
	}

	@Override
	public void delete(String key) {

	}

	@Override
	public void clear() {

	}

	@Override
	public void stop() {

	}
}
