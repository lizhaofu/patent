package com.success.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrCollectionPool implements Serializable {  
    private static Logger log = Logger.getLogger(SolrCollectionPool.class);  
    public static SolrCollectionPool instance = new SolrCollectionPool();  
    private static Map<String, BlockingQueue<CloudSolrClient>> poolMap = new ConcurrentHashMap<String, BlockingQueue<CloudSolrClient>>();  
    private static CloudSolrClient patentSolrClient;  
    private static CloudSolrClient patentToRefSolrClient;    
    private static CloudSolrClient patentByRefSolrClient;    
    public SolrCollectionPool() {  
  
    }  
    
    public  static synchronized CloudSolrClient getPatentSolr() {    
        if(patentSolrClient == null) {    
            try {    
            	 String zkHost = "114.215.25.48:2181,114.215.19.130:2181,118.190.43.88:2181"; 
            	 patentSolrClient = new CloudSolrClient(zkHost);
            	 patentSolrClient.setDefaultCollection("patentOfCountry");  
            	 patentSolrClient.setZkClientTimeout(60000);  
            	 patentSolrClient.setZkConnectTimeout(60000);  
            	 patentSolrClient.connect();  
            }catch(Exception e) {    
                e.printStackTrace();    
            }   
        }    
            
        return patentSolrClient;    
    }  
    public  static synchronized CloudSolrClient getPatentToRefSolr() {    
        if(patentToRefSolrClient == null) {    
            try {    
            	 String zkHost = "114.215.19.130:2181,114.215.25.48:2181,118.190.43.88:2181"; 
            	 patentToRefSolrClient = new CloudSolrClient(zkHost);
            	 patentToRefSolrClient.setDefaultCollection("patentToRef");  
            	 patentToRefSolrClient.setZkClientTimeout(60000);  
            	 patentToRefSolrClient.setZkConnectTimeout(60000);  
            	 patentToRefSolrClient.connect();  
            }catch(Exception e) {    
                e.printStackTrace();    
            }   
        }    
            
        return patentToRefSolrClient;    
    }  
    public  static synchronized CloudSolrClient getPatentByRefSolr() {    
        if(patentByRefSolrClient == null) {    
            try {    
            	 String zkHost = "114.215.19.130:2181,114.215.25.48:2181,118.190.43.88:2181"; 
            	 patentByRefSolrClient = new CloudSolrClient(zkHost);
            	 patentByRefSolrClient.setDefaultCollection("patentByRef");  
            	 patentByRefSolrClient.setZkClientTimeout(60000);  
            	 patentByRefSolrClient.setZkConnectTimeout(60000);  
            	 patentByRefSolrClient.connect();  
            }catch(Exception e) {    
                e.printStackTrace();    
            }   
        }    
            
        return patentByRefSolrClient;    
    }  
    /**
     * 获取solr连接
     * @param collection  集合名称
     * @param size        初始化连接数
     * @return
     */
//    public synchronized BlockingQueue<CloudSolrClient> getCollectionPool( String collection, final int size) {  
//        if (poolMap.get(collection) == null || poolMap.get(collection).size()==0) {  
//            log.warn("solr:" + collection + " poolsize:" + size);  
//            System.setProperty("javax.xml.parsers.DocumentBuilderFactory",  
//                    "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");  
//            System.setProperty("javax.xml.parsers.SAXParserFactory",  
//                    "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");  
//            BlockingQueue<CloudSolrClient> serverList = new LinkedBlockingQueue<CloudSolrClient>(size);  
//            String zkHost = "114.215.19.130:2181,114.215.25.48:2181,118.190.43.88:2181"; 
//            for (int i = 0; i < size; i++) {  
//            	CloudSolrClient cloudServer = new CloudSolrClient(zkHost);  
//                cloudServer.setDefaultCollection(collection);  
//                cloudServer.setZkClientTimeout(60000);  
//                cloudServer.setZkConnectTimeout(60000);  
//                cloudServer.connect();  
//                serverList.add(cloudServer);  
//            }  
//            poolMap.put(collection, serverList);  
//        }  
//        return poolMap.get(collection);  
//    }  
//  
//    public static SolrCollectionPool instance() {  
//        return SolrCollectionPool.instance;  
//    }  
//    
    
    
    public static boolean searchPatent(String id) {          
        SolrQuery query = new SolrQuery();
        query.setQuery("id:"+tSolrc(id));  
        query.addField("id");
        query.setStart(0);
        query.setRows(200);
        List<Map<String,Object>> mmlist=new ArrayList<Map<String,Object>>();
        try {  
          QueryResponse response = getPatentSolr().query(query);    
          SolrDocumentList docs = response.getResults();
          return docs.size()>0;
        } catch (Exception e) {    
          e.printStackTrace(); 
          return true;
        }    
      } 
    public static boolean searchPatentBy(String patent_id) {          
        SolrQuery query = new SolrQuery();
        query.setQuery("patent_id:"+tSolrc(patent_id));  
        query.addField("patent_id");
        query.setStart(0);
        query.setRows(200);
        List<Map<String,Object>> mmlist=new ArrayList<Map<String,Object>>();
        try {  
          QueryResponse response = getPatentByRefSolr().query(query);    
          SolrDocumentList docs = response.getResults();
          return docs.size()>0;
        } catch (Exception e) {    
          e.printStackTrace(); 
          return true;
        }    
      } 
 // Solr特殊字符转义
 	public static String tSolrc(String input) {
 		StringBuffer str = new StringBuffer();
 		String regex = "[+\\-&amp;|!(){}\\[\\]^\"~*?:(\\) ]";
 		Pattern pattern = Pattern.compile(regex);
 		Matcher matcher = pattern.matcher(input);
 		while (matcher.find()) {
 			matcher.appendReplacement(str, "\\\\" + matcher.group());
 		}
 		matcher.appendTail(str);
 		return str.toString();
 	}
    public static void main(String[] args) {
    	System.out.println(searchPatent("7,778,727"));
	}
}  
