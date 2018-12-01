package framework.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 네비게이션 관련 페이징 정보 추출 라이브러리
 */
public class PagingUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private PagingUtil() {
	}

	/**
	 * 페이징을 위해 필요한 정보를 리턴한다.
	 * @param totcnt 전체 레코드 건수
	 * @param pagenum 현재 페이지 번호
	 * @param pagesize 한페이지에 보여질 사이즈
	 * @param displaysize 네비게이션 페이징 사이즈
	 * @return totcnt(전체 레코드 건수), pagesize(한페이지에 보여질 사이즈), totalpage(전체페이지수), pagenum(현재페이지), startpage(시작페이지), endpage(끝페이지), beforepage(이전페이지), afterpage(이후페이지) 정보를 담고 있는 맵 객체
	 */
	public static Map<String, Integer> getPagingMap(int totcnt, int pagenum, int pagesize, int displaysize) {
		int beforepage = 0;
		int afterpage = 0;
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		int totalpage = totcnt / pagesize;
		if (totcnt % pagesize != 0) {
			totalpage += 1;
		}
		int startpage = (((pagenum - 1) / displaysize) * displaysize) + 1;
		int endpage = (((pagenum - 1) + displaysize) / displaysize) * displaysize;
		if (totalpage <= endpage) {
			endpage = totalpage;
		}
		if ((startpage - displaysize) > 0) {
			beforepage = ((((pagenum - displaysize) - 1) / displaysize) * displaysize) + 1;
		}
		if ((startpage + displaysize) <= totalpage) {
			afterpage = ((((pagenum + displaysize) - 1) / displaysize) * displaysize) + 1;
		}
		resultMap.put("totcnt", Integer.valueOf(totcnt));
		resultMap.put("totalpage", Integer.valueOf(totalpage));
		resultMap.put("pagenum", Integer.valueOf(pagenum));
		resultMap.put("startpage", Integer.valueOf(startpage));
		resultMap.put("endpage", Integer.valueOf(endpage));
		resultMap.put("pagesize", Integer.valueOf(pagesize));
		resultMap.put("displaysize", Integer.valueOf(displaysize));
		resultMap.put("beforepage", Integer.valueOf(beforepage));
		resultMap.put("afterpage", Integer.valueOf(afterpage));
		return resultMap;
	}
}