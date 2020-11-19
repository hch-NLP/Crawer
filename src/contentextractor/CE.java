package contentextractor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class CE {
	/**
	 * �зֿ�Ĵ�С(���С=BLOCKS+1)
	 */
	private static final int BLOCKS = 0;
	/**
	 * ÿ����С����
	 */
	private static final int MIN_LENGTH = 2;// ÿһ������ݲ�������MIN_LENGTH����
	private static final float TITLEYUZHI = 0.85f;// TITLEYUZHIһ�㲻��С��0.6��������[0.6-1.0]��Χ
	// private static final String END_INDEX[] = { "��һҳ", "��һҳ", "��һƪ", "��һƪ",
	// "��������", "���౨��", "�������", "����Ķ�", "�������",
	// "���ʱ��", "��ر���", "�������", "��������", "��ӭͶ��", "��Ȩ����", "��վ����", "֣������", "������",
	// "����Ƽ�", "�����Ķ�", "�Ƽ��Ķ�", "��Ʒ�Ƽ�",
	// "����Ķ�", "�����Ƽ�", "��ͬ�����Ķ�", "�����Ƽ�", "�ȵ��Ƽ�", "�����Ƽ�", "�Ƽ�����", "��������", "������ѡ",
	// "��̳����", "ר���Ƽ�", "��ͬ�����Ķ�", "ר������",
	// "�鿴����", "�ȵ�����", "�ͻ�������", "���ؿͻ���", "����APP", "�ؼ��ʣ�", "�ؼ��֣�", "��ǩ��", "�༭�Ƽ�",
	// "�������", "�Ķ�����", "���а�", "�����Ȱ�",
	// "�Ķ��Ƽ�", "�����ж�", "��վ�ȵ�", "��������", "���ž�ѡ", "�������", "Ƶ����ѡ", "END", "(��)",
	// "���꣩", "���α༭", "�༭��", "�༭:", "��ࣺ",
	// "����༭", "����༭", "���ߣ�", "׫��:", "ͨѶԱ:", "С�ࣺ", "ִ�ʣ�", "ȫվ��ѡ", "��������",
	// "�����ȵ�", "��������", "����ϲ��", "ͷ���Ƽ�", "��������",
	// "��Ҫ����", "������", "��������", "�ȵ�����", "�����ȵ�", "ȫ������", "�������", "��������", "��������",
	// "��ע����", "��ӭ��ע", "������", "�������",
	// "�Ķ�ȫ��", "���ŵ��", "�����Ƽ�", "�ۺ���Ѷ", "��������", "��������", "��������", "��������", "���Ѹ���",
	// "���ѻ���", "���Ÿ���", "����", "������Ϣ","property",
	// "Copyright", "copyright" };
	private static final String END_INDEX[] = { "��һҳ", "��һҳ", "��һƪ", "��һƪ", "��������", "���౨��", "�������", "����Ķ�", "�������",
			"���ʱ��", "��ر���", "�������", "��������", "��ӭͶ��", "��Ȩ����", "��վ����", "֣������", "������", "����Ƽ�", "�����Ķ�", "�Ƽ��Ķ�", "��Ʒ�Ƽ�",
			"����Ķ�", "�����Ƽ�", "��ͬ�����Ķ�", "�����Ƽ�", "�ȵ��Ƽ�", "�����Ƽ�", "�Ƽ�����", "��������", "������ѡ", "��̳����", "ר���Ƽ�", "��ͬ�����Ķ�", "ר������",
			"�鿴����", "�ȵ�����", "�ͻ�������", "���ؿͻ���", "����APP", "��ǩ��", "�༭�Ƽ�", "�������", "�Ķ�����", "���а�", "�����Ȱ�", "�Ķ��Ƽ�", "�����ж�",
			"��վ�ȵ�", "��������", "���ž�ѡ", "�������", "Ƶ����ѡ", "END", "(��)", "���꣩", "ȫվ��ѡ", "��������", "�����ȵ�", "��������", "����ϲ��", "ͷ���Ƽ�",
			"��������", "��Ҫ����", "������", "��������", "�ȵ�����", "�����ȵ�", "ȫ������", "�������", "��������", "��������", "��ע����", "��ӭ��ע", "�������",
			"�Ķ�ȫ��", "���ŵ��", "�����Ƽ�", "�ۺ���Ѷ", "��������", "��������", "��������", "��������", "���Ѹ���", "���ѻ���", "���Ÿ���", "������Ϣ", "property",
			"Copyright", "copyright" };

	@SuppressWarnings({ "deprecation", "resource" })
	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();
		int k = 0, m = 0, j = 0;
		List<ServerAddress> addresses = new ArrayList<ServerAddress>();
		ServerAddress address1 = new ServerAddress("172.20.14.70", 27017);
		ServerAddress address2 = new ServerAddress("172.20.14.75", 27017);
		ServerAddress address3 = new ServerAddress("172.20.14.76", 27017);
		addresses.add(address1);
		addresses.add(address2);
		addresses.add(address3);
		ReadPreference preference = ReadPreference.secondaryPreferred();// �������ݵĶ�д����
		MongoClient mc = new MongoClient(addresses);// �°汾API
		mc.setReadPreference(preference);
		MongoDatabase db = mc.getDatabase("crawler");
		FindIterable<Document> dbtitle = db.getCollection("contentTask_20181221").find(Filters.eq("state", 5))
				.limit(200);
		for (Document doc : dbtitle) {
			m++;
			try {
				System.out.println("********************************************");
				String html = (String) doc.get("html");
				String title = (String) doc.get("title");
				String t1 = (String) doc.get("content");
				String t2 = extractContent(title, html);
				if (1.0 * calculateLCS(t1.substring(0, Math.min(800, t1.length())),
						t2.substring(0, Math.min(800, t2.length()))).length()
						/ t1.substring(0, Math.min(800, t1.length())).length() >= 0.8) {
					k++;
				}
				System.out.println((String) doc.get("url"));
				System.out.println("�����㷨�����ȡ�� " + title);
				System.out.println("�����㷨��ȡ����� " + "GD_" + t1);
				System.out.println("�����㷨��ȡ����� " + "ZY_" + t2);
			} catch (Exception e) {
				j++;
				e.printStackTrace();
			}
		}
		System.out.println("��ȡ׼ȷ��Ϊ��" + (k + j) * 1.0 / m);
		System.out.println("��ȷ��ȡ����Ϊ��" + k);
		System.out.println("�����ȡ����Ϊ��" + (m - k - j));
		System.out.println("�쳣����Ϊ��" + j);
		System.out.println("������Ϊ��" + m);
		long endTime = System.currentTimeMillis();
		System.out.println("��ȡ��ʱ��" + (endTime - startTime) / 1000.0 + " (S)");
	}

	/**
	 * ȥ��html��ǩ
	 * 
	 * @param html
	 * @�����õ�html�ı�
	 * @return ���ı�
	 * @throws Exception
	 */
	public static String extractContent(String title, String html) throws Exception {
		Map<Integer, String> map = null;
		try {
			html = deleteLabel(html); // �Ƴ���ǩ�ڵ�
			map = splitBlock(html);
			return getcontent(map, title);
		} catch (Exception e) {
			throw new Exception("���ĳ�ȡʧ�ܣ�");
		}
	}

	public static String deleteLabel(String html) throws Exception {
		String regEx_pageinfo = "<pageinfo[^>]*?>[\\s\\S]*?<\\/pageinfo>"; // ����pageinfo��������ʽ
		String regEx_pageinfo1 = "<pageinfo1[^>]*?>[\\s\\S]*?<\\/pageinfo1>"; // ����pageinfo1��������ʽ
		String regEx_pageinfo2 = "<pageinfo2[^>]*?>[\\s\\S]*?<\\/pageinfo2>"; // ����pageinfo2��������ʽ
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // ����script��������ʽ
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // ����style��������ʽ
		String regEx_html = "<[^>]+>"; // ����HTML��ǩ��������ʽ
		String regEx_anno = "(?is)<!--[\\s\\S]*?-->"; // �Ƴ�htmlע�ͷ�
		try {
			html = html.replaceAll(regEx_pageinfo, "");
			html = html.replaceAll(regEx_pageinfo1, "");
			html = html.replaceAll(regEx_pageinfo2, "");
			html = html.replaceAll(regEx_script, "");
			html = html.replaceAll(regEx_style, "");
			html = html.replaceAll(regEx_html, "");
			html = html.replaceAll(regEx_anno, "");
			html = html.replace("|", "\r\n");
			html = html.replace("��", "\r\n");
			html = html.replace("-->", "\r\n");// �Ƴ�����ע�ͽ�β��
			html = html.replace(">", "\r\n");// �Ƴ����б�ǩ��β��
			html = html.replaceAll("((\r\n)|\n)[\\s\\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");// ȥ���հ���
			html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
			html = html.replaceAll("&.{2,5};|&#.{2,5};", ""); // �Ƴ������ַ�
			html = html.replaceAll("(?is)<.*?>", "");// �Ƴ����б�ǩ
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
		String line = null, blocksLine = "";
		int theCount = 0, groupCount = 0;
		try {
			while ((line = br.readLine()) != null) {
				if (line.replaceAll("\\s*", "").length() >= MIN_LENGTH) {
					if (theCount <= BLOCKS) {
						blocksLine += line.trim();
						theCount++;
					} else {
						groupMap.put(groupCount, blocksLine);
						groupCount++;
						blocksLine = line.trim();
						theCount = 1;
					}
				}
			}
			if (theCount != 0) {
				groupMap.put(groupCount, blocksLine);
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
		String context = "";
		int StartBlock = 1, EndBlock = map.size() - 1, Endindex = map.size() - 1, topvalue = title.length(),
				map_title = StartBlock;
		boolean flag = false;
		try {
			if (map.size() <= 1) {
				throw new Exception("��ȡ����ʧ�ܣ�");
			}
			if (StartBlock < map.size()) {// ��ȡǱ������������Ŀ����ڵ�λ��
				double longestblock = map.get(StartBlock).length();
				for (int k = StartBlock; k < map.size(); k++) {
					if (calculateLCS(map.get(k), title).length() >= 2 && map.get(k).length() >= longestblock) {
						longestblock = map.get(k).length();
						topvalue = k;
						if (longestblock >= title.length() + 200) {
							break;
						}
					}
				}
			}
			for (int i = 0; i < topvalue; i++) {// ����п��е�Ǳ�ڱ���
				if (map.get(i).contains("_")) {
					map_title = i + 1;
					break;
				}
			}
			for (int i = map_title; i < topvalue; i++) {// �������������Ŀ�ʼ�п�ţ����õ�������������ж����㷨
				if (calculateLCS(map.get(i), title).length()
						/ (0.5 * (title.length() + map.get(i).length())) >= TITLEYUZHI || map.get(i).contains(title)) {
					StartBlock = i;
				}
			}
			if (calculateLCS(map.get(StartBlock), title).length() < 2) {// ��������Ϊ�գ���������ݿ��������
				return map.get(topvalue).replaceAll("&nbsp", "").replaceAll("	", "").replaceAll("  ", "")
						.replaceAll("��", "").replaceAll("��", "");
			} else {
				if (StartBlock < topvalue - 1 && topvalue - 1 < map.size()) {// ���µ���Ǳ������������Ŀ����ڵ�λ��
					for (int k = topvalue - 1; k > StartBlock; k--) {
						if (calculateLCS(map.get(k), title).length() >= 2 && map.get(k).length() > title.length()) {
							topvalue = k;
							break;
						}
					}
				}
				// System.out.println("StartBlock��" + StartBlock);
				// System.out.println("topvalue��" + topvalue);
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
				for (int k = Endindex; k >= StartBlock; k--) {
					if (map.get(k).contains("��")) {// �����ݾ�����Ż������������ݽ�������п�λ��
						Endindex = k;
						break;
					}
				}
				for (int k = Endindex; k >= StartBlock; k--) {// �����������Ľ����п�ţ�
					if (calculateLCS(map.get(k), title).length() >= 2) {
						EndBlock = k;
						break;
					}
				}
				EndBlock = Math.min(Endindex, EndBlock);
				// System.out.println("EndBlock��" + EndBlock);
				if (StartBlock < EndBlock) {
					for (int j = StartBlock; j <= EndBlock; j++) {
						context += map.get(j);
					}
				} else if (StartBlock == EndBlock) {
					context += map.get(StartBlock);
				} else {
					throw new Exception("������ȡʧ�ܣ�");
				}
				return context.replaceAll("&nbsp", "").replaceAll("	", "").replaceAll("  ", "").replaceAll("��", "")
						.replaceAll("��", "");
			}
		} catch (Exception e) {
			throw new Exception("������ȡʧ�ܣ�");
		}
	}

	static public String calculateLCS(String s1, String s2) throws Exception {/*���㷨��������������У�����������Ӵ�*/
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
			String subString = "";
			i = L1;
			j = L2;
			while (i > 0 && j > 0) {
				if (opt[i][j] > opt[i - 1][j] && opt[i][j] > opt[i][j - 1]) {
					subString += s1.charAt(i - 1);
					--i;
					--j;
				} else if (opt[i][j] == opt[i - 1][j]) {
					--i;
				} else if (opt[i][j] == opt[i][j - 1]) {
					--j;
				}
			}
			String op = "";
			for (i = subString.length() - 1; i >= 0; --i) {
				op += subString.charAt(i);
			}
			return op;
		} catch (Exception e) {
			throw new Exception("������Ӵ�����ʧ�ܣ�");
		}
	}
}
