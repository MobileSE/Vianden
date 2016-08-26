package org.vianden.tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vianden.config.ReadConfigFile;

public class MatchTool {
	
	/**
	 * Determine whether matched with the given string
	 * 
	 * @return true if matched, false while not matched
	 * */
	public static boolean matcher(List<String>regaxList, String string) {
		boolean finded = false;
		Pattern pattern = null;
		Matcher matcher = null;
		for (int i = 0; i < regaxList.size(); i ++) {
			pattern = Pattern.compile(regaxList.get(i));
			matcher = pattern.matcher(string);
			if (matcher.find()) {
				finded = true;
			} else {
				finded = false;
				break;
			}
		}
		return finded;
	}
	
	/**
	 * Get regex list from config path
	 * 
	 * @return list of regex
	 * */
	public static List<String> getRegexList(String configPath) {
		List<String> keywordsList = new ArrayList<String>();
		keywordsList = ReadConfigFile.readConfigFile(configPath);
		
		List<String> regaxList = new ArrayList<String>();
		Iterator<String> iterator = keywordsList.iterator();
		while (iterator.hasNext()) {
			String keywords = iterator.next().trim();
			regaxList.add("(" + keywords.replaceAll(";", "|") + ")");
		}
		return regaxList;
	}
	
}
