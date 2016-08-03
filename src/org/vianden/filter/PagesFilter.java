package org.vianden.filter;

import org.vianden.model.Paper;
import org.vianden.model.Publisher;

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
		//get standard of minimum pages
		int minPages = Publisher.isSingleColumn(paper.getpPublisher()) ? minSingleColumnPages : minDoubleColumnPages;
		
		//judge whether filtered
		if(paper.getpPages()!=null){
			int pages = Integer.valueOf(paper.getpPages());
			if(pages<minPages){
				return true;
			}
		}
		
		return false;
	}

}
