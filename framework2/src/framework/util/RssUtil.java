/* 
 * @(#)RssUtil.java
 */
package framework.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import framework.db.RecordSet;

/**
 * RSS를 이용하여 개발할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class RssUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private RssUtil() {
	}

	private static final String BR = System.getProperty("line.separator");

	/**
	 * RssItem 객체
	 */
	public static class RssItem {
		private String _title = null;
		private String _link = null;
		private String _description = null;
		private String _author = null;
		private String _category = null;
		private Date _pubDate = null;

		public RssItem() {
		}

		public RssItem(String title, String link, String description, String author, String category, Date pubDate) {
			setTitle(title);
			setLink(link);
			setDescription(description);
			setAuthor(author);
			setCategory(category);
			setPubDate(pubDate);
		}

		public String getTitle() {
			return _title;
		}

		public String getLink() {
			return _link;
		}

		public String getDescription() {
			return _description;
		}

		public String getAuthor() {
			return _author;
		}

		public String getCategory() {
			return _category;
		}

		public Date getPubDate() {
			return _pubDate;
		}

		public void setTitle(String title) {
			_title = title;
		}

		public void setLink(String link) {
			_link = link;
		}

		public void setDescription(String description) {
			_description = description;
		}

		public void setAuthor(String author) {
			_author = author;
		}

		public void setCategory(String category) {
			_category = category;
		}

		public void setPubDate(Date pubDate) {
			_pubDate = pubDate;
		}
	}

	/**
	 * 입력한 값으로 RssItem을 생성한다.
	 * <br>
	 * ex) titie, link, description, author, category, pubDate로 RssItem객체를 생성하는 경우: RssUtil.makeRssItem(title, link, description, author, category, pubDate)
	 * 
	 * @param title 제목
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함)
	 * @param description 설명
	 * @param author 작성자(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함)
	 * @param category 분류
	 * @param pubDate 작성일
	 * @return RssItem 객체
	 */
	public static RssItem makeRssItem(String title, String link, String description, String author, String category, Date pubDate) {
		return new RssItem(title, link, description, author, category, pubDate);
	}

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet 이용

	/**
	 * RecordSet을 RSS 2.0 형식으로 출력한다. RecordSet에는 다음컬럼이 반드시 포함되어야 한다.(title, link, description, author, category, pubDate). RssUtil.setRecordSet과 동일
	 * <br>
	 * ex) response로 rs를 RSS 형식으로 출력하는 경우: RssUtil.render(response, rs, "utf-8", "제목", "http://www.xxx.com", "설명", "admin@xxx.com")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RSS 형식으로 변환할 RecordSet 객체
	 * @param encoding 헤더에 포함될 인코딩
	 * @param title 제목 : 필수
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함) : 필수
	 * @param description 설명 : 필수
	 * @param webMaster 웹마스터 e-mail 주소(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함) : 옵션
	 * @return 처리건수
	 * @throws IOException IOException
	 */
	public static int render(HttpServletResponse response, RecordSet rs, String encoding, String title, String link, String description, String webMaster) throws IOException {
		return setRecordSet(response, rs, encoding, title, link, description, webMaster);
	}

	/**
	 * RecordSet을 RSS 2.0 형식으로 출력한다. RecordSet에는 다음컬럼이 반드시 포함되어야 한다.(title, link, description, author, category, pubDate).
	 * <br>
	 * ex) response로 rs를 RSS 형식으로 출력하는 경우: RssUtil.setRecordSet(response, rs, "utf-8", "제목", "http://www.xxx.com", "설명", "admin@xxx.com")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RSS 형식으로 변환할 RecordSet 객체
	 * @param encoding 헤더에 포함될 인코딩
	 * @param title 제목 : 필수
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함) : 필수
	 * @param description 설명 : 필수
	 * @param webMaster 웹마스터 e-mail 주소(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함) : 옵션
	 * @return 처리건수
	 * @throws IOException IOException
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs, String encoding, String title, String link, String description, String webMaster) throws IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		rs.moveRow(0);
		pw.println(xmlHeaderStr(encoding));
		pw.println("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">");
		pw.println("  <channel>");
		pw.println("    <title>" + "<![CDATA[" + title + "]]>" + "</title>");
		pw.println("    <link>" + link + "</link>");
		pw.println("    <description>" + "<![CDATA[" + description + "]]>" + "</description>");
		pw.println("    <language>ko</language>");
		pw.println("    <atom:link href=\"" + link + "\" rel=\"self\" type=\"application/rss+xml\"/>");
		pw.println("    <pubDate>" + toRfc822DateFormat(new Date()) + "</pubDate>");
		if (webMaster != null && !"".equals(webMaster)) {
			pw.println("    <webMaster>" + webMaster + "</webMaster>");
		}
		int rowCount = 0;
		while (rs.nextRow()) {
			rowCount++;
			pw.println(rssItemStr(rs));
		}
		pw.println("  </channel>");
		pw.println("</rss>");
		return rowCount;
	}

	/**
	 * RecordSet을 RSS 2.0 형식으로 변환한다. RecordSet에는 다음컬럼이 반드시 포함되어야 한다.(title, link, description, author, category, pubDate). RssUtil.format과 동일
	 * <br>
	 * ex) rs를 RSS 형식으로 변환하는 경우: String rss = RssUtil.render(rs, "utf-8", "제목", "http://www.xxx.com", "설명", "admin@xxx.com")
	 *
	 * @param rs RSS 형식으로 변환할 RecordSet 객체
	 * @param encoding 헤더에 포함될 인코딩
	 * @param title 제목 : 필수
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함) : 필수
	 * @param description 설명 : 필수
	 * @param webMaster 웹마스터 e-mail 주소(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함) : 옵션
	 * @return RSS 형식으로 변환된 문자열
	 */
	public static String render(RecordSet rs, String encoding, String title, String link, String description, String webMaster) {
		return format(rs, encoding, title, link, description, webMaster);
	}

	/**
	 * RecordSet을 RSS 2.0 형식으로 변환한다. RecordSet에는 다음컬럼이 반드시 포함되어야 한다.(title, link, description, author, category, pubDate).
	 * <br>
	 * ex) rs를 RSS 형식으로 변환하는 경우: String rss = RssUtil.format(rs, "utf-8", "제목", "http://www.xxx.com", "설명", "admin@xxx.com")
	 *
	 * @param rs RSS 형식으로 변환할 RecordSet 객체
	 * @param encoding 헤더에 포함될 인코딩
	 * @param title 제목 : 필수
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함) : 필수
	 * @param description 설명 : 필수
	 * @param webMaster 웹마스터 e-mail 주소(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함) : 옵션
	 * @return RSS 형식으로 변환된 문자열
	 */
	public static String format(RecordSet rs, String encoding, String title, String link, String description, String webMaster) {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		rs.moveRow(0);
		buffer.append(xmlHeaderStr(encoding) + BR);
		buffer.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">" + BR);
		buffer.append("  <channel>" + BR);
		buffer.append("    <title>" + "<![CDATA[" + title + "]]>" + "</title>" + BR);
		buffer.append("    <link>" + link + "</link>" + BR);
		buffer.append("    <description>" + "<![CDATA[" + description + "]]>" + "</description>" + BR);
		buffer.append("    <language>ko</language>" + BR);
		buffer.append("    <atom:link href=\"" + link + "\" rel=\"self\" type=\"application/rss+xml\"/>" + BR);
		buffer.append("    <pubDate>" + toRfc822DateFormat(new Date()) + "</pubDate>" + BR);
		if (webMaster != null && !"".equals(webMaster)) {
			buffer.append("    <webMaster>" + webMaster + "</webMaster>" + BR);
		}
		while (rs.nextRow()) {
			buffer.append(rssItemStr(rs) + BR);
		}
		buffer.append("  </channel>" + BR);
		buffer.append("</rss>" + BR);
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet 이용

	/**
	 * ResultSet을 RSS 2.0 형식으로 출력한다. ResultSet에는 다음컬럼이 반드시 포함되어야 한다.(title, link, description, author, category, pubDate). RssUtil.setResultSet과 동일
	 * <br>
	 * ex) response로 rs를 RSS 형식으로 출력하는 경우: RssUtil.render(response, rs, "utf-8", "제목", "http://www.xxx.com", "설명", "admin@xxx.com")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RSS 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param encoding 헤더에 포함될 인코딩
	 * @param title 제목 : 필수
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함) : 필수
	 * @param description 설명 : 필수
	 * @param webMaster 웹마스터 e-mail 주소(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함) : 옵션
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int render(HttpServletResponse response, ResultSet rs, String encoding, String title, String link, String description, String webMaster) throws SQLException, IOException {
		return setResultSet(response, rs, encoding, title, link, description, webMaster);
	}

	/**
	 * ResultSet을 RSS 2.0 형식으로 출력한다. ResultSet에는 다음컬럼이 반드시 포함되어야 한다.(title, link, description, author, category, pubDate).
	 * <br>
	 * ex) response로 rs를 RSS 형식으로 출력하는 경우: RssUtil.setResultSet(response, rs, "utf-8", "제목", "http://www.xxx.com", "설명", "admin@xxx.com")
	 *
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs RSS 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param encoding 헤더에 포함될 인코딩
	 * @param title 제목 : 필수
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함) : 필수
	 * @param description 설명 : 필수
	 * @param webMaster 웹마스터 e-mail 주소(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함) : 옵션
	 * @return 처리건수
	 * @throws SQLException SQLException
	 * @throws IOException IOException
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs, String encoding, String title, String link, String description, String webMaster) throws SQLException, IOException {
		if (rs == null) {
			return 0;
		}
		PrintWriter pw = response.getWriter();
		try {
			pw.println(xmlHeaderStr(encoding));
			pw.println("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">");
			pw.println("  <channel>");
			pw.println("    <title>" + "<![CDATA[" + title + "]]>" + "</title>");
			pw.println("    <link>" + link + "</link>");
			pw.println("    <description>" + "<![CDATA[" + description + "]]>" + "</description>");
			pw.println("    <language>ko</language>");
			pw.println("    <atom:link href=\"" + link + "\" rel=\"self\" type=\"application/rss+xml\"/>");
			pw.println("    <pubDate>" + toRfc822DateFormat(new Date()) + "</pubDate>");
			if (webMaster != null && !"".equals(webMaster)) {
				pw.println("    <webMaster>" + webMaster + "</webMaster>");
			}
			int rowCount = 0;
			while (rs.next()) {
				rowCount++;
				pw.println(rssItemStr(rs));
			}
			pw.println("  </channel>");
			pw.println("</rss>");
			return rowCount;
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * ResultSet을 RSS 2.0 형식으로 변환한다. ResultSet에는 다음컬럼이 반드시 포함되어야 한다.(title, link, description, author, category, pubDate). RssUtil.format과 동일
	 * <br>
	 * ex) rs를 RSS 형식으로 변환하는 경우: String rss = RssUtil.render(rs, "utf-8", "제목", "http://www.xxx.com", "설명", "admin@xxx.com")
	 *
	 * @param rs RSS 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param encoding 헤더에 포함될 인코딩
	 * @param title 제목 : 필수
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함) : 필수
	 * @param description 설명 : 필수
	 * @param webMaster 웹마스터 e-mail 주소(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함) : 옵션
	 * @throws SQLException SQLException
	 * @return RSS 2.0 형식 문자열
	 */
	public static String render(ResultSet rs, String encoding, String title, String link, String description, String webMaster) throws SQLException {
		return format(rs, encoding, title, link, description, webMaster);
	}

	/**
	 * ResultSet을 RSS 2.0 형식으로 변환한다. ResultSet에는 다음컬럼이 반드시 포함되어야 한다.(title, link, description, author, category, pubDate).
	 * <br>
	 * ex) rs를 RSS 형식으로 변환하는 경우: String rss = RssUtil.format(rs, "utf-8", "제목", "http://www.xxx.com", "설명", "admin@xxx.com")
	 *
	 * @param rs RSS 형식으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param encoding 헤더에 포함될 인코딩
	 * @param title 제목 : 필수
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함) : 필수
	 * @param description 설명 : 필수
	 * @param webMaster 웹마스터 e-mail 주소(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함) : 옵션
	 * @throws SQLException SQLException
	 * @return RSS 2.0 형식 문자열
	 */
	public static String format(ResultSet rs, String encoding, String title, String link, String description, String webMaster) throws SQLException {
		if (rs == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		try {
			buffer.append(xmlHeaderStr(encoding) + BR);
			buffer.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">" + BR);
			buffer.append("  <channel>" + BR);
			buffer.append("    <title>" + "<![CDATA[" + title + "]]>" + "</title>" + BR);
			buffer.append("    <link>" + link + "</link>" + BR);
			buffer.append("    <description>" + "<![CDATA[" + description + "]]>" + "</description>" + BR);
			buffer.append("    <language>ko</language>" + BR);
			buffer.append("    <atom:link href=\"" + link + "\" rel=\"self\" type=\"application/rss+xml\"/>" + BR);
			buffer.append("    <pubDate>" + toRfc822DateFormat(new Date()) + "</pubDate>" + BR);
			if (webMaster != null && !"".equals(webMaster)) {
				buffer.append("    <webMaster>" + webMaster + "</webMaster>" + BR);
			}
			while (rs.next()) {
				buffer.append(rssItemStr(rs) + BR);
			}
			buffer.append("  </channel>" + BR);
			buffer.append("</rss>" + BR);
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// 기타 Collection 이용

	/**
	 * List객체를 RSS 2.0 형태로 변환한다. RssUtil.format과 동일
	 * <br>
	 * ex) rssItemList를 RSS 로 변환하는 경우 : String rss = RssUtil.render(rssItemList, "utf-8", "제목", "http://www.xxx.com", "설명", "admin@xxx.com")
	 *
	 * @param rssItemList 변환할 List객체
	 * @param encoding 헤더에 포함될 인코딩
	 * @param title 제목 : 필수
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함) : 필수
	 * @param description 설명 : 필수
	 * @param webMaster 웹마스터 e-mail 주소(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함) : 옵션
	 * @return RSS 형식으로 변환된 문자열
	 */
	public static String render(List<RssItem> rssItemList, String encoding, String title, String link, String description, String webMaster) {
		return format(rssItemList, encoding, title, link, description, webMaster);
	}

	/**
	 * List객체를 RSS 2.0 형태로 변환한다.
	 * <br>
	 * ex) rssItemList를 RSS 로 변환하는 경우 : String rss = RssUtil.format(rssItemList, "utf-8", "제목", "http://www.xxx.com", "설명", "admin@xxx.com")
	 *
	 * @param rssItemList 변환할 List객체
	 * @param encoding 헤더에 포함될 인코딩
	 * @param title 제목 : 필수
	 * @param link 링크(validator를 통과하기 위해서는 url에 앰퍼센드등은 엔터티표기를 사용하여야 함) : 필수
	 * @param description 설명 : 필수
	 * @param webMaster 웹마스터 e-mail 주소(validator를 통과하기 위해서는 "이메일주소(이름)" 형식으로 표기하여야 함) : 옵션
	 * @return RSS 형식으로 변환된 문자열
	 */
	public static String format(List<RssItem> rssItemList, String encoding, String title, String link, String description, String webMaster) {
		if (rssItemList == null) {
			return null;
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append(xmlHeaderStr(encoding) + BR);
		buffer.append("<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">" + BR);
		buffer.append("  <channel>" + BR);
		buffer.append("    <title>" + "<![CDATA[" + title + "]]>" + "</title>" + BR);
		buffer.append("    <link>" + link + "</link>" + BR);
		buffer.append("    <description>" + "<![CDATA[" + description + "]]>" + "</description>" + BR);
		buffer.append("    <language>ko</language>" + BR);
		buffer.append("    <atom:link href=\"" + link + "\" rel=\"self\" type=\"application/rss+xml\"/>" + BR);
		buffer.append("    <pubDate>" + toRfc822DateFormat(new Date()) + "</pubDate>" + BR);
		if (webMaster != null && !"".equals(webMaster)) {
			buffer.append("    <webMaster>" + webMaster + "</webMaster>" + BR);
		}
		for (RssItem rssItem : rssItemList) {
			buffer.append(rssItemStr(rssItem) + BR);
		}
		buffer.append("  </channel>" + BR);
		buffer.append("</rss>" + BR);
		return buffer.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////// Private 메소드

	/**
	 *  xml 헤더 문자열 생성
	 */
	private static String xmlHeaderStr(String encoding) {
		return "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>";
	}

	/**
	 * rss item 문자열 생성
	 */
	private static String rssItemStr(RssItem item) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("    "); // 들여쓰기용
		buffer.append("<item>");
		if (item.getTitle() != null && !"".equals(item.getTitle()))
			buffer.append("<title>" + "<![CDATA[" + item.getTitle() + "]]>" + "</title>");
		if (item.getLink() != null && !"".equals(item.getLink()))
			buffer.append("<link>" + item.getLink() + "</link>");
		if (item.getDescription() != null && !"".equals(item.getDescription()))
			buffer.append("<description>" + "<![CDATA[" + item.getDescription().replaceAll(BR, "") + "]]>" + "</description>");
		if (item.getAuthor() != null && !"".equals(item.getAuthor()))
			buffer.append("<author>" + item.getAuthor() + "</author>");
		if (item.getCategory() != null && !"".equals(item.getCategory()))
			buffer.append("<category>" + "<![CDATA[" + item.getCategory() + "]]>" + "</category>");
		if (item.getLink() != null && !"".equals(item.getLink()))
			buffer.append("<guid>" + item.getLink() + "</guid>");
		if (item.getPubDate() != null)
			buffer.append("<pubDate>" + toRfc822DateFormat(item.getPubDate()) + "</pubDate>");
		buffer.append("</item>");
		return buffer.toString();
	}

	/**
	 * rss item 문자열 생성
	 */
	private static String rssItemStr(RecordSet rs) {
		String title = null;
		String link = null;
		String description = null;
		String author = null;
		String category = null;
		Date pubDate = null;
		title = rs.getString("TITLE");
		link = rs.getString("LINK");
		description = rs.getString("DESCRIPTION");
		author = rs.getString("AUTHOR");
		category = rs.getString("CATEGORY");
		pubDate = rs.getTimestamp("PUBDATE");
		return rssItemStr(makeRssItem(title, link, description, author, category, pubDate));
	}

	/**
	 * rss item 문자열 생성
	 */
	private static String rssItemStr(ResultSet rs) {
		String title = null;
		String link = null;
		String description = null;
		String author = null;
		String category = null;
		Date pubDate = null;
		try {
			title = rs.getString("TITLE");
		} catch (SQLException e) {
		}
		try {
			link = rs.getString("LINK");
		} catch (SQLException e) {
		}
		try {
			description = rs.getString("DESCRIPTION");
		} catch (SQLException e) {
		}
		try {
			author = rs.getString("AUTHOR");
		} catch (SQLException e) {
		}
		try {
			category = rs.getString("CATEGORY");
		} catch (SQLException e) {
		}
		try {
			pubDate = rs.getTimestamp("PUBDATE");
		} catch (SQLException e) {
		}
		return rssItemStr(makeRssItem(title, link, description, author, category, pubDate));
	}

	/**
	 * 날짜를 Rfc822 날짜형식으로 변환
	 * @param date 변환할 날짜
	 * @return Rfc822 형식의 날짜 문자열
	 */
	private static String toRfc822DateFormat(Date date) {
		SimpleDateFormat rfc822DateFormat = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
		return rfc822DateFormat.format(date);
	}
}
