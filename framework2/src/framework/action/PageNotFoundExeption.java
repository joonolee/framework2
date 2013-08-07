/** 
 * @(#)PageNotFoundExeption.java
 */
package framework.action;

/**
 * 호출한 url에 해당하는 컨트롤러나 액션메소드가 없을 경우 프레임워크 내부에서 발생하는 예외
 */
public class PageNotFoundExeption extends RuntimeException {
	private static final long serialVersionUID = 2427049883577660202L;

	public PageNotFoundExeption() {
		super();
	}

	public PageNotFoundExeption(String message) {
		super(message);
	}

	public PageNotFoundExeption(Exception e) {
		super(e);
	}
}
