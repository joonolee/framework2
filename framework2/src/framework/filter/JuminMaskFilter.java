/* 
 * @(#)JuminMaskFilter.java
 * 응답데이터에서 주민번호 패턴 마스킹 필터
 */
package framework.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class JuminMaskFilter implements Filter {
	private static final String _JUMIN_PATTERN = "(\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12][0-9]|3[01])(?:\\s|&nbsp;)*[-|~]?(?:\\s|&nbsp;)*)[1-8]\\d{6}";

	private static Pattern _juminPattern;

	static {
		_juminPattern = Pattern.compile(_JUMIN_PATTERN, Pattern.MULTILINE);
	}

	public void doFilter(ServletRequest p_req, ServletResponse p_res, FilterChain p_chain) throws IOException, ServletException {
		StringResponseWrapper responseWrapper = new StringResponseWrapper((HttpServletResponse) p_res);
		p_chain.doFilter(p_req, responseWrapper);
		StringBuffer l_result = responseWrapper.getBuffer();
		String l_juminMaskData = _juminPattern.matcher(l_result).replaceAll("$1******");
		PrintWriter l_writer = p_res.getWriter();
		l_writer.print(l_juminMaskData);
		l_writer.flush();
		l_writer.close();
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}

	public class StringResponseWrapper extends HttpServletResponseWrapper {
		private StringWriter _stringWriter;

		public StringResponseWrapper(HttpServletResponse p_res) {
			super(p_res);
			_stringWriter = new StringWriter(4096);
		}

		@Override
		public PrintWriter getWriter() {
			return new PrintWriter(_stringWriter);
		}

		@Override
		public ServletOutputStream getOutputStream() {
			return new StringOutputStream(_stringWriter);
		}

		@Override
		public String toString() {
			return _stringWriter.toString();
		}

		public StringBuffer getBuffer() {
			return _stringWriter.getBuffer();
		}
	}

	public class StringOutputStream extends ServletOutputStream {
		private StringWriter _stringWriter;

		public StringOutputStream(StringWriter p_stringWriter) {
			this._stringWriter = p_stringWriter;
		}

		@Override
		public void write(int p_c) {
			_stringWriter.write(p_c);
		}
	}
}
