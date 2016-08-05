package org.vianden;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpRugText {
	public static void main(String[] args) {
		String test = "a  classa ";
		String regex = "(bug|defect|fault|flaw|error)*(class|type|pattern)";
		//String regex2 = "(class|type|pattern)";
		Pattern pattern = Pattern.compile(regex);
		//Pattern pattern2 = Pattern.compile(regex2);
		Matcher matcher = pattern.matcher(test);
		//Matcher matcher2 = pattern2.matcher(test);
		if (matcher.find() ){//&& matcher2.find()) {
			System.out.println(1);
		} else { 
			System.out.println(2);
		}
	}
}
