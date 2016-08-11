package org.vianden.crawler;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.SearchEngine;
import org.vianden.config.AgentInfo;
import org.vianden.model.Paper;

public class WileyCrawler extends AbstractCrawler {

	public WileyCrawler(Paper paper) throws IOException {
		super(paper);
	}

	@Override
	public void crawl() throws IOException {
		//keywords
		Element keylist = doc.getElementById("abstractKeywords1");
		if(keylist != null){
			Elements lis = keylist.getElementsByTag("li");
			for(Element li : lis){
				keywordsStr += li.text();
			}
		}
		
		//abstract
		Element absWilly = doc.getElementById("abstract");
		if(absWilly != null){
			Elements ps = absWilly.getElementsByTag("p");
			abstractStr = "";
			for(Element p : ps){
				abstractStr += p.text();
			}
		}
		
		//email
		Elements mailEle1 = doc.getElementsByClass("article-header__authors-item-corresp-addr");
		if(mailEle1 != null && mailEle1.size() >0){
			Elements as = mailEle1.first().getElementsByTag("a");
			if(as != null && as.size()>0){
				
				for(Element a : as){
					String mail = a.text();
					
					if(mail.contains("@") && !emailStr.contains(mail)){
						emailStr += mail + ";";
					}
					
				}
			}
		}
		
		Elements mailEle2 = doc.getElementsByClass("article-header__authors-item-details");
		if(mailEle2 != null && mailEle2.size() >0){
			Elements as = mailEle2.first().getElementsByTag("a");
			if(as != null && as.size()>0){
				
				for(Element a : as){
					String mail = a.text();
					
					if(mail.contains("@") && !emailStr.contains(mail)){
						emailStr += mail + ";";
					}
				}
			}
		}
		
		//references
		Elements tableContent = doc.select(".tabbedContent");
		
		if(tableContent != null && tableContent.size() > 0){
			Elements as = tableContent.first().getElementsByTag("a");
			if(as != null && as.size() >0){
				Element refWiley = as.get(1);
				//can not get absolute address via .attr("abs:href")
				String refWileyUrl = "http://onlinelibrary.wiley.com" + refWiley.attr("href");
				Document reWileyDoc = SearchEngine.accesssUrlContent(refWileyUrl, AgentInfo.LONG_TIME_OUT, AgentInfo.LONG_SLEEP_TIME);
				Elements refsWiley = reWileyDoc.getElementById("fulltext").getElementsByTag("cite");
				
				for(Element refinfo : refsWiley){
					reference.add(refinfo.text());
				}
			}
		}
	}

}
