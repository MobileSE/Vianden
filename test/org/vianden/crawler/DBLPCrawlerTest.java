package org.vianden.crawler;

import java.util.List;

import org.junit.Test;

public class DBLPCrawlerTest 
{
	@Test
	public void testCrawlVenueURLs()
	{
		//String dblpUrl = "http://dblp.uni-trier.de/db/conf/kbse/";
		String dblpUrl = "http://dblp.uni-trier.de/db/journals/tse/index.html";
		
		List<String> urls = DBLPCrawler.crawlVenueURLs(dblpUrl, 1998, true);
		
		for (String url : urls)
		{
			System.out.println(url);
		}
	}
}
