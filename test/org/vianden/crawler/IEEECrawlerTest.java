package org.vianden.crawler;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.vianden.model.Paper;

public class IEEECrawlerTest 
{
	@Test
	public void testCrawl()
	{
		String doi = "http://dx.doi.org/10.1109/ICSE.2015.30";
		
		Paper paper = new Paper();
		paper.setpDoi(doi);
		
		try {
			AbstractCrawler crawler = new IEEECrawler(paper);
			crawler.crawl();
			
			paper = crawler.getPaper();
		} catch (IOException e) {
			e.printStackTrace();
			
			//Exception here is not expected.
			Assert.fail();
		}
		
		Assert.assertTrue("Composite Constant Propagation: Application to Android Inter-Component Communication Analysis".equalsIgnoreCase(paper.getpTitle()));
		Assert.assertTrue("77 - 88".equals(paper.getpPages()));    //change pages from String to int
		
		Assert.assertEquals(5, paper.getpAuthors().size());
		
		Assert.assertTrue("Damien Octeau".equalsIgnoreCase(paper.getpAuthors().get(0).getName()));
		Assert.assertTrue("Daniel Luchaup".equalsIgnoreCase(paper.getpAuthors().get(1).getName()));
		Assert.assertTrue("Matthew Dering".equalsIgnoreCase(paper.getpAuthors().get(2).getName()));
		Assert.assertTrue("Somesh Jha".equalsIgnoreCase(paper.getpAuthors().get(3).getName()));
		Assert.assertTrue("Patrick McDaniel".equalsIgnoreCase(paper.getpAuthors().get(4).getName()));
	}
}
