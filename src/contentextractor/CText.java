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
	 * 行分块的大小(块大小=BLOCKS+1)
	 */
	private static final int BLOCKS = 0;
	/**
	 * 每行最小长度
	 */
	private static final int MIN_LENGTH = 8;
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
		String URL = "http://column.chinadaily.com.cn/article.php?pid=33093";
		News news = null;
		String html = null, Encoding = "";
		Encoding = getEncodingByContentUrl(URL);// 自动校正网页编码格式(速度最快)
		try {
			news = ContentExtractor.getNewsByUrl(URL, Encoding);
		} catch (Exception e1) {
			e1.printStackTrace();
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
		String title= news.getTitle();
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
		html = deleteLabel(html); // 移除标签节点
		try {
			System.out.println("自研方式-->稿件来源-->" + extractOriginalPublishedFromHtml(html));
			System.out.println("自研方式-->责任人员-->" + extractEditorFromHtml(html));
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("下标越界！" + e);
		}
		Map<Integer, String> map = splitBlock(html);
		// for(int j=0;j<map.size();j++){
		// System.out.println(j+"+"+map.get(j).length());
		// }
		try {
			LCSAlgrithom subSq = new LCSAlgrithom();
			String t1 = news.getContent().trim();
			String t2 = getcontent(map, title);
			System.out.println("自研方式-->正文内容-->" + getcontent(map, title));
			int sb = subSq.calculateLCS(t1, t2).length;
			if (sb * 1.0 / t2.length() >= yuzhi) {
				System.out.println("抽取成功！抽取率为：" + (int) (sb * 1.0 / t2.length() * 100) + "%");
			} else {
				System.out.println("抽取失败！抽取率为：" + (int) (sb * 1.0 / t2.length() * 100) + "%");
			}
		} catch (NullPointerException e) {
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

	/**
	 * 去除html标签
	 * 
	 * @param html
	 *            请求获得的html文本
	 * @return 纯文本
	 */
	public static String deleteLabel(String html) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
		String regEx_anno = "(?is)<!--[\\s\\S]*?-->"; // html注释
		html = html.replaceAll(regEx_script, "");
		html = html.replaceAll(regEx_style, "");
		html = html.replaceAll(regEx_html, "");
		html = html.replace(regEx_anno, "");
		html = html.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");// 去除空白行
		html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
		html = html.replaceAll("&.{2,5};|&#.{2,5};", " "); // remove special char
		html = html.replaceAll("(?is)<.*?>", "");// 移除所有标签
		return html.trim();
	}

	/**
	 * 将纯文本按BLOCKS分块
	 * 
	 * @param text
	 *            纯文本
	 * @return 分块后的map集合,键即为块号,值为块内容
	 */
	public static Map<Integer, String> splitBlock(String text) {
		Map<Integer, String> groupMap = new HashMap<Integer, String>();
		ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(bais));
		String line = null, blocksLine = "";
		int theCount = 0, groupCount = 0;// 1.记录每次添加的行数；2.记录块号
		try {
			while ((line = br.readLine()) != null) {
				String cc1 = line, cc2 = "";
				cc2 = line.replaceAll("[。]", "");
				line = cc1;
				if (line.length() > MIN_LENGTH && (line.length() - cc2.length() >= 1)) {
					if (theCount <= BLOCKS) {
						blocksLine += line.trim();
						theCount++;
					} else {
						groupMap.put(groupCount, blocksLine); // 块号和对应的值
						groupCount++;
						blocksLine = line.trim();
						theCount = 1;
					}
				}
//				if (line.contains("相关阅读") || line.contains("相关新闻") || line.contains("相关时政新闻") || line.contains("相关报道")
//						|| line.contains("延伸阅读") || line.contains("推荐阅读") || line.contains("精品推荐")
//						|| line.contains("免责声明") || line.contains("博客推荐") || line.contains("相同作者阅读")
//						|| line.contains("热点推荐") || line.contains("精彩推荐") || line.contains("论坛热帖")
//						|| line.contains("专题推荐") || line.contains("相同主题阅读") || line.contains("更多报道")
//						|| line.contains("专栏报道") || line.contains("查看更多") || line.contains("热点排行")
//						|| line.contains("编辑推荐") || line.contains("点击排行") || line.contains("阅读排行")
//						|| line.contains("阅读推荐") || line.contains("网站热点") || line.contains("新闻排行")
//						|| line.contains("点击排名") || line.contains("网站声明") || line.contains("版权声明")
//						|| line.contains("关于我们|") || line.contains("频道精选") || line.contains("END")) {
//					break;
//				}
			}
			if (theCount != 0) {// 加上没凑齐的给定块数
				groupMap.put(groupCount, blocksLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return groupMap;
	}

	public static String getcontent(Map<Integer, String> map, String title) {
		LCSAlgrithom subSq = new LCSAlgrithom();
		title = title.replaceAll("[\\d+。！？；，、“” 	]", "");
		String context = "";
		int StartBlock = 0, EndBlock = map.size() - 1;
		for (int i = 0; i <= map.size() / 2; i++) {// 正向搜索正文开始行块号！
			OutputVO output = subSq.calculateLCS(map.get(i), title);
			if (output.length >= 3) {
				StartBlock = i;
				break;
			}
		}
		for (int k = (map.size() - 1); k >= map.size() / 2; k--) {// 逆向搜索正文结束行块号！
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
		return context.replaceAll("　　", "");
	}

	/**
	 * 根据网页内容获取页面编码 case : 适用于可以直接读取网页的情况(例外情况:一些博客网站禁止不带User-Agent信息的访问请求)
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
