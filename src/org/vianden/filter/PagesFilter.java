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
		int minPages = Publisher.isSingleColumn(paper.getPublisher()) ? minSingleColumnPages : minDoubleColumnPages;
		
		if(paper.getPages()!=null){
			int pages = Integer.valueOf(paper.getPages());
		//get standard of minimum pages
		//judge whether filtered
			if(pages<minPages){
				return true;
			}
		}
		
		return false;
	}

}
