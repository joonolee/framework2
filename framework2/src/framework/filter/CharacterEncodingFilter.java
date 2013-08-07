/* 
 * @(#)CharacterEncodingFilter.java
 * 요청과 응답을 인코딩 하는 필터
 */
package framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CharacterEncodingFilter implements Filter {
	private Log _logger = LogFactory.getLog(framework.filter.CharacterEncodingFilter.class);
	private String _encoding = null;
	private boolean _force = true;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		long currTime = 0;
		if (_getLogger().isDebugEnabled()) {
			currTime = System.currentTimeMillis();
			_getLogger().debug("Start [ encoding=" + this._encoding + ", force=" + this._force + " ]");
		}
		if (this._force || (request.getCharacterEncoding() == null)) {
			if (this._encoding != null) {
				request.setCharacterEncoding(this._encoding);
			}
		}
		if (this._force || (response.getCharacterEncoding() == null)) {
			if (this._encoding != null) {
				response.setCharacterEncoding(this._encoding);
			}
		}
		filterChain.doFilter(request, response);
		if (_getLogger().isDebugEnabled()) {
			_getLogger().debug("End | duration : " + (System.currentTimeMillis() - currTime) + " msec");
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this._encoding = filterConfig.getInitParameter("encoding");
		String force = filterConfig.getInitParameter("force");
		if (force == null) {
			this._force = true;
		} else if (force.equalsIgnoreCase("true")) {
			this._force = true;
		} else if (force.equalsIgnoreCase("yes")) {
			this._force = true;
		} else {
			this._force = false;
		}
	}

	@Override
	public void destroy() {
	}

	private Log _getLogger() {
		return this._logger;
	}
}
