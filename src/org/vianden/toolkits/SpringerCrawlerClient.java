package org.vianden.toolkits;

import java.io.IOException;
import java.util.List;

import mudam.util.CommonUtils;

import org.vianden.crawler.SpringerCrawler;
import org.vianden.model.Paper;
import org.vianden.model.Publisher;

public class SpringerCrawlerClient {

	public static void main(String[] args) throws IOException 
	{
		List<String> dois = CommonUtils.loadFileToList("src/org/vianden/toolkits/springer.txt");
		
		for (String doi : dois)
		{
			System.out.println(doi);
			
			try
			{
				Paper paper = new Paper();
				paper.setDoi(doi);	
				paper.setPublisher(Publisher.SPRINGER);
				
				SpringerCrawler crawler = new SpringerCrawler(paper);
				crawler.commonCrawl();
				crawler.crawl();
				crawler.finishCrawl();
				
				CommonUtils.writeResultToFile("springer_results.txt", paper.toString() + "\n");
				
				//Thread.sleep(1000);
			}
			catch (Exception ex)
			{
				
			}
		}
		
		
	}

}
