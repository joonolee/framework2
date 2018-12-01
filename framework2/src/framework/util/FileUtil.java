/*
 * @(#)FileUtil.java
 */
package framework.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * 파일처리, 업로드, 다운로드시 이용할 수 있는 유틸리티 클래스이다.
 */
public class FileUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private FileUtil() {
	}

	/**
	 * 인자로 전달된 경로에 해당하는 디렉토리의 크기를 byte 단위로 반환하는 메소드
	 *
	 * @param directoryPath 디렉토리 경로
	 * @return 디렉토리의 byte 단위의 크기
	 */
	public static long getDirSizeToByteUnit(String directoryPath) {
		return getDirSizeToByteUnit(new File(directoryPath));
	}

	/**
	 * 인자로 전달된 디렉토리의 크기를 byte 단위로 반환하는 메소드
	 *
	 * @param directory 디렉토리 파일객체
	 * @return 디렉토리의 byte 단위의 크기
	 */
	public static long getDirSizeToByteUnit(File directory) {
		long totalSum = 0;
		if (directory != null && directory.isDirectory()) {
			File[] fileItems = directory.listFiles();
			for (File item : fileItems) {
				if (item.isFile()) {
					totalSum += item.length();
				} else {
					totalSum += FileUtil.getDirSizeToByteUnit(item);
				}
			}
		}
		return totalSum;
	}

	/**
	 * 인자로 전달된 파일의 확장자를 반환하는 메소드
	 *
	 * @param file 확장자를 알고자 원하는 파일명
	 * @return 확장자명
	 */
	public static String getFileExtension(File file) {
		return FileUtil.getFileExtension(file.toString());
	}

	/**
	 * 인자로 전달된 파일명의 확장자를 반환하는 메소드
	 *
	 * @param filePath 확장자를 알고자 원하는 파일명
	 * @return 확장자명
	 */
	public static String getFileExtension(String filePath) {
		return filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
	}

	/**
	 * 인자로 전달된 파일경로에서 파일명만 추출(경로는 제거)하는 메소드
	 *
	 * @param filePath 파일경로
	 * @return 경로가 제거된 파일명
	 */
	public static String getFileName(String filePath) {
		filePath = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
		filePath = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
		return filePath;
	}

	/**
	 * 인자로 전달된 파일객체에서 파일명만 추출(경로는 제거)하는 메소드
	 *
	 * @param file 파일
	 * @return 경로가 제거된 파일명
	 */
	public static String getFileName(File file) {
		return getFileName(file.getPath());
	}

	/**
	 * 파일을 복사하는 메소드
	 *
	 * @param src 원본 파일 객체
	 * @param dest 대상 파일 객체
	 * @throws IOException IOException
	 */
	public static void copyFile(java.io.File src, java.io.File dest) throws IOException {
		java.io.InputStream in = new FileInputStream(src);

		try {
			java.io.OutputStream out = new FileOutputStream(dest);
			try {
				copy(in, out);
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}

	/**
	 * 입력 stream 데이터를 출력 stream 으로 복사하는 메소드
	 *
	 * @param in 입력스트림
	 * @param out 출력스트림
	 * @throws IOException IOException
	 */
	public static void copy(InputStream in, OutputStream out) throws IOException {
		int availcnt = 1024;
		byte[] buffer = new byte[availcnt];
		int read;

		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
	}

	/**
	 * 파일 삭제 메소드
	 *
	 * @param fileName 파일 경로
	 * @return 성공여부
	 */
	public static boolean deleteFile(String fileName) {
		return deleteFile(new File(fileName));
	}

	/**
	 * 파일 삭제 메소드
	 *
	 * @param file 파일 객체
	 * @return 성공여부
	 */
	public static boolean deleteFile(File file) {
		return file.canWrite() ? file.delete() : false;
	}

	/**
	 * 디렉토리 삭제 메소드
	 *
	 * @param directoryPath 디렉토리 경로
	 * @return 성공여부
	 */
	public static boolean deleteDirectory(String directoryPath) {
		return deleteDirectory(new File(directoryPath));
	}

	/**
	 * 디렉토리 삭제 메소드
	 *
	 * @param directory 디렉토리 객체
	 * @return 성공여부
	 */
	public static boolean deleteDirectory(File directory) {
		if (directory != null && directory.isDirectory() && directory.exists()) {
			for (File item : directory.listFiles()) {
				if (!item.delete())
					return false;
			}
			return directory.delete();
		} else {
			return false;
		}
	}

	/**
	 * 이미지 데이터를 stream 으로 전달하는 메소드
	 *
	 * @param response 응답객체
	 * @param file 파일
	 */
	public static void displayImage(HttpServletResponse response, File file) {
		if (file != null && file.isFile() && file.length() != 0) {
			long fileLen = file.length();
			response.setContentLength((int) fileLen);
			response.setContentType("image/pjpeg");
			response.setHeader("Content-Disposition", "inline; filename=\"\"");
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			_download(response, file);
		}
	}

	/**
	 * 비디오 데이터를 stream 으로 전달하는 메소드
	 *
	 * @param response 응답객체
	 * @param file 파일
	 */
	public static void displayVideo(HttpServletResponse response, File file) {
		if (file != null && file.isFile() && file.length() != 0) {
			long fileLen = file.length();
			response.setContentLength((int) fileLen);
			response.setContentType("video/x-ms-wmv");
			response.setHeader("Content-Disposition", "inline; filename=\"\"");
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			_download(response, file);
		}
	}

	/**
	 * 파일을 stream 으로 전달하는 메소드
	 *
	 * @param response 응답객체
	 * @param displayName 파일명
	 * @param file 파일
	 */
	public static void download(HttpServletResponse response, String displayName, File file) {
		if (file != null && file.isFile() && file.length() != 0) {
			long fileLen = file.length();
			response.setContentLength((int) fileLen);
			response.setContentType("application/octet-stream;");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + displayName + "\"");
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			_download(response, file);
		}
	}

	private static void _download(HttpServletResponse response, File file) {
		BufferedInputStream bufferin = null;
		BufferedOutputStream stream = null;
		try {
			int readBytes = 0;
			int available = 1024;
			byte b[] = new byte[available];
			bufferin = new BufferedInputStream(new FileInputStream(file));
			stream = new BufferedOutputStream(response.getOutputStream());
			while ((readBytes = bufferin.read(b, 0, available)) != -1) {
				stream.write(b, 0, readBytes);
			}
		} catch (IOException e) {
		} catch (IllegalStateException e) {
		} catch (Exception e) {
		} finally {
			try {
				bufferin.close();
				stream.close();
			} catch (Exception e) {
			}
		}
	}
}