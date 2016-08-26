package org.vianden.crawler;

import java.io.IOException;

import org.vianden.model.Paper;

public interface ICrawler 
{
	/**
	 *  Main crawling part of each crawler
	 * */
	public void crawl() throws IOException;
	
	/**
	 *  Get paper of the crawl results
	 *  @return paper that had been crawled
	 * */
	public Paper getPaper();
}
