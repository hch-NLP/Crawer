package kd.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TextProcess {

	public static void main(String[] args) {
		try {
			FileOutputStream fos = new FileOutputStream("C:/Users/lenovo/Desktop/股票名称全称.txt");
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			FileOutputStream fos1 = new FileOutputStream("C:/Users/lenovo/Desktop/股票名称简称.txt");
			OutputStreamWriter osw1 = new OutputStreamWriter(fos1, "UTF-8");
			File file = new File("C:/Users/lenovo/Desktop/股票名称.txt");
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				String aa[] = null;
				if (s.contains("	")) {// Tab键分割
					aa = s.replaceAll(" ", "").split("	");
					//System.out.println(aa[0]);
					osw.write(aa[1] + " " + "/Ni" + " "+ "100"+ "\n");
					osw.flush();
					osw1.write(aa[0] + " " + "/Ni" + " "+ "100"+ "\n");
					osw1.flush();
				}
			}
			isr.close();
			br.close();
			fos.close();
			osw.close();
			fos1.close();
			osw1.close();
			System.out.println("Finished!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
