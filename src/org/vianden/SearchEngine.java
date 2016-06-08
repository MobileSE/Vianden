package org.vianden;

import java.util.List;

import org.vianden.model.DatabaseType;
import org.vianden.model.Paper;

public class SearchEngine 
{
	/**
	 * Based on the res/dblp.config, to crawl all the papers relating to the given venues (conferences or journals).
	 * 
	 * Each line in res/dblp.config represents a link for a whole venue, for example, 
	 * link http://dblp.uni-trier.de/db/conf/sigsoft/index.html shows all the venues (from 1993 to 2015) of FSE.
	 * The actually papers are under the [contents] item.
	 * 
	 * @param startingYear
	 * @return all papers of the configured venues from the startingYear. 
	 */
	public List<Paper> search(int startingYear)
	{
		return null;
	}
	
	/**
	 * To refill the missing attributes (e.g., abstract, author affiliation, etc.) of a given paper
	 * 
	 * @param paper
	 * @return the enriched version of the given paper
	 */
	public Paper refine(Paper paper)
	{
		switch (paper.getpDatabaseType())
		{
		case DatabaseType.ACM:
			break;
		case DatabaseType.IEEE:
			break;
		case DatabaseType.SPRINGER:
			break;
		case DatabaseType.ELSEVIER:
			break;
		case DatabaseType.WILEY:
			break;
		case DatabaseType.CAMBRIDGE_UNIVERSITY_PRESS:
			break;
		default:
			break;
		}
		
		
		return paper;
	}
}
