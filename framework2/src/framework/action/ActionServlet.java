/** 
 * @(#)ActionServlet.java
 */
package framework.action;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.cache.Cache;
import framework.util.StringUtil;

/** 
 * 컨트롤러 역할을 하는 서블릿으로 모든 클라이언트의 요청을 받아 해당 액션을 실행한다.
 * 확장자가 (.do)로 실행되는 모든 요청을 이 서블릿이 처리하기 위하여 web.xml 파일에서 서블릿을 매핑하여야 하며
 * 서버 부팅시 한개의 객체를 생성해 놓는다.  
 * 요청에서 추출한 액션키로 action.properties에서 Action클래스를 찾아 객체를 생성하여 비지니스 프로세스를 실행한다. 
 */
public class ActionServlet extends HttpServlet {
	private static final long serialVersionUID = -6478697606075642071L;
	private final Log _logger = LogFactory.getLog(framework.action.ActionServlet.class);

	/**
	 * 서블릿 객체를 초기화 한다.
	 * web.xml에 초기화 파라미터로 등록되어 있는 action-mapping 값을 찾아 리소스 번들을 생성하는 역할을 한다.
	 * 
	 * @param config ServletConfig 객체
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ResourceBundle bundle = null;
		String[] _DEFAULT_SERVLET_NAMES = new String[] { "default", "WorkerServlet", "ResourceServlet", "FileServlet", "resin-file", "SimpleFileServlet", "_ah_default" };
		try {
			bundle = ResourceBundle.getBundle(config.getInitParameter("action-mapping"));
			String defaultServletName = StringUtil.nullToBlankString(config.getInitParameter("default-servlet-name"));
			if ("".equals(defaultServletName)) {
				for (String servletName : _DEFAULT_SERVLET_NAMES) {
					if (getServletContext().getNamedDispatcher(servletName) != null) {
						defaultServletName = servletName;
						break;
					}
				}
			}
			RequestDispatcher dispatcher = getServletContext().getNamedDispatcher(defaultServletName);
			if (dispatcher == null) {
				getLogger().info("Default Servlet을 찾을 수 없습니다.");
			} else {
				getServletContext().setAttribute("default-servlet-dispatcher", dispatcher);
				getLogger().info("Default Servlet을 찾았습니다. (" + defaultServletName + ")");
			}
		} catch (MissingResourceException e) {
			throw new ServletException(e);
		}
		getServletContext().setAttribute("action-mapping", bundle);
		// Cache
		Cache.init();
	}

	/**
	 * 클라이언트가 Get 방식으로 요청할 경우 processRequest로 처리를 이관한다.
	 * 
	 * @param request HTTP 클라이언트 요청객체
	 * @param response HTTP 클라이언트 응답객체
	 * 
	 * @exception java.io.IOException ActionServlet에서 IO와 관련된 오류가 발생할 경우 
	 * @exception javax.servlet.ServletException 서블릿과 관련된 오류가 발생할 경우
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		processRequest(request, response);
	}

	/**
	 * 클라이언트가 Post 방식으로 요청할 경우 processRequest로 처리를 이관한다.
	 * 
	 * @param request HTTP 클라이언트 요청객체
	 * @param response HTTP 클라이언트 응답객체
	 * 
	 * @exception java.io.IOException ActionServlet에서 IO와 관련된 오류가 발생할 경우 
	 * @exception javax.servlet.ServletException 서블릿과 관련된 오류가 발생할 경우
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		processRequest(request, response);
	}

	/**
	 * 클라이언트가 Put 방식으로 요청할 경우 processRequest로 처리를 이관한다.
	 * 
	 * @param request HTTP 클라이언트 요청객체
	 * @param response HTTP 클라이언트 응답객체
	 * 
	 * @exception java.io.IOException ActionServlet에서 IO와 관련된 오류가 발생할 경우 
	 * @exception javax.servlet.ServletException 서블릿과 관련된 오류가 발생할 경우
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * 클라이언트가 Delete 방식으로 요청할 경우 processRequest로 처리를 이관한다.
	 * 
	 * @param request HTTP 클라이언트 요청객체
	 * @param response HTTP 클라이언트 응답객체
	 * 
	 * @exception java.io.IOException ActionServlet에서 IO와 관련된 오류가 발생할 경우 
	 * @exception javax.servlet.ServletException 서블릿과 관련된 오류가 발생할 경우
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String actionKey = getActionKey(request);
			String actionClassName = getActionClass(actionKey);
			Action action = null;
			if (actionClassName == null) {
				throw new PageNotFoundExeption("controller");
			} else {
				Class<?> actionClass = Class.forName(actionClassName);
				action = (Action) actionClass.newInstance();
				long currTime = 0;
				if (getLogger().isDebugEnabled()) {
					currTime = System.currentTimeMillis();
					getLogger().debug("★★★ " + request.getRemoteAddr() + " 로 부터 \"" + request.getMethod() + " " + request.getRequestURI() + "\" 요청이 시작되었습니다");
					getLogger().debug("ContentLength : " + request.getContentLength() + "bytes");
				}
				action.execute(this, request, response);
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("☆☆☆ " + request.getRemoteAddr() + " 로 부터 \"" + request.getMethod() + " " + request.getRequestURI() + "\" 요청이 종료되었습니다 | duration : " + (System.currentTimeMillis() - currTime) + "ms\n");
				}
			}
		} catch (PageNotFoundExeption e) {
			RequestDispatcher dispatcher = (RequestDispatcher) getServletContext().getAttribute("default-servlet-dispatcher");
			if (dispatcher != null) {
				dispatcher.forward(request, response);
			} else {
				response.sendError(404);
				response.setContentType("text/plain;charset=utf-8");
			}
			return;
		} catch (Exception e) {
			getLogger().error(e);
			throw new RuntimeException(e);
		}
	}

	private String getActionClass(String actionKey) {
		ResourceBundle bundle = (ResourceBundle) getServletContext().getAttribute("action-mapping");
		try {
			return ((String) bundle.getObject(actionKey)).trim();
		} catch (MissingResourceException e) {
			getLogger().error("error actionkey is " + actionKey);
			getLogger().error(e.getMessage());
			return null;
		}
	}

	private String getActionKey(HttpServletRequest request) {
		String path = request.getServletPath();
		int slash = path.lastIndexOf("/");
		int period = path.lastIndexOf(".");
		if (period > 0 && period > slash) {
			path = path.substring(0, period);
			return path;
		}
		return null;
	}

	private Log getLogger() {
		return this._logger;
	}
}