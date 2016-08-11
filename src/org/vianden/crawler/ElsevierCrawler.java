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
		Elements absEles1 = doc.select(".abstract.svAbstract");
		Elements absEles2 = doc.select(".abstract.abstract-type-author");
		Element absElsvier = null;
		if(absEles1!=null && absEles1.size()>0){
			absElsvier = absEles1.last().getElementsByTag("p").first();
		}else if(absEles2!=null && absEles2.size()>0){
			absElsvier = absEles2.first().getElementsByTag("div").first();
		}
		if(absElsvier != null){
			abstractStr = absElsvier.text();
		}
		//affiliations
		HashMap<String, String> affMap = new HashMap<String, String>();
		Element affiliations = doc.select(".affiliation.authAffil").first();

		if(affiliations != null){
			Elements afflis = affiliations.getElementsByTag("li");
			
			if(afflis != null){
				for(Element affli:afflis){
					Element tagEle = affli.getElementsByTag("sup").first();
					Element nameEle = affli.getElementsByTag("span").first();
					
					if(tagEle != null && nameEle != null){
						String tag = tagEle.text();
						String affName = nameEle.text();
						affMap.put(tag, affName);

					}
				}
			}
		}else{
			affiliations = doc.getElementsByClass("affiliation").first();
			String affiStr = affiliations.text();
			
			int authorsNum = authors.size();
			String[] affis = affiStr.split(",", authorsNum);
			
			for(int i=0; i<authorsNum; ++i){
				Author author = authors.get(i);
				Set<String> set = new HashSet<String>();
				set.add(affis[i]);
				author.setAffiliation(set);
			}
			
		}
		
		//authors
		Element authorElsvier = doc.select(".authorGroup.noCollab.svAuthor").first();
		if(authorElsvier != null){
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
		}else{
			//Email
			Elements emailAs = doc.getElementsByClass("author-email");
			for(int i=0; i<emailAs.size(); ++i){
				Element email = emailAs.get(i);
				emailStr+=email.attr("href").replace("mailto:", "") + ";";
			}
		}
		
		//pdfStr
		Element pdfEle1 = doc.getElementById("pdfLink");
		Element pdfEle2 = doc.getElementById("article-download");
		
		if(pdfEle1 != null){
			pdfUrlStr = pdfEle1.attr("pdfurl");
		}else if(pdfEle2 != null){
			pdfUrlStr = pdfEle2.getElementsByTag("a").first().attr("href");
		}
		
		//pages
		Element volIssue = doc.getElementsByClass("volIssue").first();
		if(volIssue != null){
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
		}
		
		//references
		Element refslist = doc.select(".references.reference-list").first();
		
		if(refslist != null){
			Elements refs = refslist.getElementsByClass("reference-item-container");
			
			if(refs != null && refs.size() > 0){
				for(Element ref : refs){
					String refStr = ref.text();
					reference.add(refStr);
				}
			}
		}
	}

}
