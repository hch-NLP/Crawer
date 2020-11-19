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
	public static final String[] name = { "�������_��", "�����_��", "���������_��", "���ּ��_��", "�������_��", "�ӱ����_��", "���ϼ��_ԥ",
			"ɽ�����_³", "ɽ�����_��", "�������_��", "���ɹż��_��", "���ļ��_��", "������_��", "�½����_��", "�ຣ���_��", "���ؼ��_��", "�������_��",
			"���ռ��_��", "���ռ��_��", "�Ϻ����_��", "�㽭���_��", "�������_��", "���ϼ��_��", "�������_��", "�Ĵ����_��", "������_��", "���ݼ��_��",
			"���ϼ��_��", "�㶫���_��", "�������_��", "���ϼ��_��", "��ۼ��_��", "���ż��_��", "̨����_̨", "�й��׶�_����", "Ӣ���׶�_�׶�", "�����׶�_��ʢ��",
			"ϣ���׶�_�ŵ�", "�����׶�_��ɳ", "����׶�_����", "��ʿ�׶�_������", "�����׶�_����", "�ձ��׶�_����", "�����׶�_�׶�", "�����׶�_ƽ��", "�¹��׶�_����",
			"̩���׶�_����", "Խ���׶�_����", "�����׶�_����", "ӡ���׶�_�µ���", "ӡ���׶�_�µ���", "�����׶�_����", "�����׶�_ʥ���Ǹ�", "��³�׶�_����", "����ʡ��_����",
			"���ʡ��_���", "�ӱ�ʡ��_ʯ��ׯ", "ɽ��ʡ��_̫ԭ", "���ɹ�ʡ��_���ͺ���", "����ʡ��_����", "����ʡ��_����", "������ʡ��_������", "�Ϻ�ʡ��_�Ϻ�", "����ʡ��_�Ͼ�",
			"�㽭ʡ��_����", "����ʡ��_�Ϸ�", "����ʡ��_����", "����ʡ��_�ϲ�", "ɽ��ʡ��_����", "����ʡ��_֣��", "����ʡ��_�人", "����ʡ��_��ɳ", "�㶫ʡ��_����",
			"����ʡ��_����", "����ʡ��_����", "����ʡ��_����", "�Ĵ�ʡ��_�ɶ�", "����ʡ��_����", "����ʡ��_����", "����ʡ��_����", "����ʡ��_����", "����ʡ��_����",
			"�ຣʡ��_����", "����ʡ��_����", "�½�ʡ��_��³ľ��", "���������Ŀ_����", "��Ӿ������Ŀ_����", "���߹�����Ŀ_����", "��Զ������Ŀ_����", "����������Ŀ_����",
			"Ǧ�������Ŀ_����", "��Ʊ��ҵ����_����", "���������Ŀ_����", "ծȯ������Ŀ_����", "�ڻ�������Ŀ_����", "����������Ŀ_ҽ��", "��ƹ�����Ŀ_ҽ��", "�ڿƹ�����Ŀ_ҽ��",
			"��֢������Ŀ_ҽ��", "���׹�����Ŀ_ҽ��", "1+2=_3", "1+1=_2" , "1*2=_2", "1+5=_6", "1+9=_10", "10-7=_3", "3*3=_9"};
	public static Random random = new Random();

	public static int r(int min, int max) {
		int num = 0;
		num = random.nextInt(max - min) + min;
		return num;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// ���ڴ��д���һ��ͼƬ
		int index = (int) (Math.random() * name.length);
		String Q = name[index].split("_")[0];
		String A = name[index].split("_")[1];
		int w = Q.length() * 36;
		int h = 58;
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		// ��ͼƬ�ϻ�һ�����ε�����
		Graphics g = img.getGraphics();
		g.setColor(new Color(r(200, 255), r(200, 255), r(200, 255)));
		g.fillRect(0, 0, w, h);
		g.setColor(new Color(r(50, 80), r(50, 80), r(50, 80)));
		g.setFont(new Font("����", Font.PLAIN, 32));
		g.drawString(String.valueOf(Q), 10 + 0 * 30, r(h - 30, h));
		// �������
		for (int i = 0; i < 22; i++) {
			g.setColor(new Color(r(50, 180), r(50, 180), r(50, 180)));
			g.drawLine(r(0, w), r(0, h), r(0, w), r(0, h));
		}
		// ���ڴ��д�����ͼ��������ļ���
		File file = new File("C:\\Users\\lenovo\\Desktop\\" + A + ".png");
		ImageIO.write(img, "png", file);
		System.out.println(Q + "-->" + A);
	}
}