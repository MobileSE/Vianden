package org.vianden.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.vianden.model.DatabaseType;
import org.vianden.model.Paper;

public class ACMCrawlerTest {

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
		tAbstract=pp.getProperty("abstractACM");
		tKeywords=pp.getProperty("keywordsACM");
		tPdfurl=pp.getProperty("pdfurlACM");
		tPages=pp.getProperty("pagesACM");
		tReferences=pp.getProperty("referencesACM");
		tAuthors=pp.getProperty("authorsACM");
		
		//construct paper
		String urlACM = pp.getProperty("urlACM");
		paper = new Paper();
		paper.setpDoi(urlACM);
		paper.setpDatabaseType(DatabaseType.ACM);
		
		//initialize crawler
		crawler = new ACMCrawler(paper);
	}

	@Test
	public void testCrawl() throws IOException {
		crawler.crawl();
		crawler.finishCrawl();
		
		assertEquals(tAbstract, paper.getpAbstract());
		assertEquals(tKeywords, paper.getpKeywords());
		assertEquals(tPdfurl, paper.getpPdfUrl());
		assertEquals(tPages, paper.getpPages());
		assertEquals(tReferences, paper.getpReferences().toString());
		assertEquals(tAuthors, paper.getpAuthors().toString());
	}

}
