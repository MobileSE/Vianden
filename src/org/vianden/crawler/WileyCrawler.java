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
		Elements lis = keylist.getElementsByTag("li");
		for(Element li : lis){
			keywordsStr += li.text();
		}
		
		//abstract
		Element absWilly = doc.getElementById("abstract");
		Elements ps = absWilly.getElementsByTag("p");
		abstractStr = "";
		for(Element p : ps){
			abstractStr += p.text();
		}
		
		//references
		Element refWiley = doc.select(".tabbedContent").first().getElementsByTag("a").get(1);
		//can not get absolute address via .attr("abs:href")
		String refWileyUrl = "http://onlinelibrary.wiley.com" + refWiley.attr("href");
		Document reWileyDoc = SearchEngine.accesssUrlContent(refWileyUrl, AgentInfo.LONG_TIME_OUT, AgentInfo.LONG_SLEEP_TIME);
		Elements refsWiley = reWileyDoc.getElementById("fulltext").getElementsByTag("cite");
		
		for(Element refinfo : refsWiley){
			reference.add(refinfo.text());
		}
	}

}
