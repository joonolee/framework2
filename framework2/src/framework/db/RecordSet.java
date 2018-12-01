/**
 * @(#)RecordSet.java
 */
package framework.db;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 데이타베이스 쿼리를 수행한 후 그 결과에 대한 접근 기반을 제공하는 클래스이다.
 */
public class RecordSet implements Iterable<Map<String, Object>>, Serializable {
	private static final long serialVersionUID = -1248669129395067939L;
	/**
	 * DB의 columns 이름
	 */
	private String[] _colNms = null;
	private int[] _colSize = null;
	private int[] _colSizeReal = null;
	private int[] _colScale = null;
	private String[] _colInfo = null;
	private int[] _columnsType = null;
	//Rows의 값
	private List<Map<String, Object>> _rows = new ArrayList<Map<String, Object>>();
	private int _currow = 0;

	RecordSet() {
	};

	/**
	 * RecordSet의 생성자
	 * @param rs ResultSet
	 * @throws SQLException SQLException
	 */
	public RecordSet(ResultSet rs) throws SQLException {
		this(rs, 0, 0);
	}

	/**
	 * 주어진 범위에 포함되는 새로운 RecordSet 객체를 생성한다
	 *
	 * @param rs 쿼리 실행결과
	 * @param curpage 현재 표시할 페이지
	 * @param pagesize 한 페이지에 표시할 데이터 갯수
	 *
	 * @throws SQLException SQLException
	 */
	public RecordSet(ResultSet rs, int curpage, int pagesize) throws SQLException {
		if (rs == null) {
			return;
		}
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		_colNms = new String[count];
		_colInfo = new String[count];
		_colSize = new int[count];
		_colSizeReal = new int[count];
		_colScale = new int[count];
		// byte[] 데이터 처리를 위해서 추가
		_columnsType = new int[count];
		for (int i = 1; i <= count; i++) {
			//Table의 Field 가 소문자 인것은 대문자로 변경처리
			_colNms[i - 1] = rsmd.getColumnName(i).toUpperCase();
			_columnsType[i - 1] = rsmd.getColumnType(i);
			//Fiels 의 정보 및 Size 추가
			_colSize[i - 1] = rsmd.getColumnDisplaySize(i);
			_colSizeReal[i - 1] = rsmd.getPrecision(i);
			_colScale[i - 1] = rsmd.getScale(i);
			_colInfo[i - 1] = rsmd.getColumnTypeName(i);
		}
		rs.setFetchSize(100);
		int num = 0;
		while (rs.next()) {
			// 현재 Row 저장 객체
			Map<String, Object> columns = new HashMap<String, Object>(count);
			num++;
			if (curpage != 0 && (num < (curpage - 1) * pagesize + 1)) {
				continue;
			}
			if (pagesize != 0 && (num > curpage * pagesize)) {
				break;
			}
			for (int i = 1; i <= count; i++) {
				Object value = rs.getObject(_colNms[i - 1]);
				if (value instanceof Number) {
					columns.put(_colNms[i - 1], rs.getObject(_colNms[i - 1]));
				} else {
					columns.put(_colNms[i - 1], rs.getString(_colNms[i - 1]));
				}
			}
			_rows.add(columns);
		}
		if (rs != null)
			rs.close();
	}

	/**
	 * 주어진 쿼리를 수행 후 컬럼명을 String[] 로 반환
	 *
	 * @return String[]
	 */
	public String[] getColumns() {
		return _colNms;
	}

	/**
	 * 주어진 쿼리를 수행 후 컬럼의 Size을 int[] 로 반환
	 *
	 * @return String[]
	 */
	public int[] getColumnsSize() {
		return _colSize;
	}

	/**
	 * 주어진 쿼리를 수행 후 컬럼의 실제 Size(숫자속성에 사용)을 int[] 로 반환
	 *
	 * @return String[]
	 */
	public int[] getColumnsSizeReal() {
		return _colSizeReal;
	}

	/**
	 * 주어진 쿼리를 수행 후 컬럼의 소숫점 아래 사이즈를 int[] 로 반환
	 *
	 * @return String[]
	 */
	public int[] getColumnsScale() {
		return _colScale;
	}

