package org.vianden.model;

import java.util.Iterator;
import java.util.List;

public class Paper 
{
	//DBLP Search
	protected String title;
	protected List<Author> authors;
	protected String year;
	protected String doi;
	protected String venue;
	protected int publisher;
	
	//Database Refine
	protected String _abstract;
	protected String pages;
	protected String email;
	protected String keywords;
	protected String pdfUrl;
	/*
	 * In my opinion, the references are not necessary to the 'refine' method.
	 * References won't influence the result of filtering.
	 * However, the obtain of references will influence of the effectiveness of 'refine' method.
	 * 
	 * Only after we finally confirm the selected papers, we may need the references of papers for further works.
	 * So, all methods with respect to references should be put on the future research work.
	 * For example, we want to do extension research works about the selected papers.
	 * 
	 */
	protected List<String> references;  //references should be papers.
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	public int getPublisher() {
		return publisher;
	}
	public void setPublisher(int publisher) {
		this.publisher = publisher;
	}
	public String getAbstract() {
		return _abstract;
	}
	public void setAbstract(String _abstract) {
		this._abstract = _abstract;
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getPdfUrl() {
		return pdfUrl;
	}
	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}
	public List<String> getReferences() {
		return references;
	}
	public void setReferences(List<String> references) {
		this.references = references;
	}
	
	@Override
	public int hashCode() 
	{
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Paper other = (Paper) obj;
		
		if (doi == null) {
			if (other.doi != null)
				return false;
		} else if (!doi.equals(other.doi))
			return false;
		
		return true;
	}
	
	@Override
	public String toString() 
	{
		/*
		 * I didn't test this method, but I think there is a bug in this method.
		 * Member variables 'authors' and 'references' are belong the type of List,
		 * so, authors.toString() and referenes.toString() won't return what we really want.
		 */
		return "Paper [pTitle=" + title + ", pAuthors=" + authors + ", pYear=" + year + ", pDoi=" + doi
				+ ", pVenue=" + venue + ", pDatabaseType=" + publisher + ", pAbstract=" + _abstract + ", pPages="
				+ pages + ", pEmail=" + email + ", pKeywords=" + keywords + ", pPdfUrl=" + pdfUrl + ", pReferences="
				+ references + "]";
	}
	
	public String getAllAuthorsName() {
		String authorsName = "";
		Iterator<Author> it = authors.iterator();
		while (it.hasNext()) {
			if (!"".equals(authorsName) ) {
				authorsName += ";";
			}
			authorsName += it.next().getName();
		}
		return authorsName;
	}
}
