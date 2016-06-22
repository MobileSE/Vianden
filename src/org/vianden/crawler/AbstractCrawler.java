package org.vianden.crawler;

/**
 * Put all the common part such as init, destory, etc in this class
 * 
 * @author li.li
 *
 */
public abstract class AbstractCrawler implements ICrawler
{
	protected String doi = "";
	
	public AbstractCrawler(String doi)
	{
		this.doi = doi;
	}
}
