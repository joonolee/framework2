/** 
 * @(#)GauceUtil.java
 */
package framework.util;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gauce.GauceDataColumn;
import com.gauce.GauceDataRow;
import com.gauce.GauceDataSet;
import com.gauce.GauceException;
import com.gauce.http.HttpGauceRequest;
import com.gauce.http.HttpGauceResponse;
import com.gauce.io.GauceInputStream;
import com.gauce.io.GauceOutputStream;

import framework.action.Box;
import framework.db.ColumnNotFoundException;
import framework.db.RecordSet;

/**
 * 가우스를 이용하여 개발할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class GauceUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private GauceUtil() {
	}

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet 이용
	/**
	 * RecordSet을 가우스 데이타셋으로 변환하여 응답객체로 전송한다. GauceUtil.setRecordSet과 동일
	 * <br>
	 * ex) rs를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.render(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, rs);
	}

	/**
	 * RecordSet을 가우스 데이타셋으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.setRecordSet(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, "", rs);
	}

	/**
	 * RecordSet을 가우스 데이타셋(명칭은 datasetName 인자 값)으로 변환하여 응답객체로 전송한다. GauceUtil.setRecordSet과 동일
	 * <br>
	 * ex) rs를 가우스 데이터셋(명칭은 result)으로 변환하여 response로 전송하는 경우 => GauceUtil.render(response, "result", rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetName 데이타셋 이름
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, String datasetName, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, datasetName, rs);
	}

	/**
	 * RecordSet을 가우스 데이타셋(명칭은 datasetName 인자 값)으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs를 가우스 데이터셋(명칭은 result)으로 변환하여 response로 전송하는 경우 => GauceUtil.setRecordSet(response, "result", rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetName 데이타셋 이름
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, String datasetName, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, new String[] { datasetName }, new RecordSet[] { rs });
	}

	/**
	 * RecordSet을 가우스 데이타셋(명칭은 datasetNameArray 인자 값)으로 변환하여 응답객체로 전송한다. GauceUtil.setRecordSet과 동일
	 * <br>
	 * ex) rs1과 rs2를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.render(response, new String[] { "result1", "result2" }, new RecordSet[] { rs1, rs2 })
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetNameArray 데이타셋 이름 배열
	 * @param rsArray 가우스 데이타셋으로 변환할 RecordSet 객체 배열
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, String[] datasetNameArray, RecordSet[] rsArray) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, datasetNameArray, rsArray);
	}

	/**
	 * RecordSet을 가우스 데이타셋(명칭은 datasetNameArray 인자 값)으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs1과 rs2를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.setRecordSet(response, new String[] { "result1", "result2" }, new RecordSet[] { rs1, rs2 })
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetNameArray 데이타셋 이름 배열
	 * @param rsArray 가우스 데이타셋으로 변환할 RecordSet 객체 배열
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, String[] datasetNameArray, RecordSet[] rsArray) throws ColumnNotFoundException, IOException {
		if (datasetNameArray.length != rsArray.length)
			throw new IllegalArgumentException("DataSet이름 갯수와 RecordSet갯수가 일치하지 않습니다.");
		int rowCount = 0;
		GauceOutputStream gos = getGOS(response);
		for (int i = 0, len = rsArray.length; i < len; i++) {
			GauceDataSet dSet = new GauceDataSet(datasetNameArray[i]);
			gos.fragment(dSet);
			rowCount += appendDataSet(dSet, rsArray[i]);
			gos.write(dSet);
		}
		return rowCount;
	}

	/**
	 * RecordSet을 인자로 넘어온 가우스 데이타셋으로 변환하여 응답객체로 전송한다. GauceUtil.setRecordSet과 동일
	 * <br>
	 * ex) rs를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.render(response, dSet, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param dSet 데이타셋
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, GauceDataSet dSet, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, dSet, rs);
	}

	/**
	 * RecordSet을 인자로 넘어온 가우스 데이타셋으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.setRecordSet(response, dSet, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param dSet 데이타셋
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, GauceDataSet dSet, RecordSet rs) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, new GauceDataSet[] { dSet }, new RecordSet[] { rs });
	}

	/**
	 * RecordSet을 인자로 넘어온 가우스 데이타셋으로 변환하여 응답객체로 전송한다. GauceUtil.setRecordSet과 동일
	 * <br>
	 * ex) rs1과 rs2를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.render(response, new GauceDataSet[] { dSet1, dSet2 }, new RecordSet[] { rs1, rs2 })
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param dSetArray 데이타셋 배열
	 * @param rsArray 가우스 데이타셋으로 변환할 RecordSet 객체 배열
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int render(HttpServletResponse response, GauceDataSet[] dSetArray, RecordSet[] rsArray) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, dSetArray, rsArray);
	}

	/**
	 * RecordSet을 인자로 넘어온 가우스 데이타셋으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs1과 rs2를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.setRecordSet(response, new GauceDataSet[] { dSet1, dSet2 }, new RecordSet[] { rs1, rs2 })
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param dSetArray 데이타셋 배열
	 * @param rsArray 가우스 데이타셋으로 변환할 RecordSet 객체 배열
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 * @throws IOException 
	 */
	public static int setRecordSet(HttpServletResponse response, GauceDataSet[] dSetArray, RecordSet[] rsArray) throws ColumnNotFoundException, IOException {
		if (dSetArray.length != rsArray.length)
			throw new IllegalArgumentException("DataSet 갯수와 RecordSet갯수가 일치하지 않습니다.");
		int rowCount = 0;
		GauceOutputStream gos = getGOS(response);
		for (int i = 0, len = rsArray.length; i < len; i++) {
			GauceDataSet dSet = dSetArray[i];
			gos.fragment(dSet);
			rowCount += appendDataSet(dSet, rsArray[i]);
			gos.write(dSet);
		}
		return rowCount;
	}

	/**
	 * RecordSet을 가우스 데이타셋으로 변환한다.
	 * <br>
	 * ex) rs를 dSet이라는 가우스 데이터셋으로 변환하는 경우 => GauceUtil.appendDataSet(dSet, rs)
	 * 
	 * @param dSet 출력용 가우스 데이타셋 객체
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 */
	public static int appendDataSet(GauceDataSet dSet, RecordSet rs) throws ColumnNotFoundException {
		if (rs == null) {
			return 0;
		}
		String[] colNms = rs.getColumns();
		String[] colInfo = rs.getColumnsInfo();
		int[] colSize = rs.getColumnsSize();
		int[] colSizeReal = rs.getColumnsSizeReal();
		int[] colScale = rs.getColumnsScale();
		rs.moveRow(0); // rs의 위치를 1번째로 이동 
		int rowCount = 0;
		while (rs.nextRow()) {
			rowCount++;
			appendRow(dSet, rs, colNms, colInfo, colSize, colSizeReal, colScale);
		}
		return rowCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet 이용

	/**
	 * ResultSet을 가우스 데이타셋으로 변환하여 응답객체로 전송한다. GauceUtil.ResultSet과 동일
	 * <br>
	 * ex) rs를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.render(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws IOException 
	 * @throws SQLException
	 */
	public static int render(HttpServletResponse response, ResultSet rs) throws IOException, SQLException {
		return setResultSet(response, rs);
	}

	/**
	 * ResultSet을 가우스 데이타셋으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.ResultSet(response, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws IOException 
	 * @throws SQLException
	 */
	public static int setResultSet(HttpServletResponse response, ResultSet rs) throws IOException, SQLException {
		return setResultSet(response, "", rs);
	}

	/**
	 * ResultSet을 가우스 데이타셋(명칭은 datasetName 인자 값)으로 변환하여 응답객체로 전송한다. GauceUtil.ResultSet과 동일
	 * <br>
	 * ex) rs를 가우스 데이터셋(명칭은 result)으로 변환하여 response로 전송하는 경우 => GauceUtil.render(response, "result", rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetName 데이타셋 이름
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws IOException 
	 * @throws SQLException
	 */
	public static int render(HttpServletResponse response, String datasetName, ResultSet rs) throws IOException, SQLException {
		return setResultSet(response, datasetName, rs);
	}

	/**
	 * ResultSet을 가우스 데이타셋(명칭은 datasetName 인자 값)으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs를 가우스 데이터셋(명칭은 result)으로 변환하여 response로 전송하는 경우 => GauceUtil.ResultSet(response, "result", rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetName 데이타셋 이름
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws IOException 
	 * @throws SQLException
	 */
	public static int setResultSet(HttpServletResponse response, String datasetName, ResultSet rs) throws IOException, SQLException {
		return setResultSet(response, new String[] { datasetName }, new ResultSet[] { rs });
	}

	/**
	 * ResultSet을 가우스 데이타셋(명칭은 datasetNameArray 인자 값)으로 변환하여 응답객체로 전송한다. GauceUtil.setResultSet과 동일
	 * <br>
	 * ex) rs1과 rs2를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.render(response, new String[] { "result1", "result2" }, new ResultSet[] { rs1, rs2 })
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetNameArray 데이타셋 이름 배열
	 * @param rsArray 가우스 데이타셋으로 변환할 ResultSet 객체 배열, ResultSet 객체는 자동으로 close 된다.
	 * @return 처리건수
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static int render(HttpServletResponse response, String[] datasetNameArray, ResultSet[] rsArray) throws IOException, SQLException {
		return setResultSet(response, datasetNameArray, rsArray);
	}

	/**
	 * ResultSet을 가우스 데이타셋(명칭은 datasetNameArray 인자 값)으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs1과 rs2를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.setResultSet(response, new String[] { "result1", "result2" }, new ResultSet[] { rs1, rs2 })
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetNameArray 데이타셋 이름 배열
	 * @param rsArray 가우스 데이타셋으로 변환할 ResultSet 객체 배열, ResultSet 객체는 자동으로 close 된다.
	 * @return 처리건수
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static int setResultSet(HttpServletResponse response, String[] datasetNameArray, ResultSet[] rsArray) throws IOException, SQLException {
		if (datasetNameArray.length != rsArray.length)
			throw new IllegalArgumentException("DataSet이름 갯수와 RecordSet갯수가 일치하지 않습니다.");
		int rowCount = 0;
		GauceOutputStream gos = getGOS(response);
		for (int i = 0, len = rsArray.length; i < len; i++) {
			GauceDataSet dSet = new GauceDataSet(datasetNameArray[i]);
			gos.fragment(dSet);
			rowCount += appendDataSet(dSet, rsArray[i]);
			gos.write(dSet);
		}
		return rowCount;
	}

	/**
	 * ResultSet을 인자로 넘어온 가우스 데이타셋으로 변환하여 응답객체로 전송한다. GauceUtil.ResultSet과 동일
	 * <br>
	 * ex) rs를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.render(response, dSet, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param dSet 데이타셋
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws IOException 
	 * @throws SQLException
	 */
	public static int render(HttpServletResponse response, GauceDataSet dSet, ResultSet rs) throws IOException, SQLException {
		return setResultSet(response, dSet, rs);
	}

	/**
	 * ResultSet을 인자로 넘어온 가우스 데이타셋으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.ResultSet(response, dSet, rs)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param dSet 데이타셋
	 * @param rs 가우스 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws IOException 
	 * @throws SQLException
	 */
	public static int setResultSet(HttpServletResponse response, GauceDataSet dSet, ResultSet rs) throws IOException, SQLException {
		return setResultSet(response, new GauceDataSet[] { dSet }, new ResultSet[] { rs });
	}

	/**
	 * ResultSet을 인자로 넘어온 가우스 데이타셋으로 변환하여 응답객체로 전송한다. GauceUtil.setResultSet과 동일
	 * <br>
	 * ex) rs1과 rs2를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.render(response, new GauceDataSet[] { dSet1, dSet2 }, new ResultSet[] { rs1, rs2 })
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param dSetArray 데이타셋 이름 배열
	 * @param rsArray 가우스 데이타셋으로 변환할 ResultSet 객체 배열, ResultSet 객체는 자동으로 close 된다.
	 * @return 처리건수
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static int render(HttpServletResponse response, GauceDataSet[] dSetArray, ResultSet[] rsArray) throws IOException, SQLException {
		return setResultSet(response, dSetArray, rsArray);
	}

	/**
	 * ResultSet을 인자로 넘어온 가우스 데이타셋으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs1과 rs2를 가우스 데이터셋으로 변환하여 response로 전송하는 경우 => GauceUtil.setResultSet(response, new GauceDataSet[] { dSet1, dSet2 }, new ResultSet[] { rs1, rs2 })
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param dSetArray 데이타셋 이름 배열
	 * @param rsArray 가우스 데이타셋으로 변환할 ResultSet 객체 배열, ResultSet 객체는 자동으로 close 된다.
	 * @return 처리건수
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static int setResultSet(HttpServletResponse response, GauceDataSet[] dSetArray, ResultSet[] rsArray) throws IOException, SQLException {
		if (dSetArray.length != rsArray.length)
			throw new IllegalArgumentException("DataSet 갯수와 RecordSet갯수가 일치하지 않습니다.");
		int rowCount = 0;
		GauceOutputStream gos = getGOS(response);
		for (int i = 0, len = rsArray.length; i < len; i++) {
			GauceDataSet dSet = dSetArray[i];
			gos.fragment(dSet);
			rowCount += appendDataSet(dSet, rsArray[i]);
			gos.write(dSet);
		}
		return rowCount;
	}

	/**
	 * ResultSet을 가우스 데이타셋으로 변환한다.
	 * <br>
	 * ex) rs를 dSet이라는 가우스 데이터셋으로 변환하는 경우 => GauceUtil.appendDataSet(dSet, rs)
	 * 
	 * @param dSet 출력용 가우스데이타셋 객체
	 * @param rs 가우스 데이타셋으로 변환할 ResultSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException 
	 */
	public static int appendDataSet(GauceDataSet dSet, ResultSet rs) throws SQLException {
		if (rs == null) {
			return 0;
		}
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			String[] colInfo = new String[count];
			int[] colSize = new int[count];
			int[] colSizeReal = new int[count];
			int[] colScale = new int[count];
			for (int i = 1; i <= count; i++) {
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				//Fiels 의 정보 및 Size 추가
				colSize[i - 1] = rsmd.getColumnDisplaySize(i);
				colSizeReal[i - 1] = rsmd.getPrecision(i);
				colScale[i - 1] = rsmd.getScale(i);
				colInfo[i - 1] = rsmd.getColumnTypeName(i);
			}
			int rowCount = 0;
			while (rs.next()) {
				rowCount++;
				appendRow(dSet, rs, colNms, colInfo, colSize, colSizeReal, colScale);
			}
			return rowCount;
		} finally {
			Statement stmt = rs.getStatement();
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////// 유틸리티

	/**
	 * 해당 HttpServletRequest로 부터 GauceInputStream을 반환받는다
	 * <br>
	 * ex) 요청객체로 부터 가우스 입력스트림을 구하는 경우 => GauceInputStream gis = GauceUtil.getGIS(request)
	 * 
	 * @param request 클라이언트에서 요청된 Request 객체
	 * 
	 * @return 요청객체에서 구한 GauceInputStream 객체
	 * @throws IOException 
	 */
	public static GauceInputStream getGIS(HttpServletRequest request) throws IOException {
		GauceInputStream inputGis = null;
		inputGis = ((HttpGauceRequest) request).getGauceInputStream();
		return inputGis;
	}

	/**
	 * 해당 HttpServletResponse로 부터 GauceOutputStream을 반환받는다
	 * <br>
	 * ex) 응답객체로 부터 가우스 출력스트림을 구하는 경우 => GauceOutputStream gos = GauceUtil.getGOS(response)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * 
	 * @return 응답객체에서 구한 GauceOutputStream 객체
	 * @throws IOException 
	 */
	public static GauceOutputStream getGOS(HttpServletResponse response) throws IOException {
		GauceOutputStream inputGos = null;
		inputGos = ((HttpGauceResponse) response).getGauceOutputStream();
		return inputGos;
	}

	/**
	 * 세션객체가 null 인 경우 클라이언트에게 세션이 없음을 알리기 위해 예외를 설정한다.
	 * <br>
	 * ex) GauceUtil.setSessionException(getResponse())
	 * 
	 * @param response response 클라이언트로 응답할 Response 객체
	 */
	public static void setSessionException(HttpServletResponse response) {
		try {
			((HttpGauceResponse) response).addException(new GauceException("SESSION", "0000", "OUT"));
			((HttpGauceResponse) response).getGauceOutputStream().close();
		} catch (IOException e) {
		}
	}

	/**
	 * 클라이언트에게 가우스 예외를 설정한다.
	 * <br>
	 * ex) GauceUtil.setException(new GauceException("Native", "9999", e.toString()), getResponse())
	 * 
	 * @param exception 클라이언트로 응답할 GauceException 객체
	 * @param response 클라이언트로 응답할 Response 객체
	 */
	public static void setException(GauceException exception, HttpServletResponse response) {
		try {
			((HttpGauceResponse) response).addException(exception);
			((HttpGauceResponse) response).getGauceOutputStream().close();
		} catch (IOException e) {
		}
	}

	/**
	 * 해당 GauceDataSet로 부터 Box를 반환받는다
	 * <br>
	 * ex) GauceDataSet으로 부터 Box를 구하는 경우 => Box box = GauceUtil.getBox(dSet)
	 * 
	 * @param dSet Box로 변환할 GauceDataSet 객체
	 * 
	 * @return GauceDataSet에서 구한 Box 객체
	 */
	public static Box getBox(GauceDataSet dSet) {
		if (dSet.getDataRowCnt() != 1) { // row 수가 1개가 아니면 잘못된 인자
			throw new IllegalArgumentException("row 수는 1개 이어야 합니다.");
		}
		Box box = new Box("gaucebox");
		GauceDataRow dRow = dSet.getDataRow(0);
		for (GauceDataColumn column : dSet.getDataColumns()) {
			String key = column.getColName();
			box.put(key, new String[] { dRow.getColumnValue(dSet.indexOfColumn(key)).toString() });
		}
		return box;
	}

	//////////////////////////////////////////////////////////////////////////////////////// Private 메소드
	/**
	 * 가우스 데이타셋에 RecordSet 한행 추가
	 * @throws ColumnNotFoundException 
	 */
	private static void appendRow(GauceDataSet dSet, RecordSet rs, String[] colNms, String[] colInfo, int[] colSize, int[] colSizeReal, int[] colScale) throws ColumnNotFoundException {
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.get(colNms[c]);
			if (value == null) {
				dSet.put(colNms[c], "", colSize[c], GauceDataColumn.TB_NORMAL);
			} else {
				if (value instanceof Number) {
					double dblSize = colSize[c];
					if (colSizeReal[c] > 0) {
						if (colScale[c] > 0) {
							dblSize = Double.parseDouble("" + colSizeReal[c] + "." + colScale[c]);
						} else {
							dblSize = colSizeReal[c];
						}
					}
					dSet.put(colNms[c], rs.getDouble(colNms[c]), dblSize, GauceDataColumn.TB_DECIMAL);
				} else {
					dSet.put(colNms[c], (rs.getString(colNms[c])), colSize[c], GauceDataColumn.TB_NORMAL);
				}
			}
		}
		dSet.heap();
	}

	/**
	 * 가우스 데이타셋에 ResultSet 한행 추가
	 * @throws SQLException 
	 */
	private static void appendRow(GauceDataSet dSet, ResultSet rs, String[] colNms, String[] colInfo, int[] colSize, int[] colSizeReal, int[] colScale) throws SQLException {
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.getObject(colNms[c]);
			if (value == null) {
				dSet.put(colNms[c], "", colSize[c], GauceDataColumn.TB_NORMAL);
			} else {
				if (value instanceof Number) {
					double dblSize = colSize[c];
					if (colSizeReal[c] > 0) {
						if (colScale[c] > 0) {
							dblSize = Double.parseDouble("" + colSizeReal[c] + "." + colScale[c]);
						} else {
							dblSize = colSizeReal[c];
						}
					}
					dSet.put(colNms[c], rs.getDouble(colNms[c]), dblSize, GauceDataColumn.TB_DECIMAL);
				} else {
					dSet.put(colNms[c], (rs.getString(colNms[c])), colSize[c], GauceDataColumn.TB_NORMAL);
				}
			}
		}
		dSet.heap();
	}
}