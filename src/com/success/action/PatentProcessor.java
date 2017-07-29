package com.success.action;
/**
 * ��ȡ���ר����Ϣ
 */
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.success.utils.Utils;

public class PatentProcessor implements PageProcessor{
	
	private static Logger log = Logger.getLogger(PatentProcessor.class);
	private static int start;
	private static int end;
	// ץȡ��վ��������ã��������롢ץȡ��������Դ�����
	private Site site = Site.me().setRetryTimes(6).setTimeOut(50000).setSleepTime(1000).setCharset("UTF-8");
    public static void main(String[] args) {
    	to_Data();
    }
    /**
     * ��ȡ����
     */
    private static void to_Data(){
    	try {
    		Properties pro = new Properties();
	    	pro.load(new FileInputStream("conf/patent.properties"));
//    		pro.load(new FileInputStream("conf/patent.properties"));
	    	 start = Integer.parseInt(pro.getProperty("s_page").toString());
	    	 end= Integer.parseInt(pro.getProperty("e_page").toString());
	    	while(true){
		    	Request request = Utils.getRequest(start);
	    	    Spider.create(new PatentProcessor()).addPipeline(new PatentLine()).addRequest(request).thread(5).run();
	    	    start += 12;
	    	    log.warn("��"+start+"����ȡ���");
	        	if(start >= end){
	        		log.warn("��ȡ��ϣ�");
	    			System.exit(0);
	        	}
	    	}
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    log.error(e.getMessage());
    	    log.warn("��ǰ��¼��:" + start);
    	}
    }
    @Override
    public Site getSite() {
        return site;
    }
 
    @Override
    public void process(Page page) {
       
    	try{
    		// ����pageҳ����Ϣ
        	Utils.processPage(page);
    	}catch(Exception e){
    		e.printStackTrace();
    		 log.error(e.getMessage());
    	}
    	
    }
}
