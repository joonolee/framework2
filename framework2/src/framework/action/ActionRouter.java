/** 
 * @(#)ActionRouter.java
 */
package framework.action;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * 클라이언트 요청을 라우팅(포워드 또는 재지향) 해주는 클래스이다.
 * action.properties에 등록되어진 키 값에 매핑되어 있는 JSP 페이지를 찾아 파라미터로 입력받은 플래그를 통해 포워딩할지
 * 재지항 할지를 결정하게 된다.
 */
public class ActionRouter {
	private final String _key;
	private final boolean _isForward;
	private static Log _logger = LogFactory.getLog(framework.action.ActionRouter.class);

	/**
	 * 요청을 JSP페이지로 포워드(Forward) 하기위한 객체가 생성된다.
	 * 
	 * @param key action.properties 파일에 등록된 JSP 페이지의 키
	 */
	public ActionRouter(String key) {
		this(key, true);
	}

	/**
	 * 요청을 JSP페이지로 포워드(Forward) 또는 재지향(Redirect) 하기위한 객체가 생성된다.
	 * 
	 * @param key action.properties 파일에 등록된 JSP 페이지의 키
	 * @param isForward true 이면 포워드, false 이면 재지향 하기위한 플래그
	 */
	public ActionRouter(String key, boolean isForward) {
		this._key = key;
		this._isForward = isForward;
	}

	/**
	 * 실제 요청을 라우팅 하게 된다.
	 * 
	 * @param servlet 객체를 호출한 서블릿
	 * @param request 클라이언트에서 요청된 Request객체
	 * @param response 클라이언트로 응답할 Response객체
	 */
	public synchronized void route(GenericServlet servlet, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ResourceBundle bundle = (ResourceBundle) servlet.getServletContext().getAttribute("action-mapping");
		String url = ((String) bundle.getObject(this._key)).trim();
		if (this._isForward) {
			servlet.getServletContext().getRequestDispatcher(response.encodeURL(url)).forward(request, response);
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("☆☆☆ " + request.getRemoteAddr() + " 로 부터 \"" + request.getMethod() + " " + request.getRequestURI() + "\" 요청이 \"" + url + "\" 로 forward 되었습니다");
			}
		} else {
			response.sendRedirect(response.encodeRedirectURL(url));
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("☆☆☆ " + request.getRemoteAddr() + " 로 부터 \"" + request.getMethod() + " " + request.getRequestURI() + "\" 요청이 \"" + url + "\" 로 redirect 되었습니다");
			}
		}
	}

	/** 
	 * ActionRouter의 로거객체를 리턴한다.
	 * 모든 로그는 해당 로거를 이용해서 출력하여야 한다.
	 * <br>
	 * ex1) 에러 정보를 출력할 경우 => getLogger().error("...에러메시지내용")
	 * <br>
	 * ex2) 디버그 정보를 출력할 경우 => getLogger().debug("...디버그메시지내용")
	 *
	 * @return ActionRouter의 로거객체
	 */
	protected Log getLogger() {
		return ActionRouter._logger;
	}
}