package org.vianden;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.config.AgentInfo;
import org.vianden.config.FilePathes;
import org.vianden.config.ReadConfigFile;
import org.vianden.crawler.ACMCrawler;
import org.vianden.crawler.AbstractCrawler;
import org.vianden.crawler.ElsevierCrawler;
import org.vianden.crawler.IEEECrawler;
import org.vianden.crawler.IETCrawler;
import org.vianden.crawler.SpringerCrawler;
import org.vianden.crawler.USENIXCrawler;
import org.vianden.crawler.WileyCrawler;
import org.vianden.model.Author;
import org.vianden.model.Publisher;
import org.vianden.model.Paper;

public class SearchEngine {
	
	private static List<String> urls = null;
	

	/**
	 * Based on the res/dblp.config, to crawl all the papers relating to the
	 * given venues (conferences or journals).
	 * 
	 * Each line in res/dblp.config represents a link for a whole venue, for
	 * example, link http://dblp.uni-trier.de/db/conf/sigsoft/index.html shows
	 * all the venues (from 1993 to 2015) of FSE. The actually papers are under
	 * the [contents] item.
	 * 
	 * @param startingYear
	 * @return all papers of the configured venues from the startingYear.
	 * @throws Exception
	 */
	public List<Paper> search(int startingYear) {
		List<Paper> paperlist = new ArrayList<Paper>();
		urls = ReadConfigFile.readConfigFile(FilePathes.DBLP_CONFIG);
		
		if (urls.size() == 1 && urls.listIterator().next() == ReadConfigFile.FNFExpStr) {
			System.out.println(ReadConfigFile.FNFExpStr);
			return null;
		}

		// start crawl papers with basic information from dblp sites
		// from starting year to current year
		// get Document of each url with network
		// get url, type, venue information of each paper
		List<Map<String, String>> paperForCrawlList = new ArrayList<Map<String, String>>();
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		Document doc = null;
		for (int index = 0; index < urls.size(); ++index) {
			String dblpUrl = urls.get(index);
			doc = this.accesssUrlContent(dblpUrl);
			if (doc == null) {
				index ++;
				continue;
			}
			this.analysisDocument(paperForCrawlList, dblpUrl, doc, startingYear, currentYear);
		}		
		
		//get detail information of each paper with network
		for(int i = 0; i< paperForCrawlList.size(); ++i){
			Map<String, String> map = paperForCrawlList.get(i);
			String url = map.get("url");
			String type = map.get("type");
			String venue = map.get("venue");
			
			// get papers of each conference and journals
			List<Paper> list = this.getPapers(url, type, venue);
			if (list != null) {
				paperlist.addAll(list);
			}
		}

		return paperlist;
	}

<<<<<<< HEAD
	private Document accesssUrlContent(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).userAgent(AgentInfo.getUSER_AGENT()).timeout(AgentInfo.getTIME_OUT()).get();
		} catch (IOException e) {
			System.out.println("Failed to access the website of " + url);
			doc = null;
=======

	/**
	 * To refill the missing attributes (e.g., abstract, author affiliation,
	 * etc.) of a given paper
	 * 
	 * @param paper
	 * @return the enriched version of the given paper
	 * @throws Exception
	 */
	public Paper refine(Paper paper) throws Exception {
		AbstractCrawler crawler = null;

		switch (paper.getPublisher()) {
		case Publisher.ACM:
			crawler = new ACMCrawler(paper);
			break;
		case Publisher.IEEE:
			crawler = new IEEECrawler(paper);
			break;
		case Publisher.SPRINGER:
			crawler = new SpringerCrawler(paper);
			break;
		case Publisher.ELSEVIER:
			crawler = new ElsevierCrawler(paper);
			break;
		case Publisher.WILEY:
			crawler = new WileyCrawler(paper);
			break;
		case Publisher.USENIX:
			crawler = new USENIXCrawler(paper);
			break;
		case Publisher.IET:
			crawler = new IETCrawler(paper);
			break;
		default:
			//dbType=-1, no doi and do nothing
			break;
		}

		if (crawler != null) {
			// crawling
			crawler.crawl();
			// set data to paper
			crawler.finishCrawl();
>>>>>>> 302b72eb628a966146a0b213f73727b1438f864b
		}
		return doc;
	}

	/**
	 * get papers from each single journal or conference page
	 * 
	 * @param url(journal or conference page url)
	 * @param type(article or inproceedings)
	 * @param venue
	 * @return the papers list of the single journal or conference
	 * @throws Exception
	 */
	private List<Paper> getPapers(String url, String type, String venue) {
		List<Paper> list = new ArrayList<Paper>();

		Document doc = this.accesssUrlContent(url);
		if (doc == null) {
			System.out.println("--Failed to access the website of " + url);
			return null;
		}
		Elements entries = doc.select(".entry").select("." + type);

		for (Element ele : entries) {
			Element data = ele.getElementsByClass("data").first();
			// get title
			String title;
			try {
				title = data.getElementsByClass("title").text();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				continue;
			}
			// get year
			Elements eYear = data.getElementsByAttributeValue("itemprop", "datePublished");
			String year = "";
			try {
				year = eYear.first().attr("content");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				continue;
			}
			// get authors
			Elements authors = data.getElementsByAttributeValue("itemprop", "author");
			List<Author> authorlist = new ArrayList<Author>();
			for (Element tmp : authors) {
				Author author = new Author(tmp.text(), null);
				authorlist.add(author);
			}
			// get doi
<<<<<<< HEAD
			String doi;
			try {
				doi = ele.select(".publ").select(".head").get(0).getElementsByTag("a").first().attr("href");
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
=======
			Element doiEle = ele.select(".publ").select(".head").get(0);
			String doi = null;
			if(doiEle.getElementsByTag("a").first() != null && doiEle.getElementsByTag("a").first().hasAttr("href")){
				doi = doiEle.getElementsByTag("a").first().attr("href");
			}
			
>>>>>>> 302b72eb628a966146a0b213f73727b1438f864b
			// get database type by doi number
			int dbtype = -1;
			if(doi == null){
				//do nothing
			}else if (doi.contains("10.1145")) {
				dbtype = Publisher.ACM;
			} else if (doi.contains("10.1109")) {
				dbtype = Publisher.IEEE;
			} else if (doi.contains("10.1007")) {
				dbtype = Publisher.SPRINGER;
			} else if (doi.contains("10.1016")) {
				dbtype = Publisher.ELSEVIER;
			} else if (doi.contains("10.1002")) {
				dbtype = Publisher.WILEY;
			} else if (doi.contains("usenix")) {
				dbtype = Publisher.USENIX;
			} else if (doi.contains("10.1049")) {
				dbtype = Publisher.IET;
			}

			/*
			 * Filtering out the unrelated papers of which title does not include the keywords.
			 * This filtering method won't be perfect.
			 * But this method can help reduce the workload of the 'refine' method.
			 * So, we must be very sure about the keywords.
			 * For example, during the case study, selecting papers about bug classification, 
			 * I am very sure that the title of selected papers must contain words: bug, defect, fault, flaw or error.
			 *  
			 * We can improve this part by reading a value from a config file.
			 * The value (boolean filter) decides whether we filter the papers before refine method or not.
			 *  
			 */
			if (filterByKeyword(title)) {
				// construct paper with obtained information
				Paper paper = new Paper();
				paper.setYear(year);
				paper.setAuthors(authorlist);
				paper.setTitle(title);
				paper.setDoi(doi);
				paper.setVenue(venue);
				paper.setPublisher(dbtype);

				// add paper to list
				list.add(paper);	
			}
		}

		return list;
	}

	private boolean filterByKeyword(String title) {
		boolean isKeeped = false;
		List<String> regaxList = new ArrayList<String>();
		regaxList = getRegaxList();
		if(matcher(regaxList, title)) {
			isKeeped = true;
		}
		return isKeeped;
	}

	private boolean matcher(List<String>regaxList, String title) {
		boolean finded = false;
		Pattern pattern = null;
		Matcher matcher = null;
		for (int i = 0; i < regaxList.size(); i ++) {
			pattern = Pattern.compile(regaxList.get(i));
			matcher = pattern.matcher(title);
			if (matcher.find()) {
				finded = true;
			} else {
				finded = false;
				break;
			}
		}
		return finded;
	}

	private List<String> getRegaxList() {
		List<String> keywordsList = new ArrayList<String>();
		keywordsList = ReadConfigFile.readConfigFile(FilePathes.KEYWORD_CONFIG);
		
		List<String> regaxList = new ArrayList<String>();
		Iterator<String> iterator = keywordsList.iterator();
		while (iterator.hasNext()) {
			String keywords = iterator.next().trim();
			regaxList.add("(" + keywords.replaceAll(",", "|") + ")");
		}
		return regaxList;
	}

	/**
	 *  this function handle the analysis work of each venue's html document to get wanted paper urls
	 *  then, add these urls to prepared list
	 *  
	 *  @param paperForCrawlList
	 *  @param dplpUrl, used to tell journal or conference
	 *  @param doc, document waited to be analyzed
	 *  @param startYear
	 *  @param endYear
	 *  @return void
	 */
	private void analysisDocument(List<Map<String, String>> paperForCrawlList, String dblpUrl, Document doc, int startYear, int endYear){
		
		// start analyzing corresponding to journal and conference
		if (dblpUrl.contains("db/journals")) {
			Element main = doc.getElementById("main");
			Elements lis = main.getElementsByTag("li");
			// get venue
			String venue = doc.select(".headline").select(".noline").first().text();

			for (Element li : lis) {
				// get papers from starting year
				for (int i = startYear; i <= endYear; ++i) {
					String validYear = String.valueOf(i);
					if (li.text().contains(validYear)) {
						Elements as = li.getElementsByTag("a");
						for (Element a : as) {
							String url = a.attr("href");
							Map<String, String> map = new HashMap<String, String>();
							map.put("url", url);
							map.put("type", "article");
							map.put("venue", venue);
							paperForCrawlList.add(map);
						}
						continue;
					}
				}
			}

		} else if (dblpUrl.contains("db/conf")) {
			Elements eles = doc.select(".entry");

			String venue = doc.select(".headline").select(".noline").first().text();

			for (Element ele : eles) {
				Element eData = ele.getElementsByClass("data").first();
				// get year
				int year = 0;
				Elements eYear;
				try {
					eYear = eData.getElementsByAttributeValue("itemprop", "datePublished");
				} catch (NullPointerException e) {
					System.out.println("Failed to find the element (itemprop=datePublished)");
					continue;
				}
				year = Integer.valueOf(eYear.first().text());
				// get detail conference paper list
				String cUrl;
				try {
					cUrl = ele.select(".publ").select(".head").get(0).getElementsByTag("a").first().attr("href");
				} catch (NullPointerException e) {
					e.printStackTrace();
					continue;
				}
				// get papers from starting year
				if (year >= startYear) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("url", cUrl);
					map.put("type", "inproceedings");
					map.put("venue", venue);
					paperForCrawlList.add(map);
				}
			}
		}
		
	}

	
	/**
	 * To refill the missing attributes (e.g., abstract, author affiliation,
	 * etc.) of a given paper
	 * 
	 * @param paper
	 * @return the enriched version of the given paper
	 * @throws Exception
	 */
	public Paper refine(Paper paper) throws Exception {
		AbstractCrawler crawler = null;

		switch (paper.getPublisher()) {
		case Publisher.ACM:
			crawler = new ACMCrawler(paper);
			break;
		case Publisher.IEEE:
			crawler = new IEEECrawler(paper);
			break;
		case Publisher.SPRINGER:
			crawler = new SpringerCrawler(paper);
			break;
		case Publisher.ELSEVIER:
			crawler = new ElsevierCrawler(paper);
			break;
		case Publisher.WILEY:
			crawler = new WileyCrawler(paper);
			break;
		case Publisher.USENIX:
			crawler = new USENIXCrawler(paper);
			break;
		case Publisher.IET:
			crawler = new IETCrawler(paper);
			break;
		default:
			break;
		}

		if (crawler != null) {
			// crawling
			crawler.crawl();
			// set data to paper
			crawler.finishCrawl();
		}

		return paper;
	}
}