package org.vianden;

import java.util.List;

import org.vianden.model.Paper;

public class SearchEngineTest 
{
	public static void main(String[] args) throws Exception
	{
		SearchEngine se = new SearchEngine();
		List<Paper> papers = se.search(2015);
		
		int count = 0;
		
		for (Paper p : papers)
		{
			count++;
			
			if (count < 10)
			{
				continue;
			}
			
			p = se.refine(p);
			
			System.out.println(p.getpAbstract());
			System.out.println(p.getpAuthors());
			
			break;
		}
	}
}
