/** 
 * @(#)Action.java
 */
package framework.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import framework.config.Configuration;
import framework.db.ConnectionManager;

/** 
 * 비지니스 로직을 처리하는 클래스가 상속받아야 할 추상클래스이다.
 * 뷰페이지(jsp 페이지)가 실행되기 전에 클라이언트에서 서버로 전송된 데이터를 편리하게 업무로직에 반영하기 
 * 위한 전처리(Pre-processing)모듈이다. 하나의 서비스에 대해 여러개의 업무로직을 컴포넌트 형태로 저작하여 등록할 수 있다. 
 * 작성된 Actioin은 action.properties에 등록된다.
 */
public abstract class Action {
	private Map<String, ConnectionManager> _connMgrMap = new HashMap<String, ConnectionManager>();
	private HttpServlet _servlet = null;
	private Box _input = null;
	private Box _cookies = null;
	private MultipartBox _multipartInput = null;
	private PrintWriter _out = null;
	private HttpServletRequest _request = null;
	private HttpServletResponse _response = null;
	private static Log _logger = LogFactory.getLog(framework.action.Action.class);

	/** 
	 * 클라이언트에서 서비스를 호출할 때 요청파라미터 action에 설정된 값을 참고하여 해당 메소드를 실행한다.
	 * 메소드명은 process 를 접두어로 하여 action값을 추가한 명칭이다.
	 * 정의되지 않은 메소드를 호출할 경우 로그에 오류메시지가 기록되며 메소드 실행을 마친 후 데이타베이스 컨넥을 자동으로 닫아준다.
	 * <br>
	 * ex) action이 search 일때 => processSearch() 메소드가 호출된다.
	 * 
	 * @param servlet 서블릿 객체
	 * @param request 클라이언트에서 요청된 Request객체
	 * @param response 클라이언트로 응답할 Response객체
	 */
	public void execute(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) {
		setServlet(servlet);
		setRequest(request);
		setResponse(response);
		try {
			Method method = getMethod(request.getParameter("action"));
			method.invoke(this, (Object[]) null);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			getLogger().error("Action execute Error!", e);
		} finally {
			destroy();
		}
	}

	/**
	 * 요청을 JSP페이지로 포워드(Forward) 한다.
	 * 작성된 JSP페이지는  action.properties에 등록된다.
	 * <br>
	 * ex) 키가 search-jsp 인 JSP페이지로 포워딩 할 경우 => route("search-jsp")
	 * 
	 * @param key action.properties 파일에 등록된 JSP 페이지의 키
	 */
	protected void route(String key) {
		try {
			ActionRouter router = new ActionRouter(key);
			router.route(getServlet(), getRequest(), getResponse());
		} catch (Exception e) {
			getLogger().error("Router Error!", e);
		}
	}

	/**
	 * 요청을 JSP페이지로 포워드(Forward) 한다.
	 * 작성된 JSP페이지는  action.properties에 등록된다.
	 * <br>
	 * ex) 키가 search-jsp 인 JSP페이지로 포워딩 할 경우 => render("search-jsp")
	 * 
	 * @param key action.properties 파일에 등록된 JSP 페이지의 키
	 */
	protected void render(String key) {
		route(key);
	}

	/** 
	 * 요청을 JSP페이지로 포워드(Forward) 또는 재지향(Redirect) 한다.
	 * 작성된 JSP페이지는  action.properties에 등록된다.
	 * <br>
	 * ex1) 키가 search-jsp 인 JSP페이지로 포워딩 할 경우 => route("search-jsp", true)
	 * <br>
	 * ex2) 키가 search-jsp 인 JSP페이지로 재지향 할 경우 => route("search-jsp", false)
	 * 
	 * @param key action.properties 파일에 등록된 JSP 페이지의 키
	 * @param isForward true이면 포워드(Forward), false 이면 재지향(Redirect)
	 */
	protected void route(String key, boolean isForward) {
		try {
			ActionRouter router = new ActionRouter(key, isForward);
			router.route(getServlet(), getRequest(), getResponse());
		} catch (Exception e) {
			getLogger().error("Router Error!", e);
		}
	}

