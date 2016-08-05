package org.vianden.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.vianden.model.Publisher;
import org.vianden.model.Paper;

public class IETCrawlerTest {
	
	private static AbstractCrawler crawler = null;
	private static Paper paper = null;
	private static String tAbstract = null;
	private static String tKeywords = null;
	private static String tPdfurl = null;
	private static String tPages = null;
	private static String tReferences = null;
	private static String tAuthors = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//read test case
		Properties pp = new Properties();
		pp.load(IEEECrawlerTest.class.getClassLoader().getResourceAsStream("org/vianden/crawler/CrawlTestCase.properties"));
		tAbstract=pp.getProperty("abstractIET");
		tKeywords=pp.getProperty("keywordsIET");
		tPdfurl=pp.getProperty("pdfurlIET");
		tPages=pp.getProperty("pagesIET");
		tReferences=pp.getProperty("referencesIET");
		tAuthors=pp.getProperty("authorsIET");
		
		//construct paper
		String urlIET = pp.getProperty("urlIET");
		paper = new Paper();
		paper.setDoi(urlIET);
		paper.setPublisher(Publisher.IET);
		
		//initialize crawler
		crawler = new IETCrawler(paper);
	}

	@Test
	public void testCrawl() throws IOException {
		crawler.crawl();
		crawler.finishCrawl();
		
		assertEquals(tAbstract, paper.getAbstract());
		assertEquals(tKeywords, paper.getKeywords());
		assertEquals(tPdfurl, paper.getPdfUrl());
		assertEquals(tPages, paper.getPages());
		assertEquals(tReferences, paper.getReferences().toString());
		assertEquals(tAuthors, paper.getAuthors().toString());
	}

}
