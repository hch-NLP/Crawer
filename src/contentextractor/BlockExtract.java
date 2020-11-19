package contentextractor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection.Response;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

public class BlockExtract {
	/**
	 * �зֿ�Ĵ�С(���С=BLOCKS+1)
	 */
	private static final int BLOCKS = 0;
	/**
	 * ÿ����С����
	 */
	private static final int MIN_LENGTH = 2;// ÿһ������ݲ�������MIN_LENGTH����
	private static final float TITLEYUZHI = 0.85f;// TITLEYUZHI����С��0.6��������[0.6-1.0]��Χ
	private static final String END_INDEX[] = { "��һҳ", "��һҳ", "��һƪ", "��һƪ", "��������", "���౨��", "�������", "����Ķ�", "�������",
			"���ʱ��", "��ر���", "�������", "��������", "��ӭͶ��", "��Ȩ����", "��վ����", "֣������", "������", "����Ƽ�", "�����Ķ�", "�Ƽ��Ķ�", "��Ʒ�Ƽ�",
			"����Ķ�", "�����Ƽ�", "��ͬ�����Ķ�", "�����Ƽ�", "�ȵ��Ƽ�", "�����Ƽ�", "�Ƽ�����", "��������", "������ѡ", "��̳����", "ר���Ƽ�", "��ͬ�����Ķ�", "ר������",
			"�鿴����", "�ȵ�����", "�ͻ�������", "���ؿͻ���", "����APP", "��ǩ��", "�༭�Ƽ�", "�������", "�Ķ�����", "���а�", "�����Ȱ�", "�Ķ��Ƽ�", "�����ж�",
			"��վ�ȵ�", "��������", "���ž�ѡ", "�������", "Ƶ����ѡ", "END", "(��)", "���꣩", "ȫվ��ѡ", "��������", "�����ȵ�", "��������", "����ϲ��", "ͷ���Ƽ�",
			"��������", "��Ҫ����", "������", "��������", "�ȵ�����", "�����ȵ�", "ȫ������", "�������", "��������", "��������", "��ע����", "��ӭ��ע", "�������",
			"�Ķ�ȫ��", "���ŵ��", "�����Ƽ�", "�ۺ���Ѷ", "��������", "��������", "��������", "��������", "���Ѹ���", "���ѻ���", "���Ÿ���", "������Ϣ", "property",
			"Copyright", "copyright" };
	private static final String REGEX_PAGEINFO1 = "<pageinfo[^>]*?>[\\s\\S]*?<\\/pageinfo>"; // ����pageinfo��������ʽ
	private static final String REGEX_PAGEINFO2 = "<pageinfo1[^>]*?>[\\s\\S]*?<\\/pageinfo1>"; // ����pageinfo1��������ʽ
	private static final String REGEX_PAGEINFO3 = "<pageinfo2[^>]*?>[\\s\\S]*?<\\/pageinfo2>"; // ����pageinfo2��������ʽ

	private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";// ����script��������ʽ
	private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";// ����style��������ʽ
	private static final String REGEX_HTML = "<[^>]+>";// ����HTML��ǩ��������ʽ
	private static final String REGEX_ANNO = "(?is)<!--[\\s\\S]*?-->";// htmlע��
	private static final String REGEX_BLANK1 = "((\r\n)|\n)[\\s\t ]*(\\1)+";// htmlע��
	private static final String REGEX_BLANK2 = "^((\r\n)|\n)";// htmlע��
	private static final String REGEX_HEAD = "(?is)<!DOCTYPE.*?>";// �׾�
	private static final String REGEX_HTML_ALL = "(?is)<.*?>";// �׾�
	private static final String REGEX_SPECIAL_CHAR = "&.{2,5};|&#.{2,5};";// remove
																			// special
																			// char
	private static final String REGEX_TAG = "$1";

