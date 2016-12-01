package org.vianden.toolkits;

import java.io.IOException;
import java.util.Set;

import mudam.util.CommonUtils;

import org.vianden.crawler.ACMCrawler;
import org.vianden.model.Paper;
import org.vianden.model.Publisher;

public class ACMCrawlerClient {

	public static void main(String[] args) throws IOException, InterruptedException 
	{
		Set<String> dois = CommonUtils.loadFile("src/org/vianden/toolkits/acm.txt");
		
		for (String doi : dois)
		{
			try
			{
				System.out.println(doi);
				
				doi = "http://doi.acm.org/" + doi;
				
				Paper paper = new Paper();
				paper.setDoi(doi);	
				paper.setPublisher(Publisher.SPRINGER);
				
				ACMCrawler crawler = new ACMCrawler(paper);
				crawler.commonCrawl();
				crawler.crawl();
				crawler.finishCrawl();
				
				CommonUtils.writeResultToFile("acm_results.txt", paper.toString() + "\n");
				
				Thread.sleep(1000);
			}
			catch (Exception ex)
			{
				
			}
			
		}
		
		
		

	}

}
