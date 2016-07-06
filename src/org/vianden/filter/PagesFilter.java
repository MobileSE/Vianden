package org.vianden.filter;

import java.util.List;

import org.vianden.model.Paper;

/**
 * Based on the given keywords to filter out irrelevant papers.
 * 
 * @author li.li
 */
public class PagesFilter implements IFilter 
{
	private int minSingleColumnPages = 9;
	private int minDoubleColumnPages = 5;
	
	public PagesFilter(int minSingleColumnPages, int minDoubleColumnPages)
	{
		this.minSingleColumnPages = minSingleColumnPages;
		this.minDoubleColumnPages = minDoubleColumnPages;
	}
	
	
	@Override
	public boolean filter(Paper paper) 
	{
		
		return false;
	}

}
