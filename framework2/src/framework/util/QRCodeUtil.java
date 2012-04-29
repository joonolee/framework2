/** 
 * @(#)QRCodeUtil.java
 */
package framework.util;

import java.io.File;
import java.io.FileOutputStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * QR Code 이미지를 생성할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class QRCodeUtil {
	
	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private QRCodeUtil() {
	}
	
	/**
	 * QRCode 이미지를 생성한다.
	 * @param url					: QRCode 스캔 시 이동할 곳의 URL
	 * @param target_folderpath	: QRCode 저장 폴더 경로
	 * @param target_filename		: QRCode 파일명
	 * @param width				: QRCode 이미지 가로 길이
	 */
	public static void create(String url, String target_folderpath, String target_filename, int width) {
		File l_target_folder = new File(target_folderpath);
		if (!l_target_folder.exists()) {
			l_target_folder.mkdirs();
		}
		QRCodeUtil.create(url, new File(l_target_folder.getAbsolutePath(), target_filename), width);
	}

	/**
	 * QRCode 이미지를 생성한다.
	 * @param url			: QRCode 스캔 시 이동할 곳의 URL
	 * @param target_file	: QRCode 이미지 파일 객체
	 * @param width		: QRCode 이미지 가로 길이
	 */
	public static void create(String url, File target_file, int width) {
		QRCodeUtil.create(url, target_file, width, width);
	}

	/**
	 * QRCode 이미지를 생성한다.
	 * @param url			: QRCode 스캔 시 이동할 곳의 URL
	 * @param target_file	: QRCode 이미지 파일 객체
	 * @param width		: QRCode 이미지 가로 길이
	 * @param height		: QRCode 이미지 세로 길이
	 */
	public static void create(String url, File target_file, int width, int height) {
		QRCodeWriter l_qr_writer = new QRCodeWriter();
		try {
			String l_url = new String(url.getBytes("UTF-8"), "ISO-8859-1");
			BitMatrix l_bit_matrix = l_qr_writer.encode(l_url, BarcodeFormat.QR_CODE, width, height);
			MatrixToImageWriter.writeToStream(l_bit_matrix, "png", new FileOutputStream(target_file));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("QRCode 유틸 사용중에 예외가 발생하였습니다. 예외사유 : " + e.getMessage(), e);
		}
	}
}
