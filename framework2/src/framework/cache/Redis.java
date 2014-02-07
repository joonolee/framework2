/** 
 * @(#)Redis.java
 */
package framework.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import framework.config.Configuration;

/**
 * Redis 캐시 구현체 (http://redis.io/)
 */
public class Redis extends AbstractCache {
	/**
	 * 싱글톤 객체
	 */
	private static Redis _uniqueInstance;

	/** 
	 * 타임아웃 값 (ms)
	 */
	private static final int _TIMEOUT = 500;

	/**
	 * 캐시 클라이언트 Pool
	 */
	private ShardedJedisPool _pool;

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private Redis() {
		List<JedisShardInfo> shards;
		if (_getConfig().containsKey("redis.host")) {
			shards = _getAddresses(_getConfig().getString("redis.host"));
		} else if (_getConfig().containsKey("redis.1.host")) {
			int count = 1;
			StringBuilder buffer = new StringBuilder();
			while (_getConfig().containsKey("redis." + count + ".host")) {
				buffer.append(_getConfig().getString("redis." + count + ".host") + " ");
				count++;
			}
			shards = _getAddresses(buffer.toString());
		} else {
			throw new RuntimeException("redis의 호스트설정이 누락되었습니다.");
		}
		_pool = new ShardedJedisPool(new JedisPoolConfig(), shards);
	}

	/** 
	 * 객체의 인스턴스를 리턴해준다.
	 * 
	 * @return Redis 객체의 인스턴스
	 */
	public synchronized static Redis getInstance() {
		if (_uniqueInstance == null) {
			_uniqueInstance = new Redis();
		}
		return _uniqueInstance;
	}

	@Override
	public void set(String key, Object value, int seconds) {
		set(_serialize(key), _serialize(value), seconds);
	}

	public void set(byte[] key, byte[] value, int seconds) {
		ShardedJedis jedis = null;
		try {
			jedis = _pool.getResource();
			jedis.setex(key, seconds, value);
		} catch (JedisConnectionException e) {
			if (jedis != null) {
				_pool.returnBrokenResource(jedis);
			}
		} finally {
			if (jedis != null) {
				_pool.returnResource(jedis);
			}
		}
	}

	@Override
	public Object get(String key) {
		return get(_serialize(key));
	}

	public Object get(byte[] key) {
		ShardedJedis jedis = null;
		Object value = null;
		try {
			jedis = _pool.getResource();
			value = _deserialize(jedis.get(key));
		} catch (JedisConnectionException e) {
			if (jedis != null) {
				_pool.returnBrokenResource(jedis);
			}
		} finally {
			if (jedis != null) {
				_pool.returnResource(jedis);
			}
		}
		return value;
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
	public long incr(String key, int by) {
		return incr(_serialize(key), by);
	}

	public long incr(byte[] key, int by) {
		ShardedJedis jedis = null;
		Long value = null;
		try {
			jedis = _pool.getResource();
			value = jedis.incrBy(key, by);
		} catch (JedisConnectionException e) {
			if (jedis != null) {
				_pool.returnBrokenResource(jedis);
			}
		} finally {
			if (jedis != null) {
				_pool.returnResource(jedis);
			}
		}
		if (value == null) {
			value = Long.valueOf(-1);
		}
		return value;
	}

	@Override
	public long decr(String key, int by) {
		return decr(_serialize(key), by);
	}

	public long decr(byte[] key, int by) {
		ShardedJedis jedis = null;
		Long value = null;
		try {
			jedis = _pool.getResource();
			value = jedis.decrBy(key, by);
		} catch (JedisConnectionException e) {
			if (jedis != null) {
				_pool.returnBrokenResource(jedis);
			}
		} finally {
			if (jedis != null) {
				_pool.returnResource(jedis);
			}
		}
		if (value == null) {
			value = Long.valueOf(-1);
		}
		return value;
	}

	@Override
	public void delete(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = _pool.getResource();
			jedis.del(key);
		} catch (JedisConnectionException e) {
			if (jedis != null) {
				_pool.returnBrokenResource(jedis);
			}
		} finally {
			if (jedis != null) {
				_pool.returnResource(jedis);
			}
		}
	}

	@Override
	public void clear() {
		ShardedJedis jedis = null;
		try {
			jedis = _pool.getResource();
			for (Jedis j : jedis.getAllShards()) {
				j.flushAll();
			}
		} catch (JedisConnectionException e) {
			if (jedis != null) {
				_pool.returnBrokenResource(jedis);
			}
		} finally {
			if (jedis != null) {
				_pool.returnResource(jedis);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////Private 메소드

	/**
	* 설정파일(config.properties)에서 값을 읽어오는 클래스를 리턴한다.
	* @return 설정객체
	*/
	private Configuration _getConfig() {
		return Configuration.getInstance();
	}

	/**
	 * 문자열에서 redis 호스트 주소를 파싱하여 리턴한다.
	 * @param str 스페이스로 구분된 주소문자열
	 * @return 샤드주소객체
	 */
	private List<JedisShardInfo> _getAddresses(String str) {
		if (str == null || "".equals(str.trim())) {
			throw new IllegalArgumentException("redis의 호스트설정이 누락되었습니다.");
		}
		ArrayList<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		for (String addr : str.split("(?:\\s|,)+")) {
			if ("".equals(addr)) {
				continue;
			}
			int sep = addr.lastIndexOf(':');
			if (sep < 1) {
				throw new IllegalArgumentException("서버설정이 잘못되었습니다. 형식=>호스트:포트");
			}
			shards.add(new JedisShardInfo(addr.substring(0, sep), Integer.valueOf(addr.substring(sep + 1)), _TIMEOUT));
		}
		assert !shards.isEmpty() : "redis의 호스트설정이 누락되었습니다.";
		return shards;
	}

	/**
	 * 객체를 바이트배열로 직렬화 한다.
	 * @param obj 직렬화할 객체
	 * @return 바이트배열
	 */
	public byte[] _serialize(Object obj) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 바이트배열을 객체로 역직렬화 한다.
	 * @param bytes 바이트배열
	 * @return 역직렬화된 객체
	 */
	public Object _deserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
		}
		return null;
	}
}
