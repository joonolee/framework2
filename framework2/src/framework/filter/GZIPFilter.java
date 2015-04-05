package framework.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * GZIP Compression filter
 */
public class GZIPFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		MyResponseWrapper resWrapper = null;
		try {
			resWrapper = new MyResponseWrapper((HttpServletResponse) response);
			filterChain.doFilter(request, resWrapper);
			String contentType = _nullToBlankString(resWrapper.getContentType());
			if (_isTextualContentType(contentType)) {
				if (_isGzipSupported(request)) {
					resWrapper.setHeader("Content-Encoding", "gzip");
					GZIPOutputStream gzos = new GZIPOutputStream(response.getOutputStream());
					OutputStreamWriter osw = new OutputStreamWriter(gzos, response.getCharacterEncoding());
					PrintWriter writer = new PrintWriter(osw);
					writer.print(resWrapper.toString());
					writer.flush();
					gzos.finish();
				} else {
					PrintWriter writer = response.getWriter();
					writer.print(resWrapper.toString());
					writer.flush();
				}
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

	private boolean _isGzipSupported(ServletRequest request) {
		String browserEncodings = ((HttpServletRequest) request).getHeader("Accept-Encoding");
		return ((browserEncodings != null) && (browserEncodings.indexOf("gzip") != -1));
	}

	private boolean _isTextualContentType(String contentType) {
		return "".equals(contentType) || contentType.contains("text") || contentType.contains("json") || contentType.contains("xml");
	}

	private static String _nullToBlankString(String str) {
		String rval = "";
		if (str == null) {
			rval = "";
		} else {
			rval = str;
		}
		return rval;
	}

	class MyResponseWrapper extends HttpServletResponseWrapper {
		private ByteArrayOutputStream _bytes;
		private PrintWriter _writer;

		public MyResponseWrapper(HttpServletResponse p_res) throws IOException {
			super(p_res);
			_bytes = new ByteArrayOutputStream(8 * 1024);
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
		public void write(int p_c) throws IOException {
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