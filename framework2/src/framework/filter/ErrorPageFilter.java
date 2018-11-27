package framework.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 에러페이지 처리  Filter
 */
public class ErrorPageFilter implements Filter {
	// 서블릿 컨텍스트 객체
	private ServletContext application = null;
	// 에러페이지 Url 맵 객체
	private Map<String, String> errorpageMap = new HashMap<String, String>();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		final HttpServletRequest req = (HttpServletRequest) request;
		final HttpServletResponse res = (HttpServletResponse) response;
		HttpServletResponse resWrapper = new HttpServletResponseWrapper(res) {
			@Override
			public void sendError(int sc) throws IOException {
				ErrorPageFilter.this.sendError(sc, null, req, res);
			}

			@Override
			public void sendError(int sc, String msg) throws IOException {
				ErrorPageFilter.this.sendError(sc, msg, req, res);
			}
		};
		try {
			filterChain.doFilter(request, resWrapper);
		} catch (Throwable e) {
			sendError(500, null, req, res);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// 서블릿 컨텍스트 할당
		this.application = filterConfig.getServletContext();
		// 클라이언트 오류(4xx)
		this.errorpageMap.put("4xx", filterConfig.getInitParameter("4xx"));
		this.errorpageMap.put("40x", filterConfig.getInitParameter("40x"));
		this.errorpageMap.put("41x", filterConfig.getInitParameter("41x"));
		this.errorpageMap.put("42x", filterConfig.getInitParameter("42x"));
		this.errorpageMap.put("43x", filterConfig.getInitParameter("43x"));
		this.errorpageMap.put("44x", filterConfig.getInitParameter("44x"));
		this.errorpageMap.put("45x", filterConfig.getInitParameter("45x"));
		this.errorpageMap.put("49x", filterConfig.getInitParameter("49x"));
		this.errorpageMap.put("400", filterConfig.getInitParameter("400"));
		this.errorpageMap.put("401", filterConfig.getInitParameter("401"));
		this.errorpageMap.put("402", filterConfig.getInitParameter("402"));
		this.errorpageMap.put("403", filterConfig.getInitParameter("403"));
		this.errorpageMap.put("404", filterConfig.getInitParameter("404"));
		this.errorpageMap.put("405", filterConfig.getInitParameter("405"));
		this.errorpageMap.put("406", filterConfig.getInitParameter("406"));
		this.errorpageMap.put("407", filterConfig.getInitParameter("407"));
		this.errorpageMap.put("408", filterConfig.getInitParameter("408"));
		this.errorpageMap.put("409", filterConfig.getInitParameter("409"));
		this.errorpageMap.put("410", filterConfig.getInitParameter("410"));
		this.errorpageMap.put("411", filterConfig.getInitParameter("411"));
		this.errorpageMap.put("412", filterConfig.getInitParameter("412"));
		this.errorpageMap.put("413", filterConfig.getInitParameter("413"));
		this.errorpageMap.put("414", filterConfig.getInitParameter("414"));
		this.errorpageMap.put("415", filterConfig.getInitParameter("415"));
		this.errorpageMap.put("416", filterConfig.getInitParameter("416"));
		this.errorpageMap.put("417", filterConfig.getInitParameter("417"));
		this.errorpageMap.put("418", filterConfig.getInitParameter("418"));
		this.errorpageMap.put("420", filterConfig.getInitParameter("420"));
		this.errorpageMap.put("422", filterConfig.getInitParameter("422"));
		this.errorpageMap.put("423", filterConfig.getInitParameter("423"));
		this.errorpageMap.put("424", filterConfig.getInitParameter("424"));
		this.errorpageMap.put("425", filterConfig.getInitParameter("425"));
		this.errorpageMap.put("426", filterConfig.getInitParameter("426"));
		this.errorpageMap.put("428", filterConfig.getInitParameter("428"));
		this.errorpageMap.put("429", filterConfig.getInitParameter("429"));
		this.errorpageMap.put("431", filterConfig.getInitParameter("431"));
		this.errorpageMap.put("444", filterConfig.getInitParameter("444"));
		this.errorpageMap.put("449", filterConfig.getInitParameter("449"));
		this.errorpageMap.put("450", filterConfig.getInitParameter("450"));
		this.errorpageMap.put("451", filterConfig.getInitParameter("451"));
		this.errorpageMap.put("494", filterConfig.getInitParameter("494"));
		this.errorpageMap.put("495", filterConfig.getInitParameter("495"));
		this.errorpageMap.put("496", filterConfig.getInitParameter("496"));
		this.errorpageMap.put("497", filterConfig.getInitParameter("497"));
		this.errorpageMap.put("499", filterConfig.getInitParameter("499"));
		// 서버 오류(5xx)
		this.errorpageMap.put("5xx", filterConfig.getInitParameter("5xx"));
		this.errorpageMap.put("50x", filterConfig.getInitParameter("50x"));
		this.errorpageMap.put("51x", filterConfig.getInitParameter("51x"));
		this.errorpageMap.put("52x", filterConfig.getInitParameter("52x"));
		this.errorpageMap.put("59x", filterConfig.getInitParameter("59x"));
		this.errorpageMap.put("500", filterConfig.getInitParameter("500"));
		this.errorpageMap.put("501", filterConfig.getInitParameter("501"));
		this.errorpageMap.put("502", filterConfig.getInitParameter("502"));
		this.errorpageMap.put("503", filterConfig.getInitParameter("503"));
		this.errorpageMap.put("504", filterConfig.getInitParameter("504"));
		this.errorpageMap.put("505", filterConfig.getInitParameter("505"));
		this.errorpageMap.put("506", filterConfig.getInitParameter("506"));
		this.errorpageMap.put("507", filterConfig.getInitParameter("507"));
		this.errorpageMap.put("508", filterConfig.getInitParameter("508"));
		this.errorpageMap.put("509", filterConfig.getInitParameter("509"));
		this.errorpageMap.put("510", filterConfig.getInitParameter("510"));
		this.errorpageMap.put("511", filterConfig.getInitParameter("511"));
		this.errorpageMap.put("520", filterConfig.getInitParameter("520"));
		this.errorpageMap.put("598", filterConfig.getInitParameter("598"));
		this.errorpageMap.put("599", filterConfig.getInitParameter("599"));
	}

	@Override
	public void destroy() {
	}

	private void sendError(int sc, String message, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String statusCode = Integer.toString(sc);
		String[] keys = new String[] { statusCode, statusCode.subSequence(0, 2) + "x", statusCode.subSequence(0, 1) + "xx" };
		String errorpageUrl = null;
		for (String key : keys) {
			if ((errorpageUrl = this.errorpageMap.get(key)) != null) {
				break;
			}
		}
		if (errorpageUrl != null) {
			try {
				this.application.getRequestDispatcher(response.encodeURL(errorpageUrl)).forward(request, response);
			} catch (ServletException e) {
			}
		} else {
			// 처리되지 않은 상태코드는  bypass
			if (message == null) {
				response.sendError(sc);
			} else {
				response.sendError(sc, message);
			}
		}
	}
}
