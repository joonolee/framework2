package framework.cache;

import java.util.Map;

public abstract class AbstractCache {

	public abstract void add(String key, Object value, int seconds);

	public abstract void set(String key, Object value, int seconds);

	public abstract void replace(String key, Object value, int seconds);

	public abstract Object get(String key);

	public abstract Map<String, Object> get(String[] keys);

	public abstract void delete(String key);

	public abstract void clear();

	public abstract void stop();
}
