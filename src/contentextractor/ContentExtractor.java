package contentextractor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

public class ContentExtractor {

	protected Document doc;

	ContentExtractor(Document doc) {
		this.doc = doc;
	}

	protected Map<Element, CountInfo> infoMap = new HashMap<Element, CountInfo>();

	class CountInfo {

		int textCount = 0;
		int linkTextCount = 0;
		int tagCount = 0;
		int linkTagCount = 0;
		double density = 0;
		double densitySum = 0;
		double score = 0;
		int pCount = 0;
		ArrayList<Integer> leafList = new ArrayList<Integer>();

	}

	protected void clean() {
		doc.select("script,noscript,style,iframe,br").remove();
	}

	protected CountInfo computeInfo(Node node) {

		if (node instanceof Element) {
			Element tag = (Element) node;

			CountInfo countInfo = new CountInfo();
			for (Node childNode : tag.childNodes()) {
				CountInfo childCountInfo = computeInfo(childNode);
				countInfo.textCount += childCountInfo.textCount;
				countInfo.linkTextCount += childCountInfo.linkTextCount;
				countInfo.tagCount += childCountInfo.tagCount;
				countInfo.linkTagCount += childCountInfo.linkTagCount;
				countInfo.leafList.addAll(childCountInfo.leafList);
				countInfo.densitySum += childCountInfo.density;
				countInfo.pCount += childCountInfo.pCount;
			}
			countInfo.tagCount++;
			String tagName = tag.tagName();
			if (tagName.equals("a")) {
				countInfo.linkTextCount = countInfo.textCount;
				countInfo.linkTagCount++;
			} else if (tagName.equals("p")) {
				countInfo.pCount++;
			}

			int pureLen = countInfo.textCount - countInfo.linkTextCount;
			int len = countInfo.tagCount - countInfo.linkTagCount;
			if (pureLen == 0 || len == 0) {
				countInfo.density = 0;
			} else {
				countInfo.density = (pureLen + 0.0) / len;
			}

			infoMap.put(tag, countInfo);
			return countInfo;
		} else if (node instanceof TextNode) {
			TextNode tn = (TextNode) node;
			CountInfo countInfo = new CountInfo();
			String text = tn.text();
			int len = text.length();
			countInfo.textCount = len;
			countInfo.leafList.add(len);
			return countInfo;
		} else {
			return new CountInfo();
		}
	}

	protected double computeScore(Element tag) {
		CountInfo countInfo = infoMap.get(tag);
		double var = Math.sqrt(computeVar(countInfo.leafList) + 1);
		double score = Math.log(var) * countInfo.densitySum
				* Math.log(countInfo.textCount - countInfo.linkTextCount + 1) * Math.log10(countInfo.pCount + 2);
		return score;
	}

	protected double computeVar(ArrayList<Integer> data) {
		if (data.size() == 0) {
			return 0;
		}
		if (data.size() == 1) {
			return data.get(0) / 2;
		}
		double sum = 0;
		for (Integer i : data) {
			sum += i;
		}
		double ave = sum / data.size();
		sum = 0;
		for (Integer i : data) {
			sum += (i - ave) * (i - ave);
		}
		sum = sum / data.size();
		return sum;
	}

	public Element getContentElement() throws Exception {
		clean();
		computeInfo(doc.body());
		double maxScore = 0;
		Element content = null;
		for (Map.Entry<Element, CountInfo> entry : infoMap.entrySet()) {
			Element tag = entry.getKey();
			if (tag.tagName().equals("a") || tag == doc.body()) {
				continue;
			}
			double score = computeScore(tag);
			if (score > maxScore) {
				maxScore = score;
				content = tag;
			}
		}
		if (content == null) {
			throw new Exception("extraction failed");
		}
		return content;
	}

	public News getNews() throws Exception {
		News news = new News();
		Element contentElement;
		try {
			contentElement = getContentElement();
			news.setContentElement(contentElement);
		} catch (Exception ex) {
			// LOG.info("news content extraction failed,extraction abort", ex);
			throw new Exception(ex);
		}

		if (doc.baseUri() != null) {
			news.setUrl(doc.baseUri());
		}

		try {
			news.setDate(getTime(doc));
			// news.setTime(getTime(doc,contentElement));
		} catch (Exception ex) {
			// LOG.info("news title extraction failed", ex);
		}
		try {
			news.setTitle(getTitle(contentElement));
		} catch (Exception ex) {
			// LOG.info("title extraction failed", ex);
		}
		return news;
	}

