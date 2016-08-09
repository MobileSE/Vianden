package org.vianden.filter;

import org.vianden.model.Paper;

public interface IFilter 
{	
	/** exclude papers that satisfied the exclude conditions
	 * 
	 * @param paper
	 * @return true this paper is excluded in paper set
	 * @return false this paper is kept/included in paper set
	 * */
	public boolean filter(Paper paper);
}
