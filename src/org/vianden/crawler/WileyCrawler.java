package org.vianden.crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
		Document reWileyDoc = Jsoup.connect(refWileyUrl).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17")
				.timeout(10000).get();
		Elements refsWiley = reWileyDoc.getElementById("fulltext").getElementsByTag("cite");
		reference = new ArrayList<String>();
		for(Element refinfo : refsWiley){
			reference.add(refinfo.text());
		}
	}

}