	/**
	 * 주어진 쿼리를 수행 후 컬럼의 성격을  String[] 로 반환
	 *
	 * @return String[]
	 */
	public String[] getColumnsInfo() {
		return _colInfo;
	}

	/**
	 * 주어진 쿼리를 수행 후 컬럼의 타입을 int[] 로 반환
	 * @return String[]
	 */
	public int[] getColumnsType() {
		return _columnsType;
	}

	/**
	 * 주어진 쿼리를 수행 후 결과를  ArrayList 로 반환
	 *
	 * @return ArrayList
	 */
	public List<Map<String, Object>> getRows() {
		return _rows;
	}

	/**
	 * 주어진 쿼리 수행 후 결과 column의 갯수를 구한다
	 *
	 * @return	int 컬럼의 갯수
	 */
	public int getColumnCount() {
		if (_colNms == null) {
			return 0;
		}
		return _colNms.length;
	}

	/**
	 * 주어진 쿼리 수행 후 결과 row의 갯수를 구한다
	 *
	 * @return	int Row의 갯수
	 */
	public int getRowCount() {
		if (_rows == null) {
			return 0;
		}
		return _rows.size();
	}

	/**
	 * 현재 참조하고 있는 row의 위치를 구한다.
	 *
	 * @return	int 현재 Row의 위치
	 */
	public int getCurrentRow() {
		return _currow;
	}

	/**
	 * 쿼리 수행에 의해 얻어진 결과의 특정 column의 이름을 얻는다
	 *
	 * @param	index	얻고자 하는 컬럼 위치, 첫번째 컬럼은 1
	 *
	 * @return	String 해당 column의 이름
	 */
	public String getColumnLabel(int index) throws IllegalArgumentException, NullPointerException {
		if (index < 1) {
			throw new IllegalArgumentException("index 0 is not vaild ");
		}
		if (_colNms == null) {
			throw new NullPointerException("is not find");
		}
		return _colNms[index - 1];
	}

	/**
	 * RecordSet의 처음으로 이동한다.
	 *
	 * @return boolean
	 */
	public boolean firstRow() {
		return moveRow(0);
	}

	/**
	 * RecordSet의 처음row인지 아닌지 여부 판단.
	 *
	 * @return boolean
	 */
	public boolean isFirst() {
		return (_currow == 0);
	}

	/**
	 * RecordSet의 마지막row인지 아닌지 여부 판단.
	 *
	 * @return boolean
	 */
	public boolean isLast() {
		return (_currow == _rows.size() && _rows.size() != 0);
	}

	/**
	 * RecordSet의 마지막으로 이동한다.
	 *
	 * @return boolean
	 */
	public boolean lastRow() {
		if (_rows == null || _rows.size() == 0) {
			return false;
		}
		_currow = _rows.size();
		return true;
	}

	/**
	 * RecordSet에서 현재 row의 다음 row로 이동한다.
	 *
	 * @return boolean
	 */
	public boolean nextRow() {
		_currow++;
		if (_currow == 0 || _rows == null || _rows.size() == 0 || _currow > _rows.size()) {
			return false;
		}
		return true;
	}

	/**
	 * RecordSet의 현재 row의 이전 row로 이동한다.
	 *
	 * @return boolean
	 */
	public boolean preRow() {
		_currow--;
		if (_currow == 0 || _rows == null || _rows.size() == 0 || _currow > _rows.size()) {
			return false;
		}
		return true;
	}

	/**
	 * 해당하는 하는 row로 이동
	 * @param row row number, 첫번째 row는 1
	 * @return 이동성공 여부
	 */
	public boolean moveRow(int row) {
		if (_rows != null && _rows.size() != 0 && row <= _rows.size()) {
			_currow = row;
			return true;
		}
		return false;
	}

	/**
	 * Recordset 데이타를 얻어온다.
	 *
	 * @param row cnt : start 1
	 * @param column name
	 * @return column data
	 */
	public Object get(int row, String column) {
		return _rows.get(row - 1).get(column.toUpperCase());
	}