	protected Date getTime(Document doc) throws Exception {
		String regex = "([1-2][0-9]{3})+[|年|-|-|/]{1,5}?([0-1]?[0-9])+[|月|-|-|/]{1,5}?([0-3]?[0-9])+[|日|号|\\s]+{1,5}?([0-2]?[0-9])+[|:|时|小时|：]{1,5}?([0-5]?[0-9])[^0-9]{1,5}?([0-5]?[0-9])?";
		Pattern pattern = Pattern.compile(regex);
		String currentHtml = doc.outerHtml();
		Matcher matcher = pattern.matcher(currentHtml);
		String s = "";
		if (matcher.find()) {
			String t = matcher.group();
			t = t.replaceAll("年", "-").replaceAll("/", "-").replaceAll("-", "-").replaceAll("月", "-")
					.replaceAll("日", "-").replaceAll(":", "-").replaceAll("：", "-").replaceAll("时", "-")
					.replaceAll("小时", "-").replaceAll("分", "-").replaceAll("分钟", "-").replaceAll(" ", "-")
					.replaceAll("--", "-").replaceAll("[^-^0-9]", "");
			String[] M = null;
			M = t.split("-");
			for (int k = 1; k < M.length; k++) {
				if (M[k].length() == 0) {
					M[k] = "01";// 01-12之间
				}
			}
			if (M.length == 4) {
				s = M[0] + "-" + M[1] + "-" + M[2] + " " + M[3] + ":00:00";
			} else if (M.length == 5) {
				s = M[0] + "-" + M[1] + "-" + M[2] + " " + M[3] + ":" + M[4] + ":00";
			} else if (M.length == 6) {
				s = M[0] + "-" + M[1] + "-" + M[2] + " " + M[3] + ":" + M[4] + ":" + M[5];
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = null;
			try {
				date = format.parse(s);
				return date;
			} catch (ParseException e) {
			}
		}
		try {
			return getDate(doc);
		} catch (Exception ex) {
			throw new Exception("Date not found");
		}
	}

	protected Date getDate(Document doc) throws Exception {
		String regex = "([1-2][0-9]{3})+[|年|-|-|/]{1,5}+([0-1]?[0-9])+[|月|-|-|/]{1,5}+([0-3]?[0-9])+";
		Pattern pattern = Pattern.compile(regex);
		String s = null;
		String currentHtml = doc.outerHtml();
		Matcher matcher = pattern.matcher(currentHtml);
		if (matcher.find()) {
			s = matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3) + " 00:00:00";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			try {
				date = format.parse(s);
				return date;
			} catch (ParseException e) {
			}
		}
		try {
			return getMonth_Day(doc);
		} catch (Exception ex) {
			throw new Exception("Date not found");
		}
	}

	protected Date getMonth_Day(Document doc) throws Exception {
		String regex = "(\\d{1,2})[|月|-|-|/]+(\\d{1,2})+";
		Pattern pattern = Pattern.compile(regex);
		String s = null;
		String currentHtml = doc.outerHtml();
		Matcher matcher = pattern.matcher(currentHtml);
		if (matcher.find()) {
			s = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
			s = s + "-" + matcher.group(1) + "-" + matcher.group(2) + " 00:00:00";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			try {
				date = format.parse(s);
				return date;
			} catch (ParseException e) {
			}
		}
		throw new Exception("Date not found");
	}

	protected double strSim(String a, String b) {
		int len1 = a.length();
		int len2 = b.length();
		if (len1 == 0 || len2 == 0) {
			return 0;
		}
//		double ratio;
//		if (len1 > len2) {
//			ratio = (len1 + 0.0) / len2;
//		} else {
//			ratio = (len2 + 0.0) / len1;
//		}
//		if (ratio >= 3) {
//			return 0;
//		}
		return (lcs(a, b) + 0.0) / Math.max(len1, len2);
	}

	protected String getTitle(final Element contentElement) throws Exception {
		/*final ArrayList<Element> titleList = new ArrayList<Element>();*///注意判断标题是否为空？
		final ArrayList<String> titleList = new ArrayList<String>();
		final ArrayList<Double> titleSim = new ArrayList<Double>();
		final AtomicInteger contentIndex = new AtomicInteger();
		final String metaTitle = doc.title().trim();
		if (!metaTitle.isEmpty()) {
			doc.body().traverse(new NodeVisitor() {
				@Override
				public void head(Node node, int i) {
					if (node instanceof Element) {
						Element tag = (Element) node;
						if (tag == contentElement) {
							contentIndex.set(titleList.size());
							return;
						}
						String tagName = tag.tagName();
						if (Pattern.matches("h[1-6]", tagName)) {
							String title = tag.text().trim();
							double sim = strSim(title, metaTitle);
							titleSim.add(sim);
							/*titleList.add(tag);*/
							titleList.add(tag.text());
						}
					}
				}

				@Override
				public void tail(Node node, int i) {
				}
			});
			int index = contentIndex.get();
			if (index > 0) {
				double maxScore = 0;
				int maxIndex = -1;
				for (int i = 0; i < index; i++) {
					double score = (i + 1) * titleSim.get(i);//(i + 1) * 移除
					if (score > maxScore) {//>=
						maxScore = score;
						maxIndex = i;
					}
				}
				if (maxIndex != -1) {
					/*return titleList.get(maxIndex).text();*/
					return titleList.get(maxIndex);
				}
			}
		}

		Elements titles = doc.body().select("*[id^=title],*[id$=title],*[class^=title],*[class$=title]");
		if (titles.size() > 0) {
			String title = titles.first().text();
			if (title.length() > 5 && title.length() < 40) {
				return title;
			}
		}
		try {
			return getTitleByEditDistance(contentElement);
		} catch (Exception ex) {
			throw new Exception("标题抽取失败！");
		}

	}

