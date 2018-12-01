/*
 * @(#)EmailUtil.java
 */
package framework.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * JavaMail을 이용해 메일을 발송하는 유틸리티 클래스이다.
 */
public class EmailUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private EmailUtil() {
	}

	/**
	 * 기본 인코딩 값
	 */
	private static final String DEFAULT_CHARSET = "euc-kr";

	//////////////////////////////////////////////////////////////////////////////////////////SMTP서버가 인증이 필요한 경우

	/**
	 * 보내는 SMTP 서버 인증을 통하여 전자메일을 발송한다.
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailAuth("mail.xxx.co.kr", "25", "id", "password", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동");
	 *
	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param smtpUser 보내는 SMTP 서버 인증아이디
	 * @param smtpPassword 보내는 SMTP 서버 인증비밀번호
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuth(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName) throws UnsupportedEncodingException, MessagingException {
		sendMailAuth(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, DEFAULT_CHARSET, null);
	}

	/**
	 * 보내는 SMTP 서버 인증을 통하여 전자메일을 발송한다. (보안연결-SSL 이 필요할때)
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailAuthSSL("mail.xxx.co.kr", "465", "id", "password", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동");
	 *
	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param smtpUser 보내는 SMTP 서버 인증아이디
	 * @param smtpPassword 보내는 SMTP 서버 인증비밀번호
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName) throws UnsupportedEncodingException, MessagingException {
		sendMailAuthSSL(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, DEFAULT_CHARSET, null);
	}

	/**
	 * 보내는 SMTP 서버 인증을 통하여 전자메일을 발송한다.
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailAuth("mail.xxx.co.kr", "25", "id", "password", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동", "euc-kr");

	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param smtpUser 보내는 SMTP 서버 인증아이디
	 * @param smtpPassword 보내는 SMTP 서버 인증비밀번호
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 * @param charset 인코딩 캐릭터셋
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuth(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) throws UnsupportedEncodingException, MessagingException {
		sendMailAuth(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * 보내는 SMTP 서버 인증을 통하여 전자메일을 발송한다. (보안연결-SSL 이 필요할때)
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailAuthSSL("mail.xxx.co.kr", "465", "id", "password", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동", "euc-kr");

	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param smtpUser 보내는 SMTP 서버 인증아이디
	 * @param smtpPassword 보내는 SMTP 서버 인증비밀번호
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 * @param charset 인코딩 캐릭터셋
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) throws UnsupportedEncodingException, MessagingException {
		sendMailAuthSSL(smtpHost, smtpPort, smtpUser, smtpPassword, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * 보내는 SMTP 서버 인증을 통하여 전자메일을 발송한다.
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailAuth("mail.xxx.co.kr", "25", "id", "password", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동", "euc-kr", new File[] { f1, f2 });

	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param smtpUser 보내는 SMTP 서버 인증아이디
	 * @param smtpPassword 보내는 SMTP 서버 인증비밀번호
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 * @param charset 인코딩 캐릭터셋
	 * @param attachFiles 첨부파일 배열
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuth(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.user", smtpUser);
		props.put("mail.smtp.auth", true);
		MyAuthenticator auth = new MyAuthenticator(smtpUser, smtpPassword);
		Session session = Session.getDefaultInstance(props, auth);
		sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	/**
	 * 보내는 SMTP 서버 인증을 통하여 전자메일을 발송한다. (보안연결-SSL 이 필요할때)
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailAuthSSL("mail.xxx.co.kr", "465", "id", "password", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동", "euc-kr", new File[] { f1, f2 });
	 *
	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param smtpUser 보내는 SMTP 서버 인증아이디
	 * @param smtpPassword 보내는 SMTP 서버 인증비밀번호
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 * @param charset 인코딩 캐릭터셋
	 * @param attachFiles 첨부파일 배열
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailAuthSSL(String smtpHost, String smtpPort, String smtpUser, String smtpPassword, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.user", smtpUser);
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		MyAuthenticator auth = new MyAuthenticator(smtpUser, smtpPassword);
		Session session = Session.getDefaultInstance(props, auth);
		sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	//////////////////////////////////////////////////////////////////////////////////////////SMTP서버가 인증이 필요없는 경우

	/**
	 * 보내는 SMTP 서버 인증없이 전자메일을 발송한다.
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailNoAuth("mail.xxx.co.kr", "25", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동");

	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuth(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName) throws UnsupportedEncodingException, MessagingException {
		sendMailNoAuth(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, DEFAULT_CHARSET, null);
	}

	/**
	 * 보내는 SMTP 서버 인증없이 전자메일을 발송한다. (보안연결-SSL 이 필요할때)
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailNoAuthSSL("mail.xxx.co.kr", "465", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동");

	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuthSSL(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName) throws UnsupportedEncodingException, MessagingException {
		sendMailNoAuthSSL(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, DEFAULT_CHARSET, null);
	}

	/**
	 * 보내는 SMTP 서버 인증없이 전자메일을 발송한다.
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailNoAuth("mail.xxx.co.kr", "25", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동", "euc-kr");

	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 * @param charset 인코딩 캐릭터셋
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuth(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) throws UnsupportedEncodingException, MessagingException {
		sendMailNoAuth(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * 보내는 SMTP 서버 인증없이 전자메일을 발송한다. (보안연결-SSL 이 필요할때)
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailNoAuthSSL("mail.xxx.co.kr", "465", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동", "euc-kr");

	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 * @param charset 인코딩 캐릭터셋
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuthSSL(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset) throws UnsupportedEncodingException, MessagingException {
		sendMailNoAuthSSL(smtpHost, smtpPort, subject, content, toEmail, fromEmail, fromName, charset, null);
	}

	/**
	 * 보내는 SMTP 서버 인증없이 전자메일을 발송한다.
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailNoAuth("mail.xxx.co.kr", "25", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동", "euc-kr", new File[] { f1, f2 });

	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 * @param charset 인코딩 캐릭터셋
	 * @param attachFiles 첨부파일 배열
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuth(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);

		Session session = Session.getDefaultInstance(props, null);
		sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	/**
	 * 보내는 SMTP 서버 인증없이 전자메일을 발송한다. (보안연결-SSL 이 필요할때)
	 * <br>
	 * ex) receiver@xxx.co.kr 가 sender@xxx.co.kr 에게 메일을 보내는 경우: EmailUtil.sendMailNoAuthSSL("mail.xxx.co.kr", "465", "제목", "내용", "receiver@xxx.co.kr", "sender@xxx.co.kr", "홍길동", "euc-kr", new File[] { f1, f2 });

	 * @param smtpHost 보내는 SMTP 서버주소
	 * @param smtpPort 보내는 SMTP 포트
	 * @param subject 메일제목
	 * @param content 메일내용
	 * @param toEmail 받는사람 메일주소
	 * @param fromEmail 보내는사람 메일주소
	 * @param fromName 보내는사람 이름
	 * @param charset 인코딩 캐릭터셋
	 * @param attachFiles 첨부파일 배열
	 *
	 * @throws UnsupportedEncodingException UnsupportedEncodingException
	 * @throws MessagingException MessagingException
	 */
	public static void sendMailNoAuthSSL(String smtpHost, String smtpPort, String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles) throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Session session = Session.getDefaultInstance(props, null);
		sendMail(subject, content, toEmail, fromEmail, fromName, charset, attachFiles, session);
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드 및 객체

	/**
	 * 메일발송 및 첨부파일 처리
	 */
	private static void sendMail(String subject, String content, String toEmail, String fromEmail, String fromName, String charset, File[] attachFiles, Session session) throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = new MimeMessage(session);
		InternetAddress addr = new InternetAddress(fromEmail, fromName, charset);
		message.setFrom(addr);
		message.setSubject(subject);
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

		if (attachFiles == null) {
			message.setContent(content, "text/html; charset=" + charset);
		} else {
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();

			messageBodyPart.setText(content);
			multipart.addBodyPart(messageBodyPart);

			for (File f : attachFiles) {
				BodyPart fileBodyPart = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(f);
				fileBodyPart.setDataHandler(new DataHandler(fds));
				fileBodyPart.setFileName(MimeUtility.encodeText(f.getName(), charset, "B"));
				multipart.addBodyPart(fileBodyPart);
			}
			message.setContent(multipart);
		}
		Transport.send(message);
	}

	/**
	 * 메일인증을 위한 객체
	 */
	private static class MyAuthenticator extends Authenticator {
		private String id;
		private String pw;

		public MyAuthenticator(String id, String pw) {
			this.id = id;
			this.pw = pw;
		}

		@Override
		protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
			return new javax.mail.PasswordAuthentication(id, pw);
		}
	}
}