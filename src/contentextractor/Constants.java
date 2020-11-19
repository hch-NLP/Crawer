package contentextractor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class Constants {

	
	public final static String HTTP_GET = "GET";
	public final static String HTTP_POST = "POST";
	public final static String HTTP_HEAD = "HEAD";
	public final static String HTTP_PUT = "PUT";
	public final static String HTTP_DELETE = "DELETE";
	public final static String HTTP_TRACE = "TRACE";
	
	
	public final static String HTTP_HEADER_USERAGENT="User-Agent";
	public final static String HTTP_HEADER_REFERER="Referer";
	
	public final static String HTTP_ACCEPT_ENCODING="Accept-Encoding";
	public final static String VALUE_ACCEPT_ENCODING="gzip, deflate";
	
	public final static String HTTP_ACCEPT="Accept";
	public final static String VALUE_ACCEPT="text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	
	
	public final static String HTTP_ACCEPT_LANGUAGE="Accept-Language";
	public final static String VALUE_HTTP_ACCEPT_LANGUAGE="Zh-CN,zh;q=0.8,en-gb;q=0.8,en;q=0.7";
	
	
    public static final int HTTP_CODE_200 = 200;

    //***************************************************************
    //任务状态定义
    //***************************************************************
	public final static Integer STATE_DUPLICATION = 0;
	public final static Integer STATE_NEW = 1;
	public final static Integer STATE_START = 2;
	public final static Integer STATE_DOWNLOAD = 3;
	public final static Integer STATE_EXTRACT = 4;
	public final static Integer STATE_RESUTL = 5;
	public final static Integer STATE_END = 6;
	public final static Integer STATE_ERROR = 0;
	public final static Integer STATE_ERROR_DL = -1;
	public final static Integer STATE_ERROR_ET = -2;
	public final static Integer STATE_ERROR_R = -3;
	
    //***************************************************************
    //日期格式定义
    //***************************************************************
	public static final String[] DF_STRING_YYYY = {"yyyy"};
    public static final String[] DF_STRING_YYYYMM = {"yyyy-MM","yyyyMM","yyyy/MM"};
    public static final String[] DF_STRING_YYYYMMDD = {"yyyyMMdd","yyyy-MM-dd","yyyy/MM/dd"};
    public static final String[] DF_STRING_YYYYMMDDHH = {"yyyyMMdd","yyyy-MM-dd","yyyy/MM/dd"};
    public static final String[] DF_STRING_YYYYMMDDHHMM = {"yyyyMMddHHmm","yyyy-MM-dd HH:mm","yyyy/MM/dd HH:mm"};
    public static final String[] DF_STRING_YYYYMMDDHHMMSS = {"yyyyMMddHHmmss","yyyy-MM-dd HH:mm:ss","yyyy/MM/dd HH:mm:ss"};
    
    public static final String[] DF_STRING_DATETIME = {"yyyyMMddHHmmss","yyyyMMdd"};
    

    public static final String DF_DATE_YYYYMMDD = "yyyyMMdd";
    public static final String DF_DATE_YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static final String DF_DATE_MMHHDDMMYYYY = "mmHHddMMyyyy";
    
    public static final String DF_DATE_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	
	
    //***************************************************************
    //字段定义
    //***************************************************************
	
	public final static String DB_NAME = "cloud-platform";
	
	
	public final static String TABLE_SITE = "site";
	public final static String TABLE_TYPEDICTIONARY = "typeDictionary";
	public final static String TABLE_CHANNEL = "channel";
	public final static String TABLE_INDEXTASK = "indexTask";
	public final static String TABLE_CONTENTTASK = "contentTask";
	public final static String TABLE_RESULT = "info";
	public final static String TABLE_URLS = "urls";
	public static final String HBASE_INFO = "INFO";
	
	public final static String DB_MG_SET = "$set";
	public final static String DB_MG_IN = "$in";
	public final static String DB_MG_OR = "$or";
	
	public final static String DB_MG_URI="mongodb.uri";
	public final static String DB_MG_DB="mongodb.db";

	public final static String FIELD_ID = "_id";
	public final static String FIELD_NAME = "name";
	public final static String FIELD_VALUE = "Value";
	public final static String FIELD_CHANNELTYPEID = "type";
	public final static String FIELD_TASKTYPE = "taskType";
	public final static String FIELD_CONTENTTYPE = "contentType";
	public final static String FIELD_ORGTYPE = "orgType";
	public final static String FIELD_DOWNLOAD_TYPE = "downloadType";
	public final static String FIELD_DICID = "dicId";
	public final static String FIELD_STIETYPEID = "stieTypeId";
	
	
	public final static String FIELD_REQUESTMETHOD = "requestMethod";
	public final static String FIELD_REQUESTTIMEOUT = "requestTimeout";
	public final static String FIELD_REQUESTRETRYCOUNT = "requestRetryCount";
	public final static String FIELD_REQUESTRETRYTIME = "requestRetryTime";
	
	public final static String FIELD_TASKID = "taskId";
	public final static String FIELD_WORKDAY = "workday";
	public final static String FIELD_CRAWLERCYCLE = "cycle";
	public final static String FIELD_NEXTCRAWLERTIME = "nextCrawlerTime";
	public final static String FIELD_ENCODING = "encoding";
	
	
	public final static String FIELD_HTML = "html";
	public final static String FIELD_CONTENT = "content";
	public final static String FIELD_LOGS = "logs";
	public final static String FIELD_CRAWLERID = "crawlerId";
	public final static String FIELD_PARSERID = "parserId";
	public final static String FIELD_EXCEPTION = "exception";
	public final static String FIELD_URLMATCHREGEX = "urlMatchRegex";
	
	public final static String FIELD_ALLCOUNT = "allCount";
	public final static String FIELD_ERRCOUNT = "errCount";
	public final static String FIELD_TOTALNUM = "totalNum";
	public final static String FIELD_LASTTOTALNUM = "lastTotalNum";
	public final static String FIELD_DAYEFFECTIVENUM = "dayEffectiveNum";
	public final static String FIELD_LASTEFFECTIVENUM = "lastEffectiveNum";
	
	public final static String FIELD_RETRYCOUNT = "retryCount";
	public final static String FIELD_LASTRETRYCOUNT = "lastRetryCount";
	
	
	public final static String FIELD_LASTENDCRAWLERTIME = "lastEndCrawlerTime";
	public final static String FIELD_LASTSTARTCRAWLERTIME = "lastStartCrawlerTime";
	
	public final static String FIELD_LASTMODIFILEDTIME = "lastModifiledTime";
	
	
	public final static String FIELD_SITEID = "siteId";
	public final static String FIELD_CHANNELID = "channelId";
	public final static String FIELD_CHANNELURL = "channelUrl";
	public final static String FIELD_URL = "url";
	
	public final static String FIELD_STATE = "state";
	public final static String FIELD_CREATETIME = "createTime";
	public final static String FIELD_STARTCRAWLERWORKTIME = "startCrawlerWorkTime";
	public final static String FIELD_ENDCRAWLERWORKTIME = "endCrawlerWorkTime";
	
	
	public final static String FIELD_STARTDOWNLOADTASKTIME = "startDownloadTaskTime";
	public final static String FIELD_ENDDOWNLOADTASKTIME = "endDownloadTaskTime";
	public final static String FIELD_STARTDOWNLOADWORKTIME = "startDownloadWorkTime";
	public final static String FIELD_ENDDOWNLOADWORKTIME = "endDownloadWorkTime";
	
	public final static String FIELD_STARTPARSERTASKTIME = "startParserTaskTime";
	public final static String FIELD_ENDPARSERTASKTIME = "endParserTaskTime";
	public final static String FIELD_STARTPARSERWORKTIME = "startParserWorkTime";
	public final static String FIELD_ENDPARSERWORKTIME = "endParserWorkTime";
	
	public final static String FIELD_STARTRESULTTASKTIME = "startResultTaskTime";
	public final static String FIELD_ENDRESULTTASKTIME = "endResultTaskTime";
	public final static String FIELD_STARTRESULTWORKTIME = "startResultWorkTime";
	public final static String FIELD_ENDRESULTWORKTIME = "endResultWorkTime";
	
	public final static String FIELD_TITLE = "title";
	public final static String FIELD_AUTHOR = "author";
	public final static String FIELD_PUBLISHTIME = "publishTime";
	public final static String FIELD_ORIGIN = "origin";
	public final static String FIELD_EXTRACTER = "extracter";
	
	public final static String TIME_DEFAULT_VALUE="12";
	public final static String ZREO_D = "00";
	public final static String ZREO = "0";
	public final static String ONE = "1";
	public final static String STRIKE = "-";
	public final static String COLON = ":";
	public final static String UNDERLINE = "_";
	public final static String COMMA = ",";
	public final static String EMPTY = "";
	public final static String BLANK = " ";
	public final static String TAB = "\\t";
	
	public final static String NEWLINE = "\r\n";
	
	
	public final static String PERIOD_GB2312 = "。";
	public final static String LEFT_BRACKET_GB2312 = "（";
	public final static String RIGHT_BRACKET_GB2312 = "）";
	
	
	public final static String BLANK_DOUBLE = "  ";
	public final static String EQUALS = "=";
	public final static String URLMATCHREGEX_SPLIT = "\\|";
	public final static String URL_PATH_SPLIT = "/";

	
	public final static String TRUE = "true";
	public final static String FALSE = "false";
	
	
	public final static String MIMETYPE="text/html";
	public final static String CHARSET_DEFAULT_NAME="GB2312";

	
	public final static String HTML_TAG_META="meta";
	public final static String HTML_TAG_CONTENT="content";
	public final static String HTML_TAG_CHARSET="charset";
	
	
	
	
	
	
	
	/**
	 * 主要属性列簇
	 */
	public static final byte[] FAMILY_INFO_BASE = "c".getBytes();
	/**
	 * 附件音视频列簇
	 */
	public static final byte[] FAMILY_INFO_ATTACH = "a".getBytes();
	/**
	 * 分析挖掘列簇
	 */
	public static final byte[] FAMILY_INFO_MINING = "m".getBytes();
	
	
	/**
	 * 字段定义
	 */
	public static enum InfoField {
		//*************基本数据列簇 c *****************
		ID("id", "_id", "主键", Long.class, FAMILY_INFO_BASE),
		MEDIA_TYPE("mediaType", "m", "媒体类型", String.class, FAMILY_INFO_BASE),
		TITLE("title", "t", "标题", String.class, FAMILY_INFO_BASE),
		AUTHOR("author", "a", "作者", String.class, FAMILY_INFO_BASE),
		CONTENT("content", "c", "正文", String.class, FAMILY_INFO_BASE),
		URL("url", "u", "资讯的URL", String.class, FAMILY_INFO_BASE),
		PUBLISH_TIME("publishTime", "p", "发表时间", String.class, FAMILY_INFO_BASE),
		LOCATION("location", "l", "地点（原文中标注的地点）", String.class, FAMILY_INFO_BASE),
		GEO("geo", "g", "经纬度(lng,lat)", String.class, FAMILY_INFO_BASE),
		FROM_SITE("fromSite", "f", "转载网站名称", String.class, FAMILY_INFO_BASE),
		VIEW_COUNT("viewCount", "v", "点击数（查看数）", Integer.class, FAMILY_INFO_BASE),
		COMMENT_COUNT("commentCount", "c1", "评论数（回复数）", Integer.class, FAMILY_INFO_BASE),
		LIKE_COUNT("likeCount", "l1", "点赞数", Integer.class, FAMILY_INFO_BASE),
		SITE_ID("siteId", "s", "站点ID", String.class, FAMILY_INFO_BASE),
		SITE_NAME("siteName", "s1", "站点名称", String.class, FAMILY_INFO_BASE),
		SITE_TYPEID("siteTypeId", "s2", "站点类型", String.class, FAMILY_INFO_BASE),
		SITE_TYPENAME("siteTypeName", "s2", "站点类型", String.class, FAMILY_INFO_BASE),
		CHANNEL_ID("channelId", "c2", "频道ID", String.class, FAMILY_INFO_BASE),
		CHANNEL_NAME("channelName", "c3", "频道名称", String.class, FAMILY_INFO_BASE),
		CHANNEL_TYPEID("channelTypeId", "c4", "频道类型ID", String.class, FAMILY_INFO_BASE),
		CHANNEL_TYPENAME("channelTypeName", "c2", "频道类型名称", String.class, FAMILY_INFO_BASE),
		CHANNEL_URL("channelUrl", "c5", "频道入口URL", String.class, FAMILY_INFO_BASE),
		COUNTRY("country", "c6", "站点所在国家", String.class, FAMILY_INFO_BASE),
		LANGUAGE("language", "l2", "语言", String.class, FAMILY_INFO_BASE),
		ENCODING("encoding", "e", "内容编码", String.class, FAMILY_INFO_BASE),
		CRAWLER_ID("crawlerId", "c7", "爬虫ID（表示哪个爬虫采集的）", String.class, FAMILY_INFO_BASE),
		CRAWLER_TYPE("crawlerType", "8", "爬虫类型", String.class, FAMILY_INFO_BASE),
		CREATE_TIME("createTime", "c9", "创建时间", Date.class, FAMILY_INFO_BASE),
		MODIFY_TIME("modifyTime", "m1", "修改时间", Date.class, FAMILY_INFO_BASE),
		
		//**************附件列簇 a *****************
		SOURCE_CODE("sourceCode", "s", "资讯源码", String.class, FAMILY_INFO_ATTACH),
		IMAGE_URL("imageUrl", "i", "资讯内图片的URL（多值）", String.class, FAMILY_INFO_ATTACH),
		IMAGE_STORE_PATH("imageStorePath", "i1", "图片存储路径（多值）", String.class, FAMILY_INFO_ATTACH),
		VIDEO_URL("videoUrl", "v", "资讯内音视频的URL（多值）", String.class, FAMILY_INFO_ATTACH),
		VIDEO_STORE_PATH("videoStorePath", "v1", "音视频存储路径（多值）", String.class, FAMILY_INFO_ATTACH),
		ATTACH_URL("attachUrl", "a", "资讯内附件的URL（多值）", String.class, FAMILY_INFO_ATTACH),
		ATTACH_STORE_PATH("attachStorePath", "a1", "附件存储路径（多值）", String.class, FAMILY_INFO_ATTACH),
		
		//***************分析挖掘列簇 m **************
		KEYWORD("keyword", "k", "关键词", String.class, FAMILY_INFO_MINING),
		ABSTRACT_TEXT("abstractText", "a", "摘要", String.class, FAMILY_INFO_MINING),
		POSITIVE_WORD("positiveWord", "p", "正面词", String.class, FAMILY_INFO_MINING),
		NEGATIVE_WORD("negativeWord", "n", "负面词", String.class, FAMILY_INFO_MINING),
		SETIMENT_SCORE("sentimentScore", "s", "情感值", Float.class, FAMILY_INFO_MINING),
		PEOPLE("people", "p1", "人物", String.class, FAMILY_INFO_MINING),
		PLACE("place", "p2", "地名", String.class, FAMILY_INFO_MINING),
		ORGANIZATION("organization", "o", "机构名称", String.class, FAMILY_INFO_MINING),
		
		;
		
		private String name;
		private String dbName;
		private String desc;
		private Class<?> type;
		private byte[] family;
		
		private static Map<String, InfoField> fields = new HashMap<>();
		
		static {
			for(InfoField field : InfoField.values()) {
				fields.put(field.dbName, field);
			}
		}
		
		private InfoField(String name, String dbName, String desc, Class<?> type, byte[] family) {
			this.name = name;
			this.dbName = dbName;
			this.desc = desc;
			this.type = type;
			this.family = family;
		}

		public String getName() {
			return name;
		}

		public String getDbName() {
			return dbName;
		}

		public String getDesc() {
			return desc;
		}

		public Class<?> getType() {
			return type;
		}
		
		public byte[] getFamily() {
			return family;
		}
		
		public static InfoField fromDbName(String dbName) {
			return fields.get(dbName);
		}

	}
	
	
	

}
