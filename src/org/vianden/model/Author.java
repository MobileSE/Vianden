package org.vianden.model;

import java.util.Set;

public class Author 
{
	private String name;
	private Set<String> affiliation;    //one author may have multiple affiliation
	
	public Author(String name, Set<String> affiliation)
	{
		this.name = name;
		this.affiliation = affiliation;
	}

	@Override
	public String toString() {
		String afStr = "";
		for(String str:this.affiliation){
			afStr += str + ";";
		}
		return "author name:"+ name + ", affiliation:" + afStr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(Set<String> affiliation) {
		this.affiliation = affiliation;
	}
}
