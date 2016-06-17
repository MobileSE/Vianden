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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
								//get papers of each journals and add to papaer list
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
//		String html = paper.getpDoi();
		
		String abstractStr ="";
		String keywordsStr = "";
		String emailStr ="";
		String pdfUrlStr = "";
		int firstPage = 0;
		int lastPage = 0;
		int pages = 0;
		List<String> reference = null;
		List<Author> authors = new ArrayList<Author>();
		
		Document doc = Jsoup.parse(html);
		
		Elements metaEles = doc.getElementsByTag("meta");
		for(int i=0; i< metaEles.size(); ++i){
			Element ele = metaEles.get(i);
			String name = ele.attr("name");
			if(name.equals("citation_keywords")){	//keywords:IEEE,IET,ACM
				keywordsStr = ele.attr("content");
				keywordsStr = keywordsStr.replaceAll("\t|\r|\n", "");
			}else if(name.equals("citation_author_email")){	//email:IEEE, Springer
				emailStr += ele.attr("content") + ";";
			}else if(name.equals("citation_pdf_url")){	//pdf:IEEE, Springer, USENIX, ACM
				pdfUrlStr = ele.attr("content");
			}else if(name.equals("citation_abstract")){ //citation_abstract:IET
				abstractStr = ele.attr("content");
			}else if(name.equals("citation_firstpage")){
				firstPage = Integer.valueOf(ele.attr("content")); //firstPage:IEEE, Springer, USENIX, Willy, IET, ACM
			}else if(name.equals("citation_lastpage")){
				lastPage = Integer.valueOf(ele.attr("content")); //lastPage:IEEE, Springer, USENIX, Willy, IET
			}else if(name.equals("citation_author")){	//authors:IEEE, Springer, Willy, IET
				//define informations of author
				String authorName = ele.attr("content");
				Set<String> authorInstitutionSet  = new HashSet<String>();
				//get affiliations
				for(int j=i+1; j<metaEles.size(); ++j){
					Element eleInstitution = metaEles.get(j);
					if(eleInstitution!=null && eleInstitution.attr("name").equals("citation_author_institution")){
						authorInstitutionSet.add(eleInstitution.attr("content"));
					}else{
						break;
					}
				}
				//construct author
				Author author = new Author(authorName, authorInstitutionSet);
				authors.add(author);
			}
		}
		
		if(firstPage!=0 && lastPage!=0){
			pages = lastPage - firstPage + 1;
		}else if(firstPage!=0 || lastPage!=0){
			pages = 1;
		}
		
		switch (paper.getpDatabaseType())
		{
		case DatabaseType.ACM:
			//authors
			Element table = doc.getElementById("divmain").getElementsByTag("table").get(2);
			Elements trs = table.getElementsByTag("tr");
			for(Element tr : trs){
				Elements tds = tr.getElementsByTag("td");
				String name = tds.get(1).text();
				Set<String> authorInstitutionSet  = new HashSet<String>();
				if(tds.size() > 2){
					for(int i=2; i<tds.size(); ++i){
						authorInstitutionSet.add(tds.get(i).text());
					}
				}
				//construct author add to authors list
				Author author = new Author(name, authorInstitutionSet);
				authors.add(author);
			}
			//get the destination of script
			Elements scripts = doc.getElementsByTag("script");
			String desStr = null;
			for(Element script:scripts){
				if(script.toString().contains("initializeTabLayout")){
					desStr = script.toString();
					break;
				}
			}
			//abstract, references
			if(desStr != null){
				String prefix = "http://dl.acm.org/";
				String absStartStr = "'bindTo':'abstract','bindExpr':['";
				String refStartStr = "'bindTo':'references','bindExpr':['";	
				String endStr = "']},ColdFusion.Bind.urlBindHandler,true);";
				//abstract suffix
				int absstart = desStr.indexOf(absStartStr) + absStartStr.length();
				int absend = desStr.indexOf(endStr);
				String abssuffix = desStr.substring(absstart, absend);
				
				//direct to referenes
				desStr = desStr.substring(absend + endStr.length());
				desStr = desStr.substring(desStr.indexOf(endStr) + endStr.length());
				
				//references suffix 
				int refstart = desStr.indexOf(refStartStr) + refStartStr.length();
				int refend = desStr.indexOf(endStr);
				String refsuffix = desStr.substring(refstart, refend);
				
				//get abstract
				abstractStr = this.getHtml(prefix + abssuffix);
				System.out.println(abssuffix);
				System.out.println(refsuffix);
				
				//get references
				reference = new ArrayList<String>();
				String refhtml = this.getHtml(prefix + refsuffix);
				if(refhtml != null){
					Elements acmrefs = Jsoup.parse(refhtml).getElementsByTag("tr");
					for(Element tr : acmrefs){
						Element td = tr.getElementsByTag("td").get(1);
						reference.add(td.text());
					}
				}else{
					System.out.println("ref null");
				}
				
			}
//			System.out.println(table.toString());
			break;
		case DatabaseType.IEEE:
			//abstract
			Element ieee = doc.select("div.article").first();
			abstractStr = ieee.text();
			//references
			reference = new ArrayList<String>();
			Element ref = doc.getElementById("abstract-references-tab");
			//can not get absolute address via .attr("abs:href")
			String refurl = "http://ieeexplore.ieee.org" + ref.attr("href"); 
			
			String refhtml = this.getHtml(refurl);
			Elements docsClass = Jsoup.parse(refhtml).getElementsByClass("docs");
			if(docsClass.size()>0){
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
			//keywords
			Element keywordsElsvier = doc.getElementsByClass("keyword").first();
			keywordsStr = keywordsElsvier.text();
			//abstract
			Element absElsvier = doc.select(".abstract").select(".svAbstract").get(1).getElementsByTag("p").first();
			abstractStr = absElsvier.text();
			//affiliations
			HashMap<String, String> affMap = new HashMap<String, String>();
			Element affiliations = doc.select(".affiliation").select(".authAffil").first();
			System.out.println(affiliations.text());
			Elements afflis = affiliations.getElementsByTag("li");
			for(Element affli:afflis){
				String tag = affli.getElementsByTag("sup").first().text();
				String affName = affli.getElementsByTag("span").first().text();
				affMap.put(tag, affName);
			}
			//authors
			Element authorElsvier = doc.select(".authorGroup.noCollab.svAuthor").first();
			Elements authorElsvierLis = authorElsvier.getElementsByTag("li");
			for(Element li : authorElsvierLis){
				String authorName = li.select(".authorName.svAuthor").first().text();
				//get author affiliations
				Set<String> authorInstitutionSet  = new HashSet<String>();
				Elements affs = li.select(".intra_ref.auth_aff");
				for(Element aff : affs){
					String tag = aff.text();
					String institution = affMap.get(tag);
					authorInstitutionSet.add(institution);
				}
				//add to authors
				Author author = new Author(authorName, authorInstitutionSet);
				authors.add(author);
				
				//Email
				Elements emailAs = li.getElementsByClass("auth_mail");
				for(int i=0; i<emailAs.size(); ++i){
					Element email = emailAs.get(i);
					emailStr+=email.attr("href").replace("mailto:", "") + ";";
				}
			}
			//pdfStr
			pdfUrlStr = doc.getElementById("pdfLink").attr("pdfurl");
			//pages
			Element volIssue = doc.getElementsByClass("volIssue").first();
			String volStr[] = volIssue.text().split(",");
			String pageStr[] = null;
			for(String str : volStr){
				if(str.contains("Pages")){
					str = str.trim();
					pageStr = str.substring(5).split("–");
				}
			}
			
			if(pageStr != null){
				if(pageStr.length == 2){
					pages = Integer.valueOf(pageStr[1].trim()) - Integer.valueOf(pageStr[0].trim()) + 1;
				}else if(pageStr.length == 1){
					pages = 1;
				}
			}
			//no references
			break;
		case DatabaseType.WILEY:
			//keywords
			Element keylist = doc.getElementById("abstractKeywords1");
			Elements lis = keylist.getElementsByTag("li");
			for(Element li : lis){
				keywordsStr += li.text();
			}
			//abstract
			Element absWilly = doc.getElementById("abstract");
			Elements ps = absWilly.getElementsByTag("p");
			abstractStr = "";
			for(Element p : ps){
				abstractStr += p.text();
			}
			//references
			Element refWiley = doc.select(".tabbedContent").first().getElementsByTag("a").get(1);
			//can not get absolute address via .attr("abs:href")
			String refWileyUrl = "http://onlinelibrary.wiley.com" + refWiley.attr("href");
			String refWileyHtml = this.getHtml(refWileyUrl);
			Elements refsWiley = Jsoup.parse(refWileyHtml).getElementById("fulltext").getElementsByTag("cite");
			reference = new ArrayList<String>();
			for(Element refinfo : refsWiley){
				reference.add(refinfo.text());
			}
			
			break;
		case DatabaseType.USENIX:
			Elements usenix = doc.select(".field.field-name-field-paper-description.field-type-text-long.field-label-above")
			.select(".field-items");
			if(usenix != null){
				//abstract
				Element absUsenix = usenix.first();
				abstractStr = absUsenix.text();
				//no reference and keywords
			}
			break;
		case DatabaseType.IET:
			Elements as = doc.getElementsByTag("a");
			//iet database url
			String ieturl = as.get(2).attr("href");
			String ietHtml = this.getHtml(ieturl);
			//refine by iet database
			Document ietdoc = Jsoup.parse(ietHtml);
			Elements ietmetaEles = ietdoc.getElementsByTag("meta");
			for(int i=0; i< ietmetaEles.size(); ++i){
				Element ele = ietmetaEles.get(i);
				String name = ele.attr("name");
				if(name.equals("citation_keywords")){	//keywords
					keywordsStr = ele.attr("content");
					keywordsStr = keywordsStr.replaceAll("\t|\r|\n", "");
				}else if(name.equals("citation_author_email")){	//email
					emailStr += ele.attr("content") + ";";
				}else if(name.equals("citation_abstract")){ //abstract
					abstractStr = ele.attr("content");
				}else if(name.equals("citation_firstpage")){
					firstPage = Integer.valueOf(ele.attr("content")); //firstPage
				}else if(name.equals("citation_lastpage")){
					lastPage = Integer.valueOf(ele.attr("content")); //lastPage:
				}else if(name.equals("citation_author")){	//authors
					//define informations of author
					String authorName = ele.attr("content");
					Set<String> authorInstitutionSet  = new HashSet<String>();
					//get affiliations
					for(int j=i+1; j<metaEles.size(); ++j){
						Element eleInstitution = metaEles.get(j);
						if(eleInstitution!=null && eleInstitution.attr("name").equals("citation_author_institution")){
							authorInstitutionSet.add(eleInstitution.attr("content"));
						}else{
							break;
						}
					}
					//construct author
					Author author = new Author(authorName, authorInstitutionSet);
					authors.add(author);
				}
			}
			
			//set pages
			if(firstPage!=0 && lastPage!=0){
				pages = lastPage - firstPage + 1;
			}
			
			//get references
			Elements reflis = ietdoc.getElementsByClass("refdetail");
			reference = new ArrayList<String>();
			for(Element refli : reflis){
				reference.add(refli.text());
			}
			
			//ieee database url to get pdf
			//refine by ieee database
			String ieeeurl = as.get(3).attr("href");
			String ieeeHtml = this.getHtml(ieeeurl);
			Document ieeedoc = Jsoup.parse(ieeeHtml);
			Elements ieeemetaEles = ieeedoc.getElementsByTag("meta");
			for(int i=0; i< ieeemetaEles.size(); ++i){
				Element ele = ieeemetaEles.get(i);
				String name = ele.attr("name");
				if(name.equals("citation_author_email")){	//email
					emailStr += ele.attr("content") + ";";
				}else if(name.equals("citation_pdf_url")){	//pdf
					pdfUrlStr = ele.attr("content");
				}
			}
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
		if(authors.size()>0){
			paper.setpAuthors(authors);
		}
		
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
			//get database type by doi number
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
            
        	throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
//        	return null;
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
