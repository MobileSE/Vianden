package org.vianden.crawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
		
		Document redoc = Jsoup.connect(refurl).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17")
				.timeout(10000).get();
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