	/** 
	 * 요청을 JSP페이지로 재지향(Redirect) 한다.
	 * 작성된 JSP페이지는  action.properties에 등록된다.
	 * <br>
	 * ex) 키가 search-jsp 인 JSP페이지로 재지향 할 경우 => redirect("search-jsp")
	 * 
	 * @param key action.properties 파일에 등록된 JSP 페이지의 키
	 */
	protected void redirect(String key) {
		route(key, false);
	}

	/** 
	 * 데이타베이스 연결관리자(컨넥션 매니저) 객체를 리턴한다.
	 * <br>
	 * config.properties에 datasource가 등록되어 있으면 JNDI에 등록되어있는 데이타소스에서 컨넥션을 생성한다.
	 * datasource가 등록되어 있지 않는 경우 연결정보를 바탕으로 jdbc 컨넥션을 생성한다.
	 * 업무명이 default에 해당하는 설정파일 정보를 이용하여 컨넥션을 생성한다.
	 * 생성된 컨넥션의 autoCommit 속성은 false 로 셋팅된다.
	 *
	 * @return 연결관리자(컨넥션 매니저) 객체
	 */
	protected ConnectionManager getConnectionManager() {
		return getConnectionManager("default");
	}

	/** 
	 * 데이타베이스 연결관리자(컨넥션 매니저) 객체를 리턴한다.
	 * <br>
	 * config.properties에 datasource가 등록되어 있으면 JNDI에 등록되어있는 데이타소스에서 컨넥션을 생성한다.
	 * datasource가 등록되어 있지 않는 경우 연결정보를 바탕으로 jdbc 컨넥션을 생성한다.
	 * 파라미터로 넘겨진 업무명에 해당하는 설정파일 정보를 이용하여 컨넥션을 생성한다.
	 * 생성된 컨넥션의 autoCommit 속성은 false 로 셋팅된다.
	 *
	 * @param serviceName 서비스명(업무명)
	 * @return 연결관리자(컨넥션 매니저) 객체
	 */
	protected ConnectionManager getConnectionManager(String serviceName) {
		if (!this._connMgrMap.containsKey(serviceName)) {
			String dsName = null;
			String jdbcDriver = null;
			String jdbcUrl = null;
			String jdbcUid = null;
			String jdbcPw = null;
			try {
				dsName = getConfig().getString("jdbc." + serviceName + ".datasource");
			} catch (Exception e) {
				// 설정파일에 데이타소스가 정의되어있지 않으면 실행
				jdbcDriver = getConfig().getString("jdbc." + serviceName + ".driver");
				jdbcUrl = getConfig().getString("jdbc." + serviceName + ".url");
				jdbcUid = getConfig().getString("jdbc." + serviceName + ".uid");
				jdbcPw = getConfig().getString("jdbc." + serviceName + ".pwd");
			}
			try {
				ConnectionManager connMgr = new ConnectionManager(dsName, this);
				if (dsName != null) {
					connMgr.connect();
				} else {
					connMgr.connect(jdbcDriver, jdbcUrl, jdbcUid, jdbcPw);
				}
				connMgr.setAutoCommit(false);
				this._connMgrMap.put(serviceName, connMgr);
			} catch (Exception e) {
				getLogger().error("DB Connection Error!", e);
			}
		}
		return this._connMgrMap.get(serviceName);
	}

	/** 
	 * 설정정보를 가지고 있는 객체를 생성하여 리턴한다.
	 *
	 * @return config.properties의 설정정보를 가지고 있는 객체
	 */
	protected Configuration getConfig() {
		return Configuration.getInstance();
	}

	/** 
	 * Action객체를 호출한 서블릿 객체를 리턴한다.
	 *
	 * @return Action객체를 호출한 서블릿 객체
	 */
	protected HttpServlet getServlet() {
		return this._servlet;
	}

