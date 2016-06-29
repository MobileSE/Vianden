package org.vianden.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.vianden.model.DatabaseType;
import org.vianden.model.Paper;

public class WileyCrawlerTest {
	
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
		tAbstract=pp.getProperty("abstractWiley");
		tKeywords=pp.getProperty("keywordsWiley");
		tPdfurl=pp.getProperty("pdfurlWiley");
		tPages=pp.getProperty("pagesWiley");
		tReferences=pp.getProperty("referencesWiley");
		tAuthors=pp.getProperty("authorsWiley");
		
		//construct paper
		String urlWiley = pp.getProperty("urlWiley");
		paper = new Paper();
		paper.setpDoi(urlWiley);
		paper.setpDatabaseType(DatabaseType.ACM);
		
		//initialize crawler
		crawler = new WileyCrawler(paper);
	}

	@Test
	public void testCrawl() throws IOException {
		crawler.crawl();
		crawler.FinishCrawl();
		
		assertEquals(tAbstract, paper.getpAbstract());
		assertEquals(tKeywords, paper.getpKeywords());
		assertEquals(tPdfurl, paper.getpPdfUrl());
		assertEquals(tPages, paper.getpPages());
		assertEquals(tReferences, paper.getpReferences().toString());
		assertEquals(tAuthors, paper.getpAuthors().toString());
	}

}
