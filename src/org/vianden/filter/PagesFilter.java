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
<<<<<<< HEAD
		int minPages = Publisher.isSingleColumn(paper.getPublisher()) ? minSingleColumnPages : minDoubleColumnPages;
		
		if(paper.getPages()!=null){
			int pages = Integer.valueOf(paper.getPages());
=======
		//get standard of minimum pages
		int minPages = Publisher.isSingleColumn(paper.getpPublisher()) ? minSingleColumnPages : minDoubleColumnPages;
		
		//judge whether filtered
		if(paper.getpPages()!=null){
			int pages = Integer.valueOf(paper.getpPages());
>>>>>>> e818f408d7292bcdef9050f63197b447ced33d9e
			if(pages<minPages){
				return true;
			}
		}
		
		return false;
	}

}