	/** 
	 * HTTP 클라이언트 요청 객체를 리턴한다.
	 *
	 * @return HTTP 클라이언트 요청객체
	 */
	protected HttpServletRequest getRequest() {
		return this._request;
	}

	/** 
	 * HTTP 클라이언트 응답 객체를 리턴한다.
	 *
	 * @return HTTP 클라이언트 응답객체
	 */
	protected HttpServletResponse getResponse() {
		return this._response;
	}

	/** 
	 * 클라이언트의 세션 객체를 리턴한다.
	 * 이미 세션이 생성되어 있는경우는 기존 세션을 리턴하며 세션이 없는경우는 새로 생성하여 리턴한다. 
	 *
	 * @return 클라이언트의 세션 객체
	 */
	protected HttpSession getSession() {
		return getRequest().getSession();
	}

	/** 
	 * 클라이언트의 세션 객체를 리턴한다.
	 * 세션이 없는경우는 파라미터 값이 true이면 세션을 새로 생성하며 false 이면 생성하지 않는다. 
	 *
	 * @param create 세션이 없을경우 true이면 새로 생성, false이면 생성하지 않음
	 * @return 클라이언트의 세션 객체
	 */
	protected HttpSession getSession(boolean create) {
		return getRequest().getSession(create);
	}

	/** 
	 * 세션객체에서 해당 키에 해당하는 오브젝트를 리턴한다.
	 * <br>
	 * ex) 세션에서 result라는 키로 오브젝트를 리턴받는 경우 => Object obj = getSessionAttribute("result")
	 *
	 * @param key 세션객체의 조회키
	 * @return 세션객체에서 얻어온 오브젝트
	 */
	protected Object getSessionAttribute(String key) {
		return getSession().getAttribute(key);
	}

	/** 
	 * 요청파라미터의 값을 담고 있는 해시테이블을 리턴한다.
	 * <br>
	 * ex1) [ name=홍길동 ]인 요청파라미터를 받아오는 경우 => String name = getInput().getString("name")
	 * <br>
	 * ex2) [ age=20 ]인 요청파라미터를 받아오는 경우 => Integer age = getInput().getInteger("age")
	 *
	 * @return 요청파라미터의 값을 담는 해시테이블
	 */
	protected Box getInput() {
		if (this._input == null) {
			this._input = Box.getBox(getRequest());
		}
		return this._input;
	}

	/** 
	 * Multipart 요청파라미터의 값을 담고 있는 해시테이블을 리턴한다.
	 * <br>
	 * ex1) [ name=홍길동 ]인 요청파라미터를 받아오는 경우 => String name = getMultipartInput().getString("name")
	 * <br>
	 * ex2) [ age=20 ]인 요청파라미터를 받아오는 경우 => Integer age = getMultipartInput().getInteger("age")
	 * <br>
	 * ex3) 전송된 파일를 받아오는 경우 => List<FileItem> files = getMultipartInput().getFileItems()
	 * 
	 * @return 요청파라미터의 값을 담는 해시테이블
	 */
	protected MultipartBox getMultipartInput() {
		if (this._multipartInput == null) {
			this._multipartInput = MultipartBox.getMultipartBox(getRequest());
		}
		return this._multipartInput;
	}

	/** 
	 * 쿠키값을 담고 있는 해시테이블을 리턴한다.
	 * <br>
	 * ex1) [ name=홍길동 ]인 쿠키를 받아오는 경우 => String name = getCookies().getString("name")
	 * <br>
	 * ex2) [ age=20 ]인 쿠키를 받아오는 경우 => Integer age = getCookies().getInteger("age")
	 *
	 * @return 쿠키값을 담는 해시테이블
	 */
	protected Box getCookies() {
		if (this._cookies == null) {
			this._cookies = Box.getBoxFromCookie(getRequest());
		}
		return this._cookies;
	}