	private static final String REGEX_1 = "|";
	private static final String REGEX_2 = "��";
	private static final String REGEX_3 = "-->";
	private static final String REGEX_4 = ">";
	private static final String HTML_TAG_BLANK = "&nbsp";
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
		String URL = "http://politics.people.com.cn/n1/2018/1025/c1024-30360903.html";
		News news = null;
		String html = null, Encoding = "";
		Encoding = getEncodingByContentUrl(URL);// �Զ�У����ҳ�����ʽ(�ٶ����)
		try {
			news = ContentExtractor.getNewsByUrl(URL, Encoding);
		} catch (Exception e) {
			e.printStackTrace();
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
		String title = news.getTitle();
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
		try {
			html = deleteLabel(html);
		} catch (Exception e1) {
			e1.printStackTrace();
		} // �Ƴ���ǩ�ڵ�
		try {
			System.out.println("���з�ʽ-->�����Դ-->" + extractOriginalPublishedFromHtml(html));
			System.out.println("���з�ʽ-->������Ա-->" + extractEditorFromHtml(html));
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("�±�Խ�磡" + e);
		}
		Map<Integer, String> map = null;
		try {
			map = splitBlock(html);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (int j = 0; j < map.size(); j++) {
			System.out.println(j + "������---->" + map.get(j));
		}
		try {
			LCSAlgrithom subSq = new LCSAlgrithom();
			String t1 = news.getContent().trim();
			String t2 = getcontent(map, title);
			map.clear();
			System.out.println("����ʽ-->��������-->" + t1);
			System.out.println("���з�ʽ-->��������-->" + t2);
			int sb = subSq.calculateLCS(t1, t2).length;
			if (sb * 1.0 / Math.min(t1.length(), t2.length()) >= yuzhi) {
				System.out.println("��ȡ�ɹ�����ȡ��Ϊ��" + (int) (sb * 1.0 / t2.length() * 100) + "%");
			} else {
				System.out.println("��ȡʧ�ܣ���ȡ��Ϊ��" + (int) (sb * 1.0 / t2.length() * 100) + "%");
			}
		} catch (Exception e) {
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

	public static String deleteLabel(String html) throws Exception {
		try {
			html = html.replaceAll(REGEX_PAGEINFO1, Constants.EMPTY);
			html = html.replaceAll(REGEX_PAGEINFO2, Constants.EMPTY);
			html = html.replaceAll(REGEX_PAGEINFO3, Constants.EMPTY);
			html = html.replaceAll(REGEX_SCRIPT, Constants.EMPTY);
			html = html.replaceAll(REGEX_STYLE, Constants.EMPTY);
			html = html.replaceAll(REGEX_HTML, Constants.EMPTY);
			html = html.replaceAll(REGEX_ANNO, Constants.EMPTY);

			html = html.replace(REGEX_1, Constants.NEWLINE);
			html = html.replace(REGEX_2, Constants.NEWLINE);
			html = html.replace(REGEX_3, Constants.NEWLINE);// �Ƴ�����ע�ͽ�β��
			html = html.replace(REGEX_4, Constants.NEWLINE);// �Ƴ����б�ǩ��β��

			html = html.replaceAll(REGEX_BLANK1, REGEX_TAG).replaceAll(REGEX_BLANK2, Constants.EMPTY);// ȥ���հ���
			html = html.replaceAll(REGEX_SPECIAL_CHAR, Constants.BLANK); // remove
																			// special
																			// char
			html = html.replaceAll(REGEX_HEAD, Constants.EMPTY);

			html = html.replaceAll(REGEX_HTML_ALL, Constants.EMPTY);// �Ƴ����б�ǩ
			return html.trim();
		} catch (Exception e) {
			throw new Exception("ȥ����ǩʧ�ܣ�");
		}
	}

	/**
	 * �����ı���BLOCKS�ֿ�
	 * 
	 * @param text���ı�
	 * @return �ֿ���map����,����Ϊ���,ֵΪ������
	 * @throws Exception
	 */
	public static Map<Integer, String> splitBlock(String text) throws Exception {
		Map<Integer, String> groupMap = new HashMap<Integer, String>();
		ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(bais));
		String line = Constants.EMPTY;
		StringBuilder blocksLine = new StringBuilder();
		int theCount = 0, groupCount = 0;
		try {
			while ((line = br.readLine()) != null) {
				if (StringUtils.length(StringUtils.deleteWhitespace(line)) >= MIN_LENGTH) {
					if (theCount <= BLOCKS) {
						blocksLine.append(line.trim());
						// blocksLine += line.trim();
						theCount++;
					} else {
						groupMap.put(groupCount, blocksLine.toString());
						groupCount++;
						blocksLine.delete(0, blocksLine.length());
						blocksLine.append(line.trim());
						// blocksLine = line.trim();
						theCount = 1;
					}
				}
			}
			if (theCount != 0) {
				groupMap.put(groupCount, blocksLine.toString());
			}
			return groupMap;
		} catch (Exception e) {
			throw new Exception("�п�ָ�ʧ�ܣ�");
		}
	}

	/*
	 * ��ȡ�Ƚ����п�˫��ֲ㶨λ��������ȡ��������
	 */
	public static String getcontent(Map<Integer, String> map, String title) throws Exception {
		int startBlock = 1, EndBlock = map.size() - 1, Endindex = map.size() - 1, topvalue = title.length();
		int map_title = startBlock;
		boolean flag = false;
		try {
			if (map.size() <= 1) {
				throw new Exception("��ȡ����ʧ�ܣ�");
			}
			if (startBlock < map.size()) {// ��ȡǱ������������Ŀ����ڵ�λ��
				double longestblock = map.get(startBlock).length();
				for (int k = startBlock; k < map.size(); k++) {
					if (calculateLCS(map.get(k), title) >= 2 && map.get(k).length() >= longestblock) {
						longestblock = map.get(k).length();
						topvalue = k;
						if (longestblock >= title.length() + 200) {
							break;
						}
					}
				}
			}
			for (int i = 0; i < topvalue; i++) {// ����п��е�Ǳ�ڱ���
				if (map.get(i).contains(Constants.UNDERLINE)) {
					map_title = i + 1;
					break;
				}
			}
			for (int i = map_title; i < topvalue; i++) {// �������������Ŀ�ʼ�п�ţ����õ�������������ж����㷨
				if (calculateLCS(map.get(i), title) / (0.5 * (title.length() + map.get(i).length())) >= TITLEYUZHI
						|| map.get(i).contains(title)) {
					startBlock = i;
				}
			}
			if (calculateLCS(map.get(startBlock), title) < 2) {// ��������Ϊ�գ���������ݿ��������
				return map.get(topvalue).replaceAll(HTML_TAG_BLANK, Constants.EMPTY)
						.replaceAll(Constants.TAB, Constants.EMPTY).replaceAll(Constants.BLANK_DOUBLE, Constants.EMPTY)
						.replaceAll("��", "").replaceAll("��", "");
			} else {
				if (startBlock < topvalue - 1 && topvalue - 1 < map.size()) {// ���µ���Ǳ������������Ŀ����ڵ�λ��
					for (int k = topvalue - 1; k > startBlock; k--) {
						if (calculateLCS(map.get(k), title) >= 2 && map.get(k).length() > title.length()) {
							topvalue = k;
							break;
						}
					}
				}
				System.out.println("StartBlock��" + startBlock);
				System.out.println("topvalue��" + topvalue);
				if (topvalue <= map.size() - 1) {// ��ȡǱ���������ݽ�����λ�ñ�־
					for (int k = topvalue; k < map.size(); k++) {
						for (int j = 0; j < END_INDEX.length; j++) {
							if (map.get(k).contains(END_INDEX[j])) {
								flag = true;
								break;
							}
						}
						if (flag) {
							Endindex = k;
							break;
						}
					}
				}
				if (flag == false) {
					if (topvalue <= Endindex - 2) {// ���ݿ鳤���������������ݽ���������λ��
						for (int k = topvalue; k <= Endindex - 2; k++) {
							if (map.get(k).length() + map.get(k + 1).length() + map.get(k + 2).length() < 20) {
								Endindex = k + 2;
								break;
							}
						}
					} else {
						Endindex = topvalue;
					}
				}
				for (int k = Endindex; k >= startBlock; k--) {
					if (map.get(k).contains(Constants.PERIOD_GB2312)) {// �����ݾ�����Ż������������ݽ�������п�λ��
						Endindex = k;
						break;
					}
				}
				for (int k = Endindex; k >= startBlock; k--) {// �����������Ľ����п�ţ�
					if (calculateLCS(map.get(k), title) >= 2) {
						EndBlock = k;
						break;
					}
				}
				EndBlock = Math.min(Endindex, EndBlock);
				System.out.println("EndBlock��" + EndBlock);
				String context = Constants.EMPTY;
				if (startBlock < EndBlock) {
					for (int j = startBlock; j <= EndBlock; j++) {
						context += map.get(j);
					}
				} else if (startBlock == EndBlock) {
					context += map.get(startBlock);
				} else {
					throw new Exception("������ȡʧ�ܣ�");
				}
				return context.replaceAll(HTML_TAG_BLANK, Constants.EMPTY).replaceAll(Constants.TAB, Constants.EMPTY)
						.replaceAll(Constants.BLANK_DOUBLE, Constants.EMPTY)
						.replaceAll(Constants.LEFT_BRACKET_GB2312, Constants.EMPTY)
						.replaceAll(Constants.RIGHT_BRACKET_GB2312, Constants.EMPTY);
			}
		} catch (Exception e) {
			throw new Exception("������ȡʧ�ܣ�");
		}
	}

	/**
	 * ������ҳ���ݻ�ȡҳ����� case : �����ڿ���ֱ�Ӷ�ȡ��ҳ�����(�������:һЩ������վ��ֹ����User-Agent��Ϣ�ķ�������)
	 * 
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (charset.name().toLowerCase().contains("gb")) {
			return "gbk";
		} else {
			return "utf-8";
		}
		// return charset == null ? "utf-8" : charset.name().toLowerCase();
	}
	private static int calculateLCS(String s1, String s2) throws Exception {/*���㷨��������������У�����������Ӵ�*/
		int L1 = s1.length();
		int L2 = s2.length();
		int i = 0, j = 0;
		int[][] opt = new int[L1 + 1][L2 + 1];
		try {
			for (j = 0; j < L2; ++j) {
				opt[0][j] = 0;
			}
			for (i = 1; i <= L1; ++i) {
				opt[i][0] = 0;
				for (j = 1; j <= L2; ++j) {
					if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
						opt[i][j] = opt[i - 1][j - 1] + 1;
					} else {
						opt[i][j] = Math.max(opt[i][j - 1], opt[i - 1][j]);
					}
				}
			}
			StringBuilder subString = new StringBuilder();
			i = L1;
			j = L2;
			while (i > 0 && j > 0) {
				if (opt[i][j] > opt[i - 1][j] && opt[i][j] > opt[i][j - 1]) {
					subString.append(s1.charAt(i - 1));
					--i;
					--j;
				} else if (opt[i][j] == opt[i - 1][j]) {
					--i;
				} else if (opt[i][j] == opt[i][j - 1]) {
					--j;
				}
			}
			return subString.length();
		} catch (Exception e) {
			throw new Exception("������Ӵ�����ʧ�ܣ�");
		}
	}
}
