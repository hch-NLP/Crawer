package contentextractor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import contentextractor.LCSAlgrithom.OutputVO;
import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CText {
	/**
	 * �зֿ�Ĵ�С(���С=BLOCKS+1)
	 */
	private static final int BLOCKS = 0;
	/**
	 * ÿ����С����
	 */
	private static final int MIN_LENGTH = 8;
	private static final Pattern OriginalPublished_TAG = Pattern.compile("(��Դ)+[��|:][^0-9^\n^\r\n]{1,10}+",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern Editor_TAG = Pattern.compile("(С��|������|�༭|���)+[:|��][^0-9^\n^\r\n]{1,7}+",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern Title_TAG = Pattern.compile("[\u4e00-\u9fa5]+", Pattern.CASE_INSENSITIVE);
	public static ArrayList<String> readtext(String path) {
		ArrayList<String> biaozhuresult = new ArrayList<String>();
		try {
			FileInputStream fis = new FileInputStream(path);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				biaozhuresult.add(line);
			}
			fis.close();
			isr.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return biaozhuresult;
	}
	public static void main(String[] args) {
		float yuzhi = 0.9f;
		long startTime = System.currentTimeMillis();
		String URL = "http://column.chinadaily.com.cn/article.php?pid=33093";
		News news = null;
		String html = null, Encoding = "";
		Encoding = getEncodingByContentUrl(URL);// �Զ�У����ҳ�����ʽ(�ٶ����)
		try {
			news = ContentExtractor.getNewsByUrl(URL, Encoding);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Response r = null;
		try {
			r = Jsoup.connect(URL).execute();
		} catch (IOException e) {
			System.out.println("��������쳣��" + e);
		}
		try {
			html = new String(r.bodyAsBytes(), Encoding);
		} catch (UnsupportedEncodingException e) {
			System.out.println("�ļ������쳣��" + e);
		}
		System.out.println("���з�ʽ-->URL-->" + URL);
		String title= news.getTitle();
		Matcher MT = Title_TAG.matcher(title.replaceAll(" ", ""));
		if (MT.find()) {
			System.out.println("���з�ʽ-->�����ж���ȷ��");
		} else {
			System.out.println("���з�ʽ-->�����ж�����");
		}
		System.out.println("�ӱ���ҵ-->���ű���-->" + title);
		Date date1 = news.getDate();
		Date date2 = new Date();
		if (date1 == null || (date1.getTime() - date2.getTime()) > 0) {
			System.out.println("���з�ʽ-->����ʱ��-->" + date2);
		} else {
			System.out.println("���з�ʽ-->����ʱ��-->" + date1);
		}
		html = deleteLabel(html); // �Ƴ���ǩ�ڵ�
		try {
			System.out.println("���з�ʽ-->�����Դ-->" + extractOriginalPublishedFromHtml(html));
			System.out.println("���з�ʽ-->������Ա-->" + extractEditorFromHtml(html));
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("�±�Խ�磡" + e);
		}
		Map<Integer, String> map = splitBlock(html);
		// for(int j=0;j<map.size();j++){
		// System.out.println(j+"+"+map.get(j).length());
		// }
		try {
			LCSAlgrithom subSq = new LCSAlgrithom();
			String t1 = news.getContent().trim();
			String t2 = getcontent(map, title);
			System.out.println("���з�ʽ-->��������-->" + getcontent(map, title));
			int sb = subSq.calculateLCS(t1, t2).length;
			if (sb * 1.0 / t2.length() >= yuzhi) {
				System.out.println("��ȡ�ɹ�����ȡ��Ϊ��" + (int) (sb * 1.0 / t2.length() * 100) + "%");
			} else {
				System.out.println("��ȡʧ�ܣ���ȡ��Ϊ��" + (int) (sb * 1.0 / t2.length() * 100) + "%");
			}
		} catch (NullPointerException e) {
			System.out.println("��ָ�룡" + e);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("��ȡ��ʱ��" + (endTime - startTime) / 1000.0 + " (S)");
	}

	static String extractOriginalPublishedFromHtml(String html) {
		if (html != null) {
			Matcher matcher = OriginalPublished_TAG.matcher(html);
			if (matcher.find()) {
				String t = matcher.group().trim();
				return t.replaceAll("��", ":").replaceAll(":", "").split("\\|")[0].split("ԭ")[0].split("����")[0]
						.split("\\��")[0].split("\\(")[0].split("��")[0].split("�༭")[0].split("��Դ")[1].split(" ")[0];
			}
			return null;
		}
		return null;
	}

	static String extractEditorFromHtml(String html) {
		if (html != null) {
			Matcher matcher = Editor_TAG.matcher(html);
			if (matcher.find()) {
				String t = matcher.group();
				return t.replaceAll("\r\n", " ").replaceAll("\n", " ").replaceAll("��", ":")
						.replaceAll("[\\[|:|\\(|\\��|\\��|\\)|\\]|\\��|\\��]", "").replaceAll("С��", "�༭")
						.replaceAll("������", "�༭").replaceAll("���", "�༭").split("�༭")[1].split(" ")[0];
			}
			return null;
		}
		return null;
	}

	/**
	 * ȥ��html��ǩ
	 * 
	 * @param html
	 *            �����õ�html�ı�
	 * @return ���ı�
	 */
	public static String deleteLabel(String html) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // ����script��������ʽ
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // ����style��������ʽ
		String regEx_html = "<[^>]+>"; // ����HTML��ǩ��������ʽ
		String regEx_anno = "(?is)<!--[\\s\\S]*?-->"; // htmlע��
		html = html.replaceAll(regEx_script, "");
		html = html.replaceAll(regEx_style, "");
		html = html.replaceAll(regEx_html, "");
		html = html.replace(regEx_anno, "");
		html = html.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");// ȥ���հ���
		html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
		html = html.replaceAll("&.{2,5};|&#.{2,5};", " "); // remove special char
		html = html.replaceAll("(?is)<.*?>", "");// �Ƴ����б�ǩ
		return html.trim();
	}

	/**
	 * �����ı���BLOCKS�ֿ�
	 * 
	 * @param text
	 *            ���ı�
	 * @return �ֿ���map����,����Ϊ���,ֵΪ������
	 */
	public static Map<Integer, String> splitBlock(String text) {
		Map<Integer, String> groupMap = new HashMap<Integer, String>();
		ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(bais));
		String line = null, blocksLine = "";
		int theCount = 0, groupCount = 0;// 1.��¼ÿ����ӵ�������2.��¼���
		try {
			while ((line = br.readLine()) != null) {
				String cc1 = line, cc2 = "";
				cc2 = line.replaceAll("[��]", "");
				line = cc1;
				if (line.length() > MIN_LENGTH && (line.length() - cc2.length() >= 1)) {
					if (theCount <= BLOCKS) {
						blocksLine += line.trim();
						theCount++;
					} else {
						groupMap.put(groupCount, blocksLine); // ��źͶ�Ӧ��ֵ
						groupCount++;
						blocksLine = line.trim();
						theCount = 1;
					}
				}
//				if (line.contains("����Ķ�") || line.contains("�������") || line.contains("���ʱ������") || line.contains("��ر���")
//						|| line.contains("�����Ķ�") || line.contains("�Ƽ��Ķ�") || line.contains("��Ʒ�Ƽ�")
//						|| line.contains("��������") || line.contains("�����Ƽ�") || line.contains("��ͬ�����Ķ�")
//						|| line.contains("�ȵ��Ƽ�") || line.contains("�����Ƽ�") || line.contains("��̳����")
//						|| line.contains("ר���Ƽ�") || line.contains("��ͬ�����Ķ�") || line.contains("���౨��")
//						|| line.contains("ר������") || line.contains("�鿴����") || line.contains("�ȵ�����")
//						|| line.contains("�༭�Ƽ�") || line.contains("�������") || line.contains("�Ķ�����")
//						|| line.contains("�Ķ��Ƽ�") || line.contains("��վ�ȵ�") || line.contains("��������")
//						|| line.contains("�������") || line.contains("��վ����") || line.contains("��Ȩ����")
//						|| line.contains("��������|") || line.contains("Ƶ����ѡ") || line.contains("END")) {
//					break;
//				}
			}
			if (theCount != 0) {// ����û����ĸ�������
				groupMap.put(groupCount, blocksLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return groupMap;
	}

	public static String getcontent(Map<Integer, String> map, String title) {
		LCSAlgrithom subSq = new LCSAlgrithom();
		title = title.replaceAll("[\\d+���������������� 	]", "");
		String context = "";
		int StartBlock = 0, EndBlock = map.size() - 1;
		for (int i = 0; i <= map.size() / 2; i++) {// �����������Ŀ�ʼ�п�ţ�
			OutputVO output = subSq.calculateLCS(map.get(i), title);
			if (output.length >= 3) {
				StartBlock = i;
				break;
			}
		}
		for (int k = (map.size() - 1); k >= map.size() / 2; k--) {// �����������Ľ����п�ţ�
			OutputVO output = subSq.calculateLCS(map.get(k), title);
			if (output.length >= 3) {
				EndBlock = k;
				break;
			}
		}
		if (StartBlock < EndBlock) {
			for (int j = StartBlock; j <= EndBlock; j++) {
				context += map.get(j);
			}
		} else if (StartBlock == EndBlock) {
			context += map.get(StartBlock);
		}
		return context.replaceAll("����", "");
	}

	/**
	 * ������ҳ���ݻ�ȡҳ����� case : �����ڿ���ֱ�Ӷ�ȡ��ҳ�����(�������:һЩ������վ��ֹ����User-Agent��Ϣ�ķ�������)
	 * @param url
	 * @return
	 */
	public static String getEncodingByContentUrl(String url) {
		CodepageDetectorProxy cdp = CodepageDetectorProxy.getInstance();
		cdp.add(JChardetFacade.getInstance());// ����jar�� ��antlr.jar & chardet.jar
		cdp.add(ASCIIDetector.getInstance());
		cdp.add(UnicodeDetector.getInstance());
		cdp.add(new ParsingDetector(false));
		cdp.add(new ByteOrderMarkDetector());
		Charset charset = null;
		try {
			charset = cdp.detectCodepage(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (charset.name().toLowerCase().contains("gb")) {
			return "gbk";
		} else {
			return "utf-8";
		}
		// return charset == null ? "utf-8" : charset.name().toLowerCase();
	}
}
