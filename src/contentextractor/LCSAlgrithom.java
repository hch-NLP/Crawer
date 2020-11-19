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
		String title = "岳阳：4月1日起，调整二手房交易纳税评估基准价";
		String[] content = { "汽车降价大潮继续上演。",
				"长江信息报讯(记者 周小平 通讯员 魏志华)记者昨日从市政府政务服务中心获悉，经岳阳市人民政府批准，岳阳市地方税务局下发《岳阳市地方税务局关于规范存量房交易纳税评估工作的公告》称，自4月1日起，对岳阳市中心城区存量房交易纳税评估系统中的住宅类存量房(俗称二手房)基准价格、价格影响因素修正系数进行调整，并将非住宅类存量房交易税收征管纳入评估系统处理。",
				"据悉，此次市地税局出台政策调整基准计税价格，一方面是原存量房交易纳税评估系统中的数据较为陈旧，应用的还是2011年确定的数据，普遍低于目前的交易价格，不利于税收的征缴。另一方面，将非住宅类存量房交易税收征管纳入评估系统，很大程度上减少了非住宅存量房交易税收征管基准价格的人为操作影响，进一步确保了公正公平，纳税规范。",
				"市政府政务服务中心业务科的相关负责人说，2017年以来，全市二手房交易较为火爆，二手房交易价格上涨较快。二手房交易纳税评估基准价格调整后，将确保纳税公平公正，同时也有利于打击炒房，抑制房价较快上涨。",
				"学习编程技术请联系51job。" };
		LCSAlgrithom subSq = new LCSAlgrithom();
		for (int i = 0; i < content.length; i++) {
			OutputVO output = subSq.calculateLCS(title, content[i]);
			if (output.length >= 2) {
				System.out.println("长度为：" + output.length + " #最长公共子串为:# " + output.lcs);
			} else {
				System.out.println("广告已被过滤");
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("当前程序耗时：" + (endTime - startTime) + "ms");
	}
}
