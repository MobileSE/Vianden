package org.vianden.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.model.Author;
import org.vianden.model.Paper;

public class ACMCrawler extends AbstractCrawler {

	public ACMCrawler(Paper paper) throws IOException {
		super(paper);
	}

	@Override
	public void crawl() throws IOException {
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
			Elements ps = Jsoup.connect(prefix + abssuffix).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17")
					.timeout(10000).get().getElementsByTag("p");
			if(ps.size()>0){
				abstractStr = ps.first().text();
			}
			System.out.println(abssuffix);
			System.out.println(refsuffix);
			
			//get references
			reference = new ArrayList<String>();
			Elements acmrefs = Jsoup.connect(prefix + refsuffix).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17")
					.timeout(10000).get().getElementsByTag("tr");
			if(acmrefs != null){
				for(Element tr : acmrefs){
					Element td = tr.getElementsByTag("td").get(2);
					reference.add(td.text());
				}
			}else{
				System.out.println("ref null");
			}	
		}
	}

}
