package org.vianden.crawler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.vianden.model.Publisher;
import org.vianden.model.Paper;

public class SpringerCrawlerTest {
	
	private static AbstractCrawler crawler = null;
	private static Paper paper = null;
	private static String tAbstract = null;
	private static String tEmail = null;
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
		tAbstract=pp.getProperty("abstractSpringer");
		tEmail=pp.getProperty("emailSpringer");
		tKeywords=pp.getProperty("keywordsSpringer");
		tPdfurl=pp.getProperty("pdfurlSpringer");
		tPages=pp.getProperty("pagesSpringer");
		tReferences=pp.getProperty("referencesSpringer");
		tAuthors=pp.getProperty("authorsSpringer");
		
		//construct paper
		String urlSpringer = pp.getProperty("urlSpringer");
		paper = new Paper();
		paper.setDoi(urlSpringer);
		paper.setPublisher(Publisher.SPRINGER);
		
		//initialize crawler
		crawler = new SpringerCrawler(paper);
	}

	@Test
	public void testCrawl() throws IOException {
		crawler.commonCrawl();
		crawler.crawl();
		crawler.finishCrawl();
		
		assertEquals(tAbstract, paper.getAbstract());
		assertEquals(tEmail, paper.getEmail());
		assertEquals(tKeywords, paper.getKeywords());
		assertEquals(tPdfurl, paper.getPdfUrl());
		assertEquals(tPages, paper.getPages());
		assertEquals(tReferences, paper.getReferences().toString());
		assertEquals(tAuthors, paper.getAuthors().toString());
	}

}