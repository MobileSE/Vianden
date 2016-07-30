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
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17";
	private static final int TIME_OUT = 100000;

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
		List<Document> docList = new ArrayList<Document>();
		for (int index = 0; index < urls.size(); ++index) {
			String dblpUrl = urls.get(index);
			Document doc = Jsoup.connect(dblpUrl).userAgent(USER_AGENT).timeout(TIME_OUT).get();
			docList.add(doc);
		}
		
		// analysis of document with Jsoup
		// get url, type, venue information of each paper
		List<Map<String, String>> paperForCrawlList = new ArrayList<Map<String, String>>();
		for(int index = 0; index < docList.size(); ++index){
			String dblpUrl = urls.get(index);
			Document doc = docList.get(index);
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
		AbstractCrawler absCrawler = null;

		switch (paper.getpPublisher()) {
		case Publisher.ACM:
			absCrawler = new ACMCrawler(paper);
			break;
		case Publisher.IEEE:
			absCrawler = new IEEECrawler(paper);
			break;
		case Publisher.SPRINGER:
			absCrawler = new SpringerCrawler(paper);
			break;
		case Publisher.ELSEVIER:
			absCrawler = new ElsevierCrawler(paper);
			break;
		case Publisher.WILEY:
			absCrawler = new WileyCrawler(paper);
			break;
		case Publisher.USENIX:
			absCrawler = new USENIXCrawler(paper);
			break;
		case Publisher.IET:
			absCrawler = new IETCrawler(paper);
			break;
		default:
			break;
		}

		if (absCrawler != null) {
			// crawling
			absCrawler.crawl();
			// set data to paper
			absCrawler.finishCrawl();
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

		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_OUT).get();
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
			String doi = ele.select(".publ").select(".head").get(0).getElementsByTag("a").first().attr("href");
			// get database type by doi number
			int dbtype = -1;
			if (doi.contains("10.1145")) {
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

			// construct paper with obtained information
			Paper paper = new Paper();
			paper.setpYear(year);
			paper.setpAuthors(authorlist);
			paper.setpTitle(title);
			paper.setpDoi(doi);
			paper.setpVenue(venue);
			paper.setpPublisher(dbtype);

			// add paper to list
			list.add(paper);
		}

		return list;
	}

	/**
	 *  this function handle the analysis work of each venue's html document to get wannted paper urls
	 *  then, add these urls to prepared list
	 *  
	 *  @param paperForCrawlList
	 *  @param dplpUrl, used to tell journal or conference
	 *  @param doc, document waited to be analyse
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