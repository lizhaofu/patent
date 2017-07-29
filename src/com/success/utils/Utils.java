package com.success.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.HttpConstant;

import com.success.bean.Patent;

public class Utils {
	
	
	public static Request getRequest(int start)throws Exception{
		//设置POST请求
    	Request request = new Request("http://www.pss-system.gov.cn/sipopublicsearch/patentsearch/showSearchResult-startWa.shtml");
    	//只有POST请求才可以添加附加参数
    	request.setMethod(HttpConstant.Method.POST);
    	//设置POST参数
    	List<NameValuePair> nvs = new ArrayList<NameValuePair>();
    	nvs.add(new BasicNameValuePair("resultPagination.limit", "12"));
    	nvs.add(new BasicNameValuePair("resultPagination.sumLimit", "10"));
    	nvs.add(new BasicNameValuePair("resultPagination.start", start+""));
    	nvs.add(new BasicNameValuePair("resultPagination.totalCount", "89760512"));
    	nvs.add(new BasicNameValuePair("searchCondition.searchType", "Sino_foreign"));
    	nvs.add(new BasicNameValuePair("searchCondition.dbId", ""));
    	nvs.add(new BasicNameValuePair("searchCondition.power", "false"));
    	nvs.add(new BasicNameValuePair("searchCondition.searchExp", "公开（公告）日>=2000-01-01"));
    	nvs.add(new BasicNameValuePair("wee.bizlog.modulelevel", "0200201"));
    	nvs.add(new BasicNameValuePair("searchCondition.executableSearchExp", "VDB:(PD>='2000-01-01')"));
    	nvs.add(new BasicNameValuePair("searchCondition.literatureSF", ""));
    	nvs.add(new BasicNameValuePair("searchCondition.strategy", ""));
//    	nvs.add(new BasicNameValuePair("searchCondition.originalLanguage", ""));
//    	nvs.add(new BasicNameValuePair("searchCondition.extendInfo['MODE']", "MODE_TABLE"));
//    	nvs.add(new BasicNameValuePair("searchCondition.extendInfo['STRATEGY']", "STRATEGY_CALCULATE"));
//    	nvs.add(new BasicNameValuePair("searchCondition.targetLanguage", ""));
//    	nvs.add(new BasicNameValuePair("searchCondition.literatureSF", "公开（公告）日>=2000-01-01"));
//    	nvs.add(new BasicNameValuePair("searchCondition.strategy", ""));
    	nvs.add(new BasicNameValuePair("searchCondition.searchKeywords", ""));
    	nvs.add(new BasicNameValuePair("searchCondition.searchKeywords", "[2][ ]{0,}[0][ ]{0,}[0][ ]{0,}[0][ ]{0,}[.][ ]{0,}[0][ ]{0,}[1][ ]{0,}[.][ ]{0,}[0][ ]{0,}[1][ ]{0,}"));
    
    	//转换为键值对数组
    	NameValuePair[] values = nvs.toArray(new NameValuePair[] {});

    	//将键值对数组添加到map中
    	Map<String, Object> params = new HashMap<String, Object>();
    	//key必须是：nameValuePair
    	params.put("nameValuePair", values);

    	//设置request参数
    	request.setExtras(params);
    	
    	return request;
	}
	/**
	 * 获取page页面信息
	 * @param page
	 */
	public static void processPage(Page page){
		
		List<Patent> patentList = new ArrayList<>();
		// 专利信息
    	List<String> liList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li").all();
    	// 专利标题
//    	List<String> titleList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"titleHidden\"]/@value").all();
    	// 地址
    	List<String> addressList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"appAddrHidden\"]/@value").all();
    	// 邮编
    	List<String> zipCodeList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div[@class=\"item\"]/input[@name=\"appZipHidden\"]/@value").all();
    	// 省份
    	List<String> provinceList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"appCountryHidden\"]/@value").all();
    	// 语言
    	List<String> languagList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"langHidden\"]/@value").all();
    	// 文档状态
    	List<String> docStatusList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"docStatusHidden\"]/@value").all();
    	// 文献标识
    	List<String> docLogList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"vIdHidden\"]/@value").all();
    	// 文献唯一标识
    	List<String> docOnlyLog = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"idHidden\"]/@value").all();
    	
    	Patent patent = null;
    	
    	for(int i = 0; i < liList.size(); i++){
    		Html html = new Html(liList.get(i));
    		List<String> plist = html.xpath("//p/allText()").all();
    		String title = html.xpath("//div/input[@name=\"titleHidden\"]/@value").toString();
    		patent = new Patent();
    		patent.setTitle(title);
    		patent.setAddress(addressList.get(i));
    		patent.setZip_code(zipCodeList.get(i));
    		patent.setProvince(provinceList.get(i));
    		patent.setLanguage(languagList.get(i));
    		patent.setDoc_status(docStatusList.get(i));
    		patent.setDoc_log(docLogList.get(i));
    		patent.setDoc_only_log(docOnlyLog.get(i));  	
    		for(String p : plist){
            	savePage(p,patent);
            }
    		patentList.add(patent);
    	}
    	page.putField("data", patentList);
    	
	}
	/**
	 * 封装page数据
	 * @param info
	 */
	public static void savePage(String info,Patent patent){
		
		if(info.contains("申请号")){
			String id = info.substring(info.indexOf(":") + 1);
			patent.setId(id);
		}else if(info.contains("申请日")){
			String field_date = info.substring(info.indexOf(":") + 1);
			patent.setField_date(field_date);
			patent.setField_s(getYear(field_date));
		}else if(info.contains("公开（公告）号")){
			String document_ldentifier = info.substring(info.indexOf(":") + 1);
			patent.setDocument_ldentifier(document_ldentifier);
		}else if(info.contains("公开（公告）日")){
			String publication_date = info.substring(info.indexOf(":") + 1);
			patent.setPublication_date(publication_date);
			patent.setPublication_s(getYear(publication_date));
		}else if(info.contains("IPC分类号")){
			String cpc_class = info.substring(info.indexOf(":") + 1);
			patent.setCpc_class(cpc_class);			
		}else if(info.contains("申请（专利权）人")){
			String applicant = info.substring(info.indexOf(":") + 1);
			patent.setApplicant(applicant);			
		}else if(info.contains("发明人")){
			String inventors = info.substring(info.indexOf(":") + 1);
			patent.setInventors(inventors);	
		}else if(info.contains("优先权号")){
			String patent_pr = info.substring(info.indexOf(":") + 1);
			patent.setPatent_pr(patent_pr);
		}else if(info.contains("优先权日")){
			String lssue_date = info.substring(info.indexOf(":") + 1);
			patent.setLssue_date(lssue_date);
			patent.setLssue_s(getYear(lssue_date));
		}else if(info.contains("代理人")){
			String assignee = info.substring(info.indexOf(":") + 1);
			patent.setAssignee(assignee);			
		}else if(info.contains("代理机构")){
			String appl_no = info.substring(info.indexOf(":") + 1);
			patent.setAppl_no(appl_no);			
		}
	}
	/**
	 * 根据参数日期获取年份
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public static String getYear(String date) {
		String year = "";

		if (date != null && !"".equals(date)) {
							
				char[] date_c = date.toCharArray();
				for(int i = 0; i < date_c.length; i++){
					if(year.length() >= 4){
						break;
					}
					if(Character.isDigit(date_c[i])){
						year += date_c[i];
					}else{
						year = "";
					}
				}
		}
		

		return year;

	}
}