	/** 
	 * 응답객체의 PrintWriter 객체를 리턴한다.
	 * <br>
	 * ex) 응답에 Hello World 를 쓰는 경우 => getOut().println("Hello World!")
	 *
	 * @return 응답객체의 PrintWriter 객체
	 */
	protected PrintWriter getOut() {
		if (this._out == null) {
			try {
				this._out = getResponse().getWriter();
			} catch (IOException e) {
			}
		}
		return this._out;
	}

	/** 
	 * Action의 로거객체를 리턴한다.
	 * 모든 로그는 해당 로거를 이용해서 출력하여야 한다.
	 * <br>
	 * ex1) 에러 정보를 출력할 경우 => getLogger().error("...에러메시지내용")
	 * <br>
	 * ex2) 디버그 정보를 출력할 경우 => getLogger().debug("...디버그메시지내용")
	 *
	 * @return Action의 로거객체
	 */
	protected Log getLogger() {
		return Action._logger;
	}

	/**
	 * 응답객체를 클라이언트에게 전송하기 전에 컨텐츠타입을 설정한다. 
	 * <br>
	 * ex1) xml파일을 전송 하는 경우 => setContentType("text/xml; charset=utf-8")
	 * <br>
	 * ex2) 텍스트 파일을 전송하는 경우 => setContentType("text/plain; charset=euc-kr")
	 *
	 * @param contentType 응답객체에 설정할 컨텐츠 타입
	 */
	protected void setContentType(String contentType) {
		getResponse().setContentType(contentType);
	}

	/** 
	 * 요청객체에 키,값 속성을 설정한다.
	 * Action에서 처리한 결과를 뷰 로 넘길때 요청객체에 속성을 설정하여 라우팅한다.
	 * <br>
	 * ex) rs라는 RecordSet 객체를 result 라는 키로 요청객체에 설정하는 경우 => setAttribute("result", re) 
	 *
	 * @param key 속성의 키 문자열
	 * @param value 속성의 값 객체
	 */
	protected void setAttribute(String key, Object value) {
		getRequest().setAttribute(key, value);
	}

	/** 
	 * 세션객체에 키,값 속성을 설정한다.
	 * Action에서 처리한 결과를 세션에 저장한다.
	 * <br>
	 * ex) userinfo 라는 사용자정보객체를 userinfo 라는 키로 세션객체에 설정하는 경우 => setSessionAttribute("userinfo", userinfo)
	 *
	 * @param key 속성의 키 문자열
	 * @param value 속성의 값 객체
	 */
	protected void setSessionAttribute(String key, Object value) {
		getSession().setAttribute(key, value);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드
	private void setServlet(HttpServlet servlet) {
		this._servlet = servlet;
	}

	private void setRequest(HttpServletRequest req) {
		this._request = req;
	}

	private void setResponse(HttpServletResponse res) {
		this._response = res;
	}

	private void destroy() {
		ConnectionManager connMgr = null;
		for (String key : this._connMgrMap.keySet()) {
			connMgr = this._connMgrMap.get(key);
			if (connMgr != null) {
				connMgr.release();
				connMgr = null;
			}
		}
		this._connMgrMap.clear();
		this._input = null;
		this._out = null;
	}

	private Method getMethod(String methodName) {
		if (methodName == null || methodName.trim().equals("")) {
			methodName = "init";
		}
		StringBuilder sb = new StringBuilder(methodName);
		sb.setCharAt(0, Character.toUpperCase(methodName.charAt(0)));
		String name = "process" + sb.toString().trim();
		Method m = getMethod(this.getClass(), name);
		if (m == null) {
			throw new IllegalArgumentException("Can not find method named '" + name + "' ");
		}
		return m;
	}

	private static Method getMethod(Class<?> actionClass, String methodName) {
		Method method[] = actionClass.getMethods();
		for (int i = 0; i < method.length; i++) {
			if (method[i].getName().equals(methodName)) {
				return method[i];
			}
		}
		return null;
	}
}