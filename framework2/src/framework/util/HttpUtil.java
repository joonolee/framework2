package framework.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * HTTP 클라이언트의 기능을 이용할 수 있는 유틸리티 클래스이다.
 */
public class HttpUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private HttpUtil() {
	}

	/**
	 * Result 객체
	 */
	public static class Result {
		private int _statusCode;
		private String _content;

		public Result() {
			super();
		}

		public Result(int statusCode, String content) {
			super();
			this._statusCode = statusCode;
			this._content = content;
		}

		public int getStatusCode() {
			return _statusCode;
		}

		public String getContent() {
			return _content;
		}

		@Override
		public String toString() {
			return String.format("Result={ statusCode : %d, content : %s }", getStatusCode(), getContent());
		}
	}

	/**
	 * url 을 Get 방식으로 호출하고 결과를 리턴한다.
	 * @param url
	 * @return Result 객체
	 */
	public static Result get(String url) {
		return get(url, null);
	}

	/**
	 * url 을 Get 방식으로 호출하고 결과를 리턴한다.
	 * @param url
	 * @param headerMap
	 * @return Result 객체
	 */
	public static Result get(String url, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		HttpClient httpClient = null;
		try {
			httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					httpGet.addHeader(entry.getKey(), entry.getValue());
				}
			}
			HttpResponse response = httpClient.execute(httpGet);
			statusCode = response.getStatusLine().getStatusCode();
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				content = EntityUtils.toString(resEntity);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
		return new Result(statusCode, content);
	}

	/**
	 * url 을 Post 방식으로 호출하고 결과를 리턴한다.
	 * @param url
	 * @return Result 객체
	 */
	public static Result post(String url) {
		return post(url, null, (Map<String, String>) null);
	}

	/**
	 * url 을 Post 방식으로 호출하고 결과를 리턴한다.
	 * @param url
	 * @param paramMap
	 * @return Result 객체
	 */
	public static Result post(String url, Map<String, String> paramMap) {
		return post(url, paramMap, (Map<String, String>) null);
	}

	/**
	 * url 을 Post 방식으로 호출하고 결과를 리턴한다.
	 * @param url
	 * @param paramMap
	 * @param headerMap
	 * @return Result 객체
	 */
	public static Result post(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		HttpClient httpClient = null;
		try {
			httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (paramMap != null) {
				for (Entry<String, String> entry : paramMap.entrySet()) {
					params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(ent);
			HttpResponse response = httpClient.execute(httpPost);
			statusCode = response.getStatusLine().getStatusCode();
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				content = EntityUtils.toString(resEntity);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
		return new Result(statusCode, content);
	}

	/**
	 * url 을 Post 방식으로 호출하고 결과를 리턴한다. (첨부파일 포함)
	 * @param url
	 * @param paramMap
	 * @param fileList
	 * @return Result 객체
	 */
	public static Result post(String url, Map<String, String> paramMap, List<File> fileList) {
		return post(url, paramMap, fileList, null);
	}

	/**
	 * url 을 Post 방식으로 호출하고 결과를 리턴한다. (첨부파일 포함)
	 * @param url
	 * @param paramMap
	 * @param fileList
	 * @param headerMap
	 * @return Result 객체
	 */
	public static Result post(String url, Map<String, String> paramMap, List<File> fileList, Map<String, String> headerMap) {
		int statusCode = 0;
		String content = "";
		HttpClient httpClient = null;
		try {
			httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			if (paramMap != null) {
				for (Entry<String, String> entry : paramMap.entrySet()) {
					reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue()));
				}
			}
			if (fileList != null) {
				for (File file : fileList) {
					ContentBody contentBody = new FileBody(file);
					reqEntity.addPart("userfile", contentBody);
				}
			}
			httpPost.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(httpPost);
			statusCode = response.getStatusLine().getStatusCode();
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				content = EntityUtils.toString(resEntity);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
		return new Result(statusCode, content);
	}
}