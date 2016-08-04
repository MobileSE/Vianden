package org.vianden.filter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.vianden.model.Paper;

public class TitleFilter implements IFilter{
	List<String> filterwords = null;
	private static BufferedReader br = null;
	
	public TitleFilter(List<String> filterwords) {
		this.filterwords = filterwords;
	}
	
	public TitleFilter(String configPath) throws IOException{
		if(configPath == null){
			configPath = System.getProperty("user.dir") + "/res/keywords.config";
		}
		
		//read configs
		FileReader reader = new FileReader(configPath);
		br = new BufferedReader(reader);
		//initialize keywords list
		filterwords = new ArrayList<String>();
		
		String tmp = null;
		while ((tmp = br.readLine()) != null) {
			String[] strArr = tmp.split(";");
			for(int i=0; i<strArr.length; ++i){
				//add to keywords list with lower case
				filterwords.add(strArr[i].toLowerCase());
			}
		}
	}

	@Override
	public boolean filter(Paper paper) {
		
		for(int i=0; i<filterwords.size(); ++i){
			if(paper.getTitle().contains(filterwords.get(i))){
				return true;
			}
		}
		
		return false;
	}

}
