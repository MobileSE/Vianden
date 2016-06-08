package org.vianden.model;

import java.util.Set;

public class Author 
{
	public String name;
	public Set<String> affiliation;    //one author may have multiple affiliation
	
	public Author(String name, Set<String> affiliation)
	{
		this.name = name;
		this.affiliation = affiliation;
	}
}
