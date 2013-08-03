/* 
 * @(#)JuminMaskFilter.java
 * 응답데이터에서 주민번호 패턴 마스킹 필터
 */
package framework.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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

import framework.util.StringUtil;

public class JuminMaskFilter implements Filter {
	private static final String _JUMIN_PATTERN = "(?<=[^0-9])(\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12][0-9]|3[01])(?:\\s|&nbsp;)*[-|~]?(?:\\s|&nbsp;)*)[1-8]\\d{6}(?=[^0-9])?";

	private static Pattern _juminPattern;

	static {
		_juminPattern = Pattern.compile(_JUMIN_PATTERN, Pattern.MULTILINE);
	}

	public void doFilter(ServletRequest p_req, ServletResponse p_res, FilterChain p_chain) throws IOException, ServletException {
		MyResponseWrapper l_resWrapper = null;
		try {
			l_resWrapper = new MyResponseWrapper((HttpServletResponse) p_res);
			p_chain.doFilter(p_req, l_resWrapper);
			String l_contentType = StringUtil.nullToBlankString(p_res.getContentType());
			if (l_contentType.contains("text") || l_contentType.contains("json")) {
				String l_juminMaskData = _juminPattern.matcher(l_resWrapper.toString()).replaceAll("$1******");
				PrintWriter l_writer = p_res.getWriter();
				l_writer.print(l_juminMaskData);
				l_writer.flush();
				l_writer.close();
			} else {
				l_resWrapper.writeTo(p_res.getOutputStream());
			}
		} finally {
			if (l_resWrapper != null) {
				l_resWrapper.close();
			}
		}
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}

	public class MyResponseWrapper extends HttpServletResponseWrapper {
		private ByteArrayOutputStream _bytes;
		private PrintWriter _writer;

		public MyResponseWrapper(HttpServletResponse p_res) {
			super(p_res);
			_bytes = new ByteArrayOutputStream();
			_writer = new PrintWriter(_bytes);
		}

		@Override
		public PrintWriter getWriter() {
			return _writer;
		}

		@Override
		public ServletOutputStream getOutputStream() {
			return new MyOutputStream(_bytes);
		}

		@Override
		public String toString() {
			_writer.flush();
			return _bytes.toString();
		}

		public void writeTo(OutputStream os) throws IOException {
			_bytes.writeTo(os);
		}

		public void close() throws IOException {
			_bytes.close();
			_writer.close();
		}
	}

	public class MyOutputStream extends ServletOutputStream {
		private ByteArrayOutputStream _bytes;

		public MyOutputStream(ByteArrayOutputStream p_bytes) {
			this._bytes = p_bytes;
		}

		@Override
		public void write(int p_c) {
			_bytes.write(p_c);
		}

		@Override
		public void write(byte[] b) throws IOException {
			_bytes.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			_bytes.write(b, off, len);
		}
	}
}