package org.vianden.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadConfigFile {
	/**
	 * Error Messages
	 * */
	public static final String FNFExpStr = "File not found!";
	public static final String FRFExpStr = "Failed to read the file ";
	
	/**
	 * Read the content of the file.
	 * @param filePath the relative path of the file.
	 * @return a array list of the file content, each line matches a list member.
	 */
	public static List<String> readConfigFile(String filePath) {
		List<String> list = new ArrayList<String>();
		FileReader reader = null;
		BufferedReader br = null;
		String lineContent = null;
		try {
			reader = new FileReader(System.getProperty("user.dir") + filePath);

			br = new BufferedReader(reader);
			while ((lineContent = br.readLine()) != null) {
				list.add(lineContent);
			}
		} catch (FileNotFoundException e) {
			list.add(FNFExpStr);
		} catch (IOException e) {
			list.add(FRFExpStr + filePath);
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
