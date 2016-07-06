package org.vianden.filter;

import java.util.List;

import org.vianden.model.Paper;

/**
 * Based on the given keywords to filter out irrelevant papers.
 * 
 * @author li.li
 */
public class KeywordsFilter implements IFilter 
{
	List<List<String>> keywords = null;
	
	public KeywordsFilter(List<List<String>> keywords)
	{
		this.keywords = keywords;
	}
	
	// The default path is res/keywords.config
	public KeywordsFilter(String configPath)
	{
		
	}
	
	@Override
	public boolean filter(Paper paper) 
	{
		
		return false;
	}

}
