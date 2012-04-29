/** 
 * @(#)CacheException.java
 */
package framework.cache;

/**
 * 캐시에서 발생되는 예외객체
 */
public class CacheException extends RuntimeException {

	private static final long serialVersionUID = -3769987321701639169L;

	public CacheException() {
		super();
	}

	public CacheException(String s) {
		super(s);
	}

	public CacheException(Exception e) {
		super(e);
	}
}