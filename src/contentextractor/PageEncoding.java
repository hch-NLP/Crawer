package contentextractor;
import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
public class PageEncoding {
    public static void main(String[] args) {
    	  long startTime = System.currentTimeMillis();
//        String charset1 = getEncodingByContentStream("http://news.szhk.com/2018/04/17/282997737181858.html");
//        System.out.println(charset1);
//        String charset = getEncodingByHeader("http://news.szhk.com/2018/04/17/282997737181858.html");
//        String charset = getEncodingByMeta("http://news.szhk.com/2018/04/17/282997737181858.html");
//    	  System.out.println(charset); 
//        String charset2= getEncodingByContentUrl("http://news.szhk.com/2018/04/17/282997737181858.html");
//        System.out.println(charset2);
    	  long endTime = System.currentTimeMillis();
  		System.out.println("��ǰ�����ʱ��" + (endTime - startTime) + "ms");
    }

    /**
     * ��header�л�ȡҳ�����
     * @param strUrl
     * @return
     */
    public static String getEncodingByHeader(String strUrl){
        String charset = null;
        try {
            URLConnection urlConn = new URL(strUrl).openConnection();
            // ��ȡ���ӵ�header
            Map<String, List<String>> headerFields = urlConn.getHeaderFields();
            // �ж�headers���Ƿ����Content-Type
            if(headerFields.containsKey("Content-Type")){
                //�õ�header �е� Content-Type ��[text/html; charset=utf-8]
                List<String> attrs = headerFields.get("Content-Type");
                String[] as = attrs.get(0).split(";");
                System.out.println(attrs.get(0));
                for (String att : as) {
                    if(att.contains("charset")){
                        //System.out.println(att.split("=")[1]);
                        charset = att.split("=")[1];
                    }
                }
            } 
            return charset;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return charset;
        } catch (IOException e) {
            e.printStackTrace();
            return charset;
        }
    }
    
    /**
     * ��meta�л�ȡҳ�����
     * @param strUrl
     * @return
     */
    public static String getEncodingByMeta(String strUrl){
        String charset = null;
        try {
            URLConnection urlConn = new URL(strUrl).openConnection();
            //���ⱻ�ܾ�
            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
            // ��html��ȡ����,����list
            List<String> lines = IOUtils.readLines(urlConn.getInputStream());
            for (String line : lines) {
                if(line.contains("http-equiv")&&line.contains("charset")){  
                    String tmp = line.split(";")[1];
                    charset = tmp.substring(tmp.indexOf("=")+1, tmp.indexOf("\""));
                }else{
                    continue;
                }
            }
            return charset;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return charset;
        } catch (IOException e) {
            e.printStackTrace();
            return charset;
        }
    }
    
    /**
     * ������ҳ���ݻ�ȡҳ�����
     *     case : �����ڿ���ֱ�Ӷ�ȡ��ҳ�����(�������:һЩ������վ��ֹ����User-Agent��Ϣ�ķ�������)
     * @param url
     * @return
     */
    public static String getEncodingByContentUrl(String url) {
        CodepageDetectorProxy cdp = CodepageDetectorProxy.getInstance();
        cdp.add(JChardetFacade.getInstance());// ����jar�� ��antlr.jar & chardet.jar
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
        return charset == null ? null : charset.name().toLowerCase();
    }
    
    /**
     * ������ҳ���ݻ�ȡҳ�����
     *     case : �����ڲ�����ֱ�Ӷ�ȡ��ҳ�����,ͨ��������ҳת��Ϊ֧��mark��������,Ȼ���������
     * @param strUrl
     * @return
     */
    public static String getEncodingByContentStream(String strUrl) {
        Charset charset = null;
        try {
            URLConnection urlConn = new URL(strUrl).openConnection();
            //������,����User-Agent,���ⱻ�ܾ�
            urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
            
            //����ҳ������
            CodepageDetectorProxy cdp = CodepageDetectorProxy.getInstance();
            cdp.add(JChardetFacade.getInstance());// ����jar�� ��antlr.jar & chardet.jar
            cdp.add(ASCIIDetector.getInstance());
            cdp.add(UnicodeDetector.getInstance());
            cdp.add(new ParsingDetector(false));
            cdp.add(new ByteOrderMarkDetector());
            
            InputStream in = urlConn.getInputStream();
            ByteArrayInputStream bais = new ByteArrayInputStream(IOUtils.toByteArray(in));
            // detectCodepage(InputStream in, int length) ֻ֧�ֿ���mark��InputStream
            charset = cdp.detectCodepage(bais, 2147483647);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return charset == null ? null : charset.name().toLowerCase();
    }
}
