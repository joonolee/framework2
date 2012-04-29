package framework.cache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import framework.config.Configuration;
import framework.config.ConfigurationException;

/**
 * 
 */
public class Memcached extends AbstractCache {

	private static Memcached _uniqueInstance;
	private MemcachedClient _client;

	/**
	 * 
	 * @throws IOException
	 */
	private Memcached() throws IOException {
		System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
		List<InetSocketAddress> addrList;
		if (getConfig().containsKey("memcached.host")) {
			addrList = AddrUtil.getAddresses(getConfig().getString("memcached.host"));
		} else if (getConfig().containsKey("memcached.1.host")) {
			int count = 1;
			StringBuilder buffer = new StringBuilder();
			while (getConfig().containsKey("memcached." + count + ".host")) {
				buffer.append(getConfig().getString("memcached." + count + ".host") + " ");
				count++;
			}
			addrList = AddrUtil.getAddresses(buffer.toString());
		} else {
			throw new ConfigurationException("memcached의 호스트설정이 누락되었습니다.");
		}
		_client = new MemcachedClient(addrList);
	}

	/** 
	 * 객체의 인스턴스를 리턴해준다.
	 * 
	 * @return Memcached 객체의 인스턴스
	 * @throws IOException 
	 */
	public static Memcached getInstance() throws IOException {
		if (_uniqueInstance == null) {
			_uniqueInstance = new Memcached();
		}
		return _uniqueInstance;
	}

	@Override
	public void add(String key, Object value, int seconds) {
		_client.add(key, seconds, value);
	}

	@Override
	public void set(String key, Object value, int seconds) {
		_client.set(key, seconds, value);
	}

	@Override
	public void replace(String key, Object value, int seconds) {
		_client.replace(key, seconds, value);
	}

	@Override
	public Object get(String key) {
		return _client.get(key);
	}

	@Override
	public Map<String, Object> get(String[] keys) {
		Map<String, Object> valueMap = _client.getBulk();
		return (valueMap == null) ? Collections.<String, Object> emptyMap() : valueMap;
	}

	@Override
	public long incr(String key, int by) {
		return _client.incr(key, by, 0);
	}

	@Override
	public long decr(String key, int by) {
		return _client.decr(key, by, 0);
	}

	@Override
	public void delete(String key) {
		_client.delete(key);
	}

	@Override
	public void clear() {
		_client.flush();
	}

	@Override
	public void stop() {
		_client.shutdown();
	}

	////////////////////////////////////////////////////////////////////////////////////////Private 메소드
	private Configuration getConfig() {
		return Configuration.getInstance();
	}
}
