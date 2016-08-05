package org.vianden;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.config.AgentInfo;
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
	
	private static BufferedReader br = null;
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
	public List<Paper> search(int startingYear) throws Exception {
		List<Paper> paperlist = new ArrayList<Paper>();
		this.getUrls();

		// start crawl papers with basic information from dblp sites
		// from starting year to current year
		// get Document of each url with network
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		Document doc = null;
		// get url, type, venue information of each paper
		List<Map<String, String>> paperForCrawlList = new ArrayList<Map<String, String>>();
		for (int index = 0; index < urls.size(); ++index) {
			String dblpUrl = urls.get(index);
			doc = Jsoup.connect(dblpUrl).userAgent(AgentInfo.getUSER_AGENT()).timeout(AgentInfo.getTIME_OUT()).get();
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
			paperlist.addAll(list);
		}

		return paperlist;
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
			//dbType=-1, no doi and do nothing
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

	/**
	 * Getting the web sites which will be used to search papers from the file
	 * dblp.config.
	 * 
	 * @throws IOException
	 */
	private void getUrls() throws IOException {
		if(urls == null){
			urls = new ArrayList<String>();
		}
		
		FileReader reader = new FileReader(System.getProperty("user.dir") + "/res/dblp.config");
		br = new BufferedReader(reader);
		String url = null;
		while ((url = br.readLine()) != null) {
			urls.add(url);
		}
		
		br.close();
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
	private List<Paper> getPapers(String url, String type, String venue) throws Exception {
		List<Paper> list = new ArrayList<Paper>();

		Document doc = Jsoup.connect(url).userAgent(AgentInfo.getUSER_AGENT()).timeout(AgentInfo.getTIME_OUT()).get();
		Elements entries = doc.select(".entry").select("." + type);

		for (Element ele : entries) {
			Element data = ele.getElementsByClass("data").first();
			// get title
			String title = data.getElementsByClass("title").text();
			// get year
			Elements eYear = data.getElementsByAttributeValue("itemprop", "datePublished");
			String year = eYear.first().attr("content");
			// get authors
			Elements authors = data.getElementsByAttributeValue("itemprop", "author");
			List<Author> authorlist = new ArrayList<Author>();
			for (Element tmp : authors) {
				Author author = new Author(tmp.text(), null);
				authorlist.add(author);
			}
			// get doi
			Element doiEle = ele.select(".publ").select(".head").get(0);
			String doi = null;
			if(doiEle.getElementsByTag("a").first() != null && doiEle.getElementsByTag("a").first().hasAttr("href")){
				doi = doiEle.getElementsByTag("a").first().attr("href");
			}
			
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
			 * Filtering out the unrelated papers which do not include the keywords.
			 * This filtering method won't be perfect.
			 * But this method can help reduce the workload of the 'refine' method.
			 * So, we must be very sure about the keywords.
			 * For example, during the case study, selecting papers about bug classification, 
			 * I am very sure that the title of selected papers must contain words: bug, defect, fault, flaw or error.
			 *  
			 */
			if (filterByKeyword1(title) && filterByKeyword2(title)) {
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

	private boolean filterByKeyword1(String title) {
		boolean isKeeped = false;
		if (title.indexOf("bug") >= 0 ||
			title.indexOf(Keywords.defect.toString()) >= 0 ||
			title.indexOf(Keywords.fault.toString()) >= 0 ||
			title.indexOf(Keywords.flaw.toString()) >= 0 ||
			title.indexOf(Keywords.error.toString()) >= 0) {
			isKeeped = true;
		}
		return isKeeped;
	}

	private boolean filterByKeyword2(String title) {
		boolean isKeeped = false;
		if (title.indexOf("class") >= 0 ||
			//title.indexOf("classify") >= 0 ||
			//title.indexOf("classification") >= 0 ||  //These two will be included by the first one.
			title.indexOf("type") >= 0 ||
			title.indexOf("pattern") >= 0 ||
			title.indexOf("model") >= 0 ||
			title.indexOf("sort") >= 0 ||
			title.indexOf("category") >= 0 ||
			title.indexOf("systematics") >= 0 ||
			title.indexOf("systematisation") >= 0||
			title.indexOf("species") >= 0) {
			isKeeped = true;
		}
		return isKeeped;
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
				Elements eYear = eData.getElementsByAttributeValue("itemprop", "datePublished");
				year = Integer.valueOf(eYear.first().text());
				// get detail conference paper list
				String cUrl = ele.select(".publ").select(".head").get(0).getElementsByTag("a").first().attr("href");
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
}