	/**
	 * RecordSet의 column 값을 String으로 반환하는 메소드
	 *
	 * @param row  row number, 첫번째 row는 1
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public String getString(int row, String column) {
		if (get(row, column) == null) {
			return "";
		}
		return get(row, column).toString().trim();
	}

	/**
	 * RecordSet의 column 값을 int로 반환하는 메소드
	 *
	 * @param row  row number, 첫번째 row는 1
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public int getInt(int row, String column) {
		return getBigDecimal(row, column).intValue();
	}

	/**
	 * RecordSet의 column 값을 int로 반환하는 메소드
	 *
	 * @param row  row number, 첫번째 row는 1
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public int getInteger(int row, String column) {
		return getBigDecimal(row, column).intValue();
	}

	/**
	 * RecordSet의 column 값을 long 형으로 반환하는 메소드
	 *
	 * @param row  row number, 첫번째 row는 1
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public long getLong(int row, String column) {
		return getBigDecimal(row, column).longValue();
	}

	/**
	 * RecordSet의 Column 값을 double 로 반환하는 메소드
	 *
	 * @param row  row number, 첫번째 row는 1
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public double getDouble(int row, String column) {
		return getBigDecimal(row, column).doubleValue();
	}

	/**
	 * RecordSet의 Column 값을 BigDecimal 로 반환하는 메소드
	 *
	 * @param row  row number, 첫번째 row는 1
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public BigDecimal getBigDecimal(int row, String column) {
		if (get(row, column) == null) {
			return BigDecimal.valueOf(0);
		}
		return new BigDecimal(get(row, column).toString());
	}

	/**
	 * RecordSet의 Column 값을 BigDecimal 로 반환하는 메소드
	 *
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public BigDecimal getBigDecimal(String column) {
		return getBigDecimal(_currow, column);
	}

	/**
	 * RecordSet의 column 값을 float로 반환하는 메소드
	 *
	 * @param row  row number, 첫번째 row는 1
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public float getFloat(int row, String column) {
		return getBigDecimal(row, column).floatValue();
	}

	/**
	 * RecordSet의 column 값을 Date형으로 반환하는 메소드
	 * YYYY-MM-DD 로 반환
	 *
	 * @param row  row number, 첫번째 row는 1
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public Date getDate(int row, String column) {
		return Date.valueOf(getString(row, column).substring(0, 10));
	}

	/**
	 * RecordSet의 column 값을 Timestamp형으로 반환하는 메소드
	 * YYYY-MM-DD 로 반환
	 *
	 * @param row  row number, 첫번째 row는 1
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public Timestamp getTimestamp(int row, String column) {
		if ((String) get(row, column) == null) {
			return null;
		} else {
			return Timestamp.valueOf(get(row, column).toString());
		}
	}

	/**
	 * 현재 pointing 된 row의 column 데이터를 읽는다
	 *
	 * @param	column	column number, 첫번째 column 은 1
	 *
	 * @return column data
	 */
	public Object get(int column) {
		return get(_currow, _colNms[column]);
	}

	/**
	 * 현재행의 RecordSet의 int 값을 반환하는 메소드
	 *
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public int getInt(int column) {
		return getInt(_currow, _colNms[column]);
	}

	/**
	 * 현재행의 RecordSet의 int 값을 반환하는 메소드
	 *
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public int getInteger(int column) {
		return getInteger(_currow, _colNms[column]);
	}

	/**
	 * 현재 행의 RecordSet의 long 값을 반환하는 메소드
	 *
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public long getLong(int column) {
		return getLong(_currow, _colNms[column]);
	}

	/**
	 * 현재 행의 RecordSet의 float 값을 반환하는 메소드
	 *
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public float getFloat(int column) {
		return getFloat(_currow, _colNms[column]);
	}

	/**
	 * 현재 행의 RecordSet의 double 값을 반환하는 메소드
	 *
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public double getDouble(int column) {
		return getDouble(_currow, _colNms[column]);
	}

	/**
	 * 현재 행의 RecordSet의 Date 값을 반환하는 메소드
	 * YYYY-MM-DD 로 반환
	 *
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public Date getDate(int column) {
		return getDate(_currow, _colNms[column]);
	}

	/**
	 * 현재 형의 RecordSet의 Timestamp 값을 반환하는 메소드
	 *
	 * @param column  column number, 첫번째 column은 1
	 *
	 * @return column data
	 */
	public Timestamp getTimestamp(int column) {
		return getTimestamp(_currow, _colNms[column]);
	}

