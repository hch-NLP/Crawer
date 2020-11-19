package contentextractor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class Mytest10 {
	public static final String[] name = { "北京简称_京", "天津简称_津", "黑龙江简称_黑", "吉林简称_吉", "辽宁简称_辽", "河北简称_冀", "河南简称_豫",
			"山东简称_鲁", "山西简称_晋", "陕西简称_陕", "内蒙古简称_蒙", "宁夏简称_宁", "甘肃简称_甘", "新疆简称_新", "青海简称_青", "西藏简称_藏", "湖北简称_鄂",
			"安徽简称_皖", "江苏简称_苏", "上海简称_沪", "浙江简称_浙", "福建简称_闵", "湖南简称_湘", "江西简称_赣", "四川简称_川", "重庆简称_渝", "贵州简称_贵",
			"云南简称_云", "广东简称_粤", "广西简称_桂", "海南简称_琼", "香港简称_港", "澳门简称_澳", "台湾简称_台", "中国首都_北京", "英国首都_伦敦", "美国首都_华盛顿",
			"希腊首都_雅典", "波兰首都_华沙", "缅甸首都_仰光", "瑞士首都_伯尔尼", "法国首都_巴黎", "日本首都_东京", "韩国首都_首尔", "朝鲜首都_平壤", "德国首都_柏林",
			"泰国首都_曼谷", "越南首都_河内", "老挝首都_万象", "印度首都_新德里", "印度首都_新德里", "埃及首都_开罗", "智利首都_圣地亚哥", "秘鲁首都_利马", "北京省会_北京",
			"天津省会_天津", "河北省会_石家庄", "山西省会_太原", "内蒙古省会_呼和浩特", "辽宁省会_沈阳", "吉林省会_长春", "黑龙江省会_哈尔滨", "上海省会_上海", "江苏省会_南京",
			"浙江省会_杭州", "安徽省会_合肥", "福建省会_福州", "江西省会_南昌", "山东省会_济南", "河南省会_郑州", "湖北省会_武汉", "湖南省会_长沙", "广东省会_广州",
			"广西省会_南宁", "海南省会_海口", "重庆省会_重庆", "四川省会_成都", "贵州省会_贵阳", "云南省会_昆明", "西藏省会_拉萨", "陕西省会_西安", "甘肃省会_兰州",
			"青海省会_西宁", "宁夏省会_银川", "新疆省会_乌鲁木齐", "篮球归属项目_体育", "游泳归属项目_体育", "跳高归属项目_体育", "跳远归属项目_体育", "跨栏归属项目_体育",
			"铅球归属项目_体育", "股票行业归属_金融", "基金归属项目_金融", "债券归属项目_金融", "期货归属项目_金融", "手术归属项目_医疗", "外科归属项目_医疗", "内科归属项目_医疗",
			"癌症归属项目_医疗", "肺炎归属项目_医疗", "1+2=_3", "1+1=_2" , "1*2=_2", "1+5=_6", "1+9=_10", "10-7=_3", "3*3=_9"};
	public static Random random = new Random();

	public static int r(int min, int max) {
		int num = 0;
		num = random.nextInt(max - min) + min;
		return num;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// 在内存中创建一副图片
		int index = (int) (Math.random() * name.length);
		String Q = name[index].split("_")[0];
		String A = name[index].split("_")[1];
		int w = Q.length() * 36;
		int h = 58;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		// 在图片上画一个矩形当背景
		Graphics g = img.getGraphics();
		g.setColor(new Color(r(200, 255), r(200, 255), r(200, 255)));
		g.fillRect(0, 0, w, h);
		g.setColor(new Color(r(50, 80), r(50, 80), r(50, 80)));
		g.setFont(new Font("黑体", Font.PLAIN, 32));
		g.drawString(String.valueOf(Q), 10 + 0 * 30, r(h - 30, h));
		// 画随机线
		for (int i = 0; i < 22; i++) {
			g.setColor(new Color(r(50, 180), r(50, 180), r(50, 180)));
			g.drawLine(r(0, w), r(0, h), r(0, w), r(0, h));
		}
		// 把内存中创建的图像输出到文件中
		File file = new File("C:\\Users\\lenovo\\Desktop\\" + A + ".png");
		ImageIO.write(img, "png", file);
		System.out.println(Q + "-->" + A);
	}
}
