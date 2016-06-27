package org.vianden.crawler;

import java.io.IOException;

import org.vianden.model.Paper;

public interface ICrawler 
{
	public void crawl() throws IOException;
	
	public Paper getPaper();
}
