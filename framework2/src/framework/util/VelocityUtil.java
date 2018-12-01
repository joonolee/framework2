/* 
 * @(#)VelocityUtil.java
 */
package framework.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServlet;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import framework.action.Box;

/**
 * Velocity를 이용한 템플릿 처리 라이브러리
 */
public class VelocityUtil {

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없도록 설정
	 */
	private VelocityUtil() {
	}

	/**
	 * action.properties 파일에 설정된 key와 연결된 템플릿 파일에서 statement에 정의된 COMMAND의 문자열을 파라미터를 
	 * 적용한 문자열을 생성한다. VelocityUtil.evalutate과 동일
	 * <br>
	 * Sql 문장생성 및 이메일 발송을 위한 템플릿 생성할때 응용할 수 있다.
	 * @param servlet 서블릿 객체
	 * @param key action.properties에 등록한 템플릿의 키 문자열 
	 * @param statement 문장식별 문자열
	 * @param param 파라미터 Box 객체
	 * @return 템플릿이 적용된 문자열
	 * @throws Exception Exception
	 */
	public static String render(HttpServlet servlet, String key, String statement, Box param) throws Exception {
		return evaluate(servlet, key, statement, param);
	}

	/**
	 * action.properties 파일에 설정된 key와 연결된 템플릿 파일에서 statement에 정의된 COMMAND의 문자열을 파라미터를 
	 * 적용한 문자열을 생성한다.
	 * <br>
	 * Sql 문장생성 및 이메일 발송을 위한 템플릿 생성할때 응용할 수 있다.
	 * @param servlet 서블릿 객체
	 * @param key action.properties에 등록한 템플릿의 키 문자열 
	 * @param statement 문장식별 문자열
	 * @param param 파라미터 Box 객체
	 * @return 템플릿이 적용된 문자열
	 * @throws Exception Exception
	 */
	public static String evaluate(HttpServlet servlet, String key, String statement, Box param) throws Exception {
		Velocity.init();
		VelocityContext context = new VelocityContext();
		context.put("COMMAND", statement);
		context.put("PARAM", param);
		context.put("UTIL", StringUtil.class);
		ResourceBundle bundle = (ResourceBundle) servlet.getServletContext().getAttribute("action-mapping");
		String fileName = ((String) bundle.getObject(key)).trim();
		StringWriter writer = new StringWriter();
		String template = readTemplate(servlet, fileName);
		StringReader reader = new StringReader(template);
		Velocity.evaluate(context, writer, "framework.util.VelocityUtil", reader);
		return writer.toString();
	}

	/**
	 * 템플릿파일을 읽어들인다.
	 * @throws IOException 
	 */
	private static String readTemplate(HttpServlet servlet, String fileName) throws IOException {
		String pathFile = servlet.getServletContext().getRealPath(fileName);
		return read(pathFile);
	}

	/** 
	 * 파일의 path를 가지 파일명으로 파일 내용 읽어서 String으로 리턴한다 
	 * @throws IOException 
	 */
	private static String read(String pathFile) throws IOException {
		StringBuilder ta = new StringBuilder();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(pathFile);
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				ta.append(line + "\n");
			}
		} catch (IOException e) {
			ta.append("Problems reading file" + e.getMessage());
			throw e;
		} finally {
			if (br != null)
				br.close();
			if (fr != null)
				fr.close();
		}
		return ta.toString();
	}
}
