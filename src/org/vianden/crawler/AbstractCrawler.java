package org.vianden.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vianden.model.Author;
import org.vianden.model.Paper;

/**
 * Put all the common part such as init, destory, etc in this class
 * 
 * @author li.li
 *
 */
public abstract class AbstractCrawler implements ICrawler
{
	protected Paper paper = null;
	protected String abstractStr ="";
	protected String keywordsStr = "";
	protected String emailStr ="";
	protected String pdfUrlStr = "";
	protected int firstPage = 0;
	protected int lastPage = 0;
	protected int pages = 0;
	protected List<String> reference = null;
	protected List<Author> authors = null;
	protected Document doc = null;
	
	public AbstractCrawler(Paper paper) throws IOException
	{
		//initialize
		this.paper = paper;
		authors = new ArrayList<Author>();
		reference = new ArrayList<String>();
		
		//common part crawl
		this.commonCrawl();
	}
	
	private void commonCrawl() throws IOException{
		doc = Jsoup.connect(paper.getpDoi())
				.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/601.6.17 (KHTML, like Gecko) Version/9.1.1 Safari/601.6.17")
				.timeout(20000).get();
		
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
	}
	
	public void FinishCrawl(){
		//set paper's informations
		paper.setpAbstract(abstractStr.trim());
		paper.setpEmail(emailStr.trim());
		paper.setpKeywords(keywordsStr.trim());
		paper.setpPdfUrl(pdfUrlStr.trim());
		paper.setpPages(String.valueOf(pages).trim());
		paper.setpReferences(reference);
		if(authors.size()>0){
			paper.setpAuthors(authors);
		}
		
	}

	@Override
	public Paper getPaper() 
	{
		return paper;
	}
}
