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
	 * 行分块的大小(块大小=BLOCKS+1)
	 */
	private static final int BLOCKS = 0;
	/**
	 * 每行最小长度
	 */
	private static final int MIN_LENGTH = 2;// 每一块的内容不得少于MIN_LENGTH个字
	private static final float TITLEYUZHI = 0.85f;// TITLEYUZHI一般不能小于0.6，建议在[0.6-1.0]范围
	// private static final String END_INDEX[] = { "上一页", "下一页", "上一篇", "下一篇",
	// "更多新闻", "更多报道", "相关链接", "相关阅读", "相关新闻",
	// "相关时政", "相关报道", "相关文章", "免责声明", "欢迎投稿", "版权声明", "网站声明", "郑重声明", "声明：",
	// "相关推荐", "延伸阅读", "推荐阅读", "精品推荐",
	// "深度阅读", "博客推荐", "相同作者阅读", "新闻推荐", "热点推荐", "精彩推荐", "推荐新闻", "精彩文章", "看荐精选",
	// "论坛热帖", "专题推荐", "相同主题阅读", "专栏报道",
	// "查看更多", "热点排行", "客户端下载", "下载客户端", "下载APP", "关键词：", "关键字：", "标签：", "编辑推荐",
	// "点击排行", "阅读排行", "排行榜", "本周热榜",
	// "阅读推荐", "往期有读", "网站热点", "新闻排行", "新闻精选", "点击排名", "频道精选", "END", "(完)",
	// "（完）", "责任编辑", "编辑：", "编辑:", "责编：",
	// "网络编辑", "助理编辑", "作者：", "撰稿:", "通讯员:", "小编：", "执笔：", "全站精选", "最新文章",
	// "今日热点", "发表评论", "猜你喜欢", "头条推荐", "网友评论",
	// "我要评论", "热评论", "参与评论", "热点评论", "最新热点", "全部评论", "添加评论", "参与讨论", "更多评论",
	// "关注我们", "欢迎关注", "分享到：", "相关内容",
	// "阅读全文", "热门点击", "热门推荐", "综合资讯", "热门搜索", "热门文章", "最新热门", "友情链接", "网友跟帖",
	// "网友互动", "热门跟帖", "评论", "更多信息","property",
	// "Copyright", "copyright" };
	private static final String END_INDEX[] = { "上一页", "下一页", "上一篇", "下一篇", "更多新闻", "更多报道", "相关链接", "相关阅读", "相关新闻",
			"相关时政", "相关报道", "相关文章", "免责声明", "欢迎投稿", "版权声明", "网站声明", "郑重声明", "声明：", "相关推荐", "延伸阅读", "推荐阅读", "精品推荐",
			"深度阅读", "博客推荐", "相同作者阅读", "新闻推荐", "热点推荐", "精彩推荐", "推荐新闻", "精彩文章", "看荐精选", "论坛热帖", "专题推荐", "相同主题阅读", "专栏报道",
			"查看更多", "热点排行", "客户端下载", "下载客户端", "下载APP", "标签：", "编辑推荐", "点击排行", "阅读排行", "排行榜", "本周热榜", "阅读推荐", "往期有读",
			"网站热点", "新闻排行", "新闻精选", "点击排名", "频道精选", "END", "(完)", "（完）", "全站精选", "最新文章", "今日热点", "发表评论", "猜你喜欢", "头条推荐",
			"网友评论", "我要评论", "热评论", "参与评论", "热点评论", "最新热点", "全部评论", "添加评论", "参与讨论", "更多评论", "关注我们", "欢迎关注", "相关内容",
			"阅读全文", "热门点击", "热门推荐", "综合资讯", "热门搜索", "热门文章", "最新热门", "友情链接", "网友跟帖", "网友互动", "热门跟帖", "更多信息", "property",
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
		ReadPreference preference = ReadPreference.secondaryPreferred();// 设置数据的读写分离
		MongoClient mc = new MongoClient(addresses);// 新版本API
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
				System.out.println("自研算法标题抽取： " + title);
				System.out.println("工大算法抽取结果： " + "GD_" + t1);
				System.out.println("自研算法抽取结果： " + "ZY_" + t2);
			} catch (Exception e) {
				j++;
				e.printStackTrace();
			}
		}
		System.out.println("抽取准确率为：" + (k + j) * 1.0 / m);
		System.out.println("正确抽取数据为：" + k);
		System.out.println("错误抽取数据为：" + (m - k - j));
		System.out.println("异常数据为：" + j);
		System.out.println("总数据为：" + m);
		long endTime = System.currentTimeMillis();
		System.out.println("抽取耗时：" + (endTime - startTime) / 1000.0 + " (S)");
	}

	/**
	 * 去除html标签
	 * 
	 * @param html
	 * @请求获得的html文本
	 * @return 纯文本
	 * @throws Exception
	 */
	public static String extractContent(String title, String html) throws Exception {
		Map<Integer, String> map = null;
		try {
			html = deleteLabel(html); // 移除标签节点
			map = splitBlock(html);
			return getcontent(map, title);
		} catch (Exception e) {
			throw new Exception("正文抽取失败！");
		}
	}

	public static String deleteLabel(String html) throws Exception {
		String regEx_pageinfo = "<pageinfo[^>]*?>[\\s\\S]*?<\\/pageinfo>"; // 定义pageinfo的正则表达式
		String regEx_pageinfo1 = "<pageinfo1[^>]*?>[\\s\\S]*?<\\/pageinfo1>"; // 定义pageinfo1的正则表达式
		String regEx_pageinfo2 = "<pageinfo2[^>]*?>[\\s\\S]*?<\\/pageinfo2>"; // 定义pageinfo2的正则表达式
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
		String regEx_anno = "(?is)<!--[\\s\\S]*?-->"; // 移除html注释符
		try {
			html = html.replaceAll(regEx_pageinfo, "");
			html = html.replaceAll(regEx_pageinfo1, "");
			html = html.replaceAll(regEx_pageinfo2, "");
			html = html.replaceAll(regEx_script, "");
			html = html.replaceAll(regEx_style, "");
			html = html.replaceAll(regEx_html, "");
			html = html.replaceAll(regEx_anno, "");
			html = html.replace("|", "\r\n");
			html = html.replace("┊", "\r\n");
			html = html.replace("-->", "\r\n");// 移除所有注释结尾符
			html = html.replace(">", "\r\n");// 移除所有标签结尾符
			html = html.replaceAll("((\r\n)|\n)[\\s\\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");// 去除空白行
			html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
			html = html.replaceAll("&.{2,5};|&#.{2,5};", ""); // 移除特殊字符
			html = html.replaceAll("(?is)<.*?>", "");// 移除所有标签
			return html.trim();
		} catch (Exception e) {
			throw new Exception("去除标签失败！");
		}
	}

	/**
	 * 将纯文本按BLOCKS分块
	 * 
	 * @param text纯文本
	 * @return 分块后的map集合,键即为块号,值为块内容
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
			throw new Exception("行块分割失败！");
		}
	}

	/*
	 * 采取先进的行块双向分层定位技术来获取正文内容
	 */
	public static String getcontent(Map<Integer, String> map, String title) throws Exception {
		String context = "";
		int StartBlock = 1, EndBlock = map.size() - 1, Endindex = map.size() - 1, topvalue = title.length(),
				map_title = StartBlock;
		boolean flag = false;
		try {
			if (map.size() <= 1) {
				throw new Exception("获取正文失败！");
			}
			if (StartBlock < map.size()) {// 获取潜在正文内容最长的块所在的位置
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
			for (int i = 0; i < topvalue; i++) {// 标记行块中的潜在标题
				if (map.get(i).contains("_")) {
					map_title = i + 1;
					break;
				}
			}
			for (int i = map_title; i < topvalue; i++) {// 首中向搜索正文开始行块号！采用调和最长公共子序列度量算法
				if (calculateLCS(map.get(i), title).length()
						/ (0.5 * (title.length() + map.get(i).length())) >= TITLEYUZHI || map.get(i).contains(title)) {
					StartBlock = i;
				}
			}
			if (calculateLCS(map.get(StartBlock), title).length() < 2) {// 正文内容为空，就以最长内容块代替正文
				return map.get(topvalue).replaceAll("&nbsp", "").replaceAll("	", "").replaceAll("  ", "")
						.replaceAll("（", "").replaceAll("）", "");
			} else {
				if (StartBlock < topvalue - 1 && topvalue - 1 < map.size()) {// 更新调整潜在正文内容最长的块所在的位置
					for (int k = topvalue - 1; k > StartBlock; k--) {
						if (calculateLCS(map.get(k), title).length() >= 2 && map.get(k).length() > title.length()) {
							topvalue = k;
							break;
						}
					}
				}
				// System.out.println("StartBlock：" + StartBlock);
				// System.out.println("topvalue：" + topvalue);
				if (topvalue <= map.size() - 1) {// 获取潜在正文内容结束块位置标志
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
					if (topvalue <= Endindex - 2) {// 根据块长度来调整正文内容结束块所在位置
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
					if (map.get(k).contains("。")) {// 最后根据句号来优化调整正文内容结束块的行块位置
						Endindex = k;
						break;
					}
				}
				for (int k = Endindex; k >= StartBlock; k--) {// 逆向搜索正文结束行块号！
					if (calculateLCS(map.get(k), title).length() >= 2) {
						EndBlock = k;
						break;
					}
				}
				EndBlock = Math.min(Endindex, EndBlock);
				// System.out.println("EndBlock：" + EndBlock);
				if (StartBlock < EndBlock) {
					for (int j = StartBlock; j <= EndBlock; j++) {
						context += map.get(j);
					}
				} else if (StartBlock == EndBlock) {
					context += map.get(StartBlock);
				} else {
					throw new Exception("正文提取失败！");
				}
				return context.replaceAll("&nbsp", "").replaceAll("	", "").replaceAll("  ", "").replaceAll("（", "")
						.replaceAll("）", "");
			}
		} catch (Exception e) {
			throw new Exception("正文提取失败！");
		}
	}

	static public String calculateLCS(String s1, String s2) throws Exception {/*本算法采用最长公共子序列，并非最长公共子串*/
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
			throw new Exception("最长公共子串计算失败！");
		}
	}
}
