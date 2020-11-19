package kd.com;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawerTest {
	public static void main(String[] args) throws IOException {
		FileOutputStream fos = new FileOutputStream("C:/Users/lenovo/Desktop/股票名称信息表.txt");
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		for (int j = 2; j < 179; j++) {//首页的是单独爬取的
			Document doc = Jsoup.connect("http://www.yz21.org/stock/info/stocklist_" + j + ".html").get();// stocklist_2.html
			Elements links = doc.select("[class=PindaoColumn]");
			String name[] = null;
			for (Element e : links) {
				// System.out.println(e.getElementsByTag("td").select("[class=content]").text().trim());
				Elements namenew = e.getElementsByTag("tr");
				for (Element e1 : namenew) {
					System.out.println(e1.getElementsByTag("td").text().trim());
					osw.write(e1.getElementsByTag("td").text().trim()+"\n");
					osw.flush();
				}
			}
		}
		fos.close();
		osw.close();
	}
}
