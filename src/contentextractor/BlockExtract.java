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
	 * 行分块的大小(块大小=BLOCKS+1)
	 */
	private static final int BLOCKS = 0;
	/**
	 * 每行最小长度
	 */
	private static final int MIN_LENGTH = 2;// 每一块的内容不得少于MIN_LENGTH个字
	private static final float TITLEYUZHI = 0.85f;// TITLEYUZHI不能小于0.6，建议在[0.6-1.0]范围
	private static final String END_INDEX[] = { "上一页", "下一页", "上一篇", "下一篇", "更多新闻", "更多报道", "相关链接", "相关阅读", "相关新闻",
			"相关时政", "相关报道", "相关文章", "免责声明", "欢迎投稿", "版权声明", "网站声明", "郑重声明", "声明：", "相关推荐", "延伸阅读", "推荐阅读", "精品推荐",
			"深度阅读", "博客推荐", "相同作者阅读", "新闻推荐", "热点推荐", "精彩推荐", "推荐新闻", "精彩文章", "看荐精选", "论坛热帖", "专题推荐", "相同主题阅读", "专栏报道",
			"查看更多", "热点排行", "客户端下载", "下载客户端", "下载APP", "标签：", "编辑推荐", "点击排行", "阅读排行", "排行榜", "本周热榜", "阅读推荐", "往期有读",
			"网站热点", "新闻排行", "新闻精选", "点击排名", "频道精选", "END", "(完)", "（完）", "全站精选", "最新文章", "今日热点", "发表评论", "猜你喜欢", "头条推荐",
			"网友评论", "我要评论", "热评论", "参与评论", "热点评论", "最新热点", "全部评论", "添加评论", "参与讨论", "更多评论", "关注我们", "欢迎关注", "相关内容",
			"阅读全文", "热门点击", "热门推荐", "综合资讯", "热门搜索", "热门文章", "最新热门", "友情链接", "网友跟帖", "网友互动", "热门跟帖", "更多信息", "property",
			"Copyright", "copyright" };
	private static final String REGEX_PAGEINFO1 = "<pageinfo[^>]*?>[\\s\\S]*?<\\/pageinfo>"; // 定义pageinfo的正则表达式
	private static final String REGEX_PAGEINFO2 = "<pageinfo1[^>]*?>[\\s\\S]*?<\\/pageinfo1>"; // 定义pageinfo1的正则表达式
	private static final String REGEX_PAGEINFO3 = "<pageinfo2[^>]*?>[\\s\\S]*?<\\/pageinfo2>"; // 定义pageinfo2的正则表达式

	private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";// 定义script的正则表达式
	private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";// 定义style的正则表达式
	private static final String REGEX_HTML = "<[^>]+>";// 定义HTML标签的正则表达式
	private static final String REGEX_ANNO = "(?is)<!--[\\s\\S]*?-->";// html注释
	private static final String REGEX_BLANK1 = "((\r\n)|\n)[\\s\t ]*(\\1)+";// html注释
	private static final String REGEX_BLANK2 = "^((\r\n)|\n)";// html注释
	private static final String REGEX_HEAD = "(?is)<!DOCTYPE.*?>";// 首句
	private static final String REGEX_HTML_ALL = "(?is)<.*?>";// 首句
	private static final String REGEX_SPECIAL_CHAR = "&.{2,5};|&#.{2,5};";// remove
																			// special
																			// char
	private static final String REGEX_TAG = "$1";

	private static final String REGEX_1 = "|";
	private static final String REGEX_2 = "┊";
	private static final String REGEX_3 = "-->";
	private static final String REGEX_4 = ">";
	private static final String HTML_TAG_BLANK = "&nbsp";
	private static final Pattern OriginalPublished_TAG = Pattern.compile("(来源)+[：|:][^0-9^\n^\r\n]{1,10}+",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern Editor_TAG = Pattern.compile("(小编|责任人|编辑|责编)+[:|：][^0-9^\n^\r\n]{1,7}+",
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
		Encoding = getEncodingByContentUrl(URL);// 自动校正网页编码格式(速度最快)
		try {
			news = ContentExtractor.getNewsByUrl(URL, Encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Response r = null;
		try {
			r = Jsoup.connect(URL).execute();
		} catch (IOException e) {
			System.out.println("输入输出异常！" + e);
		}
		try {
			html = new String(r.bodyAsBytes(), Encoding);
		} catch (UnsupportedEncodingException e) {
			System.out.println("文件编码异常！" + e);
		}
		System.out.println("自研方式-->URL-->" + URL);
		String title = news.getTitle();
		Matcher MT = Title_TAG.matcher(title.replaceAll(" ", ""));
		if (MT.find()) {
			System.out.println("自研方式-->编码判定正确！");
		} else {
			System.out.println("自研方式-->编码判定错误！");
		}
		System.out.println("河北工业-->新闻标题-->" + title);
		Date date1 = news.getDate();
		Date date2 = new Date();
		if (date1 == null || (date1.getTime() - date2.getTime()) > 0) {
			System.out.println("自研方式-->发布时间-->" + date2);
		} else {
			System.out.println("自研方式-->发布时间-->" + date1);
		}
		try {
			html = deleteLabel(html);
		} catch (Exception e1) {
			e1.printStackTrace();
		} // 移除标签节点
		try {
			System.out.println("自研方式-->稿件来源-->" + extractOriginalPublishedFromHtml(html));
			System.out.println("自研方式-->责任人员-->" + extractEditorFromHtml(html));
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("下标越界！" + e);
		}
		Map<Integer, String> map = null;
		try {
			map = splitBlock(html);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (int j = 0; j < map.size(); j++) {
			System.out.println(j + "块内容---->" + map.get(j));
		}
		try {
			LCSAlgrithom subSq = new LCSAlgrithom();
			String t1 = news.getContent().trim();
			String t2 = getcontent(map, title);
			map.clear();
			System.out.println("工大方式-->正文内容-->" + t1);
			System.out.println("自研方式-->正文内容-->" + t2);
			int sb = subSq.calculateLCS(t1, t2).length;
			if (sb * 1.0 / Math.min(t1.length(), t2.length()) >= yuzhi) {
				System.out.println("抽取成功！抽取率为：" + (int) (sb * 1.0 / t2.length() * 100) + "%");
			} else {
				System.out.println("抽取失败！抽取率为：" + (int) (sb * 1.0 / t2.length() * 100) + "%");
			}
		} catch (Exception e) {
			System.out.println("空指针！" + e);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("抽取耗时：" + (endTime - startTime) / 1000.0 + " (S)");
	}

	static String extractOriginalPublishedFromHtml(String html) {
		if (html != null) {
			Matcher matcher = OriginalPublished_TAG.matcher(html);
			if (matcher.find()) {
				String t = matcher.group().trim();
				return t.replaceAll("：", ":").replaceAll(":", "").split("\\|")[0].split("原")[0].split("作者")[0]
						.split("\\（")[0].split("\\(")[0].split("责")[0].split("编辑")[0].split("来源")[1].split(" ")[0];
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
				return t.replaceAll("\r\n", " ").replaceAll("\n", " ").replaceAll("：", ":")
						.replaceAll("[\\[|:|\\(|\\（|\\）|\\)|\\]|\\【|\\】]", "").replaceAll("小编", "编辑")
						.replaceAll("责任人", "编辑").replaceAll("责编", "编辑").split("编辑")[1].split(" ")[0];
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
			html = html.replace(REGEX_3, Constants.NEWLINE);// 移除所有注释结尾符
			html = html.replace(REGEX_4, Constants.NEWLINE);// 移除所有标签结尾符

			html = html.replaceAll(REGEX_BLANK1, REGEX_TAG).replaceAll(REGEX_BLANK2, Constants.EMPTY);// 去除空白行
			html = html.replaceAll(REGEX_SPECIAL_CHAR, Constants.BLANK); // remove
																			// special
																			// char
			html = html.replaceAll(REGEX_HEAD, Constants.EMPTY);

			html = html.replaceAll(REGEX_HTML_ALL, Constants.EMPTY);// 移除所有标签
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
			throw new Exception("行块分割失败！");
		}
	}

	/*
	 * 采取先进的行块双向分层定位技术来获取正文内容
	 */
	public static String getcontent(Map<Integer, String> map, String title) throws Exception {
		int startBlock = 1, EndBlock = map.size() - 1, Endindex = map.size() - 1, topvalue = title.length();
		int map_title = startBlock;
		boolean flag = false;
		try {
			if (map.size() <= 1) {
				throw new Exception("获取正文失败！");
			}
			if (startBlock < map.size()) {// 获取潜在正文内容最长的块所在的位置
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
			for (int i = 0; i < topvalue; i++) {// 标记行块中的潜在标题
				if (map.get(i).contains(Constants.UNDERLINE)) {
					map_title = i + 1;
					break;
				}
			}
			for (int i = map_title; i < topvalue; i++) {// 首中向搜索正文开始行块号！采用调和最长公共子序列度量算法
				if (calculateLCS(map.get(i), title) / (0.5 * (title.length() + map.get(i).length())) >= TITLEYUZHI
						|| map.get(i).contains(title)) {
					startBlock = i;
				}
			}
			if (calculateLCS(map.get(startBlock), title) < 2) {// 正文内容为空，就以最长内容块代替正文
				return map.get(topvalue).replaceAll(HTML_TAG_BLANK, Constants.EMPTY)
						.replaceAll(Constants.TAB, Constants.EMPTY).replaceAll(Constants.BLANK_DOUBLE, Constants.EMPTY)
						.replaceAll("（", "").replaceAll("）", "");
			} else {
				if (startBlock < topvalue - 1 && topvalue - 1 < map.size()) {// 更新调整潜在正文内容最长的块所在的位置
					for (int k = topvalue - 1; k > startBlock; k--) {
						if (calculateLCS(map.get(k), title) >= 2 && map.get(k).length() > title.length()) {
							topvalue = k;
							break;
						}
					}
				}
				System.out.println("StartBlock：" + startBlock);
				System.out.println("topvalue：" + topvalue);
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
				for (int k = Endindex; k >= startBlock; k--) {
					if (map.get(k).contains(Constants.PERIOD_GB2312)) {// 最后根据句号来优化调整正文内容结束块的行块位置
						Endindex = k;
						break;
					}
				}
				for (int k = Endindex; k >= startBlock; k--) {// 逆向搜索正文结束行块号！
					if (calculateLCS(map.get(k), title) >= 2) {
						EndBlock = k;
						break;
					}
				}
				EndBlock = Math.min(Endindex, EndBlock);
				System.out.println("EndBlock：" + EndBlock);
				String context = Constants.EMPTY;
				if (startBlock < EndBlock) {
					for (int j = startBlock; j <= EndBlock; j++) {
						context += map.get(j);
					}
				} else if (startBlock == EndBlock) {
					context += map.get(startBlock);
				} else {
					throw new Exception("正文提取失败！");
				}
				return context.replaceAll(HTML_TAG_BLANK, Constants.EMPTY).replaceAll(Constants.TAB, Constants.EMPTY)
						.replaceAll(Constants.BLANK_DOUBLE, Constants.EMPTY)
						.replaceAll(Constants.LEFT_BRACKET_GB2312, Constants.EMPTY)
						.replaceAll(Constants.RIGHT_BRACKET_GB2312, Constants.EMPTY);
			}
		} catch (Exception e) {
			throw new Exception("正文提取失败！");
		}
	}

	/**
	 * 根据网页内容获取页面编码 case : 适用于可以直接读取网页的情况(例外情况:一些博客网站禁止不带User-Agent信息的访问请求)
	 * 
	 * @param url
	 * @return
	 */
	public static String getEncodingByContentUrl(String url) {
		CodepageDetectorProxy cdp = CodepageDetectorProxy.getInstance();
		cdp.add(JChardetFacade.getInstance());// 依赖jar包 ：antlr.jar & chardet.jar
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
	private static int calculateLCS(String s1, String s2) throws Exception {/*本算法采用最长公共子序列，并非最长公共子串*/
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
			throw new Exception("最长公共子串计算失败！");
		}
	}
}
