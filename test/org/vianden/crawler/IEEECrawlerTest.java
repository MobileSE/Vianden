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
		paper.setDoi(doi);
		
		try {
			AbstractCrawler crawler = new IEEECrawler(paper);
			crawler.commonCrawl();
			crawler.crawl();
			crawler.finishCrawl();
			
			paper = crawler.getPaper();
		} catch (IOException e) {
			e.printStackTrace();
			
			//Exception here is not expected.
			Assert.fail();
		}
		
		//title
		Assert.assertTrue("Composite Constant Propagation: Application to Android Inter-Component Communication Analysis".equalsIgnoreCase(paper.getTitle()));
		
		//pages
		Assert.assertTrue("12".equals(paper.getPages()));    //change pages from String to int
		
		//authors
		Assert.assertEquals(5, paper.getAuthors().size());
		
		Assert.assertTrue("Damien Octeau".equalsIgnoreCase(paper.getAuthors().get(0).getName()));
		Assert.assertTrue("Daniel Luchaup".equalsIgnoreCase(paper.getAuthors().get(1).getName()));
		Assert.assertTrue("Matthew Dering".equalsIgnoreCase(paper.getAuthors().get(2).getName()));
		Assert.assertTrue("Somesh Jha".equalsIgnoreCase(paper.getAuthors().get(3).getName()));
		Assert.assertTrue("Patrick McDaniel".equalsIgnoreCase(paper.getAuthors().get(4).getName()));
		
		Assert.assertEquals("Dept. of Comput. Sci., Univ. of Wisconsin, Madison, WI, USA", paper.getAuthors().get(0).getAffiliation().iterator().next());
		
		//abstract
		Assert.assertTrue("Many program analyses require statically inferring the possible values of composite types. However, current approaches either do not account for correlations between object fields or do so in an ad hoc manner. In this paper, we introduce the problem of composite constant propagation. We develop the first generic solver that infers all possible values of complex objects in an interprocedural, flow and context-sensitive manner, taking field correlations into account. Composite constant propagation problems are specified using COAL, a declarative language. We apply our COAL solver to the problem of inferring Android Inter-Component Communication (ICC) values, which is required to understand how the components of Android applications interact. Using COAL, we model ICC objects in Android more thoroughly than the state-of-the-art. We compute ICC values for 460 applications from the Play store. The ICC values we infer are substantially more precise than previous work. The analysis is efficient, taking slightly over two minutes per application on average. While this work can be used as the basis for many whole-program analyses of Android applications, the COAL solver can also be used to infer the values of composite objects in many other contexts.".equals(paper.getAbstract()));
	
		//pdf url
		Assert.assertTrue("http://ieeexplore.ieee.org/iel7/7174815/7194545/07194563.pdf?arnumber=7194563".equals(paper.getPdfUrl()));
	
		//References
		Assert.assertEquals(0, paper.getReferences().size());
	}
}