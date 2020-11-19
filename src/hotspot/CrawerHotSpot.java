package hotspot;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawerHotSpot {

	public static void main(String[] args) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("C:/Users/lenovo/Desktop/汽车.txt");
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(fos, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		Document doc = null;
		try {
			doc = Jsoup.connect("http://top.baidu.com/buzz?b=1540&c=18&fr=topcategory_c18").get();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Elements links = doc.select("[class=keyword]");
		for (Element e : links) {
			Elements namenew = e.getElementsByTag("a");
			for (Element e1 : namenew) {
				System.out.println("汽车：" + e1.text().trim() + "   URL：" + e1.attr("href"));
				try {
					osw.write("汽车：" + e1.text().trim() + "   URL：" + e1.attr("href")+ "\n");
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				try {
					osw.flush();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				break;
			}
		}
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			osw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("今日热点话题爬取任务已完成！");
	}

}
