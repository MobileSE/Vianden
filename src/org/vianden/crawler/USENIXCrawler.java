package org.vianden.crawler;

import java.io.IOException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.model.Paper;

public class USENIXCrawler extends AbstractCrawler{

	public USENIXCrawler(Paper paper) throws IOException {
		super(paper);
	}

	@Override
	public void crawl() throws IOException {
		Elements usenix = doc.select(".field.field-name-field-paper-description.field-type-text-long.field-label-above")
				.select(".field-items");
		if(usenix != null){
			//abstract
			Element absUsenix = usenix.first();
			abstractStr = absUsenix.text();
			//no reference and keywords
		}
	}

}
