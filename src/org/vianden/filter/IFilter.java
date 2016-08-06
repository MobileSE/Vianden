package org.vianden.filter;

import org.vianden.model.Paper;

public interface IFilter 
{
	//exclude papers that satisfied the exclude conditions
	public boolean excludeFilter(Paper paper);
	//include papers that satisfied the include conditions
	public boolean includeFilter(Paper paper);
}
