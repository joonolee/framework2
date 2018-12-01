/*
 * @(#)XSSFilter.java
 * Anti cross-site scripting (XSS) filter
 */
package framework.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XSSFilter implements Filter {
	// Avoid anything between script tags
	private Pattern _scriptPattern1 = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);

	// Avoid anything in a src='...' type of expression
	private Pattern _scriptPattern2 = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	private Pattern _scriptPattern3 = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	// Remove any lonesome </script> tag
	private Pattern _scriptPattern4 = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);

	// Remove any lonesome <script ...> tag
	private Pattern _scriptPattern5 = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	// Avoid eval(...) expressions
	private Pattern _scriptPattern6 = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	// Avoid expression(...) expressions
	private Pattern _scriptPattern7 = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	// Avoid javascript:... expressions
	private Pattern _scriptPattern8 = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);

	// Avoid vbscript:... expressions
	private Pattern _scriptPattern9 = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);

	// Avoid onload= expressions
	private Pattern _scriptPattern10 = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		filterChain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	class XSSRequestWrapper extends HttpServletRequestWrapper {
		public XSSRequestWrapper(HttpServletRequest servletRequest) {
			super(servletRequest);
		}

		@Override
		public String[] getParameterValues(String parameter) {
			String[] values = super.getParameterValues(parameter);
			if (values == null) {
				return null;
			}
			int count = values.length;
			String[] encodedValues = new String[count];
			for (int i = 0; i < count; i++) {
				encodedValues[i] = stripXSS(values[i]);
			}
			return encodedValues;
		}

		@Override
		public String getParameter(String parameter) {
			String value = super.getParameter(parameter);
			return stripXSS(value);
		}

		@Override
		public String getHeader(String name) {
			String value = super.getHeader(name);
			return stripXSS(value);
		}

		private String stripXSS(String value) {
			if (value != null) {
				value = value.replaceAll("\0", ""); // Avoid null characters
				value = _scriptPattern1.matcher(value).replaceAll("");
				value = _scriptPattern2.matcher(value).replaceAll("");
				value = _scriptPattern3.matcher(value).replaceAll("");
				value = _scriptPattern4.matcher(value).replaceAll("");
				value = _scriptPattern5.matcher(value).replaceAll("");
				value = _scriptPattern6.matcher(value).replaceAll("");
				value = _scriptPattern7.matcher(value).replaceAll("");
				value = _scriptPattern8.matcher(value).replaceAll("");
				value = _scriptPattern9.matcher(value).replaceAll("");
				value = _scriptPattern10.matcher(value).replaceAll("");
			}
			return value;
		}
	}
}