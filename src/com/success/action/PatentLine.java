package com.success.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.success.bean.Patent;
import com.success.utils.SolrCollectionPool;

public class PatentLine implements Pipeline{
	
	private static Logger log = Logger.getLogger(PatentLine.class);
	private static int count=1;
	@Override
	public void process(ResultItems items, Task arg1) {
	
		try{
			List<Patent> list = (List<Patent>)items.get("data");		
			Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			SolrInputDocument doc = null;
			
			for(Patent patent : list){
				doc = new SolrInputDocument();
				doc.addField("id", patent.getId() == null ? "" : patent.getId());
				doc.addField("field_date", patent.getField_date() == null ? "" : patent.getField_date());
				doc.addField("field_s", patent.getField_s() == null ? "" : patent.getField_s());
				doc.addField("document_ldentifier", patent.getDocument_ldentifier() == null ? "" : patent.getDocument_ldentifier());
				doc.addField("publication_date", patent.getPublication_date() == null ? "" : patent.getPublication_date());
				doc.addField("publication_s", patent.getPublication_s() == null ? "" : patent.getPublication_s());
				doc.addField("cpc_class", patent.getCpc_class() == null ? "" : patent.getCpc_class());
				doc.addField("applicant", patent.getApplicant() == null ? "" : patent.getApplicant());
				doc.addField("inventors", patent.getInventors() == null ? "" : patent.getInventors());
				doc.addField("patent_pr", patent.getPatent_pr() == null ? "" : patent.getPatent_pr());
				doc.addField("lssue_date", patent.getLssue_date() == null ? "" : patent.getLssue_date());
				doc.addField("lssue_s", patent.getLssue_s() == null ? "" : patent.getLssue_s());
				doc.addField("title", patent.getTitle() == null ? "" : patent.getTitle());
				doc.addField("assignee", patent.getAssignee() == null ? "" : patent.getAssignee());
				doc.addField("appl_no", patent.getAppl_no() == null ? "" : patent.getAppl_no());
				doc.addField("address_ci", patent.getAddress() == null ? "" : patent.getAddress());
				doc.addField("claims", patent.getZip_code() == null ? "" : patent.getZip_code());
				doc.addField("international_class", patent.getProvince() == null ? "" : patent.getProvince());
				doc.addField("family_id", patent.getLanguage() == null ? "" : patent.getLanguage());
				doc.addField("class_id", patent.getDoc_log() == null ? "" : patent.getDoc_log());
				doc.addField("class_date", patent.getDoc_only_log() == null ? "" : patent.getDoc_only_log());
				
				docs.add(doc);
			}
			if (docs.size() > 0) {
				CloudSolrClient cloudSolrClient = SolrCollectionPool
						.getPatentSolr();
				cloudSolrClient.add(docs);
				cloudSolrClient.commit();
			}
		}catch(Exception e){
			e.printStackTrace();
			String start = items.get("start");
			log.info(e.getMessage());
			log.info(start);
		}
		
	}

}
