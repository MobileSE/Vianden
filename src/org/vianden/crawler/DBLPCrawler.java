package org.vianden.crawler;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.config.AgentInfo;

public class DBLPCrawler 
{
	/**
	 * includingWorkshop is ignored when venueMainPageURL stands for journals.
	 * 
	 * @param venueMainPageURL
	 * @param startingYear
	 * @param includingWorkshop
	 * @return
	 */
	public static List<String> crawlVenueURLs(String venueMainPageURL, int startingYear, boolean includingWorkshop)
	{
		List<String> venueDBLPPages = new ArrayList<String>();
		
		try 
		{
			Document doc = Jsoup.connect(venueMainPageURL).userAgent(AgentInfo.USER_AGENT).timeout(AgentInfo.TIME_OUT).get();
			Thread.sleep(AgentInfo.SLEEP_TIME);
			
			if (venueMainPageURL.contains("db/journals")) {
				Elements lis = doc.getElementById("main").getElementsByTag("li");
				
				for (Element li : lis) 
				{
					Elements as = li.getElementsByTag("a");
					for (Element a : as) {
						String url = a.attr("href");
						
						String text = a.text();
						if (! text.startsWith("Volume"))
						{
							continue;
						}
						
						String volumnYearStr = a.text().split(",")[1].trim();
						if (volumnYearStr.contains("/"))
						{
							volumnYearStr = volumnYearStr.split("/")[1];
						}
						
						int venueYear = Integer.parseInt(volumnYearStr);
						
						if (venueYear < startingYear)
						{
							continue;
						}
						
						venueDBLPPages.add(url);
					}
				}

			} 
			else if (venueMainPageURL.contains("db/conf")) 
			{
				Elements eles = doc.select(".entry");
				for (Element ele : eles) 
				{
					String cUrl = ele.select(".publ").select(".head").get(0).getElementsByTag("a").first().attr("href");
					
					if (-1 != startingYear)
					{
						String venueYearStr = ele.getElementsByAttributeValue("itemprop", "datePublished").first().text();
						
						int venueYear = Integer.parseInt(venueYearStr);
						if (venueYear < startingYear)
						{
							continue;
						}
					}
					
					if (! includingWorkshop)
					{
						String  venueTitle = ele.select(".title").first().text();
						if (venueTitle.contains("Workshop"))
						{
							continue;
						}
					}
					
					venueDBLPPages.add(cUrl);
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return venueDBLPPages;
	}
}
