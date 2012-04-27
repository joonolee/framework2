/* 
 * @(#)PagingUtil.java
 */
package framework.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 네비게이션 관련 페이징 정보 추출 라이브러리
 */
public class PagingUtil {
	/**
	 * 페이징을 위해 필요한 정보를 리턴한다.
	 * 
	 * @param totcnt 전체 레코드 건수
	 * @param pagenum 현재 페이지 번호 
	 * @param pagesize 한페이지에 보여질 사이즈
	 * @param displaysize 네비게이션 페이징 사이즈
	 * @return totalpage(전체페이지수), pagenum(현재페이지), startpage(시작페이지), endpage(끝페이지)정보를 담고 있는 맵 객체
	 */
	public static Map<String, Integer> getPagingMap(Integer totcnt, Integer pagenum, Integer pagesize, Integer displaysize) {
		int l_totcnt = totcnt.intValue();
		int l_pagenum = pagenum.intValue();
		int l_pagesize = pagesize.intValue();
		int l_displaysize = displaysize.intValue();
		Map<String, Integer> resultMap = new HashMap<String, Integer>();

		int l_totalpage = l_totcnt / l_pagesize;
		if (l_totcnt % l_pagesize != 0)
			l_totalpage += 1;
		int l_startpage = (((l_pagenum - 1) / l_displaysize) * l_displaysize) + 1;
		int l_endpage = (((l_pagenum - 1) + l_displaysize) / l_displaysize) * l_displaysize;
		if (l_totalpage <= l_endpage)
			l_endpage = l_totalpage;

		resultMap.put("totalpage", Integer.valueOf(l_totalpage));
		resultMap.put("pagenum", Integer.valueOf(l_pagenum));
		resultMap.put("startpage", Integer.valueOf(l_startpage));
		resultMap.put("endpage", Integer.valueOf(l_endpage));
		resultMap.put("displaysize", Integer.valueOf(l_displaysize));

		return resultMap;
	}
}
