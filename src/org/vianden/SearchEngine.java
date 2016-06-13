package org.vianden;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.model.Author;
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
	 * @throws Exception 
	 */
	public List<Paper> search(int startingYear) throws Exception
	{
		//url list used to crawl papers, paper list used to return
		List<String> urllist = new ArrayList<String>();
		List<Paper> paperlist = new ArrayList<Paper>();
		//read paper sites from config file to paperlist
		FileReader reader = new FileReader(System.getProperty("user.dir") + "/res/dblp.config");
		BufferedReader br = new BufferedReader(reader);
		
		String str = null;
		while((str = br.readLine()) !=null){
			System.out.println(str);
			urllist.add(str);
		}
		
		//start crawl papers with basic information from dblp sites
		//from starting year to current year
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		System.out.println("cur year:"+currentYear);
		for(int index=0; index<urllist.size(); ++index){
			String dblpUrl = urllist.get(index);
			String html = this.getHtml(dblpUrl);
			
			//start analyzing corresponding to journal and conference
			if(dblpUrl.contains("db/journals")){
				Document doc = Jsoup.parse(html);
				Element main = doc.getElementById("main");
				Elements lis = main.getElementsByTag("li");
				//get venue
				String venue = doc.select(".headline").select(".noline").first().text();
				System.out.println("venue:"+venue);
				
				for(Element li : lis){
					//get papers from starting year
					for(int i=startingYear; i<=currentYear; ++i){
						String validYear = String.valueOf(i);
						if(li.text().contains(validYear)){
							Elements as = li.getElementsByTag("a");
							for(Element a :as){
								String url = a.attr("href");
								System.out.println("url:"+url);
								List<Paper> list = this.getPaper(url, "article", venue);
								paperlist.addAll(list);
							}
							continue;
						}
					}
				}
				
			}else if(dblpUrl.contains("db/conf")){
				Document doc = Jsoup.parse(html);
				Elements eles = doc.select(".entry");
				
				String venue = doc.select(".headline").select(".noline").first().text();
				System.out.println("eles size:"+eles.size()+", venue:"+venue);
				
				for(Element ele :eles){
					Element eData = ele.getElementsByClass("data").first();
					//get year
					int year = 0;
					Elements eYear = eData.getElementsByAttributeValue("itemprop", "datePublished");
					year = Integer.valueOf(eYear.first().text());
					//get detail conference paper list
					String cUrl = ele.select(".publ").select(".head").get(0).getElementsByTag("a").first().attr("href");
					//get papers from starting year
					if(year>=startingYear){
						System.out.println("year:"+year+",confUrl:"+cUrl);
						//get papers of each conference and add to papaer list
						List<Paper> list = this.getPaper(cUrl, "inproceedings", venue);
						paperlist.addAll(list);
					}
				}
				
			}
		}
		
		return paperlist;
	}
	
	/**
	 * To refill the missing attributes (e.g., abstract, author affiliation, etc.) of a given paper
	 * 
	 * @param paper
	 * @return the enriched version of the given paper
	 * @throws Exception 
	 */
	public Paper refine(Paper paper) throws Exception
	{
		String html = this.getHtml(paper.getpDoi());
		
		String abstractStr ="";
		String keywordsStr = "";
		String emailStr ="";
		String pdfUrlStr = "";
		int firstPage = -1;
		int lastPage = -1;
		int pages = 0;
		List<String> reference = null;
		
		Document doc = Jsoup.parse(html);
		
		Elements metaEles = doc.getElementsByTag("meta");
		for(Element ele :metaEles){
			String name = ele.attr("name");
			if(name.equals("citation_keywords")){	//keywords:IEEE
				keywordsStr = ele.attr("content");
				keywordsStr = keywordsStr.replaceAll("\t|\r|\n", "");
			}else if(name.equals("citation_author_email")){	//email:IEEE, Springer
				emailStr += ele.attr("content") + ";";
			}else if(name.equals("citation_pdf_url")){	//pdf:IEEE, Springer
				pdfUrlStr = ele.attr("content");
			}else if(name.equals("citation_abstract_html_url")){ //abstract_html_url:IEEE, Springer
				abstractStr = ele.attr("content");
			}else if(name.equals("citation_firstpage")){
				firstPage = Integer.valueOf(ele.attr("citation_firstpage")); //firstPage:IEEE, Springer
			}else if(name.equals("citation_lastpage")){
				lastPage = Integer.valueOf(ele.attr("citation_lastpage")); //lastPage:IEEE, Springer
			}
		}
		
		if(firstPage!=-1 && lastPage!=-1){
			pages = lastPage - firstPage + 1;
		}
		
		switch (paper.getpDatabaseType())
		{
		case DatabaseType.ACM:
			break;
		case DatabaseType.IEEE:
			//abstract
			Element ieee = doc.select("div.article").first();
			abstractStr = ieee.text();
			
			//references
			reference = new ArrayList<String>();
			Element ref = doc.getElementById("abstract-references-tab");
			String refurl = "http://ieeexplore.ieee.org"+ref.attr("href");
			String refhtml = this.getHtml(refurl);
			Elements docsClass = Jsoup.parse(refhtml).getElementsByClass("docs");
			if(docsClass != null){
				Element docs = docsClass.first();
				Elements refs = docs.getElementsByTag("li");
				for(Element li : refs){
					String refstr = li.text();
					reference.add(refstr);
				}
			}
			break;
		case DatabaseType.SPRINGER:
			Element springer = doc.getElementById("Abs1");
			if(springer!=null){
				//abstract
				Element absEle = springer.select("p.Para").first();
				abstractStr= absEle.text();
				//keywords
				Element keyGroupEle = doc.select("div.KeywordGroup").first();
				if(keyGroupEle != null){
					Elements keyEles = keyGroupEle.getElementsByClass("Keyword");
	    			
	    			for(Element keyword : keyEles){
	    				String kw = keyword.text()+";";
	    				keywordsStr += kw;
	    			}
				}
				//references
				reference = new ArrayList<String>();
				Element refs = doc.getElementById("abstract-references");
				if(refs != null){
					Elements lis = refs.getElementsByClass("Citation");
					for(Element li:lis){
						String refstr = li.text();
						reference.add(refstr);
					}
				}
			}
			break;
		case DatabaseType.ELSEVIER:
			break;
		case DatabaseType.WILEY:
			break;
		case DatabaseType.USENIX:
			break;
		case DatabaseType.IET:
			break;
		default:
			break;
		}
		
		
		//set paper's informations
		paper.setpAbstract(abstractStr);
		paper.setpEmail(emailStr);
		paper.setpKeywords(keywordsStr);
		paper.setpPdfUrl(pdfUrlStr);
		paper.setpPages(String.valueOf(pages));
		paper.setpReferences(reference);
		
		return paper;
	}
	
	/**
	 * get papers from each single journal or conference page 
	 * 
	 * @param url(journal or conference page url), type(article or inproceedings), venue
	 * @return the papers list of the single journal or conference
	 * @throws Exception
	 */
	private List<Paper> getPaper(String url, String type, String venue) throws Exception{
		List<Paper> list = new ArrayList<Paper>();
	
		String html = this.getHtml(url);
		Elements doc = Jsoup.parse(html).select(".entry").select("."+type);
		
		System.out.println("detail:"+doc.size());
		
		for(Element ele : doc){
			Element data = ele.getElementsByClass("data").first();
			//get title
			String title = data.getElementsByClass("title").text();
			//get year
			Elements eYear = data.getElementsByAttributeValue("itemprop", "datePublished");
			String year = eYear.first().attr("content");
			//get authors
			Elements authors = data.getElementsByAttributeValue("itemprop", "author");
			List<Author> authorlist = new ArrayList<Author>();
			for(Element tmp : authors){
				Author author = new Author(tmp.text(), null);
				authorlist.add(author);
			}
			//get doi
			String doi = ele.select(".publ").select(".head").get(0).getElementsByTag("a").first().attr("href");
			//get database type, but it seems that database type can be determined by doi except acm
			int dbtype = -1;
			if(doi.contains("10.1145")){
				dbtype = DatabaseType.ACM;
			}else if (doi.contains("10.1109")){
				dbtype = DatabaseType.IEEE;
			}else if(doi.contains("10.1007")){
				dbtype = DatabaseType.SPRINGER;
			}else if(doi.contains("10.1016")){
				dbtype = DatabaseType.ELSEVIER;
			}else if(doi.contains("10.1002")){
				dbtype = DatabaseType.WILEY;
			}else if(doi.contains("usenix")){
				dbtype = DatabaseType.USENIX;
			}else if(doi.contains("10.1049")){
				dbtype = DatabaseType.IET;
			}
			
			System.out.println("name:"+title+", year:"+year+",doi:"+doi);
			
			//construct paper with obtained information
			Paper paper = new Paper();
			paper.setpYear(year);
			paper.setpAuthors(authorlist);
			paper.setpTitle(title);
			paper.setpDoi(doi);
			paper.setpVenue(venue);
			paper.setpDatabaseType(dbtype);
		}
		
		return list;
	}
	
	/**
	 * Web request, to get the web page details of sites that prepared to be crawled
	 * 
	 * @param url
	 * @return the html of the url
	 */
	private String getHtml(String url) throws Exception {
        URL localURL = new URL(url);
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
        
        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        
        if (httpURLConnection.getResponseCode() >= 300) {
            return null;
        	//throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }
        
        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            
        } finally {
            
            if (reader != null) {
                reader.close();
            }
            
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            
            if (inputStream != null) {
                inputStream.close();
            }
            
        }
        
        return resultBuffer.toString();
    }
}