	protected String getTitleByEditDistance(Element contentElement) throws Exception {
		final String metaTitle = doc.title();

		final ArrayList<Double> max = new ArrayList<Double>();
		max.add(0.0);
		final StringBuilder sb = new StringBuilder();
		doc.body().traverse(new NodeVisitor() {

			public void head(Node node, int i) {

				if (node instanceof TextNode) {
					TextNode tn = (TextNode) node;
					String text = tn.text().trim();
					double sim = strSim(text, metaTitle);
					if (sim > 0) {
						if (sim > max.get(0)) {
							max.set(0, sim);
							sb.setLength(0);
							sb.append(text);
						}
					}

				}
			}

			public void tail(Node node, int i) {
			}
		});
		if (sb.length() > 0) {
			return sb.toString();
		}
		throw new Exception();

	}

	protected int lcs(String x, String y) {

		int M = x.length();
		int N = y.length();
		if (M == 0 || N == 0) {
			return 0;
		}
		int[][] opt = new int[M + 1][N + 1];

		for (int i = M - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				if (x.charAt(i) == y.charAt(j)) {
					opt[i][j] = opt[i + 1][j + 1] + 1;
				} else {
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
				}
			}
		}

		return opt[0][0];

	}

	protected int editDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();

		int[][] dp = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}

		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}

		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);

				if (c1 == c2) {
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;

					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}

		return dp[len1][len2];
	}

	/* 输入Jsoup的Document，获取正文所在Element */
	public static Element getContentElementByDoc(Document doc) throws Exception {
		ContentExtractor ce = new ContentExtractor(doc);
		return ce.getContentElement();
	}

	/* 输入HTML，获取正文所在Element */
	public static Element getContentElementByHtml(String html) throws Exception {
		Document doc = Jsoup.parse(html);
		return getContentElementByDoc(doc);
	}

	/* 输入HTML和URL，获取正文所在Element */
	public static Element getContentElementByHtml(String html, String url) throws Exception {
		Document doc = Jsoup.parse(html, url);
		return getContentElementByDoc(doc);
	}

	/* 输入URL，获取正文所在Element */
	public static Element getContentElementByUrl(String url) throws Exception {
		Response r = Jsoup.connect(url).execute();
		String html = r.body();
		return getContentElementByHtml(html, url);
	}
	/* 输入Jsoup的Document，获取正文文本 */
	public static String getContentByDoc(Document doc) throws Exception {
		ContentExtractor ce = new ContentExtractor(doc);
		return ce.getContentElement().text();
	}

	/* 输入HTML，获取正文文本 */
	public static String getContentByHtml(String html) throws Exception {
		Document doc = Jsoup.parse(html);
		return getContentElementByDoc(doc).text();
	}

	/* 输入HTML和URL，获取正文文本 */
	public static String getContentByHtml(String html, String url) throws Exception {
		Document doc = Jsoup.parse(html, url);
		return getContentElementByDoc(doc).text();
	}

	/* 输入URL，获取正文文本 */
	public static String getContentByUrl(String url) throws Exception {
		Response r = Jsoup.connect(url).execute();
		String html = r.body();
		return getContentByHtml(html, url);
	}

	/* 输入Jsoup的Document，获取结构化新闻信息 */
	public static News getNewsByDoc(Document doc) throws Exception {
		ContentExtractor ce = new ContentExtractor(doc);
		return ce.getNews();
	}

	/* 输入HTML，获取结构化新闻信息 */
	public static News getNewsByHtml(String html) throws Exception {
		Document doc = Jsoup.parse(html);
		return getNewsByDoc(doc);
	}

	/* 输入HTML和URL，获取结构化新闻信息 */
	public static News getNewsByHtml(String html, String url) throws Exception {
		Document doc = Jsoup.parse(html, url);
		return getNewsByDoc(doc);
	}

	/* 输入URL，获取结构化新闻信息 */
	public static News getNewsByUrl(String url, String Encoding) throws Exception {
		Response r = Jsoup.connect(url).execute();
		// String html = r.body();
		String html = new String(r.bodyAsBytes(), Encoding);
		return getNewsByHtml(html, url);
	}

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		News news = ContentExtractor.getNewsByUrl("http://news.sina.com.cn/o/2018-06-20/doc-iheauxwa0068237.shtml", "utf-8");
		System.out.println(news.getUrl());
		System.out.println(news.getTitle());
		System.out.println(news.getDate());
		System.out.println(news.getContent().trim());
		long endTime = System.currentTimeMillis();
		System.out.println("抽取耗时：" + (endTime - startTime) / 1000.0 + "(S)");
	}

}
