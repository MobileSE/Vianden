package org.vianden.dblp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mudam.util.CommonUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.vianden.model.Author;
import org.vianden.model.Paper;
import org.vianden.tool.ConvertTool;

public class DBLPParser 
{

	public static Map<String, Set<Paper>> venue2papers = new HashMap<String, Set<Paper>>();
	public static Set<String> seplVenues = CommonUtils.loadFile("res/ccf_sepl.txt");
	public static Set<String> secVenues = CommonUtils.loadFile("res/ccf_sec.txt");
	
	public static void main(String[] args) throws IOException 
	{
		parse(new File("dblp.xml"));
		
		int totalPaper = 0;
		for (Map.Entry<String, Set<Paper>> entry : venue2papers.entrySet())
		{
			totalPaper += entry.getValue().size();
			ConvertTool.paperToXls(entry.getValue(), entry.getKey().replace("/", "-") + ".xls");
		}
		
		System.out.println("total papers:" + totalPaper);
	}

	public static void parse(File dblpXmlFile) throws IOException
	{
		LineIterator iterator = FileUtils.lineIterator(dblpXmlFile, "UTF-8");
		
		Paper paper = new Paper();
		String venue = "";
		
		while (iterator.hasNext())
		{
			String line = iterator.next();
			
			if (line.startsWith("<article>") || line.startsWith("<inproceedings>"))
			{
				paper = new Paper();
			}
			else if (line.startsWith("</article>") || line.startsWith("</inproceedings>"))
			{
				if (Integer.parseInt(paper.getYear()) < 2010)
				{
					continue;
				}
				
				if (seplVenues.contains(venue) || secVenues.contains(venue))
				{
					String key = venue + "_" + paper.getYear();
					
					if (venue2papers.containsKey(key))
					{
						Set<Paper> papers = venue2papers.get(key);
						papers.add(paper);
						venue2papers.put(key, papers);
					}
					else
					{
						Set<Paper> papers = new HashSet<Paper>();
						papers.add(paper);
						venue2papers.put(key, papers);
					}
				}
			}
			else
			{
				if (line.startsWith("<author>"))
				{
					String name = line.replace("<author>", "").replace("</author>", "").trim();
					Author author = new Author(name, null);
					paper.getAuthors().add(author);
				}
				else if (line.startsWith("<title>"))
				{
					String title = line.replace("<title>", "").replace("</title>", "").trim();
					if (title.endsWith("."))
					{
						title = title.substring(0, title.length()-1);
					}

					paper.setTitle(title);
				}
				else if (line.startsWith("<pages>"))
				{
					String pages = line.replace("<pages>", "").replace("</pages>", "").trim();
					paper.setPages(pages);
				}
				else if (line.startsWith("<booktitle>"))
				{
					String booktitle = line.replace("<booktitle>", "").replace("</booktitle>", "").trim();
					paper.setVenue(booktitle);
				}
				else if (line.startsWith("<journal>"))
				{
					String journal = line.replace("<journal>", "").replace("</journal>", "").trim();
					paper.setVenue(journal);
				}
				else if (line.startsWith("<year>"))
				{
					String year = line.replace("<year>", "").replace("</year>", "").trim();
					paper.setYear(year);
				}
				else if (line.startsWith("<ee>") && line.contains("dx.doi.org"))
				{
					String ee = line.replace("<ee>", "").replace("</ee>", "").trim();
					paper.setDoi(ee);
				}
				else if (line.startsWith("<url>"))
				{
					String url = line.replace("<url>", "").replace("</url>", "").trim();
					venue = url.substring(0, url.lastIndexOf('/'));
				}
			}
		}
	}
}
