/* 
 * @(#)ImageUtil.java
 */
package framework.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import nl.captcha.Captcha;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;

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
	 * 
	 * @param srcPath 소스 이미지 경로
	 * @param destPath 대상 이미지 경로
	 * @param width 리사이즈할 가로 사이즈
	 * @param height 리사이즈할 세로 사이즈
	 * 
	 * @throws IOException
	 */
	public static void create(String srcPath, String destPath, int width, int height) throws IOException {
		resize(srcPath, destPath, width, height);
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 width, height 중 크기가 큰 쪽을 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * 
	 * @param srcFile 소스 이미지 파일
	 * @param destFile 대상 이미지 파일
	 * @param width 리사이즈할 가로 사이즈
	 * @param height 리사이즈할 세로 사이즈
	 * 
	 * @throws IOException
	 */
	public static void create(File srcFile, File destFile, int width, int height) throws IOException {
		resize(srcFile, destFile, width, height);
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 width, height 중 크기가 큰 쪽을 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * 
	 * @param srcPath 소스 이미지 경로
	 * @param destPath 대상 이미지 경로
	 * @param width 리사이즈할 가로 사이즈
	 * @param height 리사이즈할 세로 사이즈
	 * 
	 * @throws IOException
	 */
	public static void resize(String srcPath, String destPath, int width, int height) throws IOException {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);
		resize(srcFile, destFile, width, height);
	}

	/**
	 * 이미지를 리사이즈 한다. 
	 * 소스 이미지 파일의 width, height 중 크기가 큰 쪽을 기준으로 하여 비율을 유지한채 이미지를 생성한다.
	 * 
	 * @param srcFile 소스 이미지 파일
	 * @param destFile 대상 이미지 파일
	 * @param width 리사이즈할 가로 사이즈
	 * @param height 리사이즈할 세로 사이즈
	 * 
	 * @throws IOException
	 */
	public static void resize(File srcFile, File destFile, int width, int height) throws IOException {
		Image image = new ImageIcon(srcFile.getAbsolutePath()).getImage();
		if (image.getWidth(null) < 1 || image.getHeight(null) < 1) {
			throw new IllegalArgumentException("파일이 존재하지 않습니다.");
		}
		double scale = getScale(width, height, image.getWidth(null), image.getHeight(null));
		int scaleWidth = (int) (scale * image.getWidth(null));
		int scaleHeight = (int) (scale * image.getHeight(null));
		BufferedImage bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = bufImg.createGraphics();
		AffineTransform ax = new AffineTransform();
		ax.setToScale(1, 1);
		g2d.drawImage(image, ax, null);
		Image resizedImg = bufImg.getScaledInstance(scaleWidth, scaleHeight, BufferedImage.SCALE_SMOOTH);
		writePNG(resizedImg, destFile);
	}

	/**
	 * 이미지를 PNG 형식으로 저장한다.
	 * 
	 * @param image 저장할 이미지 객체
	 * @param destPath 대상 이미지 경로
	 * 
	 * @throws IOException
	 */
	public static void writePNG(Image image, String destPath) throws IOException {
		File destFile = new File(destPath);
		writePNG(image, destFile);
	}

	/**
	 * 이미지를 PNG 형식으로 저장한다.
	 * 
	 * @param image 저장할 이미지 객체
	 * @param destFile 대상 이미지 파일
	 * 
	 * @throws IOException
	 */
	public static void writePNG(Image image, File destFile) throws IOException {
		BufferedImage bufImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = bufImg.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		ImageIO.write(bufImg, "png", destFile);
	}

	/**
	 * CAPTCHA 이미지를 응답객체로 전송하고, 생성된 문자열을 리턴한다.
	 * 기본사이즈는 가로 200px, 세로 50px으로 한다.
	 * 
	 * @param response captcha 이미지를 전송할 응답객체
	 * @return 생성된 문자열
	 */
	public static String captcha(HttpServletResponse response) {
		return captcha(response, 200, 50);
	}

	/**
	 * CAPTCHA 이미지를 응답객체로 전송하고, 생성된 문자열을 리턴한다.
	 * 
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

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드

	/**
	 * 원본 이미지 사이즈와 리사이즈할 사이즈로 이미지 스케일 비율을 구한다. 
	 * 크기가 큰 폭을 기준으로 동일 비율로 한다.
	 * 
	 * @param resizeWidth 리사이즈할 가로 사이즈
	 * @param resizeHeight 리사이즈할 세로 사이즈
	 * @param imageWidth 원본 이미지의 가로 사이즈
	 * @param imageHeight 원본 이미지의 세로 사이즈
	 * 
	 * @return 스케일 바율
	 */
	private static double getScale(int resizeWidth, int resizeHeight, int imageWidth, int imageHeight) {
		double widthScale = (double) resizeWidth / imageWidth;
		double heightScale = (double) resizeHeight / (double) imageHeight;

		if (widthScale > heightScale) {
			return heightScale;
		} else {
			return widthScale;
		}
	}
}
