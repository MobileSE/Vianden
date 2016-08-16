package org.vianden.filter;

import java.io.IOException;
import java.util.List;

import org.vianden.config.FilePathes;
import org.vianden.model.Paper;
import org.vianden.tool.MatchTool;

public class TitleFilter implements IFilter{
	private List<String> filterList = null;
	
	public TitleFilter(List<String> filterList) {
		this.filterList = filterList;
	}
	
	public TitleFilter(String configPath) throws IOException{
		filterList = MatchTool.getRegexList(configPath);
	}
	
	//default filter words
	public TitleFilter(){
		filterList = MatchTool.getRegexList(FilePathes.TITLE_KEYWORD_CONFIG);
	}

	@Override
	public boolean filter(Paper paper) {
		boolean isFilteredd = true;

		//filterList=null or size=0 means no filter applied
		if(filterList == null || filterList.size() == 0 || MatchTool.matcher(filterList, paper.getTitle())){
			isFilteredd = false;
		}
		
		return isFilteredd;
	}

}
