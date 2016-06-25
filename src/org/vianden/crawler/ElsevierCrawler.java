package org.vianden.crawler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.model.Author;
import org.vianden.model.Paper;

public class ElsevierCrawler extends AbstractCrawler {

	public ElsevierCrawler(Paper paper) throws IOException {
		super(paper);
	}

	@Override
	public void crawl() throws IOException {
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
	}

}
