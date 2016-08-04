package org.vianden.crawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.config.AgentInfo;
import org.vianden.model.Paper;

public class IEEECrawler extends AbstractCrawler
{

	public IEEECrawler(Paper paper) throws IOException {
		super(paper);
	}

	@Override
	public void crawl() throws IOException 
	{
		//abstract
		Element ieee = doc.select("div.article").first();
		abstractStr = ieee.text();
		//references
		Element ref = doc.getElementById("abstract-references-tab");
		//can not get absolute address via .attr("abs:href")
		String refurl = "http://ieeexplore.ieee.org" + ref.attr("href"); 
		
		Document redoc = Jsoup.connect(refurl).userAgent(AgentInfo.getUSER_AGENT())
				.timeout(AgentInfo.getTIME_OUT()).get();
		Elements docsClass = redoc.getElementsByClass("docs");
		if(docsClass!=null && docsClass.size()>0){
			Element docs = docsClass.first();
			Elements refs = docs.getElementsByTag("li");
			for(Element li : refs){
				String refstr = li.text();
				reference.add(refstr);
			}
		}
		
	}
	
	
}
