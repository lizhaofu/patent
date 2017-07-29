package com.success.action;
/**
 * 读取多国专利信息
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
	// 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me().setRetryTimes(6).setTimeOut(50000).setSleepTime(1000).setCharset("UTF-8");
    public static void main(String[] args) {
    	to_Data();
    }
    /**
     * 爬取数据
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
	    	    log.warn("第"+start+"条爬取完毕");
	        	if(start >= end){
	        		log.warn("爬取完毕！");
	    			System.exit(0);
	        	}
	    	}
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    log.error(e.getMessage());
    	    log.warn("当前记录数:" + start);
    	}
    }
    @Override
    public Site getSite() {
        return site;
    }
 
    @Override
    public void process(Page page) {
       
    	try{
    		// 处理page页面信息
        	Utils.processPage(page);
    	}catch(Exception e){
    		e.printStackTrace();
    		 log.error(e.getMessage());
    	}
    	
    }
}
