package org.vianden.filter;

import org.vianden.model.Paper;
import org.vianden.model.Publisher;

/**
 * Based on the given pages to filter out irrelevant papers.
 * 
 * @author li.li
 */
public class PagesFilter implements IFilter {
	//can't tell whether a paper takes single column or double
	private int minSingleColumnPages = 9;
	private int minDoubleColumnPages = 5;
	
	public PagesFilter(int minSingleColumnPages, int minDoubleColumnPages) {
		this.minSingleColumnPages = minSingleColumnPages;
		this.minDoubleColumnPages = minDoubleColumnPages;
	}
	
	@Override
	public boolean filter(Paper paper) {
		int minPages = Publisher.isSingleColumn(paper.getpPublisher()) ? minSingleColumnPages : minDoubleColumnPages;
		
		if(paper.getpPages()!=null){
			int pages = Integer.valueOf(paper.getpPages());
			if(pages<minPages){
				return true;
			}
		}
		
		return false;
	}

}
