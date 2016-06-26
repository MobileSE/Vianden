package org.vianden.crawler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.model.Author;
import org.vianden.model.Paper;

public class IETCrawler extends AbstractCrawler {

	public IETCrawler(Paper paper) throws IOException {
		super(paper);
	}

	@Override
	public void crawl() throws IOException {
		Elements as = doc.getElementsByTag("a");
		//iet database url
		String ieturl = as.get(2).attr("href");
		//refine by iet database
		Document ietdoc = Jsoup.connect(ieturl).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17")
				.timeout(20000).get();
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
				Elements metaEles = doc.getElementsByTag("meta");
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
		for(Element refli : reflis){
			reference.add(refli.text());
		}
		
		//ieee database url to get pdf
		//refine by ieee database
		String ieeeurl = as.get(3).attr("href");
		Document ieeedoc = Jsoup.connect(ieeeurl).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17")
				.timeout(10000).get();
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
	}

}
