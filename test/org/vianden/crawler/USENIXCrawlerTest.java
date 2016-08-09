package org.vianden.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.vianden.model.Publisher;
import org.vianden.model.Paper;

public class USENIXCrawlerTest {
	
	private static AbstractCrawler crawler = null;
	private static Paper paper = null;
	private static String tAbstract = null;
	private static String tPdfurl = null;
	private static String tPages = null;
	private static String tAuthors = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//read test case
		Properties pp = new Properties();
		pp.load(IEEECrawlerTest.class.getClassLoader().getResourceAsStream("org/vianden/crawler/CrawlTestCase.properties"));
		tAbstract=pp.getProperty("abstractUSENIX");
		tPdfurl=pp.getProperty("pdfurlUSENIX");
		tPages=pp.getProperty("pagesUSENIX");
		tAuthors=pp.getProperty("authorsUSENIX");
		
		//construct paper
		String urlUSENIX = pp.getProperty("urlUSENIX");
		paper = new Paper();
		paper.setDoi(urlUSENIX);
		paper.setPublisher(Publisher.USENIX);
		
		//initialize crawler
		crawler = new USENIXCrawler(paper);
	}

	@Test
	public void testCrawl() throws IOException {
		crawler.commonCrawl();
		crawler.crawl();
		crawler.finishCrawl();
		
		assertEquals(tAbstract, paper.getAbstract());
		assertEquals(tPdfurl, paper.getPdfUrl());
		assertEquals(tPages, paper.getPages());
		assertEquals(tAuthors, paper.getAuthors().toString());
	}

}