	/**
	 * 인자로 전해진 이름을 가지는 현재 pointing된 row의 column 데이터를 구한다
	 *
	 * @param	name	읽고자 하는 column 이름
	 *
	 * @return	column data
	 */
	public Object get(String name) {
		return get(_currow, name);
	}

	/**
	 * 인자로 전해진 이름을 가지는 현재 pointing된 row의 int형 column 데이터를 구한다
	 *
	 * @param name 읽고자 하는 column 이름
	 *
	 * @return column data
	 */
	public int getInt(String name) {
		return getInt(_currow, name);
	}

	/**
	 * 인자로 전해진 이름을 가지는 현재 pointing된 row의 int형 column 데이터를 구한다
	 *
	 * @param name 읽고자 하는 column 이름
	 *
	 * @return column data
	 */
	public Integer getInteger(String name) {
		Integer returnValue = null;
		returnValue = Integer.valueOf(getInt(_currow, name));
		return returnValue;
	}

	/**
	 * 인자로 전해진 이름을 가지는 현재 pointing된 row의 long형 column 데이터를 구한다
	 *
	 * @param name 읽고자 하는 column 이름
	 *
	 * @return column data
	 */
	public long getLong(String name) {
		return getLong(_currow, name);
	}

	/**
	 * 인자로 전해진 이름을 가지는 현재 pointing된 row의 String형 column 데이터를 구한다
	 *
	 * @param name 읽고자 하는 column 이름
	 *
	 * @return column data
	 */
	public String getString(String name) {
		return getString(_currow, name);
	}

	/**
	 * 인자로 전해진 이름을 가지는 현재 pointing된 row의 float형 column 데이터를 구한다
	 *
	 * @param name 읽고자 하는 column 이름
	 *
	 * @return column data
	 */
	public float getFloat(String name) {
		return getFloat(_currow, name);
	}

	/**
	 * 인자로 전해진 이름을 가지는 현재 pointing된 row의 double형 column 데이터를 구한다
	 *
	 * @param name 읽고자 하는 column 이름
	 *
	 * @return column data
	 */
	public double getDouble(String name) {
		return getDouble(_currow, name);
	}

	/**
	 * 인자로 전해진 이름을 가지는 현재 pointing된 row의 Date형 column 데이터를 구한다
	 * YYYY-MM-DD로 반환
	 *
	 * @param name 읽고자 하는 column 이름
	 *
	 * @return column data
	 */
	public Date getDate(String name) {
		return getDate(_currow, name);
	}

	/**
	 * 인자로 전해진 이름을 가지는 현재 pointing된 row의 Date형 column 데이터를 구한다
	 * YYYY-MM-DD로 반환
	 *
	 * @param name 읽고자 하는 column 이름
	 *
	 * @return column data
	 */
	public Timestamp getTimestamp(String name) {
		return getTimestamp(_currow, name);
	}

	/**
	 * 인자로 전해진 이름을 가지는 column의 위치를 구한다.
	 *
	 * @param	name 	column 이름
	 *
	 * @return column index, 찾지 못하면 -1
	 * @throws ColumnNotFoundException ColumnNotFoundException
	 */
	public int findColumn(String name) throws ColumnNotFoundException {
		if (name == null || _colNms == null) {
			throw new ColumnNotFoundException("name or column_keys is null ");
		}
		int count = _colNms.length;
		for (int i = 0; i < count; i++) {
			if (name.equals(_colNms[i])) {
				return i + 1;
			}
		}
		throw new ColumnNotFoundException("name : " + name + " is not found ");
	}

	/**
	 * 레코드 수가 0 인지 check
	 *
	 * @return boolean True if there are no records in this object, false otherwise
	 */
	public boolean isEmpty() {
		if (_rows.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 이터레이터를 반환한다.
	 */
	@Override
	public Iterator<Map<String, Object>> iterator() {
		return getRows().iterator();
	}
}