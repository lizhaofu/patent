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
		//����POST����
    	Request request = new Request("http://www.pss-system.gov.cn/sipopublicsearch/patentsearch/showSearchResult-startWa.shtml");
    	//ֻ��POST����ſ�����Ӹ��Ӳ���
    	request.setMethod(HttpConstant.Method.POST);
    	//����POST����
    	List<NameValuePair> nvs = new ArrayList<NameValuePair>();
    	nvs.add(new BasicNameValuePair("resultPagination.limit", "12"));
    	nvs.add(new BasicNameValuePair("resultPagination.sumLimit", "10"));
    	nvs.add(new BasicNameValuePair("resultPagination.start", start+""));
    	nvs.add(new BasicNameValuePair("resultPagination.totalCount", "89760512"));
    	nvs.add(new BasicNameValuePair("searchCondition.searchType", "Sino_foreign"));
    	nvs.add(new BasicNameValuePair("searchCondition.dbId", ""));
    	nvs.add(new BasicNameValuePair("searchCondition.power", "false"));
    	nvs.add(new BasicNameValuePair("searchCondition.searchExp", "���������棩��>=2000-01-01"));
    	nvs.add(new BasicNameValuePair("wee.bizlog.modulelevel", "0200201"));
    	nvs.add(new BasicNameValuePair("searchCondition.executableSearchExp", "VDB:(PD>='2000-01-01')"));
    	nvs.add(new BasicNameValuePair("searchCondition.literatureSF", ""));
    	nvs.add(new BasicNameValuePair("searchCondition.strategy", ""));
//    	nvs.add(new BasicNameValuePair("searchCondition.originalLanguage", ""));
//    	nvs.add(new BasicNameValuePair("searchCondition.extendInfo['MODE']", "MODE_TABLE"));
//    	nvs.add(new BasicNameValuePair("searchCondition.extendInfo['STRATEGY']", "STRATEGY_CALCULATE"));
//    	nvs.add(new BasicNameValuePair("searchCondition.targetLanguage", ""));
//    	nvs.add(new BasicNameValuePair("searchCondition.literatureSF", "���������棩��>=2000-01-01"));
//    	nvs.add(new BasicNameValuePair("searchCondition.strategy", ""));
    	nvs.add(new BasicNameValuePair("searchCondition.searchKeywords", ""));
    	nvs.add(new BasicNameValuePair("searchCondition.searchKeywords", "[2][ ]{0,}[0][ ]{0,}[0][ ]{0,}[0][ ]{0,}[.][ ]{0,}[0][ ]{0,}[1][ ]{0,}[.][ ]{0,}[0][ ]{0,}[1][ ]{0,}"));
    
    	//ת��Ϊ��ֵ������
    	NameValuePair[] values = nvs.toArray(new NameValuePair[] {});

    	//����ֵ��������ӵ�map��
    	Map<String, Object> params = new HashMap<String, Object>();
    	//key�����ǣ�nameValuePair
    	params.put("nameValuePair", values);

    	//����request����
    	request.setExtras(params);
    	
    	return request;
	}
	/**
	 * ��ȡpageҳ����Ϣ
	 * @param page
	 */
	public static void processPage(Page page){
		
		List<Patent> patentList = new ArrayList<>();
		// ר����Ϣ
    	List<String> liList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li").all();
    	// ר������
//    	List<String> titleList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"titleHidden\"]/@value").all();
    	// ��ַ
    	List<String> addressList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"appAddrHidden\"]/@value").all();
    	// �ʱ�
    	List<String> zipCodeList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div[@class=\"item\"]/input[@name=\"appZipHidden\"]/@value").all();
    	// ʡ��
    	List<String> provinceList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"appCountryHidden\"]/@value").all();
    	// ����
    	List<String> languagList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"langHidden\"]/@value").all();
    	// �ĵ�״̬
    	List<String> docStatusList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"docStatusHidden\"]/@value").all();
    	// ���ױ�ʶ
    	List<String> docLogList = page.getHtml().xpath("//div[@class=\"list-container\"]/ul/li/div/input[@name=\"vIdHidden\"]/@value").all();
    	// ����Ψһ��ʶ
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
	 * ��װpage����
	 * @param info
	 */
	public static void savePage(String info,Patent patent){
		
		if(info.contains("�����")){
			String id = info.substring(info.indexOf(":") + 1);
			patent.setId(id);
		}else if(info.contains("������")){
			String field_date = info.substring(info.indexOf(":") + 1);
			patent.setField_date(field_date);
			patent.setField_s(getYear(field_date));
		}else if(info.contains("���������棩��")){
			String document_ldentifier = info.substring(info.indexOf(":") + 1);
			patent.setDocument_ldentifier(document_ldentifier);
		}else if(info.contains("���������棩��")){
			String publication_date = info.substring(info.indexOf(":") + 1);
			patent.setPublication_date(publication_date);
			patent.setPublication_s(getYear(publication_date));
		}else if(info.contains("IPC�����")){
			String cpc_class = info.substring(info.indexOf(":") + 1);
			patent.setCpc_class(cpc_class);			
		}else if(info.contains("���루ר��Ȩ����")){
			String applicant = info.substring(info.indexOf(":") + 1);
			patent.setApplicant(applicant);			
		}else if(info.contains("������")){
			String inventors = info.substring(info.indexOf(":") + 1);
			patent.setInventors(inventors);	
		}else if(info.contains("����Ȩ��")){
			String patent_pr = info.substring(info.indexOf(":") + 1);
			patent.setPatent_pr(patent_pr);
		}else if(info.contains("����Ȩ��")){
			String lssue_date = info.substring(info.indexOf(":") + 1);
			patent.setLssue_date(lssue_date);
			patent.setLssue_s(getYear(lssue_date));
		}else if(info.contains("������")){
			String assignee = info.substring(info.indexOf(":") + 1);
			patent.setAssignee(assignee);			
		}else if(info.contains("�������")){
			String appl_no = info.substring(info.indexOf(":") + 1);
			patent.setAppl_no(appl_no);			
		}
	}
	/**
	 * ���ݲ������ڻ�ȡ���
	 * 
	 * @param date
	 *            ����
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
