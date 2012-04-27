/** 
 * @(#)ThumbnailUtil.java
 */
package framework.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.ImageIcon;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ThumbnailUtil {

	/**
	 * <b>섬네일 이미지를 생성한다.</b> 소스 이미지 파일의 width, height 중, <b>크기가 큰 쪽을 기준으로 하여 이미지를 생성</b>한다.
	 * 
	 * @param srcFile 소스 파일 객체
	 * @param destFile 결과 파일 객체
	 * @param standardWidth 결과 파일의 가로 기준 사이즈
	 * @param standardHeight 결과 파일의 세로 기준 사이즈
	 */
	public static void create(File srcFile, File destFile, int standardWidth, int standardHeight) {
		ThumbnailUtil.create(srcFile.getAbsolutePath(), destFile.getAbsolutePath(), standardWidth, standardHeight);
	}

	/**
	 * <b>섬네일 이미지를 생성한다.</b> 소스 이미지 파일의 width, height 중, <b>크기가 큰 쪽을 기준으로 하여 이미지를 생성</b>한다.
	 * 
	 * @param srcFileName 소스파일명(경로포함)
	 * @param destFileName 결과파일명(경로포함)
	 * @param standardWidth 결과 파일의 가로 기준 사이즈
	 * @param standardHeight 결과 파일의 세로 기준 사이즈
	 */
	public static void create(String srcFileName, String destFileName, int standardWidth, int standardHeight) {
		OutputStream os = null;

		try {
			// 이미지 파일 불러옴
			Image inImage = new ImageIcon(srcFileName).getImage();
			
			// 이미지 스케일 설정 : maxWidth 기준으로 비율을 계산함(가로/세로 또는 세로/가로. 큰 사이즈의 길이값이 분모로서 계산된다)
			double scale = ThumbnailUtil.getScale(standardWidth, standardHeight, inImage.getWidth(null), inImage.getHeight(null));
			// 위에서 추출한 스케일을 기준으로 width, height를 설정함.
			int scaledW = (int) (scale * inImage.getWidth(null));
			int scaledH = (int) (scale * inImage.getHeight(null));

			// BufferedImage 생성
			BufferedImage outImage = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_RGB);

			// 스케일링
			AffineTransform tx = new AffineTransform();

			// 이미지 사이즈가 결과로 원하는 사이즈보다 작을 경우, 스케일링이 처리되지 않는다.
			if (scale < 1.0d) {
				tx.scale(scale, scale);
			}

			// 이미지를 생성한다.
			Graphics2D g2d = outImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 안티얼리어싱 적용
			g2d.drawImage(inImage, tx, null);
			g2d.dispose();

			// JPEG-encode 로 이미지를 쓴다
			os = new FileOutputStream(destFileName);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);

			// 인코더 퀄리티 설정 시작
			JPEGEncodeParam encoderParam = encoder.getDefaultJPEGEncodeParam(outImage);
			encoderParam.setQuality(1.0f, true);
			encoder.setJPEGEncodeParam(encoderParam);
			// 인코더 퀄리티 설정 종료

			encoder.encode(outImage);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("썸네일 유틸 사용중에 예외가 발생하였습니다. 예외사유 : " + e.getMessage(), e);
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static double getScale(int standardWidth, int standardHeight, int imageWidth, int imageHeight) {
		double widthScale = (double) standardWidth / imageWidth;
		double heightScale = (double) standardHeight / (double) imageHeight;
		
		if (widthScale > heightScale) {
			return heightScale;
		} else {
			return widthScale;
		}
	}
}