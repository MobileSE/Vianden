package org.vianden.model;

import java.util.List;

public class Paper 
{
	//DBLP Search
	protected String pTitle;
	protected List<Author> pAuthors;
	protected String pYear;
	protected String pDoi;
	protected String pVenue;
	protected int pDatabaseType;
	
	//Database Refine
	protected String pAbstract;
	protected String pPages;
	protected String pEmail;
	protected String pKeywords;
	protected String pPdfUrl;
	protected List<String> pReferences;
	
	
	public String getpTitle() {
		return pTitle;
	}
	public void setpTitle(String pTitle) {
		this.pTitle = pTitle;
	}
	public List<Author> getpAuthors() {
		return pAuthors;
	}
	public void setpAuthors(List<Author> pAuthors) {
		this.pAuthors = pAuthors;
	}
	public String getpYear() {
		return pYear;
	}
	public void setpYear(String pYear) {
		this.pYear = pYear;
	}
	public String getpDoi() {
		return pDoi;
	}
	public void setpDoi(String pDoi) {
		this.pDoi = pDoi;
	}
	public String getpVenue() {
		return pVenue;
	}
	public void setpVenue(String pVenue) {
		this.pVenue = pVenue;
	}
	public int getpDatabaseType() {
		return pDatabaseType;
	}
	public void setpDatabaseType(int pDatabaseType) {
		this.pDatabaseType = pDatabaseType;
	}
	public String getpAbstract() {
		return pAbstract;
	}
	public void setpAbstract(String pAbstract) {
		this.pAbstract = pAbstract;
	}
	public String getpPages() {
		return pPages;
	}
	public void setpPages(String pPages) {
		this.pPages = pPages;
	}
	public String getpEmail() {
		return pEmail;
	}
	public void setpEmail(String pEmail) {
		this.pEmail = pEmail;
	}
	public String getpKeywords() {
		return pKeywords;
	}
	public void setpKeywords(String pKeywords) {
		this.pKeywords = pKeywords;
	}
	public String getpPdfUrl() {
		return pPdfUrl;
	}
	public void setpPdfUrl(String pPdfUrl) {
		this.pPdfUrl = pPdfUrl;
	}
	public List<String> getpReferences() {
		return pReferences;
	}
	public void setpReferences(List<String> pReferences) {
		this.pReferences = pReferences;
	}
}
