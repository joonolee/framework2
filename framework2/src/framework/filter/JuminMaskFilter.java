/*
 * @(#)JuminMaskFilter.java
 * 응답데이터에서 주민번호 패턴 마스킹 필터
 */
package framework.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.regex.Matcher;
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
	private Pattern _juminPattern = Pattern.compile("(?<=[^0-9])(\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12][0-9]|3[01])(?:\\s|&nbsp;)*[-|~]?(?:\\s|&nbsp;)*)[1-8]\\d{6}(?=[^0-9])?", Pattern.MULTILINE);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		MyResponseWrapper resWrapper = null;
		try {
			resWrapper = new MyResponseWrapper((HttpServletResponse) response);
			filterChain.doFilter(request, resWrapper);
			String contentType = StringUtil.nullToBlankString(resWrapper.getContentType()).toLowerCase();
			if ("".equals(contentType) || contentType.contains("text") || contentType.contains("json") || contentType.contains("xml")) {
				Matcher matcher = _juminPattern.matcher(resWrapper.toString());
				String juminMaskData = matcher.replaceAll("$1******");
				PrintWriter writer = response.getWriter();
				writer.print(juminMaskData);
				writer.flush();
				writer.close();
				writer = null;
			} else {
				resWrapper.writeTo(response.getOutputStream());
			}
		} finally {
			if (resWrapper != null) {
				resWrapper.close();
				resWrapper = null;
			}
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	class MyResponseWrapper extends HttpServletResponseWrapper {
		private ByteArrayOutputStream _bytes;
		private PrintWriter _writer;

		public MyResponseWrapper(HttpServletResponse p_res) {
			super(p_res);
			_bytes = new ByteArrayOutputStream(8192);
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
			_bytes = null;
			_writer = null;
		}
	}

	class MyOutputStream extends ServletOutputStream {
		private ByteArrayOutputStream _bytes;

		public MyOutputStream(ByteArrayOutputStream p_bytes) {
			_bytes = p_bytes;
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

		@Override
		public void close() throws IOException {
			_bytes.close();
			super.close();
			_bytes = null;
		}
	}
}