/** 
 * @(#)SelectConditionObject.java
 */
package framework.db;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL 문장에서 조건조회시 필요한 검색조건을 담는 객체 클래스이다.
 * 검색 조건은 PreparedStatement 실행시 바인드 되어지며 로그 출력시 문자열과 바인드 되어 출력된다.
 */
public class SelectConditionObject {
	private List<Object> _param = new ArrayList<Object>();

	/**
	 * 검색 조건으로 바인딩할 객체(Object)를 셋팅한다.
	 * 
	 * @param obj 바인딩할 객체
	 */
	public void setObject(Object obj) {
		_param.add(obj);
	}

	/**
	 * 검색 조건으로 바인딩할 int형 변수를 셋팅한다.
	 * 
	 * @param i 바인딩할 int형 변수
	 */
	public void setInt(int i) {
		setObject(Integer.valueOf(i));
	}

	/**
	 * 검색 조건으로 바인딩할 long형 변수를 셋팅한다.
	 * 
	 * @param l 바인딩할 long형 변수
	 */
	public void setLong(long l) {
		setObject(Long.valueOf(l));
	}

	/**
	 * 검색 조건으로 바인딩할 double형 변수를 셋팅한다.
	 * 
	 * @param d 바인딩할 double형 변수
	 */
	public void setDouble(double d) {
		setObject(Double.valueOf(d));
	}

	/**
	 * 검색조건으로 바인딩 할 모든 파라미터를 오브젝트 배열로 리턴한다.
	 * 
	 * @return 바인딩할 오브젝트 파라미터
	 */
	public Object[] getParameter() {
		if (_param == null) {
			return null;
		}
		return _param.toArray();
	}
}