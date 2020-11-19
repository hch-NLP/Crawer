package contentextractor;

public class LCSAlgrithom {
	/*
	 * Author: Chunhui H. Date: 2018-03-23 
	 * Algrithom function: remove news web
	 * noise information
	 */
	class OutputVO {
		String lcs;
		int length;
	}
	public OutputVO calculateLCS(String s1, String s2) {
		int L1 = s1.length();
		int L2 = s2.length();
		int i = 0, j = 0;
		int[][] opt = new int[L1 + 1][L2 + 1];
		OutputVO output = new OutputVO();
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
		output.length = subString.length();
		String op = "";
		for (i = subString.length() - 1; i >= 0; --i) {
			op += subString.charAt(i);
		}
		output.lcs = op;
		return output;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String title = "������4��1���𣬵������ַ�������˰������׼��";
		String[] content = { "�������۴󳱼������ݡ�",
				"������Ϣ��Ѷ(���� ��Сƽ ͨѶԱ κ־��)�������մ�����������������Ļ�Ϥ��������������������׼�������еط�˰����·��������еط�˰��ֹ��ڹ淶������������˰���������Ĺ��桷�ƣ���4��1���𣬶����������ĳ���������������˰����ϵͳ�е�סլ�������(�׳ƶ��ַ�)��׼�۸񡢼۸�Ӱ����������ϵ�����е�����������סլ�����������˰��������������ϵͳ����",
				"��Ϥ���˴��е�˰�ֳ�̨���ߵ�����׼��˰�۸�һ������ԭ������������˰����ϵͳ�е����ݽ�Ϊ�¾ɣ�Ӧ�õĻ���2011��ȷ�������ݣ��ձ����Ŀǰ�Ľ��׼۸񣬲�����˰�յ����ɡ���һ���棬����סլ�����������˰��������������ϵͳ���ܴ�̶��ϼ����˷�סլ����������˰�����ܻ�׼�۸����Ϊ����Ӱ�죬��һ��ȷ���˹�����ƽ����˰�淶��",
				"�����������������ҵ��Ƶ���ظ�����˵��2017��������ȫ�ж��ַ����׽�Ϊ�𱬣����ַ����׼۸����ǽϿ졣���ַ�������˰������׼�۸�����󣬽�ȷ����˰��ƽ������ͬʱҲ�����ڴ�����������Ʒ��۽Ͽ����ǡ�",
				"ѧϰ��̼�������ϵ51job��" };
		LCSAlgrithom subSq = new LCSAlgrithom();
		for (int i = 0; i < content.length; i++) {
			OutputVO output = subSq.calculateLCS(title, content[i]);
			if (output.length >= 2) {
				System.out.println("����Ϊ��" + output.length + " #������Ӵ�Ϊ:# " + output.lcs);
			} else {
				System.out.println("����ѱ�����");
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("��ǰ�����ʱ��" + (endTime - startTime) + "ms");
	}
}
