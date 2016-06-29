package org.vianden.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.vianden.model.DatabaseType;
import org.vianden.model.Paper;

public class IEEECrawlerTest 
{
	private static AbstractCrawler crawler = null;
	private static Paper paper = null;
	private static String tAbstract = null;
	private static String tKeywords = null;
	private static String tPdfurl = null;
	private static String tPages = null;
	private static String tReferences = null;
	private static String tAuthors = null;
	
	@BeforeClass
	public static void setUp() throws Exception {
		//read test case
		Properties pp = new Properties();
		pp.load(IEEECrawlerTest.class.getClassLoader().getResourceAsStream("org/vianden/crawler/CrawlTestCase.properties"));
		tAbstract=pp.getProperty("abstractIEEE");
		tKeywords=pp.getProperty("keywordsIEEE");
		tPdfurl=pp.getProperty("pdfurlIEEE");
		tPages=pp.getProperty("pagesIEEE");
		tReferences=pp.getProperty("referencesIEEE");
		tAuthors=pp.getProperty("authorsIEEE");
		
		//construct paper
		String urlIeee = pp.getProperty("urlIeee");
		paper = new Paper();
		paper.setpDoi(urlIeee);
		paper.setpDatabaseType(DatabaseType.IEEE);
		
		//initialize crawler
		crawler = new IEEECrawler(paper);
	}
	
	@Test
	public void testCrawl() throws IOException
	{
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
