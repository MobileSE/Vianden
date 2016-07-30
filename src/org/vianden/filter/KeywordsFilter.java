package org.vianden.filter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.vianden.model.Paper;

/**
 * Based on the given keywords to filter out irrelevant papers.
 * 
 * @author li.li
 */
public class KeywordsFilter implements IFilter {
	List<String> keywords = null;
	private static BufferedReader br = null;
	
	public KeywordsFilter(List<String> keywords) {
		this.keywords = keywords;
	}
	
	// The default path is res/keywords.config
	public KeywordsFilter(String configPath) throws IOException {
		if(configPath == null){
			configPath = System.getProperty("user.dir") + "/res/keywords.config";
		}
		//read configs
		FileReader reader = new FileReader(configPath);
		br = new BufferedReader(reader);
		//initialize keywords list
		keywords = new ArrayList<String>();
		String tmp = null;
		while ((tmp = br.readLine()) != null) {
			String[] strArr = tmp.split(";");
			for(int i=0; i<strArr.length; ++i){
				//add to keywords list with lower case
				keywords.add(strArr[i].toLowerCase());
			}
		}
	}
	
	@Override
	public boolean filter(Paper paper) 
	{
		String[] keyArr = paper.getpKeywords().split(";");
		//start filtering
		for(int i=0; i<keywords.size(); ++i){
			for(int j=0; j<keyArr.length; ++j){
				if(keyArr[j].toLowerCase().equals(keywords.get(i))){
					return true;
				}
			}
		}
		
		return false;
	}

}
