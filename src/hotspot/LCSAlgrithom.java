package hotspot;

public class LCSAlgrithom {
	/*
	 * Author: Chunhui H. Date: 2018-11-26 
	 * Algrithom function: remove news web
	 * noise information
	 */
	static String getCommonStrLength(String str1, String str2) {
        str1 = str1.toLowerCase();  
       str2 = str2.toLowerCase();  
       int len1 = str1.length();  
       int len2 = str2.length();  
       String min = null;  
       String max = null;  
       String target = null;
       min = len1 <= len2 ? str1 : str2;
       max = len1 >  len2 ? str1 : str2;
       //最外层：min子串的长度，从最大长度开始
       for (int i = min.length(); i >= 1; i--) {
           //遍历长度为i的min子串，从0开始
           for (int j = 0; j <= min.length() - i; j++) {  
               target = min.substring(j, j + i);  
               //遍历长度为i的max子串，判断是否与target子串相同，从0开始
               for (int k = 0; k <= max.length() - i; k++) {  
                   if (max.substring(k,k + i).equals(target)) {  
                       return target;  
                       //return i; 
                   }
               }
           }
       }  
       return "";  
}
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String title = "YXXXX";
		String[] content = {"XYXXXXYYX"};
		for (int i = 0; i < content.length; i++) {
			String s=getCommonStrLength(title,content[i]);
			if (s.length() >= 2) {
				System.out.println("#最长公共子串为:# " + s+" #长度为#：" + s.length());
			} else {
				System.out.println("广告已被过滤");
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("当前程序耗时：" + (endTime - startTime) + "ms");
	}
}
