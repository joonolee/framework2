/** 
 * @(#)MiPlatformUtil.java
 */
package framework.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tobesoft.platform.PlatformRequest;
import com.tobesoft.platform.PlatformResponse;
import com.tobesoft.platform.data.ColumnInfo;
import com.tobesoft.platform.data.Dataset;
import com.tobesoft.platform.data.DatasetList;
import com.tobesoft.platform.data.VariableList;

import framework.action.Box;
import framework.db.ColumnNotFoundException;
import framework.db.RecordSet;

/**
 * 마이플랫폼을 이용하여 개발할 때 이용할 수 있는 유틸리티 클래스이다.
 */
public class MiPlatformUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private MiPlatformUtil() {
	}

	/**
	 * 미압축 방식의 바이너리 송수신 형식
	 */
	public static int BIN = PlatformRequest.BIN;

	/**
	 * 미압축 방식의 XML 송수신 형식
	 */
	public static int XML = PlatformRequest.XML;

	/**
	 * Zlib 압축 방식의 바이너리 송수신 형식
	 */
	public static int ZLIB_COMP = PlatformRequest.ZLIB_COMP;

	////////////////////////////////////////////////////////////////////////////////////////// RecordSet 이용

	/**
	 * RecordSet을 마이플랫폼 데이타셋(명칭은 datasetName 인자 값)으로 변환하여 응답객체로 전송한다. MiPlatformUtil.setRecordSet과 동일
	 * <br>
	 * ex) rs를 마이플랫폼 데이터셋(명칭은 result)으로 변환하여 response로 XML 형식으로 전송하는 경우: MiPlatformUtil.render(response, "result", rs, MiPlatformUtil.XML)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetName 데이타셋 이름
	 * @param rs 마이플랫폼 데이타셋으로 변환할 RecordSet 객체
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int render(HttpServletResponse response, String datasetName, RecordSet rs, int dataFormat) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, datasetName, rs, dataFormat);
	}

	/**
	 * RecordSet을 마이플랫폼 데이타셋(명칭은 datasetName 인자 값)으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs를 마이플랫폼 데이터셋(명칭은 result)으로 변환하여 response로 XML 형식으로 전송하는 경우: MiPlatformUtil.setRecordSet(response, "result", rs, MiPlatformUtil.XML)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetName 데이타셋 이름
	 * @param rs 마이플랫폼 데이타셋으로 변환할 RecordSet 객체
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSet(HttpServletResponse response, String datasetName, RecordSet rs, int dataFormat) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, new String[] { datasetName }, new RecordSet[] { rs }, dataFormat);
	}

	/**
	 * RecordSet을 마이플랫폼 데이타셋(명칭은 datasetNameArray 인자 값)으로 변환하여 응답객체로 전송한다. MiPlatformUtil.setRecordSet과 동일
	 * <br>
	 * ex) rs1과 rs2를 마이플랫폼 데이터셋으로 변환하여 response로 XML 형식으로 전송하는 경우: MiPlatformUtil.render(response, new String[] { "result1", "result2" }, new RecordSet[] { rs1, rs2 }, MiPlatformUtil.XML)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetNameArray 데이타셋 이름 배열
	 * @param rsArray 마이플랫폼 데이타셋으로 변환할 RecordSet 객체 배열
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int render(HttpServletResponse response, String[] datasetNameArray, RecordSet[] rsArray, int dataFormat) throws ColumnNotFoundException, IOException {
		return setRecordSet(response, datasetNameArray, rsArray, dataFormat);
	}

	/**
	 * RecordSet을 마이플랫폼 데이타셋(명칭은 datasetNameArray 인자 값)으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs1과 rs2를 마이플랫폼 데이터셋으로 변환하여 response로 XML 형식으로 전송하는 경우: MiPlatformUtil.setRecordSet(response, new String[] { "result1", "result2" }, new RecordSet[] { rs1, rs2 }, MiPlatformUtil.XML)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetNameArray 데이타셋 이름 배열
	 * @param rsArray 마이플랫폼 데이타셋으로 변환할 RecordSet 객체 배열
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 * @throws IOException IOException
	 */
	public static int setRecordSet(HttpServletResponse response, String[] datasetNameArray, RecordSet[] rsArray, int dataFormat) throws ColumnNotFoundException, IOException {
		if (datasetNameArray.length != rsArray.length)
			throw new IllegalArgumentException("Dataset이름 갯수와 RecordSet갯수가 일치하지 않습니다.");
		int rowCount = 0;
		VariableList vl = new VariableList();
		DatasetList dl = new DatasetList();
		try {
			for (int i = 0, len = rsArray.length; i < len; i++) {
				Dataset dSet = new Dataset(datasetNameArray[i], "euc-kr", false, false);
				rowCount += appendDataset(dSet, rsArray[i]);
				dl.addDataset(dSet);
			}
			vl.addStr("ErrorCode", "0");
			vl.addStr("ErrorMsg", "SUCC");
		} catch (ColumnNotFoundException e) {
			vl.addStr("ErrorCode", "-1");
			vl.addStr("ErrorMsg", e.getMessage());
			throw e;
		} finally {
			sendData(response, vl, dl, dataFormat);
		}
		return rowCount;
	}

	/**
	 * RecordSet을 마이플랫폼 데이타셋으로 변환한다.
	 * <br>
	 * ex) rs를 dSet이라는 마이플랫폼 데이터셋으로 변환하는 경우: MiPlatformUtil.appendDataset(dSet, rs)
	 * 
	 * @param dSet 출력용 마이플랫폼 데이타셋 객체
	 * @param rs 마이플랫폼 데이타셋으로 변환할 RecordSet 객체
	 * @return 처리건수
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public static int appendDataset(Dataset dSet, RecordSet rs) throws ColumnNotFoundException {
		if (rs == null) {
			return 0;
		}
		String[] colNms = rs.getColumns();
		int[] colSize = rs.getColumnsSize();
		int[] colType = rs.getColumnsType();
		// 컬럼 레이아웃 셋팅
		for (int c = 0; c < colNms.length; c++) {
			switch (colType[c]) {
			case Types.BIGINT:
			case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.INTEGER:
			case Types.NUMERIC:
			case Types.REAL:
			case Types.SMALLINT:
			case Types.TINYINT:
				dSet.addColumn(colNms[c].toLowerCase(), ColumnInfo.COLUMN_TYPE_DECIMAL, colSize[c]);
				break;
			default:
				dSet.addColumn(colNms[c].toLowerCase(), ColumnInfo.COLUMN_TYPE_STRING, colSize[c]);
				break;
			}
		}
		rs.moveRow(0); // rs의 위치를 1번째로 이동 
		int rowCount = 0;
		while (rs.nextRow()) {
			rowCount++;
			appendRow(dSet, rs, colNms);
		}
		return rowCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////// ResultSet 이용

	/**
	 * ResultSet을 마이플랫폼 데이타셋(명칭은 datasetName 인자 값)으로 변환하여 응답객체로 전송한다. MiPlatformUtil.setResultSet과 동일
	 * <br>
	 * ex) rs를 마이플랫폼 데이터셋(명칭은 result)으로 변환하여 response로 XML 형식으로 전송하는 경우: MiPlatformUtil.render(response, "result", rs, MiPlatformUtil.XML)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetName 데이타셋 이름
	 * @param rs 마이플랫폼 데이타셋으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return 처리건수
	 * @throws IOException IOException
	 * @throws SQLException SQLException
	 */
	public static int render(HttpServletResponse response, String datasetName, ResultSet rs, int dataFormat) throws IOException, SQLException {
		return setResultSet(response, datasetName, rs, dataFormat);
	}

	/**
	 * ResultSet을 마이플랫폼 데이타셋(명칭은 datasetName 인자 값)으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs를 마이플랫폼 데이터셋(명칭은 result)으로 변환하여 response로 XML 형식으로 전송하는 경우: MiPlatformUtil.setResultSet(response, "result", rs, MiPlatformUtil.XML)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetName 데이타셋 이름
	 * @param rs 마이플랫폼 데이타셋으로 변환할 ResultSet 객체, ResultSet 객체는 자동으로 close 된다.
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return 처리건수
	 * @throws IOException IOException
	 * @throws SQLException SQLException
	 */
	public static int setResultSet(HttpServletResponse response, String datasetName, ResultSet rs, int dataFormat) throws IOException, SQLException {
		return setResultSet(response, new String[] { datasetName }, new ResultSet[] { rs }, dataFormat);
	}

	/**
	 * ResultSet을 마이플랫폼 데이타셋(명칭은 datasetNameArray 인자 값)으로 변환하여 응답객체로 전송한다. MiPlatformUtil.setResultSet과 동일
	 * <br>
	 * ex) rs1과 rs2를 마이플랫폼 데이터셋으로 변환하여 response로 XML 형식으로 전송하는 경우: MiPlatformUtil.render(response, new String[] { "result1", "result2" }, new ResultSet[] { rs1, rs2 }, MiPlatformUtil.XML)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetNameArray 데이타셋 이름 배열
	 * @param rsArray 마이플랫폼 데이타셋으로 변환할 ResultSet 객체 배열, ResultSet 객체는 자동으로 close 된다.
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return 처리건수
	 * @throws IOException IOException
	 * @throws SQLException SQLException
	 */
	public static int render(HttpServletResponse response, String[] datasetNameArray, ResultSet[] rsArray, int dataFormat) throws IOException, SQLException {
		return setResultSet(response, datasetNameArray, rsArray, dataFormat);
	}

	/**
	 * ResultSet을 마이플랫폼 데이타셋(명칭은 datasetNameArray 인자 값)으로 변환하여 응답객체로 전송한다.
	 * <br>
	 * ex) rs1과 rs2를 마이플랫폼 데이터셋으로 변환하여 response로 XML 형식으로 전송하는 경우: MiPlatformUtil.setResultSet(response, new String[] { "result1", "result2" }, new ResultSet[] { rs1, rs2 }, MiPlatformUtil.XML)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param datasetNameArray 데이타셋 이름 배열
	 * @param rsArray 마이플랫폼 데이타셋으로 변환할 ResultSet 객체 배열, ResultSet 객체는 자동으로 close 된다.
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return 처리건수
	 * @throws IOException IOException
	 * @throws SQLException SQLException
	 */
	public static int setResultSet(HttpServletResponse response, String[] datasetNameArray, ResultSet[] rsArray, int dataFormat) throws IOException, SQLException {
		if (datasetNameArray.length != rsArray.length)
			throw new IllegalArgumentException("Dataset이름 갯수와 ResultSet갯수가 일치하지 않습니다.");
		int rowCount = 0;
		VariableList vl = new VariableList();
		DatasetList dl = new DatasetList();
		try {
			for (int i = 0, len = rsArray.length; i < len; i++) {
				Dataset dSet = new Dataset(datasetNameArray[i], "euc-kr", false, false);
				rowCount += appendDataset(dSet, rsArray[i]);
				dl.addDataset(dSet);
			}
			vl.addStr("ErrorCode", "0");
			vl.addStr("ErrorMsg", "SUCC");
		} catch (SQLException e) {
			vl.addStr("ErrorCode", "-1");
			vl.addStr("ErrorMsg", e.getMessage());
			throw e;
		} finally {
			sendData(response, vl, dl, dataFormat);
		}
		return rowCount;
	}

	/**
	 * ResultSet을 마이플랫폼 데이타셋으로 변환한다.
	 * <br>
	 * ex) rs를 dSet이라는 마이플랫폼 데이터셋으로 변환하는 경우: MiPlatformUtil.appendDataset(dSet, rs)
	 * 
	 * @param dSet 출력용 마이플랫폼 데이타셋 객체
	 * @param rs 마이플랫폼 데이타셋으로 변환할 ResultSet 객체
	 * @return 처리건수
	 * @throws SQLException SQLException
	 */
	public static int appendDataset(Dataset dSet, ResultSet rs) throws SQLException {
		if (rs == null) {
			return 0;
		}
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			String[] colNms = new String[count];
			int[] colSize = new int[count];
			int[] colType = new int[count];
			for (int i = 1; i <= count; i++) {
				//Table의 Field 가 소문자 인것은 대문자로 변경처리
				colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
				//Fiels 의 정보 및 Size 추가
				colSize[i - 1] = rsmd.getColumnDisplaySize(i);
				// Field 의 타입 추가
				colType[i - 1] = rsmd.getColumnType(i);
			}
			// 컬럼 레이아웃 셋팅
			for (int c = 0; c < colNms.length; c++) {
				switch (colType[c]) {
				case Types.BIGINT:
				case Types.DECIMAL:
				case Types.DOUBLE:
				case Types.FLOAT:
				case Types.INTEGER:
				case Types.NUMERIC:
				case Types.REAL:
				case Types.SMALLINT:
				case Types.TINYINT:
					dSet.addColumn(colNms[c].toLowerCase(), ColumnInfo.COLUMN_TYPE_DECIMAL, colSize[c]);
					break;
				default:
					dSet.addColumn(colNms[c].toLowerCase(), ColumnInfo.COLUMN_TYPE_STRING, colSize[c]);
					break;
				}
			}
			int rowCount = 0;
			while (rs.next()) {
				rowCount++;
				appendRow(dSet, rs, colNms);
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
	 * 해당 HttpServletRequest로 부터 PlatformRequest 반환받는다
	 * <br>
	 * ex) 요청객체로 부터 마이플랫폼 요청객체를 구하는 경우: PlatformRequest pReq = MiPlatformUtil.getPReq(request)
	 * 
	 * @param request 클라이언트에서 요청된 Request 객체
	 * @return 요청객체에서 구한 PlatformRequest 객체
	 * @throws IOException IOException
	 */
	public static PlatformRequest getPReq(HttpServletRequest request) throws IOException {
		PlatformRequest inputPR = null;
		inputPR = new PlatformRequest(request);
		inputPR.receiveData();
		return inputPR;
	}

	/**
	 * 해당 HttpServletRequest로 부터 encoding 형식의 PlatformRequest 반환받는다
	 * <br>
	 * ex) 요청객체로 부터 utf-8 형식의 마이플랫폼 요청객체를 구하는 경우: PlatformRequest pReq = MiPlatformUtil.getPReq(request, "utf-8")
	 * 
	 * @param request 클라이언트에서 요청된 Request 객체
	 * @param encoding 인코딩할 형식
	 * @return 요청객체에서 구한 PlatformRequest 객체
	 * @throws IOException IOException
	 */
	public static PlatformRequest getPReq(HttpServletRequest request, String encoding) throws IOException {
		PlatformRequest inputPR = null;
		inputPR = new PlatformRequest(request, encoding);
		inputPR.receiveData();
		return inputPR;
	}

	/**
	 * 해당 HttpServletResponse로 부터 PlatformResponse 반환받는다
	 * <br>
	 * ex) 응답객체로 부터 XML 송수신 형식의 마이플랫폼 응답객체를 구하는 경우: PlatformResponse pRes = MiPlatformUtil.getPRes(response, MiPlatformUtil.XML)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @return 응답객체에서 구한 PlatformResponse 객체
	 * @throws IOException IOException
	 */
	public static PlatformResponse getPRes(HttpServletResponse response, int dataFormat) throws IOException {
		PlatformResponse inputPRes = null;
		inputPRes = new PlatformResponse(response, dataFormat);
		return inputPRes;
	}

	/**
	 * 해당 HttpServletResponse로 부터 encoding 형식의 PlatformResponse 반환받는다
	 * <br>
	 * ex) 응답객체로 부터 utf-8 형식의 XML 송수신 형식의 마이플랫폼 응답객체를 구하는 경우: PlatformResponse pRes = MiPlatformUtil.getPRes(response, MiPlatformUtil.XML, "utf-8")
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @param encoding 인코딩할 형식
	 * @return 응답객체에서 구한 PlatformResponse 객체
	 * @throws IOException IOException
	 */
	public static PlatformResponse getPRes(HttpServletResponse response, int dataFormat, String encoding) throws IOException {
		PlatformResponse inputPRes = null;
		inputPRes = new PlatformResponse(response, dataFormat, encoding);
		return inputPRes;
	}

	/**
	 * 해당 DataSet로 부터 Box를 반환받는다
	 * <br>
	 * ex) DataSet으로 부터 Box를 구하는 경우: Box box = MiPlatformUtil.getBox(dSet)
	 * 
	 * @param dSet Box로 변환할 DataSet 객체
	 * @return DataSet에서 구한 Box 객체
	 */
	public static Box getBox(Dataset dSet) {
		if (dSet.getRowCount() != 1) { // row 수가 1개가 아니면 잘못된 인자
			throw new IllegalArgumentException("row 수는 1개 이어야 합니다.");
		}
		Box box = new Box("miplatformbox");
		for (int i = 0, col = dSet.getColumnCount(); i < col; i++) {
			String key = dSet.getColumnId(i);
			box.put(key, new String[] { dSet.getColumn(0, i).toString() });
		}
		return box;
	}

	/**
	 * VariableList와 DatasetList를 응답객체로 전송한다.
	 * <br>
	 * ex) vl과 dl을 response로 XML 형식으로 전송하는 경우: MiPlatformUtil.sendData(response, vl, dl, MiPlatformUtil.XML)
	 * 
	 * @param response 클라이언트로 응답할 Response 객체
	 * @param vl 마이플랫폼 VariableList 객체
	 * @param dl 마이플랫폼 DatasetList 객체
	 * @param dataFormat 송수신 형식 (MiPlatformUtil.BIN, MiPlatformUtil.ZLIB_COMP, MiPlatformUtil.XML)
	 * @throws IOException IOException
	 */
	public static void sendData(HttpServletResponse response, VariableList vl, DatasetList dl, int dataFormat) throws IOException {
		PlatformResponse pResponse = getPRes(response, dataFormat);
		pResponse.sendData(vl, dl);
	}

	/** 
	 * Dataset의 값을 추출하여 String 객체를 리턴한다.
	 * 
	 * @param dSet 값을 추출할 Dataset
	 * @param row 추출할 행번호
	 * @param colName 추출할 열이름
	 * 
	 * @return 추출된 값을 담고 있는 String 객체
	 */
	public static String getString(Dataset dSet, int row, String colName) {
		String str = dSet.getColumnAsString(row, colName);
		if (str == null) {
			return "";
		}
		return str;
	}

	/** 
	 * Dataset의 값을 추출하여 Double 객체를 리턴한다.
	 * 
	 * @param dSet 값을 추출할 Dataset
	 * @param row 추출할 행번호
	 * @param colName 추출할 열이름
	 * 
	 * @return 추출된 값을 담고 있는 Double 객체
	 */
	public static Double getDouble(Dataset dSet, int row, String colName) {
		String value = getString(dSet, row, colName).trim().replaceAll(",", "");
		if (value.equals("")) {
			return Double.valueOf(0);
		}
		Double num = null;
		try {
			num = Double.valueOf(value);
		} catch (Exception e) {
			num = Double.valueOf(0);
		}
		return num;
	}

	/** 
	 * Dataset의 값을 추출하여 Long 객체를 리턴한다.
	 * 
	 * @param dSet 값을 추출할 Dataset
	 * @param row 추출할 행번호
	 * @param colName 추출할 열이름
	 * 
	 * @return 추출된 값을 담고 있는 Long 객체
	 */
	public static Long getLong(Dataset dSet, int row, String colName) {
		Double value = getDouble(dSet, row, colName);
		return Long.valueOf(value.longValue());
	}

	/** 
	 * Dataset의 값을 추출하여 Integer 객체를 리턴한다.
	 * 
	 * @param dSet 값을 추출할 Dataset
	 * @param row 추출할 행번호
	 * @param colName 추출할 열이름
	 * 
	 * @return 추출된 값을 담고 있는 Integer 객체
	 */
	public static Integer getInteger(Dataset dSet, int row, String colName) {
		Double value = getDouble(dSet, row, colName);
		return Integer.valueOf(value.intValue());
	}

	/** 
	 * Dataset의 값을 추출하여 Float 객체를 리턴한다.
	 * 
	 * @param dSet 값을 추출할 Dataset
	 * @param row 추출할 행번호
	 * @param colName 추출할 열이름
	 * 
	 * @return 추출된 값을 담고 있는 Float 객체
	 */
	public static Float getFloat(Dataset dSet, int row, String colName) {
		return new Float(getDouble(dSet, row, colName).doubleValue());
	}

	/** 
	 * Dataset의 값을 추출하여 BigDecimal 객체를 리턴한다.
	 * 
	 * @param dSet 값을 추출할 Dataset
	 * @param row 추출할 행번호
	 * @param colName 추출할 열이름
	 * 
	 * @return 추출된 값을 담고 있는 BigDecimal 객체
	 */
	public static BigDecimal getBigDecimal(Dataset dSet, int row, String colName) {
		String value = getString(dSet, row, colName).trim().replaceAll(",", "");
		if (value.equals("")) {
			return BigDecimal.valueOf(0);
		}
		try {
			return new BigDecimal(value);
		} catch (Exception e) {
			return BigDecimal.valueOf(0);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////// Private 메소드
	/**
	 * 마이플랫폼 데이타셋에 RecordSet 한행 추가
	 * @throws ColumnNotFoundException 
	 */
	private static void appendRow(Dataset dSet, RecordSet rs, String[] colNms) throws ColumnNotFoundException {
		if (rs.getRowCount() == 0)
			return;
		int row = dSet.appendRow();
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.get(colNms[c]);
			if (value == null) {
				dSet.setColumn(row, colNms[c].toLowerCase(), "");
			} else {
				if (value instanceof Number) {
					dSet.setColumn(row, colNms[c].toLowerCase(), rs.getDouble(colNms[c]));
				} else {
					dSet.setColumn(row, colNms[c].toLowerCase(), rs.getString(colNms[c]));
				}
			}
		}
	}

	/**
	 * 마이플랫폼 데이타셋에 ResultSet 한행 추가
	 * @throws SQLException 
	 */
	private static void appendRow(Dataset dSet, ResultSet rs, String[] colNms) throws SQLException {
		if (rs.getRow() == 0)
			return;
		int row = dSet.appendRow();
		for (int c = 0; c < colNms.length; c++) {
			Object value = rs.getObject(colNms[c]);
			if (value == null) {
				dSet.setColumn(row, colNms[c].toLowerCase(), "");
			} else {
				if (value instanceof Number) {
					dSet.setColumn(row, colNms[c].toLowerCase(), rs.getDouble(colNms[c]));
				} else {
					dSet.setColumn(row, colNms[c].toLowerCase(), rs.getString(colNms[c]));
				}
			}
		}
	}
}
