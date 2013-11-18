/* 
 * @(#)ImageUtil.java
 */
package framework.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import nl.captcha.Captcha;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * 이미지 포맷 변경, 크기 변경시 이용할 수 있는 유틸리티 클래스이다.
 */
public class ImageUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private ImageUtil() {
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 width, height 중 크기가 큰 쪽을 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * @param srcPath 소스 이미지 경로
	 * @param destPath 대상 이미지 경로
	 * @param width 리사이즈할 가로 사이즈
	 * @param height 리사이즈할 세로 사이즈
	 */
	public static void resize(String srcPath, String destPath, int width, int height) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		resize(srcFile, destFile, width, height);
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 width, height 중 크기가 큰 쪽을 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * @param srcFile 소스 이미지 파일
	 * @param destFile 대상 이미지 파일
	 * @param width 리사이즈할 가로 사이즈
	 * @param height 리사이즈할 세로 사이즈
	 */
	public static void resize(File srcFile, File destFile, int width, int height) {
		Image image = new ImageIcon(srcFile.getAbsolutePath()).getImage();
		if (image.getWidth(null) < 1 || image.getHeight(null) < 1) {
			throw new IllegalArgumentException("파일이 존재하지 않습니다.");
		}
		double scale = _getScale(width, height, image.getWidth(null), image.getHeight(null));
		int scaleWidth = (int) (scale * image.getWidth(null));
		int scaleHeight = (int) (scale * image.getHeight(null));
		BufferedImage bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = bufImg.createGraphics();
		AffineTransform ax = new AffineTransform();
		ax.setToScale(1, 1);
		g2d.drawImage(image, ax, null);
		Image resizedImg = bufImg.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);
		_writePNG(resizedImg, destFile);
	}

	/**
	 * CAPTCHA 이미지를 응답객체로 전송하고, 생성된 문자열을 리턴한다.
	 * 기본사이즈는 가로 200px, 세로 50px으로 한다.
	 * @param response captcha 이미지를 전송할 응답객체
	 * @return 생성된 문자열
	 */
	public static String captcha(HttpServletResponse response) {
		return captcha(response, 200, 50);
	}

	/**
	 * CAPTCHA 이미지를 응답객체로 전송하고, 생성된 문자열을 리턴한다.
	 * @param response captcha 이미지를 전송할 응답객체
	 * @param width 가로 사이즈 픽셀
	 * @param height 세로 사이즈 픽셀
	 * @return 생성된 문자열
	 */
	public static String captcha(HttpServletResponse response, int width, int height) {
		response.reset();
		Captcha captcha = new Captcha.Builder(width, height).addText().addBackground().gimp(new RippleGimpyRenderer()).build();
		CaptchaServletUtil.writeImage(response, captcha.getImage());
		return captcha.getAnswer();
	}

	/**
	 * QRCode 이미지를 생성한다.
	 * @param url QRCode 스캔 시 이동할 곳의 URL
	 * @param destPath QRCode 파일명
	 * @param width QRCode 이미지 가로 길이
	 */
	public static void qrcode(String url, String destPath, int width) {
		qrcode(url, new File(destPath), width);
	}

	/**
	 * QRCode 이미지를 생성한다.
	 * @param url QRCode 스캔 시 이동할 곳의 URL
	 * @param destFile QRCode 이미지 파일 객체
	 * @param width QRCode 이미지 길이
	 */
	public static void qrcode(String url, File destFile, int width) {
		try {
			qrcode(url, new FileOutputStream(destFile), width);
		} catch (FileNotFoundException e) {
			new RuntimeException(e);
		}
	}

	/**
	 * QRCode 이미지를 생성한다.
	 * @param url QRCode 스캔 시 이동할 곳의 URL
	 * @param response qrcode 이미지를 전송할 응답객체
	 * @param width QRCode 이미지 길이
	 */
	public static void qrcode(String url, HttpServletResponse response, int width) {
		try {
			response.reset();
			response.setContentType("image/png");
			qrcode(url, response.getOutputStream(), width);
		} catch (Exception e) {
			new RuntimeException(e);
		}
	}

	/**
	 * QRCode 이미지를 생성한다.
	 * @param url QRCode 스캔 시 이동할 곳의 URL
	 * @param os 출력 스트림
	 * @param width QRCode 이미지 길이
	 */
	public static void qrcode(String url, OutputStream os, int width) {
		QRCodeWriter l_qr_writer = new QRCodeWriter();
		try {
			String l_url = new String(url.getBytes("UTF-8"), "ISO-8859-1");
			BitMatrix l_bit_matrix = l_qr_writer.encode(l_url, BarcodeFormat.QR_CODE, width, width);
			MatrixToImageWriter.writeToStream(l_bit_matrix, "png", os);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드

	/**
	 * 원본 이미지 사이즈와 리사이즈할 사이즈로 이미지 스케일 비율을 구한다. 
	 * 크기가 큰 폭을 기준으로 동일 비율로 한다.
	 * @param resizeWidth 리사이즈할 가로 사이즈
	 * @param resizeHeight 리사이즈할 세로 사이즈
	 * @param imageWidth 원본 이미지의 가로 사이즈
	 * @param imageHeight 원본 이미지의 세로 사이즈
	 * @return 스케일 바율
	 */
	private static double _getScale(int resizeWidth, int resizeHeight, int imageWidth, int imageHeight) {
		double widthScale = (double) resizeWidth / imageWidth;
		double heightScale = (double) resizeHeight / (double) imageHeight;
		if (widthScale > heightScale) {
			return heightScale;
		} else {
			return widthScale;
		}
	}

	/**
	 * 이미지를 PNG 형식으로 저장한다.
	 * @param image 저장할 이미지 객체
	 * @param destFile 대상 이미지 파일
	 */
	private static void _writePNG(Image image, File destFile) {
		BufferedImage bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = bufImg.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		try {
			ImageIO.write(bufImg, "png", destFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
