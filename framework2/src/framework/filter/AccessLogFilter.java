/* 
 * @(#)AccessLogFilter.java
 * 클라이언트 요청 시작과 종료를 로깅하는 필터
 */
package framework.filter;

import java.io.IOException;
import java.lang.reflect.Array;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AccessLogFilter implements Filter {
	private static Log _logger = LogFactory.getLog(framework.filter.AccessLogFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) req;
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("★★★ " + httpReq.getRemoteAddr() + " 로 부터 \"" + httpReq.getMethod() + " " + httpReq.getRequestURI() + "\" 요청이 시작되었습니다");
			getLogger().debug(getParamString(httpReq));
			getLogger().debug("ContentLength : " + httpReq.getContentLength() + "bytes");
		}
		chain.doFilter(req, res);
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("★★★ " + httpReq.getRemoteAddr() + " 로 부터 \"" + httpReq.getMethod() + " " + httpReq.getRequestURI() + "\" 요청이 종료되었습니다\n");
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	private String getParamString(HttpServletRequest req) {
		StringBuilder buf = new StringBuilder();
		buf.append("{ ");
		long currentRow = 0;
		for (Object obj : req.getParameterMap().keySet()) {
			String key = (String) obj;
			String value = null;
			Object o = req.getParameterValues(key);
			if (o == null) {
				value = "";
			} else {
				int length = Array.getLength(o);
				if (length == 0) {
					value = "";
				} else if (length == 1) {
					Object item = Array.get(o, 0);
					if (item == null) {
						value = "";
					} else {
						value = item.toString();
					}
				} else {
					StringBuilder valueBuf = new StringBuilder();
					valueBuf.append("[");
					for (int j = 0; j < length; j++) {
						Object item = Array.get(o, j);
						if (item != null) {
							valueBuf.append(item.toString());
						}
						if (j < length - 1) {
							valueBuf.append(",");
						}
					}
					valueBuf.append("]");
					value = valueBuf.toString();
				}
			}
			if (currentRow++ > 0) {
				buf.append(", ");
			}
			buf.append(key + "=" + value);
		}
		buf.append(" }");
		return "Box[requestbox]=" + buf.toString();
	}

	private Log getLogger() {
		return AccessLogFilter._logger;
	}
}