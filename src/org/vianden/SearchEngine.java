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
	private List<String> errorList = null;
	

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
		errorList = new ArrayList<String>();
		
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
				String errorMsg = "Failed to access the website of " + dblpUrl;
				this.errorList.add(errorMsg);
				System.out.println(errorMsg);
				continue;
			}
			this.analysisDocument(paperForCrawlList, dblpUrl, doc, startingYear, currentYear);
		}		
		
		//get detail information of each paper with network
		for(int i = 0; i< paperForCrawlList.size(); ++i){
			Map<String, String> map = paperForCrawlList.get(i);
			String url = map.get("url");
			String venue = map.get("venue");
			
			// get papers of each conference and journals
			List<Paper> list = this.getPapers(url, venue);
			if (list != null) {
				paperlist.addAll(list);
			}
			
			System.out.println("The "+(i+1)+"th paper of The Total " + paperForCrawlList.size() +" papers");
		}

		return paperlist;
	}

	private Document accesssUrlContent(String url) {
		
		if(url == null){
			return null;
		}
		
		Document doc = null;
		try {
			doc = Jsoup.connect(url).userAgent(AgentInfo.getUSER_AGENT()).timeout(AgentInfo.getTIME_OUT()).get();
//			Thread.sleep(AgentInfo.getSLEEP_TIME());
		} catch (IOException e) {
			doc = null;
			String errorMsg = "Invalid url:" + url;
			this.errorList.add(errorMsg);
			System.out.println(errorMsg);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
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
	private List<Paper> getPapers(String url, String venue) {
		//if url=null, return
		if(url == null){
			String errorMsg = "url:" + url + " = null, " + " venue:" + venue;
			this.errorList.add(errorMsg);
			return null;
		}
		
		List<Paper> list = new ArrayList<Paper>();

		Document doc = null;
		
		doc = this.accesssUrlContent(url);
		
		if (doc == null) {
			String errorMsg = "--Failed to access the website of " + url;
			this.errorList.add(errorMsg);
			System.out.println(errorMsg);
			return null;
		}
		Elements entries = doc.select(".entry");
		System.out.println("get Paper:"+url+" num:"+entries.size());
		for (Element ele : entries) {
			Element data = ele.getElementsByClass("data").first();
			// get title
			String title = null;
			try {
				if(data.hasClass("title")){
					title = data.getElementsByClass("title").text();
				}
			} catch (NullPointerException e1) {
				String errorMsg = "data"+data +" url:" + url;
				this.errorList.add(errorMsg);
				System.out.println(errorMsg);
				continue;
			}
			// get year
			String year = null;
			Element eYear = null;
			try {
				Elements props = ele.getElementsByAttribute("itemprop");
				
				for(Element prop : props){
					String value = prop.attr("itemprop");
					if(value.equals("datePublished")){
						eYear = prop;
						if(prop.hasText()){
							year = eYear.text();
						}else{
							year = eYear.attr("content");
						}
						break;
					}
				}
				
			} catch (NullPointerException e) {
				e.printStackTrace();
				String errorMsg = "Failed to find the element (itemprop=datePublished) at "+url;
				this.errorList.add(errorMsg);
				System.out.println(errorMsg);
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
			if (title != null && filterByKeyword(title)) {
				// construct paper with obtained information
				Paper paper = new Paper();
				paper.setYear(year);
				paper.setAuthors(authorlist);
				paper.setTitle(title);
				paper.setDoi(doi);
				paper.setVenue(venue);
				paper.setPublisher(dbtype);

				System.out.println("Paper:"+paper.getTitle());
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
			regaxList.add("(" + keywords.replaceAll(";", "|") + ")");
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
							map.put("venue", venue);
							paperForCrawlList.add(map);
						}
						continue;
					}
				}
			}

		} else if (dblpUrl.contains("db/conf")) {
			try{
				Elements eles = doc.select(".entry");
				
				String venue = doc.select(".headline").select(".noline").first().text();

				for (Element ele : eles) {
					Element eData = ele.getElementsByClass("data").first();
					// get year
					int year = 0;
					Element eYear = null;
					try {
						Elements props = eData.getElementsByAttribute("itemprop");
						
						for(Element prop : props){
							String value = prop.attr("itemprop");
							if(value.equals("datePublished")){
								eYear = prop;
								if(prop.hasText()){
									year = Integer.valueOf(eYear.text());
								}else{
									year = Integer.valueOf(eYear.attr("content"));
								}
								break;
							}
						}
						
					} catch (NullPointerException e) {
						String errorMsg = "Failed to find the element (itemprop=datePublished) at " + dblpUrl;
						this.errorList.add(errorMsg);
						System.out.println(errorMsg);
						continue;
					}
					// get detail conference paper list
					String cUrl = null;
					try {
						Element cUrlEle = ele.select(".publ").select(".head").get(0);
						if(cUrlEle.getElementsByTag("a").first() != null && cUrlEle.getElementsByTag("a").first().hasAttr("href")){
							cUrl = cUrlEle.getElementsByTag("a").first().attr("href");
						}
					} catch (NullPointerException e) {
						System.out.println(e.getMessage());
						continue;
					}
					// get papers from starting year
					if (year >= startYear || year == 0) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("url", cUrl);
						map.put("venue", venue);
						paperForCrawlList.add(map);
					}
				}
			}catch(NullPointerException e){
				String errorMsg = "NullPointerException for entry dblpUrl:"+dblpUrl;
				this.errorList.add(errorMsg);
				System.out.println(errorMsg);
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