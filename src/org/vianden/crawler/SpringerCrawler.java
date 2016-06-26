package org.vianden.crawler;

import java.io.IOException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.model.Paper;

public class SpringerCrawler extends AbstractCrawler {

	public SpringerCrawler(Paper paper) throws IOException {
		super(paper);
	}

	@Override
	public void crawl() throws IOException {
		Element springer = doc.getElementById("Abs1");
		if(springer!=null){
			//abstract
			Element absEle = springer.select("p.Para").first();
			abstractStr= absEle.text();
			//keywords
			Element keyGroupEle = doc.select("div.KeywordGroup").first();
			if(keyGroupEle != null){
				Elements keyEles = keyGroupEle.getElementsByClass("Keyword");
    			
    			for(Element keyword : keyEles){
    				String kw = keyword.text()+";";
    				keywordsStr += kw;
    			}
			}
			//references
			Elements lis = doc.getElementsByClass("Citation");
			for(Element li:lis){
				String refstr = li.text();
				reference.add(refstr);
			}
			
		}
	}

}
