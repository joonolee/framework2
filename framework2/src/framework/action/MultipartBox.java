/**
 * @(#)MultipartBox.java
 */
package framework.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import framework.config.Configuration;

/**
 * Multipart 요청객체, 쿠키객체의 값을 담는 해시테이블 객체이다.
 * Multipart 요청객체의 파라미터를 추상화 하여 MultipartBox 를 생성해 놓고 파라미터이름을 키로 해당 값을 원하는 데이타 타입으로 반환받는다.
 */
public class MultipartBox extends Box {
	private static final long serialVersionUID = -8810823011616521004L;
	private List<FileItem> _fileItems = null;

	/***
	 * MultipartBox 생성자
	 * @param name MultipartBox 객체의 이름
	 */
	public MultipartBox(String name) {
		super(name);
		this._fileItems = new ArrayList<FileItem>();
	}

	/**
	 * Multipart 요청객체의 파라미터 이름과 값을 저장한 해시테이블을 생성한다.
	 * <br>
	 * ex) Multipart Request Box 객체를 얻는 경우: MultipartBox multipartBox = MultipartBox.getMultipartBox(request)
	 *
	 * @param request HTTP 클라이언트 요청객체
	 *
	 * @return 요청MultipartBox 객체
	 */
	@SuppressWarnings("unchecked")
	public static MultipartBox getMultipartBox(HttpServletRequest request) {
		MultipartBox multipartBox = new MultipartBox("multipartbox");
		for (Object obj : request.getParameterMap().keySet()) {
			String key = (String) obj;
			multipartBox.put(key, request.getParameterValues(key));
		}
		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				try {
					factory.setSizeThreshold(getConfig().getInt("fileupload.sizeThreshold"));
				} catch (IllegalArgumentException e) {
				}
				try {
					factory.setRepository(new File(getConfig().getString("fileupload.repository")));
				} catch (IllegalArgumentException e) {
				}
				ServletFileUpload upload = new ServletFileUpload(factory);
				try {
					upload.setSizeMax(getConfig().getInt("fileupload.sizeMax"));
				} catch (IllegalArgumentException e) {
				}
				List<FileItem> items = upload.parseRequest(request);
				for (FileItem item : items) {
					if (item.isFormField()) {
						String fieldName = item.getFieldName();
						String fieldValue = item.getString(request.getCharacterEncoding());
						String[] oldValue = multipartBox.getArray(fieldName);
						if (oldValue == null) {
							multipartBox.put(fieldName, new String[] { fieldValue });
						} else {
							int size = oldValue.length;
							String[] newValue = new String[size + 1];
							for (int i = 0; i < size; i++) {
								newValue[i] = oldValue[i];
							}
							newValue[size] = fieldValue;
							multipartBox.put(fieldName, newValue);
						}
					} else {
						multipartBox.addFileItem(item);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return multipartBox;
	}

	/**
	 * 파일아이템(FileItem)의 리스트 객체를 리턴한다.
	 *
	 * @return 파일아이템 리스트 객체
	 */
	public List<FileItem> getFileItems() {
		return _fileItems;
	}

	//////////////////////////////////////////////////////////////////////////////////////////Private 메소드
	/**
	 * Multipart 파일업로드시 파일 아이템을 리스트에 추가한다.
	 *
	 * @param item 파일을 담고 있는 객체
	 * @return 성공여부
	 */
	private boolean addFileItem(FileItem item) {
		return _fileItems.add(item);
	}

	/**
	 * 설정정보를 가지고 있는 객체를 생성하여 리턴한다.
	 *
	 * @return config.properties의 설정정보를 가지고 있는 객체
	 */
	private static Configuration getConfig() {
		return Configuration.getInstance();
	}
}
